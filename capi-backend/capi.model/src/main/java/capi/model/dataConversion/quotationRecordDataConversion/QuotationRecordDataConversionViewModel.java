package capi.model.dataConversion.quotationRecordDataConversion;

import java.util.List;

import capi.model.KeyValueModel;

public class QuotationRecordDataConversionViewModel {
	private String search;
	private List<KeyValueModel> outletTypeSelected;
	private String unitCategorySelected;
	private KeyValueModel outletSelected;
	private String surveyFormSelected;
	private String seasonalItemSelected;
	private String availabilitySelected;
	private String firmStatusSelected;
	private String isPRNullSelected;
	private String quotationIdSelected;
	
	private Integer orderColumn;
	private String orderDir;
	
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public List<KeyValueModel> getOutletTypeSelected() {
		return outletTypeSelected;
	}
	public void setOutletTypeSelected(List<KeyValueModel> outletTypeSelected) {
		this.outletTypeSelected = outletTypeSelected;
	}
	public String getUnitCategorySelected() {
		return unitCategorySelected;
	}
	public void setUnitCategorySelected(String unitCategorySelected) {
		this.unitCategorySelected = unitCategorySelected;
	}
	public KeyValueModel getOutletSelected() {
		return outletSelected;
	}
	public void setOutletSelected(KeyValueModel outletSelected) {
		this.outletSelected = outletSelected;
	}
	public String getSurveyFormSelected() {
		return surveyFormSelected;
	}
	public void setSurveyFormSelected(String surveyFormSelected) {
		this.surveyFormSelected = surveyFormSelected;
	}
	public String getSeasonalItemSelected() {
		return seasonalItemSelected;
	}
	public void setSeasonalItemSelected(String seasonalItemSelected) {
		this.seasonalItemSelected = seasonalItemSelected;
	}
	public String getAvailabilitySelected() {
		return availabilitySelected;
	}
	public void setAvailabilitySelected(String availabilitySelected) {
		this.availabilitySelected = availabilitySelected;
	}
	public String getFirmStatusSelected() {
		return firmStatusSelected;
	}
	public void setFirmStatusSelected(String firmStatusSelected) {
		this.firmStatusSelected = firmStatusSelected;
	}
	public String getIsPRNullSelected() {
		return isPRNullSelected;
	}
	public void setIsPRNullSelected(String isPRNullSelected) {
		this.isPRNullSelected = isPRNullSelected;
	}
	public String getQuotationIdSelected() {
		return quotationIdSelected;
	}
	public void setQuotationIdSelected(String quotationIdSelected) {
		this.quotationIdSelected = quotationIdSelected;
	}
	public Integer getOrderColumn() {
		return orderColumn;
	}
	public void setOrderColumn(Integer orderColumn) {
		this.orderColumn = orderColumn;
	}
	public String getOrderDir() {
		return orderDir;
	}
	public void setOrderDir(String orderDir) {
		this.orderDir = orderDir;
	}
}
