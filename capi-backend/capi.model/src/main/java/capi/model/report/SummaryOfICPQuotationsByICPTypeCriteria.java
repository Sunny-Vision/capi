package capi.model.report;

import java.util.ArrayList;

public class SummaryOfICPQuotationsByICPTypeCriteria {

	private ArrayList<String> icpType;
	
	//private ArrayList<Integer> unitId;
	private ArrayList<String> icpProductCode;
	
	private String fromMonth;

	private String toMonth;
	
	public ArrayList<String> getIcpType() {
		return icpType;
	}

	public void setIcpType(ArrayList<String> icpType) {
		this.icpType = icpType;
	}

//	public ArrayList<Integer> getUnitId() {
//		return unitId;
//	}
//
//	public void setUnitId(ArrayList<Integer> unitId) {
//		this.unitId = unitId;
//	}

	public String getFromMonth() {
		return fromMonth;
	}

	public void setFromMonth(String fromMonth) {
		this.fromMonth = fromMonth;
	}

	public String getToMonth() {
		return toMonth;
	}

	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
	}

	public ArrayList<String> getIcpProductCode() {
		return icpProductCode;
	}

	public void setIcpProductCode(ArrayList<String> icpProductCode) {
		this.icpProductCode = icpProductCode;
	}
	
}
