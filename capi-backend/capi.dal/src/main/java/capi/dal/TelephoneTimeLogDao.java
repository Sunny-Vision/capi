package capi.dal;

import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.TelephoneTimeLog;
import capi.model.api.dataSync.TelephoneTimeLogSyncData;

@Repository("TelephoneTimeLogDao")
public class TelephoneTimeLogDao extends GenericDao<TelephoneTimeLog> {
	
	public List<TelephoneTimeLogSyncData> getUpdatedTelephoneTimeLog(Date lastSyncTime, Integer[] timeLogIds, Integer[] telephoneTimeLogIds){
		String hql = "select tt.telephoneTimeLogId as telephoneTimeLogId, tt.referenceMonth as referenceMonth"
				+ ", tt.survey as survey, tt.caseReferenceNo as caseReferenceNo"
				+ ", tt.status as status, tt.session as session"
				+ ", tt.createdDate as createdDate, tt.modifiedDate as modifiedDate"
				+ ", tl.timeLogId as timeLogId, a.assignmentId as assignmentId"
				+ ", tt.totalQuotation as totalQuotation"
				+ ", tt.quotationCount as quotationCount"
				+ " from TelephoneTimeLog as tt"
				+ " left join tt.timeLog as tl"
				+ " left join tt.assignment as a"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and tt.modifiedDate >= :modifiedDate";
		}
		if(timeLogIds!=null && timeLogIds.length>0){
			hql += " and tl.timeLogId in ( :timeLogIds )";
		}
		
		if(telephoneTimeLogIds!=null && telephoneTimeLogIds.length>0){
			hql += " and tt.telephoneTimeLogId in ( :telephoneTimeLogIds )";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null)
			query.setParameter("modifiedDate", lastSyncTime);
		if(timeLogIds!=null && timeLogIds.length>0)
			query.setParameterList("timeLogIds", timeLogIds);
		if(telephoneTimeLogIds!=null && telephoneTimeLogIds.length>0){
			query.setParameterList("telephoneTimeLogIds", telephoneTimeLogIds);
		}
		query.setResultTransformer(Transformers.aliasToBean(TelephoneTimeLogSyncData.class));
		return query.list();
	}
}
