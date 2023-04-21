package capi.model.report;

import java.util.Date;

public class SummaryOfProgressReport {

	private Date referenceMonth;
	private String staffCode;
	private String staffName;
	private String team;
	private String survey;
	private String purpose;
	private String batchName;
	private String rank;
	private Integer totalAssignment;
	private Integer completedAssignment;
	private Integer totalQuotationRecord;
	private Integer completedQuotationRecord;
	private Integer outstandingQuotationRecord;

	public Date getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public String getStaffCode() {
		return staffCode;
	}
	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public String getSurvey() {
		return survey;
	}
	public void setSurvey(String survey) {
		this.survey = survey;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public Integer getTotalAssignment() {
		return totalAssignment;
	}
	public void setTotalAssignment(Integer totalAssignment) {
		this.totalAssignment = totalAssignment;
	}
	public Integer getCompletedAssignment() {
		return completedAssignment;
	}
	public void setCompletedAssignment(Integer completedAssignment) {
		this.completedAssignment = completedAssignment;
	}
	public Integer getTotalQuotationRecord() {
		return totalQuotationRecord;
	}
	public void setTotalQuotationRecord(Integer totalQuotationRecord) {
		this.totalQuotationRecord = totalQuotationRecord;
	}
	public Integer getCompletedQuotationRecord() {
		return completedQuotationRecord;
	}
	public void setCompletedQuotationRecord(Integer completedQuotationRecord) {
		this.completedQuotationRecord = completedQuotationRecord;
	}
	public Integer getOutstandingQuotationRecord() {
		return outstandingQuotationRecord;
	}
	public void setOutstandingQuotationRecord(Integer outstandingQuotationRecord) {
		this.outstandingQuotationRecord = outstandingQuotationRecord;
	}
}
