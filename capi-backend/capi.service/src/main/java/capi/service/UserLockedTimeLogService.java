package capi.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.UserLockedTimeLogDao;
import capi.entity.UserLockedTimeLog;
import capi.dal.UserDao;

@Service("UserLockedTimeLogService")
public class UserLockedTimeLogService {
	
	@Autowired
	private UserLockedTimeLogDao userLockedTimeLogDao;
	
	public UserLockedTimeLog getUserLockedTimeLogByUserName(String username){
		return userLockedTimeLogDao.findUserLockedTimeLogByUsername(username);
	}
	
	public void saveUserLockedTime(String username){
		System.out.println("==============UserLockedTimeLogService=====saveUserLockedTime");
		
		UserLockedTimeLog oldEntity = getUserLockedTimeLogByUserName(username);
		if (oldEntity != null) {
			oldEntity.setLockedDate(new Date());
			userLockedTimeLogDao.save(oldEntity);
		} else {
			UserLockedTimeLog model = new UserLockedTimeLog();
			model.setUsername(username);
			model.setLockedDate(new Date());
			userLockedTimeLogDao.save(model);
		}
		userLockedTimeLogDao.flush();

	}
	
	/*
	public void saveUserLastAttemptTime(String username){
		System.out.println("==============UserLockedTimeLogService=====saveUserLateAttemptTime");
		
		UserLockedTimeLog oldEntity = getUserLockedTimeLogByUserName(username);
		if (oldEntity != null) {
			oldEntity.setLastAttemptDate(new Date());
			userLockedTimeLogDao.save(oldEntity);
		} else {
			UserLockedTimeLog model = new UserLockedTimeLog();
			model.setUsername(username);
			model.setLastAttemptDate(new Date());
			userLockedTimeLogDao.save(model);
		}
		userLockedTimeLogDao.flush();

	}
	*/
	
}
