package capi.model.api.dataSync;

import java.util.Date;

public class SubPriceFieldSyncData {
	private Integer subPriceFieldId;
	
	private String fieldName;
	
	private String fieldType;
	
	private String variableName;
	
	private Date createdDate;
	
	private Date modifiedDate;

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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
}
