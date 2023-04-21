package capi.model.api.dataSync;

import java.util.Date;

public class PECheckFormSyncData {

	private Integer peCheckFormId;
	
	private String contactPerson;
	
	private Date checkingDate;
	
	private String checkingTime;
	
	private Integer assignmentId;
	
	private Integer officerId;
	
	private Integer checkingMode;
	
	private String peCheckRemark;
	
	private String otherRemark;
	
	private String status;
	
	private boolean isNonContact;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer contactDateResult;
	
	private Integer contactTimeResult;
	
	private Integer contactDurationResult;
	
	private Integer contactModeResult;
	
	private Integer dateCollectedResult;
	
	private Integer othersResult;
	
	private Integer userId;
	
	private Integer localId;
	
	private String localDbRecordStatus;

	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}

	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Date getCheckingDate() {
		return checkingDate;
	}

	public void setCheckingDate(Date checkingDate) {
		this.checkingDate = checkingDate;
	}

	public String getCheckingTime() {
		return checkingTime;
	}

	public void setCheckingTime(String checkingTime) {
		this.checkingTime = checkingTime;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Integer getOfficerId() {
		return officerId;
	}

	public void setOfficerId(Integer officerId) {
		this.officerId = officerId;
	}

	public Integer getCheckingMode() {
		return checkingMode;
	}

	public void setCheckingMode(Integer checkingMode) {
		this.checkingMode = checkingMode;
	}

	public String getPeCheckRemark() {
		return peCheckRemark;
	}

	public void setPeCheckRemark(String peCheckRemark) {
		this.peCheckRemark = peCheckRemark;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Integer getContactDateResult() {
		return contactDateResult;
	}

	public void setContactDateResult(Integer contactDateResult) {
		this.contactDateResult = contactDateResult;
	}

	public Integer getContactTimeResult() {
		return contactTimeResult;
	}

	public void setContactTimeResult(Integer contactTimeResult) {
		this.contactTimeResult = contactTimeResult;
	}

	public Integer getContactDurationResult() {
		return contactDurationResult;
	}

	public void setContactDurationResult(Integer contactDurationResult) {
		this.contactDurationResult = contactDurationResult;
	}

	public Integer getContactModeResult() {
		return contactModeResult;
	}

	public void setContactModeResult(Integer contactModeResult) {
		this.contactModeResult = contactModeResult;
	}

	public Integer getDateCollectedResult() {
		return dateCollectedResult;
	}

	public void setDateCollectedResult(Integer dateCollectedResult) {
		this.dateCollectedResult = dateCollectedResult;
	}

	public Integer getOthersResult() {
		return othersResult;
	}

	public void setOthersResult(Integer othersResult) {
		this.othersResult = othersResult;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public boolean isNonContact() {
		return isNonContact;
	}

	public void setNonContact(boolean isNonContact) {
		this.isNonContact = isNonContact;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignmentId == null) ? 0 : assignmentId.hashCode());
		result = prime * result + ((checkingDate == null) ? 0 : checkingDate.hashCode());
		result = prime * result + ((checkingMode == null) ? 0 : checkingMode.hashCode());
		result = prime * result + ((checkingTime == null) ? 0 : checkingTime.hashCode());
		result = prime * result + ((contactDateResult == null) ? 0 : contactDateResult.hashCode());
		result = prime * result + ((contactDurationResult == null) ? 0 : contactDurationResult.hashCode());
		result = prime * result + ((contactModeResult == null) ? 0 : contactModeResult.hashCode());
		result = prime * result + ((contactPerson == null) ? 0 : contactPerson.hashCode());
		result = prime * result + ((contactTimeResult == null) ? 0 : contactTimeResult.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((dateCollectedResult == null) ? 0 : dateCollectedResult.hashCode());
		result = prime * result + (isNonContact ? 1231 : 1237);
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((officerId == null) ? 0 : officerId.hashCode());
		result = prime * result + ((otherRemark == null) ? 0 : otherRemark.hashCode());
		result = prime * result + ((othersResult == null) ? 0 : othersResult.hashCode());
		result = prime * result + ((peCheckFormId == null) ? 0 : peCheckFormId.hashCode());
		result = prime * result + ((peCheckRemark == null) ? 0 : peCheckRemark.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		PECheckFormSyncData other = (PECheckFormSyncData) obj;
		if (assignmentId == null) {
			if (other.assignmentId != null)
				return false;
		} else if (!assignmentId.equals(other.assignmentId))
			return false;
		if (checkingDate == null) {
			if (other.checkingDate != null)
				return false;
		} else if (!checkingDate.equals(other.checkingDate))
			return false;
		if (checkingMode == null) {
			if (other.checkingMode != null)
				return false;
		} else if (!checkingMode.equals(other.checkingMode))
			return false;
		if (checkingTime == null) {
			if (other.checkingTime != null)
				return false;
		} else if (!checkingTime.equals(other.checkingTime))
			return false;
		if (contactDateResult == null) {
			if (other.contactDateResult != null)
				return false;
		} else if (!contactDateResult.equals(other.contactDateResult))
			return false;
		if (contactDurationResult == null) {
			if (other.contactDurationResult != null)
				return false;
		} else if (!contactDurationResult.equals(other.contactDurationResult))
			return false;
		if (contactModeResult == null) {
			if (other.contactModeResult != null)
				return false;
		} else if (!contactModeResult.equals(other.contactModeResult))
			return false;
		if (contactPerson == null) {
			if (other.contactPerson != null)
				return false;
		} else if (!contactPerson.equals(other.contactPerson))
			return false;
		if (contactTimeResult == null) {
			if (other.contactTimeResult != null)
				return false;
		} else if (!contactTimeResult.equals(other.contactTimeResult))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (dateCollectedResult == null) {
			if (other.dateCollectedResult != null)
				return false;
		} else if (!dateCollectedResult.equals(other.dateCollectedResult))
			return false;
		if (isNonContact != other.isNonContact)
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
		if (officerId == null) {
			if (other.officerId != null)
				return false;
		} else if (!officerId.equals(other.officerId))
			return false;
		if (otherRemark == null) {
			if (other.otherRemark != null)
				return false;
		} else if (!otherRemark.equals(other.otherRemark))
			return false;
		if (othersResult == null) {
			if (other.othersResult != null)
				return false;
		} else if (!othersResult.equals(other.othersResult))
			return false;
		if (peCheckFormId == null) {
			if (other.peCheckFormId != null)
				return false;
		} else if (!peCheckFormId.equals(other.peCheckFormId))
			return false;
		if (peCheckRemark == null) {
			if (other.peCheckRemark != null)
				return false;
		} else if (!peCheckRemark.equals(other.peCheckRemark))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
}
