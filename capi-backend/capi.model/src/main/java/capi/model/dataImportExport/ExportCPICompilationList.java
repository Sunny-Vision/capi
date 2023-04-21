package capi.model.dataImportExport;

import java.util.Date;

public class ExportCPICompilationList {
	private Integer rowNum;
	
	private Integer indoorQuotationRecordId;
	
	private Date indoorReferenceMonth;
	
	private Integer quotationId;
	
	private Integer quotationRecordId;
	
	private Integer quotationRecordSequence;
	
	private Double currentNPrice;
	
	private Double currentSPrice;
	
	private Double previousNPrice;
	
	private Double previousSPrice;
	
	private Double lastNPrice;
	
	private Double lastSPrice;
	
	private Date lastPriceDate;
	
	private Double qsSumCurrentSPrice;
	
	private Integer qsCountCurrentSPrice;
	
	private Double qsAverageCurrentSPrice;
	
	private Double qsSumLastSPrice;
	
	private Integer qsCountLastSPrice;
	
	private Double qsAverageLastSPrice;
	
	private Double qsLastHasPriceSumCurrentSPrice;
	
	private Integer qsLastHasPriceCountCurrentSPrice;
	
	private Double qsLastHasPriceAverageCurrentSPrice;
	
	private Date qsLastHasPriceReferenceMonth;
	
	private Double usSumCurrentSPrice;
	
	private Integer usCountCurrentSPrice;
	
	private Double usAverageCurrentSPrice;
	
	private Double usSumPRSPrice;
	
	private Double usCountPRSPrice;
	
	private Double usAveragePRSPrice;
	
	private Double usLastHasPriceSumCurrentSPrice;
	
	private Integer usLastHasPriceCountCurrentSPrice;
	
	private Double usLastHasPriceAverageCurrentSPrice;
	
	private Date usLastHasPriceReferenceMonth;
	
	private Double usSumLastSPrice;
	
	private Integer usCountLastSPrice;
	
	private Double usAverageLastSPrice;
	
	private Double imputedQuotationPrice;
	
	private String imputedQuotationRemark;
	
	private Double imputedUnitPrice;
	
	private String imputedUnitRemark;
	
	private String ucDescription;
	
	private Double unitUomValue;
	
	private String cpiBasePeriod;
	
	private String unitCode;
	
	private String unitChineseName;
	
	private String unitEnglishName;
	
	private String qCPICompilationSeries;
	
	private boolean isOutlier;
	
	private String outlierRemark;
	
	private Date referenceDate;
	
	private Date qrReferenceMonth;
	
	private String uSeasonality;
	
	private String uCPIQoutationType;
	
	private String purposeName;
	
	private Integer compilationMethod;
	
	private boolean uIsFreshItem;
	
	private String indoorRemark;
	
	private String qOldFormBarSerial;
	
	private String qOldFormSequence;
	
	private boolean isProductChange;
	
	private boolean isNewProduct;
	
	private boolean isNewRecruitment;
	
	private Integer productGroupId;
	
	private String batchCode;
	
	private Boolean isCurrentPriceKeepNo;
	
	private Integer copyLastPriceType;
	
	private String staffCode;
	
	private boolean qIsICP;
	
	private String icpProductCode;
	
	private String icpProductName;
	
	private String icpType;
	
	private String cpiQuotationType;
	
	private Integer outletCode;
	
	private Integer productId;
	
	private Double usFinalPRSPrice;
	
	private String formType;
	
	private String quotationStatus;
	
	private String pricingMonth;
	
	private Boolean unitKeepPrice;

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

	public Date getIndoorReferenceMonth() {
		return indoorReferenceMonth;
	}

