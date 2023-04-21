package capi.model.userAccountManagement;

public class ActingModel {

	private Integer staffId;
	private String actedTeam;
	private Integer grantAuthorityLevel;

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public String getActedTeam() {
		return actedTeam;
	}

	public void setActedTeam(String actedTeam) {
		this.actedTeam = actedTeam;
	}

	public Integer getGrantAuthorityLevel() {
		return grantAuthorityLevel;
	}

	public void setGrantAuthorityLevel(Integer grantAuthorityLevel) {
		this.grantAuthorityLevel = grantAuthorityLevel;
	}

}
