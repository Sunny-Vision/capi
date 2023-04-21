package capi.service.assignmentAllocationAndReallocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AllocationBatchDao;
import capi.dal.AssignmentAdjustmentDao;
import capi.dal.AssignmentDao;
import capi.dal.CalendarEventDao;
import capi.dal.DistrictHeadAdjustmentDao;
import capi.dal.NotificationDao;
import capi.dal.PECheckTaskDao;
import capi.dal.QuotationRecordDao;
import capi.dal.UserDao;
import capi.entity.Assignment;
import capi.entity.AssignmentAdjustment;
import capi.entity.DistrictHeadAdjustment;
import capi.entity.QuotationRecord;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.PostModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.RecommendAssignmentModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.SelectedAssignmentModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.SelectedSessionModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.TableList;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.ViewModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.UserService;
import capi.service.qualityControlManagement.PostEnumerationCertaintyCaseService;

@Service("AssignmentTransferInOutMaintenanceService")
public class AssignmentTransferInOutMaintenanceService extends BaseService {

	@Autowired
	AssignmentAdjustmentDao dao;
	
	@Autowired
	AssignmentDao assignmentDao;
	
	@Autowired
	QuotationRecordDao quotationRecordDao;
	
	@Autowired
	AllocationBatchDao allocationBatchDao;
	
	@Autowired
	UserDao userDao;

	@Autowired
	NotificationDao notificationDao;
	
	@Autowired
	CalendarEventDao calendarEventDao;
	
	@Autowired
	DistrictHeadAdjustmentDao districtHeadAdjustmentDao;
	
	@Autowired
	CommonService commonService;

	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	NotificationService notifyService;
	
