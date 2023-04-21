package capi.model.report;

import java.util.Date;

public class FieldworkOutputByOutletTypeReport {
	
	private Date referenceMonth;
	
	private String purpose;
	
	private Integer purposeSequence;
	
	private String batchName;
	
	private String collectionMethod;
	
	private Integer userId;
	
	private String team;
	
	private String staffCode;
	
	private String staffName;
	
	private String rank;
	
	private String outletTypeName;
	
	private String outletTypeCode;
	
	private Double manDayRequired;
	
	private Integer completedAssignment;
	
	private Integer completedQuotationRecord;

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Integer getPurposeSequence() {
		return purposeSequence;
	}

	public void setPurposeSequence(Integer purposeSequence) {
		this.purposeSequence = purposeSequence;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getCollectionMethod() {
		return collectionMethod;
	}

	public void setCollectionMethod(String collectionMethod) {
		this.collectionMethod = collectionMethod;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
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

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getOutletTypeName() {
		return outletTypeName;
	}

	public void setOutletTypeName(String outletTypeName) {
		this.outletTypeName = outletTypeName;
	}

	public String getOutletTypeCode() {
		return outletTypeCode;
	}

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public Double getManDayRequired() {
		return manDayRequired;
	}

	public void setManDayRequired(Double manDayRequired) {
		this.manDayRequired = manDayRequired;
	}

	public Integer getCompletedAssignment() {
		return completedAssignment;
	}

	public void setCompletedAssignment(Integer completedAssignment) {
		this.completedAssignment = completedAssignment;
	}

	public Integer getCompletedQuotationRecord() {
		return completedQuotationRecord;
	}

	public void setCompletedQuotationRecord(Integer completedQuotationRecord) {
		this.completedQuotationRecord = completedQuotationRecord;
	}
	
}
