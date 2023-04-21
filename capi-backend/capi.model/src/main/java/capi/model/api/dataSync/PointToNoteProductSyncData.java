package capi.model.api.dataSync;

import java.util.Date;

public class PointToNoteProductSyncData {
	private Integer pointToNoteId;
	
	private Integer productId;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getPointToNoteId() {
		return pointToNoteId;
	}

	public void setPointToNoteId(Integer pointToNoteId) {
		this.pointToNoteId = pointToNoteId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
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
