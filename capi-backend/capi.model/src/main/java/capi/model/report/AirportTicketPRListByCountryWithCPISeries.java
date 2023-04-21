package capi.model.report;

import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;

public class AirportTicketPRListByCountryWithCPISeries {

	private Integer no;
	private Integer indoorQuotationRecordId;
	private Integer fieldQuotationRecordId;
	private Integer quotationId;
	private String referenceMonth;
	private String referenceDate;
	private String purpose;
	private String cpiBasePeriod;
	private String cpiSeries;
	private String groupCode;
	private String groupEnglishName;
	private String itemCode;
	private String itemEnglishName;
	private String varietyCode;
	private String varietyEnglishName;
	private String quotationStatus;
	private String dataConversionStatus;
	private String outletCode;
	private String outletName;
	private String outletTypeCode;
	private String outletTypeEnglishName;
	private Integer productId;
	private String countryOfOrigin;
	private String productAttributes1;
	private String productAttributes2;
	private String productAttributes3;
	private String productAttributes4;
	private String productAttributes5;
	private String productAttributes6;
	private Integer availability;
	private Double surveyNPrice;
	private Double surveySPrice;
	private Double lastEditedNPrice;
	private Double lastEditedSPrice;
	private Double previousEditedNPrice;
	private Double previousEditedSPrice;
	private Double currentEditedNPrice;
	private Double currentEditedSPrice;
	private BigDecimal nPricePr;
	private BigDecimal sPricePr;
	private String priceReason;
	private BigDecimal sPricePrAggregated;
	private String keepNumber;
	private String whetherFieldworkIsNeeded;
	private String dataConversionRemarks;
	private Integer noOfRecordByVariety;
	private Integer totalNoOfRecordByCpiSeries;
	
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
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
	public String getCpiSeries() {
		return cpiSeries;
	}
	public void setCpiSeries(String cpiSeries) {
		this.cpiSeries = cpiSeries;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getGroupEnglishName() {
		return groupEnglishName;
	}
	public void setGroupEnglishName(String groupEnglishName) {
		this.groupEnglishName = groupEnglishName;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemEnglishName() {
		return itemEnglishName;
	}
	public void setItemEnglishName(String itemEnglishName) {
		this.itemEnglishName = itemEnglishName;
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
	//2018-01-04 cheung_cheng [MB9010]"Outlet Type" should be "Outlet Type Code (last 3 digits) - Name" 
	public String getOutletTypeCode() {
		if(outletTypeCode != null) {
			return StringUtils.right(outletTypeCode, 3);
		}
		return outletTypeCode;
	}
	public void setOutletTypeEnglishName(String outletTypeEnglishName) {
		this.outletTypeEnglishName = outletTypeEnglishName;
	}
	public String getOutletTypeEnglishName() {
		return outletTypeEnglishName;
	}
	public void setOutletType(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
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
	public String getProductAttributes6() {
		return productAttributes6;
	}
	public void setProductAttributes6(String productAttributes6) {
		this.productAttributes6 = productAttributes6;
	}
	public Integer getAvailability() {
		return availability;
	}
	public void setAvailability(Integer availability) {
		this.availability = availability;
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
	public BigDecimal getnPricePr() {
		return nPricePr;
	}
	public void setnPricePr(BigDecimal nPricePr) {
		this.nPricePr = nPricePr;
	}
	public BigDecimal getsPricePr() {
		return sPricePr;
	}
	public void setsPricePr(BigDecimal sPricePr) {
		this.sPricePr = sPricePr;
	}
	public String getPriceReason() {
		return priceReason;
	}
	public void setPriceReason(String priceReason) {
		this.priceReason = priceReason;
	}
	public BigDecimal getsPricePrAggregated() {
		return sPricePrAggregated;
	}
	public void setsPricePrAggregated(BigDecimal sPricePrAggregated) {
		this.sPricePrAggregated = sPricePrAggregated;
	}
	public String getKeepNumber() {
		return keepNumber;
	}
	public void setKeepNumber(String keepNumber) {
		this.keepNumber = keepNumber;
	}
	public String getWhetherFieldworkIsNeeded() {
		return whetherFieldworkIsNeeded;
	}
	public void setWhetherFieldworkIsNeeded(String whetherFieldworkIsNeeded) {
		this.whetherFieldworkIsNeeded = whetherFieldworkIsNeeded;
	}
	public String getDataConversionRemarks() {
		return dataConversionRemarks;
	}
	public void setDataConversionRemarks(String dataConversionRemarks) {
		this.dataConversionRemarks = dataConversionRemarks;
	}
	public Integer getNoOfRecordByVariety() {
		return noOfRecordByVariety;
	}
	public void setNoOfRecordByVariety(Integer noOfRecordByVariety) {
		this.noOfRecordByVariety = noOfRecordByVariety;
	}
	public Integer getTotalNoOfRecordByCpiSeries() {
		return totalNoOfRecordByCpiSeries;
	}
	public void setTotalNoOfRecordByCpiSeries(Integer totalNoOfRecordByCpiSeries) {
		this.totalNoOfRecordByCpiSeries = totalNoOfRecordByCpiSeries;
	}
	
}
