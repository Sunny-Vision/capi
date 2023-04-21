package capi.model.api.dataSync;

import java.util.Date;

public class QuotationRecordSyncData {

	private Integer quotationRecordId;
	
	private Integer outletId;
	
	private Integer productId;
	
	private Integer quotationId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer assignmentId;
	
	private String quotationState;
	
	private Date referenceDate;
	
	private Date collectionDate;
	
	private Double nPrice;
	
	private Double sPrice;
	
	private boolean isSPricePeculiar;
	
	private Double uomValue;
	
	private Integer uomId;
	
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
	
	private Integer originalQuotationRecordId;
	
	private String verificationRemark;
	
	private String rejectReason;
	
	private String peCheckRemark;
	
	private boolean isNewProduct;
	
	private String status;
	
	private boolean isNewRecruitment;
	
	private boolean isFlag;
	
	private boolean isNewOutlet;
	
	private Integer userId;
	
	private boolean isCollectFR;
	
	private String discountRemark;
	
	private Date assignedCollectionDate;
	
	private Date assignedStartDate;
	
	private Date assignedEndDate;
	
	private Integer firmStatus;
	
	private String consignmentCounterName;
	
	private boolean isVisited;
	
	private String verificationReply;
	
	private String validationError;
	
	private boolean passValidation;
	
	private String outletDiscountRemark;
	
	private Date historyDate;
	
	private boolean isSpecifiedUser;
	
	private boolean isReleased;
	
	private Integer allocationBatchId;
	
	private String productPosition;
	
	private boolean verifyFirm;
	
	private boolean verifyCategory;
	
	private boolean verifyQuotation;
	
	private Integer localId;

	private String localDbRecordStatus;

	private Integer version;
	
	private Date approvedDate;
	
	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
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

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
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

	public Double getUomValue() {
		return uomValue;
	}

	public void setUomValue(Double uomValue) {
		this.uomValue = uomValue;
	}

	public Integer getUomId() {
		return uomId;
	}

	public void setUomId(Integer uomId) {
		this.uomId = uomId;
	}

	public Double getFr() {
		return fr;
	}

