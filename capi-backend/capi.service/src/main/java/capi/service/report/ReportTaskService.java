package capi.service.report;

import java.io.File;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import capi.dal.NotificationDao;
import capi.dal.ReportTaskDao;
import capi.dal.SystemFunctionDao;
import capi.entity.Notification;
import capi.entity.ReportTask;
import capi.entity.SystemFunction;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.report.ReportTaskList;
import capi.service.AppConfigService;
import capi.service.BaseService;

@Service("ReportTaskService")
public class ReportTaskService extends BaseService{

	@Autowired
	private ReportTaskDao reportTaskDao;
	
	@Autowired
	private AppConfigService config;
	
	@Autowired
	private AppConfigService appConfig;
	
	@Autowired
	private NotificationDao notifcationDao;
	
	@Autowired
	private SystemFunctionDao systemFuncDao;

	@Transactional
	public ReportTask updateTaskStatus(Integer taskId, String status){
		return this.updateTaskStatus(taskId, status, null);
	}
	
	@Transactional
	public ReportTask updateTaskStatus(Integer taskId, String status, String exception){
		ReportTask task = reportTaskDao.findById(taskId);
		task.setStatus(status);
		task.setExceptionMessage(exception);
		reportTaskDao.save(task);
		
		if (status.equals(SystemConstant.TASK_STATUS_FINISHED)){
			String funcCode = task.getFunctionCode();
			SystemFunction sysFunc = systemFuncDao.getFunctionByCode(funcCode);
			String desc = sysFunc.getDescription();
			Notification notification = new Notification();
			notification.setSubject(String.format("Report \"%s\" has been generated", desc));
			notification.setContent(String.format("Report \"%s\" has been generated with the following criteria: \n %s", desc, task.getDescription()));
			notification.setUser(task.getUser());
			notifcationDao.save(notification);
		}
		else if (status.equals(SystemConstant.TASK_STATUS_FAILED)){
			String funcCode = task.getFunctionCode();
			SystemFunction sysFunc = systemFuncDao.getFunctionByCode(funcCode);
			String desc = sysFunc.getDescription();
			Notification notification = new Notification();
			notification.setSubject(String.format("The generation of Report \"%s\" is failed", desc));
			notification.setContent(String.format("The generation of Report \"%s\" is failed with the following criteria: \n %s", desc, task.getDescription()));
			notification.setUser(task.getUser());
			notifcationDao.save(notification);
		}
		
		reportTaskDao.flush();
		return task;
	}
	
	public DatatableResponseModel<ReportTaskList> queryReportTask(DatatableRequestModel requestModel, Integer userId, String funcCode){
		
		Order order = this.getOrder(requestModel, "r.createdDate", "description", "createdBy", "status");
		
		String search = requestModel.getSearch().get("value");
		
		List<ReportTaskList> result = reportTaskDao.searchReportTaskList(search, requestModel.getStart(), requestModel.getLength(), order, userId, funcCode);

		
		DatatableResponseModel<ReportTaskList> response = new DatatableResponseModel<ReportTaskList>();
		response.setDraw(requestModel.getDraw());
		response.setData(result);
		Long recordTotal = reportTaskDao.countReportTask("", userId, funcCode);
		response.setRecordsTotal(recordTotal.intValue());
		Long recordFiltered = reportTaskDao.countReportTask(search, userId, funcCode);
		response.setRecordsFiltered(recordFiltered.intValue());
		
		return response;
		
	}
	
	@Transactional
	public void houseKeepReportTask(String funcCode){
		List<ReportTask> list = reportTaskDao.getHouseKeepTask(funcCode);
		if (list != null && list.size() > 0){
			for(ReportTask task : list){
				File file = new File(appConfig.getImportFileLoc() + task.getPath());
				if (file.exists()){
					file.delete();
				}
				reportTaskDao.delete(task);
			}
		}
		reportTaskDao.flush();
	}
	
	public File getExportFileById(Integer id){
		ReportTask task = reportTaskDao.findById(id);
		File file = new File(config.getReportLocation() + task.getPath());
		return file;		
	}
	
	public ReportTask getReportTaskById(Integer id){
		ReportTask task = reportTaskDao.findById(id);
		return task;
	}
	
	public String getContentType(Integer reportType){
		switch(reportType){
			case ReportServiceBase.DOCX: return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			case ReportServiceBase.PDF: return "application/pdf";
			case ReportServiceBase.XLSX: return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			case ReportServiceBase.ZIP: return "application/zip";
		}		
		return "";
	}

	public ReportTask getLatestReportTask(String functionCode) {
		return reportTaskDao.getPreviousReportTask(functionCode);
	}

	public SystemFunction getSystemFunctionByCode(String code) {
		return systemFuncDao.getFunctionByCode(code);
	}
	
}
