package capi.model.api.dataSync;

import java.util.Date;

public class OutletUnitStatisticSyncData {
	private Integer outletId;
	
	private Integer unitId;
	
	private Integer quotationCnt;
	
	private Integer outletUnitStatisticId;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public Integer getQuotationCnt() {
		return quotationCnt;
	}

	public void setQuotationCnt(Integer quotationCnt) {
		this.quotationCnt = quotationCnt;
	}

	public Integer getOutletUnitStatisticId() {
		return outletUnitStatisticId;
	}

	public void setOutletUnitStatisticId(Integer outletUnitStatisticId) {
		this.outletUnitStatisticId = outletUnitStatisticId;
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
