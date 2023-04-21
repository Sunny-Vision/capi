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

@Service("ApplicationTimeOffService")
public class ApplicationTimeOffService extends ReportServiceBase{
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
		
		Double balance = userDao.getDayStartTimeOffBalance(date, criteria.getUserId());
		List<CalendarEvent> events = calendarEventDao.getTimeOffEvents(criteria.getUserId(), date);
		if (events != null){
			Hashtable<String, String> table = new Hashtable<String, String>();
			String eventDate = "";
			String dateRange = "";
			String duration ="";
			String subBalance="";
			
			
			for (CalendarEvent event : events){
				eventDate += commonService.formatDate(event.getEventDate())+"\n"; 
				dateRange += String.format("%s - %s", commonService.formatTime(event.getStartTime()), commonService.formatTime(event.getEndTime()))+"\n";
				Double doubleDuration = DateUtils.getFragmentInMinutes(event.getDuration(), Calendar.DATE) / 60.0;
				duration += String.format("%.2f", doubleDuration) + "\n";
				subBalance += String.format("%.2f", (balance/60)) + "\n";
			}			
			
    		User user = userDao.findById(criteria.getUserId());
    		User supervisor = user.getSupervisor();
    		table.put("createdDate", commonService.formatDate(new Date()));
    		table.put("superCreatedDate", commonService.formatDate(new Date()));
    		table.put("date", eventDate);
    		table.put("time", dateRange);
    		table.put("duration", duration + "hr(s)");
    		table.put("balance", subBalance + "hr(s)");
    		table.put("name", user.getEnglishName());
    		table.put("post", user.getDestination());
    		if (user.getRank() != null){
        		table.put("rank", user.getRank().getCode());
    		}
    		
//    		if (supervisor!= null){
//    			table.put("superpost",supervisor.getDestination());
//        		table.put("supername", supervisor.getEnglishName());
//        		if (supervisor.getRank() != null){
//            		table.put("superrank", supervisor.getRank().getCode());
//        		}
//    		} 
    		
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
    			table.put("superpost", recordedSuperDestination);
    			table.put("supername", recordedSuperName);
    			table.put("superrank", superRank);  
    		}
    		
			String path = this.exportDocx(task, "MB9040", table, this.getFunctionCode());
			
			task.setPath(path);
			reportTaskDao.save(task);
			reportTaskDao.flush();
		}
		else{
			throw new RuntimeException("No Time Off event could be found");
		}
	}

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9040";
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
