package capi.model.masterMaintenance;

import java.util.List;

public class UnitEditModel {
	private Integer id;
	
	private String sectionCode;
	private String sectionChineseName;
	private String sectionEnglishName;
	
	private String groupCode;
	private String groupChineseName;
	private String groupEnglishName;
	
	private String subGroupCode;
	private String subGroupChineseName;
	private String subGroupEnglishName;
	
	private String itemCode;
	private String itemChineseName;
	private String itemEnglishName;
	
	private String outletTypeCode;
	private String outletTypeChineseName;
	private String outletTypeEnglishName;
	
	private String subItemCode;
	private String subItemChineseName;
	private String subItemEnglishName;

	private boolean isMRPS;
	private Integer purposeId;
	
	private String code;
	private String chineseName;
	private String englishName;
	private String displayName;
	private Integer maxQuotation;
	private Integer minQuotation;
	
	private String unitCategory;
	private Integer standardUOMId;
	private Double uomValue;
	private Integer productCategoryId;
	private Integer subPriceTypeId;
	private String obsoleteDate;
	
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
	
	private String effectiveDate;
	private boolean isFreshItem;
	private boolean allowProductChange;
	
	private Integer formDisplay;
	private Integer productCycle;
	private String Status;
	
	private String cpiBasePeriod;
	private String crossCheckGroup;
	private Integer cpiQoutationType;
	
	private boolean isNPriceMandatory;
	private boolean isSPriceMandatory;
	
	private Integer subItemCompilationMethod;
	
	private String dataTransmissionRule;
	
	private List<Integer> uomCategoryIds;
	
	// onspot validation
	private boolean isUom1Reported;
	private boolean isUom2GreaterZero;
	private boolean isNPriceGreaterZero;
	private boolean isSPriceGreaterZero;
	
	private boolean provideReasonPRNPrice;
	private Double prNPriceThreshold;
	
	private boolean provideReasonPRSPrice;
	private Double prSPriceThreshold;
	
	private boolean provideReasonSPriceMaxMin;
	private boolean provideReasonNPriceMaxMin;
	private boolean nPriceGreaterSPrice;
	private boolean provideRemarkForNotSuitablePrice;
	private boolean reminderForPricingCycle;
	
	private boolean provideReasonPRSPriceSD;
	private boolean provideReasonPRNPriceSD;
	private Double prSPriceSDPositive;
	private Double prSPriceSDNegative;
	private Double prNPriceSDPositive;
	private Double prNPriceSDNegative;
	private Integer prSPriceMonth;
	private Integer prNPriceMonth;
	
	private boolean provideReasonSPriceSD;
	private boolean provideReasonNPriceSD;
	private Double sPriceSDPositive;
	private Double sPriceSDNegative;
	private Double nPriceSDPositive;
	private Double nPriceSDNegative;
	private Integer sPriceMonth;
	private Integer nPriceMonth;
	
	private boolean provideReasonPRNPriceLower;
	private Double prNPriceLowerThreshold;
	private boolean provideReasonPRSPriceLower;
	private Double prSPriceLowerThreshold;
	
	private String displayCreatedDate;
	private String displayModifiedDate;
	
	private boolean provideRemarkForNotAvailableQuotation;
	
	private boolean convertAfterClosingDate;
		
