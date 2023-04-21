package capi.model.api.dataSync;

import java.util.Date;

public class PriceReasonSyncData {
	private Integer priceReasonId;
	
	private Integer sequence;
	
	private boolean isAllOutletType;
	
	private String description;
	
	private Integer reasonType;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getPriceReasonId() {
		return priceReasonId;
	}

	public void setPriceReasonId(Integer priceReasonId) {
		this.priceReasonId = priceReasonId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public boolean isAllOutletType() {
		return isAllOutletType;
	}

	public void setAllOutletType(boolean isAllOutletType) {
		this.isAllOutletType = isAllOutletType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getReasonType() {
		return reasonType;
	}

	public void setReasonType(Integer reasonType) {
		this.reasonType = reasonType;
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
