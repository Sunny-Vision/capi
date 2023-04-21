package capi.model.report;

import java.util.Date;

public class TravellingClaimFormRecord {

	private String date;

	private String from;

	private String to;

	private String transport;

	private Double expenses;

	private String remark;

	private Boolean transit;
	
	private Integer timeLogId;
	
	private Date startTime;
	
	private String activity;
	
	private String destination;
	
	private boolean includeInTransportForm;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public Double getExpenses() {
		return expenses;
	}

	public void setExpenses(Double expenses) {
		this.expenses = expenses;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getTransit() {
		return transit;
	}

	public void setTransit(Boolean transit) {
		this.transit = transit;
	}

	public Integer getTimeLogId() {
		return timeLogId;
	}

	public void setTimeLogId(Integer timeLogId) {
		this.timeLogId = timeLogId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public boolean isIncludeInTransportForm() {
		return includeInTransportForm;
	}

	public void setIncludeInTransportForm(boolean includeInTransportForm) {
		this.includeInTransportForm = includeInTransportForm;
	}

}
