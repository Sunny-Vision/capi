package capi.dal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import capi.entity.Product;
import capi.model.api.dataSync.OutletSyncData;
import capi.model.api.dataSync.ProductSyncData;
import capi.model.api.dataSync.ProductZipImageSyncData;
import capi.model.api.dataSync.UpdateProductImageModel;
import capi.model.commonLookup.ProductLookupDetailTableList;
import capi.model.commonLookup.ProductLookupTableList;
import capi.model.productMaintenance.ProductEditModel;
import capi.model.productMaintenance.ProductSpecificationEditModel;
import capi.model.productMaintenance.ProductTableList;
import capi.model.report.ProductReviewReport;

@Repository("ProductDao")
public class ProductDao  extends GenericDao<Product>{

	@SuppressWarnings("unchecked")
	public List<ProductLookupTableList> getLookupTableList(String search,
			int firstRecord, int displayLenght, Order order,
			String status, Boolean reviewed, Integer productGroupId, Integer skipProductId, String barcode, Integer[] selectedIds) {

		String hql = "select p.productId as id, "
                + " case when spec1.productSpecificationId is null then '' "
                + "   else concat(spec1.specificationName, '=', spec1.value) end as productAttribute1, "
                + " case when spec2.productSpecificationId is null then '' "
                + "   else concat(spec2.specificationName, '=', spec2.value) end as productAttribute2, "
                + " case when spec3.productSpecificationId is null then '' "
                + "   else concat(spec3.specificationName, '=', spec3.value) end as productAttribute3,"
                + " count (distinct q.quotationId) as numberOfQuotations "
                + " , p.remark as remark, p.status as status, p.reviewed as reviewed, "
                + " concat(g.code, ' - ', g.chineseName) as category, "
                + " p.barcode as barcode "
                + " from Product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join p.quotations as q "
                + " left join p.productGroup as g "
                + " where (str(p.id) like :search or p.remark like :search or p.status like :search"
                + " or spec1.specificationName like :search or spec1.value like :search"
                + " or spec2.specificationName like :search or spec2.value like :search"
                + " or spec3.specificationName like :search or spec3.value like :search"
                + " or g.code like :search or g.chineseName like :search"
                + " ) "
                + (StringUtils.isNotEmpty(status)?  " and p.status = :status " : "")
                + (reviewed != null ?  " and p.reviewed = :reviewed " : "")
                + (productGroupId != null ?  " and p.productGroup.id = :productGroupId " : "")
                + (skipProductId != null ?  " and p.id <> :skipProductId " : "")
                + (barcode != null ?  " and p.barcode = :barcode " : "")
                + (selectedIds != null && selectedIds.length > 0 ? " and p.productId not in (:selectedIds) " : "")
                + " group by p.productId, "
                + " spec1.productSpecificationId,spec1.specificationName,spec1.value,"
                + " spec2.productSpecificationId,spec2.specificationName,spec2.value,"
                + " spec3.productSpecificationId,spec3.specificationName,spec3.value, "
                + " p.remark, p.status, p.reviewed,"
                + " g.code, g.chineseName,"
                + " p.barcode"
                + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if (reviewed != null) {
			query.setParameter("reviewed", reviewed);
		}
		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		if (skipProductId != null) {
			query.setParameter("skipProductId", skipProductId);
		}
		if (barcode != null) {
			query.setParameter("barcode", barcode);
		}
		if(selectedIds != null && selectedIds.length > 0) {
			query.setParameterList("selectedIds", Arrays.asList(selectedIds));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(ProductLookupTableList.class));

		return query.list();
	}
	
