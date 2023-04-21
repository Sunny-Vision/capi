package capi.model.api.dataSync;

import java.util.Date;

public class WorkingSessionSettingSyncData {
	private Integer workingSessionSettingId;
	
	private Date fromTime;
	
	private Date toTime;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getWorkingSessionSettingId() {
		return workingSessionSettingId;
	}

	public void setWorkingSessionSettingId(Integer workingSessionSettingId) {
		this.workingSessionSettingId = workingSessionSettingId;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createDate) {
		this.createdDate = createDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
}
