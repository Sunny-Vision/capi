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
@Table(name="SpotCheckStatistic")
public class SpotCheckStatistic  extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SpotCheckStatisticId")
	private Integer spotCheckStatisticId;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UserId", nullable = true)
	private User user;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@Column(name="SpotCheckCount")
	private Integer spotCheckCount;
	
	@Column(name="GHSSpotCheckCount")
	private Integer ghsSpotCheckCount;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSpotCheckStatisticId();
	}

	public Integer getSpotCheckStatisticId() {
		return spotCheckStatisticId;
	}

	public void setSpotCheckStatisticId(Integer spotCheckStatisticId) {
		this.spotCheckStatisticId = spotCheckStatisticId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Integer getSpotCheckCount() {
		return spotCheckCount;
	}

	public void setSpotCheckCount(Integer spotCheckCount) {
		this.spotCheckCount = spotCheckCount;
	}

	public Integer getGhsSpotCheckCount() {
		return ghsSpotCheckCount;
	}

	public void setGhsSpotCheckCount(Integer ghsSpotCheckCount) {
		this.ghsSpotCheckCount = ghsSpotCheckCount;
	}

}
