package capi.dal;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Disjunction;
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
import capi.entity.SpotCheckForm;
import capi.model.SystemConstant;
import capi.model.batch.QCStatisticModel;
import capi.model.qualityControlManagement.SpotCheckApprovalTableList;
import capi.model.qualityControlManagement.SpotCheckPhoneCallTableList;
import capi.model.qualityControlManagement.SpotCheckResultTableList;
import capi.model.qualityControlManagement.SpotCheckTableList;
import capi.model.report.InformationSpotCheckForm;
import capi.model.report.SummaryOfSupervisoryVisitSpotCheckData;

@Repository("SpotCheckFormDao")
public class SpotCheckFormDao  extends GenericDao<SpotCheckForm>{

	@SuppressWarnings("unchecked")
	public List<SpotCheckTableList> selectAllSpotCheck(String search, int firstRecord, int displayLength, Order order, boolean aboveSupervisor, List<Integer> userId, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("scf")
								.createAlias("scf.officer", "o", JoinType.LEFT_OUTER_JOIN)
								.createAlias("o.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("scf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String spotCheckDate = String.format("FORMAT({alias}.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("scf.spotCheckFormId"), "spotCheckFormId");
		projList.add(Projections.property("o.userId"), "officerId");
		projList.add(Projections.property("o.staffCode"), "officerCode");
		projList.add(Projections.property("o.chineseName"), "officerName");
		projList.add(Projections.property("checker.staffCode"), "checkerCode");
		if(aboveSupervisor) projList.add(Projections.property("sup.staffCode"), "supervisorCode");
		projList.add(SQLProjectionExt.sqlProjection(spotCheckDate + " as spotCheckDate", 
				new String [] {"spotCheckDate"}, new Type[]{StandardBasicTypes.STRING}), "spotCheckDate");
		projList.add(Projections.property("scf.status"), "status");
		
		criteria.setProjection(projList);
		
		if(!aboveSupervisor) {
			criteria.add(
				Restrictions.in("sup.userId", userId)
			);
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(
					Restrictions.ne("checker.rank.rankId", 8)
			);
		}
		
		if (!StringUtils.isEmpty(search)) {
			
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("o.staffCode", "%" + search + "%"));
			disjunction.add(Restrictions.like("o.chineseName", "%" + search + "%"));
			if(aboveSupervisor) disjunction.add(Restrictions.like("sup.staffCode", "%" + search + "%"));
			disjunction.add(Restrictions.sqlRestriction(
					spotCheckDate + " LIKE (?)",
					"%" + search + "%", StandardBasicTypes.STRING));
			disjunction.add(Restrictions.like("scf.status", "%" + search + "%"));
			
			criteria.add(
				/*Restrictions.or(
					Restrictions.like("o.staffCode", "%" + search + "%"),
					Restrictions.like("o.chineseName", "%" + search + "%"),
					Restrictions.like("sup.staffCode", "%" + search + "%"),
					Restrictions.sqlRestriction(
						spotCheckDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("scf.status", "%" + search + "%")
			)*/disjunction);
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckTableList.class));

        return criteria.list();
	}

