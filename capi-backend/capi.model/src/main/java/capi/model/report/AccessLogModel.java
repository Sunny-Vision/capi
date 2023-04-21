package capi.model.report;

public class AccessLogModel {

	private Integer accessLogId;
	private String username;
	private String event;
	private String eventDateTime;

	public Integer getAccessLogId() {
		return accessLogId;
	}

	public void setAccessLogId(Integer accessLogId) {
		this.accessLogId = accessLogId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEventDateTime() {
		return eventDateTime;
	}

	public void setEventDateTime(String eventDateTime) {
		this.eventDateTime = eventDateTime;
	}

}
