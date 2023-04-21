package capi.model.api.dataSync;

import java.util.Date;

public class TelephoneTimeLogSyncData {

	private Integer telephoneTimeLogId;
	
	private Date referenceMonth;
	
	private String survey;
	
	private String caseReferenceNo;
	
	private String status;
	
	private String session;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer timeLogId;
	
	private Integer assignmentId;
	
	private Integer totalQuotation;
	
	private Integer quotationCount;
	
	private Integer localId;
	
	private String localDbRecordStatus;

	
	public Integer getTelephoneTimeLogId() {
		return telephoneTimeLogId;
	}

	public void setTelephoneTimeLogId(Integer telephoneTimeLogId) {
		this.telephoneTimeLogId = telephoneTimeLogId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public String getCaseReferenceNo() {
		return caseReferenceNo;
	}

	public void setCaseReferenceNo(String caseReferenceNo) {
		this.caseReferenceNo = caseReferenceNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
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

	public Integer getTimeLogId() {
		return timeLogId;
	}

	public void setTimeLogId(Integer timeLogId) {
		this.timeLogId = timeLogId;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Integer getTotalQuotation() {
		return totalQuotation;
	}

	public void setTotalQuotation(Integer totalQuotation) {
		this.totalQuotation = totalQuotation;
	}

	public Integer getQuotationCount() {
		return quotationCount;
	}

	public void setQuotationCount(Integer quotationCount) {
		this.quotationCount = quotationCount;
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
		result = prime * result + ((assignmentId == null) ? 0 : assignmentId.hashCode());
		result = prime * result + ((caseReferenceNo == null) ? 0 : caseReferenceNo.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((quotationCount == null) ? 0 : quotationCount.hashCode());
		result = prime * result + ((referenceMonth == null) ? 0 : referenceMonth.hashCode());
		result = prime * result + ((session == null) ? 0 : session.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((survey == null) ? 0 : survey.hashCode());
		result = prime * result + ((telephoneTimeLogId == null) ? 0 : telephoneTimeLogId.hashCode());
		result = prime * result + ((timeLogId == null) ? 0 : timeLogId.hashCode());
		result = prime * result + ((totalQuotation == null) ? 0 : totalQuotation.hashCode());
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
		TelephoneTimeLogSyncData other = (TelephoneTimeLogSyncData) obj;
		if (assignmentId == null) {
			if (other.assignmentId != null)
				return false;
		} else if (!assignmentId.equals(other.assignmentId))
			return false;
		if (caseReferenceNo == null) {
			if (other.caseReferenceNo != null)
				return false;
		} else if (!caseReferenceNo.equals(other.caseReferenceNo))
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
		if (quotationCount == null) {
			if (other.quotationCount != null)
				return false;
		} else if (!quotationCount.equals(other.quotationCount))
			return false;
		if (referenceMonth == null) {
			if (other.referenceMonth != null)
				return false;
		} else if (!referenceMonth.equals(other.referenceMonth))
			return false;
		if (session == null) {
			if (other.session != null)
				return false;
		} else if (!session.equals(other.session))
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
		if (telephoneTimeLogId == null) {
			if (other.telephoneTimeLogId != null)
				return false;
		} else if (!telephoneTimeLogId.equals(other.telephoneTimeLogId))
			return false;
		if (timeLogId == null) {
			if (other.timeLogId != null)
				return false;
		} else if (!timeLogId.equals(other.timeLogId))
			return false;
		if (totalQuotation == null) {
			if (other.totalQuotation != null)
				return false;
		} else if (!totalQuotation.equals(other.totalQuotation))
			return false;
		return true;
	}
	
}
