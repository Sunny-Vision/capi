package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.AssignmentAttribute;
import capi.entity.SurveyMonth;
import capi.model.api.dataSync.AssignmentAttributeSyncData;

@Repository("AssignmentAttributeDao")
public class AssignmentAttributeDao  extends GenericDao<AssignmentAttribute>{
	public List<AssignmentAttribute> findAssignmentAttributeBySurveyMonth(SurveyMonth surveyMonth){
		Criteria criteria = this.createCriteria("aa");
		
		criteria.add(Restrictions.eq("surveyMonth", surveyMonth));
		
		return criteria.list();
		
	}
	
	public List<AssignmentAttribute> findAssignmentAttributeWithDependency(SurveyMonth surveyMonth){
		String hql = "select aa "
				+ " from AssignmentAttribute as aa"
				+ " left join fetch aa.batch as b "
				//+ " left join fetch aa.batchCollectionDates as bc "
				+ " left join fetch aa.user as u "
				+ " left join fetch aa.allocationBatch as ab "
				+ " where aa.surveyMonth = :surveyMonth ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("surveyMonth", surveyMonth);
		
		return query.list();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentAttribute> findAssignmentAttributeByBatchAndSurveyMonth(int batchId, int surveyMonthId) {
//		return this.createCriteria("attr")
//				.add(Restrictions.eq("attr.batch.id", batchId))
//				.add(Restrictions.eq("attr.surveyMonth.id", surveyMonthId))
//				.list();
		String hql = "select aa "
				+ " from AssignmentAttribute as aa"
				+ " where aa.surveyMonth.id = :surveyMonthId "
				+ " and aa.batch.id = :batchId"
				+ " order by aa.assignmentAttributeId asc";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameter("batchId", batchId);
		
		return query.list();
	}
	
	
	
	public List<AssignmentAttribute> getNoFieldRelatedAttribute(Date date){
		String hql = "select attr "
				+ " from AssignmentAttribute as attr "
				+ " inner join attr.batch as b "
				+ " inner join b.quotations as q on q.status != 'Inactive' "
				+ " left join q.indoorQuotationRecords as iqr "
				+ " left join attr.batchCollectionDates as dates "
				+ " where iqr.indoorQuotationRecordId is null and (attr.endDate <= :today or dates.date <= dateadd(day, 1, :today)) ";
		Query query = this.getSession().createQuery(hql);
		query.setParameter("today", date);
		
		return query.list();
	}
	
	public List<AssignmentAttributeSyncData> getUpdatedAssignmentAttribute(Date lastSyncTime){
		Criteria criteria = this.createCriteria("aa")
				.createAlias("aa.allocationBatch", "ab", JoinType.LEFT_OUTER_JOIN)
				.createAlias("aa.user", "u", JoinType.LEFT_OUTER_JOIN)
				.createAlias("aa.batch", "b", JoinType.LEFT_OUTER_JOIN)
				.createAlias("aa.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.ge("aa.modifiedDate", lastSyncTime));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("aa.assignmentAttributeId"), "assignmentAttributeId");
		projList.add(Projections.property("aa.batchCategory"), "batchCategory");
		projList.add(Projections.property("aa.startDate"), "startDate");
		projList.add(Projections.property("aa.endDate"), "endDate");
		projList.add(Projections.property("aa.session"), "session");
		projList.add(Projections.property("ab.allocationBatchId"), "allocationBatchId");
		projList.add(Projections.property("u.userId"), "userId");
		projList.add(Projections.property("aa.createdDate"), "createdDate");
		projList.add(Projections.property("aa.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("b.batchId"), "batchId");
		projList.add(Projections.property("sm.surveyMonthId"), "surveyMonthId");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(AssignmentAttributeSyncData.class));
		return criteria.list();
	}
}
