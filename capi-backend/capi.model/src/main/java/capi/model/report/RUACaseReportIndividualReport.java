package capi.model.report;

import java.util.Date;

public class RUACaseReportIndividualReport {

	public static class districtCodeMapping{
		private String district;
		
		private Integer newRecruitment;
		
		public String getDistrict() {
			return district;
		}

		public void setDistrict(String district) {
			this.district = district;
		}

		public Integer getNewRecruitment() {
			return newRecruitment;
		}

		public void setNewRecruitment(Integer newRecruitment) {
			this.newRecruitment = newRecruitment;
		}

	}
	
	private Integer surveyMonthId;
	
	private Date referenceMonth;
	
	private Integer purposeId;
	
	private String purpose;
	
	private Integer userId;
	
	private String team;
	
	private String rank;
	
	private String staffCode;
	
	private String staffName;
	
	private String outletTypeCode;
	
	private String outletTypeName;

	public Integer getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
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

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
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

	public String getOutletTypeCode() {
		return outletTypeCode;
	}

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public String getOutletTypeName() {
		return outletTypeName;
	}

	public void setOutletTypeName(String outletTypeName) {
		this.outletTypeName = outletTypeName;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}
	
}
