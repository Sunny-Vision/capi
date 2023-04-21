package capi.model.report;

import java.util.Date;
import java.util.Map;

public class SummaryItineraryReport {
	private String referenceMonth;
	
	private String date;
	
	private String staffCode;
	
	private String staffName;
	
	private String team;
	
	private String rank;

	private Integer userId;
	
	private Integer countTimeLog;
	
	private Map<String, Integer> mapDay;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getCountTimeLog() {
		return countTimeLog;
	}

	public void setCountTimeLog(Integer countTimeLog) {
		this.countTimeLog = countTimeLog;
	}

	public Map<String, Integer> getMapDay() {
		return mapDay;
	}

	public void setMapDay(Map<String, Integer> mapDay) {
		this.mapDay = mapDay;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

}
