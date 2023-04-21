package capi.model.qualityControlManagement;


public class SupervisoryVisitDetailTableList {

	private Integer supervisoryVisitDetailId;

	private String assignmentId;

	private String referenceNo;

	private String survey;

	private Integer result;

	private String otherRemark;

	private Integer rowNumber;

	public Integer getSupervisoryVisitDetailId() {
		return supervisoryVisitDetailId;
	}

	public void setSupervisoryVisitDetailId(Integer supervisoryVisitDetailId) {
		this.supervisoryVisitDetailId = supervisoryVisitDetailId;
	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getOtherRemark() {
		return otherRemark;
	}

	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

}
