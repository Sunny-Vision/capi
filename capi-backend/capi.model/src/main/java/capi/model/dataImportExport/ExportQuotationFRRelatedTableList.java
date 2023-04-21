package capi.model.dataImportExport;

import java.util.Date;

public class ExportQuotationFRRelatedTableList {
	
	private Integer quotationId;
	
	private Integer firmCode;
	
	private String firmName;
	
	private String purposeCode;
	
	private String unitCode;
	
	private String cpiBasePeriod;
	
	private String unitEnglishName;
	
	private String unitChineseName;
	
	private Integer seasonality;
	
	private String pricingFrequencyName;
	
	private boolean isICP;
	
	private String icpProductCode;
	
	private String icpProductName;
	
	private String icpType;
	
	private String quotationStatus;
	
	private boolean isFRRequired;
	
	private boolean isReturnGoods;
	
	private boolean isReturnNewGoods;
	
	private boolean lastSeasonReturnGoods;
	
	private boolean isFRApplied;
	
	private Boolean isUseFRAdmin;
	
	private Double usedFRValue;
	
	private Boolean isUsedFRPercentage;
	
	private Date lastFRAppliedDate;
	
	private Double frAdmin;
	
	private Boolean isFRAdminPercentage;
	
	private Date seasonalWithdrawal;

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public Integer getFirmCode() {
		return firmCode;
	}

	public void setFirmCode(Integer firmCode) {
		this.firmCode = firmCode;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getPurposeCode() {
		return purposeCode;
	}

	public void setPurposeCode(String purposeCode) {
		this.purposeCode = purposeCode;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public String getUnitEnglishName() {
		return unitEnglishName;
	}

	public void setUnitEnglishName(String unitEnglishName) {
		this.unitEnglishName = unitEnglishName;
	}

	public String getUnitChineseName() {
		return unitChineseName;
	}

	public void setUnitChineseName(String unitChineseName) {
		this.unitChineseName = unitChineseName;
	}

	public Integer getSeasonality() {
		return seasonality;
	}

	public void setSeasonality(Integer seasonality) {
		this.seasonality = seasonality;
	}

	public String getPricingFrequencyName() {
		return pricingFrequencyName;
	}

	public void setPricingFrequencyName(String pricingFrequencyName) {
		this.pricingFrequencyName = pricingFrequencyName;
	}

	public boolean isICP() {
		return isICP;
	}

	public void setICP(boolean isICP) {
		this.isICP = isICP;
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

	public String getIcpType() {
		return icpType;
	}

	public void setIcpType(String icpType) {
		this.icpType = icpType;
	}

	public String getQuotationStatus() {
		return quotationStatus;
	}

	public void setQuotationStatus(String quotationStatus) {
		this.quotationStatus = quotationStatus;
	}

	public boolean isFRRequired() {
		return isFRRequired;
	}

	public void setFRRequired(boolean isFRRequired) {
		this.isFRRequired = isFRRequired;
	}

	public boolean isReturnGoods() {
		return isReturnGoods;
	}

	public void setReturnGoods(boolean isReturnGoods) {
		this.isReturnGoods = isReturnGoods;
	}

	public boolean isReturnNewGoods() {
		return isReturnNewGoods;
	}

	public void setReturnNewGoods(boolean isReturnNewGoods) {
		this.isReturnNewGoods = isReturnNewGoods;
	}

	public boolean isLastSeasonReturnGoods() {
		return lastSeasonReturnGoods;
	}

	public void setLastSeasonReturnGoods(boolean lastSeasonReturnGoods) {
		this.lastSeasonReturnGoods = lastSeasonReturnGoods;
	}

	public boolean isFRApplied() {
		return isFRApplied;
	}

	public void setFRApplied(boolean isFRApplied) {
		this.isFRApplied = isFRApplied;
	}

	public Boolean getIsUseFRAdmin() {
		return isUseFRAdmin;
	}

	public void setIsUseFRAdmin(Boolean isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}

	public Double getUsedFRValue() {
		return usedFRValue;
	}

	public void setUsedFRValue(Double usedFRValue) {
		this.usedFRValue = usedFRValue;
	}

	public Boolean getIsUsedFRPercentage() {
		return isUsedFRPercentage;
	}

	public void setIsUsedFRPercentage(Boolean isUsedFRPercentage) {
		this.isUsedFRPercentage = isUsedFRPercentage;
	}

	public Date getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}

	public void setLastFRAppliedDate(Date lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
	}

	public Double getFrAdmin() {
		return frAdmin;
	}

	public void setFrAdmin(Double frAdmin) {
		this.frAdmin = frAdmin;
	}

	public Boolean getIsFRAdminPercentage() {
		return isFRAdminPercentage;
	}

	public void setIsFRAdminPercentage(Boolean isFRAdminPercentage) {
		this.isFRAdminPercentage = isFRAdminPercentage;
	}

	public Date getSeasonalWithdrawal() {
		return seasonalWithdrawal;
	}

	public void setSeasonalWithdrawal(Date seasonalWithdrawal) {
		this.seasonalWithdrawal = seasonalWithdrawal;
	}

}