	public void setFr(Double fr) {
		this.fr = fr;
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

	public Integer getOriginalQuotationRecordId() {
		return originalQuotationRecordId;
	}

	public void setOriginalQuotationRecordId(Integer originalQuotationRecordId) {
		this.originalQuotationRecordId = originalQuotationRecordId;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getFirmStatus() {
		return firmStatus;
	}

	public void setFirmStatus(Integer firmStatus) {
		this.firmStatus = firmStatus;
	}

	public String getConsignmentCounterName() {
		return consignmentCounterName;
	}

	public void setConsignmentCounterName(String consignmentCounterName) {
		this.consignmentCounterName = consignmentCounterName;
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

	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}

	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}

	public String getProductPosition() {
		return productPosition;
	}

	public void setProductPosition(String productPosition) {
		this.productPosition = productPosition;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}

	public boolean isSPricePeculiar() {
		return isSPricePeculiar;
	}

	public void setSPricePeculiar(boolean isSPricePeculiar) {
		this.isSPricePeculiar = isSPricePeculiar;
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

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public boolean isPassValidation() {
		return passValidation;
	}

	public void setPassValidation(boolean passValidation) {
		this.passValidation = passValidation;
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allocationBatchId == null) ? 0 : allocationBatchId.hashCode());
		result = prime * result + ((approvedDate == null) ? 0 : approvedDate.hashCode());
		result = prime * result + ((assignedCollectionDate == null) ? 0 : assignedCollectionDate.hashCode());
		result = prime * result + ((assignedEndDate == null) ? 0 : assignedEndDate.hashCode());
		result = prime * result + ((assignedStartDate == null) ? 0 : assignedStartDate.hashCode());
		result = prime * result + ((assignmentId == null) ? 0 : assignmentId.hashCode());
		result = prime * result + ((availability == null) ? 0 : availability.hashCode());
		result = prime * result + ((categoryRemark == null) ? 0 : categoryRemark.hashCode());
		result = prime * result + ((collectionDate == null) ? 0 : collectionDate.hashCode());
		result = prime * result + ((consignmentCounterName == null) ? 0 : consignmentCounterName.hashCode());
		result = prime * result + ((consignmentCounterRemark == null) ? 0 : consignmentCounterRemark.hashCode());
		result = prime * result + ((contactPerson == null) ? 0 : contactPerson.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((discount == null) ? 0 : discount.hashCode());
		result = prime * result + ((discountRemark == null) ? 0 : discountRemark.hashCode());
		result = prime * result + ((firmStatus == null) ? 0 : firmStatus.hashCode());
		result = prime * result + ((formDisplay == null) ? 0 : formDisplay.hashCode());
		result = prime * result + ((fr == null) ? 0 : fr.hashCode());
		result = prime * result + ((historyDate == null) ? 0 : historyDate.hashCode());
		result = prime * result + (isBackNo ? 1231 : 1237);
		result = prime * result + (isBackTrack ? 1231 : 1237);
		result = prime * result + (isCollectFR ? 1231 : 1237);
		result = prime * result + (isConsignmentCounter ? 1231 : 1237);
		result = prime * result + (isFRPercentage ? 1231 : 1237);
		result = prime * result + (isFlag ? 1231 : 1237);
		result = prime * result + (isNewOutlet ? 1231 : 1237);
		result = prime * result + (isNewProduct ? 1231 : 1237);
		result = prime * result + (isNewRecruitment ? 1231 : 1237);
		result = prime * result + (isProductChange ? 1231 : 1237);
		result = prime * result + (isReleased ? 1231 : 1237);
		result = prime * result + (isSPricePeculiar ? 1231 : 1237);
		result = prime * result + (isSpecifiedUser ? 1231 : 1237);
		result = prime * result + (isVisited ? 1231 : 1237);
		result = prime * result + ((localDbRecordStatus == null) ? 0 : localDbRecordStatus.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((nPrice == null) ? 0 : nPrice.hashCode());
		result = prime * result + ((originalQuotationRecordId == null) ? 0 : originalQuotationRecordId.hashCode());
		result = prime * result + ((outletDiscountRemark == null) ? 0 : outletDiscountRemark.hashCode());
		result = prime * result + ((outletId == null) ? 0 : outletId.hashCode());
		result = prime * result + (passValidation ? 1231 : 1237);
		result = prime * result + ((peCheckRemark == null) ? 0 : peCheckRemark.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((productPosition == null) ? 0 : productPosition.hashCode());
		result = prime * result + ((productRemark == null) ? 0 : productRemark.hashCode());
		result = prime * result + ((quotationId == null) ? 0 : quotationId.hashCode());
		result = prime * result + ((quotationRecordId == null) ? 0 : quotationRecordId.hashCode());
		result = prime * result + ((quotationState == null) ? 0 : quotationState.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		result = prime * result + ((referenceDate == null) ? 0 : referenceDate.hashCode());
		result = prime * result + ((rejectReason == null) ? 0 : rejectReason.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((sPrice == null) ? 0 : sPrice.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((uomId == null) ? 0 : uomId.hashCode());
		result = prime * result + ((uomValue == null) ? 0 : uomValue.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((validationError == null) ? 0 : validationError.hashCode());
		result = prime * result + ((verificationRemark == null) ? 0 : verificationRemark.hashCode());
		result = prime * result + ((verificationReply == null) ? 0 : verificationReply.hashCode());
		result = prime * result + (verifyCategory ? 1231 : 1237);
		result = prime * result + (verifyFirm ? 1231 : 1237);
		result = prime * result + (verifyQuotation ? 1231 : 1237);
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuotationRecordSyncData other = (QuotationRecordSyncData) obj;
		if (allocationBatchId == null) {
			if (other.allocationBatchId != null)
				return false;
		} else if (!allocationBatchId.equals(other.allocationBatchId))
			return false;
		if (approvedDate == null) {
			if (other.approvedDate != null)
				return false;
		} else if (!approvedDate.equals(other.approvedDate))
			return false;
		if (assignedCollectionDate == null) {
			if (other.assignedCollectionDate != null)
				return false;
		} else if (!assignedCollectionDate.equals(other.assignedCollectionDate))
			return false;
		if (assignedEndDate == null) {
			if (other.assignedEndDate != null)
				return false;
		} else if (!assignedEndDate.equals(other.assignedEndDate))
			return false;
		if (assignedStartDate == null) {
			if (other.assignedStartDate != null)
				return false;
		} else if (!assignedStartDate.equals(other.assignedStartDate))
			return false;
		if (assignmentId == null) {
			if (other.assignmentId != null)
				return false;
		} else if (!assignmentId.equals(other.assignmentId))
			return false;
		if (availability == null) {
			if (other.availability != null)
				return false;
		} else if (!availability.equals(other.availability))
			return false;
		if (categoryRemark == null) {
			if (other.categoryRemark != null)
				return false;
		} else if (!categoryRemark.equals(other.categoryRemark))
			return false;
		if (collectionDate == null) {
			if (other.collectionDate != null)
				return false;
		} else if (!collectionDate.equals(other.collectionDate))
			return false;
		if (consignmentCounterName == null) {
			if (other.consignmentCounterName != null)
				return false;
		} else if (!consignmentCounterName.equals(other.consignmentCounterName))
			return false;
		if (consignmentCounterRemark == null) {
			if (other.consignmentCounterRemark != null)
				return false;
		} else if (!consignmentCounterRemark.equals(other.consignmentCounterRemark))
			return false;
		if (contactPerson == null) {
			if (other.contactPerson != null)
				return false;
		} else if (!contactPerson.equals(other.contactPerson))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (discount == null) {
			if (other.discount != null)
				return false;
		} else if (!discount.equals(other.discount))
			return false;
		if (discountRemark == null) {
			if (other.discountRemark != null)
				return false;
		} else if (!discountRemark.equals(other.discountRemark))
			return false;
		if (firmStatus == null) {
			if (other.firmStatus != null)
				return false;
		} else if (!firmStatus.equals(other.firmStatus))
			return false;
		if (formDisplay == null) {
			if (other.formDisplay != null)
				return false;
		} else if (!formDisplay.equals(other.formDisplay))
			return false;
		if (fr == null) {
			if (other.fr != null)
				return false;
		} else if (!fr.equals(other.fr))
			return false;
		if (historyDate == null) {
			if (other.historyDate != null)
				return false;
		} else if (!historyDate.equals(other.historyDate))
			return false;
		if (isBackNo != other.isBackNo)
			return false;
		if (isBackTrack != other.isBackTrack)
			return false;
		if (isCollectFR != other.isCollectFR)
			return false;
		if (isConsignmentCounter != other.isConsignmentCounter)
			return false;
		if (isFRPercentage != other.isFRPercentage)
			return false;
		if (isFlag != other.isFlag)
			return false;
		if (isNewOutlet != other.isNewOutlet)
			return false;
		if (isNewProduct != other.isNewProduct)
			return false;
		if (isNewRecruitment != other.isNewRecruitment)
			return false;
		if (isProductChange != other.isProductChange)
			return false;
		if (isReleased != other.isReleased)
			return false;
		if (isSPricePeculiar != other.isSPricePeculiar)
			return false;
		if (isSpecifiedUser != other.isSpecifiedUser)
			return false;
		if (isVisited != other.isVisited)
			return false;
		if (localDbRecordStatus == null) {
			if (other.localDbRecordStatus != null)
				return false;
		} else if (!localDbRecordStatus.equals(other.localDbRecordStatus))
			return false;
		if (localId == null) {
			if (other.localId != null)
				return false;
		} else if (!localId.equals(other.localId))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (nPrice == null) {
			if (other.nPrice != null)
				return false;
		} else if (!nPrice.equals(other.nPrice))
			return false;
		if (originalQuotationRecordId == null) {
			if (other.originalQuotationRecordId != null)
				return false;
		} else if (!originalQuotationRecordId.equals(other.originalQuotationRecordId))
			return false;
		if (outletDiscountRemark == null) {
			if (other.outletDiscountRemark != null)
				return false;
		} else if (!outletDiscountRemark.equals(other.outletDiscountRemark))
			return false;
		if (outletId == null) {
			if (other.outletId != null)
				return false;
		} else if (!outletId.equals(other.outletId))
			return false;
		if (passValidation != other.passValidation)
			return false;
		if (peCheckRemark == null) {
			if (other.peCheckRemark != null)
				return false;
		} else if (!peCheckRemark.equals(other.peCheckRemark))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (productPosition == null) {
			if (other.productPosition != null)
				return false;
		} else if (!productPosition.equals(other.productPosition))
			return false;
		if (productRemark == null) {
			if (other.productRemark != null)
				return false;
		} else if (!productRemark.equals(other.productRemark))
			return false;
		if (quotationId == null) {
			if (other.quotationId != null)
				return false;
		} else if (!quotationId.equals(other.quotationId))
			return false;
		if (quotationRecordId == null) {
			if (other.quotationRecordId != null)
				return false;
		} else if (!quotationRecordId.equals(other.quotationRecordId))
			return false;
		if (quotationState == null) {
			if (other.quotationState != null)
				return false;
		} else if (!quotationState.equals(other.quotationState))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		if (referenceDate == null) {
			if (other.referenceDate != null)
				return false;
		} else if (!referenceDate.equals(other.referenceDate))
			return false;
		if (rejectReason == null) {
			if (other.rejectReason != null)
				return false;
		} else if (!rejectReason.equals(other.rejectReason))
			return false;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (sPrice == null) {
			if (other.sPrice != null)
				return false;
		} else if (!sPrice.equals(other.sPrice))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (uomId == null) {
			if (other.uomId != null)
				return false;
		} else if (!uomId.equals(other.uomId))
			return false;
		if (uomValue == null) {
			if (other.uomValue != null)
				return false;
		} else if (!uomValue.equals(other.uomValue))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (validationError == null) {
			if (other.validationError != null)
				return false;
		} else if (!validationError.equals(other.validationError))
			return false;
		if (verificationRemark == null) {
			if (other.verificationRemark != null)
				return false;
		} else if (!verificationRemark.equals(other.verificationRemark))
			return false;
		if (verificationReply == null) {
			if (other.verificationReply != null)
				return false;
		} else if (!verificationReply.equals(other.verificationReply))
			return false;
		if (verifyCategory != other.verifyCategory)
			return false;
		if (verifyFirm != other.verifyFirm)
			return false;
		if (verifyQuotation != other.verifyQuotation)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	

	
}
