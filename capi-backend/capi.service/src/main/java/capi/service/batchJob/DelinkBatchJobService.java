package capi.service.batchJob;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import capi.dal.DelinkTaskDao;
import capi.entity.DelinkTask;
import capi.service.CommonService;
import capi.service.NotificationService;

/**
 * Check if there is pending delink task
 * @author stanley_tsang
 *
 */
@Service("DelinkBatchJobService")
public class DelinkBatchJobService  implements BatchJobService{
	
	@Autowired
	private DelinkTaskDao delinkTaskDao;
	
	@Autowired
	private NotificationService notifyService;
	
	@Resource(name="messageSource")
	private MessageSource messageSource;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public String getJobName() {
		// TODO Auto-generated method stub
		return "Delink";
	}

	@Override
	public void runTask() throws Exception {
		// TODO Auto-generated method stub
		List<DelinkTask> tasks = delinkTaskDao.getPendingDelinkTask();
		if (tasks != null && tasks.size() > 0){
			for (DelinkTask task : tasks){
				delinkTaskDao.delink(task.getReferenceMonth());
				task.setRun(true);
				
				String month = commonService.formatMonth(task.getReferenceMonth());
				
				String subject = messageSource.getMessage("N00060", null, Locale.ENGLISH);
				String content = messageSource.getMessage("N00061", new Object[]{month}, Locale.ENGLISH);
				notifyService.sendNotification(task.getUser(), subject, content, false);
				
			}
			delinkTaskDao.flushAndClearCache();
		}
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return true;
	}

}
