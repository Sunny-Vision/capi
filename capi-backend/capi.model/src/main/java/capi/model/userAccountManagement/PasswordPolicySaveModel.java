package capi.model.userAccountManagement;

public class PasswordPolicySaveModel {
	private Integer unlockDuration;
	private Integer maxAttempt;
	private Integer resetAttemptDuration;
	private Integer enforcePasswordHistory;
	private Integer maxAge;
	private Integer minAge;
	private Integer minLength;
	private Integer notificationDate;
	

	public Integer getNotificationDate() {
		return notificationDate;
	}
	public void setNotificationDate(Integer notificationDate) {
		this.notificationDate = notificationDate;
	}
	public Integer getUnlockDuration() {
		return unlockDuration;
	}
	public void setUnlockDuration(Integer unlockDuration) {
		this.unlockDuration = unlockDuration;
	}
	
	public Integer getMaxAttempt() {
		return maxAttempt;
	}
	public void setMaxAttempt(Integer maxAttempt) {
		this.maxAttempt = maxAttempt;
	}
	
	public Integer getResetAttemptDuration() {
		return resetAttemptDuration;
	}
	public void setResetAttemptDuration(Integer resetAttemptDuration) {
		this.resetAttemptDuration = resetAttemptDuration;
	}
	
	public Integer getEnforcePasswordHistory() {
		return enforcePasswordHistory;
	}
	public void setEnforcePasswordHistory(Integer enforcePasswordHistory) {
		this.enforcePasswordHistory = enforcePasswordHistory;
	}
	
	public Integer getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
	
	public Integer getMinAge() {
		return minAge;
	}
	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}
	
	public Integer getMinLength() {
		return minLength;
	}
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}
}
