package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AllocationBatchDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationRecordDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.AllocationBatch;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.SurveyMonth;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.FieldworkOutputByOutletTypeCriteria;
import capi.model.report.FieldworkOutputByOutletTypeReport;
import capi.model.report.ManDayAvailable;
import capi.service.CommonService;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;

@Service("FieldworkOutputByOutletTypeService")
public class FieldworkOutputByOutletTypeService extends DataReportServiceBase{
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private AllocationBatchDao allocationBatchDao;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private SurveyMonthService surveyMonthService;
	
	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9044";
	}
	
	private static final String[] headers = new String[] {
			"Reference Month", "Purpose", "Allocation Batch", "Collection Method", "Team",
			"Post", "Field Officer Code", "Field Officer Name", "Man-day available", "Total man-day required for the assignments",
			"Outlet Type Code", "Outlet Type English Name", "Total no. of Assignments", "Total no. of Quotation Records"
			};
	
	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		FieldworkOutputByOutletTypeCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), FieldworkOutputByOutletTypeCriteria.class);
		
		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		
		SurveyMonth surveyMonth = surveyMonthService.getSurveyMonthByReferenceMonth(refMonth);
		Date refMonthStart = surveyMonth.getStartDate();
		Date refMonthEnd = surveyMonth.getEndDate();
		
		List<ManDayAvailable> manDays = allocationBatchDao.getManDayAvailableInMonth(refMonth);
		
		Map<Integer, Double> manDayMap = new Hashtable<Integer, Double>();
		for (ManDayAvailable data : manDays){
			manDayMap.put(data.getUserId(), data.getManDayAvailable());
		}
		
		StringBuilder b = new StringBuilder();
		String purpose = null, user = null, allocationBatch = null, outletType = null;
		
		if (criteria.getPurposeId() != null && criteria.getPurposeId().size() > 0){
			b.append("<query>");
			for(Integer i : criteria.getPurposeId()) {	
				b.append("<purposeId>"+i+"</purposeId>");
			}
			b.append("</query>");
			purpose = b.toString();
			b = new StringBuilder();
		}
		
		if (criteria.getOfficerId()!=null && criteria.getOfficerId().size() > 0){
			b.append("<query>");
			for(Integer i : criteria.getOfficerId()) {	
				b.append("<userId>"+i+"</userId>");
			}
			b.append("</query>");
			user = b.toString();
			b = new StringBuilder();
		} else if ((criteria.getAuthorityLevel() & 771) > 0) {
			b.append("<query>");
			for(Integer i : userDao.getReportLookupTableSelectAll("", 20, null, null, null, null, refMonthStart, refMonthEnd, null, 1)) {	
				b.append("<userId>"+i+"</userId>");
			}
			b.append("</query>");
			user = b.toString();
			b = new StringBuilder();		
		} else {
			b.append("<query>");
			for(Integer i : userDao.getReportLookupTableSelectAll("", 16, null, null, null, null, refMonthStart, refMonthEnd, new Integer[]{criteria.getUserId()}, 1)) {	
				b.append("<userId>"+i+"</userId>");
			}
			b.append("</query>");
			user = b.toString();
			b = new StringBuilder();	
		}
		
		if (criteria.getAllocationBatchId() !=null && criteria.getAllocationBatchId().size() > 0){
			b.append("<query>");
			for(Integer i : criteria.getAllocationBatchId()) {	
				b.append("<allocationBatchId>"+i+"</allocationBatchId>");
			}
			b.append("</query>");
			allocationBatch = b.toString();
			b = new StringBuilder();
		}
		
		if (criteria.getOutletTypeShortCode()!=null && criteria.getOutletTypeShortCode().size() > 0){
			b.append("<query>");
			for(String i : criteria.getOutletTypeShortCode()) {	
				b.append("<outletType>"+i+"</outletType>");
			}
			b.append("</query>");
			outletType = b.toString();
			b = new StringBuilder();
		}
		
		List<FieldworkOutputByOutletTypeReport> results = quotationRecordDao.getFieldworkOutletTypeReport(user
				, purpose, refMonth, allocationBatch, outletType);
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (FieldworkOutputByOutletTypeReport data : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBatchName());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCollectionMethod());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			if (data.getUserId() != null && manDayMap.containsKey(data.getUserId())){
				cell.setCellValue(manDayMap.get(data.getUserId()));
			}
			
			cellCnt = 9;
			cell = row.createCell(cellCnt);
			if (data.getManDayRequired() == null) {
				cell.setCellValue(0.0);
			} else {
				cell.setCellValue(data.getManDayRequired());
			}
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeCode());
			
			cellCnt = 11;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOutletTypeName());
			
			cellCnt = 12;
			cell = row.createCell(cellCnt);
			if (data.getCompletedAssignment()!=null){
				cell.setCellValue(data.getCompletedAssignment());
			}
			
			cellCnt = 13;
			cell = row.createCell(cellCnt);
			if (data.getCompletedQuotationRecord()!=null){
				cell.setCellValue(data.getCompletedQuotationRecord());
			}
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Field Output (by officer)");
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
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		FieldworkOutputByOutletTypeCriteria criteria = (FieldworkOutputByOutletTypeCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		List<Integer> officerId = criteria.getOfficerId();
		descBuilder.append("Field Staff: ");
		if(officerId != null){
			List<String> userStr = new ArrayList<String>();
			for(Integer uid: officerId){
				User user = userDao.findById(uid);
				String str = user.getStaffCode() + " - " + user.getEnglishName();
				userStr.add(str);
//				descBuilder.append(String.format("%s - %s, ", user.getStaffCode(), user.getChineseName()));
			}
			descBuilder.append(String.format("%s", StringUtils.join(userStr, ", ")));
		}else{
			descBuilder.append("-");
		}
		descBuilder.append("\n");
		descBuilder.append("Purpose: ");
		Purpose p = null;
		if(criteria.getPurposeId() != null){
			List<String> codes = new ArrayList<String>();
			for(Integer pid: criteria.getPurposeId()){
				p = this.purposeDao.findById(pid);
				if(p != null){
					codes.add(p.getCode());
				}
			}
			descBuilder.append(String.format("%s", StringUtils.join(codes, ", ")));
		}
		descBuilder.append("\n");
		descBuilder.append("Outlet Type: ");
		if(criteria.getOutletTypeShortCode() != null){
			descBuilder.append(String.format("%s", StringUtils.join(criteria.getOutletTypeShortCode(), ", ")));
		}
		descBuilder.append("\n");
		descBuilder.append("ReferenceMonth: " + criteria.getRefMonth());
		descBuilder.append("\n");
		descBuilder.append("Allocation Batch: ");
		AllocationBatch ab = null;
		if(criteria.getAllocationBatchId() != null){
			List<String> codes = new ArrayList<String>();
			for(Integer abid: criteria.getAllocationBatchId()){
				ab = this.allocationBatchDao.findById(abid);
				if(ab != null){
					codes.add(ab.getBatchName());
				}
			}
			descBuilder.append(String.format("%s", StringUtils.join(codes, ", ")));
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
