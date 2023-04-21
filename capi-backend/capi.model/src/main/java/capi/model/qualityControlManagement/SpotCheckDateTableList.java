package capi.model.qualityControlManagement;

import java.util.Date;
import java.util.List;

public class SpotCheckDateTableList {

	private Integer spotCheckDateId;

	private List<Integer> spotCheckDateIds;
	
	private List<String> spotCheckDates;

	private List<String> spotCheckDatesList;

	private String referenceMonth;

	private String selectedSpotCheckDate;

	private Long noOfDays;

	private Integer surveyMonthId;

	private Date createdDate;

	private Date modifiedDate;

	public Integer getSpotCheckDateId() {
		return spotCheckDateId;
	}

	public void setSpotCheckDateId(Integer spotCheckDateId) {
		this.spotCheckDateId = spotCheckDateId;
	}

	public List<Integer> getSpotCheckDateIds() {
		return spotCheckDateIds;
	}

	public void setSpotCheckDateIds(List<Integer> spotCheckDateIds) {
		this.spotCheckDateIds = spotCheckDateIds;
	}

	public List<String> getSpotCheckDates() {
		return spotCheckDates;
	}

	public void setSpotCheckDates(List<String> spotCheckDates) {
		this.spotCheckDates = spotCheckDates;
	}

	public List<String> getSpotCheckDatesList() {
		return spotCheckDatesList;
	}

	public void setSpotCheckDatesList(List<String> spotCheckDatesList) {
		this.spotCheckDatesList = spotCheckDatesList;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getSelectedSpotCheckDate() {
		return selectedSpotCheckDate;
	}

	public void setSelectedSpotCheckDate(String selectedSpotCheckDate) {
		this.selectedSpotCheckDate = selectedSpotCheckDate;
	}

	public Long getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(Long noOfDays) {
		this.noOfDays = noOfDays;
	}

	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
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

}
