package capi.model.assignmentManagement.assignmentManagement;

import java.util.List;

import capi.model.shared.quotationRecord.PageViewModel;

public class QuotationRecordPageViewModel extends PageViewModel {
	private String validationSummary;
	private int assignmentId;
	private Integer userId;
	
	private Integer previousQuotationRecordId;
	private Integer nextQuotationRecordId;
	private Integer totalQuotationRecords;
	private Integer currentQuotationRecordNumber;
	
	private List<BackTrackDateModel> backTrackDates;
	
	private boolean allBackTrackPassValidation;
	
	private boolean isFlag;
	
	public String getValidationSummary() {
		return validationSummary;
	}
	public void setValidationSummary(String validationSummary) {
		this.validationSummary = validationSummary;
	}
	public int getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(int assignmentId) {
		this.assignmentId = assignmentId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getPreviousQuotationRecordId() {
		return previousQuotationRecordId;
	}
	public void setPreviousQuotationRecordId(Integer previousQuotationRecordId) {
		this.previousQuotationRecordId = previousQuotationRecordId;
	}
	public Integer getNextQuotationRecordId() {
		return nextQuotationRecordId;
	}
	public void setNextQuotationRecordId(Integer nextQuotationRecordId) {
		this.nextQuotationRecordId = nextQuotationRecordId;
	}
	public Integer getTotalQuotationRecords() {
		return totalQuotationRecords;
	}
	public void setTotalQuotationRecords(Integer totalQuotationRecords) {
		this.totalQuotationRecords = totalQuotationRecords;
	}
	public Integer getCurrentQuotationRecordNumber() {
		return currentQuotationRecordNumber;
	}
	public void setCurrentQuotationRecordNumber(Integer currentQuotationRecordNumber) {
		this.currentQuotationRecordNumber = currentQuotationRecordNumber;
	}
	public List<BackTrackDateModel> getBackTrackDates() {
		return backTrackDates;
	}
	public void setBackTrackDates(List<BackTrackDateModel> backTrackDates) {
		this.backTrackDates = backTrackDates;
	}
	public boolean isAllBackTrackPassValidation() {
		return allBackTrackPassValidation;
	}
	public void setAllBackTrackPassValidation(boolean allBackTrackPassValidation) {
		this.allBackTrackPassValidation = allBackTrackPassValidation;
	}
	public boolean isFlag() {
		return isFlag;
	}
	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}
}
