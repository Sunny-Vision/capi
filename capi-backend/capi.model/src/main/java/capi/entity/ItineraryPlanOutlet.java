package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ItineraryPlanOutlet")
public class ItineraryPlanOutlet extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ItineraryPlanOutletId")
	private Integer itineraryPlanOutletId;
	
	@Column(name="Sequence")
	private Integer sequence;
	
	@Column(name="MajorLocationSequence")
	private Integer majorLocationSequence;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OutletId", nullable = true)
	private Outlet outlet;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ItineraryPlanId", nullable = true)
	private ItineraryPlan itineraryPlan;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MajorLocationId", nullable = true)
	private MajorLocation majorLocation;
	
	// for imported assignment
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignmentId", nullable = true)
	private Assignment assignment;
	
	/**
	 * 1 - outlet
	 * 2 - imported assignment
	 */
	@Column(name="PlanType")
	private Integer planType;
	
	@Column(name="FirmCode")
	private String firmCode;
	
	@Column(name="ReferenceNo")
	private String referenceNo;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getItineraryPlanOutletId();
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getMajorLocationSequence() {
		return majorLocationSequence;
	}

	public void setMajorLocationSequence(Integer majorLocationSequence) {
		this.majorLocationSequence = majorLocationSequence;
	}

	public ItineraryPlan getItineraryPlan() {
		return itineraryPlan;
	}

	public void setItineraryPlan(ItineraryPlan itineraryPlan) {
		this.itineraryPlan = itineraryPlan;
	}

	public MajorLocation getMajorLocation() {
		return majorLocation;
	}

	public void setMajorLocation(MajorLocation majorLocation) {
		this.majorLocation = majorLocation;
	}

	public Integer getItineraryPlanOutletId() {
		return itineraryPlanOutletId;
	}

	public void setItineraryPlanOutletId(Integer itineraryPlanOutletId) {
		this.itineraryPlanOutletId = itineraryPlanOutletId;
	}

	public Outlet getOutlet() {
		return outlet;
	}

	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public Integer getPlanType() {
		return planType;
	}

	public void setPlanType(Integer planType) {
		this.planType = planType;
	}

	public String getFirmCode() {
		return firmCode;
	}

	public void setFirmCode(String firmCode) {
		this.firmCode = firmCode;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	
	
}
