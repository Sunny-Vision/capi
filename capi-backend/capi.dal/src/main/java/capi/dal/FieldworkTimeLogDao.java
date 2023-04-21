package capi.dal;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.FieldworkTimeLog;
import capi.model.SystemConstant;
import capi.model.api.dataSync.FieldworkTimeLogSyncData;
import capi.model.report.SummaryOfWorkload;
import capi.model.report.TravellingClaimFormACSO;
import capi.model.report.TravellingClaimFormRecord;

@Repository("FieldworkTimeLogDao")
public class FieldworkTimeLogDao  extends GenericDao<FieldworkTimeLog>{

	public List<SummaryOfWorkload> getSummaryOfWorkload(Date startMonth, Date endMonth){
		
		SQLQuery query = this.getSession().createSQLQuery("exec [CalculateSummaryOfWorkloadReport] :startMonth, :endMonth");
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("outletTypeCode", StandardBasicTypes.STRING);
		query.addScalar("outletTypeName", StandardBasicTypes.STRING);
		query.addScalar("districtCode", StandardBasicTypes.STRING);
		query.addScalar("districtId", StandardBasicTypes.INTEGER);
		query.addScalar("spent", StandardBasicTypes.DOUBLE);
		query.addScalar("countQuotationRecordId", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfWorkload.class));
		
		return query.list();
	}
	
	public List<FieldworkTimeLogSyncData> getUpdatedFielworkTimeLog(Date lastSyncTime, Integer[] timeLogIds, List<Integer> unUpdateIds){
		String startTime = String.format("dbo.FormatTime(ft.startTime, '%s')", SystemConstant.TIME_FORMAT);
		String endTime = String.format("dbo.FormatTime(ft.endTime, '%s')", SystemConstant.TIME_FORMAT);
		
		String hql = "select ft.fieldworkTimeLogId as fieldworkTimeLogId, ft.referenceMonth as referenceMonth"
				+ ", case when ft.startTime is null then '' else "+startTime+" end as startTime"
				+ ", ft.survey as survey"
				+ ", ft.caseReferenceNo as caseReferenceNo, a.assignmentId as assignmentId"
				+ ", ft.activity as activity, ft.enumerationOutcome as enumerationOutcome"
				+ ", ft.building as building, ft.destination as destination"
				+ ", ft.transport as transport, ft.expenses as expenses"
				+ ", ft.remark as remark, ft.createdDate as createdDate"
				+ ", ft.modifiedDate as modifiedDate, tl.timeLogId as timeLogId"
				+ ", case when ft.endTime is null then '' else "+endTime+" end as endTime"
				+ ", ft.totalQuotation as totalQuotation, ft.quotationCount as quotationCount"
				+ ", ft.recordType as recordType, ft.fromLocation as fromLocation"
				+ ", ft.toLocation as toLocation, ft.includeInTransportForm as includeInTransportForm"
				+ ", ft.transit as transit"
				+ " from FieldworkTimeLog as ft"
				+ " left join ft.assignment as a"
				+ " left join ft.timeLog as tl"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and ft.modifiedDate >= :modifiedDate";
		}
		
		if(timeLogIds!=null && timeLogIds.length>0){
			hql += " and tl.timeLogId in ( :timeLogIds )";
		}
		
		if(unUpdateIds!=null && unUpdateIds.size()>0){
			hql += " and ft.fieldworkTimeLogId in ( :unUpdateIds )";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if(lastSyncTime!=null)
			query.setParameter("modifiedDate", lastSyncTime);
		if(timeLogIds!=null && timeLogIds.length>0)
			query.setParameterList("timeLogIds", timeLogIds);
		if(unUpdateIds!=null && unUpdateIds.size()>0){
			query.setParameterList("unUpdateIds", unUpdateIds);
		}
		query.setResultTransformer(Transformers.aliasToBean(FieldworkTimeLogSyncData.class));
		return query.list();
	}
	
	public List<TravellingClaimFormACSO> getTravellingClaimFormTotalAmount(Date month, Integer userId) {

		Criteria criteria = this.createCriteria("f")
								.createAlias("f.timeLog", "t", JoinType.LEFT_OUTER_JOIN)
								.createAlias("t.user", "u", JoinType.LEFT_OUTER_JOIN)
								.createAlias("u.rank", "r", JoinType.LEFT_OUTER_JOIN);
		
		String rank = "{r}.name";
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("u.englishName"), "name");
		projList.add(SQLProjectionExt.sqlProjection(rank + " as rank", new String[]{"rank"}, StandardBasicTypes.STRING), "rank");
		projList.add(Projections.property("u.office"), "office");
		projList.add(Projections.property("u.office2"), "office2");
		projList.add(Projections.property("f.transport"), "transport");
		projList.add(Projections.property("f.expenses"), "expenses");
		
		criteria.setProjection(projList);
		
		Date lastDay = DateUtils.addDays(DateUtils.addMonths(month, 1),-1);
		criteria.add(Restrictions.and(
				Restrictions.between("t.date", month, lastDay),
				Restrictions.eq("u.userId", userId)
		));
		criteria.add(Restrictions.eq("f.includeInTransportForm",true));
		criteria.add(Restrictions.in("t.status", new String[] {"Submitted", "Approved"}));
		criteria.setResultTransformer(Transformers.aliasToBean(TravellingClaimFormACSO.class));

		return criteria.list();
	}
	
	public List<TravellingClaimFormRecord> getTravellingClaimFormRecord(Date month, Integer userId) {

		String date = String.format("FORMAT({t}.date, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		Criteria criteria = this.createCriteria("f")
								.createAlias("f.timeLog", "t", JoinType.LEFT_OUTER_JOIN)
								.createAlias("t.user", "u", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.sqlProjection(date + " as date", new String[]{"date"}, StandardBasicTypes.STRING), "date");
		projList.add(Projections.property("f.fromLocation"), "from");
		projList.add(Projections.property("f.toLocation"), "to");
		projList.add(Projections.property("f.transport"), "transport");
		projList.add(Projections.property("f.expenses"), "expenses");
		projList.add(Projections.property("f.remark"), "remark");
		projList.add(Projections.property("f.transit"), "transit");
		projList.add(Projections.property("f.startTime"), "startTime");
		projList.add(Projections.property("t.timeLogId"), "timeLogId");
		projList.add(Projections.property("f.activity"), "activity");
		projList.add(Projections.property("f.destination"), "destination");
		projList.add(Projections.property("f.includeInTransportForm"), "includeInTransportForm");
		
		criteria.setProjection(projList);
		Date lastDay = DateUtils.addDays(DateUtils.addMonths(month, 1),-1);
		criteria.add(Restrictions.and(
				//Restrictions.eq("f.referenceMonth", month),
				Restrictions.between("t.date", month, lastDay),
				Restrictions.eq("u.userId", userId)
		));
		
		criteria.addOrder(Order.asc("t.date"));
		criteria.addOrder(Order.asc("f.startTime"));
		criteria.add(Restrictions.in("t.status", new String[] {"Submitted", "Approved"}));
		criteria.setResultTransformer(Transformers.aliasToBean(TravellingClaimFormRecord.class));

		return criteria.list();
	}
	
}
