package com.kinetix.controller.qualityControlManagement;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.SupervisoryVisitForm;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.SupervisoryVisitEditModel;
import capi.model.qualityControlManagement.SupervisoryVisitTableList;
import capi.service.qualityControlManagement.SupervisoryVisitService;

import com.kinetix.component.FuncCode;


/**
 * RF-2002 Supervisory Visit Maintenance
 */
@Secured("RF2002")
@FuncCode("RF2002")
@Controller("SupervisoryVisitMaintenanceController")
@RequestMapping("qualityControlManagement/SupervisoryVisitMaintenance")
public class SupervisoryVisitMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(SupervisoryVisitMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private SupervisoryVisitService service;

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
	public @ResponseBody DatatableResponseModel<SupervisoryVisitTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth) {

		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			
			int authorityLevel = detail.getAuthorityLevel();
			
			boolean aboveSupervisor = false;
			if( ((authorityLevel & 1) == 1) || ((authorityLevel & 2) == 2)
					|| ((authorityLevel & 256) == 256) || ((authorityLevel & 2048) == 2048) )
				aboveSupervisor = true;
			
			List<Integer> actedUsers = detail.getActedUsers();
			actedUsers.add(detail.getUserId());
			
			return service.getSupervisoryVisitList(requestModel, aboveSupervisor, actedUsers, detail.getUserId(), authorityLevel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Supervisory Visit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, @RequestParam(value = "fieldOfficerId", required = false) Integer fieldOfficerId,
			@RequestParam(value = "supervisorId", required = false) Integer supervisorId, Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			SupervisoryVisitForm item = null;
			item = service.getSupervisoryVisitById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SupervisoryVisitMaintenance/home";
			}

			SupervisoryVisitEditModel editModel = service.convertEntityToModel(item, id, fieldOfficerId, supervisorId);

			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save Supervisory Visit
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute SupervisoryVisitEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes,
						@RequestParam(value="submitBtn") String submitOrSave) {
		try {
			if (item.getSupervisoryVisitFormId() != null) {
				if (service.getSupervisoryVisitById(item.getSupervisoryVisitFormId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SupervisoryVisitMaintenance/home";
				}
			}
			
			service.saveSupervisoryVisit(item, submitOrSave);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/qualityControlManagement/SupervisoryVisitMaintenance/home";
	}

	/**
	 * Get item select format
	 */
	@RequestMapping(value = "queryAssignmentWithSurveyMonthSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryAssignmentWithSurveyMonthSelect2(Select2RequestModel requestModel,
					@RequestParam(value="surveyMonthId") Integer surveyMonthId,
					@RequestParam(value="fieldOfficerId") Integer fieldOfficerId) {
		try {
			return service.queryAssignmentWithSurveyMonthSelect2(requestModel, surveyMonthId, fieldOfficerId);
		} catch (Exception e) {
			logger.error("queryAssignmentWithSurveyMonthSelect2", e);
		}
		return null;
	}

	@RequestMapping(value = "getAssignmentWithSurveyMonthById")
	public @ResponseBody List<SupervisoryVisitEditModel.AssignmentWithSurveyMonth> getAssignmentWithSurveyMonthById(Integer assignmentId){
		try {
			return service.getAssignmentWithSurveyMonthById(assignmentId);
		} catch (Exception e) {
			logger.error("getAssignmentWithSurveyMonthById", e);
		}
		return null;		
	}

}