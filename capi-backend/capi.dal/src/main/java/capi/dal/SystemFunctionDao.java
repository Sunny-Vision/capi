package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.SystemFunction;
import capi.model.commonLookup.SystemFunctionLookupTableList;

@Repository("SystemFunctionDao")
public class SystemFunctionDao  extends GenericDao<SystemFunction>{

	public SystemFunction getFunctionByCode(String code){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("code", code));
		return (SystemFunction)criteria.uniqueResult();
	}
	
	public List<SystemFunction> getDeleteRoleFunctionsByIds(Integer roleId, Integer[] ids) {

		Criteria criteria = this.createCriteria("sf")
				.createAlias("sf.roles", "rf", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.and(
				Restrictions.eq("rf.roleId", roleId),
				Restrictions.in("sf.systemFunctionId", ids)
			));

		return criteria.list();
	}

	public List<SystemFunction> getNewRoleFunctionsByIds(Integer[] ids) {

		Criteria criteria = this.createCriteria("sf")
				.createAlias("sf.roles", "rf", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.in("sf.systemFunctionId", ids));

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SystemFunctionLookupTableList> getLookupTableList(String search, int firstRecord, int displayLength, Order order, Boolean isMobile) {

		Criteria criteria = this.createCriteria();

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("systemFunctionId"), "id");
		projList.add(Projections.property("code"), "code");
		projList.add(Projections.property("description"), "description");

		criteria.setProjection(projList);

		criteria.add(Restrictions.eq("isMobile", isMobile));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("description", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(SystemFunctionLookupTableList.class));

		return (List<SystemFunctionLookupTableList>)criteria.list();
	}

	public long countLookupTableList(String search, Boolean isMobile) {

		Criteria criteria = this.createCriteria();

		criteria.add(Restrictions.eq("isMobile", isMobile));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("description", "%" + search + "%")
			));
		}

		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLookupTableSelectAll(String search, Boolean isMobile) {

		Criteria criteria = this.createCriteria();

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("systemFunctionId"), "id");

		criteria.setProjection(projList);

		criteria.add(Restrictions.eq("isMobile", isMobile));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("description", "%" + search + "%")
			));
		}
		
		return (List<Integer>)criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SystemFunctionLookupTableList> getTableListByIds(Boolean isMobile, Integer[] ids) {

		Criteria criteria = this.createCriteria();

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("systemFunctionId"), "id");
		projList.add(Projections.property("code"), "code");
		projList.add(Projections.property("description"), "description");

		criteria.setProjection(projList);

		criteria.add(Restrictions.eq("isMobile", isMobile));

		if (ids != null && ids.length > 0) {
        	criteria.add(Restrictions.in("systemFunctionId", ids));
        }

		criteria.setResultTransformer(Transformers.aliasToBean(SystemFunctionLookupTableList.class));

		return (List<SystemFunctionLookupTableList>)criteria.list();
	}

}
