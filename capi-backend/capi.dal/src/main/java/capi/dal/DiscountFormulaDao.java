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
import org.springframework.util.StringUtils;

import capi.entity.DiscountFormula;
import capi.model.api.dataSync.DiscountFormulaSyncData;
import capi.model.commonLookup.DiscountFormulaLookupModel;

@Repository("DiscountFormulaDao")
public class DiscountFormulaDao  extends GenericDao<DiscountFormula>{

	@SuppressWarnings("unchecked")
	public List<DiscountFormula> getTableList(String search,
			int firstRecord, int displayLenght, Order order, String status) {

		Criteria critera = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLenght).addOrder(order);

		if (!StringUtils.isEmpty(search)) {
			critera.add(Restrictions.or(
					Restrictions.like("displayPattern", "%" + search + "%"),
					Restrictions.like("formula", "%" + search + "%"),
					Restrictions.like("status", "%" + search + "%")));
		}
		
		if (!StringUtils.isEmpty(status)) {
			critera.add(Restrictions.eq("status", status));
		}

		return critera.list();
	}
	
	public long countTableList(String search, String status) {
		Criteria critera = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			critera.add(Restrictions.or(
					Restrictions.like("displayPattern", "%" + search + "%"),
					Restrictions.like("formula", "%" + search + "%"),
					Restrictions.like("status", "%" + search + "%")));
		}
		
		if (!StringUtils.isEmpty(status)) {
			critera.add(Restrictions.eq("status", status));
		}
		
		return (long) critera.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public List<DiscountFormula> getDiscountFormulasByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("discountFormulaId", ids));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DiscountFormulaLookupModel> getAllEnabledFormula() {
		Criteria criteria = this.createCriteria();
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("formula"), "formula");
		projList.add(Projections.property("pattern"), "pattern");
		projList.add(Projections.property("variable"), "variable");
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("status", "Enable"));
		criteria.setResultTransformer(Transformers.aliasToBean(DiscountFormulaLookupModel.class));
		return criteria.list();
	}
	
	public List<DiscountFormulaSyncData> getUpdateDiscountFormula(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("discountFormulaId"), "discountFormulaId");
		projList.add(Projections.property("formula"), "formula");
		projList.add(Projections.property("pattern"), "pattern");
		projList.add(Projections.property("variable"), "variable");
		projList.add(Projections.property("status"), "status");
		projList.add(Projections.property("displayPattern"), "displayPattern");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(DiscountFormulaSyncData.class));
		return criteria.list();
	}
}
