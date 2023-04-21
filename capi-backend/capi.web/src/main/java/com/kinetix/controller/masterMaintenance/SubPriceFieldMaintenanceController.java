package com.kinetix.controller.masterMaintenance;

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.SubPriceField;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.service.masterMaintenance.SubPriceService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */
@FuncCode("UF1116")
@Controller("SubPriceFieldMaintenanceController")
@RequestMapping("masterMaintenance/SubPriceFieldMaintenance")
public class SubPriceFieldMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(SubPriceFieldMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private SubPriceService service;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model) {
	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody
	DatatableResponseModel<SubPriceField> query(
			Locale locale,
			Model model,
			DatatableRequestModel requestModel,
			@RequestParam(value = "subPriceFieldId[]", required = false) Integer[] subPriceFieldId) {
		try {
			return service.querySubPriceField(requestModel, subPriceFieldId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Sub Price Field
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			SubPriceField item = null;
			if (id != null) {
				item = service.getSubPriceFieldById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/SubPriceFieldMaintenance/home";
				}
			} else {
				item = new SubPriceField();
			}
			model.addAttribute("model", item);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save Sub Price Field
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute SubPriceField item, Model model,
			Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getId() != null) {
				if (service.getSubPriceFieldById(item.getId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/SubPriceFieldMaintenance/home";
				}
			}

			service.saveSubPriceField(item);
			redirectAttributes.addFlashAttribute(
					SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/SubPriceFieldMaintenance/home";
	}

	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteSubPriceField(id)) {
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
