package capi.service.batchJob;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.SpotCheckFormDao;
import capi.dal.UserDao;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.batch.QCStatisticModel;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 * On first working day of Mar, Jun, Sep & Dec, check 
 * (i) whether at least 1, 2, 3, & 4 times of SC had been performed for each field officer respectively; 
 * (ii) whether 1 GHS SC had been performed. (if isGHS of field officer = true)
 * 
 * If not, send a summary of field officers to Field Team Head and corresponding Field Supervisor.
 * 
 */
@Service("SpotCheckReminder")
public class SpotCheckReminder implements BatchJobService{
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private SpotCheckFormDao spotCheckFormDao;
	
	@Autowired
	private NotificationService notifyService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private UserDao userDao;

	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Spot Check Reminder";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date curMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curMonth);
		int month = calendar.get(Calendar.MONTH);
		Date today = new Date();
		
		List<QCStatisticModel> statistics = spotCheckFormDao.getSpotCheckCompleteStatus();
		Boolean hasThreeContinuousMonthZeroRecords = spotCheckFormDao.checkSpotCheckContinuousMonthsRecordExist(today);	
		
		HashMap<Integer, List<QCStatisticModel>> supervisorMap = new HashMap<Integer, List<QCStatisticModel>>();
		List<QCStatisticModel> invalidStat = new ArrayList<QCStatisticModel>();
		
		if (statistics != null && statistics.size() > 0){
			for (QCStatisticModel stat: statistics){
				User user = userDao.findById(stat.getUserId());
				stat.setUser(user);
				if (stat.getFormCompletedCount() < (month+1)/3 || user.isGHS() && stat.getGhsCompletedCount() < 1 
						|| (hasThreeContinuousMonthZeroRecords != null && hasThreeContinuousMonthZeroRecords)){
					invalidStat.add(stat);
					if (supervisorMap.containsKey(user.getSupervisor().getUserId())){
						List<QCStatisticModel> models = supervisorMap.get(user.getSupervisor().getUserId());
						models.add(stat);
					} else if (user.getSupervisor() != null){
						List<QCStatisticModel> models = new ArrayList<QCStatisticModel>();
						models.add(stat);
						supervisorMap.put(user.getSupervisor().getUserId(), models);
					}					
				}
			}
		}
		
		if (invalidStat != null && invalidStat.size() > 0){
			String subject = messageSource.getMessage("N00039", null, Locale.ENGLISH);
			String contentHeader = messageSource.getMessage("N00040", null, Locale.ENGLISH);
			String content = getNotificationContent(invalidStat);
			List<User> users = userDao.getActiveUsersWithAuthorityLevel(2, null);
			
			for (User user : users){
				notifyService.sendNotification(user, subject, contentHeader+"\n"+content, false);				
			}
			
			for (Integer userId: supervisorMap.keySet()){
				User supervisor = userDao.findById(userId);
				String superContent = getNotificationContent(supervisorMap.get(userId));
				notifyService.sendNotification(supervisor, subject, contentHeader+"\n"+superContent, false);	
			}
			
		}
	}
	
	
	private String getNotificationContent(List<QCStatisticModel> models){
		StringBuilder builder = new StringBuilder();
		for (QCStatisticModel model: models){
			User user = model.getUser();
			String team = StringUtils.isBlank(user.getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : user.getTeam();
			String content = messageSource.getMessage("N00041", 
					new Object[]{
							user.getStaffCode(),
							user.getEnglishName(),
							model.getFormCompletedCount(),
							model.getGhsCompletedCount(),
							team
					}, 
					Locale.ENGLISH);
			builder.append(content+"\n");
		}
		return builder.toString();
	}
	

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		
		return (month == 2 || month ==5 || month == 8 || month == 11) && (date == 10);
	}

}
