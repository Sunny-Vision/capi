package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.PETopManagement;

@Repository("PETopManagementDao")
public class PETopManagementDao   extends GenericDao<PETopManagement>{

	public List<PETopManagement> getBySurveyMonth(Integer surveyMonthId){
		Criteria criteria = this.createCriteria("pe")
				.createAlias("pe.allocationBatch", "ab")
				.createAlias("ab.surveyMonth", "s");
		criteria.add(Restrictions.eq("s.surveyMonthId", surveyMonthId));
		
		return criteria.list();
	}
	
	
	public List<PETopManagement> getByReferenceMonth(Date referenceMonth){
		Criteria criteria = this.createCriteria("pe")
				.createAlias("pe.allocationBatch", "ab")
				.createAlias("ab.surveyMonth", "s");
		criteria.add(Restrictions.eq("s.referenceMonth", referenceMonth));
		
		return criteria.list();
	}
	
	public List<PETopManagement> getSectionHeadTaskByReferenceMonth(Date referenceMonth){
		Criteria criteria = this.createCriteria("pe")
				.createAlias("pe.allocationBatch", "ab")
				.createAlias("ab.surveyMonth", "s");
		criteria.add(Restrictions.eq("s.referenceMonth", referenceMonth));
		criteria.add(Restrictions.eq("pe.isSectionHead", true));
		return criteria.list();
	}
	
	
	public PETopManagement getByRefernceMonthUserId(Integer userId, Date referenceMonth){
		Criteria criteria = this.createCriteria("pe")
				.createAlias("pe.allocationBatch", "ab")
				.createAlias("ab.surveyMonth", "s");
		
		criteria.add(Restrictions.eq("pe.user.userId", userId));
		criteria.add(Restrictions.eq("s.referenceMonth", referenceMonth));
		
		criteria.setMaxResults(1);
		
		return (PETopManagement)criteria.uniqueResult();
		
	}
	
}
