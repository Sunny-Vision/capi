package capi.model.api.onlineFunction;

import capi.model.api.dataSync.SyncModel;

public class CheckStatusSyncModel extends SyncModel<CheckAssignmentAndQuotationRecordStatus> {
	private Integer[] assignmentIds;

	private Integer[] quotationRecordIds;

	public Integer[] getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(Integer[] assignmentIds) {
		this.assignmentIds = assignmentIds;
	}

	public Integer[] getQuotationRecordIds() {
		return quotationRecordIds;
	}

	public void setQuotationRecordIds(Integer[] quotationRecordIds) {
		this.quotationRecordIds = quotationRecordIds;
	}

	
}
