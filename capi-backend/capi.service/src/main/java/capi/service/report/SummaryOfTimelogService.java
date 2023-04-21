package capi.service.report;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
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
import capi.model.report.SummaryOfTimelog;
import capi.model.report.SummaryOfTimelogCriteria;
import capi.service.CommonService;

@Service("SummaryOfTimelogService")
public class SummaryOfTimelogService extends DataReportServiceBase{

	@Autowired
	private TimeLogDao dao;

	@Autowired
	private ReportTaskDao reportTaskDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private UserDao userDao;

	@Override
	public String getFunctionCode() {
		return "RF9014";
	}
	
	private static final String[] headers = {"Reference Month From","Reference Month To","Survey","Team","Post","Field Officer Code",
			"Field Officer Name","FI","TR","SD","LD","SA","OD","OT","/","Total"
	};
	
	private static final String[] headers1 = {"Reference Month From","Reference Month To","Survey","Team","FI","TR","SD","LD","SA","OD","OT","/","Total"};
	
	private static final String[] headers2 = {"Reference Month From","Reference Month To","Survey","FI","TR","SD","LD","SA","OD","OT","/","Total"};

	@Override
	public void generateReport(Integer taskId) throws Exception{
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfTimelogCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfTimelogCriteria.class);
		
		Date startMonth = commonService.getMonth(criteria.getStartMonth());
		Date endMonth = commonService.getMonth(criteria.getEndMonth());
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		String startMonths = formatter.format(startMonth);
		String endMonths = formatter.format(endMonth);
		
		endMonth = DateUtils.addMonths(endMonth, 1);
		endMonth = DateUtils.addDays(endMonth, -1);
		
		List<Integer> userIds = null;
		
		if (criteria.getUserIds() != null && criteria.getUserIds().size()>0 ) {
			userIds = criteria.getUserIds();
		} else if ((criteria.getAuthorityLevel() & 771) > 0) {
			userIds = userDao.getReportLookupTableSelectAll("", 20, null, null,  endMonth, startMonth,null, null, null, 1);
		} else {
			userIds = userDao.getReportLookupTableSelectAll("", 16, null, null, endMonth, startMonth
					, null, null, new Integer[]{criteria.getRequestUserId()}, 1);
		}
		
		
		List<SummaryOfTimelog> results = dao.getSummaryOfTimelogActivity(
				criteria.getSurvey(), criteria.getTeam(), 
				startMonth, endMonth, userIds);
		
		// Start Generate Excel
		SXSSFWorkbook workBook = prepareWorkbook();
		SXSSFSheet sheet = workBook.getSheetAt(0);
		
		int rowCnt = 1;
		
		for (SummaryOfTimelog data : results){
			if (data.getTotal() > 0) {
				SXSSFRow row = sheet.createRow(rowCnt);
				
				// Input Row of Data start
				int cellCnt = 0;
				SXSSFCell cell = row.createCell(cellCnt);
				cell.setCellValue(startMonths);

				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(endMonths);
				
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
				cell.setCellValue(data.getFi());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getTr());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getSd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getLd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getSa());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOt());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOthers());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getTotal());
				
				// Input Row of Data end
				rowCnt++;
				if (rowCnt % 2000 == 0){
					sheet.flushRows();
				}
			}
		}

		//add sheet
		List<SummaryOfTimelog> results1 = dao.getSummaryOfTimelogActivityTeam(criteria.getSurvey(), criteria.getTeam(), startMonth, endMonth, userIds);
		
		SXSSFSheet sheet1 = workBook.createSheet();
		SXSSFRow row = sheet1.createRow(0);
		int cnt = 0;
		for(String header: headers1) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		
		rowCnt = 1;
		
		for (SummaryOfTimelog data : results1){
			if (data.getTotal() > 0) {
				row = sheet1.createRow(rowCnt);
				
				// Input Row of Data start
				int cellCnt = 0;
				SXSSFCell cell = row.createCell(cellCnt);
				cell.setCellValue(startMonths);

				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(endMonths);
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getSurvey());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getTeam());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getFi());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getTr());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getSd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getLd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getSa());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOt());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOthers());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getTotal());
				
				// Input Row of Data end
				rowCnt++;
				if (rowCnt % 2000 == 0){
					sheet1.flushRows();
				}
			}
		}
		
		//add sheet
		List<SummaryOfTimelog> results2 = dao.getSummaryOfTimelogActivityOverAll(criteria.getSurvey(), criteria.getTeam(), startMonth, endMonth, criteria.getUserIds());
		
		SXSSFSheet sheet2 = workBook.createSheet();
		row = sheet2.createRow(0);
		cnt = 0;
		for(String header: headers2) {
			SXSSFCell cell = row.createCell(cnt);
			cell.setCellValue(header);
			cnt++;
		}
		
		rowCnt = 1;
		
		for (SummaryOfTimelog data : results2){
			if (data.getTotal() > 0) {
				row = sheet2.createRow(rowCnt);
				
				// Input Row of Data start
				int cellCnt = 0;
				SXSSFCell cell = row.createCell(cellCnt);
				cell.setCellValue(startMonths);
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(endMonths);

				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getSurvey());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getFi());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getTr());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getSd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getLd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getSa());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOd());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOt());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getOthers());
				
				cellCnt++;
				cell = row.createCell(cellCnt);
				cell.setCellValue(data.getTotal());
				
				// Input Row of Data end
				rowCnt++;
				if (rowCnt % 2000 == 0){
					sheet2.flushRows();
				}
			}
		}
		
		// End Generate Excel
		workBook.setSheetName(workBook.getSheetIndex(sheet), "Time Log Activity");
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
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception{
		
		SummaryOfTimelogCriteria criteria = (SummaryOfTimelogCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s - %s", criteria.getStartMonth(), criteria.getEndMonth()));
		
		if (criteria.getSurvey() != null && criteria.getSurvey().size() > 0){
			descBuilder.append("\n");
			descBuilder.append(String.format("Survey: %s", StringUtils.join(criteria.getSurvey(), ", ")));
		}
		if (criteria.getUserIds() != null && criteria.getUserIds().size() > 0){
			descBuilder.append("\n");
			
			List<String> fieldOfficers = new ArrayList<String>();
			List<User> users = userDao.getUsersByIds(criteria.getUserIds());
			for(User user : users) {
				fieldOfficers.add(user.getStaffCode() + " - " + user.getEnglishName());
			}
			descBuilder.append(String.format("Field Officer(s): %s", StringUtils.join(fieldOfficers, ", ")));			
			
//			descBuilder.append(String.format("Field Officer Id: %s", StringUtils.join(criteria.getUserIds(), ",")));
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
