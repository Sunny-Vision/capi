package capi.service.batchJob;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.ItineraryPlanDao;
import capi.entity.ItineraryPlan;
import capi.entity.User;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 * The reminder service to notify user to submit time log
 * 
 * If officer planned itinerary and the time log of that day haven't submitted > 3
 * 
 */
@Service("TimeLogSubmissionReminderService")
public class TimeLogSubmissionReminderService implements BatchJobService{
	
	@Autowired
	private ItineraryPlanDao itineraryPlanDao;
	
	@Resource(name="messageSource")
	private MessageSource messageSource;
	
	@Autowired
	private NotificationService notifyService;

	@Autowired
	private CommonService commonService;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Time Log Submission Reminder";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		List<Integer> planIds = itineraryPlanDao.getTimeLogNotSubmittedItineraryPlan();
		if (planIds == null || planIds.size() == 0) return;
		
		Calendar calendar = Calendar.getInstance();	
		Date prevMonth = DateUtils.addMonths(new Date(), -1);
		calendar.setTime(prevMonth);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Date fromDate =  calendar.getTime();
		Date toDate = new Date();
		
		List<ItineraryPlan> plans =  itineraryPlanDao.getItineraryByIdsDate(planIds, fromDate, toDate);
		
		Hashtable<User,List<User>> userMapping = new Hashtable<User,List<User>>();
		Hashtable<User,List<Date>> planDateMapping = new Hashtable<User,List<Date>>();
		
		if (plans != null && plans.size() > 0){
			for (ItineraryPlan plan : plans){
				User officer = plan.getUser();
				User supervisor = officer.getSupervisor();
				if (userMapping.containsKey(supervisor)){
					List<User> users = userMapping.get(supervisor);
					if (!users.contains(officer)){
						users.add(officer);
					}
				}
				else{
					List<User> users = new ArrayList<User>();
					users.add(officer);
					userMapping.put(supervisor, users);
				}
				
				if (planDateMapping.containsKey(officer)){
					List<Date> dates = planDateMapping.get(officer);
					if (! dates.contains(plan.getDate())){
						dates.add(plan.getDate());
					}
				} else {
					List<Date> dates = new ArrayList<Date>();
					dates.add(plan.getDate());
					planDateMapping.put(officer, dates);
				}
				
				String subject = messageSource.getMessage("N00022", null, Locale.ENGLISH);
				String content = messageSource.getMessage("N00023", new Object[]{commonService.formatDate(plan.getDate())}, Locale.ENGLISH);
				notifyService.sendNotification(officer, subject, content, false);
				
			}
			
			for (User supervisor: userMapping.keySet()){
				List<User> users = userMapping.get(supervisor);
				StringBuilder builder = new StringBuilder();
				for (User user : users){
					List<Date> dates = planDateMapping.get(user);
					for (Date date: dates){
						builder.append("\n"+String.format("%s-%s", user.getStaffCode(), user.getChineseName())+" on "+commonService.formatDate(date));
					}
				}
				String subject = messageSource.getMessage("N00022", null, Locale.ENGLISH);
				String content = messageSource.getMessage("N00025", new Object[]{builder.toString()}, Locale.ENGLISH);
				notifyService.sendNotification(supervisor, subject, content, false);				
			}
			
		}
		itineraryPlanDao.flushAndClearCache();
		
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
