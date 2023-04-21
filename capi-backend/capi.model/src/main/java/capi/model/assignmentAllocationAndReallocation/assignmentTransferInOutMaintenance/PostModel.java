package capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance;

public class PostModel {
	private Integer submitToApproveUserId;
	private Integer assignmentAdjustmentId;
	private String rejectReason;
	private String btnAction;

	public Integer getSubmitToApproveUserId() {
		return submitToApproveUserId;
	}

	public void setSubmitToApproveUserId(Integer submitToApproveUserId) {
		this.submitToApproveUserId = submitToApproveUserId;
	}

	public Integer getAssignmentAdjustmentId() {
		return assignmentAdjustmentId;
	}

	public void setAssignmentAdjustmentId(Integer assignmentAdjustmentId) {
		this.assignmentAdjustmentId = assignmentAdjustmentId;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getBtnAction() {
		return btnAction;
	}

	public void setBtnAction(String btnAction) {
		this.btnAction = btnAction;
	}
}