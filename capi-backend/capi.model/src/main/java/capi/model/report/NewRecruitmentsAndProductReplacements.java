package capi.model.report;

import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;

public class NewRecruitmentsAndProductReplacements {

	private Integer rowNum ;
	private Integer indoorQuotationRecordId ;
	private Integer fieldQuotationRecordId ;
	private Integer quotationId ;
	private String referenceMonth ;
	private String referenceDate ;
	private String purpose ;
	private String cpiBasePeriod ;
	private String varietyCode ;
	private String varietyEnglishName ;
	private String quotationStatus ;
	private String dataConversionStatus ;
	private String outletCode ;
	private String outletName ;
	private String outletType ;
	private String outletTypeEnglishName ;
	private Integer productId ;
	private String countryOfOrigin ;
	private String productAttributes1 ;
	private String productAttributes2 ;
	private String productAttributes3 ;
	private String productAttributes4 ;
	private String productAttributes5 ;
	private Double surveyNPrice ;
	private Double surveySPrice ;
	private Double lastEditedNPrice ;
	private Double lastEditedSPrice ;
	private Double previousEditedNPrice ;
	private Double previousEditedSPrice ;
	private Double currentEditedNPrice ;
	private Double currentEditedSPrice ;
	private BigDecimal nPricePr;
	private BigDecimal sPricePr;
	private String quotationRecordReason ;
	private String quotationRecordRemarks ;
	private String newRecruitmentCase ;
	private String productChange ;
	private String productRemarks ;
	private String dataConversionRemarks ;
	
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
	public Integer getFieldQuotationRecordId() {
		return fieldQuotationRecordId;
	}
	public void setFieldQuotationRecordId(Integer fieldQuotationRecordId) {
		this.fieldQuotationRecordId = fieldQuotationRecordId;
	}
	public Integer getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}
	public String getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public String getReferenceDate() {
		return referenceDate;
	}
	public void setReferenceDate(String referenceDate) {
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
	public String getVarietyCode() {
		return varietyCode;
	}
	public void setVarietyCode(String varietyCode) {
		this.varietyCode = varietyCode;
	}
	public String getVarietyEnglishName() {
		return varietyEnglishName;
	}
	public void setVarietyEnglishName(String varietyEnglishName) {
		this.varietyEnglishName = varietyEnglishName;
	}
	public String getQuotationStatus() {
		return quotationStatus;
	}
	public void setQuotationStatus(String quotationStatus) {
		this.quotationStatus = quotationStatus;
	}
	public String getDataConversionStatus() {
		return dataConversionStatus;
	}
	public void setDataConversionStatus(String dataConversionStatus) {
		this.dataConversionStatus = dataConversionStatus;
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
	public String getOutletType() {
		if(outletType != null) {
			//2018-01-01 cheung_cheng [MB9008]
			//1. Col. O "Outlet Type" should be "Outlet Type Code (last 3 digits) - Name" (the report show the wrong outlet type code)
			return StringUtils.right(outletType, 3);
		}
		return outletType;
	}
	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}
	public String getOutletTypeEnglishName() {
		return outletTypeEnglishName;
	}
	public void setOutletTypeEnglishName(String outletTypeEnglishName) {
		this.outletTypeEnglishName = outletTypeEnglishName;
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
	public String getProductAttributes1() {
		return productAttributes1;
	}
	public void setProductAttributes1(String productAttributes1) {
		this.productAttributes1 = productAttributes1;
	}
	public String getProductAttributes2() {
		return productAttributes2;
	}
	public void setProductAttributes2(String productAttributes2) {
		this.productAttributes2 = productAttributes2;
	}
	public String getProductAttributes3() {
		return productAttributes3;
	}
	public void setProductAttributes3(String productAttributes3) {
		this.productAttributes3 = productAttributes3;
	}
	public String getProductAttributes4() {
		return productAttributes4;
	}
	public void setProductAttributes4(String productAttributes4) {
		this.productAttributes4 = productAttributes4;
	}
	public String getProductAttributes5() {
		return productAttributes5;
	}
	public void setProductAttributes5(String productAttributes5) {
		this.productAttributes5 = productAttributes5;
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
	public Double getLastEditedNPrice() {
		return lastEditedNPrice;
	}
	public void setLastEditedNPrice(Double lastEditedNPrice) {
		this.lastEditedNPrice = lastEditedNPrice;
	}
	public Double getLastEditedSPrice() {
		return lastEditedSPrice;
	}
	public void setLastEditedSPrice(Double lastEditedSPrice) {
		this.lastEditedSPrice = lastEditedSPrice;
	}
	public Double getPreviousEditedNPrice() {
		return previousEditedNPrice;
	}
	public void setPreviousEditedNPrice(Double previousEditedNPrice) {
		this.previousEditedNPrice = previousEditedNPrice;
	}
	public Double getPreviousEditedSPrice() {
		return previousEditedSPrice;
	}
	public void setPreviousEditedSPrice(Double previousEditedSPrice) {
		this.previousEditedSPrice = previousEditedSPrice;
	}
	public Double getCurrentEditedNPrice() {
		return currentEditedNPrice;
	}
	public void setCurrentEditedNPrice(Double currentEditedNPrice) {
		this.currentEditedNPrice = currentEditedNPrice;
	}
	public Double getCurrentEditedSPrice() {
		return currentEditedSPrice;
	}
	public void setCurrentEditedSPrice(Double currentEditedSPrice) {
		this.currentEditedSPrice = currentEditedSPrice;
	}
	public void setnPricePr(BigDecimal nPricePr) {
		this.nPricePr = nPricePr;
	}
	public BigDecimal getnPricePr() {
		return nPricePr;
	}
	public void setsPricePr(BigDecimal sPricePr) {
		this.sPricePr = sPricePr;
	}
	public BigDecimal getsPricePr() {
		return sPricePr;
	}
	public String getQuotationRecordReason() {
		return quotationRecordReason;
	}
	public void setQuotationRecordReason(String quotationRecordReason) {
		this.quotationRecordReason = quotationRecordReason;
	}
	public String getQuotationRecordRemarks() {
		return quotationRecordRemarks;
	}
	public void setQuotationRecordRemarks(String quotationRecordRemarks) {
		this.quotationRecordRemarks = quotationRecordRemarks;
	}
	public String getNewRecruitmentCase() {
		return newRecruitmentCase;
	}
	public void setNewRecruitmentCase(String newRecruitmentCase) {
		this.newRecruitmentCase = newRecruitmentCase;
	}
	public String getProductChange() {
		return productChange;
	}
	public void setProductChange(String productChange) {
		this.productChange = productChange;
	}
	public String getProductRemarks() {
		return productRemarks;
	}
	public void setProductRemarks(String productRemarks) {
		this.productRemarks = productRemarks;
	}
	public String getDataConversionRemarks() {
		return dataConversionRemarks;
	}
	public void setDataConversionRemarks(String dataConversionRemarks) {
		this.dataConversionRemarks = dataConversionRemarks;
	}
}
