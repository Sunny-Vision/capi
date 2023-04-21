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
@Table(name="SpotCheckPhoneCall")
public class SpotCheckPhoneCall extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SpotCheckPhoneCallId")
	private Integer spotCheckPhoneCallId;
	
	@Temporal(TemporalType.TIME)
	@Column(name="PhoneCallTime")
	private Date phoneCallTime;
	
	/**
	 * 1- Contacted
	 * 2- Voice mail
	 * 3- Others
	 */
	@Column(name="Result")
	private Integer result;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SpotCheckFormId", nullable = true)
	private SpotCheckForm spotCheckForm;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSpotCheckPhoneCallId();
	}

	public Integer getSpotCheckPhoneCallId() {
		return spotCheckPhoneCallId;
	}

	public void setSpotCheckPhoneCallId(Integer spotCheckPhoneCallId) {
		this.spotCheckPhoneCallId = spotCheckPhoneCallId;
	}

	public Date getPhoneCallTime() {
		return phoneCallTime;
	}

	public void setPhoneCallTime(Date phoneCallTime) {
		this.phoneCallTime = phoneCallTime;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public SpotCheckForm getSpotCheckForm() {
		return spotCheckForm;
	}

	public void setSpotCheckForm(SpotCheckForm spotCheckForm) {
		this.spotCheckForm = spotCheckForm;
	}

}
