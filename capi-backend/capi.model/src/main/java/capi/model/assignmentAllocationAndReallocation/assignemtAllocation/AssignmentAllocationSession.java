package capi.model.assignmentAllocationAndReallocation.assignemtAllocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.AdjustmentModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.DistrictHeadRowModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.districtHeadTab.DistrictHeadTabModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab.AllocationBatchTabModel;

public class AssignmentAllocationSession implements Serializable{
	private Boolean readonly = false;
	
	private AllocationBatchTabModel sessionAllocationBatchTabModel;
	
	private List<DistrictHeadTabModel> sessionDistrictHeadTabModel;
	
	private List<DistrictHeadRowModel> sessionDistrictHeadRows;
	
	private List<AdjustmentModel> sessionAdjustmentModels;
	
	public AssignmentAllocationSession(){
		sessionDistrictHeadTabModel = new ArrayList<DistrictHeadTabModel>();
	}

	public Boolean getReadonly() {
		return readonly;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public AllocationBatchTabModel getSessionAllocationBatchTabModel() {
		return sessionAllocationBatchTabModel;
	}

	public void setSessionAllocationBatchTabModel(
			AllocationBatchTabModel allocationBatchTabModel) {
		this.sessionAllocationBatchTabModel = allocationBatchTabModel;
	}

	public List<DistrictHeadTabModel> getSessionDistrictHeadTabModel() {
		return sessionDistrictHeadTabModel;
	}

	public void setSessionDistrictHeadTabModel(
			List<DistrictHeadTabModel> sessionDistrictHeadTabModel) {
		this.sessionDistrictHeadTabModel = sessionDistrictHeadTabModel;
	}

	public List<DistrictHeadRowModel> getSessionDistrictHeadRows() {
		return sessionDistrictHeadRows;
	}

	public void setSessionDistrictHeadRows(
			List<DistrictHeadRowModel> sessionDistrictHeadRows) {
		this.sessionDistrictHeadRows = sessionDistrictHeadRows;
	}

	public List<AdjustmentModel> getSessionAdjustmentModels() {
		return sessionAdjustmentModels;
	}

	public void setSessionAdjustmentModels(
			List<AdjustmentModel> sessionAdjustmentModels) {
		this.sessionAdjustmentModels = sessionAdjustmentModels;
	}

	
	
}
