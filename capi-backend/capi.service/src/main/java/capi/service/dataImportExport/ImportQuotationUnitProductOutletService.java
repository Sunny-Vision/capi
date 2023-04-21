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

import capi.dal.BatchDao;
import capi.dal.DistrictDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.OutletDao;
import capi.dal.ProductDao;
import capi.dal.QuotationDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.Batch;
import capi.entity.District;
import capi.entity.ImportExportTask;
import capi.entity.Outlet;
import capi.entity.Product;
import capi.entity.Quotation;
import capi.entity.Unit;
import capi.entity.User;
import capi.service.CommonService;

@Service("ImportQuotationUnitProductOutletService")
public class ImportQuotationUnitProductOutletService extends DataImportServiceBase{

	@Autowired
	private QuotationDao dao;
	
	@Autowired
	private UnitDao unitDao;
	
	@Autowired
	private BatchDao batchDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 21;
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
		rows.next(); // remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String[] values = getStringValues(row, 26);
			
			//For Quotation
			Quotation quotation = fillEntity(values, rowCnt);
			dao.save(quotation);
			
			if(quotation.getQuotationId()!=null){
				ids.add(quotation.getQuotationId());
			}
			
			if(rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
			
		}
		
//		// Delete
//		List<Integer> existing = dao.getExistingQuotationIds();
//		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existing, ids));
//		
//		deleteEntities(notExisting, dao);
		
		dao.flush();
		
		workbook.close();
	}
	
	private Quotation fillEntity(String[] values, int rowCnt){
		Quotation entity = null;
		
		int col = 0;
		try{
			//For Quotation
			String idStr = values[col];
			if(StringUtils.isEmpty(idStr)){
//				throw new RuntimeException("Quotation Id should not be empty");
				entity = new Quotation();
			} else {
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity==null){
					throw new RuntimeException("Record not find: Quotation Id ="+id);
				}
			}
			
			col = 2;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Variety Code should not be empty");
			}
			String unitCode = values[col].trim();
			
			col = 3;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("CPI Base Period should not be empty");
			}
			String cpiBasePeriod = values[col].trim();
			
			Unit unit = unitDao.getUnitByCode(unitCode, cpiBasePeriod);
			if(unit == null){
				throw new RuntimeException("Variety not found: Variety Code = "+unitCode+" CPI Base Period = "+cpiBasePeriod);
			}
			entity.setUnit(unit);
			
			col = 6;
			if(!StringUtils.isEmpty(values[col])){
				int firmCode = (int) Double.parseDouble(values[col]);
				Outlet outlet = outletDao.getOutletbyFirmCode(firmCode);
				
				if(outlet == null){
					throw new RuntimeException("Outlet not found: Outlet Code = "+firmCode);
				}
				
				if(!(dao.validateOutletType(unit.getUnitId(), outlet.getOutletId()) > 0)){
					throw new RuntimeException("The outlet type of the quotation has not been defined in the outlet");
				}
				entity.setOutlet(outlet);
			} else {
				entity.setOutlet(null);
			}
			
//			col = 8;
			col = 7;
			if(!StringUtils.isEmpty(values[col])){
				int productId = (int)Double.parseDouble(values[col]);
				Product product = productDao.findById(productId);
				if(product == null){
					throw new RuntimeException("Product not found product Id="+productId);
				}
				if(unit.getProductCategory().getId()!=product.getProductGroup().getId()){
					throw new RuntimeException("Unit Product Group and Product Product Group not Same");
				}
				entity.setProduct(product);
			} else {
				entity.setProduct(null);
			}
			
//			col = 9;
			col = 8;
			if(!StringUtils.isEmpty(values[col])){
				String date = values[col].trim();
				if(!(date.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("Last Product Change Date Format should be DD-MM-YYYY");
				}
				Date lastProductChangeDate = commonService.getDate(date);
				entity.setLastProductChangeDate(lastProductChangeDate);
			} else {
				entity.setLastProductChangeDate(null);
			}

//			col = 10;
			col = 9;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Batch Code should not be empty");
			}
			String batchCode = values[col].trim();
			Batch batch = batchDao.getBatchByCode(batchCode);
			if(batch == null){
				throw new RuntimeException("Batch not found: Batch Code = "+batchCode);
			}
			entity.setBatch(batch);
			
//			col = 11;
			col = 10;
			if(!StringUtils.isEmpty(values[col])){
				String cpiCompilationSeries = values[col].trim();
				entity.setCpiCompilationSeries(cpiCompilationSeries);
			} else {
				entity.setCpiCompilationSeries(null);
			}
			
//			col = 12;
			col = 11;
			if(!StringUtils.isEmpty(values[col])){
				String formType = values[col].trim();
				entity.setFormType(formType);
			} else {
				entity.setFormType(null);
			}
			
//			col = 13;
			col = 12;
			if(!StringUtils.isEmpty(values[col])){
				Double quotationLoading = Double.parseDouble(values[col]);
				entity.setQuotationLoading(quotationLoading);
			} else {
				entity.setQuotationLoading(null);
			}
			
