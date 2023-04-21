package capi.model.api.dataSync;

import java.util.Date;


public class OutletAttachmentZipImageSyncData {

	private Integer outletAttachmentId;
	
	private Integer sequence;
	
	private Integer outletId;
	
	private Integer localId;
	
	private Date createdDate;
	
	private Date modifiedDate;

	private String path;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

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
	
	
}
