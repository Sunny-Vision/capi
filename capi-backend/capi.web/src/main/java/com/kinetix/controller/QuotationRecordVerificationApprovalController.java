package com.kinetix.controller;

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
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionSessionModel;
import capi.model.quotationRecordVerificationApproval.QuotationRecordVerificationApprovalTableListModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.service.CommonService;
import capi.service.NotificationService;
import capi.service.dataConversion.QuotationRecordDataConversionService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;

import com.kinetix.component.FuncCode;
import com.kinetix.controller.assignmentAllocationAndReallocation.SurveyMonthController;

/**
* UF-2103 QuotationRecordVerificationApproval
*/
@Secured("UF2103")
@FuncCode("UF2103")
@Controller("QuotationRecordVerificationApprovalController")
@RequestMapping("QuotationRecordVerificationApproval")
@SessionAttributes({"quotationRecordSession"})
public class QuotationRecordVerificationApprovalController {
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
	private PurposeService purposeService;
	
	@Autowired
	private NotificationService notificationService;
	
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
	 * Get single outlet
	 */
	@RequestMapping(value = "queryOutletSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryOutletSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return outletService.getOutletSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryOutletSelectSingle", e);
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
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationRecordVerificationApprovalTableListModel>
		query(Locale locale,
				Model model,
				SessionStatus sessionStatus,
				@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
				DatatableRequestModel requestModel) {
		try {
			return service.queryIndoorQuotationRecordApprovalTableList(requestModel, session);
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
				
			model.addAttribute("model", quotationRecordPageViewModel);
			model.addAttribute("session", session);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	@PreAuthorize("hasPermission(#user, 32) or hasPermission(#user, 256)")
	@RequestMapping(value = "approval", method = RequestMethod.POST)
	public String approval(Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			@RequestParam(value = "indoorQuotationRecordId", required = true) Integer indoorQuotationRecordId,
			@RequestParam(value = "reason", required = false) String reason,
			@RequestParam(value = "btnSubmit", required = true) String btnSubmit
			) {
		
		try{
			Boolean approval =btnSubmit.equalsIgnoreCase("approve") ? Boolean.TRUE : Boolean.FALSE;
			this.service.indoorQuotationRecordApproval(indoorQuotationRecordId, approval, reason);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch(Exception e){
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/QuotationRecordVerificationApproval/home";
		
	}
	
	@PreAuthorize("hasPermission(#user, 32) or hasPermission(#user, 256)")
	@RequestMapping(value = "approvalSelected", method = RequestMethod.POST)
	public String approvalSelected(Locale locale,
			SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes,
			Authentication auth,
			Model model,
			@ModelAttribute("quotationRecordSession") QuotationRecordDataConversionSessionModel session,
			@RequestParam(value = "id", required = false) List<Integer> indoorQuotationRecordIds,
			@RequestParam(value = "reason", required = false) String reason,
			@RequestParam(value = "approval", required = true) Boolean approval
			) {
		
		try{
			if(indoorQuotationRecordIds == null || indoorQuotationRecordIds.size() == 0){
				indoorQuotationRecordIds = session.getIndoorQuotationRecordIds();
			}
			for(Integer id : indoorQuotationRecordIds){
				this.service.indoorQuotationRecordApproval(id, approval, reason);
			}
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}catch(Exception e){
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "/partial/messageRibbons";		
	}
}
