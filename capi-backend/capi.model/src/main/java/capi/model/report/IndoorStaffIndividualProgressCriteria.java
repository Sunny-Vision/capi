package capi.model.report;

import java.util.ArrayList;

public class IndoorStaffIndividualProgressCriteria {

	private ArrayList<Integer> purpose;
	
	private ArrayList<Integer> cpiSurveyForm;
	
	private ArrayList<Integer> batch;
	
	private ArrayList<Integer> group;
	
	private ArrayList<Integer> userId;
	
	private ArrayList<Integer> subGroup;
	
	private String startMonth;
	
	private String endMonth;
	
	private ArrayList<String> cpiBasePeriods;

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public ArrayList<Integer> getCpiSurveyForm() {
		return cpiSurveyForm;
	}

	public void setCpiSurveyForm(ArrayList<Integer> cpiSurveyForm) {
		this.cpiSurveyForm = cpiSurveyForm;
	}

	public ArrayList<Integer> getBatch() {
		return batch;
	}

	public void setBatch(ArrayList<Integer> batch) {
		this.batch = batch;
	}

	public ArrayList<Integer> getGroup() {
		return group;
	}

	public void setGroup(ArrayList<Integer> group) {
		this.group = group;
	}

	public ArrayList<Integer> getUserId() {
		return userId;
	}

	public void setUserId(ArrayList<Integer> userId) {
		this.userId = userId;
	}

	public ArrayList<Integer> getSubGroup() {
		return subGroup;
	}

	public void setSubGroup(ArrayList<Integer> subGroup) {
		this.subGroup = subGroup;
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

	public ArrayList<String> getCpiBasePeriods() {
		return cpiBasePeriods;
	}

	public void setCpiBasePeriods(ArrayList<String> cpiBasePeriods) {
		this.cpiBasePeriods = cpiBasePeriods;
	}

	
	
}
