package capi.model.report;

import java.util.ArrayList;

public class AirportTicketPRListByCountryWithCPISeriesCriteria {

	private ArrayList<Integer> purpose;
 
	//2018-01-04 chenug_cheng [MB9010]  The lookup table should show up to item level only  -
	//private ArrayList<Integer> unitId;
	private ArrayList<Integer> itemId;

	private ArrayList<Integer> cpiSurveyForm;

	private ArrayList<String> quotationFormType;

	private String startMonth;

	private String endMonth;

	private String dataCollection;

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

	public ArrayList<String> getQuotationFormType() {
		return quotationFormType;
	}

	public void setQuotationFormType(ArrayList<String> quotationFormType) {
		this.quotationFormType = quotationFormType;
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

	public String getDataCollection() {
		return dataCollection;
	}

	public void setDataCollection(String dataCollection) {
		this.dataCollection = dataCollection;
	}

}
