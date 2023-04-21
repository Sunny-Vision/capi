package capi.service.assignmentManagement;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.BatchDao;
import capi.dal.DistrictDao;
import capi.dal.OutletDao;
import capi.dal.PricingMonthDao;
import capi.dal.ProductDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationDao;
import capi.dal.SurveyMonthDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.District;
import capi.entity.Outlet;
import capi.entity.PricingFrequency;
import capi.entity.Quotation;
import capi.entity.SurveyMonth;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.QuotationSyncData;
import capi.model.api.dataSync.RUAQuotationSyncData;
import capi.model.api.dataSync.RUAUserSyncData;
import capi.model.assignmentManagement.QuotationEditModel;
import capi.model.assignmentManagement.QuotationTableList;
import capi.model.assignmentManagement.RUASettingEditModel;
import capi.model.commonLookup.QuotationLookupTableList;
import capi.model.productMaintenance.ProductSpecificationEditModel;
import capi.service.BaseService;
import capi.service.CommonService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Service("QuotationService")
public class QuotationService extends BaseService {

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private UnitDao unitDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private OutletDao outletDao;

	@Autowired
	private BatchDao batchDao;

	@Autowired
	private PricingMonthDao pricingMonthDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SystemConfigurationDao sysConfigDao;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	
	/** 
	 * Quotation datatable query 
	 */
	public DatatableResponseModel<QuotationTableList> queryQuotation(DatatableRequestModel model){
		Order order = this.getOrder(model, "", "id", "purpose", "cpiBasePeriod", "unitCode", "unitName", 
				"productAttribute1","productAttribute2","productAttribute3",
				"Status","firmName","batchCode","cpiQuotationType", "formType",
				"pricingFrequency","frAdmin","frField","lastFRAppliedDate","seasonalWithdrawal","seasonality","ruaDate",				
				"isICP","productId","outletId",
				"productAttribute4","productAttribute5",
				"indoorAllocationCode"
				);		
		
		/*Order order = this.getOrder(model, "", "id", "purpose", "unitCode", "unitName", "productId","productAttribute1","outletId",
				"firmName","batchCode","pricingFrequency","Status","isICP","indoorAllocationCode","CPICompilationSeries",
				"seasonalWithdrawal","frAdmin","frField","lastFRAppliedDate","seasonality","ruaMonth");
		*/
		String search = model.getSearch().get("value");
		
		Integer purposeId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))) 
			purposeId = Integer.parseInt(model.getSearch().get("purposeId"));
		/*
		Integer unitId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))) 
			unitId = Integer.parseInt(model.getSearch().get("unitId"));
		*/
		List<Integer> unitId = new ArrayList<Integer>();
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString = Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
		}
		
		Integer productId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("productId")))
			productId = Integer.parseInt(model.getSearch().get("productId"));
		
		Integer outletId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			outletId = Integer.parseInt(model.getSearch().get("outletId"));
		
		Integer batchId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("batchId")))
			batchId = Integer.parseInt(model.getSearch().get("batchId"));
		
		Integer pricingFrequencyId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("pricingFrequencyId"))) 
			pricingFrequencyId = Integer.parseInt(model.getSearch().get("pricingFrequencyId"));
		
		String status = model.getSearch().get("status");
		
		Boolean isICP = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("isICP"))) {
			isICP = (model.getSearch().get("isICP").trim().equals("Y")) ? Boolean.TRUE : Boolean.FALSE;
		}
				String indoorAllocationCode = model.getSearch().get("indoorAllocationCode");
		
		List<QuotationTableList> result = quotationDao.getQuotationTableList(search, purposeId, unitId, productId,
				outletId, batchId, pricingFrequencyId, status, isICP, indoorAllocationCode, 
				model.getStart(), model.getLength(), order);
	
		DatatableResponseModel<QuotationTableList> response = new DatatableResponseModel<QuotationTableList>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationDao.countQuotationTableList("", null, null, null,
				null, null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationDao.countQuotationTableList(search, purposeId, unitId, productId,
				outletId, batchId, pricingFrequencyId, status, isICP, indoorAllocationCode);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		
		return response;
	}
	

	/** 
	 * Quotation datatable query 
	 */
	public DatatableResponseModel<QuotationTableList> queryQuotationRUA(DatatableRequestModel model, int userId){
		Order order = this.getOrder(model, "", "id", "purpose", "cpiBasePeriod", "unitCode", "unitName", 
				"productAttribute1","productAttribute2","productAttribute3",
				"Status","firmName","batchCode","cpiQuotationType",				
				"formType","pricingFrequency","frAdmin","frField","lastFRAppliedDate","seasonalWithdrawal","seasonality","ruaDate",				
				"isICP","productId","outletId",
				"productAttribute4","productAttribute5",
				"indoorAllocationCode"
				);		
		
		/*Order order = this.getOrder(model, "", "id", "purpose", "unitCode", "unitName", "productId","productAttribute1","outletId",
				"firmName","batchCode","pricingFrequency","Status","isICP","indoorAllocationCode","CPICompilationSeries",
				"seasonalWithdrawal","frAdmin","frField","lastFRAppliedDate","seasonality","ruaMonth");
		*/
		String search = model.getSearch().get("value");
		
		Integer purposeId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))) 
			purposeId = Integer.parseInt(model.getSearch().get("purposeId"));
		
