package capi.model.report;

import java.util.Date;

public class AssignmentAdjustmentSummaryReport {
	private String stage;
	private Date referenceMonth;
	private String batchName;
	private String collectionMethod;
	private String fromUser;
	private String toUser;
	private Integer numOfAssignments;
	private Integer numOfQuotations;
	private String recommendBy;
	private Date recommendDate;
	private String approveBy;
	private Date approveDate;
	private String reasons;
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
	public String getCollectionMethod() {
		return collectionMethod;
	}
	public void setCollectionMethod(String collectionMethod) {
		this.collectionMethod = collectionMethod;
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
	public Integer getNumOfAssignments() {
		return numOfAssignments;
	}
	public void setNumOfAssignments(Integer numOfAssignments) {
		this.numOfAssignments = numOfAssignments;
	}
	public Integer getNumOfQuotations() {
		return numOfQuotations;
	}
	public void setNumOfQuotations(Integer numOfQuotations) {
		this.numOfQuotations = numOfQuotations;
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
	public String getReasons() {
		return reasons;
	}
	public void setReasons(String reasons) {
		this.reasons = reasons;
	}
}
