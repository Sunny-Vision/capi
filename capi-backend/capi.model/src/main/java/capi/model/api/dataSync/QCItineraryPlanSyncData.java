package capi.model.api.dataSync;

import java.util.Date;

public class QCItineraryPlanSyncData {

	private Integer qcItineraryPlanId;
	
	private Date date;
	
	private String session;
	
	private String rejectReason;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String status;
	
	private Integer userId;
	
	private Integer submitTo;
	
	public Integer getQcItineraryPlanId() {
		return qcItineraryPlanId;
	}

	public void setQcItineraryPlanId(Integer qcItineraryPlanId) {
		this.qcItineraryPlanId = qcItineraryPlanId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getSubmitTo() {
		return submitTo;
	}

	public void setSubmitTo(Integer submitTo) {
		this.submitTo = submitTo;
	}
}
