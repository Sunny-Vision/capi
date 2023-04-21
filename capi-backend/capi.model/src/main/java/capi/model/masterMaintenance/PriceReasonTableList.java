package capi.model.masterMaintenance;


public class PriceReasonTableList {

	public Integer priceReasonId;
	
	private String description;
	
	private Integer reasonType;

	private String reasonTypeLabel;

	private Integer sequence;
	
	private Boolean isAllOutletType;
	
	private Long shortCode;

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

	public String getReasonTypeLabel() {
		return reasonTypeLabel;
	}

	public void setReasonTypeLabel(String reasonTypeLabel) {
		this.reasonTypeLabel = reasonTypeLabel;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Boolean getIsAllOutletType() {
		return isAllOutletType;
	}

	public void setIsAllOutletType(Boolean isAllOutletType) {
		this.isAllOutletType = isAllOutletType;
	}

	public Long getShortCode() {
		return shortCode;
	}

	public void setShortCode(Long shortCode) {
		this.shortCode = shortCode;
	}

}
