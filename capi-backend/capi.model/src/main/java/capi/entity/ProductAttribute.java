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
@Table(name="ProductAttribute")
public class ProductAttribute extends EntityBase {

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ProductAttributeId")
	private Integer productAttributeId;
	
	@Column(name="Sequence")
	private Integer sequence;
	
	@Column(name="SpecificationName")
	private String specificationName;
	
	/**
	 * 1- Text
	 * 2- options
	 * 3- options with others
	 */
	@Column(name="AttributeType")
	private Integer attributeType;
	
	@Column(name="[Option]")
	private String option;
	
	@Column(name="IsMandatory")
	private boolean isMandatory;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductGroupId", nullable = true)
	private ProductGroup productGroup;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productAttribute")
	private Set<ProductSpecification> productSpecifications = new HashSet<ProductSpecification>();

	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getProductAttributeId();
	}

	public Integer getSequence() {
		return sequence;
	}


	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}


	public String getSpecificationName() {
		return specificationName;
	}


	public void setSpecificationName(String specificationName) {
		this.specificationName = specificationName;
	}


	public Integer getAttributeType() {
		return attributeType;
	}


	public void setAttributeType(Integer attributeType) {
		this.attributeType = attributeType;
	}


	public String getOption() {
		return option;
	}


	public void setOption(String option) {
		this.option = option;
	}


	public boolean getIsMandatory() {
		return isMandatory;
	}


	public void setIsMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}


	public ProductGroup getProductGroup() {
		return productGroup;
	}


	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}


	public Set<ProductSpecification> getProductSpecifications() {
		return productSpecifications;
	}


	public void setProductSpecifications(
			Set<ProductSpecification> productSpecifications) {
		this.productSpecifications = productSpecifications;
	}

	public Integer getProductAttributeId() {
		return productAttributeId;
	}

	public void setProductAttributeId(Integer productAttributeId) {
		this.productAttributeId = productAttributeId;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

}
