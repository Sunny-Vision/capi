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
@Table(name="SubPriceColumn")
public class SubPriceColumn extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="SubPriceColumnId")
	private Integer subPriceColumnId;
	
	@Column(name="ColumnValue")
	private String columnValue;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubPriceFieldMappingId", nullable = true)
	private SubPriceFieldMapping subPriceFieldMapping;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SubPriceRecordId", nullable = true)
	private SubPriceRecord subPriceRecord;
	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getSubPriceColumnId();
	}


	public Integer getSubPriceColumnId() {
		return subPriceColumnId;
	}


	public void setSubPriceColumnId(Integer subPriceColumnId) {
		this.subPriceColumnId = subPriceColumnId;
	}


	public String getColumnValue() {
		return columnValue;
	}


	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}


	public SubPriceFieldMapping getSubPriceFieldMapping() {
		return subPriceFieldMapping;
	}


	public void setSubPriceFieldMapping(SubPriceFieldMapping subPriceFieldMapping) {
		this.subPriceFieldMapping = subPriceFieldMapping;
	}


	public SubPriceRecord getSubPriceRecord() {
		return subPriceRecord;
	}


	public void setSubPriceRecord(SubPriceRecord subPriceRecord) {
		this.subPriceRecord = subPriceRecord;
	}



}
