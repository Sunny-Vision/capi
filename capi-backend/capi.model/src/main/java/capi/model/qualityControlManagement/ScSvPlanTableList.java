package capi.model.qualityControlManagement;

import java.util.Date;

public class ScSvPlanTableList {
	
	private Integer scSvPlanId;
	
	private String visitDate;
	
	private Date visitDate2;
	
	private String staffCode;
	
	private String chineseName;
	
	private String englishName;
	
	private String team;
	
	private String supervisor;
	
	private String checker;
	
	private String qcType;
	
	private boolean isMandatoryPlan;
	
	private boolean deletable;
	
	public Integer getId() {
		return scSvPlanId;
	}
	
	public Integer getScSvPlanId() {
		return scSvPlanId;
	}

	public void setScSvPlanId(Integer scSvPlanId) {
		this.scSvPlanId = scSvPlanId;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public Date getVisitDate2() {
		return visitDate2;
	}

	public void setVisitDate2(Date visitDate2) {
		this.visitDate2 = visitDate2;
	}

	public String getStaffCode() {
		return staffCode;
	}
	
	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}
	
	public String getChineseName() {
		return chineseName;
	}
	
	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}
	
	public String getEnglishName() {
		return englishName;
	}
	
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	
	public String getTeam() {
		return team;
	}
	
	public void setTeam(String team) {
		this.team = team;
	}
	
	public String getSupervisor() {
		return supervisor;
	}
	
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getQcType() {
		return qcType;
	}

	public void setQcType(String qcType) {
		this.qcType = qcType;
	}

	public boolean isMandatoryPlan() {
		return isMandatoryPlan;
	}

	public void setMandatoryPlan(boolean isMandatoryPlan) {
		this.isMandatoryPlan = isMandatoryPlan;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

}
