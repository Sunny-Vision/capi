package capi.service.dataImportExport;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ImportExportTaskDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.entity.ImportExportTask;
import capi.model.dataImportExport.ExportCPICompilationList;
import capi.service.CommonService;

@Service("ExportCPICompilationService")
public class ExportCPICompilationService extends DataExportServiceBase{

	@Autowired
	private IndoorQuotationRecordDao dao;
	
	@Autowired
	private ImportExportTaskDao taskDao;
	
	@Autowired
	private CommonService commonService;
	
	private static final String[] headers = new String[]{
		"Reference Month","Quotation Id", "IndoorQuotation Record Id", "FieldQuotation Record Id"
		, "Quotation Record Sequence (Order of collection date)", "Edited N Price (Current)"
		, "Edited S Price (Current)", "Edited N Price (Previous)", "Edited S Price (Previous)"
		, "Quotation Record Price relative (Current / Previous)", "Edited N Price (Last)"
		, "Edited S Price (Last)", "Edited Keep Number (Last) (Date)"
		, "Quotation Record Price relative (Current / Last)", "Sum Quotation price (Current)"
		, "No. of Quotation records with price of the quotation (Current)"
		, "Average Quotation price (Current)", "Sum Quotation price (Previous)"
		, "No. of Quotation records with price of the quotation (Previous)"
		, "Average Quotation price (Previous)", "Sum Quotation price (Last)"
		, "No. of Quotation records with price of the quotation (Last)"
		, "Average Quotation price (Last)", "Keep Average Quotation price (Last) (Date)"
		, "Average Quotation price (Current used)", "Average Quotation price (Previous used)"
		, "Quotation Price relative (Current used / Previous used)"
		, "Keep Average Quotation price (Current used)", "Sum Unit price (Current)"
		, "No. of Quotation records with price of the Unit (Current)", "Average Unit price (Current)"
		, "Sum Unit price (Previous)", "No. of Quotation records with price of the Unit (Previous)"
		, "Average Unit price (Previous)", "Sum Unit price (Last)"
		, "No. of Quotation records with price of the Unit (Last)", "Average Unit price (Last)"
		, "Keep Average Unit price (Last) (Date)", "Average Unit price (Current used)"
		, "Average Unit price (Previous used)", "Unit Price relative (Current used / Previous used)"
		, "Keep Average Unit price (Current used)", "Imputed Average Quotation price (Previous used)"
		, "Imputed Average Quotation price (Previous used) remark"
		, "Imputed Average Unit price (Previous used)", "Imputed Average Unit price (Previous used) remark"
		, "Variety Standard UOM", "Variety Standard UOM Value", "CPI Base period", "Variety Code"
		, "Variety Name (Chin & Eng)", "CPI Compilation Series", "Outlier (Y/N)", "Outlier remark"
		, "Reference Date", "Reference Month Year", "Seasonal item (S/W/O)", "Survey type", "Purpose"
		, "CPI Compilation method", "Fresh item (Y/N)", "IndoorQuotation record remark (Current)"
		, "Old Barcode", "Old Barcode Sequence", "Product change (Y/N)", "New Product (Y/N)"
		, "New Recruitment (Y/N)", "Product Category id", "Batch code"
		, "Indicator of Current Quotation record Keep number (Y/N)"
		, "Indicator of Sprice copy from Nprice (Y/N)", "Indicator of Nprice copy from Sprice (Y/N)"
		, "Responsible Indoor officer ID", "Is ICP", "ICP Product Code"
		, "ICP Product Name", "ICP Type (Data Export: Unit/Quotation)", "CPI Quotation Type"
		, "Outlet code (Field quotation Record)", "Product id (Field quotation Record)"
		, "Form Type", "Quotation Status", "Pricing Month"
	};
	
	@Override
	public int getTaskNo(){
		// TODO Auto-generated method stub
		return 25;
	}
	
