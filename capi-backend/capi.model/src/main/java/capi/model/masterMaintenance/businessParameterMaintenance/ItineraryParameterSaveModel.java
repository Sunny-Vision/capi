package capi.model.masterMaintenance.businessParameterMaintenance;

public class ItineraryParameterSaveModel {

	private Integer assignmentDeviation;
	private Double assignmentDeviationPlus;
	private Double assignmentDeviationMinus;
	private Integer sequenceDeviation;
	private Double sequenceDeviationPercentage;
	private Integer tpuSequenceDeviation;
	private Integer tpuSequenceDeviationTimes;
	
	public Integer getAssignmentDeviation() {
		return assignmentDeviation;
	}
	public void setAssignmentDeviation(Integer assignmentDeviation) {
		this.assignmentDeviation = assignmentDeviation;
	}
	public Double getAssignmentDeviationPlus() {
		return assignmentDeviationPlus;
	}
	public void setAssignmentDeviationPlus(Double assignmentDeviationPlus) {
		this.assignmentDeviationPlus = assignmentDeviationPlus;
	}
	public Double getAssignmentDeviationMinus() {
		return assignmentDeviationMinus;
	}
	public void setAssignmentDeviationMinus(Double assignmentDeviationMinus) {
		this.assignmentDeviationMinus = assignmentDeviationMinus;
	}
	public Integer getSequenceDeviation() {
		return sequenceDeviation;
	}
	public void setSequenceDeviation(Integer sequenceDeviation) {
		this.sequenceDeviation = sequenceDeviation;
	}
	public Double getSequenceDeviationPercentage() {
		return sequenceDeviationPercentage;
	}
	public void setSequenceDeviationPercentage(Double sequenceDeviationPercentage) {
		this.sequenceDeviationPercentage = sequenceDeviationPercentage;
	}
	public Integer getTpuSequenceDeviation() {
		return tpuSequenceDeviation;
	}
	public void setTpuSequenceDeviation(Integer tpuSequenceDeviation) {
		this.tpuSequenceDeviation = tpuSequenceDeviation;
	}
	public Integer getTpuSequenceDeviationTimes() {
		return tpuSequenceDeviationTimes;
	}
	public void setTpuSequenceDeviationTimes(Integer tpuSequenceDeviationTimes) {
		this.tpuSequenceDeviationTimes = tpuSequenceDeviationTimes;
	}
	
}
