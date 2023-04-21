package capi.dal;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import capi.entity.PasswordHistory;
import capi.entity.User;
import capi.model.userAccountManagement.ChangePasswordEditModel;

@Repository("PasswordHistoryDao")
public class PasswordHistoryDao extends GenericDao<PasswordHistory> {
	
	public void savePasswordHistoryByUser(User user, String hashPass){
		System.out.println("===============================PasswordHistoryDao========savePasswordHistoryByUser");
		Date passwordUpdateDate = new Date();
		PasswordHistory oldEntity = getUserLatestPasswordHistory(user);
		if (oldEntity == null) {
			passwordUpdateDate = user.getModifiedDate();
		}
		
		PasswordHistory passwordHistory = new PasswordHistory();
		passwordHistory.setUserId(user.getUserId());
		passwordHistory.setPassword(hashPass);
		passwordHistory.setPasswordUpdatedDate(passwordUpdateDate);
		save(passwordHistory);
		flush();
	}

	public boolean isPasswordEnforced (User user, String newHashPass, int enforcePasswordHistory) {
		
		Criteria critera = this.createCriteria()
				.setFirstResult(0)
				.setMaxResults(enforcePasswordHistory)
				.addOrder(Order.desc("passwordUpdateDate"));
				
		critera.add(Restrictions.eq("userId", user.getUserId()));
				
		List<PasswordHistory> list = critera.list();
		for (PasswordHistory row : list) {
			if (row.getPassword().equals(newHashPass)) {
				return true;
			}
		}
		
		return false;
	}
	
	public PasswordHistory getUserLatestPasswordHistory(User user){
		Criteria critera = this.createCriteria();
		critera.setFirstResult(0)
				.setMaxResults(1)
				.addOrder(Order.desc("createdDate"));
		critera.add(Restrictions.eq("userId",  user.getUserId()));
		
		PasswordHistory result = (PasswordHistory) critera.uniqueResult();
		
		return result;
	}

}
