package capi.model.commonLookup;

import java.util.List;

import capi.model.productMaintenance.ProductSpecificationEditModel;

public class ChangeProductViewModel {
	private Integer productId;
	private String countryOfOrigin;
	private List<ProductSpecificationEditModel> attributes;
	private String barcode;
	private Boolean hasPhoto1;
	private Boolean hasPhoto2;
	private List<String> countryOfOrigins;
	private Integer productGroupId;
	
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}
	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}
	public List<ProductSpecificationEditModel> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<ProductSpecificationEditModel> attributes) {
		this.attributes = attributes;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public Boolean getHasPhoto1() {
		return hasPhoto1;
	}
	public void setHasPhoto1(Boolean hasPhoto1) {
		this.hasPhoto1 = hasPhoto1;
	}
	public Boolean getHasPhoto2() {
		return hasPhoto2;
	}
	public void setHasPhoto2(Boolean hasPhoto2) {
		this.hasPhoto2 = hasPhoto2;
	}
	public List<String> getCountryOfOrigins() {
		return countryOfOrigins;
	}
	public void setCountryOfOrigins(List<String> countryOfOrigins) {
		this.countryOfOrigins = countryOfOrigins;
	}
	public Integer getProductGroupId() {
		return productGroupId;
	}
	public void setProductGroupId(Integer productGroupId) {
		this.productGroupId = productGroupId;
	}
}
