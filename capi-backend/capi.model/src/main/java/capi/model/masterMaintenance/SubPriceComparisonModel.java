package capi.model.masterMaintenance;

import java.util.List;

import capi.entity.SubPriceType;

public class SubPriceComparisonModel {
	
	private String date1;
	
	private String date2;
	
	private SubPriceType type1;

	private List<SubPriceFieldList> fieldList1;
	
	private List<SubPriceRowModel> rows1;
	
	private List<SubPriceFieldList> fieldList2;
	
	private List<SubPriceRowModel> rows2;
	
	private SubPriceType type2;

	public List<SubPriceFieldList> getFieldList1() {
		return fieldList1;
	}

	public void setFieldList1(List<SubPriceFieldList> fieldList1) {
		this.fieldList1 = fieldList1;
	}

	public List<SubPriceFieldList> getFieldList2() {
		return fieldList2;
	}

	public void setFieldList2(List<SubPriceFieldList> fieldList2) {
		this.fieldList2 = fieldList2;
	}



	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	public SubPriceType getType1() {
		return type1;
	}

	public void setType1(SubPriceType type1) {
		this.type1 = type1;
	}

	public SubPriceType getType2() {
		return type2;
	}

	public void setType2(SubPriceType type2) {
		this.type2 = type2;
	}

	public List<SubPriceRowModel> getRows1() {
		return rows1;
	}

	public void setRows1(List<SubPriceRowModel> rows1) {
		this.rows1 = rows1;
	}

	public List<SubPriceRowModel> getRows2() {
		return rows2;
	}

	public void setRows2(List<SubPriceRowModel> rows2) {
		this.rows2 = rows2;
	}
	
}
