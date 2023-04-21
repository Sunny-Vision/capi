package capi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="TelephoneTimeLog")
public class TelephoneTimeLog extends EntityBase{
	
	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="TelephoneTimeLogId")
	private Integer telephoneTimeLogId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;

	@Column(name="Survey")
	private String survey;
	
	@Column(name="CaseReferenceNo")
	private String caseReferenceNo;
	
	@Column(name="Status")
	private String status;
	
	@Column(name="Session")
	private String session;
	
	@Deprecated
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignmentId", nullable = true)
	private Assignment assignment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TimeLogId", nullable = true)
	private TimeLog timeLog;
	
	@Column(name="TotalQuotation")
	private Integer totalQuotation;
	
	@Column(name="QuotationCount")
	private Integer quotationCount;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getTelephoneTimeLogId();
	}

	public Integer getTelephoneTimeLogId() {
		return telephoneTimeLogId;
	}

	public void setTelephoneTimeLogId(Integer telephoneTimeLogId) {
		this.telephoneTimeLogId = telephoneTimeLogId;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
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
	@Deprecated
	public Assignment getAssignment() {
		return assignment;
	}
	@Deprecated
	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public TimeLog getTimeLog() {
		return timeLog;
	}

	public void setTimeLog(TimeLog timeLog) {
		this.timeLog = timeLog;
	}

	public Integer getTotalQuotation() {
		return totalQuotation;
	}

	public void setTotalQuotation(Integer totalQuotation) {
		this.totalQuotation = totalQuotation;
	}

	public Integer getQuotationCount() {
		return quotationCount;
	}

	public void setQuotationCount(Integer quotationCount) {
		this.quotationCount = quotationCount;
	}

}
