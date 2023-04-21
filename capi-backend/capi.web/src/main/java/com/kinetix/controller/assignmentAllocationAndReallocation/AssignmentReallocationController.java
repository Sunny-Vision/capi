package com.kinetix.controller.assignmentAllocationAndReallocation;

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

import capi.entity.AssignmentReallocation;
import capi.entity.Tpu;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationEditModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationTableList;
import capi.model.commonLookup.AssignmentReallocationLookupTableList;
import capi.model.commonLookup.OutstandingAssignmentLookupTableList;
import capi.model.commonLookup.QuotationRecordReallocationLookupTableList;
import capi.service.NotificationService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.AssignmentReallocationService;
import capi.service.lookup.AssignmentReallocationLookupService;
import capi.service.lookup.OutstandingAssignmentLookupService;
import capi.service.lookup.QuotationRecordReallocationLookupService;


/**
 * UF-1505 Assignment Reallocation
 */
@Secured("UF1505")
@FuncCode("UF1505")
@Controller("AssignmentReallocationController")
@RequestMapping("assignmentAllocationAndReallocation/AssignmentReallocation")
public class AssignmentReallocationController {
	
	private static final Logger logger = LoggerFactory.getLogger(AssignmentReallocationController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private AssignmentReallocationService service;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private AssignmentReallocationLookupService assignmentReallocationLookupService;

	@Autowired
	private QuotationRecordReallocationLookupService quotationRecordReallocationLookupService;

	@Autowired
	private OutstandingAssignmentLookupService outstandingAssignmentLookupService;

	/**
	 * List assignment reallocation
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model) {
		
	}

	/**
	 * DataTable query
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<AssignmentReallocationTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication authentication) {

		try {
			CapiWebAuthenticationDetails capiWebAuthenticationDetails =  (CapiWebAuthenticationDetails)authentication.getDetails();
			Integer originalUserId = capiWebAuthenticationDetails.getUserId();
			Integer authorityLevel = capiWebAuthenticationDetails.getAuthorityLevel();
			
			boolean isFieldOfficer = false;
			if( ((authorityLevel & 2) != 2)
					&& ((authorityLevel & 4) != 4)
					&& ((authorityLevel & 8) != 8)
					&& ((authorityLevel & 256) != 256)
					&& ((authorityLevel & 2048) != 2048) )
				if((authorityLevel & 16) == 16)
					isFieldOfficer = true;
			
			return service.getAssignmentReallocationList(requestModel, isFieldOfficer, originalUserId);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Edit assignment reallocation - change person / edit
	 */
	@RequestMapping(value = "changePerson", method = RequestMethod.GET)
	public String changePerson(@RequestParam(value = "act", required = false) String act, @RequestParam(value = "id", required = false) Integer id, 
			Model model, Locale locale, RedirectAttributes redirectAttributes, Authentication authentication) {

		try {
			AssignmentReallocation item = null;
			if ("edit".equals(act)) {
				model.addAttribute("act", "edit");
				item = service.getAssignmentReallocationById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/assignmentAllocationAndReallocation/AssignmentReallocation/home";
				}
			} else {
				model.addAttribute("act", "add");
				item = new AssignmentReallocation();
			}
			
			CapiWebAuthenticationDetails capiWebAuthenticationDetails =  (CapiWebAuthenticationDetails)authentication.getDetails();
			Integer creatorId = capiWebAuthenticationDetails.getUserId();
			Integer authorityLevel = capiWebAuthenticationDetails.getAuthorityLevel();
			
			AssignmentReallocationEditModel editModel = service.convertEntityToModel(item, creatorId, authorityLevel);
			
			model.addAttribute("model", editModel);
			
			//List<User> supervisors = service.getSupervisors();
			
			//model.addAttribute("supervisors", supervisors);
			
			List<Tpu> tpus = service.getTpus();
			
			model.addAttribute("tpus", tpus);
			
		} catch (Exception e) {
			logger.error("changePerson", e);
		}
		return null;
	}

