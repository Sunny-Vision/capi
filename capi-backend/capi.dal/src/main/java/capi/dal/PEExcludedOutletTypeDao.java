package capi.dal;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.PEExcludedOutletType;

@Repository("PEExcludedOutletTypeDao")
public class PEExcludedOutletTypeDao  extends GenericDao<PEExcludedOutletType>{
	
	public List<PEExcludedOutletType> getExcludedPEExcludedOutletType(List<String> ids ){
		Criteria criteria = this.createCriteria("peeot").createAlias("outletType", "ot");
		criteria.setFetchMode("outletType", FetchMode.JOIN);
		if (ids != null && ids.size() > 0)
			criteria.add(Restrictions.not(Restrictions.in("ot.shortCode", ids)));
		return criteria.list();
	}
	
	public List<PEExcludedOutletType> getPEExcludedOutletType(List<String> ids ){
		Criteria criteria = this.createCriteria("peeot").createAlias("outletType", "ot");		
		criteria.setFetchMode("outletType", FetchMode.JOIN);
		if (ids != null && ids.size() > 0)
			criteria.add((Restrictions.in("ot.shortCode", ids)));
		return criteria.list();
	}

	public List<PEExcludedOutletType> getPEExcludedOutletType(){
		Criteria criteria = this.createCriteria("peeot").createAlias("outletType", "ot");
		criteria.setFetchMode("outletType", FetchMode.JOIN);
		return criteria.list();
	}
		
}
