package capi.model.report;

import java.util.Date;

public class AssignmentAllocationSummaryReport {

	private Integer firmCode;
	
	private String district;
	
	private String tpu;
	
	private Long numOfQuotation;
	
	private String firmName;
	
	private String inFrom;
	
	private String inFromName;
	
	private String outTo;
	
	private String outToName;
	
	private String recommendedBy;
	
	private String recommendedByName;
	
	private Date recommendedDate;
	
	private String approvedBy;
	
	private String approvedByName;
	
	private Date approvedDate;
	
	private String allocationBatch;
	
	private Integer collectionMethod;
	
	private String stage;
	
	private Date referenceMonth;
	
	private Integer outletId;
	
	public Integer getOutletId() {
		return outletId;
	}
	
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

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

	public String getInFromName() {
		return inFromName;
	}

	public void setInFromName(String inFromName) {
		this.inFromName = inFromName;
	}

	public String getOutTo() {
		return outTo;
	}

	public void setOutTo(String outTo) {
		this.outTo = outTo;
	}

	public String getOutToName() {
		return outToName;
	}

	public void setOutToName(String outToName) {
		this.outToName = outToName;
	}

	public String getRecommendedBy() {
		return recommendedBy;
	}

	public void setRecommendedBy(String recommendedBy) {
		this.recommendedBy = recommendedBy;
	}

	public String getRecommendedByName() {
		return recommendedByName;
	}

	public void setRecommendedByName(String recommendedByName) {
		this.recommendedByName = recommendedByName;
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

	public String getApprovedByName() {
		return approvedByName;
	}

	public void setApprovedByName(String approvedByName) {
		this.approvedByName = approvedByName;
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

	public Integer getCollectionMethod() {
		return collectionMethod;
	}

	public void setCollectionMethod(Integer collectionMethod) {
		this.collectionMethod = collectionMethod;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	
}
