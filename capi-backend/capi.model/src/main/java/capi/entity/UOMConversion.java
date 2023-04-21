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
@Table(name="UOMConversion")
public class UOMConversion extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="UOMConversionId")
	private Integer uomConversionId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BaseUOMId", nullable = true)
	private Uom baseUOM;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TargetUOMId", nullable = true)
	private Uom targetUOM;
	
	@Column(name="Factor")
	private Double factor;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getUomConversionId();
	}

	public Integer getUomConversionId() {
		return uomConversionId;
	}

	public void setUomConversionId(Integer uomConversionId) {
		this.uomConversionId = uomConversionId;
	}

	public Uom getBaseUOM() {
		return baseUOM;
	}

	public void setBaseUOM(Uom baseUOM) {
		this.baseUOM = baseUOM;
	}

	public Uom getTargetUOM() {
		return targetUOM;
	}

	public void setTargetUOM(Uom targetUOM) {
		this.targetUOM = targetUOM;
	}

	public Double getFactor() {
		return factor;
	}

	public void setFactor(Double factor) {
		this.factor = factor;
	}
	

}
