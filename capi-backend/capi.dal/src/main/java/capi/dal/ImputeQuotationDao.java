package capi.dal;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.ImputeQuotation;

@Repository("ImputeQuotaitonDao")
public class ImputeQuotationDao  extends GenericDao<ImputeQuotation>{
	
	public ImputeQuotation getImputeQuotation(Integer quotationId, Date refMonth) {

		Criteria criteria = this.createCriteria("iq").createAlias("iq.quotation", "q");

		criteria.add(Restrictions.eq("q.quotationId", quotationId)).add(Restrictions.eq("referenceMonth", refMonth));

		criteria.setMaxResults(1);

		return (ImputeQuotation)criteria.uniqueResult();
	}
	
	public ImputeQuotation getPreviousImputeQuotation(Integer quotationId, Date refMonth) {

		Criteria criteria = this.createCriteria("iq").createAlias("iq.quotation", "q");

		criteria.add(Restrictions.eq("q.quotationId", quotationId)).add(Restrictions.lt("referenceMonth", refMonth)).addOrder(Order.desc("referenceMonth"));

		criteria.setMaxResults(1);

		return (ImputeQuotation)criteria.uniqueResult();
	}
}
