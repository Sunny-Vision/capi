package com.kinetix.controller.userAccountManagement;

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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import capi.entity.Role;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.KeyValueModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.userAccountManagement.StaffProfileEditModel;
import capi.model.userAccountManagement.StaffProfileTableList;
import capi.model.userAccountManagement.UserRoleTableList;
import capi.service.masterMaintenance.BatchService;
import capi.service.userAccountManagement.PasswordPolicyService;
import capi.service.userAccountManagement.RankService;
import capi.service.userAccountManagement.StaffProfileService;

import com.kinetix.component.FuncCode;


/**
 * UF-1301 Staff Profile Maintenance
 */
@Secured("UF1301")
@FuncCode("UF1301")
@Controller("StaffProfileMaintenanceController")
@RequestMapping("userAccountManagement/StaffProfileMaintenance")
public class StaffProfileMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(StaffProfileMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private StaffProfileService service;
	
	@Autowired
	private RankService rankService;

	@Autowired
	private BatchService batchService;
	
	@Autowired
	private PasswordPolicyService passwordPolicyService;
	
	/**
	 * List  Staff Profile
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<StaffProfileTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel) {

		try {
			return service.getStaffProfileList(requestModel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Staff Profile
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			User item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getStaffProfileById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/StaffProfileMaintenance/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new User();
				item.setStatus("Active");
				item.setStaffType(1);
				item.setGender("M");
			}
			
			//StaffProfileEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", item);

			//TODO CR6 REQ07
			model.addAttribute("pwMinLength", passwordPolicyService.getParameters().getMinLength());
			
			//List<Batch> batchCodes = service.getBatchCodes();

			//model.addAttribute("batchCodes", batchCodes);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		
		//model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00158",  new Object[]{"5"}, locale));
		return null;
	}

	/**
	 * Get rank select format
	 */
	@RequestMapping(value = "queryRankSelect", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryRankSelect(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return rankService.queryRankSelect(requestModel);
		} catch (Exception e) {
			logger.error("queryRankSelect2", e);
		}
		return null;
	}

	/**
	 * Get role
	 */
	@RequestMapping(value = "roleChosen")
	public @ResponseBody List<UserRoleTableList> roleChosen(Locale locale, Model model, DatatableRequestModel requestModel, Integer[] ids) {
		try {
			List<Integer> selectedIds = new ArrayList<Integer>();
			// Convert Integer[] into List<Integer>
			for(Integer id : ids) {
				selectedIds.add(id);
			}
			List<Role> roles =  service.getRoleList(selectedIds);
			
			List<UserRoleTableList> userRoles = new ArrayList<UserRoleTableList>();
			for(int i = 0; i < roles.size(); i++) {
				UserRoleTableList userRole = new UserRoleTableList();
				userRole.setRoleId(roles.get(i).getRoleId());
				userRole.setCode(roles.get(i).getName());
				userRole.setDescription(roles.get(i).getDescription());
				userRoles.add(userRole);
			}
					
			return userRoles;
		} catch (Exception e) {
			logger.error("fieldOfficerChosen", e);
		}
		return null;
	}

	/**
	 * Save Staff Profile
	 */
	@PreAuthorize("hasPermission(#user, 512)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@RequestParam(value = "act", required = false) String act,
			@ModelAttribute StaffProfileEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes, Authentication authentication) {
		try {
			CapiWebAuthenticationDetails capiWebAuthenticationDetails =  (CapiWebAuthenticationDetails)authentication.getDetails();
			int authorityLevel = capiWebAuthenticationDetails.getAuthorityLevel();
			
			if (item.getUserId() != null) {
				if (service.getStaffProfileById(item.getUserId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/userAccountManagement/StaffProfileMaintenance/home";
				}
			}
			String saveResult = service.saveStaffProfile(item, authorityLevel, act);

			
			if (!"Success".equals(saveResult)) {
				
				if ("PasswordMinimumAge".equals(saveResult)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00160", new Object[]{passwordPolicyService.getMinAgeInteger()}, locale));
					return "redirect:/userAccountManagement/StaffProfileMaintenance/edit?act=edit&id="+item.getUserId();
				} else if ("EnforcedPassword".equals(saveResult)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00158", new Object[]{passwordPolicyService.getParameters().getEnforcePasswordHistory()}, locale));
					return "redirect:/userAccountManagement/StaffProfileMaintenance/edit?act=edit&id="+item.getUserId();
				} else {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
					model.addAttribute("model", item);
					model.addAttribute("act", "add");
					return "/userAccountManagement/StaffProfileMaintenance/edit";
				}
				
			} 
			
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/userAccountManagement/StaffProfileMaintenance/home";
	}

	/**
	 * Delete Staff Profile
	 */
	@PreAuthorize("hasPermission(#user, 512)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> id, Model model, Locale locale) {
		try {
			if (!service.deleteStaffProfile(id)) {
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
	
	
	@RequestMapping(value = "getBatchCodes", method = RequestMethod.POST)
	public @ResponseBody List<KeyValueModel> getBatchCodes(Integer[] ids){
		try{
			return batchService.getBatchCodes(ids);
		}
		catch(Exception ex){
			logger.error("getBatchCodes", ex);
		}
		
		return null;
	}
	

	/**
	 * Get batch select format
	 */
	@RequestMapping(value = "queryBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryBatchSelect2(Select2RequestModel requestModel) {
		try {
			return batchService.queryBatchSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryBatchSelect2", e);
		}
		return null;
	}

}
