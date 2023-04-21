package capi.model.report;

public class SummaryOfNightSessionReport {
	private String team;
	
	private String rank;
	
	private Integer rankSequence;
	
	private String staffCode;
	
	private String staffName;

	private Integer userId;
	
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

	public Integer getRankSequence() {
		return rankSequence;
	}

	public void setRankSequence(Integer rankSequence) {
		this.rankSequence = rankSequence;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
}
