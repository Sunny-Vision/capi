package com.kinetix.controller.dataConversion;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import capi.entity.QuotationRecord;
import capi.entity.Unit;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.dataConversion.quotationRecordDataConversion.PurposeIndoorQuotationRecordCountModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionSessionModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionTableListModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.service.CommonService;
import capi.service.assignmentManagement.OnSpotValidationService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.dataConversion.QuotationRecordDataConversionService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.UnitService;

import com.kinetix.component.FuncCode;
import com.kinetix.controller.assignmentAllocationAndReallocation.SurveyMonthController;

/**
* UF-2101 QuotationRecordDataConversion
*/
@Secured("UF2101")
@FuncCode("UF2101")
@Controller("QuotationRecordDataConversionController")
@RequestMapping("dataConversion/QuotationRecordDataConversion")
@SessionAttributes({"quotationRecordSession"})
public class QuotationRecordDataConversionController {
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
	private QuotationRecordService quotationRecordService;

	@Autowired
	private BatchService batchService;

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
			Authentication auth,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session) {
		try {
			sessionStatus.setComplete();
			session = new QuotationRecordDataConversionSessionModel();
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
//			IndoorQuotationRecord lastIqr = this.service.getFirstHistoryRecordByQuotationId(iqr);
//			model.addAttribute("lastIndoorQuotationRecord", lastIqr);
			
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
			 
			boolean isFRLocked = service.isFRLocked(iqr);
			model.addAttribute("isFRLocked", isFRLocked);
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
			Date refMonth = commonService.getDate("01-"+refMonthStr);
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer userId = detail.getUserId();
			
			List<PurposeIndoorQuotationRecordCountModel> purposeList = this.service.getPurposeIndoorQuotationRecordCounts(refMonth, new String[]{"Conversion", "Reject Verification"}, null, userId);
			
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
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "referenceMonthStr", required = true) String referenceMonthStr,
			@RequestParam(value = "purposeId", required = true) Integer purposeId){
		
		try {
			DatatableRequestModel lastRequestModel = session.getLastRequestModel();
			model.addAttribute("viewModel", service.prepareConversionVideModel(lastRequestModel));
			
			Purpose purpose = this.service.findPurpose(purposeId);
			Date refMonth = commonService.getDate("01-"+referenceMonthStr);
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			Integer userId = detail.getUserId();
			Long totalNoOfQuotation = this.service.countIndoorQuotation(refMonth, purposeId, new String[]{"Conversion","Reject Verification"}, null, userId);
			Long totalNoOfOutlet = this.service.countIndoorQuotationOutlet(refMonth, purposeId, new String[]{"Conversion","Reject Verification"}, null, userId);
			List<String> surveyForms = batchService.getSurveyForm();
						
			model.addAttribute("referenceMonthStr", referenceMonthStr);
			model.addAttribute("purpose", purpose);
			model.addAttribute("totalNoOfQuotation", totalNoOfQuotation);
			model.addAttribute("totalNoOfOutlet", totalNoOfOutlet);
			model.addAttribute("surveyForms", surveyForms);
			
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
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return service.queryIndoorQuotationRecordDataConversionTableList(requestModel, session, detail.getUserId(), "Conversion", "Reject Verification");
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get history quotation record
	 */
	@RequestMapping(value = "getHistoryQuotationRecord", method = RequestMethod.GET)
	public String getHistoryQuotationRecord(Model model, @RequestParam("id") int id) {
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
	public @ResponseBody String getFrString(Model model, @RequestParam("indoorQuotationRecordId") int indoorQuotationRecordId) {
		 IndoorQuotationRecord iqr = this.service.getIndoorQuotationRecord(indoorQuotationRecordId);
		 Quotation q = iqr.getQuotation();
		 String response = "";
		 Date date = null;
		 if(q.getLastFRAppliedDate()!=null){
			 date = q.getLastFRAppliedDate();
		 } else {
			 date = q.getTempLastFRAppliedDate();
		 }
		 if(q.isUseFRAdmin() != null && q.isUseFRAdmin()){
			 response = "(Last date applied FR: "+commonService.formatDate(date)+") with FR="+q.getFrAdmin();
			 if(q.getIsFRAdminPercentage() != null && q.getIsFRAdminPercentage()){
				 response = response+"%"; 
			 }
		 }else{
			 response = "(Last date applied FR: "+commonService.formatDate(date)+") with FR="+q.getFrField();
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
	
	@PreAuthorize("hasPermission(#user, 128) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			IndoorQuotationRecord indoorQuotationRecord,
			String btnSubmit,
			String verification) {
		try {
			IndoorQuotationRecord entity = this.service.getIndoorQuotationRecord(indoorQuotationRecord.getIndoorQuotationRecordId());
			Quotation quotation = entity.getQuotation();
			Unit unit = quotation.getUnit();
			//check need update fr
			boolean isFRLocked = service.isFRLocked(entity);
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
						return "redirect:/dataConversion/QuotationRecordDataConversion/edit?id="+indoorQuotationRecord.getId();
					}
				}
				//B158 Follow "Firm Verification" to fix Fail to update record
				else if("isCategoryVerify".equalsIgnoreCase(verification)){
					boolean isAllowCategoryVerify = quotationRecordService.isAllowFirmVerify(indoorQuotationRecord.getIndoorQuotationRecordId());
					if(!isAllowCategoryVerify)
					{
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00170", null, locale));
						return "redirect:/dataConversion/QuotationRecordDataConversion/edit?id="+indoorQuotationRecord.getId();
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

			// Current N Price
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

			// Current S Price
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
			entity.setRemark(indoorQuotationRecord.getRemark());

			// Previous N Price
			if (indoorQuotationRecord.isNullPreviousNPrice()){
				entity.setNullPreviousNPrice(true);
				entity.setPreviousNPrice(null);
			} else {
				qrForValidation.setnPrice(indoorQuotationRecord.getPreviousNPrice());
				// NPriceGreaterZero returns true if N price <= 0, NOT > 0... ok...
				if (onSpotValidationService.NPriceGreaterZero(qrForValidation)) {
					errorMessages.add("Previous " + onSpotValidationService.getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_3));
				} else {
					entity.setNullPreviousNPrice(false);
					entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
				}
			}

			// Previous S Price
			if (indoorQuotationRecord.isNullPreviousSPrice()){
				entity.setNullPreviousSPrice(true);
				entity.setPreviousSPrice(null);
			} else {
				qrForValidation.setsPrice(indoorQuotationRecord.getPreviousSPrice());
				// SPriceGreaterZero returns true if S price <= 0, NOT > 0... ok...
				if (onSpotValidationService.SPriceGreaterZero(qrForValidation)) {
					errorMessages.add("Previous " + onSpotValidationService.getMessage(SystemConstant.ON_SPOT_VALIDATION_MESSAGE_4));
				} else {
					entity.setNullPreviousSPrice(false);
					entity.setPreviousSPrice(indoorQuotationRecord.getPreviousSPrice());
				}
			}
			//end form binding

			if (errorMessages.size() > 0) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, StringUtils.join(errorMessages, "<br>"));
				return "redirect:/dataConversion/QuotationRecordDataConversion/edit?id=" + indoorQuotationRecord.getId();
			}

			//calculation logic
			if(updateFr && indoorQuotationRecord.isApplyFR()){
				entity.setApplyFR(true);
				entity.setFRPercentage(indoorQuotationRecord.isFRPercentage());
				entity.setFr(indoorQuotationRecord.getFr());
				quotation.setLastFRAppliedDate(commonService.getDate(commonService.formatDate(new Date())));
								
				entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
				entity.setPreviousSPrice(indoorQuotationRecord.getPreviousSPrice());
				/* calculate at js.
				if(indoorQuotationRecord.getLastNPrice() != null && indoorQuotationRecord.getLastSPrice() != null){
					if(indoorQuotationRecord.isFRPercentage()){
						entity.setPreviousNPrice(indoorQuotationRecord.getCurrentNPrice() / (1 + (indoorQuotationRecord.getFr() / 100)));
						entity.setPreviousSPrice((indoorQuotationRecord.getCurrentSPrice() / (1 + (indoorQuotationRecord.getFr() / 100))*indoorQuotationRecord.getLastSPrice()/indoorQuotationRecord.getLastNPrice()));
					}else{
						entity.setPreviousNPrice(indoorQuotationRecord.getCurrentNPrice() / (1 + indoorQuotationRecord.getFr()));
						entity.setPreviousSPrice((indoorQuotationRecord.getCurrentSPrice() / (1 + indoorQuotationRecord.getFr())*indoorQuotationRecord.getLastSPrice()/indoorQuotationRecord.getLastNPrice()));
					}
				}else{
					if(indoorQuotationRecord.isFRPercentage()){
						entity.setPreviousNPrice(indoorQuotationRecord.getCurrentNPrice() / (1 + (indoorQuotationRecord.getFr() / 100)));
						entity.setPreviousSPrice(indoorQuotationRecord.getCurrentSPrice() / (1 + (indoorQuotationRecord.getFr() / 100)));
					}else{
						entity.setPreviousNPrice(indoorQuotationRecord.getCurrentNPrice() / (1 + indoorQuotationRecord.getFr()));
						entity.setPreviousSPrice(indoorQuotationRecord.getCurrentSPrice() / (1 + indoorQuotationRecord.getFr()));
					}
				}
				*/
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
//				quotation.setTempFRValue(indoorQuotationRecord.getFr());
//				if(indoorQuotationRecord.isFRPercentage()){
//					quotation.setTempFRPercentage(true);
//				}else{
//					quotation.setTempFRPercentage(false);
//				}
//				if(quotation.isTempIsFRApplied() == null || !quotation.isTempIsFRApplied()){
//					quotation.setTempLastFRAppliedDate(commonService.getDate(commonService.formatDate(new Date())));
//					quotation.setTempIsFRApplied(true);
//				}
			}else if(updateFr && !indoorQuotationRecord.isApplyFR()){
				// Acting. Please follow
				
				quotation.setFrAdmin(quotation.getTempFRValue());
				quotation.setFrField(quotation.getTempFRValue());
				quotation.setLastFRAppliedDate(quotation.getTempLastFRAppliedDate());
				quotation.setFRApplied(false);
//				quotation.setIsUseFRAdmin(quotation.isTempIsUseFRAdmin());
				quotation.setIsFRAdminPercentage(quotation.isTempFRPercentage());
				quotation.setIsFRFieldPercentage(quotation.isTempFRPercentage());
//				quotation.setReturnGoods(quotation.getTempIsReturnGoods());
//				quotation.setReturnNewGoods(quotation.getTempIsReturnNewGoods());
				
//				quotation.setTempFRValue(null);
//				quotation.setTempLastFRAppliedDate(null);
//				quotation.setTempIsFRApplied(false);
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
				
				/*
				if(indoorQuotationRecord.getPreviousNPrice() != null && indoorQuotationRecord.getPreviousSPrice() != null){
					if(entity.getLastNPrice() != null && entity.getLastSPrice() != null){
						entity.setPreviousNPrice(indoorQuotationRecord.getCurrentNPrice());
						entity.setPreviousSPrice((indoorQuotationRecord.getCurrentNPrice() * entity.getLastSPrice())/entity.getLastNPrice());
					}else{
						entity.setPreviousNPrice(null);
						entity.setPreviousSPrice(null);
					}
				}else{
					if(entity.getLastNPrice() != null && entity.getLastSPrice() != null){
						entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
						entity.setPreviousSPrice(indoorQuotationRecord.getPreviousNPrice() * entity.getLastSPrice() / entity.getLastNPrice());
					}else{
						entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
						entity.setPreviousSPrice(indoorQuotationRecord.getPreviousSPrice());
					}
				}*/
			}else{
				entity.setSpicing(false);
				entity.setPreviousNPrice(indoorQuotationRecord.getPreviousNPrice());
				entity.setPreviousSPrice(indoorQuotationRecord.getPreviousSPrice());
			}
			//end spicing logic
			
			if(btnSubmit.equalsIgnoreCase("verification") && !(entity.isRUA() || entity.isNoField())){
				entity.setStatus("Request Verification");
			}
			if(btnSubmit.equalsIgnoreCase("submit") || btnSubmit.equalsIgnoreCase("submitNext")){
				entity.setStatus("Complete");
			}
			
			if (verification != null && !(entity.isRUA() || entity.isNoField())) {
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
			
			this.service.updateRecord(entity, quotation);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			switch(btnSubmit){
				case "saveNext":
					Integer nextId = session.getNextId(indoorQuotationRecord.getId());
					if(nextId == null){
						return "redirect:/dataConversion/QuotationRecordDataConversion/selectSurveyType?purposeId="+unit.getPurpose().getId()+"&referenceMonthStr="+commonService.formatMonth(entity.getReferenceMonth());
					}
					return "redirect:/dataConversion/QuotationRecordDataConversion/edit?id="+nextId;
				case "submitNext":
					nextId = session.getNextId(indoorQuotationRecord.getId());
					if(nextId == null){
						return "redirect:/dataConversion/QuotationRecordDataConversion/selectSurveyType?purposeId="+unit.getPurpose().getId()+"&referenceMonthStr="+commonService.formatMonth(entity.getReferenceMonth());
					}
					return "redirect:/dataConversion/QuotationRecordDataConversion/edit?id="+nextId;
				case "save":
					return "redirect:/dataConversion/QuotationRecordDataConversion/edit?id="+indoorQuotationRecord.getId();
				case "verification":
				case "submit":
					return "redirect:/dataConversion/QuotationRecordDataConversion/selectSurveyType?purposeId="+unit.getPurpose().getId()+"&referenceMonthStr="+commonService.formatMonth(entity.getReferenceMonth());
			}
			
		} catch (Exception e) {
			logger.error("save", e);
		}
		redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		return "redirect:/dataConversion/QuotationRecordDataConversion/edit?id="+indoorQuotationRecord.getId();
	}
	
	/**
	 * Flag
	 */
	@PreAuthorize("hasPermission(#user, 128) or hasPermission(#user, 256)")
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
			logger.error("delete", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
	
	/**
	 * fetchIdList
	 */
	@RequestMapping(value = "fetchIdList")
	public @ResponseBody void fetchIdList(Locale locale,
			Model model,
			SessionStatus sessionStatus,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			DatatableRequestModel requestModel){

		session.setLastRequestModel(requestModel);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		this.service.getConversionTableIds(requestModel, session,  detail.getUserId(), "Conversion", "Reject Verification");
	}
	
	@RequestMapping(value = "getLastRequestModel")
	public @ResponseBody DatatableRequestModel getLastRequestModel(@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session) {
		return session.getLastRequestModel();
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
