package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.TimeLog;
import capi.model.SystemConstant;
import capi.model.api.dataSync.TimeLogSyncData;
import capi.model.dataImportExport.ExportTimeLogMaintenanceList;
import capi.model.report.SummaryItineraryReport;
import capi.model.report.SummaryOfTimeLogEnumerationOutcomeReport;
import capi.model.report.SummaryOfTimelog;
import capi.model.timeLogManagement.ItineraryCheckingTableList;
import capi.model.timeLogManagement.TimeLogTableList;

@Repository("TimeLogDao")
public class TimeLogDao extends GenericDao<TimeLog> {
	
	@SuppressWarnings("unchecked")
	public List<TimeLogTableList> getTimeLogTableList(String search, int firstRecord, int displayLength, Order order,
			Integer[] userIds, Integer[] supervisorIds, Integer selfUserId, Integer ownerId) {

		String date = String.format("FORMAT(t.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = " select t.timeLogId as id, "
				+ " case when t.date is null then '' else "+date+" end as date, "
				+ " u.staffCode as officerCode, " 
				+ " u.chineseName as officerChineseName, "
				+ " t.status as status, "
				+ " a.staffCode as approvedByCode "
				+ " from TimeLog t "
				+ " left join t.user as u "
				+ " left join u.supervisor as sv "
				+ " left join t.approvedBy as a "
				+ " where 1=1 "
				+ (( userIds != null && userIds.length > 0 ) ? " and u.userId in (:userId) " : "") ;
		if (selfUserId != null && supervisorIds != null && supervisorIds.length > 0){
			hql += " and ( sv.userId in (:supervisorIds) and t.status not in ('Draft', 'Rejected') or u.userId = :selfUserId or u.userId = :ownerId) ";
		}
		else if (supervisorIds != null && supervisorIds.length > 0){
			hql += " and (sv.userId in (:supervisorIds) and t.status not in ('Draft', 'Rejected') or u.userId = :ownerId) ";
		}
		else if (selfUserId != null){
			hql += " and (u.userId = :selfUserId  or u.userId = :ownerId)";
		}
		else{
			hql += " and (t.status not in ('Draft', 'Rejected') or u.userId = :ownerId) ";
		}
		
		//		+ (( supervisorIds != null && supervisorIds.length > 0 ) ? " and sv.userId in (:supervisorIds) " : "");
		
		if (!StringUtils.isEmpty(search)){
			hql += " and  ( "+date+" like :search or u.staffCode like :search or u.chineseName like :search or t.status like :search ) ";
		}
		
		if ("status".equals(order.getPropertyName())){
			hql += " order by case when t.status = 'Rejected' then '1' else '2' end " + (order.isAscending()? " asc" : " desc" ) 
					+ "," + (order.getPropertyName()) + (order.isAscending()? " asc" : " desc" ) ;
		} else {
			hql += " order by case when t.status = 'Rejected' then '1' else '2' end, " + 
					(order.getPropertyName()) + (order.isAscending()? " asc" : " desc" ) ;
		}

		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		if (selfUserId != null){
			query.setParameter("selfUserId",selfUserId);
		}
		if(userIds != null && userIds.length > 0) query.setParameterList("userId", userIds);
		if(ownerId != null) query.setParameter("ownerId", ownerId);
		if(supervisorIds != null && supervisorIds.length > 0) query.setParameterList("supervisorIds", supervisorIds);
				
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));

