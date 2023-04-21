package capi.service.dashboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentDao;
import capi.dal.QuotationRecordDao;
import capi.dal.UserDao;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.dashboard.AssignmentAndQuotationModel;
import capi.model.dashboard.BaseSectionModel;
import capi.model.dashboard.DashboardStatistics;
import capi.model.dashboard.DataConversionModel;
import capi.model.dashboard.DeadlineRowModel;
import capi.model.dashboard.OutstandingQCAssignmentListModel;
import capi.model.dashboard.PendingApprovalListModel;
import capi.model.dashboard.ViewModel;
import capi.service.UserService;
import capi.service.assignmentManagement.AssignmentApprovalService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationService;
import capi.service.itineraryPlanning.ItineraryPlanningService;
import capi.service.qualityControlManagement.PECheckService;
import capi.service.qualityControlManagement.SpotCheckService;
import capi.service.qualityControlManagement.SupervisoryVisitService;
import capi.service.timeLogManagement.TimeLogService;
import capi.service.userAccountManagement.StaffProfileService;

@Service("DashboardService")
public class DashboardService {
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private StaffProfileService staffProfileService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ItineraryPlanningService itineraryPlanningService;
	
	@Autowired
	private AssignmentApprovalService assignmentApprovalService;
	
	@Autowired
	private TimeLogService timeLogService;
	
	@Autowired
	private SpotCheckService spotCheckService;
	
	@Autowired
	private SupervisoryVisitService supervisoryVisitService;
	
	@Autowired
	private PECheckService peCheckService;
	
	@Autowired
	private QuotationService quotationService;
	
	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;
	
	@Autowired
	private UserDao userDao;
	
	/**
	 * Prepare view model
	 */
	public ViewModel prepareViewModel(Integer selectedUserId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer loginUserId = detail.getUserId();
		
		ViewModel model = new ViewModel();
		
		if (selectedUserId != null && selectedUserId == 0) selectedUserId = null;

		int sumAuthorityLevel = 0;
		
		if (selectedUserId != null) {
			User user = userDao.findById(selectedUserId);
			model.setSelectedUserId(selectedUserId);
			model.setSelectedUserName(user.getStaffCode() + " - " + user.getChineseName());
			
			sumAuthorityLevel = staffProfileService.getSumAuthorityLevelByUser(selectedUserId);
		} else {
			model.setSelectedUserName("All");
			model.setSelectedUserId(0);
			
			sumAuthorityLevel = detail.getAuthorityLevel();
		}
		
		
		
		
		
		
		
		
		DashboardStatistics stat = assignmentDao.getDashboardStatistics(sumAuthorityLevel, loginUserId, selectedUserId);
		
		if ((sumAuthorityLevel & 16) == 16) { // field officer
			model.setShowFieldOfficer(true);
			prepareFieldOfficer(model, sumAuthorityLevel, loginUserId, selectedUserId, stat);
		}
		
		if ((sumAuthorityLevel & 4) == 4) { // field supervisor
			model.setShowFieldSupervisor(true);
			prepareFieldSupervisor(model, sumAuthorityLevel, loginUserId, selectedUserId, stat);
		}
		
		if ((sumAuthorityLevel & 2) == 2) { // field team head
			model.setShowFieldTeamHead(true);
			prepareFieldTeamHead(model, sumAuthorityLevel, loginUserId, selectedUserId, stat);
		}
		
		if ((sumAuthorityLevel & 128) == 128) { // indoor data conversion
			model.setShowIndoorDataConversion(true);
			prepareIndoorDataConversion(model, sumAuthorityLevel, loginUserId, selectedUserId, stat);
		}
		
		if (((sumAuthorityLevel & 32) == 32) || ((sumAuthorityLevel & 1024) == 1024)) { // indoor allocator, indoor supervisor
			model.setShowIndoorAllocatorSupervisor(true);
			prepareIndoorAllocatorSupervisor(model, sumAuthorityLevel, loginUserId, selectedUserId, stat);
		}
		
		
		
		
		
		
		
		
		
		return model;
	}
	
