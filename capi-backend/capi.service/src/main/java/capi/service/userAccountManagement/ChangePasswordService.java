package capi.service.userAccountManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capi.dal.PasswordHistoryDao;
import capi.dal.UserDao;
import capi.entity.PasswordHistory;
import capi.entity.User;
import capi.model.userAccountManagement.ChangePasswordEditModel;
import capi.service.BaseService;

@Service("ChangePasswordService")
public class ChangePasswordService extends BaseService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PasswordHistoryService passwordHistoryService;
	/**
	 * Get by ID
	 */
	public User getUserById(int id) {
		return userDao.findById(id);
	}

	/**
	 * Save
	 */
	@Transactional
	public String savePassword(ChangePasswordEditModel model, String userName) throws Exception {

		ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);

		String hashPass = encoder.encodePassword(model.getOldPassword(), null);
		String newHashPass = encoder.encodePassword(model.getNewPassword(), null);

		User user = userDao.getUserByUserNameAndPw(userName, hashPass);
		if(user != null) {
			
			//TODO CR6 REQ006
			if (passwordHistoryService.inPasswordMinAge(user)) {
				return "PasswordMinimumAge";
			}
			
			//TODO CR6 REQ004
			if (passwordHistoryService.isPasswordEnforced(user, newHashPass)) {
				return "EnforcedPassword";
			}
			
			user.setPassword(newHashPass);
			
			userDao.save(user);
			userDao.flush();
			
			//TODO CR6 REQ004
			passwordHistoryService.savePasswordHistory(user, newHashPass, true);
			
			return "Success";
		}
		return "Fail";
	}

}
