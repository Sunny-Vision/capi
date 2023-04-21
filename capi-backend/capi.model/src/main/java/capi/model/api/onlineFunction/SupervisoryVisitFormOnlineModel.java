package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.SupervisoryVisitDetailSyncData;

public class SupervisoryVisitFormOnlineModel {

	private Integer supervisoryVisitFormId;
	
	private Integer officerId;
	
	private Integer supervisorId;
	
	private Integer session;
	
	private Date visitDate;
	
	private String fromTime;
	
	private String toTime;
	
	private String rejectReason;
	
	private Date discussionDate;
	
	private String remark;
	
	private Integer knowledgeOfWorkResult;
	
	private String knowledgeOfWorkRemark;
	
	private Integer interviewTechniqueResult;
	
	private String interviewTechniqueRemark;
	
	private Integer handleDifficultInterviewResult;
	
	private String handleDifficultInterviewRemark;
	
	private Integer dataRecordingResult;
	
	private String dataRecordingRemark;
	
	private Integer localGeographyResult;
	
	private String localGeographyRemark;
	
	private Integer mannerWithPublicResult;
	
	private String mannerWithPublicRemark;
	
	private Integer judgmentResult;
	
	private String judgmentRemark;
	
	private Integer organizationOfWorkResult;
	
	private String organizationOfWorkRemark;
	
	private Integer otherResult;
	
	private String otherRemark;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer submitTo;
	
	private String status;
	
	private Integer scSvPlanId;
	
	private Integer localId;
	
	private String localDbRecordStatus;
	
	private List<SupervisoryVisitDetailSyncData> details;

	public Integer getSupervisoryVisitFormId() {
		return supervisoryVisitFormId;
	}

	public void setSupervisoryVisitFormId(Integer supervisoryVisitFormId) {
		this.supervisoryVisitFormId = supervisoryVisitFormId;
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

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
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

	public void setHandleDifficultInterviewResult(Integer handleDifficultInterviewResult) {
		this.handleDifficultInterviewResult = handleDifficultInterviewResult;
	}

	public String getHandleDifficultInterviewRemark() {
		return handleDifficultInterviewRemark;
	}

	public void setHandleDifficultInterviewRemark(String handleDifficultInterviewRemark) {
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

	public Integer getSubmitTo() {
		return submitTo;
	}

	public void setSubmitTo(Integer submitTo) {
		this.submitTo = submitTo;
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

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public List<SupervisoryVisitDetailSyncData> getDetails() {
		return details;
	}

	public void setDetails(List<SupervisoryVisitDetailSyncData> details) {
		this.details = details;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}
	
}
