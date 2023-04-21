package capi.model.masterMaintenance;

public class DistrictTableList {

	private Integer districtId;

	private String code;

	private String chineseName;

	private String englishName;

	private Long tpus;
	
	private String coverage;

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
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

	public Long getTpus() {
		return tpus;
	}

	public void setTpus(Long tpus) {
		this.tpus = tpus;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

}
