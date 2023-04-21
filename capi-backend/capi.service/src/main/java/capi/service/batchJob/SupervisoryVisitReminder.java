package capi.service.batchJob;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.SupervisoryVisitFormDao;
import capi.dal.UserDao;
import capi.entity.User;
import capi.model.batch.QCStatisticModel;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * ** On first working day of Jun & Dec, check 
 * (i) whether at least 1 & 2 times of SV had been performed for each field officer respectively; 
 * (ii) whether 1 GHS Night SV had been performed.
 * @author stanley_tsang
 *
 */
@Service("SupervisoryVisitReminder")
public class SupervisoryVisitReminder implements BatchJobService{
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private NotificationService notifyService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitDao;

	@Autowired
	private UserDao userDao;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Supervisory Visit Reminder";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		Date curMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curMonth);
		int month = calendar.get(Calendar.MONTH);
		Date today = new Date();
		
		List<QCStatisticModel> statistics = supervisoryVisitDao.getSupervisoryVisitCompleteStatus();
		Boolean hasThreeContinuousMonthZeroRecords = supervisoryVisitDao.checkSVContinuousMonthsRecordExist(today);	
		
		HashMap<User, List<QCStatisticModel>> supervisorMap = new HashMap<User, List<QCStatisticModel>>();
		List<QCStatisticModel> invalidStat = new ArrayList<QCStatisticModel>();
		
		if (statistics != null && statistics.size() > 0){
			for (QCStatisticModel stat: statistics){
				User user = userDao.findById(stat.getUserId());
				stat.setUser(user);
				if (stat.getFormCompletedCount() < (month+1)/6 || user.isGHS() && stat.getGhsCompletedCount() < 1 
						|| (hasThreeContinuousMonthZeroRecords != null && hasThreeContinuousMonthZeroRecords)){
					invalidStat.add(stat);
					User supervisor = user.getSupervisor();
					if (supervisorMap.containsKey(supervisor)){
						List<QCStatisticModel> models = supervisorMap.get(supervisor);
						models.add(stat);
					}
					else{
						List<QCStatisticModel> models = new ArrayList<QCStatisticModel>();
						models.add(stat);
						supervisorMap.put(supervisor, models);
					}					
				}
			}
		}
		
		if (invalidStat != null && invalidStat.size() > 0){
			String refYear = Integer.toString(calendar.get(Calendar.YEAR)).toString();
			String subject = messageSource.getMessage("N00042", null, Locale.ENGLISH);
			String contentHeader = messageSource.getMessage("N00043", new Object[]{refYear}, Locale.ENGLISH);
			String content = getNotificationContent(invalidStat);
			List<User> users = userDao.getActiveUsersWithAuthorityLevel(2, null);
			
			for (User user : users){
				notifyService.sendNotification(user, subject, contentHeader+"\n"+content, false);				
			}
			
			for (User user: supervisorMap.keySet()){
				String superContent = getNotificationContent(supervisorMap.get(user));
				notifyService.sendNotification(user, subject, contentHeader+"\n"+superContent, false);	
			}
			
		}
	}
	
	
	private String getNotificationContent(List<QCStatisticModel> models){
		StringBuilder builder = new StringBuilder();
		for (QCStatisticModel model: models){
			User user = model.getUser();
			String content = messageSource.getMessage("N00044", 
					new Object[]{
							user.getStaffCode(),
							user.getEnglishName(),
							model.getFormCompletedCount(),
							model.getGhsCompletedCount()
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
		
		return (month ==5 || month == 11) && (date==10);
	}

}
