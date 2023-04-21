package capi.model.api.dataSync;

import java.util.Date;

public class MajorLocationSyncData {

	private Integer majorLocationId;
	
	private String remark;
	
	private Integer sequence;
	
	private Integer itineraryPlanId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String taskName;
	
	private String location;
	
	private String session;
	
	private boolean isFreeEntryTask;
	
	private String district;
	
	private String tpu;
	
	private String street;
	
	private boolean isNewRecruitmentTask;
	
	private String marketName;
	
	private String address;

	public Integer getMajorLocationId() {
		return majorLocationId;
	}

	public void setMajorLocationId(Integer majorLocationId) {
		this.majorLocationId = majorLocationId;
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

	public boolean isFreeEntryTask() {
		return isFreeEntryTask;
	}

	public void setFreeEntryTask(boolean isFreeEntryTask) {
		this.isFreeEntryTask = isFreeEntryTask;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTpu() {
		return tpu;
	}

	public void setTpu(String tpu) {
		this.tpu = tpu;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public boolean isNewRecruitmentTask() {
		return isNewRecruitmentTask;
	}

	public void setNewRecruitmentTask(boolean isNewRecruitmentTask) {
		this.isNewRecruitmentTask = isNewRecruitmentTask;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