	/**
	 * Prepare field officer
	 */
	public void prepareFieldOfficer(ViewModel model, int sumAuthorityLevel, int loginUserId, Integer selectedUserId, DashboardStatistics stat) {
		AssignmentAndQuotationModel assignmentModel = new AssignmentAndQuotationModel();
		assignmentModel.setShowMetric(true);
		assignmentModel.setShowProgress(true);
		assignmentModel.setShowDeadlineTable(true);
		assignmentModel.setShowDeadlineTableViewButton(true);
		assignmentModel.setShowVerficationRevisitCount(true);
		assignmentModel.setCount(stat.getFieldOfficerAssignmentCount());
		assignmentModel.setTotal(stat.getFieldOfficerAssignmentTotal());
		assignmentModel.setPercent(calcualtePercent(assignmentModel.getCount(), assignmentModel.getTotal()));
		assignmentModel.setVerification(stat.getFieldOfficerAssignmentVerification());
		assignmentModel.setRevisit(stat.getFieldOfficerAssignmentRevisit());
		
		List<DeadlineRowModel> assignmentDeadlineList = null;
		
		if (selectedUserId != null)
			assignmentDeadlineList = assignmentDao.getDashboardFieldOfficerAssignments(selectedUserId);
		else
			assignmentDeadlineList = assignmentDao.getDashboardFieldOfficerAssignments(loginUserId);
		
		assignmentModel.setDeadlines(assignmentDeadlineList);
		
		model.setAssignment(assignmentModel);
		
		AssignmentAndQuotationModel quotationModel = new AssignmentAndQuotationModel();
		quotationModel.setShowMetric(true);
		quotationModel.setShowProgress(true);
		quotationModel.setShowDeadlineTable(true);
		quotationModel.setShowVerficationRevisitCount(true);
		quotationModel.setCount(stat.getFieldOfficerQuotationCount());
		quotationModel.setTotal(stat.getFieldOfficerQuotationTotal());
		quotationModel.setPercent(calcualtePercent(quotationModel.getCount(), quotationModel.getTotal()));
		quotationModel.setVerification(stat.getFieldOfficerQuotationVerification());
		quotationModel.setRevisit(stat.getFieldOfficerQuotationRevisit());

		List<DeadlineRowModel> quotationDeadlineList = null;

		if (selectedUserId != null)
			quotationDeadlineList = quotationRecordDao.getDashboardFieldOfficerQuotationRecords(selectedUserId);
		else
			quotationDeadlineList = quotationRecordDao.getDashboardFieldOfficerQuotationRecords(loginUserId);
		
		quotationModel.setDeadlines(quotationDeadlineList);
		
		model.setQuotation(quotationModel);
		
		BaseSectionModel ruaCaseModel = new BaseSectionModel();
		ruaCaseModel.setShowMetric(true);
		ruaCaseModel.setShowRUAViewAllButton(true);
		ruaCaseModel.setCount(quotationService.queryQuotationRUACount());
		model.setRuaCase(ruaCaseModel);
		
		BaseSectionModel outstandingNewRecruitmentModel = new BaseSectionModel();
		outstandingNewRecruitmentModel.setShowMetric(true);
		outstandingNewRecruitmentModel.setShowOutstandingViewAllButton(true);
		outstandingNewRecruitmentModel.setCount(assignmentMaintenanceService.getNewRecruitmentTableCount());
		model.setOutstandingNewRecruitment(outstandingNewRecruitmentModel);
	}
	
