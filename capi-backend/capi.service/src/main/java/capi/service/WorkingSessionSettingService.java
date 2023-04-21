package capi.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.WorkingSessionSettingDao;
import capi.model.api.dataSync.WorkingSessionSettingSyncData;
import capi.service.BaseService;

@Service("WorkingSessionSettingService")
public class WorkingSessionSettingService extends BaseService{
	@Autowired
	private WorkingSessionSettingDao workingSessionSettingDao;
	
	public List<WorkingSessionSettingSyncData> getUpdateWorkingSessionSetting(Date lastSyncTime){
		return workingSessionSettingDao.getUpdateWorkingSessionSetting(lastSyncTime);
	}
}