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
@Table(name="QuotationLoading")
public class QuotationLoading extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="QuotationLoadingId")
	private Integer quotationLoadingId;
	
	@Column(name="QuotationPerManDay")
	private Double quotationPerManDay;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DistrictId", nullable = true)
	private District district;	
	
	@ManyToMany(fetch = FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@JoinTable(name = "OutletTypeQuotationLoading", 
			joinColumns = { @JoinColumn(name = "QuotationLoadingId", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "ShortCode", nullable = false, updatable = false) })
	private Set<VwOutletTypeShortForm> outletTypes = new HashSet<VwOutletTypeShortForm>();

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getQuotationLoadingId();
	}

	public Integer getQuotationLoadingId() {
		return quotationLoadingId;
	}

	public void setQuotationLoadingId(Integer quotationLoadingId) {
		this.quotationLoadingId = quotationLoadingId;
	}

	public Double getQuotationPerManDay() {
		return quotationPerManDay;
	}

	public void setQuotationPerManDay(Double quotationPerManDay) {
		this.quotationPerManDay = quotationPerManDay;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Set<VwOutletTypeShortForm> getOutletTypes() {
		return outletTypes;
	}

	public void setOutletTypes(Set<VwOutletTypeShortForm> outletTypes) {
		this.outletTypes = outletTypes;
	}

}
