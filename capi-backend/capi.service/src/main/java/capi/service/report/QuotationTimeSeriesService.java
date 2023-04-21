package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
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
import capi.model.report.QuotationTimeSeriesCriteria;
import capi.model.report.QuotationTimeSeriesUnitStat;
import capi.service.CommonService;

@Service("QuotationTimeSeriesService")
public class QuotationTimeSeriesService extends DataReportServiceBase{

	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private UnitStatisticDao unitStatisticDao;
	
	@Override
	public String getFunctionCode() {
		return "RF9023";
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
			"New Recruitment case",
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
			"Quotation's Average Price: T-2",
			"Quotation's Average Price: T-1",
			"Quotation's Average Price: Current Month (T)",
			"Quotation's Average Price's PR: T-1",
			"Quotation's Average Price's PR: Current Month (T)",
			"Quotation's Standard deviation: T-1",
			"Quotation's Standard deviation: Current Month (T)",
			"Quotation's Min Price: T-1",
			"Quotation's Min Price: Current Month (T)",
			"Quotation's Max Price: T-1",
			"Quotation's Max Price: Current Month (T)",
			"Quotation's Sum:  T-1",
			"Quotation's Sum:  Current Month (T)",
			"Quotation's Count: T-1",
			"Quotation's Count: Current Month (T)",
			"Variety's Average Price: T-2",
			"Variety's Average Price: T-1",
			"Variety's Average Price: Current Month (T)",
			"Variety's Average Price's PR: T-1",
			"Variety's Average Price's PR: Current Month (T)",
			"Variety's Standard deviation: T-1",
			"Variety's Standard deviation: Current Month (T)",
			"Variety's Min Price: T-1",
			"Variety's Min Price: Current Month (T)",
			"Variety's Max Price: T-1",
			"Variety's Max Price: Current Month (T)",
			"Variety's Sum:  T-1",
			"Variety's Sum:  Current Month (T)",
			"Variety's Count: T-1",
			"Variety's Count: Current Month (T)",
			"Variety's Price relative (AM/GM) T-1",
			"Variety's Price relative (AM/GM) Current Month (T)",
			"Variety's No. of PR for calculate PR(GM) T-1",
			"Variety's No. of PR for calculate PR(GM) Current Month (T)"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		QuotationTimeSeriesCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), QuotationTimeSeriesCriteria.class);
		
		Date periodReferenceMonth = commonService.getMonth(criteria.getPeriodReferenceMonth());


		List<QuotationTimeSeriesUnitStat> results = unitStatisticDao.getQuotationTimeSeriesUnitStat
				(criteria.getPurpose(), criteria.getItemId(), criteria.getCpiSurveyForm(), periodReferenceMonth, criteria.getQuotationIds());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		CellStyle center = workBook.createCellStyle();
		center.setAlignment(CellStyle.ALIGN_CENTER);
		
		int rowCnt = 1;
				
		for (QuotationTimeSeriesUnitStat data : results){
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
			if (data.getIsNoField().equals("TRUE"))
				cell.setCellValue(true);
			else
				cell.setCellValue(false);
			
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
			//if(data.getFinalPRSPrice2() != null && data.getFinalPRSPrice1() != null) {
			if(data.getFinalPRSPrice1() != null) {
				/* 9023 - CAPI_Batch4b_comment_v12
				BigDecimal n = BigDecimal.valueOf(data.getFinalPRSPrice1())
						.divide(BigDecimal.valueOf(data.getFinalPRSPrice2()), 5, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100))
						.setScale(3, BigDecimal.ROUND_HALF_EVEN);
				cell.setCellValue(n.toString());*/
				//cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice2()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice1()).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
//			if(data.getFinalPRSPrice() != null && data.getFinalPRSPrice1() != null) {
			if(data.getFinalPRSPrice() != null) {	
				/* 9023 - CAPI_Batch4b_comment_v12
				BigDecimal n = BigDecimal.valueOf(data.getFinalPRSPrice())
						.divide(BigDecimal.valueOf(data.getFinalPRSPrice1()), 5, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100))
						.setScale(3, BigDecimal.ROUND_HALF_EVEN);
				cell.setCellValue(n.toString());*/
				cell.setCellValue(BigDecimal.valueOf(data.getFinalPRSPrice()).doubleValue());
			}

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
			if(data.getMinSPrice1()!=null) 
			cell.setCellValue(data.getMinSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getMinSPrice()!=null) 
			cell.setCellValue(data.getMinSPrice());

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
			if(data.getSumCurrentSPrice1()!=null) 
			cell.setCellValue(data.getSumCurrentSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSumCurrentSPrice()!=null) 
			cell.setCellValue(data.getSumCurrentSPrice());

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
//			if(data.getUsAveragePRSPrice2()!=null&&data.getUsAveragePRSPrice1()!=null) { 
			if(data.getUsAveragePRSPrice1()!=null) { 
				/* 9023 - CAPI_Batch4b_comment_v12
				BigDecimal n = BigDecimal.valueOf(data.getUsAveragePRSPrice1())
						.divide(BigDecimal.valueOf(data.getUsAveragePRSPrice2()), 5, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100))
						.setScale(3, BigDecimal.ROUND_HALF_EVEN);
				cell.setCellValue(n.toString());*/
				//cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice2()).doubleValue());
				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice1()).doubleValue());
			}

			cellCnt++;
			cell = row.createCell(cellCnt);
//			if(data.getUsAveragePRSPrice()!=null&&data.getUsAveragePRSPrice1()!=null) { 
			if(data.getUsAveragePRSPrice()!=null) { 
				/* 9023 - CAPI_Batch4b_comment_v12
				BigDecimal n = BigDecimal.valueOf(data.getUsAveragePRSPrice())
						.divide(BigDecimal.valueOf(data.getUsAveragePRSPrice1()), 5, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100))
						.setScale(3, BigDecimal.ROUND_HALF_EVEN);
				cell.setCellValue(n.toString());*/
				cell.setCellValue(BigDecimal.valueOf(data.getUsAveragePRSPrice()).doubleValue());
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
			if(data.getUsFinalPRSPrice1()!=null) 
			cell.setCellValue(data.getUsFinalPRSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsFinalPRSPrice()!=null) 
			cell.setCellValue(data.getUsFinalPRSPrice());

			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountPRSPrice1()!=null) 
			cell.setCellValue(data.getUsCountPRSPrice1());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getUsCountPRSPrice()!=null) 
			cell.setCellValue(data.getUsCountPRSPrice());
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
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
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		QuotationTimeSeriesCriteria criteria = (QuotationTimeSeriesCriteria)criteriaObject;
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
			descBuilder.append(String.format("Item: %s", StringUtils.join(codes, ", ")));
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
		
		if (criteria.getQuotationIds() != null && criteria.getQuotationIds().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			for (Integer quotationId : criteria.getQuotationIds()){
				codes.add(quotationId.toString());
			}
			descBuilder.append(String.format("Quotation: %s", StringUtils.join(codes, ", ")));
		}
		
		if (descBuilder.length() > 0) descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s", criteria.getPeriodReferenceMonth()));		
		
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
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}


	

	
}
