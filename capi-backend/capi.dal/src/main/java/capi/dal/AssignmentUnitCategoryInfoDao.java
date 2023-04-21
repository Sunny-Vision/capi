package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.AssignmentUnitCategoryInfo;
import capi.model.SystemConstant;
import capi.model.api.dataSync.AssignmentUnitCategoryInfoSyncData;
import capi.model.assignmentManagement.assignmentManagement.AssignmentUnitCategoryInfoWithVerify;

@Repository("AssignmentUnitCategoryInfoDao")
public class AssignmentUnitCategoryInfoDao extends GenericDao<AssignmentUnitCategoryInfo> {
	public AssignmentUnitCategoryInfo getUnitCategoryInfosByQuotationRecordId(int id) {
		String hql = " select i.assignmentUnitCategoryInfoId as assignmentUnitCategoryInfoId, "
				+ " i.contactPerson as contactPerson, i.remark as remark, i.unitCategory as unitCategory, i.sequence as sequence "
				+ " from QuotationRecord as qr "
				+ " inner join qr.assignment as a "
				+ " inner join a.categoryInfo as i "
				+ " where 1 = 1 ";
		
		hql += " and i.unitCategory = qr.quotation.unit.unitCategory "
				+ " and qr.id = :id ";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("id", id);
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfo.class));
		
		return (AssignmentUnitCategoryInfo)query.uniqueResult();
	}
	
	public AssignmentUnitCategoryInfo findByIdWithRelated(int id) {
		Criteria c = this.createCriteria().add(Restrictions.eq("id", id));
		c.setFetchMode("outlet", FetchMode.JOIN);
		return (AssignmentUnitCategoryInfo)c.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentUnitCategoryInfoWithVerify> getAllForVerify(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, Integer verificationType, String unitCategory) {
		String hql = "select cat.id as assignmentUnitCategoryInfoId, cat.contactPerson as contactPerson, cat.remark as remark, "
				+ " cat.unitCategory as unitCategory, "
				+ " max(case when r.verifyFirm = true then 1 else 0 end) as verifyFirm, "
				+ " max(case when r.verifyCategory = true then 1 else 0 end) as verifyCategory, "
				+ " max(case when r.verifyQuotation = true then 1 else 0 end) as verifyQuotation "
                + " from AssignmentUnitCategoryInfo as cat "
                + " left join cat.assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and cat.unitCategory = unit.unitCategory ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = 'Verify' ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (verificationType != null) {
			if (verificationType == 1) {
				hql += " and r.verifyCategory = true ";
			} else if (verificationType == 2) {
				hql += " and r.verifyFirm = true ";
			} else if (verificationType == 3) {
				hql += " and r.verifyQuotation = true ";
			}
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		hql += " group by cat.id, cat.contactPerson, cat.remark, "
				+ " cat.unitCategory ";
		hql += " order by cat.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", dateSelectedAssignmentId);
		query.setParameter("userId", userId);

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoWithVerify.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentUnitCategoryInfoWithVerify> getAllForRevisit(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) {
		String hql = "select cat.id as assignmentUnitCategoryInfoId, cat.contactPerson as contactPerson, cat.remark as remark, "
				+ " cat.unitCategory as unitCategory "
                + " from AssignmentUnitCategoryInfo as cat "
                + " left join cat.assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and cat.unitCategory = unit.unitCategory ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = 'Revisit' ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		hql += " group by cat.id, cat.contactPerson, cat.remark, "
				+ " cat.unitCategory ";
		hql += " order by cat.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", dateSelectedAssignmentId);
		query.setParameter("userId", userId);

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoWithVerify.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentUnitCategoryInfoWithVerify> getAllForIP(Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) {
		String hql = "select cat.id as assignmentUnitCategoryInfoId, cat.contactPerson as contactPerson, cat.remark as remark, "
				+ " cat.unitCategory as unitCategory "
                + " from AssignmentUnitCategoryInfo as cat "
                + " left join cat.assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and cat.unitCategory = unit.unitCategory ";
		
		hql += " and (r.quotationState = 'IP') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		hql += " group by cat.id, cat.contactPerson, cat.remark, "
				+ " cat.unitCategory ";
		hql += " order by cat.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", dateSelectedAssignmentId);
		query.setParameter("userId", userId);

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoWithVerify.class));
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<AssignmentUnitCategoryInfoWithVerify> getAllForNormal(int assignmentId) {
		return this.createCriteria()
				.setProjection(Projections.projectionList()
						.add(Projections.property("assignmentUnitCategoryInfoId"), "assignmentUnitCategoryInfoId")
						.add(Projections.property("contactPerson"), "contactPerson")
						.add(Projections.property("remark"), "remark")
						.add(Projections.property("unitCategory"), "unitCategory")
						.add(Projections.property("sequence"), "sequence"))
				.add(Restrictions.eq("assignment.id", assignmentId))
				.addOrder(Order.asc("sequence"))
				.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoWithVerify.class))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<AssignmentUnitCategoryInfoWithVerify> getAllForNewRecruitmentNormal(Integer assignmentId, Integer userId, String consignmentCounter, String unitCategory) {
		String hql = "select cat.id as assignmentUnitCategoryInfoId, cat.contactPerson as contactPerson, cat.remark as remark, "
				+ " cat.unitCategory as unitCategory "
                + " from AssignmentUnitCategoryInfo as cat "
                + " left join cat.assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and cat.unitCategory = unit.unitCategory ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = 'Normal' ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		hql += " group by cat.id, cat.contactPerson, cat.remark, "
				+ " cat.unitCategory ";
		hql += " order by cat.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoWithVerify.class));
		
		return query.list();
	}
	

	@SuppressWarnings("unchecked")
	public List<AssignmentUnitCategoryInfoWithVerify> getAllForPEView(Integer assignmentId, String consignmentCounter, String unitCategory) {
		String hql = "select cat.id as assignmentUnitCategoryInfoId, cat.contactPerson as contactPerson, cat.remark as remark, "
				+ " cat.unitCategory as unitCategory "
                + " from AssignmentUnitCategoryInfo as cat "
                + " left join cat.assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and cat.unitCategory = unit.unitCategory ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = 'Normal' ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		hql += " group by cat.id, cat.contactPerson, cat.remark, "
				+ " cat.unitCategory ";
		hql += " order by cat.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoWithVerify.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentUnitCategoryInfoWithVerify> getAllForRUACaseApproval(Integer assignmentId, String consignmentCounter, String unitCategory) {
		String hql = "select cat.id as assignmentUnitCategoryInfoId, cat.contactPerson as contactPerson, cat.remark as remark, "
				+ " cat.unitCategory as unitCategory "
                + " from AssignmentUnitCategoryInfo as cat "
                + " left join cat.assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and cat.unitCategory = unit.unitCategory ";
		
		hql += " and r.status = 'Submitted' ";
		
		hql += " and r.availability = 5 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		hql += " group by cat.id, cat.contactPerson, cat.remark, "
				+ " cat.unitCategory ";
		hql += " order by cat.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoWithVerify.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AssignmentUnitCategoryInfoWithVerify> getAllForNewRecruitmentApproval(Integer assignmentId, String consignmentCounter, String unitCategory) {
		String hql = "select cat.id as assignmentUnitCategoryInfoId, cat.contactPerson as contactPerson, cat.remark as remark, "
				+ " cat.unitCategory as unitCategory "
                + " from AssignmentUnitCategoryInfo as cat "
                + " left join cat.assignment as a "
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and cat.unitCategory = unit.unitCategory ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		hql += " group by cat.id, cat.contactPerson, cat.remark, "
				+ " cat.unitCategory ";
		hql += " order by cat.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoWithVerify.class));
		
		return query.list();
	}

	public List<AssignmentUnitCategoryInfoSyncData> getUpdatedAssignmentUnitCategoryInfo(Date lastSyncTime, Integer[] assignmentIds, Integer[] assignmentUnitCategoryInfoIds){
		String hql = "select au.assignmentUnitCategoryInfoId as assignmentUnitCategoryInfoId"
				+ ", o.outletId as outletId, au.contactPerson as contactPerson, au.remark as remark"
				+ ", au.unitCategory as unitCategory, au.createdDate as createdDate, au.modifiedDate as modifiedDate"
				+ ", au.sequence as sequence, a.assignmentId as assignmentId"
				+ " from AssignmentUnitCategoryInfo as au"
				+ " left join au.outlet as o"
				+ " left join au.assignment as a"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and au.modifiedDate >= :modifiedDate";
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			hql += " and a.assignmentId in ( :assignmentId )";
		}
		
		if(assignmentUnitCategoryInfoIds!=null && assignmentUnitCategoryInfoIds.length>0){
			hql += " and au.assignmentUnitCategoryInfoId in ( :assignmentUnitCategoryInfoIds )";
		}
		
		Query query = getSession().createQuery(hql);
		
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			query.setParameterList("assignmentId", assignmentIds);
		}
		
		if(assignmentUnitCategoryInfoIds!=null && assignmentUnitCategoryInfoIds.length>0){
			query.setParameterList("assignmentUnitCategoryInfoIds", assignmentUnitCategoryInfoIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoSyncData.class));
		return query.list();
		
	}
	
	public AssignmentUnitCategoryInfo findByUnitCategoryAndAssignment(String unitCategory, int assignmentId){
		return (AssignmentUnitCategoryInfo)this.createCriteria()
					.add(Restrictions.eq("unitCategory", unitCategory))
					.add(Restrictions.eq("assignment.assignmentId", assignmentId))
					.setMaxResults(1)
					.uniqueResult();

	}
	

	public List<AssignmentUnitCategoryInfoSyncData> getHistoryAssignmentUnitCategoryInfoByAssignmentIds(
			List<Integer> assignmentIds) {
		String query1 = "SELECT DISTINCT au.* FROM QuotationRecord qr, AssignmentUnitCategoryInfo au, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.assignmentId = au.assignmentId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<AssignmentUnitCategoryInfoSyncData> result1 = addScalarForAssignmentUnitCategoryInfo(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		String query2 = "SELECT DISTINCT au.* FROM QuotationRecord qr, AssignmentUnitCategoryInfo au, "
				+ SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.assignmentId = au.assignmentId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<AssignmentUnitCategoryInfoSyncData> result2 = addScalarForAssignmentUnitCategoryInfo(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		List<AssignmentUnitCategoryInfoSyncData> returnResult = new ArrayList<AssignmentUnitCategoryInfoSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public List<AssignmentUnitCategoryInfoSyncData> getHistoryAssignmentUnitCategoryInfoByQuotationIdsHistoryDates(
			String quotationIdsHistoryDates) {
		String query1 = "SELECT DISTINCT au.* FROM QuotationRecord qr, AssignmentUnitCategoryInfo au, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId AND qr.assignmentId = au.assignmentId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<AssignmentUnitCategoryInfoSyncData> result1 = addScalarForAssignmentUnitCategoryInfo(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		String query2 = "SELECT DISTINCT au.* FROM QuotationRecord qr, AssignmentUnitCategoryInfo au, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId AND qr.assignmentId = au.assignmentId ORDER BY  1  DESC ";

		// "WHERE (qr.QuotationRecordId = a.QuotationRecordId or ( qr.isBackNo =
		// 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId ) ) AND
		// qr.assignmentId = au.assignmentId ORDER BY 1 DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<AssignmentUnitCategoryInfoSyncData> result2 = addScalarForAssignmentUnitCategoryInfo(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(AssignmentUnitCategoryInfoSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		List<AssignmentUnitCategoryInfoSyncData> returnResult = new ArrayList<AssignmentUnitCategoryInfoSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}

	public SQLQuery addScalarForAssignmentUnitCategoryInfo(SQLQuery sqlQuery) {
		sqlQuery
		.addScalar("assignmentUnitCategoryInfoId", StandardBasicTypes.INTEGER)
		.addScalar("outletId", StandardBasicTypes.INTEGER).addScalar("contactPerson", StandardBasicTypes.STRING)
		.addScalar("remark", StandardBasicTypes.STRING).addScalar("unitCategory", StandardBasicTypes.STRING)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("sequence", StandardBasicTypes.INTEGER).addScalar("assignmentId", StandardBasicTypes.INTEGER)
		// .addScalar("localId", StandardBasicTypes.INTEGER)
		// .addScalar("localDbRecordStatus", StandardBasicTypes.STRING)
		;
		
		return sqlQuery;
	}	
}