		query.setResultTransformer(Transformers.aliasToBean(TimeLogTableList.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItineraryCheckingTableList> getItineraryCheckingTableList(Integer[] supervisorIds,
			Double noOfAssignmentDeviationLimit, Double sequenceDeviationLimit, Integer tpuDeviationLimit,
			String search, int firstRecord, int displayLength, Order order) {

		String date = String.format("FORMAT(t.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String AssignmentDeviation = "";
		if(noOfAssignmentDeviationLimit!=null){
			AssignmentDeviation += ", case when t.assignmentDeviation is not null then "
					+ " case when t.assignmentDeviation > "+noOfAssignmentDeviationLimit+" then true "
							+ " else false end "
					+ " else false end as isAssignmentDeviationRemark ";
		}
		
		String sequenceDeviation = "";
		if(sequenceDeviationLimit!=null){
			sequenceDeviation += ", case when t.sequenceDeviation is not null then "
					+ " case when t.sequenceDeviation > "+sequenceDeviationLimit+" then true "
							+ " else false end "
					+ " else false end as isSequenceDeviationRemark ";
		}
		
		String tpuDeviation = "";
		if(tpuDeviationLimit!=null){
			tpuDeviation += ", case when t.tpuDeviation is not null then "
					+ " case when t.tpuDeviation > "+tpuDeviationLimit+" then true "
							+ " else false end "
					+ " else false end as isTpuDeviationRemark ";
		}
		
		String hql = " select t.timeLogId as id, "
				+ " case when t.date is null then '' else "+date+" end as date, "
				+ " u.staffCode as officerCode, " 
				+ " u.chineseName as officerChineseName, "
				+ " t.assignmentDeviation as assignmentDeviation, "
				+ " t.sequenceDeviation as sequenceDeviation, "
				+ " t.tpuDeviation as tpuDeviation, "
				+ " t.itineraryCheckRemark as itineraryCheckRemark "
				+ AssignmentDeviation
				+ sequenceDeviation
				+ tpuDeviation
				+ " from TimeLog t "
				+ " left join t.user as u "
				+ " left join u.supervisor as sv "
				+ " where t.status = :status ";
				
		if (supervisorIds != null && supervisorIds.length > 0){
			hql += " and sv.userId in (:supervisorIds)";
		}				
		
		if (!StringUtils.isEmpty(search)){
			hql += " and  ( "+date+" like :search or u.staffCode like :search or u.chineseName like :search or t.status like :search or t.itineraryCheckRemark like :search ) ";
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
	
		query.setParameter("status", SystemConstant.TIMELOG_STATUS_VOILATED );
		if (supervisorIds != null && supervisorIds.length > 0){
			query.setParameterList("supervisorIds", supervisorIds );
		}
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		query.setResultTransformer(Transformers.aliasToBean(ItineraryCheckingTableList.class));

		return query.list();
	}
	
	public Long countTimeLogTableList(String search, Integer[] userIds, Integer[] supervisorIds, Integer selfUserId, Integer ownerId) {

		String date = String.format("FORMAT(t.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = " select count(distinct t.timeLogId) "
				+ " from TimeLog t "
				+ " left join t.user as u "
				+ " left join u.supervisor as sv "
				+ " where 1=1 "
				+ (( userIds != null && userIds.length > 0 ) ? " and u.userId in (:userId) " : "");
				//+ (( supervisorIds != null && supervisorIds.length > 0 ) ? " and sv.userId in (:supervisorIds) " : "");

		if (selfUserId != null && supervisorIds != null && supervisorIds.length > 0){
			hql += " and ( sv.userId in (:supervisorIds) and t.status not in ('Draft', 'Rejected') or u.userId = :selfUserId or u.userId = :ownerId) ";
		}
		else if (supervisorIds != null && supervisorIds.length > 0){
			hql += " and (sv.userId in (:supervisorIds) and t.status not in ('Draft', 'Rejected')  or u.userId = :ownerId) ";
		}
		else if (selfUserId != null){
			hql += " and (u.userId = :selfUserId  or u.userId = :ownerId)";
		}
		else{
			hql += " and (t.status not in ('Draft', 'Rejected') or u.userId = :ownerId) ";
		}
		
		if (!StringUtils.isEmpty(search)){
			hql += " and ( "+date+" like :search or u.staffCode like :search or u.chineseName like :search or t.status like :search ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
				
		if (selfUserId != null){
			query.setParameter("selfUserId",selfUserId);
		}
		
		
		if(userIds != null && userIds.length > 0) query.setParameterList("userId", userIds);

		if(supervisorIds != null && supervisorIds.length > 0) query.setParameterList("supervisorIds", supervisorIds);
		
		if(ownerId != null) query.setParameter("ownerId", ownerId);
				
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));

		return (Long) query.uniqueResult();
	}

	public Long countItineraryCheckingTableList(Integer[] supervisorIds, String search) {

		String date = String.format("FORMAT(t.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = " select count(distinct t.timeLogId) "
				+ " from TimeLog t "
				+ " left join t.user as u "
				+ " left join u.supervisor as sv "
				+ " where t.status = :status ";
		
		if (supervisorIds != null && supervisorIds.length > 0){
			hql += " and sv.userId in (:supervisorIds)";
		}
		
		if (!StringUtils.isEmpty(search)){
			hql += " and  ( "+date+" like :search or u.staffCode like :search or u.chineseName like :search or t.status like :search or t.itineraryCheckRemark like :search ) ";
		}
			
		Query query = this.getSession().createQuery(hql);
	
		query.setParameter("status", SystemConstant.TIMELOG_STATUS_VOILATED);
		if (supervisorIds != null && supervisorIds.length > 0){
			query.setParameterList("supervisorIds", supervisorIds );
		}		
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));

		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<SummaryItineraryReport> getSummaryItineraryReport(Date start, Date end, List<Integer> userIds
			, Double noOfAssignmentDeviationLimit, Double sequenceDeviationLimit, Integer tpuDeviationLimit){
		String sql = "Select u.Team as team "
				+ " , r.Name as [rank] "
				+ " , u.StaffCode as staffCode "
				+ " , u.EnglishName as staffName "
				+ " , Day(t.[Date]) as [date] "
				+ " , u.UserId as userId "
				+ " , 1 as countTimeLog "
				+ " From TimeLog as t "
				+ " left join [User] as u on t.UserId = u.UserId "
				+ " left join [Rank] as r on u.RankId = r.RankId "
				+ " where t.[Date] between :start and :end ";
		
		if (userIds != null && userIds.size()>0) {
			sql += " and u.userId in ( :userIds )";
		}
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("( 1<>1 ");
		if (noOfAssignmentDeviationLimit != null) {
			builder.append("or t.AssignmentDeviation > :assignmentDeviation ");
		}
		if (sequenceDeviationLimit != null) {
			builder.append("or t.SequenceDeviation > :sequenceDeviation ");
		}
		if (tpuDeviationLimit != null) {
			builder.append("or t.TpuDeviation > :tpuDeviation "); 
		}
		builder.append(" )");
		
		sql += " and "+builder.toString();
		sql += " group by u.Team, r.Name, u.StaffCode, u.EnglishName, t.[Date], u.UserId ";
		sql += " order by team, [rank], staffCode, [date]";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("start", start);
		query.setParameter("end", end);
		
		if (userIds != null && userIds.size()>0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (noOfAssignmentDeviationLimit != null) {
			query.setParameter("assignmentDeviation", noOfAssignmentDeviationLimit);
		}
		if (sequenceDeviationLimit != null) {
			query.setParameter("sequenceDeviation", sequenceDeviationLimit);
		}
		if (tpuDeviationLimit != null) {
			query.setParameter("tpuDeviation", tpuDeviationLimit); 
		}
		
		query.addScalar("date", StandardBasicTypes.STRING);
		query.addScalar("staffCode", StandardBasicTypes.STRING);
		query.addScalar("staffName", StandardBasicTypes.STRING);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("rank", StandardBasicTypes.STRING);
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("countTimeLog", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryItineraryReport.class));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryItineraryReport> getSummaryItineraryTeamReport(Date start, Date end, List<Integer> userIds
			, Double noOfAssignmentDeviationLimit, Double sequenceDeviationLimit, Integer tpuDeviationLimit){
		String sql = "Select u.Team as team "
				+ " , Day(t.[Date]) as [date] "
				+ " , count(distinct t.timeLogId) as countTimeLog "
				+ " From TimeLog as t "
				+ " left join [User] as u on t.UserId = u.UserId "
				+ " left join [Rank] as r on u.RankId = r.RankId "
				+ " where t.[Date] between :start and :end ";
		
		if (userIds != null && userIds.size()>0) {
			sql += " and u.userId in ( :userIds )";
		}
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("( 1<>1 ");
		if (noOfAssignmentDeviationLimit != null) {
			builder.append("or t.AssignmentDeviation > :assignmentDeviation ");
		}
		if (sequenceDeviationLimit != null) {
			builder.append("or t.SequenceDeviation > :sequenceDeviation ");
		}
		if (tpuDeviationLimit != null) {
			builder.append("or t.TpuDeviation > :tpuDeviation "); 
		}
		builder.append(" )");
		
		sql += " and "+builder.toString();
		sql += " group by u.Team, t.[Date] ";
		sql += " order by team, [date]";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("start", start);
		query.setParameter("end", end);
		
		if (userIds != null && userIds.size()>0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (noOfAssignmentDeviationLimit != null) {
			query.setParameter("assignmentDeviation", noOfAssignmentDeviationLimit);
		}
		if (sequenceDeviationLimit != null) {
			query.setParameter("sequenceDeviation", sequenceDeviationLimit);
		}
		if (tpuDeviationLimit != null) {
			query.setParameter("tpuDeviation", tpuDeviationLimit); 
		}
		
		query.addScalar("date", StandardBasicTypes.STRING);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("countTimeLog", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryItineraryReport.class));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryItineraryReport> getSummaryItineraryMonthReport(Date start, Date end, List<Integer> userIds
			, Double noOfAssignmentDeviationLimit, Double sequenceDeviationLimit, Integer tpuDeviationLimit){
		String sql = "Select Day(t.[Date]) as [date] "
				+ " , count(distinct t.timeLogId) as countTimeLog "
				+ " From TimeLog as t "
				+ " left join [User] as u on t.UserId = u.UserId "
				+ " left join [Rank] as r on u.RankId = r.RankId "
				+ " where t.[Date] between :start and :end ";
		
		if (userIds != null && userIds.size()>0) {
			sql += " and u.userId in ( :userIds )";
		}
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("( 1<>1 ");
		if (noOfAssignmentDeviationLimit != null) {
			builder.append("or t.AssignmentDeviation > :assignmentDeviation ");
		}
		if (sequenceDeviationLimit != null) {
			builder.append("or t.SequenceDeviation > :sequenceDeviation ");
		}
		if (tpuDeviationLimit != null) {
			builder.append("or t.TpuDeviation > :tpuDeviation "); 
		}
		builder.append(" )");
		
		sql += " and "+builder.toString();
		sql += " group by t.[Date] ";
		sql += " order by [date]";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("start", start);
		query.setParameter("end", end);
		
		if (userIds != null && userIds.size()>0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (noOfAssignmentDeviationLimit != null) {
			query.setParameter("assignmentDeviation", noOfAssignmentDeviationLimit);
		}
		if (sequenceDeviationLimit != null) {
			query.setParameter("sequenceDeviation", sequenceDeviationLimit);
		}
		if (tpuDeviationLimit != null) {
			query.setParameter("tpuDeviation", tpuDeviationLimit); 
		}
		
		query.addScalar("date", StandardBasicTypes.STRING);
		query.addScalar("countTimeLog", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryItineraryReport.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<TimeLog> getTimeLogsByIds(List<Integer> ids) {
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.in("timeLogId", ids));
		return (List<TimeLog>) criteria.list();
	}

	public Long countFieldworkTimeLog(Date selectedDate, Integer userId, String referenceNo) {
		return (Long)this.createCriteria()
				.createAlias("fieldworkTimeLogs", "fieldworkTimeLogs")
				.setProjection(Projections.countDistinct("fieldworkTimeLogs.id"))
				.add(Restrictions.eq("date", selectedDate))
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("fieldworkTimeLogs.caseReferenceNo", referenceNo))
				.uniqueResult();
	}

	public Long countTelephoneTimeLog(Date selectedDate, Integer userId, String referenceNo) {
		return (Long)this.createCriteria()
				.createAlias("telephoneTimeLogs", "telephoneTimeLogs")
				.setProjection(Projections.countDistinct("telephoneTimeLogs.id"))
				.add(Restrictions.eq("date", selectedDate))
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("telephoneTimeLogs.caseReferenceNo", referenceNo))
				.uniqueResult();
	}
	
	public Integer getTimeLogIdByUserIdAndDate(Integer userId, Date date) {
		Criteria criteria = this.createCriteria().setProjection(Projections.property("timeLogId"));		
		criteria.add(Restrictions.eq("user.id", userId))
		.add(Restrictions.eq("date", date));
		return (Integer) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<TimeLogSyncData> getUpdatedTimeLog(Date lastSyncTime, Integer[] timeLogIds, Date[] dates, Integer userId){
		String otherWorkingSessionFrom = String.format("dbo.FormatTime(tl.otherWorkingSessionFrom, '%s')", SystemConstant.TIME_FORMAT);
		String otherWorkingSessionTo = String.format("dbo.FormatTime(tl.otherWorkingSessionTo, '%s')", SystemConstant.TIME_FORMAT);
		String otClaimed = String.format("dbo.FormatTime(tl.otClaimed, '%s')", SystemConstant.TIME_FORMAT);
		String timeoffTaken = String.format("dbo.FormatTime(tl.timeoffTaken, '%s')", SystemConstant.TIME_FORMAT);
		String hql = "select tl.timeLogId as timeLogId, tl.date as date"
				+ ", s.workingSessionSettingId as workingSessionSettingId, tl.isOtherWorkingSession as isOtherWorkingSession"
				+ ", tl.isClaimOT as isClaimOT"
				+ ", case when tl.otherWorkingSessionFrom is null then '' else "+otherWorkingSessionFrom+" end as otherWorkingSessionFrom"
				+ ", case when tl.otherWorkingSessionTo is null then '' else "+otherWorkingSessionTo+" end as otherWorkingSessionTo"
				+ ", case when tl.otClaimed is null then '' else "+otClaimed+" end as otClaimed"
				+ ", case when tl.timeoffTaken is null then '' else "+timeoffTaken+" end as timeoffTaken"
				+ ", u.userId as userId"
				+ ", tl.rejectReason as rejectReason, tl.itineraryCheckRemark as itineraryCheckRemark"
				+ ", tl.createdDate as createdDate, tl.modifiedDate as modifiedDate"
				+ ", tl.isTrainingAM as isTrainingAM, tl.isTrainingPM as isTrainingPM"
				+ ", tl.isVLSLAM as isVLSLAM, tl.isVLSLPM as isVLSLPM"
				+ ", tl.status as status, tl.isVoilateItineraryCheck as isVoilateItineraryCheck"
				+ ", tl.preApproval as preApproval, a.userId as approvedBy"
				+ ", tl.assignmentDeviation as assignmentDeviation, tl.sequenceDeviation as sequenceDeviation"
				+ ", tl.tpuDeviation as tpuDeviation"
				+ " from TimeLog as tl"
				+ " left join tl.setting as s"
				+ " left join tl.user as u"
				+ " left join tl.approvedBy as a"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and tl.modifiedDate >= :modifiedDate ";
		}
		
		List<String> condition = new ArrayList<String>();
		if (timeLogIds != null && timeLogIds.length > 0){
			condition.add("tl.timeLogId in ( :timeLogIds )");
		}
		if (dates != null && dates.length > 0){
			condition.add("tl.date in ( :dates ) and u.userId = :userId ");
		}
		
		String cond = StringUtils.join(condition.toArray(new String[0]), " or ");
		if (!StringUtils.isEmpty(cond)){
			hql += " and ("+cond+")";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if(lastSyncTime!=null)
			query.setParameter("modifiedDate", lastSyncTime);
		if (timeLogIds != null && timeLogIds.length > 0){
			query.setParameterList("timeLogIds", timeLogIds);
		}
		if (dates != null && dates.length > 0){
			query.setParameterList("dates", dates);
			query.setParameter("userId", userId);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(TimeLogSyncData.class));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getTimeLogDateByNightSession(Integer userId, Date start, Date end){
		String time = "18:30";
		
		String sql = "Select Day(t.[Date]) as otDdate "
				+ " From TimeLog as t "
				+ " left join WorkingSessionSetting as w on w.WorkingSessionSettingId = t.WorkingSessionSettingId "
				+ " where t.[Date] between :start and :end "
				+ " and t.userId = :userId "
				+ " and (t.OtherWorkingSessionTo > :time or w.ToTime > :time )";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("time", time);
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameter("userId", userId);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryOfTimelog> getSummaryOfTimelogActivity(List<String> survey, 
			List<String> team, Date startMonth, Date endMonth, List<Integer> userId){

		String sql = "Select ft.Survey as survey" + 
				"	, u.Team as team" + 
				"	, r.code as [rank]" + 
				"	, u.StaffCode as staffCode" + 
				"	, u.EnglishName as staffName" + 
				"	, sum(case when ft.Activity = 'FI' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as fi" + 
				"	, sum(case when ft.Activity = 'TR' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as tr" + 
				"	, sum(case when ft.Activity = 'SD' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as sd" + 
				"	, sum(case when ft.Activity = 'LD' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as ld" + 
				"	, sum(case when ft.Activity = 'SA' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as sa" + 
				"	, 0 as od" + 
				"	, sum(case when ft.Activity = 'OT' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as ot" + 
				"	, sum(case when ft.Activity = '/' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as others" + 
				"	, sum(case when ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as total" + 
				"	From TimeLog as t" + 
				"	left join FieldworkTimeLog as ft on t.TimeLogId = ft.TimeLogId" + 
				"	left join [User] as u on u.UserId = t.UserId" + 
				"	left join [Rank] as r on u.RankId = r.RankId" + 
				"	where t.[Date] between :startMonth and :endMonth" ;
		
		if (survey != null && survey.size() > 0){
			sql += " and ft.Survey in (:survey) ";
		}
		if (team != null && team.size() > 0){
			sql += " and u.team in (:team) ";
		}
		if (userId != null && userId.size() > 0){
			sql += " and u.userId in (:userId) ";
		}
		
		sql += " group by ft.Survey, u.Team, r.code, u.StaffCode, u.EnglishName";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		if (survey != null && survey.size() > 0){
			query.setParameterList("survey", survey);
		}
		if (team != null && team.size() > 0){
			query.setParameterList("team", team);
		}
		if (userId != null && userId.size() > 0){
			query.setParameterList("userId", userId);
		}
		
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("rank",StandardBasicTypes.STRING);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("staffName",StandardBasicTypes.STRING);
		query.addScalar("fi",StandardBasicTypes.INTEGER);
		query.addScalar("tr",StandardBasicTypes.INTEGER);
		query.addScalar("sd",StandardBasicTypes.INTEGER);
		query.addScalar("ld",StandardBasicTypes.INTEGER);
		query.addScalar("sa",StandardBasicTypes.INTEGER);
		query.addScalar("od",StandardBasicTypes.INTEGER);
		query.addScalar("ot",StandardBasicTypes.INTEGER);
		query.addScalar("others",StandardBasicTypes.INTEGER);
		query.addScalar("total",StandardBasicTypes.INTEGER);		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfTimelog.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryOfTimelog> getSummaryOfTimelogActivityTeam(List<String> survey, 
			List<String> team, Date startMonth, Date endMonth, List<Integer> userId){

		String sql = "Select ft.Survey as survey" + 
				"	, u.Team as team" + 
				"	, sum(case when ft.Activity = 'FI' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as fi" + 
				"	, sum(case when ft.Activity = 'TR' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as tr" + 
				"	, sum(case when ft.Activity = 'SD' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as sd" + 
				"	, sum(case when ft.Activity = 'LD' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as ld" + 
				"	, sum(case when ft.Activity = 'SA' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as sa" + 
				"	, 0 as od" + 
				"	, sum(case when ft.Activity = 'OT' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as ot" + 
				"	, sum(case when ft.Activity = '/' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as others" + 
				"	, sum(case when ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as total" + 
				"	From TimeLog as t" + 
				"	left join FieldworkTimeLog as ft on t.TimeLogId = ft.TimeLogId" + 
				"	left join [User] as u on u.UserId = t.UserId" + 
				"	where t.[Date] between :startMonth and :endMonth" ;
		
		if (survey != null && survey.size() > 0){
			sql += " and ft.Survey in (:survey) ";
		}
		if (team != null && team.size() > 0){
			sql += " and u.team in (:team) ";
		}
		if (userId != null && userId.size() > 0){
			sql += " and u.userId in (:userId) ";
		}
		
		sql += " group by ft.Survey, u.Team  "
				+ " order by u.Team ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		if (survey != null && survey.size() > 0){
			query.setParameterList("survey", survey);
		}
		if (team != null && team.size() > 0){
			query.setParameterList("team", team);
		}
		if (userId != null && userId.size() > 0){
			query.setParameterList("userId", userId);
		}
		
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("fi",StandardBasicTypes.INTEGER);
		query.addScalar("tr",StandardBasicTypes.INTEGER);
		query.addScalar("sd",StandardBasicTypes.INTEGER);
		query.addScalar("ld",StandardBasicTypes.INTEGER);
		query.addScalar("sa",StandardBasicTypes.INTEGER);
		query.addScalar("od",StandardBasicTypes.INTEGER);
		query.addScalar("ot",StandardBasicTypes.INTEGER);
		query.addScalar("others",StandardBasicTypes.INTEGER);
		query.addScalar("total",StandardBasicTypes.INTEGER);		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfTimelog.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryOfTimelog> getSummaryOfTimelogActivityOverAll(List<String> survey, 
			List<String> team, Date startMonth, Date endMonth, List<Integer> userId){

		String sql = "Select ft.Survey as survey" + 
				"	, sum(case when ft.Activity = 'FI' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as fi" + 
				"	, sum(case when ft.Activity = 'TR' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as tr" + 
				"	, sum(case when ft.Activity = 'SD' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as sd" + 
				"	, sum(case when ft.Activity = 'LD' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as ld" + 
				"	, sum(case when ft.Activity = 'SA' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as sa" + 
				"	, 0 as od" + 
				"	, sum(case when ft.Activity = 'OT' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as ot" + 
				"	, sum(case when ft.Activity = '/' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as others" + 
				"	, sum(case when ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as total" + 
				"	From TimeLog as t" + 
				"	left join FieldworkTimeLog as ft on t.TimeLogId = ft.TimeLogId" + 
				"	left join [User] as u on u.UserId = t.UserId" + 
				"	where t.[Date] between :startMonth and :endMonth" ;
		
		if (survey != null && survey.size() > 0){
			sql += " and ft.Survey in (:survey) ";
		}
		if (team != null && team.size() > 0){
			sql += " and u.team in (:team) ";
		}
		if (userId != null && userId.size() > 0){
			sql += " and u.userId in (:userId) ";
		}
		
		sql += " group by ft.Survey "
				+ " order by ft.Survey ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		if (survey != null && survey.size() > 0){
			query.setParameterList("survey", survey);
		}
		if (team != null && team.size() > 0){
			query.setParameterList("team", team);
		}
		if (userId != null && userId.size() > 0){
			query.setParameterList("userId", userId);
		}
		
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("fi",StandardBasicTypes.INTEGER);
		query.addScalar("tr",StandardBasicTypes.INTEGER);
		query.addScalar("sd",StandardBasicTypes.INTEGER);
		query.addScalar("ld",StandardBasicTypes.INTEGER);
		query.addScalar("sa",StandardBasicTypes.INTEGER);
		query.addScalar("od",StandardBasicTypes.INTEGER);
		query.addScalar("ot",StandardBasicTypes.INTEGER);
		query.addScalar("others",StandardBasicTypes.INTEGER);
		query.addScalar("total",StandardBasicTypes.INTEGER);		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfTimelog.class));
		
		return query.list();
	}
	
	public TimeLog getTimeLogByUserIdAndDate(Integer userId, Date date) {
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.eq("user.id", userId))
		.add(Restrictions.eq("date", date));
		return (TimeLog) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<String> getTimeLogDateByUserId(Integer id) {
		String date = String.format("FORMAT(t.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = " select case when t.date is null then '' else "+date+" end as date "
				+ " from TimeLog t "
				+ " where t.user.userId = :id ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("id", id);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryOfTimeLogEnumerationOutcomeReport> getSummaryOfTimeLogEnumerationOutcomeReport (
			List<String> survey, List<Integer> userId, Date startDate, Date endDate) {

		String sql = "Select t.[Date] as timeLogDate" + 
				"	, case when ft.Survey = 'MRPS' then 'MRPS' " 
					+ " when ft.Survey = 'GHS' then 'GHS' "
					+ " when ft.Survey = 'BMWPS' then 'BMWPS' "
					+ " when ft.Survey is null or ft.Survey = '' then '' "
					+ " else 'Others' end as survey " +
				"	, case when ft.Survey = 'MRPS' then 1 " 
					+ " when ft.Survey = 'GHS' then 2 "
					+ " when ft.Survey = 'BMWPS' then 3 "
					+ " else 4 end as surveySequence " +
				"	, u.Team as team" + 
				"	, r.Code as [rank]" + 
				"	, u.StaffCode as staffCode" + 
				"	, u.EnglishName as staffName" + 
				"	, sum(case when ft.EnumerationOutcome = 'C' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as completion" + 
				"	, sum(case when ft.EnumerationOutcome = 'D' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as deletion" + 
				"	, sum(case when ft.EnumerationOutcome = 'L' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as doorLocked" + 
				"	, sum(case when ft.EnumerationOutcome = 'M' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as Moved" + 
				"	, sum(case when ft.EnumerationOutcome = 'N' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as nonContact" + 
				"	, sum(case when ft.EnumerationOutcome = 'ND' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as nonDomestic" + 
				"	, sum(case when ft.EnumerationOutcome = 'U' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as unoccupied" + 
				"	, sum(case when ft.EnumerationOutcome = 'P' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as partially" + 
				"	, sum(case when ft.EnumerationOutcome = 'R' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as refusal" + 
				"	, sum(case when ft.EnumerationOutcome = 'O' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as others" + 
				"	, sum(case when ft.EnumerationOutcome = '' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as blank" + 
				"	, sum(case when ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as total" + 
				"	From TimeLog as t" + 
				"	inner join FieldworkTimeLog as ft on t.TimeLogId = ft.TimeLogId" + 
				"	left join [User] as u on u.UserId = t.UserId" + 
				"	left join [Rank] as r on u.RankId = r.RankId" +
				"	where t.[Date] between :startDate and :endDate";
		
		if (survey != null && survey.size() > 0){
			if (survey.contains("Others")){
				sql += " and ( ft.survey in (:survey) or ft.survey not in ('MRPS', 'GHS', 'BMWPS') )";
			} else {
				sql += " and ft.survey in (:survey) ";
			}
		}
		if (userId != null && userId.size() > 0){
			sql += " and u.userId in (:userId) ";
		}
		
		sql += " group by t.[Date], survey, u.Team, r.Code, u.StaffCode, u.EnglishName "
				+ " order by t.[Date], surveySequence, u.team, r.code, u.staffCode";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		if (survey != null && survey.size() > 0){
			query.setParameterList("survey", survey);
		}
		if (userId != null && userId.size() > 0){
			query.setParameterList("userId", userId);
		}
		
		query.addScalar("timeLogDate", StandardBasicTypes.DATE);
		query.addScalar("survey", StandardBasicTypes.STRING);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("rank", StandardBasicTypes.STRING);
		query.addScalar("staffCode", StandardBasicTypes.STRING);
		query.addScalar("staffName", StandardBasicTypes.STRING);
		query.addScalar("completion", StandardBasicTypes.INTEGER);
		query.addScalar("deletion", StandardBasicTypes.INTEGER);
		query.addScalar("doorLocked", StandardBasicTypes.INTEGER);
		query.addScalar("Moved", StandardBasicTypes.INTEGER);
		query.addScalar("nonContact", StandardBasicTypes.INTEGER);
		query.addScalar("nonDomestic", StandardBasicTypes.INTEGER);
		query.addScalar("unoccupied", StandardBasicTypes.INTEGER);
		query.addScalar("partially", StandardBasicTypes.INTEGER);
		query.addScalar("refusal", StandardBasicTypes.INTEGER);
		query.addScalar("others", StandardBasicTypes.INTEGER);
		query.addScalar("blank", StandardBasicTypes.INTEGER);
		query.addScalar("total", StandardBasicTypes.INTEGER);
		query.addScalar("surveySequence", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfTimeLogEnumerationOutcomeReport.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryOfTimeLogEnumerationOutcomeReport> getSummaryOfTimeLogEnumerationOutcomeReportTeam (
			List<String> survey, List<String> team, List<Integer> userId, Date startDate, Date endDate) {

		String sql = "Select t.[Date] as timeLogDate" + 
				"	, case when ft.Survey = 'MRPS' then 'MRPS' " 
					+ " when ft.Survey = 'GHS' then 'GHS' "
					+ " when ft.Survey = 'BMWPS' then 'BMWPS' "
					+ " when ft.Survey is null or ft.Survey = '' then '' "
					+ " else 'Others' end as survey " +
				"	, case when ft.Survey = 'MRPS' then 1 " 
					+ " when ft.Survey = 'GHS' then 2 "
					+ " when ft.Survey = 'BMWPS' then 3 "
					+ " else 4 end as surveySequence " +
				"	, u.Team as team" + 
				"	, sum(case when ft.EnumerationOutcome = 'C' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as completion" + 
				"	, sum(case when ft.EnumerationOutcome = 'D' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as deletion" + 
				"	, sum(case when ft.EnumerationOutcome = 'L' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as doorLocked" + 
				"	, sum(case when ft.EnumerationOutcome = 'M' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as Moved" + 
				"	, sum(case when ft.EnumerationOutcome = 'N' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as nonContact" + 
				"	, sum(case when ft.EnumerationOutcome = 'ND' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as nonDomestic" + 
				"	, sum(case when ft.EnumerationOutcome = 'U' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as unoccupied" + 
				"	, sum(case when ft.EnumerationOutcome = 'P' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as partially" + 
				"	, sum(case when ft.EnumerationOutcome = 'R' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as refusal" + 
				"	, sum(case when ft.EnumerationOutcome = 'O' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as others" + 
				"	, sum(case when ft.EnumerationOutcome = '' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as blank" + 
				"	, sum(case when ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as total" + 
				"	From TimeLog as t" + 
				"	inner join FieldworkTimeLog as ft on t.TimeLogId = ft.TimeLogId" + 
				"	left join [User] as u on u.UserId = t.UserId" +
				"	where t.[Date] between :startDate and :endDate"; 
		
		if (survey != null && survey.size() > 0){
			if (survey.contains("Others")){
				sql += " and ( ft.survey in (:survey) or ft.survey not in ('MRPS', 'GHS', 'BMWPS') )";
			} else {
				sql += " and ft.survey in (:survey) ";
			}
		}
		if (team != null && team.size() > 0){
			sql += " and u.team in (:team) ";
		}
		if (userId != null && userId.size() > 0){
			sql += " and u.userId in (:userId) ";
		}
		
		sql += " group by t.[Date], survey, u.Team "
				+ " order by t.[Date], surveySequence, u.Team ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		if (survey != null && survey.size() > 0){
			query.setParameterList("survey", survey);
		}
		if (team != null && team.size() > 0){
			query.setParameterList("team", team);
		}
		if (userId != null && userId.size() > 0){
			query.setParameterList("userId", userId);
		}
		
		query.addScalar("timeLogDate", StandardBasicTypes.DATE);
		query.addScalar("survey", StandardBasicTypes.STRING);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("completion", StandardBasicTypes.INTEGER);
		query.addScalar("deletion", StandardBasicTypes.INTEGER);
		query.addScalar("doorLocked", StandardBasicTypes.INTEGER);
		query.addScalar("Moved", StandardBasicTypes.INTEGER);
		query.addScalar("nonContact", StandardBasicTypes.INTEGER);
		query.addScalar("nonDomestic", StandardBasicTypes.INTEGER);
		query.addScalar("unoccupied", StandardBasicTypes.INTEGER);
		query.addScalar("partially", StandardBasicTypes.INTEGER);
		query.addScalar("refusal", StandardBasicTypes.INTEGER);
		query.addScalar("others", StandardBasicTypes.INTEGER);
		query.addScalar("blank", StandardBasicTypes.INTEGER);
		query.addScalar("total", StandardBasicTypes.INTEGER);
		query.addScalar("surveySequence", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfTimeLogEnumerationOutcomeReport.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryOfTimeLogEnumerationOutcomeReport> getSummaryOfTimeLogEnumerationOutcomeReportOverAll(
			List<String> survey, List<String> team, List<Integer> userId, Date startDate, Date endDate) {

		String sql = "Select t.[Date] as timeLogDate" + 
				"	, case when ft.Survey = 'MRPS' then 'MRPS' " 
					+ " when ft.Survey = 'GHS' then 'GHS' "
					+ " when ft.Survey = 'BMWPS' then 'BMWPS' "
					+ " when ft.Survey is null or ft.Survey = '' then '' "
					+ " else 'Others' end as survey " +
				"	, case when ft.Survey = 'MRPS' then 1 " 
					+ " when ft.Survey = 'GHS' then 2 "
					+ " when ft.Survey = 'BMWPS' then 3 "
					+ " else 4 end as surveySequence " +
				"	, sum(case when ft.EnumerationOutcome = 'C' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as completion" + 
				"	, sum(case when ft.EnumerationOutcome = 'D' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as deletion" + 
				"	, sum(case when ft.EnumerationOutcome = 'L' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as doorLocked" + 
				"	, sum(case when ft.EnumerationOutcome = 'M' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as Moved" + 
				"	, sum(case when ft.EnumerationOutcome = 'N' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as nonContact" + 
				"	, sum(case when ft.EnumerationOutcome = 'ND' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as nonDomestic" + 
				"	, sum(case when ft.EnumerationOutcome = 'U' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as unoccupied" + 
				"	, sum(case when ft.EnumerationOutcome = 'P' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as partially" + 
				"	, sum(case when ft.EnumerationOutcome = 'R' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as refusal" + 
				"	, sum(case when ft.EnumerationOutcome = 'O' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as others" + 
				"	, sum(case when ft.EnumerationOutcome = '' and ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as blank" + 
				"	, sum(case when ft.EndTime is not null then DATEDIFF(MINUTE, ft.StartTime, ft.EndTime) else 0 end) as total" + 
				"	From TimeLog as t" + 
				"	inner join FieldworkTimeLog as ft on t.TimeLogId = ft.TimeLogId" + 
				"	left join [User] as u on u.UserId = t.UserId" +
				"	where t.[Date] between :startDate and :endDate"; 
		
		if (survey != null && survey.size() > 0){
			if (survey.contains("Others")){
				sql += " and ( ft.survey in (:survey) or ft.survey not in ('MRPS', 'GHS', 'BMWPS') )";
			} else {
				sql += " and ft.survey in (:survey) ";
			}
		}
		if (team != null && team.size() > 0){
			sql += " and u.team in (:team) ";
		}
		if (userId != null && userId.size() > 0){
			sql += " and u.userId in (:userId) ";
		}
		
		sql += " group by t.[Date], survey "
				+ " order by t.[Date], surveySequence";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		if (survey != null && survey.size() > 0){
			query.setParameterList("survey", survey);
		}
		if (team != null && team.size() > 0){
			query.setParameterList("team", team);
		}
		if (userId != null && userId.size() > 0){
			query.setParameterList("userId", userId);
		}
		
		query.addScalar("timeLogDate", StandardBasicTypes.DATE);
		query.addScalar("survey", StandardBasicTypes.STRING);
		query.addScalar("completion", StandardBasicTypes.INTEGER);
		query.addScalar("deletion", StandardBasicTypes.INTEGER);
		query.addScalar("doorLocked", StandardBasicTypes.INTEGER);
		query.addScalar("Moved", StandardBasicTypes.INTEGER);
		query.addScalar("nonContact", StandardBasicTypes.INTEGER);
		query.addScalar("nonDomestic", StandardBasicTypes.INTEGER);
		query.addScalar("unoccupied", StandardBasicTypes.INTEGER);
		query.addScalar("partially", StandardBasicTypes.INTEGER);
		query.addScalar("refusal", StandardBasicTypes.INTEGER);
		query.addScalar("others", StandardBasicTypes.INTEGER);
		query.addScalar("blank", StandardBasicTypes.INTEGER);
		query.addScalar("total", StandardBasicTypes.INTEGER);
		query.addScalar("surveySequence", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfTimeLogEnumerationOutcomeReport.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ExportTimeLogMaintenanceList> getExportTimeLogList(List<String> timeLogPeriod, List<String> timeLogUserId){
		
		//String timeLogPeriodFormat = String.format("FORMAT(t.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "select "
						//TimeLog
						+ "t.[IsOtherWorkingSession] as isOtherWorkingSession, "
						+ "t.[OtherWorkingSessionFrom] as otherWorkingSessionFrom, "
						+ "t.OtherWorkingSessionTo as otherWorkingSessionTo, "
						+ "t.TimeLogId as timeLogId, "
						+ "t.[Date] as date, "
						+ "u.StaffCode as staffCode, "
						+ "ws.FromTime as workingSessionFrom, "
						+ "ws.ToTime as workingSessionTo, "
						+ "t.OTClaimed as otClaimed, "
						+ "t.Timeofftaken as timeoffTaken, "
						+ "t.IsTrainingAM as isTrainingAM, "
						+ "t.IsTrainingPM as isTrainingPM, "
						+ "t.IsVLSLAM as isVLSLAM, "
						+ "t.IsVLSLPM as isVLSLPM, "
						+ "t.Status as status, "
						+ "t.IsVoilateItineraryCheck as isVoilateItineraryCheck, "
						+ "t.ApprovedBy as approvedBy, "
						+ "t.AssignmentDeviation as assignmentDeviation, "
						+ "t.SequenceDeviation as sequenceDeviation, "
						+ "t.TpuDeviation as tpuDeviation, "
						+ "t.RejectReason as rejectReason, "
						+ "t.ItineraryCheckRemark as itineraryCheckRemark, "
						+ "t.CreatedDate as createdDate, "
						+ "t.CreatedBy as createdBy, "
						+ "t.ModifiedDate as modifiedDate, "
						+ "t.ModifiedBy as modifiedBy "
					+ "from TimeLog as t "
						+ "left join [User] as u on t.UserId = u.UserId "
						+ "left join WorkingSessionSetting as ws on t.WorkingSessionSettingId = ws.WorkingSessionSettingId "
					+ "where 1=1 ";
						if(timeLogPeriod != null && timeLogPeriod.size() == 2) {
							sql+= "and t.[Date] between convert(date, :timeLogPeriodStart, 103) and convert(date, :timeLogPeriodEnd, 103) ";
						}
						if(timeLogUserId != null && timeLogUserId.size() > 0){
							sql+= "and t.UserId in (:timeLogUserId) ";
						}

					sql+= "order by t.TimeLogId asc";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		
		if (timeLogPeriod != null && timeLogPeriod.size() == 2) {
			if(timeLogPeriod.get(0) != null && timeLogPeriod.get(1) != null){
				query.setParameter("timeLogPeriodStart", timeLogPeriod.get(0));
				query.setParameter("timeLogPeriodEnd", timeLogPeriod.get(1));
			}
		}
		
		if (timeLogUserId != null && timeLogUserId.size() > 0 ) {
			query.setParameterList("timeLogUserId", timeLogUserId);
		}
		
		//TimeLog
		query.addScalar("timeLogId", StandardBasicTypes.INTEGER)
			.addScalar("date", StandardBasicTypes.TIMESTAMP)
			//User
			.addScalar("staffCode", StandardBasicTypes.STRING)
			//WorkingSessionSetting
			.addScalar("workingSessionFrom", StandardBasicTypes.TIMESTAMP)
			.addScalar("workingSessionTo", StandardBasicTypes.TIMESTAMP)
			.addScalar("otClaimed", StandardBasicTypes.TIMESTAMP)
			.addScalar("timeoffTaken", StandardBasicTypes.TIMESTAMP)
			.addScalar("isTrainingAM", StandardBasicTypes.BOOLEAN)
			.addScalar("isTrainingPM", StandardBasicTypes.BOOLEAN)
			.addScalar("isVLSLAM", StandardBasicTypes.BOOLEAN)
			.addScalar("isVLSLPM", StandardBasicTypes.BOOLEAN)
			.addScalar("status", StandardBasicTypes.STRING)
			.addScalar("isVoilateItineraryCheck", StandardBasicTypes.BOOLEAN)
			.addScalar("approvedBy", StandardBasicTypes.INTEGER)
			.addScalar("assignmentDeviation", StandardBasicTypes.DOUBLE)
			.addScalar("sequenceDeviation", StandardBasicTypes.DOUBLE)
			.addScalar("tpuDeviation", StandardBasicTypes.DOUBLE)
			.addScalar("rejectReason", StandardBasicTypes.STRING)
			.addScalar("itineraryCheckRemark", StandardBasicTypes.STRING)
			.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
			.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
			.addScalar("createdBy", StandardBasicTypes.STRING)
			.addScalar("modifiedBy", StandardBasicTypes.STRING)

			//Other Working Session
			.addScalar("isOtherWorkingSession", StandardBasicTypes.BOOLEAN)
			.addScalar("otherWorkingSessionFrom", StandardBasicTypes.TIMESTAMP)
			.addScalar("otherWorkingSessionTo", StandardBasicTypes.TIMESTAMP);
			
			query.setResultTransformer(Transformers.aliasToBean(ExportTimeLogMaintenanceList.class));
			
			return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ExportTimeLogMaintenanceList> getExportTelephoneTimeLogList(List<String> timeLogPeriod, List<String> timeLogUserId){
		
		//String timeLogPeriodFormat = String.format("FORMAT(t.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "select "
					//TelephoneTimeLog
					+ "tt.TimeLogId, "
					+ "tt.TelephoneTimeLogId as telephoneTimeLogId, "
					+ "tt.ReferenceMonth as referenceMonth, "
					+ "tt.Survey as survey, "
					+ "p.Name as purpose, "
					+ "tt.[Session] as telSession, "
					+ "tt.assignmentId as assignmentId, "
					+ "tt.CaseReferenceNo as caseReferenceNo, "
					+ "tt.TotalQuotation as totalQuotation, "
					+ "tt.QuotationCount as quotationCount, "
					+ "tt.Status as enumerationOutcome, "
					+ "tt.CreatedDate as createdDate, "
					+ "tt.CreatedBy as createdBy, "
					+ "tt.ModifiedDate as modifiedDate, "
					+ "tt.ModifiedBy as modifiedBy "
				+ "from TelephoneTimeLog as tt "
					+ "inner join TimeLog as t on t.TimeLogId = tt.TimeLogId "
					+ "left join [User] as u on t.UserId = u.UserId "
					+ "left join Purpose as p on tt.PurposeId = p.PurposeId "
				+ "where 1=1 ";
					if(timeLogPeriod != null && timeLogPeriod.size() == 2) {
						sql+= "and t.[Date] between convert(date, :timeLogPeriodStart, 103) and convert(date, :timeLogPeriodEnd, 103) ";
					}
					if(timeLogUserId != null && timeLogUserId.size() > 0){
						sql+= "and t.UserId in (:timeLogUserId) ";
					}
				sql+= "order by t.TimeLogId, tt.TelephoneTimeLogId asc";
	
	SQLQuery query = getSession().createSQLQuery(sql);
	
	if (timeLogPeriod != null && timeLogPeriod.size() == 2) {
		if(timeLogPeriod.get(0) != null && timeLogPeriod.get(1) != null){
			query.setParameter("timeLogPeriodStart", timeLogPeriod.get(0));
			query.setParameter("timeLogPeriodEnd", timeLogPeriod.get(1));
		}
	}
	if (timeLogUserId != null && timeLogUserId.size() > 0 ) {
		query.setParameterList("timeLogUserId", timeLogUserId);
	}
	
	//TimeLog
	query.addScalar("timeLogId", StandardBasicTypes.INTEGER)
		//TelephoneTimeLog
		.addScalar("telephoneTimeLogId", StandardBasicTypes.INTEGER)
		.addScalar("referenceMonth", StandardBasicTypes.TIMESTAMP)
		.addScalar("survey", StandardBasicTypes.STRING)
		.addScalar("purpose", StandardBasicTypes.STRING)
		.addScalar("telSession", StandardBasicTypes.STRING)
		.addScalar("assignmentId", StandardBasicTypes.INTEGER)
		.addScalar("caseReferenceNo", StandardBasicTypes.STRING)
		.addScalar("totalQuotation", StandardBasicTypes.INTEGER)
		.addScalar("quotationCount", StandardBasicTypes.INTEGER)
		.addScalar("enumerationOutcome", StandardBasicTypes.STRING)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("createdBy", StandardBasicTypes.STRING)
		.addScalar("modifiedBy", StandardBasicTypes.STRING);

		query.setResultTransformer(Transformers.aliasToBean(ExportTimeLogMaintenanceList.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ExportTimeLogMaintenanceList> getExportFieldworkTimeLogList(List<String> timeLogPeriod, List<String> timeLogUserId){
		
		//String timeLogPeriodFormat = String.format("FORMAT(t.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "select "
							//TimeLog
							+ "t.TimeLogId as timeLogId, "
							//FieldworkTimeLog
							+ "ft.FieldworkTimeLogId as fieldworkTimeLogId, "
							+ "ft.ReferenceMonth as referenceMonth, "
							+ "ft.Survey as survey, "
							+ "p.Name as purpose, "
							+ "ft.Activity as fieldActivity, "
							+ "ft.StartTime as fieldStartTime, "
							+ "ft.EndTime as fieldNextActivityTime, "
							+ "ft.AssignmentId as assignmentId, "
							+ "ft.CaseReferenceNo as caseReferenceNo, "
							+ "ft.Destination as fieldDestination, "
							+ "ft.RecordType as fieldRecordType, "
							+ "ft.TotalQuotation as totalQuotation, "
							+ "ft.QuotationCount as quotationCount, "
							+ "ft.Building as fieldBuilding, "
							+" ft.EnumerationOutcome as enumerationOutcome, "
							+ "ft.Remark as fieldRemark, "
							+ "ft.FromLocation as fieldFromLocation, "
							+ "ft.ToLocation as fieldToLocation, "
							+ "ft.IncludeInTransportForm as fieldIncludeInTransportForm, "
							+ "ft.Transit as fieldTransit, "
							+ "ft.Transport as fieldTransport, "
							+ "ft.Expenses as fieldExpenses, "
							+ "ft.CreatedDate as createdDate, "
							+ "ft.CreatedBy as createdBy, "
							+ "ft.ModifiedDate as modifiedDate, "
							+ "ft.ModifiedBy as modifiedBy "
						+ "from FieldworkTimeLog as ft "
							+ "inner join TimeLog as t on t.TimeLogId = ft.TimeLogId "
							+ "left join [User] as u on t.UserId = u.UserId "
							+ "left join Purpose as p on ft.PurposeId = p.PurposeId "
						+ "where 1=1 ";
							if (timeLogPeriod != null && timeLogPeriod.size() > 0) {
								sql+= "and t.[Date] between convert(date, :timeLogPeriodStart, 103) and convert(date, :timeLogPeriodEnd, 103) ";
							}
							if(timeLogUserId != null && timeLogUserId.size() > 0){
								sql+= "and t.UserId in (:timeLogUserId) ";
							}
					sql+= "order by t.TimeLogId, ft.FieldworkTimeLogId asc";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		
		if (timeLogPeriod != null && timeLogPeriod.size() == 2) {
			if(timeLogPeriod.get(0) != null && timeLogPeriod.get(1) != null){
				query.setParameter("timeLogPeriodStart", timeLogPeriod.get(0));
				query.setParameter("timeLogPeriodEnd", timeLogPeriod.get(1));
			}
		}
		if (timeLogUserId != null && timeLogUserId.size() > 0 ) {
			query.setParameterList("timeLogUserId", timeLogUserId);
		}
		
		//TimeLog
		query.addScalar("timeLogId", StandardBasicTypes.INTEGER)
			//FieldworkTimeLog
			.addScalar("fieldworkTimeLogId", StandardBasicTypes.INTEGER)
			.addScalar("referenceMonth", StandardBasicTypes.TIMESTAMP)
			.addScalar("survey", StandardBasicTypes.STRING)
			.addScalar("purpose", StandardBasicTypes.STRING)
			.addScalar("fieldActivity", StandardBasicTypes.STRING)
			.addScalar("fieldStartTime", StandardBasicTypes.TIMESTAMP)
			.addScalar("fieldNextActivityTime", StandardBasicTypes.TIMESTAMP)
			.addScalar("assignmentId", StandardBasicTypes.INTEGER)
			.addScalar("caseReferenceNo", StandardBasicTypes.STRING)
			.addScalar("fieldDestination", StandardBasicTypes.STRING)
			.addScalar("fieldRecordType", StandardBasicTypes.INTEGER)
			.addScalar("totalQuotation", StandardBasicTypes.INTEGER)
			.addScalar("quotationCount", StandardBasicTypes.INTEGER)
			.addScalar("fieldBuilding", StandardBasicTypes.INTEGER)
			.addScalar("enumerationOutcome", StandardBasicTypes.STRING)
			.addScalar("fieldRemark", StandardBasicTypes.STRING)
			.addScalar("fieldFromLocation", StandardBasicTypes.STRING)
			.addScalar("fieldToLocation", StandardBasicTypes.STRING)
			.addScalar("fieldIncludeInTransportForm", StandardBasicTypes.BOOLEAN)
			.addScalar("fieldTransit", StandardBasicTypes.BOOLEAN)
			.addScalar("fieldTransport", StandardBasicTypes.STRING)
			.addScalar("fieldExpenses", StandardBasicTypes.DOUBLE)
			.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
			.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
			.addScalar("createdBy", StandardBasicTypes.STRING)
			.addScalar("modifiedBy", StandardBasicTypes.STRING);
			
			query.setResultTransformer(Transformers.aliasToBean(ExportTimeLogMaintenanceList.class));
			
			return query.list();
	}
}
