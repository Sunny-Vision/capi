package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.SubPriceFieldMapping;
import capi.model.api.dataSync.SubPriceFieldMappingSyncData;
import capi.model.masterMaintenance.SubPriceFieldList;

@Repository("SubPriceFieldMappingDao")
public class SubPriceFieldMappingDao extends GenericDao<SubPriceFieldMapping> {

	public SubPriceFieldMapping getByFieldIdTypeId(int fieldId, int typeId){
		Criteria criteria = this.createCriteria("mapping").createAlias("mapping.subPriceField", "field", JoinType.INNER_JOIN).createAlias("mapping.subPriceType", "type", JoinType.INNER_JOIN);
		criteria.add(Restrictions.and(Restrictions.eq("field.subPriceFieldId", fieldId), Restrictions.eq("type.subPriceTypeId", typeId)));
		return (SubPriceFieldMapping) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<SubPriceFieldList> getFieldByTypeId(int typeId){
		Criteria criteria = this.createCriteria("mapping")
				.createAlias("mapping.subPriceField", "field", JoinType.INNER_JOIN)
				.createAlias("mapping.subPriceType", "type", JoinType.INNER_JOIN);
		ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("field.subPriceFieldId"), "subPriceFieldId");
        projList.add(Projections.groupProperty("field.fieldName"), "fieldName");
        projList.add(Projections.groupProperty("field.fieldType"), "fieldType");
        projList.add(Projections.groupProperty("field.variableName"), "variableName");
        projList.add(Projections.groupProperty("mapping.sequence"), "sequence");
        criteria.setProjection(projList);
        criteria.add(Restrictions.eq("type.subPriceTypeId", typeId)).addOrder(Order.asc("mapping.sequence"));
        
        criteria.setResultTransformer(Transformers.aliasToBean(SubPriceFieldList.class));
        return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<SubPriceFieldMapping> getMapByTypeId(int typeId, boolean fetchField){
		Criteria criteria = this.createCriteria("mapping");
		if (fetchField){
			criteria.setFetchMode("subPriceField", FetchMode.JOIN);
		}
		criteria.createAlias("mapping.subPriceType", "type", JoinType.INNER_JOIN);
        criteria.add(Restrictions.eq("type.subPriceTypeId", typeId)).addOrder(Order.asc("mapping.sequence"));
        return criteria.list();
	} 
	
	
	
	public List<SubPriceFieldMapping> getSubPriceFieldMappingBySubPriceTypeIds(List<Integer> ids){
		Criteria criteria = this.createCriteria("m").createAlias("m.subPriceType", "f");
		criteria.add(Restrictions.in("f.subPriceTypeId", ids));
		return criteria.list();
	}


	public List<SubPriceFieldMapping> getByFieldIdsTypeId(List<Integer> ids, Integer typeId, boolean fetchField){
		Criteria criteria = this.createCriteria("m");
		if (fetchField){
			criteria.setFetchMode("subPriceField", FetchMode.JOIN);
		}
		criteria.createAlias("m.subPriceType", "t")
				.createAlias("m.subPriceField", "f");
		criteria.add(Restrictions.and(
				Restrictions.in("f.subPriceFieldId", ids),
				Restrictions.eq("t.subPriceTypeId", typeId)
		));
		return criteria.list();
	}
	
	public List<SubPriceFieldMappingSyncData> getUpdateSubPriceFieldMapping(Date lastSyncTime){
		Criteria criteria = this.createCriteria("m")
				.createAlias("m.subPriceType", "t")
				.createAlias("m.subPriceField", "f");
		criteria.add(Restrictions.ge("m.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("m.subPriceFieldMappingId"), "subPriceFieldMappingId");
		projList.add(Projections.property("f.subPriceFieldId"), "subPriceFieldId");
		projList.add(Projections.property("t.subPriceTypeId"), "subPriceTypeId");
		projList.add(Projections.property("m.sequence"), "sequence");
		projList.add(Projections.property("m.createdDate"), "createdDate");
		projList.add(Projections.property("m.modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(SubPriceFieldMappingSyncData.class));
		return criteria.list();
	}
	
	public List<SubPriceFieldMapping> getSubPriceFieldMappingByTypeId(int subPriceTypeId){
		Criteria criteria = this.createCriteria("sfm")
				.add(Restrictions.eq("sfm.subPriceType.subPriceTypeId", subPriceTypeId))
				.setFetchMode("subPriceField", FetchMode.JOIN);
		criteria.addOrder(Order.asc("sequence"));
		return criteria.list();
	}
}
