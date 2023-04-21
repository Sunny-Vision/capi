package capi.service.report;

import java.io.Console;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ItemDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UnitStatisticDao;
import capi.dal.UserDao;
import capi.entity.Item;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.QuotationStatisticsReportByQuotationCriteria;
import capi.model.report.QuotationStatisticsReportByQuotationUnitStat;
import capi.service.CommonService;

@Service("QuotationStatisticsReportByQuotationService")
public class QuotationStatisticsReportByQuotationService extends DataReportServiceBase{

	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private UnitStatisticDao unitStatisticDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode() {
		return "RF9021";
	}

	private final static String[] headers = {
			"No.",
			"Indoor Quotation record ID",
			"Field Quotation Record ID",
			"Quotation ID",
			"Reference Month",
			"Reference Date",
			"Purpose",
			"CPI based period",
			"Variety Code",
			"Variety Chinese name",
			"Variety English name",
			"Quotation Status",
			"Data Conversion Status",
			"Quotation Record Sequence (Order of collection date)",
			"Outlet Code",
			//"Outlet Name",
			"Outlet Type",
			"Product ID",
			"Country of Origin",
			"Product Attributes 1",
			"Product Attributes 2",
			"Product Attributes 3",
			"Product Attributes 4",
			"Product Attributes 5",
			"Product Availability",
			"Survey N Price",
			"Survey S Price",
			"Last Edited N Price",
			"Last Edited S Price",
			"Previous Edited N Price",
			"Previous Edited S Price",
			"Current Edited N Price",
			"Current Edited S Price",
			"N Price PR",
			"S Price PR",
			"Quotation record reason",
			"Quotation record remarks",
			"New replacement case",
			"Product change",
			"Data Conversion Remarks",
			"Outlier Case",
			"Outlier remarks",
			"Whether fieldwork is needed",
			"CPI Compilation Series",
			"CompilationMethod",
			"Seasonal item (S/W/O)",
			"Survey type",
			"Responsible Indoor officer ID",
			"Quotation Status",
			"Quotation's Average Price: T-6",
			"Quotation's Average Price: T-5",
			"Quotation's Average Price: T-4",
			"Quotation's Average Price: T-3",
			"Quotation's Average Price: T-2",
			"Quotation's Average Price: T-1",
			"Quotation's Average Price: Current Month (T)",
			"Quotation's Average Price's PR: T-5",
			"Quotation's Average Price's PR: T-4",
			"Quotation's Average Price's PR: T-3",
			"Quotation's Average Price's PR: T-2",
			"Quotation's Average Price's PR: T-1",
			"Quotation's Average Price's PR: Current Month (T)",
			"Quotation's Standard deviation: T-5",
			"Quotation's Standard deviation: T-4",
			"Quotation's Standard deviation: T-3",
			"Quotation's Standard deviation: T-2",
			"Quotation's Standard deviation: T-1",
			"Quotation's Standard deviation: Current Month (T)",
			"Quotation's Min Price: T-5",
			"Quotation's Min Price: T-4",
			"Quotation's Min Price: T-3",
			"Quotation's Min Price: T-2",
			"Quotation's Min Price: T-1",
			"Quotation's Min Price: Current Month (T)",
			"Quotation's Max Price: T-5",
			"Quotation's Max Price: T-4",
			"Quotation's Max Price: T-3",
			"Quotation's Max Price: T-2",
			"Quotation's Max Price: T-1",
			"Quotation's Max Price: Current Month (T)",
			"Quotation's Sum:  T-5",
			"Quotation's Sum:  T-4",
			"Quotation's Sum:  T-3",
			"Quotation's Sum:  T-2",
			"Quotation's Sum:  T-1",
			"Quotation's Sum:  Current Month (T)",
			"Quotation's Count: T-5",
			"Quotation's Count: T-4",
			"Quotation's Count: T-3",
			"Quotation's Count: T-2",
			"Quotation's Count: T-1",
			"Quotation's Count: Current Month (T)",
			"Variety's Average Price: T-6",
			"Variety's Average Price: T-5",
			"Variety's Average Price: T-4",
			"Variety's Average Price: T-3",
			"Variety's Average Price: T-2",
			"Variety's Average Price: T-1",
			"Variety's Average Price: Current Month (T)",
			"Variety's Average Price's PR: T-5",
			"Variety's Average Price's PR: T-4",
			"Variety's Average Price's PR: T-3",
			"Variety's Average Price's PR: T-2",
			"Variety's Average Price's PR: T-1",
			"Variety's Average Price's PR: Current Month (T)",
			"Variety's Standard deviation: T-5",
			"Variety's Standard deviation: T-4",
			"Variety's Standard deviation: T-3",
			"Variety's Standard deviation: T-2",
			"Variety's Standard deviation: T-1",
			"Variety's Standard deviation: Current Month (T)",
			"Variety's Min Price: T-5",
			"Variety's Min Price: T-4",
			"Variety's Min Price: T-3",
			"Variety's Min Price: T-2",
			"Variety's Min Price: T-1",
			"Variety's Min Price: Current Month (T)",
			"Variety's Max Price: T-5",
			"Variety's Max Price: T-4",
			"Variety's Max Price: T-3",
			"Variety's Max Price: T-2",
			"Variety's Max Price: T-1",
			"Variety's Max Price: Current Month (T)",
			"Variety's Sum:  T-5",
			"Variety's Sum:  T-4",
			"Variety's Sum:  T-3",
			"Variety's Sum:  T-2",
			"Variety's Sum:  T-1",
			"Variety's Sum:  Current Month (T)",
			"Variety's Count: T-5",
			"Variety's Count: T-4",
			"Variety's Count: T-3",
			"Variety's Count: T-2",
			"Variety's Count: T-1",
			"Variety's Count: Current Month (T)",
			"Variety's Price relative (AM/GM) T-5",
			"Variety's Price relative (AM/GM) T-4",
			"Variety's Price relative (AM/GM) T-3",
			"Variety's Price relative (AM/GM) T-2",
			"Variety's Price relative (AM/GM) T-1",
			"Variety's Price relative (AM/GM) Current Month (T)",
			"Variety's No. of PR for calculate PR(GM) T-5",
			"Variety's No. of PR for calculate PR(GM) T-4",
			"Variety's No. of PR for calculate PR(GM) T-3",
			"Variety's No. of PR for calculate PR(GM) T-2",
			"Variety's No. of PR for calculate PR(GM) T-1",
			"Variety's No. of PR for calculate PR(GM) Current Month (T) "
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		QuotationStatisticsReportByQuotationCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), QuotationStatisticsReportByQuotationCriteria.class);
		
		Date period = commonService.getMonth(criteria.getPeriodReferenceMonth());
		
		List<QuotationStatisticsReportByQuotationUnitStat> quotationStat = unitStatisticDao.getByQuotationStatisticsReportByQuotation(criteria.getPurpose(), criteria.getItemId(), criteria.getCpiSurveyForm(), criteria.getQuotationId(),period);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		CellStyle center = workBook.createCellStyle();
		center.setAlignment(CellStyle.ALIGN_CENTER);
		
		int rowCnt = 1;
		
		for(QuotationStatisticsReportByQuotationUnitStat data : quotationStat){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(rowCnt);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIndoorQuotationRecordId()!=null)
			cell.setCellValue(data.getIndoorQuotationRecordId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getQuotationRecordId()!=null)
			cell.setCellValue(data.getQuotationRecordId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getQuotationId()!=null)
			cell.setCellValue(data.getQuotationId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(sdf1.format(data.getReferenceDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurposeCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitChinName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitEngName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationStatus());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIndoorQuotationStatus());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationRecordSequence());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletCode());
			