	public long countLookupTableList(String search, String status, Boolean reviewed, Integer productGroupId, Integer skipProductId,
			String barcode, Integer[] selectedIds) {

		String hql = "select p.productId"
                + " from Product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join p.quotations as q "
                + " left join p.productGroup as g "
                + " where (str(p.id) like :search or p.remark like :search or p.status like :search"
                + " or spec1.specificationName like :search or spec1.value like :search"
                + " or spec2.specificationName like :search or spec2.value like :search"
                + " or spec3.specificationName like :search or spec3.value like :search"
                + " or g.code like :search or g.chineseName like :search"
                + " ) "
                + (StringUtils.isNotEmpty(status)?  " and p.status = :status " : "")
                + (reviewed != null ?  " and p.reviewed = :reviewed " : "")
                + (productGroupId != null ?  " and p.productGroup.id = :productGroupId " : "")
                + (skipProductId != null ?  " and p.id <> :skipProductId " : "")
                + (barcode != null ?  " and p.barcode = :barcode " : "")
                + (selectedIds != null && selectedIds.length > 0 ? " and p.productId not in (:selectedIds) " : "")
                //+ " group by p.productId";
                + " group by p.productId, "
                + " spec1.productSpecificationId,spec1.specificationName,spec1.value,"
                + " spec2.productSpecificationId,spec2.specificationName,spec2.value,"
                + " spec3.productSpecificationId,spec3.specificationName,spec3.value, "
                + " p.remark, p.status, p.reviewed,"
                + " g.code, g.chineseName,"
                + " p.barcode";
		
		String countHql = "select count(*) from Product as p where p.productId in ("+hql+")";

		Query query = getSession().createQuery(countHql);

		query.setParameter("search", String.format("%%%s%%", search));
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if (reviewed != null) {
			query.setParameter("reviewed", reviewed);
		}
		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		if (skipProductId != null) {
			query.setParameter("skipProductId", skipProductId);
		}
		if (barcode != null) {
			query.setParameter("barcode", barcode);
		}
		if(selectedIds != null && selectedIds.length > 0) {
			query.setParameterList("selectedIds", Arrays.asList(selectedIds));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getLookupTableSelectAll(String search, String status, Boolean reviewed, Integer productGroupId, Integer skipProductId, String barcode) {

		String hql = "select p.productId as id"
                + " from Product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join p.quotations as q "
                + " left join p.productGroup as g "
                + " where (str(p.id) like :search or p.remark like :search or p.status like :search"
                + " or spec1.specificationName like :search or spec1.value like :search"
                + " or spec2.specificationName like :search or spec2.value like :search"
                + " or spec3.specificationName like :search or spec3.value like :search"
                + " or g.code like :search or g.chineseName like :search"
                + " ) "
                + (StringUtils.isNotEmpty(status)?  " and p.status = :status " : "")
                + (reviewed != null ?  " and p.reviewed = :reviewed " : "")
                + (productGroupId != null ?  " and p.productGroup.id = :productGroupId " : "")
                + (skipProductId != null ?  " and p.id <> :skipProductId " : "")
                + (barcode != null ?  " and p.barcode = :barcode " : "")
                + " group by p.productId, "
                + " spec1.productSpecificationId,spec1.specificationName,spec1.value,"
                + " spec2.productSpecificationId,spec2.specificationName,spec2.value,"
                + " spec3.productSpecificationId,spec3.specificationName,spec3.value, "
                + " p.remark, p.status, p.reviewed";

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if (reviewed != null) {
			query.setParameter("reviewed", reviewed);
		}
		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		if (skipProductId != null) {
			query.setParameter("skipProductId", skipProductId);
		}
		if (barcode != null) {
			query.setParameter("barcode", barcode);
		}

		return (List<Integer>)query.list();
	}

	public long countByProductGroupId(Integer productGroupId) {
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.eq("productGroupId", productGroupId));				
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductTableList> listProduct(String search, Integer productGroupId, String hasAnyLinkage, String status, Boolean reviewed,
			int firstRecord, int displayLenght, Order order) {

		String hql = "select p.productId as productId, "
				+ " g.productGroupId as productGroupId, "
				+ " g.code as productGroupCode, "
				+ " g.chineseName as productGroupChineseName, "
				+ " g.englishName as productGroupEnglishName, "
                + " case when spec1.value is null then '' "
                + "   else concat(spec1.specificationName, '=', spec1.value) end as productAttribute1, "
                + " case when spec2.value is null then '' "
                + "   else concat(spec2.specificationName, '=', spec2.value) end as productAttribute2, "
                + " case when spec3.value is null then '' "
                + "   else concat(spec3.specificationName, '=', spec3.value) end as productAttribute3, "
                + " case when spec4.value is null then '' "
                + "   else concat(spec4.specificationName, '=', spec4.value) end as productAttribute4, "
                + " case when spec5.value is null then '' "
                + "   else concat(spec5.specificationName, '=', spec5.value) end as productAttribute5, "
                // updated 2020-10-12: CR12 comment product table list loading slowly
                // use subquery to speed up count quotation
                + " (select count(distinct q.quotationId) from p.quotations as q) as noOfQuotation, "
                // 2020-11-11: count total # of entity references of product, for user to delete product without any FK refs
                // (related quotation/product specification records will be delinked before delete product)
                + " (select count(distinct qr.quotationRecordId) from p.quotationRecords as qr) as noOfQuotationRecord, "
                + " (select count(distinct qnewp.quotationId) from p.quotationsNewProduct as qnewp) as noOfQuotationNewProduct, "
                + " (select count(distinct ptn.pointToNoteId) from p.pointToNotes as ptn where p in elements(ptn.products)) as noOfPointToNote, "
                + " p.remark as remark, p.status as status, p.reviewed as reviewed, "
                + " p.createdDate as createdDate "
                + ", p.barcode as barcode "
                + " from Product as p "
                + " join p.productGroup as g "
//                + " left join p.quotations as q "
                + " left join p.distinctSpecificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.distinctSpecificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.distinctSpecificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join p.distinctSpecificationViews as spec4 "
                + "   on spec4.sequence = 4 "
                + " left join p.distinctSpecificationViews as spec5 "
                + "   on spec5.sequence = 5 "
                + " where 1 = 1 ";
		
		if(search != null && search.length() > 0) {
			hql += " and (str(p.id) like :search or p.remark like :search or p.status like :search "
                + " or concat(trim(str(month(p.createdDate))),'-',trim(str(year(p.createdDate)))) like :search "
                + " or str(g.productGroupId) like :search or g.code like :search "
                + " or g.chineseName like :search or g.englishName like :search "
                + " or spec1.specificationName like :search or spec1.value like :search"
                + " or spec2.specificationName like :search or spec2.value like :search"
                + " or spec3.specificationName like :search or spec3.value like :search"
                + " or spec4.specificationName like :search or spec4.value like :search"
                + " or spec5.specificationName like :search or spec5.value like :search"
                + " or p.barcode like :search "
                + " ) ";
		}
		
		if(status != null && status.length() > 0) {
			hql += " and p.status = :status ";
		}
		
		if(reviewed != null) {
			hql += " and p.reviewed = :reviewed ";
		}
		
		if(productGroupId != null && productGroupId > 0) {
			hql += " and g.productGroupId = :productGroupId ";
		}
		
        // 2020-11-11: count total # of entity references of product, for user to delete product without any FK refs
		if (hasAnyLinkage != null && hasAnyLinkage.length() > 0) {
			if (hasAnyLinkage.equals("Y")) {
				hql += "and ("
//						+ "   (select count(distinct q.quotationId) from p.quotations as q) > 0 "
						+ "   (select count(distinct qr.quotationRecordId) from p.quotationRecords as qr) > 0 "
						+ "   or (select count(distinct qnewp.quotationId) from p.quotationsNewProduct as qnewp) > 0 "
						+ "   or (select count(distinct ptn.pointToNoteId) from p.pointToNotes as ptn where p in elements(ptn.products)) > 0 "
						+ " ) ";
			} else if (hasAnyLinkage.equals("N")) {
				hql += "and ("
//						+ "   (select count(distinct q.quotationId) from p.quotations as q) = 0 "
						+ "   (select count(distinct qr.quotationRecordId) from p.quotationRecords as qr) = 0 "
						+ "   and (select count(distinct qnewp.quotationId) from p.quotationsNewProduct as qnewp) = 0 "
						+ "   and (select count(distinct ptn.pointToNoteId) from p.pointToNotes as ptn where p in elements(ptn.products)) = 0 "
						+ " ) ";
			}
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if(search != null && search.length() > 0) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if (reviewed != null) {
			query.setParameter("reviewed", reviewed);
		}
		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);
		
		query.setResultTransformer(Transformers.aliasToBean(ProductTableList.class));
		
		return query.list();
	}
	
	public long countProduct(String search, Integer productGroupId, String hasAnyLinkage, String status, Boolean reviewed) {
		// changed 2020-10-08: CR12 search filter for indoor quotation record
		String hql = "select distinct p.productId " // list distinct products, count result below
                + " from Product as p "
                + " join p.productGroup as g"
                + " left join p.distinctSpecificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.distinctSpecificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.distinctSpecificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join p.distinctSpecificationViews as spec4 "
                + "   on spec4.sequence = 4 "
                + " left join p.distinctSpecificationViews as spec5 "
                + "   on spec5.sequence = 5 "
                + " left join p.quotationRecords qr "
                + " left join qr.indoorQuotationRecord iqr "
                + " where 1 = 1 ";
		
        if(search != null && search.length() > 0) {
        	hql += " and (str(p.id) like :search or p.remark like :search or p.status like :search "
                + " or concat(trim(str(month(p.createdDate))),'-',trim(str(year(p.createdDate)))) like :search "
                + " or str(g.productGroupId) like :search or g.code like :search "
                + " or g.chineseName like :search or g.englishName like :search "
                + " or spec1.specificationName like :search or spec1.value like :search"
                + " or spec2.specificationName like :search or spec2.value like :search"
                + " or spec3.specificationName like :search or spec3.value like :search"
                + " or spec4.specificationName like :search or spec4.value like :search"
                + " or spec5.specificationName like :search or spec5.value like :search"
                + " or p.barcode like :search "
                + " ) ";
        }
        
        if(status != null && status.length() > 0) {
        	hql += " and p.status = :status ";
        }
        
        if(reviewed != null) {
        	hql += " and p.reviewed = :reviewed ";
        }
        
        if(productGroupId != null && productGroupId > 0) {
        	hql += " and g.productGroupId = :productGroupId ";
        }
        
        // added 2020-10-07: CR12 search filter for indoor quotation record
        // 2020-11-11: count total # of entity references of product, for user to delete product without any FK refs
		if (hasAnyLinkage != null && hasAnyLinkage.length() > 0) {
			if (hasAnyLinkage.equals("Y")) {
				hql += "and ("
//						+ "   (select count(distinct q.quotationId) from p.quotations as q) > 0 "
						+ "   (select count(distinct qr.quotationRecordId) from p.quotationRecords as qr) > 0 "
						+ "   or (select count(distinct qnewp.quotationId) from p.quotationsNewProduct as qnewp) > 0 "
						+ "   or (select count(distinct ptn.pointToNoteId) from p.pointToNotes as ptn where p in elements(ptn.products)) > 0 "
						+ " ) ";
			} else if (hasAnyLinkage.equals("N")) {
				hql += "and ("
//						+ "   (select count(distinct q.quotationId) from p.quotations as q) = 0 "
						+ "   (select count(distinct qr.quotationRecordId) from p.quotationRecords as qr) = 0 "
						+ "   and (select count(distinct qnewp.quotationId) from p.quotationsNewProduct as qnewp) = 0 "
						+ "   and (select count(distinct ptn.pointToNoteId) from p.pointToNotes as ptn where p in elements(ptn.products)) = 0 "
						+ " ) ";
			}
		}
		
		Query query = getSession().createQuery(hql);

		if(search != null && search.length() > 0) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if (reviewed != null) {
			query.setParameter("reviewed", reviewed);
		}
		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}

		return query.list().size();
	}
	
	public ProductEditModel getProductEditModelById(Integer id) {

		Criteria criteria = this.createCriteria("p").createAlias("p.productGroup","g", JoinType.INNER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("p.productId"),"productId");
		projList.add(Projections.property("p.countryOfOrigin"),"countryOfOrigin");
		projList.add(Projections.property("g.productGroupId"),"productGroupId");
		projList.add(Projections.property("g.code"),"productGroupCode");
		projList.add(Projections.property("g.englishName"),"productGroupEnglishName");
		projList.add(Projections.property("g.chineseName"),"productGroupChineseName");
		projList.add(Projections.property("p.barcode"),"barcode");
		projList.add(Projections.property("p.photo1Path"),"photo1Path");
		projList.add(Projections.property("p.photo2Path"),"photo2Path");
		projList.add(Projections.property("p.remark"),"remark");
		projList.add(Projections.property("p.status"),"status");
		projList.add(Projections.property("p.reviewed"),"reviewed");
		projList.add(Projections.property("p.createdDate"),"createdDateDisplay");
		projList.add(Projections.property("p.modifiedDate"),"modifiedDateDisplay");
		
		criteria.setProjection(projList).add(Restrictions.eq("p.productId", id));
		criteria.setResultTransformer(Transformers.aliasToBean(ProductEditModel.class));
		
		return (ProductEditModel) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductSpecificationEditModel> getProductSpecificationEditModelById(Integer id) {
		
		Criteria criteria = this.createCriteria("p").createAlias("p.specificationViews","s", JoinType.INNER_JOIN).createAlias("s.productAttribute","a", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("s.productSpecificationId"),"productSpecificationId");
		projList.add(Projections.property("a.productAttributeId"),"productAttributeId");
		projList.add(Projections.property("a.attributeType"),"attributeType");
		projList.add(Projections.property("s.sequence"),"sequence");
		projList.add(Projections.property("s.option"),"option");
		projList.add(Projections.property("s.specificationName"),"name");
		projList.add(Projections.property("s.isMandatory"),"isMandatory");
		projList.add(Projections.property("s.value"),"value");
		
		criteria.setProjection(projList).add(Restrictions.eq("p.productId", id));
		
		criteria.addOrder(Order.asc("s.sequence"));
		criteria.setResultTransformer(Transformers.aliasToBean(ProductSpecificationEditModel.class));
		
		return (List<ProductSpecificationEditModel>) criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Product> getProductByIds(List<Integer> productIds) {
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.in("productId", productIds));
		return (List<Product>) criteria.list();
	}
	
	public boolean checkProductById(Integer productId) {
		Criteria criteria = this.createCriteria();		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("productId"));
		criteria.setProjection(projList).add(Restrictions.eq("productId", productId));
		return (criteria.list().size() > 0);
	}

	@SuppressWarnings("unchecked")
	public List<ProductLookupDetailTableList> getTableListByIds(Integer[] ids) {

		String hql = "select p.productId as id, "
                + " case when spec1.productSpecificationId is null then '' "
                + "   else concat(spec1.specificationName, '=', spec1.value) end as productAttribute1, "
                + " case when spec2.productSpecificationId is null then '' "
                + "   else concat(spec2.specificationName, '=', spec2.value) end as productAttribute2, "
                + " case when spec3.productSpecificationId is null then '' "
                + "   else concat(spec3.specificationName, '=', spec3.value) end as productAttribute3, "
                + " case when spec4.productSpecificationId is null then '' "
                + "   else concat(spec4.specificationName, '=', spec4.value) end as productAttribute4, "
                + " case when spec5.productSpecificationId is null then '' "
                + "   else concat(spec5.specificationName, '=', spec5.value) end as productAttribute5, "
                + " count (distinct q.quotationId) as numberOfQuotations "
                + " , p.remark as remark, p.status as status, p.reviewed as reviewed "
                + " , concat(g.code, ' - ', g.chineseName) as category "
                + " from Product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join p.specificationViews as spec4 "
                + "   on spec4.sequence = 4 "
                + " left join p.specificationViews as spec5 "
                + "   on spec5.sequence = 5 "
                + " left join p.quotations as q "
                + " left join p.productGroup as g "
                + " where 1 = 1 "
                + (ids != null && ids.length > 0 ?  " and p.id in :ids " : "")
                + " group by p.productId, "
                + " spec1.productSpecificationId,spec1.specificationName,spec1.value, "
                + " spec2.productSpecificationId,spec2.specificationName,spec2.value, "
                + " spec3.productSpecificationId,spec3.specificationName,spec3.value, "
                + " spec4.productSpecificationId,spec4.specificationName,spec4.value, "
                + " spec5.productSpecificationId,spec5.specificationName,spec5.value, "
                + " p.remark, p.status, p.reviewed "
                + " , g.code, g.chineseName";

		Query query = getSession().createQuery(hql);

		if (ids != null && ids.length > 0) {
			query.setParameterList("ids", ids);
		}

		query.setResultTransformer(Transformers.aliasToBean(ProductLookupDetailTableList.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getSelect2List(String search,
			int firstRecord, int displayLenght, Order order, Integer productGroupId) {

		String hql = "select p.productId as id "
                + " from Product as p "
                + " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
               hql += " and (str(p.id) like :search )";
		}
        hql += (productGroupId != null ?  " and p.productGroup.id = :productGroupId " : "")
                + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		return query.list();
	}
	
	public long countSelect2List(String search, Integer productGroupId) {

		String hql = " select count(distinct p.productId) "
				+ " from Product as p "
                + " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
               hql += " and (str(p.id) like :search )";
		}
        hql += (productGroupId != null ?  " and p.productGroup.id = :productGroupId " : "");

		Query query = getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public ScrollableResults getProductResultByProductGroupId(Integer productGroupId){
		Criteria criteria = this.createCriteria("p");
		criteria.createAlias("p.productGroup", "g", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("g.productGroupId", productGroupId));
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<Product> getNotExistedProductByProductGroupIdAndProductId(List<Integer> ids, Integer productGroupId){
		Criteria criteria = this.createCriteria()
				.createAlias("productGroup", "productGroup");
		criteria.add(Restrictions.eq("productGroup.productGroupId", productGroupId));
		criteria.add(Restrictions.not(Restrictions.in("productId", ids)));
		return criteria.list();
	}
	
	public List<Product> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("productId", ids));
		return criteria.list();
	}
	
	//For Rebasing
	public List<Product> getProductByProductGroupIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("productGroup.productGroupId", ids)).addOrder(Order.asc("productId"));
		return criteria.list();
	}
	
	
	public List<Product> getProductByProductGroupId(Integer id){
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		return getProductByProductGroupIds(ids);
	}
	
