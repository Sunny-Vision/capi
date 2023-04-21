package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.IndoorQuotationRecordDao;
import capi.dal.ItemDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationStatisticDao;
import capi.dal.ReportTaskDao;
import capi.dal.UnitDao;
import capi.dal.UnitStatisticDao;
import capi.dal.UserDao;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.Unit;
import capi.entity.Item;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.QuotationStatisticsReportByVariety;
import capi.model.report.QuotationStatisticsReportByVarietyCriteria;
import capi.service.CommonService;
import net.sf.jasperreports.engine.JasperPrint;

@Service("QuotationStatisticsReportByVarietyService")
public class QuotationStatisticsReportByVarietyService extends DataReportServiceBase {

	@Autowired
	private IndoorQuotationRecordDao dao;

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PurposeDao purposeDao;

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private ItemDao itemDao;

	@Autowired
	private UnitStatisticDao unitStatisticDao;

	@Autowired
	private QuotationDao quotationDao;

	@Autowired
	private QuotationStatisticDao quotationStatisticDao;

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9022";
	}

	private final static String[] headers = { "No.", "Quotation ID", "Reference Month", "Reference Date", "Purpose",
			"CPI based period", "Variety Code", "Variety Chinese name", "Variety English name", "Outlet Code",
			//"Outlet Name", 
			"Outlet Type", "Day 1 - N Price", "Day 2 - N Price", "Day 3 - N Price", "Day 4 - N Price",
			"Day 5 - N Price", "Day 6 - N Price", "Day 7 - N Price", "Day 8 - N Price", "Day 9 - N Price",
			"Day 10 - N Price", "Day 11 - N Price", "Day 12 - N Price", "Day 13 - N Price", "Day 14 - N Price",
			"Day 15 - N Price", "Day 16 - N Price", "Day 17 - N Price", "Day 18 - N Price", "Day 1 - S Price",
			"Day 2 - S Price", "Day 3 - S Price", "Day 4 - S Price", "Day 5 - S Price", "Day 6 - S Price",
			"Day 7 - S Price", "Day 8 - S Price", "Day 9 - S Price", "Day 10 - S Price", "Day 11 - S Price",
			"Day 12 - S Price", "Day 13 - S Price", "Day 14 - S Price", "Day 15 - S Price", "Day 16 - S Price",
			"Day 17 - S Price", "Day 18 - S Price", "CPI Compilation Series", "CompilationMethod",
			"Seasonal item (S/W/O)", "Survey type", "Quotation Status", "Quotation's Average Price: T-1",
			"Quotation's Average Price: Current Month (T)", "Quotation's Average Price's PR: Current Month (T)",
			"Quotation's Standard deviation: T-1", "Quotation's Standard deviation: Current Month (T)",
			"Quotation's Min Price: T-1", "Quotation's Min Price: Current Month (T)", "Quotation's Max Price: T-1",
			"Quotation's Max Price: Current Month (T)", "Quotation's Sum:  T-1", "Quotation's Sum:  Current Month (T)",
			"Quotation's Count: T-1", "Quotation's Count: Current Month (T)", "Variety's Average Price: T-1",
			"Variety's Average Price: Current Month (T)", "Variety's Average Price's PR: Current Month (T)",
			"Variety's Standard deviation: T-1", "Variety's Standard deviation: Current Month (T)",
			"Variety's Min Price: T-1", "Variety's Min Price: Current Month (T)", "Variety's Max Price: T-1",
			"Variety's Max Price: Current Month (T)", "Variety's Sum:  T-1", "Variety's Sum:  Current Month (T)",
			"Variety's Count: T-1", "Variety's Count: Current Month (T)", "Variety's Price relative (AM/GM) T-1",
			"Variety's Price relative (AM/GM) Current Month (T)", "Variety's No. of PR for calculate PR(GM) T-1",
			"Variety's No. of PR for calculate PR(GM) Current Month (T)" };

	private final static String[] headers1 = { "No.", "Quotation ID", "Reference Month", "Reference Date", "Purpose",
			"CPI based period", "Variety Code", "Variety Chinese name", "Variety English name", "Outlet Code",
			//"Outlet Name", 
			"Outlet Type", "Day 1 - N Price", "Day 2 - N Price", "Day 3 - N Price", "Day 4 - N Price",
			"Day 5 - N Price", "Day 6 - N Price", "Day 7 - N Price", "Day 8 - N Price", "Day 9 - N Price",
			"Day 10 - N Price", "Day 11 - N Price", "Day 12 - N Price", "Day 13 - N Price", "Day 14 - N Price",
			"Day 15 - N Price", "Day 16 - N Price", "Day 17 - N Price", "Day 18 - N Price", "Day 1 - S Price",
			"Day 2 - S Price", "Day 3 - S Price", "Day 4 - S Price", "Day 5 - S Price", "Day 6 - S Price",
			"Day 7 - S Price", "Day 8 - S Price", "Day 9 - S Price", "Day 10 - S Price", "Day 11 - S Price",
			"Day 12 - S Price", "Day 13 - S Price", "Day 14 - S Price", "Day 15 - S Price", "Day 16 - S Price",
			"Day 17 - S Price", "Day 18 - S Price", "CPI Compilation Series", "CompilationMethod",
			"Seasonal item (S/W/O)", "Survey type", "Quotation Status", "Quotation's Average Price: T-2",
			"Quotation's Average Price: T-1", "Quotation's Average Price's PR: T-1",
			"Quotation's Standard deviation: T-2", "Quotation's Standard deviation: T-1", "Quotation's Min Price: T-2",
			"Quotation's Min Price: T-1", "Quotation's Max Price: T-2", "Quotation's Max Price: T-1",
			"Quotation's Sum:  T-2", "Quotation's Sum: T-1", "Quotation's Count: T-2",
			"Quotation's Count: T-1", "Variety's Average Price: T-2", "Variety's Average Price: T-1",
			"Variety's Average Price's PR: T-1", "Variety's Standard deviation: T-2",
			"Variety's Standard deviation: T-1", "Variety's Min Price: T-2", "Variety's Min Price: T-1",
			"Variety's Max Price: T-2", "Variety's Max Price: T-1", "Variety's Sum:  T-2",
			"Variety's Sum: T-1", "Variety's Count: T-2", "Variety's Count: T-1",
			"Variety's Price relative (AM/GM) T-2", "Variety's Price relative (AM/GM) T-1",
			"Variety's No. of PR for calculate PR(GM) T-2",
			"Variety's No. of PR for calculate PR(GM) T-1" };

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())) {
			throw new RuntimeException("Criteria not defined");
		}

		QuotationStatisticsReportByVarietyCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(),
				QuotationStatisticsReportByVarietyCriteria.class);

		Date periodReferenceMonth = commonService.getMonth(criteria.getPeriodReferenceMonth());

		// Calculate previous Month
		Date previousPeriodReferenceMonth = null;

		if (Integer.parseInt(criteria.getPeriodReferenceMonth().substring(0, 2)) > 1
				&& Integer.parseInt(criteria.getPeriodReferenceMonth().substring(0, 2)) < 13) {
			String previousMonth = Integer
					.toString(Integer.parseInt(criteria.getPeriodReferenceMonth().substring(0, 2)) - 1);
			String previousYear = criteria.getPeriodReferenceMonth().substring(3, 7);
			previousPeriodReferenceMonth = commonService.getMonth(previousMonth + "-" + previousYear);
		} else if (Integer.parseInt(criteria.getPeriodReferenceMonth().substring(0, 2)) == 1) {
			String previousMonth = "12";
			String previousYear = Integer
					.toString(Integer.parseInt(criteria.getPeriodReferenceMonth().substring(3, 7)) - 1);
			previousPeriodReferenceMonth = commonService.getMonth(previousMonth + "-" + previousYear);
		}

		List<QuotationStatisticsReportByVariety> result = dao.getQuotationStatisticsReportByVariety(
				criteria.getPurpose(), criteria.getItemId(), criteria.getCpiSurveyForm(), criteria.getQuotationId(),
				periodReferenceMonth, "0");
		List<QuotationStatisticsReportByVariety> result1 = dao.getQuotationStatisticsReportByVariety(
				criteria.getPurpose(), criteria.getItemId(), criteria.getCpiSurveyForm(), criteria.getQuotationId(), 
				previousPeriodReferenceMonth, "1");
		
		/**List<QuotationStatisticsReportByVariety> exportResult  = setDynamicPriceList(result);
		List<QuotationStatisticsReportByVariety> exportResult1  = setDynamicPriceList(result1);**/
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();

		// Sheet 0: Current Month List
		SXSSFSheet sheet = workBook.getSheetAt(0);
		workBook.setSheetName(0, "Current Month List");

		int rowCnt = 1;

		for (QuotationStatisticsReportByVariety data : result) {
			SXSSFRow row = sheet.createRow(rowCnt);

			// Input Row of Data start

			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(rowCnt);

			// Quotation ID
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationId() != null) {
				cell.setCellValue(data.getQuotationId());
			}

			// Reference Month
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getReferenceMonth() != null) {
				cell.setCellValue(String.valueOf(commonService.formatShortMonth(data.getReferenceMonth())));
			}

			// Reference Date
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(" - ");
//			if (data.getReferenceDate() != null) {
//				cell.setCellValue(String.valueOf(commonService.formatDateStr(data.getReferenceDate())));
//			}
			
			

			// Purpose
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPurpose() != null) {
				cell.setCellValue(data.getPurpose());
			}

			// CPI based period
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCpiBasePeriod() != null) {
				cell.setCellValue(data.getCpiBasePeriod());
			}

			// Variety Code
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCode() != null) {
				cell.setCellValue(data.getVarietyCode());
			}

			// Variety Chinese name
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getChineseName() != null) {
				cell.setCellValue(data.getChineseName());
			}

			// Variety English name
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getEnglishName() != null) {
				cell.setCellValue(data.getEnglishName());
			}

			// Outlet Code
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getOutletCode() != null) {
				cell.setCellValue(data.getOutletCode());
			}

			// Outlet Name
