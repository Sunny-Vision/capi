package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Rebasing")
public class Rebasing  extends capi.entity.EntityBase{
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="RebasingId")
	private Integer rebasingId;
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;
	
	@Column(name="CPIBasePeriod")
	private String cpiBasePeriod;
	
	@Column(name="IsEffected")
	private boolean isEffected;

	public Integer getRebasingId() {
		return rebasingId;
	}

	public void setRebasingId(Integer rebasingId) {
		this.rebasingId = rebasingId;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getRebasingId();
	}

	public String getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(String cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public boolean isEffected() {
		return isEffected;
	}

	public void setEffected(boolean isEffected) {
		this.isEffected = isEffected;
	}
}
