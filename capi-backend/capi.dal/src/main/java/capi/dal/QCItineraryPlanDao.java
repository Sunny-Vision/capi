package capi.dal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.QCItineraryPlan;
import capi.model.SystemConstant;
import capi.model.api.dataSync.PECheckFormSyncData;
import capi.model.api.dataSync.QCItineraryPlanSyncData;
import capi.model.api.dataSync.SpotCheckFormSyncData;
import capi.model.api.dataSync.SupervisoryVisitFormSyncData;
import capi.model.api.onlineFunction.QCItineraryPlanOnlineModel;
import capi.model.itineraryPlanning.QCItineraryPlanApprovalTableList;
import capi.model.itineraryPlanning.QCItineraryPlanTableList;

@Repository("QCItinerayPlanDao")
public class QCItineraryPlanDao  extends GenericDao<QCItineraryPlan>{

	@SuppressWarnings("unchecked")
	public List<QCItineraryPlanTableList> getTableList(String search,
			int firstRecord, int displayLenght, Order order, Integer userId) {

		String date = String.format("FORMAT(q.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select q.qcItineraryPlanId as id, "
                + " case when q.date is null then '' else "+date+" end as date, "
                + " q.status as status, "
                + " count(distinct sc.qcItineraryPlanItemId) as scCount, "
                + " count(distinct sv.qcItineraryPlanItemId) as svCount, "
                + " count(distinct pe.qcItineraryPlanItemId) as peCount "
                + " from QCItineraryPlan as q "
                + " left join q.qcItineraryPlanItems as sc on sc.itemType = 1 "
                + " left join q.qcItineraryPlanItems as sv on sv.itemType = 2 "
                + " left join q.qcItineraryPlanItems as pe on pe.itemType = 3 "
                + " where 1=1 ";
        if (!StringUtils.isEmpty(search)){
        	hql += " and (str(q.qcItineraryPlanId) like :search or "+date+" like :search "
                + " or q.status like :search "
                + " ) ";
        }
        if (userId != null){
        	hql += " and q.user.userId = :userId ";
        }
        hql += " group by q.qcItineraryPlanId, q.date, q.status "
                + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));			
		}
		if (userId != null){
			query.setParameter("userId", userId);			
		}
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);
		query.setResultTransformer(Transformers.aliasToBean(QCItineraryPlanTableList.class));

		return query.list();
	}
	
	public long countTableList(String search, Integer userId) {

		String planDate = String.format("FORMAT(q.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select count(distinct q.qcItineraryPlanId) "
                + " from QCItineraryPlan as q "
                + " where 1=1 ";
        if (!StringUtils.isEmpty(search)){
        	hql += " and (str(q.qcItineraryPlanId) like :search or "+planDate+" like :search "
                    + " or q.status like :search"
                    + " ) ";
        }
        if (userId != null){
        	hql += " and q.user.userId = :userId ";
        }

		Query query = getSession().createQuery(hql);
		 if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));			 
		 }
		
		if (userId != null){
			query.setParameter("userId", userId);			
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<QCItineraryPlan> getQCItineraryByIds(List<Integer> planIds) {
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.in("qcItineraryPlanId", planIds));
		return (List<QCItineraryPlan>) criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QCItineraryPlanApprovalTableList> getApprovalTableList(String search,
			int firstRecord, int displayLenght, Order order, List<Integer> userIds) {

		String date = String.format("FORMAT(q.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select q.qcItineraryPlanId as id, "
                + " case when q.date is null then '' else "+date+" end as date, "
                + " concat ( sf.staffCode , ' - ' , sf.chineseName ) as submitFrom, "
                + " concat ( st.staffCode , ' - ' , st.chineseName ) as submitTo, "
                + " q.status as status "
                + " from QCItineraryPlan as q "
                + " left join q.user sf "
                + " left join q.submitTo st "
                + " where 1=1 and q.status = 'Submitted' ";
		if (!StringUtils.isEmpty(search)){
			  hql += "  and (str(q.qcItineraryPlanId) like :search or "+date+" like :search "
		                + " or q.status like :search"
		                + " ) ";
		}
		if (userIds != null && userIds.size() > 0){
			hql += " and q.submitTo.userId in (:userIds) ";
		}
              
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));			
		}
		if (userIds != null && userIds.size() > 0){
			query.setParameterList("userIds", userIds);
		}
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);
		query.setResultTransformer(Transformers.aliasToBean(QCItineraryPlanApprovalTableList.class));

		return query.list();
	}
	
	public long countApprovalTableList(String search, List<Integer> userIds) {

		String planDate = String.format("FORMAT(q.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select count(distinct q.qcItineraryPlanId) "
                + " from QCItineraryPlan as q "
                + " where 1=1  and q.status = 'Submitted' ";
		if (!StringUtils.isEmpty(search)){
			  hql += " and (str(q.qcItineraryPlanId) like :search or "+planDate+" like :search "
		                + " or q.status like :search"
		                + " ) ";
		}
		if (userIds != null && userIds.size() > 0){
			hql += " and q.submitTo.userId in (:userIds) ";
		}

		Query query = getSession().createQuery(hql);

		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));			
		}
		if (userIds != null && userIds.size() > 0){
			query.setParameterList("userIds", userIds);
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	
	@SuppressWarnings("unchecked")
	public List<Date> getPlanDateByUserId(Integer officerId) {
		
		Criteria criteria = this.createCriteria("p").createAlias("p.user","u", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("p.date"),"date");
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		
		criteria.setProjection(projList).add(Restrictions.eq("u.userId", officerId));
		criteria.setProjection(projList).add(Restrictions.ge("p.date", today));

		criteria.addOrder(Order.asc("p.date"));

		return (List<Date>) criteria.list();
	}
	
	public List<String> getFuturePlanDate(Date today, Integer userId){
		String planDate = String.format("FORMAT(date, '%s', 'en-us') as date", SystemConstant.DATE_FORMAT);
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("date", today));
		criteria.add(Restrictions.eq("user.userId", userId));
		criteria.setProjection(SQLProjectionExt.sqlProjection(planDate, new String[]{"date"}, new Type[]{StandardBasicTypes.STRING}));
		
		return criteria.list();
	}
	
	public List<QCItineraryPlanSyncData> getUpdateQCItineraryPlan(Date lastSyncTime, Integer[] qcItineraryPlanIds){
		Criteria criteria = this.createCriteria("q")
				.createAlias("q.user", "u", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.submitTo", "s", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.and(
				Restrictions.ge("q.modifiedDate", lastSyncTime)
				, Restrictions.in("q.qcItineraryPlanId", qcItineraryPlanIds)));
			
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("q.qcItineraryPlanId"), "qcItineraryPlanId");
		projList.add(Projections.property("q.date"), "date");
		projList.add(Projections.property("q.session"), "session");
		projList.add(Projections.property("q.rejectReason"), "rejectReason");
		projList.add(Projections.property("q.createdDate"), "createdDate");
		projList.add(Projections.property("q.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("q.status"), "status");
		projList.add(Projections.property("u.userId"), "userId");
		projList.add(Projections.property("s.userId"), "submitTo");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(QCItineraryPlanSyncData.class));
		return criteria.list();
	}
	
	public List<QCItineraryPlanOnlineModel> houseKeepQCItineraryPlan(Integer userId){
		String hql = "select qc.qcItineraryPlanId as qcItineraryPlanId"
				+ ", qc.date as date, qc.session as session"
				+ ", qc.rejectReason as rejectReason, qc.createdDate as createdDate"
				+ ", qc.modifiedDate as modifiedDate, qc.status as status"
				+ ", u.userId as userId, s.userId as submitTo"
				+ " from QCItineraryPlan as qc"
				+ " left join qc.user as u"
				+ " left join qc.submitTo as s"
				+ " where u.userId = :userId"
				+ " and qc.status = :status"
				+ " and qc.date >= convert(date, getDate())";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("userId", userId);
		query.setParameter("status", "Approved");
		query.setResultTransformer(Transformers.aliasToBean(QCItineraryPlanOnlineModel.class));
		
		return query.list();
	}
	
	public List<SpotCheckFormSyncData> getUpdatedSpotCheckForm(Date lastSyncTime, Integer[] spotCheckFormIds, Integer[] qcItineraryPlanIds){
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
				+ " from QCItineraryPlan as qc"
				+ " left join qc.qcItineraryPlanItems as qci"
				+ " left join qci.spotCheckForm as sc"
				+ " left join sc.officer as o"
				+ " left join sc.supervisor as s"
				+ " left join sc.submitTo as st"
				+ " left join sc.scSvPlan as ss"
				+ " where 1=1";
				
		if(lastSyncTime!=null){
			hql += " and sc.modifiedDate >= :modifiedDate";
		}
		
		List<String> condition = new ArrayList<String>();
		
		if(spotCheckFormIds!=null && spotCheckFormIds.length>0){
			condition.add(" sc.spotCheckFormId in ( :spotCheckFormIds )");
		}
		if(qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
			condition.add(" qc.qcItineraryPlanId in ( :qcItineraryPlanIds )");
		}
		String cond = StringUtils.join(condition.toArray(new String[0]), " or ");
		if(!StringUtils.isEmpty(cond)){
			hql += " and ( "+ cond + ") ";
		}
		
		hql += " group by sc.spotCheckFormId"
				+ ", o.userId, s.userId"
				+ ", sc.spotCheckDate"
				+ ", sc.timeCallback"
				+ ", sc.activityBeingPerformed, sc.interviewReferenceNo"
				+ ", sc.location, sc.caseReferenceNo"
				+ ", sc.remarksForNonContact, sc.scheduledPlace"
				+ ", sc.scheduledTime"
				+ ", sc.turnUpTime"
				+ ", sc.isReasonable, sc.isIrregular"
				+ ", sc.remarkForTurnUpTime, st.userId"
				+ ", sc.rejectReason, sc.isSuccessful"
				+ ", sc.unsuccessfulRemark, sc.createdDate"
				+ ", sc.modifiedDate, sc.verCheck1ReferenceNo"
				+ ", sc.verCheck1IsIrregular, sc.verCheck1Remark"
				+ ", sc.verCheck2ReferenceNo, sc.verCheck2IsIrregular"
				+ ", sc.verCheck2Remark, sc.session"
				+ ", sc.status, ss.scSvPlanId"
				+ ", sc.survey";		
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		if(spotCheckFormIds!=null && spotCheckFormIds.length>0){
			query.setParameterList("spotCheckFormIds", spotCheckFormIds);
		}
		if(qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
			query.setParameterList("qcItineraryPlanIds", qcItineraryPlanIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SpotCheckFormSyncData.class));
		
		return query.list();
	}
	
	public List<SupervisoryVisitFormSyncData> getUpdatedSupervisoryVisitForm(Date lastSyncTime, Integer[] supervisoryVisitFormIds, Integer[] qcItineraryPlanIds){
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
				+ " from QCItineraryPlan as qc"
				+ " left join qc.qcItineraryPlanItems as qci"
				+ " left join qci.supervisoryVisitForm as sv"
				+ " left join sv.user as o"
				+ " left join sv.supervisor as s"
				+ " left join sv.submitTo as st"
				+ " left join sv.scSvPlan as ss"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and sv.modifiedDate >= :modifiedDate";
		}
		
		List<String> condition = new ArrayList<String>();
		if(supervisoryVisitFormIds!=null && supervisoryVisitFormIds.length>0){
			condition.add(" sv.supervisoryVisitFormId in ( :supervisoryVisitFormIds )");
		}
		if(qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
			condition.add(" qc.qcItineraryPlanId in ( :qcItineraryPlanIds )");
		}
		String cond = StringUtils.join(condition.toArray(new String[0]), " or ");
		if (!StringUtils.isEmpty(cond)){
			hql += " and ( "+cond+" )";
		}
		
		hql += " group by sv.supervisoryVisitFormId"
				+ ", o.userId, s.userId"
				+ ", sv.session, sv.visitDate"
				+ ", sv.fromTime"
				+ ", sv.toTime"
				+ ", sv.rejectReason, sv.discussionDate"
				+ ", sv.remark, sv.knowledgeOfWorkResult"
				+ ", sv.knowledgeOfWorkRemark, sv.interviewTechniqueResult"
				+ ", sv.interviewTechniqueRemark, sv.handleDifficultInterviewResult"
				+ ", sv.handleDifficultInterviewRemark, sv.dataRecordingResult"
				+ ", sv.dataRecordingRemark, sv.localGeographyResult"
				+ ", sv.localGeographyRemark, sv.mannerWithPublicResult"
				+ ", sv.mannerWithPublicRemark, sv.judgmentResult"
				+ ", sv.judgmentRemark, sv.organizationOfWorkResult"
				+ ", sv.organizationOfWorkRemark, sv.otherResult"
				+ ", sv.otherRemark, sv.createdDate"
				+ ", sv.modifiedDate, st.userId"
				+ ", sv.status, ss.scSvPlanId";
		
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		if(supervisoryVisitFormIds!=null && supervisoryVisitFormIds.length>0){
			query.setParameterList("supervisoryVisitFormIds", supervisoryVisitFormIds);
		}
		if(qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
			query.setParameterList("qcItineraryPlanIds", qcItineraryPlanIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SupervisoryVisitFormSyncData.class));
		return query.list();
		
	}
	
	public List<PECheckFormSyncData> getUpdatedPECheckForm(Date lastSyncTime, Integer[] peCheckFormIds, Integer[] qcItineraryPlanIds){
		String checkingTime = String.format("dbo.FormatTime(pe.checkingTime, '%s')", SystemConstant.TIME_FORMAT);
		
		String hql = "select pe.peCheckFormId as peCheckFormId"
				+ ", pe.contactPerson as contactPerson, pe.checkingDate as checkingDate"
				+ ", case when pe.checkingTime is null then '' else "+checkingTime+" end as checkingTime"
				+ ", a.assignmentId as assignmentId, o.userId as officerId"
				+ ", pe.checkingMode as checkingMode, pe.peCheckRemark as peCheckRemark"
				+ ", pe.otherRemark as otherRemark, pe.status as status"
				+ ", pe.isNonContact as isNonContact, pe.createdDate as createdDate"
				+ ", pe.modifiedDate as modifiedDate, pe.contactDateResult as contactDateResult"
				+ ", pe.contactTimeResult as contactTimeResult, pe.contactDurationResult as contactDurationResult"
				+ ", pe.contactModeResult as contactModeResult, pe.dateCollectedResult as dateCollectedResult"
				+ ", pe.othersResult as othersResult, u.userId as userId"
				+ " from QCItineraryPlan as qc"
				+ " left join qc.qcItineraryPlanItems as qci"
				+ " left join qci.peCheckForm as pe"
				+ " left join pe.assignment as a"
				+ " left join pe.officer as o"
				+ " left join pe.user as u"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and pe.modifiedDate >= :modifiedDate";
		}
		
		hql += " and (";
		
		if(peCheckFormIds!=null && peCheckFormIds.length>0){
			hql += " pe.peCheckFormId in ( :peCheckFormIds )";
		}
		
		if(peCheckFormIds!=null && peCheckFormIds.length>0 && qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
			hql += " or";
		}
		
		if(qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
			hql += " qc.qcItineraryPlanId in ( :qcItineraryPlanIds )";
		}
		
		hql += " )";
		
		hql += " group by pe.peCheckFormId, pe.contactPerson"
				+ ", pe.checkingDate, pe.checkingTime"
				+ ", a.assignmentId, o.userId"
				+ ", pe.checkingMode, pe.peCheckRemark"
				+ ", pe.otherRemark, pe.status"
				+ ", pe.isNonContact, pe.createdDate"
				+ ", pe.modifiedDate, pe.contactDateResult"
				+ ", pe.contactTimeResult, pe.contactDurationResult"
				+ ", pe.contactModeResult, pe.dateCollectedResult"
				+ ", pe.othersResult, u.userId";
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		
		if(peCheckFormIds!=null && peCheckFormIds.length>0){
			query.setParameterList("peCheckFormIds", peCheckFormIds);
		}
		
		if(qcItineraryPlanIds!=null && qcItineraryPlanIds.length>0){
			query.setParameterList("qcItineraryPlanIds", qcItineraryPlanIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(PECheckFormSyncData.class));
		return query.list();
	}
}
