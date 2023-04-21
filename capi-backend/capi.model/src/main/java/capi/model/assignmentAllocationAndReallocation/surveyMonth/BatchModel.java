package capi.model.assignmentAllocationAndReallocation.surveyMonth;

public class BatchModel {
	private Integer batchId;
	private String code;
	private Integer assignmentType;
	private Integer assignmentAttributeId;
	
	public BatchModel(){
		this.batchId = 0;
		this.code = "";
		this.assignmentType = 0;
		this.assignmentAttributeId = 0;
	}
	
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batcId) {
		this.batchId = batcId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public Integer getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(Integer assignmentType) {
		this.assignmentType = assignmentType;
	}

	public Integer getAssignmentAttributeId() {
		return assignmentAttributeId;
	}

	public void setAssignmentAttributeId(Integer assignmentAttributeId) {
		this.assignmentAttributeId = assignmentAttributeId;
	}
	
	
}
