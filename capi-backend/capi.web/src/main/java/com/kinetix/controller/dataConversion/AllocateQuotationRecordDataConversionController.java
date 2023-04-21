package com.kinetix.controller.dataConversion;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.IndoorQuotationRecord;
import capi.entity.Purpose;
import capi.entity.Quotation;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.dataConversion.quotationRecordDataConversion.AllocateQuotationRecordDataConversionCountModel;
import capi.model.dataConversion.quotationRecordDataConversion.AllocateQuotationRecordDataConversionTableListModel;
import capi.model.dataConversion.quotationRecordDataConversion.PurposeIndoorQuotationRecordCountModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionSessionModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.model.assignmentManagement.QuotationRecordHistoryDateModel;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.assignmentManagement.QuotationService;
import capi.service.dataConversion.QuotationRecordDataConversionService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;

import com.kinetix.component.FuncCode;
import com.kinetix.controller.assignmentAllocationAndReallocation.SurveyMonthController;

/**
* UF-2102 AllocateQuotationRecordDataConversion
*/
@Secured("UF2102")
@FuncCode("UF2102")
@Controller("AllocateQuotationRecordDataConversionController")
@RequestMapping("dataConversion/AllocateQuotationRecordDataConversion")
@SessionAttributes({"quotationRecordSession"})
public class AllocateQuotationRecordDataConversionController {
private static final Logger logger = LoggerFactory.getLogger(SurveyMonthController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private QuotationRecordDataConversionService service;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired
	private UnitService unitService;

	@Autowired
	private BatchService batchService;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private QuotationService quotationService;
	
	@Autowired
	private PurposeService purposeService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * Init model attribute to session
	 */
	@ModelAttribute("quotationRecordSession")
    public QuotationRecordDataConversionSessionModel initQuotationRecordSession() {
        return new QuotationRecordDataConversionSessionModel();
    }
	
	/**
	 * QuotationRecordDataConversion home
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth) {
		try {
			
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	@RequestMapping(value = "getSurveyMonthDetails", method = RequestMethod.POST)
	public String getSurveyMonthDetails(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "refMonthStr", required = true) String refMonthStr){
		
		try {
			Date refMonth = commonService.getMonth(refMonthStr);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer userId = detail.getUserId();
			List<PurposeIndoorQuotationRecordCountModel> purposeList = this.service.getPurposeIndoorQuotationRecordCountsByBatches(refMonth, new String[]{ "Allocation", "Conversion", "Reject Verification" } , null, userId);
			
			model.addAttribute("purposeList", purposeList);
			model.addAttribute("refMonth", refMonth);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		
		return "shared/indoorQuotationRecord/partial/referenceMonthDetails";
	}
	
	@RequestMapping(value = "selectSurveyType", method = {RequestMethod.POST, RequestMethod.GET})
	public void selectSurveyType(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "referenceMonthStr", required = true) String referenceMonthStr,
			@RequestParam(value = "purposeId", required = true) Integer purposeId){
		
		try {
			sessionStatus.setComplete();
			session = new QuotationRecordDataConversionSessionModel();
			Purpose purpose = this.service.findPurpose(purposeId);
			
			List<User> users = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_INDOOR_DATA_CONVERSION);
			List<String> surveyForms = batchService.getSurveyForm();
			
			model.addAttribute("referenceMonthStr", referenceMonthStr);
			model.addAttribute("purpose", purpose);
			model.addAttribute("users", users);
			model.addAttribute("surveyForms", surveyForms);
			
		} catch (Exception e) {
			logger.error("selectSurveyType", e);
		}
	
	}
	
	/**
	 * Get group select format
	 */
	@RequestMapping(value = "queryBatchCodeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryBatchCodeSelect2(Select2RequestModel requestModel, String cpiBasePeriod) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return this.batchService.queryBatchByUserSelect2(requestModel, detail.getUserId());
		} catch (Exception e) {
			logger.error("querySubGroupSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Indoor Allocation Code select format
	 */
	@RequestMapping(value = "queryIndoorAllocationCodeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryIndoorAllocationCodeSelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
			return quotationService.queryIndoorAllocationCodeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryIndoorAllocationCodeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPurposeSelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get group select format
	 */
	@RequestMapping(value = "queryGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryGroupSelect2(Select2RequestModel requestModel, String cpiBasePeriod) {
		try {
			return unitService.queryGroupSelect2(requestModel, cpiBasePeriod, "");
		} catch (Exception e) {
			logger.error("queryGroupSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get group select format
	 */
	@RequestMapping(value = "querySubGroupSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel querySubGroupSelect2(Select2RequestModel requestModel, String cpiBasePeriod) {
		try {
			return this.unitService.querySubGroupSelect2(requestModel, cpiBasePeriod, "");
		} catch (Exception e) {
			logger.error("querySubGroupSelect2", e);
		}
		return null;
	}

	/**
	 * Get item select format
	 */
	@RequestMapping(value = "queryItemSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryItemSelect2(Select2RequestModel requestModel, String cpiBasePeriod) {
		try {
			return unitService.queryItemSelect2(requestModel, cpiBasePeriod, null);
		} catch (Exception e) {
			logger.error("queryItemSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get outlet type select format
	 */
	@RequestMapping(value = "queryOutletTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<AllocateQuotationRecordDataConversionTableListModel>
		query(Locale locale,
				Model model,
				SessionStatus sessionStatus,
				@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
				DatatableRequestModel requestModel) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return service.queryAllocationIndoorQuotationRecordTableList(requestModel, session, detail.getUserId());
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * QuotationRecordDataConversion edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@RequestParam(value = "id", required = false) Integer indoorQuotationRecordId) {
		try {
			IndoorQuotationRecord iqr = this.service.getIndoorQuotationRecord(indoorQuotationRecordId);
			model.addAttribute("indoorQuotationRecord", iqr);
			User user = iqr.getUser();
			model.addAttribute("user", user);
			Quotation q = iqr.getQuotation();
			model.addAttribute("quotation", q);
			Unit unit = iqr.getQuotation().getUnit();
			model.addAttribute("unit", unit);
			Purpose purpose = unit.getPurpose();
			model.addAttribute("purpose", purpose);
			String refMonthStr = commonService.formatMonth(iqr.getReferenceMonth());
			model.addAttribute("refMonthStr", refMonthStr);
			PageViewModel quotationRecordPageViewModel = null;
			if(iqr.getQuotationRecord() != null)
				quotationRecordPageViewModel = this.service.prepareQuotationRecordViewModel(iqr.getQuotationRecord().getId());
			else{
				quotationRecordPageViewModel = this.service.prepareQuotationRecordViewModelByQuotaionId(iqr.getQuotation().getId());
			}

			boolean isHiddenFieldOfficer = iqr.getStatus() != null ? !(commonService.stringEquals(iqr.getStatus(), "Conversion") || commonService.stringEquals(iqr.getStatus(), "Reject Verification") || 
					commonService.stringEquals(iqr.getStatus(), "Allocation") || commonService.stringEquals(iqr.getStatus(), "Complete")) : true ;
			if (quotationRecordPageViewModel.getHistories() != null){
				for(QuotationRecordHistoryDateModel qrDateModel : quotationRecordPageViewModel.getHistories()){
					qrDateModel.setIsHiddenFieldOfficer(isHiddenFieldOfficer);
				}
			}
			quotationRecordPageViewModel.setIsHiddenFieldOfficer(isHiddenFieldOfficer);
			
			Double average = (double) 0;
			if(quotationRecordPageViewModel.getQuotationRecord().getFormDisplay() != null && quotationRecordPageViewModel.getQuotationRecord().getFormDisplay() != 1 && quotationRecordPageViewModel.getQuotationRecord().getTourRecord() != null){
				average = this.service.calTourRecordAveragePrice(quotationRecordPageViewModel.getQuotationRecord().getTourRecord());
			}
			model.addAttribute("average", average);
				
			model.addAttribute("model", quotationRecordPageViewModel);
			model.addAttribute("session", session);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * QuotationRecordDataConversion edit
	 */
	@PreAuthorize("hasPermission(#user, 32) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@RequestParam(value = "indoorQuotationRecordId", required = true) Integer indoorQuotationRecordId,
			@RequestParam(value = "userId", required = true) Integer userId) {
		try {
			String redirParam = this.service.updateRecordUser(indoorQuotationRecordId, userId);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/dataConversion/AllocateQuotationRecordDataConversion/selectSurveyType?" + redirParam;
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			return "redirect:/dataConversion/AllocateQuotationRecordDataConversion/edit?id="+indoorQuotationRecordId;
		}
	}
	
	@RequestMapping(value = "allocateSelected", method = RequestMethod.POST)
	public String allocateSelected(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@RequestParam(value = "indoorQuotationRecordId", required = true) List<Integer> indoorQuotationRecordId,
			@RequestParam(value = "userId", required = true) Integer userId){
		try {
			service.updateRecordUser(indoorQuotationRecordId, userId);
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch (Exception e) {
			logger.error("home", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}		
		return "/partial/messageRibbons";
	}
	
	@RequestMapping(value = "allocateAll", method = RequestMethod.POST)
	public String allocateAll(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			@RequestParam(value = "userId", required = true) Integer userId){
		try {
			service.updateRecordUser(session.getIndoorQuotationRecordIds(), userId);
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch (Exception e) {
			logger.error("home", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}		
		return "/partial/messageRibbons";
	}
	
	@RequestMapping(value = "markCompleteSelected", method = RequestMethod.POST)
	public String markCompleteSelected(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@RequestParam(value = "indoorQuotationRecordId", required = true) List<Integer> indoorQuotationRecordId){
		try {
			service.markComplete(indoorQuotationRecordId);
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch (Exception e) {
			logger.error("home", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}		
		return "/partial/messageRibbons";
	}
	
	@RequestMapping(value = "markCompleteAll", method = RequestMethod.POST)
	public String markCompleteAll(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session){
		try {
			service.markComplete(session.getIndoorQuotationRecordIds());
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch (Exception e) {
			logger.error("home", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}		
		return "/partial/messageRibbons";
	}
	
	/**
	 * Get unit select format
	 */
	@RequestMapping(value = "queryUnitSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryUnitSelect2(Select2RequestModel requestModel) {
		try {
			return unitService.queryUnitSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryUnitSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single unit
	 */
	@RequestMapping(value = "queryUnitSelectSingle")
	public @ResponseBody List<KeyValueModel> queryUnitSelectSingle(@RequestParam(value = "id[]") Integer[] id) {
		try {
			return unitService.getKeyValueByIds(id);
		} catch (Exception e) {
			logger.error("getKeyValueByIds", e);
		}
		return null;
	}
	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryQuotationSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryQuotationSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
			@RequestParam(value = "unitId[]", required = false) Integer[] unitId) {
		try {
			return quotationService.queryQuotationSelect2(requestModel, purposeId, unitId);
		} catch (Exception e) {
			logger.error("queryQuotationSelect2", e);
		}
		return null;
	}

	@RequestMapping(value = "getCount", method = {RequestMethod.GET})
	public @ResponseBody AllocateQuotationRecordDataConversionCountModel getCount(
			@RequestParam(value = "referenceMonthStr", required = true) String referenceMonthStr,
			@RequestParam(value = "purposeId", required = true) Integer purposeId){
		
		try {
			Date refMonth = commonService.getMonth(referenceMonthStr);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			
			Long totalNoOfPending = this.service.countAllocateQuotationRecordConversion(refMonth, purposeId, new String[]{"Allocation"}, null, detail.getUserId());
			Long totalNoOfRecord = this.service.countAllocateQuotationRecordConversion(refMonth, purposeId, new String[]{"Allocation", "Conversion", "Reject Verification"}, null, detail.getUserId());
			
			AllocateQuotationRecordDataConversionCountModel model = new AllocateQuotationRecordDataConversionCountModel();
			model.setTotalNoOfPending(totalNoOfPending);
			model.setTotalNoOfRecord(totalNoOfRecord);
			return model;
		} catch (ParseException e) {
			logger.error("getCount", e);
		}
		return null;
	}
	
	@RequestMapping(value = "clearSelectedIndoorAllocatedOfficer", method = RequestMethod.POST)
	public String clearSelectedIndoorAllocatedOfficer(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@RequestParam(value = "indoorQuotationRecordId", required = true) List<Integer> indoorQuotationRecordId){
		try {
			service.clearIndoorAllocatedOfficer(indoorQuotationRecordId);
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch (Exception e) {
			logger.error("home", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}		
		return "/partial/messageRibbons";
	}
	
	@RequestMapping(value = "clearAllIndoorAllocatedOfficer", method = RequestMethod.POST)
	public String clearAllIndoorAllocatedOfficer(Model model,
			Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session){
		try {
			service.clearIndoorAllocatedOfficer(session.getIndoorQuotationRecordIds());
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch (Exception e) {
			logger.error("home", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}		
		return "/partial/messageRibbons";
	}
}

