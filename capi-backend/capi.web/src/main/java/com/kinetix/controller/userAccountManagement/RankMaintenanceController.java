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

import capi.entity.Rank;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.userAccountManagement.RankEditModel;
import capi.model.userAccountManagement.RankTableList;
import capi.service.userAccountManagement.RankService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */
@Secured("UF1117")
@FuncCode("UF1117")
@Controller("RankMaintenanceController")
@RequestMapping("userAccountManagement/RankMaintenance")
public class RankMaintenanceController {	
	private static final Logger logger = LoggerFactory
			.getLogger(RankMaintenanceController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private RankService service;

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
	public @ResponseBody DatatableResponseModel<RankTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {
		try {
			return service.getRankList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Edit Rank
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act,
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			Rank item = null;
			
			if ("edit".equals(act)) {
				item = service.getRankById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/RankMaintenance/home";
				}
			} else {
				item = new Rank();
				model.addAttribute("act", "add");
			}
			
			RankEditModel editModel = service.convertEntityToModel(item);

			model.addAttribute("model", editModel);
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	
	/**
	 * Save Rank
	 */
	@PreAuthorize("hasPermission(#user, 512)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@RequestParam(value = "act", required = false) String act,
			@ModelAttribute RankEditModel item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes) {
		try {
			if (item.getId() != null) {
				if (service.getRankById(item.getId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/RankMaintenance/home";
				}
			}
			
			if (!service.saveRank(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00145", null, locale));
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/userAccountManagement/RankMaintenance/edit";
			}
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/userAccountManagement/RankMaintenance/home";
	}
	


	/**
	 * Delete
	 */
	@PreAuthorize("hasPermission(#user, 512)")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteRanks(id)) {
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
