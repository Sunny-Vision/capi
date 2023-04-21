package capi.dal;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.OutletAttachment;
import capi.model.api.dataSync.OutletAttachmentSyncData;
import capi.model.api.dataSync.OutletAttachmentZipImageSyncData;

@Repository("OutletAttachmentDao")
public class OutletAttachmentDao extends GenericDao<OutletAttachment>{
	@SuppressWarnings("unchecked")
	public List<OutletAttachment> getAllByOutletId(int outletId) {
		return this.createCriteria()
				.add(Restrictions.eq("outlet.outletId", outletId))
				.addOrder(Order.asc("sequence"))
				.list();
	}
	
	public Integer getMaxSequence(int outletId) {
		return (Integer)this.createCriteria()
				.add(Restrictions.eq("outlet.outletId", outletId))
				.setProjection(Projections.max("sequence"))
				.uniqueResult();
	}
	
	public List<OutletAttachmentSyncData> getUpdatedOutletAttachments(Date lastSyncTime, Integer[] ids){
		if (lastSyncTime != null){
			lastSyncTime = DateUtils.addSeconds(lastSyncTime, -5);
		}
		Criteria criteria = this.createCriteria("a")
				.createAlias("a.outlet", "o");
		
		if(lastSyncTime!=null){
			criteria.add(Restrictions.or(
					Restrictions.ge("a.modifiedDate", lastSyncTime),
					Restrictions.ge("a.createdDate", lastSyncTime)
					));
		}
			
		
		if(ids!=null && ids.length>0)
			criteria.add(Restrictions.in("a.outletAttachmentId", ids));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("o.outletId"), "outletId");
		projList.add(Projections.property("a.outletAttachmentId"), "outletAttachmentId");
		projList.add(Projections.property("a.sequence"), "sequence");
		projList.add(Projections.property("a.createdDate"), "createdDate");
		projList.add(Projections.property("a.modifiedDate"), "modifiedDate");
		criteria.setProjection(projList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(OutletAttachmentSyncData.class));
		
		return criteria.list();
	}
	
	
	public List<OutletAttachmentZipImageSyncData> getOutletAttachmentInfoMonthly(String endDate) {

		String sql = " "
				+ " Select OutletAttachmentId as outletAttachmentId, " 
				+ " ModifiedDate AS modifiedDate, "
				+ " sequence AS sequence, "
				+ " outletId AS outletId, "
				+ " Path AS path "
				+ " FROM OutletAttachment " 
				+ " WHERE 1 = 1 "
				//+ " AND (ModifiedDate between '" + startDate + "' AND '" + endDate + "') "
				+ " AND (ModifiedDate <= '" + endDate + "') "
				+ " AND (Path <> '' AND Path IS NOT NULL) ";

				
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("outletAttachmentId", StandardBasicTypes.INTEGER) 
						.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
						.addScalar("path", StandardBasicTypes.STRING)
						.addScalar("sequence", StandardBasicTypes.INTEGER)
						.addScalar("outletId", StandardBasicTypes.INTEGER);
		//query.setParameter("startDate", startDate);
		//query.setParameter("endDate", endDate);
		
		query.setResultTransformer(Transformers.aliasToBean(OutletAttachmentZipImageSyncData.class));

		return query.list();
	}
	
}
	