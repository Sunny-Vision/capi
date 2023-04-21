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
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="PriceReason")
public class PriceReason extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="PriceReasonId")
	private Integer priceReasonId;
	
	@Column(name="Description")
	private String description;
		
	@Column(name="Sequence")
	private Integer sequence;
	
	@Column(name="IsAllOutletType")
	private boolean isAllOutletType;
	
	/**
	 * 1- Price
	 * 2-Discount
	 */
	@Column(name="ReasonType")
	private Integer reasonType;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "OutletTypePriceReason", 
			joinColumns = { @JoinColumn(name = "PriceReasonId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "ShortCode", nullable = false, updatable = false) })
	private Set<VwOutletTypeShortForm> outletTypes = new HashSet<VwOutletTypeShortForm>();
	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getPriceReasonId();
	}

	public Integer getPriceReasonId() {
		return priceReasonId;
	}

	public void setPriceReasonId(Integer priceReasonId) {
		this.priceReasonId = priceReasonId;
	}


	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public boolean isAllOutletType() {
		return isAllOutletType;
	}

	public void setAllOutletType(boolean isAllOutletType) {
		this.isAllOutletType = isAllOutletType;
	}

	public Set<VwOutletTypeShortForm> getOutletTypes() {
		return outletTypes;
	}

	public void setOutletTypes(Set<VwOutletTypeShortForm> outletTypes) {
		this.outletTypes = outletTypes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getReasonType() {
		return reasonType;
	}

	public void setReasonType(Integer reasonType) {
		this.reasonType = reasonType;
	}


	
}
