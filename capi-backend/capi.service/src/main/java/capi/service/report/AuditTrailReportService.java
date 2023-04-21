package capi.service.report;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.AuditLogCriteria;
import capi.model.report.AuditLogModel;
import capi.service.CommonService;

@Service("AuditTrailReportService")
public class AuditTrailReportService extends ReportServiceBase{
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private CommonService commonService;
	

	@Override
	public void generateReport(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())){
			throw new RuntimeException("Criteria not defined");
		}
		
		AuditLogCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), AuditLogCriteria.class);
		
		Date startDate = commonService.getDate(criteria.getFromDate());
		Date endDate = commonService.getDate(criteria.getToDate());
		
		List<AuditLogModel> progress = reportTaskDao.getAuditLog(startDate, endDate);
				
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		
		parameters.put("period", criteria.getFromDate() + " - " + criteria.getToDate());
		
		
		String path = this.exportReport(task, "AuditTrail", progress, parameters);
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public String getFunctionCode() {
		// TODO Auto-generated method stub
		return "UF9001";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		// TODO Auto-generated method stub
		AuditLogCriteria criteria = (AuditLogCriteria)criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		
		//descBuilder.append("\n");
		descBuilder.append(String.format("Period: %s - %s", criteria.getFromDate(), criteria.getToDate()));
		
		if(taskType == ReportServiceBase.PDF){
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		}
		else{
			descBuilder.append("\n");
			descBuilder.append("Export Type: XLSX");
		}
		
		User creator = userDao.findById(userId);
		
		task.setCriteriaSerialize(serialize);
		task.setFunctionCode(this.getFunctionCode());
		task.setReportType(taskType);
		task.setDescription(descBuilder.toString());
		task.setStatus(SystemConstant.TASK_STATUS_PENDING);
		task.setUser(creator);
		
		reportTaskDao.save(task);
		reportTaskDao.flush();
		return task;		
	}

}
