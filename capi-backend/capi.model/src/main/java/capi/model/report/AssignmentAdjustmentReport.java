package capi.model.report;

import java.util.Date;

public class AssignmentAdjustmentReport {
	private String stage;
	private Date referenceMonth;
	private String batchName;
	private String fromUser;
	private String toUser;
	private Integer numOfQuotation;
	private String outletCode;
	private String outletName;
	private String district;
	private String tpu;
	private String collectionMethod;
	private String recommendBy;
	private Date recommendDate;
	private String approveBy;
	private Date approveDate;
	private Integer outletId;
	
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
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public Integer getNumOfQuotation() {
		return numOfQuotation;
	}
	public void setNumOfQuotation(Integer numOfQuotation) {
		this.numOfQuotation = numOfQuotation;
	}
	public String getOutletCode() {
		return outletCode;
	}
	public void setOutletCode(String outletCode) {
		this.outletCode = outletCode;
	}
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
	public String getOutletName() {
		return outletName;
	}
	public void setOutletName(String outletName) {
		this.outletName = outletName;
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
	public String getCollectionMethod() {
		return collectionMethod;
	}
	public void setCollectionMethod(String collectionMethod) {
		this.collectionMethod = collectionMethod;
	}
	public String getRecommendBy() {
		return recommendBy;
	}
	public void setRecommendBy(String recommendBy) {
		this.recommendBy = recommendBy;
	}
	public Date getRecommendDate() {
		return recommendDate;
	}
	public void setRecommendDate(Date recommendDate) {
		this.recommendDate = recommendDate;
	}
	public String getApproveBy() {
		return approveBy;
	}
	public void setApproveBy(String approveBy) {
		this.approveBy = approveBy;
	}
	public Date getApproveDate() {
		return approveDate;
	}
	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}
}
