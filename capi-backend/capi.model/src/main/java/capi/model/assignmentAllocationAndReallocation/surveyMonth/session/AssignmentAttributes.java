package capi.model.assignmentAllocationAndReallocation.surveyMonth.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import capi.entity.User;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BackTrackDateDisplayModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.BatchCollectionDateEditModel;


public class AssignmentAttributes implements Serializable{
	private Integer assignmentAttributeId;
	private String session;
	private Integer batchId;
	private Date startDate;
	private Date endDate;
	private List<BatchCollectionDateEditModel> batchCollectionDates;
	private List<BackTrackDateDisplayModel> collectionDateList;
	private String startDateStr;
	private String endDateStr;
	private String collectionDatesStr;
	private User officer;
	private Integer officerIds;
	private String allocationBatchRefId;
	private Integer allocationBatchId;
	private String referenceId;
	private String category;
	private Integer selectedBatchType;
	private Integer backTrackDateDisplayModelId;
	
	public AssignmentAttributes(){
		this.assignmentAttributeId = null;
		this.batchId = 0;
		this.startDate = null;
		this.startDateStr = "";
		this.endDate = null;
		this.endDateStr = "";
		this.collectionDateList = new ArrayList<BackTrackDateDisplayModel>();
		this.collectionDatesStr = "";
		this.session = "";
		this.officer = null;
		this.officerIds = 0;
		this.allocationBatchRefId = "";
		this.allocationBatchId = 0;
		this.referenceId = "";
		this.category = "";
		this.selectedBatchType = 0;
	}

	public Integer getAssignmentAttributeId() {
		return assignmentAttributeId;
	}

	public void setAssignmentAttributeId(Integer assignmentAttributeId) {
		this.assignmentAttributeId = assignmentAttributeId;
	}
	
	public Integer getBatchId() {
		return batchId;
	}
	
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public String getStartDateStr() {
		return startDateStr;
	}
	
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public List<BatchCollectionDateEditModel> getBatchCollectionDates() {
		return batchCollectionDates;
	}

	public void setBatchCollectionDates(List<BatchCollectionDateEditModel> batchCollectionDates) {
		this.batchCollectionDates = batchCollectionDates;
	}

	public String getEndDateStr() {
		return endDateStr;
	}
	
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}
	
	public List<BackTrackDateDisplayModel> getCollectionDateList() {
		return collectionDateList;
	}
	
	public void setCollectionDateList(List<BackTrackDateDisplayModel> collectionDateList) {
		this.collectionDateList = collectionDateList;
	}
	
	public String getCollectionDatesStr() {
		return collectionDatesStr;
	}
	
	public void setCollectionDatesStr(String collectionDatesStr) {
		this.collectionDatesStr = collectionDatesStr;
	}
	
	public String getSession() {
		return session;
	}
	
	public void setSession(String session) {
		this.session = session;
	}
	
	public User getOfficer() {
		return officer;
	}
	
	public void setOfficer(User officer) {
		this.officer = officer;
	}
	
	public Integer getOfficerIds() {
		return officerIds;
	}
	
	public void setOfficerIds(Integer officerIds) {
		this.officerIds = officerIds;
	}
	
	public String getAllocationBatchRefId() {
		return allocationBatchRefId;
	}
	
	public void setAllocationBatchRefId(String allocationBatchRefId) {
		this.allocationBatchRefId = allocationBatchRefId;
	}
	
	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}
	
	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}
	
	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getSelectedBatchType() {
		return selectedBatchType;
	}

	public void setSelectedBatchType(Integer selectedBatchType) {
		this.selectedBatchType = selectedBatchType;
	}

	public Integer getBackTrackDateDisplayModelId() {
		return backTrackDateDisplayModelId;
	}

	public void setBackTrackDateDisplayModelId(Integer backTrackDateDisplayModelId) {
		this.backTrackDateDisplayModelId = backTrackDateDisplayModelId;
	}
	
}
