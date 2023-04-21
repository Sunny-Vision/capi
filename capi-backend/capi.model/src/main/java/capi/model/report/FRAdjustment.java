package capi.model.report;

import java.util.Date;

public class FRAdjustment {

	private String indoorQuotationRecordId;

	private String quotationRecordId;

	private String quotationId;

	private Date referenceMonth;

	private Date referenceDate;

	private String purpose;

	private String cpiBasePeriod;

	private String unitCode;

	private String unitEnglishName;

	private String quotationStatus;

	private String indoorQuotationRecordStatus;

	private String outletCode;

	private String outletName;

	private String outletTypeCode;

	private String productId;

	private String countryOfOrigin;

	private String productAttr1;

	private String productAttr2;

	private String productAttr3;

	private String productAttr4;

	private String productAttr5;

	private Double surveyNPrice;

	private Double surveySPrice;

	private Double lastNPrice;

	private Double lastSPrice;

	private Double previousNPrice;

	private Double previousSPrice;

	private Double currentNPrice;

	private Double currentSPrice;

	private String qrReason;

	private String qrRemark;

	private Integer isNewRecruitment;
	
	private Integer isProductChange;
	
	private String iqrRemark;
	
	private Double FRField;

	private Integer isFRFieldPercentage;
	
	private Double FRAdmin;
	
	private Integer isFRAdminPercentage;
	
	private Double FR;

	private Integer isFRPercentage;
	
	private Integer isConsignmentCounter;
	
	private String consignmentCounterName;
	
	private String consignmentCounterRemark;
	
	private Integer isApplyFR;
	
	private Integer isFRApplied;

	private String isReturnNewGoods;
	
	private Integer isKeepNum;
	
	private String seasonalWithdraw;
	
	private String seasonality;
	
	private Integer isUseFRAdmin;

	private Date lastFRAppliedDate;
	
	private Integer isNoField;

	public String getIndoorQuotationRecordId() {
		return indoorQuotationRecordId;
	}

	public void setIndoorQuotationRecordId(String indoorQuotationRecordId) {
		this.indoorQuotationRecordId = indoorQuotationRecordId;
	}

