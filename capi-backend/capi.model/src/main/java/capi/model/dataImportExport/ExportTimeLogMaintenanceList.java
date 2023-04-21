package capi.model.dataImportExport;

import java.util.Date;

public class ExportTimeLogMaintenanceList {
	
	//TimeLog
	private Integer timeLogId;
	private Date date;
	//User
	private String staffCode;
	//WorkingSessionSetting
	private Date workingSessionFrom;
	private Date workingSessionTo;
	private Date otClaimed;
	private Date timeoffTaken;
	private Boolean isTrainingAM;
	private Boolean isTrainingPM;
	private Boolean isVLSLAM;
	private Boolean isVLSLPM;
	private String status;
	private Boolean isVoilateItineraryCheck;
	private Integer approvedBy;
	private Double assignmentDeviation;
	private Double sequenceDeviation;
	private Double tpuDeviation;
	private String rejectReason;
	private String itineraryCheckRemark;
	
	//Telephone TimeLog
	private Integer telephoneTimeLogId;
	private String telSession;
	private String telCaseReferenceNo;
	private Integer telTotalQuotation;
	private Integer telQuotationCount;
	
	//FieldworkTimeLog
	private Integer fieldworkTimeLogId;
	private String fieldActivity;
	private Date fieldStartTime;
	private Date fieldNextActivityTime;
	private String fieldDestination;
	private Integer fieldRecordType;
	private Integer fieldBuilding;
	private String fieldRemark;		
	private String fieldFromLocation;
	private String fieldToLocation;
	private Boolean fieldIncludeInTransportForm;
	private Boolean fieldTransit;
	private String fieldTransport;
	private Double fieldExpenses;
	
	//Common
	private Date referenceMonth;
	private String survey;
	private String purpose;
    private Integer assignmentId;
	private String caseReferenceNo; 
	private Integer totalQuotation;
	private Integer quotationCount;
	private String enumerationOutcome;
	private Date createdDate;
	private String createdBy;
	private Date modifiedDate;
	private String modifiedBy;

	//Other Working Session
	private Boolean isOtherWorkingSession;
	private Date otherWorkingSessionFrom;
	private Date otherWorkingSessionTo;
	
//	//Survey Count
//	private Integer mrpsFIQuotationCount;
//	private Integer mrpsFITotalQuotation;
//	
//	private Integer mrpsTIQuotationCount;
//	private Integer mrpsTITotalQuotation;
//	
//	private Integer ghsFIQuotationCount;
//	private Integer ghsFITotalQuotation;
//	
//	private Integer ghsTIQuotationCount;
//	private Integer ghsTITotalQuotation;
//	
//	private Integer bmwpsFIBuildings;
//	private Integer bmwpsTIQuotationCount;
	
	public Boolean getIsOtherWorkingSession() {
		return isOtherWorkingSession;
	}
	public void setIsOtherWorkingSession(Boolean isOtherWorkingSession) {
		this.isOtherWorkingSession = isOtherWorkingSession;
	}
	
	public Date getOtherWorkingSessionFrom() {
		return otherWorkingSessionFrom;
	}
	public void setOtherWorkingSessionFrom(Date otherWorkingSessionFrom) {
		this.otherWorkingSessionFrom = otherWorkingSessionFrom;
	}
	