	@Override
	public void runTask(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ImportExportTask task = taskDao.findById(taskId);
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		Date refMonth = task.getReferenceMonth();
		Integer purposeId = task.getPurposeId();
		List<ExportCPICompilationList> results = dao.exportCPICompilationList(refMonth, purposeId);
		
		int rowCnt = 1;
		for (ExportCPICompilationList code : results){
			BigDecimal number;
			SXSSFRow row = sheet.createRow(rowCnt);
			
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatMonth(code.getIndoorReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			if(code.getQuotationId()!=null)
				cell.setCellValue(String.valueOf(code.getQuotationId()));
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			if(code.getIndoorQuotationRecordId()!=null)
				cell.setCellValue(String.valueOf(code.getIndoorQuotationRecordId()));
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			if(code.getQuotationRecordId()!=null)
				cell.setCellValue(String.valueOf(code.getQuotationRecordId()));
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			if(code.getQuotationRecordSequence()!=null)
				cell.setCellValue(String.valueOf(code.getQuotationRecordSequence()));
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			if(code.getCurrentNPrice()!=null)
				cell.setCellValue(code.getCurrentNPrice());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			if(code.getCurrentSPrice()!=null)
				cell.setCellValue(code.getCurrentSPrice());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			if(code.getPreviousNPrice()!=null)
				cell.setCellValue(code.getPreviousNPrice());
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			if(code.getPreviousSPrice()!=null)
				cell.setCellValue(code.getPreviousSPrice());
			
			cellCnt = 9;
			cell = row.createCell(cellCnt);
			if(code.getCurrentSPrice()!=null && code.getPreviousSPrice()!=null && code.getPreviousSPrice()!=0){
				number = BigDecimal.valueOf(code.getCurrentSPrice()).divide(BigDecimal.valueOf(code.getPreviousSPrice()),5,RoundingMode.HALF_UP);
				cell.setCellValue(number.multiply(BigDecimal.valueOf(100)).doubleValue());
			}
				
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			if(code.getLastNPrice()!=null)
				cell.setCellValue(code.getLastNPrice());
			
			cellCnt = 11;
			cell = row.createCell(cellCnt);
			if(code.getLastSPrice()!=null)
				cell.setCellValue(code.getLastSPrice());
			
			cellCnt = 12;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getLastPriceDate()));
			
			cellCnt = 13;
			cell = row.createCell(cellCnt);
			if(code.getCurrentSPrice()!=null &&code.getLastSPrice()!=null&&code.getLastSPrice()!=0){
				number = BigDecimal.valueOf(code.getCurrentSPrice()).divide(BigDecimal.valueOf(code.getLastSPrice()),5, RoundingMode.HALF_UP);
				cell.setCellValue(number.multiply(BigDecimal.valueOf(100)).doubleValue());
			}
//				cell.setCellValue(round(code.getCurrentSPrice()/code.getLastSPrice()*100,3));
			
			cellCnt = 14;
			cell = row.createCell(cellCnt);
			if(code.getQsSumCurrentSPrice()!=null)
				cell.setCellValue(code.getQsSumCurrentSPrice());
			
			cellCnt = 15;
			cell = row.createCell(cellCnt);
			if(code.getQsCountCurrentSPrice()!=null)
				cell.setCellValue(code.getQsCountCurrentSPrice());
			
			cellCnt = 16;
			cell = row.createCell(cellCnt);
			if(code.getQsAverageCurrentSPrice()!=null){
				number = BigDecimal.valueOf(code.getQsAverageCurrentSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(round(code.getQsAverageCurrentSPrice(),2));

			cellCnt = 17;
			cell = row.createCell(cellCnt);
			if(code.getQsSumLastSPrice()!=null)
				cell.setCellValue(code.getQsSumLastSPrice());
			
			cellCnt = 18;
			cell = row.createCell(cellCnt);
			if(code.getQsCountLastSPrice()!=null)
				cell.setCellValue(code.getQsCountLastSPrice());
			
			cellCnt = 19;
			cell = row.createCell(cellCnt);
			if(code.getQsAverageLastSPrice()!=null){
				number = BigDecimal.valueOf(code.getQsAverageLastSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(round(code.getQsAverageLastSPrice(),2));
			
			cellCnt = 20;
			cell = row.createCell(cellCnt);
			if(code.getQsLastHasPriceSumCurrentSPrice()!=null)
				cell.setCellValue(code.getQsLastHasPriceSumCurrentSPrice());
			
			cellCnt = 21;
			cell = row.createCell(cellCnt);
			if(code.getQsLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(code.getQsLastHasPriceCountCurrentSPrice());
			
			cellCnt = 22;
			cell = row.createCell(cellCnt);
			if(code.getQsLastHasPriceAverageCurrentSPrice()!=null){
				number = BigDecimal.valueOf(code.getQsLastHasPriceAverageCurrentSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(round(code.getQsLastHasPriceAverageCurrentSPrice(),2));
			
			cellCnt = 23;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatMonth(code.getQsLastHasPriceReferenceMonth()));
			
			BigDecimal currentPrice = null;
			BigDecimal previousPrice = null;
			
			cellCnt = 24;
			cell = row.createCell(cellCnt);
			if(code.getCompilationMethod() == 5 || code.getCompilationMethod() == 6){
				if(code.getQsAverageCurrentSPrice() != null){
					number = BigDecimal.valueOf(code.getQsAverageCurrentSPrice());
					currentPrice = number.setScale(2, RoundingMode.HALF_UP);
				}
//					currentPrice = round(code.getQsAverageCurrentSPrice(),2);
			} else {
				if(code.getQsAverageCurrentSPrice() == null){
					if(code.getQsLastHasPriceAverageCurrentSPrice()!=null){
						number = BigDecimal.valueOf(code.getQsLastHasPriceAverageCurrentSPrice());
						currentPrice = number.setScale(2, RoundingMode.HALF_UP);
					}
				}
//					currentPrice = round(code.getQsLastHasPriceAverageCurrentSPrice(),2);
				else {
					number = BigDecimal.valueOf(code.getQsAverageCurrentSPrice());
					currentPrice = number.setScale(2, RoundingMode.HALF_UP);
				}
//					currentPrice = round(code.getQsAverageCurrentSPrice(),2);
			}
			
			if(currentPrice!=null)
				cell.setCellValue(currentPrice.doubleValue());
			
			cellCnt = 25;
			cell = row.createCell(cellCnt);
			if(code.getCompilationMethod() == 5 || code.getCompilationMethod() == 6){
				if(code.getQsAverageLastSPrice()!=null){
					number = BigDecimal.valueOf(code.getQsAverageLastSPrice());
					previousPrice = number.setScale(2, RoundingMode.HALF_UP);
				}
//					previousPrice = round(code.getQsAverageLastSPrice(),2);
			} else {
				if(code.getQsAverageLastSPrice()==null){
					if (code.getQsLastHasPriceAverageCurrentSPrice() != null){
						number = BigDecimal.valueOf(code.getQsLastHasPriceAverageCurrentSPrice());
						previousPrice = number.setScale(2, RoundingMode.HALF_UP);
					}
				}
//					previousPrice = round(code.getQsLastHasPriceAverageCurrentSPrice(),2);
				else{
					number = BigDecimal.valueOf(code.getQsAverageLastSPrice());
					previousPrice = number.setScale(2, RoundingMode.HALF_UP);
				}
//					previousPrice = round(code.getQsAverageLastSPrice(),2);
			}
			if(previousPrice!=null)
				cell.setCellValue(previousPrice.doubleValue());
			
			cellCnt = 26;
			cell = row.createCell(cellCnt);
			if(currentPrice!=null && previousPrice!=null && previousPrice.compareTo(BigDecimal.ZERO)!=0){
				number = currentPrice.divide(previousPrice, 5, RoundingMode.HALF_UP);
				cell.setCellValue(number.multiply(BigDecimal.valueOf(100)).doubleValue());
			}
//				cell.setCellValue(round(currentPrice/previousPrice*100,3));
			
			cellCnt = 27;
			cell = row.createCell(cellCnt);
			if(code.getCompilationMethod() == 5 || code.getCompilationMethod() == 6){
				cell.setCellValue(false);
			} else {
				cell.setCellValue(code.getQsAverageCurrentSPrice() == null && code.getQsLastHasPriceAverageCurrentSPrice() != null ? true : false);
			}
			
			cellCnt = 28;
			cell = row.createCell(cellCnt);
			if(code.getUsSumCurrentSPrice()!=null)
				cell.setCellValue(code.getUsSumCurrentSPrice());
			
			cellCnt = 29;
			cell = row.createCell(cellCnt);
			if(code.getUsCountCurrentSPrice()!=null)
				cell.setCellValue(code.getUsCountCurrentSPrice());
			
			cellCnt = 30;
			cell = row.createCell(cellCnt);
			if(code.getUsAverageCurrentSPrice()!=null){
				number = BigDecimal.valueOf(code.getUsAverageCurrentSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(round(code.getUsAverageCurrentSPrice(),2));
			
			cellCnt = 31;
			cell = row.createCell(cellCnt);
			if(code.getUsSumLastSPrice()!=null){
				number = BigDecimal.valueOf(code.getUsSumLastSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(round(code.getUsSumLastSPrice(),2));
			
			cellCnt = 32;
			cell = row.createCell(cellCnt);
			if(code.getUsCountLastSPrice()!=null){
				number = BigDecimal.valueOf(code.getUsCountLastSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(code.getUsCountLastSPrice());
			
			cellCnt = 33;
			cell = row.createCell(cellCnt);
			if(code.getUsAverageLastSPrice()!=null){
				number = BigDecimal.valueOf(code.getUsAverageLastSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(round(code.getUsAverageLastSPrice(),2));
			
			cellCnt = 34;
			cell = row.createCell(cellCnt);
			if(code.getUsLastHasPriceSumCurrentSPrice()!=null){
				number = BigDecimal.valueOf(code.getUsLastHasPriceSumCurrentSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(round(code.getUsLastHasPriceSumCurrentSPrice(),2));
			
			cellCnt = 35;
			cell = row.createCell(cellCnt);
			if(code.getUsLastHasPriceCountCurrentSPrice()!=null)
				cell.setCellValue(code.getUsLastHasPriceCountCurrentSPrice());
			
			cellCnt = 36;
			cell = row.createCell(cellCnt);
			if(code.getUsLastHasPriceAverageCurrentSPrice()!=null){
				number = BigDecimal.valueOf(code.getUsLastHasPriceAverageCurrentSPrice());
				cell.setCellValue(number.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
//				cell.setCellValue(round(code.getUsLastHasPriceAverageCurrentSPrice(),2));
			
			cellCnt = 37;
			cell = row.createCell(cellCnt);
			if(code.getUsLastHasPriceReferenceMonth()!=null)
				cell.setCellValue(commonService.formatMonth(code.getUsLastHasPriceReferenceMonth()));
			
			currentPrice = null;
			previousPrice = null;
			
			cellCnt = 38;
			cell = row.createCell(cellCnt);
			if(code.getCompilationMethod() == 5 || code.getCompilationMethod() == 6){
				if(code.getUsAverageCurrentSPrice()!=null){
					number = BigDecimal.valueOf(code.getUsAverageCurrentSPrice());
					currentPrice = number.setScale(2, RoundingMode.HALF_UP);
				}
//					currentPrice = round(code.getUsAverageCurrentSPrice(),2);
			} else {
				if(code.getUsAverageCurrentSPrice()==null){
					if (code.getUsLastHasPriceAverageCurrentSPrice() != null){
						number = BigDecimal.valueOf(code.getUsLastHasPriceAverageCurrentSPrice());
						currentPrice = number.setScale(2, RoundingMode.HALF_UP);
					}
				}
//					currentPrice = round(code.getUsLastHasPriceAverageCurrentSPrice(),2);
				else {
					number = BigDecimal.valueOf(code.getUsAverageCurrentSPrice());
					currentPrice = number.setScale(2, RoundingMode.HALF_UP);
				}
//					currentPrice = round(code.getUsAverageCurrentSPrice(),2);
			}
			if(currentPrice!=null) {
				cell.setCellValue(currentPrice.doubleValue());
			}
//				cell.setCellValue(currentPrice);
			
			cellCnt = 39;
			cell = row.createCell(cellCnt);
			if(code.getCompilationMethod() == 5 || code.getCompilationMethod() == 6){
				if(code.getUsAverageLastSPrice()!=null){
					number = BigDecimal.valueOf(code.getUsAverageLastSPrice());
					previousPrice = number.setScale(2, RoundingMode.HALF_UP);
				}
//					previousPrice = round(code.getUsAverageLastSPrice(),2);
			} else {
				if(code.getUsAverageLastSPrice()==null){
					if(code.getUsLastHasPriceAverageCurrentSPrice()!=null){
						number = BigDecimal.valueOf(code.getUsLastHasPriceAverageCurrentSPrice());
						previousPrice = number.setScale(2, RoundingMode.HALF_UP);
					}
				}
//					previousPrice = round(code.getUsLastHasPriceAverageCurrentSPrice(),2);
				else {
					number = BigDecimal.valueOf(code.getUsAverageLastSPrice());
					previousPrice = number.setScale(2, RoundingMode.HALF_UP);
				}
//					previousPrice = round(code.getUsAverageLastSPrice(),2);
			}
			
			if(previousPrice!=null){
				cell.setCellValue(previousPrice.doubleValue());
			}
//				cell.setCellValue(previousPrice);
			
			cellCnt = 40;
			cell = row.createCell(cellCnt);
			if(code.getUsFinalPRSPrice()!=null)
				cell.setCellValue(code.getUsFinalPRSPrice());
			
			cellCnt = 41;
			cell = row.createCell(cellCnt);
//			if(code.getCompilationMethod() == 5 || code.getCompilationMethod() == 6){
//				cell.setCellValue(false);
//			} else {
//				cell.setCellValue(code.getUsAverageCurrentSPrice() == null && code.getUsLastHasPriceAverageCurrentSPrice() != null ? true : false);
//			}
			cell.setCellValue(code.getUnitKeepPrice());
			
			cellCnt = 42;
			cell = row.createCell(cellCnt);
			if(code.getImputedQuotationPrice()!=null)
				cell.setCellValue(code.getImputedQuotationPrice());
			
			cellCnt = 43;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getImputedQuotationRemark());
			
			cellCnt = 44;
			cell = row.createCell(cellCnt);
			if(code.getImputedUnitPrice()!=null)
				cell.setCellValue(code.getImputedUnitPrice());
			
			cellCnt = 45;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getImputedUnitRemark());
			
			cellCnt = 46;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUcDescription());
			
			cellCnt = 47;
			cell = row.createCell(cellCnt);
			if(code.getUnitUomValue()!=null)
				cell.setCellValue(code.getUnitUomValue());
			
			cellCnt = 48;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCpiBasePeriod());
			
			cellCnt = 49;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitCode());
			
			cellCnt = 50;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getUnitChineseName()+" & "+code.getUnitEnglishName());
			
			cellCnt = 51;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getqCPICompilationSeries());
			
			cellCnt = 52;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isOutlier());
			
			cellCnt = 53;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getOutlierRemark());
			
			cellCnt = 54;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(code.getReferenceDate()));
			
			cellCnt = 55;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatMonth(code.getQrReferenceMonth()));
			
			cellCnt = 56;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getuSeasonality());
			
			cellCnt = 57;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getuCPIQoutationType());
			
			cellCnt = 58;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getPurposeName());
			
			cellCnt = 59;
			cell = row.createCell(cellCnt);
			switch (code.getCompilationMethod()){
				case 1 :
					cell.setCellValue("A.M. (Supermarket, fresh)");
					break;
				case 2 :
					cell.setCellValue("A.M. (Supermarket, non-fresh)");
					break;
				case 3 :
					cell.setCellValue("A.M. (Market)");
					break;
				case 4 :
					cell.setCellValue("G.M. (Supermarket)");
					break;
				case 5 :
					cell.setCellValue("G.M. (Batch)");
					break;
				case 6 :
					cell.setCellValue("A.M. (Batch)");
					break;
				default :
					break;
			}
			
			
			cellCnt = 60;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isuIsFreshItem());
			
			cellCnt = 61;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIndoorRemark());
			
			cellCnt = 62;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getqOldFormBarSerial());
			
			cellCnt = 63;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getqOldFormSequence());
			
			cellCnt = 64;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isProductChange());
			
