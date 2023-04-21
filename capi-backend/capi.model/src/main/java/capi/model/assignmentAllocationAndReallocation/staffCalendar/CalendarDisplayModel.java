package capi.model.assignmentAllocationAndReallocation.staffCalendar;

import java.util.Date;
import java.util.List;

import capi.entity.ActivityCode;
import capi.entity.CalendarEvent;
import capi.entity.User;
import capi.model.commonLookup.UserLookupTableList;

public class CalendarDisplayModel {
	public Date fromDate;
	public List<Date> dateList;
	public Date previousMonth;
	public Date nextMonth;
	public Integer showSelf;
	public List<User> userList;
	public List<CalendarEvent> calendarEventList;
	public List<CalendarEvent> calendarHolidayList;
	public List<AssignmentDisplayModel> assignmentList;
	public List<ActivityCode> activityList;
	public Integer showEdit;
	public Integer hideHoliday;
	public String filterStaffIds;
	public String calendarEventColor;
	public String assignmentEventColor;
	public List<UserLookupTableList> userListStaffCalendar;

	public String getFilterStaffIds() {
		return filterStaffIds;
	}

	public void setFilterStaffIds(String filterStaffIds) {
		this.filterStaffIds = filterStaffIds;
	}

	public CalendarDisplayModel(){
		this.showSelf = 0;
		this.showEdit = 0;
		this.hideHoliday = 1;
	}
	
	public Integer getShowEdit() {
		return showEdit;
	}
	public void setShowEdit(Integer showEdit) {
		this.showEdit = showEdit;
	}
	public Integer getHideHoliday() {
		return hideHoliday;
	}
	public void setHideHoliday(Integer hideHoliday) {
		this.hideHoliday = hideHoliday;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public List<CalendarEvent> getCalendarHolidayList() {
		return calendarHolidayList;
	}
	public void setCalendarHolidayList(List<CalendarEvent> calendarHolidayList) {
		this.calendarHolidayList = calendarHolidayList;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public List<Date> getDateList() {
		return dateList;
	}
	public void setDateList(List<Date> dateList) {
		this.dateList = dateList;
	}
	public Date getPreviousMonth() {
		return previousMonth;
	}
	public void setPreviousMonth(Date previousMonth) {
		this.previousMonth = previousMonth;
	}
	public Date getNextMonth() {
		return nextMonth;
	}
	public void setNextMonth(Date nextMonth) {
		this.nextMonth = nextMonth;
	}
	public Integer getShowSelf() {
		return showSelf;
	}
	public void setShowSelf(Integer showSelf) {
		this.showSelf = showSelf;
	}
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	public List<CalendarEvent> getCalendarEventList() {
		return calendarEventList;
	}
	public void setCalendarEventList(List<CalendarEvent> calendarEventList) {
		this.calendarEventList = calendarEventList;
	}
	public List<AssignmentDisplayModel> getAssignmentList() {
		return assignmentList;
	}
	public void setAssignmentList(List<AssignmentDisplayModel> assignmentList) {
		this.assignmentList = assignmentList;
	}
	public List<ActivityCode> getActivityList() {
		return activityList;
	}
	public void setActivityList(List<ActivityCode> activityList) {
		this.activityList = activityList;
	}
	public String getCalendarEventColor() {
		return calendarEventColor;
	}

	public void setCalendarEventColor(String calendarEventColor) {
		this.calendarEventColor = calendarEventColor;
	}

	public String getAssignmentEventColor() {
		return assignmentEventColor;
	}

	public void setAssignmentEventColor(String assignmentEventColor) {
		this.assignmentEventColor = assignmentEventColor;
	}

	public List<UserLookupTableList> getUserListStaffCalendar() {
		return userListStaffCalendar;
	}

	public void setUserListStaffCalendar(
			List<UserLookupTableList> userListStaffCalendar) {
		this.userListStaffCalendar = userListStaffCalendar;
	}
}
