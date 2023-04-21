package com.kinetix.controller.userAccountManagement;

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

import capi.entity.User;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.DistrictEditModel;
import capi.model.userAccountManagement.FieldExperienceTableList;
import capi.service.userAccountManagement.FieldExperienceService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetix.component.FuncCode;


/**
 * UF-1305 Field Experience Maintenance
 */
@Secured("UF1305")
@FuncCode("UF1305")
@Controller("FieldExperienceMaintenanceController")
@RequestMapping("userAccountManagement/FieldExperienceMaintenance")
public class FieldExperienceMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(FieldExperienceMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private FieldExperienceService service;

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
	public @ResponseBody DatatableResponseModel<FieldExperienceTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getFieldExperienceList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit field experience
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			User item = null;
			item = service.getUserById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/userAccountManagement/FieldExperienceMaintenance/home";
			}

			List<DistrictEditModel> districts = service.getAllDistrict();
			
			model.addAttribute("districts", districts);

			FieldExperienceTableList editModel = service.convertEntityToModel(item);
			
			ObjectMapper mapper = new ObjectMapper();
			
			model.addAttribute("reasons", mapper.writeValueAsString(editModel.getReasons()));
			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Save field experience
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute FieldExperienceTableList item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getUserId() != null) {
				if (service.getUserById(item.getUserId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/FieldExperienceMaintenance/home";
				}
			}
			
			service.saveFieldExperience(item);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/userAccountManagement/FieldExperienceMaintenance/home";
	}

}