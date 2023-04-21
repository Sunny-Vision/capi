package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.TourRecordSyncData;

public class QuotationRecordOnlineModel {

	private Integer QuotationRecordId;
	
	private String quotationState;
	
	private Date referenceDate;
	
	private Date collectionDate;
	
	private Double nPrice;
	
	private Double sPrice;
	
	private boolean isSPricePeculiar;
	
	private Double uomValue;
	
	private Double fr;
	
	private boolean isFRPercentage;
	
	private boolean isConsignmentCounter;
	
	private String consignmentCounterRemark;
	
	private String reason;
	
	private String discount;
	
	private String remark;
	
	private Integer availability;
	
	private String categoryRemark;
	
	private String contactPerson;
	
	private boolean isBackNo;
	
	private boolean isBackTrack;
	
	private Integer formDisplay;
	
	private String productRemark;
	
	private boolean isProductChange;
	
	private boolean isNewProduct;
	
	private String verificationRemark;
	
	private String rejectReason;
	
	private String peCheckRemark;
	
	private String status;
	
	private boolean isNewRecruitment;
	
	private boolean isFlag;
	
	private boolean isNewOutlet;
	
	private boolean isCollectFR;
	
	private String discountRemark;
	
	private Date assignedCollectionDate;
	
	private Date assignedStartDate;
	
	private Date assignedEndDate;
	
	private String consignmentCounterName;
	
	private Integer firmStatus;
	
	private String productPosition;
	
	private boolean isVisited;
	
	private String verificationReply;
	
	private String validationError;
	
	private boolean passValidation;
	
	private String outletDiscountRemark;
	
	private Date historyDate;
	
	private boolean isSpecifiedUser;
	
	private boolean isReleased;
	
	private boolean verifyFirm;
	
	private boolean verifyCategory;
	
	private boolean verifyQuotation;
	
	private Date approveDate;
	
	private Integer localId;
	
	private String localDbRecordStatus;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	//Foreign Key
	private Integer assignmentId;
	
	private Integer uomId;
	
	private Integer userId;
	
	private Integer allocationBatchId;
	
	private Integer originalQuotationRecordId;
	
	private Integer quotationId;
	
	//Foreign Model
	private OutletSyncData outlet; // map required
	
	private TourRecordSyncData tourRecord;
	
	private ProductOnlineModel product; // map required
	
	private List<SubPriceRecordOnlineModel> subPriceRecords; 
	
	private List<QuotationRecordOnlineModel> otherQuotationRecords;
	
	public Integer getQuotationRecordId() {
		return QuotationRecordId;
	}

	public void setQuotationRecordId(Integer quotationRecordId) {
		QuotationRecordId = quotationRecordId;
	}

	public String getQuotationState() {
		return quotationState;
	}

