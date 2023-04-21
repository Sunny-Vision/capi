package capi.model.api.dataSync;

import java.util.Date;

public class UomSyncData {
	private Integer uomId;
	
	private String chineseName;
	
	private String englishName;
	
	private Integer uomCategoryId;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getUomId() {
		return uomId;
	}

	public void setUomId(Integer uomId) {
		this.uomId = uomId;
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

	public Integer getUomCategoryId() {
		return uomCategoryId;
	}

	public void setUomCategoryId(Integer uomCategoryId) {
		this.uomCategoryId = uomCategoryId;
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
