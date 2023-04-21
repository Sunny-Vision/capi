package capi.model.report;

import java.util.List;

public class RUACaseReportOverviewCriteria {
	
	private List<Integer> officerIds;
	
	private List<Integer> districtIds;
	
	private String refMonth;

	public List<Integer> getOfficerIds() {
		return officerIds;
	}

	public void setOfficerIds(List<Integer> officerIds) {
		this.officerIds = officerIds;
	}

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	
	public List<Integer> getDistrictIds() {
		return districtIds;
	}
	

	public void setDistrictIds(List<Integer> districtIds) {
		this.districtIds = districtIds;
	}
}