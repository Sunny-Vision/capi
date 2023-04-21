package com.kinetix.controller.itineraryPlanning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.itineraryPlanning.ItineraryPlanEditModel;
import capi.model.itineraryPlanning.ItineraryPlanTableList;
import capi.service.AppConfigService;
import capi.service.UserService;
import capi.service.itineraryPlanning.ItineraryPlanningService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1702")
@FuncCode("UF1702")
@Controller("ItineraryPlanApprovalController")
@RequestMapping("itineraryPlanning/ItineraryPlanApproval")
public class ItineraryPlanApprovalController {

	private static final Logger logger = LoggerFactory.getLogger(ItineraryPlanApprovalController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private ItineraryPlanningService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AppConfigService appConfigService;
	
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
	public @ResponseBody DatatableResponseModel<ItineraryPlanTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth) {
		try {
//			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//			List<Integer> officerIds = new ArrayList<Integer>();
//			if ((detail.getAuthorityLevel() & 4) != 4) {
//				officerIds.add(detail.getUserId());
//			}
			return service.queryItineraryPlanApproval(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * View Itinerary Plan
	 */
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(@RequestParam(value = "id" ) Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			
			ItineraryPlanEditModel item;			
			item = service.getItineraryPlanEditModel(id);		
			User user =	userService.getUserById(item.getUserId());
			model.addAttribute("user", user);
			model.addAttribute("model", item);
			model.addAttribute("googleBrowserKey", appConfigService.getGoogleBroswerKey());

		} catch (Exception e) {
			logger.error("view", e);
		}
		return null;
	}

	/**
	 * Approve Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "approve", method = RequestMethod.POST)
	public String approve(@RequestParam(value = "id") ArrayList<String> itineraryPlanIds, 
			@RequestParam(value = "page", required = false) String page,
			Model model, Locale locale,
			RedirectAttributes redirectAttributes) {

		try {
			
			Map<Integer, Integer> itineraryPlanIdWithVersion = new HashMap<Integer, Integer>();
			
			for (String item : itineraryPlanIds) {
				itineraryPlanIdWithVersion.put(new Integer(item.split("_")[0]) , new Integer(item.split("_")[1]));
			}

			if (!service.setItineraryPlanStatus(itineraryPlanIdWithVersion, "Approved", null)) {
				
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00011", null, locale));
				
			} else {
				
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
	 * Approve Itinerary Plan in View
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveView", method = RequestMethod.POST)
	public String approveView(@RequestParam(value = "id") ArrayList<String> itineraryPlanIds, 
			Model model, Locale locale,
			RedirectAttributes redirectAttributes) {

		try {
			
			Map<Integer, Integer> itineraryPlanIdWithVersion = new HashMap<Integer, Integer>();
			
			for (String item : itineraryPlanIds) {
				itineraryPlanIdWithVersion.put(new Integer(item.split("_")[0]) , new Integer(item.split("_")[1]));
			}

			if (!service.setItineraryPlanStatus(itineraryPlanIdWithVersion, "Approved", null)) {
				
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00011", null, locale));
				
			} else {
				
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00003", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("approveView", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00058", null, locale));
		}
		return "redirect:/itineraryPlanning/ItineraryPlanApproval/home";

	}

	/**
	 * Reject
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "reject", method = RequestMethod.POST)
	public String reject(@RequestParam(value = "id") ArrayList<String> itineraryPlanIds, 
			@RequestParam(value = "reason") String rejectReason, 
			Model model, Locale locale,
			RedirectAttributes redirectAttributes) {
		try {

			Map<Integer, Integer> itineraryPlanIdWithVersion = new HashMap<Integer, Integer>();
			
			for (String item : itineraryPlanIds) {
				itineraryPlanIdWithVersion.put(new Integer(item.split("_")[0]) , new Integer(item.split("_")[1]));
			}
			
			if (!service.setItineraryPlanStatus(itineraryPlanIdWithVersion, "Rejected", rejectReason)) {
				
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
	 * Reject
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "rejectView", method = RequestMethod.POST)
	public String rejectView(@RequestParam(value = "id") ArrayList<String> itineraryPlanIds, 
			@RequestParam(value = "reason") String rejectReason, 
			Model model, Locale locale,
			RedirectAttributes redirectAttributes) {
		try {

			Map<Integer, Integer> itineraryPlanIdWithVersion = new HashMap<Integer, Integer>();
			
			for (String item : itineraryPlanIds) {
				itineraryPlanIdWithVersion.put(new Integer(item.split("_")[0]) , new Integer(item.split("_")[1]));
			}
			
			if (!service.setItineraryPlanStatus(itineraryPlanIdWithVersion, "Rejected", rejectReason)) {
				
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00011", null, locale));
				return "/partial/messageRibbons";
				
			} else {
				
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00004", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("reject", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00059", null, locale));
		}
		return "redirect:/itineraryPlanning/ItineraryPlanApproval/home";

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
	
}
