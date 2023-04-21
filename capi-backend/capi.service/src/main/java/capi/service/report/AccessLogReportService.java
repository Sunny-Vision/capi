package capi.service.report;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.ReportTaskDao;
import capi.dal.UserDao;
import capi.entity.ReportTask;
import capi.entity.User;
import capi.model.SystemConstant;
import capi.model.report.AccessLogCriteria;
import capi.model.report.AccessLogModel;
import capi.service.CommonService;


@Service("AccessLogReportService")
public class AccessLogReportService extends ReportServiceBase {
	
	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CommonService commonService;

	@Override
	public void generateReport(Integer taskId) throws Exception {
		ReportTask task = reportTaskDao.findById(taskId);
		if (StringUtils.isEmpty(task.getCriteriaSerialize())) {
			throw new RuntimeException("Criteria does not defined");
		}
		
		AccessLogCriteria criteria = this.deserializeObject(task.getCriteriaSerialize(), AccessLogCriteria.class);
		
		Calendar calendar = Calendar.getInstance();

		// Set the Start Time of fromDate
		Date fromDate = commonService.getDate(criteria.getFromDate());
		calendar.setTime(fromDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date fromDateWithStartTime = calendar.getTime();
		calendar.clear();
		
		Date toDate = commonService.getDate(criteria.getToDate());
		calendar.setTime(toDate);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date toDateWithEndTime = calendar.getTime();
		
		List<AccessLogModel> resultSet = reportTaskDao.getAccessLog(fromDateWithStartTime, toDateWithEndTime);
		
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		
		parameters.put("period", String.format("From %s To %s", criteria.getFromDate(), criteria.getToDate()));
		
		String path = this.exportReport(task, "AccessLog", resultSet, parameters);
		task.setPath(path);
		reportTaskDao.save(task);
		reportTaskDao.flush();
	}

	@Override
	public String getFunctionCode() {
		return "UF9051";
	}

	@Override
	public ReportTask createReportTask(Object criteriaObject, Integer taskType, Integer userId) throws Exception {
		AccessLogCriteria criteria = (AccessLogCriteria) criteriaObject;
		ReportTask task = new ReportTask();
		String serialize = this.serializeObject(criteria);
		StringBuilder descBuilder = new StringBuilder();
		
		descBuilder.append(String.format("Period: %s - %s", criteria.getFromDate(), criteria.getToDate()));
		
		if (taskType == ReportServiceBase.PDF) {
			descBuilder.append("\n");
			descBuilder.append("Export Type: PDF");
		} else {
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
