package capi.model.api.dataSync;

import java.util.Date;
import java.util.List;

public class OutletTypeOutletSyncModel {

	private List<OutletTypeOutletSyncData> data;
	
	private Date lastSyncTime;
	
	private Boolean dataReturn;

	public Date getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(Date lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public Boolean getDataReturn() {
		return dataReturn;
	}

	public void setDataReturn(Boolean dataReturn) {
		this.dataReturn = dataReturn;
	}

	public List<OutletTypeOutletSyncData> getData() {
		return data;
	}

	public void setData(List<OutletTypeOutletSyncData> data) {
		this.data = data;
	}
	
}
