package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="OutletTypeMapping")
public class OutletTypeMapping  extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="OutletTypeMappingId")
	private Integer outletTypeMappingId;
	
	@Column(name="OldCode")
	private String oldCode;
	
	@Column(name="ShortCode")
	private String shortCode;
	
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;


	public Integer getOutletTypeMappingId() {
		return outletTypeMappingId;
	}


	public void setOutletTypeMappingId(Integer outletTypeMappingId) {
		this.outletTypeMappingId = outletTypeMappingId;
	}


	public String getOldCode() {
		return oldCode;
	}


	public void setOldCode(String oldCode) {
		this.oldCode = oldCode;
	}


	public String getShortCode() {
		return shortCode;
	}


	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
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
		return getOutletTypeMappingId();
	}
	
}
