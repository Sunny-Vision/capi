package capi.model.api.dataSync;

import java.util.Date;

public class PointToNoteOutletSyncData {
	private Integer pointToNoteId;
	
	private Integer outletId;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getPointToNoteId() {
		return pointToNoteId;
	}

	public void setPointToNoteId(Integer pointToNoteId) {
		this.pointToNoteId = pointToNoteId;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
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
