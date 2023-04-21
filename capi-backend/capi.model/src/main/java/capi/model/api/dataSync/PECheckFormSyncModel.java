package capi.model.api.dataSync;

public class PECheckFormSyncModel extends SyncModel<PECheckFormSyncData>{

	private Integer[] peCheckFormIds;

	private Integer[] qcItineraryPlanIds;
	
	public Integer[] getPeCheckFormIds() {
		return peCheckFormIds;
	}

	public void setPeCheckFormIds(Integer[] peCheckFormIds) {
		this.peCheckFormIds = peCheckFormIds;
	}

	public Integer[] getQcItineraryPlanIds() {
		return qcItineraryPlanIds;
	}

	public void setQcItineraryPlanIds(Integer[] qcItineraryPlanIds) {
		this.qcItineraryPlanIds = qcItineraryPlanIds;
	}
	
}
