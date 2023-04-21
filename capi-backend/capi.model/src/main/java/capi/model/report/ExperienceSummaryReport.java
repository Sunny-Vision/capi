package capi.model.report;

public class ExperienceSummaryReport {
	
	private String referenceMonth;
	
	private String team;
	
	private String rank;
	
	private String staffCode;
	
	private String staffName;
	
	private Integer cpiQuotationType;

	private String purpose;

	private String district;

	private Integer count;

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
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

	public Integer getCpiQuotationType() {
		return cpiQuotationType;
	}

	public void setCpiQuotationType(Integer cpiQuotationType) {
		this.cpiQuotationType = cpiQuotationType;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Integer getCount() {
		return count;
	}

	public void setCountAssignment(Integer count) {
		this.count = count;
	}
}
