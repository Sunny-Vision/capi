package capi.model.assignmentAllocationAndReallocation;

public class UnAssignedHeadPETask {

	private Integer userId;
	
	private Boolean isFieldHead;	
	
	private Boolean isSectionHead;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Boolean getIsFieldHead() {
		return isFieldHead;
	}

	public void setIsFieldHead(Boolean isFieldHead) {
		this.isFieldHead = isFieldHead;
	}

	public Boolean getIsSectionHead() {
		return isSectionHead;
	}

	public void setIsSectionHead(Boolean isSectionHead) {
		this.isSectionHead = isSectionHead;
	}
	
}
