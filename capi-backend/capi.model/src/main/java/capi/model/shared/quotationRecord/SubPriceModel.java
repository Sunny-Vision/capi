package capi.model.shared.quotationRecord;

import java.util.List;

public class SubPriceModel {
	private Double nPrice;
	private Double sPrice;
	private String discount;
	private Integer subPriceTypeId;
	private List<SubPriceColumnModel> subPriceColumns;
	private Integer subPriceRecordId;
	
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
	public Integer getSubPriceTypeId() {
		return subPriceTypeId;
	}
	public void setSubPriceTypeId(Integer subPriceTypeId) {
		this.subPriceTypeId = subPriceTypeId;
	}
	public List<SubPriceColumnModel> getSubPriceColumns() {
		return subPriceColumns;
	}
	public void setSubPriceColumns(List<SubPriceColumnModel> subPriceColumns) {
		this.subPriceColumns = subPriceColumns;
	}
	public Integer getSubPriceRecordId() {
		return subPriceRecordId;
	}
	public void setSubPriceRecordId(Integer subPriceRecordId) {
		this.subPriceRecordId = subPriceRecordId;
	}
}
