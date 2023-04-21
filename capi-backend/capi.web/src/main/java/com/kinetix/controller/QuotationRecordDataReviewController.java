package com.kinetix.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import com.kinetix.component.FuncCode;

import capi.dal.QuotationRecordDao;
import capi.entity.ImputeQuotation;
import capi.entity.ImputeUnit;
import capi.entity.IndoorQuotationRecord;
import capi.entity.Purpose;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.Unit;
import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.dataConversion.quotationRecordDataConversion.PurposeIndoorQuotationRecordCountModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionSessionModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionTableListModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataReviewCountModel;
import capi.model.masterMaintenance.businessParameterMaintenance.DisplayModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentManagement.OnSpotValidationService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.dataConversion.QuotationRecordDataConversionService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.BusinessParameterService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.UnitService;

/**
* UF-2201 QuotationRecordDataConversion
*/
@Secured("UF2201")
@FuncCode("UF2201")
@Controller("QuotationRecordDataReviewController")
@RequestMapping("QuotationRecordDataReview")
@SessionAttributes({"quotationRecordSession"})
public class QuotationRecordDataReviewController {
	private static final Logger logger = LoggerFactory.getLogger(QuotationRecordDataReviewController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private QuotationRecordDataConversionService service;
	
	@Autowired
	private BusinessParameterService businessParameterService;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private BatchService batchService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private OnSpotValidationService onSpotValidationService;
	
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
			 Double average = (double) 0;
			 if(quotationRecordPageViewModel.getQuotationRecord().getFormDisplay() != null && quotationRecordPageViewModel.getQuotationRecord().getFormDisplay() != 1 && quotationRecordPageViewModel.getQuotationRecord().getTourRecord() != null){
				 average = this.service.calTourRecordAveragePrice(quotationRecordPageViewModel.getQuotationRecord().getTourRecord());
			 }
			 model.addAttribute("average", average);
			 model.addAttribute("model", quotationRecordPageViewModel);
			 model.addAttribute("session", session);
			 ImputeQuotation iq = this.service.getImputeQuotation(q.getId(), iqr.getReferenceMonth());
			 model.addAttribute("iq", iq);
			 ImputeQuotation preiq = this.service.getPreviousImputeQuotation(q.getId(), iqr.getReferenceMonth());
			 model.addAttribute("preiq", preiq);
			 ImputeUnit iu = this.service.getImputeUnit(unit.getId(), iqr.getReferenceMonth());
			 model.addAttribute("iu", iu);
			 ImputeUnit preiu = this.service.getPreviousImputeUnit(unit.getId(), iqr.getReferenceMonth());
			 model.addAttribute("preiu", preiu);
//			 IndoorQuotationRecord lastIqr = this.service.getFirstHistoryRecordByQuotationId(iqr);
//			 model.addAttribute("lastIndoorQuotationRecord", lastIqr);
			 
			 boolean isFRLocked = service.isFRLocked(iqr);
			 model.addAttribute("isFRLocked", isFRLocked);
			 
			 boolean isIP = service.isIP(iqr);
			 model.addAttribute("isIP", isIP);
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
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//			Integer userId = detail.getUserId();
			List<PurposeIndoorQuotationRecordCountModel> purposeList = this.service.getPurposeIndoorQuotationRecordCounts(refMonth, new String[]{"Complete"}, null, null);
			
			model.addAttribute("purposeList", purposeList);
			model.addAttribute("refMonth", refMonth);
			
		} catch (ParseException e) {
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
			Date refMonth = commonService.getMonth(referenceMonthStr);
			
			DisplayModel displayModel = businessParameterService.getParameters();
			Integer delinkPeriod;
			if (displayModel.getDelinkPeriod() != null) {
				delinkPeriod = Integer.parseInt(displayModel.getDelinkPeriod());
			} else {
				delinkPeriod = 24;
			}
			
			model.addAttribute("displayModel", displayModel);
			
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//			Integer userId = detail.getUserId();
			Long totalNoOfQuotation = this.service.countIndoorQuotation(refMonth, purposeId, new String[]{"Complete"}, null, null);
			Long totalNoOfOutlet = this.service.countIndoorQuotationOutlet(refMonth, purposeId, new String[]{"Complete"}, null, null);
			List<User> users = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_INDOOR_DATA_CONVERSION);
			
