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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@Entity
@Table(name="FieldworkTimeLog")
public class FieldworkTimeLog extends EntityBase{

	@Id
	@GeneratedValue(strategy =  javax.persistence.GenerationType.AUTO)
	@Column(name="FieldworkTimeLogId")
	private Integer fieldworkTimeLogId;
	
	@Column(name="ReferenceMonth")
	private Date referenceMonth;
	
	@Temporal(TemporalType.TIME)
	@Column(name="StartTime")
	private Date startTime;
	
	@Column(name="Survey")
	private String survey;
	
	@Column(name="CaseReferenceNo")
	private String caseReferenceNo;
	
	@Column(name="Activity")
	private String activity;
	
	@Column(name="EnumerationOutcome")
	private String enumerationOutcome;
	
	@Column(name="Building")
	private Integer building;
	
	@Column(name="Destination")
	private String destination;
	
	@Column(name="Transport")
	private String transport;
	
	@Column(name="Remark")
	private String remark;
	
	@Column(name="Expenses")
	private Double expenses;
	
	@Temporal(TemporalType.TIME)
	@Column(name="EndTime")
	private Date endTime;
	
	@Column(name="TotalQuotation")
	private Integer totalQuotation;
	
	@Column(name="QuotationCount")
	private Integer quotationCount;
	
	@Deprecated
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AssignmentId", nullable = true)
	private Assignment assignment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TimeLogId", nullable = true)
	private TimeLog timeLog;
	
	@Column(name="RecordType")
	private Integer recordType;
	
	@Column(name="FromLocation")
	private String fromLocation;
	
	@Column(name="ToLocation")
	private String toLocation;
	
	@Column(name="IncludeInTransportForm")
	private boolean includeInTransportForm;
	
	@Column(name="Transit")
	private boolean transit;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return getFieldworkTimeLogId();
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
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

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getEnumerationOutcome() {
		return enumerationOutcome;
	}

	public void setEnumerationOutcome(String enumerationOutcome) {
		this.enumerationOutcome = enumerationOutcome;
	}

	public Integer getBuilding() {
		return building;
	}

	public void setBuilding(Integer building) {
		this.building = building;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getExpenses() {
		return expenses;
	}

	public void setExpenses(Double expenses) {
		this.expenses = expenses;
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

	public Integer getFieldworkTimeLogId() {
		return fieldworkTimeLogId;
	}

	public void setFieldworkTimeLogId(Integer fieldworkTimeLogId) {
		this.fieldworkTimeLogId = fieldworkTimeLogId;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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

	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public boolean isIncludeInTransportForm() {
		return includeInTransportForm;
	}

	public void setIncludeInTransportForm(boolean includeInTransportForm) {
		this.includeInTransportForm = includeInTransportForm;
	}

	public boolean isTransit() {
		return transit;
	}

	public void setTransit(boolean transit) {
		this.transit = transit;
	}

}
