package capi.model.api.onlineFunction;

import java.util.Date;
import java.util.List;

import capi.model.api.dataSync.QCItineraryPlanItemSyncData;

public class QCItineraryPlanOnlineModel {
	
	private Integer qcItineraryPlanId;
	
	private Date date;
	
	private String session;
	
	private String rejectReason;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String status;
	
	private Integer userId;
	
	private Integer submitTo;
	
	private List<QCItineraryPlanItemSyncData> qcItineraryPlanItems;
	
	private List<ItineraryPlanOnlineModel> itineraryPlans;

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
	
	public List<QCItineraryPlanItemSyncData> getQcItineraryPlanItems() {
		return qcItineraryPlanItems;
	}

	public void setQcItineraryPlanItems(List<QCItineraryPlanItemSyncData> qcItineraryPlanItems) {
		this.qcItineraryPlanItems = qcItineraryPlanItems;
	}

	public List<ItineraryPlanOnlineModel> getItineraryPlans() {
		return itineraryPlans;
	}

	public void setItineraryPlans(List<ItineraryPlanOnlineModel> itineraryPlans) {
		this.itineraryPlans = itineraryPlans;
	}
	
}
