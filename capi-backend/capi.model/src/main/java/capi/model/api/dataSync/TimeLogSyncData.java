package capi.model.api.dataSync;

import java.util.Date;

public class TimeLogSyncData {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((approvedBy == null) ? 0 : approvedBy.hashCode());
		result = prime * result + ((assignmentDeviation == null) ? 0 : assignmentDeviation.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (isClaimOT ? 1231 : 1237);
		result = prime * result + (isOtherWorkingSession ? 1231 : 1237);
		result = prime * result + (isTrainingAM ? 1231 : 1237);
		result = prime * result + (isTrainingPM ? 1231 : 1237);
		result = prime * result + (isVLSLAM ? 1231 : 1237);
		result = prime * result + (isVLSLPM ? 1231 : 1237);
		result = prime * result + (isVoilateItineraryCheck ? 1231 : 1237);
		result = prime * result + ((itineraryCheckRemark == null) ? 0 : itineraryCheckRemark.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((otClaimed == null) ? 0 : otClaimed.hashCode());
		result = prime * result + ((otherWorkingSessionFrom == null) ? 0 : otherWorkingSessionFrom.hashCode());
		result = prime * result + ((otherWorkingSessionTo == null) ? 0 : otherWorkingSessionTo.hashCode());
		result = prime * result + (preApproval ? 1231 : 1237);
		result = prime * result + ((rejectReason == null) ? 0 : rejectReason.hashCode());
		result = prime * result + ((sequenceDeviation == null) ? 0 : sequenceDeviation.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((timeLogId == null) ? 0 : timeLogId.hashCode());
		result = prime * result + ((timeoffTaken == null) ? 0 : timeoffTaken.hashCode());
		result = prime * result + ((tpuDeviation == null) ? 0 : tpuDeviation.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((workingSessionSettingId == null) ? 0 : workingSessionSettingId.hashCode());
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
		TimeLogSyncData other = (TimeLogSyncData) obj;
		if (approvedBy == null) {
			if (other.approvedBy != null)
				return false;
		} else if (!approvedBy.equals(other.approvedBy))
			return false;
		if (assignmentDeviation == null) {
			if (other.assignmentDeviation != null)
				return false;
		} else if (!assignmentDeviation.equals(other.assignmentDeviation))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (isClaimOT != other.isClaimOT)
			return false;
		if (isOtherWorkingSession != other.isOtherWorkingSession)
			return false;
		if (isTrainingAM != other.isTrainingAM)
			return false;
		if (isTrainingPM != other.isTrainingPM)
			return false;
		if (isVLSLAM != other.isVLSLAM)
			return false;
		if (isVLSLPM != other.isVLSLPM)
			return false;
		if (isVoilateItineraryCheck != other.isVoilateItineraryCheck)
			return false;
		if (itineraryCheckRemark == null) {
			if (other.itineraryCheckRemark != null)
				return false;
		} else if (!itineraryCheckRemark.equals(other.itineraryCheckRemark))
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
		if (otClaimed == null) {
			if (other.otClaimed != null)
				return false;
		} else if (!otClaimed.equals(other.otClaimed))
			return false;
		if (otherWorkingSessionFrom == null) {
			if (other.otherWorkingSessionFrom != null)
				return false;
		} else if (!otherWorkingSessionFrom.equals(other.otherWorkingSessionFrom))
			return false;
		if (otherWorkingSessionTo == null) {
			if (other.otherWorkingSessionTo != null)
				return false;
		} else if (!otherWorkingSessionTo.equals(other.otherWorkingSessionTo))
			return false;
		if (preApproval != other.preApproval)
			return false;
		if (rejectReason == null) {
			if (other.rejectReason != null)
				return false;
		} else if (!rejectReason.equals(other.rejectReason))
			return false;
		if (sequenceDeviation == null) {
			if (other.sequenceDeviation != null)
				return false;
		} else if (!sequenceDeviation.equals(other.sequenceDeviation))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (timeLogId == null) {
			if (other.timeLogId != null)
				return false;
		} else if (!timeLogId.equals(other.timeLogId))
			return false;
		if (timeoffTaken == null) {
			if (other.timeoffTaken != null)
				return false;
		} else if (!timeoffTaken.equals(other.timeoffTaken))
			return false;
		if (tpuDeviation == null) {
			if (other.tpuDeviation != null)
				return false;
		} else if (!tpuDeviation.equals(other.tpuDeviation))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (workingSessionSettingId == null) {
			if (other.workingSessionSettingId != null)
				return false;
		} else if (!workingSessionSettingId.equals(other.workingSessionSettingId))
			return false;
		return true;
	}
	
}
