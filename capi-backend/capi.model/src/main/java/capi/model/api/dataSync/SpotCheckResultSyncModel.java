package capi.model.api.dataSync;

public class SpotCheckResultSyncModel extends SyncModel<SpotCheckResultSyncData>{

	private Integer[] spotCheckFormIds;

	public Integer[] getSpotCheckFormIds() {
		return spotCheckFormIds;
	}

	public void setSpotCheckFormIds(Integer[] spotCheckFormIds) {
		this.spotCheckFormIds = spotCheckFormIds;
	}
	
}
