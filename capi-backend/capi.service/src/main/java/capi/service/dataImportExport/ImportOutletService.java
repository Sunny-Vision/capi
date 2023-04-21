package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.java.swing.plaf.windows.WindowsBorders.DashedBorder;

import capi.dal.DistrictDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.OutletDao;
import capi.dal.TpuDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.ImportExportTask;
import capi.entity.Outlet;
import capi.entity.Tpu;
import capi.entity.VwOutletTypeShortForm;
import capi.model.GeoLocation;
import capi.service.AppConfigService;
import capi.service.CommonService;
import capi.service.masterMaintenance.OutletService;

@Service("ImportOutletService")
public class ImportOutletService extends DataImportServiceBase{

	@Autowired
	private OutletDao dao;
	
	@Autowired
	private TpuDao tpuDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private VwOutletTypeShortFormDao outletTypeDao;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
				
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		
		int rowCnt = 1;
		rows.next(); //remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String [] values = getStringValues(row, 36);
			Outlet outlet = fillEntity(values, rowCnt);
			
			dao.save(outlet);
			
			if(outlet.getId() != null){
				ids.add(outlet.getId());
			}
			
			if(rowCnt % 20==0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		List<Integer> existingOutlet = dao.getExistingOutletId();
		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existingOutlet, ids));
		
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = notExisting.size() / maxSize;
		int remainder = notExisting.size() % maxSize;
		
		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();
		
		// Quotient
		for(int i = 0; i < times; i++) {
			splited = notExisting.subList(fromIdx, toIdx);
			List<Outlet> outlets = dao.getByIds(splited);
			//Delete Outlet
			for(Outlet outlet : outlets){
				outletService.deleteOutlet(outlet.getOutletId(), configService.getFileBaseLoc());
			}
			
			if(i < (times - 1)) {
				fromIdx = toIdx;
				toIdx += maxSize;
			}
		}
		
		// Remainder
		if(times == 0) {
			if(remainder != 0) {
				splited = notExisting.subList(fromIdx, remainder);
			}
		} else {
			if(remainder != 0) {
				splited = notExisting.subList(toIdx, (toIdx + remainder));
			}
		}
		
		if(remainder != 0) {
			List<Outlet> outlets = dao.getByIds(splited);
			//Delete Outlet
			for(Outlet outlet : outlets){
				outletService.deleteOutlet(outlet.getOutletId(), configService.getFileBaseLoc());
			}
		}
		
		
		dao.flush();
		workbook.close();
	}
	
	public Outlet fillEntity(String[] values, int rowCnt) throws Exception{
		Outlet entity = null;
		int col = 0;
		try{
			String idStr = values[0];
			if(StringUtils.isEmpty(idStr)){
				entity = new Outlet();
			} else {
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: Outlet Id="+id);
				}
			}
			
			col = 1;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Outlet Code should not be empty");
			}
			int firmCode = (int)Double.parseDouble(values[col]);
			Outlet outlet = dao.getOutletbyFirmCode(firmCode);
			if(outlet != null && !outlet.getOutletId().equals(entity.getOutletId()))
				throw new RuntimeException("Outlet Code already existed: "+firmCode);
			entity.setFirmCode(firmCode);
			
//			col = 2;
//			if(StringUtils.isEmpty(values[col])){
//				throw new RuntimeException("Outlet Name should be empty");
//			}
//			String name = values[col].trim();
//			entity.setName(name);
			
			
			if(entity.getName() == null || entity.getName().equals("")) {
				entity.setName("");
			}
			
			
//			col = 4;
			col = 3;
			if(!StringUtils.isEmpty(values[col])){
				String brCode = values[col].trim();
				entity.setBrCode(brCode);
			} else {
				entity.setBrCode(null);
			}
			
//			col = 9;
			col = 8;
			if(!StringUtils.isEmpty(values[col])){
				int outletMarketType = (int)Double.parseDouble(values[col]);
				if(!(outletMarketType==1 || outletMarketType==2)){
					throw new RuntimeException("Market / Non-market must be 1 or 2");
				}
				entity.setOutletMarketType(outletMarketType);
			} else {
				entity.setOutletMarketType(null);
			}
			
//			col = 10;
			col = 9;
			if(!StringUtils.isEmpty(values[col])){
				String indoorMarketName = values[col].trim();
				entity.setIndoorMarketName(indoorMarketName);
			} else {
				entity.setIndoorMarketName(null);
			}
			
//			col = 11;
			col = 10;
			if(!StringUtils.isEmpty(values[col])){
				int collectionMethod = (int)Double.parseDouble(values[col]);
				if(collectionMethod<1 || collectionMethod>4){
					throw new RuntimeException("Collection Method cannot more than 4 or less than 1");
				}
				entity.setCollectionMethod(collectionMethod);
			} else {
				entity.setCollectionMethod(null);
			}
			
