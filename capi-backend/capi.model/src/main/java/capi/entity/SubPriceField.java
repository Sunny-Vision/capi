package capi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="SubPriceField")
public class SubPriceField extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SubPriceFieldId")
	private Integer subPriceFieldId;
	
	@Column(name="FieldName")
	private String fieldName;
	
	/**
	 * Text, Date, Number, Time, Checkbox
	 */
	@Column(name="FieldType")
	private String fieldType;
	
	@Column(name="VariableName")
	private String variableName;
	
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSubPriceFieldId();
	}


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

}
