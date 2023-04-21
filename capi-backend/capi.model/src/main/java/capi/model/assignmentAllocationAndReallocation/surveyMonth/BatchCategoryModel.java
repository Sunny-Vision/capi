package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.util.ArrayList;
import java.util.List;

public class BatchCategoryModel {
	private String batchCategoryName;
	
	private List<BatchModel> batchList; 
	
	private Boolean allAssignmentTypeOne;
	
	private Boolean allAssignmentTypeNotOne;
	
	public BatchCategoryModel(){
		this.batchCategoryName = "";
		this.batchList = new ArrayList<BatchModel>();
		this.allAssignmentTypeOne = false;
		this.allAssignmentTypeNotOne = false;
	}

	public String getBatchCategoryName() {
		return batchCategoryName;
	}

	public void setBatchCategoryName(String batchCategoryName) {
		this.batchCategoryName = batchCategoryName;
	}

	public List<BatchModel> getBatchList() {
		return batchList;
	}

	public void setBatchList(List<BatchModel> batchList) {
		this.batchList = batchList;
	}

	public Boolean getAllAssignmentTypeOne() {
		return allAssignmentTypeOne;
	}

	public void setAllAssignmentTypeOne(Boolean allAssignmentTypeOne) {
		this.allAssignmentTypeOne = allAssignmentTypeOne;
	}

	public Boolean getAllAssignmentTypeNotOne() {
		return allAssignmentTypeNotOne;
	}

	public void setAllAssignmentTypeNotOne(Boolean allAssignmentTypeNotOne) {
		this.allAssignmentTypeNotOne = allAssignmentTypeNotOne;
	}
	
}
