package capi.model.report;

import java.util.ArrayList;

public class InformationSpotCheckFormCriteria {

	private String refMonth;

	private ArrayList<String> survey;

	private ArrayList<Integer> fieldOfficerId;

	private ArrayList<String> spotCheckDates;

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	public ArrayList<String> getSurvey() {
		return survey;
	}

	public void setSurvey(ArrayList<String> survey) {
		this.survey = survey;
	}

	public ArrayList<Integer> getFieldOfficerId() {
		return fieldOfficerId;
	}

	public void setFieldOfficerId(ArrayList<Integer> fieldOfficerId) {
		this.fieldOfficerId = fieldOfficerId;
	}

	public ArrayList<String> getSpotCheckDates() {
		return spotCheckDates;
	}

	public void setSpotCheckDates(ArrayList<String> spotCheckDates) {
		this.spotCheckDates = spotCheckDates;
	}

}
