package capi.model.assignmentManagement;

import java.util.Date;

public class DataConversionModel {
	private Double previousEditedNPrice;
	private Double previousEditedSPrice;
	private Double editedNPrice;
	private Double editedSPrice;
	private Double newProductLastMonthNPrice;
	private Double newProductLastMonthSPrice;
	
	private Boolean isFRApplied;
	private Boolean isReturnGoods;
	private Boolean isReturnNewGoods;
	private Date lastFRAppliedDate;
	private Double frValue;
	private Boolean frPercentage;
	private Boolean isUseFRAdmin;
	private Integer keepNoMonth;
	
	
	public Double getPreviousEditedNPrice() {
		return previousEditedNPrice;
	}
	public void setPreviousEditedNPrice(Double previousEditedNPrice) {
		this.previousEditedNPrice = previousEditedNPrice;
	}
	public Double getPreviousEditedSPrice() {
		return previousEditedSPrice;
	}
	public void setPreviousEditedSPrice(Double previousEditedSPrice) {
		this.previousEditedSPrice = previousEditedSPrice;
	}
	public Double getEditedNPrice() {
		return editedNPrice;
	}
	public void setEditedNPrice(Double editedNPrice) {
		this.editedNPrice = editedNPrice;
	}
	public Double getEditedSPrice() {
		return editedSPrice;
	}
	public void setEditedSPrice(Double editedSPrice) {
		this.editedSPrice = editedSPrice;
	}
	public Double getNewProductLastMonthNPrice() {
		return newProductLastMonthNPrice;
	}
	public void setNewProductLastMonthNPrice(Double newProductLastMonthNPrice) {
		this.newProductLastMonthNPrice = newProductLastMonthNPrice;
	}
	public Double getNewProductLastMonthSPrice() {
		return newProductLastMonthSPrice;
	}
	public void setNewProductLastMonthSPrice(Double newProductLastMonthSPrice) {
		this.newProductLastMonthSPrice = newProductLastMonthSPrice;
	}
	public Boolean getIsFRApplied() {
		return isFRApplied;
	}
	public void setIsFRApplied(Boolean isFRApplied) {
		this.isFRApplied = isFRApplied;
	}
	public Boolean getIsReturnGoods() {
		return isReturnGoods;
	}
	public void setIsReturnGoods(Boolean isReturnGoods) {
		this.isReturnGoods = isReturnGoods;
	}
	public Boolean getIsReturnNewGoods() {
		return isReturnNewGoods;
	}
	public void setIsReturnNewGoods(Boolean isReturnNewGoods) {
		this.isReturnNewGoods = isReturnNewGoods;
	}
	public Date getLastFRAppliedDate() {
		return lastFRAppliedDate;
	}
	public void setLastFRAppliedDate(Date lastFRAppliedDate) {
		this.lastFRAppliedDate = lastFRAppliedDate;
	}
	public Double getFrValue() {
		return frValue;
	}
	public void setFrValue(Double frValue) {
		this.frValue = frValue;
	}
	public Boolean getFrPercentage() {
		return frPercentage;
	}
	public void setFrPercentage(Boolean frPercentage) {
		this.frPercentage = frPercentage;
	}
	public Boolean getIsUseFRAdmin() {
		return isUseFRAdmin;
	}
	public void setIsUseFRAdmin(Boolean isUseFRAdmin) {
		this.isUseFRAdmin = isUseFRAdmin;
	}
	public Integer getKeepNoMonth() {
		return keepNoMonth;
	}
	public void setKeepNoMonth(Integer keepNoMonth) {
		this.keepNoMonth = keepNoMonth;
	}
}
