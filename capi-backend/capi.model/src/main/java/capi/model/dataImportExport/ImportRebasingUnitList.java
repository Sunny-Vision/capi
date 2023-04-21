package capi.model.dataImportExport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import capi.entity.Unit;

public class ImportRebasingUnitList {
	
	private String oldSectionCode;
	
	private String oldGroupCode;
	
	private String oldSubGroupCode;
	
	private String oldItemCode;
	
	private String oldOutletTypeCode;
	
	private String oldSubItemCode;
	
//	private List<Integer> oldUnitIds = new ArrayList<Integer>();
//	
//	private List<String> oldUnitCodes = new ArrayList<String>();
//	
//	private List<String> oldCPIBasePeriods = new ArrayList<String>();
	
	private List<Unit> oldUnits = new ArrayList<Unit>();
	
	private String subItemCode;
	
	private Integer unitId;
	
	private String code;
	
	private String unitChineseName;
	
	private String unitEnglishName;
	
	private Date ObsoleteDate;
	
	private String displayName;
	
	private Boolean isMRPS;
	
	private Integer purposeId;
	
	private String purposeCode;
	
	private Integer maxQuotation;
	
	private Integer minQuotation;

	private String unitCategory;
	
	private Integer standardUOMId;
	
	private Double uomValue;
	
	private Integer productCategoryId;
	
	private String productCategoryCode;
	
	private Integer subPriceTypeId;
	
	private Boolean spicingRequired;
	
	private Boolean frRequired;
	
	private Integer seasonality;
	
	private Integer seasonStartMonth;
	
	private Integer seasonEndMonth;
	
	private Integer pricingFrequencyId;
	
	private Integer rtnPeriod;
	
	private Boolean backdateRequired;
	
	private Boolean allowEditPMPrice;
	
	private Boolean ruaAllowed;
	
	private Boolean isFreshItem;
	
	private Boolean allowProductChange;
	
	private Integer formDisplay;
	
	private Integer productCycle;
	
	private String crossCheckGroup;
	
	private Integer cpiQuotationType;
	
	private Boolean isTemporary;
	
	private Boolean isNPriceMandatory;
	
	private Boolean isSPriceMandatory;
	
	private String dataTransmissionRule;
	
	private Double consolidatedSPRMean;
	
	private Double consolidatedSPRSD;
	
	private Double consolidatedNPRMean;
	
	private Double consolidatedNPRSD;
	
	public String icpType;
	
	public Boolean convertAfterClosingDate;
	
	public List<Integer> uomCategoryIds;

	public String getOldSectionCode() {
		return oldSectionCode;
	}

	public void setOldSectionCode(String oldSectionCode) {
		this.oldSectionCode = oldSectionCode;
	}

	public String getOldGroupCode() {
		return oldGroupCode;
	}

	public void setOldGroupCode(String oldGroupCode) {
		this.oldGroupCode = oldGroupCode;
	}

	public String getOldSubGroupCode() {
		return oldSubGroupCode;
	}

	public void setOldSubGroupCode(String oldSubGroupCode) {
		this.oldSubGroupCode = oldSubGroupCode;
	}

	public String getOldItemCode() {
		return oldItemCode;
	}

	public void setOldItemCode(String oldItemCode) {
		this.oldItemCode = oldItemCode;
	}

	public String getOldOutletTypeCode() {
		return oldOutletTypeCode;
	}

	public void setOldOutletTypeCode(String oldOutletTypeCode) {
		this.oldOutletTypeCode = oldOutletTypeCode;
	}

	public String getOldSubItemCode() {
		return oldSubItemCode;
	}

	public void setOldSubItemCode(String oldSubItemCode) {
		this.oldSubItemCode = oldSubItemCode;
	}

//	public List<Integer> getOldUnitIds() {
//		return oldUnitIds;
//	}
//
//	public void setOldUnitIds(List<Integer> oldUnitIds) {
//		this.oldUnitIds = oldUnitIds;
//	}
//
//	public List<String> getOldUnitCodes() {
//		return oldUnitCodes;
//	}
//
//	public void setOldUnitCodes(List<String> oldUnitCodes) {
//		this.oldUnitCodes = oldUnitCodes;
//	}
//
//	public List<String> getOldCPIBasePeriods() {
//		return oldCPIBasePeriods;
//	}
//
//	public void setOldCPIBasePeriods(List<String> oldCPIBasePeriods) {
//		this.oldCPIBasePeriods = oldCPIBasePeriods;
//	}

	public String getSubItemCode() {
		return subItemCode;
	}

