package capi.model.qualityControlManagement;

import java.util.List;

public class PostEnumerationCertaintyCaseEditModel {

	private Integer surveyMonthId;

	private List<Integer> assignmentIds;

	private String firm;

	private String batchCode;

	private String collectionDate;

	private String district;

	private String tpu;

	private String address;

	private Long noOfQuotation;

	private Long totalNoOfAssignments;

	private Long selectedAssignments;

	private Boolean randomCreated;
	
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}

	public List<Integer> getAssignmentIds() {
		return assignmentIds;
	}

	public void setAssignmentIds(List<Integer> assignmentIds) {
		this.assignmentIds = assignmentIds;
	}

	public String getFirm() {
		return firm;
	}

	public void setFirm(String firm) {
		this.firm = firm;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public String getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getNoOfQuotation() {
		return noOfQuotation;
	}

	public void setNoOfQuotation(Long noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
	}

	public Long getTotalNoOfAssignments() {
		return totalNoOfAssignments;
	}

	public void setTotalNoOfAssignments(Long totalNoOfAssignments) {
		this.totalNoOfAssignments = totalNoOfAssignments;
	}

	public Long getSelectedAssignments() {
		return selectedAssignments;
	}

	public void setSelectedAssignments(Long selectedAssignments) {
		this.selectedAssignments = selectedAssignments;
	}

	public Boolean getRandomCreated() {
		return randomCreated;
	}

	public void setRandomCreated(Boolean randomCreated) {
		this.randomCreated = randomCreated;
	}

}
