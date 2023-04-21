package capi.dal;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import capi.entity.ActivityCode;
import capi.entity.CalendarEvent;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.commonLookup.UserLookupTableList;
import capi.model.report.CalendarSummaryReport;
import capi.model.report.SummaryOfTimeoffOvertimeReportAccumulated;

@Repository("CalendarEventDao")
public class CalendarEventDao extends GenericDao<CalendarEvent> {
	public CalendarEvent findOrCreate(User user, Integer activityType, Date eventDate){
		CalendarEvent ce;
		
		Criteria criteria = this.createCriteria("ce").add(Restrictions.eq("user", user)).add(Restrictions.eq("activityType", activityType))
				.add(Restrictions.eq("eventDate", eventDate));
		
		ce = (CalendarEvent) criteria.uniqueResult();
		if(ce == null){
			ce = new CalendarEvent();
		}
		return ce;
	}
	
	public CalendarEvent findOrCreate(User user, ActivityCode activityCode, String session, Date eventDate){
		CalendarEvent ce;
		
		Criteria criteria = this.createCriteria("ce").add(Restrictions.eq("user", user)).add(Restrictions.eq("activityCode", activityCode))
				.add(Restrictions.eq("session", session)).add(Restrictions.eq("eventDate", eventDate)).add(Restrictions.eq("isPublicHoliday", false));
		
		ce = (CalendarEvent) criteria.uniqueResult();
		if(ce == null){
			ce = new CalendarEvent();
		}
		return ce;
	}
	
	public List<CalendarEvent> getCalendarEvents(List<User> users, Date fromDate, Date toDate){
		Criteria criteria = this.createCriteria("ce")
				.add(Restrictions.and(Restrictions.ge("eventDate", fromDate), Restrictions.le("eventDate", toDate)))
				.addOrder(Order.asc("eventDate")).add(Restrictions.eq("isPublicHoliday", false));
		
		if (users != null && users.size() > 0){
			criteria.add(Restrictions.in("user", users));
		}

		criteria.setFetchMode("user", FetchMode.JOIN);
		criteria.setFetchMode("activityType", FetchMode.JOIN);
		
		return criteria.list();
	}
	
	public List<CalendarEvent> getCalendarEventsForStaffCalendar(List<UserLookupTableList> users, Date fromDate, Date toDate){
		List<Integer> userIds = new ArrayList<Integer> ();
		if(users != null && users.size() > 0) {
			for(UserLookupTableList user : users) {
				userIds.add(user.getId());
			}
		}
		
		Criteria criteria = this.createCriteria("ce").createAlias("ce.user", "u", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.and(Restrictions.ge("eventDate", fromDate), Restrictions.le("eventDate", toDate)))
				.addOrder(Order.asc("eventDate")).add(Restrictions.eq("isPublicHoliday", false));
		
		if (userIds != null && userIds.size() > 0){
			criteria.add(Restrictions.in("u.userId", userIds));
		}

		criteria.setFetchMode("user", FetchMode.JOIN);
		criteria.setFetchMode("activityType", FetchMode.JOIN);
		
		return criteria.list();
	}
	
	public CalendarEvent createEvent(){
		return new CalendarEvent();
	}
	
	public CalendarEvent findHolidayExist(String uid){
		Criteria criteria = this.createCriteria("ce").add(Restrictions.and(Restrictions.eq("publicUid", uid), Restrictions.eq("isPublicHoliday", true)));
		return (CalendarEvent) criteria.uniqueResult();
	}
	
	public List<CalendarEvent> getCalendarHolidays(Date fromDate, Date toDate){
		Criteria criteria = this.createCriteria("ce").add(Restrictions.and(Restrictions.ge("eventDate", fromDate), Restrictions.le("eventDate", toDate)))
				.addOrder(Order.asc("eventDate")).add(Restrictions.eq("isPublicHoliday", true));
		return criteria.list();
	}

	public Date findPreviousWorkingDate(Date date) {

		String sql = "select dbo.getPreviousWorkingDate(:date);";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("date", date);
		
		return (Date)query.uniqueResult();
	}
	
	/**
	 * @param user
	 * @param date
	 * @return
	 * 
	 * return list of id fulfill.
	 */
	public List<Integer> findOT(User user, Date date){
		Criteria criteria = this.createCriteria("ce");
		criteria.setProjection(
				Projections.projectionList()
				.add(Projections.property("ce.calendarEventId"))
			);
		criteria.add(Restrictions.and(
				Restrictions.eq("user", user),
				Restrictions.eq("eventDate", date),
				Restrictions.eq("activityType", SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_OT)
			));
		
		return criteria.list();
	}
	
