package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.FieldworkTimeLogSyncData;
import capi.model.api.dataSync.TelephoneTimeLogSyncData;

public class TimeLogOnlineModel {

private Integer timeLogId;
	
	private Date date;
	
	private Integer workingSessionSettingId;
	
	private boolean isOtherWorkingSession;
	
	private boolean isClaimOT;
	
	private String otherWorkingSessionFrom;
	
	private String otherWorkingSessionTo;
	
	private String otClaimed;
	
	private String timeoffTaken;
	
	private Integer userId;
	
	private String rejectReason;
	
	private String itineraryCheckRemark;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private boolean isTrainingAM;
	
	private boolean isTrainingPM;
	
	private boolean isVLSLAM;
	
	private boolean isVLSLPM;
	
	private String status;
	
	private boolean isVoilateItineraryCheck;
	
	private boolean preApproval;
	
	private Integer approvedBy;
	
	private Double assignmentDeviation;
	
	private Double sequenceDeviation;
	
	private Integer tpuDeviation;

	private Integer localId;
	
	private String localDbRecordStatus;
	
	private List<FieldworkTimeLogSyncData> fieldworkTimeLogs;
	
	private List<TelephoneTimeLogSyncData> telephoneTimeLogs;

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

	public Integer getWorkingSessionSettingId() {
		return workingSessionSettingId;
	}

	public void setWorkingSessionSettingId(Integer workingSessionSettingId) {
		this.workingSessionSettingId = workingSessionSettingId;
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

	public String getOtClaimed() {
		return otClaimed;
	}

	public void setOtClaimed(String otClaimed) {
		this.otClaimed = otClaimed;
	}

	public String getTimeoffTaken() {
		return timeoffTaken;
	}

	public void setTimeoffTaken(String timeoffTaken) {
		this.timeoffTaken = timeoffTaken;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getTpuDeviation() {
		return tpuDeviation;
	}

	public void setTpuDeviation(Integer tpuDeviation) {
		this.tpuDeviation = tpuDeviation;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public List<FieldworkTimeLogSyncData> getFieldworkTimeLogs() {
		return fieldworkTimeLogs;
	}

	public void setFieldworkTimeLogs(List<FieldworkTimeLogSyncData> fieldworkTimeLogs) {
		this.fieldworkTimeLogs = fieldworkTimeLogs;
	}

	public List<TelephoneTimeLogSyncData> getTelephoneTimeLogs() {
		return telephoneTimeLogs;
	}

	public void setTelephoneTimeLogs(List<TelephoneTimeLogSyncData> telephoneTimeLogs) {
		this.telephoneTimeLogs = telephoneTimeLogs;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}

	public boolean isOtherWorkingSession() {
		return isOtherWorkingSession;
	}

	public void setOtherWorkingSession(boolean isOtherWorkingSession) {
		this.isOtherWorkingSession = isOtherWorkingSession;
	}

	public boolean isClaimOT() {
		return isClaimOT;
	}

	public void setClaimOT(boolean isClaimOT) {
		this.isClaimOT = isClaimOT;
	}

	public boolean isTrainingAM() {
		return isTrainingAM;
	}

	public void setTrainingAM(boolean isTrainingAM) {
		this.isTrainingAM = isTrainingAM;
	}

	public boolean isTrainingPM() {
		return isTrainingPM;
	}

	public void setTrainingPM(boolean isTrainingPM) {
		this.isTrainingPM = isTrainingPM;
	}

	public boolean isVLSLAM() {
		return isVLSLAM;
	}

	public void setVLSLAM(boolean isVLSLAM) {
		this.isVLSLAM = isVLSLAM;
	}

	public boolean isVLSLPM() {
		return isVLSLPM;
	}

	public void setVLSLPM(boolean isVLSLPM) {
		this.isVLSLPM = isVLSLPM;
	}

	public boolean isVoilateItineraryCheck() {
		return isVoilateItineraryCheck;
	}

	public void setVoilateItineraryCheck(boolean isVoilateItineraryCheck) {
		this.isVoilateItineraryCheck = isVoilateItineraryCheck;
	}

	public boolean isPreApproval() {
		return preApproval;
	}

	public void setPreApproval(boolean preApproval) {
		this.preApproval = preApproval;
	}
}
