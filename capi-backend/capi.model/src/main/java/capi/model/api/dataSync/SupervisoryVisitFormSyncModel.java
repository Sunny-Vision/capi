package capi.model.api.dataSync;

public class SupervisoryVisitFormSyncModel extends SyncModel<SupervisoryVisitFormSyncData>{
	
	private Integer[] supervisoryVisitFormIds;

	private Integer[] qcItineraryPlanIds;
	
	public Integer[] getSupervisoryVisitFormIds() {
		return supervisoryVisitFormIds;
	}

	public void setSupervisoryVisitFormIds(Integer[] supervisoryVisitFormIds) {
		this.supervisoryVisitFormIds = supervisoryVisitFormIds;
	}

	public Integer[] getQcItineraryPlanIds() {
		return qcItineraryPlanIds;
	}

	public void setQcItineraryPlanIds(Integer[] qcItineraryPlanIds) {
		this.qcItineraryPlanIds = qcItineraryPlanIds;
	}
	
}
