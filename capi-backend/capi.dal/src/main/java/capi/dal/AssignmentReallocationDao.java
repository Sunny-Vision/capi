package capi.dal;

import java.util.Date;
import java.util.List;

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
import org.springframework.util.StringUtils;

import capi.entity.AssignmentReallocation;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationApprovalEditModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationApprovalTableList;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationRecommendationEditModel;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationRecommendationTableList;
import capi.model.assignmentAllocationAndReallocation.AssignmentReallocationTableList;
import capi.model.commonLookup.AssignmentReallocationLookupTableList;
import capi.model.commonLookup.OutstandingAssignmentLookupTableList;
import capi.model.commonLookup.QuotationRecordReallocationLookupTableList;
import capi.model.report.AssignmentAdjustmentReport;
import capi.model.report.AssignmentAdjustmentSummaryReport;


@Repository("AssignmentReallocationDao")
public class AssignmentReallocationDao  extends GenericDao<AssignmentReallocation>{

	public List<AssignmentReallocationTableList> selectAllAssignmentReallocation(String search, int firstRecord, int displayLength, Order order
			, boolean isFieldOfficer, Integer originalUserId) {

		String createdDate = String.format("FORMAT(ar.createdDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select ar.assignmentReallocationId as assignmentReallocationId "
				+ ", ou.userId as originalUserId "
				+ ", (ou.staffCode + ' - ' + ou.chineseName) as originalUser "
				+ ", tu.userId as targetUserId "
				+ ", (tu.staffCode + ' - ' + tu.chineseName) as targetUser "
				+ ", ar.status as status "
				+ ", case when ar.createdDate is null then '' else " + createdDate + " end as createdDate "
				+ ", ar.createdDate as createdDate2"
				+ " from AssignmentReallocation as ar "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " where 1=1 ";
		
		if(isFieldOfficer) {
			hql += " and ou.userId = :ouUserId ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ( (ou.staffCode + ' - ' + ou.chineseName) like :search "
				 + " or (tu.staffCode + ' - ' + tu.chineseName) like :search "
				 + " or ar.status like :search "
				 + " or " + createdDate + "like :search) ";
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if (isFieldOfficer) query.setParameter("ouUserId", originalUserId);
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationTableList.class));
		
		return query.list();
	}

	public long countSelectAllAssignmentReallocation(String search) {

		String createdDate = String.format("FORMAT(ar.createdDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select count(*) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " where 1=1 ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ( (ou.staffCode + ' - ' + ou.chineseName) like :search "
				+  " or (tu.staffCode + ' - ' + tu.chineseName) like :search "
				+  " or ar.status like :search "
				+  " or " + createdDate + " like :search ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (long)query.uniqueResult();
	}

	public List<AssignmentReallocationLookupTableList> getAssignmentReallocationLookupTableList(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, 
			List<Integer> oldAssignmentIds, Integer assignmentStatus, Integer surveyMonthId) {
		
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
				//+ ", b.code as batchCode "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ ", case when count(qr.quotationRecordId) = count(qr2.quotationRecordId) then 'Not Start' else 'In Progress' end as assignmentStatus "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join a.quotationRecords as qr2 on qr2.status = 'Blank' and qr = qr2 "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join a.surveyMonth as sm "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			hql += " and a.user.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			hql += " and a.user.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		hql += " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(oldAssignmentIds != null && oldAssignmentIds.size() > 0) hql += " and a.assignmentId not in (:oldAssignmentIds) ";
		
		if(surveyMonthId != null && surveyMonthId > 0) hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				hql += " having count(distinct qr2.quotationRecordId) <> count(distinct qr.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				hql += " having count(distinct qr2.quotationRecordId) = count(distinct qr.quotationRecordId) ";
			}
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(oldAssignmentIds != null && oldAssignmentIds.size() > 0) query.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationLookupTableList.class));
		
