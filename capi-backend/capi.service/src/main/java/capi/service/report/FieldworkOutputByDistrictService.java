package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.AllocationBatchDao;
import capi.dal.DistrictDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationRecordDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.AllocationBatch;
import capi.entity.District;
import capi.entity.Purpose;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.UserAccessModel;
import capi.model.report.FieldworkOutputByDistrictCriteria;
import capi.model.report.FieldworkOutputByDistrictReport;
import capi.model.report.ManDayAvailable;
import capi.service.CommonService;
import capi.service.UserService;

@Service("FieldworkOutputByDistrictService")
public class FieldworkOutputByDistrictService extends DataReportServiceBase {

	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private AllocationBatchDao allocationBatchDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Override
	public String getFunctionCode() {
		return "RF9041";
	}
	
	private static final String[] headers = new String[] {
			"Reference Month", "Survey", "Purpose", "Allocation Batch", "Collection Method", "Team",
			"Post", "Field Officer Code", "Field Officer Name", "Man-day available", "Total man-day required for the assignments"
			};
		
	@Override
	public void generateReport(Integer taskId) throws Exception {
		ReportTask task = reportTaskDao.findById(taskId);
		User creator = task.getUser();
		UserAccessModel uam = this.userService.gatherUserRequiredInfo(creator);
		uam.getOrgAuthorityLevel();
		
		if(StringUtils.isEmpty(task.getCriteriaSerialize())) {
			throw new RuntimeException("Criteria not defined");
		}
		
		FieldworkOutputByDistrictCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), FieldworkOutputByDistrictCriteria.class);

		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		
		Date start = refMonth;
		Date end = refMonth;
		start = DateUtils.addMonths(start, 1);
		start = DateUtils.addDays(start, -1);
		
		List<ManDayAvailable> manDays = allocationBatchDao.getManDayAvailableInMonth(refMonth);
		
		Map<Integer, Double> manDayMap = new Hashtable<Integer, Double>();
		for (ManDayAvailable data : manDays){
			manDayMap.put(data.getUserId(), data.getManDayAvailable());
		}
		
		Map<String, Integer> keyMap = new Hashtable<String, Integer>();
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		List<Integer> districtIds = new ArrayList<Integer>();
		if (criteria.getDistrictIds() != null && criteria.getDistrictIds().length > 0){
			districtIds = Arrays.asList(criteria.getDistrictIds());
		}
		
		List<String> districts = districtDao.getDistrictCodeByIds(districtIds);
		
		sheet = createDistrictHeader(sheet, districts, headers, keyMap);
		
		StringBuilder b = new StringBuilder();
		String purpose = null, user = null, allocationBatch = null, district = null;
		
		if(criteria.getPurposeIds() != null && criteria.getPurposeIds().length > 0) {
			b.append("<query>");
			for(Integer i : criteria.getPurposeIds()) {	
				b.append("<purposeId>"+i+"</purposeId>");
			}
			b.append("</query>");
			purpose = b.toString();
			b = new StringBuilder();
		}
		
		
		
		if(criteria.getOfficerIds() != null && criteria.getOfficerIds().length > 0) {
			b.append("<query>");
			for(Integer i : criteria.getOfficerIds()) {	
				b.append("<userId>"+i+"</userId>");
			}
			b.append("</query>");
			user = b.toString();
			b = new StringBuilder();
		} else {
			List<Integer> userIds = userDao.getReportLookupTableSelectAll("", 16, null, null, start, end, null, null, null, 1);
			b.append("<query>");
			for (Integer i : userIds){
				b.append("<userId>"+i+"</userId>");
			}
			b.append("</query>");
			user = b.toString();
			b = new StringBuilder();
		}
		
		if(criteria.getAllocationBatchIds() != null && criteria.getAllocationBatchIds().length > 0) {
			b.append("<query>");
			for(Integer i : criteria.getAllocationBatchIds()) {	
				b.append("<allocationBatchId>"+i+"</allocationBatchId>");
			}
			b.append("</query>");
			allocationBatch = b.toString();
			b = new StringBuilder();
		}
		
		if(criteria.getDistrictIds() != null && criteria.getDistrictIds().length > 0) {
			b.append("<query>");
			for(Integer i : criteria.getDistrictIds()) {	
				b.append("<districtId>"+i+"</districtId>");
			}
			b.append("</query>");
			district = b.toString();
			b = new StringBuilder();
		}
		
		List<FieldworkOutputByDistrictReport> results = quotationRecordDao.getFieldworkOutputByDistrictReport(user
				, purpose, refMonth, allocationBatch, district);
		
		int rowCnt = 0;
		
		Integer id = 0;
		int totalAssignment = 0;
		int totalQuotationRecord = 0;
		
		for (FieldworkOutputByDistrictReport data : results){
			int cellCnt = 0;
			SXSSFRow row;
			SXSSFCell cell;
			if (id!=null && data.getId() != null && id.equals(data.getId())){
				totalAssignment += data.getCountAssignment();
				totalQuotationRecord += data.getCountQuotationRecord();
				row = sheet.getRow(rowCnt);
				
				cellCnt = keyMap.get(data.getDistrict());
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getCountAssignment());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getCountQuotationRecord());
				
				cellCnt = keyMap.get("Total");
				cell = row.createCell(cellCnt);
				cell.setCellValue(totalAssignment);
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(totalQuotationRecord);
				
				cellCnt++;
				
				continue;
			}
			rowCnt++;
			
			id = data.getId();
			row = sheet.createRow(rowCnt);
			totalAssignment = data.getCountAssignment();
			totalQuotationRecord = data.getCountQuotationRecord();
			
			cellCnt = keyMap.get(data.getDistrict());
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCountAssignment());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCountQuotationRecord());
			
			cellCnt = keyMap.get("Total");
			cell = row.createCell(cellCnt);
			cell.setCellValue(totalAssignment);
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(totalQuotationRecord);
			
			cellCnt = 0;
			cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(data.getReferenceMonth()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPurpose());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBatchName());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCollectionMethod());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt = 7;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt = 8;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());
			
			cellCnt = 9;
			cell = row.createCell(cellCnt);
			if(data.getUserId() != null && manDayMap != null && manDayMap.get(data.getUserId()) != null) {
				cell.setCellValue(manDayMap.get(data.getUserId()));
			}
			
			cellCnt = 10;
			cell = row.createCell(cellCnt);
			if (data.getManDayRequired()!=null){
				cell.setCellValue(data.getManDayRequired());
			}
			
			// Input Row of Data end
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Summary (by officer)");
		// End Generate Sheet
		
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
		FieldworkOutputByDistrictCriteria criteria = (FieldworkOutputByDistrictCriteria) criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getOfficerIds() != null && criteria.getOfficerIds().length > 0) {
			List<User> users = userDao.getUsersByIds(Arrays.asList(criteria.getOfficerIds()));
			List<String> userNames = new ArrayList<String>();
			descBuilder.append("Officers: ");
			for (User user : users) {
				userNames.add(String.format("%s - %s", user.getStaffCode(), user.getEnglishName()));
			}
			descBuilder.append(StringUtils.join(userNames, ", "));
			descBuilder.append("\n");
		}
		
		if (criteria.getDistrictIds() != null && criteria.getDistrictIds().length > 0) {
			List<District> districts = districtDao.getDistrictsByIds(Arrays.asList(criteria.getDistrictIds()));
			List<String> districtNames = new ArrayList<String>();
			descBuilder.append("District: ");
			for (District district : districts) {
				districtNames.add(district.getEnglishName());
			}
			descBuilder.append(StringUtils.join(districtNames, ", "));
			descBuilder.append("\n");
		}
		
		if  (StringUtils.isNotBlank(criteria.getRefMonth())) {
			descBuilder.append(String.format("Period: %s", criteria.getRefMonth()));
			descBuilder.append("\n");
		}
		
		if (criteria.getAllocationBatchIds() != null && criteria.getAllocationBatchIds().length > 0) {
			List<AllocationBatch> allocationBatchs = allocationBatchDao.getAllocationBatchByIds(Arrays.asList(criteria.getAllocationBatchIds()));
			List<String> aBatchNames = new ArrayList<String>();
			descBuilder.append("Allocation Batch: ");
			for (AllocationBatch allocationBatch : allocationBatchs) {
				aBatchNames.add(allocationBatch.getBatchName());
			}
			descBuilder.append(StringUtils.join(aBatchNames, ", "));
			descBuilder.append("\n");
		}
		
		if (criteria.getPurposeIds() != null && criteria.getPurposeIds().length > 0) {
			List<Purpose> purposes = purposeDao.getSurveyTypesByIds(Arrays.asList(criteria.getPurposeIds()));
			List<String> purposeNames = new ArrayList<String>();
			descBuilder.append("Purpose: ");
			for (Purpose purpose : purposes) {
				purposeNames.add(purpose.getName());
			}
			descBuilder.append(StringUtils.join(purposeNames,", "));
			descBuilder.append("\n");
		}
		
		descBuilder.append("\n");
		
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
	
	public SXSSFSheet createDistrictHeader(SXSSFSheet sheet, List<String> districts, String[] header, Map<String, Integer> keyMap){
		int rowCnt = 0;
		
		SXSSFRow row = sheet.getRow(rowCnt);
		int cnt = header.length;
		
		for (String code : districts) {
			SXSSFCell cellHeader = row.createCell(cnt);
			cellHeader.setCellValue(code+" (Assignment)");
			keyMap.put(code, cnt);
			cnt++;
			
			cellHeader = row.createCell(cnt);
			cellHeader.setCellValue(code+" (Quotation Record)");
			cnt++;
		}
		
		keyMap.put("Total", cnt);
		SXSSFCell cellHeader = row.createCell(cnt);
		cellHeader.setCellValue("Total (Assignment)");
		
		cnt++;
		cellHeader = row.createCell(cnt);
		cellHeader.setCellValue("Total (Quotation Record)");
		
		return sheet;
	}
}
