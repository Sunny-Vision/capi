package capi.model.shared.quotationRecord;

public class PagePostModel {
	private QuotationRecordPostModel quotationRecord;
	private QuotationRecordPostModel backNoQuotationRecord;
	private ProductPostModel product;
	private boolean dirty;
	private String peCheckRemark;
	
	public QuotationRecordPostModel getQuotationRecord() {
		return quotationRecord;
	}
	public void setQuotationRecord(QuotationRecordPostModel quotationRecord) {
		this.quotationRecord = quotationRecord;
	}
	public QuotationRecordPostModel getBackNoQuotationRecord() {
		return backNoQuotationRecord;
	}
	public void setBackNoQuotationRecord(QuotationRecordPostModel backNoQuotationRecord) {
		this.backNoQuotationRecord = backNoQuotationRecord;
	}
	public ProductPostModel getProduct() {
		return product;
	}
	public void setProduct(ProductPostModel product) {
		this.product = product;
	}
	public boolean isDirty() {
		return dirty;
	}
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	public String getPeCheckRemark() {
		return peCheckRemark;
	}
	public void setPeCheckRemark(String peCheckRemark) {
		this.peCheckRemark = peCheckRemark;
	}
}