			List<String> surveyForms = batchService.getSurveyForm();
			
			model.addAttribute("delinkPeriod", delinkPeriod);
			model.addAttribute("referenceMonth", refMonth);
			model.addAttribute("referenceMonthStr", referenceMonthStr);
			model.addAttribute("purpose", purpose);
			model.addAttribute("totalNoOfQuotation", totalNoOfQuotation);
			model.addAttribute("totalNoOfOutlet", totalNoOfOutlet);
			model.addAttribute("surveyForms", surveyForms);
			model.addAttribute("users", users);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
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
	 * Get normal distinct unit category select format
	 */
	@RequestMapping(value = "queryValidDistinctUnitCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryValidDistinctUnitCategorySelect2(Select2RequestModel requestModel) {
		try {
			return this.unitService.queryValidDistinctUnitCategorySelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryValidDistinctUnitCategorySelect2", e);
		}
		return null;
	}
	
	/**
	 * Get outlet select format
	 */
	@RequestMapping(value = "queryOutletSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOutletSelect2(Select2RequestModel requestModel, @RequestParam(value = "q", required = false) String search) {
		try {
			if (requestModel.getTerm() == null) {
				requestModel.setTerm(search);
			}
			return outletService.queryOutletSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletSelect2", e);
		}
		return null;
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
	 * Get single outlet
	 */
	@RequestMapping(value = "queryOutletSelectSingle", produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryOutletSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return outletService.getOutletSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryOutletSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationRecordDataConversionTableListModel>
		query(Locale locale,
				Model model,
				SessionStatus sessionStatus,
				@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
				DatatableRequestModel requestModel) {
		try {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return service.queryIndoorQuotationRecordTableList(requestModel, session, null, "Complete");
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get history quotation record
	 */
	@RequestMapping(value = "getHistoryQuotationRecord", method = RequestMethod.GET)
	public String getHistoryQuotationRecord(Model model, @RequestParam("id") Integer id) {
		try {
			capi.model.shared.quotationRecord.PageViewModel viewModel = quotationRecordService.prepareViewModel(id, true, true);
			
			Double average = (double) 0;
			if(viewModel.getQuotationRecord().getFormDisplay() != null && viewModel.getQuotationRecord().getFormDisplay() != 1 && viewModel.getQuotationRecord().getTourRecord() != null){
				average = this.service.calTourRecordAveragePrice(viewModel.getQuotationRecord().getTourRecord());
			}
			model.addAttribute("average", average);
			 
			model.addAttribute("model", viewModel);
			return "shared/indoorQuotationRecord/partial/quotationRecord/editForm";
		} catch (Exception e) {
			logger.error("getHistoryQuotationRecord", e);
		}
		return null;
	}
	
	@RequestMapping(value = "getFrString", method = RequestMethod.POST)
	public @ResponseBody String getFrString(Model model, @RequestParam("indoorQuotationRecordId") Integer indoorQuotationRecordId) {
		 IndoorQuotationRecord iqr = this.service.getIndoorQuotationRecord(indoorQuotationRecordId);
		 Quotation q = iqr.getQuotation();
		 String response = "";
		 if(q.isUseFRAdmin() != null && q.isUseFRAdmin()){
			 response = "(Last date applied FR: "+commonService.formatDate(q.getLastFRAppliedDate())+") with FR="+q.getFrAdmin();
			 if(q.getIsFRAdminPercentage() != null && q.getIsFRAdminPercentage()){
				 response = response+"%"; 
			 }
		 }else{
			 response = "(Last date applied FR: "+commonService.formatDate(q.getLastFRAppliedDate())+") with FR="+q.getFrField();
			 if(q.getIsFRFieldPercentage() != null && q.getIsFRFieldPercentage()){
				 response = response+"%"; 
			 }
		 }
		
		 return response;
	}
	
	@RequestMapping(value = "getProductPartial", method = RequestMethod.POST)
	public String getProductPartial(Model model, @RequestParam("quotationRecordId") Integer quotationRecordId) {
		PageViewModel quotationRecordPageViewModel = this.service.prepareQuotationRecordViewModel(quotationRecordId);
		model.addAttribute("model", quotationRecordPageViewModel);
		return "shared/indoorQuotationRecord/partial/productDetails";
	}
	
	@RequestMapping(value = "getOutletPartial", method = RequestMethod.POST)
	public String getOutletPartial(Model model, @RequestParam("quotationRecordId") Integer quotationRecordId) {
		PageViewModel quotationRecordPageViewModel = this.service.prepareQuotationRecordViewModel(quotationRecordId);
		model.addAttribute("model", quotationRecordPageViewModel);
		return "shared/indoorQuotationRecord/partial/outletDetails";
	}
	
	@PreAuthorize("hasPermission(#user, 64) or hasPermission(#user, 256) or hasPermission(#user, 1024)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			IndoorQuotationRecord indoorQuotationRecord,
			String btnSubmit,
			String verification,
			String imputeQuotationPrice,
			String imputeQuotationPriceRemark,
			String imputeUnitPrice,
			String imputeUnitPriceRemark,
			String indoorRemark) {
		try {
			IndoorQuotationRecord entity = this.service.getIndoorQuotationRecord(indoorQuotationRecord.getIndoorQuotationRecordId());
			Quotation quotation = entity.getQuotation();
			Unit unit = quotation.getUnit();
			//check need update fr
			boolean isFRLocked = service.isFRLocked(entity);
			
			//Update Quotation IndoorRemark
			quotation.setOldFormSequence(indoorRemark);
			Boolean updateFr = false;
			if(unit.isFrRequired() && !isFRLocked){
				updateFr = true;
			}
			//end check need update fr
			//2018-01-11 cheung_cheng [B137] When approval the verified the records which have not indoor record, it will fail to update
			//check is the assignment having some without indoor quotation record 
			if(btnSubmit.equalsIgnoreCase("verification")){
				if(verification.equalsIgnoreCase("isFirmVerify")){
					boolean isAllowFirmVerify = quotationRecordService.isAllowFirmVerify(indoorQuotationRecord.getIndoorQuotationRecordId());
					if(!isAllowFirmVerify)
					{
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00170", null, locale));
						return "redirect:/QuotationRecordDataReview/edit?id="+indoorQuotationRecord.getId();
					}
				}
				//B158 Follow "Firm Verification" to fix Fail to update record
				else if("isCategoryVerify".equalsIgnoreCase(verification)){
					boolean isAllowCategoryVerify = quotationRecordService.isAllowFirmVerify(indoorQuotationRecord.getIndoorQuotationRecordId());
					if(!isAllowCategoryVerify)
					{
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00170", null, locale));
						return "redirect:/QuotationRecordDataReview/edit?id="+indoorQuotationRecord.getId();
					}
				}
			}

			// 2020-07-16: validate N/S price > 0, cannot be <= 0
			// PIR-239: indoor QR batch job failed, if some N/S price = 0, due to some calculation divide-by-zero
			List<String> errorMessages = new ArrayList<String>();
			
			// mock QR with quotation for ad-hoc use of On Spot Validation
			QuotationRecord qrForValidation = new QuotationRecord();
			qrForValidation.setQuotation(quotation);
			
			//form binding
			if(indoorQuotationRecord.isNullCurrentNPrice()){
				entity.setNullCurrentNPrice(true);
				entity.setCurrentNPrice(null);
			}else{
				qrForValidation.setnPrice(indoorQuotationRecord.getCurrentNPrice());
				// NPriceGreaterZero returns true if N price <= 0, NOT > 0... ok...
				if (onSpotValidationService.NPriceGreaterZero(qrForValidation)) {
					errorMessages.add("Current " + onSpotValidationService.getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3));
				} else {
					entity.setNullCurrentNPrice(false);
					entity.setCurrentNPrice(indoorQuotationRecord.getCurrentNPrice());
				}
			}
			
			if(indoorQuotationRecord.isNullCurrentSPrice()){
				entity.setNullCurrentSPrice(true);
				entity.setCurrentSPrice(null);
			}else{
				qrForValidation.setsPrice(indoorQuotationRecord.getCurrentSPrice());
				// SPriceGreaterZero returns true if S price <= 0, NOT > 0... ok...
				if (onSpotValidationService.SPriceGreaterZero(qrForValidation)) {
					errorMessages.add("Current " + onSpotValidationService.getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4));
				} else {
					entity.setNullCurrentSPrice(false);
					entity.setCurrentSPrice(indoorQuotationRecord.getCurrentSPrice());
				}
			}

			if(indoorQuotationRecord.isNullPreviousNPrice()){
				entity.setNullPreviousNPrice(true);
				entity.setPreviousNPrice(null);
			}else{
				qrForValidation.setnPrice(indoorQuotationRecord.getPreviousNPrice());
				// NPriceGreaterZero returns true if N price <= 0, NOT > 0... ok...
				if (onSpotValidationService.NPriceGreaterZero(qrForValidation)) {
					errorMessages.add("Previous " + onSpotValidationService.getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3));
				} else {
					entity.setNullPreviousNPrice(false);
					entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
				}
			}
			
			if(indoorQuotationRecord.isNullPreviousSPrice()){
				entity.setNullPreviousSPrice(true);
				entity.setPreviousSPrice(null);
			}else{
				qrForValidation.setsPrice(indoorQuotationRecord.getPreviousSPrice());
				// SPriceGreaterZero returns true if S price <= 0, NOT > 0... ok...
				if (onSpotValidationService.SPriceGreaterZero(qrForValidation)) {
					errorMessages.add("Previous " + onSpotValidationService.getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4));
				} else {
					entity.setNullPreviousSPrice(false);
					entity.setPreviousSPrice(indoorQuotationRecord.getPreviousSPrice());
				}
			}
			entity.setRemark(indoorQuotationRecord.getRemark());
			
			//end form binding

			if (errorMessages.size() > 0) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, StringUtils.join(errorMessages, "<br>"));
				return "redirect:/QuotationRecordDataReview/edit?id="+indoorQuotationRecord.getId();
			}
			
