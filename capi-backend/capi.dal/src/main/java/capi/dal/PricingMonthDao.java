package capi.dal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.PricingFrequency;
import capi.model.api.dataSync.PricingFrequencySyncData;
import capi.model.masterMaintenance.PricingMonthTableList;


@Repository("PricingMonthDao")
public class PricingMonthDao  extends GenericDao<PricingFrequency>{

	@SuppressWarnings("unchecked")
	public List<PricingMonthTableList> selectAllPricingMonth(String search, int firstRecord, int displayLength, Order order) {
		
		String hql = "select p.pricingFrequencyId as pricingFrequencyId, p.name as name, " +
						"p.isJan as isJan, p.isFeb as isFeb, p.isMar as isMar, p.isApr as isApr, p.isMay as isMay, p.isJun as isJun, " +
						"p.isJul as isJul, p.isAug as isAug, p.isSep as isSep, p.isOct as isOct, p.isNov as isNov, p.isDec as isDec " +
						"FROM PricingFrequency p where 1=1 ";

		if (!StringUtils.isEmpty(search)) {
			hql += "and ( str(p.pricingFrequencyId) like :search or p.name like :search ) ";
		}
		
		if("name".equals(order.getPropertyName()) || "pricingFrequencyId".equals(order.getPropertyName())) {
			hql += "ORDER BY " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		} else {
			if(order.isAscending()) {
				hql += "ORDER BY p.isJan desc, p.isFeb desc, p.isMar desc, p.isApr desc, " +
						"p.isMay desc, p.isJun desc, p.isJul desc, p.isAug desc, " +
						"p.isSep desc, p.isOct desc, p.isNov desc, p.isDec desc ";
			} else {
				hql += "ORDER BY p.isJan asc, p.isFeb asc, p.isMar asc, p.isApr asc, " +
						"p.isMay asc, p.isJun asc, p.isJul asc, p.isAug asc, " +
						"p.isSep asc, p.isOct asc, p.isNov asc, p.isDec asc ";
			}
		}
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.setResultTransformer(Transformers.aliasToBean(PricingMonthTableList.class));
		
		return query.list();
	}

	public long countSelectAllPricingMonth(String search) {

		String hql = "select count(*) as cnt " +
				"FROM PricingFrequency p where 1=1 ";

		if (!StringUtils.isEmpty(search)) {
			hql += "and p.name like :search ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		return (long)query.uniqueResult();
	}

	public List<PricingFrequency> getPricingMonthsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("pricingFrequencyId", ids));
		
		return criteria.list();
	}
	
	public PricingFrequency getPricingMonthByDescription(String description) {
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("name", description));
		
		return (PricingFrequency)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<PricingFrequency> search(String search, int firstRecord, int displayLength) {
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("name"));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return criteria.list();
	}

	public long countSearch(String search) {
		Criteria criteria = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public PricingFrequency checkDuplicatePricingMonth(Map<String, Boolean> map){
		String[] months = new String[] {"isJan", "isFeb", "isMar", "isApr", "isMay", "isJun",
										"isJul", "isAug", "isSep", "isOct", "isNov", "isDec"};

		Criteria criteria = this.createCriteria();
		for(int i = 0; i < months.length; i++) {
			criteria.add(Restrictions.eq(months[i], map.get(months[i])));
		}

		return (PricingFrequency)criteria.uniqueResult();
	}

	public List<PricingFrequencySyncData> getUpdatePricingFrequency(Date lastSyncTime){
	Criteria criteria = this.createCriteria("pf");
	criteria.add(Restrictions.ge("pf.modifiedDate", lastSyncTime));
	ProjectionList projList = Projections.projectionList();
	projList.add(Projections.property("pf.pricingFrequencyId"), "pricingFrequencyId");
	projList.add(Projections.property("pf.name"), "name");
	projList.add(Projections.property("pf.isJan"), "isJan");
	projList.add(Projections.property("pf.isFeb"), "isFeb");
	projList.add(Projections.property("pf.isMar"), "isMar");
	projList.add(Projections.property("pf.isApr"), "isApr");
	projList.add(Projections.property("pf.isMay"), "isMay");
	projList.add(Projections.property("pf.isJun"), "isJun");
	projList.add(Projections.property("pf.isJul"), "isJul");
	projList.add(Projections.property("pf.isAug"), "isAug");
	projList.add(Projections.property("pf.isSep"), "isSep");
	projList.add(Projections.property("pf.isOct"), "isOct");
	projList.add(Projections.property("pf.isNov"), "isNov");
	projList.add(Projections.property("pf.isDec"), "isDec");
	projList.add(Projections.property("pf.createdDate"), "createdDate");
	projList.add(Projections.property("pf.modifiedDate"), "modifiedDate");
	
	criteria.setProjection(projList);
	criteria.setResultTransformer(Transformers.aliasToBean(PricingFrequencySyncData.class));
	return criteria.list();
}
}
