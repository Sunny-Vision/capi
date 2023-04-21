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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="TimeLog")
public class TimeLog extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="TimeLogId")
	private Integer timeLogId;	
	
	@Column(name="[Date]")
	private Date date;
	
	@Column(name="IsOtherWorkingSession")
	private boolean isOtherWorkingSession;
	
	@Column(name="IsClaimOT")
	private boolean isClaimOT;
	
	@Column(name="OtherWorkingSessionFrom")
	private Date otherWorkingSessionFrom;
	
	@Column(name="OtherWorkingSessionTo")
	private Date otherWorkingSessionTo;
	
	@Column(name="OTClaimed")
	private Date otClaimed;
	
	@Column(name="TimeoffTaken")
	private Date timeoffTaken;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@Column(name="RejectReason")
	private String rejectReason;
	
	@Column(name="ItineraryCheckRemark")
	private String itineraryCheckRemark;
	
	@Column(name="IsTrainingAM")
	private boolean isTrainingAM;

	@Column(name="IsTrainingPM")
	private boolean isTrainingPM;
	
	@Column(name="IsVLSLAM")
	private boolean isVLSLAM;
	
	@Column(name="IsVLSLPM")
	private boolean isVLSLPM;
	
	@Column(name="Status")
	private String status;
	
	@Column(name="IsVoilateItineraryCheck")
	private boolean isVoilateItineraryCheck;
	
	@Column(name="PreApproval")
	private boolean preApproval;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ApprovedBy", nullable = true)
	private User approvedBy;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WorkingSessionSettingId", nullable = true)
	private WorkingSessionSetting setting;	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "timeLog")
	private Set<FieldworkTimeLog> fieldworkTimeLogs = new HashSet<FieldworkTimeLog>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "timeLog")
	private Set<TelephoneTimeLog> telephoneTimeLogs = new HashSet<TelephoneTimeLog>();
	
	@Column(name="AssignmentDeviation")
	private Double assignmentDeviation;
	
	@Column(name="SequenceDeviation")
	private Double sequenceDeviation;
	
	@Column(name="TpuDeviation")
	private Integer tpuDeviation;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy="timeLog")
	private VwItineraryPlanTimeLogMapBinded itineraryPlanMap;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getTimeLogId();
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public WorkingSessionSetting getSetting() {
		return setting;
	}

	public void setSetting(WorkingSessionSetting setting) {
		this.setting = setting;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isVoilateItineraryCheck() {
		return isVoilateItineraryCheck;
	}

	public void setVoilateItineraryCheck(boolean isVoilateItineraryCheck) {
		this.isVoilateItineraryCheck = isVoilateItineraryCheck;
	}

	public Set<FieldworkTimeLog> getFieldworkTimeLogs() {
		return fieldworkTimeLogs;
	}

	public void setFieldworkTimeLogs(Set<FieldworkTimeLog> fieldworkTimeLogs) {
		this.fieldworkTimeLogs = fieldworkTimeLogs;
	}

	public Set<TelephoneTimeLog> getTelephoneTimeLogs() {
		return telephoneTimeLogs;
	}

	public void setTelephoneTimeLogs(Set<TelephoneTimeLog> telephoneTimeLogs) {
		this.telephoneTimeLogs = telephoneTimeLogs;
	}

	public boolean isPreApproval() {
		return preApproval;
	}

	public void setPreApproval(boolean preApproval) {
		this.preApproval = preApproval;
	}

	public User getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(User approvedBy) {
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

	public VwItineraryPlanTimeLogMapBinded getItineraryPlanMap() {
		return itineraryPlanMap;
	}

	public void setItineraryPlanMap(VwItineraryPlanTimeLogMapBinded itineraryPlanMap) {
		this.itineraryPlanMap = itineraryPlanMap;
	}

}
