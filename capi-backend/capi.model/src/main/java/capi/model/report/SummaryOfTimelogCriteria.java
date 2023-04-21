package capi.model.report;

import java.util.ArrayList;

public class SummaryOfTimelogCriteria {

	private ArrayList<String> survey;

	private String startMonth;

	private String endMonth;

	private ArrayList<String> team;

	private ArrayList<Integer> userIds;
	
	private Integer requestUserId;
	
	private Integer authorityLevel;

	public ArrayList<String> getSurvey() {
		return survey;
	}

	public void setSurvey(ArrayList<String> survey) {
		this.survey = survey;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public ArrayList<String> getTeam() {
		return team;
	}

	public void setTeam(ArrayList<String> team) {
		this.team = team;
	}

	public ArrayList<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(ArrayList<Integer> userIds) {
		this.userIds = userIds;
	}

	public Integer getRequestUserId() {
		return requestUserId;
	}

	public void setRequestUserId(Integer requestUserId) {
		this.requestUserId = requestUserId;
	}

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}

}
