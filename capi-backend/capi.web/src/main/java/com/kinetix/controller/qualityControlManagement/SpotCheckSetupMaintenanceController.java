package com.kinetix.controller.qualityControlManagement;

import java.util.ArrayList;
import java.util.Date;
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

import capi.entity.SpotCheckSetup;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.SpotCheckSetupTableList;
import capi.service.CommonService;
import capi.service.qualityControlManagement.SpotCheckSetupService;

import com.kinetix.component.FuncCode;


/**
 * RF-2005 Spot Check Setup Maintenance
 */
@Secured("RF2005")
@FuncCode("RF2005")
@Controller("SpotCheckSetupMaintenanceController")
@RequestMapping("qualityControlManagement/SpotCheckSetupMaintenance")
public class SpotCheckSetupMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(SpotCheckSetupMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private SpotCheckSetupService service;
	
	@Autowired
	private CommonService commonService;

	/**
	 * List field experience
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<SpotCheckSetupTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getSpotCheckSetupList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Spot Check Setup
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, 
			@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			SpotCheckSetup item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getSpotCheckSetupById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SpotCheckSetupMaintenance/home";
				}
				Date notificationDate = commonService.getDateWithoutTime(item.getNotificationDate());
				Date today = commonService.getDateWithoutTime(new Date());
				model.addAttribute("editable", notificationDate.after(today));
				
			} else {
				model.addAttribute("act", "add");
				model.addAttribute("editable", true);
				item = new SpotCheckSetup();
				item.setNotificationType(1);
			}
			
			SpotCheckSetupTableList editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Get spot check date select format
	 */
	@RequestMapping(value = "querySpotCheckDateSelect", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		querySpotCheckDateSelect(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return service.querySpotCheckDateSelect(requestModel);
		} catch (Exception e) {
			logger.error("querySpotCheckDateSelect2", e);
		}
		return null;
	}

	/**
	 * Save Spot Check Setup
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute SpotCheckSetupTableList item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getSpotCheckSetupId() != null) {
				if (service.getSpotCheckSetupById(item.getSpotCheckSetupId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SpotCheckSetupMaintenance/home";
				}
			}
			
			service.saveSpotCheckSetup(item);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/qualityControlManagement/SpotCheckSetupMaintenance/home";
	}

	/**
	 * Delete Spot Check Setup
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteSpotCheckSetup(id)) {
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

	/**
	 * Get supervisor and update
	 */
	@RequestMapping(value = "fieldOfficerChosen")
	public @ResponseBody String
	fieldOfficerChosen(Locale locale, Model model, Integer userId) {
		try {
			String supervisor = service.getSupervisorFromUserId(userId);
			return supervisor;
		} catch (Exception e) {
			logger.error("fieldOfficerChosen", e);
		}
		return null;
	}

}