package capi.model.api.dataSync;

import java.util.Date;

public class AssignmentSyncData {

	private Integer assignmentId;
	
	private Date assignedCollectionDate;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer userId;
	
	private Date startDate;
	
	private Date endDate;
	
	private Integer outletId;
	
	private Integer collectionMethod;
	
	private Integer status;
	
	private Date collectionDate;
	
	private Integer surveyMonthId;
	
	private boolean isNewRecruitment;
	
	private String referenceNo;
	
	private String workingSession;
	
	private Integer assignedUserId;
	
	private boolean isCompleted;
	
	private String additionalFirmNo;
	
	private String additionalFirmName;
	
	private String additionalFirmAddress;
	
	private String additionalContactPerson;
	
	private Integer additionalNoOfForms;
	
	private String survey;
	
	private boolean isImportedAssignment;
	
	private Integer additionalDistrictId;
	
	private Integer additionalTpuId;
	
	private String additionalLatitude;
	
	private String additionalLongitude;
	
	private String outletDiscountRemark;
	
	private boolean lockFirmStatus;
	
	private Date approvedDate;
	
	private Integer localId;
	
	private String localDbRecordStatus; // D : delete

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Date getAssignedCollectionDate() {
		return assignedCollectionDate;
	}

	public void setAssignedCollectionDate(Date assignedCollectionDate) {
		this.assignedCollectionDate = assignedCollectionDate;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Integer getCollectionMethod() {
		return collectionMethod;
	}

	public void setCollectionMethod(Integer collectionMethod) {
		this.collectionMethod = collectionMethod;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getWorkingSession() {
		return workingSession;
	}

	public void setWorkingSession(String workingSession) {
		this.workingSession = workingSession;
	}

	public Integer getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(Integer assignedUserId) {
		this.assignedUserId = assignedUserId;
	}
	
	public String getAdditionalFirmNo() {
		return additionalFirmNo;
	}

	public void setAdditionalFirmNo(String additionalFirmNo) {
		this.additionalFirmNo = additionalFirmNo;
	}

	public String getAdditionalFirmName() {
		return additionalFirmName;
	}

	public void setAdditionalFirmName(String additionalFirmName) {
		this.additionalFirmName = additionalFirmName;
	}

	public String getAdditionalFirmAddress() {
		return additionalFirmAddress;
	}

	public void setAdditionalFirmAddress(String additionalFirmAddress) {
		this.additionalFirmAddress = additionalFirmAddress;
	}

	public String getAdditionalContactPerson() {
		return additionalContactPerson;
	}

	public void setAdditionalContactPerson(String additionalContactPerson) {
		this.additionalContactPerson = additionalContactPerson;
	}

	public Integer getAdditionalNoOfForms() {
		return additionalNoOfForms;
	}

	public void setAdditionalNoOfForms(Integer additionalNoOfForms) {
		this.additionalNoOfForms = additionalNoOfForms;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public Integer getAdditionalDistrictId() {
		return additionalDistrictId;
	}

	public void setAdditionalDistrictId(Integer additionalDistrictId) {
		this.additionalDistrictId = additionalDistrictId;
	}

	public Integer getAdditionalTpuId() {
		return additionalTpuId;
	}

	public void setAdditionalTpuId(Integer additionalTpuId) {
		this.additionalTpuId = additionalTpuId;
	}

	public String getAdditionalLatitude() {
		return additionalLatitude;
	}

	public void setAdditionalLatitude(String additionalLatitude) {
		this.additionalLatitude = additionalLatitude;
	}

	public String getOutletDiscountRemark() {
		return outletDiscountRemark;
	}

	public void setOutletDiscountRemark(String outletDiscountRemark) {
		this.outletDiscountRemark = outletDiscountRemark;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public String getAdditionalLongitude() {
		return additionalLongitude;
	}

	public void setAdditionalLongitude(String additionalLongitude) {
		this.additionalLongitude = additionalLongitude;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}

	public boolean isNewRecruitment() {
		return isNewRecruitment;
	}

	public void setNewRecruitment(boolean isNewRecruitment) {
		this.isNewRecruitment = isNewRecruitment;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean isImportedAssignment() {
		return isImportedAssignment;
	}

	public void setImportedAssignment(boolean isImportedAssignment) {
		this.isImportedAssignment = isImportedAssignment;
	}

	public boolean isLockFirmStatus() {
		return lockFirmStatus;
	}

	public void setLockFirmStatus(boolean lockFirmStatus) {
		this.lockFirmStatus = lockFirmStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((additionalContactPerson == null) ? 0 : additionalContactPerson.hashCode());
		result = prime * result + ((additionalDistrictId == null) ? 0 : additionalDistrictId.hashCode());
		result = prime * result + ((additionalFirmAddress == null) ? 0 : additionalFirmAddress.hashCode());
		result = prime * result + ((additionalFirmName == null) ? 0 : additionalFirmName.hashCode());
		result = prime * result + ((additionalFirmNo == null) ? 0 : additionalFirmNo.hashCode());
		result = prime * result + ((additionalLatitude == null) ? 0 : additionalLatitude.hashCode());
		result = prime * result + ((additionalLongitude == null) ? 0 : additionalLongitude.hashCode());
		result = prime * result + ((additionalNoOfForms == null) ? 0 : additionalNoOfForms.hashCode());
		result = prime * result + ((additionalTpuId == null) ? 0 : additionalTpuId.hashCode());
		result = prime * result + ((approvedDate == null) ? 0 : approvedDate.hashCode());
		result = prime * result + ((assignedCollectionDate == null) ? 0 : assignedCollectionDate.hashCode());
		result = prime * result + ((assignedUserId == null) ? 0 : assignedUserId.hashCode());
		result = prime * result + ((assignmentId == null) ? 0 : assignmentId.hashCode());
		result = prime * result + ((collectionDate == null) ? 0 : collectionDate.hashCode());
		result = prime * result + ((collectionMethod == null) ? 0 : collectionMethod.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + (isCompleted ? 1231 : 1237);
		result = prime * result + (isImportedAssignment ? 1231 : 1237);
		result = prime * result + (isNewRecruitment ? 1231 : 1237);
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + (lockFirmStatus ? 1231 : 1237);
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((outletDiscountRemark == null) ? 0 : outletDiscountRemark.hashCode());
		result = prime * result + ((outletId == null) ? 0 : outletId.hashCode());
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((survey == null) ? 0 : survey.hashCode());
		result = prime * result + ((surveyMonthId == null) ? 0 : surveyMonthId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((workingSession == null) ? 0 : workingSession.hashCode());
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
		AssignmentSyncData other = (AssignmentSyncData) obj;
		if (additionalContactPerson == null) {
			if (other.additionalContactPerson != null)
				return false;
		} else if (!additionalContactPerson.equals(other.additionalContactPerson))
			return false;
		if (additionalDistrictId == null) {
			if (other.additionalDistrictId != null)
				return false;
		} else if (!additionalDistrictId.equals(other.additionalDistrictId))
			return false;
		if (additionalFirmAddress == null) {
			if (other.additionalFirmAddress != null)
				return false;
		} else if (!additionalFirmAddress.equals(other.additionalFirmAddress))
			return false;
		if (additionalFirmName == null) {
			if (other.additionalFirmName != null)
				return false;
		} else if (!additionalFirmName.equals(other.additionalFirmName))
			return false;
		if (additionalFirmNo == null) {
			if (other.additionalFirmNo != null)
				return false;
		} else if (!additionalFirmNo.equals(other.additionalFirmNo))
			return false;
		if (additionalLatitude == null) {
			if (other.additionalLatitude != null)
				return false;
		} else if (!additionalLatitude.equals(other.additionalLatitude))
			return false;
		if (additionalLongitude == null) {
			if (other.additionalLongitude != null)
				return false;
		} else if (!additionalLongitude.equals(other.additionalLongitude))
			return false;
		if (additionalNoOfForms == null) {
			if (other.additionalNoOfForms != null)
				return false;
		} else if (!additionalNoOfForms.equals(other.additionalNoOfForms))
			return false;
		if (additionalTpuId == null) {
			if (other.additionalTpuId != null)
				return false;
		} else if (!additionalTpuId.equals(other.additionalTpuId))
			return false;
		if (approvedDate == null) {
			if (other.approvedDate != null)
				return false;
		} else if (!approvedDate.equals(other.approvedDate))
			return false;
		if (assignedCollectionDate == null) {
			if (other.assignedCollectionDate != null)
				return false;
		} else if (!assignedCollectionDate.equals(other.assignedCollectionDate))
			return false;
		if (assignedUserId == null) {
			if (other.assignedUserId != null)
				return false;
		} else if (!assignedUserId.equals(other.assignedUserId))
			return false;
		if (assignmentId == null) {
			if (other.assignmentId != null)
				return false;
		} else if (!assignmentId.equals(other.assignmentId))
			return false;
		if (collectionDate == null) {
			if (other.collectionDate != null)
				return false;
		} else if (!collectionDate.equals(other.collectionDate))
			return false;
		if (collectionMethod == null) {
			if (other.collectionMethod != null)
				return false;
		} else if (!collectionMethod.equals(other.collectionMethod))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (isCompleted != other.isCompleted)
			return false;
		if (isImportedAssignment != other.isImportedAssignment)
			return false;
		if (isNewRecruitment != other.isNewRecruitment)
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
		if (lockFirmStatus != other.lockFirmStatus)
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (outletDiscountRemark == null) {
			if (other.outletDiscountRemark != null)
				return false;
		} else if (!outletDiscountRemark.equals(other.outletDiscountRemark))
			return false;
		if (outletId == null) {
			if (other.outletId != null)
				return false;
		} else if (!outletId.equals(other.outletId))
			return false;
		if (referenceNo == null) {
			if (other.referenceNo != null)
				return false;
		} else if (!referenceNo.equals(other.referenceNo))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (survey == null) {
			if (other.survey != null)
				return false;
		} else if (!survey.equals(other.survey))
			return false;
		if (surveyMonthId == null) {
			if (other.surveyMonthId != null)
				return false;
		} else if (!surveyMonthId.equals(other.surveyMonthId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (workingSession == null) {
			if (other.workingSession != null)
				return false;
		} else if (!workingSession.equals(other.workingSession))
			return false;
		return true;
	}
	
}
