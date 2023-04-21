package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.OutletUnitStatistic;
import capi.model.api.dataSync.OutletUnitStatisticSyncData;

@Repository("OutletUnitStatisticDao")
public class OutletUnitStatisticDao extends GenericDao<OutletUnitStatistic>{
	public List<OutletUnitStatisticSyncData> getUpdateOutletUnitStatistic(Date lastSyncTime){
		Criteria criteria = this.createCriteria("s")
				.createAlias("s.outlet", "o")
				.createAlias("s.unit", "u");
		criteria.add(Restrictions.ge("s.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("o.outletId"), "outletId");
		projList.add(Projections.property("u.unitId"), "unitId");
		projList.add(Projections.property("s.quotationCnt"), "quotationCnt");
		projList.add(Projections.property("s.outletUnitStatisticId"), "outletUnitStatisticId");
		projList.add(Projections.property("s.createdDate"), "createdDate");
		projList.add(Projections.property("s.modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(OutletUnitStatisticSyncData.class));
		return criteria.list();
	}
	public void CalculateQuotationCountInUnitOutlet(){
		Query query = this.getSession().createSQLQuery("exec [CalculateQuotationCountInUnitOutlet]");
		query.executeUpdate();
	}
}