	/**
	 * Prepare field supervisor
	 */
	public void prepareFieldSupervisor(ViewModel model, int sumAuthorityLevel, int loginUserId, Integer selectedUserId, DashboardStatistics stat) {
		Set<Integer> userIds = userService.getLoginUserSubordinatesAndSelfWithActing();
		List<Integer> userIdList = new ArrayList<Integer>(userIds);
		
		AssignmentAndQuotationModel assignmentModel = new AssignmentAndQuotationModel();
		assignmentModel.setShowMetric(true);
		assignmentModel.setShowProgress(true);
		assignmentModel.setShowDeadlineTable(true);
		assignmentModel.setShowDeadlineTableViewButton(true);
		assignmentModel.setShowVerficationRevisitCount(true);
		assignmentModel.setCount(stat.getFieldSupervisorTeamAssignmentCount());
		assignmentModel.setTotal(stat.getFieldSupervisorTeamAssignmentTotal());
		assignmentModel.setPercent(calcualtePercent(assignmentModel.getCount(), assignmentModel.getTotal()));
		assignmentModel.setVerification(stat.getFieldSupervisorTeamAssignmentVerification());
		assignmentModel.setRevisit(stat.getFieldSupervisorTeamAssignmentRevisit());
		
		List<DeadlineRowModel> assignmentDeadlineList = null;
		
		assignmentDeadlineList = assignmentDao.getDashboardFieldOfficerAssignments(userIdList);
		
		assignmentModel.setDeadlines(assignmentDeadlineList);
		
		model.setTeamAssignment(assignmentModel);
		
		AssignmentAndQuotationModel quotationModel = new AssignmentAndQuotationModel();
		quotationModel.setShowMetric(true);
		quotationModel.setShowProgress(true);
		quotationModel.setShowDeadlineTable(true);
		quotationModel.setShowVerficationRevisitCount(true);
		quotationModel.setCount(stat.getFieldSupervisorTeamQuotationCount());
		quotationModel.setTotal(stat.getFieldSupervisorTeamQuotationTotal());
		quotationModel.setPercent(calcualtePercent(quotationModel.getCount(), quotationModel.getTotal()));
		quotationModel.setVerification(stat.getFieldSupervisorTeamQuotationVerification());
		quotationModel.setRevisit(stat.getFieldSupervisorTeamQuotationRevisit());

		List<DeadlineRowModel> quotationDeadlineList = null;

		quotationDeadlineList = quotationRecordDao.getDashboardFieldOfficerQuotationRecords(userIdList);
		
		quotationModel.setDeadlines(quotationDeadlineList);

		model.setTeamQuotation(quotationModel);
		
		PendingApprovalListModel pendingApprovalList = new PendingApprovalListModel();
		pendingApprovalList.setShowPendingApprovalList(true);
		pendingApprovalList.setItineraryPlan(itineraryPlanningService.queryItineraryPlanApprovalCount());
		pendingApprovalList.setSubmittedAssignment(assignmentApprovalService.getAssignemntApprovalTableListCount());
		pendingApprovalList.setItineraryCheck(timeLogService.queryItineraryCheckingTableCount());
		pendingApprovalList.setRua(assignmentApprovalService.getRUACaseApprovalTableCount());
		pendingApprovalList.setNewRecruitment(assignmentApprovalService.getNewRecruitmentApprovalTableCount());
		model.setPendingApprovalList(pendingApprovalList);
		
		OutstandingQCAssignmentListModel outstandingQCAssignmentList = new OutstandingQCAssignmentListModel();
		outstandingQCAssignmentList.setShowOutstandingQCAssignmentList(true);
		outstandingQCAssignmentList.setSpotCheck(spotCheckService.getSpotCheckCount(false));
		outstandingQCAssignmentList.setSupervisoryCheck(supervisoryVisitService.getSupervisoryVisitCount(sumAuthorityLevel, userIdList, false));
		outstandingQCAssignmentList.setPeCheck(peCheckService.getCheckTableCount());
		model.setOutstandingQCAssignmentList(outstandingQCAssignmentList);
	}

