package capi.model.report;

import java.util.Map;

public class DynamicExperienceSummaryReport {
	
	private String referenceMonth;
	
	private String team;
	
	private String rank;
	
	private String staffCode;
	
	private String staffName;
	
	private Integer cpiQuotationType;
	
	private String cpiSurveyForm;

	private String purpose;
	
	private Map<String,Integer> districtAndCount;
	
	private Integer total;
	
	public DynamicExperienceSummaryReport(ExperienceSummaryReport data, Map<String, Integer> map) {
		this.referenceMonth = data.getReferenceMonth();
		this.team = data.getTeam();
		this.rank = data.getRank();
		this.staffCode = data.getStaffCode();
		this.staffName = data.getStaffName();
		this.cpiQuotationType = data.getCpiQuotationType();
		this.purpose = data.getPurpose();
		this.districtAndCount = map;
		setTotal();
		setCpiSurveyForm();
	}

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

	public Map<String, Integer> getDistrictAndCount() {
		return districtAndCount;
	}

	public void setDistrictAndCount(Map<String, Integer> districtAndCount) {
		this.districtAndCount = districtAndCount;
	}
	
	public Integer getTotal() {
		return total;
	}

	public void setTotal() {
		int i = 0;
		for(Map.Entry<String,Integer> entry : districtAndCount.entrySet()) {
			i+=entry.getValue();
		}
		this.total = i;
	}

	public String getCpiSurveyForm() {
		return cpiSurveyForm;
	}

	public void setCpiSurveyForm() {
		switch (cpiQuotationType) {
		case 1:this.cpiSurveyForm = "Market";break;
		case 2:this.cpiSurveyForm = "Supermarket";break;
		case 3:this.cpiSurveyForm = "Batch";break;
		case 4:this.cpiSurveyForm = "Others";break;
		default:this.cpiSurveyForm = "";
		}	
	}

}
