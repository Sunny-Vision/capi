package capi.model.dataImportExport;

import java.util.Date;

public class ExportEditedPriceList {
	//IndoorQuotationRecord	
	private Integer IndoorQuotationRecordId;
	
	private Double originalNPrice;
	
	private Double originalSPrice;
	
	private Double currentNPrice;
	
	private Double currentSPrice;
	
	private Double previousNPrice;
	
	private Double previousSPrice;
	
	private Double fr;
	
	private boolean isApplyFR;
	
	private boolean isFRPercentage;
	
	private String remark;
	
	private String status;
	
	private Double imputedQuotationPrice;
	
	private String imputedQuotationRemark;
	
	private Double imputedUnitPrice;
	
	private String imputedUnitRemark;
	
	private Double backNoLastNPirce;
	
	private Double backNoLastSPrice;
	
	private Double lastNPrice;
	
	private Double lastSPrice;
	
	private Integer copyPriceType;
	
	private Boolean isCurrentPriceKeepNo;
	
	private Date referenceMonth;
	
	private Date lastPriceDate;
	
	private Date referenceDate;
	
	private boolean isNoField;
	
	private boolean isProductChange;
	
	private boolean isNewProduct;
	
	private boolean isNewRecruitment;
	
	private boolean isNewOutlet;
	
	private Double nPriceAfterUOMConversion;
	
	private Double sPriceAfterUOMConversion;
	
	private Double computedNPrice;
	
	private Double computedSPrice;
	
	private boolean isNullCurrentNPrice;
	
	private boolean isNullCurrentSPrice;
	
	private boolean isNullPreviousSPrice;
	
	private boolean isNullPreviousNPrice;
	
	private Integer copyLastPriceType;
	
	private boolean isRUA;
	
	private Date ruaDate;
	
	private boolean isProductNotAvailable;
	
	private Date productNotAvailableFrom;
	
	private String rejectReason;
	
	private boolean isSpicing;
	
	private boolean isOutlier;
	
	private String outlierRemark;
	
	private String firmRemark;
	
	private String categoryRemark;
	
	private String quotationRemark;
	
	private String modifiedBy;
	
	private Date modifiedDate;
	
	//Verification
	private Long firmVerify;
	
	private Long categoryVerify;
	
	private Long quotationVerify;
	
	//QuotationRecord
	private Integer quotationRecordId;
	
	//User
	private String staffCode;
	
	//QuotationRecord.Outlet
	private Integer outletFirmCode;
	
	private String outletName;
	
	private String detailAddress;
	
	private String indoorMarketName;
	
	//QuotationRecord.Outlet.TPU
	private String tpuCode;
	
	private String councilDistrict;
	
	//QuotationRecord.Product
	private Integer productId;
	
	private String countryOfOrigin;
	
	//QuotationRecord.Product.ProductGroup
	private Integer productGroupId;
	
	private String productGroupCode;
	
	private String productGroupEnglishName;
	
	private String productGroupChineseName;
	
	//QuotationRecord.Product.ProductGroup.ProductAttribute
	private String pa1Name;
	
	private String pa2Name;
	
	private String pa3Name;
	
	private String pa4Name;
	
	private String pa5Name;
	
	//QuotationRecord.Product.ProductAttribute.ProductSpecification
	private String ps1Value;
	
	private String ps2Value;
	
	private String ps3Value;
	
	private String ps4Value;
	
	private String ps5Value;
	
	//Quotation
	private Integer quotationId;
	
	private String quotationStatus;
	
	private String formType;
	
	private String cpiCompilationSeries;
	
	private boolean isICP;
	
	private String icpProductCode;
	
	private String icpProductName;
	
	private String icpType;
	
	private Date lastProductChangeDate;
	
	private Date lastFRAppliedDate;
	
	//Quotation.Unit
	private String unitCode;
	
	private String cpiBasePeriod;
	
	private String unitEnglishName;
	
	private String unitChineseName;
	
	private Integer seasonality;
	
	private Integer cpiQuotationType;
	
	//Quotation.Unit.Purpose
	private String purposeCode;
	
	//Quotation.Unit.SubItem.OutletType;
	private String outletTypeCode;
	
	private String outletTypeEnglishName;
	
	//Quotation.District
	private String districtCode;
	
	private String districtEnglishName;
	
	//Quotation.Batch
	private String batchCode;

	public Integer getIndoorQuotationRecordId() {
		return IndoorQuotationRecordId;
	}

	public void setIndoorQuotationRecordId(Integer indoorQuotationRecordId) {
		IndoorQuotationRecordId = indoorQuotationRecordId;
	}

