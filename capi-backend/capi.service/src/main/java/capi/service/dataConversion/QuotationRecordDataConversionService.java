package capi.service.dataConversion;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AssignmentDao;
import capi.dal.BatchDao;
import capi.dal.DelinkTaskDao;
import capi.dal.ImputeQuotationDao;
import capi.dal.ImputeUnitDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.dal.IndoorVerificationHistoryDao;
import capi.dal.OutletDao;
import capi.dal.PurposeDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.UnitStatisticDao;
import capi.dal.UserDao;
import capi.dal.VwOutletTypeShortFormDao;
import capi.entity.Assignment;
import capi.entity.Batch;
import capi.entity.DelinkTask;
import capi.entity.District;
import capi.entity.ImputeQuotation;
import capi.entity.ImputeUnit;
import capi.entity.IndoorQuotationRecord;
import capi.entity.IndoorVerificationHistory;
import capi.entity.Outlet;
import capi.entity.Purpose;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.SurveyMonth;
import capi.entity.TourRecord;
import capi.entity.Unit;
import capi.entity.UnitStatistic;
import capi.entity.User;
import capi.entity.VwOutletTypeShortForm;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.assignmentManagement.QuotationRecordHistoryDateModel;
import capi.model.assignmentManagement.assignmentManagement.BackTrackDateModel;
import capi.model.commonLookup.QuotationRecordHistoryStatisticLast2Year;
import capi.model.commonLookup.QuotationRecordHistoryStatisticModel;
import capi.model.commonLookup.QuotationRecordHistoryTableListModel;
import capi.model.dataConversion.quotationRecordDataConversion.AllocateQuotationRecordDataConversionFilterModel;
import capi.model.dataConversion.quotationRecordDataConversion.AllocateQuotationRecordDataConversionTableListModel;
import capi.model.dataConversion.quotationRecordDataConversion.PurposeIndoorQuotationRecordCountModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionSessionModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionTableListModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionViewModel;
import capi.model.quotationRecordVerificationApproval.QuotationRecordVerificationApprovalFilterModel;
import capi.model.quotationRecordVerificationApproval.QuotationRecordVerificationApprovalTableListModel;
import capi.model.report.QuotationUnitStatistic;
import capi.model.shared.quotationRecord.OutletViewModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.model.shared.quotationRecord.ProductPostModel;
import capi.model.shared.quotationRecord.QuotationRecordViewModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationRecordService;
import edu.emory.mathcs.backport.java.util.Collections;

@Service("QuotationRecordService")
public class QuotationRecordDataConversionService extends BaseService{
	
	@Autowired
	PurposeDao purposeDao;
	
	@Autowired
	IndoorQuotationRecordDao indoorQuotationRecordDao;

	@Autowired
	QuotationRecordDao quotationRecordDao;
	
	@Autowired
	QuotationDao quotationDao;
	
	@Autowired
	ImputeQuotationDao imputeQuotationDao;
	
	@Autowired
	ImputeUnitDao imputeUnitDao;
	
	@Autowired
	IndoorVerificationHistoryDao indoorVerificationHistoryDao;
	
	@Autowired
	QuotationRecordService quotationRecordService;
	
	@Autowired
	CommonService commonService;

	@Autowired
	private SurveyMonthService surveyMonthService;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private AssignmentMaintenanceService assignmentService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UnitStatisticDao unitStatisticDao;
	
	@Autowired
	private NotificationService notifyService;
	
	@Autowired
	private DelinkTaskDao delinkTaskDao;

	@Autowired
	private VwOutletTypeShortFormDao vwOutletTypeShortFormDao;
	
	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private BatchDao batchDao;
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	public List<Purpose> getPurposes(){
		return this.purposeDao.findAll();
	}
	
	public List<PurposeIndoorQuotationRecordCountModel> getPurposeIndoorQuotationRecordCounts(Date referenceMonth, String[] status, String notEqStatus, Integer userId){
		
		List<PurposeIndoorQuotationRecordCountModel> list = this.purposeDao.countIndoorQuotationRecords(referenceMonth, status, notEqStatus, userId);
		Collections.sort(list);
		return list;
	}

	public List<PurposeIndoorQuotationRecordCountModel> getPurposeIndoorQuotationRecordCountsByBatches(Date referenceMonth, String[] status, String notEqStatus, Integer userId){
		
		// get user batches
		List<Integer> batchIds = new ArrayList<Integer>();
		User loginUser = userDao.findById(userId);
		for (Batch b : loginUser.getBatches()) {
			batchIds.add(b.getBatchId());
		}
		
		List<PurposeIndoorQuotationRecordCountModel> list = this.purposeDao.countIndoorQuotationRecordsByBatches(referenceMonth, status, notEqStatus, batchIds);
		Collections.sort(list);
		return list;
	}
	
	public Purpose findPurpose(Integer id){
		return this.purposeDao.findById(id);
	}
	
	public Long countIndoorQuotation(Date referenceMonth, Integer purposeId, String[] status, String notEqStatus, Integer userId){
		return this.indoorQuotationRecordDao.countIndoorQuotationRecord(referenceMonth, purposeId, status, notEqStatus, userId);
	}
	
	public Long countIndoorQuotationOutlet(Date referenceMonth, Integer purposeId, String[] status, String notEqStatus, Integer userId ){
		return this.indoorQuotationRecordDao.countIndoorQuotationRecordOutlet(referenceMonth, purposeId, status, notEqStatus, userId);
	}
	
