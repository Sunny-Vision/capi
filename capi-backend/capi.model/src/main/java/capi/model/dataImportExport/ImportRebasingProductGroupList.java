package capi.model.dataImportExport;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ImportRebasingProductGroupList {

	private Integer newId;
	
	private String newCode;
	
	private String chineseName;
	
	private String englishName;
	
	private Boolean isNewProductCategory;
	
	private List<Integer> oldIds;

	private Map<Integer, Integer> mapAttributes = new Hashtable<Integer, Integer>();  //<oldProductAttributeId, newProductAttributeId>
	
	private Map<Integer, Integer> mapProducts = new Hashtable<Integer, Integer>();	//<oldProductId, newProductId>
	
	public Integer getNewId() {
		return newId;
	}

	public void setNewId(Integer newId) {
		this.newId = newId;
	}

	public String getNewCode() {
		return newCode;
	}

	public void setNewCode(String newCode) {
		this.newCode = newCode;
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

	public List<Integer> getOldIds() {
		return oldIds;
	}

	public void setOldIds(List<Integer> oldIds) {
		this.oldIds = oldIds;
	}

	public Map<Integer, Integer> getMapAttributes() {
		return mapAttributes;
	}

	public void setMapAttributes(Map<Integer, Integer> mapAttributes) {
		this.mapAttributes = mapAttributes;
	}

	public Map<Integer, Integer> getMapProducts() {
		return mapProducts;
	}

	public void setMapProducts(Map<Integer, Integer> mapProducts) {
		this.mapProducts = mapProducts;
	}

	public Boolean getIsNewProductCategory() {
		return isNewProductCategory;
	}

	public void setIsNewProductCategory(Boolean isNewProductCategory) {
		this.isNewProductCategory = isNewProductCategory;
	}
	
	
}
