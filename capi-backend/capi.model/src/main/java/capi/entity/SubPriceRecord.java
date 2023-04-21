package capi.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="SubPriceRecord")
public class SubPriceRecord extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SubPriceRecordId")
	private Integer subPriceRecordId;
	
	@Column(name="Sequence")
	private Integer sequence;	
	
	@Column(name="NPrice")
	private Double nPrice;
	
	@Column(name="SPrice")
	private Double sPrice;
	
	@Column(name="Discount")
	private String discount;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubPriceTypeId", nullable = true)
	private SubPriceType subPriceType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QuotationRecordId", nullable = true)
	private QuotationRecord quotationRecord;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subPriceRecord")
	private Set<SubPriceColumn> subPriceColumns = new HashSet<SubPriceColumn>();

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSubPriceRecordId();
	}

	public Integer getSubPriceRecordId() {
		return subPriceRecordId;
	}

	public void setSubPriceRecordId(Integer subPriceRecordId) {
		this.subPriceRecordId = subPriceRecordId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public SubPriceType getSubPriceType() {
		return subPriceType;
	}

	public void setSubPriceType(SubPriceType subPriceType) {
		this.subPriceType = subPriceType;
	}

	public QuotationRecord getQuotationRecord() {
		return quotationRecord;
	}

	public void setQuotationRecord(QuotationRecord quotationRecord) {
		this.quotationRecord = quotationRecord;
	}

	public Set<SubPriceColumn> getSubPriceColumns() {
		return subPriceColumns;
	}

	public void setSubPriceColumns(Set<SubPriceColumn> subPriceColumns) {
		this.subPriceColumns = subPriceColumns;
	}

	public Double getnPrice() {
		return nPrice;
	}

	public void setnPrice(Double nPrice) {
		this.nPrice = nPrice;
	}

	public Double getsPrice() {
		return sPrice;
	}

	public void setsPrice(Double sPrice) {
		this.sPrice = sPrice;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

}
