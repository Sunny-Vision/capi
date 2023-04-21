package capi.model.report;

import java.util.List;

public class IndividualQuotationRecordCriteria {

	private String referenceMonth;
	
	private List<Integer> purpose;	
	
	private List<Integer> unitId;
	
	private List<Integer> assignmentIds;
	
	private List<Integer> quotationId;
	
	private List<Integer> quotationRecordId;
	
	private String contextPath;
	
	private Boolean isgetImage;

	public void setIsgetImage(Boolean isgetImage) {
		this.isgetImage = isgetImage;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getContextPath() {
		return contextPath;
	}
	public Boolean getIsgetImage() {
		return isgetImage;
	}
	
	public List<Integer> getPurpose() {
		return purpose;
	}

	public void setPurpose(List<Integer> purpose) {
		this.purpose = purpose;
	}

	public List<Integer> getUnitId() {
		return unitId;
	}

	public void setUnitId(List<Integer> unitId) {
		this.unitId = unitId;
	}

	public List<Integer> getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(List<Integer> quotationId) {
		this.quotationId = quotationId;
	}

	public List<Integer> getAssignmentIds() {
		return assignmentIds;
	}

	public List<Integer> getQuotationRecordId() {
		return quotationRecordId;
	}

	public void setQuotationRecordId(List<Integer> quotationRecordId) {
		this.quotationRecordId = quotationRecordId;
	}

	public void setAssignmentIds(List<Integer> assignmentIds) {
		this.assignmentIds = assignmentIds;
	}

	
}
