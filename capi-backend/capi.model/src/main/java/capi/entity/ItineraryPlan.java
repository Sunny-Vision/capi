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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="ItineraryPlan")
public class ItineraryPlan  extends capi.entity.EntityBase implements OptimisticLockEntity{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ItineraryPlanId")
	private Integer itineraryPlanId;
	
	@Column(name="[Date]")
	private  Date date;
	
	/**
	 * AP, PE
	 */
	@Column(name="Session")
	private  String session;
	
	@Column(name="Status")
	private  String status;
	
	@Column(name="RejectReason")
	private  String rejectReason;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SupervisorId", nullable = true)
	private User supervisor;	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itineraryPlan")
	private Set<ItineraryPlanOutlet> outlets = new HashSet<ItineraryPlanOutlet>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itineraryPlan")
	private Set<MajorLocation> majorLocations = new HashSet<MajorLocation>();
		
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "ItineraryPlanAssignment", 
			joinColumns = { @JoinColumn(name = "ItineraryPlanId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "AssignmentId", nullable = false, updatable = false) })
	private Set<Assignment> assignments = new HashSet<Assignment>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "ItineraryUnPlanAssignment", 
			joinColumns = { @JoinColumn(name = "ItineraryPlanId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "AssignmentId", nullable = false, updatable = false) })
	private Set<Assignment> unPlanAssignments = new HashSet<Assignment>();
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy="itineraryPlan")
	private VwItineraryPlanTimeLogMapBinded timeLogMap;
	
	@Version
	@Column(name="Version")
	private Integer version;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getItineraryPlanId();
	}


	public Integer getItineraryPlanId() {
		return itineraryPlanId;
	}


	public void setItineraryPlanId(Integer itineraryPlanId) {
		this.itineraryPlanId = itineraryPlanId;
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


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public User getSupervisor() {
		return supervisor;
	}


	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}


	public Set<MajorLocation> getMajorLocations() {
		return majorLocations;
	}


	public void setMajorLocations(Set<MajorLocation> majorLocations) {
		this.majorLocations = majorLocations;
	}


	public Set<ItineraryPlanOutlet> getOutlets() {
		return outlets;
	}


	public void setOutlets(Set<ItineraryPlanOutlet> outlets) {
		this.outlets = outlets;
	}


	public Set<Assignment> getAssignments() {
		return assignments;
	}


	public void setAssignments(Set<Assignment> assignments) {
		this.assignments = assignments;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}


	public Set<Assignment> getUnPlanAssignments() {
		return unPlanAssignments;
	}


	public void setUnPlanAssignments(Set<Assignment> unPlanAssignments) {
		this.unPlanAssignments = unPlanAssignments;
	}


	public VwItineraryPlanTimeLogMapBinded getTimeLogMap() {
		return timeLogMap;
	}


	public void setTimeLogMap(VwItineraryPlanTimeLogMapBinded timeLogMap) {
		this.timeLogMap = timeLogMap;
	}


}
