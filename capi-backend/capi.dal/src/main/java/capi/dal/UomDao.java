package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.Uom;
import capi.model.api.dataSync.UomSyncData;
import capi.model.masterMaintenance.UomModel;

@Repository("UomDao")
public class UomDao extends GenericDao<Uom> {
	
	@SuppressWarnings("unchecked")
	public List<UomModel> listUom(String search, int firstRecord, int displayLenght, Order order,  Integer uomCategoryId) {
		
		String hql = "select u.uomId as uomId, u.chineseName as chineseName, u.englishName as englishName, "
				+ " c.uomCategoryId as uomCategoryId, c.description as description "
				+ " from Uom as u "
				+ " inner join u.uomCategory as c "
				+ " where 1=1 ";
		if (!StringUtils.isEmpty(search)){
			hql += " and (str(u.uomId) like :search or u.englishName like :search or u.chineseName like :search "
					+ " or str(c.uomCategoryId) like :search or c.description like :search) ";
		}
		
		if (uomCategoryId != null){
			hql +=  " and c.uomCategoryId = :uomCategoryId ";
		}
		
		hql +=  " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		 Query query = getSession().createQuery(hql);
		 
		 if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		 }
		 if (uomCategoryId != null){
			 query.setParameter("uomCategoryId", uomCategoryId);
		 }
		
		 query.setFirstResult(firstRecord);
		 query.setMaxResults(displayLenght);
		 
		 query.setResultTransformer(Transformers.aliasToBean(UomModel.class));

		return query.list();
	}
	
	public List<Uom> searchUom(String search, int firstRecord, int displayLength) {
		return searchUom(search, firstRecord, displayLength, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Uom> searchUom(String search, int firstRecord, int displayLength, Integer[] categoryIds) {
		Criteria criteria = this.createCriteria()
				.createAlias("uomCategory", "category", JoinType.LEFT_OUTER_JOIN);
		
		if (categoryIds != null) {
			criteria.add(Restrictions.in("category.id", categoryIds));
		}
		
		criteria.setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("uomId")).addOrder(Order.asc("chineseName"));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("str({alias}.uomId)+' - '+chineseName LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return criteria.list();
	}

	public long countSearchUom(String search) {
		return countSearchUom(search, null);
	}
	
	public long countSearchUom(String search, Integer[] categoryIds) {
		Criteria criteria = this.createCriteria()
				.createAlias("uomCategory", "category", JoinType.LEFT_OUTER_JOIN);

		if (categoryIds != null) {
			criteria.add(Restrictions.in("category.id", categoryIds));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("str({alias}.uomId)+' - '+chineseName LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public long countUom() {
		return (long) this.createCriteria()
				.setProjection(Projections.rowCount()).uniqueResult();
	}

	public long countUom(String search, Integer uomCategoryId) {
		String from = "select count(*) from Uom as u"
					+ " inner join u.uomCategory as c ";
		String where = "where 1=1 ";

		if (!StringUtils.isEmpty(search)) {
			where += " and (str(u.uomId) like :search or u.englishName like :search or u.chineseName like :search "
					+ " or str(c.uomCategoryId) like :search) ";
		}
		if (uomCategoryId != null){
			where += " and c.uomCategoryId=:uomCategoryId ";
		}
		
		 Query query = getSession().createQuery(from + where);
		 
		 if (!StringUtils.isEmpty(search)) {
			 query.setParameter("search", "%" +search+ "%");
			// query.setParameter("searchInt", "%" +search+ "%", StandardBasicTypes.STRING);
		 }
		 
		 if (uomCategoryId != null){
			 query.setParameter("uomCategoryId", uomCategoryId);
		 }

		 return (long)query.uniqueResult();
		//return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	
	@SuppressWarnings("unchecked")
	public List<Uom> findAllbyUOMCategoryId(Integer uomCategoryID) {
		Criteria critera = this.createCriteria().add(Restrictions.eq("uomCategory.uomCategoryId",uomCategoryID));		
		return critera.list();
	}
	
	
	public List<Uom> getUomsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("uomId", ids));
		return criteria.list();
	}
	
	public List<Uom> getNotExistedUom(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("uomId", ids)));
		return criteria.list();		
	}
	
	public List<Integer> getExistingUomId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<Uom> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("uomId", ids));
		return criteria.list();
	}
	
	public List<UomSyncData> getUpdateUom(Date lastSyncTime){
		Criteria criteria = this.createCriteria("u").createAlias("u.uomCategory", "c");
		criteria.add(Restrictions.ge("u.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("u.uomId"), "uomId");
		projList.add(Projections.property("u.chineseName"), "chineseName");
		projList.add(Projections.property("u.englishName"), "englishName");
		projList.add(Projections.property("c.uomCategoryId"), "uomCategoryId");
		projList.add(Projections.property("u.createdDate"), "createdDate");
		projList.add(Projections.property("u.modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(UomSyncData.class));
		return criteria.list();
	}
	
	public Uom getUOMByChineseName(String chineseName){
		return (Uom)this.createCriteria().add(Restrictions.eq("chineseName", chineseName)).uniqueResult();
	}
	
	public Uom getUOMByEnglishName(String englishName){
		return (Uom)this.createCriteria().add(Restrictions.eq("englishName", englishName)).uniqueResult();
	}
}
