package capi.dal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.QuotationStatistic;
import capi.model.SystemConstant;
import capi.model.report.QuotationStatisticsReportByQuotationQuotationStat;
import capi.model.report.QuotationTimeSeriesQuotationStat;
import capi.model.report.SummaryQuotationStatistic;

@Repository("QuotationStatisticDao")
public class QuotationStatisticDao extends GenericDao<QuotationStatistic>{

	
	public void calculateQuotationStatistic(Date referenceMonth){
		Query query = this.getSession().createSQLQuery("exec [CalculateQuotationStatistic] :refMonth");
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
	
	
	public long countQuotationStatisticInMonth(Date month){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.eq("referenceMonth", month));
		
		return (long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public QuotationStatisticsReportByQuotationQuotationStat getQuotationStatisticsReportByQuotationQuotationStat(int quotationId, Date referenceMonth) {

		Calendar month1 = Calendar.getInstance();
		month1.setTime(referenceMonth);
		month1.add(Calendar.MONTH, -1);

		Calendar month2 = Calendar.getInstance();
		month2.setTime(referenceMonth);
		month2.add(Calendar.MONTH, -2);

		Calendar month3 = Calendar.getInstance();
		month3.setTime(referenceMonth);
		month3.add(Calendar.MONTH, -3);

		Calendar month4 = Calendar.getInstance();
		month4.setTime(referenceMonth);
		month4.add(Calendar.MONTH, -4);

		Calendar month5 = Calendar.getInstance();
		month5.setTime(referenceMonth);
		month5.add(Calendar.MONTH, -5);
		
		String hql = " select q.quotationId as quotationId, "
				+ " qs0.averageCurrentSPrice as averagePriceT0, qs1.averageCurrentSPrice as averagePriceT1, qs2.averageCurrentSPrice as averagePriceT2, qs3.averageCurrentSPrice as averagePriceT3, qs4.averageCurrentSPrice as averagePriceT4, qs5.averageCurrentSPrice as averagePriceT5, "
				+ " qs0.finalPRSPrice as averagePricePRT0, qs1.finalPRSPrice as averagePricePRT1, qs2.finalPRSPrice as averagePricePRT2, qs3.finalPRSPrice as averagePricePRT3, qs4.finalPRSPrice as averagePricePRT4, qs5.finalPRSPrice as averagePricePRT5, "
				+ " qs0.standardDeviationSPrice as averagePriceStandardDeviationT0, qs1.standardDeviationSPrice as averagePriceStandardDeviationT1, qs2.standardDeviationSPrice as averagePriceStandardDeviationT2, qs3.standardDeviationSPrice as averagePriceStandardDeviationT3, qs4.standardDeviationSPrice as averagePriceStandardDeviationT4, qs5.standardDeviationSPrice as averagePriceStandardDeviationT5, "
				+ " qs0.minSPrice as minPriceT0, qs1.minSPrice as minPriceT1, qs2.minSPrice as minPriceT2, qs3.minSPrice as minPriceT3, qs4.minSPrice as minPriceT4, qs5.minSPrice as minPriceT5, "
				+ " qs0.maxSPrice as maxPriceT0, qs1.maxSPrice as maxPriceT1, qs2.maxSPrice as maxPriceT2, qs3.maxSPrice as maxPriceT3, qs4.maxSPrice as maxPriceT4, qs5.maxSPrice as maxPriceT5, "
				+ " qs0.sumCurrentSPrice as sumT0, qs1.sumCurrentSPrice as sumT1, qs2.sumCurrentSPrice as sumT2, qs3.sumCurrentSPrice as sumT3, qs4.sumCurrentSPrice as sumT4, qs5.sumCurrentSPrice as sumT5, "
				+ " qs0.countCurrentSPrice as countT0, qs1.countCurrentSPrice as countT1, qs2.countCurrentSPrice as countT2, qs3.countCurrentSPrice as countT3, qs4.countCurrentSPrice as countT4, qs5.countCurrentSPrice as countT5, "
				+ " qs0.id as statId0, qs1.id as statId1, qs2.id as statId2, qs3.id as statId3, qs4.id as statId4, qs5.id as statId5 "
				+ " from Quotation as q "
				+ " inner join q.quotationStatistics as qs0 on qs0.referenceMonth = :referenceMonth0 "
				+ " left join q.quotationStatistics as qs1 on qs1.referenceMonth = :referenceMonth1 "
				+ " left join q.quotationStatistics as qs2 on qs2.referenceMonth = :referenceMonth2 "
				+ " left join q.quotationStatistics as qs3 on qs3.referenceMonth = :referenceMonth3 "
				+ " left join q.quotationStatistics as qs4 on qs4.referenceMonth = :referenceMonth4 "
				+ " left join q.quotationStatistics as qs5 on qs5.referenceMonth = :referenceMonth5 "
				+ " where 1 = 1 ";
		
		hql += " and q.quotationId = :quotationId ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("referenceMonth0", referenceMonth);
		query.setParameter("referenceMonth1", month1.getTime());
		query.setParameter("referenceMonth2", month2.getTime());
		query.setParameter("referenceMonth3", month3.getTime());
		query.setParameter("referenceMonth4", month4.getTime());
		query.setParameter("referenceMonth5", month5.getTime());
		
		query.setParameter("quotationId", quotationId);

		query.setResultTransformer(Transformers.aliasToBean(QuotationStatisticsReportByQuotationQuotationStat.class));
		
		return (QuotationStatisticsReportByQuotationQuotationStat)query.uniqueResult();
	}
	
	public QuotationTimeSeriesQuotationStat getQuotationTimeSeriesQuotationStat(Integer quotationId, Date period) {

		String qsReferenceMonth = String.format("format(qs.referenceMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		String qsKeepMonth = String.format("format(qs.keepNoStartMonth, '%s', 'en-us')", SystemConstant.DATE_FORMAT);
		
		String hql = " select q.quotationId as quotationId "
				+ ", case when qs.referenceMonth is null then '' else " + qsReferenceMonth + " end as qsReferenceMonth "
				+ ", qs.averageCurrentSPrice as qsAveragePrice, qs.finalPRSPrice as qsAveragePricePR "
				+ ", qs.standardDeviationSPrice as qsAveragePriceSD "
				+ ", qs.minSPrice as qsMinPrice, qs.maxSPrice as qsMaxPrice, qs.countCurrentSPrice as qsCount"
				+ ", qs.sumCurrentSPrice as qsSumPrice "
				+ ", case when qs.keepNoStartMonth is null then '' else " + qsKeepMonth + " end as qsKeepMonth "
				+ " from Quotation as q "
				+ "  inner join q.quotationStatistics as qs on qs.referenceMonth = :period "
				+ " where 1 = 1 ";
		
		hql += " and q.quotationId = :quotationId ";
		
		hql += "group by q.quotationId "
			+ ", qs.referenceMonth, qs.averageCurrentSPrice, qs.finalPRSPrice "
			+ ", qs.standardDeviationSPrice, qs.minSPrice, qs.maxSPrice, qs.countCurrentSPrice, qs.sumCurrentSPrice, qs.keepNoStartMonth ";
		
		Query query = this.getSession().createQuery(hql);
		
		query.setParameter("period", period);
		query.setParameter("quotationId", quotationId);
		
		query.setResultTransformer(Transformers.aliasToBean(QuotationTimeSeriesQuotationStat.class));
		
		return (QuotationTimeSeriesQuotationStat)query.uniqueResult();
	}
	
	public List<SummaryQuotationStatistic> getSummaryQuotationStatistic(List<Integer> purpose, List<Integer> unitId, 
			List<Integer> cpiSurveyForm, Date period) {

		String sql = "select qs.QuotationStatisticId as quotationStatisticId,"
				+ " qs.ReferenceMonth as referenceMonth,"
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
				+ " qs.SumCurrentSPrice as sumCurrentSPrice,"
				+ " qs.CountCurrentSPrice as countCurrentSPrice,"
				+ " qs.AverageCurrentSPrice as averageCurrentSPrice,"
				+ " qs.SumLastSPrice as sumLastSPrice,"
				+ " qs.CountLastSPrice as countLastSPrice,"
				+ " qs.AverageLastSPrice as averageLastSPrice,"
				+ " qs.FinalPRSPrice as finalPRSPrice,"
				+ " qs.CreatedDate as createDate,"
				+ " qs.ModifiedDate as modifyDate,"
				+ " qs.CreatedBy as createBy,"
				+ " qs.ModifiedBy as modifyBy,"
				+ " qs.StandardDeviationSPrice as standardDevitationSPrice,"
				+ " qs.MedianSPrice as medianSPrice,"
				+ " qs.MinSPrice as minSPrice,"
				+ " qs.MaxSPrice as maxSPrice,"
				+ " qs.LastHasPriceSumCurrentSPrice as lastHasPriceSumCurrentSPrice,"
				+ " qs.LastHasPriceCountCurrentSPrice as lastHasPriceCountCurrentSPrice,"
				+ " qs.LastHasPriceAverageCurrentSPrice as lastHasPriceAverageCurrentSPrice,"
				+ " qs.LastHasPriceReferenceMonth as lastHasPriceReferenceMonth,"
				+ " qs.QuotationId as quotationId,"
				+ " qs.DeviationSum as deviationSum,"
				+ " qs.Variance as varinance,"
				+ " qs.SumCurrentNPrice as sumCurrentNPrice,"
				+ " qs.CountCurrentNPrice as countCurrentNPrice,"
				+ " qs.AverageCurrentNPrice as averageCurrentNPrice,"
				+ " qs.LastHasPriceSumCurrentNPrice as lastHasPriceSumCurrentNPrice,"
				+ " qs.LastHasPriceCountCurrentNPrice as lastHasPriceCountCurrentNPrice,"
				+ " qs.LastHasPriceAverageCurrentNPrice as lastHasPriceAverageCurrentNPrice,"
				+ " qs.FinalPRNPrice as finalPRNPrice,"
				+ " qs.AverageLastNPrice as averageLastNPrice,"
				+ " qs.CountLastNPrice as countLastNPrice,"
				+ " qs.SumLastNPrice as sumLastNPrice,"
				+ " qs.LastHasPriceSumLastSPrice as lastHasPriceSumLastSPrice,"
				+ " qs.LastHasPriceCountLastSPrice as lastHasPriceCountLastSPrice,"
				+ " qs.LastHasPriceAverageLastSPrice as lastHasPriceAverageLastSPrice,"
				+ " qs.KeepNoStartMonth as keepNostartMonth"
				+ " from QuotationStatistic qs "
				+ " left join Quotation q on qs.QuotationId = q.QuotationId "
				+ " left join Unit u on q.UnitId = u.UnitId"
				+ "	left join Purpose pp on u.PurposeId = pp.PurposeId "
				+ " left join SubItem s on u.SubItemId = s.SubItemId "
				+ " where qs.ReferenceMonth between :period and :period ";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		if (unitId != null && unitId.size() > 0){
			sql += " and u.unitId in (:unitId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		sql += " order by qs.referenceMonth , u.cpiBasePeriod , u.code ";
			
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
		
		query.addScalar("quotationStatisticId",StandardBasicTypes.STRING);
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
		query.addScalar("createDate",StandardBasicTypes.DATE);
		query.addScalar("modifyDate",StandardBasicTypes.DATE);
		query.addScalar("createBy",StandardBasicTypes.STRING);
		query.addScalar("modifyBy",StandardBasicTypes.STRING);
		query.addScalar("standardDevitationSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("medianSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("maxSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceSumCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceCountCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceAverageCurrentSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceReferenceMonth",StandardBasicTypes.DATE);
		query.addScalar("quotationId",StandardBasicTypes.STRING);
		query.addScalar("deviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("varinance",StandardBasicTypes.DOUBLE);
		query.addScalar("sumCurrentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("countCurrentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("averageCurrentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceSumCurrentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceCountCurrentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceAverageCurrentNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("finalPRNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("averageLastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("countLastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("sumLastNPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceSumLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceCountLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceAverageLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("lastHasPriceCountLastSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("keepNostartMonth",StandardBasicTypes.DATE);
		
		query.setResultTransformer(Transformers.aliasToBean(SummaryQuotationStatistic.class));
		
		return query.list();
	}
	
}
