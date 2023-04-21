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

import capi.entity.SubPriceField;
import capi.model.api.dataSync.SubPriceFieldSyncData;

@Repository("SubPriceFieldDao")
public class SubPriceFieldDao extends GenericDao<SubPriceField> {

	@SuppressWarnings("unchecked")
	public List<SubPriceField> listSubPriceField(String search, Integer[] skipIds,
			int firstRecord, int displayLenght, Order order) {

		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLenght).addOrder(order);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("fieldName", "%" + search + "%"),
					Restrictions.like("fieldType", "%" + search + "%")));
		}
		
		if (skipIds != null && skipIds.length > 0) {
			criteria.add(Restrictions.not(Restrictions.in("subPriceFieldId", skipIds)));
        }

		return criteria.list();
	}


	public long countSubPriceField(String search, Integer[] skipIds) {
		Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.or(
				Restrictions.like("fieldName", "%" + search + "%"),
				Restrictions.like("fieldType", "%" + search + "%")));

		if (skipIds != null && skipIds.length > 0) {
			criteria.add(Restrictions.not(Restrictions.in("subPriceFieldId", skipIds)));
        }		

//		ScrollableResults result = criteria.scroll();
//		result.last();
//		int total = result.getRowNumber() + 1;
//		result.close();
//		
//		return total;
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public int getCurrentMaxId(){
		Criteria criteria = this.createCriteria().addOrder(Order.desc("subPriceFieldId")).setMaxResults(1);
		try {
			return ((SubPriceField) criteria.uniqueResult()).getId();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public List<SubPriceField> getSubPriceFieldsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("subPriceFieldId", ids));
		return criteria.list();
	}
	
	public List<SubPriceFieldSyncData> getUpdateSubPriceField(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("subPriceFieldId"), "subPriceFieldId");
		projList.add(Projections.property("fieldName"), "fieldName");
		projList.add(Projections.property("fieldType"), "fieldType");
		projList.add(Projections.property("variableName"), "variableName");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(SubPriceFieldSyncData.class));
		return criteria.list();
	}
}