	/**
	 * Prepare field team head 
	 */
	public void prepareFieldTeamHead(ViewModel model, int sumAuthorityLevel, int loginUserId, Integer selectedUserId, DashboardStatistics stat) {
		Set<Integer> userIds = userService.getLoginUserSubSubordinatesAndSelfWithActing();
		List<Integer> userIdList = new ArrayList<Integer>(userIds);
		
		AssignmentAndQuotationModel assignmentModel = new AssignmentAndQuotationModel();
		assignmentModel.setShowMetric(true);
		assignmentModel.setShowProgress(true);
		assignmentModel.setShowDeadlineTable(true);
		assignmentModel.setShowDeadlineTableViewButton(true);
		assignmentModel.setShowVerficationRevisitCount(true);
		assignmentModel.setCount(stat.getFieldTeamHeadTeamAssignmentCount());
		assignmentModel.setTotal(stat.getFieldTeamHeadTeamAssignmentTotal());
		assignmentModel.setPercent(calcualtePercent(assignmentModel.getCount(), assignmentModel.getTotal()));
		assignmentModel.setVerification(stat.getFieldTeamHeadTeamAssignmentVerification());
		assignmentModel.setRevisit(stat.getFieldTeamHeadTeamAssignmentRevisit());
		
		List<DeadlineRowModel> assignmentDeadlineList = null;
		
		assignmentDeadlineList = assignmentDao.getDashboardFieldOfficerAssignments(userIdList);
		
		assignmentModel.setDeadlines(assignmentDeadlineList);
		
		model.setTeamAssignment(assignmentModel);
		
		AssignmentAndQuotationModel quotationModel = new AssignmentAndQuotationModel();
		quotationModel.setShowMetric(true);
		quotationModel.setShowProgress(true);
		quotationModel.setShowDeadlineTable(true);
		quotationModel.setShowVerficationRevisitCount(true);
		quotationModel.setCount(stat.getFieldTeamHeadTeamQuotationCount());
		quotationModel.setTotal(stat.getFieldTeamHeadTeamQuotationTotal());
		quotationModel.setPercent(calcualtePercent(quotationModel.getCount(), quotationModel.getTotal()));
		quotationModel.setVerification(stat.getFieldTeamHeadTeamQuotationVerification());
		quotationModel.setRevisit(stat.getFieldTeamHeadTeamQuotationRevisit());

		List<DeadlineRowModel> quotationDeadlineList = null;

		quotationDeadlineList = quotationRecordDao.getDashboardFieldOfficerQuotationRecords(userIdList);
		
		quotationModel.setDeadlines(quotationDeadlineList);

		model.setTeamQuotation(quotationModel);
		
		PendingApprovalListModel pendingApprovalList = new PendingApprovalListModel();
		pendingApprovalList.setShowPendingApprovalList(true);
		pendingApprovalList.setItineraryPlan(itineraryPlanningService.queryItineraryPlanApprovalCount());
		pendingApprovalList.setSubmittedAssignment(assignmentApprovalService.getAssignemntApprovalTableListCount());
		pendingApprovalList.setItineraryCheck(timeLogService.queryItineraryCheckingTableCount());
		pendingApprovalList.setRua(assignmentApprovalService.getRUACaseApprovalTableCount());
		pendingApprovalList.setNewRecruitment(assignmentApprovalService.getNewRecruitmentApprovalTableCount());
		model.setPendingApprovalList(pendingApprovalList);
		
		OutstandingQCAssignmentListModel outstandingQCAssignmentList = new OutstandingQCAssignmentListModel();
		outstandingQCAssignmentList.setShowOutstandingQCAssignmentList(true);
		outstandingQCAssignmentList.setSpotCheck(spotCheckService.getSpotCheckCount(true));
		outstandingQCAssignmentList.setSupervisoryCheck(supervisoryVisitService.getSupervisoryVisitCount(sumAuthorityLevel,userIdList,true));
		outstandingQCAssignmentList.setPeCheck(peCheckService.getCheckTableCount());
		model.setOutstandingQCAssignmentList(outstandingQCAssignmentList);
	}
	
