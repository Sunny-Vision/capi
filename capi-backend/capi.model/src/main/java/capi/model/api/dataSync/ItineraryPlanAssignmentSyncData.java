package capi.model.api.dataSync;

import java.util.Date;

public class ItineraryPlanAssignmentSyncData {

	private Integer assignmentId;
	
	private Integer itineraryPlanId;
	
	private Date createdDate;
	
	private Date modifiedDate;

	private Integer numOfQuotationRecords;
	
	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Integer getItineraryPlanId() {
		return itineraryPlanId;
	}

	public void setItineraryPlanId(Integer itineraryPlanId) {
		this.itineraryPlanId = itineraryPlanId;
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

	public Integer getNumOfQuotationRecords() {
		return numOfQuotationRecords;
	}

	public void setNumOfQuotationRecords(Integer numOfQuotationRecords) {
		this.numOfQuotationRecords = numOfQuotationRecords;
	}
	
}
