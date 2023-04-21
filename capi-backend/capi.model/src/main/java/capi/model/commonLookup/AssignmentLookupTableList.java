package capi.model.commonLookup;

import java.util.Date;

public class AssignmentLookupTableList {
	private Integer assignmentId;
	private Integer outletId;
	private String firm;
	private String district;
	private String tpu;
	private String deadline;
	private String address;
	private Long noOfQuotation;
	private Date convenientStartTime;
	private Date convenientEndTime;
	private String convenientTime;
	private String outletRemark;
	private String referenceNo;
	private String batchCode;
	
	public Integer getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
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
	public String getDeadline() {
		return deadline;
	}
	public void setDeadline(String deadline) {
		this.deadline = deadline;
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
	public Date getConvenientStartTime() {
		return convenientStartTime;
	}
	public void setConvenientStartTime(Date convenientStartTime) {
		this.convenientStartTime = convenientStartTime;
	}
	public Date getConvenientEndTime() {
		return convenientEndTime;
	}
	public void setConvenientEndTime(Date convenientEndTime) {
		this.convenientEndTime = convenientEndTime;
	}
	public String getConvenientTime() {
		return convenientTime;
	}
	public void setConvenientTime(String convenientTime) {
		this.convenientTime = convenientTime;
	}
	public String getOutletRemark() {
		return outletRemark;
	}
	public void setOutletRemark(String outletRemark) {
		this.outletRemark = outletRemark;
	}
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	
}