	private String icpType;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSectionCode() {
		return sectionCode;
	}
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	public String getSectionChineseName() {
		return sectionChineseName;
	}
	public void setSectionChineseName(String sectionChineseName) {
		this.sectionChineseName = sectionChineseName;
	}
	public String getSectionEnglishName() {
		return sectionEnglishName;
	}
	public void setSectionEnglishName(String sectionEnglishName) {
		this.sectionEnglishName = sectionEnglishName;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getGroupChineseName() {
		return groupChineseName;
	}
	public void setGroupChineseName(String groupChineseName) {
		this.groupChineseName = groupChineseName;
	}
	public String getGroupEnglishName() {
		return groupEnglishName;
	}
	public void setGroupEnglishName(String groupEnglishName) {
		this.groupEnglishName = groupEnglishName;
	}
	public String getSubGroupCode() {
		return subGroupCode;
	}
	public void setSubGroupCode(String subGroupCode) {
		this.subGroupCode = subGroupCode;
	}
	public String getSubGroupChineseName() {
		return subGroupChineseName;
	}
	public void setSubGroupChineseName(String subGroupChineseName) {
		this.subGroupChineseName = subGroupChineseName;
	}
	public String getSubGroupEnglishName() {
		return subGroupEnglishName;
	}
	public void setSubGroupEnglishName(String subGroupEnglishName) {
		this.subGroupEnglishName = subGroupEnglishName;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemChineseName() {
		return itemChineseName;
	}
	public void setItemChineseName(String itemChineseName) {
		this.itemChineseName = itemChineseName;
	}
	public String getItemEnglishName() {
		return itemEnglishName;
	}
	public void setItemEnglishName(String itemEnglishName) {
		this.itemEnglishName = itemEnglishName;
	}
	public String getOutletTypeCode() {
		return outletTypeCode;
	}
	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}
	public String getOutletTypeChineseName() {
		return outletTypeChineseName;
	}
	public void setOutletTypeChineseName(String outletTypeChineseName) {
		this.outletTypeChineseName = outletTypeChineseName;
	}
	public String getOutletTypeEnglishName() {
		return outletTypeEnglishName;
	}
	public void setOutletTypeEnglishName(String outletTypeEnglishName) {
		this.outletTypeEnglishName = outletTypeEnglishName;
	}
	public String getSubItemCode() {
		return subItemCode;
	}
	public void setSubItemCode(String subItemCode) {
		this.subItemCode = subItemCode;
	}
	public String getSubItemChineseName() {
		return subItemChineseName;
	}
	public void setSubItemChineseName(String subItemChineseName) {
		this.subItemChineseName = subItemChineseName;
	}
	public String getSubItemEnglishName() {
		return subItemEnglishName;
	}
	public void setSubItemEnglishName(String subItemEnglishName) {
		this.subItemEnglishName = subItemEnglishName;
	}
	public Integer getPurposeId() {
		return purposeId;
	}
	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
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
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	public String getObsoleteDate() {
		return obsoleteDate;
	}
	public void setObsoleteDate(String obsoleteDate) {
		this.obsoleteDate = obsoleteDate;
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
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
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
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
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
	public Integer getSubItemCompilationMethod() {
		return subItemCompilationMethod;
	}
	public void setSubItemCompilationMethod(Integer subItemCompilationMethod) {
		this.subItemCompilationMethod = subItemCompilationMethod;
	}
	public boolean isUom1Reported() {
		return isUom1Reported;
	}
	public void setUom1Reported(boolean isUom1Reported) {
		this.isUom1Reported = isUom1Reported;
	}
	public boolean isMRPS() {
		return isMRPS;
	}
	public void setMRPS(boolean isMRPS) {
		this.isMRPS = isMRPS;
	}
	public List<Integer> getUomCategoryIds() {
		return uomCategoryIds;
	}
	public void setUomCategoryIds(List<Integer> uomCategoryIds) {
		this.uomCategoryIds = uomCategoryIds;
	}
	public boolean isUom2GreaterZero() {
		return isUom2GreaterZero;
	}
	public void setUom2GreaterZero(boolean isUom2GreaterZero) {
		this.isUom2GreaterZero = isUom2GreaterZero;
	}
	public boolean isNPriceGreaterZero() {
		return isNPriceGreaterZero;
	}
	public void setNPriceGreaterZero(boolean isNPriceGreaterZero) {
		this.isNPriceGreaterZero = isNPriceGreaterZero;
	}
	public boolean isSPriceGreaterZero() {
		return isSPriceGreaterZero;
	}
	public void setSPriceGreaterZero(boolean isSPriceGreaterZero) {
		this.isSPriceGreaterZero = isSPriceGreaterZero;
	}
	public boolean isProvideReasonPRNPrice() {
		return provideReasonPRNPrice;
	}
	public void setProvideReasonPRNPrice(boolean provideReasonPRNPrice) {
		this.provideReasonPRNPrice = provideReasonPRNPrice;
	}
	public Double getPrNPriceThreshold() {
		return prNPriceThreshold;
	}
	public void setPrNPriceThreshold(Double prNPriceThreshold) {
		this.prNPriceThreshold = prNPriceThreshold;
	}
	public boolean isProvideReasonPRSPrice() {
		return provideReasonPRSPrice;
	}
	public void setProvideReasonPRSPrice(boolean provideReasonPRSPrice) {
		this.provideReasonPRSPrice = provideReasonPRSPrice;
	}
	public Double getPrSPriceThreshold() {
		return prSPriceThreshold;
	}
	public void setPrSPriceThreshold(Double prSPriceThreshold) {
		this.prSPriceThreshold = prSPriceThreshold;
	}
	public boolean isProvideReasonSPriceMaxMin() {
		return provideReasonSPriceMaxMin;
	}
	public void setProvideReasonSPriceMaxMin(boolean provideReasonSPriceMaxMin) {
		this.provideReasonSPriceMaxMin = provideReasonSPriceMaxMin;
	}
	public boolean isProvideReasonNPriceMaxMin() {
		return provideReasonNPriceMaxMin;
	}
	public void setProvideReasonNPriceMaxMin(boolean provideReasonNPriceMaxMin) {
		this.provideReasonNPriceMaxMin = provideReasonNPriceMaxMin;
	}
	public boolean isnPriceGreaterSPrice() {
		return nPriceGreaterSPrice;
	}
	public void setnPriceGreaterSPrice(boolean nPriceGreaterSPrice) {
		this.nPriceGreaterSPrice = nPriceGreaterSPrice;
	}
	public boolean isProvideRemarkForNotSuitablePrice() {
		return provideRemarkForNotSuitablePrice;
	}
	public void setProvideRemarkForNotSuitablePrice(boolean provideRemarkForNotSuitablePrice) {
		this.provideRemarkForNotSuitablePrice = provideRemarkForNotSuitablePrice;
	}
	public boolean isReminderForPricingCycle() {
		return reminderForPricingCycle;
	}
	public void setReminderForPricingCycle(boolean reminderForPricingCycle) {
		this.reminderForPricingCycle = reminderForPricingCycle;
	}
	public boolean isProvideReasonPRSPriceSD() {
		return provideReasonPRSPriceSD;
	}
	public void setProvideReasonPRSPriceSD(boolean provideReasonPRSPriceSD) {
		this.provideReasonPRSPriceSD = provideReasonPRSPriceSD;
	}
	public boolean isProvideReasonPRNPriceSD() {
		return provideReasonPRNPriceSD;
	}
	public void setProvideReasonPRNPriceSD(boolean provideReasonPRNPriceSD) {
		this.provideReasonPRNPriceSD = provideReasonPRNPriceSD;
	}
	public Double getPrSPriceSDPositive() {
		return prSPriceSDPositive;
	}
	public void setPrSPriceSDPositive(Double prSPriceSDPositive) {
		this.prSPriceSDPositive = prSPriceSDPositive;
	}
	public Double getPrSPriceSDNegative() {
		return prSPriceSDNegative;
	}
	public void setPrSPriceSDNegative(Double prSPriceSDNegative) {
		this.prSPriceSDNegative = prSPriceSDNegative;
	}
	public Double getPrNPriceSDPositive() {
		return prNPriceSDPositive;
	}
	public void setPrNPriceSDPositive(Double prNPriceSDPositive) {
		this.prNPriceSDPositive = prNPriceSDPositive;
	}
	public Double getPrNPriceSDNegative() {
		return prNPriceSDNegative;
	}
	public void setPrNPriceSDNegative(Double prNPriceSDNegative) {
		this.prNPriceSDNegative = prNPriceSDNegative;
	}
	public Integer getPrSPriceMonth() {
		return prSPriceMonth;
	}
	public void setPrSPriceMonth(Integer prSPriceMonth) {
		this.prSPriceMonth = prSPriceMonth;
	}
	public Integer getPrNPriceMonth() {
		return prNPriceMonth;
	}
	public void setPrNPriceMonth(Integer prNPriceMonth) {
		this.prNPriceMonth = prNPriceMonth;
	}
	public boolean isProvideReasonSPriceSD() {
		return provideReasonSPriceSD;
	}
	public void setProvideReasonSPriceSD(boolean provideReasonSPriceSD) {
		this.provideReasonSPriceSD = provideReasonSPriceSD;
	}
	public boolean isProvideReasonNPriceSD() {
		return provideReasonNPriceSD;
	}
	public void setProvideReasonNPriceSD(boolean provideReasonNPriceSD) {
		this.provideReasonNPriceSD = provideReasonNPriceSD;
	}
	public Double getsPriceSDPositive() {
		return sPriceSDPositive;
	}
	public void setsPriceSDPositive(Double sPriceSDPositive) {
		this.sPriceSDPositive = sPriceSDPositive;
	}
	public Double getsPriceSDNegative() {
		return sPriceSDNegative;
	}
	public void setsPriceSDNegative(Double sPriceSDNegative) {
		this.sPriceSDNegative = sPriceSDNegative;
	}
	public Double getnPriceSDPositive() {
		return nPriceSDPositive;
	}
	public void setnPriceSDPositive(Double nPriceSDPositive) {
		this.nPriceSDPositive = nPriceSDPositive;
	}
	public Double getnPriceSDNegative() {
		return nPriceSDNegative;
	}
	public void setnPriceSDNegative(Double nPriceSDNegative) {
		this.nPriceSDNegative = nPriceSDNegative;
	}
	public Integer getsPriceMonth() {
		return sPriceMonth;
	}
	public void setsPriceMonth(Integer sPriceMonth) {
		this.sPriceMonth = sPriceMonth;
	}
	public Integer getnPriceMonth() {
		return nPriceMonth;
	}
	public void setnPriceMonth(Integer nPriceMonth) {
		this.nPriceMonth = nPriceMonth;
	}
	public boolean isProvideReasonPRNPriceLower() {
		return provideReasonPRNPriceLower;
	}
	public void setProvideReasonPRNPriceLower(boolean provideReasonPRNPriceLower) {
		this.provideReasonPRNPriceLower = provideReasonPRNPriceLower;
	}
	public Double getPrNPriceLowerThreshold() {
		return prNPriceLowerThreshold;
	}
	public void setPrNPriceLowerThreshold(Double prNPriceLowerThreshold) {
		this.prNPriceLowerThreshold = prNPriceLowerThreshold;
	}
	public boolean isProvideReasonPRSPriceLower() {
		return provideReasonPRSPriceLower;
	}
	public void setProvideReasonPRSPriceLower(boolean provideReasonPRSPriceLower) {
		this.provideReasonPRSPriceLower = provideReasonPRSPriceLower;
	}
	public Double getPrSPriceLowerThreshold() {
		return prSPriceLowerThreshold;
	}
	public void setPrSPriceLowerThreshold(Double prSPriceLowerThreshold) {
		this.prSPriceLowerThreshold = prSPriceLowerThreshold;
	}
	public String getDisplayCreatedDate() {
		return displayCreatedDate;
	}
	public void setDisplayCreatedDate(String displayCreatedDate) {
		this.displayCreatedDate = displayCreatedDate;
	}
	public String getDisplayModifiedDate() {
		return displayModifiedDate;
	}
	public void setDisplayModifiedDate(String displayModifiedDate) {
		this.displayModifiedDate = displayModifiedDate;
	}
	public boolean isProvideRemarkForNotAvailableQuotation() {
		return provideRemarkForNotAvailableQuotation;
	}
	public void setProvideRemarkForNotAvailableQuotation(boolean provideRemarkForNotAvailableQuotation) {
		this.provideRemarkForNotAvailableQuotation = provideRemarkForNotAvailableQuotation;
	}
	public String getDataTransmissionRule() {
		return dataTransmissionRule;
	}
	public void setDataTransmissionRule(String dataTransmissionRule) {
		this.dataTransmissionRule = dataTransmissionRule;
	}
	public boolean isConvertAfterClosingDate() {
		return convertAfterClosingDate;
	}
	public void setConvertAfterClosingDate(boolean convertAfterClosingDate) {
		this.convertAfterClosingDate = convertAfterClosingDate;
	}
	public String getIcpType() {
		return icpType;
	}
	public void setIcpType(String icpType) {
		this.icpType = icpType;
	}
}
