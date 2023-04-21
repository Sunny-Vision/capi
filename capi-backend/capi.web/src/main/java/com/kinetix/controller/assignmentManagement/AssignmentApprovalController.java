package com.kinetix.controller.assignmentManagement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
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

import capi.entity.QuotationRecord;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentManagement.assignmentApproval.TableList;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordPageViewModel;
import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import capi.service.AppConfigService;
import capi.service.UserService;
import capi.service.assignmentManagement.AssignmentApprovalService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.DataConversionService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.PurposeService;
import capi.service.masterMaintenance.TpuService;
import capi.service.masterMaintenance.UnitService;
import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * UF-1406 Assignment Approval
 */
@Secured("UF1406")
@FuncCode("UF1406")
@Controller("AssignmentApprovalController")
@RequestMapping("assignmentManagement/AssignmentApproval")
@SessionAttributes({"assignmentApprovalSessionModel"})
public class AssignmentApprovalController {

	private static final Logger logger = LoggerFactory.getLogger(AssignmentApprovalController.class);

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
	private ApprovedQuotationRecordFollowupJob approvedFollowupJob;
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	@Autowired
	private AssignmentApprovalService approvalService;
	@Autowired
	private DataConversionService dataConversionService;
	@Autowired
	private PurposeService purposeService;
	
	/**
	 * Init session
	 */
	@ModelAttribute("assignmentApprovalSessionModel")
	public SessionModel initSessionModel() {
		return new SessionModel();
	}
	
