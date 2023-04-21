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
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
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
import capi.model.report.FRAdjustment;
import capi.model.report.FRAdjustmentCriteria;
import capi.service.CommonService;

@Service("FRAdjustmentService")
public class FRAdjustmentService extends DataReportServiceBase{

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
		return "RF9011";
	}
	
	private static final String[] headers = {"No.","Indoor Quotation record ID","Field Quotation Record ID","Quotation ID","Reference Month",
			"Reference Date","Purpose","CPI based period","Variety Code","Variety English name","Quotation Status","Data Conversion Status",
			"Outlet Code",
			//"Outlet Name",
			"Outlet Type","Product ID","Country of Origin","Product Attributes 1","Product Attributes 2",
			"Product Attributes 3","Product Attributes 4","Product Attributes 5","Survey N Price","Survey S Price","Last Edited N Price",
			"Last Edited S Price","Previous Edited N Price","Previous Edited S Price","Current Edited N Price","Current Edited S Price",
			"N Price PR","S Price PR","Quotation record reason","Quotation record remarks","New Recruitment case","Product change",
			"Data Conversion Remarks","FR (field)","FR (field) is % or not","FR (admin)","FR (admin) is % or not","Field record's FR (field)",
			"Field record's FR (field) is % or not","Consignment counter",
			//"Consignment counter name",
			"Consignment remarks",
			"Applied admin/field FR in this month","FR applied in this season","First Return product type","Keep number",
			"Seasonal withdrawal","Seasonal item","Whether FR Admin is used","Last FR Applied Date","Whether fieldwork is needed"
	};

	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		FRAdjustmentCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), FRAdjustmentCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		
		List<FRAdjustment> progress = dao.getFRAdjustmentReport(
				criteria.getPurpose(), criteria.getItemId(), criteria.getOutletType(), criteria.getFrRequired(), criteria.getConsignmentCounter(), 
				criteria.getSeasonalWithdrawal(), startMonth, endMonth, criteria.getAppliedAdmin(), 
				criteria.getFrApplied(), criteria.getFirstReturn(), criteria.getDataCollection());
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (FRAdjustment data : progress){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(rowCnt);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIndoorQuotationRecordId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationRecordId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationId());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(format.format(data.getReferenceMonth()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(format2.format(data.getReferenceDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCpiBasePeriod());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnitEnglishName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQuotationStatus());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIndoorQuotationRecordStatus());

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
			if(data.getSurveyNPrice() != null)
				cell.setCellValue(data.getSurveyNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getSurveySPrice() != null)
				cell.setCellValue(data.getSurveySPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getLastNPrice() != null)
				cell.setCellValue(data.getLastNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getLastSPrice() != null)
				cell.setCellValue(data.getLastSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getPreviousNPrice() != null)
				cell.setCellValue(data.getPreviousNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getPreviousSPrice() != null)
				cell.setCellValue(data.getPreviousSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCurrentNPrice() != null)
				cell.setCellValue(data.getCurrentNPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCurrentSPrice() != null)
				cell.setCellValue(data.getCurrentSPrice());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCurrentNPrice() != null && data.getPreviousNPrice() != null) {
				BigDecimal num = BigDecimal.valueOf(data.getCurrentNPrice()).divide(BigDecimal.valueOf(data.getPreviousNPrice()),5,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).setScale(3, RoundingMode.HALF_UP).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getCurrentSPrice() != null && data.getPreviousSPrice() != null) {
				BigDecimal num = BigDecimal.valueOf(data.getCurrentSPrice()).divide(BigDecimal.valueOf(data.getPreviousSPrice()),5,RoundingMode.HALF_UP);
				cell.setCellValue(num.multiply(BigDecimal.valueOf(100)).setScale(3, RoundingMode.HALF_UP).doubleValue());
			}
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQrReason());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getQrRemark());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsNewRecruitment() != null)
				cell.setCellValue(data.getIsNewRecruitment() == 0?"N":"Y");
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsProductChange() != null)
				cell.setCellValue(data.getIsProductChange() == 0?"N":"Y");
			else
				cell.setCellValue("N");
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIqrRemark());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getFRField() != null)
				cell.setCellValue(data.getFRField());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsFRFieldPercentage() != null)
				cell.setCellValue(data.getIsFRFieldPercentage() == 0?false:true);
			else
				cell.setCellValue(false);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getFRAdmin() != null)
				cell.setCellValue(data.getFRAdmin());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsFRAdminPercentage() != null)
				cell.setCellValue(data.getIsFRAdminPercentage() == 0?false:true);
			else
				cell.setCellValue(false);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getFR() != null)
				cell.setCellValue(data.getFR());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsFRPercentage() != null)
				cell.setCellValue(data.getIsFRPercentage() == 0?false:true); //COL AM
			else
				cell.setCellValue(false);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsConsignmentCounter() != null)
				cell.setCellValue(data.getIsConsignmentCounter() == 0?false:true);
			else
				cell.setCellValue(false);
			
