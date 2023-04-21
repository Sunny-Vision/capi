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

import capi.dal.ImportExportTaskDao;
import capi.dal.UOMConversionDao;
import capi.entity.ImportExportTask;
import capi.entity.UOMConversion;
import capi.entity.Uom;

@Service("ExportUOMConversionService")
public class ExportUOMConversionService extends DataExportServiceBase{
	
	@Autowired
	private UOMConversionDao dao;
	@Autowired
	private ImportExportTaskDao taskDao;
		
	private static final String[] headers = new String []{
			"UOM Conversion Id", "Base UOM Id", "Base UOM Chinese Name", "Base UOM English Name", "Target UOM Id"
			, "Target UOM Chinese Name", "Target UOM English Name", "Conversion Factor" 
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 7;
	}

	// 0/UOM Conversion Id
	// 1/Base UOM Id
	// 2/Base UOM Chi Name
	// 3/Base UOM Eng Name
	// 4/Target UOM Id
	// 5/Target UOM Chi Name
	// 6/Target UOM Eng Name
	// 7/Conversion Factor
	

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		ScrollableResults results = dao.getAllUOMConversionResult();

		
		int rowCnt = 1;
		while (results.next()){
			UOMConversion entity = (UOMConversion)results.get()[0];
			
			Uom baseUom = entity.getBaseUOM();
			Uom targetUom = entity.getTargetUOM();
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(entity.getId()));
			SXSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(String.valueOf(baseUom.getId()));
			SXSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(baseUom.getChineseName());
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(baseUom.getEnglishName());
			SXSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(String.valueOf(targetUom.getId()));
			SXSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(targetUom.getChineseName());
			SXSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(targetUom.getEnglishName());
			SXSSFCell cell7 = row.createCell(7);
			cell7.setCellValue(entity.getFactor());
			
			dao.evit(entity);
			rowCnt++;
			
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}

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