			//calculation logic
			if(updateFr && indoorQuotationRecord.isApplyFR()){
				entity.setApplyFR(true);
				entity.setFRPercentage(indoorQuotationRecord.isFRPercentage());
				entity.setFr(indoorQuotationRecord.getFr());
				quotation.setLastFRAppliedDate(commonService.getDate(commonService.formatDate(new Date())));
				
				entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
				entity.setPreviousSPrice(indoorQuotationRecord.getPreviousSPrice());
			}
			//end calculation logic
			
			//update fr logic
			// Acting. Please follow
			if(updateFr && indoorQuotationRecord.isApplyFR()){
				quotation.setFrAdmin(indoorQuotationRecord.getFr());
				quotation.setFrField(indoorQuotationRecord.getFr());
				if(indoorQuotationRecord.isFRPercentage()){
					quotation.setIsFRAdminPercentage(true);
					quotation.setIsFRFieldPercentage(true);
				}else{
					quotation.setIsFRAdminPercentage(false);
					quotation.setIsFRFieldPercentage(false);
				}
				quotation.setFRApplied(true);
			}else if(updateFr && !indoorQuotationRecord.isApplyFR()){
				// Acting. Please follow
				quotation.setFrAdmin(quotation.getTempFRValue());
				quotation.setFrField(quotation.getTempFRValue());
				quotation.setLastFRAppliedDate(quotation.getTempLastFRAppliedDate());
				quotation.setFRApplied(false);
				quotation.setIsFRAdminPercentage(quotation.isTempFRPercentage());
				quotation.setIsFRFieldPercentage(quotation.isTempFRPercentage());
			}
			if (updateFr) {
				entity.setApplyFR(indoorQuotationRecord.isApplyFR());
				if(!indoorQuotationRecord.isApplyFR()){
					entity.setFr(null);
					entity.setFRPercentage(false);
				}
			}
			//end update fr logic
			