	/**
	 * Prepare indoor data conversion
	 */
	public void prepareIndoorDataConversion(ViewModel model, int sumAuthorityLevel, int loginUserId, Integer selectedUserId, DashboardStatistics stat) {
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
		Calendar date = Calendar.getInstance();
		String currentMonth = monthFormat.format(date.getTime());
		date.add(Calendar.MONTH, -1);
		String previousMonth = monthFormat.format(date.getTime());

		model.setCurrentMonth(currentMonth);
		model.setPreviousMonth(previousMonth);
		
		DataConversionModel dataConversionCurrentMonth = new DataConversionModel();
		dataConversionCurrentMonth.setShowMetric(true);
		dataConversionCurrentMonth.setShowProgress(true);
		dataConversionCurrentMonth.setShowMRPS(true);
		dataConversionCurrentMonth.setCount(stat.getIndoorDataConversionCurrentMonthIndividualCount());
		dataConversionCurrentMonth.setTotal(stat.getIndoorDataConversionCurrentMonthIndividualTotal());
		dataConversionCurrentMonth.setPercent(calcualtePercent(dataConversionCurrentMonth.getCount(), dataConversionCurrentMonth.getTotal()));
		dataConversionCurrentMonth.setMrpsCount(stat.getIndoorDataConversionCurrentMonthIndividualMRPSCount());
		dataConversionCurrentMonth.setMrpsTotal(stat.getIndoorDataConversionCurrentMonthIndividualMRPSTotal());
		dataConversionCurrentMonth.setOthersCount(stat.getIndoorDataConversionCurrentMonthIndividualOthersCount());
		dataConversionCurrentMonth.setOthersTotal(stat.getIndoorDataConversionCurrentMonthIndividualOthersTotal());
		model.setDataConversionCurrentMonth(dataConversionCurrentMonth);
		
		BaseSectionModel dataVerificationCurrentMonth = new BaseSectionModel();
		dataVerificationCurrentMonth.setShowMetric(true);
		dataVerificationCurrentMonth.setCount(stat.getIndoorDataConversionCurrentMonthIndividualVerification());
		model.setDataVerificationCurrentMonth(dataVerificationCurrentMonth);
		
		DataConversionModel dataConversionPreviousMonth = new DataConversionModel();
		dataConversionPreviousMonth.setShowMetric(true);
		dataConversionPreviousMonth.setShowProgress(true);
		dataConversionPreviousMonth.setShowMRPS(true);
		dataConversionPreviousMonth.setCount(stat.getIndoorDataConversionPreviousMonthIndividualCount());
		dataConversionPreviousMonth.setTotal(stat.getIndoorDataConversionPreviousMonthIndividualTotal());
		dataConversionPreviousMonth.setPercent(calcualtePercent(dataConversionPreviousMonth.getCount(), dataConversionPreviousMonth.getTotal()));
		dataConversionPreviousMonth.setMrpsCount(stat.getIndoorDataConversionPreviousMonthIndividualMRPSCount());
		dataConversionPreviousMonth.setMrpsTotal(stat.getIndoorDataConversionPreviousMonthIndividualMRPSTotal());
		dataConversionPreviousMonth.setOthersCount(stat.getIndoorDataConversionPreviousMonthIndividualOthersCount());
		dataConversionPreviousMonth.setOthersTotal(stat.getIndoorDataConversionPreviousMonthIndividualOthersTotal());
		model.setDataConversionPreviousMonth(dataConversionPreviousMonth);
		
		BaseSectionModel dataVerificationPreviousMonth = new BaseSectionModel();
		dataVerificationPreviousMonth.setShowMetric(true);
		dataVerificationPreviousMonth.setCount(stat.getIndoorDataConversionPreviousMonthIndividualVerification());
		model.setDataVerificationPreviousMonth(dataVerificationPreviousMonth);
	}

