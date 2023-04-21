package capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class SelectedSessionModel implements Serializable{
	private Integer id;
	private Long selectedAssignments;
	private Long selectedQuotations;
	private Double actualReleaseManDays;
	private BigDecimal actualReleaseManDaysBD;
	private List<SelectedAssignmentModel> assignments;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public List<SelectedAssignmentModel> getAssignments() {
		return assignments;
	}
	public void setAssignments(List<SelectedAssignmentModel> assignments) {
		this.assignments = assignments;
	}
	public BigDecimal getActualReleaseManDaysBD() {
		return actualReleaseManDaysBD;
	}
	public void setActualReleaseManDaysBD(BigDecimal actualReleaseManDaysBD) {
		this.actualReleaseManDaysBD = actualReleaseManDaysBD;
	}
}
