package capi.dal;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.User;
import capi.entity.UserLockedTimeLog;

@Repository("UserLockedTimeLogDao")
public class UserLockedTimeLogDao extends GenericDao<UserLockedTimeLog> {

	public UserLockedTimeLog findUserLockedTimeLogByUsername(String username){
		return (UserLockedTimeLog)this.createCriteria()
				.add(Restrictions.eq("username", username)).uniqueResult();
		
	}
}
