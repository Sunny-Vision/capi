package capi.service.assignmentManagement;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.SystemException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AllocationBatchDao;
import capi.dal.AssignmentDao;
import capi.dal.AssignmentReallocationDao;
import capi.dal.AssignmentUnitCategoryInfoDao;
import capi.dal.IndoorQuotationRecordDao;
import capi.dal.OutletDao;
import capi.dal.PointToNoteDao;
import capi.dal.ProductDao;
import capi.dal.ProductGroupDao;
import capi.dal.ProductSpecificationDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.SubPriceColumnDao;
import capi.dal.SubPriceFieldMappingDao;
import capi.dal.SubPriceRecordDao;
import capi.dal.SubPriceTypeDao;
import capi.dal.SurveyMonthDao;
import capi.dal.TourRecordDao;
import capi.dal.UnitDao;
import capi.dal.UomDao;
import capi.dal.UserDao;
import capi.dal.VwProductFullSpecDao;
import capi.entity.AllocationBatch;
import capi.entity.Assignment;
import capi.entity.AssignmentReallocation;
import capi.entity.AssignmentUnitCategoryInfo;
import capi.entity.IndoorQuotationRecord;
import capi.entity.OnSpotValidation;
import capi.entity.Outlet;
import capi.entity.PointToNote;
import capi.entity.Product;
import capi.entity.ProductAttribute;
import capi.entity.ProductGroup;
import capi.entity.ProductSpecification;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.SubPriceColumn;
import capi.entity.SubPriceFieldMapping;
import capi.entity.SubPriceRecord;
import capi.entity.SubPriceType;
import capi.entity.SurveyMonth;
import capi.entity.TourRecord;
import capi.entity.UOMCategory;
import capi.entity.Unit;
import capi.entity.Uom;
import capi.entity.User;
import capi.entity.VwOutletTypeShortForm;
import capi.entity.VwProductFullSpec;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.Select2ResponseModel.Pagination;
import capi.model.Select2ResponseModel.Select2Item;
import capi.model.api.dataSync.QuotationRecordResponseModel;
import capi.model.api.dataSync.QuotationRecordSyncData;
import capi.model.api.dataSync.SubPriceColumnSyncData;
import capi.model.api.dataSync.SubPriceRecordSyncData;
import capi.model.api.dataSync.TourRecordSyncData;
import capi.model.assignmentManagement.QuotationRecordHistoryDateModel;
import capi.model.assignmentManagement.QuotationRecordTableList;
import capi.model.commonLookup.ChangeProductViewModel;
import capi.model.masterMaintenance.SubPriceFieldList;
import capi.model.productMaintenance.ProductSpecificationEditModel;
import capi.model.shared.quotationRecord.CheckProductChangeResultModel;
import capi.model.shared.quotationRecord.OutletViewModel;
import capi.model.shared.quotationRecord.PagePostModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.model.shared.quotationRecord.ProductPostModel;
import capi.model.shared.quotationRecord.QuotationRecordPostModel;
import capi.model.shared.quotationRecord.QuotationRecordViewModel;
import capi.model.shared.quotationRecord.SubPriceColumnModel;
import capi.model.shared.quotationRecord.SubPriceModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.masterMaintenance.SubPriceService;
import capi.service.productMaintenance.ProductService;
import edu.emory.mathcs.backport.java.util.Arrays;
import capi.model.api.onlineFunction.CheckAssignmentAndQuotationRecordStatus;
@Service("QuotationRecordMaintenanceService")
public class QuotationRecordService extends BaseService {

	private static final Logger logger = LoggerFactory.getLogger(QuotationRecordService.class);

	@Autowired
	private CommonService commonService;

	@Autowired
	private ProductService productService;

	@Autowired
	private SubPriceService subPriceService;

	@Autowired
	private QuotationRecordDao quotationRecordDao;

	@Autowired
	private UomDao uomDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private VwProductFullSpecDao productFullSpecDao;

	@Autowired
	private SubPriceColumnDao subPriceColumnDao;

	@Autowired
	private SubPriceRecordDao subPriceRecordDao;

	@Autowired
	private SubPriceTypeDao subPriceTypeDao;

	@Autowired
	private SurveyMonthDao surveyMonthDao;

	@Autowired
	private SubPriceFieldMappingDao subPriceFieldMappingDao;

	@Autowired
	private ProductSpecificationDao productSpecificationDao;

	@Autowired
	private TourRecordDao tourRecordDao;

	@Autowired
	private AssignmentUnitCategoryInfoDao assignmentUnitCategoryInfoDao;

	@Autowired
	private QuotationRecordValidationService quotationRecordValidationService;

	@Autowired
	private OnSpotValidationService onSpotValidationService;

	@Autowired
	private ProductGroupDao productGroupDao;

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private OutletDao outletDao;

	@Autowired
	private QuotationDao quotationDao;

	@Autowired
	private AssignmentDao assignmentDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AllocationBatchDao allocationBatchDao;

	@Autowired
	private IndoorQuotationRecordDao indoorQuotationRecordDao;

	@Autowired
	private AssignmentReallocationDao assignmentReallocationDao;

	@Autowired
	private PointToNoteDao pointToNoteDao;

	@Autowired
	private QuotationService quotationService;
	
	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;
	
	/**
	 * DataTable query
	 */
	public DatatableResponseModel<QuotationRecordTableList> getTableList(DatatableRequestModel model,
			String referenceMonth, Integer purposeId, Integer outletId, String[] outletTypeId, Integer[] unitId,
			Integer[] districtId, Integer[] tpuId, String status, Integer[] userId) throws Exception {

		Order order = this.getOrder(model, "id", "quotationId", "officer", "deadline2", "unitCode", "unitName",
				"firmName", "outletType", "districtCode", "tpu", "productAttribute1", "productAttribute2",
				"productAttribute3", "status", "nPrice", "sPrice", "discount", "subPrice", "remark", "mapAddress",
				"detailAddress", "isBackTrack", "productAttribute4", "productAttribute5", "firmId", "id");

		String search = model.getSearch().get("value");

		SurveyMonth month = surveyMonthDao.getSurveyMonthByReferenceMonth(commonService.getMonth(referenceMonth));
		if (month == null) {
			DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
			response.setDraw(model.getDraw());
			response.setRecordsTotal(0);
			return response;
		}
		// Date startDate = month.getStartDate();
		// Date endDate = null;
		// Date today = commonService.getDateWithoutTime(new Date());
		// if (DateUtils.truncatedCompareTo(today, month.getEndDate(),
		// Calendar.DAY_OF_MONTH) > 0)
		// endDate = month.getEndDate();
		// else
		// endDate = today;

		List<QuotationRecordTableList> result = quotationRecordDao.getTableList(search, model.getStart(),
				model.getLength(), order, month.getSurveyMonthId(), purposeId, outletId, outletTypeId, unitId,
				districtId, tpuId, status, userId);

		DatatableResponseModel<QuotationRecordTableList> response = new DatatableResponseModel<QuotationRecordTableList>();
		response.setDraw(model.getDraw());
		response.setData(result);
		Long recordTotal = quotationRecordDao.countTableList("", month.getSurveyMonthId(), purposeId, null, null, null,
				null, null, null, null);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = quotationRecordDao.countTableList(search, month.getSurveyMonthId(), purposeId, outletId,
				outletTypeId, unitId, districtId, tpuId, status, userId);
		response.setRecordsFiltered(recordFiltered.intValue());

		return response;
	}

	public PageViewModel prepareViewModel(int id, boolean loadForHistoryTab, boolean readonly) {
		return prepareViewModel(id, loadForHistoryTab, readonly, false);
	}

	/**
	 * Prepare view model
	 */
	public PageViewModel prepareViewModel(int id, boolean loadForHistoryTab, boolean readonly,
			boolean peCheckRemarkReturnRaw) {
		PageViewModel model = new PageViewModel();
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
			histories = getHistoryDatesAndRecordId(quotationRecord);
			if (histories.size() > 0) {
				historyRecordEntity = quotationRecordDao.getByIdWithRelated(histories.get(0).getId());
			}
		}

		QuotationRecordViewModel quotationRecordViewModel = prepareQuotationRecordViewModel(quotationRecord,
				historyRecordEntity, loadForHistoryTab);
		model.setQuotationRecord(quotationRecordViewModel);

		QuotationRecord backNoQuotationRecord = quotationRecordDao.getBackNoRecord(id);
		if (backNoQuotationRecord == null) {
			backNoQuotationRecord = new QuotationRecord();
			initBackNoByOriginal(quotationRecord, backNoQuotationRecord);
		}
		QuotationRecordViewModel backNoQuotationRecordViewModel = prepareQuotationRecordViewModel(backNoQuotationRecord,
				historyRecordEntity, loadForHistoryTab);
		model.setBackNoQuotationRecord(backNoQuotationRecordViewModel);

		if (!loadForHistoryTab) {
			ProductPostModel productViewModel = prepareProductViewModel(quotationRecord);
			model.setProduct(productViewModel);

			if (quotationRecord.getOutlet() != null) {
				OutletViewModel outletViewModel = prepareOutletViewModel(quotationRecord.getOutlet());
				model.setOutlet(outletViewModel);
			}

			model.setHistories(histories);

			model.setPointToNote(concatPointToNotes(quotationRecord));
			model.setVerificationRemark(commonService.nl2br(quotationRecord.getVerificationRemark()));
			model.setRejectReason(commonService.nl2br(quotationRecord.getRejectReason()));
			if (peCheckRemarkReturnRaw) {
				model.setPeCheckRemark(quotationRecord.getPeCheckRemark());
			} else {
				model.setPeCheckRemark(commonService.nl2br(quotationRecord.getPeCheckRemark()));
			}
			model.setQuotationId(quotationRecord.getQuotation().getId());
			model.setQuotationRecordId(quotationRecord.getId());
			if (quotationRecord.getUser() != null) {
				model.setOfficer(quotationRecord.getUser().getEnglishName());
			}
			model.setValidationError(quotationRecord.getValidationError());
		}

