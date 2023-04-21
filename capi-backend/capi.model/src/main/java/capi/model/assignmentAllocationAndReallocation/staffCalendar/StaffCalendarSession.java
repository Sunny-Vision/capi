package capi.model.assignmentAllocationAndReallocation.staffCalendar;

import java.io.Serializable;

public class StaffCalendarSession implements Serializable{

	private Integer year;
	private Integer month;
	private Integer showEdit;
	private Integer showSelf;
	private Integer hideHoliday;
	private String[] filterOfficers;
	
	private Integer showCalendar;
	private boolean firstTime;
	
	public StaffCalendarSession(){
		this.filterOfficers = new String[0];
		this.showCalendar = 0;
		this.firstTime = true;
	}
	
	public String[] getFilterOfficers() {
		return filterOfficers;
	}
	public void setFilterOfficers(String[] filterOfficers) {
		this.filterOfficers = filterOfficers;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getShowEdit() {
		return showEdit;
	}
	public void setShowEdit(Integer showEdit) {
		this.showEdit = showEdit;
	}
	public Integer getShowSelf() {
		return showSelf;
	}
	public void setShowSelf(Integer showSelf) {
		this.showSelf = showSelf;
	}
	public Integer getHideHoliday() {
		return hideHoliday;
	}
	public void setHideHoliday(Integer hideHoliday) {
		this.hideHoliday = hideHoliday;
	}
	
	public Integer getShowCalendar() {
		return showCalendar;
	}
	public void setShowCalendar(Integer showCalendar) {
		this.showCalendar = showCalendar;
	}
	public boolean isFirstTime() {
		return firstTime;
	}
	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	
}
