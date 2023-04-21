package capi.model.api.dataSync;

public class AssignmentUnitCategorySyncModel extends SyncModel<AssignmentUnitCategoryInfoSyncData>{

	private Integer[] assignmentIds;

	public Integer[] getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(Integer[] assignmentIds) {
		this.assignmentIds = assignmentIds;
	}
	
}
