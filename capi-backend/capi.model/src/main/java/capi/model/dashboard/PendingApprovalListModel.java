package capi.model.dashboard;

public class PendingApprovalListModel extends BaseSectionModel {
	private Long itineraryPlan;
	private Long submittedAssignment;
	private Long itineraryCheck;
	private Long rua;
	private Long newRecruitment;
	
	public Long getItineraryPlan() {
		return itineraryPlan;
	}
	public void setItineraryPlan(Long itineraryPlan) {
		this.itineraryPlan = itineraryPlan;
	}
	public Long getSubmittedAssignment() {
		return submittedAssignment;
	}
	public void setSubmittedAssignment(Long submittedAssignment) {
		this.submittedAssignment = submittedAssignment;
	}
	public Long getItineraryCheck() {
		return itineraryCheck;
	}
	public void setItineraryCheck(Long itineraryCheck) {
		this.itineraryCheck = itineraryCheck;
	}
	public Long getRua() {
		return rua;
	}
	public void setRua(Long rua) {
		this.rua = rua;
	}
	public Long getNewRecruitment() {
		return newRecruitment;
	}
	public void setNewRecruitment(Long newRecruitment) {
		this.newRecruitment = newRecruitment;
	}
}