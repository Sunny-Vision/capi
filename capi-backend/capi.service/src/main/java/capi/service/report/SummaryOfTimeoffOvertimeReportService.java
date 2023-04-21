package capi.service.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.CalendarEvent;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.SummaryOfTimeoffOvertimeCriteria;
import capi.model.report.SummaryOfTimeoffOvertimeReport;
import capi.model.report.SummaryOfTimeoffOvertimeReportAccumulated;
import capi.service.CommonService;

@Service("SummaryOfTimeoffOvertimeReportService")
public class SummaryOfTimeoffOvertimeReportService extends ReportServiceBase{
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private CommonService commonService;
	

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		SummaryOfTimeoffOvertimeCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), SummaryOfTimeoffOvertimeCriteria.class);
		Date startDate = commonService.getDate(criteria.getFromDate());
		Date endDate = commonService.getDate(criteria.getToDate());
		
		Integer authorityLevel = criteria.getAuthorityLevel();
		Integer[] officerIds = new Integer[] {criteria.getUserId()};
		
		if ((authorityLevel & 2) > 0){
			if (criteria.getOfficerIds()==null || criteria.getOfficerIds().length<1){
				List<Integer> userIds = userDao.getReportLookupTableSelectAll(null, 20, null, null, startDate, endDate, null, null, officerIds, null);
				officerIds = userIds.toArray(new Integer[0]);
			} else {
				officerIds = criteria.getOfficerIds();
			}
		} else if ((authorityLevel & 4) > 0){
			if (criteria.getOfficerIds()==null || criteria.getOfficerIds().length<1){
				List<Integer> userIds = userDao.getReportLookupTableSelectAll(null, 16, null, null, startDate, endDate, null, null, officerIds, null);
				officerIds = userIds.toArray(new Integer[0]);
			} else {
				officerIds = criteria.getOfficerIds();
			}
		}
		
		List<CalendarEvent> data = calendarEventDao.getTimeoffOvertimeEvents(startDate, endDate, officerIds, criteria.getTeams());
		
		List<SummaryOfTimeoffOvertimeReport> progress = new ArrayList<SummaryOfTimeoffOvertimeReport>();
		SummaryOfTimeoffOvertimeReport report = new SummaryOfTimeoffOvertimeReport();
		User prevUser = null;
		
		for (CalendarEvent item : data ) {
			User user = item.getUser();
			
			if (prevUser != user) {
				if (prevUser != null) {
					progress.add(report);
					report = new SummaryOfTimeoffOvertimeReport();
				}
				report.setPost(user.getRank().getCode());
				report.setTeam(user.getTeam());
				report.setUserId(user.getId());
				report.setOfficer(user.getEnglishName()+"\n("+user.getStaffCode()+")");
				report.setAccumulatedOT(user.getAccumulatedOT());	
			}
			
			String duration = commonService.formatTime(item.getDuration());
			Integer hour = Integer.parseInt(duration.split(":")[0]);
			Integer minutes = Integer.parseInt(duration.split(":")[1]);
			String durationText = "";
			if (hour > 0) {
				durationText += String.format("%d hours ", hour);
			}
			if (hour == 0 || minutes > 0 ) {
				durationText += String.format("%d minutes ", minutes);
			}
			int durationMinutes = hour * 60 + minutes;
			if (hour > 0) {
				durationText += String.format("(%d minutes)", durationMinutes);
			}
			
			if (item.getActivityType() ==  1) {

				if (report.getCotDate() == null || report.getCotDate().length() == 0 ) {
					report.setCotDate(commonService.formatDate(item.getEventDate()));
					report.setCotTime(commonService.formatTime(item.getStartTime())+ " - " + commonService.formatTime(item.getEndTime()));
					report.setCotDuration(durationText);
					report.setCotSubTotalMinutes(durationMinutes);
				} else {
					report.setCotDate(report.getCotDate() + "\n" + commonService.formatDate(item.getEventDate()));
					report.setCotTime(report.getCotTime() + "\n" + commonService.formatTime(item.getStartTime())+ " - " + commonService.formatTime(item.getEndTime()));
					report.setCotDuration(report.getCotDuration() + "\n" + durationText);
					report.setCotSubTotalMinutes(report.getCotSubTotalMinutes() + durationMinutes);										
				}
			} else if (item.getActivityType() ==  2) {

				if (report.getCtoDate() == null || report.getCtoDate().length() == 0 ) {
					report.setCtoDate(commonService.formatDate(item.getEventDate()));
					report.setCtoTime(commonService.formatTime(item.getStartTime())+ " - " + commonService.formatTime(item.getEndTime()));
					report.setCtoDuration(durationText);
					report.setCtoSubTotalMinutes(durationMinutes);
				} else {
					report.setCtoDate(report.getCtoDate() + "\n" + commonService.formatDate(item.getEventDate()));
					report.setCtoTime(report.getCtoTime() + "\n" + commonService.formatTime(item.getStartTime())+ " - " + commonService.formatTime(item.getEndTime()));
					report.setCtoDuration(report.getCtoDuration() + "\n" + durationText);					
					report.setCtoSubTotalMinutes(report.getCtoSubTotalMinutes() + durationMinutes);					
				}
			}
			prevUser = user;
		}
		progress.add(report);
		
		for (SummaryOfTimeoffOvertimeReport item : progress) {
			SummaryOfTimeoffOvertimeReportAccumulated accumulateStart = calendarEventDao.getAccumulatedTime(item.getUserId(), startDate);
//			SummaryOfTimeoffOvertimeReportAccumulated accumulateEnd = calendarEventDao.getAccumulatedTime(item.getUserId(), endDate);			
			
		    Double openingMinutes = item.getAccumulatedOT();
		    if (openingMinutes == null){
		    	openingMinutes = 0.0;
		    }
		    
		    if (accumulateStart != null) {
		    	openingMinutes = openingMinutes + accumulateStart.getAccumulatedTF() - accumulateStart.getAccumulatedOT();
		    }
		    
		    Double closingMinutes = openingMinutes + item.getCotSubTotalMinutes() - item.getCtoSubTotalMinutes();
//		    Double closingMinutes = item.getAccumulatedOT();
//		    if (closingMinutes == null){
//		    	closingMinutes = 0.0;
//		    }		
//		    if (accumulateEnd != null) {
//		    	closingMinutes = closingMinutes	+ accumulateEnd.getAccumulatedTF() - accumulateEnd.getAccumulatedOT();
//		    }
		    
		    //Add a blank line on the top and bottom for the date, time and duration
//		    {
//			    if (StringUtils.isNotEmpty(item.getCotDuration())) {  	
//			    	item.setCotDuration("\n" + item.getCotDuration() + "\n");
//			    }
//			    if (StringUtils.isNotEmpty(item.getCtoDuration())) {
//			    	item.setCtoDuration("\n" + item.getCtoDuration() + "\n");  
//			    }
//			    if (StringUtils.isNotEmpty(item.getCtoDate())) {
//			    	item.setCtoDate("\n" + item.getCtoDate() + "\n");  
//			    }
//			    if (StringUtils.isNotEmpty(item.getCotDate())) {
//			    	item.setCotDate("\n" + item.getCotDate() + "\n");  
//			    }
//			    if (StringUtils.isNotEmpty(item.getCotTime())) {
//			    	item.setCotTime("\n" + item.getCotTime() + "\n");  
//			    }
//			    if (StringUtils.isNotEmpty(item.getCtoTime())) {
//			    	item.setCtoTime("\n" + item.getCtoTime() + "\n");  
//			    }
//		    }
		    
		    String cotSubTotal = "";
		    if (item.getCotSubTotalMinutes() >= 60) {
		    	cotSubTotal += String.format("%d hours ", item.getCotSubTotalMinutes() / 60);
		    }
		    cotSubTotal += String.format("%d minutes", item.getCotSubTotalMinutes() % 60);
		    if (item.getCotSubTotalMinutes() >= 60) {
		    	cotSubTotal += String.format("\n(%d minutes)", item.getCotSubTotalMinutes());
		    }
		    
		    item.setCotSubTotal(cotSubTotal);
		    
		    String ctoSubTotal = "";
		    if (item.getCtoSubTotalMinutes() >= 60) {
		    	ctoSubTotal += String.format("%d hours ", item.getCtoSubTotalMinutes() / 60);
		    }
		    ctoSubTotal += String.format("%d minutes", item.getCtoSubTotalMinutes() % 60);
		    if (item.getCtoSubTotalMinutes() >= 60) {
		    	ctoSubTotal += String.format("\n(%d minutes)", item.getCtoSubTotalMinutes());
		    }
		    
		    item.setCtoSubTotal(ctoSubTotal);
		    
		    String opening = "";
		    if (Math.abs(openingMinutes) >= 60) {
		    	opening += String.format("%d hours ", openingMinutes.intValue() / 60);
		    }
		    opening += String.format("%d minutes", openingMinutes.intValue() % 60);
		    if (Math.abs(openingMinutes) >= 60) {
		    	opening += String.format("\n(%d minutes)", openingMinutes.intValue());
		    }
		    
		    item.setOpeningTimeOffBalance(opening);
		    
		    String closing = "";
		    if (Math.abs(closingMinutes) >= 60) {
		    	closing += String.format("%d hours ", closingMinutes.intValue() / 60);
		    }
		    closing += String.format("%d minutes", closingMinutes.intValue() % 60);
		    if (Math.abs(closingMinutes) >= 60) {
		    	closing += String.format("\n(%s minutes)", closingMinutes.intValue());
		    }
		    item.setClosingTimeOffBalance(closing);
		}		

		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		
		parameters.put("period", criteria.getFromDate() + " - " + criteria.getToDate());
		
		if (criteria.getTeams() != null && criteria.getTeams().length > 0){
			parameters.put("team", StringUtils.join(criteria.getTeams(), ","));
		}
		else{
			parameters.put("team", "-");
		}
		
		if (criteria.getOfficerIds() != null && criteria.getOfficerIds().length > 0){
			parameters.put("officer", StringUtils.join(criteria.getOfficerIds(), ","));
		}
		else{
			parameters.put("officer", "-");
		}
		
		if (task.getReportType().equals(ReportServiceBase.XLSX)) {
			parameters.put("IS_IGNORE_PAGINATION", true);
		}
		
		String path = this.exportReport(task, "SummaryOfTimeoffOvertime", progress, parameters);
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "RF9043";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		SummaryOfTimeoffOvertimeCriteria criteria = (SummaryOfTimeoffOvertimeCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		if (criteria.getTeams() != null) {
			descBuilder.append("Teams: ");
			descBuilder.append(StringUtils.join(criteria.getTeams(), " "));
			descBuilder.append("\n");
		}
		if (criteria.getOfficerIds() != null) {
			List<User> users = userDao.getUsersByIds(Arrays.asList(criteria.getOfficerIds()));
			if(users != null && users.size() > 0) {
				List<String> userStr = new ArrayList<String>();
				for(User user : users) {
					String str = user.getStaffCode() + " - " + user.getEnglishName();
					userStr.add(str);
				}
				descBuilder.append(String.format("Officer: %s", StringUtils.join(userStr, ", ")));
			}
			descBuilder.append("\n");
		}
		
		descBuilder.append(String.format("Period: %s - %s", criteria.getFromDate(), criteria.getToDate()));
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		}
		else{
			descBuilder.append("\n");
			descBuilder.append("Export Type: XLSX");
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

}