	/**
	 * Prepare indoor allocator supervisor
	 */
	public void prepareIndoorAllocatorSupervisor(ViewModel model, int sumAuthorityLevel, int loginUserId, Integer selectedUserId, DashboardStatistics stat) {
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
		Calendar date = Calendar.getInstance();
		String currentMonth = monthFormat.format(date.getTime());
		date.add(Calendar.MONTH, -1);
		String previousMonth = monthFormat.format(date.getTime());

		model.setCurrentMonth(currentMonth);
		model.setPreviousMonth(previousMonth);
		
		DataConversionModel dataCollectionCurrentMonthSupervisor = new DataConversionModel();
		dataCollectionCurrentMonthSupervisor.setShowMetric(true);
		dataCollectionCurrentMonthSupervisor.setShowProgress(true);
		dataCollectionCurrentMonthSupervisor.setShowMRPS(true);
		dataCollectionCurrentMonthSupervisor.setCount(stat.getIndoorAllocatorSupervisorCurrentMonthCount());
		dataCollectionCurrentMonthSupervisor.setTotal(stat.getIndoorAllocatorSupervisorCurrentMonthTotal());
		dataCollectionCurrentMonthSupervisor.setPercent(calcualtePercent(dataCollectionCurrentMonthSupervisor.getCount(), dataCollectionCurrentMonthSupervisor.getTotal()));
		dataCollectionCurrentMonthSupervisor.setMrpsCount(stat.getIndoorAllocatorSupervisorCurrentMonthMRPSCount());
		dataCollectionCurrentMonthSupervisor.setMrpsTotal(stat.getIndoorAllocatorSupervisorCurrentMonthMRPSTotal());
		dataCollectionCurrentMonthSupervisor.setOthersCount(stat.getIndoorAllocatorSupervisorCurrentMonthOthersCount());
		dataCollectionCurrentMonthSupervisor.setOthersTotal(stat.getIndoorAllocatorSupervisorCurrentMonthOthersTotal());
		model.setDataCollectionCurrentMonthSupervisor(dataCollectionCurrentMonthSupervisor);
		
		DataConversionModel dataConversionCurrentMonthSupervisor = new DataConversionModel();
		dataConversionCurrentMonthSupervisor.setShowMetric(true);
		dataConversionCurrentMonthSupervisor.setShowProgress(true);
		dataConversionCurrentMonthSupervisor.setShowMRPS(true);
		dataConversionCurrentMonthSupervisor.setCount(stat.getIndoorAllocatorSupervisorCurrentMonthConversionCount());
		dataConversionCurrentMonthSupervisor.setTotal(stat.getIndoorAllocatorSupervisorCurrentMonthConversionTotal());
		dataConversionCurrentMonthSupervisor.setPercent(calcualtePercent(dataConversionCurrentMonthSupervisor.getCount(), dataConversionCurrentMonthSupervisor.getTotal()));
		dataConversionCurrentMonthSupervisor.setMrpsCount(stat.getIndoorAllocatorSupervisorCurrentMonthConversionMRPSCount());
		dataConversionCurrentMonthSupervisor.setMrpsTotal(stat.getIndoorAllocatorSupervisorCurrentMonthConversionMRPSTotal());
		dataConversionCurrentMonthSupervisor.setOthersCount(stat.getIndoorAllocatorSupervisorCurrentMonthConversionOthersCount());
		dataConversionCurrentMonthSupervisor.setOthersTotal(stat.getIndoorAllocatorSupervisorCurrentMonthConversionOthersTotal());
		model.setDataConversionCurrentMonthSupervisor(dataConversionCurrentMonthSupervisor);
		
		DataConversionModel waitingForAllocationCurrentMonthSupervisor = new DataConversionModel();
		waitingForAllocationCurrentMonthSupervisor.setShowMetric(true);
		waitingForAllocationCurrentMonthSupervisor.setShowProgress(true);
		waitingForAllocationCurrentMonthSupervisor.setShowMRPS(true);
		waitingForAllocationCurrentMonthSupervisor.setCount(stat.getIndoorAllocatorSupervisorCurrentMonthAllocationCount());
		waitingForAllocationCurrentMonthSupervisor.setTotal(stat.getIndoorAllocatorSupervisorCurrentMonthAllocationTotal());
		waitingForAllocationCurrentMonthSupervisor.setPercent(calcualtePercent(waitingForAllocationCurrentMonthSupervisor.getCount(), waitingForAllocationCurrentMonthSupervisor.getTotal()));
		waitingForAllocationCurrentMonthSupervisor.setMrpsCount(stat.getIndoorAllocatorSupervisorCurrentMonthAllocationMRPSCount());
		waitingForAllocationCurrentMonthSupervisor.setMrpsTotal(stat.getIndoorAllocatorSupervisorCurrentMonthAllocationMRPSTotal());
		waitingForAllocationCurrentMonthSupervisor.setOthersCount(stat.getIndoorAllocatorSupervisorCurrentMonthAllocationOthersCount());
		waitingForAllocationCurrentMonthSupervisor.setOthersTotal(stat.getIndoorAllocatorSupervisorCurrentMonthAllocationOthersTotal());
		model.setWaitingForAllocationCurrentMonthSupervisor(waitingForAllocationCurrentMonthSupervisor);
		
		BaseSectionModel dataVerificationCurrentMonthSupervisor = new BaseSectionModel();
		dataVerificationCurrentMonthSupervisor.setShowMetric(true);
		dataVerificationCurrentMonthSupervisor.setCount(stat.getIndoorAllocatorSupervisorCurrentMonthVerification());
		model.setDataVerificationCurrentMonthSupervisor(dataVerificationCurrentMonthSupervisor);
		
		DataConversionModel dataCollectionPreviousMonthSupervisor = new DataConversionModel();
		dataCollectionPreviousMonthSupervisor.setShowMetric(true);
		dataCollectionPreviousMonthSupervisor.setShowProgress(true);
		dataCollectionPreviousMonthSupervisor.setShowMRPS(true);
		dataCollectionPreviousMonthSupervisor.setCount(stat.getIndoorAllocatorSupervisorPreviousMonthCount());
		dataCollectionPreviousMonthSupervisor.setTotal(stat.getIndoorAllocatorSupervisorPreviousMonthTotal());
		dataCollectionPreviousMonthSupervisor.setPercent(calcualtePercent(dataCollectionPreviousMonthSupervisor.getCount(), dataCollectionPreviousMonthSupervisor.getTotal()));
		dataCollectionPreviousMonthSupervisor.setMrpsCount(stat.getIndoorAllocatorSupervisorPreviousMonthMRPSCount());
		dataCollectionPreviousMonthSupervisor.setMrpsTotal(stat.getIndoorAllocatorSupervisorPreviousMonthMRPSTotal());
		dataCollectionPreviousMonthSupervisor.setOthersCount(stat.getIndoorAllocatorSupervisorPreviousMonthOthersCount());
		dataCollectionPreviousMonthSupervisor.setOthersTotal(stat.getIndoorAllocatorSupervisorPreviousMonthOthersTotal());
		model.setDataCollectionPreviousMonthSupervisor(dataCollectionPreviousMonthSupervisor);
		
		DataConversionModel dataConversionPreviousMonthSupervisor = new DataConversionModel();
		dataConversionPreviousMonthSupervisor.setShowMetric(true);
		dataConversionPreviousMonthSupervisor.setShowProgress(true);
		dataConversionPreviousMonthSupervisor.setShowMRPS(true);
		dataConversionPreviousMonthSupervisor.setCount(stat.getIndoorAllocatorSupervisorPreviousMonthConversionCount());
		dataConversionPreviousMonthSupervisor.setTotal(stat.getIndoorAllocatorSupervisorPreviousMonthConversionTotal());
		dataConversionPreviousMonthSupervisor.setPercent(calcualtePercent(dataConversionPreviousMonthSupervisor.getCount(), dataConversionPreviousMonthSupervisor.getTotal()));
		dataConversionPreviousMonthSupervisor.setMrpsCount(stat.getIndoorAllocatorSupervisorPreviousMonthConversionMRPSCount());
		dataConversionPreviousMonthSupervisor.setMrpsTotal(stat.getIndoorAllocatorSupervisorPreviousMonthConversionMRPSTotal());
		dataConversionPreviousMonthSupervisor.setOthersCount(stat.getIndoorAllocatorSupervisorPreviousMonthConversionOthersCount());
		dataConversionPreviousMonthSupervisor.setOthersTotal(stat.getIndoorAllocatorSupervisorPreviousMonthConversionOthersTotal());
		model.setDataConversionPreviousMonthSupervisor(dataConversionPreviousMonthSupervisor);
		
		DataConversionModel waitingForAllocationPreviousMonthSupervisor = new DataConversionModel();
		waitingForAllocationPreviousMonthSupervisor.setShowMetric(true);
		waitingForAllocationPreviousMonthSupervisor.setShowProgress(true);
		waitingForAllocationPreviousMonthSupervisor.setShowMRPS(true);
		waitingForAllocationPreviousMonthSupervisor.setCount(stat.getIndoorAllocatorSupervisorPreviousMonthAllocationCount());
		waitingForAllocationPreviousMonthSupervisor.setTotal(stat.getIndoorAllocatorSupervisorPreviousMonthAllocationTotal());
		waitingForAllocationPreviousMonthSupervisor.setPercent(calcualtePercent(waitingForAllocationPreviousMonthSupervisor.getCount(), waitingForAllocationPreviousMonthSupervisor.getTotal()));
		waitingForAllocationPreviousMonthSupervisor.setMrpsCount(stat.getIndoorAllocatorSupervisorPreviousMonthAllocationMRPSCount());
		waitingForAllocationPreviousMonthSupervisor.setMrpsTotal(stat.getIndoorAllocatorSupervisorPreviousMonthAllocationMRPSTotal());
		waitingForAllocationPreviousMonthSupervisor.setOthersCount(stat.getIndoorAllocatorSupervisorPreviousMonthAllocationOthersCount());
		waitingForAllocationPreviousMonthSupervisor.setOthersTotal(stat.getIndoorAllocatorSupervisorPreviousMonthAllocationOthersTotal());
		model.setWaitingForAllocationPreviousMonthSupervisor(waitingForAllocationPreviousMonthSupervisor);
		
		BaseSectionModel dataVerificationPreviousMonthSupervisor = new BaseSectionModel();
		dataVerificationPreviousMonthSupervisor.setShowMetric(true);
		dataVerificationPreviousMonthSupervisor.setCount(stat.getIndoorAllocatorSupervisorPreviousMonthVerification());
		model.setDataVerificationPreviousMonthSupervisor(dataVerificationPreviousMonthSupervisor);
	}
	
	/**
	 * Calculate percent
	 */
	public Double calcualtePercent(Long count, Long total) {
		if (count == null || total == null) return null;
		
		double percent = (double)Math.round((double)count / (double)total * 1000 ) / 10;
		return percent;
	}

	/**
	 * Get Officer select format
	 */
	public Select2ResponseModel queryOfficerSelect2(Select2RequestModel queryModel, Integer userId) {
		Select2ResponseModel model = userService.queryOfficerSelect2(queryModel, userId);
		if(queryModel.getPage() == 0) {
			Select2ResponseModel.Select2Item allItem = model.new Select2Item();
			allItem.setText("All");
			allItem.setId("0");
			model.getResults().add(0, allItem);
		}
		return model;
	}
}
