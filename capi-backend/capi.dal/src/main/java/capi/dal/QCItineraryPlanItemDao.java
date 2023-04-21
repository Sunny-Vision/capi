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

import capi.entity.QCItineraryPlanItem;
import capi.model.api.dataSync.QCItineraryPlanItemSyncData;

@Repository("QCItinerayPlanItemDao")
public class QCItineraryPlanItemDao  extends GenericDao<QCItineraryPlanItem>{
	public List<QCItineraryPlanItemSyncData> getUpdateQCItineraryPlanItem(Date lastSyncTime, Integer[] qcItineraryPlanIds){
		Criteria criteria = this.createCriteria("ipi")
				.createAlias("ipi.qcItineraryPlan", "qip", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ipi.spotCheckForm", "sc", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ipi.supervisoryVisitForm", "sv", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ipi.peCheckForm", "pe", JoinType.LEFT_OUTER_JOIN);
		
		if(lastSyncTime==null){
			criteria.add(Restrictions.in("qip.qcItineraryPlanId", qcItineraryPlanIds));
		} else {
			criteria.add(Restrictions.and(
					Restrictions.ge("ipi.modifiedDate", lastSyncTime)
					, Restrictions.in("qip.qcItineraryPlanId", qcItineraryPlanIds)));
		}
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("ipi.qcItineraryPlanItemId"), "qcItineraryPlanItemId");
		projList.add(Projections.property("ipi.remark"), "remark");
		projList.add(Projections.property("ipi.sequence"), "sequence");
		projList.add(Projections.property("qip.qcItineraryPlanId"), "qcItineraryPlanId");
		projList.add(Projections.property("ipi.taskName"), "taskName");
		projList.add(Projections.property("ipi.location"), "location");
		projList.add(Projections.property("ipi.session"), "session");
		projList.add(Projections.property("ipi.createdDate"), "createdDate");
		projList.add(Projections.property("ipi.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("sc.spotCheckFormId"), "spotCheckFormId");
		projList.add(Projections.property("sv.supervisoryVisitFormId"), "supervisoryVisitFormId");
		projList.add(Projections.property("pe.peCheckFormId"), "peCheckFormId");
		projList.add(Projections.property("ipi.itemType"), "itemType");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(QCItineraryPlanItemSyncData.class));
		return criteria.list();
	}
}
