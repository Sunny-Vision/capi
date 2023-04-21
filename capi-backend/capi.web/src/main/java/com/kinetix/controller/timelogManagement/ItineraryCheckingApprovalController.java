package com.kinetix.controller.timelogManagement;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.WorkingSessionSetting;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.UserAccessModel;
import capi.model.itineraryPlanning.ItineraryPlanEditModel;
import capi.model.timeLogManagement.ItineraryCheckingTableList;
import capi.model.timeLogManagement.TimeLogModel;
import capi.service.AppConfigService;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.itineraryPlanning.ItineraryPlanningService;
import capi.service.timeLogManagement.TimeLogService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1802")
@FuncCode("UF1802")
@Controller("ItineraryCheckingApprovalController")
@RequestMapping("timeLogManagement/ItineraryCheckingApproval")
public class ItineraryCheckingApprovalController {

	private static final Logger logger = LoggerFactory.getLogger(ItineraryCheckingApprovalController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private TimeLogService service;

	@Autowired
	private ItineraryPlanningService itineraryPlanningService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;
	
	@Autowired
	private AppConfigService appConfigService;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model, Authentication auth) {
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		UserAccessModel uam = userService.gatherUserRequiredInfo(userService.getUserById(detail.getUserId()));
		model.addAttribute("uam", uam);

	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<ItineraryCheckingTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth) {
		try {
			return service.queryItineraryCheckingTable(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * View
	 */
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public void edit(@RequestParam(value = "id", required = true) Integer id,
			Locale locale, Model model, Authentication auth) {
		TimeLogModel timeLogModel;

		timeLogModel = service.getTimeLogModel(id);
		Collections.sort(timeLogModel.getFieldworkTimeLogs());
		
		List<String> surveyCountList = new ArrayList<String>();
		surveyCountList.add(new String(SystemConstant.SURVEY_1));
		surveyCountList.add(new String(SystemConstant.SURVEY_2));
		surveyCountList.add(new String(SystemConstant.SURVEY_3));
		model.addAttribute("surveyCountList", surveyCountList);
		
		if (timeLogModel.getIsOtherWorkingSession() != null && timeLogModel.getIsOtherWorkingSession() == true) {
			timeLogModel.setWorkingSessionText(timeLogModel.getOtherWorkingSessionFrom() + " - " + timeLogModel.getOtherWorkingSessionTo() );
		} else {
			WorkingSessionSetting workingSessionSetting = service.getWorkingSessionSettings(timeLogModel.getWorkingSessionId());
			timeLogModel.setWorkingSessionText( commonService.formatTime(workingSessionSetting.getFromTime()) + " - " + commonService.formatTime(workingSessionSetting.getToTime()));
		}
		model.addAttribute("model", timeLogModel);
		model.addAttribute("googleBrowserKey", appConfigService.getGoogleBroswerKey());
		
		try {
			ItineraryPlanEditModel planned;
			Integer planId = itineraryPlanningService.getPlanId(timeLogModel.getUserId(), commonService.getDate(timeLogModel.getDate()));
			if (planId != null ) {
				planned = itineraryPlanningService.getItineraryPlanEditModel(planId);		
				model.addAttribute("planned", planned);
			}
			
			ItineraryPlanEditModel visited;
			visited = service.getItineraryPlanEditModel(timeLogModel);
			model.addAttribute("visited", visited);
			
		} catch (Exception e) {
			logger.error("view", e);
		}
	}

	/**
	 * Approve Time Log
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)")
	@RequestMapping(value = "approve", method = RequestMethod.POST)
	public String approve(@RequestParam(value = "id") ArrayList<Integer> ids, 
			@RequestParam(value = "page", required = false) String page,
			Model model, Locale locale, Authentication auth,
			RedirectAttributes redirectAttributes) {
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		try {
			
			if (!service.setTimeLogStatus(detail.getUserId(), ids, SystemConstant.TIMELOG_STATUS_APPROVED , null, null)) {
				
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00011", null, locale));
				
			} else {
				for (Integer id : ids) {
					TimeLogModel timeLogModel = service.getTimeLogModel(id);
					Collections.sort(timeLogModel.getFieldworkTimeLogs());
					service.addOTCalendarEvent(timeLogModel);
				}
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00003", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("approve", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00058", null, locale));
		}
		return "/partial/messageRibbons";

	}
	
	/**
	 * Approve in View
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)")
	@RequestMapping(value = "approveView", method = RequestMethod.POST)
	public String approveView(@RequestParam(value = "timeLogId") Integer id, 
			@RequestParam(value = "isPreApproval", required = false) Boolean isPreApproval,
			Model model, Locale locale, Authentication auth,
			RedirectAttributes redirectAttributes) {

		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();

		try {
			
			if (!service.setTimeLogStatus(detail.getUserId(), Arrays.asList(id), SystemConstant.TIMELOG_STATUS_APPROVED, null, isPreApproval)) {
				
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00011", null, locale));
				
			} else {
				TimeLogModel timeLogModel = service.getTimeLogModel(id);
				Collections.sort(timeLogModel.getFieldworkTimeLogs());
				service.addOTCalendarEvent(timeLogModel);
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00003", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("approveView", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00058", null, locale));
		}
		return "redirect:/timeLogManagement/ItineraryCheckingApproval/home";

	}

	/**
	 * Reject
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)")
	@RequestMapping(value = "reject", method = RequestMethod.POST)
	public String reject(@RequestParam(value = "id") List<Integer> ids, 
			@RequestParam(value = "reason") String rejectReason, 
			Model model, Locale locale, Authentication auth,
			RedirectAttributes redirectAttributes) {
		try {

			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();

			if (!service.setTimeLogStatus(detail.getUserId(), ids, SystemConstant.TIMELOG_STATUS_REJECTED, rejectReason, null)) {
				
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00011", null, locale));
				
			} else {
				 
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00004", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("reject", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00059", null, locale));	
		}	
		return "/partial/messageRibbons";

	}
	
	/**
	 * Reject in view
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)")
	@RequestMapping(value = "rejectView", method = RequestMethod.POST)
	public String rejectView(@RequestParam(value = "timeLogId") Integer id, 
			@RequestParam(value = "rejectReason") String rejectReason, 
			@RequestParam(value = "isPreApproval", required = false) Boolean isPreApproval,
			Model model, Locale locale, Authentication auth,
			RedirectAttributes redirectAttributes) {
		try {

			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();

			if (!service.setTimeLogStatus(detail.getUserId(), Arrays.asList(id), SystemConstant.TIMELOG_STATUS_REJECTED, rejectReason, isPreApproval)) {
				
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00011", null, locale));

			} else {
				
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00004", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("reject", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00059", null, locale));
		}
		return "redirect:/timeLogManagement/ItineraryCheckingApproval/home";

	}

	
}
