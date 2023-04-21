package capi.model.report;

import java.util.ArrayList;

public class ProductReviewCriteria {

	private String refMonth;
	
	private ArrayList<Integer> purpose;
	
	private ArrayList<Integer> productGroup;
	
	private String reviewed;

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public ArrayList<Integer> getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ArrayList<Integer> productGroup) {
		this.productGroup = productGroup;
	}

	public String getReviewed() {
		return reviewed;
	}

	public void setReviewed(String reviewed) {
		this.reviewed = reviewed;
	}
	
}
