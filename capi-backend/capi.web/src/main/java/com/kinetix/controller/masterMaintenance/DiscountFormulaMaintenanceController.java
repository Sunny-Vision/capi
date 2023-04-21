package com.kinetix.controller.masterMaintenance;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.entity.DiscountFormula;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.DiscountCalculatorItemsList;
import capi.service.masterMaintenance.DiscountFormulaService;


/**
 * UF-1108 Discount Formula Maintenance
 */
@Secured("UF1108")
@FuncCode("UF1108")
@Controller("DiscountFormulaMaintenanceController")
@RequestMapping("masterMaintenance/DiscountFormulaMaintenance")
public class DiscountFormulaMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(DiscountFormulaMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private DiscountFormulaService service;

	/**
	 * List discount formula
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<DiscountFormula>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.getDiscountFormulaList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteDiscountFormula(id)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "/partial/messageRibbons";
			}		
			model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));
			return "/partial/messageRibbons";
		} catch (Exception e) {
			logger.error("delete", e);
		}
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		return "/partial/messageRibbons";
	}
	
	/**
	 * Edit discount formula
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			DiscountFormula item = null;
			if (id != null) {
				item = service.getDiscountFormulaById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/DiscountFormulaMaintenance/home";
				}
			} else {
				item = new DiscountFormula();
				item.setStatus("Enable");
			}
			model.addAttribute("model", item);
			
			DiscountCalculatorItemsList itemlist = service.getDiscountItems();
			model.addAttribute("itemlist", itemlist);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Save discount calculator fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute DiscountFormula item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getId() != null) {
				if (service.getDiscountFormulaById(item.getId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/DiscountFormulaMaintenance/home";
				}
			}
			
			service.saveDiscountFormula(item);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/DiscountFormulaMaintenance/home";
	}
}
