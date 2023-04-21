package capi.model.api.dataSync;

import java.util.Date;

public class TimeLogSyncModel extends SyncModel<TimeLogSyncData>{

	private Integer[] timeLogIds;
	
	private Date [] dates;

	public Integer[] getTimeLogIds() {
		return timeLogIds;
	}

	public void setTimeLogIds(Integer[] timeLogIds) {
		this.timeLogIds = timeLogIds;
	}

	public Date[] getDates() {
		return dates;
	}

	public void setDates(Date[] dates) {
		this.dates = dates;
	}
}
