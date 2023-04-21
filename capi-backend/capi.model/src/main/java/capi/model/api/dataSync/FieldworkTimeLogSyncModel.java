package capi.model.api.dataSync;

public class FieldworkTimeLogSyncModel extends SyncModel<FieldworkTimeLogSyncData>{

	private Integer[] timeLogIds;

	public Integer[] getTimeLogIds() {
		return timeLogIds;
	}

	public void setTimeLogIds(Integer[] timeLogIds) {
		this.timeLogIds = timeLogIds;
	}
	
}
