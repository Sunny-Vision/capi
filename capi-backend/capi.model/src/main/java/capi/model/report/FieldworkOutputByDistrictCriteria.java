package capi.model.report;

public class FieldworkOutputByDistrictCriteria {
	
	private Integer[] officerIds;
	
	private Integer[] districtIds;
	
	private String refMonth;
	
	private Integer[] allocationBatchIds;
	
	private Integer[] purposeIds;

	public Integer[] getOfficerIds() {
		return officerIds;
	}

	public void setOfficerIds(Integer[] officerIds) {
		this.officerIds = officerIds;
	}

	public Integer[] getDistrictIds() {
		return districtIds;
	}

	public void setDistrictIds(Integer[] districtIds) {
		this.districtIds = districtIds;
	}

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	public Integer[] getAllocationBatchIds() {
		return allocationBatchIds;
	}

	public void setAllocationBatchIds(Integer[] allocationBatchIds) {
		this.allocationBatchIds = allocationBatchIds;
	}

	public Integer[] getPurposeIds() {
		return purposeIds;
	}

	public void setPurposeIds(Integer[] purposeIds) {
		this.purposeIds = purposeIds;
	}
	
	
}