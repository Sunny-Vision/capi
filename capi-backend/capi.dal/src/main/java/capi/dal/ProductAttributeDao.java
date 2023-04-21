package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.ProductAttribute;
import capi.model.api.dataSync.ProductAttributeSyncData;
import capi.model.dataImportExport.ExportProductAttributeList;

@Repository("ProductAttributeDao")
public class ProductAttributeDao extends GenericDao<ProductAttribute> {

	@SuppressWarnings("unchecked")
	public List<ProductAttribute> listProductGroup(String search,
			int firstRecord, int displayLenght, Order order) {

		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLenght).addOrder(order);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("specificationName", "%" + search + "%"),
					Restrictions.like("option", "%" + search + "%")));
		}

		return criteria.list();
	}

	
	@SuppressWarnings("unchecked")
	public List<ProductAttribute> getProductAttributeByProductGroupIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("productGroup.productGroupId", ids)).addOrder(Order.asc("sequence"));
		return criteria.list();
	}
	
	
	public List<ProductAttribute> getProductAttributeByProductGroupId(Integer id){
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		return getProductAttributeByProductGroupIds(ids);
	}
	
	public List<ProductAttributeSyncData> getUpdateProductAttribute(Date lastSyncTime){
		Criteria criteria = this.createCriteria("p")
				.createAlias("p.productGroup", "g");
		criteria.add(Restrictions.ge("p.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("p.productAttributeId"), "productAttributeId");
		projList.add(Projections.property("g.productGroupId"), "productGroupId");
		projList.add(Projections.property("p.sequence"), "sequence");
		projList.add(Projections.property("p.specificationName"), "specificationName");
		projList.add(Projections.property("p.attributeType"), "attributeType");
		projList.add(Projections.property("p.option"), "option");
		projList.add(Projections.property("p.isMandatory"), "isMandatory");
		projList.add(Projections.property("p.createdDate"), "createdDate");
		projList.add(Projections.property("p.modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(ProductAttributeSyncData.class));
		return criteria.list();
	}
	
	public List<ExportProductAttributeList> getAllProductAttributeResult(){
		String hql = "select pa.sequence as sequence"
				+ ", pa.productAttributeId as attributeId"
				+ ", pa.specificationName as specificationName"
				+ ", pa.attributeType as attributeType"
				+ ", pa.isMandatory as isMandatory"
				+ ", pa.option as option"
				+ ", pg.code as code"
				+ " from ProductAttribute as pa"
				+ " left join pa.productGroup as pg"
				+ " order by pg.productGroupId, pa.sequence";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setResultTransformer(Transformers.aliasToBean(ExportProductAttributeList.class));
		
		return query.list();
	}
	
	//For Rebasing
	public List<ProductAttribute> getProductAttributeByRebasing(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("productGroup.productGroupId", ids)).addOrder(Order.asc("productGroup.productGroupId")).addOrder(Order.asc("sequence"));
		return criteria.list();
	}
	
	public void insertProductAttributeByRebasing(List<Integer> oldGroupIds, Integer newGroupId){
		String sql = "INSERT INTO [ProductAttribute] ([ProductGroupId], [Sequence], [SpecificationName], [AttributeType], [Option], [IsMandatory], [CreatedDate], [ModifiedDate]) "
				+ "SELECT :productGroupId , ROW_NUMBER() OVER (ORDER BY [ProductGroupId], [Sequence]) AS [Row], [SpecificationName], [AttributeType], [Option], [IsMandatory], getDate(), getDate() FROM [ProductAttribute] "
				+ "WHERE [ProductGroupId] IN ( :oldGroupIds )";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("productGroupId", newGroupId);
		query.setParameterList("oldGroupIds", oldGroupIds);
		
		query.executeUpdate();
	}
	
	public List<Integer> getExistingProductAttributeId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
}
