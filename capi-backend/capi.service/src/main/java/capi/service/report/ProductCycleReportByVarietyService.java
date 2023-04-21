package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ProductGroupDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationRecordDao;
import capi.dal.ReportTaskDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.entity.ProductGroup;
import capi.entity.Purpose;
import capi.entity.QuotationRecord;
import capi.entity.ReportTask;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.ProductCycleReportByVariety;
import capi.model.report.ProductCycleReportByVarietyCriteria;
import capi.service.CommonService;
import capi.service.assignmentManagement.QuotationRecordService;
import edu.emory.mathcs.backport.java.util.Collections;

@Service("ProductCycleReportByVarietyService")
public class ProductCycleReportByVarietyService extends DataReportServiceBase{

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
	private ProductGroupDao productGroupDao;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;

	@Autowired
	private SystemConfigurationDao systemConfigurationDao;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	private final static String[] headers = { "Product group", "Variety Code", "Variety Chin Name", "Variety Eng Name",
			"No of quotations", "Total no of times of product change by quotation", "Total no of Pricing month",
			"Product life Cycle" };
	
	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9042";
	}

	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		ProductCycleReportByVarietyCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), ProductCycleReportByVarietyCriteria.class);

		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		Calendar startMonthCalendar = Calendar.getInstance();
		startMonthCalendar.setTime(startMonth);
		Calendar endMonthCalendar = Calendar.getInstance();
		endMonthCalendar.setTime(endMonth);
		
		List<ProductCycleReportByVariety> data = unitDao.getProductCycleReportByVariety(criteria.getUnitId(), criteria.getCpiSurveyForm(), criteria.getPurpose(), criteria.getProductGroup(), startMonth, endMonth);
		List<ProductCycleReportByVariety> newDataList = new ArrayList<>();
		Map<String, ProductCycleReportByVariety> mergedData = new HashMap<>();

		int countDuplicate = 1;
		String duplicateId = "";
		
		for(ProductCycleReportByVariety item : data) {
//			if(mergedData.containsKey(item.getVarietyCode())) {
//				ProductCycleReportByVariety existingItem = mergedData.get(item.getVarietyCode());
			if(mergedData.containsKey(item.getProductGroup()+item.getVarietyCode())) {
				ProductCycleReportByVariety existingItem = mergedData.get(item.getProductGroup()+item.getVarietyCode());
				countDuplicate ++;
				duplicateId = item.getProductGroup()+item.getVarietyCode();
				
				Integer oldNoOfQuotations = existingItem.getNoOfQuotations();
				Integer oldProductChange = existingItem.getProductChange();
				Integer oldPricingMonth = existingItem.getTotalNoOfPricingMonth();
				
				Integer newNoOfQuotations = item.getNoOfQuotations();
				Integer newProductChange = item.getProductChange();
				Integer newPricingMonth = item.getTotalNoOfPricingMonth();
				
				existingItem.setNoOfQuotations(oldNoOfQuotations + newNoOfQuotations);
				existingItem.setProductChange(oldProductChange + newProductChange);
				existingItem.setTotalNoOfPricingMonth(oldPricingMonth + newPricingMonth);
			} else {
//				mergedData.put(item.getVarietyCode(), item);
				if(duplicateId.length() > 0 && countDuplicate > 1){
					ProductCycleReportByVariety existingItem = mergedData.get(duplicateId);
					Integer oldNoOfQuotations = existingItem.getNoOfQuotations();					
					Integer newNoOfQuotations = oldNoOfQuotations/countDuplicate;
					existingItem.setNoOfQuotations(newNoOfQuotations);
				}
				
				countDuplicate = 1;
				mergedData.put(item.getProductGroup()+item.getVarietyCode(), item);
			}
		}
		
		for(Map.Entry<String, ProductCycleReportByVariety> entry : mergedData.entrySet()) {
			newDataList.add(entry.getValue());
		}
		
		Collections.sort(newDataList, new Comparator<ProductCycleReportByVariety>() {
			@Override
			public int compare(ProductCycleReportByVariety o1, ProductCycleReportByVariety o2) {
				return o1.getProductGroup().compareTo(o2.getProductGroup());
			}
		});
		
		SXSSFWorkbook workbook = prepareWorkbook();
		SXSSFSheet sheet = workbook.getSheetAt(0);
		
		int rowIndex = 1;
		
		for(ProductCycleReportByVariety item : newDataList) {
			SXSSFRow row = sheet.createRow(rowIndex);
			int cellIndex = 0;
			SXSSFCell cell = row.createCell(cellIndex);
			cell.setCellValue(item.getProductGroup());
			
			cellIndex = 1;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getVarietyCode());
			
			cellIndex = 2;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getVarietyChineseName());
			
			cellIndex = 3;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getVarietyEnglishName());
			
			cellIndex = 4;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getNoOfQuotations());
			
			cellIndex = 5;
			cell = row.createCell(cellIndex);
			cell.setCellValue(item.getProductChange());
			
			cellIndex = 6;
			cell = row.createCell(cellIndex);
			if(item.getTotalNoOfPricingMonth() != null) 
			cell.setCellValue(item.getTotalNoOfPricingMonth());
			
			cellIndex = 7;
			cell = row.createCell(cellIndex);
			if(item.getTotalNoOfPricingMonth() != null && item.getProductChange() != null) {		
				try {
					BigDecimal n = BigDecimal.valueOf(item.getTotalNoOfPricingMonth())
		                       .divide(BigDecimal.valueOf(item.getProductChange()), 7, RoundingMode.HALF_UP)
		                       .setScale(4, BigDecimal.ROUND_HALF_EVEN);
//					cell.setCellValue(n.toString());
					cell.setCellValue(n.doubleValue());
				} catch (Exception e) {
					cell.setCellValue(0);
				}
			} else {
				cell.setCellValue(0);
			}
			
			if(rowIndex % 2000 == 0) {
				sheet.flushRows();
			}
			
			rowIndex++;
			
		}
		
		workbook.setSheetName(workbook.getSheetIndex(sheet), "Product Cycle Report (by Variety)");
		
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
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		ProductCycleReportByVarietyCriteria criteria = (ProductCycleReportByVarietyCriteria)criteriaObject;
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
			List<Unit> units = unitDao.getByIds(criteria.getUnitId().toArray(new Integer[0]));
			List<String> codes = new ArrayList<String>();
			for (Unit unit : units){
				codes.add(unit.getCode() + " - " + unit.getEnglishName());
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

		if (criteria.getProductGroup() != null && criteria.getProductGroup().size() > 0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<ProductGroup> entities = productGroupDao.getProductGroupsByIds(criteria.getProductGroup());
			List<String> codes = new ArrayList<String>();
			for (ProductGroup item : entities){
				codes.add(item.getCode() + " - " + item.getChineseName());
			}
			descBuilder.append(String.format("Product Group: %s", StringUtils.join(codes, ", ")));
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
		
		for(String header: headers) {
			SXSSFCell cell = row.createCell(cellIndex);
			cell.setCellValue(header);
			cellIndex++;
		}
		
	}
}
