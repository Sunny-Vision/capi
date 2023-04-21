package capi.model.api.dataSync;

public class AssignmentSyncModel extends SyncModel<AssignmentSyncData>{

	private Integer[] assignmentIds;
	
	private Integer[] itineraryPlanIds;

	public Integer[] getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(Integer[] assignmentIds) {
		this.assignmentIds = assignmentIds;
	}

	public Integer[] getItineraryPlanIds() {
		return itineraryPlanIds;
	}

	public void setItineraryPlanIds(Integer[] itineraryPlanIds) {
		this.itineraryPlanIds = itineraryPlanIds;
	}
}
