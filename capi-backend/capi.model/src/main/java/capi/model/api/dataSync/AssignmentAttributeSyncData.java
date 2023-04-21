package capi.model.api.dataSync;

import java.util.Date;

public class AssignmentAttributeSyncData {

	private Integer assignmentAttributeId;
	
	private String batchCategory;
	
	private Date startDate;
	
	private Date endDate;
	
	private String session;
	
	private Integer allocationBatchId;
	
	private Integer userId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer batchId;
	
	private Integer surveyMonthId;

	public Integer getAssignmentAttributeId() {
		return assignmentAttributeId;
	}

	public void setAssignmentAttributeId(Integer assignmentAttributeId) {
		this.assignmentAttributeId = assignmentAttributeId;
	}

	public String getBatchCategory() {
		return batchCategory;
	}

	public void setBatchCategory(String batchCategory) {
		this.batchCategory = batchCategory;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}

	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}
	
}
