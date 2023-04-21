package capi.model.api.dataSync;

import java.util.Date;

public class UpdateOutletImageModel {

	private Integer outletId;
	
	private Date modifiedDate;

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
}