	public DatatableResponseModel<QuotationRecordVerificationApprovalTableListModel> queryIndoorQuotationRecordApprovalTableList(DatatableRequestModel model, QuotationRecordDataConversionSessionModel sessionModel){
		Order order = this.getOrder(model, "indoorQuotationRecordId", "firmName", "unitCode",
				"unitEnglishName", "unitChineseName",
				"subGroupEnglishName", "subGroupChineseName",
				"fieldOfficerName", "indoorOfficerName",
				"firmVerifyRemark", "categoryVerifyRemark", "quotationVerifyRemark"
				);
		QuotationRecordVerificationApprovalFilterModel filterModel = new QuotationRecordVerificationApprovalFilterModel();
		
		filterModel.setSearch(model.getSearch().get("value"));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId")))
			filterModel.setPurposeId(Integer.parseInt(model.getSearch().get("purposeId")));
		
		List<Integer> unitId = new ArrayList<Integer>();
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
		}
		filterModel.setUnitId(unitId);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			filterModel.setOutletId(Integer.parseInt(model.getSearch().get("outletId")));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("subgroupId")))
			filterModel.setSubgroupId(Integer.parseInt(model.getSearch().get("subgroupId")));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("indoorUserId")))
			filterModel.setIndoorUserId(Integer.parseInt(model.getSearch().get("indoorUserId")));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("referenceMonthStr"))){
			try {
				filterModel.setRefMonth(commonService.getMonth(model.getSearch().get("referenceMonthStr")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(StringUtils.isNotEmpty(model.getSearch().get("isVerify"))) {
			filterModel.setIsVerify(Integer.parseInt(model.getSearch().get("isVerify")));
		}
		
		List<QuotationRecordVerificationApprovalTableListModel> result = this.indoorQuotationRecordDao.queryQuotationRecordVerificationApprovalTableList(filterModel, model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<QuotationRecordVerificationApprovalTableListModel> response = new DatatableResponseModel<QuotationRecordVerificationApprovalTableListModel>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		
		QuotationRecordVerificationApprovalFilterModel emptyFilter = new QuotationRecordVerificationApprovalFilterModel();
		emptyFilter.setSearch("");
		
		Long recordTotal = this.indoorQuotationRecordDao.countQuotationRecordVerificationApprovalTableList(emptyFilter);
		response.setRecordsTotal(recordTotal.intValue());
//		Long recordFiltered = this.indoorQuotationRecordDao.countQuotationRecordVerificationApprovalTableList(filterModel);
//		response.setRecordsFiltered(recordFiltered.intValue());
		
		sessionModel.setIndoorQuotationRecordIds(this.indoorQuotationRecordDao.getQuotationRecordVerificationApprovalTableListIds(filterModel));
		response.setRecordsFiltered(sessionModel.getIndoorQuotationRecordIds().size());
		
		return response;
	}
	
	/** 
	 * Quotation datatable query 
	 */
	public DatatableResponseModel<QuotationRecordDataConversionTableListModel>
		queryIndoorQuotationRecordTableList(DatatableRequestModel model, QuotationRecordDataConversionSessionModel sessionModel, Integer userId, String ... status){
		Order order = this.getOrder(model, "", "referenceDate", "indoorQuotationRecordId", "quotationId", "isFlag", "subGroupEnglishName", "subGroupChineseName",
				"unitEnglishName", "unitChineseName", "editedCurrentSPrice", "editedPreviousSPrice", "pr",
				"outletType", "outletName", "productAttribute1",
				"subPrice", "seasonalItem", "indoorRemark", "outletDiscountRemark", 
				"priceRemarks", "otherRemarks", "discountRemark",  "unitCategory",
				"productRemarks");
		
		String search = model.getSearch().get("value");
		
		Integer purposeId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))) 
			purposeId = Integer.parseInt(model.getSearch().get("purposeId"));
		
		Integer subGroupId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("subGroupId"))) 
			subGroupId = Integer.parseInt(model.getSearch().get("subGroupId"));
		
		List<Integer> unitId = new ArrayList<Integer>();
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
		}
		
		Integer outletId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			outletId = Integer.parseInt(model.getSearch().get("outletId"));
		
		List<String> outletTypeShortCode = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
			outletTypeShortCode = Arrays.asList(model.getSearch().get("outletTypeId").split("\\s*,\\s*"));
		}
	
		Integer seasonalItem = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("seasonalItem")))
			seasonalItem = Integer.parseInt(model.getSearch().get("seasonalItem"));
		
		
		String surveyForm = model.getSearch().get("surveyForm");
		
		String outletCategory = model.getSearch().get("outletCategory");
		
		String referenceMonthStr = model.getSearch().get("referenceMonthStr");
		
		String referenceDateCrit = model.getSearch().get("referenceDateCrit");
		
		Boolean allocatedIndoorOfficer = null;
		Integer indoorOfficer = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("allocatedIndoorOfficer"))){
			allocatedIndoorOfficer = (model.getSearch().get("allocatedIndoorOfficer").trim().equals("0")) ? Boolean.FALSE : Boolean.TRUE;
			if (allocatedIndoorOfficer){
				indoorOfficer = Integer.parseInt(model.getSearch().get("allocatedIndoorOfficer"));
			}
		}
		
		Double greatedThan = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("greaterThan"))){			
			try{
				greatedThan = Double.parseDouble(model.getSearch().get("greaterThan"));
			}catch(NumberFormatException nfe){
				greatedThan = null;
			}
		}
		
		Double lessThan = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("lessThan"))){
			try{
				lessThan = Double.parseDouble(model.getSearch().get("lessThan"));
			}catch(NumberFormatException nfe){
				lessThan = null;
			}
		}
		
		Double equal = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("equal"))){
			try{
				equal = Double.parseDouble(model.getSearch().get("equal"));
			}catch(NumberFormatException nfe){
				equal = null;
			}
		}
		
		Boolean withPriceReason = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withPriceReason")))
			withPriceReason = (model.getSearch().get("withPriceReason").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withOtherRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withOtherRemark")))
			withOtherRemark = (model.getSearch().get("withOtherRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withProductRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withProductRemark")))
			withProductRemark = (model.getSearch().get("withProductRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withDiscountRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withDiscountRemark")))
			withDiscountRemark = (model.getSearch().get("withDiscountRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withDiscount = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withDiscount")))
			withDiscount = (model.getSearch().get("withDiscount").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Date referenceMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
		
		try {
			referenceMonth = this.commonService.getMonth(referenceMonthStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Boolean subPrice = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("subPrice"))) {
			subPrice = (model.getSearch().get("subPrice").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean priceRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("priceRemark"))) {
			priceRemark = (model.getSearch().get("priceRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean outletCategoryRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletCategoryRemark"))) {
			outletCategoryRemark = (model.getSearch().get("outletCategoryRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean withFieldwork = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withFieldwork"))) {
			withFieldwork = (model.getSearch().get("withFieldwork").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean isPRNull = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("isPRNull"))) {
			isPRNull = (model.getSearch().get("isPRNull").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean withIndoorConversionRemarks = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withIndoorConversionRemarks"))) {
			withIndoorConversionRemarks = (model.getSearch().get("withIndoorConversionRemarks").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		Integer firmStatus = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("firmStatus"))){
			firmStatus = Integer.parseInt(model.getSearch().get("firmStatus"));
		}
		Integer availability = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("availability"))){
			availability = Integer.parseInt(model.getSearch().get("availability"));
		}
		Integer quotationId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("quotationId"))){
			quotationId = Integer.parseInt(model.getSearch().get("quotationId"));
		}
		
		List<QuotationRecordDataConversionTableListModel> result = this.indoorQuotationRecordDao.getIndoorQuotationRecordTableList(search,
				userId, status, purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, 
				seasonalItem, outletCategory, subPrice, outletCategoryRemark, priceRemark,surveyForm, 
				allocatedIndoorOfficer, indoorOfficer, greatedThan, lessThan, equal,
				withPriceReason, withOtherRemark, withProductRemark, withDiscountRemark,
				withFieldwork, isPRNull, withIndoorConversionRemarks, referenceDateCrit, firmStatus,
				availability, withDiscount, quotationId,
				model.getStart(), model.getLength(), order);
	
		DatatableResponseModel<QuotationRecordDataConversionTableListModel> response = new DatatableResponseModel<QuotationRecordDataConversionTableListModel>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = this.indoorQuotationRecordDao.countIndoorQuotationRecordTableList(
				"", userId, status, purposeId, referenceMonth,
				null, null, null, null, null, null, 
				null, null, null, null, null, null, 
				null, null, null, null, null, null, 
				null, null, null, null, null, null, 
				null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = this.indoorQuotationRecordDao.countIndoorQuotationRecordTableList(
				search, userId, status, purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, 
				seasonalItem, outletCategory, subPrice, outletCategoryRemark, priceRemark, surveyForm,
				allocatedIndoorOfficer, indoorOfficer, greatedThan, lessThan, equal,
				withPriceReason, withOtherRemark, withProductRemark, withDiscountRemark, withFieldwork, isPRNull, 
				withIndoorConversionRemarks, referenceDateCrit, firmStatus, availability, withDiscount, quotationId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	public DatatableResponseModel<QuotationRecordDataConversionTableListModel>
		queryIndoorQuotationRecordDataConversionTableList(DatatableRequestModel model, QuotationRecordDataConversionSessionModel sessionModel, Integer userId, String ... status){
		Order order = this.getOrder(model, "", "referenceDate", "indoorQuotationRecordId", "quotationId", "isFlag", "subGroupEnglishName", "subGroupChineseName", "unitEnglishName", "unitChineseName",
				"outletType", "outletName", "productAttribute1",
				"editedCurrentSPrice", "editedPreviousSPrice", "pr", "subPrice", 
				"seasonalItem", "quotationRecordStatus", "priceRemarks", "productRemarks", "otherRemarks");
		
		
		String search = model.getSearch().get("value");
		
		Integer purposeId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))) 
			purposeId = Integer.parseInt(model.getSearch().get("purposeId"));
		
		Integer subGroupId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("subGroupId"))) 
			subGroupId = Integer.parseInt(model.getSearch().get("subGroupId"));
		
		List<Integer> unitId = new ArrayList<Integer>();
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
		}
		
		Integer outletId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			outletId = Integer.parseInt(model.getSearch().get("outletId"));
		
		List<String> outletTypeShortCode = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
			outletTypeShortCode = Arrays.asList(model.getSearch().get("outletTypeId").split("\\s*,\\s*"));
		}
	
		Integer seasonalItem = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("seasonalItem")))
			seasonalItem = Integer.parseInt(model.getSearch().get("seasonalItem"));
		
		
		String surveyForm = model.getSearch().get("surveyForm");
		
		String outletCategory = model.getSearch().get("outletCategory");
		
		String referenceMonthStr = model.getSearch().get("referenceMonthStr");
		
		Boolean allocatedIndoorOfficer = null;
		Integer indoorOfficer = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("allocatedIndoorOfficer"))){
			allocatedIndoorOfficer = (model.getSearch().get("allocatedIndoorOfficer").trim().equals("0")) ? Boolean.FALSE : Boolean.TRUE;
			if (allocatedIndoorOfficer){
				indoorOfficer = Integer.parseInt(model.getSearch().get("allocatedIndoorOfficer"));
			}
		}
		
		Double greatedThan = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("greaterThan"))){			
			try{
				greatedThan = Double.parseDouble(model.getSearch().get("greaterThan"));
			}catch(NumberFormatException nfe){
				greatedThan = null;
			}
		}
		
		Double lessThan = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("lessThan"))){
			try{
				lessThan = Double.parseDouble(model.getSearch().get("lessThan"));
			}catch(NumberFormatException nfe){
				lessThan = null;
			}
		}
		
		Double equal = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("equal"))){
			try{
				equal = Double.parseDouble(model.getSearch().get("equal"));
			}catch(NumberFormatException nfe){
				equal = null;
			}
		}
		
		Boolean withPriceReason = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withPriceReason")))
			withPriceReason = (model.getSearch().get("withPriceReason").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withOtherRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withOtherRemark")))
			withOtherRemark = (model.getSearch().get("withOtherRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withProductRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withProductRemark")))
			withProductRemark = (model.getSearch().get("withProductRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withDiscountRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withDiscountRemark")))
			withDiscountRemark = (model.getSearch().get("withDiscountRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Date referenceMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
		
		try {
			referenceMonth = this.commonService.getMonth(referenceMonthStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Boolean subPrice = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("subPrice"))) {
			subPrice = (model.getSearch().get("subPrice").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean priceRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("priceRemark"))) {
			priceRemark = (model.getSearch().get("priceRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean outletCategoryRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletCategoryRemark"))) {
			outletCategoryRemark = (model.getSearch().get("outletCategoryRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean withFieldwork = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withFieldwork")))
			withFieldwork = (model.getSearch().get("withFieldwork").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean isPRNull = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("isPRNull"))) {
			isPRNull = (model.getSearch().get("isPRNull").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Integer firmStatus = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("firmStatus"))){
			firmStatus = Integer.parseInt(model.getSearch().get("firmStatus"));
		}
		
		Integer availability = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("availability"))){
			availability = Integer.parseInt(model.getSearch().get("availability"));
		}
		
		Integer quotationId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("quotationId"))){
			quotationId = Integer.parseInt(model.getSearch().get("quotationId"));
		}
		
		List<QuotationRecordDataConversionTableListModel> result = this.indoorQuotationRecordDao.getIndoorQuotationRecordDataConversionTableList(search,
				userId, status, purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, 
				seasonalItem, outletCategory, subPrice, outletCategoryRemark, priceRemark,surveyForm, 
				allocatedIndoorOfficer, indoorOfficer, greatedThan, lessThan, equal,
				withPriceReason, withOtherRemark, withProductRemark, withDiscountRemark,
				withFieldwork, isPRNull, firmStatus, availability, quotationId,
				model.getStart(), model.getLength(), order);
	
		DatatableResponseModel<QuotationRecordDataConversionTableListModel> response = new DatatableResponseModel<QuotationRecordDataConversionTableListModel>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = this.indoorQuotationRecordDao.countIndoorQuotationRecordDataConversionTableList(
				"", userId, status, purposeId, referenceMonth, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null,
				null, null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = this.indoorQuotationRecordDao.countIndoorQuotationRecordDataConversionTableList(
				search, userId, status, purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, 
				seasonalItem, outletCategory, subPrice, outletCategoryRemark, priceRemark, surveyForm,
				allocatedIndoorOfficer, indoorOfficer, greatedThan, lessThan, equal,
				withPriceReason, withOtherRemark, withProductRemark, withDiscountRemark, withFieldwork, isPRNull, firmStatus, availability, quotationId);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
	}
	
	/** 
	 * Quotation datatable query 
	 */
	public DatatableResponseModel<AllocateQuotationRecordDataConversionTableListModel>
		queryAllocationIndoorQuotationRecordTableList(DatatableRequestModel model, QuotationRecordDataConversionSessionModel sessionModel, Integer userId){
		Order order = this.getOrder(model, "referenceDate", "indoorQuotationRecordId", "subGroupEnglishName", "subGroupChineseName",
				"unitEnglishName", "unitChineseName", "outletType", "outletName",
				"editedCurrentSPrice", "editedPreviousSPrice", "pr", "subPriceUsed",
				"priceRemarks", "productRemarks", "otherRemarks", "allocatedIndoorOfficer", "seasonalItem", "quotationRecordStatus"
				);
		
		AllocateQuotationRecordDataConversionFilterModel filterModel = new AllocateQuotationRecordDataConversionFilterModel();
		
		// get user batches
		List<Integer> batchId = new ArrayList<Integer>();
		User loginUser = userDao.findById(userId);
		/*for (Batch b : loginUser.getBatches()) {
			batchIds.add(b.getBatchId());
		}*/
		batchId = this.batchDao.getBatchByUserId(userId);
		System.out.println("User Id is : " + userId);
		System.out.println("The length of batchId is : " + batchId.size());
		
		filterModel.setSearch(model.getSearch().get("value"));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))) 
			filterModel.setPurposeId(Integer.parseInt(model.getSearch().get("purposeId")));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("batchCode"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("batchCode").split("\\s*,\\s*"));
			List<Integer> tempIds = new ArrayList<Integer>();
			for(String s : tempIdsString) tempIds.add(Integer.valueOf(s));
			filterModel.setBatchCode(tempIds);
		} else {
			filterModel.setBatchCode(batchId);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			List<Integer> tempIds = new ArrayList<Integer>();
			for(String s : tempIdsString) tempIds.add(Integer.valueOf(s));
			filterModel.setUnitIds(tempIds);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("indoorAllocationCode"))) 
			filterModel.setIndoorAllocationCode(model.getSearch().get("indoorAllocationCode"));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("surveyForm"))) 
			filterModel.setSurveyForm(model.getSearch().get("surveyForm"));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("groupId"))) 
			filterModel.setGroupId(Integer.parseInt(model.getSearch().get("groupId")));

		if (StringUtils.isNotEmpty(model.getSearch().get("subGroupId"))) 
			filterModel.setSubGroupId(Integer.parseInt(model.getSearch().get("subGroupId")));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("itemId"))) 
			filterModel.setItemId(Integer.parseInt(model.getSearch().get("itemId")));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
			filterModel.setOutletTypeId(Arrays.asList(model.getSearch().get("outletTypeId").split("\\s*,\\s*")));
		}
	
		if (StringUtils.isNotEmpty(model.getSearch().get("greaterThan"))){
			Double price = null;
			try{
				price = Double.parseDouble(model.getSearch().get("greaterThan"));
			}catch(NumberFormatException nfe){
				price = null;
			}
			filterModel.setGreaterThan(price);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("lessThan"))){
			Double price = null;
			try{
				price = Double.parseDouble(model.getSearch().get("lessThan"));
			}catch(NumberFormatException nfe){
				price = null;
			}
			filterModel.setLessThan(price);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("equal"))){
			Double price = null;
			try{
				price = Double.parseDouble(model.getSearch().get("equal"));
			}catch(NumberFormatException nfe){
				price = null;
			}
			filterModel.setEqual(price);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("ruaQuotationStatus")))
			filterModel.setRuaQuotationStatus((model.getSearch().get("ruaQuotationStatus").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("quotationRecordStatus")))
			filterModel.setQuotationRecordStatus((model.getSearch().get("quotationRecordStatus").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("appliedFR")))
			filterModel.setAppliedFR((model.getSearch().get("appliedFR").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		
		if (StringUtils.isNotEmpty(model.getSearch().get("seasonalItem"))) 
			filterModel.setSeasonalItem(Integer.parseInt(model.getSearch().get("seasonalItem")));
		
		if (StringUtils.isNotEmpty(model.getSearch().get("allocatedIndoorOfficer"))){
			filterModel.setAllocatedIndoorOfficer((model.getSearch().get("allocatedIndoorOfficer").trim().equals("0")) ? Boolean.FALSE : Boolean.TRUE);
			if (filterModel.getAllocatedIndoorOfficer()){
				filterModel.setIndoorOfficer(Integer.parseInt(model.getSearch().get("allocatedIndoorOfficer")));
			}
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("outlectCategoryRemark")))
			filterModel.setOutlectCategoryRemark((model.getSearch().get("outlectCategoryRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		
		if (StringUtils.isNotEmpty(model.getSearch().get("withPriceReason")))
			filterModel.setWithPriceReason((model.getSearch().get("withPriceReason").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("withPriceRemark")))
			filterModel.setWithPriceRemark((model.getSearch().get("withPriceRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);

		if (StringUtils.isNotEmpty(model.getSearch().get("consignmentCounter")))
			filterModel.setConsignmentCounter((model.getSearch().get("consignmentCounter").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("withOtherRemark")))
			filterModel.setWithOtherRemark((model.getSearch().get("withOtherRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("withProductRemark")))
			filterModel.setWithProductRemark((model.getSearch().get("withProductRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("withDiscountRemark")))
			filterModel.setWithDiscountRemark((model.getSearch().get("withDiscountRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("newProductCase")))
			filterModel.setNewProductCase((model.getSearch().get("newProductCase").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("changeProductCase")))
			filterModel.setChangeProductCase((model.getSearch().get("changeProductCase").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("newRecruitmentCase")))
			filterModel.setNewRecruitmentCase((model.getSearch().get("newRecruitmentCase").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("spicing")))
			filterModel.setSpicing((model.getSearch().get("spicing").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("fr")))
			filterModel.setFr((model.getSearch().get("fr").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("applicability")))
			filterModel.setApplicability((model.getSearch().get("applicability").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		
		if (StringUtils.isNotEmpty(model.getSearch().get("withDiscountPattern"))) {
			filterModel.setWithDiscountPattern((model.getSearch().get("withDiscountPattern").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("referenceDate"))){
			Date referenceDate = new Date();
			try{
				referenceDate = commonService.getDate(model.getSearch().get("referenceDate"));
				filterModel.setReferenceDate(referenceDate);
			} catch (ParseException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("availability"))){
			filterModel.setAvailability(Integer.parseInt(model.getSearch().get("availability")));
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("firmStatus"))){
			filterModel.setFirmStatus(Integer.parseInt(model.getSearch().get("firmStatus")));
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("pr"))){
			filterModel.setPr((model.getSearch().get("pr").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("quotationId"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("quotationId").split("\\s*,\\s*"));
			List<Integer> tempIds = new ArrayList<Integer>();
			for(String s : tempIdsString) tempIds.add(Integer.valueOf(s));
			filterModel.setQuotationIds(tempIds);
		}
		
		if (StringUtils.isNotEmpty(model.getSearch().get("remark"))){
			filterModel.setRemark((model.getSearch().get("remark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE);
		}
			
		String referenceMonthStr = model.getSearch().get("referenceMonthStr");
		
		Date referenceMonth = new Date();
		
		try {
			referenceMonth = this.commonService.getMonth(referenceMonthStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<AllocateQuotationRecordDataConversionTableListModel> result = this.indoorQuotationRecordDao.getAllocateIndoorQuotationRecordTableList(filterModel, referenceMonth, model.getStart(), model.getLength(), order);
	
		DatatableResponseModel<AllocateQuotationRecordDataConversionTableListModel> response = new DatatableResponseModel<AllocateQuotationRecordDataConversionTableListModel>();
		
		response.setDraw(model.getDraw());
		response.setData(result);
		
		AllocateQuotationRecordDataConversionFilterModel emptyFilter = new AllocateQuotationRecordDataConversionFilterModel();
		emptyFilter.setPurposeId(filterModel.getPurposeId());
		emptyFilter.setSearch("");
		emptyFilter.setBatchCode(batchId);

		Long recordTotal = this.indoorQuotationRecordDao.countAllocateIndoorQuotationRecordTableList(emptyFilter, referenceMonth, model.getStart(), model.getLength());
		response.setRecordsTotal(recordTotal.intValue());
		
		
//		Long recordFiltered = this.indoorQuotationRecordDao.countAllocateIndoorQuotationRecordTableList(filterModel, referenceMonth, model.getStart(), model.getLength());
//		response.setRecordsFiltered(recordFiltered.intValue());
		sessionModel.setIndoorQuotationRecordIds(this.indoorQuotationRecordDao.getAllocateIndoorQuotationRecordTableListIds(filterModel, referenceMonth));
		response.setRecordsFiltered(sessionModel.getIndoorQuotationRecordIds().size());
		System.out.println("The size is : " + sessionModel.getIndoorQuotationRecordIds().size());
		
		return response;
	}
	
	public IndoorQuotationRecord getIndoorQuotationRecord(Integer id){
		return this.indoorQuotationRecordDao.findById(id);
	}
	
	/**
	 * Prepare quotation record view model
	 */
	public PageViewModel prepareQuotationRecordViewModel(int id) {
		PageViewModel model = new PageViewModel();
		model.setReadonly(true);
		model.setHistory(false);
		QuotationRecord quotationRecord = quotationRecordDao.getByIdWithRelated(id);
		if (quotationRecord.getCollectionDate() == null) {
			quotationRecord.setCollectionDate(new Date());
		}
		if (quotationRecord.getReferenceDate() == null) {
			quotationRecord.setReferenceDate(new Date());
		}
		
		List<BackTrackDateModel> backTrackDates;
		if (quotationRecord.isBackTrack())
			backTrackDates = assignmentService.prepareBackTrackDatesSelect(quotationRecord.getOriginalQuotationRecord());
		else
			backTrackDates = assignmentService.prepareBackTrackDatesSelect(quotationRecord);
		
		model.setBackTracks(backTrackDates);

		List<QuotationRecordHistoryDateModel> histories = new ArrayList<QuotationRecordHistoryDateModel>();
		QuotationRecord historyRecordEntity = null;
		
		histories = quotationRecordService.getHistoryDatesAndRecordId(quotationRecord);
		if (histories.size() > 0) {
			historyRecordEntity = quotationRecordDao.getByIdWithRelated(histories.get(0).getId());
		}
		
		QuotationRecordViewModel quotationRecordViewModel = quotationRecordService.prepareQuotationRecordViewModel(quotationRecord, historyRecordEntity, false);
		model.setQuotationRecord(quotationRecordViewModel);

		QuotationRecord backNoQuotationRecord = quotationRecordDao.getBackNoRecord(id);
		if (backNoQuotationRecord == null) {
			backNoQuotationRecord = new QuotationRecord();
			quotationRecordService.initBackNoByOriginal(quotationRecord, backNoQuotationRecord);
		}
		QuotationRecordViewModel backNoQuotationRecordViewModel = quotationRecordService.prepareQuotationRecordViewModel(backNoQuotationRecord, historyRecordEntity, false);
		model.setBackNoQuotationRecord(backNoQuotationRecordViewModel);
		
		ProductPostModel productViewModel = quotationRecordService.prepareProductViewModel(quotationRecord);
		model.setProduct(productViewModel);
		
		if (quotationRecord.getOutlet() != null) {
			OutletViewModel outletViewModel = quotationRecordService.prepareOutletViewModel(quotationRecord.getOutlet());
			String code = quotationRecord.getQuotation().getUnit().getSubItem().getOutletType().getCode();
			code = code.substring(code.length() - 3);
			outletViewModel.setQuotationRecordOutletType(code);
			model.setOutlet(outletViewModel);
		}
		
		model.setHistories(histories);
		
		model.setPointToNote(quotationRecordService.concatPointToNotes(quotationRecord));
		model.setVerificationRemark(quotationRecord.getVerificationRemark());
		model.setPeCheckRemark(quotationRecord.getPeCheckRemark());
		
		return model;
	}
	
	/**
	 * Prepare quotation record view model
	 */
	public PageViewModel prepareQuotationRecordViewModelByQuotaionId(int id) {
		PageViewModel model = new PageViewModel();
		model.setReadonly(true);
		model.setHistory(false);
		
		Quotation quotation = this.quotationDao.findById(id);
		
		List<QuotationRecordHistoryDateModel> histories = new ArrayList<QuotationRecordHistoryDateModel>();
		QuotationRecord historyRecordEntity = null;
		
		histories = quotationRecordService.getHistoryDatesAndRecordId(quotation, new Date());
		if (histories.size() > 0) {
			historyRecordEntity = quotationRecordDao.getByIdWithRelated(histories.get(0).getId());
		}
		
		QuotationRecordViewModel quotationRecordViewModel = new QuotationRecordViewModel();
		quotationRecordViewModel.setFormDisplay(quotation.getUnit().getFormDisplay());
		model.setQuotationRecord(quotationRecordViewModel);

		QuotationRecordViewModel backNoQuotationRecordViewModel = new QuotationRecordViewModel();
		backNoQuotationRecordViewModel.setBackNo(true);
		model.setBackNoQuotationRecord(backNoQuotationRecordViewModel);
		
		ProductPostModel productViewModel = quotationRecordService.prepareProductViewModelByQuotation(quotation);
		model.setProduct(productViewModel);
		
		if (quotation.getOutlet() != null) {
			OutletViewModel outletViewModel = quotationRecordService.prepareOutletViewModel(quotation.getOutlet());
			String code = quotation.getUnit().getSubItem().getOutletType().getCode();
			code = code.substring(code.length() - 3);
			outletViewModel.setQuotationRecordOutletType(code);
			model.setOutlet(outletViewModel);
		}
		
		model.setHistories(histories);
		
		model.setPointToNote("");
		model.setVerificationRemark("");
		model.setPeCheckRemark("");
		
		return model;
	}
	
	@Transactional
	public void updateRecord(IndoorQuotationRecord entity, Quotation quotation){
		this.indoorQuotationRecordDao.save(entity);
		this.quotationDao.save(quotation);
		
		this.indoorQuotationRecordDao.flush();
	}
	
	public ImputeQuotation getImputeQuotation(Integer quotationId, Date refMonth){
		return this.imputeQuotationDao.getImputeQuotation(quotationId, refMonth);
	}
	
	public ImputeQuotation getPreviousImputeQuotation(Integer quotationId, Date refMonth){
		return this.imputeQuotationDao.getPreviousImputeQuotation(quotationId, refMonth);
	}
	
	@Transactional
	public void saveImputeQuotation(ImputeQuotation entity){
		this.imputeQuotationDao.save(entity);
	}
	
	@Transactional
	public ImputeUnit getImputeUnit(Integer unitId, Date refMonth){
		return this.imputeUnitDao.getImputeUnit(unitId, refMonth);
	}
	
	public ImputeUnit getPreviousImputeUnit(Integer unitId, Date refMonth){
		return this.imputeUnitDao.getPreviousImputeUnit(unitId, refMonth);
	}
	
	@Transactional
	public void saveImputeUnit(ImputeUnit entity){
		this.imputeUnitDao.save(entity);
	}
	

//	public DatatableResponseModel<QuotationRecordDataConversionTableListModel>
//	queryAllocateIndoorQuotationRecordTableList(DatatableRequestModel model, QuotationRecordDataConversionSessionModel sessionModel, Integer userId, String status){
//		Order order = this.getOrder(model, "subGroupChineseName", "subGroupEnglishName",
//				"unitChineseName", "UnitEnglishName", "outletType", "outletName",
//				"subPrice", "seasonalItem", "quotationRecordStatus", "priceRemarks", "productRemarks", "otherRemarks", "allocatedIndoorOfficer"
//				);		
//		
//		
//		String search = model.getSearch().get("value");
//		
//		Integer purposeId = null;
//		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))) 
//			purposeId = Integer.parseInt(model.getSearch().get("purposeId"));
//		
//		Integer subGroupId = null;
//		if (StringUtils.isNotEmpty(model.getSearch().get("subGroupId"))) 
//			subGroupId = Integer.parseInt(model.getSearch().get("subGroupId"));
//		
////		Integer unitId = null;
////		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))) 
////			unitId = Integer.parseInt(model.getSearch().get("unitId"));
//		List<Integer> unitId = new ArrayList<Integer>();
//		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
//			List<String> tempIdsString =Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
//			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
//		}
//		
//		Integer outletId = null;
//		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
//			outletId = Integer.parseInt(model.getSearch().get("outletId"));
//		
//		List<String> outletTypeShortCode = new ArrayList<String>();
//		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
//			outletTypeShortCode = Arrays.asList(model.getSearch().get("outletTypeId").split("\\s*,\\s*"));
//		}
//	
//		Integer seasonalItem = null;
//		if (StringUtils.isNotEmpty(model.getSearch().get("seasonalItem")))
//			seasonalItem = Integer.parseInt(model.getSearch().get("seasonalItem"));
//		
//		String outletCategory = model.getSearch().get("outletCategory");
//		
//		String referenceMonthStr = model.getSearch().get("referenceMonthStr");
//		
//		Date referenceMonth = new Date();
//		
//		try {
//			referenceMonth = this.commonService.getDate("01-"+referenceMonthStr);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Boolean subPrice = null;
//		if (StringUtils.isNotEmpty(model.getSearch().get("subPrice"))) {
//			subPrice = (model.getSearch().get("subPrice").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
//		}
//		
//		Boolean priceRemark = null;
//		if (StringUtils.isNotEmpty(model.getSearch().get("priceRemark"))) {
//			priceRemark = (model.getSearch().get("priceRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
//		}
//		
//		Boolean outletCategoryRemark = null;
//		if (StringUtils.isNotEmpty(model.getSearch().get("outletCategoryRemark"))) {
//			outletCategoryRemark = (model.getSearch().get("outletCategoryRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
//		}
//		
//		List<QuotationRecordDataConversionTableListModel> result = this.indoorQuotationRecordDao.getIndoorQuotationRecordTableList(search, userId, status,
//				purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, seasonalItem, outletCategory, subPrice,
//				outletCategoryRemark, priceRemark, "", model.getStart(), model.getLength(), order);
//	
//		DatatableResponseModel<QuotationRecordDataConversionTableListModel> response = new DatatableResponseModel<QuotationRecordDataConversionTableListModel>();
//		
//		response.setDraw(model.getDraw());
//		response.setData(result);
//		Long recordTotal = this.indoorQuotationRecordDao.countIndoorQuotationRecordTableList("", userId, status, purposeId, referenceMonth, null, null, null, null, null, null, null, null, null, null);
//		response.setRecordsTotal(recordTotal.intValue());
//		Long recordFiltered = this.indoorQuotationRecordDao.countIndoorQuotationRecordTableList(search, userId, status, purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, seasonalItem, outletCategory, subPrice, outletCategoryRemark, priceRemark, "");
//		response.setRecordsFiltered(recordFiltered.intValue());
//		
//		sessionModel.setIndoorQuotationRecordIds(this.indoorQuotationRecordDao.getIndoorQuotationRecordTableIds(search, userId, status, purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, seasonalItem, outletCategory, subPrice, outletCategoryRemark, priceRemark, ""));
//		
//		
//		return response;
//	}

	@Transactional
	public boolean toggleFlag(List<Integer> id, Boolean flag){
		List<IndoorQuotationRecord> indoorQuotationRecords = this.indoorQuotationRecordDao.getIndoorQuotationRecordsByIds(id);
		if (id.size() != indoorQuotationRecords.size()){
			return false;
		}
		
		for (IndoorQuotationRecord indoorQuotationRecord : indoorQuotationRecords){
			indoorQuotationRecord.setFlag(flag!=null&&flag);
		}

		this.indoorQuotationRecordDao.flush();

		return true;
	}
	
	@Transactional
	public IndoorQuotationRecord saveRevisit(Integer indoorAllocationRecordId, Date startDate, Date endDate, Date collectionDate ){
		try{
			IndoorQuotationRecord entity = this.getIndoorQuotationRecord(indoorAllocationRecordId);
			entity.setStatus("Revisit");
			QuotationRecord qr = entity.getQuotationRecord();
			if(qr == null){
				qr = new QuotationRecord();
				qr.setQuotation(entity.getQuotation());
				qr.setIndoorQuotationRecord(entity);
				qr.setOutlet(entity.getQuotation().getOutlet());
				qr.setProduct(entity.getQuotation().getProduct());
				qr.setStatus("Blank");
				qr.setUser(null);
				qr.setQuotationState("Revisit");
				if (collectionDate != null){
					qr.setReferenceDate(collectionDate);
				} else {
					qr.setReferenceDate(startDate);
				}
				qr.setCollectionDate(collectionDate);
				qr.setAssignedCollectionDate(collectionDate);
				qr.setAssignedStartDate(startDate);
				qr.setAssignedEndDate(endDate);
				qr.setFormDisplay(entity.getQuotation().getUnit().getFormDisplay());
				if (qr.getQuotation().getUnit().isFrRequired() && !qr.getQuotation().isFRApplied()){
					qr.setCollectFR(true);
				} else {
					qr.setCollectFR(false);
				}
				
				
				Assignment a = new Assignment();
				SurveyMonth sm = this.surveyMonthService.getSurveyMonthByReferenceMonth(entity.getReferenceMonth());
				a.setSurveyMonth(sm);
				Set<QuotationRecord> qrSet = new HashSet<QuotationRecord>();
				qr.setAssignment(a);
				qrSet.add(qr);
				a.setQuotationRecords(qrSet);
				entity.setQuotationRecord(qr);
				a.setStartDate(startDate);
				a.setEndDate(endDate);
				a.setCollectionDate(collectionDate);
				if (collectionDate == null){
					a.setAssignedCollectionDate(startDate);
				} else {
					a.setAssignedCollectionDate(collectionDate);
				}
				a.setStatus(1);
				
				Outlet outlet = entity.getQuotation().getOutlet();
				District district = outlet.getTpu().getDistrict();
				
				String referenceNo = district.getCode()+outlet.getFirmCode();
				a.setReferenceNo(referenceNo);
				
				a.setCollectionMethod(outlet.getCollectionMethod());
				
				a.setOutlet(entity.getQuotation().getOutlet());
	
				this.assignmentDao.save(a);
			}else{
				qr.setNewRecruitment(false);
				if (qr.getOtherQuotationRecords()!=null){
					for (QuotationRecord backno : qr.getOtherQuotationRecords()){
						if (backno.isBackNo()){
							backno.setNewRecruitment(false);
						}
					}
				}
				qr.setStatus("Blank");
				qr.setUser(null);
				qr.setQuotationState("Revisit");
				if (collectionDate != null){
					qr.setReferenceDate(collectionDate);
				} else {
					qr.setReferenceDate(startDate);
				}
				qr.setCollectionDate(collectionDate);
				qr.setAssignedCollectionDate(collectionDate);
				qr.setAssignedStartDate(startDate);
				qr.setAssignedEndDate(endDate);
				qr.setVisited(false);
			}
			this.quotationRecordDao.save(qr);
			this.indoorQuotationRecordDao.save(entity);
			this.indoorQuotationRecordDao.flush();
			
			return entity;
		}catch(Exception e){
			return null;
		}
	}
	
	@Transactional
	public boolean saveAllocate(List<Integer> list){
		List<List<Integer>> splitList = commonService.splitListByMaxSize(list);
		
		List<IndoorQuotationRecord> indoorQuotationRecords = new ArrayList<IndoorQuotationRecord>();
		
		for (List<Integer> subList : splitList) {
			indoorQuotationRecords.addAll(indoorQuotationRecordDao.getEntityByIds(subList));
		}
		
		if (list.size() != indoorQuotationRecords.size()){
			return false;
		}
		
		for (IndoorQuotationRecord indoorQuotationRecord : indoorQuotationRecords){
			indoorQuotationRecord.setStatus("Conversion");
		}

		this.indoorQuotationRecordDao.flush();

		return true;
	}
	
	@Transactional
	public Boolean saveRevisitIds(List<Integer> list, Date startDate, Date endDate, Date collectionDate ){
		List<List<Integer>> splitList = commonService.splitListByMaxSize(list);
		
		List<IndoorQuotationRecord> indoorQuotationRecords = new ArrayList<IndoorQuotationRecord>();
		
		for (List<Integer> subList : splitList) {
			indoorQuotationRecords.addAll(indoorQuotationRecordDao.getEntityByIds(subList));
		}
		
		if (list.size() != indoorQuotationRecords.size()){
			return false;
		}
		
		for(IndoorQuotationRecord entity : indoorQuotationRecords){
			if (entity.isRUA() || "RUA".equals(entity.getQuotation().getStatus())){
				continue;
			}
			
			entity.setStatus("Revisit");
			QuotationRecord qr = entity.getQuotationRecord();
			if(qr == null){
				qr = new QuotationRecord();
				qr.setQuotation(entity.getQuotation());
				qr.setIndoorQuotationRecord(entity);
				qr.setOutlet(entity.getQuotation().getOutlet());
				qr.setProduct(entity.getQuotation().getProduct());
				qr.setStatus("Blank");
				qr.setUser(null);
				qr.setQuotationState("Revisit");
				if (collectionDate != null){
					qr.setReferenceDate(collectionDate);
				} else {
					qr.setReferenceDate(startDate);
				}
				qr.setCollectionDate(collectionDate);
				qr.setAssignedCollectionDate(collectionDate);
				qr.setAssignedStartDate(startDate);
				qr.setAssignedEndDate(endDate);
				qr.setSPricePeculiar(false);
				qr.setFRPercentage(false);
				qr.setConsignmentCounter(false);
				qr.setBackNo(false);
				qr.setBackTrack(false);
				qr.setFormDisplay(1);
				qr.setProductChange(false);
				qr.setNewProduct(false);
				qr.setFlag(false);
				qr.setNewOutlet(false);
				qr.setVersion(0);
				qr.setVisited(false);
				qr.setPassValidation(false);
				qr.setSpecifiedUser(true);
				qr.setReleased(false);
				qr.setVerifyFirm(false);
				qr.setVerifyCategory(false);
				qr.setVerifyQuotation(false);
				
				if (qr.getQuotation().getUnit().isFrRequired() && !qr.getQuotation().isFRApplied()){
					qr.setCollectFR(true);
				} else {
					qr.setCollectFR(false);
				}
				
				Assignment a = new Assignment();
				SurveyMonth sm = this.surveyMonthService.getSurveyMonthByReferenceMonth(entity.getReferenceMonth());
				a.setSurveyMonth(sm);
				Set<QuotationRecord> qrSet = new HashSet<QuotationRecord>();
				qr.setAssignment(a);
				qrSet.add(qr);
				a.setQuotationRecords(qrSet);
				entity.setQuotationRecord(qr);
				a.setStartDate(startDate);
				a.setEndDate(endDate);
				a.setCollectionDate(collectionDate);
				
				if (collectionDate == null){
					a.setAssignedCollectionDate(startDate);
				} else {
					a.setAssignedCollectionDate(collectionDate);
				}
				a.setStatus(1);
				
				Outlet outlet = entity.getQuotation().getOutlet();
				District district = outlet.getTpu().getDistrict();
				
				String referenceNo = district.getCode()+outlet.getFirmCode();
				a.setReferenceNo(referenceNo);
				
				a.setCollectionMethod(outlet.getCollectionMethod());
				
				a.setOutlet(entity.getQuotation().getOutlet());
				a.setByPassLog(true);
				this.assignmentDao.save(a);
			}else{
				qr.setNewRecruitment(false);
				if (qr.getOtherQuotationRecords()!=null){
					for (QuotationRecord backno : qr.getOtherQuotationRecords()){
						if (backno.isBackNo()){
							backno.setNewRecruitment(false);
						}
					}
				}
				qr.setStatus("Blank");
				qr.setUser(null);
				qr.setQuotationState("Revisit");
				if (collectionDate != null){
					qr.setReferenceDate(collectionDate);
				} else {
					qr.setReferenceDate(startDate);
				}
				qr.setCollectionDate(collectionDate);
				qr.setAssignedCollectionDate(collectionDate);
				qr.setAssignedStartDate(startDate);
				qr.setAssignedEndDate(endDate);
				qr.setVisited(false);
			}
			qr.setByPassLog(true);
			this.quotationRecordDao.save(qr);
			entity.setByPassLog(true);
			this.indoorQuotationRecordDao.save(entity);
		}
		this.indoorQuotationRecordDao.flush();
		
		return true;
		
	}
	
	@Transactional
	public Boolean delink(Date refMonth){
//		List<QuotationRecord> qrs = this.quotationRecordService.getQuotationRecordsSurveyMonth(refMonth);
//		List<Assignment> as = new ArrayList<Assignment>();
//		for(QuotationRecord record : qrs){
//			as.add(record.getAssignment());
//			this.quotationRecordService.deleteQuotationRecordFromDatabase(record);
//		}
//		for(Assignment assignment : as){
//			this.assignmentService.deleteAssignment(assignment);
//		}
//		this.assignmentDao.flush();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		User user = userDao.findById(detail.getUserId());
		DelinkTask task = new DelinkTask();
		task.setReferenceMonth(refMonth);
		task.setUser(user);
		delinkTaskDao.save(task);
		
		
		return true;
	}
	
	public Double calTourRecordAveragePrice(TourRecord tr){
		 //tour case;
		 Double sum = (double) 0;
		 Integer count = 0;
		 Class<? extends TourRecord> c = tr.getClass();
		 for(int i = 1; i <= 31; i++){
			 try{
				 Field f = c.getDeclaredField("day"+i+"Price");
				 f.setAccessible(true);
				 sum = sum + (Double)f.get(tr);
				 count++;
			 }catch(Exception e){
				 System.out.println(e.getMessage());
			 }
		 }
		 if(count > 0){
			 return (double) (sum / count);
		 }else{
			 return (double) 0;
		 }
	}
	
	public String updateRecordUser(Integer indoorQuotationRecordId, Integer userId){
		
		
		IndoorQuotationRecord entity = this.getIndoorQuotationRecord(indoorQuotationRecordId);
		entity.setStatus("Conversion");
		
		Unit unit = entity.getQuotation().getUnit();
		
		capi.entity.User u = this.userDao.findById(userId);
		
		entity.setUser(u);
		
		this.indoorQuotationRecordDao.save(entity);
		this.indoorQuotationRecordDao.flush();
		
		String rediectParam = "purposeId="+unit.getPurpose().getId()+"&referenceMonthStr="+commonService.formatMonth(entity.getReferenceMonth());
		return rediectParam;
	}
	
	public void updateRecordUser(List<Integer> indoorQuotationRecordIds, Integer userId){
		capi.entity.User u = this.userDao.findById(userId);
		
		List<List<Integer>> splitList = commonService.splitListByMaxSize(indoorQuotationRecordIds);
		
		List<IndoorQuotationRecord> entities = new ArrayList<IndoorQuotationRecord>();
		
		for (List<Integer> subList : splitList) {
			entities.addAll(indoorQuotationRecordDao.getEntityByIds(subList));
		}
		
		for (IndoorQuotationRecord entity : entities) {
			entity.setStatus("Conversion");
			entity.setUser(u);
			this.indoorQuotationRecordDao.save(entity);
		}
		
		this.indoorQuotationRecordDao.flush();
	}

	public void markComplete(List<Integer> indoorQuotationRecordIds){
		
		List<List<Integer>> splitList = commonService.splitListByMaxSize(indoorQuotationRecordIds);
		
		List<IndoorQuotationRecord> entities = new ArrayList<IndoorQuotationRecord>();
		
		for (List<Integer> subList : splitList) {
			entities.addAll(indoorQuotationRecordDao.getEntityByIds(subList));
		}

		for (IndoorQuotationRecord entity : entities) {
			entity.setStatus("Complete");
			this.indoorQuotationRecordDao.save(entity);
		}
		
		this.indoorQuotationRecordDao.flush();
	}
	
	public void indoorQuotationRecordApproval(Integer indoorQuotationRecordId, Boolean approval, String reason ){
		IndoorQuotationRecord entity = this.getIndoorQuotationRecord(indoorQuotationRecordId);
		List<User> userList = new ArrayList<User>();
		if(approval){
			entity.setStatus("Approve Verification");
			
			String verification = "";
			if(entity.isFirmVerify()){
				verification = "isFirmVerify";
			}
			if(entity.isCategoryVerify()){
				verification = "isCategoryVerify";
			}
			if(entity.isQuotationVerify()){
				verification = "isQuotationVerify";
			}
			
			String remark = "";
			List<QuotationRecord> qrList = new ArrayList<QuotationRecord>();
			List<QuotationRecord> removeList = new ArrayList<QuotationRecord>();
			if(verification.equalsIgnoreCase("isFirmVerify")){
				if (entity.getQuotationRecord() != null) {
					QuotationRecord qr= entity.getQuotationRecord();
					Assignment a = qr.getAssignment();
					if(a != null){
						qrList = new ArrayList<QuotationRecord>(a.getQuotationRecords());
					}else{
						qrList = new ArrayList<QuotationRecord>();
					}
					for(QuotationRecord remove : qrList){
						if ((remove.getIndoorQuotationRecord()!=null 
								&& remove.getIndoorQuotationRecord().isRUA()) || remove.isBackNo()){
							removeList.add(remove);
						}
					}
				}
				remark = entity.getFirmRemark();
			}else if(verification.equalsIgnoreCase("isCategoryVerify")){
				if (entity.getQuotationRecord() != null && entity.getQuotation() != null) {
					List<QuotationRecord> tempQrList = new ArrayList<QuotationRecord>(entity.getQuotationRecord().getAssignment().getQuotationRecords());
					for(QuotationRecord collection : tempQrList){
						if(collection.getQuotation().getUnit().getUnitCategory().equalsIgnoreCase(entity.getQuotation().getUnit().getUnitCategory())){
							qrList.add(collection);
						}
					}
					for(QuotationRecord remove : qrList){
						if ((remove.getIndoorQuotationRecord()!=null 
								&& remove.getIndoorQuotationRecord().isRUA()) || remove.isBackNo()){
							removeList.add(remove);
						}
					}
				}
				remark = entity.getCategoryRemark();
			}else if(verification.equalsIgnoreCase("isQuotationVerify")){
				if(entity.getQuotationRecord() != null && !entity.isRUA()){
					qrList.add(entity.getQuotationRecord());
				}
				remark = entity.getQuotationRemark();
			}
			qrList.removeAll(removeList);
			for(QuotationRecord quotationRecord : qrList){
				quotationRecord.setStatus("Draft");
				quotationRecord.setQuotationState("Verify");
				quotationRecord.setVisited(false);
				quotationRecord.setNewRecruitment(false);
				if (quotationRecord.getOtherQuotationRecords()!=null){
					for (QuotationRecord backno : quotationRecord.getOtherQuotationRecords()){
						if (backno.isBackNo()){
							backno.setNewRecruitment(false);
						}
					}
				}
				//quotationRecord.setRemark(quotationRecord.getRemark()+"\n"+remark);

				quotationRecord.setVerifyFirm(false);
				quotationRecord.setVerifyCategory(false);
				quotationRecord.setVerifyQuotation(false);
				if (verification.equalsIgnoreCase("isFirmVerify")) {
					quotationRecord.setVerifyFirm(true);
				} else if(verification.equalsIgnoreCase("isCategoryVerify")) {
					quotationRecord.setVerifyCategory(true);
				} else if(verification.equalsIgnoreCase("isQuotationVerify")) {
					quotationRecord.setVerifyQuotation(true);
				}
				quotationRecord.setVerificationRemark((quotationRecord.getVerificationRemark() != null ? quotationRecord.getVerificationRemark() + "\n" : "") + remark);
				
				if (quotationRecord.getIndoorQuotationRecord() != null){
					quotationRecord.getIndoorQuotationRecord().setStatus("Approve Verification");
					quotationRecord.getIndoorQuotationRecord().setFirmVerify(entity.isFirmVerify());
					quotationRecord.getIndoorQuotationRecord().setCategoryVerify(entity.isCategoryVerify());
					quotationRecord.getIndoorQuotationRecord().setQuotationVerify(entity.isQuotationVerify());
					
					quotationRecord.getIndoorQuotationRecord().setFirmRemark(entity.getFirmRemark());
					quotationRecord.getIndoorQuotationRecord().setCategoryRemark(entity.getCategoryRemark());
					quotationRecord.getIndoorQuotationRecord().setQuotationRemark(entity.getQuotationRemark());
				}
				
				this.quotationRecordService.saveRecord(quotationRecord);
			}
			
			createVerificationHistory(verification, entity.getReferenceMonth(), entity.getQuotationRecord().getAssignment().getUser(), qrList);
			
//			User user = entity.getUser();
//			userList.add(user);
//			QuotationRecord qr = entity.getQuotationRecord();
//			if(qr != null){
//				if(qr.getUser() != null){
//					user = qr.getUser();
//					userList.add(user);
//				
//					User supervisor = user.getSupervisor();
//					userList.add(supervisor);
//				}
//			}
//			
//			String subject = messageSource.getMessage("N00045", null, Locale.ENGLISH);
//			String content = messageSource.getMessage("N00050", null, Locale.ENGLISH);
//			for(User u : userList){
//				notifyService.sendNotification(u, subject, content, false);
//			}
			
		}else{
			entity.setStatus("Reject Verification");
			entity.setRejectReason(reason);
			
			User user = entity.getUser();
			userList.add(user);
			
			entity.setFirmVerify(false);
			entity.setCategoryVerify(false);
			entity.setQuotationVerify(false);
			entity.setFirmRemark(null);
			entity.setCategoryRemark(null);
			entity.setQuotationRemark(null);
			
			String subject = messageSource.getMessage("N00045", null, Locale.ENGLISH);
			String content = messageSource.getMessage("N00051", null, Locale.ENGLISH);
			for(User u : userList){
				notifyService.sendNotification(u, subject, content, false);
			}
		}
		this.indoorQuotationRecordDao.save(entity);
		this.indoorQuotationRecordDao.flush();
	}
	
	public DatatableResponseModel<QuotationRecordHistoryTableListModel> queryHistoryDialogList(DatatableRequestModel model, Integer indoorQuotationRecordId){
		
		Order order = this.getOrder(model, "", "submissionDate", "referenceMonth", "collectedNPrice",
				"collectedSPrice", "subPriceId", "discount", "availability", "fr", "previousNPrice",
				"previousSPrice", "currentNPrice", "currentSPrice", "isFlag", "qs.maxSPrice", "qs.minSPrice", "qs.averageCurrentSPrice"
				);
		
		Quotation q = this.indoorQuotationRecordDao.findById(indoorQuotationRecordId).getQuotation();
		List<QuotationRecordHistoryTableListModel> histories = new ArrayList<QuotationRecordHistoryTableListModel>();
		
		histories = this.quotationRecordDao.getHistoryDataList(model.getSearch().get("value"), q.getQuotationId(), model.getStart(), model.getLength(), order);
		
		DatatableResponseModel<QuotationRecordHistoryTableListModel> response = new DatatableResponseModel<QuotationRecordHistoryTableListModel>();
		for(QuotationRecordHistoryTableListModel displayModel : histories ){
			displayModel.setSubmissionDateStr(commonService.formatDate(displayModel.getSubmissionDate()));
			displayModel.setReferenceMonthStr(commonService.formatMonth(displayModel.getReferenceMonth()));
		}
		response.setData(histories);
		
		response.setRecordsTotal(this.quotationRecordDao.countHistoryDataList("", q.getQuotationId()).intValue());
		response.setRecordsFiltered(this.quotationRecordDao.countHistoryDataList(model.getSearch().get("value"), q.getQuotationId()).intValue());
		
		return response;
	}
	
	public QuotationRecordHistoryStatisticModel calculateQuotationRecordHistoryStat(Integer indoorQuotationRecordId){
		IndoorQuotationRecord record= indoorQuotationRecordDao.findById(indoorQuotationRecordId);
		QuotationRecordHistoryStatisticLast2Year twoYearStat = null;
		UnitStatistic current = null;
		UnitStatistic previous = null;
		Integer countQuotationRecord = null;
		Integer countPreviousQuotationRecord = null;
		
			twoYearStat = this.indoorQuotationRecordDao.getLastTwoYearStatistic(indoorQuotationRecordId, record.getReferenceMonth());

			Integer unitId = record.getQuotation().getUnit().getId();
			//current = this.indoorQuotationRecordDao.getUnitStatic(indoorQuotationRecordId, currentSurveyMonth.getReferenceMonth());
			current = this.unitStatisticDao.getByUnitAndReferenceMonth(unitId, record.getReferenceMonth());
			if (current == null){
				List<QuotationUnitStatistic> stat = unitStatisticDao.getQuotationUnitStatistic(record.getReferenceMonth(),unitId);
				if (stat != null && stat.size() > 0){
					current = new UnitStatistic();
					//BeanUtils.copyProperties(stat.get(0), current);
					current.setMedianSPrice(stat.get(0).getUnitMedianSPrice());
					current.setAveragePRSPrice(stat.get(0).getUnitAveragePRSPrice());
					current.setStandardDeviationPRSPrice(stat.get(0).getUnitStandardDeviationPRSPrice());
				}
			}
			
			Date month = record.getReferenceMonth();
			Date prevMonth = DateUtils.addMonths(month, -1);
//			previous = this.indoorQuotationRecordDao.getUnitStatic(indoorQuotationRecordId, prevMonth);
			previous = this.unitStatisticDao.getByUnitAndReferenceMonth(unitId, prevMonth);
			if (previous == null){
				List<QuotationUnitStatistic> stat = unitStatisticDao.getQuotationUnitStatistic(prevMonth,unitId);
				if (stat != null && stat.size() > 0){
					previous = new UnitStatistic();
					//BeanUtils.copyProperties(stat.get(0), previous);
					previous.setMedianSPrice(stat.get(0).getUnitMedianSPrice());
					previous.setAveragePRSPrice(stat.get(0).getUnitAveragePRSPrice());
					previous.setStandardDeviationPRSPrice(stat.get(0).getUnitStandardDeviationPRSPrice());
				}
			}
			//previous = this.indoorQuotationRecordDao.getPreviousUnitStatic(indoorQuotationRecordId, currentSurveyMonth.getReferenceMonth());
			
			countQuotationRecord = this.indoorQuotationRecordDao.countQuotationRecords(indoorQuotationRecordId, record.getReferenceMonth());
			if(previous != null){
				countPreviousQuotationRecord = this.indoorQuotationRecordDao.countQuotationRecords(indoorQuotationRecordId, previous.getReferenceMonth());
			}
			
		
		QuotationRecordHistoryStatisticModel result = new QuotationRecordHistoryStatisticModel();
		
		result.setCurrentStatistic(current);
		result.setPreviousStatistic(previous);
		result.setLast2Year(twoYearStat);
		result.setCurrentCount(countQuotationRecord);
		result.setPreviousCount(countPreviousQuotationRecord);
		
		return result;
	}
	
	public void createVerificationHistory(String verification, Date referenceMonth, User assignmentUser, List<QuotationRecord> quotationRecordList) {
		IndoorVerificationHistory entity = new IndoorVerificationHistory();
		entity.setReferenceMonth(referenceMonth);
		entity.setUser(assignmentUser);
		
		if (verification.equalsIgnoreCase("isFirmVerify")) {
			entity.setVerifyType(1);
		} else if (verification.equalsIgnoreCase("isCategoryVerify")) {
			entity.setVerifyType(2);
		} else if (verification.equalsIgnoreCase("isQuotationVerify")) {
			entity.setVerifyType(3);
		}
		
		entity.getQuotationRecords().addAll(quotationRecordList);
		
		indoorVerificationHistoryDao.save(entity);
	}
	
	public void getConversionTableIds(DatatableRequestModel model, QuotationRecordDataConversionSessionModel sessionModel, Integer userId, String ... status){
		
		Order order = this.getOrder(model, 
				"", "iqr.referenceDate", "iqr.indoorQuotationRecordId", "q.quotationId", "iqr.isFlag"
				, "sg.englishName", "sg.chineseName", "u.englishName", "u.chineseName", "substring(ot.Code, len(ot.Code)-2, 3)"
				, "o.name", "concat(pa.SpecificationName,'=',ps.Value)", "iqr.CurrentSPrice", "iqr.PreviousSPrice"
				, "case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null "
					+"else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice ) * 100, 3) as decimal(10, 3)) end"
				, "case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end"
				, "case when u.Seasonality = 1 then 'All-time' else"
					+ "		 case when u.Seasonality = 2 then 'Summer' else"
					+ "			 case when u.Seasonality = 3 then 'Winter' else "
					+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
					+ "			 end"
					+ "		 end"
					+ " end"
				, "iqr.Status", "qr.Reason", "qr.ProductRemark", "qr.Remark"
			);
		
		String search = model.getSearch().get("value");
		
		Integer purposeId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))) 
			purposeId = Integer.parseInt(model.getSearch().get("purposeId"));
		
		Integer subGroupId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("subGroupId"))) 
			subGroupId = Integer.parseInt(model.getSearch().get("subGroupId"));
		
		List<Integer> unitId = new ArrayList<Integer>();
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
		}
		
		Integer outletId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			outletId = Integer.parseInt(model.getSearch().get("outletId"));
		
		List<String> outletTypeShortCode = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
			outletTypeShortCode = Arrays.asList(model.getSearch().get("outletTypeId").split("\\s*,\\s*"));
		}
	
		Integer seasonalItem = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("seasonalItem")))
			seasonalItem = Integer.parseInt(model.getSearch().get("seasonalItem"));
		
		
		String surveyForm = model.getSearch().get("surveyForm");
		
		String outletCategory = model.getSearch().get("outletCategory");
		
		String referenceMonthStr = model.getSearch().get("referenceMonthStr");
		
		Boolean allocatedIndoorOfficer = null;
		Integer indoorOfficer = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("allocatedIndoorOfficer"))){
			allocatedIndoorOfficer = (model.getSearch().get("allocatedIndoorOfficer").trim().equals("0")) ? Boolean.FALSE : Boolean.TRUE;
			if (allocatedIndoorOfficer){
				indoorOfficer = Integer.parseInt(model.getSearch().get("allocatedIndoorOfficer"));
			}
		}
		
		Double greatedThan = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("greaterThan"))){			
			try{
				greatedThan = Double.parseDouble(model.getSearch().get("greaterThan"));
			}catch(NumberFormatException nfe){
				greatedThan = null;
			}
		}
		
		Double lessThan = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("lessThan"))){
			try{
				lessThan = Double.parseDouble(model.getSearch().get("lessThan"));
			}catch(NumberFormatException nfe){
				lessThan = null;
			}
		}
		
		Double equal = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("equal"))){
			try{
				equal = Double.parseDouble(model.getSearch().get("equal"));
			}catch(NumberFormatException nfe){
				equal = null;
			}
		}
		
		Boolean withPriceReason = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withPriceReason")))
			withPriceReason = (model.getSearch().get("withPriceReason").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withOtherRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withOtherRemark")))
			withOtherRemark = (model.getSearch().get("withOtherRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withProductRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withProductRemark")))
			withProductRemark = (model.getSearch().get("withProductRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withDiscountRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withDiscountRemark")))
			withDiscountRemark = (model.getSearch().get("withDiscountRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Date referenceMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
		
		try {
			referenceMonth = this.commonService.getMonth(referenceMonthStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Boolean subPrice = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("subPrice"))) {
			subPrice = (model.getSearch().get("subPrice").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean priceRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("priceRemark"))) {
			priceRemark = (model.getSearch().get("priceRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean outletCategoryRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletCategoryRemark"))) {
			outletCategoryRemark = (model.getSearch().get("outletCategoryRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean withFieldwork = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withFieldwork")))
			withFieldwork = (model.getSearch().get("withFieldwork").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean isPRNull = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("isPRNull"))) {
			isPRNull = (model.getSearch().get("isPRNull").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Integer firmStatus = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("firmStatus"))){
			firmStatus = Integer.parseInt(model.getSearch().get("firmStatus"));
		}
		
		Integer availability = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("availability"))){
			availability = Integer.parseInt(model.getSearch().get("availability"));
		}
		
		Integer quotationId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("quotationId"))){
			quotationId = Integer.parseInt(model.getSearch().get("quotationId"));
		}
		
		sessionModel.setIndoorQuotationRecordIds(this.indoorQuotationRecordDao.getConversionTableIds(search,
				userId, status, purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, 
				seasonalItem, outletCategory, subPrice, outletCategoryRemark, priceRemark,surveyForm, 
				allocatedIndoorOfficer, indoorOfficer, greatedThan, lessThan, equal,
				withPriceReason, withOtherRemark, withProductRemark, withDiscountRemark,
				withFieldwork, isPRNull, firmStatus, availability, quotationId,
				order));
	}
	
	public void getReviewTableIds(DatatableRequestModel model, QuotationRecordDataConversionSessionModel sessionModel, Integer userId, String ... status){
		String search = model.getSearch().get("value");
		
		Integer purposeId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("purposeId"))) 
			purposeId = Integer.parseInt(model.getSearch().get("purposeId"));
		
		Integer subGroupId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("subGroupId"))) 
			subGroupId = Integer.parseInt(model.getSearch().get("subGroupId"));
		
		List<Integer> unitId = new ArrayList<Integer>();
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
		}
		
		Integer outletId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			outletId = Integer.parseInt(model.getSearch().get("outletId"));
		
		List<String> outletTypeShortCode = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
			outletTypeShortCode = Arrays.asList(model.getSearch().get("outletTypeId").split("\\s*,\\s*"));
		}
	
		Integer seasonalItem = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("seasonalItem")))
			seasonalItem = Integer.parseInt(model.getSearch().get("seasonalItem"));
		
		
		String surveyForm = model.getSearch().get("surveyForm");
		
		String outletCategory = model.getSearch().get("outletCategory");
		
		String referenceMonthStr = model.getSearch().get("referenceMonthStr");
		
		String referenceDateCrit = model.getSearch().get("referenceDateCrit");
		
		Boolean allocatedIndoorOfficer = null;
		Integer indoorOfficer = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("allocatedIndoorOfficer"))){
			allocatedIndoorOfficer = (model.getSearch().get("allocatedIndoorOfficer").trim().equals("0")) ? Boolean.FALSE : Boolean.TRUE;
			if (allocatedIndoorOfficer){
				indoorOfficer = Integer.parseInt(model.getSearch().get("allocatedIndoorOfficer"));
			}
		}
		
		Double greatedThan = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("greaterThan"))){			
			try{
				greatedThan = Double.parseDouble(model.getSearch().get("greaterThan"));
			}catch(NumberFormatException nfe){
				greatedThan = null;
			}
		}
		
		Double lessThan = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("lessThan"))){
			try{
				lessThan = Double.parseDouble(model.getSearch().get("lessThan"));
			}catch(NumberFormatException nfe){
				lessThan = null;
			}
		}
		
		Double equal = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("equal"))){
			try{
				equal = Double.parseDouble(model.getSearch().get("equal"));
			}catch(NumberFormatException nfe){
				equal = null;
			}
		}
		
		Boolean withPriceReason = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withPriceReason")))
			withPriceReason = (model.getSearch().get("withPriceReason").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withOtherRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withOtherRemark")))
			withOtherRemark = (model.getSearch().get("withOtherRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withProductRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withProductRemark")))
			withProductRemark = (model.getSearch().get("withProductRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withDiscountRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withDiscountRemark")))
			withDiscountRemark = (model.getSearch().get("withDiscountRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Boolean withDiscount = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withDiscount")))
			withDiscount = (model.getSearch().get("withDiscount").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		
		Date referenceMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
		
		try {
			referenceMonth = this.commonService.getMonth(referenceMonthStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Boolean subPrice = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("subPrice"))) {
			subPrice = (model.getSearch().get("subPrice").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean priceRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("priceRemark"))) {
			priceRemark = (model.getSearch().get("priceRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean outletCategoryRemark = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletCategoryRemark"))) {
			outletCategoryRemark = (model.getSearch().get("outletCategoryRemark").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean withFieldwork = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withFieldwork"))) {
			withFieldwork = (model.getSearch().get("withFieldwork").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean isPRNull = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("isPRNull"))) {
			isPRNull = (model.getSearch().get("isPRNull").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		Boolean withIndoorConversionRemarks = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("withIndoorConversionRemarks"))) {
			withIndoorConversionRemarks = (model.getSearch().get("withIndoorConversionRemarks").trim().equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}
		Integer firmStatus = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("firmStatus"))){
			firmStatus = Integer.parseInt(model.getSearch().get("firmStatus"));
		}
		Integer availability = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("availability"))){
			availability = Integer.parseInt(model.getSearch().get("availability"));
		}
		Integer quotationId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("quotationId"))){
			quotationId = Integer.parseInt(model.getSearch().get("quotationId"));
		}
		
		sessionModel.setIndoorQuotationRecordIds(this.indoorQuotationRecordDao.getReviewTableIds(search,
				userId, status, purposeId, referenceMonth, subGroupId, unitId, outletId, outletTypeShortCode, 
				seasonalItem, outletCategory, subPrice, outletCategoryRemark, priceRemark,surveyForm, 
				allocatedIndoorOfficer, indoorOfficer, greatedThan, lessThan, equal,
				withPriceReason, withOtherRemark, withProductRemark, withDiscountRemark,
				withFieldwork, isPRNull, withIndoorConversionRemarks, referenceDateCrit, firmStatus,
				availability, withDiscount, quotationId
				));
	}
	
	public boolean isFRLocked(IndoorQuotationRecord iqr) {
		SurveyMonth sm = this.surveyMonthService.getSurveyMonthByReferenceMonth(iqr.getReferenceMonth());
		if (sm == null) {
			return false;
		}
		Date today = new Date();
		today = commonService.getDateWithoutTime(today);
		if (sm.getClosingDate() != null && sm.getClosingDate().getClosingDate() != null
				&& today.after(sm.getClosingDate().getClosingDate())) {
			return true;
		}
		if (iqr.getQuotation() != null && iqr.getQuotation().getTempIsFRApplied() != null && iqr.getQuotation().getTempIsFRApplied()) {
			return true;
		}
		
		return false;
	}

	public Long countAllocateQuotationRecordConversion(Date referenceMonth, Integer purposeId, String[] status, String notEqStatus, Integer userId){
		List<Integer> batchId = new ArrayList<Integer>();
		batchId = this.batchDao.getBatchByUserId(userId);
		return this.indoorQuotationRecordDao.countAllocateQuotationRecordConversion(referenceMonth, purposeId, status, notEqStatus, batchId);
	}
	
	public QuotationRecordDataConversionViewModel prepareConversionVideModel(DatatableRequestModel model) {
		if (model == null || model.getSearch() == null) {
			return null;
		}
		
		String search = model.getSearch().get("value");
		
		List<String> outletTypeShortCode = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getSearch().get("outletTypeId"))){
			outletTypeShortCode = Arrays.asList(model.getSearch().get("outletTypeId").split("\\s*,\\s*"));
		}
		
		String unitCategory = model.getSearch().get("outletCategory");
		
		
		Integer outletId = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("outletId")))
			outletId = Integer.parseInt(model.getSearch().get("outletId"));

		List<Integer> unitId = new ArrayList<Integer>();
		if (StringUtils.isNotEmpty(model.getSearch().get("unitId"))){
			List<String> tempIdsString =Arrays.asList(model.getSearch().get("unitId").split("\\s*,\\s*"));
			for(String s : tempIdsString) unitId.add(Integer.valueOf(s));
		}
		
		String surveyForm = model.getSearch().get("surveyForm");
	
		Integer seasonalItem = null;
		if (StringUtils.isNotEmpty(model.getSearch().get("seasonalItem")))
			seasonalItem = Integer.parseInt(model.getSearch().get("seasonalItem"));

		QuotationRecordDataConversionViewModel viewModel = new QuotationRecordDataConversionViewModel();
		viewModel.setSearch(search);
		
		viewModel.setOrderColumn(Integer.parseInt(model.getOrder().get(0).get("column")));
		viewModel.setOrderDir(model.getOrder().get(0).get("dir"));
		
		if (outletTypeShortCode.size() > 0) {
			viewModel.setOutletTypeSelected(new ArrayList<KeyValueModel>());
			List<VwOutletTypeShortForm> outletTypes = vwOutletTypeShortFormDao.getByIds(outletTypeShortCode.toArray(new String[0]));
			for (VwOutletTypeShortForm outletType : outletTypes) {
				KeyValueModel keyValue = new KeyValueModel();
				keyValue.setKey(outletType.getShortCode() + " - " + outletType.getChineseName());
				keyValue.setValue(outletType.getShortCode());
				viewModel.getOutletTypeSelected().add(keyValue);
			}
		}
		
		viewModel.setUnitCategorySelected(unitCategory);
		
		if (outletId != null) {
			Outlet entity = outletDao.findById(outletId);
			KeyValueModel keyValue = new KeyValueModel();
			keyValue.setKey(entity.getFirmCode() + " - " + entity.getName());
			keyValue.setValue(entity.getId().toString());
			viewModel.setOutletSelected(keyValue);
		}
		
		viewModel.setSurveyFormSelected(surveyForm);
		viewModel.setSeasonalItemSelected(seasonalItem != null ? seasonalItem.toString() : null);
		viewModel.setAvailabilitySelected(model.getSearch().get("availability"));
		viewModel.setFirmStatusSelected(model.getSearch().get("firmStatus"));
		viewModel.setIsPRNullSelected(model.getSearch().get("isPRNull"));
		viewModel.setQuotationIdSelected(model.getSearch().get("quotationId"));
		
		return viewModel;
	}
	
	public boolean isIP(IndoorQuotationRecord iqr) {
		if (iqr.getQuotationRecord() == null) {
			return false;
		}
		return iqr.getQuotationRecord().getQuotationState().equals("IP") || iqr.getQuotationRecord().getAvailability() == 2;
	}
	
	public void updateIndoorStatusInQuotationRecordList(List<QuotationRecord> qrList, String status) {
		List<Integer> qrIds = new ArrayList<Integer>();
		for (QuotationRecord qr : qrList) {
			qrIds.add(qr.getId());
		}
		List<IndoorQuotationRecord> iqrList = indoorQuotationRecordDao.getIndoorQuotationRecordsByQuotationRecordIds(qrIds);
		for (IndoorQuotationRecord iqr : iqrList) {
			iqr.setStatus(status);
			indoorQuotationRecordDao.save(iqr);
		}
	}
	
	public IndoorQuotationRecord getFirstHistoryRecordByQuotationId(IndoorQuotationRecord iqr){
		return indoorQuotationRecordDao.getFirstHistoryRecordByQuotationId(iqr.getQuotation().getQuotationId(), iqr.getReferenceDate());
	}
	
	/**
	 * Get quotation id select2 format
	 */
	public Select2ResponseModel queryQuotationIdSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<Integer> quotationIds = indoorQuotationRecordDao.searchQuotationIdForFilter(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		queryModel.setRecordsTotal(indoorQuotationRecordDao.countQuotationIdSelect2(queryModel.getTerm()));
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Integer quotationId : quotationIds){
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(quotationId));
			item.setText(String.valueOf(quotationId));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	public void clearIndoorAllocatedOfficer(List<Integer> indoorQuotationRecordIds){	
		List<List<Integer>> splitList = commonService.splitListByMaxSize(indoorQuotationRecordIds);
		
		List<IndoorQuotationRecord> entities = new ArrayList<IndoorQuotationRecord>();
		
		for (List<Integer> subList : splitList) {
			entities.addAll(indoorQuotationRecordDao.getEntityByIds(subList));
		}
		
		for (IndoorQuotationRecord entity : entities) {
			entity.setStatus("Allocation");
			entity.setUser(null);
			this.indoorQuotationRecordDao.save(entity);
		}
		
		this.indoorQuotationRecordDao.flush();
	}
}

