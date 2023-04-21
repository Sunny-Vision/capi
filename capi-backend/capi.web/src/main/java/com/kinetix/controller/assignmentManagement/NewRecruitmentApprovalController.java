package com.kinetix.controller.assignmentManagement;

import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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

import capi.entity.QuotationRecord;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.ServiceException;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.assignmentManagement.AssignmentUnitCategoryInfoWithVerify;
import capi.model.assignmentManagement.assignmentManagement.EditModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordPageViewModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList;
import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import capi.model.assignmentManagement.newRecruitmentApproval.TableList;
import capi.service.assignmentManagement.AssignmentApprovalService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.DataConversionService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.OutletService;
import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * UF-2602 New Recruitment Approval
 */
@Secured("UF2602")
@FuncCode("UF2602")
@Controller("NewRecruitmentApprovalController")
@RequestMapping("assignmentManagement/NewRecruitmentApproval")
@SessionAttributes({"sessionModel"})
public class NewRecruitmentApprovalController {

	private static final Logger logger = LoggerFactory.getLogger(NewRecruitmentApprovalController.class);

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
	private AssignmentMaintenanceService assignmentMaintenanceService;
	
	@Autowired
	private DataConversionService dataConversionService;
	
	@Autowired
	private BatchService batchService;
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
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
	 * Get batch code select format
	 */
	@RequestMapping(value = "queryBatchCodeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryBatchCodeSelect2(Select2RequestModel requestModel, @RequestParam(value = "outletId") int outletId) {
		try {
			return outletService.queryBatchCodeSelect2(requestModel, outletId);
		} catch (Exception e) {
			logger.error("queryBatchCodeSelect2", e);
		}
		return null;
	}
	
