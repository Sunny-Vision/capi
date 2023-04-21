package com.kinetix.controller.itineraryPlanning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.TimeLog;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.itineraryPlanning.ItineraryPlanOutletModel;
import capi.model.itineraryPlanning.ItineraryPlanEditModel;
import capi.model.itineraryPlanning.ItineraryPlanTableList;
import capi.service.AppConfigService;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.CalendarEventService;
import capi.service.itineraryPlanning.ItineraryPlanningService;
import capi.service.masterMaintenance.OutletService;
import capi.service.timeLogManagement.TimeLogService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1701")
@FuncCode("UF1701")
@Controller("ItineraryPlanController")
@RequestMapping("itineraryPlanning/ItineraryPlan")
public class ItineraryPlanController {

	private static final Logger logger = LoggerFactory.getLogger(ItineraryPlanController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private ItineraryPlanningService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private AppConfigService appConfigService;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired
	private CalendarEventService calendarService;
	
	@Autowired
	private TimeLogService timeLogService;
	

	@InitBinder
	public void initListBinder(WebDataBinder binder) {
	    binder.setAutoGrowCollectionLimit(100000);
	}
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	//@PreAuthorize("hasPermission(#user, 1) || hasPermission(#user, 2) || hasPermission(#user, 4) || hasPermission(#user, 16)")
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model, Authentication auth) {
//		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//		UserAccessModel uam = userService.gatherUserRequiredInfo(userService.getUserById(detail.getUserId()));
//		model.addAttribute("uam", uam);

	}

