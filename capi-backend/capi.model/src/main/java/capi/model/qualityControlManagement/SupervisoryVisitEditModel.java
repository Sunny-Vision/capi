package capi.model.qualityControlManagement;

import java.util.Date;
import java.util.List;

public class SupervisoryVisitEditModel {

	public static class AssignmentWithSurveyMonth {

		private Integer assignmentId;

		private String referenceNo;

		public Integer getAssignmentId() {
			return assignmentId;
		}

		public void setAssignmentId(Integer assignmentId) {
			this.assignmentId = assignmentId;
		}

		public String getReferenceNo() {
			return referenceNo;
		}

		public void setReferenceNo(String referenceNo) {
			this.referenceNo = referenceNo;
		}

	}

	private Integer supervisoryVisitFormId;

	private Integer surveyMonthId;

	private Integer fieldOfficerId;
	
	private String fieldOfficer;

	private String fieldOfficerPost;

	private String supervisor;

	private String supervisorPost;

	private Integer session;

	private String visitDate;

	private String fromTime;

	private String toTime;

	private String rejectReason;

	private List<String> detailSurveyList;

	private List<SupervisoryVisitDetailTableList> supervisoryVisitDetailList;

	private String discussionDate;

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

	private String submitTo;

	private Integer submitToId;

	private Date createdDate;

	private Date modifiedDate;
	
	private String status;

	public Integer getSupervisoryVisitFormId() {
		return supervisoryVisitFormId;
	}

	public void setSupervisoryVisitFormId(Integer supervisoryVisitFormId) {
		this.supervisoryVisitFormId = supervisoryVisitFormId;
	}

	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}

	public Integer getFieldOfficerId() {
		return fieldOfficerId;
	}

	public void setFieldOfficerId(Integer fieldOfficerId) {
		this.fieldOfficerId = fieldOfficerId;
	}

	public String getFieldOfficer() {
		return fieldOfficer;
	}

	public void setFieldOfficer(String fieldOfficer) {
		this.fieldOfficer = fieldOfficer;
	}

	public String getFieldOfficerPost() {
		return fieldOfficerPost;
	}

	public void setFieldOfficerPost(String fieldOfficerPost) {
		this.fieldOfficerPost = fieldOfficerPost;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getSupervisorPost() {
		return supervisorPost;
	}

	public void setSupervisorPost(String supervisorPost) {
		this.supervisorPost = supervisorPost;
	}

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
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

	public List<String> getDetailSurveyList() {
		return detailSurveyList;
	}

	public void setDetailSurveyList(List<String> detailSurveyList) {
		this.detailSurveyList = detailSurveyList;
	}

	public List<SupervisoryVisitDetailTableList> getSupervisoryVisitDetailList() {
		return supervisoryVisitDetailList;
	}

	public void setSupervisoryVisitDetailList(
			List<SupervisoryVisitDetailTableList> supervisoryVisitDetailList) {
		this.supervisoryVisitDetailList = supervisoryVisitDetailList;
	}

	public String getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(String discussionDate) {
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
