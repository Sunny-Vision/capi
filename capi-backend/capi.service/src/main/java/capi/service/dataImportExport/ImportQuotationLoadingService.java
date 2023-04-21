package capi.service.dataImportExport;

import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.DistrictDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.QuotationLoadingDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.ImportExportTask;
import capi.entity.QuotationLoading;

@Service("ImportQuotationLoadingService")
public class ImportQuotationLoadingService extends DataImportServiceBase{
	
	@Autowired
	private QuotationLoadingDao dao;
	
	@Autowired
	private DistrictDao districtdao;
	
	@Autowired
	private VwOutletTypeShortFormDao outletTypeDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 23;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
//		List<Integer> ids = new ArrayList<Integer>();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String[] values = getStringValues(row, 4);
			
			//For QuotationLoading
			QuotationLoading quotationLoading = fillEntity(values, rowCnt);
			dao.save(quotationLoading);
//			if(quotationLoading.getId()!=null){
//				ids.add(quotationLoading.getId());
//			}
			
			//For OutletTypeQuotationLoading
			
			
			if(rowCnt%20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
//		List<QuotationLoading> quotationLoadings = dao.getNotExistedQuotationLoading(ids);
//		for(QuotationLoading quotationLoading : quotationLoadings){
//			quotationLoading.getOutletTypes().clear();
//			dao.delete(quotationLoading);
//		}
		
//		List<Integer> existingQuotationLoading = dao.getExistingQuotationLoadingId();
//		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existingQuotationLoading, ids));
//		
//		int fromIdx = 0;
//		int toIdx = 0;
//		int maxSize = 2000;
//		int times = notExisting.size() / maxSize;
//		int remainder = notExisting.size() % maxSize;
//		
//		toIdx = maxSize;
//		List<Integer> splited = new ArrayList<Integer>();
//		
//		// Quotient
//		for(int i = 0; i < times; i++) {
//			splited = notExisting.subList(fromIdx, toIdx);
//			List<QuotationLoading> quotationLoadings = dao.getByIds(splited);
//			//Delete QuotationLoading
//			for(QuotationLoading quotationLoading : quotationLoadings){
//				dao.delete(quotationLoading);
//			}
//			
//			if(i < (times - 1)) {
//				fromIdx = toIdx;
//				toIdx += maxSize;
//			}
//		}
//		
//		// Remainder
//		if(times == 0) {
//			if(remainder != 0) {
//				splited = notExisting.subList(fromIdx, remainder);
//			}
//		} else {
//			if(remainder != 0) {
//				splited = notExisting.subList(toIdx, (toIdx + remainder));
//			}
//		}
//		
//		List<QuotationLoading> quotationLoadings = dao.getByIds(splited);
//		//Delete QuotationLoading
//		for(QuotationLoading quotationLoading : quotationLoadings){
//			dao.delete(quotationLoading);
//		}
		
		dao.flush();
		
		workbook.close();
	}
	
	private QuotationLoading fillEntity(String[] values, int rowCnt) throws Exception{
		QuotationLoading entity = null;
//		District district = null;
				
		int col = 0;
		try{
			//For QuotationLoading
			String idStr = values[0];
			if(StringUtils.isEmpty(idStr)){
//				entity = new QuotationLoading();
				throw new RuntimeException("Quotation Loading Id should not be empty");
			} else {
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: Quotation Loading Id="+id);
				}
			}
			
			col = 3;
			if(StringUtils.isEmpty(values[3])){
				throw new RuntimeException("Quotation Per Man Day should not be empty");
			}
			Double quotationPerManDay = Double.parseDouble(values[3]);
			entity.setQuotationPerManDay(quotationPerManDay);
			
//			//For District
//			col = 2;
//			if(StringUtils.isEmpty(values[2])){
//				throw new RuntimeException("District Code should not be empty");
//			}
//			String districtCode = values[2].trim();
//			
//			district = districtdao.getDistrictByCode(districtCode);
//			if(district == null){
//				throw new RuntimeException("District Code not found Code="+districtCode);
//			}
//			entity.setDistrict(district);
//			
//			//For OutletTypeQuotationLoading
//			col = 3;
//			if(StringUtils.isEmpty(values[3])){
//				throw new RuntimeException("Outlet Type should not be empty");
//			}
//			
//			String[] shortCode = {values[3]};
//			
//			List<VwOutletTypeShortForm> outletTypes = outletTypeDao.getByIds(shortCode);
//			if(outletTypes.isEmpty()){
//				throw new RuntimeException("Outlet Type not find="+values[3]);
//			}
//			entity.getOutletTypes().clear();
//			entity.getOutletTypes().addAll(outletTypes);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage()+" (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}

}
