package capi.model.qualityControlManagement;

import java.util.Date;

import capi.model.shared.quotationRecord.OutletViewModel;

public class PECheckFormModel {
	

	private Date createdDate;
	private Date modifiedDate;
	
	private Integer peCheckFormId;
	private Integer surveyMonthId;
	private String contactPerson;
	private Date checkingDate;
	private String checkingDateText;
	private Date checkingTime;
	private String checkingTimeText;
	private Integer assignmentId;
	private Integer officerId;
	private String officerText;
	private Integer userId;
	private String userText;
	private String enumerationDate;
	private String collectionMethod;
	private Integer checkingMode;
	private String checkingModeText;
	private String peCheckRemark;
	private String otherRemark;
	private String status;
	
	private boolean isNonContact;
	
	private Integer contactDateResult;
	private Integer contactTimeResult;
	private Integer contactDurationResult;
	private Integer contactModeResult;
	private Integer dateCollectedResult;
	private Integer othersResult;
	
	private OutletViewModel outlet;
	
	private Integer firmStatus;

	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}
	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}
	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}
	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public Date getCheckingDate() {
		return checkingDate;
	}
	public void setCheckingDate(Date checkingDate) {
		this.checkingDate = checkingDate;
	}
	public String getCheckingDateText() {
		return checkingDateText;
	}
	public void setCheckingDateText(String checkingDateText) {
		this.checkingDateText = checkingDateText;
	}
	public Date getCheckingTime() {
		return checkingTime;
	}
	public void setCheckingTime(Date checkingTime) {
		this.checkingTime = checkingTime;
	}
	public String getCheckingTimeText() {
		return checkingTimeText;
	}
	public void setCheckingTimeText(String checkingTimeText) {
		this.checkingTimeText = checkingTimeText;
	}
	public Integer getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	public Integer getOfficerId() {
		return officerId;
	}
	public void setOfficerId(Integer officerId) {
		this.officerId = officerId;
	}
	public String getOfficerText() {
		return officerText;
	}
	public void setOfficerText(String officerText) {
		this.officerText = officerText;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getEnumerationDate() {
		return enumerationDate;
	}
	public void setEnumerationDate(String enumerationDate) {
		this.enumerationDate = enumerationDate;
	}
	public String getCollectionMethod() {
		return collectionMethod;
	}
	public void setCollectionMethod(String collectionMethod) {
		this.collectionMethod = collectionMethod;
	}
	public String getUserText() {
		return userText;
	}
	public void setUserText(String userText) {
		this.userText = userText;
	}
	public Integer getCheckingMode() {
		return checkingMode;
	}
	public void setCheckingMode(Integer checkingMode) {
		this.checkingMode = checkingMode;
	}
	public String getCheckingModeText() {
		return checkingModeText;
	}
	public void setCheckingModeText(String checkingModeText) {
		this.checkingModeText = checkingModeText;
	}
	public String getPeCheckRemark() {
		return peCheckRemark;
	}
	public void setPeCheckRemark(String peCheckRemark) {
		this.peCheckRemark = peCheckRemark;
	}
	public String getOtherRemark() {
		return otherRemark;
	}
	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isNonContact() {
		return isNonContact;
	}
	public void setNonContact(boolean isNonContact) {
		this.isNonContact = isNonContact;
	}
	public Integer getContactDateResult() {
		return contactDateResult;
	}
	public void setContactDateResult(Integer contactDateResult) {
		this.contactDateResult = contactDateResult;
	}
	public Integer getContactTimeResult() {
		return contactTimeResult;
	}
	public void setContactTimeResult(Integer contactTimeResult) {
		this.contactTimeResult = contactTimeResult;
	}
	public Integer getContactDurationResult() {
		return contactDurationResult;
	}
	public void setContactDurationResult(Integer contactDurationResult) {
		this.contactDurationResult = contactDurationResult;
	}
	public Integer getContactModeResult() {
		return contactModeResult;
	}
	public void setContactModeResult(Integer contactModeResult) {
		this.contactModeResult = contactModeResult;
	}
	public Integer getDateCollectedResult() {
		return dateCollectedResult;
	}
	public void setDateCollectedResult(Integer dateCollectedResult) {
		this.dateCollectedResult = dateCollectedResult;
	}
	public Integer getOthersResult() {
		return othersResult;
	}
	public void setOthersResult(Integer othersResult) {
		this.othersResult = othersResult;
	}
	public OutletViewModel getOutlet() {
		return outlet;
	}
	public void setOutlet(OutletViewModel outlet) {
		this.outlet = outlet;
	}
	public Integer getFirmStatus() {
		return firmStatus;
	}
	public void setFirmStatus(Integer firmStatus) {
		this.firmStatus = firmStatus;
	}

}
