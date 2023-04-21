package capi.model.report;

public class SummaryOfTimeoffOvertimeCriteria {

	private String[] teams;
	private Integer[] officerIds;
	private String fromDate;
	private String toDate;
	private Integer userId;
	private Integer authorityLevel;

	public String[] getTeams() {
		return teams;
	}

	public void setTeams(String[] teams) {
		this.teams = teams;
	}

	public Integer[] getOfficerIds() {
		return officerIds;
	}

	public void setOfficerIds(Integer[] officerIds) {
		this.officerIds = officerIds;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
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
