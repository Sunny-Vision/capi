package capi.model.report;

import java.sql.Date;

public class ProductReviewReport {

	private Integer productId;
	
	private Integer productGroupId;
	
	private String productGroupCode;
	
	private String productGroupChineseName;
	
	private String productGroupEnglishName;
	
	private String countyOfOrigin;
	
	private String noOfQuotation;
	
	private Date productCreateDate;
	
	private String productReviewed;
	
	private Date lastModifyDate;
	
	private String lastModifyBy;
	
	private String lastModifyUser;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(Integer productGroupId) {
		this.productGroupId = productGroupId;
	}

	public String getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(String productGroupCode) {
		this.productGroupCode = productGroupCode;
	}

	public String getProductGroupChineseName() {
		return productGroupChineseName;
	}

	public void setProductGroupChineseName(String productGroupChineseName) {
		this.productGroupChineseName = productGroupChineseName;
	}

	public String getProductGroupEnglishName() {
		return productGroupEnglishName;
	}

	public void setProductGroupEnglishName(String productGroupEnglishName) {
		this.productGroupEnglishName = productGroupEnglishName;
	}

	public String getCountyOfOrigin() {
		return countyOfOrigin;
	}

	public void setCountyOfOrigin(String countyOfOrigin) {
		this.countyOfOrigin = countyOfOrigin;
	}

	public String getNoOfQuotation() {
		return noOfQuotation;
	}

	public void setNoOfQuotation(String noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
	}

	public Date getProductCreateDate() {
		return productCreateDate;
	}

	public void setProductCreateDate(Date productCreateDate) {
		this.productCreateDate = productCreateDate;
	}

	public String getProductReviewed() {
		return productReviewed;
	}

	public void setProductReviewed(String productReviewed) {
		this.productReviewed = productReviewed;
	}

	public Date getLastModifyDate() {
		return lastModifyDate;
	}

	public void setLastModifyDate(Date lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

	public String getLastModifyBy() {
		return lastModifyBy;
	}

	public void setLastModifyBy(String lastModifyBy) {
		this.lastModifyBy = lastModifyBy;
	}

	public String getLastModifyUser() {
		return lastModifyUser;
	}

	public void setLastModifyUser(String lastModifyUser) {
		this.lastModifyUser = lastModifyUser;
	}	
}
