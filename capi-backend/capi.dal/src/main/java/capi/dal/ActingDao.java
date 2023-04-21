package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.Acting;
import capi.model.SystemConstant;
import capi.model.userAccountManagement.ActingModel;
import capi.model.userAccountManagement.ActingTableList;

@Repository("ActingDao")
public class ActingDao  extends GenericDao<Acting>{
	@SuppressWarnings("unchecked")
	public List<ActingTableList> searchActingList(String search, int firstRecord, int displayLength, Order order) {
		
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select a.actingId as actingId "
				+ ", (s.staffCode + ' - ' + s.chineseName) as staff "
				+ ", (r.staffCode + ' - ' + r.chineseName) as replacement "
				+ ", role.name as roleName "
				+ ", case when a.startDate is null then '' else "+startDate+" end as startDate"
				+ ", case when a.endDate is null then '' else "+endDate+" end as endDate "
				+ " from Acting as a "
				+ " left join a.staff as s "
				+ " left join a.replacement as r "
				+ " left join a.role as role ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " where (s.staffCode + ' - ' + s.chineseName) like :search or "
					+ " (r.staffCode + ' - ' + r.chineseName) like :search or "
					+ " role.name like :search or "
					+ " "+startDate+" like :search or "
					+ " "+endDate+" like :search ";
		}
		hql += " order by "+order.getPropertyName() + (order.isAscending()? " asc" : " desc" );
		
		Query query = this.getSession().createQuery(hql);
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", "%"+search+"%");
		}
		
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		query.setResultTransformer(Transformers.aliasToBean(ActingTableList.class));
		return query.list();

//		Criteria criteria = this.createCriteria("a")
//								.createAlias("a.staff", "s", JoinType.LEFT_OUTER_JOIN)
//								.createAlias("a.replacement", "r", JoinType.LEFT_OUTER_JOIN)
//								.createAlias("a.role", "role", JoinType.LEFT_OUTER_JOIN);
//
//		String staff = "{s}.staffCode + ' - ' + {s}.chineseName";
//		String replacement = "{r}.staffCode + ' - ' + {r}.chineseName";
//		String startDate = String.format("FORMAT({alias}.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		String endDate = String.format("FORMAT({alias}.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//
//		ProjectionList projList = Projections.projectionList();
//		
//		projList.add(Projections.property("a.actingId"), "actingId");
//		
//		projList.add(Projections.property("s.userId"), "staffId");
//		projList.add(Projections.property("s.staffCode"), "staffCode");
//		projList.add(Projections.property("s.chineseName"), "staffChineseName");
//		projList.add(Projections.property("s.englishName"), "staffEnglishName");
//		projList.add(SQLProjectionExt.sqlProjection(staff + " as staff", new String [] {"staff"}, new Type[]{StandardBasicTypes.STRING}), "staff");
//		
//		projList.add(Projections.property("r.userId"), "replacementId");
//		projList.add(Projections.property("r.staffCode"), "replacementCode");
//		projList.add(Projections.property("r.chineseName"), "replacementChineseName");
//		projList.add(Projections.property("r.englishName"), "replacementEnglishName");
//		projList.add(SQLProjectionExt.sqlProjection(replacement + " as replacement", new String [] {"replacement"}, new Type[]{StandardBasicTypes.STRING}), "replacement");
//		
//		projList.add(Projections.property("role.roleId"), "roleId");
//		projList.add(Projections.property("role.name"), "roleName");
//		projList.add(Projections.sqlProjection(startDate + " as startDate", new String [] {"startDate"}, new Type[]{StandardBasicTypes.STRING}), "startDate");
//		projList.add(Projections.sqlProjection(endDate + " as endDate", new String [] {"endDate"}, new Type[]{StandardBasicTypes.STRING}), "endDate");
//		
//		criteria.setProjection(projList);
//
//		if (!StringUtils.isEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("s.staffCode", "%" + search + "%"),
//					Restrictions.like("s.chineseName", "%" + search + "%"),
//					Restrictions.like("r.staffCode", "%" + search + "%"),
//					Restrictions.like("r.chineseName", "%" + search + "%"),
//					Restrictions.like("role.name", "%" + search + "%"),
//					Restrictions.sqlRestriction(
//							startDate + " LIKE (?)",
//							"%" + search + "%", StandardBasicTypes.STRING)
//							,
//					Restrictions.sqlRestriction(
//							endDate + " LIKE (?)",
//							"%" + search + "%", StandardBasicTypes.STRING)
//			));
//		}
//
//		criteria.setFirstResult(firstRecord);
//		criteria.setMaxResults(displayLength);
//        criteria.addOrder(order);
//
//		criteria.setResultTransformer(Transformers.aliasToBean(ActingTableList.class));
//
//		return criteria.list();
	}

	public long countActingList(String search) {		

		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select count (*) "
				+ " from Acting as a "
				+ " left join a.staff as s "
				+ " left join a.replacement as r "
				+ " left join a.role as role ";
		
		if (!StringUtils.isEmpty(search)){
			hql += " where (s.staffCode + ' - ' + s.chineseName) like :search or "
					+ " (r.staffCode + ' - ' + r.chineseName) like :search or "
					+ " role.name like :search or "
					+ " "+startDate+" like :search or "
					+ " "+endDate+" like :search ";
		}
		Query query = this.getSession().createQuery(hql);
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", "%"+search+"%");
		}
		return (long)query.uniqueResult();

