package capi.model.userAccountManagement;

public class StaffProfileTableList {

	private Integer userId;

	private String staffCode;

	private String userName;

	private String englishName;

	private String chineseName;

	private String supervisorStaffCode;

	private String rank;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getSupervisorStaffCode() {
		return supervisorStaffCode;
	}

	public void setSupervisorStaffCode(String supervisorStaffCode) {
		this.supervisorStaffCode = supervisorStaffCode;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

}
