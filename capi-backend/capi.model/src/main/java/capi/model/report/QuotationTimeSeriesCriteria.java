package capi.model.report;

import java.util.ArrayList;

public class QuotationTimeSeriesCriteria {

	private ArrayList<Integer> purpose;
	
	private ArrayList<Integer> itemId;

	private ArrayList<Integer> cpiSurveyForm;
	
	private ArrayList<Integer> quotationIds;
	
	private String periodReferenceMonth;

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

	public ArrayList<Integer> getQuotationIds() {
		return quotationIds;
	}

	public void setQuotationIds(ArrayList<Integer> quotationIds) {
		this.quotationIds = quotationIds;
	}

	public String getPeriodReferenceMonth() {
		return periodReferenceMonth;
	}

	public void setPeriodReferenceMonth(String periodReferenceMonth) {
		this.periodReferenceMonth = periodReferenceMonth;
	}

}