		return query.list();
	}
	
	public List<AssignmentReallocationLookupTableList.BatchCodeMapping> getAssignmentReallocationBatchLookupTableList
			(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, 
			List<Integer> oldAssignmentIds, Integer assignmentStatus, Integer surveyMonthId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select a.assignmentId as assignmentId "
				+ ", b.code as batchCode "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join a.quotationRecords as qr2 on qr2.status = 'Blank' and qr = qr2 "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join a.surveyMonth as sm "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			hql += " and a.user.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			hql += " and a.user.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		hql += " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(oldAssignmentIds != null && oldAssignmentIds.size() > 0) hql += " and a.assignmentId not in (:oldAssignmentIds) ";
		
		if(surveyMonthId != null && surveyMonthId > 0) hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		hql += " group by a.assignmentId, b.code ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				hql += " having count(distinct qr2.quotationRecordId) <> count(distinct qr.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				hql += " having count(distinct qr2.quotationRecordId) = count(distinct qr.quotationRecordId) ";
			}
		}
		
		hql += " order by a.assignmentId desc ";
		
		Query query = this.getSession().createQuery(hql);
		//query.setFirstResult(firstRecord);
		//query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(oldAssignmentIds != null && oldAssignmentIds.size() > 0) query.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationLookupTableList.BatchCodeMapping.class));
		
		return query.list();
	}

	public long countAssignmentReallocationLookupTableList(String search, 
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, 
			List<Integer> oldAssignmentIds, Integer assignmentStatus, Integer surveyMonthId) {
		
		String collectionDate = String.format("FORMAT(a.CollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.StartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.EndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		/*String hql = " select count( distinct a.assignmentId ) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join a.quotationRecords as qr2 on qr2.status = 'Blank' and qr = qr2 "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " where 1=1 ";*/
		String sql = " select count(*) from ( "
				+ " select distinct a.AssignmentId "
				+ " from AssignmentReallocation as ar "
				+ " right join ReallocatedAssignment as ra on ar.AssignmentReallocationId = ra.AssignmentReallcationId "
				+ " right join Assignment as a on ra.AssignmentId = a.AssignmentId "
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId "
				+ " left join QuotationRecord as qr2 on a.AssignmentId = qr2.AssignmentId "
				+ " and qr2.status = 'Blank' and qr.QuotationRecordId = qr2.QuotationRecordId "
				+ " left join Quotation as q on qr.QuotationId = q.QuotationId "
				+ " left join Batch as b on q.BatchId = b.BatchId "
				+ " left join Outlet as o on a.OutletId = o.OutletId "
				+ " left join Tpu as t on o.TpuId = t.TpuId "
				+ " left join District as d on t.DistrictId = d.DistrictId "
				+ " left join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
				+ " left join vwOutletTypeShortForm as v on oto.ShortCode = v.ShortCode "
				+ " left join SurveyMonth as sm on a.surveyMonthId = sm.surveyMonthId "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			sql += " and a.UserId = :originalUserId "
				+ " and ( a.IsImportedAssignment = 1 or ( a.IsImportedAssignment = 0 "
//				+ " and qr.Status in (:blank, :draft, :rejected) ) )";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			sql += " and a.UserId is null "
				+ " and ( a.IsImportedAssignment = 1 or ( a.IsImportedAssignment = 0 "
//				+ " and qr.QuotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		sql += " and qr.IsBackTrack = 0 and qr.IsBackNo = 0 ";
		
		if(tpuIds != null) sql += " and t.TpuId in (:tpuIds) ";
		
//		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and ot.shortCode = :outletTypeId ";
		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and v.ShortCode = :outletTypeId ";
		
		if(districtId != null) sql += " and d.DistrictId = :districtId ";
		
		if(batchId != null) sql += " and b.BatchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( "
				+ " case "
				+ " when a.CollectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(oldAssignmentIds != null && oldAssignmentIds.size() > 0) sql += " and a.AssignmentId not in (:oldAssignmentIds) ";
		
		if(surveyMonthId != null && surveyMonthId > 0) sql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.Name like :search or d.ChineseName like :search or t.Code like :search or b.Code like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.StartDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.EndDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " ) ";
		}
		
		sql += " and a.IsCompleted = 0 ";
		
		sql += " group by "
				+ " a.AssignmentId "
				+ ", a.CollectionDate, a.StartDate, a.EndDate "
				+ ", o.Name, d.ChineseName, t.Code ";
			
			if (assignmentStatus != null) {
				if (assignmentStatus == 1) { // In Progress
					sql += " having count(distinct qr2.QuotationRecordId) <> count(distinct qr.QuotationRecordId) ";
				} else if (assignmentStatus == 2) { // Not Start
					sql += " having count(distinct qr2.QuotationRecordId) = count(distinct qr.QuotationRecordId) ";
				}
			}
		
		sql += " ) as data ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(oldAssignmentIds != null && oldAssignmentIds.size() > 0) query.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
//		return (long)query.uniqueResult();
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getAssignmentReallocationLookupTableSelectAll(String search,
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate
			,Integer assignmentStatus, Integer surveyMonthId, List<Integer> excludedAssignmentIds) {

		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select distinct a.assignmentId as assignmentId "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join a.quotationRecords as qr2 on qr2.status = 'Blank' and qr = qr2 "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join a.surveyMonth as sm "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			hql += " and a.user.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			hql += " and a.user.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
//		hql += " and ( ar.assignmentReallocationId is null or ( ar.assignmentReallocationId is not null and ar.status = :arStatus ) ) "
		hql += " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if(surveyMonthId != null && surveyMonthId > 0) hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		//2018-01-11 cheung_cheng
		if (excludedAssignmentIds != null && excludedAssignmentIds.size() > 0){
			hql += " and a.assignmentId not in (:excludedAssignmentIds) ";
		}
		
		hql += " and a.isCompleted = 0 ";
		
		hql += " group by "
			+ " a.assignmentId "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				hql += " having count(distinct qr2.quotationRecordId) <> count(distinct qr.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				hql += " having count(distinct qr2.quotationRecordId) = count(distinct qr.quotationRecordId) ";
			}
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
//		query.setParameter("arStatus", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_REJECTED);
		if (excludedAssignmentIds != null && excludedAssignmentIds.size() > 0){
			query.setParameterList("excludedAssignmentIds", excludedAssignmentIds);
		}
		
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (List<Integer>)query.list();
	}
	
	public List<AssignmentReallocationLookupTableList> getAssignmentReallocationListByIds(Integer originalUserId, Integer[] assignmentIds, List<Integer> excludedAssignmentIds) {
		
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
				//+ ", b.code as batchCode "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ ", case when count(qr.quotationRecordId) = count(qr2.quotationRecordId) then 'Not Start' else 'In Progress' end as assignmentStatus "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join a.quotationRecords as qr2 on qr2.status = 'Blank' and qr = qr2 "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			hql += " and a.user.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			hql += " and a.user.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
//		hql += " and ( ar.assignmentReallocationId is null or ( ar.assignmentReallocationId is not null and ar.status = :arStatus ) ) ";
		
		if (excludedAssignmentIds != null && excludedAssignmentIds.size() > 0){
			hql += " and a.assignmentId not in (:excludedAssignmentIds) ";
		}
		
		
		hql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(assignmentIds != null && assignmentIds.length > 0) hql += " and a.assignmentId in (:assignmentIds) ";
		
		hql += " group by "
			+ " a.assignmentId "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		Query query = this.getSession().createQuery(hql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
//		query.setParameter("arStatus", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_REJECTED);
		if (excludedAssignmentIds != null && excludedAssignmentIds.size()>0){
			query.setParameterList("excludedAssignmentIds", excludedAssignmentIds);
		}
		
		if(assignmentIds != null && assignmentIds.length > 0) query.setParameterList("assignmentIds", assignmentIds);
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationLookupTableList.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getAssignmentIdsFromReallocatedAssignment(Integer assignmentReallocationId) {
		Criteria criteria = this.createCriteria("ar")
								.createAlias("ar.assignments", "a", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("a.assignmentId"));
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("ar.assignmentReallocationId", assignmentReallocationId));
		
		return (List<Integer>) criteria.list();
	}
	
	public List<OutstandingAssignmentLookupTableList> getOutstandingAssignmentLookupTableList(String search, int firstRecord, int displayLength, Order order, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, Integer[] oldAssignmentIds) {
		
		String collectionDate = String.format("FORMAT(a.AssignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.StartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.EndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		/*
		String hql = " select a.assignmentId as id "
				+ ", case when a.assignedCollectionDate is null then '' else " + collectionDate + " end as collectionDate"
				+ ", case when a.startDate is null and a.endDate is null then '' "
				+ " else " + endDate + " end  as seDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", b.code as batchCode "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ ", u.staffCode + ' - ' + u.chineseName as pic "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " right join a.quotationRecords as qr "
				+ " right join qr.quotation as q "
				+ " right join q.batch as b "
				+ " right join a.outlet as o "
				+ " right join o.tpu as t "
				+ " right join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join a.user as u "
				+ " where 1=1 ";
		
		hql += " and (a.assignedCollectionDate is not null and a.assignedCollectionDate > getdate() "
				+ " or a.assignedCollectionDate is null  and a.endDate > getdate() "
				+ " ) "
				+ " and qr.status in (:blank, :draft, :rejected) ";
		*/
		String sql = " select a.AssignmentId as id "
				+ ", case when a.AssignedCollectionDate is null then '' else " + collectionDate + " end as collectionDate "
				+ ", case when a.StartDate is null and a.EndDate is null then '' "
				+ " else " + startDate + " + ' / ' + " + endDate + " end as seDate "
				+ ", o.Name as firm "
				+ ", d.ChineseName as district "
				+ ", t.Code as tpu "
				//+ ", b.code as batchCode "
				+ ", count(distinct qr.QuotationRecordId) as noOfQuotation "
				+ ", u.StaffCode + ' - ' + u.ChineseName as pic "
				+ " from AssignmentReallocation as ar "
				+ " right join ReallocatedAssignment as ra on ar.AssignmentReallocationId = ra.AssignmentReallcationId "
				+ " right join Assignment as a on ra.AssignmentId = a.AssignmentId "
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId "
				+ " left join Quotation as q on qr.QuotationId = q.QuotationId "
				+ " left join Batch as b on q.BatchId = b.BatchId "
				+ " left join Outlet as o on a.OutletId = o.OutletId "
				+ " left join Tpu as t on o.TpuId = t.TpuId "
				+ " left join District as d on t.DistrictId = d.DistrictId "
				+ " left join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
				+ " left join dbo.vwOutletTypeShortForm as v on oto.ShortCode = v.ShortCode "
				+ " left join [User] as u on a.UserId = u.UserId "
				+ " where 1 = 1 ";
		
//		sql += " and (a.AssignedCollectionDate is not null and a.AssignedCollectionDate >= getdate() "
//				+ " or a.AssignedCollectionDate is null and a.EndDate >= getdate() ) "
				sql += " and qr.Status in (:blank, :draft, :rejected) ";
		
		if(tpuIds != null) sql += " and t.TpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and oto.ShortCode = :outletTypeId ";
		
		if(districtId != null) sql += " and d.DistrictId = :districtId ";
		
		if(batchId != null) sql += " and b.BatchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( ( "
				+ " case "
				+ " when a.AssignedCollectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
			
			sql += " or ( "
				+ " case "
				+ " when a.EndDate is null then '' "
				+ " else " + endDate + " end ) like :searchCollectionDate ) ";
		}
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0){
			sql += " and a.AssignmentId not in (:oldAssignmentIds) ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			//2018-01-15 cheung missing PIC
//			sql += " and  ( o.Name like :search or d.ChineseName like :search or t.Code like :search or b.Code like :search ) "
			sql += " and  ( o.Name like :search or d.ChineseName like :search or t.Code like :search or b.Code like :search "
					+ " or u.StaffCode like :search  or u.ChineseName like :search) ";
				/*+ " or ( "
				+ "   case "
				+ "   when a.AssignedCollectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search  "
				+ " or ( "
				+ "   case "
				+ "   when a.EndDate is null then '' "
				+ "   else " + endDate + " end ) like :search ) "*/;
		}
		
		sql += " group by "
			+ " a.AssignmentId "
			+ ", a.AssignedCollectionDate, a.StartDate, a.EndDate "
			+ ", o.Name, d.ChineseName, t.Code "
			+ ", u.StaffCode, u.ChineseName ";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		sqlQuery.setFirstResult(firstRecord);
		sqlQuery.setMaxResults(displayLength);
		
		sqlQuery.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		sqlQuery.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
		sqlQuery.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		
		if(tpuIds != null) sqlQuery.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) sqlQuery.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) sqlQuery.setParameter("districtId", districtId);
		
		if(batchId != null) sqlQuery.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) sqlQuery.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) sqlQuery.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if (!StringUtils.isEmpty(search)) sqlQuery.setParameter("search", String.format("%%%s%%", search));
		
		sqlQuery.addScalar("id", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("collectionDate", StandardBasicTypes.STRING);
		sqlQuery.addScalar("seDate", StandardBasicTypes.STRING);
		sqlQuery.addScalar("firm", StandardBasicTypes.STRING);
		sqlQuery.addScalar("district", StandardBasicTypes.STRING);
		sqlQuery.addScalar("tpu", StandardBasicTypes.STRING);
		//sqlQuery.addScalar("batchCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		sqlQuery.addScalar("pic", StandardBasicTypes.STRING);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(OutstandingAssignmentLookupTableList.class));
		
		return sqlQuery.list();
	}
	
	public List<OutstandingAssignmentLookupTableList.BatchCodeMapping> getOutstandingAssignmentBatchLookupTableList(String search, int firstRecord, int displayLength
			, Order order, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, Integer[] oldAssignmentIds) {
		
		String collectionDate = String.format("FORMAT(a.AssignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.StartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.EndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select a.AssignmentId as assignmentId "
				+ ", b.code as batchCode "
				+ " from AssignmentReallocation as ar "
				+ " right join ReallocatedAssignment as ra on ar.AssignmentReallocationId = ra.AssignmentReallcationId "
				+ " right join Assignment as a on ra.AssignmentId = a.AssignmentId "
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId "
				+ " left join Quotation as q on qr.QuotationId = q.QuotationId "
				+ " left join Batch as b on q.BatchId = b.BatchId "
				+ " left join Outlet as o on a.OutletId = o.OutletId "
				+ " left join Tpu as t on o.TpuId = t.TpuId "
				+ " left join District as d on t.DistrictId = d.DistrictId "
				+ " left join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
				+ " left join dbo.vwOutletTypeShortForm as v on oto.ShortCode = v.ShortCode "
				+ " left join [User] as u on a.UserId = u.UserId "
				+ " where 1 = 1 ";
		
//		sql += " and (a.AssignedCollectionDate is not null and a.AssignedCollectionDate > getdate() "
//				+ " or a.AssignedCollectionDate is null and a.EndDate > getdate() ) "
		sql += " and qr.Status in (:blank, :draft, :rejected) ";
		
		if(tpuIds != null) sql += " and t.TpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and oto.ShortCode = :outletTypeId ";
		
		if(districtId != null) sql += " and d.DistrictId = :districtId ";
		
		if(batchId != null) sql += " and b.BatchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( ( "
				+ " case "
				+ " when a.AssignedCollectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
			
			sql += " or ( "
				+ " case "
				+ " when a.EndDate is null then '' "
				+ " else " + endDate + " end ) like :searchCollectionDate ) ";
		}
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0){
			sql += " and a.AssignmentId not in (:oldAssignmentIds) ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.Name like :search or d.ChineseName like :search or t.Code like :search or b.Code like :search "
					+ " or u.StaffCode like :search  or u.ChineseName like :search) ";
		}
		
		sql += " group by "
			+ " a.AssignmentId, b.Code ";
		
		sql += " order by a.assignmentId desc ";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		//sqlQuery.setFirstResult(firstRecord);
		//sqlQuery.setMaxResults(displayLength);
		
		sqlQuery.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		sqlQuery.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
		sqlQuery.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		
		if(tpuIds != null) sqlQuery.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) sqlQuery.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) sqlQuery.setParameter("districtId", districtId);
		
		if(batchId != null) sqlQuery.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) sqlQuery.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) sqlQuery.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if (!StringUtils.isEmpty(search)) sqlQuery.setParameter("search", String.format("%%%s%%", search));
		
		sqlQuery.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("batchCode", StandardBasicTypes.STRING);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(OutstandingAssignmentLookupTableList.BatchCodeMapping.class));
		
		return sqlQuery.list();
	}

	public long countOutstandingAssignmentLookupTableList(String search, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, Integer[] oldAssignmentIds) {
		
//		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);

		String collectionDate = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select count( distinct a.assignmentId ) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join a.user as u "
				+ " where 1=1 ";
		
		//hql += " and a.collectionDate is not null and a.collectionDate > getdate() and qr.status in (:blank, :draft, :rejected) ";
//		hql += " and (a.assignedCollectionDate is not null and a.assignedCollectionDate >= getdate() "
//				+ " or a.assignedCollectionDate is null  and a.endDate >= getdate() "
//				+ " ) "
			hql	+= " and qr.status in (:blank, :draft, :rejected) ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
				
			hql += " or ( "
				+ " case "
				+ " when a.endDate is null then '' "
				+ " else " + endDate + " end ) like :searchCollectionDate ) ";
		}
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) hql += " and a.assignmentId not in (:oldAssignmentIds) ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
					+ " or u.staffCode like :search  or u.chineseName like :search) ";
					/*+ " or ( "
					+ "   case "
					+ "   when a.collectionDate is null then '' "
					+ "   else " + collectionDate + " end ) like :search  "
					+ " or ( "
					+ "   case "
					+ "   when a.endDate is null then '' "
					+ "   else " + endDate + " end ) like :search ) "*/;
