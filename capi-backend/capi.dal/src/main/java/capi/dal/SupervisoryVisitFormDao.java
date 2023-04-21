package capi.dal;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
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
import org.springframework.util.StringUtils;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.SupervisoryVisitForm;
import capi.model.SystemConstant;
import capi.model.api.dataSync.SupervisoryVisitFormSyncData;
import capi.model.batch.QCStatisticModel;
import capi.model.qualityControlManagement.SupervisoryVisitApprovalTableList;
import capi.model.qualityControlManagement.SupervisoryVisitDetailTableList;
import capi.model.qualityControlManagement.SupervisoryVisitTableList;
import capi.model.report.SummaryOfSupervisoryVisitSpotCheckData;

@Repository("SupervisoryVisitFormDao")
public class SupervisoryVisitFormDao  extends GenericDao<SupervisoryVisitForm>{

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitTableList> selectAllSupervisoryVisit(String search, int firstRecord, int displayLength, Order order
			, boolean aboveSupervisor, List<Integer> actedUserIds, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("svf")
								.createAlias("svf.user", "u", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("svf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String visitDate = String.format("FORMAT({alias}.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("svf.supervisoryVisitFormId"), "supervisoryVisitFormId");
		projList.add(Projections.property("u.userId"), "fieldOfficerId");
		projList.add(Projections.property("u.staffCode"), "fieldOfficer");
		projList.add(Projections.property("u.chineseName"), "fieldOfficerName");
		projList.add(Projections.property("sup.userId"), "supervisorId");
		projList.add(Projections.property("checker.staffCode"), "checkerCode");
		projList.add(SQLProjectionExt.sqlProjection(visitDate + " as visitDate", 
				new String [] {"visitDate"}, new Type[]{StandardBasicTypes.STRING}), "visitDate");
		projList.add(Projections.property("svf.status"), "status");
		
		criteria.setProjection(projList);
		
		if(!aboveSupervisor) {
			criteria.add(Restrictions.in("checker.userId", actedUserIds));
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(Restrictions.ne("checker.rank.rankId", 8));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.sqlRestriction(
							visitDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("svf.status", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SupervisoryVisitTableList.class));

        return criteria.list();
	}

	public long countSelectAllSupervisoryVisit(String search, boolean aboveSupervisor, List<Integer> actedUserIds, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("svf")
								.createAlias("svf.user", "u", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("svf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String visitDate = String.format("FORMAT({alias}.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("svf.supervisoryVisitFormId"),
				Projections.groupProperty("u.userId"),
				Projections.groupProperty("u.staffCode"),
				Projections.groupProperty("u.chineseName"),
				Projections.groupProperty("sup.userId"),
				Projections.groupProperty("svf.visitDate"),
				Projections.groupProperty("checker.userId"),
				Projections.groupProperty("svf.status")
		));

		criteria.setProjection(projList);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.sqlRestriction(
							visitDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("svf.status", "%" + search + "%")
			));
		}
		
		if(!aboveSupervisor) {
			criteria.add(Restrictions.in("checker.userId", actedUserIds));
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(Restrictions.ne("checker.rank.rankId", 8));
		}

        return (long)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitTableList> selectOutstandingSupervisoryVisit(String search, int firstRecord, int displayLength, Order order
			, boolean aboveSupervisor, List<Integer> actedUserIds, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("svf")
								.createAlias("svf.user", "u", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("svf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String visitDate = String.format("FORMAT({alias}.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("svf.supervisoryVisitFormId"), "supervisoryVisitFormId");
		projList.add(Projections.property("u.userId"), "fieldOfficerId");
		projList.add(Projections.property("u.staffCode"), "fieldOfficer");
		projList.add(Projections.property("u.chineseName"), "fieldOfficerName");
		projList.add(Projections.property("sup.userId"), "supervisorId");
		projList.add(Projections.property("checker.staffCode"), "checkerCode");
		projList.add(SQLProjectionExt.sqlProjection(visitDate + " as visitDate", 
				new String [] {"visitDate"}, new Type[]{StandardBasicTypes.STRING}), "visitDate");
		projList.add(Projections.property("svf.status"), "status");
		
		criteria.setProjection(projList);
		
		if(!aboveSupervisor) {
			criteria.add(Restrictions.in("checker.userId", actedUserIds));
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(Restrictions.ne("checker.rank.rankId", 8));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.sqlRestriction(
							visitDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("svf.status", "%" + search + "%")
			));
		}
		
		criteria.add(
				Restrictions.or(
					Restrictions.not(
						Restrictions.in("svf.status", new String[] {SystemConstant.SUPERVISORYVISITFORM_STATUS_SUBMITTED,
																	SystemConstant.SUPERVISORYVISITFORM_STATUS_APPROVED} )),
					Restrictions.isNull("svf.status")
				)
		);

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SupervisoryVisitTableList.class));

        return criteria.list();
	}

	public long countSelectOutstandingSupervisoryVisit(String search, boolean aboveSupervisor, List<Integer> actedUserIds, Boolean hasAuthorityViewAllRecord
			, Date fromDate, Date toDate) {

		Criteria criteria = this.createCriteria("svf")
								.createAlias("svf.user", "u", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("svf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String visitDate = String.format("FORMAT({alias}.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("svf.supervisoryVisitFormId"),
				Projections.groupProperty("u.userId"),
				Projections.groupProperty("u.staffCode"),
				Projections.groupProperty("u.chineseName"),
				Projections.groupProperty("sup.userId"),
				Projections.groupProperty("svf.visitDate"),
				Projections.groupProperty("checker.userId"),
				Projections.groupProperty("svf.status")
		));

		criteria.setProjection(projList);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.sqlRestriction(
							visitDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("svf.status", "%" + search + "%")
			));
		}
		
		criteria.add(
				Restrictions.or(
					Restrictions.not(
						Restrictions.in("svf.status", new String[] {SystemConstant.SUPERVISORYVISITFORM_STATUS_SUBMITTED,
																	SystemConstant.SUPERVISORYVISITFORM_STATUS_APPROVED} )),
					Restrictions.isNull("svf.status")
		));
		criteria.add(Restrictions.between("svf.visitDate", fromDate, toDate));
		
		if(!aboveSupervisor) {
			criteria.add(Restrictions.in("checker.userId", actedUserIds));
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(Restrictions.ne("checker.rank.rankId", 8));
		}

        return (long)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitDetailTableList> selectSupervisoryVisitDetailListBySupervisoryVisitFormId(Integer supervisoryVisitFormId) {

		Criteria criteria = this.createCriteria("svf")
								.createAlias("svf.details", "svd", JoinType.LEFT_OUTER_JOIN);
//								.createAlias("svd.assignment", "a", JoinType.LEFT_OUTER_JOIN);

		String rowNumber = "ROW_NUMBER() OVER(ORDER BY {svd}.supervisoryVisitDetailId)";

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("svd.supervisoryVisitDetailId"), "supervisoryVisitDetailId");
//		projList.add(Projections.property("a.assignmentId"), "assignmentId");
//		projList.add(Projections.property("a.referenceNo"), "referenceNo");
		projList.add(Projections.property("svd.referenceNo"), "assignmentId");
		projList.add(Projections.property("svd.referenceNo"), "referenceNo");
		projList.add(Projections.property("svd.survey"), "survey");
		projList.add(Projections.property("svd.result"), "result");
		projList.add(Projections.property("svd.otherRemark"), "otherRemark");
		projList.add(SQLProjectionExt.sqlProjection(rowNumber + " as rowNumber", 
				new String [] {"rowNumber"}, new Type[]{StandardBasicTypes.INTEGER}), "rowNumber");

		criteria.setProjection(projList);
		
		if(supervisoryVisitFormId != null) {
			criteria.add(Restrictions.eq("svf.supervisoryVisitFormId", supervisoryVisitFormId));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(SupervisoryVisitDetailTableList.class));
		
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitApprovalTableList> selectAllSupervisoryVisitApproval(String search, int firstRecord, int displayLength, Order order
			, boolean isBusinessData, List<Integer> actedUserIds, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("svf")
								.createAlias("svf.user", "u", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("svf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String visitDate = String.format("FORMAT({alias}.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("svf.supervisoryVisitFormId"), "supervisoryVisitFormId");
		projList.add(Projections.property("u.userId"), "fieldOfficerId");
		projList.add(Projections.property("u.staffCode"), "fieldOfficer");
		projList.add(Projections.property("u.chineseName"), "fieldOfficerName");
		projList.add(Projections.property("u.team"), "team");
		projList.add(Projections.property("sup.userId"), "supervisorId");
		projList.add(Projections.property("sup.chineseName"), "supervisor");
		projList.add(Projections.property("checker.staffCode"), "checkerCode");
		projList.add(SQLProjectionExt.sqlProjection(visitDate + " as visitDate", 
				new String [] {"visitDate"}, new Type[]{StandardBasicTypes.STRING}), "visitDate");
		
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("svf.status", SystemConstant.SUPERVISORYVISITFORM_STATUS_SUBMITTED));
		
		if(!isBusinessData) {
			criteria.add(Restrictions.in("svf.submitTo.userId", actedUserIds));
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(Restrictions.ne("checker.rank.rankId", 8));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("u.team", "%" + search + "%"),
					Restrictions.sqlRestriction(
							visitDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("sup.staffCode", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SupervisoryVisitApprovalTableList.class));

        return criteria.list();
	}

	public long countSlectAllSupervisoryVisitApproval(String search, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("svf")
								.createAlias("svf.user", "u", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("svf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String visitDate = String.format("FORMAT({alias}.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("svf.supervisoryVisitFormId"),
				Projections.groupProperty("u.userId"),
				Projections.groupProperty("u.staffCode"),
				Projections.groupProperty("u.chineseName"),
				Projections.groupProperty("u.team"),
				Projections.groupProperty("sup.userId"),
				Projections.groupProperty("sup.staffCode"),
				Projections.groupProperty("checker.staffCode"),
				Projections.groupProperty("svf.visitDate")
		));

		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("svf.status", SystemConstant.SUPERVISORYVISITFORM_STATUS_SUBMITTED));
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(Restrictions.ne("checker.rank.rankId", 8));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("u.team", "%" + search + "%"),
					Restrictions.sqlRestriction(
							visitDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("sup.staffCode", "%" + search + "%")
			));
		}

        return (long)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitForm> getSupervisoryVisitFormsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("supervisoryVisitFormId", ids));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitForm> getSupervisoryVisitFormsByVisitDate(Date visitDate, Integer userId){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("visitDate", visitDate));
		criteria.add(Restrictions.eq("supervisor.userId", userId));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getVisitDatesByRefMonth(Date refMonth){
		Date refMonthEnd = DateUtils.addMonths(refMonth, 1);
		String visitDate = String.format("FORMAT({alias}.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		Criteria criteria = this.createCriteria("svf");
		criteria.setProjection(
				SQLProjectionExt.sqlProjection(visitDate + " as visitDate", 
						new String [] {"visitDate"}, new Type[]{StandardBasicTypes.STRING})
			);
		criteria.add(Restrictions.between("visitDate", refMonth, refMonthEnd));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitForm> getSVFormsByUserAndSVDate(List<Integer> officerIds, List<Date> visitDates){
		String[] status = {SystemConstant.SUPERVISORYVISITFORM_STATUS_APPROVED};
		Criteria criteria = this.createCriteria("svf");
		criteria.add(Restrictions.and(
				Restrictions.in("svf.user.userId", officerIds),
				Restrictions.in("svf.visitDate", visitDates),
				Restrictions.in("svf.status", status)
			)
		);
		return criteria.list();
	}
	

	public List<QCStatisticModel> getSupervisoryVisitCompleteStatus(){
		String sql = "Select u.userId as userId "
				+ " , count(distinct f.supervisoryVisitFormId) as formCompletedCount "
				+ " , count(distinct d.supervisoryVisitFormId) as ghsCompletedCount "
				+ " From [User] as u "
				+ " left join SupervisoryVisitForm as f on u.userId = f.officerId "
					+ " and (f.status = 'Submitted' or f.status = 'Approved') "
					+ " and YEAR(f.visitDate) = YEAR(getDate()) "
				+ " left join SupervisoryVisitForm as ghs on f.supervisoryVisitFormId = ghs.supervisoryVisitFormId "
				+ " and ghs.session = 2 "
				+ " left join SupervisoryVisitDetail as d on d.supervisoryVisitFormId = ghs.supervisoryVisitFormId "
				+ " and d.survey = 'GHS' "
				+ " inner join [UserRole] as ur on u.userId = ur.userId "
				+ " inner join [Role] as r on r.roleId = ur.roleId "
				+ " where (r.AuthorityLevel & 16) = 16 and u.[Status] <> 'Inactive' "
				+ " group by u.userId, u.staffCode"
				+ "	order by u.StaffCode";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("formCompletedCount", StandardBasicTypes.LONG);
		query.addScalar("ghsCompletedCount", StandardBasicTypes.LONG);
		
		query.setResultTransformer(Transformers.aliasToBean(QCStatisticModel.class));
		
		return query.list();
		
	}

	public List<SupervisoryVisitFormSyncData> getUpdatedSupervisoryVisitForm(Date lastSyncTime, Integer[] supervisoryVisitFormIds){
		String fromTime = String.format("dbo.FormatTime(sv.fromTime, '%s')", SystemConstant.TIME_FORMAT);
		String toTime = String.format("dbo.FormatTime(sv.toTime, '%s')", SystemConstant.TIME_FORMAT);
		
		String hql = "select sv.supervisoryVisitFormId as supervisoryVisitFormId"
				+ ", o.userId as officerId, s.userId as supervisorId"
				+ ", sv.session as session, sv.visitDate as visitDate"
				+ ", case when sv.fromTime is null then '' else "+fromTime+" end as fromTime"
				+ ", case when sv.toTime is null then '' else "+toTime+" end as toTime"
				+ ", sv.rejectReason as rejectReason, sv.discussionDate as discussionDate"
				+ ", sv.remark as remark, sv.knowledgeOfWorkResult as knowledgeOfWorkResult"
				+ ", sv.knowledgeOfWorkRemark as knowledgeOfWorkRemark, sv.interviewTechniqueResult as interviewTechniqueResult"
				+ ", sv.interviewTechniqueRemark as interviewTechniqueRemark, sv.handleDifficultInterviewResult as handleDifficultInterviewResult"
				+ ", sv.handleDifficultInterviewRemark as handleDifficultInterviewRemark, sv.dataRecordingResult as dataRecordingResult"
				+ ", sv.dataRecordingRemark as dataRecordingRemark, sv.localGeographyResult as localGeographyResult"
				+ ", sv.localGeographyRemark as localGeographyRemark, sv.mannerWithPublicResult as mannerWithPublicResult"
				+ ", sv.mannerWithPublicRemark as mannerWithPublicRemark, sv.judgmentResult as judgmentResult"
				+ ", sv.judgmentRemark as judgmentRemark, sv.organizationOfWorkResult as organizationOfWorkResult"
				+ ", sv.organizationOfWorkRemark as organizationOfWorkRemark, sv.otherResult as otherResult"
				+ ", sv.otherRemark as otherRemark, sv.createdDate as createdDate"
				+ ", sv.modifiedDate as modifiedDate, st.userId as submitTo"
				+ ", sv.status as status, ss.scSvPlanId as scSvPlanId"
				+ " from SupervisoryVisitForm as sv"
				+ " left join sv.user as o"
				+ " left join sv.supervisor as s"
				+ " left join sv.submitTo as st"
				+ " left join sv.scSvPlan as ss"
				+ " where 1=1 ";
		
		if (supervisoryVisitFormIds != null && supervisoryVisitFormIds.length > 0){
			hql += " and sv.supervisoryVisitFormId in ( :supervisoryVisitFormIds )";
		}
				
		
		if(lastSyncTime!=null){
			hql += " and sv.modifiedDate >= :modifiedDate";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		
		if (supervisoryVisitFormIds != null && supervisoryVisitFormIds.length > 0){
			query.setParameterList("supervisoryVisitFormIds", supervisoryVisitFormIds);
		}
		query.setResultTransformer(Transformers.aliasToBean(SupervisoryVisitFormSyncData.class));
		return query.list();
		
	}
	
//	@SuppressWarnings("unchecked")
//	public List<SummaryOfSupervisoryVisitSpotCheckData> getSummaryOfSupervisoryVisitSpotCheckReportData(Integer year, List<Integer> userIds){
//		
//		String sql = "select "
//				+ "u.UserId as userId, "
//				+ "cast ( u.StaffCode as varchar) as userStaffCode, "
//				+ "cast ( u.chineseName as varchar) as userChineseName, "
//				+ "cast ( u.englishName as varchar) as userEnglishName, "
//				+ "cast ( u.Team as varchar) as userTeam, "
//				+ "cast ( u.Destination as varchar) as userDestination, "
//				+ "u.isGHS as isGHS, "
//				+ "CONVERT(int, RIGHT(LEFT(CONVERT(varchar, f.VisitDate,112),6),2)) as month, "
//				+ "LEFT(CONVERT(varchar, f.VisitDate,109),6) as date, "
//				+ "case srf.Name when 'Field Head' then CAST(1 AS BIT) else CAST(0 AS BIT)  end as isFieldHead, "
//				+ "case srs.Name when 'Section Head' then CAST(1 AS BIT) else CAST(0 AS BIT)  end as isSectionHead, "
//				+ "count (distinct CASE when cpid.survey is null then null else cpi.supervisoryVisitFormId end ) as cpiCount, "
//				+ "count (distinct CASE when ghsd.survey is null then null else ghs.supervisoryVisitFormId end ) as ghsCount, "
//				+ "f.session as session "
//				+ "from [dbo].[User] as u "
//				+ "inner join UserRole as ur "
//				+ "on u.UserId = ur.UserId "
//				+ "inner join [Role] as r "
//				+ "on r.RoleId = ur.RoleId and (r.[AuthorityLevel] & 16) = 16 "
//				+ "left join [dbo].[SupervisoryVisitForm] as f  "
//				+ "on u.UserId = f.OfficerId and year(f.VisitDate) = :year "
//				+ "left join [dbo].[SupervisoryVisitForm] as ghs "
//				+ "on u.UserId = f.OfficerId and f.VisitDate = ghs.VisitDate "
//				+ "left join SupervisoryVisitDetail as ghsd "
//				+ "on ghsd.SupervisoryVisitFormId = ghs.SupervisoryVisitFormId and ghsd.Survey = 'GHS' "
//				+ "left join [dbo].[SupervisoryVisitForm] as cpi "
//				+ "on u.UserId = f.OfficerId and f.VisitDate = cpi.VisitDate "
//				+ "left join SupervisoryVisitDetail as cpid "
//				+ "on cpid.SupervisoryVisitFormId = cpi.SupervisoryVisitFormId and ghsd.Survey = 'CPI' "
//				+ "left join [User] as s "
//				+ "on f.SupervisorId = s.UserId "
//				+ "left join [UserRole] as sur "
//				+ "on sur.UserId = s.UserId "
//				+ "left join [Role] as srf "
//				+ "on srf.RoleId = sur.RoleId and srf.name = 'Field Head' "
//				+ "left join [Role] as srs "
//				+ "on srs.RoleId = sur.RoleId and srs.name = 'Section Head' "
//				+ "where u.userId in (:userIds) "
//				+ "group by u.UserId, u.StaffCode,	u.chineseName, u.englishName, u.Team, u.Destination, u.isGHS, LEFT(CONVERT(varchar, f.VisitDate,112),6),LEFT(CONVERT(varchar, f.VisitDate,109),6), f.session, srf.name, srs.name ";
//		
//		Query query = this.getSession().createSQLQuery(sql);
//
//		query.setParameter("year", year);
//		query.setParameterList("userIds", userIds);
//		
//		query.setResultTransformer(Transformers.aliasToBean(SummaryOfSupervisoryVisitSpotCheckData.class));
//		
//		return query.list();
//	}
	
	@SuppressWarnings("unchecked")
	public List<SummaryOfSupervisoryVisitSpotCheckData> getSummaryOfSupervisoryVisitSpotCheckReportData(Integer year, List<Integer> userIds){
		
		String sql = "select "
				+ "u.UserId as userId, "
				+ "u.StaffCode as userStaffCode, "
				+ "u.EnglishName as userEnglishName, "
				+ "u.Team  as userTeam, "
				+ "u.IsGHS as isGHS, "
				+ "Month(svf.VisitDate) as month, "
				+ "Concat(DATENAME(DAY, svf.VisitDate), ' ', Cast(Datename(Month,svf.VisitDate) as Char(3))) as date, "
				+ "svf.Session as ghsSession, "
				+ "svd.Survey as survey, "
				+ "rank.Code as userRankCode, "
				+ "sRank.Code as superRankCode, "
				+ "isNull(Count(case when svd.Survey = 'GHS' then svf.supervisoryVisitFormId end) OVER (PARTITION BY svf.supervisoryVisitFormId ),0) as ghsCount,"
				+ "isNull(Count(case when svd.Survey = 'GHS' and svf.Session = 1 then svf.supervisoryVisitFormId end) OVER (PARTITION BY svf.supervisoryVisitFormId ),0) as dayShiftCount,"
				+ "isNull(Count(case when svd.Survey = 'GHS' and svf.Session = 2 then svf.supervisoryVisitFormId end) OVER (PARTITION BY svf.supervisoryVisitFormId ),0) as nightShiftCount,"
				+ "isNull(Count(case when svd.Survey = 'MRPS' or svd.Survey = 'BMWPS' then svf.supervisoryVisitFormId end) OVER (PARTITION BY svf.supervisoryVisitFormId ),0) as cpiCount "
				+ "FROM [User] AS u LEFT JOIN "
				+ "UserRole AS ur ON u.UserId = ur.UserId LEFT JOIN "
				+ "[Rank] AS rank ON rank.RankId = u.RankId LEFT JOIN "
				+ "Role AS r ON r.RoleId = ur.RoleId AND r.AuthorityLevel & 16 = 16 LEFT JOIN "
				+ "SupervisoryVisitForm AS svf ON u.UserId = svf.OfficerId "
				+ "and YEAR(svf.Visitdate) = :year and svf.Status = 'Approved' LEFT JOIN "
				+ "[User] AS s ON svf.SupervisorId = s.UserId LEFT JOIN "
				+ "[Rank] AS sRank on sRank.RankId = s.RankId "
				+ "OUTER APPLY (select top 1 * from SupervisoryVisitDetail where SupervisoryVisitFormId = svf.SupervisoryVisitFormId ORDER BY svf.SupervisoryVisitFormId asc) as svd "
				+ "WHERE 1=1 "
				+ "and u.UserId IN :userIds "
				+ "GROUP BY u.UserId, u.StaffCode, u.EnglishName, u.Team, u.IsGHS, svf.Visitdate, "
				+ "svf.Session, svd.Survey, rank.Code, sRank.Code, svf.supervisoryVisitFormId, rank.rankId "
			    + "ORDER BY u.team, rank.rankId, u.staffCode asc, svf.VisitDate asc";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);

		sqlQuery.setParameter("year", year);
		sqlQuery.setParameterList("userIds", userIds);
		
		sqlQuery.addScalar("userId", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("userStaffCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("userEnglishName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("userTeam", StandardBasicTypes.STRING);
		sqlQuery.addScalar("isGHS", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("month", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("date", StandardBasicTypes.STRING);
		sqlQuery.addScalar("ghsSession", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("survey", StandardBasicTypes.STRING);
		sqlQuery.addScalar("userRankCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("superRankCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("ghsCount", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("dayShiftCount", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("nightShiftCount", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("cpiCount", StandardBasicTypes.INTEGER);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SummaryOfSupervisoryVisitSpotCheckData.class));
		
		return sqlQuery.list();
	}
	
	public List<SummaryOfSupervisoryVisitSpotCheckData> getSupervisoryVisitReportYearlySummary(Integer year, List<Integer> userIds){
		
		String sql = "select "
				+ "MONTH(svf.VisitDate) as month, "
				+ "isNull(Count(distinct(Case when svd.Survey = 'GHS' then svf.SupervisoryVisitFormId end)),0) as ghsCount, "
				+ "isNull(Count(distinct(Case when svd.Survey = 'MRPS' or svd.Survey = 'BMWPS' then svf.SupervisoryVisitFormId end)),0) as cpiCount, "
				+ "isNull(Count(distinct(case when svd.Survey = 'GHS' and svf.Session = 1 then svf.supervisoryVisitFormId end)),0) as dayShiftCount, "
				+ "isNull(Count(distinct(case when svd.Survey = 'GHS' and svf.Session = 2 then svf.supervisoryVisitFormId end)),0) as nightShiftCount "
				+ "from SupervisoryVisitForm as svf "
				+ "INNER JOIN [User] AS u on u.UserId = svf.OfficerId "
				+ "INNER JOIN UserRole AS ur ON u.UserId = ur.UserId "
				+ "INNER JOIN [Rank] AS rank ON rank.RankId = u.RankId "
				+ "INNER JOIN Role AS r ON r.RoleId = ur.RoleId AND r.AuthorityLevel & 16 = 16 "
				+ "OUTER APPLY (select top 1 * from SupervisoryVisitDetail where SupervisoryVisitFormId = svf.SupervisoryVisitFormId ORDER BY svf.SupervisoryVisitFormId asc) as svd "
				+ "where 1 = 1 "
				+ "and u.UserId in (:userIds) "
				+ "and svf.Status = 'Approved' "
				+ "and YEAR(svf.VisitDate) = :year "
				+ "group by Month(svf.VisitDate) ;";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);

		sqlQuery.setParameter("year", year);
		sqlQuery.setParameterList("userIds", userIds);
		
		sqlQuery.addScalar("month", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("ghsCount", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("dayShiftCount", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("nightShiftCount", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("cpiCount", StandardBasicTypes.INTEGER);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SummaryOfSupervisoryVisitSpotCheckData.class));
		
		return sqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getSupervisoryVisitDateByUserId(Integer userId) {
		
		String visitDate = String.format("FORMAT(svf.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select case when svf.visitDate is null then '' else " + visitDate + " end as visitDate "
					+ " from SupervisoryVisitForm as svf "
					+ " left join svf.user as o "
					+ " where o.userId = :userId ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		
		return query.list();
	}	
	
	public Boolean checkSVContinuousMonthsRecordExist(Date date){
		SQLQuery query = this.getSession().createSQLQuery("exec dbo.CheckNonCompletedSVInThreeMonth :date");
		query.setParameter("date", date);		
		query.addScalar("result", StandardBasicTypes.BOOLEAN);
		
		return (Boolean)query.uniqueResult();
	}
}
