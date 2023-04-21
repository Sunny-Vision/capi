package capi.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="PECheckUnitCriteria")
public class PECheckUnitCriteria extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PECheckUnitCriteriaId")
	private Integer peCheckUnitCriteriaId;
	
	@Deprecated
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UnitId", nullable = true)
	private Unit unit;
	
	@Column(name="PRSymbol")
	private String prSymbol;
	
	@Column(name="PRValue")
	private Double prValue;
	
	@Column(name="NoOfMonth")
	private int noOfMonth;
	
	@Column(name="QuotationPercentage")
	private Double quotationPercentage;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "PECheckItemCriteria", 
			joinColumns = { @JoinColumn(name = "PECheckUnitCriteriaId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "ItemId", nullable = false, updatable = false) })
	private Set<Item> items = new HashSet<Item>();

	public Integer getPeCheckUnitCriteriaId() {
		return peCheckUnitCriteriaId;
	}

	public void setPeCheckUnitCriteriaId(Integer peCheckUnitCriteriaId) {
		this.peCheckUnitCriteriaId = peCheckUnitCriteriaId;
	}
	
	@Deprecated
	public Unit getUnit() {
		return unit;
	}
	@Deprecated
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public String getPrSymbol() {
		return prSymbol;
	}

	public void setPrSymbol(String prSymbol) {
		this.prSymbol = prSymbol;
	}

	public Double getPrValue() {
		return prValue;
	}

	public void setPrValue(Double prValue) {
		this.prValue = prValue;
	}

	public int getNoOfMonth() {
		return noOfMonth;
	}

	public void setNoOfMonth(int noOfMonth) {
		this.noOfMonth = noOfMonth;
	}

	public Double getQuotationPercentage() {
		return quotationPercentage;
	}

	public void setQuotationPercentage(Double quotationPercentage) {
		this.quotationPercentage = quotationPercentage;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPeCheckUnitCriteriaId();
	}

	public Set<Item> getItems() {
		return items;
	}

	public void setItems(Set<Item> items) {
		this.items = items;
	}
	

	
}
