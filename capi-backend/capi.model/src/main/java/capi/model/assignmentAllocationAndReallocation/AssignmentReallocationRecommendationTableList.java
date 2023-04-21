package capi.model.assignmentAllocationAndReallocation;

public class AssignmentReallocationRecommendationTableList {

	private Integer assignmentReallocationId;

	private Integer originalFieldOfficerId;

	private String originalFieldOfficer;

	private String originalFieldOfficerName;

	private Integer targetFieldOfficerId;

	private String targetFieldOfficer;

	private String targetFieldOfficerName;
	
	private Long countAssignment;
	
	private Long countQuotationRecord;
	
	private String createdDate;

	public Integer getAssignmentReallocationId() {
		return assignmentReallocationId;
	}

	public void setAssignmentReallocationId(Integer assignmentReallocationId) {
		this.assignmentReallocationId = assignmentReallocationId;
	}

	public Integer getOriginalFieldOfficerId() {
		return originalFieldOfficerId;
	}

	public void setOriginalFieldOfficerId(Integer originalFieldOfficerId) {
		this.originalFieldOfficerId = originalFieldOfficerId;
	}

	public String getOriginalFieldOfficer() {
		return originalFieldOfficer;
	}

	public void setOriginalFieldOfficer(String originalFieldOfficer) {
		this.originalFieldOfficer = originalFieldOfficer;
	}

	public String getOriginalFieldOfficerName() {
		return originalFieldOfficerName;
	}

	public void setOriginalFieldOfficerName(String originalFieldOfficerName) {
		this.originalFieldOfficerName = originalFieldOfficerName;
	}

	public Integer getTargetFieldOfficerId() {
		return targetFieldOfficerId;
	}

	public void setTargetFieldOfficerId(Integer targetFieldOfficerId) {
		this.targetFieldOfficerId = targetFieldOfficerId;
	}

	public String getTargetFieldOfficer() {
		return targetFieldOfficer;
	}

	public void setTargetFieldOfficer(String targetFieldOfficer) {
		this.targetFieldOfficer = targetFieldOfficer;
	}

	public String getTargetFieldOfficerName() {
		return targetFieldOfficerName;
	}

	public void setTargetFieldOfficerName(String targetFieldOfficerName) {
		this.targetFieldOfficerName = targetFieldOfficerName;
	}

	public Long getCountAssignment() {
		return countAssignment;
	}

	public void setCountAssignment(Long countAssignment) {
		this.countAssignment = countAssignment;
	}

	public Long getCountQuotationRecord() {
		return countQuotationRecord;
	}

	public void setCountQuotationRecord(Long countQuotationRecord) {
		this.countQuotationRecord = countQuotationRecord;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

}
