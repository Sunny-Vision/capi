package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.ProductCleaning;

@Repository("ProductCleaningDao")
public class ProductCleaningDao extends GenericDao<ProductCleaning>{

	public List<ProductCleaning> getUpdatedProductCleaning(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		criteria.addOrder(Order.asc("modifiedDate"));
		return criteria.list();
	}
	
	public List<ProductCleaning> getObsoleteProductCleaning(Date obsoleteDate){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.le("modifiedDate", obsoleteDate));
		return criteria.list();
	}
}
