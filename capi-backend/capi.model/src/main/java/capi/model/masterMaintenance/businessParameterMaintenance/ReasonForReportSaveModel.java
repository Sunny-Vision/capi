package capi.model.masterMaintenance.businessParameterMaintenance;

import java.util.List;

import capi.entity.ReportReasonSetting;

public class ReasonForReportSaveModel {
	
	private List<ReportReasonSetting> newFieldworkAssignmentReason;
	private List<ReportReasonSetting> fieldworkAssignmentReason;
	public List<ReportReasonSetting> getNewFieldworkAssignmentReason() {
		return newFieldworkAssignmentReason;
	}
	public void setNewFieldworkAssignmentReason(
			List<ReportReasonSetting> newFieldworkAssignmentReason) {
		this.newFieldworkAssignmentReason = newFieldworkAssignmentReason;
	}
	public List<ReportReasonSetting> getFieldworkAssignmentReason() {
		return fieldworkAssignmentReason;
	}
	public void setFieldworkAssignmentReason(
			List<ReportReasonSetting> fieldworkAssignmentReason) {
		this.fieldworkAssignmentReason = fieldworkAssignmentReason;
	}
	
}
