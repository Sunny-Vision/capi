package capi.model.api.dataSync;

import java.util.Date;


public class OutletAttachmentSyncData {

	private Integer outletAttachmentId;
	
	private Integer sequence;
	
	private Integer outletId;
	
	private Integer localId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String localDbRecordStatus;

	public Integer getOutletAttachmentId() {
		return outletAttachmentId;
	}

	public void setOutletAttachmentId(Integer outletAttachmentId) {
		this.outletAttachmentId = outletAttachmentId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
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
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((outletAttachmentId == null) ? 0 : outletAttachmentId.hashCode());
		result = prime * result + ((outletId == null) ? 0 : outletId.hashCode());
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
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
		OutletAttachmentSyncData other = (OutletAttachmentSyncData) obj;
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
		if (outletAttachmentId == null) {
			if (other.outletAttachmentId != null)
				return false;
		} else if (!outletAttachmentId.equals(other.outletAttachmentId))
			return false;
		if (outletId == null) {
			if (other.outletId != null)
				return false;
		} else if (!outletId.equals(other.outletId))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}
	
}
