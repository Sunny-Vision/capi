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
@Table(name="QCItineraryPlanItem")
public class QCItineraryPlanItem extends EntityBase {
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="QCItineraryPlanItemId")
	private Integer qcItineraryPlanItemId;
	
	@Column(name="Remark")
	private String remark;
		
	@Column(name="Sequence")
	private Integer sequence;
	
	@Column(name="TaskName")
	private String taskName;
	
	@Column(name="Location")
	private String location;
	
	@Column(name="Session")
	private String session;
	
	/**
	 * 1- spot check
	 * 2- supervisory visit
	 * 3- pe check
	 * 4- other
	 */
	@Column(name="ItemType")
	private Integer itemType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QCItineraryPlanId", nullable = true)
	private QCItineraryPlan qcItineraryPlan;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SpotCheckFormId", nullable = true)
	private SpotCheckForm spotCheckForm;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SupervisoryVisitFormId", nullable = true)
	private SupervisoryVisitForm supervisoryVisitForm;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PECheckFormId", nullable = true)
	private PECheckForm peCheckForm;
	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getQcItineraryPlanItemId();
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


	public Integer getItemType() {
		return itemType;
	}


	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}


	public QCItineraryPlan getQcItineraryPlan() {
		return qcItineraryPlan;
	}


	public void setQcItineraryPlan(QCItineraryPlan qcItineraryPlan) {
		this.qcItineraryPlan = qcItineraryPlan;
	}


	public SpotCheckForm getSpotCheckForm() {
		return spotCheckForm;
	}


	public void setSpotCheckForm(SpotCheckForm spotCheckForm) {
		this.spotCheckForm = spotCheckForm;
	}


	public SupervisoryVisitForm getSupervisoryVisitForm() {
		return supervisoryVisitForm;
	}


	public void setSupervisoryVisitForm(SupervisoryVisitForm supervisoryVisitForm) {
		this.supervisoryVisitForm = supervisoryVisitForm;
	}


	public PECheckForm getPeCheckForm() {
		return peCheckForm;
	}


	public void setPeCheckForm(PECheckForm peCheckForm) {
		this.peCheckForm = peCheckForm;
	}


	public Integer getQcItineraryPlanItemId() {
		return qcItineraryPlanItemId;
	}


	public void setQcItineraryPlanItemId(Integer qcItineraryPlanItemId) {
		this.qcItineraryPlanItemId = qcItineraryPlanItemId;
	}

}
