package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.util.Date;

import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch;

public class AllocationBatchEditModel {
	
	private Integer allocationBatchId;
	private Integer modelRefId;
	private String batchName;
	private Date startDate;
	private Date endDate;
	private Integer surveyMonthId;
	
	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}
	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}
	public Integer getModelRefId() {
		return modelRefId;
	}
	public void setModelRefId(Integer modelRefId) {
		this.modelRefId = modelRefId;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
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
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}
	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}
	
	public void convert(AllocationBatch sessionModel, Integer surveyMonthId) {
		this.setAllocationBatchId(sessionModel.getAllocationBatchId());
		this.setModelRefId(sessionModel.getId());
		this.setBatchName(sessionModel.getNumberOfBatch());
		this.setStartDate(sessionModel.getStartDate());
		this.setEndDate(sessionModel.getEndDate());
		this.setSurveyMonthId(surveyMonthId);
	}
}