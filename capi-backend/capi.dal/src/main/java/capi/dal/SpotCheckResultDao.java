package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.SpotCheckResult;
import capi.model.api.dataSync.SpotCheckResultSyncData;

@Repository("SpotCheckResultDao")
public class SpotCheckResultDao  extends GenericDao<SpotCheckResult>{

	@SuppressWarnings("unchecked")
	public List<SpotCheckResult> getByIds(Integer[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("spotCheckResultId", ids)).list();
	}

	@SuppressWarnings("unchecked")
	public List<SpotCheckResult> getSCResultsBySCFormId(Integer spotCheckFormId){
		Criteria criteria = this.createCriteria("scr");
		criteria.add(
				Restrictions.eq("scr.spotCheckForm.spotCheckFormId", spotCheckFormId)
		);
		return criteria.list();
	}
	
	public List<SpotCheckResultSyncData> getUpdatedSpotCheckResult(Date lastSyncTime, Integer[] spotCheckFormIds, Integer[] spotCheckResultIds){
		String hql = "select sr.spotCheckResultId as spotCheckResultId"
				+ ", sr.result as result, sr.otherRemark as otherRemark"
				+ ", sr.referenceNo as referenceNo "
				+ ", sf.spotCheckFormId as spotCheckFormId, sr.createdDate as createdDate"
				+ ", sr.modifiedDate as modifiedDate"
				+ " from SpotCheckResult as sr"
				+ " left join sr.spotCheckForm as sf"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and sr.modifiedDate >= :modifiedDate";
		}
		
		if(spotCheckFormIds!=null && spotCheckFormIds.length>0){
			hql += " and sf.spotCheckFormId in ( :spotCheckFormIds )";
		}
		
		if(spotCheckResultIds!=null && spotCheckResultIds.length>0){
			hql += " and sr.spotCheckResultId in ( :spotCheckResultIds )";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null)
			query.setParameter("modifiedDate", lastSyncTime);
		
		if(spotCheckFormIds!=null && spotCheckFormIds.length>0)
			query.setParameterList("spotCheckFormIds", spotCheckFormIds);
		
		if(spotCheckResultIds!=null && spotCheckResultIds.length>0){
			query.setParameterList("spotCheckResultIds", spotCheckResultIds);
		}
		query.setResultTransformer(Transformers.aliasToBean(SpotCheckResultSyncData.class));
		return query.list();
	}
}
