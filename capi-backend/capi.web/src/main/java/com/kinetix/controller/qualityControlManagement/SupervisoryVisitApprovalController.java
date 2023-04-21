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
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.SupervisoryVisitApprovalTableList;
import capi.model.qualityControlManagement.SupervisoryVisitEditModel;
import capi.service.qualityControlManagement.SupervisoryVisitApprovalService;

import com.kinetix.component.FuncCode;


/**
 * RF-2008 Supervisory Visit Approval
 */
@Secured("RF2008")
@FuncCode("RF2008")
@Controller("SupervisoryVisitApprovalController")
@RequestMapping("qualityControlManagement/SupervisoryVisitApproval")
public class SupervisoryVisitApprovalController {
	
	private static final Logger logger = LoggerFactory.getLogger(SupervisoryVisitApprovalController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private SupervisoryVisitApprovalService service;

	/**
	 * List supervisory visit approval
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<SupervisoryVisitApprovalTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication authentication) {

		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)authentication.getDetails();
			
			int authorityLevel = detail.getAuthorityLevel();
			
			boolean isBusinessData = false;
			if( ((authorityLevel & 256) == 256) || ((authorityLevel & 2048) == 2048) )
				isBusinessData = true;
			
			List<Integer> actedUsers = detail.getActedUsers();
			actedUsers.add(detail.getUserId());
			
			return service.getSupervisoryVisitApprovalList(requestModel, isBusinessData, actedUsers, detail.getUserId(), authorityLevel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Supervisory Visit Approval
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, @RequestParam(value = "fieldOfficer", required = false) String fieldOfficer,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			SupervisoryVisitForm item = null;
			item = service.getSupervisoryVisitById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
			}

			SupervisoryVisitEditModel editModel = service.convertEntityToModel(item, id, fieldOfficer);

			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Approve Supervisory Visit at edit.jsp
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "approve", method = RequestMethod.POST)
	public String approve(@ModelAttribute SupervisoryVisitEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getSupervisoryVisitFormId() != null) {
				if (service.getSupervisoryVisitById(item.getSupervisoryVisitFormId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
				}
			}
			
			if (!service.approveSupervisoryVisit(item)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
	}

	/**
	 * Approve Supervisory Visit at home.jsp
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveAtHome", method = RequestMethod.POST)
	public String approveAtHome(Model model, Locale locale, RedirectAttributes redirectAttributes,
							@RequestParam(value = "id", required = false) ArrayList<Integer> id) {
		try {
			if (!service.approveSupervisoryVisit(id)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "/partial/messageRibbons";
			}		
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
			return "/partial/messageRibbons";
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		return "/partial/messageRibbons";
	}

	/**
	 * Reject Spot Check at edit.jsp
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "reject", method = RequestMethod.POST)
	public String reject(@ModelAttribute SupervisoryVisitEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getSupervisoryVisitFormId() != null) {
				if (service.getSupervisoryVisitById(item.getSupervisoryVisitFormId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
				}
			}
			
			if (!service.rejectSupervisoryVisit(item)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
	}

	/**
	 * Reject Spot Check at home.jsp
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "rejectAtHome", method = RequestMethod.POST)
	public String rejectAtHome(Model model, Locale locale, RedirectAttributes redirectAttributes,
							@RequestParam(value = "id", required = false) ArrayList<Integer> id,
							@RequestParam(value = "rejectReason", required = false) String rejectReason) {
		try {
			if (!service.rejectSupervisoryVisit(id, rejectReason)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/qualityControlManagement/SupervisoryVisitApproval/home";
	}

}