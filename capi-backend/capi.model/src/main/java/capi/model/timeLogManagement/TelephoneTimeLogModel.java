package capi.model.timeLogManagement;

import java.util.Comparator;

public class TelephoneTimeLogModel implements Comparator<TelephoneTimeLogModel>{	
	private Integer telephoneTimeLogId;
	private String referenceMonth;
	private String survey;
	private String caseReferenceNo;
	private String status;
	private Integer completionQuotationCount;
	private Integer completionTotalQuotation;
	private Integer deletionQuotationCount;
	private Integer deletionTotalQuotation;
	private String session;
	private Boolean existReference;
	private Integer assignmentId;
	
	public Integer getTelephoneTimeLogId() {
		return telephoneTimeLogId;
	}
	public void setTelephoneTimeLogId(Integer telephoneTimeLogId) {
		this.telephoneTimeLogId = telephoneTimeLogId;
	}
	public String getReferenceMonth() {
		return referenceMonth;
	}
	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}
	public String getSurvey() {
		return survey;
	}
	public void setSurvey(String survey) {
		this.survey = survey;
	}
	public String getCaseReferenceNo() {
		return caseReferenceNo;
	}
	public void setCaseReferenceNo(String caseReferenceNo) {
		this.caseReferenceNo = caseReferenceNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	
	public Integer getCompletionQuotationCount() {
		return completionQuotationCount;
	}
	public void setCompletionQuotationCount(Integer completionQuotationCount) {
		this.completionQuotationCount = completionQuotationCount;
	}
	public Integer getCompletionTotalQuotation() {
		return completionTotalQuotation;
	}
	public void setCompletionTotalQuotation(Integer completionTotalQuotation) {
		this.completionTotalQuotation = completionTotalQuotation;
	}
	public Integer getDeletionQuotationCount() {
		return deletionQuotationCount;
	}
	public void setDeletionQuotationCount(Integer deletionQuotationCount) {
		this.deletionQuotationCount = deletionQuotationCount;
	}
	public Integer getDeletionTotalQuotation() {
		return deletionTotalQuotation;
	}
	public void setDeletionTotalQuotation(Integer deletionTotalQuotation) {
		this.deletionTotalQuotation = deletionTotalQuotation;
	}
	public Boolean getExistReference() {
		return existReference;
	}
	public void setExistReference(Boolean existReference) {
		this.existReference = existReference;
	}
	public Integer getTotalQuotation() {
		if (status == "") {
			return completionTotalQuotation;
		} else {
			return deletionTotalQuotation;
		}
	}
	public Integer getQuotationCount() {
		if (status == "") {
			return completionQuotationCount;
		} else {
			return deletionQuotationCount;
		}
		
	}
	public Integer getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	@Override
	public int compare(TelephoneTimeLogModel o1, TelephoneTimeLogModel o2) {
        String s1 = ((TelephoneTimeLogModel) o1).getSurvey();
        String s2 = ((TelephoneTimeLogModel) o2).getSurvey();
        int stringComp = s1.compareTo(s2);

        if (stringComp != 0) {
           return stringComp;
        } else {
           String c1 = ((TelephoneTimeLogModel) o1).getCaseReferenceNo();
           String c2 = ((TelephoneTimeLogModel) o2).getCaseReferenceNo();
           return c1.compareTo(c2);
        }
	}
	

}
