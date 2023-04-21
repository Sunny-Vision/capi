package capi.service.dataImportExport;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.QuotationRecordDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.ImportExportTask;
import capi.entity.QuotationRecord;
import capi.entity.User;
import capi.service.CommonService;

@Service("ImportAssignmentAllocationService")
public class ImportAssignmentAllocationService extends DataImportServiceBase{

	@Autowired
	private AssignmentDao dao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 27;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while(rows.hasNext()){
			Row row = rows.next();
			String[] values = getStringValues(row, 20);
			
			//For Assignment
			Assignment assignment = fillEntity(values, rowCnt);
			dao.save(assignment);
			
			if(rowCnt%20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
		
		dao.flush();
		
		workbook.close();
		
		
		
	}
	
	private Assignment fillEntity(String[] values, int rowCnt) throws Exception{
		
		Assignment entity = null;
		int col = 0;
		try{
			//For Assignment
			String idStr = values[0];
			if(StringUtils.isEmpty(idStr)){
				throw new RuntimeException("Assignment Id should not be empty");
			}
			int id = (int)Double.parseDouble(idStr);
			entity = dao.findById(id);
			if(entity == null){
				throw new RuntimeException("Record not  found: Assignment id="+id);
			}
			
			col = 4;
			if(!StringUtils.isEmpty(values[4])){
				String date = values[4].trim();
				entity.setStartDate(commonService.getDate(date));
			} else {
				entity.setStartDate(null);
			}
			
			col = 5;
			if(!StringUtils.isEmpty(values[5])){
				String date = values[5].trim();
				entity.setEndDate(commonService.getDate(date));
			} else {
				entity.setEndDate(null);
			}
			
			col = 6;
			if(!StringUtils.isEmpty(values[6])){
				String date = values[6].trim();
				entity.setAssignedCollectionDate(commonService.getDate(date));
			} else {
				entity.setAssignedCollectionDate(null);
			}
			
//			col = 19;
			col = 18;
			User newUser = null;
			if(!StringUtils.isEmpty(values[col])){
				String staffCode = values[col].trim();
				newUser = userDao.getUserByStaffCode(staffCode);
			}
			
			entity.setUser(newUser);
			
			//For QuotationRecord
			List<QuotationRecord> QuotationRecords = quotationRecordDao.getUndoQuotationRecordsByAssignmentId(id);
			for(QuotationRecord quotationRecord : QuotationRecords) {
				quotationRecord.setUser(newUser);
				
				if(entity.getAssignedCollectionDate() != null){
					quotationRecord.setReferenceDate(entity.getAssignedCollectionDate());
					quotationRecord.setHistoryDate(entity.getAssignedCollectionDate());
				} else {
					quotationRecord.setReferenceDate(entity.getStartDate());
					quotationRecord.setHistoryDate(entity.getStartDate());
				}
				quotationRecord.setAssignedCollectionDate(entity.getAssignedCollectionDate());
				quotationRecord.setAssignedStartDate(entity.getStartDate());
				quotationRecord.setAssignedEndDate(entity.getEndDate());
				
				quotationRecord.setModifiedDate(quotationRecord.getModifiedDate());
				quotationRecord.setByPassModifiedDate(true);
				quotationRecordDao.save(quotationRecord);
				for(QuotationRecord otherQuotationRecord : quotationRecord.getOtherQuotationRecords()) {
					otherQuotationRecord.setUser(newUser);
					otherQuotationRecord.setModifiedDate(otherQuotationRecord.getModifiedDate());
					otherQuotationRecord.setByPassModifiedDate(true);
					quotationRecordDao.save(otherQuotationRecord);
				}
			}
			
			entity.setModifiedDate(entity.getModifiedDate());
			entity.setByPassModifiedDate(true);
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
		
	}
	
}
