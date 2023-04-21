package capi.model.api.dataSync;

import java.util.Date;

public class UOMConversionSyncData {
	private Integer uomConversionId;
	
	private Integer baseUOMId;
	
	private Integer targetUOMId;
	
	private Double factor;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getUomConversionId() {
		return uomConversionId;
	}

	public void setUomConversionId(Integer uomConversionId) {
		this.uomConversionId = uomConversionId;
	}

	public Integer getBaseUOMId() {
		return baseUOMId;
	}

	public void setBaseUOMId(Integer baseUOMId) {
		this.baseUOMId = baseUOMId;
	}

	public Integer getTargetUOMId() {
		return targetUOMId;
	}

	public void setTargetUOMId(Integer targetUOMId) {
		this.targetUOMId = targetUOMId;
	}

	public Double getFactor() {
		return factor;
	}

	public void setFactor(Double factor) {
		this.factor = factor;
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
