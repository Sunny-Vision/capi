package capi.model.masterMaintenance;

import java.util.ArrayList;
import java.util.Date;


public class PriceReasonEditModel {

	public Integer priceReasonId;
	
	private String description;
	
	private Integer reasonType;
	
	private Integer sequence;
	
	private Boolean allOutletType;
	
	private ArrayList<String> outletTypeIds;

	private String outletTypeLabel;

	private Date createdDate;

	private Date modifiedDate;

	public Integer getPriceReasonId() {
		return priceReasonId;
	}

	public void setPriceReasonId(Integer priceReasonId) {
		this.priceReasonId = priceReasonId;
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

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Boolean getAllOutletType() {
		return allOutletType;
	}

	public void setAllOutletType(Boolean allOutletType) {
		this.allOutletType = allOutletType;
	}

	public ArrayList<String> getOutletTypeIds() {
		return outletTypeIds;
	}

	public void setOutletTypeIds(ArrayList<String> outletTypeIds) {
		this.outletTypeIds = outletTypeIds;
	}

	public String getOutletTypeLabel() {
		return outletTypeLabel;
	}

	public void setOutletTypeLabel(String outletTypeLabel) {
		this.outletTypeLabel = outletTypeLabel;
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
