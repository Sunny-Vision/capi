package capi.model.qualityControlManagement;

import java.util.Date;

public class PECheckTableList {

	private String referenceNo;

	private String status;

	private String userStaffCode;

	private String userChineseName;

	private Integer peCheckFormId;
	
	private String outletName;
	
	private Integer firmCode;
	
	private Date modifiedDate;
	
	private String modifiedBy;
	
	private String surveyMonth;

	private Boolean isCertaintyCase;
	
	private Boolean isSectionHead;
	
	private Boolean isFieldTeamHead;
	
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserStaffCode() {
		return userStaffCode;
	}

	public void setUserStaffCode(String userStaffCode) {
		this.userStaffCode = userStaffCode;
	}

	public String getUserChineseName() {
		return userChineseName;
	}

	public void setUserChineseName(String userChineseName) {
		this.userChineseName = userChineseName;
	}

	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}

	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public Integer getFirmCode() {
		return firmCode;
	}

	public void setFirmCode(Integer firmCode) {
		this.firmCode = firmCode;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getSurveyMonth() {
		return surveyMonth;
	}

	public void setSurveyMonth(String surveyMonth) {
		this.surveyMonth = surveyMonth;
	}

	public Boolean getIsCertaintyCase() {
		return isCertaintyCase;
	}

	public void setIsCertaintyCase(Boolean isCertaintyCase) {
		this.isCertaintyCase = isCertaintyCase;
	}

	public Boolean getIsSectionHead() {
		return isSectionHead;
	}

	public void setIsSectionHead(Boolean isSectionHead) {
		this.isSectionHead = isSectionHead;
	}

	public Boolean getIsFieldTeamHead() {
		return isFieldTeamHead;
	}

	public void setIsFieldTeamHead(Boolean isFieldTeamHead) {
		this.isFieldTeamHead = isFieldTeamHead;
	}
	
}
