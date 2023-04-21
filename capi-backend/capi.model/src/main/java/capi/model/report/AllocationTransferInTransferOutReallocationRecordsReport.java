package capi.model.report;

import java.util.Date;

public class AllocationTransferInTransferOutReallocationRecordsReport {

	private Integer firmCode;
	
	private String district;
	
	private String tpu;
	
	private Long numOfQuotation;
	
	private String firmName;
	
	private String inFrom;
	
	private String outTo;
	
	private String recommendedBy;
	
	private Date recommendedDate;
	
	private String approvedBy;
	
	private Date approvedDate;
	
	private String allocationBatch;
	
	private String stage;
	
	private Integer assignmentId;

	public Integer getFirmCode() {
		return firmCode;
	}

	public void setFirmCode(Integer firmCode) {
		this.firmCode = firmCode;
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

	public Long getNumOfQuotation() {
		return numOfQuotation;
	}

	public void setNumOfQuotation(Long numOfQuotation) {
		this.numOfQuotation = numOfQuotation;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getInFrom() {
		return inFrom;
	}

	public void setInFrom(String inFrom) {
		this.inFrom = inFrom;
	}

	public String getOutTo() {
		return outTo;
	}

	public void setOutTo(String outTo) {
		this.outTo = outTo;
	}

	public String getRecommendedBy() {
		return recommendedBy;
	}

	public void setRecommendedBy(String recommendedBy) {
		this.recommendedBy = recommendedBy;
	}

	public Date getRecommendedDate() {
		return recommendedDate;
	}

	public void setRecommendedDate(Date recommendedDate) {
		this.recommendedDate = recommendedDate;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getAllocationBatch() {
		return allocationBatch;
	}

	public void setAllocationBatch(String allocationBatch) {
		this.allocationBatch = allocationBatch;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	
	public Integer getAssignmentId() {
		return assignmentId;
	}
	

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	
}