	@Autowired
	PostEnumerationCertaintyCaseService peService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PECheckTaskDao peTaskDao;
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<TableList> getTableList(DatatableRequestModel model,
			String referenceMonth, Integer fromUserId, Integer fromUserPermissionId, List<Integer> supervisorIds,
			String[] statuses) throws Exception {

		Order order = this.getOrder(model, "fromFieldOfficer", "targetFieldOfficer", "targetReleaseManDay", "actualReleaseManDay", "status");
		
		String search = model.getSearch().get("value");
		Date referenceMonthDate = null;
		if (!StringUtils.isEmpty(referenceMonth)) {
			referenceMonthDate = commonService.getMonth(referenceMonth);
		}
		
		List<TableList> result = dao.getTableList(search, model.getStart(), model.getLength(), order,
				referenceMonthDate, fromUserId, fromUserPermissionId, supervisorIds,
				statuses);
		for(TableList resultModel : result){
			if(resultModel.getActualReleaseManDay() != null){
				resultModel.setActualReleaseManDayBD(new BigDecimal(resultModel.getActualReleaseManDay()).setScale(1, BigDecimal.ROUND_HALF_DOWN));
				System.out.println("The released day is : " + resultModel.getActualReleaseManDay());
				resultModel.setActualReleaseManDayString(resultModel.getActualReleaseManDayBD().toString());
				System.out.println("The actual released day is : " + resultModel.getActualReleaseManDayBD());
				if(resultModel.getActualReleaseManDay() == 0 || resultModel.getActualReleaseManDay() == 0.0){
					resultModel.setActualReleaseManDayBD(new BigDecimal(0).setScale(1));
					resultModel.setActualReleaseManDayString(resultModel.getActualReleaseManDayBD().toString());
				}
			}	
			else{
				resultModel.setActualReleaseManDayBD(new BigDecimal(0).setScale(1));
				resultModel.setActualReleaseManDayString(resultModel.getActualReleaseManDayBD().toString());
			}
		}
		
		DatatableResponseModel<TableList> response = new DatatableResponseModel<TableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = dao.countTableList("", null, null, fromUserPermissionId, supervisorIds, statuses);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = dao.countTableList(search, referenceMonthDate, fromUserId, fromUserPermissionId, supervisorIds, statuses);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query for recommend
	 */
	public DatatableResponseModel<TableList> getTableListForRecommend(DatatableRequestModel model,
			String referenceMonth, Integer fromUserId, List<Integer> submitToUserIds,
			String[] statuses) throws Exception {

		Order order = this.getOrder(model, "fromFieldOfficer", "targetFieldOfficer", "targetReleaseManDay", "actualReleaseManDay", "status");
		
		String search = model.getSearch().get("value");
		Date referenceMonthDate = null;
		if (!StringUtils.isEmpty(referenceMonth)) {
			referenceMonthDate = commonService.getMonth(referenceMonth);
		}
		
		List<TableList> result = dao.getTableListForRecommend(search, model.getStart(), model.getLength(), order,
				referenceMonthDate, fromUserId, submitToUserIds,
				statuses);
		
		for(TableList resultModel : result){
			if(resultModel.getActualReleaseManDay() != null){
				resultModel.setActualReleaseManDayBD(new BigDecimal(resultModel.getActualReleaseManDay()).setScale(1, BigDecimal.ROUND_HALF_DOWN));
				resultModel.setActualReleaseManDayString(resultModel.getActualReleaseManDayBD().toString());
				if(resultModel.getActualReleaseManDay() == 0 || resultModel.getActualReleaseManDay() == 0.0){
					resultModel.setActualReleaseManDayBD(new BigDecimal(0).setScale(1));
					resultModel.setActualReleaseManDayString(resultModel.getActualReleaseManDayBD().toString());
				}
			}	
			else{
				resultModel.setActualReleaseManDayBD(new BigDecimal(0).setScale(1));
				resultModel.setActualReleaseManDayString(resultModel.getActualReleaseManDayBD().toString());
			}
		}
		
		DatatableResponseModel<TableList> response = new DatatableResponseModel<TableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = dao.countTableListForRecommend("", null, null, submitToUserIds, statuses);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = dao.countTableListForRecommend(search, referenceMonthDate, fromUserId, submitToUserIds, statuses);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query for approve
	 */
	public DatatableResponseModel<TableList> getTableListForApproval(DatatableRequestModel model,
			String referenceMonth, Integer fromUserId, List<Integer> submitToUserIds,
			String[] statuses) throws Exception {

		Order order = this.getOrder(model, "fromFieldOfficer", "targetFieldOfficer", "targetReleaseManDay", "actualReleaseManDay", "status");
		
		String search = model.getSearch().get("value");
		Date referenceMonthDate = null;
		if (!StringUtils.isEmpty(referenceMonth)) {
			referenceMonthDate = commonService.getMonth(referenceMonth);
		}
		
		List<TableList> result = dao.getTableListForApproval(search, model.getStart(), model.getLength(), order,
				referenceMonthDate, fromUserId, submitToUserIds,
				statuses);
		
		for(TableList resultModel : result){
			if (resultModel.getActualReleaseManDay() != null || resultModel.getActualReleaseManDay() != 0 || resultModel.getActualReleaseManDay() != 0.0){
				resultModel.setActualReleaseManDayBD(new BigDecimal(resultModel.getActualReleaseManDay()).setScale(1, BigDecimal.ROUND_HALF_DOWN));
				System.out.println("The actual release man day is: " + resultModel.getActualReleaseManDayBD());
				resultModel.setActualReleaseManDayString(resultModel.getActualReleaseManDayBD().toString());
			}
			else{
				resultModel.setActualReleaseManDayBD(new BigDecimal(0).setScale(1));
				resultModel.setActualReleaseManDayString(resultModel.getActualReleaseManDayBD().toString());
			}
		}
		
		DatatableResponseModel<TableList> response = new DatatableResponseModel<TableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = dao.countTableListForApproval("", null, null, submitToUserIds, statuses);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = dao.countTableListForApproval(search, referenceMonthDate, fromUserId, submitToUserIds, statuses);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Prepare view model
	 */
	public ViewModel prepareViewModel(int id, int userId, List<Integer> selectedAssignmentIds) {
		ViewModel model = new ViewModel();
		AssignmentAdjustment entity = dao.findById(id);
		if (entity == null) return null;
		
		BeanUtils.copyProperties(entity, model);
		
		List<Integer> excludeAssignmentIds = assignmentDao.getAssignmentOfSameAllocationBatch(entity.getAllocationBatch().getId());
		if (excludeAssignmentIds.size() > 0 && excludeAssignmentIds.get(0) == null) {
			excludeAssignmentIds = new ArrayList<Integer>();
		}
		
		User toUser = entity.getToUser();
		model.setTargetFieldOfficer(toUser.getStaffCode() + " - " + (StringUtils.isBlank(toUser.getChineseName()) ? toUser.getEnglishName():toUser.getChineseName()));
		
		User approver = entity.getSubmitToRecommend();
		if (approver != null)
			model.setSubmitToApproveUserId(approver.getId());
		
		if (entity.getAllocationBatch() != null){
			model.setTotalAssignments(assignmentDao.countAssignmentByAllocationBatchIdUserId(entity.getFromUser().getId(), entity.getAllocationBatch().getId(), selectedAssignmentIds, excludeAssignmentIds));
			model.setTotalQuotations(assignmentDao.countQuotationRecordsByAllocationBatchIdUserId(entity.getFromUser().getId(), entity.getAllocationBatch().getId(), selectedAssignmentIds, excludeAssignmentIds));
		} else {
			model.setTotalAssignments(0L);
			model.setTotalQuotations(0L);
		}
		
		if (entity.getSubmitToRecommend() == null) {
			User user = userDao.findById(userId);
			if (user.getSupervisor() != null){
				model.setPreSelectSupervisorId(user.getSupervisor().getId());
			}			
		} else {
			model.setPreSelectSupervisorId(entity.getSubmitToRecommend().getId());
		}
		
		List<User> users = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR, null);
		model.setUsersForSelection(users);
		
		model.setRejectReason(commonService.nl2br(entity.getRejectReason()));
		
		return model;
	}
	
	/**
	 * Prepare view model for recommend
	 */
	public ViewModel prepareViewModelForRecommend(int id, int userId) {
		ViewModel model = new ViewModel();
		AssignmentAdjustment entity = dao.findById(id);
		if (entity == null) return null;
		
		User fromUser = entity.getFromUser();
		User toUser = entity.getToUser();
		
		DistrictHeadAdjustment districtHeadAdjustment = districtHeadAdjustmentDao.findDistrictHeadAdjustmentByAllocationBatchIdUserId(entity.getAllocationBatch().getId(), fromUser.getId());
		if (districtHeadAdjustment == null) return null;
		
		BeanUtils.copyProperties(entity, model);
		
		model.setFromFieldOfficer(fromUser.getStaffCode() + " - " + (StringUtils.isBlank(fromUser.getChineseName()) ? fromUser.getEnglishName() : fromUser.getChineseName()));
		model.setTargetFieldOfficer(toUser.getStaffCode() + " - " + (StringUtils.isBlank(toUser.getChineseName()) ? toUser.getEnglishName() : toUser.getChineseName()));
		
		User approver = entity.getSubmitToApprove();
		if (approver != null)
			model.setSubmitToApproveUserId(approver.getId());
		
		if (entity.getSubmitToApprove() == null) {
			User user = userDao.findById(userId);
			if (user.getSupervisor() != null){
				model.setPreSelectSupervisorId(user.getSupervisor().getId());
			}
		} else {
			model.setPreSelectSupervisorId(entity.getSubmitToApprove().getId());
		}
		
		Set<User> users = new HashSet<User>();
		users.addAll(userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD, null));
		users.addAll(userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD, null));
		
		model.setUsersForSelection(new ArrayList<User>(users));
		
		SelectedSessionModel sessionModel = new SelectedSessionModel();
		sessionModel.setId(model.getId());
		prepareSelectedSessionModel(sessionModel);
		model.setSelectedAssignments(sessionModel.getSelectedAssignments());
		model.setSelectedQuotations(sessionModel.getSelectedQuotations());
		model.setActualReleaseManDays(sessionModel.getActualReleaseManDaysBD().doubleValue());
		model.setActualReleaseManDaysBD(sessionModel.getActualReleaseManDaysBD());
		
		if (entity.getAllocationBatch() != null){
			model.setTotalAssignments(assignmentDao.countAssignmentByAllocationBatchIdUserId(fromUser.getId(), entity.getAllocationBatch().getId()));
			model.setTotalQuotations(assignmentDao.countQuotationRecordsByAllocationBatchIdUserId(fromUser.getId(), entity.getAllocationBatch().getId()));

			Double officerAvailableManDay = calendarEventDao.getUserAvaliableManDayBetween(entity.getAllocationBatch().getStartDate(), entity.getAllocationBatch().getEndDate(), fromUser.getId());
			Double actualReleaseManDay = model.getActualReleaseManDays() != null ? model.getActualReleaseManDays() : 0.0 ;
			
			Double temp = officerAvailableManDay - districtHeadAdjustment.getAdjustedManDay() + actualReleaseManDay;
			//model.setResultantManDayBalance(new BigDecimal(Math.round((officerAvailableManDay - districtHeadAdjustment.getAdjustedManDay() + actualReleaseManDay)*10000.0) / 10000.0));
			model.setResultantManDayBalanceBD(new BigDecimal(temp).setScale(1, BigDecimal.ROUND_HALF_DOWN));
			System.out.println("The resultant man day balance is : " + model.getResultantManDayBalanceBD());
		} else {
			model.setTotalAssignments(0L);
			model.setTotalQuotations(0L);
		}
			
		return model;
	}
	
