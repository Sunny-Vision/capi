package capi.model.assignmentAllocationAndReallocation.surveyMonth;

public class BatchQuotationActiveModel {

	private Integer batchId;

	private String code;

	private String status;

	private String batchCategory;

	private Integer assignmentType;

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBatchCategory() {
		return batchCategory;
	}

	public void setBatchCategory(String batchCategory) {
		this.batchCategory = batchCategory;
	}

	public Integer getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(Integer assignmentType) {
		this.assignmentType = assignmentType;
	}

}
