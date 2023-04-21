package capi.model.masterMaintenance;

public class UomModel {
	private Integer uomId;
	
	private String chineseName;
	
	private String englishName;
	
	private Integer uomCategoryId;
	
	private String description;
	
	public Integer getId() {
		return uomId;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	


}
