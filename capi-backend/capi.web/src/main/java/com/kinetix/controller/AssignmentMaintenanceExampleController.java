package com.kinetix.controller;

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

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.assignmentManagement.AssignmentUnitCategoryInfoWithVerify;
import capi.model.assignmentManagement.assignmentManagement.EditAssignmentUnitCategoryModel;
import capi.model.assignmentManagement.assignmentManagement.EditModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList;
import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import capi.model.shared.quotationRecord.OutletPostModel;
import capi.service.assignmentManagement.AssignmentMaintenanceService;

// http://localhost:8080/assignmentMaintenanceExample/edit?assignmentId=14&userId=4
@Secured("RF2003")
@Controller("AssignmentMaintenanceExampleController")
@RequestMapping("assignmentMaintenanceExample")
public class AssignmentMaintenanceExampleController {
	
	private static final Logger logger = LoggerFactory.getLogger(AssignmentMaintenanceExampleController.class);

	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;

	@Resource(name="messageSource")
	private MessageSource messageSource;

	/**
	 * Get normal consignment select format
	 */
	@RequestMapping(value = "queryNormalConsignmentSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryNormalConsignmentSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId) {
		try {
			return assignmentMaintenanceService.queryNewRecruitmentApprovalConsignmentSelect2(requestModel, assignmentId);
		} catch (Exception e) {
			logger.error("queryNormalConsignmentSelect2", e);
		}
		return null;
	}
	
	/**
	 * Get normal distinct unit category select format
	 */
	@RequestMapping(value = "queryNormalDistinctUnitCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryNormalDistinctUnitCategorySelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId) {
		try {
			return assignmentMaintenanceService.queryNewRecruitmentApprovalDistinctUnitCategorySelect2(requestModel, assignmentId);
		} catch (Exception e) {
			logger.error("queryNormalDistinctUnitCategorySelect2", e);
		}
		return null;
	}

	/**
	 * edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(@RequestParam(value = "assignmentId") Integer assignmentId, @RequestParam(value = "userId") Integer userId,
			Model model) {
		try {
			EditModel viewModel = assignmentMaintenanceService.prepareViewModel(assignmentId, userId);
			model.addAttribute("model", viewModel);
		} catch (Exception e) {
			logger.error("edit", e);
		}
	}
	
	/**
	 * Save outlet
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)")
	@RequestMapping(value = "saveOutlet", method = RequestMethod.POST)
	public String saveOutlet(OutletPostModel item
			, Model model
			, @RequestParam(value = "assignmentId") Integer assignmentId, @RequestParam(value = "userId") Integer userId
			, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			assignmentMaintenanceService.saveOutletExample(item, assignmentId, userId);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/assignmentMaintenanceExample/edit?assignmentId=" + assignmentId + "&userId=" + userId;
		} catch (Exception e) {
			logger.error("saveOutlet", e);
		}
		
		try {
			model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			
			EditModel viewModel = assignmentMaintenanceService.prepareViewModel(assignmentId, userId);
			
			assignmentMaintenanceService.prefillOutletViewModelWithPost(viewModel.getOutlet(), item);
			
			model.addAttribute("model", viewModel);
			
			return "/assignmentMaintenanceExample/edit";
		} catch (Exception e) {
			logger.error("saveOutlet", e);
		}
		return "/assignmentMaintenanceExample/home";
	}

	/**
	 * DataTable query normal quotation record
	 */
	@RequestMapping(value = "queryNormalQuotationRecord", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationRecordTableList>
		queryNormalQuotationRecord(DatatableRequestModel requestModel,
				@RequestParam(value = "assignmentId") Integer assignmentId,
				@RequestParam(value = "userId") Integer userId,
				@RequestParam(value = "consignmentCounter", required = false) String consignmentCounter,
				@RequestParam(value = "unitCategory", required = false) String unitCategory) {
		try {
			return assignmentMaintenanceService.getNewRecruitmentQuotationRecordTableList(requestModel, assignmentId, userId, consignmentCounter, unitCategory);
		} catch (Exception e) {
			logger.error("queryNormalQuotationRecord", e);
		}
		return null;
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialog", method = RequestMethod.GET)
	public void assignmentUnitCategoryInfoDialog(Model model,
			@RequestParam(value = "assignmentId") Integer assignmentId,
			@RequestParam(value = "userId") Integer userId) {
		model.addAttribute("assignmentId", assignmentId);
		model.addAttribute("userId", userId);
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog normal Content
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialogNormalContent", method = RequestMethod.POST)
	public String assignmentUnitCategoryInfoDialogNormalContent(Model model, @ModelAttribute SessionModel sessionModel) {
		try {
			List<AssignmentUnitCategoryInfoWithVerify> list = assignmentMaintenanceService.getUnitCategoryForNewRecruitmentNormal(sessionModel.getAssignmentId(), sessionModel.getUserId(), sessionModel.getConsignmentCounter(), sessionModel.getUnitCategory());
			model.addAttribute("model", list);
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogNormalContent", e);
		}
		return "/assignmentManagement/NewRecruitmentMaintenance/assignmentUnitCategoryInfoDialogContent";
	}
	
	/**
	 * Save AssignmentUnitCategory
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4)")
	@RequestMapping(value = "saveAssignmentUnitCategory", method = RequestMethod.POST)
	public String saveAssignmentUnitCategory(@ModelAttribute EditAssignmentUnitCategoryModel editModel
			, @ModelAttribute SessionModel sessionModel
			, RedirectAttributes redirectAttributes, Locale locale) {
		try {
			assignmentMaintenanceService.saveNewRecruitmentAssignmentUnitCategory(sessionModel, editModel.getCategories());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("saveAssignmentUnitCategory", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentMaintenanceExample/edit?assignmentId=" + sessionModel.getAssignmentId() + "&userId=" + sessionModel.getUserId();
	}
}
