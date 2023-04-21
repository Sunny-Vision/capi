package capi.model.report;

import java.util.Date;

public class ProductCycleReportByVariety {

	private String productGroup;
	private String varietyCode;
	private String varietyChineseName;
	private String varietyEnglishName;
	private Integer noOfQuotations;
	private Integer productChange;
	private Integer totalNoOfPricingMonth;

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getVarietyCode() {
		return varietyCode;
	}

	public void setVarietyCode(String varietyCode) {
		this.varietyCode = varietyCode;
	}

	public String getVarietyChineseName() {
		return varietyChineseName;
	}

	public void setVarietyChineseName(String varietyChineseName) {
		this.varietyChineseName = varietyChineseName;
	}

	public String getVarietyEnglishName() {
		return varietyEnglishName;
	}

	public void setVarietyEnglishName(String varietyEnglishName) {
		this.varietyEnglishName = varietyEnglishName;
	}

	public Integer getNoOfQuotations() {
		return noOfQuotations;
	}

	public void setNoOfQuotations(Integer noOfQuotations) {
		this.noOfQuotations = noOfQuotations;
	}

	public Integer getProductChange() {
		return productChange;
	}

	public void setProductChange(Integer productChange) {
		this.productChange = productChange;
	}

	public Integer getTotalNoOfPricingMonth() {
		return totalNoOfPricingMonth;
	}

	public void setTotalNoOfPricingMonth(Integer totalNoOfPricingMonth) {
		this.totalNoOfPricingMonth = totalNoOfPricingMonth;
	}

}
