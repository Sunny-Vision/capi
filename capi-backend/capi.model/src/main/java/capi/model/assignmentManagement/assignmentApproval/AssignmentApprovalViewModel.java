package capi.model.assignmentManagement.assignmentApproval;

import java.util.List;

import capi.model.KeyValueModel;

public class AssignmentApprovalViewModel {

	private KeyValueModel outletSelected;
	
	private KeyValueModel outletTypeSelected;
	
	private KeyValueModel personInChargeSelected;
	
	private List<KeyValueModel> districtSelected;
	
	private String unitCategory;
	
	private List<KeyValueModel> tpuSelected;
	
	private Boolean isProductChange;
	
	private Boolean isSPricePeculiar;
	
	private List<Boolean> availability;
	
	private String referenceMonth;
	
	private List<KeyValueModel> purposeSelected;
	
	private String search;
	
	private Integer orderColumn;
	
	private String orderDir;

	public KeyValueModel getOutletSelected() {
		return outletSelected;
	}

	public void setOutletSelected(KeyValueModel outletSelected) {
		this.outletSelected = outletSelected;
	}

	public KeyValueModel getOutletTypeSelected() {
		return outletTypeSelected;
	}

	public void setOutletTypeSelected(KeyValueModel outletTypeSelected) {
		this.outletTypeSelected = outletTypeSelected;
	}

	public KeyValueModel getPersonInChargeSelected() {
		return personInChargeSelected;
	}

	public void setPersonInChargeSelected(KeyValueModel personInChargeSelected) {
		this.personInChargeSelected = personInChargeSelected;
	}

	public List<KeyValueModel> getDistrictSelected() {
		return districtSelected;
	}

	public void setDistrictSelected(List<KeyValueModel> districtSelected) {
		this.districtSelected = districtSelected;
	}

	public String getUnitCategory() {
		return unitCategory;
	}

	public void setUnitCategory(String unitCategory) {
		this.unitCategory = unitCategory;
	}

	public List<KeyValueModel> getTpuSelected() {
		return tpuSelected;
	}

	public void setTpuSelected(List<KeyValueModel> tpuSelected) {
		this.tpuSelected = tpuSelected;
	}

	public Boolean getIsProductChange() {
		return isProductChange;
	}

	public void setIsProductChange(Boolean isProductChange) {
		this.isProductChange = isProductChange;
	}

	public Boolean getIsSPricePeculiar() {
		return isSPricePeculiar;
	}

	public void setIsSPricePeculiar(Boolean isSPricePeculiar) {
		this.isSPricePeculiar = isSPricePeculiar;
	}

	public List<Boolean> getAvailability() {
		return availability;
	}

	public void setAvailability(List<Boolean> availability) {
		this.availability = availability;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public List<KeyValueModel> getPurposeSelected() {
		return purposeSelected;
	}

	public void setPurposeSelected(List<KeyValueModel> purposeSelected) {
		this.purposeSelected = purposeSelected;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
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
