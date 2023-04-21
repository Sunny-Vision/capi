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

@Entity
@Table(name="Assignment")
public class Assignment extends capi.entity.EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="AssignmentId")
	private Integer assignmentId;
	
	@Column(name="AssignedCollectionDate")
	private Date assignedCollectionDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@Column(name="StartDate")
	private Date startDate;
	
	@Column(name="EndDate")
	private Date endDate;
	
	@Column(name="CollectionDate")
	private Date collectionDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SurveyMonthId", nullable = true)
	private SurveyMonth surveyMonth;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OutletId", nullable = true)
	private Outlet outlet;
	
	/**
	 * 1: Field visit
	 * 2: Telephone
	 * 3: Fax
	 * 4: Others
	 */
	@Column(name="CollectionMethod")
	private Integer collectionMethod;
	
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
	@Column(name="Status")
	private Integer status;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "assignment")
	private Set<QuotationRecord> quotationRecords = new HashSet<QuotationRecord>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "assignment")
	private Set<AssignmentUnitCategoryInfo> categoryInfo = new HashSet<AssignmentUnitCategoryInfo>();
	
	@Column(name="IsNewRecruitment")
	private boolean isNewRecruitment;	
	
	@Column(name="ReferenceNo")
	private String referenceNo;
	
	@Column(name="WorkingSession")
	private String workingSession;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignedUserId", nullable = true)
	private User assignedUser;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "assignment")
	private PECheckTask peCheckTask;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "assignment")
	private PECheckForm peCheckForm;
	
	@Column(name="IsCompleted")
	private boolean isCompleted;
	
	@Column(name="AdditionalFirmNo")
	private String additionalFirmNo;
	
	@Column(name="AdditionalFirmName")
	private String additionalFirmName;
	
	@Column(name="AdditionalFirmAddress")
	private String additionalFirmAddress;
	
	@Column(name="AdditionalContactPerson")
	private String additionalContactPerson;
	
	@Column(name="AdditionalNoOfForms")
	private Integer additionalNoOfForms;
	
	@Column(name="Survey")
	private String survey;
	
	@Column(name="IsImportedAssignment")
	private boolean isImportedAssignment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AdditionalDistrictId", nullable = true)
	private District additionalDistrict;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AdditionalTpuId", nullable = true)
	private Tpu additionalTpu;
		
	@Column(name="AdditionalLatitude")
	private String additionalLatitude;
	
	@Column(name="AdditionalLongitude")
	private String additionalLongitude;
	
	@Column(name="OutletDiscountRemark")
	private String outletDiscountRemark;
	
	@Column(name="LockFirmStatus")
	private boolean lockFirmStatus;

	@Column(name="ApprovedDate")
	private Date approvedDate;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "assignments")
	private Set<ItineraryPlan> plannedItinerary = new HashSet<ItineraryPlan>();
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "unPlanAssignments")
	private Set<ItineraryPlan> unplannedItinerary = new HashSet<ItineraryPlan>();


	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "assignments")
	private Set<AssignmentReallocation> assignmentReallocations = new HashSet<AssignmentReallocation>();

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Date getAssignedCollectionDate() {
		return assignedCollectionDate;
	}

	public void setAssignedCollectionDate(Date assignedCollectionDate) {
		this.assignedCollectionDate = assignedCollectionDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<QuotationRecord> getQuotationRecords() {
		return quotationRecords;
	}

	public void setQuotationRecords(Set<QuotationRecord> quotationRecords) {
		this.quotationRecords = quotationRecords;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getAssignmentId();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public Outlet getOutlet() {
		return outlet;
	}

	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}

	public Integer getCollectionMethod() {
		return collectionMethod;
	}

	public void setCollectionMethod(Integer collectionMethod) {
		this.collectionMethod = collectionMethod;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public SurveyMonth getSurveyMonth() {
		return surveyMonth;
	}

	public void setSurveyMonth(SurveyMonth surveyMonth) {
		this.surveyMonth = surveyMonth;
	}

	public boolean isNewRecruitment() {
		return isNewRecruitment;
	}

	public void setNewRecruitment(boolean isNewRecruitment) {
		this.isNewRecruitment = isNewRecruitment;
	}

	public User getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(User assignedUser) {
		this.assignedUser = assignedUser;
	}

	public PECheckTask getPeCheckTask() {
		return peCheckTask;
	}

	public void setPeCheckTask(PECheckTask peCheckTask) {
		this.peCheckTask = peCheckTask;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String getAdditionalFirmNo() {
		return additionalFirmNo;
	}

	public void setAdditionalFirmNo(String additionalFirmNo) {
		this.additionalFirmNo = additionalFirmNo;
	}

	public String getAdditionalFirmName() {
		return additionalFirmName;
	}

	public void setAdditionalFirmName(String additionalFirmName) {
		this.additionalFirmName = additionalFirmName;
	}

	public String getAdditionalFirmAddress() {
		return additionalFirmAddress;
	}

	public void setAdditionalFirmAddress(String additionalFirmAddress) {
		this.additionalFirmAddress = additionalFirmAddress;
	}

	public String getAdditionalContactPerson() {
		return additionalContactPerson;
	}

	public void setAdditionalContactPerson(String additionalContactPerson) {
		this.additionalContactPerson = additionalContactPerson;
	}

	public Integer getAdditionalNoOfForms() {
		return additionalNoOfForms;
	}

	public void setAdditionalNoOfForms(Integer additionalNoOfForms) {
		this.additionalNoOfForms = additionalNoOfForms;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public boolean isImportedAssignment() {
		return isImportedAssignment;
	}

	public void setImportedAssignment(boolean isImportedAssignment) {
		this.isImportedAssignment = isImportedAssignment;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@Deprecated
	public String getWorkingSession() {
		return workingSession;
	}
	@Deprecated
	public void setWorkingSession(String workingSession) {
		this.workingSession = workingSession;
	}

	public District getAdditionalDistrict() {
		return additionalDistrict;
	}

	public void setAdditionalDistrict(District additionalDistrict) {
		this.additionalDistrict = additionalDistrict;
	}

	public Tpu getAdditionalTpu() {
		return additionalTpu;
	}

	public void setAdditionalTpu(Tpu additionalTpu) {
		this.additionalTpu = additionalTpu;
	}

	public String getAdditionalLatitude() {
		return additionalLatitude;
	}

	public void setAdditionalLatitude(String additionalLatitude) {
		this.additionalLatitude = additionalLatitude;
	}

	public String getAdditionalLongitude() {
		return additionalLongitude;
	}

	public void setAdditionalLongitude(String additionalLongitude) {
		this.additionalLongitude = additionalLongitude;
	}

	public Set<AssignmentUnitCategoryInfo> getCategoryInfo() {
		return categoryInfo;
	}

	public void setCategoryInfo(Set<AssignmentUnitCategoryInfo> categoryInfo) {
		this.categoryInfo = categoryInfo;
	}

	public String getOutletDiscountRemark() {
		return outletDiscountRemark;
	}

	public void setOutletDiscountRemark(String outletDiscountRemark) {
		this.outletDiscountRemark = outletDiscountRemark;
	}

	public boolean isLockFirmStatus() {
		return lockFirmStatus;
	}

	public void setLockFirmStatus(boolean lockFirmStatus) {
		this.lockFirmStatus = lockFirmStatus;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public PECheckForm getPeCheckForm() {
		return peCheckForm;
	}

	public void setPeCheckForm(PECheckForm peCheckForm) {
		this.peCheckForm = peCheckForm;
	}

	public Set<ItineraryPlan> getPlannedItinerary() {
		return plannedItinerary;
	}

	public void setPlannedItinerary(Set<ItineraryPlan> plannedItinerary) {
		this.plannedItinerary = plannedItinerary;
	}

	public Set<ItineraryPlan> getUnplannedItinerary() {
		return unplannedItinerary;
	}

	public void setUnplannedItinerary(Set<ItineraryPlan> unplannedItinerary) {
		this.unplannedItinerary = unplannedItinerary;
	}

	public Set<AssignmentReallocation> getAssignmentReallocations() {
		return assignmentReallocations;
	}

	public void setAssignmentReallocations(Set<AssignmentReallocation> assignmentReallocations) {
		this.assignmentReallocations = assignmentReallocations;
	}

}
