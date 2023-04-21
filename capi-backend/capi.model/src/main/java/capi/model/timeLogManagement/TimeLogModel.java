package capi.model.timeLogManagement;

import java.util.List;


public class TimeLogModel{
	
	private String createdDateDisplay;
	private String modifiedDateDisplay;
	private Integer timeLogId;	
	private String date;
	private Boolean isOtherWorkingSession;
	private Boolean isClaimOT;
	private String otherWorkingSessionFrom;
	private String otherWorkingSessionTo;
	private String otClaimedDuration;
	private String otClaimed;
	private String otClaimedStart;
	private String otClaimedEnd;
	private String otClaimedHr;
	private String otClaimedMin;
	private String timeoffTaken;
	private String timeoffTakenDuration;
	private String timeoffTakenStart;
	private String timeoffTakenEnd;
	private String timeoffTakenHr;
	private String timeoffTakenMin;
	private Integer userId;
	private String userCode;

	private String rejectReason;
	private String approvedBy;
	
	private String itineraryCheckRemark;
	private Boolean isTrainingAM;
	private Boolean isTrainingPM;
	
	private Boolean isVLSLAM;
	private Boolean isVLSLPM;

	private String status;
	
	private Boolean isVoilateItineraryCheck;
	
	private Boolean isPreApproval;
	
	private Integer workingSessionId;
	private String workingSessionText;
	
	private Double assignmentDeviation;
	private Double sequenceDeviation;
	private Integer tpuDeviation;
	
	private List<FieldworkTimeLogModel> fieldworkTimeLogs;
	
	private List<TelephoneTimeLogModel> telephoneTimeLogs;

	public String getCreatedDateDisplay() {
		return createdDateDisplay;
	}

	public void setCreatedDateDisplay(String createdDateDisplay) {
		this.createdDateDisplay = createdDateDisplay;
	}

	public String getModifiedDateDisplay() {
		return modifiedDateDisplay;
	}

	public void setModifiedDateDisplay(String modifiedDateDisplay) {
		this.modifiedDateDisplay = modifiedDateDisplay;
	}

	public Integer getTimeLogId() {
		return timeLogId;
	}

	public void setTimeLogId(Integer timeLogId) {
		this.timeLogId = timeLogId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}


	public String getOtherWorkingSessionFrom() {
		return otherWorkingSessionFrom;
	}

	public void setOtherWorkingSessionFrom(String otherWorkingSessionFrom) {
		this.otherWorkingSessionFrom = otherWorkingSessionFrom;
	}

	public String getOtherWorkingSessionTo() {
		return otherWorkingSessionTo;
	}

	public void setOtherWorkingSessionTo(String otherWorkingSessionTo) {
		this.otherWorkingSessionTo = otherWorkingSessionTo;
	}

	public String getOtClaimedDuration() {
		return otClaimedDuration;
	}

	public void setOtClaimedDuration(String otClaimedDuration) {
		this.otClaimedDuration = otClaimedDuration;
	}
	
	public String getOtClaimed() {
		return otClaimed;
	}

	public void setOtClaimed(String otClaimed) {
		this.otClaimed = otClaimed;
	}

	public String getOtClaimedStart() {
		return otClaimedStart;
	}

	public void setOtClaimedStart(String otClaimedStart) {
		this.otClaimedStart = otClaimedStart;
	}

	public String getOtClaimedEnd() {
		return otClaimedEnd;
	}

	public void setOtClaimedEnd(String otClaimedEnd) {
		this.otClaimedEnd = otClaimedEnd;
	}

	public String getOtClaimedHr() {
		return otClaimedHr;
	}

	public void setOtClaimedHr(String otClaimedHr) {
		this.otClaimedHr = otClaimedHr;
	}

	public String getOtClaimedMin() {
		return otClaimedMin;
	}

	public void setOtClaimedMin(String otClaimedMin) {
		this.otClaimedMin = otClaimedMin;
	}

	public String getTimeoffTakenDuration() {
		return timeoffTakenDuration;
	}

	public String getTimeoffTaken() {
		return timeoffTaken;
	}

	public void setTimeoffTaken(String timeoffTaken) {
		this.timeoffTaken = timeoffTaken;
	}

	public void setTimeoffTakenDuration(String timeoffTakenDuration) {
		this.timeoffTakenDuration = timeoffTakenDuration;
	}

	public String getTimeoffTakenStart() {
		return timeoffTakenStart;
	}

	public void setTimeoffTakenStart(String timeoffTakenStart) {
		this.timeoffTakenStart = timeoffTakenStart;
	}

	public String getTimeoffTakenEnd() {
		return timeoffTakenEnd;
	}

	public void setTimeoffTakenEnd(String timeoffTakenEnd) {
		this.timeoffTakenEnd = timeoffTakenEnd;
	}

	public String getTimeoffTakenHr() {
		return timeoffTakenHr;
	}

	public void setTimeoffTakenHr(String timeoffTakenHr) {
		this.timeoffTakenHr = timeoffTakenHr;
	}

	public String getTimeoffTakenMin() {
		return timeoffTakenMin;
	}

	public void setTimeoffTakenMin(String timeoffTakenMin) {
		this.timeoffTakenMin = timeoffTakenMin;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getItineraryCheckRemark() {
		return itineraryCheckRemark;
	}

	public void setItineraryCheckRemark(String itineraryCheckRemark) {
		this.itineraryCheckRemark = itineraryCheckRemark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getWorkingSessionId() {
		return workingSessionId;
	}

	public void setWorkingSessionId(Integer workingSessionId) {
		this.workingSessionId = workingSessionId;
	}

	public List<FieldworkTimeLogModel> getFieldworkTimeLogs() {
		return fieldworkTimeLogs;
	}

	public void setFieldworkTimeLogs(List<FieldworkTimeLogModel> fieldworkTimeLogs) {
		this.fieldworkTimeLogs = fieldworkTimeLogs;
	}

	public List<TelephoneTimeLogModel> getTelephoneTimeLogs() {
		return telephoneTimeLogs;
	}

	public void setTelephoneTimeLogs(List<TelephoneTimeLogModel> telephoneTimeLogs) {
		this.telephoneTimeLogs = telephoneTimeLogs;
	}

	public Boolean getIsOtherWorkingSession() {
		return isOtherWorkingSession;
	}

	public void setIsOtherWorkingSession(Boolean isOtherWorkingSession) {
		this.isOtherWorkingSession = isOtherWorkingSession;
	}

	public Boolean getIsClaimOT() {
		return isClaimOT;
	}

	public void setIsClaimOT(Boolean isClaimOT) {
		this.isClaimOT = isClaimOT;
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

	public String getWorkingSessionText() {
		return workingSessionText;
	}

	public void setWorkingSessionText(String workingSessionText) {
		this.workingSessionText = workingSessionText;
	}

	public Boolean getIsVoilateItineraryCheck() {
		return isVoilateItineraryCheck;
	}

	public void setIsVoilateItineraryCheck(Boolean isVoilateItineraryCheck) {
		this.isVoilateItineraryCheck = isVoilateItineraryCheck;
	}

	public Boolean getIsPreApproval() {
		return isPreApproval;
	}

	public void setIsPreApproval(Boolean preApproval) {
		this.isPreApproval = preApproval;
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

	public Integer getTpuDeviation() {
		return tpuDeviation;
	}

	public void setTpuDeviation(Integer tpuDeviation) {
		this.tpuDeviation = tpuDeviation;
	}
	
}
