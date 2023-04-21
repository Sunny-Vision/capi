package capi.model.itineraryPlanning;

import java.util.Date;
import java.util.List;

public class QCItineraryPlanModel {
	
	private Integer qcItineraryPlanId;
	private Date createdDate;
	private Date modifiedDate;
	private String inputModifiedDate;
	private Date date;
	private String inputDate;
	private Integer userId;
	private String user;
	private String submitTo;
	private Integer submitToId;
	private String session;
	private String rejectReason;
	private List <QCItineraryPlanItemModel> qcItineraryPlanItemModel;
	private String submitStatus;	
	private Integer version;
	
	public Integer getQcItineraryPlanId() {
		return qcItineraryPlanId;
	}
	public void setQcItineraryPlanId(Integer qcItineraryPlanId) {
		this.qcItineraryPlanId = qcItineraryPlanId;
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
	public String getInputModifiedDate() {
		return inputModifiedDate;
	}
	public void setInputModifiedDate(String inputModifiedDate) {
		this.inputModifiedDate = inputModifiedDate;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getInputDate() {
		return inputDate;
	}
	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getSubmitTo() {
		return submitTo;
	}
	public void setSubmitTo(String submitTo) {
		this.submitTo = submitTo;
	}
	public Integer getSubmitToId() {
		return submitToId;
	}
	public void setSubmitToId(Integer submitToId) {
		this.submitToId = submitToId;
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
	public List<QCItineraryPlanItemModel> getQcItineraryPlanItemModel() {
		return qcItineraryPlanItemModel;
	}
	public void setQcItineraryPlanItemModel(List<QCItineraryPlanItemModel> qcItineraryPlanItemModel) {
		this.qcItineraryPlanItemModel = qcItineraryPlanItemModel;
	}
	public String getSubmitStatus() {
		return submitStatus;
	}
	public void setSubmitStatus(String submitStatus) {
		this.submitStatus = submitStatus;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
