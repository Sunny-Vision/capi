package capi.model.api.dataSync;

import java.util.Date;

public class OutletTypePriceReasonSyncData {
	private Integer priceReasonId;
	
	private String shortCode;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getPriceReasonId() {
		return priceReasonId;
	}

	public void setPriceReasonId(Integer priceReasonId) {
		this.priceReasonId = priceReasonId;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
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
