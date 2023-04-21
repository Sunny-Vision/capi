package com.kinetix.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.component.FuncCode;

import capi.entity.User;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.NotificationListPreviewModel;
import capi.model.Select2RequestModel;
import capi.model.Select2ResponseModel;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.CalendarDisplayModel;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.StaffCalendarSession;
import capi.model.dashboard.ViewModel;
import capi.service.NotificationService;
import capi.service.UserService;
import capi.service.assignmentAllocationAndReallocation.CalendarEventService;
import capi.service.dashboard.DashboardService;


/**
 * Handles requests for the application home page.
 */
@FuncCode("RF1901")
@Controller("HomeController")
@SessionAttributes({"staffCalendarSession"})
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private NotificationService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CalendarEventService calendarEventService;
	
	/**
	 * Init model attribute to session
	 */
	@ModelAttribute("staffCalendarSession")
    public StaffCalendarSession initStaffCalendarSession() {
        return new StaffCalendarSession();
    }
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model,
			@RequestParam(value = "selectedUserId", required = false) Integer selectedUserId, Authentication auth,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession,
			@RequestParam(value = "showCalendar", required = false) Integer showCalendar) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			
			ViewModel viewModel = dashboardService.prepareViewModel(selectedUserId);
			
			if ((detail.getAuthorityLevel() & 4) == 4 || (detail.getAuthorityLevel() & 2) == 2)
				viewModel.setShowSelectStaff(true);
			
			model.addAttribute("model", viewModel);
			
			if(showCalendar != null && showCalendar == 1 || staffCalendarSession.getShowCalendar() == 1) {
				prepareStaffCalendar(model, staffCalendarSession, selectedUserId, showCalendar, auth);
				model.addAttribute("showCalendar", 1);
			} else {
				model.addAttribute("showCalendar", 0);
			}
			
			//prepareStaffCalendar(model, staffCalendarSession, selectedUserId);
		} catch (Exception e) {
			logger.error("home", e);
		}
		
		return "home";
	}
	
	/**
	 * Prepare staff calendar
	 */
	private void prepareStaffCalendar(Model model, StaffCalendarSession staffCalendarSession
			, Integer selectedUserId, Integer showCalendar, Authentication auth) throws Exception {
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

		List<User> userList = new ArrayList<User>();
		List<Integer> userIds = new ArrayList<Integer>();
		CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
		
		if (selectedUserId != null && selectedUserId > 0) {
			userIds.add(selectedUserId);
		} else if ((detail.getAuthorityLevel() & 2) == 2){
			userIds.addAll(userService.getActiveUserIdsWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, null, null));
		} else if ((detail.getAuthorityLevel() & 4) == 4){
			User user = userService.getUserById(detail.getUserId());
			String team = user.getTeam();
			userIds.addAll(userService.getUserIdsWithSameTeam(team));
		} else if ((detail.getAuthorityLevel() & 16) == 16){
			userIds.add(detail.getUserId());
		}
//		if (selectedUserId != null && selectedUserId > 0) {
//			userList.add(userService.getUserById(selectedUserId));
//		} else {
//			Set<Integer> userIds = userService.getLoginUserSubordinatesAndSelfWithActing();
//			if (userIds.size() > 0) {
//				userList = userService.getUsersByIds(new ArrayList<Integer>(userIds));
//			}
//		}
		if(userIds!=null && userIds.size()>0){
			userList = userService.getActiveUsersWithAuthorityLevel(SystemConstant.AUTHORITY_LEVEL_FIELD_OFFICERS, userIds.toArray(new Integer[0]));
		}
		
		CalendarDisplayModel displayModel = calendarEventService.gatherDisplayModel(fromDate, toDate, userList);
		
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
		model.addAttribute("noOfWorkingDayInMonth", noOfWorkingDayInMonth);
		
		model.addAttribute("showCalendar", showCalendar);
	}
	

	/**
	 * navigate calendar
	 */
	@RequestMapping(value = "nevigateCalendar", method = RequestMethod.GET)
	public String nevigateCalendar(Model model,
			@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "month", required = true) Integer month,
			Locale locale, RedirectAttributes redirectAttributes,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession,
			@RequestParam(value = "selectedUserId", required = false) Integer selectedUserId,
			@RequestParam(value = "showCalendar", required = false) Integer showCalendar){
		
		try {
			staffCalendarSession.setYear(year);
			staffCalendarSession.setMonth(month);
			staffCalendarSession.setShowCalendar(showCalendar);
			
			String urlParam = "";
			if (selectedUserId != null && selectedUserId > 0)
				urlParam = "?selectedUserId=" + selectedUserId;
			
			return "redirect:/" + urlParam + "#staffCalendar";
		} catch (Exception e) {
			logger.error("nevigateCalendar", e);
		}
		return "redirect:/";
	}
	
	/**
	 * navigate calendar current
	 */
	@RequestMapping(value = "nevigateCalendarCurrent", method = RequestMethod.GET)
	public String nevigateCalendarCurrent(Model model,
			Locale locale, RedirectAttributes redirectAttributes,
			@ModelAttribute("staffCalendarSession") StaffCalendarSession staffCalendarSession,
			@RequestParam(value = "selectedUserId", required = false) Integer selectedUserId,
			@RequestParam(value = "showCalendar", required = false) Integer showCalendar){
		
		try {
			staffCalendarSession.setYear(null);
			staffCalendarSession.setMonth(null);
			staffCalendarSession.setShowCalendar(showCalendar);
			
			String urlParam = "";
			if (selectedUserId != null && selectedUserId > 0)
				urlParam = "?selectedUserId=" + selectedUserId;
			
			return "redirect:/" + urlParam + "#staffCalendar";
		} catch (Exception e) {
			logger.error("nevigateCalendar", e);
		}
		return "redirect:/";
	}
	
	@RequestMapping(value = "/getNotification", method = RequestMethod.GET)
	public @ResponseBody NotificationListPreviewModel getNotification(Authentication auth){
		try{
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return service.getNotificationPreviewList(detail.getUserId());
		}
		catch(Exception ex){
			logger.error("get Notification error", ex);
		}
		return null;
	}

	/**
	 * Get subordinate select2 format
	 */
	@RequestMapping(value = "querySubordinateSelect2", method = RequestMethod.GET)
	public @ResponseBody Select2ResponseModel
		querySubordinateSelect2(Locale locale, Model model, Select2RequestModel requestModel, Authentication auth) {
		try {
			CapiWebAuthenticationDetails detail = (CapiWebAuthenticationDetails)auth.getDetails();
			return dashboardService.queryOfficerSelect2(requestModel, detail.getUserId());
		} catch (Exception e) {
			logger.error("queryOfficerSelect2", e);
		}
		return null;
	}
}
