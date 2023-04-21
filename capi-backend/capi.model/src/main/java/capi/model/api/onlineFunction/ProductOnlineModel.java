package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.ProductSpecificationSyncData;

public class ProductOnlineModel {

	private Integer productId;
	
	private String countryOfOrigin;
	
	private Date photo1ModifiedTime;
	
	private Date photo2ModifiedTime;
	
	private String remark;
	
	private String status;
	
	private boolean reviewed;
	
	private String barcode;
	
	private Date effectiveDate;
	
	private Date obsoleteDate;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer localId;
	
	private String localDbRecordStatus;
	
	//Foreign Key
	private Integer productGroupId;
	
	//Foreign Model
	private List<ProductSpecificationSyncData> productSpecifications;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public Date getPhoto1ModifiedTime() {
		return photo1ModifiedTime;
	}

	public void setPhoto1ModifiedTime(Date photo1ModifiedTime) {
		this.photo1ModifiedTime = photo1ModifiedTime;
	}

	public Date getPhoto2ModifiedTime() {
		return photo2ModifiedTime;
	}

	public void setPhoto2ModifiedTime(Date photo2ModifiedTime) {
		this.photo2ModifiedTime = photo2ModifiedTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getObsoleteDate() {
		return obsoleteDate;
	}

	public void setObsoleteDate(Date obsoleteDate) {
		this.obsoleteDate = obsoleteDate;
	}

	public Integer getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(Integer productGroupId) {
		this.productGroupId = productGroupId;
	}

	public List<ProductSpecificationSyncData> getProductSpecifications() {
		return productSpecifications;
	}

	public void setProductSpecifications(List<ProductSpecificationSyncData> productSpecifications) {
		this.productSpecifications = productSpecifications;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
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
	
}
