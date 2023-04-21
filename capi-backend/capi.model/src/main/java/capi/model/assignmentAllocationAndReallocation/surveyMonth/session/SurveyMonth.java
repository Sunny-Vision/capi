package capi.model.assignmentAllocationAndReallocation.surveyMonth.session;

import java.io.Serializable;
import java.util.Date;

public class SurveyMonth implements Serializable{
	private Date referenceMonth;
	private Date startDate;
	private Date endDate;
	private Date closingDate;
	private Integer closingDateId;
	private String startDateStr;
	private String endDateStr;
	private String closingDateStr;
	private String referenceMonthStr;
	private Integer id;
	
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
	public Integer getClosingDateId() {
		return closingDateId;
	}
	public void setClosingDateId(Integer closingDateId) {
		this.closingDateId = closingDateId;
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
