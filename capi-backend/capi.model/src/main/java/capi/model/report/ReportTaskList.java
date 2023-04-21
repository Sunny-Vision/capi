package capi.model.report;

public class ReportTaskList {

	private Integer reportTaskId;
	
	private String description;
	
	private String createdBy;
	
	private String status;
	
	private String createdDate;
	
	private String exceptionMessage;

	public Integer getReportTaskId() {
		return reportTaskId;
	}

	public void setReportTaskId(Integer reportTaskId) {
		this.reportTaskId = reportTaskId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
	
}
