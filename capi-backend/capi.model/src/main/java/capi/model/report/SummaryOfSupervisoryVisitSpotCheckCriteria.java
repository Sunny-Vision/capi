package capi.model.report;

import java.util.ArrayList;

public class SummaryOfSupervisoryVisitSpotCheckCriteria {

	private ArrayList<String> team;
	private ArrayList<Integer> userId;
	private String year;
	
	public ArrayList<String> getTeam() {
		return team;
	}
	public void setTeam(ArrayList<String> team) {
		this.team = team;
	}
	public ArrayList<Integer> getUserId() {
		return userId;
	}
	public void setUserId(ArrayList<Integer> userId) {
		this.userId = userId;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	
}
