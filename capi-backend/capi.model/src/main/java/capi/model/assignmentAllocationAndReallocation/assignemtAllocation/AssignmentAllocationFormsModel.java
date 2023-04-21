package capi.model.assignmentAllocationAndReallocation.assignemtAllocation;

import java.util.ArrayList;
import java.util.List;

import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.AdjustmentModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.DistrictHeadRowModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.districtHeadTab.DistrictHeadTabModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab.AllocationBatchTabModel;

public class AssignmentAllocationFormsModel {
	private AllocationBatchTabModel allocationBatchTabModel;
	
	private List<DistrictHeadTabModel> districtHeadTabModel; 
	
	private List<DistrictHeadRowModel> districtHead;
	
	private List<AdjustmentModel> adjustment;
	
	
	public AssignmentAllocationFormsModel(){
		this.allocationBatchTabModel = new AllocationBatchTabModel();
		this.districtHeadTabModel = new ArrayList<DistrictHeadTabModel>();
	}

	public AllocationBatchTabModel getAllocationBatchTabModel() {
		return allocationBatchTabModel;
	}

	public void setAllocationBatchTabModel(
			AllocationBatchTabModel allocationBatchTabModel) {
		this.allocationBatchTabModel = allocationBatchTabModel;
	}

	public List<DistrictHeadTabModel> getDistrictHeadTabModel() {
		return districtHeadTabModel;
	}

	public void setDistrictHeadTabModel(
			List<DistrictHeadTabModel> districtHeadTabModel) {
		this.districtHeadTabModel = districtHeadTabModel;
	}

	public List<DistrictHeadRowModel> getDistrictHead() {
		return districtHead;
	}

	public void setDistrictHead(List<DistrictHeadRowModel> districtHead) {
		this.districtHead = districtHead;
	}

	public List<AdjustmentModel> getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(List<AdjustmentModel> adjustment) {
		this.adjustment = adjustment;
	}

}
