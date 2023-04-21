package capi.model.assignmentAllocationAndReallocation.assignemtAllocation;

import java.util.Date;

public class AssignmentAllocationListModel {
	private Integer allocationBatchId;
	private String batchName;
	private Date referenceMonth;
	private String referenceMonthStr;
	private Integer status;
	
	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}
	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public Date getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public String getReferenceMonthStr() {
		return referenceMonthStr;
	}
	public void setReferenceMonthStr(String referenceMonthStr) {
		this.referenceMonthStr = referenceMonthStr;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}
