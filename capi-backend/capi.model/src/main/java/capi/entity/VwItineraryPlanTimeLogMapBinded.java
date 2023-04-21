package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@IdClass(VwItineraryPlanTimeLogMapPK.class)
@Table(name="vwItineraryPlanTimeLogMapBinded")
public class VwItineraryPlanTimeLogMapBinded implements ViewBase {
	
	@Id
	@Column(name="TimeLogId")
	private Integer timeLogId;
	
	@Id
	@Column(name="ItineraryPlanId")
	private Integer itineraryPlanId;

	@OneToOne
	@JoinColumn(name="timeLogId", insertable=false, updatable=false)
	private TimeLog timeLog;
	
	@OneToOne
	@JoinColumn(name="ItineraryPlanId", insertable=false, updatable=false)
	private ItineraryPlan itineraryPlan;
	
	@OneToOne
	@JoinColumn(name="UserId", insertable=false, updatable=false)
	private User user;
	
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return timeLog.getId()+"-"+itineraryPlan.getId();
	}


	public ItineraryPlan getItineraryPlan() {
		return itineraryPlan;
	}


	public void setItineraryPlan(ItineraryPlan itineraryPlan) {
		this.itineraryPlan = itineraryPlan;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public TimeLog getTimeLog() {
		return timeLog;
	}


	public void setTimeLog(TimeLog timeLog) {
		this.timeLog = timeLog;
	}

}