	public void insertProductByRebasing(List<Integer> oldGroupIds, Integer newGroupId, Date effectiveDate){
		String sql = "INSERT INTO [Product] ( [CountryOfOrigin], [ProductGroupId], [Photo1Path], [Photo1ModifiedTime], [Photo2Path], [Photo2ModifiedTime], [Remark], [Status], [Reviewed], [Barcode], [EffectiveDate], [CreatedDate], [ModifiedDate] ) "
				+ "SELECT [CountryOfOrigin], :productGroupId , [Photo1Path], [Photo1ModifiedTime], [Photo2Path], [Photo2ModifiedTime], [Remark], 'Active', [Reviewed], [Barcode], :effectiveDate , getDate(), getDate() FROM [Product] "
				+ "WHERE [ProductGroupId] in ( :oldGroupIds ) order by [ProductId]";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("productGroupId", newGroupId);
		query.setParameter("effectiveDate", effectiveDate);
		query.setParameterList("oldGroupIds", oldGroupIds);
		
		query.executeUpdate();
	}
	
	public void updateProductByRebasing(List<Integer> oldGroupIds, Date obsoleteDate){
		String sql = "UPDATE [Product] SET [ObsoleteDate] = :obsoleteDate where [ProductGroupId] in :ids ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("obsoleteDate", obsoleteDate);
		query.setParameterList("ids", oldGroupIds);
		
		query.executeUpdate();
	}
	
