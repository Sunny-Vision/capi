package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="IndoorQuotationRecord")
public class IndoorQuotationRecord extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="IndoorQuotationRecordId")
	private Integer indoorQuotationRecordId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@Column(name="ReferenceDate")
	private Date referenceDate;
	
	@Column(name="OriginalNPrice")
	private Double originalNPrice;
	
	@Column(name="OriginalSPrice")
	private Double originalSPrice;
	
	@Column(name="NPriceAfterUOMConversion")
	private Double nPriceAfterUOMConversion;
	
	@Column(name="SPriceAfterUOMConversion")
	private Double sPriceAfterUOMConversion;
	
	@Column(name="ComputedNPrice")
	private Double computedNPrice;
	
	@Column(name="ComputedSPrice")
	private Double computedSPrice;
	
	@Column(name="CurrentNPrice")
	private Double currentNPrice;
	
	@Column(name="CurrentSPrice")
	private Double currentSPrice;
	
	@Column(name="PreviousNPrice")
	private Double previousNPrice;
	
	@Column(name="PreviousSPrice")
	private Double previousSPrice;
	
	@Column(name="FR")
	private Double fr;
	
	@Column(name="IsApplyFR")
	private boolean isApplyFR;
	
	@Column(name="IsFRPercentage")
	private boolean isFRPercentage;	
	
	@Column(name="Remark")
	private String remark;
	
	@Column(name="IsFirmVerify")
	private boolean isFirmVerify;
	
	@Column(name="IsCategoryVerify")
	private boolean isCategoryVerify;
	
	@Column(name="IsQuotationVerify")
	private boolean isQuotationVerify;
	
	@Column(name="FirmRemark")
	private String firmRemark;
	
	@Column(name="CategoryRemark")
	private String categoryRemark;
	
	@Column(name="QuotationRemark")
	private String quotationRemark;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="QuotationRecordId", nullable=true)
	private QuotationRecord quotationRecord;
	
	/**
	 * Allocation,
	 * Conversion,
	 * Complete,
	 * Request Verification,
	 * Approve Verification,
	 * Reject Verification,
	 * Review Verification,
	 * Revisit
	 */
	@Column(name="Status")
	private String status;
	
	@Column(name="RejectReason")
	private String rejectReason;
	
	@Deprecated
	@Column(name="ImputedQuotationPrice")
	private Double imputedQuotationPrice;
	
	@Deprecated
	@Column(name="ImputedQuotationRemark")
	private String imputedQuotationRemark;
	
	@Deprecated
	@Column(name="ImputedUnitPrice")
	private Double imputedUnitPrice;
	
	@Deprecated
	@Column(name="ImputedUnitRemark")
	private String imputedUnitRemark;
	
	@Column(name="BackNoLastNPirce")
	private Double backNoLastNPirce;
	
	@Column(name="BackNoLastSPrice")
	private Double backNoLastSPrice;
	
	@Column(name="LastNPrice")
	private Double lastNPrice;
	
	@Column(name="LastSPrice")
	private Double lastSPrice;
	

	@Column(name="LastPriceDate")
	private Date lastPriceDate;
	
	/**
	 * 1- none
	 * 2- N -> S
	 * 3- S -> N
	 */
	@Column(name="CopyPriceType")
	private Integer copyPriceType;
	
	/**
	 * 1- none
	 * 2- N -> S
	 * 3- S -> N
	 */
	@Column(name="CopyLastPriceType")
	private Integer copyLastPriceType;
	
	/**
	 * set true if availability <> available and keep no 
	 */
	@Column(name="IsCurrentPriceKeepNo")
	private Boolean isCurrentPriceKeepNo;
	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QuotationId", nullable = true)
	private Quotation quotation;
	
	
	@Column(name="IsProductChange")
	private boolean isProductChange;
		
	@Column(name="IsNewProduct")
	private boolean isNewProduct;
	
	@Column(name="IsNewRecruitment")
	private boolean isNewRecruitment;
	
	@Column(name="IsNewOutlet")
	private boolean isNewOutlet;
	
	@Column(name="IsOutlier")
	private boolean isOutlier;
	
	@Column(name="OutlierRemark")
	private String outlierRemark;
	
	@Column(name="IsSpicing")
	private boolean isSpicing;
	
	@Column(name="IsNullCurrentNPrice")
	private boolean isNullCurrentNPrice;
	
	@Column(name="IsNullCurrentSPrice")
	private boolean isNullCurrentSPrice;
	
	@Column(name="IsNullPreviousNPrice")
	private boolean isNullPreviousNPrice;
	
	@Column(name="IsNullPreviousSPrice")
	private boolean isNullPreviousSPrice;
	
	@Column(name="IsFlag")
	private boolean isFlag;
	
	@Column(name="IsRUA")
	private boolean isRUA;
	
	@Column(name="RUADate")
	private Date ruaDate;
	
	@Column(name="IsNoField")
	private boolean isNoField;
	
	@Column(name="IsProductNotAvailable")
	private boolean isProductNotAvailable;
	
	@Column(name="ProductNotAvailableFrom")
	private Date productNotAvailableFrom;
	