//			cellCnt++;
//			cell = row.createCell(cellCnt);
//			if (data.getOutletName() != null) {
//				cell.setCellValue(data.getOutletName());
//			}

			// Outlet Type
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getOutletType() != null) {
				cell.setCellValue(data.getOutletType());
			}
			
//			int priceDate = 18;
//			
//			// Date - N Price
//			if(data.getnPriceDataCollection() != null){
//				for (Double nPrice : data.getnPriceDataCollection()){
//					cellCnt++;
//					cell = row.createCell(cellCnt);
//					if(nPrice != null) {
//						BigDecimal bd = new BigDecimal(nPrice);
//						bd = bd.setScale(4, RoundingMode.HALF_UP);
//						cell.setCellValue(bd.doubleValue());
//					} else {
//						cell.setCellValue("");
//					}
//				}
//			}
//			 
//			if(data.getnPriceDataCollection().size() < priceDate){
//				for (int i = 0; i < (priceDate - data.getnPriceDataCollection().size()); i++){
//					cellCnt++;
//					cell = row.createCell(cellCnt);
//					cell.setCellValue("");
//				}
//			}
//			
//			
//			// Date - S Price
//			if(data.getsPriceDataCollection() != null){
//				if(data.getQuotationId() == "31868"){
//					System.out.println("getsPriceDataCollection" + data.getsPriceDataCollection().size());
//					for (Double sPrice : data.getsPriceDataCollection()){
//						System.out.println("S Price" + sPrice);
//					}
//				}
//				for (Double sPrice : data.getsPriceDataCollection()){
//					cellCnt++;
//					cell = row.createCell(cellCnt);
//					if(sPrice != null){
//						BigDecimal bd = new BigDecimal(sPrice);
//						bd = bd.setScale(4, RoundingMode.HALF_UP);
//						cell.setCellValue(bd.doubleValue());
//					} else {
//						cell.setCellValue("");
//					}
//				}
//				
//			} 
//			
//			if(data.getsPriceDataCollection().size() < priceDate){
//				for (int i = 0; i < (priceDate - data.getsPriceDataCollection().size()); i++){
//					cellCnt++;
//					cell = row.createCell(cellCnt);
//					cell.setCellValue("");
//				}
//			}
//			
			
			//Day 1 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection1() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 2 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection2() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection2());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 3 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection3() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection3());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 4 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection4() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection4());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 5 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection5() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection5());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 6 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection6() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection6());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 7 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection7() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection7());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 8 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection8() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection8());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 9 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection9() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection9());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 10 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection10() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection10());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 11 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection11() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection11());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 12 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection12() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection12());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 13 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection13() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection13());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 14 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection14() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection14());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 15 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection15() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection15());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 16 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection16() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection16());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 17 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection17() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection17());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 18 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection18() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection18());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			//
