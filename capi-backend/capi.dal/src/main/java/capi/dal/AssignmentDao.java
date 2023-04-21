package capi.dal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.Assignment;
import capi.entity.Outlet;
import capi.entity.SurveyMonth;
import capi.entity.User;
import capi.model.KeyValueModel;
import capi.model.SystemConstant;
import capi.model.api.dataSync.AssignmentSyncData;
import capi.model.api.onlineFunction.NonScheduleAssignmentList;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.RecommendAssignmentModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.SelectedAssignmentModel;
import capi.model.assignmentAllocationAndReallocation.staffCalendar.AssignmentDisplayModel;
import capi.model.assignmentManagement.TpuReferenceNoModel;
import capi.model.commonLookup.AssignmentLookupSurveyMonthTableList;
import capi.model.commonLookup.AssignmentLookupTableList;
import capi.model.commonLookup.AssignmentReallocationLookupTableList;
import capi.model.commonLookup.PECertaintyCaseLookupTableList;
import capi.model.commonLookup.ReportAssignmentLookupList;
import capi.model.commonLookup.UserLookupTableList;
import capi.model.dashboard.DashboardStatistics;
import capi.model.dashboard.DeadlineRowModel;
import capi.model.dataImportExport.ExportAssignmentAllocationList;
import capi.model.dataImportExport.ExportPECheckSelectedList;
import capi.model.itineraryPlanning.MetricSelectedAssignmentModel;
import capi.model.qualityControlManagement.SupervisoryVisitEditModel;
import capi.model.report.AllocationTransferInTransferOutReallocationRecordsReport;
import capi.model.report.AssignmentAllocationSummaryReport;
import capi.model.report.ExperienceSummaryReport;
import capi.model.report.FieldworkOutputByDistrictReport;
import capi.model.report.FieldworkOutputByOutletTypeReport;
import capi.model.report.SummaryOfProgressReport;
import capi.model.report.SummaryOfQuotations;
import capi.model.timeLogManagement.AssignmentReferenceNoModel;
import capi.model.timeLogManagement.TimeLogSelect2Item;

@Repository("AssignmentDao")
public class AssignmentDao extends GenericDao<Assignment> {
	private static final Logger logger = LoggerFactory.getLogger(AssignmentDao.class);

	public List<AssignmentDisplayModel> getCalendarAssignments(List<User> users, Date fromDate, Date toDate){
		
		String hql = "select attr.session as session, b.code as code, u.userId as userId, cd.date as eventDate "
				+ " from AssignmentAttribute as attr "
				+ " inner join attr.batch as b "
				//+ " inner join attr.allocationBatch as ab "
				+ " inner join attr.user as u "
				+ " inner join attr.batchCollectionDates as cd "
				+ " where cd.date between :fromDate and :toDate ";
		
		if (users != null && users.size() > 0){
			hql += " and u in (:users) ";
		}
		
		hql += " group by  attr.session, b.code, u.userId, cd.date ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		if (users != null && users.size() > 0){
			query.setParameterList("users", users);
		}
				
		query.setResultTransformer(Transformers.aliasToBean(AssignmentDisplayModel.class));
		
		return query.list();
	}
	
	public List<AssignmentDisplayModel> getCalendarAssignmentsForStaffCalendar(List<UserLookupTableList> users, Date fromDate, Date toDate){
		List<Integer> userIds = new ArrayList<Integer> ();
		if(users != null && users.size() > 0) {
			for(UserLookupTableList user : users) {
				userIds.add(user.getId());
			}
		}
		
		String hql = "select attr.session as session, b.code as code, u.userId as userId, cd.date as eventDate "
				+ " from AssignmentAttribute as attr "
				+ " inner join attr.batch as b "
				//+ " inner join attr.allocationBatch as ab "
				+ " inner join attr.user as u "
				+ " inner join attr.batchCollectionDates as cd "
				+ " inner join attr.surveyMonth as sm "
				+ " where cd.date between :fromDate and :toDate ";
		
		hql += " and sm.status = 5 "; // approved
		
		if (userIds != null && userIds.size() > 0){
			hql += " and u.userId in (:userIds) ";
		}
		
		hql += " group by  attr.session, b.code, u.userId, cd.date ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		if (userIds != null && userIds.size() > 0){
			query.setParameterList("userIds", userIds);
		}
				
		query.setResultTransformer(Transformers.aliasToBean(AssignmentDisplayModel.class));
		
		return query.list();
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public List<AssignmentLookupTableList> getOutletTableList(String search,
			int firstRecord, int displayLength, Order order, Integer userId,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, Date planDate, Integer[] excludedOutletIds) {
		
		logger.info("getOutletTableList.");
		//String endDateFormat = String.format("FORMAT(endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		///String endDateFormat = String.format("case when {qr}.assignedCollectionDate is null then FORMAT({qr}.assignedEndDate, '%1$s', 'en-us') else FORMAT({qr}.assignedCollectionDate, '%1$s', 'en-us') end", SystemConstant.DATE_FORMAT);
		//String convenientTime = String.format("dbo.FormatTime(o.convenientStartTime, '%1$s')+'-'+dbo.FormatTime(o.convenientEndTime, '%1$s')", SystemConstant.TIME_FORMAT);
		
		String endDateFormat = String.format(" when min(qr.assignedCollectionDate) is null then FORMAT(min(qr.assignedEndDate), '%1$s', 'en-us') else FORMAT(min(qr.assignedCollectionDate), '%1$s', 'en-us') end", SystemConstant.DATE_FORMAT);
		String [] status = new String[]{"Draft", "Blank", "Rejected"};
		
		String sql = "select CONCAT(d.code, o.firmCode) as referenceNo, "
						+ "b.code as batchCode, "
						+ "o.outletId as outletId, "
						+ "o.name as firm, "
						+ "d.code as district, "
						+ "tpu.code as tpu, "
						+ "o.streetAddress as address, "
						+ "o.convenientStartTime as convenientStartTime, "
						+ "o.convenientEndTime as convenientEndTime, o.remark as outletRemark, count(distinct quotationRecordId) as noOfQuotation, "
						+ "case when min(qr.assignedCollectionDate) is null and min(qr.assignedEndDate) is null "
						+ "then '' " + endDateFormat + " as deadline, "
						+ "case when min(qr.assignedCollectionDate) is null "
						+ "then min(qr.assignedEndDate) else min(qr.assignedCollectionDate) end as deadline2 "
					+ " from "
						+ "Assignment as a "
						+ "inner join Outlet as o on o.outletId = a.outletId "
						+ "inner join Tpu as tpu on tpu.tpuId = o.tpuId "
						+ "inner join District as d on d.districtId = tpu.districtId "
						+ "left join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
						+ "left join Quotation as q on q.QuotationId = qr.QuotationId "
						+ "left join Batch as b on q.BatchId = b.BatchId "
						+ "left join [User] as u on u.userId = qr.userId "
						+ "left join OutletTypeOutlet as t on t.outletId = o.outletId "
					+ "where "
						+ "u.userId = :userId and qr.isBackTrack = 0 and qr.isBackNo = 0 "
						+ "and ( qr.assignedCollectionDate is null and qr.assignedStartDate <= :planDate "
						+ "and qr.status in (:status) "
						+ "or "
						+ "qr.assignedCollectionDate is not null and qr.assignedCollectionDate <= :planDate "
						+ "and qr.status in (:status) "
						+ "or "
						+ "qr.availability = 2 "
						+ "or "
						+ "qr.quotationState = 'Verify' "
						+ "and qr.status in (:status) "
						+ " )";
		
		if (excludedOutletIds != null && excludedOutletIds.length > 0){
			sql += " and o.outletId not in (:excludedOutletIds) ";
        }
        
        if (StringUtils.isNotEmpty(search)) {
        	sql += " and ( "
        			+ " o.name like :search  "
        			+ " or o.streetAddress like :search "
        			+ " or tpu.code like :search "
        			+ " or d.code like :search "
        			+ " or CONCAT(d.code, o.firmCode) like :search"
        			+ " or b.code like :search "
        			+ " ) ";
        }

        if (outletTypeId != null && outletTypeId.length > 0) {
        	sql += " and t.shortCode in (:outletTypeId) ";
        }
        
        if (districtId != null && districtId.length > 0) {
        	sql += " and d.districtId in (:districtId) ";
        }
        
        if (tpuId != null && tpuId.length > 0) {
        	sql += " and tpu.tpuId in (:tpuId) ";
        }
		
        sql += " group by o.outletId, o.firmCode, o.name, d.code, tpu.code, o.streetAddress, o.convenientStartTime, "
        		+ " o.convenientEndTime, o.remark, b.code ";
        
        if ("convenientTime".equals(order.getPropertyName())){
        	sql += " order by o.convenientStartTime " + (order.isAscending()? " asc": " desc")
        		+ " ,o.convenientEndTime " + (order.isAscending()? " asc": " desc");
        }
        else{
        	sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
        }
        		
		
        SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("planDate", planDate);
		query.setParameterList("status", status);
		
		if (excludedOutletIds != null && excludedOutletIds.length > 0){
			query.setParameterList("excludedOutletIds", excludedOutletIds);
        }
        
        if (StringUtils.isNotEmpty(search)) {
        	query.setParameter("search", "%"+search+"%");
        }

        if (outletTypeId != null && outletTypeId.length > 0) {
        	query.setParameterList("outletTypeId", outletTypeId);
        }
        
        if (districtId != null && districtId.length > 0) {
        	query.setParameterList("districtId", districtId);
        }
        
        if (tpuId != null && tpuId.length > 0) {
        	query.setParameterList("tpuId", tpuId);
        }
        query.setFirstResult(firstRecord);
        query.setMaxResults(displayLength);
        
        query.addScalar("outletId", StandardBasicTypes.INTEGER);
        query.addScalar("firm", StandardBasicTypes.STRING);
        query.addScalar("district", StandardBasicTypes.STRING);
        query.addScalar("tpu", StandardBasicTypes.STRING);
        query.addScalar("address", StandardBasicTypes.STRING);
        query.addScalar("convenientStartTime", StandardBasicTypes.TIME);
        query.addScalar("convenientEndTime", StandardBasicTypes.TIME);
        query.addScalar("outletRemark", StandardBasicTypes.STRING);
        query.addScalar("deadline", StandardBasicTypes.STRING);
        query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
        query.addScalar("referenceNo", StandardBasicTypes.STRING);
        query.addScalar("batchCode", StandardBasicTypes.STRING);
      
		query.setResultTransformer(Transformers.aliasToBean(AssignmentLookupTableList.class));
		  
		return query.list();
		
//		Criteria criteria = this.createCriteria("a")
//				.createAlias("a.outlet", "o", JoinType.INNER_JOIN)
//                .createAlias("o.tpu", "tpu", JoinType.INNER_JOIN)
//                .createAlias("tpu.district", "d", JoinType.INNER_JOIN)
//                .createAlias("a.quotationRecords", "qr",JoinType.LEFT_OUTER_JOIN)
//                .createAlias("qr.user", "u",JoinType.LEFT_OUTER_JOIN)
//                .createAlias("o.outletTypes", "t", JoinType.LEFT_OUTER_JOIN);
//                //.createAlias("o.quotations", "q", JoinType.LEFT_OUTER_JOIN);
//        
//        ProjectionList projList = Projections.projectionList();
//        projList.add(Projections.groupProperty("o.outletId"), "outletId");
//        projList.add(Projections.groupProperty("o.name"), "firm");
//        projList.add(Projections.groupProperty("d.code"), "district");
//        projList.add(Projections.groupProperty("tpu.code"), "tpu");
//        projList.add(Projections.groupProperty("o.streetAddress"), "address");
//        
//      
//        
//        projList.add(SQLProjectionExt.sqlProjection(endDateFormat+" as deadline", endDateFormat, new String [] {"deadline"}, new Type[]{StandardBasicTypes.STRING}), "deadline");
//        projList.add(Projections.groupProperty("o.convenientStartTime"), "convenientStartTime");
//        projList.add(Projections.groupProperty("o.convenientEndTime"), "convenientEndTime");
//        projList.add(SQLProjectionExt.sqlProjection(convenientTime+" as convenientTime", convenientTime, new String [] {"convenientTime"}, new Type[]{StandardBasicTypes.STRING}), "convenientTime");
//        
//        projList.add(Projections.groupProperty("o.remark"), "outletRemark");
//        projList.add(Projections.countDistinct("qr.quotationRecordId"), "noOfQuotation");
//        
//        criteria.setProjection(projList);
//        
//        criteria.add(Restrictions.eq("u.userId", userId));
//        
//       
//        
//        criteria.add(Restrictions.or(
//	    		Restrictions.and(
//					Restrictions.isNull("qr.assignedCollectionDate"),
//					Restrictions.ge("qr.assignedEndDate", planDate),
//					Restrictions.le("qr.assignedStartDate", planDate),
//					Restrictions.in("qr.status", status)
//				),
//				Restrictions.and(
//					Restrictions.isNotNull("qr.assignedCollectionDate"),
//					Restrictions.eq("qr.assignedCollectionDate", planDate),
//					Restrictions.in("qr.status", status)
//				),
//				Restrictions.eq("qr.availability", 2),
//				Restrictions.and(
//					Restrictions.eq("qr.quotationState", "Verify"),
//					Restrictions.in("qr.status", status)
//				)
//			)
////    		Restrictions.or(
////    			Restrictions.eq("qr.quotationState", "Revisit"),
////    			Restrictions.eq("qr.quotationState", "Verify"))
////    		)
//        );
//  
////        Restrictions.or(Restrictions.eq("qr.status", "Draft"),Restrictions.eq("qr.status", "Blank"),Restrictions.eq("qr.status", "Rejected"))
//        	
//        if (excludedOutletIds != null && excludedOutletIds.length > 0){
//        	criteria.add(Restrictions.not(Restrictions.in("o.outletId", excludedOutletIds)));
//        }
//        
//        if (StringUtils.isNotEmpty(search)) {
//	        criteria.add(Restrictions.or(
//	                Restrictions.like("o.name", "%"+search+"%"),
//	                Restrictions.like("o.streetAddress", "%"+search+"%"),
//	                Restrictions.like("tpu.code", "%"+search+"%"),
//	                Restrictions.like("d.code", "%"+search+"%")
//	            ));
//        }
//
//        if (outletTypeId != null && outletTypeId.length > 0) {
//        	criteria.add(Restrictions.in("t.shortCode", outletTypeId));
//        }
//        
//        if (districtId != null && districtId.length > 0) {
//        	criteria.add(Restrictions.in("d.districtId", districtId));
//        }
//        
//        if (tpuId != null && tpuId.length > 0) {
//        	criteria.add(Restrictions.in("tpu.tpuId", tpuId));
//        }
//        
//        
//        criteria.setFirstResult(firstRecord);
//        criteria.setMaxResults(displayLength);
//        criteria.addOrder(order);
//        
//        criteria.setResultTransformer(Transformers.aliasToBean(AssignmentLookupTableList.class));
//        
//        return criteria.list();
	}
	
	public List<Integer> getPlanOutletIds(Integer officerId, Date planDate){
		String hql = "Select distinct o.outletId"
				+ " from Assignment as a"
				+ " inner join a.outlet as o"
				+ " left join a.quotationRecords as qr on "
					+ "	qr.isBackNo = false"
					+ " and qr.isBackTrack = false"
					+ " and (qr.status not in ( :status ) "
					+ "	or qr.status in ( :status ) and qr.availability = 2)"
				+ " left join qr.user as u"
				+ " where qr.assignedCollectionDate = :planDate "
				+ " and u.userId = :userId";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("userId", officerId);
		query.setParameter("planDate", planDate);
		query.setParameterList("status", new String[]{"Submitted", "Approved"});
		
		return query.list();
	}

	public List<Assignment> getPlanAssignment(Integer officerId, List<Integer> outletIds, Date planDate){
		String hql = "Select a "
				+ " from Assignment as a"
				+ " inner join a.outlet as o"
				+ " left join a.quotationRecords as qr on "
					+ "	qr.isBackNo = false"
					+ " and qr.isBackTrack = false"
					+ " and (qr.status not in ( :status ) "
					+ "	or qr.status in ( :status ) and qr.availability = 2)"
				+ " left join qr.user as u"
				+ " where 1=1 and ";
		
		if(planDate!=null){
			hql	+= " ((qr.assignedCollectionDate is null and qr.assignedStartDate <= :planDate )"
					+ " or (qr.assignedCollectionDate is not null and qr.assignedCollectionDate <= :planDate ))";
		}
		
		if(officerId!=null){
			hql += " and u.userId = :userId";
		}
				
		if(outletIds!=null && outletIds.size()>0){
			hql += " and o.outletId in ( :outletIds )";
		}
		
		hql += " order by o.outletId asc";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("status", new String[]{"Submitted", "Approved"});
		
		if(officerId!=null){
			query.setParameter("userId", officerId);
		}
		
		if(planDate!=null){
			query.setParameter("planDate", planDate);
		}
		
		if(outletIds!=null && outletIds.size()>0){
			query.setParameterList("outletIds", outletIds);
		}
		
		return query.list();
	}
	
