package capi.service.masterMaintenance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import capi.dal.BatchDao;
import capi.dal.DistrictDao;
import capi.dal.OutletAttachmentDao;
import capi.dal.OutletDao;
import capi.dal.QuotationDao;
import capi.dal.TpuDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.Outlet;
import capi.entity.OutletAttachment;
import capi.entity.Quotation;
import capi.entity.Tpu;
import capi.entity.VwOutletTypeShortForm;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.GeoLocation;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.api.dataSync.OutletAttachmentSyncData;
import capi.model.api.dataSync.OutletAttachmentZipImageSyncData;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.OutletTypeOutletSyncData;
import capi.model.api.dataSync.UpdateOutletImageModel;
import capi.model.masterMaintenance.OutletBatchCodeModel;
import capi.model.masterMaintenance.OutletEditModel;
import capi.model.masterMaintenance.OutletTableList;
import capi.model.productMaintenance.ProductTableList;
import capi.model.shared.quotationRecord.MapOutletModel;
import capi.model.shared.quotationRecord.OutletViewModel;
import capi.service.AppConfigService;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.assignmentManagement.QuotationService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("OutletService")
public class OutletService extends BaseService {
	
	@Autowired
	private OutletDao outletDao;

	@Autowired
	private TpuDao tpuDao;

	@Autowired
	private VwOutletTypeShortFormDao outletTypeDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private BatchDao batchDao;

	@Autowired
	private OutletAttachmentDao outletAttachmentDao;

	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private QuotationService quotationService;
	
