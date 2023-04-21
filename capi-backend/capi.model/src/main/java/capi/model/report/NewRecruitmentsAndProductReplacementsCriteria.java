package capi.model.report;

import java.util.ArrayList;

public class NewRecruitmentsAndProductReplacementsCriteria {

	private ArrayList<Integer> purpose;
//2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
	private ArrayList<Integer> itemId;

	private ArrayList<Integer> cpiSurveyForm;

	private String startMonth;

	private String endMonth;

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public ArrayList<Integer> getItemId() {
		return itemId;
	}

	public void setItemId(ArrayList<Integer> itemId) {
		this.itemId = itemId;
	}

	public ArrayList<Integer> getCpiSurveyForm() {
		return cpiSurveyForm;
	}

	public void setCpiSurveyForm(ArrayList<Integer> cpiSurveyForm) {
		this.cpiSurveyForm = cpiSurveyForm;
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

}
