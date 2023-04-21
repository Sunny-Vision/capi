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
@Table(name="SubPriceFieldMapping")
public class SubPriceFieldMapping extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SubPriceFieldMappingId")
	private Integer subPriceFieldMappingId;
	
	@Column(name="Sequence")
	private Integer sequence;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubPriceTypeId", nullable = true)
	private SubPriceType subPriceType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubPriceFieldId", nullable = true)
	private SubPriceField subPriceField;
	
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSubPriceFieldMappingId();
	}

	public Integer getSubPriceFieldMappingId() {
		return subPriceFieldMappingId;
	}

	public void setSubPriceFieldMappingId(Integer subPriceFieldMappingId) {
		this.subPriceFieldMappingId = subPriceFieldMappingId;
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

	public SubPriceField getSubPriceField() {
		return subPriceField;
	}

	public void setSubPriceField(SubPriceField subPriceField) {
		this.subPriceField = subPriceField;
	}
	
	
}