	/**
	 * datatable query function
	 */
	//@PreAuthorize("hasPermission(#user, 1) || hasPermission(#user, 2) || hasPermission(#user, 4) || hasPermission(#user, 16)")
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<ItineraryPlanTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth) {
		try {
//			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//			List<Integer> officerIds = new ArrayList<Integer>();
//			if ((detail.getAuthorityLevel() & 1) != 1 && (detail.getAuthorityLevel() & 2) != 2 && (detail.getAuthorityLevel() & 4) != 4) {
//				officerIds.add(detail.getUserId());
//			} 
			return service.queryItineraryPlan(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 4) || hasPermission(#user, 16) || hasPermission(#user, 256)")
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			ItineraryPlanEditModel item;
			
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			model.addAttribute("fieldOfficerId", detail.getUserId());
			
			if (id != null) {
				model.addAttribute("act", "edit");
				item = service.getItineraryPlanEditModel(id);
				User user =	userService.getUserById(item.getUserId());
				String userText =  String.format("%s - %s", user.getStaffCode(), user.getEnglishName());
				model.addAttribute("userText", userText);
				String supervisorText = "";
				if (item.getSupervisorId() != null){
					User supervisor = userService.getUserById(item.getSupervisorId());
					supervisorText =  String.format("%s - %s", supervisor.getStaffCode(), supervisor.getEnglishName());
				}
				
				TimeLog timelog = timeLogService.getTimeLogByUserIdAndDate(item.getUserId(), item.getDate());
				
				if (timelog != null && ("Submitted".equals(timelog.getStatus()) || "Approved".equals(timelog.getStatus()))){
					model.addAttribute("timeLogApproved", true);
				}
				
				model.addAttribute("supervisorText", supervisorText);
				model.addAttribute("userId", detail.getUserId());
				
			} else {
				List<String> dates = service.getFuturePlanDate(detail.getUserId());
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
				
				model.addAttribute("act", "add");
				item = new ItineraryPlanEditModel();
				if ((detail.getAuthorityLevel() & 4) != 4 && (detail.getAuthorityLevel() & 16) == 16){
					model.addAttribute("userId", detail.getUserId());
				}
				
				User user = userService.getUserById(detail.getUserId());
				if (user.getSupervisor() != null){
					item.setSupervisorId(user.getSupervisor().getId());
					String supervisorText =  String.format("%s - %s", user.getSupervisor().getStaffCode(), user.getSupervisor().getEnglishName());
					model.addAttribute("supervisorText", supervisorText);
				}
				
				item.setStatus("Draft");
			}
					
			model.addAttribute("model", item);
			model.addAttribute("googleBrowserKey", appConfigService.getGoogleBroswerKey());


		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * View Itinerary Plan
	 */
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			ItineraryPlanEditModel item;
			
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			model.addAttribute("fieldOfficerId", detail.getUserId());
			
			if (id != null) {
				model.addAttribute("act", "edit");
				item = service.getItineraryPlanEditModel(id);
				User user =	userService.getUserById(item.getUserId());
				String userText =  user.getStaffCode() +" - " + user.getEnglishName();
				model.addAttribute("userText", userText);
				String supervisorText = "";
				if (item.getSupervisorId() != null){
					User supervisor = userService.getUserById(item.getSupervisorId());
					supervisorText =  supervisor.getStaffCode() +" - " + supervisor.getEnglishName();
				}
				model.addAttribute("supervisorText", supervisorText);
				model.addAttribute("model", item);
				
			}
							
			model.addAttribute("googleBrowserKey", appConfigService.getGoogleBroswerKey());


		} catch (Exception e) {
			logger.error("view", e);
		}
		return null;
	}
	
	/**
	 * Save Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 4) || hasPermission(#user, 16) || hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute ItineraryPlanEditModel itineraryPlanEditModel, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
	
			switch (service.saveItineraryPlan(itineraryPlanEditModel)) {
			case 0:
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
						messageSource.getMessage("I00001", null, locale));
				break;
			case 1:
				model.addAttribute("model",itineraryPlanEditModel);
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00011", null, locale));
				return "/itineraryPlanning/ItineraryPlan/edit";
			case 2:
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00133", new Object[]{itineraryPlanEditModel.getDate()}, locale));
			}
			
		} catch (javax.persistence.OptimisticLockException e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00105", null, locale));
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/itineraryPlanning/ItineraryPlan/home";
	}
	
	/**
	 * Print Itinerary Plan
	 */
	//@PreAuthorize("hasPermission(#user, 4) || hasPermission(#user, 16)")
	@RequestMapping(value = "print", method = RequestMethod.GET)
	public String print(@RequestParam(value = "id" ) Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			
			ItineraryPlanEditModel item;			
			item = service.getItineraryPlanEditModel(id);		
			User user =	userService.getUserById(item.getUserId());
			model.addAttribute("plainLayout",true);
			model.addAttribute("user", user);
			model.addAttribute("model", item);
			model.addAttribute("googleBrowserKey", appConfigService.getGoogleBroswerKey());

		} catch (Exception e) {
			logger.error("print", e);
		}
		return null;
	}
	
	/**
	 * Undo the status to Draft
	 */
	@PreAuthorize("hasPermission(#user, 4) || hasPermission(#user, 16) || hasPermission(#user, 256)")
	@RequestMapping(value = "undo", method = RequestMethod.GET)
	public String undo(@RequestParam(value = "id") Integer id, Model model, Locale locale, 
			RedirectAttributes redirectAttributes) {
		try {
			if (!service.undoItineraryPlan(id)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "/partial/messageRibbons";
			}
			redirectAttributes.addAttribute("id", id);
			redirectAttributes.addAttribute("act", "edit");
			return "redirect:/itineraryPlanning/ItineraryPlan/edit";
		} catch (Exception e) {
			logger.error("undo", e);
		}
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00104", null, locale));
		return "/partial/messageRibbons";
	}

	/**
	 * Cancel
	 */
	@PreAuthorize("hasPermission(#user, 4) || hasPermission(#user, 16) || hasPermission(#user, 256)")
	@RequestMapping(value = "cancel", method = RequestMethod.POST)
	public String cancel(@RequestParam(value = "id") ArrayList<Integer> ids, Model model, Locale locale) {
		try {
			if (!service.cancelItineraryPlan(ids)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "/partial/messageRibbons";
			}
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));
			return "/partial/messageRibbons";
		} catch (Exception e) {
			logger.error("cancel", e);
		}
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		return "/partial/messageRibbons";
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
	 * Get Supervisor select2 format
	 */
	@RequestMapping(value = "querySupervisorSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		querySupervisorSelect2(@RequestParam(value = "officerId") Integer officerId, Select2RequestModel requestModel) {
		try {
			return userService.querySupervisorWithHeadUserSelect2(requestModel, officerId);
		} catch (Exception e) {
			logger.error("querySupervisorSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Date select2 format
	 */
	@RequestMapping(value = "queryDateSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDateSelect2(@RequestParam(value = "officerId") Integer officerId, Select2RequestModel requestModel ) {
		try {
			return service.queryDateSelect2(requestModel, officerId);
		} catch (Exception e) {
			logger.error("queryDateSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Outlet Type select2 format
	 */
	@RequestMapping(value = "queryOutletTypeFilterSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOutletTypeFilterSelect2(@RequestParam(value = "outletIds[]", required = false) Integer[] outletIds, Select2RequestModel requestModel) {
		try {
			return service.queryOutletTypeFilterSelect2(requestModel, outletIds);
		} catch (Exception e) {
			logger.error("querySupervisorSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get Plan Assignments
	 */
	@RequestMapping(value = "queryAssignments", method = RequestMethod.POST)
	public @ResponseBody List<ItineraryPlanOutletModel>
		queryAssignments(@RequestParam(value = "officerId") Integer officerId, 
				@RequestParam(value = "outletIds[]", required = false) Integer[] outletIds,
				@RequestParam(value = "planDate", required = false) String planDate,
				@RequestParam(value = "assignedCollectionDate", required = false) String assignedCollectionDate) {
		try {
			List<ItineraryPlanOutletModel> planAssignments = service.getPlanAssignment(officerId, 
					outletIds == null ? null : Arrays.asList(outletIds), 
					planDate == null ? null : commonService.getDate(planDate), 
					assignedCollectionDate == null ? null : commonService.getDate(assignedCollectionDate));
			for (ItineraryPlanOutletModel entry: planAssignments) {
		
				entry.setConvenientTime(commonService.formatTime(entry.getConvenientStartTime())+"-"+commonService.formatTime(entry.getConvenientEndTime()));
				entry.setConvenientTime2(commonService.formatTime(entry.getConvenientStartTime2())+"-"+commonService.formatTime(entry.getConvenientEndTime2()));
			}
			return planAssignments;
		} catch (Exception e) {
			logger.error("queryAssignments", e);
		}
		return null;
	}
	
	/**
	 * Get Imported Assignments
	 */
	@RequestMapping(value = "queryImportedAssignments", method = RequestMethod.POST)
	public @ResponseBody List<ItineraryPlanOutletModel>
		queryImportedAssignments(@RequestParam(value = "officerId") Integer officerId, 
				@RequestParam(value = "assignmentIds[]", required = false) Integer[] assignmentIds,
				@RequestParam(value = "planDate", required = false) String planDate,
				@RequestParam(value = "assignedCollectionDate", required = false) String assignedCollectionDate) {
		try {
			List<ItineraryPlanOutletModel> planAssignments = service.getImportedAssignment(officerId, 
					assignmentIds == null ? null : Arrays.asList(assignmentIds), 
					planDate == null ? null : commonService.getDate(planDate), 
					assignedCollectionDate == null ? null : commonService.getDate(assignedCollectionDate));

			for(ItineraryPlanOutletModel model : planAssignments ) {
				model.getAssignmentIds().add(model.getId());
			}
			
			return planAssignments;
		} catch (Exception e) {
			logger.error("queryImportedAssignments", e);
		}
		return null;
	}
	
	@PreAuthorize("hasPermission(#user, 4) || hasPermission(#user, 16) || hasPermission(#user, 256)")
	@RequestMapping(value = "updateDefaultMajorLocation", method = RequestMethod.POST)
	public @ResponseBody String updateDefaultMajorLocation(Integer outletId, String majorLocation){
		try{
			outletService.updateMajorLocation(outletId, majorLocation);
			return "Success";
		}
		catch(Exception ex){
			logger.error("update major location fail", ex);
			return "Fail";
		}
	}
	
	@RequestMapping(value = "getUserPlannedDates")
	public @ResponseBody List<String> getUserPlannedDates(Integer userId){
		List<String> dates = service.getFuturePlanDate(userId);		
		return dates;
	}
	
}
