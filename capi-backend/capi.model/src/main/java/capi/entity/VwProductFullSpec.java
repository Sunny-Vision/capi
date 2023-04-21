package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="VwProductFullSpec")
public class VwProductFullSpec  implements ViewBase{

	@Id
	@Column(name="SpecId")
	private String specId;
	
	@Column(name="ProductSpecificationId")
	private Integer productSpecificationId;
	
	@Column(name="SpecificationName")
	private String specificationName;
	
	@Column(name="[Sequence]")
	private Integer sequence;
	
	@Column(name="IsMandatory")
	private Boolean isMandatory;
	
	@Column(name="[Option]")
	private String option;
	
	@Column(name="AttributeType")
	private Integer attributeType;
	
	@Column(name="Value")
	private String value;
	
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
		return getSpecId();
	}


	public Integer getProductSpecificationId() {
		return productSpecificationId;
	}


	public void setProductSpecificationId(Integer productSpecificationId) {
		this.productSpecificationId = productSpecificationId;
	}


	public String getSpecificationName() {
		return specificationName;
	}


	public void setSpecificationName(String specificationName) {
		this.specificationName = specificationName;
	}


	public Integer getSequence() {
		return sequence;
	}


	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}


	public Boolean getIsMandatory() {
		return isMandatory;
	}


	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}


	public String getOption() {
		return option;
	}


	public void setOption(String option) {
		this.option = option;
	}


	public Integer getAttributeType() {
		return attributeType;
	}


	public void setAttributeType(Integer attributeType) {
		this.attributeType = attributeType;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getSpecId() {
		return specId;
	}


	public void setSpecId(String specId) {
		this.specId = specId;
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
