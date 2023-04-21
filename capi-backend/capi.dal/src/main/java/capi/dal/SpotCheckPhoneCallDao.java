package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.SpotCheckPhoneCall;
import capi.model.SystemConstant;
import capi.model.api.dataSync.SpotCheckPhoneCallSyncData;

@Repository("SpotCheckPhoneCallDao")
public class SpotCheckPhoneCallDao  extends GenericDao<SpotCheckPhoneCall>{

	@SuppressWarnings("unchecked")
	public List<SpotCheckPhoneCall> getByIds(Integer[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("spotCheckPhoneCallId", ids)).list();
	}

	@SuppressWarnings("unchecked")
	public List<SpotCheckPhoneCall> getSCPhoneCallsBySCFormId(Integer spotCheckFormId){
		Criteria criteria = this.createCriteria("scpc");
		criteria.add(
				Restrictions.eq("scpc.spotCheckForm.spotCheckFormId", spotCheckFormId)
		);
		return criteria.list();
	}

	/**
	 * Data Sync
	 */
	public List<SpotCheckPhoneCallSyncData> getUpdatedSpotCheckPhoneCall(Date lastSyncTime, Integer[] spotCheckFormIds, Integer[] spotCheckPhoneCallIds){
		String phoneCallTime = String.format("dbo.FormatTime(sp.phoneCallTime, '%s')", SystemConstant.TIME_FORMAT);
		String hql = "select sp.spotCheckPhoneCallId as spotCheckPhoneCallId"
				+ ", case when sp.phoneCallTime is null then '' else "+phoneCallTime+" end as phoneCallTime"
				+ ", sp.result as result, sp.createdDate as createdDate"
				+ ", sp.modifiedDate as modifiedDate, sc.spotCheckFormId as spotCheckFormId"
				+ " from SpotCheckPhoneCall as sp"
				+ " left join sp.spotCheckForm as sc"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and sp.modifiedDate >= :modifiedDate";
		}
		
		if(spotCheckFormIds!=null && spotCheckFormIds.length>0){
			hql += " and sc.spotCheckFormId in ( :spotCheckFormIds )";
		}
		
		if(spotCheckPhoneCallIds!=null && spotCheckPhoneCallIds.length>0){
			hql += " and sp.spotCheckPhoneCallId in ( :spotCheckPhoneCallIds )";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		if(spotCheckFormIds!=null && spotCheckFormIds.length>0){
			query.setParameterList("spotCheckFormIds", spotCheckFormIds);
		}
		
		if(spotCheckPhoneCallIds!=null && spotCheckPhoneCallIds.length>0){
			query.setParameterList("spotCheckPhoneCallIds", spotCheckPhoneCallIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SpotCheckPhoneCallSyncData.class));
		return query.list();
		
	}
}
