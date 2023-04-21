package capi.model.api.dataSync;

import java.util.Date;

public class UnitSyncData {
	private Integer unitId;
	
	private String code;
	
	private String chineseName;
	
	private String englishName;
	
	private Integer subItemId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Date obsoleteDate;
	
	private Date effectiveDate;
	
	private String displayName;
	
	private boolean isMRPS;
	
	private Integer purposeId;
	
	private Integer maxQuotation;
	
	private Integer minQuotation;
	
	private String unitCategory;
	
	private Integer standardUOMId;
	
	private Double uomValue;
	
	private Integer productCategoryId;
	
	private Integer subPriceTypeId;
	
	private boolean spicingRequired;
	
	private boolean frRequired;
	
	private Integer seasonality;
	
	private Integer seasonStartMonth;
	
	private Integer seasonEndMonth;
	
	private Integer pricingFrequencyId;
	
	private Integer rtnPeriod;
	
	private boolean backdateRequired;
	
	private boolean allowEditPMPrice;
	
	private boolean ruaAllowed;
	
	private boolean isFreshItem;
	
	private boolean allowProductChange;
	
	private Integer formDisplay;
	
	private Integer productCycle;
	
	private String status;
	
	private String cpiBasePeriod;
	
	private String crossCheckGroup;
	
	private Integer cpiQoutationType;
	
	private boolean isTemporary;
	
	private boolean isNPriceMandatory;
	
	private boolean isSPriceMandatory;
	
	private String dataTransmissionRule;
	
	private Double consolidatedSPRMean;
	
	private Double consolidatedSPRSD;
	
	private Double consolidatedNPRMean;
	
	private Double consolidatedNPRSD;

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Integer getSubItemId() {
		return subItemId;
	}

