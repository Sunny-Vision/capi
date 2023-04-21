package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.QuotationDao;
import capi.entity.ImportExportTask;
import capi.model.dataImportExport.ExportQuotationIndoorAllocationTableList;

@Service("ExportQuotationIndoorAllocationService")
public class ExportQuotationIndoorAllocationService extends DataExportServiceBase{
	
	@Autowired
	private QuotationDao dao;
	@Autowired
	private ImportExportTaskDao taskDao;
	
	private static final String[] headers = new String []{
			"Quotation Id", "Variety Code", "Variety Chinese Name", "Variety English Name", "Cpi Base Period"
			, "Purpose Code", "Group Code", "Sub-group Code", "Outlet Type Code", "Batch Code"
			, "Outlet Code"
			//, "Outlet Name"
			, "Pricing Month", "Seasonality", "Is FR Required"
			, "Indoor Allocation Code"
			};

	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 19;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		List<ExportQuotationIndoorAllocationTableList> results = dao.getAllQuotationIndoorAllocationResult();
		
		int rowCnt = 1;
		//while (results.next()){
		for (ExportQuotationIndoorAllocationTableList code: results){
			//ExportQuotationIndoorAllocationTableList tableList = (ExportQuotationIndoorAllocationTableList)results.get()[0];
			SXSSFRow row = sheet.createRow(rowCnt);
			
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(code.getQuotationId()));
			SXSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(code.getUnitCode());
			SXSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(code.getUnitChineseName());
			SXSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(code.getUnitEnglishName());
			SXSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(code.getCpiBasePeriod());
			SXSSFCell cell5 = row.createCell(5);
			cell5.setCellValue(code.getPurposeCode());
			SXSSFCell cell6 = row.createCell(6);
			cell6.setCellValue(code.getGroupCode());
			SXSSFCell cell7 = row.createCell(7);
			cell7.setCellValue(code.getSubGroupCode());
			SXSSFCell cell8 = row.createCell(8);
			cell8.setCellValue(code.getOutletTypeCode());
			SXSSFCell cell9 = row.createCell(9);
			cell9.setCellValue(code.getBatchCode());
			SXSSFCell cell10 = row.createCell(10);
			cell10.setCellValue(String.valueOf(code.getOutletFirmCode()));
//			SXSSFCell cell11 = row.createCell(11);
//			cell11.setCellValue(code.getOutletName());
			SXSSFCell cell12 = row.createCell(11);
			cell12.setCellValue(code.getPricingMonthName());
			SXSSFCell cell13 = row.createCell(12);
			cell13.setCellValue(String.valueOf(code.getSeasonality()));
			SXSSFCell cell14 = row.createCell(13);
			cell14.setCellValue(code.getIsFRRequired());
			SXSSFCell cell15 = row.createCell(14);
			cell15.setCellValue(code.getIndoorAllocationCode());
			
			rowCnt++;
			
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
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
