package capi.model.report;

import java.util.ArrayList;

public class SummaryStatisticsOfPriceRelativesCriteria {

	private String fromMonth;
	
	private String toMonth;
	
	private ArrayList<Integer> purpose;
	
	private ArrayList<Integer> itemId;
	
	private ArrayList<String> outletTypeId;
	
	private ArrayList<Integer> cpiSurveyForm;

	public String getFromMonth() {
		return fromMonth;
	}

	public void setFromMonth(String fromMonth) {
		this.fromMonth = fromMonth;
	}

	public String getToMonth() {
		return toMonth;
	}

	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
	}

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

	public ArrayList<String> getOutletTypeId() {
		return outletTypeId;
	}

	public void setOutletTypeId(ArrayList<String> outletTypeId) {
		this.outletTypeId = outletTypeId;
	}

	public ArrayList<Integer> getCpiSurveyForm() {
		return cpiSurveyForm;
	}

	public void setCpiSurveyForm(ArrayList<Integer> cpiSurveyForm) {
		this.cpiSurveyForm = cpiSurveyForm;
	}
	
}
