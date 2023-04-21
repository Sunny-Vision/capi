package capi.model.report;

public class SummaryOfICPQuotationsByICPTypeReport {

	private String icpType;
	
	private String icpProductCode;
	
	private String icpProductName;
	
	private Double preferredQuantity;
	
	private String preferredUOM;
	
	private Integer numOfQuotation;
	
	private Double averagePrice;
	
	private Double standardDeviation;
	
	private Double cv;
	
	private Double minPrice;
	
	private Double maxPrice;
	
	private Double radio;

	public String getIcpType() {
		return icpType;
	}

	public void setIcpType(String icpType) {
		this.icpType = icpType;
	}

	public String getIcpProductCode() {
		return icpProductCode;
	}

	public void setIcpProductCode(String icpProductCode) {
		this.icpProductCode = icpProductCode;
	}

	public String getIcpProductName() {
		return icpProductName;
	}

	public void setIcpProductName(String icpProductName) {
		this.icpProductName = icpProductName;
	}

	public Double getPreferredQuantity() {
		return preferredQuantity;
	}

	public void setPreferredQuantity(Double preferredQuantity) {
		this.preferredQuantity = preferredQuantity;
	}

	public String getPreferredUOM() {
		return preferredUOM;
	}

	public void setPreferredUOM(String preferredUOM) {
		this.preferredUOM = preferredUOM;
	}

	public Integer getNumOfQuotation() {
		return numOfQuotation;
	}

	public void setNumOfQuotation(Integer numOfQuotation) {
		this.numOfQuotation = numOfQuotation;
	}

	public Double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(Double averagePrice) {
		this.averagePrice = averagePrice;
	}

	public Double getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(Double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public Double getCv() {
		return cv;
	}

	public void setCv(Double cv) {
		this.cv = cv;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Double getRadio() {
		return radio;
	}

	public void setRadio(Double radio) {
		this.radio = radio;
	}
	
}
