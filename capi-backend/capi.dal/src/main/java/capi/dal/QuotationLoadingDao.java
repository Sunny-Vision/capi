package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.QuotationLoading;
import capi.model.masterMaintenance.QuotationLoadingTableList;

@Repository("QuotationLoadingDao")
public class QuotationLoadingDao extends GenericDao<QuotationLoading> {
	
	@SuppressWarnings("unchecked")
	public List<QuotationLoadingTableList> listQuotationLoading(String search,
			int firstRecord, int displayLenght, Order order) {
		
		String hql = "select q.quotationLoadingId as quotationLoadingId, q.quotationPerManDay as quotationPerManDay, "
				+ " (d.code +' - '+ d.chineseName) as district, "
				+ " (o.shortCode +' - '+ o.chineseName) as outletType "
				+ " from QuotationLoading as q "
				+ " left join q.district as d "
				+ " left join q.outletTypes as o ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " where (d.code +' - '+ d.chineseName) like :search "
					+ " or (o.shortCode +' - '+ o.chineseName) like :search "
					+ " or str(q.quotationPerManDay) like :search ";
		}
		
		hql += " order by "+order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);
		query.setResultTransformer(Transformers.aliasToBean(QuotationLoadingTableList.class));
		
		
		return query.list();
		
//		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
//				.setMaxResults(displayLenght).addOrder(order)
//				.createAlias("district", "d", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("outletTypes", "o", JoinType.LEFT_OUTER_JOIN);
//				;
//
//		criteria.setProjection(
//				Projections.projectionList()
//				.add(Projections.property("quotationLoadingId"),"quotationLoadingId")
//				.add(Projections.property("quotationPerManDay"),"quotationPerManDay")
//				.add(Projections.property("d.englishName"),"district")
//				.add(SQLProjectionExt.sqlProjection("({o}.shortCode +' - '+ {o}.englishName) as outletType", new String [] {"outletType"}, new Type[]{StandardBasicTypes.STRING}), "outletType")
//				);
//
//		
//		if (!StringUtils.isEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("d.englishName", "%" + search + "%"),
//					Restrictions.like("o.shortCode", "%" + search + "%"),
//					Restrictions.like("o.englishName", "%" + search + "%"),
//					Restrictions.sqlRestriction("quotationPerManDay LIKE (?)",
//							"%" + search + "%", StandardBasicTypes.STRING)
//					));
//		}
//		
//		criteria.setResultTransformer(Transformers.aliasToBean(QuotationLoadingTableList.class));
//		
//		return criteria.list();
	}

	public long countQuotationLoading() {
		return (long) this.createCriteria()
				.setProjection(Projections.rowCount()).uniqueResult();
	}

	public long countQuotationLoading(String search) {
		String hql = "select count (*) "
				+ " from QuotationLoading as q "
				+ " left join q.district as d "
				+ " left join q.outletTypes as o ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " where (d.code +' - '+ d.chineseName) like :search "
					+ " or (o.shortCode +' - '+ o.chineseName) like :search "
					+ " or str(q.quotationPerManDay) like :search ";
		}
		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		return (long)query.uniqueResult();
		
//		Criteria criteria = this.createCriteria()
//				.createAlias("district", "d", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("outletTypes", "o", JoinType.LEFT_OUTER_JOIN);
//				;
//
//		criteria.add(Restrictions.or(
//				Restrictions.like("d.englishName", "%" + search + "%"),
//				Restrictions.like("o.shortCode", "%" + search + "%"),
//				Restrictions.like("o.englishName", "%" + search + "%"),
//				Restrictions.sqlRestriction("quotationPerManDay LIKE (?)",
//						"%" + search + "%", StandardBasicTypes.STRING)
//				));
//		
//		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	
	public List<QuotationLoading> getQuotationLoadingByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("quotationLoadingId", ids));
		return criteria.list();
		
	}

	public QuotationLoading getQuotationLoadingByDistrictOutlet(int districtId, String outletTypeId){

		Criteria criteria = this.createCriteria("q")
				.createAlias("q.district", "d", JoinType.LEFT_OUTER_JOIN)
				.createAlias("q.outletTypes", "o", JoinType.LEFT_OUTER_JOIN);
				;
				
		criteria.add(Restrictions.eq("d.districtId", districtId))
		.add(Restrictions.eq("o.shortCode", outletTypeId));
		
		return (QuotationLoading)criteria.uniqueResult();
	}
	
	public ScrollableResults getAllQuotationLoadingResult(){
		Criteria criteria = this.createCriteria();
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<QuotationLoading> getNotExistedQuotationLoading(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("quotationLoadingId", ids)));
		return criteria.list();
	}
	
	public List<Integer> getExistingQuotationLoadingId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<QuotationLoading> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("quotationLoadingId", ids));
		return criteria.list();
	}
	
}
