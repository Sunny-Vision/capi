package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

@Service("ImportQuotationCreationService")
public class ImportQuotationCreationService extends DataImportServiceBase{
	
	@Autowired
	private QuotationDao dao;
	
	@Autowired
	private UnitDao unitDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private BatchDao batchDao;
	
	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 30;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();		
			String [] values = getStringValues(row, 22);
			
			//For Quotation
			Quotation quotation = fillEntity(values, rowCnt);
			dao.save(quotation);
			
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		dao.flush();
		
		workbook.close();
	}
	
	private Quotation fillEntity(String[] values, int rowCnt) throws Exception{
		Quotation entity = new Quotation();
		int col = 0;
		
		try{
			//For Quotation
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Variety Code could not be empty");
			}
			String unitCode = values[col].trim();
			
			col = 1;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("CPI Base Period could not be empty");
			}
			String cpiBasePeriod = values[col].trim();
			
			Unit unit = unitDao.getUnitByCode(unitCode, cpiBasePeriod);
			if(unit == null){
				throw new RuntimeException("Variety not found: Variety Code ="+unitCode+", CPI Base Period ="+cpiBasePeriod);
			}
			entity.setUnit(unit);
			
			col = 2;
			if(!StringUtils.isEmpty(values[col])){
				int productId = (int)Double.parseDouble(values[col]);
				Product product = productDao.findById(productId);
				if(product == null){
					throw new RuntimeException("Product not found product Id="+productId);
				}
				if(!unit.getProductCategory().getId().equals(product.getProductGroup().getId())){
					throw new RuntimeException("Unit Product Group and Product Product Group not Same");
				}
				entity.setProduct(product);
			} else {
				entity.setProduct(null);
			}
			
			col = 3;
			if(!StringUtils.isEmpty(values[col])){
				int firmCode = (int)Double.parseDouble(values[col]);
				Outlet outlet = outletDao.getOutletbyFirmCode(firmCode);
				if(outlet == null){
					throw new RuntimeException("Outlet not found Outlet Firm Code="+firmCode);
				}
				if(!(dao.validateOutletType(unit.getUnitId(), outlet.getOutletId()) > 0)){
					throw new RuntimeException("The outlet type of the quotation has not been defined in the outlet");
				}
				entity.setOutlet(outlet);
			} else {
				entity.setOutlet(null);
			}
			
			col = 4;
			if (StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Status should not be empty");
			}
			String status = values[col].trim();
			if(!(status.equals("Active") || status.equals("Inactive") || status.equals("RUA"))){
				throw new RuntimeException("Status only Active or Inactive or RUA");
			}
			entity.setStatus(status);
			
			col = 5;
			if(!StringUtils.isEmpty(values[col])){
				Double quotationLoading = Double.parseDouble(values[col]);
				entity.setQuotationLoading(quotationLoading);
			} else {
				entity.setQuotationLoading(null);
			}
			
			col = 6;
			if(!StringUtils.isEmpty(values[col])){
				String indoorAllocationCode = values[col];
				entity.setIndoorAllocationCode(indoorAllocationCode);
			} else {
				entity.setIndoorAllocationCode(null);
			}
			
			col = 7;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("ICP should not be empty");
			}
			boolean isICP = Boolean.parseBoolean(values[col]);
			entity.setICP(isICP);
			
			col = 8;
			if(!StringUtils.isEmpty(values[col])){
				String cpiCompilationSeries = values[col];
				entity.setCpiCompilationSeries(cpiCompilationSeries);
			} else {
				entity.setCpiCompilationSeries(null);
			}
			
			col = 9;
			if(!StringUtils.isEmpty(values[col])){
				String oldFormBarSerial = values[col];
				entity.setOldFormBarSerial(oldFormBarSerial);
			} else {
				entity.setOldFormBarSerial(null);
			}
			
			col = 10;
			if(!StringUtils.isEmpty(values[col])){
				String oldFormSequence = values[col];
				entity.setOldFormSequence(oldFormSequence);
			} else {
				entity.setOldFormSequence(null);
			}
			
			col = 11;
			if(!StringUtils.isEmpty(values[col])){
				String icpProductCode = values[col];
				entity.setIcpProductCode(icpProductCode);
			} else {
				entity.setIcpProductCode(null);
			}
						
			col = 12;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("FR Applied should not be empty");
			} 
			boolean isFRApplied = Boolean.parseBoolean(values[col]);
			entity.setFRApplied(isFRApplied);
			
			col = 13;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Return Goods should not be empty");
			}
			boolean isReturnGoods = Boolean.parseBoolean(values[col]);
			entity.setReturnGoods(isReturnGoods);
			
			col = 14;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Return New Goods should not be empty");
			}
			boolean isReturnNewGoods = Boolean.parseBoolean(values[col]);
			entity.setReturnNewGoods(isReturnNewGoods);
			
			//For Batch
			col = 15;
			if(StringUtils.isEmpty(values[col])){
				throw new RuntimeException("Batch should not be empty");
			}
			String batchCode = values[col].trim();
			Batch batch = batchDao.getBatchByCode(batchCode);
			if(batch == null){
				throw new RuntimeException("Batch not found Batch Code="+batchCode);
			}
			entity.setBatch(batch);
			
			col = 16;
			if(!StringUtils.isEmpty(values[col])){
				String formType = values[col].trim();
				entity.setFormType(formType);
			} else {
				entity.setFormType(null);
			}
			
			col = 17;
			if(!StringUtils.isEmpty(values[col])){
				String icpProductName = values[col].trim();
				entity.setIcpProductName(icpProductName);
			} else {
				entity.setIcpProductName(null);
			}
			
			col = 18;
			if(!StringUtils.isEmpty(values[col])){
				String icpType = values[col].trim();
				entity.setIcpType(icpType);
			} else {
				entity.setIcpType(null);
			}
			
			col = 19;
			String isRUAAllDistrict = values[col].trim();
			entity.setRUAAllDistrict(Boolean.parseBoolean(isRUAAllDistrict));
			
			col = 20;
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
			
			col = 21;
			if(status.equals("RUA") && StringUtils.isNotEmpty(values[col])){
				updateUser(values[col].trim(), entity);
			} else {
				entity.getUsers().clear();
			}
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
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
