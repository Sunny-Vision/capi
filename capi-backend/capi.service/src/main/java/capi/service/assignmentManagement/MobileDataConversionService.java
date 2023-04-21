	package capi.service.assignmentManagement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.AllocationBatchDao;
import capi.dal.AssignmentDao;
import capi.dal.AssignmentUnitCategoryInfoDao;
import capi.dal.DistrictDao;
import capi.dal.ItineraryPlanDao;
import capi.dal.ItineraryPlanOutletDao;
import capi.dal.MajorLocationDao;
import capi.dal.OutletDao;
import capi.dal.PECheckFormDao;
import capi.dal.ProductAttributeDao;
import capi.dal.ProductDao;
import capi.dal.ProductGroupDao;
import capi.dal.ProductSpecificationDao;
import capi.dal.QCItineraryPlanDao;
import capi.dal.QCItineraryPlanItemDao;
import capi.dal.QuotationDao;
import capi.dal.QuotationRecordDao;
import capi.dal.ScSvPlanDao;
import capi.dal.SpotCheckFormDao;
import capi.dal.SpotCheckPhoneCallDao;
import capi.dal.SpotCheckResultDao;
import capi.dal.SubPriceColumnDao;
import capi.dal.SubPriceFieldMappingDao;
import capi.dal.SubPriceRecordDao;
import capi.dal.SubPriceTypeDao;
import capi.dal.SupervisoryVisitDetailDao;
import capi.dal.SupervisoryVisitFormDao;
import capi.dal.SurveyMonthDao;
import capi.dal.TimeLogDao;
import capi.dal.TourRecordDao;
import capi.dal.TpuDao;
import capi.dal.UomDao;
import capi.dal.UserDao;
import capi.entity.AllocationBatch;
import capi.entity.Assignment;
import capi.entity.AssignmentUnitCategoryInfo;
import capi.entity.District;
import capi.entity.Outlet;
import capi.entity.PECheckForm;
import capi.entity.Product;
import capi.entity.ProductAttribute;
import capi.entity.ProductGroup;
import capi.entity.ProductSpecification;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.ScSvPlan;
import capi.entity.SpotCheckForm;
import capi.entity.SpotCheckPhoneCall;
import capi.entity.SpotCheckResult;
import capi.entity.SubPriceColumn;
import capi.entity.SubPriceFieldMapping;
import capi.entity.SubPriceRecord;
import capi.entity.SubPriceType;
import capi.entity.SupervisoryVisitDetail;
import capi.entity.SupervisoryVisitForm;
import capi.entity.SurveyMonth;
import capi.entity.TourRecord;
import capi.entity.Tpu;
import capi.entity.Uom;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.api.dataSync.AssignmentUnitCategoryInfoSyncData;
import capi.model.api.dataSync.FieldworkTimeLogSyncData;
import capi.model.api.dataSync.ItineraryPlanOutletSyncData;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.PECheckFormSyncData;
import capi.model.api.dataSync.ProductSpecificationSyncData;
import capi.model.api.dataSync.QCItineraryPlanItemSyncData;
import capi.model.api.dataSync.SpotCheckPhoneCallSyncData;
import capi.model.api.dataSync.SpotCheckResultSyncData;
import capi.model.api.dataSync.SubPriceColumnSyncData;
import capi.model.api.dataSync.SupervisoryVisitDetailSyncData;
import capi.model.api.dataSync.TelephoneTimeLogSyncData;
import capi.model.api.dataSync.TourRecordSyncData;
import capi.model.api.onlineFunction.AssignmentDataReturn;
import capi.model.api.onlineFunction.AssignmentOnlineModel;
import capi.model.api.onlineFunction.ItineraryPlanOnlineModel;
import capi.model.api.onlineFunction.PECheckFormOnlineModel;
import capi.model.api.onlineFunction.ProductOnlineModel;
import capi.model.api.onlineFunction.QCItineraryPlanOnlineModel;
import capi.model.api.onlineFunction.QuotationRecordOnlineModel;
import capi.model.api.onlineFunction.SpotCheckFormOnlineModel;
import capi.model.api.onlineFunction.SubPriceRecordOnlineModel;
import capi.model.api.onlineFunction.SupervisoryVisitFormOnlineModel;
import capi.model.api.onlineFunction.TimeLogOnlineModel;
import capi.model.timeLogManagement.FieldworkTimeLogModel;
import capi.model.timeLogManagement.TelephoneTimeLogModel;
import capi.model.timeLogManagement.TimeLogModel;
import capi.service.BaseService;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.timeLogManagement.TimeLogService;

@Service("MobileDataConversionService")
public class MobileDataConversionService  extends BaseService {
	
	@Autowired
	private QuotationRecordDao quotationRecordDao;
	
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private UomDao uomDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AllocationBatchDao allocationBatchDao;
	
	@Autowired
	private QuotationDao quotationDao;
	
	@Autowired
	private OutletDao outletDao;
	
	@Autowired
	private TpuDao tpuDao;
	
	@Autowired
	private TourRecordDao tourRecordDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductGroupDao productGroupDao;
	
	@Autowired
	private ProductAttributeDao attributeDao;
	
	@Autowired
	private ProductSpecificationDao specificationDao;
	
	@Autowired
	private SubPriceRecordDao subPriceRecordDao;
	
	@Autowired
	private SubPriceTypeDao subPriceTypeDao;
	
	@Autowired
	private SubPriceColumnDao subPriceColumnDao;
	
	@Autowired
	private SubPriceFieldMappingDao subPriceFieldMappingDao;
	
	@Autowired
	private SupervisoryVisitFormDao supervisoryVisitFormDao;
	
	@Autowired
	private SupervisoryVisitDetailDao supervisoryVisitDetailDao;
	
	@Autowired
	private ScSvPlanDao scSvPlanDao;
	
	@Autowired
	private TimeLogService timeLogService;
	
	@Autowired
	private TimeLogDao timeLogDao;
	
	@Autowired
	private SurveyMonthDao surveyMonthDao;
	
	@Autowired
	private DistrictDao districtDao;
	
	@Autowired
	private AssignmentUnitCategoryInfoDao categoryDao;
	
	@Autowired
	private SpotCheckFormDao spotCheckFormDao;
	
	@Autowired
	private SpotCheckResultDao spotCheckResultDao;
	
	@Autowired
	private SpotCheckPhoneCallDao spotCheckPhoneCallDao;
	
	@Autowired
	private ItineraryPlanDao itineraryPlanDao;
	
	@Autowired
	private MajorLocationDao majorLocationDao;
	
	@Autowired
	private ItineraryPlanOutletDao itineraryPlanOutletDao;
	
	@Autowired
	private QCItineraryPlanDao qcItineraryPlanDao;
	
	@Autowired
	private QCItineraryPlanItemDao qcItineraryPlanItemDao;
	
	@Autowired
	private PECheckFormDao peCheckFormDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private AssignmentMaintenanceService assignmentService;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private NotificationService notifyService;
	
	@Autowired
	private AssignmentApprovalService assignmentApprovalService;
	