	public Date getOtherWorkingSessionTo() {
		return otherWorkingSessionTo;
	}
	public void setOtherWorkingSessionTo(Date otherWorkingSessionTo) {
		this.otherWorkingSessionTo = otherWorkingSessionTo;
	}
	
	
	public Integer getTimeLogId() {
		return timeLogId;
	}
	public void setTimeLogId(Integer timeLogId) {
		this.timeLogId = timeLogId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getStaffCode() {
		return staffCode;
	}
	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}
	public Date getWorkingSessionFrom() {
		return workingSessionFrom;
	}
	public void setWorkingSessionFrom(Date workingSessionFrom) {
		this.workingSessionFrom = workingSessionFrom;
	}
	public Date getWorkingSessionTo() {
		return workingSessionTo;
	}
	public void setWorkingSessionTo(Date workingSessionTo) {
		this.workingSessionTo = workingSessionTo;
	}
	public Date getOtClaimed() {
		return otClaimed;
	}
	public void setOtClaimed(Date otClaimed) {
		this.otClaimed = otClaimed;
	}
	public Date getTimeoffTaken() {
		return timeoffTaken;
	}
	public void setTimeoffTaken(Date timeoffTaken) {
		this.timeoffTaken = timeoffTaken;
	}
	public Boolean getIsTrainingAM() {
		return isTrainingAM;
	}
	public void setIsTrainingAM(Boolean isTrainingAM) {
		this.isTrainingAM = isTrainingAM;
	}
	public Boolean getIsTrainingPM() {
		return isTrainingPM;
	}
	public void setIsTrainingPM(Boolean isTrainingPM) {
		this.isTrainingPM = isTrainingPM;
	}
	public Boolean getIsVLSLAM() {
		return isVLSLAM;
	}
	public void setIsVLSLAM(Boolean isVLSLAM) {
		this.isVLSLAM = isVLSLAM;
	}
	public Boolean getIsVLSLPM() {
		return isVLSLPM;
	}
	public void setIsVLSLPM(Boolean isVLSLPM) {
		this.isVLSLPM = isVLSLPM;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Boolean getIsVoilateItineraryCheck() {
		return isVoilateItineraryCheck;
	}
	public void setIsVoilateItineraryCheck(Boolean isVoilateItineraryCheck) {
		this.isVoilateItineraryCheck = isVoilateItineraryCheck;
	}
	public Integer getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(Integer approvedBy) {
		this.approvedBy = approvedBy;
	}
	public Double getAssignmentDeviation() {
		return assignmentDeviation;
	}
	public void setAssignmentDeviation(Double assignmentDeviation) {
		this.assignmentDeviation = assignmentDeviation;
	}
	public Double getSequenceDeviation() {
		return sequenceDeviation;
	}
	public void setSequenceDeviation(Double sequenceDeviation) {
		this.sequenceDeviation = sequenceDeviation;
	}
	public Double getTpuDeviation() {
		return tpuDeviation;
	}
	public void setTpuDeviation(Double tpuDeviation) {
		this.tpuDeviation = tpuDeviation;
	}
	public String getRejectReason() {
		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	public String getItineraryCheckRemark() {
		return itineraryCheckRemark;
	}
	public void setItineraryCheckRemark(String itineraryCheckRemark) {
		this.itineraryCheckRemark = itineraryCheckRemark;
	}
	public Integer getTelephoneTimeLogId() {
		return telephoneTimeLogId;
	}
	public void setTelephoneTimeLogId(Integer telephoneTimeLogId) {
		this.telephoneTimeLogId = telephoneTimeLogId;
	}
	public String getTelSession() {
		return telSession;
	}
	public void setTelSession(String telSession) {
		this.telSession = telSession;
	}
	public String getTelCaseReferenceNo() {
		return telCaseReferenceNo;
	}
	public void setTelCaseReferenceNo(String telCaseReferenceNo) {
		this.telCaseReferenceNo = telCaseReferenceNo;
	}
	public Integer getTelTotalQuotation() {
		return telTotalQuotation;
	}
	public void setTelTotalQuotation(Integer telTotalQuotation) {
		this.telTotalQuotation = telTotalQuotation;
	}
	public Integer getTelQuotationCount() {
		return telQuotationCount;
	}
	public void setTelQuotationCount(Integer telQuotationCount) {
		this.telQuotationCount = telQuotationCount;
	}
	public Integer getFieldworkTimeLogId() {
		return fieldworkTimeLogId;
	}
	public void setFieldworkTimeLogId(Integer fieldworkTimeLogId) {
		this.fieldworkTimeLogId = fieldworkTimeLogId;
	}
	public String getFieldActivity() {
		return fieldActivity;
	}
	public void setFieldActivity(String fieldActivity) {
		this.fieldActivity = fieldActivity;
	}
	public Date getFieldStartTime() {
		return fieldStartTime;
	}
	public void setFieldStartTime(Date fieldStartTime) {
		this.fieldStartTime = fieldStartTime;
	}
	public Date getFieldNextActivityTime() {
		return fieldNextActivityTime;
	}
	public void setFieldNextActivityTime(Date fieldNextActivityTime) {
		this.fieldNextActivityTime = fieldNextActivityTime;
	}
	public String getFieldDestination() {
		return fieldDestination;
	}
	public void setFieldDestination(String fieldDestination) {
		this.fieldDestination = fieldDestination;
	}
	public Integer getFieldRecordType() {
		return fieldRecordType;
	}
	public void setFieldRecordType(Integer fieldRecordType) {
		this.fieldRecordType = fieldRecordType;
	}
	public Integer getFieldBuilding() {
		return fieldBuilding;
	}
	public void setFieldBuilding(Integer fieldBuilding) {
		this.fieldBuilding = fieldBuilding;
	}
	public String getFieldRemark() {
		return fieldRemark;
	}
	public void setFieldRemark(String fieldRemark) {
		this.fieldRemark = fieldRemark;
	}
	public String getFieldFromLocation() {
		return fieldFromLocation;
	}
	public void setFieldFromLocation(String fieldFromLocation) {
		this.fieldFromLocation = fieldFromLocation;
	}
	public String getFieldToLocation() {
		return fieldToLocation;
	}
	public void setFieldToLocation(String fieldToLocation) {
		this.fieldToLocation = fieldToLocation;
	}
	public Boolean getFieldIncludeInTransportForm() {
		return fieldIncludeInTransportForm;
	}
	public void setFieldIncludeInTransportForm(Boolean fieldIncludeInTransportForm) {
		this.fieldIncludeInTransportForm = fieldIncludeInTransportForm;
	}
	public Boolean getFieldTransit() {
		return fieldTransit;
	}
	public void setFieldTransit(Boolean fieldTransit) {
		this.fieldTransit = fieldTransit;
	}
	public String getFieldTransport() {
		return fieldTransport;
	}
	public void setFieldTransport(String fieldTransport) {
		this.fieldTransport = fieldTransport;
	}
	public Double getFieldExpenses() {
		return fieldExpenses;
	}
	public void setFieldExpenses(Double fieldExpenses) {
		this.fieldExpenses = fieldExpenses;
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
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public Integer getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	public String getCaseReferenceNo() {
		return caseReferenceNo;
	}
	public void setCaseReferenceNo(String caseReferenceNo) {
		this.caseReferenceNo = caseReferenceNo;
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
	public String getEnumerationOutcome() {
		return enumerationOutcome;
	}
	public void setEnumerationOutcome(String enumerationOutcome) {
		this.enumerationOutcome = enumerationOutcome;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
