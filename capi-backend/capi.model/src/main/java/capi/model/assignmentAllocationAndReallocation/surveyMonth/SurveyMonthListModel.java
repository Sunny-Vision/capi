package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.util.Date;

public class SurveyMonthListModel {
	Integer surveyMonthId;
	Date referenceMonth;
	Date startDate;
	Date endDate;
	Date closingDate;
	String referenceMonthStr;
	String startDateStr;
	String endDateStr;
	String closingDateStr;
	Boolean removable;
	Integer status;
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}
	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}
	public Date getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
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
	public Date getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}
	public String getReferenceMonthStr() {
		return referenceMonthStr;
	}
	public void setReferenceMonthStr(String referenceMonthStr) {
		this.referenceMonthStr = referenceMonthStr;
	}
	public String getStartDateStr() {
		return startDateStr;
	}
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}
	public String getEndDateStr() {
		return endDateStr;
	}
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}
	public String getClosingDateStr() {
		return closingDateStr;
	}
	public void setClosingDateStr(String closingDateStr) {
		this.closingDateStr = closingDateStr;
	}
	public Boolean getRemovable() {
		return removable;
	}
	public void setRemovable(Boolean removable) {
		this.removable = removable;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
	
}
