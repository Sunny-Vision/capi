package capi.model.masterMaintenance;

import java.util.ArrayList;

import capi.model.DatatableRequestModel;

public class UnitTableRequestModel extends DatatableRequestModel{

	private ArrayList<Integer> surveyTypeId = new ArrayList<Integer>();
	
	private ArrayList<Integer> sectionId = new ArrayList<Integer>();
	
	private ArrayList<Integer> groupId = new ArrayList<Integer>();
	
	private ArrayList<Integer> subGroupId = new ArrayList<Integer>();
	
	private ArrayList<Integer> itemId = new ArrayList<Integer>();
	
	private ArrayList<String> outletTypeId = new ArrayList<String>();

	private ArrayList<Integer> subItemId = new ArrayList<Integer>();
	
	private ArrayList<Integer> productCategoryId = new ArrayList<Integer>();
	
	private ArrayList<String> cpiBasePeriod = new ArrayList<String>();
	
	private String status;

	public ArrayList<Integer> getSurveyTypeId() {
		return surveyTypeId;
	}

	public void setSurveyTypeId(ArrayList<Integer> surveyTypeId) {
		this.surveyTypeId = surveyTypeId;
	}

	public ArrayList<Integer> getSectionId() {
		return sectionId;
	}

	public void setSectionId(ArrayList<Integer> sectionId) {
		this.sectionId = sectionId;
	}

	public ArrayList<Integer> getGroupId() {
		return groupId;
	}

	public void setGroupId(ArrayList<Integer> groupId) {
		this.groupId = groupId;
	}

	public ArrayList<Integer> getSubGroupId() {
		return subGroupId;
	}

	public void setSubGroupId(ArrayList<Integer> subGroupId) {
		this.subGroupId = subGroupId;
	}

	public ArrayList<Integer> getItemId() {
		return itemId;
	}

	public void setItemId(ArrayList<Integer> itemId) {
		this.itemId = itemId;
	}

	public ArrayList<String> getOutletTypeId() {
		return outletTypeId;
	}

	public void setOutletTypeId(ArrayList<String> outletTypeId) {
		this.outletTypeId = outletTypeId;
	}

	public ArrayList<Integer> getSubItemId() {
		return subItemId;
	}

	public void setSubItemId(ArrayList<Integer> subItemId) {
		this.subItemId = subItemId;
	}

	public ArrayList<Integer> getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(ArrayList<Integer> productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	public ArrayList<String> getCpiBasePeriod() {
		return cpiBasePeriod;
	}

	public void setCpiBasePeriod(ArrayList<String> cpiBasePeriod) {
		this.cpiBasePeriod = cpiBasePeriod;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
