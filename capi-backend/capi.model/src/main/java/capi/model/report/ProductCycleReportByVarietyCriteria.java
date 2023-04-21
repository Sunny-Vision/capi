package capi.model.report;

import java.util.ArrayList;

public class ProductCycleReportByVarietyCriteria {

	private ArrayList<Integer> purpose;
	private ArrayList<Integer> unitId;
	private String startMonth;
	private String endMonth;
	
	private ArrayList<Integer> cpiSurveyForm;
	private ArrayList<Integer> productGroup;
	
	public ArrayList<Integer> getPurpose() {
		return purpose;
	}
	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}
	public ArrayList<Integer> getUnitId() {
		return unitId;
	}
	public void setUnitId(ArrayList<Integer> unitId) {
		this.unitId = unitId;
	}
	public String getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}
	public String getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}
	public ArrayList<Integer> getCpiSurveyForm() {
		return cpiSurveyForm;
	}
	public void setCpiSurveyForm(ArrayList<Integer> cpiSurveyForm) {
		this.cpiSurveyForm = cpiSurveyForm;
	}
	public ArrayList<Integer> getProductGroup() {
		return productGroup;
	}
	public void setProductGroup(ArrayList<Integer> productGroup) {
		this.productGroup = productGroup;
	}
}