	public String getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(String quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public String getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
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

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitEnglishName() {
		return unitEnglishName;
	}

	public void setUnitEnglishName(String unitEnglishName) {
		this.unitEnglishName = unitEnglishName;
	}

	public String getQuotationStatus() {
		return quotationStatus;
	}

	public void setQuotationStatus(String quotationStatus) {
		this.quotationStatus = quotationStatus;
	}

	public String getIndoorQuotationRecordStatus() {
		return indoorQuotationRecordStatus;
	}

	public void setIndoorQuotationRecordStatus(String indoorQuotationRecordStatus) {
		this.indoorQuotationRecordStatus = indoorQuotationRecordStatus;
	}

	public String getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(String outletCode) {
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public String getProductAttr1() {
		return productAttr1;
	}

	public void setProductAttr1(String productAttr1) {
		this.productAttr1 = productAttr1;
	}

	public String getProductAttr2() {
		return productAttr2;
	}

	public void setProductAttr2(String productAttr2) {
		this.productAttr2 = productAttr2;
	}

	public String getProductAttr3() {
		return productAttr3;
	}

	public void setProductAttr3(String productAttr3) {
		this.productAttr3 = productAttr3;
	}

	public String getProductAttr4() {
		return productAttr4;
	}

	public void setProductAttr4(String productAttr4) {
		this.productAttr4 = productAttr4;
	}

	public String getProductAttr5() {
		return productAttr5;
	}

	public void setProductAttr5(String productAttr5) {
		this.productAttr5 = productAttr5;
	}

	public Double getSurveyNPrice() {
		return surveyNPrice;
	}

	public void setSurveyNPrice(Double surveyNPrice) {
		this.surveyNPrice = surveyNPrice;
	}

	public Double getSurveySPrice() {
		return surveySPrice;
	}

	public void setSurveySPrice(Double surveySPrice) {
		this.surveySPrice = surveySPrice;
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

	public String getQrReason() {
		return qrReason;
	}

	public void setQrReason(String qrReason) {
		this.qrReason = qrReason;
	}

	public String getQrRemark() {
		return qrRemark;
	}

	public void setQrRemark(String qrRemark) {
		this.qrRemark = qrRemark;
	}

	public Integer getIsNewRecruitment() {
		return isNewRecruitment;
	}

	public void setIsNewRecruitment(Integer isNewRecruitment) {
		this.isNewRecruitment = isNewRecruitment;
	}

	public Integer getIsProductChange() {
		return isProductChange;
	}

	public void setIsProductChange(Integer isProductChange) {
		this.isProductChange = isProductChange;
	}

	public String getIqrRemark() {
		return iqrRemark;
	}

	public void setIqrRemark(String iqrRemark) {
		this.iqrRemark = iqrRemark;
	}

	public Double getFRField() {
		return FRField;
	}

	public void setFRField(Double fRField) {
		FRField = fRField;
	}

	public Integer getIsFRFieldPercentage() {
		return isFRFieldPercentage;
	}

	public void setIsFRFieldPercentage(Integer isFRFieldPercentage) {
		this.isFRFieldPercentage = isFRFieldPercentage;
	}

	public Double getFRAdmin() {
		return FRAdmin;
	}

	public void setFRAdmin(Double fRAdmin) {
		FRAdmin = fRAdmin;
	}

	public Integer getIsFRAdminPercentage() {
		return isFRAdminPercentage;
	}

	public void setIsFRAdminPercentage(Integer isFRAdminPercentage) {
		this.isFRAdminPercentage = isFRAdminPercentage;
	}

	public Double getFR() {
		return FR;
	}

	public void setFR(Double fR) {
		FR = fR;
	}

	public Integer getIsFRPercentage() {
		return isFRPercentage;
	}

	public void setIsFRPercentage(Integer isFRPercentage) {
		this.isFRPercentage = isFRPercentage;
	}

	public Integer getIsConsignmentCounter() {
		return isConsignmentCounter;
	}

	public void setIsConsignmentCounter(Integer isConsignmentCounter) {
		this.isConsignmentCounter = isConsignmentCounter;
	}

	public String getConsignmentCounterName() {
		return consignmentCounterName;
	}

	public void setConsignmentCounterName(String consignmentCounterName) {
		this.consignmentCounterName = consignmentCounterName;
	}

	public String getConsignmentCounterRemark() {
		return consignmentCounterRemark;
	}

	public void setConsignmentCounterRemark(String consignmentCounterRemark) {
		this.consignmentCounterRemark = consignmentCounterRemark;
	}

	public Integer getIsApplyFR() {
		return isApplyFR;
	}

	public void setIsApplyFR(Integer isApplyFR) {
		this.isApplyFR = isApplyFR;
	}

	public Integer getIsFRApplied() {
		return isFRApplied;
	}

	public void setIsFRApplied(Integer isFRApplied) {
		this.isFRApplied = isFRApplied;
	}

	public String getIsReturnNewGoods() {
		return isReturnNewGoods;
	}

	public void setIsReturnNewGoods(String isReturnNewGoods) {
		this.isReturnNewGoods = isReturnNewGoods;
	}

	public Integer getIsKeepNum() {
		return isKeepNum;
	}

	public void setIsKeepNum(Integer isKeepNum) {
		this.isKeepNum = isKeepNum;
	}

	public String getSeasonalWithdraw() {
		return seasonalWithdraw;
	}

	public void setSeasonalWithdraw(String seasonalWithdraw) {
		this.seasonalWithdraw = seasonalWithdraw;
	}

	public String getSeasonality() {
		return seasonality;
	}

	public void setSeasonality(String seasonality) {
		this.seasonality = seasonality;
	}

	public Integer getIsUseFRAdmin() {
		return isUseFRAdmin;
	}

	public void setIsUseFRAdmin(Integer isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}

	public Date getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}

	public void setLastFRAppliedDate(Date lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
	}

	public Integer getIsNoField() {
		return isNoField;
	}

	public void setIsNoField(Integer isNoField) {
		this.isNoField = isNoField;
	}
}
