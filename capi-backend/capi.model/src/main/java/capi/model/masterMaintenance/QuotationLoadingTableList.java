package capi.model.masterMaintenance;


public class QuotationLoadingTableList {
	
	private Integer quotationLoadingId;

	private String district;
	
	private String outletType;
	
	private Double quotationPerManDay;
	
	
	public Integer getId() {
		return quotationLoadingId;
	}


	public Integer getQuotationLoadingId() {
		return quotationLoadingId;
	}


	public void setQuotationLoadingId(Integer quotationLoadingId) {
		this.quotationLoadingId = quotationLoadingId;
	}


	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	public String getOutletType() {
		return outletType;
	}


	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}


	public Double getQuotationPerManDay() {
		return quotationPerManDay;
	}


	public void setQuotationPerManDay(Double quotationPerManDay) {
		this.quotationPerManDay = quotationPerManDay;
	}

	
}
