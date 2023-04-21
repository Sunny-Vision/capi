package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ActivityCodeDao;
import capi.dal.ImportExportTaskDao;
import capi.entity.ActivityCode;
import capi.entity.ImportExportTask;

@Service("ExportActivityCodeService")
public class ExportActivityCodeService extends DataExportServiceBase{
	
	@Autowired
	private ActivityCodeDao dao;
	@Autowired
	private ImportExportTaskDao taskDao;
		
	private static final String[] headers = new String []{"Activity Code Id", "Activity Code", "Description", "Man Day"};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		ScrollableResults results = dao.getAllActivityCodeResult();

		
		int rowCnt = 1;
		while (results.next()){
			ActivityCode code = (ActivityCode)results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(code.getActivityCodeId()));
			SXSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(code.getCode());
			SXSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(code.getDescription());
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(code.getManDay());
			dao.evit(code);
			
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
			rowCnt++;
		}	
		results.close();

		try{
			String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			
			ImportExportTask task = taskDao.findById(taskId);
			task.setFilePath(this.getFileRelativeBase()+"/"+filename);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		taskDao.flush();
		
	}
	
	@Override
	public void createHeader(SXSSFRow row){
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}

}
