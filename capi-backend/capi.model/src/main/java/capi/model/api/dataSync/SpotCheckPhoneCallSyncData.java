package capi.model.api.dataSync;

import java.util.Date;

public class SpotCheckPhoneCallSyncData {

	private Integer spotCheckPhoneCallId;
	
	private String phoneCallTime;
	
	private Integer result;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer spotCheckFormId;

	private Integer localId;
	
	private String localDbRecordStatus;
	
	public Integer getSpotCheckPhoneCallId() {
		return spotCheckPhoneCallId;
	}

	public void setSpotCheckPhoneCallId(Integer spotCheckPhoneCallId) {
		this.spotCheckPhoneCallId = spotCheckPhoneCallId;
	}

	public String getPhoneCallTime() {
		return phoneCallTime;
	}

	public void setPhoneCallTime(String phoneCallTime) {
		this.phoneCallTime = phoneCallTime;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}

	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((phoneCallTime == null) ? 0 : phoneCallTime.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((spotCheckFormId == null) ? 0 : spotCheckFormId.hashCode());
		result = prime * result + ((spotCheckPhoneCallId == null) ? 0 : spotCheckPhoneCallId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpotCheckPhoneCallSyncData other = (SpotCheckPhoneCallSyncData) obj;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (localDbRecordStatus == null) {
			if (other.localDbRecordStatus != null)
				return false;
		} else if (!localDbRecordStatus.equals(other.localDbRecordStatus))
			return false;
		if (localId == null) {
			if (other.localId != null)
				return false;
		} else if (!localId.equals(other.localId))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (phoneCallTime == null) {
			if (other.phoneCallTime != null)
				return false;
		} else if (!phoneCallTime.equals(other.phoneCallTime))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (spotCheckFormId == null) {
			if (other.spotCheckFormId != null)
				return false;
		} else if (!spotCheckFormId.equals(other.spotCheckFormId))
			return false;
		if (spotCheckPhoneCallId == null) {
			if (other.spotCheckPhoneCallId != null)
				return false;
		} else if (!spotCheckPhoneCallId.equals(other.spotCheckPhoneCallId))
			return false;
		return true;
	}
	
}
