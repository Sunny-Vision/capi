package capi.model.timeLogManagement;

public class ItineraryCheckingTableList {
	private Integer id;
	private String date;
	private String officerCode;
	private String officerChineseName;
	private Double assignmentDeviation;
	private Double sequenceDeviation;
	private Integer tpuDeviation;
	private String itineraryCheckRemark;
	private boolean isAssignmentDeviationRemark;
	private boolean isSequenceDeviationRemark;
	private boolean isTpuDeviationRemark;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getOfficerCode() {
		return officerCode;
	}
	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}
	public String getOfficerChineseName() {
		return officerChineseName;
	}
	public void setOfficerChineseName(String officerChineseName) {
		this.officerChineseName = officerChineseName;
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
	public String getItineraryCheckRemark() {
		return itineraryCheckRemark;
	}
	public void setItineraryCheckRemark(String itineraryCheckRemark) {
		this.itineraryCheckRemark = itineraryCheckRemark;
	}
	public boolean isAssignmentDeviationRemark() {
		return isAssignmentDeviationRemark;
	}
	public void setAssignmentDeviationRemark(boolean isAssignmentDeviationRemark) {
		this.isAssignmentDeviationRemark = isAssignmentDeviationRemark;
	}
	public boolean isSequenceDeviationRemark() {
		return isSequenceDeviationRemark;
	}
	public void setSequenceDeviationRemark(boolean isSequenceDeviationRemark) {
		this.isSequenceDeviationRemark = isSequenceDeviationRemark;
	}
	public boolean isTpuDeviationRemark() {
		return isTpuDeviationRemark;
	}
	public void setTpuDeviationRemark(boolean isTpuDeviationRemark) {
		this.isTpuDeviationRemark = isTpuDeviationRemark;
	}
	
}
