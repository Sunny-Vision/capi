package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import capi.entity.SectionStatistic;
import capi.model.report.SummarySectionStatistic;

@Repository("SectionStatisticDao")
public class SectionStatisticDao  extends GenericDao<SectionStatistic>{

	public void calculateSectionStatistic(Date referenceMonth){
		calculateSectionStatistic(referenceMonth, false);
	}

	public void calculateSectionStatistic(Date referenceMonth, boolean onlyNotCalculated){
		String sql = "";
		if (onlyNotCalculated){	
			sql = "exec [calculateSectionStatistic] :refMonth, 1";
		}
		else{
			sql = "exec [calculateSectionStatistic] :refMonth";
		}
		Query query = this.getSession().createSQLQuery(sql);
		query.setParameter("refMonth", referenceMonth);
		query.executeUpdate();
	}
	
	public List<SummarySectionStatistic> getSummarySectionStatistic(List<Integer> purpose, List<Integer> unitId, List<Integer> cpiSurveyForm, Date period) {
		String sql = "select distinct ss.SectionStatisticId as sectionStatisticId,"
				+ " st.Code as sectionCode,"
				+ " st.ChineseName as sectionChinName,"
				+ " st.EnglishName as sectionEngName,"
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
				+ " st.sectionId as sectionId,"
				+ " ss.DeviationSum as deviationSum,"
				+ " ss.Variance as variance,"
				+ " ss.PRSPriceDeviationSum as PRSPriceDeviationSum,"
				+ " ss.PRSPriceVariance as PRSPriceVariance"
				+ " from SectionStatistic ss"
				+ " left join Section st on st.SectionId = ss.SectionId"
				+ " left join [Group] g on st.SectionId = g.SectionId"
				+ " left join SubGroup sg on sg.GroupId = g.GroupId "
				+ " left join Item i on i.SubGroupId = sg.SubGroupId "
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
		
		sql += " order by ss.referenceMonth , u.cpiBasePeriod , st.code ";
			
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
		
		query.addScalar("sectionStatisticId",StandardBasicTypes.STRING);
		query.addScalar("sectionCode",StandardBasicTypes.STRING);
		query.addScalar("sectionChinName",StandardBasicTypes.STRING);
		query.addScalar("sectionEngName",StandardBasicTypes.STRING);
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
		query.addScalar("sectionId",StandardBasicTypes.STRING);
		query.addScalar("deviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("variance",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceDeviationSum",StandardBasicTypes.DOUBLE);
		query.addScalar("PRSPriceVariance",StandardBasicTypes.DOUBLE);
		query.setResultTransformer(Transformers.aliasToBean(SummarySectionStatistic.class));
		
		return query.list();
	}
}
