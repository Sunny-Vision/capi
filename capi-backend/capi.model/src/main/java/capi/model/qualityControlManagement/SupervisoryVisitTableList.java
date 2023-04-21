package capi.model.qualityControlManagement;


public class SupervisoryVisitTableList {

	private Integer supervisoryVisitFormId;

	private String visitDate;

	private Integer fieldOfficerId;

	private String fieldOfficer;

	private String fieldOfficerName;

	private Integer supervisorId;
	
	private String checkerCode;

	private String status;

	public Integer getSupervisoryVisitFormId() {
		return supervisoryVisitFormId;
	}

	public void setSupervisoryVisitFormId(Integer supervisoryVisitFormId) {
		this.supervisoryVisitFormId = supervisoryVisitFormId;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public Integer getFieldOfficerId() {
		return fieldOfficerId;
	}

	public void setFieldOfficerId(Integer fieldOfficerId) {
		this.fieldOfficerId = fieldOfficerId;
	}

	public String getFieldOfficer() {
		return fieldOfficer;
	}

	public void setFieldOfficer(String fieldOfficer) {
		this.fieldOfficer = fieldOfficer;
	}

	public String getFieldOfficerName() {
		return fieldOfficerName;
	}

	public void setFieldOfficerName(String fieldOfficerName) {
		this.fieldOfficerName = fieldOfficerName;
	}

	public Integer getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
	}

	public String getCheckerCode() {
		return checkerCode;
	}

	public void setCheckerCode(String checkerCode) {
		this.checkerCode = checkerCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
