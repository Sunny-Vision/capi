package capi.model.report;

import java.util.ArrayList;

public class QuotationRecordImputationReportCriteria {

	private ArrayList<Integer> purpose;
	private ArrayList<Integer> unitId;
	private ArrayList<Integer> quotationId;
	private String startMonth;
	private String endMonth;
	
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
	public ArrayList<Integer> getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(ArrayList<Integer> quotationId) {
		this.quotationId = quotationId;
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
}
