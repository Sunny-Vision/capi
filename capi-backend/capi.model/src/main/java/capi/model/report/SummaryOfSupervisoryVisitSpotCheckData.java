package capi.model.report;

public class SummaryOfSupervisoryVisitSpotCheckData {

	private Integer userId;
	private String userStaffCode;
	private String userEnglishName;
	private String userTeam;
	private Boolean isGHS;
	private Integer month;
	private String date;
	private Integer ghsSession;
	private String survey;
	private String userRankCode;
	private String superRankCode;
	private Integer ghsCount;
	private Integer dayShiftCount;
	private Integer nightShiftCount;
	private Integer cpiCount;

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserStaffCode() {
		return userStaffCode;
	}
	public void setUserStaffCode(String userStaffCode) {
		this.userStaffCode = userStaffCode;
	}
	public String getUserEnglishName() {
		return userEnglishName;
	}
	public void setUserEnglishName(String userEnglishName) {
		this.userEnglishName = userEnglishName;
	}
	public String getUserTeam() {
		return userTeam;
	}
	public void setUserTeam(String userTeam) {
		this.userTeam = userTeam;
	}
	public Boolean getIsGHS() {
		return isGHS;
	}
	public void setIsGHS(Boolean isGHS) {
		this.isGHS = isGHS;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getCpiCount() {
		return cpiCount;
	}
	public void setCpiCount(Integer cpiCount) {
		this.cpiCount = cpiCount;
	}
	public Integer getGhsSession() {
		return ghsSession;
	}
	public void setGhsSession(Integer ghsSession) {
		this.ghsSession = ghsSession;
	}
	public String getSuperRankCode() {
		return superRankCode;
	}
	public void setSuperRankCode(String superRankCode) {
		this.superRankCode = superRankCode;
	}
	public Integer getGhsCount() {
		return ghsCount;
	}
	public void setGhsCount(Integer ghsCount) {
		this.ghsCount = ghsCount;
	}
	public String getUserRankCode() {
		return userRankCode;
	}
	public void setUserRankCode(String userRankCode) {
		this.userRankCode = userRankCode;
	}
	public Integer getDayShiftCount() {
		return dayShiftCount;
	}
	public void setDayShiftCount(Integer dayShiftCount) {
		this.dayShiftCount = dayShiftCount;
	}
	public Integer getNightShiftCount() {
		return nightShiftCount;
	}
	public void setNightShiftCount(Integer nightShiftCount) {
		this.nightShiftCount = nightShiftCount;
	}
	public String getSurvey() {
		return survey;
	}
	public void setSurvey(String survey) {
		this.survey = survey;
	}

}
