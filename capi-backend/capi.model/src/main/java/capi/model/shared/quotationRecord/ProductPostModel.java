package capi.model.shared.quotationRecord;

import java.util.List;

import capi.model.productMaintenance.ProductSpecificationEditModel;

public class ProductPostModel {
	private String countryOfOrigin;
	private String barcode;
	private List<ProductSpecificationEditModel> attributes;
	private String displayName;
	private String photo1Path;
	private String photo2Path;
	private Integer productId;
	private Integer productGroupId;
	private String collectionDate;
	private String productRemark;
	private CheckProductChangeResultModel lastPostbackChangeResult;
	private boolean showSpecDialog;
	private boolean allowProductChange;
	
	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}
	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public List<ProductSpecificationEditModel> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<ProductSpecificationEditModel> attributes) {
		this.attributes = attributes;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	public String getProductRemark() {
		return productRemark;
	}
	public void setProductRemark(String productRemark) {
		this.productRemark = productRemark;
	}
	public String getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}
	public CheckProductChangeResultModel getLastPostbackChangeResult() {
		return lastPostbackChangeResult;
	}
	public void setLastPostbackChangeResult(CheckProductChangeResultModel lastPostbackChangeResult) {
		this.lastPostbackChangeResult = lastPostbackChangeResult;
	}
	public boolean isShowSpecDialog() {
		return showSpecDialog;
	}
	public void setShowSpecDialog(boolean showSpecDialog) {
		this.showSpecDialog = showSpecDialog;
	}
	public boolean isAllowProductChange() {
		return allowProductChange;
	}
	public void setAllowProductChange(boolean allowProductChange) {
		this.allowProductChange = allowProductChange;
	}
}
