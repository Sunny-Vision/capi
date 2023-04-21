package capi.model.report;

import java.util.List;

public class ExperienceSummaryCriteria {
	private Integer purposeId;
	private List<String> surveyId;
	private String startMonthStr;
	private String endMonthStr;
	private Integer marketType;
	private List<String> teamId;
	private List<Integer> officerId;
	private Integer cpiQuotationType;
	
	public Integer getPurposeId() {
		return purposeId;
	}
	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}
	public List<String> getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(List<String> surveyId) {
		this.surveyId = surveyId;
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
	public Integer getMarketType() {
		return marketType;
	}
	public void setMarketType(Integer marketType) {
		this.marketType = marketType;
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
	}
	public Integer getCpiQuotationType() {
		return cpiQuotationType;
	}
	public void setCpiQuotationType(Integer cpiQuotationType) {
		this.cpiQuotationType = cpiQuotationType;
	}
}
