package capi.model.report;

public class SummaryOfWorkload {
	
	private Integer id;

	private Integer districtId;

	private String districtCode;

	private String outletTypeCode;

	private String outletTypeName;

	private Double spent;
	
	private Integer countQuotationRecordId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public String getOutletTypeName() {
		return outletTypeName;
	}

	public void setOutletTypeName(String outletTypeName) {
		this.outletTypeName = outletTypeName;
	}

	public Double getSpent() {
		return spent;
	}

	public void setSpent(Double spent) {
		this.spent = spent;
	}

	public Integer getCountQuotationRecordId() {
		return countQuotationRecordId;
	}

	public void setCountQuotationRecordId(Integer countQuotationRecordId) {
		this.countQuotationRecordId = countQuotationRecordId;
	}	

}
