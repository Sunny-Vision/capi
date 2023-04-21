package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.BackTrackDate;

public class BackTrackDateDisplayModel implements Serializable{
	private Integer batchCollectionDateId;
	private Integer backTrackDateDisplayModelId;
	private String batchCode;
	private Integer batchId;
	private List<BackTrackDate> backTrackDayList;
	private Integer assignmentAttributeRefId;
	
	public BackTrackDateDisplayModel(){
//		this.batchCollectionDateId = null;
		this.backTrackDateDisplayModelId = null;
		this.backTrackDayList = new ArrayList<BackTrackDate>();
		this.batchCode = "";
		this.batchId = 0;
		this.assignmentAttributeRefId = 0;
	}
	
	public String getBatchCode() {
		return batchCode;
	}
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
	public Integer getBatchCollectionDateId() {
		return batchCollectionDateId;
	}
	public void setBatchCollectionDateId(Integer batchCollectionDateId) {
		this.batchCollectionDateId = batchCollectionDateId;
	}
	public Integer getBackTrackDateDisplayModelId() {
		return backTrackDateDisplayModelId;
	}
	public void setBackTrackDateDisplayModelId(Integer backTrackDateDisplayModelId) {
		this.backTrackDateDisplayModelId = backTrackDateDisplayModelId;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public List<BackTrackDate> getBackTrackDayList() {
		return backTrackDayList;
	}
	public void setBackTrackDayList(List<BackTrackDate> backTrackDayList) {
		this.backTrackDayList = backTrackDayList;
	}
	public Integer getAssignmentAttributeRefId() {
		return assignmentAttributeRefId;
	}
	public void setAssignmentAttributeRefId(Integer assignmentAttributeRefId) {
		this.assignmentAttributeRefId = assignmentAttributeRefId;
	}
	
}
