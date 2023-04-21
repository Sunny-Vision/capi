package capi.model.report;

public class SummaryOfQuotations {
	
	private String id;

	private String referenceMonth;

	private String purpose;
	
	private Integer districtId;
	
	private String districtCode;
	
	private String outletTypeCode;
	
	private String outletTypeName;
	
	private Integer quantity;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getOutletTypeCode() {
		return outletTypeCode;
	}

	public void setOutletType(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public String getOutletTypeName() {
		return outletTypeName;
	}

	public void setOutletTypeName(String outletTypeName) {
		this.outletTypeName = outletTypeName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
