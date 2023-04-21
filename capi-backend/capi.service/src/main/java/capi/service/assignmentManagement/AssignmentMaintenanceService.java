package capi.service.assignmentManagement;

import java.io.File;
import java.io.InputStream;
import java.security.SecureRandom;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.transaction.SystemException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.ActingDao;
import capi.dal.AssignmentAttributeDao;
import capi.dal.AssignmentDao;
import capi.dal.AssignmentReallocationDao;
import capi.dal.AssignmentUnitCategoryInfoDao;
import capi.dal.BatchCollectionDateDao;
import capi.dal.DistrictDao;
import capi.dal.OutletDao;
import capi.dal.PECheckFormDao;
import capi.dal.PECheckTaskDao;
import capi.dal.PointToNoteDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.SubPriceColumnDao;
import capi.dal.SubPriceRecordDao;
import capi.dal.SupervisoryVisitDetailDao;
import capi.dal.SurveyMonthDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.TimeLogDao;
import capi.dal.TourRecordDao;
import capi.dal.TpuDao;
import capi.dal.UnitCategoryInfoDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.Assignment;
import capi.entity.AssignmentReallocation;
import capi.entity.AssignmentUnitCategoryInfo;
import capi.entity.District;
import capi.entity.ItineraryPlan;
import capi.entity.Outlet;
import capi.entity.PECheckTask;
import capi.entity.PointToNote;
import capi.entity.QuotationRecord;
import capi.entity.SupervisoryVisitDetail;
import capi.entity.SurveyMonth;
import capi.entity.SystemConfiguration;
import capi.entity.Tpu;
import capi.entity.UnitCategoryInfo;
import capi.entity.User;
import capi.entity.VwOutletTypeShortForm;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.api.dataSync.AssignmentAttributeSyncData;
import capi.model.api.dataSync.AssignmentSyncData;
import capi.model.api.dataSync.AssignmentUnitCategoryInfoSyncData;
import capi.model.api.dataSync.BatchCollectionDateSyncData;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.QuotationRecordSyncData;
import capi.model.api.dataSync.QuotationSyncData;
import capi.model.api.dataSync.SubPriceColumnSyncData;
import capi.model.api.dataSync.SubPriceRecordSyncData;
import capi.model.api.dataSync.TourRecordSyncData;
import capi.model.api.dataSync.UnitCategoryInfoSyncData;
import capi.model.api.onlineFunction.NonScheduleAssignmentDetail;
import capi.model.api.onlineFunction.NonScheduleAssignmentDetailList;
import capi.model.api.onlineFunction.NonScheduleAssignmentList;
import capi.model.api.onlineFunction.QuotationOnlineModel;
import capi.model.assignmentManagement.QuotationRecordHistoryDateModel;
import capi.model.assignmentManagement.assignmentManagement.AssignmentUnitCategoryInfoWithVerify;
import capi.model.assignmentManagement.assignmentManagement.AssignmentViewModel;
import capi.model.assignmentManagement.assignmentManagement.BackTrackDateModel;
import capi.model.assignmentManagement.assignmentManagement.EditModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordCountByTabModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordPageViewModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList;
import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import capi.model.assignmentManagement.assignmentManagement.TableList;
import capi.model.assignmentManagement.assignmentManagement.VerificationTypeGroupModel;
import capi.model.commonLookup.ReportAssignmentLookupList;
import capi.model.shared.quotationRecord.OutletPostModel;
import capi.model.shared.quotationRecord.OutletViewModel;
import capi.model.shared.quotationRecord.PagePostModel;
import capi.model.shared.quotationRecord.ProductPostModel;
import capi.model.shared.quotationRecord.QuotationRecordPostModel;
import capi.model.shared.quotationRecord.QuotationRecordViewModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.UserService;
import capi.service.masterMaintenance.OutletService;

import capi.model.api.onlineFunction.CheckAssignmentAndQuotationRecordStatus;

@Service("AssignmentMaintenanceService")
public class AssignmentMaintenanceService extends BaseService {
	@Autowired
	AssignmentDao assignmentDao;

	@Autowired
	SurveyMonthDao surveyMonthDao;

	@Autowired
	OutletDao outletDao;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	TpuDao tpuDao;
	
	@Autowired
	QuotationRecordDao quotationRecordDao;
	
	@Autowired
	UnitDao unitDao;
	
	@Autowired
	AssignmentUnitCategoryInfoDao assignmentUnitCategoryInfoDao;
	
	@Autowired
	UnitCategoryInfoDao unitCategoryInfoDao;

	@Autowired
	ActingDao actingDao;
	
	@Autowired
	TimeLogDao timeLogDao;
	
	@Autowired
	PECheckTaskDao peCheckTaskDao;
	
	@Autowired
	PECheckFormDao peCheckFormDao;
	
	@Autowired
	QuotationRecordService quotationRecordService;

	@Autowired
	AssignmentApprovalService assignmentApprovalService;
	
	@Autowired
	private OnSpotValidationService onSpotValidationService;
	
	@Autowired
	private QuotationRecordValidationService quotationRecordValidationService;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private SubPriceRecordDao subPriceRecordDao;
	
	@Autowired
	private SubPriceColumnDao subPriceColumnDao;
	
	@Autowired
	private TourRecordDao tourRecordDao;

	@Autowired
	private AssignmentAttributeDao assignmentAttributeDao;
	
	@Autowired
	private BatchCollectionDateDao batchCollectionDateDao;
	
	@Autowired
	private SystemConfigurationDao sysConfigDao;
	
	@Autowired
	CommonService commonService;

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private NotificationService  notifyService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private AssignmentReallocationDao assignmentReallocationDao;
	
	@Autowired
	private SupervisoryVisitDetailDao supervisoryVisitDetailDao;

	@Autowired
	private PointToNoteDao pointToNoteDao;
	
	@Autowired
	private VwOutletTypeShortFormDao vwOutletTypeShortFormDao;
	
	int maxChunkSize = 2000; // chunk size to divide
	
