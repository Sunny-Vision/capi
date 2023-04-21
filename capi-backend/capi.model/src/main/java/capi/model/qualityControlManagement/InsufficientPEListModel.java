package capi.model.qualityControlManagement;

public class InsufficientPEListModel {

	private Integer userId;
	
	private Long totalAssignment;
	
	private Long peAssignment;
	
	private Integer remaining;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getTotalAssignment() {
		return totalAssignment;
	}

	public void setTotalAssignment(Long totalAssignment) {
		this.totalAssignment = totalAssignment;
	}

	public Long getPeAssignment() {
		return peAssignment;
	}

	public void setPeAssignment(Long peAssignment) {
		this.peAssignment = peAssignment;
	}

	public Integer getRemaining() {
		return remaining;
	}

	public void setRemaining(Integer remaining) {
		this.remaining = remaining;
	}
}
