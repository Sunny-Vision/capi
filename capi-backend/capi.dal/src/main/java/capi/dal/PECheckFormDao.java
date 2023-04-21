package capi.dal;

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
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.PECheckForm;
import capi.model.SystemConstant;
import capi.model.api.dataSync.PECheckFormSyncData;
import capi.model.commonLookup.PECheckLookupTableList;
import capi.model.report.PECheckSummaryReport;

@Repository("PECheckFormDao")
public class PECheckFormDao extends GenericDao<PECheckForm> {
	
	@SuppressWarnings("unchecked")
	public List<PECheckLookupTableList> getPECheckLookupTableList(String search, int firstRecord, int displayLength, Order order,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, Integer[]excludedPEFormIds, List<Integer> userIds) {

		String convenientStartTime = String.format("dbo.FormatTime(o.convenientStartTime, '%s')", SystemConstant.TIME_FORMAT);
		String convenientEndTime = String.format("dbo.FormatTime(o.convenientEndTime, '%s')", SystemConstant.TIME_FORMAT);
		String collectionDate = String.format("case when a.collectionDate is null then FORMAT(a.endDate, '%1$s', 'en-us') else FORMAT(a.collectionDate, '%1$s', 'en-us') end", SystemConstant.DATE_FORMAT);
		String convenientTime = String.format(
			 " case when o.convenientStartTime is null then '' else dbo.FormatTime(o.convenientStartTime, '%1$s') end"
				+ "+'-'+"
				+ " case when o.convenientEndTime is null then '' else  dbo.FormatTime(o.convenientEndTime, '%1$s') end  ", 
				SystemConstant.TIME_FORMAT);
		
		//Fixed PE Check show some null date - Refer Asssignment Main
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		
		String hql = " select pef.peCheckFormId as peCheckFormId, "
				+ " o.name as firm, d.chineseName as district, t.code as tpu, "
				/*
				+ " case "
				+ "	when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end  as deadline, "
				*/

				//Fixed PE Check show some null date - Refer Asssignment Main
				+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
				+ "   when a.endDate is not null then " + endDateFormat + " "
				+ "   else '' end as deadline, "
				
				+ " o.streetAddress as address, count(distinct qr.quotationRecordId) as noOfQuotation, "
				+ " case "
				+ "	when o.convenientStartTime is null then '' "
				+ " else " + convenientStartTime + " end  as convenientStartTime, "
				+ " case "
				+ "	when o.convenientEndTime is null then '' "
				+ " else " + convenientEndTime + " end  as convenientEndTime, "		
				+  convenientTime + " as convenientTime, "	
				+ " o.remark as outletRemark, "
				+ " ofc.staffCode as fieldOfficerCode, ofc.chineseName as chineseName, ofc.englishName as englishName"
				+ " from PECheckForm pef "
				+ " left join pef.assignment as a left join a.quotationRecords as qr left join qr.quotation as q "
				+ " left join a.outlet as o left join o.tpu as t left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join pef.officer as ofc "
				+ " where (pef.status is null or pef.status != 'Submitted') ";
		
		if (outletTypeId != null && outletTypeId.length > 0 ) hql += " and ot.shortCode in ( :outletTypeId ) ";
		
		if(districtId != null && districtId.length > 0 ) hql += " and d.districtId in ( :districtId ) ";
		
		if(tpuId != null && tpuId.length > 0 ) hql += " and t.tpuId in ( :tpuId ) ";
		
		if (excludedPEFormIds != null && excludedPEFormIds.length > 0) hql += " and pef.peCheckFormId not in (:excludedPEFormIds) ";
		
		if (userIds != null && userIds.size() > 0){
			hql += " and ofc.supervisor.userId in (:userIds) ";
		}
			
		if (!StringUtils.isEmpty(search)){
			hql += " and  (o.name like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when o.convenientStartTime is null then '' "
				+ "   else " + convenientStartTime + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when o.convenientEndTime is null then '' "
				+ "   else " + convenientEndTime + " end ) like :search "
				+ " or o.remark like :search "
				+ " or ofc.staffCode like :search or ofc.chineseName like :search or ofc.englishName like :search "
				+ " ) ";
		}
		
		//hql += " group by pef.peCheckFormId, o.name, d.chineseName, t.code, a.collectionDate, "
		hql += " group by pef.peCheckFormId, o.name, d.chineseName, t.code, a.assignedCollectionDate, "
				+ " o.streetAddress, o.convenientStartTime, o.convenientEndTime, o.remark, " 
			+ " ofc.staffCode, ofc.chineseName, ofc.englishName, a.endDate ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
				
		if (outletTypeId != null && outletTypeId.length > 0) query.setParameterList("outletTypeId", outletTypeId);
		
		if(districtId != null && districtId.length > 0) query.setParameterList("districtId", districtId);
		
		if(tpuId != null && tpuId.length > 0) query.setParameterList("tpuId", tpuId);
		
		if (excludedPEFormIds!= null && excludedPEFormIds.length > 0){
			query.setParameterList("excludedPEFormIds", excludedPEFormIds);
		}
		
		if (userIds != null && userIds.size() > 0){
			query.setParameterList("userIds", userIds);
		}
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));

		query.setResultTransformer(Transformers.aliasToBean(PECheckLookupTableList.class));

		return query.list();
	}
	
	public Long countPECheckLookupTableList(String search, 
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, Integer [] excludedPEFormIds, List<Integer> userIds) {
		
		String convenientStartTime = String.format("dbo.FormatTime(o.convenientStartTime, '%s')", SystemConstant.TIME_FORMAT);
		String convenientEndTime = String.format("dbo.FormatTime(o.convenientEndTime, '%s')", SystemConstant.TIME_FORMAT);
		String collectionDate = String.format("case when a.collectionDate is null then FORMAT(a.endDate, '%1$s', 'en-us') else FORMAT(a.collectionDate, '%1$s', 'en-us') end", SystemConstant.DATE_FORMAT);
//		String convenientTime = String.format(
//			 " case when o.convenientStartTime is null then '' else dbo.FormatTime(o.convenientStartTime, '%1$s') end"
//				+ "+'-'+"
//				+ " case when o.convenientEndTime is null then '' else  dbo.FormatTime(o.convenientEndTime, '%1$s') end  ", 
//				SystemConstant.TIME_FORMAT);
		
		String hql = " select count(distinct pef.peCheckFormId) as peCheckFormCount "
				+ " from PECheckForm pef "
				+ " left join pef.assignment as a left join a.quotationRecords as qr left join qr.quotation as q "
				+ " left join a.outlet as o left join o.tpu as t left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join pef.officer as ofc "
				+ " where (pef.status is null or pef.status != 'Submitted') ";
		
		if (outletTypeId != null && outletTypeId.length > 0 ) hql += " and ot.shortCode in ( :outletTypeId ) ";
		
		if(districtId != null && districtId.length > 0 ) hql += " and d.districtId in ( :districtId ) ";
		
		if(tpuId != null && tpuId.length > 0 ) hql += " and t.tpuId in ( :tpuId ) ";
		
		if (excludedPEFormIds != null && excludedPEFormIds.length > 0) hql += " and pef.peCheckFormId not in (:excludedPEFormIds) ";
		
		if (userIds != null && userIds.size() > 0){
			hql += " and ofc.supervisor.userId in (:userIds) ";
		}
			
		if (!StringUtils.isEmpty(search)){
			hql += " and  (o.name like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when o.convenientStartTime is null then '' "
				+ "   else " + convenientStartTime + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when o.convenientEndTime is null then '' "
				+ "   else " + convenientEndTime + " end ) like :search "
				+ " or o.remark like :search "
				+ " or ofc.staffCode like :search or ofc.chineseName like :search or ofc.englishName like :search "
				+ " ) ";
		}
		
//		hql += " group by pef.peCheckFormId, o.name, d.chineseName, t.code, a.collectionDate, "
//			+ " o.streetAddress, o.convenientStartTime, o.convenientEndTime, o.remark, " 
//			+ " ofc.staffCode, ofc.chineseName, ofc.englishName, a.endDate ";
		
		//hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		//query.setFirstResult(firstRecord);
		//query.setMaxResults(displayLength);
				
		if (outletTypeId != null && outletTypeId.length > 0) query.setParameterList("outletTypeId", outletTypeId);
		
		if(districtId != null && districtId.length > 0) query.setParameterList("districtId", districtId);
		
		if(tpuId != null && tpuId.length > 0) query.setParameterList("tpuId", tpuId);
		
		if (excludedPEFormIds!= null && excludedPEFormIds.length > 0){
			query.setParameterList("excludedPEFormIds", excludedPEFormIds);
		}
		
		if (userIds != null && userIds.size() > 0){
			query.setParameterList("userIds", userIds);
		}
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (Long) query.uniqueResult();

//		String convenientStartTime = String.format("dbo.FormatTime(o.convenientStartTime, '%s', 'en-us')", SystemConstant.TIME_FORMAT);
//		String convenientEndTime = String.format("dbo.FormatTime(o.convenientStartTime, '%s', 'en-us')", SystemConstant.TIME_FORMAT);
//		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		
//		String hql = " select count(distinct pef.peCheckFormId) "
//				+ " from PECheckForm pef "
//				+ " left join pef.assignment as a left join a.quotationRecords as qr left join qr.quotation as q "
//				+ " left join a.outlet as o left join o.tpu as t left join t.district as d "
//				+ " left join o.outletTypes as ot "
//				+ " left join pef.officer as ofc "
//				+ " where (pef.status is null or pef.status != 'Submitted') ";
//		
//		if (outletTypeId != null && outletTypeId.length > 0 ) hql += " and ot.shortCode in ( :outletTypeId ) ";
//		
//		if(districtId != null && districtId.length > 0 ) hql += " and d.districtId in ( :districtId ) ";
//		
//		if(tpuId != null && tpuId.length > 0 ) hql += " and t.tpuId in ( :tpuId ) ";
//
//		if (excludedPEFormIds != null && excludedPEFormIds.length > 0) hql += " and pef.peCheckFormId not in (:excludedPEFormIds) ";
//		
//		if (userIds != null && userIds.size() > 0){
//			hql += " and ofc.supervisor.userId not in (:userIds) ";
//		}
//		
//		if (!StringUtils.isEmpty(search)){
//			hql += " and  (o.name like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
//				+ " or ( "
//				+ "   case "
//				+ "   when a.collectionDate is null then '' "
//				+ "   else " + collectionDate + " end ) like :search "
//				+ " ) "
//				+ " or ( "
//				+ "   case "
//				+ "   when o.convenientStartTime is null then '' "
//				+ "   else " + convenientStartTime + " end ) like :search "
//				+ " ) "
//				+ " or ( "
//				+ "   case "
//				+ "   when o.convenientEndTime is null then '' "
//				+ "   else " + convenientEndTime + " end ) like :search "
//				+ " ) "
//				+ " or outletRemark like :search "
//				+ " or ofc.staffCode like :search or ofc.chineseName like :search or ofc.englishName like :search ";
//		}
//		
//		Query query = this.getSession().createQuery(hql);
//				
//		if (outletTypeId != null && outletTypeId.length > 0) query.setParameterList("outletTypeId", outletTypeId);
//		
//		if(districtId != null && districtId.length > 0) query.setParameterList("districtId", districtId);
//		
//		if(tpuId != null && tpuId.length > 0) query.setParameterList("tpuId", tpuId);
//		
//		if (excludedPEFormIds != null && excludedPEFormIds.length > 0){
//			query.setParameterList("excludedPEFormIds", excludedPEFormIds);
//		}
//		
//		if (userIds != null && userIds.size() > 0){
//			query.setParameterList("userIds", userIds);
//		}
//		
//		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
//
//		return (Long) query.uniqueResult();
	}
	
	public List<PECheckForm> getPEReminderList(){
		Date month = DateUtils.truncate(new Date(), Calendar.MONTH);
		Date lastMonth = DateUtils.addMonths(month, -1);
		Criteria criteria = this.createCriteria("p");
		criteria.createAlias("p.assignment", "a");
		criteria.createAlias("a.outlet", "o");
		criteria.createAlias("a.surveyMonth", "s");
		criteria.add(Restrictions.eq("s.referenceMonth", lastMonth));
		criteria.add(Restrictions.or(
				Restrictions.isNull("p.status")
				, Restrictions.ne("p.status", "Submitted")
				));
		//criteria.addOrder(Order.asc("o.outletId"));
		criteria.addOrder(Order.asc("o.firmCode"));		
		return criteria.list();
	}
	
	public List<PECheckFormSyncData> getUpdatedPECheckForm(Date lastSyncTime, Integer[] peCheckFormIds){
		String checkingTime = String.format("dbo.FormatTime(pe.checkingTime, '%s')", SystemConstant.TIME_FORMAT);
		
		String hql = "select pe.peCheckFormId as peCheckFormId"
				+ ", pe.contactPerson as contactPerson, pe.checkingDate as checkingDate"
				+ ", case when pe.checkingTime is null then '' else "+checkingTime+" end as checkingTime"
				+ ", a.assignmentId as assignmentId, o.userId as officerId"
				+ ", pe.checkingMode as checkingMode, pe.peCheckRemark as peCheckRemark"
				+ ", pe.otherRemark as otherRemark, pe.status as status"
				+ ", pe.isNonContact as isNonContact, pe.createdDate as createdDate"
				+ ", pe.modifiedDate as modifiedDate, pe.contactDateResult as contactDateResult"
				+ ", pe.contactTimeResult as contactTimeResult, pe.contactDurationResult as contactDurationResult"
				+ ", pe.contactModeResult as contactModeResult, pe.dateCollectedResult as dateCollectedResult"
				+ ", pe.othersResult as othersResult, u.userId as userId"
				+ " from PECheckForm as pe"
				+ " left join pe.assignment as a"
				+ " left join pe.officer as o"
				+ " left join pe.user as u"
				+ " where pe.peCheckFormId in ( :peCheckFormIds )";
		
		if(lastSyncTime!=null){
			hql += " and pe.modifiedDate >= :modifiedDate";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		query.setParameterList("peCheckFormIds", peCheckFormIds);
		query.setResultTransformer(Transformers.aliasToBean(PECheckFormSyncData.class));
		return query.list();
	}
	
	public List<PECheckForm> getPECheckbyUserReferenceMonth(Integer[] userIds, Date fromMonth, Date toMonth){
		Criteria criteria = this.createCriteria("pe")
				.createAlias("pe.officer", "o", JoinType.LEFT_OUTER_JOIN)
				.createAlias("pe.assignment", "a", JoinType.LEFT_OUTER_JOIN)
				.createAlias("a.surveyMonth", "s", JoinType.LEFT_OUTER_JOIN)
				.setFetchMode("pe.officer", FetchMode.JOIN);
		
		criteria.add(Restrictions.in("o.userId", userIds));
		//criteria.add(Restrictions.ne("o.status","Inactive"));
		criteria.add(Restrictions.and(
				Restrictions.ge("s.referenceMonth", fromMonth),
				Restrictions.le("s.referenceMonth", toMonth)));
		
		return criteria.list();
	}
	
	public List<PECheckSummaryReport> getPECheckSummaryReportByUserReferenceMonth(Integer[] userIds, Date fromMonth, Date toMonth){
		
		String sql = "select "
						+ "u.userId as officerId, "
						+ "isNull(count(case when pe.IsNonContact = 0 and pe.Status = 'Submitted' then pe.IsNonContact end) , 0) as noOfFirmsChecked, "
						+ "isNull(count(case when pe.IsNonContact = 1 and pe.Status = 'Submitted' then pe.IsNonContact end), 0) as noOfFirmsNC, "
						+ "isNull(Count(pe.assignmentId) ,0) AS totalFirmEnumerated "
					+ "from [User] as u "
						+ "left join PECheckForm as pe on u.UserId = pe.OfficerId "
						+ "left join Assignment as a on pe.AssignmentId = a.AssignmentId "
						+ "left join SurveyMonth as s on a.SurveyMonthId = s.SurveyMonthId "
							+ "and s.ReferenceMonth > :fromMonth and s.ReferenceMonth < :toMonth "
					+ "where 1=1 "
						+ "and u.UserId in (:userIds) "
						+ "and u.Status <> 'Inactive' "
					+ "group by u.UserId, pe.OfficerId "
					+ "order by u.UserId asc";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);

		sqlQuery.setParameterList("userIds", userIds);
		sqlQuery.setParameter("fromMonth", fromMonth);
		sqlQuery.setParameter("toMonth", toMonth);
		
		sqlQuery.addScalar("officerId", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("noOfFirmsChecked", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("noOfFirmsNC", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("totalFirmEnumerated", StandardBasicTypes.INTEGER);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(PECheckSummaryReport.class));
		
		return sqlQuery.list();
	}
}
