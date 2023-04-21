package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.IndoorQuotationRecordDao;
import capi.dal.ItemDao;
import capi.dal.PurposeDao;
import capi.dal.ReportTaskDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.Item;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.QuotationRecordImputationReport;
import capi.model.report.QuotationRecordImputationReportCriteria;
import capi.service.CommonService;

@Service("QuotationRecordImputationReportService")
public class QuotationRecordImputationReportService extends DataReportServiceBase{

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
	private ItemDao itemDao;
	
	@Autowired
	private IndoorQuotationRecordDao indoorQuotationRecordDao;
	
	private final static String[] headers = { "No.", "Indoor Quotation record ID", "Quotation ID",
			"Field Quotation record ID", "Reference Month", "Reference Date", "Purpose", "CPI base period",
			"Variety Code", "Variety Chinese Name", "Variety English Name", "Quotation Status",
			"Data Conversion Status", "Quotation Record Sequence (Order of collection date)", "Outlet Code",
			//"Outlet Name",
			"Outlet Type", "Product ID", "Country of Origin", "Product Attributes 1",
			"Product Attributes 2", "Product Attributes 3", "Product Attributes 4", "Product Attributes 5",
			"Availability", "Survey N Price", "Survey S Price", "Last Edited N Price", "Last Edited S Price",
			"Previous Edited N Price", "Previous Edited S Price", "Current Edited N Price", "Current Edited S Price",
			"N Price PR", "S Price PR", "Reason", "Price Remarks", "Whether fieldwork is needed",
			"Data Conversion Remarks", "Last Quotation Average price", "Imputed Quotation Average price",
			"Current Quotation Average price", "Current vs Last Quotation Average price PR",
			"Current vs Imputed Last Quotation Average price PR", "Remark of Imputed Quotation Average price",
			"Last Variety Average price", "Imputed Variety Average price", "Current Variety Average price",
			"Current vs Last Variety Average price PR", "Current vs Imputed Last Variety Average price PR",
			"Remark of Imputed Variety Average price", "Compilation Method" };
	
	private static final String[] availabilityStatuses = {
			"Available", "IP", "有價無貨", "缺貨", "Not Suitable", "回倉",
			"無團", "未返", "New"
	};
	
	@Override
	public String getFunctionCode() {
		return "RF9048";
	}

	@Override
	public void generateReport(Integer taskId) throws Exception{
		
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		QuotationRecordImputationReportCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), QuotationRecordImputationReportCriteria.class);
		
		Date startMonth = null;
		Date endMonth = null;
		String plainStartMonth = criteria.getStartMonth();
		String plainEndMonth = criteria.getEndMonth();
		
		if(!"".equals(plainStartMonth) && !"".equals(plainEndMonth)) {
			startMonth = commonService.getMonth(criteria.getStartMonth());
			endMonth = commonService.getMonth(criteria.getEndMonth());
		}
		
		List<QuotationRecordImputationReport> data = indoorQuotationRecordDao.getQuotationRecordImputationReport(
				startMonth, endMonth, criteria.getPurpose(), criteria.getUnitId(), criteria.getQuotationId());

		SXSSFWorkbook workbook = prepareWorkbook();
		SXSSFSheet sheet = workbook.getSheetAt(0);

		int rowIndex = 1;
	
		for (QuotationRecordImputationReport item : data) {
			SXSSFRow row = sheet.createRow(rowIndex);
			int cellIndex = 0;
			SXSSFCell cell = row.createCell(cellIndex);
			
			cell.setCellValue(rowIndex);

			cellIndex = 1;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getIndoorQuotationRecordId());
			
			cellIndex = 2;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getQuotationId());
			
			cellIndex = 3;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getFieldQuotationRecordId());
			
			cellIndex = 4;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getReferenceMonth());
			
			cellIndex = 5;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getReferenceDate());
			
			cellIndex = 6;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getPurpose());
			
			cellIndex = 7;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getCpiBasePeriod());
			
			cellIndex = 8;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getVarietyCode());
			
			cellIndex = 9;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getVarietyChineseName());
			
			cellIndex = 10;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getVarietyEnglishName());
			
			cellIndex = 11;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getQuotationStatus());
			
			cellIndex = 12;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getDataConversionStatus());
			
			cellIndex = 13;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getQuotationRecordSequence());
			
			cellIndex = 14;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getOutletCode());
			