	/**
	 * @param user
	 * @param date
	 * @return
	 * 
	 * return list of id fulfill.
	 */
	public List<Integer> findTimeOff(User user, Date date){
		Criteria criteria = this.createCriteria("ce");
		criteria.setProjection(
				Projections.projectionList()
				.add(Projections.property("ce.calendarEventId"))
			);
		criteria.add(Restrictions.and(
				Restrictions.eq("user", user),
				Restrictions.eq("eventDate", date),
				Restrictions.eq("activityType", SystemConstant.STAFF_CALENDAR_ACTIVITY_TYPE_TIMEOFF)
			));
		
		return criteria.list();
	}
	
	public Double getNoOfWorkingDay(Date month){
		
		DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		String fromDate = format.format(month);
		
		Query query = this.getSession().createSQLQuery(
				"execute [GetNoOfWorkingDayInMonth] :month")
				.setParameter("month", fromDate);
		return ((BigDecimal) query.uniqueResult()).doubleValue();
	}
	
	
	public List<Date> getPreviousNWorkdingDate(Date date, int noOfDays){
		SQLQuery query = this.getSession().createSQLQuery("exec [GetPreviousNWorkingDate] :date,:noOfDays");
		query.addScalar("workingDate", StandardBasicTypes.DATE);
		query.setParameter("date", date);
		query.setParameter("noOfDays", noOfDays);
		return query.list();
	}
		
	public List<Date> getNextNWorkdingDate(Date date, int noOfDays){
		SQLQuery query = this.getSession().createSQLQuery("exec [GetNextNWorkingDate] :date,:noOfDays");
		query.addScalar("workingDate", StandardBasicTypes.DATE);
		query.setParameter("date", date);
		query.setParameter("noOfDays", noOfDays);
		return query.list();
	}
	
	public List<CalendarEvent> getEventsInMonth(Date month){
		Criteria criteria = this.createCriteria("ce");
		criteria.add(Restrictions.and(
				Restrictions.sqlRestriction("cast(eventDate as date) >= ?", month, StandardBasicTypes.DATE),
				Restrictions.sqlRestriction("cast(eventDate as date) < dateadd(month, 1, ?) ", month, StandardBasicTypes.DATE)
			));
		
		return criteria.list();
	}
	
