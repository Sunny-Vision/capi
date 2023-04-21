package capi.model.quotationRecordVerificationApproval;

import java.util.Date;
import java.util.List;

public class QuotationRecordVerificationApprovalFilterModel {
	private List<Integer> unitId;
	private Integer outletId;
	private Integer purposeId;
	private Integer subgroupId;
	private Integer indoorUserId;
	private String search;
	
	private Date refMonth;
	
	private Integer isVerify;
	
	public List<Integer> getUnitId() {
		return unitId;
	}
	public void setUnitId(List<Integer> unitId) {
		this.unitId = unitId;
	}
	public Integer getOutletId() {
		return outletId;
	}
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
	public Integer getPurposeId() {
		return purposeId;
	}
	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}
	public Integer getSubgroupId() {
		return subgroupId;
	}
	public void setSubgroupId(Integer subgroupId) {
		this.subgroupId = subgroupId;
	}
	public Integer getIndoorUserId() {
		return indoorUserId;
	}
	public void setIndoorUserId(Integer indoorUserId) {
		this.indoorUserId = indoorUserId;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public Date getRefMonth() {
		return refMonth;
	}
	public void setRefMonth(Date refMonth) {
		this.refMonth = refMonth;
	}
	public Integer getIsVerify() {
		return isVerify;
	}
	public void setIsVerify(Integer isVerify) {
		this.isVerify = isVerify;
	}
	
}
