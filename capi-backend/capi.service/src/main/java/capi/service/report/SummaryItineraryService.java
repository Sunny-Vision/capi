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

import capi.dal.ReportTaskDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.TimeLogDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.SystemConfiguration;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SummaryItineraryCriteria;
import capi.model.report.SummaryItineraryReport;
import capi.service.CommonService;

@Service("SummaryItineraryService")
public class SummaryItineraryService extends DataReportServiceBase{

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TimeLogDao dao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SystemConfigurationDao systemConfigurationDao;
	
	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9025";
	}
	
	private static final String headers[] = new String[]{
			"Reference Month", "Team", "Post", "Field Officer Code", "Field Officer Name"
	};
	
	private static final String headers2[] = new String[]{
			"Reference Month", "Team"
	};
	
	private static final String headers3[] = new String[]{
			"Reference Month"
	};
	
	@Override
	public void generateReport(Integer taskId) throws Exception{
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if(StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryItineraryCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryItineraryCriteria.class);
		
		//Assignment Deviation;
		SystemConfiguration systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION_PLUS);
		SystemConfiguration assignmentDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_NO_OF_ASSIGNMENT_DEVIATION);
		Double noOfAssignmentDeviationLimit = null;
		if (assignmentDeviationConfig != null && assignmentDeviationConfig.getValue() != null && assignmentDeviationConfig.getValue().equals("1"))
			noOfAssignmentDeviationLimit = Double.parseDouble(systemConfiguration.getValue());
		
		//Sequence Deviation;
		systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_SEQUENCE_PERCENTS);
		SystemConfiguration sequenceDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_SEQUENCE_DEVIATION);
		Double sequenceDeviationLimit = null;
		if (sequenceDeviationConfig != null && sequenceDeviationConfig.getValue() != null && sequenceDeviationConfig.getValue().equals("1"))
			sequenceDeviationLimit = Double.parseDouble(systemConfiguration.getValue());
		
		//TPU Deviation
		systemConfiguration = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION_TIMES);
		SystemConfiguration tpuDeviationConfig = systemConfigurationDao.findByName(SystemConstant.ITINERARY_PARAM_TPU_SEQUENCE_DEVIATION);
		Integer tpuDeviationLimit = null;
		if (tpuDeviationConfig != null && tpuDeviationConfig.getValue() != null && tpuDeviationConfig.getValue().equals("1"))
			tpuDeviationLimit = Integer.parseInt(systemConfiguration.getValue());
		
		Date start = null;
		Date end = null;
		start = commonService.getMonth(criteria.getRefMonth());
		end = commonService.getMonth(criteria.getRefMonth());
		end = DateUtils.addMonths(end, 1);
		end = DateUtils.addDays(end, -1);
		List<String> dayRange = getDayRange(start, end);
		List<Integer> userIds = null;
		if (criteria.getOfficerIds()!=null && criteria.getOfficerIds().length>0) {
			userIds = Arrays.asList(criteria.getOfficerIds());
		}
		
		List<SummaryItineraryReport> results = dao.getSummaryItineraryReport(start, end, userIds, noOfAssignmentDeviationLimit, sequenceDeviationLimit, tpuDeviationLimit);
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		SXSSFSheet sheet1 = workBook.createSheet();
		SXSSFSheet sheet2 = workBook.createSheet();
		
		sheet = createDateHeader(sheet, headers, dayRange);
		sheet1 = createSheetHeader(sheet1, headers2);
		sheet1 = createDateHeader(sheet1, headers2, dayRange);
		sheet2 = createSheetHeader(sheet2, headers3);
		sheet2 = createDateHeader(sheet2, headers3, dayRange);
		
		// Sheet 0
		fillSheet0(sheet, setDynamicSheet(results, commonService.formatShortMonth(start)), dayRange);
		
		// Sheet 1
		results = dao.getSummaryItineraryTeamReport(start, end, userIds, noOfAssignmentDeviationLimit, sequenceDeviationLimit, tpuDeviationLimit);
		fillSheet1(sheet1, setDynamicSheet1(results, commonService.formatShortMonth(start)), dayRange);
		
		// Sheet 2
		results = dao.getSummaryItineraryMonthReport(start, end, userIds, noOfAssignmentDeviationLimit, sequenceDeviationLimit, tpuDeviationLimit);
		fillSheet2(sheet2, setDynamicSheet2(results, commonService.formatShortMonth(start)), dayRange);
		
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Summary (by officer)");
		workBook.setSheetName(workBook.getSheetIndex(sheet1), "Summary (by team)");
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
	
	public void fillSheet0(SXSSFSheet sheet, List<SummaryItineraryReport> results, List<String> dayRange) throws Exception {
		int rowCnt = 1;
		
		for (SummaryItineraryReport data : results) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
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
			
			int total = 0;
			Map<String, Integer> mapDay = data.getMapDay();
			for (String day : dayRange) {
				cellCnt++;
				if (mapDay.containsKey(day)) {
					cell = row.createCell(cellCnt);
					cell.setCellValue("Y");
					total += 1;
				}
			}
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(total);
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet0
	}
	
	public void fillSheet1(SXSSFSheet sheet, List<SummaryItineraryReport> results, List<String> dayRange) throws Exception {
		int rowCnt = 1;
		
		for (SummaryItineraryReport data : results) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			cellCnt = 1;
			cell = row.createCell(cellCnt);
			cell.setCellValue(data.getTeam());
			
			int total = 0;
			Map<String, Integer> mapDay = data.getMapDay();
			for (String day : dayRange) {
				cellCnt++;
				if (mapDay.containsKey(day)) {
					cell = row.createCell(cellCnt);
					cell.setCellValue(mapDay.get(day));
					total += mapDay.get(day);
				}
			}
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(total);
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet0
	}
	
	public void fillSheet2(SXSSFSheet sheet, List<SummaryItineraryReport> results, List<String> dayRange) throws Exception {
		int rowCnt = 1;
		
		for (SummaryItineraryReport data : results) {
			SXSSFRow row = sheet.createRow(rowCnt);
			
			// Input Row of Data start
			int cellCnt = 0;
			SXSSFCell cell = row.createCell(cellCnt);
			cell.setCellValue(data.getReferenceMonth());
			
			int total = 0;
			Map<String, Integer> mapDay = data.getMapDay();
			for (String day : dayRange) {
				cellCnt++;
				if (mapDay.containsKey(day)) {
					cell = row.createCell(cellCnt);
					cell.setCellValue(mapDay.get(day));
					total += mapDay.get(day);
				}
			}
			cellCnt++;
			cell = row.createCell(cellCnt);
			cell.setCellValue(total);
			
			// Input Row of Data end
			rowCnt++;
			if (rowCnt % 2000 == 0){
				sheet.flushRows();
			}
		}
		// End Generate Sheet0
	}
	
	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		// TODO Auto-generated method stub
		SummaryItineraryCriteria criteria = (SummaryItineraryCriteria)criteriaObject;
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
				descBuilder.append(String.format("Officer: %s", StringUtils.join(userStr, ", ")));
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
	
	public SXSSFSheet createSheetHeader(SXSSFSheet sheet, String[] headers){
		int cnt = 0;
		
		SXSSFRow row = sheet.createRow(cnt);
		for (String header : headers){
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		return sheet;
	}
	
	public List<String> getDayRange(Date start, Date end){
		List<String> dayRange = new ArrayList<String>();
		
		long diff = end.getTime() - start.getTime();;
		
		Integer days = (int) (diff / (24 * 60 * 60 * 1000));
		
		for (int i = 0; i <= days; i++) {
			dayRange.add(String.valueOf(i+1));
		}
		
		return dayRange;
	}
	
	public SXSSFSheet createDateHeader(SXSSFSheet sheet, String[] header, List<String> dayRange){
		int rowCnt = 0;
		
		SXSSFRow row = sheet.getRow(rowCnt);
		int cnt = header.length;
		
		for (String s : dayRange) {
			SXSSFCell cellHeader = row.createCell(cnt);
			cellHeader.setCellValue(s);
			cnt++;
		}
		SXSSFCell cellHeader = row.createCell(cnt);
		cellHeader.setCellValue("Total");
		
		return sheet;
	}
	
	private List<SummaryItineraryReport> setDynamicSheet(List<SummaryItineraryReport> results, String refMonth) {
		List<SummaryItineraryReport> list = new ArrayList<SummaryItineraryReport>();
		Map<String, Integer> mapDay = new Hashtable<String, Integer>();;
		SummaryItineraryReport temp = null;
		
		if (results != null && results.size() > 0) {
			for (SummaryItineraryReport data : results) {
				if (temp != null && (!(data.getUserId()!=null && data.getUserId().equals(temp.getUserId()) ))) {
					temp.setReferenceMonth(refMonth);
					temp.setMapDay(mapDay);
					list.add(temp);
					mapDay = new Hashtable<String, Integer>();
				}
				mapDay.put(data.getDate(), data.getCountTimeLog());
				temp = data;
			}
			temp.setReferenceMonth(refMonth);
			temp.setMapDay(mapDay);
			list.add(temp);
		}
		return list;
	}
	
	private List<SummaryItineraryReport> setDynamicSheet1(List<SummaryItineraryReport> results, String refMonth) {
		List<SummaryItineraryReport> list = new ArrayList<SummaryItineraryReport>();
		Map<String, Integer> mapDay = new Hashtable<String, Integer>();;
		SummaryItineraryReport temp = null;
		
		if (results != null && results.size() > 0) {
			for (SummaryItineraryReport data : results) {
				if (temp != null && (!(data.getTeam()!=null && data.getTeam().equals(temp.getTeam()) ))) {
					temp.setReferenceMonth(refMonth);
					temp.setMapDay(mapDay);
					list.add(temp);
					mapDay = new Hashtable<String, Integer>();
				}
				mapDay.put(data.getDate(), data.getCountTimeLog());
				temp = data;
			}
			temp.setReferenceMonth(refMonth);
			temp.setMapDay(mapDay);
			list.add(temp);
		}
		return list;
	}
	
	private List<SummaryItineraryReport> setDynamicSheet2(List<SummaryItineraryReport> results, String refMonth) {
		List<SummaryItineraryReport> list = new ArrayList<SummaryItineraryReport>();
		Map<String, Integer> mapDay = new Hashtable<String, Integer>();
		if(results.size() > 0){
			SummaryItineraryReport temp = results.get(0);
			
			if (results != null && results.size() > 0) {
				for (SummaryItineraryReport data : results) {
					mapDay.put(data.getDate(), data.getCountTimeLog());
				}
				temp.setReferenceMonth(refMonth);
				temp.setMapDay(mapDay);
				list.add(temp);
			}
		}
		return list;
	}
	
}