//	public List<Assignment> getPlanAssignment(Integer officerId, List<Integer> outletIds, Date planDate, Date collectionDate, List<Integer> assignmentIds){
//		String whereClase = "";
//		if (collectionDate != null){
//			whereClase += " and qr.assignedCollectionDate = :collectionDate and (qr.status = 'Draft' or qr.status = 'Blank' or qr.status='Rejected') ";
//		}
//		if (collectionDate == null && planDate != null){
//			whereClase += " and ( "
//						+ "( "
//							+ " ( qr.assignedCollectionDate is null and  qr.assignedStartDate <= :planDate and  qr.assignedEndDate > :planDate) "
//							+ " or (qr.assignedCollectionDate is not null and qr.assignedCollectionDate = :planDate) "
//							+ " or qr.quotationState = 'Verify' "
//						+ ") "
//						+ " and (qr.status = 'Draft' or qr.status = 'Blank' or qr.status='Rejected') "
//						+ " or qr.availability = 2 "
//					+ ")";
//		}
//		if (outletIds != null){
//			whereClase += " and o.outletId in ( :outletIds ) ";
//		}
//		if (officerId != null){
//			whereClase += " and u.userId = :officerId ";
//		}
//		if (assignmentIds != null){
//			whereClase += " and a.assignmentId in (:assignmentIds) ";
//		}
//		String from = " Assignment a "
//                + " inner join fetch a.outlet as o " 
//                + " left join a.quotationRecords as qr "
//                + " left join qr.user as u ";
//		
//		String hql = "select a "
//                + " from "
//                + from
//                + " where 1=1  and qr.isBackTrack = false and qr.isBackNo = false "
//                + whereClase
//                + " order by o.outletId asc";
//		
//		Query query = getSession().createQuery(hql);
//		
//		if (collectionDate != null) {
//			query.setParameter("collectionDate", collectionDate);
//		}
//		if (planDate != null) {
//			query.setParameter("planDate", planDate);
//		}
//		if (officerId != null) {
//			query.setParameter("officerId", officerId);
//		}
//		if (outletIds != null) {
//			query.setParameterList("outletIds", outletIds);
//		}
//		if (assignmentIds != null) {
//			query.setParameterList("assignmentIds", assignmentIds);
//		}
//		
//
//		return query.list();
//	}
		
	public MetricSelectedAssignmentModel countOutletLookupSelectedAssignment(Integer userId, Date planDate, Integer[] selectedOutletIds){
		String [] status = new String[]{"Draft", "Blank", "Rejected"};
		
		String sql = "select count(distinct a.assignmentId) as selectedAssignments, "
				+ " count(distinct qr.quotationRecordId) as selectedQuotations "
				+ "from "
					+ "Assignment as a "
					+ "inner join Outlet as o on o.outletId = a.outletId "
					+ "inner join Tpu as tpu on tpu.tpuId = o.tpuId "
					+ "inner join District as d on d.districtId = tpu.districtId "
					+ "left join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
					+ "left join Quotation as q on qr.QuotationId = q.QuotationId "
					+ "left join Batch as b on q.BatchId = b.BatchId "
					+ "left join [User] as u on u.userId = qr.userId "
					+ "left join OutletTypeOutlet as t on t.outletId = o.outletId "
				+ "where "
					+ "u.userId = :userId and qr.isBackTrack = 0 and qr.isBackNo = 0 and "
					+ "( qr.assignedCollectionDate is null and qr.assignedStartDate <= :planDate "
						+ "and qr.status in (:status) "
						+ "or "
						+ "qr.assignedCollectionDate is not null and qr.assignedCollectionDate <= :planDate "
						+ "and qr.status in (:status) "
						+ "or "
						+ "qr.availability = 2 "
						+ "or "
						+ "qr.quotationState = 'Verify' "
						+ "and qr.status in (:status) "
					+ ")";
		
		if (selectedOutletIds != null){
			sql += " and o.outletId in (:selectedOutletIds) ";
		} else {
			sql += " and o.outletId is null";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("planDate", planDate);
		query.setParameterList("status", status);
		
		if (selectedOutletIds != null){
			query.setParameterList("selectedOutletIds", selectedOutletIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(MetricSelectedAssignmentModel.class));
		
		query.addScalar("selectedAssignments", StandardBasicTypes.LONG);
		query.addScalar("selectedQuotations", StandardBasicTypes.LONG);
		
        return (MetricSelectedAssignmentModel)query.uniqueResult();
	}
	
	public long countOutletLookupTableList(String search, Integer userId,
			String[] outletTypeId, Integer[] districtId, Integer[] tpuId, Date planDate, Integer[] excludedOutletIds) {
//			String endDateFormat = String.format("FORMAT(endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//			String endDateFormat = String.format(" when qr.assignedCollectionDate is null then FORMAT(qr.assignedEndDate, '%1$s', 'en-us') else FORMAT(qr.assignedCollectionDate, '%1$s', 'en-us') end", SystemConstant.DATE_FORMAT);
//			String convenientTime = String.format("dbo.FormatTime(o.convenientStartTime, '%1$s')+'-'+dbo.FormatTime(o.convenientEndTime, '%1$s')", SystemConstant.TIME_FORMAT);
//			String convenientTime = String.format("dbo.FormatTime(o.convenientStartTime, '%1$s')+'-'+dbo.FormatTime(o.convenientEndTime, '%1$s')", SystemConstant.TIME_FORMAT);	
			
			String endDateFormat = String.format(" when min(qr.assignedCollectionDate) is null then FORMAT(min(qr.assignedEndDate), '%1$s', 'en-us') else FORMAT(min(qr.assignedCollectionDate), '%1$s', 'en-us') end", SystemConstant.DATE_FORMAT);
			String [] status = new String[]{"Draft", "Blank", "Rejected"};
			
			String sql = "select CONCAT(d.code, o.firmCode) as referenceNo, "
					+ "b.code as batchCode, "
					+ "o.outletId as outletId, "
					+ "o.name as firm, "
					+ "d.code as district,"
					+ "tpu.code as tpu, "
					+ "o.streetAddress as address, "
					+ "o.convenientStartTime as convenientStartTime, "
					+ "o.convenientEndTime as convenientEndTime, o.remark as outletRemark, count(distinct quotationRecordId) as noOfQuotation, "
					+ "case when min(qr.assignedCollectionDate) is null and min(qr.assignedEndDate) is null then '' " + endDateFormat + " as deadline "
					+ "from "
						+ "Assignment as a "
						+ "inner join Outlet as o on o.outletId = a.outletId "
						+ "inner join Tpu as tpu on tpu.tpuId = o.tpuId "
						+ "inner join District as d on d.districtId = tpu.districtId "
						+ "left join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
						+ "left join Quotation as q on qr.QuotationId = q.QuotationId "
						+ "left join Batch as b on q.BatchId = b.BatchId "
						+ "left join [User] as u on u.userId = qr.userId "
						+ "left join OutletTypeOutlet as t on t.outletId = o.outletId "
					+ "where "
						+ "u.userId = :userId and qr.isBackTrack = 0 and qr.isBackNo = 0 and "
						+ "( qr.assignedCollectionDate is null and qr.assignedStartDate <= :planDate "
							+ "and qr.status in (:status) "
							+ "or "
							+ "qr.assignedCollectionDate is not null and qr.assignedCollectionDate <= :planDate "
							+ "and qr.status in (:status) "
							+ "or "
							+ "qr.availability = 2 "
							+ "or "
							+ "qr.quotationState = 'Verify' "
							+ "and qr.status in (:status) "
						+ ")";
			
			if (excludedOutletIds != null && excludedOutletIds.length > 0){
				sql += " and o.outletId not in (:excludedOutletIds) ";
	        }
	        
	        if (StringUtils.isNotEmpty(search)) {
	        	sql += " and ( "
	        			+ " o.name like :search  "
	        			+ " or o.streetAddress like :search "
	        			+ " or tpu.code like :search "
	        			+ " or d.code like :search "
	        			+ " or CONCAT(d.code, o.firmCode) like :search "
	        			+ " or b.code like :search "
	        			+ " ) ";
	        }

	        if (outletTypeId != null && outletTypeId.length > 0) {
	        	sql += " and t.shortCode in (:outletTypeId) ";
	        }
	        
	        if (districtId != null && districtId.length > 0) {
	        	sql += " and d.districtId in (:districtId) ";
	        }
	        
	        if (tpuId != null && tpuId.length > 0) {
	        	sql += " and tpu.tpuId in (:tpuId) ";
	        }
			
//	        sql += " group by o.outletId, o.name, d.code, tpu.code, o.streetAddress, o.convenientStartTime, "
//	        		+ " o.convenientEndTime, o.remark, qr.assignedCollectionDate ";
	        sql += " group by o.outletId, o.firmCode, o.name, d.code, tpu.code, o.streetAddress, o.convenientStartTime, "
	        		+ " o.convenientEndTime, o.remark, b.code ";
	       	        		
	        sql = "select count(*) as cnt from (" + sql + ") as a ";
	        
	        SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("userId", userId);
			query.setParameter("planDate", planDate);
			query.setParameterList("status", status);
			
			if (excludedOutletIds != null && excludedOutletIds.length > 0){
				query.setParameterList("excludedOutletIds", excludedOutletIds);
	        }
	        
	        if (StringUtils.isNotEmpty(search)) {
	        	query.setParameter("search", "%"+search+"%");
	        }

	        if (outletTypeId != null && outletTypeId.length > 0) {
	        	query.setParameterList("outletTypeId", outletTypeId);
	        }
	        
	        if (districtId != null && districtId.length > 0) {
	        	query.setParameterList("districtId", districtId);
	        }
	        
	        if (tpuId != null && tpuId.length > 0) {
	        	query.setParameterList("tpuId", tpuId);
	        }
	        
	        query.addScalar("cnt", StandardBasicTypes.LONG);
			
	        return (long)query.uniqueResult();
			
//			Criteria criteria = this.createCriteria("a")
//					.createAlias("a.outlet", "o", JoinType.INNER_JOIN)
//	                .createAlias("o.tpu", "tpu", JoinType.INNER_JOIN)
//	                .createAlias("tpu.district", "d", JoinType.INNER_JOIN)
//	                .createAlias("a.quotationRecords", "qr",JoinType.LEFT_OUTER_JOIN)
//	                .createAlias("qr.user", "u",JoinType.LEFT_OUTER_JOIN)
//	                .createAlias("o.outletTypes", "t", JoinType.LEFT_OUTER_JOIN);
//	                //.createAlias("o.quotations", "q", JoinType.LEFT_OUTER_JOIN);
//	        
//	        ProjectionList projList = Projections.projectionList();
//	        SQLProjectionExt project = SQLProjectionExt.groupCount(
//	        		Projections.groupProperty("o.outletId"),
//	        		Projections.groupProperty("o.name"),
//	        		Projections.groupProperty("d.code"),
//	        		Projections.groupProperty("tpu.code"),
//	        		Projections.groupProperty("o.streetAddress"),
//	        		//SQLProjectionExt.sqlProjection(endDateFormat+" as deadline", endDateFormat, new String [] {"deadline"}, new Type[]{StandardBasicTypes.STRING}),
//	        		Projections.groupProperty("o.convenientStartTime"),
//	        		Projections.groupProperty("o.convenientEndTime"),
//	        		SQLProjectionExt.sqlProjection(convenientTime+" as convenientTime", convenientTime, new String [] {"convenientTime"}, new Type[]{StandardBasicTypes.STRING}),
//	        		Projections.groupProperty("o.remark")
//	        );
	        
	        /*
	        projList.add(Projections.groupProperty("o.outletId"), "outletId");
	        projList.add(Projections.groupProperty("o.name"), "firm");
	        projList.add(Projections.groupProperty("d.code"), "district");
	        projList.add(Projections.groupProperty("tpu.code"), "tpu");
	        projList.add(Projections.groupProperty("o.streetAddress"), "address");
	        projList.add(SQLProjectionExt.sqlProjection(endDateFormat+" as deadline", endDateFormat, new String [] {"deadline"}, new Type[]{StandardBasicTypes.STRING}), "deadline");
	        projList.add(Projections.groupProperty("o.convenientStartTime"), "convenientStartTime");
	        projList.add(Projections.groupProperty("o.convenientEndTime"), "convenientEndTime");
	        projList.add(SQLProjectionExt.sqlProjection(convenientTime+" as convenientTime", convenientTime, new String [] {"convenientTime"}, new Type[]{StandardBasicTypes.STRING}), "convenientTime");
	        
	        projList.add(Projections.groupProperty("o.remark"), "outletRemark");
	        projList.add(Projections.countDistinct("qr.quotationRecordId"), "noOfQuotation");
	        */
//	        projList.add(project);
//	        criteria.setProjection(projList);
//	        
//	        criteria.add(Restrictions.eq("u.userId", userId));
//	        
//	        String [] status = new String[]{"Draft", "Blank", "Rejected"};
//	        
//	        criteria.add(Restrictions.or(
//		    		Restrictions.and(
//						Restrictions.isNull("qr.assignedCollectionDate"),
//						Restrictions.ge("qr.assignedEndDate", planDate),
//						Restrictions.le("qr.assignedStartDate", planDate),
//						Restrictions.in("qr.status", status)
//					),
//					Restrictions.and(
//						Restrictions.isNotNull("qr.assignedCollectionDate"),
//						Restrictions.eq("qr.assignedCollectionDate", planDate),
//						Restrictions.in("qr.status", status)
//					),
//					Restrictions.eq("qr.availability", 2),
//					Restrictions.and(
//						Restrictions.eq("qr.quotationState", "Verify"),
//						Restrictions.in("qr.status", status)
//					)
//				)
////		    		Restrictions.or(
////		    			Restrictions.eq("qr.quotationState", "Revisit"),
////		    			Restrictions.eq("qr.quotationState", "Verify"))
////		    		)
//	        );
//	  
////		        Restrictions.or(Restrictions.eq("qr.status", "Draft"),Restrictions.eq("qr.status", "Blank"),Restrictions.eq("qr.status", "Rejected"))
//	        	
//	        if (excludedOutletIds != null && excludedOutletIds.length > 0){
//	        	criteria.add(Restrictions.not(Restrictions.in("o.outletId", excludedOutletIds)));
//	        }
//	        
//	        if (StringUtils.isNotEmpty(search)) {
//		        criteria.add(Restrictions.or(
//		                Restrictions.like("o.name", "%"+search+"%"),
//		                Restrictions.like("o.streetAddress", "%"+search+"%"),
//		                Restrictions.like("tpu.code", "%"+search+"%"),
//		                Restrictions.like("d.code", "%"+search+"%")
//		            ));
//	        }
//
//	        if (outletTypeId != null && outletTypeId.length > 0) {
//	        	criteria.add(Restrictions.in("t.shortCode", outletTypeId));
//	        }
//	        
//	        if (districtId != null && districtId.length > 0) {
//	        	criteria.add(Restrictions.in("d.districtId", districtId));
//	        }
//	        
//	        if (tpuId != null && tpuId.length > 0) {
//	        	criteria.add(Restrictions.in("tpu.tpuId", tpuId));
//	        }
//	        
//	        return (long) criteria.uniqueResult();
		
		
		/*
		
		Criteria criteria = this.createCriteria("a")
				.createAlias("a.outlet", "o", JoinType.INNER_JOIN)
                .createAlias("o.tpu", "tpu", JoinType.INNER_JOIN)
                .createAlias("tpu.district", "d", JoinType.INNER_JOIN)
                .createAlias("a.quotationRecords", "qr",JoinType.LEFT_OUTER_JOIN)
                .createAlias("o.outletTypes", "t", JoinType.LEFT_OUTER_JOIN)
                .createAlias("o.quotations", "q", JoinType.LEFT_OUTER_JOIN);
        
        ProjectionList projList = Projections.projectionList();
        
        SQLProjectionExt project = SQLProjectionExt.groupCount(
        		Projections.groupProperty("o.outletId"),
        		Projections.groupProperty("o.name"),
        		Projections.groupProperty("tpu.code"),
        		Projections.groupProperty("o.streetAddress"),
        		Projections.groupProperty("endDate")		
		);
        
        projList.add(project);
        criteria.setProjection(projList);
        
        criteria.add(Restrictions.eq("a.user.userId", userId));
        
        criteria.add(Restrictions.or(
    		Restrictions.and(
				Restrictions.isNull("a.assignedCollectionDate"),
				Restrictions.ge("a.endDate", planDate),
				Restrictions.le("a.startDate", planDate)
			),
    		Restrictions.or(
    			Restrictions.eq("qr.quotationState", "Revisit"),
    			Restrictions.eq("qr.quotationState", "Verify"))
    		)
        );
        
        if (StringUtils.isNotEmpty(search)) {
	        criteria.add(Restrictions.or(
	                Restrictions.like("o.name", "%"+search+"%"),
	                Restrictions.like("o.streetAddress", "%"+search+"%"),
	                Restrictions.like("tpu.code", "%"+search+"%"),
	                Restrictions.like("d.code", "%"+search+"%")
	            ));
        }
        
    	
        if (excludedOutletIds != null && excludedOutletIds.length > 0){
        	criteria.add(Restrictions.not(Restrictions.in("o.outletId", excludedOutletIds)));
        }

        if (outletTypeId != null && outletTypeId.length > 0) {
        	criteria.add(Restrictions.in("t.shortCode", outletTypeId));
        }
        
        if (districtId != null && districtId.length > 0) {
        	criteria.add(Restrictions.in("d.districtId", districtId));
        }
        
        if (tpuId != null && tpuId.length > 0) {
        	criteria.add(Restrictions.in("tpu.tpuId", tpuId));
        }
        
        return (long) criteria.uniqueResult();*/
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentLookupTableList> getBuildingTableList(String search,
			int firstRecord, int displayLength, Order order, Integer userId,
			Integer[] districtId, Integer[] tpuId, String[] survey, Integer[] surveyMonthId, Date planDate, Integer[] excludedAssignmentIds) {

		String endDateFormat = String.format("FORMAT({s}.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		Criteria criteria = this.createCriteria("a")
				.createAlias("a.surveyMonth", "s", JoinType.LEFT_OUTER_JOIN)
                .createAlias("a.additionalTpu", "tpu", JoinType.INNER_JOIN)
                .createAlias("a.additionalDistrict", "d", JoinType.INNER_JOIN);

        
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.property("a.assignmentId"), "assignmentId");
        projList.add(Projections.property("a.additionalFirmName"), "firm");
        projList.add(Projections.property("d.code"), "district");
        projList.add(Projections.property("tpu.code"), "tpu");
        projList.add(Projections.property("a.additionalFirmAddress"), "address");
        projList.add(Projections.property("a.referenceNo"), "referenceNo");
        projList.add(SQLProjectionExt.sqlProjection("case when {s}.endDate is null then '' else "+endDateFormat+" end as deadline", new String [] {"deadline"}, new Type[]{StandardBasicTypes.STRING}), "deadline");

        criteria.setProjection(projList);
        criteria.add(Restrictions.eq("a.isCompleted", false));
        criteria.add(Restrictions.eq("a.user.userId", userId));
        //criteria.add(Restrictions.)
        
//        criteria.add(Restrictions.or(
//    		Restrictions.and(
//				Restrictions.isNull("a.assignedCollectionDate"),
//				Restrictions.ge("a.endDate", planDate),
//				Restrictions.le("a.startDate", planDate)
//			),
//    		Restrictions.or(
//    			Restrictions.eq("qr.quotationState", "Revisit"),
//    			Restrictions.eq("qr.quotationState", "Verify"))
//    		)
//        );
        
        criteria.add(Restrictions.ge("s.endDate", planDate));
       // criteria.add(Restrictions.in("qr.status", new String[]{"Draft","Blank","Rejected"}));
       // criteria.add(Restrictions.or(Restrictions.eq("qr.status", "Draft"),Restrictions.eq("qr.status", "Blank")));
        
        
        if (StringUtils.isNotEmpty(search)) {
	        criteria.add(Restrictions.or(
	                Restrictions.like("a.additionalFirmName", "%"+search+"%"),
	                Restrictions.like("a.additionalFirmAddress", "%"+search+"%"),
	                Restrictions.like("tpu.code", "%"+search+"%"),
	                Restrictions.like("d.code", "%"+search+"%"),
	                Restrictions.like("a.referenceNo", "%"+search+"%")
	            ));
        }
        
        if (excludedAssignmentIds != null && excludedAssignmentIds.length > 0) {
        	criteria.add(Restrictions.not(Restrictions.in("a.assignmentId", excludedAssignmentIds)));
        }
        
        if (districtId != null && districtId.length > 0) {
        	criteria.add(Restrictions.in("d.districtId", districtId));
        }
        
        if (tpuId != null && tpuId.length > 0) {
        	criteria.add(Restrictions.in("tpu.tpuId", tpuId));
        }
        
        if (survey != null && survey.length > 0) {
        	criteria.add(Restrictions.in("a.survey", survey));
        }
        
        if (surveyMonthId != null && surveyMonthId.length > 0) {
        	criteria.add(Restrictions.in("s.surveyMonthId", surveyMonthId));
        }
        
        criteria.setFirstResult(firstRecord);
        criteria.setMaxResults(displayLength);
        criteria.addOrder(order);
        
        criteria.setResultTransformer(Transformers.aliasToBean(AssignmentLookupTableList.class));
        
        return criteria.list();
	}

	
	public long countBuildingLookupTableList(String search, Integer userId,
			Integer[] districtId, Integer[] tpuId, String[] survey, Integer[] surveyMonthId, Date planDate, Integer[] excludedAssignmentIds) {

		
		Criteria criteria = this.createCriteria("a")
				.createAlias("a.surveyMonth", "s", JoinType.LEFT_OUTER_JOIN)
                .createAlias("a.additionalTpu", "tpu", JoinType.INNER_JOIN)
                .createAlias("a.additionalDistrict", "d", JoinType.INNER_JOIN);
                //.createAlias("a.quotationRecords", "qr",JoinType.LEFT_OUTER_JOIN);

        
       criteria.setProjection(Projections.rowCount());
       
        criteria.add(Restrictions.eq("a.isCompleted", false));
        criteria.add(Restrictions.eq("a.user.userId", userId));
        

        criteria.add(Restrictions.ge("s.endDate", planDate));
        
       //criteria.add(Restrictions.eq("a.user.userId", userId));
        
//        criteria.add(Restrictions.or(
//    		Restrictions.and(
//				Restrictions.isNull("a.assignedCollectionDate"),
//				Restrictions.ge("a.endDate", planDate),
//				Restrictions.le("a.startDate", planDate)
//			),
//    		Restrictions.or(
//    			Restrictions.eq("qr.quotationState", "Revisit"),
//    			Restrictions.eq("qr.quotationState", "Verify"))
//    		)
//        );
        
        if (StringUtils.isNotEmpty(search)) {
        	criteria.add(Restrictions.or(
	                Restrictions.like("a.additionalFirmName", "%"+search+"%"),
	                Restrictions.like("a.additionalFirmAddress", "%"+search+"%"),
	                Restrictions.like("tpu.code", "%"+search+"%"),
	                Restrictions.like("d.code", "%"+search+"%"),
	                Restrictions.like("a.referenceNo", "%"+search+"%")
	            ));
        }
        
        if (excludedAssignmentIds != null && excludedAssignmentIds.length > 0) {
        	criteria.add(Restrictions.not(Restrictions.in("a.assignmentId", excludedAssignmentIds)));
        }
        
        if (districtId != null && districtId.length > 0) {
        	criteria.add(Restrictions.in("d.districtId", districtId));
        }
        
        if (tpuId != null && tpuId.length > 0) {
        	criteria.add(Restrictions.in("tpu.tpuId", tpuId));
        }
        
        if ( survey!= null && survey.length > 0) {
        	criteria.add(Restrictions.in("a.survey", survey));
        }
        
        if ( surveyMonthId != null && surveyMonthId.length > 0) {
        	criteria.add(Restrictions.in("s.surveyMonthId", surveyMonthId));
        }
        
        return (long) criteria.uniqueResult();
	}
	
//	public List<ExportAssignmentAllocationList> getAllAssignmentResult(Date referenceMonth){
//		String hql = "Select a.assignmentId as assignmentId"
////				+ ", a.survey as survey"
//				+ ", CASE WHEN a.survey IS NULL THEN " 
//				+ "	STUFF((SELECT '; ' + CAST( pp.survey AS VARCHAR(100)) [text()] FROM " 
//				+ " Assignment as ass " 
//				+ " LEFT JOIN ass.quotationRecord as qr1 "
//				+ " LEFT JOIN qr1.quotation as q "
//				+ " LEFT JOIN q.unit as un "
//				+ " LEFT JOIN un.purpose as pp "
//				+ " WHERE ass.assignmentId= a.assignmentId "
//				+ " GROUP BY pp.survey "
//				+ " FOR XML PATH(''), TYPE).value('.','NVARCHAR(MAX)'),1,2,' ')"
//				+ "	ELSE a.survey END as survey"
//				
//				+ ", a.isImportedAssignment as isImportedAssignment"
//				+ ", sm.referenceMonth as referenceMonth"
//				+ ", a.startDate as startDate"
//				+ ", a.endDate as endDate"
//				+ ", a.assignedCollectionDate as assignedCollectionDate"
////				+ ", o.firmCode as firmCode"  
//				+ ", case when a.survey IN ('GHS','BMWPS') then a.referenceNo"
//					+ " else o.firmCode end as firmCode"
////				+ ", o.name as outletName"
//				+ ", case when a.survey IN ('GHS','BMWPS') then a.additionalFirmName"
//				+ " else o.name end as outletName"
////				+ ", o.outletTypes as outletTypes"
//				+ ", case when a.isImportedAssignment = true then ad.code"
//					+ " else d.code end as districtCode"
//				+ ", case when a.isImportedAssignment = true then at.code"
//					+ " else t.code end as tpuCode"
////				+ ", o.streetAddress as address"
//				+ ", case when a.survey IN ('GHS','BMWPS') then a.additionalFirmAddress"
//				+ " else o.streetAddress end as address"
//				+ ", o.collectionMethod as collectionMethod"
//				+ ", o.outletId as outletId"
//				+ ", o.outletMarketType as outletMarketType"
////				+ ", qr.quotationRecordId as quotationRecordId"
//				+ ", count(qr.quotationRecordId) as numOfQuotation"
//				+ ", a.status as firmStatus"
//				+ ", a.isCompleted as isCompleted"
//				+ ", u.staffCode as staffCode"
//				+ ", b.code as batchCode"
//				+ " from Assignment as a"
//				+ " left join a.quotationRecords as qr"
//				+ " left join qr.quotation as q"
//				+ " left join q.batch as b"
//				+ " left join a.surveyMonth as sm"
//				+ " left join a.outlet as o"
//				+ " left join a.additionalDistrict as ad"
//				+ " left join a.additionalTpu as at"
//				+ " left join a.user as u"
//				+ " left join o.tpu as t"
//				+ " left join t.district as d"
//				+ " where sm.referenceMonth = :referenceMonth"
////				+ " and qr.status in ( :status )"
//				+ " and (a.isImportedAssignment = true "
//				+ " or ( qr.status in ( :status )"
//				+ " or qr.quotationState = 'IP' or qr.availability = 2 ))"
//				+ " group by a.assignmentId"
//				+ ", a.survey"
//				+ ", a.isImportedAssignment"
//				+ ", sm.referenceMonth"
//				+ ", a.startDate"
//				+ ", a.endDate"
//				+ ", a.assignedCollectionDate"
//				+ ", a.referenceNo"
//				+ ", o.firmCode"
//				+ ", o.name"
//				+ ", a.additionalFirmName"
//				+ ", o.outletId"
////				+ ", o.outletTypes"
//				+ ", d.code"
//				+ ", t.code"
//				+ ", ad.code"
//				+ ", at.code"
//				+ ", o.streetAddress"
//				+ ", a.additionalFirmAddress"
//				+ ", o.collectionMethod"
//				+ ", o.outletMarketType"
//				+ ", a.status"
//				+ ", a.isCompleted"
//				+ ", u.staffCode"
//				+ ", b.code";
//		
//		Query query = this.getSession().createQuery(hql);
//		
//		query.setParameter("referenceMonth", referenceMonth);
//		String[] qrStatus = new String[]{"Blank","Draft","Rejected"};
//		query.setParameterList("status", qrStatus);
//		
//		query.setResultTransformer(Transformers.aliasToBean(ExportAssignmentAllocationList.class));
//		
//		return query.list();
//	}
	
	@SuppressWarnings("unchecked")
	public List<ExportAssignmentAllocationList> getAllAssignmentResult(Date referenceMonth){
		String sql = "Select a.AssignmentId as assignmentId"
				+ ", CASE WHEN a.Survey IS NULL THEN " 
					+ "	STUFF((SELECT '; ' + CAST( pp.Survey AS VARCHAR(100)) [text()] FROM " 
					+ " Assignment ass " 
					+ " LEFT JOIN QuotationRecord qr1 ON qr1.AssignmentId = ass.AssignmentId"
					+ " LEFT JOIN Quotation q ON q.QuotationId = qr1.QuotationId and (qr1.IsBackNo=0) "
					+ " LEFT JOIN Unit un ON un.UnitId = q.UnitId "
					+ " LEFT JOIN Purpose pp ON pp.PurposeId = un.PurposeId "
					+ " WHERE ass.AssignmentId= a.AssignmentId "
					+ " GROUP BY pp.Survey "
					+ " FOR XML PATH(''), TYPE).value('.','NVARCHAR(MAX)'),1,2,' ')"
				+ "	ELSE a.Survey END as survey"
				
				+ ", a.IsImportedAssignment as isImportedAssignment"
				+ ", sm.ReferenceMonth as referenceMonth"
				+ ", a.StartDate as startDate"
				+ ", a.EndDate as endDate"
				+ ", a.AssignedCollectionDate as assignedCollectionDate"
//				+ ", o.firmCode as firmCode"  
				+ ", case when a.Survey IN ('GHS','BMWPS', 'Others') then a.ReferenceNo"
					+ " else  CONVERT(NVARCHAR(MAX),o.FirmCode) end as referencefirmCode"
//				+ ", o.name as outletName"
				+ ", case when a.Survey IN ('GHS','BMWPS') then a.AdditionalFirmName"
				+ " else o.Name end as outletName"
//				+ ", o.outletTypes as outletTypes"
				+ ", case when a.IsImportedAssignment = 1 then ad.Code"
					+ " else d.Code end as districtCode"
				+ ", case when a.IsImportedAssignment = 1 then at.Code"
					+ " else t.Code end as tpuCode"
//				+ ", o.streetAddress as address"
				+ ", case when a.Survey IN ('GHS','BMWPS') then a.AdditionalFirmAddress"
				+ " else o.StreetAddress end as address"
				+ ", o.CollectionMethod as collectionMethod"
				+ ", o.OutletId as outletId"
				+ ", o.OutletMarketType as outletMarketType"
//				+ ", qr.quotationRecordId as quotationRecordId"
				+ ", count(qr.QuotationRecordId) as numOfQuotation"
				+ ", a.Status as firmStatus"
				+ ", a.IsCompleted as isCompleted"
				+ ", u.StaffCode as staffCode"
				+ ", b.Code as batchCode"
				+ " from Assignment a"
				+ " left join QuotationRecord qr ON a.AssignmentId = qr.AssignmentId"
				+ " left join Quotation q ON q.QuotationId = qr.QuotationId"
				+ " left join Batch b ON b.BatchId = q.BatchId"
				+ " left join SurveyMonth sm ON sm.SurveyMonthId = a.SurveyMonthId"
				+ " left join Outlet o ON o.OutletId = a.OutletId"
				+ " left join District ad ON ad.DistrictId = a.AdditionalDistrictId "
				+ " left join Tpu at ON at.TpuId = a.AdditionalTpuId"
				+ " left join [User] u ON u.UserId = a.UserId"
				+ " left join Tpu t ON o.TpuId = t.TpuId"
				+ " left join District d ON d.DistrictId = t.DistrictId"
				+ " where sm.ReferenceMonth = :referenceMonth"
//				+ " and qr.status in ( :status )"
				+ " and (a.IsImportedAssignment = 1 "
				+ " or ( qr.Status in ( :status )"
				+ " or qr.QuotationState = 'IP' or qr.Availability = 2 ))"
				+ " group by a.AssignmentId"
				+ ", a.Survey"
				+ ", a.IsImportedAssignment"
				+ ", sm.ReferenceMonth"
				+ ", a.StartDate"
				+ ", a.EndDate"
				+ ", a.AssignedCollectionDate"
				+ ", a.ReferenceNo"
				+ ", o.FirmCode"
				+ ", o.Name"
				+ ", a.AdditionalFirmName"
				+ ", o.OutletId"
//				+ ", o.outletTypes"
				+ ", d.Code"
				+ ", t.Code"
				+ ", ad.Code"
				+ ", at.Code"
				+ ", o.StreetAddress"
				+ ", a.AdditionalFirmAddress"
				+ ", o.CollectionMethod"
				+ ", o.OutletMarketType"
				+ ", a.Status"
				+ ", a.IsCompleted"
				+ ", u.StaffCode"
				+ ", b.Code";
		
//		Query query = this.getSession().createQuery(hql);
		SQLQuery query = getSession().createSQLQuery(sql);

		query.addScalar("assignmentId", StandardBasicTypes.INTEGER)
		.addScalar("survey", StandardBasicTypes.STRING)
		.addScalar("isImportedAssignment", StandardBasicTypes.BOOLEAN)
		.addScalar("referenceMonth", StandardBasicTypes.DATE)
		.addScalar("startDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("endDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("assignedCollectionDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("referencefirmCode", StandardBasicTypes.STRING)
		.addScalar("outletName", StandardBasicTypes.STRING)
		.addScalar("districtCode", StandardBasicTypes.STRING)
		.addScalar("tpuCode", StandardBasicTypes.STRING)
		.addScalar("address", StandardBasicTypes.STRING)
		.addScalar("collectionMethod", StandardBasicTypes.INTEGER)
		.addScalar("outletId", StandardBasicTypes.INTEGER)
		.addScalar("outletMarketType", StandardBasicTypes.INTEGER)
		.addScalar("numOfQuotation", StandardBasicTypes.LONG)
		.addScalar("firmStatus", StandardBasicTypes.INTEGER)
		.addScalar("isCompleted", StandardBasicTypes.BOOLEAN)
		.addScalar("staffCode",StandardBasicTypes.STRING)
		.addScalar("batchCode",StandardBasicTypes.STRING);


		String[] qrStatus = new String[]{"Blank","Draft","Rejected"};
		query.setResultTransformer(Transformers.aliasToBean(ExportAssignmentAllocationList.class))
				.setParameter("referenceMonth", referenceMonth)
				.setParameterList("status", qrStatus).list();

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Assignment> getByIds(Integer[] ids) {
		if (ids.length == 0) return new ArrayList<Assignment>();
		return this.createCriteria()
				.add(Restrictions.in("assignmentId", ids)).list();
	}
	
	public List<Assignment> findAssignmentBySurveyMonth(SurveyMonth surveyMonth){
		
		
		Criteria criteria = this.createCriteria("a");
		
		criteria.add(Restrictions.eq("surveyMonth", surveyMonth));
		
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<AssignmentLookupSurveyMonthTableList> getAssignmentLookupSurveyMonthTableList(String search, int firstRecord, int displayLength, Order order, 
			Integer surveyMonthId, Integer officerId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select a.assignmentId as id "
				+ ", a.referenceNo as referenceNo "
				+ ", case "
				+ "	when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end  as collectionDate "
				+ ", case "
				+ "	when a.startDate is null then '' "
				+ " else " + startDate + " end  as startDate "
				+ ", case "
				+ "	when a.endDate is null then '' "
				+ " else " + endDate + " end  as endDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", b.code as batchCode "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ " from Assignment as a "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join a.surveyMonth as sm "
				+ " where 1=1 ";
		
		hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if(officerId != null && officerId > 0) {
			hql += " and a.user.userId = :officerId ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or a.referenceNo like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		hql += " group by "
			+ " a.assignmentId, a.referenceNo "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code, b.code ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if(surveyMonthId != null) query.setParameter("surveyMonthId", surveyMonthId);
		
		if(officerId != null && officerId > 0) query.setParameter("officerId", officerId);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentLookupSurveyMonthTableList.class));
		
		return query.list();
	}

	public long countAssignmentLookupSurveyMonthTableList(String search, 
			Integer surveyMonthId, Integer officerId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select count( distinct a.assignmentId ) as cnt "
				+ " from Assignment as a "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join a.surveyMonth as sm "
				+ " where 1=1 ";
		
		hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if(officerId != null && officerId > 0) {
			hql += " and a.user.userId = :officerId ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or a.referenceNo like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if(surveyMonthId != null) query.setParameter("surveyMonthId", surveyMonthId);
		
		if(officerId != null && officerId > 0) query.setParameter("officerId", officerId);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (long)query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Assignment> searchAssignmentBySurveyMonthId(String search, int firstRecord, int displayLength, Integer surveyMonthId, Integer fieldOfficerId) {

		Criteria criteria = this.createCriteria("a")
								//.createAlias("a.quotationRecords", "qr", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("qr.quotation", "q", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("q.batch", "b", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("a.outlet", "o", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("o.tpu", "t", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("t.district", "d", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.user", "u", JoinType.LEFT_OUTER_JOIN)
								.setFirstResult(firstRecord)
								.setMaxResults(displayLength)
								.addOrder(Order.asc("a.referenceNo"));

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.sqlRestriction("{alias}.referenceNo LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		if (surveyMonthId != null && surveyMonthId > 0) {
			criteria.add(Restrictions.eq("sm.surveyMonthId", surveyMonthId));
		}
		
		if (fieldOfficerId != null && fieldOfficerId > 0) {
			criteria.add(Restrictions.eq("u.userId", fieldOfficerId));
		}
		
		return criteria.list();
	}

	public long countSearchAssignmentBySurveyMonthId(String search, Integer surveyMonthId, Integer fieldOfficerId) {

		Criteria criteria = this.createCriteria("a")
								//.createAlias("a.quotationRecords", "qr", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("qr.quotation", "q", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("q.batch", "b", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("a.outlet", "o", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("o.tpu", "t", JoinType.LEFT_OUTER_JOIN)
								//.createAlias("t.district", "d", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.user", "u", JoinType.LEFT_OUTER_JOIN);

		if (!StringUtils.isEmpty(search)) {
			criteria.add(
				Restrictions.sqlRestriction("{alias}.referenceNo LIKE (?)", "%" + search + "%", StandardBasicTypes.STRING)
			);
		}
		
		if (surveyMonthId != null && surveyMonthId > 0) {
			criteria.add(Restrictions.eq("sm.surveyMonthId", surveyMonthId));
		}
		
		if (fieldOfficerId != null && fieldOfficerId > 0) {
			criteria.add(Restrictions.eq("u.userId", fieldOfficerId));
		}
		
		return (long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitEditModel.AssignmentWithSurveyMonth> getAssignmentWithSurveyMonthById(Integer assignmentId){

		Criteria criteria = this.createCriteria();
		
		ProjectionList list = Projections.projectionList();
		list.add(Projections.property("assignmentId"), "assignmentId")
			.add(Projections.property("referenceNo"), "referenceNo");
		
		criteria.setProjection(list);
		if (assignmentId != null && assignmentId > 0){
			criteria.add(Restrictions.eq("assignmentId", assignmentId));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(SupervisoryVisitEditModel.AssignmentWithSurveyMonth.class));
		
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<SelectedAssignmentModel> getAssignmentsForMaintenance(List<Integer> ids) {
		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select a.id as id, "
				+ " outlet.name as firm, "
				+ " district.chineseName as district, "
				+ " tpu.code as tpu, "
				+ " outlet.detailAddress as address, "
				+ " case when a.startDate is null then '' else " + startDateFormat + " end as startDate, "
				+ " case when a.endDate is null then '' else " + endDateFormat + " end as endDate, "
				+ " count(distinct qr.id) as noOfQuotation, "
				+ " sum(q.quotationLoading) as requiredManDay, "
				+ " isnull(sum(case when u.seasonality in (1,4) then q.quotationLoading end),0) as fullSeasonLoading, "
				+ " isnull(sum(case when u.seasonality = 2 then q.quotationLoading end),0) as summerLoading, "
				+ " isnull(sum(case when u.seasonality = 3 then q.quotationLoading end),0) as winterLoading, "
				+ " a.user.id as userId "
                + " from Assignment as a "
                + " left join a.quotationRecords as qr "
                + " left join qr.quotation as q "
                + " left join q.unit as u "
                + " left join a.outlet as outlet "
                + " left join outlet.tpu as tpu "
                + " left join tpu.district as district "
                + " where a.id in :ids "
                + " and qr.isBackNo = 0 "
                + " and qr.isBackTrack = 0 ";
		
		hql += " group by a.id, outlet.name, district.chineseName, tpu.code, outlet.detailAddress, a.startDate, a.endDate, a.user.id ";

		Query query = getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);

		query.setResultTransformer(Transformers.aliasToBean(SelectedAssignmentModel.class));
		return query.list();
	}

	public long countQuotations(List<Integer> ids) {
		String hql = "select count(distinct qr.id) "
                + " from Assignment as a "
                + " left join a.quotationRecords as qr"
                + " where a.id in :ids ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<SelectedAssignmentModel> getTransferInOutMaintenanceTableList(String search,
			int firstRecord, int displayLength, Order order,
			List<Integer> excludeAssignmentIds,
			int allocationBatchId, Integer fromUserId,
			Integer[] tpuId, String outletTypeId, Integer districtId, Integer batchId, List<Integer> selectedAssignmentIds) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select a.id as id, "
				+ " outlet.name as firm, "
				+ " district.code as district, "
				+ " tpu.code as tpu, "
				+ " outlet.detailAddress as address, "
				+ " case when a.startDate is null then '' else " + startDateFormat + " end as startDate, "
				+ " case when a.endDate is null then '' else " + endDateFormat + " end as endDate, "
				+ " count(distinct r.id) as noOfQuotation, "
				+ " sum(q.quotationLoading) as requiredManDay, "
				+ " isnull(sum(case when u.seasonality in (1,4) then q.quotationLoading end),0) as fullSeasonLoading, " 
				+ " isnull(sum(case when u.seasonality = 2 then q.quotationLoading end),0) as summerLoading, "
				+ " isnull(sum(case when u.seasonality = 3 then q.quotationLoading end),0) as winterLoading, "
				+ " batch.code as batchCode, "
				+ " a.referenceNo as referenceNo" 
                + " from Assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.allocationBatch as allocationBatch "
                + " left join r.quotation as q "
                + " left join q.unit as u "
                + " left join q.batch as batch "
                + " left join a.outlet as outlet "
                + " left join outlet.tpu as tpu "
                + " left join tpu.district as district ";
		        if (StringUtils.isNotEmpty(outletTypeId)) {
		        	hql += " left join outlet.outletTypes as ot ";
		        }
                hql += " where 1 = 1 ";

		hql += " and allocationBatch.id = :allocationBatchId ";
		
		hql += " and r.user.id = :fromUserId ";
		
		hql += " and (r.isSpecifiedUser = 0) ";
		hql += " and r.isReleased = 0 ";
		
		if (excludeAssignmentIds!=null && excludeAssignmentIds.size() > 0) {
			hql += " and a.id not in (:excludeAssignmentIds) ";
		}
		if (selectedAssignmentIds.size() > 0) {
			hql += " and a.id not in (:selectedAssignmentIds) ";
		}
		
		if (tpuId != null && tpuId.length > 0) {
			hql += " and tpu.tpuId in :tpuId ";
		}
		if (StringUtils.isNotEmpty(outletTypeId)) {
			hql += " and ot.shortCode = :outletTypeId ";
		}
		if (districtId != null) {
			hql += " and district.districtId = :districtId ";
		}
		if (batchId != null) {
			hql += " and batch.id = :batchId ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
        		+ " outlet.name like :search or "
        		+ " district.code like :search or "
        		+ " tpu.code like :search or "
        		+ " outlet.detailAddress like :search or "
                + " " + startDateFormat + " like :search or "
                + " " + endDateFormat + " like :search or "
                + " batch.code like :search or "
                + " a.referenceNo like :search "
                + " ) ";
		}

		hql += " group by a.id, outlet.name, district.code, tpu.code, outlet.detailAddress, a.startDate, a.endDate, batch.code, a.referenceNo ";
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("fromUserId", fromUserId);

		if (excludeAssignmentIds!=null && excludeAssignmentIds.size() > 0) {
			query.setParameterList("excludeAssignmentIds", excludeAssignmentIds);
		}
		if (selectedAssignmentIds.size() > 0) {
			query.setParameterList("selectedAssignmentIds", selectedAssignmentIds);
		}
		
		if (tpuId != null) {
			query.setParameterList("tpuId", tpuId);
		}
		if (StringUtils.isNotEmpty(outletTypeId)) {
			query.setParameter("outletTypeId", outletTypeId);
		}
		if (districtId != null) {
			query.setParameter("districtId", districtId);
		}
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%"+search+"%");
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(SelectedAssignmentModel.class));

		return query.list();
	}
	
	public long countTransferInOutMaintenanceTableList(String search,
			List<Integer> excludeAssignmentIds,
			int allocationBatchId, Integer fromUserId,
			Integer[] tpuId, String outletTypeId, Integer districtId, Integer batchId, List<Integer> selectedAssignmentIds) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "select count(*) from "
				+ " ("
				+ " select count(distinct r.quotationRecordId) as cnt "
				+ " from Assignment as a "
                + " left join QuotationRecord as r on r.assignmentId = a.assignmentId "
                + " left join AllocationBatch as allocationBatch on allocationBatch.allocationBatchId = r.allocationBatchId "
                + " left join Quotation as q on r.quotationId = q.quotationId "
                + " left join Batch as batch on batch.batchId = q.batchId "
                + " left join Outlet as outlet on outlet.outletId = a.outletId "
                + " left join Tpu as tpu on tpu.tpuId = outlet.tpuId "
                + " left join District as district on district.districtId = tpu.districtId "
                + " left join OutletTypeOutlet as ot on ot.outletId = outlet.outletId "
                + " where 1 = 1 ";

		sql += " and allocationBatch.allocationBatchId = :allocationBatchId ";
		
		sql += " and r.userId = :fromUserId ";

		sql += " and (r.isSpecifiedUser = 0) ";
		sql += " and r.isReleased = 0 ";

		if (excludeAssignmentIds!=null && excludeAssignmentIds.size() > 0) {
			sql += " and a.assignmentId not in (:excludeAssignmentIds) ";
		}
		if (selectedAssignmentIds.size() > 0) {
			sql += " and a.assignmentId not in (:selectedAssignmentIds) ";
		}
		
		if (tpuId != null && tpuId.length > 0) {
			sql += " and tpu.tpuId in :tpuId ";
		}
		if (StringUtils.isNotEmpty(outletTypeId)) {
			sql += " and ot.shortCode = :outletTypeId ";
		}
		if (districtId != null) {
			sql += " and district.districtId = :districtId ";
		}
		if (batchId != null) {
			sql += " and batch.batchId = :batchId ";
		}
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
        		+ " outlet.name like :search or "
        		+ " district.code like :search or "
        		+ " tpu.code like :search or "
        		+ " outlet.detailAddress like :search or "
                + " " + startDateFormat + " like :search or "
                + " " + endDateFormat + " like :search or"
                + " batch.code like :search or "
                + " a.referenceNo like :search"
                + " ) ";
		}

		sql += " group by a.assignmentId, outlet.name, district.code, tpu.code, outlet.detailAddress, a.startDate, a.endDate, batch.code, a.referenceNo ";
				
		sql += " ) as data ";
		
		
//		
//		String hql = "select count(distinct r.quotationRecordId) "
//				+ " from Assignment as a "
//                + " left join a.quotationRecords as r "
//                + " left join r.allocationBatch as allocationBatch "
//                + " left join r.quotation as q "
//                + " left join q.batch as batch "
//                + " left join a.outlet as outlet "
//                + " left join outlet.tpu as tpu "
//                + " left join tpu.district as district "
//                + " left join outlet.outletTypes as ot "
//                + " where 1 = 1 ";
//
//		hql += " and allocationBatch.id = :allocationBatchId ";
//		
//		hql += " and r.user.id = :fromUserId ";
//		
//		hql += " and r.isSpecifiedUser = 0 ";
//		hql += " and r.isReleased = 0 ";
//
//		if (excludeAssignmentIds.size() > 0) {
//			hql += " and a.id not in (:excludeAssignmentIds) ";
//		}
//		
//		if (tpuId != null && tpuId.length > 0) {
//			hql += " and tpu.tpuId in :tpuId ";
//		}
//		if (StringUtils.isNotEmpty(outletTypeId)) {
//			hql += " and ot.shortCode = :outletTypeId ";
//		}
//		if (districtId != null) {
//			hql += " and district.districtId in :districtId ";
//		}
//		if (batchId != null) {
//			hql += " and batch.id = :batchId ";
//		}
//		if (StringUtils.isNotEmpty(search)) {
//			hql += " and ( "
//        		+ " outlet.name like :search or "
//        		+ " district.code like :search or "
//        		+ " tpu.code like :search or "
//        		+ " outlet.detailAddress like :search or "
//                + " " + startDateFormat + " like :search or "
//                + " " + endDateFormat + " like :search "
//                + " ) ";
//		}
//
//		hql += " group by a.id, outlet.name, district.code, tpu.code, outlet.detailAddress, a.startDate, a.endDate ";
//
//		Query query = getSession().createQuery(hql);
		
		Query query = getSession().createSQLQuery(sql);

		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("fromUserId", fromUserId);

		if (excludeAssignmentIds!=null && excludeAssignmentIds.size() > 0) {
			query.setParameterList("excludeAssignmentIds", excludeAssignmentIds);
		}
		if (selectedAssignmentIds.size() > 0) {
			query.setParameterList("selectedAssignmentIds", selectedAssignmentIds);
		}
		
		if (tpuId != null) {
			query.setParameterList("tpuId", tpuId);
		}
		if (StringUtils.isNotEmpty(outletTypeId)) {
			query.setParameter("outletTypeId", outletTypeId);
		}
		if (districtId != null) {
			query.setParameter("districtId", districtId);
		}
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%"+search+"%");
		}
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getTransferInOutMaintenanceTableSelectAll(String search,
			List<Integer> excludeAssignmentIds,
			int allocationBatchId, Integer fromUserId,
			Integer[] tpuId, String outletTypeId, Integer districtId, Integer batchId) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select a.id as id "
                + " from Assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.allocationBatch as allocationBatch "
                + " left join r.quotation as q "
                + " left join q.batch as batch "
                + " left join a.outlet as outlet "
                + " left join outlet.tpu as tpu "
                + " left join tpu.district as district "
                + " left join outlet.outletTypes as ot "
                + " where 1 = 1 ";

		hql += " and allocationBatch.id = :allocationBatchId ";
		
		hql += " and r.user.id = :fromUserId ";
		
		hql += " and r.isSpecifiedUser = 0 ";
		hql += " and r.isReleased = 0 ";
		
		if (excludeAssignmentIds.size() > 0) {
			hql += " and a.id not in (:excludeAssignmentIds) ";
		}
		
		if (tpuId != null && tpuId.length > 0) {
			hql += " and tpu.tpuId in :tpuId ";
		}
		if (StringUtils.isNotEmpty(outletTypeId)) {
			hql += " and ot.shortCode = :outletTypeId ";
		}
		if (districtId != null) {
			hql += " and district.districtId in :districtId ";
		}
		if (batchId != null) {
			hql += " and batch.id = :batchId ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
        		+ " outlet.name like :search or "
        		+ " district.code like :search or "
        		+ " tpu.code like :search or "
        		+ " outlet.detailAddress like :search or "
                + " " + startDateFormat + " like :search or "
                + " " + endDateFormat + " like :search or "
                + " batch.code like :search or "
                + " a.referenceNo like :search "
                + " ) ";
		}

		hql += " group by a.id, outlet.name, district.code, tpu.code, outlet.detailAddress, a.startDate, a.endDate, batch.code, a.referenceNo ";

		Query query = getSession().createQuery(hql);

		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("fromUserId", fromUserId);

		if (excludeAssignmentIds.size() > 0) {
			query.setParameterList("excludeAssignmentIds", excludeAssignmentIds);
		}
		
		if (tpuId != null) {
			query.setParameterList("tpuId", tpuId);
		}
		if (StringUtils.isNotEmpty(outletTypeId)) {
			query.setParameter("outletTypeId", outletTypeId);
		}
		if (districtId != null) {
			query.setParameter("districtId", districtId);
		}
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%"+search+"%");
		}

		return (List<Integer>)query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getAssignmentOfSameAllocationBatch(int batchId) {
		String hql = "select distinct assignment.id as id "
                + " from AssignmentAdjustment as a "
                + " left join a.allocationBatch as batch "
                + " left join a.assignments as assignment "
                + " where 1 = 1 ";

		hql += " and (a.status = 'Submitted' or a.status = 'Approved' or a.status = 'Recommended') ";
		
		hql += " and batch.id = :batchId and assignment.id is not null ";

		Query query = getSession().createQuery(hql);

		query.setParameter("batchId", batchId);

		return (List<Integer>)query.list();
	}

	@SuppressWarnings("unchecked")
	public List<ExportPECheckSelectedList> getPECheckTaskByAssignment(Date referenceMonth){
		String sql = "Select a.AssignmentId as assignmentId"
//				+ " , a.survey as survey"
				+ ", CASE WHEN a.Survey IS NULL THEN " 
				+ "	STUFF((SELECT '; ' + CAST( pp.Survey AS VARCHAR(100)) [text()] FROM " 
				+ " Assignment ass " 
				+ " LEFT JOIN QuotationRecord qr1 ON qr1.AssignmentId = ass.AssignmentId"
				+ " LEFT JOIN Quotation q ON q.QuotationId = qr1.QuotationId and (qr1.IsBackNo=0) "
				+ " LEFT JOIN Unit un ON un.UnitId = q.UnitId "
				+ " LEFT JOIN Purpose pp ON pp.PurposeId = un.PurposeId "
				+ " WHERE ass.AssignmentId= a.AssignmentId "
				+ " GROUP BY pp.Survey "
				+ " FOR XML PATH(''), TYPE).value('.','NVARCHAR(MAX)'),1,2,' ')"
				+ "	ELSE a.Survey END as survey"
				+ " , sm.ReferenceMonth as referenceMonth"
				+ " , a.AssignedCollectionDate as assignedCollectionDate"
				+ " , a.StartDate as startDate"
				+ " , a.EndDate as endDate"
				+ " , a.CollectionDate as collectionDate"
				+ " , o.FirmCode as outletCode"
				+ " , o.Name as outletName"
				+ " , u.StaffCode as staffCode"
				+ " , u.Team as team"
				+ " , d.Code as district"
				+ " , t.Code as tpu"
				+ " , o.StreetAddress as address"
				+ " , o.CollectionMethod as collectionMethod"
				+ " , o.OutletMarketType as outletMarketType"
				+ " , count(distinct qr.QuotationRecordId) as noOfQuotation"
				+ " , pe.IsSelected as isSelected"
				+ " , pe.IsSectionHead as isSectionHead"
				+ " , pe.IsFieldTeamHead as isFieldTeamHead"
				+ " , pe.IsCertaintyCase as isCertaintyCase"
				+ " , pe.IsRandomCase as isRandomCase"
				+ " from Assignment as a"
				+ " left join QuotationRecord qr on qr.AssignmentId = a.AssignmentId AND qr.isBackNo = 0 "
				+ " left join Outlet o ON o.OutletId = a.OutletId"
				+ " left join Tpu t ON t.TpuId = o.TpuId"
				+ " left join District d ON d.DistrictId = t.DistrictId"
				+ " left join SurveyMonth sm ON sm.SurveyMonthId = a.SurveyMonthId"
				+ " left join PECheckTask pe ON a.AssignmentId = pe.AssignmentId"
				+ " left join [User] u ON u.UserId = a.UserId"
				+ " where 1=1"
				+ " and sm.ReferenceMonth = :referenceMonth"
				+ " and a.IsImportedAssignment = 0";
		
		sql += " group by a.assignmentId,sm.ReferenceMonth, a.survey, a.assignedCollectionDate, a.startDate"
				+ ", a.endDate, a.collectionDate, o.firmCode, o.name, u.staffCode, u.team"
				+ ", d.code, t.code, o.streetAddress, o.collectionMethod, o.outletMarketType"
				+ ", pe.isSelected, pe.isSectionHead, pe.isFieldTeamHead, pe.isCertaintyCase"
				+ ", pe.isRandomCase";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("referenceMonth", referenceMonth);
		
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER)
		.addScalar("survey", StandardBasicTypes.STRING)
		.addScalar("referenceMonth", StandardBasicTypes.DATE)
		.addScalar("assignedCollectionDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("startDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("endDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("collectionDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("outletCode", StandardBasicTypes.INTEGER)
		.addScalar("outletName", StandardBasicTypes.STRING)
		.addScalar("staffCode",StandardBasicTypes.STRING)
		.addScalar("team",StandardBasicTypes.STRING)
		.addScalar("district",StandardBasicTypes.STRING)
		.addScalar("tpu",StandardBasicTypes.STRING)
		.addScalar("address",StandardBasicTypes.STRING)
		.addScalar("collectionMethod",StandardBasicTypes.INTEGER)
		.addScalar("outletMarketType",StandardBasicTypes.INTEGER)
		.addScalar("noOfQuotation",StandardBasicTypes.LONG)
		.addScalar("isSelected", StandardBasicTypes.BOOLEAN)
		.addScalar("isSectionHead", StandardBasicTypes.BOOLEAN)
		.addScalar("isFieldTeamHead", StandardBasicTypes.BOOLEAN)
		.addScalar("isCertaintyCase", StandardBasicTypes.BOOLEAN)
		.addScalar("isRandomCase", StandardBasicTypes.BOOLEAN);
		
		query.setResultTransformer(Transformers.aliasToBean(ExportPECheckSelectedList.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<RecommendAssignmentModel> getTransferInOutRecommendTableList(String search,
			int firstRecord, int displayLength, Order order,
			int allocationBatchId, Integer fromUserId, int assignmentAdjustmentId,
			Integer[] tpuId, String[] outletTypeId, Integer districtId, Integer batchId, boolean isRecommend) {

		String startDateFormat = String.format("FORMAT(a.StartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.EndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "select a.AssignmentId as id,"
						+ " o.Name as firm, "
						+ " district.Code as district, "
						+ " tpu.Code as tpu, "
						+ " o.DetailAddress as address, "
						+ " case when a.StartDate is null then '' else " + startDateFormat + " end as startDate, "
						+ " case when a.EndDate is null then '' else " + endDateFormat + " end as endDate, "
						+ " case when ra.AssignmentAdjustmentId is null then 0 else 1 end as selected, "
						+ " count(qr.QuotationRecordId) as noOfQuotation, "
						+ " sum(q.QuotationLoading) as requiredManDay, "
						+ " isnull(sum(case when u.seasonality in (1,4) then q.quotationLoading end),0) as fullSeasonLoading, " 
						+ " isnull(sum(case when u.seasonality = 2 then q.quotationLoading end),0) as summerLoading, "
						+ " isnull(sum(case when u.seasonality = 3 then q.quotationLoading end),0) as winterLoading " 
						+ " from Assignment as a "
						+ " inner join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
						+ " inner join AllocationBatch as ab on ab.AllocationBatchId = qr.AllocationBatchId "
						+ " inner join Quotation as q on q.QuotationId = qr.QuotationId "
						+ " left join Unit as u on u.UnitId = q.UnitId "
						+ " left join Outlet as o on o.OutletId = a.OutletId "
						+ " left join Tpu as tpu on tpu.TpuId = o.TpuId "
						+ " left join District as district on district.DistrictId = tpu.DistrictId "
						+ " left join ReleasedAssignment as ra on ra.AssignmentId = a.AssignmentId and ra.AssignmentAdjustmentId = :assignmentAdjustmentId "

						+ " where 1=1 ";
		
		sql += " and qr.AllocationBatchId = :allocationBatchId ";
		
		sql += " and qr.UserId = :fromUserId ";
		
		if (tpuId != null && tpuId.length > 0) {
			sql += " and tpu.TpuId in :tpuId ";
		}
		
		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and o.OutletId in ( "
					+ " select o.OutletId "
					+ " from Outlet as o "
					+ " left join OutletTypeOutlet as ot on ot.OutletId = o.OutletId "
					+ " where ot.ShortCode in (:outletTypeId) "
					+ " ) ";
		}

		if (districtId != null) {
			sql += " and district.DistrictId = :districtId ";
		}

		if (batchId != null) {
			sql += " and q.BatchId = :batchId ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
        		+ " o.name like :search or "
        		+ " district.Code like :search or "
        		+ " tpu.Code like :search or "
        		+ " o.DetailAddress like :search or "
                + " " + startDateFormat + " like :search or "
                + " " + endDateFormat + " like :search "
                + " ) ";
		}

		sql += " group by a.AssignmentId, o.Name, district.Code, tpu.Code, o.DetailAddress, a.StartDate, a.EndDate, ra.AssignmentAdjustmentId ";
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		SQLQuery query = getSession().createSQLQuery(sql);

		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("fromUserId", fromUserId);
		query.setParameter("assignmentAdjustmentId", assignmentAdjustmentId);

		if (tpuId != null) {
			query.setParameterList("tpuId", tpuId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (districtId != null) {
			query.setParameter("districtId", districtId);
		}
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%"+search+"%");
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(RecommendAssignmentModel.class));

		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("startDate", StandardBasicTypes.STRING);
		query.addScalar("endDate", StandardBasicTypes.STRING);
		query.addScalar("selected", StandardBasicTypes.BOOLEAN);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("requiredManDay", StandardBasicTypes.DOUBLE);
		query.addScalar("fullSeasonLoading", StandardBasicTypes.DOUBLE);
		query.addScalar("summerLoading", StandardBasicTypes.DOUBLE);
		query.addScalar("winterLoading", StandardBasicTypes.DOUBLE);
		
		return query.list();
	}
	
	public long countTransferInOutRecommendTableList(String search,
			int allocationBatchId, Integer fromUserId, int assignmentAdjustmentId,
			Integer[] tpuId, String[] outletTypeId, Integer districtId, Integer batchId, boolean isRecommend) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String sql = "select count(distinct a.AssignmentId) as count "
						
						+ " from Assignment as a "
						+ " inner join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
						+ " inner join AllocationBatch as ab on ab.AllocationBatchId = qr.AllocationBatchId "
						+ " inner join Quotation as q on q.QuotationId = qr.QuotationId "
						+ " left join Outlet as o on o.OutletId = a.OutletId "
						+ " left join Tpu as tpu on tpu.TpuId = o.TpuId "
						+ " left join District as district on district.DistrictId = tpu.DistrictId "
						+ " left join Unit as u on u.UnitId = q.UnitId "
						+ " left join ReleasedAssignment as ra on ra.AssignmentId = a.AssignmentId and ra.AssignmentAdjustmentId = :assignmentAdjustmentId "

						+ " where 1=1 ";
		
		sql += " and qr.AllocationBatchId = :allocationBatchId ";
		
		sql += " and qr.UserId = :fromUserId ";
		
		if (tpuId != null && tpuId.length > 0) {
			sql += " and tpu.TpuId in :tpuId ";
		}
		
		if (outletTypeId != null && outletTypeId.length > 0) {
			sql += " and o.OutletId in ( "
					+ " select o.OutletId "
					+ " from Outlet as o "
					+ " left join OutletTypeOutlet as ot on ot.OutletId = o.OutletId "
					+ " where ot.ShortCode in (:outletTypeId) "
					+ " ) ";
		}

		if (districtId != null) {
			sql += " and district.DistrictId = :districtId ";
		}

		if (batchId != null) {
			sql += " and q.BatchId = :batchId ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
        		+ " o.name like :search or "
        		+ " district.Code like :search or "
        		+ " tpu.Code like :search or "
        		+ " o.DetailAddress like :search or "
                + " " + startDateFormat + " like :search or "
                + " " + endDateFormat + " like :search "
                + " ) ";
		}

		SQLQuery query = getSession().createSQLQuery(sql);

		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("fromUserId", fromUserId);
		query.setParameter("assignmentAdjustmentId", assignmentAdjustmentId);

		if (tpuId != null) {
			query.setParameterList("tpuId", tpuId);
		}
		if (outletTypeId != null && outletTypeId.length > 0) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (districtId != null) {
			query.setParameter("districtId", districtId);
		}
		if (batchId != null) {
			query.setParameter("batchId", batchId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", "%"+search+"%");
		}
		
		query.addScalar("count", StandardBasicTypes.LONG);
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public List<Assignment> getAssignmentsHavingPECheckTasks(SurveyMonth surveyMonth, Outlet o){
		Criteria criteria = this.createCriteria("a");
		
		criteria.add(Restrictions.eq("a.surveyMonth", surveyMonth)).add(Restrictions.eq("a.outlet", o)).add(Restrictions.isNotNull("a.peCheckTask"));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.TableList> getAssignmentMaintenanceAssignmentTableList(String search,
			int firstRecord, int displayLength, Order order,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId, Integer assignmentStatus, String deadline, String[] outletTypeId, Integer[] districtId,
			boolean isBusinessAdmin, String quotationState) {

		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String referenceMonth = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		
		String hql = "select a.id as assignmentId, "
				+ " user.id as userId, "
				+ " a.referenceNo as referenceNo, "
				+ " o.name as firm, "
				+ " o.id as outletId, "
				+ " d.code + ' - ' + d.englishName as district, "
				+ " tpu.code as tpu,"
				+ " count(distinct r.id) as noOfQuotation, "
				+ " user.englishName as personInCharge, "
				+ " case when a.status = 1 then 'EN' "
				+ "   when a.status = 2 then 'Closed' "
				+ "   when a.status = 3 then 'MV' "
				+ "   when a.status = 4 then 'Not Suitable' "
				+ "   when a.status = 5 then 'Refusal' "
				+ "   when a.status = 6 then 'Wrong Outlet' "
				+ "   when a.status = 7 then 'DL' "
				+ "   when a.status = 8 then 'NC' "
				+ "   when a.status = 9 then 'IP' "
				+ "   else '' end as firmStatus, "
				+ " case when count(r.id) = count(r2.id) then 'Not Start' else 'In Progress' end as assignmentStatus, "
				+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
				+ "   when a.endDate is not null then " + endDateFormat + " "
				+ "   else '' end as deadline, "
				+ " case when min(a.assignedCollectionDate) is null then min(a.endDate) else min(a.assignedCollectionDate) end as deadline2 "
				+ " , case when sm.referenceMonth is not null then " + referenceMonth + " else '' end as referenceMonth "
                + " from Assignment as a "
                + " inner join a.quotationRecords as r "
                + " left join a.quotationRecords as r2 on r2.status = 'Blank' and r=r2 "
                + " left join r.user as user "
                + " left join r.outlet as o "
                + " left join o.outletTypes as ot "
                + " left join o.tpu as tpu "
                + " left join tpu.district as d "
                + " left join a.surveyMonth as sm "
                + " where 1 = 1 ";
		
		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		/*
		hql += " and (r.status in ('Blank', 'Draft', 'Rejected') or "
				+ " r.quotationState = 'IP' or "
				+ " r.availability = 2 " // IP
				+ " ) "
				+ " and (a.assignedCollectionDate is null or a.assignedCollectionDate <= :today )"
				+ " and (a.startDate is null or a.startDate <= :today ) ";
		*/
		hql += " and (r.status in ('Blank', 'Draft', 'Rejected') or "
				+ " r.quotationState = 'IP' or "
				+ " r.availability = 2 " // IP
				+ " ) ";
		
		if(!isBusinessAdmin) {
			hql	+= " and (a.assignedCollectionDate is null or a.assignedCollectionDate <= :today )"
					+ " and (a.startDate is null or a.startDate <= :today ) ";
		}
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.id in (:userIds) ";
		}
		
		if (personInChargeId != null) {
			hql += " and r.user.id = :personInChargeId ";
		}
		if (surveyMonthId != null) {
			hql += " and a.surveyMonth.id = :surveyMonthId ";
		}
		if (StringUtils.isNotBlank(deadline)) {
			hql += " and ( "
					+ " (" + assignedCollectionDateFormat + " = :deadline) "
					+ " or (a.assignedCollectionDate is null and " + endDateFormat + " = :deadline) "
					+ " ) ";
		}
		if (outletTypeId != null) {
			hql += " and ot.shortCode in :outletTypeId ";
		}
		if (districtId != null && districtId.length > 0) {
			hql += " and d.districtId in :districtId ";
		}
		if (StringUtils.isNotBlank(quotationState)) {
			hql += " and r.quotationState = :quotationState ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " a.referenceNo like :search or "
					+ " o.name like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " user.englishName like :search or "
					+ " case when a.status = 1 then 'EN' "
					+ "   when a.status = 2 then 'Closed' "
					+ "   when a.status = 3 then 'MV' "
					+ "   when a.status = 4 then 'Not Suitable' "
					+ "   when a.status = 5 then 'Refusal' "
					+ "   when a.status = 6 then 'Wrong Outlet' "
					+ "   when a.status = 7 then 'DL' "
					+ "   when a.status = 8 then 'NC' "
					+ "   when a.status = 9 then 'IP' "
					+ "   else '' end like :search or "
	                + " (" + assignedCollectionDateFormat + " like :search or "
	                + " (a.assignedCollectionDate is null and " + endDateFormat + " like :search) "
	                + " or (" + referenceMonth + " like :search) ) "
	                + " ) ";
		}
		
		hql += " group by a.assignmentId, user.id, "
				+ " a.referenceNo, o.name, o.id, d.code, d.englishName, tpu.code, user.englishName, "
				+ " a.status, "
                + " a.assignedCollectionDate, a.endDate "
				+ ", sm.referenceMonth ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				hql += " having count(distinct r2.id) <> count(distinct r.id) ";
			} else if (assignmentStatus == 2) { // Not Start
				hql += " having count(distinct r2.id) = count(distinct r.id) ";
			}
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);
		
		if(!isBusinessAdmin) {
			Date today = DateUtils.truncate(new Date(), Calendar.DATE);
			query.setParameter("today", today);
		}

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		if (surveyMonthId != null) {
			query.setParameter("surveyMonthId", surveyMonthId);
		}
		if (StringUtils.isNotBlank(deadline)) {
			query.setParameter("deadline", deadline);
		}
		if (outletTypeId != null) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (districtId != null && districtId.length > 0) {
			query.setParameterList("districtId", districtId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (StringUtils.isNotBlank(quotationState)) {
			query.setParameter("quotationState", quotationState);
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.TableList.class));

		return query.list();
	}
	
	public long countAssignmentMaintenanceAssignmentTableList(String search,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId, Integer assignmentStatus, String deadline, String[] outletTypeId, Integer[] districtId,
			boolean isBusinessAdmin, String quotationState) {

		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String referenceMonth = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		
		String sql = "select count(*) from ("
				+ " select a.assignmentId "
                + " from Assignment as a "
                + " inner join QuotationRecord as r on r.assignmentId = a.assignmentId "
                + " left join QuotationRecord as r2 on r2.assignmentId = a.assignmentId and r2.status = 'Blank' and r.quotationRecordId = r2.quotationRecordId "
                + " left join SurveyMonth as m on m.surveyMonthId = a.surveyMonthId "
                + " left join [User] as u on r.userId = u.userId "
                + " left join Outlet as o on r.outletId = o.outletId "
                + " left join OutletTypeOutlet as ot on ot.outletId = o.outletId "
                + " left join TPU as tpu on tpu.tpuId = o.tpuId "
                + " left join District as d on d.districtId = tpu.districtId "
                + " left join SurveyMonth as sm on sm.surveyMonthId = a.surveyMonthId "
                + " where 1=1 ";
//		sql += " and r.isBackNo = 0 ";
//
//		sql += " and (r.status in ('Blank', 'Draft', 'Rejected') or "
//				+ " r.quotationState = 'IP' or "
//				+ " r.availability = 2 " // IP
//				+ " ) ";
		
		sql += " and r.isBackNo = 0 and r.isBackTrack = 0 ";
		
		/*
		sql += " and (r.status in ('Blank', 'Draft', 'Rejected') or "
				+ " r.quotationState = 'IP' or "
				+ " r.availability = 2 " // IP
				+ " ) "
				+ " and (a.assignedCollectionDate is null or a.assignedCollectionDate <= :today )"
				+ " and (a.startDate is null or a.startDate <= :today ) ";
		*/
		sql += " and (r.status in ('Blank', 'Draft', 'Rejected') or "
				+ " r.quotationState = 'IP' or "
				+ " r.availability = 2 " // IP
				+ " ) ";
		
		if(!isBusinessAdmin) {
			sql += " and (a.assignedCollectionDate is null or a.assignedCollectionDate <= :today )"
					+ " and (a.startDate is null or a.startDate <= :today ) ";
		}
		
		if (userIds != null && userIds.size() > 0) {
			sql += " and u.userId in (:userIds) ";
		}
		
		if (personInChargeId != null) {
			sql += " and u.userId = :personInChargeId ";
		}
		if (surveyMonthId != null) {
			sql += " and m.surveyMonthId = :surveyMonthId ";
		}
		if (StringUtils.isNotBlank(deadline)) {
			sql += " and ( "
					+ " (" + assignedCollectionDateFormat + " = :deadline) "
					+ " or (a.assignedCollectionDate is null and " + endDateFormat + " = :deadline) "
					+ " ) ";
		}
		if (outletTypeId != null) {
			sql += " and ot.shortCode in (:outletTypeId) ";
		}
		if (districtId != null && districtId.length > 0) {
			sql += " and d.districtId in :districtId ";
		}
		if (StringUtils.isNotBlank(quotationState)) {
			sql += " and r.quotationState = :quotationState ";
		}
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
					+ " a.referenceNo like :search or "
					+ " o.name like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " u.englishName like :search or "
					+ " case when a.status = 1 then 'EN' "
					+ "   when a.status = 2 then 'Closed' "
					+ "   when a.status = 3 then 'MV' "
					+ "   when a.status = 4 then 'Not Suitable' "
					+ "   when a.status = 5 then 'Refusal' "
					+ "   when a.status = 6 then 'Wrong Outlet' "
					+ "   when a.status = 7 then 'DL' "
					+ "   when a.status = 8 then 'NC' "
					+ "   when a.status = 9 then 'IP' "
					+ "   else '' end like :search or "
	                + " (" + assignedCollectionDateFormat + " like :search or "
	                + " (a.assignedCollectionDate is null and " + endDateFormat + " like :search) "
	                + " or (" + referenceMonth + " like :search) )"
	                + " ) ";
		}

		sql += " group by a.assignmentId, u.userId, "
				+ " a.referenceNo, o.name, o.outletId, d.code, tpu.code, u.englishName, "
				+ " a.status, "
                + " a.assignedCollectionDate, a.endDate "
                + " , sm.referenceMonth ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				sql += " having count(distinct r2.quotationRecordId) <> count(distinct r.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				sql += " having count(distinct r2.quotationRecordId) = count(distinct r.quotationRecordId) ";
			}
		}
		
		sql += ") as data ";
		
		
//		String hql = "select a.id "
//                + " from Assignment as a "
//                + " inner join a.quotationRecords as r "
//                + " left join a.quotationRecords as r2 on r2.status = 'Blank' "
//                + " left join r.user as user "
//                + " left join r.outlet as o "
//                + " left join o.outletTypes as ot "
//                + " left join o.tpu as tpu "
//                + " left join tpu.district as d "
//                + " where 1 = 1 ";
//		
//		hql += " and r.isBackNo = false "
//				+ " and r.status in ('Blank', 'Draft', 'Rejected') ";
//		
//		if (userIds != null && userIds.size() > 0) {
//			hql += " and user.id in (:userIds) ";
//		}
//		
//		if (personInChargeId != null) {
//			hql += " and r.user.id = :personInChargeId ";
//		}
//		if (surveyMonthId != null) {
//			hql += " and a.surveyMonth.id = :surveyMonthId ";
//		}
//		if (StringUtils.isNotBlank(deadline)) {
//			hql += " and ( "
//					+ " (" + assignedCollectionDateFormat + " = :deadline) "
//					+ " or (a.assignedCollectionDate is null and " + startDateFormat + " = :deadline) "
//					+ " ) ";
//		}
//		if (outletTypeId != null) {
//			hql += " and ot.shortCode in :outletTypeId ";
//		}
//		if (StringUtils.isNotEmpty(search)) {
//			hql += " and ( "
//					+ " a.referenceNo like :search or "
//					+ " o.name like :search or "
//					+ " d.code like :search or "
//					+ " tpu.code like :search or "
//					+ " user.englishName like :search or "
//					+ " case when a.status = 1 then 'EN' "
//					+ "   when a.status = 2 then 'Closed' "
//					+ "   when a.status = 3 then 'MV' "
//					+ "   when a.status = 4 then 'Not Suitable' "
//					+ "   when a.status = 5 then 'Refusal' "
//					+ "   when a.status = 6 then 'Wrong Outlet' "
//					+ "   when a.status = 7 then 'DL' "
//					+ "   when a.status = 8 then 'NC' "
//					+ "   when a.status = 9 then 'IP' "
//					+ "   else '' end like :search or "
//	                + " (" + assignedCollectionDateFormat + " like :search or "
//	                + " (a.collectionDate is null and " + startDateFormat + " like :search))"
//	                + " ) ";
//		}
//
//		hql += " group by a.assignmentId, user.id, "
//				+ " a.referenceNo, o.name, o.id, d.code, tpu.code, user.englishName, "
//				+ " a.status, "
//                + " a.assignedCollectionDate, a.startDate ";
//		
//		if (assignmentStatus != null) {
//			if (assignmentStatus == 1) { // In Progress
//				hql += " having count(distinct r2.id) <> count(distinct r.id) ";
//			} else if (assignmentStatus == 2) { // Not Start
//				hql += " having count(distinct r2.id) = count(distinct r.id) ";
//			}
//		}
//		
//		String countHql = "select count(distinct acount.id) "
//				+ " from Assignment as acount "
//				+ " where acount.id in (" + hql + ") ";
//
//		Query query = getSession().createQuery(countHql);
		
		Query query = getSession().createSQLQuery(sql);
		
		if(!isBusinessAdmin) {
			query.setParameter("today", DateUtils.truncate(new Date(), Calendar.DATE));
		}

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		if (surveyMonthId != null) {
			query.setParameter("surveyMonthId", surveyMonthId);
		}
		if (StringUtils.isNotBlank(deadline)) {
			query.setParameter("deadline", deadline);
		}
		if (outletTypeId != null) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (districtId != null && districtId.length > 0) {
			query.setParameterList("districtId", districtId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		if (StringUtils.isNotBlank(quotationState)) {
			query.setParameter("quotationState", quotationState);
		}
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
		
//		if(assignmentStatus != null){
//			return Long.valueOf(query.list().size());
//		}else{
//			Long count = (Long)query.uniqueResult();
//			return count == null ? 0 : count;
//		}
	}

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.TableList> getNewRecruitmentMaintenanceAssignmentTableList(String search,
			int firstRecord, int displayLength, Order order,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId, Integer assignmentStatus, String deadline, String[] outletTypeId) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select a.id as assignmentId, "
				+ " user.id as userId, "
				+ " a.referenceNo as referenceNo, "
				+ " o.name as firm, "
				+ " o.id as outletId, "
				+ " d.code as district, "
				+ " tpu.code as tpu,"
				+ " count(distinct r.id) as noOfQuotation, "
				+ " user.englishName as personInCharge, "
				+ " case when a.status = 1 then 'EN' "
				+ "   when a.status = 2 then 'Closed' "
				+ "   when a.status = 3 then 'MV' "
				+ "   when a.status = 4 then 'Not Suitable' "
				+ "   when a.status = 5 then 'Refusal' "
				+ "   when a.status = 6 then 'Wrong Outlet' "
				+ "   when a.status = 7 then 'DL' "
				+ "   when a.status = 8 then 'NC' "
				+ "   when a.status = 9 then 'IP' "
				+ "   else '' end as firmStatus, "
				+ " case when count(r.id) = count(r2.id) then 'Not Start' else 'In Progress' end as assignmentStatus, "
				+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
				+ "   when a.startDate is not null then " + startDateFormat + " "
				+ "   else '' end as deadline, "
				+ " case when min(a.assignedCollectionDate) is null then min(a.startDate) else min(a.assignedCollectionDate) end as deadline2 "
                + " from Assignment as a "
                + " inner join a.quotationRecords as r "
                + " left join a.quotationRecords as r2 on r2.status = 'Blank' and r=r2 "
                + " left join r.user as user "
                + " left join r.outlet as o "
                + " left join o.outletTypes as ot "
                + " left join o.tpu as tpu "
                + " left join tpu.district as d "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = true ";
		
		hql += " and r.isBackNo = false "
				+ " and r.status in ('Blank', 'Draft', 'Rejected') ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.id in (:userIds) ";
		}
		
		if (personInChargeId != null) {
			hql += " and r.user.id = :personInChargeId ";
		}
		if (surveyMonthId != null) {
			hql += " and a.surveyMonth.id = :surveyMonthId ";
		}
		if (StringUtils.isNotBlank(deadline)) {
			hql += " and ( "
					+ " (" + assignedCollectionDateFormat + " = :deadline) "
					+ " or (a.assignedCollectionDate is null and " + startDateFormat + " = :deadline) "
					+ " ) ";
		}
		if (outletTypeId != null) {
			hql += " and ot.shortCode in :outletTypeId ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " a.referenceNo like :search or "
					+ " o.name like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " a.user.englishName like :search or "
					+ " case when a.status = 1 then 'EN' "
					+ "   when a.status = 2 then 'Closed' "
					+ "   when a.status = 3 then 'MV' "
					+ "   when a.status = 4 then 'Not Suitable' "
					+ "   when a.status = 5 then 'Refusal' "
					+ "   when a.status = 6 then 'Wrong Outlet' "
					+ "   when a.status = 7 then 'DL' "
					+ "   when a.status = 8 then 'NC' "
					+ "   when a.status = 9 then 'IP' "
					+ "   else '' end like :search or "
	                + " (" + assignedCollectionDateFormat + " like :search or "
	                + " (a.collectionDate is null and " + startDateFormat + " like :search))"
	                + " ) ";
		}
		
		hql += " group by a.assignmentId, user.id, "
				+ " a.referenceNo, o.name, o.id, d.code, tpu.code, user.englishName, "
				+ " a.status, "
                + " a.assignedCollectionDate, a.startDate ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				hql += " having count(distinct r2.id) <> count(distinct r.id) ";
			} else if (assignmentStatus == 2) { // Not Start
				hql += " having count(distinct r2.id) = count(distinct r.id) ";
			}
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		if (surveyMonthId != null) {
			query.setParameter("surveyMonthId", surveyMonthId);
		}
		if (StringUtils.isNotBlank(deadline)) {
			query.setParameter("deadline", deadline);
		}
		if (outletTypeId != null) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.TableList.class));

		return query.list();
	}
	
	public long countNewRecruitmentMaintenanceAssignmentTableList(String search,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId, Integer assignmentStatus, String deadline, String[] outletTypeId) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select count(*) from ( "
				+ " select count(distinct a.assignmentId) as cnt "
				+ " from Assignment as a "
                + " inner join QuotationRecord as r on r.assignmentId = a.assignmentId "
                + " left join QuotationRecord as r2 on r2.assignmentId = a.assignmentId and r2.status = 'Blank' and r.quotationRecordId = r2.quotationRecordId "
                + " left join SurveyMonth as m on m.surveyMonthId = a.surveyMonthId "
                + " left join [User] as u on u.userId = r.userId "
                + " left join Outlet as o on o.outletId = r.outletId "
                + " left join OutletTypeOutlet as ot on ot.outletId = o.outletId "
                + " left join TPU as tpu on tpu.tpuId = o.tpuId  "
                + " left join District as d on d.districtId = tpu.districtId "
                + " where 1 = 1 ";
		sql += " and r.isNewRecruitment = 1 ";
		
		sql += " and r.isBackNo = 0 "
				+ " and r.status in ('Blank', 'Draft', 'Rejected') ";
		
		if (userIds != null && userIds.size() > 0) {
			sql += " and u.userId in (:userIds) ";
		}
		
		if (personInChargeId != null) {
			sql += " and u.userId = :personInChargeId ";
		}
		if (surveyMonthId != null) {
			sql += " and m.surveyMonthId = :surveyMonthId ";
		}
		if (StringUtils.isNotBlank(deadline)) {
			sql += " and ( "
					+ " (" + assignedCollectionDateFormat + " = :deadline) "
					+ " or (a.assignedCollectionDate is null and " + startDateFormat + " = :deadline) "
					+ " ) ";
		}
		if (outletTypeId != null) {
			sql += " and ot.shortCode in (:outletTypeId) ";
		}
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
					+ " a.referenceNo like :search or "
					+ " o.name like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " u.englishName like :search or "
					+ " case when a.status = 1 then 'EN' "
					+ "   when a.status = 2 then 'Closed' "
					+ "   when a.status = 3 then 'MV' "
					+ "   when a.status = 4 then 'Not Suitable' "
					+ "   when a.status = 5 then 'Refusal' "
					+ "   when a.status = 6 then 'Wrong Outlet' "
					+ "   when a.status = 7 then 'DL' "
					+ "   when a.status = 8 then 'NC' "
					+ "   when a.status = 9 then 'IP' "
					+ "   else '' end like :search or "
	                + " (" + assignedCollectionDateFormat + " like :search or "
	                + " (a.collectionDate is null and " + startDateFormat + " like :search))"
	                + " ) ";
		}
		
		sql += " group by a.assignmentId, u.userId, "
				+ " a.referenceNo, o.name, d.code, tpu.code, u.englishName, "
				+ " a.status, "
                + " a.assignedCollectionDate, a.startDate ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				sql += " having count(distinct r2.quotationRecordId) <> count(distinct r.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				sql += " having count(distinct r2.quotationRecordId) = count(distinct r.quotationRecordId) ";
			}
		}
		
		sql += " ) as d ";
//		
//		String hql = "select count(distinct a.id) "
//                + " from Assignment as a "
//                + " inner join a.quotationRecords as r "
//                + " left join a.quotationRecords as r2 on r2.status = 'Blank' "
//                + " left join r.user as user "
//                + " left join r.outlet as o "
//                + " left join o.outletTypes as ot "
//                + " left join o.tpu as tpu "
//                + " left join tpu.district as d "
//                + " where 1 = 1 ";
//		
//		hql += " and r.isNewRecruitment = true ";
//		
//		hql += " and r.isBackNo = false "
//				+ " and r.status in ('Blank', 'Draft', 'Rejected') ";
//		
//		if (userIds != null && userIds.size() > 0) {
//			hql += " and user.id in (:userIds) ";
//		}
//		
//		if (personInChargeId != null) {
//			hql += " and r.user.id = :personInChargeId ";
//		}
//		if (surveyMonthId != null) {
//			hql += " and a.surveyMonth.id = :surveyMonthId ";
//		}
//		if (StringUtils.isNotBlank(deadline)) {
//			hql += " and ( "
//					+ " (" + assignedCollectionDateFormat + " = :deadline) "
//					+ " or (a.assignedCollectionDate is null and " + startDateFormat + " = :deadline) "
//					+ " ) ";
//		}
//		if (outletTypeId != null) {
//			hql += " and ot.shortCode in :outletTypeId ";
//		}
//		if (StringUtils.isNotEmpty(search)) {
//			hql += " and ( "
//					+ " a.referenceNo like :search or "
//					+ " o.name like :search or "
//					+ " d.code like :search or "
//					+ " tpu.code like :search or "
//					+ " a.user.englishName like :search or "
//					+ " case when a.status = 1 then 'EN' "
//					+ "   when a.status = 2 then 'Closed' "
//					+ "   when a.status = 3 then 'MV' "
//					+ "   when a.status = 4 then 'Not Suitable' "
//					+ "   when a.status = 5 then 'Refusal' "
//					+ "   when a.status = 6 then 'Wrong Outlet' "
//					+ "   when a.status = 7 then 'DL' "
//					+ "   when a.status = 8 then 'NC' "
//					+ "   when a.status = 9 then 'IP' "
//					+ "   else '' end like :search or "
//	                + " (" + assignedCollectionDateFormat + " like :search or "
//	                + " (a.collectionDate is null and " + startDateFormat + " like :search))"
//	                + " ) ";
//		}
//		
//		hql += " group by a.assignmentId, user.id, "
//				+ " a.referenceNo, o.name, d.code, tpu.code, user.englishName, "
//				+ " a.status, "
//                + " a.assignedCollectionDate, a.startDate ";
//		
//		if (assignmentStatus != null) {
//			if (assignmentStatus == 1) { // In Progress
//				hql += " having count(distinct r2.id) <> count(distinct r.id) ";
//			} else if (assignmentStatus == 2) { // Not Start
//				hql += " having count(distinct r2.id) = count(distinct r.id) ";
//			}
//		}
		
		Query query = getSession().createSQLQuery(sql);
		//Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		if (surveyMonthId != null) {
			query.setParameter("surveyMonthId", surveyMonthId);
		}
		if (StringUtils.isNotBlank(deadline)) {
			query.setParameter("deadline", deadline);
		}
		if (outletTypeId != null) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.TableList> getImportedAssignmentMaintenanceAssignmentTableList(String search,
			int firstRecord, int displayLength, Order order,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId) {

		String referenceMonth = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		
		String hql = "select a.id as assignmentId, "
				+ " user.id as userId, "
				+ " a.referenceNo as referenceNo, "
				+ " a.additionalFirmName as firm, "
				+ " a.survey as survey, "
				+ " a.additionalNoOfForms as noOfForms, "
				+ " o.id as outletId, "
				+ " a.additionalFirmAddress as address, "
				+ " d.code as district, "
				+ " tpu.code as tpu,"
				//+ " count(distinct r.id) as noOfQuotation, "
				+ " user.englishName as personInCharge "
				+ " , case when sm.referenceMonth is not null then " + referenceMonth + " else '' end as referenceMonth "
                + " from Assignment as a "
               // + " left join a.quotationRecords as r "
                + " left join a.user as user "
                + " left join a.outlet as o "
                + " left join o.outletTypes as ot "
                + " left join a.additionalTpu as tpu "
                + " left join tpu.district as d "
                + " left join a.surveyMonth as sm "
                + " where 1 = 1 ";
		
		hql += " and a.isNewRecruitment = false ";
		hql += " and a.isImportedAssignment = true ";
		hql += " and a.isCompleted = false";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.id in (:userIds) ";
		}
		
		if (personInChargeId != null) {
			hql += " and user.id = :personInChargeId ";
		}
		if (surveyMonthId != null) {
			hql += " and a.surveyMonth.id = :surveyMonthId ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " a.referenceNo like :search or "
					+ " o.name like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " a.user.englishName like :search or"
					+ " a.survey like :search "
					+ " or " + referenceMonth + " like :search "
					+ " or a.additionalFirmAddress like :search "
					+ " or a.survey like :search"
	                + " ) ";
		}
		
//		hql += " group by a.assignmentId, user.id, "
//				+ " a.referenceNo, o.name, o.id, d.code, tpu.code, user.englishName ";
		
		if(order.getPropertyName() != null && !order.getPropertyName().equals("")){
			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");	
		}
		
		
		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		if (surveyMonthId != null) {
			query.setParameter("surveyMonthId", surveyMonthId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.TableList.class));

		return query.list();
	}
	
	public long countImportedAssignmentMaintenanceAssignmentTableList(String search,
			List<Integer> userIds,
			Integer personInChargeId, Integer surveyMonthId) {
		
		String referenceMonth = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		
			//String sql = "select count(*) from ( "
			String sql = " select count(distinct a.assignmentId) as cnt "
					 + " from Assignment as a "
		                //+ " left join QuotationRecord as r on r.AssignmentId = a.AssignmentId "
		                + " left join [User] as u on a.userId = u.userId "
		                + " left join Outlet as o on o.outletId = a.outletId  "
		                + " left join OutletTypeOutlet as ot on ot.outletId = o.outletId  "
		                + " left join TPU as tpu on tpu.tpuId = o.tpuId "
		                + " left join District as d on d.districtId = tpu.districtId "
		                + " left join SurveyMonth as sm on sm.surveyMonthId = a.surveyMonthId "
		                + " where 1 = 1 ";
				
			sql += " and a.isNewRecruitment = 0 ";
			sql += " and a.isImportedAssignment = 1 ";
			sql += " and a.isCompleted = 0";
				
			if (userIds != null && userIds.size() > 0) {
				sql += " and u.userId in (:userIds) ";
			}
			
			if (personInChargeId != null) {
				sql += " and u.userId = :personInChargeId ";
			}
			if (surveyMonthId != null) {
				sql += " and a.surveyMonthId = :surveyMonthId ";
			}
			if (StringUtils.isNotEmpty(search)) {
				sql += " and ( "
						+ " a.referenceNo like :search or "
						+ " o.name like :search or "
						+ " d.code like :search or "
						+ " tpu.code like :search or "
						+ " u.englishName like :search or"
						+ " a.survey like :search"
						+ " or " + referenceMonth + " like :search "
						+ " or a.additionalFirmAddress like :search "
						+ " or a.survey like :search"
		                + " ) ";
			}
			
//			sql += " group by a.assignmentId, u.userId, "
//					+ " a.referenceNo, o.name, d.code, tpu.code, u.englishName "
//				+ " ) as d ";
		

//		String hql = "select count(distinct a.id) "
//                + " from Assignment as a "
//                + " left join a.quotationRecords as r "
//                + " left join a.user as user "
//                + " left join a.outlet as o "
//                + " left join o.outletTypes as ot "
//                + " left join o.tpu as tpu "
//                + " left join tpu.district as d "
//                + " where 1 = 1 ";
//		
//		hql += " and a.isNewRecruitment = false ";
//		hql += " and a.isImportedAssignment = true ";
//		
//		if (userIds != null && userIds.size() > 0) {
//			hql += " and user.id in (:userIds) ";
//		}
//		
//		if (personInChargeId != null) {
//			hql += " and r.user.id = :personInChargeId ";
//		}
//		if (surveyMonthId != null) {
//			hql += " and a.surveyMonth.id = :surveyMonthId ";
//		}
//		if (StringUtils.isNotEmpty(search)) {
//			hql += " and ( "
//					+ " a.referenceNo like :search or "
//					+ " o.name like :search or "
//					+ " d.code like :search or "
//					+ " tpu.code like :search or "
//					+ " a.user.englishName like :search "
//	                + " ) ";
//		}
//		
//		hql += " group by a.assignmentId, user.id, "
//				+ " a.referenceNo, o.name, d.code, tpu.code, user.englishName ";

		Query query = getSession().createSQLQuery(sql);
		//Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		if (surveyMonthId != null) {
			query.setParameter("surveyMonthId", surveyMonthId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public Assignment getByIdForMaintenance(int id) {
		Criteria c = this.createCriteria().add(Restrictions.eq("id", id));
		c.setFetchMode("outlet", FetchMode.JOIN);
		c.setFetchMode("outlet.outletTypes", FetchMode.JOIN);
		c.setFetchMode("outlet.tpu", FetchMode.JOIN);
		c.setFetchMode("outlet.tpu.district", FetchMode.JOIN);
		c.setFetchMode("surveyMonth", FetchMode.JOIN);
		return (Assignment)c.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<KeyValueModel> searchDateSelectionNormalRevisitVerifyForAssignmentMaintenance(String search, int firstRecord, int displayLength,
			int userId, int outletId, String quotationState) {
		
		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select str(a.id) as key, "
				+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
						+ " when a.startDate is not null then (" + startDateFormat + " + ' - ' + " + endDateFormat + ") "
						+ " else '' "
						+ " end as value "
                + " from QuotationRecord as r "
                + " left join r.assignment as a "
                + " where 1 = 1 ";
		
		hql += " and r.user.id = :userId ";
		hql += " and r.outlet.id = :outletId ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and r.quotationState = :quotationState ";
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " " + assignedCollectionDateFormat + " like :search or "
					+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
						+ " when a.startDate is not null then (" + startDateFormat + " + ' - ' + " + endDateFormat + ") "
						+ " else '' "
						+ " end like :search "
	                + " ) ";
		}
		
		hql += " group by a.assignmentId, "
				+ " a.assignedCollectionDate, a.startDate, a.endDate ";
		
		hql += " order by a.assignedCollectionDate, a.startDate, a.endDate asc";

		Query query = getSession().createQuery(hql);

		query.setParameter("userId", userId);
		query.setParameter("outletId", outletId);
		query.setParameter("quotationState", quotationState);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(KeyValueModel.class));

		return query.list();
	}

	public long countSearchDateSelectionNormalRevisitVerifyForAssignmentMaintenance(String search,
			int userId, int outletId, String quotationState) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select count(distinct a.id) "
                + " from QuotationRecord as r "
                + " left join r.assignment as a "
                + " where 1 = 1 ";
		
		hql += " and r.user.id = :userId ";
		hql += " and r.outlet.id = :outletId ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and r.quotationState = :quotationState ";
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " " + assignedCollectionDateFormat + " like :search or "
					+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
						+ " when a.startDate is not null then (" + startDateFormat + " + ' - ' + " + endDateFormat + ") "
						+ " else '' "
						+ " end like :search "
	                + " ) ";
		}
		
		//hql += " group by a.assignmentId ";

		Query query = getSession().createQuery(hql);

		query.setParameter("userId", userId);
		query.setParameter("outletId", outletId);
		query.setParameter("quotationState", quotationState);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<KeyValueModel> searchDateSelectionIPForAssignmentMaintenance(String search, int firstRecord, int displayLength,
			int userId, int outletId) {
		
		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select str(a.id) as key, "
				+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
						+ " when a.startDate is not null then (" + startDateFormat + " + ' - ' + " + endDateFormat + ") "
						+ " else '' "
						+ " end as value "
                + " from QuotationRecord as r "
                + " left join r.assignment as a "
                + " where 1 = 1 ";
		
		hql += " and r.user.id = :userId ";
		hql += " and r.outlet.id = :outletId ";
		
		hql += " and (r.quotationState = 'IP') ";
		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " " + assignedCollectionDateFormat + " like :search or "
					+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
						+ " when a.startDate is not null then (" + startDateFormat + " + ' - ' + " + endDateFormat + ") "
						+ " else '' "
						+ " end like :search "
	                + " ) ";
		}
		
		hql += " group by a.assignmentId, "
				+ " a.assignedCollectionDate, a.startDate, a.endDate ";
		
		hql += " order by a.assignedCollectionDate, a.startDate, a.endDate asc";

		Query query = getSession().createQuery(hql);

		query.setParameter("userId", userId);
		query.setParameter("outletId", outletId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(KeyValueModel.class));

		return query.list();
	}

	public long countSearchDateSelectionIPForAssignmentMaintenance(String search,
			int userId, int outletId) {

		String startDateFormat = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDateFormat = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select count(distinct a.id) "
                + " from QuotationRecord as r "
                + " left join r.assignment as a "
                + " where 1 = 1 ";
		
		hql += " and r.user.id = :userId ";
		hql += " and r.outlet.id = :outletId ";

		hql += " and (r.quotationState = 'IP') ";
		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " " + assignedCollectionDateFormat + " like :search or "
					+ " case when a.assignedCollectionDate is not null then " + assignedCollectionDateFormat + " "
						+ " when a.startDate is not null then (" + startDateFormat + " + ' - ' + " + endDateFormat + ") "
						+ " else '' "
						+ " end like :search "
	                + " ) ";
		}
		
//		hql += " group by a.assignmentId, "
//				+ " a.assignedCollectionDate, a.startDate, a.endDate ";
//		
//		hql += " order by a.assignedCollectionDate, a.startDate, a.endDate asc";

		Query query = getSession().createQuery(hql);

		query.setParameter("userId", userId);
		query.setParameter("outletId", outletId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	public List<SummaryOfQuotations> getSummaryOfAssignments(Date startMonth, Date endMonth, List<String> outletTypes, List<Integer> districtIds, List<Integer> purpose) {
		
		String referenceMonth = String.format("FORMAT(sm.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		
		String sql = "Select " +
				" case when sm.ReferenceMonth is not null then " + referenceMonth + " else '' end as referenceMonth " +
				"	, pp.code as purpose"+
				"	, d.DistrictId as districtId" + 
				"	, d.Code as districtCode" + 
				"	, substring(ot.Code, len(ot.Code)-2, 3) as outletTypeCode" + 
				"	, ot.EnglishName as outletTypeName" + 
				"	, count(distinct a.AssignmentId) as quantity" + 
				"	From QuotationRecord as qr" + 
				"	left join Assignment as a on qr.AssignmentId = a.AssignmentId" + 
				"	left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId" + 
				"	left join Quotation as q on q.QuotationId = qr.QuotationId" + 
				"	left join Outlet as o on o.OutletId = qr.OutletId" + 
				"	left join Tpu as t on o.TpuId = t.TpuId" + 
				"	left join District as d on t.DistrictId = d.DistrictId" + 
				"	left join Unit as u on u.UnitId = q.UnitId" + 
				"	left join SubItem as si on u.SubItemId = si.SubItemId" + 
				"	left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId" + 
				"	left join Purpose as pp on pp.PurposeId = u.PurposeId" + 
				"	where pp.Code not in ('GHS', 'BMWPS')" +
				"	and sm.referenceMonth between :startMonth and :endMonth";
		
		if(outletTypes != null && outletTypes.size() > 0) {
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypes) ";
		}
		
		if(districtIds != null && districtIds.size() > 0) {
			sql += " and d.DistrictId in (:districtIds) ";
		}
		
		if(purpose != null && purpose.size() > 0) {
			sql += " and pp.PurposeId in (:purpose) ";
		}
		
		sql+=" group by sm.ReferenceMonth, substring(ot.Code, len(ot.Code)-2, 3), ot.EnglishName, pp.code, d.DistrictId, d.Code";
		
		sql+=" order by sm.ReferenceMonth asc, substring(ot.Code, len(ot.Code)-2, 3) asc, pp.code asc, d.Code asc";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if(outletTypes != null && outletTypes.size() > 0) {
			query.setParameterList("outletTypes", outletTypes);
		} 
		if(districtIds != null && districtIds.size() > 0) {
			query.setParameterList("districtIds", districtIds);
		} 
		if(purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		} 
		
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("districtId", StandardBasicTypes.INTEGER);
		query.addScalar("districtCode", StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode", StandardBasicTypes.STRING);
		query.addScalar("outletTypeName", StandardBasicTypes.STRING);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("quantity", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfQuotations.class));
		
		return query.list();
	}
	
	public List<SummaryOfQuotations> getSummaryOfQuotationsPurpose(Date startMonth, Date endMonth, List<String> outletTypes, List<Integer> districtIds, List<Integer> purpose) {
		
		String referenceMonth = String.format("FORMAT(sm.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		
		String sql = "Select distinct CONCAT(" +
				" 	case when sm.ReferenceMonth is not null then " + referenceMonth + " else '' end" +
				"	, substring(ot.Code, len(ot.Code)-2, 3)) as id" +
				"	, pp.Code as purpose" +
				"	From QuotationRecord as qr" + 
				"	left join Assignment as a on qr.AssignmentId = a.AssignmentId" + 
				"	left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId" + 
				"	left join Quotation as q on q.QuotationId = qr.QuotationId" + 
				"	left join Outlet as o on o.OutletId = qr.OutletId" + 
				"	left join Tpu as t on o.TpuId = t.TpuId" + 
				"	left join District as d on t.DistrictId = d.DistrictId" + 
				"	left join Unit as u on u.UnitId = q.UnitId" + 
				"	left join SubItem as si on u.SubItemId = si.SubItemId" + 
				"	left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId" + 
				"	left join Purpose as pp on pp.PurposeId = u.PurposeId" + 
				"	where pp.Code not in ('GHS', 'BMWPS')" +
				"	and sm.referenceMonth between :startMonth and :endMonth";
		
		if(outletTypes != null && outletTypes.size() > 0) {
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypes) ";
		}
		
		if(districtIds != null && districtIds.size() > 0) {
			sql += " and d.DistrictId in (:districtIds) ";
		}
		
		if(purpose != null && purpose.size() > 0) {
			sql += " and pp.PurposeId in (:purpose) ";
		}
		
		sql+=" group by sm.ReferenceMonth, substring(ot.Code, len(ot.Code)-2, 3), pp.Code";
		
		sql+=" order by "
				+ "CONCAT(case when sm.ReferenceMonth is not null then FORMAT(sm.ReferenceMonth, 'yyyyMM', 'en-us') else '' end, substring(ot.Code, len(ot.Code)-2, 3)) asc, "
				+ "pp.Code asc";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if(outletTypes != null && outletTypes.size() > 0) {
			query.setParameterList("outletTypes", outletTypes);
		} 
		if(districtIds != null && districtIds.size() > 0) {
			query.setParameterList("districtIds", districtIds);
		} 
		if(purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		} 
		
		query.addScalar("id", StandardBasicTypes.STRING);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfQuotations.class));
		
		return query.list();
	}
	
	public List<SummaryOfQuotations> getSummaryOfQuotations(Date startMonth, Date endMonth, List<String> outletTypes, List<Integer> districtIds, List<Integer> purpose) {
		
		String referenceMonth = String.format("FORMAT(sm.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
	
		String sql = "Select" +
				"	case when sm.ReferenceMonth is not null then " + referenceMonth + " else '' end as referenceMonth " +
				"	, pp.code as purpose" +
				"	, d.DistrictId as districtId" + 
				"	, d.Code as districtCode" + 
				"	, substring(ot.Code, len(ot.Code)-2, 3) as outletTypeCode" + 
				"	, ot.EnglishName as outletTypeName" + 
				"	, count(distinct qr.QuotationRecordId) as quantity" + 
				"	From QuotationRecord as qr" + 
				"	left join Assignment as a on qr.AssignmentId = a.AssignmentId" + 
				"	left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId" + 
				"	left join Quotation as q on q.QuotationId = qr.QuotationId" + 
				"	left join Outlet as o on o.OutletId = qr.OutletId" + 
				"	left join Tpu as t on o.TpuId = t.TpuId" + 
				"	left join District as d on t.DistrictId = d.DistrictId" + 
				"	left join Unit as u on u.UnitId = q.UnitId" + 
				"	left join SubItem as si on u.SubItemId = si.SubItemId" + 
				"	left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId" + 
				"	left join Purpose as pp on pp.PurposeId = u.PurposeId" + 
				"	where pp.Code not in ('GHS', 'BMWPS') and qr.IsBackNo = 0" +
				"	and sm.referenceMonth between :startMonth and :endMonth";
		
		if(outletTypes != null && outletTypes.size() > 0) {
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypes) ";
		}
		
		if(districtIds != null && districtIds.size() > 0) {
			sql += " and d.DistrictId in (:districtIds) ";
		}
		
		if(purpose != null && purpose.size() > 0) {
			sql += " and pp.PurposeId in (:purpose) ";
		}
		
		sql+=" group by sm.ReferenceMonth, substring(ot.Code, len(ot.Code)-2, 3), ot.EnglishName, pp.code, d.DistrictId, d.Code";
		
		sql+=" order by sm.ReferenceMonth asc, substring(ot.Code, len(ot.Code)-2, 3) asc, pp.code asc, d.Code asc";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if(outletTypes != null && outletTypes.size() > 0) {
			query.setParameterList("outletTypes", outletTypes);
		} 
		if(districtIds != null && districtIds.size() > 0) {
			query.setParameterList("districtIds", districtIds);
		} 
		if(purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		} 
		
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("districtId", StandardBasicTypes.INTEGER);
		query.addScalar("districtCode", StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode", StandardBasicTypes.STRING);
		query.addScalar("outletTypeName", StandardBasicTypes.STRING);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("quantity", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfQuotations.class));
		
		return query.list();
	}

	/*
	public SummaryOfQuotations.TotalCount getTCSummaryOfQuotations(Date startMonth, Date endMonth, List<String> outletTypes, List<Integer> districtIds, List<String> survey) {
		
		String hql = " select count(distinct a.assignmentId) as countAllAssignment, count(distinct qr.quotationRecordId) as countAllQuotationRecord "
				+ " from Assignment as a "
				+ "  inner join a.outlet as o "
				+ "  inner join o.tpu as tpu "
				+ "  inner join tpu.district as d "
				+ "  inner join a.quotationRecords as qr "
				+ "  inner join qr.quotation as q "
				+ "  inner join q.unit as u "
				+ "  inner join u.subItem as si "
				+ "  inner join si.outletType as ot "
				+ "  inner join a.surveyMonth as sm "
				+ "  inner join u.purpose as p "
				+ " where 1=1 "
				+ " and sm.referenceMonth >= :startMonth and sm.referenceMonth <= :endMonth ";
		if(survey != null && survey.size() > 0) {
			hql += " and p.survey in (:survey) ";
		}
		if(outletTypes != null && outletTypes.size() > 0) {
			hql += " and substring(ot.code, len(ot.code)-2, 3) in (:outletTypes) ";
		}
		if(districtIds != null && districtIds.size() > 0) {
			hql += " and d.districtId in (:districtIds) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if(survey != null && survey.size() > 0) {
			query.setParameterList("survey", survey);
		}
		if(outletTypes != null && outletTypes.size() > 0) {
			query.setParameterList("outletTypes", outletTypes);
		}
		if(districtIds != null && districtIds.size() > 0) {
			query.setParameterList("districtIds", districtIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfQuotations.TotalCount.class));
		
		return (SummaryOfQuotations.TotalCount) query.uniqueResult();
	}*/
	@SuppressWarnings("unchecked")
	public List<Outlet> getOutletByAssignmentReferenceNo(Date referenceMonth, String referenceNo){
		
		String hql = " select distinct o "
                + " from Assignment as a "
                + " inner join a.outlet as o "
                + " inner join a.surveyMonth as s "
                + " where s.referenceMonth = :referenceMonth "
                + " and a.referenceNo = :referenceNo";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("referenceNo", referenceNo);
		query.setParameter("referenceMonth", referenceMonth);
		return query.list();
		
	}
	
	public List<AssignmentReferenceNoModel> getAssignmentsByReferenceNo(List<String> referenceNo){
		String hql = " select o.outletId as outletId "
				+ " , case when a.isImportedAssignment = true then o.name else a.additionalFirmName end as firm "
				+ " , o.marketName as marketName "
				+ " , case when a.isImportedAssignment = false then otpu.code else atpu.code end as tpu "
				+ " , case when a.isImportedAssignment = false then o.streetAddress else a.additionalFirmAddress end as address "
				+ " , case when a.isImportedAssignment = false then o.detailAddress else a.additionalFirmAddress end as detailAddress "
				+ " , case when a.isImportedAssignment = false then o.latitude else a.additionalLatitude end as latitude "
				+ " , case when a.isImportedAssignment = false then o.longitude else a.additionalLongitude end as longitude "
				+ " , a.referenceNo as referenceNo "
                + " from Assignment as a "
                + " left join a.outlet as o "
                + " left join o.tpu as otpu "
                + " left join a.additionalTpu as atpu "
                + " where a.referenceNo in (:referenceNo) "
                + " group by o.outletId, o.name, a.additionalFirmName, o.marketName, a.isImportedAssignment, "
                + " otpu.code, atpu.code, o.streetAddress, o.detailAddress, a.additionalFirmAddress, o.latitude, a.additionalLatitude "
                + " ,o.longitude, a.additionalLongitude, a.referenceNo ";
		
		Query query = getSession().createQuery(hql);
		query.setParameterList("referenceNo", referenceNo);
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReferenceNoModel.class));
		
		return query.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Assignment> getAssignmentByReferenceMonth(Date referenceMonth){
		return 
			this.createCriteria("a").createAlias("a.surveyMonth", "sm")
				.add(Restrictions.eq("sm.referenceMonth", referenceMonth)).list();
		
	}
	
	public String getTpuByReferenceNo(String referenceNo){
		
		String hql = "select distinct case when a.isImportedAssignment = 1 then at.code else t.code end as tpu "
                + " from Assignment as a "
				+ " left join a.additionalTpu as at "
                + " left join a.outlet as  o"
				+ " left join o.tpu as t "
                + " where "
                + " a.referenceNo = :referenceNo";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("referenceNo", referenceNo);
		return (String)query.uniqueResult();
	}
	
	
	public List<TpuReferenceNoModel> getReferenceNoRelatedTpu(List<String> referenceNo){
		String hql = " select distinct a.referenceNo as referenceCode, case when a.isImportedAssignment = 1 then at.code else t.code end as tpuCode "
				+ " from Assignment as a "
				+ " left join a.outlet as o"
				+ " left join o.tpu as t "
				+ " left join a.additionalTpu as at "
				+ " where a.referenceNo in (:referenceNo)";
		Query query = getSession().createQuery(hql);
		query.setParameterList("referenceNo", referenceNo);
		
		query.setResultTransformer(Transformers.aliasToBean(TpuReferenceNoModel.class));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<TimeLogSelect2Item> getTimeLogSelect2ItemByTelephoneReferenceNo(Date referenceMonth, String survey, Integer userId, String referenceNo, int firstRecord, int displayLength){
		
		String hql = "select distinct a.referenceNo as id "
				//+ " qt.firmStatus as firmStatus, "
				//+ " count(distinct qc.quotationRecordId) as count, "
				//+ " count(distinct qt.quotationRecordId) as total "
                + " from Assignment as a "
                + " inner join a.surveyMonth as s "
                + " left join a.quotationRecords as qt "
                + " left join qt.quotation as q"
                + " left join q.unit as u"
                + " left join u.purpose as p"
                + " where 1=1 ";
		if (referenceMonth != null){
			hql += " and s.referenceMonth = :referenceMonth ";
		}
		if (!StringUtils.isEmpty(survey) && !"Others".equals(survey)){
        	hql += " and ((a.isImportedAssignment = true and a.survey = :survey)"
        			+ " or (a.isImportedAssignment = false and p.survey = :survey))";
        } else if(!StringUtils.isEmpty(survey)){
        	hql += " and ((a.isImportedAssignment = true and (a.survey not in :surveys or a.survey is null))"
        			+ " or (a.isImportedAssignment = false and (p.survey not in :surveys or p.survey is null)))";
        }
		if (referenceNo != null) {
			hql += " and a.referenceNo like :referenceNo ";
		}
		if (userId != null) {
			hql += " and a.user.id = :userId ";
		}
        //hql += " group by a.referenceNo, qt.firmStatus ";
        hql += " group by a.referenceNo ";
        
        hql += " order by a.referenceNo asc ";
		
		Query query = getSession().createQuery(hql);
//		query.setParameter("status", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		
		if (referenceMonth != null){
			query.setParameter("referenceMonth", referenceMonth);
		}
		
		if (!StringUtils.isEmpty(survey) && !"Others".equals(survey)){
			query.setParameter("survey", survey);
		} else if (!StringUtils.isEmpty(survey)){
        	query.setParameterList("surveys", new String[]{"MRPS", "GHS", "BMWPS"});
        }
		
		if (referenceNo != null) {
			query.setParameter("referenceNo", String.format("%%%s%%", referenceNo));
		}
		
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		
		if(displayLength > 0) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLength);
		}
		query.setResultTransformer(Transformers.aliasToBean(TimeLogSelect2Item.class));
		return query.list();
		
	}
	
	public long countTimeLogSelect2ItemByTelephoneReferenceNo(Date referenceMonth, String survey, Integer userId, String referenceNo){
		
		String hql = "select count(distinct a.referenceNo) as cnt "
                + " from Assignment as a "
                + " inner join a.surveyMonth as s "
                + " left join a.quotationRecords as qt "
                + " left join qt.quotation as q"
                + " left join q.unit as u"
                + " left join u.purpose as p"
                + " where 1=1 ";
		
		if (referenceMonth != null){
			hql += " and s.referenceMonth = :referenceMonth ";
		}
		if (!StringUtils.isEmpty(survey) && !"Others".equals(survey)){
        	hql += " and ((a.isImportedAssignment = true and a.survey = :survey)"
        			+ " or (a.isImportedAssignment = false and p.survey = :survey))";
        } else if(!StringUtils.isEmpty(survey)){
        	hql += " and ((a.isImportedAssignment = true and (a.survey not in :surveys or a.survey is null))"
        			+ " or (a.isImportedAssignment = false and (p.survey not in :surveys or p.survey is null)))";
        }
		if (userId != null){
			hql += " and a.user.id = :userId ";
		}
		if (referenceNo != null) {
			hql += " and a.referenceNo like :referenceNo ";
		}
		
		Query query = getSession().createQuery(hql);
//		query.setParameter("status", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		
		if (referenceMonth != null){
			query.setParameter("referenceMonth", referenceMonth);
		}
		if (!StringUtils.isEmpty(survey) && !"Others".equals(survey)){
			query.setParameter("survey", survey);
		} else if (!StringUtils.isEmpty(survey)){
        	query.setParameterList("surveys", new String[]{"MRPS", "GHS", "BMWPS"});
        }
		if (userId != null){
			query.setParameter("userId", userId);
		}
		if (referenceNo != null) {
			query.setParameter("referenceNo", String.format("%%%s%%", referenceNo));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public TimeLogSelect2Item getTimeLogTelephoneReferenceNoDetailById(Date referenceMonth, String referenceNo){
		
		String sql = " select sub.id as id, min(sub.firmStatus) as firmStatus, sum(sub.countQcId) as count, sum(sub.countQtId) as total "
				+ " from ( "
				+ " select distinct a.referenceNo as id "
				+ ", case when qt.FirmStatus is null then 1 else qt.FirmStatus end as firmStatus "
				+ ", count(distinct qc.QuotationRecordId) as countQcId, count(distinct qt.QuotationRecordId) as countQtId"
				+ " from assignment as a "
				+ " inner join SurveyMonth as s on a.SurveyMonthId = s.SurveyMonthId "
				+ " left join QuotationRecord as qt on a.AssignmentId = qt.AssignmentId and qt.isBackNo = 0 and qt.isBackTrack = 0 "
				+ " left join QuotationRecord as qc on a.AssignmentId = qc.AssignmentId and qc.Status != :status "
					+ " and qc.quotationRecordId = qt.quotationRecordId"
				+ " where 1 = 1 ";
		
		if (referenceMonth != null){
			sql += " and s.referenceMonth = :referenceMonth ";
		}
		if (referenceNo != null) {
			sql += " and a.referenceNo like :referenceNo ";
		}
		
		sql += " group by a.ReferenceNo, qt.FirmStatus "
			+ " ) as sub "
			+ " group by sub.id ";
						
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("status", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		
		if (referenceMonth != null){
			sqlQuery.setParameter("referenceMonth", referenceMonth);
		}
		
		if (referenceNo != null) {
			sqlQuery.setParameter("referenceNo", String.format("%%%s%%", referenceNo));
		}
		
		sqlQuery.addScalar("id", StandardBasicTypes.STRING);
		sqlQuery.addScalar("firmStatus", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("count", StandardBasicTypes.LONG);
		sqlQuery.addScalar("total", StandardBasicTypes.LONG);
		
		sqlQuery.setMaxResults(1);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(TimeLogSelect2Item.class));
		return (TimeLogSelect2Item)sqlQuery.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<TimeLogSelect2Item> getTimeLogSelect2ItemByFieldworkReferenceNo(Date referenceMonth, String survey, Integer userId, String referenceNo, int firstRecord, int displayLength){
		
		String hql = "select distinct a.referenceNo as id "
				//+ " o.outletMarketType as marketType, "
				//+ " case when a.isImportedAssignment = 1 then a.additionalFirmAddress else o.streetAddress end as address, "
				//+ " sum (case when a.isImportedAssignment = 1 then a.additionalNoOfForms else null end) as building, "
				//+ " count(distinct qc.quotationRecordId) as count, "
				//+ " count(distinct qt.quotationRecordId) as total "
                + " from Assignment as a "
				+ " inner join a.surveyMonth as s"
                + " left join a.outlet as o"
                + " left join a.quotationRecords as qc"
                // + " qc.quotationState != :quotationState and "
//                	+ " qc.status != :status"
                + " left join qc.quotation as q"
                + " left join q.unit as u"
                + " left join u.purpose as p"
                + " where 1=1 ";
                if (referenceMonth != null){
                	hql +=" and s.referenceMonth = :referenceMonth ";
                }
                if (!StringUtils.isEmpty(survey) && !"Others".equals(survey)){
                	hql += " and ((a.isImportedAssignment = true and a.survey = :survey)"
                			+ " or (a.isImportedAssignment = false and p.survey = :survey))";
                } else if(!StringUtils.isEmpty(survey)){
                	hql += " and ((a.isImportedAssignment = true and (a.survey not in :surveys or a.survey is null))"
                			+ " or (a.isImportedAssignment = false and (p.survey not in :surveys or p.survey is null)))";
                }
        		if (referenceNo != null) {
        			hql += " and a.referenceNo like :referenceNo ";
        		}
        		if (userId != null) {
        			hql += " and a.user.id = :userId ";
        		}
            		
                hql += " group by a.referenceNo, o.outletMarketType, case when a.isImportedAssignment = 1 then a.additionalFirmAddress else o.streetAddress end";
                
                hql += " order by a.referenceNo asc";
		
		Query query = getSession().createQuery(hql);
//		query.setParameter("status", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		//query.setParameter("quotationState", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_VERIFY);

		if (referenceMonth != null){
			query.setParameter("referenceMonth", referenceMonth);			
		}
		if (!StringUtils.isEmpty(survey) && !"Others".equals(survey)){
			query.setParameter("survey", survey);	
        } else if (!StringUtils.isEmpty(survey)){
        	query.setParameterList("surveys", new String[]{"MRPS", "GHS", "BMWPS"});
        }
		if (referenceNo != null) {
			query.setParameter("referenceNo", String.format("%%%s%%", referenceNo));
		}
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		
		if(displayLength > 0) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLength);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(TimeLogSelect2Item.class));
		return query.list();
	}
	
	public long countTimeLogSelect2ItemByFieldworkReferenceNo(Date referenceMonth, String survey, Integer userId, String referenceNo){
		
		String hql = "select count(distinct a.referenceNo) as cnt "
                + " from Assignment as a "
				+ " inner join a.surveyMonth as s"
                + " left join a.outlet as o"
                //+ " left join a.quotationRecords as qt "
                + " left join a.quotationRecords as qc "
                + " left join qc.quotation as q"
                + " left join q.unit as u"
                + " left join u.purpose as p"
                + " where 1=1 ";
		
		if (referenceMonth != null){
			hql +=" and s.referenceMonth = :referenceMonth ";
		}
		if (!StringUtils.isEmpty(survey) && !"Others".equals(survey)){
        	hql += " and ((a.isImportedAssignment = true and a.survey = :survey)"
        			+ " or (a.isImportedAssignment = false and p.survey = :survey))";
        } else if(!StringUtils.isEmpty(survey)){
        	hql += " and ((a.isImportedAssignment = true and (a.survey not in :surveys or a.survey is null))"
        			+ " or (a.isImportedAssignment = false and (p.survey not in :surveys or p.survey is null)))";
        }
		if (userId != null){
			hql += " and a.user.id = :userId ";
		}
		if (referenceNo != null) {
			hql += " and a.referenceNo like :referenceNo ";
		}
        		
		Query query = getSession().createQuery(hql);
//		query.setParameter("status", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);

		if (referenceMonth != null){
			query.setParameter("referenceMonth", referenceMonth);			
		}
		if (!StringUtils.isEmpty(survey) && !"Others".equals(survey)){
			query.setParameter("survey", survey);
		} else if (!StringUtils.isEmpty(survey)){
        	query.setParameterList("surveys", new String[]{"MRPS", "GHS", "BMWPS"});
        }
		if (userId !=null){
			query.setParameter("userId", userId);
		}
		if (referenceNo != null) {
			query.setParameter("referenceNo", String.format("%%%s%%", referenceNo));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public TimeLogSelect2Item getTimeLogFieldworkReferenceNoDetailById(Date referenceMonth, String referenceNo){
		
		String sql = "select sub.id as id, min(sub.marketType) as marketType, sub.address as address "
				+ ", sum(sub.building) as building, sum(sub.count) as count, sum(total) as total "
				+ " from ( "
				+ " select distinct a.referenceNo as id "
				+ ", o.OutletMarketType as marketType "
				+ ", case when a.IsImportedAssignment = 1 then a.AdditionalFirmAddress "
				+ " when a.IsImportedAssignment = 0 then o.StreetAddress else '' end as address "
				+ ", sum(case when a.IsImportedAssignment = 1 then a.AdditionalNoOfForms else 0 end) as building "
				+ ", count(distinct qc.QuotationRecordId) as count "
				+ ", count(distinct qt.QuotationRecordId) as total "
				+ " from Assignment as a "
				+ " inner join SurveyMonth as s on a.SurveyMonthId = s.SurveyMonthId "
				+ " left join Outlet as o on a.OutletId = o.OutletId "
				+ " left join QuotationRecord as qt on a.AssignmentId = qt.AssignmentId and qt.isBackNo = 0 and qt.isBackTrack = 0 "
				+ " left join QuotationRecord as qc on a.AssignmentId = qc.AssignmentId and qc.Status != :status "
					+ " and qc.quotationRecordId = qt.quotationRecordId"
				+ " where 1 = 1 ";
		if (referenceMonth != null){
			sql +=" and s.referenceMonth = :referenceMonth ";
		}
		if (referenceNo != null) {
			sql += " and a.referenceNo like :referenceNo ";
		}
		sql += " group by a.ReferenceNo, o.OutletMarketType "
			+ ", a.IsImportedAssignment, a.AdditionalFirmAddress, o.StreetAddress, a.AdditionalNoOfForms "
			+ " ) as sub "
			+ " group by sub.id, sub.address "
			+ " order by sub.id asc ";
		
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("status", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		
		if (referenceMonth != null){
			sqlQuery.setParameter("referenceMonth", referenceMonth);
		}
		
		if (referenceNo != null) {
			sqlQuery.setParameter("referenceNo", String.format("%%%s%%", referenceNo));
		}
		
		sqlQuery.addScalar("id", StandardBasicTypes.STRING);
		sqlQuery.addScalar("marketType", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("address", StandardBasicTypes.STRING);
		sqlQuery.addScalar("building", StandardBasicTypes.LONG);
		sqlQuery.addScalar("count", StandardBasicTypes.LONG);
		sqlQuery.addScalar("total", StandardBasicTypes.LONG);
		
		sqlQuery.setMaxResults(1);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(TimeLogSelect2Item.class));
		return (TimeLogSelect2Item)sqlQuery.uniqueResult();
	}
	
	public Integer getVerifiedQuotationCount(Date referenceMonth, String referenceNo) {
		String hql = "select "
				+ " count(distinct qr.quotationRecordId) "
                + " from Assignment as a "
				+ " inner join a.surveyMonth as s"
                + " left join a.quotationRecords as qr on qr.status not in (:status) "
                + " where s.referenceMonth = :referenceMonth "
                + " and a.referenceNo like :referenceNo";
		
		Query query = getSession().createQuery(hql);
		query.setParameterList("status", new String[]{SystemConstant.QUOTATIONRECORD_STATUS_APPROVED, SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED});
		query.setParameter("quotationState", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
		query.setParameter("referenceNo",referenceNo);
		query.setParameter("referenceMonth", referenceMonth);
		return (Integer)query.uniqueResult();
	}
	
	public List<NonScheduleAssignmentList> getOnlineNonScheduleAssignment(Integer userId){
		String hql = "select a.assignmentId as assignmentId, a.assignedCollectionDate as assignedCollectionDate"
				+ ", a.startDate as startDate, a.endDate as endDate"
				+ ", u.userId as userId"
				+ ", o.id as outletId, o.name as outletName, o.collectionMethod as collectionMethod"
				+ ", o.streetAddress as streetAddress, o.detailAddress as detailAddress"
				+ ", t.tpuId as tpuId, t.code as tpuCode, t.description as tpuDescription"
				+ ", d.districtId as districtId, d.code as districtCode"
				+ ", d.chineseName as districtChineseName, d.englishName as districtEnglishName"
				+ ", count(distinct total.quotationRecordId) as totalCnt"
				+ ", count(distinct unstarted.quotationRecordId) as unstartedCnt "
				+ ", count(distinct normal.quotationRecordId) as normalCnt "
				+ ", count(distinct verify.quotationRecordId) as verifyCnt "
				+ ", count(distinct revisit.quotationRecordId) as revisitCnt "
				+ " from Assignment as a"
				+ " left join a.user as u"
				+ " left join a.outlet as o"
				+ " left join o.tpu as t"
				+ " left join t.district as d"
				+ " inner join a.quotationRecords as total "
				+ " 	on ((total.assignedCollectionDate is NULL and total.status not in ('Approved', 'Submitted'))"
				+ " 		or total.quotationState = 'IP') and total.isBackNo = false "
				+ " left join a.quotationRecords as unstarted"
				+ " 	on ((unstarted.assignedCollectionDate is NULL and unstarted.status = 'Blank')"
				+ " 		or unstarted.quotationState = 'IP') and unstarted.isBackNo = false "
				+ " left join a.quotationRecords as normal"
				+ " 	on normal=total and normal.quotationState = 'Normal' "
				+ " left join a.quotationRecords as verify"
				+ " 	on verify=total and verify.quotationState = 'Verify' "
				+ " left join a.quotationRecords as revisit"
				+ " 	on revisit=total and revisit.quotationState = 'Revisit' "
				+ " where u.userId = :userId"
				+ " group by a.assignmentId, a.assignedCollectionDate, a.startDate, a.endDate"
				+ ", u.userId"
				+ ", o.id, o.name, o.collectionMethod"
				+ ", o.streetAddress , o.detailAddress"
				+ ", t.tpuId, t.code, t.description"
				+ ", d.districtId, d.code"
				+ ", d.chineseName, d.englishName";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("userId", userId);
		query.setResultTransformer(Transformers.aliasToBean(NonScheduleAssignmentList.class));
		return query.list();		
	}
	
//	@SuppressWarnings("unchecked")
//	public List<Integer> getAssignmentForRandomCase(int staffId, int surveyMonthId) {
//		String hql = " select a.id as id "
//				+ " from Assignment as a "
//				+ " left join a.peCheckTask as task "
//				+ " left join a.quotationRecords as qr "
//				+ " left join a.surveyMonth as m "
//				+ " left join qr.quotation as q "
//				+ " left join q.unit as u "
//				+ " left join u.subItem as si "
//				+ " left join si.outletType as ot "
//				+ " left join u.purpose as p "
//				//+ " left join a.quotationRecords as approved on approved.status != 'Approved' "
//				//+ " left join a.quotationRecords as approvedThisWeek on approvedThisWeek.status = 'Approved' and approvedThisWeek.approvedDate between :todayMinus7 and :today "
//				+ " where a.user.id = :staffId "
//				+ " and (qr.status != 'Approved' or qr.status = 'Approved' and qr.approvedDate between :todayMinus7 and :today) "
//				+ " and (task.isSelected = false or task.id is null)"
//				+ " and m.surveyMonthId = :surveyMonthId "
//				+ " and substring(ot.code, length(ot.code)-2 ,3) not in ("
//				+ "		select eot.shortCode from PEExcludedOutletType as et "
//				+ "			inner join et.outletType as eot "
//				+ ") "
//				+ " and p.peIncluded = true "
//				+ " group by a.id, a.outlet.firmCode "
//				//+ ", a.outlet.firmCode "
////				+ " having ( "
////					+ " count(qr) <> count(approved) "
////					+ " or "
////					+ " (count(qr) = count(approvedThisWeek)) "
////				+ " ) "
//				+ " order by a.outlet.firmCode asc ";
//		
//		Query query = getSession().createQuery(hql);
//		
//		query.setParameter("staffId", staffId);
//		query.setParameter("surveyMonthId", surveyMonthId);
//		
//		Date today = new Date();
//		today = DateUtils.truncate(today, Calendar.DATE);
//		Date todayMinus7 = DateUtils.addDays(today, -7);
//		query.setParameter("todayMinus7", todayMinus7);
//		query.setParameter("today", today);
//		
//		return query.list();
//	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getAssignmentForRandomCase(int staffId, int surveyMonthId, Date excludedMonth){
		String sql = " Select a.AssignmentId "
				+ " from Assignment as a "
				+ " left join PECheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join Outlet as o on o.OutletId = a.OutletId"
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId"
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
				+ " left join Unit as u on u.UnitId = q.UnitId"
				+ " left join Purpose as pp on pp.PurposeId = u.PurposeId"
				+ " left join SubItem as si on si.SubItemId = u.SubItemId"
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
				+ " left join AllocationBatch as ab on qr.AllocationBatchId = ab.AllocationBatchId"
				+ " where a.SurveyMonthId = :surveyMonthId"
				+ " and (ab.allocationBatchId is null or ab.status = 2)"
				+ " and (qr.status != 'Approved' or qr.status = 'Approved' and qr.approvedDate between :todayMinus7 and :today) "
				+ " and (pe.isSelected = 0 or pe.peCheckTaskId is null)"
				+ " and a.userId = :userId"
				+ " and subString(ot.code, len(ot.code)-2, 3) not in ("
					+ " select eot.shortCode from PEExcludedOutletType as eot)"
				+ " and pp.peIncluded = 1";
		
		if(excludedMonth != null){
			sql += " and o.outletId not in ("
					+ " select a.outletId from PECheckTask as pe"
					+ " inner join Assignment as a on a.AssignmentId = pe.AssignmentId"
					+ " inner join SurveyMonth as sm on sm.SurveyMonthId = a.SurveyMonthId"
					+ " where sm.referenceMonth >= :excludedMonth"
					+ " group by a.outletId)";
		}
		
		sql += " group by a.AssignmentId, o.firmCode";
		sql += " order by o.firmCode asc";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("userId", staffId);
		query.setParameter("surveyMonthId", surveyMonthId);
		
		if (excludedMonth != null){
			query.setParameter("excludedMonth", excludedMonth);
		}
		
		Date today = new Date();
		today = DateUtils.truncate(today, Calendar.DATE);
		Date todayMinus7 = DateUtils.addDays(today, -7);
		query.setParameter("todayMinus7", todayMinus7);
		query.setParameter("today", today);
		
		return query.list();
	}
	
	public Assignment getOneAssignmentByCollectiondateOutletUser(Date collectionDate, int outletId, int userId) {
		return (Assignment)this.createCriteria("a")
				.add(Restrictions.eq("a.collectionDate", collectionDate))
				.add(Restrictions.eq("a.outlet.id", outletId))
				.add(Restrictions.eq("a.user.id", userId))
				.setFetchSize(1)
				.uniqueResult();
	}
	

	public List<Assignment> getNotSubmittedRandomCaseAssignment(int userId, int noOfAssignment) {
		return this.createCriteria("a")
				.createAlias("a.peCheckForm", "form", JoinType.LEFT_OUTER_JOIN)
				.createAlias("a.peCheckTask", "task")
				.add(Restrictions.eq("task.isSelected", true))
				.add(Restrictions.eq("task.isRandomCase", true))

				.add(Restrictions.or(
					Restrictions.and(
						Restrictions.eq("form.user.id", userId),
						Restrictions.ne("form.status", "Submitted")
					),
					Restrictions.isNull("form.id")
				))
				.setFetchSize(noOfAssignment)
				.list();
	}
	
	/**
	 * Data Sync
	 */
	public List<AssignmentSyncData> getUpdatedAssignment(Date lastSyncTime, Integer[] assignmentIds, Integer[] itineraryPlanIds){
		String hql = "select a.assignmentId as assignmentId"
				+ ", a.assignedCollectionDate as assignedCollectionDate, a.createdDate as createdDate"
				+ ", a.modifiedDate as modifiedDate, u.userId as userId"
				+ ", a.startDate as startDate, a.endDate as endDate"
				+ ", o.outletId as outletId, a.collectionMethod as collectionMethod"
				+ ", a.status as status, a.collectionDate as collectionDate"
				+ ", sm.surveyMonthId as surveyMonthId, a.isNewRecruitment as isNewRecruitment"
				+ ", a.referenceNo as referenceNo, a.workingSession as workingSession"
				+ ", au.userId as assignedUserId, a.isCompleted as isCompleted"
				+ ", a.additionalFirmNo as additionalFirmNo, a.additionalFirmName as additionalFirmName"
				+ ", a.additionalFirmAddress as additionalFirmAddress, a.additionalContactPerson as additionalContactPerson"
				+ ", a.additionalNoOfForms as additionalNoOfForms, a.survey as survey"
				+ ", a.isImportedAssignment as isImportedAssignment, d.districtId as additionalDistrictId"
				+ ", t.tpuId as additionalTpuId, a.additionalLatitude as additionalLatitude"
				+ ", a.additionalLongitude as additionalLongitude"
				+ ", case when a.outletDiscountRemark is null or a.outletDiscountRemark = '' "
				+ " then o.discountRemark "
				+ " else a.outletDiscountRemark end as outletDiscountRemark"
				+ ", a.lockFirmStatus as lockFirmStatus, a.approvedDate as approvedDate"
				+ " from Assignment as a"
				+ " left join a.user as u"
				+ " left join a.outlet as o"
				+ " left join a.surveyMonth as sm"
				+ " left join a.assignedUser as au"
				+ " left join a.additionalDistrict as d"
				+ " left join a.additionalTpu as t"
				+ " left join a.plannedItinerary as pi"
				+ " left join a.unplannedItinerary as upi"
				+ " where 1=1";
		
		if(lastSyncTime != null){
			hql += " and a.modifiedDate >= :modifiedDate";
		}
		
		hql += " and (";
		
		if(assignmentIds!=null && assignmentIds.length>0){
			hql += " a.assignmentId in ( :assignmentIds )";
		}
		
		if(assignmentIds!=null && assignmentIds.length>0 && itineraryPlanIds!=null && itineraryPlanIds.length>0){
			hql += " or";
		}
		
		if(itineraryPlanIds!=null && itineraryPlanIds.length>0){
			hql += " pi.itineraryPlanId in ( :itineraryPlanIds ) or upi.itineraryPlanId in ( :itineraryPlanIds )";
		}
		
		hql += " )";
		
		hql += " group by a.assignmentId"
				+ ", a.assignedCollectionDate, a.createdDate"
				+ ", a.modifiedDate, u.userId"
				+ ", a.startDate, a.endDate"
				+ ", o.outletId, a.collectionMethod"
				+ ", a.status, a.collectionDate"
				+ ", sm.surveyMonthId, a.isNewRecruitment"
				+ ", a.referenceNo, a.workingSession"
				+ ", au.userId, a.isCompleted"
				+ ", a.additionalFirmNo, a.additionalFirmName"
				+ ", a.additionalFirmAddress, a.additionalContactPerson"
				+ ", a.additionalNoOfForms, a.survey"
				+ ", a.isImportedAssignment, d.districtId"
				+ ", t.tpuId, a.additionalLatitude"
				+ ", a.additionalLongitude, a.outletDiscountRemark"
				+ ", a.lockFirmStatus, a.approvedDate"
				+ ", o.discountRemark";
		
		Query query = this.getSession().createQuery(hql);
		
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		if(itineraryPlanIds!=null && itineraryPlanIds.length>0){
			query.setParameterList("itineraryPlanIds", itineraryPlanIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentSyncData.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.ruaCaseApproval.TableList> getRUACaseApprovalTableList(String search,
			int firstRecord, int displayLength, Order order,
			List<Integer> userIds,
			String outletTypeId, Integer personInChargeId,
			Integer[] districtId, Integer[] tpuId) {

		String hql = "select a.id as assignmentId, "
				+ " o.name as firm, "
				+ " o.id as outletId, "
				+ " min(ot.shortCode + ' - ' + ot.englishName) as outletType, "
				+ " count(distinct ot) as noOfOutletType, "
				+ " d.code as district, "
				+ " tpu.code as tpu, "
				+ " count(distinct rall) as noOfQuotation, "
				+ " count(distinct r) as noOfRUA, "
				+ " user.englishName as personInCharge, "
				+ " case when count(r.id) = count(r2.id) then 'Not Start' else 'In Progress' end as assignmentStatus "
                + " from Assignment as a "
                + " inner join a.quotationRecords as r "
                + " left join a.quotationRecords as r2 on r2.status = 'Blank' and r=r2 "
                + " left join a.quotationRecords as rall "
                + " inner join a.outlet as o "
                + " inner join r.user as user "
                + " inner join o.tpu as tpu "
                + " inner join tpu.district as d "
                + " inner join r.quotation as q "
                + " left join o.outletTypes as ot "
                + " where 1 = 1 ";
		
		hql += " and a.isNewRecruitment = false ";
		
		hql += " and r.availability = 5 ";

		hql += " and r.isBackNo = false and r.isBackTrack = false "
				+ " and r.status = 'Submitted' ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.supervisor.id in (:userIds) ";
		}
		
		if (!StringUtils.isBlank(outletTypeId)) {
			hql += " and ot.shortCode = :outletTypeId ";
		}
		if (personInChargeId != null) {
			hql += " and r.user.id = :personInChargeId ";
		}
		if (districtId != null && districtId.length > 0) {
			hql += " and d.id in :districtId ";
		}
		if (tpuId != null && tpuId.length > 0) {
			hql += " and tpu.id in :tpuId ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " o.name like :search or "
					+ " user.englishName like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search "
	                + " ) ";
		}
		
		hql += " group by a.id, "
				+ " o.name, o.outletId, "
				+ " user.englishName, d.code, tpu.code ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (!StringUtils.isBlank(outletTypeId)) {
			query.setParameter("outletTypeId", outletTypeId);
		}
		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		if (districtId != null && districtId.length > 0) {
			query.setParameterList("districtId", districtId);
		}
		if (tpuId != null && tpuId.length > 0) {
			query.setParameterList("tpuId", tpuId);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (displayLength > 0) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLength);
		}

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.ruaCaseApproval.TableList.class));

		return query.list();
	}

	public long countRUACaseApprovalTableList(String search,
			List<Integer> userIds,
			String outletTypeId, Integer personInChargeId,
			Integer[] districtId, Integer[] tpuId) {

		String hql = "select count(distinct a.id) "
                + " from Assignment as a "
                + " inner join a.quotationRecords as r "
                + " left join a.quotationRecords as r2 on r2.status = 'Blank' and r=r2 "
                + " left join a.quotationRecords as rall "
                + " inner join a.outlet as o "
                + " inner join r.user as user "
                + " inner join o.tpu as tpu "
                + " inner join tpu.district as d "
                + " inner join r.quotation as q "
                + " left join o.outletTypes as ot "
                + " where 1 = 1 ";
		
		hql += " and a.isNewRecruitment = false ";
		
		hql += " and r.availability = 5 ";
		
		hql += " and r.isBackNo = false and r.isBackTrack = false "
				+ " and r.status = 'Submitted' ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.supervisor.id in (:userIds) ";
		}
		
		if (!StringUtils.isBlank(outletTypeId)) {
			hql += " and ot.shortCode = :outletTypeId ";
		}
		if (personInChargeId != null) {
			hql += " and r.user.id = :personInChargeId ";
		}
		if (districtId != null && districtId.length > 0) {
			hql += " and d.id in :districtId ";
		}
		if (tpuId != null && tpuId.length > 0) {
			hql += " and tpu.id in :tpuId ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " o.name like :search or "
					+ " user.englishName like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search "
	                + " ) ";
		}
		
		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (!StringUtils.isBlank(outletTypeId)) {
			query.setParameter("outletTypeId", outletTypeId);
		}
		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		if (districtId != null && districtId.length > 0) {
			query.setParameterList("districtId", districtId);
		}
		if (tpuId != null && tpuId.length > 0) {
			query.setParameterList("tpuId", tpuId);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public List<Integer> getApprovedAssignment(List<Integer> ids){
		
		String hql = "select a.assignmentId "
				+ " from Assignment as a "
				+ " inner join a.quotationRecords as allQR "
					+ " on allQR.isBackNo = false"
				+ " left join a.quotationRecords as approvedQR on approvedQR.status = 'Approved' "
					+ " and approvedQR.isBackNo = false"
				+ " where a.assignmentId in  :ids  "
				+ " group by a.assignmentId "
				+ " having count(distinct allQR.quotationRecordId) = count(distinct approvedQR.quotationRecordId) ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameterList("ids", ids);
		return query.list();
		
	}

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.newRecruitmentApproval.TableList> getNewRecruitmentApprovalTableList(String search,
			int firstRecord, int displayLength, Order order,
			List<Integer> userIds,
			String outletTypeId) {
		
		String ruaDateFormat = String.format("FORMAT(q.ruaDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String minCollectionDateFormat = String.format("FORMAT(min(r.collectionDate), '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String referenceMonthDateFormat = String.format("FORMAT(surveyMonth.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		

		String hql = "select a.assignmentId as assignmentId, "
				+ " o.id as outletId, "
				+ " o.firmCode as firmCode, "
				+ " o.name as firm, "
//				+ " min(ot.shortCode + ' - ' + ot.englishName) as outletType, "
				+ " d.code as district, "
				+ " tpu.code as tpu, "
				+ " o.detailAddress as address, "
//				+ " count(distinct q) as originalNoOfQuotation, "
				+ " count(distinct q) as noOfQuotationRecruited, "
//				+ " count(distinct q) + count(r) as noOfQuotationAfterRecruitment, "
//				+ " case when q.ruaDate is null then '' else " + ruaDateFormat + " end as ruaStartingFrom, "
				+ " case when surveyMonth.referenceMonth is null then '' else " + referenceMonthDateFormat + " end as referenceMonth, "
				+ " case when min(r.collectionDate) is null then '' else " + minCollectionDateFormat + " end as newRecruitmentDate, "
				+ " user.userId as personInChargeId, " // 2020-06-23: filter new recruitment list by field officer (PIR-231)
				+ " user.englishName as personInCharge, "
				+ " case when r.isNewOutlet = true then 'Y' else 'N' end as newFirm "
                + " from Assignment as a "
                + " left join a.quotationRecords as r"
                + " left join r.outlet as o "
                + " left join r.quotation as q "
                + " left join a.surveyMonth as surveyMonth "
                + " inner join r.user as user "
                + " left join q.unit as u "
                + " left join u.subItem as si "
                + " left join si.outletType as ot "
//                + " left join o.outletTypes as ot "
                + " inner join o.tpu as tpu "
                + " inner join tpu.district as d "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.supervisor.id in (:userIds) ";
		}
		
		if (!StringUtils.isBlank(outletTypeId)) {
			hql += " and substring(ot.code, len(ot.code)-2, 3) = :outletTypeId ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " str(o.firmCode) like :search or "
					+ " o.name like :search or "
					+ " user.englishName like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " o.detailAddress like :search "
	                + " ) ";
		}
		
		hql += " group by a.assignmentId, o.outletId, "
				+ " o.firmCode, o.name, "
				+ " user.userId, user.englishName, d.code, tpu.code, "
				+ " o.detailAddress, "
				+ " r.isNewOutlet, "
				+ " surveyMonth.referenceMonth ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (!StringUtils.isBlank(outletTypeId)) {
			query.setParameter("outletTypeId", outletTypeId);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (displayLength > 0) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLength);
		}

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.newRecruitmentApproval.TableList.class));

		return query.list();
	}

	public long countNewRecruitmentApprovalTableList(String search,
			List<Integer> userIds,
			String outletTypeId) {
		
		String sql = " select count(*) from ( "
				 + " select count(distinct a.assignmentId) as cnt"
				 + " from QuotationRecord as r "
                + " left join Outlet as o on o.outletId = r.outletId "
                + " left join Quotation as q on q.quotationId = r.quotationId "
                + " left join Assignment as a on a.assignmentId = r.assignmentId "
                + " left join SurveyMonth as surveyMonth on surveyMonth.surveyMonthId = a.surveyMonthId "
                + " inner join [User] as u on r.userId = u.userId "
                + " left join Unit as ut on ut.unitId = q.unitId "
                + " left join SubItem as si on si.subItemId = ut.subItemId "
                + " left join OutletType as ot on ot.outletTypeId = si.outletTypeId "
//                + " left join OutletTypeOutlet as ot on ot.outletId = o.outletId "
                + " inner join TPU as tpu on tpu.tpuId = o.tpuId "
                + " inner join District as d on d.districtId = tpu.districtId ";
		
		sql += " and r.isNewRecruitment = 1 and r.status = 'Submitted' ";

		sql += " and r.isBackNo = 0 and r.isBackTrack = 0 ";
		
		if (userIds != null && userIds.size() > 0) {
			sql += " and u.supervisorId in (:userIds) ";
		}
		
		if (!StringUtils.isBlank(outletTypeId)) {
			sql += " and substring(ot.code, len(ot.code)-2, 3) = :outletTypeId ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
					+ " o.firmCode like :search or "
					+ " o.name like :search or "
					+ " u.englishName like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " o.detailAddress like :search "
	                + " ) ";
		}
		
		sql += " group by a.assignmentId, o.outletId, "
				+ " o.firmCode, o.name, "
				+ " u.englishName, d.code, tpu.code, "
				+ " o.detailAddress, "
				+ " r.isNewOutlet, "
				+ " surveyMonth.referenceMonth ";
		
		sql += " ) as d ";

//		String hql = "select count(distinct a) "
//                + " from Outlet as o "
//                + " left join o.quotations as q "
//                + " left join q.quotationRecords as r "
//                + " left join r.assignment as a "
//                + " left join a.surveyMonth as surveyMonth "
//                + " inner join r.user as user "
//                + " left join o.outletTypes as ot "
//                + " inner join o.tpu as tpu "
//                + " inner join tpu.district as d "
//                + " where 1 = 1 ";
//		
//		hql += " and q.status = 'Active' ";
//		
//		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";
//
//		hql += " and r.isBackNo = false and r.isBackTrack = false ";
//		
//		if (userIds != null && userIds.size() > 0) {
//			hql += " and user.id in (:userIds) ";
//		}
//		
//		if (!StringUtils.isBlank(outletTypeId)) {
//			hql += " and ot.shortCode = :outletTypeId ";
//		}
//		
//		if (StringUtils.isNotEmpty(search)) {
//			hql += " and ( "
//					+ " str(o.firmCode) like :search or "
//					+ " o.name like :search or "
//					+ " user.englishName like :search or "
//					+ " d.code like :search or "
//					+ " tpu.code like :search or "
//					+ " o.detailAddress like :search "
//	                + " ) ";
//		}

		Query query = getSession().createSQLQuery(sql);
//		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (!StringUtils.isBlank(outletTypeId)) {
			query.setParameter("outletTypeId", outletTypeId);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}	

	public List<Assignment> getUnGeneratedPEAssignment(){
		
		String hql = "select a "
				+ " from Assignment as a "
				+ " where a.assignmentId in ( "
				+ "   select a.assignmentId "
				+ " 	from Assignment as a "
				+ " 	inner join a.peCheckTask as task "
				+ " 	left join a.peCheckForm as form "
				+ " 	left join a.quotationRecords as total on total.isBackNo = false"
				+ " 	left join a.quotationRecords as approved on approved.status = 'Approved' and approved.isBackNo = false"
				+ " 	where form.peCheckFormId is null and task.isSelected = true "
				+ " 	group by a.assignmentId  "
				+ " 	having count(distinct total.quotationRecordId) = count(distinct approved.quotationRecordId)"
				+ ") ";
		
		Query query = this.getSession().createQuery(hql);
		return query.list();

	}

	public List<String> getValidReferenceCode(List<String> testCode){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("referenceNo"));
		criteria.add(Restrictions.in("referenceNo", testCode));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getAssignmentIdWithoutQuotationRecord(Set<Integer> assignmentIds) {
//		return this.createCriteria("a")
//				.createAlias("a.quotationRecords", "q", JoinType.LEFT_OUTER_JOIN)
//				.setProjection(SQLProjectionExt.groupByHaving("{alias}.assignmentId", StandardBasicTypes.INTEGER, "count({q}.quotationRecordId) = 0"))
//				.add(Restrictions.in("a.id", assignmentIds))
//				.list();
		
		String sql = "Select a.AssignmentId as assignmentId"
				+ "	from Assignment as a"
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId"
				+ " where a.AssignmentId in ( :assignmentIds )"
				+ " group by a.AssignmentId"
				+ " having count(qr.quotationRecordId) = 0";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameterList("assignmentIds", assignmentIds);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		
		return query.list();
	}
	
	public DashboardStatistics getDashboardStatistics(int selectedUserAuthorityLevel, int loginUserId, Integer selectedUserId) {
		String sql = " exec dbo.GetDashboardStatistics :selectedUserAuthorityLevel, :loginUserId, :selectedUserId ";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("selectedUserAuthorityLevel", selectedUserAuthorityLevel);
		query.setParameter("loginUserId", loginUserId);
		query.setParameter("selectedUserId", selectedUserId);
		query.addScalar("fieldOfficerAssignmentCount", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerAssignmentTotal", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerAssignmentVerification", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerAssignmentRevisit", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerQuotationCount", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerQuotationTotal", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerQuotationVerification", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerQuotationRevisit", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerRUA", StandardBasicTypes.LONG);
		query.addScalar("fieldOfficerOutstandingNewRecruitment", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorTeamAssignmentCount", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorTeamAssignmentTotal", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorTeamAssignmentVerification", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorTeamAssignmentRevisit", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorTeamQuotationCount", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorTeamQuotationTotal", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorTeamQuotationVerification", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorTeamQuotationRevisit", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorItineraryPlan", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorSubmittedAssignment", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorItineraryCheck", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorRUA", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorNewRecruitment", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorSpotCheck", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorSupervisoryCheck", StandardBasicTypes.LONG);
		query.addScalar("fieldSupervisorPECheck", StandardBasicTypes.LONG);
		
		query.addScalar("fieldTeamHeadTeamAssignmentCount", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadTeamAssignmentTotal", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadTeamAssignmentVerification", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadTeamAssignmentRevisit", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadTeamQuotationCount", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadTeamQuotationTotal", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadTeamQuotationVerification", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadTeamQuotationRevisit", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadItineraryPlan", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadSubmittedAssignment", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadItineraryCheck", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadRUA", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadNewRecruitment", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadSpotCheck", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadSupervisoryCheck", StandardBasicTypes.LONG);
		query.addScalar("fieldTeamHeadPECheck", StandardBasicTypes.LONG);
		
		query.addScalar("indoorDataConversionCurrentMonthIndividualCount", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionCurrentMonthIndividualTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionCurrentMonthIndividualMRPSCount", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionCurrentMonthIndividualMRPSTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionCurrentMonthIndividualOthersCount", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionCurrentMonthIndividualOthersTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionCurrentMonthIndividualVerification", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionPreviousMonthIndividualCount", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionPreviousMonthIndividualTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionPreviousMonthIndividualMRPSCount", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionPreviousMonthIndividualMRPSTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionPreviousMonthIndividualOthersCount", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionPreviousMonthIndividualOthersTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorDataConversionPreviousMonthIndividualVerification", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthMRPSCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthMRPSTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthOthersCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthOthersTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthConversionCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthConversionTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthConversionMRPSCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthConversionMRPSTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthConversionOthersCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthConversionOthersTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthAllocationCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthAllocationTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthAllocationMRPSCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthAllocationMRPSTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthAllocationOthersCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthAllocationOthersTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorCurrentMonthVerification", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthMRPSCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthMRPSTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthOthersCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthOthersTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthConversionCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthConversionTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthConversionMRPSCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthConversionMRPSTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthConversionOthersCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthConversionOthersTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthAllocationCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthAllocationTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthAllocationMRPSCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthAllocationMRPSTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthAllocationOthersCount", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthAllocationOthersTotal", StandardBasicTypes.LONG);
		query.addScalar("indoorAllocatorSupervisorPreviousMonthVerification", StandardBasicTypes.LONG);
		query.setResultTransformer(Transformers.aliasToBean(DashboardStatistics.class));
		return (DashboardStatistics)query.uniqueResult();
	}

	public List<DeadlineRowModel> getDashboardFieldOfficerAssignments(int userId) {
		List<Integer> userIds = new ArrayList<Integer>();
		userIds.add(userId);
		return getDashboardFieldOfficerAssignments(userIds);
	}
	
	@SuppressWarnings("unchecked")
	public List<DeadlineRowModel> getDashboardFieldOfficerAssignments(List<Integer> userIds) {
//		String sql = "exec dbo.GetAssignmentProgress :userIds, :dateFormat";
//		
//		StringBuilder builder = new StringBuilder();
//		
//		builder.append("<query>");
//		for (Integer userId : userIds){
//			builder.append("<userid>"+userId+"</userid>");
//		}
//		builder.append("</query>");
		
		String sql = "select "
				+ "	case when a.AssignedCollectionDate is null "
				+ " then FORMAT(a.EndDate, 'dd-MM-yyyy', 'en-us') else FORMAT(a.AssignedCollectionDate, 'dd-MM-yyyy', 'en-us') end as [date], "
				+ " count (distinct a.AssignmentId) as total, "
				+ " count (distinct completed.AssignmentId) as [count] "
				+ " from Assignment as a "
				+ " inner join QuotationRecord as qr "
				+ " on a.AssignmentId = qr.AssignmentId "
				+ " left join (	select ca.AssignmentId"
				+ "		from Assignment as ca"
				+ "		inner join QuotationRecord as qr"
				+ "		on qr.AssignmentId = ca.AssignmentId"
				+ "		left join QuotationRecord as qr2"
				+ "		on qr2.AssignmentId = ca.AssignmentId and qr2.Status in ('Approved', 'Submitted') and qr2.IsBackNo = 0 "
				+ "		and qr2.QuotationRecordId = qr.QuotationRecordId  and qr2.QuotationState <> 'IP' and qr2.Availability <> 2  "
				+ "		where  ca.UserId in (:userIds) and qr.IsBackNo = 0 "
				+ "		group by ca.AssignmentId"
				+ "		having count(distinct qr.QuotationRecordId) = count (distinct qr2.QuotationRecordId)"
				+ "	) as completed"
				+ "		on a.AssignmentId = completed.AssignmentId"
				+ "	where a.UserId in (:userIds) and (a.AssignedCollectionDate is not null or a.EndDate is not null) "
//				+ " and qr.IsBackNo = 0"
				+ "	group by "
				+ "	case when a.AssignedCollectionDate is null "
				+ "	then FORMAT(a.EndDate, 'dd-MM-yyyy', 'en-us') else FORMAT(a.AssignedCollectionDate, 'dd-MM-yyyy', 'en-us') end"
				+ "	having count(distinct a.AssignmentId) <> count(distinct completed.AssignmentId) or count(completed.AssignmentId) = 0"
				+ "	order by  convert(date, case when a.AssignedCollectionDate is null "
				+ "	then FORMAT(a.EndDate, 'dd-MM-yyyy', 'en-us') else FORMAT(a.AssignedCollectionDate, 'dd-MM-yyyy', 'en-us') end, 105) asc";
		
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameterList("userIds", userIds);
		//query.setParameter("dateFormat", SystemConstant.DATE_FORMAT);
		
		query.addScalar("date", StandardBasicTypes.STRING);
		query.addScalar("count", StandardBasicTypes.LONG);
		query.addScalar("total", StandardBasicTypes.LONG);
		
		query.setResultTransformer(Transformers.aliasToBean(DeadlineRowModel.class));
		
		return query.list();
		
//		String dateFormat = String.format("case when a.assignedCollectionDate is null then FORMAT(a.collectionDate, '%1$s', 'en-us') when a.assignedCollectionDate is not null then FORMAT(a.assignedCollectionDate, '%1$s', 'en-us') else '' end", SystemConstant.DATE_FORMAT);
//		String sql = " select " + dateFormat + " as date, "
//				+ " count(distinct a) as total, "
//				+ " count(distinct a2) as count "
//				+ " from Assignment as a "
//				+ " inner join a.quotationRecords as qr1 "
//				+ " inner join qr1.user as user "
//				+ " left join a.quotationRecords as qr2 on qr2.status in ('Submitted', 'Approved') and qr1=qr2 "
//				+ " left join qr2.assignment as a2 on a = a2 "
//				//+ " left join a.quotationRecords as qr3 on qr3.status not in ('Submitted', 'Approved') and qr1=qr3 "
//				+ " where user.userId in :userIds "
//				+ " group by a.assignedCollectionDate, a.collectionDate "
//				+ " having count(qr1) != count(qr2) "
//				+ " order by case when a.assignedCollectionDate is null then a.collectionDate when a.assignedCollectionDate is not null then a.assignedCollectionDate else null end desc ";
//		
//		Query query = this.getSession().createQuery(sql);
//		query.setParameterList("userIds", userIds);
//		query.setResultTransformer(Transformers.aliasToBean(DeadlineRowModel.class));
//		return query.list();
	}
	
	public Date getEnumerationDate(Integer assignmentId) {
		String sql = " select min(qr.collectionDate) "
				+ " from QuotationRecord as qr "
				+ " inner join qr.assignment as a "
				+ " where a.assignmentId = :assignmentId ";
		
		Query query = this.getSession().createQuery(sql);
		query.setParameter("assignmentId", assignmentId);
		
		return (Date)query.uniqueResult();
	}
	

	public List<Assignment> getAssignmentByUserRefMonth(User user, Date referenceMonth){
		Criteria criteria = this.createCriteria("a")
				.createAlias("a.user", "u", JoinType.LEFT_OUTER_JOIN)
				.createAlias("a.surveyMonth", "s", JoinType.LEFT_OUTER_JOIN)
				.setFetchMode("a.peCheckTask", FetchMode.JOIN);
		
		criteria.add(Restrictions.and(
				Restrictions.eq("a.user", user), Restrictions.eq("s.referenceMonth", referenceMonth)));
		
		
		
		return criteria.list();
	}
	
	public List<Assignment> getImportedAssignmentByReferenceNo(List<String> referenceNo){
		Criteria criteria = this.createCriteria("a");
		
		criteria.add(Restrictions.in("a.referenceNo", referenceNo));
		criteria.add(Restrictions.eq("a.isImportedAssignment", true));
		
		
		return criteria.list();
	}
	

	public List<ExperienceSummaryReport> getExperienceSummaryReport(Integer purposeId,List<String> surveys,List<String> teams,List<Integer> officerIds,
			Date startReferenceMonth,Date endReferenceMonth,Integer marketType,Integer cpiQuotationType) {
		
		String refMonth = String.format("FORMAT(sm.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		
		String sql = "Select case when sm.ReferenceMonth is null then '' "
				+ " else " + refMonth +" end as referenceMonth" + 
				"	, ur.Team as team" + 
				"	, r.Code as [rank]" + 
				"	, ur.StaffCode as staffCode" + 
				"	, ur.EnglishName as staffName" + 
				"	, u.CPIQoutationType as cpiQuotationType" + 
				"	, pp.Code as purpose" + 
				"	, d.Code as district" + 
				"	, count(distinct a.AssignmentId) as count" + 
				"	From QuotationRecord as qr " + 
				"	left join Assignment as a on qr.AssignmentId = a.AssignmentId" + 
				"	left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId" + 
				"	left join Quotation as q on q.QuotationId = qr.QuotationId" + 
				"	left join Unit as u on u.UnitId = q.UnitId" + 
				"	left join Purpose as pp on pp.PurposeId = u.PurposeId" + 
				"	left join [User] as ur on qr.UserId = ur.UserId" + 
				"	left join [Rank] as r on r.RankId = ur.RankId" + 
				"	left join Outlet as o on o.OutletId = qr.OutletId" + 
				"	left join Tpu as t on o.TpuId = t.TpuId" + 
				"	left join District as d on d.DistrictId = t.DistrictId" + 
				"	where  pp.Code not in ('BMWPS', 'GHS') and qr.IsBackNo = 0" +
				"	and sm.ReferenceMonth between :startMonth and :endMonth ";
		
		if(purposeId != null){
			sql+= " and pp.purposeId = :purposeId ";
		}
		
		if(surveys != null){
			sql+= " and pp.survey in (:surveys) ";
		}
		
		if(teams != null){
			sql+= " and ur.team in (:teams) ";
		}
		
		if(officerIds != null){
			sql+= " and ur.userId in (:officerIds) ";
		}
		
		if(marketType != null){
			if (marketType == 1) {
				sql += " and u.cpiQoutationType = 1 ";
			} else {
				sql += " and u.cpiQoutationType <> 1 ";
			}
		}
		
		if(cpiQuotationType != null){
			sql+= " and u.cpiQoutationType = :cpiQuotationType ";
		}

		sql+= "	group by sm.ReferenceMonth, ur.Team, r.Code, ur.StaffCode, ur.EnglishName, u.CPIQoutationType, pp.Code, d.Code";
		
		sql+= " order by sm.ReferenceMonth, ur.Team, r.Code, ur.StaffCode, u.CPIQoutationType, pp.Code";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startReferenceMonth);
		query.setParameter("endMonth", endReferenceMonth);
		if(purposeId != null){
			query.setParameter("purposeId", purposeId);
		}
		if(surveys != null){
			query.setParameterList("surveys", surveys);
		}
		if(teams != null){
			query.setParameterList("teams", teams);
		}
		if(officerIds != null){
			query.setParameterList("officerIds", officerIds);
		}
//		if(marketType != null){
//			query.setParameter("marketType", marketType);
//		}
		if(cpiQuotationType != null){
			query.setParameter("cpiQuotationType", cpiQuotationType);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.STRING);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("rank",StandardBasicTypes.STRING);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("staffName",StandardBasicTypes.STRING);
		query.addScalar("cpiQuotationType",StandardBasicTypes.INTEGER);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("district",StandardBasicTypes.STRING);
		query.addScalar("count",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(ExperienceSummaryReport.class));
		return query.list();
	}
	
	public List<ExperienceSummaryReport> getExperienceSummaryReportQuotation(Integer purposeId,List<String> surveys,List<String> teams,List<Integer> officerIds,
			Date startReferenceMonth,Date endReferenceMonth,Integer marketType,Integer cpiQuotationType) {
		
		String refMonth = String.format("FORMAT(sm.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
				
		String sql = "Select case when sm.ReferenceMonth is null then '' "
				+ " else " + refMonth +" end as referenceMonth" + 
				"	, ur.Team as team" + 
				"	, r.Code as [rank]" + 
				"	, ur.StaffCode as staffCode" + 
				"	, ur.EnglishName as staffName" + 
				"	, u.CPIQoutationType as cpiQuotationType" + 
				"	, pp.Code as purpose" + 
				"	, d.Code as district" + 
				"	, count(distinct qr.QuotationRecordId) as count" + 
				"	From QuotationRecord as qr " + 
				"	left join Assignment as a on qr.AssignmentId = a.AssignmentId" + 
				"	left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId" + 
				"	left join Quotation as q on q.QuotationId = qr.QuotationId" + 
				"	left join Unit as u on u.UnitId = q.UnitId" + 
				"	left join Purpose as pp on pp.PurposeId = u.PurposeId" + 
				"	left join [User] as ur on qr.UserId = ur.UserId" + 
				"	left join [Rank] as r on r.RankId = ur.RankId" + 
				"	left join Outlet as o on o.OutletId = qr.OutletId" + 
				"	left join Tpu as t on o.TpuId = t.TpuId" + 
				"	left join District as d on d.DistrictId = t.DistrictId" + 
				"	where  pp.Code not in ('BMWPS', 'GHS') and qr.IsBackNo = 0" +
				"	and sm.ReferenceMonth between :startMonth and :endMonth ";
		
		if(purposeId != null){
			sql+= " and pp.purposeId = :purposeId ";
		}
		
		if(surveys != null){
			sql+= " and pp.survey in (:surveys) ";
		}
		
		if(teams != null){
			sql+= " and ur.team in (:teams) ";
		}
		
		if(officerIds != null){
			sql+= " and ur.userId in (:officerIds) ";
		}
		
		if(marketType != null){
			if (marketType == 1) {
				sql += " and u.cpiQoutationType = 1 ";
			} else {
				sql += " and u.cpiQoutationType <> 1 ";
			}
		}
		
		if(cpiQuotationType != null){
			sql+= " and u.cpiQoutationType = :cpiQuotationType ";
		}

		sql+= "	group by sm.ReferenceMonth, ur.Team, r.Code, ur.StaffCode, ur.EnglishName, u.CPIQoutationType, pp.Code, d.Code";
		
		sql+= " order by sm.ReferenceMonth, ur.Team, r.Code, ur.StaffCode, u.CPIQoutationType, pp.Code";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startReferenceMonth);
		query.setParameter("endMonth", endReferenceMonth);
		if(purposeId != null){
			query.setParameter("purposeId", purposeId);
		}
		if(surveys != null){
			query.setParameterList("surveys", surveys);
		}
		if(teams != null){
			query.setParameterList("teams", teams);
		}
		if(officerIds != null){
			query.setParameterList("officerIds", officerIds);
		}
//		if(marketType != null){
//			query.setParameter("marketType", marketType);
//		}
		if(cpiQuotationType != null){
			query.setParameter("cpiQuotationType", cpiQuotationType);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.STRING);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("rank",StandardBasicTypes.STRING);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("staffName",StandardBasicTypes.STRING);
		query.addScalar("cpiQuotationType",StandardBasicTypes.INTEGER);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("district",StandardBasicTypes.STRING);
		query.addScalar("count",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(ExperienceSummaryReport.class));
		return query.list();
	}
	
	public List<ReportAssignmentLookupList> getAssignmentLookupTableList(String search, int firstRecord, int displayLength, Order order, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, Integer[] oldAssignmentIds, Date month) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select a.assignmentId as id "
				+ ", case "
				+ "	when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end  as collectionDate "
				+ ", case "
				+ "	when a.startDate is null then '' "
				+ " else " + startDate + " end  as startDate "
				+ ", case "
				+ "	when a.endDate is null then '' "
				+ " else " + endDate + " end  as endDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ " from Assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " where a.isImportedAssignment = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) hql += " and a.assignmentId not in (:oldAssignmentIds) ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or a.assignmentId like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		if (month != null){
			hql += " and sm.referenceMonth = :referenceMonth ";
		}
		
		hql += " and a.isCompleted = 0 ";
		
		hql += " and qr.quotationRecordId is not null ";
		
		hql += " group by "
			+ " a.assignmentId "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
				
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) query.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if (month != null){
			query.setParameter("referenceMonth", month);
		}
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(ReportAssignmentLookupList.class));
		
		return query.list();
	}

	public long countAssignmentLookupTableList(String search, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, Integer[] oldAssignmentIds, Date month) {
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select "
				+ " count(distinct a.assignmentId) as noOfAssignment "
				+ " from Assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " where a.isImportedAssignment = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) hql += " and a.assignmentId not in (:oldAssignmentIds) ";
		

		if (month != null){
			hql += " and sm.referenceMonth = :referenceMonth ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or a.assignmentId like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		hql += " and a.isCompleted = 0 ";
		
		hql += " and qr.quotationRecordId is not null ";
		
//		hql += " group by "
//			+ " a.assignmentId "
//			+ ", a.collectionDate, a.startDate, a.endDate "
//			+ ", o.name, d.chineseName, t.code ";
//			
		
		Query query = this.getSession().createQuery(hql);
		
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		

		if (month != null){
			query.setParameter("referenceMonth", month);
		}
		
				
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) query.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		//query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationLookupTableList.class));
		
		
		return (long)query.uniqueResult();
	}
	
	
	public List<Integer> getAssignmentLookupSelectAll(String search, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId) {
		
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select a.assignmentId as id "
				+ " from Assignment as a "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " where a.isImportedAssignment = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search  "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		hql += " and a.isCompleted = 0 ";
		
		hql += " group by "
			+ " a.assignmentId "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		Query query = this.getSession().createQuery(hql);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
						
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
				
		return query.list();
	}
	public List<Integer> getAssignmentLookupSelectAll(String search, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId,Date referenceMonth) {
		
		
		
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select a.assignmentId as id "
				+ " from Assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " where a.isImportedAssignment = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";

		if (referenceMonth != null){
			hql += " and sm.referenceMonth = :referenceMonth ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search  "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		hql += " and a.isCompleted = 0 ";
		
		
		hql += " group by "
			+ " a.assignmentId "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		Query query = this.getSession().createQuery(hql);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (referenceMonth != null){
			query.setParameter("referenceMonth", referenceMonth);
		}
						
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
				
		return query.list();
	}
	
	public List<Integer> queryAssignmentSelect2(String search, int firstRecord, int displayLength, Date referenceMonth){
		String hql = " select distinct a.assignmentId as id "				
				+ " from Assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " where a.isImportedAssignment = false " ;
		
		if (referenceMonth != null){
			hql += " and sm.referenceMonth = :referenceMonth ";
		}
				
		
		if (!StringUtils.isEmpty(search)){
			hql += " and str(a.assignmentId) like :search ";
		}	
				
		hql += " order by a.assignmentId ";
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if (referenceMonth != null){
			query.setParameter("referenceMonth", referenceMonth);
		}
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		return query.list();
	}
	
	
	public long countAssignmentSelect2(String search, Date referenceMonth){
		String hql = " select count(distinct a.assignmentId) as id "				
				+ " from Assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " where a.isImportedAssignment = false " ;
		if (referenceMonth != null){
			hql += " and sm.referenceMonth = :referenceMonth ";
		}				
		
		if (!StringUtils.isEmpty(search)){
			hql += " and str(a.assignmentId) like :search ";
		}	
				
		//hql += " order by a.assignmentId ";
		Query query = this.getSession().createQuery(hql);
		if (referenceMonth != null){
			query.setParameter("referenceMonth", referenceMonth);
		}
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		return (long)query.uniqueResult();
	}

	public List<FieldworkOutputByOutletTypeReport> getFieldworkOutputByOutletType(
			List<String> teams, List<Integer> officerIds,
			List<Integer> purposeId, List<String> outletTypeShortCode,
			Date startReferenceMonth, Date endReferenceMonth, List<Integer> allocationBatchId, List<Integer> subOrdinates) {
		
			String sql = 	
				"select"
				+"	cast(u.team as varchar) as team,"
				+"	cast(r.name as varchar) as rank, "
				+"	cast(concat(r.name, ': ', u.englishName, ' (', u.staffCode, ')') as varchar) as displayName, "
				+"	cast(ot.ShortCode as varchar) as outletTypeShortCode,"
				+"	cast(ot.EnglishName as varchar) as outletTypeEnglishName,"
				+"	cast(count(distinct a2.assignmentId) as int) as assignmentCount, "
				+"	cast(count(distinct qr.quotationRecordId) as int) as quotationRecordCount "
				+"from Assignment a"
				+"	CROSS join [user] u"
				+"	left join Assignment a2 on a.AssignmentId = a2.AssignmentId and a2.UserId = u.UserId"
				+"	left join SurveyMonth sm on sm.SurveyMonthId = a.SurveyMonthId"
				+"	left join AllocationBatch ab on ab.SurveyMonthId = a.SurveyMonthId"
				+"	left join Outlet o on o.OutletId = a.OutletId"
				+"	left join OutletTypeOutlet oto on oto.OutletId = o.OutletId"
				+"	left join vwOutletTypeShortForm ot on ot.ShortCode = oto.ShortCode"
				+"	left join QuotationRecord qr on qr.AssignmentId = a.AssignmentId  and a.UserId = u.UserId"
				+"	left join Quotation q on q.QuotationId = qr.QuotationId"
				+"	left join Unit un on un.UnitId = q.UnitId"
				+"	left join Purpose p on p.PurposeId = un.PurposeId"
				+"	left join rank r on r.RankId = u.RankId "
				+"where "
				+"	sm.ReferenceMonth >= :startReferenceMonth and sm.ReferenceMonth <= :endReferenceMonth ";
		
		if(purposeId != null){
			sql = sql + " and p.purposeId in (:purposeId) ";
		}
		
		if(teams != null){
			sql = sql + " and u.team in (:teams) ";
		}
		
		if(officerIds != null){
			sql = sql + " and u.userId in (:officerIds) ";
		}
		
		if(subOrdinates != null){
			sql = sql + " and u.userId in (:subOrdinates) ";
		}
		
		if(outletTypeShortCode != null){
			sql = sql + " and ot.shortCode in (:shortCode) ";
		}
		
		if(allocationBatchId != null){
			sql = sql + " and ab.allocationBatchId in (:allocationBatchId) ";
		}

		sql = sql + "	group by "+
				"		u.userId,"+
				"		ot.shortCode,"+
				"		ot.englishName,"+
				"		u.team,"+
				"		r.name,"+
				"		u.englishName,"+
				"		u.staffCode";
		
		Query query = this.getSession().createSQLQuery(sql);
		
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
		if(outletTypeShortCode != null){
			query.setParameterList("shortCode", outletTypeShortCode);
		}
		if(allocationBatchId != null){
			query.setParameterList("allocationBatchId", allocationBatchId);
		}
		query.setResultTransformer(Transformers.aliasToBean(FieldworkOutputByOutletTypeReport.class));
		return query.list();
	}
	
	public List<AssignmentAllocationSummaryReport> getAssignmentAllocationSummary(List<Integer> officerIds, List<String> teams
			, List<Integer> purposeIds, List<Integer> collectionMethod, List<String> allocationBatch, Date fromMonth, Date toMonth){
		String sql = "exec [dbo].[GetAssignmentAllocationSummary] :officer , :team , :purpose , :collectionMode , :allocationBatch , :fromMonth , :toMonth";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if(officerIds!=null && officerIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : officerIds){
				builder.append("<userId>"+userId+"</userId>");
			}
			builder.append("</query>");
			
			query.setParameter("officer", builder.toString());
		} else {
			query.setParameter("officer", null);
		}
		
		if(teams!=null && teams.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String team : teams){
				builder.append("<team>"+team+"</team>");
			}
			builder.append("</query>");
			
			query.setParameter("team", builder.toString());
		} else {
			query.setParameter("team", null);
		}
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer purposeId : purposeIds){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("purpose", builder.toString());
		} else {
			query.setParameter("purpose", null);
		}
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer collection : collectionMethod){
				builder.append("<collectionMode>"+collection+"</collectionMode>");
			}
			builder.append("</query>");
			
			query.setParameter("collectionMode", builder.toString());
		} else {
			query.setParameter("collectionMode", null);
		}
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String allocation : allocationBatch){
				builder.append("<batchName>"+allocation+"</batchName>");
			}
			builder.append("</query>");
			
			query.setParameter("allocationBatch", builder.toString());
		} else {
			query.setParameter("allocationBatch", null);
		}
		
		query.setParameter("fromMonth", fromMonth);
		query.setParameter("toMonth", toMonth);
		
		query.addScalar("referenceMonth", StandardBasicTypes.DATE);
		query.addScalar("firmCode", StandardBasicTypes.INTEGER);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("numOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("firmName", StandardBasicTypes.STRING);
		query.addScalar("inFrom", StandardBasicTypes.STRING);
		query.addScalar("inFromName", StandardBasicTypes.STRING);
		query.addScalar("outTo", StandardBasicTypes.STRING);
		query.addScalar("outToName", StandardBasicTypes.STRING);
		query.addScalar("recommendedBy", StandardBasicTypes.STRING);
		query.addScalar("recommendedByName", StandardBasicTypes.STRING);
		query.addScalar("recommendedDate", StandardBasicTypes.DATE);
		query.addScalar("approvedBy", StandardBasicTypes.STRING);
		query.addScalar("approvedByName", StandardBasicTypes.STRING);
		query.addScalar("approvedDate", StandardBasicTypes.DATE);
		query.addScalar("allocationBatch", StandardBasicTypes.STRING);
		query.addScalar("collectionMethod", StandardBasicTypes.INTEGER);
		query.addScalar("stage", StandardBasicTypes.STRING);
		query.addScalar("outletId", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentAllocationSummaryReport.class));
		return query.list();
	}
	public List<FieldworkOutputByDistrictReport> getFieldworkOutputStatistic(String[] team, Integer[] userIds,  Integer[] districtIds, Date refMonth, Integer[] allocationBatchIds, Integer[] purposeIds, List<Integer> subordinateIds) {
		
		String sql = " select u.Team as team, r.Code as rank, u.EnglishName + ' (' + u.StaffCode + ')' as officerDetail , d.EnglishName as district, count(distinct a.AssignmentId) as countOfAssignment, count(distinct qr.QuotationRecordId) as countOfQuotation "
				+ " from [User] as u cross join [District] as d "
				+ " inner join [Rank] as r on r.RankId = u.RankId "
				+ " inner join [Tpu] as t on t.DistrictId = d.DistrictId "
				+ " inner join [Outlet] as o on o.TpuId = t.TpuId "
				+ " inner join [Assignment] as a on a.OutletId = o.OutletId and a.UserId = u.UserId "
				+ " left join [QuotationRecord] as qr on qr.AssignmentId = a.AssignmentId and qr.UserId = u.UserId "
				+ " inner join [Quotation] as q on q.QuotationId = qr.QuotationId "
				+ " inner join [Unit] as ut on ut.UnitId = q.UnitId "
				+ " inner join [Purpose] as p on p.PurposeId = ut.PurposeId "
				+ " inner join [Batch] as b on b.BatchId = q.BatchId "
				+ " inner join [AssignmentAttribute] as aatt on aatt.BatchId = b.BatchId "
				+ " inner join [AllocationBatch] as ab on ab.AllocationBatchId = aatt.AllocationBatchId "
				+ " inner join [SurveyMonth] as sm on sm.SurveyMonthId = ab.SurveyMonthId "
				+ " where 1 = 1 ";
		
		if (team != null && team.length > 0) {
			sql += " and u.Team IN (:team) ";
		}
				
		if (userIds != null && userIds.length > 0) {
			sql += " and u.UserId IN (:userIds) ";
		}
		
		if (subordinateIds != null && subordinateIds.size() > 0) {
			sql += " and u.UserId IN (:subordinateIds) ";
		}
		
		if (districtIds != null && districtIds.length > 0) {
			sql += " and d.DistrictId IN (:districtIds) ";
		}
		
		if (refMonth != null) {
			sql += " and sm.ReferenceMonth = :refMonth ";
		}
		
		if (allocationBatchIds != null && allocationBatchIds.length > 0) {
			sql += " and ab.AllocationBatchId IN (:allocationBatchIds) ";
		}
		
		if (purposeIds != null && purposeIds.length > 0) {
			sql += " and p.PurposeId IN (:purposeIds) ";
		}
		
		sql += " group by u.Team, r.Code, u.UserId, u.StaffCode, u.EnglishName, d.DistrictId, d.EnglishName ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if (team != null && team.length > 0) {
			query.setParameterList("team", team);
		}
				
		if (userIds != null && userIds.length > 0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (subordinateIds != null && subordinateIds.size() > 0) {
			query.setParameterList("subordinateIds", subordinateIds);
		}
		
		if (districtIds != null && districtIds.length > 0) {
			query.setParameterList("districtIds", districtIds);
		}
		
		if (refMonth != null) {
			query.setDate("refMonth", refMonth);
		}
		
		if (allocationBatchIds != null && allocationBatchIds.length > 0) {
			query.setParameterList("allocationBatchIds", allocationBatchIds);
		}
		
		if (purposeIds != null && purposeIds.length > 0) {
			query.setParameterList("purposeIds", purposeIds);
		}
		
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("rank", StandardBasicTypes.STRING);
		query.addScalar("officerDetail", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("countOfAssignment", StandardBasicTypes.INTEGER);
		query.addScalar("countOfQuotation", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(FieldworkOutputByDistrictReport.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AllocationTransferInTransferOutReallocationRecordsReport> getAssignmentReallocationRecords(List<Integer> officerIds, List<String> teams
			, List<Integer> purposeIds, List<Integer> collectionMethod, List<String> allocationBatch, Date fromMonth, Date toMonth, int reallocationId){
		String sql = "exec [dbo].[getAssignmentReallocationRecords] :officer , :team , :purpose , :collectionMode , :allocationBatch , :fromMonth , :toMonth, :reallocationId";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if(officerIds!=null && officerIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : officerIds){
				builder.append("<userId>"+userId+"</userId>");
			}
			builder.append("</query>");
			
			query.setParameter("officer", builder.toString());
		} else {
			query.setParameter("officer", null);
		}
		
		if(teams!=null && teams.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String team : teams){
				builder.append("<team>"+team+"</team>");
			}
			builder.append("</query>");
			
			query.setParameter("team", builder.toString());
		} else {
			query.setParameter("team", null);
		}
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer purposeId : purposeIds){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("purpose", builder.toString());
		} else {
			query.setParameter("purpose", null);
		}
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer collection : collectionMethod){
				builder.append("<collectionMode>"+collection+"</collectionMode>");
			}
			builder.append("</query>");
			
			query.setParameter("collectionMode", builder.toString());
		} else {
			query.setParameter("collectionMode", null);
		}
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String allocation : allocationBatch){
				builder.append("<batchName>"+allocation+"</batchName>");
			}
			builder.append("</query>");
			
			query.setParameter("allocationBatch", builder.toString());
		} else {
			query.setParameter("allocationBatch", null);
		}
		
		query.setParameter("fromMonth", fromMonth);
		query.setParameter("toMonth", toMonth);
		
		query.setParameter("reallocationId", reallocationId);
		
		query.addScalar("firmCode", StandardBasicTypes.INTEGER);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("numOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("firmName", StandardBasicTypes.STRING);
		query.addScalar("inFrom", StandardBasicTypes.STRING);
		query.addScalar("outTo", StandardBasicTypes.STRING);
		query.addScalar("recommendedBy", StandardBasicTypes.STRING);
		query.addScalar("recommendedDate", StandardBasicTypes.DATE);
		query.addScalar("approvedBy", StandardBasicTypes.STRING);
		query.addScalar("approvedDate", StandardBasicTypes.DATE);
		query.addScalar("allocationBatch", StandardBasicTypes.STRING);
		query.addScalar("stage", StandardBasicTypes.STRING);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(AllocationTransferInTransferOutReallocationRecordsReport.class));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<AllocationTransferInTransferOutReallocationRecordsReport> getAssignmentAdjustmentRecords(List<Integer> officerIds, List<String> teams
			, List<Integer> purposeIds, List<Integer> collectionMethod, List<String> allocationBatch, Date fromMonth, Date toMonth, int adjustmentId){
		String sql = "exec [dbo].[GetAssignmentAdjustmentRecords] :officer , :team , :purpose , :collectionMode , :allocationBatch , :fromMonth , :toMonth, :adjustmentId";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if(officerIds!=null && officerIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : officerIds){
				builder.append("<userId>"+userId+"</userId>");
			}
			builder.append("</query>");
			
			query.setParameter("officer", builder.toString());
		} else {
			query.setParameter("officer", null);
		}
		
		if(teams!=null && teams.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String team : teams){
				builder.append("<team>"+team+"</team>");
			}
			builder.append("</query>");
			
			query.setParameter("team", builder.toString());
		} else {
			query.setParameter("team", null);
		}
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer purposeId : purposeIds){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("purpose", builder.toString());
		} else {
			query.setParameter("purpose", null);
		}
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer collection : collectionMethod){
				builder.append("<collectionMode>"+collection+"</collectionMode>");
			}
			builder.append("</query>");
			
			query.setParameter("collectionMode", builder.toString());
		} else {
			query.setParameter("collectionMode", null);
		}
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String allocation : allocationBatch){
				builder.append("<batchName>"+allocation+"</batchName>");
			}
			builder.append("</query>");
			
			query.setParameter("allocationBatch", builder.toString());
		} else {
			query.setParameter("allocationBatch", null);
		}
		
		query.setParameter("fromMonth", fromMonth);
		query.setParameter("toMonth", toMonth);
		
		query.setParameter("adjustmentId", adjustmentId);
		
		query.addScalar("firmCode", StandardBasicTypes.INTEGER);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("numOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("firmName", StandardBasicTypes.STRING);
		query.addScalar("inFrom", StandardBasicTypes.STRING);
		query.addScalar("outTo", StandardBasicTypes.STRING);
		query.addScalar("recommendedBy", StandardBasicTypes.STRING);
		query.addScalar("recommendedDate", StandardBasicTypes.DATE);
		query.addScalar("approvedBy", StandardBasicTypes.STRING);
		query.addScalar("approvedDate", StandardBasicTypes.DATE);
		query.addScalar("allocationBatch", StandardBasicTypes.STRING);
		query.addScalar("stage", StandardBasicTypes.STRING);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(AllocationTransferInTransferOutReallocationRecordsReport.class));
		return query.list();
	}
	
	public List<SummaryOfProgressReport> getSummaryOfProgressReportByFieldOfficer(List<Integer> purposeIds, List<Integer> allocationBatchIds, List<Integer> userIds, Date refMonth ) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetSummaryOfProgressByFieldOfficerReport] :refMonth , :purposeId , :userId , :allocationBatchId");
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer purposeId : purposeIds){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("purposeId", builder.toString());
		} else {
			query.setParameter("purposeId", null);
		}
		
		if(allocationBatchIds!=null && allocationBatchIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer allocationBatchId : allocationBatchIds){
				builder.append("<allocationBatchId>"+allocationBatchId+"</allocationBatchId>");
			}
			builder.append("</query>");
			
			query.setParameter("allocationBatchId", builder.toString());
		} else {
			query.setParameter("allocationBatchId", null);
		}
		
		if(userIds!=null && userIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : userIds){
				builder.append("<userId>"+userId+"</userId>");
			}
			builder.append("</query>");
			
			query.setParameter("userId", builder.toString());
		} else {
			query.setParameter("userId", null);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("staffName",StandardBasicTypes.STRING);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("batchName",StandardBasicTypes.STRING);
		query.addScalar("rank",StandardBasicTypes.STRING);
		query.addScalar("totalAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("completedAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("totalQuotationRecord",StandardBasicTypes.INTEGER);
		query.addScalar("completedQuotationRecord",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfProgressReport.class));
		
		return query.list();
	}
	
	public List<SummaryOfProgressReport> getSummaryOfProgressReportByTeam(List<Integer> purposeIds, List<Integer> allocationBatchIds, List<Integer> userIds, Date refMonth ) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetSummaryOfProgressByFieldTeamReport] :refMonth , :purposeId , :userId , :allocationBatchId");
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer purposeId : purposeIds){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("purposeId", builder.toString());
		} else {
			query.setParameter("purposeId", null);
		}
		
		if(allocationBatchIds!=null && allocationBatchIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer allocationBatchId : allocationBatchIds){
				builder.append("<allocationBatchId>"+allocationBatchId+"</allocationBatchId>");
			}
			builder.append("</query>");
			
			query.setParameter("allocationBatchId", builder.toString());
		} else {
			query.setParameter("allocationBatchId", null);
		}
		
		if(userIds!=null && userIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : userIds){
				builder.append("<userId>"+userId+"</userId>");
			}
			builder.append("</query>");
			
			query.setParameter("userId", builder.toString());
		} else {
			query.setParameter("userId", null);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("batchName",StandardBasicTypes.STRING);
		query.addScalar("totalAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("completedAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("totalQuotationRecord",StandardBasicTypes.INTEGER);
		query.addScalar("completedQuotationRecord",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfProgressReport.class));
		
		return query.list();
	}
	

	public List<SummaryOfProgressReport> getSummaryOfProgressImportedReportByFieldOfficer(List<String> purposeIds, List<Integer> userIds, Date refMonth ) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetSummaryOfProgressImportByFieldOfficerReport] :refMonth , :purposeId , :userId ");
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String purposeId : purposeIds){
				builder.append("<survey>"+purposeId+"</survey>");
			}
			builder.append("</query>");
			
			query.setParameter("purposeId", builder.toString());
		} else {
			query.setParameter("purposeId", null);
		}
		
		if(userIds!=null && userIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : userIds){
				builder.append("<userId>"+userId+"</userId>");
			}
			builder.append("</query>");
			
			query.setParameter("userId", builder.toString());
		} else {
			query.setParameter("userId", null);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("staffName",StandardBasicTypes.STRING);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("rank",StandardBasicTypes.STRING);
		query.addScalar("totalAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("completedAssignment",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfProgressReport.class));
		
		return query.list();
	}
	
	public List<SummaryOfProgressReport> getSummaryOfProgressImportedReportByTeam(List<String> purposeIds, List<Integer> userIds, Date refMonth ) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetSummaryOfProgressImportByTeamReport] :refMonth , :purposeId , :userId ");
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String purposeId : purposeIds){
				builder.append("<survey>"+purposeId+"</survey>");
			}
			builder.append("</query>");
			
			query.setParameter("purposeId", builder.toString());
		} else {
			query.setParameter("purposeId", null);
		}
		
		if(userIds!=null && userIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : userIds){
				builder.append("<userId>"+userId+"</userId>");
			}
			builder.append("</query>");
			
			query.setParameter("userId", builder.toString());
		} else {
			query.setParameter("userId", null);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("totalAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("completedAssignment",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfProgressReport.class));
		
		return query.list();
	}
	
	public List<SummaryOfProgressReport> getSummaryOfProgressImportedReportBySurvey(List<String> purposeIds, List<Integer> userIds, Date refMonth ) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetSummaryOfProgressImportBySurveyReport] :refMonth , :purposeId , :userId ");
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String purposeId : purposeIds){
				builder.append("<survey>"+purposeId+"</survey>");
			}
			builder.append("</query>");
			
			query.setParameter("purposeId", builder.toString());
		} else {
			query.setParameter("purposeId", null);
		}
		
		if(userIds!=null && userIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : userIds){
				builder.append("<userId>"+userId+"</userId>");
			}
			builder.append("</query>");
			
			query.setParameter("userId", builder.toString());
		} else {
			query.setParameter("userId", null);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("totalAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("completedAssignment",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfProgressReport.class));
		
		return query.list();
	}
	
	public List<SummaryOfProgressReport> getImportAssignmentByOfficer(List<Integer> purposeIds, List<Integer> allocationBatchIds, List<String> teams, List<Integer> userIds, Date refMonth ) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetSummaryOfProgressReportByTeam] :refMonth , :purposeId , :userId , :allocationBatchId, :team");
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer purposeId : purposeIds){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("purposeId", builder.toString());
		} else {
			query.setParameter("purposeId", null);
		}
		
		if(allocationBatchIds!=null && allocationBatchIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer allocationBatchId : allocationBatchIds){
				builder.append("<allocationBatchId>"+allocationBatchId+"</allocationBatchId>");
			}
			builder.append("</query>");
			
			query.setParameter("allocationBatchId", builder.toString());
		} else {
			query.setParameter("allocationBatchId", null);
		}
		
		if(userIds!=null && userIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : userIds){
				builder.append("<purposeId>"+userId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("userId", builder.toString());
		} else {
			query.setParameter("userId", null);
		}
		
		if(teams!=null && teams.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String team : teams){
				builder.append("<purposeId>"+team+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("team", builder.toString());
		} else {
			query.setParameter("team", null);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("batchName",StandardBasicTypes.STRING);
		query.addScalar("totalAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("completedAssignment",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfProgressReport.class));
		
		return query.list();
	}
	
	public List<SummaryOfProgressReport> getImportAssignmentByTeam(List<Integer> purposeIds, List<Integer> allocationBatchIds, List<String> teams, List<Integer> userIds, Date refMonth ) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetSummaryOfProgressReportByTeam] :refMonth , :purposeId , :userId , :allocationBatchId, :team");
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer purposeId : purposeIds){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("purposeId", builder.toString());
		} else {
			query.setParameter("purposeId", null);
		}
		
		if(allocationBatchIds!=null && allocationBatchIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer allocationBatchId : allocationBatchIds){
				builder.append("<allocationBatchId>"+allocationBatchId+"</allocationBatchId>");
			}
			builder.append("</query>");
			
			query.setParameter("allocationBatchId", builder.toString());
		} else {
			query.setParameter("allocationBatchId", null);
		}
		
		if(userIds!=null && userIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : userIds){
				builder.append("<purposeId>"+userId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("userId", builder.toString());
		} else {
			query.setParameter("userId", null);
		}
		
		if(teams!=null && teams.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String team : teams){
				builder.append("<purposeId>"+team+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("team", builder.toString());
		} else {
			query.setParameter("team", null);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("batchName",StandardBasicTypes.STRING);
		query.addScalar("totalAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("completedAssignment",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfProgressReport.class));
		
		return query.list();
	}

	public List<SummaryOfProgressReport> getImportAssignmentByPurpose(List<Integer> purposeIds, List<Integer> allocationBatchIds, List<String> teams, List<Integer> userIds, Date refMonth ) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetSummaryOfProgressReportByTeam] :refMonth , :purposeId , :userId , :allocationBatchId, :team");
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeIds!=null && purposeIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer purposeId : purposeIds){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("purposeId", builder.toString());
		} else {
			query.setParameter("purposeId", null);
		}
		
		if(allocationBatchIds!=null && allocationBatchIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer allocationBatchId : allocationBatchIds){
				builder.append("<allocationBatchId>"+allocationBatchId+"</allocationBatchId>");
			}
			builder.append("</query>");
			
			query.setParameter("allocationBatchId", builder.toString());
		} else {
			query.setParameter("allocationBatchId", null);
		}
		
		if(userIds!=null && userIds.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (Integer userId : userIds){
				builder.append("<purposeId>"+userId+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("userId", builder.toString());
		} else {
			query.setParameter("userId", null);
		}
		
		if(teams!=null && teams.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<query>");
			for (String team : teams){
				builder.append("<purposeId>"+team+"</purposeId>");
			}
			builder.append("</query>");
			
			query.setParameter("team", builder.toString());
		} else {
			query.setParameter("team", null);
		}
		
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("team",StandardBasicTypes.STRING);
		query.addScalar("survey",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("totalAssignment",StandardBasicTypes.INTEGER);
		query.addScalar("completedAssignment",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOfProgressReport.class));
		
		return query.list();
	}
	
	public String getDetailAddressByReferenceNo(String referenceNo) {
		Criteria criteria = this.createCriteria("a")
								.createAlias("a.outlet", "o", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.property("o.detailAddress"));
		
		criteria.add(Restrictions.eq("a.referenceNo", referenceNo));
		
		criteria.setMaxResults(1);
		
		return (String)criteria.uniqueResult();
	}
	
	public long countUserAssignmentInSurveyMonth(Integer userId, Integer surveyMonthId){
		Criteria criteria = this.createCriteria("a")
				.createAlias("a.user", "u", JoinType.LEFT_OUTER_JOIN)
				.createAlias("a.surveyMonth", "s", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("u.userId", userId));
		criteria.add(Restrictions.eq("s.surveyMonthId", surveyMonthId));
		
		criteria.setProjection(Projections.rowCount());
		
		return (long)criteria.uniqueResult();
		
	}

	public long countNotSubmittedQuotationRecord(Integer assignmentId) {
		String hql = "select count(distinct r.id) "
                + " from Assignment as a "
                + " left join a.quotationRecords as r"
                + " where a.id = :id ";
		
		hql += " and r.isBackNo = false ";
		
		hql += " and r.status in ('Blank', 'Draft', 'Rejected') "
				+ " and (a.assignedCollectionDate is null or a.assignedCollectionDate <= :today ) ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("id", assignmentId);
		
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		query.setParameter("today", today);
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	public long countNotSubmittedNewRecruitment(Integer assignmentId){
		String hql = "Select count(distinct qr.id)"
				+ " from Assignment as a"
				+ " left join a.quotationRecords as qr on qr.isBackNo = false "
					+ " and qr.isBackTrack = false and qr.isNewRecruitment = true"
				+ " where a.assignmentId = :id"
				+ " and qr.status not in :status ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("id", assignmentId);
		query.setParameterList("status", new String[]{"Approved", "Submitted"});
		
		Long count = (Long) query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public long countAssignmentByDistrictIdUserId(Integer districtId, Integer surveyMonthId, Integer allocationBatchId) {
		
		String hql = "select count(distinct a.assignmentId) as count "
				+ " from QuotationRecord as qr "
				+ " inner join qr.allocationBatch as ab "
				+ " inner join ab.surveyMonth as sm "
				+ " inner join qr.assignment as a "
				+ " inner join a.outlet as o "
				+ " inner join o.tpu as t "
				+ " inner join t.district as d "
				+ " where ab.allocationBatchId = :allocationBatchId "
				+ " and sm.surveyMonthId = :surveyMonthId "
				+ " and qr.isSpecifiedUser = 0 "
				+ " and d.districtId = :districtId ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameter("districtId", districtId);
		
		return (Long)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public long countAssignmentByAllocationBatchIdUserId(Integer userId, Integer allocationBatchId){
		
		String sql = " select count(distinct a.AssignmentId) as count "
				+ "from Assignment as a "
				+ "inner join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
				+ "inner join AllocationBatch as ab on ab.AllocationBatchId = qr.AllocationBatchId "
				+ "inner join Quotation as q on q.QuotationId = qr.QuotationId "
				+ "left join Unit as u on u.UnitId = q.UnitId "
				+ "left join Outlet as o on o.OutletId = a.OutletId "
				+ "left join Tpu as tpu on tpu.TpuId = o.TpuId "
				+ "left join District as district on district.DistrictId = tpu.DistrictId "
				+ "left join ReleasedAssignment as ra on ra.AssignmentId = a.AssignmentId and ra.AssignmentAdjustmentId = 145 "
				+ "where 1=1 "
				+ "and qr.AllocationBatchId = :allocationBatchId "
				+ "and qr.UserId = :userId";

		sql += " and (qr.isSpecifiedUser = 0) ";
		sql += " and qr.isReleased = 0 ";
		
		
		SQLQuery query = this.getSession().createSQLQuery(sql);

		query.setParameter("userId", userId);
		query.setParameter("allocationBatchId", allocationBatchId);
		
		query.addScalar("count", StandardBasicTypes.LONG);
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public long countAssignmentByAllocationBatchIdUserId(Integer userId, Integer allocationBatchId, List<Integer> selectedAssignmentIds, List<Integer> excludeAssignmentIds){
		
		String sql = " select count(distinct a.AssignmentId) as count "
				+ "from Assignment as a "
				+ "inner join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
				+ "inner join AllocationBatch as ab on ab.AllocationBatchId = qr.AllocationBatchId "
				+ "inner join Quotation as q on q.QuotationId = qr.QuotationId "
				+ "left join Unit as u on u.UnitId = q.UnitId "
				+ "left join Outlet as o on o.OutletId = a.OutletId "
				+ "left join Tpu as tpu on tpu.TpuId = o.TpuId "
				+ "left join District as district on district.DistrictId = tpu.DistrictId "
				+ "left join ReleasedAssignment as ra on ra.AssignmentId = a.AssignmentId and ra.AssignmentAdjustmentId = 145 "
				+ "where 1=1 "
				+ "and qr.AllocationBatchId = :allocationBatchId "
				+ "and qr.UserId = :userId";

		sql += " and (qr.isSpecifiedUser = 0) ";
		sql += " and qr.isReleased = 0 ";
		
		if (excludeAssignmentIds!=null && excludeAssignmentIds.size() > 0) {
			sql += " and a.assignmentId not in (:excludeAssignmentIds) ";
		}
		if (selectedAssignmentIds.size() > 0) {
			sql += " and a.assignmentId not in (:selectedAssignmentIds) ";
		}
		SQLQuery query = this.getSession().createSQLQuery(sql);

		query.setParameter("userId", userId);
		query.setParameter("allocationBatchId", allocationBatchId);
		if (excludeAssignmentIds!=null && excludeAssignmentIds.size() > 0) {
			query.setParameterList("excludeAssignmentIds", excludeAssignmentIds);
		}
		if (selectedAssignmentIds.size() > 0) {
			query.setParameterList("selectedAssignmentIds", selectedAssignmentIds);
		}
		
		query.addScalar("count", StandardBasicTypes.LONG);
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public long countQuotationRecordsByAllocationBatchIdUserId(Integer userId, Integer allocationBatchId){
		
		String sql = " select count(distinct qr.QuotationRecordId) as count "
				+ "from Assignment as a "
				+ "inner join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
				+ "inner join AllocationBatch as ab on ab.AllocationBatchId = qr.AllocationBatchId "
				+ "inner join Quotation as q on q.QuotationId = qr.QuotationId "
				+ "left join Unit as u on u.UnitId = q.UnitId "
				+ "left join Outlet as o on o.OutletId = a.OutletId "
				+ "left join Tpu as tpu on tpu.TpuId = o.TpuId "
				+ "left join District as district on district.DistrictId = tpu.DistrictId "
				+ "left join ReleasedAssignment as ra on ra.AssignmentId = a.AssignmentId and ra.AssignmentAdjustmentId = 145 "
				+ "where 1=1 "
				+ "and qr.AllocationBatchId = :allocationBatchId "
				+ "and qr.UserId = :userId";

		sql += " and (qr.isSpecifiedUser = 0) ";
		sql += " and qr.isReleased = 0 ";
		SQLQuery query = this.getSession().createSQLQuery(sql);

		query.setParameter("userId", userId);
		query.setParameter("allocationBatchId", allocationBatchId);
		
		query.addScalar("count", StandardBasicTypes.LONG);
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public long countQuotationRecordsByAllocationBatchIdUserId(Integer userId, Integer allocationBatchId, List<Integer> selectedAssignmentIds, List<Integer> excludeAssignmentIds){
		
		String sql = " select count(distinct qr.QuotationRecordId) as count "
				+ "from Assignment as a "
				+ "inner join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId "
				+ "inner join AllocationBatch as ab on ab.AllocationBatchId = qr.AllocationBatchId "
				+ "inner join Quotation as q on q.QuotationId = qr.QuotationId "
				+ "left join Unit as u on u.UnitId = q.UnitId "
				+ "left join Outlet as o on o.OutletId = a.OutletId "
				+ "left join Tpu as tpu on tpu.TpuId = o.TpuId "
				+ "left join District as district on district.DistrictId = tpu.DistrictId "
				+ "left join ReleasedAssignment as ra on ra.AssignmentId = a.AssignmentId and ra.AssignmentAdjustmentId = 145 "
				+ "where 1=1 "
				+ "and qr.AllocationBatchId = :allocationBatchId "
				+ "and qr.UserId = :userId";

		sql += " and (qr.isSpecifiedUser = 0) ";
		sql += " and qr.isReleased = 0 ";
		
		if (excludeAssignmentIds!=null && excludeAssignmentIds.size() > 0) {
			sql += " and a.assignmentId not in (:excludeAssignmentIds) ";
		}
		if (selectedAssignmentIds.size() > 0) {
			sql += " and a.assignmentId not in (:selectedAssignmentIds) ";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);

		query.setParameter("userId", userId);
		query.setParameter("allocationBatchId", allocationBatchId);
		if (excludeAssignmentIds!=null && excludeAssignmentIds.size() > 0) {
			query.setParameterList("excludeAssignmentIds", excludeAssignmentIds);
		}
		if (selectedAssignmentIds.size() > 0) {
			query.setParameterList("selectedAssignmentIds", selectedAssignmentIds);
		}
		
		query.addScalar("count", StandardBasicTypes.LONG);
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentSyncData> getAssignmentsByAssignmentIds(List<Integer> assignmentIds) {
	String query1 = "SELECT DISTINCT at.* FROM QuotationRecord qr, Assignment at, " + 
				SystemConstant.getQuotationHistoryByAssignmentIdsSQL + 
				"WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.AssignmentId = at.AssignmentId ORDER BY  1  DESC ";
	
		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<AssignmentSyncData> result1 = addScalarForAssignment(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(AssignmentSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		String query2 = "SELECT DISTINCT at.* FROM QuotationRecord qr, Assignment at,  "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.AssignmentId = at.AssignmentId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<AssignmentSyncData> result2 = addScalarForAssignment(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(AssignmentSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		List<AssignmentSyncData> returnResult = new ArrayList<AssignmentSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentSyncData> getHistoryAssignmentByQuotationIdsHistoryDates(String quotationIdsHistoryDates) {
		String query1 = "SELECT DISTINCT at.* FROM QuotationRecord qr, Assignment at, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.AssignmentId = at.AssignmentId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<AssignmentSyncData> result1 = addScalarForAssignment(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(AssignmentSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		String query2 = "SELECT DISTINCT at.* FROM QuotationRecord qr, Assignment at,  "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.AssignmentId = at.AssignmentId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<AssignmentSyncData> result2 = addScalarForAssignment(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(AssignmentSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		List<AssignmentSyncData> returnResult = new ArrayList<AssignmentSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}

	public SQLQuery addScalarForAssignment(SQLQuery sqlQuery) {
		sqlQuery
		.addScalar("assignmentId", StandardBasicTypes.INTEGER)
		.addScalar("assignedCollectionDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("userId", StandardBasicTypes.INTEGER)
		.addScalar("startDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("endDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("outletId", StandardBasicTypes.INTEGER)
		.addScalar("collectionMethod", StandardBasicTypes.INTEGER)
		.addScalar("status", StandardBasicTypes.INTEGER)
		.addScalar("collectionDate", StandardBasicTypes.DATE)
		.addScalar("SurveyMonthId", StandardBasicTypes.INTEGER)
		.addScalar("isNewRecruitment", StandardBasicTypes.BOOLEAN)
		.addScalar("referenceNo", StandardBasicTypes.STRING)
		.addScalar("workingSession", StandardBasicTypes.STRING)
		.addScalar("assignedUserId", StandardBasicTypes.INTEGER)
		.addScalar("isCompleted", StandardBasicTypes.BOOLEAN)
		.addScalar("additionalFirmNo", StandardBasicTypes.STRING)
		.addScalar("additionalFirmName", StandardBasicTypes.STRING)
		.addScalar("additionalFirmAddress", StandardBasicTypes.STRING)
		.addScalar("additionalContactPerson", StandardBasicTypes.STRING)
		.addScalar("additionalNoOfForms", StandardBasicTypes.INTEGER)
		.addScalar("survey", StandardBasicTypes.STRING)
		.addScalar("isImportedAssignment", StandardBasicTypes.BOOLEAN)
		.addScalar("additionalDistrictId", StandardBasicTypes.INTEGER)
		.addScalar("additionalTpuId", StandardBasicTypes.INTEGER)
		.addScalar("additionalLatitude", StandardBasicTypes.STRING)
		.addScalar("additionalLongitude", StandardBasicTypes.STRING)
		.addScalar("outletDiscountRemark", StandardBasicTypes.STRING)
		.addScalar("lockFirmStatus", StandardBasicTypes.BOOLEAN)
		.addScalar("approvedDate", StandardBasicTypes.DATE);
		// .addScalar("localId", StandardBasicTypes.INTEGER)
		// .addScalar("localDbRecordStatus", StandardBasicTypes.STRING)
		
		return sqlQuery;
	}	
	
	@SuppressWarnings("unchecked")
	public List<PECertaintyCaseLookupTableList.BatchCodeMapping> getAssignmentBatchListByIds(Integer surveyMonthId, Integer[] assignmentIds) {

		String hql = " select a.assignmentId as assignmentId,b.code as batchCode "
				+ " from Assignment a "
				+ " left join a.surveyMonth as sm "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " where 1=1 ";
		
		if(surveyMonthId != null) {
			hql += " and sm.surveyMonthId = :surveyMonthId ";
		}
		
		if(assignmentIds != null && assignmentIds.length > 0) {
			hql += " and a.assignmentId in (:assignmentIds) ";
		}
		
		hql += " group by a.assignmentId, b.code ";
		
		Query query = this.getSession().createQuery(hql);
		
		if (surveyMonthId != null) {
			query.setParameter("surveyMonthId", surveyMonthId);
		}
		
		if(assignmentIds != null && assignmentIds.length > 0) {
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(PECertaintyCaseLookupTableList.BatchCodeMapping.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentReallocationLookupTableList.BatchCodeMapping> getAssignmentReallocationBatchListByIds(Integer[] assignmentIds) {

		String hql = " select a.assignmentId as assignmentId,b.code as batchCode "
				+ " from Assignment a "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " where 1=1 ";
		
		if(assignmentIds != null && assignmentIds.length > 0) {
			hql += " and a.assignmentId in (:assignmentIds) ";
		}
		
		hql += " group by a.assignmentId, b.code ";
		
		Query query = this.getSession().createQuery(hql);
		
		
		if(assignmentIds != null && assignmentIds.length > 0) {
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationLookupTableList.BatchCodeMapping.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	   public long countTotalAssignmentBySurveyMonth(int surveyMonthId){
	       String hql = "select count(distinct a.assignmentId) as count "
	               + " from Assignment as a "
	               + " inner join a.surveyMonth as sm"
	               + " where a.isImportedAssignment = false "
	               + " and sm.surveyMonthId = :surveyMonthId";
	 
	       Query query = getSession().createQuery(hql);
	       
	       query.setParameter("surveyMonthId", surveyMonthId);
	 
	       return (Long)query.uniqueResult();
	   }
	
	public boolean getLockFirmStatusByAssignment(Integer assignmentId){
		String [] status = new String[]{"Draft", "Blank", "Rejected"};
		
		String hql = " select case when count(distinct total.quotationRecordId) = count(distinct notStart.quotationRecordId) "
				+ " then false else true end as lockFirmStatus "				
				+ " from Assignment as a "
				+ " left join a.quotationRecords as total on total.isBackNo = false and total.isBackTrack = false "
				+ " left join a.quotationRecords as notStart on total = notStart "
				+ " and ( notStart.status in ( :status ) or notStart.availability = 2 or notStart.quotationState = 'IP' )"
				+ " where a.assignmentId = :assignmentId " ;
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("status", status);
		query.setParameter("assignmentId", assignmentId);
		
		return (boolean)query.uniqueResult();
	}
	
	public List<ReportAssignmentLookupList> getAssignmentLookupTableForIndividualQuotationRecordList(String search, int firstRecord, int displayLength, Order order, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, Integer[] oldAssignmentIds, Date month) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select a.assignmentId as id "
				+ ", case "
				+ "	when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end  as collectionDate "
				+ ", case "
				+ "	when a.startDate is null then '' "
				+ " else " + startDate + " end  as startDate "
				+ ", case "
				+ "	when a.endDate is null then '' "
				+ " else " + endDate + " end  as endDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ " from Assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				//+ " left join o.outletTypes as ot "
				
				+ " left join q.unit as u "
				+ " left join u.subItem as si "
				+ " inner join si.outletType as oto "
				+ " where a.isImportedAssignment = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		
		//]if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and substring(oto.code, len(oto.code)-2, 3) in (:outletTypeId) ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) hql += " and a.assignmentId not in (:oldAssignmentIds) ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or a.assignmentId like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		if (month != null){
			hql += " and sm.referenceMonth = :referenceMonth ";
		}
		
		hql += " and a.isCompleted = 0 ";
		
		hql += " and qr.quotationRecordId is not null ";
		
		hql += " group by "
			+ " a.assignmentId "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
				
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) query.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if (month != null){
			query.setParameter("referenceMonth", month);
		}
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(ReportAssignmentLookupList.class));
		
		return query.list();
	}
	
	public long countAssignmentLookupTableForIndividualQuotationRecordList(String search, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, Integer[] oldAssignmentIds, Date month) {
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select "
				+ " count(distinct a.assignmentId) as noOfAssignment "
				+ " from Assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				//+ " left join o.outletTypes as ot "
				
				+ " left join q.unit as u "
				+ " left join u.subItem as si "
				+ " inner join si.outletType as oto "
				
				+ " where a.isImportedAssignment = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		
//		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		System.out.print("Outlet Code :: " + outletTypeId);
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and substring(oto.code, len(oto.code)-2, 3) in (:outletTypeId) ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) hql += " and a.assignmentId not in (:oldAssignmentIds) ";
		

		if (month != null){
			hql += " and sm.referenceMonth = :referenceMonth ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or a.assignmentId like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		hql += " and a.isCompleted = 0 ";
		
		hql += " and qr.quotationRecordId is not null ";
		
//		hql += " group by "
//			+ " a.assignmentId "
//			+ ", a.collectionDate, a.startDate, a.endDate "
//			+ ", o.name, d.chineseName, t.code ";
//			
		
		Query query = this.getSession().createQuery(hql);
		
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		

		if (month != null){
			query.setParameter("referenceMonth", month);
		}
		
				
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) query.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		//query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationLookupTableList.class));
		
		
		return (long)query.uniqueResult();
	}
	
	public List<Assignment> getAssignmentStatus(List<Integer> assignmentIds){
		/*
		 * LockFirmStatus = 1 mean submit Assignment
		 */
		/*
		Criteria criteria = this.createCriteria("a");
		criteria.add(Restrictions.in("assignmentId", assignmentIds));
		return criteria.list();
		*/
		String hql = " from Assignment"
				+ " where 1=1 ";
		if(assignmentIds!=null){
			hql += " and assignmentId in ( :assignmentIds )";
			hql += " and lockFirmStatus in (1) ";
		}
		Query query = this.getSession().createQuery(hql);
		if(assignmentIds!=null){
			query.setParameterList("assignmentIds", assignmentIds);
		}return query.list();
	}
	
	// 2020-07-17: fix ReleasedAssignment FK constraint failed when deleting Assignment
	// test case: approve RUA leaving no other QR left in Assignment
	public int deleteRelatedReleasedAssignments(Integer assignmentId) {
		SQLQuery query = this.getSession().createSQLQuery("DELETE FROM ReleasedAssignment WHERE AssignmentId = :assignmentId");
		query.setParameter("assignmentId", assignmentId);
		return query.executeUpdate();
	}

}
