package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import capi.entity.VwSurveyFormBinded;

@Repository("VwSurveyFormBindedDao")
public class VwSurveyFormBindedDao extends GenericDao<VwSurveyFormBinded>{
	@SuppressWarnings("unchecked")
	public List<VwSurveyFormBinded> getAll() {
		return this.createCriteria().addOrder(Order.asc("surveyForm")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<VwSurveyFormBinded> getByIds(String[] ids) {
		return this.createCriteria()
				.add(Restrictions.in("id", ids)).list();
	}
	
	public long countSurveyForm(String search) {
		Criteria criteria = this.createCriteria();

		if (!StringUtils.isEmpty(search)) {
			criteria.add(Restrictions.or(
					Restrictions.like("surveyForm", "%" + search + "%")));
		}
		
		return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
}