	/**
	 * ProductReviewReport
	 **/
//	public List<ProductReviewReport> getProductReviewReport(List<Integer> productGroupIds, Date refMonth, String reviewed, ArrayList<Integer> purpose){
	public List<Map<String, Object>> getProductReviewReport(List<Integer> productGroupIds, Date refMonth, String reviewed, ArrayList<Integer> purpose){
		//For Dynamic Generate SQL for Product Attributes (Current set to fixed value = 18)
		String productAttributesSQL = "";
		String productAttributesJoinTable = "";
		String productAttributesGroupBy = "";
		int maxSeq = 25;
		for(int i=1; i<maxSeq+1; i++){
			String prodAttr = "prodAttr" + i;
			String prodSpec = "prodSpec" + i;
			String productAttributes = "productAttributes" + i;
			
			productAttributesSQL += "case when ("+prodAttr+".SpecificationName is null and "+prodSpec+".Value is null) then '' else concat("+prodAttr+".SpecificationName,':',"+prodSpec+".Value) end as "+productAttributes + ",";
			
			productAttributesGroupBy += "," + prodAttr +".SpecificationName, " + prodSpec + ".Value"  ;
			productAttributesJoinTable += "left join ProductAttribute as "+prodAttr+" on "+prodAttr+".ProductGroupId = pg.ProductGroupId and "+prodAttr+".Sequence = "+String.valueOf(i)+" ";
			productAttributesJoinTable += "left join ProductSpecification as "+prodSpec+" on "+prodSpec+".ProductAttributeId = "+prodAttr+".ProductAttributeId and "+prodSpec+".ProductId = p.ProductId ";
		}
		
		String sql = "select p.ProductId as productId," + 
				"pg.ProductGroupId as productGroupId," + 
				"pg.Code as productGroupCode," + 
				"pg.ChineseName as productGroupChineseName," + 
				"pg.EnglishName as productGroupEnglishName," + 
				"p.CountryOfOrigin as countyOfOrigin," + 
				"q.count as noOfQuotation," + 
				"p.CreatedDate as productCreateDate," + 
				"p.Reviewed as productReviewed," + 
				"p.ModifiedDate as lastModifyDate," + 
				"p.ModifiedBy as lastModifyBy," + 
				//Product Attributes
				productAttributesSQL  +
				"u.EnglishName as lastModifyUser" + 
				" from Product p " + 
				"join ProductGroup pg on pg.ProductGroupId = p.ProductGroupId " + 
				"left join [user] u on p.ModifiedBy = u.Username " + 
				"left join (select count(*) as count ,productId from quotation where Status = 'Active' group by productId) q on q.productId = p.ProductId "
				+ "left join Quotation qu on qu.ProductId = p.ProductId "
				+ "left join Unit ut on ut.UnitId = qu.UnitId "
				+ "left join Purpose pp on pp.PurposeId = ut.PurposeId "
				+ productAttributesJoinTable
				+ "where 1=1 ";
		
		if(refMonth != null) {
			sql+= " and p.CreatedDate >= :refMonth ";
		}
		
		if(productGroupIds != null && productGroupIds.size() > 0) {
			sql+= " and pg.productGroupId in (:productGroupIds) ";
		}
		
		if(reviewed != null) {
			if(reviewed.equals("Y")) {
				sql+= " and p.Reviewed = 1 ";
			}else if(reviewed.equals("N")){
				sql+= " and p.Reviewed = 0 ";
			}
		}
		
		if(purpose != null && purpose.size() > 0) {
			sql += " and pp.PurposeId in (:purpose) ";
		}
		
		sql+= " group by p.ProductId,pg.ProductGroupId,pg.Code,pg.ChineseName,pg.EnglishName,p.CountryOfOrigin, q.count, "
				+ "p.CreatedDate,p.Reviewed,p.ModifiedDate,p.ModifiedBy,u.EnglishName "
				+ productAttributesGroupBy;
		sql+= " order by pg.code, p.CreatedDate ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if(refMonth != null) {
			query.setParameter("refMonth", refMonth);
		}
		
