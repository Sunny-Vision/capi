package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.ScSvPlan;
import capi.model.SystemConstant;
import capi.model.qualityControlManagement.ScSvPlanTableList;

@Repository("ScSvPlanDao")
public class ScSvPlanDao extends GenericDao<ScSvPlan> {
	
	@SuppressWarnings("unchecked")
	public List<ScSvPlanTableList> listScSvPlan(String search,
			int firstRecord, int displayLenght, Order order, Integer userId, Integer qcType
			, boolean aboveSupervisor, List<Integer> actedUserIds, Boolean hasAuthorityViewAllRecord) {

		String visitDate = String.format("FORMAT(s.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "Select s.scSvPlanId as scSvPlanId"
				+ " , u.staffCode as staffCode"
				+ " , u.chineseName as chineseName"
				+ " , u.englishName as englishName"
				+ " , u.team as team"
				+ " , sup.staffCode as supervisor"
				+ " , o.staffCode as checker"
				+ " , s.isMandatoryPlan as isMandatoryPlan"
				+ " , case when s.visitDate is null then '' else "+visitDate+" end as visitDate"
				+ " , s.visitDate as visitDate2"
				+ " , case when s.qcType = 1 THEN 'SC' ELSE 'SV' END as qcType"
				+ " , case when count(sc.spotCheckFormId) + count(sv.supervisoryVisitFormId) > 0 "
					+ " then true else false end as deletable"
				+ " from ScSvPlan as s"
				+ " left join s.user as u"
				+ " left join u.supervisor as sup"
				+ " left join s.owner as o"
				+ " left join s.spotCheckForms as sc"
				+ " left join s.supervisoryVisitForms as sv"
				+ " where 1=1";
		
		if (!aboveSupervisor){
			hql += " and o.userId in :actedUserIds";
		}
		
		if (!hasAuthorityViewAllRecord) {
			hql += " and o.rank.rankId <> 8";
		}
		
		if(qcType != null){
			hql += " and s.qcType = :qcType";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ("
					+ " str(s.scSvPlanId) like :search"
					+ " or u.staffCode like :search"
					+ " or u.chineseName like :search"
					+ " or u.englishName like :search"
					+ " or u.team like :search"
					+ " or sup.staffCode like :search"
					+ " or case when s.qcType = 1 THEN 'SC' ELSE 'SV' END like :search"
					+ " or "+visitDate+" like :search )";
		}
		
		hql += " group by s.scSvPlanId, u.staffCode, u.chineseName, u.englishName"
				+ " , u.team, sup.staffCode, o.staffCode, s.qcType, s.visitDate, s.isMandatoryPlan";
		
		if (order != null){
			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if (!aboveSupervisor){
			query.setParameterList("actedUserIds", actedUserIds);
		}
		
		if(qcType != null){
			query.setParameter("qcType", qcType);
		}
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);
		
		query.setResultTransformer(Transformers.aliasToBean(ScSvPlanTableList.class));
		
		return query.list();
	}


	public Integer countScSvPlan(String search, Integer userId, Integer qcType, boolean aboveSupervisor, List<Integer> actedUserIds, Boolean hasAuthorityViewAllRecord) {
		String visitDate = String.format("FORMAT(s.visitDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "Select s.scSvPlanId as scSvPlanId"
				+ " , u.staffCode as staffCode"
				+ " , u.chineseName as chineseName"
				+ " , u.englishName as englishName"
				+ " , u.team as team"
				+ " , sup.staffCode as supervisor"
				+ " , s.isMandatoryPlan as isMandatoryPlan"
				+ " , case when s.visitDate is null then '' else "+visitDate+" end as visitDate"
				+ " , s.visitDate as visitDate2"
				+ " , case when s.qcType = 1 THEN 'SC' ELSE 'SV' END as qcType"
				+ " , case when count(sc.spotCheckFormId) + count(sv.supervisoryVisitFormId) > 0 "
					+ " then 1 else 0 end as deletable"
				+ " from ScSvPlan as s"
				+ " left join [User] as u on s.userId = u.userId"
				+ " left join [User] as sup on u.supervisorId = sup.userId"
				+ " left join [User] as o on s.ownerId = o.userId"
				+ " left join SpotCheckForm as sc on sc.scSvPlanId = s.scSvPlanId"
				+ " left join SupervisoryVisitForm as sv on sv.scSvPlanId = s.scSvPlanId"
				+ " where 1=1";
		
		if (!aboveSupervisor){
			sql += " and o.userId in :actedUserIds";
		}
		
		if(!hasAuthorityViewAllRecord){
			sql += " and o.rankId <> 8";
		}
		
		if(qcType != null){
			sql += " and s.qcType = :qcType";
		}
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and ("
					+ " str(s.scSvPlanId) like :search"
					+ " or u.staffCode like :search"
					+ " or u.chineseName like :search"
					+ " or u.englishName like :search"
					+ " or u.team like :search"
					+ " or sup.staffCode like :search"
					+ " or case when s.qcType = 1 THEN 'SC' ELSE 'SV' END like :search"
					+ " or "+visitDate+" like :search) ";
		}
		
		sql += " group by s.scSvPlanId, u.staffCode, u.chineseName, u.englishName"
				+ " , u.team, sup.staffCode, s.qcType, s.visitDate, s.isMandatoryPlan";
		
		String countSQL = "Select count(*) from ("+sql+") as a";
		
		SQLQuery query = this.getSession().createSQLQuery(countSQL);
		
		if (!aboveSupervisor){
			query.setParameterList("actedUserIds", actedUserIds);
		}
		
		if(qcType != null){
			query.setParameter("qcType", qcType);
		}
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	
	public List<ScSvPlan> getScSvPlanByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("scSvPlanId", ids));
		return criteria.list();
	}

	public List<ScSvPlan> getScSvPlanByDate(Date date, boolean fetchDependency){
		Criteria criteria = this.createCriteria();
		if (fetchDependency){
			criteria.setFetchMode("user", FetchMode.JOIN);
			criteria.setFetchMode("owner", FetchMode.JOIN);
		}
		criteria.add(Restrictions.eq("visitDate", date));
		return criteria.list();
	}
	
}
