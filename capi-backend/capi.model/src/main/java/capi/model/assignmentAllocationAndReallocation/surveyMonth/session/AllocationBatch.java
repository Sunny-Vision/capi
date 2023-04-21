package capi.model.assignmentAllocationAndReallocation.surveyMonth.session;

import java.io.Serializable;
import java.util.Date;

public class AllocationBatch implements Serializable{
	private Integer id; 
	private String numberOfBatch;
	private String startDateStr;
	private String endDateStr;
	private Date startDate;
	private Date endDate;
	
	private Integer allocationBatchId;
	
	public AllocationBatch(){
		this.numberOfBatch = "";
		this.startDateStr = "";
		this.endDateStr = "";
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumberOfBatch() {
		return numberOfBatch;
	}
	
	public void setNumberOfBatch(String numberOfBatch) {
		this.numberOfBatch = numberOfBatch;
	}
	
	public String getStartDateStr() {
		return startDateStr;
	}
	
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}
	
	public String getEndDateStr() {
		return endDateStr;
	}
	
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getAllocationBatchId() {
		return allocationBatchId;
	}

	public void setAllocationBatchId(Integer allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}
}
