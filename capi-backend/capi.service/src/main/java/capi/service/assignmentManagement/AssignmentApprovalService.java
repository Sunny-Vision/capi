package capi.service.assignmentManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import capi.dal.AssignmentAttributeDao;
import capi.dal.AssignmentDao;
import capi.dal.AssignmentReallocationDao;
import capi.dal.BatchCollectionDateDao;
import capi.dal.BatchDao;
import capi.dal.DistrictDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.dal.OutletDao;
import capi.dal.OutletTypeDao;
import capi.dal.PECheckFormDao;
import capi.dal.PECheckTaskDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.SubPriceColumnDao;
import capi.dal.SubPriceRecordDao;
import capi.dal.SystemConfigurationDao;
import capi.dal.TourRecordDao;
import capi.dal.TpuDao;
import capi.dal.UnitDao;
import capi.dal.UserDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.Assignment;
import capi.entity.AssignmentAttribute;
import capi.entity.Batch;
import capi.entity.BatchCollectionDate;
import capi.entity.District;
import capi.entity.IndoorQuotationRecord;
import capi.entity.Outlet;
import capi.entity.PECheckForm;
import capi.entity.PECheckTask;
import capi.entity.Purpose;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.SystemConfiguration;
import capi.entity.Tpu;
import capi.entity.Unit;
import capi.entity.User;
import capi.entity.VwOutletTypeShortForm;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.QuotationRecordHistoryDateModel;
import capi.model.assignmentManagement.assignmentApproval.AssignmentApprovalViewModel;
import capi.model.assignmentManagement.assignmentApproval.TableList;
import capi.model.assignmentManagement.assignmentManagement.BackTrackDateModel;
import capi.model.assignmentManagement.assignmentManagement.EditModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordPageViewModel;
import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import capi.model.shared.quotationRecord.OutletViewModel;
import capi.model.shared.quotationRecord.ProductPostModel;
import capi.model.shared.quotationRecord.QuotationRecordViewModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;

@Service("AssignmentApprovalService")
public class AssignmentApprovalService extends BaseService {

	@Autowired
	private QuotationRecordDao quotationRecordDao;

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private SystemConfigurationDao systemConfigurationDao;
	
	@Autowired
	private BatchCollectionDateDao batchCollectionDateDao;

	@Autowired
	private AssignmentAttributeDao assignmentAttrDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private PECheckFormDao peCheckFormDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private SubPriceRecordDao subPriceRecordDao;
	
	@Autowired
	private SubPriceColumnDao subPriceColumnDao;
	
	@Autowired
	private TourRecordDao tourRecordDao;
	
	@Autowired
	private IndoorQuotationRecordDao indoorQuotationRecordDao;
	
	@Autowired
	private AssignmentReallocationDao assignmentReallocationDao;
	
	@Autowired
	private VwOutletTypeShortFormDao vwOutletTypeShortFormDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private BatchDao batchDao;
	
	@Autowired
	private TpuDao tpuDao;
	
	@Autowired
	private PurposeDao purposeDao;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;
	
	@Autowired
	private DataConversionService dataConversionService;
	
	@Autowired
	private SurveyMonthService surveyMonthService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private CommonService commonService;

	@Resource(name = "messageSource")
	private MessageSource messageSource;
	
	@Autowired
	private PECheckTaskDao peCheckTaskDao;
	
