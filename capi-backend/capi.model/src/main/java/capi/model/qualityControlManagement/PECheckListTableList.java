package capi.model.qualityControlManagement;

public class PECheckListTableList {

	private Integer userId;

	private String officerCode;

	private String officerName;
	
	private String team;

	private Long total;

	private Long checked;

	private Long selected;
	
	private Long excluded;
	
	private Long approved;

	private Long nonContact;
	
	private Integer surveyMonthId;
	
	private Integer isFieldTeamHead;
	
	private Integer isSectionHead;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getOfficerCode() {
		return officerCode;
	}

	public void setOfficerCode(String officerCode) {
		this.officerCode = officerCode;
	}

	public String getOfficerName() {
		return officerName;
	}

	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getChecked() {
		return checked;
	}

	public void setChecked(Long checked) {
		this.checked = checked;
	}

	public Long getSelected() {
		return selected;
	}

	public void setSelected(Long selected) {
		this.selected = selected;
	}

	public Long getExcluded() {
		return excluded;
	}

	public void setExcluded(Long excluded) {
		this.excluded = excluded;
	}

	public Long getApproved() {
		return approved;
	}

	public void setApproved(Long approved) {
		this.approved = approved;
	}

	public Long getNonContact() {
		return nonContact;
	}

	public void setNonContact(Long nonContact) {
		this.nonContact = nonContact;
	}

	public Integer getSurveyMonthId() {
		return surveyMonthId;
	}

	public void setSurveyMonthId(Integer surveyMonthId) {
		this.surveyMonthId = surveyMonthId;
	}

	public Integer getIsFieldTeamHead() {
		return isFieldTeamHead;
	}

	public void setIsFieldTeamHead(Integer isFieldTeamHead) {
		this.isFieldTeamHead = isFieldTeamHead;
	}

	public Integer getIsSectionHead() {
		return isSectionHead;
	}

	public void setIsSectionHead(Integer isSectionHead) {
		this.isSectionHead = isSectionHead;
	}

}
