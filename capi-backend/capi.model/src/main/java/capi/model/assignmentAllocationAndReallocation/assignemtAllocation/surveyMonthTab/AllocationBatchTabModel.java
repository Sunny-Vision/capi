package capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab;

import java.io.Serializable;

public class AllocationBatchTabModel implements Serializable{
	
	private String referenceMonthStr;
	private Integer allocationBatchId;
//	private AllocationBatch ab;
	
	public String getReferenceMonthStr() {
		return referenceMonthStr;
	}
	public void setReferenceMonthStr(String referenceMonthStr) {
		this.referenceMonthStr = referenceMonthStr;
	}
	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}
	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}
//	public AllocationBatch getAb() {
//		return ab;
//	}
//	public void setAb(AllocationBatch ab) {
//		this.ab = ab;
//	}
	
	
}