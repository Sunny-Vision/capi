package capi.model.report;

import java.util.ArrayList;
import java.util.Map;

public class IndividualQuotationRecordReport3 {
	/*private Integer quotationReocrdId;
	private Integer subPriceRecordId;*/
	private String quotationReocrdId;
	private String subPriceRecordId;
	private String	name;
	private Double	nPrice;
	private Double	sPrice;
	private Double	subNprice;
	private Double	subSprice;
	private String	discount;
	private Integer sequence;
	private String	fieldName;
	private String	columnValue;
	
	private Integer	Sequence;
	private ArrayList<String> subPriceField;	
	
	public String getQuotationReocrdId() {
		return quotationReocrdId;
	}
	public String getSubPriceRecordId() {
		return subPriceRecordId;
	}
	public String getName() {
		return name;
	}
	public Double getnPrice() {
		return nPrice;
	}
	public Double getsPrice() {
		return sPrice;
	}
	public Double getSubNprice() {
		return subNprice;
	}
	public Double getSubSprice() {
		return subSprice;
	}
	public String getDiscount() {
		return discount;
	}
	public Integer getSequence() {
		return sequence;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getColumnValue() {
		return columnValue;
	}
	public ArrayList<String> getSubPriceField() {
		return subPriceField;
	}
	public void setQuotationReocrdId(String quotationReocrdId) {
		this.quotationReocrdId = quotationReocrdId;
	}
	public void setSubPriceRecordId(String subPriceRecordId) {
		this.subPriceRecordId = subPriceRecordId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setnPrice(Double nPrice) {
		this.nPrice = nPrice;
	}
	public void setsPrice(Double sPrice) {
		this.sPrice = sPrice;
	}
	public void setSubNprice(Double subNprice) {
		this.subNprice = subNprice;
	}
	public void setSubSprice(Double subSprice) {
		this.subSprice = subSprice;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
	public void setSubPriceField(ArrayList<String> subPriceField) {
		this.subPriceField = subPriceField;
	}

}
