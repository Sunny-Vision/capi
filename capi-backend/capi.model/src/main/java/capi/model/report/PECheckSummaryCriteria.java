package capi.model.report;

public class PECheckSummaryCriteria {

	private Integer userId;
	
	private Integer authorityLevel;
	
	private String fromMonth;
	
	private String toMonth;
	
	private Integer[] officerIds;
	
	private String[] teams;

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

	public String getFromMonth() {
		return fromMonth;
	}

	public void setFromMonth(String fromMonth) {
		this.fromMonth = fromMonth;
	}

	public String getToMonth() {
		return toMonth;
	}

	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
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
