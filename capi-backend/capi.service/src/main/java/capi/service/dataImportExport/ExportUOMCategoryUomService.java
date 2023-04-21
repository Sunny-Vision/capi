package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.UOMCategoryDao;
import capi.entity.ImportExportTask;
import capi.entity.UOMCategory;
import capi.entity.Uom;

@Service("ExportUOMCategoryUomService")
public class ExportUOMCategoryUomService extends DataExportServiceBase{
	
	@Autowired
	private UOMCategoryDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
		
	private static final String[] headers = new String []{
			"UOM Id", "UOM Chinese name", "UOM English name", "UOM Category Id", "UOM Category description"
			};
	
	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		ScrollableResults results = dao.getAllUOMCategoryResult();
		
		/**
		 * 0/UOM Id
		 * 1/UOM Chinese name
		 * 2/UOM English name
		 * 
		 * 3/UOM Category Id
		 * 4//UOM Category description
		 */
		
		int rowCnt = 1;
		while (results.next()){
			UOMCategory code = (UOMCategory)results.get()[0];
			
			
			SXSSFRow row = sheet.createRow(rowCnt);
			
			//For UOMCategory
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(String.valueOf(code.getId()));
			SXSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(code.getDescription());

			if(!code.getUoms().isEmpty()){
				Iterator<Uom> uomList = code.getUoms().iterator();
				while (uomList.hasNext()){
					Uom uom = uomList.next();
					
					SXSSFCell cell = row.createCell(0);
					cell.setCellValue(String.valueOf(uom.getId()));
					SXSSFCell cell1 = row.createCell(1);
					cell1.setCellValue(uom.getChineseName());
					SXSSFCell cell2 = row.createCell(2);
					cell2.setCellValue(uom.getEnglishName());
					dao.evit(uom);
				}
			}
			
			dao.evit(code);
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
