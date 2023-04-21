package capi.model.assignmentManagement.assignmentManagement;

import java.util.Date;

import capi.model.shared.quotationRecord.OutletViewModel;

public class EditModel {
	private Integer assignmentId;
	private Integer userId;
	private Integer outletId;
	private String surveyMonth;
	private String pointToNote;
	private String personInCharge;
	private OutletViewModel outlet;
	private String preSelectTab;
	private String preSelectUnitCategory;
	private Integer preSelectDateSelectedAssignmentId;
	private String preSelectDateSelected;
	private Date createdDate;
	private Date modifiedDate;
	private boolean lockFirmStatus;
	private String preSelectConsignmentCounter;
	private Integer preSelectVerificationType;
	private Integer normalAssignmentStatus;
	private QuotationRecordCountByTabModel count;
	private boolean isNormalAssignment;
	private boolean isNewOutlet;
	
	public Integer getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
	public String getSurveyMonth() {
		return surveyMonth;
	}
	public void setSurveyMonth(String surveyMonth) {
		this.surveyMonth = surveyMonth;
	}
	public String getPointToNote() {
		return pointToNote;
	}
	public void setPointToNote(String pointToNote) {
		this.pointToNote = pointToNote;
	}
	public String getPersonInCharge() {
		return personInCharge;
	}
	public void setPersonInCharge(String personInCharge) {
		this.personInCharge = personInCharge;
	}
	public OutletViewModel getOutlet() {
		return outlet;
	}
	public void setOutlet(OutletViewModel outlet) {
		this.outlet = outlet;
	}
	public String getPreSelectTab() {
		return preSelectTab;
	}
	public void setPreSelectTab(String preSelectTab) {
		this.preSelectTab = preSelectTab;
	}
	public String getPreSelectUnitCategory() {
		return preSelectUnitCategory;
	}
	public void setPreSelectUnitCategory(String preSelectUnitCategory) {
		this.preSelectUnitCategory = preSelectUnitCategory;
	}
	public Integer getPreSelectDateSelectedAssignmentId() {
		return preSelectDateSelectedAssignmentId;
	}
	public void setPreSelectDateSelectedAssignmentId(Integer preSelectDateSelectedAssignmentId) {
		this.preSelectDateSelectedAssignmentId = preSelectDateSelectedAssignmentId;
	}
	public String getPreSelectDateSelected() {
		return preSelectDateSelected;
	}
	public void setPreSelectDateSelected(String preSelectDateSelected) {
		this.preSelectDateSelected = preSelectDateSelected;
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
	public boolean isLockFirmStatus() {
		return lockFirmStatus;
	}
	public void setLockFirmStatus(boolean lockFirmStatus) {
		this.lockFirmStatus = lockFirmStatus;
	}
	public String getPreSelectConsignmentCounter() {
		return preSelectConsignmentCounter;
	}
	public void setPreSelectConsignmentCounter(String preSelectConsignmentCounter) {
		this.preSelectConsignmentCounter = preSelectConsignmentCounter;
	}
	public Integer getPreSelectVerificationType() {
		return preSelectVerificationType;
	}
	public void setPreSelectVerificationType(Integer preSelectVerificationType) {
		this.preSelectVerificationType = preSelectVerificationType;
	}
	public Integer getNormalAssignmentStatus() {
		return normalAssignmentStatus;
	}
	public void setNormalAssignmentStatus(Integer normalAssignmentStatus) {
		this.normalAssignmentStatus = normalAssignmentStatus;
	}
	public QuotationRecordCountByTabModel getCount() {
		return count;
	}
	public void setCount(QuotationRecordCountByTabModel count) {
		this.count = count;
	}
	public boolean isNormalAssignment() {
		return isNormalAssignment;
	}
	public void setNormalAssignment(boolean isNormalAssignment) {
		this.isNormalAssignment = isNormalAssignment;
	}
	public boolean isNewOutlet() {
		return isNewOutlet;
	}
	public void setNewOutlet(boolean isNewOutlet) {
		this.isNewOutlet = isNewOutlet;
	}
}
