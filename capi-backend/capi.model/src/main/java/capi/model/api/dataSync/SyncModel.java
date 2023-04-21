package capi.model.api.dataSync;

import java.util.Date;
import java.util.List;

public class SyncModel<T> {

	private Date lastSyncTime;
	
	private Boolean dataReturn;
	
	private List<T> data;

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

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
}
