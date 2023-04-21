package capi.model.assignmentManagement.newRecruitmentApproval;

public class TableList {
	private Integer assignmentId;
	private Integer outletId;
	private Integer firmCode;
	private String firm;
	private String outletType;
	private String district;
	private String tpu;
	private String address;
	private Long originalNoOfQuotation;
	private Long noOfQuotationRecruited;
	private Long noOfQuotationAfterRecruitment;
	private String ruaStartingFrom;
	private String referenceMonth;
	private String newRecruitmentDate;
	private Integer personInChargeId; // 2020-06-23: filter new recruitment list by field officer (PIR-231)
	private String personInCharge;
	private String newFirm;
	
	public Integer getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
	public Integer getFirmCode() {
		return firmCode;
	}
	public void setFirmCode(Integer firmCode) {
		this.firmCode = firmCode;
	}
	public String getFirm() {
		return firm;
	}
	public void setFirm(String firm) {
		this.firm = firm;
	}
	public String getOutletType() {
		return outletType;
	}
	public void setOutletType(String outletType) {
		this.outletType = outletType;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Long getOriginalNoOfQuotation() {
		return originalNoOfQuotation;
	}
	public void setOriginalNoOfQuotation(Long originalNoOfQuotation) {
		this.originalNoOfQuotation = originalNoOfQuotation;
	}
	public Long getNoOfQuotationRecruited() {
		return noOfQuotationRecruited;
	}
	public void setNoOfQuotationRecruited(Long noOfQuotationRecruited) {
		this.noOfQuotationRecruited = noOfQuotationRecruited;
	}
	public Long getNoOfQuotationAfterRecruitment() {
		return noOfQuotationAfterRecruitment;
	}
	public void setNoOfQuotationAfterRecruitment(Long noOfQuotationAfterRecruitment) {
		this.noOfQuotationAfterRecruitment = noOfQuotationAfterRecruitment;
	}
	public String getRuaStartingFrom() {
		return ruaStartingFrom;
	}
	public void setRuaStartingFrom(String ruaStartingFrom) {
		this.ruaStartingFrom = ruaStartingFrom;
	}
	public String getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public String getNewRecruitmentDate() {
		return newRecruitmentDate;
	}
	public void setNewRecruitmentDate(String newRecruitmentDate) {
		this.newRecruitmentDate = newRecruitmentDate;
	}
	public Integer getPersonInChargeId() {
		return personInChargeId;
	}
	public void setPersonInChargeId(Integer personInChargeId) {
		this.personInChargeId = personInChargeId;
	}
	public String getPersonInCharge() {
		return personInCharge;
	}
	public void setPersonInCharge(String personInCharge) {
		this.personInCharge = personInCharge;
	}
	public String getNewFirm() {
		return newFirm;
	}
	public void setNewFirm(String newFirm) {
		this.newFirm = newFirm;
	}
}