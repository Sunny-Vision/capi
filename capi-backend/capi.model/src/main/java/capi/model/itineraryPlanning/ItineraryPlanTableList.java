package capi.model.itineraryPlanning;

public class ItineraryPlanTableList {
	private Integer id;
	private Integer version;
	private String date;
	private Long noOfAssignment;
	private String status;
	private String fieldOfficerCode;
	private String chineseName;
	private Integer timeLogId;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Long getNoOfAssignment() {
		return noOfAssignment;
	}
	public void setNoOfAssignment(Long noOfAssignment) {
		this.noOfAssignment = noOfAssignment;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFieldOfficerCode() {
		return fieldOfficerCode;
	}
	public void setFieldOfficerCode(String fieldOfficerCode) {
		this.fieldOfficerCode = fieldOfficerCode;
	}
	public String getChineseName() {
		return chineseName;
	}
	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}
	public Integer getTimeLogId() {
		return timeLogId;
	}
	public void setTimeLogId(Integer timeLogId) {
		this.timeLogId = timeLogId;
	}
	
}
