package capi.model.assignmentAllocationAndReallocation;

import java.util.Date;

public class AssignmentReallocationTableList {

	private Integer assignmentReallocationId;

	private Integer originalUserId;

	private String originalUser;

	private Integer targetUserId;

	private String targetUser;

	private String status;
	
	private String createdDate;
	
	private Date createdDate2;

	public Integer getAssignmentReallocationId() {
		return assignmentReallocationId;
	}

	public void setAssignmentReallocationId(Integer assignmentReallocationId) {
		this.assignmentReallocationId = assignmentReallocationId;
	}

	public Integer getOriginalUserId() {
		return originalUserId;
	}

	public void setOriginalUserId(Integer originalUserId) {
		this.originalUserId = originalUserId;
	}

	public String getOriginalUser() {
		return originalUser;
	}

	public void setOriginalUser(String originalUser) {
		this.originalUser = originalUser;
	}

	public Integer getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(Integer targetUserId) {
		this.targetUserId = targetUserId;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate2() {
		return createdDate2;
	}

	public void setCreatedDate2(Date createdDate2) {
		this.createdDate2 = createdDate2;
	}

}
