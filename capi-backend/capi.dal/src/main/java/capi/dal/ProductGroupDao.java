package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.ProductGroup;
import capi.model.api.dataSync.ProductGroupSyncData;
import capi.model.dataImportExport.ImportRebasingProductGroupList;
import capi.model.productMaintenance.ProductGroupEditModel;
import capi.model.productMaintenance.ProductGroupTableList;
import capi.model.productMaintenance.ProductSpecificationEditModel;

@Repository("ProductGroupDao")
public class ProductGroupDao extends GenericDao<ProductGroup> {

	@SuppressWarnings("unchecked")
	public List<ProductGroupTableList> listProductGroup(String search,
			int firstRecord, int displayLenght, Order order) {

		Date today = new Date();
		
		Criteria criteria = this.createCriteria("g")
								.createAlias("g.products","p", JoinType.LEFT_OUTER_JOIN, Restrictions.eq("p.status", "Active"))
								.createAlias("g.productAttributes", "a", JoinType.LEFT_OUTER_JOIN)
								.createAlias("g.units", "u", JoinType.LEFT_OUTER_JOIN, 
										Restrictions.and(
											Restrictions.eq("u.status", "Active"),
											Restrictions.or(
													Restrictions.isNull("u.obsoleteDate"),
													Restrictions.gt("u.obsoleteDate", today)
											)
										));

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("g.productGroupId"),"productGroupId");
		projList.add(Projections.groupProperty("g.code"),"code");
		projList.add(Projections.groupProperty("g.chineseName"),"chineseName");
		projList.add(Projections.groupProperty("g.englishName"),"englishName");
		projList.add(Projections.countDistinct("p.productId"),"noOfProduct");
		projList.add(Projections.countDistinct("a.productAttributeId"),"noOfAttribute");
		projList.add(Projections.countDistinct("u.unitId"),"noOfUnit");
		projList.add(Projections.groupProperty("g.status"),"status");
		
		criteria.setProjection(projList);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("g.code", "%" + search + "%"),
					Restrictions.like("g.englishName", "%" + search + "%"),
					Restrictions.like("g.chineseName", "%" + search + "%"),
					Restrictions.like("g.status", "%" + search + "%")
				));
		}
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLenght);
		criteria.addOrder(order);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProductGroupTableList.class));
		
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<ProductGroup> searchProductGroup(String search,
			int firstRecord, int displayLenght, Order order) {

		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("status", "Active"));
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%")));
		}
		criteria.add(Restrictions.or(
			Restrictions.isNull("effectiveDate"),
			Restrictions.le("effectiveDate", today)
		));
		
		criteria.add(Restrictions.or(
			Restrictions.isNull("obsoleteDate"),
			Restrictions.gt("obsoleteDate", today)
		));
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLenght);
		criteria.addOrder(order);
			
		return criteria.list();
	}
	

	public long countSearchProductGroup(String search) {
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.eq("status", "Active"));
		
		if (!StringUtils.isEmpty(search)) {
		criteria.add(Restrictions.or(
				Restrictions.like("code", "%" + search + "%"),
				Restrictions.like("englishName", "%" + search + "%"),
				Restrictions.like("chineseName", "%" + search + "%")));
		}
		
		criteria.add(Restrictions.or(
				Restrictions.isNull("effectiveDate"),
				Restrictions.le("effectiveDate", today)
			));
			
		criteria.add(Restrictions.or(
			Restrictions.isNull("obsoleteDate"),
			Restrictions.gt("obsoleteDate", today)
		));

		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductSpecificationEditModel> getProductSpecificationEditModelByProductGroupId(Integer id) {

		Criteria criteria = this.createCriteria("g").createAlias("g.productAttributes","a", JoinType.INNER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("a.productAttributeId"),"productAttributeId");	
		projList.add(Projections.property("a.sequence"),"sequence");
		projList.add(Projections.property("a.option"),"option");
		projList.add(Projections.property("a.specificationName"),"name");
		projList.add(Projections.property("a.isMandatory"),"isMandatory");
		projList.add(Projections.property("a.attributeType"),"attributeType");		
		criteria.setProjection(projList).add(Restrictions.eq("g.productGroupId", id));
		
		criteria.addOrder(Order.asc("a.sequence"));
		criteria.setResultTransformer(Transformers.aliasToBean(ProductSpecificationEditModel.class));
		
		return (List<ProductSpecificationEditModel>) criteria.list();
	}
	
	public long countProductGroup(String search) {
		
//		Criteria criteria = this.createCriteria();
		
		Date today = new Date();
		Criteria criteria = this.createCriteria("g")
								.createAlias("g.products","p", JoinType.LEFT_OUTER_JOIN)
								.createAlias("g.productAttributes", "a", JoinType.LEFT_OUTER_JOIN)
								.createAlias("g.units", "u", JoinType.LEFT_OUTER_JOIN, 
									Restrictions.or(
										Restrictions.isNull("u.obsoleteDate"),
										Restrictions.gt("u.obsoleteDate", today)
									));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(Projections.groupProperty("g.productGroupId")));
		
		criteria.setProjection(projList);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%")));
		}