//			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
//				+ " or ( "
//				+ "   case "
//				+ "   when a.collectionDate is null then '' "
//				+ "   else " + collectionDate + " end ) like :search ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
		query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(oldAssignmentIds != null && oldAssignmentIds.length > 0) query.setParameterList("oldAssignmentIds", oldAssignmentIds);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (long)query.uniqueResult();
	}

	public List<Integer> getOutstandingAssignmentLookupTableSelectAll(String search, 
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate) {
		
//		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String collectionDate = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select distinct a.assignmentId as id "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join a.user as u "
				+ " where 1=1 ";
		
		//hql += " and a.collectionDate is not null and a.collectionDate > getdate() and qr.status in (:blank, :draft, :rejected) ";
//		hql += " and (a.assignedCollectionDate is not null and a.assignedCollectionDate > getdate() "
//				+ " or a.assignedCollectionDate is null  and a.endDate > getdate() "
//				+ " ) "
		hql	+= " and qr.status in (:blank, :draft, :rejected) ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
//		if (!StringUtils.isEmpty(searchCollectionDate)) {
//			hql += " and ( "
//				+ " case "
//				+ " when a.collectionDate is null then '' "
//				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
//		}
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and  ( "
					+ " ( "
					+ "   case "
					+ "   when a.assignedCollectionDate is null then '' "
					+ "   else " + collectionDate + " end ) like :searchCollectionDate  "
					+ " or ( "
					+ "   case "
					+ "   when a.endDate is null then '' "
					+ "   else " + endDate + " end ) like :searchCollectionDate ) ";
		}
		
//		if (!StringUtils.isEmpty(search)) {
//			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
//				+ " or ( "
//				+ "   case "
//				+ "   when a.collectionDate is null then '' "
//				+ "   else " + collectionDate + " end ) like :search ) ";
//		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
					+ " or u.staffCode like :search  or u.chineseName like :search) ";
					/*+ " or ( "
					+ "   case "
					+ "   when a.collectionDate is null then '' "
					+ "   else " + collectionDate + " end ) like :search  "
					+ " or ( "
					+ "   case "
					+ "   when a.endDate is null then '' "
					+ "   else " + endDate + " end ) like :search ) "*/;
//			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
//				+ " or ( "
//				+ "   case "
//				+ "   when a.collectionDate is null then '' "
//				+ "   else " + collectionDate + " end ) like :search ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
		query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return query.list();
	}

	public List<OutstandingAssignmentLookupTableList> getOutstandingAssignmentListByIds(Integer[] assignmentIds) {
		
//		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String collectionDate = String.format("FORMAT(a.AssignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.StartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.EndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		/*String hql = " select a.assignmentId as id "
				+ ", case "
				+ "	when a.assignedCollectionDate is null and a.endDate is null then '' "
				+ " when a.assignedCollectionDate is not null then "+ collectionDate
				+ " else " + endDate + " end  as collectionDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", b.code as batchCode "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ ", u.staffCode + ' - ' + u.chineseName as pic "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " right join a.quotationRecords as qr "
				+ " right join qr.quotation as q "
				+ " right join q.batch as b "
				+ " right join a.outlet as o "
				+ " right join o.tpu as t "
				+ " right join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join a.user as u "
				+ " where 1=1 ";
		
//		hql += " and a.collectionDate is not null and a.collectionDate > getdate() and qr.status in (:blank, :draft, :rejected) ";
		
		hql += " and (a.assignedCollectionDate is not null and a.assignedCollectionDate > getdate() "
				+ " or a.assignedCollectionDate is null  and a.endDate > getdate() "
				+ " ) "
				+ " and qr.status in (:blank, :draft, :rejected) ";
		*/
		String sql = " select a.AssignmentId as id "
				+ ", case when a.AssignedCollectionDate is null then '' else " + collectionDate + " end as collectionDate "
				+ ", case when a.StartDate is null and a.EndDate is null then '' "
				+ " else " + startDate + " + ' / ' + " + endDate + " end as seDate "
				+ ", o.Name as firm "
				+ ", d.ChineseName as district "
				+ ", t.Code as tpu "
				//+ ", b.code as batchCode "
				+ ", count(distinct qr.QuotationRecordId) as noOfQuotation "
				+ ", u.StaffCode + ' - ' + u.ChineseName as pic "
				+ " from AssignmentReallocation as ar "
				+ " right join ReallocatedAssignment as ra on ar.AssignmentReallocationId = ra.AssignmentReallcationId "
				+ " right join Assignment as a on ra.AssignmentId = a.AssignmentId "
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId "
				+ " left join Quotation as q on qr.QuotationId = q.QuotationId "
				+ " left join Batch as b on q.BatchId = b.BatchId "
				+ " left join Outlet as o on a.OutletId = o.OutletId "
				+ " left join Tpu as t on o.TpuId = t.TpuId "
				+ " left join District as d on t.DistrictId = d.DistrictId "
				+ " left join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
				+ " left join dbo.vwOutletTypeShortForm as v on oto.ShortCode = v.ShortCode "
				+ " left join [User] as u on a.UserId = u.UserId "
				+ " where 1 = 1 ";
		
//		sql += " and (a.AssignedCollectionDate is not null and a.AssignedCollectionDate > getdate() "
//				+ " or a.AssignedCollectionDate is null and a.EndDate > getdate() ) "
		sql += " and qr.Status in (:blank, :draft, :rejected) ";
		
		if(assignmentIds != null && assignmentIds.length > 0) sql += " and a.AssignmentId in (:assignmentIds) ";
		
		sql += " group by "
			+ " a.AssignmentId "
			+ ", a.AssignedCollectionDate, a.StartDate, a.EndDate  "
			+ ", o.Name, d.ChineseName, t.Code "
			+ ", u.StaffCode, u.ChineseName ";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		sqlQuery.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
		sqlQuery.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		
		if(assignmentIds != null && assignmentIds.length > 0) sqlQuery.setParameterList("assignmentIds", assignmentIds);
		
		sqlQuery.addScalar("id", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("collectionDate", StandardBasicTypes.STRING);
		sqlQuery.addScalar("seDate", StandardBasicTypes.STRING);
		sqlQuery.addScalar("firm", StandardBasicTypes.STRING);
		sqlQuery.addScalar("district", StandardBasicTypes.STRING);
		sqlQuery.addScalar("tpu", StandardBasicTypes.STRING);
		//sqlQuery.addScalar("batchCode", StandardBasicTypes.STRING);
		sqlQuery.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		sqlQuery.addScalar("pic", StandardBasicTypes.STRING);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(OutstandingAssignmentLookupTableList.class));
		
		return sqlQuery.list();
	}

	public List<OutstandingAssignmentLookupTableList.BatchCodeMapping> getOutstandingAssignmentBatchListByIds(Integer[] assignmentIds) {
		
		String sql = " select a.AssignmentId as assignmentId "
				+ ", b.code as batchCode "
				+ " from AssignmentReallocation as ar "
				+ " right join ReallocatedAssignment as ra on ar.AssignmentReallocationId = ra.AssignmentReallcationId "
				+ " right join Assignment as a on ra.AssignmentId = a.AssignmentId "
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId "
				+ " left join Quotation as q on qr.QuotationId = q.QuotationId "
				+ " left join Batch as b on q.BatchId = b.BatchId "
				+ " left join Outlet as o on a.OutletId = o.OutletId "
				+ " left join Tpu as t on o.TpuId = t.TpuId "
				+ " left join District as d on t.DistrictId = d.DistrictId "
				+ " left join OutletTypeOutlet as oto on o.OutletId = oto.OutletId "
				+ " left join dbo.vwOutletTypeShortForm as v on oto.ShortCode = v.ShortCode "
				+ " left join [User] as u on a.UserId = u.UserId "
				+ " where 1 = 1 ";
		
//		sql += " and (a.AssignedCollectionDate is not null and a.AssignedCollectionDate > getdate() "
//				+ " or a.AssignedCollectionDate is null and a.EndDate > getdate() ) "
		sql += " and qr.Status in (:blank, :draft, :rejected) ";
		
		if(assignmentIds != null && assignmentIds.length > 0) sql += " and a.AssignmentId in (:assignmentIds) ";
		
		sql += " group by "
			+ " a.AssignmentId, b.Code ";
		
		SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		
		sqlQuery.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
		sqlQuery.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
		sqlQuery.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		
		if(assignmentIds != null && assignmentIds.length > 0) sqlQuery.setParameterList("assignmentIds", assignmentIds);
		
		sqlQuery.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		sqlQuery.addScalar("batchCode", StandardBasicTypes.STRING);
		
		sqlQuery.setResultTransformer(Transformers.aliasToBean(OutstandingAssignmentLookupTableList.BatchCodeMapping.class));
		
		return sqlQuery.list();
	}

	@SuppressWarnings("unchecked")
	public List<AssignmentReallocationRecommendationTableList> selectAllAssignmentReallocationRecommendation(String search, 
					int firstRecord, int displayLength, Order order, List<Integer> actedUsers) {

		String createdDate = String.format("FORMAT(ar.createdDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select ar.assignmentReallocationId as assignmentReallocationId "
				+ ", ou.userId as originalFieldOfficerId "
				+ ", ou.staffCode as originalFieldOfficer "
				+ ", case when ou.chineseName is null then ou.englishName else ou.chineseName end as originalFieldOfficerName "
				+ ", tu.userId as targetFieldOfficerId "
				+ ", tu.staffCode as targetFieldOfficer "
				+ ", case when tu.chineseName is null then tu.englishName else tu.chineseName end as targetFieldOfficerName "
				+ ", count(distinct a.assignmentId) as countAssignment "
				+ ", count(distinct qr.quotationRecordId) as countQuotationRecord "
				+ ", case when ar.createdDate is null then '' else " + createdDate + " end as createdDate"
				+ " from AssignmentReallocation as ar "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join ar.submitToUser as su "
				+ " left join ar.assignments as a "
				+ " left join ar.quotationRecords as qr "
				+ " where 1=1 ";
		
		hql += " and ar.status = :submitted and su.userId in (:actedUsers) ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ( ou.staffCode like :search or ou.chineseName like :search "
				+  " or tu.staffCode like :search or tu.chineseName like :search "
				+  " or "+ createdDate + " like :search ) ";
		}
		
		hql += " group by ar.assignmentReallocationId, ou.userId, ou.staffCode "
				+ ", ou.chineseName, ou.englishName, tu.userId, tu.staffCode, tu.chineseName, tu.englishName, ar.createdDate ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setParameter("submitted", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_SUBMITTED);
		query.setParameterList("actedUsers", actedUsers);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationRecommendationTableList.class));
		
		return query.list();
	}

	public long countSelectAllAssignmentReallocationRecommendation(String search, List<Integer> actedUsers) {

		String createdDate = String.format("FORMAT(ar.createdDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select count(distinct ar.assignmentReallocationId) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join ar.submitToUser as su "
				+ " left join ar.assignments as a "
				+ " left join ar.quotationRecords as qr "
				+ " where 1=1 ";
		
		hql += " and ar.status = :submitted and su.userId in (:actedUsers) ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ( ou.staffCode like :search or ou.chineseName like :search "
				+  " or tu.staffCode like :search or tu.chineseName like :search "
				+  " or " + createdDate + " like :search) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("submitted", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_SUBMITTED);
		query.setParameterList("actedUsers", actedUsers);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (long)query.uniqueResult();
	}
	
	public List<AssignmentReallocationRecommendationEditModel> getAssignmentReallocationRecommendationList(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			Integer assignmentStatus, Integer surveyMonthId, Integer assignmentReallocationId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select ar.assignmentReallocationId as assignmentReallocationId "
				+ ", ar.rejectReason as rejectReason "
				+ ", a.assignmentId as assignmentId "
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
				//+ ", b.code as batchCode "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ ", case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end as selected "
				+ ", case when count(qr.quotationRecordId) = count(qr2.quotationRecordId) then 'Not Start' else 'In Progress' end as assignmentStatus "
				+ " from Assignment as a "
				+ " left join reallocatedAssignment as ra on ra.assignmentId = a.assignmentId and ra.AssignmentReallcationId = :assignmentReallocationId "
				+ " left join assignmentReallocation as ar on ra.AssignmentReallcationId = ar.AssignmentReallocationId "
				+ " left join quotationRecord as qr on qr.assignmentId = a.assignmentId "
				+ " left join quotationRecord as qr2 on qr2.status = 'Blank' and qr.quotationRecordId = qr2.quotationRecordId "
				+ " left join quotation as q on qr.quotationId = q.quotationId "
				+ " left join batch as b on b.batchId = q.batchId "
				+ " left join outlet as o on a.outletId = o.outletId "
				+ " left join tpu as t on t.tpuId = o.tpuId "
				+ " left join district as d on d.districtId = t.districtId "
				+ " left join [user] as ou on ar.originalUserId = ou.userId "
				+ " left join [user] as tu on tu.userId = ar.targetUserId "
				+ " left join unit as u on u.unitId = q.unitId "
				+ " left join subItem as si on si.subItemId = u.subItemId "
				+ " left join outletType as ot on si.outletTypeId = ot.outletTypeId "
				+ " left join surveyMonth as sm on a.surveyMonthId = sm.surveyMonthId "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			sql += " and a.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			sql += " and a.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		sql += " and ( tu.userId = :targetUserId or tu.userId is null ) ";
		
		if(tpuIds != null) sql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and substring(ot.Code, len(ot.Code)-2, 3) = :outletTypeId ";
		
		if(districtId != null) sql += " and d.districtId = :districtId ";
		
		if(batchId != null) sql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(surveyMonthId != null && surveyMonthId > 0) sql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(selected)) {
			sql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		sql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
			+ " ar.assignmentReallocationId, ar.rejectReason, a.assignmentId "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				sql += " having count(distinct qr2.quotationRecordId) <> count(distinct qr.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				sql += " having count(distinct qr2.quotationRecordId) = count(distinct qr.quotationRecordId) ";
			}
		}
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		query.setParameter("targetUserId", targetUserId);
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationRecommendationEditModel.class));
		
		query.addScalar("assignmentReallocationId", StandardBasicTypes.INTEGER);
		query.addScalar("rejectReason", StandardBasicTypes.STRING);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("collectionDate", StandardBasicTypes.STRING);
		query.addScalar("startDate", StandardBasicTypes.STRING);
		query.addScalar("endDate", StandardBasicTypes.STRING);
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("selected", StandardBasicTypes.STRING);
		query.addScalar("assignmentStatus", StandardBasicTypes.STRING);
		
		return query.list();
	}

	public List<AssignmentReallocationRecommendationEditModel.BatchCodeMapping> getAssignmentReallocationRecommendationBatchList
			(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			Integer assignmentStatus, Integer surveyMonthId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select "
				+ " a.assignmentId as assignmentId "
				+ ", b.code as batchCode "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join a.quotationRecords as qr2 on qr2.status = 'Blank' and qr = qr2 "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join o.outletTypes as ot "
				+ " left join a.surveyMonth as sm "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			hql += " and a.user.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			hql += " and a.user.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		hql += " and ( tu.userId = :targetUserId or tu.userId is null ) ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(surveyMonthId != null && surveyMonthId > 0) hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(selected)) {
			hql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		hql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		hql += " group by a.assignmentId, b.code ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				hql += " having count(distinct qr2.quotationRecordId) <> count(distinct qr.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				hql += " having count(distinct qr2.quotationRecordId) = count(distinct qr.quotationRecordId) ";
			}
		}
		
		hql += " order by a.assignmentId desc ";
		
		Query query = this.getSession().createQuery(hql);
		//query.setFirstResult(firstRecord);
		//query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		query.setParameter("targetUserId", targetUserId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationRecommendationEditModel.BatchCodeMapping.class));
		
		return query.list();
	}

	public long countAssignmentReallocationRecommendationList(String search,
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			Integer assignmentStatus, Integer surveyMonthId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		/*String hql = " select count( distinct a.assignmentId ) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join o.outletTypes as ot "
				+ " where 1=1 ";*/
		String sql = " select count(*) from ( "
				+ " select distinct a.AssignmentId "
				+ " from AssignmentReallocation as ar "
				+ " right join ReallocatedAssignment as ra on ar.AssignmentReallocationId = ra.AssignmentReallcationId "
				+ " right join Assignment as a on ra.AssignmentId = a.AssignmentId "
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId "
				+ " left join QuotationRecord as qr2 on a.AssignmentId = qr2.AssignmentId "
				+ " and qr2.status = 'Blank' and qr.QuotationRecordId = qr2.QuotationRecordId "
				+ " left join Quotation as q on qr.QuotationId = q.QuotationId "
				+ " left join Batch as b on q.BatchId = b.BatchId "
				+ " left join Outlet as o on a.OutletId = o.OutletId "
				+ " left join Tpu as t on o.TpuId = t.TpuId "
				+ " left join District as d on t.DistrictId = d.DistrictId "
				+ " left join [User] as ou on ar.OriginalUserId = ou.UserId "
				+ " left join [User] as tu on ar.TargetUserId = tu.UserId "
				+ " left join Unit as u on u.UnitId = q.UnitId "
				+ " left join SubItem as si on si.SubItemId = u.SubItemId "
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ " left join SurveyMonth as sm on a.surveyMonthId = sm.surveyMonthId "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			sql += " and a.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			sql += " and a.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		sql += " and ( tu.userId = :targetUserId or tu.userId is null ) ";
		
		if(tpuIds != null) sql += " and t.tpuId in (:tpuIds) ";
		
//		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
//		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and v.ShortCode = :outletTypeId ";
		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and substring(ot.Code, len(ot.Code)-2, 3) = :outletTypeId ";
		
		if(districtId != null) sql += " and d.districtId = :districtId ";
		
		if(batchId != null) sql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(surveyMonthId != null && surveyMonthId > 0) sql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(selected)) {
			sql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		sql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
				+ " a.AssignmentId "
				+ ", a.CollectionDate, a.StartDate, a.EndDate "
				+ ", o.Name, d.ChineseName, t.Code ";
			
			if (assignmentStatus != null) {
				if (assignmentStatus == 1) { // In Progress
					sql += " having count(distinct qr2.QuotationRecordId) <> count(distinct qr.QuotationRecordId) ";
				} else if (assignmentStatus == 2) { // Not Start
					sql += " having count(distinct qr2.QuotationRecordId) = count(distinct qr.QuotationRecordId) ";
				}
			}
		
		sql += " ) as data ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		query.setParameter("targetUserId", targetUserId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
//		return (long)query.uniqueResult();
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public List<AssignmentReallocationApprovalTableList> selectAllAssignmentReallocationApproval(String search, int firstRecord, int displayLength, Order order,
					List<Integer> actedUsers) {

		String hql = " select ar.assignmentReallocationId as assignmentReallocationId "
				+ ", ou.userId as originalFieldOfficerId "
				+ ", ou.staffCode as originalFieldOfficer "
				+ ", ou.chineseName as originalFieldOfficerName "
				+ ", tu.userId as targetFieldOfficerId "
				+ ", tu.staffCode as targetFieldOfficer "
				+ ", tu.chineseName as targetFieldOfficerName "
				+ ", count(distinct a.assignmentId) as countAssignment "
				+ ", count(distinct qr.quotationRecordId) as countQuotationRecord "
				+ " from AssignmentReallocation as ar "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join ar.submitToApprove as sa "
				+ " left join ar.assignments as a "
				+ " left join ar.quotationRecords as qr "
				+ " where 1=1 ";
		
		hql += " and ar.status = :recommended and sa.userId in (:actedUsers) ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ( ou.staffCode like :search or ou.chineseName like :search or tu.staffCode like :search or tu.chineseName like :search ) ";
		}
		
		hql += " group by ar.assignmentReallocationId, ou.userId, ou.staffCode "
				+ ", ou.chineseName, ou.englishName, tu.userId, tu.staffCode, tu.chineseName, tu.englishName ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setParameter("recommended", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_RECOMMENDED);
		query.setParameterList("actedUsers", actedUsers);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationApprovalTableList.class));
		
		return query.list();
	}

	public long countSelectAllAssignmentReallocationApproval(String search, List<Integer> actedUsers) {

		String hql = " select count(distinct ar.assignmentReallocationId) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join ar.submitToApprove as sa "
				+ " left join ar.assignments as a "
				+ " left join ar.quotationRecords as qr "
				+ " where 1=1 ";
		
		hql += " and ar.status = :recommended and sa.userId in (:actedUsers) ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and ( ou.staffCode like :search or ou.chineseName like :search or tu.staffCode like :search or tu.chineseName like :search ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("recommended", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_RECOMMENDED);
		query.setParameterList("actedUsers", actedUsers);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (long)query.uniqueResult();
	}


	public List<AssignmentReallocationApprovalEditModel> getAssignmentReallocationApprovalList(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			Integer assignmentStatus, Integer surveyMonthId, Integer assignmentReallocationId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select ar.assignmentReallocationId as assignmentReallocationId "
				+ ", ar.rejectReason as rejectReason "
				+ ", a.assignmentId as assignmentId "
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
				//+ ", b.code as batchCode "
				+ ", count(distinct qr.quotationRecordId) as noOfQuotation "
				+ ", case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end as selected "
				+ ", case when count(qr.quotationRecordId) = count(qr2.quotationRecordId) then 'Not Start' else 'In Progress' end as assignmentStatus "
				+ " from Assignment as a "
				+ " left join reallocatedAssignment as ra on ra.assignmentId = a.assignmentId and ra.AssignmentReallcationId = :assignmentReallocationId "
				+ " left join assignmentReallocation as ar on ra.AssignmentReallcationId = ar.AssignmentReallocationId "
				+ " left join quotationRecord as qr on qr.assignmentId = a.assignmentId "
				+ " left join quotationRecord as qr2 on qr2.status = 'Blank' and qr.quotationRecordId = qr2.quotationRecordId "
				+ " left join quotation as q on qr.quotationId = q.quotationId "
				+ " left join batch as b on b.batchId = q.batchId "
				+ " left join outlet as o on a.outletId = o.outletId "
				+ " left join tpu as t on t.tpuId = o.tpuId "
				+ " left join district as d on d.districtId = t.districtId "
				+ " left join [user] as ou on ar.originalUserId = ou.userId "
				+ " left join [user] as tu on tu.userId = ar.targetUserId "
				+ " left join unit as u on u.unitId = q.unitId "
				+ " left join subItem as si on si.subItemId = u.subItemId "
				+ " left join outletType as ot on si.outletTypeId = ot.outletTypeId "
				+ " left join surveyMonth as sm on a.surveyMonthId = sm.surveyMonthId "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			sql += " and a.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			sql += " and a.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		sql += " and ( tu.userId = :targetUserId or tu.userId is null ) ";
		
		if(tpuIds != null) sql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and substring(ot.Code, len(ot.Code)-2, 3) = :outletTypeId ";
		
		if(districtId != null) sql += " and d.districtId = :districtId ";
		
		if(batchId != null) sql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(surveyMonthId != null && surveyMonthId > 0) sql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(selected)) {
			sql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		sql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
			+ " ar.assignmentReallocationId, ar.rejectReason , a.assignmentId "
			+ ", a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				sql += " having count(distinct qr2.quotationRecordId) <> count(distinct qr.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				sql += " having count(distinct qr2.quotationRecordId) = count(distinct qr.quotationRecordId) ";
			}
		}
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		query.setParameter("targetUserId", targetUserId);
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationApprovalEditModel.class));
		
		query.addScalar("assignmentReallocationId", StandardBasicTypes.INTEGER);
		query.addScalar("rejectReason", StandardBasicTypes.STRING);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("collectionDate", StandardBasicTypes.STRING);
		query.addScalar("startDate", StandardBasicTypes.STRING);
		query.addScalar("endDate", StandardBasicTypes.STRING);
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("selected", StandardBasicTypes.STRING);
		query.addScalar("assignmentStatus", StandardBasicTypes.STRING);
		
		return query.list();
	}

	public List<AssignmentReallocationApprovalEditModel.BatchCodeMapping> getAssignmentReallocationApprovalBatchList
			(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			Integer assignmentStatus, Integer surveyMonthId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select "
				+ " a.assignmentId as assignmentId "
				+ ", b.code as batchCode "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join a.quotationRecords as qr2 on qr2.status = 'Blank' and qr = qr2 "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join o.outletTypes as ot "
				+ " left join a.surveyMonth as sm "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			hql += " and a.user.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			hql += " and a.user.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		hql += " and ( tu.userId = :targetUserId or tu.userId is null ) ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(surveyMonthId != null && surveyMonthId > 0) hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(selected)) {
			hql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		hql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		hql += " group by a.assignmentId, b.code ";
		
		if (assignmentStatus != null) {
			if (assignmentStatus == 1) { // In Progress
				hql += " having count(distinct qr2.quotationRecordId) <> count(distinct qr.quotationRecordId) ";
			} else if (assignmentStatus == 2) { // Not Start
				hql += " having count(distinct qr2.quotationRecordId) = count(distinct qr.quotationRecordId) ";
			}
		}
		
		hql += " order by a.assignmentId desc ";
		
		Query query = this.getSession().createQuery(hql);
		//query.setFirstResult(firstRecord);
		//query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		query.setParameter("targetUserId", targetUserId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationApprovalEditModel.BatchCodeMapping.class));
		
		return query.list();
	}

	public long countAssignmentReallocationApprovalList(String search,
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			Integer assignmentStatus, Integer surveyMonthId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		/*String hql = " select count( distinct a.assignmentId ) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.assignments as a "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join o.outletTypes as ot "
				+ " where 1=1 ";*/
		String sql = " select count(*) from ( "
				+ " select distinct a.AssignmentId "
				+ " from AssignmentReallocation as ar "
				+ " right join ReallocatedAssignment as ra on ar.AssignmentReallocationId = ra.AssignmentReallcationId "
				+ " right join Assignment as a on ra.AssignmentId = a.AssignmentId "
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId "
				+ " left join QuotationRecord as qr2 on a.AssignmentId = qr2.AssignmentId "
				+ " and qr2.status = 'Blank' and qr.QuotationRecordId = qr2.QuotationRecordId "
				+ " left join Quotation as q on qr.QuotationId = q.QuotationId "
				+ " left join Batch as b on q.BatchId = b.BatchId "
				+ " left join Outlet as o on a.OutletId = o.OutletId "
				+ " left join Tpu as t on o.TpuId = t.TpuId "
				+ " left join District as d on t.DistrictId = d.DistrictId "
				+ " left join [User] as ou on ar.OriginalUserId = ou.UserId "
				+ " left join [User] as tu on ar.TargetUserId = tu.UserId "
				+ " left join Unit as u on u.UnitId = q.UnitId "
				+ " left join SubItem as si on si.SubItemId = u.SubItemId "
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ " left join SurveyMonth as sm on a.surveyMonthId = sm.surveyMonthId "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			sql += " and a.userId = :originalUserId "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.status in (:blank, :draft, :rejected) ) ) ";
				+ " and (qr.status in (:blank, :draft, :rejected) or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		} else {
			sql += " and a.userId is null "
				+ " and ( a.isImportedAssignment = 1 or ( a.isImportedAssignment = 0 "
