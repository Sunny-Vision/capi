package capi.service.assignmentAllocationAndReallocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AssignmentDao;
import capi.dal.CalendarEventDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.UserDao;
import capi.entity.ActivityCode;
import capi.entity.CalendarEvent;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.AssignmentDisplayModel;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.CalendarDisplayModel;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.CalendarEventModel;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.StaffNameModel;
import capi.model.commonLookup.UserLookupTableList;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.masterMaintenance.ActivityCodeService;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@Service("CalendarEventService")
public class CalendarEventService extends BaseService {
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ActivityCodeService activityService;
	
	@Autowired
	private SystemConfigurationDao systemConfigDao;
	
	public CalendarEvent findById(Integer eventId){
		return calendarEventDao.findById(eventId);
	}

	public CalendarEvent findOrCreate(Integer userId, Integer activityType, String eventDateStr){
		User user = userService.getUserById(userId);
		Date date;
		try {
			date = commonService.getDate(eventDateStr);
			return calendarEventDao.findOrCreate(user, activityType, date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public CalendarEvent findOrCreate(Integer userId, Integer activityCodeId, String session, String eventDateStr){
		User user = userService.getUserById(userId);
		ActivityCode activityCode = activityService.getActivityCodeById(activityCodeId);
		Date date;
		try {
			date = commonService.getDate(eventDateStr);
			return calendarEventDao.findOrCreate(user, activityCode, session, date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public CalendarEvent findOrCreate(Integer calendarEventId){
		
		CalendarEvent entity = calendarEventDao.findById(calendarEventId);
		if(entity == null){
			entity = calendarEventDao.createEvent();
		}
		return entity;
	}

	@Transactional(isolation=Isolation.REPEATABLE_READ)
	public int save(CalendarEventModel entityModel, Boolean findById, Boolean checkDuplicate) throws ParseException, ConstraintViolationException{
		
		CalendarEvent entity = null;
		Integer activityCodeId = 0;
		
		if(findById){
			entity = this.findOrCreate(entityModel.getCalendarEventId());
			if(checkDuplicate && entity.getCalendarEventId() != null && entity.getCalendarEventId() != entityModel.getCalendarEventId()){
				throw new ConstraintViolationException("E00012. Event existed in same day", null, null);
			}
		}else{
			try{
				activityCodeId = Integer.parseInt(entityModel.getActivityCodeId());
			}catch(Exception e){
				activityCodeId = 0;
			}
			entity = this.findOrCreate(entityModel.getUserId(), activityCodeId, entityModel.getSession(), entityModel.getEventDate());
			if(entity == null){
				throw new ParseException("Event Date Parsing Error", 0);
			}
			if(checkDuplicate && entity.getCalendarEventId() != null && entity.getCalendarEventId() != entityModel.getCalendarEventId()){
				throw new ConstraintViolationException("E00012. Event existed in same day", null, null);
			}
		}
		
		User user = userService.getUserById(entityModel.getUserId());
		ActivityCode activityCode = null;
		//handle null man day submitted when batch apply or select btn clicked
		if(entityModel.getActivityType() == SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS){
			try{
				activityCodeId = Integer.parseInt(entityModel.getActivityCodeId());
			}catch(Exception e){
				String code = "";
				String description = "";
				if(entityModel.getActivityCodeId().contains(" - ")){	
					code = entityModel.getActivityCodeId().split(" - ")[0];
					description = entityModel.getActivityCodeId().split(" - ")[1];
				}else{
					code = entityModel.getActivityCodeId();
				}
				activityCodeId = activityService.createActivityCode(code, description, entityModel.getManDay()).getActivityCodeId();
			}
			activityCode = activityService.getActivityCodeById(activityCodeId);
			if(entityModel.getManDay() == null){
				entityModel.setManDay(activityCode.getManDay());
			}
		}else{
			if(entityModel.getActivityType() == SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT){
				List<Integer> oTIds = this.calendarEventDao.findOT(user, commonService.getDate(entityModel.getEventDate()));
				if(oTIds.size() > 0 && !oTIds.contains(entity.getCalendarEventId())){
					throw new ConstraintViolationException("E00116. Duplicate OT Evnet in same date", null, null);
				}
				
				List<Integer> timeOffIds = this.calendarEventDao.findTimeOff(user, commonService.getDate(entityModel.getEventDate()));
				if(timeOffIds.size() > 0){
					throw new ConstraintViolationException("E00117. TimeOff event existed in same day when saving new OT event", null, null);
				}
			}
			
			if(entityModel.getActivityType() == SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_TIMEOFF){
				List<Integer> oTIds = this.calendarEventDao.findOT(user, commonService.getDate(entityModel.getEventDate()));
				if(oTIds.size() > 0){
					throw new ConstraintViolationException("E00117. OT event existed in same day when saving new Time Off event", null, null);
				}
			}
			
		}
		
		if(entity.getId() != null && entity.getId() != entityModel.getCalendarEventId()){

			/** skip update existing record; **/
			return entity.getId();
			/*
			try{
				this.updateUserAccumulatedOT(user, entity.getActivityType(), entity.getManDay(), entityModel.getActivityType(), entityModel.getManDay());
			}catch(Exception e){
				throw e;
			}*/
		}else if(entityModel.getActivityType() == SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT 
				|| entityModel.getActivityType() == SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_TIMEOFF){
			Date date = commonService.getTime(entityModel.getDuration());
			long minutes = DateUtils.getFragmentInMinutes(date, java.util.Calendar.DATE);
			this.updateUserAccumulatedOT(user, entityModel.getActivityType(), 0, entityModel.getActivityType(), minutes);
//			try{
//				// calculate while create event
//				this.updateUserAccumulatedOT(user, SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS, 0, entityModel.getActivityType(), entityModel.getManDay());
//			}catch(Exception e){
//				throw e;
//			}
		}
		
		entity.setActivityType(entityModel.getActivityType());
		entity.setUser(user);
		entity.setManDay(entityModel.getManDay());
		entity.setSession(entityModel.getSession());
		entity.setEventDate(commonService.getDate(entityModel.getEventDate()));
		entity.setRemark(entityModel.getRemarks());
		
		entity.setPublicHoliday(false);
		
		if(entity.getActivityType() == 3){
			entity.setActivityCode(activityCode);
			entity.setStartTime(null);
			entity.setEndTime(null);
			entity.setDuration(null);
		}else{
			entity.setActivityCode(null);
			entity.setStartTime(commonService.getTime(entityModel.getStartTime()));
			entity.setEndTime(commonService.getTime(entityModel.getEndTime()));
			entity.setDuration(commonService.getTime(entityModel.getDuration()));					
		}
		
		calendarEventDao.save(entity);
		calendarEventDao.flush();
		
		return entity.getId();
	}
	
	public List<CalendarEvent> getCalendarEvents(List<User> users, Date fromDate, Date toDate){
		return calendarEventDao.getCalendarEvents(users, fromDate, toDate);
	}
	
	public List<CalendarEvent> getCalendarEventsForStaffCalendar(List<UserLookupTableList> users, Date fromDate, Date toDate){
		return calendarEventDao.getCalendarEventsForStaffCalendar(users, fromDate, toDate);
	}
	
	@Transactional
	public void deleteEvent(CalendarEvent ce){		
		//restore UserAccumulatedOT
		if (ce.getActivityType() == SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT || ce.getActivityType() == SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_TIMEOFF){
			User user = ce.getUser();
			if (ce.getDuration() != null){
				long minutes = DateUtils.getFragmentInMinutes(ce.getDuration(), java.util.Calendar.DATE);
				updateUserAccumulatedOT(user, ce.getActivityType(), minutes, ce.getActivityType(), 0);				
			}
		}
		calendarEventDao.delete(ce);
		calendarEventDao.flush();
	}
	
	public List<AssignmentDisplayModel> getCalendarAssignments(List<User> users, Date fromDate, Date toDate){
		return assignmentDao.getCalendarAssignments(users, fromDate, toDate);
	}
	
	public List<AssignmentDisplayModel> getCalendarAssignmentsForStaffCalendar(List<UserLookupTableList> users, Date fromDate, Date toDate){
		return assignmentDao.getCalendarAssignmentsForStaffCalendar(users, fromDate, toDate);
	}
	
	@Transactional
	public void syncPublicCalendar() throws Exception{
		String urlStr = systemConfigDao.findByName(SystemConstant.BUS_PARAM_PUBLIC_HOLIDAY_URL).getValue();
//		URL url = new URL(urlStr);
//		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//		conn.setRequestMethod("GET");
//		conn.connect();
//		InputStream stream = conn.getInputStream();
		// 2020-12-01 don't attempt to download calendar ics file from internet
		// use file manually placed in the directory
		
//		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
//	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//	                return null;
//	            }
//	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//	            }
//	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//	            }
//	        }
//	    };
//	
//	    // Install the all-trusting trust manager
//	    SSLContext sc = SSLContext.getInstance("SSL");
//	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
//	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//	
//	    // Create all-trusting host name verifier
//	    HostnameVerifier allHostsValid = new HostnameVerifier() {
//	        public boolean verify(String hostname, SSLSession session) {
//	            return true;
//	        }
//	    };
//	
//	    // Install the all-trusting host verifier 
//	    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//	
//	    URL url = new URL(urlStr);
//	    URLConnection con = url.openConnection();
//	    InputStream stream = con.getInputStream();
	    //BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    //InputStream stream = IOUtils.toInputStream(IOUtils.toString(in), Charsets.UTF_8);
	    /*
	    Reader reader = new InputStreamReader(con.getInputStream());
	    while (true) {
	        int ch = reader.read();
	        if (ch==-1) {
	            break;
	        }
	        System.out.print((char)ch);
	    }
		*/
		//InputStream stream = commonService.httpGetRequestStream(urlStr);		
		
        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar;
        
        File initialFile = new File("C:\\Calender\\en.ics");
        InputStream targetStream = new FileInputStream(initialFile);
		
		calendar = builder.build(targetStream);
		
		
		ParameterList paramList = calendar.getCalendarScale().getParameters();
		
		Iterator it = paramList.iterator();
		while (it.hasNext()){
			Parameter param = (Parameter)it.next();
			System.out.println(param.getName()+"="+param.getValue());
		}
		PropertyList clist = calendar.getProperties();
		for (int j =0; j < clist.size(); j++){
			Property prop = (Property)clist.get(j);
			System.out.println(prop.getName()+"="+prop.getValue());
			
		}
		
		List<CalendarEvent> holidayList = new ArrayList<CalendarEvent>(); 
		
        ComponentList cs = calendar.getComponents();
        for (int i =0; i < cs.size(); i++){
        	Component component = (Component)cs.get(i);
        	if (component instanceof VEvent){
        		VEvent event = (VEvent)component;
        		
        		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        		
        		CalendarEvent ce = new CalendarEvent();
        		ce.setActivityType(SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS);
        		ce.setEventDate(event.getStartDate().getDate());
        		String uid = event.getUid().toString().replace("UID:", "");
        		ce.setPublicUid(uid);
        		ce.setPublicHoliday(true);
        		String remark = event.getSummary().toString().replace("SUMMARY:", "");
        		ce.setRemark(remark);
        		ce.setSession("A");
        		holidayList.add(ce);
        	}
        	else{
        		PropertyList list = component.getProperties();
        		for (int j =0; j < list.size(); j++){
        			Property prop = (Property)list.get(j);
        			System.out.println(prop.getName()+"="+prop.getValue());
        			
        		}
        	}
        }
//        stream.close();	        
        this.updateHolidays(holidayList);		
	}
	
	@Transactional
	public void updateHolidays(List<CalendarEvent> holidayList){
		for(int i = 0; i < holidayList.size(); i++){
			CalendarEvent entry = holidayList.get(i);
			CalendarEvent ce = this.calendarEventDao.findHolidayExist(entry.getPublicUid());
			if(ce != null){
				ce.setRemark(entry.getRemark());
				ce.setEventDate(entry.getEventDate());
        		ce.setActivityType(SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS);
        		entry = ce;
			}
			this.calendarEventDao.save(entry);
		}
		calendarEventDao.flush();
	}
	
	@Transactional
	private void updateUserAccumulatedOT(User user, int oldType, double oldMinutes, int newType, double newMinutes){
		switch(oldType){
//			case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS:
//				switch(newType){
//					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS:
//					break;
//					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT:
//						user.setAccumulatedOT(user.getAccumulatedOT() + newManDay);
//					break;
//					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_TIMEOFF:
//						user.setAccumulatedOT(user.getAccumulatedOT() - newManDay);
//					break;
//				}
//			break;
			case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT:
				switch(newType){
//					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS:
//						user.setAccumulatedOT(user.getAccumulatedOT() - oldManDay);
//					break;
					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT:
						user.setAccumulatedOT(user.getAccumulatedOT() - oldMinutes + newMinutes);
					break;
					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_TIMEOFF:
						user.setAccumulatedOT(user.getAccumulatedOT() - oldMinutes - newMinutes);
					break;
				}
			break;
			case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_TIMEOFF:
				switch(newType){
//					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS:
//						user.setAccumulatedOT(user.getAccumulatedOT() + oldManDay);
//					break;
					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT:
						user.setAccumulatedOT(user.getAccumulatedOT() + oldMinutes + newMinutes);
					break;
					case SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_TIMEOFF:
						user.setAccumulatedOT(user.getAccumulatedOT() + oldMinutes - newMinutes);
					break;
				}
			break;
		}
		userService.saveUser(user);
	}
	
	
	public CalendarDisplayModel gatherDisplayModel(Date fromDate, Date toDate, List<User> userList){
		CalendarDisplayModel model = new CalendarDisplayModel();
		java.util.Calendar c = java.util.Calendar.getInstance();
		
		model.setFromDate(fromDate);
		
		List<Date> dateList = new ArrayList<Date>();
		for(Date i = fromDate; i.before(toDate) || i.equals(toDate); ){
			dateList.add(i);
			c.setTime(i);
			c.add(java.util.Calendar.DATE, 1);
			i = c.getTime();
		}
		model.setDateList(dateList);
		
		c.setTime(fromDate);
		c.add(java.util.Calendar.MONTH, -1);
		model.setPreviousMonth(c.getTime());
		
		c.setTime(fromDate);
		c.add(java.util.Calendar.MONTH, 1);
		model.setNextMonth(c.getTime());
		
		List<CalendarEvent> calendarEventList = this.getCalendarEvents(userList, fromDate, toDate);
		model.setCalendarEventList(calendarEventList);
		
		List<CalendarEvent> calendarHolidayList = this.getCalendarHolidays(fromDate, toDate);
		model.setCalendarHolidayList(calendarHolidayList);
		
		List<AssignmentDisplayModel> assignmentList = this.getCalendarAssignments(userList, fromDate, toDate);
		model.setAssignmentList(assignmentList);
		
		List<ActivityCode> activityList = activityService.getActivityCodes();
		model.setActivityList(activityList);
		
		model.setUserList(userList);
		
		try{
			String calendarEventColor = systemConfigDao.findByName(SystemConstant.BUS_PARAM_CALENDAR_EVENT_COLOR).getValue();
			model.setCalendarEventColor("btn-"+calendarEventColor);
		}catch(NullPointerException e){
			model.setCalendarEventColor("btn-primary");
		}
		
		try{
			String assignmentEventColor = systemConfigDao.findByName(SystemConstant.BUS_PARAM_ASSIGNMENT_EVENT_COLOR).getValue();
			model.setAssignmentEventColor("btn-"+assignmentEventColor);
		}catch(NullPointerException e){
			model.setCalendarEventColor("btn-success");
		}
		
		return model;
	}
	
	// For Staff Calendar
	public CalendarDisplayModel gatherDisplayModelForStaffCalendar(Date fromDate, Date toDate, List<UserLookupTableList> userList){
		CalendarDisplayModel model = new CalendarDisplayModel();
		java.util.Calendar c = java.util.Calendar.getInstance();
		
		model.setFromDate(fromDate);
		
		List<Date> dateList = new ArrayList<Date>();
		for(Date i = fromDate; i.before(toDate) || i.equals(toDate); ){
			dateList.add(i);
			c.setTime(i);
			c.add(java.util.Calendar.DATE, 1);
			i = c.getTime();
		}
		model.setDateList(dateList);
		
		c.setTime(fromDate);
		c.add(java.util.Calendar.MONTH, -1);
		model.setPreviousMonth(c.getTime());
		
		c.setTime(fromDate);
		c.add(java.util.Calendar.MONTH, 1);
		model.setNextMonth(c.getTime());
		
		List<CalendarEvent> calendarEventList = this.getCalendarEventsForStaffCalendar(userList, fromDate, toDate);
		model.setCalendarEventList(calendarEventList);
		
		List<CalendarEvent> calendarHolidayList = this.getCalendarHolidays(fromDate, toDate);
		model.setCalendarHolidayList(calendarHolidayList);
		
		List<AssignmentDisplayModel> assignmentList = this.getCalendarAssignmentsForStaffCalendar(userList, fromDate, toDate);
		model.setAssignmentList(assignmentList);
		
		List<ActivityCode> activityList = activityService.getActivityCodes();
		model.setActivityList(activityList);
		
		model.setUserListStaffCalendar(userList);
		
		try{
			String calendarEventColor = systemConfigDao.findByName(SystemConstant.BUS_PARAM_CALENDAR_EVENT_COLOR).getValue();
			model.setCalendarEventColor("btn-"+calendarEventColor);
		}catch(NullPointerException e){
			model.setCalendarEventColor("btn-primary");
		}
		
		try{
			String assignmentEventColor = systemConfigDao.findByName(SystemConstant.BUS_PARAM_ASSIGNMENT_EVENT_COLOR).getValue();
			model.setAssignmentEventColor("btn-"+assignmentEventColor);
		}catch(NullPointerException e){
			model.setCalendarEventColor("btn-success");
		}
		
		return model;
	}
	
	public List<CalendarEvent> getCalendarHolidays(Date fromDate, Date toDate){
		return this.calendarEventDao.getCalendarHolidays(fromDate, toDate);
	}
	
	public List<StaffNameModel> getSelectedStaffName(List<Integer> ids){
		Iterator<User> users = this.userDao.getUsersByIds(ids).iterator();
		List<StaffNameModel> nameList = new ArrayList<StaffNameModel>();
		while(users.hasNext()){
			User u = users.next();
			StaffNameModel model = new StaffNameModel();
			model.setStaffName(u.getStaffCode() + " - " + u.getChineseName() + " ( " + u.getDestination() + " )");
			model.setUserId(u.getUserId());
			nameList.add(model);
		}
		return nameList;
	}
	
	public Double getNoOfWorkingDay(Date month){
		return this.calendarEventDao.getNoOfWorkingDay(month);
	}
	
	
	public List<String> getOTDatesInYear(Integer userId){
		Date date = DateUtils.addYears(new Date(), -1);
		commonService.getDateWithoutTime(commonService.getDateWithoutTime(date));
		return calendarEventDao.getOTDates(userId, date);
		
	}
	
	public List<String> getTimeOffDatesInYear(Integer userId){
		Date date = DateUtils.addYears(new Date(), -1);
		commonService.getDateWithoutTime(commonService.getDateWithoutTime(date));
		return calendarEventDao.getTimeOffDates(userId, date);
		
	}
	
	public List<Date> getNonWorkingDate(Date startDate, Date endDate){
		return calendarEventDao.getNonWorkingDate(startDate, endDate);
	}
}
