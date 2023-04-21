package capi.model.report;

import java.util.ArrayList;

public class SummaryOfProgressReportByFieldOfficerCriteria {

	private ArrayList<Integer> purposeIds;
	
	private ArrayList<Integer> allocationBatchIds;
	
	private ArrayList<String> team;
	
	private ArrayList<Integer> officerIds;
	
	private String refMonth;
	

	public ArrayList<Integer> getPurposeIds() {
		return purposeIds;
	}

	public void setPurposeIds(ArrayList<Integer> purposeIds) {
		this.purposeIds = purposeIds;
	}

	public ArrayList<Integer> getAllocationBatchIds() {
		return allocationBatchIds;
	}

	public void setAllocationBatchIds(ArrayList<Integer> allocationBatchIds) {
		this.allocationBatchIds = allocationBatchIds;
	}

	public ArrayList<String> getTeam() {
		return team;
	}

	public void setTeam(ArrayList<String> team) {
		this.team = team;
	}

	public ArrayList<Integer> getOfficerIds() {
		return officerIds;
	}

	public void setOfficerIds(ArrayList<Integer> officerIds) {
		this.officerIds = officerIds;
	}

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}
	
}
