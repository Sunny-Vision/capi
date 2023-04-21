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
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.District;
import capi.model.api.dataSync.DistrictSyncData;
import capi.model.assignmentAllocationAndReallocation.DistrictManDayRequiredModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.districtHeadTab.DistrictModel;
import capi.model.masterMaintenance.DistrictEditModel;
import capi.model.masterMaintenance.DistrictTableList;
import capi.model.report.RUACaseReportIndividualReport;
import capi.model.report.RUACaseReportOverviewReport;


@Repository("DistrictDao")
public class DistrictDao  extends GenericDao<District>{
	
	@SuppressWarnings("unchecked")
	public List<District> searchDistrict(String search, int firstRecord, int displayLength) {
		Criteria criteria = this.createCriteria().setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("code")).addOrder(Order.asc("englishName"));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("{alias}.code+' - '+englishName LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
				);
		}
		
		return criteria.list();
	}

	public long countSearchDistrict(String search) {
		Criteria criteria = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.sqlRestriction("{alias}.code+' - '+englishName LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	public List<DistrictTableList> selectAllDistrict(String search, int firstRecord, int displayLength, Order order) {

		Criteria criteria = this.createCriteria("d")
										.createAlias("d.tpus", "t", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.groupProperty("d.districtId"), "districtId");
		projList.add(Projections.groupProperty("d.code"), "code");
		projList.add(Projections.groupProperty("d.chineseName"), "chineseName");
		projList.add(Projections.groupProperty("d.englishName"), "englishName");
		projList.add(Projections.groupProperty("d.coverage"), "coverage");
		projList.add(Projections.count("t.tpuId"), "tpus");
		
		criteria.setProjection(projList);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("code", "%" + search + "%"),
					Restrictions.like("chineseName", "%" + search + "%"),
					Restrictions.like("englishName", "%" + search + "%")
			));
		}

		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
        criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(DistrictTableList.class));

		return criteria.list();
	}

	public long countSelectAllDistrict(String search) {

		Criteria criteria = this.createCriteria("d")
										.createAlias("d.tpus", "t", JoinType.LEFT_OUTER_JOIN);
		
		

		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("d.districtId"),
				Projections.groupProperty("d.code"),
				Projections.groupProperty("d.chineseName"),
				Projections.groupProperty("d.englishName")
		));
		
		
//		projList.add(Projections.groupProperty("d.districtId"), "districtId");
//		projList.add(Projections.groupProperty("d.code"), "code");
//		projList.add(Projections.groupProperty("d.chineseName"), "chineseName");
//		projList.add(Projections.groupProperty("d.englishName"), "englishName");
//		projList.add(Projections.count("t.tpuId"), "tpus");		
//		
		
		criteria.setProjection(projList);
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
				Restrictions.like("code", "%" + search + "%"),
				Restrictions.like("chineseName", "%" + search + "%"),
				Restrictions.like("englishName", "%" + search + "%")
			));
			//criteria.add(Restrictions.sqlRestriction("having count(t.tpuId) like '%" + search + "%'"));
		}
		
		
