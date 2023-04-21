package capi.model.assignmentManagement.assignmentManagement;

public class QuotationRecordTableList {
	private Integer id;
	private String productAttribute;
	private Double nPrice;
	private Double sPrice;
	private String discount;
	private String status;
	private Integer availability;
	private Boolean isSPricePeculiar;
	private boolean isFlag;
	private boolean passValidation;
	private Integer firmStatus;
	private Integer batchId;
	private String batchCode;
	private Integer assignmentType;
	private String unitDisplayName;
	private Integer productId;
	private boolean hasBacktrack;
	private String formType;
	private Boolean hasICP;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProductAttribute() {
		return productAttribute;
	}
	public void setProductAttribute(String productAttribute) {
		this.productAttribute = productAttribute;
	}
	public Double getnPrice() {
		return nPrice;
	}
	public void setnPrice(Double nPrice) {
		this.nPrice = nPrice;
	}
	public Double getsPrice() {
		return sPrice;
	}
	public void setsPrice(Double sPrice) {
		this.sPrice = sPrice;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getAvailability() {
		return availability;
	}
	public void setAvailability(Integer availability) {
		this.availability = availability;
	}
	public Boolean getIsSPricePeculiar() {
		return isSPricePeculiar;
	}
	public void setIsSPricePeculiar(Boolean isSPricePeculiar) {
		this.isSPricePeculiar = isSPricePeculiar;
	}
	public boolean isFlag() {
		return isFlag;
	}
	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}
	public boolean isPassValidation() {
		return passValidation;
	}
	public void setPassValidation(boolean passValidation) {
		this.passValidation = passValidation;
	}
	public Integer getFirmStatus() {
		return firmStatus;
	}
	public void setFirmStatus(Integer firmStatus) {
		this.firmStatus = firmStatus;
	}
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public Integer getAssignmentType() {
		return assignmentType;
	}
	public void setAssignmentType(Integer assignmentType) {
		this.assignmentType = assignmentType;
	}
	public String getUnitDisplayName() {
		return unitDisplayName;
	}
	public void setUnitDisplayName(String unitDisplayName) {
		this.unitDisplayName = unitDisplayName;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public boolean isHasBacktrack() {
		return hasBacktrack;
	}
	public void setHasBacktrack(boolean hasBacktrack) {
		this.hasBacktrack = hasBacktrack;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public Boolean getHasICP() {
		return hasICP;
	}
	public void setHasICP(Boolean hasICP) {
		this.hasICP = hasICP;
	}
}
