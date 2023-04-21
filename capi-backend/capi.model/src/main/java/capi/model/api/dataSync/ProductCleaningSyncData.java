package capi.model.api.dataSync;

import java.util.List;

public class ProductCleaningSyncData {

	private Integer productId;
	
	private List<Integer> replaceProductId;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public List<Integer> getReplaceProductId() {
		return replaceProductId;
	}

	public void setReplaceProductId(List<Integer> replaceProductId) {
		this.replaceProductId = replaceProductId;
	}

}
