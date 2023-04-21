package com.kinetix.api;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.Outlet;
import capi.entity.OutletAttachment;
import capi.entity.Product;
import capi.entity.VwEntityRemoveLog;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.SystemConstant;
import capi.model.api.LoginResultModel;
import capi.model.api.MobileResponseModel;
import capi.model.api.dataSync.AssignmentAttributeSyncData;
import capi.model.api.dataSync.AssignmentSyncData;
import capi.model.api.dataSync.AssignmentSyncModel;
import capi.model.api.dataSync.AssignmentUnitCategoryInfoSyncData;
import capi.model.api.dataSync.AssignmentUnitCategorySyncModel;
import capi.model.api.dataSync.BatchCollectionDateSyncData;
import capi.model.api.dataSync.BusinessParameterSyncData;
import capi.model.api.dataSync.DiscountFormulaSyncData;
import capi.model.api.dataSync.DistrictSyncData;
import capi.model.api.dataSync.FieldworkTimeLogSyncData;
import capi.model.api.dataSync.FieldworkTimeLogSyncModel;
import capi.model.api.dataSync.GetModel;
import capi.model.api.dataSync.HousekeepingDataSync;
import capi.model.api.dataSync.ItineraryPlanAssignmentSyncData;
import capi.model.api.dataSync.ItineraryPlanOutletSyncData;
import capi.model.api.dataSync.ItineraryPlanSyncData;
import capi.model.api.dataSync.ItineraryUnPlanAssignmentSyncData;
import capi.model.api.dataSync.MajorLocationSyncData;
import capi.model.api.dataSync.NotificationSyncData;
import capi.model.api.dataSync.OnSpotValidationSyncData;
import capi.model.api.dataSync.OutletAttachmentSyncData;
import capi.model.api.dataSync.OutletAttachmentSyncModel;
import capi.model.api.dataSync.OutletAttachmentZipImageSyncData;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.OutletSyncModel;
import capi.model.api.dataSync.OutletTypeOutletSyncData;
import capi.model.api.dataSync.OutletTypeOutletSyncModel;
import capi.model.api.dataSync.OutletTypePriceReasonSyncData;
import capi.model.api.dataSync.OutletTypeSyncData;
import capi.model.api.dataSync.OutletUnitStatisticSyncData;
import capi.model.api.dataSync.PECheckFormSyncData;
import capi.model.api.dataSync.PECheckFormSyncModel;
import capi.model.api.dataSync.PointToNoteOutletSyncData;
import capi.model.api.dataSync.PointToNoteProductSyncData;
import capi.model.api.dataSync.PointToNoteQuotationSyncData;
import capi.model.api.dataSync.PointToNoteSyncData;
import capi.model.api.dataSync.PointToNoteUnitSyncData;
import capi.model.api.dataSync.PriceReasonSyncData;
import capi.model.api.dataSync.PricingFrequencySyncData;
import capi.model.api.dataSync.ProductAttributeSyncData;
import capi.model.api.dataSync.ProductCleaningSyncData;
import capi.model.api.dataSync.ProductGroupSyncData;
import capi.model.api.dataSync.ProductSpecificationSyncData;
import capi.model.api.dataSync.ProductSyncData;
import capi.model.api.dataSync.ProductZipImageSyncData;
import capi.model.api.dataSync.PurposeSyncData;
import capi.model.api.dataSync.QCItineraryPlanItemSyncData;
import capi.model.api.dataSync.QCItineraryPlanSyncData;
import capi.model.api.dataSync.QuotationRecordResponseModel;
import capi.model.api.dataSync.QuotationRecordSyncData;
import capi.model.api.dataSync.QuotationRecordSyncModel;
import capi.model.api.dataSync.QuotationSyncData;
import capi.model.api.dataSync.RUAQuotationSyncData;
import capi.model.api.dataSync.RUAUserSyncData;
import capi.model.api.dataSync.RoleSyncData;
import capi.model.api.dataSync.SpotCheckFormSyncData;
import capi.model.api.dataSync.SpotCheckFormSyncModel;
import capi.model.api.dataSync.SpotCheckPhoneCallSyncData;
import capi.model.api.dataSync.SpotCheckPhoneCallSyncModel;
import capi.model.api.dataSync.SpotCheckResultSyncData;
import capi.model.api.dataSync.SpotCheckResultSyncModel;
import capi.model.api.dataSync.SubItemSyncData;
import capi.model.api.dataSync.SubPriceColumnSyncData;
import capi.model.api.dataSync.SubPriceColumnSyncModel;
import capi.model.api.dataSync.SubPriceFieldMappingSyncData;
import capi.model.api.dataSync.SubPriceFieldSyncData;
import capi.model.api.dataSync.SubPriceRecordSyncData;
import capi.model.api.dataSync.SubPriceRecordSyncModel;
import capi.model.api.dataSync.SubPriceTypeSyncData;
import capi.model.api.dataSync.SupervisoryVisitDetailSyncData;
import capi.model.api.dataSync.SupervisoryVisitDetailSyncModel;
import capi.model.api.dataSync.SupervisoryVisitFormSyncData;
import capi.model.api.dataSync.SupervisoryVisitFormSyncModel;
import capi.model.api.dataSync.SurveyMonthSyncData;
import capi.model.api.dataSync.SyncModel;
import capi.model.api.dataSync.TelephoneTimeLogSyncData;
import capi.model.api.dataSync.TelephoneTimeLogSyncModel;
import capi.model.api.dataSync.TimeLogSyncData;
import capi.model.api.dataSync.TimeLogSyncModel;
import capi.model.api.dataSync.TotalZipFile;
import capi.model.api.dataSync.TourRecordSyncData;
import capi.model.api.dataSync.TourRecordSyncModel;
import capi.model.api.dataSync.TpuSyncData;
import capi.model.api.dataSync.UOMCategorySyncData;
import capi.model.api.dataSync.UOMCategoryUnitSyncData;
import capi.model.api.dataSync.UOMConversionSyncData;
import capi.model.api.dataSync.UnitCategoryInfoSyncData;
import capi.model.api.dataSync.UnitSyncData;
import capi.model.api.dataSync.UomSyncData;
import capi.model.api.dataSync.UpdateOutletImageModel;
import capi.model.api.dataSync.UpdateProductImageModel;
import capi.model.api.dataSync.UserRoleSyncData;
import capi.model.api.dataSync.UserSyncData;
import capi.model.api.dataSync.WorkingSessionSettingSyncData;
import capi.model.api.onlineFunction.AssignmentOnlineModel;
import capi.service.AppConfigService;
import capi.service.EntityRemoveLogService;
import capi.service.NotificationService;
import capi.service.UserService;
import capi.service.WorkingSessionSettingService;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.assignmentManagement.QuotationService;
import capi.service.itineraryPlanning.ItineraryPlanningService;
import capi.service.masterMaintenance.BusinessParameterService;
import capi.service.masterMaintenance.DiscountFormulaService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PointToNoteService;
import capi.service.masterMaintenance.PriceReasonService;
import capi.service.masterMaintenance.PricingMonthService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.SubPriceService;
import capi.service.masterMaintenance.TpuService;
import capi.service.masterMaintenance.UOMCategoryService;
import capi.service.masterMaintenance.UOMConversionService;
import capi.service.masterMaintenance.UnitService;
import capi.service.masterMaintenance.UomService;
import capi.service.productMaintenance.ProductService;
import capi.service.qualityControlManagement.PECheckService;
import capi.service.qualityControlManagement.SpotCheckService;
import capi.service.qualityControlManagement.SupervisoryVisitService;
import capi.service.timeLogManagement.TimeLogService;
import capi.service.userAccountManagement.RoleService;

@Controller("DataSyncController")
@RequestMapping("api/DataSync")
public class DataSyncController {
	
	private static final Logger logger = LoggerFactory.getLogger(DataSyncController.class);
	