	public long countSelectAllSpotCheck(String search, boolean aboveSupervisor, List<Integer> userId, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("scf")
								.createAlias("scf.officer", "o", JoinType.LEFT_OUTER_JOIN)
								.createAlias("o.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("scf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String spotCheckDate = String.format("FORMAT({alias}.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		
		if(aboveSupervisor) {
			projList.add(SQLProjectionExt.groupCount(
					Projections.groupProperty("scf.spotCheckFormId"),
					Projections.groupProperty("o.userId"),
					Projections.groupProperty("o.staffCode"),
					Projections.groupProperty("o.chineseName"),
					Projections.groupProperty("sup.staffCode"),
					Projections.groupProperty("checker.staffCode"),
					Projections.groupProperty("scf.spotCheckDate"),
					Projections.groupProperty("scf.status")
			));
		} else {
			projList.add(SQLProjectionExt.groupCount(
					Projections.groupProperty("scf.spotCheckFormId"),
					Projections.groupProperty("o.userId"),
					Projections.groupProperty("o.staffCode"),
					Projections.groupProperty("o.chineseName"),
					Projections.groupProperty("checker.staffCode"),
					Projections.groupProperty("scf.spotCheckDate"),
					Projections.groupProperty("scf.status")
			));
		}

		criteria.setProjection(projList);
		
		if(!aboveSupervisor) {
			criteria.add(
				Restrictions.in("sup.userId", userId)
			);
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(
				Restrictions.ne("checker.rank.rankId", 8)
			);
		}
		
		if (!StringUtils.isEmpty(search)) {
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("o.staffCode", "%" + search + "%"));
			disjunction.add(Restrictions.like("o.chineseName", "%" + search + "%"));
			if(aboveSupervisor) disjunction.add(Restrictions.like("sup.staffCode", "%" + search + "%"));
			disjunction.add(Restrictions.sqlRestriction(
					spotCheckDate + " LIKE (?)",
					"%" + search + "%", StandardBasicTypes.STRING));
			disjunction.add(Restrictions.like("scf.status", "%" + search + "%"));
			
			criteria.add(
				/*Restrictions.or(
					Restrictions.like("o.staffCode", "%" + search + "%"),
					Restrictions.like("o.chineseName", "%" + search + "%"),
					Restrictions.like("sup.staffCode", "%" + search + "%"),
					Restrictions.sqlRestriction(
						spotCheckDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("scf.status", "%" + search + "%")
			)*/disjunction);
		}

        return (long)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<SpotCheckTableList> selectOutstandingSpotCheck(String search, int firstRecord, int displayLength, Order order, boolean aboveSupervisor, List<Integer> userId, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("scf")
								.createAlias("scf.officer", "o", JoinType.LEFT_OUTER_JOIN)
								.createAlias("o.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("scf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String spotCheckDate = String.format("FORMAT({alias}.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("scf.spotCheckFormId"), "spotCheckFormId");
		projList.add(Projections.property("o.userId"), "officerId");
		projList.add(Projections.property("o.staffCode"), "officerCode");
		projList.add(Projections.property("o.chineseName"), "officerName");
		projList.add(Projections.property("checker.staffCode"), "checkerCode");
		if(aboveSupervisor) projList.add(Projections.property("sup.staffCode"), "supervisorCode");
		projList.add(SQLProjectionExt.sqlProjection(spotCheckDate + " as spotCheckDate", 
				new String [] {"spotCheckDate"}, new Type[]{StandardBasicTypes.STRING}), "spotCheckDate");
		projList.add(Projections.property("scf.status"), "status");
		
		criteria.setProjection(projList);
		
		if(!aboveSupervisor) {
			criteria.add(
				Restrictions.in("sup.userId", userId)
			);
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(
					Restrictions.ne("checker.rank.rankId", 8)
			);
		}
		
		if (!StringUtils.isEmpty(search)) {
			
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("o.staffCode", "%" + search + "%"));
			disjunction.add(Restrictions.like("o.chineseName", "%" + search + "%"));
			if(aboveSupervisor) disjunction.add(Restrictions.like("sup.staffCode", "%" + search + "%"));
			disjunction.add(Restrictions.sqlRestriction(
					spotCheckDate + " LIKE (?)",
					"%" + search + "%", StandardBasicTypes.STRING));
			disjunction.add(Restrictions.like("scf.status", "%" + search + "%"));
			
			criteria.add(
				/*Restrictions.or(
					Restrictions.like("o.staffCode", "%" + search + "%"),
					Restrictions.like("o.chineseName", "%" + search + "%"),
					Restrictions.like("sup.staffCode", "%" + search + "%"),
					Restrictions.sqlRestriction(
						spotCheckDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("scf.status", "%" + search + "%")
			)*/disjunction);
		}
		criteria.add(
				Restrictions.or(
					Restrictions.not(
						Restrictions.in("scf.status", new String[] {SystemConstant.SPOTCHECKFORM_STATUS_SUBMITTED,
																	SystemConstant.SPOTCHECKFORM_STATUS_APPROVED} )),
					Restrictions.isNull("scf.status")
				)
		);
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckTableList.class));

        return criteria.list();
	}

	public long countSelectOutstandingSpotCheck(String search, boolean aboveSupervisor, List<Integer> userId, Boolean hasAuthorityViewAllRecord
			,Date fromDate, Date toDate) {

		Criteria criteria = this.createCriteria("scf")
								.createAlias("scf.officer", "o", JoinType.LEFT_OUTER_JOIN)
								.createAlias("o.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("scf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String spotCheckDate = String.format("FORMAT({alias}.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		
		if(aboveSupervisor) {
			projList.add(SQLProjectionExt.groupCount(
					Projections.groupProperty("scf.spotCheckFormId"),
					Projections.groupProperty("o.userId"),
					Projections.groupProperty("o.staffCode"),
					Projections.groupProperty("o.chineseName"),
					Projections.groupProperty("sup.staffCode"),
					Projections.groupProperty("checker.staffCode"),
					Projections.groupProperty("scf.spotCheckDate"),
					Projections.groupProperty("scf.status")
			));
		} else {
			projList.add(SQLProjectionExt.groupCount(
					Projections.groupProperty("scf.spotCheckFormId"),
					Projections.groupProperty("o.userId"),
					Projections.groupProperty("o.staffCode"),
					Projections.groupProperty("o.chineseName"),
					Projections.groupProperty("checker.staffCode"),
					Projections.groupProperty("scf.spotCheckDate"),
					Projections.groupProperty("scf.status")
			));
		}

		criteria.setProjection(projList);
		
		if(!aboveSupervisor) {
			criteria.add(
				Restrictions.in("sup.userId", userId)
			);
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(
				Restrictions.ne("checker.rank.rankId", 8)
			);
		}
		
		if (!StringUtils.isEmpty(search)) {
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("o.staffCode", "%" + search + "%"));
			disjunction.add(Restrictions.like("o.chineseName", "%" + search + "%"));
			if(aboveSupervisor) disjunction.add(Restrictions.like("sup.staffCode", "%" + search + "%"));
			disjunction.add(Restrictions.sqlRestriction(
					spotCheckDate + " LIKE (?)",
					"%" + search + "%", StandardBasicTypes.STRING));
			disjunction.add(Restrictions.like("scf.status", "%" + search + "%"));
			
			criteria.add(
				/*Restrictions.or(
					Restrictions.like("o.staffCode", "%" + search + "%"),
					Restrictions.like("o.chineseName", "%" + search + "%"),
					Restrictions.like("sup.staffCode", "%" + search + "%"),
					Restrictions.sqlRestriction(
						spotCheckDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("scf.status", "%" + search + "%")
			)*/disjunction);
		}
		
		criteria.add(
						Restrictions.or(
							Restrictions.not(
								Restrictions.in("scf.status", new String[] {SystemConstant.SPOTCHECKFORM_STATUS_SUBMITTED,
																			SystemConstant.SPOTCHECKFORM_STATUS_APPROVED} )),
							Restrictions.isNull("scf.status")
		));
		criteria.add(Restrictions.between("scf.spotCheckDate", fromDate, toDate));

        return (long)criteria.uniqueResult();
	}

	public List<SpotCheckPhoneCallTableList> selectSpotCheckPhoneCallListBySpotCheckId(Integer spotCheckId) {

		Criteria criteria = this.createCriteria("scf")
								.createAlias("scf.spotCheckPhoneCalls", "scpc", JoinType.LEFT_OUTER_JOIN);

//		String phoneCallTime = String.format("FORMAT({scpc}.phoneCallTime, '%s', 'en-us')", SystemConstant.TIME_FORMAT);
		
		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("scpc.spotCheckPhoneCallId"), "spotCheckPhoneCallId");
		projList.add(Projections.property("scpc.phoneCallTime"), "phoneCallTimeDate");
//		projList.add(SQLProjectionExt.sqlProjection(phoneCallTime + " as phoneCallTime", 
//				new String [] {"phoneCallTime"}, new Type[]{StandardBasicTypes.STRING}), "phoneCallTime");
		projList.add(Projections.property("scpc.result"), "phoneCallResult");

		criteria.setProjection(projList);
		
		if(spotCheckId != null) {
			criteria.add(Restrictions.eq("scf.spotCheckFormId", spotCheckId));
		}
		
		criteria.addOrder(Order.asc("scpc.phoneCallTime"));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckPhoneCallTableList.class));
		
