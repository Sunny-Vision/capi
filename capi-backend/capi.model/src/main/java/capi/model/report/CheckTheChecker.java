package capi.model.report;

public class CheckTheChecker {

	public static class OutletTypeMapping {

		private Integer peCheckTaskId;

		private String shortCode;

		public Integer getPeCheckTaskId() {
			return peCheckTaskId;
		}

		public void setPeCheckTaskId(Integer peCheckTaskId) {
			this.peCheckTaskId = peCheckTaskId;
		}

		public String getShortCode() {
			return shortCode;
		}

		public void setShortCode(String shortCode) {
			this.shortCode = shortCode;
		}

	}

	private Integer peCheckTaskId;

	private Integer peCheckFormId;

	private Integer assignmentId;

	private Integer firmNo;

	private String firmName;

	private String outletType;

	private String address;

	private String contactPerson;

	private String enumerationDate;

	private String enumerationBy;
	
	private String team;

	private String peCheckDate;

	private String peCheckBy;
	
	private Integer outletId;

	public Integer getPeCheckTaskId() {
		return peCheckTaskId;
	}

	public void setPeCheckTaskId(Integer peCheckTaskId) {
		this.peCheckTaskId = peCheckTaskId;
	}

	public Integer getPeCheckFormId() {
		return peCheckFormId;
	}

	public void setPeCheckFormId(Integer peCheckFormId) {
		this.peCheckFormId = peCheckFormId;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Integer getFirmNo() {
		return firmNo;
	}

	public void setFirmNo(Integer firmNo) {
		this.firmNo = firmNo;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getOutletType() {
		return outletType;
	}

	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getEnumerationDate() {
		return enumerationDate;
	}

	public void setEnumerationDate(String enumerationDate) {
		this.enumerationDate = enumerationDate;
	}

	public String getEnumerationBy() {
		return enumerationBy;
	}

	public void setEnumerationBy(String enumerationBy) {
		this.enumerationBy = enumerationBy;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getPeCheckDate() {
		return peCheckDate;
	}

	public void setPeCheckDate(String peCheckDate) {
		this.peCheckDate = peCheckDate;
	}

	public String getPeCheckBy() {
		return peCheckBy;
	}

	public void setPeCheckBy(String peCheckBy) {
		this.peCheckBy = peCheckBy;
	}
	
	public Integer getOutletId() {
		return outletId;
	}
	
	public void setOutletId(Integer outletId) {
		this.outletId = outletId;
	}

}
