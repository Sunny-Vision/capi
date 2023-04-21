package capi.model.api.dataSync;

import java.util.Date;

public class SupervisoryVisitDetailSyncData {

	private Integer supervisoryVisitDetailId;
	
	private Integer assignmentId;
	
	private String survey;
	
	private Integer result;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer supervisoryVisitFormId;
	
	private String otherRemark;
	
	private String referenceNo;
	
	private Integer localId;

	private String localDbRecordStatus;
	
	public Integer getSupervisoryVisitDetailId() {
		return supervisoryVisitDetailId;
	}

	public void setSupervisoryVisitDetailId(Integer supervisoryVisitDetailId) {
		this.supervisoryVisitDetailId = supervisoryVisitDetailId;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
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

	public Integer getSupervisoryVisitFormId() {
		return supervisoryVisitFormId;
	}

	public void setSupervisoryVisitFormId(Integer supervisoryVisitFormId) {
		this.supervisoryVisitFormId = supervisoryVisitFormId;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
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
		result = prime * result + ((assignmentId == null) ? 0 : assignmentId.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((otherRemark == null) ? 0 : otherRemark.hashCode());
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((supervisoryVisitDetailId == null) ? 0 : supervisoryVisitDetailId.hashCode());
		result = prime * result + ((supervisoryVisitFormId == null) ? 0 : supervisoryVisitFormId.hashCode());
		result = prime * result + ((survey == null) ? 0 : survey.hashCode());
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
		SupervisoryVisitDetailSyncData other = (SupervisoryVisitDetailSyncData) obj;
		if (assignmentId == null) {
			if (other.assignmentId != null)
				return false;
		} else if (!assignmentId.equals(other.assignmentId))
			return false;
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
		if (supervisoryVisitDetailId == null) {
			if (other.supervisoryVisitDetailId != null)
				return false;
		} else if (!supervisoryVisitDetailId.equals(other.supervisoryVisitDetailId))
			return false;
		if (supervisoryVisitFormId == null) {
			if (other.supervisoryVisitFormId != null)
				return false;
		} else if (!supervisoryVisitFormId.equals(other.supervisoryVisitFormId))
			return false;
		if (survey == null) {
			if (other.survey != null)
				return false;
		} else if (!survey.equals(other.survey))
			return false;
		return true;
	}
	
}
