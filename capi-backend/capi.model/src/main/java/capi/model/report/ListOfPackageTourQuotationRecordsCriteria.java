package capi.model.report;

import java.util.ArrayList;

public class ListOfPackageTourQuotationRecordsCriteria {

	private ArrayList<Integer> purpose;
	
	private ArrayList<Integer> cpiSurveyForm;

	//2018-01-04 cheung_cheng [MB9012] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
	private ArrayList<Integer> itemId;
	
	private ArrayList<Integer> quotationId;
	
	private String periodReferenceMonth;
	
	private String dataCollection;
	
	private String startMonth;
	
	private String endMonth;
	
	private ArrayList<String> cpiCompilationSeries1;
	
	private ArrayList<String> cpiCompilationSeries2;

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public ArrayList<Integer> getCpiSurveyForm() {
		return cpiSurveyForm;
	}

	public void setCpiSurveyForm(ArrayList<Integer> cpiSurveyForm) {
		this.cpiSurveyForm = cpiSurveyForm;
	}

	public ArrayList<Integer> getItemId() {
		return itemId;
	}

	public void setItemId(ArrayList<Integer> itemId) {
		this.itemId = itemId;
	}

	public ArrayList<Integer> getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(ArrayList<Integer> quotationId) {
		this.quotationId = quotationId;
	}

	public String getPeriodReferenceMonth() {
		return periodReferenceMonth;
	}

	public void setPeriodReferenceMonth(String periodReferenceMonth) {
		this.periodReferenceMonth = periodReferenceMonth;
	}


	public String getDataCollection() {
		return dataCollection;
	}
	

	public void setDataCollection(String dataCollection) {
		this.dataCollection = dataCollection;
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


	public ArrayList<String> getCpiCompilationSeries1() {
		return cpiCompilationSeries1;
	}

	
	public void setCpiCompilationSeries1(ArrayList<String> cpiCompilationSeries1) {
		this.cpiCompilationSeries1 = cpiCompilationSeries1;
	}

	
	public ArrayList<String> getCpiCompilationSeries2() {
		return cpiCompilationSeries2;
	}

	
	public void setCpiCompilationSeries2(ArrayList<String> cpiCompilationSeries2) {
		this.cpiCompilationSeries2 = cpiCompilationSeries2;
	}

}
