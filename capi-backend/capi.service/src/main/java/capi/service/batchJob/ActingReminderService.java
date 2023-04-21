package capi.service.batchJob;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.ActingDao;
import capi.dal.UserDao;
import capi.entity.Acting;
import capi.entity.User;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 * The reminder service to notify the start and the end of acting
 */
@Service("ActingReminderService")
public class ActingReminderService implements BatchJobService{
	
	@Autowired
	private ActingDao actingDao;
		
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private NotificationService notifyService;
	
	@Autowired
	private UserDao userDao;
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Reminder for acting";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		final Date date = commonService.getDateWithoutTime(new Date());
		List<Acting> actings = actingDao.getActingByStartDate(date);
			
		if (actings.size() > 0){
			for (Acting acting : actings){
				
				Set<Integer> uniqueIds = new HashSet<Integer>();
				uniqueIds.add(acting.getStaff().getId());
				uniqueIds.add(acting.getReplacement().getId());
				if(acting.getStaff().getSupervisor() != null){
					uniqueIds.add(acting.getStaff().getSupervisor().getId());
				}
				if(acting.getReplacement().getSupervisor() != null){
					uniqueIds.add(acting.getReplacement().getSupervisor().getId());
				}
				List<Integer> ids = (!uniqueIds.isEmpty()) ? new ArrayList<Integer>(uniqueIds) : null;
				List<User> users = (ids != null && !ids.isEmpty()) ? userDao.getActiveUserByIds(ids) : null;
				
				User orgUser = acting.getStaff();
				User replace = acting.getReplacement();
				
				String act = orgUser.getStaffCode()+" - "+orgUser.getChineseName();
				String actBy = replace.getStaffCode()+" - "+replace.getChineseName();
				
				String startDate = commonService.formatDate(acting.getStartDate());
				String endDate = commonService.formatDate(acting.getEndDate());
				
				//N00009 = Please be informed that {1} will take up the duties of {0} from {2} to {3}.
				String message =  messageSource.getMessage("N00009", new Object[]{act, actBy, startDate, endDate}, Locale.ENGLISH);
				if(users != null && !users.isEmpty()){
					for(User user: users){
						notifyService.sendNotification(user, "Acting Event", message, false);
					}
				}
			}
		}	
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return false;
	}

}
