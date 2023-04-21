package capi.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name="QuotationRecord")
public class QuotationRecord extends capi.entity.EntityBase implements OptimisticLockEntity{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="QuotationRecordId")
	private Integer quotationRecordId;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QuotationId", nullable = true)
	private Quotation quotation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductId", nullable = true)
	private Product product;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OutletId", nullable = true)
	private Outlet outlet;

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignmentId", nullable = true)
	private Assignment assignment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UOMId", nullable = true)
	private Uom uom;
	
	/**
	 * Normal, Revisit, Verify, IP
	 */
	@Column(name="QuotationState")
	private String quotationState;
	
	@Column(name="ReferenceDate")
	private Date referenceDate;
	
	@Column(name="CollectionDate")
	private Date collectionDate;
	
	@Column(name="NPrice")
	private Double nPrice;
	
	@Column(name="SPrice")
	private Double sPrice;
	
	@Column(name="IsSPricePeculiar")
	private boolean isSPricePeculiar;
	
	@Column(name="UomValue")
	private Double uomValue;
	
	@Column(name="FR")
	private Double fr;
	
	@Column(name="IsFRPercentage")
	private boolean isFRPercentage;

	@Column(name="IsConsignmentCounter")
	private boolean isConsignmentCounter;
	
	@Column(name="ConsignmentCounterRemark")
	private String consignmentCounterRemark;
	
	@Column(name="Reason")
	private String reason;
	
	@Column(name="Discount")
	private String discount;
	
	@Column(name="Remark")
	private String remark;
	
	/**
	 * 1- Available
	 * 2- IP
	 * 3- 有價無貨
	 * 4- 無價無貨 -> 缺貨
	 * 5- Not Suitable
	 * 6- 回倉
	 * 7- 無團
	 * 8- 未返
	 * 9- New
	 */
	@Column(name="Availability")
	private Integer availability;
	
	@Column(name="CategoryRemark")
	private String categoryRemark;
	
	@Column(name="ContactPerson")
	private String contactPerson;
		
	@Column(name="IsBackNo")
	private boolean isBackNo;
	
	@Column(name="IsBackTrack")
	private boolean isBackTrack;
	
	/**
	 * 1 - normal
	 * 2 - tour
	 */
	@Column(name="FormDisplay")
	private Integer formDisplay;
	
	@Column(name="ProductRemark")
	private String productRemark;
	
	@Column(name="IsProductChange")
	private boolean isProductChange;
	
	@Column(name="IsNewProduct")
	private boolean isNewProduct;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OriginalQuotationRecordId", nullable = true)
	private QuotationRecord originalQuotationRecord;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "originalQuotationRecord")
	private Set<QuotationRecord> otherQuotationRecords = new HashSet<QuotationRecord>();
	
	@Column(name="VerificationRemark")
	private String verificationRemark;
	
	@Column(name="RejectReason")
	private String rejectReason;
	
	@Column(name="PECheckRemark")
	private String peCheckRemark;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "quotationRecord")
	private Set<SubPriceRecord> subPriceRecords = new HashSet<SubPriceRecord>();
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "quotationRecord")
	private TourRecord tourRecord;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "quotationRecord")
	private IndoorQuotationRecord indoorQuotationRecord;

	/**
	 * Blank,
	 * Draft,
	 * Submitted,
	 * Approved,
	 * Rejected
	 */
	@Column(name="Status")
	private String status;
	
	@Column(name="IsNewRecruitment")
	private boolean isNewRecruitment;
	
	@Column(name="IsFlag")
	private boolean isFlag;
	
	@Column(name="IsNewOutlet")
	private boolean isNewOutlet;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@Column(name="IsCollectFR")
	private boolean isCollectFR;
	
	@Column(name="DiscountRemark")
	private String discountRemark;
	
	@Column(name="AssignedCollectionDate")
	private Date assignedCollectionDate;
	
	@Column(name="AssignedStartDate")
	private Date assignedStartDate;
	
	@Column(name="AssignedEndDate")
	private Date assignedEndDate;
	
	@Version
	@Column(name="Version")
	private Integer version;
	
	@Column(name="ConsignmentCounterName")
	private String consignmentCounterName;
	
	/**
	 * 1- EN
	 * 2- Closed
	 * 3- MV
	 * 4- Not Suitable
	 * 5- Refusal
	 * 6- Wrong Outlet
	 * 7- DL
	 * 8- NC
	 * 9- IP
	 * 10 - DU
	 */
	@Column(name="FirmStatus")
	private Integer firmStatus;
	
	@Column(name="ProductPosition")
	private String productPosition;
	
	
	/**
	 * For verification and revisit use
	 */
	@Column(name="IsVisited")
	private boolean isVisited;
	
	/**
	 * Accumulate the changes in verification
	 */
	@Column(name="VerificationReply")
	private String verificationReply;
	
	/**
	 * Store the error in the form when saving
	 */
	@Column(name="ValidationError")
	private String validationError;
	
	/**
	 * if the form passed all validation
	 */
	@Column(name="PassValidation")
	private boolean passValidation;
	
	/**
	 * Copy from the discount remark in outlet
	 */	
	@Column(name="OutletDiscountRemark")
	private String outletDiscountRemark;
	
	@Column(name="HistoryDate")
	private Date historyDate;
	
	/**
	 * set true if specified user
	 */
	@Column(name="IsSpecifiedUser")
	private boolean isSpecifiedUser;
	
	/**
	 * set true if the assignment is transferred to another user in assignment selection state
	 */
	@Column(name="IsReleased")
	private boolean isReleased;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AllocationBatchId", nullable = true)
	private AllocationBatch allocationBatch;
	
	@Column(name="VerifyFirm")
	private boolean verifyFirm;
	
	@Column(name="VerifyCategory")
	private boolean verifyCategory;
	
	@Column(name="VerifyQuotation")
	private boolean verifyQuotation;
	
	@Column(name="ApprovedDate")
	private Date approvedDate;
	
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "quotationRecords")
	private Set<AssignmentReallocation> assignmentReallocations = new HashSet<AssignmentReallocation>();
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getQuotationRecordId();
	}


	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}


	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}


	public Quotation getQuotation() {
		return quotation;
	}


	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}


	public Product getProduct() {
		return product;
	}


	public void setProduct(Product product) {
		this.product = product;
	}


	public Outlet getOutlet() {
		return outlet;
	}


	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}


	public Assignment getAssignment() {
		return assignment;
	}


	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}


	public String getQuotationState() {
		return quotationState;
	}


	public void setQuotationState(String quotationState) {
		this.quotationState = quotationState;
	}


	public Uom getUom() {
		return uom;
	}


	public void setUom(Uom uom) {
		this.uom = uom;
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


	public QuotationRecord getOriginalQuotationRecord() {
		return originalQuotationRecord;
	}


	public void setOriginalQuotationRecord(QuotationRecord originalQuotationRecord) {
		this.originalQuotationRecord = originalQuotationRecord;
	}


	public Set<QuotationRecord> getOtherQuotationRecords() {
		return otherQuotationRecords;
	}


	public void setOtherQuotationRecords(Set<QuotationRecord> otherQuotationRecords) {
		this.otherQuotationRecords = otherQuotationRecords;
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


	public Set<SubPriceRecord> getSubPriceRecords() {
		return subPriceRecords;
	}


	public void setSubPriceRecords(Set<SubPriceRecord> subPriceRecords) {
		this.subPriceRecords = subPriceRecords;
	}


	public boolean isNewProduct() {
		return isNewProduct;
	}


	public void setNewProduct(boolean isNewProduct) {
		this.isNewProduct = isNewProduct;
	}


	public TourRecord getTourRecord() {
		return tourRecord;
	}


	public void setTourRecord(TourRecord tourRecord) {
		this.tourRecord = tourRecord;
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


	public IndoorQuotationRecord getIndoorQuotationRecord() {
		return indoorQuotationRecord;
	}


	public void setIndoorQuotationRecord(IndoorQuotationRecord indoorQuotationRecord) {
		this.indoorQuotationRecord = indoorQuotationRecord;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
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


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
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


	public AllocationBatch getAllocationBatch() {
		return allocationBatch;
	}


	public void setAllocationBatch(AllocationBatch allocationBatch) {
		this.allocationBatch = allocationBatch;
	}


	public String getProductPosition() {
		return productPosition;
	}


	public void setProductPosition(String productPosition) {
		this.productPosition = productPosition;
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


	public Date getApprovedDate() {
		return approvedDate;
	}


	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public Set<AssignmentReallocation> getAssignmentReallocations() {
		return assignmentReallocations;
	}


	public void setAssignmentReallocations(Set<AssignmentReallocation> assignmentReallocations) {
		this.assignmentReallocations = assignmentReallocations;
	}

}
