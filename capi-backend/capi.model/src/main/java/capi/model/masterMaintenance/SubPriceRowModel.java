package capi.model.masterMaintenance;

import java.util.List;


import capi.entity.SubPriceColumn;

public class SubPriceRowModel {

	private List<SubPriceColumn> columns;
	
	private Double nPrice;
	
	private Double sPrice;
	
	private String discount;

	public List<SubPriceColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<SubPriceColumn> columns) {
		this.columns = columns;
	}

	public Double getnPrice() {
		return nPrice;
	}

	public void setnPrice(Double nPrice) {
		this.nPrice = nPrice;
	}

	public Double getsPrice() {
		return sPrice;
	}

	public void setsPrice(Double sPrice) {
		this.sPrice = sPrice;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}
	
}
