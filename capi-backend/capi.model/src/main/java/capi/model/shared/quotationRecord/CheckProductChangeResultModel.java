package capi.model.shared.quotationRecord;

public class CheckProductChangeResultModel {
	private boolean isProductChange;
	private boolean isNewProduct;
	private Integer sameAttributeProductId;
	
	public boolean isProductChange() {
		return isProductChange;
	}
	public void setProductChange(boolean isProductChange) {
		this.isProductChange = isProductChange;
	}
	public boolean isNewProduct() {
		return isNewProduct;
	}
	public void setNewProduct(boolean isNewProduct) {
		this.isNewProduct = isNewProduct;
	}
	public Integer getSameAttributeProductId() {
		return sameAttributeProductId;
	}
	public void setSameAttributeProductId(Integer sameAttributeProductId) {
		this.sameAttributeProductId = sameAttributeProductId;
	}
}
