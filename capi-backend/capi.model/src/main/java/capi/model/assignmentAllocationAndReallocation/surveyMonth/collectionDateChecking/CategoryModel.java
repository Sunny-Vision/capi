package capi.model.assignmentAllocationAndReallocation.surveyMonth.collectionDateChecking;

import java.util.ArrayList;
import java.util.List;

public class CategoryModel {
	private String category;
	private List<BatchCodeModel> batchCodeList;
	
	public CategoryModel(){
		this.batchCodeList = new ArrayList<BatchCodeModel>();
		this.category = "";
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<BatchCodeModel> getBatchCodeList() {
		return batchCodeList;
	}

	public void setBatchCodeList(List<BatchCodeModel> batchCodeList) {
		this.batchCodeList = batchCodeList;
	}
	
	
}
