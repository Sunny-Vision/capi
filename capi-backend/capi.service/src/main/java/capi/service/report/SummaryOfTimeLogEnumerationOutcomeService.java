package capi.service.report;

import java.io.FileOutputStream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ReportTaskDao;
import capi.dal.TimeLogDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SummaryOfTimeLogEnumerationOutcomeCriteria;
import capi.model.report.SummaryOfTimeLogEnumerationOutcomeReport;
import capi.service.CommonService;

@Service("SummaryOfTimeLogEnumerationOutcomeService")
public class SummaryOfTimeLogEnumerationOutcomeService extends DataReportServiceBase{
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private TimeLogDao timeLogDao;
	
	@Autowired
	private CommonService commonService;
	
	private static final String[] headers = {"Time Log Date","Survey","Team","Post","Field Officer Code",
			"Field Officer Name","C","D","L","M","N","ND","U","P","R","O","Blank","Total"
	};
	
	private static final String[] headers1 = {"Time Log Date","Survey","Team","C","D","L","M","N","ND","U","P","R","O","Blank","Total"};
	
	private static final String[] headers2 = {"Time Log Date","Survey","C","D","L","M","N","ND","U","P","R","O","Blank","Total"};
	
	@Override
	public void generateReport(Integer taskId) throws Exception {
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfTimeLogEnumerationOutcomeCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfTimeLogEnumerationOutcomeCriteria.class);

		Date startDate = commonService.getDate(criteria.getFromDate());
		Date endDate = commonService.getDate(criteria.getToDate());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		List<Integer> userIds = null;
		
		if (criteria.getOfficerId() != null && criteria.getOfficerId().size()>0 ) {
			userIds = criteria.getOfficerId();
		} else if ((criteria.getAuthorityLevel() & 771) > 0) {
			userIds = userDao.getReportLookupTableSelectAll("", 20, null, null, endDate, startDate, null, null, null, 1);
		} else {
			userIds = userDao.getReportLookupTableSelectAll("", 16, null, null, endDate, startDate
					, null, null, new Integer[]{criteria.getUserId()}, 1);
		}
		
		List<SummaryOfTimeLogEnumerationOutcomeReport> results = timeLogDao.getSummaryOfTimeLogEnumerationOutcomeReport(criteria.getSurvey(), userIds, startDate, endDate);
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (SummaryOfTimeLogEnumerationOutcomeReport data : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getTimeLogDate()));

			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCompletion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDeletion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDoorLocked());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getMoved());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getNonContact());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getNonDomestic());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnoccupied());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPartially());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRefusal());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOthers());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBlank());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotal());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		
		//add sheet
		List<SummaryOfTimeLogEnumerationOutcomeReport> results1 = timeLogDao.getSummaryOfTimeLogEnumerationOutcomeReportTeam(criteria.getSurvey(), criteria.getTeam(), userIds, startDate, endDate);
		
		SXSSFSheet sheet1 = workBook.createSheet();
		SXSSFRow row = sheet1.createRow(0);
		int cnt = 0;
		for(String header: headers1) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		
		rowCnt = 1;
		for (SummaryOfTimeLogEnumerationOutcomeReport data : results1){
			row = sheet1.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getTimeLogDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCompletion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDeletion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDoorLocked());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getMoved());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getNonContact());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getNonDomestic());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnoccupied());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPartially());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRefusal());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOthers());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBlank());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotal());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet1.flushRows();
			}
		}
		
		
		//add sheet
		List<SummaryOfTimeLogEnumerationOutcomeReport> results2 = timeLogDao.getSummaryOfTimeLogEnumerationOutcomeReportOverAll(criteria.getSurvey(), criteria.getTeam(), userIds, startDate, endDate);
		
		SXSSFSheet sheet2 = workBook.createSheet();
		row = sheet2.createRow(0);
		cnt = 0;
		for(String header: headers2) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		
		rowCnt = 1;
		for (SummaryOfTimeLogEnumerationOutcomeReport data : results2){
			row = sheet2.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(sdf.format(data.getTimeLogDate()));
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getSurvey());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getCompletion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDeletion());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getDoorLocked());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getMoved());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getNonContact());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getNonDomestic());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getUnoccupied());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getPartially());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRefusal());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getOthers());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getBlank());
			
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTotal());
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet2.flushRows();
			}
		}
		// End Generate Excel
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Time Log En Outcome");
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Summary (Team)");
		workBook.setSheetName(workBook.getSheetIndex(sheet2), "Summary (Overall)");
		
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
	public String getFunctionCode() {
		return "RF9015";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		SummaryOfTimeLogEnumerationOutcomeCriteria criteria = (SummaryOfTimeLogEnumerationOutcomeCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();

		if (criteria.getSurvey() != null && criteria.getSurvey().size() > 0) {
			if (descBuilder.length() > 0) descBuilder.append("\n");
			descBuilder.append("Surveys: ");
			descBuilder.append(StringUtils.join(criteria.getSurvey(), ", "));
		}
		
		if (criteria.getOfficerId() != null && criteria.getOfficerId().size() > 0) {
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<User> users = userDao.getUsersByIds(criteria.getOfficerId());
			descBuilder.append("Officers: ");
			List<String> codes = new ArrayList<String>();
			for (User user : users) {
				codes.add(user.getStaffCode() + " - " + user.getEnglishName());
			}
			descBuilder.append(StringUtils.join(codes, ", "));
		}
		
		descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s - %s", criteria.getFromDate(), criteria.getToDate()));
		
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
