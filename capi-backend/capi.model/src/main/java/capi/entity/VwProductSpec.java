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
@Table(name="vwProductSpec")
public class VwProductSpec implements ViewBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="ProductSpecificationId")
	private Integer productSpecificationId;
	
	@Column(name="Value")
	private String value;
	
	@Column(name="IsMandatory")
	private boolean isMandatory;
	
	@Column(name="AttributeType")
	private String attributeType;
	
	@Column(name="[Option]")
	private String option;
		
	@Column(name="[Sequence]")
	private Integer sequence;
	
	@Column(name="SpecificationName")
	private String specificationName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductId", nullable = true)
	private Product product;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductAttributeId", nullable = true)
	private ProductAttribute productAttribute;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProductGroupId", nullable = true)
	private ProductGroup productGroup;

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return String.valueOf(getProductSpecificationId());
	}

	public Integer getProductSpecificationId() {
		return productSpecificationId;
	}

	public void setProductSpecificationId(Integer productSpecificationId) {
		this.productSpecificationId = productSpecificationId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public ProductAttribute getProductAttribute() {
		return productAttribute;
	}

	public void setProductAttribute(ProductAttribute productAttribute) {
		this.productAttribute = productAttribute;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

}
