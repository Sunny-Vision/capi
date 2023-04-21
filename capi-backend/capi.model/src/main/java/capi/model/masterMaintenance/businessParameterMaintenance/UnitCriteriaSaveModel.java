package capi.model.masterMaintenance.businessParameterMaintenance;

public class UnitCriteriaSaveModel {
	private Integer unitCriteriaId;
	
	@Deprecated
	private Integer unitId;
	private Integer noOfMonth;
	private Double percentageOfQuotation;
	private String prOperator;
	private Double prPercentage;
	
	private Integer[] itemIds;
	
	public Integer getUnitCriteriaId() {
		return unitCriteriaId;
	}
	public void setUnitCriteriaId(Integer unitCriteriaId) {
		this.unitCriteriaId = unitCriteriaId;
	}
	@Deprecated
	public Integer getUnitId() {
		return unitId;
	}
	@Deprecated
	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}
	public Integer getNoOfMonth() {
		return noOfMonth;
	}
	public void setNoOfMonth(Integer noOfMonth) {
		this.noOfMonth = noOfMonth;
	}
	public Double getPercentageOfQuotation() {
		return percentageOfQuotation;
	}
	public void setPercentageOfQuotation(Double percentageOfQuotation) {
		this.percentageOfQuotation = percentageOfQuotation;
	}
	public String getPrOperator() {
		return prOperator;
	}
	public void setPrOperator(String prOperator) {
		this.prOperator = prOperator;
	}
	public Double getPrPercentage() {
		return prPercentage;
	}
	public void setPrPercentage(Double prPercentage) {
		this.prPercentage = prPercentage;
	}
	public Integer[] getItemIds() {
		return itemIds;
	}
	public void setItemIds(Integer[] itemIds) {
		this.itemIds = itemIds;
	}
	
	
}
