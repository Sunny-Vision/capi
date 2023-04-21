package capi.dal;

import java.util.Date;
import java.util.List;

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
import capi.entity.SurveyMonth;
import capi.model.SystemConstant;
import capi.model.api.dataSync.SurveyMonthSyncData;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.SurveyMonthListModel;
import capi.model.qualityControlManagement.PostEnumerationCertaintyCaseTableList;
import capi.model.qualityControlManagement.SpotCheckDateTableList;


@Repository("SurveyMonthDao")
public class SurveyMonthDao  extends GenericDao<SurveyMonth>{

	@SuppressWarnings("unchecked")
	public List<SpotCheckDateTableList> selectAllSpotCheckDate(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("sm")
								.createAlias("sm.spotCheckDates", "scd", JoinType.LEFT_OUTER_JOIN);

		String referenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);

		criteria.setProjection(
				Projections.projectionList()
				.add(SQLProjectionExt.sqlProjection(referenceMonth + " as referenceMonth", "{alias}.referenceMonth", 
						new String [] {"referenceMonth"}, new Type[]{StandardBasicTypes.STRING}), "referenceMonth")
				.add(Projections.count("scd.date"), "noOfDays")
				.add(Projections.groupProperty("sm.surveyMonthId"), "surveyMonthId")
			);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.sqlRestriction(
						referenceMonth + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
			);
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckDateTableList.class));

        return criteria.list();
	}

	public long countSelectAllSpotCheckDate(String search) {

		Criteria criteria = this.createCriteria("sm")
								.createAlias("sm.spotCheckDates", "scd", JoinType.LEFT_OUTER_JOIN);

		String referenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("sm.referenceMonth"),
				Projections.groupProperty("sm.surveyMonthId")
		));

		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.sqlRestriction(
						referenceMonth + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
			);
		}

		return (long)criteria.uniqueResult();
	}
	
	public SurveyMonth getSurveyMonthByDate(Date date) {

		Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.le("startDate", date))
		.add(Restrictions.ge("endDate", date));

		criteria.setMaxResults(1);

		return (SurveyMonth)criteria.uniqueResult();
	}

	public SurveyMonth getSurveyMonthByReferenceMonth(Date month){

		Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("referenceMonth", month));

		return (SurveyMonth)criteria.uniqueResult();
	}

	
	public SurveyMonth getLatestSurveyMonthByPublishDate(Date date){
		Criteria criteria = this.createCriteria("s").createAlias("s.closingDate", "c");
		
		criteria.add(Restrictions.eq("c.publishDate", date));
		
		criteria.addOrder(Order.desc("s.referenceMonth"));
		
		criteria.setMaxResults(1);
		
		return (SurveyMonth)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<SurveyMonthListModel> listSurveyMonth(String search,
			int firstRecord, int displayLenght, Order order) {

		String hql = "select sm.surveyMonthId as surveyMonthId,"
						+ " sm.referenceMonth as referenceMonth,"
						+ " sm.startDate as startDate,"
						+ " sm.endDate as endDate,"
						+ " case when sm.status is not null then sm.status else -1 end as status,"
						+ " cd.closingDate as closingDate"
						+ " from SurveyMonth sm"
						+ " left join sm.closingDate as cd"
						+ " where FORMAT(sm.referenceMonth, '"+SystemConstant.MONTH_FORMAT+"', 'en-us') like :search"
						+ " or FORMAT(sm.startDate, '"+SystemConstant.DATE_FORMAT+"', 'en-us') like :search"
						+ " or FORMAT(sm.endDate, '"+SystemConstant.DATE_FORMAT+"', 'en-us') like :search"
						+ " or FORMAT(cd.closingDate, '"+SystemConstant.DATE_FORMAT+"', 'en-us') like :search"
						+ " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);
		
		query.setParameter("search", String.format("%%%s%%", search));
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);
		
		query.setResultTransformer(Transformers.aliasToBean(SurveyMonthListModel.class));
		
		return query.list();
	}

	public long countSurveyMonth(String search) {
		String hql = "select count(*)"
				+ " from SurveyMonth sm"
				+ " left join sm.closingDate as cd"
				+ " where FORMAT(sm.startDate, '%s', 'en-us') like :search"
				+ " or FORMAT(sm.endDate, '%s', 'en-us') like :search"
				+ " or FORMAT(cd.closingDate, '%s', 'en-us') like :search";

			Query query = getSession().createQuery(hql);
			
			query.setParameter("search", String.format("%%%s%%", search));
			
			return (long) query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<PostEnumerationCertaintyCaseTableList> selectAllPostEnumerationCertaintyCase(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("sm");

		String referenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		String startDate = String.format("FORMAT({alias}.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT({alias}.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		criteria.setProjection(
				Projections.projectionList()
				.add(SQLProjectionExt.sqlProjection(referenceMonth + " as referenceMonth",
						new String [] {"referenceMonth"}, new Type[]{StandardBasicTypes.STRING}), "referenceMonth")
				.add(SQLProjectionExt.sqlProjection(startDate + " as startDate",
						new String [] {"startDate"}, new Type[]{StandardBasicTypes.STRING}), "startDate")
				.add(SQLProjectionExt.sqlProjection(endDate + " as endDate",
						new String [] {"endDate"}, new Type[]{StandardBasicTypes.STRING}), "endDate")
				.add(Projections.property("sm.surveyMonthId"), "surveyMonthId")
			);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.sqlRestriction(
						referenceMonth + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.sqlRestriction(
							startDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.sqlRestriction(
							endDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(PostEnumerationCertaintyCaseTableList.class));

        return criteria.list();
	}

	public long countSelectAllPostEnumerationCertaintyCase(String search) {

		Criteria criteria = this.createCriteria("sm");

		String referenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		String startDate = String.format("FORMAT({alias}.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT({alias}.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("sm.referenceMonth"),
				Projections.groupProperty("sm.startDate"),
				Projections.groupProperty("sm.endDate"),
				Projections.groupProperty("sm.surveyMonthId")
		));

		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.sqlRestriction(
						referenceMonth + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.sqlRestriction(
							startDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.sqlRestriction(
							endDate + " LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING)
			));
		}

        return (long)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<SurveyMonth> searchForAssignmentMaintenance(String search,
			List<Integer> userIds,
			int firstRecord, int displayLength) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select surveyMonth.surveyMonthId as surveyMonthId, "
				+ " surveyMonth.startDate as startDate, "
				+ " surveyMonth.endDate as endDate "
                + " from Assignment as a "
                + " inner join a.surveyMonth as surveyMonth "
                + " left join a.user as user "
                + " where 1 = 1 ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.id in (:userIds) ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ( "
	                + " " + startDateFormat + " like :search or "
	                + " " + endDateFormat + " like :search"
	                + " ) ";
		}
		
		hql += " group by surveyMonth.surveyMonthId, surveyMonth.startDate, surveyMonth.endDate ";
		
//		hql += " order by surveyMonth.startDate asc";
		hql += " order by surveyMonth.startDate desc";

		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(SurveyMonth.class));

		return query.list();
	}
	
	public long countSearchForAssignmentMaintenance(String search, List<Integer> userIds) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String hql = "select count(distinct surveyMonth.surveyMonthId) "
                + " from Assignment as a "
                + " inner join a.surveyMonth as surveyMonth "
                + " left join a.user as user "
                + " where 1 = 1 ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.id in (:userIds) ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ( "
	                + " " + startDateFormat + " like :search or "
	                + " " + endDateFormat + " like :search"
	                + " ) ";
		}
		
		//hql += " group by surveyMonth.surveyMonthId ";
		
		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public SurveyMonth getLatestSurveyMonth(){
		Criteria criteria = this.createCriteria();
		criteria.addOrder(Order.desc("endDate"));
		criteria.setMaxResults(1);
		return (SurveyMonth)criteria.uniqueResult();
	}
	
	public List<SurveyMonthSyncData> getUpdateSurvey(Date lastSyncTime){
		Criteria criteria = this.createCriteria("s")
				.createAlias("s.closingDate", "c", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("s.surveyMonthId"), "surveyMonthId");
		projList.add(Projections.property("s.referenceMonth"), "referenceMonth");
		projList.add(Projections.property("s.createdDate"), "createdDate");
		projList.add(Projections.property("s.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("c.closingDateId"), "closingDateId");
		projList.add(Projections.property("s.startDate"), "startDate");
		projList.add(Projections.property("s.endDate"), "endDate");
		projList.add(Projections.property("s.status"), "status");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(SurveyMonthSyncData.class));
		return criteria.list();
	}
	
	public SurveyMonth findByDate(Date date){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.and(Restrictions.le("startDate", date),  Restrictions.ge("endDate", date)));
		return (SurveyMonth) criteria.uniqueResult();
	}
	
	public void deleteSurveyMonth(Integer surveyMonthId) {

		SQLQuery query = this.getSession().createSQLQuery("exec [DeleteSurveyMonth] :surveyMonthId");
		query.setParameter("surveyMonthId", surveyMonthId);
	}
	
	@SuppressWarnings("unchecked")
	public List<SurveyMonth> searchSurveyMonth(String search, int firstRecord, int displayLength) {
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.desc("surveyMonthId"));
		
		String referenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("referenceMonth LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return criteria.list();
	}

	public long countSearchSurveyMonth(String search) {
		Criteria criteria = this.createCriteria();

		String referenceMonth = String.format("FORMAT({alias}.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("referenceMonth LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
}
