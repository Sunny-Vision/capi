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

import capi.entity.SpotCheckForm;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.SpotCheckApprovalTableList;
import capi.model.qualityControlManagement.SpotCheckEditModel;
import capi.service.qualityControlManagement.SpotCheckApprovalService;

import com.kinetix.component.FuncCode;


/**
 * RF-2013 Spot Check Approval
 */
@Secured("RF2013")
@FuncCode("RF2013")
@Controller("SpotCheckApprovalController")
@RequestMapping("qualityControlManagement/SpotCheckApproval")
public class SpotCheckApprovalController {
	
	private static final Logger logger = LoggerFactory.getLogger(SpotCheckApprovalController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private SpotCheckApprovalService service;

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
	public @ResponseBody DatatableResponseModel<SpotCheckApprovalTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication authentication) {

		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)authentication.getDetails();
			
			int authorityLevel = detail.getAuthorityLevel();
			
			boolean isBusinessData = false;
			if( ((authorityLevel & 256) == 256) || ((authorityLevel & 2048) == 2048) )
				isBusinessData = true;
			
			List<Integer> actedUsers = detail.getActedUsers();
			actedUsers.add(detail.getUserId());
			
			return service.getSpotCheckApprovalList(requestModel, isBusinessData, actedUsers, detail.getUserId(), authorityLevel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Spot Check
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, 
			@RequestParam(value = "officerId", required = false) Integer officerId, @RequestParam(value = "officerCode", required = false) String officerCode,
			@RequestParam(value = "spotCheckDate", required = false) String spotCheckDate, Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			SpotCheckForm item = null;
			item = service.getSpotCheckById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SpotCheckApproval/home";
			}

			SpotCheckEditModel editModel = service.convertEntityToModel(item, id, officerId, officerCode, spotCheckDate);

			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Approve Spot Check at edit.jsp
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "approve", method = RequestMethod.POST)
	public String approve(@ModelAttribute SpotCheckEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getSpotCheckFormId() != null) {
				if (service.getSpotCheckById(item.getSpotCheckFormId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SpotCheckApproval/home";
				}
			}
			
			if (!service.approveSpotCheck(item)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SpotCheckApproval/home";
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/qualityControlManagement/SpotCheckApproval/home";
	}

	/**
	 * Approve Spot Check at home.jsp
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveAtHome", method = RequestMethod.POST)
	public String approveAtHome(Model model, Locale locale, RedirectAttributes redirectAttributes,
							@RequestParam(value = "id", required = false) ArrayList<Integer> id) {
		try {
			if (!service.approveSpotCheck(id)) {
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
	public String reject(@ModelAttribute SpotCheckEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getSpotCheckFormId() != null) {
				if (service.getSpotCheckById(item.getSpotCheckFormId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SpotCheckApproval/home";
				}
			}
			
			if (!service.rejectSpotCheck(item)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SpotCheckApproval/home";
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/qualityControlManagement/SpotCheckApproval/home";
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
			if (!service.rejectSpotCheck(id, rejectReason)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SpotCheckApproval/home";
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/qualityControlManagement/SpotCheckApproval/home";
	}

}