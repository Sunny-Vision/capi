package com.kinetix.controller.report;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kinetix.batch.ReportBatchTask;

import capi.entity.ReportTask;
import capi.entity.SystemFunction;
import capi.model.CapiWebAuthenticationDetails;
import capi.model.DatatableRequestModel;
import capi.model.DatatableResponseModel;
import capi.model.SystemConstant;
import capi.model.report.ReportTaskList;
import capi.service.POIFileService;
import capi.service.report.ReportService;
import capi.service.report.ReportTaskService;

public abstract class ReportBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportBaseController.class);
	
	@Autowired
	private ReportTaskService reportTaskService;
	
	@Autowired
	private ReportBatchTask reportBatchTask;
	
	@Resource(name="messageSource")
	protected MessageSource messageSource;
	
	@Autowired
	private POIFileService poiFileService;
	
	public DatatableResponseModel<ReportTaskList> queryReportTask(DatatableRequestModel requestModel, String funcCode){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CapiWebAuthenticationDetails details = (CapiWebAuthenticationDetails) authentication.getDetails();
		Integer creatorId = details.getUserId();
		
		return reportTaskService.queryReportTask(requestModel, creatorId, funcCode);
	}
	
	
	public boolean downloadFile(Integer id, RedirectAttributes redirectAttributes, Locale locale, HttpServletResponse response){
		try {
			File file = reportTaskService.getExportFileById(id);
			if (file.exists()){
				ReportTask task = reportTaskService.getReportTaskById(id);
				
				SystemFunction systemFunction = reportTaskService.getSystemFunctionByCode(task.getFunctionCode());
				String description = systemFunction.getDescription();
				if(description.contains("/") || description.contains("\"")) {
					description = description.replaceAll("/", "");
					description = description.replaceAll("\"", "");
				}
				String ext = file.getName();
				int pos = ext.lastIndexOf(".");
				if(pos >= 0) {
					ext = ext.substring(pos, ext.length());
				}
				
				response.setContentType(reportTaskService.getContentType(task.getReportType()));	
		        response.setContentLength((int)file.length());		        
//		        response.setHeader("Content-Disposition","attachment; filename=\""+file.getName()+"\"");
		        response.setHeader("Content-Disposition","attachment; filename=\""+description+ext+"\"");
		 
		        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());	
		        return true;
			}
			else{
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00080", null, locale));
			}
		} catch (Exception e) {
			logger.error("Download file fail", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00083", null, locale));
		}
		
		return false;
	}
	
	public abstract ReportService getReportService();
	
	public void generateReport(Object criteria, Integer userId, RedirectAttributes redirectAttributes, Locale locale, Integer taskType){
		try{
			ReportTask task = getReportService().createReportTask(criteria, taskType, userId);
			reportBatchTask.generateReport(task.getReportTaskId());
			redirectAttributes.addFlashAttribute(SystemConstant.SUCCESS_MESSAGE, messageSource.getMessage("I00001", null, locale));
		}
		catch (Exception ex){
			logger.error("Generate report failed", ex);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00080", null, locale));
		}		
	}
	
	// Added 2020-09-29 Toby: (CR12) download password-protected report
	public boolean addPasswordForDownloadFile(Integer id, String password, RedirectAttributes redirectAttributes, Locale locale, HttpServletResponse response){
		try {
			File file = reportTaskService.getExportFileById(id);
			if (file.exists()){
				ReportTask task = reportTaskService.getReportTaskById(id);
				
				SystemFunction systemFunction = reportTaskService.getSystemFunctionByCode(task.getFunctionCode());
				String description = systemFunction.getDescription();
				if(description.contains("/") || description.contains("\"")) {
					description = description.replaceAll("/", "");
					description = description.replaceAll("\"", "");
				}
				String ext = file.getName();
				int pos = ext.lastIndexOf(".");
				if(pos >= 0) {
					ext = ext.substring(pos, ext.length());
				}
				
				File tempReportFile = poiFileService.makePasswordProtectedTempFile(file, password, ext);
		        
				response.setContentType(reportTaskService.getContentType(task.getReportType()));
		        response.setContentLength((int)tempReportFile.length());
		        response.setHeader("Content-Disposition","attachment; filename=\""+description+ext+"\"");
		        
		        FileUtils.copyFile(tempReportFile, response.getOutputStream());
		        tempReportFile.delete();
		        
	        	return true;
			} else {
				redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00080", null, locale));
			}
		} catch (Exception e) {
			logger.error("Download file fail", e);
			redirectAttributes.addFlashAttribute(SystemConstant.FAIL_MESSAGE, messageSource.getMessage("E00083", null, locale));
		}
		
		return false;
	}
	
}
