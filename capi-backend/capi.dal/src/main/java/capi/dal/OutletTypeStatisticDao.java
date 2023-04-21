package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.OutletTypeStatistic;
import capi.model.report.SummaryOutletTypeStatistic;

@Repository("OutletTypeStatisticDao")
public class OutletTypeStatisticDao extends GenericDao<OutletTypeStatistic>{

	public void calculateOutletTypeStatistic(Date referenceMonth){
		calculateOutletTypeStatistic(referenceMonth, false);
	}
	
	public void calculateOutletTypeStatistic(Date referenceMonth, boolean onlyNotCalculated){
		String sql = "";
		if (onlyNotCalculated){	
			sql = "exec [CalculateOutletTypeStatistic] :refMonth, 1";
		}
		else{
			sql = "exec [CalculateOutletTypeStatistic] :refMonth";
		}
		Query query = this.getSession().createSQLQuery(sql);
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
	
	public List<SummaryOutletTypeStatistic> getSummaryOutletTypeStatistic(List<Integer> purpose, List<Integer> unitId, List<Integer> cpiSurveyForm, Date period) {
		String sql = "select distinct os.OutletTypeStatisticId as outletTypeStatisticId,"
				+ " ot.Code as outletTypeCode,"
				+ " ot.ChineseName as outletTypeChinName,"
				+ " ot.EnglishName as outletTypeEngName,"
				+ " u.CPIBasePeriod as cpiBasePeriod, "
				+ " os.ReferenceMonth as referenceMonth,"
				+ " os.SumCurrentSPrice as sumCurrentSPrice,"
				+ " os.CountCurrentSPrice as countCurrentSPrice,"
				+ " os.AverageCurrentSPrice as averageCurrentSPrice,"
				+ " os.SumLastSPrice as sumLastSPrice,"
				+ " os.CountLastSPrice as countLastSPrice,"
				+ " os.AverageLastSPrice as averageLastSPrice,"
				+ " os.FinalPRSPrice as finalPRSPrice,"
				+ " os.StandardDeviationSPrice as  standardDeviationSPrice,"
				+ " os.MedianSPrice as medianSPrice,"
				+ " os.MinSPrice as minSPrice,"
				+ " os.MaxSPrice as maxSPrice,"
				+ " os.LastHasPriceSumCurrentSPrice as lastHasPriceSumCurrentSPrice,"
				+ " os.LastHasPriceCountCurrentSPrice as lastHasPriceCountCurrentSPrice,"
				+ " os.LastHasPriceAverageCurrentSPrice as lastHasPriceAverageCurrentSPrice,"
				+ " os.LastHasPriceReferenceMonth as lastHasPriceReferenceMonth,"
				+ " os.AveragePRSPrice as averagePRSPrice,"
				+ " os.CountPRSPrice as countPRSPrice,"
				+ " os.SumPRSPrice as sumPRSPrice, "
				+ " os.StandardDeviationPRSPrice as standardDeviationPRSPrice,"
				+ " os.MedianPRPrice as medianPRPrice,"
				+ " os.MinPRPrice as minPRPrice,"
				+ " os.MaxPRPrice as maxPRPrice,"
				+ " os.OutletTypeId as outletTypeId,"
				+ " os.DeviationSum as deviationSum,"
				+ " os.Variance as variance,"
				+ " os.PRSPriceDeviationSum as PRSPriceDeviationSum,"
				+ " os.PRSPriceVariance as PRSPriceVariance "
				+ " from OutletTypeStatistic os "
				+ " left join OutletType ot on os.OutletTypeId = ot.OutletTypeId"
				+ " left join SubItem s on s.OutletTypeId = os.OutletTypeId" 
				+ " left join Unit u on u.SubItemId = s.SubItemId" 
				+ " left join Purpose pp on pp.PurposeId = u.PurposeId "
				+ " where os.ReferenceMonth between :period and :period ";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		if (unitId != null && unitId.size() > 0){
			sql += " and u.unitId in (:unitId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		sql += " order by os.referenceMonth , u.cpiBasePeriod , ot.code ";
		
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
		
		query.addScalar("outletTypeStatisticId",StandardBasicTypes.STRING);
		query.addScalar("outletTypeCode",StandardBasicTypes.STRING);
		query.addScalar("outletTypeChinName",StandardBasicTypes.STRING);
		query.addScalar("outletTypeEngName",StandardBasicTypes.STRING);
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
		query.addScalar("outletTypeId",StandardBasicTypes.STRING);
		query.addScalar("deviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("variance",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceDeviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceVariance",StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(SummaryOutletTypeStatistic.class));
		
		return query.list();
	}
}
