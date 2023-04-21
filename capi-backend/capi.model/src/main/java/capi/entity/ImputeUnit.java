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
@Table(name="ImputeUnit")
public class ImputeUnit  extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ImputeUnitId")
	private Integer imputeUnitId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="UnitId", nullable=true)
	private Unit unit;
	
	@Column(name="Price")
	private Double price;
	
	@Column(name="Remark")
	private String remark;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getImputeUnitId();
	}

	public Integer getImputeUnitId() {
		return imputeUnitId;
	}

	public void setImputeUnitId(Integer imputeUnitId) {
		this.imputeUnitId = imputeUnitId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
