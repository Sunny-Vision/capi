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

import capi.entity.UOMConversion;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.UOMConversionEditModel;
import capi.model.masterMaintenance.UOMConversionTableList;
import capi.service.masterMaintenance.UOMConversionService;
import capi.service.masterMaintenance.UomService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */
@Secured("UF1115")
@FuncCode("UF1115")
@Controller("UOMConversionMaintenanceController")
@RequestMapping("masterMaintenance/UOMConversionMaintenance")
public class UOMConversionMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(UOMConversionMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;
	
	@Autowired
	private UomService uomService;

	@Autowired
	private UOMConversionService service;

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
	public @ResponseBody DatatableResponseModel<UOMConversionTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.queryUOMConversion(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit UOM Conversion
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act,
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			UOMConversion item = null;
			
			if ("edit".equals(act)) {
				item = service.getUOMConversionById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/UOMConversionMaintenance/home";
				}
			} else {
				item = new UOMConversion();
				model.addAttribute("act", "add");
			}
			
			UOMConversionEditModel editModel = service.convertEntityToModel(item);

			model.addAttribute("model", editModel);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Get UOM select2 format
	 */
	@RequestMapping(value = "queryUomSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
	queryUomSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return uomService.queryUomSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryBaseUomSelect2", e);
		}
		return null;
	}
	
	/**
	 * Save UOM Conversion
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@RequestParam(value = "act", required = false) String act,
			@ModelAttribute UOMConversionEditModel item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
			if (item.getId() != null) {
				if (service.getUOMConversionById(item.getId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/UOMConversionMaintenance/home";
				}
			}
			
			if (!service.saveUOMConversion(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/masterMaintenance/UOMConversionMaintenance/edit";
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/UOMConversionMaintenance/home";
	}
	


	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteUOMConversions(id)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));				
			}
			else{
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));				
			}	
		} catch (Exception e) {
			logger.error("delete", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
}
