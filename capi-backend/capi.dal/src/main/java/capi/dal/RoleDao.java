package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.Role;
import capi.model.api.dataSync.RoleSyncData;
import capi.model.commonLookup.RoleLookupTableList;
import capi.model.userAccountManagement.RoleTableList;

@Repository("RoleDao")
public class RoleDao  extends GenericDao<Role>{
	
	public List<Role> getUserRoleWithActing(Integer userId){
		
		String hql = "select distinct r from Role as r "
				+ " left join fetch r.functions as f "
				+ " left join r.users as u "
				+ " left join r.actings as a "
				+ " 	on convert(date,getDate()) between a.startDate and a.endDate "
				+ " left join a.replacement as ad "
				+ " where u.userId = :user or ad.userId = :user";
				
		Query query = this.getSession().createQuery(hql);
		query.setParameter("user", userId);
		
		return query.list();		
	}

	public List<Role> searchRole(String search, int firstRecord, int displayLength) {
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("roleId")).addOrder(Order.asc("name"));
		return criteria.list();
	}

	public long countSearchRole(String search) {
		Criteria criteria = this.createCriteria();
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<RoleTableList> selectAllRole(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("r");

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("r.roleId"), "roleId");
		projList.add(Projections.property("r.name"), "roleName");
		projList.add(Projections.property("r.description"), "roleDescription");

		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("r.name", "%" + search + "%"),
					Restrictions.like("r.description", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(RoleTableList.class));

		return criteria.list();
	}

	public long countSelectAllRole(String search) {

		Criteria criteria = this.createCriteria("r");

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("r.name", "%" + search + "%"),
					Restrictions.like("r.description", "%" + search + "%")
			));
		}

		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<RoleTableList> selectAllBackendFunctionList(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("r")
								.createAlias("r.functions", "sf", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("sf.systemFunctionId"), "systemFunctionId");
		projList.add(Projections.property("sf.code"), "functionCode");
		projList.add(Projections.property("sf.description"), "functionDescription");

		criteria.setProjection(Projections.distinct(projList));

		criteria.add(Restrictions.eq("sf.isMobile", false));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("sf.code", "%" + search + "%"),
					Restrictions.like("sf.description", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(RoleTableList.class));

		return criteria.list();
	}

	public long countSelectAllBackendFunctionList(String search) {

		Criteria criteria = this.createCriteria("r")
								.createAlias("r.functions", "sf", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("sf.systemFunctionId")
		));
		criteria.setProjection(Projections.distinct(projList));

		criteria.add(Restrictions.eq("sf.isMobile", false));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("sf.code", "%" + search + "%"),
					Restrictions.like("sf.description", "%" + search + "%")
			));
		}

		return (long) criteria.uniqueResult();
	}

	public List<RoleTableList> selectAllFrontendFunctionList(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("r")
								.createAlias("r.functions", "sf", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("sf.systemFunctionId"), "systemFunctionId");
		projList.add(Projections.property("sf.code"), "functionCode");
		projList.add(Projections.property("sf.description"), "functionDescription");

		criteria.setProjection(Projections.distinct(projList));

		criteria.add(Restrictions.eq("sf.isMobile", true));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("sf.code", "%" + search + "%"),
					Restrictions.like("sf.description", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(RoleTableList.class));

		return criteria.list();
	}

	public long countSelectAllFrontendFunctionList(String search) {

		Criteria criteria = this.createCriteria("r")
								.createAlias("r.functions", "sf", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("sf.systemFunctionId")
		));
		criteria.setProjection(Projections.distinct(projList));

		criteria.add(Restrictions.eq("sf.isMobile", true));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("sf.code", "%" + search + "%"),
					Restrictions.like("sf.description", "%" + search + "%")
			));
		}

		return (long) criteria.uniqueResult();
	}

	public List<Integer> selectBackendFunctionWithRoleId(Integer roleId) {

		Criteria criteria = this.createCriteria("r")
								.createAlias("r.functions", "sf", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("sf.systemFunctionId"), "systemFunctionId");

		criteria.setProjection(projList);

		criteria.add(Restrictions.and(
				Restrictions.eq("r.roleId", roleId),
				Restrictions.eq("sf.isMobile", false)
			));

		return criteria.list();
	}

	public List<Integer> selectFrontendFunctionWithRoleId(Integer roleId) {

		Criteria criteria = this.createCriteria("r")
								.createAlias("r.functions", "sf", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("sf.systemFunctionId"), "systemFunctionId");

		criteria.setProjection(projList);

		criteria.add(Restrictions.and(
				Restrictions.eq("r.roleId", roleId),
				Restrictions.eq("sf.isMobile", true)
			));

		return criteria.list();
	}

	public Role getRoleByCode(String roleName){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("name", roleName));
		return (Role)criteria.uniqueResult();
	}

	public List<Role> getRolesByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("roleId", ids));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<RoleLookupTableList> getLookupTableList(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("r");

		ProjectionList projList = Projections.projectionList();

		projList.add(Projections.property("r.roleId"), "id");
		projList.add(Projections.property("r.name"), "name");
		projList.add(Projections.property("r.description"), "description");

		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("r.name", "%" + search + "%"),
					Restrictions.like("r.description", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(RoleLookupTableList.class));

		return (List<RoleLookupTableList>)criteria.list();
	}

	public long countLookupTableList(String search) {

		Criteria criteria = this.createCriteria("r");

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("r.name", "%" + search + "%"),
					Restrictions.like("r.description", "%" + search + "%")
			));
		}

		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLookupTableSelectAll(String search) {

		Criteria criteria = this.createCriteria("r");
				
		ProjectionList projList = Projections.projectionList();
		
		projList.add(Projections.property("r.roleId"), "roleId");
		
		criteria.setProjection(projList);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("r.name", "%" + search + "%"),
					Restrictions.like("r.description", "%" + search + "%")
			));
		}
		
		return (List<Integer>)criteria.list();
	}

	public List<Role> getByIds(Integer[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("roleId", ids)).list();
	}
	
	public List<RoleSyncData> getUpdateRole(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("roleId"), "roleId");
		projList.add(Projections.property("name"), "name");
		projList.add(Projections.property("description"), "description");
		projList.add(Projections.property("authorityLevel"), "authorityLevel");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(RoleSyncData.class));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRolesByUser(Integer userId) {
		return this.createCriteria("r")
				.createAlias("r.users", "u")
				.add(Restrictions.eq("u.id", userId))
				.list();
	}
}
