package capi.model.dataImportExport;

import java.util.Date;

public class ExportAssignmentAllocationList {
	
	private Integer assignmentId;
		
	private String survey;
	
	private Date startDate;
	
	private Date endDate;
	
	private Date assignedCollectionDate;
	
	private Integer firmStatus;
	
	private boolean isCompleted;
	
	private boolean isImportedAssignment;
	
	//QuotationRecord
	private Long numOfQuotation;
	
	//Outlet
	private Integer outletId;
	
	private Integer firmCode;
	
	private String outletName;
	
//	private Set<VwOutletTypeShortForm> outletTypes = new HashSet<VwOutletTypeShortForm>();
	
	private String address;
	
	private Integer collectionMethod;
	
	private Integer outletMarketType;
	
	//District
	private String districtCode;
	
	//Tpu
	private String tpuCode;
	
	//User
	private String staffCode;

	//SurveyMonth
	private Date referenceMonth;
	
	//Quotation.Batch
	private String batchCode;
	
	private String referencefirmCode; 
	
	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getSurvey() {
		return survey;
	}

	public void setSurvey(String survey) {
		this.survey = survey;
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

	public Date getAssignedCollectionDate() {
		return assignedCollectionDate;
	}

	public void setAssignedCollectionDate(Date assignedCollectionDate) {
		this.assignedCollectionDate = assignedCollectionDate;
	}

	public Integer getFirmStatus() {
		return firmStatus;
	}

	public void setFirmStatus(Integer firmStatus) {
		this.firmStatus = firmStatus;
	}

	public Long getNumOfQuotation() {
		return numOfQuotation;
	}

	public void setNumOfQuotation(Long numOfQuotation) {
		this.numOfQuotation = numOfQuotation;
	}

	public Integer getFirmCode() {
		return firmCode;
	}

	public void setFirmCode(Integer firmCode) {
		this.firmCode = firmCode;
	}

	public String getOutletName() {
		return outletName;
	}

	public void setOutletName(String outletName) {
		this.outletName = outletName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getCollectionMethod() {
		return collectionMethod;
	}

	public void setCollectionMethod(Integer collectionMethod) {
		this.collectionMethod = collectionMethod;
	}

	public Integer getOutletMarketType() {
		return outletMarketType;
	}

	public void setOutletMarketType(Integer outletMarketType) {
		this.outletMarketType = outletMarketType;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getTpuCode() {
		return tpuCode;
	}

	public void setTpuCode(String tpuCode) {
		this.tpuCode = tpuCode;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public Date getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(Date referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean isImportedAssignment() {
		return isImportedAssignment;
	}

	public void setImportedAssignment(boolean isImportedAssignment) {
		this.isImportedAssignment = isImportedAssignment;
	}

	public Integer getOutletId() {
		return outletId;
	}

	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public String getReferencefirmCode() {
		return referencefirmCode;
	}

	public void setReferencefirmCode(String referencefirmCode) {
		this.referencefirmCode = referencefirmCode;
	}
	
}
