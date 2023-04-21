package com.kinetix.controller.assignmentAllocationAndReallocation;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.PostModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.SelectedAssignmentModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.SelectedSessionModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.TableList;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.ViewModel;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.AssignmentTransferInOutMaintenanceService;
import edu.emory.mathcs.backport.java.util.Arrays;

@Secured("UF1503")
@FuncCode("UF1503")
@Controller("AssignmentTransferInOutMaintenanceController")
@RequestMapping("assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance")
@SessionAttributes({"selectedModelSession"})
public class AssignmentTransferInOutMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentTransferInOutMaintenanceController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	AssignmentTransferInOutMaintenanceService service;
	
	@Autowired
	UserService userService;

	/**
	 * Init filter session
	 */
	@ModelAttribute("selectedModelSession")
	public SelectedSessionModel initFilterSession() {
		return new SelectedSessionModel();
	}
	
	/**
	 * list
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(SessionStatus sessionStatus, @ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession) {
		try {
			sessionStatus.setComplete();
			selectedModelSession = new SelectedSessionModel();
		} catch (Exception e) {
			logger.error("home", e);
		}
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<TableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "referenceMonth", required = false) String referenceMonth,
				@RequestParam(value = "fromUserId", required = false) Integer fromUserId, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			
			List<Integer> supervisorIds = new ArrayList<Integer>();
			Integer fromUserPermissionId = null;
			if (((detail.getAuthorityLevel() & 2) == 2) || ((detail.getAuthorityLevel() & 256) == 256) || ((detail.getAuthorityLevel() & 2048) == 2048)) {
				// no filter for: (2): Field Team head, (256): Business Data Administrator, (2048): Business Data Viewer
			} else if ((detail.getAuthorityLevel() & 4) == 4) {
				// (4): Field Supervisor
				supervisorIds.add(detail.getUserId());
				supervisorIds.addAll(detail.getActedUsers());
			} else if ((detail.getAuthorityLevel() & 16) == 16) {
				// (16): Field Officers
				fromUserPermissionId = detail.getUserId();
			}
			
			return service.getTableList(requestModel, referenceMonth, fromUserId, fromUserPermissionId, supervisorIds, new String[]{"Pending", "Rejected"});
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	
	/**
	 * Get officer select2 format
	 */
	@RequestMapping(value = "queryOfficerSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOfficerSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return userService.queryOfficerSelect2(requestModel, detail.getUserId());
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}

	/**
	 * edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(Model model, @RequestParam(value = "id", required = true) Integer id,
			Locale locale, RedirectAttributes redirectAttributes,
			@ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession
			, Authentication auth) {
		try {
			selectedModelSession.setId(id);
			service.prepareSelectedSessionModel(selectedModelSession);
			List<Integer> selectedAssignmentIds = new ArrayList<Integer>();
			for (SelectedAssignmentModel a : selectedModelSession.getAssignments()) {
				selectedAssignmentIds.add(a.getId());
			}
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			ViewModel viewModel = service.prepareViewModel(id, detail.getUserId(), selectedAssignmentIds);

			if (viewModel == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/home";
			}
			model.addAttribute("model", viewModel);

			
		} catch (Exception e) {
			logger.error("edit", e);
			return "redirect:/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/home";
		}
		return null;
	}
	
	/**
	 * Edit selected table
	 */
	@RequestMapping(value = "editTable", method = RequestMethod.GET)
	public void editTable(Model model, @ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession) {
		try {
			model.addAttribute("model", selectedModelSession);
		} catch (Exception e) {
			logger.error("editTable", e);
		}
	}

	/**
	 * Assignment lookup query
	 */
	@RequestMapping(value = "queryAssignment", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<SelectedAssignmentModel>
		queryAssignment(DatatableRequestModel requestModel,
				@ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				@RequestParam(value = "outletTypeId", required = false) String outletTypeId,
				@RequestParam(value = "districtId", required = false) Integer districtId,
				@RequestParam(value = "batchId", required = false) Integer batchId) {
		try {
			List<Integer> selectedAssignmentIds = new ArrayList<Integer>();
			for (SelectedAssignmentModel a : selectedModelSession.getAssignments()) {
				selectedAssignmentIds.add(a.getId());
			}
			
			return service.getAssignmentTableList(requestModel, selectedModelSession.getId(),
					tpuId, outletTypeId, districtId, batchId, selectedAssignmentIds);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/** 
	 * Assignment lookup select all
	 */
	@RequestMapping(value = "queryAssignmentSelectAll", method = RequestMethod.POST)
	public @ResponseBody List<Integer> queryAssignmentSelectAll(String search,
			@ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession,
			@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
			@RequestParam(value = "outletTypeId", required = false) String outletTypeId,
			@RequestParam(value = "districtId", required = false) Integer districtId,
			@RequestParam(value = "batchId", required = false) Integer batchId){
		try {
			return service.getAssignmentTableSelectAll(search, selectedModelSession.getId(),
					tpuId, outletTypeId, districtId, batchId);
		} catch (Exception e) {
			logger.error("queryAssignmentSelectAll", e);
		}
		return null;
	}
	
	/**
	 * Select new assignments
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "addAssignmentsToModel", method = RequestMethod.POST)
	public @ResponseBody void addAssignmentsToModel(@ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession,
			@RequestParam(value = "newIds[]", required = false) Integer[] newIds) {
		try {
			service.addAssignmentsToModel(selectedModelSession, new ArrayList<Integer>(Arrays.asList(newIds)));
		} catch (Exception e) {
			logger.error("addAssignmentsToModel", e);
		}
	}
	
	/**
	 * Delete assignment
	 */
	@RequestMapping(value = "deleteAssignmentFromModel", method = RequestMethod.POST)
	public @ResponseBody void deleteAssignmentFromModel(@ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession,
			@RequestParam(value = "deleteId", required = false) Integer deleteId) {
		try {
			service.deleteAssignmentFromModel(selectedModelSession, deleteId);
		} catch (Exception e) {
			logger.error("deleteAssignmentFromModel", e);
		}
	} 

	/**
	 * Save
	 */
	@PreAuthorize("hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "submit", method = RequestMethod.POST)
	public String submit(
			@ModelAttribute PostModel postModel,
			@ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			service.submit(postModel, selectedModelSession);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (ServiceException e) {
			logger.error("submit", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
		} catch (Exception e) {
			logger.error("submit", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/home";
	}

	/**
	 * Calculate metric for assignment selection popup
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "metricForAssignmentSelectionPopup", method = RequestMethod.POST)
	public void metricForAssignmentSelectionPopup(Model model,
			@ModelAttribute("selectedModelSession") SelectedSessionModel selectedModelSession,
			@RequestParam(value = "newIds[]", required = false) Integer[] newIds) {
		try {
			SelectedSessionModel newCalculatedModel = service.calculateMetricForAssignmentSelectionPopup(selectedModelSession, newIds != null ? new ArrayList<Integer>(Arrays.asList(newIds)) : null);
			model.addAttribute("model", newCalculatedModel);
		} catch (Exception e) {
			logger.error("metricForAssignmentSelectionPopup", e);
		}
	}
}