		if(productGroupIds != null && productGroupIds.size() > 0) {
			query.setParameterList("productGroupIds", productGroupIds);
		}
		
		if(purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		}
		

		query.addScalar("productId", StandardBasicTypes.STRING);
		query.addScalar("productGroupId", StandardBasicTypes.STRING);
		query.addScalar("productGroupCode", StandardBasicTypes.STRING);
		query.addScalar("productGroupChineseName", StandardBasicTypes.STRING);
		query.addScalar("productGroupEnglishName", StandardBasicTypes.STRING);
		query.addScalar("countyOfOrigin", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.STRING);
		query.addScalar("productCreateDate", StandardBasicTypes.DATE);
		query.addScalar("productReviewed", StandardBasicTypes.STRING);
		query.addScalar("lastModifyDate", StandardBasicTypes.DATE);
		query.addScalar("lastModifyBy", StandardBasicTypes.STRING);
		query.addScalar("lastModifyUser", StandardBasicTypes.STRING);
		
		for(int i=1; i<maxSeq+1; i++){
			String alias = "productAttributes" + i;
			query.addScalar(alias, StandardBasicTypes.STRING);
		}
		
		//Step1 - Get result set 		
		List<Object[]> cleanedObjects = new ArrayList<Object[]>();
		List<Object[]> object = query.list();
		List<Map<String, Object>> result = new ArrayList<>();

