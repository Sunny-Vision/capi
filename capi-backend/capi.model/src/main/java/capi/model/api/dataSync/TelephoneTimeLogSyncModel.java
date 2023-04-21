package capi.model.api.dataSync;

public class TelephoneTimeLogSyncModel extends SyncModel<TelephoneTimeLogSyncData>{

	private Integer[] timeLogIds;

	public Integer[] getTimeLogIds() {
		return timeLogIds;
	}

	public void setTimeLogIds(Integer[] timeLogIds) {
		this.timeLogIds = timeLogIds;
	}
	
}