//						// Day 1 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection1() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 2 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection2() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection2());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 3 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection3() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection3());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 4 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection4() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection4());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 5 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection5() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection5());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 6 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection6() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection6());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 7 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection7() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection7());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 8 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection8() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection8());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 9 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection9() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection9());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 10 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection10() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection10());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 11 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection11() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection11());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 12 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection12() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection12());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 13 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection13() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection13());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 14 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection14() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection14());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 15 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection15() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection15());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 16 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection16() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection16());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 17 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection17() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection17());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			////
			//Day 18 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection18() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection18());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// CPI Compilation Series
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCpiCompilationSeries() != null) {
				cell.setCellValue(data.getCpiCompilationSeries());
			}

			// CompilationMethod
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCompilationMethod() != null) {
				switch (data.getCompilationMethod()) {
				case 1:
					cell.setCellValue("A.M. (Supermarket, fresh)");
					break;
				case 2:
					cell.setCellValue("A.M. (Supermarket, non-fresh)");
					break;
				case 3:
					cell.setCellValue("A.M. (Market)");
					break;
				case 4:
					cell.setCellValue("G.M. (Supermarket)");
					break;
				case 5:
					cell.setCellValue("G.M. (Batch)");
					break;
				case 6:
					cell.setCellValue("A.M. (Batch)");
					break;
				default: // do nothing
					break;
				}
			}

			// Seasonal item (S/W/O)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSeasonalityItem() != null) {
				cell.setCellValue(data.getSeasonalityItem());
			}

			// Survey type
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSurveyType() != null) {
				switch (data.getSurveyType()) {
				case 1:
					cell.setCellValue("Market");
					break;
				case 2:
					cell.setCellValue("Supermarket");
					break;
				case 3:
					cell.setCellValue("Batch");
					break;
				case 4:
					cell.setCellValue("Other");
					break;
				default: // do nothing
					break;
				}
			}

			// Quotation Status
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationStatus() != null) {
				cell.setCellValue(data.getQuotationStatus());
			}

			// Quotation's Average Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationAveragePriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationAveragePriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Average Price: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationAveragePriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationAveragePriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Average Price's PR: Current Month (T)
			//cell = row.createCell(cellCnt++);
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice() != null) {
				BigDecimal bd = new BigDecimal(data.getFinalPRSPrice());
				bd = bd.setScale(3, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			/*
			if (data.getQuotationAveragePriceT() != null && data.getQuotationAveragePriceT1() != null) {
				if (data.getQuotationAveragePriceT1() != 0) {
					BigDecimal bd = new BigDecimal(
							100.0 * data.getQuotationAveragePriceT() / data.getQuotationAveragePriceT1());
//					bd = bd.setScale(3, RoundingMode.UP);	
					double quotationAveragePriceT = Math.round(Math.round(bd.doubleValue() * Math.pow(10, (3) + 1)) / 10) / Math.pow(10, (3));	

					cell.setCellValue(quotationAveragePriceT);
				}
			}
			*/
			
			// Quotation's Standard deviation: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationStandardDeviationT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationStandardDeviationT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Standard deviation: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationStandardDeviationT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationStandardDeviationT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Min Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationMinPriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationMinPriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Min Price: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationMinPriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationMinPriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Max Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationMaxPriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationMaxPriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Max Price: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationMaxPriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationMaxPriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Sum: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationSumT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationSumT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Sum: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationSumT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationSumT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Count: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationCountT1() != null) {
				cell.setCellValue(data.getQuotationCountT1());
			}

			// Quotation's Count: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationCountT() != null) {
				cell.setCellValue(data.getQuotationCountT());
			}

			// Variety's Average Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyAveragePriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyAveragePriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Average Price: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyAveragePriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyAveragePriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Average Price's PR: Current Month (T)
			//cell = row.createCell(cellCnt++);
			cellCnt++;
			cell = row.createCell(cellCnt);

			if (data.getAveragePRSPrice() != null) {
				BigDecimal bd = new BigDecimal(data.getAveragePRSPrice());
				bd = bd.setScale(3, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
//			if (data.getVarietyAveragePriceT() != null && data.getVarietyAveragePriceT1() != null) {
//				if (data.getVarietyAveragePriceT() != 0) {
//					
//					BigDecimal bd = new BigDecimal(
//							100.0 * data.getVarietyAveragePriceT1() / data.getVarietyAveragePriceT());
//					bd = bd.setScale(4, RoundingMode.HALF_UP);
//					cell.setCellValue(bd.doubleValue());
//				}
//			}

			// Variety's Standard deviation: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyStandardDeviationT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyStandardDeviationT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Standard deviation: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyStandardDeviationT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyStandardDeviationT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Min Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyMinPriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyMinPriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Min Price: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyMinPriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyMinPriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Max Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyMaxPriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyMaxPriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Max Price: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyMaxPriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyMaxPriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Sum: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietySumT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietySumT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Sum: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietySumT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietySumT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Count: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCountT1() != null) {
				cell.setCellValue(data.getVarietyCountT1());
			}

			// Variety's Count: Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCountT() != null) {
				cell.setCellValue(data.getVarietyCountT());
			}

			// Variety's Price relative (AM/GM) T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyPriceRelativeT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyPriceRelativeT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Price relative (AM/GM) Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyPriceRelativeT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyPriceRelativeT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's No. of PR for calculate PR(GM) T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCountPRT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyCountPRT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's No. of PR for calculate PR(GM) Current Month (T)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCountPRT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyCountPRT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet.flushRows();
			}
		}
		
		// Sheet 1: Previous Month List
		SXSSFSheet sheet1 = workBook.createSheet("Previous Month List");
		SXSSFRow row = sheet1.createRow(0);
		int cnt = 0;
		for (String header : headers1) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}

		rowCnt = 1;

		for (QuotationStatisticsReportByVariety data : result1) {
			row = sheet1.createRow(rowCnt);

			// Input Row of Data start

			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(rowCnt);

		// Quotation ID
		cellCnt++;
		cell = row.createCell(cellCnt);
		if (data.getQuotationId() != null) {
				cell.setCellValue(data.getQuotationId());
			}

			// Reference Month
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getReferenceMonth() != null) {
				cell.setCellValue(String.valueOf(commonService.formatShortMonth(data.getReferenceMonth())));
			}

			// Reference Date
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(" - ");
//			if (data.getReferenceDate() != null) {
//				cell.setCellValue(String.valueOf(commonService.formatDateStr(data.getReferenceDate())));
//			}

			// Purpose
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getPurpose() != null) {
				cell.setCellValue(data.getPurpose());
			}

			// CPI based period
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCpiBasePeriod() != null) {
				cell.setCellValue(data.getCpiBasePeriod());
			}

			// Variety Code
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCode() != null) {
				cell.setCellValue(data.getVarietyCode());
			}

			// Variety Chinese name
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getChineseName() != null) {
				cell.setCellValue(data.getChineseName());
			}

			// Variety English name
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getEnglishName() != null) {
				cell.setCellValue(data.getEnglishName());
			}

			// Outlet Code
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getOutletCode() != null) {
				cell.setCellValue(data.getOutletCode());
			}

			// Outlet Name
