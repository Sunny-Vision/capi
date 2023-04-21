package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import capi.entity.OnSpotValidation;
import capi.model.api.dataSync.OnSpotValidationSyncData;

@Repository("OnSpotValidationDao")
public class OnSpotValidationDao  extends GenericDao<OnSpotValidation>{
	public List<OnSpotValidationSyncData> getUpdateOnSpotValidation(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.ge("modifiedDate", lastSyncTime));
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.sqlProjection("unitId as unitId", new String[]{"unitId"}, new Type[]{StandardBasicTypes.INTEGER}));
		projList.add(Projections.property("isUom1Reported"), "isUom1Reported");
		projList.add(Projections.property("isUom2GreaterZero"), "isUom2GreaterZero");
		projList.add(Projections.property("isNPriceGreaterZero"), "isNPriceGreaterZero");
		projList.add(Projections.property("isSPriceGreaterZero"), "isSPriceGreaterZero");
		projList.add(Projections.property("provideReasonPRNPrice"), "provideReasonPRNPrice");
		projList.add(Projections.property("prNPriceThreshold"), "prNPriceThreshold");
		projList.add(Projections.property("provideReasonPRSPrice"), "provideReasonPRSPrice");
		projList.add(Projections.property("prSPriceThreshold"), "prSPriceThreshold");
		projList.add(Projections.property("provideReasonSPriceMaxMin"), "provideReasonSPriceMaxMin");
		projList.add(Projections.property("provideReasonNPriceMaxMin"), "provideReasonNPriceMaxMin");
		projList.add(Projections.property("nPriceGreaterSPrice"), "nPriceGreaterSPrice");
		projList.add(Projections.property("provideRemarkForNotSuitablePrice"), "provideRemarkForNotSuitablePrice");
		projList.add(Projections.property("reminderForPricingCycle"), "reminderForPricingCycle");
		projList.add(Projections.property("provideReasonPRSPriceSD"), "provideReasonPRSPriceSD");
		projList.add(Projections.property("provideReasonPRNPriceSD"), "provideReasonPRNPriceSD");
		projList.add(Projections.property("prSPriceSDPositive"), "prSPriceSDPositive");
		projList.add(Projections.property("prSPriceSDNegative"), "prSPriceSDNegative");
		projList.add(Projections.property("prNPriceSDPositive"), "prNPriceSDPositive");
		projList.add(Projections.property("prNPriceSDNegative"), "prNPriceSDNegative");
		projList.add(Projections.property("prSPriceMonth"), "prSPriceMonth");
		projList.add(Projections.property("prNPriceMonth"), "prNPriceMonth");
		projList.add(Projections.property("provideReasonSPriceSD"), "provideReasonSPriceSD");
		projList.add(Projections.property("provideReasonNPriceSD"), "provideReasonNPriceSD");
		projList.add(Projections.property("sPriceSDPositive"), "sPriceSDPositive");
		projList.add(Projections.property("sPriceSDNegative"), "sPriceSDNegative");
		projList.add(Projections.property("nPriceSDPositive"), "nPriceSDPositive");
		projList.add(Projections.property("nPriceSDNegative"), "nPriceSDNegative");
		projList.add(Projections.property("sPriceMonth"), "sPriceMonth");
		projList.add(Projections.property("nPriceMonth"), "nPriceMonth");
		projList.add(Projections.property("provideReasonPRNPriceLower"), "provideReasonPRNPriceLower");
		projList.add(Projections.property("prNPriceLowerThreshold"), "prNPriceLowerThreshold");
		projList.add(Projections.property("provideReasonPRSPriceLower"), "provideReasonPRSPriceLower");
		projList.add(Projections.property("prSPriceLowerThreshold"), "prSPriceLowerThreshold");
		projList.add(Projections.property("provideRemarkForNotAvailableQuotation"), "provideRemarkForNotAvailableQuotation");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(OnSpotValidationSyncData.class));
		return criteria.list();
	}
	
	public ScrollableResults getAllOnSpotValidationResult(String basePeriod){
		Criteria criteria = this.createCriteria("o").createAlias("o.unit", "u");
		criteria.add(Restrictions.eq("u.cpiBasePeriod", basePeriod));
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	public List<OnSpotValidation> getNotExistedOnSpotValidation(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.not(Restrictions.in("unitId", ids)));
		return criteria.list();		
	}
	
	public List<Integer> getExistingOnSpotValidationId(){
		Criteria criteria = this.createCriteria();
		criteria.setProjection(Projections.property("id"));
		return criteria.list();
	}
	
	public List<OnSpotValidation> getByIds(List<Integer> ids){
		Criteria criteria = this.createCriteria();
		criteria.add(Restrictions.in("unitId", ids));
		return criteria.list();
	}
	
	public void insertOnSpotValidationByRebasing(Integer newUnitId, Integer oldUnitId){
		String sql = "INSERT INTO [OnSpotValidation] ("
				+ "[UnitId],[IsUom1Reported],[IsUom2GreaterZero],[IsNPriceGreaterZero],[IsSPriceGreaterZero]"
				+ "	,[ProvideReasonPRNPrice],[PRNPriceThreshold],[ProvideReasonPRSPrice],[PRSPriceThreshold],[ProvideReasonSPriceMaxMin]"
				+ "	,[ProvideReasonNPriceMaxMin],[NPriceGreaterSPrice],[ProvideRemarkForNotSuitablePrice],[ReminderForPricingCycle],[ProvideReasonPRSPriceSD]"
				+ "	,[ProvideReasonPRNPriceSD],[PRSPriceSDPositive],[PRSPriceSDNegative],[PRNPriceSDPositive],[PRNPriceSDNegative]"
				+ " ,[PRSPriceMonth],[PRNPriceMonth],[ProvideReasonSPriceSD],[ProvideReasonNPriceSD],[SPriceSDPositive]"
				+ "	,[SPriceSDNegative],[NPriceSDPositive],[NPriceSDNegative],[SPriceMonth],[NPriceMonth]"
				+ "	,[ProvideReasonPRNPriceLower],[PRNPriceLowerThreshold],[ProvideReasonPRSPriceLower],[PRSPriceLowerThreshold],[ProvideRemarkForNotAvailableQuotation]"
				+ ", [CreatedDate], [ModifiedDate])"
				
				+ " SELECT :newUnitId , [IsUom1Reported],[IsUom2GreaterZero],[IsNPriceGreaterZero],[IsSPriceGreaterZero]"
				+ "	,[ProvideReasonPRNPrice],[PRNPriceThreshold],[ProvideReasonPRSPrice],[PRSPriceThreshold],[ProvideReasonSPriceMaxMin]"
				+ "	,[ProvideReasonNPriceMaxMin],[NPriceGreaterSPrice],[ProvideRemarkForNotSuitablePrice],[ReminderForPricingCycle],[ProvideReasonPRSPriceSD]"
				+ "	,[ProvideReasonPRNPriceSD],[PRSPriceSDPositive],[PRSPriceSDNegative],[PRNPriceSDPositive],[PRNPriceSDNegative]"
				+ " ,[PRSPriceMonth],[PRNPriceMonth],[ProvideReasonSPriceSD],[ProvideReasonNPriceSD],[SPriceSDPositive]"
				+ "	,[SPriceSDNegative],[NPriceSDPositive],[NPriceSDNegative],[SPriceMonth],[NPriceMonth]"
				+ "	,[ProvideReasonPRNPriceLower],[PRNPriceLowerThreshold],[ProvideReasonPRSPriceLower],[PRSPriceLowerThreshold],[ProvideRemarkForNotAvailableQuotation]"
				+ " , getDate(), getDate()"
				+ "  FROM [OnSpotValidation] where UnitId = :oldUnitId ";
		
		SQLQuery query = this.getSession().createSQLQuery(sql);
		query.setParameter("newUnitId", newUnitId);
		query.setParameter("oldUnitId", oldUnitId);
		query.executeUpdate();
	}
}
