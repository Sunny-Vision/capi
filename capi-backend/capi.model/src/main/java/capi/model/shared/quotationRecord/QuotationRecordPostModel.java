package capi.model.shared.quotationRecord;

import java.util.List;

public class QuotationRecordPostModel {
	private Integer quotationRecordId;
	private String referenceDate;
	private String collectionDate;
	private Double nPrice;
	private Double sPrice;
	private boolean isSPricePeculiar;
	private Integer uomId;
	private Double uomValue;
	private Double fr;
	private boolean isFRPercentage;
	private boolean isConsignmentCounter;
	private String consignmentCounterRemark;
	private String reason;
	private String discount;
	private String discountRemark;
	private String remark;
	private Integer availability;
	private String productRemark;
	private List<SubPriceModel> subPrices;
	private Integer productId;
	private TourRecordModel tourRecord;
	private String categoryRemark;
	private String outletDiscountRemark;
	private String contactPerson;
	private boolean productChange;
	private String consignmentCounterName;
	private boolean isVisited;
	private String verificationReply;
	private Integer version;
	private String productPosition;
	
	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}
	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}
	public String getReferenceDate() {
		return referenceDate;
	}
	public void setReferenceDate(String referenceDate) {
		this.referenceDate = referenceDate;
	}
	public String getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}
	public Double getnPrice() {
		return nPrice;
	}
	public void setnPrice(Double nPrice) {
		this.nPrice = nPrice;
	}
	public Double getsPrice() {
		return sPrice;
	}
	public void setsPrice(Double sPrice) {
		this.sPrice = sPrice;
	}
	public boolean isSPricePeculiar() {
		return isSPricePeculiar;
	}
	public void setSPricePeculiar(boolean isSPricePeculiar) {
		this.isSPricePeculiar = isSPricePeculiar;
	}
	public Integer getUomId() {
		return uomId;
	}
	public void setUomId(Integer uomId) {
		this.uomId = uomId;
	}
	public Double getUomValue() {
		return uomValue;
	}
	public void setUomValue(Double uomValue) {
		this.uomValue = uomValue;
	}
	public Double getFr() {
		return fr;
	}
	public void setFr(Double fr) {
		this.fr = fr;
	}
	public boolean isFRPercentage() {
		return isFRPercentage;
	}
	public void setFRPercentage(boolean isFRPercentage) {
		this.isFRPercentage = isFRPercentage;
	}
	public boolean isConsignmentCounter() {
		return isConsignmentCounter;
	}
	public void setConsignmentCounter(boolean isConsignmentCounter) {
		this.isConsignmentCounter = isConsignmentCounter;
	}
	public String getConsignmentCounterRemark() {
		return consignmentCounterRemark;
	}
	public void setConsignmentCounterRemark(String consignmentCounterRemark) {
		this.consignmentCounterRemark = consignmentCounterRemark;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getDiscountRemark() {
		return discountRemark;
	}
	public void setDiscountRemark(String discountRemark) {
		this.discountRemark = discountRemark;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getAvailability() {
		return availability;
	}
	public void setAvailability(Integer availability) {
		this.availability = availability;
	}
	public String getProductRemark() {
		return productRemark;
	}
	public void setProductRemark(String productRemark) {
		this.productRemark = productRemark;
	}
	public List<SubPriceModel> getSubPrices() {
		return subPrices;
	}
	public void setSubPrices(List<SubPriceModel> subPrices) {
		this.subPrices = subPrices;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public TourRecordModel getTourRecord() {
		return tourRecord;
	}
	public void setTourRecord(TourRecordModel tourRecord) {
		this.tourRecord = tourRecord;
	}
	public String getCategoryRemark() {
		return categoryRemark;
	}
	public void setCategoryRemark(String categoryRemark) {
		this.categoryRemark = categoryRemark;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public boolean isProductChange() {
		return productChange;
	}
	public void setProductChange(boolean productChange) {
		this.productChange = productChange;
	}
	public String getConsignmentCounterName() {
		return consignmentCounterName;
	}
	public void setConsignmentCounterName(String consignmentCounterName) {
		this.consignmentCounterName = consignmentCounterName;
	}
	public boolean isVisited() {
		return isVisited;
	}
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	public String getVerificationReply() {
		return verificationReply;
	}
	public void setVerificationReply(String verificationReply) {
		this.verificationReply = verificationReply;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getOutletDiscountRemark() {
		return outletDiscountRemark;
	}
	public void setOutletDiscountRemark(String outletDiscountRemark) {
		this.outletDiscountRemark = outletDiscountRemark;
	}
	public String getProductPosition() {
		return productPosition;
	}
	public void setProductPosition(String productPosition) {
		this.productPosition = productPosition;
	}
}
