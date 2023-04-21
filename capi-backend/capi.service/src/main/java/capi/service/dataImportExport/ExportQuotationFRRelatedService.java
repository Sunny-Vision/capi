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
import capi.model.dataImportExport.ExportQuotationFRRelatedTableList;
import capi.service.CommonService;

@Service("ExportQuotationFRRelatedService")
public class ExportQuotationFRRelatedService extends DataExportServiceBase{
	
	@Autowired
	private QuotationDao dao;
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	private static final String[] headers = 
			new String []{
					"QuotationId", "Firm Code", 
					//"Firm Name", 
					"Purpose", "Variety Code"
					, "CPI Base Period", "Variety English Name", "Variety Chinese Name", "Seasonality", "Pricing Frequency"
					, "Is ICP", "ICP Product Code", "ICP Product Name", "ICP Type", "Quotation Status"
					, "Is FR Required", "Is Seasonal Withdrawl", "Is Return Goods", "Is Return New Goods", "Last Season Return Goods"
					, "Is FR Applied", "Whether FR Admin is used", "Used FR Value", "Is Used FR in Percentage", "Last FR Applied Date"
					, "FRAdmin", "Is FR Admin in Percentage"
			};

	@Override
	public int getTaskNo() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public void runTask(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);

		List<ExportQuotationFRRelatedTableList> results = dao.getAllQuotationFRRelatedResult();
		
		int rowCnt = 1;
		for (ExportQuotationFRRelatedTableList tableList : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			SXSSFCell cell = row.createCell(0);
			cell.setCellValue(String.valueOf(tableList.getQuotationId()));
			if(tableList.getFirmCode()!=null){
				SXSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(String.valueOf(tableList.getFirmCode()));
			}
//			SXSSFCell cell2 = row.createCell(1);
//			cell2.setCellValue(tableList.getFirmName());
			SXSSFCell cell3 = row.createCell(2);
			cell3.setCellValue(tableList.getPurposeCode());
			SXSSFCell cell4 = row.createCell(3);
			cell4.setCellValue(tableList.getUnitCode());
			SXSSFCell cell5 = row.createCell(4);
			cell5.setCellValue(tableList.getCpiBasePeriod());
			SXSSFCell cell6 = row.createCell(5);
			cell6.setCellValue(tableList.getUnitEnglishName());
			SXSSFCell cell7 = row.createCell(6);
			cell7.setCellValue(tableList.getUnitChineseName());
			if(tableList.getSeasonality()!=null){
				SXSSFCell cell8 = row.createCell(7);
				cell8.setCellValue(String.valueOf(tableList.getSeasonality()));
			}
			SXSSFCell cell9 = row.createCell(8);
			cell9.setCellValue(tableList.getPricingFrequencyName());
			SXSSFCell cell10 =row.createCell(9);
			cell10.setCellValue(tableList.isICP());
			SXSSFCell cell11 = row.createCell(10);
			cell11.setCellValue(tableList.getIcpProductCode());
			SXSSFCell cell12 = row.createCell(11);
			cell12.setCellValue(tableList.getIcpProductName());
			SXSSFCell cell13 = row.createCell(12);
			cell13.setCellValue(tableList.getIcpType());
			SXSSFCell cell14 = row.createCell(13);
			cell14.setCellValue(tableList.getQuotationStatus());
			SXSSFCell cell15 = row.createCell(14);
			cell15.setCellValue(tableList.isFRRequired());
			if(tableList.getSeasonalWithdrawal()!=null){
				SXSSFCell cell16 = row.createCell(15);
				cell16.setCellValue(commonService.formatDate(tableList.getSeasonalWithdrawal()));
			}
			SXSSFCell cell17 = row.createCell(16);
			cell17.setCellValue(tableList.isReturnGoods());
			SXSSFCell cell18 = row.createCell(17);
			cell18.setCellValue(tableList.isReturnNewGoods());
			SXSSFCell cell19 = row.createCell(18);
			cell19.setCellValue(tableList.isLastSeasonReturnGoods());
			SXSSFCell cell20 = row.createCell(19);
			cell20.setCellValue(tableList.isFRApplied());
			
			if(tableList.getIsUseFRAdmin()!=null){
				SXSSFCell cell21 = row.createCell(20);
				cell21.setCellValue(tableList.getIsUseFRAdmin());
			}
			if(tableList.getUsedFRValue()!=null){
				SXSSFCell cell22 = row.createCell(21);
				cell22.setCellValue(tableList.getUsedFRValue());
			}
			if(tableList.getIsUsedFRPercentage()!=null){
				SXSSFCell cell23 = row.createCell(22);
				cell23.setCellValue(tableList.getIsUsedFRPercentage());
			}
			if(tableList.getLastFRAppliedDate()!=null){
				SXSSFCell cell24 =row.createCell(23);
				cell24.setCellValue(commonService.formatDate(tableList.getLastFRAppliedDate()));
			}
			
			if(tableList.getFrAdmin()!=null){
				SXSSFCell cell25 = row.createCell(24);
				cell25.setCellValue(tableList.getFrAdmin());
			}
			if(tableList.getIsFRAdminPercentage()!=null){
				SXSSFCell cell26 = row.createCell(25);
				cell26.setCellValue(tableList.getIsFRAdminPercentage());
			}
			
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
