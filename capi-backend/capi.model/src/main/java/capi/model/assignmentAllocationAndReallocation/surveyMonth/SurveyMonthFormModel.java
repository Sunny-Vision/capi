package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.util.ArrayList;
import java.util.List;

import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.BackTrackDateFormModel;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.SurveyMonth;

public class SurveyMonthFormModel {
	
	private SurveyMonth surveyMonth;
	
	private List<AllocationBatch> allocationBatch;
	
	private List<AllocationBatch> newAllocationBatch;
	
	private List<AssignmentAttributes> newAssignmentAttr;
	
	private List<AssignmentAttributes> assignmentAttr;
	
	private List<AssignmentAttributes> nonCateAssignmentAttr;
	
	private List<BackTrackDateFormModel> backTrackDate;
	
	public SurveyMonthFormModel(){
		this.surveyMonth = new SurveyMonth();
		this.allocationBatch = new ArrayList<AllocationBatch>();
		this.newAllocationBatch = new ArrayList<AllocationBatch>();
		this.backTrackDate = new ArrayList<BackTrackDateFormModel>();
	}

	public SurveyMonth getSurveyMonth() {
		return surveyMonth;
	}

	public void setSurveyMonth(SurveyMonth surveyMonth) {
		this.surveyMonth = surveyMonth;
	}

	public List<AllocationBatch> getAllocationBatch() {
		return allocationBatch;
	}

	public void setAllocationBatch(List<AllocationBatch> allocationBatch) {
		this.allocationBatch = allocationBatch;
	}

	public List<AllocationBatch> getNewAllocationBatch() {
		return newAllocationBatch;
	}

	public void setNewAllocationBatch(List<AllocationBatch> newAllocationBatch) {
		this.newAllocationBatch = newAllocationBatch;
	}

	public List<AssignmentAttributes> getNewAssignmentAttr() {
		return newAssignmentAttr;
	}

	public void setNewAssignmentAttr(List<AssignmentAttributes> newAssignmentAttr) {
		this.newAssignmentAttr = newAssignmentAttr;
	}

	public List<AssignmentAttributes> getAssignmentAttr() {
		return assignmentAttr;
	}

	public void setAssignmentAttr(List<AssignmentAttributes> assignmentAttr) {
		this.assignmentAttr = assignmentAttr;
	}

	public List<AssignmentAttributes> getNonCateAssignmentAttr() {
		return nonCateAssignmentAttr;
	}

	public void setNonCateAssignmentAttr(
			List<AssignmentAttributes> nonCateAssignmentAttr) {
		this.nonCateAssignmentAttr = nonCateAssignmentAttr;
	}

	public List<BackTrackDateFormModel> getBackTrackDate() {
		return backTrackDate;
	}

	public void setBackTrackDate(List<BackTrackDateFormModel> backTrackDate) {
		this.backTrackDate = backTrackDate;
	}
	
	
	
}