	public void setIndoorReferenceMonth(Date indoorReferenceMonth) {
		this.indoorReferenceMonth = indoorReferenceMonth;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public Integer getQuotationRecordSequence() {
		return quotationRecordSequence;
	}

	public void setQuotationRecordSequence(Integer quotationRecordSequence) {
		this.quotationRecordSequence = quotationRecordSequence;
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

	public Double getQsSumCurrentSPrice() {
		return qsSumCurrentSPrice;
	}

	public void setQsSumCurrentSPrice(Double qsSumCurrentSPrice) {
		this.qsSumCurrentSPrice = qsSumCurrentSPrice;
	}

	public Double getQsAverageCurrentSPrice() {
		return qsAverageCurrentSPrice;
	}

	public void setQsAverageCurrentSPrice(Double qsAverageCurrentSPrice) {
		this.qsAverageCurrentSPrice = qsAverageCurrentSPrice;
	}

	public Double getQsSumLastSPrice() {
		return qsSumLastSPrice;
	}

	public void setQsSumLastSPrice(Double qsSumLastSPrice) {
		this.qsSumLastSPrice = qsSumLastSPrice;
	}
	
	public Double getQsAverageLastSPrice() {
		return qsAverageLastSPrice;
	}

	public void setQsAverageLastSPrice(Double qsAverageLastSPrice) {
		this.qsAverageLastSPrice = qsAverageLastSPrice;
	}

	public Double getQsLastHasPriceSumCurrentSPrice() {
		return qsLastHasPriceSumCurrentSPrice;
	}

	public void setQsLastHasPriceSumCurrentSPrice(Double qsLastHasPriceSumCurrentSPrice) {
		this.qsLastHasPriceSumCurrentSPrice = qsLastHasPriceSumCurrentSPrice;
	}

	public Double getQsLastHasPriceAverageCurrentSPrice() {
		return qsLastHasPriceAverageCurrentSPrice;
	}

	public void setQsLastHasPriceAverageCurrentSPrice(Double qsLastHasPriceAverageCurrentSPrice) {
		this.qsLastHasPriceAverageCurrentSPrice = qsLastHasPriceAverageCurrentSPrice;
	}

	public Date getQsLastHasPriceReferenceMonth() {
		return qsLastHasPriceReferenceMonth;
	}

	public void setQsLastHasPriceReferenceMonth(Date qsLastHasPriceReferenceMonth) {
		this.qsLastHasPriceReferenceMonth = qsLastHasPriceReferenceMonth;
	}

	public Double getUsSumCurrentSPrice() {
		return usSumCurrentSPrice;
	}

	public void setUsSumCurrentSPrice(Double usSumCurrentSPrice) {
		this.usSumCurrentSPrice = usSumCurrentSPrice;
	}

	public Double getUsAverageCurrentSPrice() {
		return usAverageCurrentSPrice;
	}

	public void setUsAverageCurrentSPrice(Double usAverageCurrentSPrice) {
		this.usAverageCurrentSPrice = usAverageCurrentSPrice;
	}

	public Double getUsSumPRSPrice() {
		return usSumPRSPrice;
	}

	public void setUsSumPRSPrice(Double usSumPRSPrice) {
		this.usSumPRSPrice = usSumPRSPrice;
	}

	public Double getUsCountPRSPrice() {
		return usCountPRSPrice;
	}

	public void setUsCountPRSPrice(Double usCountPRSPrice) {
		this.usCountPRSPrice = usCountPRSPrice;
	}

	public Double getUsAveragePRSPrice() {
		return usAveragePRSPrice;
	}

	public void setUsAveragePRSPrice(Double usAveragePRSPrice) {
		this.usAveragePRSPrice = usAveragePRSPrice;
	}

	public Double getUsLastHasPriceSumCurrentSPrice() {
		return usLastHasPriceSumCurrentSPrice;
	}

	public void setUsLastHasPriceSumCurrentSPrice(Double usLastHasPriceSumCurrentSPrice) {
		this.usLastHasPriceSumCurrentSPrice = usLastHasPriceSumCurrentSPrice;
	}

	public Double getUsLastHasPriceAverageCurrentSPrice() {
		return usLastHasPriceAverageCurrentSPrice;
	}

	public void setUsLastHasPriceAverageCurrentSPrice(Double usLastHasPriceAverageCurrentSPrice) {
		this.usLastHasPriceAverageCurrentSPrice = usLastHasPriceAverageCurrentSPrice;
	}

	public Date getUsLastHasPriceReferenceMonth() {
		return usLastHasPriceReferenceMonth;
	}

	public void setUsLastHasPriceReferenceMonth(Date usLastHasPriceReferenceMonth) {
		this.usLastHasPriceReferenceMonth = usLastHasPriceReferenceMonth;
	}

	public Double getUsAverageLastSPrice() {
		return usAverageLastSPrice;
	}

	public void setUsAverageLastSPrice(Double usAverageLastSPrice) {
		this.usAverageLastSPrice = usAverageLastSPrice;
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

	public String getUcDescription() {
		return ucDescription;
	}

	public void setUcDescription(String ucDescription) {
		this.ucDescription = ucDescription;
	}

	public Double getUnitUomValue() {
		return unitUomValue;
	}

	public void setUnitUomValue(Double unitUomValue) {
		this.unitUomValue = unitUomValue;
	}

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
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

	public String getqCPICompilationSeries() {
		return qCPICompilationSeries;
	}

	public void setqCPICompilationSeries(String qCPICompilationSeries) {
		this.qCPICompilationSeries = qCPICompilationSeries;
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

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public Date getQrReferenceMonth() {
		return qrReferenceMonth;
	}

	public void setQrReferenceMonth(Date qrReferenceMonth) {
		this.qrReferenceMonth = qrReferenceMonth;
	}

	public String getuSeasonality() {
		return uSeasonality;
	}

	public void setuSeasonality(String uSeasonality) {
		this.uSeasonality = uSeasonality;
	}

	public String getuCPIQoutationType() {
		return uCPIQoutationType;
	}

	public void setuCPIQoutationType(String uCPIQoutationType) {
		this.uCPIQoutationType = uCPIQoutationType;
	}

	public String getPurposeName() {
		return purposeName;
	}

	public void setPurposeName(String purposeName) {
		this.purposeName = purposeName;
	}

	public Integer getCompilationMethod() {
		return compilationMethod;
	}

	public void setCompilationMethod(Integer compilationMethod) {
		this.compilationMethod = compilationMethod;
	}

	public boolean isuIsFreshItem() {
		return uIsFreshItem;
	}

	public void setuIsFreshItem(boolean uIsFreshItem) {
		this.uIsFreshItem = uIsFreshItem;
	}

	public String getIndoorRemark() {
		return indoorRemark;
	}

	public void setIndoorRemark(String indoorRemark) {
		this.indoorRemark = indoorRemark;
	}

	public String getqOldFormBarSerial() {
		return qOldFormBarSerial;
	}

	public void setqOldFormBarSerial(String qOldFormBarSerial) {
		this.qOldFormBarSerial = qOldFormBarSerial;
	}

	public String getqOldFormSequence() {
		return qOldFormSequence;
	}

	public void setqOldFormSequence(String qOldFormSequence) {
		this.qOldFormSequence = qOldFormSequence;
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

	public Integer getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(Integer productGroupId) {
		this.productGroupId = productGroupId;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public Boolean getIsCurrentPriceKeepNo() {
		return isCurrentPriceKeepNo;
	}

	public void setIsCurrentPriceKeepNo(Boolean isCurrentPriceKeepNo) {
		this.isCurrentPriceKeepNo = isCurrentPriceKeepNo;
	}

	public Integer getCopyLastPriceType() {
		return copyLastPriceType;
	}

	public void setCopyLastPriceType(Integer copyLastPriceType) {
		this.copyLastPriceType = copyLastPriceType;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public boolean isqIsICP() {
		return qIsICP;
	}

	public void setqIsICP(boolean qIsICP) {
		this.qIsICP = qIsICP;
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

	public String getCpiQuotationType() {
		return cpiQuotationType;
	}

	public void setCpiQuotationType(String cpiQuotationType) {
		this.cpiQuotationType = cpiQuotationType;
	}

	public Integer getQsCountCurrentSPrice() {
		return qsCountCurrentSPrice;
	}

	public void setQsCountCurrentSPrice(Integer qsCountCurrentSPrice) {
		this.qsCountCurrentSPrice = qsCountCurrentSPrice;
	}

	public Integer getQsCountLastSPrice() {
		return qsCountLastSPrice;
	}

	public void setQsCountLastSPrice(Integer qsCountLastSPrice) {
		this.qsCountLastSPrice = qsCountLastSPrice;
	}

	public Integer getQsLastHasPriceCountCurrentSPrice() {
		return qsLastHasPriceCountCurrentSPrice;
	}

	public void setQsLastHasPriceCountCurrentSPrice(Integer qsLastHasPriceCountCurrentSPrice) {
		this.qsLastHasPriceCountCurrentSPrice = qsLastHasPriceCountCurrentSPrice;
	}

	public Integer getUsCountCurrentSPrice() {
		return usCountCurrentSPrice;
	}

	public void setUsCountCurrentSPrice(Integer usCountCurrentSPrice) {
		this.usCountCurrentSPrice = usCountCurrentSPrice;
	}

	public Integer getUsLastHasPriceCountCurrentSPrice() {
		return usLastHasPriceCountCurrentSPrice;
	}

	public void setUsLastHasPriceCountCurrentSPrice(Integer usLastHasPriceCountCurrentSPrice) {
		this.usLastHasPriceCountCurrentSPrice = usLastHasPriceCountCurrentSPrice;
	}

	public Integer getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(Integer outletCode) {
		this.outletCode = outletCode;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Double getUsSumLastSPrice() {
		return usSumLastSPrice;
	}

	public void setUsSumLastSPrice(Double usSumLastSPrice) {
		this.usSumLastSPrice = usSumLastSPrice;
	}

	public Integer getUsCountLastSPrice() {
		return usCountLastSPrice;
	}

	public void setUsCountLastSPrice(Integer usCountLastSPrice) {
		this.usCountLastSPrice = usCountLastSPrice;
	}

	public Double getUsFinalPRSPrice() {
		return usFinalPRSPrice;
	}

	public void setUsFinalPRSPrice(Double usFinalPRSPrice) {
		this.usFinalPRSPrice = usFinalPRSPrice;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getQuotationStatus() {
		return quotationStatus;
	}

	public void setQuotationStatus(String quotationStatus) {
		this.quotationStatus = quotationStatus;
	}

	public String getPricingMonth() {
		return pricingMonth;
	}

	public void setPricingMonth(String pricingMonth) {
		this.pricingMonth = pricingMonth;
	}

	public Boolean getUnitKeepPrice() {
		return unitKeepPrice;
	}

	public void setUnitKeepPrice(Boolean unitKeepPrice) {
		this.unitKeepPrice = unitKeepPrice;
	}
	
}