		if (!object.isEmpty()) {
			if (object.get(0) instanceof Object[]) {
				cleanedObjects = object;
			} else {
				Object[] row;
				for (int i = 0; i < object.size(); i++) {
					row = new Object[1];
					row[0] = object.get(i);
					cleanedObjects.add(row);
				}
			}
		
		
			//Step2 - Obtain the column aliases
			List<NativeSQLQueryReturn> resultHeaders = query.getQueryReturns();
			List<String> headers = new ArrayList<>();
			
			if(!resultHeaders.isEmpty()){
				
				for (NativeSQLQueryReturn row : resultHeaders) {
					if (row instanceof NativeSQLQueryReturn){
						NativeSQLQueryScalarReturn header = (NativeSQLQueryScalarReturn)row;
						headers.add(header.getColumnAlias());
					}
				}
				
				//Step3 - Covert Object(row of record) into List for getting Product Attributes
				for (Object[] obj : cleanedObjects){
					Map<String,Object> record = new HashMap<>();
					for(int i=0; i<obj.length; i++){
						record.put(headers.get(i), obj[i]);
					}
					result.add(record);
				}
				
			}
		}
		return result;
	}
	
	public List<ProductSyncData> getUpdatedProduct(Date lastSyncTime, Integer[] productIds){
		if (lastSyncTime != null){
			lastSyncTime = DateUtils.addSeconds(lastSyncTime, -5);
		}
		
		Criteria criteria = this.createCriteria("p")
				.createAlias("p.productGroup", "pg", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("p.productId"), "productId");
		projList.add(Projections.property("p.countryOfOrigin"), "countryOfOrigin");
		projList.add(Projections.property("pg.productGroupId"), "productGroupId");
		projList.add(Projections.property("p.photo1ModifiedTime"), "photo1ModifiedTime");
		projList.add(Projections.property("p.photo2ModifiedTime"), "photo2ModifiedTime");
		projList.add(Projections.property("p.remark"), "remark");
		projList.add(Projections.property("p.status"), "status");
		projList.add(Projections.property("p.reviewed"), "reviewed");
		projList.add(Projections.property("p.createdDate"), "createdDate");
		projList.add(Projections.property("p.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("p.barcode"), "barcode");
		projList.add(Projections.property("p.effectiveDate"), "effectiveDate");
		projList.add(Projections.property("p.obsoleteDate"), "obsoleteDate");
		
		if(lastSyncTime!=null){
			criteria.add(Restrictions.or(
					Restrictions.ge("p.modifiedDate", lastSyncTime),
					Restrictions.ge("p.createdDate", lastSyncTime)
					));
			
		}
			
		if(productIds!=null && productIds.length>0)
			criteria.add(Restrictions.in("p.productId", productIds));
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(ProductSyncData.class));
		return criteria.list();
	}
	
	public List<UpdateProductImageModel> getUpdateProductImage(Date lastSyncTime, int imageType){
		Criteria criteria = this.createCriteria("p");
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("p.productId"), "productId");
		if (imageType == 1){
			projList.add(Projections.property("p.photo1ModifiedTime"), "modifiedDate");
			criteria.add(Restrictions.gt("p.photo1ModifiedTime", lastSyncTime));
		}
		else {
			projList.add(Projections.property("p.photo2ModifiedTime"), "modifiedDate");
			criteria.add(Restrictions.gt("p.photo2ModifiedTime", lastSyncTime));
		}
		projList.add(Projections.sqlProjection("'"+imageType+"' as imageType", new String[]{"imageType"}, new Type[]{StandardBasicTypes.INTEGER}), "imageType");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(UpdateProductImageModel.class));
		
		return criteria.list();
	}
	
	public List<ProductLookupTableList> getLookupTableList(List<Integer> productIds) {

		String hql = "select p.productId as id, "
                + " case when spec1.productSpecificationId is null then '' "
                + "   else concat(spec1.specificationName, '=', spec1.value) end as productAttribute1, "
                + " case when spec2.productSpecificationId is null then '' "
                + "   else concat(spec2.specificationName, '=', spec2.value) end as productAttribute2, "
                + " case when spec3.productSpecificationId is null then '' "
                + "   else concat(spec3.specificationName, '=', spec3.value) end as productAttribute3,"
                + " count (distinct q.quotationId) as numberOfQuotations "
                + " , p.remark as remark, p.status as status, p.reviewed as reviewed, "
                + " concat(g.code, ' - ', g.chineseName) as category, "
                + " p.barcode as barcode "
                + " from Product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join p.quotations as q "
                + " left join p.productGroup as g "
                + " where 1=1 "
                + (productIds != null ?  " and p.id in (:productIds) " : "")
                + " group by p.productId, "
                + " spec1.productSpecificationId,spec1.specificationName,spec1.value,"
                + " spec2.productSpecificationId,spec2.specificationName,spec2.value,"
                + " spec3.productSpecificationId,spec3.specificationName,spec3.value, "
                + " p.remark, p.status, p.reviewed,"
                + " g.code, g.chineseName,"
                + " p.barcode";

		Query query = getSession().createQuery(hql);

		if (productIds != null && productIds.size() > 0) {
			query.setParameterList("productIds", productIds);
		}

		query.setResultTransformer(Transformers.aliasToBean(ProductLookupTableList.class));

		return query.list();
	}
	
	public List<String> getProductImageMonthly(String endDate, String startDate, Boolean isFirst){
		
		System.out.println("endDate :: " + endDate);
		System.out.println("startDate :: " + startDate);
		System.out.println("isFirst :: " + isFirst);
		
		
		String sql = ""
				+ " SELECT Photo1Path AS ImagePath "
				+ " FROM Product "
				+ " WHERE 1 = 1 "
				+ " AND Photo1Path <> '' AND Photo1Path IS NOT NULL ";
				
				if(!isFirst) {
					sql += " AND (Photo1ModifiedTime between '" + startDate + "' AND '" + endDate + "') ";
				}
				
				sql += " UNION ALL "
				+ " SELECT Photo2Path AS ImagePath "
				+ " FROM Product  "
				+ " WHERE 1 = 1  " 
				+ " AND Photo2Path <> '' AND Photo2Path IS NOT NULL ";
				if(!isFirst) {
					sql += " AND (Photo2ModifiedTime between '" + startDate + "' AND '" + endDate + "') ";
				}

		SQLQuery query = this.getSession().createSQLQuery(sql);

		query.addScalar("ImagePath", StandardBasicTypes.STRING);

		return query.list();
	}
	
	public List<ProductZipImageSyncData> getProductInfoMonthly(String endDate) {

		String sql = " "
				+ " SELECT Photo1Path AS photoPath, "
				+ " Photo1ModifiedTime AS photoModifiedTime, "
				+ " '1' AS type, "
				+ " ProductId AS productId"
				+ " FROM Product "
				+ " WHERE 1 = 1 "
				+ " AND Photo1Path <> '' AND Photo1Path IS NOT NULL"
				//+ " AND (Photo1ModifiedTime between '" + startDate + "' AND '" + endDate + "') "
				+ " AND (Photo1ModifiedTime <= '" + endDate + "') "
				+ " UNION ALL "
				+ " SELECT Photo2Path AS ImagePath, "
				+ " Photo2ModifiedTime AS ModifiedTime, "
				+ " '2' AS type, "
				+ " ProductId AS productId"
				+ " FROM Product  "
				+ " WHERE 1 = 1  "
				+ " AND Photo2Path <> '' AND Photo2Path IS NOT NULL"
				//+ " AND (Photo2ModifiedTime between '" + startDate + "' AND '" + endDate + "') ";
				+ " AND (Photo2ModifiedTime <= '" + endDate + "') ";
				
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("productId", StandardBasicTypes.INTEGER)
				.addScalar("photoModifiedTime", StandardBasicTypes.TIMESTAMP)
				.addScalar("photoPath", StandardBasicTypes.STRING)
				.addScalar("type", StandardBasicTypes.STRING);
		
		//query.setParameter("startDate", startDate);
		//query.setParameter("endDate", endDate);
		
		query.setResultTransformer(Transformers.aliasToBean(ProductZipImageSyncData.class));

		return query.list();
	}

	public List<Product> getProductWithPhotoPathButNullPhotoModifiedTime(){
		Criteria criteria = this.createCriteria();
		org.hibernate.criterion.LogicalExpression photo1NoModTime = 
				Restrictions.and(Restrictions.isNotNull("photo1Path"), Restrictions.isNull("photo1ModifiedTime"));
		org.hibernate.criterion.LogicalExpression photo2NoModTime = 
				Restrictions.and(Restrictions.isNotNull("photo2Path"), Restrictions.isNull("photo2ModifiedTime"));
		criteria.add(Restrictions.or(photo1NoModTime, photo2NoModTime));
		return criteria.list();
	}
	

}
