package capi.dal;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.UnitStatistic;
import capi.model.report.CrossCheck;
import capi.model.report.QuotationStatisticsReportByQuotationUnitStat;
import capi.model.report.QuotationTimeSeriesUnitStat;
import capi.model.report.QuotationUnitStatistic;
import capi.model.report.SummaryStatisticsOfPriceRelativesReport;
import capi.model.report.SummaryUnitStatistic;

@Repository("UnitStatisticDao")
public class UnitStatisticDao extends GenericDao<UnitStatistic> {

	
	public void calculateUnitStatistic(Date referenceMonth){
		calculateUnitStatistic(referenceMonth, false);
	}
	
	public void calculateUnitStatistic(Date referenceMonth, boolean onlyNotCalculated){
		String sql = "";
		if (onlyNotCalculated){	
			sql = "exec [CalculateUnitStatistic] :refMonth, 1";
		}
		else{
			sql = "exec [CalculateUnitStatistic] :refMonth";
		}
		
		Query query = this.getSession().createSQLQuery(sql);
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
	
	public UnitStatistic getByUnitAndReferenceMonth(Integer unitId, Date referenceMonth) {
		Criteria criteria = this.createCriteria("stat")
				.add(Restrictions.eq("unit.id", unitId))
				.add(Restrictions.eq("referenceMonth", referenceMonth));
		return (UnitStatistic)criteria.uniqueResult();
	}
	
	public List<CrossCheck> getCrossCheck(List<Integer> purpose, Date refMonth,  
			List<String> crossCheckGroup/*, List<String> cpiBasePeriods*/) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[CrossCheckReport] :refMonth, :purpose, :crossCheckGroup");
		
		query.setParameter("refMonth", refMonth);
		
		StringBuilder b = new StringBuilder();
		if(purpose != null && purpose.size() > 0) {
			b.append("<query>");
			for(Integer i : purpose) {	
				b.append("<purposeId>"+i+"</purposeId>");
			}
			b.append("</query>");
			query.setParameter("purpose", b.toString());
		}else {
			query.setParameter("purpose", null);
		}
		if(crossCheckGroup != null && crossCheckGroup.size() > 0) {
			StringBuilder crossCheckGroupsb = new StringBuilder();
			crossCheckGroupsb.append("<query>");
			for(String i : crossCheckGroup) {	
				crossCheckGroupsb.append("<crossCheckGroupId>"+i+"</crossCheckGroupId>");
			}
			crossCheckGroupsb.append("</query>");
			query.setParameter("crossCheckGroup", crossCheckGroupsb.toString());
			//query.setParameter("crossCheckGroup", StringUtils.join(crossCheckGroup,","));
		}else {
			query.setParameter("crossCheckGroup", null);
		}
		
		query.addScalar("referenceMonth", StandardBasicTypes.STRING);
		query.addScalar("purpose", StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod", StandardBasicTypes.STRING);
		query.addScalar("groupCode", StandardBasicTypes.STRING);
		query.addScalar("groupName", StandardBasicTypes.STRING);
		query.addScalar("itemCode", StandardBasicTypes.STRING);
		query.addScalar("itemName", StandardBasicTypes.STRING);
		query.addScalar("unitCode", StandardBasicTypes.STRING);
		query.addScalar("unitName", StandardBasicTypes.STRING);
		query.addScalar("crossCheckGroup", StandardBasicTypes.STRING);
		query.addScalar("compilationMethod", StandardBasicTypes.STRING);
		query.addScalar("pr", StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(CrossCheck.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationStatisticsReportByQuotationUnitStat> getByQuotationStatisticsReportByQuotation(List<Integer> purpose, List<Integer> itemId, List<Integer> cpiSurveyForm, List<Integer> quotationId, Date referenceMonth) {
		
		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[QuotationStatisticsReportByQuotation] :refMonth, :purposeId, :cpiQuotationType, :itemId, :quotationId");
		
		query.setParameter("refMonth", referenceMonth);
		
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
		
		query.addScalar("indoorQuotationRecordId",StandardBasicTypes.STRING);
		query.addScalar("quotationRecordId",StandardBasicTypes.STRING);
		query.addScalar("quotationId",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("referenceDate",StandardBasicTypes.DATE);
		query.addScalar("purposeCode",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("unitCode",StandardBasicTypes.STRING);
		query.addScalar("unitChinName",StandardBasicTypes.STRING);
		query.addScalar("unitEngName",StandardBasicTypes.STRING);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("indoorQuotationStatus",StandardBasicTypes.STRING);
		query.addScalar("quotationRecordSequence",StandardBasicTypes.STRING);
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
		query.addScalar("isProductNotAvailable",StandardBasicTypes.STRING);
		query.addScalar("NPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("SPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qrReason",StandardBasicTypes.STRING);
		query.addScalar("qrRemark",StandardBasicTypes.STRING);
		query.addScalar("isNewRecruitment",StandardBasicTypes.STRING);
		query.addScalar("isProductChange",StandardBasicTypes.STRING);
		query.addScalar("iqrRemark",StandardBasicTypes.STRING);
		query.addScalar("isOutlier",StandardBasicTypes.STRING);
		query.addScalar("outlierRemark",StandardBasicTypes.STRING);
		query.addScalar("isNoField",StandardBasicTypes.BOOLEAN);
		query.addScalar("CPICompilationSeries",StandardBasicTypes.STRING);
		query.addScalar("compilationMethod",StandardBasicTypes.STRING);
		query.addScalar("seasonality",StandardBasicTypes.STRING);
		query.addScalar("CPIQoutationType",StandardBasicTypes.STRING);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("averageCurrentSPrice6",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice6",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("countCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("countCurrentSPrice1",StandardBasicTypes.INTEGER);
		query.addScalar("countCurrentSPrice2",StandardBasicTypes.INTEGER);
		query.addScalar("countCurrentSPrice3",StandardBasicTypes.INTEGER);
		query.addScalar("countCurrentSPrice4",StandardBasicTypes.INTEGER);
		query.addScalar("countCurrentSPrice5",StandardBasicTypes.INTEGER);
		query.addScalar("usAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice6",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice6",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice6",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice5",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountCurrentSPrice",StandardBasicTypes.INTEGER);
		query.addScalar("usCountCurrentSPrice1",StandardBasicTypes.INTEGER);
		query.addScalar("usCountCurrentSPrice2",StandardBasicTypes.INTEGER);
		query.addScalar("usCountCurrentSPrice3",StandardBasicTypes.INTEGER);
		query.addScalar("usCountCurrentSPrice4",StandardBasicTypes.INTEGER);
		query.addScalar("usCountCurrentSPrice5",StandardBasicTypes.INTEGER);
		query.addScalar("usCountPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountPRSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountPRSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountPRSPrice3",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountPRSPrice4",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountPRSPrice5",StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(QuotationStatisticsReportByQuotationUnitStat.class));
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuotationUnitStatistic> getQuotationUnitStatistic(Date referenceMonth, Integer unitId){
		String sql = "exec dbo.TempCalculateQuotationAndUnitStatistic :referenceMonth, :unitId";
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("referenceMonth", referenceMonth);
		query.setParameter("unitId", unitId);

		query.addScalar("referenceMonth", StandardBasicTypes.DATE);
		query.addScalar("quotationSumCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationCountCurrentSPrice", StandardBasicTypes.INTEGER);
		query.addScalar("quotationAverageCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationSumLastSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationCountLastSPrice", StandardBasicTypes.INTEGER);
		query.addScalar("quotationAverageLastSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationFinalPRSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationStandardDeviationSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationMedianSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationMinSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationMaxSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationLastHasPriceSumCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationLastHasPriceCountCurrentSPrice", StandardBasicTypes.INTEGER);
		query.addScalar("quotationLastHasPriceAverageCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationLastHasPriceReferenceMonth", StandardBasicTypes.DATE);
		query.addScalar("quotationDeviationSum", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationVariance", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationSumCurrentNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationCountCurrentNPrice", StandardBasicTypes.INTEGER);
		query.addScalar("quotationAverageCurrentNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationLastHasPriceSumCurrentNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationLastHasPriceCountCurrentNPrice", StandardBasicTypes.INTEGER);
		query.addScalar("quotationLastHasPriceAverageCurrentNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationAverageLastNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationCountLastNPrice", StandardBasicTypes.INTEGER);
		query.addScalar("quotationSumLastNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationFinalPRNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("quotationId", StandardBasicTypes.INTEGER);
		query.addScalar("quotationKeepNoStartMonth", StandardBasicTypes.DATE);

		query.addScalar("unitSumCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitCountCurrentSPrice", StandardBasicTypes.INTEGER);
		query.addScalar("unitAverageCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitSumLastSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitCountLastSPrice", StandardBasicTypes.INTEGER);
		query.addScalar("unitAverageLastSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitFinalPRSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitStandardDeviationSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitMedianSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitMinSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitMaxSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitLastHasPriceSumCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitLastHasPriceCountCurrentSPrice", StandardBasicTypes.INTEGER);
		query.addScalar("unitLastHasPriceAverageCurrentSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitLastHasPriceReferenceMonth", StandardBasicTypes.DATE);
		query.addScalar("unitAveragePRSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitCountPRSPrice", StandardBasicTypes.INTEGER);
		query.addScalar("unitSumPRSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitStandardDeviationPRSPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitMedianPRPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitMinPRPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitMaxPRPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitDeviationSum", StandardBasicTypes.DOUBLE);
		query.addScalar("unitVariance", StandardBasicTypes.DOUBLE);
		query.addScalar("unitPRSPriceDeviationSum", StandardBasicTypes.DOUBLE);
		query.addScalar("unitPRSPriceVariance", StandardBasicTypes.DOUBLE);
		query.addScalar("unitCountPRNPrice", StandardBasicTypes.INTEGER);
		query.addScalar("unitPRNPriceDeviationSum", StandardBasicTypes.DOUBLE);
		query.addScalar("unitPRNPriceVariance", StandardBasicTypes.DOUBLE);
		query.addScalar("unitStandardDeviationPRNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitSumPRNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitMinNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitMaxNPrice", StandardBasicTypes.DOUBLE);
		query.addScalar("unitId", StandardBasicTypes.INTEGER);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationUnitStatistic.class));
		return query.list();
	}
	
	public List<SummaryStatisticsOfPriceRelativesReport> getByPriceRelatives(Date fromMonth, Date toMonth, List<Integer> purposeIds, List<Integer> itemIds, List<String> outletTypeIds, List<Integer> cpiSurveyForms){
		String sql = "select " +
//				"ur.StaffCode as staffCode, " + 
//				"ur.ChineseName as staffChinName, " + 
//				"ur.EnglishName as staffEngName, " + 
				"iqr.ReferenceMonth as referenceMonth, " + 
				"pp.Code as purpose, " + 
				"u.CPIBasePeriod as cpiBasePeriod, " + 
				"u.Code as unitCode, " + 
				"u.ChineseName as unitChinName, " + 
				"u.EnglishName as unitEngName, " + 
				"s.Code as sectionCode, " + 
				"s.ChineseName as sectionChinName, " + 
				"s.EnglishName as sectionEngName, " + 
				"g.Code as groupCode, " + 
				"g.ChineseName as groupChinName, " + 
				"g.EnglishName as groupEngName, " + 
				"sg.Code as subGroupCode, " + 
				"sg.ChineseName as subGroupChinName, " + 
				"sg.EnglishName as subGroupEngName, " + 
				"i.Code as itemCode, " + 
				"i.ChineseName as itemChinName, " + 
				"i.EnglishName as itemEngName, " + 
				"ot.Code as outletTypeCode, " + 
				"ot.ChineseName as outletTypeChinName, " + 
				"ot.EnglishName as outletTypeEngName, " + 
				"si.Code as subItemCode, " + 
				"si.ChineseName as subItemChinName, " + 
				"si.EnglishName as subItemEngName, " + 
				"ss.FinalPRSPrice as sectionPR, " + 
				"gs.FinalPRSPrice as groupPR, " + 
				"sgs.FinalPRSPrice as subGroupPR, " + 
				"its.FinalPRSPrice as itemPR, " + 
				"os.FinalPRSPrice as outletTypePR, " + 
				"sis.FinalPRSPrice as subItemPR, " + 
				"us.FinalPRSPrice as unitPR, " + 
				"us.StandardDeviationPRSPrice as unitStandardDeviationPR, " + 
				"us.MaxPRPrice as unitMaxPR, " + 
				"us.MinPRPrice as unitMinPR, " + 
				"us.AverageCurrentSPrice as unitAvgCurrentSPrice, " + 
				"us.AverageLastSPrice as unitAvgLastSPrice, " + 
				"us.LastHasPriceAverageCurrentSPrice as unitLastHasPriceAvgCurrSPrcie, " +
				//2018-01-09 cheung_cheng  
				// CPI Compilation method" should show "G.M. (Batch)", "A.M. (Batch)", "A.M. (Market)", "A.M. (Supermarket, fresh)", "G.M. (Supermarket)", "A.M. (Supermarket, non-fresh)"
//				"si.CompilationMethod as compilationMethod " + 
				"case " +
				"	when si.CompilationMethod = 1 then 'A.M. (Supermarket, fresh)' " +
				" 		when si.CompilationMethod = 2 then 'A.M. (Supermarket, non-fresh)' " +
				" 		when si.CompilationMethod = 3 then 'A.M. (Market)' " +
				" 		when si.CompilationMethod = 4 then 'G.M. (Supermarket)' " +
				" 		when si.CompilationMethod = 5 then 'G.M. (Batch)' " +
				" 	else 'A.M. (Batch)' " +
				"end as compilationMethod " +
				"from IndoorQuotationRecord iqr " + 
//				"join [User] as ur on iqr.userId = ur.userId " + 
//				"join QuotationRecord as qr on iqr.QuotationRecordId = qr.QuotationRecordId " + 
				"join Quotation as q on q.QuotationId = iqr.QuotationId " + 
				"left join Unit as u on u.UnitId = q.UnitId " + 
				"left join Purpose as pp on pp.PurposeId = u.PurposeId " + 
				"left join SubItem as si on si.SubItemId = u.SubItemId " + 
				"left join OutletType as ot on ot.OutletTypeId = si.OutletTypeId " + 
				"left join Item as i on i.ItemId = ot.ItemId " + 
				"left join SubGroup as sg on sg.SubGroupId = i.SubGroupId " + 
				"left join [Group] as g on g.GroupId = sg.GroupId " + 
				"left join Section as s on s.SectionId = g.SectionId " + 
				"left join UnitStatistic as us on us.[UnitId] = u.[UnitId] and iqr.ReferenceMonth = us.ReferenceMonth " + 
				"left join SectionStatistic as ss on ss.SectionId = s.SectionId and iqr.ReferenceMonth = ss.ReferenceMonth " + 
				"left join GroupStatistic as gs on gs.GroupId = g.GroupId and iqr.ReferenceMonth = gs.ReferenceMonth " + 
				"left join SubGroupStatistic as sgs on sgs.SubGroupId = sg.SubGroupId and iqr.ReferenceMonth = sgs.ReferenceMonth " + 
				"left join ItemStatistic as its on its.ItemId = i.ItemId and iqr.ReferenceMonth = its.ReferenceMonth " + 
				"left join OutletTypeStatistic as os on os.OutletTypeId = ot.OutletTypeId and os.ReferenceMonth = iqr.ReferenceMonth " + 
				"left join SubItemStatistic as sis on sis.SubItemId = si.SubItemId and sis.ReferenceMonth = iqr.ReferenceMonth " +
				"where iqr.ReferenceMonth between :fromMonth and :toMonth ";
		
		if(purposeIds!=null && purposeIds.size()>0){
			sql += " and pp.purposeId in (:purposeIds)";
		}
		
		if(itemIds!=null && itemIds.size()>0){
			sql += " and i.itemId in (:itemIds)";
		}
		
		if(outletTypeIds!=null && outletTypeIds.size()>0){
			//2018-01-09 cheung_cheng 'length' is not a recognized built-in function name.
//			sql += " and substring(ot.code, length(ot.code) - 2, length(ot.code)) in (:outletTypeIds) ";
			sql += " and substring(ot.code, len(ot.code) - 2, len(ot.code)) in (:outletTypeIds) ";
		}
		
		if(cpiSurveyForms!=null && cpiSurveyForms.size()>0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForms)";
		}
		
		sql += " group by "
//				+ "	ur.StaffCode, ur.ChineseName, ur.EnglishName, "
				+ " iqr.ReferenceMonth , pp.Code, u.CPIBasePeriod, u.Code, u.ChineseName, u.EnglishName, s.Code, s.ChineseName, s.EnglishName, g.Code, g.ChineseName, g.EnglishName, sg.Code, sg.ChineseName, sg.EnglishName , i.Code , i.ChineseName , i.EnglishName , ot.Code , ot.ChineseName , ot.EnglishName , si.Code , si.ChineseName , si.EnglishName , ss.FinalPRSPrice , gs.FinalPRSPrice , sgs.FinalPRSPrice , its.FinalPRSPrice , os.FinalPRSPrice , sis.FinalPRSPrice , us.FinalPRSPrice , us.StandardDeviationPRSPrice  , us.MaxPRPrice  , us.MinPRPrice  , us.AverageCurrentSPrice  , us.AverageLastSPrice  , us.LastHasPriceAverageCurrentSPrice , si.CompilationMethod"
				+ " order by iqr.ReferenceMonth , pp.Code, u.CPIBasePeriod , u.Code ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
		query.setParameter("fromMonth", fromMonth);
		query.setParameter("toMonth", toMonth);
		if(purposeIds!=null && purposeIds.size()>0){
			query.setParameterList("purposeIds", purposeIds);
		}
		
		if(itemIds!=null && itemIds.size()>0){
			query.setParameterList("itemIds", itemIds);
		}
		
		if(outletTypeIds!=null && outletTypeIds.size()>0){
			query.setParameterList("outletTypeIds", outletTypeIds);
		}
		
		if(cpiSurveyForms!=null && cpiSurveyForms.size()>0){
			query.setParameterList("cpiSurveyForms", cpiSurveyForms);
		}
		
//		query.addScalar("staffCode",StandardBasicTypes.STRING);
//		query.addScalar("staffChinName",StandardBasicTypes.STRING);
//		query.addScalar("staffEngName",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("purpose",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("unitCode",StandardBasicTypes.STRING);
		query.addScalar("unitChinName",StandardBasicTypes.STRING);
		query.addScalar("unitEngName",StandardBasicTypes.STRING);
		query.addScalar("sectionCode",StandardBasicTypes.STRING);
		query.addScalar("sectionChinName",StandardBasicTypes.STRING);
		query.addScalar("sectionEngName",StandardBasicTypes.STRING);
		query.addScalar("groupCode",StandardBasicTypes.STRING);
		query.addScalar("groupChinName",StandardBasicTypes.STRING);
		query.addScalar("groupEngName",StandardBasicTypes.STRING);
		query.addScalar("subGroupCode",StandardBasicTypes.STRING);
		query.addScalar("subGroupChinName",StandardBasicTypes.STRING);
		query.addScalar("subGroupEngName",StandardBasicTypes.STRING);
		query.addScalar("itemCode",StandardBasicTypes.STRING);
		query.addScalar("itemChinName",StandardBasicTypes.STRING);
		query.addScalar("itemEngName",StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode",StandardBasicTypes.STRING);
		query.addScalar("outletTypeChinName",StandardBasicTypes.STRING);
		query.addScalar("outletTypeEngName",StandardBasicTypes.STRING);
		query.addScalar("subItemCode",StandardBasicTypes.STRING);
		query.addScalar("subItemChinName",StandardBasicTypes.STRING);
		query.addScalar("subItemEngName",StandardBasicTypes.STRING);
		query.addScalar("sectionPR",StandardBasicTypes.DOUBLE);
		query.addScalar("groupPR",StandardBasicTypes.DOUBLE);
		query.addScalar("subGroupPR",StandardBasicTypes.DOUBLE);
		query.addScalar("itemPR",StandardBasicTypes.DOUBLE);
		query.addScalar("outletTypePR",StandardBasicTypes.DOUBLE);
		query.addScalar("subItemPR",StandardBasicTypes.DOUBLE);
		query.addScalar("unitPR",StandardBasicTypes.DOUBLE);
		query.addScalar("unitStandardDeviationPR",StandardBasicTypes.DOUBLE);
		query.addScalar("unitMaxPR",StandardBasicTypes.DOUBLE);
		query.addScalar("unitMinPR",StandardBasicTypes.DOUBLE);
		query.addScalar("unitAvgCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("unitAvgLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("unitLastHasPriceAvgCurrSPrcie",StandardBasicTypes.DOUBLE);
		//2018-01-09 cheung_cheng compilation method should be string
//		query.addScalar("compilationMethod",StandardBasicTypes.INTEGER);
		query.addScalar("compilationMethod",StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(SummaryStatisticsOfPriceRelativesReport.class));
		
		return query.list();
	}
	
	public List<SummaryUnitStatistic> getSummaryUnitStatistic(List<Integer> purpose, List<Integer> unitId, 
			List<Integer> cpiSurveyForm, Date period) {

		String sql = "select us.UnitStatisticId as unitStatisticId,"
				+ " us.ReferenceMonth as referenceMonth,"
				+ " u.Code as unitCode,"
				+ " u.ChineseName as unitChineseName,"
				+ " u.EnglishName as unitEnglishName,"
				+ " u.CPIBasePeriod as cpiBasePeriod,"
				+ "		case "
				+ "			when s.CompilationMethod = 1 then 'A.M. (Supermarket, fresh)' "
				+ " 		when s.CompilationMethod = 2 then 'A.M. (Supermarket, non-fresh)' "
				+ " 		when s.CompilationMethod = 3 then 'A.M. (Market)' "
				+ " 		when s.CompilationMethod = 4 then 'G.M. (Supermarket)' "
				+ " 		when s.CompilationMethod = 5 then 'G.M. (Batch)' "
				+ " 		else 'A.M. (Batch)' "
				+ "		end as compilationMethod, "
				+ " u.CPIQoutationType as cpiQuotationType,"
				+ " us.SumCurrentSPrice as sumCurrentSPrice,"
				+ " us.CountCurrentSPrice as countCurrentSPrice,"
				+ " us.AverageCurrentSPrice as averageCurrentSPrice,"
				+ " us.SumLastSPrice as sumLastSPrice,"
				+ " us.CountLastSPrice as countLastSPrice,"
				+ " us.AverageLastSPrice as averageLastSPrice,"
				+ " us.FinalPRSPrice as finalPRSPrice,"
				+ " us.StandardDeviationSPrice as standardDeviationSPrice,"
				+ " us.MedianSPrice as medianSPrice,"
				+ " us.MinSPrice as minSPrice,"
				+ " us.MaxSPrice as maxSPrice,"
				+ " us.LastHasPriceSumCurrentSPrice as lastHasPriceSumCurrentSPrice,"
				+ " us.LastHasPriceCountCurrentSPrice as lastHasPriceCountCurrentSPrice,"
				+ " us.LastHasPriceAverageCurrentSPrice as lastHasPriceAverageCurrentSPrice,"
				+ " us.LastHasPriceReferenceMonth as lastHasPriceReferenceMonth,"
				+ " us.AveragePRSPrice as averagePRSPrice,"
				+ " us.CountPRSPrice as countPRSPrice,"
				+ " us.SumPRSPrice as sumPRSPrice,"
				+ " us.StandardDeviationPRSPrice as standardDeviationPRSPrice,"
				+ " us.MedianPRPrice as medianPRPrice,"
				+ " us.MinPRPrice as minPRPrice,"
				+ " us.MaxPRPrice as maxPRPrice,"
				+ " us.CreatedDate as createDate,"
				+ " us.ModifiedDate as modifyDate,"
				+ " us.CreatedBy as createBy,"
				+ " us.ModifiedBy as modifyBy,"
				+ " us.UnitId as unitId,"
				+ " us.DeviationSum as deviationSum,"
				+ " us.Variance as variance,"
				+ " us.PRSPriceDeviationSum as PRSPriceDeviationSum,"
				+ " us.PRSPriceVariance as PRSPriceVariance,"
				+ " us.CountPRNPrice as countPRNPrice,"
				+ " us.PRNPriceDeviationSum as PRNPriceDeviationSum,"
				+ " us.PRNPriceVariance as PRNPriceVariance,"
				+ " us.StandardDeviationPRNPrice as standardDeviationPRNPrice,"
				+ " us.SumPRNPrice as sumPRNPrice,"
				+ " us.MinNPrice as minNPrice,"
				+ " us.MaxNPrice as maxNPrice"
				+ " from UnitStatistic us "
				+ " left join Unit u on us.UnitId = u.UnitId "
				+ " left join Purpose pp on u.PurposeId = pp.PurposeId"
				+ " left join SubItem s on u.SubItemId = s.SubItemId "
				+ " where us.ReferenceMonth between :period and :period ";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		if (unitId != null && unitId.size() > 0){
			sql += " and u.unitId in (:unitId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		sql += " order by us.referenceMonth , u.cpiBasePeriod , u.code ";
			
		SQLQuery query = this.getSession().createSQLQuery(sql);
		
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
		
		query.addScalar("unitStatisticId",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("unitCode",StandardBasicTypes.STRING);
		query.addScalar("unitChineseName",StandardBasicTypes.STRING);
		query.addScalar("unitEnglishName",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("compilationMethod",StandardBasicTypes.STRING);
		query.addScalar("cpiQuotationType",StandardBasicTypes.STRING);
		query.addScalar("sumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("countCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("sumLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("countLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("averageLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("medianSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceSumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceCountCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceReferenceMonth",StandardBasicTypes.DATE);
		query.addScalar("averagePRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("countPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("countPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("sumPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("medianPRPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minPRPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("maxPRPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("createDate",StandardBasicTypes.DATE);
		query.addScalar("modifyDate",StandardBasicTypes.DATE);
		query.addScalar("createBy",StandardBasicTypes.STRING);
		query.addScalar("modifyBy",StandardBasicTypes.STRING);
		query.addScalar("unitId",StandardBasicTypes.STRING);
		query.addScalar("deviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("variance",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceDeviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceVariance",StandardBasicTypes.DOUBLE);
		query.addScalar("countPRNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("PRNPriceDeviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("PRNPriceVariance",StandardBasicTypes.DOUBLE);	
		query.addScalar("standardDeviationPRNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("sumPRNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("maxNPrice",StandardBasicTypes.DOUBLE);	
		query.setResultTransformer(Transformers.aliasToBean(SummaryUnitStatistic.class));
		
		return query.list();
	}
	
	public List<QuotationTimeSeriesUnitStat> getQuotationTimeSeriesUnitStat(List<Integer> purpose, 
			List<Integer> itemId, List<Integer> cpiSurveyForm, Date referenceMonth, List<Integer> quotationId) {

		SQLQuery query = this.getSession().createSQLQuery("exec [dbo].[QuotationStatisticsReportByQuotation] :refMonth, :purposeId, :cpiQuotationType, :itemId, :quotationId");
		

		query.setParameter("refMonth", referenceMonth);
		
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
		
		if(quotationId != null && quotationId.size() > 0) {
			builder.append("<query>");
			for(Integer id : quotationId) {
				builder.append("<quotationId>"+id+"</quotationId>");
			}
			builder.append("</query>");
			query.setParameter("quotationId", builder.toString());
		}else {
			query.setParameter("quotationId", null);
		}
		
		query.addScalar("indoorQuotationRecordId",StandardBasicTypes.STRING);
		query.addScalar("quotationRecordId",StandardBasicTypes.STRING);
		query.addScalar("quotationId",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
		query.addScalar("referenceDate",StandardBasicTypes.DATE);
		query.addScalar("purposeCode",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("unitCode",StandardBasicTypes.STRING);
		query.addScalar("unitChinName",StandardBasicTypes.STRING);
		query.addScalar("unitEngName",StandardBasicTypes.STRING);
		query.addScalar("quotationStatus",StandardBasicTypes.STRING);
		query.addScalar("indoorQuotationStatus",StandardBasicTypes.STRING);
		query.addScalar("quotationRecordSequence",StandardBasicTypes.STRING);
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
		query.addScalar("isProductNotAvailable",StandardBasicTypes.STRING);
		query.addScalar("NPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("SPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("previousSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("currentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("qrReason",StandardBasicTypes.STRING);
		query.addScalar("qrRemark",StandardBasicTypes.STRING);
		query.addScalar("isNewRecruitment",StandardBasicTypes.STRING);
		query.addScalar("isProductChange",StandardBasicTypes.STRING);
		query.addScalar("iqrRemark",StandardBasicTypes.STRING);
		query.addScalar("isOutlier",StandardBasicTypes.STRING);
		query.addScalar("outlierRemark",StandardBasicTypes.STRING);
		query.addScalar("isNoField",StandardBasicTypes.STRING);
		query.addScalar("CPICompilationSeries",StandardBasicTypes.STRING);
		query.addScalar("compilationMethod",StandardBasicTypes.STRING);
		query.addScalar("seasonality",StandardBasicTypes.STRING);
		query.addScalar("CPIQoutationType",StandardBasicTypes.STRING);
		query.addScalar("staffCode",StandardBasicTypes.STRING);
		query.addScalar("averageCurrentSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("countCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("countCurrentSPrice1",StandardBasicTypes.INTEGER);
		query.addScalar("countCurrentSPrice2",StandardBasicTypes.INTEGER);
		query.addScalar("usAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usAverageCurrentSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usAveragePRSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usFinalPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usStandardDeviationSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usMinSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usMaxSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usSumCurrentSPrice2",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountCurrentSPrice",StandardBasicTypes.INTEGER);
		query.addScalar("usCountCurrentSPrice1",StandardBasicTypes.INTEGER);
		query.addScalar("usCountCurrentSPrice2",StandardBasicTypes.INTEGER);
		query.addScalar("usCountPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountPRSPrice1",StandardBasicTypes.DOUBLE);
		query.addScalar("usCountPRSPrice2",StandardBasicTypes.DOUBLE);		
		query.setResultTransformer(Transformers.aliasToBean(QuotationTimeSeriesUnitStat.class));
		
		return query.list();
	}
	
}
