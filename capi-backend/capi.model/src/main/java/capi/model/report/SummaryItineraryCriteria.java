package capi.model.report;

public class SummaryItineraryCriteria {

	private Integer userId;
	
	private Integer authorityLevel;
	
	private String refMonth;

	private Integer[] officerIds;
	
	private String[] teams;

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

	public String[] getTeams() {
		return teams;
	}

	public void setTeams(String[] teams) {
		this.teams = teams;
	}
	
}
