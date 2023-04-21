package capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab;

import java.util.Date;

public class AllocationBatchDetailsModel {

	private Date surveyMonthStartDate;
	private String surveyMonthStartDateStr;
	private Date surveyMonthEndDate;
	private String surveyMonthEndDateStr;
	private Date allocationBatchStartDate;
	private String allocationBatchStartDateStr;
	private Date allocationBatchEndDate;
	private String allocationBatchEndDateStr;
	private Long noOfAssignment;
	private Long noOfQuotation;
	private Double noOfManDayRequired;
	private Double totalAvailableManDay;
	
	public Date getSurveyMonthStartDate() {
		return surveyMonthStartDate;
	}
	public void setSurveyMonthStartDate(Date surveyMonthStartDate) {
		this.surveyMonthStartDate = surveyMonthStartDate;
	}
	public String getSurveyMonthStartDateStr() {
		return surveyMonthStartDateStr;
	}
	public void setSurveyMonthStartDateStr(String surveyMonthStartDateStr) {
		this.surveyMonthStartDateStr = surveyMonthStartDateStr;
	}
	public Date getSurveyMonthEndDate() {
		return surveyMonthEndDate;
	}
	public void setSurveyMonthEndDate(Date surveyMonthEndDate) {
		this.surveyMonthEndDate = surveyMonthEndDate;
	}
	public String getSurveyMonthEndDateStr() {
		return surveyMonthEndDateStr;
	}
	public void setSurveyMonthEndDateStr(String surveyMonthEndDateStr) {
		this.surveyMonthEndDateStr = surveyMonthEndDateStr;
	}
	public Date getAllocationBatchStartDate() {
		return allocationBatchStartDate;
	}
	public void setAllocationBatchStartDate(Date allocationBatchStartDate) {
		this.allocationBatchStartDate = allocationBatchStartDate;
	}
	public String getAllocationBatchStartDateStr() {
		return allocationBatchStartDateStr;
	}
	public void setAllocationBatchStartDateStr(String allocationBatchStartDateStr) {
		this.allocationBatchStartDateStr = allocationBatchStartDateStr;
	}
	public Date getAllocationBatchEndDate() {
		return allocationBatchEndDate;
	}
	public void setAllocationBatchEndDate(Date allocationBatchEndDate) {
		this.allocationBatchEndDate = allocationBatchEndDate;
	}
	public String getAllocationBatchEndDateStr() {
		return allocationBatchEndDateStr;
	}
	public void setAllocationBatchEndDateStr(String allocationBatchEndDateStr) {
		this.allocationBatchEndDateStr = allocationBatchEndDateStr;
	}
	public Long getNoOfAssignment() {
		return noOfAssignment;
	}
	public void setNoOfAssignment(Long noOfAssignment) {
		this.noOfAssignment = noOfAssignment;
	}
	public Long getNoOfQuotation() {
		return noOfQuotation;
	}
	public void setNoOfQuotation(Long noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
	}
	public Double getNoOfManDayRequired() {
		return noOfManDayRequired;
	}
	public void setNoOfManDayRequired(Double noOfManDayRequired) {
		this.noOfManDayRequired = noOfManDayRequired;
	}
	public Double getTotalAvailableManDay() {
		return totalAvailableManDay;
	}
	public void setTotalAvailableManDay(Double totalAvailableManDay) {
		this.totalAvailableManDay = totalAvailableManDay;
	}
	
	
}
