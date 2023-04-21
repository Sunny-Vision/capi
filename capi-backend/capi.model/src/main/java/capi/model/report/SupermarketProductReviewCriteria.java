package capi.model.report;

import java.util.ArrayList;

public class SupermarketProductReviewCriteria {

	private ArrayList<Integer> purpose;
	
	//private ArrayList<Integer> unitId;
	private ArrayList<Integer> ItemId;

	private ArrayList<Integer> cpiSurveyForm;

	private String priceRemarks;

	private String dataConversionRemarks;
	
	private String startMonth;

	private String endMonth;

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	/*public ArrayList<Integer> getUnitId() {
		return unitId;
	}

	public void setUnitId(ArrayList<Integer> unitId) {
		this.unitId = unitId;
	}*/
	
	public ArrayList<Integer> getItemId() {
		return ItemId;
	}

	public void setItemId(ArrayList<Integer> ItemId) {
		this.ItemId = ItemId;
	}

	public ArrayList<Integer> getCpiSurveyForm() {
		return cpiSurveyForm;
	}

	public void setCpiSurveyForm(ArrayList<Integer> cpiSurveyForm) {
		this.cpiSurveyForm = cpiSurveyForm;
	}

	public String getPriceRemarks() {
		return priceRemarks;
	}

	public void setPriceRemarks(String priceRemarks) {
		this.priceRemarks = priceRemarks;
	}

	public String getDataConversionRemarks() {
		return dataConversionRemarks;
	}

	public void setDataConversionRemarks(String dataConversionRemarks) {
		this.dataConversionRemarks = dataConversionRemarks;
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
