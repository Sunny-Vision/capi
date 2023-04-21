package capi.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.VwEntityRemoveLogDao;
import capi.entity.VwEntityRemoveLog;

@Service("EntityRemoveLogService")
public class EntityRemoveLogService {

	@Autowired
	private VwEntityRemoveLogDao removeLogDao;
	
	public List<VwEntityRemoveLog> getUpdatedRemoveLogs(Date lastSyncTime){
		return removeLogDao.getUpdatedRemoveLog(lastSyncTime);
	}
	
	
	
}
