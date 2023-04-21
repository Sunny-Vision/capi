package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.SubGroupStatistic;
import capi.model.report.SummarySubGroupStatistic;

@Repository("SubGroupStatisticDao")
public class SubGroupStatisticDao  extends GenericDao<SubGroupStatistic>{

	public void calculateSubGroupStatistic(Date referenceMonth){
		calculateSubGroupStatistic(referenceMonth, false);
	}
	
	public void calculateSubGroupStatistic(Date referenceMonth, boolean onlyNotCalculated){
		String sql = "";
		if (onlyNotCalculated){	
			sql = "exec [CalculateSubGroupStatistic] :refMonth, 1";
		}
		else{
			sql = "exec [CalculateSubGroupStatistic] :refMonth";
		}
		Query query = this.getSession().createSQLQuery(sql);
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
	
	public List<SummarySubGroupStatistic> getSummarySubGroupStatistic(List<Integer> purpose, List<Integer> unitId, List<Integer> cpiSurveyForm, Date period) {
		String sql = "select ss.SubGroupStatisticId as subGroupStatisticId,"
				+ " sg.Code as subGroupCode,"
				+ " sg.ChineseName as subGroupChinName,"
				+ " sg.EnglishName as subGroupEngName,"
				+ " u.CPIBasePeriod as cpiBasePeriod,"
				+ " ss.ReferenceMonth as referenceMonth,"
				+ " ss.SumCurrentSPrice as sumCurrentSPrice,"
				+ " ss.CountCurrentSPrice as countCurrentSPrice,"
				+ " ss.AverageCurrentSPrice as averageCurrentSPrice,"
				+ " ss.SumLastSPrice as sumLastSPrice,"
				+ " ss.CountLastSPrice as countLastSPrice,"
				+ " ss.AverageLastSPrice as averageLastSPrice,"
				+ " ss.FinalPRSPrice as finalPRSPrice,"
				+ " ss.StandardDeviationSPrice as  standardDeviationSPrice,"
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
				+ " sg.subGroupId as subGroupId,"
				+ " ss.DeviationSum as deviationSum,"
				+ " ss.Variance as variance,"
				+ " ss.PRSPriceDeviationSum as PRSPriceDeviationSum,"
				+ " ss.PRSPriceVariance as PRSPriceVariance"
				+ " from SubGroupStatistic ss"
				+ " left join SubGroup sg on sg.SubGroupId = ss.SubGroupId "
				+ " left join Item i on i.SubGroupId = ss.SubGroupId "
				+ " left join OutletType ot on ot.ItemId = i.ItemId "
				+ " left join SubItem s on s.OutletTypeId = ot.OutletTypeId "
				+ " left join Unit u on s.SubItemId = u.SubItemId "
				+ " left join Purpose pp on u.PurposeId = pp.PurposeId"
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
		
		sql += " group by "
				+ " ss.SubGroupStatisticId, "
				+ " sg.Code, sg.ChineseName, "
				+ " sg.EnglishName, "
				+ " u.CPIBasePeriod, "
				+ " ss.ReferenceMonth, "
				+ " ss.SumCurrentSPrice, "
				+ " ss.CountCurrentSPrice,"
				+ " ss.AverageCurrentSPrice,"
				+ " ss.SumLastSPrice,"
				+ " ss.CountLastSPrice,"
				+ " ss.AverageLastSPrice,"
				+ " ss.FinalPRSPrice,"
				+ " ss.StandardDeviationSPrice,"
				+ " ss.MedianSPrice,"
				+ " ss.MinSPrice,"
				+ " ss.MaxSPrice,"
				+ " ss.LastHasPriceSumCurrentSPrice,"
				+ " ss.LastHasPriceCountCurrentSPrice,"
				+ " ss.LastHasPriceAverageCurrentSPrice,"
				+ " ss.LastHasPriceReferenceMonth,"
				+ " ss.AveragePRSPrice,"
				+ " ss.CountPRSPrice,"
				+ " ss.SumPRSPrice,"
				+ " ss.StandardDeviationPRSPrice,"
				+ " ss.MedianPRPrice,"
				+ " ss.MinPRPrice,"
				+ " ss.MaxPRPrice,"
				+ " sg.subGroupId,"
				+ " ss.DeviationSum,"
				+ " ss.Variance,"
				+ " ss.PRSPriceDeviationSum,"
				+ " ss.PRSPriceVariance"
				+ " order by ss.referenceMonth , u.cpiBasePeriod , sg.code ";
			
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
		
		query.addScalar("subGroupStatisticId",StandardBasicTypes.STRING);
		query.addScalar("subGroupCode",StandardBasicTypes.STRING);
		query.addScalar("subGroupChinName",StandardBasicTypes.STRING);
		query.addScalar("subGroupEngName",StandardBasicTypes.STRING);
		query.addScalar("cpiBasePeriod",StandardBasicTypes.STRING);
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
		query.addScalar("subGroupId",StandardBasicTypes.STRING);
		query.addScalar("deviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("variance",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceDeviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceVariance",StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(SummarySubGroupStatistic.class));
		
		return query.list();
	}
}
