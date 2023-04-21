package capi.model.api.dataSync;

import java.util.Date;

public class SpotCheckResultSyncData {

	private Integer spotCheckResultId;
	
	private String result;
	
	private String otherRemark;
	
	private Integer spotCheckFormId;
	
	private String referenceNo;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer localId;
	
	private String localDbRecordStatus;

	public Integer getSpotCheckResultId() {
		return spotCheckResultId;
	}

	public void setSpotCheckResultId(Integer spotCheckResultId) {
		this.spotCheckResultId = spotCheckResultId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}

	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
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

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((otherRemark == null) ? 0 : otherRemark.hashCode());
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((spotCheckFormId == null) ? 0 : spotCheckFormId.hashCode());
		result = prime * result + ((spotCheckResultId == null) ? 0 : spotCheckResultId.hashCode());
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
		SpotCheckResultSyncData other = (SpotCheckResultSyncData) obj;
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
		if (otherRemark == null) {
			if (other.otherRemark != null)
				return false;
		} else if (!otherRemark.equals(other.otherRemark))
			return false;
		if (referenceNo == null) {
			if (other.referenceNo != null)
				return false;
		} else if (!referenceNo.equals(other.referenceNo))
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
		if (spotCheckResultId == null) {
			if (other.spotCheckResultId != null)
				return false;
		} else if (!spotCheckResultId.equals(other.spotCheckResultId))
			return false;
		return true;
	}
	
}
