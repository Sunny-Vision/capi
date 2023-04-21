package capi.model.report;

import java.util.ArrayList;

public class QuotationStatisticsReportByVarietyCriteria {

	private ArrayList<Integer> purpose;
	
	private ArrayList<Integer> cpiSurveyForm;
	
	private ArrayList<Integer> unitId;
	
	private String periodReferenceMonth;
	
	private ArrayList<Integer> itemId;
	
	private ArrayList<Integer> quotationId;

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

	public ArrayList<Integer> getUnitId() {
		return unitId;
	}

	public void setUnitId(ArrayList<Integer> unitId) {
		this.unitId = unitId;
	}

	public String getPeriodReferenceMonth() {
		return periodReferenceMonth;
	}

	public void setPeriodReferenceMonth(String periodReferenceMonth) {
		this.periodReferenceMonth = periodReferenceMonth;
	}

	/**
	 * @return the itemId
	 */
	public ArrayList<Integer> getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(ArrayList<Integer> itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the quotationId
	 */
	public ArrayList<Integer> getQuotationId() {
		return quotationId;
	}

	/**
	 * @param quotationId the quotationId to set
	 */
	public void setQuotationId(ArrayList<Integer> quotationId) {
		this.quotationId = quotationId;
	}

}
