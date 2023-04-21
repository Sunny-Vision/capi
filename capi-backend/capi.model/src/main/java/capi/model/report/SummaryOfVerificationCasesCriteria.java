package capi.model.report;

import java.util.List;

public class SummaryOfVerificationCasesCriteria {
	private List<Integer> purposeId;
	private String startMonthStr;
	private String endMonthStr;
	private List<String> teamId;
	private List<Integer> officerId;
	public List<Integer> getPurposeId() {
		return purposeId;
	}
	public void setPurposeId(List<Integer> purposeId) {
		this.purposeId = purposeId;
	}
	public String getStartMonthStr() {
		return startMonthStr;
	}
	public void setStartMonthStr(String startMonthStr) {
		this.startMonthStr = startMonthStr;
	}
	public String getEndMonthStr() {
		return endMonthStr;
	}
	public void setEndMonthStr(String endMonthStr) {
		this.endMonthStr = endMonthStr;
	}
	public List<String> getTeamId() {
		return teamId;
	}
	public void setTeamId(List<String> teamId) {
		this.teamId = teamId;
	}
	public List<Integer> getOfficerId() {
		return officerId;
	}
	public void setOfficerId(List<Integer> officerId) {
		this.officerId = officerId;
	};
}
