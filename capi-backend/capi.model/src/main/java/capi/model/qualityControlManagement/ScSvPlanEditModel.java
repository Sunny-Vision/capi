package capi.model.qualityControlManagement;


public class ScSvPlanEditModel {
	
	private Integer scSvPlanId;
	
	private Integer userId;
	
	private Integer checkerId;
	
	private String userNameDisplay;
	
	private String checkerNameDisplay;

	private String visitDate;
	
	private Integer qcType;
	
	private String modifiedDate;
	
	private String createdDate;
	
	public Integer getId() {
		return scSvPlanId;
	}

	public Integer getScSvPlanId() {
		return scSvPlanId;
	}

	public void setScSvPlanId(Integer scSvPlanId) {
		this.scSvPlanId = scSvPlanId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getCheckerId() {
		return checkerId;
	}

	public void setCheckerId(Integer checkerId) {
		this.checkerId = checkerId;
	}

	public String getUserNameDisplay() {
		return userNameDisplay;
	}

	public void setUserNameDisplay(String userNameDisplay) {
		this.userNameDisplay = userNameDisplay;
	}

	public String getCheckerNameDisplay() {
		return checkerNameDisplay;
	}

	public void setCheckerNameDisplay(String checkerNameDisplay) {
		this.checkerNameDisplay = checkerNameDisplay;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public Integer getQcType() {
		return qcType;
	}

	public void setQcType(Integer qcType) {
		this.qcType = qcType;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	
}
