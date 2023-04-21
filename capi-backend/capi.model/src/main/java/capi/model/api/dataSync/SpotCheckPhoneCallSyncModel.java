package capi.model.api.dataSync;

public class SpotCheckPhoneCallSyncModel extends SyncModel<SpotCheckPhoneCallSyncData>{

	private Integer[] spotCheckFormIds;

	public Integer[] getSpotCheckFormIds() {
		return spotCheckFormIds;
	}

	public void setSpotCheckFormIds(Integer[] spotCheckFormIds) {
		this.spotCheckFormIds = spotCheckFormIds;
	}
	
}
