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

import capi.entity.Purpose;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.SurveyTypeTableList;
import capi.service.masterMaintenance.PurposeService;

import com.kinetix.component.FuncCode;


/**
 * UF-1212 Survey Type Maintenance
 */
@Secured("UF1212")
@FuncCode("UF1212")
@Controller("SurveyTypeMaintenanceController")
@RequestMapping("masterMaintenance/SurveyTypeMaintenance")
public class SurveyTypeMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(SurveyTypeMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private PurposeService service;

	/**
	 * List Survey Type
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<SurveyTypeTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getSurveyTypeList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit survey type
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			Purpose item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getPurposeById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/SurveyTypeMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new Purpose();
			}
			
			SurveyTypeTableList editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save survey type fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@RequestParam(value = "act", required = false) String act,
			@ModelAttribute SurveyTypeTableList item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getPurposeId() != null) {
				if (service.getPurposeById(item.getPurposeId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/SurveyTypeMaintenance/home";
				}
			}
			
			if (!service.saveSurveyType(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00069", null, locale));
				
				List<String> surveyList = new ArrayList<String>();
				surveyList.add(new String(SystemConstant.SURVEY_1));
				surveyList.add(new String(SystemConstant.SURVEY_2));
				surveyList.add(new String(SystemConstant.SURVEY_3));
				surveyList.add(new String(SystemConstant.SURVEY_4));
				
				item.setSurveyList(surveyList);
				
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/masterMaintenance/SurveyTypeMaintenance/edit";
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/SurveyTypeMaintenance/home";
	}

	/**
	 * Delete survey type 
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteSurveyType(id)) {
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