package capi.model.report;

import java.util.Date;
import java.util.List;

public class InformationSpotCheckForm {

	public static class InformationSCPhoneCall {

		private String scPhoneCallTime;

		private Integer scPhoneCallResult;
		
		private String checkedImagePath;

		private String uncheckedImagePath;

		public String getScPhoneCallTime() {
			return scPhoneCallTime;
		}

		public void setScPhoneCallTime(String scPhoneCallTime) {
			this.scPhoneCallTime = scPhoneCallTime;
		}

		public Integer getScPhoneCallResult() {
			return scPhoneCallResult;
		}

		public void setScPhoneCallResult(Integer scPhoneCallResult) {
			this.scPhoneCallResult = scPhoneCallResult;
		}

		public String getCheckedImagePath() {
			return checkedImagePath;
		}

		public void setCheckedImagePath(String checkedImagePath) {
			this.checkedImagePath = checkedImagePath;
		}

		public String getUncheckedImagePath() {
			return uncheckedImagePath;
		}

		public void setUncheckedImagePath(String uncheckedImagePath) {
			this.uncheckedImagePath = uncheckedImagePath;
		}
		
	}

	public static class InformationSCResult {

		private String scResultResult;

		private String scResultRemark;

		private String scResultReferenceNo;
		
		private String scResultSurvey;
		
		private String checkedImagePath;

		private String uncheckedImagePath;

		public String getScResultResult() {
			return scResultResult;
		}

		public void setScResultResult(String scResultResult) {
			this.scResultResult = scResultResult;
		}

		public String getScResultRemark() {
			return scResultRemark;
		}

		public void setScResultRemark(String scResultRemark) {
			this.scResultRemark = scResultRemark;
		}

		public String getScResultReferenceNo() {
			return scResultReferenceNo;
		}

		public void setScResultReferenceNo(String scResultReferenceNo) {
			this.scResultReferenceNo = scResultReferenceNo;
		}

		public String getScResultSurvey() {
			return scResultSurvey;
		}

		public void setScResultSurvey(String scResultSurvey) {
			this.scResultSurvey = scResultSurvey;
		}

		public String getCheckedImagePath() {
			return checkedImagePath;
		}

		public void setCheckedImagePath(String checkedImagePath) {
			this.checkedImagePath = checkedImagePath;
		}

		public String getUncheckedImagePath() {
			return uncheckedImagePath;
		}

		public void setUncheckedImagePath(String uncheckedImagePath) {
			this.uncheckedImagePath = uncheckedImagePath;
		}

	}

	private Integer spotCheckFormId;

	private String officerName;

	private String officerDestination;

	private String spotCheckDate;

	private Integer timeCallBack;

	private Integer activityBeingPerformed;

	private String interviewReferenceNo;

	private String location;
	
	private String survey;

	private String caseReferenceNo;

	private String remarksForNonContact;

	private String scheduledPlace;

	private Date scheduledTimeDate;

	private String scheduledTime;

	private Date turnUpTimeDate;

	private String turnUpTime;

	private Boolean isReasonable;

	private Boolean isIrregular;

	private String remarkForTurnUpTime;

	private String verCheck1ReferenceNo;

	private Boolean verCheck1IsIrregular;

	private String verCheck1Remark;

	private String verCheck2ReferenceNo;

	private Boolean verCheck2IsIrregular;

	private String verCheck2Remark;

	private String supervisorName;

	private String supervisorDestination;

	private String checkedImagePath;

	private String uncheckedImagePath;
	
	private String approvalUserName;
	
	private String approvalUserDestination;
	
	private String lastModifiedDate;
	
	private String submittedDate;
	
	private String officerCode;
	
	private String dateOfChecking;
	
	private String mapAddress;

	private List<InformationSCPhoneCall> scPhoneCalls;

	private List<InformationSCResult> scResults;
	
	public String getMapAddress() {
		return mapAddress;
	}
	
	public void setMapAddress(String mapAddress) {
		this.mapAddress = mapAddress;
	}

	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}

	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
	}

	public String getOfficerName() {
		return officerName;
	}

	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}

	public String getOfficerDestination() {
		return officerDestination;
	}

	public void setOfficerDestination(String officerDestination) {
		this.officerDestination = officerDestination;
	}

	public String getSpotCheckDate() {
		return spotCheckDate;
	}

	public void setSpotCheckDate(String spotCheckDate) {
		this.spotCheckDate = spotCheckDate;
	}

	public Integer getTimeCallBack() {
		return timeCallBack;
	}

	public void setTimeCallBack(Integer timeCallBack) {
		this.timeCallBack = timeCallBack;
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

	public Date getScheduledTimeDate() {
		return scheduledTimeDate;
	}

	public void setScheduledTimeDate(Date scheduledTimeDate) {
		this.scheduledTimeDate = scheduledTimeDate;
	}

	public String getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(String scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public Date getTurnUpTimeDate() {
		return turnUpTimeDate;
	}

	public void setTurnUpTimeDate(Date turnUpTimeDate) {
		this.turnUpTimeDate = turnUpTimeDate;
	}

	public String getTurnUpTime() {
		return turnUpTime;
	}

	public void setTurnUpTime(String turnUpTime) {
		this.turnUpTime = turnUpTime;
	}

	public Boolean getIsReasonable() {
		return isReasonable;
	}

	public void setIsReasonable(Boolean isReasonable) {
		this.isReasonable = isReasonable;
	}

	public Boolean getIsIrregular() {
		return isIrregular;
	}

	public void setIsIrregular(Boolean isIrregular) {
		this.isIrregular = isIrregular;
	}

	public String getRemarkForTurnUpTime() {
		return remarkForTurnUpTime;
	}

	public void setRemarkForTurnUpTime(String remarkForTurnUpTime) {
		this.remarkForTurnUpTime = remarkForTurnUpTime;
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

	public String getSupervisorName() {
		return supervisorName;
	}

	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	public String getSupervisorDestination() {
		return supervisorDestination;
	}

	public void setSupervisorDestination(String supervisorDestination) {
		this.supervisorDestination = supervisorDestination;
	}

	public String getCheckedImagePath() {
		return checkedImagePath;
	}

	public void setCheckedImagePath(String checkedImagePath) {
		this.checkedImagePath = checkedImagePath;
	}

	public String getUncheckedImagePath() {
		return uncheckedImagePath;
	}

	public void setUncheckedImagePath(String uncheckedImagePath) {
		this.uncheckedImagePath = uncheckedImagePath;
	}

	public List<InformationSCPhoneCall> getScPhoneCalls() {
		return scPhoneCalls;
	}

	public void setScPhoneCalls(List<InformationSCPhoneCall> scPhoneCalls) {
		this.scPhoneCalls = scPhoneCalls;
	}

	public List<InformationSCResult> getScResults() {
		return scResults;
	}

	public void setScResults(List<InformationSCResult> scResults) {
		this.scResults = scResults;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public String getApprovalUserName() {
		return approvalUserName;
	}

	public void setApprovalUserName(String approvalUserName) {
		this.approvalUserName = approvalUserName;
	}

	public String getApprovalUserDestination() {
		return approvalUserDestination;
	}

	public void setApprovalUserDestination(String approvalUserDestination) {
		this.approvalUserDestination = approvalUserDestination;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getOfficerCode() {
		return officerCode;
	}

	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}

	public String getDateOfChecking() {
		return dateOfChecking;
	}

	public void setDateOfChecking(String dateOfChecking) {
		this.dateOfChecking = dateOfChecking;
	}

}
