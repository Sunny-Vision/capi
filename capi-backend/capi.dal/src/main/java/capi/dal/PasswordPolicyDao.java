package capi.dal;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.PasswordPolicy;
import capi.model.PasswordPolicyConstant;

@Repository("PasswordPolicyDao")
public class PasswordPolicyDao extends GenericDao<PasswordPolicy>{
	
	public PasswordPolicy findByName(String name) {
		return (PasswordPolicy)this.createCriteria()
				.add(Restrictions.and(
					Restrictions.eq("name", name)
				)).uniqueResult();
	}
	
	
}
