package capi.dal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.stereotype.Repository;

import capi.dal.utils.SQLProjectionExt;
import capi.entity.AllocationBatch;
import capi.entity.Assignment;
import capi.entity.IndoorQuotationRecord;
import capi.entity.Outlet;
import capi.entity.Quotation;
import capi.entity.QuotationRecord;
import capi.entity.SurveyMonth;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.api.dataSync.QuotationRecordSyncData;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.generation.QuotationRecordGroupingModel;
import capi.model.assignmentManagement.QuotationRecordHistoryDateModel;
import capi.model.assignmentManagement.QuotationRecordTableList;
import capi.model.assignmentManagement.assignmentManagement.BackTrackDateModel;
import capi.model.assignmentManagement.assignmentManagement.QuotationRecordCountByTabModel;
import capi.model.assignmentManagement.assignmentManagement.VerificationTypeGroupModel;
import capi.model.batch.QRDelinkReminderModel;
import capi.model.commonLookup.QuotationRecordHistoryTableListModel;
import capi.model.dashboard.DeadlineRowModel;
import capi.model.report.FieldworkOutputByDistrictReport;
import capi.model.report.FieldworkOutputByOutletTypeReport;
import capi.model.report.IndividualQuotationRecordReport;
import capi.model.report.IndividualQuotationRecordReport1;
import capi.model.report.IndividualQuotationRecordReport2;
import capi.model.report.IndividualQuotationRecordReport3;
import capi.model.report.IndividualQuotationRecordReport4;
import capi.model.report.IndividualQuotationRecordReport5;
import capi.model.report.IndividualQuotationRecordReportImage;
import capi.model.report.QuotationRecordProgress;
import capi.model.report.RUACaseReportIndividualReport;
import capi.model.report.RUACaseReportOverviewReport;


@Repository("QuotationRecordDao")
public class QuotationRecordDao  extends GenericDao<QuotationRecord>{

	@SuppressWarnings("unchecked")
	public List<QuotationRecordTableList> getTableList(String search,
			int firstRecord, int displayLenght, Order order,
			Integer surveyMonthId, Integer purposeId,
			Integer outletId, String[] outletTypeId, Integer[] unitId, Integer[] districtId, Integer[] tpuId, String status, Integer[] userId) {

		String endDateFormat = String.format("FORMAT(a.EndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.AssignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "select r.QuotationRecordId as id, "
				+ " case when a.AssignedCollectionDate is null and a.EndDate is not null then " + endDateFormat + " "
				+ "   when a.AssignedCollectionDate is not null and a.EndDate is null then " + assignedCollectionDateFormat + " "
				+ "   else '' end as deadline, "
				+ " case when min(a.AssignedCollectionDate) is null then min(a.EndDate) else min(a.AssignedCollectionDate) end as deadline2, "
				+ " u.Code as unitCode, "
				+ " u.ChineseName as unitName, "
				+ " concat(pa.SpecificationName , '=' , ps.Value) as productAttribute1, "
				+ " concat(pa1.SpecificationName , '=' , ps1.Value) as productAttribute2, "
				+ " concat(pa2.SpecificationName , '=' , ps2.Value) as productAttribute3, "
				+ " concat(pa3.SpecificationName , '=' , ps3.Value) as productAttribute4, "
				+ " concat(pa4.SpecificationName , '=' , ps4.Value) as productAttribute5, "
				+ " o.OutletId as firmId, "
				+ " o.Name as firmName, "
				+ " substring(ot.Code, len(ot.Code)-2, 3) as outletType, "
				+ " o.StreetAddress as mapAddress, "
				+ " o.DetailAddress as detailAddress, "
				+ " d.Code as districtCode, "
				+ " tpu.Code as tpu,"
				+ " r.Status as status, "
				+ " r.NPrice as nPrice, r.SPrice as sPrice, r.Discount as discount, "
				+ " case when count(distinct sp.SubPriceRecordId) > 0 then 1 else 0 end as subPrice, "
				+ " r.Remark as remark, r.IsBackTrack as isBackTrack,"
				+ " q.QuotationId as quotationId, "
				+ " user_.EnglishName as officer "
				
                + " from QuotationRecord as r "
                + " inner join Assignment as a on r.AssignmentId = a.AssignmentId "
                + " inner join SurveyMonth as s on a.SurveyMonthId = s.SurveyMonthId "
                + " left join Outlet as o on r.OutletId = o.OutletId "
                + " left outer join Tpu as tpu on o.TpuId = tpu.TpuId "
                + " left outer join District as d on tpu.DistrictId = d.DistrictId "
                + " left outer join Quotation as q on r.QuotationId = q.QuotationId "
                + " left outer join Unit as u on q.UnitId = u.UnitId "
                + " left outer join SubItem as si on u.SubItemId = si.SubItemId "
                + " left outer join OutletType as ot on si.OutletTypeId = ot.OutletTypeId "
                + " left outer join Product as p on r.ProductId = p.ProductId "
				+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 2 "
				+ " left outer join ProductSpecification as ps1 on ps1.ProductAttributeId = pa1.ProductAttributeId and ps1.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.Sequence = 3 "
				+ " left outer join ProductSpecification as ps2 on ps2.ProductAttributeId = pa2.ProductAttributeId and ps2.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.Sequence = 4 "
				+ " left outer join ProductSpecification as ps3 on ps3.ProductAttributeId = pa3.ProductAttributeId and ps3.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.Sequence = 5 "
				+ " left outer join ProductSpecification as ps4 on ps4.ProductAttributeId = pa4.ProductAttributeId and ps4.ProductId = p.ProductId "
                + " left outer join SubPriceRecord as sp on r.QuotationRecordId = sp.QuotationRecordId "
                + " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
                + " left outer join [User] as user_ on r.UserId = user_.UserId "
                
                + " where 1 = 1 ";
		
		sql += " and r.IsBackNo = 0 ";
				//+ " and r.status in ('Blank', 'Draft', 'Submitted', 'Rejected') ";
		sql += " and s.SurveyMonthId = :surveyMonthId ";
		
//		hql += " and ( "
//				+ " (r.collectionDate is not null and r.collectionDate between :startDate and :endDate) "
//				+ " or (r.assignedStartDate is not null and (r.assignedStartDate between :startDate and :endDate or r.assignedEndDate between :startDate and :endDate or (r.assignedStartDate <= :startDate and r.assignedEndDate >= :endDate) )) "
//				+ " or (r.assignedCollectionDate is not null and r.assignedCollectionDate between :startDate and :endDate) "
//				+ " ) ";
		if (purposeId != null) {
			sql += " and purpose.PurposeId = :purposeId ";
		}
		if (outletId != null) {
			sql += " and o.OutletId = :outletId ";
		}
		if (outletTypeId != null) {
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in :outletTypeId ";
		}
		if (unitId != null) {
			sql += " and u.UnitId in :unitId ";
		}
		if (districtId != null) {
			sql += " and d.DistrictId in :districtId ";
		}
		if (tpuId != null) {
			sql += " and tpu.TpuId in :tpuId ";
		}
		if (StringUtils.isNotEmpty(status)) {
			sql += " and r.Status = :status ";
		}
		if (userId != null && userId.length > 0) {
			sql += " and user_.UserId in :userId ";
		}
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
                + " " + endDateFormat + " like :search or "
                + " " + assignedCollectionDateFormat + " like :search or "
        		+ " u.Code like :search or u.ChineseName like :search or "
                + " pa.SpecificationName like :search or ps.Value like :search or "
                + " pa1.SpecificationName like :search or ps1.Value like :search or "
                + " pa2.SpecificationName like :search or ps2.Value like :search or "
                + " pa3.SpecificationName like :search or ps3.Value like :search or "
                + " pa4.SpecificationName like :search or ps4.Value like :search or "
                + " str(o.OutletId) like :search or o.Name like :search or "
                + " ot.Code like :search or ot.ChineseName like :search or "
                + " o.StreetAddress like :search or o.DetailAddress like :search or "
                + " d.Code like :search or tpu.Code like :search or "
                + " r.Status like :search or "
                + " str(r.NPrice) like :search or str(r.SPrice) like :search or r.Discount like :search or "
                + " r.Remark like :search or "
                + " str(r.QuotationRecordId) like :search or "
                + " str(q.QuotationId) like :search "
                + " ) ";
		}
		
		sql += " group by r.QuotationRecordId, "
                + " a.AssignedCollectionDate, a.EndDate, "
                + " pa.SpecificationName, ps.Value, "
                + " pa1.SpecificationName, ps1.Value, "
                + " pa2.SpecificationName, ps2.Value, "
                + " pa3.SpecificationName, ps3.Value, "
                + " pa4.SpecificationName, ps4.Value, "
                + " u.Code, u.ChineseName, "
                + " o.OutletId, o.Name, "
                + " ot.Code, ot.ChineseName, "
                + " o.StreetAddress, o.DetailAddress, "
                + " d.Code, tpu.Code, "
                + " r.Status, "
                + " r.NPrice, r.SPrice, r.Discount, r.Remark, r.IsBackTrack, "
                + " q.QuotationId, "
                + " user_.EnglishName "
                + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		SQLQuery query = getSession().createSQLQuery(sql);

//		query.setParameter("startDate", startDate);
//		query.setParameter("endDate", endDate);
		query.setParameter("surveyMonthId", surveyMonthId);
		if (purposeId != null) {
			query.setParameter("purposeId", purposeId);
		}
		if (outletId != null) {
			query.setParameter("outletId", outletId);
		}
		if (outletTypeId != null) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (unitId != null) {
			query.setParameterList("unitId", unitId);
		}
		if (districtId != null) {
			query.setParameterList("districtId", districtId);
		}
		if (tpuId != null) {
			query.setParameterList("tpuId", tpuId);
		}
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if (userId != null && userId.length > 0) {
			query.setParameterList("userId", userId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordTableList.class));
		
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("deadline", StandardBasicTypes.STRING);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("unitName", StandardBasicTypes.STRING);
		query.addScalar("productAttribute1", StandardBasicTypes.STRING);
		query.addScalar("productAttribute2", StandardBasicTypes.STRING);
		query.addScalar("productAttribute3", StandardBasicTypes.STRING);
		query.addScalar("productAttribute4", StandardBasicTypes.STRING);
		query.addScalar("productAttribute5", StandardBasicTypes.STRING);
		query.addScalar("firmId", StandardBasicTypes.INTEGER);
		query.addScalar("firmName", StandardBasicTypes.STRING);
		query.addScalar("outletType", StandardBasicTypes.STRING);
		query.addScalar("mapAddress", StandardBasicTypes.STRING);
		query.addScalar("detailAddress", StandardBasicTypes.STRING);
		query.addScalar("districtCode", StandardBasicTypes.STRING);
		query.addScalar("tpu", StandardBasicTypes.STRING);
		query.addScalar("status", StandardBasicTypes.STRING);
		query.addScalar("nPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("sPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("discount", StandardBasicTypes.STRING);
		query.addScalar("subPrice", StandardBasicTypes.BOOLEAN);
		query.addScalar("remark", StandardBasicTypes.STRING);
		query.addScalar("isBackTrack", StandardBasicTypes.BOOLEAN);
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		query.addScalar("officer", StandardBasicTypes.STRING);

		return query.list();
	}
	
	public long countTableList(String search,
			Integer surveyMonthId, Integer purposeId,
			Integer outletId, String[] outletTypeId, Integer[] unitId, Integer[] districtId, Integer[] tpuId, String status, Integer[] userId) {

		String endDateFormat = String.format("FORMAT(a.EndDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String assignedCollectionDateFormat = String.format("FORMAT(a.AssignedCollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "select count(distinct r.QuotationRecordId) "
				+ " from QuotationRecord as r "
				+ " inner join Assignment as a on r.AssignmentId = a.AssignmentId "
				+ " inner join SurveyMonth as s on a.SurveyMonthId = s.SurveyMonthId "
				+ " left join Outlet as o on r.OutletId = o.OutletId "
				+ " left outer join Tpu as tpu on o.TpuId = tpu.TpuId "
				+ " left outer join District as d on tpu.DistrictId = d.DistrictId "
				+ " left outer join Quotation as q on r.QuotationId = q.QuotationId "
				+ " left outer join Unit as u on q.UnitId = u.UnitId "
				+ " left outer join SubItem as si on u.SubItemId = si.SubItemId "
				+ " left outer join OutletType as ot on si.OutletTypeId = ot.OutletTypeId "
				+ " left outer join Product as p on r.ProductId = p.ProductId "
				+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 2 "
				+ " left outer join ProductSpecification as ps1 on ps1.ProductAttributeId = pa1.ProductAttributeId and ps1.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.Sequence = 3 "
				+ " left outer join ProductSpecification as ps2 on ps2.ProductAttributeId = pa2.ProductAttributeId and ps2.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.Sequence = 4 "
				+ " left outer join ProductSpecification as ps3 on ps3.ProductAttributeId = pa3.ProductAttributeId and ps3.ProductId = p.ProductId "
				+ " left outer join ProductAttribute as pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.Sequence = 5 "
				+ " left outer join ProductSpecification as ps4 on ps4.ProductAttributeId = pa4.ProductAttributeId and ps4.ProductId = p.ProductId "
				+ " left outer join SubPriceRecord as sp on r.QuotationRecordId = sp.QuotationRecordId "
				+ " left outer join Purpose as purpose on u.PurposeId = purpose.PurposeId "
				+ " left outer join [User] as user_ on r.UserId = user_.UserId "
                
                + " where 1 = 1 ";
		
		sql += " and r.IsBackNo = 0 ";
				//+ " and r.status in ('Blank', 'Draft', 'Submitted', 'Rejected') ";

		sql += " and s.SurveyMonthId = :surveyMonthId ";
		
//		hql += " and ( "
//				+ " (r.collectionDate is not null and r.collectionDate between :startDate and :endDate) "
//				+ " or (r.assignedStartDate is not null and (r.assignedStartDate between :startDate and :endDate or r.assignedEndDate between :startDate and :endDate or (r.assignedStartDate <= :startDate and r.assignedEndDate >= :endDate) )) "
//				+ " or (r.assignedCollectionDate is not null and r.assignedCollectionDate between :startDate and :endDate) "
//				+ " ) ";
		if (purposeId != null) {
			sql += " and purpose.PurposeId = :purposeId ";
		}
		if (outletId != null) {
			sql += " and o.OutletId = :outletId ";
		}
		if (outletTypeId != null) {
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in :outletTypeId ";
		}
		if (unitId != null) {
			sql += " and u.UnitId in :unitId ";
		}
		if (districtId != null) {
			sql += " and d.DistrictId in :districtId ";
		}
		if (tpuId != null) {
			sql += " and tpu.TpuId in :tpuId ";
		}
		if (StringUtils.isNotEmpty(status)) {
			sql += " and r.Status = :status ";
		}
		if (userId != null && userId.length > 0) {
			sql += " and user_.UserId in :userId ";
		}
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
                + " " + endDateFormat + " like :search or "
                + " " + assignedCollectionDateFormat + " like :search or "
        		+ " u.Code like :search or u.ChineseName like :search or "
                + " pa.SpecificationName like :search or ps.Value like :search or "
                + " pa1.SpecificationName like :search or ps1.Value like :search or "
                + " pa2.SpecificationName like :search or ps2.Value like :search or "
                + " pa3.SpecificationName like :search or ps3.Value like :search or "
                + " pa4.SpecificationName like :search or ps4.Value like :search or "
                + " str(o.OutletId) like :search or o.Name like :search or "
                + " ot.Code like :search or ot.ChineseName like :search or "
                + " o.StreetAddress like :search or o.DetailAddress like :search or "
                + " d.Code like :search or tpu.Code like :search or "
                + " r.Status like :search or "
                + " str(r.NPrice) like :search or str(r.SPrice) like :search or r.Discount like :search or "
                + " r.Remark like :search or "
                + " str(r.QuotationRecordId) like :search or "
                + " str(q.QuotationId) like :search "
                + " ) ";
		}

		SQLQuery query = getSession().createSQLQuery(sql);

//		query.setParameter("startDate", startDate);
//		query.setParameter("endDate", endDate);
		query.setParameter("surveyMonthId", surveyMonthId);
		if (purposeId != null) {
			query.setParameter("purposeId", purposeId);
		}
		if (outletId != null) {
			query.setParameter("outletId", outletId);
		}
		if (outletTypeId != null) {
			query.setParameterList("outletTypeId", outletTypeId);
		}
		if (unitId != null) {
			query.setParameterList("unitId", unitId);
		}
		if (districtId != null) {
			query.setParameterList("districtId", districtId);
		}
		if (tpuId != null) {
			query.setParameterList("tpuId", tpuId);
		}
		if (StringUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}
		if (userId != null && userId.length > 0) {
			query.setParameterList("userId", userId);
		}
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public QuotationRecord getByIdWithRelated(int id) {
		Criteria c = this.createCriteria().add(Restrictions.eq("id", id));
		c.setFetchMode("outlet", FetchMode.JOIN);
		c.setFetchMode("outlet.outletTypes", FetchMode.JOIN);
		c.setFetchMode("outlet.tpu", FetchMode.JOIN);
		c.setFetchMode("outlet.tpu.district", FetchMode.JOIN);
		c.setFetchMode("outlet.unit", FetchMode.JOIN);
		c.setFetchMode("outlet.unitCategoryInfo", FetchMode.JOIN);
		c.setFetchMode("subPriceRecords", FetchMode.JOIN);
		c.setFetchMode("subPriceRecords.subPriceType", FetchMode.JOIN);
		c.setFetchMode("quotation", FetchMode.JOIN);
		c.setFetchMode("quotation.unit", FetchMode.JOIN);
		c.setFetchMode("quotation.unit.standardUOM", FetchMode.JOIN);
		c.setFetchMode("uom", FetchMode.JOIN);
		c.setFetchMode("product", FetchMode.JOIN);
		return (QuotationRecord)c.uniqueResult();
	}
	
	public QuotationRecord getBackNoRecord(int id) {
		Criteria c = this.createCriteria().add(Restrictions.eq("originalQuotationRecord.id", id)).add(Restrictions.eq("isBackNo", true));
		c.setFetchMode("outlet", FetchMode.JOIN);
		c.setFetchMode("outlet.outletTypes", FetchMode.JOIN);
		c.setFetchMode("outlet.tpu", FetchMode.JOIN);
		c.setFetchMode("outlet.tpu.district", FetchMode.JOIN);
		c.setFetchMode("subPriceRecords", FetchMode.JOIN);
		c.setFetchMode("subPriceRecords.subPriceType", FetchMode.JOIN);
		return (QuotationRecord)c.uniqueResult();
	}
	
//	@SuppressWarnings("unchecked")
//	public List<QuotationRecordHistoryDateModel> getHistoryDatesAndRecordId(int quotationId, Date historyDate, Integer limit, List<Integer> skipQuotationRecordIds) {
//		String collectionDateFormat = String.format("FORMAT(a.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
//		
//		String hql = " select case when a.referenceDate is not null then " + collectionDateFormat + " else '' end as date, "
//				+ " min(a.id) as id "
//				+ " from QuotationRecord as a "
//				+ " where a.quotation.id = :quotationId"
//				+ " and isBackNo = false"
//				+ " and isBackTrack = false "
//				+ " and a.referenceDate < :historyDate ";
//		
//		if (skipQuotationRecordIds != null && skipQuotationRecordIds.size() > 0) {
//			hql += " and a.id not in :skipQuotationRecordIds ";
//		}
//		
//		hql += " group by a.referenceDate ";
//		
//		hql += " order by a.referenceDate desc ";
//		
//		Query query = getSession().createQuery(hql);
//		query.setParameter("quotationId", quotationId);
//		query.setParameter("historyDate", historyDate);
//		
//		if (skipQuotationRecordIds != null && skipQuotationRecordIds.size() > 0) {
//			query.setParameterList("skipQuotationRecordIds", skipQuotationRecordIds);
//		}
//		
//		query.setMaxResults(limit);
//		
//		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordHistoryDateModel.class));
//        
//        return query.list();
//	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecordHistoryDateModel> getHistoryDatesAndRecordId(int quotationId, Date historyDate, Integer limit, List<Integer> skipQuotationRecordIds, boolean isYear) {
		String collectionDateFormat = String.format("FORMAT(a.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select case when a.referenceDate is not null then " + collectionDateFormat + " else '' end as date, "
				+ " min(a.id) as id "
				+ " from QuotationRecord as a "
				+ " where a.quotation.id = :quotationId"
				+ " and isBackNo = false"
				+ " and a.referenceDate < :historyDate ";
		
		if(isYear == true){
			hql += " and isBackTrack = false ";
		}
		
		if (skipQuotationRecordIds != null && skipQuotationRecordIds.size() > 0) {
			hql += " and a.id not in :skipQuotationRecordIds ";
		}
		
		hql += " group by a.referenceDate ";
		
		hql += " order by a.referenceDate desc ";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("quotationId", quotationId);
		query.setParameter("historyDate", historyDate);
		
		if (skipQuotationRecordIds != null && skipQuotationRecordIds.size() > 0) {
			query.setParameterList("skipQuotationRecordIds", skipQuotationRecordIds);
		}
		
		query.setMaxResults(limit);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordHistoryDateModel.class));
        
        return query.list();
	}
	

	@SuppressWarnings("unchecked")
	public List<QuotationRecordHistoryDateModel> getHistoryDateRangeAndRecordId(int quotationId, Date historyDateStart, Date historyDateEnd, Integer limit, List<Integer> skipQuotationRecordIds, boolean isYear) {
		String collectionDateFormat = String.format("FORMAT(a.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select case when a.referenceDate is not null then " + collectionDateFormat + " else '' end as date, "
				+ " min(a.id) as id "
				+ " from QuotationRecord as a "
				+ " where a.quotation.id = :quotationId"
				+ " and isBackNo = false"
				+ " and a.referenceDate between :historyDateStart and :historyDateEnd ";
		
		if(isYear == true){
			hql += " and isBackTrack = false ";
		}
		
		if (skipQuotationRecordIds != null && skipQuotationRecordIds.size() > 0) {
			hql += " and a.id not in :skipQuotationRecordIds ";
		}
		
		hql += " group by a.referenceDate ";
		
		hql += " order by a.referenceDate desc ";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("quotationId", quotationId);
		query.setParameter("historyDateStart", historyDateStart);
		query.setParameter("historyDateEnd", historyDateEnd);
		
		if (skipQuotationRecordIds != null && skipQuotationRecordIds.size() > 0) {
			query.setParameterList("skipQuotationRecordIds", skipQuotationRecordIds);
		}
		
		query.setMaxResults(limit);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordHistoryDateModel.class));
        
        return query.list();
	}	
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecordHistoryTableListModel> getHistoryDataList(String search, int quotationId, Integer firstRecord, Integer displayLength, Order order) {
		String refDateFormat = String.format("FORMAT(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String smonthFormat = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = "select iqr.referenceDate as submissionDate, "
				+ " 	iqr.indoorQuotationRecordId as id,"
				+ "		sm.referenceMonth as referenceMonth, "
				+ "		qr.nPrice as collectedNPrice, "
				+ "		qr.sPrice as collectedSPrice, "
				+ "		max(spr.subPriceRecordId) as subPriceId,"
				+ "		qr.quotationRecordId as quotationRecordId,"
				+ "		qr.discount as discount, "
				+ "		qr.availability as availability, "
				+ "		qr.isCollectFR as fr,"
				+ "		iqr.previousNPrice as previousNPrice,"
				+ "		iqr.previousSPrice as previousSPrice,"
				+ "		iqr.currentNPrice as currentNPrice,"
				+ "		iqr.currentSPrice as currentSPrice,"
				+ "		iqr.isFlag as isFlag,"
				+ "		iqr.remark as remark,"
				+ "		iqr.isProductChange as isProductChange,"
				+ "		qs.maxSPrice as max,"
				+ "		qs.minSPrice as min,"
				+ "		qs.averageCurrentSPrice as average, "
				+ "		qr.productId as productId " 
				+ " from "
				+ "		IndoorQuotationRecord as iqr "
				+ "		left join Quotation q "
				+ "			on iqr.quotationId = q.quotationId "
				+ " 	left join QuotationRecord qr "
				+ "			on qr.quotationRecordId = iqr.quotationRecordId "
				+ " 	left join Assignment a "
				+ "			on a.assignmentId = qr.assignmentId "
				+ " 	left join SurveyMonth sm "
				+ "			on sm.surveyMonthId = a.surveyMonthId "
				+ "		left join SubPriceRecord spr "
				+ "			on spr.quotationRecordId = qr.quotationRecordId "
				+ "		left join QuotationStatistic qs "
				+ "			on qs.quotationId = q.quotationId and qs.referenceMonth = sm.referenceMonth "
				+ " where "
				+ "		q.quotationId = :quotationId";
		
		if(!StringUtils.isEmpty(search)){
			
			sql = sql 
			+ "		and ("
			+ "			"+refDateFormat+" like :search"
			+ "			or "+smonthFormat+" like :search"
			+ "			or qr.nPrice like :search"
			+ "			or qr.sPrice like :search"
			+ "			or qr.discount like :search"
			+ "			or qr.isCollectFR like :search"
			+ "			or iqr.previousNPrice like :search"
			+ "			or iqr.previousSPrice like :search"
			+ "			or iqr.currentNPrice like :search"
			+ "			or iqr.currentSPrice like :search"
			+ "			or iqr.isFlag like :search"
			+ "			or iqr.remark like :search"
			+ "			or iqr.isProductChange like :search"
			+ "			or qs.maxSPrice like :search"
			+ "			or qs.minSPrice like :search"
			+ "			or qs.averageCurrentSPrice like :search )";
			
		}
		
		sql += " group by "
				+ "	iqr.referenceDate, "
				+ "	iqr.indoorQuotationRecordId, "
				+ "	sm.referenceMonth,"
				+ "	qr.quotationRecordId, "
				+ "	qr.nPrice, "
				+ "	qr.sPrice, "
				+ "	qr.discount, "
				+ "	qr.availability, "
				+ "	qr.isCollectFR, "
				+ " iqr.previousNPrice,"
				+ "	iqr.previousSPrice,"
				+ "	iqr.currentNPrice,"
				+ "	iqr.currentSPrice,"
				+ "	iqr.isFlag,"
				+ "	iqr.remark,"
				+ "	iqr.isProductChange,"
				+ "	qs.maxSPrice,"
				+ "	qs.minSPrice,"
				+ "	qs.averageCurrentSPrice, "
				+ " qr.productId"; 
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		
		SQLQuery query = getSession().createSQLQuery(sql);
		
//		String sql = "select iqr.referenceDate as submissionDate, "
//				+ " 	iqr.id as id,"
//				+ "		sm.referenceMonth as referenceMonth, "
//				+ "		qr.nPrice as collectedNPrice, "
//				+ "		qr.sPrice as collectedSPrice, "
//				+ "		max(spr.subPriceRecordId) as subPriceId,"
//				+ "		qr.quotationRecordId as quotationRecordId,"
//				+ "		qr.discount as discount, "
//				+ "		qr.availability as availability, "
//				+ "		qr.isCollectFR as fr,"
//				+ "		iqr.previousNPrice as previousNPrice,"
//				+ "		iqr.previousSPrice as previousSPrice,"
//				+ "		iqr.currentNPrice as currentNPrice,"
//				+ "		iqr.currentSPrice as currentSPrice,"
//				+ "		iqr.isFlag as isFlag,"
//				+ "		iqr.remark as remark,"
//				+ "		iqr.isProductChange as isProductChange,"
//				+ "		qs.maxSPrice as max,"
//				+ "		qs.minSPrice as min,"
//				+ "		qs.averageCurrentSPrice as average " ;
		query.addScalar("submissionDate", StandardBasicTypes.DATE);
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("referenceMonth", StandardBasicTypes.DATE);
		query.addScalar("collectedNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("collectedSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("subPriceId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("discount", StandardBasicTypes.STRING);
		query.addScalar("availability", StandardBasicTypes.INTEGER);
		query.addScalar("fr", StandardBasicTypes.BOOLEAN);
		query.addScalar("previousNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("isFlag", StandardBasicTypes.BOOLEAN);
		query.addScalar("remark", StandardBasicTypes.STRING);
		query.addScalar("isProductChange", StandardBasicTypes.BOOLEAN);
		query.addScalar("max", StandardBasicTypes.DOUBLE);
		query.addScalar("min", StandardBasicTypes.DOUBLE);
		query.addScalar("average", StandardBasicTypes.DOUBLE);
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		
		
//		String hql = 
//				" 	select "
//				+ "		iqr.referenceDate as submissionDate, "
//				+ " 	iqr.id as id,"
//				+ "		sm.referenceMonth as referenceMonth, "
//				+ "		qr.nPrice as collectedNPrice, "
//				+ "		qr.sPrice as collectedSPrice, "
//				+ "		max(spr.subPriceRecordId) as subPriceId,"
//				+ "		qr.quotationRecordId as quotationRecordId,"
//				+ "		qr.discount as discount, "
//				+ "		qr.availability as availability, "
//				+ "		qr.isCollectFR as fr,"
//				+ "		iqr.previousNPrice as previousNPrice,"
//				+ "		iqr.previousSPrice as previousSPrice,"
//				+ "		iqr.currentNPrice as currentNPrice,"
//				+ "		iqr.currentSPrice as currentSPrice,"
//				+ "		iqr.isFlag as isFlag,"
//				+ "		iqr.remark as remark,"
//				+ "		iqr.isProductChange as isProductChange,"
//				+ "		qs.maxSPrice as max,"
//				+ "		qs.minSPrice as min,"
//				+ "		qs.averageCurrentSPrice as average"
//				+ " from "
//				+ "		IndoorQuotationRecord as iqr "
//				+ "		left join iqr.quotation q"
//				+ " 	left join iqr.quotationRecord qr"
//				+ " 	left join qr.assignment a"
//				+ " 	left join a.surveyMonth sm"
//				+ "		left join qr.subPriceRecords spr"
//				+ "		left join q.quotationStatistics qs "
//				+ " where "
//				+ "		q.quotationId = :quotationId"
//				+ "		and qs.referenceMonth = sm.referenceMonth";
		
//		if(!StringUtils.isEmpty(search)){
//			
//			hql = hql 
//			+ "		and ("
//			+ "			str(day(iqr.referenceDate)) like :search"
//			+ "			or str(day(sm.referenceMonth)) like :search"
//			+ "			or qr.nPrice like :search"
//			+ "			or qr.sPrice like :search"
//			+ "			or qr.discount like :search"
//			+ "			or qr.isCollectFR like :search"
//			+ "			or iqr.previousNPrice like :search"
//			+ "			or iqr.previousSPrice like :search"
//			+ "			or iqr.currentNPrice like :search"
//			+ "			or iqr.currentSPrice like :search"
//			+ "			or iqr.isFlag like :search"
//			+ "			or iqr.remark like :search"
//			+ "			or iqr.isProductChange like :search"
//			+ "			or qs.maxSPrice like :search"
//			+ "			or qs.minSPrice like :search"
//			+ "			or qs.averageCurrentSPrice like :search )";
//			
//		}
//		
//		hql += " group by "
//				+ "	iqr.referenceDate, "
//				+ "	iqr.id, "
//				+ "	sm.referenceMonth,"
//				+ "	qr.quotationRecordId, "
//				+ "	qr.nPrice, "
//				+ "	qr.sPrice, "
//				+ "	qr.discount, "
//				+ "	qr.availability, "
//				+ "	qr.isCollectFR, "
//				+ " iqr.previousNPrice,"
//				+ "	iqr.previousSPrice,"
//				+ "	iqr.currentNPrice,"
//				+ "	iqr.currentSPrice,"
//				+ "	iqr.isFlag,"
//				+ "	iqr.remark,"
//				+ "	iqr.isProductChange,"
//				+ "	qs.maxSPrice,"
//				+ "	qs.minSPrice,"
//				+ "	qs.averageCurrentSPrice"; 
//		
//		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		
		//Query query = getSession().createQuery(hql);
		query.setParameter("quotationId", quotationId);
		if(!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordHistoryTableListModel.class));
        
        return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public Long countHistoryDataList(String search, int quotationId) {
		String refDateFormat = String.format("FORMAT(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String smonthFormat = String.format("FORMAT(sm.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String sql = "select count(distinct iqr.referenceDate) as referenceDateCnt " 
				+ " from "
				+ "		IndoorQuotationRecord as iqr "
				+ "		left join Quotation q "
				+ "			on iqr.quotationId = q.quotationId "
				+ " 	left join QuotationRecord qr "
				+ "			on qr.quotationRecordId = iqr.quotationRecordId "
				+ " 	left join Assignment a "
				+ "			on a.assignmentId = qr.assignmentId "
				+ " 	left join SurveyMonth sm "
				+ "			on sm.surveyMonthId = a.surveyMonthId "
				+ "		left join SubPriceRecord spr "
				+ "			on spr.quotationRecordId = qr.quotationRecordId "
				+ "		left join QuotationStatistic qs "
				+ "			on qs.quotationId = q.quotationId and qs.referenceMonth = sm.referenceMonth "
				+ " where "
				+ "		q.quotationId = :quotationId";
		
		if(!StringUtils.isEmpty(search)){
			
			sql = sql 
			+ "		and ("
			+ "			"+refDateFormat+" like :search"
			+ "			or "+smonthFormat+" like :search"
			+ "			or qr.nPrice like :search"
			+ "			or qr.sPrice like :search"
			+ "			or qr.discount like :search"
			+ "			or qr.isCollectFR like :search"
			+ "			or iqr.previousNPrice like :search"
			+ "			or iqr.previousSPrice like :search"
			+ "			or iqr.currentNPrice like :search"
			+ "			or iqr.currentSPrice like :search"
			+ "			or iqr.isFlag like :search"
			+ "			or iqr.remark like :search"
			+ "			or iqr.isProductChange like :search"
			+ "			or qs.maxSPrice like :search"
			+ "			or qs.minSPrice like :search"
			+ "			or qs.averageCurrentSPrice like :search )";
			
		}
		
//		String hql = 
//				" 	select "
//				+ "		count(iqr.referenceDate) "
//				+ " from "
//				+ "		IndoorQuotationRecord as iqr "
//				+ "		left join iqr.quotation q"
//				+ " 	left join iqr.quotationRecord qr"
//				+ " 	left join qr.assignment a"
//				+ " 	left join a.surveyMonth sm"
//				+ "		left join qr.subPriceRecords spr"
//				+ "		left join q.quotationStatistics qs "
//				+ " where "
//				+ "		q.quotationId = :quotationId"
//				+ "		and qs.referenceMonth = sm.referenceMonth";
//		if(!StringUtils.isEmpty(search)){
//			
//			hql = hql 
//			+ "		and ("
//			+ "			str(day(iqr.referenceDate)) like :search"
//			+ "			or str(day(sm.referenceMonth)) like :search"
//			+ "			or qr.nPrice like :search"
//			+ "			or qr.sPrice like :search"
//			+ "			or qr.discount like :search"
//			+ "			or qr.isCollectFR like :search"
//			+ "			or iqr.previousNPrice like :search"
//			+ "			or iqr.previousSPrice like :search"
//			+ "			or iqr.currentNPrice like :search"
//			+ "			or iqr.currentSPrice like :search"
//			+ "			or iqr.isFlag like :search"
//			+ "			or iqr.remark like :search"
//			+ "			or iqr.isProductChange like :search"
//			+ "			or qs.maxSPrice like :search"
//			+ "			or qs.minSPrice like :search"
//			+ "			or qs.averageCurrentSPrice like :search )";
//			
//		}
		
//		Query query = getSession().createQuery(hql);
		SQLQuery query = getSession().createSQLQuery(sql);
		query.addScalar("referenceDateCnt", StandardBasicTypes.LONG);
		
		query.setParameter("quotationId", quotationId);
		if(!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
        
        return (Long) query.uniqueResult();
	}

//	@SuppressWarnings("unchecked")
//	public List<QuotationRecord> getUndoQuotationRecordsByAssignmentId(Integer assignmentId) {
//
//		String[] statusList = {"Blank", "Draft", "Rejected"};
//		
//		Criteria criteria = this.createCriteria("qr").createAlias("qr.assignment", "a", JoinType.LEFT_OUTER_JOIN);
//		
//		criteria.add(Restrictions.and(
//			Restrictions.in("qr.status", statusList),
//			Restrictions.eq("a.assignmentId", assignmentId)
//		));
//		
//		return criteria.list();
//	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getUndoQuotationRecordsByAssignmentId(Integer assignmentId) {
		String hql = "Select qr "
				+ " From QuotationRecord as qr "
				+ " left join qr.assignment as a"
				+ " where 1=1 "
				+ " and (qr.status not in ( :status )"
				+ " or (qr.status in ( :status ) and qr.availability = 2 ))"
				+ " and a.assignmentId = :assignmentId ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("assignmentId", assignmentId);
		query.setParameterList("status", new String[]{"Approved", "Submitted"});
		
		return query.list();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getQuotationRecordsByAlloctationBatch(AllocationBatch ab) {

		Criteria criteria = this.createCriteria("qr");
		
		criteria.add(Restrictions.and(
			Restrictions.eq("qr.allocationBatch", ab)
		));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getQuotationRecordsByReferenceMonth(Date referenceMonth) {

		Criteria criteria = this.createCriteria("qr").createAlias("qr.assignment", "a").createAlias("a.surveyMonth", "sm");
		
		criteria.add(
			Restrictions.eq("sm.referenceMonth", referenceMonth)
		);
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public long countQuotationRecordsByReferenceMonth(Date referenceMonth){
		Criteria criteria = this.createCriteria("qr")
				.createAlias("qr.assignment", "a")
				.createAlias("a.surveyMonth", "sm");
		
		criteria.add(
				Restrictions.eq("sm.referenceMonth", referenceMonth)
			);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("qr.quotationRecordId")
		));

		criteria.setProjection(projList);
		
		
		return (long)criteria.uniqueResult();
	}
	
	public Date getLastProductChangeCollectionDate(int quotationId, int skipQuotationRecordId) {
		Criteria criteria = this.createCriteria()
				.setProjection(Projections.property("collectionDate"))
				.add(Restrictions.eq("quotation.id", quotationId))
				.add(Restrictions.ne("id", skipQuotationRecordId))
				.add(Restrictions.eq("isProductChange", true))
				.add(Restrictions.eq("isBackNo", false));
		
		criteria.setMaxResults(1);
		criteria.addOrder(Order.desc("collectionDate"));
		
		return (Date)criteria.uniqueResult();
	}
	
	public List<QuotationRecord> findQuotationRecordByAssignment(Assignment assignment){
		Criteria criteria = this.createCriteria("bcd");
		
		criteria.add(Restrictions.eq("assignment", assignment));
		
		return criteria.list();
		
	}
	
	public List<QuotationRecord> findInProgressQuotationRecordByAssignment(Assignment assignment){
//		Criteria criteria = this.createCriteria("bcd");
//		
//		criteria.add(Restrictions.eq("assignment", assignment));
//		criteria.add(Restrictions.or(
//				Restrictions.not(Restrictions.in("status", new String[]{"Submitted", "Approved"}))),
//				Restrictions.or(Restrictions.eq("quotationState", "IP"), Restrictions.eq("availability", 2)));
//		
//		return criteria.list();
		
		String hql = "From QuotationRecord as qr"
				+ " where qr.assignment = :assignment"
				+ " and (qr.quotationState = 'IP' or qr.availability = 2 or qr.status not in ('Submitted', 'Approved'))";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("assignment", assignment);
		
		return query.list();
	}
	
	public List<QuotationRecord> findInProgressQuotationRecordByQuotation(Integer quotationId, Date historyDate){
//		Criteria criteria = this.createCriteria("bcd");
//		
//		criteria.add(Restrictions.eq("assignment", assignment));
//		criteria.add(Restrictions.or(
//				Restrictions.not(Restrictions.in("status", new String[]{"Submitted", "Approved"}))),
//				Restrictions.or(Restrictions.eq("quotationState", "IP"), Restrictions.eq("availability", 2)));
//		
//		return criteria.list();
		
		String hql = "From QuotationRecord as qr"
				+ " where qr.quotation.quotationId = :quotationId and qr.referenceDate < :historyDate "
				+ " and (qr.quotationState = 'IP' or qr.availability = 2 or qr.status not in ('Submitted', 'Approved'))";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("quotationId", quotationId);
		query.setParameter("historyDate", historyDate);
		
		return query.list();
	}
	
	public List<QuotationRecord> getBackTrackQuotationRecordByQuotationRecord(List<QuotationRecord> records){
		
		Criteria criteria = this.createCriteria("bcd");
		
		criteria.add(Restrictions.in("originalQuotationRecord", records));
		criteria.add(Restrictions.eq("isBackTrack", true));
		
		return criteria.list();
	}
	
	public List<QuotationRecord> getBackNoQuotationRecordByQuotationRecord(List<QuotationRecord> records){
		
		Criteria criteria = this.createCriteria("bcd");
		
		criteria.add(Restrictions.in("originalQuotationRecord", records));
		criteria.add(Restrictions.eq("isBackNo", true));
		
		return criteria.list();
	}
	
	public List<QuotationRecordGroupingModel> groupQuotationRecord(List<Integer> Ids){
		Criteria criteria = this.createCriteria("qr");
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("qr.collectionDate"), "collectionDate");
		projList.add(Projections.groupProperty("qr.assignedStartDate"), "assignedStartDate");
		projList.add(Projections.groupProperty("qr.assignedEndDate"), "assignedEndDate");
		projList.add(Projections.groupProperty("qr.outlet"), "outlet");
		projList.add(Projections.groupProperty("qr.user"), "user");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(QuotationRecordGroupingModel.class));
		
		criteria.add(Restrictions.in("qr.quotationRecordId", Ids));
		
		return criteria.list();
	}
	
	public List<QuotationRecord> findQuotationRecordByGroupedResult(
			Date collectionDate,
			Date assignedStartDate,
			Date assignedEndDate,
			Outlet outlet,
			User user,
			List<Integer> quotationRecordIds
			){
		
		Criteria criteria = this.createCriteria("qr");
		criteria.add(Restrictions.eqOrIsNull("collectionDate", collectionDate))
			.add(Restrictions.eqOrIsNull("assignedStartDate", assignedStartDate))
			.add(Restrictions.eqOrIsNull("assignedEndDate", assignedEndDate))
			.add(Restrictions.eq("outlet", outlet))
			.add(Restrictions.eq("user", user))
			.add(Restrictions.in("quotationRecordId", quotationRecordIds));
		
		return criteria.list();
		
	}
	
	public List<QuotationRecord> getQuotationRecordsByIds(List<Integer> Ids){
		Criteria criteria = this.createCriteria("qr");
		criteria.add(Restrictions.in("qr.quotationRecordId", Ids));
		return criteria.list();
	}
	
	public List<QuotationRecord> getLast2QuotationRecordBeforeDateEnd(Quotation quotation, Date dateBefore){
		Criteria criteria = this.createCriteria("qr");
		return criteria
				.add(Restrictions.or(Restrictions.le("qr.assignedEndDate", dateBefore), Restrictions.le("qr.collectionDate", dateBefore)))
				.add(Restrictions.eq("qr.quotation", quotation))
				.add(Restrictions.eq("availability", 1))
				.add(Restrictions.isNotNull("sPrice"))
				.addOrder(Order.desc("qr.assignedEndDate"))
				.setFetchSize(2)
				.list();
	}
	
	public long countQuotationRecordWithinSurveyMonth(Quotation quotation, Date from, Date to){
		Criteria criteria = this.createCriteria("qr");
		
		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.le("qr.assignedEndDate", from), Restrictions.ge("qr.assignedEndDate", to)),
				Restrictions.and(Restrictions.le("qr.collectionDate", from), Restrictions.ge("qr.collectionDate", to))
				))
				.add(Restrictions.eq("qr.quotation", quotation))
				.add(Restrictions.eq("availability", 1))
				.add(Restrictions.isNotNull("sPrice"));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(SQLProjectionExt.groupCount(
				Projections.groupProperty("qr.quotationRecordId")
		));

		criteria.setProjection(projList);
		
		
		return (long)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getByIds(Integer[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("quotationRecordId", ids)).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getByIdsWithUnit(List<Integer> ids) {
		return this.createCriteria()
				.setFetchMode("quotation", FetchMode.JOIN)
				.setFetchMode("quotation.unit", FetchMode.JOIN)
				.add(Restrictions.in("quotationRecordId", ids)).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getByIdsWithAssignment(Integer id) {
		return this.createCriteria()
				.add(Restrictions.eq("assignment.assignmentId", id)).list();
	}
	
	public List<QuotationRecord> getDeadlineQuotationRecords(Date date, Date previousDay){
		String hql = "select q "
				+ " from QuotationRecord as q "
				+ " inner join fetch q.outlet as o "
				+ " inner join fetch q.assignment as a "
				+ " inner join fetch q.user as u "
				+ " left join fetch u.supervisor as s "
				+ " where ("
				+ " q.assignedCollectionDate is not null and q.assignedCollectionDate < :previousDay or "
				+ " q.assignedEndDate is not null and q.assignedEndDate < :date "
				+ ") and q.status not in ('Submitted','Approved') and q.availability != 2 and q.quotationState != 'IP' "
				+ " and q.isBackTrack = 0 and q.isBackNo = 0 ";
		Query query = this.getSession().createQuery(hql);
		query.setParameter("date", date);
		query.setParameter("previousDay", previousDay);
		
		return query.list();
	}
	
	public List<QuotationRecordSyncData> getQuotationRecordsWithoutIndoorQuotationRecordInAssignmentById(Integer indoorQotationRecordId){
		String sql = "";
		sql = "SELECT QuotationRecordId AS quotationRecordId, AssignmentId AS assignmentId"  //2
				+ " FROM QuotationRecord "
				+ " WHERE "
				+ " AssignmentId IN (SELECT AssignmentId FROM QuotationRecord "
				+ " WHERE QuotationRecordId = (SELECT QuotationRecordId FROM IndoorQuotationRecord WHERE IndoorQuotationRecordId=:indoorQotationRecordId))"
				+ " AND (QuotationRecordId NOT IN (SELECT QuotationRecordId FROM IndoorQuotationRecord WHERE QuotationRecordId IS NOT NULL) "
				+ " OR Status NOT IN ('Approved')) "
				+ " AND (IsBackNo = 0 AND IsBackTrack = 0) "
				+ " ORDER BY AssignmentId";

		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("indoorQotationRecordId", indoorQotationRecordId);
		
		query.addScalar("quotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordSyncData.class));
	
		return query.list();
		
	}
 
	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getNormalTableListForAssignmentMaintenance(String search,
			Integer firstRecord, Integer displayLenght, Order order,
			Integer assignmentId, Integer userId, String consignmentCounter, String unitCategory
			) {

		String hql = "select r.quotationRecordId as id, "
				+ " concat( "
                + " case when spec1.productSpecificationId is null then '' "
                + "   else concat(spec1.specificationName, '=', spec1.value) end, "
                + " case when spec2.productSpecificationId is null then '' "
                + "   else concat(',', spec2.specificationName, '=', spec2.value) end, "
                + " case when spec3.productSpecificationId is null then '' "
                + "   else concat(',', spec3.specificationName, '=', spec3.value) end "
                + " ) as productAttribute, "
                + " r.nPrice as nPrice, r.sPrice as sPrice, r.discount as discount, "
                + " r.status as status, "
                + " r.availability as availability, "
                + " r.isFlag as isFlag, "
                + " case when r.passValidation = true and count(backTrack) = 0 then true else false end as passValidation, "
                + " r.firmStatus as firmStatus "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.otherQuotationRecords as backTrack on backTrack.isBackNo = false and backTrack.isBackTrack = true and backTrack.passValidation = false "
                + " left join r.product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = 'Normal' ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " spec1.specificationName like :search or spec1.value like :search or "
                + " spec2.specificationName like :search or spec2.value like :search or "
                + " spec3.specificationName like :search or spec3.value like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		
		hql += " group by r.quotationRecordId, "
                + " spec1.productSpecificationId,spec1.specificationName,spec1.value, "
                + " spec2.productSpecificationId,spec2.specificationName,spec2.value, "
                + " spec3.productSpecificationId,spec3.specificationName,spec3.value, "
                + " r.nPrice, r.sPrice, r.discount, "
                + " r.status, r.availability, r.isFlag, r.passValidation, r.firmStatus ";
		
		if (order != null)
			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}

		if (firstRecord != null) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLenght);
		}

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));

		return query.list();
	}
	
	public long countNormalTableListForAssignmentMaintenance(String search,
			Integer assignmentId, Integer userId, String consignmentCounter, String unitCategory) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from Assignment as a"
                + " left join a.quotationRecords as r "
                + " left join r.product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = 'Normal' ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " spec1.specificationName like :search or spec1.value like :search or "
                + " spec2.specificationName like :search or spec2.value like :search or "
                + " spec3.specificationName like :search or spec3.value like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	//HQL Version
//	@SuppressWarnings("unchecked")
//	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getNormalRevisitVerifyTableListForAssignmentMaintenance(String search,
//			Integer firstRecord, Integer displayLenght, Order order,
//			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, Integer verificationType, String unitCategory, String quotationState
//			) {
//
//		String hql = "select r.quotationRecordId as id, "
//				+ " unit.displayName as unitDisplayName, "
//				+ " r.product.id as productId, "
//                + " r.nPrice as nPrice, r.sPrice as sPrice, r.discount as discount, "
//                + " r.status as status, "
//                + " r.availability as availability, "
//                + " r.isFlag as isFlag, "
//                + " case when (r.passValidation = false and r.availability <> 2) or "
//                + "   count(backTrackInvalid) > 0 or "
//                + "   (r.quotationState <> 'IP' and count(backTrackIP) > 0) then false else true end as passValidation, "
//                + " case when count(allBackTrack) > 0 then true else false end as hasBacktrack, "
//                + " a.status as firmStatus "
//                + " from Assignment as a"
//                + " inner join a.quotationRecords as r "
//                + " left join r.otherQuotationRecords as backTrackInvalid on backTrackInvalid.isBackNo = false and backTrackInvalid.isBackTrack = true and backTrackInvalid.passValidation = false and backTrackInvalid.availability <> 2 "
//                + " left join r.otherQuotationRecords as allBackTrack on allBackTrack.isBackNo = false and allBackTrack.isBackTrack = true "
//                + " left join r.otherQuotationRecords as backTrackIP on backTrackIP.isBackNo = false and backTrackIP.isBackTrack = true and backTrackIP.availability = 2 "
//                + " left join r.user as user "
//                + " left join r.quotation as q "
//                + " left join q.unit as unit "
//                + " where 1 = 1 ";
//		
//		hql += " and r.status not in ('Submitted', 'Approved') ";
//		
//		hql += " and r.quotationState = :quotationState ";
//		
//		hql += " and r.isBackTrack = false and r.isBackNo = false ";
//		
//		if (dateSelectedAssignmentId != null)
//			hql += " and a.id = :assignmentId ";
//		
//		if (userId != null)
//			hql += " and user.id = :userId ";
//		
//		if (StringUtils.isNotEmpty(consignmentCounter)) {
//			hql += " and r.consignmentCounterName = :consignmentCounter ";
//		}
//		
//		if (verificationType != null) {
//			if (verificationType == 1) {
//				hql += " and r.verifyCategory = true ";
//			} else if (verificationType == 2) {
//				hql += " and r.verifyFirm = true ";
//			} else if (verificationType == 3) {
//				hql += " and r.verifyQuotation = true ";
//			}
//		}
//		
//		if (StringUtils.isNotEmpty(search)) {
//			hql += " and ( "
//                + " unit.displayName like :search or "
//                + " r.status like :search or "
//                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
//                + " ) ";
//		}
//		
//		if (StringUtils.isNotEmpty(unitCategory)) {
//			hql += " and unit.unitCategory = :unitCategory ";
//		}
//		
//		hql += " group by r.quotationRecordId, "
//                + " unit.displayName, "
//                + " r.product.id, "
//                + " r.nPrice, r.sPrice, r.discount, "
//                + " r.status, r.availability, r.isFlag, r.passValidation, a.status, "
//                + " r.quotationState ";
//		
//		if (order != null)
//			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
//		else
//			hql += " order by unitDisplayName asc ";
//
//		Query query = getSession().createQuery(hql);
//
//		if (dateSelectedAssignmentId != null)
//			query.setParameter("assignmentId", dateSelectedAssignmentId);
//		
//		if (userId != null)
//			query.setParameter("userId", userId);
//		
//		query.setParameter("quotationState", quotationState);
//		
//		if (StringUtils.isNotEmpty(search)) {
//			query.setParameter("search", String.format("%%%s%%", search));
//		}
//
//		if (StringUtils.isNotEmpty(unitCategory)) {
//			query.setParameter("unitCategory", unitCategory);
//		}
//		
//		if (StringUtils.isNotEmpty(consignmentCounter)) {
//			query.setParameter("consignmentCounter", consignmentCounter);
//		}
//
//		if (firstRecord != null) {
//			query.setFirstResult(firstRecord);
//			query.setMaxResults(displayLenght);
//		}
//
//		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));
//
//		return query.list();
//	}
	
	//SQL Version
	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getNormalRevisitVerifyTableListForAssignmentMaintenance(String search,
			Integer firstRecord, Integer displayLenght, Order order,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, Integer verificationType, String unitCategory, String quotationState
			) {

		String sql = "select qr.quotationRecordId as id, "
				+ " q.formType as formType, "
				+ " q.isICP as hasICP, "
				+ " u.displayName as unitDisplayName, "
				+ " qr.productId as productId, "
				+ " qr.nPrice as nPrice, qr.sPrice as sPrice, qr.discount as discount, "
				+ " qr.status as status, "
				+ " qr.availability as availability, "
				+ " qr.isSPricePeculiar as isSPricePeculiar, "
				+ " qr.isFlag as isFlag, "
				+ " case when qr.PassValidation = 0  and qr.availability <> 2 "
					+ " or count(backTrackInvalid.quotationRecordId) > 0 "
					+ " or qr.quotationState <> 'IP' "
					+ " and count(backTrackIP.quotationRecordId)>0 then 0 else 1 end as passValidation, "
				+ " case when count(allBackTrack.quotationRecordId) > 0 then 1 else 0 end as hasBacktrack, "
				+ " a.status as firmStatus "
				+ " from Assignment as a "
				+ " inner join QuotationRecord as qr on a.assignmentId = qr.assignmentId "
				+ " left outer join QuotationRecord as backTrackInvalid on backTrackInvalid.originalQuotationRecordId = qr.quotationRecordId "
					+ " and backTrackInvalid.isBackNo = 0 and backTrackInvalid.isBackTrack = 1 "
					+ " and backTrackInvalid.passValidation = 0 and backTrackInvalid.availability <> 2 "
				+ " left outer join QuotationRecord as allBackTrack on allBackTrack.originalQuotationRecordId = qr.quotationRecordId "
					+ " and allBackTrack.IsBackNo = 0 and allBackTrack.IsBackTrack = 1 "
				+ " left outer join QuotationRecord as backTrackIP on backTrackIP.originalQuotationRecordId = qr.quotationRecordId "
					+ " and backTrackIP.IsBackNo = 0 and backTrackIP.IsBackTrack = 1 and backTrackIP.availability = 2 "
				+ " left outer join [User] as ur on qr.userId = ur.userId "
				+ " left outer join Quotation as q on qr.quotationId = q.quotationId"
				+ " left outer join Unit as u on q.unitId = u.unitId "
				+ " left outer join Outlet as o on a.outletId = o.outletId "
				+ " left outer join AssignmentUnitCategoryInfo as ac on ac.outletId = o.outletId and ac.unitCategory = u.unitCategory "
					+ " and a.assignmentId = ac.assignmentId "
				+ " where 1=1";
		
		sql += " and qr.status not in ('Submitted', 'Approved') ";
		
		sql += " and qr.quotationState = :quotationState ";
		
		sql += " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		if (dateSelectedAssignmentId != null)
			sql += " and a.assignmentId = :assignmentId ";
		
		if (userId != null)
			sql += " and ur.userId = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			sql += " and qr.consignmentCounterName = :consignmentCounter ";
		}
		
		if (verificationType != null) {
			if (verificationType == 1) {
				sql += " and qr.verifyCategory = 1 ";
			} else if (verificationType == 2) {
				sql += " and qr.verifyFirm = 1 ";
			} else if (verificationType == 3) {
				sql += " and qr.verifyQuotation = 1 ";
			}
		}
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
                + " u.displayName like :search or "
                + " qr.status like :search or "
                + " str(qr.nPrice) like :search or str(qr.sPrice) like :search or "
                + " qr.discount like :search or "
                + " q.formType like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			sql += " and u.unitCategory = :unitCategory ";
		}
		
