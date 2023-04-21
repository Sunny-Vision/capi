package capi.dal;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
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
import capi.entity.AllocationBatch;
import capi.entity.SurveyMonth;
import capi.model.assignmentAllocationAndReallocation.AssignmentAllocationStatisiticModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.AssignmentAllocationListModel;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.surveyMonthTab.AllocationBatchDetailsModel;
import capi.model.report.ManDayAvailable;


@Repository("AllocationBatchDao")
public class AllocationBatchDao extends GenericDao<AllocationBatch> {
	
	public List<AllocationBatch> findAllocationBatchsBySurveyMonthId(SurveyMonth surveyMonth){
		Criteria criteria = this.createCriteria("ab");
		
		criteria.add(Restrictions.eq("surveyMonth", surveyMonth));
		
		return criteria.list();
		
	}
	
	public List<AssignmentAllocationListModel> listCompletedBatch(String search, int firstRecord, int displayLength, Order order){
		
		Criteria criteria = this.createCriteria("ab")
				.createAlias("ab.districtHeadAdjustments", "dha", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ab.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(
				Projections.projectionList()
				.add(Projections.groupProperty("sm.referenceMonth"), "referenceMonth")
				.add(Projections.groupProperty("ab.allocationBatchId"), "allocationBatchId")
				.add(Projections.groupProperty("ab.batchName"), "batchName")
				.add(Projections.groupProperty("ab.status"), "status")
			);
		
		criteria.add(Restrictions.isNotNull("dha.districtHeadAdjustmentId"));
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.like("ab.batchName", search, MatchMode.ANYWHERE)
			);
		}
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		if(order!=null){
			if("sm.referenceMonth".equals(order.getPropertyName())){
				criteria.addOrder(order).addOrder(Order.desc("ab.allocationBatchId"));
			}else{
				criteria.addOrder(order).addOrder(Order.desc("sm.referenceMonth"));
			}
		}

        criteria.setResultTransformer(Transformers.aliasToBean(AssignmentAllocationListModel.class));
		
