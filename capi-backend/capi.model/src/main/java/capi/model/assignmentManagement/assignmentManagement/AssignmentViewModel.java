package capi.model.assignmentManagement.assignmentManagement;

import java.util.List;

import capi.model.KeyValueModel;

public class AssignmentViewModel {
	
	private KeyValueModel personInChargeSelected;
	
	private KeyValueModel surveyMonthSelected;
	
	private Integer assignmentStatus;
	
	private String deadline;
	
	private String quotationState;
	
	private List<KeyValueModel> districtSelected;
	
	private List<KeyValueModel> outletTypeSelected;
	
	private String search;
	
	private Integer orderColumn;
	
	private String orderDir;

	public KeyValueModel getPersonInChargeSelected() {
		return personInChargeSelected;
	}

	public void setPersonInChargeSelected(KeyValueModel personInChargeSelected) {
		this.personInChargeSelected = personInChargeSelected;
	}

	public KeyValueModel getSurveyMonthSelected() {
		return surveyMonthSelected;
	}

	public void setSurveyMonthSelected(KeyValueModel surveyMonthSelected) {
		this.surveyMonthSelected = surveyMonthSelected;
	}

	public Integer getAssignmentStatus() {
		return assignmentStatus;
	}

	public void setAssignmentStatus(Integer assignmentStatus) {
		this.assignmentStatus = assignmentStatus;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getQuotationState() {
		return quotationState;
	}

	public void setQuotationState(String quotationState) {
		this.quotationState = quotationState;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Integer getOrderColumn() {
		return orderColumn;
	}

	public void setOrderColumn(Integer orderColumn) {
		this.orderColumn = orderColumn;
	}

	public String getOrderDir() {
		return orderDir;
	}

	public void setOrderDir(String orderDir) {
		this.orderDir = orderDir;
	}

	public List<KeyValueModel> getDistrictSelected() {
		return districtSelected;
	}

	public void setDistrictSelected(List<KeyValueModel> districtSelected) {
		this.districtSelected = districtSelected;
	}

	public List<KeyValueModel> getOutletTypeSelected() {
		return outletTypeSelected;
	}

	public void setOutletTypeSelected(List<KeyValueModel> outletTypeSelected) {
		this.outletTypeSelected = outletTypeSelected;
	}
}
