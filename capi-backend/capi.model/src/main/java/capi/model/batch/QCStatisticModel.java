package capi.model.batch;

import capi.entity.User;

public class QCStatisticModel {

	private Integer userId;
	
	private Long formCompletedCount;
	
	private Long ghsCompletedCount;
	
	private User user;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getFormCompletedCount() {
		return formCompletedCount;
	}

	public void setFormCompletedCount(Long formCompletedCount) {
		this.formCompletedCount = formCompletedCount;
	}

	public Long getGhsCompletedCount() {
		return ghsCompletedCount;
	}

	public void setGhsCompletedCount(Long ghsCompletedCount) {
		this.ghsCompletedCount = ghsCompletedCount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
	
}
