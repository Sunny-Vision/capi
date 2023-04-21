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
@Table(name="OutletUnitStatistic")
public class OutletUnitStatistic extends EntityBase {
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="OutletUnitStatisticId")
	private Integer outletUnitStatisticId;
	
	@Column(name="QuotationCnt")
	private Integer quotationCnt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OutletId", nullable = true)
	private Outlet outlet;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UnitId", nullable = true)
	private Unit unit;


	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getOutletUnitStatisticId();
	}


	public Integer getOutletUnitStatisticId() {
		return outletUnitStatisticId;
	}


	public void setOutletUnitStatisticId(Integer outletUnitStatisticId) {
		this.outletUnitStatisticId = outletUnitStatisticId;
	}


	public Integer getQuotationCnt() {
		return quotationCnt;
	}


	public void setQuotationCnt(Integer quotationCnt) {
		this.quotationCnt = quotationCnt;
	}


	public Outlet getOutlet() {
		return outlet;
	}


	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}


	public Unit getUnit() {
		return unit;
	}


	public void setUnit(Unit unit) {
		this.unit = unit;
	}

}