	/**
	 * home
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(@ModelAttribute("assignmentApprovalSessionModel") SessionModel sessionModel, SessionStatus sessionStatus,
			Model model,
			@RequestParam(value = "keepSession", required = false) Boolean keepSession) {
		if (!(keepSession!=null && keepSession)){
			sessionStatus.setComplete();
			sessionModel = new SessionModel();
		}
		DatatableRequestModel lastRequestModel = sessionModel.getLastRequestModel();
		model.addAttribute("viewModel", service.prepareAssignmentApprovalViewModel(lastRequestModel));
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
	 * query unit category
	 */
	@RequestMapping(value = "queryUnitCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryUnitCategorySelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return unitService.queryTodayValidDistinctUnitCategorySelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryUnitCategorySelect2", e);
		}
		return null;
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query", method = RequestMethod.POST)
	public @ResponseBody DatatableResponseModel<TableList>
		query(DatatableRequestModel requestModel,
				@RequestParam(value = "outletId", required = false) Integer outletId,
				@RequestParam(value = "outletTypeId", required = false) String outletTypeId,
				@RequestParam(value = "personInChargeId", required = false) Integer personInChargeId,
				
				@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
				@RequestParam(value = "unitCategory", required = false) String unitCategory,
				@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
				
				@RequestParam(value = "isProductChange", required = false) Boolean isProductChange,
				@RequestParam(value = "isSPricePeculiar", required = false) Boolean isSPricePeculiar,
				@RequestParam(value = "availability[]", required = false) Integer[] availability,
				
				@RequestParam(value = "referenceMonth", required = false) String referenceMonth,
				@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
				Authentication auth) {
		try {
			return service.getAssignemntApprovalTableList(requestModel,
					outletId, outletTypeId, personInChargeId,
					districtId, unitCategory, tpuId,
					isProductChange, isSPricePeculiar, availability, referenceMonth, purposeId);
		} catch (Exception e) {
			logger.error("query", e);
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
	 * Get Purpose select2 format
	 */
	@RequestMapping(value = "queryPurposeSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
	queryPurposeSelect2(Select2RequestModel requestModel) {
		try {
			return purposeService.queryPurposeSelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryPurposeSelect2", e);
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
	 * Cache Quotation Record Search Filter And Result
	 */
	@RequestMapping(value = "cacheQuotationRecordSearchFilterAndResult", method = RequestMethod.POST)
	public @ResponseBody boolean cacheQuotationRecordSearchFilterAndResult(DatatableRequestModel requestModel,
			@RequestParam(value = "outletId", required = false) Integer outletId,
			@RequestParam(value = "outletTypeId", required = false) String outletTypeId,
			@RequestParam(value = "personInChargeId", required = false) Integer personInChargeId,
			
			@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
			@RequestParam(value = "unitCategory", required = false) String unitCategory,
			@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
			
			@RequestParam(value = "isProductChange", required = false) Boolean isProductChange,
			@RequestParam(value = "isSPricePeculiar", required = false) Boolean isSPricePeculiar,
			@RequestParam(value = "availability[]", required = false) Integer[] availability,
			
			@RequestParam(value = "referenceMonth", required = false) String referenceMonth,
			@RequestParam(value = "purposeId[]", required = false) Integer[] purposeId,
			@ModelAttribute("assignmentApprovalSessionModel") SessionModel sessionModel) {
		try {
			List<Integer> ids = service.getAssignemntApprovalTableListAllIds(requestModel, null,
					outletId, outletTypeId, personInChargeId,
					districtId, unitCategory, tpuId,
					isProductChange, isSPricePeculiar, availability, referenceMonth, purposeId);
			
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
			@ModelAttribute("assignmentApprovalSessionModel") SessionModel sessionModel) {
		try {
			QuotationRecordPageViewModel viewModel = service.prepareQuotationRecordPageViewModel(quotationRecordId, sessionModel, false, true);
			model.addAttribute("model", viewModel);
		} catch (Exception e) {
			logger.error("editQuotationRecord", e);
		}
	}
	
	/**
	 * Approve/Reject Assignment Reallocation
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveRejectQuotationRecord", method = RequestMethod.POST)
	public String approveRejectQuotationRecord(@RequestParam(value = "approveRejectBtn") String approveRejectBtn,
			@RequestParam(value = "quotationRecordId") int quotationRecordId,
			@RequestParam(value = "rejectReason", required=false) String rejectReason,
			@ModelAttribute("assignmentApprovalSessionModel") SessionModel sessionModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (quotationRecordService.getQuotationRecordById(quotationRecordId) == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/assignmentManagement/AssignmentApproval/home?keepSession=true";
			}
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(quotationRecordId);
			
			if("approve".equals(approveRejectBtn) || "approveAndNext".equals(approveRejectBtn)) {
				try{
					if(!approveQuotationRecordIdsAndFollowUp(ids)) {
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
						return "redirect:/assignmentManagement/AssignmentApproval/editQuotationRecord?quotationRecordId=" + quotationRecordId;
					} else {
						service.updateSessionAfterSubmit(quotationRecordId, sessionModel);
						redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
						if ("approveAndNext".equals(approveRejectBtn))
							return "redirect:/assignmentManagement/AssignmentApproval/editQuotationRecord?quotationRecordId=" + (sessionModel.getNextId() != null ? sessionModel.getNextId() : sessionModel.getPreviousId());
					}
				} catch (Exception e){
					throw new RuntimeException(e);
				}
			} else {
				if(!service.rejectQuotationRecords(ids, rejectReason)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00059", null, locale));
					return "redirect:/assignmentManagement/AssignmentApproval/editQuotationRecord?quotationRecordId=" + quotationRecordId;
				} else {
					service.updateSessionAfterSubmit(quotationRecordId, sessionModel);
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
					if ("rejectAndNext".equals(approveRejectBtn))
						return "redirect:/assignmentManagement/AssignmentApproval/editQuotationRecord?quotationRecordId=" + (sessionModel.getNextId() != null ? sessionModel.getNextId() : sessionModel.getPreviousId());
				}
			}
		} catch (Exception e) {
			logger.error("approveRejectQuotationRecord", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentManagement/AssignmentApproval/home?keepSession=true";
	}

	/**
	 * Approve/Reject Assignment Reallocation
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveRejectQuotationRecords", method = RequestMethod.POST)
	public String approveRejectQuotationRecords(@RequestParam(value = "approveRejectBtn") String approveRejectBtn,
			@RequestParam(value = "id[]") Integer[] id,
			@RequestParam(value = "rejectReason", required=false) String rejectReason,
			@ModelAttribute("assignmentApprovalSessionModel") SessionModel sessionModel,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			@SuppressWarnings("unchecked")
			List<Integer> ids = new ArrayList<Integer>(Arrays.asList(id));
			
			if("approve".equals(approveRejectBtn)) {
				try{
					if(!approveQuotationRecordIdsAndFollowUp(ids)) {
						redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
					} else {
						redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
					}
				} catch (Exception e){
					throw new RuntimeException(e);
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
		return "redirect:/assignmentManagement/AssignmentApproval/home?keepSession=true";
	}
	
	/**
	 * Approve/Reject Assignment Reallocation
	 */
	@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveAllQuotationRecords")
	public String approveAllQuotationRecords(
			@RequestParam(value = "search", required = false) String search,
			
			@RequestParam(value = "outletId", required = false) Integer outletId,
			@RequestParam(value = "outletTypeId", required = false) String outletTypeId,
			@RequestParam(value = "personInChargeId", required = false) Integer personInChargeId,
			
			@RequestParam(value = "districtId[]", required = false) Integer[] districtId,
			@RequestParam(value = "unitCategory", required = false) String unitCategory,
			@RequestParam(value = "tpuId[]", required = false) Integer[] tpuId,
			
			@RequestParam(value = "isProductChange", required = false) Boolean isProductChange,
			@RequestParam(value = "isSPricePeculiar", required = false) Boolean isSPricePeculiar,
			@RequestParam(value = "availability[]", required = false) Integer[] availability,
			
			@RequestParam(value = "referenceMonth", required = false) String referenceMonth,
			@RequestParam(value = "purposeId", required = false) Integer[] purposeId, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			
			List<Integer> ids = service.getQuotationRecordIdToBeApproveAll(search, outletId, outletTypeId, personInChargeId, districtId, 
					unitCategory, tpuId, isProductChange, isSPricePeculiar, availability, referenceMonth, purposeId);
			try{
				if(!approveQuotationRecordIdsAndFollowUp(ids)) {
					model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
				} else {
					model.addAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
				}
			} catch (Exception e){
				throw new RuntimeException(e);
			}
			
			return "/partial/messageRibbons";
			
		} catch (Exception e) {
			logger.error("Assignment Approval: Approve All", e);
		}
		
		model.addAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
		return "/partial/messageRibbons";
	}
	
	/**
	 * Last Request Model 
	 */
	@RequestMapping(value = "saveLastRequestModel")
	public @ResponseBody void saveLastRequestModel(
			@ModelAttribute("assignmentApprovalSessionModel") SessionModel sessionModel,
			SessionStatus sessionStatus,
			DatatableRequestModel requestModel){
		sessionModel.setLastRequestModel(requestModel);
	}
	
	/**
	 * Approve quotation records and handle data conversion
	 * @throws Exception 
	 */
	@Transactional (rollbackOn=Exception.class)
	public boolean approveQuotationRecordIdsAndFollowUp(List<Integer> quotationRecordIds) throws Exception {
		beforeJob(quotationRecordIds);
		if(!service.approveQuotationRecordIds(quotationRecordIds)) {
			failJob(quotationRecordIds);
			return false;
		} else {
			followup(quotationRecordIds);
			return true;
		}
	}

	@Transactional
	public void followup(List<Integer> ids) throws Exception{
		List<Integer> quotationRecordIds = quotationRecordService.getCurrentConvertQuotationRecordId(ids);
		quotationRecordIds = quotationRecordService.getBackTrackIdByOriginalId(quotationRecordIds);
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
			dataConversionService.convert(entity);
			service.handleVerification(entity);
		}

		service.runPELogic(quotationRecordService.recursiveQuery(ids));
	}

	@Transactional
	public void beforeJob(List<Integer> ids){
		quotationRecordService.updateQuotationRecordStatusForApproval(ids, "Approved");
	}

	@Transactional
	public void failJob(List<Integer> ids){
		quotationRecordService.updateQuotationRecordStatusForApproval(ids, "Submitted");
	}
}
