package capi.model.qualityControlManagement;

import java.util.Date;


public class SpotCheckPhoneCallTableList {

	private Integer spotCheckPhoneCallId;

	private Date phoneCallTimeDate;

	private String phoneCallTime;

	private Integer phoneCallResult;

	public Integer getSpotCheckPhoneCallId() {
		return spotCheckPhoneCallId;
	}

	public void setSpotCheckPhoneCallId(Integer spotCheckPhoneCallId) {
		this.spotCheckPhoneCallId = spotCheckPhoneCallId;
	}

	public Date getPhoneCallTimeDate() {
		return phoneCallTimeDate;
	}

	public void setPhoneCallTimeDate(Date phoneCallTimeDate) {
		this.phoneCallTimeDate = phoneCallTimeDate;
	}

	public String getPhoneCallTime() {
		return phoneCallTime;
	}

	public void setPhoneCallTime(String phoneCallTime) {
		this.phoneCallTime = phoneCallTime;
	}

	public Integer getPhoneCallResult() {
		return phoneCallResult;
	}

	public void setPhoneCallResult(Integer phoneCallResult) {
		this.phoneCallResult = phoneCallResult;
	}

}
