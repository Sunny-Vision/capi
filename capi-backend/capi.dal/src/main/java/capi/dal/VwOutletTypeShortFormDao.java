package capi.dal;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.VwOutletTypeShortForm;

@Repository("VwOutletTypeShortFormDao")
public class VwOutletTypeShortFormDao extends GenericDao<VwOutletTypeShortForm>{
	@SuppressWarnings("unchecked")
	public List<VwOutletTypeShortForm> getAll() {
		return this.createCriteria().addOrder(Order.asc("shortCode")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<VwOutletTypeShortForm> getByIds(String[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("shortCode", ids))
				.addOrder(Order.asc("shortCode")).list();
	}
	
	
	public List<VwOutletTypeShortForm> searchOutletType(String search, int firstRecord, int displayLength){
		Criteria critera = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("shortCode")).addOrder(Order.asc("chineseName"));

		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("{alias}.shortCode+' - '+chineseName LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return critera.list();
	}
	
	public long countSearch(String search) {
		Criteria critera = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("{alias}.shortCode+' - '+chineseName LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return (long) critera.setProjection(Projections.rowCount()).uniqueResult();
	}
	
}
