package capi.model.assignmentAllocationAndReallocation;

import java.util.Date;
import java.util.List;

public class AssignmentReallocationApprovalEditModel {

	public static class BatchCodeMapping {

		private Integer assignmentId;

		private String batchCode;

		public Integer getAssignmentId() {
			return assignmentId;
		}

		public void setAssignmentId(Integer assignmentId) {
			this.assignmentId = assignmentId;
		}

		public String getBatchCode() {
			return batchCode;
		}

		public void setBatchCode(String batchCode) {
			this.batchCode = batchCode;
		}

	}

	private Integer assignmentReallocationId;

	private Integer creatorId;

	private String creator;

	private Integer originalUserId;

	private String originalUser;

	private Integer targetUserId;

	private String targetUser;

	private Integer submitToUserId;

	private String collectionDate;

	private String startDate;

	private String endDate;

	private String firm;

	private String district;

	private String tpu;

	private String batchCode;

	private Long noOfQuotation;

	private Integer assignmentId;

	private Integer quotationRecordId;

	private List<Integer> assignmentIds;

	private List<Integer> quotationRecordIds;

	private String assignmentStatus;

	private String selected;

	private String rejectReason;

	private String displayName;

	private String category;

	private String quotationStatus;

	private Date createdDate;

	private Date modifiedDate;

	public Integer getAssignmentReallocationId() {
		return assignmentReallocationId;
	}

	public void setAssignmentReallocationId(Integer assignmentReallocationId) {
		this.assignmentReallocationId = assignmentReallocationId;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
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

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Integer getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(Integer quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
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

	public String getAssignmentStatus() {
		return assignmentStatus;
	}

	public void setAssignmentStatus(String assignmentStatus) {
		this.assignmentStatus = assignmentStatus;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getQuotationStatus() {
		return quotationStatus;
	}

	public void setQuotationStatus(String quotationStatus) {
		this.quotationStatus = quotationStatus;
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