			//spicing logic
			if(indoorQuotationRecord.isSpicing()){
				entity.setSpicing(true);
				entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
				entity.setPreviousSPrice(indoorQuotationRecord.getPreviousSPrice());
			}else{
				entity.setSpicing(false);
				entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
				entity.setPreviousSPrice(indoorQuotationRecord.getPreviousSPrice());
			}
			//end spicing logic
			
			if (verification != null && !(entity.isRUA()||entity.isNoField())) {
				if(verification.equalsIgnoreCase("isFirmVerify")){
					entity.setFirmVerify(true);
					entity.setCategoryVerify(false);
					entity.setQuotationVerify(false);
					
					entity.setFirmRemark(indoorQuotationRecord.getFirmRemark());
					entity.setCategoryRemark(null);
					entity.setQuotationRemark(null);
				}else if(verification.equalsIgnoreCase("isCategoryVerify")){
					entity.setFirmVerify(false);
					entity.setCategoryVerify(true);
					entity.setQuotationVerify(false);
					
					entity.setFirmRemark(null);
					entity.setCategoryRemark(indoorQuotationRecord.getCategoryRemark());
					entity.setQuotationRemark(null);
				}else if(verification.equalsIgnoreCase("isQuotationVerify")){
					entity.setFirmVerify(false);
					entity.setCategoryVerify(false);
					entity.setQuotationVerify(true);
					
					entity.setFirmRemark(null);
					entity.setCategoryRemark(null);
					entity.setQuotationRemark(indoorQuotationRecord.getQuotationRemark());
				}
			} else {
				entity.setFirmVerify(false);
				entity.setCategoryVerify(false);
				entity.setQuotationVerify(false);

				entity.setFirmRemark(null);
				entity.setCategoryRemark(null);
				entity.setQuotationRemark(null);
			}
			
