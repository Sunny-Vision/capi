package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.Rebasing;

@Repository("RebasingDao")
public class RebasingDao extends GenericDao<Rebasing> {
	
	
	
	public List<Rebasing> getNotEffectedRebasing(Date date){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.le("effectiveDate", date));
		criteria.add(Restrictions.eq("isEffected", false));
		criteria.addOrder(Order.asc("effectiveDate"));
		return criteria.list();
		
	}
	
}
