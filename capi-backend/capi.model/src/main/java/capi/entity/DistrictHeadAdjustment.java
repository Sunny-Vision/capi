package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="DistrictHeadAdjustment")
public class DistrictHeadAdjustment extends EntityBase {
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="DistrictHeadAdjustmentId")
	private Integer districtHeadAdjustmentId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	// Responsible District
	@Column(name="District")
	private String district;
	
	// Available Man-Day
	@Column(name="AvailableManDay")
	private Double availableManDay;
	
	//Man-Day Required for responsible district
	@Column(name="DefaultAssignedManDay")
	private String defaultAssignedManDay;
	
	// Manual Adjustment
	@Column(name="ManualAdjustment")
	private Double manualAdjustment;
	
	// Man day of transfer in / out
	@Column(name="ManDayTransferred")
	private Double manDayTransferred;
	
	//Adjusted Man-Day Required for responsible district
	@Column(name="AdjustedManDay")
	private Double adjustedManDay;
	
	
	// Man-Day Balance
	@Column(name="ManDayBalance")
	private Double manDayBalance;
	
	@Column(name="Remark")
	private String remark;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AllocationBatchId", nullable = true)
	private AllocationBatch allocationBatch;	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getDistrictHeadAdjustmentId();
	}

	public Integer getDistrictHeadAdjustmentId() {
		return districtHeadAdjustmentId;
	}

	public void setDistrictHeadAdjustmentId(Integer districtHeadAdjustmentId) {
		this.districtHeadAdjustmentId = districtHeadAdjustmentId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Double getAvailableManDay() {
		return availableManDay;
	}

	public void setAvailableManDay(Double availableManDay) {
		this.availableManDay = availableManDay;
	}

	public String getDefaultAssignedManDay() {
		return defaultAssignedManDay;
	}

	public void setDefaultAssignedManDay(String defaultAssignedManDay) {
		this.defaultAssignedManDay = defaultAssignedManDay;
	}

	public Double getAdjustedManDay() {
		return adjustedManDay;
	}

	public void setAdjustedManDay(Double adjustedManDay) {
		this.adjustedManDay = adjustedManDay;
	}

	public Double getManDayBalance() {
		return manDayBalance;
	}

	public void setManDayBalance(Double manDayBalance) {
		this.manDayBalance = manDayBalance;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AllocationBatch getAllocationBatch() {
		return allocationBatch;
	}

	public void setAllocationBatch(AllocationBatch allocationBatch) {
		this.allocationBatch = allocationBatch;
	}

	public Double getManualAdjustment() {
		return manualAdjustment;
	}

	public void setManualAdjustment(Double manualAdjustment) {
		this.manualAdjustment = manualAdjustment;
	}

	public Double getManDayTransferred() {
		return manDayTransferred;
	}

	public void setManDayTransferred(Double manDayTransferred) {
		this.manDayTransferred = manDayTransferred;
	}

}
