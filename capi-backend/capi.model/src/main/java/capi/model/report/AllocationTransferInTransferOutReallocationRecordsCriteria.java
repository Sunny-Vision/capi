package capi.model.report;

import java.util.ArrayList;

public class AllocationTransferInTransferOutReallocationRecordsCriteria {

	private String fromMonth;
	
	private String toMonth;
	
	private Integer userId;
	
	private Integer authorityLevel;
	
	private ArrayList<String> teams;
	
	private ArrayList<Integer> officerIds;
	
	private ArrayList<Integer> purpose;
	
	private ArrayList<Integer> collectionMode;
	
	private ArrayList<String> allocationBatch;
	
	private Integer stage;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}

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

	public ArrayList<String> getTeams() {
		return teams;
	}

	public void setTeams(ArrayList<String> teams) {
		this.teams = teams;
	}

	public ArrayList<Integer> getOfficerIds() {
		return officerIds;
	}

	public void setOfficerIds(ArrayList<Integer> officerIds) {
		this.officerIds = officerIds;
	}

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public ArrayList<Integer> getCollectionMode() {
		return collectionMode;
	}

	public void setCollectionMode(ArrayList<Integer> collectionMode) {
		this.collectionMode = collectionMode;
	}

	public ArrayList<String> getAllocationBatch() {
		return allocationBatch;
	}

	public void setAllocationBatch(ArrayList<String> allocationBatch) {
		this.allocationBatch = allocationBatch;
	}

	
	public Integer getStage() {
		return stage;
	}
	

	public void setStage(Integer stage) {
		this.stage = stage;
	}
	
}
