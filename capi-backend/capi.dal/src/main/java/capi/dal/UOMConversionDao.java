package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.UOMConversion;
import capi.model.api.dataSync.UOMConversionSyncData;
import capi.model.masterMaintenance.UOMConversionTableList;

@Repository("UOMConversionDao")
public class UOMConversionDao extends GenericDao<UOMConversion> {
	
	@SuppressWarnings("unchecked")
	public List<UOMConversionTableList> listUOMConversion(String search,
			int firstRecord, int displayLenght, Order order) {

		String hql = "SELECT uc.uomConversionId as uomConversionId ,"
				+ "(str(uc.baseUOM.uomId) + ' - '+ uc.baseUOM.chineseName) as baseUOM,"
				+ "(str(uc.targetUOM.uomId) + ' - '+ uc.targetUOM.chineseName) as targetUOM,"
				+ "uc.factor as factor FROM UOMConversion as uc "
				+ "where 1=1 ";
		if (!StringUtils.isEmpty(search)){
			hql +=  " and ((str(uc.baseUOM.uomId) + ' - '+ uc.baseUOM.chineseName) like :search "
					+ "or (str(uc.targetUOM.uomId) + ' - '+ uc.targetUOM.chineseName) like :search "
					+ "or str(uc.factor) like :search) ";
		}
				
		hql += "order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
								 
				
		Query query = getSession().createQuery(hql);
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);
		
		query.setResultTransformer(Transformers.aliasToBean(UOMConversionTableList.class));
				

		return query.list();
	}

	public long countUOMConversion() {
		return (long) this.createCriteria()
				.setProjection(Projections.rowCount()).uniqueResult();
	}

	public long countUOMConversion(String search) {
		String from = "select count(*) from UOMConversion as uc ";
		String where = "where 1=1 ";

		if (!StringUtils.isEmpty(search)) {
			where += "and ((str(uc.baseUOM.uomId) + ' - '+ uc.baseUOM.chineseName) like :search "
					+ "or (str(uc.targetUOM.uomId) + ' - '+ uc.targetUOM.chineseName) like :search "
					+ "or str(uc.factor) like :search)";
		}

		Query query = getSession().createQuery(from + where);

		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", "%" + search + "%");
		}

		return (long) query.uniqueResult();
	}
	
	
	public List<UOMConversion> getUOMConversionByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("uomConversionId", ids));
		return criteria.list();
		
	}

	public ScrollableResults getAllUOMConversionResult(){
		Criteria criteria = this.createCriteria();
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<UOMConversion> getNotExistedUOMConversion(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("uomConversionId", ids)));
		return criteria.list();		
	}
	
	public List<Integer> getExistingUOMConversionId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<UOMConversion> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("uomConversionId", ids));
		return criteria.list();
	}
	
	public List<UOMConversionSyncData> getUpdateUOMConversion(Date lastSyncTime){
		Criteria criteria = this.createCriteria("c")
				.createAlias("c.baseUOM", "b")
				.createAlias("c.targetUOM", "t");
		criteria.add(Restrictions.ge("c.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("c.uomConversionId"), "uomConversionId");
		projList.add(Projections.property("b.uomId"), "baseUOMId");
		projList.add(Projections.property("t.uomId"), "targetUOMId");
		projList.add(Projections.property("c.factor"), "factor");
		projList.add(Projections.property("c.createdDate"), "createdDate");
		projList.add(Projections.property("c.modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(UOMConversionSyncData.class));
		return criteria.list();
	}
	
	public UOMConversion getUOMConversionByBaseAndTarget(int baseId, int targetId) {
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("baseUOM.id", baseId));
		criteria.add(Restrictions.eq("targetUOM.id", targetId));
		return (UOMConversion)criteria.uniqueResult();
	}
}
