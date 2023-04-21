package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.MajorLocation;
import capi.model.api.dataSync.MajorLocationSyncData;

@Repository("MajorLocationDao")
public class MajorLocationDao  extends GenericDao<MajorLocation>{

	@SuppressWarnings("unchecked")
	public List<MajorLocation> getMajorLocationByItineraryPlanId(Integer itineraryPlanId) {
		Criteria criteria = this.createCriteria("ml");
		criteria.createAlias("ml.itineraryPlan", "ip");
		criteria.add(Restrictions.eq("ip.itineraryPlanId", itineraryPlanId));
		return (List<MajorLocation>) criteria.list();
	}
	
	public List<MajorLocationSyncData> getUpdateMajorLocation(Date lastSyncTime, Integer[] itineraryPlanIds){
		Criteria criteria = this.createCriteria("a")
				.createAlias("a.itineraryPlan", "b", JoinType.LEFT_OUTER_JOIN);
		
		if(lastSyncTime==null){
			criteria.add(Restrictions.in("b.itineraryPlanId", itineraryPlanIds));
		} else {
			criteria.add(Restrictions.and(Restrictions.ge("a.modifiedDate", lastSyncTime)
					, Restrictions.in("b.itineraryPlanId", itineraryPlanIds)));
		}
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("a.majorLocationId"), "majorLocationId");
		projList.add(Projections.property("a.remark"), "remark");
		projList.add(Projections.property("a.sequence"), "sequence");
		projList.add(Projections.property("b.itineraryPlanId"), "itineraryPlanId");
		projList.add(Projections.property("a.createdDate"), "createdDate");
		projList.add(Projections.property("a.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("a.taskName"), "taskName");
		projList.add(Projections.property("a.location"), "location");
		projList.add(Projections.property("a.session"), "session");
		projList.add(Projections.property("a.isFreeEntryTask"), "isFreeEntryTask");
		projList.add(Projections.property("a.district"), "district");
		projList.add(Projections.property("a.tpu"), "tpu");
		projList.add(Projections.property("a.street"), "street");
		projList.add(Projections.property("a.isNewRecruitmentTask"), "isNewRecruitmentTask");
		projList.add(Projections.property("a.marketName"), "marketName");
		projList.add(Projections.property("a.address"), "address");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(MajorLocationSyncData.class));
		return criteria.list();
	}
}
