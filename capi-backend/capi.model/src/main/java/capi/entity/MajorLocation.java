package capi.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="MajorLocation")
public class MajorLocation extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="MajorLocationId")
	private Integer majorLocationId;
	
	@Column(name="Remark")
	private String remark;
	
	@Column(name="Sequence")
	private Integer sequence;
	
	@Column(name="TaskName")
	private String taskName;
	
	@Column(name="Location")
	private String location;
	
	/**
	 * A,P,E
	 */
	@Column(name="Session")
	private String session;
	
	@Column(name="IsFreeEntryTask")
	private Boolean isFreeEntryTask;
	
	@Column(name="District")
	private String district;
	
	@Column(name="Tpu")
	private String tpu;
	
	@Column(name="Street")
	private String street;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ItineraryPlanId", nullable = true)
	private ItineraryPlan itineraryPlan;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "majorLocation")
	private Set<ItineraryPlanOutlet> outlets = new HashSet<ItineraryPlanOutlet>();
	
	@Column(name="IsNewRecruitmentTask")
	private Boolean isNewRecruitmentTask;
	
	// for editing itinerary plan
	@Column(name="MarketName")
	private String marketName;
	
	// for editing itinerary plan
	@Column(name="Address")
	private String address;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getMajorLocationId();
	}

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

	public ItineraryPlan getItineraryPlan() {
		return itineraryPlan;
	}

	public void setItineraryPlan(ItineraryPlan itineraryPlan) {
		this.itineraryPlan = itineraryPlan;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Set<ItineraryPlanOutlet> getOutlets() {
		return outlets;
	}

	public void setOutlets(Set<ItineraryPlanOutlet> outlets) {
		this.outlets = outlets;
	}

	public Boolean getIsFreeEntryTask() {
		return isFreeEntryTask;
	}

	public void setIsFreeEntryTask(Boolean isFreeEntryTask) {
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

	public Boolean getIsNewRecruitmentTask() {
		return isNewRecruitmentTask;
	}

	public void setIsNewRecruitmentTask(Boolean isNewRecruitmentTask) {
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
