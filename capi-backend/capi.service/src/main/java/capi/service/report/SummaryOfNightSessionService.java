package capi.service.report;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import capi.model.report.SummaryOfNightSessionCriteria;
import capi.model.report.SummaryOfNightSessionReport;
import capi.service.CommonService;

@Service("SummaryOfNightSessionService")
public class SummaryOfNightSessionService extends DataReportServiceBase{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TimeLogDao timeLogDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getFunctionCode(){
		// TODO Auto-generated method stub
		return "RF9028";
	}
	
	private static final String[] headers = new String[]{
			"Reference Month", "Team", "Post", "Field Officer Code", "Field Officer Name"
		};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		// Get Data
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfNightSessionCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfNightSessionCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getRefMonth());
		
		Date endMonth = DateUtils.addMonths(startMonth, 1);
		
		endMonth = DateUtils.addDays(endMonth, -1);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(endMonth);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		
		Integer[] officerIds = criteria.getOfficerIds();
		
		List<Integer> userIds = new ArrayList<Integer>();
		if (officerIds != null && officerIds.length > 0){
			userIds = Arrays.asList(officerIds);
		} else {
			userIds = userDao.searchReportOfficerId(16, null, null, startMonth);
		}
		
		List<SummaryOfNightSessionReport> users = userDao.getUserModelByNightSessionReport(userIds);
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Night Session (by officer)");
		sheet = createHeader2(sheet, dayOfMonth);
		
		int rowCnt = 1;
		
		for(SummaryOfNightSessionReport data : users){
			List<Integer> result = timeLogDao.getTimeLogDateByNightSession(data.getUserId(), startMonth, endMonth);
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(commonService.formatShortMonth(startMonth));
			
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
			
			cellCnt++;
			for (int i = 1; i <= dayOfMonth; i++) {
				if (result.contains(i)){
					cell = row.createCell(cellCnt);
					cell.setCellValue("Y");
				}
				cellCnt++;
			}
			
			cell = row.createCell(cellCnt);
			cell.setCellValue(result.size());
			
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
		//TODO Auto-generated method stub
		SummaryOfNightSessionCriteria criteria = (SummaryOfNightSessionCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if(criteria.getOfficerIds()!=null && criteria.getOfficerIds().length>0){
			if (descBuilder.length() > 0) descBuilder.append("\n");
			List<String> codes = new ArrayList<String>();
			List<User> users = userDao.getUsersByIds(Arrays.asList(criteria.getOfficerIds()));
			for (User user : users) {
				codes.add(user.getStaffCode() + " - " + user.getEnglishName());
			}
			descBuilder.append(String.format("Officer: %s", StringUtils.join(codes, ", ")));
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
	
	public SXSSFSheet createHeader2(SXSSFSheet sheet, Integer dayOfMonth){
		int rowCnt = 0;
		
		SXSSFRow row = sheet.getRow(rowCnt);
		int cnt = headers.length;
		
		for (int i = 1; i <= dayOfMonth; i++) {
			SXSSFCell cellHeader = row.createCell(cnt);
			cellHeader.setCellValue(String.valueOf(i));
			cnt++;
		}
		
		SXSSFCell cellHeader = row.createCell(cnt);
		cellHeader.setCellValue("Total");
		
		return sheet;
	}
}
