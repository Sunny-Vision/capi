package com.kinetix.controller.masterMaintenance;

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

import capi.entity.ClosingDate;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.ClosingDateEditModel;
import capi.model.masterMaintenance.ClosingDateTableList;
import capi.service.CommonService;
import capi.service.masterMaintenance.ClosingDateService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1113")
@FuncCode("UF1113")
@Controller("ClosingDateMaintenanceController")
@RequestMapping("masterMaintenance/ClosingDateMaintenance")
public class ClosingDateMaintenanceController {

	private static final Logger logger = LoggerFactory
			.getLogger(ClosingDateMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private ClosingDateService service;
	
	@Autowired
	private CommonService commonService;

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
	public @ResponseBody DatatableResponseModel<ClosingDateTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.queryClosingDate(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit Closing Date
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act,
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			ClosingDate item = null;
			
			if ("edit".equals(act)) {
				item = service.getClosingDateById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/ClosingDateMaintenance/home";
				}
			} else {
				item = new ClosingDate();
				model.addAttribute("act", "add");
			}
			
			ClosingDateEditModel editModel = service.convertEntityToModel(item);

			model.addAttribute("model", editModel);
			
			if(item.getSurveyMonths().size() > 0) {
				model.addAttribute("connectedSurveyMonth", true);
			} else {
				model.addAttribute("connectedSurveyMonth", false);
			}
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	
	/**
	 * Save Closing Date
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@RequestParam(value = "act", required = false) String act,
			@ModelAttribute ClosingDateEditModel item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
		if (item.getId() != null) {
			if (service.getClosingDateById(item.getId()) == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/masterMaintenance/ClosingDateMaintenance/home";
			}
		}
		
		if((commonService.getDate(item.getClosingDate())).compareTo(new Date()) == -1){
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00025", null, locale));
			model.addAttribute("model", item);
			model.addAttribute("act", act);
			return "/masterMaintenance/ClosingDateMaintenance/edit";
		}

		if("add".equals(act)){
			if(service.getClosingDateByReferenceMonth(item.getReferenceMonth()) != null){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00153", null, locale));
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/masterMaintenance/ClosingDateMaintenance/edit";
			}
		}
		service.saveClosingDate(item);
		
		redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
	} catch (Exception e) {
		logger.error("home", e);
		redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
	}
		return "redirect:/masterMaintenance/ClosingDateMaintenance/home";
	}
	


	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteClosingDates(id)) {
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));				
			}
			else{
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));					
			}
			
		} catch (Exception e) {
			logger.error("delete", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00154", null, locale));
		}
		
		return "/partial/messageRibbons";
	}
}
