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

import capi.entity.ItineraryPlanOutlet;
import capi.model.api.dataSync.ItineraryPlanOutletSyncData;

@Repository("ItineraryPlanOutletDao")
public class ItineraryPlanOutletDao  extends GenericDao<ItineraryPlanOutlet>{

	@SuppressWarnings("unchecked")
	public List<ItineraryPlanOutlet> getOuletByMajorLocationId(Integer majorLocationId) {
		Criteria criteria = this.createCriteria("ipo");
		criteria.createAlias("ipo.majorLocation", "ml");
		criteria.add(Restrictions.eq("ml.majorLocationId", majorLocationId));
		return (List<ItineraryPlanOutlet>) criteria.list();
	}
	
	public List<ItineraryPlanOutletSyncData> getUpdateItineraryPlanOutlet(Date lastSyncTime, Integer[] itineraryPlanIds){
		Criteria criteria = this.createCriteria("ipo")
				.createAlias("ipo.outlet", "o", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ipo.itineraryPlan", "ip", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ipo.majorLocation", "ml", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ipo.assignment", "a", JoinType.LEFT_OUTER_JOIN);
		
		if(lastSyncTime==null){
			criteria.add(Restrictions.in("ip.itineraryPlanId", itineraryPlanIds));
		} else {
			criteria.add(Restrictions.and(
					Restrictions.ge("ipo.modifiedDate", lastSyncTime)
					, Restrictions.in("ip.itineraryPlanId", itineraryPlanIds)));
		}
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("ipo.itineraryPlanOutletId"), "itineraryPlanOutletId");
		projList.add(Projections.property("o.outletId"), "outletId");
		projList.add(Projections.property("ip.itineraryPlanId"), "itineraryPlanId");
		projList.add(Projections.property("ipo.createdDate"), "createdDate");
		projList.add(Projections.property("ipo.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("ml.majorLocationId"), "majorLocationId");
		projList.add(Projections.property("ipo.sequence"), "sequence");
		projList.add(Projections.property("ipo.majorLocationSequence"), "majorLocationSequence");
		projList.add(Projections.property("ipo.firmCode"), "firmCode");
		projList.add(Projections.property("ipo.referenceNo"), "referenceNo");
		projList.add(Projections.property("a.assignmentId"), "assignmentId");
		projList.add(Projections.property("ipo.planType"), "planType");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(ItineraryPlanOutletSyncData.class));
		return criteria.list();
	}
	
	
	public List<ItineraryPlanOutlet> getItineraryPlanOutlet(Integer userId, Date planDate){
		String hql = " select o "
				+ " from ItineraryPlan as plan "
				+ " left join plan.outlets as o "
				+ " left join fetch o.majorLocation as ml "
				+ " where plan.date = :planDate and plan.user.userId = :userId ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("planDate", planDate);
		query.setParameter("userId", userId);
		return query.list();
		
	}
}
