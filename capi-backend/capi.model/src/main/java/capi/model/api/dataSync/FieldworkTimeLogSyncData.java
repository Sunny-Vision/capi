package capi.model.api.dataSync;

import java.util.Date;

public class FieldworkTimeLogSyncData {

	private Integer fieldworkTimeLogId;
	
	private Date referenceMonth;
	
	private String startTime;
	
	private String survey;
	
	private String caseReferenceNo;
	
	private Integer assignmentId;
	
	private String activity;
	
	private String enumerationOutcome;
	
	private Integer building;
	
	private String destination;
	
	private String transport;
	
	private Double expenses;
	
	private String remark;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer timeLogId;
	
	private String endTime;
	
	private Integer totalQuotation;
	
	private Integer quotationCount;
	
	private Integer recordType;
	
	private String fromLocation;
	
	private String toLocation;
	
	private boolean includeInTransportForm;
	
	private boolean transit;
	
	private Integer localId;
	
	private String localDbRecordStatus;

	
	public Integer getFieldworkTimeLogId() {
		return fieldworkTimeLogId;
	}

	public void setFieldworkTimeLogId(Integer fieldworkTimeLogId) {
		this.fieldworkTimeLogId = fieldworkTimeLogId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getEnumerationOutcome() {
		return enumerationOutcome;
	}

	public void setEnumerationOutcome(String enumerationOutcome) {
		this.enumerationOutcome = enumerationOutcome;
	}

	public Integer getBuilding() {
		return building;
	}

	public void setBuilding(Integer building) {
		this.building = building;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public Double getExpenses() {
		return expenses;
	}

	public void setExpenses(Double expenses) {
		this.expenses = expenses;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
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

	public boolean isIncludeInTransportForm() {
		return includeInTransportForm;
	}

	public void setIncludeInTransportForm(boolean includeInTransportForm) {
		this.includeInTransportForm = includeInTransportForm;
	}

	public boolean isTransit() {
		return transit;
	}

	public void setTransit(boolean transit) {
		this.transit = transit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activity == null) ? 0 : activity.hashCode());
		result = prime * result + ((assignmentId == null) ? 0 : assignmentId.hashCode());
		result = prime * result + ((building == null) ? 0 : building.hashCode());
		result = prime * result + ((caseReferenceNo == null) ? 0 : caseReferenceNo.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((enumerationOutcome == null) ? 0 : enumerationOutcome.hashCode());
		result = prime * result + ((expenses == null) ? 0 : expenses.hashCode());
		result = prime * result + ((fieldworkTimeLogId == null) ? 0 : fieldworkTimeLogId.hashCode());
		result = prime * result + ((fromLocation == null) ? 0 : fromLocation.hashCode());
		result = prime * result + (includeInTransportForm ? 1231 : 1237);
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((quotationCount == null) ? 0 : quotationCount.hashCode());
		result = prime * result + ((recordType == null) ? 0 : recordType.hashCode());
		result = prime * result + ((referenceMonth == null) ? 0 : referenceMonth.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((survey == null) ? 0 : survey.hashCode());
		result = prime * result + ((timeLogId == null) ? 0 : timeLogId.hashCode());
		result = prime * result + ((toLocation == null) ? 0 : toLocation.hashCode());
		result = prime * result + ((totalQuotation == null) ? 0 : totalQuotation.hashCode());
		result = prime * result + (transit ? 1231 : 1237);
		result = prime * result + ((transport == null) ? 0 : transport.hashCode());
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
		FieldworkTimeLogSyncData other = (FieldworkTimeLogSyncData) obj;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		if (assignmentId == null) {
			if (other.assignmentId != null)
				return false;
		} else if (!assignmentId.equals(other.assignmentId))
			return false;
		if (building == null) {
			if (other.building != null)
				return false;
		} else if (!building.equals(other.building))
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
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (enumerationOutcome == null) {
			if (other.enumerationOutcome != null)
				return false;
		} else if (!enumerationOutcome.equals(other.enumerationOutcome))
			return false;
		if (expenses == null) {
			if (other.expenses != null)
				return false;
		} else if (!expenses.equals(other.expenses))
			return false;
		if (fieldworkTimeLogId == null) {
			if (other.fieldworkTimeLogId != null)
				return false;
		} else if (!fieldworkTimeLogId.equals(other.fieldworkTimeLogId))
			return false;
		if (fromLocation == null) {
			if (other.fromLocation != null)
				return false;
		} else if (!fromLocation.equals(other.fromLocation))
			return false;
		if (includeInTransportForm != other.includeInTransportForm)
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
		if (recordType == null) {
			if (other.recordType != null)
				return false;
		} else if (!recordType.equals(other.recordType))
			return false;
		if (referenceMonth == null) {
			if (other.referenceMonth != null)
				return false;
		} else if (!referenceMonth.equals(other.referenceMonth))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (survey == null) {
			if (other.survey != null)
				return false;
		} else if (!survey.equals(other.survey))
			return false;
		if (timeLogId == null) {
			if (other.timeLogId != null)
				return false;
		} else if (!timeLogId.equals(other.timeLogId))
			return false;
		if (toLocation == null) {
			if (other.toLocation != null)
				return false;
		} else if (!toLocation.equals(other.toLocation))
			return false;
		if (totalQuotation == null) {
			if (other.totalQuotation != null)
				return false;
		} else if (!totalQuotation.equals(other.totalQuotation))
			return false;
		if (transit != other.transit)
			return false;
		if (transport == null) {
			if (other.transport != null)
				return false;
		} else if (!transport.equals(other.transport))
			return false;
		return true;
	}
	
}
