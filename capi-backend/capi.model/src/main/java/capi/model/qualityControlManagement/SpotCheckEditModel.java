package capi.model.qualityControlManagement;

import java.util.Date;
import java.util.List;


public class SpotCheckEditModel {

	private Integer spotCheckFormId;

	private String officerCode;

	private String spotCheckDate;

	private Integer session;

	private List<String> surveyList;

	private String survey;

	private List<SpotCheckPhoneCallTableList> spotCheckPhoneCallList;

	private Integer timeCallback;

	private Integer activityBeingPerformed;

	private String interviewReferenceNo;

	private List<String> referenceNoList;

	private String location;

	private List<SpotCheckResultTableList> spotCheckResultList;

	private String caseReferenceNo;

	private String remarksForNonContact;

	private String scheduledPlace;

	private String scheduledTime;

	private String turnUpTime;

	private Boolean reasonable;

	private Boolean irregular;

	private String remarkForTurnUpTime;

	private String verCheck1RefenceNo;

	private Boolean verCheck1IsIrregular;

	private String verCheck1Remark;

	private String verCheck2RefenceNo;

	private Boolean verCheck2IsIrregular;

	private String verCheck2Remark;

	private String submitTo;
	
	private Integer submitToId;

	private String rejectReason;

	private Boolean successful;

	private String unSuccessfulRemark;

	private Date createdDate;

	private Date modifiedDate;
	
	private String status;

	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}

	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
	}

	public String getOfficerCode() {
		return officerCode;
	}

	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}

	public String getSpotCheckDate() {
		return spotCheckDate;
	}

	public void setSpotCheckDate(String spotCheckDate) {
		this.spotCheckDate = spotCheckDate;
	}

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public List<String> getSurveyList() {
		return surveyList;
	}

	public void setSurveyList(List<String> surveyList) {
		this.surveyList = surveyList;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public List<SpotCheckPhoneCallTableList> getSpotCheckPhoneCallList() {
		return spotCheckPhoneCallList;
	}

	public void setSpotCheckPhoneCallList(
			List<SpotCheckPhoneCallTableList> spotCheckPhoneCallList) {
		this.spotCheckPhoneCallList = spotCheckPhoneCallList;
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

	public List<String> getReferenceNoList() {
		return referenceNoList;
	}

	public void setReferenceNoList(List<String> referenceNoList) {
		this.referenceNoList = referenceNoList;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<SpotCheckResultTableList> getSpotCheckResultList() {
		return spotCheckResultList;
	}

	public void setSpotCheckResultList(
			List<SpotCheckResultTableList> spotCheckResultList) {
		this.spotCheckResultList = spotCheckResultList;
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

	public String getRemarkForTurnUpTime() {
		return remarkForTurnUpTime;
	}

	public void setRemarkForTurnUpTime(String remarkForTurnUpTime) {
		this.remarkForTurnUpTime = remarkForTurnUpTime;
	}

	public String getVerCheck1RefenceNo() {
		return verCheck1RefenceNo;
	}

	public void setVerCheck1RefenceNo(String verCheck1RefenceNo) {
		this.verCheck1RefenceNo = verCheck1RefenceNo;
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

	public String getVerCheck2RefenceNo() {
		return verCheck2RefenceNo;
	}

	public void setVerCheck2RefenceNo(String verCheck2RefenceNo) {
		this.verCheck2RefenceNo = verCheck2RefenceNo;
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

	public String getSubmitTo() {
		return submitTo;
	}

	public void setSubmitTo(String submitTo) {
		this.submitTo = submitTo;
	}

	public Integer getSubmitToId() {
		return submitToId;
	}

	public void setSubmitToId(Integer submitToId) {
		this.submitToId = submitToId;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public Boolean getSuccessful() {
		return successful;
	}

	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

	public String getUnSuccessfulRemark() {
		return unSuccessfulRemark;
	}

	public void setUnSuccessfulRemark(String unSuccessfulRemark) {
		this.unSuccessfulRemark = unSuccessfulRemark;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
