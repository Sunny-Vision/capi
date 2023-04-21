package capi.dal;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.ImputeUnit;

@Repository("ImputeUnitDao")
public class ImputeUnitDao  extends GenericDao<ImputeUnit>{
	
	public ImputeUnit getImputeUnit(Integer quotationId, Date refMonth) {

		Criteria criteria = this.createCriteria("iu").createAlias("iu.unit", "u");

		criteria.add(Restrictions.eq("u.unitId", quotationId)).add(Restrictions.eq("referenceMonth", refMonth));

		criteria.setMaxResults(1);

		return (ImputeUnit)criteria.uniqueResult();
	}
	
	public ImputeUnit getPreviousImputeUnit(Integer quotationId, Date refMonth) {

		Criteria criteria = this.createCriteria("iu").createAlias("iu.unit", "u");

		criteria.add(Restrictions.eq("u.unitId", quotationId)).add(Restrictions.lt("referenceMonth", refMonth)).addOrder(Order.desc("referenceMonth"));

		criteria.setMaxResults(1);

		return (ImputeUnit)criteria.uniqueResult();
	}
}
