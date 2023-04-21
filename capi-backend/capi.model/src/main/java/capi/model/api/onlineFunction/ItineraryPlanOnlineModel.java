package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.ItineraryPlanAssignmentSyncData;
import capi.model.api.dataSync.ItineraryPlanOutletSyncData;
import capi.model.api.dataSync.ItineraryUnPlanAssignmentSyncData;
import capi.model.api.dataSync.MajorLocationSyncData;
import capi.model.api.dataSync.OutletSyncData;

public class ItineraryPlanOnlineModel {
	
	private Integer itineraryPlanId;
	
	private Date date;
	
	private Integer userId;
	
	private Integer supervisorId;
	
	private String session;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String status;
	
	private String rejectReason;
	
	private List<ItineraryPlanOutletSyncData> itineraryPlanOutlets;
	
	private List<ItineraryPlanAssignmentSyncData> itineraryPlanAssignments;
	
	private List<ItineraryUnPlanAssignmentSyncData> itineraryUnPlanAssignments;
	
	private List<MajorLocationSyncData> majorLocations;

	private List<OutletSyncData> outlets;
	
	public Integer getItineraryPlanId() {
		return itineraryPlanId;
	}

	public void setItineraryPlanId(Integer itineraryPlanId) {
		this.itineraryPlanId = itineraryPlanId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public List<ItineraryPlanOutletSyncData> getItineraryPlanOutlets() {
		return itineraryPlanOutlets;
	}

	public void setItineraryPlanOutlets(List<ItineraryPlanOutletSyncData> itineraryPlanOutlets) {
		this.itineraryPlanOutlets = itineraryPlanOutlets;
	}

	public List<ItineraryPlanAssignmentSyncData> getItineraryPlanAssignments() {
		return itineraryPlanAssignments;
	}

	public void setItineraryPlanAssignments(List<ItineraryPlanAssignmentSyncData> itineraryPlanAssignments) {
		this.itineraryPlanAssignments = itineraryPlanAssignments;
	}

	public List<ItineraryUnPlanAssignmentSyncData> getItineraryUnPlanAssignments() {
		return itineraryUnPlanAssignments;
	}

	public void setItineraryUnPlanAssignments(List<ItineraryUnPlanAssignmentSyncData> itineraryUnPlanAssignments) {
		this.itineraryUnPlanAssignments = itineraryUnPlanAssignments;
	}

	public List<MajorLocationSyncData> getMajorLocations() {
		return majorLocations;
	}

	public void setMajorLocations(List<MajorLocationSyncData> majorLocations) {
		this.majorLocations = majorLocations;
	}

	public List<OutletSyncData> getOutlets() {
		return outlets;
	}

	public void setOutlets(List<OutletSyncData> outlets) {
		this.outlets = outlets;
	}

}
