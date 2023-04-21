package capi.model.masterMaintenance;

public class SubPriceFieldList {
	private Integer subPriceFieldId;
	
	private String fieldName;
	
	private String fieldType;
	
	private String variableName;
	
	private int sequence;

	public Integer getSubPriceFieldId() {
		return subPriceFieldId;
	}

	public void setSubPriceFieldId(Integer subPriceFieldId) {
		this.subPriceFieldId = subPriceFieldId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

}
