package capi.service.report;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.CalendarEvent;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.ApplicationOTimeOffWorkCriteria;
import capi.service.CommonService;

@Service("ApplicationOTWorkService")
public class ApplicationOTWorkService extends ReportServiceBase{
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		ApplicationOTimeOffWorkCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), ApplicationOTimeOffWorkCriteria.class);
		Date date = commonService.getDate(criteria.getDate());
		
		CalendarEvent event = calendarEventDao.getOTEvent(criteria.getUserId(), date);
		if (event != null){
			Hashtable<String, String> table = new Hashtable<String, String>();
			String createdDate = commonService.formatDate(new Date());
			String eventDate = commonService.formatDate(date);
    		String startTime = commonService.formatTime(event.getStartTime());
    		String endTime = commonService.formatTime(event.getEndTime());
    		Double formattedDuration = DateUtils.getFragmentInMinutes(event.getDuration(), Calendar.DATE)/60.0;
    		System.out.println("formattedDuration: " + formattedDuration);
    		String duration = String.format("%.2f", formattedDuration);
    		User user = userDao.findById(criteria.getUserId());
    		User supervisor = user.getSupervisor();
    		table.put("createdDate", createdDate);
    		table.put("superCreatedDate", createdDate);
    		table.put("datetime", String.format("%s, %s-%s", eventDate, startTime, endTime));
    		table.put("duration", String.format("%s hour(s)", duration));
    		table.put("name", user.getEnglishName());
    		table.put("destination", user.getDestination());
    		table.put("performedDuration", duration);
    		table.put("performedCreatedDate", eventDate);
    		table.put("performedDateTime", String.format("%s-%s", startTime, endTime));
    		table.put("recordedName", user.getEnglishName());
    		table.put("rank", user.getRank().getCode());
    		table.put("recordedDestination", user.getDestination());
    		table.put("recordedCreatedDate", createdDate);
    		table.put("recordedSuperCreatedDate", createdDate);
 		
    		// Search for the teamHead (SCSO, RankId = 8) with smallest userId
    		List<User> teamHeadList = userDao.findUserByRankCode("SCSO");
    		int teamHeadUserId = 9999;
    		String recordedSuperDestination = "";
    		String recordedSuperName = "";
    		String superRank = "";
    		if (!teamHeadList.isEmpty() && teamHeadList != null) {
        		for (User teamHead : teamHeadList) {
        			if (teamHead.getUserId() < teamHeadUserId) {
        				teamHeadUserId = teamHead.getUserId();
        				recordedSuperDestination = teamHead.getDestination();
        				recordedSuperName = teamHead.getEnglishName();
        				superRank = teamHead.getRank().getCode();
        			}
        		}
    		}
    		
    		if (teamHeadUserId < 9999) {
    			table.put("recordedSuperDestination", recordedSuperDestination);
    			table.put("recordedSuperName", recordedSuperName);
    			table.put("superRank", superRank);  
    		}
    		
    		if (supervisor!= null){
    			table.put("superDestination",supervisor.getDestination());
        		table.put("superName", supervisor.getEnglishName());
    		}    		
			String path = this.exportDocx(task, "MB9039", table, this.getFunctionCode());
			
			task.setPath(path);
			reportTaskDao.save(task);
			reportTaskDao.flush();
		}
		else{
			throw new RuntimeException("No OT event could be found");
		}
	}

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9039";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		ApplicationOTimeOffWorkCriteria criteria = (ApplicationOTimeOffWorkCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		User user = userDao.findById(criteria.getUserId());
		
		descBuilder.append("Date: " + criteria.getDate());
		descBuilder.append("\n");
		descBuilder.append(String.format("Field Staff: %s - %s", user.getStaffCode(), user.getEnglishName()));
		
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

}
