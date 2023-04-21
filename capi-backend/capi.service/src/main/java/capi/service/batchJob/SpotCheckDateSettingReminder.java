package capi.service.batchJob;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.SpotCheckDateDao;
import capi.dal.UserDao;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 * Reminder for Spot Check Date Setting (starting from 3 working days before the calendar month)
 * 
 */
@Service("SpotCheckDateSettingReminder")
public class SpotCheckDateSettingReminder  implements BatchJobService{
	
	@Autowired
	private CalendarEventDao calendarDao;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SpotCheckDateDao spotCheckDateDao;
	
	@Autowired
	private UserDao userDao;	
	
	@Autowired
	private NotificationService notificationService;
	
	@Resource(name="messageSource")
	private MessageSource messageSource;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Spot Check Date Setting Reminder";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		Date curMonth = DateUtils.truncate(new Date(), Calendar.MONTH); 
		Date nextMonth = DateUtils.addMonths(curMonth, 1);
		
		long curSpotCheckCount = spotCheckDateDao.countSpotCheckDateInMonth(curMonth);
		
		if (curSpotCheckCount == 0){			
			sendNotification(nextMonth);			
			return;
		}
		
		List<Date> previousDates =  calendarDao.getPreviousNWorkdingDate(nextMonth, 4);
		Date date = previousDates.get(previousDates.size() -1);		
		if (today.after(date) || today.equals(date)){
			long spotCheckCount = spotCheckDateDao.countSpotCheckDateInMonth(nextMonth);
			if (spotCheckCount == 0){
				sendNotification(nextMonth);
			}			
		}
		
	}
	
	private void sendNotification (Date month){
		int combined = (SystemConstant.AUTHORITY_LEVEL_BUSINESS_DATA_ADMINISTRATOR | SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR | SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD);
		
		String subject = messageSource.getMessage("N00032", null, Locale.ENGLISH);		
		List<User> users = userDao.getActiveUsersWithAnyAuthorityLevel(combined);
		String content = messageSource.getMessage("N00033", new Object[]{commonService.formatMonth(month)}, Locale.ENGLISH);				
		for (User user : users){
			notificationService.sendNotification(user, subject, content, false);
		}
	}
	

	@Override
	public boolean canRun() {
		/* 
		 * (temporary) disable
		 * 2. Notification (reminder) for Spot Check Date Maintenance & Spot Check Setup Maintenance (Batch job)
		 */
		// TODO Auto-generated method stub
		return false;
	}

}
