package capi.model.report;

import java.util.ArrayList;

public class InformationSupervisoryVisitFormCriteria {

	private String refMonth;

	private ArrayList<String> survey;

	private ArrayList<Integer> fieldOfficerId;

	private ArrayList<String> supervisoryVisitDates;

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

	public ArrayList<String> getSupervisoryVisitDates() {
		return supervisoryVisitDates;
	}

	public void setSupervisoryVisitDates(ArrayList<String> supervisoryVisitDates) {
		this.supervisoryVisitDates = supervisoryVisitDates;
	}

}
