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
@Table(name="RebasingUnitMapping")
public class RebasingUnitMapping extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="RebasingUnitMappingId")
	private Integer rebasingUnitMappingId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OldUnitId", nullable = true)
	private Unit oldUnit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NewUnitId", nullable = true)
	private Unit newUnit;
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;

	@Column(name="NewCPIBasePeriod")
	private String newCPIBasePeriod;
	
	@Column(name="OldCPIBasePeriod")
	private String oldCPIBasePeriod;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getRebasingUnitMappingId() {
		return rebasingUnitMappingId;
	}

	public void setRebasingUnitMappingId(Integer rebasingUnitMappingId) {
		this.rebasingUnitMappingId = rebasingUnitMappingId;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getNewCPIBasePeriod() {
		return newCPIBasePeriod;
	}

	public void setNewCPIBasePeriod(String newCPIBasePeriod) {
		this.newCPIBasePeriod = newCPIBasePeriod;
	}

	public String getOldCPIBasePeriod() {
		return oldCPIBasePeriod;
	}

	public void setOldCPIBasePeriod(String oldCPIBasePeriod) {
		this.oldCPIBasePeriod = oldCPIBasePeriod;
	}

	public Unit getOldUnit() {
		return oldUnit;
	}

	public void setOldUnit(Unit oldUnit) {
		this.oldUnit = oldUnit;
	}

	public Unit getNewUnit() {
		return newUnit;
	}

	public void setNewUnit(Unit newUnit) {
		this.newUnit = newUnit;
	}

	
}
