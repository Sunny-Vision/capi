package capi.model.userAccountManagement;

public class PasswordPolicyDisplayModel {
	
	private String unlockDuration;
	private String maxAttempt;
	private String resetAttemptDuration;
	private String enforcePasswordHistory;
	private String maxAge;
	private String minAge;
	private String minLength;
	
	private String notificationDate;

	public String getNotificationDate() {
		return notificationDate;
	}
	public void setNotificationDate(String notificationDate) {
		this.notificationDate = notificationDate;
	}

	public String getUnlockDuration() {
		return unlockDuration;
	}
	public void setUnlockDuration(String unlockDuration) {
		this.unlockDuration = unlockDuration;
	}
	
	public String getMaxAttempt() {
		return maxAttempt;
	}
	public void setMaxAttempt(String maxAttempt) {
		this.maxAttempt = maxAttempt;
	}
	
	public String getResetAttemptDuration() {
		return resetAttemptDuration;
	}
	public void setResetAttemptDuration(String resetAttemptDuration) {
		this.resetAttemptDuration = resetAttemptDuration;
	}
	
	public String getEnforcePasswordHistory() {
		return enforcePasswordHistory;
	}
	public void setEnforcePasswordHistory(String enforcePasswordHistory) {
		this.enforcePasswordHistory = enforcePasswordHistory;
	}
	
	public String getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(String maxAge) {
		this.maxAge = maxAge;
	}
	
	public String getMinAge() {
		return minAge;
	}
	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}
	
	public String getMinLength() {
		return minLength;
	}
	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}

}
