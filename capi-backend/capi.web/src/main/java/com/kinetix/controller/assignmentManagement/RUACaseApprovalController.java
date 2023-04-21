package com.kinetix.controller.assignmentManagement;

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

import com.kinetix.batch.ApprovedQuotationRecordFollowupJob;
import com.kinetix.component.FuncCode;

import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.assignmentManagement.AssignmentUnitCategoryInfoWithVerify;
import capi.model.assignmentManagement.assignmentManagement.EditModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordPageViewModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList;
import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import capi.model.assignmentManagement.ruaCaseApproval.TableList;
import capi.service.AppConfigService;
import capi.service.UserService;
import capi.service.assignmentManagement.AssignmentApprovalService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;
import capi.service.masterMaintenance.UnitService;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * UF-1410 RUA Case Approval
 */
@Secured("UF1410")
@FuncCode("UF1410")
@Controller("RUACaseApprovalController")
@RequestMapping("assignmentManagement/RUACaseApproval")
@SessionAttributes({"sessionModel"})
public class RUACaseApprovalController {

	private static final Logger logger = LoggerFactory.getLogger(RUACaseApprovalController.class);

	@Resource(name="messageSource")
	private MessageSource messageSource;

	@Autowired
	private AssignmentApprovalService service;

	@Autowired
	private AssignmentMaintenanceService maintenanceService;

	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private OutletService outletService;

	@Autowired
	private UserService userService;

	@Autowired
	private TpuService tpuService;

	@Autowired
	private DistrictService districtService;
	
	@Autowired
	private AppConfigService configService;
	
	@Autowired
	private UnitService unitService;
	
	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;
	
	@Autowired
	private ApprovedQuotationRecordFollowupJob approvedFollowupJob;
	
	/**
	 * Init session
	 */
	@ModelAttribute("sessionModel")
	public SessionModel initSessionModel() {
		return new SessionModel();
	}
	
