package capi.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.SystemConfiguration;
import capi.model.api.dataSync.BusinessParameterSyncData;

@Repository("SystemConfigurationDao")
public class SystemConfigurationDao  extends GenericDao<SystemConfiguration>{
	
	public SystemConfiguration findByName(String name) {
		return (SystemConfiguration)this.createCriteria()
				.add(Restrictions.and(
					Restrictions.eq("name", name)
				)).uniqueResult();
	}
	
	public List<BusinessParameterSyncData> getUpdateBusinessParameter(Date lastSyncTime){
		Criteria criteria = this.createCriteria();
		List<String> names = new ArrayList<String>();
		names.add("DiscountText01");
		names.add("DiscountText02");
		names.add("DiscountText03");
		names.add("DiscountText04");
		names.add("DiscountText05");
		names.add("DiscountText06");
		names.add("DiscountText07");
		names.add("DiscountText08");
		names.add("DiscountText09");
		names.add("DiscountText10");
		names.add("DiscountText11");
		names.add("DiscountText12");
		names.add("MobileSynchronizationPeriod");
		names.add("CountryOfOrigin");
		names.add("itineraryNoofAssignmentDeviation");
		names.add("itineraryNoofAssignmentDeviationPlus");
		names.add("itineraryNoofAssignmentDeviationMinus");
		names.add("itinerarySequenceDeviation");
		names.add("itinerarySequencePercents");
		names.add("itineraryTPUSequenceDeviation");
		names.add("itineraryTPUSequenceDeviationTimes");
		names.add("onSpotValidationMessage1");
		names.add("onSpotValidationMessage2");
		names.add("onSpotValidationMessage3");
		names.add("onSpotValidationMessage4");
		names.add("onSpotValidationMessage5");
		names.add("onSpotValidationMessage6");
		names.add("onSpotValidationMessage7");
		names.add("onSpotValidationMessage8");
		names.add("onSpotValidationMessage9");
		names.add("onSpotValidationMessage10");
		names.add("onSpotValidationMessage11");
		names.add("onSpotValidationMessage12");
		names.add("onSpotValidationMessage13");
		names.add("onSpotValidationMessage14");
		names.add("onSpotValidationMessage15");
		names.add("onSpotValidationMessage16");
		names.add("onSpotValidationMessage17");
		names.add("onSpotValidationMessage18");
		names.add("SummerEndDate");
		names.add("SummerStartDate");
		names.add("WinterEndDate");
		names.add("WinterStartDate");
		
		criteria.add(Restrictions.and(
				Restrictions.ge("modifiedDate", lastSyncTime), 
				Restrictions.in("name", names)
				));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("systemConfigurationId"), "systemConfigurationId");
		projList.add(Projections.property("name"), "name");
		projList.add(Projections.property("value"), "value");
		projList.add(Projections.property("createdDate"), "createdDate");
		projList.add(Projections.property("modifiedDate"), "modifiedDate");
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(BusinessParameterSyncData.class));
		
		return criteria.list();
	}
}

