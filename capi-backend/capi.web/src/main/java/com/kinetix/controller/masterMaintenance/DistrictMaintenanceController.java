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

import capi.entity.District;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.DistrictEditModel;
import capi.model.masterMaintenance.DistrictTableList;
import capi.service.masterMaintenance.DistrictService;

import com.kinetix.component.FuncCode;


/**
 * UF-1102 District Maintenance
 */
@Secured("UF1102")
@FuncCode("UF1102")
@Controller("DistrictMaintenanceController")
@RequestMapping("masterMaintenance/DistrictMaintenance")
public class DistrictMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(DistrictMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private DistrictService service;

	/**
	 * List district
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<DistrictTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getDistrictList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit district
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			District item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getDistrictById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/DistrictMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new District();
			}
			
			DistrictEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Save district fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@RequestParam(value = "act", required = false) String act,
			@ModelAttribute DistrictEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getDistrictId() != null) {
				if (service.getDistrictById(item.getDistrictId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/DistrictMaintenance/home";
				}
			}
			
			if (!service.saveDistrict(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00149", null, locale));
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/masterMaintenance/DistrictMaintenance/edit";
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/DistrictMaintenance/home";
	}
	
	/**
	 * Delete district 
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id[]") List<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteDistrict(id)) {
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