	public Double getOriginalNPrice() {
		return originalNPrice;
	}

	public void setOriginalNPrice(Double originalNPrice) {
		this.originalNPrice = originalNPrice;
	}

	public Double getOriginalSPrice() {
		return originalSPrice;
	}

	public void setOriginalSPrice(Double originalSPrice) {
		this.originalSPrice = originalSPrice;
	}

	public Double getCurrentNPrice() {
		return currentNPrice;
	}

	public void setCurrentNPrice(Double currentNPrice) {
		this.currentNPrice = currentNPrice;
	}

	public Double getCurrentSPrice() {
		return currentSPrice;
	}

	public void setCurrentSPrice(Double currentSPrice) {
		this.currentSPrice = currentSPrice;
	}

	public Double getPreviousNPrice() {
		return previousNPrice;
	}

	public void setPreviousNPrice(Double previousNPrice) {
		this.previousNPrice = previousNPrice;
	}

	public Double getPreviousSPrice() {
		return previousSPrice;
	}

	public void setPreviousSPrice(Double previousSPrice) {
		this.previousSPrice = previousSPrice;
	}

	public Double getFr() {
		return fr;
	}

	public void setFr(Double fr) {
		this.fr = fr;
	}

	public boolean isApplyFR() {
		return isApplyFR;
	}

	public void setApplyFR(boolean isApplyFR) {
		this.isApplyFR = isApplyFR;
	}

	public boolean isFRPercentage() {
		return isFRPercentage;
	}

