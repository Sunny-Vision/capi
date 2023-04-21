package com.kinetix.controller.userAccountManagement;

import java.security.Principal;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.model.SystemConstant;
import capi.model.userAccountManagement.ChangePasswordEditModel;
import capi.service.userAccountManagement.ChangePasswordService;
import capi.service.userAccountManagement.PasswordPolicyService;

import com.kinetix.component.FuncCode;


/**
 * UF-1306 CHange Password
 */
@Secured("UF1306")
@FuncCode("UF1306")
@Controller("ChangePasswordController")
@RequestMapping("userAccountManagement/ChangePassword")
public class ChangePasswordController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChangePasswordController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private ChangePasswordService service;

	@Autowired
	private PasswordPolicyService passwordPolicyService;
	
	
	/**
	 * Change Password
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(Model model) {
		try {
			logger.info("Change Password Controller edit.");
		} catch(Exception e) {
			logger.error("edit", e);
		}
		String minLength = passwordPolicyService.getParameters().getMinLength();
		model.addAttribute("pwMinLength", minLength);
		return null;
	}

	/**
	 * Save Password
	 */
	//TODO CR6 004
	//TODO CR6 006
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute ChangePasswordEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes, Principal principal) {

		System.out.println("==============ChangePasswordController=====save");
		System.out.println("locale:"+locale.toString());
		try {

			
			String saveResult = service.savePassword(item, principal.getName());
			
			if (!"Success".equals(saveResult)) {
				if ("PasswordMinimumAge".equals(saveResult)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00160", new Object[]{passwordPolicyService.getMinAgeInteger()}, locale));
				} else if ("EnforcedPassword".equals(saveResult)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00158", new Object[]{passwordPolicyService.getParameters().getEnforcePasswordHistory()}, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00168", null, locale));
				}
			} else {
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			}

		} catch(Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/userAccountManagement/ChangePassword/edit";
	}

}