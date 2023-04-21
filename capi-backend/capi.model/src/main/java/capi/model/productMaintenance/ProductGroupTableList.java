package capi.model.productMaintenance;

public class ProductGroupTableList {
	
	private Integer ProductGroupId;
	
	private String code;
	
	private String englishName;
	
	private String chineseName;
	
	private Long noOfProduct;
	
	private Long noOfUnit;
	
	private Long noOfAttribute;
	
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

	public Long getNoOfAttribute() {
		return noOfAttribute;
	}

	public void setNoOfAttribute(Long noOfAttribute) {
		this.noOfAttribute = noOfAttribute;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	


}