	@Autowired
	private OutletTypeDao outletTypeDao;
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<TableList> getAssignemntApprovalTableList(DatatableRequestModel model,
			Integer outletId, String outletTypeId, Integer personInChargeId,
			Integer[] districtId, String unitCategory, Integer[] tpuId,
			Boolean isProductChange, Boolean isSPricePeculiar, Integer[] availability, String referenceMonth, Integer[] purposeId) throws Exception {

		Order order = this.getOrder(model, "", "purpose", "firm", "outletType", "district", "tpu",
				"unitDisplayName", "nPrice", "sPrice", "discount", "availability", "reason",
				"isProductChange", "personInCharge", "isSPricePeculiar", "quotationState",
				"priceRemark", "outletCategoryRemark", "address", "unitCategory");
		
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if (!((detail.getAuthorityLevel() & 2) == 2  || (detail.getAuthorityLevel() & 1) == 1)){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		}
		
		Date refMonth = null;
		if (!StringUtils.isEmpty(referenceMonth)){
			refMonth = commonService.getMonth(referenceMonth);
		}
		
		List<TableList> result = quotationRecordDao.getAssignmentApprovalTableList(search, model.getStart(), model.getLength(), order,
				suerpervisorIds,
				outletId, outletTypeId, personInChargeId,
				districtId, unitCategory, tpuId,
				isProductChange, isSPricePeculiar, availability, refMonth, purposeId);
		
		DatatableResponseModel<TableList> response = new DatatableResponseModel<TableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countAssignmentApprovalTableList("", suerpervisorIds,
				null, null, null,
				null, null, null,
				null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countAssignmentApprovalTableList(search, suerpervisorIds,
				outletId, outletTypeId, personInChargeId,
				districtId, unitCategory, tpuId,
				isProductChange, isSPricePeculiar, availability, refMonth, purposeId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<capi.model.assignmentManagement.ruaCaseApproval.TableList> getRUACaseApprovalTableList(DatatableRequestModel model,
			String outletTypeId, Integer personInChargeId,
			Integer[] districtId, Integer[] tpuId) throws Exception {

		Order order = this.getOrder(model, "", "firm", "outletType", "noOfOutletType", "district", "tpu",
				"noOfQuotation", "noOfRUA",
				"personInCharge", "assignmentStatus");
		
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
//		if ((detail.getAuthorityLevel() & 4) == 4){
		if (!(((detail.getAuthorityLevel() & 1) == 1) || ((detail.getAuthorityLevel() & 2) == 2))){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		}
		
		List<capi.model.assignmentManagement.ruaCaseApproval.TableList> result = assignmentDao.getRUACaseApprovalTableList(search, model.getStart(), model.getLength(), order,
				suerpervisorIds,
				outletTypeId, personInChargeId,
				districtId, tpuId);
		
		DatatableResponseModel<capi.model.assignmentManagement.ruaCaseApproval.TableList> response = new DatatableResponseModel<capi.model.assignmentManagement.ruaCaseApproval.TableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = assignmentDao.countRUACaseApprovalTableList("", suerpervisorIds,
				null, null, null,
				null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countRUACaseApprovalTableList(search, suerpervisorIds,
				outletTypeId, personInChargeId,
				districtId, tpuId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public List<Integer> getAssignemntApprovalTableListAllIds(DatatableRequestModel model,
			List<Integer> userIds,
			Integer outletId, String outletTypeId, Integer personInChargeId,
			Integer[] districtId, String unitCategory, Integer[] tpuId,
			Boolean isProductChange, Boolean isSPricePeculiar, Integer[] availability, String referenceMonth, Integer[] purposeId) throws Exception {
		
		Order order = this.getOrder(model, "", "purpose", "firm", "outletType", "district", "tpu",
				"unitDisplayName", "nPrice", "sPrice", "discount", "availability", "reason",
				"isProductChange", "personInCharge", "isSPricePeculiar", "quotationState",
				"priceRemark", "outletCategoryRemark", "address", "unitCategory");
		
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();

		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if (!((detail.getAuthorityLevel() & 2) == 2  || (detail.getAuthorityLevel() & 1) == 1)){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		}
		
		Date refMonth = null;
		if (!StringUtils.isEmpty(referenceMonth)){
			refMonth = commonService.getMonth(referenceMonth);
		}
		
		List<TableList> result = quotationRecordDao.getAssignmentApprovalTableList(search, 0, 0, order,
				suerpervisorIds,
				outletId, outletTypeId, personInChargeId,
				districtId, unitCategory, tpuId,
				isProductChange, isSPricePeculiar, availability, refMonth, purposeId);
		
		List<Integer> ids = new ArrayList<Integer>();
		for (TableList row : result) {
			ids.add(row.getQuotationRecordId());
		}
		return ids;
	}

	/**
	 * DataTable query
	 */
	public DatatableResponseModel<capi.model.assignmentManagement.newRecruitmentApproval.TableList> getNewRecruitmentApprovalTableList(DatatableRequestModel model,
			String outletTypeId) throws Exception {

		Order order = this.getOrder(model, "", "firmCode", "firm", "", "district", "tpu", "address",
				"", "noOfQuotationRecruited", "",
				"referenceMonth", "newRecruitmentDate",
				"personInCharge", "newFirm");
		
		String search = model.getSearch().get("value");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if (!((detail.getAuthorityLevel() & 1) == 1 || (detail.getAuthorityLevel() & 2) == 2)){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		}
		
		List<capi.model.assignmentManagement.newRecruitmentApproval.TableList> results = assignmentDao.getNewRecruitmentApprovalTableList(search, model.getStart(), model.getLength(), order,
				suerpervisorIds,
				outletTypeId);
		
		for (capi.model.assignmentManagement.newRecruitmentApproval.TableList result : results){
			long originalNoOfQuotation = outletDao.countQuotationByOutletId(result.getOutletId());
			result.setOriginalNoOfQuotation(originalNoOfQuotation);
			result.setNoOfQuotationAfterRecruitment(result.getNoOfQuotationRecruited()+originalNoOfQuotation);
			
			List<String> outletTypeCode = outletTypeDao.getShortCodeByAssignmentId(result.getAssignmentId());
			result.setOutletType(StringUtils.join(outletTypeCode, ','));
		}
		
		DatatableResponseModel<capi.model.assignmentManagement.newRecruitmentApproval.TableList> response = new DatatableResponseModel<capi.model.assignmentManagement.newRecruitmentApproval.TableList>();
		response.setDraw(model.getDraw());
		response.setData(results);
		Long recordTotal = assignmentDao.countNewRecruitmentApprovalTableList("", suerpervisorIds,
				null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = assignmentDao.countNewRecruitmentApprovalTableList(search, suerpervisorIds,
				outletTypeId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
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
		if (quotationRecord.getReferenceDate() == null) {
			quotationRecord.setReferenceDate(new Date());
		}

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
			productViewModel.setShowSpecDialog(true);
			model.setProduct(productViewModel);
			
			if (quotationRecord.getOutlet() != null) {
				OutletViewModel outletViewModel = quotationRecordService.prepareOutletViewModel(quotationRecord.getOutlet());
				model.setOutlet(outletViewModel);
			}
			
			model.setHistories(histories);
			
			model.setPointToNote(commonService.nl2br(assignmentMaintenanceService.concatPointToNotes(quotationRecord)));
			model.setVerificationRemark(commonService.nl2br(quotationRecord.getVerificationRemark()));
			model.setRejectReason(commonService.nl2br(quotationRecord.getRejectReason()));
			model.setPeCheckRemark(commonService.nl2br(quotationRecord.getPeCheckRemark()));
			model.setValidationSummary(commonService.nl2br(quotationRecord.getValidationError()));
			
			if (sessionModel != null) {
				model.setAssignmentId(sessionModel.getAssignmentId());
				model.setUserId(sessionModel.getUserId());
				if (quotationRecord.isBackTrack())
					assignmentMaintenanceService.determineNextAndPreviousId(quotationRecord.getOriginalQuotationRecord().getId(), sessionModel, false);
				else
					assignmentMaintenanceService.determineNextAndPreviousId(id, sessionModel, false);
				
				model.setNextQuotationRecordId(sessionModel.getNextId());
				model.setPreviousQuotationRecordId(sessionModel.getPreviousId());
				model.setCurrentQuotationRecordNumber(sessionModel.getCurrentPositionInIds());
				model.setTotalQuotationRecords(sessionModel.getIds().size());
				
				List<BackTrackDateModel> backTrackDates;
				if (quotationRecord.isBackTrack())
					backTrackDates = assignmentMaintenanceService.prepareBackTrackDatesSelect(quotationRecord.getOriginalQuotationRecord());
				else
					backTrackDates = assignmentMaintenanceService.prepareBackTrackDatesSelect(quotationRecord);
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
		}
		
		return model;
	}
	

	/**
	 * Check quotation (records) reached maximum number allowed in an outlet (stated
	 * in quotation.unit):
	 * If outlet quotation count + approving quotation count <= max allowed, allow approving this quotation record; 
	 * otherwise cannot approve.
	 */
	private boolean canAddQuotationRecordToOutlet(QuotationRecord qr, int submitUnitCount) {
		Unit unit = qr.getQuotation().getUnit();

		// if outlet quotation count + approving quotation count <= max allowed, allow approving this QR; otherwise cannot approve
		final long unitCountInOutlet = quotationRecordDao.getQuotationCountInOutlet(unit.getId(), qr.getOutlet().getId());
		return unitCountInOutlet + submitUnitCount <= unit.getMaxQuotation();
	}

	public List<QuotationRecord> filterCannotApproveQuotationRecordIds(List<Integer> quotationRecordIds) {
		List<QuotationRecord> qrList = quotationRecordService.recursiveQuery(quotationRecordIds);

		// Count frequency of unit from each quotation records
		// use for checking max no. of quotation allowed in outlet:
		// user may submit multiple quotation (QR) of same unit (but different quotations)
		// output is map of (quotation UNIT ID) to (count of quotation UNIT found in quotationRecordIds)
		Map<Integer, Integer> qrUnitFrequency = qrList.stream().collect(
				Collectors.toMap(qr -> ((QuotationRecord) qr).getQuotation().getUnit().getId(), qr -> 1, Integer::sum));

		// For each QR, count the quotation in existing outlet by QR's unit;
		// return list of QR canNOT be approved (for website response error detail)
		List<QuotationRecord> qrListCannotApprove = IntStream.range(0, qrList.size())
				.filter(i -> {
					QuotationRecord qr = qrList.get(i);
					int unitId = qr.getQuotation().getUnit().getId();
					return canAddQuotationRecordToOutlet(qr, qrUnitFrequency.get(unitId)) == false;
				})
				.mapToObj(i -> qrList.get(i))
				.collect(Collectors.toList());

		return qrListCannotApprove;
	}

	/**
	 * Approve quotation records
	 */
	@Transactional
	public boolean approveQuotationRecordIds(List<Integer> quotationRecordIds) {
		List<Integer> ids = new ArrayList<Integer>();
		ids.addAll(quotationRecordIds);
		List<List<Integer>> splitQuotationRecordIds = commonService.splitListByMaxSize(quotationRecordIds);
		for (List<Integer> subIds : splitQuotationRecordIds) {
			List<Integer> backtrackIds = quotationRecordDao.getBackTrackQuotationRecordByQuotationRecordIds(subIds);
			if (backtrackIds != null && backtrackIds.size() > 0) {
				ids.addAll(backtrackIds);
			}
		}
		List<QuotationRecord> entities = quotationRecordService.recursiveQuery(ids);
		for (QuotationRecord entity : entities) {
			approveQuotationRecordButNotCommit(entity);
		}
		
		sendNotificationMessage(entities, false);
		
		quotationRecordDao.flush();
		return true;
	}
	
	/**
	 * Approve All Quotation Records
	 */
	@Transactional
	public boolean approveQuotationRecords(List<QuotationRecord> quotationRecords) {
		for (QuotationRecord entity : quotationRecords) {
			approveQuotationRecordButNotCommit(entity);
		}
		
		sendNotificationMessage(quotationRecords, false);
		
		quotationRecordDao.flush();
		return true;
	}
	
	/**
	 * Reject quotation records
	 */
	@Transactional
	public boolean rejectQuotationRecords(List<Integer> quotationRecordIds, String rejectReason) {
		List<List<Integer>> splitQuotationRecordIds = commonService.splitListByMaxSize(quotationRecordIds);
		for (List<Integer> subIds : splitQuotationRecordIds) {
			List<Integer> backtrackIds = quotationRecordDao.getBackTrackQuotationRecordByQuotationRecordIds(subIds);
			if (backtrackIds != null && backtrackIds.size() > 0) {
				quotationRecordIds.addAll(backtrackIds);
			}
		}
		
		List<QuotationRecord> entities = quotationRecordDao.getByIds(quotationRecordIds.toArray(new Integer[0]));
		for (QuotationRecord entity : entities) {
			rejectQuotationRecordButNotCommit(entity, rejectReason);
		}
		
		sendNotificationMessage(entities, true);

		quotationRecordDao.flush();
		return true;
	}
	
	/**
	 * Approve quotation record
	 */
	@Transactional
	public void approveQuotationRecordButNotCommit(QuotationRecord entity) {
		Date today = new Date();
		today = DateUtils.truncate(today, Calendar.DAY_OF_MONTH);
		entity.setApprovedDate(today);
		entity.getAssignment().setApprovedDate(today);
		
		if (entity.getAvailability() == 5) { // not suitable
			entity.setStatus("Approved");
			entity.getQuotation().setStatus("RUA");
			entity.getQuotation().setDistrict(entity.getOutlet().getTpu().getDistrict());
			entity.getQuotation().setRuaDate(entity.getCollectionDate());
			
			quotationRecordService.deleteQuotationRecordForRUA(entity.getAssignment().getSurveyMonth().getId(), entity.getQuotation().getId());
		} else if (entity.isNewRecruitment() && !entity.getQuotation().getStatus().equals("RUA")) {
			throw new ServiceException("E00139");
		} else if (entity.isNewRecruitment()) {
			entity.getQuotation().setStatus("Active");
			entity.getQuotation().setProduct(entity.getProduct());
			entity.getQuotation().setOutlet(entity.getOutlet());
			entity.setStatus("Approved");
			runNewRecruitmentLogic(entity);
			quotationRecordDao.save(entity);
		} else {
			entity.getQuotation().setProduct(entity.getProduct());
			if (entity.isProductChange()){
				if (entity.getQuotation().getLastProductChangeDate() == null){
					entity.getQuotation().setLastProductChangeDate(entity.getCollectionDate());
				} else if (entity.getQuotation().getLastProductChangeDate() != null &&
						entity.getCollectionDate().after(entity.getQuotation().getLastProductChangeDate())){
					entity.getQuotation().setLastProductChangeDate(entity.getCollectionDate());
				}
			}
			if (entity.getQuotationState().equals("Normal")) {
				entity.setStatus("Approved");
				if (entity.getAvailability() == 2) { // IP
					entity.setQuotationState("IP");
					entity.setCollectionDate(null);
				} else if (entity.getAvailability() == 6) { // 回倉
					Date todayMonth = new Date();
					DateUtils.truncate(todayMonth, Calendar.MONTH);
					entity.getQuotation().setSeasonalWithdrawal(todayMonth);
				}
			} else if (entity.getQuotationState().equals("IP")) {
				entity.setStatus("Approved");
				if (entity.getAvailability() != 2) { // IP
					entity.setQuotationState("Normal");
					entity.setReferenceDate(entity.getCollectionDate());
				} 
				if (entity.getAvailability() == 6) { // 回倉
					Date todayMonth = new Date();
					DateUtils.truncate(todayMonth, Calendar.MONTH);
					entity.getQuotation().setSeasonalWithdrawal(todayMonth);
				}
			} else if (entity.getQuotationState().equals("Revisit") || entity.getQuotationState().equals("Verify")) {
				entity.setStatus("Approved");
				if (entity.getAvailability() == 2) { // IP
					entity.setQuotationState("IP");
					entity.setCollectionDate(null);
				} else if (entity.getAvailability() == 6) { // 回倉
					Date todayMonth = new Date();
					DateUtils.truncate(todayMonth, Calendar.MONTH);
					entity.getQuotation().setSeasonalWithdrawal(todayMonth);
				}
//				entity.setVisited(false);
			}
			quotationDao.save(entity.getQuotation());
			quotationRecordDao.save(entity);
		}
	}
	
	/**
	 * Approve quotation record
	 */
	@Transactional
	public void rejectQuotationRecordButNotCommit(QuotationRecord entity, String rejectReason) {
		entity.setStatus("Rejected");
		entity.setRejectReason(rejectReason);
		if (entity.getQuotationState().equals("Revisit") || entity.getQuotationState().equals("Verify")) {
			entity.setVisited(false);
		}
		quotationRecordDao.save(entity);
	}
	
	/**
	 * Update session after submit
	 */
	@Transactional
	public void updateSessionAfterSubmit(int quotationRecordId, SessionModel sessionModel) {
		assignmentMaintenanceService.determineNextAndPreviousId(quotationRecordId, sessionModel, true);
	}
	
	/**
	 * After data conversion
	 */
	@Transactional
	public void runPELogic(List<QuotationRecord> entities) {
		SystemConfiguration includeRUACaseConfig = systemConfigurationDao.findByName(SystemConstant.PE_PARAM_INCLUDE_RUA_CASE);
		boolean includeRUACase = includeRUACaseConfig != null ? includeRUACaseConfig.getValue().equals("1") : false;

		SystemConfiguration includeNewRecruitmentConfig = systemConfigurationDao.findByName(SystemConstant.PE_PARAM_INCLUDE_NEW_RECRUITMENT);
		boolean includeNewRecruitment = includeNewRecruitmentConfig != null ? includeNewRecruitmentConfig.getValue().equals("1") : false;
		
		List<Integer> assignmentIds = new ArrayList<Integer>();
		for (QuotationRecord entity : entities) {
			// if not(record.availability = not suitable and system parameter include RUA case)
			if (!assignmentIds.contains(entity.getAssignment().getAssignmentId())){
				assignmentIds.add(entity.getAssignment().getAssignmentId());
			}			
			
			if (entity.getAvailability() == 5 && includeRUACase) {
				if (entity.getAssignment().getPeCheckTask() != null && entity.getAssignment().getPeCheckTask().isSelected()) continue;
				
				runPEGeneralRule(entity.getAssignment().getAssignmentId());
			} else if (entity.isNewRecruitment() && includeNewRecruitment) {
				if (entity.getAssignment().getPeCheckTask() != null && entity.getAssignment().getPeCheckTask().isSelected()) continue;
				
				runPEGeneralRule(entity.getAssignment().getAssignmentId());
			} else if (entity.getAssignment().getStatus() != null && entity.getAssignment().getStatus() != 1) {
				if (entity.getAssignment().getPeCheckTask() !=null && entity.getAssignment().getPeCheckTask().isSelected()) {
					entity.getAssignment().getPeCheckTask().setSelected(false);
					
					List<PECheckTask> randomTaskAndForms = assignmentMaintenanceService.generateRandomCase(entity.getUser().getId(), entity.getAssignment().getSurveyMonth().getId(), 1);
					
					for (PECheckTask taskAndForm : randomTaskAndForms) {
						taskAndForm.setFieldTeamHead(entity.getAssignment().getPeCheckTask().isFieldTeamHead());
						taskAndForm.setSectionHead(entity.getAssignment().getPeCheckTask().isSectionHead());
						assignmentIds.add(taskAndForm.getAssignment().getId());
						/*
						if (entity.getAssignment().getPeCheckTask().isFieldTeamHead()) {
							List<User> fieldTeamHeads = userDao.getActiveUsersWithAuthorityLevel(2, null, 1);
							if (fieldTeamHeads.size() > 0)
								taskAndForm.getForm().setUser(fieldTeamHeads.get(0));
							else
								taskAndForm.getForm().setUser(null);
						} else if (entity.getAssignment().getPeCheckTask().isSectionHead()) {
							List<User> fieldTeamHeads = userDao.getActiveUsersWithAuthorityLevel(1, null, 1);
							if (fieldTeamHeads.size() > 0)
								taskAndForm.getForm().setUser(fieldTeamHeads.get(0));
							else
								taskAndForm.getForm().setUser(null);
						} else {
							taskAndForm.getForm().setUser(entity.getAssignment().getUser().getSupervisor());
						}*/
					}
					
					entity.getAssignment().getPeCheckTask().setFieldTeamHead(false);
					entity.getAssignment().getPeCheckTask().setSectionHead(false);
				}
			}
		}
		
		// generate PE form if the whole assignment is approved (i.e. all quotation records in the assignment is approved)
		generatePEForm(assignmentIds);
		
		for (QuotationRecord entity : entities) {
			if (entity.getQuotationState().equals("Verify")) {
				entity.getIndoorQuotationRecord().setFirmVerify(false);
				entity.getIndoorQuotationRecord().setCategoryVerify(false);
				entity.getIndoorQuotationRecord().setQuotationVerify(false);
				entity.setOutletDiscountRemark(null);
				entity.setCategoryRemark(null);
				entity.getQuotation().setProductRemark(null);
			}
		}
	}
	
	// generate PE form if the whole assignment is approved (i.e. all quotation records in the assignment is approved)
	public void generatePEForm (List<Integer> assignmentIds){
		List<Integer> approvedIds = assignmentDao.getApprovedAssignment(assignmentIds);
		List<Assignment> approvedAssignments = assignmentDao.getByIds(approvedIds.toArray(new Integer[0]));		
		if (approvedAssignments != null && approvedAssignments.size() > 0){
			// get field head and section head if any
			List<User> heads = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD, null);
			User fieldHead = null;
			if (heads != null && heads.size() > 0){
				 fieldHead = heads.get(0);
			}
			User sectionHead = null;
			heads = userDao.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_SECTION_HEAD, null);
			if (heads != null && heads.size() > 0){
				sectionHead = heads.get(0);
			}
			for (Assignment assignment : approvedAssignments){
				if (assignment.getPeCheckTask() != null && assignment.getPeCheckTask().isSelected() && assignment.getPeCheckForm() == null){
					PECheckForm form = assignment.getPeCheckForm();
					if (form == null) {
						form = new PECheckForm();
						form.setAssignment(assignment);
					}
					form.setOfficer(assignment.getUser());
					if (assignment.getPeCheckTask().isFieldTeamHead()) {
						form.setUser(fieldHead);
					} else if (assignment.getPeCheckTask().isSectionHead()) {
						form.setUser(sectionHead);
					} else {
						form.setUser(assignment.getUser().getSupervisor());
					}
					peCheckFormDao.save(form);
				}
			}
		}	
	}
	
	/**
	 * run PE general rule
	 * remove one random case and add pe case for the assignment
	 */
	@Transactional
	public void runPEGeneralRule(Integer assignmentId) {
		Assignment assignment = assignmentDao.findById(assignmentId);
		 List<PECheckTask> list = peCheckTaskDao.getNotSubmittedRandomCase(assignment.getUser().getId(), 1, assignment.getSurveyMonth().getSurveyMonthId());
		 PECheckTask oldTask = null;
		 if (list.size() > 0){
			 oldTask = list.get(0);
			//if (assignmentForPE == null || assignmentForPE.getPeCheckTask() == null || !assignmentForPE.getPeCheckTask().isSelected()) return;
			
			if (oldTask.getAssignment().getPeCheckForm() != null){
				peCheckFormDao.delete(oldTask.getAssignment().getPeCheckForm());			
			}
			oldTask.setSelected(false);
		}		
		
		PECheckTask task = null;
		if (assignment.getPeCheckTask() == null) {
			task = new PECheckTask();
			assignment.setPeCheckTask(task);
		}
		else{
			task = assignment.getPeCheckTask();
		}
		task.setAssignment(assignment);
		task.setSurveyMonth(assignment.getSurveyMonth());
		task.setSelected(true);
		task.setCertaintyCase(true);
		task.setRandomCase(false);
		if (oldTask != null){
			task.setFieldTeamHead(oldTask.isFieldTeamHead());
			task.setSectionHead(oldTask.isSectionHead());
			oldTask.setFieldTeamHead(false);
			oldTask.setSectionHead(false);
			peCheckTaskDao.save(oldTask);
		}
		assignment.setPeCheckTask(task);
		
		peCheckTaskDao.save(task);
		assignmentDao.flush();
		
	}
	
	
	/**
	 * run new recruitment logic
	 */
	@Transactional
	public void runNewRecruitmentLogic(QuotationRecord entity) {
		entity.getQuotation().setLastProductChangeDate(entity.getCollectionDate());
		
		List<QuotationRecord> recordListWithoutAssignment = new ArrayList<QuotationRecord>();
		
		if (entity.getQuotation().getBatch().getAssignmentType() == 1) {
			List<QuotationRecord> generatedQuotationRecords = quotationRecordGeneration(entity);
			for (QuotationRecord generatedEntity : generatedQuotationRecords) {
				if (!generatedEntity.isBackTrack()) {
					Assignment selectedAssignment = assignmentDao.getOneAssignmentByCollectiondateOutletUser(generatedEntity.getCollectionDate(), generatedEntity.getOutlet().getId(), generatedEntity.getUser().getId());
					
					if (selectedAssignment == null) {
						recordListWithoutAssignment.add(generatedEntity);
					} else {
						generatedEntity.setAssignment(selectedAssignment);
						for (QuotationRecord otherRecords : generatedEntity.getOtherQuotationRecords()) {
							otherRecords.setAssignment(selectedAssignment);
						}
					}
				}
				quotationRecordDao.save(generatedEntity);
			}
			
			surveyMonthService.assignmentGeneration(entity.getAssignment().getSurveyMonth(), recordListWithoutAssignment);
			quotationRecordDao.flush();
		}
	}
	
	/**
	 * quotation record generation
	 */
	public List<QuotationRecord> quotationRecordGeneration(QuotationRecord entity) {
		// copy SurveyMonthService.quotationRecordGeneration case 1 for loop
		Quotation q = entity.getQuotation();
		QuotationRecord baseQr = new QuotationRecord();
		
		List<QuotationRecord> quotationRecords = new ArrayList<QuotationRecord>();
		List<AssignmentAttribute> attrs = assignmentAttrDao.findAssignmentAttributeByBatchAndSurveyMonth(entity.getQuotation().getBatch().getId(), entity.getAssignment().getSurveyMonth().getId());
		
		Date today = new Date();
		today = DateUtils.truncate(today, Calendar.DAY_OF_MONTH);
		for (AssignmentAttribute attr : attrs) {
			List<BatchCollectionDate> bcdList = this.batchCollectionDateDao.findBatchCollectionDateByAssignmentAttributeAfterDate(attr, today);
			
			User user = attr.getUser();
			capi.entity.AllocationBatch ab = attr.getAllocationBatch();
			for (BatchCollectionDate bcd : bcdList) {
				QuotationRecord qr = new QuotationRecord();
				qr.setQuotation(q);
				qr.setOutlet(q.getOutlet());
				qr.setCollectFR(baseQr.isCollectFR());
				qr.setAllocationBatch(ab);
				qr.setProduct(q.getProduct());
				qr.setUser(user);
				qr.setHistoryDate(bcd.getDate());
				qr.setReferenceDate(bcd.getDate());
				qr.setAssignedCollectionDate(bcd.getDate());
				qr.setCollectionDate(bcd.getDate());
				qr.setSpecifiedUser(true);
				qr.setFormDisplay(q.getUnit().getFormDisplay());
				qr.setQuotationState("Normal");
				qr.setStatus("Blank");
				qr.setAvailability(1);
				qr.setFirmStatus(1);
				//this.quotationRecordDao.save(qr);
				quotationRecords.add(qr);
				
				if(bcd.getBackTrackDate1() != null){
					QuotationRecord qrbtd1 = new QuotationRecord();
					qrbtd1.setQuotation(q);
					qrbtd1.setOutlet(q.getOutlet());
					qrbtd1.setProduct(q.getProduct());
					qrbtd1.setCollectFR(baseQr.isCollectFR());
					qrbtd1.setAllocationBatch(ab);
					qrbtd1.setUser(user);
					qrbtd1.setHistoryDate(bcd.getBackTrackDate1());
					qrbtd1.setReferenceDate(bcd.getBackTrackDate1());
					qrbtd1.setAssignedCollectionDate(bcd.getBackTrackDate1());
					qrbtd1.setCollectionDate(bcd.getBackTrackDate1());
					qrbtd1.setSpecifiedUser(true);
					qrbtd1.setBackTrack(true);
					qrbtd1.setOriginalQuotationRecord(qr);
					qrbtd1.setFormDisplay(q.getUnit().getFormDisplay());
					qrbtd1.setQuotationState("Normal");
					qrbtd1.setStatus("Blank");
					qrbtd1.setAvailability(1);
					qrbtd1.setFirmStatus(1);
					
					qr.getOtherQuotationRecords().add(qrbtd1);
					//this.quotationRecordDao.save(qrbtd1);
					//quotationRecords.add(qrbtd1);
				}
				if(bcd.getBackTrackDate2() != null){
					QuotationRecord qrbtd2 = new QuotationRecord();
					qrbtd2.setQuotation(q);
					qrbtd2.setOutlet(q.getOutlet());
					qrbtd2.setCollectFR(baseQr.isCollectFR());
					qrbtd2.setAllocationBatch(ab);
					qrbtd2.setProduct(q.getProduct());
					qrbtd2.setUser(user);
					qrbtd2.setHistoryDate(bcd.getBackTrackDate2());
					qrbtd2.setReferenceDate(bcd.getBackTrackDate2());
					qrbtd2.setAssignedCollectionDate(bcd.getBackTrackDate2());
					qrbtd2.setCollectionDate(bcd.getBackTrackDate2());
					qrbtd2.setSpecifiedUser(true);
					qrbtd2.setBackTrack(true);
					qrbtd2.setOriginalQuotationRecord(qr);
					qrbtd2.setFormDisplay(q.getUnit().getFormDisplay());
					qrbtd2.setQuotationState("Normal");
					qrbtd2.setStatus("Blank");
					qrbtd2.setAvailability(1);
					qrbtd2.setFirmStatus(1);
					
					qr.getOtherQuotationRecords().add(qrbtd2);
					//this.quotationRecordDao.save(qrbtd2);
					//quotationRecords.add(qrbtd2);
				}
				if(bcd.getBackTrackDate3() != null){
					QuotationRecord qrbtd3 = new QuotationRecord();
					qrbtd3.setQuotation(q);
					qrbtd3.setOutlet(q.getOutlet());
					qrbtd3.setCollectFR(baseQr.isCollectFR());
					qrbtd3.setAllocationBatch(ab);
					qrbtd3.setProduct(q.getProduct());
					qrbtd3.setUser(user);
					qrbtd3.setHistoryDate(bcd.getBackTrackDate3());
					qrbtd3.setReferenceDate(bcd.getBackTrackDate3());
					qrbtd3.setAssignedCollectionDate(bcd.getBackTrackDate3());
					qrbtd3.setCollectionDate(bcd.getBackTrackDate3());
					qrbtd3.setSpecifiedUser(true);
					qrbtd3.setBackTrack(true);
					qrbtd3.setOriginalQuotationRecord(qr);
					qrbtd3.setFormDisplay(q.getUnit().getFormDisplay());
					qrbtd3.setQuotationState("Normal");
					qrbtd3.setStatus("Blank");
					qrbtd3.setAvailability(1);
					qrbtd3.setFirmStatus(1);
					
					qr.getOtherQuotationRecords().add(qrbtd3);
					//this.quotationRecordDao.save(qrbtd3);
					//quotationRecords.add(qrbtd3);
				}
			}
		}
		
		return quotationRecords;
	}
	
	private void sendNotificationMessage(List<QuotationRecord> list, boolean isReject){
		HashMap<User, HashMap<Assignment,List<QuotationRecord>>> groupIdsByUser = groupQuotationRecordByUser(list);		
		
		for (User user: groupIdsByUser.keySet()){
			StringBuilder builder = new StringBuilder();
			List<Integer> rejectedIds = new ArrayList<Integer>();
			
			HashMap<Assignment, List<QuotationRecord>> assignments = groupIdsByUser.get(user);
			
			for (Assignment assignment : assignments.keySet()){
				List<QuotationRecord> quotationRecords = assignments.get(assignment);
				Outlet outlet = assignment.getOutlet();
				if (isReject){
					for (QuotationRecord qr : quotationRecords) {
						if (qr.isNewRecruitment()){
							rejectedIds.add(qr.getQuotation().getId());
						}
					}
				}
				builder.append(messageSource.getMessage("N00067", 
						new Object[]{outlet.getFirmCode(), outlet.getName(), quotationRecords.size()}, 
						Locale.ENGLISH)+"\n");
			}
			
			String content = builder.toString();
			if(isReject){
				String subject = messageSource.getMessage("N00045", null, Locale.ENGLISH);
				String contentHeader = messageSource.getMessage("N00047", null, Locale.ENGLISH);
				String rejectedQuotationIds = StringUtils.join(rejectedIds, ",");
				notificationService.sendNotification(user, subject, contentHeader + "\n" + content, rejectedQuotationIds, false);
			} else {
				String subject = messageSource.getMessage("N00070", null, Locale.ENGLISH);
				String contentHeader = messageSource.getMessage("N00046", null, Locale.ENGLISH);
				notificationService.sendNotification(user, subject, contentHeader + "\n" + content, null, false);
			}
		}
	}
	
	public HashMap<User, HashMap<Assignment, List<QuotationRecord>>> groupQuotationRecordByUser(List<QuotationRecord> quotationRecords) {
		HashMap<User, HashMap<Assignment, List<QuotationRecord>>> groupIdsByUser = new HashMap<User, HashMap<Assignment, List<QuotationRecord>>>();
		
		for (QuotationRecord quotationRecord : quotationRecords) {
			if (groupIdsByUser.containsKey(quotationRecord.getUser())) {
				HashMap<Assignment, List<QuotationRecord>> groupedAssignmentList = groupIdsByUser.get(quotationRecord.getUser());
				if (groupedAssignmentList.containsKey(quotationRecord.getAssignment())){
					groupedAssignmentList.get(quotationRecord.getAssignment()).add(quotationRecord);
				} else {
					List<QuotationRecord> list = new ArrayList<QuotationRecord>();
					list.add(quotationRecord);
					groupedAssignmentList.put(quotationRecord.getAssignment(), list);
				}
			} else {
				List<QuotationRecord> list = new ArrayList<QuotationRecord>();
				list.add(quotationRecord);
				HashMap<Assignment, List<QuotationRecord>> groupedAssignmentList = new HashMap<Assignment, List<QuotationRecord>>();
				groupedAssignmentList.put(quotationRecord.getAssignment(), list);
				groupIdsByUser.put(quotationRecord.getUser(), groupedAssignmentList);
			}
		}
		
		return groupIdsByUser;
	}
	
	/**
	 * Approve assignments
	 */
	@Transactional
	public boolean approveAssignments(List<Integer> assignmentIds) {
		List<Integer> quotationRecordIds = quotationRecordDao.getAllIdsSubmittedAndRUAByAssignments(assignmentIds);
		
		return approveQuotationRecordIds(quotationRecordIds);
	}

	/**
	 * Reject assignments
	 */
	@Transactional
	public boolean rejectAssignments(List<Integer> assignmentIds, String rejectReason) {
		List<Integer> quotationRecordIds = quotationRecordDao.getAllIdsSubmittedAndRUAByAssignments(assignmentIds);
		
		return rejectQuotationRecords(quotationRecordIds, rejectReason);
	}

	/**
	 * Approve outlets
	 */
	@Transactional
	public List<Integer> approveOutlets(List<Integer> outletIds) {
		return quotationRecordDao.getAllIdsNewRecruitmentByOutlets(outletIds);
	}

	/**
	 * Get quotation records of matching outlet and user pairs
	 */
	@Transactional
	public List<Integer> getQRidsForOutletAndUserPairs(List<Map.Entry<Integer, Integer>> outletIdUserIdPairs) {
		return quotationRecordDao.getIdsForOutletAndUserPairs(outletIdUserIdPairs);
	}

	/**
	 * Reject outlets
	 */
	@Transactional
	public boolean rejectOutlets(List<Integer> outletIds, String rejectReason) {
		List<Integer> quotationRecordIds = quotationRecordDao.getAllIdsNewRecruitmentByOutlets(outletIds);
		
		return rejectQuotationRecords(quotationRecordIds, rejectReason);
	}

	/**
	 * Reject outlets
	 */
	@Transactional
	public boolean rejectNewRecruitments(List<Map.Entry<Integer, Integer>> outletIdUserIdPairs, String rejectReason) {
		List<Integer> quotationRecordIds = quotationRecordDao.getIdsForOutletAndUserPairs(outletIdUserIdPairs);
		
		return rejectQuotationRecords(quotationRecordIds, rejectReason);
	}
	
	/**
	 * Save batch
	 */
	@Transactional
	public void saveBatch(int quotationRecordId, int batchId) {
		QuotationRecord entity = quotationRecordDao.findById(quotationRecordId);
		Quotation quotation = entity.getQuotation();
		Batch batch = batchDao.findById(batchId);
		quotation.setBatch(batch);
		quotationDao.save(quotation);
		quotationDao.flush();
	}
	
	/**
	 * After data conversion
	 */
	public void handleVerification(QuotationRecord entity) {
		if (entity.getQuotationState().equals("Revisit")) {
			entity.getIndoorQuotationRecord().setStatus("Allocation");
			entity.getIndoorQuotationRecord().setUser(null);
		}
		
		if (entity.getQuotationState().equals("Verify")) {
			IndoorQuotationRecord indoor = entity.getIndoorQuotationRecord();
			
			if (indoor.getStatus().equals("Approve Verification") || indoor.getStatus().equals("Review Verification")) {
				indoor.setFirmVerify(false);
				indoor.setCategoryVerify(false);
				indoor.setQuotationVerify(false);
				indoor.setFirmRemark(null);
				indoor.setCategoryRemark(null);
				indoor.setQuotationRemark(null);
				
				if (indoor.getStatus().equals("Approve Verification")) {
					if (indoor.getUser() == null){
						indoor.setStatus("Allocation");
					} else {
						indoor.setStatus("Conversion");
					}
				} else if (indoor.getStatus().equals("Review Verification")) {
					indoor.setStatus("Allocation");
					indoor.setUser(null);
				}
				
				entity.setVerifyFirm(false);
				entity.setVerifyCategory(false);
				entity.setVerifyQuotation(false);
//				entity.setVerificationRemark(null);
				
				entity.setByPassLog(true);
				quotationRecordDao.save(entity);
				indoor.setByPassLog(true);
				indoorQuotationRecordDao.save(indoor);
			}
		}
	}

	/**
	 * Prepare view model
	 */
	public EditModel prepareViewModelForNewRecruitment(int assignmentId) throws Exception {
		EditModel viewModel = assignmentMaintenanceService.prepareViewModel(assignmentId, null);
		
		long countIsNewOutlet = quotationRecordDao.countIsNewOutletTableListForNewRecruitmentApproval(assignmentId);
		viewModel.setNewOutlet(countIsNewOutlet > 0);
		
		return viewModel;
	}
	
	public Long getAssignemntApprovalTableListCount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if ((detail.getAuthorityLevel() & 4) == 4){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		} else if ((detail.getAuthorityLevel() & 2) == 2) {
			suerpervisorIds.add(userId);
			suerpervisorIds.addAll(detail.getActedUsers());
			List<Integer> actedUserSubordinateIds = userDao.getSubordinatesByUserId(suerpervisorIds);
			suerpervisorIds = actedUserSubordinateIds;
		}
		
		return quotationRecordDao.countAssignmentApprovalTableList("", suerpervisorIds,
				null, null, null,
				null, null, null,
				null, null, null, null, null);
	}

	/**
	 * DataTable query
	 */
	public Long getRUACaseApprovalTableCount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if ((detail.getAuthorityLevel() & 4) == 4){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		} else if ((detail.getAuthorityLevel() & 2) == 2) {
			suerpervisorIds.add(userId);
			suerpervisorIds.addAll(detail.getActedUsers());
			List<Integer> actedUserSubordinateIds = userDao.getSubordinatesByUserId(suerpervisorIds);
			suerpervisorIds = actedUserSubordinateIds;
		}
		
		return assignmentDao.countRUACaseApprovalTableList("", suerpervisorIds,
				null, null, null,
				null);
	}
	
	public Long getNewRecruitmentApprovalTableCount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if ((detail.getAuthorityLevel() & 4) == 4){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		} else if ((detail.getAuthorityLevel() & 2) == 2) {
			suerpervisorIds.add(userId);
			suerpervisorIds.addAll(detail.getActedUsers());
			List<Integer> actedUserSubordinateIds = userDao.getSubordinatesByUserId(suerpervisorIds);
			suerpervisorIds = actedUserSubordinateIds;
		}
		
		return assignmentDao.countNewRecruitmentApprovalTableList("", suerpervisorIds,
				null);
	}
	
	public List<Integer> getQuotationRecordIdToBeApproveAll(String search, Integer outletId, String outletTypeId, Integer personInChargeId,
			Integer[] districtId, String unitCategory, Integer[] tpuId,
			Boolean isProductChange, Boolean isSPricePeculiar, Integer[] availability, String referenceMonth, Integer[] purposeId) throws Exception {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		Integer userId = detail.getUserId();
		List<Integer> suerpervisorIds = new ArrayList<Integer>();
		if (!((detail.getAuthorityLevel() & 2) == 2  || (detail.getAuthorityLevel() & 1) == 1)){
			suerpervisorIds.add(userId);		
			suerpervisorIds.addAll(detail.getActedUsers());
		}
		
		Date refMonth = null;
		if (!StringUtils.isEmpty(referenceMonth)){
			refMonth = commonService.getMonth(referenceMonth);
		}
		
		List<Integer> quotationRecordIds = quotationRecordDao.getQuotationRecordToBeApproveAllList(search,
				suerpervisorIds,
				outletId, outletTypeId, personInChargeId,
				districtId, unitCategory, tpuId,
				isProductChange, isSPricePeculiar, availability, refMonth, purposeId);
		
		return quotationRecordIds;
	}
	
	public AssignmentApprovalViewModel prepareAssignmentApprovalViewModel(DatatableRequestModel model){
		if (model == null || model.getSearch() == null){
			return null;
		}
		
		AssignmentApprovalViewModel viewModel = new AssignmentApprovalViewModel();
		
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId"))){
			Integer outletId = Integer.parseInt(model.getSearch().get("outletId"));
			Outlet outlet = outletDao.findById(outletId);
			KeyValueModel keyValue = new KeyValueModel();
			keyValue.setKey(outlet.getFirmCode() + " - " + outlet.getName());
			keyValue.setValue(String.valueOf(outlet.getOutletId()));
			viewModel.setOutletSelected(keyValue);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
			String outletTypeId = model.getSearch().get("outletTypeId");
			List<VwOutletTypeShortForm> outletTypes = vwOutletTypeShortFormDao.getByIds(new String[]{outletTypeId});
			VwOutletTypeShortForm outletType = outletTypes.get(0);
			KeyValueModel keyValue = new KeyValueModel();
			keyValue.setKey(outletType.getShortCode() + " - " + outletType.getChineseName());
			keyValue.setValue(outletType.getShortCode());
			viewModel.setOutletTypeSelected(keyValue);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("personInChargeId"))){
			Integer personInChargeId = Integer.parseInt(model.getSearch().get("personInChargeId"));
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
		
		if (StringUtils.isNotEmpty(model.getSearch().get("unitCategory"))){
			String unitCategory = model.getSearch().get("unitCategory");
			viewModel.setUnitCategory(unitCategory);
		}
		
		List<String> tpuId = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("tpuId"))){
			tpuId = Arrays.asList(model.getSearch().get("tpuId").split("\\s*,\\s*"));
		}
		if (tpuId.size()>0){
			viewModel.setTpuSelected(new ArrayList<KeyValueModel>());
			List<Integer> ids = new ArrayList<Integer>();
			for (String id : tpuId){
				ids.add(Integer.parseInt(id));
			}
			List<Tpu> tpus = tpuDao.getByIds(ids);
			for (Tpu t : tpus){
				KeyValueModel keyValue = new KeyValueModel();
				keyValue.setValue(String.valueOf(t.getId()));
				keyValue.setKey(t.getCode());
				viewModel.getTpuSelected().add(keyValue);
			}
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("isProductChange"))){
			viewModel.setIsProductChange(Boolean.parseBoolean(model.getSearch().get("isProductChange")));
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("isSPricePeculiar"))){
			viewModel.setIsSPricePeculiar(Boolean.parseBoolean(model.getSearch().get("isSPricePeculiar")));
		}
		
		List<String> availability = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("availability"))){
			availability = Arrays.asList(model.getSearch().get("availability").split("\\s*,\\s*"));
		}
		List<Boolean> data = new ArrayList<Boolean>();
		for (int i = 0 ;i <= 9; i++){
			if (availability.contains(String.valueOf(i))){
				data.add(true);
			} else {
				data.add(false);
			}
		}
		viewModel.setAvailability(data);
		
		String referenceMonth = model.getSearch().get("referenceMonth");
		viewModel.setReferenceMonth(referenceMonth);
		
		List<String> purposeId = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))){
			purposeId = Arrays.asList(model.getSearch().get("purposeId").split("\\s*,\\s*"));
		}
		if (purposeId.size()>0){
			viewModel.setPurposeSelected(new ArrayList<KeyValueModel>());
			List<Integer> ids = new ArrayList<Integer>();
			for (String id : purposeId){
				ids.add(Integer.parseInt(id));
			}
			List<Purpose> purposes = purposeDao.getByIds(ids);
			for (Purpose d : purposes){
				KeyValueModel keyValue = new KeyValueModel();
				keyValue.setValue(String.valueOf(d.getId()));
				keyValue.setKey(d.getCode());
				viewModel.getPurposeSelected().add(keyValue);
			}
		}
		
		String search = model.getSearch().get("value");
		viewModel.setSearch(search);
		viewModel.setOrderColumn(Integer.parseInt(model.getOrder().get(0).get("column")));
		viewModel.setOrderDir(model.getOrder().get(0).get("dir"));
		
		return viewModel;
	}

}
