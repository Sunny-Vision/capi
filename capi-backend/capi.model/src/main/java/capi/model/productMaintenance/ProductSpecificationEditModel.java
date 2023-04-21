package capi.model.productMaintenance;

public class ProductSpecificationEditModel {
	
	private Integer productSpecificationId;
	
	private Integer productAttributeId;
	
	private String name;
	
	private boolean isMandatory;
	
	private Integer attributeType;

	private Integer sequence;
	
	private String option;
	
	private String value;

	public Integer getProductSpecificationId() {
		return productSpecificationId;
	}

	public void setProductSpecificationId(Integer productSpecificationId) {
		this.productSpecificationId = productSpecificationId;
	}

	public Integer getProductAttributeId() {
		return productAttributeId;
	}

	public void setProductAttributeId(Integer productAttributeId) {
		this.productAttributeId = productAttributeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	public String getIsMandatory() {
		return isMandatory ? "true" : "false";
	}
	
	public void setAttributeType(Integer attributeType) {
		this.attributeType = attributeType;
	}
	
	public Integer getAttributeType() {
		return attributeType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}
	
	
}
