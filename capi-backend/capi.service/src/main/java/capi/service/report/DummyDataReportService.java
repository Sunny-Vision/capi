package capi.service.report;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.CalendarSummaryCriteria;
import capi.model.report.CalendarSummaryReport;
import capi.service.CommonService;

@Service("DummyDataReportService")
public class DummyDataReportService extends DataReportServiceBase{

	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommonService commonService;
	
	private static final String[] headers = new String[]{
			"Date", "Staff Code", "Session", "Name", "Team",
			"Rank", "Is Public Holiday"
		};
	
	@Override
	public String getFunctionCode(){
		// TODO Auto-generated method stub
		return "RF9030";
	}
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		// Get Data
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		CalendarSummaryCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), CalendarSummaryCriteria.class);
		
		List<Integer> userIds = Arrays.asList(criteria.getOfficerIds());
		
		Date refMonth = commonService.getMonth(criteria.getRefMonth());
		
		List<CalendarSummaryReport> results = calendarEventDao.getCalendarSummaryReport(refMonth, userIds);
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		for(CalendarSummaryReport data : results){
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatDate(data.getDate()));
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getStaffCode());
			
			cellCnt = 2;
			cell = row.createCell(cellCnt);
			cell.setCellValue(String.valueOf(data.getSession()));
			
			cellCnt = 3;
			cell = row.createCell(cellCnt);
//			cell.setCellValue(data.getName());
			
			cellCnt = 4;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			cellCnt = 5;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getRank());
			
			cellCnt = 6;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getIsPublicHoliday());
			
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
		
		if(criteria.getOfficerIds()!=null && criteria.getOfficerIds().length>0){
			List<User> users = userDao.getUsersByIds(Arrays.asList(criteria.getOfficerIds()));
			descBuilder.append("Officers: ");
			for (User user : users) {
				descBuilder.append(user.getChineseName()+" ");
			}
			descBuilder.append("\n");
		}
		descBuilder.append(String.format("Period: %s", criteria.getRefMonth()));
		
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
