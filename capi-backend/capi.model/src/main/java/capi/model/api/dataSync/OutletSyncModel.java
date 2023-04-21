package capi.model.api.dataSync;

public class OutletSyncModel extends SyncModel<OutletSyncData>{

	private Integer[] outletIds;

	public Integer[] getOutletIds() {
		return outletIds;
	}

	public void setOutletIds(Integer[] outletIds) {
		this.outletIds = outletIds;
	}
	
}
