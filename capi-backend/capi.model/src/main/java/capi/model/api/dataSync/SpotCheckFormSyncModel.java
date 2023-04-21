package capi.model.api.dataSync;

public class SpotCheckFormSyncModel extends SyncModel<SpotCheckFormSyncData>{

	private Integer[] spotCheckFormIds;
	
	private Integer[] qcItineraryPlanIds;

	public Integer[] getSpotCheckFormIds() {
		return spotCheckFormIds;
	}

	public void setSpotCheckFormIds(Integer[] spotCheckFormIds) {
		this.spotCheckFormIds = spotCheckFormIds;
	}

	public Integer[] getQcItineraryPlanIds() {
		return qcItineraryPlanIds;
	}

	public void setQcItineraryPlanIds(Integer[] qcItineraryPlanIds) {
		this.qcItineraryPlanIds = qcItineraryPlanIds;
	}
	
}
