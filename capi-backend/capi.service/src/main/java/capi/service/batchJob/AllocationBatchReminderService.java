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

import capi.dal.AllocationBatchDao;
import capi.dal.CalendarEventDao;
import capi.dal.UserDao;
import capi.entity.AllocationBatch;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 * The reminder service to notify the need of assignment allocation of an allocation batch
 * 
 * 3,2,1 days before the next assignment allocation batch
 * Every day until the assignment allocation batch is finished after the start of the survey month DAB1502.01
 */
@Service("AllocationBatchReminderService")
public class AllocationBatchReminderService  implements BatchJobService{

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CalendarEventDao calendarEventDao;
	
	@Autowired
	private AllocationBatchDao allocationBatchDao;
	
	@Resource(name="messageSource")
	private MessageSource messageSource;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private NotificationService notifyService;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Allocation batch reminder";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());	
		AllocationBatch allocationBatch = allocationBatchDao.getLatestUnallocatedAllocationBatch();
		if (allocationBatch != null){
			List<Date> dates = calendarEventDao.getNextNWorkdingDate(today, 3);
			Date startDate = allocationBatch.getStartDate();
			Date nextDate = dates.get(1);
			Date theDateAfterTomorrow = dates.get(2);
			if (DateUtils.truncatedCompareTo(today, startDate, Calendar.DATE) >= 0 ||
				DateUtils.truncatedCompareTo(nextDate, startDate, Calendar.DATE) == 0 ||
				DateUtils.truncatedCompareTo(theDateAfterTomorrow, startDate, Calendar.DATE) == 0){
				
				List<User> fieldTeamHeads = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD, null);
				String referenceMonth = commonService.formatMonth(allocationBatch.getSurveyMonth().getReferenceMonth());
				for (User allocator : fieldTeamHeads){
					notifyService.sendNotification(allocator, messageSource.getMessage("N00012", null, Locale.ENGLISH), messageSource.getMessage("N00021", new Object[]{referenceMonth}, Locale.ENGLISH), false);					
				}				
				allocationBatchDao.flushAndClearCache();
				
			}
		}
		
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
