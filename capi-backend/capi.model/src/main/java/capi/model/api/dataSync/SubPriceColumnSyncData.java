package capi.model.api.dataSync;

import java.util.Date;

public class SubPriceColumnSyncData {

	private Integer subPriceColumnId;
	
	private String columnValue;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer subPriceFieldMappingId;
	
	private Integer subPriceRecordId;
	
	private Integer localId;
	
	private String localDbRecordStatus;

	public Integer getSubPriceColumnId() {
		return subPriceColumnId;
	}

	public void setSubPriceColumnId(Integer subPriceColumnId) {
		this.subPriceColumnId = subPriceColumnId;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
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

	public Integer getSubPriceFieldMappingId() {
		return subPriceFieldMappingId;
	}

	public void setSubPriceFieldMappingId(Integer subPriceFieldMappingId) {
		this.subPriceFieldMappingId = subPriceFieldMappingId;
	}

	public Integer getSubPriceRecordId() {
		return subPriceRecordId;
	}

	public void setSubPriceRecordId(Integer subPriceRecordId) {
		this.subPriceRecordId = subPriceRecordId;
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
		result = prime * result + ((columnValue == null) ? 0 : columnValue.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((subPriceColumnId == null) ? 0 : subPriceColumnId.hashCode());
		result = prime * result + ((subPriceFieldMappingId == null) ? 0 : subPriceFieldMappingId.hashCode());
		result = prime * result + ((subPriceRecordId == null) ? 0 : subPriceRecordId.hashCode());
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
		SubPriceColumnSyncData other = (SubPriceColumnSyncData) obj;
		if (columnValue == null) {
			if (other.columnValue != null)
				return false;
		} else if (!columnValue.equals(other.columnValue))
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
		if (subPriceColumnId == null) {
			if (other.subPriceColumnId != null)
				return false;
		} else if (!subPriceColumnId.equals(other.subPriceColumnId))
			return false;
		if (subPriceFieldMappingId == null) {
			if (other.subPriceFieldMappingId != null)
				return false;
		} else if (!subPriceFieldMappingId.equals(other.subPriceFieldMappingId))
			return false;
		if (subPriceRecordId == null) {
			if (other.subPriceRecordId != null)
				return false;
		} else if (!subPriceRecordId.equals(other.subPriceRecordId))
			return false;
		return true;
	}
	
}
