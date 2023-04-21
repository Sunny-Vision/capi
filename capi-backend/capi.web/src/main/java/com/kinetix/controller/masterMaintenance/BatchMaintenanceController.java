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

import capi.entity.Batch;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.masterMaintenance.BatchTableList;
import capi.service.masterMaintenance.BatchService;

import com.kinetix.component.FuncCode;


/**
 * UF-1114 Batch Maintenance
 */
@Secured("UF1114")
@FuncCode("UF1114")
@Controller("BatchMaintenanceController")
@RequestMapping("masterMaintenance/BatchMaintenance")
public class BatchMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(BatchMaintenanceController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private BatchService service;

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
	public @ResponseBody DatatableResponseModel<BatchTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "surveyForm", required = false) String surveyForm) {

		try {
			return service.getBatchList(requestModel, new String[]{ surveyForm });
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Batch
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model,  DatatableRequestModel requestModel, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			Batch item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getBatchById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/BatchMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new Batch();
			}
			
			BatchTableList editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Get survey form
	 */
	@RequestMapping(value = "querySurveyFormSelect", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		querySurveyFormSelect(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return service.querySureyFormSelect(requestModel);
		} catch (Exception e) {
			logger.error("querySureyFormSelect", e);
		}
		return null;
	}

	/**
	 * Save Batch fields
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@RequestParam(value = "act", required = false) String act,
			@ModelAttribute BatchTableList item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getBatchId() != null) {
				if (service.getBatchById(item.getBatchId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/BatchMaintenance/home";
				}
			}
			
			if (!service.saveBatch(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00027", null, locale));
				
				List<BatchTableList.AssignmentType> assignmentTypeList = new ArrayList<BatchTableList.AssignmentType>();
				assignmentTypeList.add(new BatchTableList.AssignmentType(1, SystemConstant.ASSIGNMENT_TYPE_1));
				assignmentTypeList.add(new BatchTableList.AssignmentType(2, SystemConstant.ASSIGNMENT_TYPE_2));
				assignmentTypeList.add(new BatchTableList.AssignmentType(3, SystemConstant.ASSIGNMENT_TYPE_3));
				item.setAssignmentTypeList(assignmentTypeList);
				
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/masterMaintenance/BatchMaintenance/edit";
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/masterMaintenance/BatchMaintenance/home";
	}

	/**
	 * Delete Batch
	 */
	@PreAuthorize("hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteBatch(id)) {
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