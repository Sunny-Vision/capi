package capi.model.api.onlineFunction;

import java.util.Date;

public class PECheckFormOnlineModel {

	private Integer peCheckFormId;
	
	private String contactPerson;
	
	private Date checkingDate;
	
	private String checkingTime;
	
	private Integer officerId;
	
	private Integer checkingMode;
	
	private String peCheckRemark;
	
	private String otherRemark;
	
	private String status;
	
	private boolean isNonContact;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private Integer contactDateResult;
	
	private Integer contactTimeResult;
	
	private Integer contactDurationResult;
	
	private Integer contactModeResult;
	
	private Integer dateCollectedResult;
	
	private Integer othersResult;
	
	private Integer userId;
	
	private Integer localId;
	
	private String localDbRecordStatus;
	
	private AssignmentOnlineModel assignment;

	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}

	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
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

	public String getCheckingTime() {
		return checkingTime;
	}

	public void setCheckingTime(String checkingTime) {
		this.checkingTime = checkingTime;
	}

	public Integer getOfficerId() {
		return officerId;
	}

	public void setOfficerId(Integer officerId) {
		this.officerId = officerId;
	}

	public Integer getCheckingMode() {
		return checkingMode;
	}

	public void setCheckingMode(Integer checkingMode) {
		this.checkingMode = checkingMode;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public String getLocalDbRecordStatus() {
		return localDbRecordStatus;
	}

	public void setLocalDbRecordStatus(String localDbRecordStatus) {
		this.localDbRecordStatus = localDbRecordStatus;
	}

	public AssignmentOnlineModel getAssignment() {
		return assignment;
	}

	public void setAssignment(AssignmentOnlineModel assignment) {
		this.assignment = assignment;
	}
	
}
