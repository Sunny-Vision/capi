package capi.model.dataImportExport;

public class ImportRebasingSubItemList {
	
	private Integer subItemId;
	
	private String code;
	
	private String chineseName;
	
	private String englishName;

	private Integer compilationMethod;
	
	private String outletTypeCode;

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

	public String getOutletTypeCode() {
		return outletTypeCode;
	}

	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}

	public Integer getCompilationMethod() {
		return compilationMethod;
	}

	public void setCompilationMethod(Integer compilationMethod) {
		this.compilationMethod = compilationMethod;
	}
	
	
}
