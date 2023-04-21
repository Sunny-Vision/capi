package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PEExcludedOutletType")
public class PEExcludedOutletType extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PEExcludedOutletTypeId")
	private Integer peExcludedOutletTypeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ShortCode", nullable = true)
	private VwOutletTypeShortForm outletType;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPeExcludedOutletTypeId();
	}

	public Integer getPeExcludedOutletTypeId() {
		return peExcludedOutletTypeId;
	}

	public void setPeExcludedOutletTypeId(Integer peExcludedOutletTypeId) {
		this.peExcludedOutletTypeId = peExcludedOutletTypeId;
	}

	public VwOutletTypeShortForm getOutletType() {
		return outletType;
	}

	public void setOutletType(VwOutletTypeShortForm outletType) {
		this.outletType = outletType;
	}


}
