package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.AssignmentUnitCategoryInfoSyncData;
import capi.model.api.dataSync.OutletSyncData;

public class AssignmentOnlineModel {

	private Integer assignmentId;
	
	private Date assignedCollectionDate;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Date startDate;
	
	private Date endDate;
	
	private Integer collectionMethod;
	
	private Integer status;
	
	private Date collectionDate;
	
	private boolean isNewRecruitment;
	
	private String referenceNo;
	
	private String workingSession;
	
	private boolean isCompleted;
	
	private String additionalFirmNo;
	
	private String additionalFirmName;
	
	private String additionalFirmAddress;
	
	private String additionalContactPerson;
	
	private Integer additionalNoOfForms;
	
	private String survey;
	
	private boolean isImportedAssignment;
	
	private String additionalLatitude;
	
	private String additionalLongitude;
	
	private String outletDiscountRemark;
	
	private boolean lockFirmStatus;
	
	private Date approvedDate;
	
	private Integer localId;
	
	private String localDbRecordStatus;
	
	//Foreign Key

	private Integer userId;
	
	private Integer outletId;
	
	private Integer surveyMonthId;
	
	private Integer assignedUserId;
	
	private Integer additionalDistrictId;
	
	private Integer additionalTpuId;
	
	//Foreign Model
	
	private OutletSyncData outlet;
	
	private List<QuotationRecordOnlineModel> quotationRecords;
	
	private List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategoryInfos;

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

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getWorkingSession() {
		return workingSession;
	}

	public void setWorkingSession(String workingSession) {
		this.workingSession = workingSession;
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

	public String getOutletDiscountRemark() {
		return outletDiscountRemark;
	}

	public void setOutletDiscountRemark(String outletDiscountRemark) {
		this.outletDiscountRemark = outletDiscountRemark;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}

	public Integer getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(Integer assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

	public Integer getAdditionalDistrictId() {
		return additionalDistrictId;
	}

	public void setAdditionalDistrictId(Integer additionalDistrictId) {
		this.additionalDistrictId = additionalDistrictId;
	}

	public Integer getAdditionalTpuId() {
		return additionalTpuId;
	}

	public void setAdditionalTpuId(Integer additionalTpuId) {
		this.additionalTpuId = additionalTpuId;
	}

	public List<QuotationRecordOnlineModel> getQuotationRecords() {
		return quotationRecords;
	}

	public void setQuotationRecords(List<QuotationRecordOnlineModel> quotationRecords) {
		this.quotationRecords = quotationRecords;
	}

	public List<AssignmentUnitCategoryInfoSyncData> getAssignmentUnitCategoryInfos() {
		return assignmentUnitCategoryInfos;
	}

	public void setAssignmentUnitCategoryInfos(List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategoryInfos) {
		this.assignmentUnitCategoryInfos = assignmentUnitCategoryInfos;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}

	public OutletSyncData getOutlet() {
		return outlet;
	}

	public void setOutlet(OutletSyncData outlet) {
		this.outlet = outlet;
	}

	public boolean isNewRecruitment() {
		return isNewRecruitment;
	}

	public void setNewRecruitment(boolean isNewRecruitment) {
		this.isNewRecruitment = isNewRecruitment;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean isImportedAssignment() {
		return isImportedAssignment;
	}

	public void setImportedAssignment(boolean isImportedAssignment) {
		this.isImportedAssignment = isImportedAssignment;
	}

	public boolean isLockFirmStatus() {
		return lockFirmStatus;
	}

	public void setLockFirmStatus(boolean lockFirmStatus) {
		this.lockFirmStatus = lockFirmStatus;
	}
	
}
