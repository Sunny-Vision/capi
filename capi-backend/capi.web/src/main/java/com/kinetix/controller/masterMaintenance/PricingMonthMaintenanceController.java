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

import capi.entity.PricingFrequency;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.PricingMonthTableList;
import capi.service.masterMaintenance.PricingMonthService;

import com.kinetix.component.FuncCode;


/**
 * UF-1119 Pricing Month Maintenance
 */
@Secured("UF1119")
@FuncCode("UF1119")
@Controller("PricingMonthMaintenanceController")
@RequestMapping("masterMaintenance/PricingMonthMaintenance")
public class PricingMonthMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(PricingMonthMaintenanceController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private PricingMonthService service;

	/**
	 * List Pricing Month
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<PricingMonthTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getPricingMonthList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit pricing month
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			PricingFrequency item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getPricingMonthById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/PricingMonthMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new PricingFrequency();
			}
			
			PricingMonthTableList editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save pricing month fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@RequestParam(value = "act", required = false) String act,
			@ModelAttribute PricingMonthTableList item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getPricingFrequencyId() != null) {
				if (service.getPricingMonthById(item.getPricingFrequencyId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/PricingMonthMaintenance/home";
				}
			}
			
			if (!service.savePricingMonth(act, item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00120", null, locale));
				model.addAttribute("model", item);
				model.addAttribute("act", act);
				return "/masterMaintenance/PricingMonthMaintenance/edit";
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/PricingMonthMaintenance/home";
	}

	/**
	 * Delete pricing frequency 
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deletePricingMonth(id)) {
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

}