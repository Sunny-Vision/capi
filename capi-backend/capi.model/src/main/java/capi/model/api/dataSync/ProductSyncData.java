package capi.model.api.dataSync;

import java.util.Date;

public class ProductSyncData {

	private Integer productId;
	
	private String countryOfOrigin;
	
	private Integer productGroupId;
	
	private Date photo1ModifiedTime;
	
	private Date photo2ModifiedTime;
	
	private String remark;
	
	private String status;
	
	private boolean reviewed;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String barcode;
	
	private Date effectiveDate;
	
	private Date obsoleteDate;

	private Integer localId;
	
	private String localDbRecordStatus;
	
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

	public Integer getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(Integer productGroupId) {
		this.productGroupId = productGroupId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
		result = prime * result + ((countryOfOrigin == null) ? 0 : countryOfOrigin.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((obsoleteDate == null) ? 0 : obsoleteDate.hashCode());
		result = prime * result + ((photo1ModifiedTime == null) ? 0 : photo1ModifiedTime.hashCode());
		result = prime * result + ((photo2ModifiedTime == null) ? 0 : photo2ModifiedTime.hashCode());
		result = prime * result + ((productGroupId == null) ? 0 : productGroupId.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + (reviewed ? 1231 : 1237);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductSyncData other = (ProductSyncData) obj;
		if (barcode == null) {
			if (other.barcode != null)
				return false;
		} else if (!barcode.equals(other.barcode))
			return false;
		if (countryOfOrigin == null) {
			if (other.countryOfOrigin != null)
				return false;
		} else if (!countryOfOrigin.equals(other.countryOfOrigin))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (effectiveDate == null) {
			if (other.effectiveDate != null)
				return false;
		} else if (!effectiveDate.equals(other.effectiveDate))
			return false;
		if (localDbRecordStatus == null) {
			if (other.localDbRecordStatus != null)
				return false;
		} else if (!localDbRecordStatus.equals(other.localDbRecordStatus))
			return false;
		if (localId == null) {
			if (other.localId != null)
				return false;
		} else if (!localId.equals(other.localId))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (obsoleteDate == null) {
			if (other.obsoleteDate != null)
				return false;
		} else if (!obsoleteDate.equals(other.obsoleteDate))
			return false;
		if (photo1ModifiedTime == null) {
			if (other.photo1ModifiedTime != null)
				return false;
		} else if (!photo1ModifiedTime.equals(other.photo1ModifiedTime))
			return false;
		if (photo2ModifiedTime == null) {
			if (other.photo2ModifiedTime != null)
				return false;
		} else if (!photo2ModifiedTime.equals(other.photo2ModifiedTime))
			return false;
		if (productGroupId == null) {
			if (other.productGroupId != null)
				return false;
		} else if (!productGroupId.equals(other.productGroupId))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (reviewed != other.reviewed)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	
}
