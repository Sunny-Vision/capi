package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="vwEntityRemoveLog")
public class VwEntityRemoveLog implements ViewBase{
	
	@Id
	@Column(name="RowNo")
	private Integer rowNum;

	@Column(name="EntityName")
	private String entityName;
	
	@Column(name="EntityId")
	private Integer entityId;
	
	@Column(name="CreatedDate")
	private Date createdDate;
	
	@Column(name="Action")
	private String action;
	
	@Column(name="ColumnName")
	private String columnName;
	
	@Column(name="ColumnValue")
	private String columnValue;
	
	@Column(name="Count")
	private Integer count;
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return String.valueOf(getRowNum());
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

}
