package capi.service.batchJob;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.CalendarEventDao;
import capi.dal.DistrictHeadAdjustmentDao;
import capi.dal.UserDao;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.service.CommonService;
import capi.service.NotificationService;
/**
 * 
 * @author stanley_tsang
 * The reminder service to notify field allocator to allocate the assignment
 * 
 * Reminder for filling in Staff Calendar (same as assignment allocation)
 * Reminder for allocating assignments (starting from 3 working days before the start of the calendar month)
 * Reminder for allocating assignments (starting from 1 working day before the start of the calendar month)
 * "Every day until the assignment allocation batch is finished after the start of the survey month DAB1502.01"
 */
@Service("AssignmentAllocationReminderService")
public class AssignmentAllocationReminderService  implements BatchJobService{

	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private NotificationService notifyService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserDao userDao;
	
	@Resource(name="messageSource")
	private MessageSource messageSource;
	
	@Autowired
	private DistrictHeadAdjustmentDao districtHeadAdjustmentDao;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Assignment Allocation Reminder";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		Date refMonth = commonService.getReferenceMonth(today);	
		Date nextMonth = DateUtils.addMonths(refMonth, 1);
		List<Date> dates = calendarEventDao.getPreviousNWorkdingDate(nextMonth, 4);
		
		Date threeDay = dates.get(1);
		Date lastDay = dates.get(3);		
		
		if (today.equals(threeDay) || today.equals(lastDay)){			
			long eventCnt = calendarEventDao.countEventsInMonth(nextMonth);
			if (eventCnt == 0){
				List<User> fieldAllocators = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_ALLOCATION_COORDINATOR, null);
				for (User allocator : fieldAllocators){
					notifyService.sendNotification(allocator, messageSource.getMessage("N00010", null, Locale.ENGLISH), messageSource.getMessage("N00011", new Object[]{commonService.formatMonth(nextMonth)}, Locale.ENGLISH), false);					
				}				
			}
			
			long adjustmentCnt = districtHeadAdjustmentDao.countDistrictHeadAdjustmentInMonth(nextMonth);
			if (adjustmentCnt == 0){
				
//				List<User> fieldAllocators = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_ALLOCATION_COORDINATOR, null);
//				for (User allocator : fieldAllocators){
//					notifyService.sendNotification(allocator, messageSource.getMessage("N00010", null, Locale.ENGLISH), messageSource.getMessage("N00011", new Object[]{commonService.formatMonth(nextMonth)}, Locale.ENGLISH), false);					
//				}
				
				if (DateUtils.truncatedCompareTo(today, lastDay, Calendar.DATE) >= 0){
					int combined = (SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR | SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD);
					List<User> notifyUsers = userDao.getActiveUsersWithAnyAuthorityLevel(combined);
					for (User allocator : notifyUsers){
						notifyService.sendNotification(allocator, messageSource.getMessage("N00012", null, Locale.ENGLISH), messageSource.getMessage("N00013", new Object[]{commonService.formatMonth(nextMonth)}, Locale.ENGLISH), false);						
					}
				}
			}	
			
			calendarEventDao.flushAndClearCache();
			
		}
		
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return false;
	}

}
