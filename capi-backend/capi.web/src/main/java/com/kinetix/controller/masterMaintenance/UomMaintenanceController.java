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

import capi.entity.Uom;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.UomEditModel;
import capi.model.masterMaintenance.UomModel;
import capi.service.masterMaintenance.UOMCategoryService;
import capi.service.masterMaintenance.UomService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1105")
@FuncCode("UF1105")
@Controller("UomMaintenanceController")
@RequestMapping("masterMaintenance/UomMaintenance")
public class UomMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(UomMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private UomService service;
	
	@Autowired
	private UOMCategoryService uomCategoryservice;

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
	public @ResponseBody DatatableResponseModel<UomModel>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Integer uomCategoryId) {
		try {
			return service.getUomList(requestModel, uomCategoryId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit UOM
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			Uom item = null;
			if ("edit".equals(act)) {
				item = service.getUomById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/UomMaintenance/home";
				}
			} else {
				item = new Uom();
			}
			
			model.addAttribute("model", item);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	

	/**
	 * Get UOM Category select2 format
	 */
	@RequestMapping(value = "queryUOMCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
	queryUOMCategorySelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return uomCategoryservice.queryUOMCategorySelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryUOMCategorySelect2", e);
		}
		return null;
	}
	
	
	/**
	 * Save uom
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@ModelAttribute UomEditModel item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes) {
		try { 
						
			if(!service.saveUom(item)){
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00011", null, locale));
				return "redirect:/masterMaintenance/UomMaintenance/home";
			}
			
			redirectAttributes.addFlashAttribute(
					SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00001", null, locale));
			
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/UomMaintenance/home";
	}
	


	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteUom(id)) {
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
