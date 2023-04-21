package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.AssignmentAdjustment;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.assignemtAllocation.adjustmentAllocationTab.AdjustmentModel;
import capi.model.assignmentAllocationAndReallocation.assignmentTransferInOutMaintenance.TableList;
import capi.model.report.AssignmentAdjustmentReport;
import capi.model.report.AssignmentAdjustmentSummaryReport;

@Repository("AssignmentAdjustmentDao")
public class AssignmentAdjustmentDao extends GenericDao<AssignmentAdjustment>{

	@SuppressWarnings("unchecked")
	public List<AssignmentAdjustment> getAssignmentAdjustmentInMonth(Date month){
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.eq("referenceMonth", month));		
		return criteria.list();
	}
	
	public long countAssignmentAdjustmentInMonth(Date month){
		Criteria criteria = this.createCriteria();		
		criteria.add(Restrictions.eq("referenceMonth", month));		
		return (long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<TableList> getTableList(String search,
			int firstRecord, int displayLength, Order order,
			Date referenceMonth, Integer fromUserId, Integer fromUserPermissionId, List<Integer> supervisorIds,
			String[] statuses) {

		String hql = "select a.id as id, "
				+ " concat(fromUser.staffCode, ' - ', "
				+ "   case when fromUser.chineseName is null then fromUser.englishName else fromUser.chineseName end "
				+ ") as fromFieldOfficer, "
				+ " concat(toUser.staffCode, ' - ', "
				+ "  case when toUser.chineseName is null then toUser.englishName else toUser.chineseName end "
				+ ") as targetFieldOfficer, "
				+ " a.manDay as targetReleaseManDay,"
				+ " a.actualManDay as actualReleaseManDay,"
				+ " a.status as status "
                + " from AssignmentAdjustment as a "
                + " inner join a.allocationBatch as batch "
                + " left join a.fromUser as fromUser "
                + " left join a.toUser as toUser "
                + " left join fromUser.supervisor as supervisor "
                + " where 1 = 1 ";
		
		hql += " and a.status in :statuses and batch.status = 2 ";
		
		if (referenceMonth != null) {
			hql += " and a.referenceMonth = :referenceMonth ";
		}
		if (fromUserId != null) {
			hql += " and fromUser.id = :fromUserId ";
		}
		if (fromUserPermissionId != null) {
			hql += " and fromUser.id = :fromUserPermissionId ";
		}
		if (supervisorIds != null && supervisorIds.size() > 0) {
			hql += " and supervisor.id in :supervisorIds ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
				+ " fromUser.staffCode like :search or fromUser.chineseName like :search or "
				+ " toUser.staffCode like :search or toUser.chineseName like :search or "
				+ " a.status like :search "
                + " ) ";
		}

		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameterList("statuses", statuses);
		
		if (referenceMonth != null) {
			query.setParameter("referenceMonth", referenceMonth);
		}

		if (fromUserId != null) {
			query.setParameter("fromUserId", fromUserId);
		}
		if (fromUserPermissionId != null) {
			query.setParameter("fromUserPermissionId", fromUserPermissionId);
		}
		if (supervisorIds != null && supervisorIds.size() > 0) {
			query.setParameterList("supervisorIds", supervisorIds);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(TableList.class));

		return query.list();
	}

	public long countTableList(String search,
			Date referenceMonth, Integer fromUserId, Integer fromUserPermissionId, List<Integer> supervisorIds,
			String[] statuses) {

		String referenceMonthFormat = String.format("FORMAT(a.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select count(distinct a.id) "
                + " from AssignmentAdjustment as a "
                + " left join a.fromUser as fromUser "
                + " left join a.toUser as toUser "
                + " left join fromUser.supervisor as supervisor "
                + " where 1 = 1 ";

		hql += " and a.status in :statuses";
		
		if (referenceMonth != null) {
			hql += " and " + referenceMonthFormat + " = :referenceMonth ";
		}
		if (fromUserId != null) {
			hql += " and fromUser.id = :fromUserId ";
		}
		if (fromUserPermissionId != null) {
			hql += " and fromUser.id = :fromUserPermissionId ";
		}
		if (supervisorIds != null && supervisorIds.size() > 0) {
			hql += " and supervisor.id in :supervisorIds ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
				+ " fromUser.staffCode like :search or fromUser.chineseName like :search or "
				+ " toUser.staffCode like :search or toUser.chineseName like :search or "
				+ " a.status like :search "
                + " ) ";
		}

		Query query = getSession().createQuery(hql);

		query.setParameterList("statuses", statuses);

		if (referenceMonth != null) {
			query.setParameter("referenceMonth", referenceMonth);
		}

		if (fromUserId != null) {
			query.setParameter("fromUserId", fromUserId);
		}
		if (fromUserPermissionId != null) {
			query.setParameter("fromUserPermissionId", fromUserPermissionId);
		}
		if (supervisorIds != null && supervisorIds.size() > 0) {
			query.setParameterList("supervisorIds", supervisorIds);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<TableList> getTableListForRecommend(String search,
			int firstRecord, int displayLength, Order order,
			Date referenceMonth, Integer fromUserId, List<Integer> submitToRecommendIds,
			String[] statuses) {

		String hql = "select a.id as id, "
				+ " concat(fromUser.staffCode, ' - ', "
				+ " case when fromUser.chineseName is null then fromUser.englishName else fromUser.chineseName end "
				+ " ) as fromFieldOfficer, "
				+ " concat(toUser.staffCode, ' - ', "
				+ " case when toUser.chineseName is null then toUser.englishName else toUser.chineseName end "
				+ ") as targetFieldOfficer, "
				+ " a.manDay as targetReleaseManDay,"
				+ " a.actualManDay as actualReleaseManDay,"
				+ " a.status as status "
                + " from AssignmentAdjustment as a "
                + " left join a.fromUser as fromUser "
                + " left join a.toUser as toUser "
                + " where 1 = 1 ";
		
		hql += " and a.status in :statuses";
		
		if (referenceMonth != null) {
			hql += " and a.referenceMonth = :referenceMonth ";
		}
		if (fromUserId != null) {
			hql += " and fromUser.id = :fromUserId ";
		}
		if (submitToRecommendIds != null && submitToRecommendIds.size() > 0) {
			hql += " and a.submitToRecommend.id in :submitToRecommendIds ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
				+ " fromUser.staffCode like :search or fromUser.chineseName like :search or "
				+ " toUser.staffCode like :search or toUser.chineseName like :search or "
				+ " a.status like :search "
                + " ) ";
		}

		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameterList("statuses", statuses);
		
		if (referenceMonth != null) {
			query.setParameter("referenceMonth", referenceMonth);
		}

		if (fromUserId != null) {
			query.setParameter("fromUserId", fromUserId);
		}
		if (submitToRecommendIds != null && submitToRecommendIds.size() > 0) {
			query.setParameterList("submitToRecommendIds", submitToRecommendIds);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(TableList.class));

		return query.list();
	}

	public long countTableListForRecommend(String search,
			Date referenceMonth, Integer fromUserId, List<Integer> submitToRecommendIds,
			String[] statuses) {

		String referenceMonthFormat = String.format("FORMAT(a.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select count(distinct a.id) "
                + " from AssignmentAdjustment as a "
                + " left join a.fromUser as fromUser "
                + " left join a.toUser as toUser "
                + " where 1 = 1 ";

		hql += " and a.status in :statuses";
		
		if (referenceMonth != null) {
			hql += " and " + referenceMonthFormat + " = :referenceMonth ";
		}
		if (fromUserId != null) {
			hql += " and fromUser.id = :fromUserId ";
		}
		if (submitToRecommendIds != null && submitToRecommendIds.size() > 0) {
			hql += " and a.submitToRecommend.id in :submitToRecommendIds ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
				+ " fromUser.staffCode like :search or fromUser.chineseName like :search or "
				+ " toUser.staffCode like :search or toUser.chineseName like :search or "
				+ " a.status like :search "
                + " ) ";
		}

		Query query = getSession().createQuery(hql);

		query.setParameterList("statuses", statuses);

		if (referenceMonth != null) {
			query.setParameter("referenceMonth", referenceMonth);
		}

		if (fromUserId != null) {
			query.setParameter("fromUserId", fromUserId);
		}
		if (submitToRecommendIds != null && submitToRecommendIds.size() > 0) {
			query.setParameterList("submitToRecommendIds", submitToRecommendIds);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<TableList> getTableListForApproval(String search,
			int firstRecord, int displayLength, Order order,
			Date referenceMonth, Integer fromUserId, List<Integer> submitToUserIds,
			String[] statuses) {

		String hql = "select a.id as id, "
				+ " concat(fromUser.staffCode, ' - ', "
				+ "  case when fromUser.chineseName is null then fromUser.englishName else fromUser.chineseName end "
				+ " ) as fromFieldOfficer, "
				+ " concat(toUser.staffCode, ' - ', "
				+ "	 case when toUser.chineseName is null then toUser.englishName else toUser.chineseName end  "
				+ " ) as targetFieldOfficer, "
				+ " a.manDay as targetReleaseManDay,"
				+ " a.actualManDay as actualReleaseManDay,"
				+ " a.status as status "
                + " from AssignmentAdjustment as a "
                + " left join a.fromUser as fromUser "
                + " left join a.toUser as toUser "
                + " where 1 = 1 ";
		
		hql += " and a.status in :statuses";
		
		if (referenceMonth != null) {
			hql += " and a.referenceMonth = :referenceMonth ";
		}
		if (fromUserId != null) {
			hql += " and fromUser.id = :fromUserId ";
		}
		if (submitToUserIds != null && submitToUserIds.size() > 0) {
			hql += " and a.submitToApprove.id in :submitToUserIds ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
				+ " fromUser.staffCode like :search or fromUser.chineseName like :search or "
				+ " toUser.staffCode like :search or toUser.chineseName like :search or "
				+ " str(a.manDay) like :search or str(a.actualReleaseManDay) like :search or "
				+ " a.status like :search "
                + " ) ";
		}

		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameterList("statuses", statuses);
		
		if (referenceMonth != null) {
			query.setParameter("referenceMonth", referenceMonth);
		}

		if (fromUserId != null) {
			query.setParameter("fromUserId", fromUserId);
		}
		if (submitToUserIds != null && submitToUserIds.size() > 0) {
			query.setParameterList("submitToUserIds", submitToUserIds);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		query.setResultTransformer(Transformers.aliasToBean(TableList.class));

		return query.list();
	}

	public long countTableListForApproval(String search,
			Date referenceMonth, Integer fromUserId, List<Integer> submitToUserIds,
			String[] statuses) {

		String referenceMonthFormat = String.format("FORMAT(a.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select count(distinct a.id) "
                + " from AssignmentAdjustment as a "
                + " left join a.fromUser as fromUser "
                + " left join a.toUser as toUser "
                + " where 1 = 1 ";

		hql += " and a.status in :statuses";
		
		if (referenceMonth != null) {
			hql += " and " + referenceMonthFormat + " = :referenceMonth ";
		}
		if (fromUserId != null) {
			hql += " and fromUser.id = :fromUserId ";
		}
		if (submitToUserIds != null && submitToUserIds.size() > 0) {
			hql += " and a.submitToApprove.id in :submitToUserIds ";
		}
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
				+ " fromUser.staffCode like :search or fromUser.chineseName like :search or "
				+ " toUser.staffCode like :search or toUser.chineseName like :search or "
				+ " str(a.manDay) like :search or str(a.actualReleaseManDay) like :search or "
				+ " a.status like :search "
                + " ) ";
		}

		Query query = getSession().createQuery(hql);

		query.setParameterList("statuses", statuses);

		if (referenceMonth != null) {
			query.setParameter("referenceMonth", referenceMonth);
		}

		if (fromUserId != null) {
			query.setParameter("fromUserId", fromUserId);
		}
		if (submitToUserIds != null && submitToUserIds.size() > 0) {
			query.setParameterList("submitToUserIds", submitToUserIds);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	

	public long countQuotations(int id) {
		String hql = "select count(distinct qr.id) "
                + " from AssignmentAdjustment as a "
                + " left join a.assignments as assignment "
                + " left join assignment.quotationRecords as qr"
                + " where a.id = :id ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("id", id);
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public long countAssignments(int id) {
		String hql = "select count(distinct assignment.id) "
                + " from AssignmentAdjustment as a "
                + " left join a.assignments as assignment "
                + " where a.id = :id ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("id", id);
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	

	public List<AssignmentAdjustment> findAssignmentAdjustmentByAllocationBatchIds(List<Integer> allocationBatchIds){
		if(allocationBatchIds.size() > 0){
			Criteria criteria = this.createCriteria("aa")
					.createAlias("aa.allocationBatch", "ab", JoinType.LEFT_OUTER_JOIN);
			
			criteria.add(Restrictions.in("ab.allocationBatchId", allocationBatchIds.toArray()));
			
			return criteria.list();
		}else{
			return new ArrayList<AssignmentAdjustment>();
		}
		
	}
	
	public List<AdjustmentModel> getAssignmentAdjustmentView(Integer allocationBatchId){
		String hql = "select fromUser.userId as fromUserId, "
				+ " concat(fromUser.staffCode, ' - ', "
				+ " case when fromUser.chineseName is null then fromUser.englishName else fromUser.chineseName end , "
				+ "' (', case when fromUser.destination is null then '' else fromUser.destination end , ')') as fromUserName, "
				+ " toUser.userId as toUserId, "
				+ " concat(toUser.staffCode, ' - ', "
				+ " case when toUser.chineseName is null then toUser.englishName else toUser.chineseName end , "
				+ " ' (', case when toUser.destination is null then '' else toUser.destination end, ')') as toUserName, "
				+ " a.manDay as manDay, "
				+ " a.remark as remark "
                + " from AssignmentAdjustment as a"
                + "	left join a.fromUser as fromUser"
                + " left join a.toUser as toUser"
                + " left join a.allocationBatch as allocationBatch "
                + " where allocationBatch.allocationBatchId = :id ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("id", allocationBatchId);
		
		query.setResultTransformer(Transformers.aliasToBean(AdjustmentModel.class));
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<AssignmentAdjustmentReport> getAssignmentAdjustmentReport(List<Integer> officerIds, List<String> teams
			, List<Integer> purposeIds, List<Integer> collectionMethod, List<String> allocationBatch, Date fromMonth, Date toMonth) {
		
		String sql = "select 'Transfer in/out' as stage, " + 
				"aa.ReferenceMonth as referenceMonth, " + 
				"ab.BatchName as batchName, " + 
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
				"from AssignmentAdjustment aa " + 
				"left join [User] fromUr on fromUr.UserId = aa.FromUserId " + 
				"left join [User] toUr on toUr.UserId = aa.ToUserId " + 
				"left join [User] reUr on reUr.UserId = aa.SubmitToRecommend " + 
				"left join [User] apprUr on apprUr.UserId = aa.SubmitToApprove " + 
				"inner join ReleasedAssignment as ra on aa.AssignmentAdjustmentId = ra.AssignmentAdjustmentId " + 
				"inner join Assignment as a on a.AssignmentId = ra.AssignmentId " + 
				"left join AllocationBatch as ab on aa.AllocationBatchId = ab.AllocationBatchId " + 
				"left join Outlet as o on a.OutletId = o.OutletId " + 
				"left join Tpu as t on t.TpuId = o.TpuId " + 
				"left join District as d on d.DistrictId = t.DistrictId " + 
				"left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId and qr.IsBackNo = 0 " + 
				"left join Quotation as q on qr.QuotationId = q.QuotationId " + 
				"left join Unit as u on q.UnitId = u.UnitId " + 
				"left join Purpose as p on p.PurposeId = u.PurposeId " + 
				" where aa.ReferenceMonth between :fromMonth and :toMonth ";
					
		if(officerIds!=null && officerIds.size()>0){
			sql+= "and (fromUr.UserId in (:officerIds) or toUr.UserId in (:officerIds)) ";
		} 
//		if(teams!=null && teams.size()>0){
//			sql+= "and fromUr.Team in (:teams) ";
//		} 
		
		if(purposeIds!=null && purposeIds.size()>0){
			sql+= "and p.PurposeId in (:purposeIds) ";
		} 
		
		if(collectionMethod!=null && collectionMethod.size()>0){
			sql+= "and o.CollectionMethod in (:collectionMethod) ";
		} 
		
		if(allocationBatch!=null && allocationBatch.size()>0){
			sql+= "and ab.BatchName in (:allocationBatch) ";
		}
		
		sql +=" group by aa.ReferenceMonth, ab.BatchName, fromUr.StaffCode, fromUr.EnglishName, toUr.StaffCode, toUr.EnglishName, o.FirmCode, d.Code , t.Code , o.Name , reUr.StaffCode, reur.EnglishName,aa.RecommendDate,apprUr.StaffCode,apprUr.EnglishName,aa.ApprovedDate,o.CollectionMethod, aa.Status, o.OutletId "
				+ " order by aa.ReferenceMonth, ab.BatchName, fromUr.StaffCode, toUr.StaffCode, d.Code, o.FirmCode, aa.RecommendDate, aa.ApprovedDate ";
		
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
		query.addScalar("batchName",StandardBasicTypes.STRING);
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
	public List<AssignmentAdjustmentSummaryReport> getAssignmentAdjustmentSummaryReport(List<Integer> officerIds, List<String> teams
			, List<Integer> purposeIds, List<Integer> collectionMethod, List<String> allocationBatch, Date fromMonth, Date toMonth) {
		
		String sql = "select 'Transfer in/out' as stage, " + 
				"aa.ReferenceMonth as referenceMonth, " + 
				"ab.BatchName as batchName, " + 
				"fromUr.StaffCode + ' - ' + fromUr.EnglishName as fromUser, " + 
				"toUr.StaffCode + ' - ' + toUr.EnglishName as toUser, " + 
				"count(distinct qr.quotationRecordId) as numOfQuotations, " + 
				"count(distinct a.AssignmentId) as numOfAssignments, " +
				"case o.CollectionMethod when 1 then 'Field Visit' when 2 then 'Telephone' when 3 then 'Fax' when 4 then 'Other' end as collectionMethod,  " + 
				"case when aa.Status = 'Rejected' then null else reUr.StaffCode + ' - ' + reUr.EnglishName end as recommendBy, " + 
				"case when aa.Status = 'Submitted' or (aa.Status = 'Rejected' and aa.RecommendDate is null) then null else aa.RecommendDate end as recommendDate, " + 
				"case when (aa.Status = 'Approved' or aa.Status = 'Recommended') then apprUr.StaffCode + ' - ' + apprUr.EnglishName else null end as approveBy, " + 
				"case when aa.Status = 'Approved' then aa.ApprovedDate else null end as approveDate, " + 
				"case when aa.RejectReason is null or aa.RejectReason = '' then null else aa.RejectReason end as reasons " +
				"from AssignmentAdjustment aa " + 
				"left join [User] fromUr on fromUr.UserId = aa.FromUserId " + 
				"left join [User] toUr on toUr.UserId = aa.ToUserId " + 
				"left join [User] reUr on reUr.UserId = aa.SubmitToRecommend " + 
				"left join [User] apprUr on apprUr.UserId = aa.SubmitToApprove " + 
				"inner join ReleasedAssignment as ra on aa.AssignmentAdjustmentId = ra.AssignmentAdjustmentId " + 
				"inner join Assignment as a on a.AssignmentId = ra.AssignmentId " + 
				"left join AllocationBatch as ab on aa.AllocationBatchId = ab.AllocationBatchId " + 
				"left join Outlet as o on a.OutletId = o.OutletId " + 
				"left join Tpu as t on t.TpuId = o.TpuId " + 
				"left join District as d on d.DistrictId = t.DistrictId " + 
				"left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId and qr.IsBackNo = 0 " + 
				"left join Quotation as q on qr.QuotationId = q.QuotationId " + 
				"left join Unit as u on q.UnitId = u.UnitId " + 
				"left join Purpose as p on p.PurposeId = u.PurposeId " + 
				" where aa.ReferenceMonth between :fromMonth and :toMonth ";
					
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
		
		sql +=" group by aa.ReferenceMonth, ab.BatchName, fromUr.StaffCode, fromUr.EnglishName, toUr.StaffCode, toUr.EnglishName, reUr.StaffCode, reur.EnglishName,aa.RecommendDate,apprUr.StaffCode,apprUr.EnglishName,aa.ApprovedDate,o.CollectionMethod,aa.RejectReason, aa.Status "
				+ " order by aa.ReferenceMonth, ab.BatchName, fromUr.StaffCode, toUr.StaffCode, aa.RecommendDate, aa.ApprovedDate ";
		
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
		query.addScalar("batchName",StandardBasicTypes.STRING);
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
	public List<AssignmentAdjustment> getAssignmentAdjustmentByAllocationBatchId(Integer allocationBatchId) {
		Criteria criteria = this.createCriteria("aa")
								.createAlias("aa.allocationBatch", "ab", JoinType.LEFT_OUTER_JOIN)
								.add(Restrictions.eq("ab.allocationBatchId", allocationBatchId));
		
		return criteria.list();
	}
	
}
