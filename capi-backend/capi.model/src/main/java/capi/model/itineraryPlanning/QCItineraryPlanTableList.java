package capi.model.itineraryPlanning;

public class QCItineraryPlanTableList {
	private Integer id;
	private String date;
	private	String submitTo;
	private String status;
	private Long scCount;
	private Long svCount;
	private Long peCount;
	
	
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
	public Long getScCount() {
		return scCount;
	}
	public void setScCount(Long scCount) {
		this.scCount = scCount;
	}
	public Long getSvCount() {
		return svCount;
	}
	public void setSvCount(Long svCount) {
		this.svCount = svCount;
	}
	public Long getPeCount() {
		return peCount;
	}
	public void setPeCount(Long peCount) {
		this.peCount = peCount;
	}
}
