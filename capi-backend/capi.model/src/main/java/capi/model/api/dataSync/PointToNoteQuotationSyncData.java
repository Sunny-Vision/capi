package capi.model.api.dataSync;

import java.util.Date;

public class PointToNoteQuotationSyncData {
	private Integer pointToNoteId;
	
	private Integer quotationId;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getPointToNoteId() {
		return pointToNoteId;
	}

	public void setPointToNoteId(Integer pointToNoteId) {
		this.pointToNoteId = pointToNoteId;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
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