	@Autowired
	private UnitService unitService;
	@Autowired
	private PointToNoteService pointToNoteService;
	@Autowired
	private PriceReasonService priceReasonService;
	@Autowired
	private SubPriceService subPriceService;
	@Autowired
	private DiscountFormulaService discountFormulaService;
	@Autowired
	private UOMConversionService uomConversionService;
	@Autowired
	private UomService uomService;
	@Autowired
	private UOMCategoryService uomCategoryService;
	@Autowired
	private ProductService productService;
	@Autowired
	private PurposeService purposeService;
	@Autowired
	private WorkingSessionSettingService workingSessionSettingService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private DistrictService districtService;
	@Autowired
	private TpuService tpuService;
	@Autowired
	private EntityRemoveLogService entityRemoveLogService;
	@Autowired
	private OutletService outletService;
	@Autowired
	private AppConfigService configService;
	@Autowired
	private BusinessParameterService businessParameterService;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private SurveyMonthService surveyMonthService;
	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;
	@Autowired
	private QuotationService quotationService;
	@Autowired
	private ItineraryPlanningService itineraryPlanningService;
	@Autowired
	private QuotationRecordService quotationRecordService;
	@Autowired
	private TimeLogService timeLogService;
	@Autowired
	private SpotCheckService spotCheckService;
	@Autowired
	private SupervisoryVisitService supervisoryVisitService;
	@Autowired
	private PECheckService peCheckService;
	@Autowired
	private PricingMonthService pricingMonthService;
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                  new CustomDateEditor(new SimpleDateFormat(SystemConstant.DATE_TIME_FORMAT),true));
    }	
	
	@RequestMapping("getDistricts")
	public @ResponseBody MobileResponseModel<DistrictSyncData> getDistricts(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<DistrictSyncData> model = new MobileResponseModel<DistrictSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getDistricts start");
			List<DistrictSyncData> data = districtService.getUpdatedDistricts(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getDistricts end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getDistricts failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		
		return model;
	}	
	
	@RequestMapping("getTpus")
	public @ResponseBody MobileResponseModel<TpuSyncData> getTpus(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<TpuSyncData> model = new MobileResponseModel<TpuSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getTpus start");
			List<TpuSyncData> data = tpuService.getUpdatedTpus(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getTpus end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getTpus failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;		
	}
	
	@RequestMapping("getNotification")
	public @ResponseBody MobileResponseModel<NotificationSyncData> getNotification(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<NotificationSyncData> model = new MobileResponseModel<NotificationSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getNotification start");
			List<NotificationSyncData> data = notificationService.getUpdateNotification(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getNotification end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getNotification failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
		
	}
	
	@RequestMapping("getWorkingSession")
	public @ResponseBody MobileResponseModel<WorkingSessionSettingSyncData> getWorkingSession(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<WorkingSessionSettingSyncData> model = new MobileResponseModel<WorkingSessionSettingSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getWorkingSession start");
			List<WorkingSessionSettingSyncData> data = workingSessionSettingService.getUpdateWorkingSessionSetting(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getWorkingSession end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getWorkingSession failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getPurpose")
	public @ResponseBody MobileResponseModel<PurposeSyncData> getPurpose(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PurposeSyncData> model = new MobileResponseModel<PurposeSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getPurpose start");
			List<PurposeSyncData> data = purposeService.getUpdatePurpose(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getPurpose end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getPurpose failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getProductGroup")
	public @ResponseBody MobileResponseModel<ProductGroupSyncData> getProductGroup(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ProductGroupSyncData> model = new MobileResponseModel<ProductGroupSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getProductGroup start");
			List<ProductGroupSyncData> data = productService.getUpdateProductGroup(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getProductGroup end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getProductGroup failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getProductAttribute")
	public @ResponseBody MobileResponseModel<ProductAttributeSyncData> getProductAttribute(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ProductAttributeSyncData> model = new MobileResponseModel<ProductAttributeSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getProductAttribute start");
			List<ProductAttributeSyncData> data = productService.getUpdateProductAttribute(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getProductAttribute end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getProductAttribute failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getUOMCategory")
	public @ResponseBody MobileResponseModel<UOMCategorySyncData> getUOMCategory(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UOMCategorySyncData> model = new MobileResponseModel<UOMCategorySyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getUOMCategory start");
			List<UOMCategorySyncData> data = uomCategoryService.getUpdateUOMCategory(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getUOMCategory end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUOMCategory failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getUom")
	public @ResponseBody MobileResponseModel<UomSyncData> getUom(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UomSyncData> model = new MobileResponseModel<UomSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getUom start");
			List<UomSyncData> data = uomService.getUpdateUom(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getUom end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUom failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getUOMConversion")
	public @ResponseBody MobileResponseModel<UOMConversionSyncData> getUOMConversion(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UOMConversionSyncData> model = new MobileResponseModel<UOMConversionSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getUOMConversion start");
			List<UOMConversionSyncData> data = uomConversionService.getUpdateUOMConversion(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getUOMConversion end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUOMConversion failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getDiscountFormula")
	public @ResponseBody MobileResponseModel<DiscountFormulaSyncData> getDiscountFormula(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<DiscountFormulaSyncData> model = new MobileResponseModel<DiscountFormulaSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getDiscountFormula start");
			List<DiscountFormulaSyncData> data = discountFormulaService.getUpdateDiscountFormula(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getDiscountFormula end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getDiscountFormula failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getSubPriceField")
	public @ResponseBody MobileResponseModel<SubPriceFieldSyncData> getSubPriceField(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SubPriceFieldSyncData> model = new MobileResponseModel<SubPriceFieldSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getSubPriceField start");
			List<SubPriceFieldSyncData> data = subPriceService.getUpdateSubPriceField(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getSubPriceField end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getSubPriceField failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getSubPriceType")
	public @ResponseBody MobileResponseModel<SubPriceTypeSyncData> getSubPriceType(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SubPriceTypeSyncData> model = new MobileResponseModel<SubPriceTypeSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getSubPriceType start");
			List<SubPriceTypeSyncData> data = subPriceService.getUpdateSubPriceType(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getSubPriceType end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getSubPriceType failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getSubPriceFieldMapping")
	public @ResponseBody MobileResponseModel<SubPriceFieldMappingSyncData> getSubPriceFieldMapping(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SubPriceFieldMappingSyncData> model = new MobileResponseModel<SubPriceFieldMappingSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getSubPriceFieldMapping start");
			List<SubPriceFieldMappingSyncData> data = subPriceService.getUpdateSubPriceFieldMapping(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getSubPriceFieldMapping end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getSubPriceFieldMapping failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getPriceReason")
	public @ResponseBody MobileResponseModel<PriceReasonSyncData> getPriceReason(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PriceReasonSyncData> model = new MobileResponseModel<PriceReasonSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getPriceReason start");
			List<PriceReasonSyncData> data = priceReasonService.getUpdatePriceReasion(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getPriceReason end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getPriceReason failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getOutletTypePriceReason")
	public @ResponseBody MobileResponseModel<OutletTypePriceReasonSyncData> getOutletTypePriceReason(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletTypePriceReasonSyncData> model = new MobileResponseModel<OutletTypePriceReasonSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getOutletTypePriceReason start");
			List<OutletTypePriceReasonSyncData> data = priceReasonService.getUpdateOutletTypePriceReason(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getOutletTypePriceReason end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getOutletTypePriceReason failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getPointToNote")
	public @ResponseBody MobileResponseModel<PointToNoteSyncData> getPointToNote(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PointToNoteSyncData> model = new MobileResponseModel<PointToNoteSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNote start");
			List<PointToNoteSyncData> data = pointToNoteService.getUpdatePointToNote(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNote end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getPointToNote failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getPointToNoteOutlet")
	public @ResponseBody MobileResponseModel<PointToNoteOutletSyncData> getPointToNoteOutlet(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PointToNoteOutletSyncData> model = new MobileResponseModel<PointToNoteOutletSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNoteOutlet start");
			List<PointToNoteOutletSyncData> data = pointToNoteService.getUpdatePointToNoteOutlet(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNoteOutlet end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getPointToNoteOutlet failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getPointToNoteProduct")
	public @ResponseBody MobileResponseModel<PointToNoteProductSyncData> getPointToNoteProduct(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PointToNoteProductSyncData> model = new MobileResponseModel<PointToNoteProductSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNoteProduct start");
			List<PointToNoteProductSyncData> data = pointToNoteService.getUpdatePointToNoteProduct(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNoteProduct end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getPointToNoteProduct failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getPointToNoteQuotation")
	public @ResponseBody MobileResponseModel<PointToNoteQuotationSyncData> getPointToNoteQuotation(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PointToNoteQuotationSyncData> model = new MobileResponseModel<PointToNoteQuotationSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNoteQuotation start");
			List<PointToNoteQuotationSyncData> data = pointToNoteService.getUpdatePointToNoteQuotation(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNoteQuotation end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getPointToNoteQuotation failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getPointToNoteUnit")
	public @ResponseBody MobileResponseModel<PointToNoteUnitSyncData> getPointToNoteUnit(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PointToNoteUnitSyncData> model = new MobileResponseModel<PointToNoteUnitSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNoteUnit start");
			List<PointToNoteUnitSyncData> data = pointToNoteService.getUpdatePointToNoteUnit(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getPointToNoteUnit end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getPointToNoteUnit failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getOutletType")
	public @ResponseBody MobileResponseModel<OutletTypeSyncData> getOutletType(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletTypeSyncData> model = new MobileResponseModel<OutletTypeSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getOutletType start");
			List<OutletTypeSyncData> data = unitService.getUpdateOutletType(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getOutletType end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getOutletType failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getSubItem")
	public @ResponseBody MobileResponseModel<SubItemSyncData> getSubItem(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SubItemSyncData> model = new MobileResponseModel<SubItemSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getSubItem start");
			List<SubItemSyncData> data = unitService.getUpdateSubItem(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getSubItem end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getSubItem failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getUnit")
	public @ResponseBody MobileResponseModel<UnitSyncData> getUnit(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UnitSyncData> model = new MobileResponseModel<UnitSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getUnit start");
			List<UnitSyncData> data = unitService.getUpdateUnit(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getUnit end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUnit failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getOnSpotValidation")
	public @ResponseBody MobileResponseModel<OnSpotValidationSyncData> getOnSpotValidation(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OnSpotValidationSyncData> model = new MobileResponseModel<OnSpotValidationSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getOnSpotValidation start");
			List<OnSpotValidationSyncData> data = unitService.getUpdateOnSpotValidation(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getOnSpotValidation end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getOnSpotValidation failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getUOMCategoryUnit")
	public @ResponseBody MobileResponseModel<UOMCategoryUnitSyncData> getUOMCategoryUnit(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UOMCategoryUnitSyncData> model = new MobileResponseModel<UOMCategoryUnitSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getUOMCategoryUnit start");
			List<UOMCategoryUnitSyncData> data = unitService.getUpdateUOMCategoryUnit(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getUOMCategoryUnit end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUOMCategoryUnit failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getOutletTypeOutlet")
	public @ResponseBody MobileResponseModel<OutletTypeOutletSyncData> getOutletTypeOutlet(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletTypeOutletSyncData> model = new MobileResponseModel<OutletTypeOutletSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getOutletTypeOutlet start");
			List<OutletTypeOutletSyncData> data = outletService.getUpdateOutletTypeOutlet(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getOutletTypeOutlet end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getOutletTypeOutlet failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getOutletUnitStatistic")
	public @ResponseBody MobileResponseModel<OutletUnitStatisticSyncData> getOutletUnitStatistic(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletUnitStatisticSyncData> model = new MobileResponseModel<OutletUnitStatisticSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getOutletUnitStatistic start");
			List<OutletUnitStatisticSyncData> data = unitService.getUpdateOutletUnitStatistic(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getOutletUnitStatistic end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getOutletUnitStatistic failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getBusinessParameter")
	public @ResponseBody MobileResponseModel<BusinessParameterSyncData> getBusinessParameter(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<BusinessParameterSyncData> model = new MobileResponseModel<BusinessParameterSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getBusinessParameter start");
			List<BusinessParameterSyncData> data = businessParameterService.getUpdateBusinessParameter(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getBusinessParameter end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getBusinessParameter failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getUser")
	public @ResponseBody MobileResponseModel<UserSyncData> getUser(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UserSyncData> model = new MobileResponseModel<UserSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getUser start");
			List<UserSyncData> data = userService.getUpdateUser(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Synce Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");	
			logger.info("User: "+detail.getUserId()+" Data sync getUser end");
		} catch (Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUser failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getRole")
	public @ResponseBody MobileResponseModel<RoleSyncData> getRole(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<RoleSyncData> model = new MobileResponseModel<RoleSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getRole start");
			List<RoleSyncData> data = roleService.getUpdateRole(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Synce Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");	
			logger.info("User: "+detail.getUserId()+" Data sync getRole end");
		} catch (Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getRole failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getUserRole")
	public @ResponseBody MobileResponseModel<UserRoleSyncData> getUserRole(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UserRoleSyncData> model = new MobileResponseModel<UserRoleSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getUserRole start");
			List<UserRoleSyncData> data = userService.getUpdateUserRole(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Synce Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");	
			logger.info("User: "+detail.getUserId()+" Data sync getUserRole end");
		} catch (Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUserRole failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getSurveyMonth")
	public @ResponseBody MobileResponseModel<SurveyMonthSyncData> getSurveyMonth(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SurveyMonthSyncData> model = new MobileResponseModel<SurveyMonthSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getSurveyMonth start");
			List<SurveyMonthSyncData> data = surveyMonthService.getUpdateSurveyMonth(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Synce Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");	
			logger.info("User: "+detail.getUserId()+" Data sync getSurveyMonth end");
		} catch (Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getSurveyMonth failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("syncUnitCategoryInfos")
	public @ResponseBody MobileResponseModel<UnitCategoryInfoSyncData> syncUnitCategoryInfos(@RequestBody SyncModel<UnitCategoryInfoSyncData> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UnitCategoryInfoSyncData> model = new MobileResponseModel<UnitCategoryInfoSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncUnitCategoryInfos start");
			List<UnitCategoryInfoSyncData> data = assignmentMaintenanceService.syncUnitCategoryInfoData(syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncUnitCategoryInfos end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncUnitCategoryInfos failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getRUAQuotations")
	public @ResponseBody MobileResponseModel<RUAQuotationSyncData> getRUAQuotations(Date lastSyncTime, Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<RUAQuotationSyncData> model = new MobileResponseModel<RUAQuotationSyncData>();
		List<Integer> quotationIds = new ArrayList<Integer>();
		if(ids!=null&&ids.length>0){
			for(int i =0; i<ids.length;i++){
				quotationIds.add(ids[i]);
			}
		}
		
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getRUAQuotations start");
			List<RUAQuotationSyncData> data = quotationService.getUpdateRUAQuotation(lastSyncTime, quotationIds);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getRUAQuotations end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getRUAQuotations failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getRUAUser")
	public @ResponseBody MobileResponseModel<RUAUserSyncData> getRUAUser(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<RUAUserSyncData> model = new MobileResponseModel<RUAUserSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getRUAUser start");
			List<RUAUserSyncData> data = quotationService.getUpdateRUAUser(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getRUAUser end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getRUAUser failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getItineraryPlan")
	public @ResponseBody MobileResponseModel<ItineraryPlanSyncData> getItineraryPlan(Date lastSyncTime, Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ItineraryPlanSyncData> model = new MobileResponseModel<ItineraryPlanSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getItineraryPlan start");
			if(ids == null || ids.length<1){
				model.setMessage("Data sync getItineraryPlan failure: ids should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getItineraryPlan failure: "
						+ "ids should not be empty");
			} else {
				List<ItineraryPlanSyncData> data = itineraryPlanningService.getUpdateItineraryPlan(lastSyncTime, ids);
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getItineraryPlan end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getItineraryPlan failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getItineraryPlanAssignment")
	public @ResponseBody MobileResponseModel<ItineraryPlanAssignmentSyncData> getItineraryPlanAssignment(Date lastSyncTime, Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ItineraryPlanAssignmentSyncData> model = new MobileResponseModel<ItineraryPlanAssignmentSyncData>();
		
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getItineraryPlanAssignment start");
			if(ids == null || ids.length<1){
				model.setMessage("Data sync getItineraryPlanAssignment failure: ids should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getItineraryPlanAssignment failure: "
						+ "ids should not be empty");
			} else {
				List<ItineraryPlanAssignmentSyncData> data = itineraryPlanningService.getUpdateItineraryPlanAssignment(lastSyncTime, ids);
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getItineraryPlanAssignment end");
			}
			
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getItineraryPlanAssignment failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getMajorLocation")
	public @ResponseBody MobileResponseModel<MajorLocationSyncData> getMajorLocation(Date lastSyncTime, Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<MajorLocationSyncData> model = new MobileResponseModel<MajorLocationSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getMajorLocation start");
			if(ids == null || ids.length<1){
				model.setMessage("Data sync getMajorLocation failure: ids should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getMajorLocation failure: "
						+ "ids should not be empty");
			} else {
				List<MajorLocationSyncData> data = itineraryPlanningService.getUpdateMajorLocation(lastSyncTime, ids);
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getMajorLocation end");
			}
			
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getMajorLocation failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getItineraryPlanOutlet")
	public @ResponseBody MobileResponseModel<ItineraryPlanOutletSyncData> getItineraryPlanOutlet(Date lastSyncTime, Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ItineraryPlanOutletSyncData> model = new MobileResponseModel<ItineraryPlanOutletSyncData>();
		
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getItineraryPlanOutlet start");
			if(ids == null || ids.length<1){
				model.setMessage("Data sync getItineraryPlanOutlet failure: ids should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getItineraryPlanOutlet failure: "
						+ "ids should not be empty");
			} else {
				List<ItineraryPlanOutletSyncData> data = itineraryPlanningService.getUpdateItineraryPlanOutlet(lastSyncTime, ids);
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getItineraryPlanOutlet end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getItineraryPlanOutlet failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getItineraryUnPlanAssignment")
	public @ResponseBody MobileResponseModel<ItineraryUnPlanAssignmentSyncData> getItineraryUnPlanAssignment(Date lastSyncTime, Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ItineraryUnPlanAssignmentSyncData> model = new MobileResponseModel<ItineraryUnPlanAssignmentSyncData>();
		
		try{	
			logger.info("User: "+detail.getUserId()+" Data sync getItineraryUnPlanAssignment start");
			if(ids == null || ids.length<1){
				model.setMessage("Data sync getItineraryUnPlanAssignment failure: ids should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getItineraryUnPlanAssignment failure: "
						+ "ids should not be empty");
			} else {
				List<ItineraryUnPlanAssignmentSyncData> data = itineraryPlanningService.getUpdateItineraryUnPlanAssignmentSyncData(lastSyncTime, ids);
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getItineraryUnPlanAssignment end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getItineraryUnPlanAssignment failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}

	@RequestMapping("getQCItineraryPlan")
	public @ResponseBody MobileResponseModel<QCItineraryPlanSyncData> getQCItineraryPlan(Date lastSyncTime, Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<QCItineraryPlanSyncData> model = new MobileResponseModel<QCItineraryPlanSyncData>();
		
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getQCItineraryPlan start");
			if(ids == null || ids.length<1){
				model.setMessage("Data sync getQCItineraryPlan failure: ids should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getQCItineraryPlan failure: "
						+ "ids should not be empty");
			} else {
				List<QCItineraryPlanSyncData> data = itineraryPlanningService.getUpdateQCItineraryPlan(lastSyncTime, ids);
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getQCItineraryPlan end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getQCItineraryPlan failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getQCItineraryPlanItem")
	public @ResponseBody MobileResponseModel<QCItineraryPlanItemSyncData> getQCItineraryPlanItem(Date lastSyncTime, Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<QCItineraryPlanItemSyncData> model = new MobileResponseModel<QCItineraryPlanItemSyncData>();
		
		try{	
			logger.info("User: "+detail.getUserId()+" Data sync getQCItineraryPlanItem start");
			if(ids == null || ids.length<1){
				model.setMessage("Data sync getQCItineraryPlanItem failure: ids should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getQCItineraryPlanItem failure: "
						+ "ids should not be empty");
			} else {
				List<QCItineraryPlanItemSyncData> data = itineraryPlanningService.getUpdateQCItineraryPlanItem(lastSyncTime, ids);
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getQCItineraryPlanItem end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getQCItineraryPlanItem failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getAssignmentAttributes")
	public @ResponseBody MobileResponseModel<AssignmentAttributeSyncData> getAssignmentAttributes(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<AssignmentAttributeSyncData> model = new MobileResponseModel<AssignmentAttributeSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getAssignmentAttributes start");
			List<AssignmentAttributeSyncData> data = assignmentMaintenanceService.getUpdatedAssignmentAttribute(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getAssignmentAttributes end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getAssignmentAttributes failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getBatchCollectionDates")
	public @ResponseBody MobileResponseModel<BatchCollectionDateSyncData> getBatchCollectionDates(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<BatchCollectionDateSyncData> model = new MobileResponseModel<BatchCollectionDateSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getBatchCollectionDates start");
			List<BatchCollectionDateSyncData> data = assignmentMaintenanceService.getUpdatedBatchCollectionDate(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getBatchCollectionDates end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getBatchCollectionDates failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("getQuotationRecordHistory")
	public @ResponseBody MobileResponseModel<QuotationRecordSyncData> getQuotationRecordHistory(Integer[] ids){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<QuotationRecordSyncData> model = new MobileResponseModel<QuotationRecordSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getQuotationRecordHistory start");
			if(ids == null || ids.length<1){
				model.setMessage("Data sync getQuotationRecordHistory failure: ids should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getQuotationRecordHistory failure: "
						+ "ids should not be empty");
			} else {
				List<QuotationRecordSyncData> data = quotationRecordService.getQuotationRecordHistory(ids);
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getQuotationRecordHistory end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getQuotationRecordHistory failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("syncQuotationRecords")
	public @ResponseBody MobileResponseModel<QuotationRecordResponseModel> syncQuotationRecords(@RequestBody QuotationRecordSyncModel syncModel, Authentication auth){
		MobileResponseModel<QuotationRecordResponseModel> model = new MobileResponseModel<QuotationRecordResponseModel>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		
		//criteria.setAuthorityLevel(detail.getAuthorityLevel());
		try{	
			logger.info("User: "+detail.getUserId()+" Data sync syncQuotationRecords start");
			if(syncModel.getDataReturn() && (syncModel.getQuotationRecordIds()==null || syncModel.getQuotationRecordIds().length<1) && (syncModel.getAssignmentIds() == null || syncModel.getAssignmentIds().length<1)){
				model.setMessage("Data sync syncQuotationRecords failure: QuotationRecordIds and AssignmentIds, both should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncQuotationRecords failure: "
						+ "QuotationRecordIds and AssignmentIds, both should not be empty");
				if (syncModel.getData() != null && !syncModel.getData().isEmpty()) {
					String syncIds = syncModel.getData().stream().map(qr->{
						String parentId = qr.getQuotationRecordId() != null? qr.getQuotationRecordId().toString(): "newID";
						if (qr.getOriginalQuotationRecordId() != null && qr.getOriginalQuotationRecordId() > 0) {
							return qr.getOriginalQuotationRecordId()+"->"+parentId;
						}
						return parentId;
					}).collect(java.util.stream.Collectors.joining(","));
					logger.warn("User: "+detail.getUserId()+" Data sync syncQuotationRecords failure but data is not empty: "+syncIds);
				}
			} else {
				try {
					if (syncModel.getData() != null && !syncModel.getData().isEmpty()) {
						String syncIds = syncModel.getData().stream().map(qr->{
							String parentId = qr.getQuotationRecordId() != null? qr.getQuotationRecordId().toString(): "newID";
							if (qr.getOriginalQuotationRecordId() != null && qr.getOriginalQuotationRecordId() > 0) {
								return qr.getOriginalQuotationRecordId()+"->"+parentId;
							}
							return parentId;
						}).collect(java.util.stream.Collectors.joining(","));
						logger.info("User: "+detail.getUserId()+" Data sync syncQuotationRecords upload quotationrecord IDs: "+syncIds);
					} else {
						logger.info("User: "+detail.getUserId()+" Data sync syncQuotationRecords upload quotationrecord IDs: Nothing");
					}
					if (syncModel.getAssignmentIds() != null && syncModel.getAssignmentIds().length > 0) {
						logger.info("User: "+detail.getUserId()+" Data sync syncQuotationRecords requested download assignment IDs: "+java.util.Arrays.stream(syncModel.getAssignmentIds()).distinct().map(i->i.toString()).collect(java.util.stream.Collectors.joining(",")));
					} else {
						logger.info("User: "+detail.getUserId()+" Data sync syncQuotationRecords requested download assignment IDs: Nothing");
					}
					if (syncModel.getQuotationRecordIds() != null && syncModel.getQuotationRecordIds().length > 0) {
						logger.info("User: "+detail.getUserId()+" Data sync syncQuotationRecords requested download quotationrecord IDs: "+java.util.Arrays.stream(syncModel.getQuotationRecordIds()).map(i->i.toString()).collect(java.util.stream.Collectors.joining(",")));
					} else {
						logger.info("User: "+detail.getUserId()+" Data sync syncQuotationRecords requested download quotationrecord IDs: Nothing");
					}
				}
				catch (Exception ex){
					logger.warn("User: "+detail.getUserId()+" Data sync syncQuotationRecords failed to log: ", ex);
				}
				QuotationRecordResponseModel data = quotationRecordService.syncQuotationRecordData(
						syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getQuotationRecordIds(), syncModel.getAssignmentIds(), detail.getAuthorityLevel());
				List<QuotationRecordResponseModel> list = new ArrayList<QuotationRecordResponseModel>();
				list.add(data);
				model.setData(list);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncQuotationRecords end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncQuotationRecords failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncProducts")
	public @ResponseBody MobileResponseModel<ProductSyncData> syncProducts(@RequestBody SyncModel<ProductSyncData> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ProductSyncData> model = new MobileResponseModel<ProductSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncProducts start");
			List<ProductSyncData> data = productService.syncProductData(syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncProducts end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncProducts failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncProductSpecifications")
	public @ResponseBody MobileResponseModel<ProductSpecificationSyncData> syncProductSpecifications(@RequestBody SyncModel<ProductSpecificationSyncData> syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ProductSpecificationSyncData> model = new MobileResponseModel<ProductSpecificationSyncData>();
		try{	
			logger.info("User: "+detail.getUserId()+" Data sync syncProductSpecifications start");
			List<ProductSpecificationSyncData> data = productService.syncProductSpecificationData(syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncProductSpecifications end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncProductSpecifications failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncAssignmentUnitCategorys")
	public @ResponseBody MobileResponseModel<AssignmentUnitCategoryInfoSyncData> syncAssignmentUnitCategorys(@RequestBody AssignmentUnitCategorySyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<AssignmentUnitCategoryInfoSyncData> model = new MobileResponseModel<AssignmentUnitCategoryInfoSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncAssignmentUnitCategorys start");
			if(syncModel.getDataReturn() && (syncModel.getAssignmentIds()==null || syncModel.getAssignmentIds().length<1)){
				model.setMessage("Data sync syncAssignmentUnitCategorys failure: AssignmentIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncAssignmentUnitCategorys failure: "
						+ "AssignmentIds should not be empty");
			} else {
				List<AssignmentUnitCategoryInfoSyncData> data = assignmentMaintenanceService.syncAssignmentUnitCategoryInfoData(syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getAssignmentIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncAssignmentUnitCategorys end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncAssignmentUnitCategorys failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncSubPriceRecords")
	public @ResponseBody MobileResponseModel<SubPriceRecordSyncData> syncSubPriceRecords(@RequestBody SubPriceRecordSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SubPriceRecordSyncData> model = new MobileResponseModel<SubPriceRecordSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncSubPriceRecords start");
			if(syncModel.getDataReturn() && (syncModel.getAssignmentIds() == null || syncModel.getAssignmentIds().length<1)){
				model.setMessage("Data sync syncSubPriceRecords failure: AssignmentIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncSubPriceRecords failure: "
						+ "AssignmentIds should not be empty");
			} else {
				List<SubPriceRecordSyncData> data = quotationRecordService.syncSubPriceRecordData(syncModel.getData()
						, syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getAssignmentIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncSubPriceRecords end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncSubPriceRecords failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncSubPriceColumns")
	public @ResponseBody MobileResponseModel<SubPriceColumnSyncData> syncSubPriceColumns(@RequestBody SubPriceColumnSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SubPriceColumnSyncData> model = new MobileResponseModel<SubPriceColumnSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncSubPriceColumns start");
			if(syncModel.getDataReturn() && (syncModel.getAssignmentIds()==null || syncModel.getAssignmentIds().length<1)){
				model.setMessage("Data sync syncSubPriceColumns failure: AssignmentIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncSubPriceColumns failure: "
						+ "AssignmentIds should not be empty");
			} else {
				List<SubPriceColumnSyncData> data = quotationRecordService.syncSubPriceColumnData(syncModel.getData()
						, syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getAssignmentIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncSubPriceColumns end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncSubPriceColumns failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncTimeLogs")
	public @ResponseBody MobileResponseModel<TimeLogSyncData> syncTimeLogs(@RequestBody TimeLogSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<TimeLogSyncData> model = new MobileResponseModel<TimeLogSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncTimeLogs start");
			List<TimeLogSyncData> data = timeLogService.syncTimeLogData(syncModel.getData()
					, syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getTimeLogIds(), syncModel.getDates());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncTimeLogs end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncTimeLogs failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncFieldworkTimeLogs")
	public @ResponseBody MobileResponseModel<FieldworkTimeLogSyncData> syncFieldworkTimeLogs(@RequestBody FieldworkTimeLogSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<FieldworkTimeLogSyncData> model = new MobileResponseModel<FieldworkTimeLogSyncData>();
		try{	
			logger.info("User: "+detail.getUserId()+" Data sync syncFieldworkTimeLogs start");
			List<FieldworkTimeLogSyncData> data = timeLogService.syncFieldworkTimeLogData(syncModel.getData()
					, syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getTimeLogIds());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncFieldworkTimeLogs end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncFieldworkTimeLogs failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncTelephoneTimeLogs")
	public @ResponseBody MobileResponseModel<TelephoneTimeLogSyncData> syncTelephoneTimeLogs(@RequestBody TelephoneTimeLogSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<TelephoneTimeLogSyncData> model = new MobileResponseModel<TelephoneTimeLogSyncData>();
		try{	
			logger.info("User: "+detail.getUserId()+" Data sync syncTelephoneTimeLogs start");
			List<TelephoneTimeLogSyncData> data = timeLogService.syncTelephoneTimeLogData(syncModel.getData()
					, syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getTimeLogIds());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncTelephoneTimeLogs end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncTelephoneTimeLogs failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncSpotCheckForms")
	public @ResponseBody MobileResponseModel<SpotCheckFormSyncData> syncSpotCheckForms(@RequestBody SpotCheckFormSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SpotCheckFormSyncData> model = new MobileResponseModel<SpotCheckFormSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncSpotCheckForms start");
			if(syncModel.getDataReturn() && (syncModel.getSpotCheckFormIds()==null || syncModel.getSpotCheckFormIds().length<1) && (syncModel.getQcItineraryPlanIds()==null || syncModel.getQcItineraryPlanIds().length<1)){
				model.setMessage("Data sync syncSpotCheckForms failure: SpotCheckFormIds or QCItineraryPlanIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncSpotCheckForms failure: "
						+ "SpotCheckFormIds or QCItineraryPlanIds should not be empty");
			} else {
				List<SpotCheckFormSyncData> data = spotCheckService.syncSpotCheckFormData(syncModel.getData()
						, syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getSpotCheckFormIds(), syncModel.getQcItineraryPlanIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncSpotCheckForms end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncSpotCheckForms failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncSpotCheckPhoneCalls")
	public @ResponseBody MobileResponseModel<SpotCheckPhoneCallSyncData> syncSpotCheckPhoneCalls(@RequestBody SpotCheckPhoneCallSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SpotCheckPhoneCallSyncData> model = new MobileResponseModel<SpotCheckPhoneCallSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncSpotCheckPhoneCalls start");
			if(syncModel.getDataReturn() && (syncModel.getSpotCheckFormIds()==null || syncModel.getSpotCheckFormIds().length<1)){
				model.setMessage("Data sync syncSpotCheckPhoneCalls failure: SpotCheckFormIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncSpotCheckPhoneCalls failure: "
						+ "SpotCheckFormIds should not be empty");
			} else {
				List<SpotCheckPhoneCallSyncData> data = spotCheckService.syncSpotCheckPhoneCallData(syncModel.getData()
						, syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getSpotCheckFormIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncSpotCheckPhoneCalls end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncSpotCheckPhoneCalls failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncSpotCheckResults")
	public @ResponseBody MobileResponseModel<SpotCheckResultSyncData> syncSpotCheckResults(@RequestBody SpotCheckResultSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SpotCheckResultSyncData> model = new MobileResponseModel<SpotCheckResultSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncSpotCheckResults start");
			if(syncModel.getDataReturn() && (syncModel.getSpotCheckFormIds()==null || syncModel.getSpotCheckFormIds().length<1)){
				model.setMessage("Data sync syncSpotCheckResults failure: SpotCheckFormIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncSpotCheckResults failure: "
						+ "SpotCheckFormIds should not be empty");
			} else {
				List<SpotCheckResultSyncData> data = spotCheckService.syncSpotCheckResultData(syncModel.getData()
						, syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getSpotCheckFormIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncSpotCheckResults end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncSpotCheckResults failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncAssignments")
	public @ResponseBody MobileResponseModel<AssignmentSyncData> syncAssignments(@RequestBody AssignmentSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<AssignmentSyncData> model = new MobileResponseModel<AssignmentSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync syncAssignments start");
			if(syncModel.getDataReturn() && (syncModel.getAssignmentIds() == null || syncModel.getAssignmentIds().length<1 ) && (syncModel.getItineraryPlanIds() == null || syncModel.getItineraryPlanIds().length<1)){
				model.setMessage("Data sync syncAssignments failure: AssignmentIds and ItineraryPlanIds, both should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncAssignments failure: "
						+ "AssignmentIds and ItineraryPlanIds, both should not be empty");
				if (syncModel.getData() != null && !syncModel.getData().isEmpty()) {
					String syncIds = syncModel.getData().stream().map(assignment->{
						return assignment.getAssignmentId() != null? assignment.getAssignmentId().toString(): "newID";
					}).collect(java.util.stream.Collectors.joining(","));
					logger.info("User: "+detail.getUserId()+" Data sync syncAssignments failure but data is not empty, assignment IDs from data: "+syncIds);
				}
			} else {
				try{
					if (syncModel.getData() != null && !syncModel.getData().isEmpty()) {
						String syncIds = syncModel.getData().stream().map(assignment->{
							return assignment.getAssignmentId() != null? assignment.getAssignmentId().toString(): "newID";
						}).collect(java.util.stream.Collectors.joining(","));
						logger.info("User: "+detail.getUserId()+" Data sync syncAssignments upload assignment IDs: "+syncIds);
					} else {
						logger.info("User: "+detail.getUserId()+" Data sync syncAssignments upload assignment IDs: Nothing");
					}
					if (syncModel.getAssignmentIds() != null && syncModel.getAssignmentIds().length > 0) {
						logger.info("User: "+detail.getUserId()+" Data sync syncAssignments requested download assignment IDs: "+java.util.Arrays.stream(syncModel.getAssignmentIds()).map(i->i.toString()).collect(java.util.stream.Collectors.joining(",")));
					} else {
						logger.info("User: "+detail.getUserId()+" Data sync syncAssignments requested download assignment IDs: Nothing");
					}
				}
				catch (Exception ex){
					logger.warn("User: "+detail.getUserId()+" Data sync syncAssignments failed to log: ", ex);
				}
				List<AssignmentSyncData> data = assignmentMaintenanceService.syncAssignmentData(
						syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getAssignmentIds(), syncModel.getItineraryPlanIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncAssignments end");
			}
			
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncAssignments failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getDownloadedQuotations") 
	public @ResponseBody MobileResponseModel<QuotationSyncData> getDownloadedQuotations(@RequestBody GetModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<QuotationSyncData> model = new MobileResponseModel<QuotationSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getDownloadedQuotations start");
			if((syncModel.getQuotationIds()==null || syncModel.getQuotationIds().length <1 )&& (syncModel.getAssignmentIds()==null || syncModel.getAssignmentIds().length<1)){
				model.setMessage("Data sync getDownloadedQuotations failure: QuotationIds and AssignmentIds, both should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync getDownloadedQuotations failure: "
						+ "QuotationIds and AssignmentIds, both should not be empty");
			} else {
				List<QuotationSyncData> data = quotationService.getUpdatedDownloadedQuotation(syncModel.getLastSyncTime(), syncModel.getQuotationIds(), syncModel.getAssignmentIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync getDownloadedQuotations end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getDownloadedQuotations failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncSupervisoryVisitForms")
	public @ResponseBody MobileResponseModel<SupervisoryVisitFormSyncData> syncSupervisoryVisitForms(@RequestBody SupervisoryVisitFormSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SupervisoryVisitFormSyncData> model = new MobileResponseModel<SupervisoryVisitFormSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncSupervisoryVisitForms start");
			if((syncModel.getDataReturn()!=null && syncModel.getDataReturn()) && (syncModel.getSupervisoryVisitFormIds()==null || syncModel.getSupervisoryVisitFormIds().length<1) && (syncModel.getQcItineraryPlanIds()==null || syncModel.getQcItineraryPlanIds().length<1)){
				model.setMessage("Data sync syncSupervisoryVisitForms failure: SupervisoryVisitFormIds or QCItineraryPlanIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncSupervisoryVisitForms failure: "
						+ "SupervisoryVisitFormIds or QCItineraryPlanIds should not be empty");
			} else {
				List<SupervisoryVisitFormSyncData> data = supervisoryVisitService.syncSupervisoryVisitFormData(
						syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getSupervisoryVisitFormIds(), syncModel.getQcItineraryPlanIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncSupervisoryVisitForms end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncSupervisoryVisitForms failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncSupervisoryVisitDetails")
	public @ResponseBody MobileResponseModel<SupervisoryVisitDetailSyncData> syncSupervisoryVisitDetails(@RequestBody SupervisoryVisitDetailSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<SupervisoryVisitDetailSyncData> model = new MobileResponseModel<SupervisoryVisitDetailSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncSupervisoryVisitDetails start");
			if(syncModel.getDataReturn() && (syncModel.getSupervisoryVisitFormIds()==null || syncModel.getSupervisoryVisitFormIds().length<1)){
				model.setMessage("Data sync syncSupervisoryVisitDetails failure: SupervisoryVisitFormIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncSupervisoryVisitDetails failure: "
						+ "SupervisoryVisitFormIds should not be empty");
			} else {
				List<SupervisoryVisitDetailSyncData> data = supervisoryVisitService.syncSupervisoryVisitDetailData(
						syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getSupervisoryVisitFormIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncSupervisoryVisitDetails end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncSupervisoryVisitDetails failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncPECheckForms")
	public @ResponseBody MobileResponseModel<PECheckFormSyncData> syncPECheckForms(@RequestBody PECheckFormSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PECheckFormSyncData> model = new MobileResponseModel<PECheckFormSyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync syncPECheckForms start");
			if(syncModel.getDataReturn() && (syncModel.getPeCheckFormIds()==null || syncModel.getPeCheckFormIds().length<1) && (syncModel.getQcItineraryPlanIds()==null || syncModel.getQcItineraryPlanIds().length<1)){
				model.setMessage("Data sync syncPECheckForms failure: PECheckFormIds or QCItineraryPlanIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncPECheckForms failure: "
						+ "PECheckFormIds or QCItineraryPlanIds should not be empty");
			} else {
				List<PECheckFormSyncData> data = peCheckService.syncPECheckFormData(
						syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getPeCheckFormIds(), syncModel.getQcItineraryPlanIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncPECheckForms end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncPECheckForms failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("syncTourRecords")
	public @ResponseBody MobileResponseModel<TourRecordSyncData> syncTourRecords(@RequestBody TourRecordSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<TourRecordSyncData> model = new MobileResponseModel<TourRecordSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncTourRecords start");
			if(syncModel.getDataReturn() && (syncModel.getAssignmentIds()==null || syncModel.getAssignmentIds().length<1)){
				model.setMessage("Data sync syncTourRecords failure: AssignmentIds should not be empty");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync syncTourRecords failure: "
						+ "AssignmentIds should not be empty");
			} else {
				List<TourRecordSyncData> data = quotationRecordService.syncTourRecordData(
						syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getAssignmentIds());
				model.setData(data);
				model.setMessage("Data Sync Successfully");
				model.setStatus(LoginResultModel.SUCCESS);
				model.setStatusString("SUCCESS");
				logger.info("User: "+detail.getUserId()+" Data sync syncTourRecords end");
			}
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncTourRecords failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping("getRemoveLogs")
	public @ResponseBody MobileResponseModel<VwEntityRemoveLog> getRemoveLogs(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<VwEntityRemoveLog> model = new MobileResponseModel<VwEntityRemoveLog>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getRemoveLogs start");
			List<VwEntityRemoveLog> data = entityRemoveLogService.getUpdatedRemoveLogs(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getRemoveLogs end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getRemoveLogs failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;		
	}
	
	@RequestMapping("syncOutlets")
	public @ResponseBody MobileResponseModel<OutletSyncData> syncOutlets(@RequestBody OutletSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletSyncData> model = new MobileResponseModel<OutletSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncOutlets start");
			List<OutletSyncData> data = outletService.syncOutletData(syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getOutletIds());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncOutlets end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncOutlets failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
		
	}
	
	@RequestMapping("getPricingFrequency")
	public @ResponseBody MobileResponseModel<PricingFrequencySyncData> getPricingFrequency(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<PricingFrequencySyncData> model = new MobileResponseModel<PricingFrequencySyncData>();
		try{
			logger.info("User: "+detail.getUserId()+" Data sync getPricingFrequency start");
			List<PricingFrequencySyncData> data = pricingMonthService.getUpdatePricingFrequency(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getPricingFrequency end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getPricingFrequency failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping("syncOutletTypeOutlets")
	public @ResponseBody MobileResponseModel<OutletTypeOutletSyncData> syncOutletTypeOutlet(@RequestBody OutletTypeOutletSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletTypeOutletSyncData> model = new MobileResponseModel<OutletTypeOutletSyncData>();
		try{	
			logger.info("User: "+detail.getUserId()+" Data sync syncOutletTypeOutlets start");
			List<OutletTypeOutletSyncData> data = outletService.syncOutletTypeOutlet(syncModel.getData());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncOutletTypeOutlets end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncOutletTypeOutlets failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}	
		return model;
	}
	
	@RequestMapping("syncOutletAttachments")
	public @ResponseBody MobileResponseModel<OutletAttachmentSyncData> syncOutletAttachments(@RequestBody OutletAttachmentSyncModel syncModel){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletAttachmentSyncData> model = new MobileResponseModel<OutletAttachmentSyncData>();
		
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync syncOutletAttachments start");
			List<OutletAttachmentSyncData> data = outletService.syncOutletAttachments(syncModel.getAttachments(), syncModel.getLastSyncTime(), syncModel.getDataReturn());
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncOutletAttachments end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncOutletAttachments failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}	
		return model;
	}
	
	/**
	 * Download outlet image
//	 */
//	@RequestMapping(value = "getOutletImage", method = RequestMethod.GET)
//	public ResponseEntity<InputStreamResource> getOutletImage(@RequestParam("id") int id) {
//		try {
//			Outlet entity = outletService.getOutletById(id);
//			File imageFile = new File(configService.getFileBaseLoc() + entity.getOutletImagePath());
//			return ResponseEntity.ok()
//					.contentLength(imageFile.length())
//					.contentType(MediaType.IMAGE_PNG)
//					.header("Accept-Ranges", "bytes")
//					//.header("Content-Disposition", "attachment; filename=\"" + id + ".png\"")
//					.body(new InputStreamResource(new FileInputStream(imageFile)));
//		} catch (Exception e) {
//			logger.error("getOutletImage", e);
//		}
//		return null;
//	}
	@RequestMapping(value = "getOutletImage", method = RequestMethod.GET)
	public void getOutletImage(@RequestParam("id") int id, HttpServletResponse response) throws Exception{
		try {
			Outlet entity = outletService.getOutletById(id);
			File imageFile = new File(configService.getFileBaseLoc() + entity.getOutletImagePath());
			response.setContentType(MediaType.IMAGE_PNG_VALUE);	
	        response.setContentLength((int)imageFile.length());		        
	        response.setHeader("Content-Disposition","attachment; filename=\""+id+".png\"");
	 
	        FileCopyUtils.copy(new FileInputStream(imageFile), response.getOutputStream());	
		} catch (Exception e) {
			logger.error("getOutletImage", e);
			throw e;
		}
	}
	
	
	/**
	 * Download outlet image
	 */
	@RequestMapping(value = "getOutletImageBase64", method = RequestMethod.GET)
	public @ResponseBody String getOutletImageBase64(@RequestParam("id") int id) {
		try {
			Outlet entity = outletService.getOutletById(id);
			java.nio.file.Path path = FileSystems.getDefault().getPath(configService.getFileBaseLoc() + entity.getOutletImagePath());
			return Base64Utils.encodeToString(Files.readAllBytes(path));
			
		} catch (Exception e) {
			logger.error("getOutletImageBase64", e);
		}
		return null;
	}
	
	/**
	 * save outlet Image
	 */
	@RequestMapping(value = "uploadOutletImage", method = RequestMethod.POST)
	public @ResponseBody MobileResponseModel uploadOutletImage(@RequestParam(value = "id") int id, MultipartHttpServletRequest request, Locale locale, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel model = new MobileResponseModel();
		
		try {
			logger.info("User: "+detail.getUserId()+" Data sync uploadOutletImage start");
			MultipartFile multiFile = request.getFile("file");
			if (multiFile == null || multiFile.isEmpty()) {
				model.setMessage("No file has been uploaded");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync uploadOutletImage failure: "
						+ "No file has been uploaded");
				return model;
			}
			
			if (!multiFile.getContentType().contains("image")) {
				model.setMessage(messageSource.getMessage("E00100", null, locale));
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync uploadOutletImage failure: "
						+ messageSource.getMessage("E00100", null, locale));
				return model;
			}
				
			outletService.uploadOutletImage(id, multiFile.getInputStream());
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync uploadOutletImage end");
			
			return model;
		} catch (Exception ex) {
			logger.error("User: "+detail.getUserId()+" Data sync uploadOutletImage failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	/**
	 * Download outlet attachment
	 */
//	@RequestMapping(value = "getOutletAttachmentImage", method = RequestMethod.GET)
//	public ResponseEntity<InputStreamResource> getOutletAttachmentImage(@RequestParam("id") int id) {
//		try {
//			OutletAttachment entity = outletService.getAttachmentById(id);
//			File imageFile = new File(configService.getFileBaseLoc() + entity.getPath());
//			
//			return ResponseEntity.ok()
//					.contentLength(imageFile.length())
//					.contentType(MediaType.IMAGE_PNG)
//					.header("Content-Disposition", "attachment; filename=\"" + id + ".png\"")
//					.body(new InputStreamResource(new FileInputStream(imageFile)));
//		} catch (Exception e) {
//			logger.error("getAttachment", e);
//		}
//		return null;
//	}
	@RequestMapping(value = "getOutletAttachmentImage", method = RequestMethod.GET)
	public void getOutletAttachmentImage(@RequestParam("id") int id, HttpServletResponse response) throws Exception{
		try {
			OutletAttachment entity = outletService.getAttachmentById(id);
			File imageFile = new File(configService.getFileBaseLoc() + entity.getPath());
			
			
			response.setContentType(MediaType.IMAGE_PNG_VALUE);	
	        response.setContentLength((int)imageFile.length());		        
	        response.setHeader("Content-Disposition","attachment; filename=\""+id+".png\"");
	 
	        FileCopyUtils.copy(new FileInputStream(imageFile), response.getOutputStream());	
		} catch (Exception e) {
			logger.error("getOutletAttachmentImage", e);
			throw e;
		}
	}
	
	
	/**
	 * Download outlet image
	 */
	@RequestMapping(value = "getOutletAttachmentImageBase64", method = RequestMethod.GET)
	public @ResponseBody String getOutletAttachmentImageBase64(@RequestParam("id") int id) {
		try {
			OutletAttachment entity = outletService.getAttachmentById(id);
			java.nio.file.Path path = FileSystems.getDefault().getPath(configService.getFileBaseLoc() + entity.getPath());
			return Base64Utils.encodeToString(Files.readAllBytes(path));
			
		} catch (Exception e) {
			logger.error("getOutletImageBase64", e);
		}
		return null;
	}
	
	/**
	 * save attachment
	 */
	@RequestMapping(value = "uploadOutletAttachment", method = RequestMethod.POST)
	public @ResponseBody MobileResponseModel uploadOutletAttachment(@RequestParam(value = "id") int id, MultipartHttpServletRequest request, Locale locale, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel model = new MobileResponseModel();
		try {
			logger.info("User: "+detail.getUserId()+" Data sync uploadOutletAttachment start");
			MultipartFile multiFile = request.getFile("file");
			if (multiFile == null || multiFile.isEmpty()) {
				model.setMessage("No file has been uploaded");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync uploadOutletAttachment failure: "
						+ "No file has been uploaded");
				return model;
			}
			
			if (!multiFile.getContentType().contains("image")) {
				model.setMessage(messageSource.getMessage("E00100", null, locale));
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync uploadOutletAttachment failure: "
						+ messageSource.getMessage("E00100", null, locale));
				return model;
			}
				
			outletService.uploadAttachment(id, multiFile.getInputStream());
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync uploadOutletAttachment end");
			
			return model;
		} catch (Exception ex) {
			logger.error("User: "+detail.getUserId()+" Data sync uploadOutletAttachment failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	/**
	 * Download product image
	 */
//	@RequestMapping(value = "getProductImage", method = RequestMethod.GET)
//	public ResponseEntity<InputStreamResource> getProductImage(@RequestParam("id") int id, @RequestParam("image") int image) {
//		try {
//			Product entity = productService.getProductById(id);
//			File imageFile = null;
//			if(image == 1){
//				imageFile = new File(configService.getFileBaseLoc() + entity.getPhoto1Path());
//			} else {
//				imageFile = new File(configService.getFileBaseLoc() + entity.getPhoto2Path());
//			}
//			
//			return ResponseEntity.ok()
//					.contentLength(imageFile.length())
//					.contentType(MediaType.IMAGE_PNG)
//					.header("Content-Disposition", "attachment; filename=\"" + id +"_"+ image + ".png\"")
//					.body(new InputStreamResource(new FileInputStream(imageFile)));
//		} catch (Exception e) {
//			logger.error("getProductImage", e);
//		}
//		return null;
//	}
	
	@RequestMapping(value = "getProductImage", method = RequestMethod.GET)
	public void getProductImage(@RequestParam("id") int id, @RequestParam("image") int image, HttpServletResponse response) throws Exception{
		try {
			//Outlet entity = outletService.getOutletById(id);
			//File imageFile = new File(configService.getFileBaseLoc() + entity.getOutletImagePath());
			Product entity = productService.getProductById(id);
			File imageFile = null;
			if(image == 1){
				imageFile = new File(configService.getFileBaseLoc() + entity.getPhoto1Path());
			} else {
				imageFile = new File(configService.getFileBaseLoc() + entity.getPhoto2Path());
			}
			
			response.setContentType(MediaType.IMAGE_PNG_VALUE);	
	        response.setContentLength((int)imageFile.length());		        
	        response.setHeader("Content-Disposition","attachment; filename=\""+id+"_"+image+".png\"");
	 
	        FileCopyUtils.copy(new FileInputStream(imageFile), response.getOutputStream());	
		} catch (Exception e) {
			logger.error("getProductImage", e);
			throw e;
		}
	}
	
	/**
	 * upload product Image
	 */
	@RequestMapping(value = "uploadProductImage", method = RequestMethod.POST)
	public @ResponseBody MobileResponseModel uploadProductImage(@RequestParam(value = "id") int id, @RequestParam(value = "image") int image
			, MultipartHttpServletRequest request, Locale locale, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel model = new MobileResponseModel();
		
		try {
			logger.info("User: "+detail.getUserId()+" Data sync uploadProductImage start");
			MultipartFile multiFile = request.getFile("file");
			if (multiFile == null || multiFile.isEmpty()) {
				model.setMessage("No file has been uploaded");
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync uploadProductImage failure: "
						+ "No file has been uploaded");
				return model;
			}
			
			if (!multiFile.getContentType().contains("image")) {
				model.setMessage(messageSource.getMessage("E00100", null, locale));
				model.setStatus(LoginResultModel.FAIL);
				model.setStatusString("FAIL");
				logger.error("User: "+detail.getUserId()+" Data sync uploadProductImage failure: "
						+ messageSource.getMessage("E00100", null, locale));
				return model;
			}
				
			productService.uploadProductImage(id, image, multiFile.getInputStream());
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync uploadProductImage end");
			
			return model;
		} catch (Exception ex) {
			logger.error("User: "+detail.getUserId()+" Data sync uploadProductImage failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		return model;
	}
	
	@RequestMapping(value = "getUpdatedOutletImage")
	public @ResponseBody MobileResponseModel<UpdateOutletImageModel> getUpdatedOutletImage(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UpdateOutletImageModel> model = new MobileResponseModel<UpdateOutletImageModel>();
		try {
			logger.info("User: "+detail.getUserId()+" Data sync getUpdatedOutletImage start");
			model.setData(outletService.getUpdatedOutletImage(lastSyncTime));
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getUpdatedOutletImage end");
		}
		catch (Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUpdatedOutletImage failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}	
		return model;
	}
	
	@RequestMapping(value = "getUpdatedProductImage")
	public @ResponseBody MobileResponseModel<UpdateProductImageModel> getUpdatedProductImage(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<UpdateProductImageModel> model = new MobileResponseModel<UpdateProductImageModel>();
		try {
			logger.info("User: "+detail.getUserId()+" Data sync getUpdatedProductImage start");
			List<UpdateProductImageModel> data = new ArrayList<UpdateProductImageModel>();
			data.addAll(productService.getUpdateProductImage(lastSyncTime, 1));
			data.addAll(productService.getUpdateProductImage(lastSyncTime, 2));
			
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getUpdatedProductImage end");
		}
		catch (Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getUpdatedProductImage failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}	
		return model;
	}
	
	@RequestMapping(value = "getMasterData")
	public @ResponseBody MobileResponseModel<HousekeepingDataSync> getMasterData(Date lastSyncTime) {
		HousekeepingDataSync dataSync = new HousekeepingDataSync();
		MobileResponseModel<HousekeepingDataSync> model = new MobileResponseModel<HousekeepingDataSync>();
		List<HousekeepingDataSync> dataSyncList = new ArrayList<HousekeepingDataSync>();
		dataSyncList.add(dataSync);
		model.setData(dataSyncList);
		try {
			// Business Parameter
			MobileResponseModel<BusinessParameterSyncData> businessParameterModel = 
					getBusinessParameter(lastSyncTime);
			if (businessParameterModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(businessParameterModel.getMessage());
			dataSync.setBusinessParameter(businessParameterModel.getData());

			// User
			MobileResponseModel<UserSyncData> userModel = getUser(lastSyncTime);
			if (userModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(userModel.getMessage());
			dataSync.setUser(userModel.getData());
			
			// Role
			MobileResponseModel<RoleSyncData> roleModel = getRole(lastSyncTime);
			if (roleModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(roleModel.getMessage());
			dataSync.setRole(roleModel.getData());			
			
			// UserRole
			MobileResponseModel<UserRoleSyncData> userRoleModel = getUserRole(lastSyncTime);
			if (userRoleModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(userRoleModel.getMessage());
			dataSync.setOfficerRole(userRoleModel.getData());
			
			// SurveyMonth
			MobileResponseModel<SurveyMonthSyncData> surveyMonthModel = getSurveyMonth(lastSyncTime);
			if (surveyMonthModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(surveyMonthModel.getMessage());
			dataSync.setSurveyMonth(surveyMonthModel.getData());

			// WorkingSession
			MobileResponseModel<WorkingSessionSettingSyncData> workingSessionModel = getWorkingSession(lastSyncTime);
			if (workingSessionModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(workingSessionModel.getMessage());
			dataSync.setWorkingSession(workingSessionModel.getData());

			// Notification
			MobileResponseModel<NotificationSyncData> notificationModel = getNotification(lastSyncTime);
			if (notificationModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(notificationModel.getMessage());
			dataSync.setNotification(notificationModel.getData());

			// Purpose
			MobileResponseModel<PurposeSyncData> purposeModel = getPurpose(lastSyncTime);
			if (purposeModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(purposeModel.getMessage());
			dataSync.setPurpose(purposeModel.getData());


			// ProductGroup
			MobileResponseModel<ProductGroupSyncData> productGroupModel = getProductGroup(lastSyncTime);
			if (productGroupModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(productGroupModel.getMessage());
			dataSync.setProductGroup(productGroupModel.getData());


			// ProductAttribute
			MobileResponseModel<ProductAttributeSyncData> productAttributeModel = getProductAttribute(lastSyncTime);
			if (productAttributeModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(productAttributeModel.getMessage());
			dataSync.setProductAttribute(productAttributeModel.getData());


			// UOMCategory
			MobileResponseModel<UOMCategorySyncData> UOMCategoryModel = getUOMCategory(lastSyncTime);
			if (UOMCategoryModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(UOMCategoryModel.getMessage());
			dataSync.setUOMCategory(UOMCategoryModel.getData());

			// Uom
			MobileResponseModel<UomSyncData> uomModel = getUom(lastSyncTime);
			if (uomModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(uomModel.getMessage());
			dataSync.setUOM(uomModel.getData());

			// UOMConversion
			MobileResponseModel<UOMConversionSyncData> UOMConversionModel = getUOMConversion(lastSyncTime);
			if (UOMConversionModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(UOMConversionModel.getMessage());
			dataSync.setUOMConversion(UOMConversionModel.getData());

			// Districts
			MobileResponseModel<DistrictSyncData> districtsModel = getDistricts(lastSyncTime);
			if (districtsModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(districtsModel.getMessage());
			dataSync.setDistrict(districtsModel.getData());

			// Tpus
			MobileResponseModel<TpuSyncData> tpusModel = getTpus(lastSyncTime);
			if (tpusModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(tpusModel.getMessage());
			dataSync.setTPU(tpusModel.getData());

			// DiscountFormula
			MobileResponseModel<DiscountFormulaSyncData> discountFormulaModel = getDiscountFormula(lastSyncTime);
			if (discountFormulaModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(discountFormulaModel.getMessage());
			dataSync.setDiscountFormula(discountFormulaModel.getData());

			// SubPriceField
			MobileResponseModel<SubPriceFieldSyncData> subPriceFieldModel = getSubPriceField(lastSyncTime);
			if (subPriceFieldModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(subPriceFieldModel.getMessage());
			dataSync.setSubPriceField(subPriceFieldModel.getData());

			// SubPriceType
			MobileResponseModel<SubPriceTypeSyncData> subPriceTypeModel = getSubPriceType(lastSyncTime);
			if (subPriceTypeModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(subPriceTypeModel.getMessage());
			dataSync.setSubPriceType(subPriceTypeModel.getData());

			// SubPriceFieldMapping
			MobileResponseModel<SubPriceFieldMappingSyncData> subPriceFieldMappingModel = getSubPriceFieldMapping(lastSyncTime);
			if (subPriceFieldMappingModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(subPriceFieldMappingModel.getMessage());
			dataSync.setSubPriceFieldMapping(subPriceFieldMappingModel.getData());

			// PriceReason
			MobileResponseModel<PriceReasonSyncData> priceReasonModel = getPriceReason(lastSyncTime);
			if (priceReasonModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(priceReasonModel.getMessage());
			dataSync.setPriceReason(priceReasonModel.getData());

			// OutletTypePriceReason
			MobileResponseModel<OutletTypePriceReasonSyncData> outletTypePriceReasonModel = getOutletTypePriceReason(lastSyncTime);
			if (outletTypePriceReasonModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(outletTypePriceReasonModel.getMessage());
			dataSync.setOutletTypeofPriceReason(outletTypePriceReasonModel.getData());

			// PricingFrequency
			MobileResponseModel<PricingFrequencySyncData> pricingFrequencyModel = getPricingFrequency(lastSyncTime);
			if (pricingFrequencyModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(pricingFrequencyModel.getMessage());
			dataSync.setPricingFrequency(pricingFrequencyModel.getData());

			// PointToNote
			MobileResponseModel<PointToNoteSyncData> pointToNoteModel = getPointToNote(lastSyncTime);
			if (pointToNoteModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(pointToNoteModel.getMessage());
			dataSync.setPointToNote(pointToNoteModel.getData());

			// PointToNoteOutlet
			MobileResponseModel<PointToNoteOutletSyncData> pointToNoteOutletModel = getPointToNoteOutlet(lastSyncTime);
			if (pointToNoteOutletModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(pointToNoteOutletModel.getMessage());
			dataSync.setPointToNoteOfOutlet(pointToNoteOutletModel.getData());
			
			// PointToNoteProduct
			MobileResponseModel<PointToNoteProductSyncData> pointToNoteProductModel = getPointToNoteProduct(lastSyncTime);
			if (pointToNoteProductModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(pointToNoteProductModel.getMessage());
			dataSync.setPointToNoteOfProduct(pointToNoteProductModel.getData());

			// PointToNoteQuotation
			MobileResponseModel<PointToNoteQuotationSyncData> pointToNoteQuotationModel = getPointToNoteQuotation(lastSyncTime);
			if (pointToNoteQuotationModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(pointToNoteQuotationModel.getMessage());
			dataSync.setPointToNoteOfQuotation(pointToNoteQuotationModel.getData());

			// PointToNoteUnit
			MobileResponseModel<PointToNoteUnitSyncData> pointToNoteUnitModel = getPointToNoteUnit(lastSyncTime);
			if (pointToNoteUnitModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(pointToNoteUnitModel.getMessage());
			dataSync.setPointToNoteOfUnit(pointToNoteUnitModel.getData());

			// OutletType
			MobileResponseModel<OutletTypeSyncData> outletTypeModel = getOutletType(lastSyncTime);
			if (outletTypeModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(outletTypeModel.getMessage());
			dataSync.setOutletType(outletTypeModel.getData());

			// SubItem
			MobileResponseModel<SubItemSyncData> subItemModel = getSubItem(lastSyncTime);
			if (subItemModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(subItemModel.getMessage());
			dataSync.setSubItem(subItemModel.getData());

			// Unit
			MobileResponseModel<UnitSyncData> unitModel = getUnit(lastSyncTime);
			if (unitModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(unitModel.getMessage());
			dataSync.setUnit(unitModel.getData());

			// OnSpotValidation
			MobileResponseModel<OnSpotValidationSyncData> onSpotValidationModel = getOnSpotValidation(lastSyncTime);
			if (onSpotValidationModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(onSpotValidationModel.getMessage());
			dataSync.setOnSpotValidation(onSpotValidationModel.getData());

			// UOMCategoryUnit
			MobileResponseModel<UOMCategoryUnitSyncData> uOMCategoryUnitModel = getUOMCategoryUnit(lastSyncTime);
			if (uOMCategoryUnitModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(uOMCategoryUnitModel.getMessage());
			dataSync.setUOMCategoryOfUnit(uOMCategoryUnitModel.getData());

			// OutletTypeOutlet
			MobileResponseModel<OutletTypeOutletSyncData> outletTypeOutletModel = getOutletTypeOutlet(lastSyncTime);
			if (outletTypeOutletModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(outletTypeOutletModel.getMessage());
			dataSync.setOutletTypeOutlet(outletTypeOutletModel.getData());

			// AssignmentAttributes
			MobileResponseModel<AssignmentAttributeSyncData> assignmentAttributesModel = getAssignmentAttributes(lastSyncTime);
			if (assignmentAttributesModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(assignmentAttributesModel.getMessage());
			dataSync.setAssignmentAttribute(assignmentAttributesModel.getData());

			// BatchCollectionDates
			MobileResponseModel<BatchCollectionDateSyncData> batchCollectionDatesModel = getBatchCollectionDates(lastSyncTime);
			if (batchCollectionDatesModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(batchCollectionDatesModel.getMessage());
			dataSync.setBatchCollectionDate(batchCollectionDatesModel.getData());

			// RUAUser
			MobileResponseModel<RUAUserSyncData> RUAUserModel = getRUAUser(lastSyncTime);
			if (RUAUserModel.getStatus() != LoginResultModel.SUCCESS)
				throw new Exception(RUAUserModel.getMessage());
			dataSync.setRUAUser(RUAUserModel.getData());
			
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");			
		}
		catch (Exception ex){
			logger.error("getMasterData", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}	
		return model;		
	}
	
	@RequestMapping(value = "logDataSyncStartTime")
	public @ResponseBody MobileResponseModel<String> logDataSyncStartTime(@RequestParam(value = "userId") int userId, @RequestParam(value = "id") int id, @RequestParam(value = "network") String network){
		MobileResponseModel<String> model = new MobileResponseModel<String>();
		try{		
			logger.info("Data sync start (" + network + ") for [user:" + userService.getUserById(userId).getUsername()  + " | syncId:" + id + "]");
			model.setMessage("Record for Data Sync[Start] is being logged.");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
		}
		catch(Exception ex){
			logger.error("UserId: "+userId+" Failed to write Data Sync[Start] record.", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}
	
	@RequestMapping(value = "logDataSyncEndTime")
	public @ResponseBody MobileResponseModel<String> logDataSyncEndTime(@RequestParam(value = "userId") int userId, @RequestParam(value = "id") int id, @RequestParam(value = "network") String network){
		MobileResponseModel<String> model = new MobileResponseModel<String>();
		try{		
			logger.info("Data sync end (" + network + ") for [user:" + userService.getUserById(userId).getUsername()  + " | syncId:" + id + "]");
			model.setMessage("Record for Data Sync[End] is being logged.");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
		}
		catch(Exception ex){
			logger.error("UserId: "+userId+" Failed to write Data Sync[End] reocrd.", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;
	}

	@RequestMapping("getProductCleaning")
	public @ResponseBody MobileResponseModel<ProductCleaningSyncData> getProductCleaning(Date lastSyncTime){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ProductCleaningSyncData> model = new MobileResponseModel<ProductCleaningSyncData>();
		try{		
			logger.info("User: "+detail.getUserId()+" Data sync getProductCleaning start");
			List<ProductCleaningSyncData> data = productService.getUpdatedProductCleaning(lastSyncTime);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync getProductCleaning end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync getProductCleaning failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}
		
		return model;
	}	
	

	// CR 11 - For Download Zip File Image 
	@RequestMapping(value = "downloadImageZipFile", method = RequestMethod.GET)
	public void downloadImageZipFile(@RequestParam("id") int id, HttpServletResponse response) throws Exception {

		logger.info(" Data sync downloadImageZipFile start :: " + id);
		try {

			String fileBaseLoc = configService.getFileBaseLoc();
			//fileBaseLoc + "/download/outletImage.zip";
			String pathString = fileBaseLoc + "/download/";
			String imageTypeString = "data";
			
			if(!imageTypeString.equals(""))
			{
				pathString += imageTypeString + id + ".zip";
			}
			
			System.out.println("pathString :: " + pathString);
			
			File file = new File(pathString);
			long size = file.length();

			if (file.exists()) {
				logger.debug("Download Path :: " + pathString);
				logger.debug("Size :: " + size);
				// System.out.println("Size :: " + size);

				// response.setContentType(MediaType.IMAGE_PNG_VALUE);
				response.setContentType("application/octet-stream");
				response.setContentLength((int) size);
		        response.setHeader("Content-Disposition","attachment; filename=\""+imageTypeString+id+".zip\"");
				FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());

			}

		} catch (Exception e) {
			logger.error("downloadImageZipFile", e);
			throw e;
		}
		
	}
	
	@RequestMapping("totalImageZipFile")
	public @ResponseBody MobileResponseModel<TotalZipFile> totalImageZipFile(HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<TotalZipFile> model = new MobileResponseModel<TotalZipFile>();
		try{					
			logger.info("User: "+detail.getUserId()+" Data sync syncZipProductsImage start");
			
			List<TotalZipFile> data = new ArrayList<TotalZipFile>(); 
			
			String fileBaseLoc = configService.getFileBaseLoc();
			String pathString = fileBaseLoc + "/download/";

	        int count = new File(pathString).list().length;
	        
	        TotalZipFile totalZipFile = new TotalZipFile();
	        totalZipFile.setTotal(count);
	        data.add(totalZipFile);
	        
			model.setData(data);
			model.setMessage("Data Sync Successfully");  
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync totalImageZipFile end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync totalImageZipFile failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
		
	}
	
	@RequestMapping("syncZipOutletsImage")
	//public @ResponseBody MobileResponseModel<OutletSyncData> syncZipOutletsImage(@RequestParam("endDate") String endDate, HttpServletResponse response){
	public @ResponseBody MobileResponseModel<OutletSyncData> syncZipOutletsImage(HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletSyncData> model = new MobileResponseModel<OutletSyncData>();
		try{					
			logger.info("User: "+detail.getUserId()+" Data sync syncZipOutletsImage start");
			//List<OutletSyncData> data = outletService.syncOutletData(syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getOutletIds());
			

			SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			String end = dft.format(calendar.getTime()) + " 23:59:59";
			
			List<OutletSyncData> data = outletService.syncZipOutletsImage(end);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncZipOutletsImage end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncZipOutletsImage failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
		
	}
	
	@RequestMapping("syncZipOutletsAttachmentImage")
	public @ResponseBody MobileResponseModel<OutletAttachmentZipImageSyncData> syncZipOutletsAttachmentImage(HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<OutletAttachmentZipImageSyncData> model = new MobileResponseModel<OutletAttachmentZipImageSyncData>();
		try{					
			logger.info("User: "+detail.getUserId()+" Data sync syncZipOutletsAttachmentImage start");
			//List<OutletSyncData> data = outletService.syncOutletData(syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getOutletIds());

			SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			String end = dft.format(calendar.getTime()) + " 23:59:59";
			
			List<OutletAttachmentZipImageSyncData> data = outletService.syncZipOutletsAttachmentImage(end);
			model.setData(data);
			model.setMessage("Data Sync Successfully");
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncZipOutletsAttachmentImage end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncZipOutletsAttachmentImage failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
		
	}
	
	@RequestMapping("syncZipProductsImage")
	public @ResponseBody MobileResponseModel<ProductZipImageSyncData> syncZipProductsImage(HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		MobileResponseModel<ProductZipImageSyncData> model = new MobileResponseModel<ProductZipImageSyncData>();
		try{					
			logger.info("User: "+detail.getUserId()+" Data sync syncZipProductsImage start");
			//List<OutletSyncData> data = outletService.syncOutletData(syncModel.getData(), syncModel.getLastSyncTime(), syncModel.getDataReturn(), syncModel.getOutletIds());
			


			SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, -1);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			String end = dft.format(calendar.getTime()) + " 23:59:59";
			
			List<ProductZipImageSyncData> data = productService.syncZipProductImage(end);
			model.setData(data);
			model.setMessage("Data Sync Successfully");  
			model.setStatus(LoginResultModel.SUCCESS);
			model.setStatusString("SUCCESS");
			logger.info("User: "+detail.getUserId()+" Data sync syncZipProductsImage end");
		}
		catch(Exception ex){
			logger.error("User: "+detail.getUserId()+" Data sync syncZipProductsImage failure: ", ex);
			model.setMessage(ex.getMessage());
			model.setStatus(LoginResultModel.FAIL);
			model.setStatusString("FAIL");
		}		
		return model;	
		
	}
	
	//
}
