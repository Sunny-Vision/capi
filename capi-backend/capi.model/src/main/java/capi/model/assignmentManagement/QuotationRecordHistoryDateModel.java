package capi.model.assignmentManagement;

public class QuotationRecordHistoryDateModel {
	private String date;
	private Integer id;
	private boolean differentProduct;
	private Boolean isHiddenFieldOfficer;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public boolean isDifferentProduct() {
		return differentProduct;
	}
	public void setDifferentProduct(boolean differentProduct) {
		this.differentProduct = differentProduct;
	}
	public Boolean getIsHiddenFieldOfficer() {
		return isHiddenFieldOfficer;
	}
	public void setIsHiddenFieldOfficer(Boolean isHiddenFieldOfficer) {
		this.isHiddenFieldOfficer = isHiddenFieldOfficer;
	}
}
