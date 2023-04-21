package capi.model.commonLookup;

import java.util.Date;
import java.util.List;

public class AssignmentReallocationLookupTableList {

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

	private Integer id;

	private Integer originalUserId;

	private String originalUser;

	private Integer targetUserId;

	private String targetUser;

	private Integer submitToUserId;

	private String submitToUser;

	private String collectionDate;

	private String startDate;

	private String endDate;

	private String firm;

	private String district;

	private String tpu;

	private String batchCode;

	private Long noOfQuotation;

	private List<Integer> assignmentId;

	private String assignmentStatus;

	private Date createdDate;

	private Date modifiedDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public List<Integer> getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(List<Integer> assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getAssignmentStatus() {
		return assignmentStatus;
	}

	public void setAssignmentStatus(String assignmentStatus) {
		this.assignmentStatus = assignmentStatus;
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
