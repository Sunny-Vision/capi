package capi.model.qualityControlManagement;

import java.util.List;

public class PECheckTaskModel {

	private Integer surveyMonthId;

	private String referenceMonth;
	
	private Integer userId;
	
	private String fieldOfficer;
	
	private Integer isFieldTeamHead;
	
	private Integer isSectionHead;

	private List<PECheckTaskList> peCheckTaskList;
	
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFieldOfficer() {
		return fieldOfficer;
	}

	public void setFieldOfficer(String fieldOfficer) {
		this.fieldOfficer = fieldOfficer;
	}

	public List<PECheckTaskList> getPeCheckTaskList() {
		return peCheckTaskList;
	}

	public void setPeCheckTaskList(List<PECheckTaskList> peCheckTaskList) {
		this.peCheckTaskList = peCheckTaskList;
	}

	public Integer getIsFieldTeamHead() {
		return isFieldTeamHead;
	}

	public void setIsFieldTeamHead(Integer isFieldTeamHead) {
		this.isFieldTeamHead = isFieldTeamHead;
	}

	public Integer getIsSectionHead() {
		return isSectionHead;
	}

	public void setIsSectionHead(Integer isSectionHead) {
		this.isSectionHead = isSectionHead;
	}

	
}
