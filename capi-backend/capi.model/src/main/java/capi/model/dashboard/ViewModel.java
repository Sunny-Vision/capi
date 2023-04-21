package capi.model.dashboard;

public class ViewModel {
	private Integer selectedUserId;
	private String selectedUserName;
	
	private AssignmentAndQuotationModel assignment;
	private AssignmentAndQuotationModel quotation;
	private BaseSectionModel ruaCase;
	private BaseSectionModel outstandingNewRecruitment;
	
	private AssignmentAndQuotationModel teamAssignment;
	private AssignmentAndQuotationModel teamQuotation;
	
	private PendingApprovalListModel pendingApprovalList;
	private OutstandingQCAssignmentListModel outstandingQCAssignmentList;
	
	private String currentMonth;
	private String previousMonth;
	private DataConversionModel dataConversionCurrentMonth;
	private DataConversionModel dataConversionPreviousMonth;
	private BaseSectionModel dataVerificationCurrentMonth;
	private BaseSectionModel dataVerificationPreviousMonth;
	
	private DataConversionModel dataCollectionCurrentMonthSupervisor;
	private DataConversionModel dataConversionCurrentMonthSupervisor;
	private DataConversionModel waitingForAllocationCurrentMonthSupervisor;
	private BaseSectionModel dataVerificationCurrentMonthSupervisor;
	
	private DataConversionModel dataCollectionPreviousMonthSupervisor;
	private DataConversionModel dataConversionPreviousMonthSupervisor;
	private DataConversionModel waitingForAllocationPreviousMonthSupervisor;
	private BaseSectionModel dataVerificationPreviousMonthSupervisor;
	
	private boolean showFieldOfficer;
	private boolean showFieldSupervisor;
	private boolean showFieldTeamHead;
	private boolean showIndoorDataConversion;
	private boolean showIndoorAllocatorSupervisor;
	private boolean showSelectStaff;
	
