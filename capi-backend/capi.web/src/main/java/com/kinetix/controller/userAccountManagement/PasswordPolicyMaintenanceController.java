package com.kinetix.controller.userAccountManagement;

import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.model.SystemConstant;
import capi.model.userAccountManagement.PasswordPolicyDisplayModel;
import capi.model.userAccountManagement.PasswordPolicySaveModel;
import capi.service.userAccountManagement.PasswordPolicyService;

/**
 * UF-UF1301 Field Experience Maintenance
 */

@Secured("UF1307")
@FuncCode("UF1307")
@Controller("PasswordPolicyMaintenanceController")
@RequestMapping("userAccountManagement/PasswordPolicyMaintenance")
public class PasswordPolicyMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(PasswordPolicyMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;
	
	@Autowired
	private PasswordPolicyService passwordPolicyService;
	
	
	/**
	 * List Password Policy Parameters
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		PasswordPolicyDisplayModel displayModel = passwordPolicyService.getParameters();
		model.addAttribute("displayModel", displayModel);
	}
	
	
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 512)")
	@RequestMapping(value = "saveParameters", method = RequestMethod.POST)
	public String saveParameters (Model model, @ModelAttribute PasswordPolicySaveModel saveModel, RedirectAttributes redirectAttributes, Locale locale){
		
		passwordPolicyService.saveParameters(saveModel);
		redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		return "redirect:/userAccountManagement/PasswordPolicyMaintenance/home";
		
	}

}
