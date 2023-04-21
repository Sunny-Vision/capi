package capi.model.api.dataSync;

import java.util.Date;

public class SubItemSyncData {
	private Integer subItemId;
	
	private String code;
	
	private String chineseName;
	
	private String englishName;
	
	private Integer outletTypeId;
	
	private Date obsoleteDate;
	
	private Date effectiveDate;
	
	private Integer compilationMethod;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getSubItemId() {
		return subItemId;
	}

	public void setSubItemId(Integer subItemId) {
		this.subItemId = subItemId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Integer getOutletTypeId() {
		return outletTypeId;
	}

	public void setOutletTypeId(Integer outletTypeId) {
		this.outletTypeId = outletTypeId;
	}

	public Date getObsoleteDate() {
		return obsoleteDate;
	}

	public void setObsoleteDate(Date obsoleteDate) {
		this.obsoleteDate = obsoleteDate;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Integer getCompilationMethod() {
		return compilationMethod;
	}

	public void setCompilationMethod(Integer compilationMethod) {
		this.compilationMethod = compilationMethod;
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
