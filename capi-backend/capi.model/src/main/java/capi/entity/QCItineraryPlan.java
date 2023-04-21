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
import javax.persistence.Version;

@Entity
@Table(name="QCItineraryPlan")
public class QCItineraryPlan extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="QCItineraryPlanId")
	private Integer qcItineraryPlanId;
	
	@Column(name="[Date]")
	private Date date;
	
	@Column(name="Session")
	private String session;
	
	@Column(name="RejectReason")
	private String rejectReason;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "qcItineraryPlan")
	private Set<QCItineraryPlanItem> qcItineraryPlanItems = new HashSet<QCItineraryPlanItem>();
	
	/**
	 * Draft, Submitted, Approved, Rejected
	 */
	@Column(name="Status")
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubmitTo", nullable = true)
	private User submitTo;
	
	@Version
	@Column(name="Version")
	private Integer version;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getQcItineraryPlanId();
	}


	public Integer getQcItineraryPlanId() {
		return qcItineraryPlanId;
	}


	public void setQcItineraryPlanId(Integer qcItineraryPlanId) {
		this.qcItineraryPlanId = qcItineraryPlanId;
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


	public String getRejectReason() {
		return rejectReason;
	}


	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}


	public Set<QCItineraryPlanItem> getQcItineraryPlanItems() {
		return qcItineraryPlanItems;
	}


	public void setQcItineraryPlanItems(
			Set<QCItineraryPlanItem> qcItineraryPlanItems) {
		this.qcItineraryPlanItems = qcItineraryPlanItems;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public User getSubmitTo() {
		return submitTo;
	}


	public void setSubmitTo(User submitTo) {
		this.submitTo = submitTo;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}

}
