package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.ProductSpecification;
import capi.model.api.dataSync.ProductSpecificationSyncData;
import capi.model.dataImportExport.ImportRebasingProductSpecificationList;
import capi.model.productMaintenance.ProductSpecificationEditModel;

@Repository("ProductSpecificationDao")
public class ProductSpecificationDao extends GenericDao<ProductSpecification> {
	
	
	@SuppressWarnings("unchecked")
	public List<ProductSpecification> getProductSpecificationByProductIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("product.productId", ids));
		return criteria.list();
	}
	
	public List<ProductSpecification> getProductSpecificationByProductId(Integer id){
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		return getProductSpecificationByProductIds(ids);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductSpecification> getProductSpecificationByProductIdsAndProdAttrId(List<Integer> productIds, Integer prodAttrId){
		Criteria criteria = this.createCriteria()
				.createAlias("product", "product")
				.createAlias("productAttribute", "productAttribute");
		criteria.add(Restrictions.in("product.productId", productIds));
		criteria.add(Restrictions.eq("productAttribute.productAttributeId", prodAttrId));	
		return criteria.list();
	}
	
	public long countProductSpecByProductAttrId(Integer productAttributeId) {
		Criteria criteria = this.createCriteria()
				.createAlias("productAttribute", "pa");
		criteria.add(Restrictions.eq("pa.productAttributeId", productAttributeId));	
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.countDistinct("pa.productAttributeId"));		
		criteria.setProjection(projList);
		
		return (long) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductSpecification> getProductSpecificationByProductAttributeId(Integer id){
		Criteria criteria = this.createCriteria()
				.createAlias("productAttribute", "productAttribute")
				.add(Restrictions.eq("productAttribute.productAttributeId", id));	
		return criteria.list();
	}
	
	public ProductSpecification getProductSpecificationByProductAndAttribute(Integer productId, Integer productAttributeId) {
		Criteria criteria = this.createCriteria()
				.createAlias("product", "product")
				.createAlias("productAttribute", "productAttribute");
		criteria.add(Restrictions.eq("product.productId", productId));
		criteria.add(Restrictions.eq("productAttribute.productAttributeId", productAttributeId));	
		return (ProductSpecification)criteria.uniqueResult();
	}
	
	public Integer getProductIdByAttributes(int productGroupId, String countryOfOrigin, String barcode,
			List<ProductSpecificationEditModel> attributes, int numberOfAttributes, Integer sameProductId) {
		String hql = "select p1.id "
				+ " from ProductGroup as g1 "
				+ " inner join g1.products as p1 ";
		
//		for (int i = 0; i < numberOfAttributes; i++) {
//			
//			hql += " inner join p1.fullSpecifications as s" + i +" on s" + i + ".sequence = " + (i + 1) + " "
//					+ " and ((:value" + i + " is null and (s" + i + ".value is null or s" + i + ".value = '')) or s" + i + ".value = :value" + i + ") ";
//		}
		
		hql += " where g1.id = :productGroupId ";
		
		if (!StringUtils.isEmpty(countryOfOrigin)) {
			hql += " and p1.countryOfOrigin = :countryOfOrigin ";
		} else {
			hql += " and (p1.countryOfOrigin is null or p1.countryOfOrigin = '') ";
		}
		
		if (!StringUtils.isEmpty(barcode)) {
			hql += " and p1.barcode = :barcode ";
		} else {
			hql += " and (p1.barcode is null or p1.barcode = '') ";
		}
		
		if (sameProductId != null) {
			hql += " and (p1.status = 'Active' or p1.id = :sameProductId) ";
		} else {
			hql += " and p1.status = 'Active' ";
		}
		
		for (int i = 0; i < numberOfAttributes; i++) {
			hql += " and p1.id in ( select s"+i+".product.id from VwProductFullSpec as s"+i+" where s"+ i +".productGroup.id = :productGroupId "
					+" and s" + i + ".sequence = " + (i + 1) + " "
					+ " and ((:value" + i + " is null and (s" + i + ".value is null or s" + i + ".value = '')) or s" + i + ".value = :value" + i + ")) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("productGroupId", productGroupId);
		if (!StringUtils.isEmpty(countryOfOrigin)) {
			query.setParameter("countryOfOrigin", countryOfOrigin);
		}
		
		if (!StringUtils.isEmpty(barcode)) {
			query.setParameter("barcode", StringUtils.stripToNull(barcode));
		}
		
		if (sameProductId != null) {
			query.setParameter("sameProductId", sameProductId);
		}

		for (int i = 0; i < numberOfAttributes; i++) {
			ProductSpecificationEditModel specModel = null;
			for (ProductSpecificationEditModel attr : attributes) {
				if (attr.getSequence().intValue() == i + 1) {
					specModel = attr;
					break;
				}
			}
			query.setParameter("value" + i, specModel != null ? StringUtils.stripToNull(specModel.getValue()) : null);
		}
		
		query.setMaxResults(1);
		
		return (Integer)query.uniqueResult();
	}
	
	/**
	 * Import Export Rebasing
	 */
	public List<ProductSpecification> getProductSpecificationByProductGroupIds(List<Integer> productGroupIds){
		String hql = "Select distinct ps"
				+ " From ProductSpecification as ps"
				+ " inner join ps.product as p"
				+ " inner join p.productGroup as pg"
				+ " where pg.productGroupId in ( :productGroupIds )";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("productGroupIds", productGroupIds);
		
		return query.list();
	}
	
	public void insertProductSpecificationByRebasing(String values, List<ImportRebasingProductSpecificationList> specList){
		String sql = "INSERT INTO [ProductSpecification] ([ProductId], [ProductAttributeId], [Value], [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		for(int i = 0;i<specList.size();i++){
			query.setParameter("productId"+i, specList.get(i).getProductId());
			query.setParameter("productAttributeId"+i, specList.get(i).getProductAttributeId());
			query.setParameter("value"+i, specList.get(i).getValue());
		}
		
		query.executeUpdate();
	}
	
	/**
	 * Data Sync
	 */
	public List<ProductSpecificationSyncData> getUpdatedProductSpecification(Date lastSyncTime, Integer[] productSpecificationIds){
		if (lastSyncTime != null){
			lastSyncTime = DateUtils.addSeconds(lastSyncTime, -5);
		}
		
		Criteria criteria = this.createCriteria("ps")
				.createAlias("ps.product", "p", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ps.productAttribute", "pa", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("ps.productSpecificationId"), "productSpecificationId");
		projList.add(Projections.property("p.productId"), "productId");
		projList.add(Projections.property("pa.productAttributeId"), "productAttributeId");
		projList.add(Projections.property("ps.value"), "value");
		projList.add(Projections.property("ps.createdDate"), "createdDate");
		projList.add(Projections.property("ps.modifiedDate"), "modifiedDate");
		
		if(lastSyncTime!=null){
			criteria.add(Restrictions.or(
					Restrictions.ge("ps.modifiedDate", lastSyncTime),
					Restrictions.ge("ps.createdDate", lastSyncTime)
					));
		}
		
		if(productSpecificationIds!=null && productSpecificationIds.length>0)
			criteria.add(Restrictions.in("ps.productSpecificationId", productSpecificationIds));
			
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(ProductSpecificationSyncData.class));
		return criteria.list();
	}
}