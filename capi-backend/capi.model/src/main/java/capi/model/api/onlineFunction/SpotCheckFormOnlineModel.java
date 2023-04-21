package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.SpotCheckPhoneCallSyncData;
import capi.model.api.dataSync.SpotCheckResultSyncData;

public class SpotCheckFormOnlineModel {

private Integer spotCheckFormId;
	
	private Integer officerId;
	
	private Integer supervisorId;
	
	private Date spotCheckDate;
	
	private Integer timeCallback;
	
	private Integer activityBeingPerformed;
	
	private String interviewReferenceNo;
	
	private String location;
	
	private String caseReferenceNo;
	
	private String remarksForNonContact;
	
	private String scheduledPlace;
	
	private String scheduledTime;
	
	private String turnUpTime;
	
	private Boolean reasonable;
	
	private Boolean irregular;
	
	private String remarkForTurnUpTime;
	
	private Integer submitTo;
	
	private String rejectReason;
	
	private Boolean successful;
	
	private String unsuccessfulRemark;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String verCheck1ReferenceNo;
	
	private Boolean verCheck1IsIrregular;
	
	private String verCheck1Remark;
	
	private String verCheck2ReferenceNo;
	
	private Boolean verCheck2IsIrregular;
	
	private String verCheck2Remark;
	
	private Integer session;
	
	private String status;
	
	private Integer scSvPlanId;
	
	private String survey;
	
	private Integer localId;
	
	private String localDbRecordStatus;
	
	private List<SpotCheckResultSyncData> spotCheckResults;
	
	private List<SpotCheckPhoneCallSyncData> spotCheckPhoneCall;

	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}

	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
	}

	public Integer getOfficerId() {
		return officerId;
	}

	public void setOfficerId(Integer officerId) {
		this.officerId = officerId;
	}

	public Integer getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
	}

	public Date getSpotCheckDate() {
		return spotCheckDate;
	}

	public void setSpotCheckDate(Date spotCheckDate) {
		this.spotCheckDate = spotCheckDate;
	}

	public Integer getTimeCallback() {
		return timeCallback;
	}

	public void setTimeCallback(Integer timeCallback) {
		this.timeCallback = timeCallback;
	}

	public Integer getActivityBeingPerformed() {
		return activityBeingPerformed;
	}

	public void setActivityBeingPerformed(Integer activityBeingPerformed) {
		this.activityBeingPerformed = activityBeingPerformed;
	}

	public String getInterviewReferenceNo() {
		return interviewReferenceNo;
	}

	public void setInterviewReferenceNo(String interviewReferenceNo) {
		this.interviewReferenceNo = interviewReferenceNo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCaseReferenceNo() {
		return caseReferenceNo;
	}

	public void setCaseReferenceNo(String caseReferenceNo) {
		this.caseReferenceNo = caseReferenceNo;
	}

	public String getRemarksForNonContact() {
		return remarksForNonContact;
	}

	public void setRemarksForNonContact(String remarksForNonContact) {
		this.remarksForNonContact = remarksForNonContact;
	}

	public String getScheduledPlace() {
		return scheduledPlace;
	}

	public void setScheduledPlace(String scheduledPlace) {
		this.scheduledPlace = scheduledPlace;
	}

	public String getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(String scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public String getTurnUpTime() {
		return turnUpTime;
	}

	public void setTurnUpTime(String turnUpTime) {
		this.turnUpTime = turnUpTime;
	}

	public String getRemarkForTurnUpTime() {
		return remarkForTurnUpTime;
	}

	public void setRemarkForTurnUpTime(String remarkForTurnUpTime) {
		this.remarkForTurnUpTime = remarkForTurnUpTime;
	}

	public Integer getSubmitTo() {
		return submitTo;
	}

	public void setSubmitTo(Integer submitTo) {
		this.submitTo = submitTo;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public Boolean getReasonable() {
		return reasonable;
	}

	public void setReasonable(Boolean reasonable) {
		this.reasonable = reasonable;
	}

	public Boolean getIrregular() {
		return irregular;
	}

	public void setIrregular(Boolean irregular) {
		this.irregular = irregular;
	}

	public Boolean getSuccessful() {
		return successful;
	}

	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

	public String getUnsuccessfulRemark() {
		return unsuccessfulRemark;
	}

	public void setUnsuccessfulRemark(String unsuccessfulRemark) {
		this.unsuccessfulRemark = unsuccessfulRemark;
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

	public String getVerCheck1ReferenceNo() {
		return verCheck1ReferenceNo;
	}

	public void setVerCheck1ReferenceNo(String verCheck1ReferenceNo) {
		this.verCheck1ReferenceNo = verCheck1ReferenceNo;
	}

	public Boolean getVerCheck1IsIrregular() {
		return verCheck1IsIrregular;
	}

	public void setVerCheck1IsIrregular(Boolean verCheck1IsIrregular) {
		this.verCheck1IsIrregular = verCheck1IsIrregular;
	}

	public String getVerCheck1Remark() {
		return verCheck1Remark;
	}

	public void setVerCheck1Remark(String verCheck1Remark) {
		this.verCheck1Remark = verCheck1Remark;
	}

	public String getVerCheck2ReferenceNo() {
		return verCheck2ReferenceNo;
	}

	public void setVerCheck2ReferenceNo(String verCheck2ReferenceNo) {
		this.verCheck2ReferenceNo = verCheck2ReferenceNo;
	}

	public Boolean getVerCheck2IsIrregular() {
		return verCheck2IsIrregular;
	}

	public void setVerCheck2IsIrregular(Boolean verCheck2IsIrregular) {
		this.verCheck2IsIrregular = verCheck2IsIrregular;
	}

	public String getVerCheck2Remark() {
		return verCheck2Remark;
	}

	public void setVerCheck2Remark(String verCheck2Remark) {
		this.verCheck2Remark = verCheck2Remark;
	}

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getScSvPlanId() {
		return scSvPlanId;
	}

	public void setScSvPlanId(Integer scSvPlanId) {
		this.scSvPlanId = scSvPlanId;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
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

	public List<SpotCheckResultSyncData> getSpotCheckResults() {
		return spotCheckResults;
	}

	public void setSpotCheckResults(List<SpotCheckResultSyncData> spotCheckResults) {
		this.spotCheckResults = spotCheckResults;
	}

	public List<SpotCheckPhoneCallSyncData> getSpotCheckPhoneCall() {
		return spotCheckPhoneCall;
	}

	public void setSpotCheckPhoneCall(List<SpotCheckPhoneCallSyncData> spotCheckPhoneCall) {
		this.spotCheckPhoneCall = spotCheckPhoneCall;
	}
	
}
