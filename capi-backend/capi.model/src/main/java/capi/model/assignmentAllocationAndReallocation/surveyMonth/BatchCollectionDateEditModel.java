package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.util.Date;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.BackTrackDate;

public class BatchCollectionDateEditModel {
	
	private Integer assignmentAttributeId;
	private Integer assignmentAttributeRefId;
	private Integer batchCollectionDateId;
	private Date date;
	private Boolean hasBackTrack;
	private Date BackTrackDate1;
	private Date BackTrackDate2;
	private Date BackTrackDate3;
	private Integer backTrackDateDisplayModelId;
	
	public Integer getAssignmentAttributeId() {
		return assignmentAttributeId;
	}
	public void setAssignmentAttributeId(Integer assignmentAttributeId) {
		this.assignmentAttributeId = assignmentAttributeId;
	}
	public Integer getAssignmentAttributeRefId() {
		return assignmentAttributeRefId;
	}
	public void setAssignmentAttributeRefId(Integer assignmentAttributeRefId) {
		this.assignmentAttributeRefId = assignmentAttributeRefId;
	}
	public Integer getBatchCollectionDateId() {
		return batchCollectionDateId;
	}
	public void setBatchCollectionDateId(Integer batchCollectionDateId) {
		this.batchCollectionDateId = batchCollectionDateId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Boolean getHasBackTrack() {
		return hasBackTrack;
	}
	public void setHasBackTrack(Boolean hasBackTrack) {
		this.hasBackTrack = hasBackTrack;
	}
	public Date getBackTrackDate1() {
		return BackTrackDate1;
	}
	public void setBackTrackDate1(Date backTrackDate1) {
		BackTrackDate1 = backTrackDate1;
	}
	public Date getBackTrackDate2() {
		return BackTrackDate2;
	}
	public void setBackTrackDate2(Date backTrackDate2) {
		BackTrackDate2 = backTrackDate2;
	}
	public Date getBackTrackDate3() {
		return BackTrackDate3;
	}
	public void setBackTrackDate3(Date backTrackDate3) {
		BackTrackDate3 = backTrackDate3;
	}
	public Integer getBackTrackDateDisplayModelId() {
		return backTrackDateDisplayModelId;
	}
	public void setBackTrackDateDisplayModelId(Integer backTrackDateDisplayModelId) {
		this.backTrackDateDisplayModelId = backTrackDateDisplayModelId;
	}
	public void convert(BackTrackDate sessionModel, Integer assignmentAttributeId, Integer assignmentAttributeRefId){
		boolean isEmpty = sessionModel.getBackTrackDateList() == null || sessionModel.getBackTrackDateList().isEmpty();

		if(assignmentAttributeRefId != null){
			this.setAssignmentAttributeRefId(assignmentAttributeRefId);
		}
		if(assignmentAttributeId != null){
			this.setAssignmentAttributeId(assignmentAttributeId);
		}
		this.setAssignmentAttributeRefId(assignmentAttributeRefId);
		this.setDate(sessionModel.getReferenceCollectionDate());
		this.setHasBackTrack(!isEmpty);
		if(sessionModel.getBatchCollectionDateId() != null){
			this.setBatchCollectionDateId(sessionModel.getBatchCollectionDateId());
		}
		
		if(!isEmpty){
			if(sessionModel.getBackTrackDateList().size() > 0){
				if(sessionModel.getBackTrackDateList().size() > 0){
					this.setBackTrackDate1(sessionModel.getBackTrackDateList().get(0));
				}
				if(sessionModel.getBackTrackDateList().size() > 1){
					this.setBackTrackDate2(sessionModel.getBackTrackDateList().get(1));
				}
				if(sessionModel.getBackTrackDateList().size() > 2){
					this.setBackTrackDate3(sessionModel.getBackTrackDateList().get(2));
				}
			}
		}
	}
}