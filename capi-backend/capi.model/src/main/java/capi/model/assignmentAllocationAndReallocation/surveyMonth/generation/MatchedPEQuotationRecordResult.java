package capi.model.assignmentAllocationAndReallocation.surveyMonth.generation;

public class MatchedPEQuotationRecordResult {

	private Integer quotationId;
	
//	private Integer indoorQuotationRecordId;
	
//	private Integer unitId;
	
	private Integer outletId;
	
	private Long matched; 
	
	private Long total;

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

//	public Integer getUnitId() {
//		return unitId;
//	}
//
//	public void setUnitId(Integer unitId) {
//		this.unitId = unitId;
//	}

	public Long getMatched() {
		return matched;
	}

	public void setMatched(Long matched) {
		this.matched = matched;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}
}