	public void setSubItemCode(String subItemCode) {
		this.subItemCode = subItemCode;
	}

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

	public String getUnitChineseName() {
		return unitChineseName;
	}

	public void setUnitChineseName(String unitChineseName) {
		this.unitChineseName = unitChineseName;
	}

	public String getUnitEnglishName() {
		return unitEnglishName;
	}

	public void setUnitEnglishName(String unitEnglishName) {
		this.unitEnglishName = unitEnglishName;
	}

	public Date getObsoleteDate() {
		return ObsoleteDate;
	}

	public void setObsoleteDate(Date obsoleteDate) {
		ObsoleteDate = obsoleteDate;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean getIsMRPS() {
		return isMRPS;
	}

	public void setIsMRPS(Boolean isMRPS) {
		this.isMRPS = isMRPS;
	}

	public Integer getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}

	public String getPurposeCode() {
		return purposeCode;
	}

	public void setPurposeCode(String purposeCode) {
		this.purposeCode = purposeCode;
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

	public String getProductCategoryCode() {
		return productCategoryCode;
	}

	public void setProductCategoryCode(String productCategoryCode) {
		this.productCategoryCode = productCategoryCode;
	}

	public Integer getSubPriceTypeId() {
		return subPriceTypeId;
	}

	public void setSubPriceTypeId(Integer subPriceTypeId) {
		this.subPriceTypeId = subPriceTypeId;
	}

	public Boolean getSpicingRequired() {
		return spicingRequired;
	}

	public void setSpicingRequired(Boolean spicingRequired) {
		this.spicingRequired = spicingRequired;
	}

	public Boolean getFrRequired() {
		return frRequired;
	}

	public void setFrRequired(Boolean frRequired) {
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

	public Boolean getBackdateRequired() {
		return backdateRequired;
	}

	public void setBackdateRequired(Boolean backdateRequired) {
		this.backdateRequired = backdateRequired;
	}

	public Boolean getAllowEditPMPrice() {
		return allowEditPMPrice;
	}

	public void setAllowEditPMPrice(Boolean allowEditPMPrice) {
		this.allowEditPMPrice = allowEditPMPrice;
	}

	public Boolean getRuaAllowed() {
		return ruaAllowed;
	}

	public void setRuaAllowed(Boolean ruaAllowed) {
		this.ruaAllowed = ruaAllowed;
	}

	public Boolean getIsFreshItem() {
		return isFreshItem;
	}

	public void setIsFreshItem(Boolean isFreshItem) {
		this.isFreshItem = isFreshItem;
	}

	public Boolean getAllowProductChange() {
		return allowProductChange;
	}

	public void setAllowProductChange(Boolean allowProductChange) {
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

	public String getCrossCheckGroup() {
		return crossCheckGroup;
	}

	public void setCrossCheckGroup(String crossCheckGroup) {
		this.crossCheckGroup = crossCheckGroup;
	}

	public Integer getCpiQuotationType() {
		return cpiQuotationType;
	}

	public void setCpiQuotationType(Integer cpiQuotationType) {
		this.cpiQuotationType = cpiQuotationType;
	}

	public Boolean getIsTemporary() {
		return isTemporary;
	}

	public void setIsTemporary(Boolean isTemporary) {
		this.isTemporary = isTemporary;
	}

	public Boolean getIsNPriceMandatory() {
		return isNPriceMandatory;
	}

	public void setIsNPriceMandatory(Boolean isNPriceMandatory) {
		this.isNPriceMandatory = isNPriceMandatory;
	}

	public Boolean getIsSPriceMandatory() {
		return isSPriceMandatory;
	}

	public void setIsSPriceMandatory(Boolean isSPriceMandatory) {
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

	public List<Unit> getOldUnits() {
		return oldUnits;
	}

	public void setOldUnits(List<Unit> oldUnits) {
		this.oldUnits = oldUnits;
	}

	public String getIcpType() {
		return icpType;
	}

	public void setIcpType(String icpType) {
		this.icpType = icpType;
	}

	public Boolean getConvertAfterClosingDate() {
		return convertAfterClosingDate;
	}

	public void setConvertAfterClosingDate(Boolean convertAfterClosingDate) {
		this.convertAfterClosingDate = convertAfterClosingDate;
	}

	public List<Integer> getUomCategoryIds() {
		return uomCategoryIds;
	}

	public void setUomCategoryIds(List<Integer> uomCategoryIds) {
		this.uomCategoryIds = uomCategoryIds;
	}

	
}
