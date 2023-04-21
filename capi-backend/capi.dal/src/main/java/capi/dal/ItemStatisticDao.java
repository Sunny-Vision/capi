package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.ItemStatistic;
import capi.model.report.SummaryItemStatistic;

@Repository("ItemStatisticDao")
public class ItemStatisticDao extends GenericDao<ItemStatistic>{

	public void calculateItemStatistic(Date referenceMonth){
		calculateItemStatistic(referenceMonth, false);
	}
	

	public void calculateItemStatistic(Date referenceMonth, boolean onlyNotCalculated){
		String sql = "";
		if (onlyNotCalculated){	
			sql = "exec [CalculateItemStatistic] :refMonth, 1";
		}
		else{
			sql = "exec [CalculateItemStatistic] :refMonth";
		}
		Query query = this.getSession().createSQLQuery(sql);
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
	
	public List<SummaryItemStatistic> getSummaryItemStatistic(List<Integer> purpose, List<Integer> unitId, List<Integer> cpiSurveyForm, Date period) {
		String sql = "select distinct it.ItemStatisticId as itemStatisticId,"
				+ " i.Code as itemCode,"
				+ " i.ChineseName as itemChinName,"
				+ " i.EnglishName as itemEngName,"
				+ " u.CPIBasePeriod as cpiBasePeriod,"
				+ " it.ReferenceMonth as referenceMonth,"
				+ " it.SumCurrentSPrice as sumCurrentSPrice,"
				+ " it.CountCurrentSPrice as countCurrentSPrice,"
				+ " it.AverageCurrentSPrice as averageCurrentSPrice,"
				+ " it.SumLastSPrice as sumLastSPrice,"
				+ " it.CountLastSPrice as countLastSPrice,"
				+ " it.AverageLastSPrice as averageLastSPrice,"
				+ " it.FinalPRSPrice as finalPRSPrice,"
				+ " it.StandardDeviationSPrice as  standardDeviationSPrice,"
				+ " it.MedianSPrice as medianSPrice,"
				+ " it.MinSPrice as minSPrice,"
				+ " it.MaxSPrice as maxSPrice,"
				+ " it.LastHasPriceSumCurrentSPrice as lastHasPriceSumCurrentSPrice,"
				+ " it.LastHasPriceCountCurrentSPrice as lastHasPriceCountCurrentSPrice,"
				+ " it.LastHasPriceAverageCurrentSPrice as lastHasPriceAverageCurrentSPrice,"
				+ " it.LastHasPriceReferenceMonth as lastHasPriceReferenceMonth,"
				+ " it.AveragePRSPrice as averagePRSPrice,"
				+ " it.CountPRSPrice as countPRSPrice,"
				+ " it.SumPRSPrice as sumPRSPrice,"
				+ " it.StandardDeviationPRSPrice as standardDeviationPRSPrice,"
				+ " it.MedianPRPrice as medianPRPrice,"
				+ " it.MinPRPrice as minPRPrice,"
				+ " it.MaxPRPrice as maxPRPrice,"
				+ " i.ItemId as itemId,"
				+ " it.DeviationSum as deviationSum,"
				+ " it.Variance as variance,"
				+ " it.PRSPriceDeviationSum as PRSPriceDeviationSum,"
				+ " it.PRSPriceVariance as PRSPriceVariance"
				+ " from ItemStatistic it "
				+ " left join Item i on i.ItemId = it.ItemId "
				+ " left join OutletType ot on ot.ItemId = i.ItemId "
				+ " left join SubItem s on s.OutletTypeId = ot.OutletTypeId "
				+ " left join Unit u on s.SubItemId = u.SubItemId "
				+ " left join Purpose pp on u.PurposeId = pp.PurposeId"
				+ " where it.ReferenceMonth between :period and :period ";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		if (unitId != null && unitId.size() > 0){
			sql += " and u.unitId in (:unitId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		sql += " order by it.referenceMonth , u.cpiBasePeriod , i.code ";
			
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
		
		query.addScalar("itemStatisticId",StandardBasicTypes.STRING);
		query.addScalar("itemCode",StandardBasicTypes.STRING);
		query.addScalar("itemChinName",StandardBasicTypes.STRING);
		query.addScalar("itemEngName",StandardBasicTypes.STRING);
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
		query.addScalar("itemId",StandardBasicTypes.STRING);
		query.addScalar("deviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("variance",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceDeviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceVariance",StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(SummaryItemStatistic.class));
		
		return query.list();
	}
}
