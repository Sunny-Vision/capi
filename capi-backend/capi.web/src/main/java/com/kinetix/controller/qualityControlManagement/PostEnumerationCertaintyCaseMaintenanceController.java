package com.kinetix.controller.qualityControlManagement;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.commonLookup.PECertaintyCaseLookupTableList;
import capi.model.qualityControlManagement.PostEnumerationCertaintyCaseEditModel;
import capi.model.qualityControlManagement.PostEnumerationCertaintyCaseTableList;
import capi.service.lookup.PECertaintyCaseLookupService;
import capi.service.qualityControlManagement.PostEnumerationCertaintyCaseService;

import com.kinetix.component.FuncCode;


/**
 * RF-2014 Post-Enumeration Certainty Case Maintenance
 */
@Secured("RF2014")
@FuncCode("RF2014")
@Controller("PostEnumerationCertaintyCaseMaintenanceController")
@RequestMapping("qualityControlManagement/PostEnumerationCertaintyCaseMaintenance")
public class PostEnumerationCertaintyCaseMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(PostEnumerationCertaintyCaseMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private PostEnumerationCertaintyCaseService service;

	@Autowired
	private PECertaintyCaseLookupService peCertaintyCaseLookupService;

	/**
	 * List field experience
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<PostEnumerationCertaintyCaseTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getSurveyMonthList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Post-Enumeration Certainty Case
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer surveyMonthId, 
			@RequestParam(value = "referenceMonth", required = false) String referenceMonth, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			PostEnumerationCertaintyCaseEditModel editModel = service.convertEntityToModel(surveyMonthId);

			model.addAttribute("model", editModel);
			
			model.addAttribute("referenceMonth", referenceMonth);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Get Post-Enumeration Certainty Case detail
	 */
	@RequestMapping(value = "getPECertaintyCaseDetail")
	public @ResponseBody List<PECertaintyCaseLookupTableList> getPECertaintyCaseDetail(
			@RequestParam(value = "surveyMonthId", required = false) Integer surveyMonthId,
			@RequestParam(value = "assignmentIds[]", required = false) Integer[] assignmentIds) {
		try {
			//if (peCheckTaskIds == null || peCheckTaskIds.length == 0) return null;
			return peCertaintyCaseLookupService.getTableListByIds(surveyMonthId, assignmentIds);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Confirm Post-Enumeration Certainty Case
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 256)")
	@RequestMapping(value = "confirm", method = RequestMethod.POST)
	public String save(@RequestParam(value = "assignmentId", required = false) ArrayList<Integer> id, 
			@RequestParam(value = "surveyMonthId", required = false) Integer surveyMonthId, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			
			if (!service.confirmPECertaintyCase(id, surveyMonthId)) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/PostEnumerationCertaintyCaseMaintenance/home";
			}		
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/qualityControlManagement/PostEnumerationCertaintyCaseMaintenance/home";
	}

}
