package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.PECheckUnitCriteria;

@Repository("PECheckUnitCriteriaDao")
public class PECheckUnitCriteriaDao extends GenericDao<PECheckUnitCriteria> {
	
	public List<PECheckUnitCriteria> getExcludedPECheckUnitCriteria(List<Integer> ids ){
		Criteria criteria = this.createCriteria("pecuc");
		
		if (ids != null && ids.size() > 0){
			criteria.add(Restrictions.not(Restrictions.in("pecuc.peCheckUnitCriteriaId", ids)));
		}
				
		return criteria.list();
	}
}
