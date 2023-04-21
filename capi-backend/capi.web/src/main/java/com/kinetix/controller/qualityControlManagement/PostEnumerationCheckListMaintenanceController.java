package com.kinetix.controller.qualityControlManagement;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.SurveyMonth;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.PECheckTaskList;
import capi.model.qualityControlManagement.PECheckListTableList;
import capi.model.qualityControlManagement.PECheckTaskModel;
import capi.service.CommonService;
import capi.service.assignmentAllocationAndReallocation.SurveyMonthService;
import capi.service.qualityControlManagement.PECheckService;
import com.kinetix.component.FuncCode;


/**
 * MB-2009 Post-Enumeration Check List Maintenance
 */
@Secured("RF2009")
@FuncCode("RF2009")
@Controller("PostEnumerationCheckListMaintenanceController")
@RequestMapping("qualityControlManagement/PostEnumerationCheckListMaintenance")
public class PostEnumerationCheckListMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(PostEnumerationCheckListMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private PECheckService service;

	@Autowired
	private SurveyMonthService surveyMonthService;
	
	@Autowired
	private CommonService commonService;
	
	/**
	 * List field experience
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(@RequestParam(value = "referenceMonth", required = false) String referenceMonth, Model model) {
		model.addAttribute("referenceMonth", referenceMonth);
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<PECheckListTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				Authentication authentication) {

		try {
	
			return service.getCheckListTableList(requestModel);
			
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit PE Check List
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "userId", required = true) Integer userId, 
			@RequestParam(value = "referenceMonth", required = false) String referenceMonth,
			@RequestParam(value = "surveyMonthId", required = false) Integer surveyMonthId, Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			if (surveyMonthId == null && !StringUtils.isEmpty(referenceMonth)){

				Date month = commonService.getMonth(referenceMonth);
				SurveyMonth surveyMonth = surveyMonthService.getSurveyMonthByReferenceMonth(month);
				if (surveyMonth!=null){
					surveyMonthId = surveyMonth.getId();
				}
				
			}
			PECheckTaskModel peCheckTaskModel = null;
			peCheckTaskModel = service.getPECheckTaskModel(userId, surveyMonthId);

			if (peCheckTaskModel == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/PostEnumerationCheckListMaintenance/home";
			}

			model.addAttribute("model", peCheckTaskModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save 
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute PECheckTaskModel peCheckTaskModel, Model model, Locale locale, 
			RedirectAttributes redirectAttributes, Authentication auth) {
		
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		
		try {
			
//			if (peCheckTaskModel != null && peCheckTaskModel.getPeCheckTaskList() != null && peCheckTaskModel.getPeCheckTaskList().size() > 0) {
//				for (PECheckTaskList peCheckTaskList : peCheckTaskModel.getPeCheckTaskList()) {
//					if (peCheckTaskList.getPeCheckFormId() != null && peCheckTaskList.getPeCheckFormId() > 0) {
//						if (service.getCheckFormById(peCheckTaskList.getPeCheckFormId()) == null) {
//							redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
//							redirectAttributes.addFlashAttribute("referenceMonth", peCheckTaskModel.getReferenceMonth());
//							return "redirect:/qualityControlManagement/PostEnumerationCheckListMaintenance/home";
//						}
//					}
//				}
//			}
			
			service.saveCheckTaskList(peCheckTaskModel, detail.getUserId());
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		redirectAttributes.addFlashAttribute("referenceMonth", peCheckTaskModel.getReferenceMonth());
		return "redirect:/qualityControlManagement/PostEnumerationCheckListMaintenance/home";
	}

	/**
	 * Get Post-Enumeration Check Assignment Case detail
	 */
	@RequestMapping(value = "getPECheckFormDetail")
	public @ResponseBody List<PECheckTaskList> getPECheckFormDetail(
			@RequestParam(value = "surveyMonthId", required = true) Integer surveyMonthId,
			@RequestParam(value = "assignmentIds[]", required = false) Integer[] assignmentIds) {
		try {
			if (assignmentIds == null || assignmentIds.length == 0) return null;
			return service.getPECheckFormList(assignmentIds, surveyMonthId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
}