	/**
	 * Prepare selected session model
	 */
	public void prepareSelectedSessionModel(SelectedSessionModel model) {
		model.setSelectedAssignments(0L);
		model.setSelectedQuotations(0L);
		model.setActualReleaseManDays(new BigDecimal(0).setScale(4).doubleValue());
		model.setAssignments(new ArrayList<SelectedAssignmentModel>());
		
		AssignmentAdjustment entity = dao.findById(model.getId());
		if (entity == null) return;
		
		List<Integer> selectedAssignmentIds = new ArrayList<Integer>();
		for (Assignment assignment : entity.getAssignments()) {
			selectedAssignmentIds.add(assignment.getId());
		}
		
		if (selectedAssignmentIds.size() > 0) {
			List<SelectedAssignmentModel> assignments = assignmentDao.getAssignmentsForMaintenance(selectedAssignmentIds);
			for (SelectedAssignmentModel assignment : assignments) {
				assignment.setRequiredManDay(new BigDecimal(assignment.getFullSeasonLoading() + Math.max(assignment.getSummerLoading(), assignment.getWinterLoading())).setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue());
				assignment.setRequiredManDayBD(new BigDecimal(assignment.getFullSeasonLoading() + Math.max(assignment.getSummerLoading(), assignment.getWinterLoading())).setScale(4, BigDecimal.ROUND_HALF_DOWN));
			}
			model.setAssignments(assignments);
		}
		prepareSelectedSessionModelCalculateMetric(model);
	}
	