	public void setSubItemId(Integer subItemId) {
		this.subItemId = subItemId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getObsoleteDate() {
		return obsoleteDate;
	}

	public void setObsoleteDate(Date obsoleteDate) {
		this.obsoleteDate = obsoleteDate;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isMRPS() {
		return isMRPS;
	}

	public void setMRPS(boolean isMRPS) {
		this.isMRPS = isMRPS;
	}

	public Integer getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}

	public Integer getMaxQuotation() {
		return maxQuotation;
	}

	public void setMaxQuotation(Integer maxQuotation) {
		this.maxQuotation = maxQuotation;
	}

	public Integer getMinQuotation() {
		return minQuotation;
	}

	public void setMinQuotation(Integer minQuotation) {
		this.minQuotation = minQuotation;
	}

	public String getUnitCategory() {
		return unitCategory;
	}

	public void setUnitCategory(String unitCategory) {
		this.unitCategory = unitCategory;
	}

	public Integer getStandardUOMId() {
		return standardUOMId;
	}

	public void setStandardUOMId(Integer standardUOMId) {
		this.standardUOMId = standardUOMId;
	}

	public Double getUomValue() {
		return uomValue;
	}

	public void setUomValue(Double uomValue) {
		this.uomValue = uomValue;
	}

	public Integer getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(Integer productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	public Integer getSubPriceTypeId() {
		return subPriceTypeId;
	}

	public void setSubPriceTypeId(Integer subPriceTypeId) {
		this.subPriceTypeId = subPriceTypeId;
	}

	public boolean isSpicingRequired() {
		return spicingRequired;
	}

	public void setSpicingRequired(boolean spicingRequired) {
		this.spicingRequired = spicingRequired;
	}

	public boolean isFrRequired() {
		return frRequired;
	}

	public void setFrRequired(boolean frRequired) {
		this.frRequired = frRequired;
	}

	public Integer getSeasonality() {
		return seasonality;
	}

	public void setSeasonality(Integer seasonality) {
		this.seasonality = seasonality;
	}

	public Integer getSeasonStartMonth() {
		return seasonStartMonth;
	}

	public void setSeasonStartMonth(Integer seasonStartMonth) {
		this.seasonStartMonth = seasonStartMonth;
	}

	public Integer getSeasonEndMonth() {
		return seasonEndMonth;
	}

	public void setSeasonEndMonth(Integer seasonEndMonth) {
		this.seasonEndMonth = seasonEndMonth;
	}

	public Integer getPricingFrequencyId() {
		return pricingFrequencyId;
	}

	public void setPricingFrequencyId(Integer pricingFrequencyId) {
		this.pricingFrequencyId = pricingFrequencyId;
	}

	public Integer getRtnPeriod() {
		return rtnPeriod;
	}

	public void setRtnPeriod(Integer rtnPeriod) {
		this.rtnPeriod = rtnPeriod;
	}

	public boolean isBackdateRequired() {
		return backdateRequired;
	}

	public void setBackdateRequired(boolean backdateRequired) {
		this.backdateRequired = backdateRequired;
	}

	public boolean isAllowEditPMPrice() {
		return allowEditPMPrice;
	}

	public void setAllowEditPMPrice(boolean allowEditPMPrice) {
		this.allowEditPMPrice = allowEditPMPrice;
	}

	public boolean isRuaAllowed() {
		return ruaAllowed;
	}

	public void setRuaAllowed(boolean ruaAllowed) {
		this.ruaAllowed = ruaAllowed;
	}

	public boolean isFreshItem() {
		return isFreshItem;
	}

	public void setFreshItem(boolean isFreshItem) {
		this.isFreshItem = isFreshItem;
	}

	public boolean isAllowProductChange() {
		return allowProductChange;
	}

	public void setAllowProductChange(boolean allowProductChange) {
		this.allowProductChange = allowProductChange;
	}

	public Integer getFormDisplay() {
		return formDisplay;
	}

	public void setFormDisplay(Integer formDisplay) {
		this.formDisplay = formDisplay;
	}

	public Integer getProductCycle() {
		return productCycle;
	}

	public void setProductCycle(Integer productCycle) {
		this.productCycle = productCycle;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public String getCrossCheckGroup() {
		return crossCheckGroup;
	}

	public void setCrossCheckGroup(String crossCheckGroup) {
		this.crossCheckGroup = crossCheckGroup;
	}

	public Integer getCpiQoutationType() {
		return cpiQoutationType;
	}

	public void setCpiQoutationType(Integer cpiQoutationType) {
		this.cpiQoutationType = cpiQoutationType;
	}

	public boolean isTemporary() {
		return isTemporary;
	}

	public void setTemporary(boolean isTemporary) {
		this.isTemporary = isTemporary;
	}

	public boolean isNPriceMandatory() {
		return isNPriceMandatory;
	}

	public void setNPriceMandatory(boolean isNPriceMandatory) {
		this.isNPriceMandatory = isNPriceMandatory;
	}

	public boolean isSPriceMandatory() {
		return isSPriceMandatory;
	}

	public void setSPriceMandatory(boolean isSPriceMandatory) {
		this.isSPriceMandatory = isSPriceMandatory;
	}

	public String getDataTransmissionRule() {
		return dataTransmissionRule;
	}

	public void setDataTransmissionRule(String dataTransmissionRule) {
		this.dataTransmissionRule = dataTransmissionRule;
	}

	public Double getConsolidatedSPRMean() {
		return consolidatedSPRMean;
	}

	public void setConsolidatedSPRMean(Double consolidatedSPRMean) {
		this.consolidatedSPRMean = consolidatedSPRMean;
	}

	public Double getConsolidatedSPRSD() {
		return consolidatedSPRSD;
	}

	public void setConsolidatedSPRSD(Double consolidatedSPRSD) {
		this.consolidatedSPRSD = consolidatedSPRSD;
	}

	public Double getConsolidatedNPRMean() {
		return consolidatedNPRMean;
	}

	public void setConsolidatedNPRMean(Double consolidatedNPRMean) {
		this.consolidatedNPRMean = consolidatedNPRMean;
	}

	public Double getConsolidatedNPRSD() {
		return consolidatedNPRSD;
	}

	public void setConsolidatedNPRSD(Double consolidatedNPRSD) {
		this.consolidatedNPRSD = consolidatedNPRSD;
	}
	
	
}
