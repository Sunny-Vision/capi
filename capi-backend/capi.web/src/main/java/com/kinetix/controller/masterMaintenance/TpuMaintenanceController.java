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

import capi.entity.Tpu;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.TpuEditModel;
import capi.model.masterMaintenance.TpuTableList;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.TpuService;

import com.kinetix.component.FuncCode;


/**
 * UF-1103 Tpu Maintenance
 */
@Secured("UF1103")
@FuncCode("UF1103")
@Controller("TpuMaintenanceController")
@RequestMapping("masterMaintenance/TpuMaintenance")
public class TpuMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(TpuMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private TpuService service;
	
	@Autowired
	private DistrictService districtService;
	
	/**
	 * List TPU
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<TpuTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "districtId", required = false) Integer districtId) {

		try {
			return service.getTpuList(requestModel, districtId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit TPU
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model,  DatatableRequestModel requestModel, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			Tpu item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getTpuById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/TpuMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new Tpu();
			}
			
			TpuEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Get district select format
	 */
	@RequestMapping(value = "queryDistrictSelect", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDistrictSelect(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Save TPU fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@RequestParam(value = "act", required = false) String act,
			@ModelAttribute TpuEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getTpuId() != null) {
				if (service.getTpuById(item.getTpuId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/TpuMaintenance/home";
				}
			}
			
			if (!service.saveTpu(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00018", null, locale));
				item.setDistrictLabel(service.getDistrictLabel(item.getDistrictId()));
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/masterMaintenance/TpuMaintenance/edit";
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/TpuMaintenance/home";
	}
	
	/**
	 * Delete TPU
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteTpu(id)) {
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