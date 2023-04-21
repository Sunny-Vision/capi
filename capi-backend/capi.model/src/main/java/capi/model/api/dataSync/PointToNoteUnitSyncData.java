package capi.model.api.dataSync;

import java.util.Date;

public class PointToNoteUnitSyncData {
	private Integer pointToNoteId;
	
	private Integer unitId;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getPointToNoteId() {
		return pointToNoteId;
	}

	public void setPointToNoteId(Integer pointToNoteId) {
		this.pointToNoteId = pointToNoteId;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
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
