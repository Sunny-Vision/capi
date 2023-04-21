package capi.model.commonLookup;

public class PECheckLookupTableList {
	private Integer peCheckFormId;
	private String firm;
	private String district;
	private String tpu;
	private String deadline;
	private String address;
	private Long noOfQuotation;
	private String convenientStartTime;
	private String convenientEndTime;
	private String convenientTime;
	private String outletRemark;
	private String fieldOfficerCode;
	private String chineseName;
	private String englishName;

	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}
	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}
	public String getFirm() {
		return firm;
	}
	public void setFirm(String firm) {
		this.firm = firm;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getTpu() {
		return tpu;
	}
	public void setTpu(String tpu) {
		this.tpu = tpu;
	}
	public String getDeadline() {
		return deadline;
	}
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Long getNoOfQuotation() {
		return noOfQuotation;
	}
	public void setNoOfQuotation(Long noOfQuotation) {
		this.noOfQuotation = noOfQuotation;
	}
	public String getConvenientStartTime() {
		return convenientStartTime;
	}
	public void setConvenientStartTime(String convenientStartTime) {
		this.convenientStartTime = convenientStartTime;
	}
	public String getConvenientEndTime() {
		return convenientEndTime;
	}
	public void setConvenientEndTime(String convenientEndTime) {
		this.convenientEndTime = convenientEndTime;
	}
//	public String getConvenientTime() {
//		return convenientStartTime == null ? "" : convenientStartTime + " - " + convenientEndTime;
//	}
	public String getOutletRemark() {
		return outletRemark;
	}
	public void setOutletRemark(String outletRemark) {
		this.outletRemark = outletRemark;
	}
	public String getFieldOfficerCode() {
		return fieldOfficerCode;
	}
	public void setFieldOfficerCode(String fieldOfficerCode) {
		this.fieldOfficerCode = fieldOfficerCode;
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
	public String getConvenientTime() {
		return convenientTime;
	}
	public void setConvenientTime(String convenientTime) {
		this.convenientTime = convenientTime;
	}
	
}
