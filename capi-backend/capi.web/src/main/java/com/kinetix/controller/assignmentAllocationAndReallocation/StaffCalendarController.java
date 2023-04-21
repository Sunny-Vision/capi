package com.kinetix.controller.assignmentAllocationAndReallocation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import capi.entity.ActivityCode;
import capi.entity.CalendarEvent;
import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.CalendarDisplayModel;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.CalendarEventModel;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.StaffCalendarSession;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.StaffNameModel;
import capi.model.commonLookup.UserLookupTableList;
import capi.model.userAccountManagement.ActingModel;
import capi.service.CommonService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.CalendarEventService;
import capi.service.masterMaintenance.ActivityCodeService;
import capi.service.userAccountManagement.ActingService;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
* UF-1303 Staff Calendar Maintenance
*/
@Secured("UF1303")
@FuncCode("UF1303")
@Controller("StaffCalendarController")
@RequestMapping("assignmentAllocationAndReallocation/StaffCalendar")
@SessionAttributes({"staffCalendarSession"})
public class StaffCalendarController {
	private static final Logger logger = LoggerFactory.getLogger(StaffCalendarController.class);
	
	@Resource(name="messageSource")
	MessageSource messageSource;

	@Autowired
	private UserService userService;
	@Autowired
	private ActivityCodeService activityService;
	@Autowired
	private CalendarEventService calendarEventService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private ActingService actingService;

	/**
	 * Init model attribute to session
	 */
	@ModelAttribute("staffCalendarSession")
    public StaffCalendarSession initStaffCalendarSession() {
        return new StaffCalendarSession();
    }
	
	/**
	 * calendar home
	 */
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public void home(Model model,
			@RequestParam(value = "clearSession", required = false) Integer clearSession,
			Locale locale,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession,
			SessionStatus sessionStatus) {
		try {
			if(clearSession != null && clearSession == 1){
				sessionStatus.setComplete();
				staffCalendarSession = new StaffCalendarSession();
			}
			
			Date fromDate = new Date();
			if(staffCalendarSession.getYear() != null && staffCalendarSession.getMonth() != null){
				String dateString = staffCalendarSession.getYear()+"-"+staffCalendarSession.getMonth()+"-"+"01";
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				fromDate = format.parse(dateString);
			}
			Date toDate = (Date) fromDate.clone();
			
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			fromDate = c.getTime();
			
			c.setTime(toDate);
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 999);
			toDate = c.getTime();

			// pre-select the officer with same team when first time enter the staff calendar
			if(staffCalendarSession.isFirstTime()) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//				User user = userService.getUserById(detail.getUserId());
//				String team = user.getTeam();
//				List<String> teammateIds = userService.getUserIdsWithSameTeam(team);
//				staffCalendarSession.setFilterOfficers(teammateIds. toArray(new String[0]));
				HashSet<String> userIds = new HashSet<String>();
				
				/*
				 * TIR147 
				 * Description: After using the Acting Function,
				 * information of more field officers should be filtered
				 * according to the acting setting.
				 */
				
				List<ActingModel> actingModels = actingService.getActingDetailsByReplacementUserId(detail.getUserId());
				
				int grantAuthorityLevel = 0;
				
				if (actingModels != null && !actingModels.isEmpty()) {
					List<String> teams = new ArrayList<String>();
					
					
					for (ActingModel item : actingModels) {
						if ((item.getGrantAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD) == 2) {
							grantAuthorityLevel |= SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD;
							break;
						} else if ((item.getGrantAuthorityLevel()
								& SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR) == 4) {
							grantAuthorityLevel |= SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR;

							if (StringUtils.isNotBlank(item.getActedTeam())) {
								teams.add(item.getActedTeam());
							}
						}
					}
					if ((grantAuthorityLevel & SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD) == 2) {
						List<Integer> ids = userService.getActiveUserIdsWithAuthorityLevel(
								SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, null, null);
						for (Integer id : ids) {
							userIds.add(String.valueOf(id));
						}
					} else if ((grantAuthorityLevel & SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR) == 4) {
						if (teams != null && !teams.isEmpty()) {
							List<Integer> teammateIds = userService
									.getUserIdsWithSameTeams(new ArrayList<String>(teams));
							for (Integer id : teammateIds) {
								userIds.add(String.valueOf(id));
							}
						}
					} 
				}

				if ((grantAuthorityLevel & SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD) != 2) {
					if ((detail.getOrgAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS) == 16) {
						userIds.add(String.valueOf(detail.getUserId()));
					}
					if ((detail.getOrgAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_FIELD_TEAM_HEAD) == 2) {
						List<Integer> ids = userService.getActiveUserIdsWithAuthorityLevel(
								SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, null, null);
						for (Integer id : ids) {
							userIds.add(String.valueOf(id));
						}
					} else if ((detail.getOrgAuthorityLevel() & SystemConstant.AUTHORITY_LEVEL_FIELD_SUPERVISOR) == 4) {
						User user = userService.getUserById(detail.getUserId());

						String team = user.getTeam();
						List<Integer> teammateIds = userService.getUserIdsWithSameTeam(team);
						for (Integer id : teammateIds) {
							userIds.add(String.valueOf(id));
						}
					} 
				}
				staffCalendarSession.setFilterOfficers(userIds.toArray(new String[0]));
				staffCalendarSession.setFirstTime(false);
			}
			
//			List<User> userList = new ArrayList<User>();
			List<UserLookupTableList> userList = new ArrayList<UserLookupTableList>();
			if(staffCalendarSession.getShowSelf() != null && staffCalendarSession.getShowSelf() == 1){
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
//				User user = userService.getUserById(detail.getUserId());
				UserLookupTableList user = userService.getUserByIdForStaffCalendar(detail.getUserId());
				userList.add(user);
			}else if(staffCalendarSession.getFilterOfficers() != null && staffCalendarSession.getFilterOfficers().length > 0){
				Integer[] intarray=new Integer[staffCalendarSession.getFilterOfficers().length];
				for(int i = 0; i < staffCalendarSession.getFilterOfficers().length; i++){
					intarray[i] = Integer.parseInt(staffCalendarSession.getFilterOfficers()[i]);
				}
//				userList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, intarray);
				userList = userService.getActiveUsersWithAuthorityLevelForStaffCalendar(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, intarray);
			}
//			else{
////				userList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
//				userList = userService.getActiveUsersWithAuthorityLevelForStaffCalendar(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
//			}
			
