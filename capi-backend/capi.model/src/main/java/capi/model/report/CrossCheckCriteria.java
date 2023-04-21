package capi.model.report;

import java.util.ArrayList;

public class CrossCheckCriteria {

	private ArrayList<Integer> purpose;

	private String refMonth;

	private ArrayList<String> crossCheckGroups;

	private Integer totalListedUnit;

	private Long totalUnit;

	public ArrayList<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(ArrayList<Integer> purpose) {
		this.purpose = purpose;
	}

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	public ArrayList<String> getCrossCheckGroups() {
		return crossCheckGroups;
	}

	public void setCrossCheckGroups(ArrayList<String> crossCheckGroups) {
		this.crossCheckGroups = crossCheckGroups;
	}

	public Integer getTotalListedUnit() {
		return totalListedUnit;
	}

	public void setTotalListedUnit(Integer totalListedUnit) {
		this.totalListedUnit = totalListedUnit;
	}

	public Long getTotalUnit() {
		return totalUnit;
	}

	public void setTotalUnit(Long totalUnit) {
		this.totalUnit = totalUnit;
	}

}
