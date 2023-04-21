package capi.model.dataImportExport;


public class ImportTaskList {

	private Integer importExportTaskId;
	
	private String startDate;
	
	private String finishedDate;
	
	private String status;
	
	private String errorMessage;
	
	private String taskName;
	
	private Integer taskNo;

	public Integer getImportExportTaskId() {
		return importExportTaskId;
	}

	public void setImportExportTaskId(Integer importExportTaskId) {
		this.importExportTaskId = importExportTaskId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(String finishedDate) {
		this.finishedDate = finishedDate;
	}
	
	public Integer getTaskNo() {
		return taskNo;
	}
	
	public void setTaskNo(Integer taskNo) {
		this.taskNo = taskNo;
	}
	
	
	
}
