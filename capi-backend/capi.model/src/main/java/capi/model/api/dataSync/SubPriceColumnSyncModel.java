package capi.model.api.dataSync;

public class SubPriceColumnSyncModel extends SyncModel<SubPriceColumnSyncData>{

	private Integer[] assignmentIds;

	public Integer[] getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(Integer[] assignmentIds) {
		this.assignmentIds = assignmentIds;
	}
	
}
