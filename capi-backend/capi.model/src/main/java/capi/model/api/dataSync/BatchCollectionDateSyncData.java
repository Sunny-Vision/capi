package capi.model.api.dataSync;

import java.util.Date;

public class BatchCollectionDateSyncData {

	private Integer batchCollectionDateId;
	
	
	private Date date;
	
	private Integer assignmentAttributeId;
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private boolean hasBackTrack;
	
	private Date backTrackDate1;
	
	private Date backTrackDate2;
	
	private Date backTrackDate3;

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

	public Integer getAssignmentAttributeId() {
		return assignmentAttributeId;
	}

	public void setAssignmentAttributeId(Integer assignmentAttributeId) {
		this.assignmentAttributeId = assignmentAttributeId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isHasBackTrack() {
		return hasBackTrack;
	}

	public void setHasBackTrack(boolean hasBackTrack) {
		this.hasBackTrack = hasBackTrack;
	}

	public Date getBackTrackDate1() {
		return backTrackDate1;
	}

	public void setBackTrackDate1(Date backTrackDate1) {
		this.backTrackDate1 = backTrackDate1;
	}

	public Date getBackTrackDate2() {
		return backTrackDate2;
	}

	public void setBackTrackDate2(Date backTrackDate2) {
		this.backTrackDate2 = backTrackDate2;
	}

	public Date getBackTrackDate3() {
		return backTrackDate3;
	}

	public void setBackTrackDate3(Date backTrackDate3) {
		this.backTrackDate3 = backTrackDate3;
	}
	
}