//			cellCnt++;
//			cell = row.createCell(cellCnt);
//			if (data.getOutletName() != null) {
//				cell.setCellValue(data.getOutletName());
//			}

			// Outlet Type
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getOutletType() != null) {
				cell.setCellValue(data.getOutletType());
			}

//			int priceDate = 18;
//			
//			// Date - N Price
//			if(data.getnPriceDataCollection() != null){
//				for (Double nPrice : data.getnPriceDataCollection()){
//					cellCnt++;
//					cell = row.createCell(cellCnt);
//					if(nPrice != null) {
//						BigDecimal bd = new BigDecimal(nPrice);
//						bd = bd.setScale(4, RoundingMode.HALF_UP);
//						cell.setCellValue(bd.doubleValue());
//					} else {
//						cell.setCellValue("");
//					}
//				}
//			}
//			 
//			if(data.getnPriceDataCollection().size() < priceDate){
//				for (int i = 0; i < (priceDate - data.getnPriceDataCollection().size()); i++){
//					cellCnt++;
//					cell = row.createCell(cellCnt);
//					cell.setCellValue("");
//				}
//			}
//			
//			
//			// Date - S Price
//			if(data.getsPriceDataCollection() != null){
//				if(data.getQuotationId() == "31868"){
//					System.out.println("getsPriceDataCollection" + data.getsPriceDataCollection().size());
//					for (Double sPrice : data.getsPriceDataCollection()){
//						System.out.println("S Price" + sPrice);
//					}
//				}
//				for (Double sPrice : data.getsPriceDataCollection()){
//					cellCnt++;
//					cell = row.createCell(cellCnt);
//					if(sPrice != null){
//						BigDecimal bd = new BigDecimal(sPrice);
//						bd = bd.setScale(4, RoundingMode.HALF_UP);
//						cell.setCellValue(bd.doubleValue());
//					} else {
//						cell.setCellValue("");
//					}
//				}
//				
//			} 
//			
//			if(data.getsPriceDataCollection().size() < priceDate){
//				for (int i = 0; i < (priceDate - data.getsPriceDataCollection().size()); i++){
//					cellCnt++;
//					cell = row.createCell(cellCnt);
//					cell.setCellValue("");
//				}
//			}
			// Day 1 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection1() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 2 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection2() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection2());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 3 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection3() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection3());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 4 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection4() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection4());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 5 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection5() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection5());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 6 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection6() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection6());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 7 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection7() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection7());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 8 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection8() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection8());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 9 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection9() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection9());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 10 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection10() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection10());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 11 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection11() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection11());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 12 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection12() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection12());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 13 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection13() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection13());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 14 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection14() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection14());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 15 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection15() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection15());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 16 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection16() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection16());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 17 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection17() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection17());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 18 - N Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getnPriceDataCollection18() != null) {
				BigDecimal bd = new BigDecimal(data.getnPriceDataCollection18());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 1 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection1() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 2 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection2() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection2());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 3 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection3() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection3());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 4 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection4() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection4());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 5 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection5() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection5());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 6 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection6() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection6());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 7 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection7() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection7());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 8 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection8() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection8());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 9 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection9() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection9());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 10 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection10() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection10());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 11 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection11() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection11());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 12 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection12() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection12());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 13 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection13() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection13());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 14 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection14() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection14());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 15 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection15() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection15());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 16 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection16() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection16());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 17 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection17() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection17());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Day 18 - S Price
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getsPriceDataCollection18() != null) {
				BigDecimal bd = new BigDecimal(data.getsPriceDataCollection18());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// CPI Compilation Series
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCpiCompilationSeries() != null) {
				cell.setCellValue(data.getCpiCompilationSeries());
			}

			// CompilationMethod
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getCompilationMethod() != null) {
				switch (data.getCompilationMethod()) {
				case 1:
					cell.setCellValue("A.M. (Supermarket, fresh)");
					break;
				case 2:
					cell.setCellValue("A.M. (Supermarket, non-fresh)");
					break;
				case 3:
					cell.setCellValue("A.M. (Market)");
					break;
				case 4:
					cell.setCellValue("G.M. (Supermarket)");
					break;
				case 5:
					cell.setCellValue("G.M. (Batch)");
					break;
				case 6:
					cell.setCellValue("A.M. (Batch)");
					break;
				default: // do nothing
					break;
				}
			}

			// Seasonal item (S/W/O)
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSeasonalityItem() != null) {
				cell.setCellValue(data.getSeasonalityItem());
			}

			// Survey type
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getSurveyType() != null) {
				switch (data.getSurveyType()) {
				case 1:
					cell.setCellValue("Market");
					break;
				case 2:
					cell.setCellValue("Supermarket");
					break;
				case 3:
					cell.setCellValue("Batch");
					break;
				case 4:
					cell.setCellValue("Other");
					break;
				default: // do nothing
					break;
				}
			}

			// Quotation Status
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationStatus() != null) {
				cell.setCellValue(data.getQuotationStatus());
			}

			// Quotation's Average Price: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationAveragePriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationAveragePriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Average Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationAveragePriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationAveragePriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			//Quotation's Average Price's PR: T-1					
			//cell = row.createCell(cellCnt++);
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getFinalPRSPrice() != null) {
				BigDecimal bd = new BigDecimal(data.getFinalPRSPrice());
				bd = bd.setScale(3, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			/*
			if (data.getQuotationAveragePriceT() != null && data.getQuotationAveragePriceT1() != null) {
				if (data.getQuotationAveragePriceT1() != 0) {
					BigDecimal bd = new BigDecimal(
							100.0 * data.getQuotationAveragePriceT() / data.getQuotationAveragePriceT1());
					bd = bd.setScale(3, RoundingMode.HALF_UP);
					cell.setCellValue(bd.doubleValue());
				}
			}
			*/
			// Quotation's Standard deviation: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationStandardDeviationT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationStandardDeviationT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Standard deviation: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationStandardDeviationT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationStandardDeviationT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Min Price: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationMinPriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationMinPriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Min Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationMinPriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationMinPriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Max Price: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationMaxPriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationMaxPriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Max Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationMaxPriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationMaxPriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Sum: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationSumT1() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationSumT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Sum: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationSumT() != null) {
				BigDecimal bd = new BigDecimal(data.getQuotationSumT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Quotation's Count: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationCountT1() != null) {
				cell.setCellValue(data.getQuotationCountT1());
			}

			// Quotation's Count: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getQuotationCountT() != null) {
				cell.setCellValue(data.getQuotationCountT());
			}

			// Variety's Average Price: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyAveragePriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyAveragePriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Average Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyAveragePriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyAveragePriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Average Price's PR: T-1
			//cell = row.createCell(cellCnt++);
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getAveragePRSPrice() != null) {
				BigDecimal bd = new BigDecimal(data.getAveragePRSPrice());
				bd = bd.setScale(3, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}
			/*
			if (data.getVarietyAveragePriceT() != null && data.getVarietyAveragePriceT1() != null) {
				if (data.getVarietyAveragePriceT1() != 0) {
					BigDecimal bd = new BigDecimal(
							100.0 * data.getVarietyAveragePriceT() / data.getVarietyAveragePriceT1());
					bd = bd.setScale(3, RoundingMode.HALF_UP);
					cell.setCellValue(bd.doubleValue());
				}
			}
			*/
			
			// Variety's Standard deviation: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyStandardDeviationT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyStandardDeviationT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Standard deviation: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyStandardDeviationT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyStandardDeviationT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Min Price: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyMinPriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyMinPriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Min Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyMinPriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyMinPriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Max Price: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyMaxPriceT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyMaxPriceT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Max Price: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyMaxPriceT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyMaxPriceT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Sum: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietySumT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietySumT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Sum: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietySumT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietySumT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Count: T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCountT1() != null) {
				cell.setCellValue(data.getVarietyCountT1());
			}

			// Variety's Count: T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCountT() != null) {
				cell.setCellValue(data.getVarietyCountT());
			}

			// Variety's Price relative (AM/GM) T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyPriceRelativeT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyPriceRelativeT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's Price relative (AM/GM) T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyPriceRelativeT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyPriceRelativeT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's No. of PR for calculate PR(GM) T-2
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCountPRT1() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyCountPRT1());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Variety's No. of PR for calculate PR(GM) T-1
			cellCnt++;
			cell = row.createCell(cellCnt);
			if (data.getVarietyCountPRT() != null) {
				BigDecimal bd = new BigDecimal(data.getVarietyCountPRT());
				bd = bd.setScale(4, RoundingMode.HALF_UP);
				cell.setCellValue(bd.doubleValue());
			}

			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0) {
				sheet1.flushRows();
			}
		}
