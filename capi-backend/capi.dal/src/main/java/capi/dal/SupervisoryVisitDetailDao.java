package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import capi.entity.SupervisoryVisitDetail;
import capi.model.api.dataSync.SupervisoryVisitDetailSyncData;

@Repository("SupervisoryVisitDetailDao")
public class SupervisoryVisitDetailDao  extends GenericDao<SupervisoryVisitDetail>{

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitDetail> getByIds(Integer[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("supervisoryVisitDetailId", ids)).list();
	}

	@SuppressWarnings("unchecked")
	public List<SupervisoryVisitDetail> getSVDetailsBySVFormId(Integer supervisoryVisitFormId, List<String> survey) {
		Criteria criteria = this.createCriteria("svd");
		
		Conjunction conjunction = Restrictions.conjunction();
		
		conjunction.add(Restrictions.eq("svd.supervisoryVisitForm.supervisoryVisitFormId", supervisoryVisitFormId));
		
		if(survey != null && survey.size() > 0) {
			conjunction.add(Restrictions.in("svd.survey", survey));
		}
		
		criteria.add(conjunction);
		
		return criteria.list();
	}
	
	public List<SupervisoryVisitDetailSyncData> getUpdatedSupervisoryVisitDetail(Date lastSyncTime, Integer[] supervisoryVisitFormIds, Integer[] supervisoryVisitDetailIds){
		String hql = "select sd.supervisoryVisitDetailId as supervisoryVisitDetailId"
				+ ", a.assignmentId as assignmentId, sd.survey as survey"
				+ ", sd.result as result, sd.createdDate as createdDate"
				+ ", sd.modifiedDate as modifiedDate, sv.supervisoryVisitFormId as supervisoryVisitFormId"
				+ ", sd.otherRemark as otherRemark, sd.referenceNo as referenceNo"
				+ " from SupervisoryVisitDetail as sd"
				+ " left join sd.assignment as a"
				+ " left join sd.supervisoryVisitForm as sv"
				+ " where 1=1";
		
		if(lastSyncTime!=null){
			hql += " and sd.modifiedDate >= :modifiedDate";
		}
		
		if(supervisoryVisitFormIds!=null && supervisoryVisitFormIds.length>0){
			hql += " and sv.supervisoryVisitFormId in ( :supervisoryVisitFormIds )";
		}
		
		if(supervisoryVisitDetailIds!=null && supervisoryVisitDetailIds.length>0){
			hql += " and sd.supervisoryVisitDetailId in ( :supervisoryVisitDetailIds )";
		}
		
		Query query = this.getSession().createQuery(hql);
		if(lastSyncTime!=null)
			query.setParameter("modifiedDate", lastSyncTime);
		if(supervisoryVisitFormIds!=null && supervisoryVisitFormIds.length>0)
			query.setParameterList("supervisoryVisitFormIds", supervisoryVisitFormIds);
		if(supervisoryVisitDetailIds!=null && supervisoryVisitDetailIds.length>0){
			query.setParameterList("supervisoryVisitDetailIds", supervisoryVisitDetailIds);
		}
		query.setResultTransformer(Transformers.aliasToBean(SupervisoryVisitDetailSyncData.class));
		return query.list();
	}
	
	public List<SupervisoryVisitDetail> getSupervisoryVisitDetailByAssingment(Integer assignmentId){
		Criteria criteria = this.createCriteria("svd");
		criteria.add(Restrictions.eq("svd.assignment.assignmentId", assignmentId));
		return criteria.list();
	}
}
