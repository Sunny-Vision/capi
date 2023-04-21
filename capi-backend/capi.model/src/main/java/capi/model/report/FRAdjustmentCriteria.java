package capi.model.report;

import java.util.ArrayList;

public class FRAdjustmentCriteria {

	private ArrayList<Integer> purpose;

	private ArrayList<Integer> itemId;

	private ArrayList<String> outletType;

	private String frRequired;

	private String consignmentCounter;

	private String seasonalWithdrawal;

	private String startMonth;

	private String endMonth;

	private String appliedAdmin;

	private String frApplied;

	private String firstReturn;

	private String dataCollection;

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public ArrayList<Integer> getItemId() {
		return itemId;
	}

	public void setItemId(ArrayList<Integer> itemId) {
		this.itemId = itemId;
	}

	public ArrayList<String> getOutletType() {
		return outletType;
	}

	public void setOutletType(ArrayList<String> outletType) {
		this.outletType = outletType;
	}

	public String getFrRequired() {
		return frRequired;
	}

	public void setFrRequired(String frRequired) {
		this.frRequired = frRequired;
	}

	public String getConsignmentCounter() {
		return consignmentCounter;
	}

	public void setConsignmentCounter(String consignmentCounter) {
		this.consignmentCounter = consignmentCounter;
	}

	public String getSeasonalWithdrawal() {
		return seasonalWithdrawal;
	}

	public void setSeasonalWithdrawal(String seasonalWithdrawal) {
		this.seasonalWithdrawal = seasonalWithdrawal;
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

	public String getAppliedAdmin() {
		return appliedAdmin;
	}

	public void setAppliedAdmin(String appliedAdmin) {
		this.appliedAdmin = appliedAdmin;
	}

	public String getFrApplied() {
		return frApplied;
	}

	public void setFrApplied(String frApplied) {
		this.frApplied = frApplied;
	}

	public String getFirstReturn() {
		return firstReturn;
	}

	public void setFirstReturn(String firstReturn) {
		this.firstReturn = firstReturn;
	}

	public String getDataCollection() {
		return dataCollection;
	}

	public void setDataCollection(String dataCollection) {
		this.dataCollection = dataCollection;
	}

}
