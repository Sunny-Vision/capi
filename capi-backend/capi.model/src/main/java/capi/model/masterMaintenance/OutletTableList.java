package capi.model.masterMaintenance;

public class OutletTableList {
	private Integer outletId;
	private Integer firmCode;
	private String name;
	private String district;
	private String tpu;
	private String activeOutlet;
	private Long quotationCount;
	private String streetAddress;
	private String detailAddress;
	private String brCode;
	
	public int getOutletId() {
		return outletId;
	}
	public void setOutletId(int outletId) {
		this.outletId = outletId;
	}
	public Integer getFirmCode() {
		return firmCode;
	}
	public void setFirmCode(Integer firmCode) {
		this.firmCode = firmCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getActiveOutlet() {
		return activeOutlet;
	}
	public void setActiveOutlet(String activeOutlet) {
		this.activeOutlet = activeOutlet;
	}
	public Long getQuotationCount() {
		return quotationCount;
	}
	public void setQuotationCount(Long quotationCount) {
		this.quotationCount = quotationCount;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getDetailAddress() {
		return detailAddress;
	}
	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}
	public String getBrCode() {
		return brCode;
	}
	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
}