//	@ManyToOne
//    @JoinColumns({
//        @JoinColumn(name="ReferenceMonth", referencedColumnName="referenceMonth"),
//        @JoinColumn(name="QuotationId", referencedColumnName="QuotationId")
//    })
//	private InputeQuotation inputeQuotation;
//		
	
	public Integer getId(){
		return getIndoorQuotationRecordId();
	}

	public Integer getIndoorQuotationRecordId() {
		return indoorQuotationRecordId;
	}

	public void setIndoorQuotationRecordId(Integer indoorQuotationRecordId) {
		this.indoorQuotationRecordId = indoorQuotationRecordId;
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

	public boolean isFirmVerify() {
		return isFirmVerify;
	}

	public void setFirmVerify(boolean isFirmVerify) {
		this.isFirmVerify = isFirmVerify;
	}

	public boolean isCategoryVerify() {
		return isCategoryVerify;
	}

	public void setCategoryVerify(boolean isCategoryVerify) {
		this.isCategoryVerify = isCategoryVerify;
	}

	public boolean isQuotationVerify() {
		return isQuotationVerify;
	}

	public void setQuotationVerify(boolean isQuotationVerify) {
		this.isQuotationVerify = isQuotationVerify;
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

	public QuotationRecord getQuotationRecord() {
		return quotationRecord;
	}

	public void setQuotationRecord(QuotationRecord quotationRecord) {
		this.quotationRecord = quotationRecord;
	}

	public String getFirmRemark() {
		return firmRemark;
	}

	public void setFirmRemark(String firmRemark) {
		this.firmRemark = firmRemark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Deprecated
	public Double getImputedQuotationPrice() {
		return imputedQuotationPrice;
	}

	@Deprecated
	public void setImputedQuotationPrice(Double imputedQuotationPrice) {
		this.imputedQuotationPrice = imputedQuotationPrice;
	}

	@Deprecated
	public String getImputedQuotationRemark() {
		return imputedQuotationRemark;
	}

	@Deprecated
	public void setImputedQuotationRemark(String imputedQuotationRemark) {
		this.imputedQuotationRemark = imputedQuotationRemark;
	}

	@Deprecated
	public Double getImputedUnitPrice() {
		return imputedUnitPrice;
	}

	@Deprecated
	public void setImputedUnitPrice(Double imputedUnitPrice) {
		this.imputedUnitPrice = imputedUnitPrice;
	}

	@Deprecated
	public String getImputedUnitRemark() {
		return imputedUnitRemark;
	}

	@Deprecated
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

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
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

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
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

	public boolean isSpicing() {
		return isSpicing;
	}

	public void setSpicing(boolean isSpicing) {
		this.isSpicing = isSpicing;
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

	public boolean isNullPreviousNPrice() {
		return isNullPreviousNPrice;
	}

	public void setNullPreviousNPrice(boolean isNullPreviousNPrice) {
		this.isNullPreviousNPrice = isNullPreviousNPrice;
	}

	public boolean isNullPreviousSPrice() {
		return isNullPreviousSPrice;
	}

	public void setNullPreviousSPrice(boolean isNullPreviousSPrice) {
		this.isNullPreviousSPrice = isNullPreviousSPrice;
	}
//
//	public InputeQuotation getInputeQuotation() {
//		return inputeQuotation;
//	}
//
//	public void setInputeQuotation(InputeQuotation inputeQuotation) {
//		this.inputeQuotation = inputeQuotation;
//	}	

	public Date getLastPriceDate() {
		return lastPriceDate;
	}

	public void setLastPriceDate(Date lastPriceDate) {
		this.lastPriceDate = lastPriceDate;
	}

	public Integer getCopyLastPriceType() {
		return copyLastPriceType;
	}

	public void setCopyLastPriceType(Integer copyLastPriceType) {
		this.copyLastPriceType = copyLastPriceType;
	}

	public boolean isFlag() {
		return isFlag;
	}

	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
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

	public boolean isNoField() {
		return isNoField;
	}

	public void setNoField(boolean isNoField) {
		this.isNoField = isNoField;
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
}
