package capi.model.shared.quotationRecord;

public class SubPriceColumnModel {
	private String columnValue;
	private Integer subPriceFieldId;
	private Integer subPriceColumnId;
	private String fieldType;
	private String variableName;
	
	public String getColumnValue() {
		return columnValue;
	}
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
	public Integer getSubPriceFieldId() {
		return subPriceFieldId;
	}
	public void setSubPriceFieldId(Integer subPriceFieldId) {
		this.subPriceFieldId = subPriceFieldId;
	}
	public Integer getSubPriceColumnId() {
		return subPriceColumnId;
	}
	public void setSubPriceColumnId(Integer subPriceColumnId) {
		this.subPriceColumnId = subPriceColumnId;
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
}
