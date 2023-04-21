package capi.model.masterMaintenance.businessParameterMaintenance;

import java.util.List;

public class WorkingSessionSaveModel {
	
	private List<WorkingSessionModel> newWorkingSessions;
	private List<WorkingSessionModel> workingSessions;
	public List<WorkingSessionModel> getNewWorkingSessions() {
		return newWorkingSessions;
	}
	public void setNewWorkingSessions(List<WorkingSessionModel> newWorkingSessions) {
		this.newWorkingSessions = newWorkingSessions;
	}
	public List<WorkingSessionModel> getWorkingSessions() {
		return workingSessions;
	}
	public void setWorkingSessions(List<WorkingSessionModel> workingSessions) {
		this.workingSessions = workingSessions;
	}
	
	
}
