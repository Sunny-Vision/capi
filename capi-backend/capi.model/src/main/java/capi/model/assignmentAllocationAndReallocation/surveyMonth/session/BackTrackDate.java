package capi.model.assignmentAllocationAndReallocation.surveyMonth.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackTrackDate implements Serializable{
	private Integer referenceId;
	private Integer batchCollectionDateId;
	private String batchCode;
	private Integer batchId;
	private String assignmentAttributesReferenceId;
	private String referenceCollectionDateStr;
	private Date referenceCollectionDate;
	private Boolean hasBackTrack;
	private String backTrackDateString;
	private List<Date> backTrackDateList;
	
	private String backTrackDateAvailableFromString;
	private Date backTrackDateAvailableFrom;
	private String backTrackDateAvailableToString;
	private Date backTrackDateAvailableTo;
	private String backTrackDateAvailableSkipString;
	private List<Date> backTrackDateAvailableSkipList;
	private String prevBackTrackDateString;
	private Date prevBackTrackDate; 
	
	public BackTrackDate(){
		this.backTrackDateAvailableSkipList = new ArrayList<Date>(); 
	}
	
	public Integer getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(Integer referenceId) {
		this.referenceId = referenceId;
	}
	public Integer getBatchCollectionDateId() {
		return batchCollectionDateId;
	}
	public void setBatchCollectionDateId(Integer batchCollectionDateId) {
		this.batchCollectionDateId = batchCollectionDateId;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
	public String getAssignmentAttributesReferenceId() {
		return assignmentAttributesReferenceId;
	}
	public void setAssignmentAttributesReferenceId(
			String assignmentAttributesReferenceId) {
		this.assignmentAttributesReferenceId = assignmentAttributesReferenceId;
	}
	public String getReferenceCollectionDateStr() {
		return referenceCollectionDateStr;
	}
	public void setReferenceCollectionDateStr(String referenceCollectionDateStr) {
		this.referenceCollectionDateStr = referenceCollectionDateStr;
	}
	public Date getReferenceCollectionDate() {
		return referenceCollectionDate;
	}
	public void setReferenceCollectionDate(Date referenceCollectionDate) {
		this.referenceCollectionDate = referenceCollectionDate;
	}
	public Boolean getHasBackTrack() {
		return hasBackTrack;
	}
	public void setHasBackTrack(Boolean hasBackTrack) {
		this.hasBackTrack = hasBackTrack;
	}
	public String getBackTrackDateString() {
		return backTrackDateString;
	}
	public void setBackTrackDateString(String backTrackDateString) {
		this.backTrackDateString = backTrackDateString;
	}
	public List<Date> getBackTrackDateList() {
		return backTrackDateList;
	}
	public void setBackTrackDateList(List<Date> backTrackDateList) {
		this.backTrackDateList = backTrackDateList;
	}
	public String getBackTrackDateAvailableFromString() {
		return backTrackDateAvailableFromString;
	}
	public void setBackTrackDateAvailableFromString(
			String backTrackDateAvailableFromString) {
		this.backTrackDateAvailableFromString = backTrackDateAvailableFromString;
	}
	public Date getBackTrackDateAvailableFrom() {
		return backTrackDateAvailableFrom;
	}
	public void setBackTrackDateAvailableFrom(Date backTrackDateAvailableFrom) {
		this.backTrackDateAvailableFrom = backTrackDateAvailableFrom;
	}
	public String getBackTrackDateAvailableToString() {
		return backTrackDateAvailableToString;
	}
	public void setBackTrackDateAvailableToString(
			String backTrackDateAvailableToString) {
		this.backTrackDateAvailableToString = backTrackDateAvailableToString;
	}
	public Date getBackTrackDateAvailableTo() {
		return backTrackDateAvailableTo;
	}
	public void setBackTrackDateAvailableTo(Date backTrackDateAvailableTo) {
		this.backTrackDateAvailableTo = backTrackDateAvailableTo;
	}
	public String getBackTrackDateAvailableSkipString() {
		return backTrackDateAvailableSkipString;
	}
	public void setBackTrackDateAvailableSkipString(
			String backTrackDateAvailableSkipString) {
		this.backTrackDateAvailableSkipString = backTrackDateAvailableSkipString;
	}
	public List<Date> getBackTrackDateAvailableSkipList() {
		return backTrackDateAvailableSkipList;
	}
	public void setBackTrackDateAvailableSkipList(
			List<Date> backTrackDateAvailableSkipList) {
		this.backTrackDateAvailableSkipList = backTrackDateAvailableSkipList;
	}

	public String getPrevBackTrackDateString() {
		return prevBackTrackDateString;
	}

	public void setPrevBackTrackDateString(String prevBackTrackDateString) {
		this.prevBackTrackDateString = prevBackTrackDateString;
	}

	public Date getPrevBackTrackDate() {
		return prevBackTrackDate;
	}

	public void setPrevBackTrackDate(Date prevBackTrackDate) {
		this.prevBackTrackDate = prevBackTrackDate;
	}
	
}
