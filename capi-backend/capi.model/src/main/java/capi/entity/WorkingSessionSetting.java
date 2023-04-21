package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="WorkingSessionSetting")
public class WorkingSessionSetting extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="WorkingSessionSettingId")
	private Integer workingSessionSettingId;
	
	@Temporal(TemporalType.TIME)
	@Column(name="FromTime")
	private Date fromTime;
	
	@Temporal(TemporalType.TIME)
	@Column(name="ToTime")
	private Date toTime;

	public Integer getWorkingSessionSettingId() {
		return workingSessionSettingId;
	}

	public void setWorkingSessionSettingId(Integer workingSessionSettingId) {
		this.workingSessionSettingId = workingSessionSettingId;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getWorkingSessionSettingId();
	}
	
}
