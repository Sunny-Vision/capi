package com.kinetix.controller.userAccountManagement;

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

import capi.entity.Acting;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.userAccountManagement.ActingTableList;
import capi.service.userAccountManagement.ActingService;
import capi.service.userAccountManagement.RoleService;

import com.kinetix.component.FuncCode;


/**
 * UF-1302 Acting Function
 */
@Secured("UF1302")
@FuncCode("UF1302")
@Controller("ActingFunctionController")
@RequestMapping("userAccountManagement/ActingFunction")
public class ActingFunctionController {

	private static final Logger logger = LoggerFactory.getLogger(ActingFunctionController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private ActingService service;
	
	@Autowired
	private RoleService roleService;

	/**
	 * List Acting
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<ActingTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getActingList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Acting
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model,  DatatableRequestModel requestModel, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			Acting item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getActingById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/ActingFunction/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new Acting();
			}
			
			ActingTableList editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Get role select format
	 */
	@RequestMapping(value = "queryRoleSelect", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryRoleSelect(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return roleService.queryRoleSelect(requestModel);
		} catch (Exception e) {
			logger.error("queryRoleSelect2", e);
		}
		return null;
	}

	/**
	 * Save Acting fields
	 */
	@PreAuthorize("hasPermission(#user, 512)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute ActingTableList item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getActingId() != null) {
				if (service.getActingById(item.getActingId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/ActingFunction/home";
				}
			}
			
			if (!service.saveActing(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00113", null, locale));
				model.addAttribute("model", item);
				return "/userAccountManagement/ActingFunction/edit";
			}

			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/userAccountManagement/ActingFunction/home";
	}

	/**
	 * Delete TPU
	 */
	@PreAuthorize("hasPermission(#user, 512)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteActing(id)) {
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