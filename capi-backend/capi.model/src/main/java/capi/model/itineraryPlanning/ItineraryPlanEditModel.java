package capi.model.itineraryPlanning;

import java.util.Date;
import java.util.List;

public class ItineraryPlanEditModel {
	private Integer itineraryPlanId;
	private Integer version;
	private Date createdDate;
	private Date modifiedDate;
	private Date date;
	private String inputDate;
	private String session;
	private String status;
	private String rejectReason;
	private Integer userId;
	private Integer supervisorId;
	private List <MajorLocationModel> majorLocations;
	
	public Integer getItineraryPlanId() {
		return itineraryPlanId;
	}
	public void setItineraryPlanId(Integer itineraryPlanId) {
		this.itineraryPlanId = itineraryPlanId;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSession() {
		return session;
	}
	public String getInputDate() {
		return inputDate;
	}
	public void setInputDate(String inputDate) {
		this.inputDate = inputDate;
	}
	public void setSession(String session) {
		this.session = session;
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
	public List< MajorLocationModel> getMajorLocations() {
		return majorLocations;
	}
	public void setMajorLocations(List<MajorLocationModel> majorLocations) {
		this.majorLocations = majorLocations;
	}

	
}