	public Integer getSelectedUserId() {
		return selectedUserId;
	}
	public void setSelectedUserId(Integer selectedUserId) {
		this.selectedUserId = selectedUserId;
	}
	public String getSelectedUserName() {
		return selectedUserName;
	}
	public void setSelectedUserName(String selectedUserName) {
		this.selectedUserName = selectedUserName;
	}
	public AssignmentAndQuotationModel getAssignment() {
		return assignment;
	}
	public void setAssignment(AssignmentAndQuotationModel assignment) {
		this.assignment = assignment;
	}
	public AssignmentAndQuotationModel getQuotation() {
		return quotation;
	}
	public void setQuotation(AssignmentAndQuotationModel quotation) {
		this.quotation = quotation;
	}
	public BaseSectionModel getRuaCase() {
		return ruaCase;
	}
	public void setRuaCase(BaseSectionModel ruaCase) {
		this.ruaCase = ruaCase;
	}
	public BaseSectionModel getOutstandingNewRecruitment() {
		return outstandingNewRecruitment;
	}
	public void setOutstandingNewRecruitment(BaseSectionModel outstandingNewRecruitment) {
		this.outstandingNewRecruitment = outstandingNewRecruitment;
	}
	public AssignmentAndQuotationModel getTeamAssignment() {
		return teamAssignment;
	}
	public void setTeamAssignment(AssignmentAndQuotationModel teamAssignment) {
		this.teamAssignment = teamAssignment;
	}
	public AssignmentAndQuotationModel getTeamQuotation() {
		return teamQuotation;
	}
	public void setTeamQuotation(AssignmentAndQuotationModel teamQuotation) {
		this.teamQuotation = teamQuotation;
	}
	public PendingApprovalListModel getPendingApprovalList() {
		return pendingApprovalList;
	}
	public void setPendingApprovalList(PendingApprovalListModel pendingApprovalList) {
		this.pendingApprovalList = pendingApprovalList;
	}
	public OutstandingQCAssignmentListModel getOutstandingQCAssignmentList() {
		return outstandingQCAssignmentList;
	}
	public void setOutstandingQCAssignmentList(OutstandingQCAssignmentListModel outstandingQCAssignmentList) {
		this.outstandingQCAssignmentList = outstandingQCAssignmentList;
	}
	public String getCurrentMonth() {
		return currentMonth;
	}
	public void setCurrentMonth(String currentMonth) {
		this.currentMonth = currentMonth;
	}
	public String getPreviousMonth() {
		return previousMonth;
	}
	public void setPreviousMonth(String previousMonth) {
		this.previousMonth = previousMonth;
	}
	public DataConversionModel getDataConversionCurrentMonth() {
		return dataConversionCurrentMonth;
	}
	public void setDataConversionCurrentMonth(DataConversionModel dataConversionCurrentMonth) {
		this.dataConversionCurrentMonth = dataConversionCurrentMonth;
	}
	public DataConversionModel getDataConversionPreviousMonth() {
		return dataConversionPreviousMonth;
	}
	public void setDataConversionPreviousMonth(DataConversionModel dataConversionPreviousMonth) {
		this.dataConversionPreviousMonth = dataConversionPreviousMonth;
	}
	public BaseSectionModel getDataVerificationCurrentMonth() {
		return dataVerificationCurrentMonth;
	}
	public void setDataVerificationCurrentMonth(BaseSectionModel dataVerificationCurrentMonth) {
		this.dataVerificationCurrentMonth = dataVerificationCurrentMonth;
	}
	public BaseSectionModel getDataVerificationPreviousMonth() {
		return dataVerificationPreviousMonth;
	}
	public void setDataVerificationPreviousMonth(BaseSectionModel dataVerificationPreviousMonth) {
		this.dataVerificationPreviousMonth = dataVerificationPreviousMonth;
	}
	public DataConversionModel getDataCollectionCurrentMonthSupervisor() {
		return dataCollectionCurrentMonthSupervisor;
	}
	public void setDataCollectionCurrentMonthSupervisor(DataConversionModel dataCollectionCurrentMonthSupervisor) {
		this.dataCollectionCurrentMonthSupervisor = dataCollectionCurrentMonthSupervisor;
	}
	public DataConversionModel getDataConversionCurrentMonthSupervisor() {
		return dataConversionCurrentMonthSupervisor;
	}
	public void setDataConversionCurrentMonthSupervisor(DataConversionModel dataConversionCurrentMonthSupervisor) {
		this.dataConversionCurrentMonthSupervisor = dataConversionCurrentMonthSupervisor;
	}
	public DataConversionModel getWaitingForAllocationCurrentMonthSupervisor() {
		return waitingForAllocationCurrentMonthSupervisor;
	}
	public void setWaitingForAllocationCurrentMonthSupervisor(
			DataConversionModel waitingForAllocationCurrentMonthSupervisor) {
		this.waitingForAllocationCurrentMonthSupervisor = waitingForAllocationCurrentMonthSupervisor;
	}
	public BaseSectionModel getDataVerificationCurrentMonthSupervisor() {
		return dataVerificationCurrentMonthSupervisor;
	}
	public void setDataVerificationCurrentMonthSupervisor(BaseSectionModel dataVerificationCurrentMonthSupervisor) {
		this.dataVerificationCurrentMonthSupervisor = dataVerificationCurrentMonthSupervisor;
	}
	public DataConversionModel getDataCollectionPreviousMonthSupervisor() {
		return dataCollectionPreviousMonthSupervisor;
	}
	public void setDataCollectionPreviousMonthSupervisor(DataConversionModel dataCollectionPreviousMonthSupervisor) {
		this.dataCollectionPreviousMonthSupervisor = dataCollectionPreviousMonthSupervisor;
	}
	public DataConversionModel getDataConversionPreviousMonthSupervisor() {
		return dataConversionPreviousMonthSupervisor;
	}
	public void setDataConversionPreviousMonthSupervisor(DataConversionModel dataConversionPreviousMonthSupervisor) {
		this.dataConversionPreviousMonthSupervisor = dataConversionPreviousMonthSupervisor;
	}
	public DataConversionModel getWaitingForAllocationPreviousMonthSupervisor() {
		return waitingForAllocationPreviousMonthSupervisor;
	}
	public void setWaitingForAllocationPreviousMonthSupervisor(
			DataConversionModel waitingForAllocationPreviousMonthSupervisor) {
		this.waitingForAllocationPreviousMonthSupervisor = waitingForAllocationPreviousMonthSupervisor;
	}
	public BaseSectionModel getDataVerificationPreviousMonthSupervisor() {
		return dataVerificationPreviousMonthSupervisor;
	}
	public void setDataVerificationPreviousMonthSupervisor(BaseSectionModel dataVerificationPreviousMonthSupervisor) {
		this.dataVerificationPreviousMonthSupervisor = dataVerificationPreviousMonthSupervisor;
	}
	public boolean isShowFieldOfficer() {
		return showFieldOfficer;
	}
	public void setShowFieldOfficer(boolean showFieldOfficer) {
		this.showFieldOfficer = showFieldOfficer;
	}
	public boolean isShowFieldSupervisor() {
		return showFieldSupervisor;
	}
	public void setShowFieldSupervisor(boolean showFieldSupervisor) {
		this.showFieldSupervisor = showFieldSupervisor;
	}
	public boolean isShowFieldTeamHead() {
		return showFieldTeamHead;
	}
	public void setShowFieldTeamHead(boolean showFieldTeamHead) {
		this.showFieldTeamHead = showFieldTeamHead;
	}
	public boolean isShowIndoorDataConversion() {
		return showIndoorDataConversion;
	}
	public void setShowIndoorDataConversion(boolean showIndoorDataConversion) {
		this.showIndoorDataConversion = showIndoorDataConversion;
	}
	public boolean isShowIndoorAllocatorSupervisor() {
		return showIndoorAllocatorSupervisor;
	}
	public void setShowIndoorAllocatorSupervisor(boolean showIndoorAllocatorSupervisor) {
		this.showIndoorAllocatorSupervisor = showIndoorAllocatorSupervisor;
	}
	public boolean isShowSelectStaff() {
		return showSelectStaff;
	}
	public void setShowSelectStaff(boolean showSelectStaff) {
		this.showSelectStaff = showSelectStaff;
	}
}
