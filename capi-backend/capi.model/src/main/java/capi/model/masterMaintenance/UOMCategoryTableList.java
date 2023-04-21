package capi.model.masterMaintenance;

public class UOMCategoryTableList {
	private Integer uomCategoryId;
	
	private String description;
	
	private Long numberOfUOM;

	public Integer getId() {
		return uomCategoryId;
	}
	
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

	public Long getNumberOfUOM() {
		return numberOfUOM;
	}

	public void setNumberOfUOM(Long numberOfUOM) {
		this.numberOfUOM = numberOfUOM;
	}

}
