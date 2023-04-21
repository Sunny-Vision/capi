package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.SubPriceType;
import capi.model.api.dataSync.SubPriceTypeSyncData;

@Repository("SubPriceTypeDao")
public class SubPriceTypeDao extends GenericDao<SubPriceType> {

	@SuppressWarnings("unchecked")
	public List<SubPriceType> listSubPriceType(String search,
			int firstRecord, int displayLenght, Order order) {

		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLenght).addOrder(order);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("category", "%" + search + "%"),
					Restrictions.like("name", "%" + search + "%"),
					Restrictions.like("status", "%" + search + "%")));
		}

		return criteria.list();
	}

	public long countSubPriceType(String search) {
		Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.or(
				Restrictions.like("category", "%" + search + "%"),
				Restrictions.like("name", "%" + search + "%"),
				Restrictions.like("status", "%" + search + "%")));
		
//		ScrollableResults result = criteria.scroll();
//		result.last();
//		int total = result.getRowNumber() + 1;
//		result.close();
//		
//		return total;
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public int getCurrentMaxId(){
		Criteria criteria = this.createCriteria().addOrder(Order.desc("subPriceFieldId"));
		try {
			return ((SubPriceType) criteria.uniqueResult()).getId();
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	public List<SubPriceType> getSubPriceTypesByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria("t");
		criteria.add(Restrictions.in("t.subPriceTypeId", ids));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SubPriceType> search(String search, int firstRecord, int displayLength) {
		Criteria criteria = this.createCriteria().addOrder(Order.asc("name"));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		criteria.add(Restrictions.eq("status", "Enable"));
		
		return criteria.list();
	}

	public long countSearch(String search) {
		Criteria criteria = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public List<SubPriceTypeSyncData> getUpdateSubPriceType(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("subPriceTypeId"), "subPriceTypeId");
		projList.add(Projections.property("numeratorFormula"), "numeratorFormula");
		projList.add(Projections.property("denominatorFormula"), "denominatorFormula");
		projList.add(Projections.property("category"), "category");
		projList.add(Projections.sqlProjection("groupByFieldId as groupByFieldId", new String[]{"groupByFieldId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.sqlProjection("dividedByFieldId as dividedByFieldId", new String[]{"dividedByFieldId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.property("name"), "name");
		projList.add(Projections.property("status"), "status");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		projList.add(Projections.property("hideNPrice"), "hideNPrice");
		projList.add(Projections.property("hideSPrice"), "hideSPrice");
		projList.add(Projections.property("hideDiscount"), "hideDiscount");
		projList.add(Projections.property("useMaxNPrice"), "useMaxNPrice");
		projList.add(Projections.property("useMaxSPrice"), "useMaxSPrice");
		projList.add(Projections.property("useMinNPrice"), "useMinNPrice");
		projList.add(Projections.property("useMinSPrice"), "useMinSPrice");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(SubPriceTypeSyncData.class));
		return criteria.list();
	}
}
