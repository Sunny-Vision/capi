package capi.model.masterMaintenance;


public class QuotationLoadingEditModel {
	
	private Integer quotationLoadingId;

	private Integer districtId;	
	
	private String districtLabel;	
	
	private Double quotationPerManDay;

	private String outletTypeId;	
	
	private String createdDate;
	
	private String modifiedDate;
	
	public Integer getId() {
		return quotationLoadingId;
	}

	public Integer getQuotationLoadingId() {
		return quotationLoadingId;
	}

	public void setQuotationLoadingId(Integer quotationLoadingId) {
		this.quotationLoadingId = quotationLoadingId;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	
	public String getDistrictLabel() {
		return districtLabel;
	}

	public void setDistrictLabel(String districtLabel) {
		this.districtLabel = districtLabel;
	}

	public Double getQuotationPerManDay() {
		return quotationPerManDay;
	}

	public void setQuotationPerManDay(Double quotationPerManDay) {
		this.quotationPerManDay = quotationPerManDay;
	}

	public String getOutletTypeId() {
		return outletTypeId;
	}

	public void setOutletTypeId(String outletTypeId) {
		this.outletTypeId = outletTypeId;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


}
