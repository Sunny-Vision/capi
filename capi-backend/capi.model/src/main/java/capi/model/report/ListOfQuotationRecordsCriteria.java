package capi.model.report;

import java.util.ArrayList;

public class ListOfQuotationRecordsCriteria {

	private ArrayList<Integer> purpose;

	private ArrayList<Integer> itemId;

	private ArrayList<Integer> cpiSurveyForm;

	private String dataCollection;

	private String startMonth;

	private String endMonth;

	private String imputedVariety;

	private String imputedQuotation;

	private String notEqual;

	private String ruaCase;

	private String priceCondition;

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

	public String getDataCollection() {
		return dataCollection;
	}

	public void setDataCollection(String dataCollection) {
		this.dataCollection = dataCollection;
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

	public String getImputedVariety() {
		return imputedVariety;
	}

	public void setImputedVariety(String imputedVariety) {
		this.imputedVariety = imputedVariety;
	}

	public String getImputedQuotation() {
		return imputedQuotation;
	}

	public void setImputedQuotation(String imputedQuotation) {
		this.imputedQuotation = imputedQuotation;
	}

	public String getNotEqual() {
		return notEqual;
	}

	public void setNotEqual(String notEqual) {
		this.notEqual = notEqual;
	}

	public String getRuaCase() {
		return ruaCase;
	}

	public void setRuaCase(String ruaCase) {
		this.ruaCase = ruaCase;
	}

	public String getPriceCondition() {
		return priceCondition;
	}

	public void setPriceCondition(String priceCondition) {
		this.priceCondition = priceCondition;
	}

}
