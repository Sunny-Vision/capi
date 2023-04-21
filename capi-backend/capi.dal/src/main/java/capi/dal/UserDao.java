package capi.dal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.UserReport;
import capi.model.api.dataSync.UserRoleSyncData;
import capi.model.api.dataSync.UserSyncData;
import capi.model.commonLookup.UserLookupTableList;
import capi.model.report.IndividualQuotationRecordReport1;
import capi.model.report.SummaryOfNightSessionReport;
import capi.model.report.SummaryOfVerificationCasesReport;
import capi.model.userAccountManagement.FieldExperienceTableList;
import capi.model.userAccountManagement.StaffProfileTableList;
import capi.model.userAccountManagement.UserPasswordTableModel;
import capi.model.userAccountManagement.UserRoleTableList;

@Repository("UserDao")
public class UserDao extends GenericDao<User>{

	public User findActiveUserByUsernamePassword(String username, String password){
		return (User)this.createCriteria()
			.add(Restrictions.and(
				Restrictions.eq("username", username),
				Restrictions.eq("password", password),
				Restrictions.eq("status", "Active")
			)).uniqueResult();
	}
	
	
	public User findActiveUserByUsername(String username){
		return (User)this.createCriteria()
				.add(Restrictions.and(
					Restrictions.eq("username", username),
					Restrictions.eq("status", "Active")
				)).uniqueResult();
		
	}
	
	public User findUserByUsername(String username){
		return (User)this.createCriteria()
				.add(Restrictions.eq("username", username))
				.uniqueResult();
	}
	
	public List<UserReport> genUserReport(){
		return this.createCriteria()
				.setProjection(Projections.projectionList()
				        .add( Projections.property("username"), "username" )
				        .add(  Projections.property("englishName"), "englishName" )
				        .add(  Projections.property("chineseName"), "chineseName" ))
				.setResultTransformer(Transformers.aliasToBean(UserReport.class)).list();
	}
	
	public List<UserPasswordTableModel> searchUserPassword(String userName) {

		Criteria criteria = this.createCriteria("u");

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("u.userId"), "userId");
		projList.add(Projections.property("u.username"), "userName");
		projList.add(Projections.property("u.password"), "oldPassword");
		
		criteria.setProjection(projList);

		criteria.add(Restrictions.eq("u.username", userName));
		
		criteria.setResultTransformer(Transformers.aliasToBean(UserPasswordTableModel.class));
		
