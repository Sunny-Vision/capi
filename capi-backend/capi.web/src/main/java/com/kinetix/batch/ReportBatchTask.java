package com.kinetix.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import capi.entity.ReportTask;
import capi.model.SystemConstant;
import capi.service.report.ReportService;
import capi.service.report.ReportTaskService;

@Component("ReportBatchTask")
public class ReportBatchTask {

	private static final Logger logger = LoggerFactory.getLogger(ReportBatchTask.class);
	
	@Autowired(required=false)
	private List<ReportService> services;
	
	@Autowired
	private ReportTaskService service;
	
	@Async
	public void generateReport(Integer taskId){
		try{
			ReportTask task = service.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_IN_PROGRESS);
			boolean found = false;
			if (services != null){				
				for (ReportService reportService: services){
					if (reportService.getFunctionCode().equals(task.getFunctionCode())){
						reportService.generateReport(taskId);
						found = true;
						break;
					}
				}				
			}
			
			if (!found){
				service.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FAILED, "report type has not defined");
			}			
			else{
				service.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FINISHED);				
			}
			
			houseKeepTaskList(task.getFunctionCode());
		}
		catch(Exception ex){
			logger.error("Generate report async error", ex);
			service.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FAILED, ex.getMessage());
		}
		
	}
	
	
	public void houseKeepTaskList(String funcCode){
		try{
			service.houseKeepReportTask(funcCode);
		}
		catch(Exception ex){
			logger.error("fail to house keep report", ex);
		}
	}
	
}
