package com.kinetix.controller.qualityControlManagement;

import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.ScSvPlan;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.ScSvPlanEditModel;
import capi.model.qualityControlManagement.ScSvPlanTableList;
import capi.service.CommonService;
import capi.service.qualityControlManagement.ScSvPlanService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("RF2006")
@FuncCode("RF2006")
@Controller("SupervisoryVisitPlanMaintenanceController")
@RequestMapping("qualityControlManagement/SupervisoryVisitPlanMaintenance")
public class SupervisoryVisitPlanMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(SupervisoryVisitPlanMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private ScSvPlanService service;
	
	@Autowired
	private CommonService commonService;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model) {
	}

	
	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<ScSvPlanTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth,
				Integer qcType) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			
			int authorityLevel = detail.getAuthorityLevel();
			
			boolean aboveSupervisor = false;
			if( ((authorityLevel & 1) == 1) || ((authorityLevel & 2) == 2)
					|| ((authorityLevel & 256) == 256) || ((authorityLevel & 2048) == 2048) )
				aboveSupervisor = true;
			
			List<Integer> actedUsers = detail.getActedUsers();
			actedUsers.add(detail.getUserId());
			
			return service.queryScSvPlan(requestModel, detail.getUserId(), qcType, aboveSupervisor, actedUsers, authorityLevel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit ScSv Plan
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act,
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			ScSvPlan item = null;
			
			if ("edit".equals(act)) {
				item = service.getScSvPlanById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SupervisoryVisitPlanMaintenance/home";
				}
			} else {
				item = new ScSvPlan();
				model.addAttribute("act", "add");
			}
			
			ScSvPlanEditModel editModel = service.convertEntityToModel(item);

			List<String> spotCheckDates = service.getSpotCheckDatesCurrentMonth();
			if (item.getVisitDate() != null){
				Date visitDate = commonService.getDateWithoutTime(item.getVisitDate());			
				Date today = commonService.getDateWithoutTime(new Date());
				model.addAttribute("isReadOnly", today.after(visitDate));
			}
			else{
				model.addAttribute("isReadOnly", false);
			}
			
			List<String> spotCheckDateBySCSetup = null;
			if("edit".equals(act)) {
				spotCheckDateBySCSetup = service.getSpotCheckDateByUserIdFromSCSetup(item.getUser().getUserId());
			}
			
			List<String> spotCheckDateBySCForm = null;
			if("edit".equals(act)) {
				spotCheckDateBySCForm = service.getSpotCheckDateByUserIdFromSCForm(item.getUser().getUserId());
			}
			
			List<String> spotCheckDateByUser = new ArrayList<> ();
			if(spotCheckDateBySCSetup != null && spotCheckDateBySCForm != null) {
				spotCheckDateByUser = new ArrayList<> (spotCheckDateBySCSetup);
				spotCheckDateBySCForm.removeAll(spotCheckDateBySCSetup);
				spotCheckDateByUser.addAll(spotCheckDateBySCForm);
			} else if(spotCheckDateBySCSetup != null) {
				spotCheckDateByUser = new ArrayList<> (spotCheckDateBySCSetup);
			} else if(spotCheckDateBySCForm != null) {
				spotCheckDateByUser = new ArrayList<> (spotCheckDateBySCForm);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			
			model.addAttribute("spotCheckDateByUser", mapper.writeValueAsString(spotCheckDateByUser));
			
			model.addAttribute("spotCheckDates", mapper.writeValueAsString(spotCheckDates));
			model.addAttribute("model", editModel);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	
	/**
	 * Save ScSv Plan
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@RequestParam(value = "act", required = false) String act,
			@ModelAttribute ScSvPlanEditModel item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes,
			Authentication auth) {
		try {
			
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			if (!service.saveScSvPlan(item, detail.getUserId())){
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
			}
			else{
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/qualityControlManagement/SupervisoryVisitPlanMaintenance/home";
	}
		


	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteScSvPlans(id)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));				
			}
			else{
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));				
			}	
		} catch (Exception e) {
			logger.error("delete", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		}
		
		return "/partial/messageRibbons";
	}

	
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "querySCSVDatesByOfficer", method = RequestMethod.POST)
	public @ResponseBody List<String> querySCSVDatesByOfficer(Locale locale, Model model, DatatableRequestModel requestModel, Integer officerId) {
		try {
			ArrayList<String> datesList = new ArrayList<String>();
			datesList.addAll(service.getSpotCheckDateByUserIdFromSCForm(officerId));
			datesList.addAll(service.getSupervisoryVisitDateByUserIdFromSVForm(officerId));
			return datesList;
		} catch (Exception e) {
			logger.error("querySCSVDatesByOfficer", e);
		}
		return null;
	}	
}
