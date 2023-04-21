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

import capi.entity.ActivityCode;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.service.masterMaintenance.ActivityCodeService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */
@Secured("UF1110")
@FuncCode("UF1110")
@Controller("ActivityCodeMaintenanceController")
@RequestMapping("masterMaintenance/ActivityCodeMaintenance")
public class ActivityCodeMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(ActivityCodeMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private ActivityCodeService service;

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
	public @ResponseBody DatatableResponseModel<ActivityCode>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.queryActivityCode(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit Activity Code
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act,
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			ActivityCode item = null;
			
			if ("edit".equals(act)) {
				item = service.getActivityCodeById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/ActivityCodeMaintenance/home";
				}
			} else {
				item = new ActivityCode();
				model.addAttribute("act", "add");
			}

			model.addAttribute("model", item);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	
	/**
	 * Save Activity Code
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@RequestParam(value = "act", required = false) String act,
			@ModelAttribute ActivityCode item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
			if (item.getId() != null) {
				if (service.getActivityCodeById(item.getId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/ActivityCodeMaintenance/home";
				}
			}
			
			if (!service.saveActivityCode(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00024", null, locale));
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/masterMaintenance/ActivityCodeMaintenance/edit";
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/ActivityCodeMaintenance/home";
	}
	


	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteActivityCodes(id)) {
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
