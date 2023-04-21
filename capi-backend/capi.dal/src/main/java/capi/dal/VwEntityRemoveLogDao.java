package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.VwEntityRemoveLog;

@Repository("VwEntityRemoveLogDao")
public class VwEntityRemoveLogDao extends GenericDao<VwEntityRemoveLog>{

	public List<VwEntityRemoveLog> getUpdatedRemoveLog(Date date){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("createdDate", date));
		return criteria.list();
	}
	
}
