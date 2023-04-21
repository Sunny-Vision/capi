package capi.model.assignmentAllocationAndReallocation;

public class AssignmentAllocationStatisiticModel {

	private Integer surveyMonthId;
	
	private Long allocationBatchCnt;
	
	private Long totalAdjustment;
	
	private Long approvedAdjustment;

	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}

	public Long getAllocationBatchCnt() {
		return allocationBatchCnt;
	}

	public void setAllocationBatchCnt(Long allocationBatchCnt) {
		this.allocationBatchCnt = allocationBatchCnt;
	}

	public Long getTotalAdjustment() {
		return totalAdjustment;
	}

	public void setTotalAdjustment(Long totalAdjustment) {
		this.totalAdjustment = totalAdjustment;
	}

	public Long getApprovedAdjustment() {
		return approvedAdjustment;
	}

	public void setApprovedAdjustment(Long approvedAdjustment) {
		this.approvedAdjustment = approvedAdjustment;
	}
	
}
