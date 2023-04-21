package capi.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="SpotCheckForm")
public class SpotCheckForm extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SpotCheckFormId")
	private Integer spotCheckFormId;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OfficerId", nullable = true)
	private User officer; 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SupervisorId", nullable = true)
	private User supervisor; 
	
	@Column(name="SpotCheckDate")
	private Date spotCheckDate;
	
	@Column(name="Survey")
	private String survey;
	
	/**
	 * 1- reasonable 
	 * 2- not reasonable 
	 * 3- N.A.
	 */
	@Column(name="TimeCallback")
	private Integer timeCallback;
	
	/**
	 * 1- on the way to outlet 
	 * 2- living quarters 
	 * 3- conducting interview 
	 * 4- others
	 */
	@Column(name="ActivityBeingPerformed")
	private Integer activityBeingPerformed;
		
	@Column(name="InterviewReferenceNo")
	private String interviewReferenceNo;
	
	@Column(name="Location")
	private String location;
	
	@Column(name="CaseReferenceNo")
	private String caseReferenceNo;
	
	@Column(name="RemarksForNonContact")
	private String remarksForNonContact;
	
	@Column(name="ScheduledPlace")
	private String scheduledPlace;
	
	@Temporal(TemporalType.TIME)
	@Column(name="ScheduledTime")
	private Date scheduledTime;
	
	@Temporal(TemporalType.TIME)
	@Column(name="TurnUpTime")
	private Date turnUpTime;
	
	@Column(name="IsReasonable")
	private Boolean isReasonable;
	
	@Column(name="IsIrregular")
	private Boolean isIrregular;
	
	@Column(name="RemarkForTurnUpTime")
	private String remarkForTurnUpTime;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubmitTo", nullable = true)
	private User submitTo;
	
	@Column(name="RejectReason")
	private String rejectReason;	
	
	@Column(name="IsSuccessful")
	private Boolean isSuccessful;
	
	@Column(name="UnsuccessfulRemark")
	private String unsuccessfulRemark;
	
	@Column(name="VerCheck1ReferenceNo")
	private String verCheck1ReferenceNo;
	
	@Column(name="VerCheck1IsIrregular")
	private Boolean verCheck1IsIrregular;
	
	@Column(name="VerCheck1Remark")
	private String verCheck1Remark;
	
	@Column(name="VerCheck2ReferenceNo")
	private String verCheck2ReferenceNo;
	
	@Column(name="VerCheck2IsIrregular")
	private Boolean verCheck2IsIrregular;
	
	@Column(name="VerCheck2Remark")
	private String verCheck2Remark;
	
	@Column(name="Session")
	private Integer session;
	
	/**
	 * Draft, Submitted, Rejected, Approved
	 */
	@Column(name="Status")
	private String status;
	
	@Column(name="SubmittedDate")
	private Date submittedDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ScSvPlanId", nullable = true)
	private ScSvPlan scSvPlan;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "spotCheckForm")
	private Set<SpotCheckPhoneCall> spotCheckPhoneCalls = new HashSet<SpotCheckPhoneCall>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "spotCheckForm")
	private Set<SpotCheckResult> spotCheckResults = new HashSet<SpotCheckResult>();
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSpotCheckFormId();
	}

	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}

	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
	}

	public User getOfficer() {
		return officer;
	}

	public void setOfficer(User officer) {
		this.officer = officer;
	}

	public User getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
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

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public Date getTurnUpTime() {
		return turnUpTime;
	}

	public void setTurnUpTime(Date turnUpTime) {
		this.turnUpTime = turnUpTime;
	}

	public Boolean isReasonable() {
		return isReasonable;
	}

	public void setReasonable(Boolean isReasonable) {
		this.isReasonable = isReasonable;
	}

	public Boolean isIrregular() {
		return isIrregular;
	}

	public void setIrregular(Boolean isIrregular) {
		this.isIrregular = isIrregular;
	}

	public String getRemarkForTurnUpTime() {
		return remarkForTurnUpTime;
	}

	public void setRemarkForTurnUpTime(String remarkForTurnUpTime) {
		this.remarkForTurnUpTime = remarkForTurnUpTime;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public Boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(Boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public String getUnsuccessfulRemark() {
		return unsuccessfulRemark;
	}

	public void setUnsuccessfulRemark(String unsuccessfulRemark) {
		this.unsuccessfulRemark = unsuccessfulRemark;
	}

	public String getVerCheck1ReferenceNo() {
		return verCheck1ReferenceNo;
	}

	public void setVerCheck1ReferenceNo(String verCheck1ReferenceNo) {
		this.verCheck1ReferenceNo = verCheck1ReferenceNo;
	}

	public Boolean isVerCheck1IsIrregular() {
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

	public Boolean isVerCheck2IsIrregular() {
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

	public Set<SpotCheckPhoneCall> getSpotCheckPhoneCalls() {
		return spotCheckPhoneCalls;
	}

	public void setSpotCheckPhoneCalls(Set<SpotCheckPhoneCall> spotCheckPhoneCalls) {
		this.spotCheckPhoneCalls = spotCheckPhoneCalls;
	}

	public Set<SpotCheckResult> getSpotCheckResults() {
		return spotCheckResults;
	}

	public void setSpotCheckResults(Set<SpotCheckResult> spotCheckResults) {
		this.spotCheckResults = spotCheckResults;
	}

	public ScSvPlan getScSvPlan() {
		return scSvPlan;
	}

	public void setScSvPlan(ScSvPlan scSvPlan) {
		this.scSvPlan = scSvPlan;
	}

	public User getSubmitTo() {
		return submitTo;
	}

	public void setSubmitTo(User submitTo) {
		this.submitTo = submitTo;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

}
