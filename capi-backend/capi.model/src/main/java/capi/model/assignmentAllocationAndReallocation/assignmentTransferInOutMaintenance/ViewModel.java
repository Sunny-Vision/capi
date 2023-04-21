package capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance;

import java.math.BigDecimal;
import java.util.List;

import capi.entity.User;

public class ViewModel {
	private Integer id;
	private String remark;
	private String rejectReason;
	private Integer submitToApproveUserId;
	private String fromFieldOfficer;
	private String targetFieldOfficer;
	private Double manDay;
	private Long totalAssignments;
	private Long totalQuotations;
	private String status;
	private String preSelectSupervisorLabel;
	private Integer preSelectSupervisorId;
	private Long selectedAssignments;
	private Long selectedQuotations;
	private Double actualReleaseManDays;
	private BigDecimal actualReleaseManDaysBD;
	private Double resultantManDayBalance;
	private BigDecimal resultantManDayBalanceBD;
	private List<User> usersForSelection;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRejectReason() {
		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
	public Integer getSubmitToApproveUserId() {
		return submitToApproveUserId;
	}
	public void setSubmitToApproveUserId(Integer submitToApproveUserId) {
		this.submitToApproveUserId = submitToApproveUserId;
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
	public Double getManDay() {
		return manDay;
	}
	public void setManDay(Double manDay) {
		this.manDay = manDay;
	}
	public Long getTotalAssignments() {
		return totalAssignments;
	}
	public void setTotalAssignments(Long totalAssignments) {
		this.totalAssignments = totalAssignments;
	}
	public Long getTotalQuotations() {
		return totalQuotations;
	}
	public void setTotalQuotations(Long totalQuotations) {
		this.totalQuotations = totalQuotations;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPreSelectSupervisorLabel() {
		return preSelectSupervisorLabel;
	}
	public void setPreSelectSupervisorLabel(String preSelectSupervisorLabel) {
		this.preSelectSupervisorLabel = preSelectSupervisorLabel;
	}
	public Integer getPreSelectSupervisorId() {
		return preSelectSupervisorId;
	}
	public void setPreSelectSupervisorId(Integer preSelectSupervisorId) {
		this.preSelectSupervisorId = preSelectSupervisorId;
	}
	public Long getSelectedAssignments() {
		return selectedAssignments;
	}
	public void setSelectedAssignments(Long selectedAssignments) {
		this.selectedAssignments = selectedAssignments;
	}
	public Long getSelectedQuotations() {
		return selectedQuotations;
	}
	public void setSelectedQuotations(Long selectedQuotations) {
		this.selectedQuotations = selectedQuotations;
	}
	public Double getActualReleaseManDays() {
		return actualReleaseManDays;
	}
	public void setActualReleaseManDays(Double actualReleaseManDays) {
		this.actualReleaseManDays = actualReleaseManDays;
	}
	public Double getResultantManDayBalance() {
		return resultantManDayBalance;
	}
	public void setResultantManDayBalance(Double resultantManDayBalance) {
		this.resultantManDayBalance = resultantManDayBalance;
	}
	public List<User> getUsersForSelection() {
		return usersForSelection;
	}
	public void setUsersForSelection(List<User> usersForSelection) {
		this.usersForSelection = usersForSelection;
	}
	public BigDecimal getActualReleaseManDaysBD() {
		return actualReleaseManDaysBD;
	}
	public void setActualReleaseManDaysBD(BigDecimal actualReleaseManDaysBD) {
		this.actualReleaseManDaysBD = actualReleaseManDaysBD;
	}
	public BigDecimal getResultantManDayBalanceBD() {
		return resultantManDayBalanceBD;
	}
	public void setResultantManDayBalanceBD(BigDecimal resultantManDayBalanceBD) {
		this.resultantManDayBalanceBD = resultantManDayBalanceBD;
	}
}
