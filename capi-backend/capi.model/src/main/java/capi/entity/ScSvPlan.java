package capi.entity;

import java.util.Date;
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
@Table(name="ScSvPlan")
public class ScSvPlan extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ScSvPlanId")
	private Integer scSvPlanId;
	
	@Column(name="VisitDate")
	private Date visitDate;

	/**
	 * 1- Spot Check
	 * 2- Supervisory Visit
	 */
	@Column(name="QCType")
	private Integer qcType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OwnerId", nullable = true)
	private User owner;
	
	@Column(name="IsMandatoryPlan")
	private boolean isMandatoryPlan;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SpotCheckSetupId", nullable = true)
	private SpotCheckSetup spotCheckSetup;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scSvPlan")
	private Set<SpotCheckForm> spotCheckForms = new HashSet<SpotCheckForm>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scSvPlan")
	private Set<SupervisoryVisitForm> supervisoryVisitForms = new HashSet<SupervisoryVisitForm>();
	
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getScSvPlanId();
	}

	public Integer getScSvPlanId() {
		return scSvPlanId;
	}

	public void setScSvPlanId(Integer scSvPlanId) {
		this.scSvPlanId = scSvPlanId;
	}

	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	public Integer getQcType() {
		return qcType;
	}

	public void setQcType(Integer qcType) {
		this.qcType = qcType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public boolean isMandatoryPlan() {
		return isMandatoryPlan;
	}

	public void setMandatoryPlan(boolean isMandatoryPlan) {
		this.isMandatoryPlan = isMandatoryPlan;
	}

	public SpotCheckSetup getSpotCheckSetup() {
		return spotCheckSetup;
	}

	public void setSpotCheckSetup(SpotCheckSetup spotCheckSetup) {
		this.spotCheckSetup = spotCheckSetup;
	}

	public Set<SpotCheckForm> getSpotCheckForms() {
		return spotCheckForms;
	}

	public void setSpotCheckForms(Set<SpotCheckForm> spotCheckForms) {
		this.spotCheckForms = spotCheckForms;
	}

	public Set<SupervisoryVisitForm> getSupervisoryVisitForms() {
		return supervisoryVisitForms;
	}

	public void setSupervisoryVisitForms(
			Set<SupervisoryVisitForm> supervisoryVisitForms) {
		this.supervisoryVisitForms = supervisoryVisitForms;
	}

}