			cellCnt = 65;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNewProduct());
			
			cellCnt = 66;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isNewRecruitment());
			
			cellCnt = 67;
			cell = row.createCell(cellCnt);
			if(code.getProductGroupId()!=null)
				cell.setCellValue(String.valueOf(code.getProductGroupId()));
			
			cellCnt = 68;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getBatchCode());
			
			cellCnt = 69;
			cell = row.createCell(cellCnt);
			if(code.getIsCurrentPriceKeepNo()!=null)
				cell.setCellValue(code.getIsCurrentPriceKeepNo());
			
			cellCnt = 70;
			cell = row.createCell(cellCnt);
			if(code.getCopyLastPriceType()!=null && code.getCopyLastPriceType()==2)
				cell.setCellValue(true);
			else
				cell.setCellValue(false);
			
			cellCnt = 71;
			cell = row.createCell(cellCnt);
			if(code.getCopyLastPriceType()!=null && code.getCopyLastPriceType()==3)
				cell.setCellValue(true);
			else
				cell.setCellValue(false);
			
			cellCnt = 72;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getStaffCode());
			
			cellCnt = 73;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.isqIsICP());
			
			cellCnt = 74;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpProductCode());
			
			cellCnt = 75;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpProductName());
			
			cellCnt = 76;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getIcpType());
			
			cellCnt = 77;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getCpiQuotationType());
			
			cellCnt = 78;
			cell = row.createCell(cellCnt);
			if(code.getOutletCode()!=null)
				cell.setCellValue(String.valueOf(code.getOutletCode()));
			
			cellCnt = 79;
			cell = row.createCell(cellCnt);
			if(code.getProductId()!=null)
				cell.setCellValue(String.valueOf(code.getProductId()));
			
			cellCnt = 80;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getFormType());
			
			cellCnt = 81;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getQuotationStatus());
			
			cellCnt = 82;
			cell = row.createCell(cellCnt);
			cell.setCellValue(code.getPricingMonth());
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		///results.close();

		try{
			String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			
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
	
//	public Double round(Double number, int dp) {
//		if(number == null)
//			return null;
//		
//		Double factor = (Double) Math.pow(10, dp);
//		
//		return Math.round(number*factor)/factor;
//	}
}
