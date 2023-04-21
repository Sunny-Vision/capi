package com.kinetix.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import capi.model.SystemConstant;
import capi.service.dataImportExport.DataExportService;
import capi.service.dataImportExport.ImportExportTaskService;

@Component("DataExportTask")
public class DataExportTask {

	private static final Logger logger = LoggerFactory.getLogger(DataExportTask.class);
	
	@Autowired(required=false)
	private List<DataExportService> services;
	@Autowired
	private ImportExportTaskService importService;
	
	
	@Async
	public void dataExportAsync(Integer taskId, Integer taskNo){
		try{
			importService.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_IN_PROGRESS);
			boolean found = false;
			if (services != null){				
				for (DataExportService service: services){
					if (service.getTaskNo() == taskNo){
				        service.runTask(taskId);
						found = true;
						break;
					}
				}				
			}
			
			if (!found){
				importService.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FAILED, "Export type has not defined");
				return;
			}
			else{
				importService.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FINISHED);
			}
			
		}
		catch(Exception ex){
			logger.error("data export async error", ex);
			importService.updateTaskStatus(taskId, SystemConstant.TASK_STATUS_FAILED, ex.getMessage());
		}
		houseKeepTask();
	}
	
	private void houseKeepTask(){
		try{
			importService.houseKeepExportTask();
		}
		catch(Exception ex){
			logger.error("house Keep Export task fail", ex);
		}
	}
}
