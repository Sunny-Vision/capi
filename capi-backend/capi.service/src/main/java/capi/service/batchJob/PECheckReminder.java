package capi.service.batchJob;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.PECheckFormDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.Outlet;
import capi.entity.PECheckForm;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * 
 * @author stanley_tsang
 *  PE Check cases of reference month T should be performed before the reference month T+2.  
 *  If not, send a summary of unfinished cases to the corresponding user and Field Team Head.
 * 
 */
@Service("PECheckReminder")
public class PECheckReminder implements BatchJobService{
	@Autowired
	private PECheckFormDao peCheckFormDao;
	
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
		return "PE Check Reminder";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		List<PECheckForm> forms = peCheckFormDao.getPEReminderList();
		if (forms != null && forms.size() > 0){
//			HashMap<User, List<PECheckForm>> peFormMap = new HashMap<User, List<PECheckForm>>();
//			for (PECheckForm form : forms){
//				if (peFormMap.containsKey(form.getUser())){
//					List<PECheckForm> list = peFormMap.get(form.getUser());
//					list.add(form);
//				}
//				else{
//					List<PECheckForm> list = new ArrayList<PECheckForm>();
//					list.add(form);
//					peFormMap.put(form.getUser(), list);
//				}				
//			}
			String subject = messageSource.getMessage("N00036", null, Locale.ENGLISH);
			
			//Send Notification to Field Team Head and Section Head
			List<User> users = new ArrayList<User>();
			users.addAll(userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD, null));
			users.addAll(userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD, null));
			
			String contentHeader = messageSource.getMessage("N00037", null, Locale.ENGLISH);
			String fieldHeadContent = fomulatePEMessage(forms);
			List<Integer> userIds = new ArrayList<Integer>();
			for (User user : users){
				if (userIds.contains(user.getUserId())){
					continue;
				} else {
					userIds.add(user.getUserId());
				}
				notifyService.sendNotification(user, subject, contentHeader+"\n"+fieldHeadContent, false);
			}
			
//			for (User user : peFormMap.keySet()){
//				String content = fomulatePEMessage(peFormMap.get(user));
//				notifyService.sendNotification(user, subject, contentHeader+"\n"+content, false);
//			}
			
		}
	}
	
	
	private String fomulatePEMessage(List<PECheckForm> list){
		StringBuilder builder = new StringBuilder();
		for (PECheckForm form : list){
			Assignment assignment = form.getAssignment();
			Outlet outlet = assignment.getOutlet();
			User officer = assignment.getUser();
			String team = StringUtils.isBlank(officer.getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : officer.getTeam();
			builder.append(messageSource.getMessage("N00038", 
					new Object[]{outlet.getFirmCode(), outlet.getName(), team, officer.getStaffCode(), officer.getEnglishName()}, 
					Locale.ENGLISH)+"\n");
		}
		return builder.toString();
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		Date today = commonService.getDateWithoutTime(new Date());
		Date nextMonth = DateUtils.truncate(DateUtils.addMonths(today, 1), Calendar.MONTH);
		Date lastDayOfMonth = DateUtils.addDays(nextMonth, -1);
		
		return today.equals(lastDayOfMonth);
	}

}
