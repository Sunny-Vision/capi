package capi.model.assignmentAllocationAndReallocation.surveyMonth.session;

public class BackTrackDateFormModel {
	private Integer referenceId;
	private Boolean isBackTrack;
	private String backTrackDateStr;
	private String batchCode;
	
	public BackTrackDateFormModel(){
		this.referenceId  = 0;
		this.isBackTrack = false;
		this.backTrackDateStr = "";
		this.batchCode = "";
	}

	public Integer getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}

	public Boolean getIsBackTrack() {
		return isBackTrack;
	}

	public void setIsBackTrack(Boolean isBackTrack) {
		this.isBackTrack = isBackTrack;
	}

	public String getBackTrackDateStr() {
		return backTrackDateStr;
	}

	public void setBackTrackDateStr(String backTrackDateStr) {
		this.backTrackDateStr = backTrackDateStr;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	
	
}