//		Criteria criteria = this.createCriteria("a")
//								.createAlias("a.staff", "s", JoinType.LEFT_OUTER_JOIN)
//								.createAlias("a.replacement", "r", JoinType.LEFT_OUTER_JOIN)
//								.createAlias("a.role", "role", JoinType.LEFT_OUTER_JOIN);
//
//		String staff = "{s}.staffCode + ' - ' + {s}.chineseName";
//		String replacement = "{r}.staffCode + ' - ' + {r}.chineseName";
//		String startDate = String.format("FORMAT({alias}.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		String endDate = String.format("FORMAT({alias}.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		
//		if (!StringUtils.isEmpty(search)) {
//			criteria.add(Restrictions.or(
//				Restrictions.like("s.staffCode", "%" + search + "%"),
//				Restrictions.like("s.chineseName", "%" + search + "%"),
//				Restrictions.like("r.staffCode", "%" + search + "%"),
//				Restrictions.like("r.chineseName", "%" + search + "%"),
//				Restrictions.like("role.name", "%" + search + "%"),
//				Restrictions.sqlRestriction(
//						startDate + " LIKE (?)",
//						"%" + search + "%", StandardBasicTypes.STRING)
//						,
//				Restrictions.sqlRestriction(
//						endDate + " LIKE (?)",
//						"%" + search + "%", StandardBasicTypes.STRING)
//			));
//		}
//		
//		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<Acting> getActingsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("actingId", ids));
		return criteria.list();
	}

	
	public List<Acting> getStartOrEndActing(Date date){
		Criteria criteria = this.createCriteria("a")
				.setFetchMode("staff", FetchMode.JOIN)
				.setFetchMode("replacement", FetchMode.JOIN);
		
		criteria.add(Restrictions.or(
				Restrictions.sqlRestriction("cast({alias}.startDate as date) <= ?", date, StandardBasicTypes.DATE),
				Restrictions.sqlRestriction("cast({alias}.endDate as date) >= ?", date, StandardBasicTypes.DATE)
			));
		
		return criteria.list();
	}

	public List<Acting> getActingByStartDate(Date date){
		Criteria criteria = this.createCriteria("a")
				.setFetchMode("staff", FetchMode.JOIN)
				.setFetchMode("replacement", FetchMode.JOIN);
		
		criteria.add(Restrictions.or(
				Restrictions.sqlRestriction("cast({alias}.startDate as date) = ?", date, StandardBasicTypes.DATE)
			));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getActingUserIdsByUserId(int userId) {
		
		String hql = "select s.userId "
				+ " from Acting as a "
				+ " left join a.staff as s "
				+ " left join a.replacement as r "
				// + " left join a.role as role "
				+ " where r.userId = :userId "
				+ " and a.startDate <= :sysdate "
				+ " and a.endDate >= :sysdate ";

		Query query = this.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		// remove timetsamp
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		query.setParameter("sysdate", today);
		
		return query.list();
		
	}

	public Integer getActedUserIdByUserId(int userId) {
		
		String hql = "select s.userId "
				+ " from Acting as a "
				+ " left join a.staff as s "
				+ " left join a.replacement as r "
				+ " where s.userId = :userId "
				+ " and a.startDate <= :sysdate "
				+ " and a.endDate >= :sysdate ";

		Query query = this.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		// remove timetsamp
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		query.setParameter("sysdate", today);
		
		query.setMaxResults(1);
		return (Integer)query.uniqueResult();
	}
	
	public List<ActingModel> getActingDetailsByReplacementUserId(int userId) {
		
		String sql = " select distinct staff.UserId as staffId, staff.Team as actedTeam, "
				+ " rolA_.AuthorityLevel as grantAuthorityLevel "
				+ " from [dbo].[Acting] a "
				+ " inner join [dbo].[User] staff on staff.UserId = a.UserId "
				+ " inner join [dbo].[User] replacement on replacement.UserId = a.ReplacementId "
				+ " left join [dbo].[Role] rolA_ on rolA_.RoleId = a.RoleId "
				+ " where replacement.UserId = :userId "
				+ " and (rolA_.authorityLevel is not null and (rolA_.authorityLevel & 2 = 2 or rolA_.authorityLevel & 4 = 4)) "
				+ " and a.startDate <= :sysdate "
				+ " and a.endDate >= :sysdate "
				+ " order by grantAuthorityLevel asc ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		query.setParameter("sysdate", today);
		
		query.addScalar("staffId", StandardBasicTypes.INTEGER)
				.addScalar("actedTeam", StandardBasicTypes.STRING)
				.addScalar("grantAuthorityLevel", StandardBasicTypes.INTEGER)
				.setResultTransformer(Transformers.aliasToBean(ActingModel.class));
		
		return query.list();
	}
}