//		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		return (long) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductGroup> getProductGroupsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("id", ids));
		return criteria.list();
	}
	
	
	public ProductGroup getProductGroupsByCode(String code){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("code", code));
		return (ProductGroup) criteria.uniqueResult();
	}
	
	public ProductGroupEditModel getProductGroupEditModelById(Integer id) {

		Criteria criteria = this.createCriteria("g").createAlias("g.products","p", JoinType.LEFT_OUTER_JOIN).createAlias("g.units", "u", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("g.productGroupId"),"productGroupId");
		projList.add(Projections.groupProperty("g.code"),"code");
		projList.add(Projections.groupProperty("g.chineseName"),"chineseName");
		projList.add(Projections.groupProperty("g.englishName"),"englishName");
		projList.add(Projections.groupProperty("g.createdDate"),"createdDateDisplay");
		projList.add(Projections.groupProperty("g.modifiedDate"),"modifiedDateDisplay");
		projList.add(Projections.groupProperty("g.status"),"status");
		projList.add(Projections.count("p.productId"),"noOfProduct");
		projList.add(Projections.count("u.unitId"),"noOfUnit");

		
		criteria.setProjection(projList).add(Restrictions.eq("g.productGroupId", id));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProductGroupEditModel.class));
		
		return (ProductGroupEditModel) criteria.uniqueResult();
	}
	
	public List<ProductGroupSyncData> getUpdateProductGroup(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("productGroupId"), "productGroupId");
		projList.add(Projections.property("code"), "code");
		projList.add(Projections.property("chineseName"), "chineseName");
		projList.add(Projections.property("englishName"), "englishName");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		projList.add(Projections.property("status"), "status");
		projList.add(Projections.property("effectiveDate"), "effectiveDate");
		projList.add(Projections.property("obsoleteDate"), "obsoleteDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(ProductGroupSyncData.class));
		return criteria.list();
	}
	
	public ScrollableResults getAllProductGroupResult(){
		Criteria criteria = this.createCriteria();
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<ProductGroup> getNotExistedProductGroup(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("productGroupId", ids)));
		return criteria.list();
	}
	
	//For Rebasing
	//Update Old Product Group Code to New Product Group Code
	public void updateProductGroupByRebasing(Integer productGroupId, String newCode, String chineseName, String englishName, Date effectiveDate){
		String sql = "UPDATE [ProductGroup] SET [NewCode] = :code , [NewChineseName] = :chineseName , [NewEnglishName] = :englishName , [NextEffectiveDate] = :effectiveDate , [ModifiedDate] = getDate() where [ProductGroupId] = :productGroupId ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("code", newCode);
		query.setParameter("chineseName", chineseName);
		query.setParameter("englishName", englishName);
		query.setParameter("effectiveDate", effectiveDate);
		query.setParameter("productGroupId", productGroupId);
		
		query.executeUpdate();
	}
	
	//Insert New Product Group By Rebasing
	public void insertProductGroupByRebasing(String values, Date effectiveDate, List<ImportRebasingProductGroupList> newProductGroupList){
		String sql = "INSERT INTO [ProductGroup] ([Code], [ChineseName], [EnglishName], [Status], [EffectiveDate], [CreatedDate], [ModifiedDate]) VALUES "+values;
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		int i = 0;
		for(ImportRebasingProductGroupList newGroup : newProductGroupList){
			if(newGroup.getIsNewProductCategory()){
				query.setParameter("code"+i, newGroup.getNewCode());
				query.setParameter("chineseName"+i, newGroup.getChineseName());
				query.setParameter("englishName"+i, newGroup.getEnglishName());
				i++;
			}
		}
		
		query.setParameter("effectiveDate", effectiveDate);
		
		query.executeUpdate();
	}
	
	public void updateProductGroupObsoleteDateByRebasing(List<Integer> oldIds, Date obsoleteDate){
		String sql = "UPDATE [ProductGroup] SET [ObsoleteDate] = :obsoleteDate where [ProductGroupId] in :ids ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("obsoleteDate", obsoleteDate);
		query.setParameterList("ids", oldIds);
		
		query.executeUpdate();
	}
	
	public List<Integer> getExistingProductGroupId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<ProductGroup> getProductGroupByObsoleteDate(){
		String hql = "Select distinct pg"
				+ " From ProductGroup as pg"
				+ " where (pg.obsoleteDate is null or pg.obsoleteDate > getDate()) and pg.status = :status";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("status", "Active");
		
		return query.list();
	}
}
