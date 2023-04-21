package capi.model.timeLogManagement;

public class ValidationResultModel {
	private boolean valid;
	private Double assignmentDeviation;
	private Double sequenceDeviation;
	private Integer tpuDeviation;
	private String status;
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public Double getAssignmentDeviation() {
		return assignmentDeviation;
	}
	public void setAssignmentDeviation(Double assignmentDeviation) {
		this.assignmentDeviation = assignmentDeviation;
	}
	public Double getSequenceDeviation() {
		return sequenceDeviation;
	}
	public void setSequenceDeviation(Double sequenceDeviation) {
		this.sequenceDeviation = sequenceDeviation;
	}
	public Integer getTpuDeviation() {
		return tpuDeviation;
	}
	public void setTpuDeviation(Integer tpuDeviation) {
		this.tpuDeviation = tpuDeviation;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