//		Integer unitId = null;
//		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))) 
//			unitId = Integer.parseInt(model.getSearch().get("unitId"));
		
		List<Integer> unitId = new ArrayList<Integer>();
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString = Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
		}
		
		
		Integer productId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("productId")))
			productId = Integer.parseInt(model.getSearch().get("productId"));
		
		Integer outletId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			outletId = Integer.parseInt(model.getSearch().get("outletId"));
		
		Integer batchId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("batchId")))
			batchId = Integer.parseInt(model.getSearch().get("batchId"));
		
		Integer pricingFrequencyId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("pricingFrequencyId"))) 
			pricingFrequencyId = Integer.parseInt(model.getSearch().get("pricingFrequencyId"));
		
		String status = "RUA";
		
		Boolean isICP = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("isICP"))) {
			isICP = (model.getSearch().get("isICP").trim().equals("Y")) ? Boolean.TRUE : Boolean.FALSE;
		}
				String indoorAllocationCode = model.getSearch().get("indoorAllocationCode");
		
		User user = userDao.findById(userId);
		List<Integer> districtIds = new ArrayList<Integer>();
		if (user.getDistricts().size() > 0) {
			for (District district : user.getDistricts()) {
				districtIds.add(district.getDistrictId());
			}
		}
		
		int summerStartDate = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_START_DATE).getValue());
		int summerEndDate = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_END_DATE).getValue());
		int winterStartDate = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_START_DATE).getValue());
		int winterEndDate = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_END_DATE).getValue());
		
		Calendar today = Calendar.getInstance();
		int todayMonth = today.get(Calendar.MONTH) + 1;
		
		boolean todayIsSummerDate = false;
		boolean todayIsWinterDate = false;
		
		if (summerStartDate == todayMonth || summerEndDate == todayMonth) {
			todayIsSummerDate = true;
		} else if (summerStartDate < summerEndDate) {
			if (summerStartDate < todayMonth && summerEndDate > todayMonth)
				todayIsSummerDate = true;
		} else if (summerStartDate > summerEndDate) {
			if (summerStartDate < todayMonth || summerEndDate > todayMonth)
				todayIsSummerDate = true;
		}
		
		if (winterStartDate == todayMonth || winterEndDate == todayMonth) {
			todayIsWinterDate = true;
		} else if (winterStartDate < winterEndDate) {
			if (winterStartDate < todayMonth && winterEndDate > todayMonth)
				todayIsWinterDate = true;
		} else if (winterStartDate > winterEndDate) {
			if (winterStartDate < todayMonth || winterEndDate > todayMonth)
				todayIsWinterDate = true;
		}
		
		List<QuotationTableList> result = quotationDao.getQuotationRUATableList(search, purposeId, unitId, productId,
				outletId, batchId, pricingFrequencyId, status, isICP, indoorAllocationCode, 
				model.getStart(), model.getLength(), order,
				todayIsSummerDate, todayIsWinterDate, districtIds, userId);
	
		DatatableResponseModel<QuotationTableList> response = new DatatableResponseModel<QuotationTableList>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationDao.countQuotationRUATableList("", null, null, null,
				null, null, null, status, null, null, todayIsSummerDate, todayIsWinterDate, districtIds, userId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationDao.countQuotationRUATableList(search, purposeId, unitId, productId,
				outletId, batchId, pricingFrequencyId, status, isICP, indoorAllocationCode,
				todayIsSummerDate, todayIsWinterDate, districtIds, userId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		
		return response;
	}
	

	
	/**
	 * Get Purpose select2 format
	 */
	public Select2ResponseModel queryIndoorAllocationCodeSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		
		List<String> indoorAllocationCodes = quotationDao.searchIndoorAllocationCode(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationDao.countSearchIndoorAllocationCode(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String indoorAllocationCode : indoorAllocationCodes) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(indoorAllocationCode);
			item.setText(indoorAllocationCode);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}	
	
	/**
	 * Get Quotation Loading by Unit Id and Outlet Id
	 */
	public Double queryQuotationLoading(Integer unitId, Integer outletId) {

		Double value = quotationDao.getQuotationLoadingByUnitIdOutletId(unitId, outletId);
		
		if (value == null || value == 0){
			return null;
		}
		else{
			return 1.0 / value;
		}

	}
	
	/**
	 * Get Quotation by ID
	 * 
	 */
	
	public Quotation getQuotationById(Integer id) {
		return quotationDao.findById(id);
	}

	
	/**
	 * Get ProductSpecificationEditModel by ID
	 */
	public List<ProductSpecificationEditModel>  getProductSpecificationListByIds(Integer productId, Integer productGroupId) {
		
		List<ProductSpecificationEditModel> all = null; //productGroupDao.getProductSpecificationEditModelByProductGroupId(productGroupId);
		List<ProductSpecificationEditModel> existing = null;
		
		if (productId != null && productId > 0) {
			existing = productDao.getProductSpecificationEditModelById(productId);
			if (all.size() != existing.size()) {
				for (int i = 0 ; i < all.size() ; i++ ) {
					for (int j = 0 ; j < existing.size() ; j++ ) {
						if (all.get(i).getProductAttributeId() == existing.get(j).getProductAttributeId()){
							all.get(i).setValue(existing.get(j).getValue());
							break;
						}
					}
				}
			} else {
				all = existing;
			}
		}
		
		return all;
	}
	
	/**
	 * Save Quotation
	*/
	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public boolean save(QuotationEditModel model) throws ParseException {
		Quotation oldEntity = null;
		if (model.getQuotationId() != null && model.getQuotationId() > 0) {
			oldEntity = quotationDao.findById(model.getQuotationId());
		} else {
			oldEntity = new Quotation();
		}
		model.setLastFRAppliedDate(oldEntity.getLastFRAppliedDate());
		
		BeanUtils.copyProperties(model, oldEntity);
		oldEntity.setFRApplied(model.getIsFRApplied()!=null&&model.getIsFRApplied());
		oldEntity.setIsFRAdminPercentage(model.getIsFRAdminPercentage());
		oldEntity.setIsFRFieldPercentage(model.getIsFRFieldPercentage());
		oldEntity.setReturnGoods(model.getIsReturnGoods()!=null&&model.getIsReturnGoods());
		oldEntity.setReturnNewGoods(model.getIsReturnNewGoods()!=null&&model.getIsReturnNewGoods());
		if (StringUtils.isNotEmpty(model.getSeasonalWithdrawalMonth())) {
			oldEntity.setSeasonalWithdrawal(commonService.getMonth(model.getSeasonalWithdrawalMonth()));
		} else {
			oldEntity.setSeasonalWithdrawal(null);
		}

		oldEntity.setUnit(unitDao.findById(model.getUnitId()));
		if (model.getProductId() != null) {
			oldEntity.setProduct(productDao.findById(model.getProductId()));
		} else {
			oldEntity.setProduct(null);
		}
		if (model.getOutletId() != null ) {
			oldEntity.setOutlet(outletDao.findById(model.getOutletId()));
		} else {
			oldEntity.setOutlet(null);
		}
		oldEntity.setBatch(batchDao.findById(model.getBatchId()));
		oldEntity.setTempIsUseFRAdmin(model.getIsTempIsUseFRAdmin());
		oldEntity.setTempIsReturnGoods(model.getIsTempIsReturnGoods() != null && model.getIsTempIsReturnGoods());
		oldEntity.setTempIsReturnNewGoods(model.getIsTempIsReturnNewGoods() != null && model.getIsTempIsReturnNewGoods());
		oldEntity.setTempIsFRApplied(model.getIsTempIsFRApplied() != null && model.getIsTempIsFRApplied());
		oldEntity.setLastSeasonReturnGoods(model.getIsLastSeasonReturnGoods() != null && model.getIsLastSeasonReturnGoods());
		
		// RUA Setting
		if(model.getStatus().equals("RUA")) {
			//if (model.getQuotationId() != null && model.getQuotationId() > 0) {
				oldEntity.setRUAAllDistrict(model.getRuaSettingEditModel().getIsRUAAllDistrict());
				if(model.getRuaSettingEditModel().getIsRUAAllDistrict() == true) {
					oldEntity.setDistrict(null);
				} else {
					District district = districtDao.getDistrictById(model.getRuaSettingEditModel().getDistrictId());
					oldEntity.setDistrict(district);
				}
				
				if (model.getRuaSettingEditModel().getUserId() != null) {
					ArrayList<Integer> oldUserIds = new ArrayList<Integer>();
					for (User user : oldEntity.getUsers()) {
						oldUserIds.add(user.getId());
					}
					
					Collection<Integer> deletedIds = (Collection<Integer>)CollectionUtils.subtract(oldUserIds, model.getRuaSettingEditModel().getUserId());
					Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(model.getRuaSettingEditModel().getUserId(), oldUserIds);
					
					if (deletedIds.size() > 0){
						Integer[] deletedIdsArray = deletedIds.toArray(new Integer[0]);
						List<Integer> deletedIdsList = new ArrayList<Integer>();
						for(int i = 0; i < deletedIdsArray.length; i++) {
							Integer deletedId = deletedIdsArray[i];
							deletedIdsList.add(deletedId);
						}
						
						List<User> deletedUsers = userDao.getUsersByIds(deletedIdsList);
						for (User user: deletedUsers){
							oldEntity.getUsers().remove(user);
						}
					}
					
					if (newIds.size() > 0) {
						Integer[] newIdsArray = newIds.toArray(new Integer[0]);
						List<Integer> newIdsList = new ArrayList<Integer>();
						for(int i = 0; i < newIdsArray.length; i++) {
							Integer newId = newIdsArray[i];
							newIdsList.add(newId);
						}
						
						List<User> newUsers = userDao.getUsersByIds(newIdsList);
						oldEntity.getUsers().addAll(newUsers);
					}
				} else {
					oldEntity.getUsers().clear();
				}
		
			//} 
		}
		
		quotationDao.save(oldEntity);
		quotationDao.flush();
		
		return true;
		
	}
	
	/**
	 * Delete
	 */
	@Transactional
	public boolean deleteQuotation(List<Integer> ids) {
		
		List<Quotation> items = quotationDao.getQuotationByIds(ids);
		if (items.size() != ids.size()){
			return false;
		}
		
		for(Quotation item: items){
			quotationDao.delete(item);
		}
		quotationDao.flush();
		
		return true;
	}
	
	/**
	 * Change Status
	 */
	@Transactional
	public boolean changeStatus(List<Integer> ids, String status) {
		
		List<Quotation> items = quotationDao.getQuotationByIds(ids);
		if (items.size() != ids.size()){
			return false;
		}
		
		for(Quotation item: items){
			item.setStatus(status);
			quotationDao.save(item);
		}
		quotationDao.flush();
		
		return true;
	}
	
	/**
	 * Convert entity to model
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public QuotationEditModel convertEntityToModel(Quotation entity){
		QuotationEditModel model = new QuotationEditModel();
		BeanUtils.copyProperties(entity, model);

		if (entity.getId() != null) {

			
			model.setUnitId(entity.getUnit().getId());
			
			if (entity.getProduct() != null) {
				model.setProductId(entity.getProduct().getId());
			}
			if (entity.getOutlet() != null) {
				model.setOutletId(entity.getOutlet().getId());
			}
			model.setBatchId(entity.getBatch().getId());
			model.setDisplayCreatedDate(entity.getCreatedDate());
			model.setDisplayModifiedDate(entity.getModifiedDate());
			
			model.setIsFRApplied(entity.isFRApplied());
			model.setIsReturnGoods(entity.isReturnGoods());
			model.setIsReturnNewGoods(entity.isReturnNewGoods());
			model.setSeasonalWithdrawalMonth(commonService.formatMonth(entity.getSeasonalWithdrawal()));
			
			model.setFrRequired(quotationDao.getFrRequiredByQuotationId(entity.getQuotationId()));
			
			model.setIsTempIsUseFRAdmin(entity.isTempIsUseFRAdmin());
			model.setIsTempIsFRApplied(entity.getTempIsFRApplied());
			model.setIsTempIsReturnGoods(entity.getTempIsReturnGoods());
			model.setIsTempIsReturnNewGoods(entity.getTempIsReturnNewGoods());
			model.setIsLastSeasonReturnGoods(entity.isLastSeasonReturnGoods());;
			
			RUASettingEditModel ruaSettingEditModel = new RUASettingEditModel();
			
			if (entity.getQuotationId() != null){
				RUASettingEditModel row = quotationDao.selectRUASettingRowById(entity.getQuotationId());
				ruaSettingEditModel.setUnitCode(row.getUnitCode());
				ruaSettingEditModel.setUnitName(row.getUnitName());
				ruaSettingEditModel.setProductAttribute(row.getProductAttribute());
				ruaSettingEditModel.setFirmName(row.getFirmName());
				ruaSettingEditModel.setBatchCode(row.getBatchCode());
				ruaSettingEditModel.setRuaDate(row.getRuaDate());
				ruaSettingEditModel.setIsRUAAllDistrict(row.getIsRUAAllDistrict());
				if(row.getIsRUAAllDistrict() == false) {
					ruaSettingEditModel.setDistrictId(row.getDistrictId());
					District district = districtDao.getDistrictById(row.getDistrictId());
					if (district != null){
						ruaSettingEditModel.setDistrictLabel(district.getCode() + " - " + district.getChineseName());
					}
				}
			}
			
			
			Iterator<User> users = entity.getUsers().iterator();
			ArrayList<Integer> userIds = new ArrayList<Integer>();
			while (users.hasNext()) {
				userIds.add(users.next().getId());
			}
			ruaSettingEditModel.setUserId(userIds);
			
			model.setRuaSettingEditModel(ruaSettingEditModel);

		}
		return model;
	}

	public boolean vilidateOutletType(QuotationEditModel item) {
		
		return (quotationDao.validateOutletType(item.getUnitId(), item.getOutletId()) > 0);
	}
	
	/**
	 * Data Sync
	 */	
	public List<RUAQuotationSyncData> getUpdateRUAQuotation(Date lastSyncTime, List<Integer> quotationIds){
		
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = quotationIds.size() / maxSize;
		int remainder = quotationIds.size() % maxSize;
		List<QuotationSyncData> ruaQuotations = quotationDao.getUpdateRUAQuotation(lastSyncTime, null);
		
		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();
		
		// Quotient
		for(int i = 0; i < times; i++) {
			splited = quotationIds.subList(fromIdx, toIdx);
			ruaQuotations.addAll(quotationDao.getUpdateRUAQuotation(lastSyncTime, splited));
			
			if(i < (times - 1)) {
				fromIdx = toIdx;
				toIdx += maxSize;
			}
		}
		
		// Remainder
		if(times == 0) {
			if(remainder != 0) {
				splited = quotationIds.subList(fromIdx, remainder);
			}
		} else {
			if(remainder != 0) {
				splited = quotationIds.subList(toIdx, (toIdx + remainder));
			}
		}
		
		if(remainder != 0) {
			ruaQuotations.addAll(quotationDao.getUpdateRUAQuotation(lastSyncTime, splited));
		}
		
		List<RUAQuotationSyncData> data = new ArrayList<RUAQuotationSyncData>();
		HashMap<Integer, List<RUAQuotationSyncData>> syncList = new HashMap<Integer, List<RUAQuotationSyncData>>();
		for (QuotationSyncData quotation : ruaQuotations){
			RUAQuotationSyncData rua = new RUAQuotationSyncData();
			BeanUtils.copyProperties(quotation, rua);
			data.add(rua);
			if (!quotationIds.contains(quotation.getQuotationId())){
				if (syncList.containsKey(quotation.getOutletId())){
					List<RUAQuotationSyncData> list = syncList.get(quotation.getOutletId());
					list.add(rua);
				}
				else{
					List<RUAQuotationSyncData> list = new ArrayList<RUAQuotationSyncData>();
					list.add(rua);
					syncList.put(quotation.getOutletId(), list);
				}
			}
		}
		
		Set<Integer> outletIds = syncList.keySet();
		if (outletIds.size() > 0){
			List<OutletSyncData> outlets= outletDao.getUpdatedOutlets(null, outletIds.toArray(new Integer[0]));
			for (OutletSyncData outlet : outlets){
				if (syncList.containsKey(outlet.getOutletId())){
					List<RUAQuotationSyncData> list = syncList.get(outlet.getOutletId());
					for (RUAQuotationSyncData item : list){
						item.setOutlet(outlet);
					}
				}
			}			
		}		
		
		return data;
	}
	
	public List<RUAUserSyncData> getUpdateRUAUser(Date lastSyncTime){
		return quotationDao.getUpdateRUAUser(lastSyncTime);
	}
	
	public List<QuotationSyncData> getUpdatedDownloadedQuotation(Date lastSyncTime, Integer[] quotationIds, Integer[] assignmentIds){
		List<Integer> allQuotationIds = new ArrayList<Integer>();
		if(quotationIds!=null && quotationIds.length>0){
			allQuotationIds.addAll(Arrays.asList(quotationIds));
		}
		if(assignmentIds!=null && assignmentIds.length>0){
			allQuotationIds.addAll(quotationDao.getAllIdsByAssignment(assignmentIds));
		}
		allQuotationIds = new ArrayList<Integer>(new HashSet<Integer>(allQuotationIds));
		
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = allQuotationIds.size() / maxSize;
		int remainder = allQuotationIds.size() % maxSize;
		List<QuotationSyncData> updatedData = new ArrayList<QuotationSyncData>();

		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();
		
		// Quotient
		for(int i = 0; i < times; i++) {
			splited = allQuotationIds.subList(fromIdx, toIdx);
			updatedData.addAll(quotationDao.getUpdatedDownloadedQuotation(lastSyncTime, splited.toArray(new Integer[0]), null));
							
			if(i < (times - 1)) {
				fromIdx = toIdx;
				toIdx += maxSize;
			}
		}
						
		// Remainder
		if(times == 0) {
			if(remainder != 0) {
				splited = allQuotationIds.subList(fromIdx, remainder);
			}
		} else {
			if(remainder != 0) {
				splited = allQuotationIds.subList(toIdx, (toIdx + remainder));
			}
		}
					
		if(remainder != 0) {
			updatedData.addAll(quotationDao.getUpdatedDownloadedQuotation(lastSyncTime, splited.toArray(new Integer[0]), null));
		}
		
		return updatedData;
	}
	
	@Transactional
	public void setRUAQuotation(Quotation quotation){
		Date curMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
		SurveyMonth month = surveyMonthDao.getSurveyMonthByReferenceMonth(curMonth);
		if (month == null) return;
		quotation.setStatus("RUA");
		Outlet outlet = quotation.getOutlet();
		if (outlet != null){
			quotation.setDistrict(outlet.getTpu().getDistrict());
		}
		else{
			quotation.setDistrict(null);
		}
		
		quotation.setRuaDate(new Date());
		quotationRecordService.deleteQuotationRecordForRUA(month.getSurveyMonthId(), quotation.getQuotationId());
	}

	/** 
	 * Quotation datatable count 
	 */
	public Long queryQuotationRUACount(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		
		String status = "RUA";
		
		Boolean isICP = null;
		
		User user = userDao.findById(userId);
		List<Integer> districtIds = new ArrayList<Integer>();
		if (user.getDistricts().size() > 0) {
			for (District district : user.getDistricts()) {
				districtIds.add(district.getDistrictId());
			}
		}
		
		int summerStartDate = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_START_DATE).getValue());
		int summerEndDate = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_SUMMER_END_DATE).getValue());
		int winterStartDate = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_START_DATE).getValue());
		int winterEndDate = Integer.parseInt(sysConfigDao.findByName(SystemConstant.BUS_PARAM_WINTER_END_DATE).getValue());
		
		Calendar today = Calendar.getInstance();
		int todayMonth = today.get(Calendar.MONTH) + 1;
		
		boolean todayIsSummerDate = false;
		boolean todayIsWinterDate = false;
		
		if (summerStartDate == todayMonth || summerEndDate == todayMonth) {
			todayIsSummerDate = true;
		} else if (summerStartDate < summerEndDate) {
			if (summerStartDate < todayMonth && summerEndDate > todayMonth)
				todayIsSummerDate = true;
		} else if (summerStartDate > summerEndDate) {
			if (summerStartDate < todayMonth || summerEndDate > todayMonth)
				todayIsSummerDate = true;
		}
		
		if (winterStartDate == todayMonth || winterEndDate == todayMonth) {
			todayIsWinterDate = true;
		} else if (winterStartDate < winterEndDate) {
			if (winterStartDate < todayMonth && winterEndDate > todayMonth)
				todayIsWinterDate = true;
		} else if (winterStartDate > winterEndDate) {
			if (winterStartDate < todayMonth || winterEndDate > todayMonth)
				todayIsWinterDate = true;
		}
		
		return quotationDao.countQuotationRUATableList("", null, null, null,
				null, null, null, status, null, null, todayIsSummerDate, todayIsWinterDate, districtIds, userId);
	}

	/**
	 * Get cpi compilation series select format
	 */
	public Select2ResponseModel queryCpiCompilationSeriesSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<String> entities = quotationDao.searchCpiCompilationSeries(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationDao.countSearchCpiCompilationSeries(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d));
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Quotation ICP Type select format
	 */
	public Select2ResponseModel queryICPTypeSelect2(Select2RequestModel queryModel){
		queryModel.setRecordsPerPage(10);
		List<String> entities = quotationDao.searchAllICPType(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationDao.countICPType(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for(String d : entities){
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(d);
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Quotation ICP Product Code select format
	 */
	public Select2ResponseModel queryICPProductCodeSelect2(Select2RequestModel queryModel){
		queryModel.setRecordsPerPage(10);;
		List<String> entities = quotationDao.searchAllICPProductCode(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationDao.countICPProductCode(queryModel.getTerm());
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for(String d : entities){
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(d);
			item.setText(d);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Quotation select2 format
	 */
	public Select2ResponseModel queryQuotationSelect2(Select2RequestModel queryModel, Integer[] purposeId, Integer[] unitId) {
		queryModel.setRecordsPerPage(10);
		
		List<Quotation> entities = quotationDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), purposeId, unitId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationDao.countSearch(queryModel.getTerm(), purposeId, unitId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Quotation entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getId()));
			item.setText(String.valueOf(entity.getId()));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public Select2ResponseModel queryQuotationSelectWithcpiSurveyForm2(Select2RequestModel queryModel, Integer[] purposeId, Integer[] unitId, Integer[] cpiSurveyForm) {
		queryModel.setRecordsPerPage(10);
		
		List<Quotation> entities = quotationDao.searchWithcpiSurveyForm(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), purposeId, unitId, cpiSurveyForm);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationDao.countSearchWithcpiSurveyForm(queryModel.getTerm(), purposeId, unitId, cpiSurveyForm);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Quotation entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity.getId()));
			item.setText(String.valueOf(entity.getId()));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	/**
	 * Get Quotation include assignment search select2 format
	 */
	public Select2ResponseModel queryQuotationSelectAssignmentIds2(Select2RequestModel queryModel, Integer[] purposeId, Integer[] unitId, Integer[] assignmentIds) {
		queryModel.setRecordsPerPage(10);
		
		List<QuotationLookupTableList> entities = quotationDao.search(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), purposeId, unitId, assignmentIds);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationDao.countSearch(queryModel.getTerm(), purposeId, unitId, assignmentIds);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (QuotationLookupTableList entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
				item.setId(String.valueOf(entity.getId()));
				item.setText(String.valueOf(entity.getId()));
				items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	
	/**
	 * Filter season and frequency
	 */
	public List<Quotation> filterSeasonAndFrequency(List<Quotation> quotations, Date month
			, Integer summerStartMonth, Integer summerEndMonth, Integer winterStartMonth, Integer winterEndMonth) {
		List<Quotation> filtered = new ArrayList<Quotation>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(month);
		int selectedMonthInt = calendar.get(Calendar.MONTH) + 1;
		
		for (Quotation q : quotations) {
			// if freq, not in selected month, ignore
			Unit unit = q.getUnit();
			PricingFrequency freq = unit.getPricingFrequency();
			boolean isNofield = false;
			if (freq != null){
				switch (selectedMonthInt){
					case 1: 
						isNofield = !freq.isJan();
					break;
					case 2: 
						isNofield = !freq.isFeb();
					break;
					case 3: 
						isNofield = !freq.isMar();
					break;
					case 4: 
						isNofield = !freq.isApr();
					break;
					case 5: 
						isNofield = !freq.isMay();
					break;
					case 6: 
						isNofield = !freq.isJun();
					break;
					case 7: 
						isNofield = !freq.isJul();
					break;
					case 8: 
						isNofield = !freq.isAug();
					break;
					case 9: 
						isNofield = !freq.isSep();
					break;
					case 10: 
						isNofield = !freq.isOct();
					break;
					case 11: 
						isNofield = !freq.isNov();
					break;
					case 12: 
						isNofield = !freq.isDec();
					break;
				}
			}
			
			if (isNofield) continue;
			
			// if season, not in selcted month, ignore
			// check seasonality
			Integer seasonStartMonth = 0; 
			Integer seasonEndMonth = 0; 
			switch(unit.getSeasonality()){
				case 2: //case summer
					seasonStartMonth = summerStartMonth;
					seasonEndMonth = summerEndMonth;
					break;
				case 3: //case winter
					seasonStartMonth = winterStartMonth;
					seasonEndMonth = winterEndMonth;
					break;
				case 4: //case custom
					seasonStartMonth = unit.getSeasonStartMonth();
					seasonEndMonth = unit.getSeasonEndMonth();
					break;
			}
			if(unit.getSeasonality() != 1 && seasonStartMonth != 0 && seasonEndMonth != 0){
				if (seasonStartMonth <= seasonEndMonth){
					if (seasonStartMonth > selectedMonthInt || seasonEndMonth < selectedMonthInt){
						isNofield = true; // not in seasonality range
					}
				}
				else if (seasonStartMonth > seasonEndMonth){ // across year
					if (seasonStartMonth > selectedMonthInt && seasonEndMonth < selectedMonthInt){
						isNofield = true; // not in seasonality range
					}
				}
			}
			
			if (isNofield) continue;
			
			filtered.add(q);
		}
		
		return filtered;
	}
}
