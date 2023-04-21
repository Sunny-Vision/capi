package com.kinetix.controller.masterMaintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
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

import capi.entity.QuotationLoading;
import capi.entity.VwOutletTypeShortForm;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.QuotationLoadingEditModel;
import capi.model.masterMaintenance.QuotationLoadingTableList;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.QuotationLoadingService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1408")
@FuncCode("UF1408")
@Controller("QuotationLoadingMaintenanceController")
@RequestMapping("masterMaintenance/QuotationLoadingMaintenance")
public class QuotationLoadingMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(QuotationLoadingMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private QuotationLoadingService service;
	
	@Autowired
	private OutletService outletService;
	
	@Autowired
	private DistrictService districtService;

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
	public @ResponseBody DatatableResponseModel<QuotationLoadingTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.queryQuotationLoading(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit Quotation Loading
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act,
			@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "outletType", required = false) String outletType,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			QuotationLoading item = null;
			
			String[] shortCode = null;
			if(!StringUtils.isEmpty(outletType)) {
				shortCode = outletType.split("-");
			}
			
			if ("edit".equals(act)) {
				item = service.getQuotationLoadingById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/QuotationLoadingMaintenance/home";
				}
			} else {
				item = new QuotationLoading();
				model.addAttribute("act", "add");
			}
			
//			QuotationLoadingEditModel editModel = service.convertEntityToModel(item);
			QuotationLoadingEditModel editModel = null;
			if(shortCode != null && shortCode.length > 0) {
				editModel = service.convertEntityToModel(item, shortCode[0].trim());
			} else {
				editModel = service.convertEntityToModel(item, null);
			}
			
			List<VwOutletTypeShortForm> outletTypes = outletService.getOutletTypes();
			
			model.addAttribute("outletTypes", outletTypes);
			model.addAttribute("model", editModel);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}


	/**
	 * Save Quotation Loading
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@RequestParam(value = "act", required = false) String act,
			@ModelAttribute QuotationLoadingEditModel item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
			
		if (item.getId() != null) {
			if (service.getQuotationLoadingById(item.getId()) == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/masterMaintenance/QuotationLoadingMaintenance/home";
			}
		}

		if (!service.saveQuotationLoading(item)){
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			model.addAttribute("model", item);
			model.addAttribute("act", act);
			return "/masterMaintenance/QuotationLoadingMaintenance/edit";
		}
		
		redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale) 
				+ "<br>" + messageSource.getMessage("I00010", null, locale));
	} catch (Exception e) {
		logger.error("home", e);
		redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
	}
		return "redirect:/masterMaintenance/QuotationLoadingMaintenance/home";
	}
	


	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteQuotationLoadings(id)) {
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
	
	
	/**
	 * Get district select2 format
	 */
	@RequestMapping(value = "queryDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDistrictSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	} 
}
