package capi.model.assignmentManagement;

public class QuotationTableList {
	private Integer id;
	private String purpose;
	private String unitCode;
	private String unitName;
	private String cpiBasePeriod;
	private String englishName;
	private String chineseName;
	
	private Integer productId;
	private String productAttribute1;
	private String productAttribute2;
	private String productAttribute3;
	private String productAttribute4;
	private String productAttribute5;
	
	private Integer outletId;
	private String firmName;
	private String batchCode;
	private String pricingFrequency;
	private String Status;
	private boolean isICP;
	private String indoorAllocationCode;
	private String cpiCompilationSeries;
	private String seasonalWithdrawal;
	private Double frAdmin;
	private Double frField;
	private String lastFRAppliedDate;
	private Integer seasonality;
	private String ruaDate;
	
	private String cpiQuotationType;
	
	private String formType;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getUnitCode() {
		return unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getProductAttribute1() {
		return productAttribute1;
	}
	public void setProductAttribute1(String productAttribute1) {
		this.productAttribute1 = productAttribute1;
	}
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
	public String getFirmName() {
		return firmName;
	}
	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public String getPricingFrequency() {
		return pricingFrequency;
	}
	public void setPricingFrequency(String pricingFrequency) {
		this.pricingFrequency = pricingFrequency;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public boolean isICP() {
		return isICP;
	}
	public void setICP(boolean isICP) {
		this.isICP = isICP;
	}
	public String getIndoorAllocationCode() {
		return indoorAllocationCode;
	}
	public void setIndoorAllocationCode(String indoorAllocationCode) {
		this.indoorAllocationCode = indoorAllocationCode;
	}
	public String getCpiCompilationSeries() {
		return cpiCompilationSeries;
	}
	public void setCpiCompilationSeries(String cpiCompilationSeries) {
		this.cpiCompilationSeries = cpiCompilationSeries;
	}

	public String getSeasonalWithdrawal() {
		return seasonalWithdrawal;
	}
	public void setSeasonalWithdrawal(String seasonalWithdrawal) {
		this.seasonalWithdrawal = seasonalWithdrawal;
	}
	public Double getFrAdmin() {
		return frAdmin;
	}
	public void setFrAdmin(Double frAdmin) {
		this.frAdmin = frAdmin;
	}
	public Double getFrField() {
		return frField;
	}
	public void setFrField(Double frField) {
		this.frField = frField;
	}
	public String getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}
	public void setLastFRAppliedDate(String lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
	}
	public Integer getSeasonality() {
		return seasonality;
	}
	public void setSeasonality(Integer seasonality) {
		this.seasonality = seasonality;
	}
	public String getRuaDate() {
		return ruaDate;
	}
	public void setRuaDate(String ruaDate) {
		this.ruaDate = ruaDate;
	}
	public String getProductAttribute2() {
		return productAttribute2;
	}
	public void setProductAttribute2(String productAttribute2) {
		this.productAttribute2 = productAttribute2;
	}
	public String getProductAttribute3() {
		return productAttribute3;
	}
	public void setProductAttribute3(String productAttribute3) {
		this.productAttribute3 = productAttribute3;
	}
	public String getProductAttribute4() {
		return productAttribute4;
	}
	public void setProductAttribute4(String productAttribute4) {
		this.productAttribute4 = productAttribute4;
	}
	public String getProductAttribute5() {
		return productAttribute5;
	}
	public void setProductAttribute5(String productAttribute5) {
		this.productAttribute5 = productAttribute5;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public String getChineseName() {
		return chineseName;
	}
	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}
	public String getCpiQuotationType() {
		return cpiQuotationType;
	}
	public void setCpiQuotationType(String cpiQuotationType) {
		this.cpiQuotationType = cpiQuotationType;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}
	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}
}
