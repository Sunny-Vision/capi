package capi.entity;

import java.util.Date;
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
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="PointToNote")
public class PointToNote  extends capi.entity.EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PointToNoteId")
	private Integer pointToNoteId;
	
	@Column(name="Note")
	private String note;
	
	@Column(name="EffectiveDate")
	private Date effectiveDate;
	
	@Column(name="ExpiryDate")
	private Date expiryDate;
	
	@Column(name="IsAllOutlet")
	private Boolean isAllOutlet;
	
	@Column(name="IsAllProduct")
	private Boolean isAllProduct;
	
	@Column(name="IsAllQuotation")
	private Boolean isAllQuotation;

	@Column(name="IsAllUnit")
	private Boolean isAllUnit;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "PointToNoteUnit", 
			joinColumns = { @JoinColumn(name = "PointToNoteId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "UnitId", nullable = false, updatable = false) })
	private Set<Unit> units = new HashSet<Unit>();
	
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "PointToNoteOutlet", 
			joinColumns = { @JoinColumn(name = "PointToNoteId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "OutletId", nullable = false, updatable = false) })
	private Set<Outlet> outlets = new HashSet<Outlet>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "PointToNoteProduct", 
			joinColumns = { @JoinColumn(name = "PointToNoteId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "ProductId", nullable = false, updatable = false) })
	private Set<Product> products = new HashSet<Product>();
	
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "PointToNoteQuotation", 
			joinColumns = { @JoinColumn(name = "PointToNoteId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "QuotationId", nullable = false, updatable = false) })
	private Set<Quotation> quotations = new HashSet<Quotation>();


	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPointToNoteId();
	}


	public Integer getPointToNoteId() {
		return pointToNoteId;
	}


	public void setPointToNoteId(Integer pointToNoteId) {
		this.pointToNoteId = pointToNoteId;
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
	}


	public Date getEffectiveDate() {
		return effectiveDate;
	}


	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	public Date getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}


	public Set<Unit> getUnits() {
		return units;
	}


	public void setUnits(Set<Unit> units) {
		this.units = units;
	}


	public Set<Outlet> getOutlets() {
		return outlets;
	}


	public void setOutlets(Set<Outlet> outlets) {
		this.outlets = outlets;
	}


	public Set<Product> getProducts() {
		return products;
	}


	public void setProducts(Set<Product> products) {
		this.products = products;
	}


	public Set<Quotation> getQuotations() {
		return quotations;
	}


	public void setQuotations(Set<Quotation> quotations) {
		this.quotations = quotations;
	}


	public Boolean getIsAllOutlet() {
		return isAllOutlet;
	}


	public void setIsAllOutlet(Boolean isAllOutlet) {
		this.isAllOutlet = isAllOutlet;
	}


	public Boolean getIsAllProduct() {
		return isAllProduct;
	}


	public void setIsAllProduct(Boolean isAllProduct) {
		this.isAllProduct = isAllProduct;
	}


	public Boolean getIsAllQuotation() {
		return isAllQuotation;
	}


	public void setIsAllQuotation(Boolean isAllQuotation) {
		this.isAllQuotation = isAllQuotation;
	}


	public Boolean getIsAllUnit() {
		return isAllUnit;
	}


	public void setIsAllUnit(Boolean isAllUnit) {
		this.isAllUnit = isAllUnit;
	}
}
