package capi.model.api.dataSync;

import java.util.Date;

public class ItineraryPlanSyncData {

	private Integer itineraryPlanId;
	
	private Date date;
	
	private Integer userId;
	
	private Integer supervisorId;
	
	private String session;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String status;
	
	private String rejectReason;

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

}