		sql += " group by qr.quotationRecordId, "
                + " u.displayName, "
                + " qr.productId, "
                + " qr.nPrice, qr.sPrice, qr.discount, "
                + " qr.status, qr.availability, qr.isSPricePeculiar, qr.isFlag, qr.passValidation, a.status, "
                + " qr.quotationState, ac.sequence, q.formType, q.isICP ";
		
		sql += " order by ";
				
		if(order!=null){
			if("unitDisplayName".equals(order.getPropertyName())){
				sql += " ac.sequence "+ (order.isAscending()? " asc": " desc") + ", ";
			}
			
			sql += order.getPropertyName() + (order.isAscending()? " asc": " desc");
			
		} else {
			sql += " ac.sequence asc , unitDisplayName asc";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if (dateSelectedAssignmentId != null)
			query.setParameter("assignmentId", dateSelectedAssignmentId);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		query.setParameter("quotationState", quotationState);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}

		if (firstRecord != null) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLenght);
		}

		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("formType", StandardBasicTypes.STRING);
		query.addScalar("hasICP", StandardBasicTypes.BOOLEAN);
		query.addScalar("unitDisplayName", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		query.addScalar("nPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("sPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("discount", StandardBasicTypes.STRING);
		query.addScalar("status", StandardBasicTypes.STRING);
		query.addScalar("availability", StandardBasicTypes.INTEGER);
		query.addScalar("isSPricePeculiar", StandardBasicTypes.BOOLEAN);
		query.addScalar("isFlag", StandardBasicTypes.BOOLEAN);
		query.addScalar("passValidation", StandardBasicTypes.BOOLEAN);
		query.addScalar("hasBacktrack", StandardBasicTypes.BOOLEAN);
		query.addScalar("firmStatus", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));
		
		return query.list();
	}
	
	public long countNormalRevisitVerifyTableListForAssignmentMaintenance(String search,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, Integer verificationType, String unitCategory, String quotationState) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from Assignment as a"
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = :quotationState ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		if (dateSelectedAssignmentId != null)
			hql += " and a.id = :assignmentId ";
		
		if (userId != null)
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
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or "
                + " r.discount like :search or "
                + " q.formType like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		Query query = getSession().createQuery(hql);

		if (dateSelectedAssignmentId != null)
			query.setParameter("assignmentId", dateSelectedAssignmentId);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		query.setParameter("quotationState", quotationState);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	//HQL Version
