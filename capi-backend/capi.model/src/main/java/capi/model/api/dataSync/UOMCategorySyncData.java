package capi.model.api.dataSync;

import java.util.Date;

public class UOMCategorySyncData {
	private Integer uomCategoryId;
	
	private String description;
	
	private Date createdDate;
	
	private Date modifiedDate;

	public Integer getUomCategoryId() {
		return uomCategoryId;
	}

	public void setUomCategoryId(Integer uomCategoryId) {
		this.uomCategoryId = uomCategoryId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