//			col = 12;
			col = 11;
			if(!StringUtils.isEmpty(values[col])){
				String detailAddress = values[col];
				entity.setDetailAddress(detailAddress);
			} else {
				entity.setDetailAddress(null);
			}
			
//			col = 13;
			col = 12;
			if(StringUtils.isEmpty(values[col])){
				entity.setStreetAddress(null);
			} else {
				String streetAddress = values[col];
				entity.setStreetAddress(streetAddress);
			}
			
//			col = 16;
			col = 15;
			String marketName = values[col];
			entity.setMarketName(marketName);
			
//			col = 17;
			col = 16;
			if(!StringUtils.isEmpty(values[col])){
				String mainContact = values[col];
				entity.setMainContact(mainContact);
			} else {
				entity.setMainContact(null);
			}
			
//			col = 18;
			col = 17;
			if(!StringUtils.isEmpty(values[col])){
				String lastContact = values[col];
				entity.setLastContact(lastContact);
			} else {
				entity.setLastContact(null);
			}
			
//			col = 19;
			col = 18;
			if(!StringUtils.isEmpty(values[col])){
				String tel = values[col].trim();
				entity.setTel(tel);
			} else {
				entity.setTel(null);
			}
			
//			col = 20;
			col = 19;
			if(!StringUtils.isEmpty(values[col])){
				String fax = values[col].trim();
				entity.setFax(fax);
			} else {
				entity.setFax(null);
			}
			
//			col = 21;
			col = 20;
			if(!StringUtils.isEmpty(values[col])){
				String webSite = values[col];
				entity.setWebSite(webSite);
			} else {
				entity.setWebSite(null);
			}
			
//			col = 22;
			col = 21;
			if(!StringUtils.isEmpty(values[col])){
				String openStartTime = values[col].trim();
				if(!(openStartTime.matches("\\d{2}:\\d{2}"))){
					throw new RuntimeException("Opening Start Time should be hh:mm");
				}
				Date openingStartTime = commonService.getTime(openStartTime);
				entity.setOpeningStartTime(openingStartTime);
			} else {
				entity.setOpeningStartTime(null);
			}
			
//			col = 23;
			col = 22;
			if(!StringUtils.isEmpty(values[col])){
				String openEndTime = values[col].trim();
				if(!(openEndTime.matches("\\d{2}:\\d{2}"))){
					throw new RuntimeException("Opening End Time should be hh:mm");
				}
				Date openingEndTime = commonService.getTime(openEndTime);
				entity.setOpeningEndTime(openingEndTime);
			} else {
				entity.setOpeningEndTime(null);
			}
			
//			col = 24;
			col = 23;
			if(!StringUtils.isEmpty(values[col])){
				String openStartTime2 = values[col].trim();
				if(!(openStartTime2.matches("\\d{2}:\\d{2}"))){
					throw new RuntimeException("Opening Start Time 2 should be hh:mm");
				}
				Date openingStartTime2 = commonService.getTime(openStartTime2);
				entity.setOpeningStartTime2(openingStartTime2);
			} else {
				entity.setOpeningStartTime2(null);
			}
			
//			col = 25;
			col = 24;
			if(!StringUtils.isEmpty(values[col])){
				String openEndTime2 = values[col].trim();
				if(!(openEndTime2.matches("\\d{2}:\\d{2}"))){
					throw new RuntimeException("Opening End Time 2 should be hh:mm");
				}
				Date openingEndTime2 = commonService.getTime(openEndTime2);
				entity.setOpeningEndTime2(openingEndTime2);
			} else {
				entity.setOpeningEndTime2(null);
			}
			
//			col = 26;
			col = 25;
			if(!StringUtils.isEmpty(values[col])){
				String convenStartTime = values[col].trim();
				if(!(convenStartTime.matches("\\d{2}:\\d{2}"))){
					throw new RuntimeException("Convenient Start Time should be hh:mm");
				}
				Date convenientStartTime = commonService.getTime(convenStartTime);
				entity.setConvenientStartTime(convenientStartTime);
			} else {
				entity.setConvenientStartTime(null);
			}
			
//			col = 27;
			col = 26;
			if(!StringUtils.isEmpty(values[col])){
				String convenEndTime = values[col].trim();
				if(!(convenEndTime.matches("\\d{2}:\\d{2}"))){
					throw new RuntimeException("Convenient End Time should be hh:mm");
				}
				Date convenientEndTime = commonService.getTime(convenEndTime);
				entity.setConvenientEndTime(convenientEndTime);
			} else {
				entity.setConvenientEndTime(null);
			}
			
//			col = 28;
			col = 27;
			if(!StringUtils.isEmpty(values[col])){
				String convenStartTime2 = values[col].trim();
				if(!(convenStartTime2.matches("\\d{2}:\\d{2}"))){
					throw new RuntimeException("Convenient Start Time 2 should be hh:mm");
				}
				Date convenientStartTime2 = commonService.getTime(convenStartTime2);
				entity.setConvenientStartTime2(convenientStartTime2);
			} else {
				entity.setConvenientStartTime2(null);
			}
			
