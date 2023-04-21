package capi.model.productMaintenance;

import java.util.Date;
import java.util.Map;

public class ProductEditModel {
	
	private Integer productId;
	
	private Integer productGroupId;
	
	private String productGroupCode;
	
	private String productGroupChineseName;
	
	private String productGroupEnglishName;
	
	private String countryOfOrigin;
	
	private String barcode;
	
	private  Map<Integer, ProductSpecificationEditModel> productSpecificationEditModels;
	
	private String photo1Path;
	
	private String photo2Path;
	
	private String remark;
	
	private String status;
	
	private boolean reviewed;
	
	private Date createdDateDisplay;
	
	private Date modifiedDateDisplay;

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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	

	public Map<Integer, ProductSpecificationEditModel> getProductSpecificationEditModels() {
		return productSpecificationEditModels;
	}

	public void setProductSpecificationEditModels(Map<Integer, ProductSpecificationEditModel> productSpecificationEditModels) {
		this.productSpecificationEditModels = productSpecificationEditModels;
	}

	public String getPhoto1Path() {
		return photo1Path;
	}

	public void setPhoto1Path(String photo1Path) {
		this.photo1Path = photo1Path;
	}

	public String getPhoto2Path() {
		return photo2Path;
	}

	public void setPhoto2Path(String photo2Path) {
		this.photo2Path = photo2Path;
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

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrgin) {
		this.countryOfOrigin = countryOfOrgin;
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

	public String getProductGroupText() {
		return this.productGroupCode + " - " + this.productGroupChineseName;
	}

	public Date getCreatedDateDisplay() {
		return createdDateDisplay;
	}

	public void setCreatedDateDisplay(Date createdDateDisplay) {
		this.createdDateDisplay = createdDateDisplay;
	}

	public Date getModifiedDateDisplay() {
		return modifiedDateDisplay;
	}

	public void setModifiedDateDisplay(Date modifiedDateDisplay) {
		this.modifiedDateDisplay = modifiedDateDisplay;
	}



	
	
	
}