//
		try {
			String filename = UUID.randomUUID().toString() + ".xlsx";
			String file = getFileBase() + "/" + filename;
			FileOutputStream outStream = new FileOutputStream(file);
			workBook.write(outStream);
			workBook.close();

			task.setPath(this.getFileRelativeBase() + "/" + filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {

		QuotationStatisticsReportByVarietyCriteria criteria = (QuotationStatisticsReportByVarietyCriteria) criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();

		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(criteria.getPurpose());
			List<String> codes = new ArrayList<String>();
			for (Purpose purpose : purposes) {
				codes.add(purpose.getCode());
			}
			descBuilder.append(String.format("Purpose: %s", StringUtils.join(codes, ", ")));
		}
		
		if (criteria.getQuotationId() != null && criteria.getQuotationId().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer quotationId : criteria.getQuotationId()) {
				codes.add(quotationId.toString());
			}
			descBuilder.append(String.format("Quotation: %s", StringUtils.join(codes, ", ")));
		}

		/*
		if (criteria.getItemId() != null && criteria.getItemId().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<Unit> units = unitDao.getByIds(criteria.getItemId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Unit unit : units) {
				codes.add(unit.getCode() + " - " + unit.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
		}
		*/

		if (criteria.getItemId() != null && criteria.getItemId().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<Item> items = itemDao.getByIds(criteria.getItemId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Item item : items) {
				codes.add(item.getCode() + " - " + item.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
		}
		
		if (criteria.getCpiSurveyForm() != null && criteria.getCpiSurveyForm().size() > 0) {
			if (descBuilder.length() > 0)
				descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer form : criteria.getCpiSurveyForm()) {
				switch (form) {
				case 1:
					codes.add("Market");
					break;
				case 2:
					codes.add("Supermarket");
					break;
				case 3:
					codes.add("Batch");
					break;
				default:
					codes.add("Others");
					break;
				}

			}
			descBuilder.append(String.format("CPI Survey Form: %s", StringUtils.join(codes, ", ")));
		}

		if (descBuilder.length() > 0)
			descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s", criteria.getPeriodReferenceMonth()));

		if (taskType == ReportServiceBase.PDF) {
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		} else {
			descBuilder.append("\n");
			descBuilder.append("Export Type: XLSX");
		}

		User creator = userDao.findById(userId);

		task.setCriteriaSerialize(serialize);
		task.setFunctionCode(this.getFunctionCode());
		task.setReportType(taskType);
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
		for (String header : headers) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}
	
//	private List<QuotationStatisticsReportByVariety> setDynamicPriceList(List<QuotationStatisticsReportByVariety> results){
//		List<QuotationStatisticsReportByVariety> list = new ArrayList<QuotationStatisticsReportByVariety>();
//		ArrayList<Double> sPriceDataCollection = new ArrayList<Double>();
//		ArrayList<Double> nPriceDataCollection = new ArrayList<Double>();
//		QuotationStatisticsReportByVariety temp = null;
//
//		if (results != null && results.size() > 0){
//			for (QuotationStatisticsReportByVariety data : results) {
//				if(temp != null && (!data.getQuotationId().equals(temp.getQuotationId()) 
//						|| !data.getOutletCode().equals(temp.getOutletCode()) 
//						|| !data.getPurpose().equals(temp.getPurpose()))
//						) {
//					temp.setsPriceDataCollection(sPriceDataCollection);
//					temp.setnPriceDataCollection(nPriceDataCollection);
//					list.add(temp);
//					sPriceDataCollection = new ArrayList<Double>();
//					nPriceDataCollection = new ArrayList<Double>();
//				}
//				
//				if(data.getsPriceDataCollection1() != null){
//					sPriceDataCollection.add(data.getsPriceDataCollection1());
//				}
//				if(data.getnPriceDataCollection1() != null){
//					nPriceDataCollection.add(data.getnPriceDataCollection1());
//				}
//				
//				temp = data;
//			}
//			temp.setsPriceDataCollection(sPriceDataCollection);
//			temp.setnPriceDataCollection(nPriceDataCollection);
//			
//			list.add(temp);
//		}
//		
//		return list;
//	}
	
}
