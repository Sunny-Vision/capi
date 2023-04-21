package com.kinetix.controller.masterMaintenance;

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

import com.kinetix.component.FuncCode;

import capi.entity.PointToNote;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.commonLookup.ProductLookupDetailTableList;
import capi.model.commonLookup.QuotationLookupTableList;
import capi.model.commonLookup.UnitLookupTableList;
import capi.model.masterMaintenance.OutletTableList;
import capi.model.masterMaintenance.PointToNoteEditModel;
import capi.model.masterMaintenance.PointToNoteExpiryNowModel;
import capi.model.masterMaintenance.PointToNoteTableList;
import capi.service.lookup.OutletLookupService;
import capi.service.lookup.ProductLookupService;
import capi.service.lookup.QuotationLookupService;
import capi.service.lookup.UnitLookupService;
import capi.service.masterMaintenance.PointToNoteService;


/**
 * UF-1118 Point To Note Maintenance
 */
@Secured("UF1118")
@FuncCode("UF1118")
@Controller("PointToNoteController")
@RequestMapping("masterMaintenance/PointToNoteMaintenance")
public class PointToNoteMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(PointToNoteMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private PointToNoteService service;

	@Autowired
	private OutletLookupService outletLookupService;

	@Autowired
	private ProductLookupService productLookupService;

	@Autowired
	private QuotationLookupService quotationLookupService;

	@Autowired
	private UnitLookupService unitLookupService;
	
	
	/**
	 * List outlet
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<PointToNoteTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.getTableList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Expiry now
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "expiryNow", method = RequestMethod.POST)
	public String expiryNow(Locale locale, RedirectAttributes redirectAttributes, @ModelAttribute PointToNoteExpiryNowModel model) {
		try {
			service.expiryNow(model);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("expiryNow", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/PointToNoteMaintenance/home";
	}
	
	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(Locale locale, RedirectAttributes redirectAttributes, @ModelAttribute PointToNoteExpiryNowModel model) {
		try {
			if (!service.delete(model))
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
			else
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));
		} catch (Exception e) {
			logger.error("delete", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
		}
		return "redirect:/masterMaintenance/PointToNoteMaintenance/home";
	}
	
	/**
	 * Edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			PointToNote entity = null;
			if (id != null) {
				entity = service.getById(id);
				if (entity == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/PointToNoteMaintenance/home";
				}
			} else {
				entity = new PointToNote();
			}
			
			PointToNoteEditModel editModel = service.convertEntityToModel(entity);
			model.addAttribute("model", editModel);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute PointToNoteEditModel editModel, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			service.save(editModel);
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/PointToNoteMaintenance/home";
	}
	
	/**
	 * Get outlet detail
	 */
	@RequestMapping(value = "getOutletDetail")
	public @ResponseBody List<OutletTableList> getOutletDetail(@RequestParam(value = "ids[]", required = false) Integer[] ids) {
		try {
			if (ids == null || ids.length == 0) return null;
			return outletLookupService.getTableListByIds(ids);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get product detail
	 */
	@RequestMapping(value = "getProductDetail")
	public @ResponseBody List<ProductLookupDetailTableList> getProductDetail(@RequestParam(value = "ids[]", required = false) Integer[] ids) {
		try {
			if (ids == null || ids.length == 0) return null;
			return productLookupService.getTableListByIds(ids);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get quotation detail
	 */
	@RequestMapping(value = "getQuotationDetail")
	public @ResponseBody List<QuotationLookupTableList> getQuotationDetail(@RequestParam(value = "ids[]", required = false) Integer[] ids) {
		try {
			if (ids == null || ids.length == 0) return null;
			return quotationLookupService.getTableListByIds(ids);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get unit detail
	 */
	@RequestMapping(value = "getUnitDetail")
	public @ResponseBody List<UnitLookupTableList> getUnitDetail(@RequestParam(value = "ids[]", required = false) Integer[] ids) {
		try {
			if (ids == null || ids.length == 0) return null;
			return unitLookupService.getTableListByIds(ids);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
}