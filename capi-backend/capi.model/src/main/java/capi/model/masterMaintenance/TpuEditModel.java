package capi.model.masterMaintenance;

import java.util.Date;

public class TpuEditModel {

	private Integer tpuId;

	private String code;

	private String description;

	private String councilDistrict;

	private Integer districtId;

	private String districtCode;

	private String districtEnglishName;
	
	private String districtLabel;

	private Date createdDate;

	private Date modifiedDate;

	public Integer getTpuId() {
		return tpuId;
	}

	public void setTpuId(Integer tpuId) {
		this.tpuId = tpuId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCouncilDistrict() {
		return councilDistrict;
	}

	public void setCouncilDistrict(String councilDistrict) {
		this.councilDistrict = councilDistrict;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictEnglishName() {
		return districtEnglishName;
	}

	public void setDistrictEnglishName(String districtEnglishName) {
		this.districtEnglishName = districtEnglishName;
	}

	public String getDistrictLabel() {
		return districtLabel;
	}

	public void setDistrictLabel(String districtLabel) {
		this.districtLabel = districtLabel;
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
