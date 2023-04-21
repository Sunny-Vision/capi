package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.Rank;
import capi.model.userAccountManagement.RankTableList;

@Repository("RankDao")
public class RankDao  extends GenericDao<Rank>{

	@SuppressWarnings("unchecked")
	public List<RankTableList> listRank(String search,
			int firstRecord, int displayLenght, Order order) {

		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLenght).addOrder(order);
		
		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("rankId"), "rankId");
		projList.add(Projections.property("code"), "code");
		projList.add(Projections.property("name"), "name");

		
		criteria.setProjection(projList);
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("name", "%" + search + "%")));
		}
	
		criteria.setResultTransformer(Transformers.aliasToBean(RankTableList.class));
		
		return criteria.list();
	}
	
	public long countRank() {
		return this.countRank("");
	}

	public long countRank(String search) {
		Criteria criteria = this.createCriteria();

		if (!StringUtils.isEmpty(search)){
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("name", "%" + search + "%")));
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public Rank getRankByCode(String code){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("code", code));
		return (Rank)criteria.uniqueResult();
	}
	
	public List<Rank> getRankByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("rankId", ids));
		return criteria.list();
		
	}

	public List<Rank> searchRank(String search, int firstRecord, int displayLength) {
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("code")).addOrder(Order.asc("name"));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("{alias}.code+' - '+name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return criteria.list();
	}

	public long countSearchRank(String search) {
		Criteria criteria = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("{alias}.code+' - '+name LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
}
