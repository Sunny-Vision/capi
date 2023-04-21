package capi.model.qualityControlManagement;


public class SpotCheckApprovalTableList {

	private Integer spotCheckFormId;

	private Integer officerId;

	private String officerCode;

	private String officerName;

	private String officerTeam;

	private String survey;

	private String supervisorCode;
	
	private String checkerCode;

	private String spotCheckDate;

	public Integer getSpotCheckFormId() {
		return spotCheckFormId;
	}

	public void setSpotCheckFormId(Integer spotCheckFormId) {
		this.spotCheckFormId = spotCheckFormId;
	}

	public Integer getOfficerId() {
		return officerId;
	}

	public void setOfficerId(Integer officerId) {
		this.officerId = officerId;
	}

	public String getOfficerCode() {
		return officerCode;
	}

	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}

	public String getOfficerName() {
		return officerName;
	}

	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}

	public String getOfficerTeam() {
		return officerTeam;
	}

	public void setOfficerTeam(String officerTeam) {
		this.officerTeam = officerTeam;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public String getSupervisorCode() {
		return supervisorCode;
	}

	public void setSupervisorCode(String supervisorCode) {
		this.supervisorCode = supervisorCode;
	}

	public String getCheckerCode() {
		return checkerCode;
	}

	public void setCheckerCode(String checkerCode) {
		this.checkerCode = checkerCode;
	}

	public String getSpotCheckDate() {
		return spotCheckDate;
	}

	public void setSpotCheckDate(String spotCheckDate) {
		this.spotCheckDate = spotCheckDate;
	}

}
