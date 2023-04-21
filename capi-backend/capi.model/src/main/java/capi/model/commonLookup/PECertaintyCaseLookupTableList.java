package capi.model.commonLookup;

public class PECertaintyCaseLookupTableList {

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

	private Integer surveyMonthId;

	private Integer assignmentId;

	private String firm;

	private Integer outletCode;
	
	private String batchCode;

	private String collectionDate;

	private String district;

	private String tpu;

	private String address;

	private Long noOfQuotation;

	private long approved;
	
	private Boolean isSelected;
	
	private Boolean IsCertaintyCase;

	private String lastPECheckMonth;
	
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
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

	public long getApproved() {
		return approved;
	}

	public void setApproved(long approved) {
		this.approved = approved;
	}

	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Boolean getIsCertaintyCase() {
		return IsCertaintyCase;
	}

	public void setIsCertaintyCase(Boolean isCertaintyCase) {
		IsCertaintyCase = isCertaintyCase;
	}

	public String getLastPECheckMonth() {
		return lastPECheckMonth;
	}

	public void setLastPECheckMonth(String lastPECheckMonth) {
		this.lastPECheckMonth = lastPECheckMonth;
	}

	public Integer getOutletCode() {
		return outletCode;
	}

	public void setOutletCode(Integer outletCode) {
		this.outletCode = outletCode;
	}

}
