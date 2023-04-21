package capi.model.report;

import java.util.ArrayList;

public class SummaryOfTimeLogEnumerationOutcomeCriteria {

	private ArrayList<String> survey;
	private ArrayList<String> team;
	private ArrayList<Integer> officerId;
	private String fromDate;
	private String toDate;
	private Integer authorityLevel;
	private Integer userId;
	
	public ArrayList<String> getSurvey() {
		return survey;
	}
	public void setSurvey(ArrayList<String> survey) {
		this.survey = survey;
	}
	public ArrayList<String> getTeam() {
		return team;
	}
	public void setTeam(ArrayList<String> team) {
		this.team = team;
	}
	public ArrayList<Integer> getOfficerId() {
		return officerId;
	}
	public void setOfficerId(ArrayList<Integer> officerId) {
		this.officerId = officerId;
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
	public Integer getAuthorityLevel() {
		return authorityLevel;
	}
	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
