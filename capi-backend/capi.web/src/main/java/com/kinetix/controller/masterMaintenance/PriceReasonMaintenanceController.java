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

import capi.entity.PriceReason;
import capi.entity.VwOutletTypeShortForm;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.PriceReasonEditModel;
import capi.model.masterMaintenance.PriceReasonTableList;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PriceReasonService;

import com.kinetix.component.FuncCode;


/**
 * UF-1106 Price Reason Maintenance
 */
@Secured("UF1106")
@FuncCode("UF1106")
@Controller("PriceReasonMaintenanceController")
@RequestMapping("masterMaintenance/PriceReasonMaintenance")
public class PriceReasonMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(PriceReasonMaintenanceController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private PriceReasonService service;
	
	@Autowired
	private OutletService outletService;

	/**
	 * List Price Reason
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		try {
			//List<VwOutletTypeShortForm> outletTypes = service.getOutletTypes();
			//model.addAttribute("outletTypes", outletTypes);
		} catch (Exception e) {
			logger.error("home", e);
		}
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<PriceReasonTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "reasonType", required = false) String reasonType) {

		try {
			return service.getPriceReasonList(requestModel, outletTypeId, reasonType);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Price Reason
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model,  DatatableRequestModel requestModel, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			PriceReason item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getPriceReasonById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/PriceReasonMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new PriceReason();
				item.setReasonType(1);
			}
			
			PriceReasonEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
			
			List<VwOutletTypeShortForm> outletTypes = service.getOutletTypes();
			
			model.addAttribute("outletTypes", outletTypes);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save Price Reason fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute PriceReasonEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getPriceReasonId() != null) {
				if (service.getPriceReasonById(item.getPriceReasonId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/PriceReasonMaintenance/home";
				}
			}
			
			service.savePriceReason(item);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/PriceReasonMaintenance/home";
	}

	/**
	 * Delete Price Reason
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deletePriceReason(id)) {
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

	@RequestMapping(value = "queryOutletTypeSelect2")
	public @ResponseBody Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel queryModel){
		try{
			return outletService.queryOutletTypeSelect2(queryModel);
		}
		catch(Exception ex){
			logger.error("queryOutletTypeSelect2", ex);
		}
		return null;
	}
	
}