	public long countEventsInMonth(Date month){
		Criteria criteria = this.createCriteria("ce");
		criteria.add(Restrictions.and(
				Restrictions.sqlRestriction("cast(eventDate as date) >= ?", month, StandardBasicTypes.DATE),
				Restrictions.sqlRestriction("cast(eventDate as date) < dateadd(month, 1, ?) ", month, StandardBasicTypes.DATE)
			));
		
		return (long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	
	public CalendarEvent getOTEvent(Integer userId, Date date){
		Criteria criteria = this.createCriteria("ce");
		criteria.createAlias("ce.user", "u");
		criteria.add(Restrictions.eq("u.userId", userId));
		criteria.add(Restrictions.eq("ce.eventDate", date));
		criteria.add(Restrictions.eq("activityType", 1));
		
		return (CalendarEvent)criteria.uniqueResult();
		
	}
	
	
	public List<String> getOTDates(Integer userId, Date minDate){
		String date = String.format("FORMAT({alias}.eventDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		Criteria criteria = this.createCriteria("ce");
		criteria.createAlias("ce.user", "u");
		criteria.add(Restrictions.eq("u.userId", userId));
		criteria.add(Restrictions.eq("ce.activityType", 1));
		ProjectionList list = Projections.projectionList();
		list.add(Projections.sqlProjection(date+" as date ", new String[]{"date"}, new Type[]{StandardBasicTypes.STRING}), "" );
		criteria.setProjection(list);
		
		if (minDate != null){
			criteria.add(Restrictions.ge("ce.eventDate", minDate));
		}
		
		return criteria.list();		
	}
	
	
	public List<String> getTimeOffDates(Integer userId, Date minDate){
		String date = String.format("FORMAT({alias}.eventDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		Criteria criteria = this.createCriteria("ce");
		criteria.createAlias("ce.user", "u");
		criteria.add(Restrictions.eq("u.userId", userId));
		criteria.add(Restrictions.eq("ce.activityType", 2));
		ProjectionList list = Projections.projectionList();
		list.add(Projections.sqlProjection(date+" as date ", new String[]{"date"}, new Type[]{StandardBasicTypes.STRING}), "" );
		criteria.setProjection(list);
		
		if (minDate != null){
			criteria.add(Restrictions.ge("ce.eventDate", minDate));
		}
		
		return criteria.list();		
	}
	
	public List<CalendarEvent> getTimeOffEvents(Integer userId, Date date){
		Criteria criteria = this.createCriteria("ce");
		criteria.createAlias("ce.user", "u");
		criteria.add(Restrictions.eq("u.userId", userId));
		criteria.add(Restrictions.eq("ce.eventDate", date));
		criteria.add(Restrictions.eq("activityType", 2));
		
		return criteria.list();
		
	}
	
	public Double getUserAvaliableManDayBetween(Date startDate, Date endDate, int userId){
		Query query = this.getSession().createSQLQuery(
				"execute [GetUserAvaliableManDayBetween] :startDate, :endDate, :userId")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.setParameter("userId", userId);
		return ((BigDecimal) query.uniqueResult()).doubleValue();
	}
	
	public List<Date> getNonWorkingDate(Date startDate, Date endDate){
		Query query = this.getSession().createSQLQuery(
				"execute [GetNonWorkingDates] :startDate, :endDate")
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		return query.list();
	}
	
	public List<CalendarEvent>  getTimeoffOvertimeEvents(Date startDate, Date endDate, Integer[] officerIds){
		return getTimeoffOvertimeEvents(startDate, endDate, officerIds, null);
	}
	
	public SummaryOfTimeoffOvertimeReportAccumulated getAccumulatedTime(Integer userId, Date startDate) {

		String hql = " select u.userId as userId, "
				+ " sum( case ce.activityType when 1 then DATEDIFF(MINUTE, '0:00:00', ce.duration) else 0 end ) as accumulatedOT, "
				+ " sum( case ce.activityType when 2 then DATEDIFF(MINUTE, '0:00:00', ce.duration) else 0 end ) as accumulatedTF "
				+ " from CalendarEvent as ce "
				+ " inner join ce.user as u "
				+ " where ce.eventDate >= :date and u.userId = :userId "
				+ " group by u.userId ";

		Query query = this.getSession().createQuery(hql);

		query.setParameter("date", startDate);
		query.setParameter("userId", userId);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfTimeoffOvertimeReportAccumulated.class));

		return (SummaryOfTimeoffOvertimeReportAccumulated)query.uniqueResult();
	}
	
	public List<CalendarEvent>  getTimeoffOvertimeEvents(Date startDate, Date endDate, Integer[] officerIds, String[] teams){
		Criteria criteria = this.createCriteria("ce");
		criteria.createAlias("ce.user", "u");
		if (officerIds != null ) {
			criteria.add(Restrictions.in("u.userId", officerIds));
		}
		if (teams != null ) {
			criteria.add(Restrictions.in("u.team", teams));
		}
		criteria.add(Restrictions.ge("ce.eventDate", startDate));
		criteria.add(Restrictions.le("ce.eventDate", endDate));
		criteria.add(Restrictions.or(
				Restrictions.eq("activityType", 1),
				Restrictions.eq("activityType", 2)
		));
		criteria.addOrder(Order.asc("u.team"));
		criteria.addOrder(Order.asc("u.rank"));
		criteria.addOrder(Order.asc("u.staffCode"));
		criteria.addOrder(Order.asc("eventDate"));
		
		return criteria.list();
	}
	
	public List<CalendarEvent>  getTimeoffOvertimeEventsByUserId(Date timeLogDate, Integer officerId){
		Criteria criteria = this.createCriteria("ce");
		criteria.createAlias("ce.user", "u");
		if (officerId != null ) {
			criteria.add(Restrictions.eq("u.userId", officerId));
		}
		criteria.add(Restrictions.eq("ce.eventDate", timeLogDate));
	
		criteria.add(Restrictions.or(
				Restrictions.eq("activityType", 1),
				Restrictions.eq("activityType", 2)
		));
		criteria.addOrder(Order.asc("eventDate"));
		return criteria.list();
	}
	
	public List<CalendarSummaryReport> getCalendarSummaryReport(Date refMonth, List<Integer> userIds){
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[getCalendarSummaryReport] :userIds, :refMonth");
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("<query>");
		for (Integer userId : userIds){
			builder.append("<userid>"+userId+"</userid>");
		}
		builder.append("</query>");
		
		query.setParameter("userIds", builder.toString());
		query.setParameter("refMonth", refMonth);
		
		query.addScalar("date", StandardBasicTypes.DATE);
		query.addScalar("staffCode", StandardBasicTypes.STRING);
		query.addScalar("staffName", StandardBasicTypes.STRING);
		query.addScalar("jobName", StandardBasicTypes.STRING);
		query.addScalar("session", StandardBasicTypes.INTEGER);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("rank", StandardBasicTypes.STRING);
		query.addScalar("isPublicHoliday", StandardBasicTypes.BOOLEAN);
		
		query.setResultTransformer(Transformers.aliasToBean(CalendarSummaryReport.class));
		return query.list();
	}
}