	public void setFRPercentage(boolean isFRPercentage) {
		this.isFRPercentage = isFRPercentage;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getImputedQuotationPrice() {
		return imputedQuotationPrice;
	}

	public void setImputedQuotationPrice(Double imputedQuotationPrice) {
		this.imputedQuotationPrice = imputedQuotationPrice;
	}

	public String getImputedQuotationRemark() {
		return imputedQuotationRemark;
	}

	public void setImputedQuotationRemark(String imputedQuotationRemark) {
		this.imputedQuotationRemark = imputedQuotationRemark;
	}

	public Double getImputedUnitPrice() {
		return imputedUnitPrice;
	}

	public void setImputedUnitPrice(Double imputedUnitPrice) {
		this.imputedUnitPrice = imputedUnitPrice;
	}

	public String getImputedUnitRemark() {
		return imputedUnitRemark;
	}

	public void setImputedUnitRemark(String imputedUnitRemark) {
		this.imputedUnitRemark = imputedUnitRemark;
	}

	public Double getBackNoLastNPirce() {
		return backNoLastNPirce;
	}

	public void setBackNoLastNPirce(Double backNoLastNPirce) {
		this.backNoLastNPirce = backNoLastNPirce;
	}

	public Double getBackNoLastSPrice() {
		return backNoLastSPrice;
	}

	public void setBackNoLastSPrice(Double backNoLastSPrice) {
		this.backNoLastSPrice = backNoLastSPrice;
	}

	public Double getLastNPrice() {
		return lastNPrice;
	}

	public void setLastNPrice(Double lastNPrice) {
		this.lastNPrice = lastNPrice;
	}

	public Double getLastSPrice() {
		return lastSPrice;
	}

	public void setLastSPrice(Double lastSPrice) {
		this.lastSPrice = lastSPrice;
	}

	public Integer getCopyPriceType() {
		return copyPriceType;
	}

	public void setCopyPriceType(Integer copyPriceType) {
		this.copyPriceType = copyPriceType;
	}

	public Boolean getIsCurrentPriceKeepNo() {
		return isCurrentPriceKeepNo;
	}

	public void setIsCurrentPriceKeepNo(Boolean isCurrentPriceKeepNo) {
		this.isCurrentPriceKeepNo = isCurrentPriceKeepNo;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Date getLastPriceDate() {
		return lastPriceDate;
	}

	public void setLastPriceDate(Date lastPriceDate) {
		this.lastPriceDate = lastPriceDate;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public boolean isNoField() {
		return isNoField;
	}

	public void setNoField(boolean isNoField) {
		this.isNoField = isNoField;
	}

	public boolean isProductChange() {
		return isProductChange;
	}

	public void setProductChange(boolean isProductChange) {
		this.isProductChange = isProductChange;
	}

	public boolean isNewProduct() {
		return isNewProduct;
	}

	public void setNewProduct(boolean isNewProduct) {
		this.isNewProduct = isNewProduct;
	}

	public boolean isNewRecruitment() {
		return isNewRecruitment;
	}

	public void setNewRecruitment(boolean isNewRecruitment) {
		this.isNewRecruitment = isNewRecruitment;
	}

	public boolean isNewOutlet() {
		return isNewOutlet;
	}

	public void setNewOutlet(boolean isNewOutlet) {
		this.isNewOutlet = isNewOutlet;
	}

	public Double getnPriceAfterUOMConversion() {
		return nPriceAfterUOMConversion;
	}

	public void setnPriceAfterUOMConversion(Double nPriceAfterUOMConversion) {
		this.nPriceAfterUOMConversion = nPriceAfterUOMConversion;
	}

	public Double getsPriceAfterUOMConversion() {
		return sPriceAfterUOMConversion;
	}

	public void setsPriceAfterUOMConversion(Double sPriceAfterUOMConversion) {
		this.sPriceAfterUOMConversion = sPriceAfterUOMConversion;
	}

	public Double getComputedNPrice() {
		return computedNPrice;
	}

	public void setComputedNPrice(Double computedNPrice) {
		this.computedNPrice = computedNPrice;
	}

	public Double getComputedSPrice() {
		return computedSPrice;
	}

	public void setComputedSPrice(Double computedSPrice) {
		this.computedSPrice = computedSPrice;
	}

	public boolean isNullCurrentNPrice() {
		return isNullCurrentNPrice;
	}

	public void setNullCurrentNPrice(boolean isNullCurrentNPrice) {
		this.isNullCurrentNPrice = isNullCurrentNPrice;
	}

	public boolean isNullCurrentSPrice() {
		return isNullCurrentSPrice;
	}

	public void setNullCurrentSPrice(boolean isNullCurrentSPrice) {
		this.isNullCurrentSPrice = isNullCurrentSPrice;
	}

	public boolean isNullPreviousSPrice() {
		return isNullPreviousSPrice;
	}

	public void setNullPreviousSPrice(boolean isNullPreviousSPrice) {
		this.isNullPreviousSPrice = isNullPreviousSPrice;
	}

	public boolean isNullPreviousNPrice() {
		return isNullPreviousNPrice;
	}

	public void setNullPreviousNPrice(boolean isNullPreviousNPrice) {
		this.isNullPreviousNPrice = isNullPreviousNPrice;
	}

	public Integer getCopyLastPriceType() {
		return copyLastPriceType;
	}

	public void setCopyLastPriceType(Integer copyLastPriceType) {
		this.copyLastPriceType = copyLastPriceType;
	}

	public boolean isRUA() {
		return isRUA;
	}

	public void setRUA(boolean isRUA) {
		this.isRUA = isRUA;
	}

	public Date getRuaDate() {
		return ruaDate;
	}

	public void setRuaDate(Date ruaDate) {
		this.ruaDate = ruaDate;
	}

	public boolean isProductNotAvailable() {
		return isProductNotAvailable;
	}

	public void setProductNotAvailable(boolean isProductNotAvailable) {
		this.isProductNotAvailable = isProductNotAvailable;
	}

	public Date getProductNotAvailableFrom() {
		return productNotAvailableFrom;
	}

	public void setProductNotAvailableFrom(Date productNotAvailableFrom) {
		this.productNotAvailableFrom = productNotAvailableFrom;
	}

	public Long getFirmVerify() {
		return firmVerify;
	}

	public void setFirmVerify(Long firmVerify) {
		this.firmVerify = firmVerify;
	}

	public Long getCategoryVerify() {
		return categoryVerify;
	}

	public void setCategoryVerify(Long categoryVerify) {
		this.categoryVerify = categoryVerify;
	}

	public Long getQuotationVerify() {
		return quotationVerify;
	}

	public void setQuotationVerify(Long quotationVerify) {
		this.quotationVerify = quotationVerify;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public boolean isOutlier() {
		return isOutlier;
	}

	public void setOutlier(boolean isOutlier) {
		this.isOutlier = isOutlier;
	}

	public String getOutlierRemark() {
		return outlierRemark;
	}

	public void setOutlierRemark(String outlierRemark) {
		this.outlierRemark = outlierRemark;
	}

	public String getFirmRemark() {
		return firmRemark;
	}

	public void setFirmRemark(String firmRemark) {
		this.firmRemark = firmRemark;
	}

	public String getCategoryRemark() {
		return categoryRemark;
	}

	public void setCategoryRemark(String categoryRemark) {
		this.categoryRemark = categoryRemark;
	}

	public String getQuotationRemark() {
		return quotationRemark;
	}

	public void setQuotationRemark(String quotationRemark) {
		this.quotationRemark = quotationRemark;
	}

	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public Integer getOutletFirmCode() {
		return outletFirmCode;
	}

	public void setOutletFirmCode(Integer outletFirmCode) {
		this.outletFirmCode = outletFirmCode;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getIndoorMarketName() {
		return indoorMarketName;
	}

	public void setIndoorMarketName(String indoorMarketName) {
		this.indoorMarketName = indoorMarketName;
	}

	public String getTpuCode() {
		return tpuCode;
	}

	public void setTpuCode(String tpuCode) {
		this.tpuCode = tpuCode;
	}

	public String getCouncilDistrict() {
		return councilDistrict;
	}

	public void setCouncilDistrict(String councilDistrict) {
		this.councilDistrict = councilDistrict;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public Integer getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(Integer productGroupId) {
		this.productGroupId = productGroupId;
	}

	public String getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(String productGroupCode) {
		this.productGroupCode = productGroupCode;
	}

	public String getProductGroupEnglishName() {
		return productGroupEnglishName;
	}

	public void setProductGroupEnglishName(String productGroupEnglishName) {
		this.productGroupEnglishName = productGroupEnglishName;
	}

	public String getProductGroupChineseName() {
		return productGroupChineseName;
	}

	public void setProductGroupChineseName(String productGroupChineseName) {
		this.productGroupChineseName = productGroupChineseName;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public String getQuotationStatus() {
		return quotationStatus;
	}

	public void setQuotationStatus(String quotationStatus) {
		this.quotationStatus = quotationStatus;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getCpiCompilationSeries() {
		return cpiCompilationSeries;
	}

	public void setCpiCompilationSeries(String cpiCompilationSeries) {
		this.cpiCompilationSeries = cpiCompilationSeries;
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

	public Date getLastProductChangeDate() {
		return lastProductChangeDate;
	}

	public void setLastProductChangeDate(Date lastProductChangeDate) {
		this.lastProductChangeDate = lastProductChangeDate;
	}

	public Date getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}

	public void setLastFRAppliedDate(Date lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
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

	public Integer getCpiQuotationType() {
		return cpiQuotationType;
	}

	public void setCpiQuotationType(Integer cpiQuotationType) {
		this.cpiQuotationType = cpiQuotationType;
	}

	public String getPurposeCode() {
		return purposeCode;
	}

	public void setPurposeCode(String purposeCode) {
		this.purposeCode = purposeCode;
	}

	public String getOutletTypeCode() {
		return outletTypeCode;
	}

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public String getOutletTypeEnglishName() {
		return outletTypeEnglishName;
	}

	public void setOutletTypeEnglishName(String outletTypeEnglishName) {
		this.outletTypeEnglishName = outletTypeEnglishName;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictEnglishName() {
		return districtEnglishName;
	}

	public void setDistrictEnglishName(String districtEnglishName) {
		this.districtEnglishName = districtEnglishName;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public boolean isSpicing() {
		return isSpicing;
	}

	public void setSpicing(boolean isSpicing) {
		this.isSpicing = isSpicing;
	}

	public String getPa1Name() {
		return pa1Name;
	}

	public void setPa1Name(String pa1Name) {
		this.pa1Name = pa1Name;
	}

	public String getPa2Name() {
		return pa2Name;
	}

	public void setPa2Name(String pa2Name) {
		this.pa2Name = pa2Name;
	}

	public String getPa3Name() {
		return pa3Name;
	}

	public void setPa3Name(String pa3Name) {
		this.pa3Name = pa3Name;
	}

	public String getPa4Name() {
		return pa4Name;
	}

	public void setPa4Name(String pa4Name) {
		this.pa4Name = pa4Name;
	}

	public String getPa5Name() {
		return pa5Name;
	}

	public void setPa5Name(String pa5Name) {
		this.pa5Name = pa5Name;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getPs1Value() {
		return ps1Value;
	}

	public void setPs1Value(String ps1Value) {
		this.ps1Value = ps1Value;
	}

	public String getPs2Value() {
		return ps2Value;
	}

	public void setPs2Value(String ps2Value) {
		this.ps2Value = ps2Value;
	}

	public String getPs3Value() {
		return ps3Value;
	}

	public void setPs3Value(String ps3Value) {
		this.ps3Value = ps3Value;
	}

	public String getPs4Value() {
		return ps4Value;
	}

	public void setPs4Value(String ps4Value) {
		this.ps4Value = ps4Value;
	}

	public String getPs5Value() {
		return ps5Value;
	}

	public void setPs5Value(String ps5Value) {
		this.ps5Value = ps5Value;
	}
	
}
