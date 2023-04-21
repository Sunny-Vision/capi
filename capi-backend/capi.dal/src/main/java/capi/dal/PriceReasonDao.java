package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.PriceReason;
import capi.model.SystemConstant;
import capi.model.api.dataSync.OutletTypePriceReasonSyncData;
import capi.model.api.dataSync.PriceReasonSyncData;
import capi.model.masterMaintenance.PriceReasonTableList;

@Repository("PriceReasonDao")
public class PriceReasonDao  extends GenericDao<PriceReason>{
	@SuppressWarnings("unchecked")
	public List<PriceReasonTableList> selectAllPriceReason(String search, int firstRecord, int displayLength, Order order, String[] outletTypeId, String reasonType) {

		String hql = " select p.priceReasonId as priceReasonId, p.description as description, p.reasonType as reasonType, p.sequence as sequence, "
				+ " case "
				+ "	when p.reasonType is null then '' "
				+ " when p.reasonType = 1 then :reasonType1 "
				+ " else :reasonType2 "
				+ " end  as reasonTypeLabel, "
				+ " p.isAllOutletType as isAllOutletType, "
				+ " count(o.shortCode) as shortCode"
				+ " from PriceReason as p "
				+ " left join p.outletTypes o "
				+ " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and ( str(p.priceReasonId) like :search or p.description like :search ) ";
		}
		
		if (outletTypeId != null && outletTypeId.length > 0) {
        	hql += " and ( p.isAllOutletType = 1 "
        		+ " or ( "
        		+ " p.isAllOutletType = 0 and o.shortCode in ( :outletTypeIds ))) ";
        }
		
		if (reasonType != null && reasonType.length() > 0) {
        	hql += " and ( "
        		+ " case "
        		+ " when p.reasonType is null then '' "
				+ " when p.reasonType = 1 then :reasonType1 "
				+ " else :reasonType2 "
				+ " end ) like :reasonType ";
        }
		
		hql += " group by p.priceReasonId, p.description, p.reasonType, p.sequence, p.isAllOutletType ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setParameter("reasonType1", SystemConstant.REASON_TYPE_1);
		query.setParameter("reasonType2", SystemConstant.REASON_TYPE_2);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeIds", outletTypeId);
        }
		
		if (reasonType != null && reasonType.length() > 0) {
			query.setParameter("reasonType", reasonType);
		}

		query.setResultTransformer(Transformers.aliasToBean(PriceReasonTableList.class));

		return query.list();

	}

	public long countSelectAllPriceReason(String search, String[] outletTypeId, String reasonType) {

		String hql = " select count(distinct p.priceReasonId) "
				+ " from PriceReason as p "
				+ " left join p.outletTypes o "
				+ " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " and ( str(p.priceReasonId) like :search or p.description like :search ) ";
		}
		
		if (outletTypeId != null && outletTypeId.length > 0) {
        	hql += " and ( p.isAllOutletType = 1 "
        		+ " or ( "
        		+ " p.isAllOutletType = 0 and o.shortCode in ( :outletTypeIds ))) ";
        }
		
		if (reasonType != null && reasonType.length() > 0) {
        	hql += " and ( "
        		+ " case "
        		+ " when p.reasonType is null then '' "
				+ " when p.reasonType = 1 then :reasonType1 "
				+ " else :reasonType2 "
				+ " end ) like :reasonType ";
        }
		
		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeIds", outletTypeId);
        }
		
		if (reasonType != null && reasonType.length() > 0) {
			query.setParameter("reasonType1", SystemConstant.REASON_TYPE_1);
			query.setParameter("reasonType2", SystemConstant.REASON_TYPE_2);
			query.setParameter("reasonType", reasonType);
		}

		return (long)query.uniqueResult();	
	}

	public List<PriceReason> getPriceReasonsByIds(List<Integer> ids, boolean fetchOutletTypes){
		Criteria criteria = this.createCriteria();
		if (fetchOutletTypes){
			criteria.setFetchMode("outletTypes", FetchMode.JOIN);
		}
		criteria.add(Restrictions.in("priceReasonId", ids));
		return criteria.list();
	}
	
	public ScrollableResults getAllPriceReasonResult(){
		Criteria criteria = this.createCriteria();
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<PriceReason> getNotExistedPriceReason(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("priceReasonId", ids)));
		return criteria.list();		
	}

	public List<Integer> getExistingPriceReasonId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<PriceReason> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("priceReasonId", ids));
		return criteria.list();
	}
	
	public List<PriceReasonSyncData> getUpdatePriceReasion(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("priceReasonId"), "priceReasonId");
		projList.add(Projections.property("sequence"), "sequence");
		projList.add(Projections.property("isAllOutletType"), "isAllOutletType");
		projList.add(Projections.property("description"), "description");
		projList.add(Projections.property("reasonType"), "reasonType");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(PriceReasonSyncData.class));
		return criteria.list();
	}
	
	public List<OutletTypePriceReasonSyncData> getUpdateOutletTypePriceReason(Date lastSyncTime){

		String sql = "Select outlet.priceReasonId as priceReasonId, "
				+ "outlet.shortCode as shortCode, "
				+ "outlet.createdDate as createdDate, "
				+ "outlet.modifiedDate as modifiedDate "
				+ "from OutletTypePriceReason outlet where "
				+ "outlet.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("priceReasonId", StandardBasicTypes.INTEGER)
				.addScalar("shortCode", StandardBasicTypes.STRING)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(OutletTypePriceReasonSyncData.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<PriceReason> getAllByType(String[] outletTypeId, String reasonType) {

		String hql = " select p.priceReasonId as priceReasonId, p.description as description, p.reasonType as reasonType, p.sequence as sequence, "
				+ " p.isAllOutletType as isAllOutletType "
				+ " from PriceReason as p "
				+ " left join p.outletTypes o "
				+ " where 1=1 ";
		
		if (outletTypeId != null && outletTypeId.length > 0) {
        	hql += " and ( p.isAllOutletType = 1 "
        		+ " or ( "
        		+ " p.isAllOutletType = 0 and o.shortCode in ( :outletTypeIds ))) ";
        } else {
        	hql += " and p.isAllOutletType = 1 ";
        }
		
		if (reasonType != null && reasonType.length() > 0) {
        	hql += " and ( "
        		+ " case "
        		+ " when p.reasonType is null then '' "
				+ " when p.reasonType = 1 then :reasonType1 "
				+ " else :reasonType2 "
				+ " end ) like :reasonType ";
        }
		
		hql += " group by p.priceReasonId, p.description, p.reasonType, p.sequence, p.isAllOutletType ";
		
		hql += " order by sequence asc";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("reasonType1", SystemConstant.REASON_TYPE_1);
		query.setParameter("reasonType2", SystemConstant.REASON_TYPE_2);
		
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeIds", outletTypeId);
        }
		
		if (reasonType != null && reasonType.length() > 0) {
			query.setParameter("reasonType", reasonType);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(PriceReason.class));

		return query.list();
	}
}
