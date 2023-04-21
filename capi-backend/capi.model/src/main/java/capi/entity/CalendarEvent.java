package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="CalendarEvent")
public class CalendarEvent extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="CalendarEventId")
	private Integer calendarEventId;
		
	/**
	 * 1 - OT, 
	 * 2 - TimeOff, 
	 * 3 - Others
	 */
	@Column(name="ActivityType")
	private Integer activityType;

	@Column(name="EventDate")
	private Date eventDate;
	
	@Column(name="ManDay")
	private Double manDay;
	
	@Column(name="Session")
	private String session;
	
	@Temporal(TemporalType.TIME)
	@Column(name="StartTime")
	private Date startTime;
	
	@Temporal(TemporalType.TIME)
	@Column(name="EndTime")
	private Date endTime;
	
	@Temporal(TemporalType.TIME)
	@Column(name="Duration")
	private Date duration;
	
	@Column(name="Remark")
	private String remark;
	
	@Column(name="IsPublicHoliday")
	private boolean isPublicHoliday;
	
	@Column(name="OtherActivity")
	private String otherActivity;
	
	@Column(name="PublicUid")
	private String publicUid;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ActivityCodeId", nullable = true)
	private ActivityCode activityCode;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getCalendarEventId();
	}

	public Integer getCalendarEventId() {
		return calendarEventId;
	}

	public void setCalendarEventId(Integer calendarEventId) {
		this.calendarEventId = calendarEventId;
	}

	public Integer getActivityType() {
		return activityType;
	}

	public void setActivityType(Integer activityType) {
		this.activityType = activityType;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Double getManDay() {
		return manDay;
	}

	public void setManDay(Double manDay) {
		this.manDay = manDay;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getDuration() {
		return duration;
	}

	public void setDuration(Date duration) {
		this.duration = duration;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isPublicHoliday() {
		return isPublicHoliday;
	}

	public void setPublicHoliday(boolean isPublicHoliday) {
		this.isPublicHoliday = isPublicHoliday;
	}

	public String getOtherActivity() {
		return otherActivity;
	}

	public void setOtherActivity(String otherActivity) {
		this.otherActivity = otherActivity;
	}

	public String getPublicUid() {
		return publicUid;
	}

	public void setPublicUid(String publicUid) {
		this.publicUid = publicUid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ActivityCode getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(ActivityCode activityCode) {
		this.activityCode = activityCode;
	}

}
