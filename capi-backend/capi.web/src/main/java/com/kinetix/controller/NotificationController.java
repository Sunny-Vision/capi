package com.kinetix.controller;

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.Notification;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.NotificationTableList;
import capi.model.SystemConstant;
import capi.service.CommonService;
import capi.service.NotificationService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */
@Secured("UF2701")
@FuncCode("UF2701")
@Controller("NotificationController")
@RequestMapping("Notification")
public class NotificationController {	
	private static final Logger logger = LoggerFactory
			.getLogger(NotificationController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private NotificationService service;
	
	@Autowired
	private CommonService commonService;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model, Authentication auth) {
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		model.addAttribute("numberOfUnread", service.countUnreadNotification(detail.getUserId()));
	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query", method = RequestMethod.GET)
	public @ResponseBody DatatableResponseModel<NotificationTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth, Boolean isUnReadOnly, Boolean isFlagOnly) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return service.getNotificationList(requestModel, detail.getUserId(), isUnReadOnly, isFlagOnly);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * View Notification
	 */
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Notification item = null;
		
			item = service.getNotificationById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/Notification/home";
			}
			
			service.updateReadStatus(item);
			String createdDate = commonService.formatDateTime(item.getCreatedDate());
			
			model.addAttribute("createdDate", createdDate);
			model.addAttribute("model", item);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Delete
	 */
	//@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale, 
							RedirectAttributes redirectAttributes) {
		try {
			if (!service.deleteActivityCodes(id)) {
//				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
			}
			else{
//				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));
			}	
		} catch (Exception e) {
			logger.error("delete", e);
//			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		}
		
//		return "/partial/messageRibbons";
		return "redirect:/Notification/home";
	}
	
	
	/**
	 * Read
	 */
	//@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "read")
	public String read(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale, 
							RedirectAttributes redirectAttributes) {
		try {
			if (!service.markAsRead(id)) {
//				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));	
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
			}
			else{
//				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}	
		} catch (Exception e) {
			logger.error("read", e);
//			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
//		return "/partial/messageRibbons";
		return "redirect:/Notification/home";
	}
	
	/**
	 * Flag
	 */
	///@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "flag")
	public String flag(@RequestParam(value = "id") ArrayList<Integer> id, Boolean flag, Model model, Locale locale) {
		try {
			if (!service.toggleFlag(id, flag)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));				
			}
			else{
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}	
		} catch (Exception e) {
			logger.error("delete", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
}