	/**
	 * Online Function
	 */
	@Transactional
	public AssignmentDataReturn onlineFunctionQuotationRecord(List<QuotationRecordOnlineModel> models) throws Exception{
		Map<Integer, Outlet> mapOutlet = new Hashtable<Integer, Outlet>();
		Map<Integer, Product> mapProduct = new Hashtable<Integer, Product>();
		Map<Integer, ProductSpecification> mapProductSpec = new Hashtable<Integer, ProductSpecification>();
		List<Integer> skipIds = new ArrayList<Integer>();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		AssignmentDataReturn ret = new AssignmentDataReturn();
		if(models!=null && models.size()>0){
			int i = 0;
			List<QuotationRecord> entities = new ArrayList<QuotationRecord>();
			for(QuotationRecordOnlineModel model : models){
				QuotationRecord entity = saveOtherQuotationRecordModel(model, mapOutlet, mapProduct, null, null, mapProductSpec, skipIds);
				if(i % 20 == 0){
					quotationRecordDao.flushAndClearCache();
				}
				i++;
				
				//*(temporary) disable requested by Liam
				//assignmentService.sendSubmitNotification(detail.getUserId(), 1);		
				
				if(entity != null){
					entities.add(entity);
				}
			}
			quotationRecordDao.flush();
			assignmentApprovalService.runPELogic(entities);
			
		}
		Map<Integer, Integer> outletIdsMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> firmCodeMap = new HashMap<Integer, Integer>();
		for (Integer key : mapOutlet.keySet()){
			outletIdsMap.put(key, mapOutlet.get(key).getId());
			firmCodeMap.put(mapOutlet.get(key).getId(), mapOutlet.get(key).getFirmCode());
		}		
		ret.setOutletIdMap(outletIdsMap);
		ret.setFirmCodeMap(firmCodeMap);
		
		Map<Integer, Integer> productIdsMap = new HashMap<Integer, Integer>();
		for (Integer key : mapProduct.keySet()){
			productIdsMap.put(key, mapProduct.get(key).getId());
		}		
		ret.setProductIdMap(productIdsMap);
		
		Map<Integer, Integer> productSpecIdsMap = new HashMap<Integer, Integer>();
		for (Integer key : mapProductSpec.keySet()){
			productSpecIdsMap.put(key, mapProductSpec.get(key).getId());
		}		
		ret.setProductSpecIdMap(productSpecIdsMap);
		ret.setSkipIds(skipIds);
		
		return ret;
	}
	
	
	@Transactional
	public QuotationRecord saveQuotationRecordModel(QuotationRecordOnlineModel quotationRecord, Assignment assignment
			, boolean isOriginal, List<Integer> skipIds) throws Exception{
		QuotationRecord entity = null;
		try{
			if(quotationRecord.getQuotationRecordId()==null){
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
				if(entity==null){
//					if(quotationRecord.isNewRecruitment()){
//						throw new RuntimeException("New Recruitment Not found");
//					}
//					if(!isOriginal){ //if it is not normal
//						return null;
//					}
					skipIds.add(quotationRecord.getQuotationRecordId());
					return null;
				}
			}
			
			BeanUtils.copyProperties(quotationRecord, entity);
			
			if("IP".equals(entity.getQuotationState()) && entity.getAvailability()!=2){
				entity.setQuotationState("Normal");
			}
			
			if(assignment == null){
				if(quotationRecord.getAssignmentId()!=null){
					Assignment tempAssignment = null;
					tempAssignment = assignmentDao.findById(quotationRecord.getAssignmentId());
					if(tempAssignment!=null){
						entity.setAssignment(tempAssignment);
					}
				}
			} else {
				entity.setAssignment(assignment);
			}
			
			Uom uom = null;
			if(quotationRecord.getUomId()!=null){
				uom = uomDao.findById(quotationRecord.getUomId());
				if(uom!=null){
					entity.setUom(uom);
				}
			}
			
			User user = null;
			if(quotationRecord.getUserId()!=null){
				user = userDao.findById(quotationRecord.getUserId());
				if(user!=null){
					entity.setUser(user);
				}
			}
			
			
			AllocationBatch allocationBatch = null;
			if(quotationRecord.getAllocationBatchId()!=null){
				allocationBatch = allocationBatchDao.findById(quotationRecord.getAllocationBatchId());
				if(allocationBatch!=null){
					entity.setAllocationBatch(allocationBatch);
				}
			}
			
			Quotation quotation = null;
			if(quotationRecord.getQuotationId()!=null){
				quotation = quotationDao.findById(quotationRecord.getQuotationId());
				if(quotation!=null){
					entity.setQuotation(quotation);
				}
			}
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		
		return entity;
	}
	
	@Transactional
	public Outlet saveOutletModel(OutletSyncData outlet) throws Exception{
		Outlet entity = null;
		try{
			if("D".equals(outlet.getLocalDbRecordStatus())){
				return null;
			}
			
			if(outlet.getOutletId()==null){
				entity = new Outlet();
				Integer maxFirmCode = outletDao.getMaxFirmCode();
				if (maxFirmCode == null)
					outlet.setFirmCode(1);
				else
					outlet.setFirmCode(maxFirmCode + 1);
			} else {
				entity = outletDao.findById(outlet.getOutletId());
				if(entity==null){
					return null;
				}
			}
			
			BeanUtils.copyProperties(outlet, entity);
			
			Date openingStartTime = null;
			if(!StringUtils.isEmpty(outlet.getOpeningStartTime())){
				try{
					openingStartTime = commonService.getTime(outlet.getOpeningStartTime());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setOpeningStartTime(openingStartTime);
			
			Date openingEndTime = null;
			if(!StringUtils.isEmpty(outlet.getOpeningEndTime())){
				try{
					openingEndTime = commonService.getTime(outlet.getOpeningEndTime());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setOpeningEndTime(openingEndTime);
			
			Date convenientStartTime = null;
			if(!StringUtils.isEmpty(outlet.getConvenientStartTime())){
				try{
					convenientStartTime = commonService.getTime(outlet.getConvenientStartTime());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setConvenientStartTime(convenientStartTime);
			
			Date convenientEndTime = null;
			if(!StringUtils.isEmpty(outlet.getConvenientEndTime())){
				try{
					convenientEndTime = commonService.getTime(outlet.getConvenientEndTime());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setConvenientEndTime(convenientEndTime);
			
			Date openingStartTime2 = null;
			if(!StringUtils.isEmpty(outlet.getOpeningStartTime2())){
				try{
					openingStartTime2 = commonService.getTime(outlet.getOpeningStartTime2());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setOpeningStartTime2(openingStartTime2);
			
			Date openingEndTime2 = null;
			if(!StringUtils.isEmpty(outlet.getOpeningEndTime2())){
				try{
					openingEndTime2 = commonService.getTime(outlet.getOpeningEndTime2());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setOpeningEndTime2(openingEndTime2);
			
			Date convenientStartTime2 = null;
			if(!StringUtils.isEmpty(outlet.getConvenientStartTime2())){
				try{
					convenientStartTime2 = commonService.getTime(outlet.getConvenientStartTime2());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setConvenientStartTime2(convenientStartTime2);
			
			Date convenientEndTime2 = null;
			if(!StringUtils.isEmpty(outlet.getConvenientEndTime2())){
				try{
					convenientEndTime2 = commonService.getTime(outlet.getConvenientEndTime2());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setConvenientEndTime2(convenientEndTime2);
			
			Tpu tpu = null;
			if(outlet.getTpuId()!=null){
				tpu = tpuDao.findById(outlet.getTpuId());
				if(tpu!=null){
					entity.setTpu(tpu);
				}
			}
			
			outletDao.save(entity);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return entity;
	}
	
	@Transactional
	public Product saveProductModel(ProductOnlineModel product, Map<Integer, ProductSpecification> mapProductSpec) throws Exception{
		Product entity = null;
		try{
			if("D".equals(product.getLocalDbRecordStatus())){
				return null;
			}
			if(product.getProductId()==null){
				entity = new Product();
			} else {
				entity = productDao.findById(product.getProductId());
				if(entity==null){
					return null;
				}
			}
			//if the incoming modified time is null, or incoming modified time is earlier than modified time from db
			//set incoming modified time to modified time from db before copyProperties
			if (product.getPhoto1ModifiedTime() == null && entity.getPhoto1ModifiedTime() != null) {
					product.setPhoto1ModifiedTime(entity.getPhoto1ModifiedTime());						
			} else if (product.getPhoto1ModifiedTime() != null && entity.getPhoto1ModifiedTime() != null) {
				if (product.getPhoto1ModifiedTime().compareTo(entity.getPhoto1ModifiedTime()) == -1) {
					product.setPhoto1ModifiedTime(entity.getPhoto1ModifiedTime());
				}						
			}
			
			if (product.getPhoto2ModifiedTime() == null && entity.getPhoto2ModifiedTime() != null) {
				product.setPhoto2ModifiedTime(entity.getPhoto2ModifiedTime());						
			} else if (product.getPhoto2ModifiedTime() != null && entity.getPhoto2ModifiedTime() != null) {
				if (product.getPhoto2ModifiedTime().compareTo(entity.getPhoto2ModifiedTime()) == -1) {
					product.setPhoto2ModifiedTime(entity.getPhoto2ModifiedTime());
				}						
			}

			BeanUtils.copyProperties(product, entity);
			if(product.getProductGroupId()!=null){
				ProductGroup productGroup = productGroupDao.findById(product.getProductGroupId());
				if(productGroup!=null){
					entity.setProductGroup(productGroup);
				}
			}
			
			/**
			 * For Product Specification
			 */
			List<ProductSpecificationSyncData> specifications = product.getProductSpecifications();
			if(specifications!=null && specifications.size()>0){
				for(ProductSpecificationSyncData specification : specifications){
					if(specification.getLocalId() != null && mapProductSpec.get(specification.getLocalId())!=null){
						continue;
					}
					ProductSpecification spec = saveProductSpecificationModel(specification,entity);
					if (spec != null && specification.getLocalId() != null){
						mapProductSpec.put(specification.getLocalId(), spec);						
					}
				}
			}
			
			productDao.save(entity);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return entity;
	}
	
	public ProductSpecification saveProductSpecificationModel(ProductSpecificationSyncData specification, Product product) throws Exception{
		ProductSpecification entity = null;
		try{
			if("D".equals(specification.getLocalDbRecordStatus())){
				if(specification.getProductSpecificationId()==null){
					return null;
				}
				entity = specificationDao.findById(specification.getProductSpecificationId());
				if(entity!=null){
					specificationDao.delete(entity);
				}
				return null;
			}
			
			if(specification.getProductSpecificationId()==null){
				entity = new ProductSpecification();
			} else {
				entity = specificationDao.findById(specification.getProductSpecificationId());
				if(entity==null){
					return null;
				}
			}
			
			BeanUtils.copyProperties(specification, entity);
			entity.setProduct(product);
			
			ProductAttribute attribute = null;
			if(specification.getProductAttributeId()!=null){
				attribute = attributeDao.findById(specification.getProductAttributeId());
				if(attribute!=null){
					entity.setProductAttribute(attribute);
				}
			}
			
			specificationDao.save(entity);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return entity;
	}
	
	@Transactional
	public void saveSubPriceRecordModel(SubPriceRecordOnlineModel subPriceRecord, QuotationRecord quotationRecord) throws Exception{
		SubPriceRecord entity = null;
		
		try{
			if("D".equals(subPriceRecord.getLocalDbRecordStatus())){
				if(subPriceRecord.getSubPriceRecordId()==null){
					return;
				}
				entity = subPriceRecordDao.findById(subPriceRecord.getSubPriceRecordId());
				
				if(entity!=null){
					for (SubPriceColumn subPriceColumn : entity.getSubPriceColumns()){
						subPriceColumnDao.delete(subPriceColumn);
					}
					entity.getSubPriceColumns().clear();
					subPriceRecordDao.delete(entity);
				}
				return;
			}
			
			if(subPriceRecord.getSubPriceRecordId()==null){
				entity = new SubPriceRecord();
			} else {
				entity = subPriceRecordDao.findById(subPriceRecord.getSubPriceRecordId());
				if(entity==null){
					return;
				}
			}
			
			BeanUtils.copyProperties(subPriceRecord, entity);
			SubPriceType subPriceType = null;
			if(subPriceRecord.getSubPriceTypeId()!=null){
				subPriceType = subPriceTypeDao.findById(subPriceRecord.getSubPriceTypeId());
				if(subPriceType!=null){
					entity.setSubPriceType(subPriceType);
				}
			}
			
			entity.setQuotationRecord(quotationRecord);
			subPriceRecordDao.save(entity);
			
			/**
			 * For Sub Price Column
			 */
			
			List<SubPriceColumnSyncData> columns = subPriceRecord.getSubPriceColumns();
			if(columns!=null && columns.size()>0){
				for(SubPriceColumnSyncData column : columns){
					saveSubPriceColumnModel(column, entity);
				}
			}
			
			subPriceRecordDao.save(entity);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return;
	}
	
	@Transactional
	public void saveSubPriceColumnModel(SubPriceColumnSyncData column, SubPriceRecord subPriceRecord){
		SubPriceColumn entity = null;
		try{
			if("D".equals(column.getLocalDbRecordStatus())){
				if(column.getSubPriceColumnId()==null){
					return;
				}
				entity = subPriceColumnDao.findById(column.getSubPriceColumnId());
				if(entity!=null){
					subPriceColumnDao.delete(entity);
				}
				return;
			}
			
			if(column.getSubPriceColumnId()==null){
				entity = new SubPriceColumn();
			} else {
				entity = subPriceColumnDao.findById(column.getSubPriceColumnId());
				if(entity==null){
					return;
				}
			}
			
			BeanUtils.copyProperties(column, entity);
			SubPriceFieldMapping mapping = null;
			if(column.getSubPriceFieldMappingId()!=null){
				mapping = subPriceFieldMappingDao.findById(column.getSubPriceFieldMappingId());
				if(mapping!=null){
					entity.setSubPriceFieldMapping(mapping);
				}
			}
			entity.setSubPriceRecord(subPriceRecord);
			
			subPriceColumnDao.save(entity);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return ;
	}
	
	@Transactional
	public void saveTourRecordModel(TourRecordSyncData tourRecord, QuotationRecord quotationRecord) throws Exception{
		TourRecord entity = null;
		try{
			if("D".equals(tourRecord.getLocalDbRecordStatus())){
				return;
			}
			
			if(tourRecord.getTourRecordId()==null){
				entity = new TourRecord();
			} else {
				entity = tourRecordDao.findById(tourRecord.getTourRecordId());
				if(entity==null && quotationRecord.getTourRecord()!=null){
					return;
					
				} else if(entity==null){
					if(quotationRecord.getFormDisplay()!=2){
						return;
					}
					entity = new TourRecord();
					tourRecord.setTourRecordId(null);
				}
			}
			
			BeanUtils.copyProperties(tourRecord, entity);
			if(tourRecord.getTourRecordId()==null){
				entity.setQuotationRecord(quotationRecord);
			}
			tourRecordDao.save(entity);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		
		return ;
	}
	
	@Transactional
	public QuotationRecord saveOtherQuotationRecordModel(QuotationRecordOnlineModel quotationRecord
			, Map<Integer, Outlet> mapOutlet
			, Map<Integer, Product> mapProduct
			, QuotationRecord originalQuotationRecord
			, Assignment assignment
			, Map<Integer, ProductSpecification> mapProductSpec
			, List<Integer> skipIds) throws Exception{
		QuotationRecord entity = null;
		
		if("D".equals(quotationRecord.getLocalDbRecordStatus())){
			return null;
		}
		
		entity = saveQuotationRecordModel(quotationRecord, assignment, originalQuotationRecord==null, skipIds);
//		if(originalQuotationRecord!=null && entity==null){  //other not found will return
//			return null;
//		}
		if (entity == null){
			return null;
		}
		
		entity.setStatus("Submitted");
		if(entity.isBackNo())
			entity.setStatus(null);
		/**
		 * For Outlet
		 */
		Outlet outlet = null;
		if(quotationRecord.getOutlet()!=null){
			OutletSyncData outletModel = quotationRecord.getOutlet();
			
			if(outletModel.getLocalId()!=null && mapOutlet.get(outletModel.getLocalId())!=null){
				outlet = mapOutlet.get(outletModel.getLocalId());
				entity.setOutlet(outlet);
			} else {
				outlet = saveOutletModel(quotationRecord.getOutlet());
				if(outlet!=null){
					entity.setOutlet(outlet);
					if(outletModel.getLocalId()!=null){
						mapOutlet.put(quotationRecord.getOutlet().getLocalId(), outlet);
					}
				}
			}	
		}
		
		/**
		 * For Product
		 */
		Product product = null;
		if(quotationRecord.getProduct()!=null){
			ProductOnlineModel productModel = quotationRecord.getProduct();
			
			if(productModel.getLocalId() !=null && mapProduct.get(productModel.getLocalId())!=null){
				product = mapProduct.get(productModel.getLocalId());
				entity.setProduct(product);
			} else {
				product = saveProductModel(productModel, mapProductSpec);
				if(product!=null){
					entity.setProduct(product);
					if(productModel.getLocalId()!=null){
						mapProduct.put(productModel.getLocalId(), product);
					}
				}
			}
		}
		
		if (originalQuotationRecord != null){
			entity.setOriginalQuotationRecord(originalQuotationRecord);
		}
		
		//After Saving QuotationRecord
		
		/**
		 * For SubPriceRecord
		 */
		List<SubPriceRecordOnlineModel> subPriceRecords =  quotationRecord.getSubPriceRecords();
		
		if(subPriceRecords!=null && subPriceRecords.size()>0){
			for(SubPriceRecordOnlineModel subPriceRecord : subPriceRecords){
				saveSubPriceRecordModel(subPriceRecord, entity);
			}
		}
		
		/**
		 * For TourRecord
		 */
		if(quotationRecord.getTourRecord()!=null){
			saveTourRecordModel(quotationRecord.getTourRecord(), entity);
		}

		/**
		 * For Others QuotationRecord
		 */
		
		List<QuotationRecordOnlineModel> otherQuotationRecords = quotationRecord.getOtherQuotationRecords();
		if(otherQuotationRecords!=null && otherQuotationRecords.size()>0){
			for(QuotationRecordOnlineModel otherQuotationRecord : otherQuotationRecords){
				saveOtherQuotationRecordModel(otherQuotationRecord, mapOutlet, mapProduct, entity, assignment, mapProductSpec, skipIds);
			}
		}
		
		quotationRecordDao.save(entity);
		quotationRecordService.updateQuotationRecordByProductChange(entity);
		return entity;
	}
	
	/**
	 * Supervisory Visit
	 */
	@Transactional
	public void submitSupervisoryVisitForm(List<SupervisoryVisitFormOnlineModel> models) throws Exception{
		
		if(models!=null && models.size()>0){
			for(SupervisoryVisitFormOnlineModel model : models){
				saveSupervisoryVisitFormModel(model);
			}
			supervisoryVisitFormDao.flush();
		}
	}
	
	@Transactional
	public void saveSupervisoryVisitFormModel(SupervisoryVisitFormOnlineModel supervisoryVisitForm) throws Exception{
		SupervisoryVisitForm entity = null;
		try{
			if("D".equals(supervisoryVisitForm.getLocalDbRecordStatus())){
					return;
			}
			
			if(supervisoryVisitForm.getSupervisoryVisitFormId()==null){
				throw new RuntimeException("Supervisory Visit Form Id should not be empty");
			}
			entity = supervisoryVisitFormDao.findById(supervisoryVisitForm.getSupervisoryVisitFormId());
			BeanUtils.copyProperties(supervisoryVisitForm, entity);
			
			Date fromTime = null;
			if(!StringUtils.isEmpty(supervisoryVisitForm.getFromTime())){
				fromTime = commonService.getTime(supervisoryVisitForm.getFromTime());
			}
			entity.setFromTime(fromTime);
			
			Date toTime = null;
			if(!StringUtils.isEmpty(supervisoryVisitForm.getToTime())){
				toTime = commonService.getTime(supervisoryVisitForm.getToTime());
			}
			entity.setToTime(toTime);
			
			if(supervisoryVisitForm.getOfficerId()!=null){
				User user = userDao.findById(supervisoryVisitForm.getOfficerId());
				if(user!=null){
					entity.setUser(user);
				}
			}
			
			if(supervisoryVisitForm.getSupervisorId()!=null){
				User supervisor = userDao.findById(supervisoryVisitForm.getSupervisorId());
				if(supervisor!=null){
					entity.setSupervisor(supervisor);
				}
			}
			
			if(supervisoryVisitForm.getSubmitTo()!=null){
				User submitTo = userDao.findById(supervisoryVisitForm.getSubmitTo());
				if(submitTo!=null){
					entity.setSubmitTo(submitTo);
				}
			}
			
			if(supervisoryVisitForm.getScSvPlanId()!=null){
				ScSvPlan scSvPlan = scSvPlanDao.findById(supervisoryVisitForm.getScSvPlanId());
				if(scSvPlan!=null){
					entity.setScSvPlan(scSvPlan);
				}
			}
						
			
			/**
			 * For Supervisory Visit Detail
			 */
			List<SupervisoryVisitDetailSyncData> supervisoryVisitDetails = supervisoryVisitForm.getDetails();
			if(supervisoryVisitDetails!=null && supervisoryVisitDetails.size()>0){
				for(SupervisoryVisitDetailSyncData detail : supervisoryVisitDetails){
					saveSupervisoryVisitDetail(detail, entity);
				}
			}
			
			entity.setStatus("Submitted");
			
			String subject = messageSource.getMessage("N00056", null, Locale.ENGLISH);
			User user = entity.getUser();
			User receiver = entity.getSubmitTo();
			if (user != null){
				String code = user.getStaffCode();
				String name = user.getEnglishName();
				String content = messageSource.getMessage("N00057", new String[]{code,name}, Locale.ENGLISH);
				notifyService.sendNotification(receiver, subject, content, false);
			}
			
			supervisoryVisitFormDao.save(entity);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return;
	}
	
	@Transactional
	public void saveSupervisoryVisitDetail(SupervisoryVisitDetailSyncData detail, SupervisoryVisitForm form) throws Exception{
		SupervisoryVisitDetail entity = null;
		try{
			if("D".equals(detail.getLocalDbRecordStatus())){
				if(detail.getSupervisoryVisitDetailId()==null){
					return;
				}
				entity = supervisoryVisitDetailDao.findById(detail.getSupervisoryVisitDetailId());
				if(entity!=null){
					supervisoryVisitDetailDao.delete(entity);
				}
				return;
			}
			
			if(detail.getSupervisoryVisitDetailId()==null){
				entity = new SupervisoryVisitDetail();
			} else {
				entity = supervisoryVisitDetailDao.findById(detail.getSupervisoryVisitDetailId());
				if(entity==null){
					return;
				}
			}
			BeanUtils.copyProperties(detail, entity);
			
			Assignment assignment = null;
			if(detail.getAssignmentId()!=null){
				assignment = assignmentDao.findById(detail.getAssignmentId());
				if(assignment!=null){
					entity.setAssignment(assignment);
				}
			}
			
			entity.setSupervisoryVisitForm(form);
			
			supervisoryVisitDetailDao.save(entity);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return;
	}
	
	/**
	 * Time Log
	 */
	@Transactional
	public void submitTimeLog(List<TimeLogOnlineModel> models) throws Exception{
		if(models!=null && models.size()>0){
			for(TimeLogOnlineModel model : models){
				saveTimeLog(model);
			}
		}
	}
	
	@Transactional
	public void saveTimeLog(TimeLogOnlineModel timeLog) throws Exception{
		try{
			if("D".equals(timeLog.getLocalDbRecordStatus())){
				return;
			}
			
			TimeLogModel timeLogModel = new TimeLogModel();
			
			BeanUtils.copyProperties(timeLog, timeLogModel);
			
			if (timeLog.getTimeLogId() == null){
				Integer id = timeLogDao.getTimeLogIdByUserIdAndDate(timeLog.getUserId(), DateUtils.truncate(timeLog.getDate(), Calendar.DATE));
				if (id != null){
					timeLogModel.setTimeLogId(id);
				}
			}			
			
			if(timeLog.isClaimOT()){
				timeLogModel.setIsClaimOT(true);
			}
			if(timeLog.isOtherWorkingSession()){
				timeLogModel.setIsOtherWorkingSession(true);
			}
			if(timeLog.isPreApproval()){
				timeLogModel.setIsPreApproval(true);
			}
			if(timeLog.isTrainingAM()){
				timeLogModel.setIsTrainingAM(true);
			}
			if(timeLog.isTrainingPM()){
				timeLogModel.setIsTrainingPM(true);
			}
			if(timeLog.isVLSLAM()){
				timeLogModel.setIsVLSLAM(true);
			}
			if(timeLog.isVLSLPM()){
				timeLogModel.setIsVLSLPM(true);
			}
			if(timeLog.isVoilateItineraryCheck()){
				timeLogModel.setIsVoilateItineraryCheck(true);
			}
			if(timeLog.isPreApproval()){
				timeLogModel.setIsPreApproval(true);
			}
			timeLogModel.setWorkingSessionId(timeLog.getWorkingSessionSettingId());
			timeLogModel.setDate(commonService.formatDate(timeLog.getDate()));
			
			List<FieldworkTimeLogSyncData> fieldworks = timeLog.getFieldworkTimeLogs();
			List<FieldworkTimeLogModel> fieldworkModels = new ArrayList<FieldworkTimeLogModel>();
			if(fieldworks!=null && fieldworks.size()>0){
				for(FieldworkTimeLogSyncData fieldwork : fieldworks){
					FieldworkTimeLogModel tempFieldwork = saveFeildworkTimeLog(fieldwork);
					if(tempFieldwork!=null){
						fieldworkModels.add(tempFieldwork);
					}
				}
			}
			
			timeLogModel.setFieldworkTimeLogs(fieldworkModels);
			
			List<TelephoneTimeLogSyncData> telephones = timeLog.getTelephoneTimeLogs();
			List<TelephoneTimeLogModel> tels = new ArrayList<TelephoneTimeLogModel>();
			if(telephones!=null && telephones.size()>0){
				for(TelephoneTimeLogSyncData telephone : telephones){
					TelephoneTimeLogModel tel = saveTelephoneTimeLog(telephone);
					if(tel!=null){
						tels.add(tel);
					}
				}
			}
			timeLogModel.setTelephoneTimeLogs(tels);
			
			timeLogService.saveTimeLog(timeLogModel);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return;
	}
	
	public FieldworkTimeLogModel saveFeildworkTimeLog(FieldworkTimeLogSyncData fieldwork){
		FieldworkTimeLogModel entity = new FieldworkTimeLogModel();
		if("D".equals(fieldwork.getLocalDbRecordStatus())){
			return null;
		}
		
		BeanUtils.copyProperties(fieldwork, entity);
		
		entity.setReferenceMonth(commonService.formatMonth(fieldwork.getReferenceMonth()));
		if(fieldwork.getRecordType()!=null && fieldwork.getRecordType()==1){
			entity.setMarketQuotationCount(fieldwork.getQuotationCount());
			entity.setMarketTotalQuotation(fieldwork.getTotalQuotation());
		} else if(fieldwork.getRecordType()!=null && fieldwork.getRecordType()==2){
			entity.setNonMarketQuotationCount(fieldwork.getQuotationCount());
			entity.setNonMarketTotalQuotation(fieldwork.getTotalQuotation());
		}
		
		return entity;
		
	}
	
	public TelephoneTimeLogModel saveTelephoneTimeLog(TelephoneTimeLogSyncData tel){
		TelephoneTimeLogModel entity = new TelephoneTimeLogModel();
		if("D".equals(tel.getLocalDbRecordStatus())){
			return null;
		}
		
		BeanUtils.copyProperties(tel, entity);
		
		entity.setReferenceMonth(commonService.formatMonth(tel.getReferenceMonth()));
		if("D".equals(tel.getStatus())){
			entity.setDeletionQuotationCount(tel.getQuotationCount());
			entity.setDeletionTotalQuotation(tel.getTotalQuotation());
		} else if("C".equals(tel.getStatus())){
			entity.setCompletionQuotationCount(tel.getQuotationCount());
			entity.setCompletionTotalQuotation(tel.getTotalQuotation());
		}
		
		return entity;
	}
	
	/**
	 * Assignment
	 */
	@Transactional
	public AssignmentDataReturn onlineFunctionAssignment(List<AssignmentOnlineModel> models) throws Exception{
		Map<Integer, Outlet> mapOutlet = new Hashtable<Integer, Outlet>();
		Map<Integer, Product> mapProduct = new Hashtable<Integer, Product>();
		Map<Integer, ProductSpecification> mapProductSpec = new Hashtable<Integer, ProductSpecification>();
		List<Integer> skipIds = new ArrayList<Integer>();
		AssignmentDataReturn ret = new AssignmentDataReturn();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails) auth.getDetails();
		
		if(models!=null && models.size()>0){
			List<Integer> ids = new ArrayList<Integer>();
			for(AssignmentOnlineModel model : models){
				Assignment assignment = submitAssignment(model, mapOutlet, mapProduct, mapProductSpec, skipIds);
				
				if(assignment!=null){
					ids.add(assignment.getAssignmentId());
				}
				
				Integer size = model.getQuotationRecords() != null ? model.getQuotationRecords().size() : 0;
				//*(temporary) disable requested by Liam
				//assignmentService.sendSubmitNotification(detail.getUserId(), size);		
			}
//			assignmentDao.flush();
			if(ids !=null && ids.size()>0){
				List<QuotationRecord> qrs = new ArrayList<QuotationRecord>();
				for(Integer id : ids){
					Assignment assignment = assignmentDao.findById(id);
					for(QuotationRecord qr : assignment.getQuotationRecords()){
						if(qr.getAvailability() == 5 || qr.isNewRecruitment()){
							if(!qr.isBackNo() && !qr.isBackTrack()){
								qrs.add(qr);
							}
						}
					}
				}
				if(qrs!=null && qrs.size()>0){
					assignmentApprovalService.runPELogic(qrs);
				}
			}
		}
		Map<Integer, Integer> outletIdsMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> firmCodeMap = new HashMap<Integer, Integer>();
		for (Integer key : mapOutlet.keySet()){
			outletIdsMap.put(key, mapOutlet.get(key).getId());
			firmCodeMap.put(mapOutlet.get(key).getId(), mapOutlet.get(key).getFirmCode());
		}		
		ret.setOutletIdMap(outletIdsMap);
		ret.setFirmCodeMap(firmCodeMap);
		
		Map<Integer, Integer> productIdsMap = new HashMap<Integer, Integer>();
		for (Integer key : mapProduct.keySet()){
			productIdsMap.put(key, mapProduct.get(key).getId());
		}		
		ret.setProductIdMap(productIdsMap);
		
		Map<Integer, Integer> productSpecIdsMap = new HashMap<Integer, Integer>();
		for (Integer key : mapProductSpec.keySet()){
			productSpecIdsMap.put(key, mapProductSpec.get(key).getId());
		}		
		ret.setProductSpecIdMap(productSpecIdsMap);
		ret.setSkipIds(skipIds);
		
		return ret;
	}
	
	@Transactional
	public Assignment submitAssignment(AssignmentOnlineModel assignment
			, Map<Integer, Outlet> mapOutlet
			, Map<Integer, Product> mapProduct
			, Map<Integer, ProductSpecification> mapProductSpec
			, List<Integer> skipIds) throws Exception{
		Assignment entity = null;
		entity = saveAssignment(assignment);
		
		if(entity==null){
			return null;
		}
		
		/**
		 * For Outlet
		 */
		Outlet outlet = null;
		if(assignment.getOutlet()!=null){
			OutletSyncData outletModel = assignment.getOutlet();
			
			if(outletModel.getLocalId()!=null && mapOutlet.get(outletModel.getLocalId())!=null){
				outlet = mapOutlet.get(outletModel.getLocalId());
				outletModel.setOutletId(outlet.getOutletId());
				entity.setOutlet(outlet);
			} else {
				outlet = saveOutletModel(assignment.getOutlet());
				if(outlet!=null){
					entity.setOutlet(outlet);
					if(outletModel.getLocalId()!=null){
						mapOutlet.put(outletModel.getLocalId(), outlet);
					}
					outletModel.setOutletId(outlet.getOutletId());
				}
			}	
		}
		
		/**
		 * For AssignmentUnitCategoryInfo
		 */
		List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategorys = assignment.getAssignmentUnitCategoryInfos();
		if(assignmentUnitCategorys!=null && assignmentUnitCategorys.size()>0){
			for(AssignmentUnitCategoryInfoSyncData assignmentUnitCategory : assignmentUnitCategorys){
				saveAssignmentUnitCategoryInfo(assignmentUnitCategory, entity);
			}
		}
		assignmentDao.save(entity);
		assignmentDao.flush();
		
		//B170 - Submit All - Category  Remark
		List<Integer> quotationRecords = new ArrayList<Integer>();
		
		List<QuotationRecordOnlineModel> quotationRecordModels = assignment.getQuotationRecords();
		if(quotationRecordModels!=null && quotationRecordModels.size()>0){
			int i = 0;
			for(QuotationRecordOnlineModel quotationRecord : quotationRecordModels){
				//B170 - When updating category remark in assignment maintenance, it cannot be updated for back no. quotations, while new remarks can be shown in original quotation.
				quotationRecords.add(quotationRecord.getQuotationRecordId());
				saveOtherQuotationRecordModel(quotationRecord, mapOutlet, mapProduct, null, entity, mapProductSpec, skipIds);
				if(i % 20 == 0){
					quotationRecordDao.flushAndClearCache();
				}
				i++;
			}
			quotationRecordDao.flush();
		}
		
		//saveCategoryRemark(assignmentUnitCategorys, quotationRecords);
		// 2020-10-21: disabled saving category remark (see PIR-243 data loss issue root cause, related PIR-178)
//		saveCategoryRemark(assignmentUnitCategorys, assignment.getAssignmentId());
		
		return entity;
		
	}
	
	@Transactional
	public Assignment saveAssignment(AssignmentOnlineModel assignment) throws Exception{
		Assignment entity = null;
		try{
			if("D".equals(assignment.getLocalDbRecordStatus())){
				return null;
			}
			
			if(assignment.getAssignmentId()==null){
				entity = new Assignment();
			} else {
				entity = assignmentDao.findById(assignment.getAssignmentId());
				if (entity==null && assignment.isNewRecruitment()){
					throw new RuntimeException("New Recruitment not found");
				}
			}
			BeanUtils.copyProperties(assignment, entity);
			
			if(assignment.getUserId()!=null){
				User user = userDao.findById(assignment.getUserId());
				if(user != null){
					entity.setUser(user);
				}
			}
			
			if(assignment.getSurveyMonthId()!=null){
				SurveyMonth surveyMonth = surveyMonthDao.findById(assignment.getSurveyMonthId());
				if(surveyMonth!=null){
					entity.setSurveyMonth(surveyMonth);
				}
			}
			
			if(assignment.getAssignedUserId()!=null){
				User assignedUser  = userDao.findById(assignment.getAssignedUserId());
				if(assignedUser==null){
					entity.setAssignedUser(assignedUser);
				}
			}
			
			if(assignment.getAdditionalDistrictId()!=null){
				District district = districtDao.findById(assignment.getAdditionalDistrictId());
				if(district==null){
					entity.setAdditionalDistrict(district);
				}
			}
			
			if(assignment.getAdditionalTpuId()!=null){
				Tpu tpu = tpuDao.findById(assignment.getAdditionalTpuId());
				if(tpu==null){
					entity.setAdditionalTpu(tpu);
				}
			}
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return entity;
	}
	
	@Transactional
	public void saveAssignmentUnitCategoryInfo(AssignmentUnitCategoryInfoSyncData category, Assignment assignment) throws Exception{
		AssignmentUnitCategoryInfo entity = null;
		try{
			if("D".equals(category.getLocalDbRecordStatus())){
				return ;
			}
			
			if(category.getAssignmentUnitCategoryInfoId()==null){
				if(assignment.getAssignmentId()!=null &&
						categoryDao.findByUnitCategoryAndAssignment(category.getUnitCategory(), assignment.getAssignmentId())!=null){
					 entity = categoryDao.findByUnitCategoryAndAssignment(category.getUnitCategory(), assignment.getAssignmentId());
					 category.setAssignmentUnitCategoryInfoId(entity.getAssignmentUnitCategoryInfoId());
				 } else {
					 entity = new AssignmentUnitCategoryInfo();
				 }
			} else {
				entity = categoryDao.findById(category.getAssignmentUnitCategoryInfoId());
				if(entity==null){
					return;
				}
			}
			
			BeanUtils.copyProperties(category, entity);
			
			entity.setAssignment(assignment);
			
			categoryDao.save(entity);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return ;
	}
	
	/**
	 * Spot Check Form
	 */
	@Transactional
	public void submitSpotCheckForm(List<SpotCheckFormOnlineModel> models) throws Exception{
		if(models!=null && models.size()>0){
			for(SpotCheckFormOnlineModel model : models){
				saveSpotCheckForm(model);
			}
			spotCheckFormDao.flush();
		}
	}
	
	@Transactional
	public void saveSpotCheckForm(SpotCheckFormOnlineModel form) throws Exception{
		SpotCheckForm entity = null;
		try{
			if("D".equals(form.getLocalDbRecordStatus())){
				return;
			}
			
			if(form.getSpotCheckFormId()==null){
				throw new RuntimeException("Spot Check Form Id should not be empty");
			}
			entity = spotCheckFormDao.findById(form.getSpotCheckFormId());
			
			BeanUtils.copyProperties(form, entity);
			
			Date turnUpTime = null;
			if(!StringUtils.isEmpty(form.getTurnUpTime())){
				try {
					turnUpTime = commonService.getTime(form.getTurnUpTime());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			
			}
			entity.setTurnUpTime(turnUpTime);
			
			Date scheduledTime = null;
			if(!StringUtils.isEmpty(form.getScheduledTime())){
				try {
					scheduledTime = commonService.getTime(form.getScheduledTime());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
			}
			entity.setScheduledTime(scheduledTime);
			
			if(form.getOfficerId()!=null){
				User officer = userDao.findById(form.getOfficerId());
				if (officer != null){
					entity.setOfficer(officer);						
				}

			}
			
			if(form.getSupervisorId()!=null){
				User supervisor = userDao.findById(form.getSupervisorId());
				if (supervisor != null){
					entity.setSupervisor(supervisor);
				}

			}
			
			if(form.getSubmitTo()!=null){
				User submitTo = userDao.findById(form.getSubmitTo());
				if (submitTo != null){
					entity.setSubmitTo(submitTo);						
				}

			}
			
			if(form.getScSvPlanId()!=null){
				ScSvPlan scSvPlan = scSvPlanDao.findById(form.getScSvPlanId());
				if (scSvPlan != null){
					entity.setScSvPlan(scSvPlan);						
				}

			}
			
			if(form.getSpotCheckResults()!=null && form.getSpotCheckResults().size()>0){
				for(SpotCheckResultSyncData spotCheckResult : form.getSpotCheckResults()){
					saveSpotCheckResult(spotCheckResult, entity);
				}
			}
			
			if(form.getSpotCheckPhoneCall()!=null && form.getSpotCheckPhoneCall().size()>0){
				for(SpotCheckPhoneCallSyncData spotCheckPhoneCall : form.getSpotCheckPhoneCall()){
					saveSpotCheckPhoneCall(spotCheckPhoneCall, entity);
				}
			}
			
			entity.setStatus("Submitted");
			
			spotCheckFormDao.save(entity);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return;
	}
	
	@Transactional
	public void saveSpotCheckResult(SpotCheckResultSyncData result, SpotCheckForm spotCheckForm) throws Exception{
		SpotCheckResult entity = null;
		try{
			if("D".equals(result.getLocalDbRecordStatus())){
				if(result.getSpotCheckResultId()==null){
					return;
				}
				entity = spotCheckResultDao.findById(result.getSpotCheckResultId());
				if(entity!=null){
					spotCheckResultDao.delete(entity);
				}
				return;
			}
			
			if(result.getSpotCheckResultId()==null){
				entity = new SpotCheckResult();
			} else {
				entity = spotCheckResultDao.findById(result.getSpotCheckResultId());
				if(entity==null){
					return;
				}
			}
			
			BeanUtils.copyProperties(result, entity);
			entity.setSpotCheckForm(spotCheckForm);
			
			spotCheckResultDao.save(entity);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return;
	}
	
	@Transactional
	public void saveSpotCheckPhoneCall(SpotCheckPhoneCallSyncData phone, SpotCheckForm spotCheckForm) throws Exception{
		SpotCheckPhoneCall entity = null;
		try{
			if("D".equals(phone.getLocalDbRecordStatus())){
				if(phone.getSpotCheckPhoneCallId()==null){
					return;
				}
				entity = spotCheckPhoneCallDao.findById(phone.getSpotCheckPhoneCallId());
				if(entity!=null){
					spotCheckPhoneCallDao.delete(entity);
				}
				return;
			}
			
			if(phone.getSpotCheckPhoneCallId()==null){
				entity = new SpotCheckPhoneCall();
			} else {
				entity = spotCheckPhoneCallDao.findById(phone.getSpotCheckPhoneCallId());
				if(entity==null){
					return;
				}
			}
			
			BeanUtils.copyProperties(phone, entity);
			
			Date phoneCallTime = null;
			if(!StringUtils.isEmpty(phone.getPhoneCallTime())){
				try {
					phoneCallTime = commonService.getTime(phone.getPhoneCallTime());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			entity.setPhoneCallTime(phoneCallTime);
			
			entity.setSpotCheckForm(spotCheckForm);
			
			spotCheckPhoneCallDao.save(entity);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return;
	}
	
	public List<ItineraryPlanOnlineModel> downloadItineraryPlan(Integer[] userIds, Date date){
		
		List<ItineraryPlanOnlineModel> itineraryPlans = itineraryPlanDao.houseKeepItineraryPlan(userIds, date);
		
		for(ItineraryPlanOnlineModel itineraryPlan : itineraryPlans){
			Integer itineraryPlanId = itineraryPlan.getItineraryPlanId();
			Integer[] itineraryPlanIds = new Integer[1];
			itineraryPlanIds[0] = itineraryPlanId;
			
			itineraryPlan.setItineraryPlanAssignments(itineraryPlanDao.getUpdateItineraryPlanAssignment(null, itineraryPlanIds));
			
			itineraryPlan.setItineraryUnPlanAssignments(itineraryPlanDao.getUpdateItineraryUnPlanAssignment(null, itineraryPlanIds));
			
			itineraryPlan.setMajorLocations(majorLocationDao.getUpdateMajorLocation(null, itineraryPlanIds));
			
			List<ItineraryPlanOutletSyncData> itineraryPlanOutlets = itineraryPlanOutletDao.getUpdateItineraryPlanOutlet(null, itineraryPlanIds);
			
			itineraryPlan.setItineraryPlanOutlets(itineraryPlanOutlets);
			
			List<Integer> outletIds = new ArrayList<Integer>();
			
			for(ItineraryPlanOutletSyncData itineraryPlanOutlet : itineraryPlanOutlets){
				if(itineraryPlanOutlet.getPlanType()==1){
					outletIds.add(itineraryPlanOutlet.getOutletId());
				}
			}
			
			List<OutletSyncData> outlets = null;
			
			if(outletIds !=null && outletIds.size()>0){
				Integer[] ids = new Integer[outletIds.size()];
				outlets = outletDao.getUpdatedOutlets(null, outletIds.toArray(ids));
			}
			
			itineraryPlan.setOutlets(outlets);
		}
		
		return itineraryPlans;
	}
	
	public List<QCItineraryPlanOnlineModel> downloarQCItineraryPlan(Integer userId){
		List<QCItineraryPlanOnlineModel> qcItineraryPlans = qcItineraryPlanDao.houseKeepQCItineraryPlan(userId);
		
		for(QCItineraryPlanOnlineModel qcItineraryPlan : qcItineraryPlans){
			Integer qcItineraryPlanId = qcItineraryPlan.getQcItineraryPlanId();
			Integer[] qcItineraryPlanIds = new Integer[1];
			qcItineraryPlanIds[0] = qcItineraryPlanId;
			
			List<QCItineraryPlanItemSyncData> qcItineraryPlanItems = qcItineraryPlanItemDao.getUpdateQCItineraryPlanItem(null, qcItineraryPlanIds);
			qcItineraryPlan.setQcItineraryPlanItems(qcItineraryPlanItems);
			
			List<Integer> peCheckFormIds = new ArrayList<Integer>();
			List<Integer> spotCheckFormIds = new ArrayList<Integer>();
			List<Integer> supervisoryVisitFormIds = new ArrayList<Integer>();
			Set<Integer> userIds = new HashSet<Integer>();
			
			for(QCItineraryPlanItemSyncData qcItineraryPlanItem : qcItineraryPlanItems){
				if(qcItineraryPlanItem.getItemType()==1){
					spotCheckFormIds.add(qcItineraryPlanItem.getSpotCheckFormId());
				} else if(qcItineraryPlanItem.getItemType()==2){
					supervisoryVisitFormIds.add(qcItineraryPlanItem.getSupervisoryVisitFormId());
				}
				peCheckFormIds.add(qcItineraryPlanItem.getPeCheckFormId());
			}
			if(spotCheckFormIds!=null && spotCheckFormIds.size()>0){
				List<SpotCheckForm> spotCheckForms = spotCheckFormDao.getSpotCheckFormsByIds(spotCheckFormIds);
				for(SpotCheckForm spotCheckForm : spotCheckForms){
					userIds.add(spotCheckForm.getOfficer().getUserId());
				}
			}
			if(supervisoryVisitFormIds!=null && supervisoryVisitFormIds.size()>0){
				List<SupervisoryVisitForm> supervisoryVisitForms = supervisoryVisitFormDao.getSupervisoryVisitFormsByIds(supervisoryVisitFormIds);
				for(SupervisoryVisitForm supervisoryVisitForm : supervisoryVisitForms){
					userIds.add(supervisoryVisitForm.getUser().getUserId());
				}
			}
			/**
			 * For PE Check
			 */
			
			if(peCheckFormIds!=null && peCheckFormIds.size()>0){
				Integer[] ids = new Integer[peCheckFormIds.size()];
				List<PECheckFormSyncData> peCheckForms = peCheckFormDao.getUpdatedPECheckForm(null, peCheckFormIds.toArray(ids));
				for(PECheckFormSyncData peCheckForm : peCheckForms){
					userIds.add(peCheckForm.getOfficerId());
				}
			}
			if(userIds!=null && userIds.size()>0){
				Integer[] ids = new Integer[userIds.size()];
				qcItineraryPlan.setItineraryPlans(downloadItineraryPlan(userIds.toArray(ids), qcItineraryPlan.getDate()));
			}
		}
		
		return qcItineraryPlans;		
		
	}
	
	@Transactional
	public AssignmentDataReturn onlineFunctionPECheck(List<PECheckFormOnlineModel> models) throws Exception{
		Map<Integer, Outlet> mapOutlet = new Hashtable<Integer, Outlet>();
		Map<Integer, Product> mapProduct = new Hashtable<Integer, Product>();
		Map<Integer, ProductSpecification> mapProductSpec = new Hashtable<Integer, ProductSpecification>();
		List<Integer> skipIds = new ArrayList<Integer>();
		
		AssignmentDataReturn ret = new AssignmentDataReturn();
		if(models!=null && models.size()>0){
			for(PECheckFormOnlineModel model : models){
				submitPECheck(model, mapOutlet, mapProduct, mapProductSpec, skipIds);
			}
			peCheckFormDao.flush();
		}
		
		Map<Integer, Integer> outletIdsMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> firmCodeMap = new HashMap<Integer, Integer>();
		for (Integer key : mapOutlet.keySet()){
			outletIdsMap.put(key, mapOutlet.get(key).getId());
			firmCodeMap.put(mapOutlet.get(key).getId(), mapOutlet.get(key).getFirmCode());
		}		
		ret.setOutletIdMap(outletIdsMap);
		ret.setFirmCodeMap(firmCodeMap);
		
		Map<Integer, Integer> productIdsMap = new HashMap<Integer, Integer>();
		for (Integer key : mapProduct.keySet()){
			productIdsMap.put(key, mapProduct.get(key).getId());
		}		
		ret.setProductIdMap(productIdsMap);
		
		Map<Integer, Integer> productSpecIdsMap = new HashMap<Integer, Integer>();
		for (Integer key : mapProductSpec.keySet()){
			productSpecIdsMap.put(key, mapProductSpec.get(key).getId());
		}		
		ret.setProductSpecIdMap(productSpecIdsMap);
		ret.setSkipIds(skipIds);
		
		return ret;
	}
	
	@Transactional
	public void submitPECheck(PECheckFormOnlineModel peCheck, Map<Integer, Outlet> mapOutlet
			, Map<Integer, Product> mapProduct
			, Map<Integer, ProductSpecification> mapProductSpec
			, List<Integer> skipIds)throws Exception{
		PECheckForm entity = null;
		
		entity = savePECheck(peCheck);
		if(entity == null){
			return;
		}
		entity.setStatus("Submitted");
		Assignment assignment = null;
		
		if(peCheck.getAssignment()!=null){
			assignment = submitAssignment(peCheck.getAssignment(), mapOutlet, mapProduct, mapProductSpec, skipIds);
		}
		
		entity.setAssignment(assignment);
		
		peCheckFormDao.save(entity);
	}
	
	@Transactional
	public PECheckForm savePECheck(PECheckFormOnlineModel peCheckForm)throws Exception{
		PECheckForm entity = null;
		try{
			if("D".equals(peCheckForm.getLocalDbRecordStatus())){
				return null;
			}
			
			if(peCheckForm.getPeCheckFormId()==null){
				entity = new PECheckForm();
			} else {
				entity = peCheckFormDao.findById(peCheckForm.getPeCheckFormId());
				if(entity==null){
					return null;
				}
			}
			
			BeanUtils.copyProperties(peCheckForm, entity);
			
			Date checkingTime = null;
			if(!StringUtils.isEmpty(peCheckForm.getCheckingTime())){
				try{
					checkingTime = commonService.getTime(peCheckForm.getCheckingTime());
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			}
			entity.setCheckingTime(checkingTime);
			
			if(peCheckForm.getOfficerId()!=null){
				User officer = userDao.findById(peCheckForm.getOfficerId());
				if (officer != null){
					entity.setOfficer(officer);						
				}
			}
			
			if(peCheckForm.getUserId()!=null){
				User user = userDao.findById(peCheckForm.getUserId());
				if (user != null){
					entity.setUser(user);						
				}
			}
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return entity;
	}
	
	//B170 - When updating category remark in assignment maintenance, it cannot be updated for back no. quotations, while new remarks can be shown in original quotation.
	@Transactional
	//public void saveCategoryRemark(List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategorys, List<Integer> quotationRecordIds) throws Exception {
	public void saveCategoryRemark(List<AssignmentUnitCategoryInfoSyncData> assignmentUnitCategorys, Integer assignmentId) throws Exception {

		//Update All Category Remark
		List<QuotationRecord> quotationRecordLists = quotationRecordDao.getByIdsWithAssignment(assignmentId);
		
		//
		
		if (quotationRecordLists.size() > 0) {
			
		//if (quotationRecordIds.size() > 0) {
			//List<QuotationRecord> quotationRecords = quotationRecordDao.getByIdsWithUnit(quotationRecordIds);
			List<Integer> quotationRecordIdListOnly = new ArrayList<Integer>();
					
			for (QuotationRecord qr : quotationRecordLists) {
				quotationRecordIdListOnly.add(qr.getQuotationRecordId());
			}
			
			if(quotationRecordIdListOnly.size() > 0 ) {
				List<QuotationRecord> quotationRecords = quotationRecordDao.getByIdsWithUnit(quotationRecordIdListOnly);

				HashMap<String, AssignmentUnitCategoryInfoSyncData> unitCategoryToAssignmentUnitCategoryInfoMapping = new HashMap<String, AssignmentUnitCategoryInfoSyncData>();
				for (AssignmentUnitCategoryInfoSyncData info : assignmentUnitCategorys) {
					unitCategoryToAssignmentUnitCategoryInfoMapping.put(info.getUnitCategory(), info);
				}

				for (QuotationRecord quotationRecord : quotationRecords) {
					AssignmentUnitCategoryInfoSyncData info = unitCategoryToAssignmentUnitCategoryInfoMapping
							.get(quotationRecord.getQuotation().getUnit().getUnitCategory());
					if (info == null) continue;
					
					QuotationRecord originalQuotationRecord = quotationRecordDao
							.getQuotationRecordsForFindBackNoData(quotationRecord);
					if (originalQuotationRecord != null) {
						originalQuotationRecord.setCategoryRemark(info.getRemark());
					}
					quotationRecord.setContactPerson(info.getContactPerson());

					quotationRecord.setCategoryRemark(info.getRemark());
					quotationRecordDao.save(quotationRecord);
				}
				quotationRecordDao.flush();
			}
			
		}
		
	}
}
