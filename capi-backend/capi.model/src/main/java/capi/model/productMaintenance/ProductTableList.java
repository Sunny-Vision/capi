package capi.model.productMaintenance;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductTableList {
	
	private Integer ProductId;
	
	private String productGroupCode;
	
	private String productGroupEnglishName;
	
	private String productGroupChineseName;
	
	private String productAttribute1;
	
	private String productAttribute2;
	
	private String productAttribute3;
	
	private String productAttribute4;
	
	private String productAttribute5;
	
	private Long noOfQuotation;
	
	private Long noOfQuotationRecord;
	
	private Long noOfQuotationNewProduct;
	
	private Long noOfPointToNote;
	
	private Long noOfProductSpec;
	
	private String remark;
	
	private String status;
	
	private Date createdDate;
	
	private String barcode;
	
	private boolean reviewed;
	
	private Integer productGroupId;
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		
		this.createdDate = createdDate;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	private String productGroupID;
	
	public Integer getProductId() {
		return ProductId;
	}

	public void setProductId(Integer productId) {
		ProductId = productId;
	}

	public String getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(String productGroupCode) {
		this.productGroupCode = productGroupCode;
	}

	public String getProductGroupEnglishName() {
		return productGroupEnglishName;
	}

	public void setProductGroupEnglishName(String productGroupEnglishName) {
		this.productGroupEnglishName = productGroupEnglishName;
	}

	public String getProductGroupChineseName() {
		return productGroupChineseName;
	}

	public void setProductGroupChineseName(String productGroupChineseName) {
		this.productGroupChineseName = productGroupChineseName;
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

	public String getProductAttribute4() {
		return productAttribute4;
	}

	public void setProductAttribute4(String productAttribute4) {
		this.productAttribute4 = productAttribute4;
	}

	public String getProductAttribute5() {
		return productAttribute5;
	}

	public void setProductAttribute5(String productAttribute5) {
		this.productAttribute5 = productAttribute5;
	}

	public Long getNoOfQuotation() {
		return noOfQuotation;
	}

	public void setNoOfQuotation(Long noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
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

	public String getProductGroupID() {
		return productGroupID;
	}

	public void setProductGroupID(String productGroupID) {
		this.productGroupID = productGroupID;
	}

	public Integer getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(Integer productGroupId) {
		this.productGroupId = productGroupId;
	}

	public String getCreatedMonth() {
		if (createdDate == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
		return sdf.format(createdDate);
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getReviewedLabel() {
		return this.reviewed ? "Y" : "N";
	}

	public Long getNoOfQuotationRecord() {
		return noOfQuotationRecord;
	}

	public void setNoOfQuotationRecord(Long noOfQuotationRecord) {
		this.noOfQuotationRecord = noOfQuotationRecord;
	}

	public Long getNoOfQuotationNewProduct() {
		return noOfQuotationNewProduct;
	}

	public void setNoOfQuotationNewProduct(Long noOfQuotationNewProduct) {
		this.noOfQuotationNewProduct = noOfQuotationNewProduct;
	}

	public Long getNoOfPointToNote() {
		return noOfPointToNote;
	}

	public void setNoOfPointToNote(Long noOfPointToNote) {
		this.noOfPointToNote = noOfPointToNote;
	}

	public Long getNoOfProductSpec() {
		return noOfProductSpec;
	}

	public void setNoOfProductSpec(Long noOfProductSpec) {
		this.noOfProductSpec = noOfProductSpec;
	}

}
