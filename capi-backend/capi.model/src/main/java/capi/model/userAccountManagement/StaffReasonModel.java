package capi.model.userAccountManagement;

public class StaffReasonModel {

	private Integer staffReasonId;
	
	private String fromDate;
	
	private String toDate;
	
	private String reason;

	public Integer getStaffReasonId() {
		return staffReasonId;
	}

	public void setStaffReasonId(Integer staffReasonId) {
		this.staffReasonId = staffReasonId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
}
