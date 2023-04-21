package capi.model.masterMaintenance.businessParameterMaintenance;

public class GeneralSaveModel {
	
	private String summerStartDate;
	private String summerEndDate;
	private String winterStartDate;
	private String winterEndDate;
	private String[] countryOfOrigins;
	private String publicHolidayUrl;
	private String ruaRatio;
	private String calendarEventColor;
	private String assignmentEventColor;
	private String freezeSurveyMonth;
	private String mobileSynchronizationPeriod;
	private Boolean syncCalendar;
	private String delinkPeriod;
	
	public String getSummerStartDate() {
		return summerStartDate;
	}
	public void setSummerStartDate(String summerStartDate) {
		this.summerStartDate = summerStartDate;
	}
	public String getSummerEndDate() {
		return summerEndDate;
	}
	public void setSummerEndDate(String summerEndDate) {
		this.summerEndDate = summerEndDate;
	}
	public String getWinterStartDate() {
		return winterStartDate;
	}
	public void setWinterStartDate(String winterStartDate) {
		this.winterStartDate = winterStartDate;
	}
	public String getWinterEndDate() {
		return winterEndDate;
	}
	public void setWinterEndDate(String winterEndDate) {
		this.winterEndDate = winterEndDate;
	}
	public String[] getCountryOfOrigins() {
		return countryOfOrigins;
	}
	public void setCountryOfOrigins(String[] countryOfOrigins) {
		this.countryOfOrigins = countryOfOrigins;
	}
	public String getPublicHolidayUrl() {
		return publicHolidayUrl;
	}
	public void setPublicHolidayUrl(String publicHolidayUrl) {
		this.publicHolidayUrl = publicHolidayUrl;
	}
	public String getRuaRatio() {
		return ruaRatio;
	}
	public void setRuaRatio(String ruaRatio) {
		this.ruaRatio = ruaRatio;
	}
	public String getcalendarEventColor() {
		return calendarEventColor;
	}
	public void setcalendarEventColor(String calendarEventColor) {
		this.calendarEventColor = calendarEventColor;
	}
	public String getAssignmentEventColor() {
		return assignmentEventColor;
	}
	public void setAssignmentEventColor(String assignmentEventColor) {
		this.assignmentEventColor = assignmentEventColor;
	}
	public String getFreezeSurveyMonth() {
		return freezeSurveyMonth;
	}
	public void setFreezeSurveyMonth(String freezeSurveyMonth) {
		this.freezeSurveyMonth = freezeSurveyMonth;
	}
	public String getMobileSynchronizationPeriod() {
		return mobileSynchronizationPeriod;
	}
	public void setMobileSynchronizationPeriod(String mobileSynchronizationPeriod) {
		this.mobileSynchronizationPeriod = mobileSynchronizationPeriod;
	}
	public Boolean getSyncCalendar() {
		return syncCalendar;
	}
	public void setSyncCalendar(Boolean syncCalendar) {
		this.syncCalendar = syncCalendar;
	}
	public void setDelinkPeriod(String delinkPeriod) {
		this.delinkPeriod = delinkPeriod;
	}
	public String getDelinkPeriod() {
		return delinkPeriod;
	}
	
}
