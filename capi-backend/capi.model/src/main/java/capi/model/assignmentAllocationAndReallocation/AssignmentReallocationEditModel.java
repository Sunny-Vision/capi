package capi.model.assignmentAllocationAndReallocation;

import java.util.Date;
import java.util.List;

public class AssignmentReallocationEditModel {

	private Integer assignmentReallocationId;

	private Integer originalUserId;

	private String originalUser;

	private Integer targetUserId;

	private String targetUser;

	private Integer submitToUserId;

	private String submitToUser;

	private Integer creatorId;

	private List<Integer> assignmentIds;

	private List<Integer> quotationRecordIds;

	private Integer id;

	private String collectionDate;

	private String startDate;

	private String endDate;

	private String firm;

	private String district;

	private String tpu;

	private String batchCode;

	private Long noOfQuotation;

	private String rejectReason;

	private String newDate;

	private Date createdDate;

	private Date modifiedDate;

	private boolean fieldSupervisor;

	private boolean fieldOfficer;

	public Integer getAssignmentReallocationId() {
		return assignmentReallocationId;
	}

	public void setAssignmentReallocationId(Integer assignmentReallocationId) {
		this.assignmentReallocationId = assignmentReallocationId;
	}

	public Integer getOriginalUserId() {
		return originalUserId;
	}

	public void setOriginalUserId(Integer originalUserId) {
		this.originalUserId = originalUserId;
	}

	public String getOriginalUser() {
		return originalUser;
	}

	public void setOriginalUser(String originalUser) {
		this.originalUser = originalUser;
	}

	public Integer getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(Integer targetUserId) {
		this.targetUserId = targetUserId;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public Integer getSubmitToUserId() {
		return submitToUserId;
	}

	public void setSubmitToUserId(Integer submitToUserId) {
		this.submitToUserId = submitToUserId;
	}

	public String getSubmitToUser() {
		return submitToUser;
	}

	public void setSubmitToUser(String submitToUser) {
		this.submitToUser = submitToUser;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public List<Integer> getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(List<Integer> assignmentIds) {
		this.assignmentIds = assignmentIds;
	}

	public List<Integer> getQuotationRecordIds() {
		return quotationRecordIds;
	}

	public void setQuotationRecordIds(List<Integer> quotationRecordIds) {
		this.quotationRecordIds = quotationRecordIds;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getFirm() {
		return firm;
	}

	public void setFirm(String firm) {
		this.firm = firm;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTpu() {
		return tpu;
	}

	public void setTpu(String tpu) {
		this.tpu = tpu;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public Long getNoOfQuotation() {
		return noOfQuotation;
	}

	public void setNoOfQuotation(Long noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getNewDate() {
		return newDate;
	}

	public void setNewDate(String newDate) {
		this.newDate = newDate;
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

	public boolean isFieldSupervisor() {
		return fieldSupervisor;
	}

	public void setFieldSupervisor(boolean fieldSupervisor) {
		this.fieldSupervisor = fieldSupervisor;
	}

	public boolean isFieldOfficer() {
		return fieldOfficer;
	}

	public void setFieldOfficer(boolean fieldOfficer) {
		this.fieldOfficer = fieldOfficer;
	}

}
