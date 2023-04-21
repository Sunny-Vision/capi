package capi.model.api.dataSync;

import java.util.Date;

public class QCItineraryPlanItemSyncData {

	private Integer qcItineraryPlanItemId;
	
	private String remark;
	
	private Integer sequence;
	
	private Integer qcItineraryPlanId;
	
	private String taskName;
	
	private String location;
	
	private String session;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer spotCheckFormId;
	
	private Integer supervisoryVisitFormId;
	
	private Integer peCheckFormId;
	
	private Integer itemType;

	public Integer getQcItineraryPlanItemId() {
		return qcItineraryPlanItemId;
	}

	public void setQcItineraryPlanItemId(Integer qcItineraryPlanItemId) {
		this.qcItineraryPlanItemId = qcItineraryPlanItemId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getQcItineraryPlanId() {
		return qcItineraryPlanId;
	}

	public void setQcItineraryPlanId(Integer qcItineraryPlanId) {
		this.qcItineraryPlanId = qcItineraryPlanId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}

	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
	}

	public Integer getSupervisoryVisitFormId() {
		return supervisoryVisitFormId;
	}

	public void setSupervisoryVisitFormId(Integer supervisoryVisitFormId) {
		this.supervisoryVisitFormId = supervisoryVisitFormId;
	}

	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}

	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}

	public Integer getItemType() {
		return itemType;
	}

	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}
	
}
