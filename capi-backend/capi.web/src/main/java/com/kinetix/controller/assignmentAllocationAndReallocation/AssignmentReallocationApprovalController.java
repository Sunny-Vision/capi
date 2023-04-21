package com.kinetix.controller.assignmentAllocationAndReallocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

import capi.entity.AssignmentReallocation;
import capi.entity.Tpu;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationApprovalEditModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationApprovalTableList;
import capi.service.NotificationService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.AssignmentReallocationApprovalService;
import capi.service.assignmentAllocationAndReallocation.AssignmentReallocationService;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.masterMaintenance.BatchService;
import capi.service.masterMaintenance.DistrictService;
import capi.service.masterMaintenance.OutletService;
import capi.service.masterMaintenance.UnitService;

import com.kinetix.component.FuncCode;


/**
 * UF-1505 Assignment Reallocation Approval
 */
@Secured("UF1507")
@FuncCode("UF1507")
@Controller("AssignmentReallocationApprovalController")
@RequestMapping("assignmentAllocationAndReallocation/AssignmentReallocationApproval")
public class AssignmentReallocationApprovalController {
	
	private static final Logger logger = LoggerFactory.getLogger(AssignmentReallocationApprovalController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private AssignmentReallocationApprovalService service;

	@Autowired
	private OutletService outletService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private BatchService batchService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private AssignmentMaintenanceService assignmentMaintenanceService;

	@Autowired
	private UnitService unitService;
	
	@Autowired
	private AssignmentReallocationService assignmentReallocationService;
	
	/**
	 * List assignment reallocation approval
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<AssignmentReallocationApprovalTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication authentication) {

		try {
			CapiWebAuthenticationDetails capiWebAuthenticationDetails =  (CapiWebAuthenticationDetails)authentication.getDetails();
			List<Integer> actedUsers = capiWebAuthenticationDetails.getActedUsers();
			actedUsers.add(capiWebAuthenticationDetails.getUserId());
			
			return service.getAssignmentReallocationApproval(requestModel, actedUsers);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit assignment reallocation approval
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(@RequestParam(value = "act", required = false) String act, 
			@RequestParam(value = "id", required = false) Integer id, 
			@RequestParam(value = "targetUserId", required = false) Integer targetUserId, 
			Model model, Locale locale, RedirectAttributes redirectAttributes) {

		try {
			AssignmentReallocation item = null;
			
			item = service.getAssignmentReallocationById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/assignmentAllocationAndReallocation/AssignmentReallocation/home";
			}
			
			AssignmentReallocationApprovalEditModel editModel = service.convertEntityToModel(item);
			
			model.addAttribute("model", editModel);
			
			List<Tpu> tpus = service.getTpus();
			
			model.addAttribute("tpus", tpus);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "queryAssignmentReallocationApprovalList")
	public @ResponseBody DatatableResponseModel<AssignmentReallocationApprovalEditModel>
		queryAssignmentReallocationApprovalList(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "originalUserId", required = false) Integer originalUserId,
				@RequestParam(value = "targetUserId", required = false) Integer targetUserId,
				@RequestParam(value = "tpuIds[]", required = false) List<Integer> tpuIds,
				@RequestParam(value = "outletTypeId", required = false) String outletTypeId,
				@RequestParam(value = "districtId", required = false) Integer districtId,
				@RequestParam(value = "batchId", required = false) Integer batchId,
				@RequestParam(value = "collectionDate", required = false) String collectionDate,
				@RequestParam(value = "selected", required = false) String selected,
				@RequestParam(value = "assignmentStatus", required = false) Integer assignmentStatus,
				@RequestParam(value = "surveyMonthId", required = false) Integer surveyMonthId,
				@RequestParam(value = "assignmentReallocationId", required = false) Integer assignmentReallocationId) {

		try {
			return service.getAssignmentReallocationApprovalList(requestModel, originalUserId, targetUserId, 
									tpuIds, outletTypeId, districtId, batchId, collectionDate, selected,
									assignmentStatus, surveyMonthId, assignmentReallocationId);
		} catch (Exception e) {
			logger.error("queryAssignmentReallocationApprovalList", e);
		}
		return null;
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "queryQuotationRecordReallocationApprovalList")
	public @ResponseBody DatatableResponseModel<AssignmentReallocationApprovalEditModel>
		queryQuotationRecordReallocationApprovalList(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "originalUserId", required = false) Integer originalUserId,
				@RequestParam(value = "targetUserId", required = false) Integer targetUserId,
				@RequestParam(value = "tpuIds[]", required = false) List<Integer> tpuIds,
				@RequestParam(value = "outletTypeId", required = false) String outletTypeId,
				@RequestParam(value = "districtId", required = false) Integer districtId,
				@RequestParam(value = "batchId", required = false) Integer batchId,
				@RequestParam(value = "collectionDate", required = false) String collectionDate,
				@RequestParam(value = "selected", required = false) String selected,
				@RequestParam(value = "category", required = false) String category,
				@RequestParam(value = "quotationStatus", required = false) String quotationStatus,
				@RequestParam(value = "assignmentReallocationId", required = false) Integer assignmentReallocationId) {

		try {
			return service.getQuotationRecordReallocationApprovalList(requestModel, originalUserId, targetUserId, 
									tpuIds, outletTypeId, districtId, batchId, collectionDate, selected,
									category, quotationStatus, assignmentReallocationId);
		} catch (Exception e) {
			logger.error("queryQuotationRecordReallocationApprovalList", e);
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
	 * Get district select format
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
	 * Get batch
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
	 * Get survey month
	 */
	@RequestMapping(value = "querySurveyMonthSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		querySurveyMonthSelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return assignmentMaintenanceService.querySurveyMonthSelect2(requestModel, null);
		} catch (Exception e) {
			logger.error("queryBatchSelect2", e);
		}
		return null;
	}