		return criteria.list();
		
	}
	

	
	public long countAllocationBatch(String search){

		Criteria criteria = this.createCriteria("ab")
				.createAlias("ab.districtHeadAdjustments", "dha", JoinType.LEFT_OUTER_JOIN)
				.createAlias("ab.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);
		
		
		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("ab.allocationBatchId"),
				Projections.groupProperty("ab.batchName")
		));

		criteria.setProjection(projList);
		
		criteria.add(Restrictions.isNotNull("dha.districtHeadAdjustmentId"));
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.like("ab.batchName", search, MatchMode.ANYWHERE)
			);
		}
		
		return (long) criteria.uniqueResult();
	}
	
	public List<AllocationBatch> getUnassignedAllocationBatchBySurveyMonth(SurveyMonth sm, Date baseDate){
		Criteria criteria = this.createCriteria("ab")
				.add(Restrictions.eq("ab.surveyMonth", sm))
				.add(Restrictions.isEmpty("ab.districtHeadAdjustments"));
		if (baseDate != null){
			criteria.add(Restrictions.gt("ab.startDate", baseDate));
		}
		return criteria.list();
		
	}
	
	public AllocationBatchDetailsModel getAllocationBatchDetails(Integer allocationBatchId){

		String hql = "select "+
				"ab.startDate as allocationBatchStartDate, ab.endDate as allocationBatchEndDate, "+
				"sm.startDate as surveyMonthStartDate, sm.endDate as surveyMonthEndDate "+
			"from AllocationBatch ab "+
			"inner join ab.surveyMonth sm "+
			"where ab.allocationBatchId = :allocationBatchId ";

		Query query = getSession().createQuery(hql);
		
		query.setParameter("allocationBatchId", allocationBatchId);
		
		query.setResultTransformer(Transformers.aliasToBean(AllocationBatchDetailsModel.class));
		
		return (AllocationBatchDetailsModel) query.uniqueResult();
	}
	
	public Double getManDeyRequired(Integer allocationBatchId){
//		String hql = "select sum(fullSeason.quotationLoading) + (case when sum(summer.quotationLoading) > sum(winter.quotationLoading) then sum(summer.quotationLoading) else sum(winter.quotationLoading) end)"
//						+ " as QuotationLoading"
//					+ " from QuotationRecord qr "
//					+ "inner join qr.quotation q "
//					+ "left join qr.quotation fullSeason left join fullSeason.unit on fullSeason.unit.seasonality = 1 or fullSeason.unit.seasonality = 4 "
//					+ "left join qr.quotation summer left join summer.unit on summer.unit.seasonality = 2 "
//					+ "left join qr.quotation winter left join winter.unit on winter.unit.seasonality = 3 "
//					+ "where qr.allocationBatch.allocationBatchId = :allocationBatchId and qr.user is null"+
//					"";
//
//		Query query = getSession().createQuery(hql);
		
		String sql = "exec CalculateAllocationBatch :allocationBatchId";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.addScalar("Total", StandardBasicTypes.DOUBLE);
		query.setParameter("allocationBatchId", allocationBatchId);
		
		return (Double) query.uniqueResult();
	}
	
	public Double getTotalAvailableManDay(Date fromDate, Date toDate){
		
		DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		String fromDateStr = format.format(fromDate);
		String toDateStr = format.format(toDate);
		
		Query query = this.getSession().createSQLQuery(
				"execute [GetAvailableManDayInDateRange] :fromDateStr, :toDateStr")
				.setParameter("fromDateStr", fromDateStr)
				.setParameter("toDateStr", toDateStr);
		return ((BigDecimal) query.uniqueResult()).doubleValue();
	}
	
	
	public Double getManDayForDateRangeSpecifedUserQuotationRecord(Integer allocationBatchId){
		return this.getManDayForDateRangeSpecifedUserQuotationRecord(allocationBatchId, null);
		
	}
	
	
	public Double getManDayForDateRangeSpecifedUserQuotationRecord(Integer allocationBatchId, Integer userId){
		String sql = " Select isnull(sum(whole.QuotationLoading), 0) "
				   + " + isnull(case when isnull(sum(summer.QuotationLoading),0) > isnull(sum(winter.QuotationLoading) ,0) "
				   + " then sum(summer.QuotationLoading) else sum(winter.QuotationLoading) end, 0) as total"
				   + " From QuotationRecord as qr"
				   		+ " inner join Quotation as q on q.quotationId = qr.quotationId "
				   		+ " inner join Outlet as o on o.outletId = qr.outletId "
				   		+ " inner join TPU as t on t.tpuId = o.tpuId "
				   		+ " inner join District as d on d.districtId = t.districtId "
				   		+ " left join Unit as fullSeason on q.unitId = fullSeason.unitId and (fullSeason.seasonality = 1 or fullSeason.seasonality = 4) "
				   		+ " left join Quotation as whole on whole.quotationId = qr.quotationId and whole.unitId = fullSeason.unitId "
				   		+ " left join Unit as summerSeason on q.unitId = summerSeason.unitId and summerSeason.seasonality = 2 "
				   		+ " left join Quotation as summer on summer.quotationId = qr.quotationId and summer.unitId = summerSeason.unitId "
				   		+ " left join Unit as winterSeason on q.unitId  = winterSeason.unitId and winterSeason.seasonality = 3 "
				   		+ " left join Quotation as winter on winter.quotationId = qr.quotationId and winter.unitId = winterSeason.unitId "
				   	+ " where qr.allocationBatchId = :allocationBatchId "
				   	+ " and qr.isBackTrack = 0 "
				   	+ " and qr.isBackNo = 0 "
				   	+ " and qr.isSpecifiedUser = 1 ";
		
					if (userId != null){
						sql += " and qr.userId = :userId ";
					}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("allocationBatchId", allocationBatchId);
		if (userId != null){
			query.setParameter("userId", userId);
		}		
		query.addScalar("total",StandardBasicTypes.DOUBLE);		
		return (Double)query.uniqueResult();
		
	}
	
	public Double getManRequiredForAllocationBatch(Integer allocationBatchId, Integer userId){
		String sql = " select isnull(sum(q.QuotationLoading),0) as total "
				+ " from QuotationRecord as qr "
				+ " inner join Quotation as q "
				+ " 	on q.QuotationId = qr.QuotationId "
				+ " where qr.allocationBatchId = :allocationBatchId ";
		if (userId != null){
			sql += " and qr.userId = :userId ";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("allocationBatchId", allocationBatchId);
		if (userId != null){
			query.setParameter("userId", userId);
		}		
		query.addScalar("total",StandardBasicTypes.DOUBLE);		
		return (Double)query.uniqueResult();
	}
	
	
	public AllocationBatch getLatestUnallocatedAllocationBatch(){
		String hql = "select ab "
				+ " from AllocationBatch ab "
				+ " left join ab.districtHeadAdjustments as dha "
				+ " where dha is null "
				+ " order by ab.startDate asc";
		
		Query query = this.getSession().createQuery(hql);
		query.setMaxResults(1);
		return (AllocationBatch)query.uniqueResult();
		
	}
	
	
	public AllocationBatch getUnallocatedAllocationBatchByStartDate(Date startDate){
		String hql = "select ab "
				+ " from AllocationBatch ab "
				+ " left join ab.districtHeadAdjustments as dha "
				+ " where dha.districtHeadAdjustmentId is null and ab.startDate = :startDate"
				+ " order by ab.startDate asc";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("startDate", startDate);
		query.setMaxResults(1);
		return (AllocationBatch)query.uniqueResult();
		
	}
	

	public List<AllocationBatch> getAllocatedAllocationBatchBySurveyMonthId(Integer surveyMonthId){
		String hql = "select ab "
				+ " from AllocationBatch ab "
				+ " left join ab.districtHeadAdjustments as dha "
				+ " left join ab.surveyMonth as s "
				+ " where dha is not null and s.surveyMonthId = :surveyMonthId "
				+ " order by ab.startDate asc";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("surveyMonthId", surveyMonthId);
		return query.list();
		
	}
	
	
	public AssignmentAllocationStatisiticModel getAllocationStatistic(Integer surveyMonthId){
		String hql = "select s.surveyMonthId as surveyMonthId, count(distinct ab) as allocationBatchCnt, "
				+ " count(distinct total) as totalAdjustment, count(distinct approved) as approvedAdjustment "
				+ " from AllocationBatch as ab "
				+ "	inner join ab.surveyMonth as s "
				+ " left join ab.assignmentAdjustments as total "
				+ " left join ab.assignmentAdjustments as approved on approved.status = 'Approved' "
				+ " where s.surveyMonthId = :surveyMonthId and ab.status = :status"
				+ " group by s.surveyMonthId ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameter("status", 2);
		query.setFetchSize(1);
		query.setResultTransformer(Transformers.aliasToBean(AssignmentAllocationStatisiticModel.class));
		
		return (AssignmentAllocationStatisiticModel)query.uniqueResult();
		
	}
	
	public List<Integer> getAllocatedAllocationBatch(Integer surveyMonthId){
		String hql = "select ab.allocationBatchId as allocationBatchId "
				+ " from AllocationBatch as ab "
				+ "	inner join ab.surveyMonth as s "
				+ " left join ab.assignmentAdjustments as total "
				+ " left join ab.assignmentAdjustments as approved on approved.status = 'Approved' "
				+ " where s.surveyMonthId = :surveyMonthId  "
				+ " group by ab.allocationBatchId "
				+ " having count(distinct approved) = count(distinct total) ";
		Query query = this.getSession().createQuery(hql);
		query.setParameter("surveyMonthId", surveyMonthId);
		return query.list();
		
	}

	public List<AllocationBatch> searchAllocationBatch(String search,
			Date referenceMonth, int firstRecord, int displayLength) {
		
		Criteria critera = this.createCriteria("ab").createAlias("ab.surveyMonth", "sm").setFirstResult(firstRecord)
				.setMaxResults(displayLength).addOrder(Order.asc("ab.batchName"));
		
		if(referenceMonth != null){
			critera.add(
					Restrictions.eq("sm.referenceMonth", referenceMonth)
				);
		}
		
		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.like("ab.batchName", "%"+search+"%")
				);
		}
		
		return critera.list();
	}

	public long countSearch(String search, Date referenceMonth) {
		Criteria critera = this.createCriteria("ab").createAlias("ab.surveyMonth", "sm");
		
		if(referenceMonth != null){
			critera.add(
				Restrictions.eq("sm.referenceMonth", referenceMonth)
			);
		}
		
		if (!StringUtils.isEmpty(search)) {
			critera.add(
					Restrictions.like("ab.batchName", "%"+search+"%")
			);
		}
		
		return (long) critera.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public List<String> searchAllocationBatchByMonthRange(String search, Integer firstRecord, Integer displayLength, Date fromMonth, Date toMonth){
		Criteria criteria = this.createCriteria("ab")
				.createAlias("ab.surveyMonth", "sm");
		
			criteria.add(Restrictions.ge("sm.referenceMonth", fromMonth));
		
			criteria.add(Restrictions.le("sm.referenceMonth", toMonth));
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.like("ab.batchName", "%" + search + "%")
				
			);
		}
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("ab.batchName"));
		criteria.setProjection(projList);
		
		criteria.setFirstResult(firstRecord);
		criteria.setMaxResults(displayLength);
		
		return criteria.list();
	}
	
	public long countSearchAllocationBatchByMonthRange(String search, Date fromMonth, Date toMonth){
		Criteria criteria = this.createCriteria("ab")
				.createAlias("ab.surveyMonth", "sm");
		
			criteria.add(Restrictions.ge("sm.referenceMonth", fromMonth));
		
			criteria.add(Restrictions.le("sm.referenceMonth", toMonth));
		
		if (!StringUtils.isEmpty(search)) {
			criteria.add(
					Restrictions.like("ab.batchName", "%" + search + "%")
			);
		}
		
		criteria.setProjection(SQLProjectionExt.groupCount(Projections.groupProperty("ab.batchName")));
		return (long)criteria.uniqueResult();
	}
	
	public List<AllocationBatch> getAllocationBatchByIds(List<Integer> ids) {
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("allocationBatchId", ids));
		return criteria.list();
	}
	
	public Long getNumberOfAssignmentInAllocationBatch(int allocationBatchId, Date referenceMonth) {
		String hql = "select count(distinct a.assignmentId) as count "
				+ " from QuotationRecord as qr "
				+ " inner join qr.allocationBatch as ab "
				+ " inner join ab.surveyMonth as sm "
				+ " inner join qr.assignment as a "
				+ " where ab.allocationBatchId = :allocationBatchId "
				+ " and sm.referenceMonth = :referenceMonth and qr.isSpecifiedUser = 0 ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("referenceMonth", referenceMonth);
		
		return (Long)query.uniqueResult();
	}

	public Long getNumberOfQuotationInAllocationBatch(int allocationBatchId, Date referenceMonth) {
		String hql = "select count(distinct qr.quotationRecordId) as count "
				+ " from QuotationRecord as qr "
				+ " inner join qr.allocationBatch as ab "
				+ " inner join ab.surveyMonth as sm "
				+ " where ab.allocationBatchId = :allocationBatchId "
				+ " and sm.referenceMonth = :referenceMonth  and qr.isSpecifiedUser = 0  ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("referenceMonth", referenceMonth);
		
		return (Long)query.uniqueResult();
	}
	
	public List<ManDayAvailable> getManDayAvailableInMonth(Date refMonth){
		String sql = "exec GetManDayAvailableInMonth :refMonth";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("refMonth", refMonth);
		
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("manDayAvailable", StandardBasicTypes.DOUBLE);
		
		query.setResultTransformer(Transformers.aliasToBean(ManDayAvailable.class));
		
		return query.list();
	}
}