			if(indoorQuotationRecord.isOutlier()){
				entity.setOutlier(true);
				entity.setOutlierRemark(indoorQuotationRecord.getOutlierRemark());
			}else{
				entity.setOutlier(false);
				entity.setOutlierRemark(null);
			}
			
			ImputeQuotation iq = this.service.getImputeQuotation(quotation.getId(), entity.getReferenceMonth());
			if(iq == null){
				iq = new ImputeQuotation();
				iq.setQuotation(quotation);
				iq.setReferenceMonth(entity.getReferenceMonth());
			}
			Double iqp = null;
			try{
				iqp = Double.valueOf(imputeQuotationPrice);
			}catch(Exception e){
				
			}
			iq.setPrice(iqp);
			iq.setRemark(imputeQuotationPriceRemark);
			
			ImputeUnit iu = this.service.getImputeUnit(unit.getId(), entity.getReferenceMonth());
			if(iu == null){
				iu = new ImputeUnit();
				iu.setUnit(unit);
				iu.setReferenceMonth(entity.getReferenceMonth());
			}
			Double iup = null;
			try{
				iup = Double.valueOf(imputeUnitPrice);
			}catch(Exception e){
				
			}
			iu.setPrice(iup);
			iu.setRemark(imputeUnitPriceRemark);
			

