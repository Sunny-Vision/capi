package capi.model.assignmentAllocationAndReallocation.surveyMonth;

public class SurveyMonthDatesModel {

	private String startDate;
	private String endDate;
	private String closingDate;
	
	public SurveyMonthDatesModel(){
		this.startDate = "";
		this.endDate = "";
		this.closingDate = "";
				
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(String closingDate) {
		this.closingDate = closingDate;
	}
	
	
}
