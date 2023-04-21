package capi.model.api.dataSync;

import java.util.Date;

public class GetModel{

	private Date lastSyncTime;
	
	private Integer[] assignmentIds;
	
	private Integer[] quotationIds;

	public Integer[] getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(Integer[] assignmentIds) {
		this.assignmentIds = assignmentIds;
	}

	public Integer[] getQuotationIds() {
		return quotationIds;
	}

	public void setQuotationIds(Integer[] quotationIds) {
		this.quotationIds = quotationIds;
	}

	public Date getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(Date lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}
	
	
}
