package capi.model.productMaintenance;

import java.util.Date;
import java.util.Map;
import capi.entity.ProductAttribute;

public class ProductGroupEditModel {
	
	private Integer ProductGroupId;
	
	private String code;
	
	private String englishName;
	
	private String chineseName;
	
	private Long noOfProduct;
	
	private Long noOfUnit;
	
	private Map<Integer, ProductAttribute> productAttributes;
	
	private Date createdDateDisplay;
	
	private Date modifiedDateDisplay;
	
	private String status;
	
	public Integer getProductGroupId() {
		return ProductGroupId;
	}

	public void setProductGroupId(Integer productGroupId) {
		ProductGroupId = productGroupId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public Map<Integer, ProductAttribute> getProductAttributes() {
		return productAttributes;
	}

	public void setProductAttributes(Map<Integer, ProductAttribute> productAttributes) {
		this.productAttributes = productAttributes;
	}

	public Long getNoOfProduct() {
		return noOfProduct;
	}

	public void setNoOfProduct(Long noOfProduct) {
		this.noOfProduct = noOfProduct;
	}

	public Long getNoOfUnit() {
		return noOfUnit;
	}

	public void setNoOfUnit(Long noOfUnit) {
		this.noOfUnit = noOfUnit;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	
}
