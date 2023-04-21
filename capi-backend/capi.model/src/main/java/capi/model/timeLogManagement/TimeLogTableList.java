package capi.model.timeLogManagement;

public class TimeLogTableList {
	private Integer id;
	private String date;
	private String officerCode;
	private String officerChineseName;
	private String status;
	private String approvedByCode;
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
	public String getOfficerCode() {
		return officerCode;
	}
	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}
	public String getOfficerChineseName() {
		return officerChineseName;
	}
	public void setOfficerChineseName(String officerChineseName) {
		this.officerChineseName = officerChineseName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApprovedByCode() {
		return approvedByCode;
	}
	public void setApprovedByCode(String approvedByCode) {
		this.approvedByCode = approvedByCode;
	}

	
}
