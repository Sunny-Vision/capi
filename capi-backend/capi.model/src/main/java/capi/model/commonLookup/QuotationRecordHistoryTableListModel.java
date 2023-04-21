package capi.model.commonLookup;

import java.util.Date;

public class QuotationRecordHistoryTableListModel {
	private Integer id;
	private Date submissionDate;
	private Date referenceMonth;
	private String submissionDateStr;
	private String referenceMonthStr;
	private Double collectedNPrice;
	private Double collectedSPrice;
	private Integer subPriceId; //special price
	private Integer quotationRecordId;
	private String discount;
	private Integer availability;
	private Boolean fr;
	private Double previousNPrice;
	private Double previousSPrice;
	private Double currentNPrice;
	private Double currentSPrice;
	private Boolean isFlag;
	private Boolean isProductChange;
	private String remark;
	private Double max;
	private Double min;
	private Double average;
	private Integer productId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}
	public Date getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public String getSubmissionDateStr() {
		return submissionDateStr;
	}
	public void setSubmissionDateStr(String submissionDateStr) {
		this.submissionDateStr = submissionDateStr;
	}
	public String getReferenceMonthStr() {
		return referenceMonthStr;
	}
	public void setReferenceMonthStr(String referenceMonthStr) {
		this.referenceMonthStr = referenceMonthStr;
	}
	public Double getCollectedNPrice() {
		return collectedNPrice;
	}
	public void setCollectedNPrice(Double collectedNPrice) {
		this.collectedNPrice = collectedNPrice;
	}
	public Double getCollectedSPrice() {
		return collectedSPrice;
	}
	public void setCollectedSPrice(Double collectedSPrice) {
		this.collectedSPrice = collectedSPrice;
	}
	public Integer getSubPriceId() {
		return subPriceId;
	}
	public void setSubPriceId(Integer subPriceId) {
		this.subPriceId = subPriceId;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public Integer getAvailability() {
		return availability;
	}
	public void setAvailability(Integer availability) {
		this.availability = availability;
	}
	public Boolean getFr() {
		return fr;
	}
	public void setFr(Boolean fr) {
		this.fr = fr;
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
	public Boolean getIsFlag() {
		return isFlag;
	}
	public void setIsFlag(Boolean isFlag) {
		this.isFlag = isFlag;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getAverage() {
		return average;
	}
	public void setAverage(Double average) {
		this.average = average;
	}
	public Boolean getIsProductChange() {
		return isProductChange;
	}
	public void setIsProductChange(Boolean isProductChange) {
		this.isProductChange = isProductChange;
	}
	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}
	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	
}