//			cellCnt++;
//			cell = row.createCell(cellCnt);
//			cell.setCellValue(data.getConsignmentCounterName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getConsignmentCounterRemark());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsApplyFR() != null)
				cell.setCellValue(data.getIsApplyFR() == 0?false:true);
			else
				cell.setCellValue(false);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsFRApplied() != null)  
				cell.setCellValue(data.getIsFRApplied() == 0?false:true);
			else
				cell.setCellValue(false);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsReturnNewGoods() != null)
				cell.setCellValue(data.getIsReturnNewGoods());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsKeepNum() != null)
				cell.setCellValue(data.getIsKeepNum() == 0? null :true);   //COL AX 
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSeasonalWithdraw());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSeasonality());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsUseFRAdmin() != null)
				cell.setCellValue(data.getIsUseFRAdmin() == 0?false:true);
			else
				cell.setCellValue("");
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getLastFRAppliedDate() != null)
				cell.setCellValue(format2.format(data.getLastFRAppliedDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			if(data.getIsNoField() != null)
				cell.setCellValue(data.getIsNoField() == 1?false:true);
			else
				cell.setCellValue(false);
			// Input Row of Data end
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
		
		FRAdjustmentCriteria criteria = (FRAdjustmentCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
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
		if (criteria.getOutletType() != null && criteria.getOutletType().size() > 0){
			descBuilder.append("\n");
			descBuilder.append(String.format("Outlet type: %s", StringUtils.join(criteria.getOutletType(), ", ")));
		}
		if(criteria.getFrRequired() != null && criteria.getFrRequired().length() > 0) {
			descBuilder.append("\n");
			if("Y".equals(criteria.getFrRequired())) descBuilder.append(String.format("FR required: %s", "Y"));
			else if("N".equals(criteria.getFrRequired())) descBuilder.append(String.format("FR required: %s", "N"));
		}
		if(criteria.getConsignmentCounter() != null && criteria.getConsignmentCounter().length() > 0) {
			descBuilder.append("\n");
			if("Y".equals(criteria.getConsignmentCounter())) descBuilder.append(String.format("Consignment counter: %s", "Y"));
			else if("N".equals(criteria.getConsignmentCounter())) descBuilder.append(String.format("Consignment counter: %s", "N"));
		}
		if(criteria.getSeasonalWithdrawal() != null && criteria.getSeasonalWithdrawal().length() > 0) {
			descBuilder.append("\n");
			if("Y".equals(criteria.getSeasonalWithdrawal())) descBuilder.append(String.format("Seasonal withdrawal: %s", "Y"));
			else if("N".equals(criteria.getSeasonalWithdrawal())) descBuilder.append(String.format("Seasonal withdrawal: %s", "Blank"));
		}
		if(criteria.getAppliedAdmin() != null && criteria.getAppliedAdmin().length() > 0) {
			descBuilder.append("\n");
			if("Y".equals(criteria.getAppliedAdmin())) descBuilder.append(String.format("Applied admin/field FR in this month: %s", "Y"));
			else if("N".equals(criteria.getAppliedAdmin())) descBuilder.append(String.format("Applied admin/field FR in this month: %s", "Blank"));
		}
		if(criteria.getFirstReturn() != null && criteria.getFirstReturn().length() > 0) {
			descBuilder.append("\n");
			if("New".equals(criteria.getFirstReturn())) descBuilder.append(String.format("First return product type: %s", "New"));
			else if("Same".equals(criteria.getFirstReturn())) descBuilder.append(String.format("First return product type: %s", "Same"));
			else if("NA".equals(criteria.getFirstReturn())) descBuilder.append(String.format("First return product type: %s", "NA"));
		}
		if(criteria.getDataCollection() != null && criteria.getDataCollection().length() > 0) {
			descBuilder.append("\n");
			if("Y".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data collection: %s", "Field"));
			else if("N".equals(criteria.getDataCollection())) descBuilder.append(String.format("Data collection: %s", "No Field"));
		}
		if(criteria.getFrApplied() != null && criteria.getFrApplied().length() > 0) {
			descBuilder.append("\n");
			if("Y".equals(criteria.getFrApplied())) descBuilder.append(String.format("FR applied in this season: %s", "Y"));
			else if("N".equals(criteria.getFrApplied())) descBuilder.append(String.format("FR applied in this season: %s", "Blank"));
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
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}	
	}

}
