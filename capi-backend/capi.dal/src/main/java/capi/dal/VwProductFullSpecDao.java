package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.VwProductFullSpec;

@Repository("VwProductFullSpecDao")
public class VwProductFullSpecDao extends GenericDao<VwProductFullSpec> {
	
	@SuppressWarnings("unchecked")
	public List<VwProductFullSpec> GetAllByProductId(int id, Integer limit) {
		Criteria criteria = this.createCriteria()
				.setFetchMode("productAttribute", FetchMode.JOIN)
				.add(Restrictions.eq("product.id", id)).addOrder(Order.asc("sequence"));
		if (limit != null){
			criteria.setMaxResults(limit);
		}
		return criteria.list();
	}
}
