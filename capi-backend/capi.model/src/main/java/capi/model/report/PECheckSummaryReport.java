package capi.model.report;

public class PECheckSummaryReport {

	private String team;
	
	private Integer contactDate;
	
	private Integer contactTime;
	
	private Integer contactDuration;
	
	private Integer contactMode;
	
	private Integer dateCollected;
	
	private Integer others;
	
	private Integer allInformationInOrder;
	
	private Integer officerId;
	
	private String officerCode;
	
	private String officerName;
	
	private Integer noOfFirmsChecked;
	
	private Integer noOfFirmsNC;
	
	private Integer totalFirmEnumerated;
	
	private String pctChecked;
	
	private String remarks;

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Integer getContactDate() {
		return contactDate;
	}

	public void setContactDate(Integer contactDate) {
		this.contactDate = contactDate;
	}

	public Integer getContactTime() {
		return contactTime;
	}

	public void setContactTime(Integer contactTime) {
		this.contactTime = contactTime;
	}

	public Integer getContactDuration() {
		return contactDuration;
	}

	public void setContactDuration(Integer contactDuration) {
		this.contactDuration = contactDuration;
	}

	public Integer getContactMode() {
		return contactMode;
	}

	public void setContactMode(Integer contactMode) {
		this.contactMode = contactMode;
	}

	public Integer getDateCollected() {
		return dateCollected;
	}

	public void setDateCollected(Integer dateCollected) {
		this.dateCollected = dateCollected;
	}

	public Integer getOthers() {
		return others;
	}

	public void setOthers(Integer others) {
		this.others = others;
	}

	public Integer getAllInformationInOrder() {
		return allInformationInOrder;
	}

	public void setAllInformationInOrder(Integer allInformationInOrder) {
		this.allInformationInOrder = allInformationInOrder;
	}

	public String getOfficerCode() {
		return officerCode;
	}

	public Integer getOfficerId() {
		return officerId;
	}

	public void setOfficerId(Integer officerId) {
		this.officerId = officerId;
	}

	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}

	public String getOfficerName() {
		return officerName;
	}

	public void setOffcierName(String offcierName) {
		this.officerName = offcierName;
	}

	public Integer getNoOfFirmsChecked() {
		return noOfFirmsChecked;
	}

	public void setNoOfFirmsChecked(Integer noOfFirmsChecked) {
		this.noOfFirmsChecked = noOfFirmsChecked;
	}

	public Integer getNoOfFirmsNC() {
		return noOfFirmsNC;
	}

	public void setNoOfFirmsNC(Integer noOfFirmsNC) {
		this.noOfFirmsNC = noOfFirmsNC;
	}

	public Integer getTotalFirmEnumerated() {
		return totalFirmEnumerated;
	}

	public void setTotalFirmEnumerated(Integer totalFirmEnumerated) {
		this.totalFirmEnumerated = totalFirmEnumerated;
	}

	public String getPctChecked() {
		return pctChecked;
	}

	public void setPctChecked(String pctChecked) {
		this.pctChecked = pctChecked;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
