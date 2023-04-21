package com.kinetix.controller.itineraryPlanning;

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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.itineraryPlanning.QCItineraryPlanItemModel;
import capi.model.itineraryPlanning.QCItineraryPlanModel;
import capi.model.itineraryPlanning.QCItineraryPlanTableList;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.CalendarEventService;
import capi.service.itineraryPlanning.ItineraryPlanningService;
import edu.emory.mathcs.backport.java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1703")
@FuncCode("UF1703")
@Controller("QCItineraryPlanController")
@RequestMapping("itineraryPlanning/QCItineraryPlan")
public class QCItineraryPlanController {

	private static final Logger logger = LoggerFactory.getLogger(QCItineraryPlanController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private ItineraryPlanningService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CalendarEventService calendarService;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model, Authentication auth) {
//		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//		UserAccessModel uam = userService.gatherUserRequiredInfo(userService.getUserById(detail.getUserId()));
//		model.addAttribute("uam", uam);

	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<QCItineraryPlanTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth) {
		try {
//			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return service.queryQCItineraryPlan(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit QC Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(@RequestParam(value = "id", required = true ) Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			
			QCItineraryPlanModel plan = service.getQCItineraryPlanModel(id);
			model.addAttribute("model", plan);

		} catch (Exception e) {
			logger.error("view", e);
		}
		return null;
	}
	
	/**
	 * Edit QC Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false ) Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			
			QCItineraryPlanModel plan;

			if (id != null) {
				plan = service.getQCItineraryPlanModel(id);	
				model.addAttribute("act", "edit");
			} else {
				CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
				plan = new QCItineraryPlanModel();
				List<String> dates = service.getQCFuturePlanDate(detail.getUserId());
				
				plan.setUserId(detail.getUserId());
				
				User supervisor = userService.getSupervisorByUserId(detail.getUserId());
				if(supervisor != null) {
					plan.setSubmitTo(supervisor.getStaffCode() + " - " + supervisor.getChineseName());
					plan.setSubmitToId(supervisor.getId());
				}
				
				ObjectMapper mapper = new ObjectMapper();
				String jsDates = mapper.writeValueAsString(dates);
				model.addAttribute("planDates", jsDates);
				
				/*
				Date today = commonService.getDateWithoutTime(new Date());
				List<Date> nonWorking =calendarService.getNonWorkingDate(today, DateUtils.addDays(today, 30));
				List<String> nonList = new ArrayList<String>();
				for (Date date : nonWorking){
					nonList.add(commonService.formatDate(date));
				}
				*/
				List<String> nonList = Collections.emptyList();
				model.addAttribute("nonWorkingDates", mapper.writeValueAsString(nonList));
				//plan.setStatus("Draft");
				plan.setSession("AP");
			}
			
			model.addAttribute("model", plan);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Save QC Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute QCItineraryPlanModel QCItineraryPlanModel, 
			Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			if (QCItineraryPlanModel.getUserId() == null){
				QCItineraryPlanModel.setUserId(detail.getUserId());
			}
			
			String error = service.saveQCItineraryPlan(QCItineraryPlanModel);
			if (error != null) {
				
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage(error, null, locale));
				
			} else {

				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/itineraryPlanning/QCItineraryPlan/home";
	}
	
	/**
	 * Query Spot and Supervisory Check
	 */
	@RequestMapping(value = "queryChecks", method = RequestMethod.POST)
	public @ResponseBody List<QCItineraryPlanItemModel>
		queryAssignments(@RequestParam(value = "date") String date) {
		try {
			List<QCItineraryPlanItemModel> checkItems = service.getCheckItems(commonService.getDate(date));
			return checkItems;
		} catch (Exception e) {
			logger.error("queryChecks", e);
		}
		return null;
	}
	
	/**
	 * Undo QC Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "undo", method = RequestMethod.GET)
	public String undo(@RequestParam(value = "id" ) Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
		
			service.undoQCItineraryPlan(id);		
			redirectAttributes.addAttribute("id", id);
			return "redirect:/itineraryPlanning/QCItineraryPlan/edit";
			
		} catch (Exception e) {
			logger.error("undo", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00104", null, locale));
		}
		return "redirect:/itineraryPlanning/QCItineraryPlan/home";
	}
	
	/**
	 * Get officer select2 format
	 */
	@RequestMapping(value = "queryOfficerSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOfficerSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return userService.queryOfficerSelect2(requestModel, detail.getUserId());
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Date select2 format
	 */
	@RequestMapping(value = "queryDateSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDateSelect2(@RequestParam(value = "userId") Integer userId, Select2RequestModel requestModel ) {
		try {
			return service.querySupervisorDateSelect2(requestModel, userId);
		} catch (Exception e) {
			logger.error("queryDateSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Supervisor select2 format
	 */
	@RequestMapping(value = "queryHeadUserSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
	queryHeadUserSelect2(Select2RequestModel requestModel) {
		try {
			return userService.queryFieldHeadSectionSelect2(requestModel);
		} catch (Exception e) {
			logger.error("querySupervisorSelect2", e);
		}
		return null;
	}
	
	/**
	 * Edit QC Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> ids, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			service.deleteQCItineraryPlan(ids);
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));
			return "/partial/messageRibbons";
		} catch (Exception e) {
			logger.error("cancel", e);
		}
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		return "/partial/messageRibbons";		
		
	}
	
	
}
