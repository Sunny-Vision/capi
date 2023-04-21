package capi.model.report;

public class CalendarSummaryCriteria {

	private String refMonth;

	private Integer[] officerIds;
	
	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	public Integer[] getOfficerIds() {
		return officerIds;
	}

	public void setOfficerIds(Integer[] officerIds) {
		this.officerIds = officerIds;
	}
	
}
