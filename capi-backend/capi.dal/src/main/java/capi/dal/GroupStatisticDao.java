package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.GroupStatistic;
import capi.model.report.SummaryGroupStatistic;

@Repository("GroupStatisticDao")
public class GroupStatisticDao extends GenericDao<GroupStatistic>{

	public void calculateGroupStatistic(Date referenceMonth){
		calculateGroupStatistic(referenceMonth, false);
	}
	

	public void calculateGroupStatistic(Date referenceMonth, boolean onlyNotCalculated){
		String sql = "";
		if (onlyNotCalculated){	
			sql = "exec [CalculateGroupStatistic] :refMonth, 1";
		}
		else{
			sql = "exec [CalculateGroupStatistic] :refMonth";
		}
		Query query = this.getSession().createSQLQuery(sql);
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
	
	public List<SummaryGroupStatistic> getSummaryGroupStatistic(List<Integer> purpose, List<Integer> unitId, List<Integer> cpiSurveyForm, Date period) {
		String sql = "select distinct gs.GroupStatisticId as groupStatisticId,"
				+ " g.Code as groupCode,"
				+ " g.ChineseName as groupChinName,"
				+ " g.EnglishName as groupEngName,"
				+ " u.CPIBasePeriod as cpiBasePeriod,"
				+ " gs.ReferenceMonth as referenceMonth,"
				+ " gs.SumCurrentSPrice as sumCurrentSPrice,"
				+ " gs.CountCurrentSPrice as countCurrentSPrice,"
				+ " gs.AverageCurrentSPrice as averageCurrentSPrice,"
				+ " gs.SumLastSPrice as sumLastSPrice,"
				+ " gs.CountLastSPrice as countLastSPrice,"
				+ " gs.AverageLastSPrice as averageLastSPrice,"
				+ " gs.FinalPRSPrice as finalPRSPrice,"
				+ " gs.StandardDeviationSPrice as  standardDeviationSPrice,"
				+ " gs.MedianSPrice as medianSPrice,"
				+ " gs.MinSPrice as minSPrice,"
				+ " gs.MaxSPrice as maxSPrice,"
				+ " gs.LastHasPriceSumCurrentSPrice as lastHasPriceSumCurrentSPrice,"
				+ " gs.LastHasPriceCountCurrentSPrice as lastHasPriceCountCurrentSPrice,"
				+ " gs.LastHasPriceAverageCurrentSPrice as lastHasPriceAverageCurrentSPrice,"
				+ " gs.LastHasPriceReferenceMonth as lastHasPriceReferenceMonth,"
				+ " gs.AveragePRSPrice as averagePRSPrice,"
				+ " gs.CountPRSPrice as countPRSPrice,"
				+ " gs.SumPRSPrice as sumPRSPrice,"
				+ " gs.StandardDeviationPRSPrice as standardDeviationPRSPrice,"
				+ " gs.MedianPRPrice as medianPRPrice,"
				+ " gs.MinPRPrice as minPRPrice,"
				+ " gs.MaxPRPrice as maxPRPrice,"
				+ " g.GroupId as groupId,"
				+ " gs.DeviationSum as deviationSum,"
				+ " gs.Variance as variance,"
				+ " gs.PRSPriceDeviationSum as PRSPriceDeviationSum,"
				+ " gs.PRSPriceVariance as PRSPriceVariance"
				+ " from GroupStatistic gs"
				+ " left join [Group] g on g.GroupId = gs.GroupId "
				+ " left join SubGroup sg on sg.GroupId = gs.GroupId "
				+ " left join Item i on i.SubGroupId = sg.SubGroupId "
				+ " left join OutletType ot on ot.ItemId = i.ItemId "
				+ " left join SubItem s on s.OutletTypeId = ot.OutletTypeId "
				+ " left join Unit u on s.SubItemId = u.SubItemId "
				+ " left join Purpose pp on u.PurposeId = pp.PurposeId"
				+ " where gs.ReferenceMonth between :period and :period ";
		
		if (purpose != null && purpose.size() > 0){
			sql += " and pp.purposeId in (:purpose) ";
		}
		if (unitId != null && unitId.size() > 0){
			sql += " and u.unitId in (:unitId) ";
		}
		if (cpiSurveyForm != null && cpiSurveyForm.size() > 0){
			sql += " and u.cpiQoutationType in (:cpiSurveyForm) ";
		}
		
		sql += " order by gs.referenceMonth , u.cpiBasePeriod , g.code ";
			
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
		
		query.addScalar("groupStatisticId",StandardBasicTypes.STRING);
		query.addScalar("groupCode",StandardBasicTypes.STRING);
		query.addScalar("groupChinName",StandardBasicTypes.STRING);
		query.addScalar("groupEngName",StandardBasicTypes.STRING);
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
		query.addScalar("groupId",StandardBasicTypes.STRING);
		query.addScalar("deviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("variance",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceDeviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceVariance",StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(SummaryGroupStatistic.class));
		
		return query.list();
	}
}
