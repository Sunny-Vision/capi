package capi.model.dataImportExport;

import java.util.Date;

public class ExportEditPriceStatisticList {

	private Integer rowNum;
	
	private Integer indoorQuotationRecordId;
	
	private Date referenceMonth;
	
	private Integer quotationRecordId;
	
	private Date referenceDate;
	
	private Boolean isNoField;
	
	private Boolean isProductChange;
	
	private Boolean isNewProduct;
	
	private Boolean isNewRecruitment;
	
	private Boolean isNewOutlet;
	
	private String indoorStatus;
	
	private Integer quotationId;
	
	private String quotationStatus;
	
	private String batchCode;
	
	private String formType;
	
	private Integer unitId;
	
	private String unitCode;
	
	private String purpose;
	
	private String cpiBasePeriod;
	
	private String unitEnglishName;
	
	private String unitChineseName;
	
	private Integer seasonality;
	
	private String cpiCompilationSeries;
	
	private Integer cpiQuotationType;
	
	private Boolean isICP;
	
	private String icpProductCode;
	
	private String icpProductName;
	
	private String quotationIcpType;
	
	private String unitIcpType;
	
	private Integer outletCode;
	
	private String outletName;
	
	private String outletTypeCode;
	
	private String outletTypeEnglishName;
	
	private String district;
	
	private String tpu;
	
	private String councilDistrict;
	
	private String districtEnglishName;
	
	private String detailAddress;
	
	private String indoorMarketName;
	
	private Integer productGroupId;
	
	private String productGroupCode;
	
	private String productGroupEnglishName;
	
	private String productGroupChineseName;
	
	private Integer productId;
	
	private String countryOfOrigin;
	
	private String pa1Name;
	
	private String pa2Name;
	
	private String pa3Name;
	
	private String pa4Name;
	
	private String pa5Name;
	
	private String ps1Value;
	
	private String ps2Value;
	
	private String ps3Value;
	
	private String ps4Value;
	
	private String ps5Value;
	
	private Date lastProductChangeDate;
	
	private String staffCode;
	
	private Double originalNPrice;
	
	private Double originalSPrice;
	
	private Double nPriceAfterUOMConversion;
	
	private Double sPriceAfterUOMConversion;
	
	private Double computedNPrice;
	
	private Double computedSPrice;
	
	private Double currentNPrice;
	
	private Double currentSPrice;
	
	private Double previousNPrice;
	
	private Double previousSPrice;
	
	private Boolean isNullCurrentNPrice;
	
	private Boolean isNullCurrentSPrice;
	
	private Boolean isNullPreviousNPrice;
	
	private Boolean isNullPreviousSPrice;
	
	private Double backNoLastNPrice;
	
	private Double backNoLastSPrice;
	
	private Double lastNPrice;
	
	private Double lastSPrice;
	
	private Date lastPriceDate;
	
	private Integer copyPriceType;
	
	private Integer copyLastPriceType;
	
	private String remark;
	
	private Boolean isCurrentPriceKeepNo;
	
	private Boolean isRUA;
	
	private Date ruaDate;
	
	private Boolean isProductNotAvailable;
	
	private Date productNotAvailableFrom;
	
	private Long firmVerify;
	
	private Long categoryVerify;
	
	private Long quotationVerify;
	
	private String rejectReason;
	
	private Boolean isSpicing;
	
	private Boolean isOutlier;
	
	private String outlierRemark;
	
	private Double fr;
	
	private Boolean isApplyFR;
	
	private Boolean isFRPercentage;
	
	private Date lastFRAppliedDate;
	
	private Double imputedQuotationPrice;
	
	private String imputedQuotationRemark;
	
	private Double imputedUnitPrice;
	
	private String imputedUnitRemark;
	
	private String firmRemark;
	
	private String categoryRemark;
	
	private String quotationRemark;
	
	private Integer quotationRecordSequence;
	
	private Double qsAverageCurrentSPrice;
	
	private Double qsAverageLastSPrice;
	
	private Double qsLastHasPriceAverageCurrentSPrice;
	
	private Double usAverageCurrentSPrice;
	
	private Double usAverageLastSPrice;
	
	private Double usLastHasPriceAverageCurrentSPrice;

	private Double qsStandardDeviationSPrice;
	
	private Double qsMaxSPrice; 
	
	private Double qsMinSPrice; 
	
	private Double usStandardDeviationSPrice; 
	
	private Double usMaxSPrice;
	
	private Double usMinSPrice;
	
	private String oldFormSequence;
	
	private String cpiQoutationType;
	
	private Integer compilationMethod;
	
	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

	public Integer getIndoorQuotationRecordId() {
		return indoorQuotationRecordId;
	}

