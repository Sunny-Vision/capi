package capi.model.assignmentManagement;

import java.util.ArrayList;
import java.util.Date;

public class RUASettingEditModel {

	private Integer quotationId;

	private String unitCode;

	private String unitName;

	private Integer productId;

	private String productAttribute;

	private String firmName;

	private String batchCode;

	private Boolean isRUAAllDistrict;

	private Integer districtId;

	private String districtLabel;

	private ArrayList<Integer> userId;

	private String ruaDate;

	private Date createdDate;

	private Date modifiedDate;

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductAttribute() {
		return productAttribute;
	}

	public void setProductAttribute(String productAttribute) {
		this.productAttribute = productAttribute;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public Boolean getIsRUAAllDistrict() {
		return isRUAAllDistrict;
	}

	public void setIsRUAAllDistrict(Boolean isRUAAllDistrict) {
		this.isRUAAllDistrict = isRUAAllDistrict;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getDistrictLabel() {
		return districtLabel;
	}

	public void setDistrictLabel(String districtLabel) {
		this.districtLabel = districtLabel;
	}

	public ArrayList<Integer> getUserId() {
		return userId;
	}

	public void setUserId(ArrayList<Integer> userId) {
		this.userId = userId;
	}

	public String getRuaDate() {
		return ruaDate;
	}

	public void setRuaDate(String ruaDate) {
		this.ruaDate = ruaDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
