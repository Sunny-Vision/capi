package capi.service.batchJob;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.SurveyMonthDao;
import capi.dal.UserDao;
import capi.entity.SurveyMonth;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 * remind user to create survey if it reach the three working day before the end date of latest survey month
 *
 * Reminder for defining Survey Month
 * No Survey Month is defined after the end date of last survey month
 */
@Service("SurveyMonthReminderService")
public class SurveyMonthReminderService implements BatchJobService{

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private NotificationService notifyService;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Survey Month Reminder";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		SurveyMonth surveyMonth = surveyMonthDao.getLatestSurveyMonth();
		Date today = commonService.getDateWithoutTime(new Date());
		if (surveyMonth != null){
			Date nextSurveyMonth = DateUtils.addMonths(surveyMonth.getReferenceMonth(), 1);
			Date endDate = surveyMonth.getEndDate();
			List<Date> previousDates =  calendarEventDao.getPreviousNWorkdingDate(endDate, 4);
			if (previousDates != null && previousDates.size() > 0){
				for (int i = previousDates.size() -1; i >= 0; i--){
					if (DateUtils.truncatedCompareTo(today, previousDates.get(i), Calendar.DATE) >=0){			
						int authorityLevel = SystemConstant.AUTHORITY_LEVEL_ALLOCATION_COORDINATOR;
						String subject = messageSource.getMessage("N00019", new Object[]{commonService.formatMonth(nextSurveyMonth)}, Locale.ENGLISH);
						String message = messageSource.getMessage("N00020", new Object[]{commonService.formatMonth(nextSurveyMonth)}, Locale.ENGLISH);
						
						List<User> users = userDao.getActiveUsersWithAuthorityLevel(authorityLevel, null);
						
						if (i == previousDates.size() -1 && DateUtils.truncatedCompareTo(today, previousDates.get(i), Calendar.DATE) >0){
							users.addAll(userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD, null));							
						}
						
						Set<User> uniqueUser = new HashSet<User>(users);
						
						for (User user: uniqueUser){
							notifyService.sendNotification(user, subject, message, false);
						}
						
						surveyMonthDao.flush();
						break;
					}
				}
			}		
			
		}
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
