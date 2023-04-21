package capi.model.report;

import java.util.ArrayList;

public class SummaryStatisticCriteria {

	private ArrayList<Integer> purpose;

	private ArrayList<Integer> unitId;

	private ArrayList<Integer> cpiSurveyForm;

	private String period;

	private String dataCollection;

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public ArrayList<Integer> getUnitId() {
		return unitId;
	}

	public void setUnitId(ArrayList<Integer> unitId) {
		this.unitId = unitId;
	}

	public ArrayList<Integer> getCpiSurveyForm() {
		return cpiSurveyForm;
	}

	public void setCpiSurveyForm(ArrayList<Integer> cpiSurveyForm) {
		this.cpiSurveyForm = cpiSurveyForm;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getDataCollection() {
		return dataCollection;
	}

	public void setDataCollection(String dataCollection) {
		this.dataCollection = dataCollection;
	}

}
