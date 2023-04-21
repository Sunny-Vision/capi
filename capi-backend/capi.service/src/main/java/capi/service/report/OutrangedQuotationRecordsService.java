package capi.service.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.Item;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.OutrangedQuotationRecords;
import capi.model.report.OutrangedQuotationRecordsCriteria;
import capi.service.CommonService;

@Service("OutrangedQuotationRecordsService")
public class OutrangedQuotationRecordsService extends DataReportServiceBase{

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

	@Override
	public String getFunctionCode() {
		return "RF9005";
	}

	private static final String[] headers = new String[]{
			"Purpose", "Reference Month", "Reference Date","CPI based period","Group Code","Group name","Item Code","Item name","Variety Code","Variety name",
			"Quotation ID", "Field Quotation record ID", "Indoor Quotation record ID", "Outlet Code",
			//"Outlet Name",
			"District Code","Outlet Type","Outlet Type English Name",
			"Product id","Country of Origin","Product Attributes 1","Product Attributes 2","Product Attributes 3","Product Attributes 4","Product Attributes 5",
			"Last Edited N Price","Last Edited S Price","Preivous Edited N Price","Preivous Edited S Price","Current Edited N Price","Current Edited S Price",
			"N Price PR","S Price PR","Data Conversion Remarks","Field price remarks","Field availability","Product change","New recruitment","FR applied",
			"CPI Compilation Series","Seasonality","Price info in Unit level (μ)","Price info in Unit level (σ)","Price info in Unit level (3σ)",
			"Price info in Unit level (Count)","PR info in Unit level (μ)","PR info in Unit level (σ)","PR info in Unit level (3σ)","PR info in Unit level (Count)",
			"Keep number","Allocated Indoor officer","Whether fieldwork is needed","CPI Quotation Type","Batch code"
		};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
				
		OutrangedQuotationRecordsCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), OutrangedQuotationRecordsCriteria.class);
		
		List<OutrangedQuotationRecordsCriteria.CriteriaList> criteriaLists = criteria.getCriteriaList();
		StringBuilder sb = new StringBuilder();
		if(criteriaLists != null && criteriaLists.size() > 0) {
			for(OutrangedQuotationRecordsCriteria.CriteriaList criteriaList : criteriaLists) {
				if(criteriaList.getOpen() != null) sb.append(criteriaList.getOpen() + " ");				
				if(criteriaList.getOperator() != null) sb.append("round(iqr.CurrentSPrice / case when iqr.PreviousSPrice <= 0 then null else iqr.PreviousSPrice end * 100, 3)");
				sb.append(criteriaList.getOperator() + " ");
				sb.append(criteriaList.getValue() + " ");
				if(criteriaList.getClose() != null) sb.append(criteriaList.getClose() + " ");
				if(criteriaList.getLogic() != null) sb.append(criteriaList.getLogic() + " ");
			}
			sb.append("and iqr.currentSPrice is not null and iqr.previousSPrice is not null ");
		}
		String str = sb.toString();
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		String refMonth = commonService.formatDateStr(startMonth);
		
		List<OutrangedQuotationRecords> results = dao.getOutrangedQuotationRecordsReport(
				criteria.getPurpose(), criteria.getItemId(), criteria.getCpiSurveyForm(), startMonth,
				refMonth, criteria.getDataCollection(), str);

		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (OutrangedQuotationRecords data : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getPurpose());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getReferenceMonth());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getReferenceDate());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getGroupCode());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getGroupName());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getItemCode());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getItemName());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getUnitCode());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getUnitName());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getQuotationId());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getQuotationRecordId());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getIndoorQuotationRecordId());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getOutletCode());
			
			//cell = row.createCell(cellCnt++);
			//cell.setCellValue(data.getOutletName());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getDistrict());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getOutletTypeCode());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getOutletTypeName());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getProductId());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getCountryOfOrigin());
			
			cell = row.createCell(cellCnt++);
			if(data.getPa1Value() != null)
				cell.setCellValue(data.getPa1Value()+" : "+(data.getPs1Value() == null?"":data.getPs1Value()));
			
			cell = row.createCell(cellCnt++);
			if(data.getPa2Value() != null)
				cell.setCellValue(data.getPa2Value()+" : "+(data.getPs2Value() == null?"":data.getPs2Value()));
			
			cell = row.createCell(cellCnt++);
			if(data.getPa3Value() != null)
				cell.setCellValue(data.getPa3Value()+" : "+(data.getPs3Value() == null?"":data.getPs3Value()));
			
			cell = row.createCell(cellCnt++);
			if(data.getPa4Value() != null)
				cell.setCellValue(data.getPa4Value()+" : "+(data.getPs4Value() == null?"":data.getPs4Value()));
			
			cell = row.createCell(cellCnt++);
			if(data.getPa5Value() != null)
				cell.setCellValue(data.getPa5Value()+" : "+(data.getPs5Value() == null?"":data.getPs5Value()));
			
			cell = row.createCell(cellCnt++);
			if(data.getLastNPrice() != null)
				cell.setCellValue(data.getLastNPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getLastSPrice() != null)
				cell.setCellValue(data.getLastSPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getPreviousNPrice() != null)
				cell.setCellValue(data.getPreviousNPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getPreviousSPrice() != null)
				cell.setCellValue(data.getPreviousSPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getCurrentNPrice() != null)
				cell.setCellValue(data.getCurrentNPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getCurrentSPrice() != null)
				cell.setCellValue(data.getCurrentSPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getCurrentNPrice() != null && data.getPreviousNPrice() != null) {
				BigDecimal number = BigDecimal.valueOf(data.getCurrentNPrice()).divide(BigDecimal.valueOf(data.getPreviousNPrice()), 5,RoundingMode.HALF_UP);
				cell.setCellValue(number.multiply(BigDecimal.valueOf(100)).doubleValue());
			}
			
			cell = row.createCell(cellCnt++);
			if(data.getCurrentSPrice() != null && data.getPreviousSPrice() != null) {
				BigDecimal number = BigDecimal.valueOf(data.getCurrentSPrice()).divide(BigDecimal.valueOf(data.getPreviousSPrice()), 5,RoundingMode.HALF_UP);
				cell.setCellValue(number.multiply(BigDecimal.valueOf(100)).doubleValue());
			}
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getIndoorRemark());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getQrRemark());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getAvailability());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue("1".equals(data.getIsProductChange()) ? "TRUE" : "FALSE");
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue("1".equals(data.getIsNewRecruitment()) ? "TRUE" : "FALSE");
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue("1".equals(data.getFrApplied()) ? "TRUE" : "FALSE");
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getCpiCompilationSeries());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getuSeasonality());
			
			cell = row.createCell(cellCnt++);
			if(data.getAverageCurrentSPrice() != null)
				cell.setCellValue(data.getAverageCurrentSPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getStandardDeviationSPrice() != null)
				cell.setCellValue(data.getStandardDeviationSPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getStandardDeviationSPrice() != null)
				cell.setCellValue(data.getStandardDeviationSPrice()*3);
			
			cell = row.createCell(cellCnt++);
			if(data.getCountCurrentSPrice() != null)
				cell.setCellValue(data.getCountCurrentSPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getAveragePRSPrice() != null)
				cell.setCellValue(data.getAveragePRSPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getStandardDeviationPRSPrice() != null)
				cell.setCellValue(data.getStandardDeviationPRSPrice());
			
			cell = row.createCell(cellCnt++);
			if(data.getStandardDeviationPRSPrice() != null)
			cell.setCellValue(data.getStandardDeviationPRSPrice()*3);
			
			cell = row.createCell(cellCnt++);
			if(data.getCountPRSPrice() != null)
				cell.setCellValue(data.getCountPRSPrice());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getKeepNumber());
			
			cell = row.createCell(cellCnt++);
			if (data.getStaffCode()!=null){
				cell.setCellValue(data.getStaffCode() + " - " + data.getStaffName());
			}
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue("1".equals(data.getNoField()) ? "FALSE" : "TRUE");
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getCpiQuotationType());
			
			cell = row.createCell(cellCnt++);
			cell.setCellValue(data.getBatch());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Outrange list");
		
		// End Generate Excel
		
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
		
		OutrangedQuotationRecordsCriteria criteria = (OutrangedQuotationRecordsCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s ", criteria.getStartMonth()));
		
		if (criteria.getPurpose() != null && criteria.getPurpose().size() > 0){
			descBuilder.append("\n");
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
			descBuilder.append("\n");
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
		
		if(criteria.getDataCollection() != null && criteria.getDataCollection().length() > 0) {
			descBuilder.append("\n");
			if("Y".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data Collection: %s", "Field"));
			else if("N".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data Collection: %s", "No Field"));
		}
		
		List<OutrangedQuotationRecordsCriteria.CriteriaList> criteriaLists = criteria.getCriteriaList();
		StringBuilder descriptionSB = new StringBuilder();
		if(criteriaLists != null && criteriaLists.size() > 0) {
			for(OutrangedQuotationRecordsCriteria.CriteriaList criteriaList : criteriaLists) {
				if(criteriaList.getOpen() != null) descriptionSB.append(criteriaList.getOpen() + " ");
				if(criteriaList.getOperator() != null) descriptionSB.append("PR ");
				descriptionSB.append(criteriaList.getOperator() + " ");
				descriptionSB.append(criteriaList.getValue() + " ");
				if(criteriaList.getClose() != null) descriptionSB.append(criteriaList.getClose() + " ");
				if(criteriaList.getLogic() != null) descriptionSB.append(criteriaList.getLogic() + " ");
			}
		}
		String descriptionSTR = descriptionSB.toString();
		if(!StringUtils.isEmpty(descriptionSTR)) {
			descBuilder.append("\n");
			descBuilder.append(String.format("Filtering Criteria: %s", descriptionSTR));
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
	public void createHeader(SXSSFRow row){
		int cnt = 0;
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
	}

}
