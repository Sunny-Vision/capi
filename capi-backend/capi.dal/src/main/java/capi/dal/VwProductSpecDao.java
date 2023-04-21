package capi.dal;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.VwProductSpec;
import capi.model.commonLookup.ProductAttributeLookupTableList;

@Repository("VwProductSpecDao")
public class VwProductSpecDao extends GenericDao<VwProductSpec> {

	@SuppressWarnings("unchecked")
	public List<ProductAttributeLookupTableList> getLookupTableList(String search,
			int firstRecord, int displayLenght, Order order,
			Integer productGroupId, Integer productAttributeId, Integer[] selectedIds) {
		String hql = "select vps.product.id as productId, value as attributeValue"
                + " from VwProductSpec as vps "
                + " where ( value like :search or str(vps.product.id) like :search  ) "
                + (productGroupId != null ?  " and vps.productGroup.id = :productGroupId " : "")
                + (productAttributeId != null ?  " and vps.productAttribute.id = :productAttributeId " : "")
                + (selectedIds != null && selectedIds.length > 0 ? " and vps.product.productId not in (:selectedIds) " : "")
                + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));

		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		if (productAttributeId != null) {
			query.setParameter("productAttributeId", productAttributeId);
		}
		if(selectedIds != null && selectedIds.length > 0) {
			query.setParameterList("selectedIds", Arrays.asList(selectedIds));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(ProductAttributeLookupTableList.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getSelect2Value(String search,
			int firstRecord, int displayLenght, Order order, Integer productGroupId, Integer productAttributeId) {
		
//		String hql = "select distinct value as attributeValue"
//                + " from VwProductSpec as vps "
//                + " where value like :search " 
//                + (productAttributeId != null ?  " and vps.productAttribute.id = :productAttributeId " : "")
//                + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		String hql = " select distinct vps.value as attributeValue "
				+ " from VwProductSpec as vps "
				+ " where vps.value is not null and vps.value != '' ";
		
		if(productGroupId != null) {
			hql += " and vps.productGroup.id = :productGroupId ";
		}
		if(productAttributeId != null) {
			hql += " and vps.productAttribute.id = :productAttributeId ";
		}
		if(search != null) {
			hql += " and vps.value like :search";
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if(search != null) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if(productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		if (productAttributeId != null) {
			query.setParameter("productAttributeId", productAttributeId);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		return query.list();
		
	}
	
	public long countSelect2Value(String search, Integer productGroupId, Integer productAttributeId) {
		/*
		String hql = "select count(distinct value)"
                + " from VwProductSpec as vps "
                + " where ( value like :search or str(vps.product.id) like :search ) "
                + (productAttributeId != null ?  " and vps.productAttribute.id = :productAttributeId " : "");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));

		if (productAttributeId != null) {
			query.setParameter("productAttributeId", productAttributeId);
		}

		return (long)query.uniqueResult();
		*/
		String hql = " select count(distinct vps.value) as cnt "
				+ " from VwProductSpec as vps "
				+ " where vps.value is not null and vps.value != '' ";
		
		if(productGroupId != null) {
			hql += " and vps.productGroup.id = :productGroupId ";
		}
		if(productAttributeId != null) {
			hql += " and vps.productAttribute.id = :productAttributeId ";
		}
		if(search != null) {
			hql += " and vps.value like :search";
		}
		
		Query query = getSession().createQuery(hql);

		if(search != null) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if(productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		if (productAttributeId != null) {
			query.setParameter("productAttributeId", productAttributeId);
		}

		return (long)query.uniqueResult();
	}
	
	public long countLookupTableList(String search,
			Integer productGroupId, Integer productAttributeId, Integer[] selectedIds) {
				
		String hql = "select count(distinct vps.product.id)"
                + " from VwProductSpec as vps "
                + " where ( value like :search or str(vps.product.id) like :search ) "
                + (productGroupId != null ?  " and vps.productGroup.id = :productGroupId " : "")
                + (productAttributeId != null ?  " and vps.productAttribute.id = :productAttributeId " : "")
                + (selectedIds != null && selectedIds.length > 0 ? " and vps.product.productId not in (:selectedIds) " : "");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));

		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		if (productAttributeId != null) {
			query.setParameter("productAttributeId", productAttributeId);
		}
		if(selectedIds != null && selectedIds.length > 0) {
			query.setParameterList("selectedIds", Arrays.asList(selectedIds));
		}

		return (long)query.uniqueResult();
		
	}
	
	public List<Integer> getLookupTableSelectAll(String search,
			Integer productGroupId, Integer productAttributeId) {
		return getLookupTableSelectAll( search,
			 productGroupId, productAttributeId, true);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getLookupTableSelectAll(String search,
			Integer productGroupId, Integer productAttributeId, boolean includeProductId) {
		
		String hql = "select vps.product.id"
                + " from VwProductSpec as vps "
                + " where ( value like :search "
                + (includeProductId ? " or str(vps.product.id) like :search " : "")
                + " ) "
                + (productGroupId != null ?  " and vps.productGroup.id = :productGroupId " : "")
                + (productAttributeId != null ?  " and vps.productAttribute.id = :productAttributeId " : "");

		Query query = getSession().createQuery(hql);

		query.setParameter("search", String.format("%%%s%%", search));

		if (productGroupId != null) {
			query.setParameter("productGroupId", productGroupId);
		}
		if (productAttributeId != null) {
			query.setParameter("productAttributeId", productAttributeId);
		}

		return query.list();
		
	}	
	
	public String getValuesByProductAttributeId(Integer productAttributeId) {
		
		String hql = "select value"
                + " from VwProductSpec as vps "
                + " where "
                + " vps.productAttribute.id = :productAttributeId ";

		Query query = getSession().createQuery(hql);

		if (productAttributeId != null) {
			query.setParameter("productAttributeId", productAttributeId);
		}
		@SuppressWarnings("unchecked")
		List<Integer> values = query.list();
		
		StringBuilder sbStr = new StringBuilder();
	    for (int i = 0, il = values.size(); i < il; i++) {
	        if (i > 0)
	            sbStr.append(";");
	        sbStr.append(values.get(i));
	    }

		return sbStr.toString();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProductAttributeLookupTableList> getLookupTableList(List<Integer> productIds, Integer productAttributeId) {
		
		String hql = "select vps.product.id as productId, value as attributeValue"
                + " from VwProductSpec as vps "
                + " where 1=1 "
                + (productIds != null && productIds.size() > 0 ?  " and vps.product.id in (:productIds) " : "")
                + (productAttributeId != null ?  " and vps.productAttribute.id = :productAttributeId " : "");
		
		Query query = getSession().createQuery(hql);

		if (productIds != null && productIds.size() > 0 ) {
			query.setParameterList("productIds", productIds);
		}
		if (productAttributeId != null) {
			query.setParameter("productAttributeId", productAttributeId);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(ProductAttributeLookupTableList.class));

		return query.list();
	}
}