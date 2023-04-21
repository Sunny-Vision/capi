package capi.service.batchJob;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.ClosingDateDao;
import capi.dal.UserDao;
import capi.entity.ClosingDate;
import capi.entity.User;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 *	The reminder service to notify user to define closing date
 *
 */
@Service("ClosingDateReminderService")
public class ClosingDateReminderService  implements BatchJobService{

	@Autowired
	private ClosingDateDao closingDateDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private NotificationService notifyService;
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Reminder for setting up closing date";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		Date refMonth = commonService.getReferenceMonth(new Date());	
		Date nextMonth = DateUtils.addMonths(refMonth, 1);
		
		ClosingDate closingDate = closingDateDao.getClosingDateByReferenceMonth(nextMonth);
		if (closingDate == null){
			List<Date> dates = calendarEventDao.getPreviousNWorkdingDate(nextMonth, 4);
			Date date = dates.get(dates.size()-1);
			if (today.equals(date)){
				List<User> users = userDao.getAuthorizedUserForClosingMaintenance();
				for (User user : users){
					String content = messageSource.getMessage("N00006", new Object[]{ commonService.formatMonth(nextMonth) }, Locale.ENGLISH);
					String subject = messageSource.getMessage("N00005", new Object[]{ commonService.formatMonth(nextMonth) }, Locale.ENGLISH);
					notifyService.sendNotification(user, subject, content, false);
				}
				
				closingDateDao.flushAndClearCache();
			}			
		}
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