			List<User> userFilterList = new ArrayList<User>();
			userFilterList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
			
			CalendarDisplayModel displayModel = calendarEventService.gatherDisplayModelForStaffCalendar(fromDate, toDate, userList);
			
			if(staffCalendarSession.getShowSelf() != null){
				displayModel.setShowSelf(staffCalendarSession.getShowSelf());
			}
			if(staffCalendarSession.getShowEdit() != null){
				displayModel.setShowEdit(staffCalendarSession.getShowEdit());
			}
			if(staffCalendarSession.getHideHoliday() != null){
				displayModel.setHideHoliday(staffCalendarSession.getHideHoliday());
			}
			if(staffCalendarSession.getFilterOfficers() != null){
				displayModel.setFilterStaffIds(StringUtils.join(staffCalendarSession.getFilterOfficers(), ","));
			}
			
			Double noOfWorkingDayInMonth = calendarEventService.getNoOfWorkingDay(fromDate);
			
			model.addAttribute("displayModel", displayModel);
			model.addAttribute("userFilterList", userFilterList);
			model.addAttribute("noOfWorkingDayInMonth", noOfWorkingDayInMonth);
			
		} catch (Exception e) {
			logger.error("home", e);
		}
	}
	
	/**
	 * showEdit
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)")
	@RequestMapping(value = "showEdit", method = RequestMethod.GET)
	public String showEdit(Model model,
			@RequestParam(value = "showEdit", required = true) Integer showEdit,
			Locale locale, RedirectAttributes redirectAttributes,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession){
		
		staffCalendarSession.setShowEdit(showEdit);
		
		return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
	}
	
	/**
	 * showSelf
	 */
	@RequestMapping(value = "showSelf", method = RequestMethod.GET)
	public String showSelf(Model model,
			@RequestParam(value = "showSelf", required = true) Integer showSelf,
			Locale locale, RedirectAttributes redirectAttributes,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession){
		
		staffCalendarSession.setShowSelf(showSelf);
		
		return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
	}
	
	/**
	 * navigate calendar
	 */
	@RequestMapping(value = "nevigateCalendar", method = RequestMethod.GET)
	public String nevigateCalendar(Model model,
			@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "month", required = true) Integer month,
			Locale locale, RedirectAttributes redirectAttributes,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession){
		
		staffCalendarSession.setYear(year);
		staffCalendarSession.setMonth(month);
		
		return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
	}
	
	/**
	 * navigate calendar current
	 */
	@RequestMapping(value = "nevigateCalendarCurrent", method = RequestMethod.GET)
	public String nevigateCalendarCurrent(Model model,
			Locale locale, RedirectAttributes redirectAttributes,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession){
		

		staffCalendarSession.setYear(null);
		staffCalendarSession.setMonth(null);
		return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
	}
	
	/**
	 * navigate calendar current
	 */
	@RequestMapping(value = "filter", method = RequestMethod.POST)
	public String filter(Model model,
			Locale locale, RedirectAttributes redirectAttributes,
			@RequestParam(value = "officers", required = true) String officersStr,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession){
		
		staffCalendarSession.setFirstTime(false);
		String[] officers = new String[0];
		if(officersStr.length() > 0){
			officers = officersStr.split(",");
			staffCalendarSession.setFilterOfficers(officers);
		}else{
			staffCalendarSession.setFilterOfficers(new String[0]);
		}
		
		return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
	}
	
	/**
	 * batch update staff calendar table
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)")
	@RequestMapping(value = "batchUpdate", method = RequestMethod.POST)
	public String batchUpdate(Model model,
			@RequestParam(value = "activityType", required = true) Integer activityTypeId,
			@RequestParam(value = "date", required = true) String requestDateStr,
			@RequestParam(value = "sessions", required = true) String[] sessions,
			@RequestParam(value = "officers", required = true) String officersStr,
			Locale locale, RedirectAttributes redirectAttributes,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession){
			try {
				String[] dates = new String[0];
				dates = requestDateStr.split(",");
				
				String[] officers = new String[0];
				officers = officersStr.split(",");
				
				for (String dateStr : dates) {
				
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					Date date = dateFormat.parse(dateStr);
					
					//ActivityCode ac = activityService.getActivityCodeById(activityTypeId);
					for(String session: sessions){
						for(String oIdStr: officers){
							Integer oId = Integer.parseInt(oIdStr);
							CalendarEventModel entityModel = new CalendarEventModel();
							
							entityModel.setActivityCodeId(activityTypeId+"");
							entityModel.setActivityType(SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS);
							entityModel.setSession(session);
							entityModel.setEventDate(dateStr);
							entityModel.setUserId(oId);
							
							try{
								calendarEventService.save(entityModel, false, true);
							}catch(ConstraintViolationException cve){
								continue;
							}
						}
					}
					redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
					
					SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
					SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
					staffCalendarSession.setYear(Integer.parseInt(yearFormat.format(date)));
					staffCalendarSession.setMonth(Integer.parseInt(monthFormat.format(date)));
				}
			} catch (Exception e) {
				logger.error("home", e);
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
			}
			
			return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
	}
	
	/**
	 * quick add staff calendar event
	 * @return 
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)")
	@RequestMapping(value = "selectEvent", method = RequestMethod.POST)
	public @ResponseBody int selectEvent(Model model,
			@RequestParam(value = "activityType", required = true) Integer activityTypeId,
			@RequestParam(value = "date", required = true) String dateStr,
			@RequestParam(value = "session", required = true) String session,
			@RequestParam(value = "officer", required = true) Integer officer,
			@RequestParam(value = "showSelf", required = false) Integer showSelf,
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "month", required = false) Integer month,
			Locale locale, RedirectAttributes redirectAttributes){
			
			try {
				CalendarEventModel entityModel = new CalendarEventModel();
				
				entityModel.setActivityCodeId(activityTypeId+"");
				entityModel.setActivityType(SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OTHERS);
				entityModel.setSession(session);
				entityModel.setEventDate(dateStr);
				entityModel.setUserId(officer);
				int id = calendarEventService.save(entityModel, false, true);
				
				
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
				
				return id;
			} catch (Exception e) {
				logger.error("home", e);
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
				return -1;
			}
			/*
			if(year != null && month != null){
				return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home?year="+year+"&month="+month+"&showSelf="+((showSelf != null && showSelf == 1 )? "1" : "0");
			}else{
				return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home?showSelf="+((showSelf != null && showSelf == 1 )? "1" : "0");
			}*/
			
			
	}
	
	/**
	 * delete staff calendar event
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)")
	@RequestMapping(value = "deleteEvent")
	public String deleteEvent(Model model,
			@RequestParam(value = "eventId", required = true) Integer eventId,
			Locale locale, RedirectAttributes redirectAttributes){
			
			try {
				CalendarEvent ce = calendarEventService.findById(eventId);
				if(ce != null){
					calendarEventService.deleteEvent(ce);
				}else{
					//throw( new Exception());
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00011", null, locale));
					//return false;
				}
				
				redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00002", null, locale));
			} catch (Exception e) {
				logger.error("home", e);
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00013", null, locale));
				//return false;
			}
			
			return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
			//return true;
	}
	
	/**
	 * delete staff calendar event
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)")
	@RequestMapping(value = "deleteEventOnHome")
	public @ResponseBody boolean deleteEventOnHome(Model model,
			@RequestParam(value = "eventId", required = true) Integer eventId,
			Locale locale, RedirectAttributes redirectAttributes){
		
		try {
			CalendarEvent ce = calendarEventService.findById(eventId);
			if(ce != null){
				calendarEventService.deleteEvent(ce);
			}else{
				return false;
			}
		} catch (Exception e) {
			logger.error("home", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Edit or Create Calendar Event
	 */
	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String edit(
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			CalendarEvent item = null;
			User user = null;
			if (id != null) {
				item = calendarEventService.findById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/masterMaintenance/SubPriceFieldMaintenance/home";
				}
				user = item.getUser();
			} else {
				item = new CalendarEvent();
			}
			
			//CalendarEventModel itemModel = new CalendarEventModel();
			//itemModel.setActivityCodeId(item.getActivityCode().getId());
			//itemModel.setActivityType(item.getActivityType());
			//itemModel.setCalendarEventId(item.getId());
			//itemModel.setDuration(item.getDuration());
			
			model.addAttribute("model", item);
			model.addAttribute("modelUser", user);
			
			List<ActivityCode> activityList = activityService.getActivityCodes();
			model.addAttribute("activityList", activityList);
			
			List<User> userList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
			model.addAttribute("userList", userList);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
	
	/**
	 * Save Sub Price Field
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(
			@ModelAttribute CalendarEventModel item,
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "subPriceFieldId[]", required = false) Integer[] subPriceFieldId,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession) {
		try {

			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date = dateFormat.parse(item.getEventDate());
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
			staffCalendarSession.setYear(Integer.parseInt(yearFormat.format(date)));
			staffCalendarSession.setMonth(Integer.parseInt(monthFormat.format(date)));
			
			if (item.getCalendarEventId() != null) {
				
				if(item.getCalendarEventId() == 0){
					calendarEventService.save(item, false, true);
				}else{
					calendarEventService.save(item, true, true);
				}
				
				redirectAttributes.addFlashAttribute(
						SystemConstant.SUCCESS_MESSAGE,
						messageSource.getMessage("I00001", null, locale));
			}else{
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
			}
			
		}catch(ConstraintViolationException cve){
			logger.error("home", cve);
			if(cve.getMessage().contains("E00116")){
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00116", null, locale));
			}
			if(cve.getMessage().contains("E00117")){
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00117", null, locale));
			}
			if(cve.getMessage().contains("E00012")){
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
						messageSource.getMessage("E00012", null, locale));
			}
		} catch (Exception e) {
			logger.error("home", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
					messageSource.getMessage("E00012", null, locale));
		}
		
		return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
	}
	
	/**
	 * Save Sub Price Field
	 */
	@PreAuthorize("hasPermission(#user, 256) or hasPermission(#user, 4) or hasPermission(#user, 8)")
	@RequestMapping(value = "syncPublicHoliday", method = RequestMethod.GET)
	public String syncPublicHoliday(
			Model model,
			Locale locale,
			RedirectAttributes redirectAttributes) {
		try {    		
			calendarEventService.syncPublicCalendar();
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));		
 		} catch (Exception e) {
 			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00012", null, locale));
 		} 
		return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
		
	}
	
	/**
	 * Save Sub Price Field
	 */
	//@PreAuthorize("hasPermission(#user, 2) or hasPermission(#user, 4) or hasPermission(#user, 8)")
	@RequestMapping(value = "getStaffsName", method = RequestMethod.POST)
	public @ResponseBody StaffNameModel[] getStaffsName(
			Model model,
			Locale locale,
			@RequestParam(value = "ids[]", required = false) String[] strIds,
			RedirectAttributes redirectAttributes) {
		if(strIds != null){
			List<StaffNameModel> nameList = new ArrayList<StaffNameModel>();
			Integer[] ids = new Integer[strIds.length];
			for(int i = 0;i < strIds.length;i++)
			{
				ids[i] = Integer.parseInt(strIds[i]);
			}
			try {    		
				nameList = calendarEventService.getSelectedStaffName(new ArrayList<Integer>(Arrays.asList(ids)));
	 		} catch (Exception e) {
	 		}
			
			return nameList.toArray(new StaffNameModel[nameList.size()]);
		}
		return new StaffNameModel[0];
	}
	
	/**
	 * View Calendar Event
	 */
	@RequestMapping(value = "editViewOnly", method = RequestMethod.GET)
	public String editViewOnly(
			@RequestParam(value = "id", required = false) Integer id,
			Model model, Locale locale, RedirectAttributes redirectAttributes) {
		try {
			CalendarEvent item = null;
			User user = null;
			if (id != null) {
				item = calendarEventService.findById(id);
				if (item == null) {
					redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE,
							messageSource.getMessage("E00011", null, locale));
					return "redirect:/assignmentAllocationAndReallocation/StaffCalendar/home";
				}
				user = item.getUser();
			} else {
				item = new CalendarEvent();
			}
			
			model.addAttribute("model", item);
			model.addAttribute("modelUser", user);
			
			List<ActivityCode> activityList = activityService.getActivityCodes();
			model.addAttribute("activityList", activityList);
			
			List<User> userList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS);
			model.addAttribute("userList", userList);
			
		} catch (Exception e) {
			logger.error("edit", e);
		}
		return null;
	}
}
