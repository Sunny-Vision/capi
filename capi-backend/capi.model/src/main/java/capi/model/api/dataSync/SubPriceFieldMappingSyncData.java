package capi.model.api.dataSync;

import java.util.Date;

public class SubPriceFieldMappingSyncData {
	private Integer subPriceFieldMappingId;
	
	private Integer subPriceFieldId;
	
	private Integer subPriceTypeId;
	
	private Integer sequence;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getSubPriceFieldMappingId() {
		return subPriceFieldMappingId;
	}

	public void setSubPriceFieldMappingId(Integer subPriceFieldMappingId) {
		this.subPriceFieldMappingId = subPriceFieldMappingId;
	}

	public Integer getSubPriceFieldId() {
		return subPriceFieldId;
	}

	public void setSubPriceFieldId(Integer subPriceFieldId) {
		this.subPriceFieldId = subPriceFieldId;
	}

	public Integer getSubPriceTypeId() {
		return subPriceTypeId;
	}

	public void setSubPriceTypeId(Integer subPriceTypeId) {
		this.subPriceTypeId = subPriceTypeId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
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