//				+ " and qr.quotationState = :revisit ) ) ";
				+ " and (qr.quotationState = :revisit or qr.quotationState = 'IP' or qr.availability = 2) ) ) ";
		}
		
		sql += " and ( tu.userId = :targetUserId or tu.userId is null ) ";
		
		if(tpuIds != null) sql += " and t.tpuId in (:tpuIds) ";
		
//		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
//		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and v.ShortCode = :outletTypeId ";
		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and substring(ot.Code, len(ot.Code)-2, 3) = :outletTypeId ";
		
		if(districtId != null) sql += " and d.districtId = :districtId ";
		
		if(batchId != null) sql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( "
				+ " case "
				+ " when a.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(surveyMonthId != null && surveyMonthId > 0) sql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if (!StringUtils.isEmpty(selected)) {
			sql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
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
		
		sql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
				+ " a.AssignmentId "
				+ ", a.CollectionDate, a.StartDate, a.EndDate "
				+ ", o.Name, d.ChineseName, t.Code ";
			
			if (assignmentStatus != null) {
				if (assignmentStatus == 1) { // In Progress
					sql += " having count(distinct qr2.QuotationRecordId) <> count(distinct qr.QuotationRecordId) ";
				} else if (assignmentStatus == 2) { // Not Start
					sql += " having count(distinct qr2.QuotationRecordId) = count(distinct qr.QuotationRecordId) ";
				}
			}
		
		sql += " ) as data ";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
			query.setParameter("blank", SystemConstant.QUOTATIONRECORD_STATUS_BLANK);
			query.setParameter("draft", SystemConstant.QUOTATIONRECORD_STATUS_DRAFT);
			query.setParameter("rejected", SystemConstant.QUOTATIONRECORD_STATUS_REJECTED);
		} else {
			query.setParameter("revisit", SystemConstant.QUOTATIONRECORD_QUOTATIONSTATE_REVISIT);
		}
		
		query.setParameter("targetUserId", targetUserId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(surveyMonthId != null && surveyMonthId > 0) query.setParameter("surveyMonthId", surveyMonthId);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
//		return (long)query.uniqueResult();
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}

	public List<QuotationRecordReallocationLookupTableList> getQuotationRecordReallocationLookupTableList(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, 
			List<Integer> oldQuotationRecordIds, String category, String quotationStatus) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(qr.assignedStartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(qr.assignedEndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select distinct qr.quotationRecordId as id "
				+ ", case "
				+ "	when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end  as collectionDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", b.code as batchCode "
				+ "	, case when qr.assignedStartDate is null then '' "
				+ " else " + startDate + " end  as startDate "
				+ "	, case when qr.assignedEndDate is null then '' "
				+ " else " + endDate + " end  as endDate "
				+ ", u.displayName as displayName "
				+ ", u.unitCategory as category "
				+ ", qr.status as quotationStatus "
				+ ", qr.collectionDate as collectionDate2 "
				+ ", qr.assignedStartDate as startDate2 "
				+ ", qr.assignedEndDate as endDate2 "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join q.unit as u "
				+ " where 1=1 "
				+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(originalUserId != null) {
			hql += " and qr.user.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			hql += " and qr.user.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
//		hql += " and ( ar.assignmentReallocationId is null or ( ar.assignmentReallocationId is not null and ar.status = :arStatus ) ) ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if (oldQuotationRecordIds != null && oldQuotationRecordIds.size()>0 ){
			hql += " and qr.quotationRecordId not in (:oldQuotationRecordIds) ";
		}
		
		if(category != null && category.length() > 0) hql += " and u.unitCategory = :category ";
		
		if(quotationStatus != null && quotationStatus.length() > 0) hql += " and qr.status = :quotationStatus ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or u.displayName like :search or u.unitCategory like :search or qr.status like :search "
				+ " ) ";
		}
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
//		query.setParameter("arStatus", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_REJECTED);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if (oldQuotationRecordIds != null && oldQuotationRecordIds.size()>0 ){
			query.setParameterList("oldQuotationRecordIds", oldQuotationRecordIds);
		}
		
		if(category != null && category.length() > 0) query.setParameter("category", category);
		
		if(quotationStatus != null && quotationStatus.length() > 0) query.setParameter("quotationStatus", quotationStatus);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordReallocationLookupTableList.class));
		
		return query.list();
	}

	public long countQuotationRecordReallocationLookupTableList(String search, 
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, 
			List<Integer> oldQuotationRecordIds, String category, String quotationStatus) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select count( distinct qr.quotationRecordId ) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join q.unit as u "
				+ " where 1=1 "
				+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(originalUserId != null) {
			hql += " and qr.user.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			hql += " and qr.user.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
//		hql += " and ( ar.assignmentReallocationId is null or ( ar.assignmentReallocationId is not null and ar.status = :arStatus ) ) ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if (oldQuotationRecordIds != null && oldQuotationRecordIds.size()>0 ){
			hql += " and qr.quotationRecordId not in (:oldQuotationRecordIds) ";
		}
		
		if(category != null && category.length() > 0) hql += " and u.unitCategory = :category ";
		
		if(quotationStatus != null && quotationStatus.length() > 0) hql += " and qr.status = :quotationStatus ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or u.displayName like :search or u.unitCategory like :search or qr.status like :search "
				+ " ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
