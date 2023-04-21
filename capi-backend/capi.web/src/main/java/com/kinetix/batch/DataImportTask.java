package com.kinetix.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import capi.model.SystemConstant;
import capi.service.dataImportExport.DataImportService;
import capi.service.dataImportExport.ImportExportTaskService;

@Component("DataImportTask")
public class DataImportTask {

	private static final Logger logger = LoggerFactory.getLogger(DataImportTask.class);
	
	@Autowired(required=false)
	private List<DataImportService> services;
	@Autowired
	private ImportExportTaskService importService;
	
	
	@Async
	public void dataImportAsync(Integer taskId, Integer taskNo){
		try{
			importService.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_IN_PROGRESS);
			boolean found = false;
			if (services != null){				
				for (DataImportService service: services){
					if (service.getTaskNo() == taskNo){
						service.runTask(taskId);
						found = true;
						break;
					}
				}				
			}
			
			if (!found){
				importService.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FAILED, "Import type has not defined");
			}
			else{
				importService.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FINISHED);
			}
			
		}
		catch(Exception ex){
			logger.error("data import async error", ex);
			importService.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FAILED, ex.getMessage());
		}
	}
	
	
}