	/**
	 * Validate Batch in outlet
	 */
	@RequestMapping(value = "validateBatch", method = RequestMethod.POST)
	public @ResponseBody boolean validateBatch(@RequestParam(value = "batchIds", required = true) List<Integer> batchIds, 
			@RequestParam(value = "outletId", required = true) Integer outletId,
			HttpServletResponse response) throws ParseException {
					
		return batchService.validateBatch(outletId, batchIds);
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
				Authentication auth) {
		try {
			return service.getNewRecruitmentApprovalTableList(requestModel,
					outletTypeId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(@RequestParam(value = "assignmentId") Integer assignmentId, @RequestParam(value = "personInChargeId") Integer personInChargeId,
			Model model,
			@ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			EditModel viewModel = service.prepareViewModelForNewRecruitment(assignmentId);
			viewModel.setPreSelectTab(sessionModel.getTab());
			viewModel.setPreSelectUnitCategory(sessionModel.getUnitCategory());
			viewModel.setPreSelectDateSelectedAssignmentId(sessionModel.getDateSelectedAssignmentId());
			viewModel.setPreSelectDateSelected(sessionModel.getDateSelected());
			viewModel.setPreSelectConsignmentCounter(sessionModel.getConsignmentCounter());
			viewModel.setPreSelectVerificationType(sessionModel.getVerificationType());
			model.addAttribute("model", viewModel);

			sessionModel.setAssignmentId(assignmentId);
			if (personInChargeId != null) {
				sessionModel.setPersonInChargeId(personInChargeId);
			}
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
				@RequestParam(value = "unitCategory", required = false) String unitCategory,
				@ModelAttribute("sessionModel") SessionModel sessionModel) {
		try {
			return assignmentMaintenanceService.getNewRecruitmentApprovalQuotationRecordTableList(requestModel, assignmentId, consignmentCounter, unitCategory, sessionModel.getPersonInChargeId());
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
			List<AssignmentUnitCategoryInfoWithVerify> list = assignmentMaintenanceService.getUnitCategoryForNewRecruitmentApproval(sessionModel.getAssignmentId(), sessionModel.getConsignmentCounter(), sessionModel.getUnitCategory());
			model.addAttribute("model", list);
			model.addAttribute("readonly", true);
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogNormalContent", e);
		}
		return "/assignmentManagement/NewRecruitmentApproval/assignmentUnitCategoryInfoDialogContent";
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
			
			if (sessionModel.getTab().equals("Normal")) {
				ids = assignmentMaintenanceService.getNewRecruitmentApprovalQuotationRecordTableListAllIds(assignmentId, consignmentCounter, unitCategory);
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
			// 2020-06-23: filter new recruitment list by field officer (PIR-231)
			model.addAttribute("sessionModel_personInChargeId", sessionModel.getPersonInChargeId());
		} catch (Exception e) {
			logger.error("editQuotationRecord", e);
		}
	}
	
	/**
	 * Approve/Reject quotation record
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveRejectQuotationRecord", method = RequestMethod.POST)
	public String approveRejectQuotationRecord(@RequestParam(value = "approveRejectBtn") String approveRejectBtn,
			@RequestParam(value = "quotationRecordId") int quotationRecordId,
			@RequestParam(value = "rejectReason", required=false) String rejectReason,
			@ModelAttribute("sessionModel") SessionModel sessionModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (quotationRecordService.getQuotationRecordById(quotationRecordId) == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/assignmentManagement/NewRecruitmentApproval/home";
			}
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(quotationRecordId);
			
			if("approve".equals(approveRejectBtn) || "approveAndNext".equals(approveRejectBtn)) {
				beforeJob(ids);
				try{
					List<QuotationRecord> qrsCannotApprove = service.filterCannotApproveQuotationRecordIds(ids);
					if (qrsCannotApprove.isEmpty() == false) {
						failJob(ids);
						// include QR names and IDs on approve fail message
						String qrNamesCannotApproveStr = qrsCannotApprove.stream().map(qr -> qr.getQuotation().getUnit().getDisplayName())
								.collect(Collectors.joining(", "));
						String qrIdsCannotApproveStr = qrsCannotApprove.stream().map(qr -> qr.getId().toString())
								.collect(Collectors.joining(", "));
						
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, 
								messageSource.getMessage("E00171", new String[] { qrNamesCannotApproveStr, qrIdsCannotApproveStr }, locale));
						return "redirect:/assignmentManagement/NewRecruitmentApproval/editQuotationRecord?quotationRecordId=" + quotationRecordId;
					}
					
					if(!service.approveQuotationRecordIds(ids)){
						failJob(ids);
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
						return "redirect:/assignmentManagement/NewRecruitmentApproval/editQuotationRecord?quotationRecordId=" + quotationRecordId;
					} else {
						service.updateSessionAfterSubmit(quotationRecordId, sessionModel);
						followup(ids);
						
						redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
						if ("approveAndNext".equals(approveRejectBtn))
							return "redirect:/assignmentManagement/NewRecruitmentApproval/editQuotationRecord?quotationRecordId=" + (sessionModel.getNextId() != null ? sessionModel.getNextId() : sessionModel.getPreviousId());
					}
				} catch (Exception e){
					failJob(ids);
					throw new RuntimeException(e);
				}
			} else {
				if(!service.rejectQuotationRecords(ids, rejectReason)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00059", null, locale));
					return "redirect:/assignmentManagement/NewRecruitmentApproval/editQuotationRecord?quotationRecordId=" + quotationRecordId;
				} else {
					service.updateSessionAfterSubmit(quotationRecordId, sessionModel);
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
					if ("rejectAndNext".equals(approveRejectBtn))
						return "redirect:/assignmentManagement/NewRecruitmentApproval/editQuotationRecord?quotationRecordId=" + (sessionModel.getNextId() != null ? sessionModel.getNextId() : sessionModel.getPreviousId());
				}
			}
		} catch (ServiceException e) {
			logger.error("approveRejectQuotationRecord", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
			return "redirect:/assignmentManagement/NewRecruitmentApproval/edit?assignmentId=" + sessionModel.getAssignmentId() + "&personInChargeId=" + sessionModel.getPersonInChargeId();
		} catch (Exception e) {
			logger.error("approveRejectQuotationRecord", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/NewRecruitmentApproval/edit?assignmentId=" + sessionModel.getAssignmentId() + "&personInChargeId=" + sessionModel.getPersonInChargeId();
	}
	
	/**
	 * Approve/Reject Assignment Reallocation
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
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
				beforeJob(ids);
				try{
					List<QuotationRecord> qrsCannotApprove = service.filterCannotApproveQuotationRecordIds(ids);
					if (qrsCannotApprove.size() > 0) {
						failJob(ids);
						// include QR names and IDs on approve fail message
						String qrNamesCannotApproveStr = qrsCannotApprove.stream().map(qr -> qr.getQuotation().getUnit().getDisplayName())
								.collect(Collectors.joining(", "));
						String qrIdsCannotApproveStr = qrsCannotApprove.stream().map(qr -> qr.getId().toString())
								.collect(Collectors.joining(", "));
						
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
								messageSource.getMessage("E00171", new String[] { qrNamesCannotApproveStr, qrIdsCannotApproveStr }, locale));
					} else if(!service.approveQuotationRecordIds(ids)){
						failJob(ids);
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
					} else {
						redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
						followup(ids);
					}
				} catch (Exception e){
					failJob(ids);
					throw new RuntimeException(e);
				}
			} else {
				if(!service.rejectQuotationRecords(ids, rejectReason)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00059", null, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
				}
			}
		} catch (ServiceException e) {
			logger.error("approveRejectQuotationRecord", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage(e.getMessage(), null, locale));
			return "redirect:/assignmentManagement/NewRecruitmentApproval/edit?assignmentId=" + sessionModel.getAssignmentId() + "&personInChargeId=" + sessionModel.getPersonInChargeId();
		} catch (Exception e) {
			logger.error("approveRejectQuotationRecords", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/NewRecruitmentApproval/edit?assignmentId=" + sessionModel.getAssignmentId() + "&personInChargeId=" + sessionModel.getPersonInChargeId();
	}

	/**
	 * Approve/Reject Outlet
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveRejectOutlets", method = RequestMethod.POST)
	public String approveRejectOutlets(@RequestParam(value = "approveRejectBtn") String approveRejectBtn,
			@RequestParam(value = "id[]") String[] id,
			@RequestParam(value = "rejectReason", required=false) String rejectReason,
			@ModelAttribute("assignmentApprovalSessionModel") SessionModel sessionModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			@SuppressWarnings("unchecked")
			List<String> ids = Arrays.asList(id);
			// 2020-12-04: fix PIR-231 part 2 (unexpected result when approve/reject new recruitment of same outlet)
//			List<Integer> quotationRecordIds = service.approveOutlets(ids);
			List<Map.Entry<Integer, Integer>> outletUserIdPairs = ids.stream().map((outletUserIdPair) -> {
				String[] pairStrs = outletUserIdPair.split(";");
				if (pairStrs.length != 2) throw new RuntimeException("Invalid parameter format for 'id[]'");
				return new java.util.AbstractMap.SimpleImmutableEntry<Integer, Integer>(
						Integer.parseInt(pairStrs[0]), Integer.parseInt(pairStrs[1]));
			}).collect(Collectors.toList());
			
			if("approve".equals(approveRejectBtn)) {
				List<Integer> quotationRecordIds = service.getQRidsForOutletAndUserPairs(outletUserIdPairs);
				beforeJob(quotationRecordIds);
				try{
					List<QuotationRecord> qrsCannotApprove = service.filterCannotApproveQuotationRecordIds(quotationRecordIds);
					if (qrsCannotApprove.size() > 0) {
						failJob(quotationRecordIds);
						// include QR names and IDs on approve fail message
						String qrNamesCannotApproveStr = qrsCannotApprove.stream().map(qr -> qr.getQuotation().getUnit().getDisplayName())
								.collect(Collectors.joining(", "));
						String qrIdsCannotApproveStr = qrsCannotApprove.stream().map(qr -> qr.getId().toString())
								.collect(Collectors.joining(", "));
						
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
								messageSource.getMessage("E00171", new String[] { qrNamesCannotApproveStr, qrIdsCannotApproveStr }, locale));
					} else if(!service.approveQuotationRecordIds(quotationRecordIds)){
						failJob(quotationRecordIds);
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
					} else {
						followup(quotationRecordIds);
						redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
					}
				} catch (Exception e){
					failJob(quotationRecordIds);
					throw new RuntimeException(e);
				}
			} else {
				if(!service.rejectNewRecruitments(outletUserIdPairs, rejectReason)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00059", null, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
				}
			}
		} catch (Exception e) {
			logger.error("approveRejectOutlets", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/NewRecruitmentApproval/home";
	}
	
	/**
	 * Save batch
	 */
	@RequestMapping(value = "saveBatch", method = RequestMethod.POST)
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	public @ResponseBody boolean saveBatch(@RequestParam(value = "quotationRecordId") int quotationRecordId, @RequestParam(value = "batchId") int batchId) {
		try {
			service.saveBatch(quotationRecordId, batchId);
			return true;
		} catch (Exception e) {
			logger.error("saveBatch", e);
		}
		return false;
	}
	
	public void followup(List<Integer> ids){
		List<Integer> quotationRecordIds = quotationRecordService.getBackTrackIdByOriginalId(ids);
		List<QuotationRecord> entities = quotationRecordService.recursiveQuery(quotationRecordIds);			
		Collections.sort(entities, new Comparator<QuotationRecord>(){
			@Override
			public int compare(QuotationRecord o1,
					QuotationRecord o2){
				// TODO Auto-generated method stub
				return o1.getReferenceDate().compareTo(o2.getReferenceDate());
			}
		});
		for (QuotationRecord entity : entities) {
			Session session = sessionFactory.openSession();
			Transaction trans = session.beginTransaction();
			try{
				dataConversionService.convert(entity);
				service.handleVerification(entity);
				
				session.flush();
				trans.commit();
			}
			catch(Exception ex){
				trans.rollback();
				logger.error("Approved Quotation Record "+entity.getQuotationRecordId()+"Followup Job failed", ex);
			}finally{		
				session.close();
			}
		}
	}
	
	public void beforeJob(List<Integer> ids){
		quotationRecordService.updateQuotationRecordStatusForApproval(ids, "Approved");
	}
	
	public void failJob(List<Integer> ids){
		quotationRecordService.updateQuotationRecordStatusForApproval(ids, "Submitted");
	}
}
