package capi.model.masterMaintenance;

import java.util.Date;
import java.util.List;


public class SurveyTypeTableList {

	private Integer purposeId;
	
	private String code;
	
	private String name;
	
	/**
	 * - 1) CPI
	 * - 2) GHS
	 * - 3) BMWPS
	 * - 4) Others
	 */
	private String survey;
	
	private List<String> surveyList;
	
	private String note;

	private Date createdDate;

	private Date modifiedDate;
	
	private List<String> outcomes;

	public Integer getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public List<String> getSurveyList() {
		return surveyList;
	}

	public void setSurveyList(List<String> surveyList) {
		this.surveyList = surveyList;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<String> getOutcomes() {
		return outcomes;
	}

	public void setOutcomes(List<String> outcomes) {
		this.outcomes = outcomes;
	}

}