//			col = 14;
			col = 13;
			String isICP = values[col].trim();
			if(StringUtils.isEmpty(values[col]))
				throw new RuntimeException("Is ICP cannot be empty");
			entity.setICP(Boolean.parseBoolean(isICP));
			
//			col = 15;
			col = 14;
			if(!StringUtils.isEmpty(values[col])){
				String icpProductCode = values[col].trim();
				entity.setIcpProductCode(icpProductCode);
			} else {
				entity.setIcpProductCode(null);
			}
			
//			col = 16;
			col = 15;
			if(!StringUtils.isEmpty(values[col])){
				String icpProductName = values[col].trim();
				entity.setIcpProductName(icpProductName);
			} else {
				entity.setIcpProductName(null);
			}
			
//			col = 17;
			col = 16;
			if(!StringUtils.isEmpty(values[col])){
				String icpType = values[col].trim();
				entity.setIcpType(icpType);
			} else {
				entity.setIcpType(null);
			}
			
//			col = 18;
			col = 17;
			if(!StringUtils.isEmpty(values[col])){
				String oldFormBarSerial = values[col].trim();
				entity.setOldFormBarSerial(oldFormBarSerial);
			} else {
				entity.setOldFormBarSerial(null);
			}
			
//			col = 19;
			col = 18;
			if(!StringUtils.isEmpty(values[col])){
				String oldFormSequence = values[col].trim();
				entity.setOldFormSequence(oldFormSequence);
			} else {
				entity.setOldFormSequence(null);
			}
			
//			col = 20;
			col = 19;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Status should not be empty");
			}
			String status = values[col].trim();
			if(!(status.equals("Active") || status.equals("Inactive") || status.equals("RUA"))){
				throw new RuntimeException("Status only Active or Inactive or RUA");
			}
			entity.setStatus(status);
			
//			col = 21;
			col = 20;
			if(!StringUtils.isEmpty(values[col])){
				int keepNoMonth = (int)Double.parseDouble(values[col]);
				entity.setKeepNoMonth(keepNoMonth);
			} else {
				entity.setKeepNoMonth(null);
			}
			
//			col = 22;
			col = 21;
			if(!StringUtils.isEmpty(values[col])){
				String ruaDate = values[col].trim();
				if(!(ruaDate.matches("\\d{2}-\\d{2}-\\d{4}"))){
					throw new RuntimeException("RUA Date Format should be DD-MM-YYYY");
				}
								
				Date date = commonService.getDate(ruaDate);
				
				if(date != null){
					entity.setRuaDate(date);
				}
			} else {
				entity.setRuaDate(null);
			}
			
//			col = 23;
			col = 22;
			String isRUAAllDistrict = values[col].trim();
			entity.setRUAAllDistrict(Boolean.parseBoolean(isRUAAllDistrict));

//			col = 24;
			col = 23;
			if(status.equals("RUA") && !Boolean.parseBoolean(isRUAAllDistrict)){
				if(StringUtils.isEmpty(values[col]) && StringUtils.isEmpty(values[col+1])){
					throw new RuntimeException("District and User cannot be empty when RUAAllDistrict is FALSE");
				}
			}
			
			if(status.equals("RUA") && StringUtils.isNotEmpty(values[col])){
				String districtCode = values[col].trim();
				
				District district = districtDao.getDistrictByCode(districtCode);
				if(district==null){
					throw new RuntimeException("District not found District Code="+districtCode);
				}
				entity.setDistrict(district);
			} else {
				entity.setDistrict(null);
			}
			
//			col = 25;
			col = 24;
			if(status.equals("RUA") && StringUtils.isNotEmpty(values[col])){
				updateUser(values[col].trim(), entity);
			} else {
				entity.getUsers().clear();
			}
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage()+" (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}
	
	private void updateUser(String codes, Quotation quotation){
		List<String> newStaffCodes = new ArrayList<String>();
		if (!StringUtils.isEmpty(codes)){
			newStaffCodes = Arrays.asList(codes.split("\\s*;\\s*"));
		}
		
		ArrayList<String> oldStaffCodes = new ArrayList<String>();
		for(User user : quotation.getUsers()){
			oldStaffCodes.add(user.getStaffCode());
		}
			
		Collection<String> deletedIds = (Collection<String>)CollectionUtils.subtract(oldStaffCodes, newStaffCodes);
		Collection<String> newIds = (Collection<String>)CollectionUtils.subtract(newStaffCodes, oldStaffCodes);
		
		if(deletedIds.size()> 0){
			List<User> deleteUsers = userDao.getUserByStaffCodes(new ArrayList<String>(deletedIds));
			for(User user : deleteUsers){
				quotation.getUsers().remove(user);
			}
		}
			
		if(newIds.size() > 0){
			List<User> newUsers = userDao.getUserByStaffCodes(new ArrayList<String>(newIds));
			quotation.getUsers().addAll(newUsers);
		}
	}
}
