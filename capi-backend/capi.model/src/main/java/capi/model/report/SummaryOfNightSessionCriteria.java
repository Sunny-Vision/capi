package capi.model.report;

public class SummaryOfNightSessionCriteria {

	private Integer userId;
	
	private Integer authorityLevel;
	
	private Integer[] officerIds;
	
	private String refMonth;

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	public Integer[] getOfficerIds() {
		return officerIds;
	}

	public void setOfficerIds(Integer[] officerIds) {
		this.officerIds = officerIds;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}
	
}
