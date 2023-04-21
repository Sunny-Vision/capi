package capi.model.timeLogManagement;

public class FieldworkTimeLogModel implements Comparable<FieldworkTimeLogModel>{


	private Integer fieldworkTimeLogId;
	private String referenceMonth;
	private String startTime;
	private String startTimeHr;
	private String startTimeMin;
	private String endTime;
	
	private String survey;
	private String caseReferenceNo;
	private String activity;
	private String enumerationOutcome;
	private Integer marketQuotationCount;
	private Integer marketTotalQuotation;
	private Integer nonMarketQuotationCount;
	private Integer nonMarketTotalQuotation;
	private Integer building;
	private String destination;
	private String transport;
	private String remark;
	private Double expenses;
	private Integer recordType;
	private Boolean existReference;
	private String fromLocation;
	private String toLocation;
	private boolean includeInTransportForm;
	private boolean transit;

	private Integer assignmentId;

	public Integer getFieldworkTimeLogId() {
		return fieldworkTimeLogId;
	}

	public void setFieldworkTimeLogId(Integer fieldworkTimeLogId) {
		this.fieldworkTimeLogId = fieldworkTimeLogId;
	}

	public String getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(String referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartTimeHr() {
		return startTimeHr;
	}

	public void setStartTimeHr(String startTimeHr) {
		this.startTimeHr = startTimeHr;
	}

	public String getStartTimeMin() {
		return startTimeMin;
	}

	public void setStartTimeMin(String startTimeMin) {
		this.startTimeMin = startTimeMin;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
	
	public Integer getMarketQuotationCount() {
		return marketQuotationCount;
	}

	public void setMarketQuotationCount(Integer marketQuotationCount) {
		this.marketQuotationCount = marketQuotationCount;
	}

	public Integer getMarketTotalQuotation() {
		return marketTotalQuotation;
	}

	public void setMarketTotalQuotation(Integer marketTotalQuotation) {
		this.marketTotalQuotation = marketTotalQuotation;
	}

	public Integer getNonMarketQuotationCount() {
		return nonMarketQuotationCount;
	}

	public void setNonMarketQuotationCount(Integer nonMarketQuotationCount) {
		this.nonMarketQuotationCount = nonMarketQuotationCount;
	}

	public Integer getNonMarketTotalQuotation() {
		return nonMarketTotalQuotation;
	}

	public void setNonMarketTotalQuotation(Integer nonMarketTotalQuotation) {
		this.nonMarketTotalQuotation = nonMarketTotalQuotation;
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

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	public Boolean getExistReference() {
		return existReference;
	}

	public void setExistReference(Boolean existReference) {
		this.existReference = existReference;
	}
	
	@Override
    public int compareTo(FieldworkTimeLogModel another) {
        return ((this.getStartTime() == null ? "" : this.getStartTime()) + this.getStartTime().toString()).compareTo(((another.getStartTime() == null ? "" : another.getStartTime()) +another.getStartTime().toString()));

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