	public void setQuotationState(String quotationState) {
		this.quotationState = quotationState;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
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

	public boolean isBackNo() {
		return isBackNo;
	}

	public void setBackNo(boolean isBackNo) {
		this.isBackNo = isBackNo;
	}

	public boolean isBackTrack() {
		return isBackTrack;
	}

	public void setBackTrack(boolean isBackTrack) {
		this.isBackTrack = isBackTrack;
	}

	public Integer getFormDisplay() {
		return formDisplay;
	}

	public void setFormDisplay(Integer formDisplay) {
		this.formDisplay = formDisplay;
	}

	public String getProductRemark() {
		return productRemark;
	}

	public void setProductRemark(String productRemark) {
		this.productRemark = productRemark;
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

	public String getVerificationRemark() {
		return verificationRemark;
	}

	public void setVerificationRemark(String verificationRemark) {
		this.verificationRemark = verificationRemark;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getPeCheckRemark() {
		return peCheckRemark;
	}

	public void setPeCheckRemark(String peCheckRemark) {
		this.peCheckRemark = peCheckRemark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isNewRecruitment() {
		return isNewRecruitment;
	}

	public void setNewRecruitment(boolean isNewRecruitment) {
		this.isNewRecruitment = isNewRecruitment;
	}

	public boolean isFlag() {
		return isFlag;
	}

	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}

	public boolean isNewOutlet() {
		return isNewOutlet;
	}

	public void setNewOutlet(boolean isNewOutlet) {
		this.isNewOutlet = isNewOutlet;
	}

	public boolean isCollectFR() {
		return isCollectFR;
	}

	public void setCollectFR(boolean isCollectFR) {
		this.isCollectFR = isCollectFR;
	}

	public String getDiscountRemark() {
		return discountRemark;
	}

	public void setDiscountRemark(String discountRemark) {
		this.discountRemark = discountRemark;
	}

	public Date getAssignedCollectionDate() {
		return assignedCollectionDate;
	}

	public void setAssignedCollectionDate(Date assignedCollectionDate) {
		this.assignedCollectionDate = assignedCollectionDate;
	}

	public Date getAssignedStartDate() {
		return assignedStartDate;
	}

	public void setAssignedStartDate(Date assignedStartDate) {
		this.assignedStartDate = assignedStartDate;
	}

	public Date getAssignedEndDate() {
		return assignedEndDate;
	}

	public void setAssignedEndDate(Date assignedEndDate) {
		this.assignedEndDate = assignedEndDate;
	}

	public String getConsignmentCounterName() {
		return consignmentCounterName;
	}

	public void setConsignmentCounterName(String consignmentCounterName) {
		this.consignmentCounterName = consignmentCounterName;
	}

	public Integer getFirmStatus() {
		return firmStatus;
	}

	public void setFirmStatus(Integer firmStatus) {
		this.firmStatus = firmStatus;
	}

	public String getProductPosition() {
		return productPosition;
	}

	public void setProductPosition(String productPosition) {
		this.productPosition = productPosition;
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

	public String getValidationError() {
		return validationError;
	}

	public void setValidationError(String validationError) {
		this.validationError = validationError;
	}

	public boolean isPassValidation() {
		return passValidation;
	}

	public void setPassValidation(boolean passValidation) {
		this.passValidation = passValidation;
	}

	public String getOutletDiscountRemark() {
		return outletDiscountRemark;
	}

	public void setOutletDiscountRemark(String outletDiscountRemark) {
		this.outletDiscountRemark = outletDiscountRemark;
	}

	public Date getHistoryDate() {
		return historyDate;
	}

	public void setHistoryDate(Date historyDate) {
		this.historyDate = historyDate;
	}

	public boolean isSpecifiedUser() {
		return isSpecifiedUser;
	}

	public void setSpecifiedUser(boolean isSpecifiedUser) {
		this.isSpecifiedUser = isSpecifiedUser;
	}

	public boolean isReleased() {
		return isReleased;
	}

	public void setReleased(boolean isReleased) {
		this.isReleased = isReleased;
	}

	public boolean isVerifyFirm() {
		return verifyFirm;
	}

	public void setVerifyFirm(boolean verifyFirm) {
		this.verifyFirm = verifyFirm;
	}

	public boolean isVerifyCategory() {
		return verifyCategory;
	}

	public void setVerifyCategory(boolean verifyCategory) {
		this.verifyCategory = verifyCategory;
	}

	public boolean isVerifyQuotation() {
		return verifyQuotation;
	}

	public void setVerifyQuotation(boolean verifyQuotation) {
		this.verifyQuotation = verifyQuotation;
	}

	public Date getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Integer getUomId() {
		return uomId;
	}

	public void setUomId(Integer uomId) {
		this.uomId = uomId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}

	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}

	public Integer getOriginalQuotationRecordId() {
		return originalQuotationRecordId;
	}

	public void setOriginalQuotationRecordId(Integer originalQuotationRecordId) {
		this.originalQuotationRecordId = originalQuotationRecordId;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public OutletSyncData getOutlet() {
		return outlet;
	}

	public void setOutlet(OutletSyncData outlet) {
		this.outlet = outlet;
	}

	public TourRecordSyncData getTourRecord() {
		return tourRecord;
	}

	public void setTourRecord(TourRecordSyncData tourRecord) {
		this.tourRecord = tourRecord;
	}

	public ProductOnlineModel getProduct() {
		return product;
	}

	public void setProduct(ProductOnlineModel product) {
		this.product = product;
	}

	public List<SubPriceRecordOnlineModel> getSubPriceRecords() {
		return subPriceRecords;
	}

	public void setSubPriceRecords(List<SubPriceRecordOnlineModel> subPriceRecords) {
		this.subPriceRecords = subPriceRecords;
	}

	public List<QuotationRecordOnlineModel> getOtherQuotationRecords() {
		return otherQuotationRecords;
	}

	public void setOtherQuotationRecords(List<QuotationRecordOnlineModel> otherQuotationRecords) {
		this.otherQuotationRecords = otherQuotationRecords;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
}