			if(btnSubmit.equalsIgnoreCase("verification") && !(entity.isRUA()||entity.isNoField())){
				entity.setStatus("Review Verification");
				String remark = "";
				List<QuotationRecord> qrList = new ArrayList<QuotationRecord>();
				List<QuotationRecord> removeList = new ArrayList<QuotationRecord>();
				if(verification.equalsIgnoreCase("isFirmVerify")){
					if (entity.getQuotationRecord() != null) {
						qrList = new ArrayList<QuotationRecord>(entity.getQuotationRecord().getAssignment().getQuotationRecords());
					}
					for(QuotationRecord remove : qrList){
						if ((remove.getIndoorQuotationRecord()!=null 
								&& remove.getIndoorQuotationRecord().isRUA()) || remove.isBackNo()){
							removeList.add(remove);
						}
					}
					remark = indoorQuotationRecord.getFirmRemark();
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
					remark = indoorQuotationRecord.getCategoryRemark();
				}else if(verification.equalsIgnoreCase("isQuotationVerify") && !entity.isRUA()){
					if(entity.getQuotationRecord() != null){
						qrList.add(entity.getQuotationRecord());
					}
					remark = indoorQuotationRecord.getQuotationRemark();
				}
				qrList.removeAll(removeList);
				Iterator<QuotationRecord> itr = qrList.iterator();
				while (itr.hasNext()) {
					QuotationRecord qr = itr.next();
					if (qr.getQuotationState().equals("IP") || qr.getAvailability() == 2) {
						itr.remove();
					}
				}
				
				for(QuotationRecord quotationRecord : qrList){
					quotationRecord.setStatus("Draft");
					quotationRecord.setQuotationState("Verify");
					quotationRecord.setVisited(false);
					//quotationRecord.setRemark(quotationRecord.getRemark()+"\n"+remark);
					
					quotationRecord.setNewRecruitment(false);
					if (quotationRecord.getOtherQuotationRecords()!=null){
						for (QuotationRecord backno : quotationRecord.getOtherQuotationRecords()){
							if (backno.isBackNo()){
								backno.setNewRecruitment(false);
							}
						}
					}
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
						quotationRecord.getIndoorQuotationRecord().setStatus("Review Verification");
						quotationRecord.getIndoorQuotationRecord().setFirmVerify(entity.isFirmVerify());
						quotationRecord.getIndoorQuotationRecord().setCategoryVerify(entity.isCategoryVerify());
						quotationRecord.getIndoorQuotationRecord().setQuotationVerify(entity.isQuotationVerify());
						
						quotationRecord.getIndoorQuotationRecord().setFirmRemark(entity.getFirmRemark());
						quotationRecord.getIndoorQuotationRecord().setCategoryRemark(entity.getCategoryRemark());
						quotationRecord.getIndoorQuotationRecord().setQuotationRemark(entity.getQuotationRemark());
					}
					
					this.quotationRecordService.saveRecord(quotationRecord);
				}
				
				service.createVerificationHistory(verification, entity.getReferenceMonth(), entity.getQuotationRecord().getAssignment().getUser(), qrList);
				