	/**
	 * Get survey month select2 format
	 */
	public Select2ResponseModel querySurveyMonthSelect2(Select2RequestModel queryModel, List<Integer> userIds) {
		queryModel.setRecordsPerPage(10);
		List<SurveyMonth> entities = surveyMonthDao.searchForAssignmentMaintenance(queryModel.getTerm(), userIds, queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = surveyMonthDao.countSearchForAssignmentMaintenance(queryModel.getTerm(), userIds);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (SurveyMonth d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d.getId()));
			item.setText(commonService.formatDate(d.getStartDate()) + " - " + commonService.formatDate(d.getEndDate()));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<TableList> getTableList(DatatableRequestModel model,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId, Integer assignmentStatus, String deadline, String[] outletTypeId, Integer[] districtId,
			boolean isBusinessAdmin, String quotationState) throws Exception {

		Order order = this.getOrder(model, "sm.referenceMonth", "referenceNo", "firm", "district", "tpu", "noOfQuotation",
				"personInCharge", "firmStatus", "assignmentStatus", "deadline2");
		
		String search = model.getSearch().get("value");
		
		List<TableList> result = assignmentDao.getAssignmentMaintenanceAssignmentTableList(search, model.getStart(), model.getLength(), order,
				userIds,
				personInChargeId, surveyMonthId, assignmentStatus, deadline, outletTypeId, districtId, isBusinessAdmin, quotationState);
		
		DatatableResponseModel<TableList> response = new DatatableResponseModel<TableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentDao.countAssignmentMaintenanceAssignmentTableList("", userIds, null, null, null, null, null, null, isBusinessAdmin, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countAssignmentMaintenanceAssignmentTableList(search, userIds,
				personInChargeId, surveyMonthId, assignmentStatus, deadline, outletTypeId, districtId, isBusinessAdmin, quotationState);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<TableList> getNewRecruitmentTableList(DatatableRequestModel model,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId, Integer assignmentStatus, String deadline, String[] outletTypeId) throws Exception {

		Order order = this.getOrder(model, "referenceNo", "firm", "district", "tpu", "noOfQuotation",
				"personInCharge", "firmStatus", "assignmentStatus", "deadline2");
		
		String search = model.getSearch().get("value");
		
		List<TableList> result = assignmentDao.getNewRecruitmentMaintenanceAssignmentTableList(search, model.getStart(), model.getLength(), order,
				userIds,
				personInChargeId, surveyMonthId, assignmentStatus, deadline, outletTypeId);
		
		DatatableResponseModel<TableList> response = new DatatableResponseModel<TableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentDao.countNewRecruitmentMaintenanceAssignmentTableList("", userIds, null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countNewRecruitmentMaintenanceAssignmentTableList(search, userIds,
				personInChargeId, surveyMonthId, assignmentStatus, deadline, outletTypeId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/**
	 * DataTable query for imported
	 */
	public DatatableResponseModel<TableList> getImportedTableList(DatatableRequestModel model,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId) throws Exception {

		Order order = this.getOrder(model, "sm.referenceMonth", "survey", "referenceNo", "firm", "district", "tpu", "address", "noOfForms",
				"personInCharge", "");
		
		String search = model.getSearch().get("value");
		
		List<TableList> result = assignmentDao.getImportedAssignmentMaintenanceAssignmentTableList(search, model.getStart(), model.getLength(), order,
				userIds,
				personInChargeId, surveyMonthId);
		
		DatatableResponseModel<TableList> response = new DatatableResponseModel<TableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentDao.countImportedAssignmentMaintenanceAssignmentTableList("", userIds, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countImportedAssignmentMaintenanceAssignmentTableList(search, userIds,
				personInChargeId, surveyMonthId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * Delete assignment
	 */
	@Transactional
	public boolean deleteAssignment(int id) {
		Assignment entity = assignmentDao.findById(id);
		if(entity==null)
			return false;
		assignmentDao.delete(entity);
		assignmentDao.flush();
		return true;
	}
	
	/**
	 * Delete assignment
	 */
	@Transactional
	public void deleteAssignment(Assignment entity) {
		int userId = 0;
		int surveyMonthId = 0;
		boolean isFieldTeamHead = false;
		boolean isSectionHead = false;
		boolean generatePECase = false;
		
		if(entity.getPeCheckForm() != null)
			this.peCheckFormDao.delete(entity.getPeCheckForm());
		
		// Check PE Case is not Selected
		// Edit 20210920 JM: Add checking for user id
		if(entity.getPeCheckTask() != null && entity.getPeCheckTask().isSelected()){
			if (entity.getUser() != null && entity.getUser().getUserId() != null) {
				userId = entity.getUser().getUserId();
			}
			surveyMonthId = entity.getSurveyMonth().getSurveyMonthId();
			isFieldTeamHead = entity.getPeCheckTask().isFieldTeamHead();
			isSectionHead = entity.getPeCheckTask().isSectionHead();
			generatePECase = true;
			this.peCheckTaskDao.delete(entity.getPeCheckTask());
		} else if (entity.getPeCheckTask() != null){
			this.peCheckTaskDao.delete(entity.getPeCheckTask());
		}
			
		for (ItineraryPlan ip : entity.getPlannedItinerary()){
			ip.getAssignments().remove(entity);
		}
		for (ItineraryPlan ip : entity.getUnplannedItinerary()){
			ip.getAssignments().remove(entity);
		}
		for (AssignmentUnitCategoryInfo auci : entity.getCategoryInfo()){
			auci.setAssignment(null);
		}
		
		List<AssignmentReallocation> assignmentReallocations = new ArrayList<AssignmentReallocation>(entity.getAssignmentReallocations());
		
		for(AssignmentReallocation arEntiy : assignmentReallocations){
			arEntiy.getAssignments().remove(entity);
			assignmentReallocationDao.save(arEntiy);
		}
		List<SupervisoryVisitDetail> svdList = this.supervisoryVisitDetailDao.getSupervisoryVisitDetailByAssingment(entity.getAssignmentId());
		for(SupervisoryVisitDetail svdEntity : svdList){
			this.supervisoryVisitDetailDao.delete(svdEntity);
		}
		
		// 2020-07-17: fix ReleasedAssignment FK constraint failed when deleting Assignment
		// test case: approve RUA leaving no other QR left in Assignment
		assignmentDao.deleteRelatedReleasedAssignments(entity.getAssignmentId());
		assignmentDao.delete(entity);
		
		// Edit 20210920 JM: Add checking for user id, if user id still 0, skip generatePECase
		if(generatePECase && userId != 0){
			List<Integer> assignmentIds = new ArrayList<Integer> ();
			List<PECheckTask> randomTaskAndForms = generateRandomCase(userId, surveyMonthId, 1);
			for (PECheckTask taskAndForm : randomTaskAndForms) {
				taskAndForm.setFieldTeamHead(isFieldTeamHead);
				taskAndForm.setSectionHead(isSectionHead);
				
				assignmentIds.add(taskAndForm.getAssignment().getAssignmentId());
			}
			
			if(assignmentIds.size() > 0) {
				// generate PE form if the whole assignment is approved (i.e. all quotation records in the assignment is approved)
				assignmentApprovalService.generatePEForm(assignmentIds);
			}
		}
		assignmentDao.flush();
	}
	
	/**
	 * Get outlet types display by outlet id
	 */
	public List<String> getOutletTypesDisplayByOutletId(int id) {
		Outlet entity = outletDao.findById(id);
		List<String> list = new ArrayList<String>();
		for (VwOutletTypeShortForm shortForm : entity.getOutletTypes()) {
			list.add(shortForm.getShortCode() + " - " + shortForm.getChineseName());
		}
		return list;
	}
	
	/**
	 * Prepare view model
	 */
	public EditModel prepareViewModel(int assignmentId, Integer userId) throws Exception {
		EditModel model = new EditModel();
		model.setAssignmentId(assignmentId);
		
		Assignment entity = assignmentDao.getByIdForMaintenance(assignmentId);
		User user = null;
		if (userId != null)
			user = userDao.findById(userId);
		else
			user = entity.getUser();

		if (user != null)
			model.setUserId(user.getUserId());
		
		if (entity.getOutletDiscountRemark() == null) {
			entity.setOutletDiscountRemark(entity.getOutlet().getDiscountRemark());
			entity.setModifiedDate(entity.getModifiedDate());
			entity.setByPassModifiedDate(true);
			assignmentDao.save(entity);
			assignmentDao.flush();
		}
		
		if (entity.getCategoryInfo() == null || entity.getCategoryInfo().size() == 0) {
			autoCreateAssignmentUnitCategory(entity);
		}
		
		SurveyMonth surveyMonth = entity.getSurveyMonth();
		model.setSurveyMonth(commonService.formatDate(surveyMonth.getStartDate()) + " to " + commonService.formatDate(surveyMonth.getEndDate()));
		
		HashSet<String> pointToNotes = new HashSet<String>();
		
		for (PointToNote note : entity.getOutlet().getPointToNotes()) {
			boolean isPointToNoteEffective = isPointToNoteEffective(note, entity.getCollectionDate());
			if (isPointToNoteEffective) pointToNotes.add(note.getNote());
		}

		List<PointToNote> selectAllNotes = pointToNoteDao.getBySelectAllOutlet();
		if (selectAllNotes.size() > 0) {
			for (PointToNote note : selectAllNotes) {
				boolean isPointToNoteEffective = isPointToNoteEffective(note, entity.getCollectionDate());
				if (isPointToNoteEffective) pointToNotes.add(note.getNote());
			}
		}
		model.setPointToNote(StringUtils.join(pointToNotes, ","));
		
		if (user != null)
			model.setPersonInCharge(user.getEnglishName());
		
		model.setOutletId(entity.getOutlet().getId());
		OutletViewModel outletModel = quotationRecordService.prepareOutletViewModel(entity.getOutlet());
		
		outletModel.setDiscountRemark(entity.getOutletDiscountRemark());
		
		model.setOutlet(outletModel);
		
		model.setModifiedDate(entity.getModifiedDate());
		model.setCreatedDate(entity.getCreatedDate());
		
		model.setLockFirmStatus(assignmentDao.getLockFirmStatusByAssignment(entity.getAssignmentId()));
		
		model.setNormalAssignmentStatus(entity.getStatus());
		
		if (model.getUserId() != null) {
			QuotationRecordCountByTabModel tabCountModel = quotationRecordDao.getQuotationRecordCountByTab(model.getAssignmentId(), model.getUserId());
			if (tabCountModel.getNormal() > 0) {
				model.setNormalAssignment(true);
				QuotationRecordCountByTabModel tabCountModel2 = quotationRecordDao.getQuotationRecordCountByTabAndOutlet(entity.getOutlet().getId(), model.getUserId());
				tabCountModel.setRevisit(tabCountModel2.getRevisit());
				tabCountModel.setVerify(tabCountModel2.getVerify());
				tabCountModel.setIp(tabCountModel2.getIp());
			}
			
			model.setCount(tabCountModel);
		} else {
			QuotationRecordCountByTabModel tabCountModel = new QuotationRecordCountByTabModel();
			tabCountModel.setNormal(quotationRecordDao.countByAssignment(model.getAssignmentId()));
			model.setNormalAssignment(true);
			model.setCount(tabCountModel);
		}
		
		return model;
	}
	
	public Boolean isPointToNoteEffective(PointToNote note, Date collectionDate) {
		boolean isEffective = false;
	
		boolean isEffDateEmpty = note.getEffectiveDate() == null;
		boolean isExpDateEmpty = note.getExpiryDate() == null;
		
		Date today = commonService.getDateWithoutTime(new Date());
		Date effDate = isEffDateEmpty ? null : commonService.getDateWithoutTime(note.getEffectiveDate());
		Date expDate = isExpDateEmpty ? null : commonService.getDateWithoutTime(note.getExpiryDate());

		Date tempDate = collectionDate == null ? today : commonService.getDateWithoutTime(collectionDate);
		
		if (!isEffDateEmpty && !isExpDateEmpty) {
			isEffective = tempDate.compareTo(effDate) >= 0 && tempDate.compareTo(expDate) <= 0 ;
		} else if (!isEffDateEmpty && isExpDateEmpty) {
			isEffective = tempDate.compareTo(effDate) >= 0;
		}
		
		return isEffective;
	}
	
	public void prefillOutletViewModelWithPost(OutletViewModel viewModel, OutletPostModel postModel) throws Exception {
		BeanUtils.copyProperties(postModel, viewModel);
		
		if (postModel.getTpuId() != null) {
			Tpu tpuEntity = tpuDao.findById(postModel.getTpuId());
			viewModel.setTpu(tpuEntity);
		}
		
		if (StringUtils.isEmpty(postModel.getOpeningStartTime()))
			viewModel.setOpeningStartTime(null);
		else
			viewModel.setOpeningStartTime(new Time(commonService.getTime(postModel.getOpeningStartTime()).getTime()));
		if (StringUtils.isEmpty(postModel.getOpeningEndTime()))
			viewModel.setOpeningEndTime(null);
		else
			viewModel.setOpeningEndTime(new Time(commonService.getTime(postModel.getOpeningEndTime()).getTime()));
		
		if (StringUtils.isEmpty(postModel.getOpeningStartTime2()))
			viewModel.setOpeningStartTime2(null);
		else
			viewModel.setOpeningStartTime2(new Time(commonService.getTime(postModel.getOpeningStartTime2()).getTime()));
		if (StringUtils.isEmpty(postModel.getOpeningEndTime2()))
			viewModel.setOpeningEndTime2(null);
		else
			viewModel.setOpeningEndTime2(new Time(commonService.getTime(postModel.getOpeningEndTime2()).getTime()));

		if (StringUtils.isEmpty(postModel.getConvenientStartTime()))
			viewModel.setConvenientStartTime(null);
		else
			viewModel.setConvenientStartTime(new Time(commonService.getTime(postModel.getConvenientStartTime()).getTime()));
		if (StringUtils.isEmpty(postModel.getConvenientEndTime()))
			viewModel.setConvenientEndTime(null);
		else
			viewModel.setConvenientEndTime(new Time(commonService.getTime(postModel.getConvenientEndTime()).getTime()));

		if (StringUtils.isEmpty(postModel.getConvenientStartTime2()))
			viewModel.setConvenientStartTime2(null);
		else
			viewModel.setConvenientStartTime2(new Time(commonService.getTime(postModel.getConvenientStartTime2()).getTime()));
		if (StringUtils.isEmpty(postModel.getConvenientEndTime2()))
			viewModel.setConvenientEndTime2(null);
		else
			viewModel.setConvenientEndTime2(new Time(commonService.getTime(postModel.getConvenientEndTime2()).getTime()));
	}
	
	/**
	 * Save outlet
	 */
	@Transactional
	public void saveOutlet(OutletPostModel model, SessionModel sessionModel, InputStream outletImageStream, String fileBaseLoc) throws Exception {
		String tabName = sessionModel.getTab();
		
		Outlet entity = outletDao.findById(model.getOutletId());
		
		copyOutletPostToEntity(model, entity);
		
//		if (outletImageStream != null) {
		if (!StringUtils.isEmpty(model.getIdenfityDel()) && model.getIdenfityDel().equals("del")){
			if (entity.getOutletImagePath() != null) {
				outletService.deleteOutletImage(entity.getOutletImagePath(), fileBaseLoc);
				entity.setOutletImagePath(null);
				entity.setImageModifiedTime(null);
			}
		}
		if (outletImageStream != null) {
			String outletImagePath = outletService.saveOutletImage(outletImageStream, fileBaseLoc, entity.getFirmCode());
			entity.setOutletImagePath(outletImagePath);
			entity.setImageModifiedTime(new java.util.Date());
		} else {
			//Click remove button and no upload new photo
			deleteOutletImage(entity.getOutletImagePath(), fileBaseLoc);
			entity.setOutletImagePath(null);
			entity.setImageModifiedTime(null);
		}
		
		if (model.getCollectionMethod() != null) {
			if (tabName.equals("IP") || tabName.equals("Normal"))
				entity.setCollectionMethod(model.getCollectionMethod());
		}
		
		Assignment assignment = assignmentDao.findById(sessionModel.getAssignmentId());
		
		List<Integer> quotationRecordIds = new ArrayList<Integer>();
		
		if (tabName.equals("Normal")) {
			quotationRecordIds = getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), sessionModel.getUserId(), null, null, null, "Normal");
		} else if (tabName.equals("Revisit")) {
			quotationRecordIds = getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), sessionModel.getUserId(), null, null, null, "Revisit");
		} else if (tabName.equals("Verify")) {
			quotationRecordIds = getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), sessionModel.getUserId(), null, null, null, "Verify");
		} else if (tabName.equals("IP")) {
			quotationRecordIds = getIPQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), sessionModel.getUserId(), null, null);
		}
		
		boolean isAllowChangeFirmStatus = isAllowChangeFirmStatus(assignment, tabName);
		
		Assignment selectedAssignment = null;
		if (tabName.equals("Normal")) {
			selectedAssignment = assignment;
		} else {
			if (sessionModel.getDateSelectedAssignmentId() != null)
				selectedAssignment = assignmentDao.findById(sessionModel.getDateSelectedAssignmentId());
			else
				selectedAssignment = assignment;
		}
		
		if (selectedAssignment != null) {
			if (isAllowChangeFirmStatus) {
				selectedAssignment.setStatus(model.getFirmStatus());
			}
			selectedAssignment.setCollectionMethod(model.getCollectionMethod());
			selectedAssignment.setOutletDiscountRemark(model.getDiscountRemark());
			
//			if (selectedAssignment.getCategoryInfo() != null) {
//				String latestContactPersonFromOutlet = null;
//				if (!StringUtils.isEmpty(selectedAssignment.getOutlet().getLastContact())) {
//					latestContactPersonFromOutlet = selectedAssignment.getOutlet().getLastContact();
//				} else {
//					latestContactPersonFromOutlet = selectedAssignment.getOutlet().getMainContact();
//				}
//				
//				for (AssignmentUnitCategoryInfo item : selectedAssignment.getCategoryInfo()) {
//					if (StringUtils.isEmpty(item.getContactPerson())) {
//						item.setContactPerson(latestContactPersonFromOutlet);
//						assignmentUnitCategoryInfoDao.save(item);
//					}
//				}
//			}
			
			assignmentDao.save(selectedAssignment);
		}
		
		if (quotationRecordIds.size() > 0) {
			List<QuotationRecord> quotationRecords = quotationRecordDao.getQuotationRecordsByIds(quotationRecordIds);
			for (QuotationRecord quotationRecord : quotationRecords) {
				List<QuotationRecord> actualQuotationRecords = getActualQuotationRecordsToUpdateWithBackTrackAndBackNo(quotationRecord);
				
				List<Integer> backNoDeletedList = new ArrayList<Integer>();
				for (QuotationRecord actualQuotationRecord : actualQuotationRecords) {
					if (backNoDeletedList.contains(actualQuotationRecord.getQuotationRecordId().intValue())) {
						continue;
					}
					
					if (isAllowChangeFirmStatus) {
						if (!actualQuotationRecord.isBackNo()) {
							actualQuotationRecord.setFirmStatus(model.getFirmStatus());
							quotationRecordService.updateAvailabilityByFirmStatus(actualQuotationRecord, model.getFirmStatus());
							QuotationRecord backNoDeleted = quotationRecordService.onAvailabilityChangedDeleteBackNo(actualQuotationRecord);
							if (backNoDeleted != null) {
								backNoDeletedList.add(backNoDeleted.getQuotationRecordId());
							}
						}
					}
					
					//actualQuotationRecord.setDiscountRemark(model.getDiscountRemark());
					
					actualQuotationRecord.setOutletDiscountRemark(model.getDiscountRemark());

					if (actualQuotationRecord.getAssignment().getStatus() == 9) { // IP
						actualQuotationRecord.setQuotationState("IP");
					}
					
					if (quotationRecord.getCollectionDate() == null) {
						quotationRecord.setCollectionDate(new Date());
					}
					
					if (!actualQuotationRecord.isBackNo()){
						quotationRecordService.validateQutoationRecord(actualQuotationRecord);
					}
					
					if (actualQuotationRecord.isBackNo()) {
						if (!quotationRecordService.isAvailabilityNotAvailable(actualQuotationRecord.getAvailability()))
							quotationRecordDao.save(actualQuotationRecord);
					} else {
						quotationRecordDao.save(actualQuotationRecord);
					}
				}
			}
		}
		

		if (selectedAssignment != null && selectedAssignment.getOutlet().getId().intValue() != entity.getId()) {
			selectedAssignment.setOutlet(entity);
			assignmentDao.save(selectedAssignment);
			
			if (quotationRecordIds.size() > 0) {
				List<QuotationRecord> quotationRecords = quotationRecordDao.getQuotationRecordsByIds(quotationRecordIds);
				for (QuotationRecord quotationRecord : quotationRecords) {
					List<QuotationRecord> actualQuotationRecords = getActualQuotationRecordsToUpdateWithBackTrackAndBackNo(quotationRecord);
					
					for (QuotationRecord actualQuotationRecord : actualQuotationRecords) {
						actualQuotationRecord.setOutlet(entity);
						quotationRecordDao.save(actualQuotationRecord);
					}
				}
			}
		}
		
		outletDao.save(entity);

//		outletDao.flush();
//		quotationRecordDao.flush();
		assignmentDao.flush();
	}
	
	/**
	 * compare outlet post to entity
	 */
	public String compareOutletPostToEntity(OutletPostModel model, Outlet entity) throws Exception {
		String result = "";
		
		
		return result;
		}
	
	/**
	 * copy outlet post to entity
	 */
	public void copyOutletPostToEntity(OutletPostModel model, Outlet entity) throws Exception {
		entity.setName(model.getName());
		
		Tpu tpuEntity = tpuDao.findById(model.getTpuId());
		entity.setTpu(tpuEntity);
		
		entity.setStreetAddress(model.getStreetAddress());
		entity.setDetailAddress(model.getDetailAddress());
		entity.setMainContact(model.getMainContact());
		entity.setLastContact(model.getLastContact());
		entity.setTel(model.getTel());
		entity.setFax(model.getFax());
		
		if (StringUtils.isEmpty(model.getOpeningStartTime()))
			entity.setOpeningStartTime(null);
		else
			entity.setOpeningStartTime(commonService.getTime(model.getOpeningStartTime()));
		if (StringUtils.isEmpty(model.getOpeningEndTime()))
			entity.setOpeningEndTime(null);
		else
			entity.setOpeningEndTime(commonService.getTime(model.getOpeningEndTime()));
		
		if (StringUtils.isEmpty(model.getOpeningStartTime2()))
			entity.setOpeningStartTime2(null);
		else
			entity.setOpeningStartTime2(commonService.getTime(model.getOpeningStartTime2()));
		if (StringUtils.isEmpty(model.getOpeningEndTime2()))
			entity.setOpeningEndTime2(null);
		else
			entity.setOpeningEndTime2(commonService.getTime(model.getOpeningEndTime2()));

		if (StringUtils.isEmpty(model.getConvenientStartTime()))
			entity.setConvenientStartTime(null);
		else
			entity.setConvenientStartTime(commonService.getTime(model.getConvenientStartTime()));
		if (StringUtils.isEmpty(model.getConvenientEndTime()))
			entity.setConvenientEndTime(null);
		else
			entity.setConvenientEndTime(commonService.getTime(model.getConvenientEndTime()));

		if (StringUtils.isEmpty(model.getConvenientStartTime2()))
			entity.setConvenientStartTime2(null);
		else
			entity.setConvenientStartTime2(commonService.getTime(model.getConvenientStartTime2()));
		if (StringUtils.isEmpty(model.getConvenientEndTime2()))
			entity.setConvenientEndTime2(null);
		else
			entity.setConvenientEndTime2(commonService.getTime(model.getConvenientEndTime2()));
		
		entity.setWebSite(model.getWebSite());
		
		entity.setRemark(model.getRemark());
		entity.setDiscountRemark(model.getDiscountRemark());
		
		entity.setBrCode(model.getBrCode());
		entity.setOutletMarketType(model.getOutletMarketType());
	}
	
	/**
	 * save outlet example
	 */
	@Transactional
	public void saveOutletExample(OutletPostModel model, int assignmentId, int userId) throws Exception {
		Outlet entity = outletDao.findById(model.getOutletId());
		
		copyOutletPostToEntity(model, entity);
		
		outletDao.save(entity);
		outletDao.flush();
	}
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<QuotationRecordTableList> getNormalRevisitVerifyQuotationRecordTableList(DatatableRequestModel model,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, Integer verificationType, String unitCategory, String quotationState) throws Exception {

		Order order = this.getOrder(model, "formType", "", "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		
		String search = model.getSearch().get("value");
		
		List<QuotationRecordTableList> result = quotationRecordDao.getNormalRevisitVerifyTableListForAssignmentMaintenance(search, model.getStart(), model.getLength(), order,
				dateSelectedAssignmentId, userId, consignmentCounter, verificationType, unitCategory, quotationState);
		
		DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countNormalRevisitVerifyTableListForAssignmentMaintenance("", dateSelectedAssignmentId, userId, null, null, null, quotationState);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countNormalRevisitVerifyTableListForAssignmentMaintenance(search, dateSelectedAssignmentId, userId, consignmentCounter, verificationType, unitCategory, quotationState);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public List<Integer> getNormalRevisitVerifyQuotationRecordTableListAllIds(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, Integer verificationType, String unitCategory, String quotationState) throws Exception {
		return getNormalRevisitVerifyQuotationRecordTableListAllIds(dateSelectedAssignmentId, userId, consignmentCounter, verificationType, unitCategory, quotationState, null);
	}

	/**
	 * Get Normal/Revisit/Verify Quotation Record Table List All Ids
	 */
	
	public List<Integer> getNormalRevisitVerifyQuotationRecordTableListAllIds(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, Integer verificationType, String unitCategory, String quotationState, DatatableRequestModel model) throws Exception {
		Order order = null;
		if (model != null)
			order = this.getOrder(model, "formType", "", "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		List<QuotationRecordTableList> result = quotationRecordDao.getNormalRevisitVerifyTableListForAssignmentMaintenance(null, null, null, order,
				dateSelectedAssignmentId, userId, consignmentCounter, verificationType, unitCategory, quotationState);
		List<Integer> ids = new ArrayList<Integer>();
		for (QuotationRecordTableList item : result) {
			ids.add(item.getId());
		}
		return ids;
	}
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<QuotationRecordTableList> getIPQuotationRecordTableList(DatatableRequestModel model,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) throws Exception {

		Order order = this.getOrder(model, "formType", "", "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		
		String search = model.getSearch().get("value");
		
		List<QuotationRecordTableList> result = quotationRecordDao.getIPTableListForAssignmentMaintenance(search, model.getStart(), model.getLength(), order,
				dateSelectedAssignmentId, userId, consignmentCounter, unitCategory);
		
		DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countIPTableListForAssignmentMaintenance("", dateSelectedAssignmentId, userId, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countIPTableListForAssignmentMaintenance(search, dateSelectedAssignmentId, userId, consignmentCounter, unitCategory);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	public List<Integer> getIPQuotationRecordTableListAllIds(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) throws Exception {
		return getIPQuotationRecordTableListAllIds(dateSelectedAssignmentId, userId, consignmentCounter, unitCategory, null);
	}
	
	/**
	 * Get Revisit/Verify Quotation Record Table List All Ids
	 */

	public List<Integer> getIPQuotationRecordTableListAllIds(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory, DatatableRequestModel model) throws Exception {
		Order order = null;
		if (model != null)
			order = this.getOrder(model, "formType", "", "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		List<QuotationRecordTableList> result = quotationRecordDao.getIPTableListForAssignmentMaintenance(null, null, null, order,
				dateSelectedAssignmentId, userId, consignmentCounter, unitCategory);
		List<Integer> ids = new ArrayList<Integer>();
		for (QuotationRecordTableList item : result) {
			ids.add(item.getId());
		}
		return ids;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<QuotationRecordTableList> getNewRecruitmentQuotationRecordTableList(DatatableRequestModel model,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) throws Exception {

		Order order = this.getOrder(model, "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		
		String search = model.getSearch().get("value");
		
		List<QuotationRecordTableList> result = quotationRecordDao.getTableListForNewRecruitmentMaintenance(search, model.getStart(), model.getLength(), order,
				dateSelectedAssignmentId, userId, consignmentCounter, unitCategory);
		
		DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countTableListForNewRecruitmentMaintenance("", dateSelectedAssignmentId, userId, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countTableListForNewRecruitmentMaintenance(search, dateSelectedAssignmentId, userId, consignmentCounter, unitCategory);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<QuotationRecordTableList> getPEViewQuotationRecordTableList(DatatableRequestModel model,
			Integer assignmentId, String consignmentCounter, String unitCategory) throws Exception {

		Order order = this.getOrder(model, "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		
		String search = model.getSearch().get("value");
		
		List<QuotationRecordTableList> result = quotationRecordDao.getPEViewTableListForAssignmentMaintenance(search, model.getStart(), model.getLength(), order,
				assignmentId, consignmentCounter, unitCategory);
		
		DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countPEViewTableListForAssignmentMaintenance("", assignmentId, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countPEViewTableListForAssignmentMaintenance(search, assignmentId, consignmentCounter, unitCategory);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public List<Integer> getPEViewQuotationRecordTableListAllIds(Integer assignmentId, String consignmentCounter, String unitCategory) throws Exception {
		return getPEViewQuotationRecordTableListAllIds(assignmentId, consignmentCounter, unitCategory, null);
	}
	
	/**
	 * Get Normal Quotation Record Table List All Ids
	 */

	public List<Integer> getPEViewQuotationRecordTableListAllIds(Integer assignmentId, String consignmentCounter, String unitCategory, DatatableRequestModel model) throws Exception {
		Order order = null;
		if (model != null)
			order = this.getOrder(model, "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		List<QuotationRecordTableList> result = quotationRecordDao.getPEViewTableListForAssignmentMaintenance(null, null, null, order,
				assignmentId, consignmentCounter, unitCategory);
		List<Integer> ids = new ArrayList<Integer>();
		for (QuotationRecordTableList item : result) {
			ids.add(item.getId());
		}
		return ids;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<QuotationRecordTableList> getRUACaseApprovalQuotationRecordTableList(DatatableRequestModel model,
			Integer assignmentId, String consignmentCounter, String unitCategory) throws Exception {

		Order order = this.getOrder(model, "", "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		
		String search = model.getSearch().get("value");
		
		List<QuotationRecordTableList> result = quotationRecordDao.getTableListForRUACaseApproval(search, model.getStart(), model.getLength(), order,
				assignmentId, consignmentCounter, unitCategory);
		
		DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countTableListForRUACaseApproval("", assignmentId, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countTableListForRUACaseApproval(search, assignmentId, consignmentCounter, unitCategory);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	public List<Integer> getRUACaseApprovalQuotationRecordTableListAllIds(Integer assignmentId, String consignmentCounter, String unitCategory) throws Exception {
		return getRUACaseApprovalQuotationRecordTableListAllIds(assignmentId, consignmentCounter, unitCategory, null);
	}
	
	/**
	 * Get Normal Quotation Record Table List All Ids
	 */

	public List<Integer> getRUACaseApprovalQuotationRecordTableListAllIds(Integer assignmentId, String consignmentCounter, String unitCategory, DatatableRequestModel model) throws Exception {
		Order order = null;
		if (model != null)
			order = this.getOrder(model, "", "unitDisplayName", "nPrice", "sPrice", "discount", "status", "availability");
		List<QuotationRecordTableList> result = quotationRecordDao.getTableListForRUACaseApproval(null, null, null, order,
				assignmentId, consignmentCounter, unitCategory);
		List<Integer> ids = new ArrayList<Integer>();
		for (QuotationRecordTableList item : result) {
			ids.add(item.getId());
		}
		return ids;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<QuotationRecordTableList> getNewRecruitmentApprovalQuotationRecordTableList(DatatableRequestModel model,
			Integer assignmentId, String consignmentCounter, String unitCategory, Integer personInChargeId) throws Exception {

		Order order = this.getOrder(model, "", "unitDisplayName", "nPrice", "sPrice", "discount", "batchCode");
		
		String search = model.getSearch().get("value");
		
		List<QuotationRecordTableList> result = quotationRecordDao.getTableListForNewRecruitmentApproval(search, model.getStart(), model.getLength(), order,
				assignmentId, consignmentCounter, unitCategory, personInChargeId);
		
		DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countTableListForNewRecruitmentApproval("", assignmentId, null, null, personInChargeId);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countTableListForNewRecruitmentApproval(search, assignmentId, consignmentCounter, unitCategory, personInChargeId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	public List<Integer> getNewRecruitmentApprovalQuotationRecordTableListAllIds(Integer assignmentId, String consignmentCounter, String unitCategory) throws Exception {
		return getNewRecruitmentApprovalQuotationRecordTableListAllIds(assignmentId, consignmentCounter, unitCategory, null);
	}
	
	/**
	 * Get Normal Quotation Record Table List All Ids
	 */

	public List<Integer> getNewRecruitmentApprovalQuotationRecordTableListAllIds(Integer assignmentId, String consignmentCounter, String unitCategory, DatatableRequestModel model) throws Exception {
		Order order = null;
		if (model != null)
			order = this.getOrder(model, "", "unitDisplayName", "nPrice", "sPrice", "discount", "batchCode");
		List<QuotationRecordTableList> result = quotationRecordDao.getTableListForNewRecruitmentApproval(null, null, null, order,
				assignmentId, consignmentCounter, unitCategory, null);
		List<Integer> ids = new ArrayList<Integer>();
		for (QuotationRecordTableList item : result) {
			ids.add(item.getId());
		}
		return ids;
	}
	
	/**
	 * Set quotation record flag
	 */
	@Transactional
	public void setQuotationRecordFlag(int id, boolean flag) {
		QuotationRecord entity = quotationRecordDao.findById(id);
		entity.setFlag(flag);
		quotationRecordDao.save(entity);
		quotationRecordDao.flush();
	}
	
	/**
	 * Get normal revisit verify distinct unit category select2 format
	 */
	public Select2ResponseModel queryNormalRevisitVerifyDistinctUnitCategorySelect2(Select2RequestModel queryModel, int assignmentId, Integer userId, Integer verificationType, String quotationState) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchNormalRevisitVerifyDistinctUnitCategoryForAssignmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId, userId, verificationType, quotationState);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchNormalRevisitVerifyDistinctUnitCategoryForAssignmentMaintenance(queryModel.getTerm(), assignmentId, userId, verificationType, quotationState);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get ip distinct unit category select2 format
	 */
	public Select2ResponseModel queryIPDistinctUnitCategorySelect2(Select2RequestModel queryModel, int assignmentId, int userId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchIPDistinctUnitCategoryForAssignmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId, userId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchIPDistinctUnitCategoryForAssignmentMaintenance(queryModel.getTerm(), assignmentId, userId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Get new recruitment distinct unit category select2 format
	 */
	public Select2ResponseModel queryNewRecruitmentDistinctUnitCategorySelect2(Select2RequestModel queryModel, int assignmentId, int userId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchDistinctUnitCategoryForNewRecruitmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId, userId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchDistinctUnitCategoryForNewRecruitmentMaintenance(queryModel.getTerm(), assignmentId, userId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get normal distinct unit category select2 format
	 */
	public Select2ResponseModel queryPEViewDistinctUnitCategorySelect2(Select2RequestModel queryModel, int assignmentId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchPEViewDistinctUnitCategoryForAssignmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchPEViewDistinctUnitCategoryForAssignmentMaintenance(queryModel.getTerm(), assignmentId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get normal distinct unit category select2 format
	 */
	public Select2ResponseModel queryRUACaseApprovalDistinctUnitCategorySelect2(Select2RequestModel queryModel, int assignmentId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchDistinctUnitCategoryForRUACaseApproval(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchDistinctUnitCategoryForRUACaseApproval(queryModel.getTerm(), assignmentId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Get normal distinct unit category select2 format
	 */
	public Select2ResponseModel queryNewRecruitmentApprovalDistinctUnitCategorySelect2(Select2RequestModel queryModel, int assignmentId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchDistinctUnitCategoryForNewRecruitmentApproval(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchDistinctUnitCategoryForNewRecruitmentApproval(queryModel.getTerm(), assignmentId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Query Date Selection For Normal Revisit Verify Select2
	 */
	public Select2ResponseModel queryDateSelectionForNormalRevisitVerifySelect2(Select2RequestModel queryModel, int userId, int outletId, String quotationState) {
		queryModel.setRecordsPerPage(10);
		
		List<KeyValueModel> entities = assignmentDao.searchDateSelectionNormalRevisitVerifyForAssignmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
				userId, outletId, quotationState);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = assignmentDao.countSearchDateSelectionNormalRevisitVerifyForAssignmentMaintenance(queryModel.getTerm(), userId, outletId, quotationState);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (KeyValueModel entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(entity.getKey().trim());
			item.setText(entity.getValue().trim());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Query Date Selection For IP Select2
	 */
	public Select2ResponseModel queryDateSelectionForIPSelect2(Select2RequestModel queryModel, int userId, int outletId) {
		queryModel.setRecordsPerPage(10);		
		
		List<KeyValueModel> entities = assignmentDao.searchDateSelectionIPForAssignmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), 
				userId, outletId);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = assignmentDao.countSearchDateSelectionIPForAssignmentMaintenance(queryModel.getTerm(), userId, outletId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (KeyValueModel entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(entity.getKey().trim());
			item.setText(entity.getValue().trim());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get verification type group select2 format
	 */
	public Select2ResponseModel queryVerificationTypeGroupSelect2(Select2RequestModel queryModel, int dateSelectedAssignmentId, int userId) {
		queryModel.setRecordsPerPage(10);
		
		VerificationTypeGroupModel groupResult = quotationRecordDao.getVerificationTypeGroup(dateSelectedAssignmentId, userId);

		List<KeyValueModel> resultList = new ArrayList<KeyValueModel>();
		if (groupResult != null) {
			if (groupResult.isVerifyCategory() && (queryModel.getTerm() == null || "Verify Category".toLowerCase().contains(queryModel.getTerm().toLowerCase())))
				resultList.add(new KeyValueModel("1", "Verify Category"));
			if (groupResult.isVerifyFirm() && (queryModel.getTerm() == null || "Verify Firm".toLowerCase().contains(queryModel.getTerm().toLowerCase())))
				resultList.add(new KeyValueModel("2", "Verify Firm"));
			if (groupResult.isVerifyQuotation() && (queryModel.getTerm() == null || "Verify Quotation".toLowerCase().contains(queryModel.getTerm().toLowerCase())))
				resultList.add(new KeyValueModel("3", "Verify Quotation"));
		}
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = resultList.size();
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (KeyValueModel result : resultList) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(result.getKey());
			item.setText(result.getValue());
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Get normal revisit verify consignment counter select2 format
	 */
	public Select2ResponseModel queryNormalRevisitVerifyConsignmentSelect2(Select2RequestModel queryModel, int assignmentId, Integer userId, String quotationState) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchNormalRevisitVerifyConsignmentForAssignmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId, userId, quotationState);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchNormalRevisitVerifyConsignmentForAssignmentMaintenance(queryModel.getTerm(), assignmentId, userId, quotationState);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Get IP consignment counter select2 format
	 */
	public Select2ResponseModel queryIPConsignmentSelect2(Select2RequestModel queryModel, int assignmentId, int userId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchIPConsignmentForAssignmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId, userId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchIPConsignmentForAssignmentMaintenance(queryModel.getTerm(), assignmentId, userId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Get new recruitment consignment counter select2 format
	 */
	public Select2ResponseModel queryNewRecruitmentConsignmentSelect2(Select2RequestModel queryModel, int assignmentId, int userId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchConsignmentForNewRecruitmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId, userId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchConsignmentForNewRecruitmentMaintenance(queryModel.getTerm(), assignmentId, userId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get pe view consignment counter select2 format
	 */
	public Select2ResponseModel queryPEViewConsignmentSelect2(Select2RequestModel queryModel, int assignmentId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchPEViewConsignmentForAssignmentMaintenance(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchPEViewConsignmentForAssignmentMaintenance(queryModel.getTerm(), assignmentId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Get rua case approval consignment counter select2 format
	 */
	public Select2ResponseModel queryRUACaseApprovalConsignmentSelect2(Select2RequestModel queryModel, int assignmentId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchConsignmentForRUACaseApproval(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchConsignmentForRUACaseApproval(queryModel.getTerm(), assignmentId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Get new recruitment approval consignment counter select2 format
	 */
	public Select2ResponseModel queryNewRecruitmentApprovalConsignmentSelect2(Select2RequestModel queryModel, int assignmentId) {
		queryModel.setRecordsPerPage(10);
		
		List<String> categories = quotationRecordDao.searchConsignmentForNewRecruitmentApproval(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), assignmentId);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countSearchConsignmentForNewRecruitmentApproval(queryModel.getTerm(), assignmentId);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String category : categories) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(category);
			item.setText(category);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	/**
	 * Prepare quotation record view model
	 */
	public QuotationRecordPageViewModel prepareQuotationRecordPageViewModel(int id, SessionModel sessionModel, boolean loadForHistoryTab, boolean readonly) {
		QuotationRecordPageViewModel model = new QuotationRecordPageViewModel();
		model.setReadonly(readonly);
		model.setHistory(loadForHistoryTab);
		QuotationRecord quotationRecord = quotationRecordDao.getByIdWithRelated(id);
		if (quotationRecord.getCollectionDate() == null) {
			quotationRecord.setCollectionDate(new Date());
		}
//		if (quotationRecord.getReferenceDate() == null) {
//			quotationRecord.setReferenceDate(new Date());
//		}

		List<QuotationRecordHistoryDateModel> histories = new ArrayList<QuotationRecordHistoryDateModel>();
		QuotationRecord historyRecordEntity = null;
		if (!loadForHistoryTab) {
			histories = quotationRecordService.getHistoryDatesAndRecordId(quotationRecord);
			if (histories.size() > 0) {
				historyRecordEntity = quotationRecordDao.getByIdWithRelated(histories.get(0).getId());
			}
		}
		
		QuotationRecordViewModel quotationRecordViewModel = quotationRecordService.prepareQuotationRecordViewModel(quotationRecord, historyRecordEntity, loadForHistoryTab);
		model.setQuotationRecord(quotationRecordViewModel);

		QuotationRecord backNoQuotationRecord = quotationRecordDao.getBackNoRecord(id);
		if (backNoQuotationRecord == null) {
			backNoQuotationRecord = new QuotationRecord();
			quotationRecordService.initBackNoByOriginal(quotationRecord, backNoQuotationRecord);
		}
		QuotationRecordViewModel backNoQuotationRecordViewModel = quotationRecordService.prepareQuotationRecordViewModel(backNoQuotationRecord, historyRecordEntity, loadForHistoryTab);
		model.setBackNoQuotationRecord(backNoQuotationRecordViewModel);
		
		if (!loadForHistoryTab) {
			ProductPostModel productViewModel = quotationRecordService.prepareProductViewModel(quotationRecord);
			model.setProduct(productViewModel);
			
			model.setHistories(histories);
			
			model.setPointToNote(commonService.nl2br(concatPointToNotes(quotationRecord)));
			model.setVerificationRemark(commonService.nl2br(quotationRecord.getVerificationRemark()));
			model.setRejectReason(commonService.nl2br(quotationRecord.getRejectReason()));
			model.setPeCheckRemark(commonService.nl2br(quotationRecord.getPeCheckRemark()));
			model.setValidationSummary(commonService.nl2br(quotationRecord.getValidationError()));
			
			if (sessionModel != null) {
				model.setAssignmentId(sessionModel.getAssignmentId());
				model.setUserId(sessionModel.getUserId());
				if (quotationRecord.isBackTrack())
					determineNextAndPreviousId(quotationRecord.getOriginalQuotationRecord().getId(), sessionModel, false);
				else
					determineNextAndPreviousId(id, sessionModel, false);
				
				model.setNextQuotationRecordId(sessionModel.getNextId());
				model.setPreviousQuotationRecordId(sessionModel.getPreviousId());
				model.setCurrentQuotationRecordNumber(sessionModel.getCurrentPositionInIds());
				model.setTotalQuotationRecords(sessionModel.getIds().size());
				
				List<BackTrackDateModel> backTrackDates;
				if (quotationRecord.isBackTrack())
					backTrackDates = prepareBackTrackDatesSelect(quotationRecord.getOriginalQuotationRecord());
				else
					backTrackDates = prepareBackTrackDatesSelect(quotationRecord);
				model.setBackTrackDates(backTrackDates);
				
				boolean allBackTrackPassValidation = true;
				for (BackTrackDateModel backTrackDate : backTrackDates) {
					if (!backTrackDate.isPassValidation()) {
						allBackTrackPassValidation = false;
						break;
					}
				}
				model.setAllBackTrackPassValidation(allBackTrackPassValidation);
			}
			
			model.setFlag(quotationRecord.isFlag());
		}
		
		return model;
	}

	/**
	 * Concat point to notes
	 */
	public String concatPointToNotes(QuotationRecord entity) {
		String result = null;
		HashSet<PointToNote> list = new HashSet<PointToNote>();
		if (entity.getQuotation() != null && entity.getQuotation().getPointToNotes() != null) {
			list.addAll(entity.getQuotation().getPointToNotes());
		}
		if (entity.getProduct() != null && entity.getProduct().getPointToNotes() != null) {
			list.addAll(entity.getProduct().getPointToNotes());
		}
		if (entity.getQuotation() != null && entity.getQuotation().getUnit() != null && entity.getQuotation().getUnit().getPointToNotes() != null) {
			list.addAll(entity.getQuotation().getUnit().getPointToNotes());
		}
		
		List<PointToNote> selectAllNotes = pointToNoteDao.getBySelectAllProductQuotationUnit();
		if (selectAllNotes.size() > 0) {
			list.addAll(selectAllNotes);
		}
		
		List<String> stringList = new ArrayList<String>();
		Date targetDate = entity.getReferenceDate();
		for (PointToNote temp : list) {
			if (temp.getEffectiveDate() != null) {
				Date tempDate = commonService.getDateWithoutTime(temp.getEffectiveDate());
				if (tempDate.compareTo(targetDate) > 0) {
					continue;
				}
			}
			if (temp.getExpiryDate() != null) {
				Date tempDate = commonService.getDateWithoutTime(temp.getExpiryDate());
				if (tempDate.compareTo(targetDate) < 0) {
					continue;
				}
			}
			
			stringList.add(temp.getNote());
		}
		result = StringUtils.join(stringList, ",");
		
		return result;
	}
	
	/**
	 * Save quotation
	 */
	@Transactional
	public void saveQuotationRecord(PagePostModel model, InputStream photo1ImageStream, InputStream photo2ImageStream, String fileBaseLoc, SessionModel sessionModel, String btnSubmit) throws Exception {
		QuotationRecordPostModel originalQuotationRecordModel = model.getQuotationRecord();
		
		QuotationRecord entity = quotationRecordDao.getByIdWithRelated(originalQuotationRecordModel.getQuotationRecordId());
		if (entity == null) throw new SystemException("E00011");
		
		quotationRecordService.appendVerificationReply(model, entity);
		
		if (!quotationRecordService.isAvailabilityNotAvailable(originalQuotationRecordModel.getAvailability())) {
			quotationRecordService.submitProduct(model, entity, fileBaseLoc, photo1ImageStream, photo2ImageStream);
		}

		quotationRecordService.submitQuotationRecord(originalQuotationRecordModel, entity);

		List<String> errorMessages = new ArrayList<String>();
		
		if (entity.getAvailability() == 1 || entity.getAvailability() == 3) {
			errorMessages.addAll(quotationRecordValidationService.validate(entity, entity));
			errorMessages.addAll(onSpotValidationService.validate(entity));
		}
		
		quotationRecordDao.save(entity);

		if (entity.getFormDisplay() == 1 && entity.isProductChange() && entity.getQuotation().getUnit().isBackdateRequired()) {
			QuotationRecordPostModel backNoQuotationRecordModel = model.getBackNoQuotationRecord();
			
			backNoQuotationRecordModel.setCollectionDate(originalQuotationRecordModel.getCollectionDate());
			
			QuotationRecord backNoEntity = null;
			if (backNoQuotationRecordModel.getQuotationRecordId() != null) {
				backNoEntity = quotationRecordDao.getByIdWithRelated(backNoQuotationRecordModel.getQuotationRecordId());
				if (backNoEntity == null) throw new SystemException("E00011");
			} else {
				backNoEntity = new QuotationRecord();
				quotationRecordService.initBackNoByOriginal(entity, backNoEntity);
			}
			quotationRecordService.appendVerificationReply(model, backNoEntity);
			quotationRecordService.submitQuotationRecord(backNoQuotationRecordModel, backNoEntity);
			backNoEntity.setProduct(entity.getProduct());
			backNoEntity.setQuotationState(entity.getQuotationState());
			backNoEntity.setFirmStatus(null);
			backNoEntity.setUser(entity.getUser());
			backNoEntity.setProductChange(entity.isProductChange());
			backNoEntity.setNewProduct(entity.isNewProduct());
			
			if (backNoEntity.getAvailability() == 1 || backNoEntity.getAvailability() == 3) {
				errorMessages.addAll(quotationRecordValidationService.validate(backNoEntity, entity));
				errorMessages.addAll(onSpotValidationService.validateBackNo(backNoEntity));
			}
			
			quotationRecordDao.save(backNoEntity);
		} else {
			QuotationRecordPostModel backNoQuotationRecordModel = model.getBackNoQuotationRecord();
			if (backNoQuotationRecordModel != null && backNoQuotationRecordModel.getQuotationRecordId() != null) {
				QuotationRecord backNoEntity = quotationRecordDao.getByIdWithRelated(backNoQuotationRecordModel.getQuotationRecordId());
				if (backNoEntity != null)
					quotationRecordDao.delete(backNoEntity);
			}
		}
		
		if (errorMessages.size() > 0) {
			entity.setPassValidation(false);
			
			if (btnSubmit.toLowerCase().contains("submit")) {
				ServiceException se = new ServiceException("");
				se.setMessages(errorMessages);
				throw se;
			}
			
			entity.setValidationError(StringUtils.join(errorMessages, "\n"));
		} else {
			entity.setValidationError(null);
			entity.setPassValidation(true);
		}
		
		if (entity.getAvailability() != 2  || entity.getAvailability() == 2 && ("Blank".equals(entity.getStatus()) || "Rejected".equals(entity.getStatus()))){
			entity.setStatus("Draft");
		}
		
		if (entity.isBackTrack() && entity.getStatus().equals("Draft")) {
			entity.getOriginalQuotationRecord().setStatus("Draft");
			quotationRecordDao.save(entity.getOriginalQuotationRecord());
		}
		
		quotationRecordDao.save(entity);
		
		quotationRecordDao.flush();
		quotationRecordService.updateQuotationRecordByProductChange(entity);
	}
	
	/**
	 * Determine next and previous ID
	 */
	public void determineNextAndPreviousId(int id, SessionModel sessionModel, boolean removeId) {
		boolean isIdFound = false;
		Integer previousId = null;
		Iterator<Integer> sessionIdIterator = sessionModel.getIds().iterator();
		while (sessionIdIterator.hasNext()) {
			int thisId = sessionIdIterator.next();
			if (thisId == id) {
				if (removeId) sessionIdIterator.remove();
				
				isIdFound = true;
				sessionModel.setNextId(null);
				sessionModel.setPreviousId(previousId);
			} else {
				if (isIdFound) {
					sessionModel.setNextId(thisId);
					break;
				}
			}
			previousId = thisId;
		}

		int currentIndex = sessionModel.getIds().indexOf(id);
		if (currentIndex >= 0)
			sessionModel.setCurrentPositionInIds(currentIndex + 1);
		else
			sessionModel.setCurrentPositionInIds(null);
	}
	
	/**
	 * Update session after submit
	 */
	public void updateSessionAfterSubmit(int quotationRecordId, SessionModel sessionModel) {
		QuotationRecord entity = quotationRecordDao.findById(quotationRecordId);
		if (entity.isBackTrack())
			determineNextAndPreviousId(entity.getOriginalQuotationRecord().getId(), sessionModel, true);
		else
			determineNextAndPreviousId(entity.getId(), sessionModel, true);
	}
	
	/**
	 * Prepare BackTrack Dates Select
	 */
	public List<BackTrackDateModel> prepareBackTrackDatesSelect(QuotationRecord originalQuotationRecord) {
		List<BackTrackDateModel> list = new ArrayList<BackTrackDateModel>();
		List<BackTrackDateModel> backTrackList = quotationRecordDao.getBackTrackDates(originalQuotationRecord.getId());
		if (backTrackList.size() > 0) {
			list.add(new BackTrackDateModel(originalQuotationRecord.getId(), commonService.formatDate(originalQuotationRecord.getCollectionDate()), originalQuotationRecord.isPassValidation()));
			list.addAll(backTrackList);
		}
		return list;
	}
	
	/**
	 * Get Unit Category By Assignment for verify
	 */
	public List<AssignmentUnitCategoryInfoWithVerify> getUnitCategoryForVerify(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, Integer verificationType, String unitCategory) {
		return assignmentUnitCategoryInfoDao.getAllForVerify(dateSelectedAssignmentId, userId, consignmentCounter, verificationType, unitCategory);
	}
	
	/**
	 * Get Unit Category By Assignment for revisit
	 */
	public List<AssignmentUnitCategoryInfoWithVerify> getUnitCategoryForRevisit(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) {
		return assignmentUnitCategoryInfoDao.getAllForRevisit(dateSelectedAssignmentId, userId, consignmentCounter, unitCategory);
	}

	/**
	 * Get Unit Category By Assignment for ip
	 */
	public List<AssignmentUnitCategoryInfoWithVerify> getUnitCategoryForIP(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) {
		return assignmentUnitCategoryInfoDao.getAllForIP(dateSelectedAssignmentId, userId, consignmentCounter, unitCategory);
	}

	/**
	 * Get Unit Category By Assignment for normal
	 */
	public List<AssignmentUnitCategoryInfoWithVerify> getUnitCategoryForNormal(int assignmentId) {
		return assignmentUnitCategoryInfoDao.getAllForNormal(assignmentId);
	}

	/**
	 * Get Unit Category By Assignment for new recruitment normal
	 */
	public List<AssignmentUnitCategoryInfoWithVerify> getUnitCategoryForNewRecruitmentNormal(Integer assignmentId, Integer userId, String consignmentCounter, String unitCategory) {
		return assignmentUnitCategoryInfoDao.getAllForNewRecruitmentNormal(assignmentId, userId, consignmentCounter, unitCategory);
	}

	/**
	 * Get Unit Category By Assignment for peview
	 */
	public List<AssignmentUnitCategoryInfoWithVerify> getUnitCategoryForPEView(Integer assignmentId, String consignmentCounter, String unitCategory) {
		return assignmentUnitCategoryInfoDao.getAllForPEView(assignmentId, consignmentCounter, unitCategory);
	}

	/**
	 * Get Unit Category By Assignment for rua approval
	 */
	public List<AssignmentUnitCategoryInfoWithVerify> getUnitCategoryForRUACaseApproval(Integer assignmentId, String consignmentCounter, String unitCategory) {
		return assignmentUnitCategoryInfoDao.getAllForRUACaseApproval(assignmentId, consignmentCounter, unitCategory);
	}

	/**
	 * Get Unit Category By Assignment for new recruitment approval
	 */
	public List<AssignmentUnitCategoryInfoWithVerify> getUnitCategoryForNewRecruitmentApproval(Integer assignmentId, String consignmentCounter, String unitCategory) {
		return assignmentUnitCategoryInfoDao.getAllForNewRecruitmentApproval(assignmentId, consignmentCounter, unitCategory);
	}
	
	/**
	 * Save AssignmentUnitCategory
	 */
	@Transactional
	public void saveAssignmentUnitCategory(SessionModel sessionModel, List<AssignmentUnitCategoryInfo> model) throws Exception {
		if (model == null) return;
		saveAssignmentUnitCategoryOnly(sessionModel, model, sessionModel.getTab().equals("Normal"));
		
		unitCategoryInfoDao.flush();
		assignmentUnitCategoryInfoDao.flush();
		
		saveAssignmentUnitCategoryInQuotationRecord(sessionModel, model);
	}
	
	/**
	 * Save assginment unit category only
	 */
	@Transactional
	public void saveAssignmentUnitCategoryOnly(SessionModel sessionModel, List<AssignmentUnitCategoryInfo> model, boolean alsoSaveInUnitCategoryInfo) {
		int sequence = 1;
		for (AssignmentUnitCategoryInfo item : model) {
			if (sessionModel.getTab().equals("Normal"))
				item.setSequence(sequence);
			
			AssignmentUnitCategoryInfo entity = assignmentUnitCategoryInfoDao.findByIdWithRelated(item.getId());
			entity.setContactPerson(item.getContactPerson());
			entity.setRemark(item.getRemark());

			if (sessionModel.getTab().equals("Normal"))
				entity.setSequence(item.getSequence());
			
			assignmentUnitCategoryInfoDao.save(entity);
			
			if (alsoSaveInUnitCategoryInfo) {
				UnitCategoryInfo unitInfo = unitCategoryInfoDao.findByUnitCategoryAndOutlet(entity.getUnitCategory(), entity.getOutlet().getId());
				if (unitInfo != null) {
					unitInfo.setContactPerson(entity.getContactPerson());
					unitInfo.setRemark(entity.getRemark());
	
					if (sessionModel.getTab().equals("Normal"))
						unitInfo.setSequence(entity.getSequence());
					
					unitCategoryInfoDao.save(unitInfo);
				}
			}
			
			sequence++;
		}
	}
	
	/**
	 * Check All PassValidation
	 */
	public boolean checkAllPassValidation(int assignmentId, int userId) {
		List<QuotationRecord> records = quotationRecordDao.getQuotationRecordsForSubmitWithoutBackTrack(assignmentId, userId);
		
		for (QuotationRecord entity : records) {
			if (!entity.isPassValidation()) return false;

			if (quotationRecordValidationService.visited(entity, entity)) return false;

			for (QuotationRecord other : entity.getOtherQuotationRecords()) {
				if (other.isBackNo()) continue;
				
				if (!other.isPassValidation()) {
					return false;
				}
				if (quotationRecordValidationService.visited(other, other)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Big submit
	 */
	@Transactional
	public boolean bigSubmit(int assignmentId, int userId) {
		List<QuotationRecord> records = quotationRecordDao.getQuotationRecordsForSubmitWithoutBackTrackWithIP(assignmentId, userId);

		boolean isPECheck = false;
		boolean allPassValidation = true;
		int numberOfQuotationRecordsSubmitted = 0;
		for (QuotationRecord entity : records) {
			// IP ignore validation
			if (!entity.isPassValidation() && entity.getAvailability() != 2) continue;

			if (quotationRecordValidationService.visited(entity, entity)) continue;

			for (QuotationRecord other : entity.getOtherQuotationRecords()) {
				if (other.isBackNo()) continue;
				
				if (!other.isPassValidation() && other.getAvailability() != 2) {
					allPassValidation = false;
					continue;
				}
				if (quotationRecordValidationService.visited(other, other)) {
					allPassValidation = false;
					continue;
				}
				// if original <> IP and backtrack any one = IP, => invalid
				if (!entity.getQuotationState().equals("IP") && other.getAvailability() == 2) {
					throw new ServiceException("E00134");
				}
				other.setStatus("Submitted");
				changeQuotationStateOnSubmit(other);
				quotationRecordDao.save(other);
			}

			if (entity.getAssignment().getStatus() == 1) {
				entity.getAssignment().setLockFirmStatus(true);
			}
			
			entity.setStatus("Submitted");
			changeQuotationStateOnSubmit(entity);
			quotationRecordDao.save(entity);
			numberOfQuotationRecordsSubmitted++;
			if(entity.getAvailability() == 5 || entity.isNewRecruitment()){
				isPECheck = true;
			}
			quotationRecordService.updateQuotationRecordByProductChange(entity);
		}
		
		//*(temporary) disable requested by Liam
//		if (numberOfQuotationRecordsSubmitted > 0) {
//			sendSubmitNotification(userId, numberOfQuotationRecordsSubmitted);
//		}
		
		quotationRecordDao.flush();
		if(isPECheck){
			assignmentApprovalService.runPELogic(records);
		}
		
		return allPassValidation;
	}
	
	/**
	 * Small submit
	 */
	@Transactional
	public boolean smallSubmit(int quotationRecordId, Integer userId) {
		List<String> errorMessages = new ArrayList<String>();
		
		QuotationRecord entity = quotationRecordDao.getByIdWithRelated(quotationRecordId);
		if (entity.isBackTrack()) {
			errorMessages.add("Only allow original submit");
			ServiceException se = new ServiceException("");
			se.setMessages(errorMessages);
			throw se;
		}
		
		if (!entity.isPassValidation() && entity.getAvailability() != 2) { // IP ignore validation
			errorMessages.add("Not pass validation");
			ServiceException se = new ServiceException("");
			se.setMessages(errorMessages);
			throw se;
		}
		
		if (quotationRecordValidationService.visited(entity, entity)) {
			if (entity.getQuotationState().equals("Verify"))
				errorMessages.add("Verify complete required");
			else
				errorMessages.add("Revisit complete required");
			ServiceException se = new ServiceException("");
			se.setMessages(errorMessages);
			throw se;
		}

		entity.setStatus("Submitted");
		changeQuotationStateOnSubmit(entity);
		quotationRecordDao.save(entity);
		
		List<BackTrackDateModel> backTrackDates = null;
		if (entity.isBackTrack()) {
			backTrackDates = quotationRecordDao.getBackTrackDates(entity.getOriginalQuotationRecord().getId());
		} else {
			backTrackDates = quotationRecordDao.getBackTrackDates(entity.getId());
		}
		
		boolean backTrackNotPassValidation = false;
		
		if (backTrackDates.size() > 0) {
			for (BackTrackDateModel backTrackDate : backTrackDates) {
				QuotationRecord backTrackRecord = quotationRecordDao.findById(backTrackDate.getQuotationRecordId());
				if (!backTrackRecord.isPassValidation() && backTrackRecord.getAvailability() != 2) {
					backTrackNotPassValidation = true;
					continue;
				}
				if (quotationRecordValidationService.visited(backTrackRecord, backTrackRecord)) {
					backTrackNotPassValidation = true;
					continue;
				}
				// if original <> IP and backtrack any one = IP, => invalid
				if (!entity.getQuotationState().equals("IP") && backTrackRecord.getAvailability() == 2) {
					throw new ServiceException("E00134");
				}
				
				backTrackRecord.setStatus("Submitted");
				changeQuotationStateOnSubmit(backTrackRecord);
				quotationRecordDao.save(backTrackRecord);
			}
		}
		
		/*
		 *(temporary) disable
		 *1. Notification when Field Officer submits a Quotation Record (to CSO) (Real-time) 
		 * 
		if (userId != null)
			sendSubmitNotification(userId, 1);*/
		quotationRecordService.updateQuotationRecordByProductChange(entity);
		
		entity.getAssignment().setLockFirmStatus(true);
		assignmentDao.save(entity.getAssignment());
		
		assignmentDao.flush();
		quotationRecordDao.flush();
		
		if(entity.getAvailability() == 5 || entity.isNewRecruitment()){
			List<QuotationRecord> entities = new ArrayList<QuotationRecord>();
			entities.add(entity);
			assignmentApprovalService.runPELogic(entities);
		}
		return !backTrackNotPassValidation;
	}
	
	/**
	 * Auto create assignment unit category
	 */
	@Transactional
	public void autoCreateAssignmentUnitCategory(Assignment entity) {
		List<String> allUnitCategories = unitCategoryInfoDao.getDistinctUnitCategoryByOutlet(entity.getOutlet().getId());
		List<UnitCategoryInfo> existingCategories = unitCategoryInfoDao.getAllByOutlet(entity.getOutlet().getId());
		
		String latestContactPersonFromOutlet = null;
		if (!StringUtils.isEmpty(entity.getOutlet().getLastContact())) {
			latestContactPersonFromOutlet = entity.getOutlet().getLastContact();
		} else {
			latestContactPersonFromOutlet = entity.getOutlet().getMainContact();
		}
		
//		if (entity.getCategoryInfo() != null) {
//			for (AssignmentUnitCategoryInfo item : entity.getCategoryInfo()) {
//				if (StringUtils.isEmpty(item.getContactPerson())) {
//					item.setContactPerson(latestContactPersonFromOutlet);
//					assignmentUnitCategoryInfoDao.save(item);
//				}
//			}
//		}
		
		for (String unitCategory : allUnitCategories) {
			UnitCategoryInfo categoryEntity = null;
			for (UnitCategoryInfo existingCategory : existingCategories) {
				if ((StringUtils.isEmpty(existingCategory.getUnitCategory()) && StringUtils.isEmpty(unitCategory)) || (existingCategory.getUnitCategory() != null && existingCategory.getUnitCategory().equals(unitCategory))) {
					categoryEntity = existingCategory;
					break;
				}
			}
			if (categoryEntity == null) {
				categoryEntity = new UnitCategoryInfo();
				categoryEntity.setUnitCategory(unitCategory);
				categoryEntity.setOutlet(entity.getOutlet());
//				categoryEntity.setContactPerson(latestContactPersonFromOutlet);
				existingCategories.add(categoryEntity);
			} else {
				if (categoryEntity.getContactPerson() == null) {
					categoryEntity.setContactPerson(latestContactPersonFromOutlet);
					unitCategoryInfoDao.save(categoryEntity);
				}
			}
		}
		
		int sequence = 1;
		for (UnitCategoryInfo existingCategory : existingCategories) {
			existingCategory.setSequence(sequence);
			unitCategoryInfoDao.save(existingCategory);
			
			AssignmentUnitCategoryInfo assignmentUnitCategory = new AssignmentUnitCategoryInfo();
			assignmentUnitCategory.setAssignment(entity);
			assignmentUnitCategory.setOutlet(existingCategory.getOutlet());
			assignmentUnitCategory.setUnitCategory(existingCategory.getUnitCategory());
			assignmentUnitCategory.setContactPerson(existingCategory.getContactPerson());
			assignmentUnitCategory.setRemark(existingCategory.getRemark());
			assignmentUnitCategory.setSequence(existingCategory.getSequence());
			assignmentUnitCategoryInfoDao.save(assignmentUnitCategory);
			sequence++;
		}
		
		assignmentUnitCategoryInfoDao.flush();
		unitCategoryInfoDao.flush();
	}
	
	/**
	 * Is allow change firm status
	 */
	public boolean isAllowChangeFirmStatus(Assignment assignment, String tabName) {
//		if (tabName.equals("Verify") || tabName.equals("Revisit")) return false;
//		
//		if (tabName.equals("IP") || tabName.equals("Normal")) {
//			if (!assignment.isLockFirmStatus())
//				return true;
//			else
//				return false;
//		}
		
		return !assignmentDao.getLockFirmStatusByAssignment(assignment.getAssignmentId());
		
		//return false;
	}
	
	/**
	 * Get actual quotation record to update with backtrack and back no
	 */
	public List<QuotationRecord> getActualQuotationRecordsToUpdateWithBackTrackAndBackNo(QuotationRecord quotationRecord) {
		List<QuotationRecord> actualQuotationRecords = new ArrayList<QuotationRecord>();
		actualQuotationRecords.add(quotationRecord);
		actualQuotationRecords.addAll(quotationRecord.getOtherQuotationRecords());
		return actualQuotationRecords;
	}
	
	/**
	 * Save assignment unit category in quotation record
	 */
	@Transactional
	public void saveAssignmentUnitCategoryInQuotationRecord(SessionModel sessionModel, List<AssignmentUnitCategoryInfo> model) throws Exception {
		List<Integer> quotationRecordIds = null;
		if (sessionModel.getTab().equals("Verify")) {
			quotationRecordIds = getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId(), sessionModel.getUserId(), null, null, null, "Verify");
		} else if (sessionModel.getTab().equals("Revisit")) {
			quotationRecordIds = getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId(), sessionModel.getUserId(), null, null, null, "Revisit");
		} else if (sessionModel.getTab().equals("IP")) {
			quotationRecordIds = getIPQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId(), sessionModel.getUserId(), null, null);
		} else if (sessionModel.getTab().equals("Normal")) {
			quotationRecordIds = quotationRecordDao.getAllIdsNotSubmittedByAssignment(sessionModel.getAssignmentId());
		}
		
		if (quotationRecordIds == null || quotationRecordIds.size() == 0) return;
		
		List<QuotationRecord> quotationRecords = quotationRecordDao.getByIdsWithUnit(quotationRecordIds);
		
		HashMap<String, AssignmentUnitCategoryInfo> unitCategoryToAssignmentUnitCategoryInfoMapping = new HashMap<String, AssignmentUnitCategoryInfo>();
		for (AssignmentUnitCategoryInfo info : model) {
			unitCategoryToAssignmentUnitCategoryInfoMapping.put(info.getUnitCategory(), info);
		}
		
		for (QuotationRecord quotationRecord : quotationRecords) {
			AssignmentUnitCategoryInfo info = unitCategoryToAssignmentUnitCategoryInfoMapping.get(quotationRecord.getQuotation().getUnit().getUnitCategory());
			if (info == null) continue;
			
			QuotationRecord originalQuotationRecord = quotationRecordDao.getQuotationRecordsForFindBackNoData(quotationRecord);
			if (originalQuotationRecord != null){
				originalQuotationRecord.setCategoryRemark(info.getRemark());
			}
			quotationRecord.setContactPerson(info.getContactPerson());
			
			quotationRecord.setCategoryRemark(info.getRemark());
			quotationRecordDao.save(quotationRecord);
		}
		quotationRecordDao.flush();
	}

	/**
	 * Set visited
	 */
	@Transactional
	public void setVisited(SessionModel sessionModel) throws Exception {
		List<Integer> quotationRecordIds = null;
		if (sessionModel.getTab().equals("Verify")) {
			quotationRecordIds = getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getDateSelectedAssignmentId() != null ? sessionModel.getDateSelectedAssignmentId() : sessionModel.getAssignmentId(), sessionModel.getUserId(), sessionModel.getConsignmentCounter(), sessionModel.getVerificationType(), sessionModel.getUnitCategory(), "Verify");
		}
		
		if (quotationRecordIds == null || quotationRecordIds.size() == 0) return;
		
		List<QuotationRecord> quotationRecords = quotationRecordDao.getByIdsWithUnit(quotationRecordIds);
		
		for (QuotationRecord quotationRecord : quotationRecords) {
			quotationRecord.setVisited(true);
			quotationRecordDao.save(quotationRecord);
		}
		quotationRecordDao.flush();
	}
	
	/**
	 * Get status by assignment id
	 */
	public int getStatusByAssignment(int assignmentId) {
		Assignment entity = assignmentDao.findById(assignmentId);
		if (entity == null || entity.getStatus() == null)
			return 1;
		else
			return entity.getStatus();
	}

	/**
	 * Save new recruitment AssignmentUnitCategory
	 */
	@Transactional
	public void saveNewRecruitmentAssignmentUnitCategory(SessionModel sessionModel, List<AssignmentUnitCategoryInfo> model) throws Exception {
		if (model == null) return;
		saveAssignmentUnitCategoryOnly(sessionModel, model, false);
		
		unitCategoryInfoDao.flush();
		assignmentUnitCategoryInfoDao.flush();
		
		saveNewRecruitmentAssignmentUnitCategoryInQuotationRecord(sessionModel, model);
	}

	/**
	 * Save assignment unit category in quotation record
	 */
	@Transactional
	public void saveNewRecruitmentAssignmentUnitCategoryInQuotationRecord(SessionModel sessionModel, List<AssignmentUnitCategoryInfo> model) throws Exception {
		List<Integer> quotationRecordIds = null;
		
		quotationRecordIds = getNormalRevisitVerifyQuotationRecordTableListAllIds(sessionModel.getAssignmentId(), sessionModel.getUserId(), sessionModel.getConsignmentCounter(), null, sessionModel.getUnitCategory(), "Normal");
		
		if (quotationRecordIds == null || quotationRecordIds.size() == 0) return;
		
		List<QuotationRecord> quotationRecords = quotationRecordDao.getByIdsWithUnit(quotationRecordIds);
		
		HashMap<String, AssignmentUnitCategoryInfo> unitCategoryToAssignmentUnitCategoryInfoMapping = new HashMap<String, AssignmentUnitCategoryInfo>();
		for (AssignmentUnitCategoryInfo info : model) {
			unitCategoryToAssignmentUnitCategoryInfoMapping.put(info.getUnitCategory(), info);
		}
		
		for (QuotationRecord quotationRecord : quotationRecords) {
			AssignmentUnitCategoryInfo info = unitCategoryToAssignmentUnitCategoryInfoMapping.get(quotationRecord.getQuotation().getUnit().getUnitCategory());
			if (info == null) continue;
			quotationRecord.setContactPerson(info.getContactPerson());
			quotationRecord.setCategoryRemark(info.getRemark());
			quotationRecordDao.save(quotationRecord);
		}
		quotationRecordDao.flush();
	}
	
	/**
	 * Send submit notification
	 */
	@Transactional
	public void sendSubmitNotification(int userId, int numberOfQuotationRecordsSubmitted) {
		User notifyUser = null;
		
		User quotationRecordUser = userDao.findById(userId);
		if (quotationRecordUser.getSupervisor() == null) return;
		
		Integer supervisorId = quotationRecordUser.getSupervisor().getId();
		
		Integer actedUserId = actingDao.getActedUserIdByUserId(supervisorId);
		if (actedUserId == null)
			notifyUser = userDao.findById(supervisorId);
		else
			notifyUser = userDao.findById(actedUserId);
		
		String subject = messageSource.getMessage("N00030", null, Locale.ENGLISH);
		String content = messageSource.getMessage("N00031", new Object[]{numberOfQuotationRecordsSubmitted}, Locale.ENGLISH);
		notifyService.sendNotification(notifyUser, subject, content, false);
	}
	
	/**
	 * Check time log exists
	 */
	public boolean checkTimeLogExists(int assignmentId, String selectedDate, int userId) throws Exception {
		Date selectedDateDate = commonService.getDate(selectedDate);
		
		Assignment entity = assignmentDao.findById(assignmentId);
		if (entity.getOutlet().getCollectionMethod() != null && entity.getOutlet().getCollectionMethod() == 1) {
			Long count = timeLogDao.countFieldworkTimeLog(selectedDateDate, userId, entity.getReferenceNo());
			return count > 0;
		} else {
			Long count = timeLogDao.countTelephoneTimeLog(selectedDateDate, userId, entity.getReferenceNo());
			return count > 0;
		}
	}
	
	/**
	 * Online Function
	 */
	public List<NonScheduleAssignmentList> getOnlineNonScheduleAssignment(Integer userId){
		 List<NonScheduleAssignmentList> assignmentList = assignmentDao.getOnlineNonScheduleAssignment(userId);
		 for(NonScheduleAssignmentList assignment : assignmentList){
			 Outlet outlet = outletDao.findById(assignment.getOutletId());
				Iterator<VwOutletTypeShortForm> outletTypes = outlet.getOutletTypes().iterator();
				List<String> codeList = new ArrayList<String>();
				while (outletTypes.hasNext()){
					VwOutletTypeShortForm outletType = outletTypes.next();
					codeList.add(outletType.getShortCode());
				}
				
				String outletTypeCode = StringUtils.join(codeList, ',');
				assignment.setOutletTypeCode(outletTypeCode);
		 }
		 return assignmentList;
	 }

	public NonScheduleAssignmentDetailList getQuotationHistoryByAssignment(List<QuotationOnlineModel> requestParams){
		// Prepare the input parameters
		List<Integer> assignmentIds = new ArrayList<Integer>();
		StringBuffer quotationIdsHistoryDatesBuffer = new StringBuffer();
		quotationIdsHistoryDatesBuffer.append(-1);
		for (QuotationOnlineModel requestParam : requestParams) {
			if (requestParam.getAssignmentId() != null) {
				assignmentIds.add(requestParam.getAssignmentId());
			} else {
				if (quotationIdsHistoryDatesBuffer.length() > 0){
					quotationIdsHistoryDatesBuffer.append(",");
					quotationIdsHistoryDatesBuffer.append(requestParam.getQuotationId());
//					quotationIdsHistoryDatesBuffer.append("_");
//					quotationIdsHistoryDatesBuffer.append(commonService.formatDateTime(requestParam.getHistoryDate()));
				}
			}
		}
		String quotationIdsHistoryDates = quotationIdsHistoryDatesBuffer.toString();
		
		// Retrieve by assignmentIds
		NonScheduleAssignmentDetailList returnData = new NonScheduleAssignmentDetailList();
		returnData.setAssignments(new ArrayList<AssignmentSyncData>());
		returnData.setOutlets(new ArrayList<OutletSyncData>());
		returnData.setQuotationRecords(new ArrayList<QuotationRecordSyncData>());
		returnData.setQuotations(new ArrayList<QuotationSyncData>());
		returnData.setSubPriceColumns(new ArrayList<SubPriceColumnSyncData>());
		returnData.setSubPriceRecords(new ArrayList<SubPriceRecordSyncData>());
		returnData.setTourRecords(new ArrayList<TourRecordSyncData>());
		returnData.setAssignmentUnitCategoryInfos(new ArrayList<AssignmentUnitCategoryInfoSyncData>());
		if (assignmentIds.size() > 0) {
			for(int i=0; i<assignmentIds.size(); i+=maxChunkSize){
				List<Integer> assignmentIdsSubList = assignmentIds.subList(i, Math.min(assignmentIds.size(), i+maxChunkSize));
				returnData.getAssignments().addAll(getAssignmentsByAssignmentIds(assignmentIdsSubList));
				returnData.getOutlets().addAll(getHistoryOutletByAssignmentIds(assignmentIdsSubList));
				returnData.getQuotationRecords().addAll(getHistoryQuotationRecordByAssignmentIds(assignmentIdsSubList));
				returnData.getQuotations().addAll(getHistoryQuotationByAssignmentIds(assignmentIdsSubList));
				returnData.getSubPriceColumns().addAll(getHistorySubPriceColumnByAssignmentIds(assignmentIdsSubList));
				returnData.getSubPriceRecords().addAll(getHistorySubPriceRecordByAssignmentIds(assignmentIdsSubList));
				returnData.getTourRecords().addAll(getHistoryTourRecordByAssignmentIds(assignmentIdsSubList));
				returnData.getAssignmentUnitCategoryInfos().addAll(getHistoryAssignmentUnitCategoryInfoByAssignmentIds(assignmentIdsSubList));
			}
		}
		
		if (!quotationIdsHistoryDates.equals("-1")) {
			returnData.getAssignments().addAll(getHistoryAssignmentByQuotationIdsHistoryDates(quotationIdsHistoryDates));
			returnData.getOutlets().addAll(getHistoryOutletByQuotationIdsHistoryDates(quotationIdsHistoryDates));
			returnData.getQuotationRecords().addAll(getHistoryQuotationRecordByQuotationIdsHistoryDates(quotationIdsHistoryDates));
			returnData.getQuotations().addAll(getHistoryQuotationByQuotationIdsHistoryDates(quotationIdsHistoryDates));
			returnData.getSubPriceColumns().addAll(getHistorySubPriceColumnByQuotationIdsHistoryDates(quotationIdsHistoryDates));
			returnData.getSubPriceRecords().addAll(getHistorySubPriceRecordByQuotationIdsHistoryDates(quotationIdsHistoryDates));
			returnData.getTourRecords().addAll(getHistoryTourRecordByQuotationIdsHistoryDates(quotationIdsHistoryDates));
			returnData.getAssignmentUnitCategoryInfos().addAll(getHistoryAssignmentUnitCategoryInfoByQuotationIdsHistoryDates(quotationIdsHistoryDates));
		}
		
		return returnData;
	}
	
	public NonScheduleAssignmentDetail getDownloadNonScheduleAssignmentDetail(Integer assignmentId){
		NonScheduleAssignmentDetail model = new NonScheduleAssignmentDetail();
		Integer[] ids = null;
		
		Integer[] assignmentIds = new Integer[1];
		assignmentIds[0] = assignmentId;
		
		AssignmentSyncData assignment = assignmentDao.getUpdatedAssignment(null, assignmentIds, null).get(0);
		/**
		 * For QuotationRecords
		 */
		List<QuotationRecord> quotationRecords = quotationRecordDao.findInProgressQuotationRecordByAssignment(assignmentDao.findById(assignmentId));
		Set<QuotationRecord> history = new HashSet<QuotationRecord>();
		Set<QuotationRecord> unique = new HashSet<QuotationRecord>(quotationRecords);
		Set<Integer> quotationRecordIds = new HashSet<Integer>();
		
		//For Notification - Download of non-scheduled assignment
		int notificationQuotationRecords = quotationRecords.size();
				
		List<QuotationRecord> backTracks = new ArrayList<QuotationRecord>();
		if(quotationRecords!=null && quotationRecords.size()>0){
			int fromIdx = 0;
			int toIdx = 0;
			int maxSize = 2000;
			int times = quotationRecords.size() / maxSize;
			int remainder = quotationRecords.size() % maxSize;
			
			toIdx = maxSize;
			List<QuotationRecord> splited = new ArrayList<QuotationRecord>();
			
			// Quotient
			for(int i = 0; i < times; i++) {
				splited = quotationRecords.subList(fromIdx, toIdx);
				backTracks = quotationRecordDao.getBackTrackQuotationRecordByQuotationRecord(splited);
				
				if(i < (times - 1)) {
					fromIdx = toIdx;
					toIdx += maxSize;
				}
			}
			
			// Remainder
			if(times == 0) {
				if(remainder != 0) {
					splited = quotationRecords.subList(fromIdx, remainder);
				}
			} else {
				if(remainder != 0) {
					splited = quotationRecords.subList(toIdx, (toIdx + remainder));
				}
			}
			
			if(remainder != 0) {
				backTracks = quotationRecordDao.getBackTrackQuotationRecordByQuotationRecord(splited);
			}
			
		}
		
		unique.addAll(backTracks);
		
		/**
		 * Give All History QuotationRecords
		 */
		for(QuotationRecord quotationRecord : unique){
			if(!quotationRecord.isBackNo()){
				int quotationId = quotationRecord.getQuotation().getId();
				Date historyDate = quotationRecord.getHistoryDate();
				if(historyDate!=null){
					history.addAll(quotationRecordDao.getByHistoryDatesAndQuotationId(quotationId, historyDate, 4));
					historyDate = DateUtils.addYears(historyDate, -1);
					history.addAll(quotationRecordDao.getByHistoryDatesAndQuotationId(quotationId, historyDate, 1));
				}
			}
		}
		Set<QuotationRecord> all = new HashSet<QuotationRecord>(quotationRecords);
		all.addAll(backTracks);
		all.addAll(history);
		if(all!=null && all.size()>0){
			List<QuotationRecord> temp = new ArrayList<QuotationRecord>(all);
			int fromIdx = 0;
			int toIdx = 0;
			int maxSize = 2000;
			int times = temp.size() / maxSize;
			int remainder = temp.size() % maxSize;
			
			toIdx = maxSize;
			List<QuotationRecord> splited = new ArrayList<QuotationRecord>();
			
			// Quotient
			for(int i = 0; i < times; i++) {
				splited = temp.subList(fromIdx, toIdx);
				unique.addAll(quotationRecordDao.getBackNoQuotationRecordByQuotationRecord(splited));
				
				if(i < (times - 1)) {
					fromIdx = toIdx;
					toIdx += maxSize;
				}
			}
			
			// Remainder
			if(times == 0) {
				if(remainder != 0) {
					splited = temp.subList(fromIdx, remainder);
				}
			} else {
				if(remainder != 0) {
					splited = temp.subList(toIdx, (toIdx + remainder));
				}
			}
			
			if(remainder != 0) {
				unique.addAll(quotationRecordDao.getBackNoQuotationRecordByQuotationRecord(splited));
			}
			
		}
			
		unique.addAll(history);
		
		for(QuotationRecord quotationRecord : unique){
			quotationRecordIds.add(quotationRecord.getQuotationRecordId());
		}
		
		List<QuotationRecordSyncData> finalQuotationRecord = new ArrayList<QuotationRecordSyncData>();
		if(quotationRecordIds!=null && quotationRecordIds.size()>0){
			List<Integer> temp = new ArrayList<Integer>(quotationRecordIds);
			int fromIdx = 0;
			int toIdx = 0;
			int maxSize = 2000;
			int times = temp.size() / maxSize;
			int remainder = temp.size() % maxSize;

			toIdx = maxSize;
			
			List<Integer> splited = new ArrayList<Integer>();
			
			// Quotient
			for (int i = 0; i < times; i++) {
				splited = temp.subList(fromIdx, toIdx);
				ids = new Integer[splited.size()];
				finalQuotationRecord.addAll(quotationRecordDao.getUpdatedQuotationRecord(null, splited.toArray(ids), null));

				if (i < (times - 1)) {
					fromIdx = toIdx;
					toIdx += maxSize;
				}
			}

			// Remainder
			if (times == 0) {
				if (remainder != 0) {
					splited = temp.subList(fromIdx, remainder);
				}
			} else {
				if (remainder != 0) {
					splited = temp.subList(toIdx, (toIdx + remainder));
				}
			}

			if (remainder != 0) {
				ids = new Integer[splited.size()];
				finalQuotationRecord.addAll(quotationRecordDao.getUpdatedQuotationRecord(null, splited.toArray(ids), null));
			}
		}
		
		/**
		 * For SubPriceRecord
		 */
		List<SubPriceRecordSyncData> subPriceRecords = new ArrayList<SubPriceRecordSyncData>();
		if(quotationRecordIds!=null && quotationRecordIds.size()>0){
			List<Integer> temp = new ArrayList<Integer>(quotationRecordIds);
			int fromIdx = 0;
			int toIdx = 0;
			int maxSize = 2000;
			int times = temp.size() / maxSize;
			int remainder = temp.size() % maxSize;

			toIdx = maxSize;
			ids = new Integer[temp.size()];
			List<Integer> splited = new ArrayList<Integer>();
			
			// Quotient
			for (int i = 0; i < times; i++) {
				splited = temp.subList(fromIdx, toIdx);
				subPriceRecords.addAll(subPriceRecordDao.getUpdatedSubPriceRecord(null, null, splited.toArray(ids), null));

				if (i < (times - 1)) {
					fromIdx = toIdx;
					toIdx += maxSize;
				}
			}

			// Remainder
			if (times == 0) {
				if (remainder != 0) {
					splited = temp.subList(fromIdx, remainder);
				}
			} else {
				if (remainder != 0) {
					splited = temp.subList(toIdx, (toIdx + remainder));
				}
			}

			if (remainder != 0) {
				subPriceRecords.addAll(subPriceRecordDao.getUpdatedSubPriceRecord(null, null, splited.toArray(ids), null));
			}
		}
		//	subPriceRecords = subPriceRecordDao.getUpdatedSubPriceRecord(null, null, quotationRecordIds.toArray(ids), null);
	
		
		/**
		 * For SubPriceColumn
		 */
		List<SubPriceColumnSyncData> subPriceColumns = new ArrayList<SubPriceColumnSyncData>();
		if(quotationRecordIds!=null && quotationRecordIds.size()>0){
			List<Integer> temp = new ArrayList<Integer>(quotationRecordIds);
			int fromIdx = 0;
			int toIdx = 0;
			int maxSize = 2000;
			int times = temp.size() / maxSize;
			int remainder = temp.size() % maxSize;

			toIdx = maxSize;
			ids = new Integer[temp.size()];
			List<Integer> splited = new ArrayList<Integer>();
			
			// Quotient
			for (int i = 0; i < times; i++) {
				splited = temp.subList(fromIdx, toIdx);
				subPriceColumns.addAll(subPriceColumnDao.getUpdatedSubPriceColumn(null, null, splited.toArray(ids), null));

				if (i < (times - 1)) {
					fromIdx = toIdx;
					toIdx += maxSize;
				}
			}

			// Remainder
			if (times == 0) {
				if (remainder != 0) {
					splited = temp.subList(fromIdx, remainder);
				}
			} else {
				if (remainder != 0) {
					splited = temp.subList(toIdx, (toIdx + remainder));
				}
			}

			if (remainder != 0) {
				subPriceColumns.addAll(subPriceColumnDao.getUpdatedSubPriceColumn(null, null, splited.toArray(ids), null));
			}
		}
//			subPriceColumns = subPriceColumnDao.getUpdatedSubPriceColumn(null, null, quotationRecordIds.toArray(ids), null);

		
		/**
		 * For TourRecord
		 */
		List<TourRecordSyncData> tourRecords = new ArrayList<TourRecordSyncData>();
		if(quotationRecordIds!=null && quotationRecordIds.size()>0){
			List<Integer> temp = new ArrayList<Integer>(quotationRecordIds);
			int fromIdx = 0;
			int toIdx = 0;
			int maxSize = 2000;
			int times = temp.size() / maxSize;
			int remainder = temp.size() % maxSize;

			toIdx = maxSize;
			ids = new Integer[temp.size()];
			List<Integer> splited = new ArrayList<Integer>();
			
			// Quotient
			for (int i = 0; i < times; i++) {
				splited = temp.subList(fromIdx, toIdx);
				tourRecords = tourRecordDao.getUpdatedTourRecord(null, null, splited.toArray(ids), null);

				if (i < (times - 1)) {
					fromIdx = toIdx;
					toIdx += maxSize;
				}
			}

			// Remainder
			if (times == 0) {
				if (remainder != 0) {
					splited = temp.subList(fromIdx, remainder);
				}
			} else {
				if (remainder != 0) {
					splited = temp.subList(toIdx, (toIdx + remainder));
				}
			}

			if (remainder != 0) {
				tourRecords = tourRecordDao.getUpdatedTourRecord(null, null, splited.toArray(ids), null);
			}
		}
//			tourRecords = tourRecordDao.getUpdatedTourRecord(null, null, quotationRecordIds.toArray(ids), null);
		
		
		/**
		 * For Quotation
		 */
		List<Integer> quotationIds = new ArrayList<Integer>();
		for(QuotationRecordSyncData quotationRecord : finalQuotationRecord){
			quotationIds.add(quotationRecord.getQuotationId());
		}
		List<QuotationSyncData> quotations = new ArrayList<QuotationSyncData>();
		if(quotationIds!=null && quotationIds.size()>0){
			List<Integer> temp = new ArrayList<Integer>(quotationIds);
			int fromIdx = 0;
			int toIdx = 0;
			int maxSize = 2000;
			int times = temp.size() / maxSize;
			int remainder = temp.size() % maxSize;

			toIdx = maxSize;
			ids = new Integer[temp.size()];
			List<Integer> splited = new ArrayList<Integer>();
			
			// Quotient
			for (int i = 0; i < times; i++) {
				splited = temp.subList(fromIdx, toIdx);
				quotations.addAll(quotationDao.getUpdatedDownloadedQuotation(null, splited.toArray(ids), null));

				if (i < (times - 1)) {
					fromIdx = toIdx;
					toIdx += maxSize;
				}
			}

			// Remainder
			if (times == 0) {
				if (remainder != 0) {
					splited = temp.subList(fromIdx, remainder);
				}
			} else {
				if (remainder != 0) {
					splited = temp.subList(toIdx, (toIdx + remainder));
				}
			}

			if (remainder != 0) {
				quotations.addAll(quotationDao.getUpdatedDownloadedQuotation(null, splited.toArray(ids), null));
			}
		}
//			quotations = quotationDao.getUpdatedDownloadedQuotation(null, quotationIds.toArray(ids), null);
		
		/**
		 * For AssignmentUnitCategoryInfo
		 */
		List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategoryInfos = new ArrayList<AssignmentUnitCategoryInfoSyncData>();
		if(assignmentIds!=null && assignmentIds.length>0){
			List<Integer> temp = Arrays.asList(assignmentIds);
			int fromIdx = 0;
			int toIdx = 0;
			int maxSize = 2000;
			int times = temp.size() / maxSize;
			int remainder = temp.size() % maxSize;

			toIdx = maxSize;
			ids = new Integer[temp.size()];
			List<Integer> splited = new ArrayList<Integer>();
			
			// Quotient
			for (int i = 0; i < times; i++) {
				splited = temp.subList(fromIdx, toIdx);
				assignmentUnitCategoryInfos.addAll(assignmentUnitCategoryInfoDao.getUpdatedAssignmentUnitCategoryInfo(null, splited.toArray(ids), null));

				if (i < (times - 1)) {
					fromIdx = toIdx;
					toIdx += maxSize;
				}
			}

			// Remainder
			if (times == 0) {
				if (remainder != 0) {
					splited = temp.subList(fromIdx, remainder);
				}
			} else {
				if (remainder != 0) {
					splited = temp.subList(toIdx, (toIdx + remainder));
				}
			}

			if (remainder != 0) {
				assignmentUnitCategoryInfos.addAll(assignmentUnitCategoryInfoDao.getUpdatedAssignmentUnitCategoryInfo(null, splited.toArray(ids), null));
			}
		}
		
//		assignmentUnitCategoryInfoDao.getUpdatedAssignmentUnitCategoryInfo(null, assignmentIds);
		/**
		 * For Outlet
		 */
		OutletSyncData outlet;
		if(assignment!=null){
			ids = new Integer[1];
			ids[0] = assignment.getOutletId();
			outlet = outletDao.getUpdatedOutlets(null, ids).get(0);
		} else {
			outlet = new OutletSyncData();
		}
		
		model.setAssignment(assignment);
		model.setQuotations(quotations);
		model.setQuotationRecords(finalQuotationRecord);
		model.setNotificationQuotationRecords(notificationQuotationRecords);
		model.setAssignmentUnitCategoryInfos(assignmentUnitCategoryInfos);
		model.setSubPriceRecords(subPriceRecords);
		model.setSubPriceColumns(subPriceColumns);
		model.setTourRecords(tourRecords);
		model.setOutlet(outlet);
		
		return model;
	}

	/**
	 * Generate random case
	 */
	@Transactional
	public List<PECheckTask> generateRandomCase(int staffId, int surveyMonthId, int noOfCase) {
		List<PECheckTask> returnList = new ArrayList<PECheckTask>();
		
		Date excludeMonth = null;
		SystemConfiguration sc = this.sysConfigDao.findByName(SystemConstant.PE_PARAM_EXCLUDE_PE_CHECK_MONTH);
		if(sc.getValue()!=null && !StringUtils.isEmpty(sc.getValue())){
			excludeMonth = DateUtils.addMonths(surveyMonthDao.findById(surveyMonthId).getReferenceMonth(), Integer.valueOf(sc.getValue())*-1);
		}
		
		List<Integer> assignmentIds = assignmentDao.getAssignmentForRandomCase(staffId, surveyMonthId, excludeMonth);
		
		if (assignmentIds == null || assignmentIds.size() == 0) {
			return returnList;
		}
		
		int noOfAssignmentPerGroup = (int)Math.floor((double)assignmentIds.size() / noOfCase);
		
		if (noOfAssignmentPerGroup > 0){
			SecureRandom random = new SecureRandom();
			for (int i = 0; i < noOfCase; i++) {
				int noOfAssignmentInThisGroup = 0;
				int lastIndexInGroup = (i + 1) * noOfAssignmentPerGroup;
				if (lastIndexInGroup > assignmentIds.size()) {
					noOfAssignmentInThisGroup = assignmentIds.size() - (i * noOfAssignmentPerGroup);
				} else {
					noOfAssignmentInThisGroup = noOfAssignmentPerGroup;
				}
				
				int indexInGroup = random.nextInt(noOfAssignmentInThisGroup);
				int assignmentId = assignmentIds.get(i * noOfAssignmentPerGroup + indexInGroup);
				Assignment assignment = assignmentDao.findById(assignmentId);
				
				//PECheckTaskAndForm model = new PECheckTaskAndForm();
				
				PECheckTask task = assignment.getPeCheckTask();
				if (task == null) {
					task = new PECheckTask();
					task.setSurveyMonth(assignment.getSurveyMonth());
					task.setAssignment(assignment);
				}
				task.setSelected(true);
				task.setRandomCase(true);
				peCheckTaskDao.save(task);
				
				returnList.add(task);
			}
		} else {
			for (Integer assignmentId : assignmentIds){
				Assignment assignment = assignmentDao.findById(assignmentId);
				
				PECheckTask task = assignment.getPeCheckTask();
				if (task == null) {
					task = new PECheckTask();
					task.setSurveyMonth(assignment.getSurveyMonth());
					task.setAssignment(assignment);
				}
				task.setSelected(true);
				task.setRandomCase(true);
				peCheckTaskDao.save(task);
				
				returnList.add(task);
			}
		}
		
		return returnList;
	}

	/**
	 * Data Sync
	 */
	
	public List<AssignmentAttributeSyncData> getUpdatedAssignmentAttribute(Date lastSyncTime){
		return assignmentAttributeDao.getUpdatedAssignmentAttribute(lastSyncTime);
	}
	
	public List<BatchCollectionDateSyncData> getUpdatedBatchCollectionDate(Date lastSyncTime){
		return batchCollectionDateDao.getUpdatedBatchCollectionDate(lastSyncTime);
	}
	
	
	@Transactional
	public List<UnitCategoryInfoSyncData> syncUnitCategoryInfoData(List<UnitCategoryInfoSyncData> unitCategoryInfos, Date lastSyncTime, Boolean dataReturn){
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> updateIds = new ArrayList<Integer>();
		if(unitCategoryInfos != null && unitCategoryInfos.size()>0){
			for(UnitCategoryInfoSyncData unitCategoryInfo : unitCategoryInfos){
				
				if ("D".equals(unitCategoryInfo.getLocalDbRecordStatus())){
					continue;
				}
				
				UnitCategoryInfo entity = null;
				if(unitCategoryInfo.getUnitCategoryInfoId() == null){
					if(unitCategoryInfoDao.findByUnitCategoryAndOutlet(unitCategoryInfo.getUnitCategory(), unitCategoryInfo.getOutletId())!=null){
						entity = unitCategoryInfoDao.findByUnitCategoryAndOutlet(unitCategoryInfo.getUnitCategory(), unitCategoryInfo.getOutletId());
						unitCategoryInfo.setUnitCategoryInfoId(entity.getUnitCategoryInfoId());
						if (entity!=null && entity.getModifiedDate() != null && entity.getModifiedDate().after(unitCategoryInfo.getModifiedDate())){
							updateIds.add(entity.getUnitCategoryInfoId());
							table.put(entity.getUnitCategoryInfoId(), unitCategoryInfo.getLocalId());
							continue;
						}
					} else {
						entity = new UnitCategoryInfo();
					}
				} else {
					entity = unitCategoryInfoDao.findById(unitCategoryInfo.getUnitCategoryInfoId());
					if (entity.getModifiedDate() != null && entity.getModifiedDate().after(unitCategoryInfo.getModifiedDate())){
						updateIds.add(entity.getUnitCategoryInfoId());
						table.put(entity.getUnitCategoryInfoId(), unitCategoryInfo.getLocalId());
						continue;
					}
				}
				
				BeanUtils.copyProperties(unitCategoryInfo, entity);
				
				if(unitCategoryInfo.getOutletId()!=null){
					Outlet outlet = outletDao.findById(unitCategoryInfo.getOutletId());
					if (outlet != null){
						entity.setOutlet(outlet);
					}
				}
				entity.setByPassModifiedDate(true);
				unitCategoryInfoDao.save(entity);
				updateIds.add(entity.getUnitCategoryInfoId());
				table.put(entity.getUnitCategoryInfoId(), unitCategoryInfo.getLocalId());
			}
			unitCategoryInfoDao.flush();
		}
		
		if(dataReturn != null && dataReturn){
			
			List<UnitCategoryInfoSyncData> updatedData = new ArrayList<UnitCategoryInfoSyncData>();
			if(updateIds!=null){
				updatedData.addAll(syncUnitCategoryByIdsRecursiveQuery(updateIds));
			}
					
			updatedData.addAll(unitCategoryInfoDao.getUpdatedUnitCategoryInfo(lastSyncTime, null));
			
			List<UnitCategoryInfoSyncData> unique = new ArrayList<UnitCategoryInfoSyncData>(new HashSet<UnitCategoryInfoSyncData>(updatedData));
			for(UnitCategoryInfoSyncData data : unique){
				if(table.containsKey(data.getUnitCategoryInfoId())){
					data.setLocalId(table.get(data.getUnitCategoryInfoId()));
				}
			}
			return unique;
		}
		
		return new ArrayList<UnitCategoryInfoSyncData>();
	}
	 
	 @Transactional
	 public List<AssignmentUnitCategoryInfoSyncData> syncAssignmentUnitCategoryInfoData(
			 List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategoryInfos
			 , Date lastSyncTime, Boolean dataReturn, Integer[] assignmentIds){
		 Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		 List<Integer> updateIds = new ArrayList<Integer>();
		 if(assignmentUnitCategoryInfos!=null && assignmentUnitCategoryInfos.size()>0){
			 for(AssignmentUnitCategoryInfoSyncData assignmentUnitCategoryInfo : assignmentUnitCategoryInfos){
				 if ("D".equals(assignmentUnitCategoryInfo.getLocalDbRecordStatus())){
					continue;
				}
				 AssignmentUnitCategoryInfo entity = null;
				 if(assignmentUnitCategoryInfo.getAssignmentUnitCategoryInfoId() == null){
					 if(assignmentUnitCategoryInfoDao.findByUnitCategoryAndAssignment(assignmentUnitCategoryInfo.getUnitCategory(), assignmentUnitCategoryInfo.getAssignmentId())!=null){
						 entity = assignmentUnitCategoryInfoDao.findByUnitCategoryAndAssignment(assignmentUnitCategoryInfo.getUnitCategory(), assignmentUnitCategoryInfo.getAssignmentId());
						 assignmentUnitCategoryInfo.setAssignmentUnitCategoryInfoId(entity.getAssignmentUnitCategoryInfoId());
						if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(assignmentUnitCategoryInfo.getModifiedDate())){
							updateIds.add(entity.getAssignmentUnitCategoryInfoId());
							table.put(entity.getAssignmentUnitCategoryInfoId(), assignmentUnitCategoryInfo.getLocalId());
							continue;
						}
					 } else {
						 entity = new AssignmentUnitCategoryInfo();
					 }
				 } else {
					 entity = assignmentUnitCategoryInfoDao.findById(assignmentUnitCategoryInfo.getAssignmentUnitCategoryInfoId());
					 if (entity.getModifiedDate() != null && entity.getModifiedDate().after(assignmentUnitCategoryInfo.getModifiedDate())){
						 updateIds.add(entity.getAssignmentUnitCategoryInfoId());
						table.put(entity.getAssignmentUnitCategoryInfoId(), assignmentUnitCategoryInfo.getLocalId());
						continue;
					}
				 }
				 
				 BeanUtils.copyProperties(assignmentUnitCategoryInfo, entity);
				 
				 if(assignmentUnitCategoryInfo.getOutletId()!=null){
					 Outlet outlet = outletDao.findById(assignmentUnitCategoryInfo.getOutletId());
					 if (outlet != null){
						 entity.setOutlet(outlet);						 
					 }
				 }
				 
				 if(assignmentUnitCategoryInfo.getAssignmentId()!=null){
					 Assignment assignment = assignmentDao.findById(assignmentUnitCategoryInfo.getAssignmentId());
					 if (assignment != null){
						 entity.setAssignment(assignment);						 
					 }
				 }
				 entity.setByPassModifiedDate(true);
				 assignmentUnitCategoryInfoDao.save(entity);
				 updateIds.add(entity.getAssignmentUnitCategoryInfoId());
				 table.put(entity.getAssignmentUnitCategoryInfoId(), assignmentUnitCategoryInfo.getLocalId());
			}
			assignmentUnitCategoryInfoDao.flush();
		 }
		 
		 if(dataReturn != null && dataReturn){
			 List<AssignmentUnitCategoryInfoSyncData> updatedData = new ArrayList<AssignmentUnitCategoryInfoSyncData>();
			 //AssignmentIds
			 if(assignmentIds!=null && assignmentIds.length>0){
				 updatedData.addAll(syncAssignmentUnitCategoryInfoByAssignmentRecursiveQuery(Arrays.asList(assignmentIds), lastSyncTime));
			 }
			 
			 //AssignmentUnitCategoryInfoIds
			 if(updateIds!=null && updateIds.size()>0){
				 updatedData.addAll(syncAssignmentUnitCategoryInfoByIdsRecursiveQuery(updateIds));
			 }
			 
			List<AssignmentUnitCategoryInfoSyncData> unqiue =  new ArrayList<AssignmentUnitCategoryInfoSyncData>(new HashSet<AssignmentUnitCategoryInfoSyncData>(updatedData));
			for(AssignmentUnitCategoryInfoSyncData data : unqiue){
				if(table.containsKey(data.getAssignmentUnitCategoryInfoId())){
					data.setLocalId(table.get(data.getAssignmentUnitCategoryInfoId()));
				}
			}
			return unqiue;
		 }
		 
		 return new ArrayList<AssignmentUnitCategoryInfoSyncData>();
	 }

	 @Transactional
	 public List<AssignmentSyncData> syncAssignmentData(
			 List<AssignmentSyncData> assignments, Date lastSyncTime
			 , Boolean dataReturn, Integer[] assignmentIds, Integer[] itineraryPlanIds){
		 Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		 List<Integer> unUpdateIds = new ArrayList<Integer>();
		 List<Integer> allAssignmentIds = new ArrayList<Integer>();
		 if(assignmentIds!=null && assignmentIds.length>0){
			 allAssignmentIds.addAll(Arrays.asList(assignmentIds));
		 }
		 
		 if(assignments!=null && assignments.size()>0){
			 for(AssignmentSyncData assignment : assignments){
				 if ("D".equals(assignment.getLocalDbRecordStatus())){
					 continue;
				 }
				 
				 Assignment entity = null;
				 if(assignment.getAssignmentId() == null){
					 entity = new Assignment();
				 } else {
					 entity = assignmentDao.findById(assignment.getAssignmentId());
					 if (entity != null && entity.getModifiedDate() != null && entity.getModifiedDate().after(assignment.getModifiedDate())){
						 unUpdateIds.add(entity.getAssignmentId());
						 table.put(entity.getAssignmentId(), assignment.getLocalId());
						 continue;
					 }
				 }
				 
				 BeanUtils.copyProperties(assignment, entity);
				 
				 if(assignment.getUserId()!=null){
					 User user = userDao.findById(assignment.getUserId());
					 if (user != null){
						 entity.setUser(user);						 
					 }
				 }
				 
				 if(assignment.getAssignedUserId()!=null){
					 User assignedUser = userDao.findById(assignment.getAssignedUserId());
					 if (assignedUser != null){
						 entity.setAssignedUser(assignedUser);
					 }
				 }				
				 
				 if(assignment.getOutletId()!=null){
					 Outlet outlet = outletDao.findById(assignment.getOutletId());
					 if (outlet != null){
						 entity.setOutlet(outlet);
					 }
				 }
				 
				 if(assignment.getSurveyMonthId()!=null){
					 SurveyMonth surveyMonth = surveyMonthDao.findById(assignment.getSurveyMonthId());
					 if (surveyMonth != null){
						 entity.setSurveyMonth(surveyMonth);
					 }
				 }
				 
				 if(assignment.getAdditionalDistrictId()!=null){
					 District additionalDistrict = districtDao.findById(assignment.getAdditionalDistrictId());
					 if (additionalDistrict != null){
						 entity.setAdditionalDistrict(additionalDistrict);						 
					 }
				 }
				 
				 if(assignment.getAdditionalTpuId()!=null){					 
					 Tpu additionalTpu = tpuDao.findById(assignment.getAdditionalTpuId());
					 if (additionalTpu != null){
						 entity.setAdditionalTpu(additionalTpu);						 
					 }
				 }
				 entity.setByPassModifiedDate(true);
				 assignmentDao.save(entity);
				 
				 allAssignmentIds.add(entity.getAssignmentId());
				 table.put(entity.getAssignmentId(), assignment.getLocalId());
			}
			 assignmentDao.flush();
		 }
		 
		 if(dataReturn != null && dataReturn){
			 List<AssignmentSyncData> updatedData = new ArrayList<AssignmentSyncData>();
			 
			 //For AssignmentIds
			 if(allAssignmentIds!=null && allAssignmentIds.size()>0)
				 updatedData.addAll(syncAssignmentRecursiveQuery(allAssignmentIds, lastSyncTime));
			 
			 //For ItineraryPlanIds
			 if(itineraryPlanIds!=null && itineraryPlanIds.length>0){
					updatedData.addAll(
							assignmentDao.getUpdatedAssignment(lastSyncTime, null, itineraryPlanIds));
			 }
			 
			 //For unUpdateIds
			 if(unUpdateIds!=null && unUpdateIds.size()>0){
				 updatedData.addAll(syncAssignmentRecursiveQuery(unUpdateIds, null));
			 }
			 
			List<AssignmentSyncData> unique = new ArrayList<AssignmentSyncData>(new HashSet<AssignmentSyncData>(updatedData));
			for(AssignmentSyncData data : unique){
				if(table.containsKey(data.getAssignmentId())){
					data.setLocalId(table.get(data.getAssignmentId()));
				}
			}
			return unique;
		 }
		 
		 return new ArrayList<AssignmentSyncData>();
	 }

	/**
	 * DataTable count
	 */
	public Long getNewRecruitmentTableCount() {
		List<Integer> userIds = new ArrayList<Integer>();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		if ((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2) {
			userIds = null;
		} else {
			userIds.add(detail.getUserId());
			List<Integer> actedUserIds = new ArrayList<Integer>();
			actedUserIds.add(detail.getUserId());
			actedUserIds.addAll(detail.getActedUsers());
			List<Integer> subordinates = userService.getOfficerIdsBySupervisors(actedUserIds);
			userIds.addAll(subordinates);
		}
		
		return assignmentDao.countNewRecruitmentMaintenanceAssignmentTableList("", userIds, null, null, null, null, null);
	}
	
	
	
	/** 
	 * datatable query
	 * @throws ParseException 
	 */
	public DatatableResponseModel<ReportAssignmentLookupList> getLookupTableList(DatatableRequestModel model,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, Integer[] oldAssignmentIds, String referenceMonth) throws ParseException{

		Order order = this.getOrder(model, "", "collectionDate", "startDate", "endDate", "firm", "district", "tpu", "noOfQuotation");
		
		String search = model.getSearch().get("value");
		Date month = null;
		if (!StringUtils.isEmpty(referenceMonth)){
			month = commonService.getMonth(referenceMonth);
		}
		
//		List<ReportAssignmentLookupList> result = assignmentDao.getAssignmentLookupTableList(search, model.getStart(), model.getLength(), order,
//				tpuIds, outletTypeId, districtId, batchId,oldAssignmentIds, month);
		List<ReportAssignmentLookupList> result = assignmentDao.getAssignmentLookupTableForIndividualQuotationRecordList(search, model.getStart(), model.getLength(), order,
				tpuIds, outletTypeId, districtId, batchId,oldAssignmentIds, month);

		
		DatatableResponseModel<ReportAssignmentLookupList> response = new DatatableResponseModel<ReportAssignmentLookupList>();
		response.setDraw(model.getDraw());
		response.setData(result);
//		Long recordTotal = assignmentDao.countAssignmentLookupTableList("", tpuIds, outletTypeId, districtId, batchId, oldAssignmentIds, month);
		Long recordTotal = assignmentDao.countAssignmentLookupTableForIndividualQuotationRecordList("", tpuIds, outletTypeId, districtId, batchId, oldAssignmentIds, month);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countAssignmentLookupTableForIndividualQuotationRecordList(search, tpuIds, outletTypeId, districtId, batchId, oldAssignmentIds, month);
//		Long recordFiltered = assignmentDao.countAssignmentLookupTableList(search, tpuIds, outletTypeId, districtId, batchId, oldAssignmentIds, month);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}

	/** 
	 * datatable select all
	 */
	public List<Integer> getLookupTableSelectAll(String search,List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId){

		return assignmentDao.getAssignmentLookupSelectAll(search, tpuIds, outletTypeId, districtId, batchId);
	}
	
	public List<Integer> getLookupTableSelectAll(String search,List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId,String referenceMonth) throws ParseException{
		Date month = null;
		if (!StringUtils.isEmpty(referenceMonth)){
			month = commonService.getMonth(referenceMonth);
		}
		return assignmentDao.getAssignmentLookupSelectAll(search, tpuIds, outletTypeId, districtId, batchId, month);
	}
	
	
	public Select2ResponseModel queryAssignmentSelect2(Select2RequestModel queryModel, String referenceMonth) throws ParseException{
		Date month = null;
		if (!StringUtils.isEmpty(referenceMonth)){
			month = commonService.getMonth(referenceMonth);
		}
		queryModel.setRecordsPerPage(10);
		List<Integer> entities = assignmentDao.queryAssignmentSelect2(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), month);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = assignmentDao.countAssignmentSelect2(queryModel.getTerm(), month);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Integer d : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(d));
			item.setText(String.valueOf(d));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	
	public Assignment getAssignmentById(Integer assignmentId){
		return assignmentDao.findById(assignmentId);
	}

	public List<Assignment> getAssignmentByIds(List<Integer> ids){
		return getAssignmentByIdsRecursiveQuery(ids);
	}
	
	public String getLocationByReferenceNo(String referenceNo) {
		return assignmentDao.getDetailAddressByReferenceNo(referenceNo);
	}
	
	public long countNotSubmittedQuotationRecord(Integer assignmentId) {
		return assignmentDao.countNotSubmittedQuotationRecord(assignmentId);
	}
	
	public long countNotSubmittedNewRecruitment(Integer assignmentId){
		return assignmentDao.countNotSubmittedNewRecruitment(assignmentId);
	}
	
	public void changeQuotationStateOnSubmit(QuotationRecord qr) {
		if ("IP".equals(qr.getQuotationState()) && qr.getAvailability() != 2) {
			qr.setQuotationState("Normal");
		}
		if (qr.getAvailability() == 2) {
			qr.setCollectionDate(null);
			if (!"Verify".equals(qr.getQuotationState())){
				qr.setQuotationState("IP");	
			}			
		}
	}
	
	public List<AssignmentSyncData> syncAssignmentRecursiveQuery(List<Integer> assignmentIds, Date lastSyncTime){
		List<AssignmentSyncData> entities = new ArrayList<AssignmentSyncData>();
		if(assignmentIds.size()>2000){
			
			List<Integer> ids = assignmentIds.subList(0, 2000);
			entities.addAll(syncAssignmentRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = assignmentIds.subList(2000, assignmentIds.size());
			entities.addAll(syncAssignmentRecursiveQuery(remainIds, lastSyncTime));
		} else if (assignmentIds.size() > 0){
			return assignmentDao.getUpdatedAssignment(lastSyncTime, assignmentIds.toArray(new Integer[0]), null);
		}
		
		return entities;
	}
	
	public List<AssignmentUnitCategoryInfoSyncData> syncAssignmentUnitCategoryInfoByAssignmentRecursiveQuery(List<Integer> assignmentIds, Date lastSyncTime){
		List<AssignmentUnitCategoryInfoSyncData> entities = new ArrayList<AssignmentUnitCategoryInfoSyncData>();
		if(assignmentIds.size()>2000){
			List<Integer> ids = assignmentIds.subList(0, 2000);
			entities.addAll(syncAssignmentUnitCategoryInfoByAssignmentRecursiveQuery(ids, lastSyncTime));
			
			List<Integer> remainIds = assignmentIds.subList(2000, assignmentIds.size());
			entities.addAll(syncAssignmentUnitCategoryInfoByAssignmentRecursiveQuery(remainIds, lastSyncTime));
		} else if (assignmentIds.size() > 0){
			return assignmentUnitCategoryInfoDao.getUpdatedAssignmentUnitCategoryInfo(lastSyncTime, assignmentIds.toArray(new Integer[0]), null);
		}
		
		return entities;
	}
	
	public List<AssignmentUnitCategoryInfoSyncData> syncAssignmentUnitCategoryInfoByIdsRecursiveQuery(List<Integer> AssignmentUnitCategoryInfoIds){
		List<AssignmentUnitCategoryInfoSyncData> entities = new ArrayList<AssignmentUnitCategoryInfoSyncData>();
		if(AssignmentUnitCategoryInfoIds.size()>2000){
			List<Integer> ids = AssignmentUnitCategoryInfoIds.subList(0, 2000);
			entities.addAll(syncAssignmentUnitCategoryInfoByIdsRecursiveQuery(ids));
			
			List<Integer> remainIds = AssignmentUnitCategoryInfoIds.subList(2000, AssignmentUnitCategoryInfoIds.size());
			entities.addAll(syncAssignmentUnitCategoryInfoByIdsRecursiveQuery(remainIds));
		} else if (AssignmentUnitCategoryInfoIds.size() > 0){
			return assignmentUnitCategoryInfoDao.getUpdatedAssignmentUnitCategoryInfo(null, AssignmentUnitCategoryInfoIds.toArray(new Integer[0]), null);
		}
		
		return entities;
	}
	
	public List<UnitCategoryInfoSyncData> syncUnitCategoryByIdsRecursiveQuery(List<Integer> unitCategoryInfoIds){
		List<UnitCategoryInfoSyncData> entities = new ArrayList<UnitCategoryInfoSyncData>();
		if(unitCategoryInfoIds.size()>2000){
			List<Integer> ids = unitCategoryInfoIds.subList(0, 2000);
			entities.addAll(syncUnitCategoryByIdsRecursiveQuery(ids));
			
			List<Integer> remainIds = unitCategoryInfoIds.subList(2000, unitCategoryInfoIds.size());
			entities.addAll(syncUnitCategoryByIdsRecursiveQuery(remainIds));
		} else if (unitCategoryInfoIds.size() > 0){
			return unitCategoryInfoDao.getUpdatedUnitCategoryInfo(null, unitCategoryInfoIds.toArray(new Integer[0]));
		}
		
		return entities;
	}
	
	public List<AssignmentSyncData> getAssignmentsByAssignmentIds(List<Integer> assignmentIds) {
		return assignmentDao.getAssignmentsByAssignmentIds(assignmentIds);
	}
	
	public List<QuotationRecordSyncData> getHistoryQuotationRecordByAssignmentIds(List<Integer> assignmentIds) {
		return quotationRecordDao.getHistoryQuotationRecordByAssignmentIds(assignmentIds);
	}
	
	public List<QuotationSyncData> getHistoryQuotationByAssignmentIds(List<Integer> assignmentIds) {
		return quotationDao.getHistoryQuotationByAssignmentIds(assignmentIds);
	}
	
	public List<OutletSyncData> getHistoryOutletByAssignmentIds(List<Integer> assignmentIds) {
		return outletDao.getHistoryOutletByAssignmentIds(assignmentIds);
	}
	
	public List<SubPriceRecordSyncData> getHistorySubPriceRecordByAssignmentIds(List<Integer> assignmentIds) {
		return subPriceRecordDao.getHistorySubPriceRecordByAssignmentIds(assignmentIds);
	}
	
	public List<SubPriceColumnSyncData> getHistorySubPriceColumnByAssignmentIds(List<Integer> assignmentIds) {
		return subPriceColumnDao.getHistorySubPriceColumnByAssignmentIds(assignmentIds);
	}
	
	public List<TourRecordSyncData> getHistoryTourRecordByAssignmentIds(List<Integer> assignmentIds) {
		return tourRecordDao.getHistoryTourRecordByAssignmentIds(assignmentIds);
	}	
	
	public List<AssignmentUnitCategoryInfoSyncData> getHistoryAssignmentUnitCategoryInfoByAssignmentIds(
			List<Integer> assignmentIds) {
		return assignmentUnitCategoryInfoDao.getHistoryAssignmentUnitCategoryInfoByAssignmentIds(assignmentIds);
	}

	public List<AssignmentSyncData> getHistoryAssignmentByQuotationIdsHistoryDates(String quotationIdsHistoryDates) {
		return assignmentDao.getHistoryAssignmentByQuotationIdsHistoryDates(quotationIdsHistoryDates);		
	}

	public List<QuotationRecordSyncData> getHistoryQuotationRecordByQuotationIdsHistoryDates(
			String quotationIdsHistoryDates) {
		return quotationRecordDao.getHistoryQuotationRecordByQuotationIdsHistoryDates(quotationIdsHistoryDates);
	}

	public List<QuotationSyncData> getHistoryQuotationByQuotationIdsHistoryDates(String quotationIdsHistoryDates) {
		return quotationDao.getHistoryQuotationByQuotationIdsHistoryDates(quotationIdsHistoryDates);
	}

	public List<OutletSyncData> getHistoryOutletByQuotationIdsHistoryDates(String quotationIdsHistoryDates) {
		return outletDao.getHistoryOutletByQuotationIdsHistoryDates(quotationIdsHistoryDates);
	}

	public List<SubPriceRecordSyncData> getHistorySubPriceRecordByQuotationIdsHistoryDates(
			String quotationIdsHistoryDates) {
		return subPriceRecordDao.getHistorySubPriceRecordByQuotationIdsHistoryDates(quotationIdsHistoryDates);
	}

	public List<SubPriceColumnSyncData> getHistorySubPriceColumnByQuotationIdsHistoryDates(
			String quotationIdsHistoryDates) {
		return subPriceColumnDao.getHistorySubPriceColumnByQuotationIdsHistoryDates(quotationIdsHistoryDates);
	}

	public List<TourRecordSyncData> getHistoryTourRecordByQuotationIdsHistoryDates(String quotationIdsHistoryDates) {
		return tourRecordDao.getHistoryTourRecordByQuotationIdsHistoryDates(quotationIdsHistoryDates);
	}

	public List<AssignmentUnitCategoryInfoSyncData> getHistoryAssignmentUnitCategoryInfoByQuotationIdsHistoryDates(
			String quotationIdsHistoryDates) {
		return assignmentUnitCategoryInfoDao.getHistoryAssignmentUnitCategoryInfoByQuotationIdsHistoryDates(quotationIdsHistoryDates);
	}
	
	public List<Assignment> getAssignmentByIdsRecursiveQuery(List<Integer> assignmentIds){
		List<Assignment> entities = new ArrayList<Assignment>();
		if(assignmentIds.size()>2000){
			List<Integer> ids = assignmentIds.subList(0, 2000);
			entities.addAll(getAssignmentByIdsRecursiveQuery(ids));
			
			List<Integer> remainIds = assignmentIds.subList(2000, assignmentIds.size());
			entities.addAll(getAssignmentByIdsRecursiveQuery(remainIds));
		} else if (assignmentIds.size()>0){
			return assignmentDao.getByIds(assignmentIds.toArray(new Integer[0]));
		}
		return entities;
	}
	
	public AssignmentViewModel prepareAssignmentViewModel(DatatableRequestModel model){
		if (model == null || model.getSearch() == null){
			return null;
		}
		
		AssignmentViewModel viewModel = new AssignmentViewModel();
		
		
		Integer personInChargeId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("personInChargeId"))){
			personInChargeId = Integer.parseInt(model.getSearch().get("personInChargeId"));
			User user = userDao.findById(personInChargeId);
			KeyValueModel keyValue = new KeyValueModel();
			if(!StringUtils.isEmpty(user.getChineseName())) {
				keyValue.setKey(user.getStaffCode() + " - " + user.getChineseName());
			} else {
				keyValue.setKey(user.getStaffCode() + " - " + user.getEnglishName());
			}
			keyValue.setValue(user.getId().toString());
			viewModel.setPersonInChargeSelected(keyValue);
		}
		
		Integer surveyMonthId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("surveyMonthId"))){
			surveyMonthId = Integer.parseInt(model.getSearch().get("surveyMonthId"));
			SurveyMonth surveyMonth = surveyMonthDao.findById(surveyMonthId);
			KeyValueModel keyValue = new KeyValueModel();
			keyValue.setKey(commonService.formatDate(surveyMonth.getStartDate()) + " - " + commonService.formatDate(surveyMonth.getEndDate()));
			keyValue.setValue(String.valueOf(surveyMonth.getId()));
			viewModel.setSurveyMonthSelected(keyValue);
		}
		
		Integer assignmentStatus = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("assignmentStatus"))){
			assignmentStatus = Integer.parseInt(model.getSearch().get("assignmentStatus"));
		}
		viewModel.setAssignmentStatus(assignmentStatus);
		
		String deadline = model.getSearch().get("deadline");
		viewModel.setDeadline(deadline);
		
		String quotationState = model.getSearch().get("quotationState");
		viewModel.setQuotationState(quotationState);
		
		List<String> districtIds = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("districtId"))){
			districtIds = Arrays.asList(model.getSearch().get("districtId").split("\\s*,\\s*"));
		}
		if (districtIds.size()>0){
			viewModel.setDistrictSelected(new ArrayList<KeyValueModel>());
			List<Integer> ids = new ArrayList<Integer>();
			for (String id : districtIds){
				ids.add(Integer.parseInt(id));
			}
			List<District> districts = districtDao.getByIds(ids);
			for (District d : districts){
				KeyValueModel keyValue = new KeyValueModel();
				keyValue.setValue(String.valueOf(d.getId()));
				keyValue.setKey(d.getCode() + " - " + d.getEnglishName());
				viewModel.getDistrictSelected().add(keyValue);
			}
		}
		
		List<String> outletTypeIds = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
			outletTypeIds = Arrays.asList(model.getSearch().get("outletTypeId").split("\\s*,\\s*"));
		}
		if (outletTypeIds.size()>0){
			viewModel.setOutletTypeSelected(new ArrayList<KeyValueModel>());
			List<VwOutletTypeShortForm> outletTypes = vwOutletTypeShortFormDao.getByIds(outletTypeIds.toArray(new String[0]));
			for (VwOutletTypeShortForm outletType : outletTypes){
				KeyValueModel keyValue = new KeyValueModel();
				keyValue.setKey(outletType.getShortCode() + " - " + outletType.getChineseName());
				keyValue.setValue(outletType.getShortCode());
				viewModel.getOutletTypeSelected().add(keyValue);
			}
		}
		
		String search = model.getSearch().get("value");
		viewModel.setSearch(search);
		viewModel.setOrderColumn(Integer.parseInt(model.getOrder().get(0).get("column")));
		viewModel.setOrderDir(model.getOrder().get(0).get("dir"));
		
		return viewModel;
	}

	public List<CheckAssignmentAndQuotationRecordStatus> getAssignmentIds(List<Integer> assignmentIds) {
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = assignmentIds.size() / maxSize;
		int remainder = assignmentIds.size() % maxSize;
		// List<Assignment> assignments =
		// assignmentDao.getAssignmentStatus(null);
		List<Assignment> assignments = new ArrayList<Assignment>();
		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();
		// Quotient
		for (int i = 0; i < times; i++) {
			splited = assignmentIds.subList(fromIdx, toIdx);
			assignments.addAll(assignmentDao.getAssignmentStatus(splited));
			if (i < (times - 1)) {
				fromIdx = toIdx;
				toIdx += maxSize;
			}
		}
		// Remainder
		if (times == 0) {
			if (remainder != 0) {
				splited = assignmentIds.subList(fromIdx, remainder);
			}
		} else {
			if (remainder != 0) {
				splited = assignmentIds.subList(toIdx, (toIdx + remainder));
			}
		}
		if (remainder != 0) {
			assignments.addAll(assignmentDao.getAssignmentStatus(splited));
		}
		List<CheckAssignmentAndQuotationRecordStatus> data = new ArrayList<CheckAssignmentAndQuotationRecordStatus>();
		if (assignments.size() > 0 && !assignments.isEmpty()) {
			int size = assignments.size();
			int curr = 0;
			Integer[] assignmentIdList = new Integer[size];
			for (Assignment a : assignments) {
				assignmentIdList[curr] = a.getAssignmentId();
				curr++;
				// CheckAssignmentAndQuotationRecordStatus result = new
				// CheckAssignmentAndQuotationRecordStatus();
				// result.setAssignmentId(a.getAssignmentId());
				// data.add(result);
			}
			CheckAssignmentAndQuotationRecordStatus result = new CheckAssignmentAndQuotationRecordStatus();
			result.setAssignmentId(assignmentIdList);
			data.add(result);
		}
		return data;
	}
	

	/**
	 * Delete outlet image
	 */
	@Transactional
	public void deleteOutletImage(String path, String fileBaseLoc) {
		File imageFile = new File(fileBaseLoc + path);
		if (imageFile.exists())
			imageFile.delete();
	}
}
