package capi.model.assignmentAllocationAndReallocation.surveyMonth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AllocationBatch;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.AssignmentAttributes;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.session.SurveyMonth;

public class SurveyMonthSession implements Serializable{
	
	private SurveyMonth sessionSurveyMonth;
	
	private List<AllocationBatch> sessionAllocationBatch;
	
	private List<AllocationBatch> sessionNewAllocationBatch;
	
	private List<AssignmentAttributes> sessionNewAssignmentAttr;
	
	private List<AssignmentAttributes> sessionAssignmentAttr;
	
	private List<AssignmentAttributes> sessionNonCateAssignmentAttr;
	
	private List<BackTrackDateDisplayModel> backTrackDateModelList;
	
	private List<Date> availableWorkingDateList;
	
	private Boolean readonly = false;
	
	private Boolean isDraft;
	
	public SurveyMonthSession(){
		this.sessionSurveyMonth = new SurveyMonth();
		this.sessionAllocationBatch = new ArrayList<AllocationBatch>();
		this.sessionNewAllocationBatch = new ArrayList<AllocationBatch>();
		this.sessionNewAssignmentAttr = new ArrayList<AssignmentAttributes>();
		this.sessionAssignmentAttr = new ArrayList<AssignmentAttributes>();
		this.sessionNonCateAssignmentAttr = new ArrayList<AssignmentAttributes>();
		this.backTrackDateModelList = new ArrayList<BackTrackDateDisplayModel>();
		this.availableWorkingDateList = new ArrayList<Date>();
		this.isDraft = true;
	}

	public SurveyMonth getSessionSurveyMonth() {
		return sessionSurveyMonth;
	}

	public void setSessionSurveyMonth(SurveyMonth sessionSurveyMonth) {
		this.sessionSurveyMonth = sessionSurveyMonth;
	}

	public List<AllocationBatch> getSessionAllocationBatch() {
		return sessionAllocationBatch;
	}

	public void setSessionAllocationBatch(
			List<AllocationBatch> sessionAllocationBatch) {
		this.sessionAllocationBatch = sessionAllocationBatch;
	}

	public List<AllocationBatch> getSessionNewAllocationBatch() {
		return sessionNewAllocationBatch;
	}

	public void setSessionNewAllocationBatch(
			List<AllocationBatch> sessionNewAllocationBatch) {
		this.sessionNewAllocationBatch = sessionNewAllocationBatch;
	}

	public List<AssignmentAttributes> getSessionNewAssignmentAttr() {
		return sessionNewAssignmentAttr;
	}

	public void setSessionNewAssignmentAttr(
			List<AssignmentAttributes> sessionNewAssignmentAttr) {
		this.sessionNewAssignmentAttr = sessionNewAssignmentAttr;
	}

	public List<AssignmentAttributes> getSessionAssignmentAttr() {
		return sessionAssignmentAttr;
	}

	public void setSessionAssignmentAttr(
			List<AssignmentAttributes> sessionAssignmentAttr) {
		this.sessionAssignmentAttr = sessionAssignmentAttr;
	}

	public List<AssignmentAttributes> getSessionNonCateAssignmentAttr() {
		return sessionNonCateAssignmentAttr;
	}

	public void setSessionNonCateAssignmentAttr(
			List<AssignmentAttributes> sessionNonCateAssignmentAttr) {
		this.sessionNonCateAssignmentAttr = sessionNonCateAssignmentAttr;
	}

	public List<BackTrackDateDisplayModel> getBackTrackDateModelList() {
		return backTrackDateModelList;
	}

	public void setBackTrackDateModelList(
			List<BackTrackDateDisplayModel> backTrackDateModelList) {
		this.backTrackDateModelList = backTrackDateModelList;
	}

	public List<Date> getAvailableWorkingDateList() {
		return availableWorkingDateList;
	}

	public void setAvailableWorkingDateList(List<Date> availableWorkingDateList) {
		this.availableWorkingDateList = availableWorkingDateList;
	}

	public Boolean getReadonly() {
		return readonly;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public Boolean getIsDraft() {
		return isDraft;
	}

	public void setIsDraft(Boolean isDraft) {
		this.isDraft = isDraft;
	}

}
