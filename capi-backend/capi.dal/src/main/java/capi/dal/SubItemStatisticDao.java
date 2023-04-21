package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.SubItemStatistic;
import capi.model.report.SummarySubItemStatistic;

@Repository("SubItemStatisticDao")
public class SubItemStatisticDao extends GenericDao<SubItemStatistic>{

	public void calculateSubItemStatistic(Date referenceMonth){
		calculateSubItemStatistic(referenceMonth, false);
	}
	
	
	public void calculateSubItemStatistic(Date referenceMonth, boolean onlyNotCalculated){
		String sql = "";
		if (onlyNotCalculated){	
			sql = "exec [CalculateSubItemStatistic] :refMonth, 1";
		}
		else{
			sql = "exec [CalculateSubItemStatistic] :refMonth";
		}
		Query query = this.getSession().createSQLQuery(sql);
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
	
	public List<SummarySubItemStatistic> getSummarySubItemStatistic(List<Integer> purpose, List<Integer> unitId, List<Integer> cpiSurveyForm, Date period) {
		String sql = "select distinct ss.SubItemStatisticId as subItemStatisticId,"
				+ " s.Code as subItemCode,"
				+ " s.ChineseName as subItemChinName,"
				+ " s.EnglishName as subItemEngName,"
				+ " u.CPIBasePeriod as cpiBasePeriod,"
				+ "		case "
				+ "			when s.CompilationMethod = 1 then 'A.M. (Supermarket, fresh)' "
				+ " 		when s.CompilationMethod = 2 then 'A.M. (Supermarket, non-fresh)' "
				+ " 		when s.CompilationMethod = 3 then 'A.M. (Market)' "
				+ " 		when s.CompilationMethod = 4 then 'G.M. (Supermarket)' "
				+ " 		when s.CompilationMethod = 5 then 'G.M. (Batch)' "
				+ " 		else 'A.M. (Batch)' "
				+ "		end as compilationMethod, "
				+ " ss.ReferenceMonth as referenceMonth,"
				+ " ss.SumCurrentSPrice as sumCurrentSPrice,"
				+ " ss.CountCurrentSPrice as countCurrentSPrice,"
				+ " ss.AverageCurrentSPrice as averageCurrentSPrice,"
				+ " ss.SumLastSPrice as sumLastSPrice,"
				+ " ss.CountLastSPrice as countLastSPrice,"
				+ " ss.AverageLastSPrice as averageLastSPrice,"
				+ " ss.FinalPRSPrice as finalPRSPrice,"
				+ " ss.StandardDeviationSPrice as standardDeviationSPrice,"
				+ " ss.MedianSPrice as medianSPrice,"
				+ " ss.MinSPrice as minSPrice,"
				+ " ss.MaxSPrice as maxSPrice,"
				+ " ss.LastHasPriceSumCurrentSPrice as lastHasPriceSumCurrentSPrice,"
				+ " ss.LastHasPriceCountCurrentSPrice as lastHasPriceCountCurrentSPrice,"
				+ " ss.LastHasPriceAverageCurrentSPrice as lastHasPriceAverageCurrentSPrice,"
				+ " ss.LastHasPriceReferenceMonth as lastHasPriceReferenceMonth,"
				+ " ss.AveragePRSPrice as averagePRSPrice,"
				+ " ss.CountPRSPrice as countPRSPrice,"
				+ " ss.SumPRSPrice as sumPRSPrice,"
				+ " ss.StandardDeviationPRSPrice as standardDeviationPRSPrice,"
				+ " ss.MedianPRPrice as medianPRPrice,"
				+ " ss.MinPRPrice as minPRPrice,"
				+ " ss.MaxPRPrice as maxPRPrice,"
				+ " ss.SubItemId as subItemId,"
				+ " ss.DeviationSum as deviationSum,"
				+ " ss.Variance as variance,"
				+ " ss.PRSPriceDeviationSum as PRSPriceDeviationSum,"
				+ " ss.PRSPriceVariance as PRSPriceVariance "
				+ " from SubItemStatistic ss "
				+ " left join SubItem s on ss.SubItemId = s.SubItemId "
				+ " left join Unit u on s.SubItemId = u.SubItemId"
				+ " left join Purpose pp on u.PurposeId = pp.PurposeId "
				+ " where ss.ReferenceMonth between :period and :period ";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		if (unitId != null && unitId.size() > 0){
			sql += " and u.unitId in (:unitId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		sql += " order by ss.referenceMonth , u.cpiBasePeriod , s.code ";
			
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
		
		query.addScalar("subItemStatisticId",StandardBasicTypes.STRING);
		query.addScalar("subItemCode",StandardBasicTypes.STRING);
		query.addScalar("subItemChinName",StandardBasicTypes.STRING);
		query.addScalar("subItemEngName",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
		query.addScalar("compilationMethod",StandardBasicTypes.STRING);
		query.addScalar("referenceMonth",StandardBasicTypes.DATE);
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
		query.addScalar("sumPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("standardDeviationPRSPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("medianPRPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("minPRPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("maxPRPrice",StandardBasicTypes.DOUBLE);
		query.addScalar("subItemId",StandardBasicTypes.STRING);
		query.addScalar("deviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("variance",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceDeviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceVariance",StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(SummarySubItemStatistic.class));
		
		return query.list();
	}
}
