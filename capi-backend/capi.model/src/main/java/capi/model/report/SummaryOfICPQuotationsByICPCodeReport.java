package capi.model.report;

public class SummaryOfICPQuotationsByICPCodeReport {

	private String icpType;
	
	private String icpProductCode;
	
	private String icpProductName;
	
	private Long icpOutletCount;
	
	private Long icpQuotationCount;
	
	private Long cpiOutletCount;
	
	private Long cpiQuotationCount;
	
	private String purpose;

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

	public Long getIcpOutletCount() {
		return icpOutletCount;
	}

	public void setIcpOutletCount(Long icpOutletCount) {
		this.icpOutletCount = icpOutletCount;
	}

	public Long getIcpQuotationCount() {
		return icpQuotationCount;
	}

	public void setIcpQuotationCount(Long icpQuotationCount) {
		this.icpQuotationCount = icpQuotationCount;
	}

	public Long getCpiOutletCount() {
		return cpiOutletCount;
	}

	public void setCpiOutletCount(Long cpiOutletCount) {
		this.cpiOutletCount = cpiOutletCount;
	}

	public Long getCpiQuotationCount() {
		return cpiQuotationCount;
	}

	public void setCpiQuotationCount(Long cpiQuotationCount) {
		this.cpiQuotationCount = cpiQuotationCount;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

}
