package capi.model.dashboard;

public class OutstandingQCAssignmentListModel extends BaseSectionModel {
	private Long spotCheck;
	private Long supervisoryCheck;
	private Long peCheck;
	
	public Long getSpotCheck() {
		return spotCheck;
	}
	public void setSpotCheck(Long spotCheck) {
		this.spotCheck = spotCheck;
	}
	public Long getSupervisoryCheck() {
		return supervisoryCheck;
	}
	public void setSupervisoryCheck(Long supervisoryCheck) {
		this.supervisoryCheck = supervisoryCheck;
	}
	public Long getPeCheck() {
		return peCheck;
	}
	public void setPeCheck(Long peCheck) {
		this.peCheck = peCheck;
	}
}