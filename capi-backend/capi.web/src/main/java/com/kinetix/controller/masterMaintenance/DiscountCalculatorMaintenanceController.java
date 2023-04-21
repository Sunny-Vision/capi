package com.kinetix.controller.masterMaintenance;

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
import capi.model.masterMaintenance.DiscountCalculatorItemsList;
import capi.service.masterMaintenance.DiscountFormulaService;


/**
 * UF-1107 Discount Calculator Maintenance
 */
@Secured("UF1107")
@FuncCode("UF1107")
@Controller("DiscountCalculatorMaintenanceController")
@RequestMapping("masterMaintenance/DiscountCalculatorMaintenance")
public class DiscountCalculatorMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(DiscountCalculatorMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private DiscountFormulaService service;
	
	/**
	 * List discount calculator fields
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		try {
			DiscountCalculatorItemsList itemlist = service.getDiscountItems();
			model.addAttribute("itemlist", itemlist);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * Save discount calculator fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute DiscountCalculatorItemsList list, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			service.saveDiscountItems(list);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/DiscountCalculatorMaintenance/home";
	}
}
