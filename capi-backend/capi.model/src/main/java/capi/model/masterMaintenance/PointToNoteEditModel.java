package capi.model.masterMaintenance;

import java.util.List;

public class PointToNoteEditModel {
	private Integer id;
	private String note;
	private String effectiveDate;
	private String expiryDate;
	private List<Integer> outletIds;
	private List<Integer> productIds;
	private List<Integer> quotationIds;
	private List<Integer> unitIds;
	
	private String createdDate;
	private String modifiedDate;

	private Boolean isAllOutlet;
	private Boolean isAllProduct;
	private Boolean isAllQuotation;
	private Boolean isAllUnit;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public List<Integer> getOutletIds() {
		return outletIds;
	}
	public void setOutletIds(List<Integer> outletIds) {
		this.outletIds = outletIds;
	}
	public List<Integer> getProductIds() {
		return productIds;
	}
	public void setProductIds(List<Integer> productIds) {
		this.productIds = productIds;
	}
	public List<Integer> getQuotationIds() {
		return quotationIds;
	}
	public void setQuotationIds(List<Integer> quotationIds) {
		this.quotationIds = quotationIds;
	}
	public List<Integer> getUnitIds() {
		return unitIds;
	}
	public void setUnitIds(List<Integer> unitIds) {
		this.unitIds = unitIds;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public Boolean getIsAllOutlet() {
		return isAllOutlet;
	}
	public void setIsAllOutlet(Boolean isAllOutlet) {
		this.isAllOutlet = isAllOutlet;
	}
	public Boolean getIsAllProduct() {
		return isAllProduct;
	}
	public void setIsAllProduct(Boolean isAllProduct) {
		this.isAllProduct = isAllProduct;
	}
	public Boolean getIsAllQuotation() {
		return isAllQuotation;
	}
	public void setIsAllQuotation(Boolean isAllQuotation) {
		this.isAllQuotation = isAllQuotation;
	}
	public Boolean getIsAllUnit() {
		return isAllUnit;
	}
	public void setIsAllUnit(Boolean isAllUnit) {
		this.isAllUnit = isAllUnit;
	}
}