//	@SuppressWarnings("unchecked")
//	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getIPTableListForAssignmentMaintenance(String search,
//			Integer firstRecord, Integer displayLenght, Order order,
//			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory
//			) {
//
//		String hql = "select r.quotationRecordId as id, "
//				+ " unit.displayName as unitDisplayName, "
//				+ " r.product.id as productId, "
//                + " r.nPrice as nPrice, r.sPrice as sPrice, r.discount as discount, "
//                + " r.status as status, "
//                + " r.availability as availability, "
//                + " r.isFlag as isFlag, "
//                + " case when (r.passValidation = false and r.availability <> 2) or "
//                + "   count(backTrackInvalid) > 0 or "
//                + "   (r.quotationState <> 'IP' and count(backTrackIP) > 0) then false else true end as passValidation, "
//                + " case when count(allBackTrack) > 0 then true else false end as hasBacktrack, "
//                + " a.status as firmStatus "
//                + " from Assignment as a"
//                + " inner join a.quotationRecords as r "
//                + " left join r.otherQuotationRecords as backTrackInvalid on backTrackInvalid.isBackNo = false and backTrackInvalid.isBackTrack = true and backTrackInvalid.passValidation = false and backTrackInvalid.availability <> 2 "
//                + " left join r.otherQuotationRecords as allBackTrack on allBackTrack.isBackNo = false and allBackTrack.isBackTrack = true "
//                + " left join r.otherQuotationRecords as backTrackIP on backTrackIP.isBackNo = false and backTrackIP.isBackTrack = true and backTrackIP.availability = 2 "
//                + " left join r.user as user "
//                + " left join r.quotation as q "
//                + " left join q.unit as unit "
//                + " where 1 = 1 ";
//		
//		hql += " and (r.quotationState = 'IP') ";
//		
//		hql += " and r.isBackTrack = false and r.isBackNo = false ";
//		
//		if (dateSelectedAssignmentId != null)
//			hql += " and a.id = :assignmentId ";
//		
//		hql += " and user.id = :userId ";
//		
//		if (StringUtils.isNotEmpty(consignmentCounter)) {
//			hql += " and r.consignmentCounterName = :consignmentCounter ";
//		}
//		
//		if (StringUtils.isNotEmpty(search)) {
//			hql += " and ( "
//                + " unit.displayName like :search or "
//                + " r.status like :search or "
//                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
//                + " ) ";
//		}
//		
//		if (StringUtils.isNotEmpty(unitCategory)) {
//			hql += " and unit.unitCategory = :unitCategory ";
//		}
//		
//		hql += " group by r.quotationRecordId, "
//                + " unit.displayName, "
//                + " r.product.id, "
//                + " r.nPrice, r.sPrice, r.discount, "
//                + " r.status, r.availability, r.isFlag, r.passValidation, a.status, "
//                + " r.quotationState ";
//		
//		if (order != null)
//			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
//		else
//			hql += " order by unitDisplayName asc ";
//
//		Query query = getSession().createQuery(hql);
//
//		if (dateSelectedAssignmentId != null)
//			query.setParameter("assignmentId", dateSelectedAssignmentId);
//		
//		query.setParameter("userId", userId);
//		
//		if (StringUtils.isNotEmpty(search)) {
//			query.setParameter("search", String.format("%%%s%%", search));
//		}
//
//		if (StringUtils.isNotEmpty(unitCategory)) {
//			query.setParameter("unitCategory", unitCategory);
//		}
//		
//		if (StringUtils.isNotEmpty(consignmentCounter)) {
//			query.setParameter("consignmentCounter", consignmentCounter);
//		}
//
//		if (firstRecord != null) {
//			query.setFirstResult(firstRecord);
//			query.setMaxResults(displayLenght);
//		}
//
//		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));
//
//		return query.list();
//	}
	
	//SQL Version
	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getIPTableListForAssignmentMaintenance(String search,
			Integer firstRecord, Integer displayLenght, Order order,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory
			) {

		String sql = "select qr.quotationRecordId as id, "
				+ " q.formType as formType, "
				+ " q.isICP as hasICP, "
				+ " u.displayName as unitDisplayName, "
				+ " qr.productId as productId, "
				+ " qr.nPrice as nPrice, qr.sPrice as sPrice, qr.discount as discount, "
				+ " qr.status as status, "
				+ " qr.availability as availability, "
				+ " qr.isFlag as isFlag, "
				+ " case when qr.PassValidation = 0  and qr.availability <> 2 "
					+ " or count(backTrackInvalid.quotationRecordId) > 0 "
					+ " or qr.quotationState <> 'IP' "
					+ " and count(backTrackIP.quotationRecordId)>0 then 0 else 1 end as passValidation, "
				+ " case when count(allBackTrack.quotationRecordId) > 0 then 1 else 0 end as hasBacktrack, "
				+ " a.status as firmStatus "
				+ " from Assignment as a "
				+ " inner join QuotationRecord as qr on a.assignmentId = qr.assignmentId "
				+ " left outer join QuotationRecord as backTrackInvalid on backTrackInvalid.originalQuotationRecordId = qr.quotationRecordId "
					+ " and backTrackInvalid.isBackNo = 0 and backTrackInvalid.isBackTrack = 1 "
					+ " and backTrackInvalid.passValidation = 0 and backTrackInvalid.availability <> 2 "
				+ " left outer join QuotationRecord as allBackTrack on allBackTrack.originalQuotationRecordId = qr.quotationRecordId "
					+ " and allBackTrack.IsBackNo = 0 and allBackTrack.IsBackTrack = 1 "
				+ " left outer join QuotationRecord as backTrackIP on backTrackIP.originalQuotationRecordId = qr.quotationRecordId "
					+ " and backTrackIP.IsBackNo = 0 and backTrackIP.IsBackTrack = 1 and backTrackIP.availability = 2 "
				+ " left outer join [User] as ur on qr.userId = ur.userId "
				+ " left outer join Quotation as q on qr.quotationId = q.quotationId"
				+ " left outer join Unit as u on q.unitId = u.unitId "
				+ " left outer join Outlet as o on a.outletId = o.outletId "
				+ " left outer join AssignmentUnitCategoryInfo as ac on ac.outletId = o.outletId and ac.unitCategory = u.unitCategory "
					+ " and a.assignmentId = ac.assignmentId"
				+ " where 1=1";
		
		sql += " and qr.quotationState = 'IP' ";
		
		sql += " and qr.isBackTrack = 0 and qr.isBackNo = 0 ";
		
		if (dateSelectedAssignmentId != null)
			sql += " and a.assignmentId = :assignmentId ";
		
		sql += " and ur.userId = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			sql += " and qr.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			sql += " and ( "
                + " u.displayName like :search or "
                + " qr.status like :search or "
                + " str(qr.nPrice) like :search or str(qr.sPrice) like :search or "
                + " qr.discount like :search or "
                + " q.formType like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			sql += " and u.unitCategory = :unitCategory ";
		}
		
		sql += " group by qr.quotationRecordId, "
                + " u.displayName, "
                + " qr.productId, "
                + " qr.nPrice, qr.sPrice, qr.discount, "
                + " qr.status, qr.availability, qr.isFlag, qr.passValidation, a.status, "
                + " qr.quotationState, ac.sequence, q.formType, q.isICP ";
		sql += " order by ";
		
		if(order!=null){
			if("unitDisplayName".equals(order.getPropertyName())){
				sql += " ac.sequence "+ (order.isAscending()? " asc": " desc") + ", ";
			}
			
			sql += order.getPropertyName() + (order.isAscending()? " asc": " desc");
			
		} else {
			sql += " ac.sequence asc , unitDisplayName asc";
		}
			
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if (dateSelectedAssignmentId != null)
			query.setParameter("assignmentId", dateSelectedAssignmentId);
		
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}

		if (firstRecord != null) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLenght);
		}

		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("formType", StandardBasicTypes.STRING);
		query.addScalar("hasICP", StandardBasicTypes.BOOLEAN);
		query.addScalar("unitDisplayName", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		query.addScalar("nPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("sPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("discount", StandardBasicTypes.STRING);
		query.addScalar("status", StandardBasicTypes.STRING);
		query.addScalar("availability", StandardBasicTypes.INTEGER);
		query.addScalar("isFlag", StandardBasicTypes.BOOLEAN);
		query.addScalar("passValidation", StandardBasicTypes.BOOLEAN);
		query.addScalar("hasBacktrack", StandardBasicTypes.BOOLEAN);
		query.addScalar("firmStatus", StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));
		
		return query.list();
	}

	public long countIPTableListForAssignmentMaintenance(String search,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from Assignment as a"
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and (r.quotationState = 'IP') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or "
                + " r.discount like :search or "
                + " q.formType like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", dateSelectedAssignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getTableListForNewRecruitmentMaintenance(String search,
			Integer firstRecord, Integer displayLenght, Order order,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory
			) {

		String hql = "select r.quotationRecordId as id, "
				+ " unit.displayName as unitDisplayName, "
				+ " r.product.id as productId, "
                + " r.nPrice as nPrice, r.sPrice as sPrice, r.discount as discount, "
                + " r.status as status, "
                + " r.availability as availability, "
                + " r.isFlag as isFlag, "
                + " case when r.passValidation = true and count(backTrack) = 0 then true else false end as passValidation, "
                + " case when count(allBackTrack) > 0 then true else false end as hasBacktrack, "
                + " a.status as firmStatus "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.otherQuotationRecords as backTrack on backTrack.isBackNo = false and backTrack.isBackTrack = true and backTrack.passValidation = false "
                + " left join r.otherQuotationRecords as allBackTrack on allBackTrack.isBackNo = false and allBackTrack.isBackTrack = true "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.isNewRecruitment = true ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		if (dateSelectedAssignmentId != null)
			hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		
		hql += " group by r.quotationRecordId, "
                + " unit.displayName, "
                + " r.product.id, "
                + " r.nPrice, r.sPrice, r.discount, "
                + " r.status, r.availability, r.isFlag, r.passValidation, a.status ";
		
		if (order != null)
			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		else
			hql += " order by unitDisplayName asc ";

		Query query = getSession().createQuery(hql);

		if (dateSelectedAssignmentId != null)
			query.setParameter("assignmentId", dateSelectedAssignmentId);
		
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}

		if (firstRecord != null) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLenght);
		}

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));

		return query.list();
	}
	
	public long countTableListForNewRecruitmentMaintenance(String search,
			Integer dateSelectedAssignmentId, Integer userId, String consignmentCounter, String unitCategory) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from Assignment as a"
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.isNewRecruitment = true ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		if (dateSelectedAssignmentId != null)
			hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		Query query = getSession().createQuery(hql);

		if (dateSelectedAssignmentId != null)
			query.setParameter("assignmentId", dateSelectedAssignmentId);
		
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getPEViewTableListForAssignmentMaintenance(String search,
			Integer firstRecord, Integer displayLenght, Order order,
			Integer assignmentId, String consignmentCounter, String unitCategory
			) {

		String hql = "select r.quotationRecordId as id, "
				+ " unit.displayName as unitDisplayName, "
				+ " r.product.id as productId, "
                + " r.nPrice as nPrice, r.sPrice as sPrice, r.discount as discount, "
                + " r.status as status, "
                + " r.availability as availability, "
                + " r.isFlag as isFlag, "
                + " case when r.passValidation = true and count(backTrack) = 0 then true else false end as passValidation, "
                + " case when count(allBackTrack) > 0 then true else false end as hasBacktrack, "
                + " a.status as firmStatus "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.otherQuotationRecords as backTrack on backTrack.isBackNo = false and backTrack.isBackTrack = true and backTrack.passValidation = false "
                + " left join r.otherQuotationRecords as allBackTrack on allBackTrack.isBackNo = false and allBackTrack.isBackTrack = true "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		
		hql += " group by r.quotationRecordId, "
                + " unit.displayName, "
                + " r.product.id, "
                + " r.nPrice, r.sPrice, r.discount, "
                + " r.status, r.availability, r.isFlag, r.passValidation, a.status ";
		
		if (order != null)
			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		else
			hql += " order by unitDisplayName asc ";

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}

		if (firstRecord != null) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLenght);
		}

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));

		return query.list();
	}
	
	public long countPEViewTableListForAssignmentMaintenance(String search,
			Integer assignmentId, String consignmentCounter, String unitCategory) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from Assignment as a"
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getTableListForRUACaseApproval(String search,
			Integer firstRecord, Integer displayLenght, Order order,
			Integer assignmentId, String consignmentCounter, String unitCategory
			) {

		String hql = "select r.quotationRecordId as id, "
				+ " unit.displayName as unitDisplayName, "
				+ " r.product.id as productId, "
                + " r.nPrice as nPrice, r.sPrice as sPrice, r.discount as discount, "
                + " r.status as status, "
                + " r.availability as availability, "
                + " r.isFlag as isFlag, "
                + " case when r.passValidation = true and count(backTrack) = 0 then true else false end as passValidation, "
                + " case when count(allBackTrack) > 0 then true else false end as hasBacktrack, "
                + " a.status as firmStatus "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.otherQuotationRecords as backTrack on backTrack.isBackNo = false and backTrack.isBackTrack = true and backTrack.passValidation = false "
                + " left join r.otherQuotationRecords as allBackTrack on allBackTrack.isBackNo = false and allBackTrack.isBackTrack = true "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and r.status = 'Submitted' and r.availability = 5 ";
		
		hql += " and r.isNewRecruitment = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		
		hql += " group by r.quotationRecordId, "
                + " unit.displayName, "
                + " r.product.id, "
                + " r.nPrice, r.sPrice, r.discount, "
                + " r.status, r.availability, r.isFlag, r.passValidation, a.status ";
		
		if (order != null)
			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		else
			hql += " order by unitDisplayName asc ";

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}

		if (firstRecord != null) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLenght);
		}

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));

		return query.list();
	}
	
	public long countTableListForRUACaseApproval(String search,
			Integer assignmentId, String consignmentCounter, String unitCategory) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from Assignment as a"
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and r.status = 'Submitted' and r.availability = 5 ";
		
		hql += " and r.isNewRecruitment = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList> getTableListForNewRecruitmentApproval(String search,
			Integer firstRecord, Integer displayLenght, Order order,
			Integer assignmentId, String consignmentCounter, String unitCategory, Integer personInChargeId
			) {

		String hql = "select r.quotationRecordId as id, "
				+ " unit.displayName as unitDisplayName, "
				+ " r.product.id as productId, "
                + " r.nPrice as nPrice, r.sPrice as sPrice, r.discount as discount, "
                + " batch.code as batchCode, "
                + " batch.batchId as batchId, "
                + " batch.assignmentType as assignmentType, "
                + " r.isFlag as isFlag, "
                + " case when r.passValidation = true and count(backTrack) = 0 then true else false end as passValidation, "
                + " case when count(allBackTrack) > 0 then true else false end as hasBacktrack, "
                + " a.status as firmStatus "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.otherQuotationRecords as backTrack on backTrack.isBackNo = false and backTrack.isBackTrack = true and backTrack.passValidation = false "
                + " left join r.otherQuotationRecords as allBackTrack on allBackTrack.isBackNo = false and allBackTrack.isBackTrack = true "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.batch as batch "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		
		if (personInChargeId != null) {
			hql += " and user.userId = :personInChargeId";
		}
		
		hql += " group by r.quotationRecordId, "
                + " unit.displayName, "
                + " r.product.id, "
                + " r.nPrice, r.sPrice, r.discount, "
                + " batch.batchId, batch.code, batch.assignmentType, r.isFlag, r.passValidation, a.status ";
		
		if (order != null)
			hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		else
			hql += " order by unitDisplayName asc ";

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}

		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}

		if (firstRecord != null) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLenght);
		}

		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordTableList.class));

		return query.list();
	}
	
	public long countTableListForNewRecruitmentApproval(String search,
			Integer assignmentId, String consignmentCounter, String unitCategory, Integer personInChargeId) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from Assignment as a"
                + " left join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.batch as batch "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			hql += " and r.consignmentCounterName = :consignmentCounter ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
                + " unit.displayName like :search or "
                + " r.status like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search "
                + " ) ";
		}
		
		if (StringUtils.isNotEmpty(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		
		if (personInChargeId != null) {
			hql += " and user.userId = :personInChargeId";
		}

		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (StringUtils.isNotEmpty(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}

		if (personInChargeId != null) {
			query.setParameter("personInChargeId", personInChargeId);
		}
		
		if (StringUtils.isNotEmpty(consignmentCounter)) {
			query.setParameter("consignmentCounter", consignmentCounter);
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public long countIsNewOutletTableListForNewRecruitmentApproval(Integer assignmentId) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from Assignment as a"
                + " left join a.quotationRecords as r "
                + " left join r.product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.batch as batch "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and q.status = 'Active' ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and r.isNewOutlet = true ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<BackTrackDateModel> getBackTrackDates(int originalQuotationRecordId) {
		String collectionDate = String.format("FORMAT(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select qr.id as quotationRecordId, case when qr.collectionDate is not null then " + collectionDate + " else '' end as date, qr.passValidation as passValidation "
				+ " from QuotationRecord qr ";
		
		hql += " where qr.originalQuotationRecord.id = :originalQuotationRecordId "
				+ " and qr.isBackTrack = true ";
		
		hql += " order by qr.collectionDate asc ";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("originalQuotationRecordId", originalQuotationRecordId);
		
		query.setResultTransformer(Transformers.aliasToBean(BackTrackDateModel.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getQuotationRecordsForSubmitWithoutBackTrack(int assignmentId, int userId) {
		return this.createCriteria()
				.add(Restrictions.eq("assignment.id", assignmentId))
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.not(Restrictions.in("status", new String[] {"Submitted", "Approved"})))
				.add(Restrictions.or(Restrictions.in("quotationState", new String[] {"Normal", "Revisit", "Verify"}), Restrictions.eq("availability", 2)))
				.add(Restrictions.eq("isBackNo", false))
				.add(Restrictions.eq("isBackTrack", false))
				.setFetchMode("otherQuotationRecords", FetchMode.JOIN)
				.setFetchMode("otherQuotationRecords.otherQuotationRecords", FetchMode.JOIN)
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getQuotationRecordsForSubmitWithoutBackTrackWithIP(int assignmentId, int userId) {
		return this.createCriteria()
				.add(Restrictions.eq("assignment.id", assignmentId))
				.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.not(Restrictions.in("status", new String[] {"Submitted", "Approved"})))
				.add(Restrictions.or(Restrictions.in("quotationState", new String[] {"Normal", "Revisit", "Verify", "IP"}), Restrictions.eq("availability", 2)))
				.add(Restrictions.eq("isBackNo", false))
				.add(Restrictions.eq("isBackTrack", false))
				.setFetchMode("otherQuotationRecords", FetchMode.JOIN)
				.setFetchMode("otherQuotationRecords.otherQuotationRecords", FetchMode.JOIN)
				.list();
	}
	
	public VerificationTypeGroupModel getVerificationTypeGroup(int assignmentId, int userId) {
		String hql = " select "
				+ " case when count(verifyCategory.verifyCategory) > 0 then true else false end as verifyCategory, "
				+ " case when count(verifyFirm.verifyFirm) > 0 then true else false end as verifyFirm, "
				+ " case when count(verifyQuotation.verifyQuotation) > 0 then true else false end as verifyQuotation "
				+ " from Assignment a "
				+ " left join a.quotationRecords as verifyCategory on verifyCategory.verifyCategory = true "
				+ " and verifyCategory.status not in ('Submitted', 'Approved') "
				+ " and verifyCategory.quotationState = 'Verify' "
				+ " and verifyCategory.isBackTrack = false and verifyCategory.isBackNo = false "
				+ " and verifyCategory.user.id = :userId "
				
				+ " left join a.quotationRecords as verifyFirm on verifyFirm.verifyFirm = true "
				+ " and verifyFirm.status not in ('Submitted', 'Approved') "
				+ " and verifyFirm.quotationState = 'Verify' "
				+ " and verifyFirm.isBackTrack = false and verifyFirm.isBackNo = false "
				+ " and verifyFirm.user.id = :userId "
				
				+ " left join a.quotationRecords as verifyQuotation on verifyQuotation.verifyQuotation = true "
				+ " and verifyQuotation.status not in ('Submitted', 'Approved') "
				+ " and verifyQuotation.quotationState = 'Verify' "
				+ " and verifyQuotation.isBackTrack = false and verifyQuotation.isBackNo = false "
				+ " and verifyQuotation.user.id = :userId ";
		
		hql += " where a.id = :assignmentId ";
		
		hql += " group by verifyFirm.verifyFirm, verifyCategory.verifyCategory, verifyQuotation.verifyQuotation ";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		query.setResultTransformer(Transformers.aliasToBean(VerificationTypeGroupModel.class));
		
		return (VerificationTypeGroupModel)query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<String> searchNormalRevisitVerifyDistinctUnitCategoryForAssignmentMaintenance(String search, int firstRecord, int displayLength, int assignmentId, Integer userId, Integer verificationType, String quotationState) {
		String hql = "select unit.unitCategory "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = :quotationState ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (userId != null)
			hql += " and user.id = :userId ";
		
		if (verificationType != null) {
			if (verificationType == 1) {
				hql += " and r.verifyCategory = true ";
			} else if (verificationType == 2) {
				hql += " and r.verifyFirm = true ";
			} else if (verificationType == 3) {
				hql += " and r.verifyQuotation = true ";
			}
		}

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " group by unit.unitCategory ";
		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		query.setParameter("quotationState", quotationState);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchNormalRevisitVerifyDistinctUnitCategoryForAssignmentMaintenance(String search, int assignmentId, Integer userId, Integer verificationType, String quotationState) {
		String hql = "select count(distinct unit.unitCategory) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = :quotationState ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (userId != null)
			hql += " and user.id = :userId ";

		if (verificationType != null) {
			if (verificationType == 1) {
				hql += " and r.verifyCategory = true ";
			} else if (verificationType == 2) {
				hql += " and r.verifyFirm = true ";
			} else if (verificationType == 3) {
				hql += " and r.verifyQuotation = true ";
			}
		}

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		query.setParameter("quotationState", quotationState);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchIPDistinctUnitCategoryForAssignmentMaintenance(String search, int firstRecord, int displayLength, int assignmentId, int userId) {
		String hql = "select unit.unitCategory "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and (r.quotationState = 'IP') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";
		
		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " group by unit.unitCategory ";
		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchIPDistinctUnitCategoryForAssignmentMaintenance(String search, int assignmentId, int userId) {
		String hql = "select count(distinct unit.unitCategory) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and (r.quotationState = 'IP') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchDistinctUnitCategoryForNewRecruitmentMaintenance(String search, int firstRecord, int displayLength, int assignmentId, int userId) {
		String hql = "select unit.unitCategory "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		hql += " and r.isNewRecruitment = true ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " group by unit.unitCategory ";
		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchDistinctUnitCategoryForNewRecruitmentMaintenance(String search, int assignmentId, int userId) {
		String hql = "select count(distinct unit.unitCategory) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		hql += " and r.isNewRecruitment = true ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchPEViewDistinctUnitCategoryForAssignmentMaintenance(String search, int firstRecord, int displayLength, int assignmentId) {
		String hql = "select unit.unitCategory "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " group by unit.unitCategory ";
		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchPEViewDistinctUnitCategoryForAssignmentMaintenance(String search, int assignmentId) {
		String hql = "select count(distinct unit.unitCategory) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchDistinctUnitCategoryForRUACaseApproval(String search, int firstRecord, int displayLength, int assignmentId) {
		String hql = "select unit.unitCategory "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and r.status = 'Submitted' and r.availability = 5 ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " group by unit.unitCategory ";
		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchDistinctUnitCategoryForRUACaseApproval(String search, int assignmentId) {
		String hql = "select count(distinct unit.unitCategory) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and r.status = 'Submitted' and r.availability = 5 ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchDistinctUnitCategoryForNewRecruitmentApproval(String search, int firstRecord, int displayLength, int assignmentId) {
		String hql = "select unit.unitCategory "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}

		hql += " group by unit.unitCategory ";
		hql += " order by unit.unitCategory asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchDistinctUnitCategoryForNewRecruitmentApproval(String search, int assignmentId) {
		String hql = "select count(distinct unit.unitCategory) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.quotation as q "
                + " left join q.unit as unit "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and unit.unitCategory like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> searchNormalRevisitVerifyConsignmentForAssignmentMaintenance(String search, int firstRecord, int displayLength, int assignmentId, Integer userId, String quotationState) {
		String hql = "select r.consignmentCounterName "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = :quotationState ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (userId != null)
			hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}

		hql += " group by r.consignmentCounterName ";
		hql += " order by r.consignmentCounterName asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		query.setParameter("quotationState", quotationState);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchNormalRevisitVerifyConsignmentForAssignmentMaintenance(String search, int assignmentId, Integer userId, String quotationState) {
		String hql = "select count(distinct r.consignmentCounterName) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.quotationState = :quotationState ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		if (userId != null)
			hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		query.setParameter("quotationState", quotationState);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchConsignmentForRUACaseApproval(String search, int firstRecord, int displayLength, int assignmentId) {
		String hql = "select r.consignmentCounterName "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and r.status = 'Submitted' and r.availability = 5 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		hql += " and r.isNewRecruitment = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}

		hql += " group by r.consignmentCounterName ";
		hql += " order by r.consignmentCounterName asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchConsignmentForRUACaseApproval(String search, int assignmentId) {
		String hql = "select count(distinct r.consignmentCounterName) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and r.status = 'Submitted' and r.availability = 5 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		hql += " and r.isNewRecruitment = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchConsignmentForNewRecruitmentApproval(String search, int firstRecord, int displayLength, int assignmentId) {
		String hql = "select r.consignmentCounterName "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " inner join r.quotation as q "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and q.status = 'Active' ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}

		hql += " group by r.consignmentCounterName ";
		hql += " order by r.consignmentCounterName asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchConsignmentForNewRecruitmentApproval(String search, int assignmentId) {
		String hql = "select count(distinct r.consignmentCounterName) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " inner join r.quotation as q "
                + " left join r.user as user "
                + " where 1 = 1 ";

		hql += " and q.status = 'Active' ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchIPConsignmentForAssignmentMaintenance(String search, int firstRecord, int displayLength, int assignmentId, int userId) {
		String hql = "select r.consignmentCounterName "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and (r.quotationState = 'IP') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}

		hql += " group by r.consignmentCounterName ";
		hql += " order by r.consignmentCounterName asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchIPConsignmentForAssignmentMaintenance(String search, int assignmentId, int userId) {
		String hql = "select count(distinct r.consignmentCounterName) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and (r.quotationState = 'IP') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchConsignmentForNewRecruitmentMaintenance(String search, int firstRecord, int displayLength, int assignmentId, int userId) {
		String hql = "select r.consignmentCounterName "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		hql += " and r.isNewRecruitment = true ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}

		hql += " group by r.consignmentCounterName ";
		hql += " order by r.consignmentCounterName asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchConsignmentForNewRecruitmentMaintenance(String search, int assignmentId, int userId) {
		String hql = "select count(distinct r.consignmentCounterName) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " left join r.user as user "
                + " where 1 = 1 ";
		
		hql += " and r.status not in ('Submitted', 'Approved') ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		hql += " and r.isNewRecruitment = true ";
		
		hql += " and a.id = :assignmentId ";
		
		hql += " and user.id = :userId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}

	@SuppressWarnings("unchecked")
	public List<String> searchPEViewConsignmentForAssignmentMaintenance(String search, int firstRecord, int displayLength, int assignmentId) {
		String hql = "select r.consignmentCounterName "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}

		hql += " group by r.consignmentCounterName ";
		hql += " order by r.consignmentCounterName asc ";
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}

	public long countSearchPEViewConsignmentForAssignmentMaintenance(String search, int assignmentId) {
		String hql = "select count(distinct r.consignmentCounterName) "
                + " from Assignment as a"
                + " inner join a.quotationRecords as r "
                + " where 1 = 1 ";
		
		hql += " and r.isBackTrack = false and r.isBackNo = false ";
		
		hql += " and a.id = :assignmentId ";

		if (!StringUtils.isEmpty(search)) {
			hql += " and r.consignmentCounterName like :search ";
		}
		
		Query query = getSession().createQuery(hql);

		query.setParameter("assignmentId", assignmentId);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getAllIdsNotSubmittedByAssignment(int assignmentId) {
		return this.createCriteria()
				.setProjection(Projections.property("quotationRecordId"))
				.add(Restrictions.eq("assignment.id", assignmentId))
				.add(Restrictions.not(Restrictions.in("status", new String[]{"Submitted", "Approved"})))
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getAllIdsSubmittedAndRUAByAssignments(List<Integer> assignmentIds) {
		return this.createCriteria()
				.setProjection(Projections.property("quotationRecordId"))
				.add(Restrictions.in("assignment.id", assignmentIds))
				.add(Restrictions.eq("status", "Submitted"))
				.add(Restrictions.eq("availability", 5))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getAllIdsNewRecruitmentByOutlets(List<Integer> outletIds) {
		String hql = " select r.id "
				+ " from QuotationRecord as r "
                + " left join r.outlet as o "
				+ " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";
		
		hql += " and o.id in :outletIds ";
		
		Query query = getSession().createQuery(hql);
		
		query.setParameterList("outletIds", outletIds);
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getIdsForOutletAndUserPairs(List<Map.Entry<Integer, Integer>> outletIdUserIdPairs) {
		String hql = " select r.id "
				+ " from QuotationRecord as r "
                + " left join r.outlet as o "
				+ " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = true and r.status = 'Submitted' ";

		hql += " and r.isBackNo = false and r.isBackTrack = false ";

//		hql += " and o.id in :outletIds ";
		
		String outletUserClause = java.util.stream.IntStream.rangeClosed(1, outletIdUserIdPairs.size())
			.mapToObj(i -> String.format("((o.id = :outletId%s) and (r.user.userId = :userId%s))", i, i))
			.collect(Collectors.joining(" or "));
		hql += String.format(" and (%s)", outletUserClause);
		
		Query query = getSession().createQuery(hql);

		for (int i = 1; i <= outletIdUserIdPairs.size(); i++) {
			Map.Entry<Integer, Integer> pair = outletIdUserIdPairs.get(i - 1);
			query.setParameter("outletId" + i, pair.getKey());
			query.setParameter("userId" + i, pair.getValue());
		}
		
//		query.setParameterList("outletIds", outletIds);
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<capi.model.assignmentManagement.assignmentApproval.TableList> getAssignmentApprovalTableList(String search,
			int firstRecord, int displayLength, Order order,
			List<Integer> userIds,
			Integer outletId, String outletTypeId, Integer personInChargeId,
			Integer[] districtId, String unitCategory, Integer[] tpuId,
			Boolean isProductChange, Boolean isSPricePeculiar, Integer[] availability, Date refMonth, Integer[] purposeId) {
		String hql = "select r.id as quotationRecordId, "
				+ " o.name as firm, "
				+ " o.id as outletId, "
				+ " concat(substring(ot2.code, len(ot2.code)-2, 3), ' - ', ot2.englishName) as outletType, "
				+ " d.code as district, "
				+ " tpu.code as tpu, "
				+ " unit.displayName as unitDisplayName, "
				+ " p.code as purpose, "
				+ " r.nPrice as nPrice, "
				+ " r.sPrice as sPrice, "
				+ " r.discount as discount, "
				+ " r.availability as availability, "
				+ " r.reason as reason, "
				+ " case when r.isProductChange = true then 'Y' else 'N' end as isProductChange, "
				+ " user.englishName as personInCharge, "
				+ " case when r.isSPricePeculiar = true then 'Y' else 'N' end as isSPricePeculiar, "
				+ " r.quotationState as quotationState, "
				+ " r.remark as priceRemark, "
				+ " r.categoryRemark as outletCategoryRemark, "
				+ " o.detailAddress as address, "
				+ " unit.unitCategory as unitCategory "
                + " from Assignment as a "
                + " inner join a.surveyMonth as s "
                + " inner join a.quotationRecords as r "
//                + " left join a.quotationRecords as r2 on r2.status in ('Submitted', 'Approved') and r=r2 "
                + " inner join r.user as user "
                + " inner join r.outlet as o "
                + " left join o.outletTypes as ot "
                + " inner join o.tpu as tpu "
                + " inner join tpu.district as d "
                + " inner join r.quotation as q "
                + " inner join q.unit as unit "
                + " left join unit.purpose as p "
                + " inner join unit.subItem as si "
                + " inner join si.outletType as ot2 "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = false ";
		hql += " and r.availability <> 5 ";
		
		hql += " and r.isBackNo = false "
				+ " and r.status = 'Submitted' ";
		
		hql += " and r.isBackTrack = false ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.supervisor.id in (:userIds) ";
		}
		
		if (outletId != null) {
			hql += " and o.id = :outletId ";
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
		if (!StringUtils.isBlank(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		if (tpuId != null && tpuId.length > 0) {
			hql += " and tpu.id in :tpuId ";
		}
		if (isProductChange != null) {
			hql += " and r.isProductChange = :isProductChange ";
		}
		if (isSPricePeculiar != null) {
			hql += " and r.isSPricePeculiar = :isSPricePeculiar ";
		}
		if (availability != null && availability.length > 0) {
			 hql += " and r.availability in ( :availability )";
		}
		
		if (refMonth != null){
			hql += " and s.referenceMonth = :refMonth ";
		}
		
		if (purposeId != null && purposeId.length > 0) {
			hql += " and p.id in :purposeId ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " o.name like :search or "
					+ " concat(substring(ot2.code, len(ot2.code)-2, 3), ' - ', ot2.englishName) like :search or "
					+ " user.englishName like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " unit.unitCategory like :search or "
					+ " unit.displayName like :search or "
					+ " str(r.nPrice) like :search or "
					+ " str(r.sPrice) like :search or "
					+ " r.discount like :search or "
					+ " r.reason like :search or "
					+ " o.detailAddress like :search or "
					+ " r.quotationState like :search or "
					+ " r.remark like :search or "
					+ " r.categoryRemark like :search or "
					+ " p.code like :search "
	                + " ) ";
		}
		
		hql += " group by r.quotationRecordId, "
				+ " o.name, o.id, "
				+ " ot2.code, ot2.englishName, "
				+ " user.englishName, d.code, tpu.code, unit.unitCategory, "
				+ " unit.displayName, r.nPrice, r.sPrice, r.discount, r.reason, o.detailAddress, "
				+ " r.availability, r.isProductChange, r.quotationState, r.isSPricePeculiar, r.remark, r.categoryRemark, p.code ";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc")
				+ ", quotationRecordId " + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (refMonth != null){
			query.setParameter("refMonth", refMonth);
		}
		
		if (outletId != null) {
			query.setParameter("outletId", outletId);
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
		if (!StringUtils.isBlank(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		if (tpuId != null && tpuId.length > 0) {
			query.setParameterList("tpuId", tpuId);
		}
		if (isProductChange != null) {
			query.setParameter("isProductChange", isProductChange);
		}
		if (isSPricePeculiar != null) {
			query.setParameter("isSPricePeculiar", isSPricePeculiar);
		}
		if (availability != null && availability.length >0) {
			 query.setParameterList("availability", availability);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		if (displayLength > 0) {
			query.setFirstResult(firstRecord);
			query.setMaxResults(displayLength);
		}

		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentApproval.TableList.class));

		return query.list();
	}
	
	public long countAssignmentApprovalTableList(String search,
			List<Integer> userIds,
			Integer outletId, String outletTypeId, Integer personInChargeId,
			Integer[] districtId, String unitCategory, Integer[] tpuId,
			Boolean isProductChange, Boolean isSPricePeculiar, Integer[] availability, Date refMonth, Integer[] purposeId) {

		String hql = "select r.quotationRecordId "
                + " from Assignment as a "
                + " inner join a.surveyMonth as s "
                + " inner join a.quotationRecords as r "
//                + " left join a.quotationRecords as r2 on r2.status = 'Blank' and r=r2 "
                + " inner join r.user as user "
                + " inner join r.outlet as o "
                + " left join o.outletTypes as ot "
                + " inner join o.tpu as tpu "
                + " inner join tpu.district as d "
                + " inner join r.quotation as q "
                + " inner join q.unit as unit "
                + " inner join unit.subItem as si "
                + " inner join si.outletType as ot2 "
                + " left join unit.purpose as p "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = false ";
		hql += " and r.availability <> 5 ";
		
		hql += " and r.isBackNo = false "
				+ " and r.status = 'Submitted' ";
		
		hql += " and r.isBackTrack = false ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.supervisor.id in (:userIds) ";
		}
		

		if (refMonth != null){
			hql += " and s.referenceMonth = :refMonth ";
		}
		
		if (outletId != null) {
			hql += " and o.outletId = :outletId ";
		}
		if (!StringUtils.isBlank(outletTypeId)) {
			hql += " and ot.shortCode = :outletTypeId ";
		}
		if (personInChargeId != null) {
			hql += " and r.user.id = :personInChargeId ";
		}
		if (districtId != null && districtId.length > 0) {
			hql += " and d.districtId in (:districtId) ";
		}
		if (!StringUtils.isBlank(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		if (tpuId != null && tpuId.length > 0) {
			hql += " and tpu.id in (:tpuId) ";
		}
		if (isProductChange != null) {
			hql += " and r.isProductChange = :isProductChange ";
		}
		if (isSPricePeculiar != null) {
			hql += " and r.isSPricePeculiar = :isSPricePeculiar ";
		}
		if (availability != null && availability.length > 0) {
			 hql += " and r.availability in ( :availability )";
		}
		if (purposeId != null && purposeId.length > 0) {
			 hql += " and p.id in ( :purposeId )";
		}

		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " o.name like :search or "
					+ " concat(substring(ot2.code, len(ot2.code)-2, 3), ' - ', ot2.englishName) like :search or "
					+ " user.englishName like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " unit.unitCategory like :search or "
					+ " unit.displayName like :search or "
					+ " str(r.nPrice) like :search or "
					+ " str(r.sPrice) like :search or "
					+ " r.discount like :search or "
					+ " r.reason like :search or "
					+ " o.detailAddress like :search or "
					+ " r.quotationState like :search or "
					+ " r.remark like :search or "
					+ " r.categoryRemark like :search or "
					+ " p.code like :search "
	                + " ) ";
		}

		String countHql = "select count(distinct qr.id) "
				+ " from QuotationRecord as qr "
				+ " where qr.id in (" + hql + ") ";

		Query query = getSession().createQuery(countHql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		

		if (refMonth != null){
			query.setParameter("refMonth", refMonth);
		}
		
		if (outletId != null) {
			query.setParameter("outletId", outletId);
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
		if (!StringUtils.isBlank(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		if (tpuId != null && tpuId.length > 0) {
			query.setParameterList("tpuId", tpuId);
		}
		if (isProductChange != null) {
			query.setParameter("isProductChange", isProductChange);
		}
		if (isSPricePeculiar != null) {
			query.setParameter("isSPricePeculiar", isSPricePeculiar);
		}
		if (availability != null && availability.length > 0) {
			 query.setParameterList("availability", availability);
		}
		if (purposeId != null && purposeId.length > 0) {
			 query.setParameterList("purposeId", purposeId);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;

	}
	
	public QuotationRecordCountByTabModel getQuotationRecordCountByTab(int assignmentId, int userId) {
		String sql = "select count(distinct normal.quotationRecordId) as normal, count(distinct revisit.quotationRecordId) as revisit, "
				+ "		count(distinct verify.quotationRecordId) as verify, count(distinct ip.quotationRecordId) as ip "
				+ " from Assignment as a "
                + " inner join QuotationRecord as all_qr "
                + "   on all_qr.assignmentId = a.assignmentId"
                + " left join QuotationRecord as normal "
                + "   on  normal.assignmentId = a.assignmentId and all_qr.quotationRecordId=normal.quotationRecordId "
                + "		and normal.quotationState = 'Normal' "
                + "     and normal.status not in ('Submitted', 'Approved') and normal.isBackTrack = 0 and normal.isBackNo = 0 "
                + " left join QuotationRecord as revisit "
                + "   on  revisit.assignmentId = a.assignmentId and all_qr.quotationRecordId=revisit.quotationRecordId "
                + "		and  revisit.quotationState = 'Revisit' "
                + "     and revisit.status not in ('Submitted', 'Approved') and revisit.isBackTrack = 0 and revisit.isBackNo = 0 "
                + " left join QuotationRecord as verify "
                + "   on verify.assignmentId = a.assignmentId and all_qr.quotationRecordId=verify.quotationRecordId "
                + "		and  verify.quotationState = 'Verify' "
                + "     and verify.status not in ('Submitted', 'Approved') and verify.isBackTrack = 0 and verify.isBackNo = 0 "
                + " left join QuotationRecord as ip "
                + "   on  ip.assignmentId = a.assignmentId and all_qr.quotationRecordId=ip.quotationRecordId  "
                + "		and (ip.quotationState = 'IP') "
                + "     and ip.isBackTrack = 0 and ip.isBackNo = 0 "
                + " where all_qr.userId = :userId ";
		
//		String hql = "select count(distinct normal) as normal, count(distinct revisit) as revisit, count(distinct verify) as verify, count(distinct ip) as ip "
//                + " from Assignment as a "
//                + " inner join a.quotationRecords as all "
//                + " left join a.quotationRecords as normal "
//                + "   on normal.quotationState = 'Normal' and normal.user.userId = :userId "
//                + "     and normal.status not in ('Submitted', 'Approved') and normal.isBackTrack = false and normal.isBackNo = false and all=nomral"
//                + " left join a.quotationRecords as revisit "
//                + "   on revisit.quotationState = 'Revisit' and revisit.user.userId = :userId "
//                + "     and revisit.status not in ('Submitted', 'Approved') and revisit.isBackTrack = false and revisit.isBackNo = false and all=revisit "
//                + " left join a.quotationRecords as verify "
//                + "   on verify.quotationState = 'Verify' and verify.user.userId = :userId "
//                + "     and verify.status not in ('Submitted', 'Approved') and verify.isBackTrack = false and verify.isBackNo = false and all=verify "
//                + " left join a.quotationRecords as ip "
//                + "   on (ip.quotationState = 'IP' or ip.availability = 2) and ip.user.userId = :userId "
//                + "     and ip.isBackTrack = false and ip.isBackNo = false and all=ip "
//                + " where 1 = 1 ";
		
		sql += " and a.assignmentId = :assignmentId ";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		
		query.setParameter("assignmentId", assignmentId);
		query.setParameter("userId", userId);
		
		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordCountByTabModel.class));
		query.addScalar("normal", StandardBasicTypes.LONG);
		query.addScalar("revisit", StandardBasicTypes.LONG);
		query.addScalar("verify", StandardBasicTypes.LONG);
		query.addScalar("ip", StandardBasicTypes.LONG);

		return (QuotationRecordCountByTabModel)query.uniqueResult();
	}
	
	public QuotationRecordCountByTabModel getQuotationRecordCountByTabAndOutlet(int outletId, int userId) {
		String sql = "select count(distinct normal.quotationRecordId) as normal, count(distinct revisit.quotationRecordId) as revisit, "
				+ "		count(distinct verify.quotationRecordId) as verify, count(distinct ip.quotationRecordId) as ip "
				+ " from Assignment as a "
                + " inner join QuotationRecord as all_qr "
                + "   on all_qr.assignmentId = a.assignmentId"
                + " left join QuotationRecord as normal "
                + "   on  normal.assignmentId = a.assignmentId and all_qr.quotationRecordId=normal.quotationRecordId "
                + "		and normal.quotationState = 'Normal' "
                + "     and normal.status not in ('Submitted', 'Approved') and normal.isBackTrack = 0 and normal.isBackNo = 0 "
                + " left join QuotationRecord as revisit "
                + "   on  revisit.assignmentId = a.assignmentId and all_qr.quotationRecordId=revisit.quotationRecordId "
                + "		 and revisit.quotationState = 'Revisit' "
                + "     and revisit.status not in ('Submitted', 'Approved') and revisit.isBackTrack = 0 and revisit.isBackNo = 0 "
                + " left join QuotationRecord as verify "
                + "   on verify.assignmentId = a.assignmentId and all_qr.quotationRecordId=verify.quotationRecordId "
                + "		and  verify.quotationState = 'Verify' "
                + "     and verify.status not in ('Submitted', 'Approved') and verify.isBackTrack = 0 and verify.isBackNo = 0 "
                + " left join QuotationRecord as ip "
                + "   on  ip.assignmentId = a.assignmentId and all_qr.quotationRecordId=ip.quotationRecordId  "
                + "		and (ip.quotationState = 'IP') "
                + "     and ip.isBackTrack = 0 and ip.isBackNo = 0 "
                + " where all_qr.userId = :userId ";
//		String hql = "select count(distinct normal) as normal, count(distinct revisit) as revisit, count(distinct verify) as verify, count(distinct ip) as ip "
//                + " from Assignment as a "
//                + " inner join a.quotationRecords as all "
//                + " left join a.quotationRecords as normal "
//                + "   on normal.quotationState = 'Normal' and normal.user.id = :userId "
//                + "     and normal.status not in ('Submitted', 'Approved') and normal.isBackTrack = false and normal.isBackNo = false and all=nomral "
//                + " left join a.quotationRecords as revisit "
//                + "   on revisit.quotationState = 'Revisit' and revisit.user.id = :userId "
//                + "     and revisit.status not in ('Submitted', 'Approved') and revisit.isBackTrack = false and revisit.isBackNo = false and all=revisit "
//                + " left join a.quotationRecords as verify "
//                + "   on verify.quotationState = 'Verify' and verify.user.id = :userId "
//                + "     and verify.status not in ('Submitted', 'Approved') and verify.isBackTrack = false and verify.isBackNo = false and all=verify "
//                + " left join a.quotationRecords as ip "
//                + "   on ip.quotationState = 'IP' and ip.user.id = :userId "
//                + "     and ip.isBackTrack = false and ip.isBackNo = false and all=ip "
//                + " where 1 = 1 ";
		
		sql += " and a.outletId = :outletId ";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		
		query.setParameter("outletId", outletId);
		query.setParameter("userId", userId);
		query.addScalar("normal", StandardBasicTypes.LONG);
		query.addScalar("revisit", StandardBasicTypes.LONG);
		query.addScalar("verify", StandardBasicTypes.LONG);
		query.addScalar("ip", StandardBasicTypes.LONG);
		query.setResultTransformer(Transformers.aliasToBean(capi.model.assignmentManagement.assignmentManagement.QuotationRecordCountByTabModel.class));

		return (QuotationRecordCountByTabModel)query.uniqueResult();
	}
	
	public List<QuotationRecord> getQuotationRecordByAllocationBatch(AllocationBatch allocationBatch){
		Criteria criteria = this.createCriteria();
		criteria.setFetchMode("assignment", FetchMode.JOIN);
		criteria.setFetchMode("assignment.outlet", FetchMode.JOIN);
		criteria.setFetchMode("assignment.outlet.tpu", FetchMode.JOIN);
		criteria.setFetchMode("assignment.outlet.tpu.district", FetchMode.JOIN);
		criteria.setFetchMode("assignment.outlet.tpu.district.user", FetchMode.JOIN);
		criteria.add(Restrictions.eq("allocationBatch", allocationBatch));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getQuotationRecordIdsForRUADeletion(Integer surveyMonthId, Integer quotationId) {
		String sql = " Select {qr.*} "
				+ " from Quotation as q"
				+ " left join QuotationRecord as qr on q.QuotationId = qr.QuotationId"
				+ " left join Assignment as a on a.AssignmentId = qr.AssignmentId"
				+ " left join SurveyMonth as sm on a.SurveyMonthId = sm.SurveyMonthId"
				+ " where 1=1"
				+ " and qr.status not in ( :status )"
				+ " and qr.isBackTrack = 0"
				+ " and qr.isBackNo = 0"
				+ " and q.quotationId = :quotationId"
				+ " and (qr.assignedCollectionDate > :today or qr.assignedEndDate > :today )"
				+ " and sm.referenceMonth >= ("
					+ " Select referenceMonth from SurveyMonth where surveyMonthId = :surveyMonthId )";
		
//		String hql = " select record "
//				+ " from Quotation as q "
//				+ " left join q.quotationRecords as record "
//				+ " left join record.assignment as assignment "
//				+ " left join assignment.surveyMonth as surveyMonth "
//				+ " where record.status <> 'Submitted' and record.status <> 'Approved' "
//				+ " and surveyMonth.id = :surveyMonthId "
//				+ " and q.quotationId = :quotationId "
//				+ " and record.isBackNo = false "
//				+ " and record.isBackTrack = false "
//				+ " and (record.assignedCollectionDate > :today or record.assignedEndDate > :today) ";
//		
//		Query query = getSession().createQuery(hql);
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameterList("status", new String[]{"Submitted", "Approved"});
		query.setParameter("surveyMonthId", surveyMonthId);
		query.setParameter("quotationId", quotationId);
		
		Date today = new Date();
		today = DateUtils.truncate(today, Calendar.DATE);
		
		query.setParameter("today", today);
		query.addEntity("qr", QuotationRecord.class);
		
		return query.list();
	}
	
	public List<QuotationRecord> getAprovedQuotationRecordForClosingDateConversion(Integer surveyMonthId){
		String hql = "select qr "
				+ " from QuotationRecord as qr "
				+ " inner join qr.quotation as q "
				+ " inner join q.unit as u "
				+ " inner join qr.assignment as a "
				+ " inner join a.surveyMonth as m "
				+ " where u.convertAfterClosingDate = true and qr.status = 'Approved' "
				+ " and m.surveyMonthId = :surveyMonthId ";
		Query query = this.getSession().createQuery(hql);
		query.setParameter("surveyMonthId", surveyMonthId);
		return query.list();
	}
	
	public List<QuotationRecord> getApprovedQuotationRecordForConversion(Integer surveyMonthId, boolean closingDate){
		String hql = "select qr from QuotationRecord as qr where qr.quotationRecordId in ( "
				+ " select qr.quotationRecordId "
				/*
				 * 02-08-2019 
				 */
				+ " from QuotationRecord as qr "
				+ " inner join qr.quotation as q "
				+ " inner join q.unit as u "
				+ " inner join qr.assignment as a "
				+ " inner join a.surveyMonth as sm "
				+ " inner join sm.closingDate as cd"
				+ " left join qr.indoorQuotationRecord as iqr"
				+ " where qr.status = 'Approved' "
				+ " and sm.surveyMonthId = :surveyMonthId "
				+ " and iqr.indoorQuotationRecordId is null"
				+ " and cd.closingDate >= :today";
		
		if(closingDate){
			hql += " and u.convertAfterClosingDate = false ";
		}

		hql += " )";
		
		hql += " order by qr.referenceDate";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("surveyMonthId", surveyMonthId);
		
		Date today = new Date();
		today = DateUtils.addDays(today, -1);
		today = DateUtils.truncate(today, Calendar.DATE);
		query.setParameter("today", today);
		
		query.setMaxResults(100);
		
		return query.list();
	}
	
	public Integer countAprovedQuotationRecordNotConversion(Integer surveyMonthId, boolean closingDate){
		String hql = "select count(distinct qr.quotationRecordId) "
				+ " from QuotationRecord as qr "
				+ " inner join qr.quotation as q "
				+ " inner join q.unit as u "
				+ " inner join qr.assignment as a "
				+ " inner join a.surveyMonth as sm "
				+ " left join sm.closingDate as cd"
				+ " left join qr.indoorQuotationRecord as iqr"
				+ " where qr.status = 'Approved' "
				+ " and sm.surveyMonthId = :surveyMonthId "
				+ " and iqr.indoorQuotationRecordId is null"
				+ " and cd.closingDate >= :today";
		
		if(closingDate){
			hql += " and u.convertAfterClosingDate = false ";
		}
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("surveyMonthId", surveyMonthId);
		
		Date today = new Date();
		today = DateUtils.addDays(today, -1);
		today = DateUtils.truncate(today, Calendar.DATE);
		query.setParameter("today", today);
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count.intValue();
	}

	/**
	 * Data Sync
	 */
	public List<Integer> getAllIdsByAssignments(List<Integer> assignmentIds) {
		return this.createCriteria()
				.setProjection(Projections.property("quotationRecordId"))
				.add(Restrictions.in("assignment.id", assignmentIds))
				.list();
	}
	
	public List<QuotationRecordSyncData> getUpdatedQuotationRecord(Date lastSyncTime, Integer[] quotationRecordIds, Integer[] assignmentIds){
		String hql = "Select qr.quotationRecordId as quotationRecordId, o.outletId as outletId, p.productId as productId"
				+ ", q.quotationId as quotationId, qr.createdDate as createdDate, qr.modifiedDate as modifiedDate"
				+ ", a.assignmentId as assignmentId, qr.quotationState as quotationState"
				+ ", qr.referenceDate as referenceDate, qr.collectionDate as collectionDate"
				+ ", qr.nPrice as nPrice, qr.sPrice as sPrice, qr.isSPricePeculiar as isSPricePeculiar"
				+ ", qr.uomValue as uomValue, uom.uomId as uomId, qr.fr as fr"
				+ ", qr.isFRPercentage as isFRPercentage, qr.isConsignmentCounter as isConsignmentCounter"
				+ ", qr.consignmentCounterRemark as consignmentCounterRemark, qr.reason as reason"
				+ ", qr.discount as discount, qr.remark as remark, qr.availability as availability"
				+ ", qr.categoryRemark as categoryRemark, qr.contactPerson as contactPerson"
				+ ", qr.isBackNo as isBackNo, qr.isBackTrack as isBackTrack, qr.formDisplay as formDisplay"
				+ ", qr.productRemark as productRemark, qr.isProductChange as isProductChange"
				+ ", oqr.quotationRecordId as originalQuotationRecordId, qr.verificationRemark as verificationRemark"
				+ ", qr.rejectReason as rejectReason, qr.peCheckRemark as peCheckRemark, qr.isNewProduct as isNewProduct"
				+ ", qr.status as status, qr.isNewRecruitment as isNewRecruitment, qr.isFlag as isFlag"
				+ ", qr.isNewOutlet as isNewOutlet, u.userId as userId, qr.isCollectFR as isCollectFR"
				+ ", case when qr.outletDiscountRemark is null or qr.outletDiscountRemark = ''"
				+ " then o.discountRemark "
				+ " else qr.outletDiscountRemark end as outletDiscountRemark"
				+ ", qr.assignedCollectionDate as assignedCollectionDate"
				+ ", qr.assignedStartDate as assignedStartDate, qr.assignedEndDate as assignedEndDate"
				+ ", qr.firmStatus as firmStatus, qr.consignmentCounterName as consignmentCounterName"
				+ ", qr.isVisited as isVisited, qr.verificationReply as verificationReply, qr.validationError as validationError"
				+ ", qr.passValidation as passValidation, qr.discountRemark as discountRemark"
				+ ", qr.historyDate as historyDate, qr.isSpecifiedUser as isSpecifiedUser, qr.isReleased as isReleased"
				+ ", ab.allocationBatchId as allocationBatchId, qr.productPosition as productPosition, qr.verifyFirm as verifyFirm"
				+ ", qr.verifyCategory as verifyCategory, qr.verifyQuotation as verifyQuotation"
				+ " from QuotationRecord as qr"
				+ " left join qr.outlet as o"
				+ " left join qr.product as p"
				+ " left join qr.quotation as q"
				+ " left join qr.assignment as a"
				+ " left join qr.uom as uom"
				+ " left join qr.originalQuotationRecord as oqr"
				+ " left join qr.user as u"
				+ " left join qr.allocationBatch as ab"
				+ " where 1=1";
				
		if(lastSyncTime!=null){
			hql += " and qr.modifiedDate >= :modifiedDate";
		}
		
		if(quotationRecordIds!=null && quotationRecordIds.length>0){
			hql += " and qr.quotationRecordId in ( :quotationRecordIds )";
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			hql += " and a.assignmentId in ( :assignmentIds )";
		}
		
		Query query = getSession().createQuery(hql);
		if(lastSyncTime!=null){
			query.setParameter("modifiedDate", lastSyncTime);
		}
		
		if(quotationRecordIds!=null && quotationRecordIds.length>0){
			query.setParameterList("quotationRecordIds", quotationRecordIds);
		}
		
		if(assignmentIds!=null && assignmentIds.length>0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
				
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordSyncData.class));
		return query.list();
	}
	
	public List<QuotationRecord> getByHistoryDatesAndQuotationId(int quotationId, Date historyDate, int limit) {
		String hql = "from QuotationRecord as a "
				+ " where a.quotation.id = :quotationId"
				+ " and isBackNo = false "
				+ " and a.referenceDate < :historyDate ";
		
		//hql += " group by a.referenceDate ";
		
		hql += " order by a.referenceDate desc ";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("quotationId", quotationId);
		query.setParameter("historyDate", historyDate);
		
		query.setMaxResults(limit);
		
        return query.list();
	}

	public List<QuotationRecord> getApprovedRecordBySurveyMonthQuotation(Quotation quotation, SurveyMonth surveyMonth){
		Criteria criteria = this.createCriteria("qr")
				.createAlias("qr.assignment", "a");
		criteria.add(Restrictions.eq("qr.quotation", quotation));
		criteria.add(Restrictions.eq("a.surveyMonth", surveyMonth));
		criteria.add(Restrictions.eq("qr.status", "Approved"));
		
		return criteria.list();
	}

	public List<DeadlineRowModel> getDashboardFieldOfficerQuotationRecords(int userId) {
		List<Integer> userIds = new ArrayList<Integer>();
		userIds.add(userId);
		return getDashboardFieldOfficerQuotationRecords(userIds);
	}
	
	@SuppressWarnings("unchecked")
	public List<DeadlineRowModel> getDashboardFieldOfficerQuotationRecords(List<Integer> userIds) {
		String dateFormat = "FORMAT(%s, '%s', 'en-us')";
		String assignedCollectionDate = String.format(dateFormat, "qr1.AssignedCollectionDate", SystemConstant.DATE_FORMAT);
		String assignedEndDate = String.format(dateFormat, "qr1.AssignedEndDate", SystemConstant.DATE_FORMAT);
		
		String sql = " select "
				+ " case when qr1.AssignedCollectionDate is null then " + assignedEndDate + " "
				+ " else " + assignedCollectionDate + " end as [date], "
				+ " count(distinct qr1.QuotationRecordId) as [total], "
				+ " count(distinct qr2.QuotationRecordId) as [count] "
				+ " from dbo.Assignment as a "
				+ " inner join dbo.QuotationRecord as qr1 on qr1.AssignmentId = a.AssignmentId "
				+ " inner join dbo.[User] as user_ on user_.UserId = qr1.UserId "
				+ " left join dbo.QuotationRecord as qr2 on qr2.[Status] in ('Submitted', 'Approved') and qr2.[Availability] != 2 "
				+ " and qr2.quotationState != 'IP' "
				+ " and qr1.QuotationRecordId = qr2.QuotationRecordId "
				+ "	where user_.UserId in (:userIds) and (qr1.AssignedCollectionDate is not null or qr1.AssignedEndDate is not null) "
				+ " and qr1.isBackNo = 0 "
				+ " group by"
				+ " case when qr1.AssignedCollectionDate is null then " + assignedEndDate + " "
				+ " else " + assignedCollectionDate + " end "
				+ " having count(distinct qr1.QuotationRecordId) != count(distinct qr2.QuotationRecordId) "
				+ " order by "
				+ " convert(date, case when qr1.AssignedCollectionDate is null then " + assignedEndDate + " "
				+ " else " + assignedCollectionDate + " end, 105) asc ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameterList("userIds", userIds);
		
		query.addScalar("date", StandardBasicTypes.STRING)
			.addScalar("total", StandardBasicTypes.LONG)
			.addScalar("count", StandardBasicTypes.LONG);
		
		query.setResultTransformer(Transformers.aliasToBean(DeadlineRowModel.class));
		
		return query.list();
		
//		String dateFormat = String.format("case when qr1.assignedCollectionDate is null then FORMAT(qr1.assignedEndDate, '%1$s', 'en-us') when qr1.assignedCollectionDate is not null then FORMAT(qr1.assignedCollectionDate, '%1$s', 'en-us') else '' end", SystemConstant.DATE_FORMAT);
//		String hql = " select " + dateFormat + " as date, "
//				+ " case when min(qr1.assignedCollectionDate) is null then min(qr1.assignedEndDate) else min(qr1.assignedCollectionDate) end as date2, "
//				+ " count(distinct qr1) as total, "
//				+ " count(distinct qr2) as count "
//				+ " from Assignment as a "
//				+ " inner join a.quotationRecords as qr1 "
//				+ " inner join qr1.user as user "
//				+ " left join a.quotationRecords as qr2 on qr2.status in ('Submitted', 'Approved') and qr2.availability!=2 and qr2.quotationState!='IP' and qr1 = qr2 "
//				+ " where user.userId in (:userIds) and (qr1.assignedCollectionDate is not null or qr1.assignedEndDate is not null) "
//				+ " and qr1.isBackNo = false "
////				+ " group by " + dateFormat
//				+ " group by qr1.assignedCollectionDate, qr1.assignedEndDate "
//				+ " having count(distinct qr1) != count(distinct qr2) "
////				+ " order by "+dateFormat+" asc ";
//				+ " order by date2 asc ";
//		
//		Query query = this.getSession().createQuery(hql);
//		query.setParameterList("userIds", userIds);
//		query.setResultTransformer(Transformers.aliasToBean(DeadlineRowModel.class));
//		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<capi.model.qualityControlManagement.QuotationRecordTableList> getTableListForPECheck(String search,
			int firstRecord, int displayLenght, Order order, Integer assignmentId) {
		
		String hql = "select q.quotationId as quotationId, "
				+ " u.chineseName as unitName, "
                + " case when spec1.productSpecificationId is null then '' "
                + "   else concat(spec1.specificationName, '=', spec1.value) end as productAttribute1, "
                + " case when spec2.productSpecificationId is null then '' "
                + "   else concat(spec2.specificationName, '=', spec2.value) end as productAttribute2, "
                + " case when spec3.productSpecificationId is null then '' "
                + "   else concat(spec3.specificationName, '=', spec3.value) end as productAttribute3, "
				+ " r.nPrice as nPrice, "
				+ " r.sPrice as sPrice, "
				+ " r.discount as discount, "
				+ " case when count(distinct sp.id) > 0 then 'Y' else 'N' end as subPrice, "
				+ " r.outletDiscountRemark as discountReason, "
				+ " r.quotationRecordId as quotationRecordId "
                + " from QuotationRecord as r "
                + " left join r.assignment as a "
                + " left join r.quotation as q "
                + " left join q.unit as u "
                + " left join r.product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left outer join r.subPriceRecords as sp "
                + " where a.assignmentId = :assignmentId "
                + " and r.isBackNo = false";

		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
        		+ " str(r.quotationRecordId) like :search or "
        		+ " u.chineseName like :search or "
                + " spec1.specificationName like :search or spec1.value like :search or "
                + " spec2.specificationName like :search or spec2.value like :search or "
                + " spec3.specificationName like :search or spec3.value like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search or "
                + " r.outletDiscountRemark like :search "
                + " ) ";
		}
		
		hql += " group by q.quotationId, "
                + " u.chineseName, "
                + " spec1.productSpecificationId,spec1.specificationName,spec1.value, "
                + " spec2.productSpecificationId,spec2.specificationName,spec2.value, "
                + " spec3.productSpecificationId,spec3.specificationName,spec3.value, "
                + " r.nPrice, r.sPrice, r.discount, r.outletDiscountRemark, "
                + " r.quotationRecordId "
                + " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = getSession().createQuery(hql);

		if (assignmentId != null) {
			query.setParameter("assignmentId", assignmentId);
		}

		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLenght);

		query.setResultTransformer(Transformers.aliasToBean(capi.model.qualityControlManagement.QuotationRecordTableList.class));

		return query.list();
	}
	
	public long countTableListForPECheck(String search, Integer assignmentId) {

		String hql = "select count(distinct r.quotationRecordId) "
                + " from QuotationRecord as r "
                + " left join r.assignment as a "
                + " left join r.quotation as q "
                + " left join q.unit as u "
                + " left join r.product as p "
                + " left join p.specificationViews as spec1 "
                + "   on spec1.sequence = 1 "
                + " left join p.specificationViews as spec2 "
                + "   on spec2.sequence = 2 "
                + " left join p.specificationViews as spec3 "
                + "   on spec3.sequence = 3 "
                + " left outer join r.subPriceRecords as sp "
                + " where a.assignmentId = :assignmentId "
                + " and r.isBackNo = false";

		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
        		+ " str(r.quotationRecordId) like :search or "
        		+ " u.chineseName like :search or "
                + " spec1.specificationName like :search or spec1.value like :search or "
                + " spec2.specificationName like :search or spec2.value like :search or "
                + " spec3.specificationName like :search or spec3.value like :search or "
                + " str(r.nPrice) like :search or str(r.sPrice) like :search or r.discount like :search or "
                + " r.outletDiscountRemark like :search "
                + " ) ";
		}
		

		Query query = getSession().createQuery(hql);


		query.setParameter("assignmentId", assignmentId);

		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		Long count = (Long)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public List<QuotationRecord> getQuotationRecordIds(List<Integer> unitIds, List<Integer> purposeIds, 
			List<Integer> quotationIds, List<Integer> assignmentIds, Date referenceMonth){
		Criteria criteria = this.createCriteria("qr");
		criteria.createAlias("qr.assignment", "a", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("a.surveyMonth", "sm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("qr.quotation", "q", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("q.unit", "u", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("u.purpose", "p", JoinType.LEFT_OUTER_JOIN);
		if (unitIds != null && unitIds.size() > 0){
			criteria.add(Restrictions.in("u.unitId", unitIds));
		}
		if (purposeIds != null && purposeIds.size() > 0){
			criteria.add(Restrictions.in("p.purposeId", purposeIds));
		}
		if (quotationIds != null && quotationIds.size() > 0){
			criteria.add(Restrictions.in("q.quotationId", quotationIds));
		}
		if (assignmentIds != null && assignmentIds.size() > 0){
			criteria.add(Restrictions.in("a.assignmentId", assignmentIds));
		}
		if (referenceMonth != null){
			criteria.add(Restrictions.eq("sm.referenceMonth", referenceMonth));
		}
		//criteria.setProjection(Projections.property("qr.quotationRecordIds"));
		
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<QuotationRecord> getQuotationRecordsForProductCycleReport(Date month, Integer unitId, ArrayList<Integer> productGroup) {
		Criteria c = this.createCriteria("qr")
				.createAlias("qr.assignment", "a")
				.createAlias("a.surveyMonth", "surveyMonth")
				.createAlias("qr.quotation", "q")
				.createAlias("q.unit", "u")
				.createAlias("qr.product", "product")
				.createAlias("product.productGroup", "productGroup")
				.add(Restrictions.eq("surveyMonth.referenceMonth", month))
				.add(Restrictions.eq("u.id", unitId));
		
		if (productGroup != null && productGroup.size() > 0)
			c.add(Restrictions.in("productGroup.id", productGroup));
		
		return c.list();
	}
	
	public List<QRDelinkReminderModel> getNotDeletedQRBeforeReferenceMonth(Date refMonth) {

		String referenceMonth = String.format("FORMAT(s.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select qr.quotationRecordId as quotationRecordId, a.assignmentId as assignmentId "
				+ ", case when s.referenceMonth is null then '' else " + referenceMonth + " end as referenceMonth "
				+ " from QuotationRecord as qr "
				+ " left join qr.assignment as a "
				+ " left join a.surveyMonth as s "
				+ " where s.referenceMonth < :refMonth "
				+ " order by s.referenceMonth asc ";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("refMonth", refMonth);
		query.setResultTransformer(Transformers.aliasToBean(QRDelinkReminderModel.class));
		
		return query.list();
	}
	
	public Long countByAssignment(int assignmentId) {
		return (Long)this.createCriteria("qr")
				.setProjection(Projections.count("id"))
				.add(Restrictions.eq("assignment.id", assignmentId))
				.uniqueResult();
	}
	
	
	public void overwriteDistrictHeadInQuotationRecordAndAssignment(int allocationBatchId) {
		
		String sql = "exec [dbo].[OverwriteDistrictHeadInQuotationRecordAndAssignment] :allocationBatchId";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
//
//		StringBuilder builder = new StringBuilder();
//		builder.append("<query>");
//		for (Integer districtId : districtHeadUserMap.keySet()){
//			builder.append("<districtHead>");
//			builder.append("<districtId>"+districtId+"</districtId>");
//			builder.append("<userId>"+districtHeadUserMap.get(districtId)+"</userId>");
//			builder.append("</districtHead>");
//		}
//		builder.append("</query>");
//		
//		query.setParameter("districtHeadList", builder.toString());
		
		query.setParameter("allocationBatchId", allocationBatchId);
		
		query.executeUpdate();
	}
	
	public long countQuotationRecordByUserInSurveyMonth(Integer userId, Integer surveyMonthId){
		Criteria criteria = this.createCriteria("qr")
				.createAlias("qr.assignment", "a", JoinType.INNER_JOIN)
				.createAlias("a.surveyMonth", "s", JoinType.INNER_JOIN)
				.createAlias("qr.user", "u", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("s.surveyMonthId", surveyMonthId));
		criteria.add(Restrictions.eq("u.userId", userId));
		
		criteria.setProjection(Projections.rowCount());
		
		return (long)criteria.uniqueResult();
		
	}
	
	public Double calculateManDayQuotationRecordByUserInSurveyMonth(Integer userId, Integer surveyMonthId){
		String sql = "select isnull(sum(whole.QuotationLoading), 0) + "
				+ " isnull(case when sum(summer.QuotationLoading) > sum(winter.QuotationLoading) "
				+ "	then sum(summer.QuotationLoading) "
				+ "	else sum(winter.QuotationLoading) end , 0) as total"
				+ " QuotationRecord as qr "
				+ "	inner join quotation as q "
				+ "	on q.quotationId = qr.QuotationId "
				+ " inner join Assignment as a "
				+ " on a.assignmentId = qr.assignmentId "
				+ "	left join unit as fullSeason on "
				+ "		q.UnitId = fullSeason.UnitId and fullSeason.Seasonality = 1 or fullSeason.Seasonality = 4 "
				+ "	left join Quotation as whole "
				+ "		on whole.QuotationId = q.QuotationId and whole.UnitId = fullSeason.UnitId "
				+ "	left join unit as summerSeason "
				+ " 	on q.UnitId = summerSeason.UnitId and summerSeason.Seasonality = 2 "
				+ "	left join Quotation as summer "
				+ "		on summer.QuotationId = q.QuotationId and summer.UnitId = summerSeason.UnitId "
				+ "	left join unit as winterSeason on "
				+ "		q.UnitId = winterSeason.UnitId and winterSeason.Seasonality = 3 "
				+ "	left join Quotation as winter "
				+ "		on winter.QuotationId = q.QuotationId and winter.UnitId = winterSeason.UnitId "
				+ "	where qr.UserId = :userId and a.surveyMonthId = :surveyMonthId ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("surveyMonthId", surveyMonthId);
		query.addScalar("total", StandardBasicTypes.DOUBLE);
		
		return (Double)query.uniqueResult();
	}
	
	public List getAllQuotationRecordResultByType(Date refMonth, Integer subPriceTypeId, List<Integer> subPriceMappingIds, Integer purposeId){
		
		String sql = "Select qr.quotationRecordId as quotationRecordId"
				+ ", qr.quotationState as quotationState"
				+ ", qr.referenceDate as referenceDate"
				+ ", qr.collectionDate as collectionDate"
				+ ", qr.nPrice as nPrice"
				+ ", qr.sPrice as sPrice"
				+ ", qr.uomValue as uomValue"
				+ ", qr.fr as fr"
				+ ", qr.isFRPercentage as isFRPercentage"
				+ ", qr.isConsignmentCounter as isConsignmentCounter"
				+ ", qr.consignmentCounterRemark as consignmentCounterRemark"
				+ ", qr.reason as reason"
				+ ", qr.discount as discount"
				+ ", qr.remark as quotationRecordRemark"
				+ ", qr.availability as availability"
				+ ", qr.categoryRemark as categoryRemark"
				+ ", qr.contactPerson as contactPerson"
				+ ", qr.isBackNo as isBackNo"
				+ ", qr.isBackTrack as isBackTrack"
				+ ", qr.formDisplay as formDisplay"
				+ ", qr.productRemark as productRemark"
				+ ", qr.isProductChange as isProductChange"
				+ ", qr.verificationRemark as verificationRemark"
				+ ", qr.rejectReason as rejectReason"
				+ ", qr.peCheckRemark as peCheckRemark"
				+ ", qr.isNewProduct as isNewProduct"
				+ ", qr.status as quotationRecordStatus"
				+ ", qr.isNewRecruitment as isNewRecruitment"
				+ ", qr.isNewOutlet as isNewOutlet"
				+ ", qr.isCollectFR as isCollectFR"
				+ ", qr.discountRemark as discountRemark"
				+ ", qr.consignmentCounterName as consignmentCounterName"
				+ ", qr.verificationReply as verificationReply"
				+ ", qr.outletDiscountRemark as outletDiscountRemark"
				+ ", qr.firmStatus as firmStatus"
				+ ", qr.isSPricePeculiar as isSPricePeculiar"
				+ ", qr.originalQuotationRecordId as originalQuotationRecordId"
				+ ", qr.productPosition as productPosition"
				+ ", qr.verifyFirm as isVerifyFirm"
				+ ", qr.verifyCategory as isVerifyCategory"
				+ ", qr.verifyQuotation as isVerifyQuotation"
				+ ", qr.isVisited as isVisited"
				+ ", qr.modifiedBy as modifiedBy"
				+ ", qr.modifiedDate as modifiedDate"
				//Quotation
				+ ", q.quotationId as quotationId"
				+ ", q.cpiCompilationSeries as cpiCompilationSeries"
				+ ", q.frAdmin as frAdmin"
				+ ", q.frField as frField"
				+ ", q.icpProductCode as icpProductCode"
				+ ", q.icpProductName as icpProductName"
				+ ", q.icpType as quotationICPType"
				+ ", q.isICP as isICP"
				+ ", q.status as quotationStatus"
				+ ", q.ruaDate as ruaDate"
				//District
				+ ", d.code as districtCode"
				//Unit
				+ ", u.chineseName as unitChineseName"
				+ ", u.englishName as unitEnglishName"
				+ ", u.code as unitCode"
				+ ", u.cpiBasePeriod as cpiBasePeriod"
				+ ", u.seasonality as seasonality"
				+ ", u.icpType as unitICPType"
				+ ", u.uomValue as unitUOMValue"
				+ ", u.cpiQoutationType as cpiQuotationType"
				//Purpose
				+ ", pp.code as purposeCode"
				//Product
				+ ", p.barcode as barcode"
				+ ", p.countryOfOrigin as countryOfOrigin"
				+ ", p.productId as productId"
				//ProductGroup
				+ ", pg.productGroupId as productGroupId"
				+ ", pg.chineseName as productGroupChineseName"
				+ ", pg.code as productGroupCode"
				+ ", pg.englishName as productGroupEnglishName"
				//ProductAttribute
				+ ", pa1.specificationName as pa1Name"
				+ ", pa2.specificationName as pa2Name"
				+ ", pa3.specificationName as pa3Name"
				+ ", pa4.specificationName as pa4Name"
				+ ", pa5.specificationName as pa5Name"
				//ProductSpecification
				+ ", ps1.value as ps1Value"
				+ ", ps2.value as ps2Value"
				+ ", ps3.value as ps3Value"
				+ ", ps4.value as ps4Value"
				+ ", ps5.value as ps5Value"
				//outlet
				+ ", o.name as outletName"
				+ ", o.firmCode as outletCode"
				//batch
				+ ", b.code as batchCode"
				//Assignment
				+ ", a.assignmentId as assignmentId"
				//SurveyMonth
				+ ", sm.referenceMonth as surveyMonth"
				//User
				+ ", us.staffCode as staffCode"
				//UOM
				+ ", um.englishName as uomEnglishName"
				+ ", um.chineseName as uomChineseName"
				//Unit.UOM
				+ ", uuom.englishName as unitUOMEnglishName"
				//Unit.SubItem.OutletType
				+ ", ot.code as outletTypeCode"
				+ ", ot.englishName as outletTypeEnglishName"
				//Assignment.TPU
				+ ", tp.code as tpuCode";
		
		if(subPriceTypeId!=null){
			sql += ", sr.subPriceRecordId as subPriceRecordId"
					+ ", sr.sPrice as srSPrice"
					+ ", sr.nPrice as srNPrice"
					+ ", sr.discount as srDiscount";
		}
		
		if(subPriceMappingIds!=null && subPriceMappingIds.size()>0){
			for(int i=0; i < subPriceMappingIds.size(); i++){
				sql += ", sc"+i+".columnValue as columnValue"+i;
			}
		}
		
		sql += " From [QuotationRecord] as qr"
				+ " left join [Quotation] as q on qr.quotationId = q.quotationId"
				+ " left join [Outlet] as o on qr.outletId = o.outletId"
				+ " left join [Assignment] as a on qr.assignmentId = a.assignmentId"
				+ " left join [Product] as p on p.productId = qr.productId"
				+ " left join [ProductGroup] as pg on pg.productGroupId = p.productGroupId"
				+ " left join [Batch] as b on q.batchId = b.batchId"
				+ " left join [Unit] as u on q.unitId = u.unitId"
				+ " left join [Purpose] as pp on u.purposeId = pp.purposeId"
				+ " left join [SurveyMonth] as sm on a.surveyMonthId = sm.surveyMonthId"
				+ " left join [User] as us on us.userId = qr.userId"
				+ " left join [Uom] as um on um.uomId = qr.uomId"
				+ " left join [Uom] as uuom on uuom.uomId = u.StandardUOMId"
				+ " left join [SubItem] as si on si.subItemId = u.subItemId"
				+ " left join [OutletType] as ot on si.outletTypeId = ot.outletTypeId"
				+ " left join [Tpu] as tp on o.tpuId = tp.tpuId"
				+ " left join [District] as d on d.districtId = tp.districtId"
				+ " left join [ProductAttribute] as pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.[Sequence] = 1"
				+ " left join [ProductAttribute] as pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.[Sequence] = 2"
				+ " left join [ProductAttribute] as pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.[Sequence] = 3"
				+ " left join [ProductAttribute] as pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.[Sequence] = 4"
				+ " left join [ProductAttribute] as pa5 on pa5.ProductGroupId = pg.ProductGroupId and pa5.[Sequence] = 5"
				+ " left join [ProductSpecification] as ps1 on ps1.ProductId = p.ProductId and pa1.ProductAttributeId = ps1.ProductAttributeId"
				+ " left join [ProductSpecification] as ps2 on ps2.ProductId = p.ProductId and pa2.ProductAttributeId = ps2.ProductAttributeId"
				+ " left join [ProductSpecification] as ps3 on ps3.ProductId = p.ProductId and pa3.ProductAttributeId = ps3.ProductAttributeId"
				+ " left join [ProductSpecification] as ps4 on ps4.ProductId = p.ProductId and pa4.ProductAttributeId = ps4.ProductAttributeId"
				+ " left join [ProductSpecification] as ps5 on ps5.ProductId = p.ProductId and pa5.ProductAttributeId = ps5.ProductAttributeId";
				
		if(subPriceMappingIds!=null && subPriceMappingIds.size()>0){
			sql += " left join [SubPriceRecord] as sr on sr.quotationRecordId = qr.quotationRecordId"
					+ " left join [SubPriceType] as st on st.subPriceTypeId = sr.subPriceTypeId";
			int i = 0;
			for(Integer subPriceMappingId : subPriceMappingIds){
				sql += " left join [SubPriceColumn] as sc"+i+" on sc"+i+".subPriceRecordId = sr.subPriceRecordId and sc"+i+".subPriceFieldMappingId = "+subPriceMappingId;
				i++;
			}
		}
				
		sql	+= " where sm.referenceMonth = :refMonth";
		if (purposeId != null){
			sql += " and pp.purposeId = :purposeId";
		}
		
		if(subPriceTypeId!=null){
			sql	+= " and st.subPriceTypeId = :subPriceTypeId";
		}
		
		sql += " group by qr.quotationRecordId"
				+ ", qr.quotationState"
				+ ", qr.referenceDate"
				+ ", qr.collectionDate"
				+ ", qr.nPrice"
				+ ", qr.sPrice"
				+ ", qr.uomValue"
				+ ", qr.fr"
				+ ", qr.isFRPercentage"
				+ ", qr.isConsignmentCounter"
				+ ", qr.consignmentCounterRemark"
				+ ", qr.reason"
				+ ", qr.discount"
				+ ", qr.remark"
				+ ", qr.availability"
				+ ", qr.categoryRemark"
				+ ", qr.contactPerson"
				+ ", qr.isBackNo"
				+ ", qr.isBackTrack"
				+ ", qr.formDisplay"
				+ ", qr.productRemark"
				+ ", qr.isProductChange"
				+ ", qr.verificationRemark"
				+ ", qr.rejectReason"
				+ ", qr.peCheckRemark"
				+ ", qr.isNewProduct"
				+ ", qr.status"
				+ ", qr.isNewRecruitment"
				+ ", qr.isNewOutlet"
				+ ", qr.isCollectFR"
				+ ", qr.discountRemark"
				+ ", qr.consignmentCounterName"
				+ ", qr.verificationReply"
				+ ", qr.outletDiscountRemark"
				+ ", qr.firmStatus"
				+ ", qr.isSPricePeculiar"
				+ ", qr.originalQuotationRecordId"
				+ ", qr.productPosition"
				+ ", qr.verifyFirm"
				+ ", qr.verifyCategory"
				+ ", qr.verifyQuotation"
				+ ", qr.isVisited"
				+ ", qr.modifiedBy"
				+ ", qr.modifiedDate"
				+ ", q.quotationId"
				+ ", q.cpiCompilationSeries"
				+ ", q.frAdmin"
				+ ", q.frField"
				+ ", q.icpProductCode"
				+ ", q.icpProductName"
				+ ", q.icpType"
				+ ", q.isICP"
				+ ", q.status"
				+ ", q.ruaDate"
				+ ", d.code"
				+ ", u.chineseName"
				+ ", u.englishName"
				+ ", u.code"
				+ ", u.cpiBasePeriod"
				+ ", u.seasonality"
				+ ", u.icpType"
				+ ", u.uomValue"
				+ ", u.cpiQoutationType"
				+ ", pp.code"
				+ ", p.barcode"
				+ ", p.countryOfOrigin"
				+ ", p.productId"
				+ ", pg.productGroupId"
				+ ", pg.chineseName"
				+ ", pg.code"
				+ ", pg.englishName"
				+ ", pa1.specificationName"
				+ ", pa2.specificationName"
				+ ", pa3.specificationName"
				+ ", pa4.specificationName"
				+ ", pa5.specificationName"
				+ ", ps1.value"
				+ ", ps2.value"
				+ ", ps3.value"
				+ ", ps4.value"
				+ ", ps5.value"
				+ ", o.name"
				+ ", o.firmCode"
				+ ", b.code"
				+ ", a.assignmentId"
				+ ", sm.referenceMonth"
				+ ", us.staffCode"
				+ ", um.englishName"
				+ ", um.chineseName"
				+ ", uuom.englishName"
				+ ", ot.code"
				+ ", ot.englishName"
				+ ", tp.code";
		
		if(subPriceTypeId!=null){
			sql += ", sr.subPriceRecordId"
					+ ", sr.sPrice"
					+ ", sr.nPrice"
					+ ", sr.discount"
					+ ", sr.sequence";
		}
		
		if(subPriceMappingIds!=null && subPriceMappingIds.size()>0){
			for(int i=0; i < subPriceMappingIds.size(); i++){
				sql += ", sc"+i+".columnValue";
			}
		}
		
		sql += " order by qr.quotationRecordId";
		if(subPriceTypeId!=null){
			sql	+= ", sr.sequence";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("refMonth", refMonth);
		if (purposeId != null){
			query.setParameter("purposeId", purposeId);
		}
		
		if(subPriceTypeId!=null){
			query.setParameter("subPriceTypeId", subPriceTypeId);
		}
		
		// QuotationRecord
		query.addScalar("quotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationState", StandardBasicTypes.STRING);
		query.addScalar("referenceDate", StandardBasicTypes.DATE);
		query.addScalar("collectionDate", StandardBasicTypes.DATE);
		query.addScalar("nPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("sPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("uomValue", StandardBasicTypes.DOUBLE);
		query.addScalar("fr", StandardBasicTypes.DOUBLE);
		query.addScalar("isFRPercentage", StandardBasicTypes.BOOLEAN);
		query.addScalar("isConsignmentCounter", StandardBasicTypes.BOOLEAN);
		query.addScalar("consignmentCounterRemark", StandardBasicTypes.STRING);
		query.addScalar("reason", StandardBasicTypes.STRING);
		query.addScalar("discount", StandardBasicTypes.STRING);
		query.addScalar("quotationRecordRemark", StandardBasicTypes.STRING);
		query.addScalar("availability", StandardBasicTypes.INTEGER);
		query.addScalar("categoryRemark", StandardBasicTypes.STRING);
		query.addScalar("contactPerson", StandardBasicTypes.STRING);
		query.addScalar("isBackNo", StandardBasicTypes.BOOLEAN);
		query.addScalar("isBackTrack", StandardBasicTypes.BOOLEAN);
		query.addScalar("formDisplay", StandardBasicTypes.INTEGER);
		query.addScalar("productRemark", StandardBasicTypes.STRING);
		query.addScalar("verificationRemark", StandardBasicTypes.STRING);
		query.addScalar("rejectReason", StandardBasicTypes.STRING);
		query.addScalar("peCheckRemark", StandardBasicTypes.STRING);
		query.addScalar("isNewProduct", StandardBasicTypes.BOOLEAN);
		query.addScalar("quotationRecordStatus", StandardBasicTypes.STRING);
		query.addScalar("isNewRecruitment", StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewOutlet", StandardBasicTypes.BOOLEAN);
		query.addScalar("isCollectFR", StandardBasicTypes.BOOLEAN);
		query.addScalar("discountRemark", StandardBasicTypes.STRING);
		query.addScalar("consignmentCounterName", StandardBasicTypes.STRING);
		query.addScalar("verificationReply", StandardBasicTypes.STRING);
		query.addScalar("outletDiscountRemark", StandardBasicTypes.STRING);
		query.addScalar("firmStatus", StandardBasicTypes.INTEGER);
		query.addScalar("isSPricePeculiar", StandardBasicTypes.BOOLEAN);
		query.addScalar("originalQuotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("productPosition", StandardBasicTypes.STRING);
		query.addScalar("isVerifyFirm", StandardBasicTypes.BOOLEAN);
		query.addScalar("isVerifyCategory", StandardBasicTypes.BOOLEAN);
		query.addScalar("isVerifyQuotation", StandardBasicTypes.BOOLEAN);
		query.addScalar("isVisited", StandardBasicTypes.BOOLEAN);
		query.addScalar("isProductChange", StandardBasicTypes.BOOLEAN);
		query.addScalar("modifiedBy", StandardBasicTypes.STRING);
		query.addScalar("modifiedDate", StandardBasicTypes.DATE);
		//Quotation
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		query.addScalar("cpiCompilationSeries", StandardBasicTypes.STRING);
		query.addScalar("frAdmin", StandardBasicTypes.DOUBLE);
		query.addScalar("frField", StandardBasicTypes.DOUBLE);
		query.addScalar("icpProductCode", StandardBasicTypes.STRING);
		query.addScalar("icpProductName", StandardBasicTypes.STRING);
		query.addScalar("quotationICPType", StandardBasicTypes.STRING);
		query.addScalar("isICP", StandardBasicTypes.BOOLEAN);
		query.addScalar("quotationStatus", StandardBasicTypes.STRING);
		query.addScalar("ruaDate", StandardBasicTypes.DATE);
		query.addScalar("cpiCompilationSeries", StandardBasicTypes.STRING);
		//District
		query.addScalar("districtCode", StandardBasicTypes.STRING);
		//Unit
		query.addScalar("unitChineseName", StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName", StandardBasicTypes.STRING);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("seasonality", StandardBasicTypes.INTEGER);
		query.addScalar("unitICPType", StandardBasicTypes.STRING);
		query.addScalar("unitUOMValue", StandardBasicTypes.DOUBLE);
		query.addScalar("cpiQuotationType", StandardBasicTypes.INTEGER);
		//Purpose
		query.addScalar("purposeCode", StandardBasicTypes.STRING);
		//Product
		query.addScalar("barcode", StandardBasicTypes.STRING);
		query.addScalar("countryOfOrigin", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		//ProductGroup
		query.addScalar("productGroupId", StandardBasicTypes.INTEGER);
		query.addScalar("productGroupChineseName", StandardBasicTypes.STRING);
		query.addScalar("productGroupEnglishName", StandardBasicTypes.STRING);
		query.addScalar("productGroupCode", StandardBasicTypes.STRING);
		//ProductAttribute
		query.addScalar("pa1Name", StandardBasicTypes.STRING);
		query.addScalar("pa2Name", StandardBasicTypes.STRING);
		query.addScalar("pa3Name", StandardBasicTypes.STRING);
		query.addScalar("pa4Name", StandardBasicTypes.STRING);
		query.addScalar("pa5Name", StandardBasicTypes.STRING);
		//ProductSpecification
		query.addScalar("ps1Value", StandardBasicTypes.STRING);
		query.addScalar("ps2Value", StandardBasicTypes.STRING);
		query.addScalar("ps3Value", StandardBasicTypes.STRING);
		query.addScalar("ps4Value", StandardBasicTypes.STRING);
		query.addScalar("ps5Value", StandardBasicTypes.STRING);
		//outlet
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("outletCode", StandardBasicTypes.INTEGER);
		//batch
		query.addScalar("batchCode", StandardBasicTypes.STRING);
		//Assignment
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		//SurveyMonth
		query.addScalar("surveyMonth", StandardBasicTypes.DATE);
		//User
		query.addScalar("staffCode", StandardBasicTypes.STRING);
		//UOM
		query.addScalar("uomEnglishName", StandardBasicTypes.STRING);
		query.addScalar("uomChineseName", StandardBasicTypes.STRING);
		//Unit.UOM
		query.addScalar("unitUOMEnglishName", StandardBasicTypes.STRING);
		//Unit.SubItem.OutletType
		query.addScalar("outletTypeCode", StandardBasicTypes.STRING);
		query.addScalar("outletTypeEnglishName", StandardBasicTypes.STRING);
		//Assignment.TPU
		query.addScalar("tpuCode", StandardBasicTypes.STRING);
		//SubPriceRecord
		if (subPriceTypeId!=null){
			query.addScalar("subPriceRecordId", StandardBasicTypes.INTEGER);
			query.addScalar("srSPrice", StandardBasicTypes.STRING);
			query.addScalar("srNPrice", StandardBasicTypes.STRING);
			query.addScalar("srDiscount", StandardBasicTypes.STRING);
		}
		
		if(subPriceMappingIds!=null && subPriceMappingIds.size()>0){
			for(int i=0; i < subPriceMappingIds.size(); i++){
				query.addScalar("columnValue"+i, StandardBasicTypes.STRING);
			}
		}
		
		
		
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		return query.list();
	}
	
	public List<QuotationRecordProgress.QRProgress> getQRProgress(List<Integer> purpose, List<Integer> unitId, 
			List<Integer> cpiSurveyForm, List<Integer> batch, Date startMonth, Date endMonth, 
			List<Integer> groups, List<Integer> subgroups, List<String> cpiBasePeriods){
		
		String sql = "Select g.groupId as groupId"
				+ ", g.code as groupCode"
				+ ", g.chineseName as groupChineseName"
				+ ", g.englishName as groupEnglishName"
				+ ", sg.subGroupId as subGroupId"
				+ ", sg.code as subGroupCode"
				+ ", sg.chineseName as subGroupChineseName"
				+ ", sg.englishName as subGroupEnglishName"
				+ ", u.CPIBasePeriod as cpiBasePeriod"
				+ ", count(distinct total.QuotationRecordId) as qrTotal"
				+ ", count(distinct unstarted.QuotationRecordId) as unstarted"
				+ ", count(distinct routine.QuotationRecordId) as routine"
				+ ", count(distinct verification.QuotationRecordId) as verification"
				+ ", count(distinct revisit.QuotationRecordId) as revisit"
				+ " From [Group] as g"
				+ "	inner join [SubGroup] as sg on g.GroupId = sg.GroupId"
				+ "	inner join [Item] as i on i.SubGroupId = sg.SubGroupId"
				+ "	inner join [OutletType] as ot on ot.ItemId = i.ItemId"
				+ "	inner join [SubItem] as si on si.OutletTypeId = ot.OutletTypeId"
				+ "	inner join [Unit] as u on u.SubItemId = si.SubItemId"
				+ "		and u.obsoleteDate is null or cast(u.obsoleteDate as date) < cast(getDate() as date)"
				+ "		and (u.effectiveDate is null or cast(u.effectiveDate as date) >= cast(getDate() as date))"
				+ "		and u.[status] = 'Active'"
				+ "	inner join [Quotation] as q on q.UnitId = u.UnitId"
				+ "	inner join [Batch] as b on q.BatchId = b.BatchId"
				+ "	left join [Purpose] as pp on u.PurposeId = pp.PurposeId"
				+ "	left join [QuotationRecord] as allQr on q.QuotationId = allQr.QuotationId"
				+ "		and allQr.ReferenceDate >= :startMonth and allQr.ReferenceDate <= :endMonth"
				+ "		and allQr.IsBackNo = 0"
				+ "	left join [QuotationRecord] as total on allQr.QuotationRecordId = total.QuotationRecordId"
				+ "	left join [QuotationRecord] as unstarted on unstarted.QuotationRecordId = allQr.QuotationRecordId"
				+ "		and ( unstarted.assignedStartDate > getDate() or unstarted.assignedCollectionDate > getDate() )"
				+ "	left join [QuotationRecord] as routine on routine.QuotationRecordId = allQr.QuotationRecordId"
				+ "		and routine.[Status] <> 'Approved'"
				+ "	left join [QuotationRecord] as verification on verification.QuotationRecordId = allQr.QuotationRecordId"
				+ "		and verification.quotationState = 'Verify'"
				+ "	left join [QuotationRecord] as revisit on revisit.QuotationRecordId = allQr.QuotationRecordId"
				+ "		and revisit.quotationState = 'Revisit'"
				+ " where 1=1";
		

		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		
		if (unitId != null && unitId.size() > 0){
			sql += " and u.unitId in (:unitId) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		if (batch != null && batch.size() > 0){
			sql += " and b.batchId in (:batch) ";
		}
		
		if (groups != null && groups.size() > 0){
			sql += " and g.groupId in (:groups) ";
		}
		
		if (subgroups != null && subgroups.size() > 0){
			sql += " and sg.subGroupId in (:subgroups) ";
		}
		
		if (cpiBasePeriods != null && cpiBasePeriods.size() > 0){
			sql += " and u.cpiBasePeriod in (:cpiBasePeriods) ";
		}
		
		sql += " group by g.groupId, g.code, g.chineseName, g.englishName, sg.subGroupId, sg.code, sg.chineseName, sg.englishName, u.CPIBasePeriod";
		sql += " order by g.code asc, sg.code asc";

		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		if (batch != null && batch.size() > 0){
			query.setParameterList("batch", batch);
		}
		if (groups != null && groups.size() > 0){
			query.setParameterList("groups", groups);
		}
		if (subgroups != null && subgroups.size() > 0){
			query.setParameterList("subgroups", subgroups);
		}
		if (cpiBasePeriods != null && cpiBasePeriods.size() > 0){
			query.setParameterList("cpiBasePeriods", cpiBasePeriods);
		}
		
		query.addScalar("groupId", StandardBasicTypes.INTEGER);
		query.addScalar("groupCode", StandardBasicTypes.STRING);
		query.addScalar("groupChineseName", StandardBasicTypes.STRING);
		query.addScalar("groupEnglishName", StandardBasicTypes.STRING);
		query.addScalar("subGroupId", StandardBasicTypes.INTEGER);
		query.addScalar("subGroupCode", StandardBasicTypes.STRING);
		query.addScalar("subGroupChineseName", StandardBasicTypes.STRING);
		query.addScalar("subGroupEnglishName", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("qrTotal", StandardBasicTypes.LONG);
		query.addScalar("unstarted", StandardBasicTypes.LONG);
		query.addScalar("routine", StandardBasicTypes.LONG);
		query.addScalar("verification", StandardBasicTypes.LONG);
		query.addScalar("revisit", StandardBasicTypes.LONG);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordProgress.QRProgress.class));
		
		return query.list();
	}
	
//	public void overwriteDistrictHeadInQuotationRecordAndAssignment(HashMap<Integer, Integer> districtHeadUserMap, int allocationBatchId) {
//		
//		String sql = "exec [dbo].[OverwriteDistrictHeadInQuotationRecordAndAssignment] :districtHeadList, :allocationBatchId";
//		
//		SQLQuery query = this.getSession().createSQLQuery(sql);
//
//		StringBuilder builder = new StringBuilder();
//		builder.append("<query>");
//		for (Integer districtId : districtHeadUserMap.keySet()){
//			builder.append("<districtHead>");
//			builder.append("<districtId>"+districtId+"</districtId>");
//			builder.append("<userId>"+districtHeadUserMap.get(districtId)+"</userId>");
//			builder.append("</districtHead>");
//		}
//		builder.append("</query>");
//		
//		query.setParameter("districtHeadList", builder.toString());
//		
//		query.setParameter("allocationBatchId", allocationBatchId);
//		
//		query.executeUpdate();
//	}

	public List<Integer> getBackTrackQuotationRecordByQuotationRecordIds(List<Integer> records){
		
		Criteria criteria = this.createCriteria("bcd");
		
		criteria.add(Restrictions.in("originalQuotationRecord.id", records));
		criteria.add(Restrictions.eq("isBackTrack", true));
		
		criteria.setProjection(Projections.property("bcd.id"));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getQuotationRecordToBeApproveAllList(String search, List<Integer> userIds, Integer outletId, 
			String outletTypeId, Integer personInChargeId,
			Integer[] districtId, String unitCategory, Integer[] tpuId,
			Boolean isProductChange, Boolean isSPricePeculiar, Integer[] availability, Date refMonth, Integer[] purposeId) {
		
		String hql = "select r.quotationRecordId "
                + " from Assignment as a "
                + " inner join a.surveyMonth as s "
                + " inner join a.quotationRecords as r "
                + " inner join r.user as user "
                + " inner join r.outlet as o "
                + " left join o.outletTypes as ot "
                + " inner join o.tpu as tpu "
                + " inner join tpu.district as d "
                + " inner join r.quotation as q "
                + " inner join q.unit as unit "
                + " inner join unit.subItem as si "
                + " inner join si.outletType as ot2 "
                + " left join unit.purpose as p "
                + " where 1 = 1 ";
		
		hql += " and r.isNewRecruitment = false ";
		hql += " and r.availability <> 5 ";
		
		hql += " and r.isBackNo = false "
				+ " and r.status = 'Submitted' ";
		
		hql += " and r.isBackTrack = false ";
		
		if (userIds != null && userIds.size() > 0) {
			hql += " and user.supervisor.id in (:userIds) ";
		}
		
		if (outletId != null) {
			hql += " and o.id = :outletId ";
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
		if (!StringUtils.isBlank(unitCategory)) {
			hql += " and unit.unitCategory = :unitCategory ";
		}
		if (tpuId != null && tpuId.length > 0) {
			hql += " and tpu.id in :tpuId ";
		}
		if (isProductChange != null) {
			hql += " and r.isProductChange = :isProductChange ";
		}
		if (isSPricePeculiar != null) {
			hql += " and r.isSPricePeculiar = :isSPricePeculiar ";
		}
		if (availability != null && availability.length > 0) {
			 hql += " and r.availability in ( :availability )";
		}
		
		if (refMonth != null){
			hql += " and s.referenceMonth = :refMonth ";
		}
		
		if (purposeId != null && purposeId.length > 0) {
			hql += " and p.id in ( :purposeId ) ";
		}
		
		if (StringUtils.isNotEmpty(search)) {
			hql += " and ( "
					+ " o.name like :search or "
					+ " concat(substring(ot2.code, len(ot2.code)-2, 3), ' - ', ot2.englishName) like :search or "
					+ " user.englishName like :search or "
					+ " d.code like :search or "
					+ " tpu.code like :search or "
					+ " unit.unitCategory like :search or "
					+ " unit.displayName like :search or "
					+ " str(r.nPrice) like :search or "
					+ " str(r.sPrice) like :search or "
					+ " r.discount like :search or "
					+ " r.reason like :search or "
					+ " o.detailAddress like :search or "
					+ " r.quotationState like :search or "
					+ " r.remark like :search or "
					+ " r.categoryRemark like :search or"
					+ " p.code like :search "
	                + " ) ";
		}
		
		hql += " group by r.quotationRecordId, "
				+ " o.name, o.id, "
				+ " ot2.code, ot2.englishName, "
				+ " user.englishName, d.code, tpu.code, unit.unitCategory, "
				+ " unit.displayName, r.nPrice, r.sPrice, r.discount, r.reason, o.detailAddress, "
				+ " r.availability, r.isProductChange, r.quotationState, r.isSPricePeculiar, r.remark, r.categoryRemark, p.code ";
		
		Query query = getSession().createQuery(hql);

		if (userIds != null && userIds.size() > 0) {
			query.setParameterList("userIds", userIds);
		}
		
		if (refMonth != null){
			query.setParameter("refMonth", refMonth);
		}
		
		if (outletId != null) {
			query.setParameter("outletId", outletId);
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
		if (!StringUtils.isBlank(unitCategory)) {
			query.setParameter("unitCategory", unitCategory);
		}
		if (tpuId != null && tpuId.length > 0) {
			query.setParameterList("tpuId", tpuId);
		}
		if (isProductChange != null) {
			query.setParameter("isProductChange", isProductChange);
		}
		if (isSPricePeculiar != null) {
			query.setParameter("isSPricePeculiar", isSPricePeculiar);
		}
		if (availability != null && availability.length >0) {
			 query.setParameterList("availability", availability);
		}
		if (purposeId != null && purposeId.length > 0) {
			query.setParameterList("purposeId", purposeId);
		}
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}
		
		return query.list();
//		String hql = "select total "
//				+ " from Assignment as a "
//				+ " inner join a.surveyMonth as sm "
//				+ " inner join a.quotationRecords as total "
//				+ " left join a.quotationRecords as original on total = original ";
//		if(isProductChange != null) {
//			hql += " and original.isProductChange = :isProductChange ";
//		}
//		if(isSPricePeculiar != null) {
//			hql += " and original.isSPricePeculiar = :isSPricePeculiar";
//		}
//		if(availability != null && availability.length > 0) {
//			hql += " and original.availability in (:availability) ";
//		}
//		hql	+= " left join original.otherQuotationRecords as other on total = other "
//			
//			+ " inner join total.user as usr "
//			+ " left join usr.supervisor as sup "
//			+ " inner join total.outlet as o "
//			+ " left join o.outletTypes as ot "
//			+ " inner join o.tpu as t "
//			+ " inner join t.district as d "
//			+ " inner join total.quotation as q "
//			+ " inner join q.unit as u "
//			+ " inner join u.subItem as si "
//			+ " inner join si.outletType as ot2 "
//			+ " where 1 = 1 ";
//		
//		hql += " and a.isNewRecruitment = false "
//			+ " and total.availability <> 5 "
//			+ " and total.status = 'Submitted' "
//			+ " and (original.quotationRecordId is not null "
//			+ " or other.quotationRecordId is not null) ";
//		
//		if (supervisorId != null && supervisorId.size() > 0) {
//			hql += " and sup.userId in (:supervisorId) ";
//		}
//		if (outletId != null) {
//			hql += " and o.outletId = :outletId ";
//		}
//		if (!StringUtils.isBlank(outletTypeId)) {
//			hql += " and ot.shortCode = :outletTypeId ";
//		}
//		if (personInChargeId != null) {
//			hql += " and usr.userId = :personInChargeId ";
//		}
//		if (districtId != null && districtId.length > 0) {
//			hql += " and d.districtId in (:districtId) ";
//		}
//		if (!StringUtils.isBlank(unitCategory)) {
//			hql += " and u.unitCategory = :unitCategory ";
//		}
//		if (tpuId != null && tpuId.length > 0) {
//			hql += " and t.tpuId in (:tpuId) ";
//		}
//		/*if (isProductChange != null) {
//			hql += " and total.isProductChange = :isProductChange ";
//		}
//		if (isSPricePeculiar != null) {
//			hql += " and total.isSPricePeculiar = :isSPricePeculiar ";
//		}
//		if (availability != null && availability.length > 0) {
//			 hql += " and total.availability in (:availability) ";
//		}*/
//		if (referenceMonth != null){
//			hql += " and sm.referenceMonth = :referenceMonth ";
//		}
//		
//		if (StringUtils.isNotEmpty(search)) {
//			hql += " and ( "
//				+ " o.name like :search or "
//				+ " concat(substring(ot2.code, len(ot2.code)-2, 3), ' - ', ot2.englishName) like :search or "
//				+ " usr.englishName like :search or "
//				+ " d.code like :search or "
//				+ " t.code like :search or "
//				+ " u.unitCategory like :search or "
//				+ " u.displayName like :search or "
//				+ " str(total.nPrice) like :search or "
//				+ " str(total.sPrice) like :search or "
//				+ " total.discount like :search or "
//				+ " total.reason like :search or "
//				+ " o.detailAddress like :search or "
//				+ " total.quotationState like :search or "
//				+ " total.remark like :search or "
//				+ " total.categoryRemark like :search "
//				+ " ) ";
//		}
//		
//		Query query = this.getSession().createQuery(hql);
//		
//		if(isProductChange != null) {
//			query.setParameter("isProductChange", isProductChange);
//		}
//		if(isSPricePeculiar != null) {
//			query.setParameter("isSPricePeculiar", isSPricePeculiar);
//		}
//		if(availability != null && availability.length > 0) {
//			query.setParameterList("availability", availability);
//		}
//		if (supervisorId != null && supervisorId.size() > 0) {
//			query.setParameterList("supervisorId", supervisorId);
//		}
//		if (outletId != null) {
//			query.setParameter("outletId", outletId);
//		}
//		if (!StringUtils.isBlank(outletTypeId)) {
//			query.setParameter("outletTypeId", outletTypeId);
//		}
//		if (personInChargeId != null) {
//			query.setParameter("personInChargeId", personInChargeId);
//		}
//		if (districtId != null && districtId.length > 0) {
//			query.setParameterList("districtId", districtId);
//		}
//		if (!StringUtils.isBlank(unitCategory)) {
//			query.setParameter("unitCategory", unitCategory);
//		}
//		if (tpuId != null && tpuId.length > 0) {
//			query.setParameterList("tpuId", tpuId);
//		}
//		if (referenceMonth != null){
//			query.setParameter("referenceMonth", referenceMonth);
//		}
//		if (StringUtils.isNotEmpty(search)) {
//			query.setParameter("search", String.format("%%%s%%", search));
//		}
//		
//		return query.list();
	}
	
	public List<QuotationRecordSyncData> getHistoryQuotationRecordByAssignmentIds(List<Integer> assignmentIds) {
		String query1 = "SELECT DISTINCT qr.* FROM QuotationRecord qr, " + SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<QuotationRecordSyncData> result1 = addScalarForQuotationRecord(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(QuotationRecordSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		String query2 = "SELECT DISTINCT qr.* FROM QuotationRecord qr, " + SystemConstant.getQuotationHistoryByAssignmentIdsSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<QuotationRecordSyncData> result2 = addScalarForQuotationRecord(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(QuotationRecordSyncData.class))
				.setParameterList("assignmentIds", assignmentIds).list();

		List<QuotationRecordSyncData> returnResult = new ArrayList<QuotationRecordSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public List<QuotationRecordSyncData> getHistoryQuotationRecordByQuotationIdsHistoryDates(
			String quotationIdsHistoryDates) {
		String query1 = "SELECT DISTINCT qr.* FROM QuotationRecord qr, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.QuotationRecordId = a.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery1 = getSession().createSQLQuery(query1);
		List<QuotationRecordSyncData> result1 = addScalarForQuotationRecord(sqlQuery1)
				.setResultTransformer(Transformers.aliasToBean(QuotationRecordSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		String query2 = "SELECT DISTINCT qr.* FROM QuotationRecord qr, "
				+ SystemConstant.getQuotationHistoryByQuotationIdsHistoryDatesSQL
				+ "WHERE qr.isBackNo = 1 AND qr.OriginalQuotationRecordId = a.QuotationRecordId ORDER BY  1  DESC ";

		SQLQuery sqlQuery2 = getSession().createSQLQuery(query2);
		List<QuotationRecordSyncData> result2 = addScalarForQuotationRecord(sqlQuery2)
				.setResultTransformer(Transformers.aliasToBean(QuotationRecordSyncData.class))
				.setParameter("quotationIdsHistoryDates", quotationIdsHistoryDates).list();

		List<QuotationRecordSyncData> returnResult = new ArrayList<QuotationRecordSyncData>();
		returnResult.addAll(result1);
		returnResult.addAll(result2);
		return returnResult;
	}
	
	public SQLQuery addScalarForQuotationRecord(SQLQuery sqlQuery) {
		sqlQuery
		.addScalar("quotationRecordId", StandardBasicTypes.INTEGER)
		.addScalar("outletId", StandardBasicTypes.INTEGER).addScalar("productId", StandardBasicTypes.INTEGER)
		.addScalar("quotationId", StandardBasicTypes.INTEGER)
		.addScalar("createdDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("modifiedDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("assignmentId", StandardBasicTypes.INTEGER)
		.addScalar("quotationState", StandardBasicTypes.STRING)
		.addScalar("referenceDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("collectionDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("nPrice", StandardBasicTypes.DOUBLE).addScalar("sPrice", StandardBasicTypes.DOUBLE)
		.addScalar("isSPricePeculiar", StandardBasicTypes.BOOLEAN)
		.addScalar("uomValue", StandardBasicTypes.DOUBLE).addScalar("uomId", StandardBasicTypes.INTEGER)
		.addScalar("fr", StandardBasicTypes.DOUBLE).addScalar("isFRPercentage", StandardBasicTypes.BOOLEAN)
		.addScalar("isConsignmentCounter", StandardBasicTypes.BOOLEAN)
		.addScalar("consignmentCounterRemark", StandardBasicTypes.STRING)
		.addScalar("reason", StandardBasicTypes.STRING).addScalar("discount", StandardBasicTypes.STRING)
		.addScalar("remark", StandardBasicTypes.STRING).addScalar("availability", StandardBasicTypes.INTEGER)
		.addScalar("categoryRemark", StandardBasicTypes.STRING)
		.addScalar("contactPerson", StandardBasicTypes.STRING).addScalar("isBackNo", StandardBasicTypes.BOOLEAN)
		.addScalar("isBackTrack", StandardBasicTypes.BOOLEAN)
		.addScalar("formDisplay", StandardBasicTypes.INTEGER)
		.addScalar("productRemark", StandardBasicTypes.STRING)
		.addScalar("isProductChange", StandardBasicTypes.BOOLEAN)
		.addScalar("originalQuotationRecordId", StandardBasicTypes.INTEGER)
		.addScalar("verificationRemark", StandardBasicTypes.STRING)
		.addScalar("rejectReason", StandardBasicTypes.STRING)
		.addScalar("peCheckRemark", StandardBasicTypes.STRING)
		.addScalar("isNewProduct", StandardBasicTypes.BOOLEAN).addScalar("status", StandardBasicTypes.STRING)
		.addScalar("isNewRecruitment", StandardBasicTypes.BOOLEAN)
		.addScalar("isFlag", StandardBasicTypes.BOOLEAN).addScalar("isNewOutlet", StandardBasicTypes.BOOLEAN)
		.addScalar("userId", StandardBasicTypes.INTEGER).addScalar("isCollectFR", StandardBasicTypes.BOOLEAN)
		.addScalar("discountRemark", StandardBasicTypes.STRING)
		.addScalar("assignedCollectionDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("assignedStartDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("assignedEndDate", StandardBasicTypes.TIMESTAMP)
		.addScalar("firmStatus", StandardBasicTypes.INTEGER)
		.addScalar("consignmentCounterName", StandardBasicTypes.STRING)
		.addScalar("isVisited", StandardBasicTypes.BOOLEAN)
		.addScalar("verificationReply", StandardBasicTypes.STRING)
		.addScalar("validationError", StandardBasicTypes.STRING)
		.addScalar("passValidation", StandardBasicTypes.BOOLEAN)
		.addScalar("outletDiscountRemark", StandardBasicTypes.STRING)
		.addScalar("historyDate", StandardBasicTypes.DATE)
		.addScalar("isSpecifiedUser", StandardBasicTypes.BOOLEAN)
		.addScalar("isReleased", StandardBasicTypes.BOOLEAN)
		.addScalar("allocationBatchId", StandardBasicTypes.INTEGER)
		.addScalar("productPosition", StandardBasicTypes.STRING)
		.addScalar("verifyFirm", StandardBasicTypes.BOOLEAN)
		.addScalar("verifyCategory", StandardBasicTypes.BOOLEAN)
		.addScalar("verifyQuotation", StandardBasicTypes.BOOLEAN)
		// .addScalar("localId", StandardBasicTypes.INTEGER)
		// .addScalar("localDbRecordStatus", StandardBasicTypes.STRING)
		.addScalar("version", StandardBasicTypes.INTEGER)
		.addScalar("approvedDate", StandardBasicTypes.TIMESTAMP);		
		
		return sqlQuery;
	}	
	
	public List<QuotationRecord> getBlankQuotationRecord(Date referenceDate, Integer quotationId){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("quotation.quotationId", quotationId));
		criteria.add(Restrictions.gt("referenceDate", referenceDate));
		criteria.add(Restrictions.eq("status", "Blank"));
		
		return criteria.list();		
	}
	
	public void updateQuotationRecordStatusForApproval(List<Integer> ids, String status){
		String sql = "Update [QuotationRecord] set [Status] = :status where [QuotationRecordId] in :quotationRecordIds ";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameterList("quotationRecordIds", ids);
		query.setParameter("status", status);
		
		query.executeUpdate();
	}
	
	public Double getDistrictManDayRequiredByAllocationBatchIdUserId(Integer allocationBatchId, Integer userId) {
		String sql = " Select sum(districtTotal) as total"
				   + " From ("
						   + " Select isnull(sum(whole.QuotationLoading), 0) "
						   + " + isnull(case when isnull(sum(summer.QuotationLoading),0) > isnull(sum(winter.QuotationLoading) ,0) "
						   + " then sum(summer.QuotationLoading) else sum(winter.QuotationLoading) end, 0) as districtTotal"
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
						   	+ " Where qr.allocationBatchId = :allocationBatchId "
						   	+ " and qr.isBackNo = 0 "
						   	+ " and qr.isBackTrack = 0 "
						   	//+ " and qr.isSpecifiedUser <> 1 "
						   	+ " and d.userId = :userId "
						   	+ " group by d.districtId " 
				   	+ " ) districtRequiredManDay ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("allocationBatchId", allocationBatchId);
		query.setParameter("userId", userId);
		query.addScalar("total", StandardBasicTypes.DOUBLE);
		
		Double total = (Double)query.uniqueResult();
		return total != null ? total : 0 ;
		
	}
	
	public List<Integer> getCurrentConvertQuotationRecordId(List<Integer> ids){
		String hql = "Select distinct qr.quotationRecordId"
				+ " from QuotationRecord as qr"
				+ " inner join qr.quotation as q"
				+ " inner join q.unit as u"
				+ " left join qr.indoorQuotationRecord as iqr"
				+ " where qr.quotationRecordId in :ids"
				+ " and (u.minQuotation = 1 or iqr.indoorQuotationRecordId is not null)";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameterList("ids", ids);
		
		return query.list();
	}
	
	public List<String> searchDistinctDiscountForFilter(String search, int firstRecord, int displayLength) {
		String hql = "select distinct discount "
                + " from QuotationRecord "
                + " where discount is not null and discount <> '' ";

		if (StringUtils.isNotEmpty(search)) {
			hql += " and discount like :search ";
		}
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}
	
	/**
	 * Count discount select2 format
	 */
	public Long countDiscountSelect2(String search) {
		String hql = "select count(distinct discount) as count "
                + " from QuotationRecord "
                + " where discount is not null and discount <> '' ";

		if (StringUtils.isNotEmpty(search)) {
			hql += " and discount like :search ";
		}
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		return (long)query.uniqueResult();
	}
	
	public List<RUACaseReportIndividualReport> getRUAIndividualReport(Date refMonth, List<Integer> userIds
			, List<String> outletTypeShortCode, List<Integer> districtIds){
		String hql = "Select sm.surveyMonthId as surveyMonthId"
				+ ", sm.referenceMonth as referenceMonth "
				+ ", pp.purposeId as purposeId "
				+ ", pp.code as purpose "
				+ ", ur.userId as userId "
				+ ", ur.team as team "
				+ ", r.code as rank "
				+ ", ur.englishName as staffName "
				+ ", ur.staffCode as staffCode "
				+ ", substring(ot.code, len(ot.code)-2, 3) as outletTypeCode "
				+ ", ot.englishName as outletTypeName "
				+ " From QuotationRecord as qr "
				+ " left join qr.assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join qr.quotation as q "
				+ " left join qr.user as ur "
				+ " left join ur.rank as r "
				+ " left join q.unit as u "
				+ " left join u.subItem as si "
				+ " left join si.outletType as ot "
				+ " left join u.purpose as pp "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " where qr.isNewRecruitment = true "
				+ " and qr.isBackNo = false "
				+ " and qr.isBackTrack = false "
				+ " and qr.status = 'Approved' "
				+ " and sm.referenceMonth = :referenceMonth ";
		
		if (userIds != null && userIds.size() > 0){
			hql += " and ur.userId in ( :userIds ) ";
		}
		
		if (districtIds != null && districtIds.size() > 0){
			hql += " and d.districtId in ( :districtIds ) ";
		}
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			hql += " and (substring(ot.code, len(ot.code)-2, 3) in ( :outletTypeShortCode ))";
		}
		
		hql += " group by sm.surveyMonthId"
				+ ", sm.referenceMonth "
				+ ", pp.purposeId "
				+ ", pp.code "
				+ ", ur.userId "
				+ ", ur.team "
				+ ", r.code "
				+ ", ur.englishName "
				+ ", ur.staffCode "
				+ ", substring(ot.code, len(ot.code)-2, 3) "
				+ ", ot.englishName "
				+ " order by sm.referenceMonth, pp.code, outletTypeCode ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("referenceMonth", refMonth);
		
		if (userIds != null && userIds.size() > 0){
			query.setParameterList("userIds", userIds);
		}
		
		if (districtIds != null && districtIds.size() > 0){
			query.setParameterList("districtIds", districtIds);
		}
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			query.setParameterList("outletTypeShortCode", outletTypeShortCode);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(RUACaseReportIndividualReport.class));
		
		return query.list();
	}
	
	public List<RUACaseReportOverviewReport> getRUAOverviewReport(Date refMonth, List<Integer> userIds
			, List<Integer> districtIds){
		String hql = "Select sm.surveyMonthId as surveyMonthId "
				+ " , sm.referenceMonth as referenceMonth "
				+ " , pp.purposeId as purposeId "
				+ " , pp.code as purpose "
				+ " , case when pp.code = 'MRPS' then 1 "
					+ "	when pp.code = 'WPU' then 2 "
					+ " when pp.code = 'CEO' then 3 "
					+ " when pp.code = 'ICP' then 4 "
					+ " else 5 end as purposeSequence "
				+ " , ur.userId as userId "
				+ " , ur.team as team "
				+ " , ur.staffCode as staffCode "
				+ " , ur.englishName as staffName "
				+ " , r.code as rank "
				+ " From QuotationRecord as qr "
				+ " left join qr.assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join qr.quotation as q "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join qr.user as ur "
				+ " left join ur.rank as r "
				+ " left join q.unit as un "
				+ " left join un.purpose as pp "
				+ " where qr.isNewRecruitment = true "
				+ " and qr.isBackNo = false "
				+ " and qr.isBackTrack = false "
				+ " and qr.status = 'Approved' "
				+ " and sm.referenceMonth = :refMonth ";
		
		if (userIds != null && userIds.size() > 0){
			hql += " and ur.userId in ( :userIds ) ";
		}
		
		if (districtIds != null && districtIds.size() > 0){
			hql += " and d.districtId in ( :districtIds ) ";
		}
		
		hql += " group by sm.surveyMonthId"
				+ ", sm.referenceMonth "
				+ ", pp.purposeId "
				+ ", pp.code "
				+ ", ur.userId "
				+ ", ur.team "
				+ ", r.code "
				+ ", ur.englishName "
				+ ", ur.staffCode "
				+ " order by sm.referenceMonth, purposeSequence, ur.team, r.code, ur.staffCode ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("refMonth", refMonth);
		
		if (userIds != null && userIds.size() > 0){
			query.setParameterList("userIds", userIds);
		}
		
		if (districtIds != null && districtIds.size() > 0){
			query.setParameterList("districtIds", districtIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(RUACaseReportOverviewReport.class));
		
		return query.list();
	}
	
	public List<RUACaseReportOverviewReport> getRUAOverviewTeamReport(Date refMonth, List<Integer> userIds, List<Integer> districtIds){
		String hql = "Select sm.surveyMonthId as surveyMonthId "
				+ " , sm.referenceMonth as referenceMonth "
				+ " , pp.purposeId as purposeId "
				+ " , pp.code as purpose "
				+ " , case when pp.code = 'MRPS' then 1 "
					+ "	when pp.code = 'WPU' then 2 "
					+ " when pp.code = 'CEO' then 3 "
					+ " when pp.code = 'ICP' then 4 "
					+ " else 5 end as purposeSequence "
				+ " , ur.team as team "
				+ ", ur.userId as userId "
				+ " From QuotationRecord as qr "
				+ " left join qr.assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " left join qr.quotation as q "
				+ " left join qr.outlet as o "
				+ " left join o.tpu as t "
				+ " left join t.district as d "
				+ " left join qr.user as ur "
				+ " left join ur.rank as r "
				+ " left join q.unit as un "
				+ " left join un.purpose as pp "
				+ " where qr.isNewRecruitment = true "
				+ " and qr.isBackNo = false "
				+ " and qr.isBackTrack = false "
				+ " and qr.status = 'Approved' "
				+ " and sm.referenceMonth = :refMonth ";
		
		if (userIds != null && userIds.size() > 0){
			hql += " and ur.userId in ( :userIds ) ";
		}
		
		if (districtIds != null && districtIds.size() > 0){
			hql += " and d.districtId in ( :districtIds ) ";
		}
		
		hql += " group by sm.surveyMonthId"
				+ ", sm.referenceMonth "
				+ ", pp.purposeId "
				+ ", pp.code "
				+ ", ur.team "
				+ ", ur.userId"
				+ " order by sm.referenceMonth, purposeSequence, ur.team ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("refMonth", refMonth);
		
		if (userIds != null && userIds.size() > 0){
			query.setParameterList("userIds", userIds);
		}
		
		if (districtIds != null && districtIds.size() > 0){
			query.setParameterList("districtIds", districtIds);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(RUACaseReportOverviewReport.class));
		
		return query.list();
	}
	
	public List<FieldworkOutputByOutletTypeReport> getFieldworkOutletTypeReport(String user
			, String purpose, Date refMonth, String allocationBatch, String outletType){
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetFieldworkOutputByOutletTypeReport] :user, "
				+ " :purpose, :allocationBatch, :outletType, :refMonth");		
		
		query.setParameter("refMonth", refMonth);
		query.setParameter("purpose", purpose);
		query.setParameter("user", user);
		query.setParameter("allocationBatch", allocationBatch);
		query.setParameter("outletType", outletType);
		
		query.addScalar("referenceMonth", StandardBasicTypes.DATE);
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("purposeSequence", StandardBasicTypes.INTEGER);
		query.addScalar("batchName", StandardBasicTypes.STRING);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("rank", StandardBasicTypes.STRING);
		query.addScalar("collectionMethod", StandardBasicTypes.STRING);
		query.addScalar("staffCode", StandardBasicTypes.STRING);
		query.addScalar("staffName", StandardBasicTypes.STRING);
		query.addScalar("outletTypeName", StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode", StandardBasicTypes.STRING);
		query.addScalar("manDayRequired", StandardBasicTypes.DOUBLE);
		query.addScalar("completedAssignment", StandardBasicTypes.INTEGER);
		query.addScalar("completedQuotationRecord", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(FieldworkOutputByOutletTypeReport.class));
		
		return query.list();
	}
	
	public List<FieldworkOutputByDistrictReport> getFieldworkOutputByDistrictReport(String user
			, String purpose, Date refMonth, String allocationBatch, String district){
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetFieldworkOutputByDistrictReport] :refMonth, :purpose"
				+ ", :user, :allocationBatch, :district");
		
		query.setParameter("refMonth", refMonth);
		query.setParameter("purpose", purpose);
		query.setParameter("user", user);
		query.setParameter("allocationBatch", allocationBatch);
		query.setParameter("district", district);
		
		query.addScalar("referenceMonth", StandardBasicTypes.DATE);
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("userId", StandardBasicTypes.INTEGER);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("survey", StandardBasicTypes.STRING);
		query.addScalar("batchName", StandardBasicTypes.STRING);
		query.addScalar("team", StandardBasicTypes.STRING);
		query.addScalar("rank", StandardBasicTypes.STRING);
		query.addScalar("collectionMethod", StandardBasicTypes.STRING);
		query.addScalar("staffCode", StandardBasicTypes.STRING);
		query.addScalar("staffName", StandardBasicTypes.STRING);
		query.addScalar("manDayRequired", StandardBasicTypes.DOUBLE);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("countAssignment", StandardBasicTypes.INTEGER);
		query.addScalar("countQuotationRecord", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(FieldworkOutputByDistrictReport.class));
		
		return query.list();
	}
	
	public List<IndividualQuotationRecordReport> getIndividualQuoationRecordReport(List<Integer> purpose, List<Integer> assignmentIds,
			List<Integer> quotation, List<Integer> unit, List<Integer> quotationRecord, Date refMonth) {
		String openingStartTime = String.format("dbo.FormatTime(o.OpeningStartTime, '%s')", SystemConstant.TIME_FORMAT);
		String openingStartTime2 = String.format("dbo.FormatTime(o.OpeningStartTime2, '%s')", SystemConstant.TIME_FORMAT);
		String openingEndTime = String.format("dbo.FormatTime(o.OpeningEndTime, '%s')", SystemConstant.TIME_FORMAT);
		String openingEndTime2 = String.format("dbo.FormatTime(o.OpeningEndTime2, '%s')", SystemConstant.TIME_FORMAT);
		String convenientStartTime = String.format("dbo.FormatTime(o.ConvenientStartTime, '%s')", SystemConstant.TIME_FORMAT);
		String convenientEndTime = String.format("dbo.FormatTime(o.ConvenientEndTime, '%s')", SystemConstant.TIME_FORMAT);
		String convenientStartTime2 = String.format("dbo.FormatTime(o.ConvenientStartTime2, '%s')", SystemConstant.TIME_FORMAT);
		String convenientEndTime2 = String.format("dbo.FormatTime(o.ConvenientEndTime2, '%s')", SystemConstant.TIME_FORMAT);
		String createdDate = String.format("FORMAT(o.CreatedDate, '%s', 'en-us')", SystemConstant.REPORT_DATE_TIME_FORMAT);
		
		
		// TODO Auto-generated method stub
		String sql = "SELECT DISTINCT o.FirmCode AS [outletCode]"
//				+ " , a.ReferenceNo AS [referNo]"
				+ " , Concat(d.Code,o.FirmCode) AS [referNo]"
				+ " , o.OutletId AS [outletId], o.Name AS [outletName] "
				//v_15
				+ " , oto.shortcode AS [outletType] "
				/*
				+ " ,STUFF("
				+ " (SELECT DISTINCT ',' + ShortCode"
				+ " FROM OutletTypeOutlet"
				+ " WHERE OutletId = o.OutletId "
				+ " FOR XML PATH ('')), 1, 1, '') AS [outletType]"
				*/
				//+ " , SUBSTRING(ot.code, len(ot.code)-2, 3) AS [outletType] "
				//+ "	, ot.ChineseName AS [outletTypeChName], ot.EnglishName AS [outletTypeEngName], d.Code AS [districtCode] "
				// v_15
				+ "	, vw.ChineseName AS [outletTypeChName], vw.EnglishName AS outletTypeEngName "
				+ " , d.Code AS [districtCode] "
				
				+ " , d.EnglishName AS [districtEngName], tpu.CouncilDistrict AS [districtCouncil],tpu.Code AS [tpuCode],o.BRCode AS [brCode]"
				+ " , o.StreetAddress AS [mapAddress], o.DetailAddress AS [detailAddress],  o.LastContact AS [lastContact]"
				+ " , o.MainContact AS [mainContact], o.Tel AS [telNo], o.Fax AS [faxNo]"

				+ " , CASE WHEN o.OpeningStartTime IS NULL THEN '' ELSE "+openingStartTime+" END AS [openStartTime] "
				+ " , CASE WHEN o.OpeningStartTime2 IS NULL THEN '' ELSE "+openingStartTime2+" END AS [openStartTime2] "
				+ " , CASE WHEN o.OpeningEndTime IS NULL THEN '' ELSE "+openingEndTime+" END AS [openEndTime] "
				+ " , CASE WHEN o.OpeningEndTime2 IS NULL THEN '' ELSE "+openingEndTime2+" END AS [openEndTime2] "
				+ " , CASE WHEN o.ConvenientStartTime IS NULL THEN '' ELSE "+convenientStartTime+" END AS [converientStartTime] "
				+ " , CASE WHEN o.ConvenientStartTime2 IS NULL THEN '' ELSE "+convenientStartTime2+" END AS [converientStartTime2] "
				+ " , CASE WHEN o.ConvenientEndTime IS NULL THEN '' ELSE "+convenientEndTime+" END AS [convenientEndTime] "
				+ " , CASE WHEN o.ConvenientEndTime2 IS NULL THEN '' ELSE "+convenientEndTime2+" END AS [convenientEndTime2] "
				
				+ " , o.WebSite AS [website],  o.OutletMarketType AS [yNonmarket], o.Remark AS [outletRemarks], o.DiscountRemark AS [outletDiscountRemarks]"
				+ " , CASE WHEN o.CollectionMethod = 1 THEN 'Field Visit' WHEN o.CollectionMethod = 2 THEN 'Telephone' WHEN o.CollectionMethod = 3 THEN 'Fax' ELSE 'Other' END AS [collectionMethod] "
				+ " , o.MarketName AS [defaultMajorLocation], o.IndoorMarketName AS [indoorMarketName] "
				+ " , o.Longitude AS [longitude], o.Latitude AS [latitude] "
				+ " , o.IsUseFRAdmin AS [isUseFRAdmin], o.Status AS [status], o.CreatedBy AS [createdBy] "

				+ " , CASE WHEN o.CreatedDate IS NULL THEN '' ELSE "+createdDate+" END AS [createdDate] "
				
				+ " FROM [QuotationRecord] as qr "
				+ " LEFT JOIN [Assignment] as a on a.AssignmentId = qr.AssignmentId "
				+ " LEFT JOIN [SurveyMonth] as sm on sm.SurveyMonthId = a.SurveyMonthId "
				+ " LEFT JOIN [Outlet] as o on o.OutletId = qr.OutletId "
				+ " LEFT JOIN [Tpu] as tpu on tpu.TpuId = o.TpuId "
				//+ " LEFT JOIN [District] as d on d.DistrictId = tpu.TpuId "
				+ " LEFT JOIN [District] as d on d.DistrictId = tpu.DistrictId "

				+ " LEFT JOIN [Quotation] as q on q.QuotationId = qr.QuotationId "
				+ " LEFT JOIN [Unit] as u on u.UnitId = q.UnitId "
				+ " LEFT JOIN [SubItem] as si on si.SubItemId = u.SubItemId "
				//+ " LEFT JOIN [OutletType] ot on ot.OutletTypeId = o.OutletId "
				+ " LEFT JOIN [OutletType] ot on  ot.outletTypeId = si.OutletTypeId "
				+ "	LEFT JOIN [Purpose] as pp on u.PurposeId = pp.PurposeId"
				// v_15
				+ " LEFT JOIN OutletTypeOutlet as oto ON oto.OutletId = o.OutletId"
				+ " LEFT JOIN  vwOutletTypeShortForm as vw ON vw.ShortCode = oto.ShortCode"

				+ " WHERE sm.ReferenceMonth = :refMonth";
		
				if (purpose != null && purpose.size() > 0){
					sql += " AND  pp.purposeId IN (:purpose) ";
				}
				
				if (assignmentIds != null && assignmentIds.size() > 0){
					sql += " AND a.AssignmentId IN (:assignmentIds) ";
				}
				
				if (quotation != null && quotation.size() > 0){
					sql += " AND q.QuotationId IN (:quotation) ";
				}
				
				if (unit != null && unit.size() > 0){
					sql += " AND q.UnitId IN (:unit) ";
				}
				
				if (quotationRecord != null && quotationRecord.size() > 0){
					sql += " AND qr.QuotationRecordId IN (:quotationRecord) ";
				}


				//sql += " GROUP BY a.ReferenceNo, o.FirmCode, o.OutletId, o.Name, d.Code, ot.ChineseName, ot.EnglishName, d.EnglishName, a.AssignmentId ";
				sql += " GROUP BY a.ReferenceNo, o.FirmCode, o.OutletId, o.Name, d.Code, vw.ChineseName, vw.EnglishName, oto.shortcode, d.EnglishName, a.AssignmentId ";
				sql += " , tpu.CouncilDistrict, tpu.Code, o.BRCode, o.StreetAddress, o.DetailAddress, o.LastContact, o.MainContact, o.Tel,o.Fax, q.UnitId";
				sql += " , o.OpeningStartTime, o.OpeningEndTime2, o.OpeningEndTime, o.ConvenientEndTime2, o.ConvenientStartTime,o.ConvenientStartTime2 ";
				sql += " , o.ConvenientEndTime, o.ConvenientEndTime2, o.WebSite, o.MarketName, o.Remark, o.DiscountRemark, o.CollectionMethod, o.IndoorMarketName ";
				sql += " , o.Latitude, o.Longitude, o.Status, o.CreatedDate, o.CreatedBy , o.OutletMarketType, ot.Code, o.OpeningStartTime2, o.IsUseFRAdmin ";
				sql	+= " ORDER BY o.FirmCode";
				
				SQLQuery query = this.getSession().createSQLQuery(sql);

				query.setParameter("refMonth", refMonth);

				if (purpose != null && purpose.size() > 0){
					query.setParameterList("purpose", purpose);
				}

				if (assignmentIds != null && assignmentIds.size() > 0){
					query.setParameterList("assignmentIds", assignmentIds);
				}
				
				if (unit != null && unit.size() > 0){
					query.setParameterList("unit", unit);
				}
				
				if (quotation != null && quotation.size() > 0){
					query.setParameterList("quotation", quotation);
				}
				
				if (quotationRecord != null && quotationRecord.size() > 0){
					query.setParameterList("quotationRecord", quotationRecord);
				}
				
				query.addScalar("referNo", StandardBasicTypes.STRING);
				query.addScalar("outletId", StandardBasicTypes.STRING);
				query.addScalar("outletCode", StandardBasicTypes.STRING);
				query.addScalar("outletName", StandardBasicTypes.STRING);
				query.addScalar("outletType", StandardBasicTypes.STRING);
				query.addScalar("outletTypeChName", StandardBasicTypes.STRING);
				query.addScalar("outletTypeEngName", StandardBasicTypes.STRING);
				query.addScalar("districtCode", StandardBasicTypes.STRING);
				query.addScalar("districtEngName", StandardBasicTypes.STRING);
				query.addScalar("districtCouncil", StandardBasicTypes.STRING);
				query.addScalar("tpuCode", StandardBasicTypes.STRING);
				query.addScalar("brCode", StandardBasicTypes.STRING);
				query.addScalar("mapAddress", StandardBasicTypes.STRING);
				query.addScalar("detailAddress", StandardBasicTypes.STRING);
				query.addScalar("lastContact", StandardBasicTypes.STRING);
				query.addScalar("mainContact", StandardBasicTypes.STRING);
				query.addScalar("telNo", StandardBasicTypes.STRING);
				query.addScalar("faxNo", StandardBasicTypes.STRING);
				query.addScalar("openStartTime", StandardBasicTypes.STRING);
				query.addScalar("openEndTime", StandardBasicTypes.STRING);
				query.addScalar("openStartTime2", StandardBasicTypes.STRING);
				query.addScalar("openEndTime2", StandardBasicTypes.STRING);
				query.addScalar("converientStartTime", StandardBasicTypes.STRING);
				query.addScalar("convenientEndTime", StandardBasicTypes.STRING);
				query.addScalar("converientStartTime2", StandardBasicTypes.STRING);
				query.addScalar("convenientEndTime2", StandardBasicTypes.STRING);
				
				query.addScalar("website", StandardBasicTypes.STRING);
				query.addScalar("yNonmarket", StandardBasicTypes.INTEGER);
				query.addScalar("outletRemarks", StandardBasicTypes.STRING);
				query.addScalar("outletDiscountRemarks", StandardBasicTypes.STRING);
				
				query.addScalar("collectionMethod", StandardBasicTypes.STRING);
				
				query.addScalar("defaultMajorLocation", StandardBasicTypes.STRING);
				query.addScalar("indoorMarketName", StandardBasicTypes.STRING);
				
				query.addScalar("latitude", StandardBasicTypes.STRING);
				query.addScalar("longitude", StandardBasicTypes.STRING);
				
				query.addScalar("isUseFRAdmin", StandardBasicTypes.BOOLEAN);
				query.addScalar("status", StandardBasicTypes.STRING);
				query.addScalar("createdDate", StandardBasicTypes.STRING);
				query.addScalar("createdBy", StandardBasicTypes.STRING);
				
				query.setResultTransformer(Transformers.aliasToBean(IndividualQuotationRecordReport.class));

				return query.list();
	}
	
	public List<IndividualQuotationRecordReport1> getIndividualQuoationRecordReport1(List<Integer> purpose, List<Integer> assignmentIds,
			List<Integer> quotation, List<Integer> unit, List<Integer> quotationRecord, Date refMonth) {		
		String referenceMonth = String.format("FORMAT(sm.ReferenceMonth, '%s', 'en-us')", SystemConstant.MONTH_FORMAT);
		String collectionDate = String.format("FORMAT(qr.CollectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String referenceDate = String.format("FORMAT(qr.ReferenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String ruaDate = String.format("FORMAT(q.RUADate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		
		String sql = "SELECT quotationRecordId, referenceMonth, collectionDate, referenceDate, isBackNo, isBackTrack, originalQuotationRecordId, isProductChange, isNewProduct, isNewRecruitment"
				+ ", isNewOutlet, quotationId, assignmentId, cpiPeriod, seasonality, outletCode, outletName, outletTypeCode, outletTpyeEngName, districtCode, tpuCode, tourRecordId, firmStatus"
				+ ", availability, nPrice, sPrice, isSPricePeculiar, uomValue, uomEngNameCName, subPriceRecordId, reason, discount, discountRemark, remark, isCollectFR, fr, isFRPercentage, isConsignmentCounter"
				+ ", consignmentCounterName, consignmentCounterRemark, outletDiscountRemark, categoryRemark, contactPerson, verificationRemark, peCheckRemark, staffCode, quotationReocrdStatus, rejectReason"
				+ ", enumerationStatus, quotationStatus, quotationState, ruaDate, bCode, uCode, ppCode, cpiBasePeriod, uEngName, varietyChineseName, ordinaryTourForm, cpiQoutationType, icp, icpProductCodeQ"
				+ ", icpProductNameQ, icpTypeU, icpTypeQ, productId, productGroupCode, productGroupEngName, productGroupCName, countryOfOrigin, pa1Name, pa2Name, pa3Name, pa4Name, pa5Name, productRemarks"
				+ ", productBarcode, productPosition, verifyFirm, verifyCategory, verifyQuotation, verificationRemarks, verificationReply, isVerficationRevisit, peCheckRemarks, capiFormType FROM ("
				+ " SELECT DISTINCT "
				+ " qr.QuotationRecordId AS [quotationRecordId] "
				/*+ ", sm.ReferenceMonth AS [referenceMonth]"
				+ ", qr.CollectionDate AS [collectionDate]"
				+ ", qr.ReferenceDate AS [referenceDate]"*/
				+ " , CASE WHEN sm.referenceMonth IS NOT NULL THEN " + referenceMonth + " ELSE '' END AS [referenceMonth] "
				+ " , CASE WHEN qr.CollectionDate IS NOT NULL THEN " + collectionDate + " ELSE '' END AS [collectionDate] "
				+ " , CASE WHEN qr.ReferenceDate IS NOT NULL THEN " + referenceDate + " ELSE '' END AS [referenceDate] "
				
				+ ", qr.IsBackNo AS [isBackNo] "
				+ ", qr.IsBackTrack AS [isBackTrack] "
				+ ", qr.OriginalQuotationRecordId AS [originalQuotationRecordId] "
				+ ", qr.IsProductChange AS [isProductChange] "
				+ ", qr.IsNewProduct AS [isNewProduct] "
				+ ", qr.IsNewRecruitment AS [isNewRecruitment] "
				+ ", qr.IsNewOutlet AS [isNewOutlet] "
				+ ", q.QuotationId AS [quotationId] "
				+ ", a.AssignmentId AS [assignmentId] "
				+ ", u.CPIBasePeriod AS [cpiPeriod] "
				+ ", u.Seasonality AS [seasonality] "
				+ ", o.FirmCode AS [outletCode] "
				+ ", o.Name AS [outletName] "
				//+ ", ot.Code AS [outletTypeCode] "
				+ " , SUBSTRING(ot.code, len(ot.code)-2, 3) AS [outletTypeCode] "
				+ ", ot.EnglishName AS [outletTpyeEngName] "
				+ ", d.Code AS [districtCode] "
				+ ", t.Code AS [tpuCode] "
				+ ", tr.TourRecordId AS [tourRecordId] "
				+ ", qr.FirmStatus AS [firmStatus] "
				+ ", qr.Availability AS [availability] "
				+ ", qr.NPrice AS [nPrice] "
				+ ", qr.SPrice AS [sPrice] "
				+ ", qr.IsSPricePeculiar AS [isSPricePeculiar] "
				+ ", qr.UomValue AS [uomValue] "
				+ ", concat(uom.ChineseName, ' - ', uom.EnglishName) AS [uomEngNameCName] "
				//+ ", sr.SubPriceRecordId AS [subPriceRecordId]"
				+ ", CASE WHEN COUNT( distinct sr.SubPriceRecordId ) > 0 THEN 1 ELSE 0 END AS [subPriceRecordId]"
				+ ", qr.Reason AS [reason] "
				+ ", qr.Discount AS [discount] "
				+ ", qr.DiscountRemark AS [discountRemark] "
				+ ", qr.Remark AS [remark] "
				+ ", qr.IsCollectFR AS [isCollectFR] "
				+ ", qr.FR AS [fr] "
				+ ", qr.IsFRPercentage AS [isFRPercentage] "
				+ ", qr.IsConsignmentCounter As [isConsignmentCounter] "
				+ ", qr.ConsignmentCounterName AS [consignmentCounterName] "
				+ ", qr.ConsignmentCounterRemark AS [consignmentCounterRemark] "
				+ ", qr.OutletDiscountRemark AS [outletDiscountRemark] "
				+ ", qr.CategoryRemark AS [categoryRemark] "
				+ ", qr.ContactPerson AS [contactPerson] "
				+ ", qr.VerificationRemark AS [verificationRemark] "
				+ ", qr.PECheckRemark AS [peCheckRemark] "
				+ ", ur.StaffCode AS [staffCode] "
				+ ", qr.Status AS [quotationReocrdStatus] "
				+ ", qr.RejectReason AS [rejectReason] "
				+ ", qr.FirmStatus AS [enumerationStatus] "
				+ ", q.Status AS [quotationStatus] "
				+ ", qr.QuotationState AS [quotationState] "
//				+ ", q.RUADate AS [ruaDate]"
				+ ", CASE WHEN q.RUADate IS NULL THEN '' ELSE "+ruaDate+" END AS [ruaDate] "
				+ ", b.Code AS [bCode] "
				+ ", u.Code AS [uCode] "
				+ ", pp.Code AS [ppCode] "
				+ ", u.CpiBasePeriod AS [cpiBasePeriod] "
				+ ", u.EnglishName AS [uEngName] "
				+ ", u.ChineseName AS [varietyChineseName] "
				+ ", qr.FormDisplay AS [ordinaryTourForm] "
				+ ", u.CpiQoutationType AS [cpiQoutationType] "
				+ ", q.IsICP AS [icp] "
				+ ", q.ICPProductCode AS [icpProductCodeQ] "
				+ ", q.ICPProductName AS [icpProductNameQ] "
				+ ", u.ICPType AS [icpTypeU] "
				+ ", q.ICPType AS [icpTypeQ] "
				+ ", p.ProductId AS [productId] "
				+ ", pg.Code AS [productGroupCode] "
				+ ", pg.EnglishName AS [productGroupEngName] "
				+ ", pg.ChineseName AS [productGroupCName] "
				+ ", p.CountryOfOrigin AS [countryOfOrigin] "
				/*+ ", case when qr.quotationRecordId is not null then pa1.specificationName else pa1.specificationName end as pa1Name "
				+ ", case when qr.quotationRecordId is not null then pa2.specificationName else pa2.specificationName end as pa2Name "
				+ ", case when qr.quotationRecordId is not null then pa3.specificationName else pa3.specificationName end as pa3Name "
				+ ", case when qr.quotationRecordId is not null then pa4.specificationName else pa4.specificationName end as pa4Name "
				+ ", case when qr.quotationRecordId is not null then pa5.specificationName else pa5.specificationName end as pa5Name "*/
				+ ", concat(pa1.SpecificationName , ':' , ps1.Value) as [pa1Name] "
				+ ", concat(pa2.SpecificationName , ':' , ps2.Value) as [pa2Name] "
				+ ", concat(pa3.SpecificationName , ':' , ps3.Value) as [pa3Name] "
				+ ", concat(pa4.SpecificationName , ':' , ps4.Value) as [pa4Name] "
				+ ", concat(pa5.SpecificationName , ':' , ps5.Value) as [pa5Name] "
				+ ", qr.ProductRemark AS [productRemarks] "
				+ ", p.BarCode AS [productBarcode] "
				+ ", qr.ProductPosition AS [productPosition]" 
				+ ", qr.VerifyFirm AS [verifyFirm] "
				+ ", qr.VerifyCategory AS [verifyCategory] "
				+ ", qr.VerifyQuotation AS [verifyQuotation] "
				+ ", qr.VerificationRemark AS [verificationRemarks] "
				+ ", qr.VerificationReply AS [verificationReply] "
				+ ", qr.IsVisited AS [isVerficationRevisit] "
				+ ", qr.PECheckRemark AS [peCheckRemarks] "
				+ ", q.FormType AS [capiFormType] "
				/*+ "FROM QuotatiONRecord AS qr "
				+ "LEFT JOIN Quotation AS q ON qr.quotationId = q.quotationId "
				+ "LEFT JOIN Outlet AS o ON qr.outletId = o.outletId "
				+ "LEFT JOIN ASsignment AS a ON qr.ASsignmentId = a.ASsignmentId "
				+ "LEFT JOIN Product AS p ON p.productId = qr.productId "
				+ "LEFT JOIN ProductGroup AS pg ON pg.productGroupId = p.productGroupId "
				+ "LEFT JOIN Batch AS b ON q.batchId = b.batchId "
				+ "LEFT JOIN Unit AS u ON q.unitId = u.unitId "
				+ "LEFT JOIN Purpose AS pp ON u.purposeId = pp.purposeId "
				+ "LEFT JOIN SurveyMonth AS sm ON a.SurveyMonthId = sm.SurveyMonthId "
				+ "LEFT JOIN [User] AS ur ON ur.userId = qr.userId "
				+ "LEFT JOIN SubItem AS si ON si.subItemId = u.subItemId "
				+ "LEFT JOIN OutletType AS ot ON si.outletTypeId = ot.outletTypeId "
				+ "LEFT JOIN Tpu AS t ON o.tpuId = t.tpuId "
				+ "LEFT JOIN District AS d ON d.districtId = t.districtId "
				+ "LEFT JOIN ProductAttribute AS pa1 ON pa1.ProductGroupId = pg.ProductGroupId AND pa1.Sequence = 1 "
				+ "LEFT JOIN ProductAttribute AS pa2 ON pa2.ProductGroupId = pg.ProductGroupId AND pa2.Sequence = 2 "
				+ "LEFT JOIN ProductAttribute AS pa3 ON pa3.ProductGroupId = pg.ProductGroupId AND pa3.Sequence = 3 "
				+ "LEFT JOIN ProductAttribute AS pa4 ON pa4.ProductGroupId = pg.ProductGroupId AND pa4.Sequence = 4 "
				+ "LEFT JOIN ProductAttribute AS pa5 ON pa5.ProductGroupId = pg.ProductGroupId AND pa5.Sequence = 5 "
				+ "left join ProductAttribute as pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 1 "
				+ "left join ProductSpecification as ps1 on ps1.ProductAttributeId = pa1.ProductAttributeId and ps1.ProductId = p.ProductId "
				+ "left join ProductAttribute as pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.Sequence = 2 "
				+ "left join ProductSpecification as ps2 on ps2.ProductAttributeId = pa2.ProductAttributeId and ps2.ProductId = p.ProductId "
				+ "left join ProductAttribute as pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.Sequence = 3 "
				+ "left join ProductSpecification as ps3 on ps3.ProductAttributeId = pa3.ProductAttributeId and ps3.ProductId = p.ProductId "
				+ "left join ProductAttribute as pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.Sequence = 4 "
				+ "left join ProductSpecification as ps4 on ps4.ProductAttributeId = pa4.ProductAttributeId and ps4.ProductId = p.ProductId "
				+ "left join ProductAttribute as pa5 on pa5.ProductGroupId = pg.ProductGroupId and pa5.Sequence = 5 "
				+ "left join ProductSpecification as ps5 on ps5.ProductAttributeId = pa5.ProductAttributeId and ps5.ProductId = p.ProductId "
				+ "LEFT JOIN TourRecord AS tr ON tr.TourRecordId = qr.QuotatiONRecordId "
				+ "LEFT JOIN SubPriceRecord AS sr ON sr.QuotatiONRecordId = qr.QuotatiONRecordId "	
				+ " JOIN Uom AS uom ON uom.uomId = qr.uomId "*/
				
				+ " FROM [QuotationRecord] AS qr "
				+ " LEFT JOIN [Quotation] AS q ON qr.quotationId = q.quotationId "
				+ " LEFT JOIN [Outlet] AS o ON qr.outletId = o.outletId "
				+ " LEFT JOIN [Assignment] AS a ON qr.assignmentId = a.assignmentId "
				+ " LEFT JOIN [Product] AS p ON p.productId = qr.productId "
				+ " LEFT JOIN [ProductGroup] AS pg ON pg.productGroupId = p.productGroupId "
				+ " LEFT JOIN [Batch] AS b ON q.batchId = b.batchId "
				+ " LEFT JOIN [Unit] AS u ON q.unitId = u.unitId "
				+ " LEFT JOIN [Purpose] AS pp ON u.purposeId = pp.purposeId "
				+ " LEFT JOIN [SurveyMonth] AS sm ON a.surveyMonthId = sm.surveyMonthId "
				+ " LEFT JOIN [User] AS ur ON ur.userId = qr.userId "
				+ " LEFT JOIN [Uom] AS um ON um.uomId = qr.uomId "
				+ " LEFT JOIN [SubItem] AS si ON si.subItemId = u.subItemId "
				+ " LEFT JOIN [OutletType] AS ot ON si.outletTypeId = ot.outletTypeId "
				+ " LEFT JOIN [Tpu] AS t ON o.tpuId = t.tpuId "
				+ " LEFT JOIN [District] AS d ON d.districtId = t.districtId "
				+ " LEFT JOIN [ProductAttribute] AS pa1 ON pa1.ProductGroupId = pg.ProductGroupId AND pa1.[Sequence] = 1 "
				+ " LEFT JOIN [ProductAttribute] AS pa2 ON pa2.ProductGroupId = pg.ProductGroupId AND pa2.[Sequence] = 2 "
				+ " LEFT JOIN [ProductAttribute] AS pa3 ON pa3.ProductGroupId = pg.ProductGroupId AND pa3.[Sequence] = 3 "
				+ " LEFT JOIN [ProductAttribute] AS pa4 ON pa4.ProductGroupId = pg.ProductGroupId AND pa4.[Sequence] = 4 "
				+ " LEFT JOIN [ProductAttribute] AS pa5 ON pa5.ProductGroupId = pg.ProductGroupId AND pa5.[Sequence] = 5 "
				+ " LEFT JOIN [ProductSpecification] AS ps1 ON ps1.ProductId = p.ProductId AND pa1.ProductAttributeId = ps1.ProductAttributeId "
				+ " LEFT JOIN [ProductSpecification] AS ps2 ON ps2.ProductId = p.ProductId AND pa2.ProductAttributeId = ps2.ProductAttributeId "
				+ " LEFT JOIN [ProductSpecification] AS ps3 ON ps3.ProductId = p.ProductId AND pa3.ProductAttributeId = ps3.ProductAttributeId "
				+ " LEFT JOIN [ProductSpecification] AS ps4 ON ps4.ProductId = p.ProductId AND pa4.ProductAttributeId = ps4.ProductAttributeId "
				+ " LEFT JOIN [ProductSpecification] AS ps5 ON ps5.ProductId = p.ProductId AND pa5.ProductAttributeId = ps5.ProductAttributeId "
				+ " LEFT JOIN SubPriceRecord AS sr ON sr.QuotatiONRecordId = qr.QuotatiONRecordId "
				+ " LEFT JOIN TourRecord AS tr ON tr.TourRecordId = qr.QuotatiONRecordId "
				+ " LEFT JOIN Uom AS uom ON uom.uomId = qr.uomId "
				+ " WHERE sm.ReferenceMonth = :refMonth ";
		
				
				if (purpose != null && purpose.size() > 0){
					sql += " AND  pp.purposeId IN (:purpose) ";
				}
				
				if (assignmentIds != null && assignmentIds.size() > 0){
					sql += " AND a.AssignmentId IN (:assignmentIds) ";
				}
				
				if (quotation != null && quotation.size() > 0){
					sql += " AND q.QuotationId IN (:quotation) ";
				}
				
				if (unit != null && unit.size() > 0){
					sql += " AND q.UnitId IN (:unit) ";
				}

				if (quotationRecord != null && quotationRecord.size() > 0){
					sql += " AND qr.QuotationRecordId IN (:quotationRecord) ";
				}

				sql += " GROUP BY qr.QuotationId, sm.ReferenceMonth, qr.CollectionDate, qr.ReferenceDate, qr.IsBackNo , qr.IsBackTrack"
						+ ", qr.OriginalQuotationRecordId, qr.IsProductChange, qr.IsNewProduct, qr.IsNewRecruitment, qr.IsNewOutlet"
						+ ", q.QuotationId, a.AssignmentId , u.CPIBasePeriod , u.Seasonality, o.FirmCode, o.Name, ot.Code"
						+ ", ot.EnglishName, d.Code, t.Code, qr.FirmStatus, qr.Availability, qr.NPrice, qr.SPrice, qr.IsSPricePeculiar"
						+ ", qr.UomValue, qr.Reason, qr.Discount, qr.DiscountRemark, qr.Remark, qr.IsCollectFR, qr.FR, qr.IsFRPercentage"
						+ ", qr.IsConsignmentCounter, qr.ConsignmentCounterName, qr.ConsignmentCounterRemark, qr.OutletDiscountRemark"
						+ ", qr.CategoryRemark, qr.ContactPerson, qr.VerificationRemark, qr.PECheckRemark, ur.StaffCode, qr.Status"
						+ ", qr.RejectReason, qr.FirmStatus, q.Status, qr.QuotationState, q.RUADate, b.Code, u.Code, pp.Code, u.CpiBasePeriod"
						+ ", u.EnglishName, u.ChineseName, qr.FormDisplay, u.CpiQoutationType, q.IsICP, q.ICPProductCode, q.ICPProductName"
						+ ", u.ICPType, q.ICPType, p.ProductId, pg.Code, pg.EnglishName, pg.ChineseName, p.CountryOfOrigin, qr.ProductRemark"
						+ ", p.BarCode, qr.ProductPosition, qr.VerifyFirm, qr.VerifyCategory, qr.VerifyQuotation, qr.VerificationRemark"
						+ ", qr.VerificationReply, qr.IsVisited, qr.PECheckRemark, tr.TourRecordId, uom.ChineseName, uom.EnglishName"
						+ ", sr.SubPriceRecordId, qr.quotationRecordId, q.FormType, pa1.specificationName, pa2.specificationName, pa3.specificationName"
						+ ", pa4.specificationName, pa5.specificationName, pp.PurposeId "
						+ ", ps1.Value, ps2.Value, ps3.Value, ps4.Value, ps5.Value ";
				sql+= " ) A";
				sql += " ORDER BY A.ReferenceMonth, A.QuotationId";
				
				SQLQuery query = this.getSession().createSQLQuery(sql);

				query.setParameter("refMonth", refMonth);
				
				if (purpose != null && purpose.size() > 0){
					query.setParameterList("purpose", purpose);
				}

				if (assignmentIds != null && assignmentIds.size() > 0){
					query.setParameterList("assignmentIds", assignmentIds);
				}
				
				if (unit != null && unit.size() > 0){
					query.setParameterList("unit", unit);
				}
				
				if (quotation != null && quotation.size() > 0){
					query.setParameterList("quotation", quotation);
				}
				
				if (quotationRecord != null && quotationRecord.size() > 0){
					query.setParameterList("quotationRecord", quotationRecord);
				}
				
				query.addScalar("quotationRecordId", StandardBasicTypes.STRING);
				query.addScalar("referenceMonth", StandardBasicTypes.STRING);
				query.addScalar("collectionDate", StandardBasicTypes.STRING);
				query.addScalar("referenceDate", StandardBasicTypes.STRING);
				query.addScalar("isBackNo", StandardBasicTypes.BOOLEAN);
				query.addScalar("isBackTrack", StandardBasicTypes.BOOLEAN);
				//query.addScalar("originalQuotationRecordId", StandardBasicTypes.INTEGER);
				query.addScalar("originalQuotationRecordId", StandardBasicTypes.STRING);
				query.addScalar("isProductChange", StandardBasicTypes.STRING);
				query.addScalar("isNewProduct", StandardBasicTypes.BOOLEAN);
				query.addScalar("isNewRecruitment", StandardBasicTypes.BOOLEAN);
				query.addScalar("isNewOutlet", StandardBasicTypes.BOOLEAN);
				query.addScalar("quotationId", StandardBasicTypes.STRING);
				query.addScalar("assignmentId", StandardBasicTypes.STRING);
				query.addScalar("cpiPeriod", StandardBasicTypes.STRING);
				query.addScalar("seasonality", StandardBasicTypes.INTEGER);
				query.addScalar("outletCode", StandardBasicTypes.STRING);
				query.addScalar("outletName", StandardBasicTypes.STRING);
				query.addScalar("outletTypeCode", StandardBasicTypes.STRING);
				query.addScalar("outletTpyeEngName", StandardBasicTypes.STRING);
				query.addScalar("districtCode", StandardBasicTypes.STRING);
				query.addScalar("tpuCode", StandardBasicTypes.STRING);
				query.addScalar("tourRecordId", StandardBasicTypes.INTEGER);
				query.addScalar("firmStatus", StandardBasicTypes.INTEGER);
				query.addScalar("availability", StandardBasicTypes.INTEGER);
				query.addScalar("nPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("sPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("isSPricePeculiar", StandardBasicTypes.BOOLEAN);
				query.addScalar("uomValue", StandardBasicTypes.DOUBLE);
				query.addScalar("uomEngNameCName", StandardBasicTypes.STRING);
				query.addScalar("subPriceRecordId", StandardBasicTypes.INTEGER);
				query.addScalar("reason", StandardBasicTypes.STRING);
				query.addScalar("discount", StandardBasicTypes.STRING);
				query.addScalar("discountRemark", StandardBasicTypes.STRING);
				query.addScalar("remark", StandardBasicTypes.STRING);
				query.addScalar("isCollectFR", StandardBasicTypes.BOOLEAN);
				query.addScalar("fr", StandardBasicTypes.DOUBLE);
				query.addScalar("isFRPercentage", StandardBasicTypes.BOOLEAN);
				query.addScalar("isConsignmentCounter", StandardBasicTypes.BOOLEAN);
				query.addScalar("consignmentCounterName", StandardBasicTypes.STRING);
				query.addScalar("consignmentCounterRemark", StandardBasicTypes.STRING);
				query.addScalar("outletDiscountRemark", StandardBasicTypes.STRING);
				query.addScalar("categoryRemark", StandardBasicTypes.STRING);
				query.addScalar("contactPerson", StandardBasicTypes.STRING);
				query.addScalar("verificationRemark", StandardBasicTypes.STRING);
				query.addScalar("peCheckRemark", StandardBasicTypes.STRING);
				query.addScalar("staffCode", StandardBasicTypes.STRING);
				query.addScalar("quotationReocrdStatus", StandardBasicTypes.STRING);
				query.addScalar("rejectReason", StandardBasicTypes.STRING);
				query.addScalar("enumerationStatus", StandardBasicTypes.INTEGER);
				query.addScalar("quotationStatus", StandardBasicTypes.STRING);
				query.addScalar("quotationState", StandardBasicTypes.STRING);
				query.addScalar("ruaDate", StandardBasicTypes.STRING);
				query.addScalar("bCode", StandardBasicTypes.STRING);
				query.addScalar("uCode", StandardBasicTypes.STRING);
				query.addScalar("ppCode", StandardBasicTypes.STRING);
				query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
				query.addScalar("uEngName", StandardBasicTypes.STRING);
				query.addScalar("varietyChineseName", StandardBasicTypes.STRING);
				query.addScalar("ordinaryTourForm", StandardBasicTypes.INTEGER);
				query.addScalar("cpiQoutationType", StandardBasicTypes.STRING);
				query.addScalar("icp", StandardBasicTypes.BOOLEAN);
				query.addScalar("icpProductCodeQ", StandardBasicTypes.STRING);
				query.addScalar("icpProductNameQ", StandardBasicTypes.STRING);
				query.addScalar("icpTypeU", StandardBasicTypes.STRING);
				query.addScalar("icpTypeQ", StandardBasicTypes.STRING);
				query.addScalar("productId", StandardBasicTypes.STRING);
				query.addScalar("productGroupCode", StandardBasicTypes.STRING);
				query.addScalar("productGroupEngName", StandardBasicTypes.STRING);
				query.addScalar("productGroupCName", StandardBasicTypes.STRING);
				query.addScalar("countryOfOrigin", StandardBasicTypes.STRING);
				query.addScalar("pa1Name", StandardBasicTypes.STRING);
				query.addScalar("pa2Name", StandardBasicTypes.STRING);
				query.addScalar("pa3Name", StandardBasicTypes.STRING);
				query.addScalar("pa4Name", StandardBasicTypes.STRING);
				query.addScalar("pa5Name", StandardBasicTypes.STRING);
				query.addScalar("productRemarks", StandardBasicTypes.STRING);
				query.addScalar("productBarcode", StandardBasicTypes.STRING);
				query.addScalar("productPosition", StandardBasicTypes.STRING);
				query.addScalar("verifyFirm", StandardBasicTypes.BOOLEAN);
				query.addScalar("verifyCategory", StandardBasicTypes.BOOLEAN);
				query.addScalar("verifyQuotation", StandardBasicTypes.BOOLEAN);
				query.addScalar("verificationRemarks", StandardBasicTypes.STRING);
				query.addScalar("verificationReply", StandardBasicTypes.STRING);
				query.addScalar("isVerficationRevisit", StandardBasicTypes.BOOLEAN);
				query.addScalar("peCheckRemarks", StandardBasicTypes.STRING);
				query.addScalar("capiFormType", StandardBasicTypes.STRING);

				query.setResultTransformer(Transformers.aliasToBean(IndividualQuotationRecordReport1.class));
				System.out.println("QuotationRecord sql ::"+sql);
				return query.list();
		
	}
	
	public List<IndividualQuotationRecordReport2> getIndividualQuoationRecordReport2(List<Integer> purpose, List<Integer> assignmentIds,
			List<Integer> quotation, List<Integer> unit, List<Integer> quotationRecord, Date refMonth) {
		
		String sql = "SELECT"
				+ " qr.QuotationRecordId AS [quotationRecordId] "
				+ " , p.ProductId AS [productID] "
				+ " , qr.IsProductChange AS [isProductChange] "
				+ " , qr.IsNewProduct AS [isNewProduct] "
				+ " , concat(pg.Code, ' - ', pg.ChineseName) AS [productType] "
				+ " , p.CountryOfOrigin AS [countryofOrigin] "
				+ " , pa.SpecificationName AS [specificationName] "
				+ " , ps.Value AS [pValue] "
				+ " , pa1.SpecificationName AS [specificationName1] "
				+ " , ps1.Value AS [pValue1] "
				+ " , pa2.SpecificationName AS [specificationName2] "
				+ " , ps2.Value AS [pValue2] "
				+ " , pa3.SpecificationName AS [specificationName3] "
				+ " , ps3.Value AS [pValue3] "
				+ " , pa4.SpecificationName AS [specificationName4] "
				+ " , ps4.Value AS [pValue4] "
				+ " , pa5.SpecificationName AS [specificationName5] "
				+ " , ps5.Value AS [pValue5] "
				+ " , pa6.SpecificationName AS [specificationName6] "
				+ " , ps6.Value AS [pValue6] "
				+ " , p.BarCode AS [productBarcode] "
				+ " , qr.ProductRemark AS [productRemarks] "
				+ " , qr.ProductPosition AS [productPosition] "
				+ " FROM [QuotationRecord] AS qr "
				+ " LEFT JOIN [Assignment] AS a ON qr.assignmentId = a.assignmentId "
				+ " LEFT JOIN [SurveyMonth] AS sm ON a.surveyMonthId = sm.surveyMonthId "
				+ " LEFT JOIN [Product] AS p ON p.productId = qr.productId "
				+ " LEFT JOIN [Quotation] as q on q.QuotationId = qr.QuotationId "
				+ " LEFT JOIN [Unit] as u on u.UnitId = q.UnitId "
				+ " LEFT JOIN [Purpose] as pp on pp.PurposeId = u.PurposeId "
				+ " LEFT JOIN [ProductGroup] AS pg ON pg.productGroupId = p.productGroupId "
				+ " LEFT JOIN [ProductAttribute] AS pa ON pa.ProductGroupId = pg.ProductGroupId AND pa.[Sequence] = 1 "
				+ " LEFT JOIN [ProductSpecification] AS ps ON ps.ProductId = p.ProductId AND pa.ProductAttributeId = ps.ProductAttributeId "
				+ " LEFT JOIN [ProductAttribute] AS pa1 ON pa1.ProductGroupId = pg.ProductGroupId AND pa1.[Sequence] = 2"
				+ " LEFT JOIN [ProductSpecification] AS ps1 ON ps1.ProductId = p.ProductId AND pa1.ProductAttributeId = ps1.ProductAttributeId "
				+ " LEFT JOIN [ProductAttribute] AS pa2 ON pa2.ProductGroupId = pg.ProductGroupId AND pa2.[Sequence] = 3"
				+ " LEFT JOIN [ProductSpecification] AS ps2 ON ps2.ProductId = p.ProductId AND pa2.ProductAttributeId = ps2.ProductAttributeId "
				+ " LEFT JOIN [ProductAttribute] AS pa3 ON pa3.ProductGroupId = pg.ProductGroupId AND pa3.[Sequence] = 4 "
				+ " LEFT JOIN [ProductSpecification] AS ps3 ON ps3.ProductId = p.ProductId AND pa3.ProductAttributeId = ps3.ProductAttributeId "
				+ " LEFT JOIN [ProductAttribute] AS pa4 ON pa4.ProductGroupId = pg.ProductGroupId AND pa4.[Sequence] = 5 "
				+ " LEFT JOIN [ProductSpecification] AS ps4 ON ps4.ProductId = p.ProductId AND pa4.ProductAttributeId = ps4.ProductAttributeId "
				+ " LEFT JOIN [ProductAttribute] AS pa5 ON pa5.ProductGroupId = pg.ProductGroupId AND pa5.[Sequence] = 6 "
				+ " LEFT JOIN [ProductSpecification] AS ps5 ON ps5.ProductId = p.ProductId AND pa5.ProductAttributeId = ps5.ProductAttributeId "
				+ " LEFT JOIN [ProductAttribute] AS pa6 ON pa6.ProductGroupId = pg.ProductGroupId AND pa6.[Sequence] = 7 "
				+ " LEFT JOIN [ProductSpecification] AS ps6 ON ps6.ProductId = p.ProductId AND pa6.ProductAttributeId = ps6.ProductAttributeId "
				+ " WHERE sm.ReferenceMonth = :refMonth ";
		
				if (purpose != null && purpose.size() > 0){
					sql += " AND  pp.purposeId IN (:purpose) ";
				}
				
				if (assignmentIds != null && assignmentIds.size() > 0){
					sql += " AND a.AssignmentId IN (:assignmentIds) ";
				}
				
				if (quotation != null && quotation.size() > 0){
					sql += " AND q.QuotationId IN (:quotation) ";
				}
				
				if (unit != null && unit.size() > 0){
					sql += " AND q.UnitId IN (:unit) ";
				}
				
				if (quotationRecord != null && quotationRecord.size() > 0){
					sql += " AND qr.QuotationRecordId IN (:quotationRecord) ";
				}

				sql +=  " GROUP BY qr.QuotationRecordId, p.ProductId, qr.IsProductChange "
				+ " , qr.IsNewProduct, pg.Code, pg.ChineseName, p.CountryOfOrigin "
				+ " , p.BarCode , qr.ProductRemark, qr.ProductPosition "
				+ " , pa.SpecificationName , ps.Value "
				+ " , pa1.SpecificationName , ps1.Value "
				+ " , pa2.SpecificationName , ps2.Value "
				+ " , pa3.SpecificationName , ps3.Value "
				+ " , pa4.SpecificationName , ps4.Value "
				+ " , pa5.SpecificationName , ps5.Value "
				+ " , pa6.SpecificationName , ps6.Value ";
				
				sql += " ORDER BY qr.QuotationRecordId";
				
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("refMonth", refMonth);
		
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}

		if (assignmentIds != null && assignmentIds.size() > 0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		if (unit != null && unit.size() > 0){
			query.setParameterList("unit", unit);
		}
		
		if (quotation != null && quotation.size() > 0){
			query.setParameterList("quotation", quotation);
		}
		
		if (quotationRecord != null && quotationRecord.size() > 0){
			query.setParameterList("quotationRecord", quotationRecord);
		}
		
		query.addScalar("quotationRecordId", StandardBasicTypes.STRING);
		//query.addScalar("productID", StandardBasicTypes.INTEGER);
		query.addScalar("productID", StandardBasicTypes.STRING);
		query.addScalar("isProductChange", StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewProduct", StandardBasicTypes.BOOLEAN);
		query.addScalar("productType", StandardBasicTypes.STRING);
		query.addScalar("countryofOrigin", StandardBasicTypes.STRING);
		query.addScalar("productBarcode", StandardBasicTypes.STRING);
		query.addScalar("productRemarks", StandardBasicTypes.STRING);
		query.addScalar("productPosition", StandardBasicTypes.STRING);

		query.addScalar("specificationName", StandardBasicTypes.STRING);
		query.addScalar("pValue", StandardBasicTypes.STRING);
		query.addScalar("specificationName1", StandardBasicTypes.STRING);
		query.addScalar("pValue1", StandardBasicTypes.STRING);
		query.addScalar("specificationName2", StandardBasicTypes.STRING);
		query.addScalar("pValue2", StandardBasicTypes.STRING);
		query.addScalar("specificationName3", StandardBasicTypes.STRING);
		query.addScalar("pValue3", StandardBasicTypes.STRING);
		query.addScalar("specificationName4", StandardBasicTypes.STRING);
		query.addScalar("pValue4", StandardBasicTypes.STRING);
		query.addScalar("specificationName5", StandardBasicTypes.STRING);
		query.addScalar("pValue5", StandardBasicTypes.STRING);
		query.addScalar("specificationName6", StandardBasicTypes.STRING);
		query.addScalar("pValue6", StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(IndividualQuotationRecordReport2.class));

		return query.list();
		
	}

	public List<IndividualQuotationRecordReport3> getIndividualQuotationReocrdReport3(List<Integer> purpose, List<Integer> assignmentIds,
			List<Integer> quotation, List<Integer> unit, List<Integer> quotationRecord, Date refMonth) {
		
		String sql = "SELECT "
				+ "qr.QuotationRecordId AS [quotationReocrdId] "
				+ " , sr.SubPriceRecordId AS [subPriceRecordId] "
				+ " , st.Name AS [name] "
				+ " , qr.Nprice AS [nPrice] "
				+ " , qr.Sprice AS [sPrice] "
				+ " , sr.Nprice AS [subNprice] "
				+ " , sr.Sprice AS [subSprice] "
				+ " , sr.Discount AS [discount] "
				+ " , sf.FieldName AS [fieldName] "
				+ " , sc.ColumnValue AS [columnValue] "
				+ " , sfm.Sequence AS [Sequence]"
				/*+ " , sfm.Sequence AS [sequence] "*/
				+ " FROM SubPriceRecord AS sr "
				+ " LEFT JOIN QuotationRecord AS qr ON qr.QuotationRecordId = sr.QuotationRecordId "
				+ " LEFT JOIN Assignment AS a ON qr.AssignmentId = a.AssignmentId "
				+ " LEFT JOIN [Quotation] as q on q.QuotationId = qr.QuotationId " 
				+ " LEFT JOIN [Unit] as u on u.UnitId = q.UnitId "
				+ " LEFT JOIN [Purpose] as pp on pp.PurposeId = u.PurposeId "
				+ " LEFT JOIN SurveyMonth AS sm ON sm.SurveyMonthId = a.SurveyMonthId "
				+ " LEFT JOIN SubPriceType AS st ON st.SubPriceTypeId = sr.SubPriceTypeId "
				+ " LEFT JOIN SubPriceFieldMapping AS sfm ON st.SubPriceTypeId = sfm.SubPriceTypeId "
				+ " LEFT JOIN SubPriceField AS sf ON sf.SubPriceFieldId = sfm.SubPriceFieldId "
				+ " LEFT JOIN SubPriceColumn AS sc ON sc.SubPriceFieldMappingId = sfm.SubPriceFieldMappingId AND sc.SubPriceRecordId = sr.SubPriceRecordId "
				+ " WHERE sm.ReferenceMonth = :refMonth ";
		
				if (purpose != null && purpose.size() > 0){
					sql += " AND  pp.purposeId IN (:purpose) ";
				}
				
				if (assignmentIds != null && assignmentIds.size() > 0){
					sql += " AND a.AssignmentId IN (:assignmentIds) ";
				}
				
				if (quotation != null && quotation.size() > 0){
					sql += " AND q.QuotationId IN (:quotation) ";
				}
				
				if (unit != null && unit.size() > 0){
					sql += " AND q.UnitId IN (:unit) ";
				}
				
				if (quotationRecord != null && quotationRecord.size() > 0){
					sql += " AND qr.QuotationRecordId IN (:quotationRecord) ";
				}

				sql += " GROUP BY qr.QuotationRecordId, sr.SubPriceRecordId, st.Name, qr.Nprice, qr.Sprice, sr.Nprice, sr.Sprice, sr.Discount, sf.FieldName, sc.ColumnValue, sfm.Sequence ";
				sql += " ORDER BY qr.QuotationRecordId, sr.SubPriceRecordId,  sfm.Sequence ";		
		
			SQLQuery query = this.getSession().createSQLQuery(sql);
			
			query.setParameter("refMonth", refMonth);
			
			if (purpose != null && purpose.size() > 0){
				query.setParameterList("purpose", purpose);
			}

			if (assignmentIds != null && assignmentIds.size() > 0){
				query.setParameterList("assignmentIds", assignmentIds);
			}
			
			if (unit != null && unit.size() > 0){
				query.setParameterList("unit", unit);
			}
			
			if (quotation != null && quotation.size() > 0){
				query.setParameterList("quotation", quotation);
			}

			if (quotationRecord != null && quotationRecord.size() > 0){
				query.setParameterList("quotationRecord", quotationRecord);
			}
			/*query.addScalar("quotationReocrdId", StandardBasicTypes.INTEGER);
			query.addScalar("subPriceRecordId", StandardBasicTypes.INTEGER);*/
			query.addScalar("quotationReocrdId", StandardBasicTypes.STRING);
			query.addScalar("subPriceRecordId", StandardBasicTypes.STRING);
			query.addScalar("name", StandardBasicTypes.STRING);
			query.addScalar("nPrice", StandardBasicTypes.DOUBLE);
			query.addScalar("sPrice", StandardBasicTypes.DOUBLE);
			query.addScalar("subNprice", StandardBasicTypes.DOUBLE);
			query.addScalar("subSprice", StandardBasicTypes.DOUBLE);
			query.addScalar("discount", StandardBasicTypes.STRING);
						
			query.addScalar("fieldName", StandardBasicTypes.STRING);
						
			query.addScalar("columnValue", StandardBasicTypes.STRING);
			
			query.addScalar("Sequence", StandardBasicTypes.INTEGER);
			
			/*query.addScalar("sequence", StandardBasicTypes.INTEGER);*/

			query.setResultTransformer(Transformers.aliasToBean(IndividualQuotationRecordReport3.class));

		return query.list();
	}
	
	public List<IndividualQuotationRecordReport4> getIndividualQuotationReocrdReport4(List<Integer> purpose, List<Integer> assignmentIds,
			List<Integer> quotation, List<Integer> unit, List<Integer> quotationRecord, Date refMonth) {
		
		String sql = "SELECT "
				+ " qr.QuotationRecordId AS [quotationRecordId] "
				+ " , tr.tourRecordId AS [tourRecordId] "
				+ " , qr.Nprice AS [nPrice] "
				+ " , qr.Sprice AS [sPrice] "
				+ " , tr.day1Price AS [day1Price], tr.day2Price AS [day2Price] , tr.day3Price AS [day3Price] "
				+ " , tr.day4Price AS [day4Price], tr.day5Price AS [day5Price], tr.day6Price AS [day6Price] "
				+ " , tr.day7Price AS [day7Price], tr.day8Price AS [day8Price], tr.day9Price AS [day9Price] "
				+ " , tr.day10Price AS [day10Price], tr.day11Price AS [day11Price], tr.day12Price AS [day12Price] "
				+ " , tr.day13Price AS [day13Price], tr.day14Price AS [day14Price], tr.day15Price AS [day15Price] "
				+ " , tr.day16Price AS [day16Price], tr.day17Price AS [day17Price], tr.day18Price AS [day18Price] "
				+ " , tr.day19Price AS [day19Price], tr.day20Price AS [day20Price], tr.day21Price AS [day21Price] "
				+ " , tr.day22Price AS [day22Price], tr.day23Price AS [day23Price], tr.day24Price AS [day24Price] "
				+ " , tr.day25Price AS [day25Price], tr.day26Price AS [day26Price], tr.day27Price AS [day27Price] "
				+ " , tr.day28Price AS [day28Price], tr.day29Price AS [day29Price], tr.day30Price AS [day30Price] "
				+ " , tr.day31Price AS [day31Price]"
				+ " , tr.extraPrice1Name AS [extraPrice1Name], tr.extraPrice1Value AS [extraPrice1Value], tr.isExtraPrice1Count AS [isExtraPrice1Count] "
				+ " , tr.extraPrice2Name AS [extraPrice2Name], tr.extraPrice2Value AS [extraPrice2Value], tr.isExtraPrice2Count AS [isExtraPrice2Count] "
				+ " , tr.extraPrice3Name AS [extraPrice3Name], tr.extraPrice3Value AS [extraPrice3Value], tr.isExtraPrice3Count AS [isExtraPrice3Count] "
				+ " , tr.extraPrice4Name AS [extraPrice4Name], tr.extraPrice4Value AS [extraPrice4Value], tr.isExtraPrice4Count AS [isExtraPrice4Count] "
				+ " , tr.extraPrice5Name AS [extraPrice5Name], tr.extraPrice5Value AS [extraPrice5Value], tr.isExtraPrice5Count AS [isExtraPrice5Count] "
				+ " FROM QuotationRecord AS qr "
				+ " LEFT JOIN Assignment AS a ON qr.AssignmentId = a.AssignmentId "
				+ " LEFT JOIN SurveyMonth AS sm ON a.SurveyMonthId = sm.SurveyMonthId "
				+ " LEFT JOIN TourRecord AS tr ON tr.TourRecordId = qr.QuotationRecordId "
				+ " LEFT JOIN [Quotation] as q on q.QuotationId = qr.QuotationId "
				+ " LEFT JOIN [Unit] as u on u.UnitId = q.UnitId "
				+ " LEFT JOIN [Purpose] as pp on pp.PurposeId = u.PurposeId "
				+ " WHERE sm.ReferenceMonth = :refMonth ";
				
				if (purpose != null && purpose.size() > 0){
					sql += " AND  pp.purposeId IN (:purpose) ";
				}
				
				if (assignmentIds != null && assignmentIds.size() > 0){
					sql += " AND a.AssignmentId IN (:assignmentIds) ";
				}
				
				if (quotation != null && quotation.size() > 0){
					sql += " AND q.QuotationId IN (:quotation) ";
				}
				
				if (unit != null && unit.size() > 0){
					sql += " AND q.UnitId IN (:unit) ";
				}
				
				if (quotationRecord != null && quotationRecord.size() > 0){
					sql += " AND qr.QuotationRecordId IN (:quotationRecord) ";
				}
				sql += " Order By qr.QuotationRecordId ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
			
		query.setParameter("refMonth", refMonth);	
		
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}

		if (assignmentIds != null && assignmentIds.size() > 0){
			query.setParameterList("assignmentIds", assignmentIds);
		}
		
		if (unit != null && unit.size() > 0){
			query.setParameterList("unit", unit);
		}
		
		if (quotation != null && quotation.size() > 0){
			query.setParameterList("quotation", quotation);
		}
		
		if (quotationRecord != null && quotationRecord.size() > 0){
			query.setParameterList("quotationRecord", quotationRecord);
		}
		
		query.addScalar("quotationRecordId", StandardBasicTypes.STRING);
		query.addScalar("tourRecordId", StandardBasicTypes.STRING);
		query.addScalar("nPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("sPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("day1Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day2Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day3Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day4Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day5Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day6Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day7Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day8Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day9Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day10Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day11Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day12Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day13Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day14Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day15Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day16Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day17Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day18Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day19Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day20Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day21Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day22Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day23Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day24Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day25Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day26Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day27Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day28Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day29Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day30Price", StandardBasicTypes.DOUBLE);
		query.addScalar("day31Price", StandardBasicTypes.DOUBLE);
		query.addScalar("extraPrice1Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice1Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice1Count", StandardBasicTypes.BOOLEAN);
		query.addScalar("extraPrice2Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice2Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice2Count", StandardBasicTypes.BOOLEAN);
		query.addScalar("extraPrice3Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice3Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice3Count", StandardBasicTypes.BOOLEAN);
		query.addScalar("extraPrice4Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice4Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice4Count", StandardBasicTypes.BOOLEAN);
		query.addScalar("extraPrice5Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice5Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice5Count", StandardBasicTypes.BOOLEAN);
		
		query.setResultTransformer(Transformers.aliasToBean(IndividualQuotationRecordReport4.class));

		return query.list();
	}

	public List<IndividualQuotationRecordReport5> getIndividualQuotationReocrdReport5(List<Integer> purpose, List<Integer> assignment,
			List<Integer> quotation, List<Integer> unit, List<Integer> quotationRecord, Date refMonth, Date endDate) {
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[getIndividualQuotationRecordReport] :refMonth, :endDate, :quotation, :assignmentIds , :unit, :purpose, :quotationRecord ");
		//SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[getIndividualQuotationRecordReport] :refMonth, :endDate, null, null, null, null ");
		
		query.setParameter("refMonth", refMonth);
		query.setParameter("endDate", endDate);
		
		StringBuilder builder = new StringBuilder();
		if(purpose != null) {
			builder.append("<query>");
			for (Integer purposeId : purpose){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");		
			query.setParameter("purpose", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("purpose", null);
		}
				
		if(assignment != null) {
			builder.append("<query>");
			for (Integer assignmentIds : assignment){
				builder.append("<assignmentId>"+assignmentIds+"</assignmentId>");
			}
			builder.append("</query>");		
			query.setParameter("assignmentIds", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("assignmentIds", null);
		}
			
		if(unit != null) {
			builder.append("<query>");
			for (Integer unitId : unit){
				builder.append("<unitId>"+unitId+"</unitId>");
			}
			builder.append("</query>");		
			query.setParameter("unit", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("unit", null);
		}
		
		if(quotation != null) {
			builder.append("<query>");
			for (Integer quotationId : quotation){
				builder.append("<quotationId>"+quotationId+"</quotationId>");
			}
			builder.append("</query>");		
			query.setParameter("quotation", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("quotation", null);
		}
		
		if(quotationRecord != null) {
			builder.append("<query>");
			for (Integer quotationRecordId : quotationRecord){
				builder.append("<quotationRecordId>"+quotationRecordId+"</quotationRecordId>");
			}
			builder.append("</query>");		
			query.setParameter("quotationRecord", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("quotationRecord", null);
		}
		
		query.addScalar("quotationRecordId", StandardBasicTypes.STRING);
		query.addScalar("pointToNoteId", StandardBasicTypes.STRING);
		query.addScalar("note", StandardBasicTypes.STRING);
		query.addScalar("effectiveDate", StandardBasicTypes.STRING);
		query.addScalar("expiryDate", StandardBasicTypes.STRING);
		query.addScalar("isAllOutlet",  StandardBasicTypes.BOOLEAN);
		query.addScalar("isAllProduct",  StandardBasicTypes.BOOLEAN);
		query.addScalar("isAllQuotation",  StandardBasicTypes.BOOLEAN);
		query.addScalar("isAllVariety",  StandardBasicTypes.BOOLEAN);
		query.addScalar("outletId", StandardBasicTypes.INTEGER);
		query.addScalar("firmCode", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.STRING);
		query.addScalar("quotationId", StandardBasicTypes.STRING);
		query.addScalar("unitId", StandardBasicTypes.INTEGER);
		//query.addScalar("unitCode", StandardBasicTypes.INTEGER);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("isOutlet",  StandardBasicTypes.BOOLEAN);
		query.addScalar("isProduct",  StandardBasicTypes.BOOLEAN);
		query.addScalar("isQuotation",  StandardBasicTypes.BOOLEAN);
		query.addScalar("isVariety",  StandardBasicTypes.BOOLEAN);

		
		query.setResultTransformer(Transformers.aliasToBean(IndividualQuotationRecordReport5.class));
		
		return query.list();
	}
	


	@SuppressWarnings("unchecked")
	public List<Integer> searchQuotationRecord(String search, int firstRecord, int displayLength,
			Integer[] quotationId, Integer[] assignmentId, Date referenceMonth) {		
		String hql = " select qr.quotationRecordId as id "				
				+ " from QuotationRecord as qr "
				+ " left join qr.assignment as a "
				+ " left join a.surveyMonth as sm "
				+ " where qr.quotationRecordId IS NOT NULL " ;
				

			if (assignmentId != null){
				hql += " and a.assignmentId in (:assignmentId) ";
			}
			
			if (quotationId != null){
				hql += " and quotationId in (:quotationId) ";
			}
			if (referenceMonth != null) {
				hql+= " and sm.referenceMonth = :referenceMonth ";
			}
			if (!StringUtils.isEmpty(search)){
				hql += " and str(qr.quotationRecordId) like :search ";
			}	
							
			hql += " order by qr.quotationRecordId ";

			Query query = this.getSession().createQuery(hql);
					query.setFirstResult(firstRecord);
					query.setMaxResults(displayLength);
					
			if (assignmentId != null){
				query.setParameterList("assignmentId", assignmentId);
			}
			if (quotationId != null){
				query.setParameterList("quotationId", quotationId);
			}
			if (referenceMonth != null){
				//referenceMonth = String.format(referenceMonth, SystemConstant.DATE_FORMAT);
				System.out.println("The new reference month is : " + referenceMonth);
				query.setParameter("referenceMonth", referenceMonth);
			}
			if (!StringUtils.isEmpty(search)){
				query.setParameter("search", String.format("%%%s%%", search));
			}
			return query.list();
	}
	
	public long countQuotationRecord(String search,
			Integer[] quotationId, Integer[] assignmentId, Date referenceMonth) {
		String hql = " select count(qr.quotationRecordId) as id "				
				+ " from QuotationRecord as qr"
				+ " left join qr.assignment as a"
				+ " left join a.surveyMonth as sm"
				+ " where qr.quotationRecordId IS NOT NULL " ;
		
		if (assignmentId != null){
			hql += " and a.assignmentId in (:assignmentId) ";
		}
		
		if (quotationId != null){
			hql += " and quotationId in (:quotationId) ";
		}
		
		if (referenceMonth != null){
			hql += " and sm.referenceMonth = :referenceMonth";
		}
		
		if (!StringUtils.isEmpty(search)){
			hql += " and str(qr.quotationRecordId) like :search ";
		}	

		Query query = this.getSession().createQuery(hql);
		
		if (quotationId != null){
			query.setParameterList("quotationId", quotationId);
		}
		
		if (assignmentId != null){
			query.setParameterList("assignmentId", assignmentId);
		}
		
		if (referenceMonth != null){
			query.setParameter("referenceMonth", referenceMonth);
		}
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		return (long)query.uniqueResult();
	}
	
	//public List<IndividualQuotationRecordReportImage> getIndividualQuotationReocrdReportImage(List<Integer> quotationRecordId) {
	public List<IndividualQuotationRecordReportImage> getIndividualQuotationReocrdReportImage(Integer[] quotationRecordId) {
		String sql = "SELECT "
				+ " a.AssignmentId AS [assignmentId] "
				+ " , p.Photo1Path AS [photo1Path] "
				+ " , p.Photo2Path AS [photo2Path] "
				+ " , o.OutletImagePath AS [outletPath]"
				+ " , oa.Path AS [outletAttachmentPath]"

				+ " FROM [QuotationRecord] as qr "
				+ " LEFT JOIN [Quotation] as q on q.quotationId = qr.quotationId "
				+ " LEFT JOIN [Assignment] as a on a.AssignmentId = qr.AssignmentId "
				+ " LEFT JOIN [Product] as p on p.ProductId = q.ProductId "
				+ " LEFT JOIN [Outlet] as o on o.OutletId = q.OutletId "
				+ " LEFT JOIN [OutletAttachment] as oa on oa.OutletId = o.OutletId "
				+ " LEFT JOIN [Unit] AS u ON q.unitId = u.unitId "
				+ " LEFT JOIN [Purpose] AS pp ON u.purposeId = pp.purposeId "
				
				+ " WHERE  qr.QuotationRecordId IS NOT NULL AND "
				+ " a.AssignmentId IS NOT NULL AND "
				+ " (p.Photo1Path IS NOT NULL OR "
				+ " p.Photo2Path IS NOT NULL OR "
				+ " oa.Path IS NOT NULL OR "
				+ " o.OutletImagePath IS NOT NULL) ";
		
		System.out.println("Doa : " + quotationRecordId.length);
		
		if (quotationRecordId != null && quotationRecordId.length > 0){
			sql += " AND qr.QuotationRecordId IN (:quotationRecordId) ";
		}
		
		sql += " GROUP BY qr.QuotationRecordId, a.AssignmentId, p.Photo1Path, p.Photo2Path, p.ProductId, oa.Path, o.OutletImagePath";
		sql += " ORDER BY qr.QuotationRecordId, a.AssignmentId";		
		
		SQLQuery query = this.getSession().createSQLQuery(sql);

		if (quotationRecordId != null && quotationRecordId.length > 0){
			query.setParameterList("quotationRecordId", quotationRecordId);
		}
		
		//query.addScalar("quotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("assignmentId", StandardBasicTypes.INTEGER);
		query.addScalar("outletPath", StandardBasicTypes.STRING);
		query.addScalar("outletAttachmentPath", StandardBasicTypes.STRING);
		query.addScalar("photo1Path", StandardBasicTypes.STRING);
		query.addScalar("photo2Path", StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(IndividualQuotationRecordReportImage.class));

		return query.list();
	}
	
	//When updating category remark in assignment maintenance, it cannot be updated for back no. quotations, while new remarks can be shown in original quotation.
	@SuppressWarnings("unchecked")
	public QuotationRecord getQuotationRecordsForFindBackNoData(QuotationRecord originalQuotationRecordId) {
		return (QuotationRecord)this.createCriteria()
		.add(Restrictions.eq("originalQuotationRecord", originalQuotationRecordId))
		.add(Restrictions.eq("isBackNo", true))
		.setMaxResults(1)
		.uniqueResult();

	}
	
	public List<QuotationRecord> getQuotationRecordStatus(List<Integer> quotationRecordIds){
		Criteria criteria = this.createCriteria("qr");
		criteria.add(Restrictions.in("quotationRecordId", quotationRecordIds));
		//criteria.add(Restrictions.like("status", "%Submitted%"));
		criteria.add(Restrictions.or(
                Restrictions.like("status", "%Submitted%"),
                Restrictions.like("status", "%Approved%")
            ));
		return criteria.list();
	}	
	
	
	public List<QuotationRecordSyncData> getRUAQuotationRecordStatus(List<Integer> assignmentIds){
		
		String sql = " ";
		
		sql += " SELECT qr.quotationrecordid AS QuotationRecordId, ";
		sql += " qr.ProductId AS ProductId, ";
		sql += " qr.QuotationId AS QuotationId, ";
		sql += " qr.OutletId AS OutletId, ";
		sql += " a.assignmentid AS AssignmentId ";
		sql += " FROM   assignment a  ";
		sql += " INNER JOIN quotationrecord qr ON a.assignmentid = qr.assignmentid  ";
		sql += " LEFT OUTER JOIN quotationrecord oq2 ON qr.quotationrecordid = oq2.originalquotationrecordid AND ( oq2.isbackno = 0 AND oq2.isbacktrack = 1 AND oq2.passvalidation = 0 )  ";
		sql += " LEFT OUTER JOIN quotationrecord oq3 ON qr.quotationrecordid = oq3.originalquotationrecordid AND ( oq3.isbackno = 0 AND oq3.isbacktrack = 1 )  ";
		sql += " LEFT OUTER JOIN [user] u ON qr.userid = u.userid  ";
		sql += " LEFT OUTER JOIN quotation q ON qr.quotationid = q.quotationid  ";
		sql += " LEFT OUTER JOIN batch b ON q.batchid = b.batchid  ";
		sql += " LEFT OUTER JOIN dbo.unit un ON q.unitid = un.unitid  ";
		sql += " WHERE  1 = 1  ";
		sql += " AND qr.isnewrecruitment = 1  ";
		sql += " AND qr.status = 'Submitted'  ";
		sql += " AND qr.isbackno = 0  ";
		sql += " AND qr.isbacktrack = 0  ";
		sql += " AND a.assignmentid IN (:AssignmentId) ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);

		if (assignmentIds != null && assignmentIds.size() > 0){
			query.setParameterList("AssignmentId", assignmentIds);
		}
		
		query.addScalar("QuotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("ProductId", StandardBasicTypes.INTEGER);
		query.addScalar("QuotationId", StandardBasicTypes.INTEGER);
		query.addScalar("OutletId", StandardBasicTypes.INTEGER);
		query.addScalar("AssignmentId", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordSyncData.class));

		return query.list();
	}

	public long getQuotationCountInOutlet(Integer unitId, Integer outletId) {
		String hql = "select "
					+ " q.unit.unitId as quotationUnitId, "
					+ " count(q.unit.unitId) as quotationCount"
				+ " from Quotation as q "
					+ " left join q.outlet as o "
					+ " left join q.unit as u "
				+ " where "
					+ " o.outletId = :outletId and "
					+ " q.unit.unitId = :unitId and "
					+ " q.status = 'Active' "
				+ " group by q.unit.unitId";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("outletId", outletId);
		query.setParameter("unitId", unitId);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		List<Map> result = query.list();
		if (result.isEmpty()) return 0;
		
		Map quotationIdCount = result.get(0);
		int selectedUnitId = (int) quotationIdCount.get("quotationUnitId");
		if (selectedUnitId != unitId) return 0;

		return (long) quotationIdCount.get("quotationCount");
	}	
	
}