	/**
	 * Get by ID
	 */
	public Outlet getOutletById(int id) {
		return outletDao.findById(id);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<OutletTableList> getOutletList(DatatableRequestModel model,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, String activeOutlet){

		Order order = this.getOrder(model, "firmCode", "name", "brCode", "district", "tpu", "activeOutlet", "quotationCount", "streetAddress", "detailAddress");
		
		String search = model.getSearch().get("value");
		
		List<OutletTableList> result = outletDao.getTableList(search, model.getStart(), model.getLength(), order,
				outletTypeId, districtId, tpuId, activeOutlet, null);
		
		DatatableResponseModel<OutletTableList> response = new DatatableResponseModel<OutletTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = outletDao.countTableList("", null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = outletDao.countTableList(search, outletTypeId, districtId, tpuId, activeOutlet, null);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Save
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void saveOutlet(OutletEditModel model, InputStream outletImageStream, String fileBaseLoc) throws Exception {
		Outlet oldEntity = null;
		if (model.getOutletId() != null && model.getOutletId() > 0) {
			oldEntity = getOutletById(model.getOutletId());
		} else {
			oldEntity = new Outlet();
			Integer maxFirmCode = outletDao.getMaxFirmCode();
			if (maxFirmCode == null)
				oldEntity.setFirmCode(1);
			else
				oldEntity.setFirmCode(maxFirmCode + 1);
		}
		

		if (StringUtils.isEmpty(model.getLatitude()) || StringUtils.isEmpty(model.getLongitude()))
			this.fillInGps(model);
		
		
		if (model.getOutletTypeIds() != null) {
			ArrayList<String> oldOutletTypeIds = new ArrayList<String>();
			for (VwOutletTypeShortForm outletType : oldEntity.getOutletTypes()) {
				oldOutletTypeIds.add(outletType.getId());
			}
			
			Collection<String> deletedIds = (Collection<String>)CollectionUtils.subtract(oldOutletTypeIds, model.getOutletTypeIds());
//			Collection<String> updatedIds = (Collection<String>)CollectionUtils.intersection(oldOutletTypeIds, model.getOutletTypeIds());
			Collection<String> newIds = (Collection<String>)CollectionUtils.subtract(model.getOutletTypeIds(), oldOutletTypeIds);
			
			if (deletedIds.size() > 0){
				List<VwOutletTypeShortForm> deletedOutletTypes = outletTypeDao.getByIds(deletedIds.toArray(new String[0]));
				for (VwOutletTypeShortForm outletType: deletedOutletTypes){
					oldEntity.getOutletTypes().remove(outletType);
				}
			}			
			
			if (newIds.size() > 0) {
				List<VwOutletTypeShortForm> newOutletTypes = outletTypeDao.getByIds(newIds.toArray(new String[0]));
				oldEntity.getOutletTypes().addAll(newOutletTypes);
			}
		} else {
			oldEntity.getOutletTypes().clear();
		}
		
//		Integer firmCode = oldEntity.getFirmCode();
//		BeanUtils.copyProperties(model, oldEntity);
//		oldEntity.setFirmCode(firmCode);
		oldEntity.setUseFRAdmin(model.isUseFRAdmin());
		oldEntity.setName(model.getName());
		oldEntity.setStreetAddress(model.getStreetAddress());
		oldEntity.setDetailAddress(model.getDetailAddress());
		oldEntity.setMainContact(model.getMainContact());
		oldEntity.setLastContact(model.getLastContact());
		oldEntity.setFax(model.getFax());
		oldEntity.setTel(model.getTel());
		oldEntity.setWebSite(model.getWebSite());
		oldEntity.setMarketName(model.getMarketName());
		oldEntity.setRemark(model.getRemark());
		oldEntity.setDiscountRemark(model.getDiscountRemark());
		oldEntity.setLatitude(model.getLatitude());
		oldEntity.setLongitude(model.getLongitude());
		oldEntity.setBrCode(model.getBrCode());
		oldEntity.setIndoorMarketName(model.getIndoorMarketName());
		oldEntity.setOutletMarketType(model.getOutletMarketType());
		oldEntity.setStatus(model.getStatus());
		oldEntity.setCollectionMethod(model.getCollectionMethod());
		
		Tpu tpu = getTpuById(model.getTpuId());
		oldEntity.setTpu(tpu);
		
		if (model.getOpenStart() != null && Pattern.matches("\\d{2}:\\d{2}", model.getOpenStart())) {
			Date time = commonService.getTime(model.getOpenStart());
			oldEntity.setOpeningStartTime(time);
		} else {
			oldEntity.setOpeningStartTime(null);
		}
		if (model.getOpenEnd() != null && Pattern.matches("\\d{2}:\\d{2}", model.getOpenEnd())) {
			Date time = commonService.getTime(model.getOpenEnd());
			oldEntity.setOpeningEndTime(time);
		} else {
			oldEntity.setOpeningEndTime(null);
		}
		if (model.getConvenientStart() != null && Pattern.matches("\\d{2}:\\d{2}", model.getConvenientStart())) {
			Date time = commonService.getTime(model.getConvenientStart());
			oldEntity.setConvenientStartTime(time);
		} else {
			oldEntity.setConvenientStartTime(null);
		}
		if (model.getConvenientEnd() != null && Pattern.matches("\\d{2}:\\d{2}", model.getConvenientEnd())) {
			Date time = commonService.getTime(model.getConvenientEnd());
			oldEntity.setConvenientEndTime(time);
		} else {
			oldEntity.setConvenientEndTime(null);
		}
		
		if (model.getOpenStart2() != null && Pattern.matches("\\d{2}:\\d{2}", model.getOpenStart2())) {
			Date time = commonService.getTime(model.getOpenStart2());
			oldEntity.setOpeningStartTime2(time);
		} else {
			oldEntity.setOpeningStartTime2(null);
		}
		if (model.getOpenEnd2() != null && Pattern.matches("\\d{2}:\\d{2}", model.getOpenEnd2())) {
			Date time = commonService.getTime(model.getOpenEnd2());
			oldEntity.setOpeningEndTime2(time);
		} else {
			oldEntity.setOpeningEndTime2(null);
		}
		if (model.getConvenientStart2() != null && Pattern.matches("\\d{2}:\\d{2}", model.getConvenientStart2())) {
			Date time = commonService.getTime(model.getConvenientStart2());
			oldEntity.setConvenientStartTime2(time);
		} else {
			oldEntity.setConvenientStartTime2(null);
		}
		if (model.getConvenientEnd2() != null && Pattern.matches("\\d{2}:\\d{2}", model.getConvenientEnd2())) {
			Date time = commonService.getTime(model.getConvenientEnd2());
			oldEntity.setConvenientEndTime2(time);
		} else {
			oldEntity.setConvenientEndTime2(null);
		}
		
		if (outletImageStream != null) {
			if (oldEntity.getOutletImagePath() != null) {
				deleteOutletImage(oldEntity.getOutletImagePath(), fileBaseLoc);
			}
			
			String outletImagePath = saveOutletImage(outletImageStream, fileBaseLoc, oldEntity.getFirmCode());
			oldEntity.setOutletImagePath(outletImagePath);
			oldEntity.setImageModifiedTime(new java.util.Date());
		} else {
			if(!StringUtils.isEmpty(model.getOutletImagePath()) && model.getOutletImagePath().equals("del")){
				//Click remove button and no upload new photo
				deleteOutletImage(oldEntity.getOutletImagePath(), fileBaseLoc);
				oldEntity.setOutletImagePath(null);
				oldEntity.setImageModifiedTime(null);
			}
		}
		
		if (model.getOutletId() != null && model.getOutletId() > 0 && "Invalid".equals(oldEntity.getStatus())) {
			List<Quotation> quotations = quotationDao.getAllByOutletId(model.getOutletId());
			for (Quotation quotation : quotations) {
				//quotation.setStatus("RUA");
				quotationService.setRUAQuotation(quotation);
			}
		}
		
		outletDao.save(oldEntity);
		outletDao.flush();
	}
	
	/**
	 * Split time to two numbers
	 */
	public Integer[] splitTimeToHourMinute(String period) {
		Integer[] numbers = new Integer[]{null, null};
		
		if (period == null || period.indexOf(':') == -1) return numbers;
		
		String[] split = period.split(":");
		
		numbers[0] = new Integer(split[0]);
		numbers[1] = new Integer(split[1]);
		
		return numbers;
	}
	
	/**
	 * Get TPU
	 */
	public Tpu getTpuById(int id) {
		return tpuDao.findById(id);
	}
	
	/**
	 * Get all outlet types
	 */
	public List<VwOutletTypeShortForm> getOutletTypes() {
		return outletTypeDao.getAll();
	}
	
	
	
	/**
	 * Get outlet type by id
	 */
	public VwOutletTypeShortForm getOutletTypeById(String id) {
		return outletTypeDao.findById(id);
	}
	
	/**
	 * Delete outlet image
	 */
	@Transactional
	public void deleteOutletImage(String path, String fileBaseLoc) {
		File imageFile = new File(fileBaseLoc + path);
		if (imageFile.exists())
			imageFile.delete();
	}
	
	/**
	 * Save outlet image
	 */
	public String saveOutletImage(InputStream outletImageStream, String fileBaseLoc, int firmCode) throws Exception {
		File dir = new File(fileBaseLoc + "/outlet/" + firmCode);
		
		System.out.println("dir :: " + fileBaseLoc + "/outlet/" + firmCode);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filename = UUID.randomUUID().toString();
		
		Path path = FileSystems.getDefault().getPath(dir.getAbsolutePath(), filename);
		//Files.copy(outletImageStream, path);
		
		if((outletImageStream.available()/1024) > 250) {
			commonService.ImageCompression(outletImageStream, path);
		} else {
			Files.copy(outletImageStream, path);
		}
		
		return "/outlet/" + firmCode + "/" + filename;
	}
	
	/**
	 * Get attachments by outlet id
	 */
	public List<OutletAttachment> getAttachmentsByOutletId(int outletId) {
		return outletAttachmentDao.getAllByOutletId(outletId);
	}
	
	/**
	 * Get attachment by id
	 */
	public OutletAttachment getAttachmentById(int id) {
		return outletAttachmentDao.findById(id);
	}
	
	/**
	 * Delete outlet
	 */
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public boolean deleteOutlet(int id, String fileBaseLoc) {
		Outlet entity = getOutletById(id);
		if (entity == null)
			return false;
		
		entity.getOutletTypes().clear();
		
		List<Quotation> quotations = quotationDao.getAllByOutletId(id);
		for (Quotation quotation : quotations) {
			quotation.setOutlet(null);
			quotation.setStatus("RUA");
		}
		
		Iterator<OutletAttachment> attachments = entity.getOutletAttachments().iterator();
		while (attachments.hasNext()) {
			OutletAttachment attachment = attachments.next();
			deleteOutletImage(attachment.getPath(), fileBaseLoc);
			outletAttachmentDao.delete(attachment);
		}
		
//		List<OutletAttachment> attachmentEntities = getAttachmentsByOutletId(id);
//		for (OutletAttachment attachment : attachmentEntities) {
//			outletAttachmentDao.delete(attachment);
//		}
		
		if (entity.getOutletImagePath() != null) {
			deleteOutletImage(entity.getOutletImagePath(), fileBaseLoc);
		}
		
		outletDao.delete(entity);
		outletDao.flush();
		return true;
	}
	
	/**
	 * Validate quotation outlet type
	 */
	public boolean validateQuotationOutletType(int outletId, String[] outletTypeIds) {
		List<String> quotationOutletTypeShortCodes = outletDao.getQuotationOutletTypeCodes(outletId);
		
		List<VwOutletTypeShortForm> selectedOutletTypes = outletTypeDao.getByIds(outletTypeIds);
		List<String> selectedOutletTypeShortCodes = new ArrayList<String>();
		for (VwOutletTypeShortForm shortForm : selectedOutletTypes) {
			selectedOutletTypeShortCodes.add(shortForm.getShortCode());
		}
		
		Collection<String> deletedOutletTypes = (Collection<String>)CollectionUtils.subtract(quotationOutletTypeShortCodes, selectedOutletTypeShortCodes);
		if (deletedOutletTypes.size() > 0)
			return false;
		else
			return true;
	}
	
	/**
	 * Fill in gps
	 */
	public void fillInGps(OutletEditModel model) throws Exception {
		
		GeoLocation loc = geocode(model.getStreetAddress());
		if (loc == null) return;
		model.setLatitude(loc.getLatitude());
		model.setLongitude(loc.getLongitude());
		
//		String googleBrowserKey = configService.getGoogleBroswerKey();
//		
//		String query = "?key=" + googleBrowserKey;
//		query += "&address=" + URLEncoder.encode(model.getStreetAddress(), "UTF-8");
//		query += "&region=hk";
//		
////		ObjectMapper mapper = new ObjectMapper();
////		JsonNode gps = mapper.readTree(new URL("https://maps.googleapis.com/maps/api/geocode/json" + query));
//		JsonNode gps = commonService.getJSON("https://maps.googleapis.com/maps/api/geocode/json" + query);
//		
//		if (!"OK".equals(gps.get("status").asText())) return;
//		
//		JsonNode location = gps.get("results").get(0).get("geometry").get("location");
//		model.setLatitude(location.get("lat").asText());
//		model.setLongitude(location.get("lng").asText());
	}
	
	
	public GeoLocation geocode(String address) throws IOException{
		String googleBrowserKey = configService.getGoogleBroswerKey();
		
		String query = "?key=" + googleBrowserKey;
		query += "&address=" + URLEncoder.encode(address, "UTF-8");
		query += "&region=hk";
		
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode gps = mapper.readTree(new URL("https://maps.googleapis.com/maps/api/geocode/json" + query));
		JsonNode gps = commonService.getJSON("https://maps.googleapis.com/maps/api/geocode/json" + query);
		
		if (!"OK".equals(gps.get("status").asText())) return null;
		GeoLocation loc = new GeoLocation();
		JsonNode location = gps.get("results").get(0).get("geometry").get("location");
		loc.setLatitude(location.get("lat").asText());
		loc.setLongitude(location.get("lng").asText());
		return loc;
	}
	
	/**
	 * Prepare model for postback
	 */
	public void prepareModelForPostback(OutletEditModel model) {
		if (model.getTpuId() != null) {
			Tpu tpu = getTpuById(model.getTpuId());
			
			model.setTpuId(tpu.getTpuId());
			//model.setTpuLabel(tpu.getCode() + " - " + tpu.getDescription());
			model.setTpuLabel(tpu.getCode());
			
			model.setDistrictId(tpu.getDistrict().getId());
			model.setDistrictLabel(tpu.getDistrict().getCode() + " - " + tpu.getDistrict().getEnglishName());
		}
		
		if (model.getOutletId() != null) {
			Outlet entity = getOutletById(model.getOutletId());
			
			if (entity.getQuotations() != null)
				model.setNoOfQuotation(entity.getQuotations().size());
			
			if (entity.getOutletImagePath() != null) {
				File imageFile = new File(configService.getFileBaseLoc() + entity.getOutletImagePath());
				if (imageFile.exists())
					model.setOutletImagePath(entity.getOutletImagePath());
			}

			if (entity.getCreatedDate() != null) {
				model.setCreatedDate(commonService.formatDateTime(entity.getCreatedDate()));
				model.setModifiedDate(commonService.formatDateTime(entity.getModifiedDate()));
			}
		}
	}
	
	/**
	 * Convert entity to model
	 */
	public OutletEditModel convertEntityToModel(Outlet entity) {
		OutletEditModel model = new OutletEditModel();
		model.setOutletId(entity.getOutletId());
		model.setFirmCode(entity.getFirmCode());
		model.setName(entity.getName());
		
		Iterator<VwOutletTypeShortForm> outletTypes = entity.getOutletTypes().iterator();
		ArrayList<String> outletTypeIds = new ArrayList<String>();
		while (outletTypes.hasNext()) {
			outletTypeIds.add(outletTypes.next().getId());
		}
		model.setOutletTypeIds(outletTypeIds);
		
		if (entity.getTpu() != null) {
			model.setTpuId(entity.getTpu().getTpuId());
			//model.setTpuLabel(entity.getTpu().getCode() + " - " + entity.getTpu().getDescription());
			model.setTpuLabel(entity.getTpu().getCode());
			
			model.setDistrictId(entity.getTpu().getDistrict().getId());
			model.setDistrictLabel(entity.getTpu().getDistrict().getCode() + " - " + entity.getTpu().getDistrict().getEnglishName());
		}
		
		if (entity.getQuotations() != null)
			model.setNoOfQuotation(entity.getQuotations().size());
		
		if (entity.getOutletImagePath() != null) {
			File imageFile = new File(configService.getFileBaseLoc() + entity.getOutletImagePath());
			if (imageFile.exists())
				model.setOutletImagePath(entity.getOutletImagePath());
		}
		
		BeanUtils.copyProperties(entity, model);
		
		if (entity.getOpeningStartTime() != null){
			model.setOpenStart(commonService.formatTime(entity.getOpeningStartTime()));
		}
		if (entity.getOpeningEndTime() != null){
			model.setOpenEnd(commonService.formatTime(entity.getOpeningEndTime()));
		}
		if (entity.getConvenientStartTime() != null){
			model.setConvenientStart(commonService.formatTime(entity.getConvenientStartTime()));
		}
		if (entity.getConvenientEndTime() != null){
			model.setConvenientEnd(commonService.formatTime(entity.getConvenientEndTime()));
		}
		if (entity.getOpeningStartTime2() != null){
			model.setOpenStart2(commonService.formatTime(entity.getOpeningStartTime2()));
		}
		if (entity.getOpeningEndTime2() != null){
			model.setOpenEnd2(commonService.formatTime(entity.getOpeningEndTime2()));
		}
		if (entity.getConvenientStartTime2() != null){
			model.setConvenientStart2(commonService.formatTime(entity.getConvenientStartTime2()));
		}
		if (entity.getConvenientEndTime2() != null){
			model.setConvenientEnd2(commonService.formatTime(entity.getConvenientEndTime2()));
		}
		
		
		if (entity.getCreatedDate() != null) {
			model.setCreatedDate(commonService.formatDateTime(entity.getCreatedDate()));
			model.setModifiedDate(commonService.formatDateTime(entity.getModifiedDate()));
		}
		
		return model;
	}
	
	/**
	 * Merge time period
	 */
	@Deprecated
	public String mergeTimePeriod(Integer start, Integer end) {
		if (start == null && end == null) return null;
		
		String period = "";
		if (start == null)
			period += "00";
		else if (start.toString().length() == 1)
			period += "0" + start.toString();
		else
			period += start.toString();
		
		period += ":";
		
		if (end == null)
			period += "00";
		else if (end.toString().length() == 1)
			period += "0" + end.toString();
		else
			period += end.toString();
		
		return period;
	}
	
	/**
	 * Save attachment
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void saveAttachment(int id, Hashtable<Integer, InputStream> overwriteAttachments, Hashtable<Integer, InputStream> newAttachments, String fileBaseLoc) throws Exception {
		Outlet outlet = getOutletById(id);
		
		Enumeration<Integer> keys = overwriteAttachments.keys();

		while (keys.hasMoreElements()) {
			Integer oldId = keys.nextElement();
			OutletAttachment oldAttachment = getAttachmentById(oldId);
			if (oldAttachment == null) continue;
			
			deleteOutletImage(oldAttachment.getPath(), fileBaseLoc);
			
			String attachmentPath = saveOutletImage(overwriteAttachments.get(oldId), fileBaseLoc, outlet.getFirmCode());
			oldAttachment.setPath(attachmentPath);
		}
		
		for(Integer key : newAttachments.keySet()){
			OutletAttachment attachment = new OutletAttachment();
			attachment.setOutlet(outlet);
			
			String attachmentPath = saveOutletImage(newAttachments.get(key), fileBaseLoc, outlet.getFirmCode());
			attachment.setPath(attachmentPath);
			attachment.setSequence(key);
			outletAttachmentDao.save(attachment);
		}
		
//		for (InputStream newAttachment : newAttachments) {
//			OutletAttachment attachment = new OutletAttachment();
//			attachment.setOutlet(outlet);
//			
//			String attachmentPath = saveOutletImage(newAttachment, fileBaseLoc, outlet.getFirmCode());
//			attachment.setPath(attachmentPath);
//			Integer maxSequence = outletAttachmentDao.getMaxSequence(outlet.getId());
//			attachment.setSequence(maxSequence == null ? 1 : maxSequence + 1);
//			outletAttachmentDao.save(attachment);
//		}
		outletAttachmentDao.flush();
	}

	/**
	 * Get outlet select2 format
	 */
	public Select2ResponseModel queryOutletSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<Outlet> entities = outletDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = outletDao.countSearch(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Outlet d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getFirmCode() + " - " + d.getName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	

	/**
	 * Get outlet select2 format
	 */
	public Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<VwOutletTypeShortForm> entities = outletTypeDao.searchOutletType(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = outletTypeDao.countSearch(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (VwOutletTypeShortForm d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(d.getShortCode() + " - " + d.getChineseName());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	
	
	public String getOutletSelectSingle(Integer id){
		Outlet entity = outletDao.findById(id);
		if (entity == null)
			return null;
		else
			return entity.getFirmCode() + " - " + entity.getName();
	}
	
	
	@Transactional
	public List<OutletSyncData> syncOutletData(List<OutletSyncData> outlets,Date lastSyncTime, Boolean dataReturn, Integer[] outletIds){
		
		for(Integer a : outletIds) {
			System.out.print(a + ", ");
		}
		
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		List<Integer> allOutletIds = new ArrayList<Integer>();
		allOutletIds.addAll(Arrays.asList(outletIds));
		List<Integer> unUpdateIds = new ArrayList<Integer>();
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		if (outlets != null && outlets.size() > 0){
			for (OutletSyncData outlet : outlets){
				if ("D".equals(outlet.getLocalDbRecordStatus())){
					continue;
				}
				Outlet entity = null;
				if (outlet.getOutletId() == null){
					entity = new Outlet();
					Integer maxFirmCode = outletDao.getMaxFirmCode();
					if (maxFirmCode == null)
						outlet.setFirmCode(1);
					else
						outlet.setFirmCode(maxFirmCode + 1);
				}
				else{
					entity = outletDao.findById(outlet.getOutletId());
					if (entity !=null && entity.getModifiedDate() != null && entity.getModifiedDate().after(outlet.getModifiedDate())){
						unUpdateIds.add(entity.getOutletId());
						table.put(entity.getOutletId(), outlet.getLocalId());
						continue;
					}
				}
				
				BeanUtils.copyProperties(outlet, entity);
				
				Date openingStartTime = null;
				if(!StringUtils.isEmpty(outlet.getOpeningStartTime())){
					try{
						openingStartTime = commonService.getTime(outlet.getOpeningStartTime());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setOpeningStartTime(openingStartTime);
				
				Date openingEndTime = null;
				if(!StringUtils.isEmpty(outlet.getOpeningEndTime())){
					try{
						openingEndTime = commonService.getTime(outlet.getOpeningEndTime());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setOpeningEndTime(openingEndTime);
				
				Date convenientStartTime = null;
				if(!StringUtils.isEmpty(outlet.getConvenientStartTime())){
					try{
						convenientStartTime = commonService.getTime(outlet.getConvenientStartTime());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setConvenientStartTime(convenientStartTime);
				
				Date convenientEndTime = null;
				if(!StringUtils.isEmpty(outlet.getConvenientEndTime())){
					try{
						convenientEndTime = commonService.getTime(outlet.getConvenientEndTime());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setConvenientEndTime(convenientEndTime);
				
				Date openingStartTime2 = null;
				if(!StringUtils.isEmpty(outlet.getOpeningStartTime2())){
					try{
						openingStartTime2 = commonService.getTime(outlet.getOpeningStartTime2());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setOpeningStartTime2(openingStartTime2);
				
				Date openingEndTime2 = null;
				if(!StringUtils.isEmpty(outlet.getOpeningEndTime2())){
					try{
						openingEndTime2 = commonService.getTime(outlet.getOpeningEndTime2());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setOpeningEndTime2(openingEndTime2);
				
				Date convenientStartTime2 = null;
				if(!StringUtils.isEmpty(outlet.getConvenientStartTime2())){
					try{
						convenientStartTime2 = commonService.getTime(outlet.getConvenientStartTime2());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setConvenientStartTime2(convenientStartTime2);
				
				Date convenientEndTime2 = null;
				if(!StringUtils.isEmpty(outlet.getConvenientEndTime2())){
					try{
						convenientEndTime2 = commonService.getTime(outlet.getConvenientEndTime2());
					} catch (Exception e){
						throw new RuntimeException(e);
					}
				}
				entity.setConvenientEndTime2(convenientEndTime2);
				
				if (outlet.getTpuId() != null){
					Tpu tpu = tpuDao.findById(outlet.getTpuId());
					if (tpu != null){
						entity.setTpu(tpu);		
					}
				}
				entity.setByPassModifiedDate(true);
				outletDao.save(entity);
				
				allOutletIds.add(entity.getOutletId());
				table.put(entity.getOutletId(), outlet.getLocalId());
			}
			outletDao.flush();
		}
		
		if (dataReturn != null && dataReturn){
			List<OutletSyncData> updatedData = new ArrayList<OutletSyncData>();
			if(allOutletIds!=null && allOutletIds.size()>0){
				allOutletIds = new ArrayList<Integer>(new HashSet<Integer>(allOutletIds));
				updatedData.addAll(syncOutletRecusiveQuery(allOutletIds, lastSyncTime));
			}
			
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncOutletRecusiveQuery(unUpdateIds, null));
			}
			
			List<OutletSyncData> unique = new ArrayList<OutletSyncData>(new HashSet<OutletSyncData>(updatedData));
			for (OutletSyncData data : unique){
				if (table.containsKey(data.getOutletId())){
					data.setLocalId(table.get(data.getOutletId()));
				}
			}	
			return unique;
		}
		
		return new ArrayList<OutletSyncData>();
	}
	
	@Transactional
	public List<OutletAttachmentSyncData> syncOutletAttachments(List<OutletAttachmentSyncData> attachments, Date lastSyncTime, Boolean dataReturn){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> unUpdateIds = new ArrayList<Integer>();
		if (attachments != null && attachments.size() > 0){
			for (OutletAttachmentSyncData attachment : attachments){
//				if (attachment.getIsDeleted() != null && attachment.getIsDeleted()){
//					OutletAttachment oldEntity = outletAttachmentDao.findById(attachment.getOutletAttachmentId());
//					if (oldEntity != null){
//						deleteOutletImage(oldEntity.getPath(), configService.getFileBaseLoc());
//						outletAttachmentDao.delete(oldEntity);	
//					}
//					continue;
//				}
				
				
				if(attachment.getOutletAttachmentId() == null){
					if("D".equals(attachment.getLocalDbRecordStatus())){
						continue;
					}
				} else {
					OutletAttachment oldEntity = outletAttachmentDao.findById(attachment.getOutletAttachmentId());
					if (oldEntity !=null && oldEntity.getModifiedDate()!=null && oldEntity.getModifiedDate().after(attachment.getModifiedDate())){
						unUpdateIds.add(oldEntity.getOutletAttachmentId());
						table.put(oldEntity.getOutletAttachmentId(), attachment.getLocalId());
						continue;
					}
					if ("D".equals(attachment.getLocalDbRecordStatus())){
						if (oldEntity != null){
							deleteOutletImage(oldEntity.getPath(), configService.getFileBaseLoc());
							outletAttachmentDao.delete(oldEntity);	
						}
						continue;
					}
				}
				
				OutletAttachment entity = null;
				
				if (attachment.getOutletAttachmentId() == null){
					entity = new OutletAttachment();
				}
				else{
					entity = outletAttachmentDao.findById(attachment.getOutletAttachmentId());
				}
				
				BeanUtils.copyProperties(attachment, entity);
				Outlet outlet = outletDao.findById(attachment.getOutletId());
				entity.setOutlet(outlet);
				entity.setByPassModifiedDate(true);
				outletAttachmentDao.save(entity);
				unUpdateIds.add(entity.getOutletAttachmentId());
				table.put(entity.getOutletAttachmentId(), attachment.getLocalId());
			}
			outletAttachmentDao.flush();
		}
		
		if (dataReturn != null && dataReturn){
			List<OutletAttachmentSyncData> updatedData = new ArrayList<OutletAttachmentSyncData>();
			
			if(unUpdateIds!=null && unUpdateIds.size()>0){
				updatedData.addAll(syncOutletAttachmentRecursiveQuery(unUpdateIds));
			}
			
			updatedData.addAll(outletAttachmentDao.getUpdatedOutletAttachments(lastSyncTime, null));
			
			List<OutletAttachmentSyncData> unique = new ArrayList<OutletAttachmentSyncData>(new HashSet<OutletAttachmentSyncData>(updatedData));
			for (OutletAttachmentSyncData attachment : unique){
				if (table.containsKey(attachment.getOutletAttachmentId())){
					attachment.setLocalId(table.get(attachment.getOutletAttachmentId()));
				}
			}	
			return unique;
		}
		
		return new ArrayList<OutletAttachmentSyncData>();
	}
	
	/**
	 * OnlineFunction
	 */
	public List<OutletSyncData> getOutletbyNamePhoneBRCode(String name, String phone, String brCode){
		return outletDao.getOutletbyNamePhoneBRCode(name, phone, brCode);
	}
	
	/**
	 * Save attachment
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void uploadAttachment(int id, InputStream attachment) throws Exception {
		OutletAttachment oldAttachment = getAttachmentById(id);
		if (oldAttachment == null) return;
		deleteOutletImage(oldAttachment.getPath(), configService.getFileBaseLoc());
		String attachmentPath = saveOutletImage(attachment, configService.getFileBaseLoc(), oldAttachment.getOutlet().getFirmCode());
		oldAttachment.setPath(attachmentPath);
		outletAttachmentDao.save(oldAttachment);
		outletAttachmentDao.flush();
	}
	
	/**
	 * Save outlet Image by DataSync
	 */
	@Transactional(isolation=Isolation.SERIALIZABLE)
	public void uploadOutletImage(int id, InputStream attachment) throws Exception {
		Outlet outlet = outletDao.findById(id);
		if (outlet == null) return;
		deleteOutletImage(outlet.getOutletImagePath(), configService.getFileBaseLoc());
		String outletImagePath = saveOutletImage(attachment, configService.getFileBaseLoc(), outlet.getFirmCode());
		outlet.setOutletImagePath(outletImagePath);
		outlet.setImageModifiedTime(new Date());
		outletDao.save(outlet);
		outletDao.flush();
	}
	
	public List<OutletTypeOutletSyncData> getUpdateOutletTypeOutlet(Date lastSyncTime){
		return outletDao.getUpdateOutletTypeOutlet(lastSyncTime);
	}
	
	@Transactional
	public void updateMajorLocation(Integer outletId, String majorLocation){
		Outlet outlet = outletDao.findById(outletId);
		outlet.setMarketName(majorLocation);
		outletDao.save(outlet);
		outletDao.flush();
	}

	/**
	 * Prepare map outlet
	 */
	public MapOutletModel prepareMapOutlet(int outletId) {
		MapOutletModel model = new MapOutletModel();

		Outlet entity = outletDao.findById(outletId);
		OutletViewModel viewModel = quotationRecordService.prepareOutletViewModel(entity);
		
		BeanUtils.copyProperties(viewModel, model);
		model.setTpuId(entity.getTpu().getId());
		//model.setTpuName(entity.getTpu().getCode() + " - " + entity.getTpu().getDescription());
		model.setTpuName(entity.getTpu().getCode());
		
		model.setOpeningStartTime(commonService.formatTime(entity.getOpeningStartTime()));
		model.setOpeningEndTime(commonService.formatTime(entity.getOpeningEndTime()));
		model.setConvenientStartTime(commonService.formatTime(entity.getConvenientStartTime()));
		model.setConvenientEndTime(commonService.formatTime(entity.getConvenientEndTime()));
		model.setOpeningStartTime2(commonService.formatTime(entity.getOpeningStartTime2()));
		model.setOpeningEndTime2(commonService.formatTime(entity.getOpeningEndTime2()));
		model.setConvenientStartTime2(commonService.formatTime(entity.getConvenientStartTime2()));
		model.setConvenientEndTime2(commonService.formatTime(entity.getConvenientEndTime2()));
		
		return model;
	}
	
	/**
	 * Get batch codes
	 */
	public List<String> getBatchCodes(int outletId) {
		return outletDao.getBatchCodes(outletId);
	}
	
	/**
	 * Get batch code select2 format
	 */
	public Select2ResponseModel queryBatchCodeSelect2(Select2RequestModel queryModel, int outletId) {
		queryModel.setRecordsPerPage(10);
		List<OutletBatchCodeModel> entities = batchDao.searchBatchCodes(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), outletId);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = batchDao.countSearchBatchCodes(queryModel.getTerm(), outletId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (OutletBatchCodeModel d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getKey()));
			item.setText(d.getValue());
			item.setParam1(d.getEmptyOutlet() != null && d.getEmptyOutlet() ? "true" : "false");
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public List<UpdateOutletImageModel> getUpdatedOutletImage(Date lastSyncTime){
		return outletDao.getUpdatedOutletImage(lastSyncTime);
	}
	
	public List<OutletTypeOutletSyncData> syncOutletTypeOutlet(List<OutletTypeOutletSyncData> outletTypeOutlets){
		
		if(outletTypeOutlets!=null && outletTypeOutlets.size()>0){
			for(OutletTypeOutletSyncData model : outletTypeOutlets){
				if("D".equals(model.getLocalDbRecordStatus())){
					continue;
				}
				
				if(model.getOutletId()==null || model.getShortCode()==null){
					continue;
				}
				
				Outlet outlet = outletDao.findById(model.getOutletId());
				
				if(outlet==null){
					throw new RuntimeException("Outlet not found: Outlet ID = "+model.getOutletId());
				}
				
				VwOutletTypeShortForm outletType = outletTypeDao.findById(model.getShortCode());
				outlet.getOutletTypes().add(outletType);
				
//				boolean hasOutletType = false;
//				for(VwOutletTypeShortForm outletType : outlet.getOutletTypes()){
//					if(model.getShortCode().equals(outletType.getId())){
//						hasOutletType = true;
//						break;
//					}
//				}
//				
//				if(!hasOutletType){
//					VwOutletTypeShortForm outletType = outletTypeDao.findById(model.getShortCode());
//					List<VwOutletTypeShortForm> outletTypes = new ArrayList<VwOutletTypeShortForm>();
//					outletTypes.add(outletType);
//					outlet.getOutletTypes().addAll(outletTypes);
//				}
				
				outletDao.save(outlet);
			}
			outletDao.flush();
		}
		
		return new ArrayList<OutletTypeOutletSyncData>();
	}
	
	public List<OutletAttachmentSyncData> syncOutletAttachmentRecursiveQuery(List<Integer> outletAttachmentIds){
		List<OutletAttachmentSyncData> entities = new ArrayList<OutletAttachmentSyncData>();
		if(outletAttachmentIds.size()>2000){
			List<Integer> ids = outletAttachmentIds.subList(0, 2000);
			entities.addAll(syncOutletAttachmentRecursiveQuery(ids));
			
			List<Integer> remainIds = outletAttachmentIds.subList(2000, outletAttachmentIds.size());
			entities.addAll(syncOutletAttachmentRecursiveQuery(remainIds));
		} else if(outletAttachmentIds.size()>0){
			return outletAttachmentDao.getUpdatedOutletAttachments(null, outletAttachmentIds.toArray(new Integer[0]));
		}
		
		return entities;
	}
	
	public List<OutletSyncData> syncOutletRecusiveQuery(List<Integer> outletIds, Date lastSyncTime){
		List<OutletSyncData> entities = new ArrayList<OutletSyncData>();
		if(outletIds.size()>2000){
			List<Integer> ids = outletIds.subList(0, 2000);
			entities.addAll(syncOutletRecusiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = outletIds.subList(2000, outletIds.size());
			entities.addAll(syncOutletRecusiveQuery(remainIds, lastSyncTime));
		} else if(outletIds.size()>0){
			return outletDao.getUpdatedOutlets(lastSyncTime, outletIds.toArray(new Integer[0]));
		}
		
		return entities;
	}
	
	public void deleteOutletAttachment(Integer id, String fileBaseLoc){
		OutletAttachment attachment = getAttachmentById(id);
		if (attachment == null) return;
		
		deleteOutletImage(attachment.getPath(), fileBaseLoc);
		outletAttachmentDao.delete(attachment);
		outletAttachmentDao.flush();
	}
	

	public List<OutletSyncData> syncZipOutletsImage(String endDate){
		List<OutletSyncData> result = new ArrayList<OutletSyncData>();
		
		result = outletDao.getOutletInfoMonthly(endDate);
		
		if(result.size() > 0) {
			return result;
		}
		
		return new ArrayList<OutletSyncData>();
	}
	
	public List<OutletAttachmentZipImageSyncData> syncZipOutletsAttachmentImage(String endDate) {

		List<OutletAttachmentZipImageSyncData> result = new ArrayList<OutletAttachmentZipImageSyncData>();
		
		result = outletAttachmentDao.getOutletAttachmentInfoMonthly(endDate);
		
		if(result.size() > 0) {
			return result;
		}
		
		return new ArrayList<OutletAttachmentZipImageSyncData>();
	}
}
