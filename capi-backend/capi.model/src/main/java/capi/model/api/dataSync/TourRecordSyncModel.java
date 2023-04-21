package capi.model.api.dataSync;

public class TourRecordSyncModel extends SyncModel<TourRecordSyncData>{

	private Integer[] assignmentIds;

	public Integer[] getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(Integer[] assignmentIds) {
		this.assignmentIds = assignmentIds;
	}
	
}
