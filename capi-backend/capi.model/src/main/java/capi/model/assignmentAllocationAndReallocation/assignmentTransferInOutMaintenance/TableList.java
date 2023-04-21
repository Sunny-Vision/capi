package capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance;

import java.math.BigDecimal;

public class TableList {
	private Integer id;
	private String fromFieldOfficer;
	private String targetFieldOfficer;
	private Double targetReleaseManDay;
	private Double actualReleaseManDay;
	private BigDecimal actualReleaseManDayBD;
	private String actualReleaseManDayString;
	private String status;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFromFieldOfficer() {
		return fromFieldOfficer;
	}
	public void setFromFieldOfficer(String fromFieldOfficer) {
		this.fromFieldOfficer = fromFieldOfficer;
	}
	public String getTargetFieldOfficer() {
		return targetFieldOfficer;
	}
	public void setTargetFieldOfficer(String targetFieldOfficer) {
		this.targetFieldOfficer = targetFieldOfficer;
	}
	public Double getTargetReleaseManDay() {
		return targetReleaseManDay;
	}
	public void setTargetReleaseManDay(Double targetReleaseManDay) {
		this.targetReleaseManDay = targetReleaseManDay;
	}
	public Double getActualReleaseManDay() {
		return actualReleaseManDay;
	}
	public void setActualReleaseManDay(Double actualReleaseManDay) {
		this.actualReleaseManDay = actualReleaseManDay;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getActualReleaseManDayBD() {
		return actualReleaseManDayBD;
	}
	public void setActualReleaseManDayBD(BigDecimal actualReleaseManDayBD) {
		this.actualReleaseManDayBD = actualReleaseManDayBD;
	}
	public String getActualReleaseManDayString() {
		return actualReleaseManDayString;
	}
	public void setActualReleaseManDayString(String actualReleaseManDayString) {
		this.actualReleaseManDayString = actualReleaseManDayString;
	}
}
