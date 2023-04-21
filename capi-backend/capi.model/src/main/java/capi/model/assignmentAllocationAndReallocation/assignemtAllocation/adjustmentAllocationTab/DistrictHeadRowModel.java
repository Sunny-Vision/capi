package capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab;

import java.io.Serializable;

public class DistrictHeadRowModel implements Serializable{
	private Integer userId;
	private String userName;
	private String districts;
	private Double availableManDays;
	private String manDayRequiredForResponsibleDistricts;
	private Double manDayRequired;
	private Double manualAdjustment;
	private Double adjustedManDayRequiredForResponsibleDistricts;
	private Double manDayOfTransferInOut;
	private Double manDayOfBalance;
	
	private long totalAssignment;
	private String noOfAssignment;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDistricts() {
		return districts;
	}
	public void setDistricts(String districts) {
		this.districts = districts;
	}
	public Double getAvailableManDays() {
		return availableManDays;
	}
	public void setAvailableManDays(Double availableManDays) {
		this.availableManDays = availableManDays;
	}
	public String getManDayRequiredForResponsibleDistricts() {
		return manDayRequiredForResponsibleDistricts;
	}
	public void setManDayRequiredForResponsibleDistricts(
			String manDayRequiredForResponsibleDistricts) {
		this.manDayRequiredForResponsibleDistricts = manDayRequiredForResponsibleDistricts;
	}
	public Double getManDayRequired() {
		return manDayRequired;
	}
	public void setManDayRequired(Double manDayRequired) {
		this.manDayRequired = manDayRequired;
	}
	public Double getManualAdjustment() {
		return manualAdjustment;
	}
	public void setManualAdjustment(Double manualAdjustment) {
		this.manualAdjustment = manualAdjustment;
	}
	public Double getAdjustedManDayRequiredForResponsibleDistricts() {
		return adjustedManDayRequiredForResponsibleDistricts;
	}
	public void setAdjustedManDayRequiredForResponsibleDistricts(
			Double adjustedManDayRequiredForResponsibleDistricts) {
		this.adjustedManDayRequiredForResponsibleDistricts = adjustedManDayRequiredForResponsibleDistricts;
	}
	public Double getManDayOfBalance() {
		return manDayOfBalance;
	}
	public void setManDayOfBalance(Double manDayOfBalance) {
		this.manDayOfBalance = manDayOfBalance;
	}
	public Double getManDayOfTransferInOut() {
		return manDayOfTransferInOut;
	}
	public void setManDayOfTransferInOut(Double manDayOfTransferInOut) {
		this.manDayOfTransferInOut = manDayOfTransferInOut;
	}
	
	
	public long getTotalAssignment() {
		return totalAssignment;
	}
	public void setTotalAssignment(long totalAssignment) {
		this.totalAssignment = totalAssignment;
	}
	public String getNoOfAssignment() {
		return noOfAssignment;
	}
	public void setNoOfAssignment(String noOfAssignment) {
		this.noOfAssignment = noOfAssignment;
	}
	
}
