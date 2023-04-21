package capi.model.api.dataSync;

import java.util.Date;

public class PointToNoteSyncData {
	
	private Integer pointToNoteId;
	
	private String note;
	
	private Date effectiveDate;
	
	private Date expiryDate;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Boolean isAllOutlet;
	
	private Boolean isAllProduct;
	
	private Boolean isAllQuotation;

	private Boolean isAllUnit;
	

	public Integer getPointToNoteId() {
		return pointToNoteId;
	}

	public void setPointToNoteId(Integer pointToNoteId) {
		this.pointToNoteId = pointToNoteId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
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