//			cellIndex = 15;
//			cell = row.createCell(cellIndex);
//			cell.setCellValue(item.getOutletName());
			
			cellIndex = 15;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getOutletType());
			
			cellIndex = 16;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getProductId());
			
			cellIndex = 17;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getCountryOfOrigin());
			
			cellIndex = 18;
			cell = row.createCell(cellIndex);
			cell.setCellValue(/*valueOrEmpty(*/item.getProductAttributes1()/*)*/);
			
			cellIndex = 19;
			cell = row.createCell(cellIndex);
			cell.setCellValue(/*valueOrEmpty(*/item.getProductAttributes2()/*)*/);
			
			cellIndex = 20;
			cell = row.createCell(cellIndex);
			cell.setCellValue(/*valueOrEmpty(*/item.getProductAttributes3()/*)*/);
			
			cellIndex = 21;
			cell = row.createCell(cellIndex);
			cell.setCellValue(/*valueOrEmpty(*/item.getProductAttributes4()/*)*/);
			
			cellIndex = 22;
			cell = row.createCell(cellIndex);
			cell.setCellValue(/*valueOrEmpty(*/item.getProductAttributes5()/*)*/);
			
			cellIndex = 23;
			cell = row.createCell(cellIndex);
			cell.setCellValue(availabilityString(item.getAvailability()));
			
			cellIndex = 24;
			cell = row.createCell(cellIndex);
			if(item.getSurveyNPrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getSurveyNPrice()/*)*/);
			
			cellIndex = 25;
			cell = row.createCell(cellIndex);
			if(item.getSurveySPrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getSurveySPrice()/*)*/);
			
			cellIndex = 26;
			cell = row.createCell(cellIndex);
			if(item.getLastEditedNPrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getLastEditedNPrice()/*)*/);
			
			cellIndex = 27;
			cell = row.createCell(cellIndex);
			if(item.getLastEditedSPrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getLastEditedSPrice()/*)*/);
			
			cellIndex = 28;
			cell = row.createCell(cellIndex);
			if(item.getPreviousEditedNPrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getPreviousEditedNPrice()/*)*/);
			
			cellIndex = 29;
			cell = row.createCell(cellIndex);
			if(item.getPreviousEditedSPrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getPreviousEditedSPrice()/*)*/);
			
			cellIndex = 30;
			cell = row.createCell(cellIndex);
			if(item.getCurrentEditedNPrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getCurrentEditedNPrice()/*)*/);
			
			cellIndex = 31;
			cell = row.createCell(cellIndex);
			if(item.getCurrentEditedSPrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getCurrentEditedSPrice()/*)*/);
			
			cellIndex = 32;
			cell = row.createCell(cellIndex);
			if(item.getnPricePR() != null)
				cell.setCellValue(/*valueOrZero(*/item.getnPricePR()/*)*/);
			
			cellIndex = 33;
			cell = row.createCell(cellIndex);
			if(item.getsPricePR() != null)
				cell.setCellValue(/*valueOrZero(*/item.getsPricePR()/*)*/);
			
			cellIndex = 34;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getReason());
			
			cellIndex = 35;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getPriceRemarks());
			
			cellIndex = 36;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getWhetherFieldworkIsNeeded());
			
			cellIndex = 37;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getDataConversionRemarks());
			
			cellIndex = 38;
			cell = row.createCell(cellIndex);
			if(item.getLastQuotationAveragePrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getLastQuotationAveragePrice()/*)*/);
			
			cellIndex = 39;
			cell = row.createCell(cellIndex);
			if(item.getImputedLastQuotationAveragePrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getImputedLastQuotationAveragePrice()/*)*/);
			
			cellIndex = 40;
			cell = row.createCell(cellIndex);
			if(item.getCurrentQuotationAveragePrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getCurrentQuotationAveragePrice()/*)*/);
			
			cellIndex = 41;
			cell = row.createCell(cellIndex);
			if(item.getCurrentVSLastQuotationAveragePricePR() != null)
				cell.setCellValue(/*valueOrZero(*/item.getCurrentVSLastQuotationAveragePricePR()/*)*/);
			
			cellIndex = 42;
			cell = row.createCell(cellIndex);
			if(item.getCurrentVSImputedLastQuotationAveragePricePR() != null)
				cell.setCellValue(/*valueOrZero(*/item.getCurrentVSImputedLastQuotationAveragePricePR()/*)*/);
			
			cellIndex = 43;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getRemarkOfImputedLastQuotationAveragePrice());
			
			cellIndex = 44;
			cell = row.createCell(cellIndex);
			if(item.getLastVarietyAveragePrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getLastVarietyAveragePrice()/*)*/);
			
			cellIndex = 45;
			cell = row.createCell(cellIndex);
			if(item.getImputedLastVarietyAveragePrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getImputedLastVarietyAveragePrice()/*)*/);
			
			cellIndex = 46;
			cell = row.createCell(cellIndex);
			if(item.getCurrentVarietyAveragePrice() != null)
				cell.setCellValue(/*valueOrZero(*/item.getCurrentVarietyAveragePrice()/*)*/);
			
			cellIndex = 47;
			cell = row.createCell(cellIndex);
			if(item.getCurrentVSLastVarietyAveragePricePR() != null)
				cell.setCellValue(/*valueOrZero(*/item.getCurrentVSLastVarietyAveragePricePR()/*)*/);
			
			cellIndex = 48;
			cell = row.createCell(cellIndex);
			if(item.getCurrentVSImputedLastVarietyAveragePricePR() != null)
			cell.setCellValue(/*valueOrZero(*/item.getCurrentVSImputedLastVarietyAveragePricePR()/*)*/);
			
			cellIndex = 49;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getRemarkOfImputedLastVarietyAveragePrice());
			
			cellIndex = 50;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getCompilationMethod());
						
			// Manually control how rows are flushed to disk
			if (rowIndex % 2000 == 0) {
				sheet.flushRows();
			}
			
			rowIndex++;
		}

		
		workbook.setSheetName(workbook.getSheetIndex(sheet), "Quotation Imputation List");
		
		// Output Excel
		try {
			String filename = UUID.randomUUID().toString() + ".xlsx";
			String file = getFileBase() + "/" + filename;
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			
			workbook.write(fileOutputStream);
			workbook.close();
			
			task.setPath(this.getFileRelativeBase() + "/" + filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}
	
	private String availabilityString(Integer availability) {
		try {
			return availabilityStatuses[availability.intValue() - 1];
		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		QuotationRecordImputationReportCriteria criteria = (QuotationRecordImputationReportCriteria)criteriaObject;
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
		
		if (criteria.getUnitId() != null && criteria.getUnitId().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<Item> units = itemDao.getByIds(criteria.getUnitId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Item unit : units){
				codes.add(unit.getCode() + " - " + unit.getEnglishName());
			}
			descBuilder.append(String.format("Unit: %s", StringUtils.join(codes, ", ")));
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
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		}
		else{
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
		int cellIndex = 0;

		for (String header : headers) {
			SXSSFCell cell = row.createCell(cellIndex);
			cell.setCellValue(header);
			cellIndex++;
		}
	}
	
	/*private Double valueOrZero(Double rowData) {
		return rowData == null ? Double.parseDouble("0") : rowData;
	}*/
	
	/*private String valueOrEmpty(String rowData) {
		return StringUtils.equals(" & ", rowData) ? "" : rowData;
	}*/
}
