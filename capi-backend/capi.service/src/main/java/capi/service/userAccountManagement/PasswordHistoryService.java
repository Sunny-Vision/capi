package capi.service.userAccountManagement;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import capi.dal.PasswordHistoryDao;
import capi.entity.PasswordHistory;
import capi.entity.User;

@Service("PasswordHistoryService")
public class PasswordHistoryService {

	@Autowired
	private PasswordHistoryDao passwordHistoryDao;
	
	@Autowired
	private PasswordPolicyService passwordPolicyService;
	
	public boolean isUserPasswordHistoryExsit(User user){
		
		if (getUserLatestPasswordHistory(user) == null) {
			return false;
		}
		
		return true;
	}
	
	
	public void savePasswordHistory(User user, String password, boolean isHashed) {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
		String hashPass = !isHashed? encoder.encodePassword(password, null): password;
		passwordHistoryDao.savePasswordHistoryByUser(user, hashPass);
	}
	
	public boolean isPasswordEnforced(User user, String newHashPass) {
		Integer enforcePasswordHistory =  passwordPolicyService.getEnforcePasswordHistoryInteger();
		return passwordHistoryDao.isPasswordEnforced(user, newHashPass, enforcePasswordHistory);
	}
	
public boolean inPasswordMinAge(User user) {
		
		Date passwordUpdateDate = user.getModifiedDate();
		PasswordHistory model = passwordHistoryDao.getUserLatestPasswordHistory(user);
		if (model != null) {
			passwordUpdateDate = model.getPasswordUpdateDate();
		} 
		
		Integer minAge =  passwordPolicyService.getMinAgeInteger();
		if (minAge==0) {
			return false;
		}
		
		long passedDay = getDateDiff(passwordUpdateDate, new Date(), TimeUnit.DAYS);
		if (passedDay <= minAge) {
			//cannot update password
			return true;
		}
	
		return false;
	}
	
	public boolean isPasswordExpired(User user) {
		
		Date passwordUpdateDate = user.getModifiedDate();
		PasswordHistory oldEntity = getUserLatestPasswordHistory(user);
		if (oldEntity != null)  {
			passwordUpdateDate = oldEntity.getPasswordUpdateDate();
		}
		
		long passedDays = getDateDiff(passwordUpdateDate, new Date(), TimeUnit.DAYS);
		
		Integer maxAge = passwordPolicyService.getMaxAgeInteger();
		if (passedDays >= maxAge) {
			return true;
		}
		
		return false;
	}
	
	public Date getPasswordExpiredDate(User user) {
		
		
		Date userLastUpdateDate = user.getModifiedDate();
		PasswordHistory model = getUserLatestPasswordHistory(user);
		
		if (model != null) {
			userLastUpdateDate = model.getPasswordUpdateDate();
		}
		Integer maxAge = passwordPolicyService.getMaxAgeInteger();
		Calendar c = Calendar.getInstance();
		c.setTime(userLastUpdateDate); 
		c.add(Calendar.DATE, maxAge);
		Date expiredDate = c.getTime();
		return expiredDate;
		
	}
	
	
	public PasswordHistory getUserLatestPasswordHistory(User user) {
		return passwordHistoryDao.getUserLatestPasswordHistory(user);
	}
	

	/**
	 * Get a difference between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the difference
	 * @return the difference value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
}


