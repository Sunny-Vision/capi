package com.kinetix.controller.masterMaintenance;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.PEExcludedOutletType;
import capi.entity.VwOutletTypeShortForm;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.businessParameterMaintenance.DisplayModel;
import capi.model.masterMaintenance.businessParameterMaintenance.GeneralSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.ItineraryParameterSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.OnSpotValidationMessageSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.PEParameterSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.ReasonForReportSaveModel;
import capi.model.masterMaintenance.businessParameterMaintenance.WorkingSessionSaveModel;
import capi.service.masterMaintenance.BusinessParameterService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.UnitService;

import com.kinetix.component.FuncCode;

/**
 * UF-1114 Batch Maintenance
 */
@Secured("UF1112")
@FuncCode("UF1112")
@Controller("BusinessParameterMaintenanceController")
@RequestMapping("masterMaintenance/BusinessParameterMaintenance")
public class BusinessParameterMaintenanceController {
	private static final Logger logger = LoggerFactory.getLogger(BatchMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	private BusinessParameterService businessParameterService;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired 
	private PurposeService purposeService;
	
	@Autowired 
	private UnitService unitService;
	
	private static final String ACTIVE_TAB = "ACTIVE_TAB";
	
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		DisplayModel displayModel = businessParameterService.getParameters();
		model.addAttribute("displayModel", displayModel);
		
		List<VwOutletTypeShortForm> outletTypes = outletService.getOutletTypes();
		model.addAttribute("outletTypes", outletTypes);
		
		Iterator<PEExcludedOutletType> excludedOutletType = businessParameterService.getPEExcludedOutletType().iterator();
		String excludedOutletTypeIds = "";
		while(excludedOutletType.hasNext()){
			PEExcludedOutletType outlet =  excludedOutletType.next();
			if(excludedOutletTypeIds.length() == 0){
				excludedOutletTypeIds = outlet.getOutletType().getShortCode();
			}else{
				excludedOutletTypeIds += ","+outlet.getOutletType().getShortCode();
			}
		}
		model.addAttribute("excludedOutletTypeIds", excludedOutletTypeIds);
		
		
		
	}
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "saveGerneral", method = RequestMethod.POST)
	public String saveGerneral(Model model, 
			@ModelAttribute GeneralSaveModel item, RedirectAttributes redirectAttributes, Locale locale){
		
		try {
			businessParameterService.saveGeneralParameters(item);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			redirectAttributes.addFlashAttribute(ACTIVE_TAB, "general");
			e.printStackTrace();
			return "redirect:/masterMaintenance/BusinessParameterMaintenance/home";
		}
		redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		redirectAttributes.addFlashAttribute(ACTIVE_TAB, "general");
		return "redirect:/masterMaintenance/BusinessParameterMaintenance/home";
		
	}
	
	@RequestMapping(value = "getUnitCode", method = RequestMethod.POST)
	public @ResponseBody String getUnitCode(Model model, 
			@RequestParam(value = "id", required = true) Integer unitId){
		
		return businessParameterService.getUnitById(unitId).getCode();
		
	}
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "savePEParameters", method = RequestMethod.POST)
	public String savePEParameters (Model model, 
			@ModelAttribute PEParameterSaveModel saveModel, RedirectAttributes redirectAttributes, Locale locale){
		
		businessParameterService.savePEParameters(saveModel);
		redirectAttributes.addFlashAttribute(ACTIVE_TAB, "PE");
		redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		return "redirect:/masterMaintenance/BusinessParameterMaintenance/home";
		
	}
	
	@RequestMapping(value = "deleteUnitCriteria", method = RequestMethod.POST)
	public @ResponseBody boolean deleteUnitCriteria(Model model,
			@RequestParam(value = "id", required = true) Integer id,
			Locale locale, RedirectAttributes redirectAttributes){
			
			try {
				
				businessParameterService.deleteUnitCriteria(id);
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			} catch (Exception e) {
				logger.error("home", e);
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
				return false;
			}
			return true;
	}
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "saveItineraryParameters", method = RequestMethod.POST)
	public String saveItineraryParameters (Model model, 
			@ModelAttribute ItineraryParameterSaveModel saveModel, RedirectAttributes redirectAttributes, Locale locale){
		
		businessParameterService.saveItineraryParameters(saveModel);
		redirectAttributes.addFlashAttribute(ACTIVE_TAB, "itinerary");
		redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		return "redirect:/masterMaintenance/BusinessParameterMaintenance/home";
		
	}
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "saveReasonForReports", method = RequestMethod.POST)
	public String saveReasonForReports (Model model, 
			@ModelAttribute ReasonForReportSaveModel saveModel, RedirectAttributes redirectAttributes, Locale locale){
		
		businessParameterService.saveReasonForReports(saveModel);
		redirectAttributes.addFlashAttribute(ACTIVE_TAB, "report");
		redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		return "redirect:/masterMaintenance/BusinessParameterMaintenance/home";
		
	}
	
	@RequestMapping(value = "deleteReasonForReports", method = RequestMethod.POST)
	public @ResponseBody boolean deleteReasonForReports(Model model,
			@RequestParam(value = "id", required = true) Integer id,
			Locale locale, RedirectAttributes redirectAttributes){
			
			try {
				
				businessParameterService.deleteReportReason(id);
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			} catch (Exception e) {
				logger.error("home", e);
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
				return false;
			}
			return true;
	}
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "saveWorkingSessions", method = RequestMethod.POST)
	public String saveWorkingSessions (Model model, 
			@ModelAttribute WorkingSessionSaveModel saveModel, RedirectAttributes redirectAttributes, Locale locale){
		try{
			businessParameterService.saveWorkingSessions(saveModel);
			redirectAttributes.addFlashAttribute(ACTIVE_TAB, "workingSession");
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e){
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(ACTIVE_TAB, "workingSession");
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/BusinessParameterMaintenance/home";
	}
	
	@RequestMapping(value = "deleteWorkingSession", method = RequestMethod.POST)
	public @ResponseBody boolean deleteWorkingSession(Model model,
			@RequestParam(value = "id", required = true) Integer id,
			Locale locale, RedirectAttributes redirectAttributes){
			
			try {
				businessParameterService.deleteWorkingSession(id);
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			} catch (Exception e) {
				logger.error("home", e);
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
				return false;
			}
			return true;
	}
	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "saveOnSpotValidate", method = RequestMethod.POST)
	public String saveOnSpotValidate (Model model, 
			@ModelAttribute OnSpotValidationMessageSaveModel saveModel, RedirectAttributes redirectAttributes, Locale locale){
		
		businessParameterService.saveOnSpotValidationMessage(saveModel);
		redirectAttributes.addFlashAttribute(ACTIVE_TAB, "onSpot");
		redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		
		return "redirect:/masterMaintenance/BusinessParameterMaintenance/home";
		
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
	 * Get Item select format
	 */
	@RequestMapping(value = "queryItemSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryItemSelect2(Select2RequestModel requestModel) {
		try {
			return unitService.queryItemSelect2(requestModel, null, null);
		} catch (Exception e) {
			logger.error("queryUnitSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get single unit
	 */
	@RequestMapping(value = "queryUnitSelectSingle", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
	public @ResponseBody String queryUnitSelectSingle(@RequestParam(value = "id") Integer id) {
		try {
			return unitService.queryUnitSelectSingle(id);
		} catch (Exception e) {
			logger.error("queryUnitSelectSingle", e);
		}
		return null;
	}
	
	/**
	 * Get single unit
	 */
	@RequestMapping(value = "queryItemSelectMutiple")
	public @ResponseBody List<KeyValueModel> queryItemSelectMutiple(@RequestParam(value = "id[]") Integer[] ids) {
		try {
			return unitService.getItemByIds(ids);
			
		} catch (Exception e) {
			logger.error("queryUnitSelectMutiple", e);
		}
		return null;
	}
	
	
	/**
	 * Get purpose select format
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryPurposeSelect2(Select2RequestModel requestModel) {
		try {
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
		}
		return null;
	}
}
