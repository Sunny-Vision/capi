package capi.model.report;

import java.util.Date;

public class FieldworkOutputByDistrictReport {
	private Integer id;
	
	private Integer userId;
	
	private Date referenceMonth;
	
	private String purpose;
	
	private String survey;
	
	private String batchName;
	
	private String team;
	
	private String rank;
	
	private String collectionMethod;
	
	private String staffCode;
	
	private String staffName;
	
	private Double manDayRequired;
	
	private String district;
	
	private Integer countAssignment;
	
	private Integer countQuotationRecord;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getCollectionMethod() {
		return collectionMethod;
	}

	public void setCollectionMethod(String collectionMethod) {
		this.collectionMethod = collectionMethod;
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

	public Double getManDayRequired() {
		return manDayRequired;
	}

	public void setManDayRequired(Double manDayRequired) {
		this.manDayRequired = manDayRequired;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Integer getCountAssignment() {
		return countAssignment;
	}

	public void setCountAssignment(Integer countAssignment) {
		this.countAssignment = countAssignment;
	}

	public Integer getCountQuotationRecord() {
		return countQuotationRecord;
	}

	public void setCountQuotationRecord(Integer countQuotationRecord) {
		this.countQuotationRecord = countQuotationRecord;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
}