		return model;
	}

	/**
	 * Prepare outlet view model
	 */
	public OutletViewModel prepareOutletViewModel(Outlet entity) {
		OutletViewModel model = new OutletViewModel();
		BeanUtils.copyProperties(entity, model);
		if (entity.getOutletTypes() != null && entity.getOutletTypes().size() > 0) {
			List<String> outletTypes = new ArrayList<String>();
			for (VwOutletTypeShortForm outletType : entity.getOutletTypes()) {
				outletTypes.add(outletType.getShortCode() + " - " + outletType.getChineseName());
			}
			model.setOutletTypeFormatted(StringUtils.join(outletTypes, " / "));
		}
		if (entity.getTpu().getDistrict() != null) {
			model.setDistrictFormatted(
					entity.getTpu().getDistrict().getCode() + " - " + entity.getTpu().getDistrict().getChineseName());
			model.setDistrictId(entity.getTpu().getDistrict().getId());
		}

		return model;
	}

	/**
	 * Prepare quotation record view model
	 */
	public QuotationRecordViewModel prepareQuotationRecordViewModel(QuotationRecord entity,
			QuotationRecord historyEntity, boolean loadForHistoryTab) {
		autoFillQuotationRecordWhenOpen(entity, historyEntity);

		int originalQuotationRecordId = 0;
		if (entity.isBackNo())
			originalQuotationRecordId = entity.getOriginalQuotationRecord().getId();
		else
			originalQuotationRecordId = entity.getId();

		QuotationRecordViewModel model = new QuotationRecordViewModel();
		BeanUtils.copyProperties(entity, model);
		if (model.getReferenceDate() == null)
			model.setReferenceDate(new Date());
		if (model.getCollectionDate() == null)
			model.setCollectionDate(new Date());

		if (historyEntity != null) {
			if (historyEntity.getnPrice() != null && model.getnPrice() != null && historyEntity.getnPrice() > 0) {
				Double result = calculatePercentageChange(historyEntity.getId(),
						entity.getQuotation().getUnit().getId(), historyEntity.getnPrice(), model.getnPrice(),
						entity.getUom() != null ? entity.getUom().getId() : null, entity.getUomValue());
				model.setHistoryNPricePercent(result);
			}
			if (historyEntity.getsPrice() != null && model.getsPrice() != null && historyEntity.getsPrice() > 0) {
				Double result = calculatePercentageChange(historyEntity.getId(),
						entity.getQuotation().getUnit().getId(), historyEntity.getsPrice(), model.getsPrice(),
						entity.getUom() != null ? entity.getUom().getId() : null, entity.getUomValue());
				model.setHistorySPricePercent(result);
			}
		}

		if (entity.getQuotation() != null && entity.getQuotation().getUnit() != null) {
			if (entity.getQuotation().getUnit().getSubPriceType() != null) {
				model.setSubPriceTypeId(entity.getQuotation().getUnit().getSubPriceType().getId());
				model.setUnitSubPriceType(true);
			}
			model.setFRRequired(entity.getQuotation().getUnit().isFrRequired());
		}
		if (!model.isUnitSubPriceType() && entity.getSubPriceRecords() != null
				&& entity.getSubPriceRecords().size() > 0) {
			model.setSubPriceTypeId(entity.getSubPriceRecords().iterator().next().getSubPriceType().getId());
		}

		if (entity.getQuotation() != null && entity.getQuotation().getUnit() != null){
			if(entity.getQuotation().getUnit().getStandardUOM() != null) {
				model.setShowUomInput(true);
			}
			if(entity.getQuotation().getUnit().getSeasonality() != null){
				model.setSeasonality(entity.getQuotation().getUnit().getSeasonality());
			}
			Integer[] categoryIds = getUomCategoriesIdByQuotationRecord(entity);
			if (categoryIds != null) {
				model.setUomCategoryIdCSV(StringUtils.join(categoryIds, ","));
			}
		}

		if (entity.getOutlet() != null && entity.getOutlet().getOutletTypes() != null) {
			List<String> outletTypes = new ArrayList<String>();

			model.setUseFRAdmin(entity.getOutlet().isUseFRAdmin());
			for (VwOutletTypeShortForm outletType : entity.getOutlet().getOutletTypes()) {
				outletTypes.add(outletType.getShortCode());
			}
			model.setOutletTypeShortCodeCSV(StringUtils.join(outletTypes, ","));
		}

		if (entity.getQuotation() != null && entity.getQuotation().getUnit() != null) {
			model.setNPriceMandatory(entity.getQuotation().getUnit().isNPriceMandatory());
			model.setSPriceMandatory(entity.getQuotation().getUnit().isSPriceMandatory());
		}

		if (!loadForHistoryTab && reminderForPricingCycle(entity)) {
			model.setReminderForPricingCycleMessage(onSpotValidationService.getMessage("onSpotValidationMessage14"));
		}

		if (entity.getQuotation() != null && entity.getQuotation().getUnit() != null
				&& entity.getQuotation().getUnit().getOnSpotValidation() != null) {
			OnSpotValidation validationModel = entity.getQuotation().getUnit().getOnSpotValidation();
			model.setProvideRemarkForNotAvailableQuotation(validationModel.isProvideRemarkForNotAvailableQuotation());
		}

//		if (entity.getProductRemark() == null) {
//			model.setProductRemark(entity.getQuotation().getProductRemark());
//		}
//
//		if (entity.getProductPosition() == null) {
//			model.setProductPosition(entity.getQuotation().getProductPosition());
//		}

		if (entity.getAssignment() != null) {
			Date surveyMonthReferenceMonth = entity.getAssignment().getSurveyMonth().getReferenceMonth();
			Calendar cal = Calendar.getInstance();
			cal.setTime(surveyMonthReferenceMonth);
			int surveyMonthDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			model.setNumberOfDaysOfSurveyMonth(surveyMonthDays);
		}

		if (entity.getUser() != null){
			String fieldOfficer = entity.getUser().getStaffCode() + " - " + entity.getUser().getChineseName();
			
			if (entity.getUser().getDestination() != null){
				fieldOfficer += " ( " + entity.getUser().getDestination() + " )";
			}
			model.setFieldOfficer(fieldOfficer);
		}
		
		model.setShowResetChangeProductButton(entity.isProductChange());
		
		if (model.getSubPriceTypeId() != null) {
			String subPriceTypeName = subPriceService.querySubPriceTypeSelectSingle(model.getSubPriceTypeId());
			model.setSubPriceTypeName(subPriceTypeName);
			SubPriceType subPriceTypeDataModel = subPriceService.getSubPriceById(model.getSubPriceTypeId());
			model.setSubPriceTypeDataModel(subPriceTypeDataModel);

			List<SubPriceFieldList> fields = subPriceService.getSubPriceFieldByType(model.getSubPriceTypeId());
			model.setFields(fields);

			List<SubPriceFieldMapping> mappings = subPriceFieldMappingDao.getMapByTypeId(model.getSubPriceTypeId(),
					true);
			if (entity.getSubPriceRecords() != null && entity.getSubPriceRecords().size() > 0) {
				List<SubPriceRecord> subPriceRecords = subPriceRecordDao.getAllByQuotationRecordId(entity.getId());

				model.setSubPrices(new ArrayList<SubPriceModel>());
				for (SubPriceRecord subPriceEntity : subPriceRecords) {
					if (subPriceEntity.getSubPriceColumns() == null || subPriceEntity.getSubPriceColumns().size() == 0)
						continue;
					SubPriceModel subPriceModel = new SubPriceModel();
					model.getSubPrices().add(subPriceModel);

					BeanUtils.copyProperties(subPriceEntity, subPriceModel);
					subPriceModel.setSubPriceColumns(new ArrayList<SubPriceColumnModel>());

					List<SubPriceColumn> columns = subPriceColumnDao.getAllByRecordId(subPriceEntity.getId());
					for (SubPriceFieldMapping map : mappings) {
						SubPriceColumnModel columnModel = new SubPriceColumnModel();
						BeanUtils.copyProperties(map.getSubPriceField(), columnModel);
						subPriceModel.getSubPriceColumns().add(columnModel);
						SubPriceColumn columnEntity = null;
						for (SubPriceColumn temp : columns) {
							if (temp.getSubPriceFieldMapping().getId().intValue() == map.getId().intValue()) {
								columnEntity = temp;
								break;
							}
						}
						if (columnEntity != null) {
							BeanUtils.copyProperties(columnEntity, columnModel);
						}
					}
				}
			}
		}

		if (loadForHistoryTab) {
			model.setAssignmentContactPerson(entity.getContactPerson());
		} else {
			AssignmentUnitCategoryInfo catInfo = assignmentUnitCategoryInfoDao.findByUnitCategoryAndAssignment(
					entity.getQuotation().getUnit().getUnitCategory(), entity.getAssignment().getAssignmentId());
			if (catInfo != null) {
				model.setAssignmentCategoryRemark(catInfo.getRemark());
				model.setAssignmentContactPerson(catInfo.getContactPerson());
			}

			if (StringUtils.isEmpty(model.getAssignmentContactPerson())) {
				if (entity.getOutlet() != null) {
					if (!StringUtils.isEmpty(entity.getOutlet().getLastContact())) {
						model.setAssignmentContactPerson(entity.getOutlet().getLastContact());
					} else {
						model.setAssignmentContactPerson(entity.getOutlet().getMainContact());
					}
				}
			}
		}

		return model;
	}

	/**
	 * Prepare product view model
	 */
	public ProductPostModel prepareProductViewModel(QuotationRecord entity) {
		ProductPostModel model = new ProductPostModel();

		if (entity.getQuotation() != null && entity.getQuotation().getUnit() != null) {
			model.setDisplayName(entity.getQuotation().getUnit().getDisplayName());
			model.setAllowProductChange(entity.getQuotation().getUnit().isAllowProductChange());
		}

		if (entity.getProduct() != null) {
			BeanUtils.copyProperties(entity.getProduct(), model);
			List<ProductSpecificationEditModel> attributes = new ArrayList<ProductSpecificationEditModel>();
			List<VwProductFullSpec> specs = productFullSpecDao.GetAllByProductId(entity.getProduct().getId(), null);
			for (VwProductFullSpec spec : specs) {
				ProductSpecificationEditModel attModel = new ProductSpecificationEditModel();
				BeanUtils.copyProperties(spec, attModel);
				attModel.setName(spec.getSpecificationName());
				attModel.setProductAttributeId(spec.getProductAttribute().getId());
				attributes.add(attModel);
			}
			model.setAttributes(attributes);

			model.setProductGroupId(entity.getProduct().getProductGroup().getId());
		} else {
			model.setProductGroupId(entity.getQuotation().getUnit().getProductCategory().getId());
			List<ProductSpecificationEditModel> attributes = new ArrayList<ProductSpecificationEditModel>();
			List<ProductAttribute> orderedList = new ArrayList<ProductAttribute>(
					entity.getQuotation().getUnit().getProductCategory().getProductAttributes());
			Collections.sort(orderedList, new Comparator<ProductAttribute>() {
				@Override
				public int compare(ProductAttribute a, ProductAttribute b) {
					return a.getSequence().compareTo(b.getSequence());
				}
			});

			for (ProductAttribute att : orderedList) {
				ProductSpecificationEditModel attModel = new ProductSpecificationEditModel();
				BeanUtils.copyProperties(att, attModel);
				attModel.setName(att.getSpecificationName());
				attModel.setProductAttributeId(att.getId());
				attributes.add(attModel);
			}
			model.setAttributes(attributes);
		}

		model.setCollectionDate(commonService.formatDate(entity.getCollectionDate()));
		model.setProductRemark(entity.getProductRemark());

		return model;
	}

	/**
	 * Prepare product view model
	 */
	public ProductPostModel prepareProductViewModelByQuotation(Quotation entity) {
		ProductPostModel model = new ProductPostModel();

		model.setDisplayName(entity.getUnit().getDisplayName());

		if (entity.getProduct() != null) {
			BeanUtils.copyProperties(entity.getProduct(), model);
			List<ProductSpecificationEditModel> attributes = new ArrayList<ProductSpecificationEditModel>();
			List<VwProductFullSpec> specs = productFullSpecDao.GetAllByProductId(entity.getProduct().getId(), null);
			for (VwProductFullSpec spec : specs) {
				ProductSpecificationEditModel attModel = new ProductSpecificationEditModel();
				BeanUtils.copyProperties(spec, attModel);
				attModel.setName(spec.getSpecificationName());
				attModel.setProductAttributeId(spec.getProductAttribute().getId());
				attributes.add(attModel);
			}
			model.setAttributes(attributes);

			model.setProductGroupId(entity.getProduct().getProductGroup().getId());
		} else {
			model.setProductGroupId(entity.getUnit().getProductCategory().getId());
			List<ProductSpecificationEditModel> attributes = new ArrayList<ProductSpecificationEditModel>();
			List<ProductAttribute> orderedList = new ArrayList<ProductAttribute>(
					entity.getUnit().getProductCategory().getProductAttributes());
			Collections.sort(orderedList, new Comparator<ProductAttribute>() {
				@Override
				public int compare(ProductAttribute a, ProductAttribute b) {
					return a.getSequence().compareTo(b.getSequence());
				}
			});

			for (ProductAttribute att : orderedList) {
				ProductSpecificationEditModel attModel = new ProductSpecificationEditModel();
				BeanUtils.copyProperties(att, attModel);
				attModel.setName(att.getSpecificationName());
				attModel.setProductAttributeId(att.getId());
				attributes.add(attModel);
			}
			model.setAttributes(attributes);
		}

		model.setCollectionDate(null);
		model.setProductRemark(entity.getProductRemark());

		return model;
	}

	/**
	 * Prepare product view model
	 */
	public ProductPostModel prepareProductViewModel(int quotationRecordId) {
		QuotationRecord quotationRecord = quotationRecordDao.getByIdWithRelated(quotationRecordId);
		if (quotationRecord == null)
			return null;
		return prepareProductViewModel(quotationRecord);
	}

	/**
	 * submit
	 */
	@Transactional
	public void submit(PagePostModel model, InputStream photo1ImageStream, InputStream photo2ImageStream,
			String fileBaseLoc) throws Exception {
		QuotationRecordPostModel originalQuotationRecordModel = model.getQuotationRecord();

		QuotationRecord entity = quotationRecordDao
				.getByIdWithRelated(originalQuotationRecordModel.getQuotationRecordId());
		if (entity == null)
			throw new SystemException("E00011");

		appendVerificationReply(model, entity);

		if (!isAvailabilityNotAvailable(originalQuotationRecordModel.getAvailability())) {
			submitProduct(model, entity, fileBaseLoc, photo1ImageStream, photo2ImageStream);
		}

		submitQuotationRecord(originalQuotationRecordModel, entity);

		List<String> errorMessages = new ArrayList<String>();

		if (entity.getAvailability() == 1 || entity.getAvailability() == 3) {
			errorMessages.addAll(quotationRecordValidationService.validate(entity, entity));
			errorMessages.addAll(onSpotValidationService.validate(entity));
		}

		quotationRecordDao.save(entity);

		if (entity.getFormDisplay() == 1 && entity.isProductChange()
				&& entity.getQuotation().getUnit().isBackdateRequired()) {
			QuotationRecordPostModel backNoQuotationRecordModel = model.getBackNoQuotationRecord();

			backNoQuotationRecordModel.setCollectionDate(originalQuotationRecordModel.getCollectionDate());

			QuotationRecord backNoEntity = null;
			if (backNoQuotationRecordModel.getQuotationRecordId() != null) {
				backNoEntity = quotationRecordDao.getByIdWithRelated(backNoQuotationRecordModel.getQuotationRecordId());
				if (backNoEntity == null)
					throw new SystemException("E00011");
			} else {
				backNoEntity = new QuotationRecord();
				initBackNoByOriginal(entity, backNoEntity);
			}
			appendVerificationReply(model, backNoEntity);
			submitQuotationRecord(backNoQuotationRecordModel, backNoEntity);
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
				QuotationRecord backNoEntity = quotationRecordDao
						.getByIdWithRelated(backNoQuotationRecordModel.getQuotationRecordId());
				if (backNoEntity != null)
					deleteQuotationRecordFromDatabase(backNoEntity);
			}
		}

		if (errorMessages.size() > 0) {
			entity.setPassValidation(false);
			entity.setValidationError(StringUtils.join(errorMessages, "\n"));
		} else {
			entity.setPassValidation(true);
			entity.setValidationError(null);
		}
		quotationRecordDao.save(entity);

		quotationRecordDao.flush();
		updateQuotationRecordByProductChange(entity);
	}

	/**
	 * Submit product
	 */
	@Transactional
	public void submitProduct(PagePostModel model, QuotationRecord recordEntity, String fileBaseLoc,
			InputStream photo1ImageStream, InputStream photo2ImageStream) throws Exception {
		if (model.getProduct() == null)
			return;
		CheckProductChangeResultModel changeResult = checkProductChange(model.getProduct(), recordEntity.getProduct(),
				recordEntity.getQuotation().getUnit().getProductCategory());
		model.getProduct().setLastPostbackChangeResult(changeResult);

		if (!changeResult.isProductChange()) {
			productService.savePhotos(recordEntity.getProduct(), null, photo2ImageStream, fileBaseLoc);
			return;
		}

		recordEntity.setProductChange(true);

		if (!changeResult.isNewProduct()) {
			Product sameAttributeProduct = productDao.findById(changeResult.getSameAttributeProductId());
			recordEntity.setProduct(sameAttributeProduct);
			productService.savePhotos(recordEntity.getProduct(), null, photo2ImageStream, fileBaseLoc);
			return;
		}

		recordEntity.setNewProduct(true);

		Product newProductEntity = new Product();
		newProductEntity.setStatus("Active");
		newProductEntity.setReviewed(false);

		Product oldProductEntity = recordEntity.getProduct();
		if (oldProductEntity != null) {
			newProductEntity.setCountryOfOrigin(oldProductEntity.getCountryOfOrigin());
			newProductEntity.setRemark(oldProductEntity.getRemark());
			newProductEntity.setBarcode(oldProductEntity.getBarcode());
			newProductEntity.setProductGroup(oldProductEntity.getProductGroup());

			for (PointToNote noteEntity : oldProductEntity.getPointToNotes()) {
				newProductEntity.getPointToNotes().add(noteEntity);
			}
		}

		BeanUtils.copyProperties(model.getProduct(), newProductEntity);
		newProductEntity.setProductId(null);
		if (newProductEntity.getProductGroup() == null){
			ProductGroup group = productGroupDao.findById(model.getProduct().getProductGroupId());
			newProductEntity.setProductGroup(group);
		}
		
		productDao.save(newProductEntity);

		productService.savePhotos(newProductEntity, photo1ImageStream, photo2ImageStream, fileBaseLoc);

		if (newProductEntity.getPhoto1Path() == null && oldProductEntity != null && oldProductEntity.getPhoto1Path() != null) {
			String newPath = productService.duplicateImage(oldProductEntity.getPhoto1Path(), fileBaseLoc);
			newProductEntity.setPhoto1Path(newPath);
			newProductEntity.setPhoto1ModifiedTime(new java.util.Date());
		}
		if (newProductEntity.getPhoto2Path() == null && oldProductEntity != null && oldProductEntity.getPhoto2Path() != null) {
			String newPath = productService.duplicateImage(oldProductEntity.getPhoto2Path(), fileBaseLoc);
			newProductEntity.setPhoto2Path(newPath);
			newProductEntity.setPhoto2ModifiedTime(new java.util.Date());
		}

		for (ProductSpecificationEditModel editSpecModel : model.getProduct().getAttributes()) {
			ProductAttribute attEntity = productService.getProductAttributeById(editSpecModel.getProductAttributeId());
			ProductSpecification newSpecEntity = new ProductSpecification();
			newSpecEntity.setProductAttribute(attEntity);
			newSpecEntity.setProduct(newProductEntity);
			newSpecEntity.setValue(editSpecModel.getValue());
			productSpecificationDao.save(newSpecEntity);
		}

		productDao.save(newProductEntity);

		recordEntity.setProduct(newProductEntity);
	}

	/**
	 * Submit quotation record
	 */
	@Transactional
	public void submitQuotationRecord(QuotationRecordPostModel model, QuotationRecord entity) throws Exception {
		Integer oldAvailability = entity.getAvailability();
		if (oldAvailability == null)
			oldAvailability = 1;

		Integer firmStatus = entity.getAssignment().getStatus();

		boolean canEditAll = firmStatus == null || firmStatus == 1 || firmStatus == 9;
		boolean canOnlyEditAvailabilityAndRemark = false;

//		OnSpotValidation onSpotModel = entity.getQuotation().getUnit().getOnSpotValidation();
//		if (onSpotModel != null) {
//			if (onSpotModel.isProvideRemarkForNotAvailableQuotation()) {
//				if (isAvailabilityNotAvailable(model.getAvailability())) {
//					canEditAll = false;
//					canOnlyEditAvailabilityAndRemark = true;
//				}
//			}
//		}
		
		if (canEditAll) {
			String categoryRemark = "";
			String outletDiscountRemark = entity.getOutletDiscountRemark();
			if(entity.getCategoryRemark()!= null && !("".equals(entity.getCategoryRemark()))){
				categoryRemark = entity.getCategoryRemark();
			}
			BeanUtils.copyProperties(model, entity);
			entity.setCategoryRemark(categoryRemark);
			if (StringUtils.isEmpty(model.getOutletDiscountRemark()) && !StringUtils.isEmpty(outletDiscountRemark)){
				entity.setOutletDiscountRemark(outletDiscountRemark);
			}
			if (firmStatus != null && firmStatus == 9)
				entity.setAvailability(oldAvailability);
		} else if (canOnlyEditAvailabilityAndRemark) {
			entity.setAvailability(model.getAvailability());
			entity.setRemark(model.getRemark());
			entity.setDiscountRemark(model.getDiscountRemark());
		} else {
			entity.setRemark(model.getRemark());
		}
		
		entity.setContactPerson(getContactPerson(entity));
		
		// if (entity.getReferenceDate() == null) {
		// if(entity.getAssignedCollectionDate()!=null){
		// //entity.setReferenceDate(entity.getAssignedCollectionDate());
		// } else {
		// entity.setReferenceDate(new Date());
		// }
		// }
		//
		// if (!entity.isBackNo()) {
		// if (StringUtils.isBlank(model.getCollectionDate()))
		// entity.setCollectionDate(null);
		// else
		// entity.setCollectionDate(commonService.getDate(model.getCollectionDate()));
		// } else {
		// if (StringUtils.isBlank(model.getReferenceDate()))
		// entity.setReferenceDate(null);
		// else
		// entity.setReferenceDate(commonService.getDate(model.getReferenceDate()));
		// }

		entity.setCollectionDate(commonService.getDate(model.getCollectionDate()));

		// if checked ip
		if (entity.getAvailability() == 2) {
			entity.setCollectionDate(null);
			if (!"Verify".equals(entity.getQuotationState())) {
				entity.setQuotationState("IP");
			}
		}

		// if old availability ip => new not ip
		// if (oldAvailability != null && oldAvailability == 2 &&
		// entity.getAvailability() != 2) {
		// if(entity.getAssignedCollectionDate()==null){
		// entity.setReferenceDate(entity.getCollectionDate());
		// }
		// }

		if (!entity.isBackNo()) {
			entity.getQuotation().setProductRemark(entity.getProductRemark());
			entity.getQuotation().setProductPosition(entity.getProductPosition());
		}

		// restore product if record is not available
		if (isAvailabilityNotAvailable(entity.getAvailability())) {
			rollbackToQuotationProduct(entity);
		}

		if (!canEditAll || canOnlyEditAvailabilityAndRemark)
			return;

		if (model.getUomId() == null)
			entity.setUom(null);
		else
			entity.setUom(uomDao.findById(model.getUomId()));

		// sub price
		if (entity.getFormDisplay() == 1) {
			cleanSubPricesPostData(model.getSubPrices());

			// check which row is changed
			ArrayList<Integer> oldIds = new ArrayList<Integer>();
			if (entity.getSubPriceRecords() != null) {
				for (SubPriceRecord temp : entity.getSubPriceRecords()) {
					oldIds.add(temp.getId());
				}
			}
			ArrayList<Integer> postIds = new ArrayList<Integer>();
			if (model.getSubPrices() != null) {
				for (SubPriceModel temp : model.getSubPrices()) {
					if (temp.getSubPriceRecordId() != null)
						postIds.add(temp.getSubPriceRecordId());
				}
			}
			Collection<Integer> deletedIds = (Collection<Integer>) CollectionUtils.subtract(oldIds, postIds);
			Collection<Integer> updatedIds = (Collection<Integer>) CollectionUtils.intersection(oldIds, postIds);

			Iterator<SubPriceRecord> iter = entity.getSubPriceRecords().iterator();
			while (iter.hasNext()) {
				SubPriceRecord recordEntity = iter.next();
				if (deletedIds != null && deletedIds.contains(recordEntity.getId().intValue())) {
					if (recordEntity.getSubPriceColumns() != null) {
						for (SubPriceColumn columnEntity : recordEntity.getSubPriceColumns()) {
							subPriceColumnDao.delete(columnEntity);
						}
						recordEntity.getSubPriceColumns().clear();
					}
					subPriceRecordDao.delete(recordEntity);
					iter.remove();
				}
				if (updatedIds != null && updatedIds.contains(recordEntity.getId().intValue())) {
					SubPriceModel m = getSubPriceModelById(model.getSubPrices(), recordEntity.getId());
					BeanUtils.copyProperties(m, recordEntity);

					if (recordEntity.getSubPriceColumns() != null) {
						for (SubPriceColumn columnEntity : recordEntity.getSubPriceColumns()) {
							SubPriceColumnModel cm = getSubPriceColumnModelById(m.getSubPriceColumns(),
									columnEntity.getId());
							BeanUtils.copyProperties(cm, columnEntity);
						}
					}

					for (SubPriceColumnModel cm : m.getSubPriceColumns()) {
						if (cm.getSubPriceColumnId() == null) {
							SubPriceColumn newMissingColumnEntity = new SubPriceColumn();
							newMissingColumnEntity.setSubPriceRecord(recordEntity);
							SubPriceFieldMapping mapping = subPriceFieldMappingDao.getByFieldIdTypeId(
									cm.getSubPriceFieldId(), recordEntity.getSubPriceType().getId());
							newMissingColumnEntity.setSubPriceFieldMapping(mapping);
							BeanUtils.copyProperties(cm, newMissingColumnEntity);
							recordEntity.getSubPriceColumns().add(newMissingColumnEntity);
							subPriceColumnDao.save(newMissingColumnEntity);
						}
					}
				}
			}

			if (model.getSubPrices() != null && model.getSubPrices().size() > 0) {
				int newSubPriceTypeId = model.getSubPrices().get(0).getSubPriceTypeId();

				List<SubPriceFieldMapping> newSubPriceFieldList = subPriceFieldMappingDao
						.getMapByTypeId(newSubPriceTypeId, true);

				for (int i = 0; i < model.getSubPrices().size(); i++) {
					SubPriceModel subPriceModel = model.getSubPrices().get(i);
					if (subPriceModel.getSubPriceRecordId() != null){
						SubPriceRecord record = subPriceRecordDao.findById(subPriceModel.getSubPriceRecordId());
						if (record != null){
							record.setSequence(i);
							subPriceRecordDao.save(record);
						}
						continue;
					}
					
					SubPriceRecord newSubPriceRecordEntity = new SubPriceRecord();
					newSubPriceRecordEntity.setSequence(i);
					SubPriceType subPriceTypeEntity = subPriceTypeDao.findById(subPriceModel.getSubPriceTypeId());
					newSubPriceRecordEntity.setSubPriceType(subPriceTypeEntity);
					if (entity.getSubPriceRecords() == null) {
						entity.setSubPriceRecords(new HashSet<SubPriceRecord>());
					}
					entity.getSubPriceRecords().add(newSubPriceRecordEntity);
					newSubPriceRecordEntity.setQuotationRecord(entity);
					BeanUtils.copyProperties(subPriceModel, newSubPriceRecordEntity);
					newSubPriceRecordEntity.setSubPriceColumns(new HashSet<SubPriceColumn>());

					for (SubPriceFieldMapping fieldMapping : newSubPriceFieldList) {
						SubPriceColumnModel columnModel = null;
						if (subPriceModel.getSubPriceColumns() != null) {
							for (SubPriceColumnModel temp : subPriceModel.getSubPriceColumns()) {
								if (temp.getSubPriceFieldId() == fieldMapping.getSubPriceField().getSubPriceFieldId()) {
									columnModel = temp;
									break;
								}
							}
						}
						SubPriceColumn newColumnEntity = new SubPriceColumn();
						newColumnEntity.setSubPriceRecord(newSubPriceRecordEntity);
						newSubPriceRecordEntity.getSubPriceColumns().add(newColumnEntity);
						newColumnEntity.setSubPriceFieldMapping(fieldMapping);
						if (columnModel != null) {
							BeanUtils.copyProperties(columnModel, newColumnEntity);
						}
						subPriceColumnDao.save(newColumnEntity);
					}
					subPriceRecordDao.save(newSubPriceRecordEntity);
				}
			}
		} else {
			// tour record
			if (model.getTourRecord() != null) {
				TourRecord tourEntity = null;
				if (entity.getTourRecord() == null) {
					tourEntity = new TourRecord();
					tourEntity.setQuotationRecord(entity);
					entity.setTourRecord(tourEntity);
				} else {
					tourEntity = entity.getTourRecord();
				}
				BeanUtils.copyProperties(model.getTourRecord(), tourEntity);
				if (tourEntity.getId() == null)
					tourRecordDao.save(tourEntity);
			} else {
				TourRecord tourEntity = null;
				
				if (entity.getTourRecord() != null) {
					tourEntity = entity.getTourRecord();
					entity.setTourRecord(null);
					tourRecordDao.delete(tourEntity);
				}
			}

			if (entity.getAvailability() == 1 || entity.getAvailability() == 2) {
				boolean checkInputNull =  checkTourPriceNull(entity.getTourRecord());
				if(checkInputNull != true){
					Double price = calculatePriceFromTour(entity.getTourRecord());
					entity.setsPrice(price);
					entity.setnPrice(price);
				}
			}
		}
	}

	/**
	 * Create verification reply
	 */
	public String createVerificationReply(QuotationRecordPostModel model, QuotationRecord entity) {
		StringBuilder builder = new StringBuilder();

		String template = "\n%s: %s => %s";

		// quotation record
		if (entity.getFormDisplay() == 1) {
			if (!doubleEquals(model.getnPrice(), entity.getnPrice())) {
				String content = String.format(template, "nPrice", entity.getnPrice(), model.getnPrice());
				builder.append(content);
			}
			if (!doubleEquals(model.getsPrice(), entity.getsPrice())) {
				String content = String.format(template, "sPrice", entity.getsPrice(), model.getsPrice());
				builder.append(content);
			}
		}
		if (!doubleEquals(model.getFr(), entity.getFr())) {
			String content = String.format(template, "fr", entity.getFr(), model.getFr());
			builder.append(content);
		}
		if (model.isFRPercentage() && !entity.isFRPercentage() || !model.isFRPercentage() && entity.isFRPercentage()) {
			String content = String.format(template, "FR percentage", entity.isFRPercentage() ? "%" : "$",
					model.isFRPercentage() ? "%" : "$");
			builder.append(content);
		}
		if (model.isConsignmentCounter() && !entity.isConsignmentCounter()
				|| !model.isConsignmentCounter() && entity.isConsignmentCounter()) {
			String content = String.format(template, "Consignment Counter", entity.isConsignmentCounter() ? "Y" : "N",
					model.isConsignmentCounter() ? "Y" : "N");
			builder.append(content);
		}
		if (!commonService.stringEquals(model.getConsignmentCounterName(), entity.getConsignmentCounterName())) {
			String content = String.format(template, "Consignment Counter Name", entity.getConsignmentCounterName(),
					model.getConsignmentCounterName());
			builder.append(content);
		}
		if (!commonService.stringEquals(model.getConsignmentCounterRemark(), entity.getConsignmentCounterRemark())) {
			String content = String.format(template, "Consignment Counter Remark", entity.getConsignmentCounterRemark(),
					model.getConsignmentCounterRemark());
			builder.append(content);
		}
		if (!commonService.stringEquals(model.getReason(), entity.getReason())) {
			String content = String.format(template, "Reason", entity.getReason(), model.getReason());
			builder.append(content);
		}
		if (!commonService.stringEquals(model.getDiscount(), entity.getDiscount())) {
			String content = String.format(template, "Discount", entity.getDiscount(), model.getDiscount());
			builder.append(content);
		}
		if (!commonService.stringEquals(model.getDiscountRemark(), entity.getDiscountRemark())) {
			String content = String.format(template, "Discount Remark", entity.getDiscountRemark(),
					model.getDiscountRemark());
			builder.append(content);
		}
		if (!commonService.stringEquals(model.getRemark(), entity.getRemark())) {
			String content = String.format(template, "Price Remark", entity.getRemark(), model.getRemark());
			builder.append(content);
		}
		if (!integerEquals(model.getAvailability(), entity.getAvailability())) {
			String content = String.format(template, "Availability", getAvailabilityText(entity.getAvailability()),
					getAvailabilityText(model.getAvailability()));
			builder.append(content);
		}

		// Uom
		if (!doubleEquals(model.getUomValue(), entity.getUomValue()) || !integerEquals(model.getUomId(), entity.getUom() == null ? null : entity.getUom().getUomId())){
			String entityContent = String.valueOf(entity.getUomValue()) + (entity.getUom() == null ? "" : entity.getUom().getChineseName());
			String modelContent = String.valueOf(model.getUomValue());
			if(model.getUomId() != null && uomDao.findById(model.getUomId())!=null){
				modelContent += uomDao.findById(model.getUomId()).getChineseName();
			}
			String content = String.format(template, "UOM", entityContent, modelContent);
			builder.append(content);
		}
		
		// sub price
		if (entity.getFormDisplay() == 1) {
			if (entity.getSubPriceRecords().size() > 0
					&& (model.getSubPrices() == null || model.getSubPrices().size() == 0)) {
				builder.append("\nAll sub prices deleted");
				String fieldTemplate = "%s=%s,";
				SubPriceType subPriceType = null;
				for (SubPriceRecord recordEntity : entity.getSubPriceRecords()) {
					if (subPriceType == null)
						subPriceType = recordEntity.getSubPriceType();

					builder.append("\n");

					if (!subPriceType.isHideNPrice()) {
						String content = String.format(fieldTemplate, "N Price", recordEntity.getnPrice());
						builder.append(content);
					}
					if (!subPriceType.isHideSPrice()) {
						String content = String.format(fieldTemplate, "S Price", recordEntity.getsPrice());
						builder.append(content);
					}
					if (!subPriceType.isHideDiscount()) {
						String content = String.format(fieldTemplate, "Discount", recordEntity.getDiscount());
						builder.append(content);
					}

					for (SubPriceColumn column : recordEntity.getSubPriceColumns()) {
						String fieldName = column.getSubPriceFieldMapping().getSubPriceField().getFieldName();
						String content = String.format(fieldTemplate, fieldName, column.getColumnValue());
						builder.append(content);
					}
				}
			} else if (entity.getSubPriceRecords().size() == 0 && model.getSubPrices() != null
					&& model.getSubPrices().size() > 0) {
//				builder.append("\nNew sub prices added");
				StringBuilder subPriceBuilder = new StringBuilder();
				String fieldTemplate = "%s=%s,";
				SubPriceType subPriceType = null;
				for (SubPriceModel subPrice : model.getSubPrices()) {
					if (subPriceType == null)
						subPriceType = subPriceTypeDao.findById(subPrice.getSubPriceTypeId());
					
					String tempStr = "";
//					builder.append("\n");

					if (!subPriceType.isHideNPrice()) {
						String content = String.format(fieldTemplate, "N Price", subPrice.getnPrice());
//						builder.append(content);
						tempStr += content;
					}
					if (!subPriceType.isHideSPrice()) {
						String content = String.format(fieldTemplate, "S Price", subPrice.getsPrice());
//						builder.append(content);
						tempStr += content;
					}
					if (!subPriceType.isHideDiscount()) {
						String content = String.format(fieldTemplate, "Discount", subPrice.getDiscount());
//						builder.append(content);
						tempStr += content;
					}

					if (subPrice.getSubPriceColumns() != null && subPrice.getSubPriceColumns().size() > 0){
						for (SubPriceColumnModel column : subPrice.getSubPriceColumns()) {
							SubPriceFieldMapping mapping = subPriceFieldMappingDao
									.getByFieldIdTypeId(column.getSubPriceFieldId(), subPriceType.getId());
							String fieldName = mapping.getSubPriceField().getFieldName();
							String content = String.format(fieldTemplate, fieldName, column.getColumnValue());
//							builder.append(content);
							tempStr += content;
						}
					}
					if (tempStr.length() > 0) {
						subPriceBuilder.append("\n" + tempStr);
					}
					if (subPriceBuilder.length() > 0) {
						builder.append("\nNew sub prices added");
						builder.append(subPriceBuilder.toString());
					}
				}
			} else if (entity.getSubPriceRecords().size() > 0 && model.getSubPrices() != null
					&& model.getSubPrices().size() > 0) {
				StringBuilder subPriceBuilder = new StringBuilder();
				String fieldTemplate = "%s changed from %s to %s,";
				SubPriceType subPriceType = null;
				for (SubPriceModel subPrice : model.getSubPrices()) {
					if (subPriceType == null)
						subPriceType = subPriceTypeDao.findById(subPrice.getSubPriceTypeId());

					String tempStr = "";

					SubPriceRecord subPriceEntity = findSubPriceRecordEntityInSetById(entity.getSubPriceRecords(),
							subPrice.getSubPriceRecordId());
					if (!subPriceType.isHideNPrice()) {
						if (subPriceEntity != null) {
							if (!doubleEquals(subPriceEntity.getnPrice(), subPrice.getnPrice())) {
								String content = String.format(fieldTemplate, "N Price", subPriceEntity.getnPrice(),
										subPrice.getnPrice());
								tempStr += content;
							}
						} else {
							String content = String.format(fieldTemplate, "N Price", 0, subPrice.getnPrice());
							tempStr += content;
						}
					}
					if (!subPriceType.isHideSPrice()) {
						if (subPriceEntity != null) {
							if (!doubleEquals(subPriceEntity.getsPrice(), subPrice.getsPrice())) {
								String content = String.format(fieldTemplate, "S Price", subPriceEntity.getsPrice(),
										subPrice.getsPrice());
								tempStr += content;
							}
						} else {
							String content = String.format(fieldTemplate, "S Price", 0, subPrice.getsPrice());
							tempStr += content;
						}
					}
					if (!subPriceType.isHideDiscount()) {
						if (subPriceEntity != null) {
							if (!commonService.stringEquals(subPriceEntity.getDiscount(), subPrice.getDiscount())) {
								String content = String.format(fieldTemplate, "Discount", subPriceEntity.getDiscount(),
										subPrice.getDiscount());
								tempStr += content;
							}
						} else {
							String content = String.format(fieldTemplate, "Discount", "", subPrice.getsPrice());
							tempStr += content;
						}
					}

					if (subPrice.getSubPriceColumns() !=null && subPrice.getSubPriceColumns().size() >0){
						for (SubPriceColumnModel column : subPrice.getSubPriceColumns()) {
							SubPriceFieldMapping mapping = subPriceFieldMappingDao
									.getByFieldIdTypeId(column.getSubPriceFieldId(), subPriceType.getId());
							String fieldName = mapping.getSubPriceField().getFieldName();
							SubPriceColumn subPriceColumnEntity = null;
							if (subPriceEntity != null) {
								subPriceColumnEntity = findSubPriceColumnEntityInSetById(
										subPriceEntity.getSubPriceColumns(), column.getSubPriceColumnId());
							}
							if (subPriceColumnEntity != null) {
								if (!commonService.stringEquals(subPriceColumnEntity.getColumnValue(),
										column.getColumnValue())) {
									String content = String.format(fieldTemplate, fieldName,
											subPriceColumnEntity.getColumnValue(), column.getColumnValue());
									tempStr += content;
								}
							} else {
								String content = String.format(fieldTemplate, fieldName, "", column.getColumnValue());
								tempStr += content;
							}
						}
					}
					if (tempStr.length() > 0) {
						subPriceBuilder.append("\n" + tempStr);
					}
				}

				if (subPriceBuilder.length() > 0) {
					builder.append("\nSub prices changed");
					builder.append(subPriceBuilder.toString());
				}
			}
		} else {
			// tour
			if (model.getTourRecord() != null) {
				TourRecord tourEntity = entity.getTourRecord();
				if (tourEntity == null) {
					builder.append("\nNew tour record");
					String fieldTemplate = "\n%s=%s";
					if (model.getTourRecord().getDay1Price() != null) {
						String content = String.format(fieldTemplate, "Day 1", model.getTourRecord().getDay1Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay2Price() != null) {
						String content = String.format(fieldTemplate, "Day 2", model.getTourRecord().getDay2Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay3Price() != null) {
						String content = String.format(fieldTemplate, "Day 3", model.getTourRecord().getDay3Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay4Price() != null) {
						String content = String.format(fieldTemplate, "Day 4", model.getTourRecord().getDay4Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay5Price() != null) {
						String content = String.format(fieldTemplate, "Day 5", model.getTourRecord().getDay5Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay6Price() != null) {
						String content = String.format(fieldTemplate, "Day 6", model.getTourRecord().getDay6Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay7Price() != null) {
						String content = String.format(fieldTemplate, "Day 7", model.getTourRecord().getDay7Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay8Price() != null) {
						String content = String.format(fieldTemplate, "Day 8", model.getTourRecord().getDay8Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay9Price() != null) {
						String content = String.format(fieldTemplate, "Day 9", model.getTourRecord().getDay9Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay10Price() != null) {
						String content = String.format(fieldTemplate, "Day 10", model.getTourRecord().getDay10Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay11Price() != null) {
						String content = String.format(fieldTemplate, "Day 11", model.getTourRecord().getDay11Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay12Price() != null) {
						String content = String.format(fieldTemplate, "Day 12", model.getTourRecord().getDay12Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay13Price() != null) {
						String content = String.format(fieldTemplate, "Day 13", model.getTourRecord().getDay13Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay14Price() != null) {
						String content = String.format(fieldTemplate, "Day 14", model.getTourRecord().getDay14Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay15Price() != null) {
						String content = String.format(fieldTemplate, "Day 15", model.getTourRecord().getDay15Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay16Price() != null) {
						String content = String.format(fieldTemplate, "Day 16", model.getTourRecord().getDay16Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay17Price() != null) {
						String content = String.format(fieldTemplate, "Day 17", model.getTourRecord().getDay17Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay18Price() != null) {
						String content = String.format(fieldTemplate, "Day 18", model.getTourRecord().getDay18Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay19Price() != null) {
						String content = String.format(fieldTemplate, "Day 19", model.getTourRecord().getDay19Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay20Price() != null) {
						String content = String.format(fieldTemplate, "Day 20", model.getTourRecord().getDay20Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay21Price() != null) {
						String content = String.format(fieldTemplate, "Day 21", model.getTourRecord().getDay21Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay22Price() != null) {
						String content = String.format(fieldTemplate, "Day 22", model.getTourRecord().getDay22Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay23Price() != null) {
						String content = String.format(fieldTemplate, "Day 23", model.getTourRecord().getDay23Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay24Price() != null) {
						String content = String.format(fieldTemplate, "Day 24", model.getTourRecord().getDay24Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay25Price() != null) {
						String content = String.format(fieldTemplate, "Day 25", model.getTourRecord().getDay25Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay26Price() != null) {
						String content = String.format(fieldTemplate, "Day 26", model.getTourRecord().getDay26Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay27Price() != null) {
						String content = String.format(fieldTemplate, "Day 27", model.getTourRecord().getDay27Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay28Price() != null) {
						String content = String.format(fieldTemplate, "Day 28", model.getTourRecord().getDay28Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay29Price() != null) {
						String content = String.format(fieldTemplate, "Day 29", model.getTourRecord().getDay29Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay30Price() != null) {
						String content = String.format(fieldTemplate, "Day 30", model.getTourRecord().getDay30Price());
						builder.append(content);
					}
					if (model.getTourRecord().getDay31Price() != null) {
						String content = String.format(fieldTemplate, "Day 31", model.getTourRecord().getDay31Price());
						builder.append(content);
					}

					if (StringUtils.isNotBlank(model.getTourRecord().getExtraPrice1Name())) {
						String content = String.format(fieldTemplate, "Extra Price 1 Name",
								model.getTourRecord().getExtraPrice1Name());
						builder.append(content);
					}
					if (model.getTourRecord().getExtraPrice1Value() != null) {
						String content = String.format(fieldTemplate, "Extra Price 1 Value",
								model.getTourRecord().getExtraPrice1Value());
						builder.append(content);
					}
					{
						String content = String.format(fieldTemplate, "Extra Price 1 Included",
								model.getTourRecord().isExtraPrice1Count() ? "Y" : "N");
						builder.append(content);
					}

					if (StringUtils.isNotBlank(model.getTourRecord().getExtraPrice2Name())) {
						String content = String.format(fieldTemplate, "Extra Price 2 Name",
								model.getTourRecord().getExtraPrice2Name());
						builder.append(content);
					}
					if (model.getTourRecord().getExtraPrice2Value() != null) {
						String content = String.format(fieldTemplate, "Extra Price 2 Value",
								model.getTourRecord().getExtraPrice2Value());
						builder.append(content);
					}
					{
						String content = String.format(fieldTemplate, "Extra Price 2 Included",
								model.getTourRecord().isExtraPrice2Count() ? "Y" : "N");
						builder.append(content);
					}

					if (StringUtils.isNotBlank(model.getTourRecord().getExtraPrice3Name())) {
						String content = String.format(fieldTemplate, "Extra Price 3 Name",
								model.getTourRecord().getExtraPrice3Name());
						builder.append(content);
					}
					if (model.getTourRecord().getExtraPrice3Value() != null) {
						String content = String.format(fieldTemplate, "Extra Price 3 Value",
								model.getTourRecord().getExtraPrice3Value());
						builder.append(content);
					}
					{
						String content = String.format(fieldTemplate, "Extra Price 3 Included",
								model.getTourRecord().isExtraPrice3Count() ? "Y" : "N");
						builder.append(content);
					}

					if (StringUtils.isNotBlank(model.getTourRecord().getExtraPrice4Name())) {
						String content = String.format(fieldTemplate, "Extra Price 4 Name",
								model.getTourRecord().getExtraPrice4Name());
						builder.append(content);
					}
					if (model.getTourRecord().getExtraPrice4Value() != null) {
						String content = String.format(fieldTemplate, "Extra Price 4 Value",
								model.getTourRecord().getExtraPrice4Value());
						builder.append(content);
					}
					{
						String content = String.format(fieldTemplate, "Extra Price 4 Included",
								model.getTourRecord().isExtraPrice4Count() ? "Y" : "N");
						builder.append(content);
					}

					if (StringUtils.isNotBlank(model.getTourRecord().getExtraPrice5Name())) {
						String content = String.format(fieldTemplate, "Extra Price 5 Name",
								model.getTourRecord().getExtraPrice5Name());
						builder.append(content);
					}
					if (model.getTourRecord().getExtraPrice5Value() != null) {
						String content = String.format(fieldTemplate, "Extra Price 5 Value",
								model.getTourRecord().getExtraPrice5Value());
						builder.append(content);
					}
					{
						String content = String.format(fieldTemplate, "Extra Price 5 Included",
								model.getTourRecord().isExtraPrice5Count() ? "Y" : "N");
						builder.append(content);
					}
				} else {
					if (!doubleEquals(tourEntity.getDay1Price(), model.getTourRecord().getDay1Price())) {
						String content = String.format(template, "Day 1", tourEntity.getDay1Price(),
								model.getTourRecord().getDay1Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay2Price(), model.getTourRecord().getDay2Price())) {
						String content = String.format(template, "Day 2", tourEntity.getDay2Price(),
								model.getTourRecord().getDay2Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay3Price(), model.getTourRecord().getDay3Price())) {
						String content = String.format(template, "Day 3", tourEntity.getDay3Price(),
								model.getTourRecord().getDay3Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay4Price(), model.getTourRecord().getDay4Price())) {
						String content = String.format(template, "Day 4", tourEntity.getDay4Price(),
								model.getTourRecord().getDay4Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay5Price(), model.getTourRecord().getDay5Price())) {
						String content = String.format(template, "Day 5", tourEntity.getDay5Price(),
								model.getTourRecord().getDay5Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay6Price(), model.getTourRecord().getDay6Price())) {
						String content = String.format(template, "Day 6", tourEntity.getDay6Price(),
								model.getTourRecord().getDay6Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay7Price(), model.getTourRecord().getDay7Price())) {
						String content = String.format(template, "Day 7", tourEntity.getDay7Price(),
								model.getTourRecord().getDay7Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay8Price(), model.getTourRecord().getDay8Price())) {
						String content = String.format(template, "Day 8", tourEntity.getDay8Price(),
								model.getTourRecord().getDay8Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay9Price(), model.getTourRecord().getDay9Price())) {
						String content = String.format(template, "Day 9", tourEntity.getDay9Price(),
								model.getTourRecord().getDay9Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay10Price(), model.getTourRecord().getDay10Price())) {
						String content = String.format(template, "Day 10", tourEntity.getDay10Price(),
								model.getTourRecord().getDay10Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay11Price(), model.getTourRecord().getDay11Price())) {
						String content = String.format(template, "Day 11", tourEntity.getDay11Price(),
								model.getTourRecord().getDay11Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay12Price(), model.getTourRecord().getDay12Price())) {
						String content = String.format(template, "Day 12", tourEntity.getDay12Price(),
								model.getTourRecord().getDay12Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay13Price(), model.getTourRecord().getDay13Price())) {
						String content = String.format(template, "Day 13", tourEntity.getDay13Price(),
								model.getTourRecord().getDay13Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay14Price(), model.getTourRecord().getDay14Price())) {
						String content = String.format(template, "Day 14", tourEntity.getDay14Price(),
								model.getTourRecord().getDay14Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay15Price(), model.getTourRecord().getDay15Price())) {
						String content = String.format(template, "Day 15", tourEntity.getDay15Price(),
								model.getTourRecord().getDay15Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay16Price(), model.getTourRecord().getDay16Price())) {
						String content = String.format(template, "Day 16", tourEntity.getDay16Price(),
								model.getTourRecord().getDay16Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay17Price(), model.getTourRecord().getDay17Price())) {
						String content = String.format(template, "Day 17", tourEntity.getDay17Price(),
								model.getTourRecord().getDay17Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay18Price(), model.getTourRecord().getDay18Price())) {
						String content = String.format(template, "Day 18", tourEntity.getDay18Price(),
								model.getTourRecord().getDay18Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay19Price(), model.getTourRecord().getDay19Price())) {
						String content = String.format(template, "Day 19", tourEntity.getDay19Price(),
								model.getTourRecord().getDay19Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay20Price(), model.getTourRecord().getDay20Price())) {
						String content = String.format(template, "Day 20", tourEntity.getDay20Price(),
								model.getTourRecord().getDay20Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay21Price(), model.getTourRecord().getDay21Price())) {
						String content = String.format(template, "Day 21", tourEntity.getDay21Price(),
								model.getTourRecord().getDay21Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay22Price(), model.getTourRecord().getDay22Price())) {
						String content = String.format(template, "Day 22", tourEntity.getDay22Price(),
								model.getTourRecord().getDay22Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay23Price(), model.getTourRecord().getDay23Price())) {
						String content = String.format(template, "Day 23", tourEntity.getDay23Price(),
								model.getTourRecord().getDay23Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay24Price(), model.getTourRecord().getDay24Price())) {
						String content = String.format(template, "Day 24", tourEntity.getDay24Price(),
								model.getTourRecord().getDay24Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay25Price(), model.getTourRecord().getDay25Price())) {
						String content = String.format(template, "Day 25", tourEntity.getDay25Price(),
								model.getTourRecord().getDay25Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay26Price(), model.getTourRecord().getDay26Price())) {
						String content = String.format(template, "Day 26", tourEntity.getDay26Price(),
								model.getTourRecord().getDay26Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay27Price(), model.getTourRecord().getDay27Price())) {
						String content = String.format(template, "Day 27", tourEntity.getDay27Price(),
								model.getTourRecord().getDay27Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay28Price(), model.getTourRecord().getDay28Price())) {
						String content = String.format(template, "Day 28", tourEntity.getDay28Price(),
								model.getTourRecord().getDay28Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay29Price(), model.getTourRecord().getDay29Price())) {
						String content = String.format(template, "Day 29", tourEntity.getDay29Price(),
								model.getTourRecord().getDay29Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay30Price(), model.getTourRecord().getDay30Price())) {
						String content = String.format(template, "Day 30", tourEntity.getDay30Price(),
								model.getTourRecord().getDay30Price());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getDay31Price(), model.getTourRecord().getDay31Price())) {
						String content = String.format(template, "Day 31", tourEntity.getDay31Price(),
								model.getTourRecord().getDay31Price());
						builder.append(content);
					}

					if (!commonService.stringEquals(tourEntity.getExtraPrice1Name(),
							model.getTourRecord().getExtraPrice1Name())) {
						String content = String.format(template, "Extra Price 1 Name", tourEntity.getExtraPrice1Name(),
								model.getTourRecord().getExtraPrice1Name());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getExtraPrice1Value(), model.getTourRecord().getExtraPrice1Value())) {
						String content = String.format(template, "Extra Price 1 Value",
								tourEntity.getExtraPrice1Value(), model.getTourRecord().getExtraPrice1Value());
						builder.append(content);
					}
					if (tourEntity.isExtraPrice1Count() != model.getTourRecord().isExtraPrice1Count()) {
						String content = String.format(template, "Extra Price 1 Included",
								tourEntity.isExtraPrice1Count() ? "Y" : "N",
								model.getTourRecord().isExtraPrice1Count() ? "Y" : "N");
						builder.append(content);
					}

					if (!commonService.stringEquals(tourEntity.getExtraPrice2Name(),
							model.getTourRecord().getExtraPrice2Name())) {
						String content = String.format(template, "Extra Price 2 Name", tourEntity.getExtraPrice2Name(),
								model.getTourRecord().getExtraPrice2Name());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getExtraPrice2Value(), model.getTourRecord().getExtraPrice2Value())) {
						String content = String.format(template, "Extra Price 2 Value",
								tourEntity.getExtraPrice2Value(), model.getTourRecord().getExtraPrice2Value());
						builder.append(content);
					}
					if (tourEntity.isExtraPrice2Count() != model.getTourRecord().isExtraPrice2Count()) {
						String content = String.format(template, "Extra Price 2 Included",
								tourEntity.isExtraPrice2Count() ? "Y" : "N",
								model.getTourRecord().isExtraPrice2Count() ? "Y" : "N");
						builder.append(content);
					}

					if (!commonService.stringEquals(tourEntity.getExtraPrice3Name(),
							model.getTourRecord().getExtraPrice3Name())) {
						String content = String.format(template, "Extra Price 3 Name", tourEntity.getExtraPrice3Name(),
								model.getTourRecord().getExtraPrice3Name());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getExtraPrice3Value(), model.getTourRecord().getExtraPrice3Value())) {
						String content = String.format(template, "Extra Price 3 Value",
								tourEntity.getExtraPrice3Value(), model.getTourRecord().getExtraPrice3Value());
						builder.append(content);
					}
					if (tourEntity.isExtraPrice3Count() != model.getTourRecord().isExtraPrice3Count()) {
						String content = String.format(template, "Extra Price 3 Included",
								tourEntity.isExtraPrice3Count() ? "Y" : "N",
								model.getTourRecord().isExtraPrice3Count() ? "Y" : "N");
						builder.append(content);
					}

					if (!commonService.stringEquals(tourEntity.getExtraPrice4Name(),
							model.getTourRecord().getExtraPrice4Name())) {
						String content = String.format(template, "Extra Price 4 Name", tourEntity.getExtraPrice4Name(),
								model.getTourRecord().getExtraPrice4Name());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getExtraPrice4Value(), model.getTourRecord().getExtraPrice4Value())) {
						String content = String.format(template, "Extra Price 4 Value",
								tourEntity.getExtraPrice4Value(), model.getTourRecord().getExtraPrice4Value());
						builder.append(content);
					}
					if (tourEntity.isExtraPrice4Count() != model.getTourRecord().isExtraPrice4Count()) {
						String content = String.format(template, "Extra Price 4 Included",
								tourEntity.isExtraPrice4Count() ? "Y" : "N",
								model.getTourRecord().isExtraPrice4Count() ? "Y" : "N");
						builder.append(content);
					}

					if (!commonService.stringEquals(tourEntity.getExtraPrice5Name(),
							model.getTourRecord().getExtraPrice5Name())) {
						String content = String.format(template, "Extra Price 5 Name", tourEntity.getExtraPrice5Name(),
								model.getTourRecord().getExtraPrice5Name());
						builder.append(content);
					}
					if (!doubleEquals(tourEntity.getExtraPrice5Value(), model.getTourRecord().getExtraPrice5Value())) {
						String content = String.format(template, "Extra Price 5 Value",
								tourEntity.getExtraPrice5Value(), model.getTourRecord().getExtraPrice5Value());
						builder.append(content);
					}
					if (tourEntity.isExtraPrice5Count() != model.getTourRecord().isExtraPrice5Count()) {
						String content = String.format(template, "Extra Price 5 Included",
								tourEntity.isExtraPrice5Count() ? "Y" : "N",
								model.getTourRecord().isExtraPrice5Count() ? "Y" : "N");
						builder.append(content);
					}
				}
			}
		}

		return builder.toString();
	}

	/**
	 * Create verification reply for product
	 */
	public String createVerificationReplyForProduct(PagePostModel model, QuotationRecord entity) {
		StringBuilder builder = new StringBuilder();

		String template = "\nProduct %s: %s => %s";

		if (!commonService.stringEquals(entity.getProductRemark(), model.getQuotationRecord().getProductRemark())) {
			String content = String.format(template, "Remark", entity.getProductRemark(),
					model.getQuotationRecord().getProductRemark());
			builder.append(content);
		}

		Product productEntity = entity.getProduct();
		ProductPostModel productModel = model.getProduct();

		if (entity.getProduct() == null)
			return builder.toString();

		if (!commonService.stringEquals(productEntity.getBarcode(), productModel.getBarcode())) {
			String content = String.format(template, "Barcode", productEntity.getBarcode(), productModel.getBarcode());
			builder.append(content);
		}

		if (!commonService.stringEquals(productEntity.getCountryOfOrigin(), productModel.getCountryOfOrigin())) {
			String content = String.format(template, "Country of Origin", productEntity.getCountryOfOrigin(),
					productModel.getCountryOfOrigin());
			builder.append(content);
		}

		for (ProductSpecificationEditModel editSpecModel : model.getProduct().getAttributes()) {
			ProductAttribute attEntity = productService.getProductAttributeById(editSpecModel.getProductAttributeId());
			VwProductFullSpec specEntity = findProductSpecificationInSetById(productEntity.getFullSpecifications(),
					editSpecModel.getProductAttributeId());
			if (specEntity == null) {
				if (StringUtils.isNotBlank(editSpecModel.getValue())) {
					String content = String.format(template, attEntity.getSpecificationName(), "",
							editSpecModel.getValue());
					builder.append(content);
				}
			} else {
				if (!commonService.stringEquals(specEntity.getValue(), editSpecModel.getValue())) {
					String content = String.format(template, attEntity.getSpecificationName(), specEntity.getValue(),
							editSpecModel.getValue());
					builder.append(content);
				}
			}
		}

		return builder.toString();
	}

	/**
	 * Append verification reply
	 */
	public void appendVerificationReply(PagePostModel model, QuotationRecord entity) {
		if (!(entity.getQuotationState() != null && entity.getQuotationState().equals("Verify")))
			return;

		String newVerificationReply = "";

		QuotationRecordPostModel recordModel = null;

		if (entity.isBackNo()) {
			recordModel = model.getBackNoQuotationRecord();
		} else {
			recordModel = model.getQuotationRecord();
			newVerificationReply = createVerificationReplyForProduct(model, entity);
		}

		newVerificationReply += createVerificationReply(recordModel, entity);

		if (StringUtils.isNotBlank(newVerificationReply)) {
			String datetime = commonService.formatDateTime(new Date());
			String title = String.format("[%s]", datetime);
			newVerificationReply = title + newVerificationReply;

			if (recordModel.getVerificationReply() == null)
				recordModel.setVerificationReply(newVerificationReply);
			else
				recordModel.setVerificationReply(recordModel.getVerificationReply() + "\n" + newVerificationReply);
		}
	}

	/**
	 * Append pe check remark
	 */
	public void appendPECheckRemark(PagePostModel model, QuotationRecord entity) {
		String newVerificationReply = "";

		QuotationRecordPostModel recordModel = null;

		if (entity.isBackNo()) {
			recordModel = model.getBackNoQuotationRecord();
		} else {
			recordModel = model.getQuotationRecord();
			newVerificationReply = createVerificationReplyForProduct(model, entity);
		}

		newVerificationReply += createVerificationReply(recordModel, entity);

		if (StringUtils.isNotBlank(newVerificationReply)) {
			String datetime = commonService.formatDateTime(new Date());
			String title = String.format("[%s]", datetime);
			newVerificationReply = title + newVerificationReply;

			if (model.getPeCheckRemark() == null)
				entity.setPeCheckRemark(newVerificationReply);
			else
				entity.setPeCheckRemark(model.getPeCheckRemark() + "\n" + newVerificationReply);
		} else {
			entity.setPeCheckRemark(model.getPeCheckRemark());
		}
	}

	/**
	 * Get availability text
	 */
	public String getAvailabilityText(Integer id) {
		if (id == null)
			return null;
		switch (id) {
		case 1:
			return "Available";
		case 2:
			return "IP";
		case 3:
			return "有價無貨";
		case 4:
			return "無價無貨";
		case 5:
			return "Not Suitable";
		case 6:
			return "回倉";
		case 7:
			return "無團";
		}
		return null;
	}

	/**
	 * Double equals
	 */
	public boolean doubleEquals(Double d1, Double d2) {
		if (d1 == null && d2 == null)
			return true;
		if ((d1 == null && d2 != null) || (d1 != null && d2 == null))
			return false;
		if (d1.doubleValue() != d2.doubleValue())
			return false;
		else
			return true;
	}

	/**
	 * Integer equals
	 */
	public boolean integerEquals(Integer d1, Integer d2) {
		if (d1 == null && d2 == null)
			return true;
		if ((d1 == null && d2 != null) || (d1 != null && d2 == null))
			return false;
		if (d1.doubleValue() != d2.doubleValue())
			return false;
		else
			return true;
	}

	/**
	 * Prefill view model with post
	 */
	public void prefillViewModelWithPost(PageViewModel viewModel, PagePostModel postModel) throws Exception {
		prefillProductViewModelWithPost(viewModel.getProduct(), postModel.getProduct(), viewModel.getQuotationRecord());

		prefillQuotationRecordViewModelWithPost(viewModel.getQuotationRecord(), postModel.getQuotationRecord());
		if (viewModel.getBackNoQuotationRecord() != null && postModel.getBackNoQuotationRecord() != null) {
			prefillQuotationRecordViewModelWithPost(viewModel.getBackNoQuotationRecord(),
					postModel.getBackNoQuotationRecord());
		}

		viewModel.setDirty(postModel.isDirty());
	}

	/**
	 * Prefill product view model with post
	 */
	public void prefillProductViewModelWithPost(ProductPostModel viewModel, ProductPostModel postModel,
			QuotationRecordViewModel quotationRecordViewModel) {
		Integer originalProductId = viewModel.getProductId();
		if (originalProductId != null) {
			if (viewModel.getProductId().intValue() != postModel.getProductId()) {
				Product postProduct = productDao.findById(postModel.getProductId());
				BeanUtils.copyProperties(postProduct, viewModel);
				List<ProductSpecificationEditModel> attributes = new ArrayList<ProductSpecificationEditModel>();
				List<VwProductFullSpec> specs = productFullSpecDao.GetAllByProductId(postProduct.getId(), 3);
				for (VwProductFullSpec spec : specs) {
					ProductSpecificationEditModel attModel = new ProductSpecificationEditModel();
					BeanUtils.copyProperties(spec, attModel);
					attModel.setName(spec.getSpecificationName());
					attModel.setProductAttributeId(spec.getProductAttribute().getId());
					attributes.add(attModel);
				}
				viewModel.setAttributes(attributes);
			}
		}

		viewModel.setCountryOfOrigin(postModel.getCountryOfOrigin());
		viewModel.setBarcode(postModel.getBarcode());
		for (int i = 0; i < viewModel.getAttributes().size(); i++) {
			viewModel.getAttributes().get(i).setValue(postModel.getAttributes().get(i).getValue());
		}

		if (postModel.getLastPostbackChangeResult() != null)
			viewModel.setLastPostbackChangeResult(postModel.getLastPostbackChangeResult());
		else {
			Product originalEntity = productDao.findById(originalProductId);
			CheckProductChangeResultModel changeResult = checkProductChange(viewModel, originalEntity,
					quotationRecordViewModel.getQuotation().getUnit().getProductCategory());
			viewModel.setLastPostbackChangeResult(changeResult);
		}

		if (viewModel.getLastPostbackChangeResult().isNewProduct()) {
			viewModel.setPhoto1Path(null);
			viewModel.setPhoto2Path(null);
		}
	}

	/**
	 * Prefill quotation record view model with post
	 */
	public void prefillQuotationRecordViewModelWithPost(QuotationRecordViewModel viewModel,
			QuotationRecordPostModel postModel) throws Exception {
		cleanSubPricesPostData(postModel.getSubPrices());
		BeanUtils.copyProperties(postModel, viewModel);

		if (postModel.isProductChange())
			viewModel.setProductChange(true);

		if (!viewModel.isBackNo()) {
			if (StringUtils.isBlank(postModel.getCollectionDate()))
				viewModel.setCollectionDate(null);
			else
				viewModel.setCollectionDate(commonService.getDate(postModel.getCollectionDate()));
		} else {
			if (StringUtils.isBlank(postModel.getReferenceDate()))
				viewModel.setReferenceDate(null);
			else
				viewModel.setReferenceDate(commonService.getDate(postModel.getReferenceDate()));
		}

		if (postModel.getUomId() != null)
			viewModel.setUom(uomDao.findById(postModel.getUomId()));
		else
			viewModel.setUom(null);

		// sub price
		if (viewModel.getSubPrices() != null && viewModel.getSubPrices().size() > 0) {
			viewModel.setSubPriceTypeId(viewModel.getSubPrices().get(0).getSubPriceTypeId());
		}

		if (viewModel.getSubPriceTypeId() != null) {
			String subPriceTypeName = subPriceService.querySubPriceTypeSelectSingle(viewModel.getSubPriceTypeId());
			viewModel.setSubPriceTypeName(subPriceTypeName);
			SubPriceType subPriceTypeDataModel = subPriceService.getSubPriceById(viewModel.getSubPriceTypeId());
			viewModel.setSubPriceTypeDataModel(subPriceTypeDataModel);

			List<SubPriceFieldList> fields = subPriceService.getSubPriceFieldByType(viewModel.getSubPriceTypeId());
			viewModel.setFields(fields);
		}
	}

	/**
	 * Get sub price model by id
	 */
	public SubPriceModel getSubPriceModelById(List<SubPriceModel> list, int id) {
		for (SubPriceModel o : list) {
			if (o.getSubPriceRecordId() == id)
				return o;
		}
		return null;
	}

	/**
	 * Get sub price column model by id
	 */
	public SubPriceColumnModel getSubPriceColumnModelById(List<SubPriceColumnModel> list, int id) {
		for (SubPriceColumnModel o : list) {
			if (o.getSubPriceColumnId() == null)
				continue;
			if (o.getSubPriceColumnId() == id)
				return o;
		}
		return null;
	}

	/**
	 * Clean sub prices post data
	 */
	public void cleanSubPricesPostData(List<SubPriceModel> list) {
		if (list != null) {
			Iterator<SubPriceModel> cleanIterator = list.iterator();
			while (cleanIterator.hasNext()) {
				SubPriceModel temp = cleanIterator.next();
				if (temp.getnPrice() == null || temp.getsPrice() == null) {
					boolean filled = false;
					if (temp.getSubPriceColumns() != null) {
						for (SubPriceColumnModel column : temp.getSubPriceColumns()) {
							if (column.getColumnValue() != null) {
								filled = true;
								break;
							}
						}
					}
					if (!filled)
						cleanIterator.remove();
				}
			}
		}
	}

	/**
	 * Prepare change product view model
	 */
	@SuppressWarnings("unchecked")
	public ChangeProductViewModel prepareChangeProductViewModel(Integer id, Integer productGroupId) {
		ChangeProductViewModel model = new ChangeProductViewModel();

		if (id != null && id > 0) {
			Product entity = productService.getProductById(id);
			BeanUtils.copyProperties(entity, model);
			model.setProductGroupId(entity.getProductGroup().getId());

			model.setHasPhoto1(entity.getPhoto1Path() != null);
			model.setHasPhoto2(entity.getPhoto2Path() != null);

			List<ProductSpecificationEditModel> attributes = productService.getProductSpecificationListByIds(id,
					productGroupId);
			model.setAttributes(attributes);
		} else {
			ProductGroup pg = productGroupDao.findById(productGroupId);

			List<ProductAttribute> orderedList = new ArrayList<ProductAttribute>(pg.getProductAttributes());
			Collections.sort(orderedList, new Comparator<ProductAttribute>() {
				@Override
				public int compare(ProductAttribute a, ProductAttribute b) {
					return a.getSequence().compareTo(b.getSequence());
				}
			});

			List<ProductSpecificationEditModel> attributes = new ArrayList<ProductSpecificationEditModel>();
			for (ProductAttribute att : orderedList) {
				ProductSpecificationEditModel attModel = new ProductSpecificationEditModel();
				BeanUtils.copyProperties(att, attModel);
				attModel.setMandatory(att.getIsMandatory());
				attModel.setName(att.getSpecificationName());
				attModel.setProductAttributeId(att.getId());
				attributes.add(attModel);
			}
			model.setAttributes(attributes);
		}

		String countryOfOriginCSV = productService.getProductCountryList();
		List<String> countryOfOrigins = new ArrayList<String>();
		countryOfOrigins.addAll(Arrays.asList(countryOfOriginCSV.split(";")));
		if (model.getCountryOfOrigin() != null) {
			if (countryOfOrigins.indexOf(model.getCountryOfOrigin()) == -1) {
				countryOfOrigins.add(model.getCountryOfOrigin());
			}
		}
		model.setCountryOfOrigins(countryOfOrigins);

		return model;
	}

	/**
	 * Check if product changed
	 */
	public CheckProductChangeResultModel checkProductChange(ProductPostModel model, Product oldProduct,
			ProductGroup productGroup) {
		CheckProductChangeResultModel resultModel = new CheckProductChangeResultModel();

		if (StringUtils.isBlank(model.getCountryOfOrigin()))
			return resultModel;

		if (oldProduct == null) {
			resultModel.setProductChange(true);
		} else if (!StringUtils.equals(StringUtils.stripToNull(model.getCountryOfOrigin()),
				StringUtils.stripToNull(oldProduct.getCountryOfOrigin()))
				|| !StringUtils.equals(StringUtils.stripToNull(model.getBarcode()),
						StringUtils.stripToNull(oldProduct.getBarcode()))) {
			resultModel.setProductChange(true);
		} else if (oldProduct.getProductGroup().getProductAttributes().size() > 0
				&& (model.getAttributes() == null || model.getAttributes().size() == 0)) {
			resultModel.setProductChange(true);
		} else if (model.getAttributes() != null) {
			Integer sameProductId = productSpecificationDao.getProductIdByAttributes(productGroup.getId(),
					model.getCountryOfOrigin(), model.getBarcode(), model.getAttributes(),
					productGroup.getProductAttributes().size(), oldProduct.getId());

			if (sameProductId == null || sameProductId.intValue() != oldProduct.getId()) {
				resultModel.setProductChange(true);
			}
		}

		if (!resultModel.isProductChange())
			return resultModel;

		Integer sameAttributeProductId = productSpecificationDao.getProductIdByAttributes(productGroup.getId(),
				model.getCountryOfOrigin(), model.getBarcode(), model.getAttributes(),
				productGroup.getProductAttributes().size(), null);

		if (sameAttributeProductId == null) {
			resultModel.setNewProduct(true);
			return resultModel;
		} else {
			resultModel.setSameAttributeProductId(sameAttributeProductId);
			return resultModel;
		}
	}

	/**
	 * Get quotation record by id
	 */
	public QuotationRecord getQuotationRecordById(int id) {
		return quotationRecordDao.findById(id);
	}

	/**
	 * Get history dates and record id
	 */
	public List<QuotationRecordHistoryDateModel> getHistoryDatesAndRecordId(QuotationRecord record) {
		// return getHistoryDatesAndRecordId(record.getQuotation(),
		// record.getHistoryDate());
		List<Integer> skipQuotationRecordIds = new ArrayList<Integer>();
		skipQuotationRecordIds.add(record.getQuotationRecordId());
		for (QuotationRecord r : record.getOtherQuotationRecords()) {
			skipQuotationRecordIds.add(r.getQuotationRecordId());

			if (r.isBackTrack() && r.getOtherQuotationRecords() != null) {
				for (QuotationRecord r2 : r.getOtherQuotationRecords()) {
					skipQuotationRecordIds.add(r2.getQuotationRecordId());
				}
			}
		}
		if (record.getHistoryDate() != null) {
			return getHistoryDatesAndRecordId(record.getQuotation(), record.getHistoryDate(), skipQuotationRecordIds);
		} else {
			return getHistoryDatesAndRecordId(record.getQuotation(), new Date(), skipQuotationRecordIds);
		}
	}

	public List<QuotationRecordHistoryDateModel> getHistoryDatesAndRecordId(Quotation quotation, Date historyDate) {
		return getHistoryDatesAndRecordId(quotation, historyDate, null);
	}

	/**
	 * Get history dates and record id
	 */
	public List<QuotationRecordHistoryDateModel> getHistoryDatesAndRecordId(Quotation quotation, Date historyDate,
			List<Integer> skipQuotationRecordIds) {
		List<QuotationRecordHistoryDateModel> list = quotationRecordDao.getHistoryDatesAndRecordId(quotation.getId(),
				historyDate, 4, skipQuotationRecordIds, false);

		for (QuotationRecordHistoryDateModel history : list) {
			QuotationRecord historyEntity = quotationRecordDao.findById(history.getId());
			if (historyEntity.isProductChange())
				history.setDifferentProduct(true);
		}

		Date historyDateStart = DateUtils.truncate(DateUtils.addYears(historyDate, -1), Calendar.MONTH);
		Date historyDateEnd = DateUtils
				.addDays(DateUtils.truncate(DateUtils.addMonths(historyDate, -11), Calendar.MONTH), -1);
		List<QuotationRecordHistoryDateModel> oneYearList = quotationRecordDao.getHistoryDateRangeAndRecordId(
				quotation.getId(), historyDateStart, historyDateEnd, 1, skipQuotationRecordIds, true);
		if (oneYearList.size() > 0) {
			QuotationRecordHistoryDateModel oneYearHistory = oneYearList.get(0);
			QuotationRecord historyEntity = quotationRecordDao.getByIdWithRelated(oneYearHistory.getId());
			if ((historyEntity.getProduct() == null && quotation.getProduct() != null)
					|| (historyEntity.getProduct() != null && quotation.getProduct() == null)) {
				oneYearHistory.setDifferentProduct(true);
			} else if (historyEntity.getProduct() != null && quotation.getProduct() != null) {
				ProductPostModel model = new ProductPostModel();
				model.setCountryOfOrigin(historyEntity.getProduct().getCountryOfOrigin());
				model.setBarcode(historyEntity.getProduct().getBarcode());

				List<ProductSpecificationEditModel> attributes = new ArrayList<ProductSpecificationEditModel>();
				for (VwProductFullSpec spec : historyEntity.getProduct().getFullSpecifications()) {
					ProductSpecificationEditModel att = new ProductSpecificationEditModel();
					att.setAttributeType(spec.getAttributeType());
					att.setMandatory(spec.getIsMandatory());
					att.setName(spec.getSpecificationName());
					att.setOption(spec.getOption());
					att.setProductAttributeId(
							spec.getProductAttribute() != null ? spec.getProductAttribute().getId() : null);
					att.setProductSpecificationId(spec.getProductSpecificationId());
					att.setSequence(spec.getSequence());
					att.setValue(spec.getValue());
					attributes.add(att);
				}
				model.setAttributes(attributes);

				CheckProductChangeResultModel result = checkProductChange(model, quotation.getProduct(),
						quotation.getUnit().getProductCategory());
				if (result.isProductChange())
					oneYearHistory.setDifferentProduct(true);
			}

			boolean alreadyInList = false;
			for (QuotationRecordHistoryDateModel history : list) {
				if (oneYearHistory.getId().intValue() == history.getId()) {
					alreadyInList = true;
					history.setDifferentProduct(oneYearHistory.isDifferentProduct());
					break;
				}
			}
			if (!alreadyInList)
				list.addAll(oneYearList);
		}
		return list;
	}

	/**
	 * Calculate price from tour
	 */
	public double calculatePriceFromTour(TourRecord record) throws Exception {
		double price = 0;
		double totalPrice = 0;
		double priceCount = 0;
		for (int i = 1; i <= 31; i++) {
			Double temp = (Double) TourRecord.class.getDeclaredMethod("getDay" + i + "Price").invoke(record);
			if (temp == null || temp == 0)
				continue;
			totalPrice += temp;
			priceCount++;
		}
		double average = Math.round(totalPrice / priceCount * 100) / 100.0;
		price = average;
		if (!record.isExtraPrice1Count() && record.getExtraPrice1Value() != null) {
			price += record.getExtraPrice1Value();
		}
		if (!record.isExtraPrice2Count() && record.getExtraPrice2Value() != null) {
			price += record.getExtraPrice2Value();
		}
		if (!record.isExtraPrice3Count() && record.getExtraPrice3Value() != null) {
			price += record.getExtraPrice3Value();
		}
		if (!record.isExtraPrice4Count() && record.getExtraPrice4Value() != null) {
			price += record.getExtraPrice4Value();
		}
		if (!record.isExtraPrice5Count() && record.getExtraPrice5Value() != null) {
			price += record.getExtraPrice5Value();
		}
		
		return price;
	}
	
	public boolean checkTourPriceNull(TourRecord record) throws Exception {
		boolean checkNull = true;
		for (int i = 1; i <= 31; i++) {
			Double temp = (Double) TourRecord.class.getDeclaredMethod("getDay" + i + "Price").invoke(record);
			if (temp == null || temp == 0)
				continue;
			checkNull = false;

		}
		if (!record.isExtraPrice1Count() && record.getExtraPrice1Value() != null) {
			checkNull = false;
		}
		if (!record.isExtraPrice2Count() && record.getExtraPrice2Value() != null) {
			checkNull = false;
		}
		if (!record.isExtraPrice3Count() && record.getExtraPrice3Value() != null) {
			checkNull = false;
		}
		if (!record.isExtraPrice4Count() && record.getExtraPrice4Value() != null) {
			checkNull = false;
		}
		if (!record.isExtraPrice5Count() && record.getExtraPrice5Value() != null) {
			checkNull = false;
		}
		
		
		return checkNull;
	}


	/**
	 * Get UomCategories Id By Quotation Record
	 */
	public Integer[] getUomCategoriesIdByQuotationRecord(QuotationRecord record) {
		if (record.getQuotation() != null && record.getQuotation().getUnit() != null
				&& record.getQuotation().getUnit().getUomCategories() != null) {
			List<Integer> ids = new ArrayList<Integer>();

			for (UOMCategory category : record.getQuotation().getUnit().getUomCategories()) {
				ids.add(category.getId());
			}
			return ids.toArray(new Integer[0]);
		}
		return null;
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
		if (entity.getQuotation() != null && entity.getQuotation().getUnit() != null
				&& entity.getQuotation().getUnit().getPointToNotes() != null) {
			list.addAll(entity.getQuotation().getUnit().getPointToNotes());
		}
		if (entity.getOutlet() != null && entity.getOutlet().getPointToNotes() != null) {
			list.addAll(entity.getOutlet().getPointToNotes());
		}

		List<PointToNote> selectAllNotes = pointToNoteDao.getBySelectAll();
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
				if (tempDate.compareTo(targetDate) <= 0) {
					continue;
				}
			}

			stringList.add(temp.getNote());
		}
		result = StringUtils.join(stringList, ",");

		return result;
	}

	/**
	 * onspotvalidation.reminderForPricingCycle
	 */
	public boolean reminderForPricingCycle(QuotationRecord entity) {
		if (entity.getQuotation() == null || entity.getQuotation().getUnit() == null
				|| entity.getQuotation().getUnit().getOnSpotValidation() == null)
			return false;

		if (entity.isBackNo() || entity.isProductChange())
			return false;

		if (entity.getQuotation().getUnit().getProductCycle() == null)
			return false;

		if (!entity.getQuotation().getUnit().getOnSpotValidation().isReminderForPricingCycle())
			return false;

		Date lastProductChangeCollectionDate = entity.getQuotation().getLastProductChangeDate();
		if (lastProductChangeCollectionDate == null)
			return false;

		// http://stackoverflow.com/questions/16558898/get-difference-between-two-dates-in-months-using-java
		// Note that if your dates are 2013-01-31 and 2013-02-01, you get a
		// distance of 1 month this way, which may or may not be what you want.

		Calendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(lastProductChangeCollectionDate);
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(entity.getCollectionDate());

		int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
		int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

		if (diffMonth > entity.getQuotation().getUnit().getProductCycle())
			return true;
		else
			return false;
	}

	/**
	 * Find SubPriceRecord Entity In Set By Id
	 */
	public SubPriceRecord findSubPriceRecordEntityInSetById(Set<SubPriceRecord> subPriceRecords, Integer id) {
		if (id == null)
			return null;
		for (SubPriceRecord entity : subPriceRecords) {
			if (entity.getId().intValue() == id)
				return entity;
		}
		return null;
	}

	/**
	 * Find SubPriceColumn Entity In Set By Id
	 */
	public SubPriceColumn findSubPriceColumnEntityInSetById(Set<SubPriceColumn> subPriceColumns, Integer id) {
		if (id == null)
			return null;
		for (SubPriceColumn entity : subPriceColumns) {
			if (entity.getId().intValue() == id)
				return entity;
		}
		return null;
	}

	/**
	 * Find ProductSpecification In Set By Id
	 */
	public VwProductFullSpec findProductSpecificationInSetById(Set<VwProductFullSpec> productSpecifications,
			Integer id) {
		if (id == null)
			return null;
		for (VwProductFullSpec entity : productSpecifications) {
			if (entity.getProductAttribute().getId().intValue() == id)
				return entity;
		}
		return null;
	}

	/**
	 * Auto fill quotation record when open
	 */
	public void autoFillQuotationRecordWhenOpen(QuotationRecord entity, QuotationRecord historyEntity) {
		if (entity.isBackNo())
			return;

		boolean isEntityChanged = false;

		if (entity.getCategoryRemark() == null || entity.getContactPerson() == null) {
			String targetUnitCategory = entity.getQuotation().getUnit().getUnitCategory();

			String categoryRemark = null;
			String contactPerson = null;
			for (AssignmentUnitCategoryInfo category : entity.getAssignment().getCategoryInfo()) {
				if ((StringUtils.isEmpty(category.getUnitCategory()) && StringUtils.isEmpty(targetUnitCategory))
						|| (category.getUnitCategory() != null
								&& category.getUnitCategory().equals(targetUnitCategory))) {
					categoryRemark = category.getRemark();
					contactPerson = category.getContactPerson();
					break;
				}
			}

			if (StringUtils.isEmpty(entity.getCategoryRemark())) {
				entity.setCategoryRemark(categoryRemark);
			}
			if (StringUtils.isEmpty(entity.getContactPerson())) {
				entity.setContactPerson(contactPerson);
			}

			isEntityChanged = true;
		}

		if (StringUtils.isEmpty(entity.getOutletDiscountRemark())) {
			String outletDiscountRemark = entity.getAssignment().getOutletDiscountRemark();
			entity.setOutletDiscountRemark(outletDiscountRemark);

			isEntityChanged = true;
		}

		if (isEntityChanged) {
			entity.setModifiedDate(entity.getModifiedDate());
			entity.setByPassModifiedDate(true);
			quotationRecordDao.save(entity);
			quotationRecordDao.flush();
		}

		// If there is no history, the quotation record should be treated as
		// change product
		// removed this logic at 2016-06-13
		// if (historyEntity == null) {
		// entity.setProductChange(true);
		// quotationRecordDao.save(entity);
		// quotationRecordDao.flush();
		// }

	}

	/**
	 * Is availability not available
	 */
	public boolean isAvailabilityNotAvailable(Integer availability) {
		if (availability == null)
			return false;
		return !(availability == 1 || availability == 2 || availability == 3);
	}

	/**
	 * Rollback to quotation product
	 */
	public void rollbackToQuotationProduct(QuotationRecord entity) {
		entity.setProductChange(false);
		entity.setNewProduct(false);
		entity.setProduct(entity.getQuotation().getProduct());
	}

	/**
	 * Initialize back no by original record
	 */
	public void initBackNoByOriginal(QuotationRecord original, QuotationRecord backNo) {
		backNo.setBackNo(true);
		backNo.setOriginalQuotationRecord(original);
		backNo.setFormDisplay(original.getFormDisplay());
		backNo.setCollectFR(original.isCollectFR());
		backNo.setQuotation(original.getQuotation());
		backNo.setProduct(original.getProduct());
		backNo.setOutlet(original.getOutlet());
		backNo.setAssignment(original.getAssignment());

		backNo.setReferenceDate(original.getReferenceDate());
		backNo.setAssignedCollectionDate(original.getAssignedCollectionDate());
		backNo.setAssignedStartDate(original.getAssignedStartDate());
		backNo.setAssignedEndDate(original.getAssignedEndDate());
		backNo.setFirmStatus(original.getAssignment().getStatus());
		backNo.setAvailability(1);
		original.getOtherQuotationRecords().add(backNo);
	}

	/**
	 * Update availability by firm status
	 */
	public void updateAvailabilityByFirmStatus(QuotationRecord entity, int firmStatus) {
		if (firmStatus == 2 || firmStatus == 3 || firmStatus == 4 || firmStatus == 5 || firmStatus == 6
				|| firmStatus == 10) {
			entity.setAvailability(5);
			entity.setStatus("Draft");
		} else if (firmStatus == 7 || firmStatus == 8) {
			if (entity.getFormDisplay() == 1)
				entity.setAvailability(4);
			else
				entity.setAvailability(7);
			entity.setStatus("Draft");
		} else if (firmStatus == 9) {
			entity.setAvailability(2);
		} else if (firmStatus == 1) {
			// when “Firm status” back to “Enumerated”, the “Availability” to
			// available => only applied to the status of quotation = "Blank"
			if (entity.getStatus().equals("Blank")) {
				entity.setAvailability(1);
				entity.setStatus("Draft");
			}
		}
		
		if (!(firmStatus == 1 || firmStatus == 9)){
			entity.setReason(null);
			entity.setDiscount(null);
			entity.setsPrice(null);
			entity.setnPrice(null);
			entity.setUomValue(null);
			entity.setUom(null);
			entity.setSPricePeculiar(false);
			entity.setFr(null);
			entity.setFRPercentage(false);
			entity.setConsignmentCounter(false);
			entity.setConsignmentCounterName(null);
			entity.setConsignmentCounterRemark(null);
			Iterator<SubPriceRecord> iter = entity.getSubPriceRecords().iterator();
			while (iter.hasNext()) {
				SubPriceRecord recordEntity = iter.next();
				if (recordEntity.getSubPriceColumns() != null) {
					for (SubPriceColumn columnEntity : recordEntity.getSubPriceColumns()) {
						subPriceColumnDao.delete(columnEntity);
					}
					recordEntity.getSubPriceColumns().clear();
				}
				subPriceRecordDao.delete(recordEntity);
				iter.remove();
			}
		}
	}

	/**
	 * On availability changed delete back no
	 */
	public QuotationRecord onAvailabilityChangedDeleteBackNo(QuotationRecord entity) {
		if (!isAvailabilityNotAvailable(entity.getAvailability()))
			return null;

		QuotationRecord backNo = null;
		for (QuotationRecord record : entity.getOtherQuotationRecords()) {
			if (record.isBackNo()) {
				backNo = record;
				break;
			}
		}
		if (backNo != null) {
			quotationRecordDao.delete(backNo);
		}

		rollbackToQuotationProduct(entity);

		quotationRecordDao.save(entity);

		return backNo;
	}

	/**
	 * Calculate percentage change
	 */
	public Double calculatePercentageChange(int historyQuotationRecordId, int currentQuotationRecordUnitId,
			double historyValue, double newValue, Integer uomId, Double uomValue) {
		QuotationRecord historyEntity = quotationRecordDao.findById(historyQuotationRecordId);
		Unit currentQuotationRecordUnit = unitDao.findById(currentQuotationRecordUnitId);

		double currentPriceUOM = 0;
		double historyPriceUOM = 0;
		if (currentQuotationRecordUnit != null && uomId != null) {
			Uom currentUom = uomDao.findById(uomId);
			currentPriceUOM = commonService.convertUom(currentQuotationRecordUnit, newValue, currentUom, uomValue);
		} else {
			currentPriceUOM = newValue;
		}
		if (historyEntity.getQuotation().getUnit() != null
				&& historyEntity.getQuotation().getUnit().getStandardUOM() != null) {
			historyPriceUOM = commonService.convertUom(historyEntity.getQuotation().getUnit(), historyValue,
					historyEntity.getUom(), historyEntity.getUomValue());
		} else {
			historyPriceUOM = historyValue;
		}

		return Math.round(((currentPriceUOM - historyPriceUOM) / historyPriceUOM * 100) * 100) / 100.0;
	}

	/**
	 * Prepare product spec
	 */
	public ProductPostModel prepareProductSpecModel(int productId) {
		ProductPostModel model = new ProductPostModel();

		Product entity = productDao.findById(productId);

		BeanUtils.copyProperties(entity, model);
		List<ProductSpecificationEditModel> attributes = new ArrayList<ProductSpecificationEditModel>();
		List<VwProductFullSpec> specs = productFullSpecDao.GetAllByProductId(productId, null);
		for (VwProductFullSpec spec : specs) {
			ProductSpecificationEditModel attModel = new ProductSpecificationEditModel();
			BeanUtils.copyProperties(spec, attModel);
			attModel.setName(spec.getSpecificationName());
			attModel.setProductAttributeId(spec.getProductAttribute().getId());
			attributes.add(attModel);
		}
		model.setAttributes(attributes);

		return model;
	}

	/**
	 * Get back no record
	 */
	public QuotationRecord getBackNoRecord(QuotationRecord original) {
		if (original.getOtherQuotationRecords() == null || original.getOtherQuotationRecords().size() == 0)
			return null;

		for (QuotationRecord record : original.getOtherQuotationRecords()) {
			if (record.isBackNo())
				return record;
		}
		return null;
	}

	/**
	 * Data Sync
	 */
	@Transactional
	public QuotationRecordResponseModel syncQuotationRecordData(List<QuotationRecordSyncData> quotationRecords,
			Date lastSyncTime, Boolean dataReturn, Integer[] quotationRecordIds, Integer[] assignmentIds,
			Integer authorityLevel) {
		List<Integer> skipIds = new ArrayList<Integer>();
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> unUpdatedIds = new ArrayList<Integer>();
		List<Integer> allQuotationRecordIds = new ArrayList<Integer>();
		if (quotationRecordIds != null && quotationRecordIds.length > 0) {
			allQuotationRecordIds.addAll(Arrays.asList(quotationRecordIds));
		}
		if (quotationRecords != null && quotationRecords.size() > 0) {
			for (QuotationRecordSyncData quotationRecord : quotationRecords) {
				if ("D".equals(quotationRecord.getLocalDbRecordStatus())) {
					continue;
				}

				QuotationRecord entity = null;
				if (quotationRecord.getQuotationRecordId() == null) {
					if (quotationRecord.isBackNo() && quotationRecord.getOriginalQuotationRecordId() != null){
						entity = quotationRecordDao.getBackNoRecord(quotationRecord.getOriginalQuotationRecordId());
						if (entity == null){
							entity = new QuotationRecord();
						}
					} else {
						entity = new QuotationRecord();
					}
				} else {
					entity = quotationRecordDao.findById(quotationRecord.getQuotationRecordId());
					if (entity==null){
						//Use below log instead of this
						//logger.info(String.format("syncQuotationRecordData QuotationRecord (%s) will be ignore update due to not exist in DB", quotationRecord.getQuotationRecordId()));
						skipIds.add(quotationRecord.getQuotationRecordId());
						continue;
					}
					if (entity != null && entity.getModifiedDate() != null
							&& entity.getModifiedDate().after(quotationRecord.getModifiedDate())) {
						unUpdatedIds.add(entity.getQuotationRecordId());
						table.put(entity.getQuotationRecordId(), quotationRecord.getLocalId());
						try {
							logger.info(String.format("syncQuotationRecordData QuotationRecord (%s) will be ignore update due to server copy (%tF) is newer from mobile copy (%tF)", entity.getQuotationRecordId(), entity.getModifiedDate(), quotationRecord.getModifiedDate()));
						} 
						catch (Exception ex) {
							logger.info(String.format("syncQuotationRecordData QuotationRecord (%s) will be ignore update due to server copy is newer from mobile copy", entity.getQuotationRecordId()));
						}
						continue;
					}

					if (!"IP".equals(entity.getQuotationState()) && entity.getAvailability() != null
							&& entity.getAvailability() != 2) {
						if ("Approved".equals(entity.getStatus()) || "Submitted".equals(entity.getStatus())) { // Check
																												// DB
																												// record
																												// is
																												// not
																												// submitted
																												// or
																												// approved
							int excludedRight = (1 | 2 | 4); // section head,
																// field head,
																// field
																// supervisor
							if ((authorityLevel & excludedRight) == 0) {
								unUpdatedIds.add(entity.getQuotationRecordId());
								table.put(entity.getQuotationRecordId(), quotationRecord.getLocalId());
								logger.info(String.format("syncQuotationRecordData QuotationRecord (%s) will be ignore update due to authorityLevel (%s)", entity.getQuotationRecordId(), authorityLevel));
								continue;
							}
							quotationRecord.setStatus(entity.getStatus());
						}
					}
				}

				try {
					if ("Approved".equals(entity.getStatus()) || "Submitted".equals(entity.getStatus())) {
						if (!("Approved".equals(quotationRecord.getStatus()) || "Submitted".equals(quotationRecord.getStatus()))) {
							logger.info(String.format("syncQuotationRecordData QuotationRecord (%s) status will return from %s to %s", entity.getQuotationRecordId(), entity.getStatus(), quotationRecord.getStatus()));
						}
					}
				}
				catch (Exception ex) {
					logger.warn(String.format("syncQuotationRecordData failed to log on %s", entity.getQuotationRecordId()), ex);
				}
				BeanUtils.copyProperties(quotationRecord, entity);
				if (quotationRecord.getOutletId() != null) {
					Outlet outlet = outletDao.findById(quotationRecord.getOutletId());
					if (outlet != null) {
						entity.setOutlet(outlet);
					}
				}

				if (quotationRecord.getProductId() != null) {
					Product product = productDao.findById(quotationRecord.getProductId());
					if (product != null) {
						entity.setProduct(product);
					}
				}

				if (quotationRecord.getQuotationId() != null) {
					Quotation quotation = quotationDao.findById(quotationRecord.getQuotationId());
					if (quotation != null) {
						entity.setQuotation(quotation);
					}
				}

				if (quotationRecord.getAssignmentId() != null) {
					Assignment assignment = assignmentDao.findById(quotationRecord.getAssignmentId());
					if (assignment != null) {
						entity.setAssignment(assignment);
					}
				}

				if (quotationRecord.getUomId() != null) {
					Uom uom = uomDao.findById(quotationRecord.getUomId());
					if (uom != null) {
						entity.setUom(uom);
					}
				}

				if (quotationRecord.getOriginalQuotationRecordId() != null) {
					QuotationRecord originalQuotationRecord = quotationRecordDao
							.findById(quotationRecord.getOriginalQuotationRecordId());
					if (originalQuotationRecord != null) {
						entity.setOriginalQuotationRecord(originalQuotationRecord);
					}
				}

				if (quotationRecord.getUserId() != null) {
					User user = userDao.findById(quotationRecord.getUserId());
					if (user != null) {
						entity.setUser(user);
					}
				}

				if (quotationRecord.getAllocationBatchId() != null) {
					AllocationBatch allocationBatch = allocationBatchDao
							.findById(quotationRecord.getAllocationBatchId());
					if (allocationBatch != null) {
						entity.setAllocationBatch(allocationBatch);
					}
				}
				if(entity.isBackNo())
					entity.setStatus(null);
				entity.setByPassModifiedDate(true);
				quotationRecordDao.save(entity);
				allQuotationRecordIds.add(entity.getQuotationRecordId());
				table.put(entity.getQuotationRecordId(), quotationRecord.getLocalId());
				updateQuotationRecordByProductChange(entity);
			}
			quotationRecordDao.flush();
		}
//		if (unUpdatedIds != null && !unUpdatedIds.isEmpty()) {
//			logger.warn(String.format("syncQuotationRecordData No of ignore update QR: %s (%s)", unUpdatedIds.size(), unUpdatedIds.stream().map(i->i.toString()).collect(java.util.stream.Collectors.joining(","))));
//		}
		if (skipIds != null && !skipIds.isEmpty()) {
			logger.info(String.format("syncQuotationRecordData No of skipped QR: %s (%s)", skipIds.size(), skipIds.stream().map(i->i.toString()).collect(java.util.stream.Collectors.joining(","))));
		}

		QuotationRecordResponseModel response = new QuotationRecordResponseModel();
		
		response.setSkipIds(skipIds);
		
		if (dataReturn != null && dataReturn) {
			List<QuotationRecordSyncData> updatedData = new ArrayList<QuotationRecordSyncData>();
			if (allQuotationRecordIds != null && allQuotationRecordIds.size() > 0) {
				updatedData.addAll(syncQuotationRecordByIdRecursiveQuery(allQuotationRecordIds, lastSyncTime));
			}

			if (assignmentIds != null && assignmentIds.length > 0) {
				List<Integer> ids = Arrays.asList(assignmentIds);
				ids = ids.stream().distinct().collect(java.util.stream.Collectors.toList());
				updatedData.addAll(
						syncQuotationRecordByAssignmentRecursiveQuery(ids, lastSyncTime));
			}

			/**
			 * Get UnUpdated QuotationRecord
			 */
			if (unUpdatedIds != null && unUpdatedIds.size() > 0) {
				updatedData.addAll(syncQuotationRecordByIdRecursiveQuery(unUpdatedIds, null));
			}

			List<QuotationRecordSyncData> unique = new ArrayList<QuotationRecordSyncData>(
					new HashSet<QuotationRecordSyncData>(updatedData));

			for (QuotationRecordSyncData data : unique) {
				if (table.containsKey(data.getQuotationRecordId())) {
					data.setLocalId(table.get(data.getQuotationRecordId()));
				}
			}
			
			response.setQuotationRecords(unique);
			
			return response;
		}

		response.setQuotationRecords(new ArrayList<QuotationRecordSyncData>());
		
		return response;
	}

	@Transactional
	public void submitByPE(PagePostModel model, InputStream photo1ImageStream, InputStream photo2ImageStream,
			String fileBaseLoc) throws Exception {
		QuotationRecordPostModel originalQuotationRecordModel = model.getQuotationRecord();

		QuotationRecord entity = quotationRecordDao
				.getByIdWithRelated(originalQuotationRecordModel.getQuotationRecordId());
		if (entity == null)
			throw new SystemException("E00011");

		// appendVerificationReply(model, entity);

		appendPECheckRemark(model, entity);

		if (!isAvailabilityNotAvailable(originalQuotationRecordModel.getAvailability())) {
			submitProduct(model, entity, fileBaseLoc, photo1ImageStream, photo2ImageStream);
		}

		submitQuotationRecord(originalQuotationRecordModel, entity);

		List<String> errorMessages = new ArrayList<String>();

		if (entity.getAvailability() == 1 || entity.getAvailability() == 3) {
			errorMessages.addAll(quotationRecordValidationService.validate(entity, entity));
			errorMessages.addAll(onSpotValidationService.validate(entity));
		}

		quotationRecordDao.save(entity);

		QuotationRecord backNoEntity = null;
		if (entity.getFormDisplay() == 1 && entity.isProductChange()
				&& entity.getQuotation().getUnit().isBackdateRequired()) {
			QuotationRecordPostModel backNoQuotationRecordModel = model.getBackNoQuotationRecord();

			backNoQuotationRecordModel.setCollectionDate(originalQuotationRecordModel.getCollectionDate());

			if (backNoQuotationRecordModel.getQuotationRecordId() != null) {
				backNoEntity = quotationRecordDao.getByIdWithRelated(backNoQuotationRecordModel.getQuotationRecordId());
				if (backNoEntity == null)
					throw new SystemException("E00011");
			} else {
				backNoEntity = new QuotationRecord();
				initBackNoByOriginal(entity, backNoEntity);
			}
			appendVerificationReply(model, backNoEntity);
			submitQuotationRecord(backNoQuotationRecordModel, backNoEntity);
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
				backNoEntity = quotationRecordDao.getByIdWithRelated(backNoQuotationRecordModel.getQuotationRecordId());
				if (backNoEntity != null)
					quotationRecordDao.delete(backNoEntity);

				backNoEntity = null;
			}
		}

		if (errorMessages.size() > 0) {
			ServiceException se = new ServiceException("");
			se.setMessages(errorMessages);
			throw se;
		} else {
			entity.setPassValidation(true);
		}

		quotationRecordDao.save(entity);
		
		quotationRecordDao.flush();
		updateQuotationRecordByProductChange(entity);
	}

	@Transactional
	public List<SubPriceRecordSyncData> syncSubPriceRecordData(List<SubPriceRecordSyncData> subPriceRecords,
			Date lastSyncTime, Boolean dataReturn, Integer[] assignmentIds) {
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> updateIds = new ArrayList<Integer>();

		if (subPriceRecords != null && subPriceRecords.size() > 0) {
			for (SubPriceRecordSyncData subPriceRecord : subPriceRecords) {
				SubPriceRecord entity = null;
				if (subPriceRecord.getSubPriceRecordId() == null) {
					if ("D".equals(subPriceRecord.getLocalDbRecordStatus())) {
						continue;
					}
					entity = new SubPriceRecord();
				} else {
					entity = subPriceRecordDao.findById(subPriceRecord.getSubPriceRecordId());
					if (entity != null && entity.getModifiedDate() != null
							&& entity.getModifiedDate().after(subPriceRecord.getModifiedDate())) {
						updateIds.add(entity.getSubPriceRecordId());
						table.put(entity.getSubPriceRecordId(), subPriceRecord.getLocalId());
						continue;
					}
					if (!"IP".equals(entity.getQuotationRecord().getQuotationState())
							&& entity.getQuotationRecord().getAvailability() != null
							&& entity.getQuotationRecord().getAvailability() != 2) {
						if ("Approved".equals(entity.getQuotationRecord().getStatus())
								|| "Submitted".equals(entity.getQuotationRecord().getStatus())) {
							updateIds.add(entity.getSubPriceRecordId());
							table.put(entity.getSubPriceRecordId(), subPriceRecord.getLocalId());
							continue;
						}
					}
					if (entity != null && "D".equals(subPriceRecord.getLocalDbRecordStatus())) {
						for (SubPriceColumn columnEntity : entity.getSubPriceColumns()) {
							subPriceColumnDao.delete(columnEntity);
						}
						entity.getSubPriceColumns().clear();
						subPriceRecordDao.delete(entity);
						continue;
					} else if ("D".equals(subPriceRecord.getLocalDbRecordStatus())) {
						continue;
					}
				}

				BeanUtils.copyProperties(subPriceRecord, entity);
				if (subPriceRecord.getSubPriceTypeId() != null) {
					SubPriceType subPriceType = subPriceTypeDao.findById(subPriceRecord.getSubPriceTypeId());
					if (subPriceType != null) {
						entity.setSubPriceType(subPriceType);
					}
				}

				if (subPriceRecord.getQuotationRecordId() != null) {
					QuotationRecord quotationRecord = quotationRecordDao
							.findById(subPriceRecord.getQuotationRecordId());
					if (quotationRecord != null) {
						entity.setQuotationRecord(quotationRecord);
					}
				}
				entity.setByPassModifiedDate(true);
				subPriceRecordDao.save(entity);
				updateIds.add(entity.getSubPriceRecordId());
				table.put(entity.getSubPriceRecordId(), subPriceRecord.getLocalId());
			}
			subPriceRecordDao.flush();
		}

		if (dataReturn != null && dataReturn) {
			List<SubPriceRecordSyncData> updatedData = new ArrayList<SubPriceRecordSyncData>();
			if (assignmentIds != null && assignmentIds.length > 0) {
				updatedData.addAll(
						syncSubPriceRecordByAssignmentRecursiveQuery(Arrays.asList(assignmentIds), lastSyncTime));
			}

			if (updateIds != null && updateIds.size() > 0) {
				updatedData.addAll(syncSubPriceRecordByIdRecursiveQuery(updateIds));
			}

			List<SubPriceRecordSyncData> unique = new ArrayList<SubPriceRecordSyncData>(
					new HashSet<SubPriceRecordSyncData>(updatedData));
			for (SubPriceRecordSyncData data : unique) {
				if (table.containsKey(data.getSubPriceRecordId())) {
					data.setLocalId(table.get(data.getSubPriceRecordId()));
				}
			}
			return unique;
		}

		return new ArrayList<SubPriceRecordSyncData>();
	}

	@Transactional
	public List<SubPriceColumnSyncData> syncSubPriceColumnData(List<SubPriceColumnSyncData> subPriceColumns,
			Date lastSyncTime, Boolean dataReturn, Integer[] assignmentIds) {
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> updateIds = new ArrayList<Integer>();

		if (subPriceColumns != null && subPriceColumns.size() > 0) {
			for (SubPriceColumnSyncData subPriceColumn : subPriceColumns) {
				SubPriceColumn entity = null;
				if (subPriceColumn.getSubPriceColumnId() == null) {
					if ("D".equals(subPriceColumn.getLocalDbRecordStatus())) {
						continue;
					}
					entity = new SubPriceColumn();
				} else {
					entity = subPriceColumnDao.findById(subPriceColumn.getSubPriceColumnId());
					if (entity != null && entity.getModifiedDate() != null
							&& entity.getModifiedDate().after(subPriceColumn.getModifiedDate())) {
						updateIds.add(entity.getSubPriceColumnId());
						table.put(entity.getSubPriceColumnId(), subPriceColumn.getLocalId());
						continue;
					}
					if (!"IP".equals(entity.getSubPriceRecord().getQuotationRecord().getQuotationState())
							&& entity.getSubPriceRecord().getQuotationRecord().getAvailability() != null
							&& entity.getSubPriceRecord().getQuotationRecord().getAvailability() != 2) {
						if ("Approved".equals(entity.getSubPriceRecord().getQuotationRecord().getStatus())
								|| "Submitted".equals(entity.getSubPriceRecord().getQuotationRecord().getStatus())) {
							updateIds.add(entity.getSubPriceColumnId());
							table.put(entity.getSubPriceColumnId(), subPriceColumn.getLocalId());
							continue;
						}
					}
					if (entity != null && "D".equals(subPriceColumn.getLocalDbRecordStatus())) {
						subPriceColumnDao.delete(entity);
						continue;
					} else if ("D".equals(subPriceColumn.getLocalDbRecordStatus())) {
						continue;
					}
				}

				BeanUtils.copyProperties(subPriceColumn, entity);
				if (subPriceColumn.getSubPriceRecordId() != null) {
					SubPriceRecord subPriceRecord = subPriceRecordDao.findById(subPriceColumn.getSubPriceRecordId());
					if (subPriceRecord != null) {
						entity.setSubPriceRecord(subPriceRecord);
					}
				}

				if (subPriceColumn.getSubPriceFieldMappingId() != null) {
					SubPriceFieldMapping subPriceFieldMapping = subPriceFieldMappingDao
							.findById(subPriceColumn.getSubPriceFieldMappingId());
					if (subPriceFieldMapping != null) {
						entity.setSubPriceFieldMapping(subPriceFieldMapping);
					}
				}
				entity.setByPassModifiedDate(true);
				subPriceColumnDao.save(entity);

				updateIds.add(entity.getSubPriceColumnId());
				table.put(entity.getSubPriceColumnId(), subPriceColumn.getLocalId());
			}
			subPriceColumnDao.flush();
		}

		if (dataReturn != null && dataReturn) {
			List<SubPriceColumnSyncData> updatedData = new ArrayList<SubPriceColumnSyncData>();

			if (assignmentIds != null && assignmentIds.length > 0) {
				updatedData.addAll(
						syncSubPriceColumnByAssignmentRecursiveQuery(Arrays.asList(assignmentIds), lastSyncTime));
			}

			if (updateIds != null && updateIds.size() > 0) {
				updatedData.addAll(syncSubPriceColumnByIdRecursiveQuery(updateIds));
			}

			List<SubPriceColumnSyncData> unique = new ArrayList<SubPriceColumnSyncData>(
					new HashSet<SubPriceColumnSyncData>(updatedData));

			for (SubPriceColumnSyncData data : unique) {
				if (table.containsKey(data.getSubPriceColumnId())) {
					data.setLocalId(table.get(data.getSubPriceColumnId()));
				}
			}
			return unique;
		}

		return new ArrayList<SubPriceColumnSyncData>();
	}

	@Transactional
	public List<TourRecordSyncData> syncTourRecordData(List<TourRecordSyncData> tourRecords, Date lastSyncTime,
			Boolean dataReturn, Integer[] assignmentIds) {
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		List<Integer> updateIds = new ArrayList<Integer>();

		if (tourRecords != null && tourRecords.size() > 0) {
			for (TourRecordSyncData tourRecord : tourRecords) {
				if (tourRecord.getTourRecordId() == null && !"D".equals(tourRecord.getLocalDbRecordStatus())) {
					throw new RuntimeException("Tour Record Id should not be empty");
				} else if (tourRecord.getTourRecordId() == null && "D".equals(tourRecord.getLocalDbRecordStatus())) {
					continue;
				}

				// QuotationRecord quotationRecord =
				// quotationRecordDao.findById(tourRecord.getTourRecordId());
				// if(quotationRecord==null){
				// throw new RuntimeException("Quotation Record not found: Tour
				// Record Id ="+tourRecord.getTourRecordId());
				// }
				TourRecord entity = tourRecordDao.findById(tourRecord.getTourRecordId());
				QuotationRecord quotationRecord = quotationRecordDao.findById(tourRecord.getTourRecordId());
				if (entity != null && entity.getModifiedDate() != null
						&& entity.getModifiedDate().after(tourRecord.getModifiedDate())) {
					updateIds.add(entity.getTourRecordId());
					table.put(entity.getTourRecordId(), tourRecord.getLocalId());
					continue;
				}
				if (entity == null && !"D".equals(tourRecord.getLocalDbRecordStatus())) {
					if (quotationRecord == null) {
						throw new RuntimeException("Quotation Record and Tour Record not found: Tour Record Id ="
								+ tourRecord.getTourRecordId());
					}
					entity = new TourRecord();
				}
				if (!"IP".equals(quotationRecord.getQuotationState()) && quotationRecord.getAvailability() != null
						&& quotationRecord.getAvailability() != 2) {
					if ("Approved".equals(quotationRecord.getStatus())
							|| "Submitted".equals(quotationRecord.getStatus())) {
						if (entity.getTourRecordId() != null) {
							updateIds.add(entity.getTourRecordId());
							table.put(entity.getTourRecordId(), tourRecord.getLocalId());
						}
						continue;
					}
				}

				if (entity != null && "D".equals(tourRecord.getLocalDbRecordStatus())) {
					tourRecordDao.delete(entity);
					continue;
				} else if ("D".equals(tourRecord.getLocalDbRecordStatus())) {
					continue;
				}

				BeanUtils.copyProperties(tourRecord, entity);

				if (entity.getQuotationRecord() == null) {
					entity.setTourRecordId(null);
					entity.setQuotationRecord(quotationRecord);
				}
				entity.setByPassModifiedDate(true);
				tourRecordDao.save(entity);
				updateIds.add(entity.getTourRecordId());
				table.put(entity.getTourRecordId(), tourRecord.getLocalId());
			}
			tourRecordDao.flush();
		}

		if (dataReturn != null && dataReturn) {
			List<TourRecordSyncData> updatedData = new ArrayList<TourRecordSyncData>();
			if (assignmentIds != null && assignmentIds.length > 0) {
				updatedData
						.addAll(syncTourRecordByAssignmentRecursiveQuery(Arrays.asList(assignmentIds), lastSyncTime));
			}
			if (updateIds != null && updateIds.size() > 0) {
				updatedData.addAll(syncTourRecordByIdRecursiveQuery(updateIds));
			}

			List<TourRecordSyncData> unique = new ArrayList<TourRecordSyncData>(
					new HashSet<TourRecordSyncData>(updatedData));
			for (TourRecordSyncData data : unique) {
				if (table.containsKey(data.getTourRecordId())) {
					data.setLocalId(table.get(data.getTourRecordId()));
				}
			}
			return unique;
		}

		return new ArrayList<TourRecordSyncData>();
	}

	/**
	 * delete quotation record from database
	 */
	@Transactional
	public void deleteQuotationRecordFromDatabase(QuotationRecord record) {
		for (SubPriceRecord temp : record.getSubPriceRecords()) {
			for (SubPriceColumn column : temp.getSubPriceColumns()) {
				subPriceColumnDao.delete(column);
			}
			subPriceRecordDao.delete(temp);
		}
		if (record.getIndoorQuotationRecord() != null) {
			IndoorQuotationRecord idqr = record.getIndoorQuotationRecord();
			idqr.setQuotationRecord(null);
			indoorQuotationRecordDao.save(idqr);
		}
		if (record.getTourRecord() != null) {
			tourRecordDao.delete(record.getTourRecord());
		}

		// if (record.getIndoorVerificationHistories() != null &&
		// record.getIndoorVerificationHistories().size() > 0){
		// for (IndoorVerificationHistory history:
		// record.getIndoorVerificationHistories()){
		// indoorVerificationHistoryDao.delete(history);
		// }
		// }

		List<AssignmentReallocation> reallocationList = assignmentReallocationDao
				.getAllByQuotationRecordId(record.getId());
		for (AssignmentReallocation reallocation : reallocationList) {
			reallocation.getQuotationRecords().remove(record);
			assignmentReallocationDao.save(reallocation);
		}

		if (record.getOtherQuotationRecords() != null && record.getOtherQuotationRecords().size() > 0) {
			for (QuotationRecord other : record.getOtherQuotationRecords()) {
				deleteQuotationRecordFromDatabase(other);
			}
		}

//		quotationRecordDao.deleteQuotationRecordById(record.getQuotationRecordId());
		quotationRecordDao.delete(record);
		quotationRecordDao.flush();
	}
	//2018-01-11 cheung_cheng [B137] When approval the verified the records which have not indoor record, it will fail to update
	//check is the assignment having some without indoor quotation record 
	public boolean isAllowFirmVerify(Integer indoorQuotationRecord){
		List<QuotationRecordSyncData> records = quotationRecordDao.getQuotationRecordsWithoutIndoorQuotationRecordInAssignmentById(indoorQuotationRecord);
		
		if(records != null && records.size() > 0)
			return false;
		else
			return true;
	}

	@Transactional
	public void saveRecord(QuotationRecord qr) {
		this.quotationRecordDao.save(qr);
		this.quotationRecordDao.flush();
	}

	/**
	 * Delete quotation record for RUA
	 */
	@Transactional
	public void deleteQuotationRecordForRUA(Integer surveyMonthId, Integer quotationId) {
		List<QuotationRecord> records = quotationRecordDao.getQuotationRecordIdsForRUADeletion(surveyMonthId,
				quotationId);
		if (records.size() == 0)
			return;
		Set<Integer> assignments = new HashSet<Integer>();
		for (QuotationRecord record : records) {
			assignments.add(record.getAssignment().getAssignmentId());
			deleteQuotationRecordFromDatabase(record);
		}
		
		if(assignments == null || assignments.size()<=0)
			return;

		List<Integer> assignmentIdsToDelete = assignmentDao.getAssignmentIdWithoutQuotationRecord(assignments);
		if (assignmentIdsToDelete.size() == 0)
			return;
		List<Assignment> assignmentsToDelete = assignmentDao.getByIds(assignmentIdsToDelete.toArray(new Integer[0]));
		for (Assignment a : assignmentsToDelete) {
			assignmentMaintenanceService.deleteAssignment(a);
		}
	}
	
	@Transactional
	public void deleteQuotationRecordByIds(List<Integer> ids) {
		List<QuotationRecord> records = recursiveQuery(ids);
		if (records.size() == 0)
			return;
		Set<Integer> assignments = new HashSet<Integer>();
		for (QuotationRecord record : records) {
			assignments.add(record.getAssignment().getAssignmentId());
			deleteQuotationRecordFromDatabase(record);
		}
		
		if(assignments == null || assignments.size()<=0)
			return;

		List<Integer> assignmentIdsToDelete = assignmentDao.getAssignmentIdWithoutQuotationRecord(assignments);
		if (assignmentIdsToDelete.size() == 0)
			return;
		List<Assignment> assignmentsToDelete = assignmentDao.getByIds(assignmentIdsToDelete.toArray(new Integer[0]));
		for (Assignment a : assignmentsToDelete) {
			assignmentMaintenanceService.deleteAssignment(a);
		}
	}

	public List<QuotationRecord> getQuotationRecordsSurveyMonth(Date referenceMonth) {
		return this.quotationRecordDao.getQuotationRecordsByReferenceMonth(referenceMonth);

	}

	/**
	 * Filter season and frequency
	 */
	public List<QuotationRecord> filterSeasonAndFrequency(List<QuotationRecord> quotationRecords, Date month,
			Integer summerStartMonth, Integer summerEndMonth, Integer winterStartMonth, Integer winterEndMonth) {
		List<QuotationRecord> filtered = new ArrayList<QuotationRecord>();

		HashSet<Quotation> distinctQuotations = new HashSet<Quotation>();
		for (QuotationRecord record : quotationRecords) {
			distinctQuotations.add(record.getQuotation());
		}
		List<Quotation> filteredQuotation = quotationService.filterSeasonAndFrequency(
				new ArrayList<Quotation>(distinctQuotations), month, summerStartMonth, summerEndMonth, winterStartMonth,
				winterEndMonth);
		for (QuotationRecord record : quotationRecords) {
			if (filteredQuotation.indexOf(record.getQuotation()) >= 0) {
				filtered.add(record);
			}
		}
		return filtered;
	}
	
	public void resetChangeProduct(int id) {
		QuotationRecord entity = quotationRecordDao.findById(id);
		Quotation quotation = entity.getQuotation();

		QuotationRecord backNo = null;
		for (QuotationRecord quotationRecord : entity.getOtherQuotationRecords()) {
			if (quotationRecord.isBackNo()) {
				backNo = quotationRecord;
				break;
			}
		}
		if (backNo != null) {
			deleteQuotationRecordFromDatabase(backNo);
		}

		entity.setProduct(quotation.getProduct());
		entity.setProductChange(false);
		entity.setNewProduct(false);
		
		quotationRecordDao.save(entity);
		quotationRecordDao.flush();
	}

	public List<QuotationRecordSyncData> getQuotationRecordHistory(Integer[] ids) {
		Set<QuotationRecord> history = new HashSet<QuotationRecord>();
		for (Integer id : ids) {
			QuotationRecord entity = quotationRecordDao.findById(id);
			if (entity == null)
				continue;
			int quotationId = entity.getQuotation().getId();
			Date historyDate = entity.getHistoryDate();
			history.addAll(quotationRecordDao.getByHistoryDatesAndQuotationId(quotationId, historyDate, 4));
			historyDate = DateUtils.addYears(historyDate, -1);
			history.addAll(quotationRecordDao.getByHistoryDatesAndQuotationId(quotationId, historyDate, 1));
		}
		List<Integer> quotationRecordIds = new ArrayList<Integer>();

		for (QuotationRecord quotationRecord : history) {
			quotationRecordIds.add(quotationRecord.getId());
		}

		List<QuotationRecordSyncData> finalQuotationRecord;
		if (quotationRecordIds != null && quotationRecordIds.size() > 0)
			finalQuotationRecord = quotationRecordDao.getUpdatedQuotationRecord(null, quotationRecordIds.toArray(ids),
					null);
		else
			finalQuotationRecord = new ArrayList<QuotationRecordSyncData>();

		return finalQuotationRecord;
	}

	public void validateQutoationRecord(QuotationRecord entity) {
		List<String> errorMessages = new ArrayList<String>();

		if (entity.getAvailability() == 1 || entity.getAvailability() == 3) {
			try {
				errorMessages.addAll(quotationRecordValidationService.validate(entity, entity));
				errorMessages.addAll(onSpotValidationService.validate(entity));
			} catch (Exception e) {
				logger.error("validateQutoationRecord", e);
			}
		}

		if (entity.getFormDisplay() == 1 && entity.isProductChange()
				&& entity.getQuotation().getUnit().isBackdateRequired()) {
			QuotationRecord backNoEntity = null;
			for (QuotationRecord qr : entity.getOtherQuotationRecords()) {
				if (qr.getOriginalQuotationRecord().getQuotationRecordId().intValue() == entity.getQuotationRecordId()
						.intValue() && qr.isBackNo()) {
					backNoEntity = qr;
					break;
				}
			}
			if (backNoEntity != null && (backNoEntity.getAvailability() == 1 || backNoEntity.getAvailability() == 3)) {
				try {
					errorMessages.addAll(quotationRecordValidationService.validate(backNoEntity, entity));
					errorMessages.addAll(onSpotValidationService.validateBackNo(backNoEntity));
				} catch (Exception e) {
					logger.error("validateQutoationRecord", e);
				}
			}
		}

		if (errorMessages.size() > 0) {
			entity.setPassValidation(false);
			entity.setValidationError(StringUtils.join(errorMessages, "\n"));
		} else {
			entity.setPassValidation(true);
			entity.setValidationError(null);
		}
	}

	public List<QuotationRecord> recursiveQuery(List<Integer> quotationRecordIds) {
		List<QuotationRecord> entities = new ArrayList<QuotationRecord>();
		if (quotationRecordIds.size() > 2000) {

			List<Integer> ids = quotationRecordIds.subList(0, 2000);
			entities.addAll(recursiveQuery(ids));

			List<Integer> remainIds = quotationRecordIds.subList(2000, quotationRecordIds.size());
			entities.addAll(recursiveQuery(remainIds));
		} else if (quotationRecordIds.size() > 0) {
			return quotationRecordDao.getByIds(quotationRecordIds.toArray(new Integer[0]));
		}

		return entities;
	}

	public List<QuotationRecordSyncData> syncQuotationRecordByIdRecursiveQuery(List<Integer> quotationRecordIds,
			Date lastSyncTime) {
		List<QuotationRecordSyncData> entities = new ArrayList<QuotationRecordSyncData>();
		if (quotationRecordIds.size() > 2000) {
			List<Integer> ids = quotationRecordIds.subList(0, 2000);
			entities.addAll(syncQuotationRecordByIdRecursiveQuery(ids, lastSyncTime));

			List<Integer> remainIds = quotationRecordIds.subList(2000, quotationRecordIds.size());
			entities.addAll(syncQuotationRecordByIdRecursiveQuery(remainIds, lastSyncTime));
		} else if (quotationRecordIds.size() > 0) {
			return quotationRecordDao.getUpdatedQuotationRecord(lastSyncTime,
					quotationRecordIds.toArray(new Integer[0]), null);
		}
		return entities;
	}

	public List<QuotationRecordSyncData> syncQuotationRecordByAssignmentRecursiveQuery(List<Integer> assignmentIds,
			Date lastSyncTime) {
		List<QuotationRecordSyncData> entities = new ArrayList<QuotationRecordSyncData>();
		if (assignmentIds.size() > 2000) {
			List<Integer> ids = assignmentIds.subList(0, 2000);
			entities.addAll(syncQuotationRecordByAssignmentRecursiveQuery(ids, lastSyncTime));

			List<Integer> remainIds = assignmentIds.subList(2000, assignmentIds.size());
			entities.addAll(syncQuotationRecordByAssignmentRecursiveQuery(remainIds, lastSyncTime));
		} else if (assignmentIds.size() > 0) {
			return quotationRecordDao.getUpdatedQuotationRecord(lastSyncTime, null,
					assignmentIds.toArray(new Integer[0]));
		}
		return entities;
	}

	public List<SubPriceColumnSyncData> syncSubPriceColumnByAssignmentRecursiveQuery(List<Integer> assignmentIds,
			Date lastSyncTime) {
		List<SubPriceColumnSyncData> entities = new ArrayList<SubPriceColumnSyncData>();
		if (assignmentIds.size() > 2000) {
			List<Integer> ids = assignmentIds.subList(0, 2000);
			entities.addAll(syncSubPriceColumnByAssignmentRecursiveQuery(ids, lastSyncTime));

			List<Integer> remainIds = assignmentIds.subList(2000, assignmentIds.size());
			entities.addAll(syncSubPriceColumnByAssignmentRecursiveQuery(remainIds, lastSyncTime));
		} else if (assignmentIds.size() > 0) {
			return subPriceColumnDao.getUpdatedSubPriceColumn(lastSyncTime, assignmentIds.toArray(new Integer[0]), null,
					null);
		}
		return entities;
	}

	public List<SubPriceColumnSyncData> syncSubPriceColumnByIdRecursiveQuery(List<Integer> subPriceColumnIds) {
		List<SubPriceColumnSyncData> entities = new ArrayList<SubPriceColumnSyncData>();
		if (subPriceColumnIds.size() > 2000) {
			List<Integer> ids = subPriceColumnIds.subList(0, 2000);
			entities.addAll(syncSubPriceColumnByIdRecursiveQuery(ids));

			List<Integer> remainIds = subPriceColumnIds.subList(2000, subPriceColumnIds.size());
			entities.addAll(syncSubPriceColumnByIdRecursiveQuery(remainIds));
		} else if (subPriceColumnIds.size() > 0) {
			return subPriceColumnDao.getUpdatedSubPriceColumn(null, null, null,
					subPriceColumnIds.toArray(new Integer[0]));
		}
		return entities;
	}

	public List<SubPriceRecordSyncData> syncSubPriceRecordByAssignmentRecursiveQuery(List<Integer> assignmentIds,
			Date lastSyncTime) {
		List<SubPriceRecordSyncData> entities = new ArrayList<SubPriceRecordSyncData>();
		if (assignmentIds.size() > 2000) {
			List<Integer> ids = assignmentIds.subList(0, 2000);
			entities.addAll(syncSubPriceRecordByAssignmentRecursiveQuery(ids, lastSyncTime));

			List<Integer> remainIds = assignmentIds.subList(2000, assignmentIds.size());
			entities.addAll(syncSubPriceRecordByAssignmentRecursiveQuery(remainIds, lastSyncTime));
		} else if (assignmentIds.size() > 0) {
			return subPriceRecordDao.getUpdatedSubPriceRecord(lastSyncTime, assignmentIds.toArray(new Integer[0]), null,
					null);
		}
		return entities;
	}

	public List<SubPriceRecordSyncData> syncSubPriceRecordByIdRecursiveQuery(List<Integer> subPriceRecordIds) {
		List<SubPriceRecordSyncData> entities = new ArrayList<SubPriceRecordSyncData>();
		if (subPriceRecordIds.size() > 2000) {
			List<Integer> ids = subPriceRecordIds.subList(0, 2000);
			entities.addAll(syncSubPriceRecordByIdRecursiveQuery(ids));

			List<Integer> remainIds = subPriceRecordIds.subList(2000, subPriceRecordIds.size());
			entities.addAll(syncSubPriceRecordByIdRecursiveQuery(remainIds));
		} else if (subPriceRecordIds.size() > 0) {
			return subPriceRecordDao.getUpdatedSubPriceRecord(null, null, null,
					subPriceRecordIds.toArray(new Integer[0]));
		}
		return entities;
	}

	public List<TourRecordSyncData> syncTourRecordByAssignmentRecursiveQuery(List<Integer> assignmentIds,
			Date lastSyncTime) {
		List<TourRecordSyncData> entities = new ArrayList<TourRecordSyncData>();
		if (assignmentIds.size() > 2000) {
			List<Integer> ids = assignmentIds.subList(0, 2000);
			entities.addAll(syncTourRecordByAssignmentRecursiveQuery(ids, lastSyncTime));

			List<Integer> remainIds = assignmentIds.subList(2000, assignmentIds.size());
			entities.addAll(syncTourRecordByAssignmentRecursiveQuery(remainIds, lastSyncTime));
		} else if (assignmentIds.size() > 0) {
			return tourRecordDao.getUpdatedTourRecord(lastSyncTime, assignmentIds.toArray(new Integer[0]), null, null);
		}
		return entities;
	}

	public List<TourRecordSyncData> syncTourRecordByIdRecursiveQuery(List<Integer> tourRecordIds) {
		List<TourRecordSyncData> entities = new ArrayList<TourRecordSyncData>();
		if (tourRecordIds.size() > 2000) {
			List<Integer> ids = tourRecordIds.subList(0, 2000);
			entities.addAll(syncTourRecordByIdRecursiveQuery(ids));

			List<Integer> remainIds = tourRecordIds.subList(2000, tourRecordIds.size());
			entities.addAll(syncTourRecordByIdRecursiveQuery(remainIds));
		} else if (tourRecordIds.size() > 0) {
			return tourRecordDao.getUpdatedTourRecord(null, null, null, tourRecordIds.toArray(new Integer[0]));
		}
		return entities;
	}
	
	public List<Integer> getAllIdsSubmittedAndRUAByAssignments(List<Integer> ids){
		return quotationRecordDao.getAllIdsSubmittedAndRUAByAssignments(ids);
	}
	
	@Transactional
	public void updateQuotationRecordByProductChange(QuotationRecord entity){
		if(!entity.isProductChange())
			return;
		if(entity.isBackNo() || entity.isBackTrack())
			return;
		List<QuotationRecord> qrs = quotationRecordDao.getBlankQuotationRecord(entity.getReferenceDate(), entity.getQuotation().getQuotationId());
		for(QuotationRecord qr : qrs){
			qr.setProduct(entity.getProduct());
			quotationRecordDao.save(qr);
		}
		if ("Submitted".equals(entity.getStatus()) || "Approved".equals(entity.getStatus())){
			entity.getQuotation().setProduct(entity.getProduct());
			quotationDao.save(entity.getQuotation());
			quotationDao.flush();
		}
		quotationRecordDao.flush();
	}
	
	@Transactional
	public void updateQuotationRecordStatusForApproval(List<Integer> quotationRecordIds, String status){
		if(quotationRecordIds.size()>2000){
			List<Integer> ids = quotationRecordIds.subList(0, 2000);
			updateQuotationRecordStatusForApproval(ids, status);
			
			List<Integer> remainIds = quotationRecordIds.subList(2000, quotationRecordIds.size());
			updateQuotationRecordStatusForApproval(remainIds, status);
		} else if (quotationRecordIds.size()>0){
			quotationRecordDao.updateQuotationRecordStatusForApproval(quotationRecordIds, status);
		}
	}
	
	private String getContactPerson(QuotationRecord entity) {
		AssignmentUnitCategoryInfo catInfo = assignmentUnitCategoryInfoDao.findByUnitCategoryAndAssignment(
				entity.getQuotation().getUnit().getUnitCategory(), entity.getAssignment().getAssignmentId());
		
		if (catInfo != null) {
			if (catInfo.getContactPerson() != null) {
				return catInfo.getContactPerson();
			}
		} 
		

		if (entity.getOutlet() != null) {
			if (entity.getOutlet().getLastContact() != null) {
				return entity.getOutlet().getLastContact();
			} 
			
			if (entity.getOutlet().getMainContact() != null) {
				return entity.getOutlet().getMainContact();
			}
		}
		
		
		return null;
	}
	
	public List<Integer> getBackTrackIdByOriginalId(List<Integer> quotationRecordIds){
		List<Integer> ids = new ArrayList<Integer>();
		ids.addAll(quotationRecordIds);
		List<List<Integer>> splitQuotationRecordIds = commonService.splitListByMaxSize(quotationRecordIds);
		for (List<Integer> subIds : splitQuotationRecordIds) {
			List<Integer> backtrackIds = quotationRecordDao.getBackTrackQuotationRecordByQuotationRecordIds(subIds);
			if (backtrackIds != null && backtrackIds.size() > 0) {
				ids.addAll(backtrackIds);
			}
		}
		return ids;
	}
	
	public List<Integer> getCurrentConvertQuotationRecordId(List<Integer> quotationRecordIds){
		List<Integer> ids = new ArrayList<Integer>();
		List<List<Integer>> splitQuotationRecordIds = commonService.splitListByMaxSize(quotationRecordIds);
		for (List<Integer> subIds : splitQuotationRecordIds) {
			List<Integer> returnIds = quotationRecordDao.getCurrentConvertQuotationRecordId(subIds);
			if (returnIds != null && returnIds.size() > 0) {
				ids.addAll(returnIds);
			}
		}
		return ids;
	}
	
	/**
	 * Get availability select2 format
	 */
	public Select2ResponseModel queryFirmStatusSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<String> firmStatus = Arrays.asList(SystemConstant.FIRM_STATUS);
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		queryModel.setRecordsTotal(firmStatus.size());
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (int i=0; i<firmStatus.size(); i++){
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(i));
			item.setText(firmStatus.get(i));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get discount select2 format
	 */
	public Select2ResponseModel queryDiscountSelect2(Select2RequestModel queryModel) {
		queryModel.setRecordsPerPage(10);
		List<String> discounts = quotationRecordDao.searchDistinctDiscountForFilter(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage());
		
		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		queryModel.setRecordsTotal(quotationRecordDao.countDiscountSelect2(queryModel.getTerm()));
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (String discount : discounts){
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(discount);
			item.setText(discount);
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}
	
	/**
	 * Get Quotation select2 format
	 */
	public Select2ResponseModel queryQuotationRecordSelect2(Select2RequestModel queryModel, Integer[] quotationId, Integer[] assignmentId, String referenceMonth) {
		queryModel.setRecordsPerPage(10);
		Date refMonth = null;
		if(referenceMonth != null && referenceMonth != ""){
			try {
				refMonth = commonService.getMonth(referenceMonth);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Integer> entities = quotationRecordDao.searchQuotationRecord(queryModel.getTerm(), queryModel.getFirstRecord(), queryModel.getRecordsPerPage(), quotationId, assignmentId, refMonth);

		Select2ResponseModel responseModel = new Select2ResponseModel();
		
		long recordsTotal = quotationRecordDao.countQuotationRecord(queryModel.getTerm(), quotationId, assignmentId, refMonth);
		queryModel.setRecordsTotal(recordsTotal);
		boolean more = queryModel.hasMore();
		Select2ResponseModel.Pagination pagination = responseModel.new Pagination();
		pagination.setMore(more);
		responseModel.setPagination(pagination);
		
		List<Select2ResponseModel.Select2Item> items = new ArrayList<Select2ResponseModel.Select2Item>();
		for (Integer entity : entities) {
			Select2ResponseModel.Select2Item item = responseModel.new Select2Item();
			item.setId(String.valueOf(entity));
			item.setText(String.valueOf(entity));
			items.add(item);
		}
		responseModel.setResults(items);
		
		return responseModel;
	}

	//public List<CheckAssignmentAndQuotationRecordStatus> getQuotationRecordIds(List<Integer> quotationRecordIds) {
	public List<QuotationRecordSyncData> getQuotationRecordIds(List<Integer> quotationRecordIds) {
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = quotationRecordIds.size() / maxSize;
		int remainder = quotationRecordIds.size() % maxSize;

		List<QuotationRecord> quotationRecords = new ArrayList<QuotationRecord>();
		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();

		// Quotient
		for (int i = 0; i < times; i++) {
			splited = quotationRecordIds.subList(fromIdx, toIdx);
			quotationRecords.addAll(quotationRecordDao.getQuotationRecordStatus(splited));

			if (i < (times - 1)) {
				fromIdx = toIdx;
				toIdx += maxSize;
			}
		}

		// Remainder
		if (times == 0) {
			if (remainder != 0) {
				splited = quotationRecordIds.subList(fromIdx, remainder);
			}
		} else {
			if (remainder != 0) {
				splited = quotationRecordIds.subList(toIdx, (toIdx + remainder));
			}
		}
		if (remainder != 0) {
			quotationRecords.addAll(quotationRecordDao.getQuotationRecordStatus(splited));
		}

		//List<CheckAssignmentAndQuotationRecordStatus> data = new ArrayList<CheckAssignmentAndQuotationRecordStatus>();
		List<QuotationRecordSyncData> data = new ArrayList<QuotationRecordSyncData>();
		
		if (quotationRecords.size() > 0 && !quotationRecords.isEmpty()) {
			//int size = quotationRecords.size();
			//int curr = 0;
			//Integer[] quotationRecordIdList = new Integer[size];
			
			for (QuotationRecord a : quotationRecords) {
				//quotationRecordIdList[curr] = a.getQuotationRecordId();
				QuotationRecordSyncData result = new QuotationRecordSyncData();
				result.setQuotationRecordId(a.getQuotationRecordId());
				result.setProductId(a.getProduct().getProductId());
				data.add(result);
				//curr++;
			}
			//result.setQuotationRecordId(quotationRecordIdList);
			//result = 
			//data.add(result);
		}
		return data;
	}
	
	
	public List<QuotationRecordSyncData> getRUAQuotationRecordIds(List<Integer> assignmentIds) {
		int fromIdx = 0;
		int toIdx = 0;
		int maxSize = 2000;
		int times = assignmentIds.size() / maxSize;
		int remainder = assignmentIds.size() % maxSize;

		List<QuotationRecordSyncData> quotationRecords = new ArrayList<QuotationRecordSyncData>();
		toIdx = maxSize;
		List<Integer> splited = new ArrayList<Integer>();

		// Quotient
		for (int i = 0; i < times; i++) {
			splited = assignmentIds.subList(fromIdx, toIdx);

			List<QuotationRecordSyncData> list = quotationRecordDao.getRUAQuotationRecordStatus(splited);
			
			for(QuotationRecordSyncData d : list){
				quotationRecords.add(d);
			}
			
			list.clear();
			
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
			List<QuotationRecordSyncData> list = quotationRecordDao.getRUAQuotationRecordStatus(splited);
			
			for(QuotationRecordSyncData d : list){
				quotationRecords.add(d);
			}

			list.clear();
			//quotationRecords.addAll(quotationRecordDao.getRUAQuotationRecordStatus(splited));
		}

		return quotationRecords;
		//return data;
	}

}
