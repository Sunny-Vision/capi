package capi.model.qualityControlManagement;

public class PECheckTaskList {

	private Integer peCheckFormId; 
	
	private Integer peCheckTaskId; 
	
	private String firm;
	
	private String batchCode;

	private String collectionDate;

	private String district;

	private String tpu;

	private String address;
	
	private Long noOfQuotation;
	
	private String status;
	
	private boolean isFieldTeamHead;
	
	private boolean isSectionHead;
	
	private boolean isCertaintyCase;
	
	private boolean isRandomCase;
	
	private Integer assignmentId;
	
	private String tel;
	
	private String outletType;
	
	private String firmStatus;

	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}

	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}

	public Integer getPeCheckTaskId() {
		return peCheckTaskId;
	}

	public void setPeCheckTaskId(Integer peCheckTaskId) {
		this.peCheckTaskId = peCheckTaskId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isFieldTeamHead() {
		return isFieldTeamHead;
	}

	public void setFieldTeamHead(boolean isFieldTeamHead) {
		this.isFieldTeamHead = isFieldTeamHead;
	}

	public boolean isSectionHead() {
		return isSectionHead;
	}

	public void setSectionHead(boolean isSectionHead) {
		this.isSectionHead = isSectionHead;
	}

	public boolean isCertaintyCase() {
		return isCertaintyCase;
	}

	public void setCertaintyCase(boolean isCertaintyCase) {
		this.isCertaintyCase = isCertaintyCase;
	}

	public boolean isRandomCase() {
		return isRandomCase;
	}

	public void setRandomCase(boolean isRandomCase) {
		this.isRandomCase = isRandomCase;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getOutletType() {
		return outletType;
	}

	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}

	public String getFirmStatus() {
		return firmStatus;
	}

	public void setFirmStatus(String firmStatus) {
		this.firmStatus = firmStatus;
	}
	
}