//		query.setParameter("arStatus", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_REJECTED);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if (oldQuotationRecordIds != null && oldQuotationRecordIds.size()>0 ){
			query.setParameterList("oldQuotationRecordIds", oldQuotationRecordIds);
		}
		
		if(category != null && category.length() > 0) query.setParameter("category", category);
		
		if(quotationStatus != null && quotationStatus.length() > 0) query.setParameter("quotationStatus", quotationStatus);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (long)query.uniqueResult();
	}

	public List<Integer> getQuotationRecordReallocationLookupTableSelectAll(String search, 
			Integer originalUserId, List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate,
			String category, String quotationStatus, List<Integer> excludedQuotationRecordIds) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select distinct qr.quotationRecordId as quotationRecordId "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join q.unit as u "
				+ " where 1=1 "
				+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(originalUserId != null) {
			hql += " and qr.user.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			hql += " and qr.user.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
//		hql += " and ( ar.assignmentReallocationId is null or ( ar.assignmentReallocationId is not null and ar.status = :arStatus ) ) ";
		if (excludedQuotationRecordIds != null && excludedQuotationRecordIds.size()>0){
			hql += " and qr.QuotationRecordId not in (:excludedQuotationRecordIds)";
		}
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(category != null && category.length() > 0) hql += " and u.unitCategory = :category ";
		
		if(quotationStatus != null && quotationStatus.length() > 0) hql += " and qr.status = :quotationStatus ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
//		query.setParameter("arStatus", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_REJECTED);
		if (excludedQuotationRecordIds != null && excludedQuotationRecordIds.size()>0){
			query.setParameterList("excludedQuotationRecordIds", excludedQuotationRecordIds);
		}
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(category != null && category.length() > 0) query.setParameter("category", category);
		
		if(quotationStatus != null && quotationStatus.length() > 0) query.setParameter("quotationStatus", quotationStatus);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return query.list();
	}

	public List<QuotationRecordReallocationLookupTableList> getQuotationRecordReallocationListByIds(
			Integer originalUserId, Integer[] quotationRecordIds, List<Integer> excludedQuotationRecordIds) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(qr.assignedStartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(qr.assignedEndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select distinct qr.quotationRecordId as id "
				+ ", case "
				+ "	when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end  as collectionDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", b.code as batchCode "
				+ "	, case when qr.assignedStartDate is null then '' "
				+ " else " + startDate + " end  as startDate "
				+ "	, case when qr.assignedEndDate is null then '' "
				+ " else " + endDate + " end  as endDate "
				+ ", u.displayName as displayName "
				+ ", u.unitCategory as category "
				+ ", qr.status as quotationStatus "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join q.unit as u "
				+ " where 1=1 "
				+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(originalUserId != null) {
			hql += " and qr.user.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			hql += " and qr.user.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
//		hql += " and ( ar.assignmentReallocationId is null or ( ar.assignmentReallocationId is not null and ar.status = :arStatus ) ) ";
		
		if (excludedQuotationRecordIds != null && excludedQuotationRecordIds.size() > 0){
			hql += " and qr.quotationRecordId not in (:excludedQuotationRecordIds)";
		}
		
		if(quotationRecordIds != null && quotationRecordIds.length > 0) hql += " and qr.quotationRecordId in (:quotationRecordIds) ";
		
		Query query = this.getSession().createQuery(hql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
//		query.setParameter("arStatus", SystemConstant.ASSIGNMENTREALLOCATION_STATUS_REJECTED);
		if (excludedQuotationRecordIds != null && excludedQuotationRecordIds.size() > 0){
			query.setParameterList("excludedQuotationRecordIds", excludedQuotationRecordIds);
		}
		
		if(quotationRecordIds != null && quotationRecordIds.length > 0) query.setParameterList("quotationRecordIds", quotationRecordIds);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordReallocationLookupTableList.class));
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getQuotationRecordIdsFromReallocatedQuotationRecord(Integer assignmentReallocationId) {
		Criteria criteria = this.createCriteria("ar")
								.createAlias("ar.quotationRecords", "qr", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("qr.quotationRecordId"));
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("ar.assignmentReallocationId", assignmentReallocationId));
		
		return (List<Integer>) criteria.list();
	}

	public List<AssignmentReallocationRecommendationEditModel> getQuotationRecordReallocationRecommendationList(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			String category, String quotationStatus, Integer assignmentReallocationId) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(qr.assignedStartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(qr.assignedEndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "Select distinct qr.QuotationRecordId as quotationRecordId "
				+ ", ar.assignmentReallocationId as assignmentReallocationId "
				+ " , case when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end as collectionDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", b.code as batchCode "
				+ ", case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end as selected "
				+ "	, case when qr.assignedStartDate is null then '' "
				+ " else " + startDate + " end  as startDate "
				+ "	, case when qr.assignedEndDate is null then '' "
				+ " else " + endDate + " end  as endDate "
				+ ", u.displayName as displayName "
				+ ", u.unitCategory as category "
				+ ", qr.status as quotationStatus "
				+ " from QuotationRecord as qr "
				+ " left join ReallocatedQuotationRecord as rq on qr.quotationRecordId = rq.quotationRecordId and rq.AssignmentReallcationId = :assignmentReallocationId "
				+ " left join AssignmentReallocation as ar on ar.assignmentReallocationId = rq.AssignmentReallcationId "
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId "
				+ " left join Batch as b on b.BatchId = q.BatchId "
				+ " left join Outlet as o on o.OutletId = qr.OutletId "
				+ " left join Tpu as t on t.tpuId = o.tpuId "
				+ " left join District as d on d.DistrictId = t.DistrictId "
				+ " left join [User] as ou on ar.OriginalUserId = ou.UserId "
				+ " left join [User] as tu on ar.TargetUserId = tu.UserId "
				+ " left join Unit as u on q.UnitId = u.UnitId "
				+ " left join SubItem as si on si.SubItemId = u.SubItemId "
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			sql += " and qr.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			sql += " and qr.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
		sql += " and ( tu.userId = :targetUserId or tu.userId is null ) "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		if(tpuIds != null) {
			sql += " and t.tpuId in (:tpuIds) ";
		}
		
		if (outletTypeId != null && outletTypeId.length() > 0) {
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) = :outletTypeId ";
		}
		
		if(districtId != null) {
			sql += " and d.districtId = :districtId ";
		}
		
		if(batchId != null) {
			sql += " and b.batchId = :batchId ";
		}
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( "
				+ " case "
				+ " when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(category != null && category.length() > 0) {
			sql += " and u.unitCategory = :category ";
		}
		
		if(quotationStatus != null && quotationStatus.length() > 0) {
			sql += " and qr.status = :quotationStatus ";
		}
		
		if (!StringUtils.isEmpty(selected)) {
			sql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or u.displayName like :search or u.unitCategory like :search or qr.status like :search "
				+ " ) ";
		}
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
		
		query.setParameter("targetUserId", targetUserId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(category != null && category.length() > 0) query.setParameter("category", category);
		
		if(quotationStatus != null && quotationStatus.length() > 0) query.setParameter("quotationStatus", quotationStatus);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationRecommendationEditModel.class));
		
		query.addScalar("assignmentReallocationId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("collectionDate", StandardBasicTypes.STRING);
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("batchCode", StandardBasicTypes.STRING);
		query.addScalar("startDate", StandardBasicTypes.STRING);
		query.addScalar("endDate", StandardBasicTypes.STRING);
		query.addScalar("displayName", StandardBasicTypes.STRING);
		query.addScalar("category", StandardBasicTypes.STRING);
		query.addScalar("quotationStatus", StandardBasicTypes.STRING);
		query.addScalar("selected", StandardBasicTypes.STRING);
		
		return query.list();
	}

	public long countQuotationRecordReallocationRecommendationList(String search, 
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			String category, String quotationStatus) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select count( distinct qr.quotationRecordId ) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join o.outletTypes as ot "
				+ " left join q.unit as u "
				+ " where 1=1 "
				+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(originalUserId != null) {
			hql += " and qr.user.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			hql += " and qr.user.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
		hql += " and ( tu.userId = :targetUserId or tu.userId is null ) ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(category != null && category.length() > 0) hql += " and u.unitCategory = :category ";
		
		if(quotationStatus != null && quotationStatus.length() > 0) hql += " and qr.status = :quotationStatus ";
		
		if (!StringUtils.isEmpty(selected)) {
			hql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or u.displayName like :search or u.unitCategory like :search or qr.status like :search "
				+ " ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
		
		query.setParameter("targetUserId", targetUserId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(category != null && category.length() > 0) query.setParameter("category", category);
		
		if(quotationStatus != null && quotationStatus.length() > 0) query.setParameter("quotationStatus", quotationStatus);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (long)query.uniqueResult();
	}

	public long countSelectedQuotationRecordReallocation(Integer originalUserId, Integer targetUserId, Integer assignmentReallocationId) {
		
		String hql = " select count(distinct ar.assignmentReallocationId) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join o.outletTypes as ot "
				+ " left join q.unit as u "
				+ " where 1=1 and ar.assignmentReallocationId = :assignmentReallocationId ";
		
		if(originalUserId != null) {
			hql += " and qr.user.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			hql += " and qr.user.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
		hql += " and ( tu.userId = :targetUserId or tu.userId is null ) "
			+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		Query query = this.getSession().createQuery(hql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
		query.setParameter("targetUserId", targetUserId);
		
		return (long)query.uniqueResult();
	}

	public List<AssignmentReallocationApprovalEditModel> getQuotationRecordReallocationApprovalList(String search, int firstRecord, int displayLength, Order order, 
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			String category, String quotationStatus, Integer assignmentReallocationId) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(qr.assignedStartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(qr.assignedEndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select distinct qr.quotationRecordId as quotationRecordId "
				+ ", ar.assignmentReallocationId as assignmentReallocationId"
				+ ", case "
				+ "	when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end  as collectionDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", b.code as batchCode "
				+ ", case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end as selected "
				+ "	, case when qr.assignedStartDate is null then '' "
				+ " else " + startDate + " end  as startDate "
				+ "	, case when qr.assignedEndDate is null then '' "
				+ " else " + endDate + " end  as endDate "
				+ ", u.displayName as displayName "
				+ ", u.unitCategory as category "
				+ ", qr.status as quotationStatus "
				+ " from QuotationRecord as qr "
				+ " left join ReallocatedQuotationRecord as rq on qr.quotationRecordId = rq.quotationRecordId and rq.AssignmentReallcationId = :assignmentReallocationId "
				+ " left join AssignmentReallocation as ar on ar.assignmentReallocationId = rq.AssignmentReallcationId "
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId "
				+ " left join Batch as b on b.BatchId = q.BatchId "
				+ " left join Outlet as o on o.OutletId = qr.OutletId "
				+ " left join Tpu as t on t.tpuId = o.tpuId "
				+ " left join District as d on d.DistrictId = t.DistrictId "
				+ " left join [User] as ou on ar.OriginalUserId = ou.UserId "
				+ " left join [User] as tu on ar.TargetUserId = tu.UserId "
				+ " left join Unit as u on q.UnitId = u.UnitId "
				+ " left join SubItem as si on si.SubItemId = u.SubItemId "
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			sql += " and qr.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			sql += " and qr.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
		sql += " and ( tu.userId = :targetUserId or tu.userId is null ) "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		if(tpuIds != null) sql += " and t.tpuId in (:tpuIds) ";
		
		//if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		if (outletTypeId != null && outletTypeId.length() > 0) sql += " and substring(ot.Code, len(ot.Code)-2, 3) = :outletTypeId ";
		
		if(districtId != null) sql += " and d.districtId = :districtId ";
		
		if(batchId != null) sql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			sql += " and ( "
				+ " case "
				+ " when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(category != null && category.length() > 0) sql += " and u.unitCategory = :category ";
		
		if(quotationStatus != null && quotationStatus.length() > 0) sql += " and qr.status = :quotationStatus ";
		
		if (!StringUtils.isEmpty(selected)) {
			sql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " ) ";
		}
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
		
		query.setParameter("targetUserId", targetUserId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(category != null && category.length() > 0) query.setParameter("category", category);
		
		if(quotationStatus != null && quotationStatus.length() > 0) query.setParameter("quotationStatus", quotationStatus);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationApprovalEditModel.class));
		
		query.addScalar("assignmentReallocationId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("collectionDate", StandardBasicTypes.STRING);
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("batchCode", StandardBasicTypes.STRING);
		query.addScalar("startDate", StandardBasicTypes.STRING);
		query.addScalar("endDate", StandardBasicTypes.STRING);
		query.addScalar("displayName", StandardBasicTypes.STRING);
		query.addScalar("category", StandardBasicTypes.STRING);
		query.addScalar("quotationStatus", StandardBasicTypes.STRING);
		query.addScalar("selected", StandardBasicTypes.STRING);
		
		return query.list();
	}

	public long countQuotationRecordReallocationApprovalList(String search, 
			Integer originalUserId, Integer targetUserId,
			List<Integer> tpuIds, String outletTypeId, Integer districtId, Integer batchId, String searchCollectionDate, String selected,
			String category, String quotationStatus) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select count( distinct qr.quotationRecordId ) as cnt "
				+ " from AssignmentReallocation as ar "
				+ " right join ar.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join ar.originalUser as ou "
				+ " left join ar.targetUser as tu "
				+ " left join q.unit as u "
				+ " left join u.subItem as si "
				+ " left join si.outletType as ot "
				+ " where 1=1 ";
		
		if(originalUserId != null) {
			hql += " and qr.user.userId = :originalUserId "
				+ " and qr.status not in (:submitted, :approved) ";
		} else {
			hql += " and qr.user.userId is null "
				+ " and qr.status not in (:submitted, :approved) ";
		}
		
		hql += " and ( tu.userId = :targetUserId or tu.userId is null ) "
			+ " and qr.isBackTrack = false and qr.isBackNo = false ";
		
		if(tpuIds != null) hql += " and t.tpuId in (:tpuIds) ";
		
//		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and substring(ot.Code, len(ot.Code)-2, 3) = :outletTypeId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(batchId != null) hql += " and b.batchId = :batchId ";
		
		if (!StringUtils.isEmpty(searchCollectionDate)) {
			hql += " and ( "
				+ " case "
				+ " when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end ) like :searchCollectionDate ";
		}
		
		if(category != null && category.length() > 0) hql += " and u.unitCategory = :category ";
		
		if(quotationStatus != null && quotationStatus.length() > 0) hql += " and qr.status = :quotationStatus ";
		
		if (!StringUtils.isEmpty(selected)) {
			hql += " and ( "
				+ " case "
				+ " when ar.assignmentReallocationId is null then 'N' "
				+ " else 'Y' end ) like :selected ";
		}
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " ) ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		if(originalUserId != null) {
			query.setParameter("originalUserId", originalUserId);
		}
		
		query.setParameter("submitted", SystemConstant.QUOTATIONRECORD_STATUS_SUBMITTED);
		query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
		
		query.setParameter("targetUserId", targetUserId);
		
		if(tpuIds != null) query.setParameterList("tpuIds", tpuIds);
		
		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(batchId != null) query.setParameter("batchId", batchId);
		
		if (!StringUtils.isEmpty(searchCollectionDate)) query.setParameter("searchCollectionDate", searchCollectionDate);
		
		if(category != null && category.length() > 0) query.setParameter("category", category);
		
		if(quotationStatus != null && quotationStatus.length() > 0) query.setParameter("quotationStatus", quotationStatus);
		
		if (!StringUtils.isEmpty(selected)) query.setParameter("selected", selected);
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		return (long)query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<AssignmentReallocation> getAllByQuotationRecordId(int id) {
		return this.createCriteria("reallocation")
				.createAlias("reallocation.quotationRecords", "qr")
				.add(Restrictions.eq("qr.id", id))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentAdjustmentReport> getAssignmentReallocationReport(List<Integer> officerIds, List<String> teams
			, List<Integer> purposeIds, List<Integer> collectionMethod, List<String> allocationBatch, Date fromMonth, Date toMonth) {
		
		//2018-01-08 cheung_cheng Spelling mistake for "Reallocation" in "Stage" (all sheets)
		String sql = "select 'Assignment Reallocation' as stage, " + 
				"sm.ReferenceMonth as referenceMonth, " + 
				//2018-01-08 cheung_cheng  "Allocation Batch" (all sheets) should show blank for Stage =  "Reallocation"
				"fromUr.StaffCode + ' - ' + fromUr.EnglishName as fromUser, " + 
				"toUr.StaffCode + ' - ' + toUr.EnglishName as toUser, " + 
				"count(distinct qr.quotationRecordId) as numOfQuotation, " + 
				"o.FirmCode as outletCode, " + 
				"o.Name as outletName, " + 
				"d.code as district, " + 
				"t.code as tpu, " + 
				"case o.CollectionMethod when 1 then 'Field Visit' when 2 then 'Telephone' when 3 then 'Fax' when 4 then 'Other' end as collectionMethod,  " + 
				"case when aa.Status = 'Rejected' then null else reUr.StaffCode + ' - ' + reUr.EnglishName end as recommendBy, " + 
				"case when aa.Status = 'Submitted' or (aa.Status = 'Rejected' and aa.RecommendDate is null) then null else aa.RecommendDate end as recommendDate, " + 
				"case when (aa.Status = 'Approved' or aa.Status = 'Recommended') then apprUr.StaffCode + ' - ' + apprUr.EnglishName else null end as approveBy, " + 
				"case when aa.Status = 'Approved' then aa.ApprovedDate else null end as approveDate, " + 
				"o.OutletId as outletId " +
				"from AssignmentReallocation aa " + 
				"left join [User] fromUr on fromUr.UserId = aa.OriginalUserId " + 
				"left join [User] toUr on toUr.UserId = aa.TargetUserId " + 
				"left join [User] reUr on reUr.UserId = aa.SubmitToUserId " + 
				"left join [User] apprUr on apprUr.UserId = aa.SubmitToApprove " + 
				"inner join ReallocatedAssignment as ra on aa.AssignmentReallocationId = ra.AssignmentReallcationId " + 
				"inner join Assignment as a on a.AssignmentId = ra.AssignmentId " +
				"left join SurveyMonth as sm on a.surveyMonthId = sm.surveyMonthId " +
				"left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId and qr.IsBackNo = 0 " +
				"left join AllocationBatch as ab on qr.AllocationBatchId = ab.AllocationBatchId " + 
				"left join Outlet as o on a.OutletId = o.OutletId " + 
				"left join Tpu as t on t.TpuId = o.TpuId " + 
				"left join District as d on d.DistrictId = t.DistrictId " + 
				"left join Quotation as q on qr.QuotationId = q.QuotationId " + 
				"left join Unit as u on q.UnitId = u.UnitId " + 
				"left join Purpose as p on p.PurposeId = u.PurposeId " + 
				" where sm.ReferenceMonth between :fromMonth and :toMonth " ;
				if(officerIds!=null && officerIds.size()>0){
					sql+= " and (fromUr.UserId in (:officerIds) or toUr.UserId in (:officerIds)) ";
				} 

				if(purposeIds!=null && purposeIds.size()>0){
					sql+= "and p.PurposeId in (:purposeIds) ";
				} 

				if(collectionMethod!=null && collectionMethod.size()>0){
					sql+= "and o.CollectionMethod in (:collectionMethod) ";
				} 
		
				if(allocationBatch!=null && allocationBatch.size()>0){
					sql+= "and ab.BatchName in (:allocationBatch) ";
				}
				sql += "group by sm.ReferenceMonth, fromUr.StaffCode, fromUr.EnglishName, toUr.StaffCode, toUr.EnglishName, o.FirmCode, d.Code , t.Code , o.Name , reUr.StaffCode, reur.EnglishName,aa.RecommendDate,apprUr.StaffCode,apprUr.EnglishName,aa.ApprovedDate,o.CollectionMethod, aa.Status, o.OutletId " +
				" union all " +
				" Select 'Quotation Reallocation' as stage, " +
				" sm.ReferenceMonth as referenceMonth," +
				" fromUr.StaffCode + ' - ' + fromUr.EnglishName as fromUser, " +
				" toUr.StaffCode + ' - ' + toUr.EnglishName as toUser, " +
				" count(distinct qr.quotationRecordId) as numOfQuotation, " + 
				" o.FirmCode as outletCode, " +
				"o.Name as outletName, " + 
				"d.code as district, " + 
				"t.code as tpu, " + 
				"case o.CollectionMethod when 1 then 'Field Visit' when 2 then 'Telephone' when 3 then 'Fax' when 4 then 'Other' end as collectionMethod,  " + 
				"case when aa.Status = 'Rejected' then null else reUr.StaffCode + ' - ' + reUr.EnglishName end as recommendBy, " + 
				"case when aa.Status = 'Submitted' or (aa.Status = 'Rejected' and aa.RecommendDate is null) then null else aa.RecommendDate end as recommendDate, " + 
				"case when (aa.Status = 'Approved' or aa.Status = 'Recommended') then apprUr.StaffCode + ' - ' + apprUr.EnglishName else null end as approveBy, " + 
				"case when aa.Status = 'Approved' then aa.ApprovedDate else null end as approveDate, " + 
				"o.OutletId as outletId" +
				" from assignmentReallocation aa " +
				" left join ReallocatedQuotationRecord as rq on aa.AssignmentReallocationId = rq.AssignmentReallcationId " + 
				" left join QuotationRecord as qr on qr.QuotationRecordId = rq.QuotationRecordId " + 
				" left join Outlet as o on qr.OutletId = o.OutletId " + 
				" left join Tpu as t on t.TpuId = o.TpuId " + 
				" left join District as d on t.DistrictId = d.DistrictId " + 
				"inner join Assignment as a on a.AssignmentId = qr.AssignmentId " +
				" left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId " + 
				" left join [User] fromUr on fromUr.UserId = aa.OriginalUserId " + 
				"left join [User] toUr on toUr.UserId = aa.TargetUserId " + 
				"left join [User] reUr on reUr.UserId = aa.SubmitToUserId " + 
				"left join [User] apprUr on apprUr.UserId = aa.SubmitToApprove "+
				"left join Quotation as q on qr.QuotationId = q.QuotationId " + 
				"left join Unit as u on q.UnitId = u.UnitId " + 
				"left join Purpose as p on p.PurposeId = u.PurposeId " +
				" where sm.ReferenceMonth between :fromMonth and :toMonth " ;
				//"group by sm.ReferenceMonth, fromUr.StaffCode, fromUr.EnglishName, toUr.StaffCode, toUr.EnglishName, o.FirmCode, d.Code , t.Code , o.Name , reUr.StaffCode, reur.EnglishName,aa.RecommendDate,apprUr.StaffCode,apprUr.EnglishName,aa.ApprovedDate,o.CollectionMethod, aa.Status ";
					
		if(officerIds!=null && officerIds.size()>0){
			sql+= " and (fromUr.UserId in (:officerIds) or toUr.UserId in (:officerIds)) ";
		} 
//		if(teams!=null && teams.size()>0){
//			sql+= "and fromUr.Team in (:teams) ";
////		} 
		
		if(purposeIds!=null && purposeIds.size()>0){
			sql+= "and p.PurposeId in (:purposeIds) ";
		} 
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			sql+= "and o.CollectionMethod in (:collectionMethod) ";
		} 
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			sql+= "and ab.BatchName in (:allocationBatch) ";
		}
		
		sql += " group by sm.ReferenceMonth, fromUr.StaffCode, fromUr.EnglishName, toUr.StaffCode, toUr.EnglishName, o.FirmCode, d.Code , t.Code , o.Name , reUr.StaffCode, reur.EnglishName,aa.RecommendDate,apprUr.StaffCode,apprUr.EnglishName,aa.ApprovedDate,o.CollectionMethod, aa.Status, o.OutletId " +
				" order by [ReferenceMonth], [fromUser], [toUser], [district], [outletCode], [recommendDate], [approveDate] ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("fromMonth", fromMonth);
		query.setParameter("toMonth", toMonth);
		if(officerIds!=null && officerIds.size()>0){
			query.setParameterList("officerIds",officerIds);
		} 
		if(teams!=null && teams.size()>0){
			query.setParameterList("teams",teams);
		} 
		
		if(purposeIds!=null && purposeIds.size()>0){
			query.setParameterList("purposeIds",purposeIds);
		} 
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			query.setParameterList("collectionMethod",collectionMethod);
		} 
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			query.setParameterList("allocationBatch",allocationBatch);
		}
				
		query.addScalar("stage",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		//2018-01-08 cheung_cheng  "Allocation Batch" (all sheets) should show blank for Stage =  "Reallocation"
//		query.addScalar("batchName",StandardBasicTypes.STRING);
		query.addScalar("fromUser",StandardBasicTypes.STRING);
		query.addScalar("toUser",StandardBasicTypes.STRING);
		query.addScalar("numOfQuotation",StandardBasicTypes.INTEGER);
		query.addScalar("outletCode",StandardBasicTypes.STRING);
		query.addScalar("outletId",StandardBasicTypes.INTEGER);
		query.addScalar("outletName",StandardBasicTypes.STRING);
		query.addScalar("district",StandardBasicTypes.STRING);
		query.addScalar("tpu",StandardBasicTypes.STRING);
		query.addScalar("collectionMethod",StandardBasicTypes.STRING);
		query.addScalar("recommendBy",StandardBasicTypes.STRING);
		query.addScalar("recommendDate",StandardBasicTypes.DATE);
		query.addScalar("approveBy",StandardBasicTypes.STRING);
		query.addScalar("approveDate",StandardBasicTypes.DATE);
		query.setResultTransformer(Transformers.aliasToBean(AssignmentAdjustmentReport.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentAdjustmentSummaryReport> getAssignmentReallocationSummaryReport(List<Integer> officerIds, List<String> teams
			, List<Integer> purposeIds, List<Integer> collectionMethod, List<String> allocationBatch, Date fromMonth, Date toMonth) {

		//2018-01-08 cheung_cheng Spelling mistake for "Reallocation" in "Stage" (all sheets)
		String sql = "select 'Assignment Reallocation' as stage, " + 
				"sm.ReferenceMonth as referenceMonth, " + 
				//2018-01-08 cheung_cheng  "Allocation Batch" (all sheets) should show blank for Stage =  "Reallocation"
				"fromUr.StaffCode + ' - ' + fromUr.EnglishName as fromUser, " + 
				"toUr.StaffCode + ' - ' + toUr.EnglishName as toUser, " + 
				"count(distinct qr.quotationRecordId) as numOfQuotations, " + 
				"count(distinct a.AssignmentId) as numOfAssignments, " +
				"case o.CollectionMethod when 1 then 'Field Visit' when 2 then 'Telephone' when 3 then 'Fax' when 4 then 'Other' end as collectionMethod,  " + 
				"case when aa.Status = 'Rejected' then null else reUr.StaffCode + ' - ' + reUr.EnglishName end as recommendBy, " + 
				"case when aa.Status = 'Submitted' or (aa.Status = 'Rejected' and aa.RecommendDate is null) then null else aa.RecommendDate end as recommendDate, " + 
				"case when (aa.Status = 'Approved' or aa.Status = 'Recommended') then apprUr.StaffCode + ' - ' + apprUr.EnglishName else null end as approveBy, " + 
				"case when aa.Status = 'Approved' then aa.ApprovedDate else null end as approveDate, " + 
				"case when aa.RejectReason is null or aa.RejectReason = '' then null else  concat(aa.RejectReason, ' BY ' , reasonUr.EnglishName  ,' ON ', convert(nvarchar(10), aa.ModifiedDate, 105)) end as reasons " +
				"from AssignmentReallocation aa " + 
				"left join [User] fromUr on fromUr.UserId = aa.OriginalUserId " + 
				"left join [User] toUr on toUr.UserId = aa.TargetUserId " + 
				"left join [User] reUr on reUr.UserId = aa.SubmitToUserId " + 
				"left join [User] apprUr on apprUr.UserId = aa.SubmitToApprove " + 
				"left join [User] reasonUr on aa.ModifiedBy = reasonUr.Username "+
				"inner join ReallocatedAssignment as ra on aa.AssignmentReallocationId = ra.AssignmentReallcationId " + 
				"inner join Assignment as a on a.AssignmentId = ra.AssignmentId " +
				"left join SurveyMonth as sm on a.surveyMonthId = sm.surveyMonthId " +
				"left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId and qr.IsBackNo = 0 " +
				"left join AllocationBatch as ab on qr.AllocationBatchId = ab.AllocationBatchId " + 
				"left join Outlet as o on a.OutletId = o.OutletId " + 
				"left join Tpu as t on t.TpuId = o.TpuId " + 
				"left join District as d on d.DistrictId = t.DistrictId " + 
				"left join Quotation as q on qr.QuotationId = q.QuotationId " + 
				"left join Unit as u on q.UnitId = u.UnitId " + 
				"left join Purpose as p on p.PurposeId = u.PurposeId " + 
				" where sm.ReferenceMonth between :fromMonth and :toMonth ";
					
		if(officerIds!=null && officerIds.size()>0){
			sql+= " and (fromUr.UserId in (:officerIds) or toUr.UserId in (:officerIds)) ";
		} 

		if(purposeIds!=null && purposeIds.size()>0){
			sql+= "and p.PurposeId in (:purposeIds) ";
		} 
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			sql+= "and o.CollectionMethod in (:collectionMethod) ";
		} 
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			sql+= "and ab.BatchName in (:allocationBatch) ";
		}
		
		sql+="group by sm.ReferenceMonth, fromUr.StaffCode, fromUr.EnglishName, toUr.StaffCode, toUr.EnglishName, reUr.StaffCode, reur.EnglishName,aa.RecommendDate,apprUr.StaffCode,apprUr.EnglishName,aa.ApprovedDate,o.CollectionMethod,aa.RejectReason, reasonUr.EnglishName, aa.ModifiedDate, aa.Status" +
				" union all " +
			    " select 'Quotation Allocation' as stage, " +
				"sm.ReferenceMonth as referenceMonth, " +
			    "fromUr.StaffCode + ' - ' + fromUr.EnglishName as fromUser, " + 
				"toUr.StaffCode + ' - ' + toUr.EnglishName as toUser, " + 
				"count(distinct qr.quotationRecordId) as numOfQuotations, " + 
				"count(distinct a.AssignmentId) as numOfAssignments, " +
				"case o.CollectionMethod when 1 then 'Field Visit' when 2 then 'Telephone' when 3 then 'Fax' when 4 then 'Other' end as collectionMethod,  " + 
				"case when aa.Status = 'Rejected' then null else reUr.StaffCode + ' - ' + reUr.EnglishName end as recommendBy, " + 
				"case when aa.Status = 'Submitted' or (aa.Status = 'Rejected' and aa.RecommendDate is null) then null else aa.RecommendDate end as recommendDate, " + 
				"case when (aa.Status = 'Approved' or aa.Status = 'Recommended') then apprUr.StaffCode + ' - ' + apprUr.EnglishName else null end as approveBy, " + 
				"case when aa.Status = 'Approved' then aa.ApprovedDate else null end as approveDate, " + 
				"case when aa.RejectReason is null or aa.RejectReason = '' then null else  concat(aa.RejectReason, ' BY ' , reasonUr.EnglishName  ,' ON ', convert(nvarchar(10), aa.ModifiedDate, 105)) end as reasons " +
				" from assignmentReallocation aa " +
				" left join ReallocatedQuotationRecord as rq on aa.AssignmentReallocationId = rq.AssignmentReallcationId " + 
				" left join QuotationRecord as qr on qr.QuotationRecordId = rq.QuotationRecordId " + 
				" left join Outlet as o on qr.OutletId = o.OutletId " + 
				" left join Tpu as t on t.TpuId = o.TpuId " + 
				" left join District as d on t.DistrictId = d.DistrictId " + 
				"inner join Assignment as a on a.AssignmentId = qr.AssignmentId " +
				" left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId " + 
				" left join [User] fromUr on fromUr.UserId = aa.OriginalUserId " + 
				"left join [User] toUr on toUr.UserId = aa.TargetUserId " + 
				"left join [User] reUr on reUr.UserId = aa.SubmitToUserId " + 
				"left join [User] apprUr on apprUr.UserId = aa.SubmitToApprove "+
				"left join [User] reasonUr on aa.ModifiedBy = reasonUr.Username " +
				"left join Quotation as q on qr.QuotationId = q.QuotationId " + 
				"left join Unit as u on q.UnitId = u.UnitId " + 
				"left join Purpose as p on p.PurposeId = u.PurposeId " +
				" where sm.ReferenceMonth between :fromMonth and :toMonth " ;
		
		if(officerIds!=null && officerIds.size()>0){
			sql+= " and (fromUr.UserId in (:officerIds) or toUr.UserId in (:officerIds)) ";
		} 
		
		if(purposeIds!=null && purposeIds.size()>0){
			sql+= "and p.PurposeId in (:purposeIds) ";
		} 
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			sql+= "and o.CollectionMethod in (:collectionMethod) ";
		} 
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			sql+= "and ab.BatchName in (:allocationBatch) ";
		}

		sql +=" group by sm.ReferenceMonth, fromUr.StaffCode, fromUr.EnglishName, toUr.StaffCode, toUr.EnglishName, reUr.StaffCode, reur.EnglishName,aa.RecommendDate,apprUr.StaffCode,apprUr.EnglishName,aa.ApprovedDate,o.CollectionMethod,aa.RejectReason, reasonUr.EnglishName, aa.ModifiedDate, aa.Status "
				+ " order by [referenceMonth], [fromUser], [toUser], [recommendDate], [approveDate] ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("fromMonth", fromMonth);
		query.setParameter("toMonth", toMonth);
		if(officerIds!=null && officerIds.size()>0){
			query.setParameterList("officerIds",officerIds);
		} 
		if(teams!=null && teams.size()>0){
			query.setParameterList("teams",teams);
		} 
		
		if(purposeIds!=null && purposeIds.size()>0){
			query.setParameterList("purposeIds",purposeIds);
		} 
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			query.setParameterList("collectionMethod",collectionMethod);
		} 
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			query.setParameterList("allocationBatch",allocationBatch);
		}
				
		query.addScalar("stage",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		//2018-01-08 cheung_cheng  "Allocation Batch" (all sheets) should show blank for Stage =  "Reallocation"
//		query.addScalar("batchName",StandardBasicTypes.STRING);
		query.addScalar("fromUser",StandardBasicTypes.STRING);
		query.addScalar("toUser",StandardBasicTypes.STRING);
		query.addScalar("numOfQuotations",StandardBasicTypes.INTEGER);
		query.addScalar("numOfAssignments",StandardBasicTypes.INTEGER);
		query.addScalar("collectionMethod",StandardBasicTypes.STRING);
		query.addScalar("recommendBy",StandardBasicTypes.STRING);
		query.addScalar("recommendDate",StandardBasicTypes.DATE);
		query.addScalar("approveBy",StandardBasicTypes.STRING);
		query.addScalar("approveDate",StandardBasicTypes.DATE);
		query.addScalar("reasons",StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(AssignmentAdjustmentSummaryReport.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getSubmittedRecommendedAssignmentId(){
		String hql = "Select distinct a.assignmentId"
				+ "	from AssignmentReallocation as ar"
				+ " inner join ar.assignments as a"
				+ " where ar.status in ( :status )";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("status", new String[]{SystemConstant.ASSIGNMENTREALLOCATION_STATUS_SUBMITTED, SystemConstant.ASSIGNMENTREALLOCATION_STATUS_RECOMMENDED});
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getSubmittedRecommendedQuotationRecordId(){
		String hql = "Select distinct qr.quotationRecordId"
				+ "	from AssignmentReallocation as ar"
				+ " inner join ar.quotationRecords as qr"
				+ " where ar.status in ( :status )";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("status", new String[]{SystemConstant.ASSIGNMENTREALLOCATION_STATUS_SUBMITTED, SystemConstant.ASSIGNMENTREALLOCATION_STATUS_RECOMMENDED});
		
		return query.list();
	}
	
	public List<AssignmentReallocationLookupTableList> getReallocationAssignmentList(String search, int firstRecord, int displayLength, Order order, 
			Integer assignmentReallocationId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select a.assignmentId as id "
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
				+ " from assignmentReallocation as ar "
				+ " left join reallocatedAssignment as ra on ra.AssignmentReallcationId = ar.AssignmentReallocationId "
				+ " left join assignment as a on ra.assignmentId = a.assignmentId "
				+ " left join quotationRecord as qr on qr.assignmentId = a.assignmentId "
				+ " left join quotation as q on qr.quotationId = q.quotationId "
				+ " left join batch as b on b.batchId = q.batchId "
				+ " left join outlet as o on a.outletId = o.outletId "
				+ " left join tpu as t on t.tpuId = o.tpuId "
				+ " left join district as d on d.districtId = t.districtId "
				+ " left join [user] as ou on ar.originalUserId = ou.userId "
				+ " left join [user] as tu on tu.userId = ar.targetUserId "
				+ " where ar.assignmentReallocationId = :assignmentReallocationId ";
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " ) ";
		}
		
		sql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
			+ " a.assignmentId, a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
				
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationLookupTableList.class));
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("collectionDate", StandardBasicTypes.STRING);
		query.addScalar("startDate", StandardBasicTypes.STRING);
		query.addScalar("endDate", StandardBasicTypes.STRING);
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		
		return query.list();
	}
	
	public List<AssignmentReallocationLookupTableList.BatchCodeMapping> getReallocationAssignmentBatchCodeList(String search, 
			Integer assignmentReallocationId) {
		
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select a.assignmentId as assignmentId "
				+ " , b.code as batchCode"
				+ " from assignmentReallocation as ar "
				+ " left join reallocatedAssignment as ra on ra.AssignmentReallcationId = ar.AssignmentReallocationId "
				+ " left join assignment as a on ra.assignmentId = a.assignmentId "
				+ " left join quotationRecord as qr on qr.assignmentId = a.assignmentId "
				+ " left join quotation as q on qr.quotationId = q.quotationId "
				+ " left join batch as b on b.batchId = q.batchId "
				+ " left join outlet as o on a.outletId = o.outletId "
				+ " left join tpu as t on t.tpuId = o.tpuId "
				+ " left join district as d on d.districtId = t.districtId "
				+ " left join [user] as ou on ar.originalUserId = ou.userId "
				+ " left join [user] as tu on tu.userId = ar.targetUserId "
				+ " where ar.assignmentReallocationId = :assignmentReallocationId ";
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " ) ";
		}
		
		sql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
			+ " a.assignmentId, b.code";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
				
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentReallocationLookupTableList.BatchCodeMapping.class));
		
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("batchCode", StandardBasicTypes.STRING);
		
		return query.list();
	}
	
	public long countReallocationAssignmentList(String search, Integer assignmentReallocationId){
		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(a.startDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select a.assignmentId as id "
				+ " from assignmentReallocation as ar "
				+ " left join reallocatedAssignment as ra on ra.AssignmentReallcationId = ar.AssignmentReallocationId "
				+ " left join assignment as a on ra.assignmentId = a.assignmentId "
				+ " left join quotationRecord as qr on qr.assignmentId = a.assignmentId "
				+ " left join quotation as q on qr.quotationId = q.quotationId "
				+ " left join batch as b on b.batchId = q.batchId "
				+ " left join outlet as o on a.outletId = o.outletId "
				+ " left join tpu as t on t.tpuId = o.tpuId "
				+ " left join district as d on d.districtId = t.districtId "
				+ " left join [user] as ou on ar.originalUserId = ou.userId "
				+ " left join [user] as tu on tu.userId = ar.targetUserId "
				+ " where ar.assignmentReallocationId = :assignmentReallocationId ";
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search or d.chineseName like :search or t.code like :search or b.code like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.startDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.endDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " ) ";
		}
		
		sql += " and a.isCompleted = 0 "
			+ " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
			+ " a.assignmentId, a.collectionDate, a.startDate, a.endDate "
			+ ", o.name, d.chineseName, t.code ";
		
		String countSql = "Select count(*) from ( "+sql+" ) as cnt";
		
		SQLQuery query = this.getSession().createSQLQuery(countSql);
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
				
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public List<QuotationRecordReallocationLookupTableList> getReallocationQuotationRecordList(String search
			, int firstRecord, int displayLength, Order order, Integer assignmentReallocationId) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(qr.assignedStartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(qr.assignedEndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select distinct qr.quotationRecordId as id "
				+ ", case "
				+ "	when qr.collectionDate is null then '' "
				+ " else " + collectionDate + " end  as collectionDate "
				+ ", case "
				+ "	when qr.assignedStartDate is null then '' "
				+ " else " + startDate + " end  as startDate "
				+ ", case "
				+ "	when qr.assignedEndDate is null then '' "
				+ " else " + endDate + " end  as endDate "
				+ ", o.name as firm "
				+ ", d.chineseName as district "
				+ ", t.code as tpu "
				+ ", b.code as batchCode "
				+ ", u.displayName as displayName "
				+ ", u.unitCategory as category "
				+ ", qr.status as quotationStatus "
				+ " from assignmentReallocation as ar "
				+ " left join reallocatedQuotationRecord as rq on rq.AssignmentReallcationId = ar.AssignmentReallocationId "
				+ " left join quotationRecord as qr on qr.quotationRecordId = rq.quotationRecordId "
				+ " left join quotation as q on qr.quotationId = q.quotationId "
				+ " left join batch as b on b.batchId = q.batchId "
				+ " left join outlet as o on qr.outletId = o.outletId "
				+ " left join tpu as t on t.tpuId = o.tpuId "
				+ " left join district as d on d.districtId = t.districtId "
				+ " left join [user] as ou on ar.originalUserId = ou.userId "
				+ " left join [user] as tu on tu.userId = ar.targetUserId "
				+ " left join unit as u on u.unitId = q.unitId "
				+ " where ar.assignmentReallocationId = :assignmentReallocationId ";
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search "
					+ " or d.chineseName like :search "
					+ " or t.code like :search "
					+ " or b.code like :search "
					+ " or u.displayName like :search "
					+ " or u.unitCategory like :search "
					+ " or qr.status like :search "
				+ " or ( "
				+ "   case "
				+ "   when qr.assignedStartDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when qr.assignedEndDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when qr.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " ) ";
		}
		
		sql += " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
			+ " qr.quotationRecordId, qr.collectionDate, qr.assignedStartDate, qr.assignedEndDate "
			+ " , o.name, d.chineseName, t.code, b.code, u.displayName, u.unitCategory "
			+ " , qr.status ";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
				
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordReallocationLookupTableList.class));
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("collectionDate", StandardBasicTypes.STRING);
		query.addScalar("startDate", StandardBasicTypes.STRING);
		query.addScalar("endDate", StandardBasicTypes.STRING);
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("batchCode", StandardBasicTypes.STRING);
		query.addScalar("displayName", StandardBasicTypes.STRING);
		query.addScalar("category", StandardBasicTypes.STRING);
		query.addScalar("quotationStatus", StandardBasicTypes.STRING);
		
		return query.list();
	}
	
	public long countReallocationQuotationRecordList(String search, Integer assignmentReallocationId) {
		
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String startDate = String.format("FORMAT(qr.assignedStartDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(qr.assignedEndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = " select distinct qr.quotationRecordId as id "
				+ " from assignmentReallocation as ar "
				+ " left join reallocatedQuotationRecord as rq on rq.AssignmentReallcationId = ar.AssignmentReallocationId "
				+ " left join quotationRecord as qr on qr.quotationRecordId = rq.quotationRecordId "
				+ " left join quotation as q on qr.quotationId = q.quotationId "
				+ " left join batch as b on b.batchId = q.batchId "
				+ " left join outlet as o on qr.outletId = o.outletId "
				+ " left join tpu as t on t.tpuId = o.tpuId "
				+ " left join district as d on d.districtId = t.districtId "
				+ " left join [user] as ou on ar.originalUserId = ou.userId "
				+ " left join [user] as tu on tu.userId = ar.targetUserId "
				+ " left join unit as u on u.unitId = q.unitId "
				+ " where ar.assignmentReallocationId = :assignmentReallocationId ";
		
		if (!StringUtils.isEmpty(search)) {
			sql += " and  ( o.name like :search "
					+ " or d.chineseName like :search "
					+ " or t.code like :search "
					+ " or b.code like :search "
					+ " or u.displayName like :search "
					+ " or u.unitCategory like :search "
					+ " or qr.status like :search "
				+ " or ( "
				+ "   case "
				+ "   when qr.assignedStartDate is null then '' "
				+ "   else " + startDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when qr.assignedEndDate is null then '' "
				+ "   else " + endDate + " end ) like :search "
				+ " or ( "
				+ "   case "
				+ "   when qr.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " ) ";
		}
		
		sql += " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		sql += " group by "
			+ " qr.quotationRecordId, qr.collectionDate, qr.assignedStartDate, qr.assignedEndDate "
			+ " , o.name, d.chineseName, t.code, b.code, u.displayName, u.unitCategory "
			+ " , qr.status ";
		
		String countSQL = "Select count(*) from ( "+sql+" ) as cnt";
		
		SQLQuery query = this.getSession().createSQLQuery(countSQL);
		
		
		query.setParameter("assignmentReallocationId", assignmentReallocationId);
				
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}
}
