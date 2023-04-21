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
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.RecommendAssignmentModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.TableList;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.ViewModel;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.AssignmentTransferInOutMaintenanceService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;

@Secured("UF1506")
@FuncCode("UF1506")
@Controller("AssignmentTransferInOutRecommendationController")
@RequestMapping("assignmentAllocationAndReallocation/AssignmentTransferInOutRecommendation")
public class AssignmentTransferInOutRecommendationController {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentTransferInOutRecommendationController.class);

	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	AssignmentTransferInOutMaintenanceService service;
	
	@Autowired
	UserService userService;

	@Autowired
	private OutletService outletService;
	
	@Autowired
	private TpuService tpuService;
	
	@Autowired
	private DistrictService districtService;

	@Autowired
	private BatchService batchService;

	/**
	 * list
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home() {
		try {
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

			List<Integer> submitToRecommendIds = new ArrayList<Integer>();
			if (((detail.getAuthorityLevel() & 2) == 2) || ((detail.getAuthorityLevel() & 256) == 256) || ((detail.getAuthorityLevel() & 2048) == 2048)) {
				// no filter for: (2): Field Team head, (256): Business Data Administrator, (2048): Business Data Viewer
			} else if ((detail.getAuthorityLevel() & 4) == 4) {
				// (4): Field Supervisor
				submitToRecommendIds.add(detail.getUserId());
				submitToRecommendIds.addAll(detail.getActedUsers());
			}
			return service.getTableListForRecommend(requestModel, referenceMonth, fromUserId, submitToRecommendIds, new String[]{"Submitted"});
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
			Locale locale, RedirectAttributes redirectAttributes, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			ViewModel viewModel = service.prepareViewModelForRecommend(id, detail.getUserId());

			if (viewModel == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/assignmentAllocationAndReallocation/AssignmentTransferInOutRecommendation/home";
			}
			model.addAttribute("model", viewModel);

		} catch (Exception e) {
			logger.error("edit", e);
			return "redirect:/assignmentAllocationAndReallocation/AssignmentTransferInOutRecommendation/home";
		}
		return null;
	}
	
	/**
	 * Assignment lookup query
	 */
	@RequestMapping(value = "queryAssignment", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<RecommendAssignmentModel>
		queryAssignment(DatatableRequestModel requestModel,
				@RequestParam(value = "assignmentAdjustmentId", required = false) int assignmentAdjustmentId,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				@RequestParam(value = "outletTypeId[]", required = false) String[] outletTypeId,
				@RequestParam(value = "districtId", required = false) Integer districtId,
				@RequestParam(value = "batchId", required = false) Integer batchId,
				Authentication auth) {
		try {
			return service.getRecommendAssignmentTableList(requestModel, assignmentAdjustmentId,
					tpuId, outletTypeId, districtId, batchId, true);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get tpu select2 format
	 */
	@RequestMapping(value = "queryTpuSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTpuSelect2(Locale locale, Model model, Select2RequestModel requestModel,
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId) {
		try {
			return tpuService.queryTpuSelect2(requestModel, districtId);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get outlet type select2 format
	 */
	@RequestMapping(value = "queryOutletTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOutletTypeSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get district select2 format
	 */
	@RequestMapping(value = "queryDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDistrictSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get batch select2 format
	 */
	@RequestMapping(value = "queryBatchSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryBatchSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return batchService.queryBatchSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryBatchSelect2", e);
		}
		return null;
	}

	/**
	 * Save
	 */
	@PreAuthorize("hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "submit", method = RequestMethod.POST)
	public String submit(
			@ModelAttribute PostModel postModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			service.recommend(postModel);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (ServiceException e) {
			logger.error("submit", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
		} catch (Exception e) {
			logger.error("submit", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentAllocationAndReallocation/AssignmentTransferInOutRecommendation/home";
	}
}
