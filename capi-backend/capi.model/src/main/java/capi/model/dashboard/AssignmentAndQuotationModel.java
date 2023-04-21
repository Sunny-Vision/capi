package capi.model.dashboard;

import java.util.List;

public class AssignmentAndQuotationModel extends BaseSectionModel {
	private List<DeadlineRowModel> deadlines;
	private Long verification;
	private Long revisit;
	
	public List<DeadlineRowModel> getDeadlines() {
		return deadlines;
	}
	public void setDeadlines(List<DeadlineRowModel> deadlines) {
		this.deadlines = deadlines;
	}
	public Long getVerification() {
		return verification;
	}
	public void setVerification(Long verification) {
		this.verification = verification;
	}
	public Long getRevisit() {
		return revisit;
	}
	public void setRevisit(Long revisit) {
		this.revisit = revisit;
	}
}
