package capi.model.assignmentManagement;

import java.io.Serializable;

public class QuotationRecordMaintenanceFilter implements Serializable{
	private String referenceMonth;
	private Integer purposeId;
	
	public String getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public Integer getPurposeId() {
		return purposeId;
	}
	public void setPurposeId(Integer purposeId) {
		this.purposeId = purposeId;
	}
}
