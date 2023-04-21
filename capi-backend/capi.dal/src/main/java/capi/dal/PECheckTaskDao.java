package capi.dal;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.PECheckTask;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.UnAssignedHeadPETask;
import capi.model.commonLookup.PECertaintyCaseLookupTableList;
import capi.model.commonLookup.PECheckAssignmentLookupTableList;
import capi.model.qualityControlManagement.InsufficientPEListModel;
import capi.model.qualityControlManagement.OverflowPETaskModel;
import capi.model.qualityControlManagement.PECheckListTableList;
import capi.model.qualityControlManagement.PECheckTableList;
import capi.model.qualityControlManagement.PECheckTaskList;
import capi.model.qualityControlManagement.PECheckTaskModel;
import capi.model.report.CheckTheChecker;
import capi.model.report.PECheckSummaryReport;

@Repository("PECheckTaskDao")
public class PECheckTaskDao extends GenericDao<PECheckTask> {

	@SuppressWarnings("unchecked")
	public List<Integer> getSelectedPECheckTaskIdBySurveyMonthId(int surveyMonthId) {

		Criteria criteria = this.createCriteria("pet")
								.createAlias("pet.assignment", "a", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.quotationRecords", "qr", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("pet.peCheckTaskId"), "peCheckTaskId");

		criteria.setProjection(Projections.distinct(projList));

		criteria.add(
			Restrictions.and(
				Restrictions.isNotNull("qr.quotationRecordId"),
				Restrictions.eq("pet.isCertaintyCase", true),
				Restrictions.eq("pet.isSelected", true),
				Restrictions.eq("sm.surveyMonthId", surveyMonthId)
			));

		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getRandomPECheckTaskIdBySurveyMonthId(int surveyMonthId) {

		Criteria criteria = this.createCriteria("pet")
								.createAlias("pet.assignment", "a", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("pet.peCheckTaskId"), "peCheckTaskId");

		criteria.setProjection(Projections.distinct(projList));

		criteria.add(
			Restrictions.and(
				Restrictions.eq("pet.isRandomCase", true),
				Restrictions.eq("pet.isSelected", true),
				Restrictions.eq("sm.surveyMonthId", surveyMonthId)
			));

		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getSelectedAssignmentIdBySurveyMonthId(int surveyMonthId) {

		Criteria criteria = this.createCriteria("pet")
								.createAlias("pet.assignment", "a", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.quotationRecords", "qr", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("a.assignmentId"), "assignmentId");

		criteria.setProjection(Projections.distinct(projList));

		criteria.add(
			Restrictions.and(
				Restrictions.isNotNull("qr.quotationRecordId"),
				Restrictions.eq("pet.isCertaintyCase", true),
				Restrictions.eq("pet.isSelected", true),
				Restrictions.eq("sm.surveyMonthId", surveyMonthId)
			));

		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getSelectedPECheckTaskIdByAssignmentIds(List<Integer> ids) {

		Criteria criteria = this.createCriteria("pet")
								.createAlias("pet.assignment", "a", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.property("pet.peCheckTaskId"), "peCheckTaskId");

		criteria.setProjection(Projections.distinct(projList));

		criteria.add(
			Restrictions.and(
				Restrictions.eq("pet.isCertaintyCase", true),
				Restrictions.eq("pet.isSelected", true),
				Restrictions.in("a.assignmentId", ids)
			));

		return criteria.list();
	}

	public long getNoOfAssignmentOfSurveyMonth(int surveyMonthId, boolean isSelected) {

		Criteria criteria = this.createCriteria("pet")
								.createAlias("pet.assignment", "a", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN)
								.createAlias("a.quotationRecords", "qr", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projList = Projections.projectionList();		
		projList.add(Projections.countDistinct("pet.peCheckTaskId"), "peCheckTaskId");

		criteria.setProjection(projList);

		Conjunction conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.isNotNull("qr.quotationRecordId"));
		conjunction.add(Restrictions.eq("pet.isCertaintyCase", true));
		conjunction.add(Restrictions.eq("sm.surveyMonthId", surveyMonthId));
		//conjunction.add(Restrictions.isNotNull("a.collectionDate"));
		if(isSelected) conjunction.add(Restrictions.eq("pet.isSelected", true));

		criteria.add(conjunction);

		return (long)criteria.uniqueResult();
	}

//	@SuppressWarnings("unchecked")
//	public List<PECertaintyCaseLookupTableList> getPECertaintyCaseLookupTableList(String search, int firstRecord, int displayLength, Order order
//					, Integer surveyMonthId, String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId, Integer[] oldPECheckTaskIds) {
//
//		String collectionDate = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		
//		String hql = " select pet.peCheckTaskId as id, a.assignmentId as assignmentId, o.name as firm, d.chineseName as district "
//				+ ", t.code as tpu, o.streetAddress as address, count(distinct qr.quotationRecordId) as noOfQuotation "
//				+ ", case "
//				+ " when a.assignedCollectionDate is null and a.endDate is null then '' "
//				+ "	when a.assignedCollectionDate is null then "+ endDate
//				+ " else " + collectionDate + " end  as collectionDate "
//				+ " from PECheckTask pet "
//				+ " left join pet.assignment as a "
//				+ " left join a.surveyMonth as sm "
//				+ " left join a.quotationRecords as qr "
//				+ " left join qr.quotation as q "
//				+ " left join q.batch as b "
//				+ " left join a.outlet as o "
//				+ " left join o.tpu as t "
//				+ " left join t.district as d "
//				//+ " left join o.outletTypes as ot "
//				+ " left join q.unit as u "
//				+ " left join u.subItem as si "
//				+ " left join si.outletType as ot "
//				+ " left join q.product as p "
//				+ " left join p.productGroup as pg "
//				+ " where 1=1 ";
//		
//		hql += "  and pet.isCertaintyCase is true and qr.quotationRecordId is not null ";
//		
//		if(surveyMonthId != null) hql += " and sm.surveyMonthId = :surveyMonthId ";
//		
//		if (outletTypeId != null && outletTypeId.length() > 0) hql += " and substring (ot.code,  length(ot.code) - 3 + 1, 3) = :outletTypeId ";
//		
//		if(productCategoryId != null) hql += " and pg.productGroupId = :productCategoryId ";
//		
//		if(districtId != null) hql += " and d.districtId = :districtId ";
//		
//		if(tpuId != null) hql += " and t.tpuId = :tpuId ";
//		
//		if(oldPECheckTaskIds != null && oldPECheckTaskIds.length > 0) hql += " and pet.peCheckTaskId not in (:oldPECheckTaskIds) ";
//		
//		if (!StringUtils.isEmpty(search)){
//			hql += " and  (o.name like :search or b.code like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
//				+ " or ( "
//				+ "   case "
//				+ " when a.assignedCollectionDate is null and a.endDate is null then '' "
//				+ "	when a.assignedCollectionDate is null then "+ endDate
//				+ " else " + collectionDate + " end ) like :search "
//				+ " ) or substring (ot.code,  length(ot.code) - 3 + 1, 3) like :search ";
//		}
//		
//		hql += " group by pet.peCheckTaskId, a.assignmentId, o.name, d.chineseName, t.code, "
//				+ " o.detailAddress, o.streetAddress, a.assignedCollectionDate, a.endDate, pet.isSelected ";
//		
//		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
//		
//		Query query = this.getSession().createQuery(hql);
//		query.setFirstResult(firstRecord);
//		query.setMaxResults(displayLength);
//		
//		if (surveyMonthId != null) query.setParameter("surveyMonthId", surveyMonthId);
//		
//		if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
//		
//		if (productCategoryId != null) query.setParameter("productCategoryId", productCategoryId);
//		
//		if(districtId != null) query.setParameter("districtId", districtId);
//		
//		if(tpuId != null) query.setParameter("tpuId", tpuId);
//		
//		if(oldPECheckTaskIds != null && oldPECheckTaskIds.length > 0) query.setParameterList("oldPECheckTaskIds", oldPECheckTaskIds);
//		
//		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
//
//		query.setResultTransformer(Transformers.aliasToBean(PECertaintyCaseLookupTableList.class));
//
//		return query.list();
//	}
	
	@SuppressWarnings("unchecked")
	public List<PECertaintyCaseLookupTableList> getPECertaintyCaseLookupTableList(String search, int firstRecord, int displayLength, Order order
			, Date refMonth,String outletTypeId, Integer purposeId, Integer districtId, Integer tpuId
			, String certaintyCase, Integer[] assignmentIds){
		String collectionDate = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String preMonth = String.format("FORMAT(pre.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "Select o.name as firm"
				+ " , o.firmCode as outletCode"
				+ " , d.chineseName as district"
				+ " , t.code as tpu"
				+ " , a.assignmentId as assignmentId"
				+ " , o.streetAddress as address"
				+ ", case "
					+ " when a.assignedCollectionDate is null and a.endDate is null then '' "
					+ "	when a.assignedCollectionDate is null then "+ endDate
					+ " else " + collectionDate + " end  as collectionDate "
				+ " , pe.IsSelected as isSelected"
				+ " , pe.IsCertaintyCase as isCertaintyCase"
				+ " , count(distinct qr.quotationRecordId) as noOfQuotation"
				+ " , case when pre.referenceMonth is null then ''"
					+ "	else "+preMonth+" end as lastPECheckMonth"
				+ " from Assignment as a "
				+ " inner join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join Tpu as t on t.TpuId = o.TpuId"
				+ " left join District as d on d.DistrictId = t.DistrictId"
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId"
					+ " and qr.IsBackNo = 0 and qr.IsBackTrack = 0"
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
				+ " left join Unit as u on u.UnitId = q.UnitId"
				+ " left join SubItem as si on si.SubItemId = u.SubItemId"
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
				+ " left join Purpose as pp on u.PurposeId = pp.PurposeId"
				+ " left join PeCheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join ("
					+ " Select preO.OutletId, max(preSm.ReferenceMonth) as ReferenceMonth"
					+ " from PECheckTask as prePe"
					+ " inner join Assignment as preA on preA.AssignmentId = prePe.AssignmentId"
					+ " inner join Outlet as preO on preA.outletID = preO.OutletId"
					+ " inner join SurveyMonth as preSm on preA.SurveyMonthId = preSm.SurveyMonthId"
						+ " and preSm.ReferenceMonth between DATEADD(Month, -12, :refMonth) and DATEADD(Month, -1, :refMonth)"
					+ " group by preO.OutletId) as pre on pre.OutletId = o.OutletId"
					+ " left join Batch as b on b.BatchId = q.BatchId"
				+ " where 1=1"
				+ " and a.IsImportedAssignment = 0"
				+ " and sm.ReferenceMonth = :refMonth ";
		
		if (outletTypeId != null && outletTypeId.length() > 0){
			sql += " and substring (ot.code,  len(ot.code) - 3 + 1, 3) = :outletTypeId ";
		}
			
		if(purposeId != null) {
			sql += " and pp.purposeId = :purposeId ";
		}
			
		if(districtId != null) {
			sql += " and d.districtId = :districtId ";
		}
		
		if(tpuId != null) {
			sql += " and t.tpuId = :tpuId ";
		}
		
		if(assignmentIds != null && assignmentIds.length > 0){
			sql += " and a.assignmentId not in (:assignmentIds) ";
		}
		
		if("Y".equals(certaintyCase)){
			sql += " and pe.isCertaintyCase = 1";
		} else if ("N".equals(certaintyCase)){
			sql += " and (pe.isCertaintyCase is null or pe.isCertaintyCase <> 1)";
		}
		
		if (!StringUtils.isEmpty(search)){
			sql += " and  (o.name like :search or b.code like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
				+ " or ( "
				+ "   case "
				+ " when a.assignedCollectionDate is null and a.endDate is null then '' "
				+ "	when a.assignedCollectionDate is null then "+ endDate
				+ " else " + collectionDate + " end ) like :search "
				+ " or substring (ot.code,  len(ot.code) - 3 + 1, 3) like :search "
				+ " or o.firmCode like :search )";
		}
		
		sql	+= " group by o.firmCode, o.name, d.chineseName, t.code, pe.IsCertaintyCase, a.assignmentId "
				+ " , o.outletId , o.streetAddress, a.assignedCollectionDate, a.endDate, pe.isSelected, pre.referenceMonth";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeId != null){
			query.setParameter("purposeId", purposeId);
		}
			
		if(outletTypeId != null && outletTypeId.length() > 0){
			query.setParameter("outletTypeId", outletTypeId);
		}
		
		if(districtId != null){
			query.setParameter("districtId", districtId);
		}
		
		if(tpuId != null){
			query.setParameter("tpuId", tpuId);
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		if(!StringUtils.isEmpty(search)){
			query.setParameter("search", search);
		}
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("outletCode", StandardBasicTypes.INTEGER);
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("collectionDate", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("isSelected", StandardBasicTypes.BOOLEAN);
		query.addScalar("IsCertaintyCase", StandardBasicTypes.BOOLEAN);
		query.addScalar("lastPECheckMonth", StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(PECertaintyCaseLookupTableList.class));

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<PECertaintyCaseLookupTableList.BatchCodeMapping> getPECertaintyCaseBatchLookupTableList(String search, int firstRecord, int displayLength, Order order
					, Integer surveyMonthId, String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId, Integer[] oldPECheckTaskIds) {

		//String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select a.assignmentId as assignmentId, b.code as batchCode "
				+ " from PECheckTask pet "
				+ " left join pet.assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join a.quotationRecords as qr "
				+ " left join qr.quotation as q "
				+ " left join q.batch as b "
				+ " left join a.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				//+ " left join o.outletTypes as ot "
				//+ " left join q.product as p "
				//+ " left join p.productGroup as pg "
				+ " where 1=1 ";
		
		hql += "  and pet.isCertaintyCase is true and qr.quotationRecordId is not null ";
		
		if(surveyMonthId != null) hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		//if (outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
		
		//if(productCategoryId != null) hql += " and pg.productGroupId = :productCategoryId ";
		
		if(districtId != null) hql += " and d.districtId = :districtId ";
		
		if(tpuId != null) hql += " and t.tpuId = :tpuId ";
		
		//if(oldPECheckTaskIds != null && oldPECheckTaskIds.length > 0) hql += " and pet.peCheckTaskId not in (:oldPECheckTaskIds) ";
		/*
		if (!StringUtils.isEmpty(search)){
			hql += " and  (o.name like :search or b.code like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
				+ " or ( "
				+ "   case "
				+ "   when a.collectionDate is null then '' "
				+ "   else " + collectionDate + " end ) like :search "
				+ " ) ";
		}
		*/
		hql += " group by a.assignmentId, b.code ";
		
		hql += " order by a.assignmentId desc ";
		
		Query query = this.getSession().createQuery(hql);
		//query.setFirstResult(firstRecord);
		//query.setMaxResults(displayLength);
		
		if (surveyMonthId != null) query.setParameter("surveyMonthId", surveyMonthId);
		
		//if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
		
		//if (productCategoryId != null) query.setParameter("productCategoryId", productCategoryId);
		
		if(districtId != null) query.setParameter("districtId", districtId);
		
		if(tpuId != null) query.setParameter("tpuId", tpuId);
		
		//if(oldPECheckTaskIds != null && oldPECheckTaskIds.length > 0) query.setParameterList("oldPECheckTaskIds", oldPECheckTaskIds);
		
		//if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));

		query.setResultTransformer(Transformers.aliasToBean(PECertaintyCaseLookupTableList.BatchCodeMapping.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public long countPECertaintyCaseLookupTableList(String search
			, Date refMonth,String outletTypeId, Integer purposeId, Integer districtId, Integer tpuId
			, String certaintyCase, Integer[] assignmentIds){
		String collectionDate = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		//String preMonth = String.format("FORMAT(pre.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "Select Count(distinct a.assignmentId)"
				+ " from (Select a.assignmentId"
				+ " from Assignment as a "
				+ " inner join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join Tpu as t on t.TpuId = o.TpuId"
				+ " left join District as d on d.DistrictId = t.DistrictId"
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId"
					+ " and qr.IsBackNo = 0 and qr.IsBackTrack = 0"
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
				+ " left join Unit as u on u.UnitId = q.UnitId"
				+ " left join SubItem as si on si.SubItemId = u.SubItemId"
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
				+ " left join Purpose as pp on u.PurposeId = pp.PurposeId"
				+ " left join PeCheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join ("
					+ " Select preO.OutletId, max(preSm.ReferenceMonth) as ReferenceMonth"
					+ " from PECheckTask as prePe"
					+ " inner join Assignment as preA on preA.AssignmentId = prePe.AssignmentId"
					+ " inner join Outlet as preO on preA.outletID = preO.OutletId"
					+ " inner join SurveyMonth as preSm on preA.SurveyMonthId = preSm.SurveyMonthId"
						+ " and preSm.ReferenceMonth between DATEADD(Month, -12, :refMonth) and DATEADD(Month, -1, :refMonth)"
					+ " group by preO.OutletId) as pre on pre.OutletId = o.OutletId"
				+ " left join Batch as b on b.BatchId = q.BatchId"
				+ " where 1=1"
				+ " and a.IsImportedAssignment = 0"
				+ " and sm.ReferenceMonth = :refMonth ";
		
		if (outletTypeId != null && outletTypeId.length() > 0){
			sql += " and substring (ot.code,  len(ot.code) - 3 + 1, 3) = :outletTypeId ";
		}
			
		if(purposeId != null) {
			sql += " and pp.purposeId = :purposeId ";
		}
			
		if(districtId != null) {
			sql += " and d.districtId = :districtId ";
		}
		
		if(tpuId != null) {
			sql += " and t.tpuId = :tpuId ";
		}
		
		if(assignmentIds != null && assignmentIds.length > 0){
			sql += " and a.assignmentId not in (:assignmentIds) ";
		}
		
		if("Y".equals(certaintyCase)){
			sql += " and pe.isCertaintyCase = 1";
		} else if ("N".equals(certaintyCase)){
			sql += " and (pe.isCertaintyCase is null or pe.isCertaintyCase <> 1)";
		}
		
		if (!StringUtils.isEmpty(search)){
			sql += " and  (o.name like :search or b.code like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
				+ " or ( "
				+ "   case "
				+ " when a.assignedCollectionDate is null and a.endDate is null then '' "
				+ "	when a.assignedCollectionDate is null then "+ endDate
				+ " else " + collectionDate + " end ) like :search "
				+ " or substring (ot.code,  len(ot.code) - 3 + 1, 3) like :search "
				+ " or o.firmCode like :search) ";
		}
		
		sql	+= " group by o.firmCode, o.name, d.chineseName, t.code, pe.IsCertaintyCase, a.assignmentId "
				+ " , o.outletId , o.streetAddress, a.assignedCollectionDate, a.endDate, pe.isSelected, pre.referenceMonth";
		
		sql += ") as a";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeId != null){
			query.setParameter("purposeId", purposeId);
		}
			
		if(outletTypeId != null && outletTypeId.length() > 0){
			query.setParameter("outletTypeId", outletTypeId);
		}
		
		if(districtId != null){
			query.setParameter("districtId", districtId);
		}
		
		if(tpuId != null){
			query.setParameter("tpuId", tpuId);
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		if(!StringUtils.isEmpty(search)){
			query.setParameter("search", search);
		}
		
		return (int) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getPECertaintyCaseLookupTableSelectAll(String search, Date refMonth
			, String outletTypeId, Integer purposeId, Integer districtId, Integer tpuId
			, String certaintyCase, Integer[] assignmentIds) {

		String collectionDate = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String preMonth = String.format("FORMAT(pre.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "Select o.name as firm"
				+ " , d.chineseName as district"
				+ " , t.code as tpu"
				+ " , a.assignmentId as assignmentId"
				+ " , o.streetAddress as address"
				+ ", case "
					+ " when a.assignedCollectionDate is null and a.endDate is null then '' "
					+ "	when a.assignedCollectionDate is null then "+ endDate
					+ " else " + collectionDate + " end  as collectionDate "
				+ " , pe.IsSelected as isSelected"
				+ " , pe.IsCertaintyCase as isCertaintyCase"
				+ " , count(distinct qr.quotationRecordId) as noOfQuotation"
				+ " , case when pre.referenceMonth is null then ''"
					+ "	else "+preMonth+" end as lastPECheckMonth"
				+ " from Assignment as a "
				+ " inner join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join Tpu as t on t.TpuId = o.TpuId"
				+ " left join District as d on d.DistrictId = t.DistrictId"
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId"
					+ " and qr.IsBackNo = 0 and qr.IsBackTrack = 0"
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
				+ " left join Unit as u on u.UnitId = q.UnitId"
				+ " left join SubItem as si on si.SubItemId = u.SubItemId"
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
				+ " left join Purpose as pp on u.PurposeId = pp.PurposeId"
				+ " left join PeCheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join ("
					+ " Select preO.OutletId, max(preSm.ReferenceMonth) as ReferenceMonth"
					+ " from PECheckTask as prePe"
					+ " inner join Assignment as preA on preA.AssignmentId = prePe.AssignmentId"
					+ " inner join Outlet as preO on preA.outletID = preO.OutletId"
					+ " inner join SurveyMonth as preSm on preA.SurveyMonthId = preSm.SurveyMonthId"
						+ " and preSm.ReferenceMonth between DATEADD(Month, -12, :refMonth) and DATEADD(Month, -1, :refMonth)"
					+ " group by preO.OutletId) as pre on pre.OutletId = o.OutletId"
				+ " left join Batch as b on b.BatchId = q.BatchId"
				+ " where 1=1"
				+ " and a.IsImportedAssignment = 0"
				+ " and sm.ReferenceMonth = :refMonth ";
		
		if (outletTypeId != null && outletTypeId.length() > 0){
			sql += " and substring (ot.code,  len(ot.code) - 3 + 1, 3) = :outletTypeId ";
		}
			
		if(purposeId != null) {
			sql += " and pp.purposeId = :purposeId ";
		}
			
		if(districtId != null) {
			sql += " and d.districtId = :districtId ";
		}
		
		if(tpuId != null) {
			sql += " and t.tpuId = :tpuId ";
		}
		
		if(assignmentIds != null && assignmentIds.length > 0){
			sql += " and a.assignmentId not in (:assignmentIds) ";
		}
		
		if("Y".equals(certaintyCase)){
			sql += " and pe.isCertaintyCase = 1";
		} else if ("N".equals(certaintyCase)){
			sql += " and (pe.isCertaintyCase is null or pe.isCertaintyCase <> 1)";
		}
		
		if (!StringUtils.isEmpty(search)){
			sql += " and  (o.name like :search or b.code like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
				+ " or ( "
				+ "   case "
				+ " when a.assignedCollectionDate is null and a.endDate is null then '' "
				+ "	when a.assignedCollectionDate is null then "+ endDate
				+ " else " + collectionDate + " end ) like :search "
				+ " ) or substring (ot.code,  len(ot.code) - 3 + 1, 3) like :search ";
		}
		
		sql	+= " group by o.name, d.chineseName, t.code, pe.IsCertaintyCase, a.assignmentId "
				+ " , o.outletId , o.streetAddress, a.assignedCollectionDate, a.endDate, pe.isSelected, pre.referenceMonth";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("refMonth", refMonth);
		
		if(purposeId != null){
			query.setParameter("purposeId", purposeId);
		}
			
		if(outletTypeId != null && outletTypeId.length() > 0){
			query.setParameter("outletTypeId", outletTypeId);
		}
		
		if(districtId != null){
			query.setParameter("districtId", districtId);
		}
		
		if(tpuId != null){
			query.setParameter("tpuId", tpuId);
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		if(!StringUtils.isEmpty(search)){
			query.setParameter("search", search);
		}
		
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<PECertaintyCaseLookupTableList> getPECertaintyCaseListByIds(Integer surveyMonthId, Integer[] assignmentIds) {
		String collectionDate = String.format("FORMAT(a.assignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String endDate = String.format("FORMAT(a.endDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "Select o.name as firm"
				+ " , o.firmCode as outletCode"
				+ " , d.chineseName as district"
				+ " , t.code as tpu"
				+ " , a.assignmentId as assignmentId"
				+ " , o.streetAddress as address"
				+ ", case "
					+ " when a.assignedCollectionDate is null and a.endDate is null then '' "
					+ "	when a.assignedCollectionDate is null then "+ endDate
					+ " else " + collectionDate + " end  as collectionDate "
				+ " , count(distinct qr.quotationRecordId) as noOfQuotation"
				+ " , pe.IsSelected as isSelected"
				+ " from Assignment as a "
				+ " inner join SurveyMonth as sm on sm.SurveyMonthId = a.SurveyMonthId"
				+ " left join PECheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId"
				+ " and qr.IsBackNo = 0 and qr.IsBackTrack = 0"
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
				+ " left join Outlet as o on o.OutletId = a.OutletId"
				+ " left join TPU as t on t.TpuId = o.TpuId"
				+ " left join District as d on d.DistrictId = t.DistrictId"
				+ " where 1=1"
				+ " and sm.SurveyMonthId = :surveyMonthId ";
		
		if(assignmentIds !=null && assignmentIds.length>0){
			sql += " and a.assignmentId in (:assignmentIds)";
		} else {
			sql += " and (pe.IsSelected = 1 and pe.IsCertaintyCase = 1 )";
		}
		
		sql	+= " group by o.name, d.chineseName, t.code, pe.IsCertaintyCase, a.assignmentId "
				+ " , o.outletId , o.streetAddress, a.assignedCollectionDate, a.endDate, pe.isSelected, o.firmCode";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("surveyMonthId", surveyMonthId);
		if(assignmentIds != null && assignmentIds.length > 0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("outletCode", StandardBasicTypes.INTEGER);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("collectionDate", StandardBasicTypes.STRING);
		query.addScalar("isSelected", StandardBasicTypes.BOOLEAN);
		
		query.setResultTransformer(Transformers.aliasToBean(PECertaintyCaseLookupTableList.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<PECertaintyCaseLookupTableList.BatchCodeMapping> getPECertaintyCaseBatchListByIds(Integer surveyMonthId, Integer[] peCheckTaskIds) {

		//String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select a.assignmentId as assignmentId,b.code as batchCode "
				+ " from PECheckTask pet "
				+ " left join pet.assignment as a left join a.surveyMonth as sm left join a.quotationRecords as qr left join qr.quotation as q "
				+ " left join q.batch as b left join a.outlet as o left join o.tpu as t left join t.district as d "
				+ " left join o.outletTypes as ot "
				+ " left join q.product as p left join p.productGroup as pg "
				+ " where 1=1 ";
		
//		hql += " and a.collectionDate is not null and pet.isCertaintyCase is true and qr.quotationRecordId is not null ";
		hql += " and pet.isCertaintyCase is true and qr.quotationRecordId is not null ";
		
		if(surveyMonthId != null) hql += " and sm.surveyMonthId = :surveyMonthId ";
		
		if(peCheckTaskIds != null && peCheckTaskIds.length > 0) hql += " and pet.peCheckTaskId in (:peCheckTaskIds) ";
		
		hql += " group by a.assignmentId, b.code ";
		
		Query query = this.getSession().createQuery(hql);
		
		//query.setParameter("approved", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
		
		if (surveyMonthId != null) query.setParameter("surveyMonthId", surveyMonthId);
		
		if(peCheckTaskIds != null && peCheckTaskIds.length > 0) query.setParameterList("peCheckTaskIds", peCheckTaskIds);
		
		query.setResultTransformer(Transformers.aliasToBean(PECertaintyCaseLookupTableList.BatchCodeMapping.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<PECheckTask> getPECheckTasksByIds(Integer [] ids){
		Criteria criteria = this.createCriteria();
		if(ids != null && ids.length > 0) {
			criteria.add(Restrictions.in("peCheckTaskId", ids));
		}
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<PECheckListTableList> getPECheckListTableList(String search, int firstRecord, int displayLength, Order order
					, Date referenceMonth, Integer[] supervisorIds) {
	
		String sql = "Select u.userId as userId"
				+ ", u.staffCode as officerCode"
				+ ", u.chineseName as officerName"
				+ ", u.team as team"
				+ ", count(distinct total.assignmentId) as total"
				+ ", count(distinct excluded.AssignmentId) as excluded"
				+ ", count(distinct checked.PECheckFormId) as checked"
				+ ", count(distinct selected.PECheckTaskId) as selected"
				+ ", count(distinct approved.AssignmentId) as approved"
				+ ", count(distinct nonContact.PECheckFormId) as nonContact"
				+ " from [User] as u"
				+ " cross join SurveyMonth as sm"
				+ " left join Assignment as total on u.userId = total.userId "
					+ " and sm.SurveyMonthId = total.SurveyMonthId"
				+ " left join"
					+ " (Select a.AssignmentId from Assignment as a "
					+ " left join QuotationRecord as qr on a.AssignmentID = qr.AssignmentId"
					+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
					+ " left join Unit as u on q.UnitId = u.UnitId"
					+ " left join SubItem as si on si.SubItemId = u.SubItemId"
					+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
					+ " left join Purpose as pp on u.PurposeId = pp.PurposeId"
					+ " where "
					+ " pp.PEIncluded = 1"
					+ " and SUBSTRING(ot.[Code], len(ot.[code])-2, 3) not in (Select eot.[shortCode] from PEExcludedOutletType as eot)"
					+ " group by a.AssignmentId) as excluded on excluded.AssignmentId = total.AssignmentId"
				+ " left join PECheckForm as checked on checked.AssignmentId = total.AssignmentId"
					+ " and checked.IsNonContact = 0 and checked.Status = 'Submitted'"
				+ " left join PECheckTask as selected on selected.AssignmentId = total.AssignmentId"
					+ " and selected.IsSelected = 1"
				+ " left join "
					+ " (Select a.AssignmentId from Assignment as a "
					+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId and qr.IsBackNo = 0"
					+ " left join QuotationRecord as approved on a.AssignmentID = approved.AssignmentId "
					+ " and approved.IsBackNo = 0 and approved.Status = 'Approved'"
					+ " group by a.AssignmentId"
					+ " having count(qr.QuotationRecordId) = count(approved.QuotationRecordId)"
					+ " ) as approved on approved.AssignmentId = excluded.AssignmentId"
				+ " left join PECheckForm as nonContact on nonContact.AssignmentId = total.AssignmentId "
					+ " and nonContact.IsNonContact = 1 and nonContact.Status = 'Submitted'"
				+ " left join userrole as ur on ur.UserId = u.UserId "
				+ " left join [Role] as r on ur.RoleId = r.RoleId "
				+ " left join [User] as supervisor on supervisor.UserId = u.SupervisorId "
				+ " inner join QuotationRecord as qr on qr.AssignmentId = total.AssignmentId"
				+ " where r.AuthorityLevel & 16 = 16 and u.Status <> 'Inactive' "
				+ " and sm.ReferenceMonth = :refMonth";
		
		if (supervisorIds != null && supervisorIds.length > 0) {
			sql += " and supervisor.userId in (:supervisorIds) ";
		}
			
		if (!StringUtils.isEmpty(search)){
			sql += " and  (u.staffCode like :search or u.chineseName like :search or u.team like :search)";
		}		
		
		sql += " group by u.userId, u.staffCode, u.chineseName, u.team ";
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setParameter("refMonth", referenceMonth);

		if (supervisorIds != null && supervisorIds.length > 0) {
			query.setParameterList("supervisorIds", supervisorIds);
		}
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("officerCode", StandardBasicTypes.STRING);
		query.addScalar("officerName", StandardBasicTypes.STRING);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("total", StandardBasicTypes.LONG);
		query.addScalar("checked", StandardBasicTypes.LONG);
		query.addScalar("selected", StandardBasicTypes.LONG);
		query.addScalar("excluded", StandardBasicTypes.LONG);
		query.addScalar("approved", StandardBasicTypes.LONG);
		query.addScalar("nonContact", StandardBasicTypes.LONG);
		
		query.setResultTransformer(Transformers.aliasToBean(PECheckListTableList.class));

		return query.list();
	}
	
	public long countPECheckListTableList(String search	, Date referenceMonth, Integer[] supervisorIds) {
		
		String sql = "Select u.userId as userId"
				+ " from [User] as u"
				+ " cross join SurveyMonth as sm"
				+ " left join Assignment as total on u.userId = total.userId "
					+ " and sm.SurveyMonthId = total.SurveyMonthId"
				+ " left join"
					+ " (Select a.AssignmentId from Assignment as a "
					+ " left join QuotationRecord as qr on a.AssignmentID = qr.AssignmentId"
					+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
					+ " left join Unit as u on q.UnitId = u.UnitId"
					+ " left join SubItem as si on si.SubItemId = u.SubItemId"
					+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
					+ " left join Purpose as pp on u.PurposeId = pp.PurposeId"
					+ " where "
					+ " pp.PEIncluded = 1"
					+ " and SUBSTRING(ot.[Code], len(ot.[code])-2, 3) not in (Select eot.[shortCode] from PEExcludedOutletType as eot)"
					+ " group by a.AssignmentId) as excluded on excluded.AssignmentId = total.AssignmentId"
				+ " left join PECheckForm as checked on checked.AssignmentId = total.AssignmentId"
					+ " and checked.IsNonContact = 0 and checked.Status = 'Submitted'"
				+ " left join PECheckTask as selected on selected.AssignmentId = total.AssignmentId"
					+ " and selected.IsSelected = 1"
				+ " left join "
					+ " (Select a.AssignmentId from Assignment as a "
					+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId and qr.IsBackNo = 0"
					+ " left join QuotationRecord as approved on a.AssignmentID = approved.AssignmentId "
					+ " and approved.IsBackNo = 0 and approved.Status = 'Approved'"
					+ " group by a.AssignmentId"
					+ " having count(qr.QuotationRecordId) = count(approved.QuotationRecordId)"
					+ " ) as approved on approved.AssignmentId = excluded.AssignmentId"
				+ " left join PECheckForm as nonContact on nonContact.AssignmentId = total.AssignmentId "
					+ " and nonContact.IsNonContact = 1 and nonContact.Status = 'Submitted'"
				+ " left join userrole as ur on ur.UserId = u.UserId "
				+ " left join [Role] as r on ur.RoleId = r.RoleId "
				+ " left join [User] as supervisor on supervisor.UserId = u.SupervisorId "
				+ " inner join QuotationRecord as qr on qr.AssignmentId = total.AssignmentId"
				+ " where r.AuthorityLevel & 16 = 16 and u.Status <> 'Inactive' "
				+ " and sm.ReferenceMonth = :refMonth";
		
		if (supervisorIds != null && supervisorIds.length > 0) {
			sql += " and supervisor.userId in (:supervisorIds) ";
		}
			
		if (!StringUtils.isEmpty(search)){
			sql += " and  (u.staffCode like :search or u.chineseName like :search or u.team like :search)";
		}		
		
		sql += " group by u.userId, u.staffCode, u.chineseName, u.team ";
	
		String countSql = "select count(*) from [User] where userId in ("+sql+") ";
		
		SQLQuery query = this.getSession().createSQLQuery(countSql);
		
		query.setParameter("refMonth", referenceMonth);

		if (supervisorIds != null && supervisorIds.length > 0) {
			query.setParameterList("supervisorIds", supervisorIds);
		}
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		return (int) query.uniqueResult();
	}
	
	public List<InsufficientPEListModel> getInsufficientPEList(Integer surveyMonthId, Double percentage){
		String sql = "exec dbo.GetInsufficientPEList :surveyMonthId, :percentage";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameter("percentage", percentage);
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("totalAssignment", StandardBasicTypes.LONG);
		query.addScalar("peAssignment", StandardBasicTypes.LONG);
		query.addScalar("remaining", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(InsufficientPEListModel.class));
		return query.list();
	}
	
	
	public List<OverflowPETaskModel> getOverflowPETask(Integer surveyMonthId){
		String sql = "exec dbo.GetOverflowPETask :surveyMonthId";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("surveyMonthId", surveyMonthId);
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("overflow", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(OverflowPETaskModel.class));
		return query.list();
	}
	
	
	
	public List<PECheckTask> getNotSubmittedRandomCase(int userId, int noOfCases, int surveyMonthId) {
		
		String hql = "select task "
				+ " from Assignment as a "
				+ " inner join a.peCheckTask as task "
				+ " left join a.peCheckForm as form "
				+ " where task.isSelected = true and task.isRandomCase = true "
				+ " and (form.id is null or form.user.id = :userId and form.status != 'Submitted' ) "
				+ " and a.user.userId = :userId "
				+ " and a.surveyMonth.surveyMonthId = :surveyMonthId";
		
		Query query = this.getSession().createQuery(hql);
		query.setMaxResults(noOfCases);
		query.setParameter("userId", userId);
		query.setParameter("surveyMonthId", surveyMonthId);
		return query.list();
		
	}
	
	
	public List<PECheckTask> getNotSubmittedPE(int userId, int noOfCases, int surveyMonthId) {
		
		String hql = "select task "
				+ " from Assignment as a "
				+ " inner join a.peCheckTask as task "
				+ " left join a.peCheckForm as form "
				+ " where task.isSelected = true "
				+ " and (form.id is null or form.user.id = :userId and form.status != 'Submitted' ) "
				+ " and a.surveyMonth.surveyMonthId = :surveyMonthId"
				+ " and a.user.userId = :userId";
		
		Query query = this.getSession().createQuery(hql);
		query.setFetchSize(noOfCases);
		query.setParameter("userId", userId);
		query.setParameter("surveyMonthId", surveyMonthId);
		return query.list();
		
	}
	
	public List<UnAssignedHeadPETask> getUnassignedHeadPETask(Integer surveyMonthId){
		String sql = "exec dbo.GetUnassignedHeadPETask :surveyMonthId";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("surveyMonthId", surveyMonthId);
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("isFieldHead", StandardBasicTypes.BOOLEAN);
		query.addScalar("isSectionHead", StandardBasicTypes.BOOLEAN);
		query.setResultTransformer(Transformers.aliasToBean(UnAssignedHeadPETask.class));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<PECheckTaskList> getPECheckFormList(Integer userId, Integer surveyMonthId) {
		String sql = "Select a.AssignmentId as assignmentId"
				+ " , pe.PECheckTaskId as peCheckTaskId"
				+ " , pe.IsSectionHead as isSectionHead"
				+ " , pe.IsFieldTeamHead as isFieldTeamHead"
				+ " , o.name as firm"
				+ " , t.Code as tpu"
				+ " , d.chineseName as district"
				+ " , o.tel as tel"
				+ " , o.streetAddress as address"
				+ " , count(qr.QuotationRecordId) as noOfQuotation"
				+ " , pe.isCertaintyCase as isCertaintyCase"
				+ " , pe.isRandomCase as isRandomCase"
				+ " , form.peCheckFormId as peCheckFormId"
				+ " , form.status as status"
				+ " , case when a.status = 1 then 'EN'"
					+ " when a.status = 2 then 'Closed'"
					+ " when a.status = 3 then 'MV'"
					+ " when a.status = 4 then 'Not Suitable'"
					+ " when a.status = 5 then 'Refusal'"
					+ " when a.status = 6 then 'Wrong Outlet'"
					+ " when a.status = 7 then 'DL'"
					+ " when a.status = 8 then 'NC'"
					+ " when a.status = 9 then 'IP'"
					+ " when a.status = 10 then 'DU'"
					+ " else '' end as firmStatus"
				+ " from PECheckTask as pe "
				+ " left join Assignment as a on pe.AssignmentId = a.AssignmentId"
				+ " left join PECheckForm as form on a.AssignmentId = form.AssignmentId"
				+ " left join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId"
					+ " and qr.IsBackNo = 0"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join Tpu as t on t.TpuId = o.TpuId"
				+ " left join District as d on d.DistrictId = t.DistrictId"
				+ " where a.AssignmentId in "
					+ " ( Select distinct a.AssignmentId "
					+ " from Assignment as a"
					+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
					+ " left join QuotationRecord as total on a.AssignmentId = total.AssignmentId and total.IsBackNo = 0"
					+ " left join QuotationRecord as approved on total.QuotationRecordId = approved.QuotationRecordId and approved.status = 'Approved'"
					+ " where sm.SurveyMonthId = :surveyMonthId and a.userId = :userId"
					+ " group by a.AssignmentId"
					+ " having count(distinct total.QuotationRecordId) = count(distinct approved.QuotationRecordId)"
					+ ")"
				+ " and pe.IsSelected = 1"
				+ " group by a.AssignmentId, pe.PECheckTaskId, pe.IsSectionHead, pe.IsFieldTeamHead"
				+ " , o.name, t.Code, d.chineseName, o.tel, o.streetAddress, pe.isCertaintyCase, pe.isRandomCase"
				+ " , form.peCheckFormId, form.status, a.status"
				+ " having count(distinct qr.QuotationRecordId) > 0";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameter("userId", userId);
		
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("tel", StandardBasicTypes.STRING);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("peCheckTaskId", StandardBasicTypes.INTEGER);
		query.addScalar("isSectionHead", StandardBasicTypes.BOOLEAN);
		query.addScalar("isFieldTeamHead", StandardBasicTypes.BOOLEAN);
		query.addScalar("isCertaintyCase", StandardBasicTypes.BOOLEAN);
		query.addScalar("isRandomCase", StandardBasicTypes.BOOLEAN);
		query.addScalar("peCheckFormId", StandardBasicTypes.INTEGER);
		query.addScalar("status", StandardBasicTypes.STRING);
		query.addScalar("firmStatus", StandardBasicTypes.STRING);
	
		query.setResultTransformer(Transformers.aliasToBean(PECheckTaskList.class));

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<PECheckAssignmentLookupTableList> getPECheckAssignmentLookupTableList(String search, int firstRecord, int displayLength, Order order
					, Integer surveyMonthId, Integer userId, String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId, 
					Integer[] ignoreAssignmentIds, Date refMonth) {
		String preMonth = String.format("FORMAT(pre.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "Select a.AssignmentId as assignmentId"
				+ " , a.referenceNo as referenceNo"
				+ " , o.name as firm"
				+ " , t.code as tpu"
				+ " , d.chineseName as district"
				+ " , o.tel as tel"
				+ " , o.streetAddress as address"
				+ " , count(qr.QuotationRecordId) as noOfQuotation"
				+ " , case when a.status = 1 then 'EN'"
					+ " when a.status = 2 then 'Closed'"
					+ " when a.status = 3 then 'MV'"
					+ " when a.status = 4 then 'Not Suitable'"
					+ " when a.status = 5 then 'Refusal'"
					+ " when a.status = 6 then 'Wrong Outlet'"
					+ " when a.status = 7 then 'DL'"
					+ " when a.status = 8 then 'NC'"
					+ " when a.status = 9 then 'IP'"
					+ " when a.status = 10 then 'DU'"
					+ " else '' end as firmStatus"
				+ " , case when pre.referenceMonth is null then ''"
					+ "	else "+preMonth+" end as lastPECheckMonth"
				+ " from Assignment as a"
				+ " left join PECheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId and qr.IsBackNo = 0"
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
				+ " left join Unit as u on u.UnitId = q.UnitId"
				+ " left join Purpose as pp on u.PurposeId = pp.PurposeId"
				+ " left join SubItem as si on si.SubItemId = u.SubItemId"
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
				+ " left join Product as p on p.ProductId = qr.ProductId"
				+ " left join ProductGroup as pg on pg.ProductGroupId = p.ProductGroupId"
				+ " left join Outlet as o on o.OutletId = qr.OutletId"
				+ " left join Tpu as t on t.TpuId = o.tpuId"
				+ " left join District as d on d.DistrictId = t.DistrictId"
				+ " left join ("
					+ " Select preO.OutletId, max(preSm.ReferenceMonth) as ReferenceMonth"
					+ " from PECheckTask as prePe"
					+ " inner join Assignment as preA on preA.AssignmentId = prePe.AssignmentId"
					+ " inner join Outlet as preO on preA.outletID = preO.OutletId"
					+ " inner join SurveyMonth as preSm on preA.SurveyMonthId = preSm.SurveyMonthId"
						+ " and preSm.ReferenceMonth between DATEADD(Month, -12, :refMonth) and DATEADD(Month, -1, :refMonth)"
					+ " group by preO.OutletId) as pre on pre.OutletId = o.OutletId"
				+ " where a.AssignmentId in "
					+ "(Select distinct a.AssignmentId"
					+ " from Assignment as a"
					+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
					+ " left join QuotationRecord as total on a.AssignmentId = total.AssignmentId and total.IsBackNo = 0"
					+ " left join QuotationRecord as approved on total.QuotationRecordId = approved.QuotationRecordId and approved.status = 'Approved'"
//					+ " left join AllocationBatch as ab on ab.AllocationBatchId = qr.AllocationBatchId"
					+ " where sm.SurveyMonthId = :surveyMonthId"
//					+ " and ab.status = 2"
					+ " group by a.AssignmentId"
					+ " having count(distinct total.QuotationRecordId) = count(distinct approved.QuotationRecordId)"
					+ " ) "
				+ "and (pe.IsSelected = 0 or pe.PECheckTaskId is null)";
		
		if (userId != null){
			sql += " and a.userId = :userId";
		}
		
		if (productCategoryId != null){
			sql += " and pg.productGroupId = :productCategoryId";
		}
		
//		if (outletTypeId != null){
//			sql += " and ot.outletTypeId = :outletTypeId";
//		}
		
		if (districtId != null){
			sql += " and d.districtId = :districtId";
		}
		
		if (tpuId != null){
			sql += " and t.tpuId = :tpuId";
		}
		
		if (ignoreAssignmentIds != null && ignoreAssignmentIds.length > 0){
			sql += " and a.assignmentId not in (:ignoreAssignmentIds)";
		}
		
		if (!StringUtils.isEmpty(search)){
			sql += " and  (o.name like :search "
					+ " or a.referenceNo like :search "
					+ " or d.chineseName like :search "
					+ " or t.code like :search "
					+ " or o.streetAddress like :search "
					+ " or o.tel like :search "
					+ " or pre.referenceMonth like :search "
					+ " or case when a.status = 1 then 'EN'"
					+ " when a.status = 2 then 'Closed'"
					+ " when a.status = 3 then 'MV'"
					+ " when a.status = 4 then 'Not Suitable'"
					+ " when a.status = 5 then 'Refusal'"
					+ " when a.status = 6 then 'Wrong Outlet'"
					+ " when a.status = 7 then 'DL'"
					+ " when a.status = 8 then 'NC'"
					+ " when a.status = 9 then 'IP'"
					+ " when a.status = 10 then 'DU' end like :search )";
		}
		
		sql += " group by a.AssignmentId, a.referenceNo, o.name, t.Code, d.chineseName"
				+ " , o.tel, o.streetAddress, a.status, pre.referenceMonth"
				+ " having count(distinct qr.QuotationRecordId) > 0";
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setParameter("surveyMonthId", surveyMonthId);

		if(userId != null) {
			query.setParameter("userId", userId);
		}

		if(ignoreAssignmentIds != null && ignoreAssignmentIds.length > 0){
			query.setParameterList("ignoreAssignmentIds", ignoreAssignmentIds);
		}

//		if (outletTypeId != null && outletTypeId.length() > 0){
//			query.setParameter("outletTypeId", outletTypeId);
//		}
		
		query.setParameter("refMonth", refMonth);
		
		if (productCategoryId != null){
			query.setParameter("productCategoryId", productCategoryId);
		}
		
		if(districtId != null){
			query.setParameter("districtId", districtId);
		}
		
		if(tpuId != null) {
			query.setParameter("tpuId", tpuId);
		}
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("referenceNo", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("tel", StandardBasicTypes.STRING);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("firmStatus", StandardBasicTypes.STRING);
		query.addScalar("lastPECheckMonth", StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(PECheckAssignmentLookupTableList.class));

		return query.list();
	}

	public Integer countPECheckAssignmentLookupTableList(String search, Integer userId, Integer surveyMonthId
			, String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId, 
			Integer[] ignoreAssignmentIds, Date refMonth) {

		String sql = "Select a.AssignmentId as assignmentId"
				+ " from Assignment as a"
				+ " left join PECheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId and qr.IsBackNo = 0"
				+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
				+ " left join Unit as u on u.UnitId = q.UnitId"
				+ " left join Purpose as pp on u.PurposeId = pp.PurposeId"
				+ " left join SubItem as si on si.SubItemId = u.SubItemId"
				+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
				+ " left join Product as p on p.ProductId = qr.ProductId"
				+ " left join ProductGroup as pg on pg.ProductGroupId = p.ProductGroupId"
				+ " left join Outlet as o on o.OutletId = qr.OutletId"
				+ " left join Tpu as t on t.TpuId = o.tpuId"
				+ " left join District as d on d.DistrictId = t.DistrictId"
				+ " left join ("
					+ " Select preO.OutletId, max(preSm.ReferenceMonth) as ReferenceMonth"
					+ " from PECheckTask as prePe"
					+ " inner join Assignment as preA on preA.AssignmentId = prePe.AssignmentId"
					+ " inner join Outlet as preO on preA.outletID = preO.OutletId"
					+ " inner join SurveyMonth as preSm on preA.SurveyMonthId = preSm.SurveyMonthId"
						+ " and preSm.ReferenceMonth between DATEADD(Month, -12, :refMonth) and DATEADD(Month, -1, :refMonth)"
					+ " group by preO.OutletId) as pre on pre.OutletId = o.OutletId"
				+ " where a.AssignmentId in "
					+ "(Select distinct a.AssignmentId"
					+ " from Assignment as a"
					+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
					+ " left join QuotationRecord as total on a.AssignmentId = total.AssignmentId and total.IsBackNo = 0"
					+ " left join QuotationRecord as approved on total.QuotationRecordId = approved.QuotationRecordId and approved.status = 'Approved'"
//					+ " left join AllocationBatch as ab on ab.AllocationBatchId = qr.AllocationBatchId"
					+ " where sm.SurveyMonthId = :surveyMonthId"
//					+ " and (ab.status = 2 or ab.AllocationBatchId is null)"
					+ " group by a.AssignmentId"
					+ " having count(distinct total.QuotationRecordId) = count(distinct approved.QuotationRecordId)"
					+ " ) "
				+ "and (pe.IsSelected = 0 or pe.PECheckTaskId is null)";
		
		if (userId != null){
			sql += " and a.userId = :userId";
		}
		
		if (productCategoryId != null){
			sql += " and pg.productGroupId = :productCategoryId";
		}
		
//		if (outletTypeId != null){
//			sql += " and ot.outletTypeId = :outletTypeId";
//		}
		
		if (districtId != null){
			sql += " and d.districtId = :districtId";
		}
		
		if (tpuId != null){
			sql += " and t.tpuId = :tpuId";
		}
		
		if (ignoreAssignmentIds != null && ignoreAssignmentIds.length > 0){
			sql += " and a.assignmentId not in (:ignoreAssignmentIds)";
		}
		
		if (!StringUtils.isEmpty(search)){
			sql += " and  (o.name like :search "
					+ " or a.referenceNo like :search "
					+ " or d.chineseName like :search "
					+ " or t.code like :search "
					+ " or o.streetAddress like :search "
					+ " or o.tel like :search "
					+ " or pre.referenceMonth like :search "
					+ " or case when a.status = 1 then 'EN'"
					+ " when a.status = 2 then 'Closed'"
					+ " when a.status = 3 then 'MV'"
					+ " when a.status = 4 then 'Not Suitable'"
					+ " when a.status = 5 then 'Refusal'"
					+ " when a.status = 6 then 'Wrong Outlet'"
					+ " when a.status = 7 then 'DL'"
					+ " when a.status = 8 then 'NC'"
					+ " when a.status = 9 then 'IP'"
					+ " when a.status = 10 then 'DU' end like :search )";
		}
		
		sql += " group by a.AssignmentId, a.referenceNo, o.name, t.Code, d.chineseName"
				+ " , o.tel, o.streetAddress, a.status, pre.referenceMonth"
				+ " having count(distinct qr.QuotationRecordId) > 0";
		
		String countSql = "select count(*) from Assignment as a where a.assignmentId in ("+sql+") ";
		
		SQLQuery query = this.getSession().createSQLQuery(countSql);
		
		query.setParameter("surveyMonthId", surveyMonthId);

		query.setParameter("refMonth", refMonth);
		
		if(userId != null) {
			query.setParameter("userId", userId);
		}

		if(ignoreAssignmentIds != null && ignoreAssignmentIds.length > 0){
			query.setParameterList("ignoreAssignmentIds", ignoreAssignmentIds);
		}

//		if (outletTypeId != null && outletTypeId.length() > 0){
//			query.setParameter("outletTypeId", outletTypeId);
//		}
		
		if (productCategoryId != null){
			query.setParameter("productCategoryId", productCategoryId);
		}
		
		if(districtId != null){
			query.setParameter("districtId", districtId);
		}
		
		if(tpuId != null) {
			query.setParameter("tpuId", tpuId);
		}
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		if (query.uniqueResult() == null) return 0;
		
		return (Integer)query.uniqueResult();
	}
	
//	@SuppressWarnings("unchecked")
//	public List<PECheckTaskList> getPECheckTaskList(Integer[] assignmentIds, Integer surveyMonthId) {
//		
//		String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//
//		String hql = " select pef.peCheckFormId as peCheckFormId,  "
//				+ " pet.peCheckTaskId as peCheckTaskId, "
//				+ " case a.isImportedAssignment when true then a.additionalFirmName else o.name end as firm, "
//				+ " b.code as batchCode, "
//				+ " case when a.collectionDate is null then '' else " + collectionDate + " end  as collectionDate, "
//				+ " d.chineseName as district, "
//				+ " tpu.code as tpu,  "
//				+ " case a.isImportedAssignment when true then a.additionalFirmAddress else o.streetAddress end as address, "
//				+ " count (distinct qr.quotationRecordId) as noOfQuotation,"
//				+ " case when pet.isFieldTeamHead is null then false else pet.isFieldTeamHead end as fieldTeamHead, "
//				+ " case when pet.isSectionHead is null then false else pet.isSectionHead end as sectionHead, "
//				+ " case when pet.isCertaintyCase is null then false else pet.isCertaintyCase end as certaintyCase, "
//				+ " case when pet.isRandomCase is null then false else pet.isRandomCase end as randomCase, "
//				+ " a.assignmentId as assignmentId, "
//				+ " pef.status as status "
//				+ " from Assignment a "
//				+ " inner join a.surveyMonth as sm  "
//				+ " left join a.peCheckTask as pet "
//				+ " left join a.peCheckForm as pef "
//				+ " left join a.user as u "
//				+ " left join a.quotationRecords as qr "
//				+ " left join qr.quotation as q "
//				+ " left join q.batch as b "
//				+ " left join a.outlet as o "
//				+ " left join o.tpu as tpu "
//				+ " left join tpu.district as d "
//				//+ " where qra = 'Approved' "
//				+ " where sm.surveyMonthId = :surveyMonthId and a.assignmentId in (:assignmentIds) ";
//	
//		hql += " group by pef.peCheckFormId, pet.peCheckTaskId, case a.isImportedAssignment when true then a.additionalFirmName else o.name end, "
//				+ " b.code, case when a.collectionDate is null then '' else " + collectionDate + " end, "
//				+ " d.chineseName, tpu.code, case a.isImportedAssignment when true then a.additionalFirmAddress else o.streetAddress end, "
//				+ " pet.isFieldTeamHead, pet.isSectionHead , pet.isCertaintyCase, pet.isRandomCase, a.assignmentId, pef.status ";
//		
//		
//		Query query = this.getSession().createQuery(hql);
//
//		query.setParameterList("assignmentIds", assignmentIds);
//		query.setParameter("surveyMonthId", surveyMonthId);
//
//		query.setResultTransformer(Transformers.aliasToBean(PECheckTaskList.class));
//
//		return query.list();
//	}
	
	@SuppressWarnings("unchecked")
	public List<PECheckTableList> getPECheckTableList(String search, int firstRecord, int displayLength, Order order
					, Date referenceMonth, List<Integer> supervisorList, String certaintyCase, Integer roleHeader) {
		String surveyMonth = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		String sql = " select case when sm.referenceMonth is null then '' else "+ surveyMonth +" end as surveyMonth, "
				+ " a.referenceNo as referenceNo, "
				+ " pef.status as status, "
				+ " u.staffCode as userStaffCode, "
				+ " u.chineseName as userChineseName, "
				+ " pef.peCheckFormId as peCheckFormId, "
				+ " o.name as outletName, "
				+ " o.firmCode as firmCode, "
				+ " pef.modifiedDate as modifiedDate, "
				+ " pef.modifiedBy as modifiedBy, "
				+ " pe.isCertaintyCase as isCertaintyCase, "
				+ " pe.isSectionHead as isSectionHead, "
				+ " pe.isFieldTeamHead as isFieldTeamHead "
				+ " from PECheckForm pef "
				+ " inner join Assignment as a on a.AssignmentId = pef.AssignmentId"
				+ " left join PECheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join QuotationRecord as total on total.AssignmentId = a.AssignmentId"
					+ " and total.isBackNo = 0 and total.isBackTrack = 0"
				+ " left join QuotationRecord as approved on total.QuotationRecordId = approved.QuotationRecordId"
					+ " and approved.status = 'Approved'"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
				+ " left join [User] as u on u.userId = pef.officerId "
				+ " where 1 = 1 ";
		
		if (supervisorList != null && supervisorList.size() > 0){
			sql += " and u.supervisorId in (:supervisor) ";
		}
		
		if (referenceMonth != null) {
			sql += " and sm.referenceMonth = :referenceMonth ";
		}

		if("Y".equals(certaintyCase)){
			sql += " and pe.isCertaintyCase = 1";
		} else if ("N".equals(certaintyCase)){
			sql += " and (pe.isCertaintyCase is null or pe.isCertaintyCase <> 1)";
		}
		
		if (roleHeader != null && roleHeader==1){
			sql += " and pe.isSectionHead = 1";
		}
		
		if (roleHeader != null && roleHeader==2){
			sql += " and pe.isFieldTeamHead = 1";
		}
		
		if (!StringUtils.isEmpty(search)){
			sql += " and  (u.staffCode like :search or u.chineseName like :search "
					+ " or a.referenceNo like :search or o.name like :search) ";
		}
		
		sql += "group by sm.referenceMonth, a.referenceNo, pef.status, u.staffCode, u.chineseName"
				+ " , pef.peCheckFormId, o.name, o.firmCode, pef.modifiedDate, pef.modifiedBy"
				+ " , pe.isCertaintyCase , pe.isSectionHead, pe.isFieldTeamHead";
		
		sql += " having count(total.quotationRecordId) = count(approved.quotationRecordId)";

		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");

		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		if (referenceMonth != null) query.setParameter("referenceMonth", referenceMonth);

		if (supervisorList != null && supervisorList.size() > 0){
			query.setParameterList("supervisor", supervisorList);
		}
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		query.setResultTransformer(Transformers.aliasToBean(PECheckTableList.class));

		query.addScalar("referenceNo", StandardBasicTypes.STRING);
		query.addScalar("status", StandardBasicTypes.STRING);
		query.addScalar("userStaffCode", StandardBasicTypes.STRING);
		query.addScalar("userChineseName", StandardBasicTypes.STRING);
		query.addScalar("peCheckFormId", StandardBasicTypes.INTEGER);
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("firmCode", StandardBasicTypes.INTEGER);
		query.addScalar("modifiedDate", StandardBasicTypes.DATE);
		query.addScalar("modifiedBy", StandardBasicTypes.STRING);
		query.addScalar("surveyMonth", StandardBasicTypes.STRING);
		query.addScalar("isCertaintyCase", StandardBasicTypes.BOOLEAN);
		query.addScalar("isSectionHead", StandardBasicTypes.BOOLEAN);
		query.addScalar("isFieldTeamHead", StandardBasicTypes.BOOLEAN);
		
		return query.list();
	}
	
	public long countPECheckTableList(String search	, Date referenceMonth, List<Integer> supervisorList
			, String certaintyCase, Integer roleHeader) {
		String sql = " select sm.referenceMonth as surveyMonth, "
				+ " a.referenceNo as referenceNo, "
				+ " pef.status as status, "
				+ " u.staffCode as userStaffCode, "
				+ " u.chineseName as userChineseName, "
				+ " pef.peCheckFormId as peCheckFormId, "
				+ " o.name as outletName, "
				+ " o.firmCode as firmCode, "
				+ " pef.modifiedDate as modifiedDate, "
				+ " pef.modifiedBy as modifiedBy, "
				+ " pe.isCertaintyCase as isCertaintyCase, "
				+ " pe.isSectionHead as isSectionHead, "
				+ " pe.isFieldTeamHead as isFieldTeamHead "
				+ " from PECheckForm pef "
				+ " inner join Assignment as a on a.AssignmentId = pef.AssignmentId"
				+ " left join PECheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join QuotationRecord as total on total.AssignmentId = a.AssignmentId"
					+ " and total.isBackNo = 0 and total.isBackTrack = 0"
				+ " left join QuotationRecord as approved on total.QuotationRecordId = approved.QuotationRecordId"
					+ " and approved.status = 'Approved'"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
				+ " left join [User] as u on u.userId = pef.officerId "
				+ " where 1 = 1 ";
		
		if (supervisorList != null && supervisorList.size() > 0){
			sql += " and u.supervisorId in (:supervisor) ";
		}
		
		if (referenceMonth != null) {
			sql += " and sm.referenceMonth = :referenceMonth ";
		}
		
		if("Y".equals(certaintyCase)){
			sql += " and pe.isCertaintyCase = 1";
		} else if ("N".equals(certaintyCase)){
			sql += " and (pe.isCertaintyCase is null or pe.isCertaintyCase <> 1)";
		}
		
		if (roleHeader != null && roleHeader==1){
			sql += " and pe.isSectionHead = 1";
		}
		
		if (roleHeader != null && roleHeader==2){
			sql += " and pe.isFieldTeamHead = 1";
		}

		if (!StringUtils.isEmpty(search)){
			sql += " and  (u.staffCode like :search or u.chineseName like :search "
					+ " or a.referenceNo like :search or o.name like :search) ";
		}
		
		sql += "group by sm.referenceMonth, a.referenceNo, pef.status, u.staffCode, u.chineseName"
				+ " , pef.peCheckFormId, o.name, o.firmCode, pef.modifiedDate, pef.modifiedBy"
				+ " , pe.isCertaintyCase , pe.isSectionHead, pe.isFieldTeamHead";
		
		sql += " having count(total.quotationRecordId) = count(approved.quotationRecordId)";
		
		String countsql = "Select count(*) from ("+sql+") as countSQL";
		
		SQLQuery query = this.getSession().createSQLQuery(countsql);
		
		if (referenceMonth != null) query.setParameter("referenceMonth", referenceMonth);
		
		if (supervisorList != null && supervisorList.size() > 0){
			query.setParameterList("supervisor", supervisorList);
		}
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));

		if (query.uniqueResult() == null) return 0;
		
		Integer count = (Integer)query.uniqueResult();
		
		return count.longValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<PECheckTableList> getOutstandingPECheckTableList(String search, int firstRecord, int displayLength, Order order
					, Date referenceMonth, List<Integer> supervisorList, String certaintyCase, Integer roleHeader) {
		String surveyMonth = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		String sql = " select case when sm.referenceMonth is null then '' else "+ surveyMonth +" end as surveyMonth, "
				+ " a.referenceNo as referenceNo, "
				+ " pef.status as status, "
				+ " u.staffCode as userStaffCode, "
				+ " u.chineseName as userChineseName, "
				+ " pef.peCheckFormId as peCheckFormId, "
				+ " o.name as outletName, "
				+ " o.firmCode as firmCode, "
				+ " pef.modifiedDate as modifiedDate, "
				+ " pef.modifiedBy as modifiedBy, "
				+ " pe.isCertaintyCase as isCertaintyCase, "
				+ " pe.isSectionHead as isSectionHead, "
				+ " pe.isFieldTeamHead as isFieldTeamHead "
				+ " from PECheckForm pef "
				+ " inner join Assignment as a on a.AssignmentId = pef.AssignmentId"
				+ " left join PECheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join QuotationRecord as total on total.AssignmentId = a.AssignmentId"
					+ " and total.isBackNo = 0 and total.isBackTrack = 0"
				+ " left join QuotationRecord as approved on total.QuotationRecordId = approved.QuotationRecordId"
					+ " and approved.status = 'Approved'"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
				+ " left join [User] as u on u.userId = pef.officerId "
				+ " where 1 = 1 "
				+ " and (pef.status not in ('Approved', 'Submitted') or pef.status is null)";
		
		if (supervisorList != null && supervisorList.size() > 0){
			sql += " and u.supervisorId in (:supervisor) ";
		}
		
		if (referenceMonth != null) {
			sql += " and sm.referenceMonth = :referenceMonth ";
		}

		if("Y".equals(certaintyCase)){
			sql += " and pe.isCertaintyCase = 1";
		} else if ("N".equals(certaintyCase)){
			sql += " and (pe.isCertaintyCase is null or pe.isCertaintyCase <> 1)";
		}
		
		if (roleHeader != null && roleHeader==1){
			sql += " and pe.isSectionHead = 1";
		}
		
		if (roleHeader != null && roleHeader==2){
			sql += " and pe.isFieldTeamHead = 1";
		}
		
		if (!StringUtils.isEmpty(search)){
			sql += " and  (u.staffCode like :search or u.chineseName like :search "
					+ " or a.referenceNo like :search or o.name like :search) ";
		}
		
		sql += "group by sm.referenceMonth, a.referenceNo, pef.status, u.staffCode, u.chineseName"
				+ " , pef.peCheckFormId, o.name, o.firmCode, pef.modifiedDate, pef.modifiedBy"
				+ " , pe.isCertaintyCase , pe.isSectionHead, pe.isFieldTeamHead";
		
		sql += " having count(total.quotationRecordId) = count(approved.quotationRecordId)";

		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc" : " desc");

		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		if (referenceMonth != null) query.setParameter("referenceMonth", referenceMonth);

		if (supervisorList != null && supervisorList.size() > 0){
			query.setParameterList("supervisor", supervisorList);
		}
		
		if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));
		query.setResultTransformer(Transformers.aliasToBean(PECheckTableList.class));

		query.addScalar("referenceNo", StandardBasicTypes.STRING);
		query.addScalar("status", StandardBasicTypes.STRING);
		query.addScalar("userStaffCode", StandardBasicTypes.STRING);
		query.addScalar("userChineseName", StandardBasicTypes.STRING);
		query.addScalar("peCheckFormId", StandardBasicTypes.INTEGER);
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("firmCode", StandardBasicTypes.INTEGER);
		query.addScalar("modifiedDate", StandardBasicTypes.DATE);
		query.addScalar("modifiedBy", StandardBasicTypes.STRING);
		query.addScalar("surveyMonth", StandardBasicTypes.STRING);
		query.addScalar("isCertaintyCase", StandardBasicTypes.BOOLEAN);
		query.addScalar("isSectionHead", StandardBasicTypes.BOOLEAN);
		query.addScalar("isFieldTeamHead", StandardBasicTypes.BOOLEAN);
		
		return query.list();
	}
	
	public long countOutstandingPECheckTableList(String search	, Date referenceMonth, List<Integer> supervisorList
			, String certaintyCase, Integer roleHeader, Date fromDate, Date toDate) {
		String sql = " select sm.referenceMonth as surveyMonth, "
				+ " a.referenceNo as referenceNo, "
				+ " pef.status as status, "
				+ " u.staffCode as userStaffCode, "
				+ " u.chineseName as userChineseName, "
				+ " pef.peCheckFormId as peCheckFormId, "
				+ " o.name as outletName, "
				+ " o.firmCode as firmCode, "
				+ " pef.modifiedDate as modifiedDate, "
				+ " pef.modifiedBy as modifiedBy, "
				+ " pe.isCertaintyCase as isCertaintyCase, "
				+ " pe.isSectionHead as isSectionHead, "
				+ " pe.isFieldTeamHead as isFieldTeamHead "
				+ " from PECheckForm pef "
				+ " inner join Assignment as a on a.AssignmentId = pef.AssignmentId"
				+ " left join PECheckTask as pe on a.AssignmentId = pe.AssignmentId"
				+ " left join QuotationRecord as total on total.AssignmentId = a.AssignmentId"
					+ " and total.isBackNo = 0 and total.isBackTrack = 0"
				+ " left join QuotationRecord as approved on total.QuotationRecordId = approved.QuotationRecordId"
					+ " and approved.status = 'Approved'"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
				+ " left join [User] as u on u.userId = pef.officerId "
				+ " where 1 = 1 "
				+ " and (pef.status not in ('Approved', 'Submitted') or pef.status is null)"
				+ " and (sm.referenceMonth between :fromDate and :toDate)";
		
		if (supervisorList != null && supervisorList.size() > 0){
			sql += " and u.supervisorId in (:supervisor) ";
		}
		
		if (referenceMonth != null) {
			sql += " and sm.referenceMonth = :referenceMonth ";
		}
		
		if("Y".equals(certaintyCase)){
			sql += " and pe.isCertaintyCase = 1";
		} else if ("N".equals(certaintyCase)){
			sql += " and (pe.isCertaintyCase is null or pe.isCertaintyCase <> 1)";
		}
		
		if (roleHeader != null && roleHeader==1){
			sql += " and pe.isSectionHead = 1";
		}
		
		if (roleHeader != null && roleHeader==2){
			sql += " and pe.isFieldTeamHead = 1";
		}

		if (!StringUtils.isEmpty(search)){
			sql += " and  (u.staffCode like :search or u.chineseName like :search "
					+ " or a.referenceNo like :search or o.name like :search) ";
		}
		
		sql += "group by sm.referenceMonth, a.referenceNo, pef.status, u.staffCode, u.chineseName"
				+ " , pef.peCheckFormId, o.name, o.firmCode, pef.modifiedDate, pef.modifiedBy"
				+ " , pe.isCertaintyCase , pe.isSectionHead, pe.isFieldTeamHead";
		
		sql += " having count(total.quotationRecordId) = count(approved.quotationRecordId)";
		
		String countsql = "Select count(*) from ("+sql+") as countSQL";
		
		SQLQuery query = this.getSession().createSQLQuery(countsql);
		
		if (referenceMonth != null) query.setParameter("referenceMonth", referenceMonth);
		
		if (supervisorList != null && supervisorList.size() > 0){
			query.setParameterList("supervisor", supervisorList);
		}
		
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);

		if (query.uniqueResult() == null) return 0;
		
		Integer count = (Integer)query.uniqueResult();
		
		return count.longValue();
	}
	
	public PECheckTaskModel getPECheckTaskModel(Integer userId, Integer surveyMonthId){
		
		String referenceMonth = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);

		String hql = " select u.userId as userId, "
				+ " u.staffCode + ' - ' + u.chineseName as fieldOfficer, "
				+ " sm.surveyMonthId as surveyMonthId, "
				+ " case when sm.referenceMonth is null then '' else " + referenceMonth + " end  as referenceMonth, "
				+ " max ( case pet.isFieldTeamHead when true then 1 else 0 end ) as isFieldTeamHead, "
				+ " max ( case pet.isSectionHead when true then 1 else 0 end ) as isSectionHead "
				+ " from PECheckTask pet "
				+ " left join pet.assignment as a "
				+ " left join pet.surveyMonth sm "
				+ " left join a.user as u "
				+ " where u.userId = :userId "
				+ " and sm.surveyMonthId = :surveyMonthId "
				+ " group by u.userId, u.staffCode, u.chineseName, sm.surveyMonthId, sm.referenceMonth ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("userId", userId);
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setMaxResults(1);
		
		query.setResultTransformer(Transformers.aliasToBean(PECheckTaskModel.class));

		return (PECheckTaskModel)query.uniqueResult();			
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getPEAssignmentLookupTableSelectAll(String search, Integer userId, Integer surveyMonthId
					, String outletTypeId, Integer productCategoryId, Integer districtId, Integer tpuId, 
					Integer[] ignoreAssignmentIds) {

				String collectionDate = String.format("FORMAT(a.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
						
				//String hql = " select count (distinct a.assignmentId) "
				String hql = " select a.assignmentId "
						+ " from Assignment a "
						+ " left join a.surveyMonth as sm "
						+ " left join a.quotationRecords as qr "
						+ " left join a.quotationRecords as qra "
						+ " left join qr.quotation as q "
						+ " left join q.batch as b "
						+ " left join a.outlet as o "
						+ " left join o.tpu as t "
						+ " left join t.district as d "
						+ " left join o.outletTypes as ot "
						+ " left join q.product as p "
						+ " left join p.productGroup as pg "
						+ " left join a.user as u "
						+ " where qra.status = :quotationRecordStatus ";
				
				if(userId != null) hql += " and u.userId = :userId ";

				if(ignoreAssignmentIds != null && ignoreAssignmentIds.length > 0) hql += " and a.assignmentId not in (:ignoreAssignmentIds) ";
				
				if(surveyMonthId != null) hql += " and sm.surveyMonthId = :surveyMonthId ";
				
				if(outletTypeId != null && outletTypeId.length() > 0) hql += " and ot.shortCode = :outletTypeId ";
				
				if(productCategoryId != null) hql += " and pg.productGroupId = :productCategoryId ";
				
				if(districtId != null) hql += " and d.districtId = :districtId ";
				
				if(tpuId != null) hql += " and t.tpuId = :tpuId ";
						
				if (!StringUtils.isEmpty(search)){
					hql += " and  (o.name like :search or b.code like :search or d.chineseName like :search or t.code like :search or o.streetAddress like :search "
						+ " or ( "
						+ "   case "
						+ "   when a.collectionDate is null then '' "
						+ "   else " + collectionDate + " end ) like :search "
						+ " ) ";
				}
				//hql += " group by u.userId ";
				hql += " group by sm.surveyMonthId, a.assignmentId, o.name, d.chineseName, t.code, o.detailAddress, o.streetAddress, a.collectionDate ";
				hql += " having count(qr.quotationRecordId) = count(qra.quotationRecordId) ";
				
				String countHql = "select count(*) from Assignment as a where a.assignmentId in ("+hql+") ";

				Query query = this.getSession().createQuery(countHql);
				
				if (userId != null) query.setParameter("userId", userId);

				if(ignoreAssignmentIds != null && ignoreAssignmentIds.length > 0) query.setParameterList("ignoreAssignmentIds", ignoreAssignmentIds);

				if (surveyMonthId != null) query.setParameter("surveyMonthId", surveyMonthId);
				
				if (outletTypeId != null && outletTypeId.length() > 0) query.setParameter("outletTypeId", outletTypeId);
				
				if (productCategoryId != null) query.setParameter("productCategoryId", productCategoryId);
				
				if(districtId != null) query.setParameter("districtId", districtId);
				
				if(tpuId != null) query.setParameter("tpuId", tpuId);

				if (!StringUtils.isEmpty(search)) query.setParameter("search", String.format("%%%s%%", search));

				query.setParameter("quotationRecordStatus", SystemConstant.QUOTATIONRECORD_STATUS_APPROVED);
				
				return query.list();
			}
	
	public List<CheckTheChecker> getCheckTheCheckerReport(Date refMonth, int noOfCases) {

		SQLQuery query = this.getSession().createSQLQuery("exec [GetCheckTheChecker] :refMonth, :noOfCases");
		
		query.setParameter("refMonth", refMonth);
		query.setParameter("noOfCases", noOfCases);
		
		query.addScalar("peCheckTaskId", StandardBasicTypes.INTEGER);
		query.addScalar("firmNo", StandardBasicTypes.INTEGER);
		query.addScalar("firmName", StandardBasicTypes.STRING);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("contactPerson", StandardBasicTypes.STRING);
		query.addScalar("enumerationDate", StandardBasicTypes.STRING);
		query.addScalar("enumerationBy", StandardBasicTypes.STRING);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("peCheckDate", StandardBasicTypes.STRING);
		query.addScalar("peCheckBy", StandardBasicTypes.STRING);
		query.addScalar("outletId", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(CheckTheChecker.class));
		
		return query.list();
	}
	
	public List<CheckTheChecker.OutletTypeMapping> getOutletTypeOfCheckTheCheckerReport(Date refMonth) {
		
		String hql = "select pt.peCheckTaskId as peCheckTaskId "
				+ ", ot.shortCode as shortCode "
				
				+ " from PECheckTask as pt "
				+ " left join pt.assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join a.peCheckForm as pf "
				+ " left join a.quotationRecords as qr "
				+ " left join a.outlet as o "
				+ " left join a.user as staff "
				+ " left join pf.user as supervisor "
				+ " left join o.outletTypes as ot "
				
				+ " where pt.isRandomCase = 1 and pt.isFieldTeamHead = 0 and pt.isSectionHead = 0 and pf.status = 'Submitted' "
				+ "  and sm.referenceMonth = :refMonth "
				
				+ " group by pt.peCheckTaskId, ot.shortCode ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("refMonth", refMonth);
		
		query.setResultTransformer(Transformers.aliasToBean(CheckTheChecker.OutletTypeMapping.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getExcludedPEOutletId(Date excludeMonth){
		Criteria criteria = this.createCriteria("pe")
				.createAlias("pe.surveyMonth", "sm", JoinType.INNER_JOIN)
				.createAlias("pe.assignment", "a", JoinType.INNER_JOIN)
				.createAlias("a.outlet", "o", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("o.outletId"), "outletId");

		criteria.setProjection(Projections.distinct(projList));
		
		criteria.add(Restrictions.ge("sm.referenceMonth", excludeMonth));
		
		return criteria.list();
	}
	
	public List<PECheckTask> getUnGeneratedPECheckTask(){
		
		String hql = "select pe "
				+ " from PECheckTask as pe "
				+ " where pe.assignment.assignmentId in ( "
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
	
	public long countSelectedCertaintyCase(int surveyMonthId){
		String hql = "select count(pe.peCheckTaskId)"
				+ " from PECheckTask as pe"
				+ " where pe.isSelected = true and pe.isCertaintyCase = true"
				+ " and pe.surveyMonth.surveyMonthId = :surveyMonthId";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("surveyMonthId", surveyMonthId);
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<PECheckTaskList> getPECheckFormListDetail(Integer[] assignmentIds, Integer surveyMonthId) {
		String sql = "Select a.AssignmentId as assignmentId"
				+ " , pe.PECheckTaskId as peCheckTaskId"
				+ " , o.name as firm"
				+ " , t.Code as tpu"
				+ " , d.chineseName as district"
				+ " , o.tel as tel"
				+ " , o.streetAddress as address"
				+ " , count(qr.QuotationRecordId) as noOfQuotation"
				+ " , case when pe.isFieldTeamHead is null then 0 else pe.isFieldTeamHead end as isFieldTeamHead "
				+ " , case when pe.isSectionHead is null then 0 else pe.isSectionHead end as isSectionHead "
				+ " , case when pe.isCertaintyCase is null then 0 else pe.isCertaintyCase end as isCertaintyCase "
				+ " , case when pe.isRandomCase is null then 0 else pe.isRandomCase end as isRandomCase "
				+ " , form.peCheckFormId as peCheckFormId"
				+ " , form.status as status"
				+ " from Assignment as a "
				+ " left join PECheckTask as pe on pe.AssignmentId = a.AssignmentId"
				+ " left join PECheckForm as form on a.AssignmentId = form.AssignmentId"
				+ " left join QuotationRecord as qr on qr.AssignmentId = a.AssignmentId"
					+ " and qr.IsBackNo = 0"
				+ " left join Outlet as o on a.OutletId = o.OutletId"
				+ " left join Tpu as t on t.TpuId = o.TpuId"
				+ " left join District as d on d.DistrictId = t.DistrictId"
				+ " where a.AssignmentId in :assignmentIds"
				+ " and a.surveyMonthId = :surveyMonthId"
				+ " group by a.AssignmentId, pe.PECheckTaskId, pe.IsSectionHead, pe.IsFieldTeamHead"
				+ " , o.name, t.Code, d.chineseName, o.tel, o.streetAddress, pe.isCertaintyCase, pe.isRandomCase"
				+ " , form.peCheckFormId, form.status";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameterList("assignmentIds", assignmentIds);
		
		query.addScalar("firm", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("noOfQuotation", StandardBasicTypes.LONG);
		query.addScalar("tel", StandardBasicTypes.STRING);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("peCheckTaskId", StandardBasicTypes.INTEGER);
		query.addScalar("isSectionHead", StandardBasicTypes.BOOLEAN);
		query.addScalar("isFieldTeamHead", StandardBasicTypes.BOOLEAN);
		query.addScalar("isCertaintyCase", StandardBasicTypes.BOOLEAN);
		query.addScalar("isRandomCase", StandardBasicTypes.BOOLEAN);
		query.addScalar("peCheckFormId", StandardBasicTypes.INTEGER);
		query.addScalar("status", StandardBasicTypes.STRING);
	
		query.setResultTransformer(Transformers.aliasToBean(PECheckTaskList.class));

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getFullPECheckListForCheckTheCheckerReport(Date refMonth) {
		String sql = "select distinct staff.Team as team "
				+ "from PECheckTask as pt "
				+ "left join Assignment as a on pt.AssignmentId = a.AssignmentId "
				+ "left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId "
				+ "left join PECheckForm as pf on a.AssignmentId = pf.AssignmentId "
				+ "left join QuotationRecord as qr on a.AssignmentId = qr.AssignmentId "
				+ "left join Outlet as o on a.OutletId = o.OutletId "
				+ "left join [User] as staff on a.UserId = staff.UserId "
				+ "left join [User] as supervisor on pf.UserId = supervisor.UserId "
				+ "left join OutletTypeOutlet as ot on o.OutletId = ot.OutletId "
				+ "where pt.IsRandomCase = 1 "
				+ "and pf.Status = 'Submitted' "
				+ "and pt.IsFieldTeamHead = 0 "
				+ "and pt.IsSectionHead = 0 "
				+ "and sm.ReferenceMonth = :refMonth";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("refMonth", refMonth);
		query.addScalar("team", StandardBasicTypes.STRING);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<PECheckSummaryReport> getPECheckSummaryReportByUserReferenceMonth(Integer[] userIds, Date fromMonth, Date toMonth) {
	
		String sql = "Select u.userId as officerId"
				+ ", count(distinct total.assignmentId) as total"
				+ ", count(distinct excluded.AssignmentId) as totalFirmEnumerated"
				+ ", count(distinct checked.PECheckFormId) as noOfFirmsChecked"
				+ ", count(distinct nonContact.PECheckFormId) as noOfFirmsNC"
				+ " from [User] as u"
				+ " cross join SurveyMonth as sm"
				+ " left join Assignment as total on u.userId = total.userId"
					+ " and sm.SurveyMonthId = total.SurveyMonthId";
				sql += (fromMonth.equals(toMonth)) ? " and sm.ReferenceMonth = :fromMonth " : " and sm.ReferenceMonth > :fromMonth and sm.ReferenceMonth < :toMonth ";
				sql += " left join"
					+ " (Select a.AssignmentId from Assignment as a "
					+ " left join QuotationRecord as qr on a.AssignmentID = qr.AssignmentId"
					+ " left join Quotation as q on q.QuotationId = qr.QuotationId"
					+ " left join Unit as u on q.UnitId = u.UnitId"
					+ " left join SubItem as si on si.SubItemId = u.SubItemId"
					+ " left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId"
					+ " left join Purpose as pp on u.PurposeId = pp.PurposeId"
					+ " where "
					+ " pp.PEIncluded = 1"
					+ " and SUBSTRING(ot.[Code], len(ot.[code])-2, 3) not in (Select eot.[shortCode] from PEExcludedOutletType as eot)"
					+ " group by a.AssignmentId) as excluded on excluded.AssignmentId = total.AssignmentId"
				+ " left join PECheckForm as checked on checked.AssignmentId = total.AssignmentId"
					+ " and checked.IsNonContact = 0 and checked.Status = 'Submitted'"
				+ " left join PECheckForm as nonContact on nonContact.AssignmentId = total.AssignmentId "
					+ " and nonContact.IsNonContact = 1 and nonContact.Status = 'Submitted'"
				+ " left join userrole as ur on ur.UserId = u.UserId "
				+ " left join [Role] as r on ur.RoleId = r.RoleId "
				+ " inner join QuotationRecord as qr on qr.AssignmentId = total.AssignmentId"
				+ " where r.AuthorityLevel & 16 = 16 and u.UserId in (:userIds)";
		
		sql += " group by u.userId ";
		sql += " order by u.UserId asc ";
		
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameterList("userIds", userIds);
		query.setParameter("fromMonth", fromMonth);
		if(!fromMonth.equals(toMonth)) { query.setParameter("toMonth", toMonth); }

		query.addScalar("officerId", StandardBasicTypes.INTEGER);
		query.addScalar("noOfFirmsChecked", StandardBasicTypes.INTEGER);
		query.addScalar("noOfFirmsNC", StandardBasicTypes.INTEGER);
		query.addScalar("totalFirmEnumerated", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(PECheckSummaryReport.class));

		return query.list();
	}
}
