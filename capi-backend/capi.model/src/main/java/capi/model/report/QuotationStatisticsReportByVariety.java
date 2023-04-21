package capi.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class QuotationStatisticsReportByVariety {

	private Date referenceMonth;
	//private Integer quotationId;
	private String quotationId;
	private Date referenceDate;
	private String purpose;
	private String cpiBasePeriod;
	private String varietyCode;
	private String chineseName;
	private String englishName;
	//private Integer outletCode;
	private String outletCode;
	private String outletName;
	private String outletType;
	private Double nPriceDataCollection1;
	private Double sPriceDataCollection1;
	private Double nPriceDataCollection2;
	private Double sPriceDataCollection2;	
	private Double nPriceDataCollection3;
	private Double sPriceDataCollection3;	
	private Double nPriceDataCollection4;
	private Double sPriceDataCollection4;	
	private Double nPriceDataCollection5;
	private Double sPriceDataCollection5;	
	private Double nPriceDataCollection6;
	private Double sPriceDataCollection6;	
	private Double nPriceDataCollection7;
	private Double sPriceDataCollection7;	
	private Double nPriceDataCollection8;
	private Double sPriceDataCollection8;	
	private Double nPriceDataCollection9;
	private Double sPriceDataCollection9;	
	private Double nPriceDataCollection10;
	private Double sPriceDataCollection10;	
	private Double nPriceDataCollection11;
	private Double sPriceDataCollection11;	
	private Double nPriceDataCollection12;
	private Double sPriceDataCollection12;	
	private Double nPriceDataCollection13;
	private Double sPriceDataCollection13;	
	private Double nPriceDataCollection14;
	private Double sPriceDataCollection14;	
	private Double nPriceDataCollection15;
	private Double sPriceDataCollection15;	
	private Double nPriceDataCollection16;
	private Double sPriceDataCollection16;	
	private Double nPriceDataCollection17;
	private Double sPriceDataCollection17;	
	private Double nPriceDataCollection18;
	private Double sPriceDataCollection18;
	
	//v15
	private Double averagePRSPrice;
	private Double finalPRSPrice;
	//private Double dFinalPRSPrice;
	
	private ArrayList<Double> sPriceDataCollection;
	private ArrayList<Double> nPriceDataCollection;
	
	private String cpiCompilationSeries;
	private Integer compilationMethod;
	//private Integer seasonalityItem;
	private String seasonalityItem;
	private Integer surveyType;
	private String quotationStatus;
	private Double quotationAveragePriceT1;
	private Double quotationAveragePriceT;
	private Double quotationStandardDeviationT1;
	private Double quotationStandardDeviationT;	
	private Double quotationMinPriceT1;
	private Double quotationMinPriceT;	
	private Double quotationMaxPriceT1;
	private Double quotationMaxPriceT;
	private Double quotationSumT1;
	private Double quotationSumT;
	private Integer quotationCountT1;
	private Integer quotationCountT;
	private Double varietyAveragePriceT1;
	private Double varietyAveragePriceT;
	private Double varietyStandardDeviationT1;
	private Double varietyStandardDeviationT;
	private Double varietyMinPriceT1;
	private Double varietyMinPriceT;
	private Double varietyMaxPriceT1;
	private Double varietyMaxPriceT;
	private Double varietySumT1;
	private Double varietySumT;
	private Integer varietyCountT1;
	private Integer varietyCountT;
	private Double varietyPriceRelativeT1;
	private Double varietyPriceRelativeT;	
	private Double varietyCountPRT1;
	private Double varietyCountPRT;
	private BigDecimal sumNPrice;
	private BigDecimal sumSPrice;
	private Integer countNPrice;
	private Integer countSPrice;
	private BigDecimal currentNAverage;
	private BigDecimal currentSAverage;
	private BigDecimal lastNAverage;
	private BigDecimal lastSAverage;
	private BigDecimal keepNAverage;
	private BigDecimal keepSAverage;
	private BigDecimal prNprice;
	private BigDecimal prSprice;
	private String cpiSeries;
	private Integer cpiCompilationMethod;
	private BigDecimal unitSum;
	private Integer unitCount;
	private BigDecimal unitCurrentAverage;
	private BigDecimal unitLastAverage;
	private BigDecimal unitKeepAverage;
	private BigDecimal unitAveragePR;
	private BigDecimal untiAverageStdDev;
	private BigDecimal unitMinSPrice;
	private BigDecimal unitMinNPrice;
	private BigDecimal unitMaxSPrice;
	private BigDecimal unitMaxNPrice;
	//private BigDecimal finalPRSPrice;
	
	public Date getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public String getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
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
	public String getVarietyCode() {
		return varietyCode;
	}
	public void setVarietyCode(String varietyCode) {
		this.varietyCode = varietyCode;
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
	/**
	 * @return the outletType
	 */
	public String getOutletType() {
		return outletType;
	}
	/**
	 * @param outletType the outletType to set
	 */
	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}
	public Double getnPriceDataCollection1() {
		return nPriceDataCollection1;
	}
	public void setnPriceDataCollection1(Double nPriceDataCollection1) {
		this.nPriceDataCollection1 = nPriceDataCollection1;
	}
	public Double getsPriceDataCollection1() {
		return sPriceDataCollection1;
	}
	public void setsPriceDataCollection1(Double sPriceDataCollection1) {
		this.sPriceDataCollection1 = sPriceDataCollection1;
	}
	public Double getnPriceDataCollection2() {
		return nPriceDataCollection2;
	}
	public void setnPriceDataCollection2(Double nPriceDataCollection2) {
		this.nPriceDataCollection2 = nPriceDataCollection2;
	}
	public Double getsPriceDataCollection2() {
		return sPriceDataCollection2;
	}
	public void setsPriceDataCollection2(Double sPriceDataCollection2) {
		this.sPriceDataCollection2 = sPriceDataCollection2;
	}
	public Double getnPriceDataCollection3() {
		return nPriceDataCollection3;
	}
	public void setnPriceDataCollection3(Double nPriceDataCollection3) {
		this.nPriceDataCollection3 = nPriceDataCollection3;
	}
	public Double getsPriceDataCollection3() {
		return sPriceDataCollection3;
	}
	public void setsPriceDataCollection3(Double sPriceDataCollection3) {
		this.sPriceDataCollection3 = sPriceDataCollection3;
	}
	public Double getnPriceDataCollection4() {
		return nPriceDataCollection4;
	}
	public void setnPriceDataCollection4(Double nPriceDataCollection4) {
		this.nPriceDataCollection4 = nPriceDataCollection4;
	}
	public Double getsPriceDataCollection4() {
		return sPriceDataCollection4;
	}
	public void setsPriceDataCollection4(Double sPriceDataCollection4) {
		this.sPriceDataCollection4 = sPriceDataCollection4;
	}
	public Double getnPriceDataCollection5() {
		return nPriceDataCollection5;
	}
	public void setnPriceDataCollection5(Double nPriceDataCollection5) {
		this.nPriceDataCollection5 = nPriceDataCollection5;
	}
	public Double getsPriceDataCollection5() {
		return sPriceDataCollection5;
	}
	public void setsPriceDataCollection5(Double sPriceDataCollection5) {
		this.sPriceDataCollection5 = sPriceDataCollection5;
	}
	public Double getnPriceDataCollection6() {
		return nPriceDataCollection6;
	}
	public void setnPriceDataCollection6(Double nPriceDataCollection6) {
		this.nPriceDataCollection6 = nPriceDataCollection6;
	}
	public Double getsPriceDataCollection6() {
		return sPriceDataCollection6;
	}
	public void setsPriceDataCollection6(Double sPriceDataCollection6) {
		this.sPriceDataCollection6 = sPriceDataCollection6;
	}
	public Double getnPriceDataCollection7() {
		return nPriceDataCollection7;
	}
	public void setnPriceDataCollection7(Double nPriceDataCollection7) {
		this.nPriceDataCollection7 = nPriceDataCollection7;
	}
	public Double getsPriceDataCollection7() {
		return sPriceDataCollection7;
	}
	public void setsPriceDataCollection7(Double sPriceDataCollection7) {
		this.sPriceDataCollection7 = sPriceDataCollection7;
	}
	public Double getnPriceDataCollection8() {
		return nPriceDataCollection8;
	}
	public void setnPriceDataCollection8(Double nPriceDataCollection8) {
		this.nPriceDataCollection8 = nPriceDataCollection8;
	}
	public Double getsPriceDataCollection8() {
		return sPriceDataCollection8;
	}
	public void setsPriceDataCollection8(Double sPriceDataCollection8) {
		this.sPriceDataCollection8 = sPriceDataCollection8;
	}
	public Double getnPriceDataCollection9() {
		return nPriceDataCollection9;
	}
	public void setnPriceDataCollection9(Double nPriceDataCollection9) {
		this.nPriceDataCollection9 = nPriceDataCollection9;
	}
	public Double getsPriceDataCollection9() {
		return sPriceDataCollection9;
	}
	public void setsPriceDataCollection9(Double sPriceDataCollection9) {
		this.sPriceDataCollection9 = sPriceDataCollection9;
	}
	public Double getnPriceDataCollection10() {
		return nPriceDataCollection10;
	}
	public void setnPriceDataCollection10(Double nPriceDataCollection10) {
		this.nPriceDataCollection10 = nPriceDataCollection10;
	}
	public Double getsPriceDataCollection10() {
		return sPriceDataCollection10;
	}
	public void setsPriceDataCollection10(Double sPriceDataCollection10) {
		this.sPriceDataCollection10 = sPriceDataCollection10;
	}
	public Double getnPriceDataCollection11() {
		return nPriceDataCollection11;
	}
	public void setnPriceDataCollection11(Double nPriceDataCollection11) {
		this.nPriceDataCollection11 = nPriceDataCollection11;
	}
	public Double getsPriceDataCollection11() {
		return sPriceDataCollection11;
	}
	public void setsPriceDataCollection11(Double sPriceDataCollection11) {
		this.sPriceDataCollection11 = sPriceDataCollection11;
	}
	public Double getnPriceDataCollection12() {
		return nPriceDataCollection12;
	}
	public void setnPriceDataCollection12(Double nPriceDataCollection12) {
		this.nPriceDataCollection12 = nPriceDataCollection12;
	}
	public Double getsPriceDataCollection12() {
		return sPriceDataCollection12;
	}
	public void setsPriceDataCollection12(Double sPriceDataCollection12) {
		this.sPriceDataCollection12 = sPriceDataCollection12;
	}
	public Double getnPriceDataCollection13() {
		return nPriceDataCollection13;
	}
	public void setnPriceDataCollection13(Double nPriceDataCollection13) {
		this.nPriceDataCollection13 = nPriceDataCollection13;
	}
	public Double getsPriceDataCollection13() {
		return sPriceDataCollection13;
	}
	public void setsPriceDataCollection13(Double sPriceDataCollection13) {
		this.sPriceDataCollection13 = sPriceDataCollection13;
	}
	public Double getnPriceDataCollection14() {
		return nPriceDataCollection14;
	}
	public void setnPriceDataCollection14(Double nPriceDataCollection14) {
		this.nPriceDataCollection14 = nPriceDataCollection14;
	}
	public Double getsPriceDataCollection14() {
		return sPriceDataCollection14;
	}
	public void setsPriceDataCollection14(Double sPriceDataCollection14) {
		this.sPriceDataCollection14 = sPriceDataCollection14;
	}
	public Double getnPriceDataCollection15() {
		return nPriceDataCollection15;
	}
	public void setnPriceDataCollection15(Double nPriceDataCollection15) {
		this.nPriceDataCollection15 = nPriceDataCollection15;
	}
	public Double getsPriceDataCollection15() {
		return sPriceDataCollection15;
	}
	public void setsPriceDataCollection15(Double sPriceDataCollection15) {
		this.sPriceDataCollection15 = sPriceDataCollection15;
	}
	public Double getnPriceDataCollection16() {
		return nPriceDataCollection16;
	}
	public void setnPriceDataCollection16(Double nPriceDataCollection16) {
		this.nPriceDataCollection16 = nPriceDataCollection16;
	}
	public Double getsPriceDataCollection16() {
		return sPriceDataCollection16;
	}
	public void setsPriceDataCollection16(Double sPriceDataCollection16) {
		this.sPriceDataCollection16 = sPriceDataCollection16;
	}
	public Double getnPriceDataCollection17() {
		return nPriceDataCollection17;
	}
	public void setnPriceDataCollection17(Double nPriceDataCollection17) {
		this.nPriceDataCollection17 = nPriceDataCollection17;
	}
	public Double getsPriceDataCollection17() {
		return sPriceDataCollection17;
	}
	public void setsPriceDataCollection17(Double sPriceDataCollection17) {
		this.sPriceDataCollection17 = sPriceDataCollection17;
	}
	public Double getnPriceDataCollection18() {
		return nPriceDataCollection18;
	}
	public void setnPriceDataCollection18(Double nPriceDataCollection18) {
		this.nPriceDataCollection18 = nPriceDataCollection18;
	}
	public Double getsPriceDataCollection18() {
		return sPriceDataCollection18;
	}
	public void setsPriceDataCollection18(Double sPriceDataCollection18) {
		this.sPriceDataCollection18 = sPriceDataCollection18;
	}
	/**
	 * @return the cpiCompilationSeries
	 */
	public String getCpiCompilationSeries() {
		return cpiCompilationSeries;
	}
	/**
	 * @param cpiCompilationSeries the cpiCompilationSeries to set
	 */
	public void setCpiCompilationSeries(String cpiCompilationSeries) {
		this.cpiCompilationSeries = cpiCompilationSeries;
	}
	/**
	 * @return the compilationMethod
	 */
	public Integer getCompilationMethod() {
		return compilationMethod;
	}
	/**
	 * @param compilationMethod the compilationMethod to set
	 */
	public void setCompilationMethod(Integer compilationMethod) {
		this.compilationMethod = compilationMethod;
	}
	/**
	 * @return the seasonalityItem
	 */
	public String getSeasonalityItem() {
		return seasonalityItem;
	}
	/**
	 * @param seasonalityItem the seasonalityItem to set
	 */
	public void setSeasonalityItem(String seasonalityItem) {
		this.seasonalityItem = seasonalityItem;
	}
	/**
	 * @return the surveyType
	 */
	public Integer getSurveyType() {
		return surveyType;
	}
	/**
	 * @param surveyType the surveyType to set
	 */
	public void setSurveyType(Integer surveyType) {
		this.surveyType = surveyType;
	}
	/**
	 * @return the quotationStatus
	 */
	public String getQuotationStatus() {
		return quotationStatus;
	}
	/**
	 * @param quotationStatus the quotationStatus to set
	 */
	public void setQuotationStatus(String quotationStatus) {
		this.quotationStatus = quotationStatus;
	}
	/**
	 * @return the quotationAveragePriceT1
	 */
	public Double getQuotationAveragePriceT1() {
		return quotationAveragePriceT1;
	}
	/**
	 * @param quotationAveragePriceT1 the quotationAveragePriceT1 to set
	 */
	public void setQuotationAveragePriceT1(Double quotationAveragePriceT1) {
		this.quotationAveragePriceT1 = quotationAveragePriceT1;
	}
	/**
	 * @return the quotationAveragePriceT
	 */
	public Double getQuotationAveragePriceT() {
		return quotationAveragePriceT;
	}
	/**
	 * @param quotationAveragePriceT the quotationAveragePriceT to set
	 */
	public void setQuotationAveragePriceT(Double quotationAveragePriceT) {
		this.quotationAveragePriceT = quotationAveragePriceT;
	}
	/**
	 * @return the quotationStandardDeviationT1
	 */
	public Double getQuotationStandardDeviationT1() {
		return quotationStandardDeviationT1;
	}
	/**
	 * @param quotationStandardDeviationT1 the quotationStandardDeviationT1 to set
	 */
	public void setQuotationStandardDeviationT1(Double quotationStandardDeviationT1) {
		this.quotationStandardDeviationT1 = quotationStandardDeviationT1;
	}
	/**
	 * @return the quotationStandardDeviationT
	 */
	public Double getQuotationStandardDeviationT() {
		return quotationStandardDeviationT;
	}
	/**
	 * @param quotationStandardDeviationT the quotationStandardDeviationT to set
	 */
	public void setQuotationStandardDeviationT(Double quotationStandardDeviationT) {
		this.quotationStandardDeviationT = quotationStandardDeviationT;
	}
	/**
	 * @return the quotationMinPriceT1
	 */
	public Double getQuotationMinPriceT1() {
		return quotationMinPriceT1;
	}
	/**
	 * @param quotationMinPriceT1 the quotationMinPriceT1 to set
	 */
	public void setQuotationMinPriceT1(Double quotationMinPriceT1) {
		this.quotationMinPriceT1 = quotationMinPriceT1;
	}
	/**
	 * @return the quotationMinPriceT
	 */
	public Double getQuotationMinPriceT() {
		return quotationMinPriceT;
	}
	/**
	 * @param quotationMinPriceT the quotationMinPriceT to set
	 */
	public void setQuotationMinPriceT(Double quotationMinPriceT) {
		this.quotationMinPriceT = quotationMinPriceT;
	}
	/**
	 * @return the quotationMaxPriceT1
	 */
	public Double getQuotationMaxPriceT1() {
		return quotationMaxPriceT1;
	}
	/**
	 * @param quotationMaxPriceT1 the quotationMaxPriceT1 to set
	 */
	public void setQuotationMaxPriceT1(Double quotationMaxPriceT1) {
		this.quotationMaxPriceT1 = quotationMaxPriceT1;
	}
	/**
	 * @return the quotationMaxPriceT
	 */
	public Double getQuotationMaxPriceT() {
		return quotationMaxPriceT;
	}
	/**
	 * @param quotationMaxPriceT the quotationMaxPriceT to set
	 */
	public void setQuotationMaxPriceT(Double quotationMaxPriceT) {
		this.quotationMaxPriceT = quotationMaxPriceT;
	}
	/**
	 * @return the quotationSumT1
	 */
	public Double getQuotationSumT1() {
		return quotationSumT1;
	}
	/**
	 * @param quotationSumT1 the quotationSumT1 to set
	 */
	public void setQuotationSumT1(Double quotationSumT1) {
		this.quotationSumT1 = quotationSumT1;
	}
	/**
	 * @return the quotationSumT
	 */
	public Double getQuotationSumT() {
		return quotationSumT;
	}
	/**
	 * @param quotationSumT the quotationSumT to set
	 */
	public void setQuotationSumT(Double quotationSumT) {
		this.quotationSumT = quotationSumT;
	}
	/**
	 * @return the quotationCountT1
	 */
	public Integer getQuotationCountT1() {
		return quotationCountT1;
	}
	/**
	 * @param quotationCountT1 the quotationCountT1 to set
	 */
	public void setQuotationCountT1(Integer quotationCountT1) {
		this.quotationCountT1 = quotationCountT1;
	}
	/**
	 * @return the quotationCountT
	 */
	public Integer getQuotationCountT() {
		return quotationCountT;
	}
	/**
	 * @param quotationCountT the quotationCountT to set
	 */
	public void setQuotationCountT(Integer quotationCountT) {
		this.quotationCountT = quotationCountT;
	}
	/**
	 * @return the varietyAveragePriceT1
	 */
	public Double getVarietyAveragePriceT1() {
		return varietyAveragePriceT1;
	}
	/**
	 * @param varietyAveragePriceT1 the varietyAveragePriceT1 to set
	 */
	public void setVarietyAveragePriceT1(Double varietyAveragePriceT1) {
		this.varietyAveragePriceT1 = varietyAveragePriceT1;
	}
	/**
	 * @return the varietyAveragePriceT
	 */
	public Double getVarietyAveragePriceT() {
		return varietyAveragePriceT;
	}
	/**
	 * @param varietyAveragePriceT the varietyAveragePriceT to set
	 */
	public void setVarietyAveragePriceT(Double varietyAveragePriceT) {
		this.varietyAveragePriceT = varietyAveragePriceT;
	}
	/**
	 * @return the varietyStandardDeviationT1
	 */
	public Double getVarietyStandardDeviationT1() {
		return varietyStandardDeviationT1;
	}
	/**
	 * @param varietyStandardDeviationT1 the varietyStandardDeviationT1 to set
	 */
	public void setVarietyStandardDeviationT1(Double varietyStandardDeviationT1) {
		this.varietyStandardDeviationT1 = varietyStandardDeviationT1;
	}
	/**
	 * @return the varietyStandardDeviationT
	 */
	public Double getVarietyStandardDeviationT() {
		return varietyStandardDeviationT;
	}
	/**
	 * @param varietyStandardDeviationT the varietyStandardDeviationT to set
	 */
	public void setVarietyStandardDeviationT(Double varietyStandardDeviationT) {
		this.varietyStandardDeviationT = varietyStandardDeviationT;
	}
	/**
	 * @return the varietyMinPriceT1
	 */
	public Double getVarietyMinPriceT1() {
		return varietyMinPriceT1;
	}
	/**
	 * @param varietyMinPriceT1 the varietyMinPriceT1 to set
	 */
	public void setVarietyMinPriceT1(Double varietyMinPriceT1) {
		this.varietyMinPriceT1 = varietyMinPriceT1;
	}
	/**
	 * @return the varietyMinPriceT
	 */
	public Double getVarietyMinPriceT() {
		return varietyMinPriceT;
	}
	/**
	 * @param varietyMinPriceT the varietyMinPriceT to set
	 */
	public void setVarietyMinPriceT(Double varietyMinPriceT) {
		this.varietyMinPriceT = varietyMinPriceT;
	}
	/**
	 * @return the varietyMaxPriceT1
	 */
	public Double getVarietyMaxPriceT1() {
		return varietyMaxPriceT1;
	}
	/**
	 * @param varietyMaxPriceT1 the varietyMaxPriceT1 to set
	 */
	public void setVarietyMaxPriceT1(Double varietyMaxPriceT1) {
		this.varietyMaxPriceT1 = varietyMaxPriceT1;
	}
	/**
	 * @return the varietyMaxPriceT
	 */
	public Double getVarietyMaxPriceT() {
		return varietyMaxPriceT;
	}
	/**
	 * @param varietyMaxPriceT the varietyMaxPriceT to set
	 */
	public void setVarietyMaxPriceT(Double varietyMaxPriceT) {
		this.varietyMaxPriceT = varietyMaxPriceT;
	}
	/**
	 * @return the varietySumT1
	 */
	public Double getVarietySumT1() {
		return varietySumT1;
	}
	/**
	 * @param varietySumT1 the varietySumT1 to set
	 */
	public void setVarietySumT1(Double varietySumT1) {
		this.varietySumT1 = varietySumT1;
	}
	/**
	 * @return the varietySumT
	 */
	public Double getVarietySumT() {
		return varietySumT;
	}
	/**
	 * @param varietySumT the varietySumT to set
	 */
	public void setVarietySumT(Double varietySumT) {
		this.varietySumT = varietySumT;
	}
	/**
	 * @return the varietyCountT1
	 */
	public Integer getVarietyCountT1() {
		return varietyCountT1;
	}
	/**
	 * @param varietyCountT1 the varietyCountT1 to set
	 */
	public void setVarietyCountT1(Integer varietyCountT1) {
		this.varietyCountT1 = varietyCountT1;
	}
	/**
	 * @return the varietyCountT
	 */
	public Integer getVarietyCountT() {
		return varietyCountT;
	}
	/**
	 * @param varietyCountT the varietyCountT to set
	 */
	public void setVarietyCountT(Integer varietyCountT) {
		this.varietyCountT = varietyCountT;
	}
	/**
	 * @return the varietyPriceRelativeT1
	 */
	public Double getVarietyPriceRelativeT1() {
		return varietyPriceRelativeT1;
	}
	/**
	 * @param varietyPriceRelativeT1 the varietyPriceRelativeT1 to set
	 */
	public void setVarietyPriceRelativeT1(Double varietyPriceRelativeT1) {
		this.varietyPriceRelativeT1 = varietyPriceRelativeT1;
	}
	/**
	 * @return the varietyPriceRelativeT
	 */
	public Double getVarietyPriceRelativeT() {
		return varietyPriceRelativeT;
	}
	/**
	 * @param varietyPriceRelativeT the varietyPriceRelativeT to set
	 */
	public void setVarietyPriceRelativeT(Double varietyPriceRelativeT) {
		this.varietyPriceRelativeT = varietyPriceRelativeT;
	}
	/**
	 * @return the varietyCountPRT1
	 */
	public Double getVarietyCountPRT1() {
		return varietyCountPRT1;
	}
	/**
	 * @param varietyCountPRT1 the varietyCountPRT1 to set
	 */
	public void setVarietyCountPRT1(Double varietyCountPRT1) {
		this.varietyCountPRT1 = varietyCountPRT1;
	}
	/**
	 * @return the varietyCountPRT
	 */
	public Double getVarietyCountPRT() {
		return varietyCountPRT;
	}
	/**
	 * @param varietyCountPRT the varietyCountPRT to set
	 */
	public void setVarietyCountPRT(Double varietyCountPRT) {
		this.varietyCountPRT = varietyCountPRT;
	}
	public BigDecimal getSumNPrice() {
		return sumNPrice;
	}
	public void setSumNPrice(BigDecimal sumNPrice) {
		this.sumNPrice = sumNPrice;
	}
	public BigDecimal getSumSPrice() {
		return sumSPrice;
	}
	public void setSumSPrice(BigDecimal sumSPrice) {
		this.sumSPrice = sumSPrice;
	}
	public Integer getCountNPrice() {
		return countNPrice;
	}
	public void setCountNPrice(Integer countNPrice) {
		this.countNPrice = countNPrice;
	}
	public Integer getCountSPrice() {
		return countSPrice;
	}
	public void setCountSPrice(Integer countSPrice) {
		this.countSPrice = countSPrice;
	}
	public BigDecimal getCurrentNAverage() {
		return currentNAverage;
	}
	public void setCurrentNAverage(BigDecimal currentNAverage) {
		this.currentNAverage = currentNAverage;
	}
	public BigDecimal getCurrentSAverage() {
		return currentSAverage;
	}
	public void setCurrentSAverage(BigDecimal currentSAverage) {
		this.currentSAverage = currentSAverage;
	}
	public BigDecimal getLastNAverage() {
		return lastNAverage;
	}
	public void setLastNAverage(BigDecimal lastNAverage) {
		this.lastNAverage = lastNAverage;
	}
	public BigDecimal getLastSAverage() {
		return lastSAverage;
	}
	public void setLastSAverage(BigDecimal lastSAverage) {
		this.lastSAverage = lastSAverage;
	}
	public BigDecimal getKeepNAverage() {
		return keepNAverage;
	}
	public void setKeepNAverage(BigDecimal keepNAverage) {
		this.keepNAverage = keepNAverage;
	}
	public BigDecimal getKeepSAverage() {
		return keepSAverage;
	}
	public void setKeepSAverage(BigDecimal keepSAverage) {
		this.keepSAverage = keepSAverage;
	}
	public BigDecimal getPrNprice() {
		return prNprice;
	}
	public void setPrNprice(BigDecimal prNprice) {
		this.prNprice = prNprice;
	}
	public BigDecimal getPrSprice() {
		return prSprice;
	}
	public void setPrSprice(BigDecimal prSprice) {
		this.prSprice = prSprice;
	}
	public String getCpiSeries() {
		return cpiSeries;
	}
	public void setCpiSeries(String cpiSeries) {
		this.cpiSeries = cpiSeries;
	}
	public Integer getCpiCompilationMethod() {
		return cpiCompilationMethod;
	}
	public void setCpiCompilationMethod(Integer cpiCompilationMethod) {
		this.cpiCompilationMethod = cpiCompilationMethod;
	}
	public BigDecimal getUnitSum() {
		return unitSum;
	}
	public void setUnitSum(BigDecimal unitSum) {
		this.unitSum = unitSum;
	}
	public Integer getUnitCount() {
		return unitCount;
	}
	public void setUnitCount(Integer unitCount) {
		this.unitCount = unitCount;
	}
	public BigDecimal getUnitCurrentAverage() {
		return unitCurrentAverage;
	}
	public void setUnitCurrentAverage(BigDecimal unitCurrentAverage) {
		this.unitCurrentAverage = unitCurrentAverage;
	}
	public BigDecimal getUnitLastAverage() {
		return unitLastAverage;
	}
	public void setUnitLastAverage(BigDecimal unitLastAverage) {
		this.unitLastAverage = unitLastAverage;
	}
	public BigDecimal getUnitKeepAverage() {
		return unitKeepAverage;
	}
	public void setUnitKeepAverage(BigDecimal unitKeepAverage) {
		this.unitKeepAverage = unitKeepAverage;
	}
	public BigDecimal getUnitAveragePR() {
		return unitAveragePR;
	}
	public void setUnitAveragePR(BigDecimal unitAveragePR) {
		this.unitAveragePR = unitAveragePR;
	}
	public BigDecimal getUntiAverageStdDev() {
		return untiAverageStdDev;
	}
	public void setUntiAverageStdDev(BigDecimal untiAverageStdDev) {
		this.untiAverageStdDev = untiAverageStdDev;
	}
	public BigDecimal getUnitMinSPrice() {
		return unitMinSPrice;
	}
	public void setUnitMinSPrice(BigDecimal unitMinSPrice) {
		this.unitMinSPrice = unitMinSPrice;
	}
	public BigDecimal getUnitMinNPrice() {
		return unitMinNPrice;
	}
	public void setUnitMinNPrice(BigDecimal unitMinNPrice) {
		this.unitMinNPrice = unitMinNPrice;
	}
	public BigDecimal getUnitMaxSPrice() {
		return unitMaxSPrice;
	}
	public void setUnitMaxSPrice(BigDecimal unitMaxSPrice) {
		this.unitMaxSPrice = unitMaxSPrice;
	}
	public BigDecimal getUnitMaxNPrice() {
		return unitMaxNPrice;
	}
	public void setUnitMaxNPrice(BigDecimal unitMaxNPrice) {
		this.unitMaxNPrice = unitMaxNPrice;
	}
	public Double getFinalPRSPrice() {
		return finalPRSPrice;
	}
	public void setFinalPRSPrice(Double finalPRSPrice) {
		this.finalPRSPrice = finalPRSPrice;
	}
	public ArrayList<Double> getsPriceDataCollection() {
		return sPriceDataCollection;
	}
	public void setsPriceDataCollection(ArrayList<Double> sPriceDataCollection) {
		this.sPriceDataCollection = sPriceDataCollection;
	}
	public ArrayList<Double> getnPriceDataCollection() {
		return nPriceDataCollection;
	}
	public void setnPriceDataCollection(ArrayList<Double> nPriceDataCollection) {
		this.nPriceDataCollection = nPriceDataCollection;
	}
	public Double getAveragePRSPrice() {
		return averagePRSPrice;
	}
	public void setAveragePRSPrice(Double averagePRSPrice) {
		this.averagePRSPrice = averagePRSPrice;
	}
	/*
	public Double getdFinalPRSPrice() {
		return dFinalPRSPrice;
	}
	public void setdFinalPRSPrice(Double dFinalPRSPrice) {
		this.dFinalPRSPrice = dFinalPRSPrice;
	}	
	*/
}
