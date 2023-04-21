package com.kinetix.controller.userAccountManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import capi.entity.Role;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.commonLookup.SystemFunctionLookupTableList;
import capi.model.userAccountManagement.AuthorityList;
import capi.model.userAccountManagement.RoleEditModel;
import capi.model.userAccountManagement.RoleTableList;
import capi.service.lookup.SystemFunctionLookupService;
import capi.service.userAccountManagement.RoleService;

import com.kinetix.component.FuncCode;


/**
 * UF-1304 Role Maintenance
 */
@Secured("UF1304")
@FuncCode("UF1304")
@Controller("RoleMaintenanceController")
@RequestMapping("userAccountManagement/RoleMaintenance")
public class RoleMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(RoleMaintenanceController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private RoleService service;

	@Autowired
	private SystemFunctionLookupService systemFunctionLookupService;

	String[] authorityLevelNameList = {
			"Section Head",
			"Field Team Head",
			"Field Supervisor",
			"Field Allocation Coordinator",
			"Field Officers",
			"Indoor Allocation Coordinator",
			"Indoor Review",
			"Indoor Data Conversion",
			"Business Data Administrator",
			"System Administrator",
			"Indoor Supervisor",
			"Business Data Viewer"
	};
	
	private Map<Integer, String> authorityLevelMap = new HashMap<Integer, String> ();

	/**
	 * List Role
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		for(int i = 1, j = 0; i <= 2048; i *= 2, j++) {
			authorityLevelMap.put(i, authorityLevelNameList[j]);
		}
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<RoleTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getRoleList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Role
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model,  DatatableRequestModel requestModel, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			Role item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getRoleById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/RoleMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new Role();
			}
			
			RoleEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Get Back-end System Function detail
	 */
	@RequestMapping(value = "getBackendSystemFunctionDetail")
	public @ResponseBody List<SystemFunctionLookupTableList> getBackendSystemFunctionDetail(
			@RequestParam(value = "isMobile", required = false) Boolean isMobile,
			@RequestParam(value = "ids[]", required = false) Integer[] ids) {
		try {
			if (ids == null || ids.length == 0) return null;
			return systemFunctionLookupService.getTableListByIds(isMobile, ids);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * DataTable query Back-end Function List
	 */
	/*@RequestMapping(value = "queryBackendFunctionList", method = RequestMethod.GET)
	public @ResponseBody DatatableResponseModel<RoleTableList>
		queryBackendFunctionList(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getBackendFunctionList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}*/

	/**
	 * Get Front-end System Function detail
	 */
	@RequestMapping(value = "getFrontendSystemFunctionDetail")
	public @ResponseBody List<SystemFunctionLookupTableList> getFrontendSystemFunctionDetail(
			@RequestParam(value = "isMobile", required = false) Boolean isMobile,
			@RequestParam(value = "ids[]", required = false) Integer[] ids) {
		try {
			if (ids == null || ids.length == 0) return null;
			return systemFunctionLookupService.getTableListByIds(isMobile, ids);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * DataTable query Front-end Function List
	 */
	/*@RequestMapping(value = "queryFrontendFunctionList", method = RequestMethod.GET)
	public @ResponseBody DatatableResponseModel<RoleTableList>
		queryFrontendFunctionList(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getFrontendFunctionList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}*/

	/**
	 * Get Authority Level detail
	 */
	@RequestMapping(value = "getAuthorityLevelDetail")
	public @ResponseBody List<AuthorityList> getAuthorityLevelDetail(
			@RequestParam(value = "ids[]", required = false) Integer[] ids) {
		try {
			if (ids == null || ids.length == 0) return null;
			List<AuthorityList> authorityLevelList = new ArrayList<AuthorityList> ();
			for(int i = 0; i < ids.length; i++) {
				if(ids[i] != null) {
					AuthorityList authority = new AuthorityList();
					authority.setId(ids[i]);
					authority.setAuthorityLevel(authorityLevelMap.get(ids[i]));
					authorityLevelList.add(authority);
				}
			}
			return authorityLevelList;
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Save Role
	 */
	@PreAuthorize("hasPermission(#user, 512)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute RoleEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getRoleId() != null) {
				if (service.getRoleById(item.getRoleId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/RoleMaintenance/home";
				}
			}
			
			/*if (!service.saveRole(item)){
				model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00052", null, locale));
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/userAccountManagement/RoleMaintenance/edit";
			}*/
			if ( !"Success".equals(service.saveRole(item)) ){
				if("Duplicated".equals(service.saveRole(item)))
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00146", null, locale));
				else if("Authority".equals(service.saveRole(item)))
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00147", null, locale));
				else if("Function".equals(service.saveRole(item)))
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00148", null, locale));
				else
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00052", null, locale));
				model.addAttribute("model", item);
				model.addAttribute("act", "add");
				return "/userAccountManagement/RoleMaintenance/edit";
			}			
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/userAccountManagement/RoleMaintenance/home";
	}

	/**
	 * Delete Role
	 */
	@PreAuthorize("hasPermission(#user, 512)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteRole(id)) {
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