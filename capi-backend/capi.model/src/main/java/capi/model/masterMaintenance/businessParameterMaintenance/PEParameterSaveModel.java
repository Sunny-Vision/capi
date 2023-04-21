package capi.model.masterMaintenance.businessParameterMaintenance;

import java.util.List;

public class PEParameterSaveModel {
	private List<String> excludedOutletType;
	private List<Integer> purposeIds;
	private List<UnitCriteriaSaveModel> unitCriteria;
	private List<UnitCriteriaSaveModel> newUnitCriteria;
	private Integer peCheckMonth;
	private Double pePercentage;
	private Integer includeNewRecruitment;
	private Integer IncludeRUACase;
	
	public List<String> getExcludedOutletType() {
		return excludedOutletType;
	}
	public void setExcludedOutletType(List<String> excludedOutletType) {
		this.excludedOutletType = excludedOutletType;
	}
	public List<UnitCriteriaSaveModel> getUnitCriteria() {
		return unitCriteria;
	}
	public void setUnitCriteria(List<UnitCriteriaSaveModel> unitCriteria) {
		this.unitCriteria = unitCriteria;
	}
	public List<UnitCriteriaSaveModel> getNewUnitCriteria() {
		return newUnitCriteria;
	}
	public void setNewUnitCriteria(List<UnitCriteriaSaveModel> newUnitCriteria) {
		this.newUnitCriteria = newUnitCriteria;
	}
	public Integer getPeCheckMonth() {
		return peCheckMonth;
	}
	public void setPeCheckMonth(Integer peCheckMonth) {
		this.peCheckMonth = peCheckMonth;
	}
	public Double getPePercentage() {
		return pePercentage;
	}
	public void setPePercentage(Double pePercentage) {
		this.pePercentage = pePercentage;
	}
	public Integer getIncludeNewRecruitment() {
		return includeNewRecruitment;
	}
	public void setIncludeNewRecruitment(Integer includeNewRecruitment) {
		this.includeNewRecruitment = includeNewRecruitment;
	}
	public Integer getIncludeRUACase() {
		return IncludeRUACase;
	}
	public void setIncludeRUACase(Integer includeRUACase) {
		IncludeRUACase = includeRUACase;
	}
	public List<Integer> getPurposeIds() {
		return purposeIds;
	}
	public void setPurposeIds(List<Integer> purposeIds) {
		this.purposeIds = purposeIds;
	}
	
}
