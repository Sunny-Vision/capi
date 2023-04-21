package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.WorkingSessionSetting;
import capi.model.api.dataSync.WorkingSessionSettingSyncData;

@Repository("WorkingSessionSettingDao")
public class WorkingSessionSettingDao extends GenericDao<WorkingSessionSetting>{
	
	public List<WorkingSessionSettingSyncData> getUpdateWorkingSessionSetting(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("workingSessionSettingId"), "workingSessionSettingId");
		projList.add(Projections.property("fromTime"), "fromTime");
		projList.add(Projections.property("toTime"), "toTime");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(WorkingSessionSettingSyncData.class));
		return criteria.list();
	}
	
	public List<WorkingSessionSetting> getExcludedWorkingSessionSetting(List<Integer> ids ){
		Criteria criteria = this.createCriteria("wss")
				.add(Restrictions.not(Restrictions.in("wss.workingSessionSettingId", ids)));
		return criteria.list();
	}
	
	public List<WorkingSessionSetting> getWorkingSessionSettings(){
		Criteria criteria = this.createCriteria("wss")
				.addOrder(Order.asc("wss.fromTime"));
		return criteria.list();
	}
}