//		ScrollableResults result = criteria.scroll();
//		result.last();
//		int total = result.getRowNumber() + 1;
//		result.close();
//		
//		return total;
		return (long)criteria.uniqueResult();
		
	}

	public List<DistrictEditModel> getAllDistrictCode() {
		Criteria criteria = this.createCriteria("d");
		
		criteria.addOrder(Order.asc("d.code"));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("d.code"), "code");
		
		criteria.setProjection(projList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(DistrictEditModel.class));
		
		return criteria.list();
	}

	public List<Integer> getTpuIdByDistrictId(Integer id) {

		Criteria criteria = this.createCriteria("d")
										.createAlias("d.tpus", "t", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("t.tpuId"), "tpuId");
		
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("d.districtId", id));
		
		return (List<Integer>) criteria.list();
	}

	public List<District> getByIds(Integer[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("id", ids)).list();
	}

	public List<District> getDistrictsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("districtId", ids));
		
		return criteria.list();
	}
	
	public District getDistrictByCode(String districtCode){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("code", districtCode));
		return (District)criteria.uniqueResult();
	}

	public List<DistrictEditModel> getAllDistrict() {

		Criteria criteria = this.createCriteria();
		
		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.groupProperty("districtId"), "districtId");
		projList.add(Projections.groupProperty("code"), "code");
		projList.add(Projections.groupProperty("chineseName"), "chineseName");
		projList.add(Projections.groupProperty("englishName"), "englishName");

		criteria.setProjection(projList);

		criteria.setResultTransformer(Transformers.aliasToBean(DistrictEditModel.class));
		
		return criteria.list();
	}

	public ScrollableResults getAllDistrictResult(){
		Criteria criteria = this.createCriteria("d").setFetchMode("tpus", FetchMode.JOIN);
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<District> getNotExistedDistrict(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("districtId", ids)));
		return criteria.list();		
	}
	
	public List<Integer> getExistingDistrictId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<District> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("districtId", ids));
		return criteria.list();
	}
	
	public List<DistrictSyncData> getUpdatedDistricts(Date lastSyncTime){
		Criteria criteria = this.createCriteria("d")
				.createAlias("d.user", "u", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("d.districtId"), "districtId");
		projList.add(Projections.property("u.userId"), "userId");
		projList.add(Projections.property("d.code"), "code");
		projList.add(Projections.property("d.chineseName"), "chineseName");
		projList.add(Projections.property("d.englishName"), "englishName");
		projList.add(Projections.property("d.createdDate"), "createdDate");
		projList.add(Projections.property("d.modifiedDate"), "modifiedDate");
		projList.add(Projections.property("d.coverage"), "coverage");

		criteria.setProjection(projList);

		criteria.setResultTransformer(Transformers.aliasToBean(DistrictSyncData.class));
		return criteria.list();
	}

	public District getDistrictById(Integer districtId){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("districtId", districtId));
		return (District)criteria.uniqueResult();
	}
	
	public List<DistrictModel> getAllDistrictByAllocationBatch(Integer allocationBatchId){
		String hql = "select distinct d.districtId as districtId, d.code as code, d.chineseName as chineseName, d.englishName as englishName, u.userId as userId "
				+ "from QuotationRecord qr "
				+ "left join qr.quotation q "
				+ "left join q.outlet o "
				+ "left join o.tpu tpu "
				+ "left join tpu.district d "
				+ "left join d.user u "
				+ "where qr.allocationBatch.allocationBatchId = :allocationBatchId";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("allocationBatchId", allocationBatchId);

		query.setResultTransformer(Transformers.aliasToBean(DistrictModel.class));
		return query.list();
	}
	
	public List<DistrictModel> getAllDistrictAsModel(){
		String hql = "select d.districtId as districtId, d.code as code, d.chineseName as chineseName, d.englishName as englishName, u.userId as userId "
				+ "from District d "
				+ "left join d.user u "
				+ "order by d.code asc ";
		
		Query query = getSession().createQuery(hql);

		query.setResultTransformer(Transformers.aliasToBean(DistrictModel.class));
		return query.list();
	}
	
	public List<DistrictManDayRequiredModel> getManDayRequired(Integer allocationBatchId){
		SQLQuery query = this.getSession().createSQLQuery("exec GetDistrictManDayRequired :allocationBatchId ");
		query.setParameter("allocationBatchId", allocationBatchId);
		query.addScalar("districtId", StandardBasicTypes.INTEGER);
		query.addScalar("total", StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(DistrictManDayRequiredModel.class));
		return query.list();
		/*
		String hql = "select sum(fullSeason.quotationLoading) + (case when sum(summer.quotationLoading) > sum(winter.quotationLoading) then sum(summer.quotationLoading) else sum(winter.quotationLoading) end) as quotationLoading, "
						+ "qr.assignedStartDate as assignedStartDate, qr.assignedEndDate as assignedEndDate "
					+ "from QuotationRecord qr "
					+ "inner join qr.quotation q "
					+ "inner join qr.outlet o "
					+ "inner join o.tpu tpu "
					+ "inner join tpu.district d "
//					+ " left join q.unit as totalUnit "
//					+ " left join q.unit as normal on normal.seasonality in (1,4) and totalUnit=normal "
//					+ " left join q.unit as summer on summer.seasonality = 2 and totalUnit=summer " 
//					+ " left join q.unit as winter on winter.seasonality = 3 and totalUnit=winter " 
//					
//					+ " left join normal.quotations as fullSeason on fullSeason = q "					
//					+ " left join summer.quotations as summerSeason on summerSeason = q "					
//					+ " left join winter.quotations as winterSeason on winterSeason = q "
					+ "left join qr.quotation fullSeason on fullSeason.unit.seasonality = 1 or fullSeason.unit.seasonality = 4 "
					+ "left join qr.quotation summer on summer.unit.seasonality = 2 "
					+ "left join qr.quotation winter on winter.unit.seasonality = 3 "
					+ "where qr.allocationBatch.allocationBatchId = :allocationBatchId "
					+ "and d.districtId = :districtId "
					+ "and qr.user is null "
					+ "group by qr.assignedStartDate, qr.assignedEndDate";

		Query query = getSession().createQuery(hql);
		
		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("districtId", districtId);
		query.setResultTransformer(Transformers.aliasToBean(ManDayRequiredModel.class));
		return query.list();
		*/
	}
	
	public List<RUACaseReportIndividualReport.districtCodeMapping> getRUAIndividualReportNewRecruitment(Integer surveyMonthId
			, Integer userId, String outletTypeShortCode, Integer purposeId, List<Integer> districtIds){
		String sql = "Select d.code as district "
				+ ", case when q.newRecruitment is null then 0 else q.newRecruitment end as newRecruitment "
				+ " From District as d "
				+ " left join (Select d.districtId as id "
				+ ", count(distinct qr.quotationId) as newRecruitment "
				+ " From QuotationRecord as qr "
				+ " left join Assignment as a on qr.AssignmentId = a.AssignmentId "
				+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId "
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId "
				+ " left join Outlet as o on o.OutletId = qr.OutletId "
				+ " left join Tpu as t on o.TpuId = t.TpuId "
				+ " left join [User] as ur on ur.UserId = qr.UserId "
				+ " left join [Rank] as r on r.RankId = ur.RankId "
				+ " left join Unit as u on u.UnitId = q.UnitId "
				+ " left join SubItem as si on u.SubItemId = si.SubItemId "
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ " left join Purpose as pp on pp.PurposeId = u.PurposeId "
				+ " left join District as d on d.districtId = t.districtId "
				+ " where qr.isNewRecruitment = 1"
				+ " and qr.isBackNo = 0 "
				+ " and qr.isBackTrack = 0 "
				+ " and qr.status = 'Approved' "
				+ " and sm.surveyMonthId = :surveyMonthId "
				+ " and ur.userId = :userId "
				+ " and substring(ot.code, len(ot.code)-2, 3) = :outletTypeShortCode "
				+ " and pp.purposeId = :purposeId ";
		
		if (districtIds != null && districtIds.size() > 0){
			sql += " and d.districtId in ( :districtIds ) ";
		}
		
		sql += " group by d.districtId ) as q on q.id = d.districtId ";
		
		if (districtIds != null && districtIds.size() > 0){
			sql += " where d.districtId in ( :districtIds ) ";
		}
		
		sql += " order by d.code ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameter("userId", userId);
		query.setParameter("purposeId", purposeId);
		query.setParameter("outletTypeShortCode", outletTypeShortCode);
		if (districtIds != null && districtIds.size() > 0){
			query.setParameterList("districtIds", districtIds);
		}
		
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("newRecruitment", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(RUACaseReportIndividualReport.districtCodeMapping.class));
		
		return query.list();
	}
	
	public List<String> getDistrictCodeByIds(List<Integer> ids) {
		Criteria criteria = this.createCriteria("d");
		
		if (ids != null && ids.size() > 0){
			criteria.add(Restrictions.in("id", ids));
		}
		
		criteria.addOrder(Order.asc("d.code"));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("d.code"), "code");
		
		criteria.setProjection(projList);
		
		return criteria.list();
	}
	
	public List<RUACaseReportOverviewReport.districtCodeMapping> getRUAOverviewReportNewRecruitment(Integer surveyMonthId
			, Integer userId, Integer purposeId, List<Integer> districtIds, String team){
		String subSql = " Select d.districtId as districtId "
				+ " , count(distinct q.quotationId) as newRecruitment "
				+ " from QuotationRecord as qr "
				+ " left join Assignment as a on qr.AssignmentId = a.AssignmentId "
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId "
				+ " left join Outlet as o on o.OutletId = qr.OutletId "
				+ " left join Tpu as t on o.tpuId = t.tpuId "
				+ " left join district as d on d.DistrictId = t.DistrictId "
				+ " left join Unit as un on un.UnitId = q.UnitId "
				+ " left join Purpose as pp on un.PurposeId = pp.PurposeId "
				+ " left join [User] as u on u.userId = qr.userId "
				+ " where qr.isNewRecruitment = 1 "
				+ " and qr.isBackNo = 0 "
				+ " and qr.isBackTrack = 0 "
				+ " and qr.status = 'Approved' "
				+ " and a.surveyMonthId = :surveyMonthId "
				+ " and pp.purposeId = :purposeId ";
		
		if (userId != null){
			subSql += " and u.userId = :userId ";
		}
		
		if (!StringUtils.isEmpty(team)){
			subSql += " and u.team = :team ";
		}
		
		if (districtIds != null && districtIds.size() > 0){
			subSql += " and d.districtId in ( :districtIds ) ";
		}
		
		subSql += " group by d.districtId ";
		
		String sql = "Select d.code as district "
				+ " , case when q.newRecruitment is null then 0 else q.newRecruitment end as newRecruitment "
				+ " From District as d "
				+ " left join ( "+subSql+" ) as q on d.districtId = q.districtId "
				+ " where 1 = 1 ";
		
		if (districtIds != null && districtIds.size() > 0){
			sql += " and d.districtId in ( :districtIds ) ";
		}
		
		sql += " order by d.code ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameter("purposeId", purposeId);
		
		if (userId != null){
			query.setParameter("userId", userId);
		}
		
		if (!StringUtils.isEmpty(team)){
			query.setParameter("team", team);
		}
		
		if (districtIds != null && districtIds.size() > 0){
			query.setParameterList("districtIds", districtIds);
		}
		
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("newRecruitment", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(RUACaseReportOverviewReport.districtCodeMapping.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<District> getOrderedDistrictsByIds(List<Integer> ids, Boolean isAsc){
		Criteria criteria = this.createCriteria("d");
		criteria.addOrder(isAsc ? Order.asc("d.code") : Order.desc("d.code"));
		if(ids != null){
			criteria.add(Restrictions.in("d.districtId", ids));
		}
		return criteria.list();
	}
}
