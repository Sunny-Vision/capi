package capi.model.report;

import java.util.List;

public class FieldworkOutputByOutletTypeCriteria {
	private List<Integer> officerId;
	
	private List<Integer> purposeId;
	
	private String refMonth;
	
	private List<Integer> allocationBatchId;
	
	private List<String> outletTypeShortCode;

	private Integer userId;
	
	private Integer authorityLevel;
	
	public List<Integer> getOfficerId() {
		return officerId;
	}

	public void setOfficerId(List<Integer> officerId) {
		this.officerId = officerId;
	}

	public List<Integer> getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(List<Integer> purposeId) {
		this.purposeId = purposeId;
	}

	public String getRefMonth() {
		return refMonth;
	}

	public void setRefMonth(String refMonth) {
		this.refMonth = refMonth;
	}

	public List<Integer> getAllocationBatchId() {
		return allocationBatchId;
	}

	public void setAllocationBatchId(List<Integer> allocationBatchId) {
		this.allocationBatchId = allocationBatchId;
	}

	public List<String> getOutletTypeShortCode() {
		return outletTypeShortCode;
	}

	public void setOutletTypeShortCode(List<String> outletTypeShortCode) {
		this.outletTypeShortCode = outletTypeShortCode;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAuthorityLevel() {
		return authorityLevel;
	}

	public void setAuthorityLevel(Integer authorityLevel) {
		this.authorityLevel = authorityLevel;
	}
	
}
