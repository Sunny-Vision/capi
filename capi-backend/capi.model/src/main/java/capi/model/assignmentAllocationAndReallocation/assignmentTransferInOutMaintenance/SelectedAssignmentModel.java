package capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance;

import java.io.Serializable;
import java.math.BigDecimal;

public class SelectedAssignmentModel implements Serializable{
	private Integer id;
	private String firm;
	private String district;
	private String tpu;
	private String address;
	private String startDate;
	private String endDate;
	private Long noOfQuotation;
	private Double fullSeasonLoading;
	private Double summerLoading;
	private Double winterLoading;
	private Double requiredManDay;
	private BigDecimal requiredManDayBD;
	private String requiredManDayString;
	private Integer userId;
	private String batchCode;
	private String referenceNo;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public Long getNoOfQuotation() {
		return noOfQuotation;
	}
	public void setNoOfQuotation(Long noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
	}
	public Double getFullSeasonLoading() {
		return fullSeasonLoading;
	}
	public void setFullSeasonLoading(Double fullSeasonLoading) {
		this.fullSeasonLoading = fullSeasonLoading;
	}
	public Double getSummerLoading() {
		return summerLoading;
	}
	public void setSummerLoading(Double summerLoading) {
		this.summerLoading = summerLoading;
	}
	public Double getWinterLoading() {
		return winterLoading;
	}
	public void setWinterLoading(Double winterLoading) {
		this.winterLoading = winterLoading;
	}
	public Double getRequiredManDay() {
		return requiredManDay;
	}
	public void setRequiredManDay(Double requiredManDay) {
		this.requiredManDay = requiredManDay;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	public BigDecimal getRequiredManDayBD() {
		return requiredManDayBD;
	}
	public void setRequiredManDayBD(BigDecimal requiredManDayBD) {
		this.requiredManDayBD = requiredManDayBD;
	}
	public String getRequiredManDayString() {
		return requiredManDayString;
	}
	public void setRequiredManDayString(String requiredManDayString) {
		this.requiredManDayString = requiredManDayString;
	}
}
