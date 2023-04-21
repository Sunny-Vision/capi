package capi.dal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.IndoorQuotationRecord;
import capi.entity.Quotation;
import capi.model.SystemConstant;
import capi.model.assignmentAllocationAndReallocation.surveyMonth.generation.MatchedPEQuotationRecordResult;
import capi.model.commonLookup.QuotationRecordHistoryStatisticLast2Year;
import capi.model.dataConversion.quotationRecordDataConversion.AllocateQuotationRecordDataConversionFilterModel;
import capi.model.dataConversion.quotationRecordDataConversion.AllocateQuotationRecordDataConversionTableListModel;
import capi.model.dataConversion.quotationRecordDataConversion.QuotationRecordDataConversionTableListModel;
import capi.model.dataImportExport.ExportCPICompilationList;
import capi.model.dataImportExport.ExportEditPriceStatisticList;
import capi.model.dataImportExport.ExportEditedPriceList;
import capi.model.quotationRecordVerificationApproval.QuotationRecordVerificationApprovalFilterModel;
import capi.model.quotationRecordVerificationApproval.QuotationRecordVerificationApprovalTableListModel;
import capi.model.report.AirportTicketPRListByCountryWithCPISeries;
import capi.model.report.FRAdjustment;
import capi.model.report.IndoorStaffIndividualProgress;
import capi.model.report.IndoorStaffProgress;
import capi.model.report.ListOfQuotationRecords;
import capi.model.report.NewRecruitmentsAndProductReplacements;
import capi.model.report.OutrangedQuotationRecords;
import capi.model.report.QuotationRecordImputationReport;
import capi.model.report.QuotationRecordProgress;
import capi.model.report.QuotationStatisticsReportByQuotation;
import capi.model.report.QuotationStatisticsReportByVariety;
import capi.model.report.QuotationTimeSeries;
import capi.model.report.SummaryStatistic;
import capi.model.report.SupermarketProductReview;

@Repository("IndoorQuotationRecordDao")
public class IndoorQuotationRecordDao  extends GenericDao<IndoorQuotationRecord>{

	public List<IndoorStaffIndividualProgress> getIndoorStaffIndividualProgress(List<Integer> userIds, List<Integer> purpose, 
			List<Integer> cpiSurveyForm, List<Integer> batch, Date startMonth, Date endMonth, 
			List<Integer> groups, List<Integer> subgroups, List<String> cpiBasePeriods){
		
		String referenceDate = String.format("FORMAT(iqr.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		
		String sql = "Select ur.EnglishName as username" + 
				"	, case when iqr.ReferenceMonth is not null then " + referenceDate + " else '' end as referenceMonth " +
				"	, pp.Code as purpose" + 
				"	, u.CPIBasePeriod as cpiBasePeriod" + 
				"	, g.code as groupCode" + 
				"	, g.ChineseName as groupChineseName" + 
				"	, g.EnglishName as groupEnglishName" + 
				"	, sg.code as subGroupCode" + 
				"	, sg.ChineseName as subGroupChineseName" + 
				"	, sg.EnglishName as subGroupEnglishName" + 
				"	, SUM(case when iqr.[Status] in ('Conversion', 'Complete', 'Request Verification'" +
					" , 'Approve Verification', 'Reject Verification', 'Review Verification') then 1 else 0 end) as allocator" + 
				"	, SUM(case when iqr.[Status] in ('Conversion', 'Reject Verification', 'Request Verification') then 1 else 0 end) as conversion" + 
				"	, SUM(case when iqr.[Status] = 'Complete' then 1 else 0 end) as complete" + 
				"	, SUM(case when iqr.[Status] in ('Approve Verification', 'Review Verification') then 1 else 0 end) as verification" + 
				"	from IndoorQuotationRecord as iqr" + 
				"	left join QuotationRecord as qr on iqr.QuotationRecordId = qr.QuotationRecordId" + 
				"	left join Quotation as q on iqr.QuotationId = q.QuotationId" + 
				"	left join Unit as u on q.UnitId = u.UnitId" + 
				"	left join Purpose as pp on u.PurposeId = pp.PurposeId" + 
				"	left join SubItem as si on si.SubItemId = u.SubItemId" + 
				"	left join OutletType as ot on si.OutletTypeId = ot.outletTypeId" + 
				"	left join Item as i on i.ItemId = ot.ItemId" + 
				"	left join SubGroup as sg on i.SubGroupId = sg.SubGroupId" + 
				"	left join [Group] as g on g.GroupId = sg.GroupId" +
				"	left join [User] as ur on ur.UserId = iqr.userId" +
				"	where iqr.ReferenceMonth between :startMonth and :endMonth";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		if (batch != null && batch.size() > 0){
			sql += " and q.batchId in (:batch) ";
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
		
		if (userIds != null && userIds.size()>0) {
			sql += " and iqr.userId in ( :userIds )";
		}
		
		sql += " Group By ur.EnglishName, iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod, g.code, g.ChineseName, g.EnglishName, sg.Code, sg.ChineseName, sg.EnglishName";
		
		sql += " order by iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod, sg.Code";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
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
		if (userIds != null && userIds.size()>0) {
			query.setParameterList("userIds", userIds);
		}
		query.addScalar("username",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("groupCode",StandardBasicTypes.STRING);
		query.addScalar("groupChineseName",StandardBasicTypes.STRING);
		query.addScalar("groupEnglishName",StandardBasicTypes.STRING);
		query.addScalar("subGroupCode",StandardBasicTypes.STRING);
		query.addScalar("subGroupChineseName",StandardBasicTypes.STRING);
		query.addScalar("subGroupEnglishName",StandardBasicTypes.STRING);
		query.addScalar("allocator",StandardBasicTypes.LONG);
		query.addScalar("conversion",StandardBasicTypes.LONG);
		query.addScalar("complete",StandardBasicTypes.LONG);
		query.addScalar("verification",StandardBasicTypes.LONG);
		query.setResultTransformer(Transformers.aliasToBean(IndoorStaffIndividualProgress.class));
		
		return query.list();
	}
	
	public List<IndoorStaffIndividualProgress> getIndoorStaffIndividualProgressGroup(List<Integer> userIds, List<Integer> purpose, 
			List<Integer> cpiSurveyForm, List<Integer> batch, Date startMonth, Date endMonth, 
			List<Integer> groups, List<Integer> subgroups, List<String> cpiBasePeriods){
		
		String referenceDate = String.format("FORMAT(iqr.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		
		String sql = "Select ur.EnglishName as username" + 
				"	, case when iqr.ReferenceMonth is not null then " + referenceDate + " else '' end as referenceMonth " +
				"	, pp.Code as purpose" + 
				"	, u.CPIBasePeriod as cpiBasePeriod" + 
				"	, g.code as groupCode" + 
				"	, g.ChineseName as groupChineseName" + 
				"	, g.EnglishName as groupEnglishName" + 
				"	, SUM(case when iqr.[Status] in ('Conversion', 'Complete', 'Request Verification'" +
					" , 'Approve Verification', 'Reject Verification', 'Review Verification') then 1 else 0 end) as allocator" + 
				"	, SUM(case when iqr.[Status] in ('Conversion', 'Reject Verification', 'Request Verification') then 1 else 0 end) as conversion" + 
				"	, SUM(case when iqr.[Status] = 'Complete' then 1 else 0 end) as complete" + 
				"	, SUM(case when iqr.[Status] in ('Approve Verification', 'Review Verification') then 1 else 0 end) as verification" +
				"	from IndoorQuotationRecord as iqr" + 
				"	left join QuotationRecord as qr on iqr.QuotationRecordId = qr.QuotationRecordId" + 
				"	left join Quotation as q on iqr.QuotationId = q.QuotationId" + 
				"	left join Unit as u on q.UnitId = u.UnitId" + 
				"	left join Purpose as pp on u.PurposeId = pp.PurposeId" + 
				"	left join SubItem as si on si.SubItemId = u.SubItemId" + 
				"	left join OutletType as ot on si.OutletTypeId = ot.outletTypeId" + 
				"	left join Item as i on i.ItemId = ot.ItemId" + 
				"	left join SubGroup as sg on i.SubGroupId = sg.SubGroupId" + 
				"	left join [Group] as g on g.GroupId = sg.GroupId" +
				" 	left join [User] as ur on ur.userId = iqr.userId" + 
				"	where iqr.ReferenceMonth between :startMonth and :endMonth";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		if (batch != null && batch.size() > 0){
			sql += " and q.batchId in (:batch) ";
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
		
		if (userIds != null && userIds.size()>0) {
			sql += " and iqr.userId in ( :userIds )";
		}
		
		sql += " Group By ur.EnglishName, iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod, g.code, g.ChineseName, g.EnglishName";
		
		sql += " order by iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod, g.code";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
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
		if (userIds != null && userIds.size()>0) {
			query.setParameterList("userIds", userIds);
		}
		query.addScalar("username",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("groupCode",StandardBasicTypes.STRING);
		query.addScalar("groupChineseName",StandardBasicTypes.STRING);
		query.addScalar("groupEnglishName",StandardBasicTypes.STRING);
		query.addScalar("allocator",StandardBasicTypes.LONG);
		query.addScalar("conversion",StandardBasicTypes.LONG);
		query.addScalar("complete",StandardBasicTypes.LONG);
		query.addScalar("verification",StandardBasicTypes.LONG);
		query.setResultTransformer(Transformers.aliasToBean(IndoorStaffIndividualProgress.class));
		
		return query.list();
	}
	
	public List<IndoorStaffIndividualProgress> getIndoorStaffIndividualProgressOverall(List<Integer> userIds, List<Integer> purpose, 
			List<Integer> cpiSurveyForm, List<Integer> batch, Date startMonth, Date endMonth, 
			List<Integer> groups, List<Integer> subgroups, List<String> cpiBasePeriods){
		
		String referenceDate = String.format("FORMAT(iqr.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		
		String sql = "Select ur.EnglishName as username" + 
				" 	, case when iqr.ReferenceMonth is not null then " + referenceDate + " else '' end as referenceMonth " +
				"	, pp.Code as purpose" + 
				"	, u.CPIBasePeriod as cpiBasePeriod" + 
				"	, SUM(case when iqr.[Status] in ('Conversion', 'Complete', 'Request Verification'" +
					" , 'Approve Verification', 'Reject Verification', 'Review Verification') then 1 else 0 end) as allocator" + 
				"	, SUM(case when iqr.[Status] in ('Conversion', 'Reject Verification', 'Request Verification') then 1 else 0 end) as conversion" + 
				"	, SUM(case when iqr.[Status] = 'Complete' then 1 else 0 end) as complete" + 
				"	, SUM(case when iqr.[Status] in ('Approve Verification', 'Review Verification') then 1 else 0 end) as verification" +
				"	from IndoorQuotationRecord as iqr" + 
				"	left join QuotationRecord as qr on iqr.QuotationRecordId = qr.QuotationRecordId" + 
				"	left join Quotation as q on iqr.QuotationId = q.QuotationId" + 
				"	left join Unit as u on q.UnitId = u.UnitId" + 
				"	left join Purpose as pp on u.PurposeId = pp.PurposeId" + 
				"	left join SubItem as si on si.SubItemId = u.SubItemId" + 
				"	left join OutletType as ot on si.OutletTypeId = ot.outletTypeId" + 
				"	left join Item as i on i.ItemId = ot.ItemId" + 
				"	left join SubGroup as sg on i.SubGroupId = sg.SubGroupId" + 
				"	left join [Group] as g on g.GroupId = sg.GroupId" + 
				"	left join [User] as ur on ur.userId = iqr.userId " +
				"	where iqr.ReferenceMonth between :startMonth and :endMonth";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		if (batch != null && batch.size() > 0){
			sql += " and q.batchId in (:batch) ";
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
		
		if (userIds != null && userIds.size()>0) {
			sql += " and iqr.userId in ( :userIds )";
		}
		
		sql += " Group By ur.EnglishName, iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod ";
		
		sql += " order by iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
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
		if (userIds != null && userIds.size()>0) {
			query.setParameterList("userIds", userIds);
		}
		query.addScalar("username",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("allocator",StandardBasicTypes.LONG);
		query.addScalar("conversion",StandardBasicTypes.LONG);
		query.addScalar("complete",StandardBasicTypes.LONG);
		query.addScalar("verification",StandardBasicTypes.LONG);
		query.setResultTransformer(Transformers.aliasToBean(IndoorStaffIndividualProgress.class));
		
		return query.list();
	}
	
	public List<ExportEditedPriceList> getResultsByMonth(Date referenceMonth, Integer purposeId){
		String sql = "Select indoor.indoorQuotationRecordId as indoorQuotationRecordId"
				+ ", indoor.referenceMonth as referenceMonth"
				+ ", indoor.referenceDate as referenceDate"
				+ ", indoor.isNoField as isNoField"
				+ ", indoor.isProductChange as isProductChange"
				+ ", indoor.isNewProduct as isNewProduct"
				+ ", indoor.isNewRecruitment as isNewRecruitment"
				+ ", indoor.isNewOutlet as isNewOutlet"
				+ ", indoor.nPriceAfterUOMConversion as nPriceAfterUOMConversion"
				+ ", indoor.sPriceAfterUOMConversion as sPriceAfterUOMConversion"
				+ ", indoor.computedNPrice as computedNPrice"
				+ ", indoor.computedSPrice as computedSPrice"
				+ ", indoor.currentNPrice as currentNPrice"
				+ ", indoor.isNullCurrentNPrice as isNullCurrentNPrice"
				+ ", indoor.currentSPrice as currentSPrice"
				+ ", indoor.isNullCurrentSPrice as isNullCurrentSPrice"
				+ ", indoor.previousNPrice as previousNPrice"
				+ ", indoor.isNullPreviousNPrice as isNullPreviousNPrice"
				+ ", indoor.previousSPrice as previousSPrice"
				+ ", indoor.isNullPreviousSPrice as isNullPreviousSPrice"
				+ ", indoor.backNoLastNPirce as backNoLastNPirce"
				+ ", indoor.backNoLastSPrice as backNoLastSPrice"
				+ ", indoor.lastNPrice as lastNPrice"
				+ ", indoor.lastSPrice as lastSPrice"
				+ ", indoor.lastPriceDate as lastPriceDate"
				+ ", indoor.copyPriceType as copyPriceType"
				+ ", indoor.copyLastPriceType as copyLastPriceType"
				+ ", indoor.remark as remark"
				+ ", indoor.[status] as [status]"
				+ ", indoor.isCurrentPriceKeepNo as isCurrentPriceKeepNo"
				+ ", indoor.fr as fr"
				+ ", indoor.isApplyFR as isApplyFR"
				+ ", indoor.isFRPercentage as isFRPercentage"
				+ ", indoor.isRUA as isRUA"
				+ ", indoor.ruaDate as ruaDate"
				+ ", indoor.isProductNotAvailable as isProductNotAvailable"
				+ ", indoor.productNotAvailableFrom as productNotAvailableFrom"
				+ ", indoor.isSpicing as isSpicing"
				+ ", indoor.isOutlier as isOutlier"
				+ ", indoor.outlierRemark as outlierRemark"
				+ ", indoor.modifiedBy as modifiedBy"
				+ ", indoor.modifiedDate as modifiedDate"
				//ImputeQuotation
				+ ", iq.price as imputedQuotationPrice"
				+ ", iq.remark as imputedQuotationRemark"
				+ ", iu.price as imputedUnitPrice"
				+ ", iu.remark as imputedUnitRemark"
				//Verfication
				+ ", sum(case when [VerifyType] = 1 then 1 else 0 end) as firmVerify"
				+ ", sum(case when [VerifyType] = 2 then 1 else 0 end) as categoryVerify"
				+ ", sum(case when [VerifyType] = 3 then 1 else 0 end) as quotationVerify"
				//User
				+ ", ur.staffCode as staffCode"
				//QuotationRecord
				+ ", qr.quotationRecordId as quotationRecordId"
				+ ", qr.nPrice as originalNPrice"
				+ ", qr.sPrice as originalSPrice"
				+ ", qr.remark as quotationRemark"
				+ ", qr.outletDiscountRemark as firmRemark"
				+ ", qr.verificationRemark as rejectReason"
				//AssignmentUnitCategoryInfo
				+ ", ac.remark as categoryRemark"
				//Product
				+ ", case when qr.quotationRecordId is not null then p.productId else qp.productId end as productId"
				+ ", case when qr.quotationRecordId is not null then p.countryOfOrigin else qp.countryOfOrigin end as countryOfOrigin"
				//ProductGroup
				+ ", case when qr.quotationRecordId is not null then pg.productGroupId else qpg.productGroupId end as productGroupId"
				+ ", case when qr.quotationRecordId is not null then pg.code else qpg.code end as productGroupCode"
				+ ", case when qr.quotationRecordId is not null then pg.englishName else qpg.englishName end as productGroupEnglishName"
				+ ", case when qr.quotationRecordId is not null then pg.chineseName else qpg.chineseName end as productGroupChineseName"
				//ProductAttribute
				+ ", case when qr.quotationRecordId is not null then pa1.specificationName else qpa1.specificationName end as pa1Name"
				+ ", case when qr.quotationRecordId is not null then pa2.specificationName else qpa2.specificationName end as pa2Name"
				+ ", case when qr.quotationRecordId is not null then pa3.specificationName else qpa3.specificationName end as pa3Name"
				+ ", case when qr.quotationRecordId is not null then pa4.specificationName else qpa4.specificationName end as pa4Name"
				+ ", case when qr.quotationRecordId is not null then pa5.specificationName else qpa5.specificationName end as pa5Name"
				//ProductSpecification
				+ ", case when qr.quotationRecordId is not null then ps1.value else qps1.value end as ps1Value"
				+ ", case when qr.quotationRecordId is not null then ps2.value else qps2.value end as ps2Value"
				+ ", case when qr.quotationRecordId is not null then ps3.value else qps3.value end as ps3Value"
				+ ", case when qr.quotationRecordId is not null then ps4.value else qps4.value end as ps4Value"
				+ ", case when qr.quotationRecordId is not null then ps5.value else qps5.value end as ps5Value"
				//Outlet
				+ ", case when qr.quotationRecordId is not null then o.firmCode else qo.firmCode end as outletFirmCode"
				+ ", case when qr.quotationRecordId is not null then o.name else qo.name end as outletName"
				+ ", case when qr.quotationRecordId is not null then o.detailAddress else qo.detailAddress end as detailAddress"
				+ ", case when qr.quotationRecordId is not null then o.indoorMarketName else qo.indoorMarketName end as indoorMarketName"
				//Tpu
				+ ", case when qr.quotationRecordId is not null then t.code else qt.code end as tpuCode"
				+ ", case when qr.quotationRecordId is not null then t.councilDistrict else qt.councilDistrict end as councilDistrict"
				//Quotation
				+ ", q.quotationId as quotationId"
				+ ", q.[status] as quotationStatus"
				+ ", q.formType as formType"
				+ ", q.cpiCompilationSeries as cpiCompilationSeries"
				+ ", q.isICP as isICP"
				+ ", q.icpProductCode as icpProductCode"
				+ ", q.icpProductName as icpProductName"
				+ ", q.icpType as icpType"
				+ ", q.lastProductChangeDate as lastProductChangeDate"
				+ ", q.lastFRAppliedDate as lastFRAppliedDate"
				//Unit
				+ ", u.code as unitCode"
				+ ", u.cpiBasePeriod as cpiBasePeriod"
				+ ", u.englishName as unitEnglishName"
				+ ", u.chineseName as unitChineseName"
				+ ", u.seasonality as seasonality"
				+ ", u.cpiQoutationType as cpiQuotationType"
				//OutletType
				+ ", ot.code as outletTypeCode"
				+ ", ot.englishName as outletTypeEnglishName"
				//Purpose
				+ ", pp.code as purposeCode"
				//District
				+ ", case when qr.quotationRecordId is not null then d.code else qd.code end as districtCode"
				+ ", case when qr.quotationRecordId is not null then d.englishName else qd.englishName end as districtEnglishName"
				//Batch
				+ ", b.code as batchCode"
				+ " From [IndoorQuotationRecord] as indoor"
				+ " left join [User] as ur on indoor.userId = ur.userId"
				+ " left join [QuotationRecord] as qr on indoor.quotationRecordId = qr.quotationRecordId"
				+ " left join [Outlet] as o on qr.outletId = o.outletId"
				+ " left join [TPU] as t on t.tpuId = o.tpuId"
				+ " left join [District] as d on t.districtId = d.districtId"
				+ " left join [Product] as p on qr.productId = p.productId"
				+ " left join [ProductGroup] as pg on p.productGroupId = pg.productGroupId"
				+ " left join [Quotation] as q on indoor.quotationId = q.quotationId"
				+ " left join [Outlet] as qo on q.outletId = qo.outletId"
				+ " left join [TPU] as qt on qt.tpuId = qo.tpuId"
				+ " left join [District] as qd on qt.districtId = qd.districtId"
				+ " left join [Product] as qp on q.productId = qp.productId"
				+ " left join [ProductGroup] as qpg on qp.productGroupId = qpg.productGroupId"
				+ " left join [Batch] as b on q.batchId = b.batchId"
				+ " left join [Unit] as u on q.unitId = u.unitId"
				+ " left join [Purpose] as pp on u.purposeId = pp.purposeId"
				+ " left join [SubItem] as si on u.subItemId = si.subItemId"
				+ " left join [OutletType] as ot on ot.outletTypeId = si.outletTypeId"
				+ " left join [ProductAttribute] as pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.[Sequence] = 1"
				+ " left join [ProductAttribute] as pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.[Sequence] = 2"
				+ " left join [ProductAttribute] as pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.[Sequence] = 3"
				+ " left join [ProductAttribute] as pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.[Sequence] = 4"
				+ " left join [ProductAttribute] as pa5 on pa5.ProductGroupId = pg.ProductGroupId and pa5.[Sequence] = 5"
				+ " left join [ProductSpecification] as ps1 on ps1.ProductId = p.ProductId and pa1.ProductAttributeId = ps1.ProductAttributeId"
				+ " left join [ProductSpecification] as ps2 on ps2.ProductId = p.ProductId and pa2.ProductAttributeId = ps2.ProductAttributeId"
				+ " left join [ProductSpecification] as ps3 on ps3.ProductId = p.ProductId and pa3.ProductAttributeId = ps3.ProductAttributeId"
				+ " left join [ProductSpecification] as ps4 on ps4.ProductId = p.ProductId and pa4.ProductAttributeId = ps4.ProductAttributeId"
				+ " left join [ProductSpecification] as ps5 on ps5.ProductId = p.ProductId and pa5.ProductAttributeId = ps5.ProductAttributeId"
				+ " left join [ProductAttribute] as qpa1 on qpa1.ProductGroupId = qpg.ProductGroupId and qpa1.[Sequence] = 1"
				+ " left join [ProductAttribute] as qpa2 on qpa2.ProductGroupId = qpg.ProductGroupId and qpa2.[Sequence] = 2"
				+ " left join [ProductAttribute] as qpa3 on qpa3.ProductGroupId = qpg.ProductGroupId and qpa3.[Sequence] = 3"
				+ " left join [ProductAttribute] as qpa4 on qpa4.ProductGroupId = qpg.ProductGroupId and qpa4.[Sequence] = 4"
				+ " left join [ProductAttribute] as qpa5 on qpa5.ProductGroupId = qpg.ProductGroupId and qpa5.[Sequence] = 5"
				+ " left join [ProductSpecification] as qps1 on qps1.ProductId = qp.ProductId and qpa1.ProductAttributeId = qps1.ProductAttributeId"
				+ " left join [ProductSpecification] as qps2 on qps2.ProductId = qp.ProductId and qpa2.ProductAttributeId = qps2.ProductAttributeId"
				+ " left join [ProductSpecification] as qps3 on qps3.ProductId = qp.ProductId and qpa3.ProductAttributeId = qps3.ProductAttributeId"
				+ " left join [ProductSpecification] as qps4 on qps4.ProductId = qp.ProductId and qpa4.ProductAttributeId = qps4.ProductAttributeId"
				+ " left join [ProductSpecification] as qps5 on qps5.ProductId = qp.ProductId and qpa5.ProductAttributeId = qps5.ProductAttributeId"
				// 2020-07-29: (fix PIR-238) select latest unit category info in reference month
				+ " left join [AssignmentUnitCategoryInfo] as ac on ac.assignmentId = qr.assignmentId and ac.unitCategory = u.unitCategory and ac.CreatedDate = (select max(CreatedDate) from AssignmentUnitCategoryInfo where AssignmentId = qr.AssignmentId)"
				+ " left join [ImputeQuotation] as iq on q.quotationId = iq.quotationId and indoor.referenceMonth = iq.referenceMonth"
				+ " left join [ImputeUnit] as iu on u.unitId = iu.unitId and indoor.referenceMonth = iu.referenceMonth"
				+ " left join [VerficiationQuotationRecordHistory] as verf on qr.quotationRecordId = verf.quotationRecordId"
				+ " left join [IndoorVerificationHistory] as indoorVerf on verf.indoorVerificationHistoryId = indoorVerf.IndoorVerificationHistoryId"
				+ " where indoor.referenceMonth = :referenceMonth";
		
		if (purposeId != null){
			sql += " and pp.purposeId = :purposeId ";
		}
		
		sql += " group by indoor.indoorQuotationRecordId"
				+ ", indoor.referenceMonth"
				+ ", indoor.referenceDate"
				+ ", indoor.isNoField"
				+ ", indoor.isProductChange"
				+ ", indoor.isNewProduct"
				+ ", indoor.isNewRecruitment"
				+ ", indoor.isNewOutlet"
				+ ", indoor.nPriceAfterUOMConversion"
				+ ", indoor.sPriceAfterUOMConversion"
				+ ", indoor.computedNPrice"
				+ ", indoor.computedSPrice"
				+ ", indoor.currentNPrice"
				+ ", indoor.isNullCurrentNPrice"
				+ ", indoor.currentSPrice"
				+ ", indoor.isNullCurrentSPrice"
				+ ", indoor.previousNPrice"
				+ ", indoor.isNullPreviousNPrice"
				+ ", indoor.previousSPrice"
				+ ", indoor.isNullPreviousSPrice"
				+ ", indoor.backNoLastNPirce"
				+ ", indoor.backNoLastSPrice"
				+ ", indoor.lastNPrice"
				+ ", indoor.lastSPrice"
				+ ", indoor.lastPriceDate"
				+ ", indoor.copyPriceType"
				+ ", indoor.copyLastPriceType"
				+ ", indoor.remark"
				+ ", indoor.[status]"
				+ ", indoor.isCurrentPriceKeepNo"
				+ ", indoor.fr"
				+ ", indoor.isApplyFR"
				+ ", indoor.isFRPercentage"
				+ ", indoor.isRUA"
				+ ", indoor.ruaDate"
				+ ", indoor.isProductNotAvailable"
				+ ", indoor.productNotAvailableFrom"
				+ ", indoor.isSpicing"
				+ ", indoor.isOutlier"
				+ ", indoor.outlierRemark"
				+ ", indoor.modifiedBy"
				+ ", indoor.modifiedDate"
				//ImputeQuotation
				+ ", iq.price"
				+ ", iq.remark"
				+ ", iu.price"
				+ ", iu.remark"
				//User
				+ ", ur.staffCode"
				//QuotationRecord
				+ ", qr.quotationRecordId"
				+ ", qr.nPrice"
				+ ", qr.sPrice"
				+ ", qr.remark"
				+ ", qr.outletDiscountRemark"
				+ ", qr.verificationRemark"
				//AssignmentUnitCategoryInfo
				+ ", ac.remark"
				//Product
				+ ", p.productId, qp.productId"
				+ ", p.countryOfOrigin, qp.countryOfOrigin"
				//ProductGroup
				+ ", pg.productGroupId, qpg.productGroupId"
				+ ", pg.code, qpg.code"
				+ ", pg.englishName, qpg.englishName"
				+ ", pg.chineseName, qpg.chineseName"
				//ProductAttribute
				+ ", pa1.specificationName, qpa1.specificationName"
				+ ", pa2.specificationName, qpa2.specificationName"
				+ ", pa3.specificationName, qpa3.specificationName"
				+ ", pa4.specificationName, qpa4.specificationName"
				+ ", pa5.specificationName, qpa5.specificationName"
				//ProductSpecification
				+ ", ps1.value, qps1.value"
				+ ", ps2.value, qps2.value"
				+ ", ps3.value, qps3.value"
				+ ", ps4.value, qps4.value"
				+ ", ps5.value, qps5.value"
				//Outlet
				+ ", o.firmCode, qo.firmCode"
				+ ", o.name, qo.name"
				+ ", o.detailAddress, qo.detailAddress"
				+ ", o.indoorMarketName, qo.indoorMarketName"
				//Tpu
				+ ", t.code, qt.code"
				+ ", t.councilDistrict, qt.councilDistrict"
				//Quotation
				+ ", q.quotationId"
				+ ", q.[status]"
				+ ", q.formType"
				+ ", q.cpiCompilationSeries"
				+ ", q.isICP"
				+ ", q.icpProductCode"
				+ ", q.icpProductName"
				+ ", q.icpType"
				+ ", q.lastProductChangeDate"
				+ ", q.lastFRAppliedDate"
				//Unit
				+ ", u.code"
				+ ", u.cpiBasePeriod"
				+ ", u.englishName"
				+ ", u.chineseName"
				+ ", u.seasonality"
				+ ", u.cpiQoutationType"
				//OutletType
				+ ", ot.code"
				+ ", ot.englishName"
				//Purpose
				+ ", pp.code"
				//District
				+ ", d.code, qd.code"
				+ ", d.englishName, qd.englishName"
				//Batch
				+ ", b.code";
		
		sql += " order by indoor.indoorQuotationRecordId";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("referenceMonth", referenceMonth);
		if (purposeId != null){
			query.setParameter("purposeId", purposeId);
		}
		
		query.addScalar("indoorQuotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("referenceDate",StandardBasicTypes.DATE);
		query.addScalar("isNoField",StandardBasicTypes.BOOLEAN);
		query.addScalar("isProductChange",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewProduct",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewRecruitment",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewOutlet",StandardBasicTypes.BOOLEAN);
		query.addScalar("originalNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("originalSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceAfterUOMConversion",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceAfterUOMConversion",StandardBasicTypes.DOUBLE);
		query.addScalar("computedNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("computedSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("isNullCurrentNPrice",StandardBasicTypes.BOOLEAN);
		query.addScalar("currentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("isNullCurrentSPrice",StandardBasicTypes.BOOLEAN);
		query.addScalar("previousNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("isNullPreviousNPrice",StandardBasicTypes.BOOLEAN);
		query.addScalar("previousSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("isNullPreviousSPrice",StandardBasicTypes.BOOLEAN);
		query.addScalar("backNoLastNPirce",StandardBasicTypes.DOUBLE);
		query.addScalar("backNoLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastPriceDate",StandardBasicTypes.DATE);
		query.addScalar("copyPriceType",StandardBasicTypes.INTEGER);
		query.addScalar("copyLastPriceType",StandardBasicTypes.INTEGER);
		query.addScalar("remark",StandardBasicTypes.STRING);
		query.addScalar("status",StandardBasicTypes.STRING);
		query.addScalar("isCurrentPriceKeepNo",StandardBasicTypes.BOOLEAN);
		query.addScalar("fr",StandardBasicTypes.DOUBLE);
		query.addScalar("isApplyFR",StandardBasicTypes.BOOLEAN);
		query.addScalar("isFRPercentage",StandardBasicTypes.BOOLEAN);
		query.addScalar("imputedQuotationPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("imputedQuotationRemark",StandardBasicTypes.STRING);
		query.addScalar("imputedUnitPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("imputedUnitRemark",StandardBasicTypes.STRING);
		query.addScalar("isRUA",StandardBasicTypes.BOOLEAN);
		query.addScalar("ruaDate",StandardBasicTypes.DATE);
		query.addScalar("isProductNotAvailable",StandardBasicTypes.BOOLEAN);
		query.addScalar("productNotAvailableFrom",StandardBasicTypes.DATE);
		query.addScalar("rejectReason",StandardBasicTypes.STRING);
		query.addScalar("isSpicing",StandardBasicTypes.BOOLEAN);
		query.addScalar("isOutlier",StandardBasicTypes.BOOLEAN);
		query.addScalar("outlierRemark",StandardBasicTypes.STRING);
		query.addScalar("firmRemark",StandardBasicTypes.STRING);
		query.addScalar("categoryRemark",StandardBasicTypes.STRING);
		query.addScalar("quotationRemark",StandardBasicTypes.STRING);
		query.addScalar("modifiedBy", StandardBasicTypes.STRING);
		query.addScalar("modifiedDate", StandardBasicTypes.DATE);
		//Indoor Verification
		query.addScalar("firmVerify",StandardBasicTypes.LONG);
		query.addScalar("categoryVerify",StandardBasicTypes.LONG);
		query.addScalar("quotationVerify",StandardBasicTypes.LONG);
		//User
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		//QuotationRecord
		query.addScalar("quotationRecordId",StandardBasicTypes.INTEGER);
		//Product
		query.addScalar("productId",StandardBasicTypes.INTEGER);
		query.addScalar("countryOfOrigin",StandardBasicTypes.STRING);
		//ProductGroup
		query.addScalar("productGroupId",StandardBasicTypes.INTEGER);
		query.addScalar("productGroupCode",StandardBasicTypes.STRING);
		query.addScalar("productGroupEnglishName",StandardBasicTypes.STRING);
		query.addScalar("productGroupChineseName",StandardBasicTypes.STRING);
		//ProductAttribute
		query.addScalar("pa1Name",StandardBasicTypes.STRING);
		query.addScalar("pa2Name",StandardBasicTypes.STRING);
		query.addScalar("pa3Name",StandardBasicTypes.STRING);
		query.addScalar("pa4Name",StandardBasicTypes.STRING);
		query.addScalar("pa5Name",StandardBasicTypes.STRING);
		//ProductSpecification
		query.addScalar("ps1Value",StandardBasicTypes.STRING);
		query.addScalar("ps2Value",StandardBasicTypes.STRING);
		query.addScalar("ps3Value",StandardBasicTypes.STRING);
		query.addScalar("ps4Value",StandardBasicTypes.STRING);
		query.addScalar("ps5Value",StandardBasicTypes.STRING);
		//Outlet
		query.addScalar("outletFirmCode",StandardBasicTypes.INTEGER);
		query.addScalar("outletName",StandardBasicTypes.STRING);
		query.addScalar("detailAddress",StandardBasicTypes.STRING);
		query.addScalar("indoorMarketName",StandardBasicTypes.STRING);
		//Tpu
		query.addScalar("tpuCode",StandardBasicTypes.STRING);
		query.addScalar("councilDistrict",StandardBasicTypes.STRING);
		//Quotation
		query.addScalar("quotationId",StandardBasicTypes.INTEGER);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("formType",StandardBasicTypes.STRING);
		query.addScalar("cpiCompilationSeries",StandardBasicTypes.STRING);
		query.addScalar("isICP",StandardBasicTypes.BOOLEAN);
		query.addScalar("icpProductCode",StandardBasicTypes.STRING);
		query.addScalar("icpProductName",StandardBasicTypes.STRING);
		query.addScalar("icpType",StandardBasicTypes.STRING);
		query.addScalar("lastProductChangeDate",StandardBasicTypes.DATE);
		query.addScalar("lastFRAppliedDate",StandardBasicTypes.DATE);
		//Unit
		query.addScalar("unitCode",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName",StandardBasicTypes.STRING);
		query.addScalar("unitChineseName",StandardBasicTypes.STRING);
		query.addScalar("seasonality",StandardBasicTypes.INTEGER);
		query.addScalar("cpiQuotationType",StandardBasicTypes.INTEGER);
		//OutletType
		query.addScalar("outletTypeCode",StandardBasicTypes.STRING);
		query.addScalar("outletTypeEnglishName",StandardBasicTypes.STRING);
		//Purpose
		query.addScalar("purposeCode",StandardBasicTypes.STRING);
		//District
		query.addScalar("districtCode",StandardBasicTypes.STRING);
		query.addScalar("districtEnglishName",StandardBasicTypes.STRING);
		//Batch
		query.addScalar("batchCode",StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(ExportEditedPriceList.class));
		
		return query.list();
		
	}
	
	public List<ExportCPICompilationList> exportCPICompilationList(Date refMonth, Integer purposeId){
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[ExportCPICompilationReport] :refMonth, :purposeId");
		query.setParameter("refMonth", refMonth);
		query.setParameter("purposeId", purposeId);
		
		//query.addScalar("rowNum",StandardBasicTypes.INTEGER);
		query.addScalar("indoorQuotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("indoorReferenceMonth",StandardBasicTypes.DATE);
		query.addScalar("quotationId",StandardBasicTypes.INTEGER);
		query.addScalar("quotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("quotationRecordSequence",StandardBasicTypes.INTEGER);
		query.addScalar("currentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastPriceDate",StandardBasicTypes.DATE);
		query.addScalar("qsSumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsCountCurrentSPrice",StandardBasicTypes.INTEGER);
		query.addScalar("qsAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsSumLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsCountLastSPrice",StandardBasicTypes.INTEGER);
		query.addScalar("qsAverageLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsLastHasPriceSumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsLastHasPriceAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsLastHasPriceCountCurrentSPrice",StandardBasicTypes.INTEGER);
		query.addScalar("qsLastHasPriceReferenceMonth",StandardBasicTypes.DATE);
		query.addScalar("usSumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountCurrentSPrice",StandardBasicTypes.INTEGER);
		query.addScalar("usAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usLastHasPriceSumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usLastHasPriceCountCurrentSPrice",StandardBasicTypes.INTEGER);
		query.addScalar("usLastHasPriceAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usLastHasPriceReferenceMonth",StandardBasicTypes.DATE);
		query.addScalar("usSumLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountLastSPrice",StandardBasicTypes.INTEGER);
		query.addScalar("usAverageLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("imputedQuotationPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("imputedQuotationRemark",StandardBasicTypes.STRING);
		query.addScalar("imputedUnitPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("imputedUnitRemark",StandardBasicTypes.STRING);
		query.addScalar("ucDescription",StandardBasicTypes.STRING);
		query.addScalar("unitUomValue",StandardBasicTypes.DOUBLE);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("unitCode",StandardBasicTypes.STRING);
		query.addScalar("unitChineseName",StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName",StandardBasicTypes.STRING);
		query.addScalar("qCPICompilationSeries",StandardBasicTypes.STRING);
		query.addScalar("isOutlier",StandardBasicTypes.BOOLEAN);
		query.addScalar("outlierRemark",StandardBasicTypes.STRING);
		query.addScalar("referenceDate",StandardBasicTypes.DATE);
		query.addScalar("qrReferenceMonth",StandardBasicTypes.DATE);
		query.addScalar("uSeasonality",StandardBasicTypes.STRING);
		query.addScalar("uCPIQoutationType",StandardBasicTypes.STRING);
		query.addScalar("purposeName",StandardBasicTypes.STRING);
		query.addScalar("compilationMethod",StandardBasicTypes.INTEGER);
		query.addScalar("uIsFreshItem",StandardBasicTypes.BOOLEAN);
		query.addScalar("indoorRemark",StandardBasicTypes.STRING);
		query.addScalar("qOldFormBarSerial",StandardBasicTypes.STRING);
		query.addScalar("qOldFormSequence",StandardBasicTypes.STRING);
		query.addScalar("isProductChange",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewProduct",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewRecruitment",StandardBasicTypes.BOOLEAN);
		query.addScalar("productGroupId",StandardBasicTypes.INTEGER);
		query.addScalar("batchCode",StandardBasicTypes.STRING);
		query.addScalar("isCurrentPriceKeepNo",StandardBasicTypes.BOOLEAN);
		query.addScalar("copyLastPriceType",StandardBasicTypes.INTEGER);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("qIsICP",StandardBasicTypes.BOOLEAN);
		query.addScalar("icpProductCode",StandardBasicTypes.STRING);
		query.addScalar("icpProductName",StandardBasicTypes.STRING);
		query.addScalar("icpType",StandardBasicTypes.STRING);
		query.addScalar("cpiQuotationType",StandardBasicTypes.STRING);
		query.addScalar("outletCode",StandardBasicTypes.INTEGER);
		query.addScalar("productId",StandardBasicTypes.INTEGER);
		query.addScalar("usFinalPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("formType",StandardBasicTypes.STRING);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("pricingMonth",StandardBasicTypes.STRING);
		query.addScalar("unitKeepPrice",StandardBasicTypes.BOOLEAN);
		
		query.setResultTransformer(Transformers.aliasToBean(ExportCPICompilationList.class));
		
		return query.list();
	}

	public List<QuotationRecordProgress.IndoorProgress> getIndoorProgress(List<Integer> purpose, List<Integer> unitId, 
			List<Integer> cpiSurveyForm, List<Integer> batch, Date startMonth, Date endMonth, 
			List<Integer> groups, List<Integer> subgroups, List<String> cpiBasePeriods){
		String sql = "Select g.groupId as groupId, g.code as groupCode, g.chineseName as groupChineseName"
				+ ", g.englishName as groupEnglishName, sg.subGroupId as subGroupId, sg.code as subGroupCode"
				+ ", sg.chineseName as subGroupChineseName, sg.englishName as subGroupEnglishName, u.CPIBasePeriod as cpiBasePeriod"
				+ ", count(distinct total.IndoorQuotationRecordId) as indoorTotal"
				+ ", count(distinct allocation.IndoorQuotationRecordId) as allocation"
				+ ", count(distinct conversion.IndoorQuotationRecordId) as conversion"
				+ ", count(distinct review_noField.IndoorQuotationRecordId) as reviewNoField"
				+ ", count(distinct review_field.QuotationRecordId) as reviewField"
				+ ", count(distinct noField.IndoorQuotationRecordId) as noField"
				+ ", count(distinct Field.IndoorQuotationRecordId) as field"
				+ " From [Group] as g"
				+ " inner join [SubGroup] as sg on g.GroupId = sg.GroupId"
				+ " inner join [Item] as i on i.SubGroupId = sg.SubGroupId"
				+ " inner join [OutletType] as ot on ot.ItemId = i.ItemId"
				+ " inner join [SubItem] as si on si.OutletTypeId = ot.OutletTypeId"
				+ " inner join [Unit] as u on u.SubItemId = si.SubItemId"
				+ "		and u.obsoleteDate is null or cast(u.obsoleteDate as date) < cast(getDate() as date)"
				+ " 	and (u.effectiveDate is null or cast(u.effectiveDate as date) >= cast(getDate() as date))"
				+ " 	and u.[status] = 'Active'"
				+ " inner join [Quotation] as q on q.UnitId = u.UnitId"
				+ " inner join [Batch] as b on q.BatchId = b.BatchId"
				+ " left join [Purpose] as pp on u.PurposeId = pp.PurposeId"
				+ " left join [IndoorQuotationRecord] as allIndoor on q.QuotationId = allIndoor.QuotationId"
				+ " 	and allIndoor.ReferenceDate >= :startMonth and allIndoor.ReferenceDate <= :endMonth"
				+ "	left join [QuotationRecord] as allQr on allQr.QuotationId = q.QuotationId"
				+ "		and allQr.ReferenceDate >= :startMonth and allQr.ReferenceDate <= :endMonth"
				+ "		and allQr.IsBackNo = 0"
				+ " left join [IndoorQuotationRecord] as total on allIndoor.IndoorQuotationRecordId = total.IndoorQuotationRecordId"
				+ " left join [IndoorQuotationRecord] as allocation on allIndoor.IndoorQuotationRecordId = allocation.IndoorQuotationRecordId"
				+ " 	and allocation.[Status] in ('Allocation')"
				+ " left join [IndoorQuotationRecord] as conversion on allIndoor.IndoorQuotationRecordId = conversion.IndoorQuotationRecordId"
				+ " 	and conversion.[Status] in ('Conversion')"
				+ " left join [IndoorQuotationRecord] as review_noField on allIndoor.IndoorQuotationRecordId = review_noField.IndoorQuotationRecordId"
				+ " 	and review_noField.IsNoField = 1"
				+ " left join [QuotationRecord] as review_field on review_field.QuotationRecordId = allQr.QuotationRecordId"
				+ "		and review_field.[Status] = 'Approved'"
				+ " left join [IndoorQuotationRecord] as noField on allIndoor.IndoorQuotationRecordId = noField.IndoorQuotationRecordId"
				+ " 	and noField.IsNoField = 1"
				+ " left join [IndoorQuotationRecord] as Field on allIndoor.IndoorQuotationRecordId = Field.IndoorQuotationRecordId"
				+ " 	and Field.IsNoField = 0"
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
		query.addScalar("indoorTotal", StandardBasicTypes.LONG);
		query.addScalar("allocation", StandardBasicTypes.LONG);
		query.addScalar("conversion", StandardBasicTypes.LONG);
		query.addScalar("reviewNoField", StandardBasicTypes.LONG);
		query.addScalar("reviewField", StandardBasicTypes.LONG);
		query.addScalar("noField", StandardBasicTypes.LONG);
		query.addScalar("field", StandardBasicTypes.LONG);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordProgress.IndoorProgress.class));
		
		return query.list();
	}
	
	public List<QuotationRecordProgress> getQuotationRecordProgress(List<Integer> purpose, List<Integer> unitId, 
			List<Integer> cpiSurveyForm, List<Integer> batch, Date startMonth, Date endMonth, 
			List<Integer> groups, List<Integer> subgroups, List<String> cpiBasePeriods){
		String hql = "select g.groupId as groupId, g.code as groupCode, g.chineseName as groupChineseName, g.englishName as groupEnglishName, "
				+ " sg.subGroupId as subGroupId, sg.code as subGroupCode, sg.chineseName as subGroupChineseName, sg.englishName as subGroupEnglishName,"
				+ " count(distinct qr_total.quotationRecordId) as fieldTotal, "
				+ " count(distinct iqr_total.indoorQuotationRecordId) as indoorTotal, "
				+ " count(distinct unstarted.quotationRecordId) as unstarted, "
				+ " count(distinct collection_routine.quotationRecordId) as collectionNormal, "
				+ " count(distinct collection_verification.quotationRecordId) as collectionVerify, "
				+ " count(distinct collection_revisit.quotationRecordId) as collectionRevisit, "
				+ " count(distinct allocation.indoorQuotationRecordId) as allocation, "
				+ " count(distinct conversion.indoorQuotationRecordId) as conversion, "
				+ " count(distinct review_field.quotationRecordId) as reviewField, "
				+ " count(distinct review_noField.indoorQuotationRecordId) as reviewNoField "
				
				+ "from Group as g "
				+ " inner join  g.subGroups as sg "
				+ " inner join sg.items as i "
				+ " inner join  i.outletTypes as ot "
				+ " inner join ot.subItems as si "
				+ " inner join si.units as u "
				+ "   on ( u.obsoleteDate is null or cast(u.obsoleteDate as date) < cast(getDate() as date)) and "
				+ "   (u.effectiveDate is null or cast(u.effectiveDate as date) >= cast(getDate() as date)) and u.status = 'Active' "
				+ " inner join u.quotations as q "
				+ " inner join q.batch as b "
				
				+ " left join q.quotationRecords as qr_all_total "
				+ "   on qr_all_total.referenceDate >= :startMonth and qr_all_total.referenceDate <= :endMonth "
				+ " left join q.indoorQuotationRecords as iqr_all_total "
				+ "   on iqr_all_total.referenceDate >= :startMonth and iqr_all_total.referenceDate <= :endMonth "
				
				+ " left join q.quotationRecords as qr_total "
				+ "   on qr_total = qr_all_total "
				+ " left join q.indoorQuotationRecords as iqr_total "
				+ "   on iqr_total = iqr_all_total and iqr_total.quotationRecord.quotationRecordId is null "
				
				+ " left join q.quotationRecords as unstarted "
				+ "   on unstarted.status = 'Blank' and unstarted.quotationState = 'Normal' "
				+ "   and ( unstarted.assignedStartDate > getDate() or unstarted.assignedCollectionDate > getDate() ) "
				+ "   and unstarted = qr_all_total "
				+ " left join q.quotationRecords as collection_routine "
				+ "   on collection_routine.status in ('Draft', 'Rejected', 'Submitted') and collection_routine.quotationState = 'Normal' "
				+ "   and ( collection_routine.assignedStartDate <= getDate() or collection_routine.assignedCollectionDate <= getDate() ) "
				+ "   and collection_routine = qr_all_total"
				+ " left join q.quotationRecords as collection_verification "
				+ "   on collection_verification.status in ('Draft', 'Rejected', 'Submitted') and collection_verification.quotationState = 'Verify' "
				+ "   and ( collection_verification.assignedStartDate <= getDate() or collection_verification.assignedCollectionDate <= getDate() ) "
				+ "   and collection_verification = qr_all_total "
				+ " left join q.quotationRecords as collection_revisit "
				+ "   on collection_revisit.status in ('Draft', 'Rejected', 'Submitted') and collection_revisit.quotationState = 'Revisit' "
				+ "   and ( collection_revisit.assignedStartDate <= getDate() or collection_revisit.assignedCollectionDate <= getDate() ) "
				+ "   and collection_revisit = qr_all_total "
				
				+ " left join q.indoorQuotationRecords as allocation "
				+ "   on allocation.status in ('Allocation') and allocation = iqr_all_total "
				+ " left join q.indoorQuotationRecords as conversion "
				+ "   on conversion.status in ('Conversion') and conversion = iqr_all_total "
				
				+ " left join q.indoorQuotationRecords as review_noField "
				+ "   on review_noField.status in ('Complete') and review_noField.quotationRecord.quotationRecordId is null and review_noField = iqr_all_total "
				+ " left join iqr_all_total.quotationRecord as review_field "
				+ "   on iqr_all_total.status in ('Complete') "
				
				+ " where 1=1 ";
		
		if (purpose != null && purpose.size() > 0){
			hql += " and p.purposeId in (:purpose) ";
		}
		
		if (unitId != null && unitId.size() > 0){
			hql += " and u.unitId in (:unitId) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			hql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		if (batch != null && batch.size() > 0){
			hql += " and b.batchId in (:batch) ";
		}
		
		if (groups != null && groups.size() > 0){
			hql += " and g.groupId in (:groups) ";
		}
		
		if (subgroups != null && subgroups.size() > 0){
			hql += " and sg.subGroupId in (:subgroups) ";
		}
		
		if (cpiBasePeriods != null && cpiBasePeriods.size() > 0){
			hql += " and u.cpiBasePeriod in (:cpiBasePeriods) ";
		}
		
		hql += " group by g.groupId, g.code, g.chineseName, g.englishName, sg.subGroupId, sg.code, sg.chineseName, sg.englishName ";
		
		Query query = this.getSession().createQuery(hql);
		
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
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordProgress.class));
		
		return query.list();
	}
	
	public List<IndoorQuotationRecord> getByDateRangeAndQuotationWithSPrice(Date from, Date to, List<Quotation> quotations){
		Criteria criteria = this.createCriteria("iqr");
		criteria.add(Restrictions.between("iqr.referenceMonth", from, to));
		criteria.add(Restrictions.in("iqr.quotation", quotations));
		criteria.add(Restrictions.isNotNull("iqr.currentSPrice"));
		criteria.add(Restrictions.isNotNull("iqr.previousNPrice"));
		return criteria.list();
	}
	
	public List<MatchedPEQuotationRecordResult> getMatchedPEQuotationRecordResult(Date from, Date to, 
			List<Integer> itemIds, String comparator, Double settingValue, Collection<String> excludedOutletTypes){

		String sql = "select iqr.quotationId, case when qr.outletId is null then q.outletId else qr.outletId end as outletId, "
				+ " sum( "
				+ " case when iqr.previousSPrice is not null and iqr.previousSPrice > 0 "
				+ " 	then "
				+ "			case when (iqr.currentSPrice / iqr.previousSPrice * 100) " + comparator + " :settingValue "
				+ " 			then 1 else 0 end "
				+ "		else 0 end "
				+ ") as matched, "
				+ " count(distinct iqr.indoorQuotationRecordId) as total "
				+ " from IndoorQuotationRecord as iqr "
				+ " inner join Quotation as q "
				+ " 	on q.QuotationId = iqr.QuotationId "
				+ " inner join [Unit] as u "
				+ "  	on u.UnitId = q.UnitId "
				+ " inner join SubItem as si "
				+ " 	on u.SubItemId = si.SubItemId "
				+ " inner join OutletType as ot "
				+ " 	on si.OutletTypeId = ot.OutletTypeId "
				+ " left join QuotationRecord as qr "
				+ "		on qr.QuotationRecordId = iqr.QuotationRecordId "
				+ " where ot.itemId in (:items) and iqr.referenceMonth between :from and :to ";
		if (excludedOutletTypes != null && excludedOutletTypes.size() > 0){
			sql += " and NOT(RIGHT(ot.Code,3) in (:excludedOutletTypes)) ";
		}	
				
		sql += " group by iqr.quotationId, case when qr.outletId is null then q.outletId else qr.outletId end ";
				//+ " and (iqr.currentSPrice / iqr.previousSPrice * 100) " + comparator + " :settingValue ";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameterList("items", itemIds);
		query.setParameter("from", from);
		query.setParameter("to", to);
		query.setParameter("settingValue", settingValue);
		if (excludedOutletTypes != null && excludedOutletTypes.size() > 0){
			query.setParameterList("excludedOutletTypes", excludedOutletTypes);
		}
		
		
		query.addScalar("matched", StandardBasicTypes.LONG);
		query.addScalar("total", StandardBasicTypes.LONG);
		
		query.addScalar("outletId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		
		//		String sql = "select iqr.quotationId, iqr.indoorQuotationRecordId, u.unitId, "
//				+ " case when (iqr.currentSPrice / iqr.previousSPrice * 100) " + comparator + " :settingValue then 1 else 0 end as matched "
//				+ " from IndoorQuotationRecord as iqr"
//				+ " inner join Quotation as q "
//				+ " 	on q.QuotationId = iqr.QuotationId "
//				+ " inner join [Unit] as u "
//				+ "  	on u.UnitId = q.UnitId "
//				+ " inner join SubItem as si "
//				+ " 	on si.SubItemId = si.SubItemId "
//				+ " inner join OutletType as ot "
//				+ " 	on si.OutletTypeId = ot.OutletTypeId  "
//				+ " where ot.itemId in (:items) and iqr.referenceMonth between :from and :to ";
//				//+ " and (iqr.currentSPrice / iqr.previousSPrice * 100) " + comparator + " :settingValue ";
//		SQLQuery query = this.getSession().createSQLQuery(sql);
//		
//		query.setParameterList("items", itemIds);
//		query.setParameter("from", from);
//		query.setParameter("to", to);
//		query.setParameter("settingValue", settingValue);
		
		query.setResultTransformer(Transformers.aliasToBean(MatchedPEQuotationRecordResult.class));
		
		return query.list();
		
	}
	
	public Long countIndoorQuotationRecord(Date referenceMonth, Integer purposeId, String[] status, String notEqStatus, Integer userId){
		String hql = "select count(distinct iqr.indoorQuotationRecordId)"
				+ " from IndoorQuotationRecord iqr"
				+ " left join iqr.quotation q "
				+ " left join q.unit u "
				+ " left join u.purpose as pu "
				+ " where iqr.referenceMonth = :referenceMonth "
				+ " and pu.purposeId = :purposeId ";
		if (userId != null){
			hql += " and iqr.user.userId = :userId";
		}
				
		if(status != null && status.length > 0){
			hql = hql + " and iqr.status in (:status)";
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			hql = hql + " and iqr.status != :notEqStatus";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);
		
		if (userId != null){
			query.setParameter("userId", userId);
		}
		if(status != null&& status.length > 0){
			query.setParameterList("status", status);
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			query.setParameter("notEqStatus", notEqStatus);
		}
		
		return (Long) query.uniqueResult();
	}
	
	public long countIndoorQuotationRecordOutlet(Date referenceMonth, Integer purposeId, String[] status, String notEqStatus, Integer userId){
		String hql = "select count(distinct o.outletId)"
				+ " from IndoorQuotationRecord iqr"
				+ " left join iqr.quotation q"
				+ " left join q.outlet o"
				+ " left join q.unit u"
				+ " left join u.purpose pu"
				+ " where iqr.referenceMonth = :referenceMonth";
		if (userId != null)
			hql += " and iqr.user.userId = :userId";
		if(status != null && status.length > 0){
			hql = hql + " and iqr.status in (:status)";
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			hql = hql + " and iqr.status != :notEqStatus";
		}
		hql = hql + " and pu.purposeId = :purposeId";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);
		if (userId != null)
			query.setParameter("userId", userId);
		if(status != null&& status.length > 0){
			query.setParameterList("status", status);
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			query.setParameter("notEqStatus", notEqStatus);
		}
		
		return (Long) query.uniqueResult();
	}
	
	public List<QuotationRecordDataConversionTableListModel> getIndoorQuotationRecordTableList(String search,
			Integer userId, String[] indoorQuotationRecordStatus, Integer purposeId, Date referenceMonth, Integer subGroupId, List<Integer> unitId, Integer outletId,
			List<String> outletTypeShortCode, Integer seasonalItem, String outletCategory, Boolean subPrice,
			Boolean outletCategoryRemark, Boolean priceRemark, String surveyForm, 
			Boolean allocatedIndoorOfficer, Integer indoorOfficer, Double greaterThan, Double lessThan, Double equal,
			Boolean withPriceReason, Boolean withOtherRemark, Boolean withProductRemark, Boolean withDiscountRemark,
			Boolean withFieldwork, Boolean isPRNull, Boolean withIndoorConversionRemarks, String referenceDateCrit,
			Integer firmStatus, Integer availability, Boolean withDiscount, Integer quotationId,
			Integer firstRecord, Integer displayLength, Order order){
		
		// qr.reason : priceRemarks -> fieldPriceReason
		// qr.remark : otherRemarks -> fieldPriceRemark
		String referenceDate = String.format("FORMAT(iqr.ReferenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = 
			"select iqr.IndoorQuotationRecordId as indoorQuotationRecordId,"
				+ " iqr.IsFlag as isFlag,"
				+ " case when iqr.referenceDate is not null then " + referenceDate + " else '' end as referenceDate,"
				+ " sg.ChineseName as subGroupChineseName, sg.EnglishName as subGroupEnglishName,"
				+ " u.ChineseName as unitChineseName, u.EnglishName as unitEnglishName,"
				+ " substring(ot.Code, len(ot.Code)-2, 3) as outletType,"
				+ " o.Name as outletName, "
				+ " concat(pa.SpecificationName,'=',ps.Value) as productAttribute1,"
				+ " case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end as subPrice,"
				+ "	(case when u.Seasonality = 1 then 'All-time' else"
				+ "		 case when u.Seasonality = 2 then 'Summer' else"
				+ "			 case when u.Seasonality = 3 then 'Winter' else "
				+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
				+ "			 end"
				+ "		 end"
				+ " end) as seasonalItem,"
				//+ " iqr.Status as quotationRecordStatus,"
				+ " qr.Reason as priceRemarks,"
				+ " qr.ProductRemark as productRemarks,"
				+ " qr.Remark as otherRemarks, "
				+ " q.QuotationId as quotationId, "
				+ " iqr.remark as indoorRemark, "
				+ " iqr.CurrentSPrice as editedCurrentSPrice, "
				+ " iqr.PreviousSPrice as editedPreviousSPrice, "
				+ " case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null "
				+ " else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice) * 100, 3) as decimal(10, 3)) end as pr, "
				+ " a.OutletDiscountRemark as outletDiscountRemark, "
				+ " qr.DiscountRemark as discountRemark, "
				+ " u.UnitCategory as unitCategory "
			+ " from IndoorQuotationRecord iqr "
				+ "	left outer join QuotationRecord qr on iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "	left outer join Assignment a on a.AssignmentId = qr.AssignmentId "
				+ " left outer join Quotation q on iqr.QuotationId = q.QuotationId "
				+ " left outer join Batch b on q.BatchId = b.BatchId "
				+ " left outer join Product p on q.ProductId = p.ProductId "
				+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
				//+ " left outer join Outlet o on q.OutletId = o.OutletId "
				//B176 - The Outlet name should be same as Quotation Record, not Quotation
				+ " left outer join Outlet o on qr.OutletId = o.OutletId "				
				+ " left outer join Unit u on q.UnitId = u.UnitId "
				+ " left outer join SubItem si on u.SubItemId = si.SubItemId "
				+ " left outer join OutletType ot on si.OutletTypeId = ot.OutletTypeId "
				+ " left outer join Item i on ot.ItemId = i.ItemId "
				+ " left outer join SubGroup sg on i.SubGroupId = sg.SubGroupId "
				+ " left outer join SubPriceRecord spr on qr.QuotationRecordId = spr.QuotationRecordId "
				+ " left outer join Purpose pu on u.PurposeId = pu.PurposeId "
				+ " left outer join [User] as user_ on iqr.UserId = user_.UserId "
				+ " left outer join AssignmentUnitCategoryInfo as auc on a.AssignmentId = auc.AssignmentId "
				+ "  and u.UnitCategory = auc.UnitCategory "
			+ " where 1=1"
			+ " and iqr.ReferenceMonth = :referenceMonth"
			+ " and pu.PurposeId = :purposeId"
			+ " and iqr.Status in (:status)";
		
		if (!StringUtils.isEmpty(search)){
			sql +=" and ("
				+ " sg.ChineseName like :search "
				+ " or sg.EnglishName like :search "
				+ " or u.ChineseName like :search "
				+ " or u.EnglishName like :search " 
				+ " or substring(ot.Code, len(ot.Code)-2, 3) like :search " 
				+ " or o.Name like :search " 
				//+ " or concat(pfs.specificationName,'=',pfs.value) like :search "
				+ " or pa.SpecificationName like :search or ps.Value like :search "
				+ " or (case when u.Seasonality = 1 then 'All-time' else"
				+ "		 case when u.Seasonality = 2 then 'Summer' else"
				+ "			 case when u.Seasonality = 3 then 'Winter' else "
				+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
				+ "			 end"
				+ "		 end"
				+ " end) like :search " 
//				+ " or iqr.Status like :search " 
				+ " or qr.Reason like :search " 
				+ " or qr.ProductRemark like :search " 
				+ " or qr.Remark like :search "
				+ " or str(iqr.IndoorQuotationRecordId) like :search "
				+ " or str(q.QuotationId) like :search " 
				+ " or iqr.remark like :search " 
				+ " or a.OutletDiscountRemark like :search "
				+ " or qr.DiscountRemark like :search "
				+ " or u.UnitCategory like :search "
				+ " or " + referenceDate + " like :search "
				+ " ) ";
		}
		
		if (userId != null)
			sql += " and user_.UserId = :userId";
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypeShortCode) ";
		}
		
		if (subGroupId != null){
			sql += " and sg.SubGroupId = :subGroupId ";
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			sql += " and u.UnitCategory = :outletCategory ";
		}
		
		if(outletId != null){
			sql += " and o.OutletId = :outletId ";
		}
		
		if(unitId != null && unitId.size() > 0){
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if(seasonalItem != null){
			sql += " and u.Seasonality = :seasonalItem ";
		}
		
		if(priceRemark != null && priceRemark){
			sql += " and (qr.Remark is not null and qr.Remark != '' )";
		}else if(priceRemark != null && !priceRemark){
			sql += " and (qr.Remark is null or qr.Remark = '' )";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			sql += " and b.SurveyForm = :surveyForm ";
		}
		
		if(outletCategoryRemark != null && outletCategoryRemark){
			sql += " and (qr.CategoryRemark is not null and qr.CategoryRemark != '' )";
		}else if(outletCategoryRemark != null && !outletCategoryRemark){
			sql += " and (qr.CategoryRemark is null or qr.CategoryRemark = '' )";
		}
		
		if(withPriceReason != null){
			if(withPriceReason){
				sql += " and qr.Reason is not null and qr.Reason <> '' ";
			}else{
				sql += " and (qr.Reason is null or qr.Reason = '') ";
			}
		}
		
		if(withProductRemark != null){
			if(withProductRemark){
				sql += " and qr.ProductRemark is not null and qr.ProductRemark <> '' ";
			}else{
				sql += " and (qr.ProductRemark is null or qr.ProductRemark = '') ";
			}
		}
		
		if(withDiscountRemark != null){
			if(withDiscountRemark){
				sql += " and qr.DiscountRemark is not null and qr.DiscountRemark <> '' ";
			}else{
				sql += " and (qr.DiscountRemark is null or qr.DiscountRemark = '') ";
			}
		}
		
		if(withOtherRemark != null){
			if(withOtherRemark){
				sql += " and a.OutletDiscountRemark is not null and a.OutletDiscountRemark <> '' ";
			}else{
				sql += " and (a.OutletDiscountRemark is null or a.OutletDiscountRemark = '') ";
			}
		}		
		
		if(withFieldwork != null){
			if(withFieldwork){
				sql += " and (iqr.isNoField is null or iqr.isNoField = 0) ";
			}else{
				sql += " and iqr.isNoField = 1 ";
			}
		}
		
		if(isPRNull != null){
			sql += " and (case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null  "
					+ "else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice ) * 100, 3) as decimal(10, 3)) end) ";
			if(isPRNull){
				sql += " is null ";
			}else{
				sql += " is not null ";
			}
		}
		
		if(withIndoorConversionRemarks != null){
			if(withIndoorConversionRemarks){
				sql += " and iqr.remark is not null and iqr.remark <> '' ";
			}else{
				sql += " and (iqr.remark is null or iqr.remark = '') ";
			}
		}
		
		if(greaterThan != null || lessThan != null || equal != null){
			List<String> params = new ArrayList<String>();
			String calculatePercent = "round(iqr.CurrentSPrice / "
					+ "case when iqr.PreviousSPrice <= 0 then null else iqr.PreviousSPrice end"
					+ " * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(greaterThan != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(lessThan != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(equal != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			sql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and ("+StringUtils.join(params.toArray(), " or ")+")";
		}
		

		if(allocatedIndoorOfficer != null){
			if(allocatedIndoorOfficer && indoorOfficer !=null){
				sql += " and user_.UserId is not null and user_.UserId = :indoorOfficer ";
			}else{
				sql += " and user_.UserId is null";
			}
		}
		
		if (StringUtils.isNotBlank(referenceDateCrit)) {
			sql += " and ( "
					+ " (" + referenceDate + " = :referenceDateCrit) "
					+ " or (iqr.referenceDate is null and " + referenceDate + " = :referenceDateCrit) "
					+ " ) ";
		}
		
		if (firmStatus != null){
			sql += " and qr.FirmStatus = :firmStatus ";
		}
		
		if (availability != null){
			sql += " and (case when qr.availability = 3 and qr.SPrice is null and qr.NPrice is null and qr.IsSPricePeculiar = 0 then 4 else "
					+ "case when qr.availability = 3 then 1 else "
					+ "case when qr.availability <> 3 then qr.availability end end end) = :availability ";
		}
		
		if(withDiscount != null){
			if(withDiscount){
				sql += " and qr.discount is not null and qr.discount <> '' ";
			}else{
				sql += " and (qr.discount is null or qr.discount = '') ";
			}
		}
		
		if(quotationId != null){
			sql += " and iqr.QuotationId = :quotationId ";
		}
		
			sql += " group by iqr.IndoorQuotationRecordId, iqr.IsFlag, iqr.referenceDate, sg.ChineseName, sg.EnglishName,"
				+ " u.ChineseName, u.EnglishName, o.Name, concat(pa.SpecificationName,'=',ps.Value),"
//				+ " iqr.Status, qr.Reason, qr.Remark, qr.ProductRemark,"
				+ " qr.Reason, qr.Remark, qr.ProductRemark,"
				+ " substring(ot.Code, len(ot.Code)-2, 3), u.Seasonality, "
				+ " q.QuotationId, iqr.remark, "
				+ " iqr.CurrentSPrice, iqr.PreviousSPrice, a.OutletDiscountRemark, qr.DiscountRemark, u.UnitCategory ";
			
		if(subPrice != null){
			sql += " having (case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end) = :subPrice ";
		}
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);		
		query.setParameterList("status", indoorQuotationRecordStatus);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			query.setParameterList("outletTypeShortCode", outletTypeShortCode);
		}
		
		if (subGroupId != null){
			query.setParameter("subGroupId", subGroupId);
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			query.setParameter("outletCategory", outletCategory);
		}
		
		if (outletId != null ){
			query.setParameter("outletId", outletId);
		}
		
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		
		if (seasonalItem != null ){
			query.setParameter("seasonalItem", seasonalItem);
		}
		
		if (subPrice != null ){
			query.setParameter("subPrice", subPrice);
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}
		
		if(greaterThan!= null){
			query.setParameter("greaterThan", greaterThan);
		}
		
		if(lessThan != null){
			query.setParameter("lessThan", lessThan);
		}
		
		if(equal != null){
			query.setParameter("equal", equal);
		}
		
		if(allocatedIndoorOfficer != null && allocatedIndoorOfficer){
			if(indoorOfficer != null){
				query.setParameter("indoorOfficer", indoorOfficer);
			}
		}
		
		if (StringUtils.isNotEmpty(referenceDateCrit)){
			query.setParameter("referenceDateCrit", referenceDateCrit);
		}
		
		if (firmStatus != null){
			query.setParameter("firmStatus", firmStatus);
		}
		
		if (availability != null){
			query.setParameter("availability", availability);			
		}
				
		if (quotationId != null){
			query.setParameter("quotationId", quotationId);
		}
		
		if (firstRecord != null){
			query.setFirstResult(firstRecord);			
		}
		
		if (displayLength != null){
			query.setMaxResults(displayLength);			
		}
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordDataConversionTableListModel.class));
		
		query.addScalar("indoorQuotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		query.addScalar("isFlag", StandardBasicTypes.BOOLEAN);
		query.addScalar("subGroupEnglishName", StandardBasicTypes.STRING);
		query.addScalar("subGroupChineseName", StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName", StandardBasicTypes.STRING);
		query.addScalar("unitChineseName", StandardBasicTypes.STRING);
		query.addScalar("outletType", StandardBasicTypes.STRING);
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("productAttribute1", StandardBasicTypes.STRING);
		query.addScalar("subPrice", StandardBasicTypes.BOOLEAN);
		query.addScalar("seasonalItem", StandardBasicTypes.STRING);
//		query.addScalar("quotationRecordStatus", StandardBasicTypes.STRING);
		query.addScalar("priceRemarks", StandardBasicTypes.STRING);
		query.addScalar("productRemarks", StandardBasicTypes.STRING);
		query.addScalar("otherRemarks", StandardBasicTypes.STRING);
		query.addScalar("indoorRemark", StandardBasicTypes.STRING);
		
		query.addScalar("editedCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("editedPreviousSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("pr", StandardBasicTypes.DOUBLE);
		query.addScalar("outletDiscountRemark", StandardBasicTypes.STRING);
		query.addScalar("discountRemark", StandardBasicTypes.STRING);
		query.addScalar("unitCategory", StandardBasicTypes.STRING);
		query.addScalar("referenceDate", StandardBasicTypes.STRING);

		return query.list();
	}
	
	public List<QuotationRecordDataConversionTableListModel> getIndoorQuotationRecordDataConversionTableList(String search,
			Integer userId, String[] indoorQuotationRecordStatus, Integer purposeId, Date referenceMonth, Integer subGroupId, List<Integer> unitId, Integer outletId,
			List<String> outletTypeShortCode, Integer seasonalItem, String outletCategory, Boolean subPrice,
			Boolean outletCategoryRemark, Boolean priceRemark, String surveyForm, 
			Boolean allocatedIndoorOfficer, Integer indoorOfficer, Double greaterThan, Double lessThan, Double equal,
			Boolean withPriceReason, Boolean withOtherRemark, Boolean withProductRemark, Boolean withDiscountRemark,
			Boolean withFieldwork, Boolean isPRNull, Integer firmStatus, Integer availability, Integer quotationId,
			Integer firstRecord, Integer displayLength, Order order){
		
		// qr.reason : priceRemarks -> fieldPriceReason
		// qr.remark : otherRemarks -> fieldPriceRemark
		
		String referenceDate = String.format("FORMAT(iqr.ReferenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = 
			"select iqr.IndoorQuotationRecordId as indoorQuotationRecordId,"
				+ " iqr.IsFlag as isFlag,"
				+ " case when iqr.ReferenceDate is not null then " + referenceDate + " else '' end as referenceDate,"
				+ " sg.ChineseName as subGroupChineseName, sg.EnglishName as subGroupEnglishName,"
				+ " u.ChineseName as unitChineseName, u.EnglishName as unitEnglishName,"
				+ " substring(ot.Code, len(ot.Code)-2, 3) as outletType,"
				+ " o.Name as outletName, "
				+ " concat(pa.SpecificationName,'=',ps.Value) as productAttribute1,"
				+ " case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end as subPrice,"
				+ "	(case when u.Seasonality = 1 then 'All-time' else"
				+ "		 case when u.Seasonality = 2 then 'Summer' else"
				+ "			 case when u.Seasonality = 3 then 'Winter' else "
				+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
				+ "			 end"
				+ "		 end"
				+ " end) as seasonalItem,"
				+ " iqr.Status as quotationRecordStatus,"
				+ " qr.Reason as priceRemarks,"
				+ " qr.ProductRemark as productRemarks,"
				+ " qr.Remark as otherRemarks, "
				+ " q.QuotationId as quotationId, "
				+ " iqr.remark as indoorRemark, "
				+ " iqr.CurrentSPrice as editedCurrentSPrice, "
				+ " iqr.PreviousSPrice as editedPreviousSPrice, "
				+ " case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null "
				+ " else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice ) * 100, 3) as decimal(10, 3)) end as pr, "
				+ " a.OutletDiscountRemark as outletDiscountRemark, "
				+ " qr.DiscountRemark as discountRemark, "
				+ " u.UnitCategory as unitCategory "
			+ " from IndoorQuotationRecord iqr "
				+ "	left outer join QuotationRecord qr on iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "	left outer join Assignment a on a.AssignmentId = qr.AssignmentId "
				+ " left outer join Quotation q on iqr.QuotationId = q.QuotationId "
				+ " left outer join Batch b on q.BatchId = b.BatchId "
				+ " left outer join Product p on q.ProductId = p.ProductId "
				+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
				+ " left outer join Outlet o on q.OutletId = o.OutletId "
				+ " left outer join Unit u on q.UnitId = u.UnitId "
				+ " left outer join SubItem si on u.SubItemId = si.SubItemId "
				+ " left outer join OutletType ot on si.OutletTypeId = ot.OutletTypeId "
				+ " left outer join Item i on ot.ItemId = i.ItemId "
				+ " left outer join SubGroup sg on i.SubGroupId = sg.SubGroupId "
				+ " left outer join SubPriceRecord spr on qr.QuotationRecordId = spr.QuotationRecordId "
				+ " left outer join Purpose pu on u.PurposeId = pu.PurposeId "
				+ " left outer join [User] as user_ on iqr.UserId = user_.UserId "
				+ " left outer join AssignmentUnitCategoryInfo as auc on a.AssignmentId = auc.AssignmentId "
				+ "  and u.UnitCategory = auc.UnitCategory "
			+ " where 1=1"
			+ " and iqr.ReferenceMonth = :referenceMonth"
			+ " and pu.PurposeId = :purposeId"
			+ " and iqr.Status in (:status)";
		
		if (!StringUtils.isEmpty(search)){
			sql +=" and ("
				+ " sg.ChineseName like :search "
				+ " or sg.EnglishName like :search "
				+ " or u.ChineseName like :search "
				+ " or u.EnglishName like :search " 
				+ " or substring(ot.Code, len(ot.Code)-2, 3) like :search " 
				+ " or o.Name like :search " 
				//+ " or concat(pfs.specificationName,'=',pfs.value) like :search "
				+ " or pa.SpecificationName like :search or ps.Value like :search "
				+ " or (case when u.Seasonality = 1 then 'All-time' else"
				+ "		 case when u.Seasonality = 2 then 'Summer' else"
				+ "			 case when u.Seasonality = 3 then 'Winter' else "
				+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
				+ "			 end"
				+ "		 end"
				+ " end) like :search " 
				+ " or " + referenceDate + " like :search "
				+ " or iqr.Status like :search " 
				+ " or qr.Reason like :search " 
				+ " or qr.ProductRemark like :search " 
				+ " or qr.Remark like :search "
				+ " or str(iqr.IndoorQuotationRecordId) like :search "
				+ " or str(q.QuotationId) like :search " 
				+ " or iqr.remark like :search " 
				+ " or a.OutletDiscountRemark like :search "
				+ " or qr.DiscountRemark like :search "
				+ " or u.UnitCategory like :search "
				+ " ) ";
		}
		
		if (userId != null)
			sql += " and user_.UserId = :userId";
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypeShortCode) ";
		}
		
		if (subGroupId != null){
			sql += " and sg.SubGroupId = :subGroupId ";
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			sql += " and u.UnitCategory = :outletCategory ";
		}
		
		if(outletId != null){
			sql += " and o.OutletId = :outletId ";
		}
		
		if(unitId != null && unitId.size() > 0){
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if(seasonalItem != null){
			sql += " and  u.Seasonality  = :seasonalItem ";
		}
		
		if(priceRemark != null && priceRemark){
			sql += " and (qr.Remark is not null and qr.Remark != '' )";
		}else if(priceRemark != null && !priceRemark){
			sql += " and (qr.Remark is null or qr.Remark = '' )";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			sql += " and b.SurveyForm = :surveyForm ";
		}
		
		if(outletCategoryRemark != null && outletCategoryRemark){
			sql += " and (qr.CategoryRemark is not null and qr.CategoryRemark != '' )";
		}else if(outletCategoryRemark != null && !outletCategoryRemark){
			sql += " and (qr.CategoryRemark is null or qr.CategoryRemark = '' )";
		}
		
		if(withPriceReason != null){
			if(withPriceReason){
				sql += " and qr.Reason is not null and qr.Reason <> '' ";
			}else{
				sql += " and (qr.Reason is null or qr.Reason = '') ";
			}
		}
		
		if(withProductRemark != null){
			if(withProductRemark){
				sql += " and qr.ProductRemark is not null and qr.ProductRemark <> '' ";
			}else{
				sql += " and (qr.ProductRemark is null or qr.ProductRemark = '') ";
			}
		}
		
		if(withDiscountRemark != null){
			if(withDiscountRemark){
				sql += " and qr.DiscountRemark is not null and qr.DiscountRemark <> '' ";
			}else{
				sql += " and (qr.DiscountRemark is null or qr.DiscountRemark = '') ";
			}
		}
		
		if(withOtherRemark != null){
			if(withOtherRemark){
				sql += " and a.OutletDiscountRemark is not null and a.OutletDiscountRemark <> '' ";
			}else{
				sql += " and (a.OutletDiscountRemark is null or a.OutletDiscountRemark = '') ";
			}
		}		
		
		if(withFieldwork != null){
			if(withFieldwork){
				sql += " and (iqr.isNoField is null or iqr.isNoField = 0) ";
			}else{
				sql += " and iqr.isNoField = 1 ";
			}
		}
		
		if(greaterThan != null || lessThan != null || equal != null){
			List<String> params = new ArrayList<String>();
			String calculatePercent = "round((iqr.currentSPrice / "
					+ " case when iqr.previousSPrice <= 0 then null else iqr.previousSPrice end"
					+ " ) * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(greaterThan != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(lessThan != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(equal != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			sql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and ("+StringUtils.join(params.toArray(), " or ")+")";
		}
		

		if(allocatedIndoorOfficer != null){
			if(allocatedIndoorOfficer && indoorOfficer !=null){
				sql += " and user_.UserId is not null and user_.UserId = :indoorOfficer ";
			}else{
				sql += " and user_.UserId is null";
			}
		}
		
		if(isPRNull != null){
			sql += " and (case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null  "
					+ "else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice ) * 100, 3) as decimal(10, 3)) end) ";
			if(isPRNull){
				sql += " is null ";
			}else{
				sql += " is not null ";
			}
		}
		
		if (firmStatus != null){
			sql += " and qr.FirmStatus = :firmStatus ";
		}
		
		if (availability != null){
			sql += " and (case when qr.availability = 3 and qr.SPrice is null and qr.NPrice is null and qr.IsSPricePeculiar = 0 then 4 else "
					+ "case when qr.availability = 3 then 1 else "
					+ "case when qr.availability <> 3 then qr.availability end end end) = :availability ";
		}
		
		if (quotationId != null){
			sql += " and iqr.QuotationId = :quotationId ";
		}
		
		//*
			sql += " group by iqr.IndoorQuotationRecordId, iqr.IsFlag, iqr.ReferenceDate, sg.ChineseName, sg.EnglishName,"
				+ " u.ChineseName, u.EnglishName, o.Name, concat(pa.SpecificationName,'=',ps.Value),"
				+ " iqr.Status, "
				+ " qr.Reason, qr.Remark, qr.ProductRemark,"
				+ " substring(ot.Code, len(ot.Code)-2, 3), u.Seasonality, "
				+ " q.QuotationId, iqr.remark, "
				+ " iqr.CurrentSPrice, iqr.PreviousSPrice, a.OutletDiscountRemark, qr.DiscountRemark, u.UnitCategory ";
		//*/
			
		if(subPrice != null){
			sql += " having (case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end) = :subPrice ";
		}
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		
		if (!"indoorQuotationRecordId".equals(order.getPropertyName())){
			sql += ", iqr.indoorQuotationRecordId asc";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);		
		query.setParameterList("status", indoorQuotationRecordStatus);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			query.setParameterList("outletTypeShortCode", outletTypeShortCode);
		}
		
		if (subGroupId != null){
			query.setParameter("subGroupId", subGroupId);
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			query.setParameter("outletCategory", outletCategory);
		}
		
		if (outletId != null ){
			query.setParameter("outletId", outletId);
		}
		
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		
		if (seasonalItem != null ){
			query.setParameter("seasonalItem", seasonalItem);
		}
		
		if (subPrice != null ){
			query.setParameter("subPrice", subPrice);
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}
		
		if(greaterThan!= null){
			query.setParameter("greaterThan", greaterThan);
		}
		
		if(lessThan != null){
			query.setParameter("lessThan", lessThan);
		}
		
		if(equal != null){
			query.setParameter("equal", equal);
		}
		
		if(allocatedIndoorOfficer != null && allocatedIndoorOfficer){
			if(indoorOfficer != null){
				query.setParameter("indoorOfficer", indoorOfficer);
			}
		}
		
		if (firstRecord != null){
			query.setFirstResult(firstRecord);			
		}
		
		if (displayLength != null){
			query.setMaxResults(displayLength);			
		}
		
		if (firmStatus != null){
			query.setParameter("firmStatus", firmStatus);
		}
		
		if (availability != null){
			query.setParameter("availability", availability);			
		}
		
		if (quotationId != null){
			query.setParameter("quotationId", quotationId);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordDataConversionTableListModel.class));
		
		query.addScalar("indoorQuotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		query.addScalar("isFlag", StandardBasicTypes.BOOLEAN);
		query.addScalar("subGroupEnglishName", StandardBasicTypes.STRING);
		query.addScalar("subGroupChineseName", StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName", StandardBasicTypes.STRING);
		query.addScalar("unitChineseName", StandardBasicTypes.STRING);
		query.addScalar("outletType", StandardBasicTypes.STRING);
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("productAttribute1", StandardBasicTypes.STRING);
		query.addScalar("subPrice", StandardBasicTypes.BOOLEAN);
		query.addScalar("seasonalItem", StandardBasicTypes.STRING);
		query.addScalar("quotationRecordStatus", StandardBasicTypes.STRING);
		query.addScalar("priceRemarks", StandardBasicTypes.STRING);
		query.addScalar("productRemarks", StandardBasicTypes.STRING);
		query.addScalar("otherRemarks", StandardBasicTypes.STRING);
		query.addScalar("indoorRemark", StandardBasicTypes.STRING);
		
		query.addScalar("editedCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("editedPreviousSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("pr", StandardBasicTypes.DOUBLE);
		query.addScalar("outletDiscountRemark", StandardBasicTypes.STRING);
		query.addScalar("discountRemark", StandardBasicTypes.STRING);
		query.addScalar("unitCategory", StandardBasicTypes.STRING);
		query.addScalar("referenceDate", StandardBasicTypes.STRING);

		return query.list();
	}
	
	public List<AllocateQuotationRecordDataConversionTableListModel> getAllocateIndoorQuotationRecordTableList(
			AllocateQuotationRecordDataConversionFilterModel filterModel,
			Date referenceMonth,
			Integer firstRecord, Integer displayLength, Order order){
		
		String referenceDate = String.format("FORMAT(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		if (filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
		String hql = 
				"select iqr.indoorQuotationRecordId as indoorQuotationRecordId,"
					+ " iqr.isFlag as isFlag,"
					+ " case when iqr.referenceDate is not null then " + referenceDate + " else '' end as referenceDate,"
					+ " sg.chineseName as subGroupChineseName, sg.englishName as subGroupEnglishName,"
					+ " u.chineseName as unitChineseName, u.englishName as unitEnglishName,"
					+ " substring(ot.code, length(ot.code)-2, 3) as outletType,"
					+ " o.name as outletName, "
					+ " case when count( distinct spr.subPriceRecordId ) > 0 then true else false end as subPriceUsed,"
					+ "	(case when u.seasonality = 1 then 'All-time' else"
					+ "		 case when u.seasonality = 2 then 'Summer' else"
					+ "			 case when u.seasonality = 3 then 'Winter' else "
					+ "				case when u.seasonality = 4 then 'Occasional' else '' end"
					+ "			 end"
					+ "		 end"
					+ " end) as seasonalItem,"
					+ " iqr.status as quotationRecordStatus,"
					+ " qr.reason as priceRemarks,"
					+ " qr.productRemark as productRemarks,"
					+ " qr.remark as otherRemarks,"
					+ " user.chineseName as allocatedIndoorOfficer,"
					+ " iqr.currentSPrice as editedCurrentSPrice, "
					+ " iqr.previousSPrice as editedPreviousSPrice, "
					+ " case when iqr.currentSPrice is null or iqr.previousSPrice is null or iqr.previousSPrice <= 0 then null "
					+ " else round((iqr.currentSPrice / iqr.previousSPrice ) * 100, 3) end as pr "
				+ " from IndoorQuotationRecord iqr "
					+ "	left join iqr.quotationRecord qr"
					+ " left join qr.assignment a"
					+ " left join iqr.quotation q"
					+ " left join q.product p"
					+ " left join p.fullSpecifications pfs on pfs.sequence = 1"
					+ " left join pfs.productAttribute pa"
					+ " left join q.outlet o"
					+ " left join q.unit u"
					+ " left join u.subItem si"
					+ " left join si.outletType ot"
					+ " left join ot.item i"
					+ " left join i.subGroup sg"
					+ " left join sg.group g"
					+ " left join qr.subPriceRecords spr"
					+ " left join u.purpose pu"
					+ " left join iqr.user user"
					+ " left join q.batch as b"
				+ " where 1=1"
				+ " and iqr.referenceMonth = :referenceMonth"
				+ " and pu.purposeId = :purposeId"
				+ " and iqr.status in :status";
		
		if (!StringUtils.isEmpty(filterModel.getSearch())){
			hql += " and ("
					+ " sg.chineseName like :search "
					+ " or " + referenceDate + "like :search "
					+ " or sg.englishName like :search "
					+ " or u.chineseName like :search "
					+ " or u.englishName like :search " 
					+ " or substring(ot.code, length(ot.code)-2, 3) like :search " 
					+ " or o.name like :search " 
					+ " or pa.specificationName like :search " 
					+ " or (case when u.seasonality = 1 then 'All-time' else"
					+ "		 case when u.seasonality = 2 then 'Summer' else"
					+ "			 case when u.seasonality = 3 then 'Winter' else "
					+ "				case when u.seasonality = 4 then 'Occasional' else '' end"
					+ "			 end"
					+ "		 end"
					+ " end) like :search " 
					+ " or iqr.status like :search " 
					+ " or qr.reason like :search " 
					+ " or qr.productRemark like :search " 
					+ " or qr.remark like :search " 
					+ " ) ";
		}
				
		
		if(filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
			hql += " and b.batchId in (:batchId)";
		}
		
		if (filterModel.getUnitIds() != null && filterModel.getUnitIds().size() > 0){
			hql += " and u.unitId in (:unitIds)";
		}		
		
		if(filterModel.getIndoorAllocationCode() != null){
			hql += " and q.indoorAllocationCode = :indoorAllocationCode";
		}
		
		if(filterModel.getGroupId() != null){
			hql += " and g.groupId = :groupId";
		}
		
		if(filterModel.getSubGroupId() != null){
			hql += " and sg.subGroupId = :subGroupId ";
		}
		
		if(!StringUtils.isEmpty(filterModel.getSurveyForm())){
			hql += " and b.surveyForm = :surveyForm ";
		}
		
		if(filterModel.getOutletTypeId() != null && filterModel.getOutletTypeId().size() > 0){
			hql += " and substring(ot.code, length(ot.code)-2, 3) in (:outletTypeId) ";
		}

		if(filterModel.getItemId() != null){
			hql += " and i.itemId = :itemId";
		}
		
		if(filterModel.getRuaQuotationStatus() != null){
			if(filterModel.getRuaQuotationStatus()){
				hql += " and q.status = 'RUA'";
			}else{
				hql += " and q.status != 'RUA'";
			}
		}
		
		if(filterModel.getQuotationRecordStatus() != null){
			if(filterModel.getQuotationRecordStatus()){
				hql += " and q.status = 'Pending'";
			}else{
				hql += " and q.status != 'Pending'";
			}
		}
		
		if(filterModel.getSeasonalItem() != null){
			hql += " and  u.seasonality  = :seasonalItem ";
		}
		
		if(filterModel.getAllocatedIndoorOfficer() != null){
			if(filterModel.getAllocatedIndoorOfficer()){
				hql += " and user is not null and user.id = :userId ";
			}else{
				hql += " and user is null";
			}
		}
		
		if(filterModel.getOutlectCategoryRemark() != null){
			if(filterModel.getOutlectCategoryRemark()){
				hql += " and qr.categoryRemark is not null and qr.categoryRemark <> '' ";
			}else{
				hql += " and (qr.categoryRemark is null or qr.categoryRemark = '') ";
			}
		}
		
		if(filterModel.getConsignmentCounter() != null){
			if(filterModel.getConsignmentCounter()){
				hql += " and qr.isConsignmentCounter = true ";
			}else{
				hql += " and qr.isConsignmentCounter = false ";
			}
		}
				
		if(filterModel.getWithPriceReason() != null){
			if(filterModel.getWithPriceReason()){
				hql += " and qr.reason is not null and qr.reason <> '' ";
			}else{
				hql += " and (qr.reason is null or qr.reason = '') ";
			}
		}
		
		if(filterModel.getWithPriceRemark() != null){
			if(filterModel.getWithPriceRemark()){
				hql += " and qr.remark is not null and qr.remark <> '' ";
			}else{
				hql += " and (qr.remark is null or qr.remark = '') ";
			}
		}
		
		if(filterModel.getWithProductRemark() != null){
			if(filterModel.getWithProductRemark()){
				hql += " and qr.productRemark is not null and qr.productRemark <> '' ";
			}else{
				hql += " and (qr.productRemark is null or qr.productRemark = '') ";
			}
		}
		
		if(filterModel.getWithDiscountRemark() != null){
			if(filterModel.getWithDiscountRemark()){
				hql += " and qr.discountRemark is not null and qr.discountRemark <> '' ";
			}else{
				hql += " and (qr.discountRemark is null or qr.discountRemark = '') ";
			}
		}
		
		if(filterModel.getWithOtherRemark() != null){
			if(filterModel.getWithOtherRemark()){
				hql += " and a.outletDiscountRemark is not null and a.outletDiscountRemark <> '' ";
			}else{
				hql += " and (a.outletDiscountRemark is null or a.outletDiscountRemark = '') ";
			}
		}
		
		if(filterModel.getNewProductCase() != null){
			if(filterModel.getNewProductCase()){
				hql += " and iqr.isNewProduct = 1";
			}else{
				hql += " and (iqr.isNewProduct = 0 or iqr.isNewProduct is null)";
			}
		}
		
		if(filterModel.getChangeProductCase() != null){
			if(filterModel.getChangeProductCase()){
				hql += " and iqr.isProductChange = 1";
			}else{
				hql += " and (iqr.isProductChange = 0 or iqr.isProductChange is null)";
			}
		}
		
		if(filterModel.getNewRecruitmentCase() != null){
			if(filterModel.getNewRecruitmentCase()){
				hql += " and iqr.isNewRecruitment = 1";
			}else{
				hql += " and (iqr.isNewRecruitment = 0 or iqr.isNewRecruitment is null)";
			}
		}
		
		if(filterModel.getSpicing() != null){
			if(filterModel.getSpicing()){
				hql += " and iqr.isSpicing = 1";
			}else{
				hql += " and (iqr.isSpicing = 0 or iqr.isSpicing is null)";
			}
		}
		
		if(filterModel.getAppliedFR() != null){
//			if(filterModel.getAppliedFR()){
//				hql += " and iqr.isApplyFR = 1";
//			}else{
//				hql += " and (iqr.isApplyFR = 0 or iqr.isApplyFR is null)";
//			}
			if(filterModel.getAppliedFR()){
				hql += " and q.isFRApplied = 1 ";
			}else{
				hql += " and (q.isFRApplied = 0 or q.isFRApplied is null) ";
			}
		}
		
		if (filterModel.getFr() != null){
			if(filterModel.getFr()){
				hql += " and u.frRequired = 1 ";
			}else{
				hql += " and u.frRequired = 0 ";
			}
		}
		
		if(filterModel.getApplicability() != null){
			if(filterModel.getApplicability()){
				hql += " and (qr.isSPricePeculiar = 0 or qr.isSPricePeculiar is null)";
			}else{
				hql += " and qr.isSPricePeculiar = 1";
			}
		}
		
		if(filterModel.getGreaterThan() != null || filterModel.getLessThan() != null || filterModel.getEqual() != null){
			List<String> params = new ArrayList<String>();
			String calculatePercent = "round((iqr.currentSPrice / "
					+ " case when iqr.previousSPrice <= 0 then null else iqr.previousSPrice end"
					+ " ) * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(filterModel.getGreaterThan() != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(filterModel.getLessThan() != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(filterModel.getEqual() != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			hql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and (" + StringUtils.join(params.toArray(), " or ") + ")";
		}
		
		if(filterModel.getWithDiscountPattern() != null){
			if(filterModel.getWithDiscountPattern()){
				hql += " and qr.discount is not null and qr.discount <> '' ";
			}else{
				hql += " and (qr.discount is null or qr.discount = '') ";
			}
		}
		
		if (filterModel.getReferenceDate()!=null){
			hql += " and iqr.referenceDate = :referenceDate ";
		}
		
		if (filterModel.getAvailability()!=null){
			hql += " and qr.availability = :availability ";
		}
		
		if (filterModel.getFirmStatus()!=null){
			hql += " and a.status = :firmStatus ";
		}
		
		if (filterModel.getPr()!=null){
			if (filterModel.getPr()){
				hql += " and ( iqr.currentSPrice is null or iqr.previousSPrice is null or iqr.previousSPrice <= 0 ) ";
			} else {
				hql += " and not ( iqr.currentSPrice is null or iqr.previousSPrice is null or iqr.previousSPrice <= 0 ) ";
			}
		}
		
		if (filterModel.getQuotationIds()!=null && filterModel.getQuotationIds().size()>0){
			hql += " and q.quotationId in (:quotationIds) ";
		}
		
		if (filterModel.getRemark()!=null){
			if (filterModel.getRemark()){
				hql += " and ( iqr.remark is not null and iqr.remark <> '')";
			} else {
				hql += " and not ( iqr.remark is not null and iqr.remark <> '')";
			}
		}
		
		hql += " group by iqr.indoorQuotationRecordId, iqr.isFlag, iqr.referenceDate, sg.chineseName, sg.englishName,"
				+ " u.chineseName, u.englishName, o.name,"
				+ " iqr.status, qr.reason, qr.remark, qr.productRemark,"
				+ " substring(ot.code, length(ot.code)-2, 3), u.seasonality, user.chineseName,"
				+ " iqr.currentSPrice, iqr.previousSPrice";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");

		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", filterModel.getPurposeId());
		if (!StringUtils.isEmpty(filterModel.getSearch())){
			query.setParameter("search", String.format("%%%s%%", filterModel.getSearch()));			
		}
		query.setParameterList("status", new String[]{"Allocation", "Conversion", "Reject Verification"});
		
		if(filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
			query.setParameterList("batchId", filterModel.getBatchCode());
		}
		
		if(!StringUtils.isEmpty(filterModel.getIndoorAllocationCode())){
			query.setParameter("indoorAllocationCode", filterModel.getIndoorAllocationCode());
		}
		
		if(filterModel.getGroupId() != null){
			query.setParameter("groupId", filterModel.getGroupId());
		}
		
		if(filterModel.getSubGroupId() != null){
			query.setParameter("subGroupId", filterModel.getSubGroupId());
		}
		
		if(!StringUtils.isEmpty(filterModel.getSurveyForm())){
			query.setParameter("surveyForm", filterModel.getSurveyForm());
		}		
		
		if(filterModel.getOutletTypeId() != null && filterModel.getOutletTypeId().size() > 0){
			query.setParameterList("outletTypeId", filterModel.getOutletTypeId());
		}

		if(filterModel.getItemId() != null){
			query.setParameter("itemId", filterModel.getItemId());
		}
		
		if(filterModel.getGreaterThan() != null){
			query.setParameter("greaterThan", filterModel.getGreaterThan());
		}
		
		if(filterModel.getLessThan() != null){
			query.setParameter("lessThan", filterModel.getLessThan());
		}
		
		if(filterModel.getEqual() != null){
			query.setParameter("equal", filterModel.getEqual());
		}
		
		if(filterModel.getSeasonalItem() != null){
			query.setParameter("seasonalItem", filterModel.getSeasonalItem());
		}
		
		if(filterModel.getAllocatedIndoorOfficer() != null && filterModel.getAllocatedIndoorOfficer()){
			if(filterModel.getIndoorOfficer() != null){
				query.setParameter("userId", filterModel.getIndoorOfficer());
			}
		}
		
		if (filterModel.getUnitIds() != null && filterModel.getUnitIds().size() > 0){
			query.setParameterList("unitIds", filterModel.getUnitIds());
		}
		
		if (filterModel.getReferenceDate() != null){
			query.setParameter("referenceDate", filterModel.getReferenceDate());
		}
		
		if (filterModel.getAvailability()!=null){
			query.setParameter("availability", filterModel.getAvailability());
		}
		
		if (filterModel.getFirmStatus()!=null){
			query.setParameter("firmStatus", filterModel.getFirmStatus());
		}
		
		if (filterModel.getQuotationIds()!=null && filterModel.getQuotationIds().size()>0){
			query.setParameterList("quotationIds", filterModel.getQuotationIds());
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setResultTransformer(Transformers.aliasToBean(AllocateQuotationRecordDataConversionTableListModel.class));

		return query.list();
		}
		else{
			List<AllocateQuotationRecordDataConversionTableListModel> emptyList = Collections.emptyList();
			System.out.println("asdasdasd");
			return emptyList;
		}
		
	}
	
	public Long countAllocateIndoorQuotationRecordTableList(
			AllocateQuotationRecordDataConversionFilterModel filterModel,
			Date referenceMonth,
			Integer firstRecord, Integer displayLength){
		if(filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
		String referenceDate = String.format("FORMAT(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String hql = 
				"select count(distinct iqr.indoorQuotationRecordId) as indoorQuotationRecordId "
				+ " from IndoorQuotationRecord iqr "
					+ "	left join iqr.quotationRecord qr"
					+ " left join qr.assignment a"
					+ " left join iqr.quotation q"
					+ " left join q.product p"
					+ " left join p.fullSpecifications pfs on pfs.sequence = 1"
					+ " left join pfs.productAttribute pa"
					+ " left join q.outlet o"
					+ " left join q.unit u"
					+ " left join u.subItem si"
					+ " left join si.outletType ot"
					+ " left join ot.item i"
					+ " left join i.subGroup sg"
					+ " left join sg.group g"
					+ " left join qr.subPriceRecords spr"
					+ " left join u.purpose pu"
					+ " left join iqr.user user"
					+ " left join q.batch as b"
				+ " where 1=1"
				+ " and iqr.referenceMonth = :referenceMonth"
				+ " and pu.purposeId = :purposeId"
				+ " and iqr.status in :status";
		if (!StringUtils.isEmpty(filterModel.getSearch())){ 
				hql += " and ("
					+ " sg.chineseName like :search "
					+ " or sg.englishName like :search "
					+ " or u.chineseName like :search "
					+ " or u.englishName like :search " 
					+ " or substring(ot.code, length(ot.code)-2, 3) like :search " 
					+ " or o.name like :search " 
					+ " or pa.specificationName like :search " 
					+ " or (case when u.seasonality = 1 then 'All-time' else"
					+ "		 case when u.seasonality = 2 then 'Summer' else"
					+ "			 case when u.seasonality = 3 then 'Winter' else "
					+ "				case when u.seasonality = 4 then 'Occasional' else '' end"
					+ "			 end"
					+ "		 end"
					+ " end) like :search " 
					+ " or iqr.status like :search " 
					+ " or qr.reason like :search " 
					+ " or qr.productRemark like :search " 
					+ " or qr.remark like :search " 
					+ " or " + referenceDate + " like :search "
					+ " ) ";
		}
		
		if(filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
			hql += " and b.batchId in (:batchId)";
		}
		
		if (filterModel.getUnitIds() != null && filterModel.getUnitIds().size() > 0){
			hql += " and u.unitId in (:unitIds)";
		}		
		
		if(!StringUtils.isEmpty(filterModel.getIndoorAllocationCode())){
			hql += " and q.indoorAllocationCode = :indoorAllocationCode";
		}
		
		if(filterModel.getGroupId() != null){
			hql += " and g.groupId = :groupId";
		}
		
		if(filterModel.getSubGroupId() != null){
			hql += " and sg.subGroupId = :subGroupId ";
		}
		
		if(!StringUtils.isEmpty(filterModel.getSurveyForm())){
			hql += " and b.surveyForm = :surveyForm ";
		}
		
		if(filterModel.getOutletTypeId() != null && filterModel.getOutletTypeId().size() > 0){
			hql += " and substring(ot.code, length(ot.code)-2, 3) in (:outletTypeId) ";
		}

		if(filterModel.getItemId() != null){
			hql += " and i.itemId = :itemId";
		}
		
		if(filterModel.getRuaQuotationStatus() != null){
			if(filterModel.getRuaQuotationStatus()){
				hql += " and q.status = 'RUA'";
			}else{
				hql += " and q.status != 'RUA'";
			}
		}
		
		if(filterModel.getQuotationRecordStatus() != null){
			if(filterModel.getQuotationRecordStatus()){
				hql += " and q.status = 'Pending'";
			}else{
				hql += " and q.status != 'Pending'";
			}
		}
		
		if(filterModel.getSeasonalItem() != null){
			hql += " and  u.seasonality  = :seasonalItem ";
		}
		
		if(filterModel.getAllocatedIndoorOfficer() != null){
			if(filterModel.getAllocatedIndoorOfficer()){
				hql += " and user is not null and user.id = :userId ";
			}else{
				hql += " and user is null";
			}
		}
		
		if(filterModel.getOutlectCategoryRemark() != null){
			if(filterModel.getOutlectCategoryRemark()){
				hql += " and qr.categoryRemark is not null and qr.categoryRemark <> '' ";
			}else{
				hql += " and (qr.categoryRemark is null or qr.categoryRemark = '') ";
			}
		}
		
		if(filterModel.getConsignmentCounter() != null){
			if(filterModel.getConsignmentCounter()){
				hql += " and qr.isConsignmentCounter = true ";
			}else{
				hql += " and qr.isConsignmentCounter = false ";
			}
		}
				
		if(filterModel.getWithPriceReason() != null){
			if(filterModel.getWithPriceReason()){
				hql += " and qr.reason is not null and qr.reason <> '' ";
			}else{
				hql += " and (qr.reason is null or qr.reason = '') ";
			}
		}
		
		if(filterModel.getWithPriceRemark() != null){
			if(filterModel.getWithPriceRemark()){
				hql += " and qr.remark is not null and qr.remark <> '' ";
			}else{
				hql += " and (qr.remark is null or qr.remark = '') ";
			}
		}
		
		if(filterModel.getWithProductRemark() != null){
			if(filterModel.getWithProductRemark()){
				hql += " and qr.productRemark is not null and qr.productRemark <> '' ";
			}else{
				hql += " and (qr.productRemark is null or qr.productRemark = '') ";
			}
		}
		
		if(filterModel.getWithDiscountRemark() != null){
			if(filterModel.getWithDiscountRemark()){
				hql += " and qr.discountRemark is not null and qr.discountRemark <> '' ";
			}else{
				hql += " and (qr.discountRemark is null or qr.discountRemark = '') ";
			}
		}
		
		if(filterModel.getWithOtherRemark() != null){
			if(filterModel.getWithOtherRemark()){
				hql += " and a.outletDiscountRemark is not null and a.outletDiscountRemark <> '' ";
			}else{
				hql += " and (a.outletDiscountRemark is null or a.outletDiscountRemark = '') ";
			}
		}
		
		if(filterModel.getNewProductCase() != null){
			if(filterModel.getNewProductCase()){
				hql += " and iqr.isNewProduct = 1";
			}else{
				hql += " and (iqr.isNewProduct = 0 or iqr.isNewProduct is null)";
			}
		}
		
		if(filterModel.getChangeProductCase() != null){
			if(filterModel.getChangeProductCase()){
				hql += " and iqr.isProductChange = 1";
			}else{
				hql += " and (iqr.isProductChange = 0 or iqr.isProductChange is null)";
			}
		}
		
		if(filterModel.getNewRecruitmentCase() != null){
			if(filterModel.getNewRecruitmentCase()){
				hql += " and iqr.isNewRecruitment = 1";
			}else{
				hql += " and (iqr.isNewRecruitment = 0 or iqr.isNewRecruitment is null)";
			}
		}
		
		if(filterModel.getSpicing() != null){
			if(filterModel.getSpicing()){
				hql += " and iqr.isSpicing = 1";
			}else{
				hql += " and (iqr.isSpicing = 0 or iqr.isSpicing is null)";
			}
		}
		
//		if(filterModel.getAppliedFR() != null){
//			if(filterModel.getAppliedFR()){
//				hql += " and iqr.isApplyFR = 1";
//			}else{
//				hql += " and (iqr.isApplyFR = 0 or iqr.isApplyFR is null)";
//			}
//		}
		if(filterModel.getAppliedFR() != null){
//			if(filterModel.getAppliedFR()){
//				hql += " and iqr.isApplyFR = 1";
//			}else{
//				hql += " and (iqr.isApplyFR = 0 or iqr.isApplyFR is null)";
//			}
			if(filterModel.getAppliedFR()){
				hql += " and q.isFRApplied = 1 ";
			}else{
				hql += " and (q.isFRApplied = 0 or q.isFRApplied is null) ";
			}
		}
		
		if (filterModel.getFr() != null){
			if(filterModel.getFr()){
				hql += " and u.frRequired = 1 ";
			}else{
				hql += " and u.frRequired = 0 ";
			}
		}
		
		if(filterModel.getApplicability() != null){
			if(filterModel.getApplicability()){
				hql += " and (qr.isSPricePeculiar = 0 or qr.isSPricePeculiar is null)";
			}else{
				hql += " and qr.isSPricePeculiar = 1";
			}
		}
		

		if(filterModel.getGreaterThan() != null || filterModel.getLessThan() != null || filterModel.getEqual() != null){
			List<String> params = new ArrayList<String>();
			String calculatePercent = "round((iqr.currentSPrice / "
					+ " case when iqr.previousSPrice <= 0 then null else iqr.previousSPrice end"
					+ " ) * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(filterModel.getGreaterThan() != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(filterModel.getLessThan() != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(filterModel.getEqual() != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			hql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and (" + StringUtils.join(params.toArray(), " or ") + ")";
		}
		
		if(filterModel.getWithDiscountPattern() != null){
			if(filterModel.getWithDiscountPattern()){
				hql += " and qr.discount is not null and qr.discount <> '' ";
			}else{
				hql += " and (qr.discount is null or qr.discount = '') ";
			}
		}
		
		if (filterModel.getReferenceDate()!=null){
			hql += " and iqr.referenceDate = :referenceDate";
		}
		
		if (filterModel.getAvailability()!=null){
			hql += " and qr.availability = :availability ";
		}
		
		if (filterModel.getFirmStatus()!=null){
			hql += " and a.status = :firmStatus ";
		}
		
		if (filterModel.getPr()!=null){
			if (filterModel.getPr()){
				hql += " and ( iqr.currentSPrice is null or iqr.previousSPrice is null or iqr.previousSPrice <= 0 )";
			} else {
				hql += " and not ( iqr.currentSPrice is null or iqr.previousSPrice is null or iqr.previousSPrice <= 0 )";
			}
		}
		
		if (filterModel.getQuotationIds()!=null && filterModel.getQuotationIds().size()>0){
			hql += " and q.quotationId in (:quotationIds) ";
		}
		
		if (filterModel.getRemark()!=null){
			if (filterModel.getRemark()){
				hql += " and ( iqr.remark is not null and iqr.remark <> '') ";
			} else {
				hql += " and not ( iqr.remark is not null and iqr.remark <> '') ";
			}
		}
		
		//hql += " group by iqr.indoorQuotationRecordId";
		
		/*String countHql = "select count(distinct iqr.id) "
				+ " from IndoorQuotationRecord as iqr "
				+ " where iqr.id in (" + hql + ") ";*/
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", filterModel.getPurposeId());
		if (!StringUtils.isEmpty(filterModel.getSearch())){ 
			query.setParameter("search", String.format("%%%s%%", filterModel.getSearch()));
		}
		query.setParameterList("status", new String[]{"Allocation", "Conversion", "Reject Verification"});
		
		if(filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
			query.setParameterList("batchId", filterModel.getBatchCode());
		}
		
		if(!StringUtils.isEmpty(filterModel.getIndoorAllocationCode())){
			query.setParameter("indoorAllocationCode", filterModel.getIndoorAllocationCode());
		}
		
		if(filterModel.getGroupId() != null){
			query.setParameter("groupId", filterModel.getGroupId());
		}
		
		if(filterModel.getSubGroupId() != null){
			query.setParameter("subGroupId", filterModel.getSubGroupId());
		}
		
		if(!StringUtils.isEmpty(filterModel.getSurveyForm())){
			query.setParameter("surveyForm", filterModel.getSurveyForm());
		}		
		
		if(filterModel.getOutletTypeId() != null && filterModel.getOutletTypeId().size() > 0){
			query.setParameterList("outletTypeId", filterModel.getOutletTypeId());
		}

		if(filterModel.getItemId() != null){
			query.setParameter("itemId", filterModel.getItemId());
		}
		
		if(filterModel.getGreaterThan() != null){
			query.setParameter("greaterThan", filterModel.getGreaterThan());
		}
		
		if(filterModel.getLessThan() != null){
			query.setParameter("lessThan", filterModel.getLessThan());
		}
		
		if(filterModel.getEqual() != null){
			query.setParameter("equal", filterModel.getEqual());
		}
		
		if(filterModel.getSeasonalItem() != null){
			query.setParameter("seasonalItem", filterModel.getSeasonalItem());
		}
		
		if(filterModel.getAllocatedIndoorOfficer() != null && filterModel.getAllocatedIndoorOfficer()){
			if(filterModel.getIndoorOfficer() != null){
				query.setParameter("userId", filterModel.getIndoorOfficer());
			}
		}
		
		if (filterModel.getUnitIds() != null && filterModel.getUnitIds().size() > 0){
			query.setParameterList("unitIds", filterModel.getUnitIds());
		}
		
		if (filterModel.getReferenceDate()!=null){
			query.setParameter("referenceDate", filterModel.getReferenceDate());
		}
		
		if (filterModel.getAvailability()!=null){
			query.setParameter("availability", filterModel.getAvailability());
		}
		
		if (filterModel.getFirmStatus()!=null){
			query.setParameter("firmStatus", filterModel.getFirmStatus());
		}
		
		if (filterModel.getQuotationIds()!=null && filterModel.getQuotationIds().size()>0){
			query.setParameterList("quotationIds", filterModel.getQuotationIds());
		}
		System.out.println("123 : " + hql);
		System.out.println("456 : " + query);
		return query.uniqueResult() == null ? (long) 0 : (long) query.uniqueResult();
		}
		else{
			return (long) 0;
		}
	
	}
	
	public List<Integer> getAllocateIndoorQuotationRecordTableListIds(
			AllocateQuotationRecordDataConversionFilterModel filterModel,
			Date referenceMonth){
		if(filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
		String referenceDate = String.format("FORMAT(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = 
				"select iqr.indoorQuotationRecordId"
				+ " from IndoorQuotationRecord iqr "
					+ "	left join iqr.quotationRecord qr"
					+ " left join qr.assignment a"
					+ " left join iqr.quotation q"
					+ " left join q.product p"
					+ " left join p.fullSpecifications pfs on pfs.sequence = 1"
					+ " left join pfs.productAttribute pa"
					+ " left join q.outlet o"
					+ " left join q.unit u"
					+ " left join u.subItem si"
					+ " left join si.outletType ot"
					+ " left join ot.item i"
					+ " left join i.subGroup sg"
					+ " left join sg.group g"
					+ " left join qr.subPriceRecords spr"
					+ " left join u.purpose pu"
					+ " left join iqr.user user"
					+ " left join q.batch as b"
				+ " where 1=1"
				+ " and iqr.referenceMonth = :referenceMonth"
				+ " and pu.purposeId = :purposeId"
				+ " and iqr.status in :status";
		if (!StringUtils.isEmpty(filterModel.getSearch())){ 
				hql += " and ("
					+ " sg.chineseName like :search "
					+ " or sg.englishName like :search "
					+ " or u.chineseName like :search "
					+ " or u.englishName like :search " 
					+ " or substring(ot.code, length(ot.code)-2, 3) like :search " 
					+ " or o.name like :search " 
					+ " or pa.specificationName like :search " 
					+ " or (case when u.seasonality = 1 then 'All-time' else"
					+ "		 case when u.seasonality = 2 then 'Summer' else"
					+ "			 case when u.seasonality = 3 then 'Winter' else "
					+ "				case when u.seasonality = 4 then 'Occasional' else '' end"
					+ "			 end"
					+ "		 end"
					+ " end) like :search " 
					+ " or iqr.status like :search " 
					+ " or qr.reason like :search " 
					+ " or qr.productRemark like :search " 
					+ " or qr.remark like :search " 
					+ " or " + referenceDate + " like :search "
					+ " ) ";
		}
		
		if(filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
			hql += " and b.batchId in (:batchId)";
		}
		
		if (filterModel.getUnitIds() != null && filterModel.getUnitIds().size() > 0){
			hql += " and u.unitId in (:unitIds)";
		}		
		
		if(!StringUtils.isEmpty(filterModel.getIndoorAllocationCode())){
			hql += " and q.indoorAllocationCode = :indoorAllocationCode";
		}
		
		if(filterModel.getGroupId() != null){
			hql += " and g.groupId = :groupId";
		}
		
		if(filterModel.getSubGroupId() != null){
			hql += " and sg.subGroupId = :subGroupId ";
		}
		
		if(!StringUtils.isEmpty(filterModel.getSurveyForm())){
			hql += " and b.surveyForm = :surveyForm ";
		}
		
		if(filterModel.getOutletTypeId() != null && filterModel.getOutletTypeId().size() > 0){
			hql += " and substring(ot.code, length(ot.code)-2, 3) in (:outletTypeId) ";
		}

		if(filterModel.getItemId() != null){
			hql += " and i.itemId = :itemId";
		}
		
		if(filterModel.getRuaQuotationStatus() != null){
			if(filterModel.getRuaQuotationStatus()){
				hql += " and q.status = 'RUA'";
			}else{
				hql += " and q.status != 'RUA'";
			}
		}
		
		if(filterModel.getQuotationRecordStatus() != null){
			if(filterModel.getQuotationRecordStatus()){
				hql += " and q.status = 'Pending'";
			}else{
				hql += " and q.status != 'Pending'";
			}
		}
		
		if(filterModel.getSeasonalItem() != null){
			hql += " and  u.seasonality  = :seasonalItem ";
		}
		
		if(filterModel.getAllocatedIndoorOfficer() != null){
			if(filterModel.getAllocatedIndoorOfficer()){
				hql += " and user is not null and user.id = :userId ";
			}else{
				hql += " and user is null";
			}
		}
		
		if(filterModel.getOutlectCategoryRemark() != null){
			if(filterModel.getOutlectCategoryRemark()){
				hql += " and qr.categoryRemark is not null and qr.categoryRemark <> '' ";
			}else{
				hql += " and (qr.categoryRemark is null or qr.categoryRemark = '') ";
			}
		}
		
		if(filterModel.getConsignmentCounter() != null){
			if(filterModel.getConsignmentCounter()){
				hql += " and qr.isConsignmentCounter = true ";
			}else{
				hql += " and qr.isConsignmentCounter = false ";
			}
		}
				
		if(filterModel.getWithPriceReason() != null){
			if(filterModel.getWithPriceReason()){
				hql += " and qr.reason is not null and qr.reason <> '' ";
			}else{
				hql += " and (qr.reason is null or qr.reason = '') ";
			}
		}
		
		if(filterModel.getWithPriceRemark() != null){
			if(filterModel.getWithPriceRemark()){
				hql += " and qr.remark is not null and qr.remark <> '' ";
			}else{
				hql += " and (qr.remark is null or qr.remark = '') ";
			}
		}
		
		if(filterModel.getWithProductRemark() != null){
			if(filterModel.getWithProductRemark()){
				hql += " and qr.productRemark is not null and qr.productRemark <> '' ";
			}else{
				hql += " and (qr.productRemark is null or qr.productRemark = '') ";
			}
		}
		
		if(filterModel.getWithDiscountRemark() != null){
			if(filterModel.getWithDiscountRemark()){
				hql += " and qr.discountRemark is not null and qr.discountRemark <> '' ";
			}else{
				hql += " and (qr.discountRemark is null or qr.discountRemark = '') ";
			}
		}
		
		if(filterModel.getWithOtherRemark() != null){
			if(filterModel.getWithOtherRemark()){
				hql += " and a.outletDiscountRemark is not null and a.outletDiscountRemark <> '' ";
			}else{
				hql += " and (a.outletDiscountRemark is null or a.outletDiscountRemark = '') ";
			}
		}
		
		if(filterModel.getNewProductCase() != null){
			if(filterModel.getNewProductCase()){
				hql += " and iqr.isNewProduct = 1";
			}else{
				hql += " and (iqr.isNewProduct = 0 or iqr.isNewProduct is null)";
			}
		}
		
		if(filterModel.getChangeProductCase() != null){
			if(filterModel.getChangeProductCase()){
				hql += " and iqr.isProductChange = 1";
			}else{
				hql += " and (iqr.isProductChange = 0 or iqr.isProductChange is null)";
			}
		}
		
		if(filterModel.getNewRecruitmentCase() != null){
			if(filterModel.getNewRecruitmentCase()){
				hql += " and iqr.isNewRecruitment = 1";
			}else{
				hql += " and (iqr.isNewRecruitment = 0 or iqr.isNewRecruitment is null)";
			}
		}
		
		if(filterModel.getSpicing() != null){
			if(filterModel.getSpicing()){
				hql += " and iqr.isSpicing = 1";
			}else{
				hql += " and (iqr.isSpicing = 0 or iqr.isSpicing is null)";
			}
		}
		
		if(filterModel.getAppliedFR() != null){
//			if(filterModel.getAppliedFR()){
//				hql += " and iqr.isApplyFR = 1";
//			}else{
//				hql += " and (iqr.isApplyFR = 0 or iqr.isApplyFR is null)";
//			}
			if(filterModel.getAppliedFR()){
				hql += " and q.isFRApplied = 1 ";
			}else{
				hql += " and (q.isFRApplied = 0 or q.isFRApplied is null) ";
			}
		}
		
		if (filterModel.getFr() != null){
			if(filterModel.getFr()){
				hql += " and u.frRequired = 1 ";
			}else{
				hql += " and u.frRequired = 0 ";
			}
		}
		
		if(filterModel.getApplicability() != null){
			if(filterModel.getApplicability()){
				hql += " and (qr.isSPricePeculiar = 0 or qr.isSPricePeculiar is null)";
			}else{
				hql += " and qr.isSPricePeculiar = 1";
			}
		}
		

		if(filterModel.getGreaterThan() != null || filterModel.getLessThan() != null || filterModel.getEqual() != null){
			List<String> params = new ArrayList<String>();
			// String calculatePercent = "round((iqr.currentSPrice - iqr.previousSPrice) / iqr.previousSPrice * 100, 2)";
			String calculatePercent = "round((iqr.currentSPrice / "
					+ " case when iqr.previousSPrice <= 0 then null else iqr.previousSPrice end"
					+ " ) * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(filterModel.getGreaterThan() != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(filterModel.getLessThan() != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(filterModel.getEqual() != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			hql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and (" + StringUtils.join(params.toArray(), " or ") + ")";
		}
		
		if(filterModel.getWithDiscountPattern() != null){
			if(filterModel.getWithDiscountPattern()){
				hql += " and qr.discount is not null and qr.discount <> '' ";
			}else{
				hql += " and (qr.discount is null or qr.discount = '') ";
			}
		}
		
		if (filterModel.getReferenceDate()!=null){
			hql += " and iqr.referenceDate = :referenceDate ";
		}
		
		if (filterModel.getAvailability()!=null){
			hql += " and qr.availability = :availability ";
		}
		
		if (filterModel.getFirmStatus()!=null){
			hql += " and a.status = :firmStatus ";
		}
		
		if (filterModel.getPr()!=null){
			if (filterModel.getPr()){
				hql += " and ( iqr.currentSPrice is null or iqr.previousSPrice is null or iqr.previousSPrice <= 0 )";
			} else {
				hql += " and not ( iqr.currentSPrice is null or iqr.previousSPrice is null or iqr.previousSPrice <= 0 )";
			}
		}
		
		if (filterModel.getQuotationIds()!=null && filterModel.getQuotationIds().size()>0){
			hql += " and q.quotationId in (:quotationIds) ";
		}
		
		if (filterModel.getRemark()!=null){
			if (filterModel.getRemark()){
				hql += " and ( iqr.remark is not null and iqr.remark <> '') ";
			} else {
				hql += " and not ( iqr.remark is not null and iqr.remark <> '') ";
			}
		}
		
		hql += " group by iqr.indoorQuotationRecordId";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", filterModel.getPurposeId());
		if (!StringUtils.isEmpty(filterModel.getSearch())){ 
			query.setParameter("search", String.format("%%%s%%", filterModel.getSearch()));
		}
		query.setParameterList("status", new String[]{"Allocation", "Conversion", "Reject Verification"});
		
		if(filterModel.getBatchCode() != null && filterModel.getBatchCode().size() > 0){
			query.setParameterList("batchId", filterModel.getBatchCode());
		}
		
		if(!StringUtils.isEmpty(filterModel.getIndoorAllocationCode())){
			query.setParameter("indoorAllocationCode", filterModel.getIndoorAllocationCode());
		}
		
		if(filterModel.getGroupId() != null){
			query.setParameter("groupId", filterModel.getGroupId());
		}
		
		if(filterModel.getSubGroupId() != null){
			query.setParameter("subGroupId", filterModel.getSubGroupId());
		}
		
		if(!StringUtils.isEmpty(filterModel.getSurveyForm())){
			query.setParameter("surveyForm", filterModel.getSurveyForm());
		}		
		
		if(filterModel.getOutletTypeId() != null && filterModel.getOutletTypeId().size() > 0){
			query.setParameterList("outletTypeId", filterModel.getOutletTypeId());
		}

		if(filterModel.getItemId() != null){
			query.setParameter("itemId", filterModel.getItemId());
		}
		
		if(filterModel.getGreaterThan() != null){
			query.setParameter("greaterThan", filterModel.getGreaterThan());
		}
		
		if(filterModel.getLessThan() != null){
			query.setParameter("lessThan", filterModel.getLessThan());
		}
		
		if(filterModel.getEqual() != null){
			query.setParameter("equal", filterModel.getEqual());
		}
		
		if(filterModel.getSeasonalItem() != null){
			query.setParameter("seasonalItem", filterModel.getSeasonalItem());
		}
		
		if(filterModel.getAllocatedIndoorOfficer() != null && filterModel.getAllocatedIndoorOfficer()){
			if(filterModel.getIndoorOfficer() != null){
				query.setParameter("userId", filterModel.getIndoorOfficer());
			}
		}
		
		if (filterModel.getUnitIds() != null && filterModel.getUnitIds().size() > 0){
			query.setParameterList("unitIds", filterModel.getUnitIds());
		}
		
		if (filterModel.getReferenceDate()!=null){
			query.setParameter("referenceDate", filterModel.getReferenceDate());
		}

		if (filterModel.getAvailability()!=null){
			query.setParameter("availability", filterModel.getAvailability());
		}
		
		if (filterModel.getFirmStatus()!=null){
			query.setParameter("firmStatus", filterModel.getFirmStatus());
		}
		
		if (filterModel.getQuotationIds()!=null && filterModel.getQuotationIds().size()>0){
			query.setParameterList("quotationIds", filterModel.getQuotationIds());
		}
		return query.list();
		}
		else{
			List<Integer> emptyList = Collections.emptyList();
			return emptyList;
		}
		
	}
	
	public long countIndoorQuotationRecordTableList(String search, Integer userId, String[] status,
			Integer purposeId, Date referenceMonth, Integer subGroupId, List<Integer> unitId, Integer outletId,
			List<String> outletTypeShortCode, Integer seasonalItem, String outletCategory, Boolean subPrice,
			Boolean outletCategoryRemark, Boolean priceRemark, String surveyForm,
			Boolean allocatedIndoorOfficer, Integer indoorOfficer, Double greaterThan, Double lessThan, Double equal,
			Boolean withPriceReason, Boolean withOtherRemark, Boolean withProductRemark, Boolean withDiscountRemark, 
			Boolean withFieldwork, Boolean isPRNull, Boolean withIndoorConversionRemarks, String referenceDateCrit,
			Integer firmStatus, Integer availability, Boolean withDiscount, Integer quotationId){
		
		String referenceDate = String.format("FORMAT(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = 
				"select iqr.IndoorQuotationRecordId "
				+ " from IndoorQuotationRecord iqr "
					+ "	left outer join QuotationRecord qr on iqr.QuotationRecordId = qr.QuotationRecordId "
					+ "	left outer join Assignment a on a.AssignmentId = qr.AssignmentId "
					+ " left outer join Quotation q on iqr.QuotationId = q.QuotationId "
					+ " left outer join Batch b on q.BatchId = b.BatchId "
					+ " left outer join Product p on q.ProductId = p.ProductId "
					+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
					+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
					+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
					+ " left outer join Outlet o on q.OutletId = o.OutletId "
					+ " left outer join Unit u on q.UnitId = u.UnitId "
					+ " left outer join SubItem si on u.SubItemId = si.SubItemId "
					+ " left outer join OutletType ot on si.OutletTypeId = ot.OutletTypeId "
					+ " left outer join Item i on ot.ItemId = i.ItemId "
					+ " left outer join SubGroup sg on i.SubGroupId = sg.SubGroupId "
					+ " left outer join SubPriceRecord spr on qr.QuotationRecordId = spr.QuotationRecordId "
					+ " left outer join Purpose pu on u.PurposeId = pu.PurposeId "
					+ " left outer join [User] as user_ on iqr.UserId = user_.UserId "
					+ " left outer join AssignmentUnitCategoryInfo as auc on a.AssignmentId = auc.AssignmentId "
					+ "  and u.UnitCategory = auc.UnitCategory "
				+ " where 1 = 1 "
				+ " and iqr.ReferenceMonth = :referenceMonth"
				+ " and pu.PurposeId = :purposeId"
				+ " and iqr.Status in (:status)";
		
		if (!StringUtils.isEmpty(search)){
			sql += " and ("
				+ " sg.ChineseName like :search "
				+ " or sg.EnglishName like :search "
				+ " or u.ChineseName like :search "
				+ " or u.EnglishName like :search " 
				+ " or substring(ot.Code, len(ot.Code)-2, 3) like :search " 
				+ " or o.Name like :search " 
//				+ " or concat(pfs.specificationName,'=',pfs.value) like :search " 
				+ " or pa.SpecificationName like :search or ps.Value like :search " 
				+ " or (case when u.Seasonality = 1 then 'All-time' else"
				+ "		 case when u.Seasonality = 2 then 'Summer' else"
				+ "			 case when u.Seasonality = 3 then 'Winter' else "
				+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
				+ "			 end"
				+ "		 end"
				+ " end) like :search " 
//				+ " or iqr.Status like :search " 
				+ " or qr.Reason like :search " 
				+ " or qr.ProductRemark like :search " 
				+ " or qr.Remark like :search " 
				+ " or str(iqr.IndoorQuotationRecordId) like :search "
				+ " or str(q.QuotationId) like :search "
				+ " or iqr.remark like :search " 
				+ " or a.OutletDiscountRemark like :search "
				+ " or qr.DiscountRemark like :search "
				+ " or u.UnitCategory like :search "
				+ " or " + referenceDate + " like :search "
				+ " ) ";
		}
		
		if (userId != null)
			sql += " and user_.UserId = :userId";
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypeShortCode) ";
		}
		
		if (subGroupId != null){
			sql += " and sg.SubGroupId = :subGroupId ";
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			sql += " and u.UnitCategory = :outletCategory ";
		}
		
		if(outletId != null){
			sql += " and o.OutletId = :outletId ";
		}
		
		if(unitId != null && unitId.size() > 0){
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if(seasonalItem != null){
			sql += " and  u.Seasonality  = :seasonalItem ";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			sql += " and  b.SurveyForm  = :surveyForm ";
		}

		if(priceRemark != null && priceRemark){
			sql += " and (qr.Remark is not null and qr.Remark != '' )";
		}else if(priceRemark != null && !priceRemark){
			sql += " and (qr.Remark is null or qr.Remark = '' )";
		}
		
		if(outletCategoryRemark != null && outletCategoryRemark){
			sql += " and (qr.CategoryRemark is not null and qr.CategoryRemark != '' )";
		}else if(outletCategoryRemark != null && !outletCategoryRemark){
			sql += " and (qr.CategoryRemark is null or qr.CategoryRemark = '' )";
		}
		

		if(withPriceReason != null){
			if(withPriceReason){
				sql += " and qr.Reason is not null and qr.Reason <> '' ";
			}else{
				sql += " and (qr.Reason is null or qr.Reason = '') ";
			}
		}
		
		if(withProductRemark != null){
			if(withProductRemark){
				sql += " and qr.ProductRemark is not null and qr.ProductRemark <> '' ";
			}else{
				sql += " and (qr.ProductRemark is null or qr.ProductRemark = '') ";
			}
		}
		
		if(withDiscountRemark != null){
			if(withDiscountRemark){
				sql += " and qr.DiscountRemark is not null and qr.DiscountRemark <> '' ";
			}else{
				sql += " and (qr.DiscountRemark is null or qr.DiscountRemark = '') ";
			}
		}
		
		if(withOtherRemark != null){
			if(withOtherRemark){
				sql += " and a.OutletDiscountRemark is not null and a.OutletDiscountRemark <> '' ";
			}else{
				sql += " and (a.OutletDiscountRemark is null or a.OutletDiscountRemark = '') ";
			}
		}		
		
		if(withFieldwork != null){
			if(withFieldwork){
				sql += " and (iqr.isNoField is null or iqr.isNoField = 0) ";
			}else{
				sql += " and iqr.isNoField = 1 ";
			}
		}
		
		if(isPRNull != null){
			sql += " and (case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null "
					+ " else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice) * 100, 3) as decimal(10, 3)) end)";
			if(isPRNull){
				sql += " is null ";
			}else{
				sql += " is not null ";
			}
		}
		
		if(withIndoorConversionRemarks != null){
			if(withIndoorConversionRemarks){
				sql += " and iqr.remark is not null and iqr.remark <> '' ";
			}else{
				sql += " and (iqr.remark is null or iqr.remark = '') ";
			}
		}
		
		if(greaterThan != null || lessThan != null || equal != null){
			List<String> params = new ArrayList<String>();
			String calculatePercent = "round(iqr.CurrentSPrice / "
					+ "case when iqr.PreviousSPrice <= 0 then null else iqr.PreviousSPrice end"
					+ " * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(greaterThan != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(lessThan != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(equal != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			sql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and ("+StringUtils.join(params.toArray(), " or ")+")";
		}
		
		if(allocatedIndoorOfficer != null){
			if(allocatedIndoorOfficer && indoorOfficer !=null){
				sql += " and user_.UserId is not null and user_.UserId = :userId ";
			}else{
				sql += " and user_.UserId is null";
			}
		}
		
		if(StringUtils.isNotBlank(referenceDateCrit)) {
			sql += " and ( "
					+ " (" + referenceDate + " = :referenceDateCrit) "
					+ " or (iqr.referenceDate is null and " + referenceDate + " = :referenceDateCrit) "
					+ " ) ";
		}
		
		if(availability != null){
			sql += " and (case when qr.availability = 3 and qr.SPrice is null and qr.NPrice is null and qr.IsSPricePeculiar = 0 then 4 else "
					+ "case when qr.availability = 3 then 1 else "
					+ "case when qr.availability <> 3 then qr.availability end end end) = :availability ";
		}
		
		if(firmStatus != null){
			sql += " and qr.firmStatus = :firmStatus";
		}
		
		if(withDiscount != null){
			if(withDiscount){
				sql += " and qr.discount is not null and qr.discount <> '' ";
			}else{
				sql += " and (qr.discount is null or qr.discount = '') ";
			}
		}
		
		if(quotationId != null){
			sql += " and iqr.quotationId = :quotationId";
		}
		
		sql += " group by (iqr.IndoorQuotationRecordId)";
		
		if(subPrice != null){			
			sql += " having (case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end) = :subPrice ";			
		}
		

		sql = "select count(*) "
				+ " from IndoorQuotationRecord as iqr "
				+ " where iqr.IndoorQuotationRecordId in (" + sql + ") ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));		
		}
		query.setParameterList("status", status);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			query.setParameterList("outletTypeShortCode", outletTypeShortCode);
		}
		
		if (subGroupId != null){
			query.setParameter("subGroupId", subGroupId);
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			query.setParameter("outletCategory", outletCategory);
		}
		
		if (outletId != null ){
			query.setParameter("outletId", outletId);
		}
		
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		
		if (seasonalItem != null ){
			query.setParameter("seasonalItem", seasonalItem);
		}
		
		if (subPrice != null ){
			query.setParameter("subPrice", subPrice);
		}

		if (!StringUtils.isEmpty(surveyForm) ){
			query.setParameter("surveyForm", surveyForm);
		}
		
		if(greaterThan!= null){
			query.setParameter("greaterThan", greaterThan);
		}
		
		if(lessThan != null){
			query.setParameter("lessThan", lessThan);
		}
		
		if(equal != null){
			query.setParameter("equal", equal);
		}
		
		if(allocatedIndoorOfficer != null && allocatedIndoorOfficer){
			if(indoorOfficer != null){
				query.setParameter("userId", indoorOfficer);
			}
		}
		
		if(StringUtils.isNotEmpty(referenceDateCrit)){
			query.setParameter("referenceDateCrit", referenceDateCrit);
		}
		
		if(firmStatus != null){
			query.setParameter("firmStatus", firmStatus);
		}
		
		if(availability != null){
			query.setParameter("availability", availability);
		}
		
		if (quotationId != null){
			query.setParameter("quotationId", quotationId);
		}

//		return (Long) query.uniqueResult();
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public long countIndoorQuotationRecordDataConversionTableList(String search, Integer userId, String[] status,
			Integer purposeId, Date referenceMonth, Integer subGroupId, List<Integer> unitId, Integer outletId,
			List<String> outletTypeShortCode, Integer seasonalItem, String outletCategory, Boolean subPrice,
			Boolean outletCategoryRemark, Boolean priceRemark, String surveyForm,
			Boolean allocatedIndoorOfficer, Integer indoorOfficer, Double greaterThan, Double lessThan, Double equal,
			Boolean withPriceReason, Boolean withOtherRemark, Boolean withProductRemark, Boolean withDiscountRemark, Boolean withFieldwork,
			Boolean isPRNull, Integer firmStatus, Integer availability, Integer quotationId){
		
		String referenceDate = String.format("FORMAT(iqr.ReferenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = 
				"select iqr.IndoorQuotationRecordId "
				+ " from IndoorQuotationRecord iqr "
					+ "	left outer join QuotationRecord qr on iqr.QuotationRecordId = qr.QuotationRecordId "
					+ "	left outer join Assignment a on a.AssignmentId = qr.AssignmentId "
					+ " left outer join Quotation q on iqr.QuotationId = q.QuotationId "
					+ " left outer join Batch b on q.BatchId = b.BatchId "
					+ " left outer join Product p on q.ProductId = p.ProductId "
					+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
					+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
					+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
					+ " left outer join Outlet o on q.OutletId = o.OutletId "
					+ " left outer join Unit u on q.UnitId = u.UnitId "
					+ " left outer join SubItem si on u.SubItemId = si.SubItemId "
					+ " left outer join OutletType ot on si.OutletTypeId = ot.OutletTypeId "
					+ " left outer join Item i on ot.ItemId = i.ItemId "
					+ " left outer join SubGroup sg on i.SubGroupId = sg.SubGroupId "
					+ " left outer join SubPriceRecord spr on qr.QuotationRecordId = spr.QuotationRecordId "
					+ " left outer join Purpose pu on u.PurposeId = pu.PurposeId "
					+ " left outer join [User] as user_ on iqr.UserId = user_.UserId "
					+ " left outer join AssignmentUnitCategoryInfo as auc on a.AssignmentId = auc.AssignmentId "
					+ "  and u.UnitCategory = auc.UnitCategory "
				+ " where 1 = 1 "
				+ " and iqr.ReferenceMonth = :referenceMonth"
				+ " and pu.PurposeId = :purposeId"
				+ " and iqr.Status in (:status)";
		
		if (!StringUtils.isEmpty(search)){
			sql += " and ("
				+ " sg.ChineseName like :search "
				+ " or " + referenceDate + " like :search "
				+ " or sg.EnglishName like :search "
				+ " or u.ChineseName like :search "
				+ " or u.EnglishName like :search " 
				+ " or substring(ot.Code, len(ot.Code)-2, 3) like :search " 
				+ " or o.Name like :search " 
//				+ " or concat(pfs.specificationName,'=',pfs.value) like :search " 
				+ " or pa.SpecificationName like :search or ps.Value like :search " 
				+ " or (case when u.Seasonality = 1 then 'All-time' else"
				+ "		 case when u.Seasonality = 2 then 'Summer' else"
				+ "			 case when u.Seasonality = 3 then 'Winter' else "
				+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
				+ "			 end"
				+ "		 end"
				+ " end) like :search " 
				+ " or iqr.Status like :search " 
				+ " or qr.Reason like :search " 
				+ " or qr.ProductRemark like :search " 
				+ " or qr.Remark like :search " 
				+ " or str(iqr.IndoorQuotationRecordId) like :search "
				+ " or str(q.QuotationId) like :search "
				+ " or iqr.remark like :search " 
				+ " or a.OutletDiscountRemark like :search "
				+ " or qr.DiscountRemark like :search "
				+ " or u.UnitCategory like :search "
				+ " ) ";
		}
		
		if (userId != null)
			sql += " and user_.UserId = :userId";
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypeShortCode) ";
		}
		
		if (subGroupId != null){
			sql += " and sg.SubGroupId = :subGroupId ";
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			sql += " and u.UnitCategory = :outletCategory ";
		}
		
		if(outletId != null){
			sql += " and o.OutletId = :outletId ";
		}
		
		if(unitId != null && unitId.size() > 0){
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if(seasonalItem != null){
			sql += " and  u.Seasonality  = :seasonalItem ";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			sql += " and  b.SurveyForm  = :surveyForm ";
		}

		if(priceRemark != null && priceRemark){
			sql += " and (qr.Remark is not null and qr.Remark != '' )";
		}else if(priceRemark != null && !priceRemark){
			sql += " and (qr.Remark is null or qr.Remark = '' )";
		}
		
		if(outletCategoryRemark != null && outletCategoryRemark){
			sql += " and (qr.CategoryRemark is not null and qr.CategoryRemark != '' )";
		}else if(outletCategoryRemark != null && !outletCategoryRemark){
			sql += " and (qr.CategoryRemark is null or qr.CategoryRemark = '' )";
		}
		

		if(withPriceReason != null){
			if(withPriceReason){
				sql += " and qr.Reason is not null and qr.Reason <> '' ";
			}else{
				sql += " and (qr.Reason is null or qr.Reason = '') ";
			}
		}
		
		if(withProductRemark != null){
			if(withProductRemark){
				sql += " and qr.ProductRemark is not null and qr.ProductRemark <> '' ";
			}else{
				sql += " and (qr.ProductRemark is null or qr.ProductRemark = '') ";
			}
		}
		
		if(withDiscountRemark != null){
			if(withDiscountRemark){
				sql += " and qr.DiscountRemark is not null and qr.DiscountRemark <> '' ";
			}else{
				sql += " and (qr.DiscountRemark is null or qr.DiscountRemark = '') ";
			}
		}
		
		if(withOtherRemark != null){
			if(withOtherRemark){
				sql += " and a.OutletDiscountRemark is not null and a.OutletDiscountRemark <> '' ";
			}else{
				sql += " and (a.OutletDiscountRemark is null or a.OutletDiscountRemark = '') ";
			}
		}		
		
		if(withFieldwork != null){
			if(withFieldwork){
				sql += " and (iqr.isNoField is null or iqr.isNoField = 0) ";
			}else{
				sql += " and iqr.isNoField = 1 ";
			}
		}
		
		if(greaterThan != null || lessThan != null || equal != null){
			List<String> params = new ArrayList<String>();
			String calculatePercent = "round((iqr.currentSPrice / "
					+ " case when iqr.previousSPrice <= 0 then null else iqr.previousSPrice end"
					+ " ) * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(greaterThan != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(lessThan != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(equal != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			sql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and ("+StringUtils.join(params.toArray(), " or ")+")";
		}
		

		if(allocatedIndoorOfficer != null){
			if(allocatedIndoorOfficer && indoorOfficer !=null){
				sql += " and user_.UserId is not null and user_.UserId = :userId ";
			}else{
				sql += " and user_.UserId is null";
			}
		}
		
		if(isPRNull != null){
			sql += " and (case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null "
				+ " else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice ) * 100, 3) as decimal(10, 3)) end) ";
			if(isPRNull){
				sql += " is null ";
			}else{
				sql += " is not null ";
			}
		}
		
		if (firmStatus != null){
			sql += " and qr.FirmStatus = :firmStatus ";
		}
		
		if (availability != null){
			sql += " and (case when qr.availability = 3 and qr.SPrice is null and qr.NPrice is null and qr.IsSPricePeculiar = 0 then 4 else "
					+ "case when qr.availability = 3 then 1 else "
					+ "case when qr.availability <> 3 then qr.availability end end end) = :availability ";
		}
		
		if(quotationId != null){
			sql += " and iqr.QuotationId = :quotationId ";
		}
		
		sql += " group by (iqr.IndoorQuotationRecordId)";
		
		if(subPrice != null){			
			sql += " having (case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end) = :subPrice ";			
		}
		

		sql = "select count(*) "
				+ " from IndoorQuotationRecord as iqr "
				+ " where iqr.IndoorQuotationRecordId in (" + sql + ") ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));		
		}
		query.setParameterList("status", status);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			query.setParameterList("outletTypeShortCode", outletTypeShortCode);
		}
		
		if (subGroupId != null){
			query.setParameter("subGroupId", subGroupId);
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			query.setParameter("outletCategory", outletCategory);
		}
		
		if (outletId != null ){
			query.setParameter("outletId", outletId);
		}
		
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		
		if (seasonalItem != null ){
			query.setParameter("seasonalItem", seasonalItem);
		}
		
		if (subPrice != null ){
			query.setParameter("subPrice", subPrice);
		}

		if (!StringUtils.isEmpty(surveyForm) ){
			query.setParameter("surveyForm", surveyForm);
		}
		
		if(greaterThan!= null){
			query.setParameter("greaterThan", greaterThan);
		}
		
		if(lessThan != null){
			query.setParameter("lessThan", lessThan);
		}
		
		if(equal != null){
			query.setParameter("equal", equal);
		}
		
		if(allocatedIndoorOfficer != null && allocatedIndoorOfficer){
			if(indoorOfficer != null){
				query.setParameter("userId", indoorOfficer);
			}
		}
		
		if (firmStatus != null){
			query.setParameter("firmStatus", firmStatus);
		}
		
		if (availability != null){
			query.setParameter("availability", availability);			
		}
		
		if (quotationId != null){
			query.setParameter("quotationId", quotationId);
		}
		
//		return (Long) query.uniqueResult();
		Integer count = (Integer)query.uniqueResult();
		return count == null ? 0 : count;
	}
	
	public List<Integer> getConversionTableIds(String search,
			Integer userId, String[] indoorQuotationRecordStatus, Integer purposeId, Date referenceMonth, Integer subGroupId, List<Integer> unitId, Integer outletId,
			List<String> outletTypeShortCode, Integer seasonalItem, String outletCategory, Boolean subPrice,
			Boolean outletCategoryRemark, Boolean priceRemark, String surveyForm, 
			Boolean allocatedIndoorOfficer, Integer indoorOfficer, Double greaterThan, Double lessThan, Double equal,
			Boolean withPriceReason, Boolean withOtherRemark, Boolean withProductRemark, Boolean withDiscountRemark,
			Boolean withFieldwork, Boolean isPRNull, Integer firmStatus, Integer availability, Integer quotationId,
			Order order){
		
		String referenceDate = String.format("FORMAT(iqr.ReferenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = 
			"select iqr.IndoorQuotationRecordId as indoorQuotationRecordId "
				+ " from IndoorQuotationRecord iqr "
				+ "	left outer join QuotationRecord qr on iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "	left outer join Assignment a on a.AssignmentId = qr.AssignmentId "
				+ " left outer join Quotation q on iqr.QuotationId = q.QuotationId "
				+ " left outer join Batch b on q.BatchId = b.BatchId "
				+ " left outer join Product p on q.ProductId = p.ProductId "
				+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
				+ " left outer join Outlet o on q.OutletId = o.OutletId "
				+ " left outer join Unit u on q.UnitId = u.UnitId "
				+ " left outer join SubItem si on u.SubItemId = si.SubItemId "
				+ " left outer join OutletType ot on si.OutletTypeId = ot.OutletTypeId "
				+ " left outer join Item i on ot.ItemId = i.ItemId "
				+ " left outer join SubGroup sg on i.SubGroupId = sg.SubGroupId "
				+ " left outer join SubPriceRecord spr on qr.QuotationRecordId = spr.QuotationRecordId "
				+ " left outer join Purpose pu on u.PurposeId = pu.PurposeId "
				+ " left outer join [User] as user_ on iqr.UserId = user_.UserId "
				+ " left outer join AssignmentUnitCategoryInfo as auc on a.AssignmentId = auc.AssignmentId "
				+ "  and u.UnitCategory = auc.UnitCategory "
			+ " where 1=1"
			+ " and iqr.ReferenceMonth = :referenceMonth"
			+ " and pu.PurposeId = :purposeId"
			+ " and iqr.Status in (:status)";
		
		if (!StringUtils.isEmpty(search)){
			sql +=" and ("
				+ " sg.ChineseName like :search "
				+ " or sg.EnglishName like :search "
				+ " or u.ChineseName like :search "
				+ " or u.EnglishName like :search " 
				+ " or substring(ot.Code, len(ot.Code)-2, 3) like :search " 
				+ " or o.Name like :search " 
				+ " or pa.SpecificationName like :search or ps.Value like :search "
				+ " or (case when u.Seasonality = 1 then 'All-time' else"
				+ "		 case when u.Seasonality = 2 then 'Summer' else"
				+ "			 case when u.Seasonality = 3 then 'Winter' else "
				+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
				+ "			 end"
				+ "		 end"
				+ " end) like :search " 
				+ " or " + referenceDate + " like :search "
				+ " or iqr.Status like :search " 
				+ " or qr.Reason like :search " 
				+ " or qr.ProductRemark like :search " 
				+ " or qr.Remark like :search "
				+ " or str(iqr.IndoorQuotationRecordId) like :search "
				+ " or str(q.QuotationId) like :search " 
				+ " or iqr.remark like :search " 
				+ " or a.OutletDiscountRemark like :search "
				+ " or qr.DiscountRemark like :search "
				+ " or u.UnitCategory like :search "
				+ " ) ";
		}
		
		if (userId != null)
			sql += " and user_.UserId = :userId";
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypeShortCode) ";
		}
		
		if (subGroupId != null){
			sql += " and sg.SubGroupId = :subGroupId ";
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			sql += " and u.UnitCategory = :outletCategory ";
		}
		
		if(outletId != null){
			sql += " and o.OutletId = :outletId ";
		}
		
		if(unitId != null && unitId.size() > 0){
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if(seasonalItem != null){
			sql += " and  u.Seasonality  = :seasonalItem ";
		}
		
		if(priceRemark != null && priceRemark){
			sql += " and (qr.Remark is not null and qr.Remark != '' )";
		}else if(priceRemark != null && !priceRemark){
			sql += " and (qr.Remark is null or qr.Remark = '' )";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			sql += " and b.SurveyForm = :surveyForm ";
		}
		
		if(outletCategoryRemark != null && outletCategoryRemark){
			sql += " and (qr.CategoryRemark is not null and qr.CategoryRemark != '' )";
		}else if(outletCategoryRemark != null && !outletCategoryRemark){
			sql += " and (qr.CategoryRemark is null or qr.CategoryRemark = '' )";
		}
		
		if(withPriceReason != null){
			if(withPriceReason){
				sql += " and qr.Reason is not null and qr.Reason <> '' ";
			}else{
				sql += " and (qr.Reason is null or qr.Reason = '') ";
			}
		}
		
		if(withProductRemark != null){
			if(withProductRemark){
				sql += " and qr.ProductRemark is not null and qr.ProductRemark <> '' ";
			}else{
				sql += " and (qr.ProductRemark is null or qr.ProductRemark = '') ";
			}
		}
		
		if(withDiscountRemark != null){
			if(withDiscountRemark){
				sql += " and qr.DiscountRemark is not null and qr.DiscountRemark <> '' ";
			}else{
				sql += " and (qr.DiscountRemark is null or qr.DiscountRemark = '') ";
			}
		}
		
		if(withOtherRemark != null){
			if(withOtherRemark){
				sql += " and a.OutletDiscountRemark is not null and a.OutletDiscountRemark <> '' ";
			}else{
				sql += " and (a.OutletDiscountRemark is null or a.OutletDiscountRemark = '') ";
			}
		}		
		
		if(withFieldwork != null){
			if(withFieldwork){
				sql += " and (iqr.isNoField is null or iqr.isNoField = 0) ";
			}else{
				sql += " and iqr.isNoField = 1 ";
			}
		}
		
		if(greaterThan != null || lessThan != null || equal != null){
			List<String> params = new ArrayList<String>();
			String calculatePercent = "round((iqr.currentSPrice / "
					+ " case when iqr.previousSPrice <= 0 then null else iqr.previousSPrice end"
					+ " ) * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(greaterThan != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(lessThan != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(equal != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			sql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and ("+StringUtils.join(params.toArray(), " or ")+")";
		}
		

		if(allocatedIndoorOfficer != null){
			if(allocatedIndoorOfficer && indoorOfficer !=null){
				sql += " and user_.UserId is not null and user_.UserId = :indoorOfficer ";
			}else{
				sql += " and user_.UserId is null";
			}
		}
		
		if(isPRNull != null){
			sql += " and (case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null  "
					+ "else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice ) * 100, 3) as decimal(10, 3)) end) ";
			if(isPRNull){
				sql += " is null ";
			}else{
				sql += " is not null ";
			}
		}
		
		if (firmStatus != null){
			sql += " and qr.FirmStatus = :firmStatus ";
		}
		
		if (availability != null){
			sql += " and (case when qr.availability = 3 and qr.SPrice is null and qr.NPrice is null and qr.IsSPricePeculiar = 0 then 4 else "
					+ "case when qr.availability = 3 then 1 else "
					+ "case when qr.availability <> 3 then qr.availability end end end) = :availability ";
		}
		
		if (quotationId != null){
			sql += " and iqr.QuotationId = :quotationId ";
		}
		
		sql += " group by iqr.IndoorQuotationRecordId, iqr.IsFlag, iqr.ReferenceDate, sg.ChineseName, sg.EnglishName,"
			+ " u.ChineseName, u.EnglishName, o.Name, concat(pa.SpecificationName,'=',ps.Value),"
			+ " iqr.Status, "
			+ " qr.Reason, qr.Remark, qr.ProductRemark,"
			+ " substring(ot.Code, len(ot.Code)-2, 3), u.Seasonality, "
			+ " q.QuotationId, iqr.remark, "
			+ " iqr.CurrentSPrice, iqr.PreviousSPrice, a.OutletDiscountRemark, qr.DiscountRemark, u.UnitCategory ";
			
		if(subPrice != null){
			sql += " having (case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end) = :subPrice ";
		}
		
		sql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		
		if (!"iqr.indoorQuotationRecordId".equals(order.getPropertyName())){
			sql += ", iqr.indoorQuotationRecordId asc";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);		
		query.setParameterList("status", indoorQuotationRecordStatus);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			query.setParameterList("outletTypeShortCode", outletTypeShortCode);
		}
		
		if (subGroupId != null){
			query.setParameter("subGroupId", subGroupId);
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			query.setParameter("outletCategory", outletCategory);
		}
		
		if (outletId != null ){
			query.setParameter("outletId", outletId);
		}
		
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		
		if (seasonalItem != null ){
			query.setParameter("seasonalItem", seasonalItem);
		}
		
		if (subPrice != null ){
			query.setParameter("subPrice", subPrice);
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}
		
		if(greaterThan!= null){
			query.setParameter("greaterThan", greaterThan);
		}
		
		if(lessThan != null){
			query.setParameter("lessThan", lessThan);
		}
		
		if(equal != null){
			query.setParameter("equal", equal);
		}
		
		if(allocatedIndoorOfficer != null && allocatedIndoorOfficer){
			if(indoorOfficer != null){
				query.setParameter("indoorOfficer", indoorOfficer);
			}
		}
		
		if (firmStatus != null){
			query.setParameter("firmStatus", firmStatus);
		}
		
		if (availability != null){
			query.setParameter("availability", availability);			
		}
		
		if (quotationId != null){
			query.setParameter("quotationId", quotationId);
		}
		
		query.addScalar("indoorQuotationRecordId", StandardBasicTypes.INTEGER);
		
		return query.list();

	}
	
	public List<Integer> getReviewTableIds(String search,
			Integer userId, String[] indoorQuotationRecordStatus, Integer purposeId, Date referenceMonth, Integer subGroupId, List<Integer> unitId, Integer outletId,
			List<String> outletTypeShortCode, Integer seasonalItem, String outletCategory, Boolean subPrice,
			Boolean outletCategoryRemark, Boolean priceRemark, String surveyForm, 
			Boolean allocatedIndoorOfficer, Integer indoorOfficer, Double greaterThan, Double lessThan, Double equal,
			Boolean withPriceReason, Boolean withOtherRemark, Boolean withProductRemark, Boolean withDiscountRemark,
			Boolean withFieldwork, Boolean isPRNull, Boolean withIndoorConversionRemarks, String referenceDateCrit,
			Integer firmStatus, Integer availability, Boolean withDiscount, Integer quotationId){
		String referenceDate = String.format("FORMAT(iqr.ReferenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String sql = 
			"select iqr.IndoorQuotationRecordId as indoorQuotationRecordId "
				+ " from IndoorQuotationRecord iqr "
				+ "	left outer join QuotationRecord qr on iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "	left outer join Assignment a on a.AssignmentId = qr.AssignmentId "
				+ " left outer join Quotation q on iqr.QuotationId = q.QuotationId "
				+ " left outer join Batch b on q.BatchId = b.BatchId "
				+ " left outer join Product p on q.ProductId = p.ProductId "
				+ " left outer join ProductGroup as pg on p.ProductGroupId = pg.ProductGroupId "
				+ " left outer join ProductAttribute as pa on pa.ProductGroupId = pg.ProductGroupId and pa.Sequence = 1 "
				+ " left outer join ProductSpecification as ps on ps.ProductAttributeId = pa.ProductAttributeId and ps.ProductId = p.ProductId "
				+ " left outer join Outlet o on q.OutletId = o.OutletId "
				+ " left outer join Unit u on q.UnitId = u.UnitId "
				+ " left outer join SubItem si on u.SubItemId = si.SubItemId "
				+ " left outer join OutletType ot on si.OutletTypeId = ot.OutletTypeId "
				+ " left outer join Item i on ot.ItemId = i.ItemId "
				+ " left outer join SubGroup sg on i.SubGroupId = sg.SubGroupId "
				+ " left outer join SubPriceRecord spr on qr.QuotationRecordId = spr.QuotationRecordId "
				+ " left outer join Purpose pu on u.PurposeId = pu.PurposeId "
				+ " left outer join [User] as user_ on iqr.UserId = user_.UserId "
				+ " left outer join AssignmentUnitCategoryInfo as auc on a.AssignmentId = auc.AssignmentId "
				+ "  and u.UnitCategory = auc.UnitCategory "
			+ " where 1=1"
			+ " and iqr.ReferenceMonth = :referenceMonth"
			+ " and pu.PurposeId = :purposeId"
			+ " and iqr.Status in (:status)";
		
		if (!StringUtils.isEmpty(search)){
			sql +=" and ("
				+ " sg.ChineseName like :search "
				+ " or sg.EnglishName like :search "
				+ " or u.ChineseName like :search "
				+ " or u.EnglishName like :search " 
				+ " or substring(ot.Code, len(ot.Code)-2, 3) like :search " 
				+ " or o.Name like :search " 
				+ " or pa.SpecificationName like :search or ps.Value like :search "
				+ " or (case when u.Seasonality = 1 then 'All-time' else"
				+ "		 case when u.Seasonality = 2 then 'Summer' else"
				+ "			 case when u.Seasonality = 3 then 'Winter' else "
				+ "				case when u.Seasonality = 4 then 'Occasional' else '' end"
				+ "			 end"
				+ "		 end"
				+ " end) like :search " 
				+ " or qr.Reason like :search " 
				+ " or qr.ProductRemark like :search " 
				+ " or qr.Remark like :search "
				+ " or str(iqr.IndoorQuotationRecordId) like :search "
				+ " or str(q.QuotationId) like :search " 
				+ " or iqr.remark like :search " 
				+ " or a.OutletDiscountRemark like :search "
				+ " or qr.DiscountRemark like :search "
				+ " or u.UnitCategory like :search "
				+ " or " + referenceDate + " like :search "
				+ " ) ";
		}
		
		if (userId != null)
			sql += " and user_.UserId = :userId";
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypeShortCode) ";
		}
		
		if (subGroupId != null){
			sql += " and sg.SubGroupId = :subGroupId ";
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			sql += " and u.UnitCategory = :outletCategory ";
		}
		
		if(outletId != null){
			sql += " and o.OutletId = :outletId ";
		}
		
		if(unitId != null && unitId.size() > 0){
			sql += " and u.UnitId in (:unitId) ";
		}
		
		if(seasonalItem != null){
			sql += " and u.Seasonality = :seasonalItem ";
		}
		
		if(priceRemark != null && priceRemark){
			sql += " and (qr.Remark is not null and qr.Remark != '' )";
		}else if(priceRemark != null && !priceRemark){
			sql += " and (qr.Remark is null or qr.Remark = '' )";
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			sql += " and b.SurveyForm = :surveyForm ";
		}
		
		if(outletCategoryRemark != null && outletCategoryRemark){
			sql += " and (qr.CategoryRemark is not null and qr.CategoryRemark != '' )";
		}else if(outletCategoryRemark != null && !outletCategoryRemark){
			sql += " and (qr.CategoryRemark is null or qr.CategoryRemark = '' )";
		}
		
		if(withPriceReason != null){
			if(withPriceReason){
				sql += " and qr.Reason is not null and qr.Reason <> '' ";
			}else{
				sql += " and (qr.Reason is null or qr.Reason = '') ";
			}
		}
		
		if(withProductRemark != null){
			if(withProductRemark){
				sql += " and qr.ProductRemark is not null and qr.ProductRemark <> '' ";
			}else{
				sql += " and (qr.ProductRemark is null or qr.ProductRemark = '') ";
			}
		}
		
		if(withDiscountRemark != null){
			if(withDiscountRemark){
				sql += " and qr.DiscountRemark is not null and qr.DiscountRemark <> '' ";
			}else{
				sql += " and (qr.DiscountRemark is null or qr.DiscountRemark = '') ";
			}
		}
		
		if(withOtherRemark != null){
			if(withOtherRemark){
				sql += " and a.OutletDiscountRemark is not null and a.OutletDiscountRemark <> '' ";
			}else{
				sql += " and (a.OutletDiscountRemark is null or a.OutletDiscountRemark = '') ";
			}
		}		
		
		if(withFieldwork != null){
			if(withFieldwork){
				sql += " and (iqr.isNoField is null or iqr.isNoField = 0) ";
			}else{
				sql += " and iqr.isNoField = 1 ";
			}
		}
		
		if(isPRNull != null){
			sql += " and (case when iqr.CurrentSPrice is null or iqr.PreviousSPrice is null or iqr.PreviousSPrice <= 0 then null  "
					+ "else cast(round((iqr.CurrentSPrice / iqr.PreviousSPrice ) * 100, 3) as decimal(10, 3)) end) ";
			if(isPRNull){
				sql += " is null ";
			}else{
				sql += " is not null ";
			}
		}
		
		if(withIndoorConversionRemarks != null){
			if(withIndoorConversionRemarks){
				sql += " and iqr.remark is not null and iqr.remark <> '' ";
			}else{
				sql += " and (iqr.remark is null or iqr.remark = '') ";
			}
		}
		
		if(greaterThan != null || lessThan != null || equal != null){
			List<String> params = new ArrayList<String>();
			String calculatePercent = "round(iqr.CurrentSPrice / "
					+ "case when iqr.PreviousSPrice <= 0 then null else iqr.PreviousSPrice end"
					+ " * 100, 3)";
			String preparedQuery = " (" + calculatePercent + " {0}) ";
			
			if(greaterThan != null){
				params.add(preparedQuery.replace("{0}", "> :greaterThan"));
			}
			if(lessThan != null){
				params.add(preparedQuery.replace("{0}", "< :lessThan"));
			}
			if(equal != null){
				params.add(preparedQuery.replace("{0}", "= :equal"));
			}
			
			sql += " and iqr.currentSPrice is not null and iqr.previousSPrice is not null "
					+ " and ("+StringUtils.join(params.toArray(), " or ")+")";
		}
		

		if(allocatedIndoorOfficer != null){
			if(allocatedIndoorOfficer && indoorOfficer !=null){
				sql += " and user_.UserId is not null and user_.UserId = :indoorOfficer ";
			}else{
				sql += " and user_.UserId is null";
			}
		}
		
		if (StringUtils.isNotBlank(referenceDateCrit)) {
			sql += " and ( "
					+ " (" + referenceDate + " = :referenceDateCrit) "
					+ " or (iqr.referenceDate is null and " + referenceDate + " = :referenceDateCrit) "
					+ " ) ";
		}
		
		if (firmStatus != null){
			sql += " and qr.FirmStatus = :firmStatus ";
		}
		
		if (availability != null){
			sql += " and (case when qr.availability = 3 and qr.SPrice is null and qr.NPrice is null and qr.IsSPricePeculiar = 0 then 4 else "
					+ "case when qr.availability = 3 then 1 else "
					+ "case when qr.availability <> 3 then qr.availability end end end) = :availability ";
		}
		
		if(withDiscount != null){
			if(withDiscount){
				sql += " and qr.discount is not null and qr.discount <> '' ";
			}else{
				sql += " and (qr.discount is null or qr.discount = '') ";
			}
		}
		
		if(quotationId != null){
			sql += " and iqr.QuotationId = :quotationId ";
		}
		
			sql += " group by iqr.IndoorQuotationRecordId, iqr.IsFlag, iqr.referenceDate, sg.ChineseName, sg.EnglishName,"
				+ " u.ChineseName, u.EnglishName, o.Name, concat(pa.SpecificationName,'=',ps.Value),"
				+ " qr.Reason, qr.Remark, qr.ProductRemark,"
				+ " substring(ot.Code, len(ot.Code)-2, 3), u.Seasonality, "
				+ " q.QuotationId, iqr.remark, "
				+ " iqr.CurrentSPrice, iqr.PreviousSPrice, a.OutletDiscountRemark, qr.DiscountRemark, u.UnitCategory ";
			
		if(subPrice != null){
			sql += " having (case when count( distinct spr.SubPriceRecordId ) > 0 then 1 else 0 end) = :subPrice ";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if (!StringUtils.isEmpty(search)){
			query.setParameter("search", String.format("%%%s%%", search));
		}
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);		
		query.setParameterList("status", indoorQuotationRecordStatus);
		
		if (userId != null)
			query.setParameter("userId", userId);
		
		if (outletTypeShortCode != null && outletTypeShortCode.size() > 0){
			query.setParameterList("outletTypeShortCode", outletTypeShortCode);
		}
		
		if (subGroupId != null){
			query.setParameter("subGroupId", subGroupId);
		}
		
		if (!StringUtils.isEmpty(outletCategory)){
			query.setParameter("outletCategory", outletCategory);
		}
		
		if (outletId != null ){
			query.setParameter("outletId", outletId);
		}
		
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		
		if (seasonalItem != null ){
			query.setParameter("seasonalItem", seasonalItem);
		}
		
		if (subPrice != null ){
			query.setParameter("subPrice", subPrice);
		}
		
		if (!StringUtils.isEmpty(surveyForm)){
			query.setParameter("surveyForm", surveyForm);
		}
		
		if(greaterThan!= null){
			query.setParameter("greaterThan", greaterThan);
		}
		
		if(lessThan != null){
			query.setParameter("lessThan", lessThan);
		}
		
		if(equal != null){
			query.setParameter("equal", equal);
		}
		
		if(allocatedIndoorOfficer != null && allocatedIndoorOfficer){
			if(indoorOfficer != null){
				query.setParameter("indoorOfficer", indoorOfficer);
			}
		}
		
		if (StringUtils.isNotEmpty(referenceDateCrit)){
			query.setParameter("referenceDateCrit", referenceDateCrit);
		}
		
		if (firmStatus != null){
			query.setParameter("firmStatus", firmStatus);
		}
		
		if (availability != null){
			query.setParameter("availability", availability);			
		}
				
		if (quotationId != null){
			query.setParameter("quotationId", quotationId);
		}
		
		query.addScalar("indoorQuotationRecordId", StandardBasicTypes.INTEGER);
		
		return query.list();
	}

	public List<IndoorStaffProgress> getIndoorStaffProgress(List<Integer> user, List<Integer> purpose, 
			List<Integer> cpiSurveyForm, List<Integer> batch, Date startMonth, Date endMonth, 
			List<String> cpiBasePeriods){
		
		String sql = "Select ur.StaffCode as staffCode,"
				+" ur.EnglishName as name,"
				+" iqr.ReferenceMonth as referenceMonth,"
				+" pp.Code as purpose,"
				+" u.CPIBasePeriod as cpiBasePeriod,"
				+"	SUM(case when iqr.[Status] in ('Conversion', 'Complete', 'Request Verification'" 
					+" , 'Approve Verification', 'Reject Verification', 'Review Verification') then 1 else 0 end) as allocator" 
					+"	, SUM(case when iqr.[Status] in ('Conversion', 'Reject Verification', 'Request Verification') then 1 else 0 end) as conversion"  
				+"	, SUM(case when iqr.[Status] = 'Complete' then 1 else 0 end) as complete" 
				+"	, SUM(case when iqr.[Status] in ('Approve Verification', 'Review Verification') then 1 else 0 end) as verification"
				+" from IndoorQuotationRecord as iqr"
				+" left join QuotationRecord as qr on iqr.QuotationRecordId = qr.QuotationRecordId"
				+" left join Quotation as q on iqr.QuotationId = q.QuotationId"
				+" left join Unit as u on q.UnitId = u.UnitId"
				+" left join Purpose as pp on u.PurposeId = pp.PurposeId"
				+" left join [User] as ur on ur.UserId = iqr.UserId"
				+" where iqr.ReferenceMonth between :startMonth and :endMonth";
		
		
		if (user != null && user.size() > 0){
			sql += " and iqr.userId in (:user) ";
		}
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		if (batch != null && batch.size() > 0){
			sql += " and q.batchId in (:batch) ";
		}
		
		if (cpiBasePeriods != null && cpiBasePeriods.size() > 0){
			sql += " and u.cpiBasePeriod in (:cpiBasePeriods) ";
		}
		
		sql += " group by ur.staffCode, ur.EnglishName, iqr.userId, iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod ";
		
		sql += " order by iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod, ur.StaffCode";
		
		
		SQLQuery query = this.getSession().createSQLQuery(sql);

		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (user != null && user.size() > 0){
			query.setParameterList("user", user);
		}
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		if (batch != null && batch.size() > 0){
			query.setParameterList("batch", batch);
		}
		if (cpiBasePeriods != null && cpiBasePeriods.size() > 0){
			query.setParameterList("cpiBasePeriods", cpiBasePeriods);
		}
		
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("staffCode", StandardBasicTypes.STRING);
		query.addScalar("name", StandardBasicTypes.STRING);
		query.addScalar("allocator", StandardBasicTypes.LONG);
		query.addScalar("conversion", StandardBasicTypes.LONG);
		query.addScalar("complete", StandardBasicTypes.LONG);
		query.addScalar("verification", StandardBasicTypes.LONG);
		query.setResultTransformer(Transformers.aliasToBean(IndoorStaffProgress.class));
		
		return query.list();
	}
	
	public List<IndoorStaffProgress> getIndoorStaffOverAllProgress(List<Integer> user, List<Integer> purpose, 
			List<Integer> cpiSurveyForm, List<Integer> batch, Date startMonth, Date endMonth, 
			List<String> cpiBasePeriods){
		
		String sql = "Select iqr.ReferenceMonth as referenceMonth ,"
				+ " pp.Code as purpose 	,"
				+ " u.CPIBasePeriod as cpiBasePeriod 	,"
				+"	SUM(case when iqr.[Status] in ('Conversion', 'Complete', 'Request Verification'" 
					+" , 'Approve Verification', 'Reject Verification', 'Review Verification') then 1 else 0 end) as allocator" 
					+"	, SUM(case when iqr.[Status] in ('Conversion', 'Reject Verification', 'Request Verification') then 1 else 0 end) as conversion"  
				+"	, SUM(case when iqr.[Status] = 'Complete' then 1 else 0 end) as complete" 
				+"	, SUM(case when iqr.[Status] in ('Approve Verification', 'Review Verification') then 1 else 0 end) as verification"
				+ " from IndoorQuotationRecord as iqr 	"
				+ " left join QuotationRecord as qr on iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "	left join Quotation as q on iqr.QuotationId = q.QuotationId "
				+ "	left join Unit as u on q.UnitId = u.UnitId "
				+ "	left join Purpose as pp on u.PurposeId = pp.PurposeId "
				+ " left join [User] as ur on ur.UserId = iqr.UserId "
				+" where iqr.ReferenceMonth between :startMonth and :endMonth";
		
		if (user != null && user.size() > 0) {
			sql += " and iqr.userId in (:user)";
		}
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		if (batch != null && batch.size() > 0){
			sql += " and q.batchId in (:batch) ";
		}
		
		if (cpiBasePeriods != null && cpiBasePeriods.size() > 0){
			sql += " and u.cpiBasePeriod in (:cpiBasePeriods) ";
		}
		
		sql += " group by iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod ";
		
		sql += " order by iqr.ReferenceMonth, pp.Code, u.CPIBasePeriod";
		
		
		SQLQuery query = this.getSession().createSQLQuery(sql);

		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (user != null && user.size() > 0){
			query.setParameterList("user", user);
		}
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		if (batch != null && batch.size() > 0){
			query.setParameterList("batch", batch);
		}
		if (cpiBasePeriods != null && cpiBasePeriods.size() > 0){
			query.setParameterList("cpiBasePeriods", cpiBasePeriods);
		}
		
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("allocator", StandardBasicTypes.LONG);
		query.addScalar("conversion", StandardBasicTypes.LONG);
		query.addScalar("complete", StandardBasicTypes.LONG);
		query.addScalar("verification", StandardBasicTypes.LONG);
		query.setResultTransformer(Transformers.aliasToBean(IndoorStaffProgress.class));
		
		return query.list();
	}
	
	public IndoorQuotationRecord getLatestIndoorByQuotation(int quotationId, Date referenceDate) {
		return (IndoorQuotationRecord)this.createCriteria()
				.add(Restrictions.eq("quotation.id", quotationId))
				.add(Restrictions.lt("referenceDate", referenceDate))
				.addOrder(Order.desc("referenceDate"))
				.setMaxResults(1)
				.uniqueResult();
	}
	
	public List<IndoorQuotationRecord> getIndoorQuotationRecordsByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("indoorQuotationRecordId", ids));
		return criteria.list();
	}
	
	public List<OutrangedQuotationRecords> getOutrangedQuotationRecordsReport(List<Integer> purpose, List<Integer> itemId, 
			List<Integer> cpiSurveyForm, Date refMonth, String refMonthStr, String dataCollection, String dynamicCondition) {

		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[OutrangedQuotationRecordsReport] :refMonth, :refMonthStr, :IsNoField, :purposeId, :cpiQuotationType, :itemId, :prCriteria");
		
		query.setParameter("refMonth", refMonth);
		query.setParameter("refMonthStr", refMonthStr);
		if(StringUtils.isNotEmpty(dataCollection)) {
			if(dataCollection.equals("Y")) {
				query.setParameter("IsNoField", 0);
			}else if(dataCollection.equals("N")) {
				query.setParameter("IsNoField", 1);
			}else {
				query.setParameter("IsNoField", null);
			}
		}else {
			query.setParameter("IsNoField", null);
		}
		
		StringBuilder builder = new StringBuilder();
		if(purpose != null) {
			builder.append("<query>");
			for (Integer purposeId : purpose){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");		
			query.setParameter("purposeId", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("purposeId", null);
		}
		if(cpiSurveyForm != null) {
			builder.append("<query>");
			for (Integer cpiQuotationType : cpiSurveyForm){
				builder.append("<cpiQuotationType>"+cpiQuotationType+"</cpiQuotationType>");
			}
			builder.append("</query>");		
			query.setParameter("cpiQuotationType", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("cpiQuotationType", null);
		}
		if(itemId != null) {
			builder.append("<query>");
			for(Integer id : itemId) {
				builder.append("<itemId>"+id+"</itemId>");
			}
			builder.append("</query>");
			query.setParameter("itemId", builder.toString());
		}else {
			query.setParameter("itemId", null);
		}
		if(dynamicCondition != null && dynamicCondition.length() > 0) {
			query.setParameter("prCriteria",dynamicCondition);
		} else {
			query.setParameter("prCriteria",null);
		}
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("referenceDate", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("groupCode", StandardBasicTypes.STRING);
		query.addScalar("groupName", StandardBasicTypes.STRING);
		query.addScalar("itemCode", StandardBasicTypes.STRING);
		query.addScalar("itemName", StandardBasicTypes.STRING);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("unitName", StandardBasicTypes.STRING);
		query.addScalar("quotationId", StandardBasicTypes.STRING);
		query.addScalar("quotationRecordId", StandardBasicTypes.STRING);
		query.addScalar("indoorQuotationRecordId", StandardBasicTypes.STRING);
		query.addScalar("outletCode", StandardBasicTypes.STRING);
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("district", StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode", StandardBasicTypes.STRING);
		query.addScalar("outletTypeName", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.STRING);
		query.addScalar("countryOfOrigin", StandardBasicTypes.STRING);
		query.addScalar("pa1Value", StandardBasicTypes.STRING);
		query.addScalar("pa2Value", StandardBasicTypes.STRING);
		query.addScalar("pa3Value", StandardBasicTypes.STRING);
		query.addScalar("pa4Value", StandardBasicTypes.STRING);
		query.addScalar("pa5Value", StandardBasicTypes.STRING);
		query.addScalar("ps1Value", StandardBasicTypes.STRING);
		query.addScalar("ps2Value", StandardBasicTypes.STRING);
		query.addScalar("ps3Value", StandardBasicTypes.STRING);
		query.addScalar("ps4Value", StandardBasicTypes.STRING);
		query.addScalar("ps5Value", StandardBasicTypes.STRING);
		query.addScalar("lastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("indoorRemark", StandardBasicTypes.STRING);
		query.addScalar("qrRemark", StandardBasicTypes.STRING);
		query.addScalar("availability", StandardBasicTypes.STRING);
		query.addScalar("isProductChange", StandardBasicTypes.STRING);
		query.addScalar("isNewRecruitment", StandardBasicTypes.STRING);
		query.addScalar("frApplied", StandardBasicTypes.STRING);
		query.addScalar("cpiCompilationSeries", StandardBasicTypes.STRING);
		query.addScalar("uSeasonality", StandardBasicTypes.STRING);
		query.addScalar("averageCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("countCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("averagePRSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationPRSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("countPRSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("keepNumber", StandardBasicTypes.STRING);
		query.addScalar("staffCode", StandardBasicTypes.STRING);
		query.addScalar("staffName", StandardBasicTypes.STRING);
		query.addScalar("noField", StandardBasicTypes.STRING);
		query.addScalar("cpiQuotationType", StandardBasicTypes.STRING);
		query.addScalar("batch", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(OutrangedQuotationRecords.class));
		
		return query.list();
	}
	
	public List<ListOfQuotationRecords> getListOfQuotationRecordsReport(List<Integer> purpose, List<Integer> itemId, 
			List<Integer> cpiSurveyForm, String dataCollection, Date startMonth, Date endMonth, 
			String imputedVariety, String imputedQuotation, String notEqual, String ruaCase, String priceCondition) {
			String referenceMonth = String.format("FORMAT(dum.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		
		String sql = "select indoorQuotationRecordId, quotationRecordId, quotationId, " +
				" case when referenceMonth is not null then " + referenceMonth + " else '' end as referenceMonth, " +
				"varietyPrice, quotationPrice, purposeCode, referenceDate, unitCPIBasePeriod, groupCode, " +
				" groupEnglishName, itemCode, itemEnglishName, unitCode, unitEnglishName, quotationStatus, indoorQuotationRecordStatus, outletFirmCode, outletName, districtCode, outletType, " +
				" outletTypeEnglishName, productId, countryOfOrigin, productAttr1, productAttr2, productAttr3, productAttr4, productAttr5, surveyNPrice, surveySPrice, lastNPrice, lastSPrice, " +
				" previousNPrice, previousSPrice, currentNPrice, currentSPrice, qrReason, qrRemark, keepNumber, cpiSeries, isOutlier, outlierRemark, uSeasonality, isProductChange, RUADate, " +
				" officer, isNoField, cpiQuotationType, batchCode, subPriceRecordId, subPriceTypeName, spNPrice, spSPrice, spDiscount, userId, countSubPriceRecord " + 
				" from (" +
				"select iqr.IndoorQuotationRecordId as indoorQuotationRecordId, " + 
				"qr.QuotationRecordId as quotationRecordId, " + 
				"q.QuotationId as quotationId, " + 
				"iqr.ReferenceMonth as referenceMonth, " + 
				//" CASE WHEN iqr.referenceMonth IS NOT NULL THEN " + referenceMonth + " ELSE '' END AS [referenceMonth], " +
				"iu.Price  AS varietyPrice, " +
				"iq.Price  AS quotationPrice, " +
				"pp.Code as purposeCode, " + 
				"iqr.ReferenceDate as referenceDate, " + 
				"u.CPIBasePeriod as unitCPIBasePeriod, " + 
				"g.Code as groupCode, " + 
				"g.EnglishName as groupEnglishName, " + 
				"i.Code as itemCode, " + 
				"i.EnglishName as itemEnglishName, " + 
				"u.Code as unitCode, " + 
				"u.EnglishName as unitEnglishName, " + 
				"q.[Status] as quotationStatus, " + 
				"iqr.[Status] as indoorQuotationRecordStatus, " + 
				"o.FirmCode as outletFirmCode, " + 
				"o.Name as outletName, " + 
				"d.Code as districtCode, " + 
				"substring(ot.Code, len(ot.Code)-2, 3) as outletType, " + 
				"ot.EnglishName as outletTypeEnglishName, " + 
				"pd.ProductId as productId, " + 
				"pd.CountryOfOrigin as countryOfOrigin, " + 
/*				"pa1.SpecificationName + ' : ' + ps1.Value as productAttr1, " + 
				"pa2.SpecificationName + ' : ' + ps2.Value as productAttr2, " + 
				"pa3.SpecificationName + ' : ' + ps3.Value as productAttr3, " + 
				"pa4.SpecificationName + ' : ' + ps4.Value as productAttr4, " + 
				"pa5.SpecificationName + ' : ' + ps5.Value as productAttr5, " + */
				"concat(pa1.SpecificationName , ':',  ps1.Value) AS productAttr1," + 
				"concat(pa2.SpecificationName , ':', ps2.Value) AS productAttr2, " + 
				"concat(pa3.SpecificationName , ':', ps3.Value) AS productAttr3," + 
				"concat(pa4.SpecificationName , ':', ps4.Value) AS productAttr4," + 
				"concat(pa5.SpecificationName , ':',  ps5.Value) AS productAttr5," + 
				"qr.NPrice as surveyNPrice, " + 
				"qr.SPrice as surveySPrice, " + 
				"iqr.LastNPrice as lastNPrice, " + 
				"iqr.LastSPrice as lastSPrice, " + 
				"iqr.PreviousNPrice as previousNPrice, " + 
				"iqr.PreviousSPrice as previousSPrice, " + 
				"iqr.CurrentNPrice as currentNPrice, " + 
				"iqr.CurrentSPrice as currentSPrice, " + 
				"qr.Reason as qrReason, " + 
				"qr.Remark as qrRemark, " + 
				"q.KeepNoMonth as keepNumber, " + 
				"q.CPICompilationSeries as cpiSeries, " + 
				"iqr.IsOutlier as isOutlier, " + 
				"iqr.OutlierRemark as outlierRemark, " + 
				"u.Seasonality as uSeasonality, " + 
				"qr.IsProductChange as isProductChange, " + 
				"q.RUADate as RUADate, " + 
				"ur.StaffCode + ' - ' + ur.EnglishName as officer, " +
				"iqr.IsNoField as isNoField, " + 
				"u.CPIQoutationType as cpiQuotationType, " + 
				"b.Code as batchCode, " + 
				"spr.SubPriceRecordId as subPriceRecordId, " + 
				"spt.Name as subPriceTypeName, " + 
				"spr.NPrice as spNPrice, " + 
				"spr.SPrice as spSPrice, " + 
				"spr.Discount as spDiscount, " +
				"qr.[UserId] as userId, " +
				"count(spr.subPriceRecordId) AS countSubPriceRecord " + 
				//"from IndoorQuotationRecord iqr " + 
				"from Quotation AS q " +
				"left join IndoorQuotationRecord AS iqr ON q.QuotationId = iqr.QuotationId " +
				"left join QuotationRecord qr on qr.QuotationRecordId = iqr.QuotationRecordId " + 
				//"left join QuotationRecord qr on qr.QuotationRecordId = iqr.QuotationRecordId " + 
				//"left join Quotation q on q.QuotationId = iqr.QuotationId " + 
				"left join [User] ur on ur.UserId = iqr.UserId " +
				"left join Product pd on pd.ProductId = qr.ProductId " + 
				"left join ProductGroup pg on pg.ProductGroupId = pd.ProductGroupId " + 
				"left join ProductAttribute pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.[Sequence] = 1 " + 
				"left join ProductAttribute pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.[Sequence] = 2 " + 
				"left join ProductAttribute pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.[Sequence] = 3 " + 
				"left join ProductAttribute pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.[Sequence] = 4 " + 
				"left join ProductAttribute pa5 on pa5.ProductGroupId = pg.ProductGroupId and pa5.[Sequence] = 5 " + 
				"left join ProductSpecification ps1 on ps1.ProductAttributeId = pa1.ProductAttributeId and ps1.ProductId = pd.ProductId " + 
				"left join ProductSpecification ps2 on ps2.ProductAttributeId = pa2.ProductAttributeId and ps2.ProductId = pd.ProductId " + 
				"left join ProductSpecification ps3 on ps3.ProductAttributeId = pa3.ProductAttributeId and ps3.ProductId = pd.ProductId " + 
				"left join ProductSpecification ps4 on ps4.ProductAttributeId = pa4.ProductAttributeId and ps4.ProductId = pd.ProductId " + 
				"left join ProductSpecification ps5 on ps5.ProductAttributeId = pa5.ProductAttributeId and ps5.ProductId = pd.ProductId " + 
				"left join Outlet o on o.OutletId = qr.OutletId " + 
				"left join ImputeQuotation iq on iq.QuotationId = iqr.QuotationId and iq.ReferenceMonth = iqr.ReferenceMonth " + 
				"left join Unit u on u.UnitId = q.UnitId " + 
				"left join ImputeUnit iu on iu.UnitId = u.UnitId and iqr.ReferenceMonth = iu.ReferenceMonth " + 
				"left join SubItem si on si.SubItemId = u.SubItemId " + 
				"left join OutletType ot on ot.OutletTypeId = si.OutletTypeId " + 
				"left join Item i on i.ItemId = ot.ItemId " + 
				"left join SubGroup sg on sg.SubGroupId = i.SubGroupId " + 
				"left join [Group] g on g.GroupId = sg.GroupId " + 
				"left join Purpose pp on pp.PurposeId = u.PurposeId " + 
				"left join SubPriceRecord spr on spr.QuotationRecordId = qr.QuotationRecordId " + 
				"left join SubPriceType spt on spt.SubPriceTypeId = spr.SubPriceTypeId " + 
				"left join SubPriceFieldMapping spfm on spfm.SubPriceTypeId = spt.SubPriceTypeId " + 
				"left join SubPriceColumn spc on spc.SubPriceRecordId = spr.SubPriceRecordId " + 
				"left join SubPriceField spf on spf.SubPriceFieldId = spfm.SubPriceFieldId " + 
				"left join TPU t on t.TpuId = o.TpuId " + 
				"left join District d on d.DistrictId = t.DistrictId " + 
				"left join Batch b on b.BatchId = q.BatchId "
				+ "	where iqr.referenceMonth between :startMonth and :endMonth "
				+ " and qr.QuotationRecordId is not null ";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		if (itemId != null && itemId.size() > 0){
			sql += " and i.itemId in (:itemId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		if (dataCollection != null && dataCollection.length() > 0){
			if("Y".equals(dataCollection)) sql += " and iqr.isNoField = 0 ";
			else if("N".equals(dataCollection)) sql += " and iqr.isNoField = 1 ";
		}
		if(imputedVariety != null && imputedVariety.length() > 0) {
			/*if("Y".equals(imputedVariety)) sql += " and iu.imputeUnitId is not null ";
			else if("N".equals(imputedVariety)) sql += " and iu.imputeUnitId is null ";*/
			if("Y".equals(imputedVariety)) sql += " and (iu.imputeUnitId is not null and iu.Price is not null) ";
			else if("N".equals(imputedVariety)) sql += " and (iu.imputeUnitId is null OR iu.Price is null) ";
		}
		if(imputedQuotation != null && imputedQuotation.length() > 0) {
			/*if("Y".equals(imputedQuotation)) sql += " and iq.imputeQuotationId is not null ";
			else if("N".equals(imputedQuotation)) sql += " and iq.imputeQuotationId is null ";*/
			if("Y".equals(imputedQuotation)) sql += " and (iq.imputeQuotationId is not null and iq.Price is not null) ";
			else if("N".equals(imputedQuotation)) sql += " and (iq.imputeQuotationId is null OR iq.Price is null) ";
		}
		
		if(notEqual != null && notEqual.length() > 0) {
			if("Y".equals(notEqual)) {
				if(ruaCase != null && ruaCase.length() > 0) {
					sql += " and ( iqr.previousSPrice <> iqr.lastSPrice ";
					if("Y".equals(ruaCase)) sql += " or iqr.isRUA = 1 ) ";
					else if("N".equals(ruaCase)) sql += " or iqr.isRUA = 0 ) ";
				} else {
					sql += " and iqr.previousSPrice <> iqr.lastSPrice ";
				}
			} else if("N".equals(notEqual)) {
				if(ruaCase != null && ruaCase.length() > 0) {
					sql += " and ( not(iqr.previousSPrice <> iqr.lastSPrice) ";
					if("Y".equals(ruaCase)) sql += " or iqr.isRUA = 1 ) ";
					else if("N".equals(ruaCase)) sql += " or iqr.isRUA = 0 ) ";
				} else {
					sql += " and not(iqr.previousSPrice <> iqr.lastSPrice) ";
				}
			} else if("B".equals(notEqual)) {
					sql += " and (iqr.previousSPrice IS NULL OR iqr.lastSPrice IS NULL) ";
			}
		} else {
			if(ruaCase != null && ruaCase.length() > 0) {
				if("Y".equals(ruaCase)) sql += " and iqr.isRUA = 1 ";
				else if("N".equals(ruaCase)) sql += " and iqr.isRUA = 0 ";
			}
		}
		
		sql+= "group by iqr.IndoorQuotationRecordId , qr.QuotationRecordId, q.QuotationId, iqr.ReferenceMonth, pp.Code, iqr.ReferenceDate, u.CPIBasePeriod, " + 
				"g.code, g.EnglishName, i.Code, i.EnglishName, u.Code, u.EnglishName, q.[Status],iqr.[Status],o.FirmCode,o.Name,d.Code,ot.Code,ot.EnglishName," + 
				"pd.ProductId,pd.CountryOfOrigin, pa1.SpecificationName,pa2.SpecificationName,pa3.SpecificationName,pa4.SpecificationName,pa5.SpecificationName," + 
				"ps1.Value,ps2.Value,ps3.Value,ps4.Value,ps5.Value,qr.NPrice,qr.SPrice,iqr.LastNPrice,iqr.LastSPrice,iqr.PreviousNPrice,iqr.PreviousSPrice, qr.IsProductChange," + 
				"iqr.CurrentNPrice,iqr.CurrentSPrice,qr.Reason,qr.Remark,q.KeepNoMonth,q.CPICompilationSeries,iqr.IsOutlier,iqr.OutlierRemark,u.Seasonality," + 
				"q.RUADate,qr.UserId, ur.EnglishName, ur.StaffCode ,iqr.IsNoField,u.CPIQoutationType,b.Code,spr.SubPriceRecordId,spt.Name, spr.NPrice, spr.SPrice, spr.Discount, iu.[Price], iq.[Price] ";
		String[] priceConditionHaving = {" (iqr.currentSPrice is null and (qr.sPrice is not null or (qr.remark is not null and qr.remark <> '')) and count(spr.subPriceRecordId) = 0 ) "
				, " not(iqr.currentSPrice is null and (qr.sPrice is not null or (qr.remark is not null and qr.remark <> '')) and count(spr.subPriceRecordId) = 0 ) "};
		
		if(priceCondition != null && priceCondition.length() > 0) {
			if("Y".equals(priceCondition)) sql += " having " + priceConditionHaving[0];
			else if("N".equals(priceCondition)) sql += " having " + priceConditionHaving[1];
			
		}
		
		sql += " union all"
				+ " select iqr1.IndoorQuotationRecordId as indoorQuotationRecordId, "
				+ " qr1.QuotationRecordId as quotationRecordId, "
				+ " q1.QuotationId as quotationId, "
				+ " iqr1.ReferenceMonth as referenceMonth, "
				//+ " CASE WHEN iqr1.referenceMonth IS NOT NULL THEN " + referenceMonth + " ELSE '' END AS [referenceMonth], "
				+ " iu1.Price  AS varietyPrice,"
				+ " iq1.Price  AS quotationPrice,"
				+ " pp1.Code as purposeCode, "
				+ " iqr1.ReferenceDate as referenceDate, "
				+ " u1.CPIBasePeriod as unitCPIBasePeriod, "
				+ " g1.Code as groupCode, "
				+ " g1.EnglishName as groupEnglishName, "
				+ " i1.Code as itemCode, "
				+ " i1.EnglishName as itemEnglishName, "
				+ " u1.Code as unitCode, "
				+ " u1.EnglishName as unitEnglishName, "
				+ " q1.[Status] as quotationStatus, "
				+ " iqr1.[Status] as indoorQuotationRecordStatus, "
				+ " o1.FirmCode as outletFirmCode, "
				+ " o1.Name as outletName, "
				+ " d1.Code as districtCode, "
				+ " substring(ot1.Code, len(ot1.Code)-2, 3) as outletType, "
				+ " ot1.EnglishName as outletTypeEnglishName, "
				+ " pd1.ProductId as productId, "
				+ " pd1.CountryOfOrigin as countryOfOrigin, "
				+ " concat(pa1a.SpecificationName , ':', ps1a.Value) AS productAttr1, "
				+ " concat(pa2a.SpecificationName , ':', ps2a.Value) AS productAttr2, "
				+ " concat(pa3a.SpecificationName , ':', ps3a.Value) AS productAttr3, "
				+ " concat(pa4a.SpecificationName , ':', ps4a.Value) AS productAttr4, "
				+ " concat(pa5a.SpecificationName , ':', ps5a.Value) AS productAttr5, "
				+ " qr1.NPrice as surveyNPrice, "
				+ " qr1.SPrice as surveySPrice, "
				+ " iqr1.LastNPrice as lastNPrice, "
				+ " iqr1.LastSPrice as lastSPrice, "
				+ " iqr1.PreviousNPrice as previousNPrice, "
				+ " iqr1.PreviousSPrice as previousSPrice, "
				+ " iqr1.CurrentNPrice as currentNPrice, "
				+ " iqr1.CurrentSPrice as currentSPrice, "
				+ " qr1.Reason as qrReason, "
				+ " qr1.Remark as qrRemark, "
				+ " q1.KeepNoMonth as keepNumber, "
				+ " q1.CPICompilationSeries as cpiSeries, "
				+ " iqr1.IsOutlier as isOutlier, "
				+ " iqr1.OutlierRemark as outlierRemark, "
				+ " u1.Seasonality as uSeasonality, "
				+ " qr1.IsProductChange as isProductChange, "
				+ " q1.RUADate as RUADate, "
				+ " ur1.StaffCode + ' - ' + ur1.EnglishName as officer, "
				+ " iqr1.IsNoField as isNoField, "
				+ " u1.CPIQoutationType as cpiQuotationType, "
				+ " b1.Code as batchCode, "
				+ " spr1.SubPriceRecordId as subPriceRecordId, "
				+ " spt1.Name as subPriceTypeName, "
				+ " spr1.NPrice as spNPrice, "
				+ " spr1.SPrice as spSPrice, "
				+ " spr1.Discount as spDiscount, "
				+ " qr1.[UserId] as userId, "
				+ " count(spr1.subPriceRecordId) AS countSubPriceRecord "
				+ " from Quotation AS q1 "
				+ " left join IndoorQuotationRecord AS iqr1 ON q1.QuotationId = iqr1.QuotationId "
				+ " left join QuotationRecord qr1 on qr1.QuotationRecordId = iqr1.QuotationRecordId "
				+ " left join [User] ur1 on ur1.UserId = iqr1.UserId "
				+ " left join Product pd1 on pd1.ProductId = q1.ProductId "
				+ " left join ProductGroup pg1 on pg1.ProductGroupId = pd1.ProductGroupId "
				+ " left join ProductAttribute pa1a on pa1a.ProductGroupId = pg1.ProductGroupId and pa1a.[Sequence] = 1 "
				+ " left join ProductAttribute pa2a on pa2a.ProductGroupId = pg1.ProductGroupId and pa2a.[Sequence] = 2 "
				+ " left join ProductAttribute pa3a on pa3a.ProductGroupId = pg1.ProductGroupId and pa3a.[Sequence] = 3 "
				+ " left join ProductAttribute pa4a on pa4a.ProductGroupId = pg1.ProductGroupId and pa4a.[Sequence] = 4 "
				+ " left join ProductAttribute pa5a on pa5a.ProductGroupId = pg1.ProductGroupId and pa5a.[Sequence] = 5 "
				+ " left join ProductSpecification ps1a on ps1a.ProductAttributeId = pa1a.ProductAttributeId and ps1a.ProductId = pd1.ProductId "
				+ " left join ProductSpecification ps2a on ps2a.ProductAttributeId = pa2a.ProductAttributeId and ps2a.ProductId = pd1.ProductId "
				+ " left join ProductSpecification ps3a on ps3a.ProductAttributeId = pa3a.ProductAttributeId and ps3a.ProductId = pd1.ProductId "
				+ " left join ProductSpecification ps4a on ps4a.ProductAttributeId = pa4a.ProductAttributeId and ps4a.ProductId = pd1.ProductId "
				+ " left join ProductSpecification ps5a on ps5a.ProductAttributeId = pa5a.ProductAttributeId and ps5a.ProductId = pd1.ProductId "
				+ " left join Outlet o1 on o1.OutletId = q1.OutletId "
				+ " left join ImputeQuotation iq1 on iq1.QuotationId = iqr1.QuotationId and iq1.ReferenceMonth = iqr1.ReferenceMonth "
				+ " left join Unit u1 on u1.UnitId = q1.UnitId "
				+ " left join ImputeUnit iu1 on iu1.UnitId = u1.UnitId and iqr1.ReferenceMonth = iu1.ReferenceMonth "
				+ " left join SubItem si1 on si1.SubItemId = u1.SubItemId "
				+ " left join OutletType ot1 on ot1.OutletTypeId = si1.OutletTypeId "
				+ " left join Item i1 on i1.ItemId = ot1.ItemId "
				+ " left join SubGroup sg1 on sg1.SubGroupId = i1.SubGroupId "
				+ " left join [Group] g1 on g1.GroupId = sg1.GroupId "
				+ " left join Purpose pp1 on pp1.PurposeId = u1.PurposeId "
				+ " left join SubPriceRecord spr1 on spr1.QuotationRecordId = qr1.QuotationRecordId "
				+ " left join SubPriceType spt1 on spt1.SubPriceTypeId = spr1.SubPriceTypeId "
				+ " left join SubPriceFieldMapping spfm1 on spfm1.SubPriceTypeId = spt1.SubPriceTypeId "
				+ " left join SubPriceColumn spc1 on spc1.SubPriceRecordId = spr1.SubPriceRecordId "
				+ " left join SubPriceField spf1 on spf1.SubPriceFieldId = spfm1.SubPriceFieldId "
				+ " left join TPU t1 on t1.TpuId = o1.TpuId "
				+ " left join District d1 on d1.DistrictId = t1.DistrictId "
				+ " left join Batch b1 on b1.BatchId = q1.BatchId "
				+ " where iqr1.referenceMonth between :startMonth and :endMonth "
				+ " and qr1.QuotationRecordId is null ";
		if (purpose != null && purpose.size() > 0){
			sql += " and pp1.purposeId in (:purpose) ";
		}
		if (itemId != null && itemId.size() > 0){
			sql += " and i1.itemId in (:itemId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u1.cpiQoutationType in (:cpiSurveyForm) ";
		}
		if (dataCollection != null && dataCollection.length() > 0){
			if("Y".equals(dataCollection)) sql += " and iqr1.isNoField = 0 ";
			else if("N".equals(dataCollection)) sql += " and iqr1.isNoField = 1 ";
		}
		if(imputedVariety != null && imputedVariety.length() > 0) {
			/*if("Y".equals(imputedVariety)) sql += " and iu.imputeUnitId is not null ";
			else if("N".equals(imputedVariety)) sql += " and iu.imputeUnitId is null ";*/
			if("Y".equals(imputedVariety)) sql += " and (iu1.imputeUnitId is not null and iu1.Price is not null) ";
			else if("N".equals(imputedVariety)) sql += " and (iu1.imputeUnitId is null OR iu1.Price is null) ";
		}
		if(imputedQuotation != null && imputedQuotation.length() > 0) {
			/*if("Y".equals(imputedQuotation)) sql += " and iq.imputeQuotationId is not null ";
			else if("N".equals(imputedQuotation)) sql += " and iq.imputeQuotationId is null ";*/
			if("Y".equals(imputedQuotation)) sql += " and (iq1.imputeQuotationId is not null and iq1.Price is not null) ";
			else if("N".equals(imputedQuotation)) sql += " and (iq1.imputeQuotationId is null OR iq1.Price is null) ";
		}
		
		if(notEqual != null && notEqual.length() > 0) {
			if("Y".equals(notEqual)) {
				if(ruaCase != null && ruaCase.length() > 0) {
					sql += " and ( iqr1.previousSPrice <> iqr1.lastSPrice ";
					if("Y".equals(ruaCase)) sql += " or iqr1.isRUA = 1 ) ";
					else if("N".equals(ruaCase)) sql += " or iqr1.isRUA = 0 ) ";
				} else {
					sql += " and iqr1.previousSPrice <> iqr1.lastSPrice ";
				}
			} else if("N".equals(notEqual)) {
				if(ruaCase != null && ruaCase.length() > 0) {
					sql += " and ( not(iqr1.previousSPrice <> iqr1.lastSPrice) ";
					if("Y".equals(ruaCase)) sql += " or iqr1.isRUA = 1 ) ";
					else if("N".equals(ruaCase)) sql += " or iqr1.isRUA = 0 ) ";
				} else {
					sql += " and not(iqr1.previousSPrice <> iqr1.lastSPrice) ";
				}
			} else if("B".equals(notEqual)) {
					sql += " and (iqr1.previousSPrice IS NULL OR iqr1.lastSPrice IS NULL) ";
			}
		} else {
			if(ruaCase != null && ruaCase.length() > 0) {
				if("Y".equals(ruaCase)) sql += " and iqr1.isRUA = 1 ";
				else if("N".equals(ruaCase)) sql += " and iqr1.isRUA = 0 ";
			}
		}
		
		sql+= " group by iqr1.IndoorQuotationRecordId , qr1.QuotationRecordId, q1.QuotationId, iqr1.ReferenceMonth, pp1.Code, iqr1.ReferenceDate, u1.CPIBasePeriod, "
				+ " g1.code, g1.EnglishName, i1.Code, i1.EnglishName, u1.Code, u1.EnglishName, q1.[Status], iqr1.[Status], o1.FirmCode, o1.Name, d1.Code, ot1.Code, ot1.EnglishName, "
				+ " pd1.ProductId, pd1.CountryOfOrigin, pa1a.SpecificationName, pa2a.SpecificationName, pa3a.SpecificationName, pa4a.SpecificationName, pa5a.SpecificationName, "
				+ " ps1a.Value, ps2a.Value, ps3a.Value, ps4a.Value, ps5a.Value, qr1.NPrice, qr1.SPrice, iqr1.LastNPrice, iqr1.LastSPrice, iqr1.PreviousNPrice, iqr1.PreviousSPrice, qr1.IsProductChange, "
				+ " iqr1.CurrentNPrice, iqr1.CurrentSPrice, qr1.Reason, qr1.Remark, q1.KeepNoMonth, q1.CPICompilationSeries, iqr1.IsOutlier, iqr1.OutlierRemark, u1.Seasonality, "
				+ " q1.RUADate, qr1.UserId, ur1.EnglishName, ur1.StaffCode, iqr1.IsNoField, u1.CPIQoutationType, b1.Code, spr1.SubPriceRecordId, spt1.Name, spr1.NPrice, spr1.SPrice, spr1.Discount, iu1.[Price], iq1.[Price] ";
		
		String[] priceConditionHaving2 = {" (iqr1.currentSPrice is null and (qr1.sPrice is not null or (qr1.remark is not null and qr1.remark <> '')) and count(spr1.subPriceRecordId) = 0 ) "
				, " not(iqr1.currentSPrice is null and (qr1.sPrice is not null or (qr1.remark is not null and qr1.remark <> '')) and count(spr1.subPriceRecordId) = 0 ) "};
		
		if(priceCondition != null && priceCondition.length() > 0) {
			if("Y".equals(priceCondition)) sql += " having " + priceConditionHaving2[0];
			else if("N".equals(priceCondition)) sql += " having " + priceConditionHaving2[1];
			
		}
		
		sql += " ) dum";
		
		sql+= " order by dum.referenceMonth, dum.unitCPIBasePeriod, dum.unitCode, dum.quotationId, dum.referenceDate "; 
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		if (itemId != null && itemId.size() > 0){
			query.setParameterList("itemId", itemId);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		
		query.addScalar("indoorQuotationRecordStatus", StandardBasicTypes.STRING);
		query.addScalar("countSubPriceRecord", StandardBasicTypes.INTEGER);
		query.addScalar("indoorQuotationRecordId", StandardBasicTypes.STRING);
		query.addScalar("quotationRecordId", StandardBasicTypes.STRING);
		query.addScalar("quotationId", StandardBasicTypes.STRING);
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("purposeCode", StandardBasicTypes.STRING);
		query.addScalar("referenceDate", StandardBasicTypes.STRING);		
		query.addScalar("unitCPIBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("groupCode", StandardBasicTypes.STRING);
		query.addScalar("groupEnglishName", StandardBasicTypes.STRING);
		query.addScalar("itemCode", StandardBasicTypes.STRING);
		query.addScalar("itemEnglishName", StandardBasicTypes.STRING);		
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName", StandardBasicTypes.STRING);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("outletFirmCode", StandardBasicTypes.STRING);
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("districtCode", StandardBasicTypes.STRING);		
		query.addScalar("outletType", StandardBasicTypes.STRING);
		query.addScalar("outletTypeEnglishName", StandardBasicTypes.STRING);	
		query.addScalar("productId", StandardBasicTypes.STRING);	
		query.addScalar("countryOfOrigin", StandardBasicTypes.STRING);
		query.addScalar("productAttr1", StandardBasicTypes.STRING);
		query.addScalar("productAttr2", StandardBasicTypes.STRING);
		query.addScalar("productAttr3", StandardBasicTypes.STRING);
		query.addScalar("productAttr4", StandardBasicTypes.STRING);
		query.addScalar("productAttr5", StandardBasicTypes.STRING);
		query.addScalar("surveyNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("surveySPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("qrReason",StandardBasicTypes.STRING);
		query.addScalar("qrRemark", StandardBasicTypes.STRING);
		query.addScalar("keepNumber", StandardBasicTypes.STRING);
		query.addScalar("cpiSeries", StandardBasicTypes.STRING);
		query.addScalar("isOutlier", StandardBasicTypes.STRING);
		query.addScalar("outlierRemark", StandardBasicTypes.STRING);
		query.addScalar("uSeasonality", StandardBasicTypes.STRING);
		query.addScalar("isProductChange", StandardBasicTypes.STRING);
		query.addScalar("RUADate", StandardBasicTypes.STRING);
		query.addScalar("officer", StandardBasicTypes.STRING);
		query.addScalar("isNoField", StandardBasicTypes.STRING);
		query.addScalar("cpiQuotationType", StandardBasicTypes.STRING);
		query.addScalar("batchCode", StandardBasicTypes.STRING);
		query.addScalar("subPriceRecordId", StandardBasicTypes.STRING);
		query.addScalar("subPriceTypeName", StandardBasicTypes.STRING);	
		query.addScalar("spNPrice", StandardBasicTypes.DOUBLE);	
		query.addScalar("spSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("spDiscount", StandardBasicTypes.STRING);
		query.addScalar("userId", StandardBasicTypes.STRING);
		
		query.addScalar("varietyPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationPrice", StandardBasicTypes.DOUBLE);
		
		query.setResultTransformer(Transformers.aliasToBean(ListOfQuotationRecords.class));
		
		System.out.println("9007 :: " + sql);
		
		return query.list();
	}
//2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
	public List<NewRecruitmentsAndProductReplacements> getNewRecruitmentsAndProductReplacementsReport(List<Integer> purpose, List<Integer> itemId, 
			List<Integer> cpiSurveyForm, Date startMonth, Date endMonth) {

		String referenceMonth = String.format("format(iqr.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		String referenceDate = String.format("format(iqr.ReferenceDate, '%s', 'en-us')", SystemConstant.REPORT_REFERENCE_DATE_FORMAT);
		
		String sql = "select ROW_NUMBER() OVER("
						+ "ORDER BY iqr.ReferenceMonth asc, "
						+ "case when p.Code = 'MRPS' then 1 "
							 + "when p.Code = 'WPU' then 2 "
							 + "when p.Code = 'CEO' then 3 "
							 + "when p.Code = 'ICP' then 4 end asc, "
						+ "u.CPIBasePeriod asc, u.Code asc, "
						+ "iqr.IndoorQuotationRecordId asc) as rowNum, "
					+ "iqr.IndoorQuotationRecordId as indoorQuotationRecordId, "
					+ "iqr.QuotationRecordId as fieldQuotationRecordId, "
					+ "iqr.QuotationId as quotationId, "
					+ "case when iqr.ReferenceMonth is null then '' else " + referenceMonth + " end as referenceMonth, "
					+ "case when iqr.ReferenceDate is null then '' else " + referenceDate + " end as referenceDate, "
					+ "p.code as purpose, "
					+ "u.CPIBasePeriod as cpiBasePeriod, "
					+ "u.Code as varietyCode, "
					+ "u.EnglishName as varietyEnglishName, "
					+ "q.Status as quotationStatus, "
					+ "iqr.Status as dataConversionStatus, "
					+ "o.FirmCode as outletCode, "
					+ "o.Name as outletName, "
					+ "ot.Code as outletType, "
					+ "ot.EnglishName as outletTypeEnglishName, "
					+ "prod.ProductId as productId, "
					+ "prod.CountryOfOrigin as countryOfOrigin, "
					+ "case when (prodAttr1.SpecificationName is null and prodSpec1.Value is null) then '' else concat(prodAttr1.SpecificationName,':',prodSpec1.Value) end as productAttributes1, "
					+ "case when (prodAttr2.SpecificationName is null and prodSpec2.Value is null) then '' else concat(prodAttr2.SpecificationName,':',prodSpec2.Value) end as productAttributes2, "
					+ "case when (prodAttr3.SpecificationName is null and prodSpec3.Value is null) then '' else concat(prodAttr3.SpecificationName,':',prodSpec3.Value) end as productAttributes3, "
					+ "case when (prodAttr4.SpecificationName is null and prodSpec4.Value is null) then '' else concat(prodAttr4.SpecificationName,':',prodSpec4.Value) end as productAttributes4, "
					+ "case when (prodAttr5.SpecificationName is null and prodSpec5.Value is null) then '' else concat(prodAttr5.SpecificationName,':',prodSpec5.Value) end as productAttributes5, "
					+ "qr.NPrice as surveyNPrice, "
					+ "qr.SPrice as surveySPrice, "
					+ "iqr.LastNPrice as lastEditedNPrice, "
					+ "iqr.LastSPrice as lastEditedSPrice, "
					+ "iqr.PreviousNPrice as previousEditedNPrice, "
					+ "iqr.PreviousSPrice as previousEditedSPrice, "
					+ "iqr.CurrentNPrice as currentEditedNPrice, "
					+ "iqr.CurrentSPrice as currentEditedSPrice, "
					+ "case when iqr.previousNPrice > 0 then (iqr.currentNPrice / iqr.previousNPrice * 100) else null end as nPricePr, "
					+ "case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end as sPricePr, "
					+ "qr.Reason as quotationRecordReason, "
					+ "qr.Remark as quotationRecordRemarks, "
					+ "case when iqr.IsNewRecruitment = 1 then 'Y' else 'N' end as newRecruitmentCase, "
					+ "case when iqr.IsProductChange = 1 then 'Y' else 'N' end as productChange, "
					+ "qr.ProductRemark as productRemarks, iqr.Remark as dataConversionRemarks "
				+ "from IndoorQuotationRecord as iqr "
					+ "left join QuotationRecord as qr on qr.QuotationRecordId = iqr.QuotationRecordId "
					+ "left join Quotation as q on q.QuotationId = iqr.QuotationId "
					+ "left join Unit as u on u.UnitId = q.UnitId "
					+ "left join Outlet as o on o.OutletId = qr.OutletId "
					+ "left join SubItem as si on si.SubItemId = u.SubItemId "
					+ "left join Purpose as p on p.PurposeId = u.PurposeId "
					+ "left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
					//2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
					+ "left join Item i on i.ItemId = ot.ItemId " 
					+ "left join Product as prod on prod.ProductId = qr.ProductId " 
					+ "left join ProductGroup as prodGp on prodGp.ProductGroupId = prod.ProductGroupId "
					+ "left join ProductAttribute as prodAttr1 on prodAttr1.ProductGroupId = prodGp.ProductGroupId and prodAttr1.Sequence = 1 "
					+ "left join ProductSpecification as prodSpec1 on prodSpec1.ProductAttributeId = prodAttr1.ProductAttributeId and prodSpec1.ProductId = prod.ProductId "
					+ "left join ProductAttribute as prodAttr2 on prodAttr2.ProductGroupId = prodGp.ProductGroupId and prodAttr2.Sequence = 2 "
					+ "left join ProductSpecification as prodSpec2 on prodSpec2.ProductAttributeId = prodAttr2.ProductAttributeId and prodSpec2.ProductId = prod.ProductId "
					+ "left join ProductAttribute as prodAttr3 on prodAttr3.ProductGroupId = prodGp.ProductGroupId and prodAttr3.Sequence = 3 "
					+ "left join ProductSpecification as prodSpec3 on prodSpec3.ProductAttributeId = prodAttr3.ProductAttributeId and prodSpec3.ProductId = prod.ProductId "
					+ "left join ProductAttribute as prodAttr4 on prodAttr4.ProductGroupId = prodGp.ProductGroupId and prodAttr4.Sequence = 4 "
					+ "left join ProductSpecification as prodSpec4 on prodSpec4.ProductAttributeId = prodAttr4.ProductAttributeId and prodSpec4.ProductId = prod.ProductId "
					+ "left join ProductAttribute as prodAttr5 on prodAttr5.ProductGroupId = prodGp.ProductGroupId and prodAttr5.Sequence = 5 "
					+ "left join ProductSpecification as prodSpec5 on prodSpec5.ProductAttributeId = prodAttr5.ProductAttributeId and prodSpec5.ProductId = prod.ProductId "
				+ "where 1=1 "				
				+ "and iqr.referenceMonth >= :startMonth and iqr.referenceMonth <= :endMonth "
				+ "and (iqr.IsNewRecruitment = 1 or iqr.IsProductChange = 1) ";
		
		if (purpose != null && purpose.size() > 0){
			sql += "and p.purposeId in (:purpose) ";
		}
		//2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
		if (itemId != null && itemId.size() > 0){
			sql += "and i.itemId in (:itemId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += "and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
//		2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
		if (itemId != null && itemId.size() > 0){
			query.setParameterList("itemId", itemId);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		
		query.addScalar("rowNum",StandardBasicTypes.INTEGER);
		query.addScalar("indoorQuotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("fieldQuotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("quotationId",StandardBasicTypes.INTEGER);
		query.addScalar("referenceMonth",StandardBasicTypes.STRING);
		query.addScalar("referenceDate",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("varietyCode",StandardBasicTypes.STRING);
		query.addScalar("varietyEnglishName",StandardBasicTypes.STRING);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("dataConversionStatus",StandardBasicTypes.STRING);
		query.addScalar("outletCode",StandardBasicTypes.STRING);
		query.addScalar("outletName",StandardBasicTypes.STRING);
		query.addScalar("outletType",StandardBasicTypes.STRING);
		query.addScalar("outletTypeEnglishName",StandardBasicTypes.STRING);
		query.addScalar("productId",StandardBasicTypes.INTEGER);
		query.addScalar("countryOfOrigin",StandardBasicTypes.STRING);
		query.addScalar("productAttributes1",StandardBasicTypes.STRING);
		query.addScalar("productAttributes2",StandardBasicTypes.STRING);
		query.addScalar("productAttributes3",StandardBasicTypes.STRING);
		query.addScalar("productAttributes4",StandardBasicTypes.STRING);
		query.addScalar("productAttributes5",StandardBasicTypes.STRING);
		query.addScalar("surveyNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("surveySPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastEditedNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastEditedSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousEditedNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousEditedSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentEditedNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentEditedSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("nPricePr",StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("sPricePr",StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("quotationRecordReason",StandardBasicTypes.STRING);
		query.addScalar("quotationRecordRemarks",StandardBasicTypes.STRING);
		query.addScalar("newRecruitmentCase",StandardBasicTypes.STRING);
		query.addScalar("productChange",StandardBasicTypes.STRING);
		query.addScalar("productRemarks",StandardBasicTypes.STRING);
		query.addScalar("dataConversionRemarks",StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(NewRecruitmentsAndProductReplacements.class));
		
		return query.list();
	}
	
	public List<QuotationRecordVerificationApprovalTableListModel> queryQuotationRecordVerificationApprovalTableList(
			QuotationRecordVerificationApprovalFilterModel filterModel,
			Integer firstRecord, Integer displayLength, Order order){
		
		String referenceDate = String.format("format(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = 
				"select iqr.indoorQuotationRecordId as indoorQuotationRecordId,"
					+ " case when iqr.referenceDate is not null then " + referenceDate + " else '' end as referenceDate,"
					+ " o.name as firmName,"
					+ " u.code as unitCode,"
					+ " sg.chineseName as subGroupChineseName, sg.englishName as subGroupEnglishName,"
					+ " u.chineseName as unitChineseName, u.englishName as unitEnglishName,"
					+ " quotationUser.staffCode as fieldOfficerName,"
					+ " indoorUser.staffCode as indoorOfficerName,"
					+ " iqr.firmRemark as firmVerifyRemark,"
					+ " iqr.quotationRemark as quotationVerifyRemark,"
					+ " iqr.categoryRemark as categoryVerifyRemark,"
					+ " iqr.indoorQuotationRecordId as indoorQuotationRecordId, "
					+ " case when iqr.referenceMonth is null then '' else Format(iqr.referenceMonth, '"+SystemConstant.MONTH_FORMAT+"', 'en-US') end as referenceMonth"
				+ " from IndoorQuotationRecord iqr "
					+ "	left join iqr.quotationRecord qr"
					+ " left join iqr.quotation q"
					+ " left join q.product p"
					+ " left join p.fullSpecifications pfs on pfs.sequence = 1"
					+ " left join pfs.productAttribute pa"
					+ " left join q.outlet o"
					+ " left join q.unit u"
					+ " left join u.subItem si"
					+ " left join si.outletType ot"
					+ " left join ot.item i"
					+ " left join i.subGroup sg"
					+ " left join sg.group g"
					+ " left join qr.subPriceRecords spr"
					+ " left join u.purpose pu"
					+ " left join iqr.user indoorUser"
					+ " left join qr.user quotationUser"
					+ " left join q.batch as b"
				+ " where 1=1"
				+ " and iqr.status = 'Request Verification'";
				
				
		if (StringUtils.isEmpty(filterModel.getSearch())){
			hql += " and ("
					+ " o.name like :search "
					+ " or u.code like :search "
					+ " or sg.chineseName like :search "
					+ " or sg.englishName like :search " 
					+ " or u.chineseName like :search "
					+ " or u.englishName like :search " 
					+ " or quotationUser.staffCode like :search " 
					+ " or indoorUser.staffCode like :search " 
					+ " or iqr.firmRemark like :search " 
					+ " or iqr.quotationRemark like :search " 
					+ " or iqr.categoryRemark like :search " 
					+ " or " + referenceDate + "like :search "
					+ " ) ";
		}
				
		
		if(filterModel.getIndoorUserId() != null){
			hql += " and indoorUser.userId = :indoorUserId";
		}
		
		if(filterModel.getOutletId() != null){
			hql += " and o.outletId = :outletId";
		}
		
		if(filterModel.getPurposeId() != null){
			hql += " and pu.purposeId = :purposeId";
		}
		
		if(filterModel.getSubgroupId() != null){
			hql += " and sg.subGroupId = :subGroupId";
		}
		
		if(filterModel.getUnitId() != null && filterModel.getUnitId().size() > 0){
			hql += " and u.unitId in (:unitId)";
		}
		
		if (filterModel.getRefMonth() != null){
			hql += " and iqr.referenceMonth = :refMonth ";
		}
		
		if(filterModel.getIsVerify() != null) {
			if(filterModel.getIsVerify() == 1) {
				hql += " and iqr.isFirmVerify = 1 ";
			} else if(filterModel.getIsVerify() == 2) {
				hql += " and iqr.isCategoryVerify = 1 ";
			} else if(filterModel.getIsVerify() == 3) {
				hql += " and iqr.isQuotationVerify = 1 ";
			}
		}
		
		hql += " group by iqr.indoorQuotationRecordId, iqr.referenceDate, sg.chineseName, sg.englishName,"
				+ " u.code, u.chineseName, u.englishName, o.name,"
				+ " quotationUser.staffCode, indoorUser.staffCode,"
				+ " iqr.firmRemark, iqr.quotationRemark, iqr.categoryRemark, iqr.referenceMonth";
		
		hql += " order by " + order.getPropertyName() + (order.isAscending()? " asc": " desc");
		
		Query query = this.getSession().createQuery(hql);

		if (StringUtils.isEmpty(filterModel.getSearch())){
			query.setParameter("search", String.format("%%%s%%",filterModel.getSearch()));
		}
		
		if(filterModel.getIndoorUserId() != null){
			query.setParameter("indoorUserId", filterModel.getIndoorUserId());
		}
		
		if(filterModel.getOutletId() != null){
			query.setParameter("outletId", filterModel.getOutletId());
		}
		
		if(filterModel.getPurposeId() != null){
			query.setParameter("purposeId", filterModel.getPurposeId());
		}
		
		if(filterModel.getSubgroupId() != null){
			query.setParameter("subGroupId", filterModel.getSubgroupId());
		}
		
		if(filterModel.getUnitId() != null && filterModel.getUnitId().size() > 0){
			query.setParameterList("unitId", filterModel.getUnitId());
		}
		
		if(filterModel.getRefMonth() != null){
			query.setParameter("refMonth", filterModel.getRefMonth());
		}
		
		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordVerificationApprovalTableListModel.class));

		return query.list();
		
		
	}
	
	public Long countQuotationRecordVerificationApprovalTableList(
			QuotationRecordVerificationApprovalFilterModel filterModel){
		
		String referenceDate = String.format("FORMAT(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = 
				"select (iqr.indoorQuotationRecordId) "
				+ " from IndoorQuotationRecord iqr "
					+ "	left join iqr.quotationRecord qr"
					+ " left join iqr.quotation q"
					+ " left join q.product p"
					+ " left join p.fullSpecifications pfs on pfs.sequence = 1"
					+ " left join pfs.productAttribute pa"
					+ " left join q.outlet o"
					+ " left join q.unit u"
					+ " left join u.subItem si"
					+ " left join si.outletType ot"
					+ " left join ot.item i"
					+ " left join i.subGroup sg"
					+ " left join sg.group g"
					+ " left join qr.subPriceRecords spr"
					+ " left join u.purpose pu"
					+ " left join iqr.user indoorUser"
					+ " left join qr.user quotationUser"
					+ " left join q.batch as b"
				+ " where 1=1"
				+ " and iqr.status = 'Request Verification'";
				
		if (!StringUtils.isEmpty(filterModel.getSearch())){
			hql += " and ("
					+ " o.name like :search "
					+ " or u.code like :search "
					+ " or sg.chineseName like :search "
					+ " or sg.englishName like :search " 
					+ " or u.chineseName like :search "
					+ " or u.englishName like :search " 
					+ " or quotationUser.staffCode like :search " 
					+ " or indoorUser.staffCode like :search " 
					+ " or iqr.firmRemark like :search " 
					+ " or iqr.quotationRemark like :search " 
					+ " or iqr.categoryRemark like :search " 
					+ " or " + referenceDate + "like :search "
					+ " ) ";
		}
		
		if(filterModel.getIndoorUserId() != null){
			hql += " and indoorUser.userId = :indoorUserId";
		}
		
		if(filterModel.getOutletId() != null){
			hql += " and o.outletId = :outletId";
		}
		
		if(filterModel.getPurposeId() != null){
			hql += " and pu.purposeId = :purposeId";
		}
		
		if(filterModel.getSubgroupId() != null){
			hql += " and sg.subGroupId = :subGroupId";
		}
		
		if(filterModel.getUnitId() != null && filterModel.getUnitId().size() > 0){
			hql += " and u.unitId in (:unitId)";
		}
		
		if (filterModel.getRefMonth() != null){
			hql += " and iqr.referenceMonth = :refMonth ";
		}
		
		if(filterModel.getIsVerify() != null) {
			if(filterModel.getIsVerify() == 1) {
				hql += " and iqr.isFirmVerify = 1 ";
			} else if(filterModel.getIsVerify() == 2) {
				hql += " and iqr.isCategoryVerify = 1 ";
			} else if(filterModel.getIsVerify() == 3) {
				hql += " and iqr.isQuotationVerify = 1 ";
			}
		}
		
		hql += " group by iqr.indoorQuotationRecordId";
		
		String countHql = "select count(distinct iqr.indoorQuotationRecordId) "
				+ " from IndoorQuotationRecord as iqr "
				+ " where iqr.id in (" + hql + ") ";
		
		Query query = this.getSession().createQuery(countHql);

		if (!StringUtils.isEmpty(filterModel.getSearch())){
			query.setParameter("search", String.format("%%%s%%",filterModel.getSearch()));
		}
		
		if(filterModel.getIndoorUserId() != null){
			query.setParameter("indoorUserId", filterModel.getIndoorUserId());
		}
		
		if(filterModel.getOutletId() != null){
			query.setParameter("outletId", filterModel.getOutletId());
		}
		
		if(filterModel.getPurposeId() != null){
			query.setParameter("purposeId", filterModel.getPurposeId());
		}
		
		if(filterModel.getSubgroupId() != null){
			query.setParameter("subGroupId", filterModel.getSubgroupId());
		}
		
		if(filterModel.getUnitId() != null && filterModel.getUnitId().size() > 0){
			query.setParameterList("unitId", filterModel.getUnitId());
		}
		
		if(filterModel.getRefMonth() != null){
			query.setParameter("refMonth", filterModel.getRefMonth());
		}

		return (Long) query.uniqueResult();
		
	}
	
	public List<Integer> getQuotationRecordVerificationApprovalTableListIds(
			QuotationRecordVerificationApprovalFilterModel filterModel){
		
		String referenceDate = String.format("FORMAT(iqr.referenceDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = 
				"select iqr.indoorQuotationRecordId "
				+ " from IndoorQuotationRecord iqr "
					+ "	left join iqr.quotationRecord qr"
					+ " left join iqr.quotation q"
					+ " left join q.product p"
					+ " left join p.fullSpecifications pfs on pfs.sequence = 1"
					+ " left join pfs.productAttribute pa"
					+ " left join q.outlet o"
					+ " left join q.unit u"
					+ " left join u.subItem si"
					+ " left join si.outletType ot"
					+ " left join ot.item i"
					+ " left join i.subGroup sg"
					+ " left join sg.group g"
					+ " left join qr.subPriceRecords spr"
					+ " left join u.purpose pu "
					+ " left join iqr.user indoorUser "
					+ " left join qr.user quotationUser "
					+ " left join q.batch as b "
				+ " where 1=1"
				+ " and iqr.status = 'Request Verification'"
				+ " and ("
					+ " o.name like :search "
					+ " or u.code like :search "
					+ " or sg.chineseName like :search "
					+ " or sg.englishName like :search " 
					+ " or u.chineseName like :search "
					+ " or u.englishName like :search " 
					+ " or quotationUser.staffCode like :search " 
					+ " or indoorUser.staffCode like :search " 
					+ " or iqr.firmRemark like :search " 
					+ " or iqr.quotationRemark like :search " 
					+ " or iqr.categoryRemark like :search "
					+ " or " + referenceDate + " like :search "
					+ " ) ";
		
		if(filterModel.getIndoorUserId() != null){
			hql += " and indoorUser.userId = :indoorUserId";
		}
		
		if(filterModel.getOutletId() != null){
			hql += " and o.outletId = :outletId";
		}
		
		if(filterModel.getPurposeId() != null){
			hql += " and pu.purposeId = :purposeId";
		}
		
		if(filterModel.getSubgroupId() != null){
			hql += " and sg.subGroupId = :subGroupId";
		}
		
		if(filterModel.getUnitId() != null && filterModel.getUnitId().size() > 0){
			hql += " and u.unitId in :unitId";
		}
		
		if(filterModel.getIsVerify() != null) {
			if(filterModel.getIsVerify() == 1) {
				hql += " and iqr.isFirmVerify = 1 ";
			} else if(filterModel.getIsVerify() == 2) {
				hql += " and iqr.isCategoryVerify = 1 ";
			} else if(filterModel.getIsVerify() == 3) {
				hql += " and iqr.isQuotationVerify = 1 ";
			}
		}
		
		hql += " group by iqr.indoorQuotationRecordId";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("search", String.format("%%%s%%",filterModel.getSearch()));
		
		if(filterModel.getIndoorUserId() != null){
			query.setParameter("indoorUserId", filterModel.getIndoorUserId());
		}
		
		if(filterModel.getOutletId() != null){
			query.setParameter("outletId", filterModel.getOutletId());
		}
		
		if(filterModel.getPurposeId() != null){
			query.setParameter("purposeId", filterModel.getPurposeId());
		}
		
		if(filterModel.getSubgroupId() != null){
			query.setParameter("subGroupId", filterModel.getSubgroupId());
		}
		
		if(filterModel.getUnitId() != null && filterModel.getUnitId().size() > 0){
			query.setParameterList("unitId", filterModel.getUnitId());
		}

		return query.list();
		
	}

	@SuppressWarnings("unchecked")
	public List<QuotationStatisticsReportByQuotation> getQuotationStatisticsReportByQuotation(int quotationId, Date periodReferenceMonth){

		Calendar month1 = Calendar.getInstance();
		month1.setTime(periodReferenceMonth);
		month1.add(Calendar.MONTH, -1);

		String referenceMonth = String.format("format(iqr.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String collectionDate = String.format("format(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = "select case when iqr.referenceMonth is null then '' else " + referenceMonth + " end as referenceMonth, "
				+ " qr.quotationRecordId as quotationRecordId, "
				+ " case when qr.collectionDate is null then '' else " + collectionDate + " end as dataCollectionDate, "
				+ " concat(str(outlet.firmCode), ' - ', outlet.name) as outletIdAndOutletName, "
				+ " qr.product.id as productId, "
				+ " fp1.value as productAttribute1, fp2.value as productAttribute2, fp3.value as productAttribute3, "
				+ " qr.availability as productAvailability, "
				+ " qr.nPrice as surveyNPrice, "
				+ " qr.sPrice as surveySPrice, "
				+ " iqr.currentNPrice as editedNPrice, "
				+ " iqr.currentSPrice as editedSPrice, "
				+ " (iqr.currentSPrice / iqr.previousSPrice * 100) as quotationRecordPR, "
				+ " iqr.isCurrentPriceKeepNo as keepNumber, "
				+ " iqr.remark as priceReason, "
				+ " iqr.isOutlier as outlierCase, "
				+ " iqr.outlierRemark as outlierRemark "
				
				+ " from QuotationRecord as qr "
				+ " inner join qr.indoorQuotationRecord as iqr "
				+ " left join qr.outlet as outlet "
				+ " left join qr.product as product "
				+ " left join product.fullSpecifications as fp1 on fp1.sequence = 1 "
				+ " left join product.fullSpecifications as fp2 on fp2.sequence = 2 "
				+ " left join product.fullSpecifications as fp3 on fp3.sequence = 3 "
				
				+ " where 1=1 ";
		
		hql += " and qr.quotation.id = :quotationId ";
		hql += " and (iqr.referenceMonth = :referenceMonth or iqr.referenceMonth = :month1) ";
		
		hql += " order by iqr.referenceMonth desc ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("quotationId", quotationId);
		query.setParameter("referenceMonth", periodReferenceMonth);
		query.setParameter("month1", month1.getTime());
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationStatisticsReportByQuotation.class));
		
		return query.list();
	}
	
	//2018-01-09 cheung_cheng should be item id
//	public List<FRAdjustment> getFRAdjustmentReport(List<Integer> purpose, List<Integer> unitId, 
	public List<FRAdjustment> getFRAdjustmentReport(List<Integer> purpose, List<Integer> itemId,
			List<String> outletTypes, String frRequired, String consignmentCounter, String seasonalWithdrawal,
			Date startMonth, Date endMonth, String appliedAdmin, String frApplied, String firstReturn,
			String dataCollection) {

		String sql = "select iqr.IndoorQuotationRecordId as indoorQuotationRecordId, " + 
				"qr.QuotationRecordId as quotationRecordId, " + 
				"q.QuotationId as quotationId, " + 
				"iqr.ReferenceMonth as referenceMonth, " + 
				"iqr.ReferenceDate as referenceDate, " + 
				"pp.Code as purpose, " + 
				"u.CPIBasePeriod as cpiBasePeriod, " + 
				"u.code as unitCode, " + 
				"u.englishName as unitEnglishName, " + 
				"q.Status as quotationStatus, " + 
				"iqr.Status as indoorQuotationRecordStatus, " +
				"o.FirmCode as outletCode," + 
				"o.Name as outletName, " +
				//2018-01-09 cheung_cheng "Outlet Type"" should show ""Outlet type code (last 3 digits) & name"
//				"ot.code as outletTypeCode, " +
				//"substring(ot.code, 8, 3) + ' - ' + ot.englishName as outletTypeCode, " +
				"substring(ot.Code, len(ot.Code)-2, 3) + ' - ' + ot.englishName as outletTypeCode, " +
				"p.ProductId as productId, " + 
				"p.CountryOfOrigin as countryOfOrigin, " + 
				"case when (pa1.SpecificationName is null and ps1.Value is null) then '' else concat(pa1.SpecificationName,':',ps1.Value) end as productAttr1, " +
				"case when (pa2.SpecificationName is null and ps2.Value is null) then '' else concat(pa2.SpecificationName,':',ps2.Value) end as productAttr2, " +
				"case when (pa3.SpecificationName is null and ps3.Value is null) then '' else concat(pa3.SpecificationName,':',ps3.Value) end as productAttr3, " +
				"case when (pa4.SpecificationName is null and ps4.Value is null) then '' else concat(pa4.SpecificationName,':',ps4.Value) end as productAttr4, " +
				"case when (pa5.SpecificationName is null and ps5.Value is null) then '' else concat(pa5.SpecificationName,':',ps5.Value) end as productAttr5, " +
				"qr.NPrice as surveyNPrice, " + 
				"qr.SPrice as surveySPrice, " + 
				"iqr.LastNPrice as lastNPrice, " + 
				"iqr.LastSPrice as lastSPrice, " + 
				"iqr.PreviousNPrice as previousNPrice, " + 
				"iqr.PreviousSPrice as previousSPrice, " + 
				"iqr.CurrentNPrice as currentNPrice, " + 
				"iqr.CurrentSPrice as currentSPrice, " + 
				"qr.Reason as qrReason, " + 
				"qr.Remark as qrRemark, " + 
				"iqr.IsNewRecruitment as isNewRecruitment, " + 
				"qr.IsProductChange as isProductChange, " + 
				"iqr.Remark as iqrRemark, " + 
				"q.FRField as FRField, " + 
				"q.IsFRFieldPercentage as isFRFieldPercentage, " + 
				"q.FRAdmin as FRAdmin, " + 
				"q.IsFRAdminPercentage as isFRAdminPercentage, " + 
				"qr.FR as FR, " + 
				"qr.IsFRPercentage as isFRPercentage, " + 
				"qr.IsConsignmentCounter as isConsignmentCounter, " + 
				"qr.ConsignmentCounterName as consignmentCounterName, " + 
				"qr.ConsignmentCounterRemark as consignmentCounterRemark, " + 
				"iqr.IsApplyFR as isApplyFR, " + 
				"q.IsFRApplied as isFRApplied, " +
				
				//2018-01-09 cheung_cheng
//				"q.IsReturnNewGoods as isReturnNewGoods, " + 
				"CASE WHEN q.isReturnGoods = 1 and q.isReturnNewGoods = 1 THEN 'New'" + 
				"ELSE " + 
				"(CASE WHEN q.isReturnGoods = 1 and q.isReturnNewGoods = 0 THEN 'Same'"+
				"ELSE"	+ 
				"(CASE WHEN q.isReturnGoods = 0 and q.isReturnNewGoods = 0 THEN 'NA'"+
				"ELSE" +
				"(CASE WHEN q.isReturnGoods = 0 and q.isReturnNewGoods = 1 THEN 'NA' END)END" +
				")END )END AS isReturnNewGoods," + 
				
				
				"iqr.IsCurrentPriceKeepNo as isKeepNum, " + 
				"q.SeasonalWithdrawal as seasonalWithdraw, " + 
				"u.Seasonality as seasonality, " + 
				"q.IsUseFRAdmin as isUseFRAdmin, " + 
				"q.LastFRAppliedDate as lastFRAppliedDate, " + 
				"iqr.IsNoField as isNoField " + 
				"from IndoorQuotationRecord iqr " + 
				"left join Quotation q on q.QuotationId = iqr.QuotationId " + 
				"left join QuotationRecord qr on qr.QuotationRecordId = iqr.QuotationRecordId " + 
				"left join Unit u on u.UnitId = q.UnitId " + 
				"left join Purpose pp on pp.PurposeId = u.PurposeId " +
//				"left join Outlet o on o.OutletId = qr.OutletId " +
				"left join Outlet o on o.OutletId = CASE WHEN qr.OutletId IS NULL THEN  q.OutletId ELSE  qr.OutletId  END " + 
				"left join SubItem si on si.SubItemId = u.SubItemId " + 
				"left join OutletType ot on ot.OutletTypeId = si.OutletTypeId " + 
				"left join Item as item on item.ItemId = ot.ItemId " + //2018-01-09 cheung_cheng export by item id
//				"left join Product p on p.ProductId = q.ProductId " +
				"left join Product p on p.ProductId = CASE WHEN qr.ProductId IS NULL THEN q.ProductId ELSE  qr.ProductId  END " + 
				"left join ProductGroup pg on pg.ProductGroupId = p.ProductGroupId " + 
				"left join ProductAttribute pa1 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 1 " + 
//				"left join ProductAttribute pa2 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 2 " + 
//				"left join ProductAttribute pa3 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 3 " + 
//				"left join ProductAttribute pa4 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 4 " + 
//				"left join ProductAttribute pa5 on pa1.ProductGroupId = pg.ProductGroupId and pa1.Sequence = 5 " + 
				"left join ProductAttribute pa2 on pa2.ProductGroupId = pg.ProductGroupId and pa2.Sequence = 2 " + 
				"left join ProductAttribute pa3 on pa3.ProductGroupId = pg.ProductGroupId and pa3.Sequence = 3 " + 
				"left join ProductAttribute pa4 on pa4.ProductGroupId = pg.ProductGroupId and pa4.Sequence = 4 " + 
				"left join ProductAttribute pa5 on pa5.ProductGroupId = pg.ProductGroupId and pa5.Sequence = 5 " + 
				"left join ProductSpecification ps1 on ps1.ProductId = p.ProductId and ps1.ProductAttributeId = pa1.ProductAttributeId " + 
				"left join ProductSpecification ps2 on ps2.ProductId = p.ProductId and ps2.ProductAttributeId = pa2.ProductAttributeId " + 
				"left join ProductSpecification ps3 on ps3.ProductId = p.ProductId and ps3.ProductAttributeId = pa3.ProductAttributeId " + 
				"left join ProductSpecification ps4 on ps4.ProductId = p.ProductId and ps4.ProductAttributeId = pa4.ProductAttributeId " + 
				"left join ProductSpecification ps5 on ps5.ProductId = p.ProductId and ps5.ProductAttributeId = pa5.ProductAttributeId " + 
				"where iqr.referenceMonth between :startMonth and :endMonth ";
		

		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		
		if (itemId != null && itemId.size() > 0){
			sql += " and item.itemId in (:itemId) ";
		}
		
		if (outletTypes != null && outletTypes.size() > 0){
//			sql += " and substring(ot.code, 8, 3) in (:outletTypes) ";
			sql += " and substring(ot.Code, len(ot.Code)-2, 3) in (:outletTypes) ";
		}
		if (frRequired != null && frRequired.length() > 0){
			if("Y".equals(frRequired)) sql += " and u.frRequired = 1 ";
			else if("N".equals(frRequired)) sql += " and u.frRequired = 0 ";
		}
		if (consignmentCounter != null && consignmentCounter.length() > 0){
			if("Y".equals(consignmentCounter)) sql += " and qr.isConsignmentCounter = 1 ";
			else if("N".equals(consignmentCounter)) sql += " and qr.isConsignmentCounter = 0 ";
		}
		if (seasonalWithdrawal != null && seasonalWithdrawal.length() > 0){
			if("Y".equals(seasonalWithdrawal)) sql += " and q.seasonalWithdrawal is not null ";
			else if("N".equals(seasonalWithdrawal)) sql += " and q.seasonalWithdrawal is null ";
		}
		
		Boolean appliedAdminFlag = false;
		Boolean frAppliedFlag = false;
		Boolean firstReturnFlag = false;
		
		if (appliedAdmin != null && appliedAdmin.length() > 0) appliedAdminFlag = true;
		if (frApplied != null && frApplied.length() > 0) frAppliedFlag = true;
		if (firstReturn != null && firstReturn.length() > 0) firstReturnFlag = true;
		
		if(appliedAdminFlag || frAppliedFlag || firstReturnFlag) {
			sql += " and ( ";
			if(appliedAdminFlag) {// 1
				if(frAppliedFlag) {// 1 1
					if(firstReturnFlag) {// 1 1 1
						if("Y".equals(appliedAdmin)) sql += " iqr.isApplyFR = 1 ";
						else if("N".equals(appliedAdmin)) sql += " iqr.isApplyFR = 0 ";
						if("Y".equals(frApplied)) sql += " or q.isFRApplied = 1 ";
						else if("N".equals(frApplied)) sql += " or q.isFRApplied = 0 ";
						if("New".equals(firstReturn)) sql += " or ( q.isReturnGoods = 1 and q.isReturnNewGoods = 1 ) ";
						else if("Same".equals(firstReturn)) sql += " or ( q.isReturnGoods = 1 and q.isReturnNewGoods = 0 ) ";
						else if("NA".equals(firstReturn)) sql += " or q.isReturnGoods = 0 ";
					} else {// 1 1 0
						if("Y".equals(appliedAdmin)) sql += " iqr.isApplyFR = 1 ";
						else if("N".equals(appliedAdmin)) sql += " iqr.isApplyFR = 0 ";
						if("Y".equals(frApplied)) sql += " or q.isFRApplied = 1 ";
						else if("N".equals(frApplied)) sql += " or q.isFRApplied = 0 ";
					}
				} else {// 1 0
					if(firstReturnFlag) {// 1 0 1
						if("Y".equals(appliedAdmin)) sql += " iqr.isApplyFR = 1 ";
						else if("N".equals(appliedAdmin)) sql += " iqr.isApplyFR = 0 ";
						if("New".equals(firstReturn)) sql += " or ( q.isReturnGoods = 1 and q.isReturnNewGoods = 1 ) ";
						else if("Same".equals(firstReturn)) sql += " or ( q.isReturnGoods = 1 and q.isReturnNewGoods = 0 ) ";
						else if("NA".equals(firstReturn)) sql += " or q.isReturnGoods = 0 ";
					} else {// 1 0 0
						if("Y".equals(appliedAdmin)) sql += " iqr.isApplyFR = 1 ";
						else if("N".equals(appliedAdmin)) sql += " iqr.isApplyFR = 0 ";
					}
				}
			} else {// 0
				if(frAppliedFlag) {// 0 1
					if(firstReturnFlag) {// 0 1 1
						if("Y".equals(frApplied)) sql += " q.isFRApplied = 1 ";
						else if("N".equals(frApplied)) sql += " q.isFRApplied = 0 ";
						if("New".equals(firstReturn)) sql += " or ( q.isReturnGoods = 1 and q.isReturnNewGoods = 1 ) ";
						else if("Same".equals(firstReturn)) sql += " or ( q.isReturnGoods = 1 and q.isReturnNewGoods = 0 ) ";
						else if("NA".equals(firstReturn)) sql += " or q.isReturnGoods = 0 ";
					} else {// 0 1 0
						if("Y".equals(frApplied)) sql += " q.isFRApplied = 1 ";
						else if("N".equals(frApplied)) sql += " q.isFRApplied = 0 ";
					}
				} else {// 0 0 1
					if("New".equals(firstReturn)) sql += " ( q.isReturnGoods = 1 and q.isReturnNewGoods = 1 ) ";
					else if("Same".equals(firstReturn)) sql += " ( q.isReturnGoods = 1 and q.isReturnNewGoods = 0 ) ";
					else if("NA".equals(firstReturn)) sql += " q.isReturnGoods = 0 ";
				}
			}
			sql += " ) ";
		}
		
		if (dataCollection != null && dataCollection.length() > 0){
			if("Y".equals(dataCollection)) sql += " and iqr.isNoField = 0 ";
			else if("N".equals(dataCollection)) sql += " and iqr.isNoField = 1 ";
		}
		
		sql += " group by iqr.IndoorQuotationRecordId, qr.QuotationRecordId, q.QuotationId, iqr.ReferenceMonth, iqr.ReferenceDate, pp.Code, u.CPIBasePeriod, u.Code, u.EnglishName, " + 
				"q.Status, iqr.Status, o.FirmCode, o.Name, ot.Code,ot.englishName, p.ProductId, p.CountryOfOrigin, pa1.SpecificationName , pa2.SpecificationName ,pa3.SpecificationName , " + 
				"pa4.SpecificationName ,pa5.SpecificationName, ps1.Value, ps2.Value, ps3.Value, ps4.Value, ps5.Value, qr.NPrice, qr.SPrice, iqr.LastNPrice, iqr.LastSPrice, " + 
				"iqr.PreviousNPrice, iqr.PreviousSPrice, iqr.CurrentNPrice, iqr.CurrentSPrice, qr.Reason, qr.Remark, iqr.IsNewRecruitment, qr.IsProductChange, iqr.Remark, " + 
				"q.FRField, q.IsFRFieldPercentage, q.FRAdmin, q.IsFRAdminPercentage, qr.FR, qr.IsFRPercentage, qr.IsConsignmentCounter, qr.ConsignmentCounterName, " + 
				"qr.ConsignmentCounterRemark, iqr.IsApplyFR, q.IsFRApplied,q.isReturnGoods, q.IsReturnNewGoods, iqr.IsCurrentPriceKeepNo, q.SeasonalWithdrawal, u.Seasonality, q.IsUseFRAdmin, " + 
				"q.LastFRAppliedDate, iqr.IsNoField " ;
		
		sql += " order by iqr.referenceMonth, pp.Code, u.CPIBasePeriod, u.Code, iqr.indoorQuotationRecordId ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		//2018-01-09 cheung_cheng export by item id
		if (itemId != null && itemId.size() > 0){
			query.setParameterList("itemId", itemId);
		}
		if (outletTypes != null && outletTypes.size() > 0){
			query.setParameterList("outletTypes", outletTypes);
		}
		
		query.addScalar("indoorQuotationRecordId",StandardBasicTypes.STRING);
		query.addScalar("quotationRecordId",StandardBasicTypes.STRING);
		//2018-01-09 cheung_cheng missing quotation id
		query.addScalar("quotationId",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("referenceDate",StandardBasicTypes.DATE);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("unitCode",StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName",StandardBasicTypes.STRING);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("indoorQuotationRecordStatus",StandardBasicTypes.STRING);
		query.addScalar("outletCode",StandardBasicTypes.STRING);
		query.addScalar("outletName",StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode",StandardBasicTypes.STRING);
		query.addScalar("productId",StandardBasicTypes.STRING);
		query.addScalar("countryOfOrigin",StandardBasicTypes.STRING);
		query.addScalar("productAttr1",StandardBasicTypes.STRING);
		query.addScalar("productAttr2",StandardBasicTypes.STRING);
		query.addScalar("productAttr3",StandardBasicTypes.STRING);
		query.addScalar("productAttr4",StandardBasicTypes.STRING);
		query.addScalar("productAttr5",StandardBasicTypes.STRING);
		query.addScalar("surveyNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("surveySPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qrReason",StandardBasicTypes.STRING);
		query.addScalar("qrRemark",StandardBasicTypes.STRING);
		query.addScalar("isNewRecruitment",StandardBasicTypes.INTEGER);
		query.addScalar("isProductChange",StandardBasicTypes.INTEGER);
		query.addScalar("iqrRemark",StandardBasicTypes.STRING);
		query.addScalar("FRField",StandardBasicTypes.DOUBLE);
		query.addScalar("isFRFieldPercentage",StandardBasicTypes.INTEGER);
		query.addScalar("FRAdmin",StandardBasicTypes.DOUBLE);
		query.addScalar("isFRAdminPercentage",StandardBasicTypes.INTEGER);
		query.addScalar("FR",StandardBasicTypes.DOUBLE);
		query.addScalar("isFRPercentage",StandardBasicTypes.INTEGER);
		query.addScalar("isConsignmentCounter",StandardBasicTypes.INTEGER);
		query.addScalar("consignmentCounterName",StandardBasicTypes.STRING);
		query.addScalar("consignmentCounterRemark",StandardBasicTypes.STRING);
		query.addScalar("isApplyFR",StandardBasicTypes.INTEGER);
		query.addScalar("isFRApplied",StandardBasicTypes.INTEGER);
//		query.addScalar("isReturnNewGoods",StandardBasicTypes.INTEGER);
		query.addScalar("isReturnNewGoods",StandardBasicTypes.STRING);
		query.addScalar("isKeepNum",StandardBasicTypes.INTEGER);
		query.addScalar("seasonalWithdraw",StandardBasicTypes.STRING);
		query.addScalar("seasonality",StandardBasicTypes.STRING);
		query.addScalar("isUseFRAdmin",StandardBasicTypes.INTEGER);
		query.addScalar("lastFRAppliedDate",StandardBasicTypes.DATE);
		query.addScalar("isNoField",StandardBasicTypes.INTEGER);
		query.setResultTransformer(Transformers.aliasToBean(FRAdjustment.class));
		
		return query.list();
	}
	
	public List<SupermarketProductReview> getSupermarketProductReview(List<Integer> purpose, 
			List<Integer> cpiSurveyForm, String productRemarks, Date startMonth, Date endMonth) {

		String referenceMonth = String.format("format(iqr.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String productNotAvailableFrom = String.format("format(iqr.productNotAvailableFrom, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select o.outletId as outletId, o.name as outletName "
				+ ", q.quotationId as quotationId, iqr.indoorQuotationRecordId as indoorQuotationRecordId"
				+ ", case when iqr.referenceMonth is null then '' else " + referenceMonth + " end as referenceMonth "
				+ ", u.code + ' - ' + u.englishName as unit "
				+ ", substring(ot.code, 8, 3) + ' - ' + ot.englishName as outletType "
				+ ", fp1.value as productAttributes1, fp2.value as productAttributes2, fp3.value as productAttributes3 "
				+ ", fp4.value as productAttributes4, fp5.value as productAttributes5 "
				+ ", qr.availability as availability "
				+ ", case when iqr.productNotAvailableFrom is null then '' else " + productNotAvailableFrom + " end as productNotAvailableFrom "
				+ ", qr.productRemark as productRemarks "
				+ ", qr.nPrice as surveyNPrice, qr.sPrice as surveySPrice "
				+ ", iqr.currentNPrice as editedNPrice, iqr.currentSPrice as editedSPrice "
				+ ", iqr.currentSPrice / iqr.previousSPrice * 100 as pr "
				+ ", qr.remark as priceRemarks "
				+ ", case when u.isFreshItem = 1 then 'Y' else '' end as freshItem "
				
				+ " from IndoorQuotationRecord as iqr "
				+ "  left join iqr.quotationRecord as qr "
				+ "  left join qr.outlet as o "
				+ "  left join iqr.quotation as q "
				+ "  left join q.unit as u "
				+ "  left join u.subItem as si "
				+ "  left join si.outletType as ot "
				+ "  left join ot.item as i "
				+ "  left join i.subGroup as sg "
				+ "  left join sg.group as g "
				+ "  left join q.product as product "
				+ "  left join product.fullSpecifications as fp1 on fp1.sequence = 1 "
				+ "  left join product.fullSpecifications as fp2 on fp2.sequence = 2 "
				+ "  left join product.fullSpecifications as fp3 on fp3.sequence = 3 "
				+ "  left join product.fullSpecifications as fp4 on fp4.sequence = 4 "
				+ "  left join product.fullSpecifications as fp5 on fp5.sequence = 5 "
				+ "  left join u.purpose as p "
				
				+ " where iqr.referenceMonth >= :startMonth and iqr.referenceMonth <= :endMonth ";
		
		if (purpose != null && purpose.size() > 0){
			hql += " and p.purposeId in (:purpose) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			hql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		if (!StringUtils.isEmpty(productRemarks)){
			if("Y".equals(productRemarks)) hql += " and qr.productRemark is not null ";
			else if("N".equals(productRemarks)) hql += " and qr.productRemark is null ";
		}
		
		hql += " group by o.outletId, o.name, q.quotationId, iqr.indoorQuotationRecordId, iqr.referenceMonth "
			+ ", u.code + ' - ' + u.englishName, substring(ot.code, 8, 3) + ' - ' + ot.englishName "
			+ ", product.productId, fp1.value, fp2.value, fp3.value, fp4.value, fp5.value " 
			+ ", qr.availability, iqr.productNotAvailableFrom, qr.productRemark "
			+ ", qr.nPrice, qr.sPrice, iqr.currentNPrice, iqr.currentSPrice "
			+ ", iqr.currentSPrice / iqr.previousSPrice, qr.remark, u.isFreshItem ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SupermarketProductReview.class));
		
		return query.list();
	}
	

	public QuotationRecordHistoryStatisticLast2Year getLastTwoYearStatistic(Integer indoorQuotationRecordId, Date referenceMonth){
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(referenceMonth);
		cal.add(Calendar.YEAR, -2);
		Date referenceMonthEnd = cal.getTime();
				
		String hql = "select max(us.maxSPrice) as maxSPrice, min(us.minSPrice) as minSPrice"
				+ " from IndoorQuotationRecord iqr"
				+ " left join iqr.quotation as q "
				+ " left join q.unit as u "
				+ "	left join u.unitStatistics as us"
				+ " where us.referenceMonth <= :referenceMonth and us.referenceMonth >= :referenceMonthEnd"
				+ " and iqr.indoorQuotationRecordId = :indoorQuotationRecordId"
				+ "	group by u.unitId";
		
		Query query = this.getSession().createQuery(hql);
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("referenceMonthEnd", referenceMonthEnd);
		query.setParameter("indoorQuotationRecordId", indoorQuotationRecordId);

		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordHistoryStatisticLast2Year.class));
		
		return (QuotationRecordHistoryStatisticLast2Year) query.uniqueResult();
	}

//	public UnitStatistic getUnitStatic(Integer indoorQuotationRecordId,
//			Date referenceMonth) {
		
//		String hql = "select us.unitStatisticId as unitStatisticId,"
//				+ "	us.referenceMonth as referenceMonth,"
//				+ " us.sumCurrentSPrice as sumCurrentSPrice,"
//				+ " us.countCurrentSPrice as countCurrentSPrice,"
//				+ "	us.averageCurrentSPrice as averageCurrentSPrice,"
//				+ " us.sumLastSPrice as sumLastSPrice,"
//				+ " us.countLastSPrice as countLastSPrice,"
//				+ " us.averageLastSPrice as averageLastSPrice,"
//				+ " us.finalPRSPrice as finalPRSPrice,"
//				+ " us.standardDeviationSPrice as standardDeviationSPrice,"
//				+ "	us.medianSPrice as medianSPrice,"
//				+ " us.minSPrice as minSPrice,"
//				+ " us.maxSPrice as maxSPrice,"
//				+ " us.lastHasPriceSumCurrentSPrice as lastHasPriceSumCurrentSPrice,"
//				+ " us.lastHasPriceCountCurrentSPrice as lastHasPriceCountCurrentSPrice,"
//				+ " us.lastHasPriceAverageCurrentSPrice as lastHasPriceAverageCurrentSPrice,"
//				+ " us.lastHasPriceReferenceMonth as lastHasPriceReferenceMonth,"
//				+ " us.averagePRSPrice as averagePRSPrice,"
//				+ " us.countPRSPrice as countPRSPrice,"
//				+ " us.sumPRSPrice as sumPRSPrice,"
//				+ " us.standardDeviationPRSPrice as standardDeviationPRSPrice,"
//				+ " us.medianPRPrice as medianPRPrice,"
//				+ " us.minPRPrice as minPRPrice,"
//				+ " us.maxPRPrice as maxPRPrice,"
//				+ " us.deviationSum as deviationSum,"
//				+ " us.variance as variance,"
//				+ " us.prSPriceDeviationSum as prSPriceDeviationSum,"
//				+ " us.prSPriceVariance as prSPriceVariance,"
//				+ " us.countPRNPrice as countPRNPrice,"
//				+ " us.prNPriceDeviationSum as prNPriceDeviationSum,"
//				+ " us.prNPriceVariance as prNPriceVariance,"
//				+ " us.standardDeviationPRNPrice as standardDeviationPRNPrice,"
//				+ " us.sumPRNPrice as sumPRNPrice,"
//				+ " us.minNPrice as minNPrice,"
//				+ " us.maxNPrice as maxNPrice"
//				+ " from IndoorQuotationRecord iqr"
//				+ " left join iqr.quotation as q "
//				+ " left join q.unit as u "
//				+ "	left join u.unitStatistics as us"
//				+ " where us.referenceMonth = :referenceMonth "
//				+ " and iqr.indoorQuotationRecordId = :indoorQuotationRecordId"
//				+ "	group by us.unitStatisticId,"
//				+ "	us.referenceMonth,"
//				+ " us.sumCurrentSPrice,"
//				+ " us.countCurrentSPrice,"
//				+ "	us.averageCurrentSPrice,"
//				+ " us.sumLastSPrice,"
//				+ " us.countLastSPrice,"
//				+ " us.averageLastSPrice,"
//				+ " us.finalPRSPrice,"
//				+ " us.standardDeviationSPrice,"
//				+ "	us.medianSPrice,"
//				+ " us.minSPrice,"
//				+ " us.maxSPrice,"
//				+ " us.lastHasPriceSumCurrentSPrice,"
//				+ " us.lastHasPriceCountCurrentSPrice,"
//				+ " us.lastHasPriceAverageCurrentSPrice,"
//				+ " us.lastHasPriceReferenceMonth,"
//				+ " us.averagePRSPrice,"
//				+ " us.countPRSPrice,"
//				+ " us.sumPRSPrice,"
//				+ " us.standardDeviationPRSPrice,"
//				+ " us.medianPRPrice,"
//				+ " us.minPRPrice,"
//				+ " us.maxPRPrice,"
//				+ " us.deviationSum,"
//				+ " us.variance,"
//				+ " us.prSPriceDeviationSum,"
//				+ " us.prSPriceVariance,"
//				+ " us.countPRNPrice,"
//				+ " us.prNPriceDeviationSum,"
//				+ " us.prNPriceVariance,"
//				+ " us.standardDeviationPRNPrice,"
//				+ " us.sumPRNPrice,"
//				+ " us.minNPrice,"
//				+ " us.maxNPrice";
		
//		Query query = this.getSession().createQuery(hql);
//		query.setParameter("referenceMonth", referenceMonth);
//		query.setParameter("indoorQuotationRecordId", indoorQuotationRecordId);
	
//		query.setResultTransformer(Transformers.aliasToBean(UnitStatistic.class));
//		
//		return (UnitStatistic) query.uniqueResult();
//	}
	
//	public UnitStatistic getPreviousUnitStatic(Integer indoorQuotationRecordId,
//			Date referenceMonth) {
//		
//		String hql = "select us.unitStatisticId as unitStatisticId,"
//				+ "	us.referenceMonth as referenceMonth,"
//				+ " us.sumCurrentSPrice as sumCurrentSPrice,"
//				+ " us.countCurrentSPrice as countCurrentSPrice,"
//				+ "	us.averageCurrentSPrice as averageCurrentSPrice,"
//				+ " us.sumLastSPrice as sumLastSPrice,"
//				+ " us.countLastSPrice as countLastSPrice,"
//				+ " us.averageLastSPrice as averageLastSPrice,"
//				+ " us.finalPRSPrice as finalPRSPrice,"
//				+ " us.standardDeviationSPrice as standardDeviationSPrice,"
//				+ "	us.medianSPrice as medianSPrice,"
//				+ " us.minSPrice as minSPrice,"
//				+ " us.maxSPrice as maxSPrice,"
//				+ " us.lastHasPriceSumCurrentSPrice as lastHasPriceSumCurrentSPrice,"
//				+ " us.lastHasPriceCountCurrentSPrice as lastHasPriceCountCurrentSPrice,"
//				+ " us.lastHasPriceAverageCurrentSPrice as lastHasPriceAverageCurrentSPrice,"
//				+ " us.lastHasPriceReferenceMonth as lastHasPriceReferenceMonth,"
//				+ " us.averagePRSPrice as averagePRSPrice,"
//				+ " us.countPRSPrice as countPRSPrice,"
//				+ " us.sumPRSPrice as sumPRSPrice,"
//				+ " us.standardDeviationPRSPrice as standardDeviationPRSPrice,"
//				+ " us.medianPRPrice as medianPRPrice,"
//				+ " us.minPRPrice as minPRPrice,"
//				+ " us.maxPRPrice as maxPRPrice,"
//				+ " us.deviationSum as deviationSum,"
//				+ " us.variance as variance,"
//				+ " us.prSPriceDeviationSum as prSPriceDeviationSum,"
//				+ " us.prSPriceVariance as prSPriceVariance,"
//				+ " us.countPRNPrice as countPRNPrice,"
//				+ " us.prNPriceDeviationSum as prNPriceDeviationSum,"
//				+ " us.prNPriceVariance as prNPriceVariance,"
//				+ " us.standardDeviationPRNPrice as standardDeviationPRNPrice,"
//				+ " us.sumPRNPrice as sumPRNPrice,"
//				+ " us.minNPrice as minNPrice,"
//				+ " us.maxNPrice as maxNPrice"
//				+ " from IndoorQuotationRecord iqr"
//				+ " left join iqr.quotation as q "
//				+ " left join q.unit as u "
//				+ "	left join u.unitStatistics as us"
//				+ " where us.referenceMonth < :referenceMonth "
//				+ " and iqr.indoorQuotationRecordId = :indoorQuotationRecordId"
//				+ "	group by us.unitStatisticId, "
//				+ "	us.referenceMonth,"
//				+ " us.sumCurrentSPrice,"
//				+ " us.countCurrentSPrice,"
//				+ "	us.averageCurrentSPrice,"
//				+ " us.sumLastSPrice,"
//				+ " us.countLastSPrice,"
//				+ " us.averageLastSPrice,"
//				+ " us.finalPRSPrice,"
//				+ " us.standardDeviationSPrice,"
//				+ "	us.medianSPrice,"
//				+ " us.minSPrice,"
//				+ " us.maxSPrice,"
//				+ " us.lastHasPriceSumCurrentSPrice,"
//				+ " us.lastHasPriceCountCurrentSPrice,"
//				+ " us.lastHasPriceAverageCurrentSPrice,"
//				+ " us.lastHasPriceReferenceMonth,"
//				+ " us.averagePRSPrice,"
//				+ " us.countPRSPrice,"
//				+ " us.sumPRSPrice,"
//				+ " us.standardDeviationPRSPrice,"
//				+ " us.medianPRPrice,"
//				+ " us.minPRPrice,"
//				+ " us.maxPRPrice,"
//				+ " us.deviationSum,"
//				+ " us.variance,"
//				+ " us.prSPriceDeviationSum,"
//				+ " us.prSPriceVariance,"
//				+ " us.countPRNPrice,"
//				+ " us.prNPriceDeviationSum,"
//				+ " us.prNPriceVariance,"
//				+ " us.standardDeviationPRNPrice,"
//				+ " us.sumPRNPrice,"
//				+ " us.minNPrice,"
//				+ " us.maxNPrice"
//				+ "	order by us.referenceMonth desc";
//		
//		Query query = this.getSession().createQuery(hql);
//		query.setParameter("referenceMonth", referenceMonth);
//		query.setParameter("indoorQuotationRecordId", indoorQuotationRecordId);
//		query.setMaxResults(1);
//		query.setResultTransformer(Transformers.aliasToBean(UnitStatistic.class));
//		
//		return (UnitStatistic) query.uniqueResult();
//	}

	public List<SummaryStatistic> getSummaryStatistic(List<Integer> purpose, Integer unitId, 
			List<Integer> cpiSurveyForm, String dataCollection, Date period) {

		String referenceMonth = String.format("format(iqr.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String dataCollectionDate = String.format("format(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String keepNumber = String.format("format(qs.keepNoStartMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select u.unitId as unitId, u.code as unitCode, u.englishName as unitName "
				+ ", q.quotationId as quotationId, iqr.indoorQuotationRecordId as indoorQuotationRecordId"
				+ ", case when iqr.referenceMonth is null then '' else " + referenceMonth + " end as referenceMonth "
				+ ", case when qr.collectionDate is null then '' else " + dataCollectionDate + " end as dataCollectionDate "
				+ ", o.outletId as outletId, o.name as outletName "
				+ ", product.productId as productId "
				+ ", fp1.value as productAttributes1, fp2.value as productAttributes2, fp3.value as productAttributes3 "
				+ ", qr.availability as productAvailability "
				+ ", iqr.lastNPrice as lastNPrice, iqr.lastSPrice as lastSPrice "
				+ ", iqr.previousNPrice as previousNPrice, iqr.previousSPrice as previousSPrice "
				+ ", iqr.currentNPrice as editedNPrice, iqr.currentSPrice as editedSPrice "
				+ ", iqr.currentSPrice / iqr.previousSPrice * 100 as quotationRecordPR "
				+ ", case when qs.keepNoStartMonth is null then '' else " + keepNumber + "end as keepNumber "
				+ ", qr.reason as priceReason "
				+ ", case when iqr.isOutlier = 1 then 'Y' else '' end as outlierCase "
				+ ", iqr.outlierRemark as outlierRemarks "
				+ ", case when qr.isNewRecruitment = 1 then 'Y' else '' end as newRecruitment "
				
				+ " from IndoorQuotationRecord as iqr "
				+ "  left join iqr.quotationRecord as qr "
				+ "  left join qr.outlet as o "
				+ "  left join iqr.quotation as q "
				+ "  left join q.unit as u "
				/*+ "  left join u.subItem as si "
				+ "  left join si.outletType as ot "
				+ "  left join ot.item as i "
				+ "  left join i.subGroup as sg "
				+ "  left join sg.group as g "*/
				+ "  left join u.purpose as p "
				+ "  left join q.product as product "
				+ "  left join product.fullSpecifications as fp1 on fp1.sequence = 1 "
				+ "  left join product.fullSpecifications as fp2 on fp2.sequence = 2 "
				+ "  left join product.fullSpecifications as fp3 on fp3.sequence = 3 "
				+ "  left join q.quotationStatistics as qs "
				
				+ " where iqr.referenceMonth = :period "
				+ "  and ( qs.quotationStatisticId is null or qs.quotationStatisticId is not null and qs.referenceMonth = iqr.referenceMonth ) ";
		
		if (purpose != null && purpose.size() > 0){
			hql += " and p.purposeId in (:purpose) ";
		}
		if (unitId != null){
			hql += " and u.unitId = :unitId ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			hql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		if (!StringUtils.isEmpty(dataCollection)){
			if("Y".equals(dataCollection)) hql += " and iqr.isNoField = 0 ";
			else if("N".equals(dataCollection)) hql += " and iqr.isNoField = 1 ";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("period", period);
		
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		if (unitId != null){
			query.setParameter("unitId", unitId);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryStatistic.class));
		
		return query.list();
	}
	
	public Integer countQuotationRecords(Integer indoorQuotationRecordId, Date referenceMonth){
		String hql = "select count(qr.quotationRecordId) "
				+ " from IndoorQuotationRecord iqr"
				+ " left join iqr.quotation as q "
				+ " left join q.unit as u "
				+ " left join u.quotations as qs"
				+ " left join qs.quotationRecords as qr"
				+ " left join qr.assignment as a"
				+ " left join a.surveyMonth as sm"
				+ " where iqr.indoorQuotationRecordId = :indoorQuotationRecordId"
				+ " and sm.referenceMonth = :referenceMonth";
		Query query = this.getSession().createQuery(hql);
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("indoorQuotationRecordId", indoorQuotationRecordId);
		query.setMaxResults(1);
		return ((Long)query.uniqueResult()).intValue();
		
	}

	@SuppressWarnings("unchecked")
	public List<SupermarketProductReview> getSupermarketProductReview(Date startMonth, Date endMonth, List<Integer> purpose, List<Integer> unitId, List<Integer> cpiSurveyForm, String priceRemark, String dataConversionRemarks){
/*		String hql = 
				  "select iqr.indoorQuotationRecordId as indoorQuotationRecordId"
				+ ", qr.quotationRecordId as fieldQuotationRecordId"
				+ ", q.quotationId as quotationId"
				+ ", iqr.referenceMonth as referenceMonthD"
				+ ", iqr.referenceDate as referenceDate"
				+ ", pp.code as purpose"
				+ ", u.cpiBasePeriod as cpiBasePeriod"
				+ ", g.code as groupCode"
				+ ", g.englishName as groupName"
				+ ", i.code as itemCode"
				+ ", i.englishName as itemName"
				+ ", u.code as varietyCode"
				+ ", u.chineseName as varietyChineseName"
				+ ", u.englishName as varietyEnglishName"
				+ ", q.status as quotationStatus"
				+ ", iqr.status as dataConversionStatus"
				+ ", o.firmCode as outletCode"
				+ ", o.name as outletName"
				+ ", ot.code as outletTypeCode"
				+ ", ot.englishName as outletTypeEnglishName"
				+ ", p.productId as productId"
				+ ", p.countryOfOrigin as countryOfOrigin"
				//+ ", concat(pfs1.specificationName, ': ', pfs1.value) as productAttributes1"
				//+ ", concat(pfs2.specificationName, ': ', pfs2.value) as productAttributes2"
				//+ ", concat(pfs3.specificationName, ': ', pfs3.value) as productAttributes3"
				//+ ", concat(pfs4.specificationName, ': ', pfs4.value) as productAttributes4"
				//+ ", concat(pfs5.specificationName, ': ', pfs5.value) as productAttributes5"
				//+ ", concat(pfs6.specificationName, ': ', pfs6.value) as productAttributes6"
				//+ ", concat(pfs7.specificationName, ': ', pfs7.value) as productAttributes7"
				//+ ", concat(pfs8.specificationName, ': ', pfs8.value) as productAttributes8"
				//+ ", concat(pfs9.specificationName, ': ', pfs9.value) as productAttributes9"
				//+ ", concat(pfs10.specificationName, ': ', pfs10.value) as productAttributes10"
				//+ ", concat(pfs11.specificationName, ': ', pfs11.value) as productAttributes11"
				//+ ", concat(pfs12.specificationName, ': ', pfs12.value) as productAttributes12"
				//+ ", concat(pfs13.specificationName, ': ', pfs13.value) as productAttributes13"
				//+ ", concat(pfs14.specificationName, ': ', pfs14.value) as productAttributes14"
				//+ ", concat(pfs15.specificationName, ': ', pfs15.value) as productAttributes15"
				//+ ", concat(pfs16.specificationName, ': ', pfs16.value) as productAttributes16"
				//+ ", concat(pfs17.specificationName, ': ', pfs17.value) as productAttributes17"
				//+ ", concat(pfs18.specificationName, ': ', pfs18.value) as productAttributes18"
				
				+ ", pfs1.specificationName as productAttributesName1"
				+ ", pfs2.specificationName as productAttributesName2"
				+ ", pfs3.specificationName as productAttributesName3"
				+ ", pfs4.specificationName as productAttributesName4"
				+ ", pfs5.specificationName as productAttributesName5"
				+ ", pfs6.specificationName as productAttributesName6"
				+ ", pfs7.specificationName as productAttributesName7"
				+ ", pfs8.specificationName as productAttributesName8"
				+ ", pfs9.specificationName as productAttributesName9"
				+ ", pfs10.specificationName as productAttributesName10"
				+ ", pfs11.specificationName as productAttributesName11"
				+ ", pfs12.specificationName as productAttributesName12"
				+ ", pfs13.specificationName as productAttributesName13"
				+ ", pfs14.specificationName as productAttributesName14"
				+ ", pfs15.specificationName as productAttributesName15"
				+ ", pfs16.specificationName as productAttributesName16"
				+ ", pfs17.specificationName as productAttributesName17"
				+ ", pfs18.specificationName as productAttributesName18"
								
				+ ", pfs1.value as productAttributesValue1"
				+ ", pfs2.value as productAttributesValue2"
				+ ", pfs3.value as productAttributesValue3"
				+ ", pfs4.value as productAttributesValue4"
				+ ", pfs5.value as productAttributesValue5"
				+ ", pfs6.value as productAttributesValue6"
				+ ", pfs7.value as productAttributesValue7"
				+ ", pfs8.value as productAttributesValue8"
				+ ", pfs9.value as productAttributesValue9"
				+ ", pfs10.value as productAttributesValue10"
				+ ", pfs11.value as productAttributesValue11"
				+ ", pfs12.value as productAttributesValue12"
				+ ", pfs13.value as productAttributesValue13"
				+ ", pfs14.value as productAttributesValue14"
				+ ", pfs15.value as productAttributesValue15"
				+ ", pfs16.value as productAttributesValue16"
				+ ", pfs17.value as productAttributesValue17"
				+ ", pfs18.value as productAttributesValue18"
				
				+ ", qr.productRemark as productRemarks"
				+ ", iqr.isProductNotAvailable as isProductNotavailable"
				+ ", iqr.productNotAvailableFrom as prodNotAvailableFrom"
				+ ", qr.availability as availability"
				+ ", qr.nPrice as surveyNPrice"
				+ ", qr.sPrice as surveySPrice"
				+ ", concat(uom.chineseName, ' & ',uom.englishName) as uomName"
				+ ", qr.uomValue as uomValue"
				+ ", iqr.lastNPrice as lastNPrice"
				+ ", iqr.lastSPrice as lastSPrice"
				+ ", iqr.previousNPrice as previousNPrice"
				+ ", iqr.previousSPrice as previousSPrice"
				+ ", iqr.currentNPrice as currentNPrice"
				+ ", iqr.currentSPrice as currentSPrice"
				+ ", qs.averageCurrentSPrice as qsAverageCurrentSPrice"
				+ ", qs.averageLastSPrice as qsAverageLastSPrice"
				+ ", qs.lastHasPriceAverageCurrentSPrice as qsLastHasPriceAverageCurrentSPrice"
				+ ", us.averageCurrentSPrice as usAverageCurrentSPrice"
				+ ", us.averageLastSPrice as usAverageLastSPrice"
				+ ", us.lastHasPriceAverageCurrentSPrice as usLastHasPriceAverageCurrentSPrice"
				+ ", qr.reason as fieldReason"
				+ ", qr.remark as priceRemarks"
				+ ", iqr.isNoField as isNoField"
				+ ", iqr.remark as dataConversionRemark"
				+ ", u.isFreshItem as isFreshItem"
				+ ", si.compilationMethod as compilationMethod"
				+ ", qru.staffCode as fieldOfficerCode"
				+ ", concat(iqru.staffCode,' - ',iqru.englishName) as indoorOfficer"
				
				+ " from IndoorQuotationRecord as iqr "
				+ " left join iqr.quotationRecord as qr"
				
				+ " left join qr.quotation as q"
				+ " left join qr.product as p"
				+ " left join qr.outlet as o"
				+ " left join q.unit as u"
				+ " left join u.unitStatistics as us on iqr.referenceMonth = us.referenceMonth"
				+ " left join u.purpose as pp"
				+ " left join qr.uom as uom"
				+ " left join u.subItem as si"
				+ " left join si.outletType as ot"
				+ " left join ot.item as i"
				+ " left join i.subGroup as sg"
				+ " left join sg.group as g"
				+ " left join q.quotationStatistics as qs on iqr.referenceMonth = qs.referenceMonth"
				+ " left join qr.user as qru"
				+ " left join iqr.user as iqru"
				+ " left join p.fullSpecifications as pfs1 on pfs1.sequence = 1"
				+ " left join p.fullSpecifications as pfs2 on pfs2.sequence = 2"
				+ " left join p.fullSpecifications as pfs3 on pfs3.sequence = 3"
				+ " left join p.fullSpecifications as pfs4 on pfs4.sequence = 4"
				+ " left join p.fullSpecifications as pfs5 on pfs5.sequence = 5"
				+ " left join p.fullSpecifications as pfs6 on pfs6.sequence = 6"
				+ " left join p.fullSpecifications as pfs7 on pfs7.sequence = 7"
				+ " left join p.fullSpecifications as pfs8 on pfs8.sequence = 8"
				+ " left join p.fullSpecifications as pfs9 on pfs9.sequence = 9"
				+ " left join p.fullSpecifications as pfs10 on pfs10.sequence = 10"
				+ " left join p.fullSpecifications as pfs11 on pfs11.sequence = 11"
				+ " left join p.fullSpecifications as pfs12 on pfs12.sequence = 12"
				+ " left join p.fullSpecifications as pfs13 on pfs13.sequence = 13"
				+ " left join p.fullSpecifications as pfs14 on pfs14.sequence = 14"
				+ " left join p.fullSpecifications as pfs15 on pfs15.sequence = 15"
				+ " left join p.fullSpecifications as pfs16 on pfs16.sequence = 16"
				+ " left join p.fullSpecifications as pfs17 on pfs17.sequence = 17"
				+ " left join p.fullSpecifications as pfs18 on pfs18.sequence = 18"	
				+ " where 1=1 "
				+ "";
		
		hql += " and iqr.referenceMonth >= :startMonth and iqr.referenceMonth <= :endMonth ";
		
		if (purpose != null && purpose.size() > 0){
			hql += " and pp.purposeId in (:purpose) ";
		}
		
		if (unitId != null && unitId.size() > 0){
			hql += " and u.unitId in (:unitId) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			hql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		if ("Y".equals(priceRemark)){
			hql += " and (qr.remark is not null and qr.remark <> '') ";
		} else if ("N".equals(priceRemark)) {
			hql += " and (qr.remark is null or qr.remark = '') ";
		}
		
		if ("Y".equals(dataConversionRemarks)){
			hql += " and (iqr.remark is not null and iqr.remark <> '') ";
			
		} else if ("N".equals(dataConversionRemarks)) {
			hql += " and (iqr.remark is null or iqr.remark = '') ";
		}
				
		hql += " order by iqr.referenceMonth asc, pp.code asc, u.cpiBasePeriod asc, u.code asc, iqr.indoorQuotationRecordId asc";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		query.setResultTransformer(Transformers.aliasToBean(SupermarketProductReview.class));
		
		return query.list();
		*/
		
		/******Change to SQL*******/	
		String referenceMonth = String.format("FORMAT(iqr.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		String sqlSelect = "SELECT iqr.indoorQuotationRecordId AS indoorQuotationRecordId, "
				 +" qr.quotationRecordId AS fieldQuotationRecordId , "
				 +" q.quotationId AS quotationId , "
				 +" CASE "
				 +"     WHEN iqr.ReferenceMonth IS NOT NULL THEN FORMAT(iqr.ReferenceMonth, 'yyyyMM', 'en-us') "
				 +"     ELSE '' "
				 +" END AS referenceMonth , "
				 +" iqr.referenceDate AS referenceDate , "
				 +" iqr.productNotAvailableFrom AS prodNotAvailableFrom , "
				 +" pp.code AS purpose , "
				 +" u.cpiBasePeriod AS cpiBasePeriod , "
				 +" g.code AS groupCode , "
				 +" g.englishName AS groupName , "
				 +" i.code AS itemCode , "
				 +" i.englishName AS itemName , "
				 +" u.code AS varietyCode , "
				 +" u.chineseName AS varietyChineseName , "
				 +" u.englishName AS varietyEnglishName , "
				 +" q.status AS quotationStatus , "
				 +" iqr.status AS dataConversionStatus , "
				 +" o.firmCode AS outletCode , "
				 +" o.name AS outletName , "
				 +" RIGHT(ot.code, 3) AS outletTypeCode , "
				 +" ot.englishName AS outletTypeEnglishName , "
				 +" p.productId AS productId , "
				 +" p.countryOfOrigin AS countryOfOrigin , "
				 +" pa1.specificationName AS productAttributesName1 , "
				 +" pa2.specificationName AS productAttributesName2 , "
				 +" pa3.specificationName AS productAttributesName3 , "
				 +" pa4.specificationName AS productAttributesName4 , "
				 +" pa5.specificationName AS productAttributesName5 , "
				 +" pa6.specificationName AS productAttributesName6 , "
				 +" pa7.specificationName AS productAttributesName7 , "
				 +" pa8.specificationName AS productAttributesName8 , "
				 +" pa9.specificationName AS productAttributesName9 , "
				 +" pa10.specificationName AS productAttributesName10 , "
				 +" pa11.specificationName AS productAttributesName11 , "
				 +" pa12.specificationName AS productAttributesName12 , "
				 +" pa13.specificationName AS productAttributesName13 , "
				 +" pa14.specificationName AS productAttributesName14 , "
				 +" pa15.specificationName AS productAttributesName15 , "
				 +" pa16.specificationName AS productAttributesName16 , "
				 +" pa17.specificationName AS productAttributesName17 , "
				 +" pa18.specificationName AS productAttributesName18 , "
				 +" ps1.value AS productAttributesValue1 , "
				 +" ps2.value AS productAttributesValue2 , "
				 +" ps3.value AS productAttributesValue3 , "
				 +" ps4.value AS productAttributesValue4 , "
				 +" ps5.value AS productAttributesValue5 , "
				 +" ps6.value AS productAttributesValue6 , "
				 +" ps7.value AS productAttributesValue7 , "
				 +" ps8.value AS productAttributesValue8 , "
				 +" ps9.value AS productAttributesValue9 , "
				 +" ps10.value AS productAttributesValue10 , "
				 +" ps11.value AS productAttributesValue11 , "
				 +" ps12.value AS productAttributesValue12 , "
				 +" ps13.value AS productAttributesValue13 , "
				 +" ps14.value AS productAttributesValue14 , "
				 +" ps15.value AS productAttributesValue15 , "
				 +" ps16.value AS productAttributesValue16 , "
				 +" ps17.value AS productAttributesValue17 , "
				 +" ps18.value AS productAttributesValue18 , "
				 +" qr.productRemark AS productRemarks , "
				 +" iqr.isProductNotAvailable AS isProductNotavailable , "
				 +" qr.availability AS availability , "
				 +" qr.nPrice AS surveyNPrice , "
				 +" qr.sPrice AS surveySPrice , "
				 +" concat(uom.chineseName,' & ',uom.englishName) AS uomName , "
				 +" qr.uomValue AS uomValue , "
				 +" iqr.lastNPrice AS lastNPrice , "
				 +" iqr.lastSPrice AS lastSPrice , "
				 +" iqr.previousNPrice AS previousNPrice , "
				 +" iqr.previousSPrice AS previousSPrice , "
				 +" iqr.currentNPrice AS currentNPrice , "
				 +" iqr.currentSPrice AS currentSPrice , "
				 +" qs.averageCurrentSPrice AS qsAverageCurrentSPrice , "
				 +" qs.averageLastSPrice AS qsAverageLastSPrice , "
				 +" qs.lastHasPriceAverageCurrentSPrice AS qsLastHasPriceAverageCurrentSPrice , "
				 +" us.averageCurrentSPrice AS usAverageCurrentSPrice , "
				 +" us.averageLastSPrice AS usAverageLastSPrice , "
				 +" us.lastHasPriceAverageCurrentSPrice AS usLastHasPriceAverageCurrentSPrice , "
				 +" qr.reason AS fieldReason , "
				 +" qr.remark AS priceRemarks , "
				 +" iqr.isNoField AS isNoField , "
				 +" iqr.remark AS dataConversionRemark , "
				 +" u.isFreshItem AS isFreshItem , "
				 +" si.compilationMethod AS compilationMethod , "
				 +" qru.staffCode AS fieldOfficerCode , "
				 +" concat(iqru.staffCode, ' - ', iqru.englishName) AS indoorOfficer ";

				String sqlTable = " IndoorQuotationRecord AS iqr "
				 +" LEFT JOIN quotationRecord AS qr ON iqr.QuotationRecordId = qr.QuotationRecordId "
				 +" LEFT JOIN quotation AS q ON iqr.QuotationId = q.QuotationId "
				 +" LEFT JOIN product AS p ON qr.ProductId = p.ProductId "
				 +" LEFT JOIN outlet AS o ON qr.OutletId = o.OutletId "
				 +" LEFT JOIN unit AS u ON q.UnitId = u.UnitId "
				 +" LEFT JOIN purpose AS pp ON u.PurposeId = pp.PurposeId "
				 +" LEFT JOIN uom AS uom ON qr.UOMId = uom.UOMId "
				 +" LEFT JOIN subItem AS si ON u.SubItemId = si.SubItemId "
				 +" LEFT JOIN outletType AS ot ON si.OutletTypeId = ot.OutletTypeId "
				 +" LEFT JOIN item AS i ON ot.ItemId = i.ItemId "
				 +" LEFT JOIN subGroup AS sg ON i.SubGroupId = sg.SubGroupId "
				 +" LEFT JOIN [group] AS g ON sg.GroupId = g.GroupId "
				 +" LEFT JOIN [User] AS qru ON qr.UserId = qru.UserId "
				 +" LEFT JOIN [User] AS iqru ON iqr.UserId = iqru.UserId "
				 +" LEFT JOIN [ProductGroup] AS pg ON p.ProductGroupId = pg.ProductGroupId "
				 +" LEFT JOIN ProductAttribute AS pa1 ON pa1.ProductGroupId = pg.ProductGroupId "
				 +" AND pa1.Sequence = 1 "
				 +" LEFT JOIN ProductSpecification AS ps1 ON ps1.ProductAttributeId = pa1.ProductAttributeId "
				 +" AND ps1.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa2 ON pa2.ProductGroupId = pg.ProductGroupId "
				 +" AND pa2.Sequence = 2 "
				 +" LEFT JOIN ProductSpecification AS ps2 ON ps2.ProductAttributeId = pa2.ProductAttributeId "
				 +" AND ps2.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa3 ON pa3.ProductGroupId = pg.ProductGroupId "
				 +" AND pa3.Sequence = 3 "
				 +" LEFT JOIN ProductSpecification AS ps3 ON ps3.ProductAttributeId = pa3.ProductAttributeId "
				 +" AND ps3.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa4 ON pa4.ProductGroupId = pg.ProductGroupId "
				 +" AND pa4.Sequence = 4 "
				 +" LEFT JOIN ProductSpecification AS ps4 ON ps4.ProductAttributeId = pa4.ProductAttributeId "
				 +" AND ps4.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa5 ON pa5.ProductGroupId = pg.ProductGroupId "
				 +" AND pa5.Sequence = 5 "
				 +" LEFT JOIN ProductSpecification AS ps5 ON ps5.ProductAttributeId = pa5.ProductAttributeId "
				 +" AND ps5.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa6 ON pa6.ProductGroupId = pg.ProductGroupId "
				 +" AND pa6.Sequence = 6 "
				 +" LEFT JOIN ProductSpecification AS ps6 ON ps6.ProductAttributeId = pa6.ProductAttributeId "
				 +" AND ps6.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa7 ON pa7.ProductGroupId = pg.ProductGroupId "
				 +" AND pa7.Sequence = 7 "
				 +" LEFT JOIN ProductSpecification AS ps7 ON ps7.ProductAttributeId = pa7.ProductAttributeId "
				 +" AND ps7.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa8 ON pa8.ProductGroupId = pg.ProductGroupId "
				 +" AND pa8.Sequence = 8 "
				 +" LEFT JOIN ProductSpecification AS ps8 ON ps8.ProductAttributeId = pa8.ProductAttributeId "
				 +" AND ps8.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa9 ON pa9.ProductGroupId = pg.ProductGroupId "
				 +" AND pa9.Sequence = 9 "
				 +" LEFT JOIN ProductSpecification AS ps9 ON ps9.ProductAttributeId = pa9.ProductAttributeId "
				 +" AND ps9.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa10 ON pa10.ProductGroupId = pg.ProductGroupId "
				 +" AND pa10.Sequence = 10 "
				 +" LEFT JOIN ProductSpecification AS ps10 ON ps10.ProductAttributeId = pa10.ProductAttributeId "
				 +" AND ps10.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa11 ON pa11.ProductGroupId = pg.ProductGroupId "
				 +" AND pa11.Sequence = 11 "
				 +" LEFT JOIN ProductSpecification AS ps11 ON ps11.ProductAttributeId = pa11.ProductAttributeId "
				 +" AND ps11.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa12 ON pa12.ProductGroupId = pg.ProductGroupId "
				 +" AND pa12.Sequence = 12 "
				 +" LEFT JOIN ProductSpecification AS ps12 ON ps12.ProductAttributeId = pa12.ProductAttributeId "
				 +" AND ps12.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa13 ON pa13.ProductGroupId = pg.ProductGroupId "
				 +" AND pa13.Sequence = 13 "
				 +" LEFT JOIN ProductSpecification AS ps13 ON ps13.ProductAttributeId = pa13.ProductAttributeId "
				 +" AND ps13.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa14 ON pa14.ProductGroupId = pg.ProductGroupId "
				 +" AND pa14.Sequence = 14 "
				 +" LEFT JOIN ProductSpecification AS ps14 ON ps14.ProductAttributeId = pa14.ProductAttributeId "
				 +" AND ps14.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa15 ON pa15.ProductGroupId = pg.ProductGroupId "
				 +" AND pa15.Sequence = 15 "
				 +" LEFT JOIN ProductSpecification AS ps15 ON ps15.ProductAttributeId = pa15.ProductAttributeId "
				 +" AND ps15.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa16 ON pa16.ProductGroupId = pg.ProductGroupId "
				 +" AND pa16.Sequence = 16 "
				 +" LEFT JOIN ProductSpecification AS ps16 ON ps16.ProductAttributeId = pa16.ProductAttributeId "
				 +" AND ps16.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa17 ON pa17.ProductGroupId = pg.ProductGroupId "
				 +" AND pa17.Sequence = 17 "
				 +" LEFT JOIN ProductSpecification AS ps17 ON ps17.ProductAttributeId = pa17.ProductAttributeId "
				 +" AND ps17.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa18 ON pa18.ProductGroupId = pg.ProductGroupId "
				 +" AND pa18.Sequence = 18 "
				 +" LEFT JOIN ProductSpecification AS ps18 ON ps18.ProductAttributeId = pa18.ProductAttributeId "
				 +" AND ps18.ProductId = p.ProductId "
				 +" LEFT JOIN [QuotationStatistic] AS qs ON q.QuotationId = qs.QuotationId "
				 +" AND iqr.referenceMonth = qs.referenceMonth "
				 +" LEFT JOIN [UnitStatistic] as us ON us.[UnitId] = u.[UnitId] "
				 +" AND iqr.referenceMonth = us.referenceMonth ";

				String sqlTable2 = " IndoorQuotationRecord as iqr "
				 +" LEFT JOIN quotationRecord AS qr ON iqr.QuotationRecordId = qr.QuotationRecordId "
				 +" LEFT JOIN quotation AS q ON iqr.QuotationId = q.QuotationId "
				 +" LEFT JOIN product AS p ON q.ProductId = p.ProductId "
				 +" LEFT JOIN outlet AS o ON q.OutletId = o.OutletId "
				 +" LEFT JOIN unit AS u ON q.UnitId = u.UnitId "
				 +" LEFT JOIN purpose AS pp ON u.PurposeId = pp.PurposeId "
				 +" LEFT JOIN uom AS uom ON qr.UOMId = uom.UOMId "
				 +" LEFT JOIN subItem AS si ON u.SubItemId = si.SubItemId "
				 +" LEFT JOIN outletType AS ot ON si.OutletTypeId = ot.OutletTypeId "
				 +" LEFT JOIN item AS i ON ot.ItemId = i.ItemId "
				 +" LEFT JOIN subGroup AS sg ON i.SubGroupId = sg.SubGroupId "
				 +" LEFT JOIN [group] AS g ON sg.GroupId = g.GroupId "
				 +" LEFT JOIN [User] AS qru ON qr.UserId = qru.UserId "
				 +" LEFT JOIN [User] AS iqru ON iqr.UserId = iqru.UserId "
				 +" LEFT JOIN [ProductGroup] AS pg ON p.ProductGroupId = pg.ProductGroupId "
				 +" LEFT JOIN ProductAttribute AS pa1 ON pa1.ProductGroupId = pg.ProductGroupId "
				 +" AND pa1.Sequence = 1 "
				 +" LEFT JOIN ProductSpecification AS ps1 ON ps1.ProductAttributeId = pa1.ProductAttributeId "
				 +" AND ps1.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa2 ON pa2.ProductGroupId = pg.ProductGroupId "
				 +" AND pa2.Sequence = 2 "
				 +" LEFT JOIN ProductSpecification AS ps2 ON ps2.ProductAttributeId = pa2.ProductAttributeId "
				 +" AND ps2.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa3 ON pa3.ProductGroupId = pg.ProductGroupId "
				 +" AND pa3.Sequence = 3 "
				 +" LEFT JOIN ProductSpecification AS ps3 ON ps3.ProductAttributeId = pa3.ProductAttributeId "
				 +" AND ps3.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa4 ON pa4.ProductGroupId = pg.ProductGroupId "
				 +" AND pa4.Sequence = 4 "
				 +" LEFT JOIN ProductSpecification AS ps4 ON ps4.ProductAttributeId = pa4.ProductAttributeId "
				 +" AND ps4.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa5 ON pa5.ProductGroupId = pg.ProductGroupId "
				 +" AND pa5.Sequence = 5 "
				 +" LEFT JOIN ProductSpecification AS ps5 ON ps5.ProductAttributeId = pa5.ProductAttributeId "
				 +" AND ps5.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa6 ON pa6.ProductGroupId = pg.ProductGroupId "
				 +" AND pa6.Sequence = 6 "
				 +" LEFT JOIN ProductSpecification AS ps6 ON ps6.ProductAttributeId = pa6.ProductAttributeId "
				 +" AND ps6.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa7 ON pa7.ProductGroupId = pg.ProductGroupId "
				 +" AND pa7.Sequence = 7 "
				 +" LEFT JOIN ProductSpecification AS ps7 ON ps7.ProductAttributeId = pa7.ProductAttributeId "
				 +" AND ps7.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa8 ON pa8.ProductGroupId = pg.ProductGroupId "
				 +" AND pa8.Sequence = 8 "
				 +" LEFT JOIN ProductSpecification AS ps8 ON ps8.ProductAttributeId = pa8.ProductAttributeId "
				 +" AND ps8.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa9 ON pa9.ProductGroupId = pg.ProductGroupId "
				 +" AND pa9.Sequence = 9 "
				 +" LEFT JOIN ProductSpecification AS ps9 ON ps9.ProductAttributeId = pa9.ProductAttributeId "
				 +" AND ps9.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa10 ON pa10.ProductGroupId = pg.ProductGroupId "
				 +" AND pa10.Sequence = 10 "
				 +" LEFT JOIN ProductSpecification AS ps10 ON ps10.ProductAttributeId = pa10.ProductAttributeId "
				 +" AND ps10.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa11 ON pa11.ProductGroupId = pg.ProductGroupId "
				 +" AND pa11.Sequence = 11 "
				 +" LEFT JOIN ProductSpecification AS ps11 ON ps11.ProductAttributeId = pa11.ProductAttributeId "
				 +" AND ps11.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa12 ON pa12.ProductGroupId = pg.ProductGroupId "
				 +" AND pa12.Sequence = 12 "
				 +" LEFT JOIN ProductSpecification AS ps12 ON ps12.ProductAttributeId = pa12.ProductAttributeId "
				 +" AND ps12.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa13 ON pa13.ProductGroupId = pg.ProductGroupId "
				 +" AND pa13.Sequence = 13 "
				 +" LEFT JOIN ProductSpecification AS ps13 ON ps13.ProductAttributeId = pa13.ProductAttributeId "
				 +" AND ps13.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa14 ON pa14.ProductGroupId = pg.ProductGroupId "
				 +" AND pa14.Sequence = 14 "
				 +" LEFT JOIN ProductSpecification AS ps14 ON ps14.ProductAttributeId = pa14.ProductAttributeId "
				 +" AND ps14.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa15 ON pa15.ProductGroupId = pg.ProductGroupId "
				 +" AND pa15.Sequence = 15 "
				 +" LEFT JOIN ProductSpecification AS ps15 ON ps15.ProductAttributeId = pa15.ProductAttributeId "
				 +" AND ps15.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa16 ON pa16.ProductGroupId = pg.ProductGroupId "
				 +" AND pa16.Sequence = 16 "
				 +" LEFT JOIN ProductSpecification AS ps16 ON ps16.ProductAttributeId = pa16.ProductAttributeId "
				 +" AND ps16.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa17 ON pa17.ProductGroupId = pg.ProductGroupId "
				 +" AND pa17.Sequence = 17 "
				 +" LEFT JOIN ProductSpecification AS ps17 ON ps17.ProductAttributeId = pa17.ProductAttributeId "
				 +" AND ps17.ProductId = p.ProductId "
				 +" LEFT JOIN ProductAttribute AS pa18 ON pa18.ProductGroupId = pg.ProductGroupId "
				 +" AND pa18.Sequence = 18 "
				 +" LEFT JOIN ProductSpecification AS ps18 ON ps18.ProductAttributeId = pa18.ProductAttributeId "
				 +" AND ps18.ProductId = p.ProductId "
				 +" LEFT JOIN [QuotationStatistic] AS qs ON q.QuotationId = qs.QuotationId "
				 +" AND iqr.referenceMonth = qs.referenceMonth "
				 +" LEFT JOIN [UnitStatistic] as us ON us.[UnitId] = u.[UnitId] "
				 +" AND iqr.referenceMonth = us.referenceMonth ";

				 String sqlWhere = " WHERE 1=1";
						sqlWhere += " and iqr.referenceMonth >= :startMonth and iqr.referenceMonth <= :endMonth ";

						if (purpose != null && purpose.size() > 0){
							sqlWhere += " and pp.purposeId in (:purpose) ";
						}
						
						if (unitId != null && unitId.size() > 0){
//							sql += " and u.unitId in (:unitId) ";
							sqlWhere += " and i.itemId in (:unitId) ";
						}
						
						if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
							sqlWhere += " and u.cpiQoutationType in (:cpiSurveyForm) ";
						}
						
						if ("Y".equals(priceRemark)){
							sqlWhere += " and (qr.remark is not null and qr.remark <> '') ";
						} else if ("N".equals(priceRemark)) {
							sqlWhere += " and (qr.remark is null or qr.remark = '') ";
						}
						
						if ("Y".equals(dataConversionRemarks)){
							sqlWhere += " and (iqr.remark is not null and iqr.remark <> '') ";
							
						} else if ("N".equals(dataConversionRemarks)) {
							sqlWhere += " and (iqr.remark is null or iqr.remark = '') ";
						}
						
				 String orderString = "SELECT ROW_NUMBER() OVER( "
				                       +" ORDER BY table1.referenceMonth ASC, table1.purpose ASC, table1.cpiBasePeriod ASC, table1.varietyCode ASC, "
				                       +" table1.indoorQuotationRecordId ASC) AS NO, ";

				StringBuilder sqlbd = new StringBuilder(); 
						sqlbd.append(orderString) 
						.append("table1.indoorQuotationRecordId, table1.fieldQuotationRecordId, table1.quotationId, table1.referenceMonth, table1.referenceDate,")
						.append("table1.indoorQuotationRecordId , table1.fieldQuotationRecordId ,table1.referenceMonth ,table1.referenceDate ,")
						.append("table1.prodNotAvailableFrom ,table1.purpose ,table1.cpiBasePeriod ,table1.groupCode ,table1.groupName ,table1.itemCode ,")
				        .append("table1.itemName ,table1.varietyCode ,table1.varietyChineseName ,table1.varietyEnglishName ,table1.quotationStatus ,")
				        .append("table1.dataConversionStatus ,table1.outletCode ,table1.outletName ,table1.outletTypeCode ,table1.outletTypeEnglishName ,")
				        .append("table1.productId ,table1.countryOfOrigin ,table1.productAttributesName1 ,table1.productAttributesName2 , table1.productAttributesName3 ,")
				        .append("table1.productAttributesName4 ,table1.productAttributesName5 ,table1.productAttributesName6 ,table1.productAttributesName7 ,")
				        .append("table1.productAttributesName8 ,table1.productAttributesName9 ,table1.productAttributesName10 ,table1.productAttributesName11 ,")
				        .append("table1.productAttributesName12 ,table1.productAttributesName13 ,table1.productAttributesName14 ,table1.productAttributesName15 ,")
				        .append("table1.productAttributesName16 ,table1.productAttributesName17 ,table1.productAttributesName18 ,table1.productAttributesValue1 ,")
				        .append("table1.productAttributesValue2 ,table1.productAttributesValue3 ,table1.productAttributesValue4 ,table1.productAttributesValue5 ,")
				        .append("table1.productAttributesValue6 ,table1.productAttributesValue7 ,table1.productAttributesValue8 ,table1.productAttributesValue9 ,")
				        .append("table1.productAttributesValue10 ,table1.productAttributesValue11 ,table1.productAttributesValue12 ,table1.productAttributesValue13 ,")
				        .append("table1.productAttributesValue14 ,table1.productAttributesValue15 ,table1.productAttributesValue16 ,table1.productAttributesValue17 ,")
				        .append("table1.productAttributesValue18 ,table1.productRemarks ,table1.isProductNotavailable ,table1.availability ,table1.surveyNPrice ,")
				        .append("table1.surveySPrice ,table1.uomName ,table1.uomValue ,table1.lastNPrice ,table1.lastSPrice ,table1.previousNPrice ,")
				        .append("table1.previousSPrice ,table1.currentNPrice ,table1.currentSPrice ,table1.qsAverageCurrentSPrice ,table1.qsAverageLastSPrice ,")
				        .append("table1.qsLastHasPriceAverageCurrentSPrice ,table1.usAverageCurrentSPrice ,table1.usAverageLastSPrice ,")
				        .append("table1.usLastHasPriceAverageCurrentSPrice ,table1.fieldReason ,table1.priceRemarks ,table1.isNoField ,table1.dataConversionRemark ,")
				        .append("table1.isFreshItem ,table1.compilationMethod ,table1.fieldOfficerCode ,table1.indoorOfficer ");

						sqlbd.append(" FROM (").append(sqlSelect).append(" FROM ").append(sqlTable).append(sqlWhere).append(" AND iqr.QuotationRecordId IS NOT NULL ") 
							 .append("UNION ").append(sqlSelect).append(" FROM ").append(sqlTable2).append(sqlWhere).append(" AND iqr.QuotationRecordId IS NULL ")
						.append(")table1");
						

//				sql += " order by iqr.referenceMonth asc, pp.code asc, u.cpiBasePeriod asc, u.code asc, iqr.indoorQuotationRecordId asc";
				
				SQLQuery query = this.getSession().createSQLQuery(sqlbd.toString());
				
				query.setParameter("startMonth", startMonth);
				query.setParameter("endMonth", endMonth);
				if (purpose != null && purpose.size() > 0){
					query.setParameterList("purpose", purpose);
				}
				if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
					query.setParameterList("cpiSurveyForm", cpiSurveyForm);
				}
				if (unitId != null && unitId.size() > 0){
					query.setParameterList("unitId", unitId);
				}
				
				query.addScalar("referenceMonth", StandardBasicTypes.STRING);
				query.addScalar("outletName", StandardBasicTypes.STRING);
				query.addScalar("quotationId", StandardBasicTypes.STRING);
				query.addScalar("indoorQuotationRecordId", StandardBasicTypes.STRING);
				query.addScalar("fieldQuotationRecordId", StandardBasicTypes.STRING);
				query.addScalar("referenceDate", StandardBasicTypes.STRING);
				query.addScalar("prodNotAvailableFrom", StandardBasicTypes.STRING);
				query.addScalar("outletCode", StandardBasicTypes.INTEGER);
				query.addScalar("productId", StandardBasicTypes.INTEGER);
				query.addScalar("productAttributesName1", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName2", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName3", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName4", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName5", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName6", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName7", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName8", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName9", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName10", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName11", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName12", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName13", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName14", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName15", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName16", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName17", StandardBasicTypes.STRING);
				query.addScalar("productAttributesName18", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue1", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue2", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue3", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue4", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue5", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue6", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue7", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue8", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue9", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue10", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue11", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue12", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue13", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue14", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue15", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue16", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue17", StandardBasicTypes.STRING);
				query.addScalar("productAttributesValue18", StandardBasicTypes.STRING);
				query.addScalar("availability", StandardBasicTypes.INTEGER);
				query.addScalar("productRemarks", StandardBasicTypes.STRING);
				query.addScalar("surveyNPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("surveySPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("priceRemarks", StandardBasicTypes.STRING);
				query.addScalar("purpose", StandardBasicTypes.STRING);
				query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
				query.addScalar("groupCode", StandardBasicTypes.STRING);
				query.addScalar("groupName", StandardBasicTypes.STRING);
				query.addScalar("itemCode", StandardBasicTypes.STRING);
				query.addScalar("itemName", StandardBasicTypes.STRING);
				query.addScalar("varietyCode", StandardBasicTypes.STRING);
				query.addScalar("varietyChineseName", StandardBasicTypes.STRING);
				query.addScalar("varietyEnglishName", StandardBasicTypes.STRING);
				query.addScalar("quotationStatus", StandardBasicTypes.STRING);
				query.addScalar("dataConversionStatus", StandardBasicTypes.STRING);
				query.addScalar("outletTypeCode", StandardBasicTypes.STRING);
				query.addScalar("outletTypeEnglishName", StandardBasicTypes.STRING);
				query.addScalar("countryOfOrigin", StandardBasicTypes.STRING);
				query.addScalar("isProductNotavailable", StandardBasicTypes.BOOLEAN);			
				query.addScalar("uomName", StandardBasicTypes.STRING);
				query.addScalar("uomValue", StandardBasicTypes.DOUBLE);
				query.addScalar("lastNPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("lastSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("previousNPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("previousSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("currentNPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("currentSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("qsAverageCurrentSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("qsAverageLastSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("qsLastHasPriceAverageCurrentSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("usAverageCurrentSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("usAverageLastSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("usLastHasPriceAverageCurrentSPrice", StandardBasicTypes.DOUBLE);
				query.addScalar("fieldReason", StandardBasicTypes.STRING);
				query.addScalar("isNoField", StandardBasicTypes.BOOLEAN);
				query.addScalar("dataConversionRemark", StandardBasicTypes.STRING);
				query.addScalar("isFreshItem", StandardBasicTypes.BOOLEAN);
				query.addScalar("compilationMethod", StandardBasicTypes.INTEGER);
				query.addScalar("fieldOfficerCode", StandardBasicTypes.STRING);
				query.addScalar("indoorOfficer", StandardBasicTypes.STRING);
				
				query.setResultTransformer(Transformers.aliasToBean(SupermarketProductReview.class));
				
				return query.list();
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	public Integer getProductAttributesMaxSeq(Date startMonth, Date endMonth,
			ArrayList<Integer> purpose, ArrayList<Integer> cpiSurveyForm, ArrayList<Integer> unitId,
			String dataCollection){
		
		String sql = "select top(1) prodAttr.Sequence "
				+ "from "
				+ "IndoorQuotationRecord as iqr "
				+ "inner join TourRecord as t on t.TourRecordId = iqr.QuotationRecordId "
				+ "left join QuotationRecord as qr on qr.QuotationRecordId = t.TourRecordId "
				+ "left join Quotation as q on q.QuotationId = qr.QuotationId "
				+ "left join Outlet as o on o.OutletId = qr.OutletId "
				+ "left join Unit as u on u.UnitId = q.UnitId "
				+ "left join Purpose as p on p.PurposeId = u.PurposeId "
				+ "left join SubItem as si on si.SubItemId = u.SubItemId "
				+ "left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ "left join Item as item on item.ItemId = ot.ItemId "
				+ "left join SubGroup as subGp on subGp.SubGroupId = item.SubGroupId "
				+ "left join [Group] as g on g.GroupId = subGp.GroupId "
				+ "left join Product as prod on prod.ProductId = qr.ProductId "
				+ "left join ProductGroup as prodGp on prodGp.ProductGroupId = prod.ProductGroupId "
				+ "left join ProductAttribute as prodAttr on prodAttr.ProductGroupId = prodGp.ProductGroupId "				
				+ " where 1=1 ";
		
		sql += " and iqr.referenceMonth >= :startMonth and iqr.referenceMonth <= :endMonth ";

		if (purpose != null && purpose.size() > 0){
			sql += " and p.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}

		if (unitId != null && unitId.size() > 0){
			sql += " and u.unitId in (:unitId) ";
		}
		
		if (dataCollection != null && dataCollection.length() > 0){
			if("Y".equals(dataCollection)) sql += " and iqr.isNoField = 0 ";
			else if("N".equals(dataCollection)) sql += " and iqr.isNoField = 1 ";
		}
		
		sql += "ORDER BY "
				+ "prodAttr.Sequence desc ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		}

		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0) {
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}

		if (unitId != null && unitId.size() > 0) {
			query.setParameterList("unitId", unitId);
		}
		
		return (Integer) query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListOfPackageTourQuotationRecords(Date startMonth, Date endMonth,
			//2018-01-04 cheung_cheng [MB9008] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
			ArrayList<Integer> purpose, ArrayList<Integer> cpiSurveyForm, ArrayList<Integer> itemId,
			String dataCollection, int maxSeq){
		
		//For Dynamic Generate SQL for Product Attributes (Current set to fixed value = 18)
		String productAttributesSQL = "";
		String productAttributesJoinTable = "";
		
		for(int i=1; i<maxSeq+1; i++){
			String prodAttr = "prodAttr" + i;
			String prodSpec = "prodSpec" + i;
			String productAttributes = "productAttributes" + i;
			
			productAttributesSQL += "case when ("+prodAttr+".SpecificationName is null and "+prodSpec+".Value is null) then '' else concat("+prodAttr+".SpecificationName,':',"+prodSpec+".Value) end as "+productAttributes+", ";
			
			productAttributesJoinTable += "left join ProductAttribute as "+prodAttr+" on "+prodAttr+".ProductGroupId = prodGp.ProductGroupId and "+prodAttr+".Sequence = "+String.valueOf(i)+" ";
			productAttributesJoinTable += "left join ProductSpecification as "+prodSpec+" on "+prodSpec+".ProductAttributeId = "+prodAttr+".ProductAttributeId and "+prodSpec+".ProductId = prod.ProductId ";
		}

		String referenceMonth = String.format("format(iqr.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		String referenceDate = String.format("format(iqr.ReferenceDate, '%s', 'en-us')", SystemConstant.REPORT_REFERENCE_DATE_FORMAT);
		
		String sql = "select iqr.IndoorQuotationRecordId as indoorQuotationRecordId, "
				+ "qr.QuotationRecordId as fieldQuotationRecordId, "
				+ "q.QuotationId as quotationId, "
				+ "case when iqr.ReferenceMonth is null then '' else " + referenceMonth + " end as referenceMonth, "
				+ "case when iqr.ReferenceDate is null then '' else " + referenceDate + " end as referenceDate, "
				+ "p.Code as purpose, "
				+ "u.CPIBasePeriod as cpiBasedPeriod, "
				+ "g.Code as groupCode, "
				+ "g.EnglishName as groupName, "
				+ "item.Code as itemCode, "
				+ "item.EnglishName as itemName, "
				+ "u.Code as varietyCode, "
				+ "u.EnglishName as varietyName, "
				+ "q.Status as quotationStatus, "
				+ "iqr.Status as dataConversionStatus, "
				+ "o.FirmCode as outletCode, "
				+ "o.Name as outletName, "
				+ "ot.Code as outletType, "
				+ "ot.EnglishName as outletTypeEnglishName, "
				+ "prod.ProductId as productId, "
				+ "prod.CountryOfOrigin as countryOfOrigin, "
				//Product Attributes
				+ productAttributesSQL
				+ "qr.Availability as availability, "
				+ "qr.NPrice as surveyNPrice, "
				+ "qr.SPrice as surveySPrice, "
				+ "iqr.LastNPrice as lastEditedNPrice, "
				+ "iqr.LastSPrice as lastEditedSPrice, "
				+ "iqr.PreviousNPrice as previousEditedNPrice, "
				+ "iqr.PreviousSPrice as previousEditedSPrice, "
				+ "iqr.CurrentNPrice as currentEditedNPrice, "
				+ "iqr.CurrentSPrice as currentEditedSPrice, "
				+ "case when iqr.previousNPrice > 0 then (iqr.currentNPrice / iqr.previousNPrice * 100) else null end as nPricePR, "
				+ "case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end as sPricePR, "
				+ "qr.Reason as reasonField, "
				+ "qr.Remark as priceRemarkField, "
				//2018-01-05 cheung_cheng Whether field work is needed" show the wrong opposite result
//				+ "case when iqr.IsNoField = 1 then 'Field' else 'No Field' end as whetherFieldworkIsNeeded, "
				+ "case when iqr.IsNoField = 1 then 'No Field' else 'Field' end as whetherFieldworkIsNeeded, "
				+ "iqr.Remark as dataConversionRemarks, "
				+ "q.CPICompilationSeries as cpiSeries, "
				+ "case when CPICompilationSeries like '%A%' then case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end end as prForA, "
				+ "case when CPICompilationSeries like '%B%' then case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end end prForB, "
				+ "case when CPICompilationSeries like '%C%' then case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end end as prForC, "
				+ "case when CPICompilationSeries like '%A%B%' then case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end end as prForAB, "
				+ "case when CPICompilationSeries like '%A%C%' then case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end end as prForAC, "
				+ "case when CPICompilationSeries like '%B%C%' then case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end end as prForBC, "
				+ "case when CPICompilationSeries like '%A%B%C%' then case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end end as prForABC, "
				+ "case when CPICompilationSeries like '%M%' then case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end end as prForM, "
				
				//
				+ "case when CPICompilationSeries like '%A%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "when CPICompilationSeries like '%A%' then ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)else null end)) "
					+ "OVER (PARTITION BY u.code, case when (prodAttr1.SpecificationName is not null and prodSpec1.Value is not null AND CPICompilationSeries like '%A%') then concat(prodAttr1.SpecificationName,':',prodSpec1.Value) "
					+ "WHEN (prodAttr1.SpecificationName IS NOT NULL AND CPICompilationSeries LIKE '%A%') THEN concat(prodAttr1.SpecificationName,':') "
					+ "end)) end "
				+ "as gmOfPRForAByVarietyCodeProductAttributes1, "
					
				+ "case when CPICompilationSeries like '%B%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "when CPICompilationSeries like '%B%' then ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)else null end)) "
					+ "OVER (PARTITION BY u.code, case when (prodAttr1.SpecificationName is not null and prodSpec1.Value is not null AND CPICompilationSeries like '%B%') then concat(prodAttr1.SpecificationName,':',prodSpec1.Value) "
					+ "WHEN (prodAttr1.SpecificationName IS NOT NULL AND CPICompilationSeries LIKE '%B%') THEN concat(prodAttr1.SpecificationName,':') "
					+ "end)) end "
				+ "as gmOfPRForBByVarietyCodeProductAttributes1, "
					
				+ "case when CPICompilationSeries like '%C%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "when CPICompilationSeries like '%C%' then ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)else null end)) "
					+ "OVER (PARTITION BY u.code, case when (prodAttr1.SpecificationName is not null and prodSpec1.Value is not null AND CPICompilationSeries like '%C%') then concat(prodAttr1.SpecificationName,':',prodSpec1.Value) "
					+ "WHEN (prodAttr1.SpecificationName IS NOT NULL AND CPICompilationSeries LIKE '%C%') THEN concat(prodAttr1.SpecificationName,':') "
					+ "end)) end "
				+ "as gmOfPRForCByVarietyCodeProductAttributes1, "
					
				+ "case when CPICompilationSeries like '%A%B%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "when CPICompilationSeries like '%A%B%' then ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)else null end)) "
					+ "OVER (PARTITION BY u.code, case when (prodAttr1.SpecificationName is not null and prodSpec1.Value is not null AND CPICompilationSeries like '%A%B%') then concat(prodAttr1.SpecificationName,':',prodSpec1.Value) "
					+ "WHEN (prodAttr1.SpecificationName IS NOT NULL AND CPICompilationSeries LIKE '%A%B%') THEN concat(prodAttr1.SpecificationName,':') "
					+ "end)) end "
				+ "as gmOfPRForABByVarietyCodeProductAttributes1, "
					
				+ "case when CPICompilationSeries like '%A%C%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "when CPICompilationSeries like '%A%C%' then ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)else null end)) "
					+ "OVER (PARTITION BY u.code, case when (prodAttr1.SpecificationName is not null and prodSpec1.Value is not null AND CPICompilationSeries like '%A%C%') then concat(prodAttr1.SpecificationName,':',prodSpec1.Value) "
					+ "WHEN (prodAttr1.SpecificationName IS NOT NULL AND CPICompilationSeries LIKE '%A%C%') THEN concat(prodAttr1.SpecificationName,':') "
					+ "end)) end "
				+ "as gmOfPRForACByVarietyCodeProductAttributes1, "
					
				+ "case when CPICompilationSeries like '%B%C%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "when CPICompilationSeries like '%B%C%' then ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)else null end)) "
					+ "OVER (PARTITION BY u.code, case when (prodAttr1.SpecificationName is not null and prodSpec1.Value is not null AND CPICompilationSeries like '%B%C%') then concat(prodAttr1.SpecificationName,':',prodSpec1.Value) "
					+ "WHEN (prodAttr1.SpecificationName IS NOT NULL AND CPICompilationSeries LIKE '%B%C%') THEN concat(prodAttr1.SpecificationName,':') "
					+ "end)) end "
					+ "as gmOfPRForBCByVarietyCodeProductAttributes1, "
					
				+ "case when CPICompilationSeries like '%A%B%C%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "when CPICompilationSeries like '%A%B%C%' then ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)else null end)) "
					+ "OVER (PARTITION BY u.code, case when (prodAttr1.SpecificationName is not null and prodSpec1.Value is not null AND CPICompilationSeries like '%A%B%C%') then concat(prodAttr1.SpecificationName,':',prodSpec1.Value) "
					+ "WHEN (prodAttr1.SpecificationName IS NOT NULL AND CPICompilationSeries LIKE '%A%B%C%') THEN concat(prodAttr1.SpecificationName,':') "
					+ "end)) end "
				+ "as gmOfPRForABCByVarietyCodeProductAttributes1, "
				
				+ "case when CPICompilationSeries like '%M%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "when CPICompilationSeries like '%M%' then ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)else null end)) "
					+ "OVER (PARTITION BY u.code, case when (prodAttr1.SpecificationName is not null and prodSpec1.Value is not null AND CPICompilationSeries like '%M%') then concat(prodAttr1.SpecificationName,':',prodSpec1.Value) "
					+ "WHEN (prodAttr1.SpecificationName IS NOT NULL AND CPICompilationSeries LIKE '%M%') THEN concat(prodAttr1.SpecificationName,':') "
					+ "end)) end "
				+ "as gmOfPRForMByVarietyCodeProductAttributes1, "
				//	
				
				+ "case when CPICompilationSeries like '%A%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "WHEN CPICompilationSeries like '%A%' THEN ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)ELSE NULL end)) "
					+ "OVER (PARTITION BY u.code)) end "
				+ "as gmOfPRForAByVarietyCode, "
				+ "case when CPICompilationSeries like '%B%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "WHEN CPICompilationSeries like '%B%' THEN ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)ELSE NULL end)) "
					+ "OVER (PARTITION BY u.code)) end "
				+ "as gmOfPRForBByVarietyCode, "
				+ "case when CPICompilationSeries like '%C%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "WHEN CPICompilationSeries like '%C%' THEN ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)ELSE NULL end)) "
					+ "OVER (PARTITION BY u.code)) end "
				+ "as gmOfPRForCByVarietyCode, "
				+ "case when CPICompilationSeries like '%A%B%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "WHEN CPICompilationSeries like '%A%B%' THEN ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)ELSE NULL end)) "
					+ "OVER (PARTITION BY u.code)) end "
				+ "as gmOfPRForABByVarietyCode, "
				+ "case when CPICompilationSeries like '%A%C%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "WHEN CPICompilationSeries like '%A%C%' THEN ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)ELSE NULL end)) "
					+ "OVER (PARTITION BY u.code)) end "
				+ "as gmOfPRForACByVarietyCode, "
				+ "case when CPICompilationSeries like '%B%C%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "WHEN CPICompilationSeries like '%B%C%' THEN ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)ELSE NULL end)) "
					+ "OVER (PARTITION BY u.code)) end "
				+ "as gmOfPRForBCByVarietyCode, "
				+ "case when CPICompilationSeries like '%A%B%C%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "WHEN CPICompilationSeries like '%A%B%C%' THEN ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)ELSE NULL end)) "
					+ "OVER (PARTITION BY u.code)) end "
				+ "as gmOfPRForABCByVarietyCode, "
				+ "case when CPICompilationSeries like '%M%' then EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null "
				+ "WHEN CPICompilationSeries like '%M%' THEN ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3)ELSE NULL end)) "
					+ "OVER (PARTITION BY u.code)) end "
				+ "as gmOfPRForMByVarietyCode, "
				+ "t.TourRecordId as tourRecordId, "
				+ "t.Day1Price as day1Price, t.Day2Price as day2Price, t.Day3Price as day3Price, t.Day4Price as day4Price, t.Day5Price as day5Price, "
				+ "t.Day6Price as day6Price, t.Day7Price as day7Price, t.Day8Price as day8Price, t.Day9Price as day9Price, t.Day10Price as day10Price, "
				+ "t.Day11Price as day11Price, t.Day12Price as day12Price, t.Day13Price as day13Price, t.Day14Price as day14Price, t.Day15Price as day15Price, "
				+ "t.Day16Price as day16Price, t.Day17Price as day17Price, t.Day18Price as day18Price, t.Day19Price as day19Price, t.Day20Price as day20Price, "
				+ "t.Day21Price as day21Price, t.Day22Price as day22Price, t.Day23Price as day23Price, t.Day24Price as day24Price, t.Day25Price as day25Price, "
				+ "t.Day26Price as day26Price, t.Day27Price as day27Price, t.Day28Price as day28Price, t.Day29Price as day29Price, t.Day30Price as day30Price, "
				+ "t.Day31Price as day31Price, "
				+ "t.ExtraPrice1Name as extraPrice1Name, "
				+ "t.ExtraPrice1Value as extraPrice1Value, "
				+ "case when t.IsExtraPrice1Count = 1 then 'Y' else 'N' end as isExtraPrice1Include, "
				+ "t.ExtraPrice2Name as extraPrice2Name, "
				+ "t.ExtraPrice2Value as extraPrice2Value, "
				+ "case when t.IsExtraPrice2Count = 1 then 'Y' else 'N' end as isExtraPrice2Include, "
				+ "t.ExtraPrice3Name as extraPrice3Name, "
				+ "t.ExtraPrice3Value as extraPrice3Value, "
				+ "case when t.IsExtraPrice3Count = 1 then 'Y' else 'N' end as isExtraPrice3Include, "
				+ "t.ExtraPrice4Name as extraPrice4Name, "
				+ "t.ExtraPrice4Value as extraPrice4Value, "
				+ "case when t.IsExtraPrice4Count = 1 then 'Y' else 'N' end as isExtraPrice4Include, "
				+ "t.ExtraPrice5Name as extraPrice5Name, "
				+ "t.ExtraPrice5Value as extraPrice5Value, "
				+ "case when t.IsExtraPrice5Count = 1 then 'Y' else 'N' end as isExtraPrice5Include "
				+ "from "
				+ "IndoorQuotationRecord as iqr "
				//2018-01-05 cheung_cheng [MB9012]  should not only generate record with tour record id
				+ "left join QuotationRecord as qr on qr.QuotationRecordId = iqr.QuotationRecordId "
				//2018-01-05 cheung_cheng [MB9012] The report should also be able to export all indoor quotation records (not just the records with tour form)
				+ "left join TourRecord as t on t.TourRecordId = iqr.QuotationRecordId "
//				+ "left join Quotation as q on q.QuotationId = qr.QuotationId "
				+ "left join Quotation as q on q.QuotationId = iqr.QuotationId "
//				+ "left join Outlet as o on o.OutletId = qr.OutletId "
				+ "left join Outlet as o on o.OutletId = CASE WHEN qr.OutletId IS NULL THEN  q.OutletId ELSE  qr.OutletId  END "
				+ "left join Unit as u on u.UnitId = q.UnitId "
				+ "left join Purpose as p on p.PurposeId = u.PurposeId "
				+ "left join SubItem as si on si.SubItemId = u.SubItemId "
				+ "left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ "left join Item as item on item.ItemId = ot.ItemId "
				+ "left join SubGroup as subGp on subGp.SubGroupId = item.SubGroupId "
				+ "left join [Group] as g on g.GroupId = subGp.GroupId "
//				+ "left join Product as prod on prod.ProductId = qr.ProductId "
				+ "left join Product as prod on prod.ProductId  = CASE WHEN qr.ProductId IS NULL THEN  q.ProductId ELSE  qr.ProductId  END "
				+ "left join ProductGroup as prodGp on prodGp.ProductGroupId = prod.ProductGroupId "
				+ productAttributesJoinTable
				
				+ " where 1=1 ";
		
		sql += " and iqr.referenceMonth >= :startMonth and iqr.referenceMonth <= :endMonth ";

		if (purpose != null && purpose.size() > 0){
			sql += " and p.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}

		//2018-01-04 cheung_cheng [MB9012] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
		if (itemId != null && itemId.size() > 0){
			sql += " and item.itemId in (:itemId) ";
		}
		
		if (dataCollection != null && dataCollection.length() > 0){
			if("Y".equals(dataCollection)) sql += " and iqr.isNoField = 0 ";
			else if("N".equals(dataCollection)) sql += " and iqr.isNoField = 1 ";
		}
		
		sql += "ORDER BY "
				+ "iqr.ReferenceMonth asc, "
				+ "p.Code asc, "
				+ "u.CPIBasePeriod asc, "
				+ "u.Code asc, "
				+ "iqr.IndoorQuotationRecordId asc";
		System.out.println("sql for tour package");
		System.out.println(sql);
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		}

		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0) {
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}

		//2018-01-04 cheung_cheng [MB9012] 1. Classification - The lookup table should show up to item level only (similar to MB9007) 
		if (itemId != null && itemId.size() > 0) {
			query.setParameterList("itemId", itemId);
		}

		addScalarForListOfPackageTourQuotationRecords(query);
		
		for(int i=1; i<maxSeq+1; i++){
			String alias = "productAttributes" + i;
			query.addScalar(alias, StandardBasicTypes.STRING);
		}

		//Step1 - Get result set 		
		List<Object[]> cleanedObjects = new ArrayList<Object[]>();
		List<Object[]> object = query.list();
		List<Map<String, Object>> result = new ArrayList<>();

		if (!object.isEmpty()) {
			if (object.get(0) instanceof Object[]) {
				cleanedObjects = object;
			} else {
				Object[] row;
				for (int i = 0; i < object.size(); i++) {
					row = new Object[1];
					row[0] = object.get(i);
					cleanedObjects.add(row);
				}
			}
		
		
			//Step2 - Obtain the column aliases
			List<NativeSQLQueryReturn> resultHeaders = query.getQueryReturns();
			List<String> headers = new ArrayList<>();
			
			if(!resultHeaders.isEmpty()){
				
				for (NativeSQLQueryReturn row : resultHeaders) {
					if (row instanceof NativeSQLQueryReturn){
						NativeSQLQueryScalarReturn header = (NativeSQLQueryScalarReturn)row;
						headers.add(header.getColumnAlias());
					}
				}
				
				//Step3 - Covert Object(row of record) into List for getting Product Attributes
				for (Object[] obj : cleanedObjects){
					Map<String,Object> record = new HashMap<>();
					for(int i=0; i<obj.length; i++){
						record.put(headers.get(i), obj[i]);
					}
					result.add(record);
				}
				
			}
		}
		return result;
	}
	
	public void addScalarForListOfPackageTourQuotationRecords(SQLQuery query){
		query.addScalar("indoorQuotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("fieldQuotationRecordId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("referenceDate", StandardBasicTypes.STRING);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("cpiBasedPeriod", StandardBasicTypes.STRING);
		query.addScalar("groupCode", StandardBasicTypes.STRING);
		query.addScalar("groupName", StandardBasicTypes.STRING);
		query.addScalar("itemCode", StandardBasicTypes.STRING);
		query.addScalar("itemName", StandardBasicTypes.STRING);
		query.addScalar("varietyCode", StandardBasicTypes.STRING);
		query.addScalar("varietyName", StandardBasicTypes.STRING);
		query.addScalar("quotationStatus", StandardBasicTypes.STRING);
		query.addScalar("dataConversionStatus", StandardBasicTypes.STRING);
		query.addScalar("outletCode", StandardBasicTypes.STRING);
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("outletType", StandardBasicTypes.STRING);
		query.addScalar("outletTypeEnglishName", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.INTEGER);
		query.addScalar("countryOfOrigin", StandardBasicTypes.STRING);
		query.addScalar("availability", StandardBasicTypes.INTEGER);
		query.addScalar("surveyNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("surveySPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("lastEditedNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("lastEditedSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousEditedNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousEditedSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentEditedNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentEditedSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("nPricePR", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("sPricePR", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("reasonField", StandardBasicTypes.STRING);
		query.addScalar("priceRemarkField", StandardBasicTypes.STRING);
		query.addScalar("whetherFieldworkIsNeeded", StandardBasicTypes.STRING);
		query.addScalar("dataConversionRemarks", StandardBasicTypes.STRING);
		query.addScalar("cpiSeries", StandardBasicTypes.STRING);
		query.addScalar("prForA", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("prForB", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("prForC", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("prForAB", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("prForAC", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("prForBC", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("prForABC", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("prForM", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForAByVarietyCodeProductAttributes1", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForBByVarietyCodeProductAttributes1", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForCByVarietyCodeProductAttributes1", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForABByVarietyCodeProductAttributes1", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForACByVarietyCodeProductAttributes1", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForBCByVarietyCodeProductAttributes1", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForABCByVarietyCodeProductAttributes1", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForMByVarietyCodeProductAttributes1", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForAByVarietyCode", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForBByVarietyCode", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForCByVarietyCode", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForABByVarietyCode", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForACByVarietyCode", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForBCByVarietyCode", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForABCByVarietyCode", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("gmOfPRForMByVarietyCode", StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("tourRecordId", StandardBasicTypes.INTEGER);
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
		query.addScalar("isExtraPrice1Include", StandardBasicTypes.STRING);
		query.addScalar("extraPrice2Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice2Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice2Include", StandardBasicTypes.STRING);
		query.addScalar("extraPrice3Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice3Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice3Include", StandardBasicTypes.STRING);
		query.addScalar("extraPrice4Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice4Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice4Include", StandardBasicTypes.STRING);
		query.addScalar("extraPrice5Name", StandardBasicTypes.STRING);
		query.addScalar("extraPrice5Value", StandardBasicTypes.DOUBLE);
		query.addScalar("isExtraPrice5Include", StandardBasicTypes.STRING);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getListOfPackageTourQuotationRecordsCpiBasedPeriod(Date startMonth, Date endMonth,
			ArrayList<Integer> purpose, ArrayList<Integer> cpiSurveyForm, ArrayList<Integer> unitId,
			String dataCollection){

		String hql = "select "
				+ " distinct u.cpiBasePeriod "
				
				+ " from QuotationRecord as qr "
				+ " inner join qr.indoorQuotationRecord as iqr "
				+ " inner join qr.quotation as q "
				+ " inner join q.unit as u "
				+ " inner join u.subItem as subItem "
				+ " inner join subItem.outletType as outletType "
				+ " inner join outletType.item as item "
				+ " inner join item.subGroup as subGroup "
				+ " inner join subGroup.group as g "
				+ " left join u.purpose as p "
				+ " left join qr.outlet as outlet "
				+ " left join qr.product as product "
				+ " left join product.fullSpecifications as fp1 on fp1.sequence = 1 "
				+ " left join product.fullSpecifications as fp2 on fp2.sequence = 2 "
				+ " left join product.fullSpecifications as fp3 on fp3.sequence = 3 "
				
				+ " where 1=1 ";
		
		hql += " and iqr.referenceMonth >= :startMonth and iqr.referenceMonth <= :endMonth ";

		if (purpose != null && purpose.size() > 0){
			hql += " and p.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			hql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}

		if (unitId != null && unitId.size() > 0){
			hql += " and u.unitId in (:unitId) ";
		}
		
		if (dataCollection != null && dataCollection.length() > 0){
			if("Y".equals(dataCollection)) hql += " and iqr.isNoField = 0 ";
			else if("N".equals(dataCollection)) hql += " and iqr.isNoField = 1 ";
		}
		
		hql += " order by u.cpiBasePeriod ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		}

		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0) {
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}

		if (unitId != null && unitId.size() > 0) {
			query.setParameterList("unitId", unitId);
		}
		
		return query.list();
	}
	
	public List<QuotationTimeSeries> getQuotationTimeSeriesReport(List<Integer> purpose, 
			List<Integer> unitId, List<Integer> cpiSurveyForm, Integer quotationId, Date period) {

		String referenceMonth = String.format("format(iqr.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String dataCollectionDate = String.format("format(qr.collectionDate, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String keepNumber = String.format("format(qs.keepNoStartMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = //" select i.itemId as itemId, i.code as itemCode, i.englishName as itemName, q.quotationId as quotationId "
				" select "
				+ " case when iqr.referenceMonth is null then '' else "+ referenceMonth + " end as referenceMonth "
				+ ", iqr.indoorQuotationRecordId as indoorQuotationRecordId "
				+ ", case when qr.collectionDate is null then '' else " + dataCollectionDate + " end as dataCollectionDate "
				+ ", o.outletId as outletId, o.name as outletName "
				+ ", product.productId as productId, fp1.value as productAttributes1 "
				+ ", fp2.value as productAttributes2, fp3.value as productAttributes3 "
				+ ", qr.availability as productAvailability "
				+ ", qr.nPrice as surveyNPrice, qr.sPrice as surveySPrice "
				+ ", iqr.currentNPrice as editedNPrice, iqr.currentSPrice as editedSPrice "
				+ ", ( iqr.currentSPrice / iqr.previousSPrice * 100 ) as quotationRecordPR "
				+ ", case when qs.keepNoStartMonth is null then '' else " + keepNumber + " end as keepNumber "
				+ ", qr.reason as priceReason "
				+ ", case when iqr.isOutlier = 1 then 'Y' else '' end as outlierCase "
				+ ", iqr.outlierRemark as outlierRemarks "
				
				+ " from IndoorQuotationRecord as iqr "
				+ "  left join iqr.quotationRecord as qr "
				+ "  left join qr.outlet as o "
				+ "  left join iqr.quotation as q "
				+ "  left join q.unit as u "
				+ "  left join u.subItem as si "
				+ "  left join si.outletType as ot "
				+ "  left join ot.item as i "
				/*+ "  left join i.subGroup as sg "
				+ "  left join sg.group as g "*/
				+ "  left join u.purpose as p "
				+ "  left join q.product as product "
				+ "  left join product.fullSpecifications as fp1 on fp1.sequence = 1 "
				+ "  left join product.fullSpecifications as fp2 on fp2.sequence = 2 "
				+ "  left join product.fullSpecifications as fp3 on fp3.sequence = 3 "
				+ "  left join q.quotationStatistics as qs "
				
				+ " where iqr.referenceMonth = :period "
				+ "  and ( qs.quotationStatisticId is null or qs.quotationStatisticId is not null and qs.referenceMonth = iqr.referenceMonth ) ";
		
		if (purpose != null && purpose.size() > 0){
			hql += " and p.purposeId in (:purpose) ";
		}
		if (unitId != null && unitId.size() > 0){
			hql += " and u.unitId in (:unitId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			hql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		if (quotationId != null){
			hql += " and q.quotationId = :quotationId ";
		}
		
		hql += //" group by i.itemId, i.code, i.englishName, q.quotationId, iqr.referenceMonth, iqr.indoorQuotationRecordId "
			" group by iqr.referenceMonth, iqr.indoorQuotationRecordId "
			+ ", qr.collectionDate, o.outletId, o.name, product.productId, fp1.value, fp2.value, fp3.value "
			+ ", qr.availability, qr.nPrice, qr.sPrice, iqr.currentNPrice, iqr.currentSPrice, iqr.currentSPrice / iqr.previousSPrice "
			+ ", qs.keepNoStartMonth, qr.reason, iqr.isOutlier, iqr.outlierRemark ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("period", period);
		
		if (purpose != null && purpose.size() > 0){
			query.setParameterList("purpose", purpose);
		}
		if (unitId != null && unitId.size() > 0){
			query.setParameterList("unitId", unitId);
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}
		if (quotationId != null){
			query.setParameter("quotationId", quotationId);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationTimeSeries.class));
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<QuotationStatisticsReportByVariety> getQuotationStatisticsReportByVariety(List<Integer> purpose, List<Integer> itemId, List<Integer> cpiSurveyForm, List<Integer> quotationId, Date referenceMonth, String type){

		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[GetQuotationStatisticsReportByUnit] :refMonth, :purposeId, :cpiQuotationType, :itemId, :quotationId, :type");
		
		query.setParameter("refMonth", referenceMonth);

		query.setParameter("type", type);
		
		StringBuilder builder = new StringBuilder();
		if(purpose != null) {
			builder.append("<query>");
			for (Integer purposeId : purpose){
				builder.append("<purposeId>"+purposeId+"</purposeId>");
			}
			builder.append("</query>");		
			query.setParameter("purposeId", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("purposeId", null);
		}
		
		if(cpiSurveyForm != null) {
			builder.append("<query>");
			for (Integer cpiQuotationType : cpiSurveyForm){
				builder.append("<cpiQuotationType>"+cpiQuotationType+"</cpiQuotationType>");
			}
			builder.append("</query>");		
			query.setParameter("cpiQuotationType", builder.toString());
			builder.setLength(0);
		}else {
			query.setParameter("cpiQuotationType", null);
		}
		
		if(itemId != null) {
			builder.append("<query>");
			for(Integer id : itemId) {
				builder.append("<itemId>"+id+"</itemId>");
			}
			builder.append("</query>");
			query.setParameter("itemId", builder.toString());
		}else {
			query.setParameter("itemId", null);
		}
		
		if(quotationId != null) {
			builder.append("<query>");
			for(Integer id : quotationId) {
				builder.append("<quotationId>"+id+"</quotationId>");
			}
			builder.append("</query>");
			query.setParameter("quotationId", builder.toString());
		}else {
			query.setParameter("quotationId", null);
		}
				
		query.addScalar("quotationId",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
//		query.addScalar("referenceDate",StandardBasicTypes.DATE);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("varietyCode",StandardBasicTypes.STRING);
		query.addScalar("chineseName",StandardBasicTypes.STRING);
		query.addScalar("englishName",StandardBasicTypes.STRING);
		query.addScalar("outletCode",StandardBasicTypes.STRING);
		query.addScalar("outletName",StandardBasicTypes.STRING);
		query.addScalar("outletType",StandardBasicTypes.STRING);
		query.addScalar("nPriceDataCollection1",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection2",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection3",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection4",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection5",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection6",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection7",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection8",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection9",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection10",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection11",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection12",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection13",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection14",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection15",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection16",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection17",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceDataCollection18",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection1",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection2",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection3",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection4",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection5",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection6",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection7",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection8",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection9",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection10",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection11",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection12",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection13",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection14",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection15",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection16",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection17",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceDataCollection18",StandardBasicTypes.DOUBLE);
		query.addScalar("cpiCompilationSeries",StandardBasicTypes.STRING);
		query.addScalar("compilationMethod",StandardBasicTypes.INTEGER);
		query.addScalar("seasonalityItem",StandardBasicTypes.STRING);
		query.addScalar("surveyType",StandardBasicTypes.INTEGER);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("quotationAveragePriceT1",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationAveragePriceT",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationStandardDeviationT1",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationStandardDeviationT",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationMinPriceT1",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationMinPriceT",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationMaxPriceT1",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationMaxPriceT",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationSumT1",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationSumT",StandardBasicTypes.DOUBLE);
		query.addScalar("quotationCountT1",StandardBasicTypes.INTEGER);
		query.addScalar("quotationCountT",StandardBasicTypes.INTEGER);
		query.addScalar("varietyAveragePriceT1",StandardBasicTypes.DOUBLE);
		query.addScalar("varietyAveragePriceT",StandardBasicTypes.DOUBLE);
		query.addScalar("varietyStandardDeviationT1",StandardBasicTypes.DOUBLE);
		query.addScalar("varietyStandardDeviationT",StandardBasicTypes.DOUBLE);
		query.addScalar("varietyMinPriceT1",StandardBasicTypes.DOUBLE);
		query.addScalar("varietyMinPriceT",StandardBasicTypes.DOUBLE);
		query.addScalar("varietyMaxPriceT1",StandardBasicTypes.DOUBLE);
		query.addScalar("varietyMaxPriceT",StandardBasicTypes.DOUBLE);
		query.addScalar("varietySumT1",StandardBasicTypes.DOUBLE);
		query.addScalar("varietySumT",StandardBasicTypes.DOUBLE);		
		query.addScalar("varietyCountT1",StandardBasicTypes.INTEGER);	
		query.addScalar("varietyCountT",StandardBasicTypes.INTEGER);	
		query.addScalar("varietyPriceRelativeT1",StandardBasicTypes.DOUBLE);	
		query.addScalar("varietyPriceRelativeT",StandardBasicTypes.DOUBLE);	
		query.addScalar("varietyCountPRT1",StandardBasicTypes.DOUBLE);	
		query.addScalar("varietyCountPRT",StandardBasicTypes.DOUBLE);
		
		//v_15
		query.addScalar("averagePRSPrice",StandardBasicTypes.DOUBLE);	
		
		//v_16
		query.addScalar("finalPRSPrice", StandardBasicTypes.DOUBLE);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationStatisticsReportByVariety.class));
		
		return query.list();

	}	
	
	@SuppressWarnings("unchecked")
	public List<QuotationRecordImputationReport> getQuotationRecordImputationReport(Date startMonth, Date endMonth,
			ArrayList<Integer> purpose, ArrayList<Integer> unitId,
			ArrayList<Integer> quotationId){
		String sql = ""
				+ "	SELECT "
				+ "	a.indoorQuotationRecordId AS indoorQuotationRecordId , "
				+ "	a.quotationId AS quotationId , "
				+ "	a.fieldQuotationRecordId AS fieldQuotationRecordId , "
				+ "	a.referenceMonth AS referenceMonth , "
				+ "	a.referenceDate AS referenceDate , "
				+ "	a.purpose AS purpose , "
				+ "	a.cpiBasePeriod AS cpiBasePeriod , "
				+ "	a.varietyCode AS varietyCode , "
				+ "	a.varietyChineseName AS varietyChineseName , "
				+ "	a.varietyEnglishName AS varietyEnglishName , "
				+ "	a.quotationStatus AS quotationStatus , "
				+ "	a.dataConversionStatus AS dataConversionStatus , "
				+ "	ROW_NUMBER() OVER (PARTITION BY a.QuotationId "
				+ "	ORDER BY a.referenceDate) AS quotationRecordSequence, "
				+ "	a.outletCode AS outletCode , "
				+ "	a.outletName AS outletName , "
				+ "	a.outletType AS outletType , "
				+ "	a.productId AS productId , "
				+ "	a.countryOfOrigin AS countryOfOrigin , "
				+ "	a.pa1Name AS pa1Name , "
				+ "	a.pa2Name AS pa2Name , "
				+ "	a.pa3Name AS pa3Name , "
				+ "	a.pa4Name AS pa4Name , "
				+ "	a.pa5Name AS pa5Name , "
				+ "	a.ps1Value AS ps1Value , "
				+ "	a.ps2Value AS ps2Value , "
				+ "	a.ps3Value AS ps3Value , "
				+ "	a.ps4Value AS ps4Value , "
				+ "	a.ps5Value AS ps5Value , "
				+ "	a.productAttributes1 AS productAttributes1 , "
				+ "	a.productAttributes2 AS productAttributes2 , "
				+ "	a.productAttributes3 AS productAttributes3 , "
				+ "	a.productAttributes4 AS productAttributes4 , "
				+ "	a.productAttributes5 AS productAttributes5 , "
				+ "	a.availability AS availability , "
				+ "	a.surveyNPrice AS surveyNPrice , "
				+ "	a.surveySPrice AS surveySPrice , "
				+ "	a.lastEditedNPrice AS lastEditedNPrice , "
				+ "	a.lastEditedSPrice AS lastEditedSPrice , "
				+ "	a.previousEditedNPrice AS previousEditedNPrice , "
				+ "	a.previousEditedSPrice AS previousEditedSPrice , "
				+ "	a.currentEditedNPrice AS currentEditedNPrice , "
				+ "	a.currentEditedSPrice AS currentEditedSPrice , "
				+ "	a.nPricePR AS nPricePR , "
				+ "	a.sPricePR AS sPricePR , "
				+ "	a.reason AS reason , "
				+ "	a.priceRemarks AS priceRemarks , "
				+ "	a.whetherFieldworkIsNeeded AS whetherFieldworkIsNeeded , "
				+ "	a.dataConversionRemarks AS dataConversionRemarks , "
				+ "	a.lastQuotationAveragePrice AS lastQuotationAveragePrice , "
				+ "	a.imputedLastQuotationAveragePrice AS imputedLastQuotationAveragePrice , "
				+ "	a.currentQuotationAveragePrice AS currentQuotationAveragePrice , "
				+ "	a.currentVSLastQuotationAveragePricePR AS currentVSLastQuotationAveragePricePR , "
				+ "	a.currentVSImputedLastQuotationAveragePricePR AS currentVSImputedLastQuotationAveragePricePR , "
				+ "	a.remarkOfImputedLastQuotationAveragePrice AS remarkOfImputedLastQuotationAveragePrice , "
				+ "	a.lastVarietyAveragePrice AS lastVarietyAveragePrice , "
				+ "	a.imputedLastVarietyAveragePrice AS imputedLastVarietyAveragePrice , "
				+ "	a.currentVarietyAveragePrice AS currentVarietyAveragePrice , "
				+ "	a.currentVSLastVarietyAveragePricePR AS currentVSLastVarietyAveragePricePR , "
				+ "	a.currentVSImputedLastVarietyAveragePricePR AS currentVSImputedLastVarietyAveragePricePR , "
				+ "	a.remarkOfImputedLastVarietyAveragePrice AS remarkOfImputedLastVarietyAveragePrice , "
				+ "	a.compilationMethod	AS compilationMethod "
				+ " FROM ( "
				+ " SELECT iqr.IndoorQuotationRecordId AS indoorQuotationRecordId "
				+ "	,q.QuotationId AS quotationId "
				+ "	,qr.QuotationRecordId AS fieldQuotationRecordId "
				+ "	,format(iqr.ReferenceMonth, 'yyyyMM', 'en-US') AS referenceMonth "
				+ "	,iqr.ReferenceDate AS referenceDate "
				+ "	,pp.Code AS purpose "
				+ "	,u.CPIBasePeriod AS cpiBasePeriod "
				+ "	,u.Code AS varietyCode "
				+ "	,u.ChineseName AS varietyChineseName "
				+ "	,u.EnglishName AS varietyEnglishName "
				+ "	,q.STATUS AS quotationStatus "
				+ "	,iqr.STATUS AS dataConversionStatus "
//				+ "	,ROW_NUMBER() OVER ( "
				//+ "		PARTITION BY iqr.QuotationId ORDER BY qr.CollectionDate "
//				+ "		PARTITION BY iqr.QuotationId ORDER BY iqr.ReferenceDate "
//				+ "		) AS quotationRecordSequence "
				+ "	, case when qr.quotationRecordId is not null then o.firmCode else qo.firmCode end as outletCode "
				+ "	, case when qr.quotationRecordId is not null then o.name else qo.name end as outletName "
				+ "	,CONCAT(RIGHT(ot.Code, 3), '-', ot.EnglishName) AS outletType "
				+ "	, case when qr.quotationRecordId is not null then p.productId else qp.productId end as productId "
				+ "	, case when qr.quotationRecordId is not null then p.countryOfOrigin else qp.countryOfOrigin end as countryOfOrigin "
				+ " , case when qr.quotationRecordId is not null then pa1.specificationName else qpa1.specificationName end as pa1Name "
				+ " , case when qr.quotationRecordId is not null then pa2.specificationName else qpa2.specificationName end as pa2Name "
				+ " , case when qr.quotationRecordId is not null then pa3.specificationName else qpa3.specificationName end as pa3Name "
				+ " , case when qr.quotationRecordId is not null then pa4.specificationName else qpa4.specificationName end as pa4Name "
				+ " , case when qr.quotationRecordId is not null then pa5.specificationName else qpa5.specificationName end as pa5Name "
				+ " , case when qr.quotationRecordId is not null then ps1.value else qps1.value end as ps1Value "
				+ " , case when qr.quotationRecordId is not null then ps2.value else qps2.value end as ps2Value "
				+ " , case when qr.quotationRecordId is not null then ps3.value else qps3.value end as ps3Value "
				+ " , case when qr.quotationRecordId is not null then ps4.value else qps4.value end as ps4Value "
				+ " , case when qr.quotationRecordId is not null then ps5.value else qps5.value end as ps5Value "
				+ "	,CASE WHEN ps1.value IS NULL THEN (pa1.SpecificationName + ' : ') WHEN ps1.value IS NOT NULL THEN (pa1.SpecificationName + ' : ' + ps1.Value) END AS productAttributes1 "
				+ "	,CASE WHEN ps2.value IS NULL THEN (pa2.SpecificationName + ' : ') WHEN ps2.value IS NOT NULL THEN (pa2.SpecificationName + ' : ' + ps2.Value) END AS productAttributes2 "
				+ "	,CASE WHEN ps3.value IS NULL THEN (pa3.SpecificationName + ' : ') WHEN ps3.value IS NOT NULL THEN (pa3.SpecificationName + ' : ' + ps3.Value) END AS productAttributes3 "
				+ "	,CASE WHEN ps4.value IS NULL THEN (pa4.SpecificationName + ' : ') WHEN ps4.value IS NOT NULL THEN (pa4.SpecificationName + ' : ' + ps4.Value) END AS productAttributes4 "
				+ "	,CASE WHEN ps5.value IS NULL THEN (pa5.SpecificationName + ' : ') WHEN ps5.value IS NOT NULL THEN (pa5.SpecificationName + ' : ' + ps5.Value) END AS productAttributes5 "
				+ "	,qr.Availability AS availability "
				+ "	,qr.NPrice AS surveyNPrice "
				+ "	,qr.SPrice AS surveySPrice "
				+ "	,iqr.LastNPrice AS lastEditedNPrice "
				+ "	,iqr.LastSPrice AS lastEditedSPrice "
				+ "	,iqr.PreviousNPrice AS previousEditedNPrice "
				+ "	,iqr.PreviousSPrice AS previousEditedSPrice "
				+ "	,iqr.CurrentNPrice AS currentEditedNPrice "
				+ "	,iqr.CurrentSPrice AS currentEditedSPrice "
				+ "	,convert(DECIMAL(12, 3), iqr.CurrentNPrice / iqr.PreviousNPrice * 100) AS nPricePR "
				+ "	,convert(DECIMAL(12, 3), iqr.CurrentSPrice / iqr.PreviousSPrice * 100) AS sPricePR "
				+ "	,qr.Reason AS reason "
				+ "	,qr.Remark AS priceRemarks "
				+ "	,CASE "
				+ "		WHEN iqr.IsNoField = 1 "
				+ "			THEN 'No Field' "
				+ "		ELSE 'Field' "
				+ "		END AS whetherFieldworkIsNeeded "
				+ "	,iqr.Remark AS dataConversionRemarks "
				+ "	,qs.LastHasPriceAverageCurrentSPrice AS lastQuotationAveragePrice "
				+ "	,iq.Price AS imputedLastQuotationAveragePrice "
				+ "	,qs.AverageCurrentSPrice AS currentQuotationAveragePrice "
				+ "	,convert(DECIMAL(12, 3), qs.AverageCurrentSPrice / qs.LastHasPriceAverageCurrentSPrice * 100) AS currentVSLastQuotationAveragePricePR "
				+ "	,convert(DECIMAL(12, 3), qs.AverageCurrentSPrice / iq.Price * 100) AS currentVSImputedLastQuotationAveragePricePR "
				+ "	,iq.Remark AS remarkOfImputedLastQuotationAveragePrice "
				+ "	,us.LastHasPriceAverageCurrentSPrice AS lastVarietyAveragePrice "
				+ "	,iu.Price AS imputedLastVarietyAveragePrice "
				+ "	,us.AverageCurrentSPrice AS currentVarietyAveragePrice "
				+ "	,convert(DECIMAL(12, 3), us.AverageCurrentSPrice / us.LastHasPriceAverageCurrentSPrice * 100) AS currentVSLastVarietyAveragePricePR "
				+ "	,convert(DECIMAL(12, 3), us.AverageCurrentSPrice / iu.Price * 100) AS currentVSImputedLastVarietyAveragePricePR "
				+ "	,iu.Remark AS remarkOfImputedLastVarietyAveragePrice "
				+ "	,CASE "
				+ "		WHEN si.CompilationMethod = 1 "
				+ "			THEN 'A.M. (Supermarket, fresh)' "
				+ "		WHEN si.CompilationMethod = 2 "
				+ "			THEN 'A.M. (Supermarket, non-fresh)' "
				+ "		WHEN si.CompilationMethod = 3 "
				+ "			THEN 'A.M. (Market)' "
				+ "		WHEN si.CompilationMethod = 4 "
				+ "			THEN 'G.M. (Supermarket)' "
				+ "		WHEN si.CompilationMethod = 5 "
				+ "			THEN 'G.M. (Batch)' "
				+ "		ELSE 'A.M. (Batch)' "
				+ "		END AS compilationMethod "
				+ "FROM IndoorQuotationRecord AS iqr "
				+ "LEFT JOIN QuotationRecord AS qr "
				+ "	ON iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "LEFT JOIN Quotation AS q "
				+ "	ON q.QuotationId = qr.QuotationId "
				+ "LEFT JOIN QuotationStatistic AS qs "
				+ "	ON q.QuotationId = qs.QuotationId "
				+ "		AND qs.ReferenceMonth = iqr.ReferenceMonth "
				+ "LEFT JOIN ImputeQuotation AS iq "
				+ "	ON iq.QuotationId = iqr.QuotationId "
				+ "		AND iq.ReferenceMonth = qs.ReferenceMonth "
				+ "LEFT JOIN Unit AS u "
				+ "	ON u.UnitId = q.UnitId "
				+ "LEFT JOIN ImputeUnit AS iu "
				+ "	ON iu.UnitId = u.UnitId "
				//+ "		AND iu.ReferenceMonth = qs.ReferenceMonth "
				+ "		AND iu.ReferenceMonth = iqr.ReferenceMonth "
				+ "LEFT JOIN Purpose AS pp "
				+ "	ON pp.PurposeId = u.PurposeId "
				+ "LEFT JOIN SubItem AS si "
				+ "	ON u.SubItemId = si.SubItemId "
				+ "LEFT JOIN UnitStatistic AS us "
				+ "	ON us.UnitId = u.UnitId "
				+ "		AND us.ReferenceMonth = qs.ReferenceMonth "
				+ "LEFT JOIN Outlet AS o "
				+ "	ON o.OutletId = q.OutletId "
				+ "LEFT JOIN OutletType ot "
				+ "	ON ot.OutletTypeId = si.OutletTypeId "
				+ " LEFT JOIN ITEM AS i "
				+ " ON ot.ItemId = i.ItemId "
				+ "LEFT JOIN Product AS p "
				+ "	ON p.ProductId = q.ProductId "
				+ "LEFT JOIN ProductGroup AS pg "
				+ "	ON pg.ProductGroupId = p.ProductGroupId "
				+ "LEFT JOIN ProductAttribute AS pa1 "
				+ "	ON pg.ProductGroupId = pa1.ProductGroupId "
				+ "		AND pa1.Sequence = 1 "
				+ "LEFT JOIN ProductAttribute AS pa2 "
				+ "	ON pg.ProductGroupId = pa2.ProductGroupId "
				+ "		AND pa2.Sequence = 2 "
				+ "LEFT JOIN ProductAttribute AS pa3 "
				+ "	ON pg.ProductGroupId = pa3.ProductGroupId "
				+ "		AND pa3.Sequence = 3 "
				+ "LEFT JOIN ProductAttribute AS pa4 "
				+ "	ON pg.ProductGroupId = pa4.ProductGroupId "
				+ "		AND pa4.Sequence = 4 "
				+ "LEFT JOIN ProductAttribute AS pa5 "
				+ "	ON pg.ProductGroupId = pa5.ProductGroupId "
				+ "		AND pa5.Sequence = 5 "
				+ "LEFT JOIN ProductSpecification AS ps1 "
				+ "	ON p.ProductId = ps1.ProductId "
				+ "		AND pa1.ProductAttributeId = ps1.ProductAttributeId "
				+ "LEFT JOIN ProductSpecification AS ps2 "
				+ "	ON p.ProductId = ps2.ProductId "
				+ "		AND pa2.ProductAttributeId = ps2.ProductAttributeId "
				+ "LEFT JOIN ProductSpecification AS ps3 "
				+ "	ON p.ProductId = ps3.ProductId "
				+ "		AND pa3.ProductAttributeId = ps3.ProductAttributeId "
				+ "LEFT JOIN ProductSpecification AS ps4 "
				+ "	ON p.ProductId = ps4.ProductId "
				+ "		AND pa4.ProductAttributeId = ps4.ProductAttributeId "
				+ "LEFT JOIN ProductSpecification AS ps5 "
				+ "	ON p.ProductId = ps5.ProductId "
				+ "		AND pa5.ProductAttributeId = ps5.ProductAttributeId "
				+" LEFT JOIN Product AS qp ON q.ProductId = qp.ProductId "
				+" LEFT JOIN Outlet AS qo ON q.OutletId = qo.OutletId "
				+" LEFT JOIN ProductGroup AS qpg ON qp.ProductGroupId = qpg.ProductGroupId "
				+" LEFT JOIN ProductAttribute AS qpa1 ON qpa1.ProductGroupId = qpg.ProductGroupId AND qpa1.Sequence = 1 "
				+" LEFT JOIN ProductAttribute AS qpa2 ON qpa2.ProductGroupId = qpg.ProductGroupId AND qpa2.Sequence = 2 "
				+" LEFT JOIN ProductAttribute as qpa3 on qpa3.ProductGroupId = qpg.ProductGroupId and qpa3.Sequence = 3 "
				+" LEFT JOIN ProductAttribute as qpa4 on qpa4.ProductGroupId = qpg.ProductGroupId and qpa4.Sequence = 4 "
				+" LEFT JOIN ProductAttribute as qpa5 on qpa5.ProductGroupId = qpg.ProductGroupId and qpa5.Sequence = 5 "
				+" LEFT JOIN ProductSpecification as qps1 on qps1.ProductId = qp.ProductId and qpa1.ProductAttributeId = qps1.ProductAttributeId "
				+" LEFT JOIN ProductSpecification as qps2 on qps2.ProductId = qp.ProductId and qpa2.ProductAttributeId = qps2.ProductAttributeId "
				+" LEFT JOIN ProductSpecification as qps3 on qps3.ProductId = qp.ProductId and qpa3.ProductAttributeId = qps3.ProductAttributeId "
				+" LEFT JOIN ProductSpecification as qps4 on qps4.ProductId = qp.ProductId and qpa4.ProductAttributeId = qps4.ProductAttributeId "
				+" LEFT JOIN ProductSpecification as qps5 on qps5.ProductId = qp.ProductId and qpa5.ProductAttributeId = qps5.ProductAttributeId "
				+ " WHERE (iq.Price IS NOT NULL OR iu.Price IS NOT NULL) AND iqr.QuotationRecordId IS NOT NULL ";
//				+ " WHERE qr.Availability is not null  AND (iq.Price IS NOT NULL OR iu.Price IS NOT NULL) ";

		
		if(startMonth != null && endMonth != null) {
			sql += "	AND iqr.ReferenceMonth BETWEEN :startMonth AND :endMonth ";
		}
		
		if (purpose != null && purpose.size() > 0) {
			sql += " and u.PurposeId in (:purpose) ";
		}
		
		if (quotationId != null && quotationId.size() > 0) {
			sql += " and q.QuotationId in (:quotationId) ";
		}

		if (unitId != null && unitId.size() > 0) {
			//sql += " and u.unitId in (:unitId) ";
			sql += " and i.ItemId in (:unitId) ";
		}
		
		sql += " UNION ALL "
				+ "SELECT iqr.IndoorQuotationRecordId AS indoorQuotationRecordId "
				+ "	,q.QuotationId AS quotationId "
				+ "	,qr.QuotationRecordId AS fieldQuotationRecordId "
				+ "	,format(iqr.ReferenceMonth, 'yyyyMM', 'en-US') AS referenceMonth "
				+ "	,iqr.ReferenceDate AS referenceDate "
				+ "	,pp.Code AS purpose "
				+ "	,u.CPIBasePeriod AS cpiBasePeriod "
				+ "	,u.Code AS varietyCode "
				+ "	,u.ChineseName AS varietyChineseName "
				+ "	,u.EnglishName AS varietyEnglishName "
				+ "	,q.STATUS AS quotationStatus "
				+ "	,iqr.STATUS AS dataConversionStatus "
//				+ "	,ROW_NUMBER() OVER ( "
				//+ "		PARTITION BY iqr.QuotationId ORDER BY qr.CollectionDate "
//				+ "		PARTITION BY iqr.QuotationId ORDER BY iqr.ReferenceDate"
//				+ "		) AS quotationRecordSequence "
				+ "	, case when qr.quotationRecordId is not null then o.firmCode else qo.firmCode end as outletCode "
				+ "	, case when qr.quotationRecordId is not null then o.name else qo.name end as outletName "
				+ "	,CONCAT(RIGHT(ot.Code, 3), '-', ot.EnglishName) AS outletType "
				+ "	, case when qr.quotationRecordId is not null then p.productId else qp.productId end as productId "
				+ "	, case when qr.quotationRecordId is not null then p.countryOfOrigin else qp.countryOfOrigin end as countryOfOrigin "
				+ " , case when qr.quotationRecordId is not null then pa1.specificationName else qpa1.specificationName end as pa1Name "
				+ " , case when qr.quotationRecordId is not null then pa2.specificationName else qpa2.specificationName end as pa2Name "
				+ " , case when qr.quotationRecordId is not null then pa3.specificationName else qpa3.specificationName end as pa3Name "
				+ " , case when qr.quotationRecordId is not null then pa4.specificationName else qpa4.specificationName end as pa4Name "
				+ " , case when qr.quotationRecordId is not null then pa5.specificationName else qpa5.specificationName end as pa5Name "
				+ " , case when qr.quotationRecordId is not null then ps1.value else qps1.value end as ps1Value "
				+ " , case when qr.quotationRecordId is not null then ps2.value else qps2.value end as ps2Value "
				+ " , case when qr.quotationRecordId is not null then ps3.value else qps3.value end as ps3Value "
				+ " , case when qr.quotationRecordId is not null then ps4.value else qps4.value end as ps4Value "
				+ " , case when qr.quotationRecordId is not null then ps5.value else qps5.value end as ps5Value "
				+ "	,CASE WHEN ps1.value IS NULL THEN (pa1.SpecificationName + ' : ') WHEN ps1.value IS NOT NULL THEN (pa1.SpecificationName + ' : ' + ps1.Value) END AS productAttributes1 "
				+ "	,CASE WHEN ps2.value IS NULL THEN (pa2.SpecificationName + ' : ') WHEN ps2.value IS NOT NULL THEN (pa2.SpecificationName + ' : ' + ps2.Value) END AS productAttributes2 "
				+ "	,CASE WHEN ps3.value IS NULL THEN (pa3.SpecificationName + ' : ') WHEN ps3.value IS NOT NULL THEN (pa3.SpecificationName + ' : ' + ps3.Value) END AS productAttributes3 "
				+ "	,CASE WHEN ps4.value IS NULL THEN (pa4.SpecificationName + ' : ') WHEN ps4.value IS NOT NULL THEN (pa4.SpecificationName + ' : ' + ps4.Value) END AS productAttributes4 "
				+ "	,CASE WHEN ps5.value IS NULL THEN (pa5.SpecificationName + ' : ') WHEN ps5.value IS NOT NULL THEN (pa5.SpecificationName + ' : ' + ps5.Value) END AS productAttributes5 "
				+ "	,qr.Availability AS availability "
				+ "	,qr.NPrice AS surveyNPrice "
				+ "	,qr.SPrice AS surveySPrice "
				+ "	,iqr.LastNPrice AS lastEditedNPrice "
				+ "	,iqr.LastSPrice AS lastEditedSPrice "
				+ "	,iqr.PreviousNPrice AS previousEditedNPrice "
				+ "	,iqr.PreviousSPrice AS previousEditedSPrice "
				+ "	,iqr.CurrentNPrice AS currentEditedNPrice "
				+ "	,iqr.CurrentSPrice AS currentEditedSPrice "
				+ "	,convert(DECIMAL(12, 3), iqr.CurrentNPrice / iqr.PreviousNPrice * 100) AS nPricePR "
				+ "	,convert(DECIMAL(12, 3), iqr.CurrentSPrice / iqr.PreviousSPrice * 100) AS sPricePR "
				+ "	,qr.Reason AS reason "
				+ "	,qr.Remark AS priceRemarks "
				+ "	,CASE "
				+ "		WHEN iqr.IsNoField = 1 "
				+ "			THEN 'No Field' "
				+ "		ELSE 'Field' "
				+ "		END AS whetherFieldworkIsNeeded "
				+ "	,iqr.Remark AS dataConversionRemarks "
				+ "	,qs.LastHasPriceAverageCurrentSPrice AS lastQuotationAveragePrice "
				+ "	,iq.Price AS imputedLastQuotationAveragePrice "
				+ "	,qs.AverageCurrentSPrice AS currentQuotationAveragePrice "
				+ "	,convert(DECIMAL(12, 3), qs.AverageCurrentSPrice / qs.LastHasPriceAverageCurrentSPrice * 100) AS currentVSLastQuotationAveragePricePR "
				+ "	,convert(DECIMAL(12, 3), qs.AverageCurrentSPrice / iq.Price * 100) AS currentVSImputedLastQuotationAveragePricePR "
				+ "	,iq.Remark AS remarkOfImputedLastQuotationAveragePrice "
				+ "	,us.LastHasPriceAverageCurrentSPrice AS lastVarietyAveragePrice "
				+ "	,iu.Price AS imputedLastVarietyAveragePrice "
				+ "	,us.AverageCurrentSPrice AS currentVarietyAveragePrice "
				+ "	,convert(DECIMAL(12, 3), us.AverageCurrentSPrice / us.LastHasPriceAverageCurrentSPrice * 100) AS currentVSLastVarietyAveragePricePR "
				+ "	,convert(DECIMAL(12, 3), us.AverageCurrentSPrice / iu.Price * 100) AS currentVSImputedLastVarietyAveragePricePR "
				+ "	,iu.Remark AS remarkOfImputedLastVarietyAveragePrice "
				+ "	,CASE "
				+ "		WHEN si.CompilationMethod = 1 "
				+ "			THEN 'A.M. (Supermarket, fresh)' "
				+ "		WHEN si.CompilationMethod = 2 "
				+ "			THEN 'A.M. (Supermarket, non-fresh)' "
				+ "		WHEN si.CompilationMethod = 3 "
				+ "			THEN 'A.M. (Market)' "
				+ "		WHEN si.CompilationMethod = 4 "
				+ "			THEN 'G.M. (Supermarket)' "
				+ "		WHEN si.CompilationMethod = 5 "
				+ "			THEN 'G.M. (Batch)' "
				+ "		ELSE 'A.M. (Batch)' "
				+ "		END AS compilationMethod "
				+ "FROM IndoorQuotationRecord AS iqr "
				+ "LEFT JOIN QuotationRecord AS qr "
				+ "	ON iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "LEFT JOIN Quotation AS q "
				+ "	ON q.QuotationId = iqr.QuotationId "
				+ "LEFT JOIN QuotationStatistic AS qs "
				+ "	ON q.QuotationId = qs.QuotationId "
				+ "		AND qs.ReferenceMonth = iqr.ReferenceMonth "
				+ "LEFT JOIN ImputeQuotation AS iq "
				+ "	ON iq.QuotationId = iqr.QuotationId "
				+ "		AND iq.ReferenceMonth = qs.ReferenceMonth "
				+ "LEFT JOIN Unit AS u "
				+ "	ON u.UnitId = q.UnitId "
				+ "LEFT JOIN ImputeUnit AS iu "
				+ "	ON iu.UnitId = u.UnitId "
				//+ "		AND iu.ReferenceMonth = qs.ReferenceMonth "
				+ "		AND iu.ReferenceMonth = iqr.ReferenceMonth "
				+ "LEFT JOIN Purpose AS pp "
				+ "	ON pp.PurposeId = u.PurposeId "
				+ "LEFT JOIN SubItem AS si "
				+ "	ON u.SubItemId = si.SubItemId "
				+ "LEFT JOIN UnitStatistic AS us "
				+ "	ON us.UnitId = u.UnitId "
				+ "		AND us.ReferenceMonth = qs.ReferenceMonth "
				+ "LEFT JOIN Outlet AS o "
				+ "	ON o.OutletId = q.OutletId "
				+ "LEFT JOIN OutletType ot "
				+ "	ON ot.OutletTypeId = si.OutletTypeId "
				+ " LEFT JOIN ITEM AS i "
				+ " ON ot.ItemId = i.ItemId "
				+ "LEFT JOIN Product AS p "
				+ "	ON p.ProductId = q.ProductId "
				+ "LEFT JOIN ProductGroup AS pg "
				+ "	ON pg.ProductGroupId = p.ProductGroupId "
				+ "LEFT JOIN ProductAttribute AS pa1 "
				+ "	ON pg.ProductGroupId = pa1.ProductGroupId "
				+ "		AND pa1.Sequence = 1 "
				+ "LEFT JOIN ProductAttribute AS pa2 "
				+ "	ON pg.ProductGroupId = pa2.ProductGroupId "
				+ "		AND pa2.Sequence = 2 "
				+ "LEFT JOIN ProductAttribute AS pa3 "
				+ "	ON pg.ProductGroupId = pa3.ProductGroupId "
				+ "		AND pa3.Sequence = 3 "
				+ "LEFT JOIN ProductAttribute AS pa4 "
				+ "	ON pg.ProductGroupId = pa4.ProductGroupId "
				+ "		AND pa4.Sequence = 4 "
				+ "LEFT JOIN ProductAttribute AS pa5 "
				+ "	ON pg.ProductGroupId = pa5.ProductGroupId "
				+ "		AND pa5.Sequence = 5 "
				+ "LEFT JOIN ProductSpecification AS ps1 "
				+ "	ON p.ProductId = ps1.ProductId "
				+ "		AND pa1.ProductAttributeId = ps1.ProductAttributeId "
				+ "LEFT JOIN ProductSpecification AS ps2 "
				+ "	ON p.ProductId = ps2.ProductId "
				+ "		AND pa2.ProductAttributeId = ps2.ProductAttributeId "
				+ "LEFT JOIN ProductSpecification AS ps3 "
				+ "	ON p.ProductId = ps3.ProductId "
				+ "		AND pa3.ProductAttributeId = ps3.ProductAttributeId "
				+ "LEFT JOIN ProductSpecification AS ps4 "
				+ "	ON p.ProductId = ps4.ProductId "
				+ "		AND pa4.ProductAttributeId = ps4.ProductAttributeId "
				+ "LEFT JOIN ProductSpecification AS ps5 "
				+ "	ON p.ProductId = ps5.ProductId "
				+ "		AND pa5.ProductAttributeId = ps5.ProductAttributeId "
				+" LEFT JOIN Product AS qp ON q.ProductId = qp.ProductId "
				+" LEFT JOIN Outlet AS qo ON q.OutletId = qo.OutletId "
				+" LEFT JOIN ProductGroup AS qpg ON qp.ProductGroupId = qpg.ProductGroupId "
				+" LEFT JOIN ProductAttribute AS qpa1 ON qpa1.ProductGroupId = qpg.ProductGroupId AND qpa1.Sequence = 1 "
				+" LEFT JOIN ProductAttribute AS qpa2 ON qpa2.ProductGroupId = qpg.ProductGroupId AND qpa2.Sequence = 2 "
				+" LEFT JOIN ProductAttribute as qpa3 on qpa3.ProductGroupId = qpg.ProductGroupId and qpa3.Sequence = 3 "
				+" LEFT JOIN ProductAttribute as qpa4 on qpa4.ProductGroupId = qpg.ProductGroupId and qpa4.Sequence = 4 "
				+" LEFT JOIN ProductAttribute as qpa5 on qpa5.ProductGroupId = qpg.ProductGroupId and qpa5.Sequence = 5 "
				+" LEFT JOIN ProductSpecification as qps1 on qps1.ProductId = qp.ProductId and qpa1.ProductAttributeId = qps1.ProductAttributeId "
				+" LEFT JOIN ProductSpecification as qps2 on qps2.ProductId = qp.ProductId and qpa2.ProductAttributeId = qps2.ProductAttributeId "
				+" LEFT JOIN ProductSpecification as qps3 on qps3.ProductId = qp.ProductId and qpa3.ProductAttributeId = qps3.ProductAttributeId "
				+" LEFT JOIN ProductSpecification as qps4 on qps4.ProductId = qp.ProductId and qpa4.ProductAttributeId = qps4.ProductAttributeId "
				+" LEFT JOIN ProductSpecification as qps5 on qps5.ProductId = qp.ProductId and qpa5.ProductAttributeId = qps5.ProductAttributeId "
				+ " WHERE (iq.Price IS NOT NULL OR iu.Price IS NOT NULL) AND iqr.QuotationRecordId IS NULL ";
		
		if(startMonth != null && endMonth != null) {
			sql += "	AND iqr.ReferenceMonth BETWEEN :startMonth AND :endMonth ";
		}
		
		if (purpose != null && purpose.size() > 0) {
			sql += " and u.PurposeId in (:purpose) ";
		}
		
		if (quotationId != null && quotationId.size() > 0) {
			sql += " and q.QuotationId in (:quotationId) ";
		}

		if (unitId != null && unitId.size() > 0) {
			//sql += " and u.unitId in (:unitId) ";
			sql += " and i.ItemId in (:unitId) ";
		}
		
		sql += ""
				+ " ) AS a "
//				+ " ORDER BY ReferenceMonth "
//				+ "	,Purpose "
//				+ "	,cpiBasePeriod "
//				+ "	,varietyCode "
//				+ "	,QuotationId "
//				+ "	,fieldQuotationRecordId";
				+ "	 ORDER BY a.ReferenceMonth , "
				+ "	 a.Purpose , "
				+ "	 a.cpiBasePeriod , "
				+ "	 a.varietyCode , "
				+ "	 a.QuotationId , "
				+ "	 IsNull(a.fieldQuotationRecordId, 2147483647) ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		if(startMonth != null && endMonth != null) {
			query.setParameter("startMonth", startMonth);
			query.setParameter("endMonth", endMonth);
		}
		
		if (purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		}

		if (quotationId != null && quotationId.size() > 0) {
			query.setParameterList("quotationId", quotationId);
		}

		if (unitId != null && unitId.size() > 0) {
			query.setParameterList("unitId", unitId);
		}
		
		query.addScalar("indoorQuotationRecordId", StandardBasicTypes.STRING);
		query.addScalar("quotationId", StandardBasicTypes.STRING);
		query.addScalar("fieldQuotationRecordId", StandardBasicTypes.STRING);
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("referenceDate", StandardBasicTypes.STRING);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("varietyCode", StandardBasicTypes.STRING);
		query.addScalar("varietyChineseName", StandardBasicTypes.STRING);
		query.addScalar("varietyEnglishName", StandardBasicTypes.STRING);
		query.addScalar("quotationStatus", StandardBasicTypes.STRING);
		query.addScalar("dataConversionStatus", StandardBasicTypes.STRING);
		query.addScalar("quotationRecordSequence", StandardBasicTypes.STRING);
		query.addScalar("outletCode", StandardBasicTypes.STRING);
		query.addScalar("outletName", StandardBasicTypes.STRING);
		query.addScalar("outletType", StandardBasicTypes.STRING);
		query.addScalar("productId", StandardBasicTypes.STRING);
		query.addScalar("countryOfOrigin", StandardBasicTypes.STRING);
		query.addScalar("productAttributes1", StandardBasicTypes.STRING);
		query.addScalar("productAttributes2", StandardBasicTypes.STRING);
		query.addScalar("productAttributes3", StandardBasicTypes.STRING);
		query.addScalar("productAttributes4", StandardBasicTypes.STRING);
		query.addScalar("productAttributes5", StandardBasicTypes.STRING);
		query.addScalar("availability", StandardBasicTypes.INTEGER);
		query.addScalar("surveyNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("surveySPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("lastEditedNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("lastEditedSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousEditedNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("previousEditedSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentEditedNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentEditedSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("nPricePR", StandardBasicTypes.DOUBLE);
		query.addScalar("sPricePR", StandardBasicTypes.DOUBLE);
		query.addScalar("reason", StandardBasicTypes.STRING);
		query.addScalar("priceRemarks", StandardBasicTypes.STRING);
		query.addScalar("whetherFieldworkIsNeeded", StandardBasicTypes.STRING);
		query.addScalar("dataConversionRemarks", StandardBasicTypes.STRING);
		query.addScalar("lastQuotationAveragePrice", StandardBasicTypes.DOUBLE);
		query.addScalar("imputedLastQuotationAveragePrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentQuotationAveragePrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentVSLastQuotationAveragePricePR", StandardBasicTypes.DOUBLE);
		query.addScalar("currentVSImputedLastQuotationAveragePricePR", StandardBasicTypes.DOUBLE);
		query.addScalar("remarkOfImputedLastQuotationAveragePrice", StandardBasicTypes.STRING);
		query.addScalar("lastVarietyAveragePrice", StandardBasicTypes.DOUBLE);
		query.addScalar("imputedLastVarietyAveragePrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentVarietyAveragePrice", StandardBasicTypes.DOUBLE);
		query.addScalar("currentVSLastVarietyAveragePricePR", StandardBasicTypes.DOUBLE);
		query.addScalar("currentVSImputedLastVarietyAveragePricePR", StandardBasicTypes.DOUBLE);
		query.addScalar("remarkOfImputedLastVarietyAveragePrice", StandardBasicTypes.STRING);
		query.addScalar("compilationMethod", StandardBasicTypes.STRING);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationRecordImputationReport.class));
		
		System.out.println("9048:: " + sql);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AirportTicketPRListByCountryWithCPISeries> getAirportTicketPRListByCountryWithCPISeries(Date startMonth, Date endMonth,
			//2018-01-04 cheung_cheng [MB9010]  The lookup table should show up to item level only  -
			ArrayList<Integer> purpose, ArrayList<Integer> cpiSurveyForm, ArrayList<Integer> itemId,
			String dataCollection,
			ArrayList<String> quotationFormType){
	
		String KEEP_NUMBER_DATE_FORMAT = "MMyy";
		
		String referenceMonth = String.format("format(iqr.ReferenceMonth, '%s', 'en-us')", SystemConstant.REPORT_SHORT_MONTH_FORMAT);
		String referenceDate = String.format("format(iqr.ReferenceDate, '%s', 'en-us')", SystemConstant.REPORT_REFERENCE_DATE_FORMAT);
		String keepNumberMonth = String.format("format(iqr.ProductNotAvailableFrom, '%s', 'en-us')", KEEP_NUMBER_DATE_FORMAT);
	
		
		String sqlSelect = "select "
				+ "iqr.IndoorQuotationRecordId as indoorQuotationRecordId, "
				+ "iqr.QuotationRecordId as fieldQuotationRecordId, "
				+ "iqr.QuotationId as quotationId, "
				+ "case when iqr.ReferenceMonth is null then '' else " + referenceMonth + " end as referenceMonth, "
				+ "case when iqr.ReferenceDate is null then '' else " + referenceDate + " end as referenceDate, "
				+ "p.Code as purpose, "
				+ "u.CPIBasePeriod as cpiBasePeriod, "
				+ "q.CPICompilationSeries as cpiSeries, "
				+ "g.Code as groupCode, "
				+ "g.EnglishName as groupEnglishName, "
				+ "item.Code as itemCode, "
				+ "item.EnglishName as itemEnglishName, "
				+ "u.Code as varietyCode, "
				+ "u.EnglishName as varietyEnglishName, "
				+ "q.Status as quotationStatus, "
				+ "iqr.Status as dataConversionStatus, "
				+ "o.FirmCode as outletCode, "
				+ "o.Name as outletName, "
				+ "ot.Code as outletTypeCode, "
				+ "ot.EnglishName as outletTypeEnglishName, "
				+ "prod.ProductId as productId, "
				+ "prod.CountryOfOrigin as countryOfOrigin, "
				+ "case when (prodAttr1.SpecificationName is null and prodSpec1.Value is null) then '' else concat(prodAttr1.SpecificationName,':',prodSpec1.Value) end as productAttributes1, "
				+ "case when (prodAttr2.SpecificationName is null and prodSpec2.Value is null) then '' else concat(prodAttr2.SpecificationName,':',prodSpec2.Value) end as productAttributes2, "
				+ "case when (prodAttr3.SpecificationName is null and prodSpec3.Value is null) then '' else concat(prodAttr3.SpecificationName,':',prodSpec3.Value) end as productAttributes3, "
				+ "case when (prodAttr4.SpecificationName is null and prodSpec4.Value is null) then '' else concat(prodAttr4.SpecificationName,':',prodSpec4.Value) end as productAttributes4, "
				+ "case when (prodAttr5.SpecificationName is null and prodSpec5.Value is null) then '' else concat(prodAttr5.SpecificationName,':',prodSpec5.Value) end as productAttributes5, "
				//2018-01-04 cheung_cheung [MB9010] "Product Attributes 6"show the wrong result 
				+ "case when (prodAttr6.SpecificationName is null and prodSpec6.Value is null) then '' else concat(prodAttr6.SpecificationName,':',prodSpec6.Value) end as productAttributes6, "
				+ "qr.Availability as availability, "
				+ "qr.NPrice as surveyNPrice, "
				+ "qr.SPrice as surveySPrice, "
				+ "iqr.LastNPrice as lastEditedNPrice, "
				+ "iqr.LastSPrice as lastEditedSPrice, "
				+ "iqr.PreviousNPrice as previousEditedNPrice, "
				+ "iqr.PreviousSPrice as previousEditedSPrice, "
				+ "iqr.CurrentNPrice as currentEditedNPrice, "
				+ "iqr.CurrentSPrice as currentEditedSPrice, "
				+ "case when iqr.previousNPrice > 0 then (iqr.currentNPrice / iqr.previousNPrice * 100) else null end as nPricePr, "
				+ "case when iqr.previousSPrice > 0 then (iqr.currentSPrice / iqr.previousSPrice * 100) else null end as sPricePr, "
				//+ "EXP(AVG(LOG(Case when (iqr.currentSPrice / iqr.previousSPrice * 100) <= 0 then null else ROUND((iqr.currentSPrice / iqr.previousSPrice * 100),3) end)) "
				//+ "OVER (PARTITION BY iqr.referenceMonth, p.Code, u.CPIBasePeriod ,q.CPICompilationSeries ,u.code)) as sPricePrAggregated, "
				+ "qr.Reason as priceReason, "
				+ "case when iqr.IsCurrentPriceKeepNo = 1 then "
					+ "case when iqr.ProductNotAvailableFrom is null then '' else " + keepNumberMonth + " end "
				+ "else '' end as keepNumber, "
				//2018-01-05 cheung_cheng [MB9010] Whether field work is needed" show the wrong opposite result
//				+ "case when iqr.IsNoField = 1 then 'Field' else 'No Field' end as whetherFieldworkIsNeeded, "
				+ "case when iqr.IsNoField = 1 then 'No Field' else 'Field' end as whetherFieldworkIsNeeded, "
				+ "iqr.Remark as dataConversionRemarks ";
				//+ "isNull(Count(iqr.IndoorQuotationRecordId) OVER (PARTITION BY iqr.referenceMonth ,p.Code ,u.CPIBasePeriod ,q.CPICompilationSeries ,u.code) ,0) as noOfRecordByVariety, "
				//+ "isNull(Count(iqr.IndoorQuotationRecordId) OVER (PARTITION BY iqr.referenceMonth ,p.Code ,u.CPIBasePeriod ,q.CPICompilationSeries) ,0) as totalNoOfRecordByCpiSeries ";
		String sqlTable = " IndoorQuotationRecord as iqr "
				//2018-01-04 cheung_cheng [MB9010] Should export all indoor quotation records for the selected period (also include indoor quotation records without quotation record id)
				+ "left join QuotationRecord as qr on iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "left join Quotation as q on q.QuotationId = iqr.QuotationId "
				+ "left join Outlet as o on o.OutletId = qr.OutletId "
				+ "left join Unit as u on u.UnitId = q.UnitId "
				+ "left join Purpose as p on p.PurposeId = u.PurposeId "
				+ "left join SubItem as si on si.SubItemId = u.SubItemId "
				+ "left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ "left join Item as item on item.ItemId = ot.ItemId "
				+ "left join SubGroup as subGp on subGp.SubGroupId = item.SubGroupId "
				+ "left join [Group] as g on g.GroupId = subGp.GroupId "
				+ "left join Product as prod on prod.ProductId = qr.ProductId "
				+ "left join ProductGroup as prodGp on prodGp.ProductGroupId = prod.ProductGroupId "
				+ "left join ProductAttribute as prodAttr1 on prodAttr1.ProductGroupId = prodGp.ProductGroupId and prodAttr1.Sequence = 1 "
				+ "left join ProductSpecification as prodSpec1 on prodSpec1.ProductAttributeId = prodAttr1.ProductAttributeId and prodSpec1.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr2 on prodAttr2.ProductGroupId = prodGp.ProductGroupId and prodAttr2.Sequence = 2 "
				+ "left join ProductSpecification as prodSpec2 on prodSpec2.ProductAttributeId = prodAttr2.ProductAttributeId and prodSpec2.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr3 on prodAttr3.ProductGroupId = prodGp.ProductGroupId and prodAttr3.Sequence = 3 "
				+ "left join ProductSpecification as prodSpec3 on prodSpec3.ProductAttributeId = prodAttr3.ProductAttributeId and prodSpec3.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr4 on prodAttr4.ProductGroupId = prodGp.ProductGroupId and prodAttr4.Sequence = 4 "
				+ "left join ProductSpecification as prodSpec4 on prodSpec4.ProductAttributeId = prodAttr4.ProductAttributeId and prodSpec4.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr5 on prodAttr5.ProductGroupId = prodGp.ProductGroupId and prodAttr5.Sequence = 5 "
				+ "left join ProductSpecification as prodSpec5 on prodSpec5.ProductAttributeId = prodAttr5.ProductAttributeId and prodSpec5.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr6 on prodAttr6.ProductGroupId = prodGp.ProductGroupId and prodAttr6.Sequence = 6 "
				+ "left join ProductSpecification as prodSpec6 on prodSpec6.ProductAttributeId = prodAttr6.ProductAttributeId and prodSpec6.ProductId = prod.ProductId ";
		
		String sqlTable2 = " IndoorQuotationRecord as iqr "
				//2018-01-04 cheung_cheng [MB9010] Should export all indoor quotation records for the selected period (also include indoor quotation records without quotation record id)
				+ "left join QuotationRecord as qr on iqr.QuotationRecordId = qr.QuotationRecordId "
				+ "left join Quotation as q on q.QuotationId = iqr.QuotationId "
				+ "left join Outlet as o on o.OutletId = q.OutletId "
				+ "left join Unit as u on u.UnitId = q.UnitId "
				+ "left join Purpose as p on p.PurposeId = u.PurposeId "
				+ "left join SubItem as si on si.SubItemId = u.SubItemId "
				+ "left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId "
				+ "left join Item as item on item.ItemId = ot.ItemId "
				+ "left join SubGroup as subGp on subGp.SubGroupId = item.SubGroupId "
				+ "left join [Group] as g on g.GroupId = subGp.GroupId "
				+ "left join Product as prod on prod.ProductId = q.ProductId "
				+ "left join ProductGroup as prodGp on prodGp.ProductGroupId = prod.ProductGroupId "
				+ "left join ProductAttribute as prodAttr1 on prodAttr1.ProductGroupId = prodGp.ProductGroupId and prodAttr1.Sequence = 1 "
				+ "left join ProductSpecification as prodSpec1 on prodSpec1.ProductAttributeId = prodAttr1.ProductAttributeId and prodSpec1.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr2 on prodAttr2.ProductGroupId = prodGp.ProductGroupId and prodAttr2.Sequence = 2 "
				+ "left join ProductSpecification as prodSpec2 on prodSpec2.ProductAttributeId = prodAttr2.ProductAttributeId and prodSpec2.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr3 on prodAttr3.ProductGroupId = prodGp.ProductGroupId and prodAttr3.Sequence = 3 "
				+ "left join ProductSpecification as prodSpec3 on prodSpec3.ProductAttributeId = prodAttr3.ProductAttributeId and prodSpec3.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr4 on prodAttr4.ProductGroupId = prodGp.ProductGroupId and prodAttr4.Sequence = 4 "
				+ "left join ProductSpecification as prodSpec4 on prodSpec4.ProductAttributeId = prodAttr4.ProductAttributeId and prodSpec4.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr5 on prodAttr5.ProductGroupId = prodGp.ProductGroupId and prodAttr5.Sequence = 5 "
				+ "left join ProductSpecification as prodSpec5 on prodSpec5.ProductAttributeId = prodAttr5.ProductAttributeId and prodSpec5.ProductId = prod.ProductId "
				+ "left join ProductAttribute as prodAttr6 on prodAttr6.ProductGroupId = prodGp.ProductGroupId and prodAttr6.Sequence = 6 "
				+ "left join ProductSpecification as prodSpec6 on prodSpec6.ProductAttributeId = prodAttr6.ProductAttributeId and prodSpec6.ProductId = prod.ProductId ";
		String sqlWhere = " WHERE 1=1";
		sqlWhere += " and iqr.referenceMonth >= :startMonth and iqr.referenceMonth <= :endMonth ";

		if (purpose != null && purpose.size() > 0){
			sqlWhere += " and p.purposeId in (:purpose) ";
		}
		
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sqlWhere += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		//2018-01-04 cheung_cheng [MB9010]  The lookup table should show up to item level only  -
		if (itemId != null && itemId.size() > 0){
			sqlWhere += " and item.itemId in (:itemId) ";
		}
		
		if (dataCollection != null && dataCollection.length() > 0){
			if("Y".equals(dataCollection)) sqlWhere += " and iqr.isNoField = 0 ";
			else if("N".equals(dataCollection)) sqlWhere += " and iqr.isNoField = 1 ";
		}
		
		Set<String> cpiCompilationSeriesMerged = new HashSet<String>();
		
		if (quotationFormType != null && quotationFormType.size() > 0) {
			cpiCompilationSeriesMerged.addAll(quotationFormType);
		}
		
		if (cpiCompilationSeriesMerged.size() > 0) {
			sqlWhere += " and q.cpiCompilationSeries in :cpiCompilationSeriesMerged ";
		}
		
		StringBuilder sqlbd = new StringBuilder(); 
		sqlbd.append(" select ROW_NUMBER() OVER( ORDER BY table1.ReferenceMonth asc, table1.purpose asc, table1.CPIBasePeriod asc, ") 
		.append("table1.cpiSeries asc, table1.varietyCode asc, table1.IndoorQuotationRecordId asc) as no,")
		.append("table1.indoorQuotationRecordId, table1.fieldQuotationRecordId, table1.quotationId, table1.referenceMonth, table1.referenceDate,")
		.append("table1.purpose, table1.cpiBasePeriod, table1.cpiSeries, table1.groupCode, table1.groupEnglishName,")
		.append("table1.itemCode,table1.itemEnglishName, table1.varietyCode, table1.varietyEnglishName, table1.quotationStatus, table1.dataConversionStatus,")
		.append("table1.outletCode, table1.outletName, table1.outletTypeCode,  table1.outletTypeEnglishName, table1.productId, ")
		.append("table1.countryOfOrigin, table1.productAttributes1, table1.productAttributes2, table1.productAttributes3,table1.productAttributes4,")
		.append("table1.productAttributes5, table1.productAttributes6, table1.availability, table1.surveyNPrice, table1.surveySPrice, ")
		.append(" table1.lastEditedNPrice, table1.lastEditedSPrice, table1.previousEditedNPrice, table1.previousEditedSPrice, table1.currentEditedNPrice, ")
		.append(" table1.currentEditedSPrice, table1.nPricePr, table1.sPricePr, ")
		//"table1.sPricePrAggregated, "
		.append(" EXP(AVG(LOG(CASE WHEN table1.previousEditedSPrice=0 then null when (table1.currentEditedSPrice / table1.previousEditedSPrice * 100) <= 0 THEN NULL ")
		.append(" ELSE ROUND((table1.currentEditedSPrice / table1.previousEditedSPrice * 100),3) END)) OVER (PARTITION BY table1.referenceMonth , table1.purpose, table1.cpiBasePeriod ,table1.cpiSeries ,table1.varietyCode)) AS sPricePrAggregated, ")
		.append(" table1.priceReason, table1.keepNumber, ")
		.append(" table1.whetherFieldworkIsNeeded, table1.dataConversionRemarks, ")
		// "table1.noOfRecordByVariety,  table1.totalNoOfRecordByCpiSeries "
		.append(" isNull(Count(table1.indoorQuotationRecordId) OVER (PARTITION BY table1.referenceMonth ,table1.purpose ,table1.cpiBasePeriod ,table1.cpiSeries,table1.varietyCode) ,0) AS noOfRecordByVariety, ")
		.append(" isNull(Count(table1.indoorQuotationRecordId) OVER (PARTITION BY table1.referenceMonth ,table1.purpose ,table1.cpiBasePeriod ,table1.cpiSeries) ,0) AS totalNoOfRecordByCpiSeries ");
		sqlbd.append("FROM (").append(sqlSelect).append(" FROM ").append(sqlTable).append(sqlWhere).append(" AND iqr.QuotationRecordId IS NOT NULL ") 
			 .append("UNION ").append(sqlSelect).append(" FROM ").append(sqlTable2).append(sqlWhere).append(" AND iqr.QuotationRecordId IS NULL ")
		.append(")table1");
		
		SQLQuery query = this.getSession().createSQLQuery(sqlbd.toString());
		
		System.out.println("SQL of 9010 : "+sqlbd.toString());
		
		query.setParameter("startMonth", startMonth);
		query.setParameter("endMonth", endMonth);
		
		if (purpose != null && purpose.size() > 0) {
			query.setParameterList("purpose", purpose);
		}

		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0) {
			query.setParameterList("cpiSurveyForm", cpiSurveyForm);
		}

		//2018-01-04 cheung_cheng [MB9010]  The lookup table should show up to item level only  -
		if (itemId != null && itemId.size() > 0) {
			query.setParameterList("itemId", itemId);
		}
		
//		if (quotationFormType != null && quotationFormType.size() > 0) {
//			query.setParameterList("cpiCompilationSeries", quotationFormType);
//		}

		if (cpiCompilationSeriesMerged.size() > 0) {
			query.setParameterList("cpiCompilationSeriesMerged", cpiCompilationSeriesMerged);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(AirportTicketPRListByCountryWithCPISeries.class));
		
		query.addScalar("no",StandardBasicTypes.INTEGER);
		query.addScalar("indoorQuotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("fieldQuotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("quotationId",StandardBasicTypes.INTEGER);
		query.addScalar("referenceMonth",StandardBasicTypes.STRING);
		query.addScalar("referenceDate",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("cpiSeries",StandardBasicTypes.STRING);
		query.addScalar("groupCode",StandardBasicTypes.STRING);
		query.addScalar("groupEnglishName",StandardBasicTypes.STRING);
		query.addScalar("itemCode",StandardBasicTypes.STRING);
		query.addScalar("itemEnglishName",StandardBasicTypes.STRING);
		query.addScalar("varietyCode",StandardBasicTypes.STRING);
		query.addScalar("varietyEnglishName",StandardBasicTypes.STRING);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("dataConversionStatus",StandardBasicTypes.STRING);
		query.addScalar("outletCode",StandardBasicTypes.STRING);
		query.addScalar("outletName",StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode",StandardBasicTypes.STRING);
		query.addScalar("outletTypeEnglishName",StandardBasicTypes.STRING);
		query.addScalar("productId",StandardBasicTypes.INTEGER);
		query.addScalar("countryOfOrigin",StandardBasicTypes.STRING);
		query.addScalar("productAttributes1",StandardBasicTypes.STRING);
		query.addScalar("productAttributes2",StandardBasicTypes.STRING);
		query.addScalar("productAttributes3",StandardBasicTypes.STRING);
		query.addScalar("productAttributes4",StandardBasicTypes.STRING);
		query.addScalar("productAttributes5",StandardBasicTypes.STRING);
		query.addScalar("productAttributes6",StandardBasicTypes.STRING);
		query.addScalar("availability",StandardBasicTypes.INTEGER);
		query.addScalar("surveyNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("surveySPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastEditedNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastEditedSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousEditedNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousEditedSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentEditedNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentEditedSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("nPricePr",StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("sPricePr",StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("priceReason",StandardBasicTypes.STRING);
		query.addScalar("sPricePrAggregated",StandardBasicTypes.BIG_DECIMAL);
		query.addScalar("keepNumber",StandardBasicTypes.STRING);
		query.addScalar("whetherFieldworkIsNeeded",StandardBasicTypes.STRING);
		query.addScalar("dataConversionRemarks",StandardBasicTypes.STRING);
		query.addScalar("noOfRecordByVariety",StandardBasicTypes.INTEGER);
		query.addScalar("totalNoOfRecordByCpiSeries",StandardBasicTypes.INTEGER);

		
		return query.list();
	}
	
	public Long countAllocateQuotationRecordConversion(Date referenceMonth, Integer purposeId, String[] status, String notEqStatus, List<Integer> batchId){
		if (batchId != null && batchId.size() > 0){
		String hql = "select count(distinct iqr.indoorQuotationRecordId) "
				+ " from IndoorQuotationRecord iqr "
				+ "	left join iqr.quotation q "
				+ " left join q.unit u "
				+ " left join u.purpose as p "
				+ " left join q.batch as b"
				+ " where iqr.referenceMonth = :referenceMonth "
				+ " and p.purposeId = :purposeId "
				+ " and b.batchId in (:batchId)";
				
		if(status != null && status.length > 0){
			hql = hql + " and iqr.status in (:status)";
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			hql = hql + " and iqr.status != :notEqStatus";
		}
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("purposeId", purposeId);
		
		if(batchId != null && batchId.size() > 0)	{
			query.setParameterList("batchId", batchId);
		}	
		if(status != null&& status.length > 0){
			query.setParameterList("status", status);
		}
		if(!StringUtils.isEmpty(notEqStatus)){
			query.setParameter("notEqStatus", notEqStatus);
		}
		
		return query.uniqueResult() == null ? 0 : (Long) query.uniqueResult();
		}
		else{
			return (long) 0;
		}
	}
	
	public List<Integer> getExistingIdsByReferenceMonth(Date refMonth){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("referenceMonth", refMonth));
		criteria.setProjection(Projections.property("id"));
		
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<IndoorQuotationRecord> getIndoorQuotationRecordsByQuotationRecordIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.createAlias("quotationRecord", "qr");
		criteria.add(Restrictions.in("qr.id", ids));
		return criteria.list();
	}
	
	public IndoorQuotationRecord getFirstHistoryRecordByQuotationId(Integer quotationId, Date referenceDate){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("quotation.quotationId", quotationId));
		criteria.add(Restrictions.lt("referenceDate", referenceDate));
		criteria.add(Restrictions.or(Restrictions.isNotNull("currentNPrice"), Restrictions.isNotNull("currentSPrice")));
		criteria.addOrder(Order.desc("referenceDate"));
		criteria.setMaxResults(1);
		return (IndoorQuotationRecord)criteria.uniqueResult();
	}
	
	public List<ExportEditPriceStatisticList> exportEditPriceStatistic(Date refMonth, Integer purposeId){
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[ExportEditPriceStatistic] :refMonth, :purposeId");
		query.setParameter("refMonth", refMonth);
		query.setParameter("purposeId", purposeId);
		
		query.addScalar("indoorQuotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("quotationRecordId",StandardBasicTypes.INTEGER);
		query.addScalar("referenceDate",StandardBasicTypes.DATE);
		query.addScalar("isNoField",StandardBasicTypes.BOOLEAN);
		query.addScalar("isProductChange",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewProduct",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewRecruitment",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNewOutlet",StandardBasicTypes.BOOLEAN);
		query.addScalar("indoorStatus",StandardBasicTypes.STRING);
		query.addScalar("quotationId",StandardBasicTypes.INTEGER);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("batchCode",StandardBasicTypes.STRING);
		query.addScalar("formType",StandardBasicTypes.STRING);
		query.addScalar("unitCode",StandardBasicTypes.STRING);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName",StandardBasicTypes.STRING);
		query.addScalar("unitChineseName",StandardBasicTypes.STRING);
		query.addScalar("seasonality",StandardBasicTypes.INTEGER);
		query.addScalar("cpiCompilationSeries",StandardBasicTypes.STRING);
		query.addScalar("cpiQuotationType",StandardBasicTypes.INTEGER);
		query.addScalar("isICP",StandardBasicTypes.BOOLEAN);
		query.addScalar("icpProductCode",StandardBasicTypes.STRING);
		query.addScalar("icpProductName",StandardBasicTypes.STRING);
		query.addScalar("quotationIcpType",StandardBasicTypes.STRING);
		query.addScalar("unitIcpType",StandardBasicTypes.STRING);
		query.addScalar("outletCode",StandardBasicTypes.INTEGER);
		query.addScalar("outletName",StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode",StandardBasicTypes.STRING);
		query.addScalar("outletTypeEnglishName",StandardBasicTypes.STRING);
		query.addScalar("district",StandardBasicTypes.STRING);
		query.addScalar("tpu",StandardBasicTypes.STRING);
		query.addScalar("councilDistrict",StandardBasicTypes.STRING);
		query.addScalar("districtEnglishName",StandardBasicTypes.STRING);
		query.addScalar("detailAddress",StandardBasicTypes.STRING);
		query.addScalar("indoorMarketName",StandardBasicTypes.STRING);
		query.addScalar("productGroupId",StandardBasicTypes.INTEGER);
		query.addScalar("productGroupCode",StandardBasicTypes.STRING);
		query.addScalar("productGroupEnglishName",StandardBasicTypes.STRING);
		query.addScalar("productGroupChineseName",StandardBasicTypes.STRING);
		query.addScalar("productId",StandardBasicTypes.INTEGER);
		query.addScalar("countryOfOrigin",StandardBasicTypes.STRING);
		query.addScalar("pa1Name",StandardBasicTypes.STRING);
		query.addScalar("pa2Name",StandardBasicTypes.STRING);
		query.addScalar("pa3Name",StandardBasicTypes.STRING);
		query.addScalar("pa4Name",StandardBasicTypes.STRING);
		query.addScalar("pa5Name",StandardBasicTypes.STRING);
		query.addScalar("ps1Value",StandardBasicTypes.STRING);
		query.addScalar("ps2Value",StandardBasicTypes.STRING);
		query.addScalar("ps3Value",StandardBasicTypes.STRING);
		query.addScalar("ps4Value",StandardBasicTypes.STRING);
		query.addScalar("ps5Value",StandardBasicTypes.STRING);
		query.addScalar("lastProductChangeDate",StandardBasicTypes.DATE);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("originalNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("originalSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("nPriceAfterUOMConversion",StandardBasicTypes.DOUBLE);
		query.addScalar("sPriceAfterUOMConversion",StandardBasicTypes.DOUBLE);
		query.addScalar("computedNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("computedSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("isNullCurrentNPrice",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNullCurrentSPrice",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNullPreviousNPrice",StandardBasicTypes.BOOLEAN);
		query.addScalar("isNullPreviousSPrice",StandardBasicTypes.BOOLEAN);
		query.addScalar("backNoLastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("backNoLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastPriceDate",StandardBasicTypes.DATE);
		query.addScalar("copyPriceType",StandardBasicTypes.INTEGER);
		query.addScalar("copyLastPriceType",StandardBasicTypes.INTEGER);
		query.addScalar("remark",StandardBasicTypes.STRING);
		query.addScalar("isCurrentPriceKeepNo",StandardBasicTypes.BOOLEAN);
		query.addScalar("isRUA",StandardBasicTypes.BOOLEAN);
		query.addScalar("ruaDate",StandardBasicTypes.DATE);
		query.addScalar("isProductNotAvailable",StandardBasicTypes.BOOLEAN);
		query.addScalar("productNotAvailableFrom",StandardBasicTypes.DATE);
		query.addScalar("firmVerify",StandardBasicTypes.LONG);
		query.addScalar("categoryVerify",StandardBasicTypes.LONG);
		query.addScalar("quotationVerify",StandardBasicTypes.LONG);
		query.addScalar("rejectReason",StandardBasicTypes.STRING);
		query.addScalar("isSpicing",StandardBasicTypes.BOOLEAN);
		query.addScalar("isOutlier",StandardBasicTypes.BOOLEAN);
		query.addScalar("outlierRemark",StandardBasicTypes.STRING);
		query.addScalar("fr",StandardBasicTypes.DOUBLE);
		query.addScalar("isApplyFR",StandardBasicTypes.BOOLEAN);
		query.addScalar("isFRPercentage",StandardBasicTypes.BOOLEAN);
		query.addScalar("lastFRAppliedDate",StandardBasicTypes.DATE);
		query.addScalar("imputedQuotationPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("imputedQuotationRemark",StandardBasicTypes.STRING);
		query.addScalar("imputedUnitPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("imputedUnitRemark",StandardBasicTypes.STRING);
		query.addScalar("firmRemark",StandardBasicTypes.STRING);
		query.addScalar("categoryRemark",StandardBasicTypes.STRING);
		query.addScalar("quotationRemark",StandardBasicTypes.STRING);
		query.addScalar("quotationRecordSequence",StandardBasicTypes.INTEGER);
		query.addScalar("qsAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsAverageLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsLastHasPriceAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usLastHasPriceAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsStandardDeviationSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsMaxSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qsMinSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("unitId",StandardBasicTypes.INTEGER);
		query.addScalar("oldFormSequence",StandardBasicTypes.STRING);
		query.addScalar("cpiQoutationType",StandardBasicTypes.STRING);
		query.addScalar("compilationMethod",StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(ExportEditPriceStatisticList.class));
		
		return query.list();
	}
	
	/**
	 * Get quotation id select2 format
	 */
	public List<Integer> searchQuotationIdForFilter(String search, int firstRecord, int displayLength) {
		String hql = "select distinct q.quotationId "
                + " from IndoorQuotationRecord as iqr "
                + " inner join iqr.quotation as q "
                + " where q.quotationId is not null and q.quotationId <> '' ";

		if (StringUtils.isNotEmpty(search)) {
			hql += " and str(q.quotationId) like :search ";
		}
		
		hql += " order by q.quotationId asc";
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		query.setFirstResult(firstRecord);
		query.setMaxResults(displayLength);

		return query.list();
	}
	
	/**
	 * Count quotation id select2 format
	 */
	public Long countQuotationIdSelect2(String search) {
		String hql = "select count(distinct q.quotationId) as count "
                + " from IndoorQuotationRecord as iqr"
                + " inner join iqr.quotation as q"
                + " where q.quotationId is not null and q.quotationId <> '' ";

		if (StringUtils.isNotEmpty(search)) {
			hql += " and str(q.quotationId) like :search ";
		}
		
		Query query = getSession().createQuery(hql);
		
		if (StringUtils.isNotEmpty(search)) {
			query.setParameter("search", String.format("%%%s%%", search));
		}

		return (long)query.uniqueResult();
	}
}