//			cellCnt++;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(data.getOutletName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCountryOfOrigin());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductAttr1());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductAttr2());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductAttr3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductAttr4());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getProductAttr5());

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellStyle(center);
			if(data.getIsProductNotAvailable().equals("TRUE"))
			cell.setCellValue(true);
			else
				cell.setCellValue(false);

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getNPrice() != null)
			cell.setCellValue(data.getNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSPrice() != null)
			cell.setCellValue(data.getSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getLastNPrice()!=null)
			cell.setCellValue(data.getLastNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getLastSPrice()!=null)
			cell.setCellValue(data.getLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getPreviousNPrice()!=null)
			cell.setCellValue(data.getPreviousNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getPreviousSPrice()!=null)
			cell.setCellValue(data.getPreviousSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCurrentNPrice()!=null)
			cell.setCellValue(data.getCurrentNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCurrentSPrice()!=null)
			cell.setCellValue(data.getCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCurrentNPrice()!=null && data.getPreviousNPrice()!=null) {
				BigDecimal n = BigDecimal.valueOf(data.getCurrentNPrice())
											.divide(BigDecimal.valueOf(data.getPreviousNPrice()), 5, RoundingMode.HALF_UP)
											.multiply(BigDecimal.valueOf(100))
											.setScale(3, BigDecimal.ROUND_HALF_EVEN);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(n.doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCurrentSPrice()!=null && data.getPreviousSPrice()!=null) {
				BigDecimal n = BigDecimal.valueOf(data.getCurrentSPrice())
											.divide(BigDecimal.valueOf(data.getPreviousSPrice()), 5, RoundingMode.HALF_UP)
											.multiply(BigDecimal.valueOf(100))
											.setScale(3, BigDecimal.ROUND_HALF_EVEN);
				cell.setCellValue(n.doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQrReason());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQrRemark());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIsNewRecruitment());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIsProductChange());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIqrRemark());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellStyle(center);
			if (data.getIsOutlier().equals("TRUE"))
			cell.setCellValue(true);
			else
				cell.setCellValue(false);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutlierRemark());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellStyle(center);
			cell.setCellValue(data.getIsNoField());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCPICompilationSeries());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCompilationMethod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSeasonality());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCPIQoutationType());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationStatus());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice6()!=null)
			cell.setCellValue(data.getAverageCurrentSPrice6());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice5()!=null)
			cell.setCellValue(data.getAverageCurrentSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice4()!=null)
			cell.setCellValue(data.getAverageCurrentSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice3()!=null)
			cell.setCellValue(data.getAverageCurrentSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice2()!=null)
			cell.setCellValue(data.getAverageCurrentSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice1()!=null)
			cell.setCellValue(data.getAverageCurrentSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getAverageCurrentSPrice()!=null)
			cell.setCellValue(data.getAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
//			if(data.getFinalPRSPrice6()!=null&&data.getFinalPRSPrice5()!=null) {
			if(data.getFinalPRSPrice5()!=null) {
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getFinalPRSPrice5()).divide(BigDecimal.valueOf(data.getFinalPRSPrice6()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
//				cell.setCellValue("Test::"+BigDecimal.valueOf(data.getFinalPRSPrice6()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice5()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
//			if(data.getFinalPRSPrice4()!=null&&data.getFinalPRSPrice5()!=null) {
			if(data.getFinalPRSPrice4()!=null) {
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getFinalPRSPrice4()).divide(BigDecimal.valueOf(data.getFinalPRSPrice5()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
//				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice5()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice4()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			//if(data.getFinalPRSPrice4()!=null&&data.getFinalPRSPrice3()!=null) {
			if(data.getFinalPRSPrice3()!=null) {
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getFinalPRSPrice3()).divide(BigDecimal.valueOf(data.getFinalPRSPrice4()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
//				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice4()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice3()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			//if(data.getFinalPRSPrice3()!=null&&data.getFinalPRSPrice2()!=null) {
			if(data.getFinalPRSPrice2()!=null) {
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getFinalPRSPrice2()).divide(BigDecimal.valueOf(data.getFinalPRSPrice3()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
				//cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice3()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice2()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			//if(data.getFinalPRSPrice2()!=null&&data.getFinalPRSPrice1()!=null) {
			if(data.getFinalPRSPrice1()!=null) {
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getFinalPRSPrice1()).divide(BigDecimal.valueOf(data.getFinalPRSPrice2()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice1()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			//if(data.getFinalPRSPrice1()!=null&&data.getFinalPRSPrice()!=null) {
			if(data.getFinalPRSPrice()!=null) {
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getFinalPRSPrice()).divide(BigDecimal.valueOf(data.getFinalPRSPrice1()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getStandardDeviationSPrice5()!=null) 
			cell.setCellValue(data.getStandardDeviationSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getStandardDeviationSPrice4()!=null) 
			cell.setCellValue(data.getStandardDeviationSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getStandardDeviationSPrice3()!=null) 
			cell.setCellValue(data.getStandardDeviationSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getStandardDeviationSPrice2()!=null) 
			cell.setCellValue(data.getStandardDeviationSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getStandardDeviationSPrice1()!=null) 
			cell.setCellValue(data.getStandardDeviationSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getStandardDeviationSPrice()!=null) 
			cell.setCellValue(data.getStandardDeviationSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMinSPrice5()!=null) 
			cell.setCellValue(data.getMinSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMinSPrice4()!=null) 
			cell.setCellValue(data.getMinSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMinSPrice3()!=null) 
			cell.setCellValue(data.getMinSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMinSPrice2()!=null) 
			cell.setCellValue(data.getMinSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMinSPrice1()!=null) 
			cell.setCellValue(data.getMinSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMinSPrice()!=null) 
			cell.setCellValue(data.getMinSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMaxSPrice5()!=null) 
			cell.setCellValue(data.getMaxSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMaxSPrice4()!=null) 
			cell.setCellValue(data.getMaxSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMaxSPrice3()!=null) 
			cell.setCellValue(data.getMaxSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMaxSPrice2()!=null) 
			cell.setCellValue(data.getMaxSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMaxSPrice1()!=null) 
			cell.setCellValue(data.getMaxSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMaxSPrice()!=null) 
			cell.setCellValue(data.getMaxSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice5()!=null) 
			cell.setCellValue(data.getSumCurrentSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice4()!=null) 
			cell.setCellValue(data.getSumCurrentSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice3()!=null) 
			cell.setCellValue(data.getSumCurrentSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice2()!=null) 
			cell.setCellValue(data.getSumCurrentSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice1()!=null) 
			cell.setCellValue(data.getSumCurrentSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice()!=null) 
			cell.setCellValue(data.getSumCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice5()!=null) 
			cell.setCellValue(data.getCountCurrentSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice4()!=null) 
			cell.setCellValue(data.getCountCurrentSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice3()!=null) 
			cell.setCellValue(data.getCountCurrentSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice2()!=null) 
			cell.setCellValue(data.getCountCurrentSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice1()!=null) 
			cell.setCellValue(data.getCountCurrentSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCountCurrentSPrice()!=null) 
			cell.setCellValue(data.getCountCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsAverageCurrentSPrice6()!=null) 
			cell.setCellValue(data.getUsAverageCurrentSPrice6());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsAverageCurrentSPrice5()!=null) 
			cell.setCellValue(data.getUsAverageCurrentSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsAverageCurrentSPrice4()!=null) 
			cell.setCellValue(data.getUsAverageCurrentSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsAverageCurrentSPrice3()!=null) 
			cell.setCellValue(data.getUsAverageCurrentSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsAverageCurrentSPrice2()!=null) 
			cell.setCellValue(data.getUsAverageCurrentSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsAverageCurrentSPrice1()!=null) 
			cell.setCellValue(data.getUsAverageCurrentSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsAverageCurrentSPrice()!=null) 
			cell.setCellValue(data.getUsAverageCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			//if(data.getUsAveragePRSPrice6()!=null&&data.getUsAveragePRSPrice5()!=null) { 
			if(data.getUsAveragePRSPrice5()!=null) { 
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getUsAveragePRSPrice5()).divide(BigDecimal.valueOf(data.getUsAveragePRSPrice6()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
//				cell.setCellValue("Testing2::"+BigDecimal.valueOf(data.getUsAveragePRSPrice6()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice5()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
//			if(data.getUsAveragePRSPrice4()!=null&&data.getUsAveragePRSPrice5()!=null) { 
			if(data.getUsAveragePRSPrice4()!=null) { 	
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getUsAveragePRSPrice4()).divide(BigDecimal.valueOf(data.getUsAveragePRSPrice5()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
//				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice5()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice4()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
//			if(data.getUsAveragePRSPrice3()!=null&&data.getUsAveragePRSPrice4()!=null) { 
			if(data.getUsAveragePRSPrice3()!=null) { 
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getUsAveragePRSPrice3()).divide(BigDecimal.valueOf(data.getUsAveragePRSPrice4()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
//				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice4()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice3()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
//			if(data.getUsAveragePRSPrice3()!=null&&data.getUsAveragePRSPrice2()!=null) {
			if(data.getUsAveragePRSPrice2()!=null) {
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getUsAveragePRSPrice2()).divide(BigDecimal.valueOf(data.getUsAveragePRSPrice3()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
//				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice3()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice2()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			//if(data.getUsAveragePRSPrice2()!=null&&data.getUsAveragePRSPrice1()!=null) { 
			if(data.getUsAveragePRSPrice1()!=null) { 
			/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getUsAveragePRSPrice1()).divide(BigDecimal.valueOf(data.getUsAveragePRSPrice2()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
//				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice2()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice1()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
//			if(data.getUsAveragePRSPrice1()!=null&&data.getUsAveragePRSPrice()!=null) { 
			if(data.getUsAveragePRSPrice()!=null) { 
				/* 9021 - CAPI_Batch4b_comment_v12
				BigDecimal num = BigDecimal.valueOf(data.getUsAveragePRSPrice()).divide(BigDecimal.valueOf(data.getUsAveragePRSPrice1()),4,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).doubleValue());*/
				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsStandardDeviationSPrice5()!=null) { 
				cell.setCellValue(data.getUsStandardDeviationSPrice5());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsStandardDeviationSPrice4()!=null) { 
				cell.setCellValue(data.getUsStandardDeviationSPrice4());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsStandardDeviationSPrice3()!=null) { 
				cell.setCellValue(data.getUsStandardDeviationSPrice3());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsStandardDeviationSPrice2()!=null) { 
				cell.setCellValue(data.getUsStandardDeviationSPrice2());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsStandardDeviationSPrice1()!=null) { 
				cell.setCellValue(data.getUsStandardDeviationSPrice1());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsStandardDeviationSPrice()!=null) { 
				cell.setCellValue(data.getUsStandardDeviationSPrice());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMinSPrice5()!=null) { 
				cell.setCellValue(data.getUsMinSPrice5());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMinSPrice4()!=null) { 
				cell.setCellValue(data.getUsMinSPrice4());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMinSPrice3()!=null) { 
				cell.setCellValue(data.getUsMinSPrice3());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMinSPrice2()!=null) { 
				cell.setCellValue(data.getUsMinSPrice2());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMinSPrice1()!=null) { 
				cell.setCellValue(data.getUsMinSPrice1());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMinSPrice()!=null) { 
				cell.setCellValue(data.getUsMinSPrice());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMaxSPrice5()!=null) { 
				cell.setCellValue(data.getUsMaxSPrice5());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMaxSPrice4()!=null) { 
				cell.setCellValue(data.getUsMaxSPrice4());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMaxSPrice3()!=null) { 
				cell.setCellValue(data.getUsMaxSPrice3());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMaxSPrice2()!=null) { 
				cell.setCellValue(data.getUsMaxSPrice2());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMaxSPrice1()!=null) { 
				cell.setCellValue(data.getUsMaxSPrice1());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsMaxSPrice()!=null) { 
				cell.setCellValue(data.getUsMaxSPrice());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsSumCurrentSPrice5()!=null) { 
				cell.setCellValue(data.getUsSumCurrentSPrice5());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsSumCurrentSPrice4()!=null) { 
				cell.setCellValue(data.getUsSumCurrentSPrice4());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsSumCurrentSPrice3()!=null) { 
				cell.setCellValue(data.getUsSumCurrentSPrice3());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsSumCurrentSPrice2()!=null) { 
				cell.setCellValue(data.getUsSumCurrentSPrice2());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsSumCurrentSPrice1()!=null) { 
				cell.setCellValue(data.getUsSumCurrentSPrice1());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsSumCurrentSPrice()!=null) { 
				cell.setCellValue(data.getUsSumCurrentSPrice());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountCurrentSPrice5()!=null) { 
				cell.setCellValue(data.getUsCountCurrentSPrice5());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountCurrentSPrice4()!=null) { 
				cell.setCellValue(data.getUsCountCurrentSPrice4());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountCurrentSPrice3()!=null) { 
				cell.setCellValue(data.getUsCountCurrentSPrice3());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountCurrentSPrice2()!=null) { 
				cell.setCellValue(data.getUsCountCurrentSPrice2());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountCurrentSPrice1()!=null) { 
				cell.setCellValue(data.getUsCountCurrentSPrice1());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountCurrentSPrice()!=null) { 
				cell.setCellValue(data.getUsCountCurrentSPrice());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsFinalPRSPrice5()!=null) 
			cell.setCellValue(data.getUsFinalPRSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsFinalPRSPrice4()!=null) 
			cell.setCellValue(data.getUsFinalPRSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsFinalPRSPrice3()!=null) 
			cell.setCellValue(data.getUsFinalPRSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsFinalPRSPrice2()!=null) 
			cell.setCellValue(data.getUsFinalPRSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsFinalPRSPrice1()!=null) 
			cell.setCellValue(data.getUsFinalPRSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsFinalPRSPrice()!=null) 
			cell.setCellValue(data.getUsFinalPRSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountPRSPrice5()!=null) 
			cell.setCellValue(data.getUsCountPRSPrice5());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountPRSPrice4()!=null) 
			cell.setCellValue(data.getUsCountPRSPrice4());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountPRSPrice3()!=null) 
			cell.setCellValue(data.getUsCountPRSPrice3());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountPRSPrice2()!=null) 
			cell.setCellValue(data.getUsCountPRSPrice2());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountPRSPrice1()!=null) 
			cell.setCellValue(data.getUsCountPRSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountPRSPrice()!=null) 
			cell.setCellValue(data.getUsCountPRSPrice());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Excel
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "List");
		
		// Output Excel 
		try{
			String filename = UUID.randomUUID().toString()+".xlsx";
			String file = getFileBase()+"/"+filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();
			
			task.setPath(this.getFileRelativeBase()+"/"+filename);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	private boolean isDouble(double doubleValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		QuotationStatisticsReportByQuotationCriteria criteria = (QuotationStatisticsReportByQuotationCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes){
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}
		
		if (criteria.getItemId() != null && criteria.getItemId().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<Item> items = itemDao.getByIds(criteria.getItemId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Item item : items){
				codes.add(item.getCode() + " - " + item.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
		}
		
		if (criteria.getCpiSurveyForm() != null && criteria.getCpiSurveyForm().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer form : criteria.getCpiSurveyForm()){
				switch (form){
					case 1:codes.add("Market"); break;
					case 2:codes.add("Supermarket"); break;
					case 3:codes.add("Batch"); break;
					default:codes.add("Others"); break;
				}
				
			}
			descBuilder.append(String.format("CPI Survey Form: %s", StringUtils.join(codes, ", ")));
		}
		
		if (criteria.getQuotationId() != null && criteria.getQuotationId().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer quotationId : criteria.getQuotationId()){
				codes.add(quotationId.toString());
			}
			descBuilder.append(String.format("Quotation: %s", StringUtils.join(codes, ", ")));
		}
		
		if (descBuilder.length() > 0) descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s", criteria.getPeriodReferenceMonth()));
		
		User creator = userDao.findById(userId);
		
		task.setCriteriaSerialize(serialize);
		task.setFunctionCode(this.getFunctionCode());
		task.setReportType(ReportServiceBase.ZIP);
		task.setDescription(descBuilder.toString());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setUser(creator);
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
		return task;		
	}

	@Override
	public void createHeader(SXSSFRow row) {
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}	
	}
}