				service.updateIndoorStatusInQuotationRecordList(qrList, entity.getStatus());
			}
			if(btnSubmit.equalsIgnoreCase("reallocate")){
				entity.setStatus("Conversion");
			}
			
			this.service.saveImputeQuotation(iq);
			this.service.saveImputeUnit(iu);
			
			this.service.updateRecord(entity, quotation);
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/QuotationRecordDataReview/selectSurveyType?purposeId="+unit.getPurpose().getId()+"&referenceMonthStr="+commonService.formatMonth(entity.getReferenceMonth());
			
		} catch (Exception e) {
			logger.error("queryValidDistinctUnitCategorySelect2", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/QuotationRecordDataReview/edit?id="+indoorQuotationRecord.getId();
	}
	
	/**
	 * Flag
	 */
	@RequestMapping(value = "flag")
	public String flag(@RequestParam(value = "id") ArrayList<Integer> id, Boolean flag, Model model, Locale locale) {
		try {
			if (!service.toggleFlag(id, flag)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));				
			}
			else{
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}	
		} catch (Exception e) {
			logger.error("flag", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
	
	/**
	 * revisit
	 */
	@RequestMapping(value = "revisit")
	public String revisit(
			@RequestParam(value = "indoorAllocationRecordId") Integer indoorAllocationRecordId,
			@RequestParam(value = "startDateStr", required = false) String startDateStr,
			@RequestParam(value = "endDateStr", required = false) String endDateStr,
			@RequestParam(value = "collectionDateStr", required = false) String collectionDateStr,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			
			Date startDate = null;
			Date endDate = null;
			Date collectionDate = null;
			if(StringUtils.isEmpty(collectionDateStr)){
				startDate = this.commonService.getDate(startDateStr);
				endDate = this.commonService.getDate(endDateStr);
			} else {
				collectionDate = this.commonService.getDate(collectionDateStr);
			}
			
			if(collectionDate == null && (startDate == null || endDate == null)){
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
				return "redirect:/QuotationRecordDataReview/edit?id="+indoorAllocationRecordId;
			}
			
			IndoorQuotationRecord entity = this.service.saveRevisit(indoorAllocationRecordId, startDate, endDate, collectionDate);
			Unit unit = entity.getQuotation().getUnit();
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/QuotationRecordDataReview/selectSurveyType?purposeId="+unit.getPurpose().getId()+"&referenceMonthStr="+commonService.formatMonth(entity.getReferenceMonth());
			
		} catch (Exception e) {
			logger.error("revisit", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/QuotationRecordDataReview/home";
	}
	
	/**
	 * reallocate
	 */
	@RequestMapping(value = "reallocate")
	public String reallocate(
			@RequestParam(value = "id") ArrayList<Integer> id,
			Model model, Locale locale) {
		try {
			
			if(this.service.saveAllocate(id)){
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}else{
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));	
			}
			
		} catch (Exception e) {
			logger.error("reallocate", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
	
	/**
	 * reallocate Selected
	 */
	@RequestMapping(value = "reallocateIds")
	public String reallocateIds(
			@RequestParam(value = "id") ArrayList<Integer> id,
			Model model, Locale locale) {
		try {
			if(this.service.saveAllocate(id)){
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}else{
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));	
			}
			
		} catch (Exception e) {
			logger.error("reallocateIds", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
	
	/**
	 * reallocate Selected
	 */
	@RequestMapping(value = "reallocateId")
	public String reallocateId(
			@RequestParam(value = "id") Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(id);
			if(this.service.saveAllocate(ids)){
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}else{
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
			}
			
			IndoorQuotationRecord entity = this.service.getIndoorQuotationRecord(id);
			Unit unit = entity.getQuotation().getUnit();
			
			return "redirect:/QuotationRecordDataReview/selectSurveyType?purposeId="+unit.getPurpose().getId()+"&referenceMonthStr="+commonService.formatMonth(entity.getReferenceMonth());
			
		} catch (Exception e) {
			logger.error("reallocateId", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/QuotationRecordDataReview/home";
	}
	
	/**
	 * reallocate all
	 */
	@RequestMapping(value = "reallocateAll")
	public String reallocateAll(
			Model model, Locale locale,
			SessionStatus sessionStatus,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session) {
		try {
			
			if(this.service.saveAllocate(session.getIndoorQuotationRecordIds())){
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}else{
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));	
			}
			
		} catch (Exception e) {
			logger.error("reallocateAll", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
	
	/**
	 * revisitSelected
	 */
	@RequestMapping(value = "revisitSelected")
	public String revisitSelected(
			@RequestParam(value = "id") ArrayList<Integer> id,
			@RequestParam(value = "startDateStr", required = false) String startDateStr,
			@RequestParam(value = "endDateStr", required = false) String endDateStr,
			@RequestParam(value = "collectionDateStr", required = false) String collectionDateStr,
			Model model, Locale locale) {
		try {
			Date startDate = null;
			Date endDate = null;
			Date collectionDate = null;
			
			if(StringUtils.isEmpty(collectionDateStr)){
				startDate = this.commonService.getDate(startDateStr);
				endDate = this.commonService.getDate(endDateStr);
			} else {
				collectionDate = this.commonService.getDate(collectionDateStr);
			}
			if( this.service.saveRevisitIds(id, startDate, endDate, collectionDate)){
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}else{
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));	
			}
		} catch (Exception e) {
			logger.error("revisitSelected", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
	
	/**
	 * revisitAll
	 */
	@RequestMapping(value = "revisitAll")
	public String revisitAll(
			SessionStatus sessionStatus,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			@RequestParam(value = "startDateStr", required = false) String startDateStr,
			@RequestParam(value = "endDateStr", required = false) String endDateStr,
			@RequestParam(value = "collectionDateStr", required = false) String collectionDateStr,
			Model model, Locale locale) {
		try {
			Date startDate = null;
			Date endDate = null;
			Date collectionDate = null;
			
			if(StringUtils.isEmpty(collectionDateStr)){
				startDate = this.commonService.getDate(startDateStr);
				endDate = this.commonService.getDate(endDateStr);
			} else {
				collectionDate = this.commonService.getDate(collectionDateStr);
			}
			if( this.service.saveRevisitIds(session.getIndoorQuotationRecordIds(), startDate, endDate, collectionDate)){
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}else{
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));	
			}
		} catch (Exception e) {
			logger.error("revisitAll", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
	
	/**
	 * revisitAll
	 */
	@RequestMapping(value = "delink")
	public String delink(
			@RequestParam(value = "refMonthStr") String refMonthStr,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Date refMonth = this.commonService.getMonth(refMonthStr);
			if( this.service.delink(refMonth)){
				//model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00012", null, locale));
			}else{
				//model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("delink", e);
			//model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		//return "/partial/messageRibbons";
		return "redirect:/QuotationRecordDataReview/home";
	}
	
	/**
	 * fetchIdList
	 */
	@RequestMapping(value = "fetchIdList")
	public String fetchIdList(Locale locale,
			Model model,
			SessionStatus sessionStatus,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			DatatableRequestModel requestModel){
		
		this.service.getReviewTableIds(requestModel, session,  null, "Complete");
		
		return "redirect:/QuotationRecordDataReview/home";
	}
	
	@RequestMapping(value = "getCount", method = {RequestMethod.GET})
	public @ResponseBody QuotationRecordDataReviewCountModel getCount(
			@RequestParam(value = "referenceMonthStr", required = true) String referenceMonthStr,
			@RequestParam(value = "purposeId", required = true) Integer purposeId){
		
		try {
			Date refMonth = commonService.getMonth(referenceMonthStr);
			
			Long totalNoOfQuotation = this.service.countIndoorQuotation(refMonth, purposeId, new String[]{"Complete"}, null, null);
			Long totalNoOfOutlet = this.service.countIndoorQuotationOutlet(refMonth, purposeId, new String[]{"Complete"}, null, null);
			
			QuotationRecordDataReviewCountModel model = new QuotationRecordDataReviewCountModel();
			model.setTotalNoOfQuotation(totalNoOfQuotation);
			model.setTotalNoOfOutlet(totalNoOfOutlet);
			
			return model;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get discount select format
	 */
	@RequestMapping(value = "queryDiscountSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryDiscountSelect2(Select2RequestModel requestModel) {
		try {
			return quotationRecordService.queryDiscountSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDiscountSelect2", e);
		}
		return null;
	}

	/**
	 * Get quotation id select format
	 */
	@RequestMapping(value = "queryQuotationIdSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryQuotationIdSelect2(Select2RequestModel requestModel) {
		try {
			return service.queryQuotationIdSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryQuotationIdSelect2", e);
		}
		return null;
	}

}