	/**
	 * Edit assignment reallocation - change date
	 */
	@RequestMapping(value = "changeDate", method = RequestMethod.GET)
	public String changeDate(@RequestParam(value = "id", required = false) Integer id, 
			Model model, Locale locale, RedirectAttributes redirectAttributes/*, Authentication authentication*/) {

		try {
			model.addAttribute("act", "changeDate");
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}

	/**
	 * Get Assignment Reallocation detail
	 */
	@RequestMapping(value = "getAssignmentReallocationDetail")
	public @ResponseBody List<AssignmentReallocationLookupTableList> getAssignmentReallocationDetail(
			@RequestParam(value = "originalUserId", required = false) Integer originalUserId,
			@RequestParam(value = "assignmentIds[]", required = false) Integer[] assignmentIds) {
		try {
			if (assignmentIds == null || assignmentIds.length == 0) return null;
			return assignmentReallocationLookupService.getTableListByIds(originalUserId, assignmentIds);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get Quotation Record Reallocation detail
	 */
	@RequestMapping(value = "getQuotationRecordReallocationDetail")
	public @ResponseBody List<QuotationRecordReallocationLookupTableList> getQuotationRecordReallocationDetail(
			@RequestParam(value = "originalUserId", required = false) Integer originalUserId,
			@RequestParam(value = "quotationRecordIds[]", required = false) Integer[] quotationRecordIds) {
		try {
			if (quotationRecordIds == null || quotationRecordIds.length == 0) return null;
			return quotationRecordReallocationLookupService.getTableListByIds(originalUserId, quotationRecordIds);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get Outstanding Assignment detail
	 */
	@RequestMapping(value = "getOutstandingAssignmentDetail")
	public @ResponseBody List<OutstandingAssignmentLookupTableList> getOutstandingAssignmentDetail(
			@RequestParam(value = "assignmentIds[]", required = false) Integer[] assignmentIds) {
		try {
			if (assignmentIds == null || assignmentIds.length == 0) return null;
			return outstandingAssignmentLookupService.getTableListByIds(assignmentIds);
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}

	/**
	 * Get supervisor id
	 */
	@RequestMapping(value = "targetUserChosen", method = RequestMethod.GET)
	public @ResponseBody String targetUserChosen(Locale locale, Model model, DatatableRequestModel requestModel, Integer targetUserId) {
		try {
			User targetUser = userService.getUserById(targetUserId);
			Integer supervisorId = null;
			String supervisor = null;
			if(targetUser.getSupervisor() != null) {
				supervisorId = targetUser.getSupervisor().getUserId();
				supervisor = targetUser.getSupervisor().getStaffCode() + " - " + targetUser.getSupervisor().getChineseName();
			}
			//return supervisorId;
			return supervisorId + "," + supervisor;
		} catch (Exception e) {
			logger.error("targetUserChosen", e);
		}
		return null;
	}

	/**
	 * Save Assignment Reallocation
	 */
	@PreAuthorize("hasPermission(#user, 4) or hasPermission(#user, 8) or hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@RequestParam(value = "act", required = false) String act,
			@ModelAttribute AssignmentReallocationEditModel item, Model model, Locale locale, RedirectAttributes redirectAttributes,
			Authentication authentication) {
		try {
			if (item.getAssignmentReallocationId() != null) {
				if (service.getAssignmentReallocationById(item.getAssignmentReallocationId()) == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					return "redirect:/assignmentAllocationAndReallocation/AssignmentReallocation/home";
				}
			}
			if("changeDate".equals(act)) {
				service.saveOutstandingAssignment(item);
			} else {
				service.saveAssignmentReallocation(item);
				
//				Set<Integer> uniqueIds = new HashSet<Integer>();
//				
//				CapiWebAuthenticationDetails capiWebAuthenticationDetails = (CapiWebAuthenticationDetails)authentication.getDetails();
//				List<Integer> actedUserIds = capiWebAuthenticationDetails.getActedUsers();
//				
//				User targetUser = userService.getUserById(item.getTargetUserId());
//				
//				uniqueIds.add(item.getSubmitToUserId());
//				if(targetUser != null && targetUser.getSupervisor() != null){
//					uniqueIds.add(targetUser.getSupervisor().getId());
//				}
//				//Integer[] staffIds = (uniqueIds != null && !uniqueIds.isEmpty()) ? uniqueIds.toArray(new Integer[uniqueIds.size()]) : null;
//				//List<User> users = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR, staffIds);
//				List<Integer> ids = (uniqueIds != null && !uniqueIds.isEmpty()) ? new ArrayList<Integer>(uniqueIds) : null;
//				List<User> users = (ids != null && !ids.isEmpty()) ? userService.getActiveUsersByIds(ids) : null;
//				
//				//N00075 = {1} - {2} (Team {3}) has reallocated {0} assignments to {4} - {5} (Team {6}).
//				//{0} = no. of assignments
//				//{1} = from officer code
//				//{2} = from officer name
//				//{3} = from officer team 
//				//{4} = to officer code (Field officer to receive the assignments)
//				//{5} = to officer name (Field officer to receive the assignments
//				//{6} = to officer team (Field officer to receive the assignments)
//				User originalUser = userService.getUserById(item.getOriginalUserId());
//				
//				int noOfAssignment = (item.getAssignmentIds() != null) ? item.getAssignmentIds().size() : 0 ;
//				String fromOfficerCode = originalUser.getStaffCode();
//				String fromOfficerName = originalUser.getEnglishName();
//				String fromOfficerTeam = StringUtils.isEmpty(originalUser.getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : originalUser.getTeam();
//				
//				String toOfficerCode = targetUser.getStaffCode();
//				String toOfficerName = targetUser.getEnglishName();
//				String toOfficerTeam = StringUtils.isEmpty(targetUser.getTeam()) ? SystemConstant.EMPTY_STRING_FORMAT : targetUser.getTeam();
//				
//				if(users != null && !users.isEmpty()){
//					for(User user : users) {
//						notificationService.sendNotification(user, "Assignment Reallocation", 
//								messageSource.getMessage("N00075", new Object[]{noOfAssignment, fromOfficerCode, fromOfficerName, fromOfficerTeam,
//										toOfficerCode, toOfficerName, toOfficerTeam}, locale.ENGLISH), true);
//					}
//				}
				
			}
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/assignmentAllocationAndReallocation/AssignmentReallocation/home";
	}

	/**
	 * View assignment reallocation - change person / view
	 */
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(@RequestParam(value = "id") Integer id, 
			Model model, Locale locale, RedirectAttributes redirectAttributes, Authentication authentication) {

		try {
			AssignmentReallocation item = null;
			item = service.getAssignmentReallocationById(id);
			if (item == null) {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
				return "redirect:/assignmentAllocationAndReallocation/AssignmentReallocation/home";
			}
			
			CapiWebAuthenticationDetails capiWebAuthenticationDetails =  (CapiWebAuthenticationDetails)authentication.getDetails();
			Integer creatorId = capiWebAuthenticationDetails.getUserId();
			Integer authorityLevel = capiWebAuthenticationDetails.getAuthorityLevel();
			
			AssignmentReallocationEditModel editModel = service.convertEntityToModel(item, creatorId, authorityLevel);
			
			model.addAttribute("model", editModel);
			
		} catch (Exception e) {
			logger.error("changePerson", e);
		}
		return null;
	}
	
	@RequestMapping(value = "queryAssignmentReallocationList")
	public @ResponseBody DatatableResponseModel<AssignmentReallocationLookupTableList>
		queryAssignmentReallocationList(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "assignmentReallocationId", required = false) Integer assignmentReallocationId){
		try {
			return service.getReallocationAssignmentList(requestModel, assignmentReallocationId);
		} catch (Exception e) {
			logger.error("queryAssignmentReallocationList", e);
		}
		return null;
	}
	
	@RequestMapping(value = "queryQuotationRecordReallocationList")
	public @ResponseBody DatatableResponseModel<QuotationRecordReallocationLookupTableList>
		queryQuotationRecordReallocationList(Locale locale, Model model, DatatableRequestModel requestModel,
				@RequestParam(value = "assignmentReallocationId", required = false) Integer assignmentReallocationId){
		try {
			return service.getReallocationQuotationRecordList(requestModel, assignmentReallocationId);
		} catch (Exception e) {
			logger.error("queryQuotationRecordReallocationList", e);
		}
		return null;
	}
	
}