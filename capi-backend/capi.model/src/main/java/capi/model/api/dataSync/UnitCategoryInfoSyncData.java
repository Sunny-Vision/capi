package capi.model.api.dataSync;

import java.util.Date;

public class UnitCategoryInfoSyncData {

	private Integer unitCategoryInfoId;
	
	private Integer outletId;
	
	private String contactPerson;
	
	private String remark;
	
	private String unitCategory;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer sequence;
	
	private Integer localId;
	
	private String localDbRecordStatus;

	public Integer getUnitCategoryInfoId() {
		return unitCategoryInfoId;
	}

	public void setUnitCategoryInfoId(Integer unitCategoryInfoId) {
		this.unitCategoryInfoId = unitCategoryInfoId;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUnitCategory() {
		return unitCategory;
	}

	public void setUnitCategory(String unitCategory) {
		this.unitCategory = unitCategory;
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

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
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
		result = prime * result + ((contactPerson == null) ? 0 : contactPerson.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((outletId == null) ? 0 : outletId.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result + ((unitCategory == null) ? 0 : unitCategory.hashCode());
		result = prime * result + ((unitCategoryInfoId == null) ? 0 : unitCategoryInfoId.hashCode());
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
		UnitCategoryInfoSyncData other = (UnitCategoryInfoSyncData) obj;
		if (contactPerson == null) {
			if (other.contactPerson != null)
				return false;
		} else if (!contactPerson.equals(other.contactPerson))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
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
		if (outletId == null) {
			if (other.outletId != null)
				return false;
		} else if (!outletId.equals(other.outletId))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		if (unitCategory == null) {
			if (other.unitCategory != null)
				return false;
		} else if (!unitCategory.equals(other.unitCategory))
			return false;
		if (unitCategoryInfoId == null) {
			if (other.unitCategoryInfoId != null)
				return false;
		} else if (!unitCategoryInfoId.equals(other.unitCategoryInfoId))
			return false;
		return true;
	}
	
}
