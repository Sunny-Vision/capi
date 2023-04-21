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

@Entity
@Table(name="SupervisoryVisitForm")
public class SupervisoryVisitForm extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SupervisoryVisitFormId")
	private Integer supervisoryVisitFormId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OfficerId", nullable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SupervisorId", nullable = true)
	private User supervisor;
	
	@Column(name="Session")
	private Integer session;
	
	@Column(name="VisitDate")
	private Date visitDate;
	
	@Column(name="FromTime")
	private Date fromTime;
	
	@Column(name="ToTime")
	private Date toTime;
	
	@Column(name="RejectReason")
	private String rejectReason;
	
	@Column(name="DiscussionDate")
	private Date discussionDate;
	
	@Column(name="Remark")
	private String remark;
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="KnowledgeOfWorkResult")
	private Integer knowledgeOfWorkResult;
	
	@Column(name="KnowledgeOfWorkRemark")
	private String knowledgeOfWorkRemark;
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="InterviewTechniqueResult")
	private Integer interviewTechniqueResult;
	
	@Column(name="InterviewTechniqueRemark")
	private String interviewTechniqueRemark;
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="HandleDifficultInterviewResult")
	private Integer handleDifficultInterviewResult;
	
	@Column(name="HandleDifficultInterviewRemark")
	private String handleDifficultInterviewRemark;
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="DataRecordingResult")
	private Integer dataRecordingResult;
	
	@Column(name="DataRecordingRemark")
	private String dataRecordingRemark;
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="LocalGeographyResult")
	private Integer localGeographyResult;
	
	@Column(name="LocalGeographyRemark")
	private String localGeographyRemark;	
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="MannerWithPublicResult")
	private Integer mannerWithPublicResult;
	
	@Column(name="MannerWithPublicRemark")
	private String mannerWithPublicRemark;
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="JudgmentResult")
	private Integer judgmentResult;
	
	@Column(name="JudgmentRemark")
	private String judgmentRemark;
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="OrganizationOfWorkResult")
	private Integer organizationOfWorkResult;
	
	@Column(name="OrganizationOfWorkRemark")
	private String organizationOfWorkRemark;
	
	/**
	 * 1- Remark 
	 * 2- No comment
	 * 3- N/A
	 */
	@Column(name="OtherResult")
	private Integer otherResult;
	
	@Column(name="OtherRemark")
	private String otherRemark;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubmitTo", nullable = true)
	private User submitTo;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supervisoryVisitForm")
	private Set<SupervisoryVisitDetail> details = new HashSet<SupervisoryVisitDetail>();
	
	/**
	 * Draft
	 * Submitted
	 * Approved
	 * Rejected
	 */
	@Column(name="Status")
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ScSvPlanId", nullable = true)
	private ScSvPlan scSvPlan;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSupervisoryVisitFormId();
	}

	public Integer getSupervisoryVisitFormId() {
		return supervisoryVisitFormId;
	}

	public void setSupervisoryVisitFormId(Integer supervisoryVisitFormId) {
		this.supervisoryVisitFormId = supervisoryVisitFormId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getKnowledgeOfWorkResult() {
		return knowledgeOfWorkResult;
	}

	public void setKnowledgeOfWorkResult(Integer knowledgeOfWorkResult) {
		this.knowledgeOfWorkResult = knowledgeOfWorkResult;
	}

	public String getKnowledgeOfWorkRemark() {
		return knowledgeOfWorkRemark;
	}

	public void setKnowledgeOfWorkRemark(String knowledgeOfWorkRemark) {
		this.knowledgeOfWorkRemark = knowledgeOfWorkRemark;
	}

	public Integer getInterviewTechniqueResult() {
		return interviewTechniqueResult;
	}

	public void setInterviewTechniqueResult(Integer interviewTechniqueResult) {
		this.interviewTechniqueResult = interviewTechniqueResult;
	}

	public String getInterviewTechniqueRemark() {
		return interviewTechniqueRemark;
	}

	public void setInterviewTechniqueRemark(String interviewTechniqueRemark) {
		this.interviewTechniqueRemark = interviewTechniqueRemark;
	}

	public Integer getHandleDifficultInterviewResult() {
		return handleDifficultInterviewResult;
	}

	public void setHandleDifficultInterviewResult(
			Integer handleDifficultInterviewResult) {
		this.handleDifficultInterviewResult = handleDifficultInterviewResult;
	}

	public String getHandleDifficultInterviewRemark() {
		return handleDifficultInterviewRemark;
	}

	public void setHandleDifficultInterviewRemark(
			String handleDifficultInterviewRemark) {
		this.handleDifficultInterviewRemark = handleDifficultInterviewRemark;
	}

	public Integer getDataRecordingResult() {
		return dataRecordingResult;
	}

	public void setDataRecordingResult(Integer dataRecordingResult) {
		this.dataRecordingResult = dataRecordingResult;
	}

	public String getDataRecordingRemark() {
		return dataRecordingRemark;
	}

	public void setDataRecordingRemark(String dataRecordingRemark) {
		this.dataRecordingRemark = dataRecordingRemark;
	}

	public Integer getLocalGeographyResult() {
		return localGeographyResult;
	}

	public void setLocalGeographyResult(Integer localGeographyResult) {
		this.localGeographyResult = localGeographyResult;
	}

	public String getLocalGeographyRemark() {
		return localGeographyRemark;
	}

	public void setLocalGeographyRemark(String localGeographyRemark) {
		this.localGeographyRemark = localGeographyRemark;
	}

	public Integer getMannerWithPublicResult() {
		return mannerWithPublicResult;
	}

	public void setMannerWithPublicResult(Integer mannerWithPublicResult) {
		this.mannerWithPublicResult = mannerWithPublicResult;
	}

	public String getMannerWithPublicRemark() {
		return mannerWithPublicRemark;
	}

	public void setMannerWithPublicRemark(String mannerWithPublicRemark) {
		this.mannerWithPublicRemark = mannerWithPublicRemark;
	}

	public Integer getJudgmentResult() {
		return judgmentResult;
	}

	public void setJudgmentResult(Integer judgmentResult) {
		this.judgmentResult = judgmentResult;
	}

	public String getJudgmentRemark() {
		return judgmentRemark;
	}

	public void setJudgmentRemark(String judgmentRemark) {
		this.judgmentRemark = judgmentRemark;
	}

	public Integer getOrganizationOfWorkResult() {
		return organizationOfWorkResult;
	}

	public void setOrganizationOfWorkResult(Integer organizationOfWorkResult) {
		this.organizationOfWorkResult = organizationOfWorkResult;
	}

	public String getOrganizationOfWorkRemark() {
		return organizationOfWorkRemark;
	}

	public void setOrganizationOfWorkRemark(String organizationOfWorkRemark) {
		this.organizationOfWorkRemark = organizationOfWorkRemark;
	}

	public Integer getOtherResult() {
		return otherResult;
	}

	public void setOtherResult(Integer otherResult) {
		this.otherResult = otherResult;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public Set<SupervisoryVisitDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<SupervisoryVisitDetail> details) {
		this.details = details;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

}
