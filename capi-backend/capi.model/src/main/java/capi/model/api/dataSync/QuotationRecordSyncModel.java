package capi.model.api.dataSync;

public class QuotationRecordSyncModel extends SyncModel<QuotationRecordSyncData>{

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
