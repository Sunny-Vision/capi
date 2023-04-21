package capi.model.dashboard;

public class BaseSectionModel {
	private Long total;
	private Long count;
	private Double percent;
	
	private boolean showMRPS;
	private boolean showMetric;
	private boolean showProgress;
	private boolean showDeadlineTable;
	private boolean showDeadlineTableViewButton;
	private boolean showVerficationRevisitCount;
	private boolean showPendingApprovalList;
	private boolean showOutstandingQCAssignmentList;
	private boolean showRUAViewAllButton;
	private boolean showOutstandingViewAllButton;
	
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
	public boolean isShowMRPS() {
		return showMRPS;
	}
	public void setShowMRPS(boolean showMRPS) {
		this.showMRPS = showMRPS;
	}
	public boolean isShowMetric() {
		return showMetric;
	}
	public void setShowMetric(boolean showMetric) {
		this.showMetric = showMetric;
	}
	public boolean isShowProgress() {
		return showProgress;
	}
	public void setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
	}
	public boolean isShowDeadlineTable() {
		return showDeadlineTable;
	}
	public void setShowDeadlineTable(boolean showDeadlineTable) {
		this.showDeadlineTable = showDeadlineTable;
	}
	public boolean isShowDeadlineTableViewButton() {
		return showDeadlineTableViewButton;
	}
	public void setShowDeadlineTableViewButton(boolean showDeadlineTableViewButton) {
		this.showDeadlineTableViewButton = showDeadlineTableViewButton;
	}
	public boolean isShowVerficationRevisitCount() {
		return showVerficationRevisitCount;
	}
	public void setShowVerficationRevisitCount(boolean showVerficationRevisitCount) {
		this.showVerficationRevisitCount = showVerficationRevisitCount;
	}
	public boolean isShowPendingApprovalList() {
		return showPendingApprovalList;
	}
	public void setShowPendingApprovalList(boolean showPendingApprovalList) {
		this.showPendingApprovalList = showPendingApprovalList;
	}
	public boolean isShowOutstandingQCAssignmentList() {
		return showOutstandingQCAssignmentList;
	}
	public void setShowOutstandingQCAssignmentList(boolean showOutstandingQCAssignmentList) {
		this.showOutstandingQCAssignmentList = showOutstandingQCAssignmentList;
	}
	public boolean isShowRUAViewAllButton() {
		return showRUAViewAllButton;
	}
	public void setShowRUAViewAllButton(boolean showRUAViewAllButton) {
		this.showRUAViewAllButton = showRUAViewAllButton;
	}
	public boolean isShowOutstandingViewAllButton() {
		return showOutstandingViewAllButton;
	}
	public void setShowOutstandingViewAllButton(boolean showOutstandingViewAllButton) {
		this.showOutstandingViewAllButton = showOutstandingViewAllButton;
	}
}