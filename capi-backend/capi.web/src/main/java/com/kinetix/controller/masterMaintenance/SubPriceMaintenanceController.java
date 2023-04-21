package com.kinetix.controller.masterMaintenance;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.SubPriceType;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.SubPriceFieldList;
import capi.model.masterMaintenance.SubPriceTypeModel;
import capi.service.masterMaintenance.SubPriceService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1109")
@FuncCode("UF1109")
@Controller("SubPriceMaintenanceController")
@RequestMapping("masterMaintenance/SubPriceMaintenance")
public class SubPriceMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(SubPriceMaintenanceController.class);

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
	public @ResponseBody DatatableResponseModel<SubPriceTypeModel>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.querySubPrice(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Sub Price Type
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			SubPriceType item = null;
			List<SubPriceFieldList> fields  = new ArrayList<SubPriceFieldList>();
			if (id != null) {
				item = service.getSubPriceById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/SubPriceMaintenance/home";
				}
				fields = service.getSubPriceFieldByType(item.getId());
			} else {
				item = new SubPriceType();
				item.setStatus("Enable");
			}
			
			model.addAttribute("model", item);
			model.addAttribute("fields", fields);
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
	public String save(
			@ModelAttribute SubPriceTypeModel item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "subPriceFieldId[]", required = false) Integer[] subPriceFieldId) {
		try {
			if (item.getId() != null) {
				if (service.getSubPriceById(item.getId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/SubPriceMaintenance/home";
				}
			}

			service.saveSubPriceType(item, subPriceFieldId);
			redirectAttributes.addFlashAttribute(
					SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/SubPriceMaintenance/home";
	}

	/**
	 * Delete
	 */	
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteSubPriceTypes(id)) {
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
