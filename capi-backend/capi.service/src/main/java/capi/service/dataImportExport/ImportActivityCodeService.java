package capi.service.dataImportExport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import capi.dal.ActivityCodeDao;
import capi.dal.ImportExportTaskDao;
import capi.entity.ActivityCode;
import capi.entity.ImportExportTask;

@Service("ImportActivityCodeService")
public class ImportActivityCodeService extends DataImportServiceBase{

	@Autowired
	private ActivityCodeDao dao;
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		
		XSSFWorkbook workbook = this.getWorkbook(task.getFilePath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		List<Integer> ids = new ArrayList<Integer>();
		
		int rowCnt = 1;
		rows.next(); // remove column header
		while (rows.hasNext()){
			Row row = rows.next();			
			ActivityCode code = fillEntity(row, rowCnt);
			dao.save(code);
			if (code.getActivityCodeId() != null){
				ids.add(code.getActivityCodeId());
			}			
			if (rowCnt % 20 == 0){
				dao.flushAndClearCache();
			}
			rowCnt++;
		}
//		List<ActivityCode> codes = dao.getNotExistedActivityCode(ids);
//		for (ActivityCode code : codes){
//			dao.delete(code);
//		}
		
		List<Integer> existingActivityCode = dao.getExistingActivityCodeId();
		List<Integer> notExisting = new ArrayList<Integer>(CollectionUtils.subtract(existingActivityCode, ids));
		
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
			List<ActivityCode> codes = dao.getByIds(splited);
			for (ActivityCode code : codes){
				dao.delete(code);
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
		
		List<ActivityCode> codes = dao.getNotExistedActivityCode(splited);
		for (ActivityCode code : codes){
			dao.delete(code);
		}
		
		dao.flush();
		workbook.close();
	}
	
	private ActivityCode fillEntity(Row row, int rowCnt) throws Exception{
		ActivityCode entity = null;
		try{
			String [] values = getStringValues(row, 4);
			String idStr = values[0];
			if (StringUtils.isEmpty(idStr)){
				entity = new ActivityCode();
			}
			else{
				int id = (int)Double.parseDouble(idStr);
				entity = dao.findById(id);
				if(entity == null){
					throw new RuntimeException("Record not found: id="+id);
				}
			}
			
			String code = values[1].trim();
			if (StringUtils.isEmpty(code)){
				throw new RuntimeException("Code cannot be empty: row no.="+rowCnt);
			}
			
			ActivityCode activityCode = dao.getActivityCodeByCode(code);
			if (entity.getActivityCodeId() == null && activityCode != null){
				throw new RuntimeException("Activity Code already existed: "+code);
			}
			entity.setCode(code);
			
			String desc = values[2];
			entity.setDescription(desc);
			
			String manDayStr = values[3];
			if (StringUtils.isEmpty(manDayStr)){
				throw new RuntimeException("Man Day cannot be empty: row no.="+rowCnt);
			}
			Double manDay = Double.parseDouble(manDayStr);
			entity.setManDay(manDay);
		}
		catch(Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+")");
		}
		return entity;
	}

}