		return criteria.list();
	}
	
	public User getUserByUserNameAndPw(String userName, String hashPass) {
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.and(
				Restrictions.eq("username", userName),
				Restrictions.eq("password", hashPass)
				));
		return (User)criteria.uniqueResult();
	}
	
	public List<User> getActedUsers(Integer userId){
		String hql = "select u from Acting as a "
				+ " inner join a.staff as u "
				+ " inner join a.replacement as r "
				+ " where convert(date, getDate()) between a.startDate and a.endDate and r.userId = :userId";		
		
		Query query = this.getSession().createQuery(hql);	
		query.setParameter("userId", userId);
		return query.list();
	}
	
	public User getUserWithDistrictById(Integer userId){
		Criteria criteria = this.createCriteria("u");
		criteria.setFetchMode("districts", FetchMode.JOIN);
		criteria.add(Restrictions.eqOrIsNull("userId", userId));
		return (User)criteria.uniqueResult();
	}
	
	public List<User> getActiveUsersWithAuthorityLevel(int authorityLevel, Integer[] staffIds){
		return getActiveUsersWithAuthorityLevel(authorityLevel, staffIds, null);
	}
	
	public List<User> getActiveUsersWithAuthorityLevel(int authorityLevel, Integer[] staffIds, Integer limit){
		/*
		  String hql = "select u from User as u " 
				+ "inner join u.roles as r "
				+ "where r.authorityLevel & :authorityLevel = :authorityLevel "
				+ "group by u.userId";
		*/
		Type[] type = {IntegerType.INSTANCE, IntegerType.INSTANCE};
		Integer[] values = {authorityLevel, authorityLevel};
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
				.add(Restrictions.sqlRestriction("authorityLevel & ? = ?", values, type))
				.add(Restrictions.ne("status", "Inactive"));     
                /*.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("u.userId"), "userId")
                .add(Projections.groupProperty("u.attemptNumber"), "attemptNumber")
                .add(Projections.groupProperty("u.batches"), "batches")
                .add(Projections.groupProperty("u.chineseName"), "chineseName")
                .add(Projections.groupProperty("u.dateOfEntry"), "dateOfEntry")
                .add(Projections.groupProperty("u.dateOfLeaving"), "dateOfLeaving")
                .add(Projections.groupProperty("u.destination"), "destination")
                .add(Projections.groupProperty("u.deviceKey"), "deviceKey")
                .add(Projections.groupProperty("u.districts"), "districts")
                .add(Projections.groupProperty("u.englishName"), "englishName")
                .add(Projections.groupProperty("u.gender"), "gender")
                .add(Projections.groupProperty("u.gic"), "gic")
                .add(Projections.groupProperty("u.homeArea"), "homeArea")
                .add(Projections.groupProperty("u.lastLoginTime"), "lastLoginTime")
                .add(Projections.groupProperty("u.notifications"), "notifications")
                .add(Projections.groupProperty("u.officePhoneNo"), "officePhoneNo")
                .add(Projections.groupProperty("u.omp"), "omp")
                .add(Projections.groupProperty("u.password"), "password")
                .add(Projections.groupProperty("u.roles"), "roles")
                .add(Projections.groupProperty("u.staffCode"), "staffCode")
                .add(Projections.groupProperty("u.staffType"), "staffType")
                .add(Projections.groupProperty("u.status"), "status")
                .add(Projections.groupProperty("u.team"), "team")
                .add(Projections.groupProperty("u.username"), "username"));*/
		
		if(staffIds != null){
			criteria.add(Restrictions.in("u.userId", staffIds));
		}
		
		if (limit != null) {
			criteria.setFetchSize(limit);
		}
		
		criteria.addOrder(Order.asc("staffCode"));
		criteria.addOrder(Order.asc("englishName"));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return criteria.list();

	}
	
	public List<Integer> getActiveUserIdsWithAuthorityLevel(int authorityLevel, Integer[] staffIds, Integer limit){
		Type[] type = {IntegerType.INSTANCE, IntegerType.INSTANCE};
		Integer[] values = {authorityLevel, authorityLevel};
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
				.add(Restrictions.sqlRestriction("authorityLevel & ? = ?", values, type))
				.add(Restrictions.ne("status", "Inactive"));     
		
		if(staffIds != null){
			criteria.add(Restrictions.in("u.userId", staffIds));
		}
		
		if (limit != null) {
			criteria.setFetchSize(limit);
		}
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("u.userId"));
		
		criteria.setProjection(projList);
		
		criteria.addOrder(Order.asc("staffCode"));
		criteria.addOrder(Order.asc("englishName"));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return criteria.list();
	}

	public List<UserLookupTableList> getLookupTableListTeamOnly(String search,
			int firstRecord, int displayLength, Order order,
			Integer authorityLevel, String username, Boolean withSelf) {
		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		
//		Criteria criteria = this.createCriteria("u")
//				.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("sup.actedBy", "actBy", JoinType.LEFT_OUTER_JOIN, Restrictions.and(
//						Restrictions.ge("actBy.startDate", date),
//						Restrictions.le("actBy.endDate", date)
//				))
//				.createAlias("actBy.replacement", "acting", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("sup.subordinates", "sub", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("u.roles", "r", JoinType.LEFT_OUTER_JOIN);
//		
//		if (withSelf != null && withSelf){
//			criteria.add(Restrictions.or(
//					Restrictions.eq("sup.username", username),
//					Restrictions.and(
//							Restrictions.eq("acting.username", username),
//							Restrictions.eqProperty("u.userId", "sub.userId")
//						),
//					Restrictions.eq("u.username", username)
//				));
//		}
//		else{
//			criteria.add(Restrictions.or(
//					Restrictions.eq("sup.username", username),
//					Restrictions.and(
//							Restrictions.eq("acting.username", username),
//							Restrictions.eqProperty("u.userId", "sub.userId")
//						)
//				));
//		}
//
//		criteria.add(Restrictions.ne("u.status","Inactive"));
//		if (authorityLevel != null)
//			criteria.add(Restrictions.sqlRestriction("(u.authorityLevel & ?) > 0", values, type));
//		
//		if (StringUtils.isNotEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("u.team", "%" + search + "%"),
//					Restrictions.like("u.englishName", "%" + search + "%"),
//					Restrictions.like("u.chineseName", "%" + search + "%"),
//					Restrictions.like("u.destination", "%" + search + "%"),
//					Restrictions.like("u.staffCode", "%" + search + "%")
//				));
//		}
//		
//		ProjectionList projList = Projections.projectionList();
//        projList.add(Projections.groupProperty("u.id"), "id");
//        projList.add(Projections.groupProperty("u.staffCode"), "staffCode");
//        projList.add(Projections.groupProperty("u.team"), "team");
//        projList.add(Projections.groupProperty("u.chineseName"), "chineseName");
//        projList.add(Projections.groupProperty("u.englishName"), "englishName");
//        projList.add(Projections.groupProperty("u.destination"), "destination");
//		criteria.setProjection(projList);
//        
//		criteria.setFirstResult(firstRecord);
//		criteria.setMaxResults(displayLength);
//		criteria.addOrder(order);
//        
//		criteria.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
//		
//		return criteria.list();
		
		String sql = " select u.UserId as id, u.StaffCode as staffCode, u.Team as team "
				+ ", u.ChineseName as chineseName, u.EnglishName as englishName, u.Destination as destination "
				+ " from [User] as u "
				+ " left join [User] as sup on u.SupervisorId = sup.UserId "
				+ " left join Acting as actBy on sup.UserId = actBy.UserId and actBy.startDate <= :date and actBy.endDate >= :date "
				+ " left join [User] as acting on actBy.ReplacementId = acting.UserId "
//				+ " left join [User] as sub on sup.UserId = sub.SupervisorId "
				+ " left join UserRole as ur on u.UserId = ur.UserId "
				+ " left join Role as r on ur.RoleId = r.RoleId "
				+ " where u.Status <> :status ";
		
		if (withSelf != null && withSelf){
			sql += " and (sup.Username = :username or acting.Username = :username  or u.Username = :username) ";
		}
		else{
			sql += " and (sup.Username = :username or acting.Username = :username ) ";
		}
		if (authorityLevel != null)
			sql += " and (r.AuthorityLevel & :value) > 0 ";
		if (StringUtils.isNotEmpty(search)) {
			sql += " and (u.StaffCode like :search or u.Team like :search "
				+ " or u.ChineseName like :search or u.EnglishName like :search or u.Destination like :search)";
		}
		
		sql += "group by u.UserId, u.StaffCode, u.Team, u.ChineseName, u.EnglishName, u.Destination ";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending() ? " asc " : " desc ");
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("date", date);
		sqlQuery.setParameter("status", "Inactive");
		sqlQuery.setParameter("username", username);
		if (authorityLevel != null)
			sqlQuery.setParameter("value", values);
		if (StringUtils.isNotEmpty(search))
			sqlQuery.setParameter("search", String.format("%%%s%%", search));
		
		sqlQuery.addScalar("id", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("staffCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("team", StandardBasicTypes.STRING);
		sqlQuery.addScalar("chineseName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("englishName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("destination", StandardBasicTypes.STRING);
		
		sqlQuery.setFirstResult(firstRecord);
		sqlQuery.setMaxResults(displayLength);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
		
		return sqlQuery.list();
	}
	
	
	public long countLookupTableListTeamOnly(String search,
			Integer authorityLevel, String username, Boolean withSelf) {
		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		
//		Criteria criteria = this.createCriteria("u")
//				.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("sup.actedBy", "actBy", JoinType.LEFT_OUTER_JOIN, Restrictions.and(
//						Restrictions.ge("actBy.startDate", date),
//						Restrictions.le("actBy.endDate", date)
//				))
//				.createAlias("actBy.replacement", "acting", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("sup.subordinates", "sub", JoinType.LEFT_OUTER_JOIN)
//				.createAlias("u.roles", "r", JoinType.LEFT_OUTER_JOIN);
//		
//		if (withSelf != null && withSelf){
//			criteria.add(Restrictions.or(
//					Restrictions.eq("sup.username", username),
//					Restrictions.and(
//							Restrictions.eq("acting.username", username),
//							Restrictions.eqProperty("u.userId", "sub.userId")
//						),
//					Restrictions.eq("u.username", username)
//				));
//		}
//		else{
//			criteria.add(Restrictions.or(
//					Restrictions.eq("sup.username", username),
//					Restrictions.and(
//						Restrictions.eq("acting.username", username),
//						Restrictions.eqProperty("u.userId", "sub.userId")
//					)
//				));
//		}
//		
//		criteria.add(Restrictions.ne("u.status","Inactive"));
//		if (authorityLevel != null)
//			criteria.add(Restrictions.sqlRestriction("(u.authorityLevel & ?) > 0", values, type));
//		
//		if (StringUtils.isNotEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("u.team", "%" + search + "%"),
//					Restrictions.like("u.englishName", "%" + search + "%"),
//					Restrictions.like("u.chineseName", "%" + search + "%"),
//					Restrictions.like("u.destination", "%" + search + "%"),
//					Restrictions.like("u.staffCode", "%" + search + "%")
//				));
//		}
//		
//		SQLProjectionExt projList = SQLProjectionExt.groupCount(
//				Projections.groupProperty("u.id"),
//				Projections.groupProperty("u.staffCode"),
//				Projections.groupProperty("u.team"),
//				Projections.groupProperty("u.chineseName"),
//				Projections.groupProperty("u.englishName"),
//				Projections.groupProperty("u.destination")
//		);
//				
//		criteria.setProjection(projList);        
//		
//		return (long)criteria.uniqueResult();
		
		String sql = " select count(distinct u.UserId) "
				+ " from [User] as u "
				+ " left join [User] as sup on u.SupervisorId = sup.UserId "
				+ " left join Acting as actBy on sup.UserId = actBy.UserId and actBy.startDate <= :date and actBy.endDate >= :date "
				+ " left join [User] as acting on actBy.ReplacementId = acting.UserId "
//				+ " left join [User] as sub on sup.UserId = sub.SupervisorId "
				+ " left join UserRole as ur on u.UserId = ur.UserId "
				+ " left join Role as r on ur.RoleId = r.RoleId "
				+ " where u.Status <> :status ";
		
		if (withSelf != null && withSelf){
			sql += " and (sup.Username = :username or acting.Username = :username  or u.Username = :username) ";
		}
		else{
			sql += " and (sup.Username = :username or acting.Username = :username ) ";
		}
		if (authorityLevel != null)
			sql += " and (r.AuthorityLevel & :value) > 0 ";
		if (StringUtils.isNotEmpty(search)) {
			sql += " and (u.StaffCode like :search or u.Team like :search "
				+ " or u.ChineseName like :search or u.EnglishName like :search or u.Destination like :search)";
		}
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("date", date);
		sqlQuery.setParameter("status", "Inactive");
		sqlQuery.setParameter("username", username);
		if (authorityLevel != null)
			sqlQuery.setParameter("value", values);
		if (StringUtils.isNotEmpty(search))
			sqlQuery.setParameter("search", String.format("%%%s%%", search));
		
		Integer count = (Integer)sqlQuery.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<UserLookupTableList> getLookupTableList(String search,
			int firstRecord, int displayLength, Order order,
			Integer authorityLevel) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").add(Restrictions.ne("status", "Inactive"));
		
		if (authorityLevel != null)
			criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type)); // for the sake of multiple authority level
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "id");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("team"), "team");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
        projList.add(Projections.groupProperty("destination"), "destination");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		criteria.addOrder(order);
        
		criteria.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
        
        return criteria.list();
	}
	
	public long countLookupTableList(String search, Integer authorityLevel) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").add(Restrictions.ne("status", "Inactive"));
		
		if (authorityLevel != null)
			criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getLookupTableSelectAll(String search, Integer authorityLevel) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").add(Restrictions.ne("status", "Inactive"));
		
		if (authorityLevel != null)
			criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		
		criteria.setProjection(Projections.groupProperty("id"));

		return (List<Integer>)criteria.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Integer> getLookupTableTeamOnlySelectAll(String search, Integer authorityLevel, String username, Boolean withSelf) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		
		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
				.createAlias("sup.actedBy", "actBy", JoinType.LEFT_OUTER_JOIN, Restrictions.and(
						Restrictions.ge("actBy.startDate", date),
						Restrictions.le("actBy.endDate", date)
				))
				.createAlias("actBy.replacement", "acting", JoinType.LEFT_OUTER_JOIN)
				.createAlias("sup.subordinates", "sub", JoinType.LEFT_OUTER_JOIN);
		
		if (withSelf != null && withSelf){
			criteria.add(Restrictions.or(
					Restrictions.eq("sup.username", username),
					Restrictions.and(
							Restrictions.eq("acting.username", username),
							Restrictions.eqProperty("u.userId", "sub.userId")
						),
					Restrictions.eq("u.username", username)
				));
		}
		else{
			criteria.add(Restrictions.or(
					Restrictions.eq("sup.username", username),
					Restrictions.and(
							Restrictions.eq("acting.username", username),
							Restrictions.eqProperty("u.userId", "sub.userId")
						)
				));
		}
				
				
		criteria.add(Restrictions.ne("u.status","Inactive"));
		if (authorityLevel != null)
			criteria.add(Restrictions.sqlRestriction("(u.authorityLevel & ?) > 0", values, type));
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.team", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("u.destination", "%" + search + "%"),
					Restrictions.like("u.staffCode", "%" + search + "%")
				));
		}		
		
		criteria.setProjection(Projections.groupProperty("u.id"));

		return (List<Integer>)criteria.list();
	}
	
	

	public List<FieldExperienceTableList> selectAllFieldExperience(Integer userId, String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("u")
								.createAlias("u.districts", "d", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("u.userId"), "userId");
		projList.add(Projections.groupProperty("u.staffCode"), "staffCode");
		projList.add(Projections.groupProperty("u.team"), "team");
		projList.add(Projections.groupProperty("u.chineseName"), "chineseName");
		projList.add(Projections.groupProperty("u.englishName"), "englishName");
		projList.add(Projections.count("d.districtId"), "countDistrict");
		
		criteria.setProjection(projList);

		criteria.add(Restrictions.and(
				Restrictions.eq("u.staffType", 1),
				Restrictions.ne("u.status", "Inactive")));

		if(userId != null){
			criteria.add(Restrictions.or(
					Restrictions.eq("u.userId", userId),
					Restrictions.eq("u.supervisor.userId", userId)));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("staffCode", "%" + search + "%"),
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(FieldExperienceTableList.class));

		return criteria.list();
	}

	public long countSelectAllFieldExperience(Integer userId, String search) {

		Criteria criteria = this.createCriteria("u")
								.createAlias("u.districts", "d", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("u.userId"),
				Projections.groupProperty("u.staffCode"),
				Projections.groupProperty("u.team"),
				Projections.groupProperty("u.chineseName"),
				Projections.groupProperty("u.englishName")
		));

		criteria.setProjection(projList);

		criteria.add(Restrictions.and(
				Restrictions.eq("u.staffType", 1),
				Restrictions.ne("u.status", "Inactive")));

		if(userId != null){
			criteria.add(Restrictions.or(
					Restrictions.eq("u.userId", userId),
					Restrictions.eq("u.supervisor.userId", userId)));
		}
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("staffCode", "%" + search + "%"),
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%")
			));
		}

		return (long) criteria.uniqueResult();
	}

	public List<Integer> getDistrictIdsFromUser(Integer id) {

		Criteria criteria = this.createCriteria("u")
				.createAlias("u.districts", "d", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("d.districtId"));
		
		criteria.setProjection(projList);
		
//		criteria.add(Restrictions.eq("d.user.userId", id));
		criteria.add(Restrictions.eq("u.userId", id));
		
		return criteria.list();
	}

	public FieldExperienceTableList getRankNSupervisorFromUser(Integer id) {

		Criteria criteria = this.createCriteria("u")
				.createAlias("u.rank", "r", JoinType.LEFT_OUTER_JOIN)
				.createAlias("u.supervisor", "us", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("r.name"), "rankName");
		projList.add(Projections.property("us.userId"), "supervisorId");
		projList.add(Projections.property("us.staffCode"), "supervisorStaffCode");
		projList.add(Projections.property("us.chineseName"), "supervisorChineseName");
		projList.add(Projections.property("us.destination"), "supervisorDestination");
		
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("u.userId", id));

		criteria.setResultTransformer(Transformers.aliasToBean(FieldExperienceTableList.class));

		return (FieldExperienceTableList)criteria.uniqueResult();
	}

	public String getSupervisorFromUserId(Integer userId) {
		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN);

		String supervisor = "{s}.staffCode + ' - ' + {s}.chineseName";
		
		ProjectionList projList = Projections.projectionList();
		
		projList.add(SQLProjectionExt.sqlProjection(supervisor + " as supervisor", new String [] {"supervisor"}, new Type[]{StandardBasicTypes.STRING}), "supervisor");
		
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("u.userId", userId));
		
		return (String)criteria.uniqueResult();
	}
	
	public User getSupervisorByUserId(Integer userId) {
		String hql = "select s from User as u "
				+ " inner join u.supervisor as s "
				+ " where u.userId = :userId";		
		
		Query query = this.getSession().createQuery(hql);	
		query.setParameter("userId", userId);
		
		return (User)query.uniqueResult();
	}
	
	public List<User> getUsersByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria("u");
		criteria.add(Restrictions.in("u.userId", ids));
		return criteria.list();
	}
	
	public List<User> getActiveUserByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria("u");
		criteria.add(
			Restrictions.and(
				Restrictions.ne("u.status", SystemConstant.USER_STATUS_INACTIVE),
				Restrictions.in("u.userId", ids)
			)
		);
		return criteria.list();
	}

	public User getUserByStaffCode(String staffCode) {
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("staffCode", staffCode));
		return (User)criteria.uniqueResult();
	}
	
	public List<User> getUserByStaffCodes(List<String> staffCode) {
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("staffCode", staffCode));
		return criteria.list();
	}

	public List<StaffProfileTableList> selectAllStaffProfile(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("u")
								.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.rank", "r", JoinType.LEFT_OUTER_JOIN);

		String rank = "{r}.code + ' - ' + {r}.name";
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("u.userId"), "userId");
		projList.add(Projections.property("u.staffCode"), "staffCode");
		projList.add(Projections.property("u.username"), "userName");
		projList.add(Projections.property("u.englishName"), "englishName");
		projList.add(Projections.property("u.chineseName"), "chineseName");
		projList.add(Projections.property("s.staffCode"), "supervisorStaffCode");
		projList.add(SQLProjectionExt.sqlProjection(rank + " as rank" , new String[]{"rank"}, new Type[]{StandardBasicTypes.STRING}), "rank");
		
		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.username", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("s.staffCode", "%" + search + "%"),
					Restrictions.like("r.code", "%" + search + "%"),
					Restrictions.like("r.name", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));

		return criteria.list();
	}

	public long countSelectAllStaffProfile(String search) {

		Criteria criteria = this.createCriteria("u")
								.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.rank", "r", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(Projections.groupProperty("u.userId")));
		
		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.username", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("s.staffCode", "%" + search + "%"),
					Restrictions.like("r.code", "%" + search + "%"),
					Restrictions.like("r.name", "%" + search + "%")
			));
		}

		return (long)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<UserRoleTableList> selectUserRoleList(String search, int firstRecord, int displayLength, Order order, Integer userId) {

		Criteria criteria = this.createCriteria("u")
								.createAlias("u.roles", "r", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("r.roleId"), "roleId");
		projList.add(Projections.property("r.name"), "code");
		projList.add(Projections.property("r.description"), "description");
		
		criteria.setProjection(projList);

		criteria.add(Restrictions.eq("u.userId", userId));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("r.name", "%" + search + "%"),
					Restrictions.like("r.description", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(UserRoleTableList.class));

		return criteria.list();
	}

	public long countSelectUserRoleList(String search, Integer userId) {

		Criteria criteria = this.createCriteria("u")
								.createAlias("u.roles", "r", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("u.userId", userId));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("r.name", "%" + search + "%"),
					Restrictions.like("r.description", "%" + search + "%")
			));
		}

		criteria.setResultTransformer(Transformers.aliasToBean(UserRoleTableList.class));

		return (long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public List<User> getAuthorizedUserForClosingMaintenance(){
		Criteria criteria = this.createCriteria("u")
								.createAlias("u.roles", "r")
								.createAlias("r.functions", "f", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.sqlRestriction("authorityLevel & 256 = 256"));
		criteria.add(Restrictions.eq("f.code", "UF1113"));
		criteria.add(Restrictions.ne("u.status", "Inactive"));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return criteria.list();
	}
	
	public List<User> getActiveUsersWithAnyAuthorityLevel(int combinedAuthorityLevel){		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
				.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", combinedAuthorityLevel, IntegerType.INSTANCE))
				.add(Restrictions.ne("status", "Inactive"));    
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return criteria.list();
	}

	/**
	 * Search for field officer only
	 * @param search
	 * @param firstRecord
	 * @param displayLength
	 * @param supervisorIds
	 * @param officerId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchOfficer(String search,
			int firstRecord, int displayLength, Integer[] supervisorIds, Integer officerId) {

		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN)
				.createAlias("u.roles", "r")
				.add(Restrictions.ne("u.status","Inactive"));
		
		if (officerId != null && supervisorIds != null && supervisorIds.length > 0){
			criteria.add(Restrictions.or(
					Restrictions.eq("u.userId", officerId),
					Restrictions.in("s.userId", supervisorIds)
				));
		}
		else if (officerId != null){
			criteria.add(Restrictions.eq("u.userId", officerId));
		}
		else if (supervisorIds != null && supervisorIds.length > 0){
			criteria.add(Restrictions.in("s.userId", supervisorIds));
		}			
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		criteria.add(Restrictions.sqlRestriction("(authorityLevel & 16) = 16"));

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "userId");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
    
		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
        
        return criteria.list();
	}
	
	/**
	 * Search for field officer and supervisor
	 * @param search
	 * @param firstRecord
	 * @param displayLength
	 * @param supervisorIds
	 * @param officerId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchOfficerSupervisor(String search,
			int firstRecord, int displayLength, Integer[] supervisorIds, Integer officerId) {

		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN)
				.createAlias("u.roles", "r")
				.add(Restrictions.ne("u.status","Inactive"));
		
		if (officerId != null && supervisorIds != null && supervisorIds.length > 0){
			criteria.add(Restrictions.or(
					Restrictions.eq("u.userId", officerId),
					Restrictions.in("s.userId", supervisorIds)
				));
		}
		else if (officerId != null){
			criteria.add(Restrictions.eq("u.userId", officerId));
		}
		else if (supervisorIds != null && supervisorIds.length > 0){
			criteria.add(Restrictions.in("s.userId", supervisorIds));
		}			
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		criteria.add(Restrictions.sqlRestriction("((authorityLevel & 16) = 16 or (authorityLevel & 4) = 4)"));

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "userId");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
    
		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
        
        return criteria.list();
	}
	
	/**
	 * Search for field officer only
	 * @param search
	 * @param firstRecord
	 * @param displayLength
	 * @param teamName
	 * @param officerId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchOfficer(String search,
			int firstRecord, int displayLength, List<String> teamName) {

		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN);
		
		if (teamName != null && teamName.size() > 0){
			criteria.add(Restrictions.or(
					Restrictions.in("u.team", teamName)
				));
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "userId");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
    
		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
        
        return criteria.list();
	}
	
	public long countSearchOfficer(String search, Integer[] supervisorIds, Integer officerId) {

		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN)
				.createAlias("u.roles", "r")
				.add(Restrictions.ne("u.status","Inactive"));
		
		if (officerId != null && supervisorIds != null && supervisorIds.length > 0){
			criteria.add(Restrictions.or(
					Restrictions.eq("u.userId", officerId),
					Restrictions.in("s.userId", supervisorIds)
				));
		}
		else if (officerId != null){
			criteria.add(Restrictions.eq("u.userId", officerId));
		}
		else if (supervisorIds != null && supervisorIds.length > 0){
			criteria.add(Restrictions.in("s.userId", supervisorIds));
		}			
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		criteria.add(Restrictions.sqlRestriction("(authorityLevel & 16) = 16"));
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public long countSearchOfficerSupervisor(String search, Integer[] supervisorIds, Integer officerId) {

		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN)
				.createAlias("u.roles", "r")
				.add(Restrictions.ne("u.status","Inactive"));
		
		if (officerId != null && supervisorIds != null && supervisorIds.length > 0){
			criteria.add(Restrictions.or(
					Restrictions.eq("u.userId", officerId),
					Restrictions.in("s.userId", supervisorIds)
				));
		}
		else if (officerId != null){
			criteria.add(Restrictions.eq("u.userId", officerId));
		}
		else if (supervisorIds != null && supervisorIds.length > 0){
			criteria.add(Restrictions.in("s.userId", supervisorIds));
		}			
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		criteria.add(Restrictions.sqlRestriction("((authorityLevel & 16) = 16 or (authorityLevel & 4) = 4)"));
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public long countSearchOfficer(String search, List<String> teamName) {

		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN)
				.createAlias("u.roles", "r")
				.add(Restrictions.ne("u.status","Inactive"));
		
		if (teamName != null && teamName.size() > 0){
			criteria.add(Restrictions.in("u.team", teamName));
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	/**
	 * Search for field officer only
	 * @param search
	 * @param firstRecord
	 * @param displayLength
	 * @param supervisorIds
	 * @param officerId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> searchTeam(String search,
			int firstRecord, int displayLength, List<Integer> userIds) {

		Criteria criteria = this.createCriteria("u");
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.team", "%" + search + "%")
				));
		}
		if (userIds != null && userIds.size() > 0){
			criteria.add(Restrictions.in("u.userId", userIds));
		}

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("u.team"), "team");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        
        return criteria.list();
	}
	
	public long countSearchTeam(String search, List<Integer> userIds) {

		Criteria criteria = this.createCriteria("u");
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.team", "%" + search + "%")
				));
		}
		if (userIds != null && userIds.size() > 0){
			criteria.add(Restrictions.in("u.userId", userIds));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("u.team")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchFieldHeadSectionHead(String search,
			int firstRecord, int displayLength) {
		
		String sql = " select this_.UserId as userId, this_.StaffCode as staffCode, "
				+ " this_.ChineseName as chineseName, this_.EnglishName as englishName "
				+ " from dbo.[User] this_ "
				+ " left outer join dbo.[UserRole] ur_ on ur_.UserId = this_.UserId"
				+ " left outer join dbo.[Role] roo_ on roo_.RoleId = ur_.RoleId "
				+ " left outer join dbo.[Acting] a_ on a_.ReplacementId = this_.UserId "
				+ " left outer join dbo.[Role] roa_ on roa_.RoleId = a_.RoleId "
				+ " where ( "
				+ "		((roo_.AuthorityLevel & 1) = 1 or (roo_.AuthorityLevel & 2) = 2) "
				+ "		or ((a_.StartDate <= :today and a_.EndDate >= :today)"
				+ "				and (roa_.AuthorityLevel & 2) = 2) "
				+ " ) "
				+ " and (this_.[Status] = 'Active')";
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and (this_.StaffCode like :search or this_.EnglishName like :search or this_.ChineseName like :search ) ";
		}
		
		sql += " group by this_.UserId, this_.StaffCode, this_.ChineseName, this_.EnglishName ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		query.setParameter("today", today);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.addScalar("userId", StandardBasicTypes.INTEGER)
				.addScalar("staffCode", StandardBasicTypes.STRING)
				.addScalar("chineseName", StandardBasicTypes.STRING)
				.addScalar("englishName", StandardBasicTypes.STRING);
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
	
//		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
//				.add(Restrictions.sqlRestriction("((authorityLevel & 1) = 1 or (authorityLevel & 2) = 2 )"));
//
//		if (StringUtils.isNotEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("u.staffCode", "%" + search + "%"),
//					Restrictions.like("u.englishName", "%" + search + "%"),
//					Restrictions.like("u.chineseName", "%" + search + "%")
//				));
//		}

//        ProjectionList projList = Projections.projectionList();
//        projList.add(Projections.groupProperty("id"), "userId");
//        projList.add(Projections.groupProperty("staffCode"), "staffCode");
//        projList.add(Projections.groupProperty("chineseName"), "chineseName");
//        projList.add(Projections.groupProperty("englishName"), "englishName");
//		criteria.setProjection(projList);
//        
//		criteria.setFirstResult(firstRecord);
//		criteria.setMaxResults(displayLength);
//    
//		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
        
        return query.list();
	}
	

	public long countSearchFieldHeadSectionHead(String search) {
		
		String sql = " select count(distinct this_.UserId) as total "
				+ " from dbo.[User] this_ "
				+ " left outer join dbo.[UserRole] ur_ on ur_.UserId = this_.UserId"
				+ " left outer join dbo.[Role] roo_ on roo_.RoleId = ur_.RoleId "
				+ " left outer join dbo.[Acting] a_ on a_.ReplacementId = this_.UserId "
				+ " left outer join dbo.[Role] roa_ on roa_.RoleId = a_.RoleId "
				+ " where ( "
				+ "		((roo_.AuthorityLevel & 1) = 1 or (roo_.AuthorityLevel & 2) = 2) "
				+ "		or ((a_.StartDate <= :today and a_.EndDate >= :today)"
				+ "				and (roa_.AuthorityLevel & 2) = 2) "
				+ " ) "
				+ " and (this_.[Status] = 'Active')";

		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and (this_.StaffCode like :search or this_.EnglishName like :search or this_.ChineseName like :search ) ";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		query.setParameter("today", today);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.addScalar("total", StandardBasicTypes.LONG);
		
		Long count = (Long) query.uniqueResult();
		return count == null ? 0 : count;

//		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
//				.add(Restrictions.sqlRestriction("((authorityLevel & 1) = 1 or (authorityLevel & 2) = 2 )"));
//		
//		if (StringUtils.isNotEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("u.staffCode", "%" + search + "%"),
//					Restrictions.like("u.englishName", "%" + search + "%"),
//					Restrictions.like("u.chineseName", "%" + search + "%")
//				));
//		}
//		
//		SQLProjectionExt project = SQLProjectionExt.groupCount(
//        		Projections.groupProperty("id")
//		);
//
//		criteria.setProjection(project);
//
//		Long count = (Long)criteria.uniqueResult();
//		return count == null ? 0 : count;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchSupervisor(Integer userId, String search,
			int firstRecord, int displayLength) {
	
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").add(Restrictions.sqlRestriction("authorityLevel & 4 = 4"));

		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "userId");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
    
		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
        
        return criteria.list();
	}
	
	public long countSearchSupervisor(Integer userId, String search) {

		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").add(Restrictions.sqlRestriction("authorityLevel & 4 = 4"));
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	

	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchSupervisorAndHeadUser(Integer userId, String search,
			int firstRecord, int displayLength) {
		
		String sql = " select u_.UserId as userId, u_.StaffCode as staffCode, u_.ChineseName as chineseName, "
				+ " u_.EnglishName as englishName "
				+ " from dbo.[User] u_ "
				+ " left join dbo.[UserRole] ur_ on ur_.UserId = u_.UserId "
				+ " left join dbo.[Role] roo_ on roo_.RoleId = ur_.RoleId "
				+ " left join dbo.[Acting] a_ on a_.ReplacementId = u_.UserId "
				+ " left join dbo.[Role] roa_ on roa_.RoleId = a_.RoleId"
				+ " where ("
				+ "	((a_.StartDate <= :today and a_.EndDate >= :today) and (roa_.AuthorityLevel & 4) = 4) "
				+ "	or ((roo_.AuthorityLevel & 4) = 4 or (roo_.AuthorityLevel & 1) = 1 or (roo_.AuthorityLevel & 2) = 2) "
				+ "	) "
				+ " and (u_.[Status] = 'Active')";
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( u_.StaffCode like :search or u_.EnglishName like :search or u_.ChineseName like :search ) ";
		}
		
		sql += " group by u_.UserId, u_.StaffCode, u_.EnglishName, u_.ChineseName ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		query.setParameter("today", today);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.addScalar("userId", StandardBasicTypes.INTEGER)
			.addScalar("staffCode", StandardBasicTypes.STRING)
			.addScalar("chineseName", StandardBasicTypes.STRING)
			.addScalar("englishName", StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
		
		return query.list();
	
//		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
//				.add(Restrictions.sqlRestriction("((authorityLevel & 4) = 4 or (authorityLevel & 1) = 1 or (authorityLevel & 2) = 2)"));
//
//		if (StringUtils.isNotEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("u.staffCode", "%" + search + "%"),
//					Restrictions.like("u.englishName", "%" + search + "%"),
//					Restrictions.like("u.chineseName", "%" + search + "%")
//				));
//		}
//
//        ProjectionList projList = Projections.projectionList();
//        projList.add(Projections.groupProperty("id"), "userId");
//        projList.add(Projections.groupProperty("staffCode"), "staffCode");
//        projList.add(Projections.groupProperty("chineseName"), "chineseName");
//        projList.add(Projections.groupProperty("englishName"), "englishName");
//		criteria.setProjection(projList);
//        
//		criteria.setFirstResult(firstRecord);
//		criteria.setMaxResults(displayLength);
//    
//		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
//        
//        return criteria.list();
	}
	
	public long countSearchSupervisorAndHeadUser(Integer userId, String search) {
		
		String sql = " select count(distinct u_.UserId) as total "
				+ " from dbo.[User] u_ "
				+ " left join dbo.[UserRole] ur_ on ur_.UserId = u_.UserId "
				+ " left join dbo.[Role] roo_ on roo_.RoleId = ur_.RoleId "
				+ " left join dbo.[Acting] a_ on a_.ReplacementId = u_.UserId "
				+ " left join dbo.[Role] roa_ on roa_.RoleId = a_.RoleId"
				+ " where ("
				+ "	((a_.StartDate <= :today and a_.EndDate >= :today) and (roa_.AuthorityLevel & 4) = 4) "
				+ "	or ((roo_.AuthorityLevel & 4) = 4 or (roo_.AuthorityLevel & 1) = 1 or (roo_.AuthorityLevel & 2) = 2) "
				+ "	) "
				+ " and (u_.[Status] = 'Active')";
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( u_.StaffCode like :search or u_.EnglishName like :search or u_.ChineseName like :search ) ";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		query.setParameter("today", today);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.addScalar("total", StandardBasicTypes.LONG);
		
		Long count = (Long) query.uniqueResult();
		
		return count == null ? 0 : count;

//		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
//				.add(Restrictions.sqlRestriction("((authorityLevel & 4) = 4 or (authorityLevel & 1) = 1 or (authorityLevel & 2) = 2)"));
//		
//		if (StringUtils.isNotEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("u.staffCode", "%" + search + "%"),
//					Restrictions.like("u.englishName", "%" + search + "%"),
//					Restrictions.like("u.chineseName", "%" + search + "%")
//				));
//		}
//		
//		SQLProjectionExt project = SQLProjectionExt.groupCount(
//        		Projections.groupProperty("id")
//		);
//
//		criteria.setProjection(project);
//
//		Long count = (Long)criteria.uniqueResult();
//		return count == null ? 0 : count;
	}
	
	

	public Double getDayStartTimeOffBalance(Date date, Integer userId){
		SQLQuery query = this.getSession().createSQLQuery("exec dbo.GetDayStartTimeOffBalance :date,:userId");
		query.setParameter("date", date);
		query.setParameter("userId", userId);
		
		query.addScalar("result", StandardBasicTypes.DOUBLE);
		
		return (Double)query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getOfficerIdsBySupervisors(List<Integer> supervisorIds) {

		Criteria criteria = this.createCriteria("u").createAlias("u.supervisor", "s")
				.add(Restrictions.in("s.userId", supervisorIds))
				.add(Restrictions.ne("u.status","Inactive"));
		
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "id");
		criteria.setProjection(projList);
		
        return (List<Integer>)criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchActiveUsersWithAuthorityLevel(String search, int firstRecord, int displayLength,
			int authorityLevel) {
		
		Type[] type = {IntegerType.INSTANCE, IntegerType.INSTANCE};
		Integer[] values = {authorityLevel, authorityLevel};
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
				.add(Restrictions.sqlRestriction("authorityLevel & ? = ?", values, type))
				.add(Restrictions.ne("status", "Inactive"));
		
		if (StringUtils.isNotBlank(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
        
		ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "userId");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
		criteria.setProjection(projList);
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        
//		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
		
		return criteria.list();
	}

	public long countSearchActiveUsersWithAuthorityLevel(String search, int authorityLevel) {
		
		Type[] type = {IntegerType.INSTANCE, IntegerType.INSTANCE};
		Integer[] values = {authorityLevel, authorityLevel};
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r")
				.add(Restrictions.sqlRestriction("authorityLevel & ? = ?", values, type))
				.add(Restrictions.ne("status", "Inactive"));
		
		if (StringUtils.isNotBlank(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);
        
		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public List<UserSyncData> getUpdateUser(Date lastSyncTime){
		Criteria criteria = this.createCriteria("u");
		criteria.createAlias("u.supervisor", "s", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ge("u.modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("u.userId"), "userId");
		projList.add(Projections.property("u.staffCode"),"staffCode");
		projList.add(Projections.property("u.destination"),"destination");
		projList.add(Projections.property("u.englishName"),"englishName");
		projList.add(Projections.property("u.chineseName"),"chineseName");
		projList.add(Projections.property("u.officePhoneNo"),"officePhoneNo");
		projList.add(Projections.property("u.status"),"status");
		projList.add(Projections.property("u.team"),"team");
		projList.add(Projections.property("s.userId"),"supervisorId");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(UserSyncData.class));
		return criteria.list();
	}
	
	public List<UserRoleSyncData> getUpdateUserRole(Date lastSyncTime){
		String sql = "Select a.userId as userId, a.roleId as roleId"
				+ ", a.createdDate as createdDate, a.modifiedDate as modifiedDate "
				+ "from UserRole a where "
				+ "a.modifiedDate >= :date";
		
		SQLQuery query = this.getSession().createSQLQuery(sql)
				.addScalar("userId", StandardBasicTypes.INTEGER)
				.addScalar("roleId", StandardBasicTypes.INTEGER)
				.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP);
		
		query.setParameter("date", lastSyncTime);
		
		query.setResultTransformer(Transformers.aliasToBean(UserRoleSyncData.class));
		
		return query.list();
				
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getSubordinatesByUserId(List<Integer> ids) {
		return this.createCriteria("u")
				.add(Restrictions.in("u.userId", ids))
				.createAlias("u.subordinates", "s")
				.setProjection(Projections.property("s.userId"))
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> searchTeam(String search, Integer firstRecord, Integer displayLength) {
		
		Criteria criteria = this.createCriteria("u");
		
		if (StringUtils.isNotBlank(search)) {
			criteria.add(
					Restrictions.like("u.team", "%" + search + "%")
				);
		}
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.distinct(Projections.property("u.team")), "team");
		criteria.setProjection(projList);
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<User> searchOfficerByTeam(String search, Integer firstRecord, Integer displayLength, String[] teams, List<Integer> userIds) {
		
		Criteria criteria = this.createCriteria("u");
		
		if (StringUtils.isNotBlank(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("u.staffCode", "%" + search + "%")
				)
			);
		}
		
		if (userIds != null && userIds.size() > 0){

			criteria.add(Restrictions.in("u.userId", userIds));
		}
		
		if (teams != null && teams.length > 0){

			criteria.add(Restrictions.in("u.team", teams));
		}
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		
		return criteria.list();
	}
	
	public Long countSearchOfficerByTeam(String search, String[] teams, List<Integer> userIds) {
		
		Criteria criteria = this.createCriteria("u");
		if (StringUtils.isNotBlank(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("u.staffCode", "%" + search + "%")
				)
			);
		}
		
		if (userIds != null && userIds.size() > 0){

			criteria.add(Restrictions.in("u.userId", userIds));
		}
		
		if (teams != null && teams.length > 0){

			criteria.add(Restrictions.in("u.team", teams));
		}

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.countDistinct("u.userId"));
		criteria.setProjection(projList);
		return (Long)criteria.uniqueResult();
	
	}
	
	
	/*public List<SummaryOfVerificationCasesReport> getSummaryOfVerificationCasesReport(
		List<Integer> purposeId,
		List<String> teams,
		List<Integer> officerIds,
		Date startReferenceMonth,
		Date endReferenceMonth,
		List<Integer> subOrdinates
		){
		
		String hql = 
				" select "
				+ " 	u.team as team,"
				+ "		r.name as rank,"
				+ "		concat(u.englishName, ' - ', u.staffCode) as displayName,"
				+ "		count(ap.purposeId) as assignments, "
				+ "		count(cp.purposeId) as categories, "
				+ "		count(qp.purposeId) as quotations "
				+ " from User u "
				+ " 	left join u.rank r"
				+ " 	left join u.indoorVerificationHistories as assignments on assignments.verifyType = 1 and assignments.referenceMonth >= :startReferenceMonth and assignments.referenceMonth <= :endReferenceMonth"
				+ " 	left join u.indoorVerificationHistories as categories on categories.verifyType = 2 and categories.referenceMonth >= :startReferenceMonth and categories.referenceMonth <= :endReferenceMonth"
				+ " 	left join u.indoorVerificationHistories as quotations on quotations.verifyType = 3 and quotations.referenceMonth >= :startReferenceMonth and quotations.referenceMonth <= :endReferenceMonth"
				+ "		left join assignments.quotationRecords as aqr"
				+ "		left join aqr.quotation as aq "
				+ "		left join aq.unit as aun"
				+ "		left join aun.purpose as ap" + (purposeId != null ? " on ap.purposeId in (:purposeId)" : "")
				+ "		left join categories.quotationRecords as cqr"
				+ "		left join cqr.quotation as cq "
				+ "		left join cq.unit as cun"
				+ "		left join cun.purpose as cp" + (purposeId != null ? " on cp.purposeId in (:purposeId)" : "")
				+ "		left join quotations.quotationRecords as qqr"
				+ "		left join qqr.quotation as qq "
				+ "		left join qq.unit as qun"
				+ "		left join qun.purpose as qp" + (purposeId != null ? " on qp.purposeId in (:purposeId)" : "")
				+ " where 1=1 ";
		
		if(teams != null){
			hql = hql + " and u.team in (:teams) ";
		}
		
		if(officerIds != null){
			hql = hql + " and u.userId in (:officerIds) ";
		}
		
		if(subOrdinates != null){
			hql = hql + " and u.userId in (:subOrdinates) ";
		}
		
		hql = hql + "	group by "+
				"		u.userId,"+
				"		u.team,"+
				"		r.name,"+
				"		u.englishName,"+
				"		u.staffCode"
				+ "	order by"
				+ "		u.team, u.staffCode";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("startReferenceMonth", startReferenceMonth);
		query.setParameter("endReferenceMonth", endReferenceMonth);
		if(purposeId != null){
			query.setParameterList("purposeId", purposeId);
		}
		if(teams != null){
			query.setParameterList("teams", teams);
		}
		if(officerIds != null){
			query.setParameterList("officerIds", officerIds);
		}
		if(subOrdinates != null){
			query.setParameterList("subOrdinates", subOrdinates);
		}
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfVerificationCasesReport.class));
		return query.list();
	}*/

	@SuppressWarnings("unchecked")
	public List<UserLookupTableList> getStaffCalendarLookupTableListTeamOnly(String search,
			int firstRecord, int displayLength, Order order,
			Integer authorityLevel, String username, Boolean withSelf, String ghs, String team) {
		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		
		String sql = " select u.UserId as id, u.StaffCode as staffCode, u.Team as team "
				+ ", u.ChineseName as chineseName, u.EnglishName as englishName, u.Destination as destination "
				+ ", u.isGHS as isGHS "
				+ ", district = STUFF(("
				+ " select char(10) + ', ' + d.Code "
				+ " from [District] as d where d.UserId = u.UserId "
				+ " FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') "
				+ " from [User] as u "
				+ " left join [User] as sup on u.SupervisorId = sup.UserId "
				+ " left join Acting as actBy on sup.UserId = actBy.UserId and actBy.startDate >= :date and actBy.endDate <= :date "
				+ " left join [User] as acting on actBy.ReplacementId = acting.UserId "
				+ " left join [User] as sub on sup.UserId = sub.SupervisorId "
				+ " left join UserRole as ur on u.UserId = ur.UserId "
				+ " left join Role as r on ur.RoleId = r.RoleId "
				+ " where u.Status <> :status ";
		
		if (withSelf != null && withSelf){
			sql += " and (sup.Username = :username or (acting.Username = :username and u.UserId = sub.UserId) or u.Username = :username) ";
		}
		else{
			sql += " and (sup.Username = :username or (acting.Username = :username and u.UserId = sub.UserId)) ";
		}
		if (authorityLevel != null)
			sql += " and (r.AuthorityLevel & :value) > 0 ";
		if (StringUtils.isNotEmpty(search)) {
			sql += " and (u.StaffCode like :search or u.Team like :search "
				+ " or u.ChineseName like :search or u.EnglishName like :search or u.Destination like :search"
				+ " or STUFF(( "
				+ " select char(10) + ', ' + d.Code "
				+ " from [District] as d where d.UserId = u.UserId "
				+ " FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') like :search "
				+ " ) ";
		}
		
		if(StringUtils.isNotEmpty(ghs)) {
			sql += " and u.isGHS = :ghs ";
		}
		
		if(StringUtils.isNotEmpty(team)) {
			sql += " and u.Team = :team ";
		}
		
		sql += "group by u.UserId, u.StaffCode, u.Team, u.ChineseName, u.EnglishName, u.Destination, u.isGHS ";
		
//		sql += " order by " + order.getPropertyName() + (order.isAscending() ? " asc " : " desc ");
		
		String finQuery = "WITH query AS ( "
				+ " select row_number() over (ORDER BY "+order.getPropertyName() + (order.isAscending() ? " asc " : " desc ")+") as row_num, inner_query.* from ("+sql+") as inner_query ) "
				+ " select id, staffCode, team, chineseName, englishName, destination, isGHS, district "
					+ " from query where row_num >= :firstRecord AND row_num < :lastRecord ";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(finQuery);
//		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("date", date);
		sqlQuery.setParameter("status", "Inactive");
		sqlQuery.setParameter("username", username);
		sqlQuery.setParameter("ghs", Boolean.valueOf(ghs));
		if (authorityLevel != null)
			sqlQuery.setParameter("value", values);
		if (StringUtils.isNotEmpty(search))
			sqlQuery.setParameter("search", String.format("%%%s%%", search));
		
		sqlQuery.setParameter("firstRecord", firstRecord + 1);
		sqlQuery.setParameter("lastRecord", firstRecord + displayLength + 1);
		
		sqlQuery.addScalar("id", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("staffCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("team", StandardBasicTypes.STRING);
		sqlQuery.addScalar("chineseName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("englishName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("destination", StandardBasicTypes.STRING);
		sqlQuery.addScalar("isGHS", StandardBasicTypes.BOOLEAN);
		
//		sqlQuery.setFirstResult(firstRecord);
//		sqlQuery.setMaxResults(displayLength);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
		
		return sqlQuery.list();
	}
	
	public long countStaffCalendarLookupTableListTeamOnly(String search,
			Integer authorityLevel, String username, Boolean withSelf, String ghs, String team) {

		Integer values = authorityLevel;
		
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		
		String sql = " select count(distinct u.UserId) "
				+ " from [User] as u "
				+ " left join [User] as sup on u.SupervisorId = sup.UserId "
				+ " left join Acting as actBy on sup.UserId = actBy.UserId and actBy.startDate >= :date and actBy.endDate <= :date "
				+ " left join [User] as acting on actBy.ReplacementId = acting.UserId "
				+ " left join [User] as sub on sup.UserId = sub.SupervisorId "
				+ " left join UserRole as ur on u.UserId = ur.UserId "
				+ " left join Role as r on ur.RoleId = r.RoleId "
				+ " where u.Status <> :status ";
		
		if (withSelf != null && withSelf){
			sql += " and (sup.Username = :username or (acting.Username = :username and u.UserId = sub.UserId) or u.Username = :username) ";
		}
		else{
			sql += " and (sup.Username = :username or (acting.Username = :username and u.UserId = sub.UserId)) ";
		}
		
		if (authorityLevel != null)
			sql += " and (r.AuthorityLevel & :value) > 0 ";
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and (u.StaffCode like :search or u.Team like :search "
				+ " or u.ChineseName like :search or u.EnglishName like :search or u.Destination like :search"
				+ " or STUFF(( "
				+ " select char(10) + ', ' + d.Code "
				+ " from [District] as d where d.UserId = u.UserId "
				+ " FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') like :search "
				+ " ) ";
		}
		
		if(StringUtils.isNotEmpty(ghs)) {
			sql += " and u.isGHS = :ghs ";
		}
		if(StringUtils.isNotEmpty(team)) {
			sql += " and u.Team = :team ";
		}
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("date", date);
		sqlQuery.setParameter("status", "Inactive");
		sqlQuery.setParameter("username", username);
		sqlQuery.setParameter("ghs", Boolean.valueOf(ghs));
		sqlQuery.setParameter("team", team);
		if (authorityLevel != null)
			sqlQuery.setParameter("value", values);
		if (StringUtils.isNotEmpty(search))
			sqlQuery.setParameter("search", String.format("%%%s%%", search));
		
		Integer count = (Integer)sqlQuery.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserLookupTableList> getStaffCalendarLookupTableList(String search,
			int firstRecord, int displayLength, Order order,
			Integer authorityLevel, String ghs, String team) {

//		Type type = IntegerType.INSTANCE;
//		Integer values = authorityLevel;
//		
//		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").add(Restrictions.ne("status", "Inactive"));
//		
//		if (authorityLevel != null)
//			criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type)); // for the sake of multiple authority level
//		
//		if (StringUtils.isNotEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("team", "%" + search + "%"),
//					Restrictions.like("englishName", "%" + search + "%"),
//					Restrictions.like("chineseName", "%" + search + "%"),
//					Restrictions.like("destination", "%" + search + "%"),
//					Restrictions.like("staffCode", "%" + search + "%")
//				));
//		}
//
//		if(StringUtils.isNotEmpty(ghs)) {
//			criteria.add(Restrictions.eq("u.isGHS", Boolean.valueOf(ghs)));
//		}
//		
//        ProjectionList projList = Projections.projectionList();
//        projList.add(Projections.groupProperty("id"), "id");
//        projList.add(Projections.groupProperty("staffCode"), "staffCode");
//        projList.add(Projections.groupProperty("team"), "team");
//        projList.add(Projections.groupProperty("chineseName"), "chineseName");
//        projList.add(Projections.groupProperty("englishName"), "englishName");
//        projList.add(Projections.groupProperty("destination"), "destination");
//        projList.add(Projections.groupProperty("isGHS"), "isGHS");
//		criteria.setProjection(projList);
//        
//		criteria.setFirstResult(firstRecord);
//		criteria.setMaxResults(displayLength);
//		criteria.addOrder(order);
//        
//		criteria.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
//        
//        return criteria.list();
        
        String sql = " select u.UserId as id, u.StaffCode as staffCode, u.Team as team, u.ChineseName as chineseName, "
					+ " u.EnglishName as englishName, u.Destination as destination, u.IsGHS as isGHS, " 
					+ " district = STUFF(( "
					+ " select char(10) + ', ' + d.Code "
					+ " from [District] as d where d.UserId = u.UserId "
					+ " FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') "
					+ " from [User] as u "
					+ " inner join UserRole as ur on u.UserId = ur.UserId " 
					+ " inner join [Role] as r on ur.RoleId = r.RoleId " 
					+ " where u.Status <> :status ";
        
        if (authorityLevel != null) {
        	sql +=  " and (r.AuthorityLevel & :authorityLevel) > 0 ";
        }
        if(ghs != null && StringUtils.isNotEmpty(ghs)) {
        	sql += " and u.IsGHS = :ghs ";
        }
        if(team != null && StringUtils.isNotEmpty(team)) {
        	sql += " and u.Team = :team ";
        }
        if (StringUtils.isNotEmpty(search)) {
			sql += " and (u.StaffCode like :search or u.Team like :search "
				+ " or u.ChineseName like :search or u.EnglishName like :search or u.Destination like :search"
				+ " or STUFF(( "
				+ " select char(10) + ', ' + d.Code "
				+ " from [District] as d where d.UserId = u.UserId "
				+ " FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') like :search"
				+ " ) ";
		}
        
		sql += " group by u.UserId, u.StaffCode, u.Team, u.ChineseName, u.EnglishName, " 
			+ " u.Destination, u.IsGHS ";
		
		//sql += " order by " + order.getPropertyName() + (order.isAscending() ? " asc " : " desc ");
		
		String finQuery = "WITH query AS ( "
				+ " select row_number() over (ORDER BY "+order.getPropertyName() + (order.isAscending() ? " asc " : " desc ")+") as row_num, inner_query.* from ("+sql+") as inner_query ) "
				+ " select id, staffCode, team, chineseName, englishName, destination, isGHS, district "
					+ " from query where row_num >= :firstRecord AND row_num < :lastRecord ";
		
		
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(finQuery);
		
		sqlQuery.setParameter("status", "Inactive");
		if(authorityLevel != null) {
			sqlQuery.setParameter("authorityLevel", authorityLevel);
		}
		if(ghs != null && StringUtils.isNotEmpty(ghs)) {
			sqlQuery.setParameter("ghs", Boolean.valueOf(ghs));
		}
		if(team != null && StringUtils.isNotEmpty(team)) {
			sqlQuery.setParameter("team", team);
		}
		if (StringUtils.isNotEmpty(search)) {
			sqlQuery.setParameter("search", String.format("%%%s%%", search));
		}
		
		sqlQuery.setParameter("firstRecord", firstRecord + 1);
		sqlQuery.setParameter("lastRecord", firstRecord + displayLength + 1);
		
		sqlQuery.addScalar("id", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("staffCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("team", StandardBasicTypes.STRING);
		sqlQuery.addScalar("chineseName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("englishName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("destination", StandardBasicTypes.STRING);
		sqlQuery.addScalar("isGHS", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("district", StandardBasicTypes.STRING);
		
//		sqlQuery.setFirstResult(firstRecord);
//		sqlQuery.setMaxResults(displayLength);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
		
		return sqlQuery.list();
	}
	
	public long countStaffCalendarLookupTableList(String search, Integer authorityLevel, String ghs, String team) {

//		Type type = IntegerType.INSTANCE;
//		Integer values = authorityLevel;
//		
//		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").add(Restrictions.ne("status", "Inactive"));
//		
//		if (authorityLevel != null)
//			criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
//		
//		if (StringUtils.isNotEmpty(search)) {
//			criteria.add(Restrictions.or(
//					Restrictions.like("team", "%" + search + "%"),
//					Restrictions.like("englishName", "%" + search + "%"),
//					Restrictions.like("chineseName", "%" + search + "%"),
//					Restrictions.like("destination", "%" + search + "%"),
//					Restrictions.like("staffCode", "%" + search + "%")
//				));
//		}
//		
//		if(StringUtils.isNotEmpty(ghs)) {
//			criteria.add(Restrictions.eq("u.isGHS", Boolean.valueOf(ghs)));
//		}
//		
//		SQLProjectionExt project = SQLProjectionExt.groupCount(
//        		Projections.groupProperty("id")
//		);
//
//		criteria.setProjection(project);
//
//		Long count = (Long)criteria.uniqueResult();
//		return count == null ? 0 : count;
		
		String sql = " select count(distinct u.UserId) "
					+ " from [User] as u "
					+ " inner join UserRole as ur on u.UserId = ur.UserId " 
					+ " inner join [Role] as r on ur.RoleId = r.RoleId " 
					+ " where u.Status <> :status ";
	    
	    if (authorityLevel != null) {
	    	sql +=  " and (r.AuthorityLevel & :authorityLevel) > 0 ";
	    }
	    if(ghs != null && StringUtils.isNotEmpty(ghs)) {
	    	sql += " and u.IsGHS = :ghs ";
	    }
	    if(team != null && StringUtils.isNotEmpty(team)) {
	    	sql += " and u.Team = :team ";
	    }
	    if (StringUtils.isNotEmpty(search)) {
			sql += " and (u.StaffCode like :search or u.Team like :search "
				+ " or u.ChineseName like :search or u.EnglishName like :search or u.Destination like :search"
				+ " or STUFF(( "
				+ " select char(10) + ', ' + d.Code "
				+ " from [District] as d where d.UserId = u.UserId "
				+ " FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') like :search"
				+ " ) ";
		}
	    
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("status", "Inactive");
		if(authorityLevel != null) {
			sqlQuery.setParameter("authorityLevel", authorityLevel);
		}
		if(ghs != null && StringUtils.isNotEmpty(ghs)) {
			sqlQuery.setParameter("ghs", Boolean.valueOf(ghs));
		}
		if(team != null && StringUtils.isNotEmpty(team)) {
			sqlQuery.setParameter("team", team);
		}
		if (StringUtils.isNotEmpty(search)) {
			sqlQuery.setParameter("search", String.format("%%%s%%", search));
		}
		
		Integer count = (Integer)sqlQuery.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getStaffCalendarLookupTableTeamOnlySelectAll(String search, Integer authorityLevel, String username, Boolean withSelf, 
							String ghs, String team) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Date date = DateUtils.truncate(new Date(), Calendar.DATE);
		
		Criteria criteria = this.createCriteria("u")
				.createAlias("u.supervisor", "sup", JoinType.LEFT_OUTER_JOIN)
				.createAlias("sup.actedBy", "actBy", JoinType.LEFT_OUTER_JOIN, Restrictions.and(
						Restrictions.ge("actBy.startDate", date),
						Restrictions.le("actBy.endDate", date)
				))
				.createAlias("actBy.replacement", "acting", JoinType.LEFT_OUTER_JOIN)
				.createAlias("sup.subordinates", "sub", JoinType.LEFT_OUTER_JOIN);
		
		if (withSelf != null && withSelf){
			criteria.add(Restrictions.or(
					Restrictions.eq("sup.username", username),
					Restrictions.and(
							Restrictions.eq("acting.username", username),
							Restrictions.eqProperty("u.userId", "sub.userId")
						),
					Restrictions.eq("u.username", username)
				));
		}
		else{
			criteria.add(Restrictions.or(
					Restrictions.eq("sup.username", username),
					Restrictions.and(
							Restrictions.eq("acting.username", username),
							Restrictions.eqProperty("u.userId", "sub.userId")
						)
				));
		}
				
				
		criteria.add(Restrictions.ne("u.status","Inactive"));
		if (authorityLevel != null)
			criteria.add(Restrictions.sqlRestriction("(u.authorityLevel & ?) > 0", values, type));
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.team", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("u.destination", "%" + search + "%"),
					Restrictions.like("u.staffCode", "%" + search + "%")
				));
		}
		
		if(StringUtils.isNotEmpty(ghs)) {
			criteria.add(Restrictions.eq("u.isGHS", Boolean.valueOf(ghs)));
		}
		if(StringUtils.isNotEmpty(team)) {
			criteria.add(Restrictions.eq("u.team", team));
		}
		
		criteria.setProjection(Projections.groupProperty("u.id"));

		return (List<Integer>)criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getStaffCalendarLookupTableSelectAll(String search, Integer authorityLevel, String ghs, String team) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").add(Restrictions.ne("status", "Inactive"));
		
		if (authorityLevel != null)
			criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		
		if(StringUtils.isNotEmpty(ghs)) {
			criteria.add(Restrictions.eq("u.isGHS", Boolean.valueOf(ghs)));
		}
		if(StringUtils.isNotEmpty(team)) {
			criteria.add(Restrictions.eq("u.team", team));
		}
		
		criteria.setProjection(Projections.groupProperty("id"));

		return (List<Integer>)criteria.list();
	}

	@SuppressWarnings("unchecked")
	public UserLookupTableList getUserByIdForStaffCalendar(Integer userId){
		
		String sql = " select distinct u.UserId as id, u.StaffCode as staffCode, u.EnglishName as englishName, u.ChineseName as chineseName, "
					+ " district = STUFF(( select char(10) + ', ' + d.Code "
					+ " from [District] as d where d.UserId = u.UserId FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') "  
					+ " from [User] as u "
					+ " left join UserRole as ur on u.UserId = ur.UserId "
					+ " left join [Role] as r on ur.RoleId = r.RoleId "
					+ " where u.UserId = :userId ";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("userId", userId);
		
		sqlQuery.addScalar("id", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("staffCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("englishName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("chineseName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("district", StandardBasicTypes.STRING);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
		
		return (UserLookupTableList)sqlQuery.uniqueResult();
	}

	public List<UserLookupTableList> getActiveUsersWithAuthorityLevelForStaffCalendar(int authorityLevel, Integer[] staffIds){
		return getActiveUsersWithAuthorityLevelForStaffCalendar(authorityLevel, staffIds, null);
	}

	@SuppressWarnings("unchecked")
	public List<UserLookupTableList> getActiveUsersWithAuthorityLevelForStaffCalendar(int authorityLevel, Integer[] staffIds, Integer limit){
		
		String sql = " select distinct u.UserId as id, u.StaffCode as staffCode, u.EnglishName as englishName, u.ChineseName as chineseName, "
					+ " district = STUFF(( select char(10) + ', ' + d.Code "
					+ " from [District] as d where d.UserId = u.UserId FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') "  
					+ " from [User] as u "
					+ " left join UserRole as ur on u.UserId = ur.UserId "
					+ " left join [Role] as r on ur.RoleId = r.RoleId "
					+ " where (r.AuthorityLevel & :authorityLevel = :authorityLevel) and u.Status <> :status ";
		
		if(staffIds != null){
			sql += " and u.UserId in (:staffIds)";
		}
		
		if (limit != null) {
			sql += "";
		}
		
		sql += " order by district asc ";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("authorityLevel", authorityLevel);
		sqlQuery.setParameter("status", "Inactive");
		
		if(staffIds != null){
			sqlQuery.setParameterList("staffIds", staffIds);
		}
		
		sqlQuery.addScalar("id", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("staffCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("englishName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("chineseName", StandardBasicTypes.STRING);
		sqlQuery.addScalar("district", StandardBasicTypes.STRING);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
		
		return sqlQuery.list();
	}
	
	public List<Integer> getTeammateUserIds(String team) {
		Criteria criteria = this.createCriteria();
		
		criteria.setProjection(Projections.property("userId"));
		if(!StringUtils.isEmpty(team)) {
			criteria.add(Restrictions.eq("team", team));
		} else {
			criteria.add(Restrictions.isNull("team"));
		}
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<User> findUserByRankCode(String rankCode){
		return this.createCriteria("u")
				.createAlias("u.rank", "r", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.eq("r.code", rankCode))
				.add(Restrictions.ne("status", "Inactive"))
				.list();
	}
	
	public User getActiveSCSOUserForReport(){
		return (User)this.createCriteria()
				.add(Restrictions.eq("rank.rankId", 8))
				.add(Restrictions.ne("status", "Inactive"))
				.setFetchMode("rank", FetchMode.JOIN)
				.addOrder(Order.asc("userId"))
				.uniqueResult();
	}
	
	public List<Integer> getTeammateUserIdsByTeams(List<String> teams) {
		Criteria criteria = this.createCriteria();
		
		criteria.setProjection(Projections.property("userId"));
		if( teams != null && teams.size() > 0 ) {
			criteria.add(Restrictions.in("team", teams));
		} else {
			criteria.add(Restrictions.isNull("team"));
		}
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getAllTeamForCheckerReport() {
		
		String sql = "select distinct team from [User] "
				   	+ "where team is not null and team <> '0' and team <> 'NA' "
				   	+ "and status <> 'Inactive' ";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		sqlQuery.addScalar("team", StandardBasicTypes.STRING);

		return sqlQuery.list();
	}
	
	public List<SummaryOfNightSessionReport> getUserModelByNightSessionReport(List<Integer> ids){
		String hql = "Select u.team as team "
				+ " , r.code as rank "
				+ " , case when r.code = 'CSO' then 1 "
					+ " when r.code = 'ACSO' then 2 "
					+ " when r.code = 'SI' then 3 "
					+ " else 4 end as rankSequence "
				+ " , u.staffCode as staffCode "
				+ " , u.englishName as staffName "
				+ " , u.userId as userId "
				+ " From User as u "
				+ " left join u.rank as r "
				+ " where u.userId in :userIds "
				+ " order by team, rankSequence, staffCode ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("userIds", ids);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfNightSessionReport.class));
		return query.list();
		
	}
	
	/*
	 * This method rotate startDate and endDate
	 */
	@SuppressWarnings("unchecked")
	public List<UserLookupTableList> getReportLookupTableList(String search,
			int firstRecord, int displayLength, Order order,
			Integer authorityLevel, String team, Integer[] excludedIds,
			Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		if (excludedIds != null && excludedIds.length > 0){
			criteria.add(Restrictions.not(Restrictions.in("id", excludedIds)));
		}

		if (StringUtils.isNotEmpty(team)){
			criteria.add(Restrictions.eq("team", team));
		}

		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", startDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", endDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "id");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("team"), "team");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
        projList.add(Projections.groupProperty("destination"), "destination");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		criteria.addOrder(order);
        
		criteria.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
        
        return criteria.list();
	}
	
	/*
	 * Called by 
	 * MB9032
	 */
	@SuppressWarnings("unchecked")
	public List<UserLookupTableList> getReportLookupTableList2(String search,
			int firstRecord, int displayLength, Order order,
			Integer authorityLevel, String team, Integer[] excludedIds,
			Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds, Integer staffType) {
		
		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		if (excludedIds != null && excludedIds.length > 0){
			criteria.add(Restrictions.not(Restrictions.in("id", excludedIds)));
		}

		if (StringUtils.isNotEmpty(team)){
			criteria.add(Restrictions.eq("team", team));
		}

		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", startDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", endDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "id");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("team"), "team");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
        projList.add(Projections.groupProperty("destination"), "destination");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		criteria.addOrder(order);
        
		criteria.setResultTransformer(Transformers.aliasToBean(UserLookupTableList.class));
        
        return criteria.list();
	}
	
	public long countReportLookupTableList(String search, Integer authorityLevel, String team, Integer[] excludedIds,
			Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		
		if (excludedIds != null && excludedIds.length > 0){
			criteria.add(Restrictions.not(Restrictions.in("id", excludedIds)));
		}
		
		if (StringUtils.isNotEmpty(team)){
			criteria.add(Restrictions.eq("team", team));
		}

		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", startDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", endDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	/*
	 * Called by 
	 * MB9032
	 */
	public long countReportLookupTableList2(String search, Integer authorityLevel, String team, Integer[] excludedIds,
			Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		
		if (excludedIds != null && excludedIds.length > 0){
			criteria.add(Restrictions.not(Restrictions.in("id", excludedIds)));
		}
		
		if (StringUtils.isNotEmpty(team)){
			criteria.add(Restrictions.eq("team", team));
		}

		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", startDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", endDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	/*
	 * This method rotate startDate and endDate
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getReportLookupTableSelectAll(String search, Integer authorityLevel, String team, Integer[] excludedIds,
			Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		
		if (excludedIds != null && excludedIds.length > 0){
			criteria.add(Restrictions.not(Restrictions.in("id", excludedIds)));
		}
		
		if (StringUtils.isNotEmpty(team)){
			criteria.add(Restrictions.eq("team", team));
		}

		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", startDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", endDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		criteria.setProjection(Projections.groupProperty("id"));

		return (List<Integer>)criteria.list();
	}
	
	/*
	 * Called by
	 * MB9032
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getReportLookupTableSelectAll2(String search, Integer authorityLevel, String team, Integer[] excludedIds,
			Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("team", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("destination", "%" + search + "%"),
					Restrictions.like("staffCode", "%" + search + "%")
				));
		}
		
		if (excludedIds != null && excludedIds.length > 0){
			criteria.add(Restrictions.not(Restrictions.in("id", excludedIds)));
		}
		
		if (StringUtils.isNotEmpty(team)){
			criteria.add(Restrictions.eq("team", team));
		}

		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", startDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", endDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		criteria.setProjection(Projections.groupProperty("id"));

		return (List<Integer>)criteria.list();
	}
	
	/*
	 * This method rotates startDate and endDate
	 */
	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchReportOfficer(String search,
			int firstRecord, int displayLength, Integer authorityLevel
			, Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds
			, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", startDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", endDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "userId");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
    
		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
        
        return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<StaffProfileTableList> searchReportOfficer2(String search,
			int firstRecord, int displayLength, Integer authorityLevel
			, Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds
			, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", startDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", endDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "userId");
        projList.add(Projections.groupProperty("staffCode"), "staffCode");
        projList.add(Projections.groupProperty("chineseName"), "chineseName");
        projList.add(Projections.groupProperty("englishName"), "englishName");
		criteria.setProjection(projList);
        
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
    
		criteria.setResultTransformer(Transformers.aliasToBean(StaffProfileTableList.class));
        
        return criteria.list();
	}
	
	/*
	 * This method rotate startDate and endDate
	 */
	public long countSearchReportOfficer(String search, Integer authorityLevel
			, Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds
			, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfEntry", startDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfLeaving", endDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public long countSearchReportOfficer2(String search, Integer authorityLevel
			, Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds
			, Integer staffType) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", startDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", endDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public long countSearchReportOfficerExcludeRank(String search, Integer authorityLevel
			, Date startDate, Date endDate, Date refMonthStart, Date refMonthEnd, Integer[] userIds
			, Integer staffType, String[] rankCodes) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r").createAlias("u.rank", "rk");
		
		if (startDate == null && endDate == null && refMonthStart == null && refMonthEnd == null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.length > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("u.staffCode", "%" + search + "%"),
					Restrictions.like("u.englishName", "%" + search + "%"),
					Restrictions.like("u.chineseName", "%" + search + "%")
				));
		}
		
		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", startDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", endDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthStart != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonthStart),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonthEnd != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", refMonthEnd),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		criteria.add(Restrictions.not(Restrictions.in("rk.code", rankCodes)));
		
		SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("id")
		);

		criteria.setProjection(project);

		Long count = (Long)criteria.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> searchReportOfficerId(Integer authorityLevel
			, Date startDate, Date endDate, Date refMonth) {

		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (startDate != null && endDate != null && refMonth != null){
			criteria.add(Restrictions.ne("status", "Inactive"));
		}
		
		if (authorityLevel != null) {
			criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
		}
		
		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", startDate),
					Restrictions.isNull("dateOfEntry")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", endDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (refMonth != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", refMonth),
					Restrictions.isNull("dateOfLeaving")
					));
		}

        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.groupProperty("id"), "userId");
		criteria.setProjection(projList);
		
        return criteria.list();
	}

	public List<SummaryOfVerificationCasesReport> getSummaryOfVerificationCasesReport1(List<Integer> purposeId, List<String> teamId, List<Integer> officerId, 
			Date fromdate, Date toDate){
		String referenceMonth = String.format("FORMAT(ih.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		DateFormat df = new SimpleDateFormat("yyyyMM");
		Date newEndDate;
		String date = df.format(toDate);
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		switch (month)
		{
		case 1: case 3: case 5:
        case 7: case 8: case 10:
        case 12:
        	cal.set(Calendar.DATE, 31);
        	break;
        case 4: case 6:
        case 9: case 11:
        	cal.set(Calendar.DATE, 30);
        	break;
        case 2:
        	if (((year % 4 == 0) && 
                    !(year % 100 == 0))
                    || (year % 400 == 0))
        		cal.set(Calendar.DATE, 29);
        	else
        		cal.set(Calendar.DATE, 28);
        	break;
        	default:
        		break;
		}
		newEndDate = DateUtils.truncate(cal.getTime(), Calendar.DATE);
		String sql = "SELECT referenceMonth, code, team, name, staffCode, engName,"
				+ " SUM(firmVerificationCasesAssignment1) AS firmVerificationCasesAssignment1, SUM(firmVerificationCasesQR1) AS firmVerificationCasesQR1" 
                + ", SUM(firmVerificationCasesQR2) AS firmVerificationCasesQR2, SUM(firmVerificationCasesAssignment2) AS firmVerificationCasesAssignment2"
                + ", SUM(firmVerificationCasesQR3) AS firmVerificationCasesQR3 FROM ( "
				+ "SELECT "
				/*+ " CASE WHEN ih.referenceMonth IS NOT NULL THEN " + referenceMonth + " ELSE '' END AS [referenceMonth] "
				+ " , pp.Code AS [code] "
				+ " , ur.Team AS [team] "
				+ " , r.Code AS [name] "
				+ " , ur.StaffCode AS [staffCode] "
				+ " , ur.EnglishName AS [engName] "
				+ " , count(distinct a.AssignmentId) AS [firmVerificationCasesAssignment1] "
				+ " , count(distinct qr.QuotationRecordId) AS [firmVerificationCasesQR1] "
				+ " , count(distinct a1.AssignmentId) AS [firmVerificationCasesQR2] "
				+ " , count(distinct qr1.QuotationRecordId) AS [firmVerificationCasesAssignment2] "
				+ " , count(distinct qr2.QuotationRecordId) AS [firmVerificationCasesQR3] "				
				+ " FROM [IndoorVerificationHistory] AS ih "
				+ " LEFT JOIN VerficiationQuotationRecordHistory AS vh ON ih.IndoorVerificationHistoryId = vh.IndoorVerificationHistoryId"
				+ " LEFT JOIN QuotationRecord AS qr ON vh.QuotationRecordId = qr.QuotationRecordId AND ih.VerifyType = 1"
				+ " LEFT JOIN QuotationRecord AS qr1 ON vh.QuotationRecordId = qr1.QuotationRecordId AND ih.VerifyType = 2 "
				+ " LEFT JOIN QuotationRecord AS qr2 ON vh.QuotationRecordId = qr2.QuotationRecordId AND ih.VerifyType = 3 "
				+ " LEFT JOIN Assignment AS a ON qr.AssignmentId = a.AssignmentId AND ih.VerifyType = 1 "
				+ " LEFT JOIN Assignment AS a1 ON qr1.AssignmentId = a.AssignmentId AND ih.VerifyType = 2 "
				+ " LEFT JOIN [User] AS ur ON qr.UserId = ur.UserId "
				+ " LEFT JOIN [Rank] AS r ON ur.RankId = r.RankId "
				+ " LEFT JOIN Quotation AS q ON q.QuotationId = qr.QuotationId "
				+ " LEFT JOIN Unit AS u ON u.UnitId = q.UnitId "
				+ " LEFT JOIN Purpose AS pp ON pp.PurposeId = u.PurposeId" */
				+ " CASE WHEN ih.referenceMonth IS NOT NULL THEN " + referenceMonth + " ELSE '' END AS [referenceMonth] "
				+ " , pp.Code AS [code] "
				+ " , ur.Team AS [team] "
				+ " , r.Code AS [name] "
				+ " , ur.StaffCode AS [staffCode] "
				+ " , ur.EnglishName AS [engName] "
				+ " , case when qr.UserId = ur.userId then count(distinct a.AssignmentId) else 0 end AS [firmVerificationCasesAssignment1] "
				+ " , case when qr.UserId = ur.userId then count(distinct qr1.QuotationRecordId) else 0 end AS [firmVerificationCasesQR1] "
				+ " , case when qr.UserId = ur.userId then count(distinct a1.AssignmentId) else 0 end AS [firmVerificationCasesQR2] "
				+ " , case when qr.UserId = ur.userId then count(distinct qr2.QuotationRecordId) else 0 end AS [firmVerificationCasesAssignment2] "
				+ " , case when qr.UserId = ur.userId then count(distinct qr3.QuotationRecordId) else 0 end AS [firmVerificationCasesQR3] "	
				+ " FROM [IndoorVerificationHistory] AS ih "
				+ " LEFT JOIN VerficiationQuotationRecordHistory AS vh ON ih.IndoorVerificationHistoryId = vh.IndoorVerificationHistoryId"
				+ " LEFT JOIN QuotationRecord AS qr ON vh.QuotationRecordId = qr.QuotationRecordId "
				+ " LEFT JOIN QuotationRecord AS qr1 ON vh.QuotationRecordId = qr1.QuotationRecordId AND ih.VerifyType = 1 "
				+ " LEFT JOIN QuotationRecord AS qr2 ON vh.QuotationRecordId = qr2.QuotationRecordId AND ih.VerifyType = 2 "
				+ " LEFT JOIN QuotationRecord AS qr3 ON vh.QuotationRecordId = qr3.QuotationRecordId AND ih.VerifyType = 3 "
				+ " LEFT JOIN Assignment AS a ON qr1.AssignmentId = a.AssignmentId AND ih.VerifyType = 1 "
				+ " LEFT JOIN Assignment AS a1 ON qr2.AssignmentId = a1.AssignmentId AND ih.VerifyType = 2 "
				/*+ " LEFT JOIN QuotationRecord AS qr ON vh.QuotationRecordId = qr.QuotationRecordId AND ih.VerifyType = 1"
				+ " LEFT JOIN QuotationRecord AS qr1 ON vh.QuotationRecordId = qr1.QuotationRecordId AND ih.VerifyType = 2 "
				+ " LEFT JOIN QuotationRecord AS qr2 ON vh.QuotationRecordId = qr2.QuotationRecordId AND ih.VerifyType = 3 "
				+ " LEFT JOIN Assignment AS a ON qr.AssignmentId = a.AssignmentId AND ih.VerifyType = 1 "
				+ " LEFT JOIN Assignment AS a1 ON qr1.AssignmentId = a.AssignmentId AND ih.VerifyType = 2 "*/
				+ " LEFT JOIN [User] AS ur ON ur.DateOfEntry <= :newEndDate and (ur.DateOfLeaving >= :fromdate or ur.DateOfLeaving is null) "
				+ " LEFT JOIN [Rank] AS r ON ur.RankId = r.RankId "
				+ " LEFT JOIN Quotation AS q ON q.QuotationId = qr.QuotationId "
				+ " LEFT JOIN Unit AS u ON u.UnitId = q.UnitId "
				+ " LEFT JOIN Purpose AS pp ON pp.PurposeId = u.PurposeId"	
				
				+ " WHERE "
				+ " 1 = 1 "
				+ "	AND ih.ReferenceMonth between :fromdate and :toDate AND qr.IsBackNo = 0 "
				+ " AND ur.EnglishName NOT LIKE '%admin%' AND ur.EnglishName NOT LIKE '%kinetix%' "
				+ " AND ur.RankId IN (10, 11)";
				if (purposeId != null && purposeId.size() > 0){
					sql += " AND  pp.purposeId IN (:purposeId) ";
				}
				
				if (teamId != null && teamId.size() > 0){
					sql += " AND  ur.Team IN (:teamId) ";
				}
				
				if (officerId != null && officerId.size() > 0){
					sql += " AND  ur.UserId IN (:officerId) ";
				}

				sql += " GROUP BY ih.[ReferenceMonth], pp.Code, ur.Team, r.Code "
						+ " , ur.StaffCode, ur.EnglishName , ih.VerifyType, ur.UserId, qr.UserId ";
				
				sql += " ) AS result ";
				
				sql += " GROUP BY result.referenceMonth, result.code, result.team, result.name, result.staffCode, result.engName ";
				
				sql += " ORDER BY result.[referenceMonth] DESC, result.[team], result.[name], result.[staffCode]";
				
				SQLQuery query = this.getSession().createSQLQuery(sql);

				query.setParameter("fromdate", fromdate);
				query.setParameter("toDate", toDate);
				query.setParameter("newEndDate", newEndDate);
				
				if (purposeId != null && purposeId.size() > 0){
					query.setParameterList("purposeId", purposeId);
				}

				if (teamId != null && teamId.size() > 0){
					query.setParameterList("teamId", teamId);
				}
				
				if (officerId != null && officerId.size() > 0){
					query.setParameterList("officerId", officerId);
				}
				
				query.addScalar("referenceMonth", StandardBasicTypes.STRING);
				query.addScalar("code", StandardBasicTypes.STRING);	
				query.addScalar("team", StandardBasicTypes.STRING);	
				query.addScalar("name", StandardBasicTypes.STRING);	
				query.addScalar("staffCode", StandardBasicTypes.STRING);	
				query.addScalar("engName", StandardBasicTypes.STRING);	
				query.addScalar("firmVerificationCasesAssignment1", StandardBasicTypes.INTEGER);	
				query.addScalar("firmVerificationCasesQR1", StandardBasicTypes.INTEGER);	
				query.addScalar("firmVerificationCasesQR2", StandardBasicTypes.INTEGER);	
				query.addScalar("firmVerificationCasesAssignment2", StandardBasicTypes.INTEGER);	
				query.addScalar("firmVerificationCasesQR3", StandardBasicTypes.INTEGER);
				query.setResultTransformer(Transformers.aliasToBean(SummaryOfVerificationCasesReport.class));

				return query.list();
	}
	
	public List<SummaryOfVerificationCasesReport> getSummaryOfVerificationCasesReport2(List<Integer> purposeId, List<String> teamId, List<Integer> officerId, 
			Date fromdate, Date toDate){
		
		String referenceMonth = String.format("FORMAT(ih.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		
		String sql = "SELECT referenceMonth, code, team, SUM(firmVerificationCasesAssignment1) AS firmVerificationCasesAssignment1,"
				+ " SUM(firmVerificationCasesQR1) AS firmVerificationCasesQR1, SUM(firmVerificationCasesQR2) AS firmVerificationCasesQR2,"
				+ " SUM(firmVerificationCasesAssignment2) AS firmVerificationCasesAssignment2, "
				+ " SUM(firmVerificationCasesQR3) AS firmVerificationCasesQR3 FROM (  "
				+ " SELECT "
				+ " CASE WHEN ih.referenceMonth IS NOT NULL THEN " + referenceMonth + " ELSE '' END AS [referenceMonth] "
				+ " , pp.Code AS [code] "
				+ " , tu.Team AS [team] "
				+ " , 0 AS [firmVerificationCasesAssignment1] "
				+ " , 0 AS [firmVerificationCasesQR1] "
				+ " , 0 AS [firmVerificationCasesQR2] "
				+ " , 0 AS [firmVerificationCasesAssignment2] "
				+ " , 0 AS [firmVerificationCasesQR3] "				
				+ " FROM (SELECT DISTINCT Team FROM [User] where team not in ('NA')) AS tu, [IndoorVerificationHistory] AS ih "
				+ " LEFT JOIN VerficiationQuotationRecordHistory AS vh ON ih.IndoorVerificationHistoryId = vh.IndoorVerificationHistoryId"
				+ " LEFT JOIN QuotationRecord AS qr ON vh.QuotationRecordId = qr.QuotationRecordId "
				+ " LEFT JOIN [User] AS ur ON qr.UserId = ur.UserId "
				+ " LEFT JOIN [Rank] AS r ON ur.RankId = r.RankId "
				+ " LEFT JOIN Quotation AS q ON q.QuotationId = qr.QuotationId "
				+ " LEFT JOIN Unit AS u ON u.UnitId = q.UnitId "
				+ " LEFT JOIN Purpose AS pp ON pp.PurposeId = u.PurposeId"	
		
		        + " WHERE "
		        + " 1 = 1 "
		        + "	AND ih.ReferenceMonth between :fromdate and :toDate and qr.IsBackNo = 0  AND ur.RankId IN (10, 11)";

		
		if (purposeId != null && purposeId.size() > 0){
			sql += " AND  pp.purposeId IN (:purposeId) ";
		}
		
		if (teamId != null && teamId.size() > 0){
			sql += " AND  ur.Team IN (:teamId) ";
		}
		
		if (officerId != null && officerId.size() > 0){
			sql += " AND  ur.UserId IN (:officerId) ";
		}

		sql += " GROUP BY ih.[ReferenceMonth], pp.Code, tu.Team, ur.Team";
		
			sql+= " UNION "
				+ "SELECT "
				+ " CASE WHEN ih.referenceMonth IS NOT NULL THEN " + referenceMonth + " ELSE '' END AS [referenceMonth] "
				+ " , pp.Code AS [code] "
				+ " , ur.Team AS [team] "
				//+ " , r.Name AS [name] "
				//+ " , r.Code AS [name] "
				//+ " , ur.StaffCode AS [staffCode] "
				//+ " , ur.EnglishName AS [engName] "
				+ " , count(distinct a.AssignmentId) AS [firmVerificationCasesAssignment1] "
				+ " , count(distinct qr1.QuotationRecordId) AS [firmVerificationCasesQR1] "
				+ " , count(distinct a1.AssignmentId) AS [firmVerificationCasesQR2] "
				+ " , count(distinct qr2.QuotationRecordId) AS [firmVerificationCasesAssignment2] "
				+ " , count(distinct qr3.QuotationRecordId) AS [firmVerificationCasesQR3] "				
				+ " FROM [IndoorVerificationHistory] AS ih "
				+ " LEFT JOIN VerficiationQuotationRecordHistory AS vh ON ih.IndoorVerificationHistoryId = vh.IndoorVerificationHistoryId"
				+ " LEFT JOIN QuotationRecord AS qr ON vh.QuotationRecordId = qr.QuotationRecordId "
				+ " LEFT JOIN QuotationRecord AS qr1 ON vh.QuotationRecordId = qr1.QuotationRecordId AND ih.VerifyType = 1 "
				+ " LEFT JOIN QuotationRecord AS qr2 ON vh.QuotationRecordId = qr2.QuotationRecordId AND ih.VerifyType = 2 "
				+ " LEFT JOIN QuotationRecord AS qr3 ON vh.QuotationRecordId = qr3.QuotationRecordId AND ih.VerifyType = 3 "
				+ " LEFT JOIN Assignment AS a ON qr1.AssignmentId = a.AssignmentId AND ih.VerifyType = 1 "
				+ " LEFT JOIN Assignment AS a1 ON qr2.AssignmentId = a1.AssignmentId AND ih.VerifyType = 2 "
				+ " LEFT JOIN [User] AS ur ON qr.UserId = ur.UserId "
				+ " LEFT JOIN [Rank] AS r ON ur.RankId = r.RankId "
				+ " LEFT JOIN Quotation AS q ON q.QuotationId = qr.QuotationId "
				+ " LEFT JOIN Unit AS u ON u.UnitId = q.UnitId "
				+ " LEFT JOIN Purpose AS pp ON pp.PurposeId = u.PurposeId"	
		
		        + " WHERE "
		        + " 1 = 1 "
		        + "	AND ih.ReferenceMonth between :fromdate and :toDate and qr.IsBackNo = 0  AND ur.RankId IN (10, 11)";

		
		if (purposeId != null && purposeId.size() > 0){
			sql += " AND  pp.purposeId IN (:purposeId) ";
		}
		
		if (teamId != null && teamId.size() > 0){
			sql += " AND  ur.Team IN (:teamId) ";
		}
		
		if (officerId != null && officerId.size() > 0){
			sql += " AND  ur.UserId IN (:officerId) ";
		}

		sql += " GROUP BY ih.[ReferenceMonth], pp.Code, ur.Team";
				//+ " , ih.VerifyType";
		
		sql += " ) result "
				+ "GROUP BY result.referenceMonth, result.code, result.team ";
		
		sql += " ORDER BY result.referenceMonth DESC, result.team";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);

		query.setParameter("fromdate", fromdate);
		query.setParameter("toDate", toDate);
		
		if (purposeId != null && purposeId.size() > 0){
			query.setParameterList("purposeId", purposeId);
		}

		if (teamId != null && teamId.size() > 0){
			query.setParameterList("teamId", teamId);
		}
		
		if (officerId != null && officerId.size() > 0){
			query.setParameterList("officerId", officerId);
		}
		
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("code", StandardBasicTypes.STRING);	
		query.addScalar("team", StandardBasicTypes.STRING);	
		//query.addScalar("name", StandardBasicTypes.STRING);	
		//query.addScalar("staffCode", StandardBasicTypes.STRING);	
		//query.addScalar("engName", StandardBasicTypes.STRING);	
		query.addScalar("firmVerificationCasesAssignment1", StandardBasicTypes.INTEGER);	
		query.addScalar("firmVerificationCasesQR1", StandardBasicTypes.INTEGER);	
		query.addScalar("firmVerificationCasesQR2", StandardBasicTypes.INTEGER);	
		query.addScalar("firmVerificationCasesAssignment2", StandardBasicTypes.INTEGER);	
		query.addScalar("firmVerificationCasesQR3", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfVerificationCasesReport.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<User> searchOfficerWithoutIndoor(String search, Integer firstRecord, Integer displayLength, String[] teams, List<Integer> userIds, Integer authorityLevel, Integer staffType) {
		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (StringUtils.isNotBlank(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("u.staffCode", "%" + search + "%")
				)
			);
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.size() > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (userIds != null && userIds.size() > 0){

			criteria.add(Restrictions.in("u.userId", userIds));
		}
		
		if (teams != null && teams.length > 0){

			criteria.add(Restrictions.in("u.team", teams));
		}
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<User> searchOfficerWithoutIndoor(String search, Integer firstRecord, Integer displayLength,
			String[] teams, List<Integer> userIds, Integer authorityLevel, Integer staffType, Date startDate, Date endDate) {
		Type type = IntegerType.INSTANCE;
		Integer values = authorityLevel;
		
		Criteria criteria = this.createCriteria("u").createAlias("u.roles", "r");
		
		if (StringUtils.isNotBlank(search)) {
			criteria.add(
				Restrictions.or(
					Restrictions.like("u.chineseName", "%" + search + "%"),
					Restrictions.like("u.staffCode", "%" + search + "%")
				)
			);
		}
		
		if (staffType != null){
			criteria.add(Restrictions.eq("staffType", staffType));
		}
		
		if (authorityLevel != null){
			if (userIds !=null && userIds.size() > 0){
				criteria.add(Restrictions.or(
						Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type),
						Restrictions.in("userId", userIds)
						)); // for the sake of multiple authority level
			} else {
				criteria.add(Restrictions.sqlRestriction("(authorityLevel & ?) > 0", values, type));
			}
		}
		
		if (userIds != null && userIds.size() > 0){

			criteria.add(Restrictions.in("u.userId", userIds));
		}
		
		if (teams != null && teams.length > 0){

			criteria.add(Restrictions.in("u.team", teams));
		}
		
		if (startDate != null){
			criteria.add(Restrictions.or(
					Restrictions.ge("dateOfLeaving", startDate),
					Restrictions.isNull("dateOfLeaving")
					));
		}
		
		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.le("dateOfEntry", endDate),
					Restrictions.isNull("dateOfEntry")
					));
		
		
	}
	criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		
		return criteria.list();

}
}
