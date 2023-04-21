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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.itineraryPlanning.QCItineraryPlanApprovalTableList;
import capi.model.itineraryPlanning.QCItineraryPlanModel;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.itineraryPlanning.ItineraryPlanningService;
import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1704")
@FuncCode("UF1704")
@Controller("QCItineraryPlanApprovalController")
@RequestMapping("itineraryPlanning/QCItineraryPlanApproval")
public class QCItineraryPlanApprovalController {

	private static final Logger logger = LoggerFactory.getLogger(QCItineraryPlanApprovalController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private ItineraryPlanningService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonService commonService;
	
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
	public @ResponseBody DatatableResponseModel<QCItineraryPlanApprovalTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth) {
		try {

			return service.queryQCItineraryPlanApproval(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit QC Itinerary Plan Approval
	 */
	//@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2)")
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id") Integer id, Model model, Locale locale,
			RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			
			QCItineraryPlanModel plan;

			plan = service.getQCItineraryPlanModel(id);	
			
			model.addAttribute("model", plan);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Approve Itinerary Plan
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "approve", method = RequestMethod.POST)
	public String approve(@RequestParam(value = "id") ArrayList<Integer> qcItineraryPlanIds, 
			Model model, Locale locale,
			RedirectAttributes redirectAttributes) {

		try {

			if (!service.setQCItineraryPlanStatus(qcItineraryPlanIds, "Approved", null)) {
				
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
	 * Approve Itinerary Plan in Edit
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveView", method = RequestMethod.POST)
	public String approveView(@RequestParam(value = "id") ArrayList<Integer> qcItineraryPlanIds, 
			Model model, Locale locale,
			RedirectAttributes redirectAttributes) {

		try {

			if (!service.setQCItineraryPlanStatus(qcItineraryPlanIds, "Approved", null)) {
				
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
		return "redirect:/itineraryPlanning/QCItineraryPlanApproval/home";

	}

	/**
	 * Reject
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "reject", method = RequestMethod.POST)
	public String reject(@RequestParam(value = "id") ArrayList<Integer> qcItineraryPlanIds, 
			@RequestParam(value = "reason") String rejectReason, 
			Model model, Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
			
			if (!service.setQCItineraryPlanStatus(qcItineraryPlanIds, "Rejected", rejectReason)) {
				
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
	 * Reject in Edit Model
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "rejectView", method = RequestMethod.POST)
	public String rejectView(@RequestParam(value = "id") List<Integer> qcItineraryPlanIds, 
			@RequestParam(value = "reason") String rejectReason, 
			Model model, Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
			
			if (!service.setQCItineraryPlanStatus(qcItineraryPlanIds, "Rejected", rejectReason)) {
				
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
		return "redirect:/itineraryPlanning/QCItineraryPlanApproval/home";

	}
	
}
