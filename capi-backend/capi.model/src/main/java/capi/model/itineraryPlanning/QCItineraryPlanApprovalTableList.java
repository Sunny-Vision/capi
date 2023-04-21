package capi.model.itineraryPlanning;

public class QCItineraryPlanApprovalTableList {
	private Integer id;
	private String date;
	private String submitFrom;
	private String submitTo;
	private String status;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSubmitFrom() {
		return submitFrom;
	}
	public void setSubmitFrom(String submitFrom) {
		this.submitFrom = submitFrom;
	}
	public String getSubmitTo() {
		return submitTo;
	}
	public void setSubmitTo(String submitTo) {
		this.submitTo = submitTo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
