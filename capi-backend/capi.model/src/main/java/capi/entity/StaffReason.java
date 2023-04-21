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

@Entity
@Table(name="StaffReason")
public class StaffReason extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="StaffReasonId")
	private Integer staffReasonId;
	
	@Column(name="FromDate")
	private Date fromDate;
	
	@Column(name="ToDate")
	private Date toDate;
	
	@Column(name="Reason")
	private String reason;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getStaffReasonId();
	}

	public Integer getStaffReasonId() {
		return staffReasonId;
	}

	public void setStaffReasonId(Integer staffReasonId) {
		this.staffReasonId = staffReasonId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