	public void setIndoorQuotationRecordId(Integer indoorQuotationRecordId) {
		this.indoorQuotationRecordId = indoorQuotationRecordId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public Boolean getIsNoField() {
		return isNoField;
	}

	public void setIsNoField(Boolean isNoField) {
		this.isNoField = isNoField;
	}

	public Boolean getIsProductChange() {
		return isProductChange;
	}

	public void setIsProductChange(Boolean isProductChange) {
		this.isProductChange = isProductChange;
	}

	public Boolean getIsNewProduct() {
		return isNewProduct;
	}

	public void setIsNewProduct(Boolean isNewProduct) {
		this.isNewProduct = isNewProduct;
	}

	public Boolean getIsNewRecruitment() {
		return isNewRecruitment;
	}

	public void setIsNewRecruitment(Boolean isNewRecruitment) {
		this.isNewRecruitment = isNewRecruitment;
	}

	public Boolean getIsNewOutlet() {
		return isNewOutlet;
	}

	public void setIsNewOutlet(Boolean isNewOutlet) {
		this.isNewOutlet = isNewOutlet;
	}

	public String getIndoorStatus() {
		return indoorStatus;
	}

	public void setIndoorStatus(String indoorStatus) {
		this.indoorStatus = indoorStatus;
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

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
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

	public Boolean getIsICP() {
		return isICP;
	}

	public void setIsICP(Boolean isICP) {
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

	public String getQuotationIcpType() {
		return quotationIcpType;
	}

	public void setQuotationIcpType(String quotationIcpType) {
		this.quotationIcpType = quotationIcpType;
	}

	public String getUnitIcpType() {
		return unitIcpType;
	}

	public void setUnitIcpType(String unitIcpType) {
		this.unitIcpType = unitIcpType;
	}

	public Integer getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(Integer outletCode) {
		this.outletCode = outletCode;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
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

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTpu() {
		return tpu;
	}

	public void setTpu(String tpu) {
		this.tpu = tpu;
	}

	public String getCouncilDistrict() {
		return councilDistrict;
	}

	public void setCouncilDistrict(String councilDistrict) {
		this.councilDistrict = councilDistrict;
	}

	public String getDistrictEnglishName() {
		return districtEnglishName;
	}

	public void setDistrictEnglishName(String districtEnglishName) {
		this.districtEnglishName = districtEnglishName;
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

	public Date getLastProductChangeDate() {
		return lastProductChangeDate;
	}

	public void setLastProductChangeDate(Date lastProductChangeDate) {
		this.lastProductChangeDate = lastProductChangeDate;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
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

	public Boolean getIsNullCurrentNPrice() {
		return isNullCurrentNPrice;
	}

	public void setIsNullCurrentNPrice(Boolean isNullCurrentNPrice) {
		this.isNullCurrentNPrice = isNullCurrentNPrice;
	}

	public Boolean getIsNullCurrentSPrice() {
		return isNullCurrentSPrice;
	}

	public void setIsNullCurrentSPrice(Boolean isNullCurrentSPrice) {
		this.isNullCurrentSPrice = isNullCurrentSPrice;
	}

	public Boolean getIsNullPreviousNPrice() {
		return isNullPreviousNPrice;
	}

	public void setIsNullPreviousNPrice(Boolean isNullPreviousNPrice) {
		this.isNullPreviousNPrice = isNullPreviousNPrice;
	}

	public Boolean getIsNullPreviousSPrice() {
		return isNullPreviousSPrice;
	}

	public void setIsNullPreviousSPrice(Boolean isNullPreviousSPrice) {
		this.isNullPreviousSPrice = isNullPreviousSPrice;
	}

	public Double getBackNoLastNPrice() {
		return backNoLastNPrice;
	}

	public void setBackNoLastNPrice(Double backNoLastNPrice) {
		this.backNoLastNPrice = backNoLastNPrice;
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

	public Date getLastPriceDate() {
		return lastPriceDate;
	}

	public void setLastPriceDate(Date lastPriceDate) {
		this.lastPriceDate = lastPriceDate;
	}

	public Integer getCopyPriceType() {
		return copyPriceType;
	}

	public void setCopyPriceType(Integer copyPriceType) {
		this.copyPriceType = copyPriceType;
	}

	public Integer getCopyLastPriceType() {
		return copyLastPriceType;
	}

	public void setCopyLastPriceType(Integer copyLastPriceType) {
		this.copyLastPriceType = copyLastPriceType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getIsCurrentPriceKeepNo() {
		return isCurrentPriceKeepNo;
	}

	public void setIsCurrentPriceKeepNo(Boolean isCurrentPriceKeepNo) {
		this.isCurrentPriceKeepNo = isCurrentPriceKeepNo;
	}

	public Boolean getIsRUA() {
		return isRUA;
	}

	public void setIsRUA(Boolean isRUA) {
		this.isRUA = isRUA;
	}

	public Date getRuaDate() {
		return ruaDate;
	}

	public void setRuaDate(Date ruaDate) {
		this.ruaDate = ruaDate;
	}

	public Boolean getIsProductNotAvailable() {
		return isProductNotAvailable;
	}

	public void setIsProductNotAvailable(Boolean isProductNotAvailable) {
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

	public Boolean getIsSpicing() {
		return isSpicing;
	}

	public void setIsSpicing(Boolean isSpicing) {
		this.isSpicing = isSpicing;
	}

	public Boolean getIsOutlier() {
		return isOutlier;
	}

	public void setIsOutlier(Boolean isOutlier) {
		this.isOutlier = isOutlier;
	}

	public String getOutlierRemark() {
		return outlierRemark;
	}

	public void setOutlierRemark(String outlierRemark) {
		this.outlierRemark = outlierRemark;
	}

	public Double getFr() {
		return fr;
	}

	public void setFr(Double fr) {
		this.fr = fr;
	}

	public Boolean getIsApplyFR() {
		return isApplyFR;
	}

	public void setIsApplyFR(Boolean isApplyFR) {
		this.isApplyFR = isApplyFR;
	}

	public Boolean getIsFRPercentage() {
		return isFRPercentage;
	}

	public void setIsFRPercentage(Boolean isFRPercentage) {
		this.isFRPercentage = isFRPercentage;
	}

	public Date getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}

	public void setLastFRAppliedDate(Date lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
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

	public Integer getQuotationRecordSequence() {
		return quotationRecordSequence;
	}

	public void setQuotationRecordSequence(Integer quotationRecordSequence) {
		this.quotationRecordSequence = quotationRecordSequence;
	}

	public Double getQsAverageCurrentSPrice() {
		return qsAverageCurrentSPrice;
	}

	public void setQsAverageCurrentSPrice(Double qsAverageCurrentSPrice) {
		this.qsAverageCurrentSPrice = qsAverageCurrentSPrice;
	}

	public Double getQsAverageLastSPrice() {
		return qsAverageLastSPrice;
	}

	public void setQsAverageLastSPrice(Double qsAverageLastSPrice) {
		this.qsAverageLastSPrice = qsAverageLastSPrice;
	}

	public Double getQsLastHasPriceAverageCurrentSPrice() {
		return qsLastHasPriceAverageCurrentSPrice;
	}

	public void setQsLastHasPriceAverageCurrentSPrice(Double qsLastHasPriceAverageCurrentSPrice) {
		this.qsLastHasPriceAverageCurrentSPrice = qsLastHasPriceAverageCurrentSPrice;
	}

	public Double getUsAverageCurrentSPrice() {
		return usAverageCurrentSPrice;
	}

	public void setUsAverageCurrentSPrice(Double usAverageCurrentSPrice) {
		this.usAverageCurrentSPrice = usAverageCurrentSPrice;
	}

	public Double getUsAverageLastSPrice() {
		return usAverageLastSPrice;
	}

	public void setUsAverageLastSPrice(Double usAverageLastSPrice) {
		this.usAverageLastSPrice = usAverageLastSPrice;
	}

	public Double getUsLastHasPriceAverageCurrentSPrice() {
		return usLastHasPriceAverageCurrentSPrice;
	}

	public void setUsLastHasPriceAverageCurrentSPrice(Double usLastHasPriceAverageCurrentSPrice) {
		this.usLastHasPriceAverageCurrentSPrice = usLastHasPriceAverageCurrentSPrice;
	}

	public String getCpiCompilationSeries() {
		return cpiCompilationSeries;
	}

	public void setCpiCompilationSeries(String cpiCompilationSeries) {
		this.cpiCompilationSeries = cpiCompilationSeries;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getOldFormSequence() {
		return oldFormSequence;
	}

	public void setOldFormSequence(String oldFormSequence) {
		this.oldFormSequence = oldFormSequence;
	}

	public Double getQsStandardDeviationSPrice() {
		return qsStandardDeviationSPrice;
	}

	public void setQsStandardDeviationSPrice(Double qsStandardDeviationSPrice) {
		this.qsStandardDeviationSPrice = qsStandardDeviationSPrice;
	}

	public Double getQsMaxSPrice() {
		return qsMaxSPrice;
	}

	public void setQsMaxSPrice(Double qsMaxSPrice) {
		this.qsMaxSPrice = qsMaxSPrice;
	}

	public Double getQsMinSPrice() {
		return qsMinSPrice;
	}

	public void setQsMinSPrice(Double qsMinSPrice) {
		this.qsMinSPrice = qsMinSPrice;
	}

	public Double getUsStandardDeviationSPrice() {
		return usStandardDeviationSPrice;
	}

	public void setUsStandardDeviationSPrice(Double usStandardDeviationSPrice) {
		this.usStandardDeviationSPrice = usStandardDeviationSPrice;
	}

	public Double getUsMaxSPrice() {
		return usMaxSPrice;
	}

	public void setUsMaxSPrice(Double usMaxSPrice) {
		this.usMaxSPrice = usMaxSPrice;
	}

	public Double getUsMinSPrice() {
		return usMinSPrice;
	}

	public void setUsMinSPrice(Double usMinSPrice) {
		this.usMinSPrice = usMinSPrice;
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

	public String getCpiQoutationType() {
		return cpiQoutationType;
	}

	public void setCpiQoutationType(String cpiQoutationType) {
		this.cpiQoutationType = cpiQoutationType;
	}

	public Integer getCompilationMethod() {
		return compilationMethod;
	}

	public void setCompilationMethod(Integer compilationMethod) {
		this.compilationMethod = compilationMethod;
	}
	
}
