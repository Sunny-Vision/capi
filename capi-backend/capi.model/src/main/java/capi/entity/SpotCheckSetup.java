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
@Table(name="SpotCheckSetup")
public class SpotCheckSetup extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SpotCheckSetupId")
	private Integer spotCheckSetupId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SpotCheckDateId", nullable = true)
	private SpotCheckDate spotCheckDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user; 
	
	@Column(name="NotificationDate")
	private Date notificationDate;
	
	/**
	 * 1- spot check date
	 * 2- previous date
	 */
	@Column(name="NotificationType")
	private Integer notificationType;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "spotCheckSetup")
	private Set<ScSvPlan> scSvplans = new HashSet<ScSvPlan>();
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSpotCheckSetupId();
	}

	public Integer getSpotCheckSetupId() {
		return spotCheckSetupId;
	}

	public void setSpotCheckSetupId(Integer spotCheckSetupId) {
		this.spotCheckSetupId = spotCheckSetupId;
	}

	public SpotCheckDate getSpotCheckDate() {
		return spotCheckDate;
	}

	public void setSpotCheckDate(SpotCheckDate spotCheckDate) {
		this.spotCheckDate = spotCheckDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Integer getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(Integer notificationType) {
		this.notificationType = notificationType;
	}

	public Set<ScSvPlan> getScSvplans() {
		return scSvplans;
	}

	public void setScSvplans(Set<ScSvPlan> scSvplans) {
		this.scSvplans = scSvplans;
	}

}
