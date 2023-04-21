package com.kinetix.controller.assignmentManagement;

import java.util.List;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import com.kinetix.component.FuncCode;

import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.assignmentManagement.assignmentManagement.AssignmentUnitCategoryInfoWithVerify;
import capi.model.assignmentManagement.assignmentManagement.EditModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordPageViewModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList;
import capi.model.assignmentManagement.assignmentManagement.SessionModel;
import capi.service.AppConfigService;
import capi.service.UserService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.TpuService;

/**
 * PEViewAssignmentMaintenance
 */
@Secured({"RF2009","RF2014","UF1503","UF1504","UF1506"})
@FuncCode("RF2009")
@Controller("PEViewAssignmentMaintenanceController")
@RequestMapping("assignmentManagement/PEViewAssignmentMaintenance")
@SessionAttributes({"peSessionModel"})
public class PEViewAssignmentMaintenanceController {

	private static final Logger logger = LoggerFactory.getLogger(PEViewAssignmentMaintenanceController.class);

	@Resource(name="messageSource")
	private MessageSource messageSource;

	@Autowired
	private AssignmentMaintenanceService service;

	@Autowired
	private QuotationRecordService quotationRecordService;
	
	@Autowired
	private OutletService outletService;

	@Autowired
	private UserService userService;

	@Autowired
	private TpuService tpuService;

	@Autowired
	private AppConfigService configService;
	
	/**
	 * Init session
	 */
	@ModelAttribute("peSessionModel")
	public SessionModel initSessionModel() {
		return new SessionModel();
	}
	
	/**
	 * Get normal consignment select format
	 */
	@RequestMapping(value = "queryNormalConsignmentSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel queryNormalConsignmentSelect2(Select2RequestModel requestModel,
			@RequestParam(value = "assignmentId") int assignmentId) {
		try {
			return service.queryPEViewConsignmentSelect2(requestModel, assignmentId);
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
			return service.queryPEViewDistinctUnitCategorySelect2(requestModel, assignmentId);
		} catch (Exception e) {
			logger.error("queryNormalDistinctUnitCategorySelect2", e);
		}
		return null;
	}

	/**
	 * Get outlet types display
	 */
	@RequestMapping(value = "getOutletTypesDisplayByOutletId", method = RequestMethod.GET)
	public @ResponseBody List<String> getOutletTypesDisplayByOutletId(@RequestParam(value = "id") Integer id) {
		try {
			List<String> list = service.getOutletTypesDisplayByOutletId(id);
			return list;
		} catch (Exception e) {
			logger.error("getOutletTypesDisplayByOutletId", e);
		}
		return null;
	}

	/**
	 * home
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public String home(@ModelAttribute("sessionModel") SessionModel sessionModel, SessionStatus sessionStatus) {
		int assignmentId = sessionModel.getAssignmentId();
		
		sessionStatus.setComplete();
		sessionModel = new SessionModel();
		
		return "redirect:/assignmentManagement/PEViewAssignmentMaintenance/edit?assignmentId=" + assignmentId;
	}
	
	/**
	 * edit
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(@RequestParam(value = "assignmentId") Integer assignmentId,
			Model model,
			@ModelAttribute("peSessionModel") SessionModel sessionModel) {
		try {
			EditModel viewModel = service.prepareViewModel(assignmentId, null);
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
			return service.getPEViewQuotationRecordTableList(requestModel, assignmentId, consignmentCounter, unitCategory);
		} catch (Exception e) {
			logger.error("queryNormalQuotationRecord", e);
		}
		return null;
	}
	
	/**
	 * Cache Quotation Record Search Filter And Result
	 */
	@RequestMapping(value = "cacheQuotationRecordSearchFilterAndResult", method = RequestMethod.POST)
	public @ResponseBody boolean cacheQuotationRecordSearchFilterAndResult(@ModelAttribute("peSessionModel") SessionModel sessionModel) {
		try {
			List<Integer> ids = null;
			int assignmentId = sessionModel.getAssignmentId();
			String unitCategory = sessionModel.getUnitCategory();
			String consignmentCounter = sessionModel.getConsignmentCounter();
			DatatableRequestModel dataTableRequestModel = new DatatableRequestModel();
			dataTableRequestModel.setOrder(sessionModel.getOrder());
			
			if (sessionModel.getTab().equals("Normal")) {
				ids = service.getPEViewQuotationRecordTableListAllIds(assignmentId, consignmentCounter, unitCategory, dataTableRequestModel);
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
			@ModelAttribute("peSessionModel") SessionModel sessionModel) {
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
	 * AssignmentUnitCategoryInfo Dialog
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialog", method = RequestMethod.GET)
	public void assignmentUnitCategoryInfoDialog() {
	}

	/**
	 * AssignmentUnitCategoryInfo Dialog normal Content
	 */
	@RequestMapping(value = "assignmentUnitCategoryInfoDialogNormalContent", method = RequestMethod.POST)
	public String assignmentUnitCategoryInfoDialogNormalContent(Model model, @ModelAttribute("peSessionModel") SessionModel sessionModel) {
		try {
			List<AssignmentUnitCategoryInfoWithVerify> list = service.getUnitCategoryForPEView(sessionModel.getAssignmentId(), sessionModel.getConsignmentCounter(), sessionModel.getUnitCategory());
			model.addAttribute("model", list);
			model.addAttribute("readonly", true);
		} catch (Exception e) {
			logger.error("assignmentUnitCategoryInfoDialogNormalContent", e);
		}
		return "/assignmentManagement/PEViewAssignmentMaintenance/assignmentUnitCategoryInfoDialogContent";
	}
	
	/**
	 * Get status by assignment
	 */
	@RequestMapping(value = "getStatusByAssignment", method = RequestMethod.GET)
	public @ResponseBody int getStatusByAssignment(@RequestParam(value = "assignmentId") int assignmentId) {
		try {
			return service.getStatusByAssignment(assignmentId);
		} catch (Exception e) {
			logger.error("getStatusByAssignment", e);
		}
		return 0;
	}
}