	public void prepareSelectedSessionModelCalculateMetric(SelectedSessionModel model) {
		if (model.getAssignments().size() > 0) {
			List<Integer> selectedAssignmentIds = new ArrayList<Integer>();
			double actualReleaseManDays = 0;
			for (SelectedAssignmentModel assignment : model.getAssignments()) {
				assignment.setRequiredManDay(new BigDecimal(assignment.getFullSeasonLoading() + Math.max(assignment.getSummerLoading(), assignment.getWinterLoading())).setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue());
				selectedAssignmentIds.add(assignment.getId());
				actualReleaseManDays += assignment.getRequiredManDay() == null ? 0 : assignment.getRequiredManDay();
			}
			model.setSelectedAssignments(new Long(selectedAssignmentIds.size()));
			model.setSelectedQuotations(assignmentDao.countQuotations(selectedAssignmentIds));
			model.setActualReleaseManDays(actualReleaseManDays);
			model.setActualReleaseManDaysBD(new BigDecimal(actualReleaseManDays).setScale(4, BigDecimal.ROUND_HALF_DOWN));
		} else {
			model.setSelectedAssignments(0L);
			model.setSelectedQuotations(0L);
			model.setActualReleaseManDays(0D);
			model.setActualReleaseManDaysBD(new BigDecimal(0).setScale(4));
		}
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<SelectedAssignmentModel> getAssignmentTableList(DatatableRequestModel model,
			int assignmentAdjustmentId,
			Integer[] tpuId, String outletTypeId, Integer districtId, Integer batchId, List<Integer> selectedAssignmentIds) throws Exception {

		Order order = this.getOrder(model, "", "referenceNo", "batchCode", "firm", "district", "tpu", "address",
				"startDate", "endDate", "noOfQuotation", "requiredManDay");

		String search = model.getSearch().get("value");

		int allocationBatchId = 0;
		Integer fromUserId = null;
		AssignmentAdjustment entity = dao.findById(assignmentAdjustmentId);
		if (entity.getAllocationBatch() != null)
			allocationBatchId = entity.getAllocationBatch().getId();
		fromUserId = entity.getFromUser().getId();
		
		List<Integer> excludeAssignmentIds = assignmentDao.getAssignmentOfSameAllocationBatch(entity.getAllocationBatch().getId());
		if (excludeAssignmentIds.size() > 0 && excludeAssignmentIds.get(0) == null) {
			excludeAssignmentIds = new ArrayList<Integer>();
		}
		
		List<SelectedAssignmentModel> result = assignmentDao.getTransferInOutMaintenanceTableList(search, model.getStart(), model.getLength(), order,
				null,
				allocationBatchId, fromUserId,
				tpuId, outletTypeId, districtId, batchId, selectedAssignmentIds);
		for(SelectedAssignmentModel x : result){
			if(x.getRequiredManDay() != null){
			x.setRequiredManDayBD(new BigDecimal(x.getRequiredManDay()).setScale(4, BigDecimal.ROUND_HALF_DOWN));
			x.setRequiredManDayString(x.getRequiredManDayBD().toString());
			}
			else{
				x.setRequiredManDayBD(new BigDecimal(0).setScale(4));
				x.setRequiredManDayString(x.getRequiredManDayBD().toString());
			}
		}
		
		DatatableResponseModel<SelectedAssignmentModel> response = new DatatableResponseModel<SelectedAssignmentModel>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentDao.countTransferInOutMaintenanceTableList("", null, allocationBatchId, fromUserId, null, null, null, null, selectedAssignmentIds);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countTransferInOutMaintenanceTableList(search, null, allocationBatchId, fromUserId,
				tpuId, outletTypeId, districtId, batchId, selectedAssignmentIds);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * Assignment select all
	 */
	public List<Integer> getAssignmentTableSelectAll(String search,
			int assignmentAdjustmentId,
			Integer[] tpuId, String outletTypeId, Integer districtId, Integer batchId){

		int allocationBatchId = 0;
		Integer fromUserId = null;
		AssignmentAdjustment entity = dao.findById(assignmentAdjustmentId);
		if (entity.getAllocationBatch() != null)
			allocationBatchId = entity.getAllocationBatch().getId();
		fromUserId = entity.getFromUser().getId();
		
		List<Integer> excludeAssignmentIds = assignmentDao.getAssignmentOfSameAllocationBatch(entity.getAllocationBatch().getId());
		if (excludeAssignmentIds.size() > 0 && excludeAssignmentIds.get(0) == null) {
			excludeAssignmentIds = new ArrayList<Integer>();
		}
		
		return assignmentDao.getTransferInOutMaintenanceTableSelectAll(search, excludeAssignmentIds, allocationBatchId, fromUserId,
				tpuId, outletTypeId, districtId, batchId);
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<RecommendAssignmentModel> getRecommendAssignmentTableList(DatatableRequestModel model,
			int assignmentAdjustmentId,
			Integer[] tpuId, String[] outletTypeId, Integer districtId, Integer batchId, boolean isRecommend) throws Exception {

		Order order = this.getOrder(model, "firm", "district", "tpu", "address",
				"startDate", "endDate", "noOfQuotation", "requiredManDay", "selected");

		String search = model.getSearch().get("value");

		int allocationBatchId = 0;
		Integer fromUserId = null;
		AssignmentAdjustment entity = dao.findById(assignmentAdjustmentId);
		if (entity.getAllocationBatch() != null)
			allocationBatchId = entity.getAllocationBatch().getId();
		fromUserId = entity.getFromUser().getId();
		
		List<RecommendAssignmentModel> result = assignmentDao.getTransferInOutRecommendTableList(search, model.getStart(), model.getLength(), order,
				allocationBatchId, fromUserId, assignmentAdjustmentId,
				tpuId, outletTypeId, districtId, batchId, isRecommend);
		
		DatatableResponseModel<RecommendAssignmentModel> response = new DatatableResponseModel<RecommendAssignmentModel>();
		
		for (RecommendAssignmentModel assignment : result) {
			assignment.setRequiredManDayBD(new BigDecimal(assignment.getFullSeasonLoading() + Math.max(assignment.getSummerLoading(), assignment.getWinterLoading())).setScale(4, BigDecimal.ROUND_HALF_DOWN));
			assignment.setRequiredManDayString(assignment.getRequiredManDayBD().toString());
		}
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentDao.countTransferInOutRecommendTableList("", allocationBatchId, fromUserId, assignmentAdjustmentId, null, null, null, null, isRecommend);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countTransferInOutRecommendTableList(search, allocationBatchId, fromUserId, assignmentAdjustmentId,
				tpuId, outletTypeId, districtId, batchId, isRecommend);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * Add assignments to model
	 */
	public void addAssignmentsToModel(SelectedSessionModel model, List<Integer> ids) {
		List<Integer> selectedAssignmentIds = new ArrayList<Integer>();
		for (SelectedAssignmentModel assignment : model.getAssignments()) {
			selectedAssignmentIds.add(assignment.getId());
		}
		
		Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(ids, selectedAssignmentIds);
		List<SelectedAssignmentModel> assignments = assignmentDao.getAssignmentsForMaintenance(new ArrayList<Integer>(newIds));
		
		for (SelectedAssignmentModel assignment : assignments) {
			assignment.setRequiredManDay(new BigDecimal(assignment.getFullSeasonLoading() + Math.max(assignment.getSummerLoading(), assignment.getWinterLoading())).setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue());
			assignment.setRequiredManDayBD(new BigDecimal(assignment.getFullSeasonLoading() + Math.max(assignment.getSummerLoading(), assignment.getWinterLoading())).setScale(4, BigDecimal.ROUND_HALF_DOWN));
			selectedAssignmentIds.add(assignment.getId());
		}
		
		model.getAssignments().addAll(assignments);
		prepareSelectedSessionModelCalculateMetric(model);
	}
	
	/**
	 * Add assignments to model
	 */
	public SelectedSessionModel calculateMetricForAssignmentSelectionPopup(SelectedSessionModel model, List<Integer> ids) {
		SelectedSessionModel newCalculatedModel = new SelectedSessionModel();
		newCalculatedModel.setAssignments(new ArrayList<SelectedAssignmentModel>(model.getAssignments()));
		
		if (ids != null && ids.size() > 0) {
			List<Integer> selectedAssignmentIds = new ArrayList<Integer>();
			for (SelectedAssignmentModel assignment : model.getAssignments()) {
				selectedAssignmentIds.add(assignment.getId());
			}
			
			Collection<Integer> newIds = (Collection<Integer>)CollectionUtils.subtract(ids, selectedAssignmentIds);
			List<SelectedAssignmentModel> assignments = assignmentDao.getAssignmentsForMaintenance(new ArrayList<Integer>(newIds));
			
			for (SelectedAssignmentModel assignment: assignments) {
				assignment.setRequiredManDay(new BigDecimal(assignment.getFullSeasonLoading() + Math.max(assignment.getSummerLoading(), assignment.getWinterLoading())).setScale(4, BigDecimal.ROUND_HALF_DOWN).doubleValue());
			}
			
			newCalculatedModel.getAssignments().addAll(assignments);
		}
		
		prepareSelectedSessionModelCalculateMetric(newCalculatedModel);
		
		return newCalculatedModel;
	}
	
	/**
	 * Delete assignment from model
	 */
	public void deleteAssignmentFromModel(SelectedSessionModel model, Integer id) {
		Iterator<SelectedAssignmentModel> iter = model.getAssignments().iterator();
		while (iter.hasNext()) {
			if (iter.next().getId().intValue() == id) {
				iter.remove();
			}
		}
		prepareSelectedSessionModelCalculateMetric(model);
	}

	/**
	 * Submit
	 */
	@Transactional
	public void submit(PostModel postModel, SelectedSessionModel model) throws Exception {
		AssignmentAdjustment entity = dao.findById(model.getId());
		if (entity == null) throw new ServiceException("E00014");
		
		entity.setActualManDay(model.getActualReleaseManDays().doubleValue());
		entity.setStatus("Submitted");
		
		User submitToRecommendUser = userDao.findById(postModel.getSubmitToApproveUserId());
		entity.setSubmitToRecommend(submitToRecommendUser);
		
//		for (Assignment oldAssignment : entity.getAssignments()) {
//			for (QuotationRecord record : oldAssignment.getQuotationRecords()) {
//				record.setSpecifiedUser(false);
//			}
//		}
		
		entity.getAssignments().clear();
		for (SelectedAssignmentModel assignmentModel : model.getAssignments()) {
			Assignment assignment = assignmentDao.findById(assignmentModel.getId());
			entity.getAssignments().add(assignment);

			for (QuotationRecord record : assignment.getQuotationRecords()) {
				record.setSpecifiedUser(true);
			}
		}
		
		//List<User> notifyUserList = getNotifyUserList(entity, SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_MAINTENANCE);
		//createNotification(entity, notifyUserList, false, SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_MAINTENANCE);
		
		dao.save(entity);
		dao.flush();
	}
	
	/**
	 * Create notification
	 */
	public void createNotification(AssignmentAdjustment entity, List<User> users, boolean withPush, String state) {
		
		int noOfAssignments = (entity.getAssignments() != null) ? entity.getAssignments().size() : 0;
		double manDays = entity.getActualManDay(); 
		
		String fromOfficerCode = entity.getFromUser().getStaffCode();
		String fromOfficerName = entity.getFromUser().getEnglishName();
		String fromOfficerTeam = StringUtils.isEmpty(entity.getFromUser().getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : entity.getFromUser().getTeam();
		
		String toOfficerCode = entity.getToUser().getStaffCode();
		String toOfficerName = entity.getToUser().getEnglishName();
		String toOfficerTeam = StringUtils.isEmpty(entity.getToUser().getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : entity.getToUser().getTeam();
		
		String messageCode = "";
		
		//{0} = No. of assignments
		//{1} = No. of man days
		//{2} = from officer code
		//{3} = from officer name
		//{4} = from officer's team
		//{5} = to officer code (Field officer to receive the assignments)
		//{6} = to officer name (Field officer to receive the assignments)
		//{7} = to officer's team (Field officer to receive the assignments)
		
		switch(state){
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_MAINTENANCE: 
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_RECOMMENDATION:
				//N00072 = {2} - {3} (Team {4}) has transferred out {0} assignments (in total {1} man-day) to {5} - {6} (Team {7}).
				messageCode = "N00072"; 
				break;
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_APPROVAL:
				//N00073 = The transfer-in/out {0} assignments (in total {1} man-day) from {2} - {3} (Team {4}) to {5} - {6} (Team {7}) has been approved.
				messageCode = "N00073";
				break;
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_REJECT_RECOMMENDATION:
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_REJECT_APPROVAL:
				//N00074 = The transfer-in/out {0} assignments (in total {1} man-day) from {2} - {3} (Team {4}) to {5} - {6} (Team {7}) has been rejected.
				messageCode = "N00074";
				break;
			default: break;
		}
		
		String subject = "Transfer Assignment Out";
		String content = messageSource.getMessage(messageCode, new Object[]{noOfAssignments, manDays, fromOfficerCode, 
				fromOfficerName, fromOfficerTeam, toOfficerCode, toOfficerName, toOfficerTeam}, Locale.ENGLISH);
		
		for (User user : users) {
			notifyService.sendNotification(user, subject, content, withPush);
		}
	}
	
	/**
	 * Get notify user list
	 */
	public List<User> getNotifyUserList(AssignmentAdjustment entity, String state) {
		List<User> list = new ArrayList<User>();
		List<Integer> ids = new ArrayList<Integer>();
		
		switch(state){
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_MAINTENANCE:
				list.add(entity.getSubmitToRecommend());
				ids.add(entity.getSubmitToRecommend().getId());
				
				if (entity.getToUser().getSupervisor() != null &&  !ids.contains(entity.getToUser().getSupervisor().getUserId())) {
					list.add(entity.getToUser().getSupervisor());
				}
				break;
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_RECOMMENDATION:
				list.add(entity.getSubmitToApprove());
				break;
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_APPROVAL:
				list.add(entity.getFromUser());
				ids.add(entity.getFromUser().getId());
				
				if (!ids.contains(entity.getToUser().getUserId())) {
					list.add(entity.getToUser());
				}
//				if (!ids.contains(entity.getSubmitToRecommend().getId())) {
//					list.add(entity.getSubmitToRecommend());
//					ids.add(entity.getSubmitToRecommend().getId());
//				}
//				if (entity.getToUser().getSupervisor() != null &&  !ids.contains(entity.getToUser().getSupervisor().getUserId())) {
//					list.add(entity.getToUser().getSupervisor());
//				}
				break;
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_REJECT_RECOMMENDATION:
				list.add(entity.getFromUser());
//				ids.add(entity.getFromUser().getId());
//				
//				if (entity.getToUser().getSupervisor() != null &&  !ids.contains(entity.getToUser().getSupervisor().getUserId())) {
//					list.add(entity.getToUser().getSupervisor());
//				}
				break;
			case SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_REJECT_APPROVAL:
				list.add(entity.getFromUser());
//				ids.add(entity.getFromUser().getId());
//				
//				if (entity.getToUser().getSupervisor() != null &&  !ids.contains(entity.getToUser().getSupervisor().getUserId())) {
//					list.add(entity.getToUser().getSupervisor());
//					ids.add(entity.getToUser().getSupervisor().getId());
//				}
//				if (!ids.contains(entity.getSubmitToRecommend().getId())) {
//					list.add(entity.getSubmitToRecommend());
//				}
				break;
			default: break;
		}
		
		return list;
	}
	
	/**
	 * Recommend
	 */
	@Transactional
	public void recommend(PostModel postModel) throws Exception {
		AssignmentAdjustment entity = dao.findById(postModel.getAssignmentAdjustmentId());
		if (entity == null) throw new ServiceException("E00014");
		
		User submitToApproveUser = userDao.findById(postModel.getSubmitToApproveUserId());
		entity.setSubmitToApprove(submitToApproveUser);
		
		List<User> notifyUserList = null;
		String state = null;
		
		if (postModel.getBtnAction().equals("Reject")) {
			state = SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_REJECT_RECOMMENDATION;
			String reason = postModel.getRejectReason();
			if (reason != null) {
				reason += " ";
			}
			String rejectUserName = entity.getSubmitToRecommend().getEnglishName();
			String rejectDate = commonService.formatDate(new Date());
			String reasonBy = messageSource.getMessage("I00011", new Object[]{rejectUserName, rejectDate}, Locale.ENGLISH);
			reason += "\n" + reasonBy;
			
			entity.setRejectReason(reason);
			entity.setStatus("Rejected");
			for (Assignment assignment : entity.getAssignments()) {
				for (QuotationRecord record : assignment.getQuotationRecords()) {
					record.setSpecifiedUser(false);
				}
			}
			notifyUserList = getNotifyUserList(entity, state);
			
		} else {
			state = SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_RECOMMENDATION;
			entity.setStatus("Recommended");
			entity.setRecommendDate(new Date());
			notifyUserList = getNotifyUserList(entity, state);
		}
		
		if(notifyUserList != null && notifyUserList.size() > 0 && state != null){
			createNotification(entity, notifyUserList, true, state);
		}
		
		dao.save(entity);
		dao.flush();
	}
	
	/**
	 * Approve
	 */
	@Transactional
	public void approve(PostModel postModel) throws Exception {
		AssignmentAdjustment entity = dao.findById(postModel.getAssignmentAdjustmentId());
		if (entity == null) throw new ServiceException("E00014");
		
		List<User> notifyUserList = null;
		String state = null;
		if (postModel.getBtnAction().equals("Reject")) {
			state = SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_REJECT_APPROVAL;
			String reason = postModel.getRejectReason();
			if (reason != null) {
				reason += " ";
			}
			String rejectUserName = entity.getSubmitToApprove().getEnglishName();
			String rejectDate = commonService.formatDate(new Date());
			String reasonBy = messageSource.getMessage("I00011", new Object[]{rejectUserName, rejectDate}, Locale.ENGLISH);
			reason += "\n" + reasonBy;
			
			entity.setRejectReason(reason);
			entity.setStatus("Rejected");
			for (Assignment assignment : entity.getAssignments()) {
				for (QuotationRecord record : assignment.getQuotationRecords()) {
					record.setSpecifiedUser(false);
				}
			}
			notifyUserList = getNotifyUserList(entity, state);
		} else {
			state = SystemConstant.ASSIGNMENTTRANSFERINOUT_STATE_APPROVAL;
			entity.setStatus("Approved");
			entity.setApprovedDate(new Date());
			
			User toUser = entity.getToUser();
			for (Assignment assignment : entity.getAssignments()) {
				assignment.setUser(toUser);
				assignment.setAssignedUser(toUser);
				for (QuotationRecord record : assignment.getQuotationRecords()) {
					record.setUser(toUser);
					record.setReleased(true);
				}
			}
			notifyUserList = getNotifyUserList(entity, state);
		}
		
		if(notifyUserList != null && notifyUserList.size() > 0 && state != null){
			createNotification(entity, notifyUserList, true, state);
		}
		
		dao.save(entity);
		dao.flush();
		
		//peService.adjustPETask(entity.getAllocationBatch().getSurveyMonth().getSurveyMonthId());
		// check peCheckTask Certainty case has not generate
		if(peTaskDao.countSelectedCertaintyCase(entity.getAllocationBatch().getSurveyMonth().getSurveyMonthId())>0){
			peService.adjustPETask(entity.getAllocationBatch().getSurveyMonth().getSurveyMonthId());
		}
	}
}
