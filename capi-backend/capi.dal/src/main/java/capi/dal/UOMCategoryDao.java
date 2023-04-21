package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.UOMCategory;
import capi.model.api.dataSync.UOMCategorySyncData;
import capi.model.masterMaintenance.UOMCategoryTableList;

@Repository("UOMCategoryDao")
public class UOMCategoryDao extends GenericDao<UOMCategory> {
	
	@SuppressWarnings("unchecked")
	public List<UOMCategoryTableList> listUOMCategory(String search,
			int firstRecord, int displayLenght, Order order) {

		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLenght).addOrder(order).createAlias("uoms", "u", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.groupProperty("uomCategoryId"), "uomCategoryId");
		projList.add(Projections.groupProperty("description"), "description");
		projList.add(Projections.count("u.uomId"), "numberOfUOM");
		
		criteria.setProjection(projList);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.sqlRestriction(
				              "{alias}.uomCategoryId LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING),
					Restrictions.like("description", "%" + search + "%")));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(UOMCategoryTableList.class));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<UOMCategory> searchUOMCategory(String search, int firstRecord, int displayLength) {
		Criteria critera = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("uomCategoryId"));

		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.sqlRestriction("str({alias}.uomCategoryId)+' - '+description LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return critera.list();
	}

	public long countUOMCategory() {
		return (long) this.createCriteria()
				.setProjection(Projections.rowCount()).uniqueResult();
	}

	public long countUOMCategory(String search) {
		Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.sqlRestriction("str({alias}.uomCategoryId)+' - '+description LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING));
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	
	public List<UOMCategory> getUomCategoriesByIds(List<Integer> ids, boolean fetchUOM){
		Criteria criteria = this.createCriteria();
		if (fetchUOM){
			criteria.setFetchMode("uoms", FetchMode.JOIN);
		}
		criteria.add(Restrictions.in("uomCategoryId", ids));
		
		return criteria.list();
		
	}
	
	public ScrollableResults getAllUOMCategoryResult(){
		Criteria criteria = this.createCriteria("u").setFetchMode("uoms", FetchMode.JOIN);
		//.createAlias("u.uoms", "t", JoinType.LEFT_OUTER_JOIN);
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<UOMCategory> getNotExistedUOMCategory(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("uomCategoryId", ids)));
		return criteria.list();		
	}
	
	public List<Integer> getExistingUOMCategoryId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<UOMCategory> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("uomCategoryId", ids));
		return criteria.list();
	}
	
	public List<UOMCategorySyncData> getUpdateUOMCategory(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("uomCategoryId"), "uomCategoryId");
		projList.add(Projections.property("description"), "description");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(UOMCategorySyncData.class));
		return criteria.list();
	}
	
	public List<UOMCategory> getUOMCategoryByUnitId(Integer unitId){
		Criteria criteria = this.createCriteria("uc")
				.createAlias("uc.units", "u");
		criteria.add(Restrictions.eq("u.unitId", unitId));
		
		return criteria.list();
	}
	
	public UOMCategory getUOMCategoryByDescription(String description){
		Criteria criteria = this.createCriteria("u");
		return (UOMCategory)criteria.add(Restrictions.eq("u.description", description)).uniqueResult();
	}
}
