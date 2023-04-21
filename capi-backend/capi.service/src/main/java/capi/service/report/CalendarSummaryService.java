package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.QuotationRecord;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.CalendarSummaryCriteria;
import capi.model.report.CalendarSummaryReport;
import capi.service.CommonService;
import capi.service.UserService;
import edu.emory.mathcs.backport.java.util.Collections;

@Service("CalendarSummaryService")
public class CalendarSummaryService extends DataReportServiceBase{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode(){
		// TODO Auto-generated method stub
		return "RF9030";
	}
	// 1 A 2 P 3 E
	
	private static final String[] headers = new String[] {
			"Reference Month", "Team", "Post", "Field Officer Code", "Field Officer Name", "Section"};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		CalendarSummaryCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), CalendarSummaryCriteria.class);
		
		Date start = null;
		Date end = null;
		start = commonService.getMonth(criteria.getRefMonth());
		start = DateUtils.addMonths(start, 1);
		start = DateUtils.addDays(start, -1);
		end = commonService.getMonth(criteria.getRefMonth());
		
		
		List<Integer> userIds = null;
		
		if (criteria.getOfficerIds() != null && criteria.getOfficerIds().length>0){
			userIds = Arrays.asList(criteria.getOfficerIds());
		} else {
			userIds = userDao.getReportLookupTableSelectAll("", 16, null, null, start, end, null, null, null, 1);
		}
		
		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		
		List<CalendarSummaryReport> results = calendarEventDao.getCalendarSummaryReport(refMonth, userIds);
		
//		List<CalendarSummaryReport> dynamicResults = setDynamicCalendarSummaryReport(results);
		
		Map<CalendarSummaryReport, List<CalendarSummaryReport.DateJob>> map = set(results);
		List<CalendarSummaryReport> report = new ArrayList<CalendarSummaryReport>(map.keySet());
		
		Collections.sort(report, new Comparator<CalendarSummaryReport>(){
			@Override
			public int compare(CalendarSummaryReport o1,
					CalendarSummaryReport o2){
				// TODO Auto-generated method stub
				int c;
				c =  o1.getTeam().compareTo(o2.getTeam());
				if (c == 0){
					c =  o1.getRank().compareTo(o2.getRank());
				}
				if (c == 0) {
					c = o1.getStaffCode().compareTo(o2.getStaffCode());
				}
				if (c == 0) {
					c = o1.getSession().compareTo(o2.getSession());
				}
				
				return c;
			}
		});
		
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		workBook.setSheetName(0, "Calendar Summary");
		sheet = createDateHeader(sheet, start, end);
		
		int rowCnt = 1;
		
		for (CalendarSummaryReport data : report){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(end));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffName());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			String session = data.getSession() == 1 ? "A" : data.getSession() == 2 ? "P" : "E";
			cell.setCellValue(session);
			
			List<CalendarSummaryReport.DateJob> jobs = map.get(data);
			
			Collections.sort(jobs, new Comparator<CalendarSummaryReport.DateJob>(){
				@Override
				public int compare(CalendarSummaryReport.DateJob o1,
						CalendarSummaryReport.DateJob o2){
					// TODO Auto-generated method stub
					return o1.getDate().compareTo(o2.getDate());
				}
			});
			
			for (CalendarSummaryReport.DateJob job : jobs) {
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(job.getJobName());
			}
			
			// Input Row of Data end
			
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
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
		// TODO Auto-generated method stub
		CalendarSummaryCriteria criteria = (CalendarSummaryCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getOfficerIds() != null && criteria.getOfficerIds().length > 0) {
			List<User> users = userDao.getUsersByIds(Arrays.asList(criteria.getOfficerIds()));
			if(users != null && users.size() > 0) {
				List<String> userStr = new ArrayList<String>();
				for(User user : users) {
					String str = user.getStaffCode() + " - " + user.getEnglishName();
					userStr.add(str);
				}
				descBuilder.append(String.format("Indoor Staff: %s", StringUtils.join(userStr, ", ")));
			}
		}
		
		descBuilder.append(String.format("\r\nPeriod: %s", criteria.getRefMonth()));
		
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
	
	public SXSSFSheet createDateHeader(SXSSFSheet sheet, Date start, Date end){
		int rowCnt = 0;
		
		SXSSFRow row = sheet.getRow(rowCnt);
		int cnt = headers.length;
		
		long diff = start.getTime() - end.getTime();;
		
		Integer days = (int) (diff / (24 * 60 * 60 * 1000));
		
		for (int i = 0; i <= days; i++) {
			SXSSFCell cellHeader = row.createCell(cnt);
			cellHeader.setCellValue(String.valueOf(i+1));
			cnt++;
		}
		
		return sheet;
	}
	
	public List<CalendarSummaryReport> setDynamicCalendarSummaryReport(List<CalendarSummaryReport> data) {
		List<CalendarSummaryReport> result = new ArrayList<CalendarSummaryReport>();
		CalendarSummaryReport comp = new CalendarSummaryReport();
		List<CalendarSummaryReport.DateJob> jobs = new ArrayList<CalendarSummaryReport.DateJob>();
		for (CalendarSummaryReport d : data) {
			if (!(d.getStaffCode().equals(comp.getStaffCode()) && d.getSession().equals(comp.getSession()))){
				if (comp.getStaffCode()!=null) {
					result.add(comp);
				}
				comp = new CalendarSummaryReport();
				jobs = new ArrayList<CalendarSummaryReport.DateJob>();
				comp.setTeam(d.getTeam());
				comp.setStaffCode(d.getStaffCode());
				comp.setStaffName(d.getStaffName());
				comp.setRank(d.getRank());
				comp.setSession(d.getSession());
				comp.setDateJobs(jobs);
			}
			CalendarSummaryReport.DateJob job = new CalendarSummaryReport.DateJob();
			job.setDate(d.getDate());
			job.setIsPublicHoliday(d.getIsPublicHoliday());
			job.setJobName(d.getJobName());
			comp.getDateJobs().add(job);
		}
		result.add(comp);
		return result;
	}
	
	public Map<CalendarSummaryReport, List<CalendarSummaryReport.DateJob>> set(List<CalendarSummaryReport> data) {
		Map<CalendarSummaryReport, List<CalendarSummaryReport.DateJob>> map = new Hashtable<CalendarSummaryReport, List<CalendarSummaryReport.DateJob>>();
		
		for (CalendarSummaryReport d : data) {
			CalendarSummaryReport.DateJob job = new CalendarSummaryReport.DateJob();
			job.setDate(d.getDate());
			job.setIsPublicHoliday(d.getIsPublicHoliday());
			job.setJobName(d.getJobName());
			
			if (map.containsKey(d)) {
				map.get(d).add(job);
			} else {
				List<CalendarSummaryReport.DateJob> jobs = new ArrayList<CalendarSummaryReport.DateJob>();
				jobs.add(job);
				map.put(d, jobs);
			}
		}
		
		return map;
	}
}
