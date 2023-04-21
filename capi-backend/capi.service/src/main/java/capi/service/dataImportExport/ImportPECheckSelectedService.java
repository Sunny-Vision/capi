package capi.service.dataImportExport;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.ImportExportTaskDao;
import capi.dal.PECheckTaskDao;
import capi.entity.Assignment;
import capi.entity.ImportExportTask;
import capi.entity.PECheckTask;

@Service("ImportPECheckSelectedService")
public class ImportPECheckSelectedService  extends DataImportServiceBase{
	
	@Autowired
	private PECheckTaskDao peCheckTaskDao;
	
	@Autowired
	private AssignmentDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 29;
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
			String[] values = getStringValues(row, 23);
			
			//For Assignment
			Assignment assignment = fillEntity(values, rowCnt);
			dao.save(assignment);
			
			//For PECheckTask
			PECheckTask peCheckTask = fillEntityForPECheckTask(values, rowCnt, assignment);
			if(peCheckTask != null){
				peCheckTaskDao.save(peCheckTask);
			}
			
			
			if (rowCnt % 20 == 0){
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
			if(entity==null){
				throw new RuntimeException("Record not found: Assignment id="+id);
			}
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return entity;
	}
	
	private PECheckTask fillEntityForPECheckTask(String[] values, int rowCnt, Assignment assignment) throws Exception{
		
		PECheckTask children = null;
		
		int col = 0;
		try{
//			col = 18;
//			col = 17;
			col = 16;
			if(!assignment.isImportedAssignment()){
//				if(StringUtils.isEmpty(values[18])){
//				if(StringUtils.isEmpty(values[17])){
				if(StringUtils.isEmpty(values[16])){
					if(assignment.getPeCheckTask()!=null){
						children = assignment.getPeCheckTask();
						children.setSelected(false);
						children.setFieldTeamHead(false);
						children.setSectionHead(false);
						children.setCertaintyCase(false);
						children.setRandomCase(false);
						
						children.setAssignment(assignment);
					}
				} else {
					if(assignment.getPeCheckTask()!=null){
						children = assignment.getPeCheckTask();
					} else {
						children = new PECheckTask();
						children.setSurveyMonth(assignment.getSurveyMonth());
					}
					
//					boolean isSelected = Boolean.parseBoolean(values[18]);
//					boolean isSelected = Boolean.parseBoolean(values[17]);
					boolean isSelected = Boolean.parseBoolean(values[16]);
					children.setSelected(isSelected);
					
//					col = 19;
//					col = 18;
					col = 17;
					if(StringUtils.isEmpty(values[col])){
						throw new RuntimeException("Section Head should not be empty");
					}
					boolean isSectionHead = Boolean.parseBoolean(values[col]);
					children.setSectionHead(isSectionHead);
					
//					col = 20;
//					col = 19;
					col = 18;
					if(StringUtils.isEmpty(values[col])){
						throw new RuntimeException("Field Team Head should not be empty");
					}
					boolean isFieldTeamHead = Boolean.parseBoolean(values[col]);
					children.setFieldTeamHead(isFieldTeamHead);
					
//					col = 21;
//					col = 20;
					col = 19;
					if(StringUtils.isEmpty(values[col])){
						throw new RuntimeException("Certainty Case should not be empty");
					}
					boolean isCertaintyCase = Boolean.parseBoolean(values[col]);
					children.setCertaintyCase(isCertaintyCase);
					
//					col = 22;
//					col = 21;
					col = 20;
					if(StringUtils.isEmpty(values[col])){
						throw new RuntimeException("Random Case should not be empty");
					}
					boolean isRandomCase = Boolean.parseBoolean(values[col]);
					children.setRandomCase(isRandomCase);
					
					children.setAssignment(assignment);
				}
			}
			
			
		} catch (Exception ex){
			throw new RuntimeException(ex.getMessage() + " (row no.="+rowCnt+", col no.="+(col+1)+")");
		}
		
		return children;
	}
}