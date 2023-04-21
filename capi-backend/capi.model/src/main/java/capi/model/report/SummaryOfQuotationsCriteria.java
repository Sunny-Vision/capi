package capi.model.report;

import java.util.ArrayList;

public class SummaryOfQuotationsCriteria {

	private String startMonth;

	private String endMonth;

	private ArrayList<String> outletType;

	private ArrayList<Integer> district;

	private ArrayList<Integer> purpose;

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

	public ArrayList<String> getOutletType() {
		return outletType;
	}

	public void setOutletType(ArrayList<String> outletType) {
		this.outletType = outletType;
	}

	public ArrayList<Integer> getDistrict() {
		return district;
	}

	public void setDistrict(ArrayList<Integer> district) {
		this.district = district;
	}

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

}
