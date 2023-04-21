package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.SpotCheckSetup;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.SpotCheckSetupTableList;


@Repository("SpotCheckSetupDao")
public class SpotCheckSetupDao  extends GenericDao<SpotCheckSetup>{

	@SuppressWarnings("unchecked")
	public List<SpotCheckSetupTableList> selectAllSpotCheckSetup(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("scs")
								.createAlias("scs.spotCheckDate", "scd", JoinType.LEFT_OUTER_JOIN);
//								.createAlias("scs.user", "u", JoinType.LEFT_OUTER_JOIN);

		Criteria subCriteria = criteria.createCriteria("scs.user", "u", JoinType.LEFT_OUTER_JOIN);

		String spotCheckDate = String.format("FORMAT({scd}.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String notificationDate = String.format("FORMAT({alias}.notificationDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		criteria.setProjection(
			Projections.projectionList()
			.add(Projections.property("scs.spotCheckSetupId"), "spotCheckSetupId")
			.add(SQLProjectionExt.sqlProjection(spotCheckDate + " as spotCheckDate", 
					new String [] {"spotCheckDate"}, new Type[]{StandardBasicTypes.STRING}), "spotCheckDate")
			.add(Projections.property("u.staffCode"), "fieldOfficerCode")
			.add(Projections.property("u.chineseName"), "chineseName")
			.add(Projections.sqlProjection(
					"case when cast({alias}.notificationDate as date) > cast(getDate() as date) then 1 else 0 end deletable", 
					new String [] {"deletable"}, 
					new Type[]{StandardBasicTypes.BOOLEAN}), "deletable")
			.add(SQLProjectionExt.sqlProjection(notificationDate + " as notificationDate", 
					new String [] {"notificationDate"}, new Type[]{StandardBasicTypes.STRING}), "notificationDate")
		);

		if (!StringUtils.isEmpty(search)) {
			subCriteria.add(
				Restrictions.or(
					Restrictions.sqlRestriction(
						"{alias}.staffCode LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("u.chineseName", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

        criteria.setResultTransformer(Transformers.aliasToBean(SpotCheckSetupTableList.class));

        return criteria.list();
	}

	public long countSelectAllSpotCheckSetup(String search) {

		Criteria criteria = this.createCriteria("scs")
								.createAlias("scs.spotCheckDate", "scd", JoinType.LEFT_OUTER_JOIN);
//								.createAlias("scs.user", "u", JoinType.LEFT_OUTER_JOIN);
		
		Criteria subCriteria = criteria.createCriteria("scs.user", "u", JoinType.LEFT_OUTER_JOIN);

		if (!StringUtils.isEmpty(search)) {
			subCriteria.add(
				Restrictions.or(
					Restrictions.sqlRestriction(
						"{alias}.staffCode LIKE (?)",
						"%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("u.chineseName", "%" + search + "%")
			));
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<SpotCheckSetup> getSpotCheckSetupsByIds(List<Integer> ids, boolean fetchAll){
		Criteria criteria = this.createCriteria();
		if (fetchAll){
			criteria.setFetchMode("scSvplans", FetchMode.JOIN);
		}
		criteria.add(Restrictions.in("spotCheckSetupId", ids));
		
		return criteria.list();
	}

	public List<SpotCheckSetup> getSpotCheckSetupByNotificationDate(Date date){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("notificationDate", date));
		
		return criteria.list();
	}
	
	
	public long countSpotCheckSetupInMonth(Date month){
		Criteria criteria = this.createCriteria("sp")
				.createAlias("sp.spotCheckDate", "d")
				.createAlias("d.surveyMonth", "s");
		criteria.add(Restrictions.eq("s.referenceMonth", month));
		criteria.setProjection(Projections.rowCount());
		
		return (long)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getSpotCheckDateByUserId(Integer userId) {
		
		String spotCheckDate = String.format("FORMAT(scd.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select case when scd.date is null then '' else " + spotCheckDate + " end as spotCheckDate "
					+ " from SpotCheckSetup as scs "
					+ " left join scs.spotCheckDate as scd "
					+ " left join scs.user as u "
					+ " where u.userId = :userId ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		
		return query.list();
	}
}
