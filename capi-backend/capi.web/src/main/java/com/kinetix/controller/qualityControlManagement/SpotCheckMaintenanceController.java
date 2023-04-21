package com.kinetix.controller.qualityControlManagement;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
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

import capi.entity.SpotCheckForm;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.SpotCheckEditModel;
import capi.model.qualityControlManagement.SpotCheckTableList;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.qualityControlManagement.SpotCheckService;

import com.kinetix.component.FuncCode;


/**
 * RF-2001 Spot Check Maintenance
 */
@Secured("RF2001")
@FuncCode("RF2001")
@Controller("SpotCheckMaintenanceController")
@RequestMapping("qualityControlManagement/SpotCheckMaintenance")
public class SpotCheckMaintenanceController {
	
	private static final Logger logger = LoggerFactory.getLogger(SpotCheckMaintenanceController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private SpotCheckService service;

	@Autowired
	private AssignmentMaintenanceService assignmentService;

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
	public @ResponseBody DatatableResponseModel<SpotCheckTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				Authentication authentication) {

		try {
			CapiWebAuthenticationDetails capiWebAuthenticationDetails =  (CapiWebAuthenticationDetails)authentication.getDetails();
			int authorityLevel = capiWebAuthenticationDetails.getAuthorityLevel();
			
			boolean aboveSupervisor = false;
			if( ((authorityLevel & 1) == 1) || ((authorityLevel & 2) == 2)
					|| ((authorityLevel & 256) == 256) || ((authorityLevel & 2048) == 2048) )
				aboveSupervisor = true;
			
			List<Integer> actedUsers = capiWebAuthenticationDetails.getActedUsers();
			actedUsers.add(capiWebAuthenticationDetails.getUserId());
			
			return service.getSpotCheckList(requestModel, aboveSupervisor, actedUsers, capiWebAuthenticationDetails.getUserId(), authorityLevel);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit Spot Check
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "id", required = false) Integer id, 
			@RequestParam(value = "officerId", required = false) Integer officerId, @RequestParam(value = "officerCode", required = false) String officerCode,
			@RequestParam(value = "spotCheckDate", required = false) String spotCheckDate, Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			SpotCheckForm item = null;
			item = service.getSpotCheckById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/qualityControlManagement/SpotCheckMaintenance/home";
			}

			SpotCheckEditModel editModel = service.convertEntityToModel(item, id, officerId, officerCode, spotCheckDate);

			model.addAttribute("model", editModel);

		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Get Location By ReferenceNo
	 */
	@RequestMapping(value = "getLocationByReferenceNo", method = RequestMethod.GET)
	public @ResponseBody String getLocationByReferenceNo(
			@RequestParam(value = "referenceNo", required = false) String referenceNo) {
		try {
			if (referenceNo == null || StringUtils.isEmpty(referenceNo)) return null;
			return assignmentService.getLocationByReferenceNo(referenceNo);
		} catch (Exception e) {
			logger.error("getLocationByReferenceNo", e);
		}
		return null;
	}
	
	/**
	 * Save Spot Check
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute SpotCheckEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes,
						@RequestParam(value="submitBtn") String submitOrSave) {
		try {
			if (item.getSpotCheckFormId() != null) {
				if (service.getSpotCheckById(item.getSpotCheckFormId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/qualityControlManagement/SpotCheckMaintenance/home";
				}
			}
			
			service.saveSpotCheck(item, submitOrSave);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/qualityControlManagement/SpotCheckMaintenance/home";
	}

}