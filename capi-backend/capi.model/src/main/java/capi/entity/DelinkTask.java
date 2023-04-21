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
@Table(name="DelinkTask")
public class DelinkTask extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="DelinkTaskId")
	private Integer delinkTaskId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@Column(name="IsRun")
	private boolean isRun;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getDelinkTaskId();
	}

	public Integer getDelinkTaskId() {
		return delinkTaskId;
	}

	public void setDelinkTaskId(Integer delinkTaskId) {
		this.delinkTaskId = delinkTaskId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
