package capi.model.api.dataSync;

import java.util.Date;

public class ItineraryPlanOutletSyncData {

	private Integer itineraryPlanOutletId;
	
	private Integer outletId;
	
	private Integer itineraryPlanId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer majorLocationId;
	
	private Integer sequence;
	
	private Integer majorLocationSequence;
	
	private String firmCode;
	
	private String referenceNo;
	
	private Integer assignmentId;
	
	private Integer planType;

	public Integer getItineraryPlanOutletId() {
		return itineraryPlanOutletId;
	}

	public void setItineraryPlanOutletId(Integer itineraryPlanOutletId) {
		this.itineraryPlanOutletId = itineraryPlanOutletId;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Integer getMajorLocationId() {
		return majorLocationId;
	}

	public void setMajorLocationId(Integer majorLocationId) {
		this.majorLocationId = majorLocationId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getMajorLocationSequence() {
		return majorLocationSequence;
	}

	public void setMajorLocationSequence(Integer majorLocationSequence) {
		this.majorLocationSequence = majorLocationSequence;
	}

	public String getFirmCode() {
		return firmCode;
	}

	public void setFirmCode(String firmCode) {
		this.firmCode = firmCode;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Integer getPlanType() {
		return planType;
	}

	public void setPlanType(Integer planType) {
		this.planType = planType;
	}

	public Integer getItineraryPlanId() {
		return itineraryPlanId;
	}

	public void setItineraryPlanId(Integer itineraryPlanId) {
		this.itineraryPlanId = itineraryPlanId;
	}
	
}
