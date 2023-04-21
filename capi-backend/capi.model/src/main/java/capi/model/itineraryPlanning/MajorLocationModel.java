package capi.model.itineraryPlanning;

import java.util.List;

import javax.persistence.Id;

public class MajorLocationModel implements Comparable<MajorLocationModel> {

	@Id
	private Integer majorLocationId;
	
	private String remark;
	private Integer sequence;
	private String taskName;
	private String location;
	
	/**
	 * A,P,E
	 */
	private String session;
	private Boolean isFreeEntryTask;
	private Boolean isNewRecruitmentTask;
	private String district;
	private String tpu;
	private String street;
	
	private String marketName;	
	private String address;
	
	private List<ItineraryPlanOutletModel> itineraryPlanOutletModels;
	
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
	public Boolean getIsFreeEntryTask() {
		return isFreeEntryTask;
	}
	public void setIsFreeEntryTask(Boolean isFreeEntryTask) {
		this.isFreeEntryTask = isFreeEntryTask;
	}
	public Boolean getIsNewRecruitmentTask() {
		return isNewRecruitmentTask;
	}
	public void setIsNewRecruitmentTask(Boolean isNewRecruitmentTask) {
		this.isNewRecruitmentTask = isNewRecruitmentTask;
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
	public List<ItineraryPlanOutletModel> getItineraryPlanOutletModels() {
		return itineraryPlanOutletModels;
	}
	public void setItineraryPlanOutletModels(List<ItineraryPlanOutletModel> itineraryPlanOutletModels) {
		this.itineraryPlanOutletModels = itineraryPlanOutletModels;
	}

	@Override
    public int compareTo(MajorLocationModel another) {
        return ( this.getSequence()).compareTo(another.getSequence());

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