//			col = 29;
			col = 28;
			if(!StringUtils.isEmpty(values[col])){
				String convenEndTime2 = values[col].trim();
				if(!(convenEndTime2.matches("\\d{2}:\\d{2}"))){
					throw new RuntimeException("Convenient End Time 2 should be hh:mm");
				}
				Date convenientEndTime2 = commonService.getTime(convenEndTime2);
				entity.setConvenientEndTime2(convenientEndTime2);
			} else {
				entity.setConvenientEndTime2(null);
			}
			
//			col = 30;
			col = 29;
			if(!StringUtils.isEmpty(values[col])){
				String remark = values[col];
				entity.setRemark(remark);
			} else {
				entity.setRemark(null);
			}
			
//			col = 31;
			col = 30;
			if(!StringUtils.isEmpty(values[col])){
				String discountRemark = values[col];
				entity.setDiscountRemark(discountRemark);
			} else {
				entity.setDiscountRemark(null);
			}
			
//			col = 32;
			col = 31;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Use FR(Admin) cannot be empty");
			}
			boolean isUseFRAdmin = Boolean.parseBoolean(values[col]);
			entity.setUseFRAdmin(isUseFRAdmin);
			
//			col = 33;
			col = 32;
			if(!StringUtils.isEmpty(values[col])){
				String status = values[col].trim();
				if(!("Valid".equals(status) || "Invalid".equals(status))){
					throw new RuntimeException("Status must be Valid or Invalid");
				}
				entity.setStatus(status);
			} else {
				entity.setStatus(null);
			}
			
			//For Tpu and District (5-9)
//			col = 8;
			col = 7;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Tpu Code cannot be empty");
			}
			String tpuCode = values[col].trim();
			Tpu tpu = tpuDao.getTpuByCode(tpuCode);
			if(tpu==null){
				throw new RuntimeException("Tpu not found: Tpu Code="+tpuCode);
			}
			
//			col = 5;
//			if(!StringUtils.isEmpty(values[5])){
//				String districtCode = values[5].trim();
//				District district = districtDao.getDistrictByCode(districtCode);
//				if(district==null){
//					throw new RuntimeException("District not found: District Code="+districtCode);
//				}
//				tpu.setDistrict(district);
//			} else {
//				tpu.setDistrict(null);
//			}
			
			entity.setTpu(tpu);
			
			//For Outlet Type
//			col = 3;
			col = 2;
//			if(StringUtils.isEmpty(values[3])){
//				throw new RuntimeException("OutletType cannot be empty");
//			}
			updateOutletType(values[col], entity);
			
			//For Latitude
//			col = 14;
			col = 13;
			if(StringUtils.isEmpty(values[col]) || StringUtils.isEmpty(values[col+1])) {
				if(entity.getStreetAddress()!=null){
					try{
						GeoLocation loc = outletService.geocode(entity.getStreetAddress());
						entity.setLatitude(loc.getLatitude());
						entity.setLongitude(loc.getLongitude());
					} catch (Exception e){
						entity.setLatitude(null);
						entity.setLongitude(null);
					}
				}
			} else {
//				col = 14;
				col = 13;
				String latitude = values[col];
				entity.setLatitude(latitude);
				
//				col = 15;
				col = 14;
				String longitude = values[col];
				entity.setLongitude(longitude);
			}
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}
	
	private void updateOutletType(String codes, Outlet outlet){
		List<String> newOutletTypeIds = new ArrayList<String>();
		if (!StringUtils.isEmpty(codes)){
			newOutletTypeIds = Arrays.asList(codes.split("\\s*;\\s*"));
		}
		
		ArrayList<String> oldOutletTypeIds = new ArrayList<String>();
		for(VwOutletTypeShortForm outletType : outlet.getOutletTypes()){
			oldOutletTypeIds.add(outletType.getId());
		}
			
		Collection<String> deletedIds = (Collection<String>)CollectionUtils.subtract(oldOutletTypeIds, newOutletTypeIds);
		Collection<String> newIds = (Collection<String>)CollectionUtils.subtract(newOutletTypeIds, oldOutletTypeIds);
		
		if(deletedIds.size()> 0){
			List<VwOutletTypeShortForm> deletedOutletTypes = outletTypeDao.getByIds(deletedIds.toArray(new String[0]));
			for(VwOutletTypeShortForm outletType : deletedOutletTypes){
				outlet.getOutletTypes().remove(outletType);
			}
		}
			
		if(newIds.size() > 0){
			List<VwOutletTypeShortForm> newOutletTypes = outletTypeDao.getByIds(newIds.toArray(new String[0]));
			outlet.getOutletTypes().addAll(newOutletTypes);
		}
		
	}
	
}