	/**
	 * Get unit category
	 */
	@RequestMapping(value = "queryCategorySelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		queryCategorySelect2(Locale locale, Model model, Select2RequestModel requestModel) {
		try {
			return unitService.queryUnitCategorySelect2(requestModel);
		} catch (Exception e) {
			logger.error("queryCategorySelect2", e);
		}
		return null;
	}

	/**
	 * Approve/Reject Assignment Reallocation
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 256)")
	@RequestMapping(value = "approveReject", method = RequestMethod.POST)
	public String approveReject(@RequestParam(value = "approveRejectBtn", required = false) String approveRejectBtn,
			@ModelAttribute AssignmentReallocationApprovalEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			if (item.getAssignmentReallocationId() != null) {
				if (service.getAssignmentReallocationById(item.getAssignmentReallocationId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/assignmentAllocationAndReallocation/AssignmentReallocationApproval/home";
				}
			}
			
			if("approve".equals(approveRejectBtn)) {
				if(!service.approveAssignmentReallocation(item)){
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00058", null, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00003", null, locale));
					
					Set<Integer> uniqueIds = new HashSet<Integer>();  
					AssignmentReallocation assignmentReallocation = service.getAssignmentReallocationById(item.getAssignmentReallocationId());

					//N00077(Prev ref. no. N00015/N00016)
					//Receiver = From officer(A), To officer(B), Submit To(CSO), CSO of A, CSO of B
					if (assignmentReallocation.getOriginalUser()!=null){
						uniqueIds.add(assignmentReallocation.getOriginalUser().getId());
					}
					uniqueIds.add(assignmentReallocation.getTargetUser().getId());
					
					List<Integer> ids = (uniqueIds != null && !uniqueIds.isEmpty()) ? new ArrayList<Integer>(uniqueIds) : null;
					List<User> users = (ids != null && !ids.isEmpty()) ? userService.getActiveUsersByIds(ids) : null;
					
					//N00076 = The Assignment Reallocation from {0} - {1} (Team {2}) to {3} - {4} (Team {5}) has been approved.
					//{0} = from officer code
					//{1} = from officer name
					//{2} = from officer team 
					//{3} = to officer code (Field officer to receive the assignments)
					//{4} = to officer name (Field officer to receive the assignments
					//{5} = to officer team (Field officer to receive the assignments)
					String fromOfficerCode = assignmentReallocation.getOriginalUser() == null ? SystemConstant.EMPTY_STRING_FORMAT : assignmentReallocation.getOriginalUser().getStaffCode();
					String fromOfficerName = assignmentReallocation.getOriginalUser() == null ? SystemConstant.EMPTY_STRING_FORMAT : assignmentReallocation.getOriginalUser().getEnglishName();
					String fromOfficerTeam = assignmentReallocation.getOriginalUser() == null || 
							StringUtils.isEmpty(assignmentReallocation.getOriginalUser().getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : assignmentReallocation.getOriginalUser().getTeam();
					
					String toOfficerCode = assignmentReallocation.getTargetUser().getStaffCode();
					String toOfficerName = assignmentReallocation.getTargetUser().getEnglishName();
					String toOfficerTeam = StringUtils.isEmpty(assignmentReallocation.getTargetUser().getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : assignmentReallocation.getTargetUser().getTeam();

					if(users != null && !users.isEmpty()){
						for(User user : users) {
							notificationService.sendNotification(user, "Assignment Reallocation Approval", 
									messageSource.getMessage("N00076", new Object[]{fromOfficerCode, fromOfficerName, fromOfficerTeam,
											toOfficerCode, toOfficerName, toOfficerTeam}, locale.ENGLISH), true);
						}
					}
				}
			} else {
				if(!service.rejectAssignmentReallocation(item)) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00059", null, locale));
				} else {
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00004", null, locale));
					
					Set<Integer> uniqueIds = new HashSet<Integer>();
					
					//N00077(Prev ref. no. N00015/N00016) - 
					//Receiver = From officer(A), Submit To(CSO), CSO of A, CSO of B 
					User originalUser = null;
					if (item.getOriginalUserId()!=null){
						originalUser = userService.getUserById(item.getOriginalUserId());
					}
					User targetUser = userService.getUserById(item.getTargetUserId());
					User submitToUser = userService.getUserById(item.getSubmitToUserId());
					
					if (originalUser!=null){
						uniqueIds.add(originalUser.getId());
					}
					
					List<Integer> ids = (uniqueIds != null && !uniqueIds.isEmpty()) ? new ArrayList<Integer>(uniqueIds) : null;
					List<User> users = (ids != null && !ids.isEmpty()) ? userService.getActiveUsersByIds(ids) : null;
					
					//N00077 = The Assignment Reallocation from {0} - {1} (Team {2}) to {3} - {4} (Team {5}) has been rejected.
					//{0} = from officer code
					//{1} = from officer name
					//{2} = from officer team 
					//{3} = to officer code (Field officer to receive the assignments)
					//{4} = to officer name (Field officer to receive the assignments
					//{5} = to officer team (Field officer to receive the assignments)
					String fromOfficerCode = originalUser == null ? SystemConstant.EMPTY_STRING_FORMAT : originalUser.getStaffCode();
					String fromOfficerName = originalUser == null ? SystemConstant.EMPTY_STRING_FORMAT : originalUser.getEnglishName();
					String fromOfficerTeam = originalUser == null || 
							StringUtils.isEmpty(originalUser.getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : originalUser.getTeam();
					
					String toOfficerCode = targetUser.getStaffCode();
					String toOfficerName = targetUser.getEnglishName();
					String toOfficerTeam = StringUtils.isEmpty(targetUser.getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : targetUser.getTeam();
					
					if(users != null && !users.isEmpty()){
						for(User user : users) {
							notificationService.sendNotification(user, "Assignment Reallocation Approval", 
									messageSource.getMessage("N00077", new Object[]{ fromOfficerCode, fromOfficerName, fromOfficerTeam,
											toOfficerCode, toOfficerName, toOfficerTeam}, locale.ENGLISH), true);
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("approveReject", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentAllocationAndReallocation/AssignmentReallocationApproval/home";
	}

}