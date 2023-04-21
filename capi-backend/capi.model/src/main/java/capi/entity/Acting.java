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
@Table(name="Acting")
public class Acting extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ActingId")
	private Integer actingId;
	
	@Column(name="StartDate")
	private Date startDate;
	
	@Column(name="EndDate")
	private Date endDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User staff;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ReplacementId", nullable = true)
	private User replacement;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RoleId", nullable = true)
	private Role role;
	
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getActingId();
	}


	public Integer getActingId() {
		return actingId;
	}


	public void setActingId(Integer actingId) {
		this.actingId = actingId;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public User getStaff() {
		return staff;
	}


	public void setStaff(User staff) {
		this.staff = staff;
	}


	public User getReplacement() {
		return replacement;
	}


	public void setReplacement(User replacement) {
		this.replacement = replacement;
	}


	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}

}