	/**
	 * home
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(@ModelAttribute("sessionModel") SessionModel sessionModel, SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		sessionModel = new SessionModel();
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
	 * Get outlet type select format
	 */
	@RequestMapping(value = "queryOutletTypeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryOutletTypeSelect2(Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletTypeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletTypeSelect2", e);
		}
		return null;
	}

	/**
	 * Get outlet types display
	 */
	@RequestMapping(value = "getOutletTypesDisplayByOutletId", method = RequestMethod.GET)
	public @ResponseBody List<String> getOutletTypesDisplayByOutletId(@RequestParam(value = "id") Integer id) {
		try {
			List<String> list = maintenanceService.getOutletTypesDisplayByOutletId(id);
			return list;
		} catch (Exception e) {
			logger.error("getOutletTypesDisplayByOutletId", e);
		}
		return null;
	}

	/**
	 * Get outlet select2 format
	 */
	@RequestMapping(value = "queryOutletSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryOutletSelect2(Select2RequestModel requestModel) {
		try {
			return outletService.queryOutletSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryOutletSelect2", e);
		}
		return null;
	}

	/**
	 * Get district select2 format
	 */
	@RequestMapping(value = "queryDistrictSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryDistrictSelect2(Select2RequestModel requestModel) {
		try {
			return districtService.queryDistrictSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get tpu select2 format
	 */
	@RequestMapping(value = "queryTpuSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryTpuSelect2(Select2RequestModel requestModel) {
		try {
			return tpuService.queryTpuSelect2(requestModel, null);
		} catch (Exception e) {
			logger.error("queryDistrictSelect2", e);
		}
		return null;
	}

	/**
	 * Get normal consignment select format
	 */
	@RequestMapping(value = "queryNormalConsignmentSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryNormalConsignmentSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId) {
		try {
			return assignmentMaintenanceService.queryRUACaseApprovalConsignmentSelect2(requestModel, assignmentId);
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
			return assignmentMaintenanceService.queryRUACaseApprovalDistinctUnitCategorySelect2(requestModel, assignmentId);
		} catch (Exception e) {
			logger.error("queryNormalDistinctUnitCategorySelect2", e);
		}
		return null;
	}

	/**
	 * Get status by assignment
	 */
	@RequestMapping(value = "getStatusByAssignment", method = RequestMethod.GET)
	public @ResponseBody int getStatusByAssignment(@RequestParam(value = "assignmentId") int assignmentId) {
		try {
			return maintenanceService.getStatusByAssignment(assignmentId);
		} catch (Exception e) {
			logger.error("getStatusByAssignment", e);
		}
		return 0;
	}
	
	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<TableList>
		query(DatatableRequestModel requestModel,
				@RequestParam(value = "outletTypeId", required = false) String outletTypeId,
				@RequestParam(value = "personInChargeId", required = false) Integer personInChargeId,
				
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				
				Authentication auth) {
		try {
			return service.getRUACaseApprovalTableList(requestModel,
					outletTypeId, personInChargeId,
					districtId, tpuId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(@RequestParam(value = "assignmentId") Integer assignmentId,
			Model model,
			@ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			EditModel viewModel = assignmentMaintenanceService.prepareViewModel(assignmentId, null);
			viewModel.setPreSelectTab(sessionModel.getTab());
			viewModel.setPreSelectUnitCategory(sessionModel.getUnitCategory());
			viewModel.setPreSelectDateSelectedAssignmentId(sessionModel.getDateSelectedAssignmentId());
			viewModel.setPreSelectDateSelected(sessionModel.getDateSelected());
			viewModel.setPreSelectConsignmentCounter(sessionModel.getConsignmentCounter());
			viewModel.setPreSelectVerificationType(sessionModel.getVerificationType());
			model.addAttribute("model", viewModel);

			sessionModel.setAssignmentId(assignmentId);
		} catch (Exception e) {
			logger.error("edit", e);
		}
	}
	
	/**
	 * DataTable query normal quotation record
	 */
	@RequestMapping(value = "queryNormalQuotationRecord", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<QuotationRecordTableList>
		queryNormalQuotationRecord(DatatableRequestModel requestModel,
				@RequestParam(value = "assignmentId") Integer assignmentId,
				@RequestParam(value = "consignmentCounter", required = false) String consignmentCounter,
				@RequestParam(value = "unitCategory", required = false) String unitCategory) {
		try {
			return assignmentMaintenanceService.getRUACaseApprovalQuotationRecordTableList(requestModel, assignmentId, consignmentCounter, unitCategory);
		} catch (Exception e) {
			logger.error("queryNormalQuotationRecord", e);
		}
		return null;
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialog", method = RequestMethod.GET)
	public void assignmentUnitCategoryInfoDialog() {
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog normal Content
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialogNormalContent", method = RequestMethod.POST)
	public String assignmentUnitCategoryInfoDialogNormalContent(Model model, @ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			List<AssignmentUnitCategoryInfoWithVerify> list = assignmentMaintenanceService.getUnitCategoryForRUACaseApproval(sessionModel.getAssignmentId(), sessionModel.getConsignmentCounter(), sessionModel.getUnitCategory());
			model.addAttribute("model", list);
			model.addAttribute("readonly", true);
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogNormalContent", e);
		}
		return "/assignmentManagement/RUACaseApproval/assignmentUnitCategoryInfoDialogContent";
	}

	/**
	 * Cache Quotation Record Search Filter And Result
	 */
	@RequestMapping(value = "cacheQuotationRecordSearchFilterAndResult", method = RequestMethod.POST)
	public @ResponseBody boolean cacheQuotationRecordSearchFilterAndResult(@ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			List<Integer> ids = null;
			int assignmentId = sessionModel.getAssignmentId();
			String unitCategory = sessionModel.getUnitCategory();
			String consignmentCounter = sessionModel.getConsignmentCounter();
			DatatableRequestModel dataTableRequestModel = new DatatableRequestModel();
			dataTableRequestModel.setOrder(sessionModel.getOrder());
			
			if (sessionModel.getTab().equals("Normal")) {
				ids = assignmentMaintenanceService.getRUACaseApprovalQuotationRecordTableListAllIds(assignmentId, consignmentCounter, unitCategory, dataTableRequestModel);
			}
			sessionModel.setIds(ids);
		} catch (Exception e) {
			logger.error("cacheQuotationRecordSearchFilterAndResult", e);
		}
		return false;
	}
	
	/**
	 * Edit quotation record
	 */
	@RequestMapping(value = "editQuotationRecord", method = RequestMethod.GET)
	public void editQuotationRecord(Model model, @RequestParam(value = "quotationRecordId") Integer quotationRecordId,
			@ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			QuotationRecordPageViewModel viewModel = service.prepareQuotationRecordPageViewModel(quotationRecordId, sessionModel, false, false);
			viewModel.setHideOutlet(true);
			viewModel.getProduct().setShowSpecDialog(true);
			viewModel.setReadonly(true);
			model.addAttribute("model", viewModel);
		} catch (Exception e) {
			logger.error("editQuotationRecord", e);
		}
	}
	
	/**
	 * Approve/Reject quotation record
	 */
	@PreAuthorize("hasPermission(#user, 4)")
	@RequestMapping(value = "approveRejectQuotationRecord", method = RequestMethod.POST)
	public String approveRejectQuotationRecord(@RequestParam(value = "approveRejectBtn") String approveRejectBtn,
			@RequestParam(value = "quotationRecordId") int quotationRecordId,
			@RequestParam(value = "rejectReason", required=false) String rejectReason,
			@ModelAttribute("sessionModel") SessionModel sessionModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (quotationRecordService.getQuotationRecordById(quotationRecordId) == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/assignmentManagement/RUACaseApproval/home";
			}
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(quotationRecordId);
			
			if("approve".equals(approveRejectBtn) || "approveAndNext".equals(approveRejectBtn)) {
				if(!service.approveQuotationRecordIds(ids)){
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
					return "redirect:/assignmentManagement/RUACaseApproval/editQuotationRecord?quotationRecordId=" + quotationRecordId;
				} else {
					service.updateSessionAfterSubmit(quotationRecordId, sessionModel);
					approvedFollowupJob.followupRUA(ids);
					
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
					if ("approveAndNext".equals(approveRejectBtn))
						return "redirect:/assignmentManagement/RUACaseApproval/editQuotationRecord?quotationRecordId=" + (sessionModel.getNextId() != null ? sessionModel.getNextId() : sessionModel.getPreviousId());
				}
			} else {
				if(!service.rejectQuotationRecords(ids, rejectReason)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00059", null, locale));
					return "redirect:/assignmentManagement/RUACaseApproval/editQuotationRecord?quotationRecordId=" + quotationRecordId;
				} else {
					service.updateSessionAfterSubmit(quotationRecordId, sessionModel);
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
					if ("rejectAndNext".equals(approveRejectBtn))
						return "redirect:/assignmentManagement/RUACaseApproval/editQuotationRecord?quotationRecordId=" + (sessionModel.getNextId() != null ? sessionModel.getNextId() : sessionModel.getPreviousId());
				}
			}
		} catch (Exception e) {
			logger.error("approveRejectQuotationRecord", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/RUACaseApproval/edit?assignmentId=" + sessionModel.getAssignmentId();
	}
	
	/**
	 * Approve/Reject Assignment Reallocation
	 */
	@PreAuthorize("hasPermission(#user, 4)")
	@RequestMapping(value = "approveRejectQuotationRecords", method = RequestMethod.POST)
	public String approveRejectQuotationRecords(@RequestParam(value = "approveRejectBtn") String approveRejectBtn,
			@RequestParam(value = "id[]") Integer[] id,
			@RequestParam(value = "rejectReason", required=false) String rejectReason,
			@ModelAttribute("sessionModel") SessionModel sessionModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			@SuppressWarnings("unchecked")
			List<Integer> ids = Arrays.asList(id);
			
			if("approve".equals(approveRejectBtn)) {
				if(!service.approveQuotationRecordIds(ids)){
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
					approvedFollowupJob.followupRUA(ids);
				}
			} else {
				if(!service.rejectQuotationRecords(ids, rejectReason)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00059", null, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
				}
			}
		} catch (Exception e) {
			logger.error("approveRejectQuotationRecords", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/RUACaseApproval/edit?assignmentId=" + sessionModel.getAssignmentId();
	}

	/**
	 * Approve/Reject Assignment
	 */
	@PreAuthorize("hasPermission(#user, 4)")
	@RequestMapping(value = "approveRejectAssignments", method = RequestMethod.POST)
	public String approveRejectAssignments(@RequestParam(value = "approveRejectBtn") String approveRejectBtn,
			@RequestParam(value = "id[]") Integer[] id,
			@RequestParam(value = "rejectReason", required=false) String rejectReason,
			@ModelAttribute("assignmentApprovalSessionModel") SessionModel sessionModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			@SuppressWarnings("unchecked")
			List<Integer> ids = Arrays.asList(id);
			List<Integer> quotationRecordIds = quotationRecordService.getAllIdsSubmittedAndRUAByAssignments(ids);
			
			if("approve".equals(approveRejectBtn)) {
				if(!service.approveAssignments(ids)){
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
					approvedFollowupJob.followupRUA(quotationRecordIds);
				}
			} else {
				if(!service.rejectAssignments(ids, rejectReason)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00059", null, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
				}
			}
		} catch (Exception e) {
			logger.error("approveRejectAssignments", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/RUACaseApproval/home";
	}
}
