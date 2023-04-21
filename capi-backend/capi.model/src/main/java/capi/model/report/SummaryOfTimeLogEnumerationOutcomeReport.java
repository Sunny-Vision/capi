package capi.model.report;

import java.util.Date;

public class SummaryOfTimeLogEnumerationOutcomeReport {

	private Date timeLogDate;
	
	private String survey;
	
	private String team;
	
	private String rank;
	
	private String staffCode;
	
	private String staffName;
	
	private Integer completion;
	
	private Integer deletion;
	
	private Integer doorLocked;
	
	private Integer Moved;
	
	private Integer nonContact;
	
	private Integer nonDomestic;
	
	private Integer unoccupied;
	
	private Integer partially;
	
	private Integer refusal;
	
	private Integer others;
	
	private Integer blank;
	
	private Integer total;
	
	private Integer surveySequence;

	public Date getTimeLogDate() {
		return timeLogDate;
	}

	public void setTimeLogDate(Date timeLogDate) {
		this.timeLogDate = timeLogDate;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
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

	public Integer getCompletion() {
		return completion;
	}

	public void setCompletion(Integer completion) {
		this.completion = completion;
	}

	public Integer getDeletion() {
		return deletion;
	}

	public void setDeletion(Integer deletion) {
		this.deletion = deletion;
	}

	public Integer getDoorLocked() {
		return doorLocked;
	}

	public void setDoorLocked(Integer doorLocked) {
		this.doorLocked = doorLocked;
	}

	public Integer getMoved() {
		return Moved;
	}

	public void setMoved(Integer moved) {
		Moved = moved;
	}

	public Integer getNonContact() {
		return nonContact;
	}

	public void setNonContact(Integer nonContact) {
		this.nonContact = nonContact;
	}

	public Integer getNonDomestic() {
		return nonDomestic;
	}

	public void setNonDomestic(Integer nonDomestic) {
		this.nonDomestic = nonDomestic;
	}

	public Integer getUnoccupied() {
		return unoccupied;
	}

	public void setUnoccupied(Integer unoccupied) {
		this.unoccupied = unoccupied;
	}

	public Integer getPartially() {
		return partially;
	}

	public void setPartially(Integer partially) {
		this.partially = partially;
	}

	public Integer getRefusal() {
		return refusal;
	}

	public void setRefusal(Integer refusal) {
		this.refusal = refusal;
	}

	public Integer getOthers() {
		return others;
	}

	public void setOthers(Integer others) {
		this.others = others;
	}

	public Integer getBlank() {
		return blank;
	}

	public void setBlank(Integer blank) {
		this.blank = blank;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getSurveySequence() {
		return surveySequence;
	}

	public void setSurveySequence(Integer surveySequence) {
		this.surveySequence = surveySequence;
	}

}
