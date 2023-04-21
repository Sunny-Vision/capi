package capi.model.commonLookup;

public class ProductLookupTableList {
	private Integer id;
	private Long numberOfQuotations;
	private String remark;
	private String status;
	private Boolean reviewed;
	private String productAttribute1;
	private String productAttribute2;
	private String productAttribute3;
	private String category;
	private String barcode;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getNumberOfQuotations() {
		return numberOfQuotations;
	}
	public void setNumberOfQuotations(Long numberOfQuotations) {
		this.numberOfQuotations = numberOfQuotations;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Boolean getReviewed() {
		return reviewed;
	}
	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}
	public String getProductAttribute1() {
		return productAttribute1;
	}
	public void setProductAttribute1(String productAttribute1) {
		this.productAttribute1 = productAttribute1;
	}
	public String getProductAttribute2() {
		return productAttribute2;
	}
	public void setProductAttribute2(String productAttribute2) {
		this.productAttribute2 = productAttribute2;
	}
	public String getProductAttribute3() {
		return productAttribute3;
	}
	public void setProductAttribute3(String productAttribute3) {
		this.productAttribute3 = productAttribute3;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
}
