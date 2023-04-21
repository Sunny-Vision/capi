package capi.model.api.dataSync;

public class SubPriceRecordSyncModel extends SyncModel<SubPriceRecordSyncData>{

	private Integer[] assignmentIds;

	public Integer[] getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(Integer[] assignmentIds) {
		this.assignmentIds = assignmentIds;
	}
	
}
