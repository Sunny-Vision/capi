package capi.model.masterMaintenance;

public class UnitCodeLookupModel {
	private String chineseName;
	private String englishName;
	private Integer compilationMethod;
	
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
	public Integer getCompilationMethod() {
		return compilationMethod;
	}
	public void setCompilationMethod(Integer compilationMethod) {
		this.compilationMethod = compilationMethod;
	}
}