		return criteria.list();
	}

	public List<SpotCheckResultTableList> selectSpotCheckResultListBySpotCheckId(Integer spotCheckId) {

		Criteria criteria = this.createCriteria("scf")
								.createAlias("scf.spotCheckResults", "scr", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("scr.spotCheckResultId"), "spotCheckResultId");
		projList.add(Projections.property("scr.result"), "result");
		projList.add(Projections.property("scr.otherRemark"), "otherRemark");
		projList.add(Projections.property("scr.referenceNo"), "referenceNo");

		criteria.setProjection(projList);
		
		if(spotCheckId != null) {
			criteria.add(Restrictions.eq("scf.spotCheckFormId", spotCheckId));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckResultTableList.class));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SpotCheckApprovalTableList> selectAllSpotCheckApproval(String search, int firstRecord, int displayLength, Order order
																		, boolean isBusinessData, List<Integer> actedUserIds, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("scf")
								.createAlias("scf.officer", "o", JoinType.LEFT_OUTER_JOIN)
								.createAlias("o.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("scf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String spotCheckDate = String.format("FORMAT({alias}.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("scf.spotCheckFormId"), "spotCheckFormId");
		projList.add(Projections.property("o.userId"), "officerId");
		projList.add(Projections.property("o.staffCode"), "officerCode");
		projList.add(Projections.property("o.chineseName"), "officerName");
		projList.add(Projections.property("o.team"), "officerTeam");
		projList.add(Projections.property("scf.survey"), "survey");
		projList.add(Projections.property("sup.staffCode"), "supervisorCode");
		projList.add(Projections.property("checker.staffCode"), "checkerCode");
		projList.add(SQLProjectionExt.sqlProjection(spotCheckDate + " as spotCheckDate", 
				new String [] {"spotCheckDate"}, new Type[]{StandardBasicTypes.STRING}), "spotCheckDate");
		
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("scf.status", "Submitted"));
		
		if(!isBusinessData) {
			criteria.add(Restrictions.in("scf.submitTo.userId", actedUserIds));
		}
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(Restrictions.ne("checker.rank.rankId", 8));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("o.staffCode", "%" + search + "%"),
					Restrictions.like("o.chineseName", "%" + search + "%"),
					Restrictions.like("o.team", "%" + search + "%"),
					Restrictions.like("scf.survey", "%" + search + "%"),
					Restrictions.like("sup.staffCode", "%" + search + "%"),
					Restrictions.sqlRestriction(
						spotCheckDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckApprovalTableList.class));

        return criteria.list();
	}

	public long countSelectAllSpotCheckApproval(String search, Boolean hasAuthorityViewAllRecord) {

		Criteria criteria = this.createCriteria("scf")
								.createAlias("scf.officer", "o", JoinType.LEFT_OUTER_JOIN)
								.createAlias("o.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
								.createAlias("scf.supervisor", "checker", JoinType.LEFT_OUTER_JOIN);

		String spotCheckDate = String.format("FORMAT({alias}.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("scf.spotCheckFormId"),
				Projections.groupProperty("o.userId"),
				Projections.groupProperty("o.staffCode"),
				Projections.groupProperty("o.chineseName"),
				Projections.groupProperty("o.team"),
				Projections.groupProperty("scf.survey"),
				Projections.groupProperty("sup.staffCode"),
				Projections.groupProperty("checker.staffCode"),
				Projections.groupProperty("scf.spotCheckDate")
		));

		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("scf.status", "Submitted"));
		
		if(!hasAuthorityViewAllRecord){
			criteria.add(Restrictions.ne("checker.rank.rankId", 8));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("o.staffCode", "%" + search + "%"),
					Restrictions.like("o.chineseName", "%" + search + "%"),
					Restrictions.like("o.team", "%" + search + "%"),
					Restrictions.like("scf.survey", "%" + search + "%"),
					Restrictions.like("sup.staffCode", "%" + search + "%"),
					Restrictions.sqlRestriction(
						spotCheckDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
			));
		}

        return (long)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<SpotCheckForm> getSpotCheckFormsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("spotCheckFormId", ids));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SpotCheckForm> getSpotCheckFormsByCheckDate(Date checkDate, Integer userId){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("spotCheckDate", checkDate));
		criteria.add(Restrictions.eq("supervisor.userId", userId));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getSpotCheckDatesByRefMonth(Date refMonth){
		Date refMonthEnd = DateUtils.addMonths(refMonth, 1);
		String spotCheckDate = String.format("FORMAT({alias}.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		Criteria criteria = this.createCriteria("scf");
		criteria.setProjection(
				SQLProjectionExt.sqlProjection(spotCheckDate + " as spotCheckDate", 
						new String [] {"spotCheckDate"}, new Type[]{StandardBasicTypes.STRING})
			);
		criteria.add(Restrictions.between("spotCheckDate", refMonth, refMonthEnd));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<InformationSpotCheckForm> getInformationSpotCheckForm(List<String> survey, List<Integer> officerId, List<String> spotCheckDates){
		String[] status = {SystemConstant.SPOTCHECKFORM_STATUS_APPROVED};
		String spotCheckDate = String.format("format(scf.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String submittedDate = String.format("format(scf.submittedDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String spotCheckDateOutput = String.format("REPLACE(CONVERT(CHAR(15), scf.spotCheckDate, 106),'','')");
		String dateOfChecking = String.format("CONVERT(CHAR(8), scf.spotCheckDate, 112)");
		
		String hql = "select scf.spotCheckFormId as spotCheckFormId, o.englishName as officerName, o.destination as officerDestination, o.staffCode as officerCode "
				+ ", su.englishName as supervisorName, su.destination as supervisorDestination "
				+ ", case when scf.spotCheckDate is null then '' else " + spotCheckDateOutput + " end as spotCheckDate "				
				+ ", scf.timeCallback as timeCallBack, scf.activityBeingPerformed as activityBeingPerformed "
				+ ", scf.interviewReferenceNo as interviewReferenceNo, scf.location as location, scf.caseReferenceNo as caseReferenceNo "
				+ ", scf.remarksForNonContact as remarksForNonContact, scf.scheduledPlace as scheduledPlace "
				+ ", scf.scheduledTime as scheduledTimeDate, scf.turnUpTime as turnUpTimeDate "
				+ ", scf.isReasonable as isReasonable, scf.isIrregular as isIrregular "
				+ ", scf.remarkForTurnUpTime as remarkForTurnUpTime, scf.verCheck1ReferenceNo as verCheck1ReferenceNo "
				+ ", scf.verCheck1IsIrregular as verCheck1IsIrregular, scf.verCheck1Remark as verCheck1Remark "
				+ ", scf.verCheck2ReferenceNo as verCheck2ReferenceNo, scf.verCheck2IsIrregular as verCheck2IsIrregular "
				+ ", scf.verCheck2Remark as verCheck2Remark, scf.survey as survey "
				+ ", st.englishName as approvalUserName, st.destination as approvalUserDestination "
				+ ", case when scf.submittedDate is null then '' else "+submittedDate+" end as submittedDate"
				+ ", case when scf.spotCheckDate is null then '' else "+dateOfChecking+" end as dateOfChecking"
				+ " from SpotCheckForm as scf "
				+ "  left join scf.officer as o "
				+ "  left join scf.supervisor as su "
				+ " left join scf.submitTo as st "
				+ " where 1=1 ";
		
		hql += " and scf.status in (:status) ";
		
		if (survey != null && survey.size() > 0){
			hql += " and scf.survey in (:survey) ";
		}
		if (officerId != null && officerId.size() > 0){
			hql += " and o.userId in (:officerId) ";
		}
		if (spotCheckDates != null && spotCheckDates.size() > 0){
			hql += " and case when scf.spotCheckDate is null then '' else " + spotCheckDate + " end in (:spotCheckDates) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("status", status);
		
		if (survey != null && survey.size() > 0){
			query.setParameterList("survey", survey);
		}
		if (officerId != null && officerId.size() > 0){
			query.setParameterList("officerId", officerId);
		}
		if (spotCheckDates != null && spotCheckDates.size() > 0){
			query.setParameterList("spotCheckDates", spotCheckDates);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(InformationSpotCheckForm.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked") 
	 public List<InformationSpotCheckForm>getInformationSpotCheckFormMapAddress(List<Integer> spotCheckFormId){
		 		
		 String sql = "SELECT scf.SpotCheckFormId as spotCheckFormId, " 
				 +	" ISNULL (o.StreetAddress, scf.[Location]) AS mapAddress "
				 +	" FROM [SpotCheckForm] scf " 
				 +	" LEFT JOIN Assignment a ON scf.InterviewReferenceNo = a.ReferenceNo " 
				 +	" LEFT JOIN Outlet o ON a.OutletId = o.OutletId " 
				 +	" WHERE scf.SpotCheckFormId IN (:spot)";
	  
	  SQLQuery query = this.getSession().createSQLQuery(sql);
	  if (spotCheckFormId != null && spotCheckFormId.size() > 0) {
		  query.setParameterList("spot", spotCheckFormId);
	  }
	  
	  query.addScalar("spotCheckFormId", StandardBasicTypes.INTEGER);
	  query.addScalar("mapAddress", StandardBasicTypes.STRING);
	  
	  query.setResultTransformer(Transformers.aliasToBean(InformationSpotCheckForm.class));
	  
	  return query.list(); 
	  }
	
	public List<QCStatisticModel> getSpotCheckCompleteStatus(){
		String sql = "Select u.userId as userId "
				+ " , count(distinct f.spotCheckFormId) as formCompletedCount "
				+ " , count(distinct ghs.spotCheckFormId) as ghsCompletedCount "
				+ " From [User] as u "
				+ " left join SpotCheckForm as f on f.officerId = u.userId "
					+ " and (f.status = 'Submitted' or f.status = 'Approved') "
					+ " and YEAR(f.spotCheckDate) = YEAR(getDate()) "
				+ " left join SpotCheckForm as ghs on f.spotCheckFormId = ghs.spotCheckFormId "
					+ " and ghs.survey = 'GHS' "
				+ " inner join [UserRole] as ur on u.userId = ur.userId "
				+ " inner join [Role] as r on r.roleId = ur.roleId "
				+ " where (r.AuthorityLevel & 16) = 16 and u.[Status] <> 'Inactive' "
				+ " group by u.userId, u.Team, u.StaffCode"
				+ " order by u.Team, u.StaffCode";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("formCompletedCount", StandardBasicTypes.LONG);
		query.addScalar("ghsCompletedCount", StandardBasicTypes.LONG);
		
		query.setResultTransformer(Transformers.aliasToBean(QCStatisticModel.class));
		
		return query.list();
		
	}
	/*
	public List<SpotCheckFormSyncData> getUpdatedSpotCheckForm(Date lastSyncTime, Integer[] spotCheckFormIds){
		String scheduledTime = String.format("dbo.FormatTime(sc.scheduledTime, '%s')", SystemConstant.TIME_FORMAT);
		String turnUpTime = String.format("dbo.FormatTime(sc.turnUpTime, '%s')", SystemConstant.TIME_FORMAT);
		
		String hql = "select sc.spotCheckFormId as spotCheckFormId"
				+ ", o.userId as officerId, s.userId as supervisorId"
				+ ", sc.spotCheckDate as spotCheckDate"
				+ ", sc.timeCallback as timeCallback"
				+ ", sc.activityBeingPerformed as activityBeingPerformed, sc.interviewReferenceNo as interviewReferenceNo"
				+ ", sc.location as location, sc.caseReferenceNo as caseReferenceNo"
				+ ", sc.remarksForNonContact as remarksForNonContact, sc.scheduledPlace as scheduledPlace"
				+ ", case when sc.scheduledTime is null then '' else "+scheduledTime+" end as scheduledTime"
				+ ", case when sc.turnUpTime is null then '' else "+turnUpTime+" end as turnUpTime"
				+ ", sc.isReasonable as reasonable, sc.isIrregular as irregular"
				+ ", sc.remarkForTurnUpTime as remarkForTurnUpTime, st.userId as submitTo"
				+ ", sc.rejectReason as rejectReason, sc.isSuccessful as successful"
				+ ", sc.unsuccessfulRemark as unsuccessfulRemark, sc.createdDate as createdDate"
				+ ", sc.modifiedDate as modifiedDate, sc.verCheck1ReferenceNo as verCheck1ReferenceNo"
				+ ", sc.verCheck1IsIrregular as verCheck1IsIrregular, sc.verCheck1Remark as verCheck1Remark"
				+ ", sc.verCheck2ReferenceNo as verCheck2ReferenceNo, sc.verCheck2IsIrregular as verCheck2IsIrregular"
				+ ", sc.verCheck2Remark as verCheck2Remark, sc.session as session"
				+ ", sc.status as status, ss.scSvPlanId as scSvPlanId"
				+ ", sc.survey as survey"
				+ " from SpotCheckForm as sc"
				+ " left join sc.officer as o"
				+ " left join sc.supervisor as s"
				+ " left join sc.submitTo as st"
				+ " left join sc.scSvPlan as ss"
				+ " where 1=1"
				+ " and sc.spotCheckFormId in ( :spotCheckFormIds )";
		
		if(lastSyncTime!=null){
			hql += " and sc.modifiedDate >= :modifiedDate";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		
		query.setParameterList("spotCheckFormIds", spotCheckFormIds);
		query.setResultTransformer(Transformers.aliasToBean(SpotCheckFormSyncData.class));
		
		return query.list();
	}*/
	
//	public List<SummaryOfSupervisoryVisitSpotCheckData> getSummaryOfSupervisoryVisitSpotCheckReportData(Integer year, List<Integer> userIds){
//		
//		String sql = "select "
//				+ "u.UserId as userId, "
//				+ "Cast ( u.StaffCode as varchar ) as userStaffCode, "
//				+ "Cast ( u.englishName as varchar ) as userEnglishName, "
//				+ "Cast ( u.chineseName as varchar ) as userChineseName, "
//				+ "Cast ( u.Team as varchar ) as  userTeam, "
//				+ "cast ( u.Destination as varchar) as userDestination, "
//				+ "u.isGHS as isGHS, "
//				+ "CONVERT(int, RIGHT(LEFT(CONVERT(varchar, f.SpotCheckDate,112),6),2)) as month, "
//				+ "LEFT(CONVERT(varchar, f.SpotCheckDate,109),6) as date, "
//				+ "case srf.Name when 'Field Head' then CAST(1 AS BIT) else CAST(0 AS BIT) end as isFieldHead, "
//				+ "case srs.Name when 'Section Head' then CAST(1 AS BIT) else CAST(0 AS BIT) end as isSectionHead, "
//				+ "count (distinct cpi.spotCheckFormId) as cpiCount, "
//				+ "count (distinct ghs.spotCheckFormId) as ghsCount, "
//				+ "f.session "
//				+ "from [dbo].[User] as u "
//				+ "inner join UserRole as ur "
//				+ "on u.UserId = ur.UserId "
//				+ "inner join [Role] as r "
//				+ "on r.RoleId = ur.RoleId and (r.[AuthorityLevel] & 16) = 16 "
//				+ "left join [dbo].[SpotCheckForm] as f  "
//				+ "on u.UserId = f.OfficerId and year(f.spotCheckDate) = :year "
//				+ "left join [dbo].[SpotCheckForm] as ghs "
//				+ "on u.UserId = f.OfficerId and f.SpotCheckDate = ghs.SpotCheckDate and ghs.Survey = 'GHS' "
//				+ "left join [dbo].[SpotCheckForm] as cpi "
//				+ "on u.UserId = f.OfficerId and f.SpotCheckDate = cpi.SpotCheckDate and cpi.Survey = 'CPI' "
//				+ "left join [User] as s "
//				+ "on f.SupervisorId = s.UserId "
//				+ "left join [UserRole] as sur "
//				+ "on sur.UserId = s.UserId "
//				+ "left join [Role] as srf "
//				+ "on srf.RoleId = sur.RoleId and srf.name = 'Field Head' "
//				+ "left join [Role] as srs "
//				+ "on srs.RoleId = sur.RoleId and srs.name = 'Section Head' "
//				+ "where  "
//				+ "u.userId in (:userIds) "
//				+ "group by u.UserId, u.StaffCode, "
//				+ "u.englishName, u.chineseName, "
//				+ "u.Team,u.Destination,u.isGHS, LEFT(CONVERT(varchar, f.SpotCheckDate,112),6),LEFT(CONVERT(varchar, f.SpotCheckDate,109),6), f.session, srf.name, srs.name ";
//		
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

	public List<SummaryOfSupervisoryVisitSpotCheckData> getSummaryOfSupervisoryVisitSpotCheckReportData(Integer year, List<Integer> userIds){
		
		String sql = "select "
				+ "u.UserId as userId, "
				+ "u.StaffCode as userStaffCode, "
				+ "u.EnglishName as userEnglishName, "
				+ "u.Team as userTeam, "
				+ "u.IsGHS as isGHS, "
				+ "Month(scf.SpotCheckDate) as month, "
				+ "Concat(DATENAME(DAY, scf.SpotCheckDate), ' ', Cast(Datename(Month,scf.SpotCheckDate) as Char(3))) as date, "
				+ "scf.Session as ghsSession, "
				+ "scf.Survey as survey, "
				+ "rank.Code as userRankCode, "
				+ "sRank.Code as superRankCode, "
				+ "isNull(Count(case when scf.Survey = 'GHS' then scf.SpotCheckFormId end) OVER (PARTITION BY scf.SpotCheckFormId ),0) as ghsCount,"
				+ "isNull(Count(case when scf.Survey = 'GHS' and scf.Session = 1 then scf.SpotCheckFormId end) OVER (PARTITION BY scf.SpotCheckFormId ),0) as dayShiftCount,"
				+ "isNull(Count(case when scf.Survey = 'GHS' and scf.Session = 2 then scf.SpotCheckFormId end) OVER (PARTITION BY scf.SpotCheckFormId ),0) as nightShiftCount,"
				+ "isNull(Count(case when scf.Survey = 'MRPS' or scf.Survey = 'BMWPS' then scf.SpotCheckFormId end) OVER (PARTITION BY scf.SpotCheckFormId ),0) as cpiCount "
				+ "FROM  [User] AS u LEFT JOIN "
				+ "UserRole AS ur ON u.UserId = ur.UserId LEFT JOIN "
				+ "[Rank] AS rank ON rank.RankId = u.RankId LEFT JOIN "
				+ "Role AS r ON r.RoleId = ur.RoleId AND r.AuthorityLevel & 16 = 16 LEFT JOIN "
				+ "SpotCheckForm AS scf ON u.UserId = scf.OfficerId "
				+ "and YEAR(scf.SpotCheckDate) = :year and scf.Status = 'Approved' LEFT JOIN "
				+ "[User] AS s ON scf.SupervisorId = s.UserId LEFT JOIN "
				+ "[Rank] AS sRank on sRank.RankId = s.RankId "
				+ "WHERE 1=1 "
				+ "and u.UserId IN :userIds "
				+ "GROUP BY u.UserId, u.StaffCode, u.EnglishName, u.Team, u.IsGHS, scf.SpotCheckDate, "
				+ "scf.session, scf.Survey, rank.code, sRank.Code, rank.rankId, scf.SpotCheckFormId "
			    + "ORDER BY u.team, rank.rankId, u.Staffcode asc, scf.SpotCheckDate asc";
		
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
	
	
	public List<SummaryOfSupervisoryVisitSpotCheckData> getSpotCheckReportYearlySummary(Integer year, List<Integer> userIds){
		
		String sql = "select "
				+ "MONTH(sc.SpotCheckDate) as month, "
				+ "isNull(Count(Case when sc.Survey = 'GHS' then sc.SpotCheckFormId end),0) as ghsCount, "
				+ "isNull(Count(Case when sc.Survey = 'MRPS' or sc.Survey = 'BMWPS'   then sc.SpotCheckFormId end),0) as cpiCount,"
				+ "isNull(Count(Case when sc.Survey = 'GHS' and sc.Session = 1 then sc.SpotCheckFormId end),0) as dayShiftCount, "
				+ "isNull(Count(Case when sc.Survey = 'GHS' and sc.Session = 2 then sc.SpotCheckFormId end),0) as nightShiftCount "
				+ "from SpotCheckForm as sc "
				+ "INNER JOIN [User] as u on sc.OfficerId = u.UserId "
				+ "INNER JOIN UserRole AS ur ON u.UserId = ur.UserId "
				+ "INNER JOIN Rank AS rank ON rank.RankId = u.RankId "
				+ "INNER JOIN Role AS r ON r.RoleId = ur.RoleId AND r.AuthorityLevel & 16 = 16 "
				+ "where 1 = 1 "
				+ "and sc.Status = 'Approved' "
				+ "and u.UserId in (:userIds)"
				+ "and YEAR(sc.SpotCheckDate) = :year "
				+ "group by Month(sc.SpotCheckDate)";
		
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
	public List<String> getSpotCheckDateByUserId(Integer userId) {
		
		String spotCheckDate = String.format("FORMAT(scf.spotCheckDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select case when scf.spotCheckDate is null then '' else " + spotCheckDate + " end as spotCheckDate "
					+ " from SpotCheckForm as scf "
					+ " left join scf.officer as o "
					+ " where o.userId = :userId ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		
		return query.list();
	}
	
	public Boolean checkSpotCheckContinuousMonthsRecordExist(Date date){
		SQLQuery query = this.getSession().createSQLQuery("exec dbo.CheckNonCompletedSpotCheckInThreeMonth :date");
		query.setParameter("date", date);		
		query.addScalar("result", StandardBasicTypes.BOOLEAN);
		
		return (Boolean)query.uniqueResult();
	}
}
