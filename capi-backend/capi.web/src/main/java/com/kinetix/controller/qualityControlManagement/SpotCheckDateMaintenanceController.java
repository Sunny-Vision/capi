package com.kinetix.controller.qualityControlManagement;

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

import capi.entity.SurveyMonth;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.SpotCheckDateTableList;
import capi.service.qualityControlManagement.SpotCheckDateService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetix.component.FuncCode;


/**
 * RF-2004 Spot Check Date Maintenance
 */
@Secured("RF2004")
@FuncCode("RF2004")
@Controller("SpotCheckDateMaintenanceController")
@RequestMapping("qualityControlManagement/SpotCheckDateMaintenance")
public class SpotCheckDateMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(SpotCheckDateMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private SpotCheckDateService service;

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
	public @ResponseBody DatatableResponseModel<SpotCheckDateTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getSpotCheckDateList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Spot Check Date
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			SurveyMonth item = null;
			item = service.getSurveyMonthById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SpotCheckDateMaintenance/home";
			}

			SpotCheckDateTableList editModel = service.convertEntityToModel(item);

			model.addAttribute("model", editModel);

			List<String> selectedDateList = new ArrayList<String>();
			for(String selectedDate : editModel.getSpotCheckDatesList()) {
				selectedDateList.add(selectedDate);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(selectedDateList);
			
			model.addAttribute("dateList", json);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save Spot Check Date
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute SpotCheckDateTableList item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getSpotCheckDateId() != null) {
				if (service.getSpotCheckDateById(item.getSpotCheckDateId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SpotCheckDateMaintenance/home";
				}
			}
			
			service.saveSpotCheckDate(item);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/qualityControlManagement/SpotCheckDateMaintenance/home";
	}

}