package com.kinetix.controller.timelogManagement;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

import capi.dal.AssignmentDao;
import capi.entity.Assignment;
import capi.entity.User;
import capi.entity.WorkingSessionSetting;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.timeLogManagement.TimeLogSelect2ResponseModel;
import capi.model.SystemConstant;
import capi.model.timeLogManagement.FieldworkTimeLogModel;
import capi.model.timeLogManagement.TelephoneTimeLogModel;
import capi.model.timeLogManagement.TimeLogModel;
import capi.model.timeLogManagement.TimeLogSelect2Item;
import capi.model.timeLogManagement.TimeLogTableList;
import capi.model.timeLogManagement.ValidationResultModel;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.itineraryPlanning.ItineraryPlanningService;
import capi.service.timeLogManagement.TimeLogService;

import com.kinetix.component.FuncCode;

/**
 * Handles requests for the application home page.
 */

@Secured("UF1801")
@FuncCode("UF1801")
@Controller("TimeLogController")
@RequestMapping("timeLogManagement/TimeLogMaintenance")
public class TimeLogController {

	private static final Logger logger = LoggerFactory.getLogger(TimeLogController.class);

	@Resource(name = "messageSource")
	MessageSource messageSource;

	@Autowired
	private TimeLogService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AssignmentDao assignmentDao;
		
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private ItineraryPlanningService itineraryPlanService;
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Locale locale, Model model, Authentication auth) {
//		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//		UserAccessModel uam = userService.gatherUserRequiredInfo(userService.getUserById(detail.getUserId()));
//		model.addAttribute("uam", uam);

	}

	/**
	 * datatable query function
	 */
	@RequestMapping(value = "query")
	public @ResponseBody DatatableResponseModel<TimeLogTableList>
		query(Locale locale, Model model, DatatableRequestModel requestModel, Authentication auth, Integer userId) {
		try {
//			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//			List<Integer> userIds = new ArrayList<Integer>();
//			if ((detail.getAuthorityLevel() & 4) != 4) {
//				userIds.add(detail.getUserId());
//			} else {
//				if (requestModel.getSearch().get("userId") != "") {
//					userIds.add(Integer.parseInt(requestModel.getSearch().get("userId")));
//				}
//			}
			List<Integer> userIds = new ArrayList<Integer>();
			if (!StringUtils.isEmpty(requestModel.getSearch().get("userId"))) {
				userIds.add(Integer.parseInt(requestModel.getSearch().get("userId")));
			}
			return service.queryTimeLog(requestModel, userIds.toArray(new Integer[userIds.size()])); //userIds.toArray(new Integer[userIds.size()]));
		} catch (Exception e) {
			logger.error("query", e);
		}
		return null;
	}
	/**
	 * view (read only)
	 */
	@RequestMapping(value = "view", method = RequestMethod.GET)
	public String view(@RequestParam(value = "id", required = true) Integer id,
			Locale locale, Model model, Authentication auth, RedirectAttributes redirectAttributes) {
		TimeLogModel timeLogModel;
//		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//		UserAccessModel uam = userService.gatherUserRequiredInfo(userService.getUserById(detail.getUserId()));
//		model.addAttribute("uam", uam);
		model.addAttribute("act", "view");
		timeLogModel = service.getTimeLogModel(id);
		
//		if ((detail.getAuthorityLevel()&1) != 1 && (detail.getAuthorityLevel()&2) != 2 && (detail.getAuthorityLevel()&4) != 4 && timeLogModel.getUserId() != detail.getUserId() ) {
//			// no access 
//			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
//					messageSource.getMessage("E00011", null, locale));
//			return "redirect:/timeLogManagement/TimeLogMaintenance/home";
//			
//		} else {	
			List<String> surveyCountList = new ArrayList<String>();
			surveyCountList.add(new String(SystemConstant.SURVEY_1));
			surveyCountList.add(new String(SystemConstant.SURVEY_2));
			surveyCountList.add(new String(SystemConstant.SURVEY_3));
			model.addAttribute("surveyCountList", surveyCountList);
			
			if (timeLogModel.getIsOtherWorkingSession() != null && timeLogModel.getIsOtherWorkingSession() == true) {
				timeLogModel.setWorkingSessionText(timeLogModel.getOtherWorkingSessionFrom() + " - " + timeLogModel.getOtherWorkingSessionTo() );
			} else {
				WorkingSessionSetting workingSessionSetting = service.getWorkingSessionSettings(timeLogModel.getWorkingSessionId());
				timeLogModel.setWorkingSessionText( commonService.formatTime(workingSessionSetting.getFromTime()) + " - " + commonService.formatTime(workingSessionSetting.getToTime()));
			}
		//}
		model.addAttribute("model", timeLogModel);
		return "/timeLogManagement/TimeLogMaintenance/view";
		
	}	
	/**
	 * Edit
	 * @throws ParseException 
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public void edit(@RequestParam(value = "id", required = false) Integer id, 
			@RequestParam(value = "assignmentId", required = false) Integer assignmentId,
			@RequestParam(value = "userId", required = false) Integer userId,
			@RequestParam(value = "tab", required = false) String tab,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "date", required = false) String date,
			Locale locale, Model model, Authentication auth) throws ParseException {
		TimeLogModel timeLogModel = new TimeLogModel();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//		UserAccessModel uam = userService.gatherUserRequiredInfo(userService.getUserById(detail.getUserId()));
//		model.addAttribute("uam", uam);
		if (assignmentId != null && userId != null && tab != null && action != null && date != null) {
			model.addAttribute("act", "edit");
			Assignment assignment = assignmentDao.findById(assignmentId);
			
			Integer collectionMethod = 1;
			
			if (assignment.getCollectionMethod() != null) {
				collectionMethod = assignment.getCollectionMethod();
			} else {
				if (assignment.getOutlet().getCollectionMethod() != null) {
					collectionMethod = assignment.getOutlet().getCollectionMethod();
				}
			}
			
			if (collectionMethod != null ) {
				if (collectionMethod == 1 ) {
					FieldworkTimeLogModel fieldworkTimeLogModel = service.getFieldworkTimeLogModel(assignment);
					Date today = new Date();
					String startTime = commonService.formatTime(today);
					fieldworkTimeLogModel.setStartTime(startTime);
					Integer timeLogId = service.getTimeLogIdByUserIdAndDate(userId, commonService.getDate(date));
					if (timeLogId == null) {					
						timeLogModel = new TimeLogModel();
						User user = userService.getUserById(userId);
						timeLogModel.setUserId(user.getId());
						timeLogModel.setUserCode(user.getStaffCode() + " - " + user.getChineseName());
						timeLogModel.setDate(date);						
					} else {
						timeLogModel = service.getTimeLogModel(timeLogId);
					}
					if (timeLogModel.getFieldworkTimeLogs() == null) {
						timeLogModel.setFieldworkTimeLogs(new ArrayList<FieldworkTimeLogModel>());
					}
					if (action.equals("Start")) {

						timeLogModel.getFieldworkTimeLogs().add(fieldworkTimeLogModel);	
						if (tab.equals("Verify")) {
							Integer verifiedCount = service.getVerifiedCount(commonService.getMonth(fieldworkTimeLogModel.getReferenceMonth()),fieldworkTimeLogModel.getCaseReferenceNo());
							fieldworkTimeLogModel.setEnumerationOutcome("O");
							fieldworkTimeLogModel.setRemark("Verification ("+verifiedCount+")");
						}
						
					} else if (action.equals("Update")) {
						List<FieldworkTimeLogModel> fieldworkTimeLogModels = timeLogModel.getFieldworkTimeLogs();
						for (FieldworkTimeLogModel existingFieldworkTimeLogModel : fieldworkTimeLogModels) {
							if (existingFieldworkTimeLogModel.getCaseReferenceNo().equals(fieldworkTimeLogModel.getCaseReferenceNo())) {
								BeanUtils.copyProperties(fieldworkTimeLogModel, existingFieldworkTimeLogModel);
								break;
							}
						}
						if (tab.equals("Verify")) {
							Integer verifiedCount = service.getVerifiedCount(commonService.getMonth(fieldworkTimeLogModel.getReferenceMonth()),fieldworkTimeLogModel.getCaseReferenceNo());
							fieldworkTimeLogModel.setEnumerationOutcome("O");
							fieldworkTimeLogModel.setRemark("Verification ("+verifiedCount+")");
						}
						
					}
				} else if (collectionMethod == 2 || collectionMethod == 3 || collectionMethod == 4){
					TelephoneTimeLogModel telephoneTimeLogModel = service.getTelephoneTimeLogModel(assignment);
					telephoneTimeLogModel.setSession("A");
					Integer timeLogId = service.getTimeLogIdByUserIdAndDate(userId, commonService.getDate(date));
					if (timeLogId == null) {
						timeLogModel = new TimeLogModel();
						User user = userService.getUserById(userId);
						timeLogModel.setUserId(user.getId());
						timeLogModel.setUserCode(user.getStaffCode() + " - " + user.getChineseName());
						timeLogModel.setDate(date);
					} else {
						timeLogModel = service.getTimeLogModel(timeLogId);
					}
					if (timeLogModel.getTelephoneTimeLogs() == null) {
						timeLogModel.setTelephoneTimeLogs(new ArrayList<TelephoneTimeLogModel>());
					}
					if (action.equals("Start")) {
						timeLogModel.getTelephoneTimeLogs().add(telephoneTimeLogModel);		
					} else if (action.equals("Update")) {		
						List<TelephoneTimeLogModel> telephoneTimeLogModels = timeLogModel.getTelephoneTimeLogs();
						for (TelephoneTimeLogModel existingTelephoneTimeLogModel : telephoneTimeLogModels) {
							if (existingTelephoneTimeLogModel.getCaseReferenceNo().equals(telephoneTimeLogModel.getCaseReferenceNo())) {
								BeanUtils.copyProperties(telephoneTimeLogModel, existingTelephoneTimeLogModel);
								break;
							}
						}
					}
				}
			}
		} else {
			if (id != null && id > 0) {
				model.addAttribute("act", "edit");
				timeLogModel = service.getTimeLogModel(id);
				
			} else {
				model.addAttribute("act", "add");
				timeLogModel = new TimeLogModel();
				timeLogModel.setUserId(detail.getUserId());
				timeLogModel.setUserCode(detail.getStaffCode() + " - " + detail.getChineseName());
			}
		}
		
		loadEditModel(model);
		model.addAttribute("model", timeLogModel);
		
	}
	/**
	 * Delete Time Log
	 */
	@PreAuthorize("hasPermission(#user, 4) or hasPermission(#user, 16) or hasPermission(#user, 256)")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id") ArrayList<Integer> ids, 
			@RequestParam(value = "page", required = false) String page,
			Model model, Locale locale, Authentication auth,
			RedirectAttributes redirectAttributes) {
		try {
			
			if (!service.deleteTimeLog(ids)) {
				
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00011", null, locale));
				
			} else {
				
				model.addAttribute(SystemConstant.SUCCESS_MESSAGE,
					messageSource.getMessage("I00002", null, locale));
			}
			
		} catch (Exception e) {
			logger.error("delete", e);
			model.addAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00013", null, locale));
		}
		return "/partial/messageRibbons";

	}
	/**
	 * Save
	 */
	@PreAuthorize("hasPermission(#user, 1) or hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 16)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(@ModelAttribute TimeLogModel timeLogModel,
			RedirectAttributes redirectAttributes, Locale locale, Model model) {
		try {
			if (timeLogModel.getFieldworkTimeLogs() != null && timeLogModel.getFieldworkTimeLogs().size() > 0) {
				Collections.sort(timeLogModel.getFieldworkTimeLogs());
			}
			switch (service.saveTimeLog(timeLogModel)) {
			case 0:
				if (timeLogModel.getStatus().equals(SystemConstant.TIMELOG_STATUS_APPROVED) || 
						timeLogModel.getStatus().equals(SystemConstant.TIMELOG_STATUS_SUBMITTED))
					service.addOTCalendarEvent(timeLogModel);
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE,
						messageSource.getMessage("I00001", null, locale));
				break;
			case 1:
				model.addAttribute("model",timeLogModel);
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00011", null, locale));
				loadEditModel(model);
				model.addAttribute("act", "edit");
				service.setUserCode(timeLogModel);
				return "/timeLogManagement/TimeLogMaintenance/edit";
			case 2:
				model.addAttribute("model",timeLogModel);
				model.addAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00140", null, locale));
				loadEditModel(model);
				model.addAttribute("act", "edit");
				service.setUserCode(timeLogModel);
				return "/timeLogManagement/TimeLogMaintenance/edit";
			}
		} catch (Exception e) {
			logger.error("save", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/timeLogManagement/TimeLogMaintenance/home";
		
	}
	
	/**
	 * Get enumeration outcome by purpose
	 * @throws ParseException 
	 */
	@RequestMapping(value = "validate", method = RequestMethod.POST)
	public @ResponseBody ValidationResultModel validate(@RequestParam(value = "caseReferenceNo", required = true) List<String> logReferenceNos, 
			@RequestParam(value = "userId", required = true) Integer userId,
			@RequestParam(value = "date", required = true) String date,
			HttpServletResponse response) throws ParseException {
					
		return service.validateTimeLogDeviation(logReferenceNos, userId, commonService.getDate(date));
	}
	
	/**
	 * Get enumeration outcome by purpose
	 */
	@RequestMapping(value = "queryEnumerationOutcome", method = RequestMethod.POST)
	public @ResponseBody String
	queryEnumerationOutcome(@RequestParam(value = "survey", required = true) String survey, HttpServletResponse response) {
		return service.getEnumerationOutcomeBySurvey(survey);
	}
	
	/**
	 * query assignment by Telephone reference no.
	 */
	@RequestMapping(value = "queryTelephoneReferenceNoSelect2", method = RequestMethod.GET)
	public @ResponseBody TimeLogSelect2ResponseModel
	queryTelephoneReferenceNoSelect2(Locale locale, Model model, Select2RequestModel requestModel,
			@RequestParam("referenceMonth") String referenceMonth, 
			@RequestParam("survey") String survey,
			@RequestParam("userId") Integer userId) {
		try {
			Date refMonth = commonService.getMonth(referenceMonth);
			return service.queryCaseReferenceNoSelect2(refMonth, survey, userId, "Telephone", requestModel);
		} catch (Exception e) {
			logger.error("queryTelephoneReferenceNoSelect2", e);
		}
		return null;
	}
	
	/**
	 * get detail by Telephone reference no.
	 */
	@RequestMapping(value = "getTelephoneReferenceNoDetail", method = RequestMethod.GET)
	public @ResponseBody TimeLogSelect2Item getTelephoneReferenceNoDetail(
			@RequestParam(value = "referenceMonth", required = false) String referenceMonth,
			@RequestParam(value = "referenceNo", required = false) String referenceNo) {
		try {
			if (referenceMonth == null || referenceMonth.length() == 0) return null;
			if (referenceNo == null || referenceNo.length() == 0) return null;
			Date refMonth = commonService.getMonth(referenceMonth);
			return service.getTelephoneReferenceNoDetailById(refMonth, referenceNo);
		} catch (Exception e) {
			logger.error("getTelephoneReferenceNoDetail", e);
		}
		return null;
	}
	
	/**
	 * query assignment by Fieldwork reference no.
	 */
	@RequestMapping(value = "queryFieldworkReferenceNoSelect2", method = RequestMethod.GET)
	public @ResponseBody TimeLogSelect2ResponseModel
	queryFieldworkReferenceNoSelect2(Locale locale, Model model, Select2RequestModel requestModel,
			@RequestParam("referenceMonth") String referenceMonth, 
			@RequestParam("survey") String survey,
			@RequestParam("userId") Integer userId) {
		try {
			if (StringUtils.isNotEmpty(referenceMonth)){
				Date refMonth = commonService.getMonth(referenceMonth);
				return service.queryCaseReferenceNoSelect2(refMonth, survey, userId, "Fieldwork", requestModel);
			}
			else{
				return service.queryCaseReferenceNoSelect2(null, survey, userId, "Fieldwork", requestModel);
			}
			
		} catch (Exception e) {
			logger.error("queryFieldworkReferenceNoSelect2", e);
		}
		return null;
	}
	
	/**
	 * get detail by Fieldwork reference no.
	 */
	@RequestMapping(value = "getFieldworkReferenceNoDetail", method = RequestMethod.GET)
	public @ResponseBody TimeLogSelect2Item getFieldworkReferenceNoDetail(
			@RequestParam(value = "referenceMonth", required = false) String referenceMonth,
			@RequestParam(value = "referenceNo", required = false) String referenceNo) {
		try {
			if (referenceNo == null || referenceNo.length() == 0) return null;
			if (!StringUtils.isEmpty(referenceMonth)) {
				Date refMonth = commonService.getMonth(referenceMonth);
				return service.getFieldworkReferenceNoDetailById(refMonth, referenceNo);
			} else {
				return service.getFieldworkReferenceNoDetailById(null, referenceNo);
			}
		} catch (Exception e) {
			logger.error("getTelephoneReferenceNoDetail", e);
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
			return userService.queryOfficerSupervisorSelect2(requestModel, detail.getUserId());
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
	
	@RequestMapping(value = "checkItineraryReferenceNo")
	public @ResponseBody boolean checkItineraryReferenceNo(@RequestParam(value = "referenceNo[]", required = false) List<String> referenceNo, Integer userId, String date){
		try {
			Date reqDate = commonService.getDate(date);
			return itineraryPlanService.checkItineraryReferenceNo(referenceNo, userId, reqDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("checkItineraryReferenceNo", e);
		}
		return true;
	}
	
	/**
	 * Reset Submitted / Approved time log record status to draft on field officer request
	 */
	@RequestMapping(value = "resetTimeLogStatus", method = RequestMethod.POST)
	public String resetTimeLogStatus(@RequestParam(value = "id", required = true) int id, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			service.resetTimeLogStatus(id);
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
			return "redirect:/timeLogManagement/TimeLogMaintenance/home";
		} catch (Exception e) {
			logger.error("resetTimeLogStatus", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
		}
		return "redirect:/timeLogManagement/TimeLogMaintenance/home";
	}
	
	/**
	 * Get existing Time Log Record Date by User id
	 */
	@RequestMapping(value = "queryTimeLogDate", method = RequestMethod.POST)
	public @ResponseBody List<String> queryTimeLogDate(@RequestParam(value = "userId") Integer userId) {
		try {
			List<String> timeLogDate = service.getCreatedTimeLogDate(userId);
			return timeLogDate;
		} catch (Exception e) {
			logger.error("queryTimeLogDate", e);
		}
		return null;
	}
	
	private void loadEditModel(Model model) {
		model.addAttribute("workingSessionSettings", service.getWorkingSessionSettings());
		
		List<String> surveyCountList = new ArrayList<String>();
		surveyCountList.add(new String(SystemConstant.SURVEY_1));
		surveyCountList.add(new String(SystemConstant.SURVEY_2));
		surveyCountList.add(new String(SystemConstant.SURVEY_3));
		model.addAttribute("surveyCountList", surveyCountList);
		
		List<String> surveyList = new ArrayList<String>();
		surveyList.add(new String(SystemConstant.SURVEY_1));
		surveyList.add(new String(SystemConstant.SURVEY_2));
		surveyList.add(new String(SystemConstant.SURVEY_3));
		surveyList.add(new String(SystemConstant.SURVEY_4));
		model.addAttribute("surveyList", surveyList);
		
		List<String> activityList = new ArrayList<String>();
		activityList.add(new String(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_1));
		activityList.add(new String(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_2));
		activityList.add(new String(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_3));
		activityList.add(new String(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_4));
		activityList.add(new String(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_5));
		activityList.add(new String(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_6));
		activityList.add(new String(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_7));
		activityList.add(new String(SystemConstant.FIELDWORKTIMELOG_ACITIVITY_8));
		model.addAttribute("activityList", activityList);

		List<String> transportList = new ArrayList<String>();
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_1));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_2));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_3));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_4));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_5));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_6));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_7));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_8));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_9));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_10));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_11));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_12));
		transportList.add(new String(SystemConstant.FIELDWORKTIMELOG_TRANSPORT_13));
		model.addAttribute("transportList", transportList);
	}
	
}
