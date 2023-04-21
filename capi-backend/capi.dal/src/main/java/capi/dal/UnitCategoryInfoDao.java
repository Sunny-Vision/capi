package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.Quotation;
import capi.entity.UnitCategoryInfo;
import capi.model.api.dataSync.UnitCategoryInfoSyncData;

@Repository("UnitCategoryInfoDao")
public class UnitCategoryInfoDao extends GenericDao<UnitCategoryInfo> {
	public UnitCategoryInfo findByUnitCategoryAndOutlet(String unitCategory, int outletId) {
		
		Criteria c = this.createCriteria();
		
		if (unitCategory == null) {
			c = c.add(Restrictions.isNull("unitCategory"));
		} else {
			c = c.add(Restrictions.eq("unitCategory", unitCategory));
		}
		
		return (UnitCategoryInfo)c
				.add(Restrictions.eq("outlet.id", outletId))
				.setFetchSize(1)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getDistinctUnitCategoryByOutlet(int outletId) {
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		return getSession().createCriteria(Quotation.class)
				.createAlias("unit", "unit", JoinType.INNER_JOIN)
				.add(Restrictions.eq("outlet.id", outletId))
				.add(Restrictions.or(Restrictions.isNull("unit.effectiveDate"), Restrictions.le("unit.effectiveDate", today)))
				.add(Restrictions.or(Restrictions.isNull("unit.obsoleteDate"), Restrictions.gt("unit.obsoleteDate", today)))
				.add(Restrictions.ne("status", "Inactive"))
				.setProjection(Projections.groupProperty("unit.unitCategory"))
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<UnitCategoryInfo> getAllByOutlet(int outletId) {
		return this.createCriteria()
				.add(Restrictions.eq("outlet.id", outletId))
				.addOrder(Order.asc("sequence"))
				.list();
	}
	
	public List<UnitCategoryInfoSyncData> getUpdatedUnitCategoryInfo(Date lastSyncTime, Integer[] unitCategoryInfoIds){
		if (lastSyncTime != null){
			lastSyncTime = DateUtils.addSeconds(lastSyncTime, -5);
		}
		
		Criteria criteria = this.createCriteria("u")
				.createAlias("u.outlet", "o");
		
		if(lastSyncTime!=null){
			criteria.add(Restrictions.or(
					Restrictions.ge("u.modifiedDate", lastSyncTime),
					Restrictions.ge("u.createdDate", lastSyncTime)
					));
		}
			
		if(unitCategoryInfoIds!=null && unitCategoryInfoIds.length>0)
			criteria.add(Restrictions.in("u.unitCategoryInfoId", unitCategoryInfoIds));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("u.unitCategoryInfoId"), "unitCategoryInfoId");
		projList.add(Projections.property("o.outletId"), "outletId");
		projList.add(Projections.property("u.contactPerson"), "contactPerson");
		projList.add(Projections.property("u.remark"), "remark");
		projList.add(Projections.property("u.unitCategory"), "unitCategory");
		projList.add(Projections.property("u.createdDate"), "createdDate");
		projList.add(Projections.property("u.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("u.sequence"), "sequence");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(UnitCategoryInfoSyncData.class));
		
		return criteria.list();
	}
}
