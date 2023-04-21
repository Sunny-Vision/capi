package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="UserLockedTimeLog")
public class UserLockedTimeLog extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="UserLockedTimeLogId")
	private Integer userLockedTimeLogId;
	
	@Column(name="Username")
	private String username;

	@Column(name="LockedDate")
	private Date lockedDate;
	
	//@Column(name="LastAttemptDate")
	//private Date lastAttemptDate;
	
	
	@Override
	public Integer getId() {
		return getUserLockedTimeLogId();
	}
	
	public Integer getUserLockedTimeLogId() {
		return userLockedTimeLogId;
	}

	public void setUserLockedTimeLogId(Integer userLockedTimeLogId) {
		this.userLockedTimeLogId = userLockedTimeLogId;
	}
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Date getLockedDate() {
		return lockedDate;
	}

	public void setLockedDate(Date lockedDate) {
		this.lockedDate = lockedDate;
	}
	
	/*
	public Date getLastAttemptDate() {
		return lastAttemptDate;
	}

	public void setLastAttemptDate(Date lastAttemptDate) {
		this.lastAttemptDate = lastAttemptDate;
	}
	*/
}
